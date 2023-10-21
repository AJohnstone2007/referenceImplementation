signature VEC =
sig
type vector
exception vector_div and zero_vector and no_component
val MakeVec : real*real*real -> vector
val VecComp : vector*int -> real
val VecDot : vector*vector -> real
val VecCross : vector*vector -> vector
val VecLen : vector -> real
val VecNeg : vector -> vector
val VecAdd : vector*vector -> vector
val VecSub : vector*vector -> vector
val VecMult : real*vector -> vector
val VecDiv : real*vector -> vector
val VecUnit : vector -> vector
val VecComb : real*vector*real*vector -> vector
val VecAddS : real*vector*vector -> vector
end;
structure Vector : VEC =
struct
datatype vector = Vec of real * real * real
fun MakeVec V = Vec V
exception no_component
fun VecComp (Vec(x, y, z), c) =
case c of
0 => x
| 1 => y
| 2 => z
| _ => raise no_component
fun VecDot (Vec(x1, y1, z1), Vec(x2, y2, z2)) = (x1*x2+y1*y2+z1*z2)
fun VecCross (Vec(x1, y1, z1), Vec(x2, y2, z2)) =
Vec(y1*z2-z1*y2, z1*x2-x1*z2, x1*y2-y1*x2)
fun VecNeg (Vec(x, y, z)) = Vec(~x, ~y, ~z)
fun VecAdd (Vec(x1, y1, z1), Vec(x2, y2, z2)) = Vec(x1+x2, y1+y2, z1+z2)
fun VecSub (Vec(x1, y1, z1), Vec(x2, y2, z2)) = Vec(x1-x2, y1-y2, z1-z2)
fun VecMult (a, Vec(x, y, z)) = Vec(a*x, a*y, a*z)
exception vector_div
fun VecDiv (a, Vec(x, y, z)) = if Real.== (a,0.0) then raise vector_div
else Vec(x/a, y/a, z/a)
fun VecLen V = sqrt(VecDot(V, V))
exception zero_vector
fun VecUnit V = VecDiv(VecLen V, V) handle vector_div => raise zero_vector
fun VecComb (a, V1, b, V2) = VecAdd(VecMult(a, V1), VecMult(b, V2))
fun VecAddS (a, V1, V2) = VecAdd(VecMult(a, V1), V2)
end;
signature RAY =
sig
structure Vector : VEC
datatype ray = Ray of Vector.vector * Vector.vector
val RayPoint : ray*real -> Vector.vector
val RayReflect : ray*Vector.vector*Vector.vector -> ray
val RayRefract : ray*real*Vector.vector*Vector.vector -> bool*ray
end;
functor Ray (structure Vector : VEC) : RAY =
struct
structure Vector = Vector
open Vector
datatype ray = Ray of vector * vector
fun RayPoint (Ray(O, D), l) = VecAddS(l, D, O)
fun RayReflect (Ray(_, I), P, N) =
Ray(P, VecAddS(~2.0*VecDot(I, N), N, I))
fun RayRefract ((R as Ray(_, I)), n, P, N) =
let val eta = 1.0/n
val c1 = ~(VecDot(I, N))
val cs2 = 1.0-eta*eta*(1.0-c1*c1)
in
if (cs2<0.0) then (true, R)
else (false, Ray(P, VecComb(eta, I, eta*c1-sqrt(cs2), N)))
end
end;
