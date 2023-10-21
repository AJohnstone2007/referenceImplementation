local
fun fib n = if n<2 then 1 else fib(n-1) + fib(n-2);
val totalRealTime = Timer.startRealTimer()
val totalCPUTime = Timer.startCPUTimer()
val totReal = Timer.totalRealTimer()
val totCPU = Timer.totalCPUTimer()
in
val a = Time.<= (Timer.checkRealTimer totalRealTime, Timer.checkRealTimer totalRealTime);
val b = Time.< (((Timer.checkRealTimer totalRealTime) before (ignore(fib 25); ())), Timer.checkRealTimer totalRealTime);
val c = Time.<= (Timer.checkRealTimer totReal, Timer.checkRealTimer totReal);
val d = Time.<= (Timer.checkRealTimer totalRealTime, Timer.checkRealTimer totReal);
val e = Time.<= (Timer.checkGCTime totalCPUTime, Timer.checkGCTime totalCPUTime);
val f = Time.<= (Timer.checkGCTime totCPU, Timer.checkGCTime totCPU);
val g = Time.<= (Timer.checkGCTime totalCPUTime, Timer.checkGCTime totCPU);
local
val op <= = fn ({usr=usr1, sys=sys1}, {usr=usr2, sys=sys2})
=> Time.<= (usr1, usr2) andalso Time.<= (sys1, sys2);
fun cput1 < cput2 = (cput1 <= cput2) andalso (cput1 <> cput2);
in
val h = (Timer.checkCPUTimer totalCPUTime) <= (Timer.checkCPUTimer totalCPUTime);
val i = ((Timer.checkCPUTimer totalCPUTime) before (ignore(fib 25); ())) < (Timer.checkCPUTimer totalCPUTime);
val j= (Timer.checkCPUTimer totCPU) <= (Timer.checkCPUTimer totCPU);
val k = (Timer.checkCPUTimer totalCPUTime) <= (Timer.checkCPUTimer totCPU);
local
val ctmr = Timer.startCPUTimer ()
in
val l = (Timer.checkCPUTimer ctmr) <= (Timer.checkCPUTimer ctmr);
val m = ((Timer.checkCPUTimer ctmr) before (ignore(fib 25); ())) < (Timer.checkCPUTimer ctmr);
end
end
end
;
