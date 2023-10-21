signature TWIG_SPECIFICATION =
sig
type cost and tree and result
eqtype rule and symbol
val get_subtrees : tree -> tree list
val node_value : tree -> symbol
val cost_less : cost * cost -> bool
datatype skeletal = Skeleton of rule * cost * tree * skeletal list
exception MatchAbort and InternalError of string
val execute_cost : rule * tree * skeletal list -> cost
val execute : skeletal -> result
val getreplacement : result -> tree
val rewriterule : rule -> bool
val matches : rule -> int
eqtype state
datatype matchtree = Chain of rule * symbol * matchtree list
val unitmatches : symbol -> matchtree list
val childsymbol : int -> symbol
val initialstate : state
val go : state * symbol -> state
val go_f : state * symbol -> (int * rule * symbol) list
end;
functor MAKEtreeprocessor ( Specification : TWIG_SPECIFICATION) =
struct
structure Spec : TWIG_SPECIFICATION = Specification
open Spec
exception NoCover
fun internal s = raise InternalError ("FATAL:"^s)
structure Representation :
sig
type 's table
structure Spec : TWIG_SPECIFICATION
val empty_table : unit -> 's table
val new_level : 's table -> 's table
val contribute0 : 's table * int * Spec.rule * Spec.symbol -> 's table
val contribute1 : 's table * int * Spec.rule * Spec.symbol * 's -> 's table
val get_level : 's table -> (Spec.symbol * (Spec.rule * int * 's list) list) list * 's table
end
=
struct
structure Spec = Spec
open Spec
type 's table = (symbol * (rule * int * 's list) list) list list
fun empty_table () = []
fun new_level l = []::l
fun insert0' (r:rule,nil) = [(r,1,[])]
| insert0' (r:rule,(h as (r',m',s'))::hs) =
if r' <> r
then h :: insert0' (r,hs)
else (r,m'+1,s')::hs
fun insert0 (nil,r,t:symbol) = [(t,[(r,1,[])])]
| insert0 ((h as (t',a))::hs,r,t) =
if t' <> t
then h :: insert0 (hs,r,t)
else (t,insert0' (r,a))::hs
fun insert1' (r:rule,s,nil) = [(r,1,[s])]
| insert1' (r,s,(h as (r',m',s'))::hs) =
if r' <> r
then h :: insert1' (r,s,hs)
else (r,m'+1,s::s')::hs
fun insert1 (nil,r,t:symbol,s) = [(t,[(r,1,[s])])]
| insert1 ((h as (t',a))::hs,r,t,s) =
if t' <> t
then h :: insert1 (hs,r,t,s)
else (t,insert1' (r,s,a)):: hs
fun contribute0 (a::l,1,r,t) = insert0 (a,r,t) :: l
| contribute0 (a::l,n,r,t) = a::contribute0(l,n-1,r,t)
| contribute0 _ = internal "run out of levels"
fun contribute1 (a::l,1,r,t,s) = insert1(a,r,t,s) :: l
| contribute1 (a::l,n,r,t,s) = a::contribute1(l,n-1,r,t,s)
| contribute1 _ = internal "run out of levels"
fun get_level (a::l) = (a,l)
| get_level _ = internal "run out of levels"
end
open Representation
val accum = revfold
fun cost (Skeleton(_,c,_,_)) = c
fun insert (i:symbol, s, nil) = [(i,s)]
| insert (i, s, (head as (i',s'))::rest) =
if i = i'
then
if cost_less (cost s,cost s')
then (i,s)::rest
else head::rest
else head :: (insert (i,s,rest))
fun build_skeleton (ar as (r,t,cs)) = Skeleton (r,execute_cost ar,t,rev cs)
fun get_closure (ct,ss,t,ac) =
accum (fn (Chain(r,n,cs),ac') =>
let val skel = build_skeleton(r,t,ss)
in
get_closure (cs,[skel],t,insert (n,skel,ac'))
end handle MatchAbort => ac')
ct ac
fun someone (t,still_best, nil) = [still_best]
| someone (t,still_best, (r,m,cs)::rest) =
if matches r = m
then
let val skel = build_skeleton (r,t,cs)
in someone (t,if cost_less (cost skel,cost still_best)
then skel
else still_best,rest)
end handle MatchAbort => someone (t,still_best,rest)
else someone(t,still_best,rest)
fun still_no_one (t,nil) = nil
| still_no_one (t,(r,m,cs)::rest) =
if matches r = m
then someone (t,build_skeleton (r,t,cs),rest)
handle MatchAbort => still_no_one (t,rest)
else still_no_one (t,rest)
fun leave_best_alone (t,nil) = internal "matcher state inconsistent. lba."
| leave_best_alone (t,l) = still_no_one (t,l)
fun skeletons_of (state,node,tab) =
let val (t,s) =
case get_subtrees node of
nil =>
let val tab' =
accum (fn ((h,r,n),t) => contribute0 (t,h-1,r,n))
(go_f(state, node_value node)) tab
in
(tab', get_closure ((unitmatches o node_value) node,[],node,[]))
end
| ls =>
let val state' = go (state, node_value node)
val (table, _) =
accum (fn (l,(t,i)) =>
let val state'' = go (state',childsymbol i)
val (t', ss) = skeletons_of (state'',l,t)
in (accum (fn ((r,s),t'') =>
let val finals = go_f (state'',r)
in
accum
(fn ((h,r,n),t''') =>
contribute1 (t''',h-1,r,n,s))
finals t''
end) ss t',i+1)
end)
ls
(new_level tab, 1)
val (toplevel, table') = get_level table
in
(table',
let val unclosurized = accum (fn ((_,nil),l) => l | ((n,[e]),l) => (n,e)::l | _ => internal "inconsistency. 01l")
(map (fn (n,sl) => (n,leave_best_alone (node,sl))) toplevel)
nil
in
accum (fn ((n,s),al) =>
get_closure (unitmatches n,[s],node,al)) unclosurized unclosurized
end)
end
in
case s of
[] => (t,[])
| [(_,S as Skeleton(r,_,_,_))] =>
if rewriterule r
then skeletons_of(state,(getreplacement o execute) S,tab)
else (t,s)
| (_,sk)::rest =>
let val best as Skeleton (r,_,_,_) =
accum (fn ((n,s),bs) =>
if cost_less (cost s,cost bs) then s else bs) rest sk
in
if rewriterule r
then skeletons_of(state,
(getreplacement o execute) best,
tab)
else (t,s)
end
end
fun translate t = execute
(case (skeletons_of (initialstate,t,empty_table())) of
(_,(_,s)::t) =>
accum (fn ((n,s),bs) =>
if cost_less (cost s,cost bs) then s else bs) t s
| (_,nil) => raise NoCover)
end;
