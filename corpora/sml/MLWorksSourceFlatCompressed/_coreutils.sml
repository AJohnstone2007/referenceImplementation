require "sigs";
require "_utils";
require "$.basis.__list";
require "$.basis.__array";
functor mkCoreUtils(structure Core : CORE) : CORE_UTILS =
struct
open Array List
infix 9 sub
val DEBUG = true
structure Core = Core
structure IntGrammar = Core.IntGrammar
structure Grammar = IntGrammar.Grammar
open Grammar IntGrammar Core
structure Assoc = SymbolAssoc
structure NtList = ListOrdSet
(struct
type elem = nonterm
val eq = eqNonterm
val gt = gtNonterm
end)
val mkFuncs = fn (GRAMMAR {rules,terms,nonterms,...}) =>
let val derives=array(nonterms,nil : rule list)
val _ =
let val f = fn {lhs=lhs as (NT n), rhs, precedence,rulenum} =>
let val rule=RULE{lhs=lhs,rhs=rhs,precedence=precedence,
rulenum=rulenum,num=0}
in update(derives,n,rule::(derives sub n))
end
in app f rules
end
val _ =
let val f =
fn (RULE{lhs,rhs,precedence,rulenum,num}, (l,i)) =>
(RULE{lhs=lhs,rhs=rhs, precedence=precedence,
rulenum=rulenum, num=i}::l,i+1)
fun g(i,num) =
if i<nonterms then
let val (l,n) =
List.foldr f ([], num) (derives sub i)
in update(derives,i,rev l); g(i+1,n)
end
else ()
in g(0,0)
end
val rules =
let fun g i =
if i < nonterms then (derives sub i) @ (g (i+1))
else nil
in g 0
end
val produces = fn (NT n) =>
if DEBUG andalso (n<0 orelse n>=nonterms) then
let exception Produces of int in raise (Produces n) end
else derives sub n
val memoize = fn f =>
let fun loop i = if i = nonterms then nil
else f (NT i) :: (loop (i+1))
val data = Array.fromList(loop 0)
in fn (NT i) => data sub i
end
val nontermClosure =
let val collectNonterms = fn n =>
List.foldr (fn (r,l) =>
case r
of RULE {rhs=NONTERM n :: _,...} =>
NtList.insert(n,l)
| _ => l) NtList.empty (produces n)
val closureNonterm = fn n =>
NtList.closure(NtList.singleton n,
collectNonterms)
in memoize closureNonterm
end
fun sortItems nt =
let fun add_item (a as RULE{rhs=symbol::rest,...},r) =
let val item = ITEM{rule=a,dot=1,rhsAfter=rest}
in Assoc.insert((symbol,case Assoc.find (symbol,r)
of SOME l => item::l
| NONE => [item]),r)
end
| add_item (_,r) = r
in List.foldr add_item Assoc.empty (produces nt)
end
val ntShifts = memoize sortItems
fun getNonterms l =
List.foldr (fn (ITEM {rhsAfter=NONTERM sym ::_, ...},r) =>
NtList.insert(sym,r)
| (_,r) => r) [] l
fun closureNonterms a =
let val nonterms = getNonterms a
in List.foldr (fn (nt,r) =>
NtList.union(nontermClosure nt,r))
nonterms nonterms
end
fun shifts (CORE (itemList,_)) =
let
fun mergeShiftItems (args as ((k,l),r)) =
case Assoc.find(k,r)
of NONE => Assoc.insert args
| SOME old => Assoc.insert ((k,l@old),r)
fun mergeItems (n,r) =
Assoc.fold mergeShiftItems (ntShifts n) r
val nonterms = closureNonterms itemList
val newsets = List.foldr mergeItems Assoc.empty nonterms
fun insertItem ((k,i),r) =
case (Assoc.find(k,r))
of NONE => Assoc.insert((k,[i]),r)
| SOME l => Assoc.insert((k,Core.insert(i,l)),r)
fun shiftCores(ITEM{rule,dot,rhsAfter=symbol::rest},r) =
insertItem((symbol,
ITEM{rule=rule,dot=dot+1,rhsAfter=rest}),r)
| shiftCores(_,r) = r
val newsets = List.foldr shiftCores newsets itemList
in Assoc.make_list newsets
end
val nontermEpsProds =
let val f = fn nt =>
List.foldr
(fn (rule as RULE {rhs=nil,...},results) => rule :: results
| (_,results) => results)
[] (produces nt)
in memoize f
end
fun epsProds (CORE (itemList,state)) =
let val prods = map nontermEpsProds (closureNonterms itemList)
in List.concat prods
end
in {produces=produces,shifts=shifts,rules=rules,epsProds=epsProds}
end
end;
