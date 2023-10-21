signature USERRPO =
sig
type Signature
type Equality
type Environment
type ORIENTATION
val userRPOLeft : Signature -> Environment -> Equality
-> ORIENTATION * Environment
val userRPORight : Signature -> Environment -> Equality
-> ORIENTATION * Environment
val userRPOMultiSet : Signature -> Environment -> Equality
-> ORIENTATION * Environment
end
;
functor UserRPOFUN (structure T : TERM
structure Eq : EQUALITY
structure O : ORDER
structure En : ENVIRONMENT
structure P : PRECEDENCE
sharing type T.Sig.Signature = Eq.Signature =
En.Signature = P.Signature
and type T.Term = Eq.Term = P.Term
and type T.Sig.O.OpId = P.OpId = T.OpId
and type Eq.Equality = En.Equality
and type O.ORIENTATION = En.ORIENTATION = Eq.ORIENTATION
and type P.Precedence = En.Precedence
and type T.Variable = T.Sig.V.Variable
) : USERRPO =
struct
type Signature = T.Sig.Signature
type Equality = Eq.Equality
type Environment = En.Environment
type ORIENTATION = O.ORIENTATION
structure Ops = T.Sig.O
structure Vars = T.Sig.V
open Eq T En O P
local
fun userRPO A Ext (P:Precedence) =
let fun RPOext l1 l2 = Ext RecPathOrd l1 l2
and
BiggerThanAny (s:Term) t1 = forall (RecPathOrd s) t1
and
RecPathOrd s t =
(
case (compound s,compound t) of
(true,true) => let val (f,g) = (root_operator s, root_operator t)
val (s1,t1) = (subterms s, subterms t)
in
((apply_prec P f g) andalso
(
BiggerThanAny s t1) )
orelse
((Ops.OpIdeq f g orelse equal_prec P f g)
andalso (
RPOext s1 t1))
orelse
(exists (permutatively_congruent t) s1)
orelse (exists (C RecPathOrd t) s1)
end
| (true,false) =>
let
val s1 = subterms s
in (exists (C RecPathOrd t) s1)
orelse (exists (TermEq t) s1)
end
| _ => false )
in RecPathOrd
end
in
fun userRPOLeft A env e = (orientation (userRPO A (LexicoExtLeft TermEq) (get_precord env)) e , env)
fun userRPORight A env e = (orientation (userRPO A (LexicoExtRight TermEq) (get_precord env)) e , env)
fun userRPOMultiSet A env e = (orientation (userRPO A MultiSetExt (get_precord env)) e , env)
end
end
;
