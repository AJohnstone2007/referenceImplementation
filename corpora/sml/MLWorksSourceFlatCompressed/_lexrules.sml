require "../utils/crash";
require "../utils/lists";
require "../basics/token";
require "../main/options";
require "../main/info";
require "regexp";
require "inbuffer";
require "lexrules";
require "../basis/__int";
require "^.basis.__string";
functor MLRules
(structure Crash : CRASH
structure Lists : LISTS
structure Token : TOKEN
structure RegExp : REGEXP
structure InBuffer : INBUFFER
structure Options : OPTIONS
structure Info : INFO) : LEXRULES =
struct
type options = Options.options
structure InBuffer = InBuffer
val new_definition = true
local
val || = RegExp.BAR
val & = RegExp.DOT
infix 0 ||
infix 1 &
val class = RegExp.CLASS
val negClass = RegExp.negClass
val star = RegExp.STAR
val plus = RegExp.plusRE
val node = RegExp.NODE
val digit = RegExp.digit
val hexDigit = RegExp.hexDigit
val letter = RegExp.letter
val alphanumeric = (class "_'") || digit || letter
val symbol = class "!%&$#+-/:<=>?@\\~`^|*"
val whitespace = plus(class " \n\t\012\013")
val alpha_id = letter & star(alphanumeric)
val symbolic_id = plus(symbol)
val id = alpha_id || symbolic_id
val long_id = id & star(node "." & id) & (node "." || RegExp.EPSILON)
val alphas = star(alphanumeric)
val tyvar = node "'" & alphas
val digits = plus(digit)
val hexdigits = node "0x" & plus(hexDigit)
val frac = (node "." & digits) || node "."
val intnum = digits || (node "~" & digits)
val intword = node "0w" & digits
val hexintnum = hexdigits || (node "~" & hexdigits)
val hexintword = node "0wx" & plus(hexDigit)
val exp = node "E" & intnum
val realnum = intnum & (frac || exp || (frac & exp))
val implode_char = MLWorks.String.implode_char
fun convert l = implode_char (rev l)
fun location_file Info.Location.UNKNOWN = Crash.impossible "location_file"
| location_file (Info.Location.FILE f) = f
| location_file (Info.Location.LINE (f, _)) = f
| location_file (Info.Location.POSITION (f, _, _)) = f
| location_file (Info.Location.EXTENT {name, ...}) = name
fun new_location (_, Info.Location.UNKNOWN) = Info.Location.UNKNOWN
| new_location (b, l) =
Info.Location.POSITION
(location_file l, InBuffer.getlinenum b, InBuffer.getlinepos b)
fun newline_location (_, Info.Location.UNKNOWN) = Info.Location.UNKNOWN
| newline_location (b, l) =
Info.Location.POSITION
(location_file l,
InBuffer.getlinenum b - 1,
InBuffer.getlastlinepos b)
fun extend_location (b, l) =
Info.Location.combine
(l, Info.Location.EXTENT {name = "", s_col = 0, s_line = 0,
e_line = InBuffer.getlinenum b,
e_col = InBuffer.getlinepos b})
in
structure RegExp = RegExp
structure Symbol = Token.Symbol
structure Info = Info
type Result = Token.Token
val eof = Token.EOF (Token.PLAIN_STATE)
val lparen = ord #"("
val rparen = ord #")"
val star = ord #"*"
val depth' = ref 0
fun read_comment (buffer, depth) =
let
val _ = depth' := depth
fun getchar b = InBuffer.getchar b
fun aux (b, 0) = Token.IGNORE
| aux (b, n) =
let
val c = getchar b
in
if c = lparen then do_lparen (b, n)
else if c = star then do_star (b, n)
else aux (b, n)
end
and do_lparen (b, n) =
let
val c = getchar b
in
if c = star then (depth' := n+1;
aux (b, n+1))
else if c = lparen then do_lparen (b, n)
else aux (b, n)
end
and do_star (b, n) =
let
val c = getchar b
in
if c = star then do_star (b, n)
else if c = rparen then (depth' := n-1;
aux (b, n-1))
else aux (b, n)
end
in
aux (buffer, depth)
handle InBuffer.Eof => Token.EOF (Token.IN_COMMENT (!depth'))
end
fun format_char 32 = true
| format_char 9 = true
| format_char 10 = true
| format_char 12 = true
| format_char 13 = true
| format_char c = false
fun read_whitespace (location, buffer, error_info) =
let
val c = InBuffer.getchar buffer
in
if format_char c then
read_whitespace (location, buffer, error_info)
else if c = ord #"\\" then
()
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (buffer, location),
"Missing \\ after formatting characters in string");
())
end
fun is_digit c = c >= ord #"0" andalso c <= ord #"9"
fun hexdigit c =
if is_digit c then
c-ord #"0"
else if c>=ord #"a" andalso c<=ord #"f" then
10+c-ord #"a"
else if c>=ord #"A" andalso
c<=ord #"Z" then
10+c-ord #"A"
else ~1
fun read_string (location, buffer, error_info, so_far) =
let
val getchar = InBuffer.getchar
fun read_digits (_, 0, b, location, c) = c
| read_digits (error_info, n, b, location, c) =
let
val c1 = getchar b
in
if is_digit c1 then
read_digits
(error_info, n - 1, b, location, 10 * c + c1 - ord #"0")
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Malformed numeric character specification in string");
0)
end
fun read_hexdigits (_, 0, b, location, acc) = acc
| read_hexdigits (error_info, n, b, location, (acc,chars)) =
let
val c1 = getchar b
val d1 = hexdigit c1
in
if d1 <> ~1 then
read_hexdigits
(error_info, n-1, b, location, (16*acc+d1,c1::chars))
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Malformed hex character specification in string");
(0,[]))
end
fun printable c = c >= 32 andalso c <= 126
val escapes = [(ord #"n",ord #"\n"),
(ord #"t",ord #"\t"),
(ord #"\"",ord #"\""),
(ord #"\\", ord #"\\"),
(ord #"a", 7),
(ord #"b", 8),
(ord #"v", 11),
(ord #"f", 12),
(ord #"r",13)]
fun is_escape_char c =
let
fun mem [] = false
| mem ((c',_)::rest) = c = c' orelse mem rest
in
mem escapes
end
fun get_escape_char c =
let
fun get [] = Crash.impossible "get_escape_char"
| get ((c',x)::rest) =
if c = c' then x else get rest
in
get escapes
end
fun aux (b, l) =
let
val quote = ord #"\""
val backslash = ord #"\\"
val newline = ord #"\n"
val c = getchar b
in
if c = quote then
Token.STRING (implode_char (rev (l @ so_far)))
else if c = backslash then
let
val c' = getchar b
in
if is_escape_char c' then aux (b, get_escape_char c' :: l)
else
if c' = ord #"^" then
let
val c'' = getchar b
in
if c'' >= 64 andalso c'' <= 95 then
aux (b, (c'' - 64) :: l)
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Illegal character after \\^ in string, " ^
"ASCII " ^ Int.toString c'');
aux (b, l))
end
else
if is_digit c' then
let
val c'' = read_digits (error_info, 2, b, location,
c' - ord #"0")
in
if c'' >= 0 andalso c'' < 256
then aux (b, c'' :: l)
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Out of range numeric character specification in string " ^
"\"\\" ^ Int.toString c'' ^ "\"");
aux (b,l))
end
else
if c'= ord #"u" then
let
val (c'',charlist) = read_hexdigits
(error_info, 4, b, location, (0,[]))
in
if c'' >= 0 andalso c'' < 256
then aux (b, c'' :: l)
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Out of range hexadecimal character specification\
                             \ in string " ^ "\"\\u" ^
(implode_char (rev charlist)) ^ "\"");
aux (b,l))
end
else
if format_char c' then
(read_whitespace (location, b, error_info);
aux (b, l))
handle InBuffer.Eof =>
Token.EOF (Token.IN_STRING (l @ so_far))
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Illegal character after \\ in string, " ^
"ASCII " ^ Int.toString c');
aux (b, l))
end
else
if c = newline then
(Info.error
error_info
(Info.RECOVERABLE, newline_location (b, location),
"Unexpected newline in string");
Token.STRING(implode_char (rev (l @ so_far))))
else
if printable c then
aux (b, c :: l)
else
(Info.error
error_info
(Info.RECOVERABLE, new_location (b, location),
"Illegal character in string, ASCII " ^
Int.toString c);
aux (b,l))
end
in
aux (buffer, [])
handle InBuffer.Eof =>
Info.error' error_info (Info.FATAL, extend_location (buffer, location),
"Unexpected end of string")
end
fun continue_string (location, buffer, error_info, so_far) =
(read_whitespace (location, buffer, error_info);
read_string (location, buffer, error_info, so_far))
handle InBuffer.Eof =>
Token.EOF (Token.IN_STRING so_far)
fun read_char(arg as (location, buffer, error_info, so_far)) =
let
val str = case read_string arg of
Token.STRING s => s
| _ => Crash.impossible"lexrules:read_string fails to return string"
in
if size str <> 1 then
Info.error' error_info (Info.RECOVERABLE, location,
"Character constant has wrong length string")
else
Token.CHAR str
end
fun fix chars = Symbol.find_symbol(implode_char chars)
fun split ([], chars, ids) = fix chars :: ids
| split (c :: rest, chars, ids) =
if c = ord #"." then
split(rest, [], fix chars :: ids)
else split(rest, c :: chars, ids)
fun getLast ([], _) =
Crash.impossible "lexrules.getLast"
| getLast ([id], strids) = (rev strids, id)
| getLast (strid :: rest, strids) =
getLast(rest, strid :: strids)
val symfuns =
map (fn (s,f) => (map ord (rev (explode s)),f))
[("abstype",fn _ => Token.RESERVED (Token.ABSTYPE)),
("and", fn _ => Token.RESERVED (Token.AND)),
("andalso",fn _ => Token.RESERVED (Token.ANDALSO)),
("as", fn _ => Token.RESERVED (Token.AS)),
("case", fn _ => Token.RESERVED (Token.CASE)),
("do", fn _ => Token.RESERVED (Token.DO)),
("datatype",fn _ => Token.RESERVED (Token.DATATYPE)),
("else", fn _ => Token.RESERVED (Token.ELSE)),
("end", fn _ => Token.RESERVED (Token.END)),
("exception",fn _ => Token.RESERVED (Token.EXCEPTION)),
("fn", fn _ => Token.RESERVED (Token.FN)),
("fun", fn _ => Token.RESERVED (Token.FUN)),
("handle", fn _ => Token.RESERVED (Token.HANDLE)),
("if", fn _ => Token.RESERVED (Token.IF)),
("in", fn _ => Token.RESERVED (Token.IN)),
("infix", fn _ => Token.RESERVED (Token.INFIX)),
("infixr", fn _ => Token.RESERVED (Token.INFIXR)),
("let", fn _ => Token.RESERVED (Token.LET)),
("local", fn _ => Token.RESERVED (Token.LOCAL)),
("nonfix", fn _ => Token.RESERVED (Token.NONFIX)),
("of", fn _ => Token.RESERVED (Token.OF)),
("op", fn _ => Token.RESERVED (Token.OP)),
("open", fn _ => Token.RESERVED (Token.OPEN)),
("orelse", fn _ => Token.RESERVED (Token.ORELSE)),
("raise", fn _ => Token.RESERVED (Token.RAISE)),
("rec", fn _ => Token.RESERVED (Token.REC)),
("require",fn options =>
case options of
Options.OPTIONS {
extension_options = Options.EXTENSIONOPTIONS {
require_keyword = true, ...
},
...} => Token.RESERVED (Token.REQUIRE)
| _ => Token.LONGID ([],
Symbol.find_symbol "require")),
("then", fn _ => Token.RESERVED (Token.THEN)),
("type", fn _ => Token.RESERVED (Token.TYPE)),
("val", fn _ => Token.RESERVED (Token.VAL)),
("with", fn _ => Token.RESERVED (Token.WITH)),
("withtype",fn _ => Token.RESERVED (Token.WITHTYPE)),
("while", fn _ => Token.RESERVED (Token.WHILE)),
("eqtype", fn _ => Token.RESERVED (Token.EQTYPE)),
("functor",fn _ => Token.RESERVED (Token.FUNCTOR)),
("include",fn _ => Token.RESERVED (Token.INCLUDE)),
("sharing",fn _ => Token.RESERVED (Token.SHARING)),
("sig", fn _ => Token.RESERVED (Token.SIG)),
("signature",fn _ => Token.RESERVED (Token.SIGNATURE)),
("struct", fn _ => Token.RESERVED (Token.STRUCT)),
("structure",fn _ => Token.RESERVED (Token.STRUCTURE)),
("where",
fn options =>
let
val Options.OPTIONS
{compat_options =
Options.COMPATOPTIONS {old_definition, ...},...} =
options
in
if old_definition
then Token.LONGID ([],Symbol.find_symbol "where")
else Token.RESERVED (Token.WHERE)
end),
("abstraction",
fn options =>
let
val Options.OPTIONS
{compat_options =
Options.COMPATOPTIONS {abstractions, ...},...} =
options
in
if abstractions
then Token.RESERVED (Token.ABSTRACTION)
else Token.LONGID ([],Symbol.find_symbol "abstraction")
end),
(":>",
fn options =>
let
val Options.OPTIONS
{compat_options =
Options.COMPATOPTIONS {old_definition, ...},...} =
options
in
if old_definition
then Token.LONGID ([],Symbol.find_symbol ":>")
else Token.RESERVED (Token.ABSCOLON)
end),
(":", fn _ => Token.RESERVED(Token.COLON)),
("|", fn _ => Token.RESERVED(Token.VBAR)),
("=", fn _ => Token.RESERVED(Token.EQUAL)),
("=>", fn _ => Token.RESERVED(Token.DARROW)),
("->", fn _ => Token.RESERVED(Token.ARROW)),
("#", fn _ => Token.RESERVED(Token.HASH))]
val max_length = 15
val keywords = MLWorks.Internal.Array.array (max_length,[])
: (int list * (Options.options -> Token.Token)) list MLWorks.Internal.Array.array
val _ =
map
(fn (data as (s,f)) =>
let
val len = length s
val entry = MLWorks.Internal.Array.sub (keywords,len)
in
MLWorks.Internal.Array.update (keywords,len,data::entry)
end)
symfuns
fun do_long_id s =
Token.LONGID(getLast (split(s, [], []), []))
fun eqchars ([],[]) = true
| eqchars ([],_::_) = false
| eqchars (_::_,[]) = false
| eqchars ((c:int)::rest,(c':int)::rest') = c = c' andalso eqchars (rest,rest')
fun do_name_token (s,options) =
let
fun lookup [] = do_long_id s
| lookup ((s',f)::rest) =
if eqchars(s,s') then f options else lookup rest
val len = length s
in
if len < max_length
then lookup (MLWorks.Internal.Array.sub (keywords,len))
else do_long_id s
end
fun make_tyvar (s,options) =
let
val Options.OPTIONS {compat_options = Options.COMPATOPTIONS {old_definition,...},
...} = options
val name = convert s
val sz = size name
val is_eq =
sz >= 2 andalso
String.sub(name, 1) = #"'"
val is_imp =
if old_definition then
if is_eq then
(sz >= 3) andalso
String.sub(name, 2) = #"_"
else
(sz >= 2) andalso
String.sub(name, 1) = #"_"
else
false
in
Token.TYVAR(Symbol.find_symbol name,is_eq,is_imp)
end
val rules =
[
(intnum, fn (_,_,s,_) => Token.INTEGER (convert s)),
(hexintnum, fn (_,_,s,_) => Token.INTEGER (convert s)),
(intword, fn (_,_,s,_) => Token.WORD (convert s)),
(hexintword, fn (_,_,s,_) => Token.WORD (convert s)),
(realnum,
fn (p,_,s,(error_info,options)) =>
let
fun check (#"." ::d::rest) = is_digit (ord d)
| check [#"." ] = false
| check (a::b) = check b
| check [] = true
val str = convert s
in
if check (explode str) then
()
else
Info.error error_info
(Info.RECOVERABLE,p,
"Malformed real literal: " ^ str);
Token.REAL (convert s)
end),
(tyvar, fn (_,_,s,(error_info,options)) => make_tyvar (s,options)),
(node "(", fn _ => Token.RESERVED(Token.LPAR)),
(node ")", fn _ => Token.RESERVED(Token.RPAR)),
(node "[", fn _ => Token.RESERVED(Token.BRA)),
(node "]", fn _ => Token.RESERVED(Token.KET)),
(node "{", fn _ => Token.RESERVED(Token.LBRACE)),
(node "}", fn _ => Token.RESERVED(Token.RBRACE)),
(node ",", fn _ => Token.RESERVED(Token.COMMA)),
(node ";", fn _ => Token.RESERVED(Token.SEMICOLON)),
(node "...", fn _ => Token.RESERVED(Token.ELLIPSIS)),
(node "_", fn _ => Token.RESERVED(Token.UNDERBAR)),
(node "#(",
fn (loc,_,_,(error_info, options)) =>
case options of
Options.OPTIONS
{extension_options =
Options.EXTENSIONOPTIONS
{type_dynamic = true, ...},...} => Token.RESERVED (Token.MAGICOPEN)
| _ =>
(Info.error
error_info
(Info.RECOVERABLE, loc,
"Dynamic type bracket not valid with these options: replacing with left parenthesis");
Token.RESERVED (Token.LPAR))),
(long_id, fn (_,_,s,(_,options)) => do_name_token (s,options)),
(node "(*", fn (loc,buf,_,(error_info, options)) =>
read_comment (buf,1)),
(node "#\"",
fn (loc, buf, _, (error_info, _)) =>
read_char(loc, buf, error_info, [])),
(node "\"", fn (loc,buf,_,(error_info,_)) =>
read_string (loc,buf,error_info,[])),
(whitespace, fn _ => Token.IGNORE)
]
end
end
;
