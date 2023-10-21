require "^.system.__time";
require "timer";
structure Timer : TIMER =
struct
val env = MLWorks.Internal.Runtime.environment
datatype cpu_timer =
CPU_TIMER of
{ usr: Time.time
, sys: Time.time
, gc: Time.time
}
datatype real_timer = REAL_TIMER of Time.time | TOTAL
val now : unit -> cpu_timer = env "Timer.now"
val startCPUTimer : unit -> cpu_timer = now
fun totalCPUTimer() =
CPU_TIMER{usr=Time.zeroTime, sys=Time.zeroTime, gc=Time.zeroTime}
fun checkCPUTimer (CPU_TIMER {usr, sys, ...}) =
let
val (CPU_TIMER {usr= usr', sys=sys', ...}) = now ()
val usr'' = Time.-(usr', usr)
val sys'' = Time.-(sys', sys)
in
{usr=usr'', sys=sys''}
end
fun checkGCTime (CPU_TIMER {gc, ...}) =
let
val (CPU_TIMER {gc=gc', ...}) = now ()
in
Time.-(gc', gc)
end
fun startRealTimer () = REAL_TIMER (Time.now ())
fun totalRealTimer() = TOTAL
val startTime = env "Time.start"
fun checkRealTimer arg =
let
val t = case arg of
REAL_TIMER t => t
| TOTAL => startTime ()
in
Time.-(Time.now (), t)
end
end
;
