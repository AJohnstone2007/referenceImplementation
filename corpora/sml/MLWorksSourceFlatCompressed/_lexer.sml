require "lexer";
require "stream";
require "__lowlevel";
functor Lexer(structure Stream: STREAM): LEXER =
struct
exception StreamUnopen
exception BadToken of string
val lineNumber = ref 1
val prompt1 = ref ""
val prompt2 = ref ""
val anyInput = ref false
datatype token = ATOM of string | VAR of string | UNDERSCORE |
BAR | LPAREN | RPAREN | LT | LTE | GT | GTE |
EQ | CONSISTENT | NOT | IS | TRUE | FALSE | SEMICOLON |
STOP | ARROW | AMPERSAND | COMMA | BACKSLASH | QUERY |
FIXED | KNOWN | NIL | EOF | COMMAND of string |
NUMERIC of string * string |
PLUSOP | TIMESOP | MINUSOP | DIVOP
val returnedTokens = ref []: token list ref
fun openFile(x,y,p1,p2,aI) = (lineNumber:=1;
prompt1:=p1;
prompt2:=p2;
anyInput:=aI;
Stream.openStream(x,y))
fun flushStdIn() = Stream.flushStdIn()
local
fun nextSym() = Stream.nextSymbol(false)
val putSym = Stream.putSymbol
fun nextLine() = (lineNumber:=(!lineNumber)+1;
if !anyInput then print (!prompt2)
else print (!prompt1))
val isUpper = fn ch => (ch>="A" andalso ch<="Z")
val isLower = fn ch => (ch>="a" andalso ch<="z")
val isDigit = fn ch => (ch>="0" andalso ch<="9")
val isAlpha = fn ch => isUpper(ch) orelse isLower(ch) orelse isDigit(ch)
fun readNumber(ch1) =
let
val numInt = ref [ch1]
val numFrac = ref []: string list ref
val ch = ref(nextSym())
in
(while isDigit(!ch) do
(numInt:=(!ch)::(!numInt);
ch:=nextSym());
if (!ch=".") then
(ch:=nextSym()
handle Stream.Eof => ch:=".";
if isDigit(!ch) then while isDigit(!ch) do
(numFrac:=(!ch)::(!numFrac);
ch:=nextSym())
else (putSym(!ch);ch:="."))
else ();
putSym(!ch);
raise Stream.Eof
)
handle Stream.Eof => NUMERIC (concat(rev(!numInt)),
concat(rev(!numFrac)))
end
fun readAlpha(CATEGORY, ch1) =
let
val name = ref [ch1]
val ch = ref(nextSym())
in
(while isAlpha(!ch) do
(name:=(!ch)::(!name);
ch:=nextSym());
putSym(!ch);
raise Stream.Eof
)
handle Stream.Eof => case concat(rev(!name)) of
"nil" => NIL
| "known" => KNOWN
| "fixed" => FIXED
| "con" => CONSISTENT
| "not" => NOT
| "is" => IS
| "true" => TRUE
| "false" => FALSE
| y => CATEGORY y
end
fun readCommand(ch1) =
if ch1="(" then
let val c = ref (nextSym())
val name = ref [] : string list ref
in
(while ((!c)<>")") do
(if (!c)="\n" then nextLine() else ();
name:=(!c)::(!name);
c:=nextSym());
COMMAND (concat(rev(!name))))
end
else readAlpha(COMMAND,ch1)
fun skipComment() =
let
val ch1 = ref(nextSym())
val ch2 = ref(nextSym())
in
while (((!ch1),(!ch2))<>("*","/")) do
(if (!ch1)="\n" then nextLine() else ();
ch1:=(!ch2);
ch2:=nextSym())
end
fun readSymbol(ch1) =
(case (ch1,nextSym()) of
("-",">") => ARROW
| ("-",X) => (putSym(X); MINUSOP)
| ("/","*") => ((skipComment(); readToken(nextSym()))
handle Stream.Eof => EOF)
| ("/",X) => (putSym(X); DIVOP)
| (">","=") => GTE
| (">",X) => (putSym(X); GT)
| ("<","=") => LTE
| ("<",X) => (putSym(X); LT)
| ( X, Y) => (putSym(Y); raise BadToken(X))
)
handle Stream.Eof => raise BadToken(ch1)
and readString() =
let
val ch = ref(nextSym())
val s = ref([]): string list ref
in
(while (!ch<>"\"") do (s:=(!ch)::(!s); ch:=(nextSym()));
ATOM (concat(rev(!s))))
end
and readToken(ch) =
(if (ch<>"\n") andalso (ch<>" ") andalso (ch<>"\t")
then anyInput:=true
else ();
if isDigit(ch) then (readNumber(ch)) else
if isLower(ch) then (readAlpha(ATOM, ch)) else
if isUpper(ch) then (readAlpha(VAR, ch)) else
case ch of
"&" => (AMPERSAND)
| "=" => (EQ)
| "," => (COMMA)
| ";" => (SEMICOLON)
| "(" => (LPAREN)
| ")" => (RPAREN)
| "." => (STOP)
| "|" => (BAR)
| "_" => (UNDERSCORE)
| "+" => (PLUSOP)
| "*" => (TIMESOP)
| "?" => (QUERY)
| "%" => (readCommand(nextSym()))
| "\"" => (readString())
| "\\" => (BACKSLASH)
| "\n" => (nextLine(); readToken(nextSym()))
| "\t" => readToken(nextSym())
| " " => readToken(nextSym())
| x => (readSymbol(x)))
in
fun putToken(t) = ((putSym(nextSym())
handle Stream.Eof => ()
| Stream.StreamUnopen =>
raise StreamUnopen);
returnedTokens:=t::(!returnedTokens))
fun nextToken() =
(case !returnedTokens of
nil => readToken(nextSym())
| a::b => (returnedTokens:=b; a)
)
handle Stream.Eof => EOF
| Stream.StreamUnopen => raise StreamUnopen
| BadToken(X) => if X="\000" then EOF
else (print ("line "^
(makeString(!lineNumber))^
": Unknown Symbol \""^X^"\"\n");
nextToken())
end
end
;
