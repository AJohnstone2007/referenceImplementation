require "$.system.__time";
require "$.basis.__array";
require "$.basis.__timer";
require "$.utils.__mutex";
local
structure T = MLWorks.Threads;
structure I = T.Internal;
structure P = I.Preemption;
local
val buffer = Array.array(10,0);
val size = ref 0;
val putIndex = ref 0;
val getIndex = ref 0;
in
fun addToBuffer x =
(if !size=10 then print "data lost!\n"
else size:= !size+1;
Array.update(buffer,!putIndex,x);
putIndex:=(!putIndex+1) mod 10)
fun readFromBuffer () =
(if !size=0 then print "phantom data!\n"
else size:= !size-1;
Array.sub(buffer,!getIndex) before
getIndex:=(!getIndex+1) mod 10)
end
val access = Mutex.newBinaryMutex false;
val empty = Mutex.newCountingMutex 10;
val full = Mutex.newCountingMutex 0;
fun makeProducer () =
let
val producerData = ref 0;
fun nextItem()=(!producerData) before (producerData:=(!producerData)+1);
fun produce() =
while true do
let val item = nextItem()
in
Mutex.wait [empty];
Mutex.critical([access],addToBuffer) item;
Mutex.signal [full]
end
in
produce
end
val output: int list ref = ref []
fun makeConsumer () =
let
fun processDatum x = output:= x::(!output)
fun consume() =
while true do
let
val _ = Mutex.wait [full];
val datum = Mutex.critical([access],readFromBuffer)();
val _ = Mutex.signal [empty];
in
processDatum datum
end
in
consume
end
val deadlockFlag = ref false;
in
val _ = MLWorks.Internal.Runtime.Memory.max_stack_blocks:=5;
fun runProdCons interval timeLimit =
let
val _ = output:=[]
val p1 = makeProducer()
val p2 = makeConsumer()
val _ = P.set_interval interval;
val _ = P.start();
val id1 = T.fork p1 ()
val id2 = T.fork p2 ()
fun detectDeadlock() =
if Mutex.allSleeping [id1,id2]
then deadlockFlag:=true
else detectDeadlock()
val _ = deadlockFlag:=false
val dd = T.fork detectDeadlock ()
fun checkTime timer =
if !deadlockFlag then ()
else if Time.toSeconds(Timer.checkRealTimer timer)<timeLimit
then checkTime timer
else (I.kill id1;
I.kill id2;
if !deadlockFlag then () else I.kill dd)
val timer = Timer.startRealTimer()
val _ = checkTime timer
val _ = if !deadlockFlag then print "Deadlock.\n"
else print "Finished.\n"
in
()
end
fun testOutput () =
let
fun testOK ([],_) = true
| testOK (h::t,n) = h+1=n andalso testOK(t,h)
in
case (!output)
of [] => true
| (h::t) => testOK (t,h)
end
end
val testAnswer = (runProdCons 1 10; testOutput())
;
