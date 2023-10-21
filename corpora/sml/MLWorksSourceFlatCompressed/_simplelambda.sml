require "../basis/__int";
require "../utils/crash";
require "../utils/lists";
require "../utils/inthashtable";
require "../utils/hashtable";
require "../utils/bignum";
require "../utils/mlworks_timer";
require "../main/pervasives";
require "../basics/scons";
require "../main/options";
require "lambdaprint";
require "lambdaflow";
require "transsimple";
require "simpleutils";
require "lambdaoptimiser";
require "^.basis.__string";
functor SimpleLambda (include
sig
structure Crash : CRASH
structure Lists : LISTS
structure Timer : INTERNAL_TIMER
structure Scons : SCONS
structure Bignum : BIGNUM
structure Options : OPTIONS
structure Pervasives : PERVASIVES
structure IntHashTable : INTHASHTABLE
structure HashTable : HASHTABLE
structure TransSimple : TRANSSIMPLE
structure SimpleUtils : SIMPLEUTILS
structure LambdaPrint : LAMBDAPRINT
structure LambdaFlow : LAMBDAFLOW
sharing TransSimple.LambdaTypes = SimpleUtils.LambdaTypes = LambdaFlow.LambdaTypes =
LambdaPrint.LambdaTypes
sharing type TransSimple.LambdaTypes.Primitive = Pervasives.pervasive
sharing type TransSimple.LambdaTypes.Ident.SCon = Scons.SCon
end where type TransSimple.LambdaTypes.LVar = int
) : LAMBDAOPTIMISER =
struct
structure LambdaTypes = TransSimple.LambdaTypes
structure Ident = LambdaTypes.Ident
structure Location = Ident.Location
structure LambdaTypes = LambdaTypes
structure Options = Options
open LambdaTypes
val N = Int.toString
val inline_functors = false
val null_debug = NONE
val null_type = LambdaTypes.null_type_annotation
val do_diag = false
val diag_level = 5
val do_print = false
val print_level = 1
val print_result = do_diag andalso false
val num_args_limit = 5
val print_timings = ref false
fun diag (level,f) =
if do_diag
then if level <= diag_level then print (f()) else ()
else ()
fun ddiag (level,f) =
if level <= diag_level then print (f()) else ()
val do_cse = true
val max_depth = 4
val inline_size = 10
val unsigned_array_test = true
val do_imperative_cse = true
val do_loop_functions = true
val do_curry_transform = false
val nulltype = NONE
fun make_call_ty ty = NONE
val crash = Crash.impossible
fun is_absent NONE = true
| is_absent _ = false
fun max ([],acc) = acc
| max ((a:int)::b,acc) = max (b,if a > acc then a else acc)
fun optfun f (SOME x) = SOME (f x)
| optfun f NONE = NONE
fun varlist_member (var:LVar,[]) = false
| varlist_member (var:LVar,var':: rest) = var = var' orelse varlist_member (var,rest)
fun varassoc (_, []) = NONE
| varassoc (v:int, ((v',a) :: rest)) =
if v = v' then SOME a else varassoc (v,rest)
fun varassoc' (v,l) =
case varassoc (v,l) of
SOME x => x
| _ => crash "var not found in varassoc'"
fun hashmap_find (v,env) = IntHashTable.tryLookup (env,v)
fun hashmap_member (v,env) =
IntHashTable.is_defined (env,v)
fun list_to_hashmap l =
let
val map = IntHashTable.new (length l)
in
Lists.iterate (fn (n,x) => IntHashTable.update (map,n,x)) l;
map
end
fun empty_hashmap () =
IntHashTable.new 16
fun lookup (e,defns) =
let
fun aux (VAR v,defns,n) =
if n > 100
then NONE
else
(case hashmap_find (v,defns) of
SOME (VAL (e as VAR _)) => aux (e,defns,n+1)
| SOME (VAL e) => SOME e
| x => NONE)
| aux (e,_,_) = SOME e
in
aux (e,defns,0)
end
fun is_inlineable (e,depth)=
SimpleUtils.size_less (e,inline_size - (depth + depth),false)
fun is_curry_inlineable (LET ((_,_,e1),FN (_,e,_,_,_,_)),depth) =
is_inlineable (e1,depth) andalso is_curry_inlineable (e,depth)
| is_curry_inlineable (LET ((_,_,e1),LET ((_,_,e2),FN (_,e,_,_,_,_))),depth) =
is_inlineable (e1,depth) andalso is_inlineable (e2,depth) andalso is_curry_inlineable (e,depth)
| is_curry_inlineable (FN (_,e,_,_,_,_),depth) = is_curry_inlineable (e,depth)
| is_curry_inlineable (e,depth) = is_inlineable (e,depth)
fun lsubst (binds,e) =
SimpleUtils.subst
(fn v =>
case hashmap_find (v,binds) of
SOME e => e
| NONE => VAR v,
e)
fun alpha (binds,e) =
let
fun aux (e as INT _) = e
| aux (e as SCON _) = e
| aux (e as MLVALUE _) = e
| aux (e as BUILTIN _) = e
| aux (e as VAR v) =
(case hashmap_find (v,binds) of
SOME e' => e'
| NONE => e)
| aux (APP (e,(el,fpel),ty)) = APP (aux e,(map aux el,map aux fpel), ty)
| aux (STRUCT (el,ty)) = STRUCT (map aux el,ty)
| aux (SWITCH (e,info,tel,opte)) =
SWITCH (aux e,
info,
map (telfun aux) tel,
optfun aux opte)
| aux (HANDLE (e1,e2,s)) = HANDLE(aux e1,aux e2,s)
| aux (RAISE e) = RAISE (aux e)
| aux (SELECT (info,e)) = SELECT (info,aux e)
| aux (LET ((v,i,e1),e2)) =
let
val v' = new_LVar ()
val e1' = aux e1
val _ = IntHashTable.update (binds,v,VAR v')
in
LET((v',i,e1'),aux e2)
end
| aux (FN ((vl,fpvl),body,status,name,ty,info)) =
let
val new_vl = map (fn v => (v,new_LVar ())) vl
val new_fpvl = map (fn v => (v,new_LVar ())) fpvl
val _ =
Lists.iterate
(fn (v,v') => IntHashTable.update (binds,v,VAR v'))
(new_vl @ new_fpvl)
in
FN ((map #2 new_vl,map #2 new_fpvl),aux body,status,name,ty,info)
end
| aux (LETREC (fl,el,e)) =
let
val fl' = map (fn (v,info) => (new_LVar(),info)) fl
val _ =
Lists.iterate
(fn ((v,_),(v',_)) => IntHashTable.update (binds,v,VAR v'))
(Lists.zip (fl,fl'))
in
LETREC (fl',map aux el,aux e)
end
in
aux e
end
fun rename e = alpha (empty_hashmap () ,e)
fun wrap_lets ([],e) = e
| wrap_lets ((v,i,e)::vel,e') =
wrap_lets (vel, LET ((v,i,e),e'))
fun unwrap_lets e =
let
fun aux (LET ((v,i,e1),e2),acc) =
aux (e2,(v,i,e1)::acc)
| aux (e,acc) = (e,acc)
in
aux (e,[])
end
fun find_selects (arg,e) =
let
fun find (e as LET ((v,i,e' as SELECT (_,VAR v')),body), acc) =
if v' = arg
then find (body,(v,i,e')::acc)
else (acc,e)
| find (e,acc) = (acc,e)
in
find (e,[])
end
fun curry_transform ([arg],e) =
let
fun lift (FN (vl,body,status,name,ty,info)) =
let
val (selects,body') = lift body
in
(selects,FN (vl,body',status,name,ty,info))
end
| lift (LET ((v,i,e1 as SELECT (_,VAR x')),body)) =
let
val (selects,body') = lift body
in
if arg = x'
then ((v,i,e1)::selects,body')
else (selects,LET ((v,i,e1),body'))
end
| lift (LET ((v,i,e1),body)) =
let
val (selects,body') = lift body
in
(selects,LET ((v,i,e1),body'))
end
| lift e = ([],e)
val (selects,e') = lift e
in
wrap_lets (rev selects,e')
end
| curry_transform (vl,e) = crash "Multi arg function in curry_transform"
val curry_transform =
fn (args,e) =>
if do_curry_transform then curry_transform (args,e)
else e
fun get_returned_exp (LET (_,e)) = get_returned_exp e
| get_returned_exp e = e
fun sub_returned_exp (e',e) =
let
fun aux (LET((v,i,e1),e2)) = LET((v,i,e1),aux e2)
| aux e = e'
in
aux e
end
fun letmap expfun e =
let
fun letmap_aux (LET ((x,i,e1),e2),acc) =
letmap_aux (e2,(x,i,letmap expfun e1)::acc)
| letmap_aux (SWITCH (e,info,tel,opte),acc) =
wrap_lets (acc,
SWITCH (letmap expfun e,
info,
map
(fn (EXP_TAG e1,e2) => (EXP_TAG (letmap expfun e1), letmap expfun e2)
| (t,e2) => (t, letmap expfun e2))
tel,
case opte of
SOME e => SOME (letmap expfun e)
| _ => NONE))
| letmap_aux (HANDLE (e1,e2,s),acc) =
wrap_lets (acc,
HANDLE (letmap expfun e1,letmap expfun e2,s))
| letmap_aux (FN (vl,e,b,name,ty,info),acc) =
wrap_lets (acc, FN (vl,letmap expfun e,b,name,ty,info))
| letmap_aux (LETREC (fl,el,e),acc) =
wrap_lets (acc, (LETREC (fl,map (letmap expfun) el,letmap expfun e)))
| letmap_aux (e,acc) = wrap_lets (acc,expfun e)
in
letmap_aux (e,[])
end
fun letiterate f e =
let
fun aux (LET ((v,i,e1),e2)) = (aux e1; aux e2)
| aux (FN (vl,e,_,_,_,_)) = aux e
| aux (SWITCH (e,info,tel,opte)) =
(aux e;
Lists.iterate (fn (EXP_TAG et,e) => (aux et;aux e) | (t,e) => aux e) tel;
ignore(optfun aux opte);
())
| aux (HANDLE (e1,e2,s)) = (aux e1; aux e2)
| aux (LETREC (fl,el,e)) =
(Lists.iterate aux el;aux e)
| aux e = f e
in
aux e
end
fun is_safe_to_lift (SELECT ({selecttype=STRUCTURE,...},_)) = true
| is_safe_to_lift (SELECT _) = false
| is_safe_to_lift _ = true
fun lift_globalp (e,dyn_vars,in_switch) =
not (SimpleUtils.is_atomic e) andalso
SimpleUtils.is_simple e andalso
(not in_switch orelse is_safe_to_lift e) andalso
SimpleUtils.safe e andalso
SimpleUtils.freevars (e,dyn_vars) = []
fun globalize_simple (e,env,dyn_vars,in_switch) =
if SimpleUtils.is_simple e
then (e,env)
else globalize' (e,env,dyn_vars,in_switch)
and globalize' (LET ((v,i,e1),e2),env,dyn_vars,in_switch) =
let
val (e1',env') = globalize_simple (e1,env,dyn_vars,in_switch)
in
if lift_globalp (e1',dyn_vars,in_switch)
then
let
val _ = diag (3, fn () => "Found global 1: " ^ LambdaPrint.pde e1' ^ "\n")
in
globalize' (e2, (v,i,e1')::env',dyn_vars,in_switch)
end
else
let
val (e2',env'') = globalize' (e2,env',v::dyn_vars,in_switch)
in
(LET ((v,i,e1'),e2'),env'')
end
end
| globalize' (FN (([x],[]),body,status,name,ty,info),env,dyn_vars,in_switch) =
(case status of
FUNC =>
let
val body' = globalize (body,[],dyn_vars,in_switch)
val id = new_LVar()
in
(FN (([x],[]),body',status,name,ty,info),env)
end
| _ =>
let
val (body',env') = globalize' (body,env,x::dyn_vars,in_switch)
in
(FN (([x],[]),body',BODY,name,ty,info),env')
end)
| globalize' (FN _,env,dyn_vars,in_switch) = crash "Multi arg FN in globalize'"
| globalize' (LETREC (fl,el,body),env,dyn_vars,in_switch) =
let
val fnnames = map #1 fl
val dyn_vars' = fnnames@dyn_vars
fun do_el (FN ((vl,fpvl),e,status,name,ty,info)::rest,env,acc) =
let
val (e',env') = globalize' (e,env,fpvl@vl@dyn_vars',in_switch)
in
do_el (rest,env',FN ((vl,fpvl),e',status,name,ty,info)::acc)
end
| do_el (_::rest,env,acc) = crash "Bad fn in letrec in globalize"
| do_el ([],env,acc) = (rev acc,env)
val (el',env') = do_el (el,env,[])
val (body',env'') = globalize' (body,env',dyn_vars',in_switch)
in
(LETREC (fl,el',body'),env'')
end
| globalize' (SWITCH (e,info,tel,opte),env,dyn_vars,in_switch) =
let
val (e',env1) = globalize_simple (e,env,dyn_vars,in_switch)
fun do_tel ([],env) = ([],env)
| do_tel ((EXP_TAG e1,e)::l,env) =
let
val (e',env') = globalize' (e,env,dyn_vars,true)
val (e1',env'') = globalize' (e1,env',dyn_vars,true)
val (tel',env''') = do_tel (l,env'')
in
((EXP_TAG e1',e') :: tel', env''')
end
| do_tel ((t,e)::l,env) =
let
val (e',env') = globalize' (e,env,dyn_vars,true)
val (tel',env'') = do_tel (l,env')
in
((t,e') :: tel', env'')
end
val (tel',env2) = do_tel (tel,env1)
val (opte',env3) =
case opte of
SOME e =>
let val (e',env) = globalize' (e,env2,dyn_vars,true)
in
(SOME e',env)
end
| NONE => (NONE,env2)
in
(SWITCH (e',info,tel',opte'),env3)
end
| globalize' (HANDLE (e1,e2,s),env,dyn_vars,in_switch) =
let
val (e1',env') = globalize' (e1,env,dyn_vars,in_switch)
val (e2',env'') = globalize' (e2,env',dyn_vars,in_switch)
in
(HANDLE (e1',e2',s),env'')
end
| globalize' (e,env,dyn_vars,in_switch) =
if lift_globalp (e,dyn_vars,in_switch)
then
let
val _ = diag (3, fn () => "Found global 2\n")
val new_var = new_LVar ()
in
(VAR new_var,(new_var,null_debug,e)::env)
end
else
(e,env)
and globalize (e,env,dyn_vars,in_switch) =
let
val (e',lets) = globalize' (e,env,dyn_vars,in_switch)
in
wrap_lets (lets,e')
end
fun case_transform arg = arg
fun return_transform arg = arg
fun select_transform (arg,dyn_vars,name,(body,env)) =
let
val (selects,newbody) = find_selects (arg,body)
val bid = new_LVar ()
val selvars = map #1 selects
val fvars = SimpleUtils.freevars (newbody,arg :: (selvars @ dyn_vars))
in
(wrap_lets (selects, APP (VAR bid, (map VAR fvars,[]),nulltype)),
return_transform ((bid,null_debug,
VAL (FN ((fvars,[]),newbody,BODY,name,null_type,LambdaTypes.internal_funinfo))) :: env))
end
fun mk_tuple_select (index,size,e) = SELECT ({index=index,size=size,selecttype=TUPLE}, e)
fun insert_closure_selections (vars,closid,body) =
let
val len = length vars
val count = ref 0
fun next () = let val result = !count in count := result + 1; result end
val binds =
map (fn var => (var,null_debug, mk_tuple_select (next(),len,VAR closid))) vars
in
SimpleUtils.insert_as_needed (binds,body)
end
fun make_closure_function (f,closure,name) =
let
val x = new_LVar ()
in
FN (([x],[]),
APP (VAR f,([closure,VAR x],[]),NONE),
ENTRY,
name ^ "<Closure>",
null_type,
LambdaTypes.internal_funinfo)
end
fun transform (func as (FN (([x],[]),body,status,name,ty,info)),env,dyn_vars,global) =
(case status of
FUNC =>
let
val _ = if not global then crash "Functor not global" else ()
val _ = if not (dyn_vars = []) then crash "Functor has dynamic vars" else ()
val (body',env') = transform (body,[],[],true)
val id = new_LVar()
in
(VAR id, (id,null_debug,FUNCTOR (x,name,(rev env',body'))) :: env)
end
| _ =>
let
val f = new_LVar ()
in
transform (LETREC ([(f,null_debug)],[func],VAR f),env,dyn_vars,global)
end)
| transform (FN _,env,dyn_vars,_) = crash "Multi arg FN in transform"
| transform (LETREC (fl,el,body),env,dyn_vars,global) =
let
val fvel =
map
(fn ((f,_),FN ((vl,[]),body,_,name,_,info)) => (f,vl,name,body)
| _ => crash "Bad fn in letrec in transform")
(Lists.zip (fl,el))
val fvel = map (fn (f,vl,name,body) => (f,vl,name,curry_transform (vl,body))) fvel
val fvars = SimpleUtils.freevars (LETREC (fl,el,INT 0),dyn_vars)
in
if fvars = []
then
let
val funnames = map #1 fvel
fun make_bodies ([],env) = env
| make_bodies ((f,[x],name,fbody)::l,env) =
let
val (fbody',env') =
select_transform (x,[x],name,transform (fbody,env,x::dyn_vars,false))
val result =
(f,null_debug,VAL (FN(([x],[]),fbody',ENTRY,name^"<Entry1>",null_type,LambdaTypes.internal_funinfo))) :: env'
in
make_bodies (l,result)
end
| make_bodies _ = crash "Can't do multiple args in letrec yet"
val env' = make_bodies (fvel,env)
in
transform (body,env',dyn_vars,global)
end
else
let
fun letrec_trans (exp,nameidmap,closexpr) =
let
fun expaux (e as APP (VAR f,([y],[]),ty)) =
(case varassoc (f,nameidmap) of
SOME f' => APP (VAR f',([closexpr,y],[]),make_call_ty ty)
| NONE => e)
| expaux e = e
in
letmap expaux exp
end
fun is_loop_function (f,_,s,e) =
let
fun has_self_tail_call e =
case e of
APP (VAR f',_,_) => f = f'
| SWITCH (e,info,tel,opte) =>
Lists.exists (fn (t,e) => has_self_tail_call e) tel
orelse
(case opte of
SOME e => has_self_tail_call e
| _ => false)
| LET (_,e) => has_self_tail_call e
| LETREC (_,_,e) => has_self_tail_call e
| _ => false
val result = has_self_tail_call e
in
if result then diag (2, fn () => "Doing " ^ s ^ " as loop function\n") else ();
result
end
val loop_function = do_loop_functions andalso Lists.forall is_loop_function fvel
val funnames = map #1 fvel
val funids = map (fn _ => new_LVar ()) funnames
val idtids = map (fn id => (id,new_LVar ())) funids
val funmap = Lists.zip (funnames,funids)
val funnamemap = Lists.zip (funmap,map #3 fvel)
val closid =
case fvars of
[x] => x
| _ => new_LVar()
val max_num_args =
let
fun get_num_args (f,[v],_,e as LET ((_,_,SELECT ({size,...},VAR v')),_)) =
if v = v' then size else if SimpleUtils.occurs (v,e) then 1 else 0
| get_num_args (f,[v],_,e) = if SimpleUtils.occurs (v,e) then 1 else 0
| get_num_args _ = crash "too many vars in get_num_args"
in
max (map get_num_args fvel,0)
end
val fvel = map (fn (f,vl,name,e) => (f,vl,name,letrec_trans (e, funmap,VAR closid))) fvel
val no_escapers =
let
val dummy_body = STRUCT (map (fn (f,vl,name,e)=> e) fvel,TUPLE)
val escapers = SimpleUtils.freevars (dummy_body,funnames)
in
escapers = []
end
val fvel =
if no_escapers
then fvel
else
let
val subst =
list_to_hashmap
(map
(fn ((f,f'),name) => (f,make_closure_function (f',VAR closid,name)))
funnamemap)
in
map (fn (f,vl,name,e) => (f,vl,name,lsubst (subst,e))) fvel
end
val inline_recs =
letmap (fn (e as APP (VAR id,([VAR c,y],[]),nulltype)) =>
if Lists.member (id,funids) andalso c = closid
then
APP (VAR (varassoc' (id,idtids)), ((map VAR fvars) @ [y],[]),nulltype)
else e
| e => e)
val inline_closure =
no_escapers andalso
(max_num_args = 0 orelse
loop_function orelse
max_num_args + length fvars <= num_args_limit)
fun make_bodies ([],env) = env
| make_bodies ((id,(f,[x],name,fbody))::l,env) =
if inline_closure
then
let
val vars = fvars @ [x]
val newbody = inline_recs fbody
val (newbody',env') =
select_transform (x, closid :: vars,name,transform (newbody,env,vars @ dyn_vars,false))
val tid = varassoc' (id,idtids)
val entrybody =
case fvars of
[y] =>
(if x = y then crash "Bad id in transform" else ();
APP(VAR tid,([VAR y,VAR x],[]),nulltype))
| _ => insert_closure_selections (fvars,closid,APP(VAR tid,(map VAR vars,[]),nulltype))
val result =
(id,null_debug,VAL (FN(([closid,x],[]),entrybody,ENTRY,name^"<Entry1>",null_type,LambdaTypes.internal_funinfo))) ::
(tid,null_debug,VAL (FN ((vars,[]),newbody',ENTRY,name^"<Entry2>",null_type,LambdaTypes.internal_funinfo))) ::
env'
in
make_bodies (l,result)
end
else
let
fun insert_select (e as LET ((v,i,e1 as SELECT (_,VAR id)),e2)) =
(if Lists.member(id,fvars)
then insert_closure_selections (fvars,closid,e)
else LET ((v,i,e1),insert_select e2))
| insert_select e = insert_closure_selections (fvars,closid,e)
val vars = fvars@[x]
val newbody =
case fvars of
[_] => fbody
| _ => insert_select fbody
val (newbody',env') =
select_transform (x,[x,closid],name,transform (newbody,env,[x,closid]@dyn_vars,false))
val result =
(id,
null_debug,
VAL (FN (([closid,x],[]),newbody',ENTRY,name^"<Entry1>",null_type,LambdaTypes.internal_funinfo))) :: env'
in
make_bodies (l,result)
end
| make_bodies ((id,(f,xl,name,e))::l,env) =
crash "Can't do multiple args in letrec yet"
fun make_lets ([],closexp,e) = e
| make_lets (((f,id),name)::l,closexp,e) =
LET ((f,null_debug,make_closure_function (id,closexp,name)),make_lets (l,closexp,e))
val clos_expression =
case fvars of
[x] => VAR x
| _ => STRUCT (map VAR fvars,TUPLE)
val body' = letrec_trans (body,funmap,VAR closid)
val body'' = if inline_closure then inline_recs body' else body'
val env' = make_bodies(Lists.zip (funids,fvel),env)
val (body''',env'') = transform (body'',env',closid::(funnames@dyn_vars),false)
in
(LET ((closid,null_debug,clos_expression),
make_lets (funnamemap,VAR closid,body''')),
env'')
end
end
| transform (LET ((v,i,e1 as FN (vl,body,BODY,name,ty,info)),e2),env,dyn_vars,global) =
transform (LETREC ([(v,i)],[e1],e2),env,dyn_vars,global)
| transform (LET ((v,i,e1 as FN (vl,body,ENTRY,name,ty,info)),e2),env,dyn_vars,global) =
transform (LETREC ([(v,i)],[e1],e2),env,dyn_vars,global)
| transform (LET ((v,i,e1),e2),env,dyn_vars,true) =
let
val (e1',env') = transform (e1,env,dyn_vars,true)
val (e2',env'') = transform(e2,(v,i,VAL e1')::env',dyn_vars,true)
in
(e2',env'')
end
| transform (LET ((v,i,e1),e2),env,dyn_vars,false) =
let
val (e1',env') = transform (e1,env,dyn_vars,false)
fun diddle_lets (LET ((v1,i1,LET ((v2,i2,e2),e1)),body)) =
diddle_lets (LET ((v2,i2,e2),LET((v1,i1,e1),body)))
| diddle_lets (LET ((v1,i1,e1),body)) =
LET ((v1,i1,e1),diddle_lets body)
| diddle_lets e = e
in
let
val (e2',env'') = transform(e2,env',v::dyn_vars,false)
in
(diddle_lets (LET ((v,i,e1'),e2')),env'')
end
end
| transform (SWITCH (e,info,tel,opte),env,dyn_vars,global) =
let
val (e',env1) = transform (e,env,dyn_vars,global)
fun do_tel ([],env) = ([],env)
| do_tel ((t,e)::l,env) =
let
val (e',env') = transform (e,env,dyn_vars,false)
val (tel',env'') = do_tel (l,env')
in
((t,e') :: tel', env'')
end
val (tel',env2) = do_tel (tel,env1)
val (opte',env3) =
case opte of
SOME e =>
let val (e',env) = transform (e,env2,dyn_vars,false)
in
(SOME e',env)
end
| NONE => (NONE,env2)
in
(SWITCH (e',info,tel',opte'),env3)
end
| transform (HANDLE (e1,e2,s),env,dyn_vars,global) =
let
val (e1',env') = transform (e1,env,dyn_vars,false)
val (e2',env'') = transform (e2,env',dyn_vars,global)
in
(HANDLE (e1',e2',s),env'')
end
| transform (e,env,_,_) = (e,env)
fun rename_all declist =
let
fun decfun (VAL e) = VAL (rename e)
| decfun (FUNCTOR (var,name,(declist,e))) =
FUNCTOR (var,name,(rename_all declist,e))
in
map (fn (var,info,dec) => (var,info,decfun dec)) declist
end
val transform =
fn args =>
let val (e,decs) = transform args
in
(e,rename_all decs)
end
fun mkif (test,e1,e2) =
SWITCH (test,SOME {num_vccs = 0,num_imms= 2},
[((IMM_TAG ("true",1)),e1),
((IMM_TAG ("false",0)),e2)],
NONE)
fun make_subtest (e1,e2,lenfun,result) =
let
val lenvar = new_LVar ()
val raiseexp =
SimpleUtils.linearize
(RAISE (STRUCT ([BUILTIN Pervasives.EXSUBSCRIPT,
STRUCT ([],TUPLE)],
CONSTRUCTOR)))
in
if unsigned_array_test
then
LET ((lenvar,NONE,APP (BUILTIN lenfun,([e1],[]),nulltype)),
mkif (APP (BUILTIN Pervasives.WORDGE,([e2,VAR lenvar],[]),NONE),
raiseexp,
result))
else
LET ((lenvar,NONE,APP (BUILTIN lenfun,([e1],[]),nulltype)),
mkif (APP (BUILTIN Pervasives.INTGREATEREQ,([e2,VAR lenvar],[]),NONE),
raiseexp,
mkif (APP (BUILTIN Pervasives.INTLESS,
([e2,SCON (Ident.INT ("0",Location.UNKNOWN),NONE)],[]),
NONE),
raiseexp,
result)))
end
fun do_builtinapp ((Pervasives.CAST,[e],ty),env) = SOME e
| do_builtinapp ((Pervasives.IDENT_FN,[e],ty),env) = SOME e
| do_builtinapp ((Pervasives.NOT,[e],ty),env) =
SOME (mkif (e,INT 0, INT 1))
| do_builtinapp ((Pervasives.O,[e1,e2],ty),env) =
let
val arg = new_LVar ()
in
SOME (FN (([arg],[]),
SimpleUtils.linearize (APP (e1,([APP (e2,([VAR arg],[]),NONE)],[]),NONE)),
BODY,
"Builtin compose",
null_type,
LambdaTypes.internal_funinfo))
end
| do_builtinapp ((Pervasives.SUB,[e1,e2],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.LENGTH,
APP (BUILTIN Pervasives.UNSAFE_SUB,([e1,e2],[]),ty)))
| do_builtinapp ((Pervasives.BYTEARRAY_SUB,[e1,e2],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.BYTEARRAY_LENGTH,
APP (BUILTIN Pervasives.BYTEARRAY_UNSAFE_SUB,([e1,e2],[]),ty)))
| do_builtinapp ((Pervasives.FLOATARRAY_SUB,[e1,e2],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.FLOATARRAY_LENGTH,
APP (BUILTIN Pervasives.FLOATARRAY_UNSAFE_SUB,([e1,e2],[]),ty)))
| do_builtinapp ((Pervasives.UPDATE,[e1,e2,e3],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.LENGTH,
APP (BUILTIN Pervasives.UNSAFE_UPDATE,([e1,e2,e3],[]),ty)))
| do_builtinapp ((Pervasives.BYTEARRAY_UPDATE,[e1,e2,e3],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.BYTEARRAY_LENGTH,
APP (BUILTIN Pervasives.BYTEARRAY_UNSAFE_UPDATE,([e1,e2,e3],[]),ty)))
| do_builtinapp ((Pervasives.FLOATARRAY_UPDATE,[e1,e2,e3],ty),env) =
SOME (make_subtest (e1,e2,Pervasives.FLOATARRAY_LENGTH,
APP (BUILTIN Pervasives.FLOATARRAY_UNSAFE_UPDATE,([e1,e2,e3],[]),ty)))
| do_builtinapp ((b,el,ty),env) =
let
exception Foo
fun power2 n =
if n <= 0
then NONE
else
let
fun aux (1,acc) = SOME acc
| aux (n,acc) =
if n mod 2 = 0
then aux (n div 2,acc+1)
else NONE
in
aux (n,0)
end
fun get_val e =
case lookup (e,env) of
SOME (e as INT _) => e
| SOME (e as SCON _) => e
| _ => raise Foo
fun make_int_scon (n,location) =
SCON (Ident.INT (N n,location), NONE)
fun make_bignum_scon (n,location) =
SCON (Ident.INT (Bignum.bignum_to_string n,location), NONE)
fun make_string_scon s =
SCON (Ident.STRING s, NONE)
fun scon_to_bignum(Ident.INT(n, _)) =
if String.isPrefix "0x" n orelse String.isPrefix "~0x" n then
Bignum.hex_string_to_bignum n
else Bignum.string_to_bignum n
| scon_to_bignum(Ident.CHAR s) =
Bignum.int_to_bignum(ord (String.sub(s, 0)))
| scon_to_bignum _ = crash "scon_to_bignum"
fun scon_location (Ident.INT(_,loc)) = loc
| scon_location (Ident.REAL(_,loc)) = loc
| scon_location _ = Location.UNKNOWN
fun scon_to_int s =
Bignum.bignum_to_int (scon_to_bignum s)
fun foldable Pervasives.LOAD_STRING = false
| foldable Pervasives.LOAD_STRUCT = false
| foldable Pervasives.LOAD_VAR = false
| foldable Pervasives.LOAD_EXN = false
| foldable Pervasives.LOAD_FUNCT = false
| foldable Pervasives.CALL_C = false
| foldable _ = SimpleUtils.safe_cse (APP (BUILTIN b,(el,[]),ty))
fun try_fold (Pervasives.ORD,[SCON (Ident.STRING s, l)]) =
(SOME (make_int_scon (ord(String.sub(s, 0)),Location.UNKNOWN))
handle MLWorks.String.Ord => NONE)
| try_fold (Pervasives.CHR,[SCON (n, _)]) =
(SOME (make_string_scon (MLWorks.String.chr (scon_to_int n)))
handle Chr => NONE
| Bignum.Unrepresentable => NONE)
| try_fold (Pervasives.HAT,[SCON (Ident.STRING s1, _),SCON (Ident.STRING s2, _)]) =
SOME (make_string_scon (s1 ^ s2))
| try_fold (Pervasives.SIZE,[SCON (Ident.STRING s, l)]) =
SOME (make_int_scon (size s,Location.UNKNOWN))
| try_fold (Pervasives.INTPLUS,[SCON (n1,_),SCON (n2,_)]) =
SOME (make_bignum_scon (Bignum.+ (scon_to_bignum n1, scon_to_bignum n2),
scon_location n1))
| try_fold (Pervasives.INTSTAR,[SCON (n1,_),SCON (n2,_)]) =
SOME (make_bignum_scon (Bignum.* (scon_to_bignum n1, scon_to_bignum n2),
scon_location n1))
| try_fold (Pervasives.INTDIV,[SCON (n1,_),SCON (n2,_)]) =
SOME (make_bignum_scon (Bignum.div (scon_to_bignum n1, scon_to_bignum n2),
scon_location n1))
| try_fold (Pervasives.INTMOD,[SCON (n1,_),SCON (n2,_)]) =
SOME (make_bignum_scon (Bignum.mod (scon_to_bignum n1, scon_to_bignum n2),
scon_location n1))
| try_fold (Pervasives.INTMINUS,[SCON (n1,_),SCON (n2,_)]) =
SOME (make_bignum_scon (Bignum.- (scon_to_bignum n1, scon_to_bignum n2),
scon_location n1))
| try_fold (Pervasives.INTEQ,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.eq (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold (Pervasives.INTLESS,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.< (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold (Pervasives.INTGREATER,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.> (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold (Pervasives.INTLESSEQ,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.<= (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold (Pervasives.INTGREATEREQ,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.>= (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold (Pervasives.INTNE,[SCON (n1,_),SCON (n2,_)]) =
SOME (INT (if (Bignum.<> (scon_to_bignum n1, scon_to_bignum n2)) then 1 else 0))
| try_fold _ = NONE
fun make_mult (0,powerexp,accexp,bindings,location) =
wrap_lets (bindings,accexp)
| make_mult (n,powerexp,accexp,bindings,location) =
let
val powervar' = new_LVar ()
val powerexp' = APP (BUILTIN Pervasives.INTPLUS, ([powerexp,powerexp],[]), ty)
val (accexp',bindings) =
if n mod 2 = 0
then (accexp,bindings)
else
let
val accvar' = new_LVar ()
val accexp' = APP (BUILTIN Pervasives.INTPLUS, ([accexp,powerexp],[]), ty)
in
(VAR accvar',
(accvar',NONE,accexp') :: bindings)
end
val n' = n div 2
in
if n' = 0 then wrap_lets (bindings,accexp')
else make_mult (n',
VAR powervar',
accexp',
(powervar',NONE,powerexp') :: bindings,
location)
end
val small_value = 10
fun do_multiply (e,i,location) =
let
val n = scon_to_int i
in
if n >= 0 andalso n <= small_value
then SOME (make_mult (n,e,SCON (Ident.INT ("0",location),NONE),[],location))
else
case power2 n of
SOME j =>
let
fun pow n = MLWorks.Internal.Bits.lshift (1,n)
val n = pow (29-j)
val n2 = pow (30-j)
val a = new_LVar ()
in
SOME (LET ((a,
NONE,
APP (BUILTIN Pervasives.UNSAFEINTPLUS, ([e,make_int_scon (n,location)],
[]),
ty)),
SWITCH (APP (BUILTIN Pervasives.WORDLT, ([VAR a,make_int_scon (n2,location)],[]),ty),
SOME {num_vccs = 0, num_imms = 2},
[(IMM_TAG ("false",0), RAISE (STRUCT ([BUILTIN Pervasives.EXOVERFLOW,
STRUCT ([],TUPLE)],CONSTRUCTOR))),
(IMM_TAG ("true",1),
APP (BUILTIN Pervasives.LSHIFT, ([e,make_int_scon (j,location)],[]),ty))],
NONE)))
end
| _ => NONE
end
fun try_simplify (Pervasives.INTDIV,[e,SCON (s,_)]) =
let
val i = scon_to_int s
val location = scon_location s
in
case power2 i of
SOME j => SOME (APP (BUILTIN Pervasives.ARSHIFT,([e,make_int_scon (j,location)],[]),ty))
| _ => NONE
end
| try_simplify (Pervasives.INTMOD,[e,SCON (s,location)]) =
let
val i = scon_to_int s
val location = scon_location s
in
case power2 i of
SOME j => SOME (APP (BUILTIN Pervasives.ANDB,([e,make_int_scon (i-1,location)],[]),ty))
| _ => NONE
end
| try_simplify (Pervasives.INTSTAR, [e,SCON (s as Ident.INT (i,l),_)]) =
do_multiply (e,s,l)
| try_simplify (Pervasives.INTSTAR, [SCON (s as Ident.INT (i,l),_),e]) =
do_multiply (e,s,l)
| try_simplify (Pervasives.ARSHIFT,[e,SCON (Ident.INT ("0",l),_)]) = SOME e
| try_simplify (Pervasives.RSHIFT,[e,SCON (Ident.INT ("0",l),_)]) = SOME e
| try_simplify (Pervasives.LSHIFT,[e,SCON (Ident.INT ("0",l),_)]) = SOME e
| try_simplify (Pervasives.INTPLUS,[e,SCON (Ident.INT ("0",l),_)]) = SOME e
| try_simplify (Pervasives.INTPLUS,[SCON (Ident.INT ("0",l),_),e]) = SOME e
| try_simplify _ = NONE
in
(if not (foldable b)
then NONE
else
let
val args = map get_val el
val result = try_fold (b,args)
val _ =
case result of
SOME e => diag (3,
fn () =>
"Folded " ^
LambdaPrint.print_exp (APP (BUILTIN b,(args,[]),ty)) ^
"\n")
| NONE => diag (2,
fn () =>
"CONSTANT " ^
LambdaPrint.print_exp (APP (BUILTIN b,(args,[]),ty)) ^
"\n");
in
result
end
handle Foo => try_simplify (b,el))
handle Bignum.Unrepresentable => NONE
| Bignum.Runtime _ => NONE
end
datatype UsesType = APP_USE | ARG_USE
fun make_use_table e =
let
val table = empty_hashmap ()
val app_use = [APP_USE]
val arg_use = [ARG_USE]
fun expfun (APP (function,(arg,_),_)) =
(case function of
VAR f => IntHashTable.update (table,f,app_use)
| _ => ();
case arg of
[VAR a] => IntHashTable.update (table,a,arg_use)
| _ => ())
| expfun _ = ()
in
letiterate expfun e;
table
end
fun simplify (e,do_full,env,changed,depth) =
let
fun change () = changed := true
val novar = NONE
val use_table = make_use_table e
fun get_uses x =
case hashmap_find (x,use_table) of
SOME uses => uses
| _ => []
fun has_app_use (NONE) = false
| has_app_use (SOME x) =
Lists.member (APP_USE,get_uses x)
fun has_arg_use (NONE) = false
| has_arg_use (SOME x) =
Lists.member (ARG_USE,get_uses x)
fun do_simple (exp,optvar,depth,switches) =
(case exp of
APP (e,(el,fpel),ty) =>
let
val def = lookup (e,env)
val _ = case fpel of [] => () | _ => Crash.impossible ("do_simple: fp args in APP")
in
case def of
SOME (FN ((vars,[]),body,status,_,_,_)) =>
if do_full andalso depth <= max_depth andalso
(status = ENTRY orelse (inline_functors andalso status = FUNC) orelse
is_inlineable (body,depth) orelse
(has_app_use optvar andalso is_curry_inlineable (body,depth)))
then
(diag (3,
(fn () =>
let
val f =
case e of
VAR f => LambdaPrint.print_var f
| _ => "<Function>"
in
"Inlined " ^ f ^ "\n"
end));
change();
aux (alpha (list_to_hashmap (Lists.zip (vars,el)),body),optvar,depth+1,switches))
else exp
| SOME (BUILTIN b) =>
let
val do_mult_convert =
case el of
[e] => SimpleUtils.get_arity b <> 1
| _ => false
in
if not do_mult_convert
then
(case do_builtinapp ((b,el,ty),env) of
SOME e => (change(); aux (e,optvar,depth,switches))
| _ => exp)
else
let
val e2 = case el of [e2] => e2 | _ => crash "Multiarg APP of BUILTIN"
val arity = SimpleUtils.get_arity b
fun make_vars (0,acc) = acc
| make_vars (n,acc) = make_vars (n-1,new_LVar () :: acc)
fun make_lets (n,[],e) = e
| make_lets (n,(var::vars),e) =
LET ((var,null_debug,mk_tuple_select (n,arity,e2)),
make_lets (n+1,vars,e))
val vars = make_vars (arity,[])
in
diag (3, fn () => "Done a builtinapp\n");
change();
aux (make_lets (0,vars,APP (BUILTIN b,(map VAR vars,[]),ty)),optvar,depth,switches)
end
end
| SOME (APP (BUILTIN Pervasives.MAP, ([e],[]),_)) =>
(change();
aux (APP (BUILTIN Pervasives.UMAP,(e::el,[]),ty), optvar,depth,switches))
| _ => exp
end
| SELECT (info as {index=i,size=j,selecttype=ty},e) =>
(let
val def = lookup (e,env)
in
case def of
SOME (STRUCT (args,ty')) =>
(diag (3, (fn () => "Done a select on " ^ LambdaPrint.print_exp (SELECT(info,e)) ^ "\n"));
if ty <> ty'
then diag (1,
fn () => "Bad struct-select pair:" ^
LambdaPrint.print_exp (SELECT (info,e)) ^ ":" ^
LambdaPrint.print_exp (STRUCT (args,ty')) ^ "\n")
else ();
change();
aux (Lists.nth (i,args),optvar,depth,switches))
| _ => exp
end
handle Lists.Nth =>
(crash ("Bad Select: " ^ LambdaPrint.print_exp (SELECT(info,e)) ^ "\n")))
| STRUCT (v::vl,ty) =>
if not (has_arg_use optvar) then
let
val def = lookup (v,env)
in
case def of
SOME (SELECT ({index=0,size=j,selecttype=ty'},e)) =>
(case lookup (e,env) of
SOME _ =>
let
val len = 1 + length vl
in
if j = len andalso ty = ty'
then
let
fun check (n,[]) = true
| check (n,v::vl) =
case lookup (v,env) of
SOME (SELECT ({index=i,size=j,...},e')) =>
i = n andalso
j = len andalso
SimpleUtils.exp_eq (e,e') andalso
check (n+1,vl)
| _ => false
in
if check (1,vl)
then (diag (3, fn () => "Done a struct\n");
change();
aux (e,optvar,depth,switches))
else exp
end
else
exp
end
| _ => exp)
| _ => exp
end
else exp
| SWITCH (e,info,tel,opte) =>
let
fun eq_tag (IMM_TAG (_,i),IMM_TAG (_,i')) = i = i'
| eq_tag (VCC_TAG (_,i),VCC_TAG (_,i')) = i = i'
| eq_tag (SCON_TAG (s,_),SCON_TAG (s',_)) =
(case s of
Ident.REAL _ => Crash.impossible"simplelambda:eq_tag:real"
| _ => Scons.scon_eqval(s, s'))
| eq_tag _ = false
fun print_tag (IMM_TAG (_,i)) = "IMM " ^ N i
| print_tag (VCC_TAG (_,i)) = "VCC " ^ N i
| print_tag (SCON_TAG (Ident.INT (i,_),_)) = "INT " ^ i
| print_tag (SCON_TAG (Ident.WORD (i,_),_)) = "WORD " ^ i
| print_tag (SCON_TAG (Ident.REAL (i,_),_)) = "REAL " ^ i
| print_tag (SCON_TAG (Ident.STRING s, _)) = "STRING " ^ MLWorks.String.ml_string (s,~1)
| print_tag (SCON_TAG (Ident.CHAR s, _)) = "CHAR " ^ MLWorks.String.ml_string (s,~1)
| print_tag (EXP_TAG i) = "<EXP_TAG>"
fun constant_switch_weed (t,[],SOME e) = e
| constant_switch_weed (t,[],NONE) = crash ("constant tag: " ^ print_tag t ^ " not found: " ^ LambdaPrint.print_exp (SWITCH (e,info,tel,opte)))
| constant_switch_weed (t,((t',e)::rest),opte) =
if eq_tag (t,t')
then e
else constant_switch_weed (t,rest,opte)
fun all_equal (tel,opte) =
let
fun dotags (e,[]) = SOME e
| dotags (e,(e'::l)) =
if SimpleUtils.exp_eq (e,e') then dotags (e,l) else NONE
in
case (tel,opte) of
(_,SOME e) => dotags (e,map #2 tel)
| (((t,e)::rest),NONE) => dotags (e, map #2 rest)
| _ => crash "No cases in all_equal"
end
fun find_switch (e,[]) = NONE
| find_switch (e,(e',t)::rest) =
if SimpleUtils.exp_eq (e,e')
then SOME t
else find_switch (e,rest)
fun find_default_tag (tel, SOME{num_vccs,num_imms}) =
if length tel + 1 = num_vccs + num_imms
then
let
fun find ([],tags,vccs,imms) = (tags,vccs,imms)
| find ((VCC_TAG (_,i),_)::rest,tags,vccs,imms) = find (rest,i::tags,1+vccs,imms)
| find ((IMM_TAG (_,i),_)::rest,tags,vccs,imms) = find (rest,i::tags,vccs,1+imms)
| find (_::rest,tags,vccs,imms) = crash "Bad tag in find_default_tag"
fun find_missing tags =
let
val tags = Lists.qsort (op<) tags
fun scan (n,[]) = n
| scan (n,(n'::rest)) =
if n = n' then scan (n+1,rest)
else n
in
scan (0,tags)
end
val (tags,vccs,imms) = find (tel,[],0,0)
in
if vccs = num_vccs-1
then SOME (VCC_TAG ("?",find_missing tags))
else
if imms = num_imms-1
then SOME (IMM_TAG ("?",find_missing tags))
else crash "Can't find default tag"
end
else NONE
| find_default_tag (tel, NONE) = NONE
in
case all_equal (tel,opte) of
SOME e' =>
let
val _ = diag (2,fn () => "All equal switch found: " ^ LambdaPrint.print_exp e' ^ "\n")
val _ = change()
val id = new_LVar ()
in
LET ((id,null_debug,aux (e,novar,depth,switches)),aux (e',optvar,depth,switches))
end
| NONE =>
let
val edef = lookup (e,env)
in
case edef of
SOME (INT i) =>
(change();
diag (1,fn _ => "Found INT constant switch\n");
aux (constant_switch_weed (IMM_TAG ("",i),tel,opte),optvar,depth,switches))
| SOME (STRUCT ([INT i,_],TUPLE)) =>
let fun auxweed i =
aux(constant_switch_weed(VCC_TAG ("",i),tel,opte),
optvar,depth,switches)
in
change();
diag (1,fn _ => "Found VCC constant switch\n");
case info of
NONE => auxweed i
| SOME {num_vccs, num_imms} =>
if num_vccs = 1 andalso num_imms = 1
then
auxweed 0
else
auxweed i
end
| SOME (STRUCT ([INT i,_],_)) =>
(change();
diag (1,fn _ => "Found VCC constant switch\n");
aux (constant_switch_weed (VCC_TAG ("",i),tel,opte),optvar,depth,switches))
| SOME (SCON(s as Ident.INT _, opt)) =>
(change();
diag (1,fn _ => "Found SCON constant switch\n");
aux (constant_switch_weed (SCON_TAG (s,opt),tel,opte),optvar,depth,switches))
| SOME (SCON(s as Ident.STRING _, opt)) =>
(change();
diag (1,fn _ => "Found SCON constant switch\n");
aux (constant_switch_weed (SCON_TAG (s,opt),tel,opte),optvar,depth,switches))
| _ =>
(case find_switch (e,switches) of
SOME t =>
(change();
diag (2,fn _ => "Repeated switch found\n");
aux (constant_switch_weed (t,tel,opte),optvar,depth,switches))
| _ =>
let
val e' =
case edef of
SOME e' =>
if SimpleUtils.switchable_exp e' then e'
else e
| _ => e
val (e'',lets) =
unwrap_lets (aux (e',novar,depth,switches))
in
wrap_lets
(lets,
SWITCH (e'',
info,
map
(fn (EXP_TAG te,e') =>
(EXP_TAG (aux (te,novar,depth,switches)),
aux(e',novar,depth,switches))
| (t as SCON_TAG(Ident.REAL _, _), e') =>
(t, aux(e',novar,depth,switches))
| (t,e') => (t,aux(e',novar,depth,(e,t)::switches)))
tel,
let
val switches' =
case find_default_tag (tel,info) of
SOME t => (e,t) :: switches
| _ => switches
in
optfun (fn e => aux (e,novar,depth,switches')) opte
end))
end)
end
end
| HANDLE (e1,e2,s) => HANDLE (aux (e1,novar,depth,switches),
aux (e2,novar,depth,switches),s)
| FN ((vl,fpvl),body,status,name,ty,debug) =>
let
val newbody = aux (body,novar,depth,switches)
fun eqvars ([],[]) = true
| eqvars (VAR v::l,v'::l') =
v = v' andalso eqvars (l,l')
| eqvars _ = false
val is_eta =
case newbody of
APP (f,(el,fpel),_) =>
if eqvars (el, vl) andalso eqvars (fpel, fpvl) then SOME f else NONE
| _ => NONE
in
case is_eta of
SOME e => (diag (2,fn _ => "Found eta\n"); change(); e)
| _ => FN ((vl,fpvl),newbody,status,name,ty,debug)
end
| LET ((x,_,e1),e2) => crash "NotLinearized: let"
| LETREC _ => crash "NotLinearized: letrec"
| _ => exp)
and merge_lets (x1,i1,LET ((x2,i2,e1),e2),e3,optvar,depth,switches) =
(IntHashTable.update (env,x2,VAL e1);
LET ((x2,i2,e1),merge_lets (x1,i1,e2,e3,optvar,depth,switches)))
| merge_lets (x1,i1,e1,e2,optvar,depth,switches) =
(IntHashTable.update (env,x1,VAL e1);
LET ((x1,i1,e1),aux (e2,optvar,depth,switches)))
and aux (LET ((x1,i1,LET ((x2,i2,e1),e2)),e3),optvar,depth,switches) =
aux (LET ((x2,i2,e1),(LET ((x1,i1,e2),e3))),optvar,depth,switches)
| aux (LET ((x1,i1,LETREC (fl,vl,e2)),e3),optvar,depth,switches) =
aux (LETREC (fl,vl, LET ((x1,i1,e2),e3)),optvar,depth,switches)
| aux (LET ((x1,i1,e1 as RAISE _),e2),optvar,depth,switches) =
aux (e1,optvar,depth,switches)
| aux (LET ((x,i,e1),e2),optvar,depth,switches) =
(case e1 of
SWITCH (e1',info,[(t1,se1 as RAISE _),
(t2,se2)],
NONE) =>
aux (SWITCH (e1',info,[(t1,se1),(t2,LET ((x,i,se2),e2))],NONE),
optvar,depth,switches)
| _ =>
merge_lets (x,i,do_simple (e1,SOME x,depth,switches),e2,optvar,depth,switches))
| aux (LETREC (fl,el,e),optvar,depth,switches) =
let
val el' =
map
(fn ((f,info),FN (args,e,status,name,ty,debug)) =>
let
val e' = aux (e,novar,depth,switches)
val f' = FN (args,e',status,name,ty,debug)
val _ = IntHashTable.update (env,f,VAL f')
in
f'
end
| ((f,info),_) => crash "Bad fn in letrec - simplify")
(Lists.zip (fl,el))
in
LETREC (fl,el',aux (e,optvar,depth,switches))
end
| aux (e,optvar,depth,switches) = do_simple (e,optvar,depth,switches)
in
aux (e,novar,depth,[])
end
fun simplify_exp (e,do_full) = simplify (e,do_full,empty_hashmap (),ref false,0)
fun remove_unused e =
let
val vars = empty_hashmap ()
fun do_simple e =
case e of
SWITCH (e,info,tel,opte) =>
SWITCH (do_simple e,info,map (telfun do_let) tel,optfun do_let opte)
| HANDLE (e1,e2,s) => HANDLE (do_let e1,do_let e2,s)
| FN (args,e,status,name,ty,debug) => FN (args,do_let e,status,name,ty,debug)
| LETREC (fl,el,e) => LETREC (fl,map do_let el,do_let e)
| LET _ => crash ("NotLinearized: remove_unused" ^ LambdaPrint.print_exp e)
| e =>
(Lists.iterate
(fn x => IntHashTable.update (vars,x,()))
(SimpleUtils.vars_of e);
e)
and do_let e =
case e of
LET ((x,i,e1),e2) =>
let
val e2' = do_let e2
in
if hashmap_member (x,vars) orelse
not (is_absent i) orelse
not (SimpleUtils.safe_elim e1)
then LET ((x,i,do_simple e1),e2')
else
(diag (3, fn () => "Dropping let\n");
e2')
end
| e => do_simple e
in
do_let e
end
fun internal_elim_simple_bindings (binds,e) =
let
fun aux (e as INT _) = e
| aux (e as SCON _) = e
| aux (e as MLVALUE _) = e
| aux (e as BUILTIN _) = e
| aux (e as VAR v) =
(case hashmap_find (v,binds) of
SOME e' => e'
| NONE => e)
| aux (APP (e,(el,fpel),ty)) = APP (aux e,(map aux el,map aux fpel), ty)
| aux (FN (args,e,status,name,ty,debug)) = FN (args,aux e,status,name,ty,debug)
| aux (LETREC (fl,el,e)) =
LETREC (fl,map aux el,aux e)
| aux (STRUCT (el,ty)) = STRUCT (map aux el,ty)
| aux (SWITCH (e,info,tel,opte)) =
SWITCH (aux e,
info,
map (telfun aux) tel,
optfun aux opte)
| aux (HANDLE (e1,e2,s)) = HANDLE(aux e1,aux e2,s)
| aux (RAISE e) = RAISE (aux e)
| aux (SELECT (info,e)) = SELECT (info,aux e)
| aux (e as LET _) = do_let (e,[])
and do_let (LET ((v,i,e1),e2),acc) =
if SimpleUtils.is_atomic e1 andalso is_absent i
then
(IntHashTable.update (binds,v,aux e1);
do_let (e2,acc))
else do_let (e2,(v,i,aux e1)::acc)
| do_let (e,acc) =
wrap_lets (acc,aux e)
in
aux e
end
fun elim_simple_bindings e = internal_elim_simple_bindings (empty_hashmap (),e)
fun pprint (level,p) =
if do_print andalso level <= print_level
then print (LambdaPrint.pds (PROGRAM p) ^ "\n")
else ()
fun pprint_exp (level,e) =
if do_print andalso level <= print_level
then print (LambdaPrint.pde e ^ "\n")
else ()
fun cleanup e =
let
val e = elim_simple_bindings e
val _ = diag (2,fn () => "Done elim simple bindings\n")
val e = remove_unused e
val _ = diag (2,fn () => "Done remove unused\n")
in
e
end
fun progmap f (declist,exp) =
let
fun decfun (VAL (FN (vl,e,status,name,ty,debug))) = VAL (FN (vl,f e,status,name,ty,debug))
| decfun (VAL e) = VAL (f e)
| decfun (FUNCTOR (var,name,prog)) =
FUNCTOR (var,name,progmap f prog)
in
(map (fn (var,info,dec) => (var,info,decfun dec)) declist,
f exp)
end
val linearize = elim_simple_bindings o SimpleUtils.linearize o elim_simple_bindings
fun linearize_all prog =
let
val result = progmap linearize prog
in
pprint (2,result);
result
end
fun make_hashmap_env declist =
let
val env = empty_hashmap ()
fun aux declist =
let
fun decfun (FUNCTOR (var,name,(declist,exp))) = aux declist
| decfun _ = ()
in
Lists.iterate (fn (var,info,dec) =>
(IntHashTable.update (env,var,dec);
decfun dec))
declist
end
in
aux declist;
env
end
fun count_occurrences (counts,prog) =
let
fun inc_var x =
case hashmap_find (x,counts) of
SOME (r,_,_) => r := 1 + (!r)
| NONE => ()
fun aux (INT _) = ()
| aux (SCON _) = ()
| aux (MLVALUE _) = ()
| aux (BUILTIN _) = ()
| aux (VAR x) = inc_var x
| aux (FN (vl,e,_,_,_,_)) = aux e
| aux (STRUCT (el,_)) = Lists.iterate aux el
| aux (SWITCH (e,info,tel,opte)) =
(aux e;
Lists.iterate (fn (EXP_TAG et,e) => (aux et;aux e) | (t,e) => aux e) tel;
ignore(optfun aux opte);
())
| aux (HANDLE (e1,e2,s)) = (aux e1; aux e2)
| aux (RAISE e) = aux e
| aux (SELECT (_,e)) = aux e
| aux (LET ((v,_,e1),e2)) = (aux e1; aux e2)
| aux (LETREC (fl,el,e)) = (Lists.iterate aux el; aux e)
| aux (APP (VAR x,(el,fpel),_)) = (inc_var x; Lists.iterate aux el; Lists.iterate aux fpel)
| aux (APP (e,(el,fpel),_)) = (aux e; Lists.iterate aux el; Lists.iterate aux fpel)
fun decfun (_,_,VAL (FN (_,e,_,_,_,_))) = aux e
| decfun (_,_,VAL e) = aux e
| decfun (_,_,FUNCTOR (var,_,prog)) =
progfun prog
and progfun (defns,exp) =
(Lists.iterate decfun defns;
aux exp)
in
progfun prog
end
fun internal_inline_single_callees (do_lifting,prog as (defns,exp)) =
let
val counts = empty_hashmap ()
fun add_name (f,args,body) =
(diag (0,fn _ => "Adding function " ^ LambdaPrint.print_var f ^ "\n");
IntHashTable.update (counts, f, (ref 0,args,body)))
fun get_exp_funnames exp =
case exp of
FN (vl,e,stat,name,ty,funinfo) => get_exp_funnames e
| LET ((f,info,e as FN ((vl,[]),funbody,stat,name,ty,funinfo)),body) =>
(add_name (f,vl,funbody);
get_exp_funnames e;
get_exp_funnames body)
| LET ((x,info,e),body) =>
(get_exp_funnames e;
get_exp_funnames body)
| LETREC (vl,el,e) =>
(Lists.iterate
(fn ((f,info),FN ((vl,[]),funbody,stat,name,ty,funinfo)) =>
add_name (f,vl,funbody)
| _ => Crash.impossible "Malformed letrec in internal_inline_single_callees")
(Lists.zip (vl,el));
Lists.iterate get_exp_funnames el;
get_exp_funnames e)
| SWITCH (e,info,tel,opte) =>
(Lists.iterate
(fn (t,e) => get_exp_funnames e)
tel;
case opte of
SOME e => get_exp_funnames e
| _ => ())
| HANDLE (e1,e2,_) =>
(get_exp_funnames e1;
get_exp_funnames e2)
| _ => ()
fun get_funnames [] = ()
| get_funnames ((f,_,VAL (FN ((vl,[]),e,stat,name,ty,funinfo))):: l) =
(add_name (f,vl,e);
get_exp_funnames e;
get_funnames l)
| get_funnames ((_,_,VAL e):: l) =
(get_exp_funnames e;
get_funnames l)
| get_funnames ((f,_,FUNCTOR (x,name,(declist,e)))::l) =
(get_funnames declist;
get_exp_funnames e;
get_funnames l)
val env = make_hashmap_env defns
val _ = get_funnames defns
val _ = get_exp_funnames exp
val _ = count_occurrences (counts,prog)
fun toinline f =
case hashmap_find (f,counts) of
SOME (ref 1,args,body) => SOME (args,body)
| _ => NONE
val max_depth = 20
fun inline (e,count) =
let
fun aux (FN (vl,e,b,name,ty,debug)) =
FN (vl, if do_lifting then e else aux e, b,name,ty,debug)
| aux (SWITCH (e,info,tel,opte)) =
SWITCH (aux e,
info,
map (telfun aux) tel,
optfun aux opte)
| aux (HANDLE (e1,e2,s)) = HANDLE (aux e1,aux e2,s)
| aux (LET ((v,i,e1),e2)) = LET ((v,i,aux e1),aux e2)
| aux (e as APP (VAR f,(el,[]),ty)) =
(case toinline f of
SOME (args,body) =>
if SimpleUtils.size_less (body,1000,true) andalso count < max_depth
then
inline (alpha (list_to_hashmap (Lists.zip (args,el)),body),count+1)
else e
| NONE => e)
| aux (e as APP _) = e
| aux (LETREC (fl,el,e)) = LETREC (fl, map aux el, aux e)
| aux e = e
in
aux e
end
in
progmap (fn e => inline (e,0)) prog
end
fun used_globals (prog as (defns,exp)) =
let
val global_env = make_hashmap_env defns
val seen = empty_hashmap ()
val todo = ref [] : LambdaExp list ref
fun pop () = case !todo of (a::b) => (todo:=b;a) | _ => crash "Bad Pop"
fun push e = todo := e :: !todo
fun check var = hashmap_member (var,global_env) andalso not (hashmap_member (var,seen))
fun add var = (IntHashTable.update (seen,var,());
do_var_def var)
and do_var_def var =
case hashmap_find (var,global_env) of
NONE => crash "No environment entry for global"
| SOME d =>
(case d of
VAL e => push e
| FUNCTOR (_,_,(defs,e)) => (Lists.iterate do_one defs; push e))
and do_one (i,info,VAL e) =
if check i andalso not (SimpleUtils.safe_elim e)
then (push e;add i)
else ()
| do_one _ = ()
fun do_var var = if check var then add var else ()
fun aux (INT _) = ()
| aux (SCON _) = ()
| aux (MLVALUE _) = ()
| aux (BUILTIN _) = ()
| aux (VAR var) = do_var var
| aux (STRUCT (el,_)) = laux el
| aux (SWITCH (e,info,tel,opte)) =
(aux e;
Lists.iterate (fn (t,e) => (aux e; taux t)) tel;
case opte of
SOME e' => aux e'
| NONE => ())
| aux (HANDLE (e1,e2,s)) = (aux e1;aux e2)
| aux (RAISE e) = aux e
| aux (SELECT (_,e)) = aux e
| aux (LET ((v,_,e1),e2)) = (aux e1;aux e2)
| aux (APP (e,(el,fpel),_)) = (aux e;Lists.iterate aux el; Lists.iterate aux fpel)
| aux (FN (vl,e,status,name,_,_)) = aux e
| aux (LETREC (fl,el,e)) = (Lists.iterate aux el; aux e)
and laux ([]) = ()
| laux (e::el) = (aux e; laux el)
and taux (EXP_TAG e) = aux e
| taux (_) = ()
fun null [] = true
| null _ = false
in
push exp;
Lists.iterate do_one defns;
while not (null (!todo)) do aux (pop());
seen
end
fun weed (prog as (defns,exp)) =
let
val _ = diag (2, fn () => ("Weeding..\n"))
val vars = used_globals prog
fun strip ((def as (a,info,VAL e))::rest,acc) =
if hashmap_member(a,vars) orelse not (is_absent info)
then strip (rest,def::acc)
else
(diag (3, fn () => ("Weeding " ^ LambdaPrint.print_var a ^ "\n"));
if SimpleUtils.safe_elim e then () else crash "Eliminating unsafe expression";
strip (rest,acc))
| strip ((a,info,FUNCTOR (var,name,(defns,exp))) :: rest,acc) =
strip (rest,(a,info,FUNCTOR (var,name,(strip (defns,[]),exp))) :: acc)
| strip ([],acc) = rev acc
val new = strip (defns,[])
in
(new,exp)
end
fun globify (defs,exp) =
let
fun globify_aux ([],acc) = rev acc
| globify_aux ((a,i1,VAL (LETREC (fl,el,e))) :: l,acc) =
let
val fns =
map (fn ((f,info),e) => (f,info,VAL e))
(Lists.zip (fl,el))
in
globify_aux (l, (a,i1,VAL e) :: (rev fns @ acc))
end
| globify_aux ((a,i1,VAL (LET ((v,i2,e1),e2))) :: l,acc) =
globify_aux ((a,i1,VAL e2)::l,(v,i2,VAL e1)::acc)
| globify_aux ((a,i1,FUNCTOR (v,name,prog)) :: l,acc) =
globify_aux (l,(a,i1,FUNCTOR (v,name,globify prog))::acc)
| globify_aux (d::l,env) =
globify_aux (l,d::env)
in
(globify_aux (defs,[]),exp)
end
fun simplify_all (prog,do_full,env,depth) =
let
val changed = ref false
fun aux (declist,exp) =
let
fun doval (FN (vl,body,status,name,ty,debug)) =
FN (vl,simplify (body,do_full,env,changed,depth),status,name,ty,debug)
| doval e = simplify (e,do_full,env,changed,depth)
fun decfun (x,info,VAL e) =
let
val new = VAL (doval e)
in
IntHashTable.update (env,x,new);
diag (1,fn _ => "Done " ^ N x ^ "\n");
(x,info,new)
end
| decfun (f,info,FUNCTOR (var,name,prog)) =
(f,info,FUNCTOR (var,name,aux prog))
in
(map decfun declist,
simplify (exp,do_full,env,changed,depth))
end
val prog' = aux prog
in
(!changed,prog')
end
fun simplify_till_done (prog as (declist,exp),do_full) =
let
val env = make_hashmap_env declist
fun aux (prog,n) =
let
val _ = diag (2, fn () => ("Optimize pass " ^ N n ^ "\n"))
val _ = pprint (2,prog)
val (changed,prog') = simplify_all (prog,do_full,env,n)
val _ = diag (2, fn () => ("Done pass " ^ N n ^ "\n"))
in
if changed then aux (globify prog',n+1)
else
(diag (1, fn () => (N n ^ " iterations of simplify_all\n"));
weed (progmap cleanup (globify prog')))
end
in
aux (prog,0)
end
fun exp_hash (VAR v) = v
| exp_hash (APP (e1,(el,fpel),_)) = 7 * exp_hash e1
| exp_hash (SCON (s,_)) = 91
| exp_hash (INT n) = 1001 + n
| exp_hash (STRUCT (el,ty)) = elist_hash el
| exp_hash (SELECT (_,e)) = 10001 + exp_hash e
| exp_hash (BUILTIN b) = 100001
| exp_hash (MLVALUE _) = 200001
| exp_hash _ = Crash.impossible "exp_hash"
and elist_hash [] = 2001
| elist_hash (a::b) = exp_hash a + elist_hash b
fun make_exp_table () =
ref (HashTable.new (16,SimpleUtils.exp_eq,exp_hash))
fun empty_exp_table table =
table := HashTable.new (16,SimpleUtils.exp_eq,exp_hash)
fun exp_add (e,v,table) =
HashTable.update (!table,e,v)
fun exp_remove (e,table) =
HashTable.delete (!table,e)
fun exp_lookup (e,table) =
HashTable.tryLookup (!table,e)
fun global_cse (defs,exp) =
let
val top_level_table = make_exp_table ()
fun aux ([],acc,substs,_) = (rev acc,substs)
| aux ((v,info,VAL (FN(args,e,s,n,t,d))) :: l,acc,substs,table) =
let
val e' = lsubst (substs,e)
in
aux (l,(v,info,VAL (FN(args,e',s,n,t,d)))::acc,substs,table)
end
| aux ((v,info,VAL e) :: l,acc,substs,table) =
let
val e' = lsubst (substs,e)
in
if SimpleUtils.is_atomic e' orelse not (SimpleUtils.is_simple e')
then aux (l,(v,info,VAL e')::acc,substs,table)
else
case exp_lookup (e',table) of
SOME v' =>
(diag (3, fn () => "CSE of " ^ LambdaPrint.print_var v ^ " = "
^ LambdaPrint.print_exp e' ^ "\n");
IntHashTable.update (substs,v,VAR v');
aux (l,acc,substs,table))
| NONE =>
if SimpleUtils.safe e'
then
(exp_add (e',v,table);
aux (l,(v,info,VAL e')::acc,substs,table))
else
aux (l,(v,info,VAL e')::acc,substs,table)
end
| aux ((v,info,FUNCTOR(arg,name,(fdefs,fexp))) :: rest,acc,substs,table) =
let
val ftable = make_exp_table ()
val _ = (ftable := HashTable.copy(!table))
val (fdefs',fsubsts') = aux (fdefs,[],empty_hashmap (),ftable)
val fexp' = lsubst (fsubsts',fexp)
in
aux (rest,(v,info,FUNCTOR(arg,name,(fdefs',fexp')))::acc,substs,table)
end
val (defs',substs') = aux (defs,[],empty_hashmap (),top_level_table)
val exp' = lsubst (substs',exp)
in
(defs',exp')
end
fun is_imperative (APP (BUILTIN builtin,_,_)) =
(case builtin of
Pervasives.SUB => true
| Pervasives.UNSAFE_SUB => true
| Pervasives.BYTEARRAY_SUB => true
| Pervasives.BYTEARRAY_UNSAFE_SUB => true
| Pervasives.DEREF => true
| _ => false)
| is_imperative _ = false
fun new_imp_env (e,imp_table) =
(empty_exp_table imp_table;
case e of
APP (BUILTIN Pervasives.UNSAFE_UPDATE, ([r,i,VAR x],[]),_) =>
let
val e = APP (BUILTIN Pervasives.UNSAFE_SUB, ([r,i],[]),NONE)
in
exp_add (e,x,imp_table);
SOME e
end
| APP (BUILTIN Pervasives.BECOMES, ([r,VAR x],[]),_) =>
let
val e = APP (BUILTIN Pervasives.DEREF, ([r],[]),NONE)
in
exp_add (e,x,imp_table);
SOME e
end
| _ => NONE)
fun local_cse exp =
let
val table = make_exp_table ()
val imp_table = make_exp_table ()
fun do_let (LET ((x,i,e1),e2),substs,acc) =
let
val e1' = lsubst (substs,e1)
in
if SimpleUtils.is_simple e1'
then
if SimpleUtils.safe_cse e1'
then
case exp_lookup (e1',table) of
SOME x' =>
(diag (1, fn () => "Found CSE: " ^ LambdaPrint.print_exp e1' ^ "\n");
IntHashTable.update (substs,x,VAR x');
do_let (e2,substs,(x,i,VAR x')::acc))
| NONE =>
let
val _ = exp_add (e1',x,table)
val result = do_let (e2,substs,(x,i,e1')::acc)
val _ = exp_remove (e1',table)
in
result
end
else if do_imperative_cse andalso is_imperative e1'
then
case exp_lookup (e1',imp_table) of
SOME x' =>
(diag (2, fn () => "Found imperative CSE: " ^ LambdaPrint.print_exp e1' ^ "\n");
IntHashTable.update (substs,x,VAR x');
do_let (e2,substs,(x,i,VAR x')::acc))
| NONE =>
let
val _ = exp_add (e1',x,imp_table)
val result = do_let (e2,substs,(x,i,e1')::acc)
val _ = exp_remove (e1',imp_table)
in
result
end
else
let
val e = new_imp_env (e1',imp_table)
val result = do_let (e2,substs,(x,i,e1')::acc)
val _ =
case e of
SOME e' => exp_remove (e',imp_table)
| _ => ()
in
result
end
else
(empty_exp_table imp_table;
do_let (e2,substs,(x,i,do_complex (e1',substs))::acc))
end
| do_let (e,substs,acc) =
wrap_lets (acc,do_complex (e,substs))
and do_complex (SWITCH (e,info,tel,opte),substs) =
let
val e' = lsubst (substs,e)
val e'' =
if SimpleUtils.is_simple e' andalso SimpleUtils.safe_cse e'
then
case exp_lookup (e',table) of
SOME x => VAR x
| _ => e'
else e'
in
SWITCH (e'',
info,
map (telfun (fn e => do_let (e,substs,[]))) tel,
optfun (fn e => do_let (e,substs,[])) opte)
end
| do_complex (HANDLE (e1,e2,s),substs) =
(empty_exp_table imp_table;
HANDLE (do_let (e1,substs,[]),do_let (e2,substs,[]),s))
| do_complex (FN (f,e,v,n,t,d),substs) =
(empty_exp_table imp_table;
FN (f,do_let (e,substs,[]),v,n,t,d))
| do_complex (LETREC (fl,el,e),substs) =
(empty_exp_table imp_table;
LETREC (fl,map (fn e => do_let (e,substs,[])) el,do_let (e,substs,[])))
| do_complex (LET _,substs) = crash "LET in do_complex in CSE"
| do_complex (e1,substs) =
let
val e1' = lsubst (substs,e1)
in
if SimpleUtils.is_simple e1'
then
if SimpleUtils.safe_cse e1'
then
case exp_lookup (e1',table) of
SOME x' =>
(diag (1, fn () => "Found CSE: " ^ LambdaPrint.print_exp e1' ^ "\n");
VAR x')
| NONE => e1'
else if do_imperative_cse andalso is_imperative e1'
then
case exp_lookup (e1',imp_table) of
SOME x' =>
(diag (1, fn () => "Found imperative CSE: " ^ LambdaPrint.print_exp e1' ^ "\n");
VAR x')
| NONE => e1'
else e1'
else e1'
end
val result = do_let (exp,empty_hashmap (),[])
in
result
end
val local_cse_all = progmap (elim_simple_bindings o local_cse)
fun elim_global_simple_bindings (defs,exp) =
let
val max_iterations = 100
val substs = empty_hashmap ()
fun aux ([],acc) = rev acc
| aux ((v,info,VAL e) :: l,acc) =
if is_absent info then
if SimpleUtils.is_atomic e then
(IntHashTable.update (substs,v,e);
diag (2,fn () => "Eliminating global " ^ LambdaPrint.print_var v ^ "\n");
aux (l,acc))
else
(case e of
FN(([x],[]), APP(e' as VAR _, ([VAR y], []), _), _, _, _, _) =>
((if x = y then
(IntHashTable.update (substs,v,e');
diag (2,fn () => "Eliminating global " ^ LambdaPrint.print_var v ^ "\n"))
else
());
aux (l,(v,info,VAL e)::acc))
| _ => aux (l,(v,info,VAL e)::acc))
else
aux (l,(v,info,VAL e)::acc)
| aux ((v,info,FUNCTOR(arg,name,(defs,exp))) :: rest,acc) =
let
val defs' = aux (defs,[])
in
aux (rest,(v,info,FUNCTOR (arg,name,(defs',exp)))::acc)
end
fun lookup (exp as VAR x,n) =
if n > max_iterations
then exp
else
(case hashmap_find (x,substs) of
SOME e =>
(case e of
VAR _ => lookup (e,n+1)
| _ => e)
| NONE => exp)
| lookup (exp,n) = exp
val defs' = aux (defs,[])
val fullsubsts = IntHashTable.map (fn (x,e) => lookup (e,0)) substs
fun subfun e = lsubst (fullsubsts,e)
in
progmap subfun (defs',exp)
end
val schedule_all = progmap SimpleUtils.schedule
fun inline_single_callees (do_lifting,prog) = weed (internal_inline_single_callees (do_lifting,prog))
fun global_cleanup prog =
let
val prog = if do_cse then global_cse prog else prog
val _ = pprint (2,prog)
val _ = diag (1, fn () => "Eta elim..\n")
in
weed(elim_global_simple_bindings prog)
end
fun optimise options e =
let
val Options.OPTIONS {compiler_options,...} = options
val Options.COMPILEROPTIONS {generate_debug_info,
local_functions,
opt_self_calls,
intercept,
debug_variables,
generate_moduler,...} = compiler_options
val do_debug = generate_moduler
val do_full = not do_debug andalso not generate_debug_info andalso not debug_variables andalso not intercept
val do_local_functions = do_full andalso local_functions
val _ =
diag (1,fn () =>
if do_full then "Full Optimization\n"
else if do_debug then "Debug Optimization\n"
else "Simple Optimization\n")
fun make_lamb e =
let
val _ = diag (1,fn () => "Linearizing..\n")
val e = linearize e
val _ = pprint_exp (2,e)
val e =
if not do_debug
then
(diag (1,fn () => "Optimizing..\n");
simplify_exp (e, do_full))
else e
val _ = diag (1,fn () => "Cleaning up..\n")
val e = cleanup e
val _ = pprint_exp (2,e)
val _ = diag (1,fn () => "Globalizing..\n")
val e = globalize (e,[],[],false)
val _ = pprint_exp (2,e)
val prog =
if do_full
then
let
val e = if do_local_functions
then (diag (1, fn () => "Preanalyse\n");
let
val _ = pprint_exp (0,e)
val e = LambdaFlow.preanalyse e
in
pprint_exp (0,e);
cleanup (simplify_exp (e,do_full))
end)
else e
val _ = pprint_exp (0,e)
val _ = diag (1,fn () => "Transforming..\n")
val (e,defs) = Timer.xtime ("Transforming",
!print_timings,
fn () => (transform (e,[],[],true)))
val _ = diag (1,fn () => "Linearizing..\n")
val prog = progmap SimpleUtils.linearize (rev defs,e)
val _ = pprint (2,prog)
val _ = diag (1,fn () => "Optimizing..\n")
val prog = simplify_till_done (prog,do_full)
in
prog
end
else globify ([],e)
val _ = diag (1,fn () => "Inlining single callees..\n")
val prog =
if do_full
then progmap SimpleUtils.linearize (inline_single_callees (do_full,prog))
else prog
val prog =
if not do_debug
then
let
val _ = diag (1,fn () => "Optimizing..\n")
val prog = simplify_till_done (prog,do_full)
val _ = pprint (2,prog)
in
prog
end
else
prog
val prog =
if do_cse
then
let
val _ = diag (1, fn () => "Doing Local CSE..\n")
val prog = local_cse_all prog
val _ = pprint (2,prog)
in
prog
end
else prog
val prog =
if not do_debug
then
let
val _ = diag (1,fn () => "Optimizing..\n")
val prog = simplify_till_done (prog,do_full)
val _ = pprint (2,prog)
in
prog
end
else
prog
val _ = diag (1, fn () => "Doing Global Cleanup..\n")
val prog = global_cleanup prog
val _ = pprint (2,prog)
val prog =
if not do_debug
then
(diag (1, fn () => "Doing Schedule..\n");
schedule_all prog)
else prog
val _ = pprint (2,prog)
val _ = diag (1, fn () => "Done\n")
val PROGRAM prog =
if do_local_functions
then LambdaFlow.tail_convert (PROGRAM prog) else PROGRAM prog
val PROGRAM prog =
if do_local_functions
then LambdaFlow.loop_analysis (PROGRAM prog) else PROGRAM prog
val PROGRAM prog =
if do_full
then LambdaFlow.findfpargs (PROGRAM prog) else PROGRAM prog
val PROGRAM prog =
if do_local_functions
then LambdaFlow.lift_locals (PROGRAM prog) else PROGRAM prog
in
prog
end
val prog =
Timer.xtime ("Inner lambda",!print_timings,fn () => make_lamb e)
in
if print_result then print (LambdaPrint.pds (PROGRAM prog) ^ "\n") else ();
TransSimple.trans_program (PROGRAM prog)
end
val optimise = fn options => fn e =>
let
val Options.OPTIONS {compiler_options,...} = options
val Options.COMPILEROPTIONS
{generate_debug_info, debug_variables, generate_moduler,
intercept, interrupt, opt_handlers, opt_leaf_fns, opt_tail_calls,
opt_self_calls, local_functions, ...} = compiler_options
in
if generate_debug_info orelse debug_variables orelse generate_moduler
orelse intercept orelse interrupt orelse opt_handlers orelse opt_leaf_fns
orelse opt_tail_calls orelse opt_self_calls orelse local_functions
then optimise options e
else e
end;
fun simple_beta_reduce e =
TransSimple.trans_program
(PROGRAM ([],cleanup (simplify_exp (linearize e,false))))
end
;
