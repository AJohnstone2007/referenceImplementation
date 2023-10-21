require "prime";
require "$.basis.__large_int";
structure Prime : PRIME =
struct
fun even n = LargeInt.rem (n, 2) = 0
fun largePowerMod (m, n, modulo) =
let
fun square () =
let
val next = largePowerMod (m, LargeInt.quot (n, 2), modulo)
val sqr = next * next
in
LargeInt.rem (sqr, modulo)
end
in
if n = LargeInt.fromInt 0 then
LargeInt.fromInt 1
else
if even n then
square ()
else
LargeInt.rem (LargeInt.* (m, square ()), modulo)
end
fun powerMod (m, n, modulo) =
LargeInt.toInt (largePowerMod (LargeInt.fromInt m,
LargeInt.fromInt n,
LargeInt.fromInt modulo))
fun fermat (q, 0) = true
| fermat (q, k) =
if powerMod (k, q - 1, q) = 1
then fermat (q, k - 1)
else
false
fun testPrime (q, k) =
if q <= 1 then
false
else
if k >= q then
fermat (q, q - 1)
else
fermat (q, k)
end
;
