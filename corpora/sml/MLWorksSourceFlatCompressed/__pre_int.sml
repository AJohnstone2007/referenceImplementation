require "__pre_string_cvt";
require "__pre_basis";
structure PreInt =
struct
type int = int
local
val env = MLWorks.Internal.Runtime.environment
in
val toLarge : int -> MLWorks.Internal.Types.int32 =
env "int int_to_int32"
val fromLarge : MLWorks.Internal.Types.int32 ->int =
env "int int32_to_int"
end
val toInt = fn x :int => x
val fromInt = fn x : int => x
val precision = SOME 30
val minInt = SOME ~536870912
val maxInt = SOME 536870911
val ~ : int -> int = ~
val op* : int * int -> int = op*
val op div : int * int -> int = op div
val op mod : int * int -> int = op mod
fun quot (a,b) =
let
val q = a div b
val r = a mod b
in
if r = 0 orelse (a > 0 andalso b > 0) orelse (a < 0 andalso b < 0) then q
else q + 1
end
fun rem (a,b) =
let
val q = a div b
val r = a mod b
in
if r = 0 orelse (a > 0 andalso b > 0) orelse (a < 0 andalso b < 0) then r
else r - b
end
val op + : int * int -> int = op +
val op - : int * int -> int = op -
val op > : int * int -> bool = op >
val op >= : int * int -> bool = op >=
val op < : int * int -> bool = op <
val abs : int -> int = abs
val compare =
fn (n,m) =>
if n < m then LESS
else if n > m then GREATER
else EQUAL
fun min(a, b) = if a < b then a else b
fun max(a, b) = if a < b then b else a
fun sign x = if x < 0 then ~1 else if x = 0 then 0 else 1
fun sameSign(a, b) = sign a = sign b
fun makeString (base,n) =
let
fun make_digit digit =
if digit >= 10 then chr (ord #"A" + digit - 10)
else chr (ord #"0" + digit)
fun makedigits (n,acc) =
let
val digit =
if n >= 0
then n mod base
else
let
val res = n mod base
in
if res = 0 then 0 else base - res
end
val n' =
if n >= 0 orelse digit = 0 then
n div base
else 1 + n div base
val acc' = make_digit digit :: acc
in
if n' <> 0
then makedigits (n',acc')
else acc'
end
in
implode (if n < 0 then #"~" :: makedigits(n,[]) else makedigits(n,[]))
end
fun tobase PreStringCvt.BIN = 2
| tobase PreStringCvt.OCT = 8
| tobase PreStringCvt.DEC = 10
| tobase PreStringCvt.HEX = 16
fun fmt radix n =
makeString (tobase radix,n)
fun toString n = fmt PreStringCvt.DEC n
fun toDigit radix =
if tobase radix <= 10 then
fn c=> (ord c) - (ord #"0")
else
fn c=>
if #"0" <= c andalso c <= #"9" then
ord c - ord #"0"
else if #"A" <= c andalso c <= #"Z" then
ord c - ord #"A" + 10
else if #"a" <= c andalso c <= #"z" then
(ord c) - ord #"a" + 10
else raise Fail ("toDigit" ^ (str c))
fun scan radix getc src =
let
val toDigit : char -> int = toDigit radix
fun isSign #"+" = true
| isSign #"-" = true
| isSign #"~" = true
| isSign _ = false
fun convertSign "~" = 1
| convertSign "-" = 1
| convertSign _ = ~1
val base = tobase radix
val isDigit =
case radix of
PreStringCvt.OCT=> PreBasis.isOctDigit
| PreStringCvt.DEC => PreBasis.isDigit
| PreStringCvt.HEX => PreBasis.isHexDigit
| PreStringCvt.BIN => (fn c=>c = #"0" orelse c = #"1")
fun convertDigit s =
let val sz = size s
fun scan (i,acc) =
if i < sz then
let
val c = chr(MLWorks.String.ordof(s,i))
in
scan (i+1, (acc * base) - (toDigit c))
end
else
acc
in
scan (0, 0)
end
val (sign,src) =
case PreStringCvt.splitlN 1 isSign getc (PreStringCvt.skipWS getc src) of
(sign, src) => (sign,
(case radix of
PreStringCvt.HEX =>
(case getc src of
SOME (#"0", src') =>
(case getc src' of
SOME (#"x", src'') => src''
| SOME (#"X", src'') => src''
| SOME _ => src
| NONE => src)
| _ => src)
| _ => src))
in
case PreStringCvt.splitl isDigit getc src of
("", _) => NONE
| (digit, src) =>
SOME (convertSign sign * (convertDigit digit), src)
end
val fromString = PreStringCvt.scanString (scan PreStringCvt.DEC)
val op <= : int * int -> bool = op <=
end
;
