local
val manner = MLWorks.Profile.make_manner
{time = true,
space = false,
calls = false,
copies = false,
depth = 2,
breakdown = []}
fun selector _ = manner
val options = MLWorks.Profile.Options {scan = 3, selector = selector}
exception Profile_Test
fun fromList [] = []
| fromList _ = raise Profile_Test
fun mkList 0 = []
| mkList n = n :: mkList (n-1);
fun give_result (MLWorks.Profile.Result r) = r
| give_result (MLWorks.Profile.Exception e) = raise e
val (result,profile) = MLWorks.Profile.profile options fromList (mkList 42)
val my_list = give_result result handle Profile_Test => [42]
in
val it = case my_list of
[42] => "Profiling handles exceptions OK."
| _ => "Profiling doesn't handle exceptions well."
end
;
