functor VariableFUN (structure S:SORT
):VARIABLE =
struct
type Sort = S.Sort
type Sort_Store = S.Sort_Store
abstype Variable = Va of int * string * Sort
with
local
val var_num = ref 0
in
fun variable_sort (Va (_,_,s)) = s
fun variable_label (Va (_,v,_)) = v
fun generate_variable s = (inc var_num ; Va (!var_num,"",s))
fun make_variable v s = (inc var_num ; Va (!var_num,v,s))
fun rename_variable (Va (_,st,s)) = (inc var_num ; Va (!var_num,st,s))
fun VarEq (Va(n,_,s)) (Va(n',_,s')) = n = n' andalso S.SortEq s s'
fun ord_v (Va(n,_,_)) (Va(n',_,_)) = n <= n'
end
end ;
local
structure VOL = OrdList2FUN (struct type T = string * Sort
fun order (s1,_) (s2,_) = stringorder s1 s2
end)
open VOL
in
abstype Variable_Store = Vars of OrdList * OrdList
with
val Empty_Variable_Store = Vars (EMPTY,EMPTY)
fun declare_variable (Vars(Wholes,Prefixes)) (v,s) =
(case lookup Wholes (v,s) of
Match (v',s') => if S.SortEq s s' then Vars(Wholes,Prefixes)
else (warning_message("Variable "^v^" already declared with different sort.  Overwriting.");
Vars(insert (remove Wholes (v,s)) (v,s) , Prefixes) )
| NoMatch => Vars(insert Wholes (v,s) , Prefixes) )
fun checkprefix (v,s) (v',s') = not (v = v' andalso S.SortEq s s')
andalso (initial_substring v v' orelse initial_substring v' v )
fun declare_prefix (Vars(Wholes,Prefixes)) (v,s) =
(case search checkprefix Prefixes (v,s) of
Match (v',s') => (error_message ("prefix "^v^" clashes with prefix "^v');
Vars(Wholes , Prefixes) )
| NoMatch => Vars(Wholes , insert Prefixes (v,s)) )
local
fun checkwhole v (v',s') = v = v'
in
fun read_variable (Vars(Wholes,Prefixes)) v =
let fun checkprefix v (v',s') = initial_substring v v'
in case (case search checkwhole Wholes v of
NoMatch => search checkprefix Prefixes v
| x => x )
of
NoMatch => NoMatch |
Match (_,s) => Match (make_variable v s)
end
fun delete_variable (Vars(Wholes,Prefixes)) v =
Vars(remove Wholes (v,S.Top), remove Prefixes (v,S.Top))
end
local
fun check s (v,s') = S.SortEq s s'
in
fun display_variable (Vars(Wholes,Prefixes)) Var =
let val st = variable_label Var
val s = variable_sort Var
in
if st = "" then
fst (case search check Prefixes s of
Match v => v |
NoMatch => (case search check Wholes s of
Match v => v |
NoMatch => ("?:"^S.sort_name s,s)
)
)
else st
end
end
fun names_of_vars (Vars(Wholes,Prefixes)) =
apply_both (List.map (apply_snd S.sort_name)) (Wholes,Prefixes)
fun variable_parser SS VS env sl =
let val ss = (strip sl)
in if null ss then Error "No More Input For Variable"
else
let val (v,rs) = (hd ss,tl ss) in
case Assoc.assoc_lookup eq v env of
Match var => OK ((var,strip rs),env)
| NoMatch =>
(case read_variable VS v of
Match var => OK ((var,strip rs),Assoc.assoc_nocheck v var env)
| NoMatch =>
let val ss = (strip rs)
in if null ss
then Error (v^" not recognised as Variable.")
else let val (c,rss) = (hd ss, tl ss) in
if c = ":" then
let val rss = (strip rss) in
if null rss then
Error (v^" not recognised as Variable.")
else let val (s,rss) = (hd rss,tl rss)
val so = S.name_sort s
in
if S.is_declared_sort SS so
then let val var = generate_variable so
in
OK ((var,rss),Assoc.assoc_nocheck v var env)
end
else Error (v^" not recognised as Variable.")
end
end
else
Error (v^" not recognised as Variable.")
end
end )
end
end
end
end
abstype Variable_Print_Env =
VPEnv of (string , int * (Variable, string) Assoc.Assoc) Assoc.Assoc
with
local
val lookup_string = Assoc.assoc_lookup (eq : string -> string -> bool)
val update_string = Assoc.assoc_update (eq : string -> string -> bool)
val lookup_var = Assoc.assoc_lookup VarEq
val update_var = Assoc.assoc_update VarEq
in
val Empty_Var_Print_Env = VPEnv Assoc.Empty_Assoc
fun lookup_var_print_env (VPEnv vt) Vs vi =
let val vs = display_variable Vs vi
in
case lookup_string vs vt of
NoMatch => (vs,VPEnv(update_string vs (1,
update_var vi "" Assoc.Empty_Assoc) vt))
| Match (n,va) => (case lookup_var vi va of
NoMatch => (vs^ makestring n,
VPEnv(update_string vs
(n+1,update_var vi (makestring n) va) vt))
| Match ss => (vs^ss,VPEnv vt))
end
end
end
end
;
