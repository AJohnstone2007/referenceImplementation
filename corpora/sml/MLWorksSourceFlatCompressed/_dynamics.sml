require "code";
require "tracer";
require "dynamics";
require "__lowlevel";
functor Dynamics(structure Code: CODE
structure Tracer: TRACER
sharing type Code.word = Tracer.word): DYNAMICS =
struct
open Code
open Tracer
val nextUnknown = ref(0);
fun resetUnknownsCounter() = nextUnknown:=0
fun getNextUnknown() = let val x = !nextUnknown
in (nextUnknown:=x+1; x)
end
abstype context = CXT of (int ref * bool ref * object ref) array ref
| nullContext
with
fun nullCntxt() = nullContext
fun index(CXT(cntxt),num) = sub(!cntxt,num)
| index(nullContext,_) = (ref ~1,ref false,
ref (UNKNOWN (getNextUnknown())))
fun setx(CXT(cntxt),num,v) = update(!cntxt,num,v)
| setx(nullContext,_,_) = ()
fun buildCntxt(args,numvars,oldCntxt,ref(trace)) =
let
fun buildCxtList(nil,0,_) = nil
| buildCxtList(nil,~1,_)= nil
| buildCxtList(nil,n,cntxt) =
(ref(~1),ref(trace),
ref(UNKNOWN(getNextUnknown())))
::buildCxtList(nil,n-1,cntxt)
| buildCxtList(ref(VARIABLE(_,num))::rest,n,cntxt) =
(index(cntxt,num))::buildCxtList(rest,n-1,cntxt)
| buildCxtList(ref(POINTER(_,_,t,sn,v))::rest,n,cntxt) =
(sn,t,v)::buildCxtList(rest,n-1,cntxt)
| buildCxtList(object::rest,n,cntxt) =
(ref(~1),ref(trace),object)::
buildCxtList(rest,n-1,cntxt)
in
CXT(ref(fromList (buildCxtList(args,numvars,oldCntxt))))
handle SubScript => nullContext
end
end
fun valOf(cntxt,num) = let val (_,_,v) = index(cntxt,num) in v end
fun suspensionNumber(cntxt,num) =
let val (n,_,_) = index(cntxt,num) in n end
fun traceStatus(cntxt,num) =
let val (_,t,_) = index(cntxt,num) in t end
fun stripPtr(POINTER(_,_,_,_,x)) = stripPtr(!x)
| stripPtr(x) = x
fun makePtr(VARIABLE(name,num),cntxt) =
POINTER(name,num, traceStatus(cntxt,num),
suspensionNumber(cntxt,num),
valOf(cntxt,num))
| makePtr(x,_) = x
fun instantiate(v as VARIABLE _,cntxt) =
instantiate(makePtr(v,cntxt),cntxt)
| instantiate(HERBRAND(l,k,r),cntxt) =
HERBRAND(instantiate(l,cntxt),
instList(k,cntxt),
instantiate(r,cntxt))
| instantiate(POINTER(a,b,t,c,p),cntxt) =
(p:=instantiate(!p,cntxt);
if isUnknown(!p) orelse
(isNumber(!p) andalso not(isConstant(!p)))
then POINTER(a,b,t,c,p) else !p
)
| instantiate (RANGE(a,b,c,d),cntxt) =
RANGE(instantiate(a,cntxt),b,c,instantiate(d,cntxt))
| instantiate (PLUS(a,b),cntxt) =
PLUS(instantiate(a,cntxt),instantiate(b,cntxt))
| instantiate (MINUS(a,b),cntxt) =
MINUS(instantiate(a,cntxt),instantiate(b,cntxt))
| instantiate (TIMES(a,b),cntxt) =
TIMES(instantiate(a,cntxt),instantiate(b,cntxt))
| instantiate (DIVIDES(a,b),cntxt) =
DIVIDES(instantiate(a,cntxt),instantiate(b,cntxt))
| instantiate (x,_) = x
and instList(nil,_) = nil
| instList(a::b,cntxt) =
(instantiate(a,cntxt))::(instList(b,cntxt))
fun instRefList(nil,_) = nil
| instRefList(a::b,cntxt) =
instantiate(!a,cntxt)::instRefList(b,cntxt)
local
fun associatedVal(a,b::c,d::e) =
if sameObjects(a,b) then !d else associatedVal(a,c,e)
| associatedVal(a,_,_) = a
in
fun instPARAMS(p as PARAMETER _,pvrs,pvls) = associatedVal(p,pvrs,pvls)
| instPARAMS(v as VARIABLE(x,xn),pvrs,pvls) =
let
val p = PARAMETER(x,xn)
val v' = associatedVal(p,pvrs,pvls)
in
if sameObjects(v',p) then v else v'
end
| instPARAMS(HERBRAND(l,k,r),pvrs,pvls) =
HERBRAND(instPARAMS(l,pvrs,pvls),
instPList(k,pvrs,pvls),
instPARAMS(r,pvrs,pvls))
| instPARAMS(POINTER(a,b,t,c,p),pvrs,pvls) =
(p:=instPARAMS(!p,pvrs,pvls);
if isUnknown(!p) then POINTER(a,b,t,c,p)
else !p
)
| instPARAMS (RANGE(a,b,c,d),pvrs,pvls) =
RANGE(instPARAMS(a,pvrs,pvls),b,c,instPARAMS(d,pvrs,pvls))
| instPARAMS (PLUS(a,b),pvrs,pvls) =
PLUS(instPARAMS(a,pvrs,pvls),instPARAMS(b,pvrs,pvls))
| instPARAMS (MINUS(a,b),pvrs,pvls) =
MINUS(instPARAMS(a,pvrs,pvls),instPARAMS(b,pvrs,pvls))
| instPARAMS (TIMES(a,b),pvrs,pvls) =
TIMES(instPARAMS(a,pvrs,pvls),instPARAMS(b,pvrs,pvls))
| instPARAMS (DIVIDES(a,b),pvrs,pvls) =
DIVIDES(instPARAMS(a,pvrs,pvls),instPARAMS(b,pvrs,pvls))
| instPARAMS (x,_,_) = x
and instPList(nil,_,_) = nil
| instPList(a::b,pvrs,pvls) =
(instPARAMS(a,pvrs,pvls))::(instPList(b,pvrs,pvls))
end
fun instantiateConstraint(known obj,cntxt,pvrs,pvls) =
known (instPARAMS(instantiate(obj,cntxt),pvrs,pvls))
| instantiateConstraint(fixed obj,cntxt,pvrs,pvls) =
fixed (instPARAMS(instantiate(obj,cntxt),pvrs,pvls))
| instantiateConstraint(neg c,cntxt,pvrs,pvls) =
neg(instantiateConstraint(c,cntxt,pvrs,pvls))
| instantiateConstraint(eq(o1,o2),cntxt,pvrs,pvls) =
eq(instPARAMS(instantiate(o1,cntxt),pvrs,pvls),
instPARAMS(instantiate(o2,cntxt),pvrs,pvls))
| instantiateConstraint(greater(b,o1,o2),cntxt,pvrs,pvls) =
greater(b,instPARAMS(instantiate(o1,cntxt),pvrs,pvls),
instPARAMS(instantiate(o2,cntxt),pvrs,pvls))
| instantiateConstraint(less(b,o1,o2),cntxt,pvrs,pvls) =
less(b,instPARAMS(instantiate(o1,cntxt),pvrs,pvls),
instPARAMS(instantiate(o2,cntxt),pvrs,pvls))
| instantiateConstraint(consistent(c),cntxt,pvrs,pvls) =
consistent(instantiateConstraint(c,cntxt,pvrs,pvls))
| instantiateConstraint(none,cntxt,_,_) = none
| instantiateConstraint(tt,_,_,_) = tt
| instantiateConstraint(ff,_,_,_) = ff
fun instantiateAgent(success,cntxt,_,_) = success
| instantiateAgent(failure,cntxt,_,_) = failure
| instantiateAgent(guard(c,a),cntxt,pvrs,pvls) =
guard(instantiateConstraint(c,cntxt,pvrs,pvls),
instantiateAgent(a,cntxt,pvrs,pvls))
| instantiateAgent(select(c,a),cntxt,pvrs,pvls) =
select(instantiateConstraint(c,cntxt,pvrs,pvls),
instantiateAgent(a,cntxt,pvrs,pvls))
| instantiateAgent(tell(c),cntxt,pvrs,pvls) =
tell(instantiateConstraint(c,cntxt,pvrs,pvls))
| instantiateAgent(call(name,args),cntxt,pvrs,pvls) =
call(name, instPList(instList(args,cntxt),pvrs,pvls))
| instantiateAgent(par a,cntxt,pvrs,pvls) =
par (instAList(a,cntxt,pvrs,pvls))
| instantiateAgent(alt a,cntxt,pvrs,pvls) =
alt (instAList(a,cntxt,pvrs,pvls))
and instAList(nil,_,_,_) = nil
| instAList(a::b,cntxt,pvrs,pvls) =
instantiateAgent(a,cntxt,pvrs,pvls)::
instAList(b,cntxt,pvrs,pvls)
fun setVal(var,obj,traced,cntxt) =
let
fun minimiseRef(p as ref(POINTER(_,_,_,_,v as ref(x))))=
minimiseRef(v)
| minimiseRef(x) = x
val var' = minimiseRef(var)
in
(if !traced then
Tracer.plainPrint((characters "----* ")::
constraintToWords(
instantiateConstraint(eq(!var,obj),cntxt,nil,nil)),
6)
else ();
var':=obj
)
end
datatype condition = varsEq of bool * object list * object ref list
| allButOneFixed of object list * constraint
| waitFixed of object list * constraint
| oneOf of object list * bool ref * constraint
| rangeNarrowed of object * constraint
fun negate(varsEq(x,y,z)) = varsEq(not x,y,z)
| negate(c) = c
fun conditionToWords(varsEq(negation,vars,vals),cntxt,pvars,pvals) =
let
val vars' = instPList(instList(vars,cntxt),pvars,pvals)
val vals' = instPList(instRefList(vals,cntxt),pvars,pvals)
val wrds = (openParen "<")
::objectListToWords(vars')@
[(closeParen ">"),
(characters "="),
(openParen "<")]@
objectListToWords(vals')@
[closeParen ">"]
in
if negation then (characters "not ")::wrds else wrds
end
| conditionToWords(waitFixed(v,constr),cntxt,pvars,pvals) =
let
val v' = instList(v,cntxt)
in
(characters "waitFixed ")::objectListToWords(v')@
((characters " then ")::constraintToWords(
instantiateConstraint(constr,cntxt,pvars,pvals)))
end
| conditionToWords(allButOneFixed(fv,constr),cntxt,pvars,pvals)=
(characters "All but one fixed ")::
objectListToWords(instList(fv,cntxt))@
((characters " then ")
::constraintToWords(
instantiateConstraint(constr,cntxt,pvars,pvals)))
| conditionToWords(oneOf(fv,_,constr),cntxt,pvars,pvals) =
(characters "On one  ")
::objectListToWords(instList(fv,cntxt))@
((characters " then ")::constraintToWords(
instantiateConstraint(constr,cntxt,pvars,pvals)))
| conditionToWords(rangeNarrowed(v,constr),cntxt,pvars,pvals) =
(characters "Narrow ")
::objectToWords(instantiate(v,cntxt))@
((characters " then ")
::constraintToWords(
instantiateConstraint(constr,cntxt,pvars,pvals)))
datatype answer = yes | no | maybe of condition
fun isYes(yes) = true
| isYes _ = false
end
;
