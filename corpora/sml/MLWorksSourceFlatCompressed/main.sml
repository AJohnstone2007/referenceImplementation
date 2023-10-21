fun ignore x = ()
signature IN =
sig
structure Csg : CSG
datatype camera = Camera of {from : Csg.Ray.Vector.vector,
lookat : Csg.Ray.Vector.vector,
up : Csg.Ray.Vector.vector,
angles : real * real,
hither : real,
resolution : int * int,
sky : Csg.Ray.Vector.vector,
zenith : real * real * real,
horizon : real * real * real,
ambience : real * real * real,
maxdepth : int,
minweight : real}
datatype light = Light of {pos : Csg.Ray.Vector.vector,
col : real * real * real,
noatten : bool,
atten : real * real * real}
val readata : string -> camera * (light list) * Csg.scene
end;
signature OUT =
sig
val start : string*(int*int) ->
SML90.outstream*(int*int*(real*real*real*real)->unit)
val finish : SML90.outstream -> unit
end;
signature RAYFN =
sig
structure Csg : CSG
structure In : IN
val ShadeSky : In.camera*Csg.Ray.ray -> real*real*real*real
val Shade : In.camera*(In.light list)*Csg.scene*int*real*
Csg.Ray.Vector.vector*Csg.Ray.Vector.vector*
Csg.Ray.Vector.vector*Csg.Ray.ray*real*Csg.surface*
(Csg.isect list) -> real*real*real*real
val Intersect : Csg.Ray.ray*Csg.scene -> Csg.isect list
val Trace : In.camera*(In.light list)*Csg.scene*Csg.Ray.ray*int*real
-> real*real*real*real
val Screen : In.camera*(In.light list)*Csg.scene*
(int*int*(real*real*real*real) -> unit) -> unit
val go : string*string -> unit
end;
functor RayFunctions (structure Csg : CSG
and AllPrimFns : ALLPRIMFNS
and In : IN
and Out : OUT
sharing Csg = AllPrimFns.Csg = In.Csg) : RAYFN =
struct
structure Csg = Csg
open Csg
open Ray
open Vector
structure In = In
open In
local fun Merge_Sort ([], (_, ys)) = ys
| Merge_Sort (xs, (_, [])) = xs
| Merge_Sort (((Isect x)::xs), (t, ((Isect y)::ys))) =
if (#l x)<=(#l y) then
(Isect x)::(Merge_Sort (xs, (t, ((Isect y)::ys))))
else
(Isect y)::(Merge_Sort ((Isect x)::xs, (t, ys)))
fun CSGIntersect ray (Primitive(Prim s), surf) =
(#intersect (element (AllPrimFns.flist, #primtype s)))
(Prim s, surf, ray)
| CSGIntersect ray (Composite(left, csgop, right), surf) =
let val (il, nl) = CSGIntersect ray (left, surf)
in
if null(nl) andalso csgop<>Union then (false, [])
else let val (ir, nr) = CSGIntersect ray (right, surf)
val i = CSGLookup csgop (il, ir)
in
(i, CSGMerge csgop (il, nl, ir, nr, i, []))
end
end
in fun Intersect (ray, Scene scene) =
foldleft Merge_Sort [] (map (CSGIntersect ray) scene)
end
fun ShadeSky (Camera cam, Ray (_, D)) =
let val m = abs(VecDot(#sky cam, D))
val n = 1.0-m
val (z0, z1, z2) = #zenith cam
val (h0, h1, h2) = #horizon cam
in
(m*z0+n*h0, m*z1+n*h1, m*z2+n*h2, 0.0)
end
local fun Shadow_Contrib _ _ (r, g, b) [] = (r, g, b)
| Shadow_Contrib ll il (r, g, b) ((Isect h)::t) =
let val (_, Surface surf) = #prim h
val ol = #l h
in
if (#noshadow surf) orelse ol>ll then (r, g, b)
else if Real.== (#tns surf,0.0) then (0.0, 0.0, 0.0)
else if (#enter h) then Shadow_Contrib ll ol (r, g, b) t
else
let val f = pow (#ttm surf) (ol-il)
in
Shadow_Contrib ll ol (r*f, g*f, b*f) t
end
end
fun Light_Diffuse_Reflection (L, N, Surface surf) =
let val Kd = #dif surf
val (c0, c1, c2) = #dcol surf
in
if Real.== (Kd,0.0) then (0.0, 0.0, 0.0)
else
let val NdL = Kd*VecDot(N, L)
in
(c0*NdL, c1*NdL, c2*NdL)
end
end
fun Light_Specular_Reflection (L, N, V, Surface surf) =
let val Ks = #spec surf
in
if Real.== (Ks,0.0) then 0.0
else
let val NdH = VecDot(N, VecUnit(VecAdd(L, V)))
in
if NdH>epsilon then Ks*(pow NdH (#sshn surf))
else 0.0
end
end
fun Light_Specular_Transmission (L, N, V, n, Surface surf) =
let val Kt = #tns surf
in
if Real.== (Kt,0.0) orelse Real.== (n,1.0) then 0.0
else
let val H = VecUnit(VecDiv(n-1.0,VecSub(V, VecMult(n, L))))
val NdH = VecDot(N, H)
in
if NdH>epsilon then Kt*(pow NdH (#tshn surf))
else 0.0
end
end
in fun Lights_Contrib (scene, V, P, N, Surface surf, n)
((i0, i1, i2, rl), Light h) =
let val Lt = VecSub((#pos h), P)
val ll = VecLen Lt
val L = VecDiv(ll, Lt)
val (df0, df1, df2) =
Light_Diffuse_Reflection (L, N, Surface surf)
val spec =
Light_Specular_Reflection (L, N, V, Surface surf) +
Light_Specular_Transmission (L, N, V, n, Surface surf)
val att =
if #noatten h then 1.0
else let val (a0, a1, a2) = #atten h
in min'r (1.0/(a0+a1*ll+a2*ll*ll)) 1.0 end
val (si0, si1, si2) =
Shadow_Contrib ll 0.0 (#col h)
(Intersect (Ray(P, L), scene))
val (cl0, cl1, cl2) =
if (#metal surf) then (#dcol surf) else (#col h)
in
((i0+si0*att*cl0*(df0+spec)),
(i1+si1*att*cl1*(df1+spec)),
(i2+si2*att*cl2*(df2+spec)), rl)
end
end
fun Get_Medium (Isect h) =
let val (_, Surface surf1) = #prim h
val ior1 = #ior surf1
val Surface surf2 = air
val ior2 = #ior surf2
in
if (#enter h) then (Surface surf2, Surface surf1, ior1/ior2)
else if ior1>0.0 then (Surface surf1, Surface surf2, ior2/ior1)
else (Surface surf1, Surface surf2, 0.0)
end
fun Get_Surface_Details (ray as Ray(_, D), Isect h) =
let val (Prim prim, Surface surf) = #prim h
val l = #l h
val V = VecNeg D
val P = RayPoint (ray, l)
val N = (#normal (element (AllPrimFns.flist, #primtype prim)))
(Prim prim, P)
in
if VecDot(D, N)>0.0 then (V, P, VecNeg N, l, surf)
else (V, P, N, l, surf)
end
fun Other_Specular_Reflection (Camera cam, lights, scene, level,
weight, ray, V, P, N, Surface surf) =
let val sweight = weight*(#spec surf)
in
if sweight > (#minweight cam) then
Trace (Camera cam, lights, scene,
RayReflect (ray, P, N), level+1, sweight)
else (0.0, 0.0, 0.0, 0.0)
end
and Other_Specular_Transmission (Camera cam, lights, scene, level,
weight, ray, V, P, N, n, Surface surf) =
let val tweight = weight*(#tns surf)
in
if tweight > (#minweight cam) then
let val (tir, tray) = RayRefract (ray, n, P, N)
in
if (not tir) then Trace (Camera cam, lights, scene, tray,
level+1, tweight)
else (0.0, 0.0, 0.0, 0.0)
end
else (0.0, 0.0, 0.0, 0.0)
end
and Shade (Camera cam, lights, scene, level, weight, V, P, N, ray, rl,
Surface surf, (Isect h::_)) =
let val (c0, c1, c2) = #dcol surf
val (ia0, ia1, ia2) = #ambience cam
val (d0, d1, d2) = (ia0*(#amb surf)*c0,
ia1*(#amb surf)*c1,
ia2*(#amb surf)*c2)
in
if (#emitter surf) then (c0, c1, c2, rl)
else if (level+1 >= (#maxdepth cam)) then (d0, d1, d2, rl)
else let val (Surface m0, Surface m1, n) = Get_Medium (Isect h)
val (ir0, ir1, ir2, dr) =
Other_Specular_Reflection (Camera cam, lights,
scene, level, weight, ray, V, P, N, Surface surf)
val (it0, it1, it2, dt) =
Other_Specular_Transmission (Camera cam, lights,
scene, level, weight, ray, V, P, N, n, Surface surf)
val t0 = (#spec surf)*(pow (#stm m0) dr)
val t1 = (#tns surf)*(pow (#ttm m1) dt)
in foldleft (Lights_Contrib (scene, V, P, N, Surface surf, n))
(d0+ir0*t0+it0*t1, d1+ir1*t0+it1*t1, d2+ir2*t0+it2*t1, rl)
(lights)
end
end
| Shade _ = raise Match
and Trace (cam, lights, scene, ray, level, weight) =
let val hit= Intersect (ray, scene)
in
if null(hit) then ShadeSky (cam, ray)
else
let val (V, P, N, rl, surf) = Get_Surface_Details (ray, (hd hit))
in
Shade(cam, lights, scene, level, weight, V, P, N, ray, rl,
Surface surf, hit)
end
end
local fun LoopX (x, xmax, xlen, xstep, y, ylen, E, V, L, U, Camera cam,
lights, scene, writefn) =
if x<xmax then
let val xnew = xlen+xstep
in
(ignore(writefn(x, y, Trace(Camera cam, lights, scene,
Ray(E, VecUnit(VecAdd(VecComb(xlen, L, ylen, U), V))),
0, 1.0)));
LoopX(x+1, xmax, xnew, xstep, y, ylen, E, V, L, U,
Camera cam, lights, scene, writefn))
end
else ()
fun LoopY (y, ymax, ylen, ystep, xmax, xstart, xstep, E, V, L, U,
Camera cam, lights, scene, writefn) =
if y<ymax then
let val ynew = ylen+ystep
in
(LoopX(0, xmax, xstart, xstep, y, ylen, E, V, L, U,
Camera cam, lights, scene, writefn);
LoopY(y+1, ymax, ynew, ystep, xmax, xstart, xstep, E, V, L,
U, Camera cam, lights, scene, writefn))
end
else ()
in fun Screen (Camera cam, lights, scene, writefn) =
let val (xangle, yangle) = #angles cam
val (xres, yres) = #resolution cam
val rxres = real xres
val ryres = real yres
val xratio = rxres/ryres
val yratio = ryres/rxres
val xstep = (2.0*xratio)/(rxres-1.0)
val ystep = ~((2.0*yratio)/(ryres-1.0))
val xwidth = tan xangle
val ywidth = tan yangle
val E = #from cam
val V = VecUnit(VecSub((#lookat cam), E))
val L = VecMult(~xwidth, VecUnit(VecCross((#up cam), V)))
val U = VecMult(~ywidth, VecUnit(VecCross(V, L)))
in
LoopY(0, yres, yratio, ystep, xres, ~xratio, xstep, E, V, L, U,
Camera cam, lights, scene, writefn)
end
end
fun go (infilename, outfilename) =
let val (Camera cam, lights, scene) = readata infilename
val (outfile, writefn) = Out.start(outfilename, #resolution cam)
in
(Screen(Camera cam, lights, scene, writefn);
Out.finish outfile)
end
end;
