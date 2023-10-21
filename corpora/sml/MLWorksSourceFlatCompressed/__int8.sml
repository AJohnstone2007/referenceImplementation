require "integer";
require "__int";
structure Int8: INTEGER =
struct
type int = MLWorks.Internal.Types.int8
val precision = SOME 8
val minInt = SOME (~128: int)
val maxInt = SOME (127: int)
fun min(a, b) = if a < b then a else b: int
fun max(a, b) = if a < b then b else a: int
fun sign (x:int) = if x < 0 then ~1 else if x = 0 then 0 else 1: Int.int
fun sameSign(a, b) = sign a = sign b
val intmaxint = (128 : Int.int)
local val cast = MLWorks.Internal.Value.cast
in
fun toInt x = cast x
fun fromInt (x:Int.int) =
if x < intmaxint andalso x >= ~intmaxint
then cast x
else raise Overflow
end
val toString : int -> string = Int.toString o toInt
fun fmt radix n = Int.fmt radix (toInt n)
fun fromString s =
case Int.fromString s of
SOME n => SOME (fromInt n)
| _ => NONE
fun scan radix getc src =
case Int.scan radix getc src of
SOME (i,r) => SOME (fromInt i, r)
| _ => NONE
fun toLarge x = Int.toLarge (toInt x)
fun fromLarge x =
let
val intx = Int.fromLarge x
in
fromInt intx
end
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
end
;
