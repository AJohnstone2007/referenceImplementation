require "$.system.__time";
require "$.basis.__array";
require "$.basis.__timer";
require "$.basis.__string";
require "$.basis.__int32";
require "$.basis.__text_io";
require "$.utils.__mutex";
val deadlockFlag = ref false;
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
in
val _ = MLWorks.Internal.Runtime.Memory.max_stack_blocks:=10;
fun runReadWrite interval timeLimit =
let
val datum = ref 0;
val OKtoWrite = Mutex.newBinaryMutex false;
val OKtoRead = Mutex.newBinaryMutex false;
val writing = ref false;
val numReaders = ref 0;
val status = Mutex.newBinaryMutex false;
fun empty mutex = Mutex.query mutex=[]
fun makeReader name =
while true do
(
occupySomeTime 1 "";
Mutex.await([OKtoRead,status], fn()=> not (!writing));
numReaders:=(!numReaders)+1;
Mutex.signal [status,OKtoRead];
occupySomeTime 1 ("  "^name^" is reading "^
(Int32.toString (!datum)^".\n"));
safePrint ("   "^name^" has finished reading.\n");
Mutex.wait [status];
numReaders:=(!numReaders)-1;
if !numReaders=0 then Mutex.signal [OKtoWrite,status] else
Mutex.signal [status]
)
fun makeWriter name =
while true do
(
occupySomeTime 1 "";
Mutex.await([OKtoWrite,status],
fn()=> not(!writing) andalso !numReaders=0);
writing:=true;
Mutex.signal [status];
datum := (random());
occupySomeTime 1 (name^
" is writing "^(Int32.toString (!datum))^".\n");
safePrint (name^" has finished writing.\n");
Mutex.wait [status];
writing:=false;
if empty OKtoRead
then Mutex.signal [OKtoWrite,status]
else Mutex.signal [OKtoRead,status]
)
val _ = outputString:=""
val _ = writing:=false
val _ = numReaders:=0
val _ = P.set_interval interval;
val _ = P.start();
val id1 = T.fork makeWriter "Voltaire"
val id2 = T.fork makeWriter "Tolstoy"
val id3 = T.fork makeWriter "Hemmingway"
val id4 = T.fork makeReader "Arthur"
val id5 = T.fork makeReader "Betty"
val id6 = T.fork makeReader "Charles"
val id7 = T.fork makeReader "Diana"
val _ = deadlockFlag:=false;
fun detectDeadlock () =
if Mutex.allSleeping [id1,id2,id3,id4,id5,id6,id7]
then deadlockFlag:=true
else detectDeadlock()
val dd = T.fork detectDeadlock ()
fun checkTime timer =
if Time.toSeconds(Timer.checkRealTimer timer)<timeLimit
andalso not(!deadlockFlag)
then checkTime timer
else (I.kill id1;
I.kill id2;
I.kill id3;
I.kill id4;
I.kill id5;
I.kill id6;
I.kill id7;
if !deadlockFlag then () else I.kill dd)
val timer = Timer.startRealTimer()
val _ = checkTime timer
val _ = if !deadlockFlag then print "Deadlock.\n"
else print "Finished.\n"
val _ = print "See file read_write.out for log.\n"
val x = TextIO.openOut "read_write.out"
in
TextIO.output(x,!outputString);
TextIO.closeOut x
end
end
;
