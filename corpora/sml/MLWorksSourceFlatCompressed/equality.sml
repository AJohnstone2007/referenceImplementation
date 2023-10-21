functor EqualityFUN (structure T : TERM
structure S : SUBSTITUTION
structure O : ORDER
sharing type T.Term = S.Term
and type T.Sig.Signature = S.Signature
sharing T.Pretty = T.Sig.O.Pretty
) : EQUALITY =
struct
type Signature = T.Sig.Signature
type Term = T.Term
type Substitution = S.Substitution
type ORIENTATION = O.ORIENTATION
open T S O
structure Pretty = T.Pretty
abstype Equality = Equation of Term * Term * bool
| Rule of Term * Term * bool
| Conjecture of (Term * Term) * (Term * Term) * bool
| Cond of Equality list * Equality
with
fun mk_equality t1 t2 = Equation (t1,t2,false)
fun mk_conjecture t1 t2 = Conjecture ((t1,t2),(t1,t2),false)
fun mk_conditional e1 es = if null es then e1 else Cond (es, e1)
fun is_conditional (Cond (el,e2)) = true
| is_conditional _ = false
fun conditions (Cond (es,e)) = es
| conditions _ = []
fun conclusion (Cond (es,e)) = e
| conclusion e = e
fun is_rule (Rule (t1,t2,p)) = true
| is_rule (Cond (el,e2)) = is_rule e2
| is_rule _ = false
fun lhs (Equation (t1,t2,p)) = t1
| lhs (Rule (t1,t2,p)) = t1
| lhs (Conjecture (e1,(t1,t2),p)) = t1
| lhs (Cond (el,e2)) = lhs e2
fun rhs (Equation (t1,t2,p)) = t2
| rhs (Rule (t1,t2,p)) = t2
| rhs (Conjecture (e1,(t1,t2),p)) = t2
| rhs (Cond (el,e2)) = rhs e2
fun terms (Equation (t1,t2,p)) = (t1,t2)
| terms (Rule (t1,t2,p)) = (t1,t2)
| terms (Conjecture (e1,(t1,t2),p)) = (t1,t2)
| terms (Cond (el,e2)) = terms e2
fun protect (Equation (t1,t2,p)) = Equation (t1,t2,true)
| protect (Rule (t1,t2,p)) = Rule (t1,t2,true)
| protect (Conjecture ((h1,h2),(t1,t2),p)) = Conjecture ((h1,h2),(t1,t2),true)
| protect (Cond (el,e2)) = Cond (el,protect e2)
fun unprotect (Equation (t1,t2,p)) = Equation(t1,t2,false)
| unprotect (Rule (t1,t2,p)) = Rule (t1,t2,false)
| unprotect (Conjecture ((h1,h2),(t1,t2),p)) = Conjecture ((h1,h2),(t1,t2),false)
| unprotect (Cond (el,e2)) = Cond (el,unprotect e2)
fun protected (Equation (t1,t2,p)) = p
| protected (Rule (t1,t2,p)) = p
| protected (Conjecture ((h1,h2),(t1,t2),p)) = p
| protected (Cond (el,e2)) = protected e2
val left_linear = T.linear o lhs
val right_linear = T.linear o rhs
val linear_equation = et left_linear right_linear
val num_ops_in_eq = apply_both num_ops_in_term o terms
val num_of_vars_in_eq = apply_both ((foldl (C (add o snd)) 0) o num_of_vars) o terms
fun order (Equation (t1,t2,p)) = Rule (t1,t2,p)
| order (Rule (t1,t2,p)) = Rule (t1,t2,p)
| order (Conjecture ((h1,h2),(t1,t2),p)) = Conjecture ((h1,h2),(t1,t2),p)
| order (Cond (el,e2)) = Cond (el,order e2)
fun reorder (Equation (t1,t2,p)) = Rule (t2,t1,p)
| reorder (Rule (t1,t2,p)) = Rule (t2,t1,p)
| reorder (Conjecture ((h1,h2),(t1,t2),p)) = Conjecture ((h2,h1),(t2,t1),p)
| reorder (Cond (el,e2)) = Cond (el,reorder e2)
fun unorder (Equation (t1,t2,p)) = Equation (t1,t2,p)
| unorder (Rule (t1,t2,p)) = Equation (t1,t2,p)
| unorder (Conjecture ((h1,h2),(t1,t2),p)) = Conjecture ((h2,h1),(t2,t1),p)
| unorder (Cond (el,e2)) = Cond (el,unorder e2)
fun mk_rule t1 t2 LR = Rule (t1,t2,false)
| mk_rule t1 t2 RL = Rule (t2,t1,false)
| mk_rule t1 t2 UNORIENTABLE = Equation (t1,t2,false)
fun relate p = uncurry p o terms
val identity = relate TermEq
fun EqualityEq (Equation (l1,r1,p1)) (Equation (l2,r2,p2)) =
let val (p,e) = alphaequiv l1 l2 Assoc.Empty_Assoc
in p andalso fst (alphaequiv r1 r2 e)
end
orelse
let val (p,e) = alphaequiv l1 r2 Assoc.Empty_Assoc
in p andalso fst (alphaequiv l2 r1 e)
end
| EqualityEq (Rule (l1,r1,p1)) (Rule (l2,r2,p2)) =
let val (p,e) = alphaequiv l1 l2 Assoc.Empty_Assoc
in p andalso fst (alphaequiv r1 r2 e)
end
| EqualityEq (Cond(el1,e1)) (Cond (el2,e2)) =
forall (fn e => exists (EqualityEq e) el2) el1
andalso
forall (fn e => exists (EqualityEq e) el1) el2
andalso
EqualityEq e1 e2
| EqualityEq e1 e2 =
let val (l1,r1) = terms e1
val (l2,r2) = terms e2
val (p,e) = alphaequiv l1 l2 Assoc.Empty_Assoc
in p andalso fst (alphaequiv r1 r2 e)
end
fun applysubtoequality s (Equation (l,r,p)) =
Equation (applysubtoterm s l,applysubtoterm s r,p)
| applysubtoequality s (Rule (l,r,p)) =
Rule (applysubtoterm s l,applysubtoterm s r,p)
| applysubtoequality s (Conjecture (e1,(l,r),p)) =
Conjecture (e1,(applysubtoterm s l,applysubtoterm s r),p)
| applysubtoequality s (Cond (el,e2)) =
Cond (map (applysubtoequality s) el,applysubtoequality s e2)
local
fun rename_eq e (Equation (l,r,p)) =
let val (env,l0) = rename_term l e
val (env,r0) = rename_term r env
in (Equation (l0,r0,p) , env)
end
| rename_eq e (Rule (l,r,p)) =
let val (env,l0) = rename_term l e
val (env,r0) = rename_term r env
in (Rule (l0,r0,p) , env)
end
| rename_eq e (Conjecture (e1,(l,r),p)) =
let val (env,l0) = rename_term l e
val (env,r0) = rename_term r env
in (Conjecture (e1,(l0,r0),p) , env)
end
| rename_eq e (Cond(es,e1)) =
let fun fold ([],e) = ([],e)
| fold (e1::es, e) =
let val (re1, e') = rename_eq e e1
val (res,e'') = fold (es,e')
in (re1::res,e'')
end
val (es',e') = fold (es,e)
val (e1',e'') = rename_eq e' e1
in (Cond(es',e1') , e'')
end
in
val rename_equality = fst o rename_eq Assoc.Empty_Assoc
end
local
val empty = Sig.V.Empty_Var_Print_Env
fun get_terms a l r e =
let val (e1,env) = unparse_term a e l
val (e2,env') = unparse_term a env r
in ((e1,e2),env')
end
fun unp_equality a env (Equation (l,r,p)) =
let val ((e1,e2),env) = get_terms a l r env
in (e1^" = "^e2^(if p then "    (*)" else ""),env)
end
| unp_equality a env (Rule (l,r,p)) =
let val ((e1,e2),env) = get_terms a l r env
in (e1^" => "^e2^(if p then "    (*)" else ""),env)
end
| unp_equality a env (Conjecture ((h1,h2),(t1,t2),p)) =
let val ((hs1,hs2),env) = get_terms a h1 h2 env
val ((ts1,ts2),env) = get_terms a t1 t2 env
in ("{"^hs1^" =?= "^hs2^"}  "^ts1^" =?= "^ts2^(if p then "PROVED  " else "") ,env)
end
| unp_equality a env (Cond(el,e1)) =
if null el then (unp_equality a env e1)
else let fun fffl env (e::es) =
let val (est,env) = unp_equality a env e
val (ess,env) = fffl env es
in (est::ess,env)
end
| fffl env [] = ([],env)
val (ess,env) = fffl env el
val (es1,env) = unp_equality a env e1
in (stringwith (""," , ","") ess ^ " ==> " ^es1 , env)
end
in
fun unparse_equality a = fst o (unp_equality a empty)
fun show_equality a e = fst (get_terms a (lhs e) (rhs e) empty)
end
local
open Pretty
val empty = Sig.V.Empty_Var_Print_Env
fun get_terms a l r e =
let val (e1,env) = pretty_term a e l
val (e2,env') = pretty_term a env r
in ((e1,e2),env')
end
fun p_equality a env (Equation (l,r,p)) =
let val ((e1,e2),env) = get_terms a l r env
in (blo (2,[e1,brk 2, str "=", brk 2, e2]@(if p then [str "    (*)"] else [])),env)
end
| p_equality a env (Rule (l,r,p)) =
let val ((e1,e2),env) = get_terms a l r env
in (blo (2,[e1,brk 1, str "=>", brk 1, e2]@(if p then [str "    (*)"] else [])),env)
end
| p_equality a env (Conjecture ((h1,h2),(t1,t2),p)) =
let val ((hs1,hs2),env) = get_terms a h1 h2 env
val ((ts1,ts2),env) = get_terms a t1 t2 env
in (blo (2,[str "{", hs1, brk 2, str "=?=", brk 2, hs2, str"}", brk 4,
ts1, brk 2, str "=?=", brk 2, ts2] @(if p then [str "PROVED  "] else [])),
env)
end
| p_equality a env (Cond(el,e1)) =
if null el then (p_equality a env e1)
else let fun fffl env (e::es) =
let val (est,env) = p_equality a env e
val (ess,env) = fffl env es
in (est::ess,env)
end
| fffl env [] = ([],env)
val (ess,env) = fffl env el
val (es1,env) = p_equality a env e1
in (blo(2, (interleave3 ess (copy (length ess -1) (str ","))
(copy (length ess -1) (brk 1)))
@ [ brk 2, str " ==> ", brk 2, es1]) , env)
end
in
fun pretty_equality a = fst o (p_equality a empty)
end
fun parse_eq Al TS es en ss =
let fun lookforCond f ((r,s),e) = parse_cond Al TS es (f r) s e
in
parse_term Al TS en ss propM
(fn ((l,s),e) => let val ss' = strip s
in if implode (take 2 ss') = "=>"
then parse_term Al TS e (drop 2 ss') propM
lookforCond (C (mk_rule l) LR)
else if implode (take 2 ss') = "<="
then parse_term Al TS e (drop 2 ss') propM
lookforCond (C (mk_rule l) RL)
else if implode (take 3 ss') = "=?="
then parse_term Al TS e (drop 3 ss') propM
lookforCond (mk_conjecture l)
else if hd ss' = "="
then parse_term Al TS e (strip (tl ss')) propM
lookforCond (mk_equality l)
else Error "Missing Right Hand Term"
end)
end
and parse_cond Al TS es e ss en =
if null ss then OK e
else if hd ss = ","
then parse_eq Al TS (snoc es e) en (tl ss)
else if implode (take 3 ss) = "==>"
then parse_eq Al TS [] en (drop 3 ss) eachM
(Cond o pair (snoc es e))
else OK e
fun parse_equality Al TS ss = parse_eq Al TS [] Assoc.Empty_Assoc ss
fun orientation order = uncurry (compare order) o terms
end
end
; 