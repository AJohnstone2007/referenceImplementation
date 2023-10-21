require "math";
structure Math : MATH =
struct
type real = real
val realEq : real * real -> bool = MLWorks.Internal.Value.real_equal
infix realEq
val env = MLWorks.Internal.Runtime.environment
val atan : real -> real = MLWorks.Internal.Value.arctan
val sqrt : real -> real = MLWorks.Internal.Value.sqrt
val sin : real -> real = MLWorks.Internal.Value.sin
val cos : real -> real = MLWorks.Internal.Value.cos
val exp : real -> real = MLWorks.Internal.Value.exp
val pi : real = 4.0 * atan 1.0
val e : real = exp 1.0
val tan : real -> real =
fn x => (sin x / cos x)
val atan2 : real * real -> real =
fn (x, y) =>
if x realEq 0.0 then
if y realEq 0.0 then
0.0
else
if y < 0.0 then
~ pi / 2.0
else
pi / 2.0
else
let
val at = atan(y/x)
in
if x > 0.0 then
at
else
if y < 0.0 then
at - pi
else
at + pi
end
val asin : real -> real =
fn y =>
let
val x = sqrt(1.0 - y*y)
in
atan2(x, y)
end
val acos : real -> real =
fn x =>
let
val y = sqrt(1.0 - x*x)
in
atan2(x, y)
end
val cpow : real * real -> real = env "real pow"
local
fun odd_integer y =
let val y_over_2 = y/2.0
in
y realEq real (floor y) andalso
not (y_over_2 realEq real (floor y_over_2))
end
in
fun pow (x,y) =
if y realEq 0.0 then
1.0
else if not (y realEq y) then
y
else if x realEq 0.0 then
if y > 0.0 then
if odd_integer y then
x
else
0.0
else
if odd_integer y then
1.0/x
else
1.0/0.0
else
cpow (x,y)
end
val ln : real -> real = env "real ln"
val log10 : real -> real = fn x => ln x / ln 10.0
fun sinh x = (exp x - exp (~x)) / 2.0
fun cosh x = (exp x + exp (~x)) / 2.0
fun tanh x = sinh x / cosh x
end
;
