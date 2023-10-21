local
val manner = MLWorks.Profile.make_manner
{time = true,
space = true,
calls = false,
copies = false,
depth = 0,
breakdown = []}
val null_manner = MLWorks.Profile.make_manner
{time = false,
space = false,
calls = false,
copies = false,
depth = 0,
breakdown = []}
fun is_my_test s =
MLWorks.String.ordof (s,0) = ord #"m"
andalso MLWorks.String.ordof (s,1) = ord #"y"
andalso MLWorks.String.ordof (s,2) = ord #"_"
andalso MLWorks.String.ordof (s,3) = ord #"t"
andalso MLWorks.String.ordof (s,4) = ord #"e"
andalso MLWorks.String.ordof (s,5) = ord #"s"
andalso MLWorks.String.ordof (s,6) = ord #"t"
fun selector s = if is_my_test s then manner else null_manner
val options = MLWorks.Profile.Options {scan = 10, selector = selector}
fun get_top_scans (MLWorks.Profile.Function_Time_Profile {top,...}) = top
fun real_data (MLWorks.Profile.Large_Size {bytes,megabytes}) =
(real megabytes)*1048576.0+(real bytes)
fun get_allocated (MLWorks.Profile.Function_Space_Profile{allocated,...}) =
real_data allocated
fun matching pred (total as (totalscans,totaldata),
MLWorks.Profile.Function_Profile
{id, time,space,...}) =
if pred id then (totalscans + (get_top_scans time),
totaldata + (get_allocated space))
else total
fun fold f z [] = z
| fold f z (x::xs) = fold f (f(z,x)) xs
local
fun length' (a,[]) = a | length' (a,x::xs) = length'(a+1,xs)
in
fun length l = length'(0,l)
end
fun my_test_rev_app (x::xs, ys) = my_test_rev_app(xs,x::ys)
| my_test_rev_app ([] , ys) = ys
fun my_test_fibl 0 = [0]
| my_test_fibl 1 = [0]
| my_test_fibl n = my_test_rev_app(my_test_fibl (n-1),my_test_fibl (n-2))
val (result,profile) = MLWorks.Profile.profile options my_test_fibl 27
val MLWorks.Profile.Profile {time = MLWorks.Profile.Time time_header,
space = MLWorks.Profile.Space space_header,
functions,...} = profile
val {scans = total_scans,...} = time_header
val {total_profiled = MLWorks.Profile.Function_Space_Profile {allocated,...},
...} = space_header
val total_data = real_data allocated
val (my_test_scans,my_test_data) = fold (matching is_my_test) (0,0.0) functions
in
val it =
if my_test_scans < 0 then
"Nonsense profiling result for my_fibl 27 : my_test_scans < 0"
else if total_scans < 0 then
"Nonsense profiling result for my_fibl 27 : total_scans < 0"
else if my_test_scans > total_scans then
"Nonsense profiling result for my_fibl 27 : my_test_scans > total_scans"
else if total_scans < 50 then
"Unexpected profiling result for my_fibl 27 : fewer than 50 scans"
else if my_test_scans < floor ((real total_scans) * 0.8) then
"Unexpected result for my_fibl 27 : less than 80% time spent in 'my_test_...'"
else if my_test_data < 0.0 then
"Nonsense profiling result for my_fibl 27 : my_test_data < 0.0"
else if total_data < 0.0 then
"Nonsense profiling result for my_fibl 27 : total_data < 0.0"
else if my_test_data > total_data then
"Nonsense profiling result for my_fibl 27 : my_test_data > total_data"
else if length functions > 10 then
"Unexpected profiling result for my_fibl 27 : more than 10 'my_test_...' fns"
else if total_data < 25.0*1048576.0 then
"Unexpected profiling result for my_fibl 27 : less than 25Mb allocated"
else if my_test_data < (total_data * 0.99) then
"Unexpected result for my_fibl 27 : less than 99% data made by 'my_test_...'"
else "Profiling my_fibl 27 has a reasonable result"
end
;
