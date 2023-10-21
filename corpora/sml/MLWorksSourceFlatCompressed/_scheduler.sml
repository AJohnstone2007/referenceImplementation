require "scheduler";
require "code";
require "dynamics";
require "tracer";
require "__lowlevel";
functor Scheduler(structure Code: CODE
structure Dynamics: DYNAMICS
structure Tracer: TRACER
sharing type Code.agent = Dynamics.agent
and type Code.object = Dynamics.object
and type Code.word = Dynamics.word
and type Code.word = Tracer.word
and type Code.constraint = Dynamics.constraint
) : SCHEDULER =
struct
open Code
open Dynamics
open Tracer
infix sub
abstype 'a queue = Q of 'a list
with
exception QueueEmpty
fun newQueue() = Q([])
fun addToQ(e,Q(q)) = Q(rev(e::(rev q)))
fun QFront(Q([])) = raise QueueEmpty
| QFront(Q(a::_)) = a
fun removeQ(Q([])) = raise QueueEmpty
| removeQ(Q(a::b))=Q(b)
fun appendQs(Q(a),Q(b))=Q(a@b)
fun QToList(Q(a))=a
end
datatype process =
alternative of bool ref * int ref * agent * context * bool ref
| guarded of condition * context * agent * bool ref
| selection of condition * context * agent * bool ref
| altGuard of bool ref
* int ref
* condition
* context
* agent * bool ref
| exec of agent * context * bool ref
| arithWait of object * object list ref * constraint * context
* bool ref
| procAlt of string
* bool ref * int ref * object list * object list
* int * agent * context
* bool ref
| procGuard of string * bool ref * int ref * object list
* object ref list * object list * object ref list
* int * context * agent * bool ref
fun processToWords(alternative(switch,counter,a,cntxt,_)) =
(if !switch
then []
else agentToWords(instantiateAgent(a,cntxt,nil,nil))
)
| processToWords(guarded(cond,cntxt,a,_)) =
let
val condWords = conditionToWords(cond,cntxt,nil,nil)
val agentWords = agentToWords(instantiateAgent(a,cntxt,nil,nil))
in
condWords @ [characters " and then "] @ agentWords
end
| processToWords(selection(cond,cntxt,a,_)) =
let
val condWords = conditionToWords(cond,cntxt,nil,nil)
val agentWords = agentToWords(instantiateAgent(a,cntxt,nil,nil))
in
(characters "If ")::condWords @ [characters " then "] @ agentWords
end
| processToWords(altGuard(switch,counter,cond,cntxt,a,_)) =
let
val condWords = conditionToWords(cond,cntxt,nil,nil)
val agentWords = agentToWords(instantiateAgent(a,cntxt,nil,nil))
val guardWords = condWords @ [characters " then "] @ agentWords
in
if !switch
then []
else (characters ("("^(makeString (!counter)^")ALT ")))::guardWords
end
| processToWords(exec(a,cntxt,_)) =
(characters "Exec "):: agentToWords(instantiateAgent(a,cntxt,nil,nil))
| processToWords(arithWait(var,ref(varlist),constr,cntxt,_)) =
(characters "Fix ")::
(openParen "(")::
objectToWords(var)@
((closeParen ")")::
objectListToWords(instList(varlist,cntxt)))@
((characters " then ")::
constraintToWords(instantiateConstraint(constr,cntxt,nil,nil)))
| processToWords(procAlt(name,switch,counter,args,params,numvars,a,cntxt,_)) =
(if !switch then []
else (characters ("("^makeString(!counter)^")CALL "))::
[(characters name),
(openParen "(")]@
objectListToWords(params)@
[(closeParen ")"),(characters " with ")]@
objectListToWords(instList(args,cntxt))
)
| processToWords(procGuard(name,switch,counter,avars,avals,pvars,pvals,
numvars,cntxt,a,_)) =
(if !switch then []
else (characters ("("^(makeString (!counter))^")Match "))::
[(characters name),
(openParen "(")]@
objectListToWords(instPList(instRefList(avals,cntxt),pvars,pvals))@
[(closeParen ")"),
(characters " to "),
(openParen "(")]@
objectListToWords(instList(avars,cntxt))@
[(closeParen ")")]
)
fun beingTraced(alternative(_,_,_,_,t)) = t
| beingTraced(guarded(_,_,_,t)) = t
| beingTraced(selection(_,_,_,t)) = t
| beingTraced(altGuard(_,_,_,_,_,t)) = t
| beingTraced(arithWait(_,_,_,_,t)) = t
| beingTraced(exec(_,_,t)) = t
| beingTraced(procAlt(_,_,_,_,_,_,_,_,t)) = t
| beingTraced(procGuard(_,_,_,_,_,_,_,_,_,_,t)) = t
val schedule = ref(newQueue()): process queue ref
fun take() =
let val proc = QFront(!schedule)
in
(schedule:=removeQ(!schedule);
if !(beingTraced(proc))
then let val w = processToWords(proc)
in
if w=[] then ()
else (Tracer.plainPrint(w,0);
Tracer.panel(beingTraced(proc)))
end
else ();
proc)
end
fun give(proc) = schedule:=addToQ(proc,!schedule)
fun giveQ(procQ) = schedule:=appendQs(!schedule,procQ)
fun wipeSchedule() = schedule:=newQueue()
val suspensions = ref(array(100,(ref(newQueue():process queue))))
val freed = ref([]): int list ref
val nextSlot = ref 0
val maxSuspended = ref 100
fun resize() =
let
val temp = array((!maxSuspended)+100,(ref(newQueue():process queue)))
val count = ref 0
in
(while (!count)<(!maxSuspended) do
(update(temp,!count,(!suspensions) sub (!count));
count:=(!count)+1);
maxSuspended:=(!maxSuspended)+100)
end
fun suspend(proc,~1) =
(
if !(beingTraced(proc)) then (print "----Suspend: ";
Tracer.plainPrint(processToWords(proc),13))
else ();
case !freed of
a::b => (freed:=b;
update(!suspensions,a,
ref(addToQ(proc,!((!suspensions) sub a))));
a
)
| nil => (if (!nextSlot)=(!maxSuspended) then resize() else ();
update(!suspensions,!nextSlot,
ref(addToQ(proc,!((!suspensions) sub !nextSlot))));
nextSlot:=(!nextSlot)+1;
(!nextSlot)-1
)
)
| suspend(proc,s) =
(if !(beingTraced(proc)) then (print "----Suspend: ";
Tracer.plainPrint(processToWords(proc),13))
else ();
update(!suspensions,s,ref(addToQ(proc,!((!suspensions) sub s))));
s)
fun wakeTrace(nil) = ()
| wakeTrace(a::b) =
(if !(beingTraced(a)) then (print "----Wake: ";
Tracer.plainPrint(processToWords(a),10))
else ();
wakeTrace(b))
fun awake(~1) = ()
| awake(s) =
let val x = !(!suspensions sub s)
in
(giveQ(!(!suspensions sub s));
update(!suspensions,s,ref(newQueue()));
wakeTrace(QToList(x));
freed:=s::(!freed)
)
end
fun suspended() =
let
fun inList(x,nil) = false
| inList(x,a::b) = if x=a then true else inList(x,b)
fun s(x) = if x=(!nextSlot) then nil
else if inList(x,!freed) then s(x+1)
else QToList(!(!suspensions sub x))::s(x+1)
in
s(0)
end
fun deSuspend() =
let
fun inList(x,nil) = false
| inList(x,a::b) = if x=a then true else inList(x,b)
fun s(x) = if x=(!nextSlot) then nil
else if inList(x,!freed) then s(x+1)
else ((!suspensions sub x):=newQueue(); s(x+1))
in
(ignore(s(0));
freed:=[];
nextSlot:=0
)
end
end
;
