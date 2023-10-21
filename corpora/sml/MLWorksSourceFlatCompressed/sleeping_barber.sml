require "$.system.__time";
require "$.basis.__array";
require "$.basis.__timer";
require "$.basis.__string";
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
val LIMIT = 4;
val numChairs = ref LIMIT;
fun makeBarber (chairs,barber,customer,custom,output) =
let
val barberSleeping = ref true
fun barbering() =
while true do
(Mutex.wait [custom,output];
if !barberSleeping
then (unsafePrint "The barber has woken up.\n";
barberSleeping:=false)
else ();
Mutex.signal [barber,output];
Mutex.wait [customer];
occupySomeTime 1 "The barber is busy.\n";
safePrint "The barber has finished.\n";
Mutex.signal [customer];
if !numChairs<LIMIT then () else
(safePrint "The barber has fallen asleep.\n";
barberSleeping:=true)
)
in
barbering
end
fun makeCustomer name (chairs,barber,customer,custom,output) =
let
val inShop = ref false
fun sillyMessage() =
case (random() mod 10)
of 0 => name^" is buying a packet of fags.\n"
| 1 => name^" is pondering lambda optimization.\n"
| 2 => name^" is enjoying a black cherry flavoured yoghurt.\n"
| 3 => name^" is chatting to a dining philosopher.\n"
| 4 => name^" has just proved a difficult theorem.\n"
| 5 => name^" is window-shopping.\n"
| 6 => name^" is buying lunch.\n"
| 7 => name^" has gone swimming.\n"
| 8 => name^" is avoiding the team dinner.\n"
| 9 => name^" is playing some guitar.\n"
| _ => ""
fun customering() =
while true do
(occupySomeTime 2 (sillyMessage());
Mutex.critical([output],fn ()=>
(Mutex.signal [custom];
unsafePrint (name^" has entered the shop.\n");
inShop:=true)
)();
Mutex.critical
([chairs], fn()=>
if !numChairs=0
then (Mutex.wait [custom,output];
unsafePrint (name^" has left the crowded shop.\n");
inShop:=false;
Mutex.signal [output])
else numChairs:=(!numChairs-1)
)();
if !inShop
then (Mutex.wait [barber];
Mutex.signal [customer];
safePrint(name^" is getting a hair cut.\n");
Mutex.wait [customer,output];
unsafePrint (name^" has a new hairstyle.\n");
Mutex.signal [output];
Mutex.wait ([chairs,custom,output]);
numChairs:=(!numChairs+1);
unsafePrint (name^" has left the shop.\n");
inShop:=false;
Mutex.signal [chairs,output]
)
else ())
in
customering
end
in
val _ = MLWorks.Internal.Runtime.Memory.max_stack_blocks:=10;
fun runBarber interval timeLimit =
let
val chairs = Mutex.newBinaryMutex false;
val _ = numChairs:= LIMIT;
val barber = Mutex.newBinaryMutex true;
val customer = Mutex.newBinaryMutex false;
val custom = Mutex.newCountingMutex 0;
val deadlockFlag = ref false;
val _ = outputString:=""
val b = makeBarber(chairs,barber,customer,custom,output)
val p1 = makeCustomer "    Jon" (chairs,barber,customer,custom,output)
val p2 = makeCustomer "   Dave" (chairs,barber,customer,custom,output)
val p3 = makeCustomer "Matthew" (chairs,barber,customer,custom,output)
val p4 = makeCustomer "Stephen" (chairs,barber,customer,custom,output)
val p6 = makeCustomer " Andrew" (chairs,barber,customer,custom,output)
val p7 = makeCustomer "     Jo" (chairs,barber,customer,custom,output)
val p8 = makeCustomer "   John" (chairs,barber,customer,custom,output)
val _ = P.set_interval interval;
val _ = P.start();
val bid = T.fork b ()
val id1 = T.fork p1 ()
val id2 = T.fork p2 ()
val id3 = T.fork p3 ()
val id4 = T.fork p4 ()
val id6 = T.fork p6 ()
val id7 = T.fork p7 ()
val id8 = T.fork p8 ()
val _ = deadlockFlag:=false;
fun detectDeadlock () =
if Mutex.allSleeping [bid,id1,id2,id3,id4,id6,id7,id8]
then deadlockFlag:=true
else detectDeadlock()
val dd = T.fork detectDeadlock ()
fun checkTime timer =
if Time.toSeconds(Timer.checkRealTimer timer)<timeLimit
andalso not (!deadlockFlag)
then checkTime timer
else (I.kill bid;
I.kill id1;
I.kill id2;
I.kill id3;
I.kill id4;
I.kill id6;
I.kill id7;
I.kill id8;
if !deadlockFlag then () else I.kill dd)
val timer = Timer.startRealTimer()
val _ = checkTime timer
val _ = if !deadlockFlag then print "Deadlock.\n"
else print "Finished.\n"
val _ = print "See file barber.out for log.\n"
val x = TextIO.openOut "barber.out";
in
TextIO.output(x,!outputString);
TextIO.closeOut x
end
end
;
