functor ACUnifyFUN (structure Dio:DIO
structure T:TERM
structure S:SUBSTITUTION
structure A:AC_TOOLS
sharing type S.Term = T.Term = A.Term
and type T.OpId = T.Sig.O.OpId = A.OpId
and type S.Variable = T.Sig.V.Variable = T.Variable = A.Variable
and type T.Sort = T.Sig.S.Sort =
T.Sig.V.Sort = T.Sig.O.Sort
and type S.Signature = T.Sig.Signature = A.Signature
) : UNIFY =
struct
type Signature = T.Sig.Signature
type Term = T.Term
type Substitution = S.Substitution
type OpId = T.Sig.O.OpId
val OpIdEq = T.Sig.O.OpIdeq
val VarEq = T.Sig.V.VarEq
open S T A
datatype Constraint = None | Con | Sym of OpId ;
fun unconstrained None = true
| unconstrained Con = false
| unconstrained (Sym _) = false
;
local
fun filter_left n = Library.filter (curry (op >=) 1 o C (curry nth) n o fst)
fun filter_right n = Library.filter (curry (op >=) 1 o C (curry nth) n o snd)
fun remove_cons_dio_sols (a::acons,bcons) n sols =
if unconstrained a
then remove_cons_dio_sols (acons,bcons)
(if null acons then 0 else n+1) sols
else remove_cons_dio_sols (acons,bcons)
(if null acons then 0 else n+1) (filter_left n sols)
| remove_cons_dio_sols ([],b::bcons) n sols =
if unconstrained b
then remove_cons_dio_sols ([],bcons) (n+1) sols
else remove_cons_dio_sols ([],bcons) (n+1)
(filter_right n sols)
| remove_cons_dio_sols (_,[]) _ sols = sols
local
fun find_clash con (a::acons, bcons) (a1::asols, bsols) =
if unconstrained a orelse a1 = 0
then find_clash con (acons, bcons) (asols, bsols)
else (case (con,a) of
(Sym f, Sym g) => if OpIdEq f g
then true
else false
| ( _ , _ ) => false
)
| find_clash con ([], b::bcons) ([], b1::bsols) =
if unconstrained b orelse b1 = 0
then find_clash con ([], bcons) ([], bsols)
else (case (con,b) of
(Sym f, Sym g) => if OpIdEq f g
then true
else false
| ( _ , _ ) => false
)
| find_clash con ([], []) ([], []) = true
| find_clash con _ _ = raise Zip
fun clash_dio_sol (a::acons, bcons) (a1::asols, bsols) =
if unconstrained a orelse a1 = 0
then clash_dio_sol (acons, bcons) (asols, bsols)
else find_clash a (acons,bcons) (asols, bsols)
andalso
clash_dio_sol (acons, bcons) (asols, bsols)
| clash_dio_sol ([], b::bcons) ([], b1::bsols) =
if unconstrained b orelse b1 = 0
then clash_dio_sol ([], bcons) ([], bsols)
else find_clash b ([],bcons) ([], bsols)
andalso
clash_dio_sol ([], bcons) ([], bsols)
| clash_dio_sol ([], []) ([], []) = true
| clash_dio_sol _ _ = raise Zip
in
fun remove_clashing_sols cons = filter (clash_dio_sol cons)
end ;
fun add_fst_hds ((a::_,_)::r) 0 = add_fst_hds r a
| add_fst_hds ((0::_,_)::r) 1 = add_fst_hds r 1
| add_fst_hds ((a::_,_)::r) 1 = 2
| add_fst_hds [] s = s
| add_fst_hds _ s = failwith "Fst_Hds"
and add_snd_hds ((_,a::_)::r) 0 = add_snd_hds r a
| add_snd_hds ((_,0::_)::r) 1 = add_snd_hds r 1
| add_snd_hds ((_,a::_)::r) 1 = 2
| add_snd_hds [] s = s
| add_snd_hds _ s = failwith "Snd_Hds";
fun exist_fst_hd ((a::_,_)::r) = a <> 0 orelse exist_fst_hd r
| exist_fst_hd [] = false
| exist_fst_hd _ = failwith "Fst_Hds"
and exist_snd_hd ((_,a::_)::r) = a <> 0 orelse exist_snd_hd r
| exist_snd_hd [] = false
| exist_snd_hd _ = failwith "Snd_Hds";
fun check_cons (ca::racs,bcs) sols =
if unconstrained ca
then exist_fst_hd sols
andalso
check_cons (racs,bcs) (map (Library.apply_fst tl) sols)
else
add_fst_hds sols 0 = 1
andalso
check_cons (racs,bcs) (map (Library.apply_fst tl) sols)
| check_cons ([],ba::rbcs) sols =
if unconstrained ba
then exist_snd_hd sols
andalso
check_cons ([],rbcs) (map (Library.apply_snd tl) sols)
else
add_snd_hds sols 0 = 1
andalso
check_cons ([],rbcs) (map (Library.apply_snd tl) sols)
| check_cons ([],[]) sols = true
in
fun generate_valid_subsets (acons,bcons) =
Library.filter (check_cons (acons,bcons)) o
Library.powerset o
remove_clashing_sols (acons,bcons) o
remove_cons_dio_sols (acons,bcons) 0
end
fun count_and_remove eq a (b::l) =
let val (nas,l') = count_and_remove eq a l
in if eq a b
then (nas+1, l')
else (nas, b :: l')
end
| count_and_remove eq a [] = (0,[])
fun occurence_lists eq (a::l) =
let val (nas, l') = count_and_remove eq a l
val (ais,nais) = occurence_lists eq l'
in (a::ais,1+nas::nais)
end
| occurence_lists eq [] = ([],[])
fun assign_var (a::l) v = (copy a v) :: assign_var l v
| assign_var [] v = []
fun resolve_subset [(s1,s2)] =
let val newvar = mk_VarTerm (Sig.V.generate_variable Sig.S.Top)
in (assign_var s1 newvar,assign_var s2 newvar)
end
| resolve_subset ((s1,s2)::rss) =
let val newvar = mk_VarTerm (Sig.V.generate_variable Sig.S.Top)
val (sv1,sv2) = (assign_var s1 newvar,assign_var s2 newvar)
val (rsv1,rsv2) = resolve_subset rss
in (map2 (op @) sv1 rsv1,map2 (op @) sv2 rsv2)
end
| resolve_subset [] = ([],[])
fun assign_new_terms ac_op (ais,bis) (asol,bsol) =
let fun pairs (x::xs,s::ss) = (x,AC_unflatten ac_op s)::pairs (xs,ss)
| pairs ([],[]) = []
| pairs _ = raise Zip
in pairs (ais,asol) @ pairs (bis,bsol)
end
fun constraints Ts = map (fn T1 =>
if compound T1
then if constant T1 then Con else Sym (root_operator T1)
else None
) Ts
fun AC_Unify_Subterms ac_op Term_List_1 Term_List_2 =
let
val Distinct_Terms_1 = bag_difference TermEq Term_List_1 Term_List_2
val Distinct_Terms_2 = bag_difference TermEq Term_List_2 Term_List_1
val (subts_1, ais) = occurence_lists TermEq Distinct_Terms_1
val (subts_2, bis) = occurence_lists TermEq Distinct_Terms_2
val constraints1 = constraints subts_1
val constraints2 = constraints subts_2
val Basis = Dio.solve_dio_equation ais bis 0
val subsets = generate_valid_subsets (constraints1,constraints2) Basis
in map (assign_new_terms ac_op (subts_1,subts_2) o resolve_subset) subsets
end
fun ACmutate T1 T2 = AC_Unify_Subterms (root_operator T1) (AC_subterms T1) (AC_subterms T2)
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
fun ACOSunify Sigma t1 t2 =
let
val V = union VarEq (vars_of_term t1) (vars_of_term t2)
val newvar = non (element VarEq V)
val LS = least_sort Sigma
val so = Sig.get_sort_ordering Sigma
val ops = Sig.get_operators Sigma
val is_C = Sig.O.C_Operator ops o root_operator
val is_AC = Sig.O.AC_Operator ops o root_operator
val <<= = Sig.S.sort_ordered_reflexive so
infix <<=
val newVarTerm = mk_VarTerm o Sig.V.generate_variable
fun elim_right (v,s) S = if newvar v then var_elim_right (v,s) S
else (v,s)::(var_elim_right (v,s) S)
fun unifylist ((s,t)::re) S =
if AC_equivalent Sigma s t
then unifylist re S
else (case (compound s,compound t) of
(true,true) => if same_root s t
then if is_AC s
then mapapp (C unifylist S o (C append re))
(ACmutate s t)
else if is_C s
then mapapp (C unifylist S o (C append re))
(Cmutate Sigma s t)
else unifylist (Decomposition s t @ re) S
else []
| (true,false) => let val ss = LS s
val st = LS t
in if ss <<= st
then
let val v = get_Variable t
in if occurs v s
then []
else unifylist
(var_elim (v,s) re) (elim_right (v,s) S)
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
(elim_right (v,newterm) S)
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
(var_elim (v,s) re) (elim_right (v,s) S)
end
else if st <<= ss
then
let val v = get_Variable s
in unifylist
(var_elim (v,t) re) (elim_right (v,t) S)
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
handle (Least_Sort ss) => (error_message "No Unification Possible - Signature not Regular" ; [])
end
val unify = ACOSunify
end
;
