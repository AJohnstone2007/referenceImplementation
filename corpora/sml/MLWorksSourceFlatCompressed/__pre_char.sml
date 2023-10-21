require "__pre_basis";
require "__int";
require "__pre_string_cvt";
structure PreChar =
struct
val makestring : char -> string = fn c=>
let val alloc_s = PreBasis.alloc_string (1+1)
in MLWorks.Internal.Value.unsafe_string_update(alloc_s, 0, ord c);
alloc_s
end
type char = char
type string = string
val chr = chr
val ord = ord
val maxOrd = 255
val minChar : char = #"\000"
val maxChar : char = chr maxOrd
fun succ (c:char) =
if c < maxChar then
chr ((ord c) + 1)
else
raise Chr
fun pred (c:char) =
if c > minChar then
chr ((ord c)-1)
else
raise Chr
fun compare (c:char, d:char):order =
if c < d then
LESS
else if c > d then
GREATER
else EQUAL
fun contains "" = (fn _=>false)
| contains s =
let
val size = size s
in
fn c=>
let
val ord = ord c
fun aux i =
i < size andalso
(MLWorks.String.ordof(s, i) = ord orelse aux(i+1))
in
aux 0
end
end
fun notContains "" = (fn _=>true)
| notContains s =
let
val size = size s
in
fn c=>
let
val ord = ord c
fun aux i =
if i < size then
MLWorks.String.ordof(s, i) <> ord andalso aux (i+1)
else
true
in
aux 0
end
end
local
val ascii_limit = chr 127
in
fun isDigit c = PreBasis.isDigit c
fun isLower c = #"a" <= c andalso c <= #"z"
fun isUpper c = #"A" <= c andalso c <= #"Z"
fun isAscii c = minChar <= c andalso c <= ascii_limit
fun isAlpha c = isLower c orelse isUpper c
fun isAlphaNum c = isDigit c orelse isAlpha c
fun isSpace c = PreBasis.isSpace c
fun toLower c =
if isUpper c then
chr (ord c - ord #"A" + ord #"a")
else c
fun toUpper c =
if isLower c then
chr (ord c - ord #"a" + ord #"A")
else c
fun isHexDigit (c:char) = PreBasis.isHexDigit c
fun isOctDigit (c:char) = PreBasis.isOctDigit c
fun isCntrl (c:char) = #"\000" <= c andalso c <= #"\031"
fun isPrint1 (c:char) : bool = #"\032" <= c andalso c < ascii_limit
fun isPrint (c:char) = isPrint1 c orelse
(c >= #"\009" andalso c <= #"\013")
fun isGraph (c:char) = #"\032" < c andalso c < ascii_limit
fun isPunct (c:char) : bool = isGraph c andalso not (isAlphaNum c)
end
fun toString (c:char) : string =
if isCntrl c orelse c < #"\032" then
case c of
#"\a" => "\\a"
| #"\b" => "\\b"
| #"\t" => "\\t"
| #"\n" => "\\n"
| #"\v" => "\\v"
| #"\f" => "\\f"
| #"\r" => "\\r"
| _ => "\\^" ^ makestring (chr (ord c + ord #"@"))
else if isPrint c then
case c of
#"\\" => "\\\\"
| #"\"" => "\\\""
| _ => makestring c
else
"\\" ^ (PreStringCvt.padLeft #"0" 3 (Int.toString (ord c)))
fun scan getc cs =
case getc cs of
SOME (#"\\", cs) =>
(case getc cs of
SOME (#"n", cs) => SOME (#"\n", cs)
| SOME (#"t", cs) => SOME (#"\t", cs)
| SOME (#"\\", cs) => SOME (#"\\", cs)
| SOME (#"\"", cs) => SOME (#"\"", cs)
| SOME (#"a", cs) => SOME (#"\a", cs)
| SOME (#"b", cs) => SOME (#"\b", cs)
| SOME (#"v", cs) => SOME (#"\v", cs)
| SOME (#"f", cs) => SOME (#"\f", cs)
| SOME (#"r", cs) => SOME (#"\r", cs)
| SOME (#"^", cs) =>
(case getc cs of
SOME (c, cs) =>
if 64 <= ord c andalso ord c <= 95 then
SOME (chr (ord c - 64), cs)
else
NONE
| NONE => NONE)
| SOME (c, cs) =>
if isDigit c then
(case PreStringCvt.getNChar 2 getc cs of
SOME ([d, e], cs) =>
if isDigit d andalso isDigit e then
let
fun convert (c,d,e) = (100 * (ord c - ord #"0") + 10 * (ord d - ord #"0") + (ord e - ord #"0"))
val res = convert (c, d, e)
in
if 0 <= res andalso res <= maxOrd then
SOME (chr res, cs)
else
NONE
end
else
NONE
| _ => NONE)
else
let
fun dropFormat getc cs =
case getc cs of
SOME (#"\\", cs) => scan getc cs
| SOME (c, cs) =>
if isSpace c then
dropFormat getc cs
else
NONE
| NONE => NONE
in
dropFormat getc cs
end
| NONE => NONE)
| SOME (c, cs) =>
if isPrint1 c then
SOME (c, cs)
else
NONE
| NONE => NONE
fun fromString "" = NONE
| fromString s = PreStringCvt.scanString scan s
fun scanc getc cs =
case getc cs of
SOME (#"\\", cs) =>
(case getc cs of
SOME (#"n", cs) => SOME (#"\n", cs)
| SOME (#"t", cs) => SOME (#"\t", cs)
| SOME (#"\\", cs) => SOME (#"\\", cs)
| SOME (#"\"", cs) => SOME (#"\"", cs)
| SOME (#"a", cs) => SOME (#"\a", cs)
| SOME (#"b", cs) => SOME (#"\b", cs)
| SOME (#"v", cs) => SOME (#"\v", cs)
| SOME (#"f", cs) => SOME (#"\f", cs)
| SOME (#"r", cs) => SOME (#"\r", cs)
| SOME (#"?", cs) => SOME (#"?", cs)
| SOME (#"'", cs) => SOME (#"'", cs)
| SOME (#"x", cs) =>
(case PreStringCvt.splitl isHexDigit getc cs of
("", cs) => NONE
| (digits, cs) =>
(case PreStringCvt.scanString (Int.scan PreStringCvt.HEX) digits of
SOME hex =>
if 0 <= hex andalso hex <= maxOrd then
SOME (chr hex, cs)
else
NONE
| NONE => NONE))
| SOME (c, cs) =>
if isOctDigit c then
(case PreStringCvt.splitlN 2 isOctDigit getc cs of
(s, cs) =>
let
val char_sub = chr o MLWorks.String.ordof
fun convert s =
let
val sz = size s
val (c,d,e) =
if sz = 0 then (#"0", #"0", c)
else if sz = 1 then (#"0", c, char_sub(s,0))
else
(c, char_sub(s,0), char_sub(s,1))
in
(8*8*(ord c - ord #"0") + 8*(ord d - ord #"0") + (ord e - ord #"0"))
end
val res = convert s
in
if 0 <= res andalso res <= maxOrd then
SOME (chr res, cs)
else
NONE
end)
else
NONE
| NONE => NONE)
| SOME (c, cs) =>
if isCntrl c orelse c = #"\127" orelse
c = #"?" orelse
c = #"\"" orelse
(not (isAscii c)) then
NONE
else
SOME (c, cs)
| NONE => NONE
fun fromCString "" = NONE
| fromCString s = PreStringCvt.scanString scanc s
fun toCString (c:char) =
case c of
#"\n" => "\\n"
| #"\t" => "\\t"
| #"\v" => "\\v"
| #"\b" => "\\b"
| #"\r" => "\\r"
| #"\f" => "\\f"
| #"\a" => "\\a"
| #"\\" => "\\\\"
| #"\"" => "\\\""
| #"?" => "\\?"
| #"'" => "\\'"
| _ =>
if isPrint c then
makestring c
else
"\\" ^ PreStringCvt.padLeft #"0" 3 (Int.fmt PreStringCvt.OCT (ord c))
val op < : char * char -> bool = op <
val op <= : char * char -> bool= op <=
val op > : char * char -> bool= op >
val op >= : char * char -> bool= op >=
end
;
