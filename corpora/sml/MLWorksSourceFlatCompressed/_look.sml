require "sigs";
require "_utils";
require "$.basis.__list";
require "$.basis.__array";
functor mkLook (structure IntGrammar : INTGRAMMAR) : LOOK =
struct
open Array List
infix 9 sub
structure Grammar = IntGrammar.Grammar
structure IntGrammar = IntGrammar
open Grammar IntGrammar
structure TermSet = ListOrdSet
(struct
type elem = term
val eq = eqTerm
val gt = gtTerm
end)
val union = TermSet.union
val make_set = TermSet.make_set
val prLook = fn (termToString,print) =>
let val printTerm = print o termToString
fun f nil = print " "
| f (a :: b) = (ignore(printTerm a);
ignore(print " ");
f b)
in f
end
structure NontermSet = ListOrdSet
(struct
type elem = nonterm
val eq = eqNonterm
val gt = gtNonterm
end)
val mkFuncs = fn {rules : rule list, nonterms : int,
produces : nonterm -> rule list} =>
let
val nullable =
let fun ok_rhs nil = true
| ok_rhs ((TERM _)::_) = false
| ok_rhs ((NONTERM i)::r) = ok_rhs r
fun add_rule (RULE {lhs,rhs,...},r) =
if ok_rhs rhs then
(lhs,map (fn (NONTERM (NT i)) => i
| _ => raise General.Fail "Impossible") rhs)::r
else r
val items = List.foldr add_rule [] rules
val nullable = array(nonterms,false)
val f = fn ((NT i,nil),(l,_)) => (update(nullable,i,true);
(l,true))
| (a as (lhs,(h::t)),(l,change)) =>
case (nullable sub h)
of false => (a::l,change)
| true => ((lhs,t)::l,true)
fun prove(l,true) = prove(List.foldr f (nil,false) l)
| prove(_,false) = ()
in (prove(items,true); fn (NT i) => nullable sub i)
end
fun scanRhs addSymbol =
let fun f (nil,result) = result
| f ((sym as NONTERM nt) :: rest,result) =
if nullable nt then f (rest,addSymbol(sym,result))
else addSymbol(sym,result)
| f ((sym as TERM _) :: _,result) = addSymbol(sym,result)
in f
end
fun accumulate(rules, empty, addObj) =
List.foldr (fn (RULE {rhs,...},r) =>(scanRhs addObj) (rhs,r)) empty rules
val nontermMemo = fn f =>
let val lookup = array(nonterms,nil)
fun g i = if i=nonterms then ()
else (update(lookup,i,f (NT i)); g (i+1))
in (g 0; fn (NT j) => lookup sub j)
end
fun first1 nt = accumulate(produces nt, TermSet.empty,
fn (TERM t, set) => TermSet.insert (t,set)
| (_, set) => set)
val first1 = nontermMemo(first1)
fun starters1 nt = accumulate(produces nt, nil,
fn (NONTERM nt, set) =>
NontermSet.insert(nt,set)
| (_, set) => set)
val starters1 = nontermMemo(starters1)
fun first nt =
List.foldr (fn (a,r) => TermSet.union(r,first1 a))
[] (NontermSet.closure (NontermSet.singleton nt, starters1))
val first = nontermMemo(first)
fun prefix symbols =
scanRhs (fn (TERM t,r) => TermSet.insert(t,r)
| (NONTERM nt,r) => TermSet.union(first nt,r))
(symbols,nil)
fun nullable_string ((TERM t) :: r) = false
| nullable_string ((NONTERM nt) :: r) =
(case (nullable nt)
of true => nullable_string r
| f => f)
| nullable_string nil = true
in {nullable = nullable, first = prefix}
end
end;
