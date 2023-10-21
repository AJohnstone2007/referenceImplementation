signature USERKBO =
sig
type Signature
type Equality
type Environment
type ORIENTATION
val userKBO : Signature -> Environment -> Equality
-> ORIENTATION * Environment
end
;
functor UserKBOFUN (structure T : TERM
structure Eq : EQUALITY
structure O : ORDER
structure En : ENVIRONMENT
structure P : PRECEDENCE
structure W : WEIGHTS
sharing type T.Sig.Signature = Eq.Signature =
En.Signature = P.Signature
and type T.Term = Eq.Term = P.Term
and type T.Sig.O.OpId = P.OpId = T.OpId = W.OpId
and type Eq.Equality = En.Equality
and type O.ORIENTATION = En.ORIENTATION = Eq.ORIENTATION
and type P.Precedence = En.Precedence
and type W.Weights = En.Weights
) : USERKBO =
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
fun userkbo (W : Ops.OpId Search -> int) (P:Precedence) =
let fun KBOlex l1 l2 = LexicoExtLeft TermEq KnuBenOrd l1 l2
and
KnuBenOrd s t =
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
orelse (same_root s t andalso (KBOlex s1 t1))
else false
end
| (true,false) => true
| _ => false )
in KnuBenOrd
end
end ;
fun userKBO A env e = (orientation (userkbo (find_weight (get_weights env)) (get_precord env)) e, env)
end
;
