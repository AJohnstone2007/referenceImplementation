require "$.system.__time";
require "$.basis.__array";
require "$.basis.__timer";
require "$.basis.__string";
require "$.basis.__list";
require "$.basis.__int32";
require "$.basis.__text_io";
require "$.utils.__mutex";
local
structure T = MLWorks.Threads;
structure I = T.Internal;
structure P = I.Preemption;
val output = Mutex.newBinaryMutex false;
val outputString = ref "";
fun safePrint message =
Mutex.critical([output], fn()=>outputString:=(!outputString^message))()
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
fun occupySomeTime scale message =
let
val timeToPass = Time.fromMilliseconds (random()*scale)
val _ = safePrint message
val timer = Timer.startRealTimer()
fun passTime() = if Time.<(Timer.checkRealTimer timer,timeToPass)
then passTime()
else ()
in
passTime()
end;
val deadlockFlag = ref false;
in
val _ = MLWorks.Internal.Runtime.Memory.max_stack_blocks:=7;
fun runSmokers interval timeLimit =
let
val paper = Mutex.newBinaryMutex true;
val tobacco = Mutex.newBinaryMutex true;
val matches = Mutex.newBinaryMutex true;
val smoked = Mutex.newBinaryMutex true;
fun makeSmoker (name,item1,item2) =
let
fun smoking() =
while true do
(
Mutex.wait [item1,item2];
occupySomeTime 1 (name^" has lit up.\n");
Mutex.signal [smoked]
)
in
smoking
end
fun makeAgent() =
let
fun supply() =
while true do
(
let val (mesg,items) =
case (random() mod 3)
of 0 => ("paper and matches",[paper,matches])
| 1 => ("paper and tobacco",[paper,tobacco])
| 2 => ("matches and tobacco",[matches,tobacco])
| _ => ("",[])
in
safePrint ("The agent has supplied "^mesg^".\n");
Mutex.signal items;
Mutex.wait [smoked]
end
)
in
supply
end
val _ = outputString:=""
val p1 = makeAgent ()
val p2 = makeSmoker ("The Marlboro man",matches,tobacco)
val p3 = makeSmoker ("Humphrey Bogart",paper,matches)
val p4 = makeSmoker ("The man in the Iron Lung",paper,tobacco)
val _ = P.set_interval interval;
val _ = P.start();
val id1 = T.fork p1 ()
val id2 = T.fork p2 ()
val id3 = T.fork p3 ()
val id4 = T.fork p4 ()
val _ = deadlockFlag:=false;
fun detectDeadlock () =
if Mutex.allSleeping [id1,id2,id3,id4]
then deadlockFlag:=true
else detectDeadlock()
val dd = T.fork detectDeadlock ()
fun checkTime timer =
if Time.toSeconds(Timer.checkRealTimer timer)<timeLimit
andalso not (!deadlockFlag)
then checkTime timer
else (I.kill id1;
I.kill id2;
I.kill id3;
I.kill id4;
if !deadlockFlag then () else I.kill dd)
val timer = Timer.startRealTimer()
val _ = checkTime timer
val _ = if !deadlockFlag then print "Deadlock.\n"
else print "Finished.\n"
val _ = print "See file smokers.out for log.\n"
val x = TextIO.openOut "smokers.out"
in
TextIO.output(x,!outputString);
TextIO.closeOut x
end
end
;
