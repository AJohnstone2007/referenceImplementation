functor OSUnifyFUN (structure T:TERM
structure S:SUBSTITUTION
sharing type S.Term = T.Term
and type T.OpId = T.Sig.O.OpId
and type S.Variable = T.Sig.V.Variable = T.Variable
and type T.Sort = T.Sig.S.Sort =
T.Sig.V.Sort = T.Sig.O.Sort
and type S.Signature = T.Sig.Signature
) : UNIFY =
struct
type Signature = T.Sig.Signature
type Term = T.Term
type Substitution = S.Substitution
type OpId = T.Sig.O.OpId
val OpIdEq = T.Sig.O.OpIdeq
val VarEq = T.Sig.V.VarEq
open S T
fun Decomposition t1 t2 = zip (subterms t1,subterms t2)
fun var_elim (v,t) = map (apply_both (applysubtoterm (addsub EMPTY (v,t))))
fun var_elim_right (v,t) = map (apply_snd (applysubtoterm (addsub EMPTY (v,t))))
fun max_common_subsorts Sigma s1 s2 =
let val so = Sig.get_sort_ordering Sigma
in Sig.S.maximal_sorts so
(intersection Sig.S.SortEq
(s1::Sig.S.subsorts so s1)
(s2::Sig.S.subsorts so s2)
)
end
fun strict_sol so sl sl' = (not (forall_pairs Sig.S.SortEq sl sl')
handle Zip => false)
andalso
Sig.S.sort_ordered_list so sl sl'
fun maximal_sort_lists so opsiglist s =
let val test = (C (curry (Sig.S.sort_ordered_reflexive so)) s) o Sig.O.get_result_sort
fun max_lists all = filter (not o C exists all o strict_sol so)
val lessigs = mapfilter test Sig.O.get_arg_sorts opsiglist
in max_lists lessigs lessigs
end ;
fun get_opsigs Sigma = Sig.O.get_OpSigs o Sig.O.operator_sig (Sig.get_operators Sigma) o root_operator ;
fun OSunify Sigma t1 t2 =
let
val V = union VarEq (vars_of_term t1) (vars_of_term t2)
val newvar = non (element VarEq V)
val LS = least_sort Sigma
val so = Sig.get_sort_ordering Sigma
val ops = Sig.get_operators Sigma
val <<= = Sig.S.sort_ordered_reflexive so
infix <<=
val newVarTerm = mk_VarTerm o Sig.V.generate_variable
fun unifylist ((s,t)::re) S =
if TermEq s t
then unifylist re S
else (case (compound s,compound t) of
(true,true) => if same_root s t
then unifylist (Decomposition s t @ re) S
else []
| (true,false) => let val ss = LS s
val st = LS t
in if ss <<= st
then
let val v = get_Variable t
in if occurs v s
then []
else unifylist
(var_elim (v,s) re)
(if newvar v then var_elim_right (v,s) S
else (v,s)::(var_elim_right (v,s) S))
end
else
let val SIGS = maximal_sort_lists so (get_opsigs Sigma s) st
in if null SIGS
then []
else let val v = get_Variable t
val st = subterms s
val f = root_operator s
fun merge sl =
let val nvars = map newVarTerm sl
val newterm = mk_OpTerm f nvars
val newpairs = zip (nvars,st)
in unifylist
(newpairs@(var_elim (v,newterm) re))
(if newvar v then var_elim_right (v,s) S
else (v,newterm)::(var_elim_right (v,newterm) S))
end
in mapapp merge SIGS
end
end
end
| (false,true) => unifylist ((t,s)::re) S
| (false,false) =>
let val ss = LS s
val st = LS t
in if ss <<= st
then
let val v = get_Variable t
in unifylist
(var_elim (v,s) re)
(if newvar v then var_elim_right (v,s) S
else (v,s)::(var_elim_right (v,s) S))
end
else if st <<= ss
then
let val v = get_Variable s
in unifylist
(var_elim (v,t) re)
(if newvar v then var_elim_right (v,t) S
else (v,t)::(var_elim_right (v,t) S))
end
else
let val sbsrts = max_common_subsorts Sigma ss st
in if null sbsrts
then []
else
let val newvars = map newVarTerm sbsrts
val v = get_Variable s
val u = get_Variable t
fun merge z = unifylist
(var_elim (v,z) (var_elim (u,z) re))
((u,z)::(v,z)::(var_elim_right (v,z)
(var_elim_right (u,z) S)))
in mapapp merge newvars
end
end
end
)
| unifylist [] S = [foldl addsub EMPTY S] ;
in
unifylist [(t1,t2)] []
end
val unify = OSunify
end
;
