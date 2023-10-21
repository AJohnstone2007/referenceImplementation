require "^.basis.__int";
require "^.basis.__list";
require "^.basis.__string";
require "../utils/lists";
require "../utils/crash";
require "../utils/counter";
require "../basics/scons";
require "../basics/identprint";
require "../basics/absynprint";
require "../typechecker/types";
require "type_utils";
require "match";
functor Match (
structure Lists : LISTS
structure Crash : CRASH
structure MVCounter : COUNTER
structure Scons : SCONS
structure IdentPrint : IDENTPRINT
structure AbsynPrint : ABSYNPRINT
structure Types : TYPES
structure TypeUtils : TYPE_UTILS
sharing Types.Datatypes = TypeUtils.Datatypes
sharing AbsynPrint.Absyn.Ident = Types.Datatypes.Ident = IdentPrint.Ident
sharing AbsynPrint.Options = IdentPrint.Options = Types.Options
sharing type AbsynPrint.Absyn.Type = Types.Datatypes.Type
sharing type AbsynPrint.Absyn.Tyfun = Types.Datatypes.Tyfun
sharing type AbsynPrint.Absyn.Instance = Types.Datatypes.Instance
sharing type AbsynPrint.Absyn.Structure = Types.Datatypes.Structure
sharing type IdentPrint.Ident.SCon = Scons.SCon
) : MATCH =
struct
structure DataTypes = Types.Datatypes
structure Absyn = AbsynPrint.Absyn
structure Ident = IdentPrint.Ident
structure Options = IdentPrint.Options
structure Bits = MLWorks.Internal.Bits
type Matchvar = int
type lvar = int
fun to_Matchvar x = x
fun from_Matchvar x = x
infix ===
fun (Ident.LONGVALID (_,Ident.CON con)) === (Ident.LONGVALID (_,Ident.CON con')) =
con = con'
| id === id' = id = id'
datatype ('a, 'b) union = INL of 'a | INR of 'b
type 'Tree BoundTree = ((int * 'Tree),lvar) union ref
datatype Constructors =
CON1 of int list |
CON2 of Ident.LongValId * Matchvar |
SCON1 of string list |
SCON2 of string * Matchvar |
REC of (Ident.Lab * Matchvar) list
type Entry = int * Constructors
datatype 'env Tree =
LEAF of Absyn.Exp * int * (Matchvar * Ident.ValId * (DataTypes.Type ref * Absyn.RuntimeInfo ref)) list
| SCON of Matchvar * (Ident.SCon * 'env Tree) list *
'env DefaultTree ref option
* 'env Tree BoundTree option ref option
* int option
| CONSTRUCTOR of DataTypes.Type * Matchvar *
(Ident.LongValId * Matchvar *
('env Tree,'env Tree BoundTree) union) list
* ('env DefaultTree ref option,'env Tree BoundTree) union
* ('env Tree BoundTree option ref,
'env Tree BoundTree * Matchvar) union list * bool
| RECORD of DataTypes.Type * Matchvar * (Ident.Lab * Matchvar) list * 'env Tree
| DEFAULT of 'env DefaultTree ref * 'env Tree BoundTree option ref option
and 'env DefaultTree =
UNBUILT of ('env -> 'env Tree)
* 'env Tree BoundTree option ref * 'env * bool
| UNBUILT' of ('env -> 'env Tree)
* 'env Tree BoundTree option ref * 'env * bool
| BUILT of 'env Tree BoundTree
| ERROR of (DataTypes.Type * Ident.ValId list) list -> unit
datatype expression =
&& of expression * expression |
|| of expression * expression |
== of Ident.LongValId * Ident.LongValId |
TRUE |
FALSE
infix &&
infix ||
infix ==
val show_match : bool ref = ref(false)
val _ = MVCounter.reset_counter 0
val next_Matchvar = MVCounter.counter
val Dummy_MV = (~1):Matchvar
fun rev_app([], ys) = ys
| rev_app(x :: xs, ys) = rev_app(xs, x :: ys)
fun isnt_nil [] = false
| isnt_nil _ = true
fun rev_filter f =
let
fun rf([], acc) = acc
| rf([] :: xs, acc) = rf(xs, acc)
| rf(x :: xs, acc) = rf(xs, x :: acc)
in
rf
end
val rev_filter =
fn f => fn x => rev_filter f (x, [])
fun printSCon (Ident.INT (x,_)) = x
| printSCon (Ident.REAL (x,_)) = x
| printSCon (Ident.WORD (x,_)) = x
| printSCon (Ident.STRING x) = x
| printSCon (Ident.CHAR x) = x
local
fun hd (x::_) = x
| hd _ = Crash.impossible "hd:match"
fun tl (_::xs) = xs
| tl _ = Crash.impossible "tl:match"
fun fetch_pat env =
let
val set_env = case env of
NONE => (fn _ => ())
| (SOME(matchvar,env)) =>
fn (vi, ty) => (env := (matchvar,vi,ty)::(!env))
fun fp (Absyn.LAYEREDpat((vi,ty),pat)) =
(set_env (vi,ty); fp pat)
| fp (Absyn.TYPEDpat(pat,_,_)) = fp pat
| fp (Absyn.VALpat((Ident.LONGVALID(_,vi as Ident.VAR _), ty),_)) =
(set_env (vi,ty); Absyn.WILDpat Ident.Location.UNKNOWN)
| fp pat = pat
in
fp
end
type 'env pattern =
((Matchvar * Ident.LongValId option) option * Absyn.Pat) list
* ((Absyn.Exp * int * (Ident.LongValId * (Matchvar * 'env DefaultTree ref)) list)
* (Matchvar * Ident.ValId
* (DataTypes.Type ref * Absyn.RuntimeInfo ref)) list)
val match_vars : Matchvar list list ref = ref []
infix :::
fun x:::(xs,y) = (x::xs,y)
local
fun fetch_environment'' rootvar env =
let
val mv = !match_vars
val l = length mv
fun member(mvar,_) = Lists.member(rootvar, Lists.nth(l-mvar, mv))
fun insert() = (match_vars := [rootvar]::mv; l+1)
fun fetch_environment' [] _ = (insert(),CON1 [])::env
| fetch_environment' (entry::envs) env =
if member entry then
entry::(rev_app(env, envs))
else
fetch_environment' envs (entry::env)
in
fetch_environment' env []
end
in
fun simple_fetch_env env rootvar =
map (fn e => fetch_environment'' rootvar e) env
fun complex_fetch_env env rootvar =
map (fn e => (fetch_environment'' rootvar e, [])) env
fun fetch_environment' env rootvar =
map
(fn ((env,mvars)) => (fetch_environment'' rootvar env,mvars))
env
fun power n = Bits.lshift(1, n)
fun bit_set(n, [], acc) =
if n >= 29 then
bit_set(n-29, [], 0 :: acc)
else
if n < 0 then
Crash.impossible"bit_set: negative integer"
else
rev(power n :: acc)
| bit_set(n, word :: words, acc) =
if n >= 29 then
bit_set(n-29, words, word :: acc)
else
if n < 0 then
Crash.impossible"bit_set: negative integer"
else
rev_app(Bits.orb(power n, word) :: acc, words)
fun is_set(n, []) = false
| is_set(n, word :: words) =
if n < 0 then
Crash.impossible"is_set: negative integer"
else
if n >= 29 then
is_set(n-29, words)
else Bits.andb(word, power n) <> 0
val redundant : bool ref = ref(true)
fun update_env (pat,mvar) env ty =
let
exception Fetch
val _ = redundant := true
fun insert (mvar,mvar') =
let
val mv = !match_vars
val l = length mv
fun add_member mvars =
if Lists.member(mvar, mvars) then mvars else mvar :: mvars
fun insert([], _, _) = Crash.impossible "insert:update_env:match"
| insert(mvars::mvarss, acc, 0) =
rev_app(acc, add_member mvars :: mvarss)
| insert(mvars::mvarss, acc, n) = insert(mvarss, mvars :: acc, n-1)
in
match_vars := insert (mv, [], l-mvar');
mvar'
end
datatype scon_type =
INT of string
| WORD of string
| OTHER of string
fun scon_test(INT s, t) =
Scons.scon_eqval(Ident.INT(s, Ident.Location.UNKNOWN),
Ident.INT(t, Ident.Location.UNKNOWN))
| scon_test(WORD s, t)=
Scons.scon_eqval(Ident.WORD(s, Ident.Location.UNKNOWN),
Ident.WORD(t, Ident.Location.UNKNOWN))
| scon_test(OTHER s, t) = s = t
fun scon_val(INT s) = s
| scon_val(WORD s) = s
| scon_val(OTHER s) = s
fun member(s as INT _, ss) = List.exists (fn t => scon_test(s, t)) ss
| member(s as WORD _, ss) = List.exists (fn t => scon_test(s, t)) ss
| member(OTHER s, ss) = Lists.member(s, ss)
fun make_scon_entry scon =
let
val scon_val = scon_val scon
in
(fn env' as (mvar',SCON1 scons)::env =>
if member(scon,scons) then (env', [])
else
(redundant := false;
((mvar',SCON1(scon_val::scons))::env,
(mvar',SCON2(scon_val,mvar))::env))
| env' as (mvar',CON1[])::env =>
(redundant := false;
((mvar',SCON1[scon_val])::env,
(mvar',SCON2(scon_val,mvar))::env))
| env' as (mvar'',SCON2(scon',mvar'))::env =>
if scon_test(scon, scon') then
(redundant := false;
([],
if mvar = Dummy_MV then env'
else
let
val env =
case fetch_environment'' mvar' env of
(mv,cons)::env => (insert(mvar,mv),cons)::env
| _ => Crash.impossible "1:make_entry:update_env:match"
in
(mvar'',SCON2(scon',mvar))::env
end))
else
(env', [])
| _ => Crash.impossible "2:make_entry:update_env:match")
end
val make_entry =
case pat of
Absyn.WILDpat _ =>
(fn (mvar',cons)::env => ([],(insert(mvar,mvar'),cons)::env)
| _ => Crash.impossible "3:make_entry:update_env:match")
| Absyn.SCONpat (scon, _) =>
let
val scon_val = printSCon scon
in
make_scon_entry
(case scon of
Ident.INT _ => INT scon_val
| Ident.WORD _ => WORD scon_val
| _ => OTHER scon_val)
end
| _ =>
let
val con =
case pat of
Absyn.VALpat((con,_),_) => con
| Absyn.APPpat((con,_),_,_,_) => con
| _ => Crash.impossible "4:make_entry:update_env:match"
in
case con of
Ident.LONGVALID(_,Ident.EXCON(con)) =>
make_scon_entry(OTHER(Ident.Symbol.symbol_name con))
| _ =>
let
val con' = case pat of
Absyn.VALpat((Ident.LONGVALID(_,con),_),_) => con
| Absyn.APPpat((Ident.LONGVALID(_,con),_),_,_,_) => con
| _ => Crash.impossible "constructor_word:make_entry:match"
val bit' = DataTypes.NewMap.rank'
(#2(TypeUtils.get_valenv(TypeUtils.get_cons_type ty)), con')
in
fn env' as (mvar',CON1(word))::env =>
if is_set(bit', word) then
(env', [])
else
(redundant := false;
((mvar',CON1(bit_set(bit', word, [])))::env,
(mvar',CON2(con,mvar))::env))
| env' as (mvar'',CON2(con',mvar'))::env =>
if con===con' then
(redundant := false;
([],
if mvar = Dummy_MV then env'
else
let
val env =
case fetch_environment'' mvar' env of
(mv,cons)::env => (insert(mvar,mv),cons)::env
| _ => Crash.impossible "5:make_entry:update_env:match"
in
(mvar'',CON2(con',mvar))::env
end))
else (env', [])
| _ => Crash.impossible "6:make_entry:update_env:match"
end
end
fun update_env env =
let
val (env1, env2) =
Lists.reducel
(fn ((l1, l2), e) =>
let
val (e1, e2) = make_entry e
in
(e1 :: l1, e2 :: l2)
end)
(([], []), env)
in
(rev_filter isnt_nil env1, rev_filter isnt_nil env2)
end
in
(update_env env, !redundant)
end
end
fun all_constructors_present env lvis ty arity =
let
fun set_n_bits(n, acc) =
if n < 0 then
acc
else
set_n_bits(n-1, Bits.lshift(acc, 1) + 1)
val every_bit = set_n_bits(28, 0)
fun all_bits_set(n, acc) =
if n < 0 then
Crash.impossible "all_bits_set:all_constructors_present:match"
else
if n >= 29 then
all_bits_set(n-29, every_bit :: acc)
else
rev(set_n_bits(n, 0) :: acc)
val word =
case arity of
0 => []
| _ => all_bits_set (arity-1, [])
val arity_non_zero = arity <> 0
fun char_makestring i = "#\"" ^ MLWorks.String.ml_string((str o chr) i, 3) ^ "\""
fun string_list_to_ints s_list =
Lists.reducel
(fn (ints, s) => bit_set(ord (String.sub(s, 0)), ints, []))
([], s_list)
fun fetch_missing_constructors word =
let
val ve = #2(TypeUtils.get_valenv ty)
val cons = DataTypes.NewMap.domain ve
in
(ty,
List.filter
(fn con =>
let
val bit' = DataTypes.NewMap.rank'(ve,con)
in
not(is_set(bit', word))
end) cons)
end
fun fetch_missing_char_constructors word' =
let
fun int_list(0, acc) = 0 :: acc
| int_list(n, acc) = int_list(n-1, n :: acc)
val word = int_list(arity-1, [])
in
map
(fn s => Ident.CON(Ident.Symbol.find_symbol(char_makestring s)))
(List.filter (fn i => not(is_set(i, word'))) word)
end
fun all_constructors_present nil result = result
| all_constructors_present ([]::env) result = all_constructors_present env result
| all_constructors_present ((entry as (_,CON1(word'))::_)::env) (result as (env',_,missing_cons)) =
if word = word' then
all_constructors_present env result
else
all_constructors_present env
(entry::env',false,
(fn ()=>fetch_missing_constructors word')::missing_cons)
| all_constructors_present ((entry as (_,CON2(_))::_)::env) (env',_,missing_cons) =
all_constructors_present env (entry::env',false,missing_cons)
| all_constructors_present ((entry as (_,SCON1 strings)::_)::env) (result as (env',_,missing_cons)) =
let
val word' = if arity_non_zero then string_list_to_ints strings else []
in
if arity_non_zero andalso word = word' then
all_constructors_present env result
else
all_constructors_present env
(entry::env',false,
(fn ()=>(ty,
if arity_non_zero then
fetch_missing_char_constructors word'
else
[]))::missing_cons)
end
| all_constructors_present ((entry as (_,SCON2 _)::_)::env) (env',_,missing_cons) =
all_constructors_present env (entry::env',false,missing_cons)
| all_constructors_present _ _ = Crash.impossible "all_constructors_present:match"
in
all_constructors_present env (nil,true,nil)
end
val redundant_patterns : (int * expression) list ref = ref nil
val inexhaustive : (DataTypes.Type * Ident.ValId list) list option ref =
ref NONE
val record = ref false
val record_type : DataTypes.Type option ref = ref NONE
val label_count : int ref = ref 0
fun next_label() =
let
val lc = (!label_count)+1
in
label_count := lc;
lc
end
fun make_default new_default_tree old_default missing_constructors =
if old_default then
case new_default_tree of
ref(ERROR inexhaustive)=>
inexhaustive(map (fn f=>f()) missing_constructors)
| _ => ()
else
case new_default_tree of
ref(UNBUILT'(make_tree,binding,env,_)) =>
let
val tree = make_tree env
val boundtree = ref(INL(next_label(),tree))
in
binding := SOME boundtree;
new_default_tree := BUILT boundtree
end
| ref(ERROR inexhaustive)=>
inexhaustive(map (fn f=>f()) missing_constructors)
| _ => ()
fun MakeDefaultTree (default_tree as ref(UNBUILT(default,binding,env',_))) env =
(default_tree := UNBUILT'(default,binding,env@env',true);
default_tree)
| MakeDefaultTree (default_tree as ref(UNBUILT'(default,binding,env',old_default))) env =
(default_tree := UNBUILT'(default,binding,env@env',old_default);
default_tree)
| MakeDefaultTree default_tree _ = default_tree
fun make_binding(default_tree as ref(UNBUILT(default,binding,env,old_default))) =
(default_tree := UNBUILT(default,binding,env,true);
if old_default then NONE
else SOME binding)
| make_binding(ref(UNBUILT' _)) = NONE
| make_binding(ref(BUILT _)) = NONE
| make_binding(ref(ERROR _)) = NONE
fun generate_tree'' generate_tree patterns
(default as (_,_,new_default_tree)) env old_default =
let
val tree = generate_tree patterns default env
in
(make_default new_default_tree old_default nil;
tree)
end
fun generate_tree rootvars ((nil,((exp,n,exns),env))::patterns) _ _ =
let
fun insert_redundant_patterns exns =
let
exception Insert
fun insert_redundant_patterns m =
let
fun insert_redundant_pattern (_,nil) = []
| insert_redundant_pattern ((n,exp),ns) =
let
fun insert_redundant_pattern nil = []
| insert_redundant_pattern ((n',exp')::ns) =
if n=n' then (n,case exp' of
TRUE => exp
| _ => exp&&exp')::ns
else (n',exp')::insert_redundant_pattern ns
in
insert_redundant_pattern ns
end
fun fetch_exns _ nil _ = raise Insert
| fetch_exns 1 (exn::exns) exns' = (exn,rev exns')
| fetch_exns m (exn::exns) exns' = fetch_exns (m-1) exns (exn::exns')
fun expression exns exn =
let
exception True
fun expression' (nil,nil) = TRUE
| expression' ((lvi,_)::exn,(lvi',_)::exn') =
if lvi===lvi' then expression' (exn,exn')
else
lvi==lvi'&&expression' (exn,exn')
| expression' _ = Crash.impossible "expression':insert_redundant_patterns:match"
fun expression nil = FALSE
| expression ((_,exn')::exns) =
case expression' (exn,exn') of
TRUE =>
raise True
| bexp => bexp || expression exns
in
expression exns
handle True => TRUE
end
val (((_,n,_),exn),exns') = fetch_exns m exns nil
in
(redundant_patterns :=
insert_redundant_pattern((n,expression exns' exn),!redundant_patterns);
insert_redundant_patterns(m+1))
end
handle Insert => ()
in
insert_redundant_patterns 2
end
fun generate_exception_tree ((_,[])::_) _ =
Crash.impossible "1:generate_exception_tree:match"
| generate_exception_tree [] _ =
Crash.impossible "2:generate_exception_tree:match"
| generate_exception_tree exns default_tree =
let
fun fetch_constructor_info ((_,(_,info)::_)::_) = info
| fetch_constructor_info _ =
Crash.impossible "fetch_constructor_info:generate_exception_tree:match"
val (mv,default_tree') = fetch_constructor_info exns
fun EXNsplit (exns as (_,(exn,_)::_)::_) =
let
fun insplit [] (e1,e2) = (e1, rev e2)
| insplit ((exn'' as (exp,(exn',_)::exns))::el) (e1,e2) =
if exn' === exn then
insplit el ((exp,exns)::e1,e2)
else
insplit el (e1,exn''::e2)
| insplit _ _ =
Crash.impossible "insplit:EXNsplit:generate_exception_tree:match"
in
case insplit exns ([],[]) of
(e1 as (exp,_)::_, e2) => (e1, e2, (exp,exn))
| _ =>
Crash.impossible "1:EXNsplit:generate_exception_tree:match"
end
| EXNsplit _ =
Crash.impossible "2:EXNsplit:generate_exception_tree:match"
fun make_tree_list (exns as _::_) (trees,boundtrees) default_tree =
let
val (exns,exns',(exp,exn)) = EXNsplit exns
in
case exns' of
nil =>
(case exns of
(_,[])::_ =>
((exn,Dummy_MV,INL(LEAF(exp)))::trees,rev(boundtrees))
| _ =>
((exn,Dummy_MV,
INL(generate_exception_tree exns default_tree))::trees,
rev(boundtrees)))
| _ =>
case exns of
(_,[])::_ =>
make_tree_list exns'
((exn,Dummy_MV,INL(LEAF(exp)))::trees,boundtrees) default_tree
| _ =>
let
val tree =
ref(INL
(next_label(),
generate_exception_tree exns default_tree))
val tree_entry = (exn,Dummy_MV,INR tree)
val default_tree =
case default_tree of
INL _ =>
ref(INL
(next_label(),
CONSTRUCTOR(Types.exn_type,mv,[tree_entry],default_tree,[],true)))
| INR
(ref(INL
(_, CONSTRUCTOR(ty,mv,trees,default_tree,boundtrees,b)))) =>
ref(INL
(next_label(),
CONSTRUCTOR(ty,mv,tree_entry::trees,default_tree,boundtrees,b)))
| _ =>
Crash.impossible "1:make_tree_list:generate_exception_tree:match"
in
make_tree_list exns'
(tree_entry::trees,
INR(default_tree,Dummy_MV)
::INR(tree,Dummy_MV)::boundtrees)
(INR(default_tree))
end
end
| make_tree_list _ _ _ =
Crash.impossible "2:make_tree_list:generate_exception_tree:match"
fun choose_default_tree(INL _) =
INL(SOME default_tree')
| choose_default_tree(INR _) = default_tree
val default_tree = choose_default_tree default_tree
val (trees,boundtrees) =
make_tree_list (rev exns) (nil,nil) default_tree
in
CONSTRUCTOR(Types.exn_type,mv,trees,default_tree,boundtrees,true)
end
in
(redundant_patterns :=
List.filter (fn (m, _) => m <> n) (!redundant_patterns));
case exns of
nil => LEAF(exp,n,env)
| _ =>
let
fun fetch_exns exns =
map
(fn (_,((exp,n,exns),env)) => ((exp,n,env),rev exns))
exns
fun fetch_default_tree ((_,(_,default_tree))::_) =
INL(SOME default_tree)
| fetch_default_tree _ =
Crash.impossible "fetch_default_tree:generate_tree:match"
val exns' = ((exp,n,env),rev exns)::fetch_exns patterns
in
insert_redundant_patterns exns';
generate_exception_tree exns' (fetch_default_tree exns)
end
end
| generate_tree (rootvars' as rootvar::rootvars) patterns (separate,exception_tree,default_tree) env =
let
fun the_construc(Absyn.APPpat((lvi,_),_,_,_)) = lvi
| the_construc(Absyn.VALpat((lvi,_),_)) = lvi
| the_construc(Absyn.SCONpat (scon, _)) =
Ident.LONGVALID
(Ident.mkPath [], Ident.VAR(Ident.Symbol.find_symbol(printSCon scon)))
| the_construc(Absyn.TYPEDpat(p,_,_)) = the_construc p
| the_construc(Absyn.LAYEREDpat(_,p)) = the_construc p
| the_construc(Absyn.WILDpat _) =
Crash.impossible "the_construc:generate_tree:match:WILD"
| the_construc(Absyn.RECORDpat _) =
Crash.impossible "the_construc:generate_tree:match:RECORD"
local
datatype irredundancy = REDUNDANT | SWAPPABLE | IRREDUNDANT
fun swap_patterns pats patterns =
let
fun swap_patterns _ nil patterns = (nil,(nil,rev patterns))
| swap_patterns pats (patterns' as pattern::patterns) patterns'' =
let
fun irredundancy nil nil = REDUNDANT
| irredundancy ((_,pat1)::pats1) ((_,pat2)::pats2) =
let
fun Irredundancy pat1 pat2 =
(case (pat1,pat2) of
(Absyn.WILDpat _,_) => REDUNDANT
| (Absyn.VALpat ((Ident.LONGVALID(_,Ident.VAR _),_),_),_) => REDUNDANT
| (_,Absyn.WILDpat _) => IRREDUNDANT
| (_, Absyn.VALpat ((Ident.LONGVALID(_,Ident.VAR _),_),_)) => IRREDUNDANT
| (Absyn.LAYEREDpat(_,pat1),pat2) => Irredundancy pat1 pat2
| (Absyn.TYPEDpat(pat1,_,_),pat2) => Irredundancy pat1 pat2
| (pat1,Absyn.LAYEREDpat(_,pat2)) => Irredundancy pat1 pat2
| (pat1,Absyn.TYPEDpat(pat2,_,_)) => Irredundancy pat1 pat2
| (Absyn.APPpat((Ident.LONGVALID( _, val_id1),_),arg,_,_),
Absyn.APPpat((Ident.LONGVALID( _, val_id2),_),arg',_,_)) =>
(case Irredundancy'' val_id1 val_id2 of
REDUNDANT => Irredundancy arg arg'
|irredundancy => irredundancy)
| (Absyn.VALpat((Ident.LONGVALID( _, val_id1),_),_),
Absyn.VALpat((Ident.LONGVALID( _, val_id2),_),_)) =>
Irredundancy'' val_id1 val_id2
| (Absyn.RECORDpat(name_pat_list,_,_),
Absyn.RECORDpat(name_pat_list',_,_)) =>
Irredundancy' name_pat_list name_pat_list'
| (_,_) => SWAPPABLE)
and Irredundancy' nil nil = REDUNDANT
| Irredundancy' ((lab,pat1)::pats1) (pats2 as _::_) =
let
fun fetch_pat([], acc) =
(rev acc, Absyn.WILDpat Ident.Location.UNKNOWN)
| fetch_pat((entry as (lab2,pat))::pats, acc) =
if Ident.lab_eq(lab,lab2) then
(rev_app(acc, pats), pat)
else
fetch_pat(pats, entry :: acc)
val (pats2,pat2) = fetch_pat(pats2, [])
in
case Irredundancy pat1 pat2 of
SWAPPABLE => SWAPPABLE
| IRREDUNDANT =>
(fn REDUNDANT => IRREDUNDANT
| other => other) (Irredundancy' pats1 pats2)
| REDUNDANT => Irredundancy' pats1 pats2
end
| Irredundancy' pats1 nil =
let
fun non_WILDpat_in nil = false
| non_WILDpat_in((_,Absyn.WILDpat _)::pats) =
non_WILDpat_in pats
| non_WILDpat_in((_,Absyn.VALpat((Ident.LONGVALID(_, Ident.VAR _),_),_))::pats) =
non_WILDpat_in pats
| non_WILDpat_in((lab,Absyn.LAYEREDpat(_,pat))::pats) =
non_WILDpat_in((lab,pat)::pats)
| non_WILDpat_in((lab,Absyn.TYPEDpat(pat,_,_))::pats) =
non_WILDpat_in((lab,pat)::pats)
| non_WILDpat_in _ = true
in
if non_WILDpat_in pats1 then IRREDUNDANT
else REDUNDANT
end
| Irredundancy' nil pats2 = REDUNDANT
and Irredundancy'' val_id val_id' =
(case (val_id,val_id') of
(Ident.CON name,Ident.CON name') =>
if name=name' then REDUNDANT else SWAPPABLE
|(Ident.EXCON name,Ident.EXCON name') =>
if name=name' then REDUNDANT else IRREDUNDANT
| _ => Crash.impossible
"Irredundancy'':Irredundancy:generate_tree:match")
in
case Irredundancy pat1 pat2 of
SWAPPABLE => SWAPPABLE
| IRREDUNDANT =>
(fn REDUNDANT => IRREDUNDANT
| other => other) (irredundancy pats1 pats2)
| REDUNDANT => irredundancy pats1 pats2
end
| irredundancy _ _ =
Crash.impossible "irredundancy:generate_tree:match"
fun irredundancy' nil _ = SWAPPABLE
| irredundancy' (pats::rest) pats' =
case irredundancy pats pats' of
SWAPPABLE => irredundancy' rest pats'
| other => other
in
case pattern of
((_,Absyn.WILDpat _)::pats',_) =>
swap_patterns (pats'::pats) patterns (pattern::patterns'')
| ((option,Absyn.VALpat((Ident.LONGVALID(_,vi as Ident.VAR _),
ty),_))::pats',(exp,env)) =>
swap_patterns (pats'::pats) patterns (((option,Absyn.WILDpat Ident.Location.UNKNOWN)::pats',
(exp,(rootvar,vi,ty)::env))::patterns'')
| ((option,Absyn.LAYEREDpat((vi,ty),pat))::pats',(exp,env)) =>
swap_patterns pats (((option,pat)::pats',
(exp,(rootvar,vi,ty)::env))::patterns) patterns''
| ((option,Absyn.TYPEDpat(pat,_,_))::pats',env) =>
swap_patterns pats (((option,pat)::pats',env)::patterns) patterns''
| ((_,pat)::pats',_) =>
(case irredundancy' pats pats' of
REDUNDANT => swap_patterns pats patterns patterns''
| IRREDUNDANT => (nil,(patterns',rev patterns''))
| SWAPPABLE =>
(case pat of
Absyn.RECORDpat(_,_,ref ty) =>
(record := true;record_type := SOME(ty))
| _ => ();
pattern:::swap_patterns pats patterns patterns''))
| _ => Crash.impossible "swap_patterns:generate_tree:match"
end
in
swap_patterns [pats] patterns nil
end
in
fun separate_defaults nil = (nil,nil)
| separate_defaults (pat::pats) =
case pat of
((_,Absyn.WILDpat _)::pats',_) =>
let
val (pats,(defaults,defaults')) = swap_patterns pats' pats
val (defaults,default_defaults) = separate_defaults defaults
in
(pats,(pat::defaults')::
(case defaults of
nil => default_defaults
| _ => defaults::default_defaults))
end
| ((option,Absyn.VALpat((Ident.LONGVALID(_,vi as Ident.VAR _),
ty),_))::pats',(exp,env)) =>
let
val (pats,(defaults,defaults')) = swap_patterns pats' pats
val (defaults,default_defaults) = separate_defaults defaults
in
(pats,(((option,Absyn.WILDpat Ident.Location.UNKNOWN)::pats',
(exp,(rootvar,vi,ty)::env))::defaults')::
(case defaults of
nil => default_defaults
| _ => defaults::default_defaults))
end
| ((option,Absyn.LAYEREDpat((vi,ty),pat))::pats',(exp,env)) =>
separate_defaults (((option,pat)::pats',
(exp,(rootvar,vi,ty)::env))::pats)
| ((option,Absyn.TYPEDpat(pat,_,_))::pats',env) =>
separate_defaults (((option,pat)::pats',env)::pats)
| ((_,pat')::_,_) =>
(case pat' of
Absyn.RECORDpat(_,_,ref ty) =>
(record := true;record_type := SOME(ty))
| _ => ();
pat:::separate_defaults pats)
| _ => Crash.impossible "separate_defaults:generate_tree:match"
end
fun separate_defaults' nil = (nil,nil)
| separate_defaults' pats =
let
fun duplicate_pattern _ nil = nil
| duplicate_pattern pat' pats =
let
fun duplicate_pattern [] = []
| duplicate_pattern (pat::pats) =
case pat of
((opt,Absyn.WILDpat _)::pats',exp) =>
((opt,pat')::pats',exp)::duplicate_pattern pats
| _ => []
in
duplicate_pattern pats
end
fun duplicate_pattern' _ nil = ([],[])
| duplicate_pattern' pat' pats =
let
fun duplicate_pattern [] (pats,pats') = (rev pats,pats')
| duplicate_pattern (pat::pats) (pats''',pats') =
case pat of
((opt,Absyn.WILDpat _)::pats'',exp) =>
duplicate_pattern pats (pat::pats''',((opt,pat')::pats'',exp)::pats')
| ((option,Absyn.VALpat((Ident.LONGVALID(_,vi as Ident.VAR _),
ty),_))::pats'',(exp,env)) =>
duplicate_pattern pats (pat::pats''',((option,pat')::pats'',
(exp,(rootvar,vi,ty)::env))::pats')
| ((option,Absyn.LAYEREDpat((vi,ty),pat))::pats'',(exp,env)) =>
duplicate_pattern (((option,pat)::pats'',
(exp,(rootvar,vi,ty)::env))::pats) (pats''',pats')
| ((option,Absyn.TYPEDpat(pat,_,_))::pats'',env) =>
duplicate_pattern (((option,pat)::pats'',env)::pats) (pats''',pats')
| ((_,Absyn.RECORDpat(_))::_,_) => duplicate_pattern pats (pats''',pat::pats')
| ((_,pat'')::_,_) =>
if the_construc pat' === the_construc pat'' then
duplicate_pattern pats (pats''',pat::pats')
else
duplicate_pattern pats (pat::pats''',pats')
| _ => Crash.impossible "duplicate_pattern':separate_defaults':generate_tree:match"
in
duplicate_pattern pats ([],[])
end
fun separate_wildpats ((wildpat as ((_,Absyn.WILDpat _)::_,_))::pats) wildpats =
separate_wildpats pats (wildpat::wildpats)
| separate_wildpats pats wildpats =
(rev pats,case wildpats of
[] => []
| _ => [wildpats])
fun duplicate_patterns nil pats = separate_wildpats pats []
| duplicate_patterns (pat::pats) pats' =
case pat of
((_,Absyn.WILDpat _)::_,_) => duplicate_patterns pats (pat::pats')
| ((option,Absyn.VALpat((Ident.LONGVALID(_,vi as Ident.VAR _),
ty),_))::pats'',(exp,env)) =>
duplicate_patterns pats (((option,Absyn.WILDpat Ident.Location.UNKNOWN)::pats'',
(exp,(rootvar,vi,ty)::env))::pats')
| ((option,Absyn.LAYEREDpat((vi,ty),pat))::pats'',(exp,env)) =>
duplicate_patterns (((option,pat)::pats'',
(exp,(rootvar,vi,ty)::env))::pats) pats'
| ((option,Absyn.TYPEDpat(pat,_,_))::pats'',env) =>
duplicate_patterns (((option,pat)::pats'',env)::pats) pats'
| ((_,pat')::_,_) =>
(case pat' of
Absyn.RECORDpat(_,_,ref ty) =>
(record := true;
record_type := SOME(ty))
| _ => ();
let
val (pats,pats'') = duplicate_pattern' pat' pats
in
duplicate_patterns pats
(pats'@pats''@(pat::duplicate_pattern pat' pats'))
end)
| _ => Crash.impossible "duplicate_patterns:separate_defaults':generate_tree:match"
in
duplicate_patterns pats []
end
val separate_defaults =
if exception_tree then separate_defaults' else separate_defaults
fun generate_tree' (patterns as ((option,_)::_,_)::_) default env =
(case option of
NONE => generate_tree rootvars patterns default env
| SOME(matchvar,_) =>
generate_tree (matchvar::rootvars) patterns default env)
| generate_tree' patterns default env = generate_tree rootvars patterns default env
fun make_construc pat opt =
case opt of
NONE => the_construc pat
| SOME(_,SOME(construc)) => construc
| _ => Crash.impossible "make_construc:generate_tree:match"
val (non_defaults,defaults') =
if separate then (record := false;
separate_defaults patterns)
else (patterns,nil)
val (defaults,include_non_defaults) =
case non_defaults of
nil => (tl defaults',true)
| _ => (defaults',false)
fun flat nil = nil
| flat (xs::xss) = xs@flat xss
fun strip nil = nil
| strip ((pat::pats',env)::rest) = (pats',env)::strip rest
| strip _ = Crash.impossible "strip:generate_tree:match"
local
val don't = not
in
fun new_default_tree nil = (true,default_tree)
| new_default_tree defaults =
let
fun new_default_tree nil _ = default_tree
| new_default_tree (default::defaults) include_non_defaults =
let
val (default,separate,generate_tree) =
if include_non_defaults then (default,false,generate_tree rootvars')
else (strip default,true,generate_tree')
val old_default =
case defaults of
[] => true
| _ => false
in
ref(UNBUILT(fn env =>
generate_tree'' generate_tree default (separate,false,
new_default_tree defaults (don't include_non_defaults))
env old_default,
ref NONE,nil,false))
end
in
(false,new_default_tree defaults include_non_defaults)
end
end
local
fun CONsplit (add_pat,exception_constructor) (patterns as (((opt,pat)::_,_),tree)::_) =
let
val construc_pat = make_construc pat opt
val same_pattern =
case exception_constructor of
SOME(_) =>
let
fun pattern_type (Absyn.APPpat((_,ref ty),_,_,_)) = ty
| pattern_type (Absyn.VALpat((_,(ref ty,_)),_)) = ty
| pattern_type _ = Crash.impossible "pattern_type:CONsplit:match"
val construc_pat_type = pattern_type pat
in
(fn (p,lvi) =>
if lvi === construc_pat then
(true,false)
else
if Types.type_eq(pattern_type p, construc_pat_type, true, true) then
(case tree of
SOME(tree) =>
(case !tree of
NONE =>
(tree :=
SOME(INL(ref(
INL(0,LEAF(Absyn.RECORDexp(nil),0,nil)))));
(true,true))
| SOME(_) => (true,true))
| NONE => Crash.impossible "same_pattern:CONsplit:match")
else (false,true))
end
| NONE =>
fn (_,lvi) => (lvi === construc_pat,false)
fun add_exn (lvi,info,exp' as ((exp,n,exns),mvs)) = ((exp,n,(lvi,info)::exns),mvs)
fun add_exns pats =
let
val info =
case exception_constructor of
SOME(info) => info
| NONE => Crash.impossible "info:add_exns:CONsplit:match"
fun add_exns nil = nil
| add_exns ((lvi,(pp,exp))::pats) = (pp,add_exn(lvi,info,exp))::add_exns pats
in
add_exns pats
end
fun insplit [] (p1,p2) =
(case tree of
SOME(ref(SOME(_))) => add_exns (rev p1)
| _ => map (fn (_,p)=>p) (rev p1), rev p2, pat, tree)
| insplit ((pats as ((opt,p)::pp,exp),tree')::pl) (p1,p2) =
let
val lvi = make_construc p opt
val (same_pattern,different_exception_pattern) = same_pattern(p,lvi)
in
if same_pattern then
insplit pl ((lvi,(add_pat p pp,exp))::p1,
if different_exception_pattern then (pats,tree)::p2
else p2)
else
insplit pl (p1,(pats,tree')::p2)
end
| insplit _ _ =
Crash.impossible "insplit:CONsplit:generate_tree:match"
in
insplit patterns ([],[])
end
| CONsplit _ _ =
Crash.impossible "CONsplit:generate_tree:match"
in
fun scon_rule patterns Type =
let
val arity =
if Types.type_eq(Type, Types.char_type, false, false)
orelse Types.type_eq(Type, Types.word8_type, false, false)
orelse Types.type_eq(Type, Types.int8_type, false, false)
then 256
else 0
val SCONsplit = CONsplit ((fn _ => fn pats => pats),NONE)
val (old_default,default_tree) = new_default_tree defaults
val binding = make_binding default_tree
val env = simple_fetch_env env rootvar
fun Make_tree_list nil result = result
| Make_tree_list patterns (trees,lvis,env) =
let
val (same_patterns,rest,pat,_) = SCONsplit patterns
in
(case pat of
Absyn.SCONpat (scon, _) =>
let
val ((env, new_env),redundant) =
update_env (pat,Dummy_MV) env Type
in
if redundant then
Make_tree_list rest (trees,lvis,env)
else
Make_tree_list rest
((scon,generate_tree' same_patterns (true,exception_tree,default_tree) new_env)::trees,
scon::lvis,
env)
end
| _ => Crash.impossible
"Make_tree_list:scon_rule:generate_tree:match")
end
val (trees,lvis,env) = Make_tree_list (map (fn pat=>(pat,NONE)) patterns) (nil,nil,env)
val (env, exhaustive, missing_cons) =
all_constructors_present env (INL lvis) Type arity
val (default, default') =
if exhaustive then
(NONE, NONE)
else
let
val def = MakeDefaultTree default_tree env
in
(SOME def, SOME def)
end
in
(make_default default_tree old_default missing_cons;
case trees of
[] => DEFAULT
(case default of
SOME default => (default, binding)
| _ => Crash.impossible "DEFAULT:scon_rule:match")
| _ =>
SCON(rootvar, trees, default', binding, Types.sizeof Type))
end
fun constructor_rule patterns Type =
let
val (old_default,default_tree) = new_default_tree defaults
val (exception_constructor,info) =
case patterns of
((_,Absyn.APPpat((Ident.LONGVALID(_,Ident.EXCON(_)),_),_,_,_))::_,_)::_ =>
(SOME(rootvar,default_tree),fn ()=>SOME(ref NONE))
| ((_,Absyn.VALpat((Ident.LONGVALID(_,Ident.EXCON(_)),(ref ty,_)),_))::_,_)::_ =>
(SOME(rootvar,default_tree),fn ()=>SOME(ref NONE))
| _ => (NONE,fn ()=>NONE)
val CONsplit = CONsplit
((fn pat => fn pats =>
case pat of
Absyn.APPpat(_,newpat,_,_) => (NONE,newpat)::pats
| _ => pats), exception_constructor)
val binding =
case make_binding default_tree of
SOME(bd) => [INL(bd)]
| NONE => []
val env = simple_fetch_env env rootvar
fun Make_tree_list nil (trees,boundtrees,lvis,env) = (rev trees,rev boundtrees,rev lvis,env)
| Make_tree_list patterns (trees,boundtrees,lvis,env) =
let
val (same_patterns,rest,pat,tree) = CONsplit patterns
in
(case pat of
Absyn.VALpat((lvi,_),_) =>
Make_tree_list rest
(case tree of
NONE =>
let
val ((env, new_env),redundant) = update_env (pat,Dummy_MV) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
((lvi,Dummy_MV,
INL(generate_tree'
same_patterns (true,exception_tree,default_tree) new_env))::trees,
boundtrees,
lvi::lvis,
env)
end
| SOME(tree) =>
(case !tree of
NONE =>
let
val ((env, new_env),redundant) = update_env (pat,Dummy_MV) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
((lvi,Dummy_MV,
INL(generate_tree' same_patterns
(true,exception_tree,default_tree) new_env))::trees,
boundtrees,
lvi::lvis,
env)
end
| SOME(INL(tree')) =>
let
val ((env, new_env),redundant) = update_env (pat,Dummy_MV) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
(tree' :=
INL(next_label(),
generate_tree' same_patterns (true,true,default_tree) new_env);
tree := SOME(INR(tree',Dummy_MV));
((lvi,Dummy_MV,INR(tree'))::trees,
INR(tree',Dummy_MV)::boundtrees,
lvi::lvis,
env))
end
| SOME(INR(tree,_)) =>
((lvi,Dummy_MV,INR(tree))::trees,
boundtrees,
lvi::lvis,
env)))
| Absyn.APPpat((lvi,_),_,_,_) =>
Make_tree_list rest
(case tree of
NONE =>
let
val matchvar = next_Matchvar()
val ((env, new_env),redundant) = update_env (pat,matchvar) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
((lvi,matchvar,
INL(generate_tree (matchvar::rootvars)
same_patterns (true,exception_tree,default_tree) new_env))::trees,
boundtrees,
lvi::lvis,
env)
end
| SOME(tree) =>
(case !tree of
NONE =>
let
val matchvar = next_Matchvar()
val ((env, new_env),redundant) = update_env (pat,matchvar) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
((lvi,matchvar,
INL(generate_tree (matchvar::rootvars) same_patterns
(true,exception_tree,default_tree) new_env))::trees,
boundtrees,
lvi::lvis,
env)
end
| SOME(INL(tree')) =>
let
val matchvar = next_Matchvar()
val ((env, new_env),redundant) = update_env (pat,matchvar) env Type
in
if redundant then (trees,boundtrees,lvis,env)
else
(tree' :=
INL(next_label(),
generate_tree (matchvar::rootvars)
same_patterns (true,true,default_tree) new_env);
tree := SOME(INR(tree',matchvar));
((lvi,matchvar,INR(tree'))::trees,
INR(tree',matchvar)::boundtrees,
lvi::lvis,
env))
end
| SOME(INR(tree,matchvar)) =>
((lvi,matchvar,INR(tree))::trees,
boundtrees,
lvi::lvis,
env)))
| _ => Crash.impossible
"Make_tree_list:constructor_rule:generate_tree:match")
end
val (trees,boundtrees,lvis,env) =
Make_tree_list (map (fn pat=>(pat,info())) patterns) (nil,nil,nil,env)
val Type = TypeUtils.get_cons_type(Types.the_type(Type))
val (env,all_constructors_present,missing_constructors) =
all_constructors_present env (INR lvis) Type (TypeUtils.get_no_cons Type)
val default =
if all_constructors_present then
NONE
else
SOME(MakeDefaultTree default_tree env)
in
(make_default default_tree old_default missing_constructors;
case trees of
[] => DEFAULT(case (default,binding) of
(SOME(default),[]) => (default,NONE)
| (SOME(default),[INL(binding)]) => (default,SOME(binding))
| _ => Crash.impossible "DEFAULT:constructor_rule:match")
| _ => CONSTRUCTOR(Type,rootvar,trees,INL(default),binding@boundtrees,false))
end
end
fun record_rule patterns Type =
let
datatype pattern =
Record of DataTypes.Type * Matchvar * (Ident.Lab * Matchvar) list ref
| NONRecord
val nested_records : (DataTypes.Type * Matchvar *
(Ident.Lab * Matchvar) list ref) list ref = ref nil
fun (x,y):::(xs,ys) = (x::xs,y::ys)
fun fetchLabelledPatterns (ty,matchvar,matchvars) toplevel patterns =
let
fun fetch_labelled_patterns nil patterns =
(case patterns of
[(nil,_)] => nil
| _ => patterns)
| fetch_labelled_patterns (((NONE,pattern),env)::rest) patterns =
let
datatype flag = ABSENT | PRESENT
fun add_dummy_patterns (nil,nil) = nil
| add_dummy_patterns ((record,pats)::patterns,ABSENT::checklist) =
(record,((NONE,Absyn.WILDpat Ident.Location.UNKNOWN),env)::pats)
::add_dummy_patterns (patterns,checklist)
| add_dummy_patterns ((record,pats)::patterns,nil) =
(record,((NONE,Absyn.WILDpat Ident.Location.UNKNOWN),env)::pats)
::add_dummy_patterns (patterns,nil)
| add_dummy_patterns (pats::patterns,PRESENT::checklist) =
pats::add_dummy_patterns (patterns,checklist)
| add_dummy_patterns _ = Crash.impossible
("add_dummy_patterns:fetch_labelled_patterns:"^
"record_rule:match")
in
(case (if toplevel then fetch_pat (SOME(matchvar,env)) pattern
else pattern) of
Absyn.RECORDpat(pats,_,_) =>
let
fun Fetch_labelled_patterns pats patterns =
let
fun Fetch_labelled_patterns nil patterns_checklist = patterns_checklist
| Fetch_labelled_patterns ((lab,pat)::rest)
(patterns_checklist as (patterns,checklist)) =
let
fun duplicate_ABSENT nil = nil
| duplicate_ABSENT (_::rest) = ABSENT::duplicate_ABSENT rest
fun pat_type pat_type' matchvar =
case fetch_pat (SOME(matchvar,env)) pat of
pat as Absyn.RECORDpat((_,_,ref ty)) =>
(case pat_type' of
NONRecord =>
let
val refnil = ref nil
in
(nested_records := (ty,matchvar,refnil)::(!nested_records);
(Record(ty,matchvar,refnil),pat))
end
| record as Record(_) => (record,pat))
| pat => (pat_type',pat)
fun add_labelled_pattern nil nil =
let
val matchvar = next_Matchvar()
val (pat_type,pat) = pat_type NONRecord matchvar
val _ = matchvars := (lab,matchvar)::(!matchvars)
fun add_labelled_wildpat (nil,nil) = nil
| add_labelled_wildpat (((_,pats)::_),checklist) =
let
fun add_labelled_wildpat nil = nil
| add_labelled_wildpat (_::rest) =
((NONE,Absyn.WILDpat Ident.Location.UNKNOWN),env)
::add_labelled_wildpat rest
in
case checklist of
PRESENT::_ => add_labelled_wildpat (tl pats)
| ABSENT::_ => add_labelled_wildpat pats
| nil => add_labelled_wildpat pats
end
| add_labelled_wildpat _ = Crash.impossible
("add_labelled_wildpat:fetch_labelled_patterns:"^
"record_rule:match")
in
([([(lab,matchvar,pat_type)],((NONE,pat),env)
::add_labelled_wildpat patterns_checklist)],[PRESENT])
end
| add_labelled_pattern [(nil,pats)] nil =
let
val matchvar = next_Matchvar()
val (pat_type,pat) = pat_type NONRecord matchvar
val _ = matchvars := (lab,matchvar)::(!matchvars)
in
([([(lab,matchvar,pat_type)],((NONE,pat),env)::pats)],
[PRESENT])
end
| add_labelled_pattern ((entry as ([(label,matchvar,pat_type')],pats))
::patterns) (check::checklist) =
if Ident.lab_eq(lab,label)
then
let val (pat_type,pat) = pat_type pat_type' matchvar
in
(([(label,matchvar,pat_type)],
((NONE,pat),env)::pats)::patterns,
PRESENT::checklist)
end
else (entry,check):::add_labelled_pattern patterns checklist
| add_labelled_pattern
((entry as ([(label,matchvar,pat_type')],pats))::patterns) nil =
if Ident.lab_eq(lab,label)
then
let val (pat_type,pat) = pat_type pat_type' matchvar
in
(([(label,matchvar,pat_type)],
((NONE,pat),env)::pats)::patterns,
PRESENT::duplicate_ABSENT patterns)
end
else (entry,ABSENT):::add_labelled_pattern patterns nil
| add_labelled_pattern _ _ = Crash.impossible
("1:add_labelled_pattern:fetch_labelled_patterns"^
":record_rule:match")
in
case pat of
Absyn.WILDpat _ =>
Fetch_labelled_patterns rest patterns_checklist
| _ =>
Fetch_labelled_patterns rest
(add_labelled_pattern patterns checklist)
end
in
case Fetch_labelled_patterns pats (patterns,nil) of
(_,nil) =>
fetch_labelled_patterns (((NONE,Absyn.WILDpat Ident.Location.UNKNOWN),env)::rest)
patterns
| patterns =>
fetch_labelled_patterns rest (add_dummy_patterns patterns)
end
in
Fetch_labelled_patterns pats patterns
end
| pat as Absyn.WILDpat _ =>
fetch_labelled_patterns rest
(case patterns of
nil => [(nil,[((NONE,pat),env)])]
| [(nil,entry)] =>
[(nil,((NONE,pat),env)::entry)]
| _ => add_dummy_patterns (patterns,nil))
| _ => Crash.impossible "1:fetch_labelled_patterns:record_rule:match")
end
| fetch_labelled_patterns _ _ =
Crash.impossible "2:fetch_labelled_patterns:record_rule:match"
fun fetch_nested_labelled_patterns nil = nil
| fetch_nested_labelled_patterns
(([(label,matchvar,NONRecord)],pats)::patterns) =
(matchvar,pats)::fetch_nested_labelled_patterns patterns
| fetch_nested_labelled_patterns
(([(label,matchvar,Record(record))],pats)::patterns) =
fetchLabelledPatterns record false (rev pats) @
fetch_nested_labelled_patterns patterns
| fetch_nested_labelled_patterns _ =
Crash.impossible "fetch_nested_labelled_patterns:record_rule:match"
in
fetch_nested_labelled_patterns (fetch_labelled_patterns patterns nil)
end
infix <
fun (Absyn.VALpat(_)) < _ = true
| (Absyn.APPpat(_,pat,_,_)) < (Absyn.APPpat(_,pat',_,_)) = pat<pat'
| _ < (Absyn.APPpat(_)) = true
| (Absyn.APPpat(_)) < _ = false
| _ < _ = true
fun inner_sort nil = nil
| inner_sort ((matchvar,patterns)::rest) =
let
fun calculate_scores patterns =
let
fun a':::(a,(b,c,d)) = (a'::a,(b,c,d))
fun split (((NONE,pat),env)::rest) =
let
fun Split nil _ (score,rest,pat) =
(nil,(score,rest,pat))
| Split (((NONE,pattern''),env)::rest)
construc (score,rest_so_far,pattern') =
(case pattern'' of
Absyn.WILDpat _ =>
((SOME(matchvar,NONE),pattern''),env)
:::(Split rest construc (score,rest_so_far,pattern'))
| _ =>
let
val construc_pattern = the_construc pattern''
val pattern =
((SOME(matchvar,SOME(construc_pattern)),
pattern''),env)
in
pattern:::(Split rest construc
(if construc===construc_pattern
then (score+1,rest_so_far,
if pattern'<pattern'' then pattern''
else pattern')
else (score,pattern::rest_so_far,
if pattern'<pattern'' then pattern''
else pattern')))
end)
| Split _ _ _ =
Crash.impossible "1:Split:inner_sort:record_rule:match"
in
case pat of
Absyn.WILDpat _ =>
((SOME(matchvar,NONE),pat),env):::(split rest)
| _ =>
let
val construc_pattern = the_construc pat
in
((SOME(matchvar,SOME(construc_pattern)),
pat),env)
:::(Split rest construc_pattern (1,nil,pat))
end
end
| split ((pattern as
(SOME(_, SOME(construc_pattern)),
pat),env)::rest) =
let
fun Split nil _ (score,rest) =
(score,rest,Absyn.WILDpat Ident.Location.UNKNOWN)
| Split ((pattern as
((SOME(_, SOME(construc_pattern)),_),env))::rest)
construc (score,rest_so_far) =
Split rest construc
(if construc===construc_pattern
then (score+1,rest_so_far)
else (score,pattern::rest_so_far))
| Split _ _ _ =
Crash.impossible "2:Split:inner_sort:record_rule:match"
in
(nil,Split rest construc_pattern (1,nil))
end
| split nil = (nil,(0,nil,Absyn.WILDpat Ident.Location.UNKNOWN))
| split _ = Crash.impossible "split:inner_sort:record_rule:match"
fun calculate_scores patterns =
let
fun calculate_scores (scores,nil) = scores
| calculate_scores (scores,rest) =
let val (_,(score,rest,_)) = split rest
in
calculate_scores (score::scores,rest)
end
val (patterns,(score,rest,pattern)) = split patterns
in
(patterns,pattern,calculate_scores ([score],rest))
end
val score =
Lists.reducel
(fn (score, pat) =>
case pat of
((NONE,Absyn.WILDpat _),_) => score
| _ => score+1)
(0, patterns)
val (patterns,pattern,scores) =
calculate_scores(rev patterns)
in
(score,patterns,pattern,scores)
end
val sort =
Lists.reducel
(fn (score1:int,score2:int) =>
if score1>score2 then score1
else score2)
val (score,patterns,pattern,scores) = calculate_scores patterns
in
(patterns,pattern,score,sort (0,scores))::inner_sort rest
end
val sort = Lists.qsort
(fn ((_,pattern,score11 : int,score21 : int),
(_,pattern',score12,score22)) =>
score11 > score12 orelse
score11 = score12 andalso
(score21>score22 orelse score21=score22
andalso pattern<pattern'))
fun merge remaining_patterns patterns =
let
fun merge nil nil = nil
| merge patterns
((remaining_pattern,(exp,remaining_env))::remaining_patterns) =
let
infix @@
infix @@@
fun (pat,pattern)@@(pats,patterns) = (pat::pats,pattern::patterns)
fun pat@@@(pats,patterns) = (pat::pats,patterns)
fun reduce [(((opt,pattern),_)::patterns,pat,
score1,score2)] =
(case patterns of
nil => ([(opt,pattern)],nil)
| _ =>
([(opt,pattern)],[(patterns,pat,score1,score2)]))
| reduce ((((opt,pattern),_)::patterns,pat,
score1,score2)::rest) =
(case patterns of
nil => (opt,pattern)@@@reduce rest
| _ =>
((opt,pattern),(patterns,pat,score1,score2))@@reduce rest)
| reduce nil = (nil,nil)
| reduce _ =
Crash.impossible "reduce:merge:record_rule:generate_tree:match"
val (patterns,rest) = reduce patterns
in
(patterns@remaining_pattern,(exp,!remaining_env))
::merge rest remaining_patterns
end
| merge _ _ =
Crash.impossible "merge:record_rule:generate_tree:match"
in
merge patterns remaining_patterns
end
fun refer_to_envs _ nil = (nil,nil)
| refer_to_envs f ((pattern::patterns,(exp,env))::rest) =
let val refenv = ref env
in
((f pattern,refenv),(patterns,(exp,refenv))):::refer_to_envs f rest
end
| refer_to_envs _ _ = Crash.impossible "refer_to_envs:match"
val (patterns,remaining_patterns) =
refer_to_envs (fn (opt,pat)=>(opt,pat)) (patterns@flat defaults)
val matchvars = ref nil
val patterns =
(merge remaining_patterns o sort o inner_sort o
fetchLabelledPatterns (Type,rootvar,matchvars) true) patterns
fun reverse nil result = result
| reverse ((ty,mv,mvs)::rest) result = reverse rest ((ty,mv,!mvs)::result)
val nested_records = (Type,rootvar,!matchvars)::reverse (!nested_records) nil
local
fun update_record_env f envs =
let
fun update_record_env' ([env],nil) = f env
| update_record_env' (env,((mvar,mvar')::mvars)) =
update_record_env' (#2(#1(update_env (Absyn.WILDpat Ident.Location.UNKNOWN,mvar)
(simple_fetch_env env mvar')
DataTypes.NULLTYPE)),mvars)
| update_record_env' _ =
Crash.impossible "update_record_env:generate_tree:match"
in
map
(fn (env, mvars) => update_record_env'([env], mvars))
envs
end
in
fun match_matchvars nil env =
Crash.impossible "1:match_matchvars:generate_tree:match"
| match_matchvars (mvars as (_,rootvar,_)::_) env =
let
fun match_matchvars nil env =
update_record_env (fn env=>env) env
| match_matchvars [(_,_,nil)] env =
update_record_env (fn env=>env) env
| match_matchvars ((_,_,nil)::(mvars as (_,rootvar,_)::_)) env =
match_matchvars mvars
(fetch_environment'
(update_record_env (fn env=>(env,nil)) env) rootvar)
| match_matchvars ((ty,root,(mvar' as (lab,mvar))::mvars)::matchvars) env =
let
fun fetch_mvar nil = NONE
| fetch_mvar ((lab',mvar')::l') =
if Ident.lab_eq (lab,lab') then SOME(mvar')
else fetch_mvar l'
in
match_matchvars ((ty,root,mvars)::matchvars)
(map (fn ((entry as (mvs,REC(mvars')))::env,mvars'') =>
(case fetch_mvar mvars' of
SOME(mvar') => (entry::env,(mvar,mvar')::mvars'')
| NONE =>
((mvs,REC(mvar'::mvars'))::env,mvars''))
| ((mvs,CON1([]))::env,mvars'') =>
((mvs,REC(mvar'::nil))::env,mvars'')
| _ =>
Crash.impossible "2:match_matchvars:generate_tree:match")
env)
end
in
match_matchvars mvars (complex_fetch_env env rootvar)
end
end
fun generate_record_tree nil =
generate_tree' patterns (true,exception_tree,default_tree)
(match_matchvars nested_records env)
| generate_record_tree ((ty,matchvar,matchvars)::records) =
RECORD(Types.the_type(ty),matchvar,matchvars,generate_record_tree records)
in
generate_record_tree nested_records
end
in
case non_defaults of
((_,Absyn.RECORDpat(_,_,ref ty))::_,_)::_ =>
(record := false; record_rule non_defaults ty)
| ((_,Absyn.APPpat((_,ref ty),_,_,_))::_,_)::_ => constructor_rule non_defaults ty
| ((_,Absyn.VALpat((Ident.LONGVALID(_,Ident.CON(_)),(ref ty,_)),_))::_,_)::_ =>
constructor_rule non_defaults ty
| ((_,Absyn.VALpat((Ident.LONGVALID(_,Ident.EXCON(_)),(ref ty,_)),_))::_,_)::_ =>
constructor_rule non_defaults ty
| ((_,Absyn.SCONpat (pat, ty))::_,_)::_ =>
scon_rule non_defaults (!ty)
| nil =>
if (!record) then
(record := false;
record_rule (hd defaults')
(case (!record_type) of
NONE =>
Crash.impossible "1:generate_tree:match"
| SOME ty => ty))
else
let val (old_default,new_default_tree) = new_default_tree defaults
in
generate_tree'' generate_tree' (strip (hd defaults'))
(true,exception_tree,new_default_tree) env old_default
end
| _ => Crash.impossible "2:generate_tree:match"
end
| generate_tree _ _ _ _ = Crash.impossible "3:generate_tree:match"
in
fun unparseTree options tree space_out =
let
fun unparseDef (INL(def)) =
(case def of
NONE => "Absent default"
| SOME(ref(BUILT(ref(INL(n,_))))) =>
"Present default of "^"'TREE "^Int.toString n^"'"
| SOME(ref(ERROR _)) =>
"Present default of 'ERROR'"
| _ =>
Crash.impossible "1:unparseDef:unparseTree:match")
| unparseDef (INR(ref(INL(n,_)))) =
"Present default of "^"'TREE "^Int.toString n^"'"
| unparseDef (INR _) =
Crash.impossible "2:unparseDef:unparseTree:match"
fun list_subs [] rest = rest
| list_subs ((m, vi, _)::ll) rest =
list_subs ll
(rest @ [" '", IdentPrint.printValId options vi, "'/#",
Int.toString m])
fun list_fields [] rest = rest
| list_fields ((m, vi)::ll) rest =
list_fields ll
(rest @ [" '", IdentPrint.printLab m, "'=#", Int.toString vi])
fun list_scons [] rest = rest
| list_scons ((m, vi)::ll) rest =
["\n", space_out, "'", IdentPrint.printSCon m,
"' =>\n"] @ unparseTree options vi ("  " ^ space_out) @ list_scons ll rest
fun list_cons [] rest = rest
| list_cons ((lvi, mv, tr)::ll) rest =
["\n", space_out, "'", IdentPrint.printLongValId options lvi,
"' --(#", Int.toString mv, ")\n"]
@ (case tr of
INL(tr) => unparseTree options tr ("  " ^ space_out)
| INR(ref(INL(n,_))) =>
["  " ^ space_out^"'TREE "^Int.toString n^"'"]
| _ => Crash.impossible "list_cons:unparseTree:match") @ list_cons ll rest
fun print_binding binding =
Lists.reducel
(fn (str,binding) =>
str@
(case binding of
INL(ref(SOME(ref(INL(n,tr))))) =>
"\n"::space_out::"   "::"'TREE "::Int.toString n::"'"
::" is bound to \n"::unparseTree options tr ("       "^space_out)
| INL(ref NONE) => nil
| INR(ref(INL(n,tr)),_) =>
"\n"::space_out::"   "::"'TREE "::Int.toString n::"'"
::" is bound to \n"::unparseTree options tr ("       "^space_out)
| _ => Crash.impossible "print_binding:unparseTree:match"))
("\n"::space_out::"BINDING :"::[],binding)
in
(case tree of
LEAF (exp,n,ll) =>
(list_subs ll [space_out, "LEAF ",
AbsynPrint.unparseExp options exp, " {"]) @ [" }"]
| SCON (mv, ll, def, binding, _) =>
([space_out, "SCON #", Int.toString mv]@list_scons ll [])
@ (("\n"^space_out)::
(unparseDef
(INL
(case def of
SOME def => SOME def
| _ => NONE))::
(case binding of
NONE => []
| SOME(bd) =>
print_binding [INL(bd)])))
| CONSTRUCTOR (_, mv, ll, def,binding,_) =>
([space_out, "CONSTRUCTOR #", Int.toString mv]@list_cons ll [])
@ (("\n"^space_out)::(unparseDef def::(case binding of
nil => []
| _ => print_binding binding)))
| RECORD (_, mv, ll, tr) =>
(list_fields ll [space_out, "RECORD #", Int.toString mv])
@ ["\n"] @ (unparseTree options tr ("  " ^ space_out))
| DEFAULT(default,binding) => [space_out,unparseDef(INL(SOME(default)))]@
(case binding of
NONE => []
| SOME(bd) => print_binding [INL(bd)]))
end
fun compile_match pats =
let
val rootvar = next_Matchvar()
fun convert nil _ (redundant_pats,pats) = (tl(rev redundant_pats),rev pats)
| convert (pat::pats) true (redundant_pats,pats') =
convert pats true (((#1(hd redundant_pats)) + 1,TRUE)::redundant_pats,pats')
| convert ((pat,exp,_)::pats) false (redundant_pats,pats') =
let
val n = (#1(hd redundant_pats)) + 1
val redundant =
case fetch_pat NONE pat of
Absyn.WILDpat _ => true
|_ => false
in
convert pats redundant
((n,TRUE)::redundant_pats,
([(NONE,pat)],((exp,n,[]),[]))::pats')
end
val (redundant_pats,pats) = convert pats false ([(0,TRUE)],[])
val the_tree =
(label_count := 0;redundant_patterns := redundant_pats;
inexhaustive := NONE;
match_vars := [];
generate_tree''
(generate_tree [rootvar])
pats
(true,false,
ref(ERROR
(fn nil => ()
| cons =>
inexhaustive :=
(case !inexhaustive of
SOME cons' =>
SOME(cons'@cons)
| NONE =>
SOME cons)))) [[]] false)
in
(if !show_match then
app (ignore o print)
("\n"::unparseTree Options.default_print_options the_tree ""@["\n"])
else
();
(rootvar,the_tree,!redundant_patterns,!inexhaustive))
end
end
end
;
