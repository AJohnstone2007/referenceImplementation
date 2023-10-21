require "$.system.__time";
require "$.basis.__timer";
require "$.basis.__string";
require "$.basis.__int32";
require "$.basis.__text_io";
require "$.utils.__mutex";
local
structure T = MLWorks.Threads;
structure I = T.Internal;
structure P = I.Preemption;
val chopstick1 = Mutex.newBinaryMutex false;
val chopstick2 = Mutex.newBinaryMutex false;
val chopstick3 = Mutex.newBinaryMutex false;
val chopstick4 = Mutex.newBinaryMutex false;
val chopstick5 = Mutex.newBinaryMutex false;
val output = Mutex.newBinaryMutex false;
val outputString = ref "";
fun safePrint message = Mutex.critical([output], fn()=>
outputString:=(!outputString^message))()
fun unsafePrint message = outputString := (!outputString)^message
fun flushOutput () = while (Mutex.query output <>[]) do ()
fun random() =
let
val timeString = Time.fmt 4 (Time.now())
val number = case (rev(String.tokens (fn c => c= #".") timeString))
of [] => 0
| (h::_) => valOf(Int32.fromString h)
in
number mod 1971
end;
fun occupySomeTime message =
let
val timeToPass = Time.fromMilliseconds (random())
val _ = safePrint message
val timer = Timer.startRealTimer()
fun passTime() = if Time.<(Timer.checkRealTimer timer,timeToPass)
then passTime()
else ()
in
passTime()
end;
fun philosopher (name,chopsticks,chopstickNames) =
let
fun sillyMessage() =
case (random() mod 5)
of 0 => ""
| 1 => name^" has burped.\n"
| 2 => ""
| 3 => name^" is throwing up.\n"
| 4 => name^" has just proved a difficult theorem.\n"
| _ => ""
fun philosophize ():unit =
while true do
(
occupySomeTime (name^" is thinking.\n");
safePrint (name^" is hungry.\n");
Mutex.wait ([output]@chopsticks);
unsafePrint (name^" has picked up chopsticks "
^chopstickNames^" and is eating heartily.\n");
Mutex.signal [output];
occupySomeTime (sillyMessage());
Mutex.critical
([output],
fn () => (Mutex.signal chopsticks;
unsafePrint (name^" has put down chopsticks "
^chopstickNames^".\n")))()
)
in
philosophize
end;
val deadlockFlag = ref false;
in
val _ = MLWorks.Internal.Runtime.Memory.max_stack_blocks:=8;
fun runPhilosophers intervals timeLimit =
let
val _ = outputString := ""
val p1 = philosopher("Russell",[chopstick1,chopstick2],"1 and 2");
val p2 = philosopher("  Godel",[chopstick2,chopstick3],"2 and 3");
val p3 = philosopher(" Kleene",[chopstick3,chopstick4],"3 and 4");
val p4 = philosopher("  Frege",[chopstick4,chopstick5],"4 and 5");
val p5 = philosopher(" Church",[chopstick5,chopstick1],"5 and 1");
val _ = P.set_interval intervals;
val _ = P.start();
val id1 = T.fork p1 ()
val id2 = T.fork p2 ()
val id3 = T.fork p3 ()
val id4 = T.fork p4 ()
val id5 = T.fork p5 ()
fun detectDeadlock() =
if Mutex.allSleeping [id1,id2,id3,id4,id5]
then deadlockFlag:=true
else detectDeadlock()
val _ = deadlockFlag:=false
val dd = T.fork detectDeadlock ()
fun checkTime timer =
if Time.toSeconds(Timer.checkRealTimer timer)<timeLimit
andalso not (!deadlockFlag)
then checkTime timer
else (I.kill id1;
I.kill id2;
I.kill id3;
I.kill id4;
I.kill id5;
if !deadlockFlag then () else I.kill dd)
val timer = Timer.startRealTimer()
val _ = checkTime timer
val _ = if Mutex.test [chopstick1] then ()
else Mutex.signal [chopstick1];
val _ = if Mutex.test [chopstick2] then ()
else Mutex.signal [chopstick2];
val _ = if Mutex.test [chopstick3] then ()
else Mutex.signal [chopstick3];
val _ = if Mutex.test [chopstick4] then ()
else Mutex.signal [chopstick4];
val _ = if Mutex.test [chopstick5] then ()
else Mutex.signal [chopstick5];
val _ = if !deadlockFlag then print "Deadlock.\n"
else print "Finished.\n"
val _ = print "See file philosophers.out for log.\n"
val out = TextIO.openOut "philosophers.out";
in
TextIO.output(out,!outputString);
TextIO.closeOut out
end
end
;
