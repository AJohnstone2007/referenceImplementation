require "quadrature";
require "$.basis.__real";
require "$.basis.__math";
structure Quadrature : QUADRATURE =
struct
exception Eval of real
datatype intervals = NODE of intervals * intervals
| INTERVAL of real * real * real * real * real * real
fun evaluate (f, x) =
let
val result = f x
in
if Real.isFinite result then
result
else
raise Eval x
end
fun subdivide (NODE (leftIntervals, rightIntervals), f) =
NODE (subdivide (leftIntervals, f),
subdivide (rightIntervals, f))
| subdivide (INTERVAL (left, mid, right, fLeft, fMid, fRight), f) =
let
val midLeft = (left + mid) / 2.0
val midRight = (mid + right) / 2.0
val fMidLeft = evaluate (f, midLeft)
val fMidRight = evaluate (f, midRight)
in
NODE (INTERVAL (left, midLeft, mid, fLeft, fMidLeft, fMid),
INTERVAL (mid, midRight, right, fMid, fMidRight, fRight))
end
fun convergence (current, error, accuracy) =
abs error <= accuracy * (1.0 + Real.abs current)
fun estimateError (s1, s2) =
(s1 - s2) / 15.0
fun simpsons (NODE (leftTree, rightTree)) =
simpsons leftTree + simpsons rightTree
| simpsons (INTERVAL (left, mid, right, fLeft, fMid, fRight)) =
(right - left) / 6.0 * (fLeft + 4.0 * fMid + fRight)
fun calcIntegral (allIntervals, lastSum, f, accuracy) =
let
val subIntervals = subdivide (allIntervals, f)
val newSum = simpsons subIntervals
val error = estimateError (lastSum, newSum)
in
if convergence (newSum, error, accuracy) then
newSum
else
calcIntegral (subIntervals, newSum, f, accuracy)
end
fun integrate (f, a, b, accuracy) =
let
val left = Real.checkFloat a
val right = Real.checkFloat b
val mid = (left + right) / 2.0
val acc =
case accuracy of
NONE => 0.0
| SOME eta => eta
val startInterval =
INTERVAL (left, mid, right,
evaluate (f, left),
evaluate (f, mid),
evaluate (f, right))
in
calcIntegral (startInterval, simpsons (startInterval), f, acc)
end
val macheps = Real.nextAfter (1.0, Real.posInf) - 1.0
val h = Math.sqrt macheps
fun differentiate f x = (f (x + h) - f (x)) / h
end
;
