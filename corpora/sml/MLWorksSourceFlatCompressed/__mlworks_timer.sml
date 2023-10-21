require "mlworks_timer";
require "^.basis.__int";
require "^.basis.__timer";
require "^.system.__time";
structure Timer_ : INTERNAL_TIMER =
struct
structure M = MLWorks.Internal.Runtime.Memory
structure V = MLWorks.Internal.Value
fun xtime (s, flag, f) =
if flag then
let
val cpu_timer = Timer.startCPUTimer()
val real_timer = Timer.startRealTimer()
val (initcollects,initbytes) = M.collections()
fun print_time () =
let
val {usr, sys} = Timer.checkCPUTimer cpu_timer
val gc = Timer.checkGCTime cpu_timer
val real_elapsed = Timer.checkRealTimer real_timer
val (finalcollects, finalbytes) = M.collections()
val bytes = finalbytes-initbytes
val coll = finalcollects - initcollects
val (showcoll,showbytes) =
if bytes > 0 then (coll,bytes) else
(coll-1,bytes+1048576)
in
print(concat ["Time for ", s, " : ",
Time.toString real_elapsed,
" (user: ",
Time.toString usr,
"(gc: ",
Time.toString gc,
"), system: ",
Time.toString sys,
")",
" allocated: (",
Int.toString showcoll,
", ",
Int.toString showbytes,
")\n"])
end
val result = f () handle exn => (print_time ();
raise exn)
in
(print_time ();
result)
end
else
f ()
fun time_it (a, b) = xtime (a, true, b)
end
;
