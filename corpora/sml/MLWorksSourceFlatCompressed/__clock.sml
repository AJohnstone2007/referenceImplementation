require "clock";
require "$.basis.__timer";
require "$.system.__time";
require "$.basis.__real";
require "$.basis.__string_cvt";
structure Clock : CLOCK =
struct
val cpuClock = ref (Timer.startCPUTimer ())
val wallClock = ref (Timer.startRealTimer ())
fun reset () =
(cpuClock := Timer.startCPUTimer ();
wallClock := Timer.startRealTimer ())
fun clock () =
let
val wallTime = Timer.checkRealTimer (!wallClock)
val userTimeOpt = SOME (#usr (Timer.checkCPUTimer (!cpuClock)))
handle Time.Time => NONE
val percentage = case userTimeOpt of
SOME userTime =>
(Time.toReal userTime) /
(Time.toReal wallTime) * 100.0
| NONE => 100.0
in
case userTimeOpt of
SOME userTime =>
print ("Overall time passed: " ^
Time.fmt 1 wallTime ^
" seconds\n" ^
"Process has had CPU for: " ^
Time.fmt 1 userTime ^
" seconds\n" ^
"Time spent on this process is " ^
Real.fmt (StringCvt.FIX (SOME 1)) percentage ^
"%\n")
| NONE =>
print "Can't get user time, timers may have been invalidated.\n"
end
end
;
