functor AC_ToolsFUN (structure T : TERM
sharing type T.OpId = T.Sig.O.OpId
and type T.Sig.V.Variable = T.Variable
) : AC_TOOLS =
struct
type Term = T.Term
type Signature = T.Sig.Signature
type OpId = T.Sig.O.OpId
type Variable = T.Sig.V.Variable
open T T.Sig T.Sig.O
val get_operators = T.Sig.get_operators
val VarEq = T.Sig.V.VarEq
local
fun insert t [] = [t]
| insert t (t1::ts) =
if ord_t t1 t then t1::insert t ts
else t::t1::ts
fun norm f (n1::ns) =
if variable n1 then insert n1 (norm f ns)
else if same_root f n1
then norm f (subterms n1 @ ns)
else insert n1 (norm f ns)
| norm f [] = []
in
fun AC_flatten Sigma T =
if variable T
then T
else let val f = root_operator T
in if AC_Operator (get_operators Sigma) f
then mk_OpTerm f
(map (AC_flatten Sigma) (norm T (subterms T)))
else mk_OpTerm f
(map (AC_flatten Sigma) (subterms T))
end
fun AC_subterms T = norm T (subterms T)
end
fun AC_unflatten f [v] = v
| AC_unflatten f (v::rvs) = mk_OpTerm f [v, AC_unflatten f rvs]
| AC_unflatten f [] = raise (Ill_Formed_Term "No AC Terms to Unflatten")
local
fun inserts a (b::l) = (a :: b :: l) :: map (cons b) (inserts a l)
| inserts a [] = [[a]]
in
fun permutations [] = []
| permutations [a] = [[a]]
| permutations (a::l) = mapapp (inserts a) (permutations l)
end
local
exception NotEq
fun AC_eq Sigma =
let
val C_Op = C_Operator (get_operators Sigma)
val AC_Op = AC_Operator (get_operators Sigma)
fun check_subterms t1s t2s = forall_pairs AC_equiv t1s t2s
and remove (t1 :: ts) t =
if AC_equiv t t1
then ts
else t1::remove ts t
| remove [] t = raise NotEq
and AC_equiv t1 t2 =
(case (variable t1 , variable t2) of
(true,true) => VarEq (get_Variable t1) (get_Variable t2)
| (false,true) => false
| (true,false) => false
|(false,false) =>
if same_root t1 t2
andalso
num_ops_in_term t1 = num_ops_in_term t2
then if constant t1
then true
else
let val t1s = subterms t1
val t2s = subterms t2
val f = root_operator t1
in if C_Op f
then forall_pairs AC_equiv t1s t2s
orelse
forall_pairs AC_equiv t1s (rev t2s)
else
if AC_Op f
then length t1s = length t2s
andalso (null (foldl remove t2s t1s) handle NotEq => false)
else
check_subterms t1s t2s
end
else false
)
in AC_equiv
end
in
fun AC_equivalent Sigma T1 T2 =
let val t1 = AC_flatten Sigma T1
val t2 = AC_flatten Sigma T2
in AC_eq Sigma t1 t2
end
end
local
val lookup = Assoc.assoc_lookup VarEq
val assoc = Assoc.assoc VarEq VarEq
fun ff e1 p (a::l) =
let val (b,e) = p a in if b then (b,e) else ff e1 p l end
| ff e1 p [] = (false,e1)
fun AC_aeq Sigma =
let
val C_Op = C_Operator (get_operators Sigma)
val AC_Op = AC_Operator (get_operators Sigma)
fun gg env (a::l1) (b::l2) =
let val (p,env') = AC_alphaequiv a b env
in if p then gg env' l1 l2
else (false,env)
end
| gg E [] [] = (true,E)
| gg E _ _ = (false,E)
and
AC_alphaequiv t1 t2 env =
( case (variable t1,variable t2) of
(true,true) =>
let val s = get_Variable t1
val t = get_Variable t2
in
(case lookup s env of
NoMatch => (true,assoc s t env)
| Match gg => (VarEq gg t,env)
)
end
| (false, false) =>
if same_root t1 t2
andalso
num_ops_in_term t1 = num_ops_in_term t2
then if constant t1
then (true,env)
else
let val t1s = subterms t1
val t2s = subterms t2
val f = root_operator t1
in
if C_Op f
then let val (b,e) = gg env t1s t2s
in if b then (b,e) else gg env t1s (rev t2s)
end
else
if AC_Op f
then let val ps = permutations t2s
in ff env (gg env t1s) ps
end
else gg env t1s t2s
end
else (false,env)
| _ => (false,env)
)
in AC_alphaequiv
end
in
fun AC_alpha_equivalent Sigma T1 T2 env =
let val t1 = AC_flatten Sigma T1
val t2 = AC_flatten Sigma T2
in AC_aeq Sigma t1 t2 env
end
end
fun Cmutate Sigma Cterm1 Cterm2 =
let val (sis, tis) = (subterms Cterm1, subterms Cterm2)
val (s1,s2) = (hd sis, hd(tl sis))
val (t1,t2) = (hd tis, hd(tl tis))
in if AC_equivalent Sigma s1 s2 orelse AC_equivalent Sigma t1 t2
then [[(s1,t1),(s2,t2)]]
else [[(s1,t1),(s2,t2)],[(s1,t2),(s2,t1)]]
end
local
fun equiv A (l,r) (g,d) =
let val (b,e) = AC_alpha_equivalent A l g Assoc.Empty_Assoc
in b andalso fst (AC_alpha_equivalent A r d e)
end
in
fun equivalentPairs A (l,r) (g,d) =
equiv A (g,d) (l,r) orelse equiv A (d,g) (l,r)
end
end
;
