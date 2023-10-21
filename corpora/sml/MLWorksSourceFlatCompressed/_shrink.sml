require "sigs";
require "^.lib.base";
require "$.basis.__list";
require "$.basis.__array";
signature SORT_ARG =
sig
type entry
val gt : entry * entry -> bool
end
signature SORT =
sig
type entry
val sort : entry list -> entry list
end
signature EQUIV_ARG =
sig
type entry
val gt : entry * entry -> bool
val eq : entry * entry -> bool
end
signature EQUIV =
sig
type entry
val equivalences : entry list -> (int * int list * entry list)
end
functor MergeSortFun(A : SORT_ARG) : SORT =
struct
type entry = A.entry
fun sort nil = nil
| sort l =
let
fun merge (l as a::at,r as b::bt) =
if A.gt(a,b)
then b :: merge(l,bt)
else a :: merge(at,r)
| merge (l,nil) = l
| merge (nil,r) = r
fun scan (a :: b :: rest) = merge(a,b) :: scan rest
| scan l = l
fun loop (a :: nil) = a
| loop l = loop (scan l)
in loop (map (fn a => [a]) l)
end
end
functor EquivFun(A : EQUIV_ARG) : EQUIV =
struct
open Array List
infix 9 sub
type entry = A.entry
val gt = fn ((a,_),(b,_)) => A.gt(a,b)
structure Sort = MergeSortFun(type entry = A.entry * int
val gt = gt)
val assignIndex =
fn l =>
let fun loop (index,nil) = nil
| loop (index,h :: t) = (h,index) :: loop(index+1,t)
in loop (0,l)
end
val createEquivalences =
let fun loop ((e,_) :: t, prev, class, R , SE) =
if A.eq(e,prev)
then loop(t,e,class,R, class :: SE)
else loop(t,e,class+1,e :: R, (class + 1) :: SE)
| loop (nil,_,_,R,SE) = (rev R, rev SE)
in fn nil => (nil,nil)
| (e,_) :: t => loop(t, e, 0, [e],[0])
end
val inversePermute = fn permutation =>
fn nil => nil
| l as h :: _ =>
let val result = array(length l,h)
fun loop (elem :: r, dest :: s) =
(update(result,dest,elem); loop(r,s))
| loop _ = ()
fun listofarray i =
if i < Array.length result then
(result sub i) :: listofarray (i+1)
else nil
in loop (l,permutation); listofarray 0
end
val makePermutation = map (fn (_,b) => b)
val equivalences = fn l =>
let val EP = assignIndex l
val sorted = Sort.sort EP
val P = makePermutation sorted
val (R, SE) = createEquivalences sorted
in (length R, inversePermute P SE, R)
end
end
functor ShrinkLrTableFun(structure LrTable : LR_TABLE) : SHRINK_LR_TABLE =
struct
structure LrTable = LrTable
open LrTable
val gtAction = fn (a,b) =>
case a
of SHIFT (STATE s) =>
(case b of SHIFT (STATE s') => s>s' | _ => true)
| REDUCE i => (case b of SHIFT _ => false | REDUCE i' => i>i'
| _ => true)
| ACCEPT => (case b of ERROR => true | _ => false)
| ERROR => false
structure ActionEntryList =
struct
type entry = (term,action) pairlist * action
val rec eqlist =
fn (EMPTY,EMPTY) => true
| (PAIR (T t,d,r),PAIR(T t',d',r')) =>
t=t' andalso d=d' andalso eqlist(r,r')
| _ => false
val rec gtlist =
fn (PAIR _,EMPTY) => true
| (PAIR(T t,d,r),PAIR(T t',d',r')) =>
t>t' orelse (t=t' andalso
(gtAction(d,d') orelse
(d=d' andalso gtlist(r,r'))))
| _ => false
val eq = fn ((l,a),(l',a')) => a=a' andalso eqlist(l,l')
val gt = fn ((l,a),(l',a')) => gtAction(a,a')
orelse (a=a' andalso gtlist(l,l'))
end
structure EquivActionList = EquivFun(ActionEntryList)
val states = fn max =>
let fun f i=if i<max then STATE i :: f(i+1) else nil
in f 0
end
val length : ('a,'b) pairlist -> int =
fn l =>
let fun g(EMPTY,len) = len
| g(PAIR(_,_,r),len) = g(r,len+1)
in g(l,0)
end
val size : (('a,'b) pairlist * 'c) list -> int =
fn l =>
let val c = ref 0
in (app (fn (row,_) => c := !c + length row) l; !c)
end
val shrinkActionList =
fn (table,verbose) =>
case EquivActionList.equivalences
(map (describeActions table) (states (numStates table)))
of result as (_,_,l) => (result,if verbose then size l else 0)
end;
