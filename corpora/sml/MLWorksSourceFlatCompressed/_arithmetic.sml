require "code";
require "dynamics";
require "scheduler";
require "arithmetic";
functor Arithmetic(structure Code: CODE
structure Dynamics: DYNAMICS
structure Scheduler: SCHEDULER
sharing type Code.object = Dynamics.object
and type Code.constraint = Dynamics.constraint
and type Code.agent = Dynamics.agent
and type Code.agent = Scheduler.agent
and type Code.object = Scheduler.object
and type Code.constraint = Scheduler.constraint
and type Code.clause = Scheduler.clause
and type Dynamics.context = Scheduler.context): ARITHMETIC =
struct
open Code
open Dynamics
open Scheduler
exception ArithError
fun sieve(nil) = nil
| sieve((a as VARIABLE _)::b) = a::sieve(b)
| sieve(_::b) = sieve(b)
fun member(_,nil) = false
| member(x,a::b) = if sameObjects(x,a) then true
else member(x,b)
fun isNil nil = true
| isNil _ = false
fun add(INFINITY,MINFINITY) = NUMBER 0.0
| add(MINFINITY,INFINITY) = NUMBER 0.0
| add(INFINITY,_) = INFINITY
| add(_,INFINITY) = INFINITY
| add(MINFINITY,_) = MINFINITY
| add(_,MINFINITY) = MINFINITY
| add(NUMBER x, NUMBER y) = NUMBER (x+y)
| add(NUMBER x, RANGE(lt,lte,gte,gt)) =
RANGE(add(lt,NUMBER x),lte,gte,add(gt,NUMBER x))
| add(RANGE(lt,lte,gte,gt),NUMBER y) =
RANGE(add(lt,NUMBER y),lte,gte,add(gt,NUMBER y))
| add(RANGE(lt,lte,gte,gt),RANGE(lt',lte',gte',gt')) =
RANGE(add(lt,lt'),lte andalso lte',gte andalso gte',add(gt,gt'))
| add _ = raise ArithError
fun sbt(INFINITY,INFINITY) = NUMBER 0.0
| sbt(MINFINITY,MINFINITY) = NUMBER 0.0
| sbt(INFINITY,_) = INFINITY
| sbt(_,INFINITY) = MINFINITY
| sbt(MINFINITY,_) = MINFINITY
| sbt(_,MINFINITY) = INFINITY
| sbt(NUMBER x, NUMBER y) = NUMBER (x-y)
| sbt(NUMBER x, RANGE(lt,lte,gte,gt)) =
RANGE(sbt(NUMBER x,gt),gte,lte,sbt(NUMBER x,lt))
| sbt(RANGE(lt,lte,gte,gt),NUMBER y) =
RANGE(sbt(lt,NUMBER y),lte,gte,sbt(gt,NUMBER y))
| sbt(RANGE(lt,lte,gte,gt),RANGE(lt',lte',gte',gt')) =
RANGE(sbt(lt,gt'),lte andalso gte',gte andalso lte',sbt(gt,lt'))
| sbt _ = raise ArithError
fun mul(INFINITY,MINFINITY) = MINFINITY
| mul(MINFINITY,INFINITY) = MINFINITY
| mul(INFINITY,NUMBER x) = if x<0.0 then MINFINITY else INFINITY
| mul(MINFINITY,NUMBER x) = if x<0.0 then INFINITY else MINFINITY
| mul(NUMBER x,INFINITY) = if x<0.0 then MINFINITY else INFINITY
| mul(NUMBER x,MINFINITY) = if x<0.0 then INFINITY else MINFINITY
| mul(INFINITY,RANGE _) = RANGE(MINFINITY,false,false,INFINITY)
| mul(MINFINITY,RANGE _) = RANGE(MINFINITY,false,false,INFINITY)
| mul(RANGE _,INFINITY) = RANGE(MINFINITY,false,false,INFINITY)
| mul(RANGE _,MINFINITY) = RANGE(MINFINITY,false,false,INFINITY)
| mul(NUMBER x, NUMBER y) = NUMBER (x*y)
| mul(NUMBER x, RANGE(lt,lte,gte,gt)) =
RANGE(mul(lt,NUMBER x),lte,gte,mul(gt,NUMBER x))
| mul(RANGE(lt,lte,gte,gt),NUMBER y) =
RANGE(mul(lt,NUMBER y),lte,gte,mul(gt,NUMBER y))
| mul(RANGE(lt,lte,gte,gt),RANGE(lt',lte',gte',gt')) =
RANGE(mul(lt,lt'),lte andalso lte',gte andalso gte',mul(gt,gt'))
| mul _ = raise ArithError
fun dvd(INFINITY,MINFINITY) = MINFINITY
| dvd(MINFINITY,INFINITY) = MINFINITY
| dvd(INFINITY,_) = INFINITY
| dvd(MINFINITY,_) = MINFINITY
| dvd(_,INFINITY) = NUMBER 0.0
| dvd(_,MINFINITY) = NUMBER 0.0
| dvd(NUMBER x, NUMBER y) = if sameObjects(NUMBER y,NUMBER 0.0) then INFINITY else NUMBER(x/y)
| dvd(NUMBER x, RANGE(lt,lte,gte,gt)) =
RANGE(dvd(NUMBER x,gt),gte,lte,dvd(NUMBER x,lt))
| dvd(RANGE(lt,lte,gte,gt),NUMBER y) =
RANGE(dvd(lt,NUMBER y),lte,gte,dvd(gt,NUMBER y))
| dvd(RANGE(lt,lte,gte,gt),RANGE(lt',lte',gte',gt')) =
RANGE(dvd(lt,gt'),lte andalso gte',gte andalso lte',dvd(gt,lt'))
| dvd _ = raise ArithError
fun GT(INFINITY,_) = yes
| GT(_,MINFINITY) = yes
| GT(_,INFINITY) = no
| GT(MINFINITY,_) = no
| GT(NUMBER x,NUMBER y) = if x>y then yes else no
| GT(NUMBER x,p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt)))) =
if (isYes(GT(NUMBER x,gt))) orelse
((not gte) andalso (isYes(EQ(NUMBER x,gt))))
then yes
else if (isYes(LT(NUMBER x,lt))) orelse
((not lte) andalso (isYes(EQ(NUMBER x,lt))))
then no
else maybe(rangeNarrowed(p,greater(false,NUMBER x,p)))
| GT(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),NUMBER y) =
if (isYes(GT(lt,NUMBER y))) orelse
((not lte) andalso (isYes(EQ(NUMBER y,lt))))
then yes
else if (isYes(LT(gt,NUMBER y))) orelse
((not gte) andalso (isYes(EQ(NUMBER y,gt))))
then no
else maybe(rangeNarrowed(p,greater(false,p,NUMBER y)))
| GT(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),
q as POINTER(_,_,_,_,ref(RANGE(lt',lte',gte',gt')))) =
if (isYes(GT(lt,gt'))) orelse
(sameObjects(lt,gt') andalso ((not lte) orelse (not gte')))
then yes
else if (isYes(LT(gt,lt'))) orelse
(sameObjects(gt,lt') andalso ((not gte) orelse (not lte')))
then no
else maybe(oneOf([p,q],ref(false),greater(false,p,q)))
| GT(v as VARIABLE _,w) = maybe(waitFixed([v],greater(false,v,w)))
| GT(p as POINTER _,w) = maybe(waitFixed([p],greater(false,p,w)))
| GT(w,v as VARIABLE _) = maybe(waitFixed([v],greater(false,w,v)))
| GT(w,p as POINTER _) = maybe(waitFixed([p],greater(false,w,p)))
| GT _ = raise ArithError
and GTE(INFINITY,_) = yes
| GTE(_,MINFINITY) = yes
| GTE(_,INFINITY) = no
| GTE(MINFINITY,_) = no
| GTE(NUMBER x,NUMBER y) = if x>=y then yes else no
| GTE(NUMBER x,p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt)))) =
if (isYes(GT(NUMBER x,gt))) orelse
(gte andalso (isYes(EQ(NUMBER x,gt))))
then yes
else if (isYes(LT(NUMBER x,lt))) orelse
((not lte) andalso (isYes(EQ(NUMBER x,lt))))
then no
else maybe(rangeNarrowed(p,greater(true,NUMBER x,p)))
| GTE(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),NUMBER y) =
if (isYes(GT(lt,NUMBER y))) orelse
(lte andalso (isYes(EQ(NUMBER y,lt))))
then yes
else if (isYes(LT(gt,NUMBER y))) orelse
((not gte) andalso (isYes(EQ(NUMBER y,gt))))
then no
else maybe(rangeNarrowed(p,greater(true,p,NUMBER y)))
| GTE(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),
q as POINTER(_,_,_,_,ref(RANGE(lt',lte',gte',gt')))) =
if (isYes(GT(lt,gt'))) orelse
(sameObjects(lt,gt') andalso (lte orelse gte'))
then yes
else if (isYes(LT(gt,lt'))) orelse
(sameObjects(gt,lt') andalso ((not gte) orelse (not lte')))
then no
else maybe(oneOf([p,q],ref(true),greater(true,p,q)))
| GTE(v as VARIABLE _,w) = maybe(waitFixed([v],greater(true,v,w)))
| GTE(p as POINTER _,w) = maybe(waitFixed([p],greater(true,p,w)))
| GTE(w,v as VARIABLE _) = maybe(waitFixed([v],greater(true,w,v)))
| GTE(w,p as POINTER _) = maybe(waitFixed([p],greater(true,w,p)))
| GTE _ = raise ArithError
and LT(p,q) = GT(q,p)
and LTE(p,q) = GTE(q,p)
and EQ(INFINITY,INFINITY) = yes
| EQ(MINFINITY,MINFINITY) = yes
| EQ(INFINITY,_) = no
| EQ(MINFINITY,_) = no
| EQ(_,INFINITY) = no
| EQ(_,MINFINITY) = no
| EQ(m as NUMBER _,n as NUMBER _) = if sameObjects(m,n) then yes else no
| EQ(NUMBER x,p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt)))) =
if (isYes(LT(NUMBER x,lt))) orelse
(isYes(GT(NUMBER x,gt))) orelse
((not lte) andalso (isYes(EQ(NUMBER x,lt)))) orelse
((not gte) andalso (isYes(EQ(NUMBER x,gt))))
then no
else maybe(rangeNarrowed(p,eq(NUMBER x,p)))
| EQ(x,NUMBER y) = EQ(NUMBER y,x)
| EQ(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),
q as POINTER(_,_,_,_,ref(RANGE(lt',lte',gte',gt')))) =
if (isYes(LT(gt,lt'))) orelse
((not gte) orelse (not lte') andalso (isYes(EQ(gt,lt')))) orelse
(isYes(GT(lt,gt'))) orelse
((not lte) orelse (not gte') andalso isYes(EQ(lt,gt')))
then no
else maybe(oneOf([p,q],ref(true),eq(p,q)))
| EQ(v as VARIABLE _,w) = maybe(waitFixed([v],eq(v,w)))
| EQ(p as POINTER _,w) = maybe(waitFixed([p],eq(p,w)))
| EQ(w,v as VARIABLE _) = maybe(waitFixed([v],eq(w,v)))
| EQ(w,p as POINTER _) = maybe(waitFixed([p],eq(w,p)))
| EQ _ = raise ArithError
fun simplify(PLUS(a,b),fv) =
let
val (a',fv') = simplify(a,fv)
val (b',fv'') = simplify(b,fv')
in
if isConstant(a') andalso isConstant(b') then (add(a',b'),fv'')
else (PLUS(a',b'),fv'')
end
| simplify(MINUS(a,b),fv) =
let
val (a',fv') = simplify(a,fv)
val (b',fv'') = simplify(b,fv')
in
if isConstant(a') andalso isConstant(b') then (sbt(a',b'),fv'')
else (MINUS(a',b'),fv'')
end
| simplify(TIMES(a,b),fv) =
let
val (a',fv') = simplify(a,fv)
val (b',fv'') = simplify(b,fv')
in
if isConstant(a') andalso isConstant(b') then (mul(a',b'),fv'')
else (TIMES(a',b'),fv'')
end
| simplify(DIVIDES(a,b),fv) =
let
val (a',fv') = simplify(a,fv)
val (b',fv'') = simplify(b,fv')
in
if isConstant(a') andalso isConstant(b') then (dvd(a',b'),fv'')
else (DIVIDES(a',b'),fv'')
end
| simplify(p as POINTER(_,_,_,_,ref(RANGE(lt,lte,gte,gt))),fv) =
if sameObjects(lt,gt) andalso lte andalso gte
then (lt,fv) else (p,p::fv)
| simplify(p as POINTER (_,_,_,_,v),fv) =
let
val (v',fv') = simplify(!v,fv)
in
if isConstant(v') then (v:=v'; (v',fv')) else
if member(p,fv) then (p,fv) else (p,p::fv)
end
| simplify x = x
fun rewrite(lhs,rhs) =
let
fun rew(VARIABLE(x,n),const) = (VARIABLE(x,n),const)
| rew(p as POINTER _,const) = (p,const)
| rew(p as PARAMETER _, const) = (p,const)
| rew(PLUS(a,b),const) =
if isConstant(a) then rew(b,sbt(const,a))
else rew(a,sbt(const,b))
| rew(MINUS(a,b),const) =
if isConstant(a) then rew(b,sbt(a,const))
else rew(a,add(b,const))
| rew(TIMES(a,b),const) =
if isConstant(a) then rew(b,dvd(const,a))
else rew(a,dvd(const,b))
| rew(DIVIDES(a,b),const) =
if isConstant(a) then rew(b,dvd(a,const))
else rew(a,mul(b,const))
| rew(_,_) = raise Fail "Impossible case 200"
in
case (isConstant(lhs),isConstant(rhs)) of
(true,true) => (false,(lhs,rhs))
| (true,false) => (true,rew(rhs,lhs))
| (false,true) => (false,rew(lhs,rhs))
| _ => raise ArithError
end
fun evalPrimitive(eq(o1,o2),cntxt) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
if not(isNil(sieve(fv))) then maybe(waitFixed(fv,eq(l,r)))
else EQ(l,r)
end
| evalPrimitive(greater(oreq,o1,o2),cntxt) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
if not(isNil(sieve(fv))) then maybe(waitFixed(fv,greater(oreq,l,r)))
else if oreq then GTE(l,r) else GT(l,r)
end
| evalPrimitive(less(oreq,o1,o2),cntxt) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
if not(isNil(sieve(fv))) then maybe(waitFixed(fv,less(oreq,l,r)))
else if oreq then LTE(l,r) else LT(l,r)
end
| evalPrimitive _ = raise ArithError
fun count nil = 0
| count (a::b) = 1+count(b)
fun coerce(var as VARIABLE(x,xn),value,cntxt,trace) =
let val (n,v)= (fn (POINTER(_,_,_,n,v)) => (n,v)
| _ => raise Fail "Impossible case 201")
(makePtr(var,cntxt))
val v' = stripPtr(value)
in case evalPrimitive(eq(var,v'),cntxt)
of yes => (Dynamics.setVal(v,value,trace,cntxt);
awake(!n); yes)
| maybe _ => (Dynamics.setVal(v,value,trace,cntxt);
awake(!n); yes)
| no => no
end
| coerce(p as POINTER(_,_,_,n,v),value,cntxt,trace) =
let val v' = stripPtr(value)
in
(case evalPrimitive(eq(p,v'),cntxt)
of yes => (Dynamics.setVal(v,value,trace,cntxt); awake(!n); yes)
| maybe _ => (Dynamics.setVal(v,value,trace,cntxt);
awake(!n); yes)
| no => no
)
end
| coerce _ = yes
fun coerceGT(b,l,r,cntxt,trace) =
case evalPrimitive(greater(b,l,r),cntxt)
of no => no
| _
=> (let
val (v,n) = case l of VARIABLE(_,xn) =>
(valOf(cntxt,xn),
suspensionNumber(cntxt,xn))
| POINTER(_,_,_,n,p) => (p,n)
| _ => raise ArithError
in case (!v,r)
of (RANGE(lt,lte,gte,gt),x) =>
if b andalso gte andalso isYes(EQ(gt,x))
then (Dynamics.setVal(v,gt,trace,cntxt);
awake(!n); yes)
else if isYes(LT(lt,x)) then
(Dynamics.setVal(v,RANGE(x,b,gte,gt),trace,cntxt);
awake(!n); yes)
else if isYes(EQ(lt,x)) then
(Dynamics.setVal(v,RANGE(x,lte andalso b,gte,gt),
trace,cntxt);
awake(!n); yes)
else yes
| (_,INFINITY) => (Dynamics.setVal(v,INFINITY,trace,cntxt);
awake(!n); yes)
| (_,MINFINITY) => yes
| (_,NUMBER y) => (Dynamics.setVal(v,RANGE(NUMBER y,b,false,
INFINITY),trace,cntxt);
awake(!n);yes)
| _ => raise ArithError
end)
fun coerceLT(b,l,r,cntxt,trace) =
case evalPrimitive(less(b,l,r),cntxt)
of no => no
| _
=> (let
val (v,n) = case l of VARIABLE(_,n) =>
(valOf(cntxt,n),
suspensionNumber(cntxt,n))
| POINTER(_,_,_,n,p) => (p,n)
| _ => raise ArithError
in case (!v,r)
of (RANGE(lt,lte,gte,gt),x) =>
if b andalso lte andalso isYes(EQ(lt,x))
then (Dynamics.setVal(v,lt,trace,cntxt);
awake(!n);yes)
else if isYes(GT(gt,x)) then
(Dynamics.setVal(v,RANGE(lt,lte,b,x),trace,cntxt);
awake(!n);yes)
else if isYes(EQ(gt,x)) then
(Dynamics.setVal(v,RANGE(lt,lte,b andalso gte,x),
trace,cntxt);
awake(!n); yes)
else yes
| (_,INFINITY) => yes
| (_,MINFINITY) => (Dynamics.setVal(v,MINFINITY,trace,cntxt);
awake(!n); yes)
| (_,NUMBER y) => (Dynamics.setVal(v,RANGE(MINFINITY,false,b,
NUMBER y),
trace,cntxt);
awake(!n);yes)
| _ => raise ArithError
end)
fun publish(eq(o1,o2),cntxt,trace) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
case count(fv)
of 0 => evalPrimitive(eq(l,r),cntxt)
| 1 => let val (_,(l',r')) = rewrite(l,r)
in coerce(l',r',cntxt,trace)
end
| _ => maybe(allButOneFixed(fv,eq(l,r)))
end
| publish(greater(b,o1,o2),cntxt,trace) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
case count(fv)
of 0 => evalPrimitive(greater(b,l,r),cntxt)
| 1 => (case rewrite(l,r)
of (false,(l',r')) => coerceGT(b,l',r',cntxt,trace)
| (true,(l',r')) => coerceLT(b,l',r',cntxt,trace)
)
| _ => maybe(allButOneFixed(fv,greater(b,l,r)))
end
| publish(less(b,o1,o2),cntxt,trace) =
let
val (l,fv') = simplify(instantiate(o1,cntxt),[])
val (r,fv) = simplify(instantiate(o2,cntxt),fv')
in
case count(fv)
of 0 => evalPrimitive(less(b,l,r),cntxt)
| 1 => (case rewrite(l,r)
of (false,(l',r')) => coerceLT(b,l',r',cntxt,trace)
| (true,(l',r')) => coerceGT(b,l',r',cntxt,trace)
)
| _ => maybe(allButOneFixed(fv,less(b,l,r)))
end
| publish _ = raise ArithError
end
;
