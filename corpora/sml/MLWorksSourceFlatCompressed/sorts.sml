use "utils/benchmark";
structure Sort =
struct
local
fun bpart(test,a,n,m,p) =
if n=m then n
else
let val next = Array.sub(a,m)
in
if test(next,p)
then
(Array.update(a,n,next);
tpart(test,a,n+1,m,p))
else
bpart(test,a,n,m-1,p)
end
and tpart(test,a,n,m,p) =
if n=m then n
else
let val next = Array.sub(a,n)
in
if test(next,p)
then
tpart(test,a,n+1,m,p)
else
(Array.update(a,m,next);
bpart(test,a,n,m-1,p))
end
fun qaux(test,a,n,m) =
if n = m
then ()
else
let
val p = Array.sub(a,n)
val s = bpart(test,a,n,m-1,p)
in
Array.update(a,s,p);
qaux(test,a,n,s);
qaux(test,a,s+1,m)
end
fun qsort test a =
qaux(test,a,0,Array.length a)
fun array_to_list a =
let fun aux 0 = []
| aux n = Array.sub (a,n-1) :: aux (n-1)
in
rev (aux (Array.length a))
end
local
val a = 16807.0
val m = 2147483647.0
in
fun nextrand seed =
let
val t = a*seed
in
t - m * real(floor(t/m))
end
end
fun myfloor x =
floor x handle OverFlow => myfloor(x/2.0)
fun randlist(n, seed, tail) =
if n = 0 then tail
else
randlist(n-1, nextrand seed, (myfloor seed) :: tail)
val sort_list = randlist(100000,1.0,[])
in
fun qsort_test () =
let
val sort_array = Array.fromList sort_list
in
qsort (fn(a:int,b) => a < b) sort_array
end
end
end;
test "array quicksort" 1 Sort.qsort_test ()
;
