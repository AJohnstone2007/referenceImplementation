functor TermFUN(structure Sig:SIGNATURE
sharing type Sig.S.Sort = Sig.O.Sort = Sig.V.Sort
and type Sig.V.Sort_Store = Sig.S.Sort_Store
):TERM =
struct
structure Pretty = Sig.O.Pretty
structure Sig = Sig
structure S = Sig.S
structure O = Sig.O
structure V = Sig.V
type Sort = S.Sort
type OpId = O.OpId
type Variable = V.Variable
abstype Term = VarTerm of V.Variable
| OpTerm of O.OpId * int * (Term list)
with
exception Ill_Formed_Term of string
local
fun reduceterm f c T1 =
(case T1 of
(OpTerm (_,_,[])) => f T1
| (VarTerm _) => f T1
| (OpTerm(f1,_,t1)) => (c f1 (map (reduceterm f c) t1)))
in
fun compound (OpTerm(_,_,_)) = true
| compound _ = false
fun variable (VarTerm _) = true
| variable _ = false
fun constant (OpTerm(_,_,[])) = true
| constant _ = false
fun subterms (OpTerm(_,_,ts)) = ts
| subterms _ = raise (Ill_Formed_Term "No Subterms to Variable")
fun root_operator (OpTerm(s,_,_)) = s
| root_operator _ = raise (Ill_Formed_Term "No Root Operator to Variable")
fun same_root (OpTerm(f,_,_)) (OpTerm(g,_,_)) = O.OpIdeq f g
| same_root _ _ = false
fun nth_subterm (OpTerm(_,_,ts)) n =
if 0 < n andalso n <= length ts
then nth (ts,n-1)
else raise (Ill_Formed_Term "Selection does not match arity" )
| nth_subterm t n = raise (Ill_Formed_Term "No Subterms to Variable")
fun num_ops_in_term (VarTerm _) = 0
| num_ops_in_term (OpTerm (_,n,_)) = n
fun mk_OpTerm f t1 = OpTerm (f,(foldl (SS add num_ops_in_term) 1 t1),t1)
fun mk_VarTerm s = VarTerm s
fun get_Variable (VarTerm v) = v
| get_Variable (OpTerm (_,_,_)) = raise (Ill_Formed_Term "Not a Variable Term")
fun TermEq T1 T2 =
(case (T1,T2) of
(VarTerm s ,VarTerm s') => V.VarEq s s'
| (OpTerm(f1,n1,t1),OpTerm(f2,n2,t2)) =>
O.OpIdeq f1 f2
andalso
n1=n2
andalso
forall_pairs TermEq t1 t2
| (_,_) => false )
fun ord_t (OpTerm(a,_,[])) (OpTerm(b,_,[])) = O.ord_o a b
| ord_t (OpTerm(a,_,[])) _ = true
| ord_t _ (OpTerm(b,_,[])) = false
| ord_t (OpTerm(f,_,ss)) (OpTerm(g,_,ts)) =
if O.OpIdeq f g then ord_lex ss ts
else O.ord_o f g
| ord_t (OpTerm(g,_,ts)) _ = true
| ord_t _ (OpTerm(g,_,ts)) = false
| ord_t (VarTerm v) (VarTerm v') = V.ord_v v v'
and ord_lex [] _ = true
| ord_lex (t1::t1s) (t2::t2s) =
if TermEq t1 t2 then ord_lex t1s t2s
else ord_t t1 t2
| ord_lex _ [] = false
val vars_of_term = reduceterm (fn (VarTerm s) => [s] | _ => [])
(fn _ => foldl (union V.VarEq) [])
fun issubterm T1 T2 =
let val nT1 = num_ops_in_term T1
fun issub T2 = TermEq T1 T2
orelse (case T2 of
OpTerm (_,n,t2) => n >= nT1 andalso exists issub t2
| _ => false )
in issub T2
end
val occurs = issubterm o mk_VarTerm
fun occurrences_of T2 T1 =
if TermEq T1 T2
then 1
else (case T1 of
OpTerm(f1,n1,t1) => if num_ops_in_term T2 >= n1
then 0
else sum (map (occurrences_of T2) t1)
| VarTerm v => 0
)
local
fun insert_var ((u,n)::rvl) v = if V.VarEq u v
then (u,n+1)::rvl
else (u,n)::insert_var rvl v
| insert_var [] v = [(v,1)]
fun var_counter vl (OpTerm(_,_,ts)) = foldl var_counter vl ts
| var_counter vl (VarTerm v) = insert_var vl v
in
val num_of_vars = var_counter []
end
val linear = forall (eq 1) o map snd o num_of_vars
local
val leq = S.sort_ordered_reflexive o Sig.get_sort_ordering
fun ranks_of sigma f = (O.get_OpSigs o O.operator_sig (Sig.get_operators sigma)) f
handle Error.MERILL_ERROR m => (error_message m ; [] )
in
fun of_sort (sigma : Sig.Signature) (S : S.Sort) (T : Term) =
(case T of
VarTerm v => leq sigma (V.variable_sort v,S)
| OpTerm(f,_,tl) =>
let fun check_sig (ss,s) =
leq sigma (s,S)
andalso
forall_pairs (of_sort sigma) ss tl
val ranks = ranks_of sigma f
in exists (check_sig o O.get_type) ranks
end
)
exception Least_Sort of S.Sort list
fun least_sort sigma (VarTerm v) = V.variable_sort v
| least_sort sigma (OpTerm(f1,n,ts)) =
let val sigs = ranks_of sigma f1
val argsorts = map (least_sort sigma) ts
val leqs = curry (leq sigma)
fun check_ls (os::ros) s cls =
if leqs os s
then os :: ros @ cls
else if leqs s os
then check_ls ros s cls
else check_ls ros s (os::cls)
| check_ls [] s cls = s::cls
fun find_ls cls ((ss,s)::rs) =
if forall_pairs leqs argsorts ss
then find_ls (check_ls cls s []) rs
else find_ls cls rs
| find_ls cls [] = (case cls of
[ls] => ls
| _ => raise (Least_Sort cls)
)
in find_ls [S.Top] (map O.get_type sigs)
end
end
local
fun testS v = S.SortEq (V.variable_sort v) o V.variable_sort
fun testV v = et (V.VarEq v) (testS v)
val lookup = Assoc.assoc_lookup testV
val assoc = Assoc.assoc testV testV
in
fun rename_term T1 env =
( case T1 of
OpTerm (_,_,[]) => (env,T1)
| VarTerm v => (case lookup v env of
NoMatch => let val v' = V.rename_variable v
in
(assoc v v' env,VarTerm v')
end
| Match v' => (env,VarTerm v') )
| OpTerm(f1,n,t1) => let fun gg (hl::rl) env =
let val (env',hl') = rename_term hl env
val (env'',rl') = gg rl env'
in (env'',hl'::rl')
end
| gg [] env = (env,[])
val (env',t1') = gg t1 env
in (env',OpTerm (f1,n,t1'))
end )
fun termmap f = reduceterm f mk_OpTerm
fun alphaequiv t1 t2 env =
( case (t1,t2) of
(VarTerm s,VarTerm t) =>
(case lookup s env of
NoMatch => if testS s t
then (true, assoc s t env)
else (false, env)
| Match gg => (testV gg t , env)
)
| (OpTerm(f,n,t1),OpTerm(g,m,t2)) =>
if O.OpIdeq f g andalso n = m
then let fun gg (a::l1) (b::l2) env =
let val (p,env') = alphaequiv a b env
in if p then gg l1 l2 env'
else (false,env)
end
| gg [] [] E = (true,E)
| gg _ _ E = (false,E)
in
gg t1 t2 env
end
else (false,env)
| _ => (false,env)
)
end
end
fun add_sort_name a term (st,vt) =
(st ^ (if !Display_Level.Show_Sorts
then ":" ^ (S.sort_name (least_sort a term)
handle (Least_Sort ss) => stringlist S.sort_name ("{",",","}") ss)
else "") , vt)
fun unparse_term a VT =
let val Ss = Sig.get_operators a
val Vs = Sig.get_variables a
fun unp_term vt term =
add_sort_name a term
(if compound term
then let val f = root_operator term
val form = (O.display_format Ss f
handle Error.MERILL_ERROR m =>
(error_message m;
let val i = length (subterms term)
in if i = 0 then O.mk_form ["?OP?"]
else O.mk_form (["?OP?("]@
interleave (copy i "_") (copy (i - 1) ",")
@[")"])
end ))
in from_form vt (O.get_form form) (subterms term)
end
else if variable term
then let val vi = get_Variable term
in V.lookup_var_print_env vt Vs vi
end
else raise (Ill_Formed_Term "")
)
and from_form ta [] [] = ("",ta)
| from_form ta [] tss =
let fun ff (t::ts) ta =
let val (st,nta) = unp_term ta t
val (ss,nnta) = ff ts nta
in (st::ss,nnta)
end
| ff [] ta = ([],ta)
val (tss',nta) = ff tss ta
in (stringwith ("(",",",")") tss',nta)
end
| from_form ta (f1::fs) [] =
if f1 = ""
then failwith "ill formed template"
else let val (st,nta) = from_form ta fs []
in (f1 ^ st,nta)
end
| from_form ta (f1::fs) (t::ts) =
if f1 = ""
then let val (st,nta) = unp_term ta t
val (ss,nnta) = from_form nta fs ts
in (if ou constant variable t
then " " ^ st ^ " "^ss
else "(" ^ st ^ ")"^ss,nnta)
end
else if ((f1 = "(" orelse f1 = ",")
andalso hd fs = "" handle Hd => false )
then let val (st,nta) = unp_term ta t
val (ss,nnta) = from_form nta (tl fs) ts
in (f1 ^ st^ss,nnta)
end
else let val (st,nta) = from_form ta fs (t::ts)
in (f1 ^ st,nta)
end
in unp_term VT
end
fun add_pretty_sort_name a term st =
if !Display_Level.Show_Sorts
then Pretty.blo(2, [st, Pretty.str ":", Pretty.str (S.sort_name (least_sort a term)
handle (Least_Sort ss) => stringlist S.sort_name ("{",",","}") ss)])
else st
fun pretty_term Sigma VT t =
let val Ss = Sig.get_operators Sigma
val Vs = Sig.get_variables Sigma
in if variable t
then let val vi = get_Variable t
in apply_fst Pretty.str (add_sort_name Sigma t (V.lookup_var_print_env VT Vs vi))
end
else
if compound t
then let val f = root_operator t
val pform = (O.pretty_form Ss f
handle Error.MERILL_ERROR m =>
(error_message m;
let val i = length (subterms t)
in if i = 0 then K(Pretty.str "?OP?")
else fn ps =>
Pretty.blo (4, [Pretty.str"(?OP?("]@
interleave
(interleave ps (copy (i - 1) (Pretty.str ",")))
(copy (i - 1) (Pretty.brk 1))
@[Pretty.str "))"])
end ))
val (subps,vt) = fold_through Sigma VT (subterms t)
in (add_pretty_sort_name Sigma t (pform subps), vt)
end
else raise (Ill_Formed_Term "")
end
and fold_through Sigma VT (t::ts) =
let val (p,vt) = pretty_term Sigma VT t
val (ps,vt) = fold_through Sigma vt ts
in (p::ps,vt)
end
| fold_through _ v [] = ([],v)
fun show_term Sigma = fst o (unparse_term Sigma V.Empty_Var_Print_Env)
fun show_pretty_term Sigma = fst o (pretty_term Sigma V.Empty_Var_Print_Env)
fun parse_term Al TS env ss =
(case (TranSys.traverse TS
(fn e => fn sl =>
V.variable_parser (Sig.get_sorts Al) (Sig.get_variables Al) e sl
eachM apply_fst (apply_fst mk_VarTerm) )
env [] ss )
of
OK ((t,sl) , e') =>
let val s = least_sort Al t
handle Least_Sort fid =>
(warning_message ("No Unique Least Sort for Term "^show_term Al t);
S.Bottom)
in if S.SortEq s S.Top
then warning_message ("No Defined Sort for Term "^show_term Al t^" Takes Sort Top.")
else () ;
OK ((t,sl) , e')
end
| Error s => Error s
)
end
end
;