signature USERAKBO =
sig
type Signature
type Equality
type Environment
type ORIENTATION
val userAKBO : Signature -> Environment -> Equality
-> ORIENTATION * Environment
end
;
functor UserAKBOFUN (structure T : TERM
structure Eq : EQUALITY
structure O : ORDER
structure A : AC_TOOLS
structure En : ENVIRONMENT
structure P : PRECEDENCE
structure W : WEIGHTS
sharing type T.Sig.Signature = Eq.Signature =
En.Signature = P.Signature = A.Signature
and type T.Term = Eq.Term = P.Term = A.Term
and type T.Sig.O.OpId = P.OpId = T.OpId = W.OpId = A.OpId
and type Eq.Equality = En.Equality
and type O.ORIENTATION = En.ORIENTATION = Eq.ORIENTATION
and type P.Precedence = En.Precedence
and type W.Weights = En.Weights
) : USERAKBO =
struct
type Signature = T.Sig.Signature
type Equality = Eq.Equality
type Environment = En.Environment
type ORIENTATION = O.ORIENTATION
structure Ops = T.Sig.O
open Eq T En O P W
local
fun weight W t =
if variable t then W NoMatch
else W (Match (root_operator t)) + (sum (map (weight W) (subterms t)))
in
fun userakbo F (W : Ops.OpId Search -> int) (P:Precedence) =
let fun KBOlex l1 l2 = LexicoExtLeft TermEq KBO l1 l2
and
KBOmult l1 l2 = MultiSetExt KBO l1 l2
and
KBO s t =
(case (compound s,compound t) of
(true,true) => let val (f,g) = (root_operator s, root_operator t)
val (s1,t1) = (subterms s, subterms t)
val ws = weight W s
val wt = weight W t
in
if ws > wt then true
else
if ws = wt then
(apply_prec P f g)
orelse (same_root s t andalso
(if Ops.AC_Operator F f
then KBOmult (A.AC_subterms s) (A.AC_subterms t)
else if Ops.C_Operator F f
then KBOmult s1 t1
else KBOlex s1 t1)
)
else false
end
| (true,false) => true
| _ => false )
in KBO
end
end ;
fun userAKBO A env e =
let val P = get_precord env
val W = find_weight (get_weights env)
val F = T.Sig.get_operators A
val ACs = filter (Ops.AC_Operator F) (Ops.all_ops F)
in if forall (eq 0 o W o Match) ACs
andalso
forall (null o sub_prec P) ACs
then (orientation (userakbo F W P) e, env)
else (Error.error_message
"Declarations fail to satisfy conditions for AC-KBO." ;
(UNORIENTABLE,env) )
end
end
;
