local
val pi = 3.141592653589793;
fun integrate (f,start,finish,num_steps) =
let
val r_num_steps = real num_steps
val range = finish - start
val step = range / r_num_steps
val values = MLWorks.Internal.Array.array (num_steps+1,0.0)
fun fill_values n =
if n > num_steps then ()
else
let
val x = (real n / r_num_steps) * range
in
(MLWorks.Internal.Array.update (values,n,f x);
fill_values (n+1))
end
val _ = fill_values 0
fun do_sum (n,acc) =
if n = num_steps then acc
else
do_sum (n+1,acc + (step * ((MLWorks.Internal.Array.sub (values,n) + MLWorks.Internal.Array.sub (values,n+1)) / 2.0)))
in
do_sum (0,0.0)
end
val limit = 1000000
val res1 = integrate (fn x => x, 0.0,1.0,limit)
val ans1 = 0.5
val res2 = integrate (fn x => x * x, 0.0,10.0,limit)
val ans2 = 1000.0/3.0
val res3 = integrate (Math.cos, 0.0,pi/2.0,limit)
val ans3 = 1.0
val res4 = integrate (Math.sin, 0.0,pi/2.0,limit)
val ans4 = 1.0
val results = [(res1,ans1),(res2,ans2),(res3,ans3),(res4,ans4)]
val tolerance = 1E~10
fun close_to(a,b) = abs((a-b)/a) < tolerance
fun all [] = true
| all (x::xs) = x andalso (all xs)
val maps = map close_to results
val string = if (all maps) then "Pass\n" else "Fail\n"
in
val _ = print string
end
;
