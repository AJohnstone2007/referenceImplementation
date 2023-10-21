require "__pre_int";
require "__string_cvt";
structure PreInt32 =
struct
type int = MLWorks.Internal.Types.int32
val cast = MLWorks.Internal.Value.cast
val precision = SOME 32
val minInt = SOME (~2147483648: int)
val maxInt = SOME (2147483647: int)
val toInt = PreInt.fromLarge
val fromInt = PreInt.toLarge
fun toLarge x = x
fun fromLarge x = x
fun tobase StringCvt.BIN = 2 : int
| tobase StringCvt.OCT = 8 : int
| tobase StringCvt.DEC = 10 : int
| tobase StringCvt.HEX = 16 : int
fun scan radix getc src =
let
val base = tobase radix
val ibase = toInt base
fun isSign c =
case c of
#"+" => true
| #"~" => true
| #"-" => true
| _ => false
fun isDigit a =
if ibase <= 10
then
a >= ord #"0" andalso
a < ord #"0" + ibase
else
(a >= ord #"0" andalso a < ord #"0" + 10) orelse
(a >= ord #"A" andalso a < ord #"A" + ibase - 10) orelse
(a >= ord #"a" andalso a < ord #"a" + ibase - 10)
exception Valof
fun valof n =
if n >= ord #"0" andalso n <= ord #"9"
then n - ord #"0"
else if n >= ord #"a" andalso n <= ord #"z"
then n - ord #"a" + 10
else if n >= ord #"A" andalso n <= ord #"Z"
then n - ord #"A" + 10
else raise Valof
fun convert_digits s =
let
fun convert ([],acc) = acc
| convert (c :: rest,acc) =
convert (rest,acc * base - fromInt (valof c))
in
convert (map ord (explode s),0)
end
fun convert_sign "~" = 1
| convert_sign "-" = 1
| convert_sign _ = ~1
val (sign,src) = StringCvt.splitl isSign getc (StringCvt.skipWS getc src)
in
if size sign > 1 then NONE
else
case StringCvt.splitl (isDigit o ord) getc src of
("",src) => NONE
| (digits,src) =>
SOME (convert_sign sign * convert_digits digits,src)
end
val fromString = StringCvt.scanString (scan StringCvt.DEC)
fun makeString (base : int, n : int) =
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
val acc' = make_digit (toInt digit) :: acc
in
if n' <> 0
then makedigits (n',acc')
else acc'
end
in
implode (if n < 0 then #"~" :: makedigits (n,[]) else makedigits (n,[]))
end
fun fmt radix n =
makeString (tobase radix,n)
fun toString n = fmt StringCvt.DEC n
val ~ : int -> int = ~
val op* : int * int -> int = op*
val op div : int * int -> int = op div
val op mod : int * int -> int = op mod
fun quot(a, b) =
let
val q = a div b
val r = a mod b
in
if r = 0 orelse (a > 0 andalso b > 0) orelse (a < 0 andalso b < 0) then
q
else
q + 1
end
fun rem(a, b) =
let
val r = a mod b
in
if r = 0 orelse (a > 0 andalso b > 0) orelse (a < 0 andalso b < 0) then
r
else
r - b
end
val op + : int * int -> int = op +
val op - : int * int -> int = op -
val op > : int * int -> bool = op >
val op >= : int * int -> bool = op >=
val op < : int * int -> bool = op <
val op <= : int * int -> bool = op <=
val abs : int -> int = abs
val compare =
fn (n,m) =>
if n < m then LESS
else if n > m then GREATER
else EQUAL
fun min(a, b) = if a < b then a else b: int
fun max(a, b) = if a < b then b else a: int
fun sign x = if x < 0 then ~1 else if x = 0 then 0 else 1: PreInt.int
fun sameSign(a, b) = sign a = sign b
end
structure PreLargeInt = PreInt32
;
