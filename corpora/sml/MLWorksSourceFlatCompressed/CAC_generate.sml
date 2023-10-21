signature CAC_THEORY =
sig
type Signature
type Equality
val CAC_Theory : Signature -> Equality list
end
;
functor CAC_TheoryFUN (structure T : TERM
structure S : SUBSTITUTION
structure M : MATCH
structure E : EQUALITY
sharing type S.Term = T.Term = M.Term = E.Term
and type S.Variable = T.Sig.V.Variable = T.Variable
and type S.Substitution = M.Substitution =
E.Substitution
and type T.Sig.S.Sort = T.Sig.V.Sort = T.Sort
and type T.Sig.S.Sort_Store = T.Sig.V.Sort_Store
and type T.OpId = T.Sig.O.OpId
and type S.Signature = T.Sig.Signature =
M.Signature = E.Signature
) : CAC_THEORY =
struct
type Signature = T.Sig.Signature
type Equality = E.Equality
open T T.Sig.S T.Sig.V T.Sig.O
val var = mk_VarTerm o generate_variable
fun mk_comm f (s1,s2) =
let val v1 = var s1
val v2 = var s2
in [(mk_OpTerm f [v1,v2] , mk_OpTerm f [v2,v1])]
end
fun mk_assoc f (s1,(s2,s3)) =
let val v1 = var s1
val v2 = var s2
val v3 = var s3
val v4 = var s1
val v5 = var s2
val v6 = var s3
in [(mk_OpTerm f [mk_OpTerm f [v1,v2],v3],
mk_OpTerm f [v1,mk_OpTerm f [v2,v3]]) ,
(mk_OpTerm f [v4,mk_OpTerm f [v5,v6]],
mk_OpTerm f [mk_OpTerm f [v4,v5],v6]) ]
end
fun check_well_formed Sigma (l,r) =
(let val sl = least_sort Sigma l
val sr = least_sort Sigma r
in if SortEq sl Top andalso SortEq sr Top
then Match []
else if SortEq sl Top orelse SortEq sr Top
then NoMatch
else Match ([(E.mk_equality l r,(sl,sr))])
end
handle Least_Sort fid => Match [])
fun all_well_formed Sigma wfes (e1::es) =
(case check_well_formed Sigma e1 of
Match [] => all_well_formed Sigma wfes es
| Match ess => all_well_formed Sigma (ess@wfes) es
| NoMatch => []
)
| all_well_formed Sigma wfes [] = wfes
fun mapapply (f::fs) a = mapapp f a @ mapapply fs a
| mapapply [] a = []
fun all_CAC_Theory Sigma =
let
val sorts = fold_over_sorts (C cons) [] (Sig.get_sorts Sigma)
val pairs = allpairs sorts
val triples = cross_product sorts pairs
val FS = Sig.get_operators Sigma
val ops = all_ops FS
val Cops = filter (C_Operator FS) ops
val ACops = filter (AC_Operator FS) ops
val ces = mapapply (map mk_comm (Cops@ACops)) pairs
val aces = mapapply (map mk_assoc ACops) triples
in
(all_well_formed Sigma [] ces ,
all_well_formed Sigma [] aces)
end
fun sort_preserving_equality (e,(s1,s2)) = SortEq s1 s2
fun sort_preserving_theory (cess,acess) =
if forall sort_preserving_equality cess
andalso
forall sort_preserving_equality acess
then (map fst cess , map fst acess )
else ([], [])
fun independent Sigma e1 e2 =
let val l1 = E.lhs e1
val l2 = E.lhs e2
in S.isfail (M.match Sigma l1 l2)
end
fun all_independent Sigma inds (e1::es) =
let val inds' = filter (independent Sigma e1) inds
in if forall (C (independent Sigma) e1) inds'
then all_independent Sigma (e1::inds') es
else all_independent Sigma inds' es
end
| all_independent Sigma inds [] = inds
fun independent_theory Sigma (ces,aces) =
all_independent Sigma [] ces @
all_independent Sigma [] aces
fun CAC_Theory Sigma =
independent_theory Sigma (sort_preserving_theory (all_CAC_Theory Sigma))
end
;
