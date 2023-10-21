signature RPO =
sig
type Signature
type Equality
type Environment
type ORIENTATION
val RPO : Signature -> Environment -> Equality
-> ORIENTATION * Environment
end
;
functor RPOFUN (structure T : TERM
structure Eq : EQUALITY
structure O : ORDER
structure En : ENVIRONMENT
structure P : PRECEDENCE
sharing type T.Sig.Signature = Eq.Signature =
En.Signature = P.Signature
and type T.Term = Eq.Term = P.Term
and type T.Sig.O.OpId = P.OpId = T.OpId
and type T.Sig.V.Variable = T.Variable
and type Eq.Equality = En.Equality
and type O.ORIENTATION = En.ORIENTATION = Eq.ORIENTATION
and type P.Precedence = En.Precedence
) : RPO =
struct
type Signature = T.Sig.Signature
type Equality = Eq.Equality
type Environment = En.Environment
type ORIENTATION = O.ORIENTATION
structure Vars = T.Sig.V
open Eq T En O P
local
fun orientable_all P t1 [] = (true, P)
| orientable_all P s1 (t1::ts) =
let val (flag1,Ps) = orientable P s1 t1
in if flag1
then let val (flag2, Qs) = orientable_all Ps s1 ts
in if flag2
then (true, Qs)
else (false, P)
end
else (false, P)
end
and orientable_some_eq P (t1::rest) t2 =
if permutatively_congruent t1 t2
then (true, P)
else let val (flag1,Ps) = orientable P t1 t2
in if flag1 then (flag1,Ps)
else orientable_some_eq P rest t2
end
| orientable_some_eq P [] t2 = (false , P)
and orientable_lex P Lhs (t1::rest1) (t2::rest2) =
if TermEq t1 t2
then orientable_lex P Lhs rest1 rest2
else let val (flag1,Ps) = orientable P t1 t2
in if flag1
then let val (flag2,Qs) = orientable_all Ps Lhs rest2
in if flag2
then (true, Qs)
else (false, P)
end
else (false, P)
end
| orientable_lex P Lhs _ _ = (false, P)
and orientable P Lhs Rhs =
if variable Lhs
then (false, P)
else let val (flag1, Ps) = orientable_some_eq P (subterms Lhs) Rhs
in if flag1 orelse variable Rhs
then (true, Ps)
else let val (f1,f2) = (root_operator Lhs, root_operator Rhs)
val (args1,args2) = (subterms Lhs, subterms Rhs)
in if same_root Lhs Rhs
then orientable_lex P Lhs args1 args2
else if apply_prec P f2 f1 then (false,P)
else let val (flag2,Ps) = orientable_all P Lhs args2
in if flag2
then (true, if apply_prec Ps f1 f2 then Ps
else add_to_prec_order Ps (f1,f2))
else (false,P)
end
end
end
in
val extendable_rpo = orientable
end;
local
fun allowable t1 t2 =
forall (element Vars.VarEq (vars_of_term t1)) (vars_of_term t2)
fun rpo A Local_ord P e =
let val (s,t) = terms e
val (ltor,ltorprec) = extendable_rpo P s t
val (rtol,rtolprec) = extendable_rpo P t s
in if ltor andalso allowable s t
then if rtol andalso allowable t s
then
case Local_ord A e of
LR => (LR,ltorprec)
| RL => (RL,rtolprec)
| UNORIENTABLE => (UNORIENTABLE,P)
else
(LR,ltorprec)
else if rtol andalso allowable t s
then (RL,rtolprec)
else (UNORIENTABLE, P)
end
in
fun RPO A env e =
let val (Direction,P) = rpo A (snd(get_locord env)) (get_precord env) e
in (Direction, set_precord (K P) env )
end
end
end
;
