functor CompletionToolsFUN (structure T : TERM
structure S : SUBSTITUTION
structure Eq : EQUALITY
structure Es : EQUALITYSET
structure iEq : I_EQUALITY
structure Str : STRATEGY
structure M : ETOOLS
structure R : REWRITE
sharing type Eq.Equality = Es.Equality = iEq.Equality =
Str.Equality = R.Equality
and type Es.EqualitySet = iEq.EqualitySet = Str.EqualitySet =
R.EqualitySet
and type Eq.Term = T.Term = Es.Term = S.Term = iEq.Term =
M.Term = R.Term
and type T.Sig.Signature = iEq.Signature =
Eq.Signature = Es.Signature = S.Signature =
M.Signature = R.Signature = Str.Signature
and type T.Sig.S.Sort = T.Sig.O.Sort = T.Sig.V.Sort = T.Sort
and type T.Sig.V.Variable = T.Variable = S.Variable
and type T.Sig.O.OpId = T.OpId
and type S.Substitution = M.Substitution
sharing T.Sig.O.Pretty = T.Pretty = Eq.Pretty = Es.Pretty
) : COMPLETIONTOOLS =
struct
structure Pretty = T.Pretty
type Signature = T.Sig.Signature
type Term = T.Term
type EqualitySet = Es.EqualitySet
type Equality = Eq.Equality
structure Sort = T.Sig.S
structure Ops = T.Sig.O
structure Vars = T.Sig.V
open T T.Sig S Es Eq iEq Str R
fun equality A = relate (M.equality A)
local
infix <<
val NewOpNumber = ref 0 : int ref
fun arg_form [] = [""]
| arg_form [a] = ["_"]
| arg_form (a::l) = "" :: "," :: arg_form l
fun get_new_op_name l =
(inc NewOpNumber ;
if length l = 0 then (["f$"^makestring (!NewOpNumber)], K (Pretty.str ("f$"^makestring (!NewOpNumber))))
else (["f$"^makestring (!NewOpNumber),"("] @ arg_form l @ [")"] ,
fn ps => Pretty.blo (4, Pretty.str ("f$"^makestring (!NewOpNumber)^"(") ::
interleave3 ps (copy (length l - 1) (Pretty.str ","))
(copy (length l - 1) (Pretty.brk 1))
@[Pretty.str ")"]
)
)
)
fun add_new_op Sigma T (l,r) vs (ss,s) =
let val (form,pform) = get_new_op_name vs
val ins = insert_by_strat Sigma by_age_strat
val newops = Ops.insert_op_form (get_operators Sigma) (Ops.mk_form form) pform (Ops.mk_OpSigSet (ss,s))
in
case Ops.find_operator newops (Ops.mk_form form) of
OK sy => (let val new_rhs = mk_OpTerm sy (map mk_VarTerm vs)
in ins (ins EmptyEqSet (mk_equality l new_rhs))
(mk_equality r new_rhs)
end ,
change_operators Sigma newops,
TranSys.build_trans_system (mk_OpTerm sy) form T)
| Error m => (error_message
"Something seriously wrong with Operator insertion and retrieval: Catastrophic Failure " ; raise Fail)
end
in
fun split Sigma T e =
let val (l,r) = terms e
val common_vars = intersection Vars.VarEq (vars_of_term l) (vars_of_term r)
val arg_sorts = map Vars.variable_sort common_vars
val sl = least_sort Sigma l handle (Least_Sort _) => Sort.Top
val sr = least_sort Sigma r handle (Least_Sort _) => Sort.Top
val so = get_sort_ordering Sigma
val op<< = Sort.sort_ordered_reflexive so
in if sl << sr
then add_new_op Sigma T (l,r) common_vars (arg_sorts,sl)
else if sr << sl
then add_new_op Sigma T (l,r) common_vars (arg_sorts,sr)
else let val maxes = Sort.maximal_sorts so (intersection Sort.SortEq
(Sort.subsorts so sl)
(Sort.subsorts so sr))
in if length maxes = 1
then add_new_op Sigma T (l,r) common_vars (arg_sorts,hd maxes)
else (error_message "no unique sort for the result type of new operator - abort";
(EmptyEqSet , Sigma , T)
)
end
end
end
;
fun subsumes A e1 e2 =
let val msub = (Statistics.inc_match_attempts () ;
M.match A (lhs e1) (lhs e2) )
in not (isfail msub)
orelse
(Statistics.inc_match_success () ;
TermEq (applysubtoterm msub (rhs e1)) (rhs e2) )
end
val finish = "Do you wish to finish"
val ident = "Conjecture Solved by Reducing to Identity."
val subsum = "Conjecture Solved by Subsumption."
fun consider_conjectures A E Rs H =
let val ins = eqinsert (fn _ => fn _ => LT)
fun is_subsumed c E =
if empty_equality_set E
then false
else if subsumes A (select_eq E) c
then true
else is_subsumed c (rest_eq E)
fun conj_solved C new_conj s rcs=
(print_line () ;
write_terminal (s^"\n") ;
printequality A "Conjecture >>  " new_conj ;
if confirm finish
then (true , ins C (protect new_conj))
else let val (b,C') = cons_conj rcs
in (b,ins C' (protect new_conj))
end
)
and cons_conj C =
if empty_equality_set C
then (false,C)
else let val (c1,rcs) = (select_eq C, rest_eq C)
in if protected c1
then let val (b,C') = cons_conj rcs
in (b,ins C' c1)
end
else
let val (l,r) = terms c1
val l' = normalise_by_sets A l Rs
val r' = normalise_by_sets A r Rs
val lb = not(M.equality A l l')
val rb = not(M.equality A r r')
val new_conj = mk_conjecture l' r'
in if lb orelse rb
then if equality A new_conj
then conj_solved C new_conj ident rcs
else if is_subsumed new_conj E
then conj_solved C new_conj subsum rcs
else let val (b,C') = cons_conj rcs
in (b,ins C' new_conj)
end
else if is_subsumed c1 E
then conj_solved C c1 subsum rcs
else let val (b,C') = cons_conj rcs
in (b,ins C' c1)
end
end
end
in
cons_conj H
end
fun how_many E = write_terminal ((eq_set_size E)^" "^(get_name E)^"\n")
fun display A E =
(write_terminal (title_line (get_name E)^"\n");
how_many E;
newline ();
display_equality_set A E)
fun stop step A Es H =
if step
then
(app (display A) Es;
if not (empty_equality_set H)
then display A H
else ();
print_line (); newline () ;
confirm "Do you wish to finish")
else (app how_many Es ; false)
fun get_number () =
givefM get_number (stringtoint (prompt_reply ""))
fun select E () =
givefM (select E)
(let val n = get_number ()
in select_by_number E n
propM
returnM o (C pair (delete_by_number E n))
end)
fun selectNext StratName E =
if StratName = "manual"
then (write_terminal ("Select by Number from "^get_label E^" : ");
select E ()
)
else (select_eq E, rest_eq E)
end
;
