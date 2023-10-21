functor AC_MatchFUN (structure T : TERM
structure S : SUBSTITUTION
structure A : AC_TOOLS
sharing type T.Term = S.Term = A.Term
and type T.OpId = T.Sig.O.OpId = A.OpId
and type S.Variable = T.Sig.V.Variable = T.Variable = A.Variable
and type T.Sort = T.Sig.S.Sort = T.Sig.V.Sort = T.Sig.O.Sort
and type S.Signature = T.Sig.Signature = A.Signature
) : MATCH =
struct
type Signature = T.Sig.Signature
type Term = T.Term
type Substitution = S.Substitution
open S T T.Sig.O T.Sig.V A
val sort_ordered_reflexive = T.Sig.S.sort_ordered_reflexive
val get_sort_ordering = T.Sig.get_sort_ordering
val get_operators = T.Sig.get_operators
fun decompose Sigma =
let
fun in_subst S = element VarEq (domain_of_sub S)
val fail = ([],FailSub)
val of_sort = T.of_sort Sigma o (T.Sig.V.variable_sort)
val is_eq_op = ou (C_Operator (get_operators Sigma))
(AC_Operator (get_operators Sigma))
val eq_AC = AC_equivalent Sigma
fun Msimpl S [] = ([],S)
| Msimpl S ((s,t)::U) =
if variable s
then let val v = get_Variable s
in if in_subst S v
then if eq_AC (applysubtoterm S s) t
then Msimpl S U
else fail
else if of_sort v t
then Msimpl (addsub S (v,t)) U
else fail
end
else if variable t
then fail
else if same_root s t
then if is_eq_op (root_operator s)
then apply_fst (cons (s,t)) (Msimpl S U)
else Msimpl S (U @ zip (subterms s,subterms t))
else fail
in
Msimpl
end ;
fun casums c cc [] [] n = []
| casums c cc [a] [ac] _ =
if c rem a = 0
then case ac of
Match sya => if c = a
then case cc of
Match syc => if OpIdeq sya syc
then [[1]]
else []
| NoMatch => []
else []
| NoMatch => [[c div a]]
else []
| casums c cc (a::ais) (ac::acs) n =
let val a' = n*a
in if a' > c
then []
else case ac of
Match sya => if n=1
then (case cc of
Match syc => if OpIdeq sya syc
then if a' = c
then [1::copy (length ais) 0]
else map (cons 1)
(casums (c-a') cc ais acs 0)
else []
| NoMatch => [])
else
if n = 0
then map (cons 0) (casums c cc ais acs 0)
@
casums c cc (a :: ais) (ac::acs) 1
else []
| NoMatch => if a' = c
then [n:: copy (length ais) 0 ]
else map (cons n) (casums (c-a') cc ais acs 0)
@
casums c cc (a :: ais) (ac::acs) (n+1)
end
| casums _ _ _ _ _ = failwith "Constraint Mismatch in AC Matching"
local
fun count_and_remove a (b::l) =
let val (nas,l') = count_and_remove a l
in if TermEq a b
then (nas+1, l')
else (nas, b :: l')
end
| count_and_remove a [] = (0,[])
in
fun occurence_lists (a::l) =
let val (nas, l') = count_and_remove a l
val (ais,nais,sys) = occurence_lists l'
in (a::ais,1+nas::nais,
((Match(root_operator a))
handle Ill_Formed_Term _ => NoMatch)::sys)
end
| occurence_lists [] = ([],[],[])
end
fun mk_subsets (ss1::sss) =
mapapp (fn l => map (R l o C cons) (mk_subsets sss)) ss1
| mk_subsets [] = [[]]
local
fun add_hds ((a::_)::r) 0 = add_hds r a
| add_hds ((0::_)::r) 1 = add_hds r 1
| add_hds ((a::_)::r) 1 = false
| add_hds _ s = s=1
in
fun check_cons (ca::racs) sols =
(case ca of
NoMatch =>
exists ((neq 0) o hd) sols
andalso
check_cons racs (map tl sols)
| Match sy =>
add_hds sols 0
andalso
check_cons racs (map tl sols)
)
| check_cons [] sols = true
end
fun mk_solution f sis (t::tis) sols =
(t,AC_unflatten f (mapapp2 (copy o hd) sols sis)) :: mk_solution f sis tis (map tl sols)
| mk_solution _ sis [] sols = []
fun AC_Match Sigma T1 T2 =
let
val Ts1 = AC_subterms T1
val Ts2 = AC_subterms T2
in
if length Ts2 < length Ts1
then []
else
let
val (subts_1, ais, asys) = occurence_lists (bag_difference TermEq Ts1 Ts2)
val (subts_2, bis, bsys) = occurence_lists (bag_difference TermEq Ts2 Ts1)
val Basis = map2 (fn (p,pc) => casums p pc ais asys 0) bis bsys
val subsets = mk_subsets Basis
in mapfilter (check_cons asys)
(mk_solution (root_operator T1) subts_2 subts_1) subsets
end
end
fun OSAC_match Sigma T1 T2 =
let
val opers = get_operators Sigma
fun clear_app Us V = mapfilter (non isfail o snd) (apply_fst (C append V)) Us
val Msimpl = decompose Sigma
fun mutate ((t,t'),s) =
map (Msimpl s)
(if C_Operator opers (root_operator t)
then Cmutate Sigma t t'
else
if AC_Operator opers (root_operator t)
then AC_Match Sigma t t'
else []
)
fun merge ((t,t')::V, s) = clear_app (mutate ((t,t'),s)) V
| merge ([], s) = [([], s)]
fun matcher (U,s) =
if null U
then [s]
else mapapp matcher (merge (U,s))
in
matcher (Msimpl EMPTY [(T1,T2)])
end
fun all_matches s t1 = filter (not o isfail) o OSAC_match s t1
fun match Sigma T1 T2 =
let
val opers = get_operators Sigma
val Msimpl = decompose Sigma
fun mutate ((t,t')::V,s) =
let val f= root_operator t
val acs = (if C_Operator opers f
then Cmutate Sigma t t'
else
if AC_Operator opers f
then AC_Match Sigma t t'
else [])
in if null V
then let fun filtmap (t1s::ts) acc =
let val (U,s') = Msimpl s t1s
in if isfail s' then filtmap ts acc
else if null U
then [([],s')]
else filtmap ts ((U,s) :: acc)
end
| filtmap [] acc = acc
in filtmap acs []
end
else
filtermap (isfail o snd) (Msimpl s) acs
end
| mutate ([], s) = [([], s)]
fun matcher (U,s) =
if null U
then [s]
else mapapp matcher (mutate (U,s))
in
matcher (Msimpl EMPTY [(T1,T2)])
end
fun match s t1 t2 =
hd (OSAC_match s t1 t2)
handle Hd => FailSub
end
;
