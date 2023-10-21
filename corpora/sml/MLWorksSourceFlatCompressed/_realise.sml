require "../utils/print";
require "../utils/crash";
require "../utils/lists";
require "../main/info";
require "../basics/identprint";
require "strnames";
require "types";
require "tyenv";
require "strenv";
require "environment";
require "sigma";
require "scheme";
require "valenv";
require "nameset";
require "realise";
functor Realise (
include
sig
structure Print : PRINT
structure Crash : CRASH
structure Lists : LISTS
structure IdentPrint : IDENTPRINT
structure Strnames : STRNAMES
structure Types : TYPES
structure Tyenv : TYENV
structure Strenv : STRENV
structure Env : ENVIRONMENT
structure Sigma : SIGMA
structure Scheme : SCHEME
structure Valenv : VALENV
structure Nameset : NAMESET
structure Info : INFO
sharing Sigma.Options = Nameset.Options = IdentPrint.Options = Scheme.Options
sharing Sigma.BasisTypes.Datatypes = Strnames.Datatypes
= Types.Datatypes = Tyenv.Datatypes = Env.Datatypes = Scheme.Datatypes
= Strenv.Datatypes = Valenv.Datatypes = Nameset.Datatypes
sharing Types.Datatypes.Ident = IdentPrint.Ident
sharing type Sigma.BasisTypes.Nameset = Nameset.Nameset
end where type Types.Datatypes.Stamp = int
) : REALISE =
struct
structure BasisTypes = Sigma.BasisTypes
structure Info = Info
structure Options = Scheme.Options
structure Datatypes = BasisTypes.Datatypes
open Datatypes
fun string_strids strids =
concat (map (fn s => IdentPrint.printStrId s ^ ".")(rev strids))
fun string_strids' [] = ""
| string_strids' (a::b) = string_strids b ^ IdentPrint.printStrId a
fun aswell (x,y) = x andalso y
fun eforall pred map =
NewMap.fold (fn (b,x,y) => aswell (pred(x,y),b)) (true,map)
val dummy_tf = ~3
val generate_moduler_debug = false
fun get_name_and_env (_,STR(m,_,e)) = (m,e)
| get_name_and_env (error,_) = Crash.impossible error
fun sigmatch
(error_info,
options as Options.OPTIONS{print_options,
compiler_options = Options.COMPILEROPTIONS{generate_moduler, ...}, ...})
(location,completion_env,level,sigma as BasisTypes.SIGMA (names,str),str') =
let
val nameset_ref = ref (Nameset.simple_copy names)
fun remove_strname strname =
nameset_ref := Nameset.remove_strname (strname,!nameset_ref)
fun remove_tyname tyname =
nameset_ref := Nameset.remove_tyname (tyname,!nameset_ref)
fun error message_list =
Info.error error_info (Info.RECOVERABLE,location,concat message_list)
fun ve_ran_enriches(level,ve as VE (_,amap),ve' as VE (_,amap'), strids) =
let
fun ran_enriches(valid, scheme) =
let
val (entry, need_coerce) =
case NewMap.tryApply'Eq(amap', valid) of
NONE =>
(case valid of
IdentPrint.Ident.VAR _ =>
(NewMap.tryApply' (amap', valid), true)
| _ => (NONE, false))
| entry => (entry, false)
fun fetch_instance1 (SCHEME(_,(_,SOME(instance,_)))) =
SOME instance
| fetch_instance1 (UNBOUND_SCHEME(_,SOME(instance,_))) =
SOME instance
| fetch_instance1 _ = NONE
fun fetch_instance2 (SCHEME(_,(_,SOME(_,instance')))) =
SOME instance'
| fetch_instance2 (UNBOUND_SCHEME(_,SOME(_,instance'))) =
SOME instance'
| fetch_instance2 _ = NONE
fun make_instance_info (NO_INSTANCE) = ZERO
| make_instance_info (INSTANCE(instances)) = ONE (length instances)
| make_instance_info _ = Crash.impossible "make_instance_info:instantiate:realise"
in
case entry of
SOME scheme' =>
let
val scheme_generalises =
(Scheme.scheme_generalises options (valid,completion_env,level,scheme,scheme')
handle Scheme.EnrichError s => (error [s];false),
need_coerce)
val instance_index =
case fetch_instance1 scheme' of
SOME (ref (INSTANCE ((ref(_,_,instances))::_))) =>
(case instances of
INSTANCE (instances) => SOME (length instances)
| _ => NONE)
| _ => NONE
val _ =
case (fetch_instance2 scheme, fetch_instance2 scheme') of
(SOME instance, SOME instance') =>
instance :=
(case !instance' of
NONE =>
(case fetch_instance1 scheme' of
SOME (ref (INSTANCE ((ref (_,_,instances))::_))) =>
SOME (ref (SIGNATURE_INSTANCE (make_instance_info instances)))
| SOME (ref(NO_INSTANCE)) =>
SOME (ref (NO_INSTANCE))
| _ =>
Crash.impossible "1:instance:ran_enriches:realise")
| SOME (ref (instance' as SIGNATURE_INSTANCE _)) =>
SOME (ref instance')
| SOME (ref NO_INSTANCE) =>
SOME (ref NO_INSTANCE)
| _ =>
Crash.impossible "2:instance:ran_enriches:realise")
| _ => ()
in
(scheme_generalises, instance_index)
end
| NONE =>
(error [case valid of
IdentPrint.Ident.EXCON _ => "Missing exception "
| _ => "Missing value ",
IdentPrint.printValId print_options valid,
" in structure ",
string_strids' strids];
((false, false), NONE))
end
fun accumulator ((res,coerce,debugger_str), valid, scheme) =
let val ((res', coerce'), instance) = ran_enriches (valid, scheme)
in (res andalso res',
coerce orelse coerce',
NewMap.define(debugger_str,valid,instance))
end
in
NewMap.fold accumulator ((true, false, NewMap.empty (Ident.valid_lt, Ident.valid_eq)), amap)
end
exception TyfunError
exception TypeDiffer
fun tystr_enriches (tycon,TYSTR (tyfun,conenv),TYSTR (tyfun',conenv')) =
if Types.tyfun_eq (tyfun,tyfun')
then
if Valenv.empty_valenvp conenv orelse Valenv.valenv_eq(conenv,conenv')
then true
else
(if Valenv.dom_valenv_eq(conenv,conenv') then
()
else
let
fun string_conenv (VE (_,amap)) =
let
fun print_spaces (res, n) =
if n = 0 then concat(" " :: res)
else print_spaces (" " :: res, n-1)
in
NewMap.string (IdentPrint.printValId print_options) (fn _ => "")
{start = "", domSep = "", itemSep = ", ", finish = ""}
(NewMap.map (fn (id,sch)=>sch) amap)
end
in
error ["Type ", IdentPrint.printTyCon tycon, " has different constructors in structure and signature:\n",
"  Structure: ", string_conenv conenv', "\n",
"  Signature: ", string_conenv conenv]
end;
false)
else raise TyfunError
fun te_ran_enriches(TE amap,TE amap', strids) =
let
fun ran_enriches(tycon,tystr) =
(let
val tystr' = NewMap.apply'(amap', tycon)
in
(tystr_enriches (tycon,tystr,tystr'))
handle TypeDiffer => false
end
handle NewMap.Undefined => false
| TyfunError => false )
in
eforall ran_enriches amap
end
fun env_enriches (level,
ENV (se as SE se_map,te,ve),
ENV (se' as SE se_map',te',ve'),
DSTR(debugger_str1,debugger_str2,_),
strids) =
let
val (res1, coerce1, debugger_str1) =
se_ran_enriches (level,se,se',debugger_str1, strids)
val res2 = te_ran_enriches (te,te', strids)
val (res3, coerce3, debugger_str) =
ve_ran_enriches(level,ve,ve', strids)
in
(res1 andalso res2 andalso res3, coerce1 orelse coerce3,
DSTR(debugger_str1,debugger_str2,debugger_str))
end
| env_enriches _ = Crash.impossible "EMPTY_DSTR:env_enriches:realise"
and str_enriches (level,str,str',debugger_str, strids) =
let
val (strname,env) = get_name_and_env ("str_enriches1",str)
val (strname',env') = get_name_and_env ("str_enriches2",str')
in
if Strnames.strname_eq (strname,strname') then
env_enriches (level,env,env',debugger_str, strids)
else
(false, false, debugger_str)
end
and se_ran_enriches (level,SE amap,SE amap',debugger_str, strids) =
let
fun ran_enriches(strid, str) =
case (NewMap.tryApply'(amap', strid),
NewMap.tryApply'(debugger_str, strid)) of
(SOME str',SOME debugger_str) =>
str_enriches (level,str,str',debugger_str, strid :: strids)
| (_,_) => (false, false, Datatypes.EMPTY_DSTR)
fun accumulator ((res,coerce,debugger_str), valid, scheme) =
let val (res', coerce',debugger_str') = ran_enriches (valid, scheme)
in (res andalso res',
coerce orelse coerce',
NewMap.define(debugger_str,valid,debugger_str'))
end
in
NewMap.fold accumulator ((true, false, NewMap.empty (Ident.strid_lt, Ident.strid_eq)), amap)
end
fun make_eta_tyfun(tyfun as TYFUN(CONSTYPE(types, tyname), i)) =
if Types.tyname_arity tyname = i andalso Types.check_debruijns(types, 0)
then ETA_TYFUN (tyname)
else tyfun
| make_eta_tyfun tyfun = tyfun
fun tystr_realise (TYSTR (tyfun,ve as VE (_,amap)),
TYSTR (tyfun',ve' as VE (_,amap')),
tycon,
strids) =
let
fun fetch_nulltyfun(tyf as ref(NULL_TYFUN _)) = tyf
| fetch_nulltyfun(ref(ETA_TYFUN(METATYNAME{1=tyfun, ...}))) =
fetch_nulltyfun tyfun
| fetch_nulltyfun _ = Crash.impossible "fetch_nulltyfun:realise"
val tyfun = make_eta_tyfun tyfun
val tyfun' = make_eta_tyfun tyfun'
in
(case Types.tyfun_eq (tyfun, tyfun') of
true => (true,
if Types.null_tyfunp tyfun' then ~2
else
if generate_moduler then Types.update_tyfun_instantiations tyfun'
else 0)
| false =>
(if Types.null_tyfunp tyfun then
let
val tyname = Types.meta_tyname tyfun
val (t, eq, name) = case tyname of
METATYNAME (t,name,_,ref eq, _, _) => (t, eq, name)
| _ => Crash.impossible"bad Types.meta_tyname tyfun"
val (ntf,id) =
let
val ntf =
case fetch_nulltyfun(t) of
tf as ref(NULL_TYFUN(id,tyf)) =>
((if generate_moduler then ()
else () ;
if generate_moduler then Types.update_tyfun_instantiations tyfun'
else 0),id)
| _ => (dummy_tf, 0)
in
ntf
end
in
if Nameset.member_of_tynames (tyname,!nameset_ref) then
if Types.arity tyfun = Types.arity tyfun' then
if (Types.equalityp tyfun' orelse not eq) then
(t := tyfun';
if Types.null_tyfunp tyfun' andalso
Nameset.member_of_tynames
(Types.meta_tyname tyfun',!nameset_ref)
then remove_tyname tyname
else ();
(true,ntf))
else
(error ["Type ",
string_strids strids,
IdentPrint.printTyCon tycon,
" in structure does not admit equality"];
t := tyfun';
(false,ntf))
else
(error ["Number of parameters of type constructor ",
string_strids strids,
IdentPrint.printTyCon tycon,
" differ in signature and structure"];
t := tyfun';
(false,ntf))
else
(error ["Type sharing violation for ",
string_strids strids,
IdentPrint.printTyCon tycon];
(false,ntf))
end
else
(true,dummy_tf)))
end
and se_realise (SE amap,se',strids) =
let
fun strname_map(strid,str) =
case Strenv.lookup (strid,se') of
SOME str' => str_realise(str,str',strid::strids)
| _ =>
(error ["Missing substructure ",
IdentPrint.printStrId strid,
" in structure ",
string_strids' strids];
(false, Datatypes.EMPTY_DSTR))
val strname_map = NewMap.map strname_map amap
val debugger_str = NewMap.map (fn (_,(_,str))=>str) strname_map
in
(NewMap.fold (fn (b',_,(b,_)) => b' andalso b) (true,strname_map), debugger_str)
end
and te_realise (TE amap,te',strids) =
let
fun tyname_map(tycon, sig_tystr) =
let
val str_tystr = Tyenv.lookup (te', tycon)
in
tystr_realise (sig_tystr,str_tystr, tycon,strids)
end
handle Tyenv.LookupTyCon _ =>
(error ["Missing type constructor ",
IdentPrint.printTyCon tycon,
" in structure ",
string_strids' strids];
(false,dummy_tf))
val tyname_map = NewMap.map tyname_map amap
val debugger_str = NewMap.map (fn (_,(_,n))=>n) tyname_map
in
(NewMap.fold (fn (b',_,(b,_)) => b' andalso b) (true,tyname_map), debugger_str)
end
and env_realise (ENV (se,te,_),ENV (se',te',_),strids) =
let
val (se_realise, debugger_str') = se_realise (se,se',strids)
val (te_realise, debugger_str) = te_realise (te,te',strids)
in
(se_realise andalso te_realise,
DSTR(debugger_str',debugger_str,
NewMap.empty (Ident.valid_lt, Ident.valid_eq)))
end
and str_realise (str1,str2,strids) =
let
val (name,env) = get_name_and_env ("str_realise1",str1)
val (name',env') = get_name_and_env ("str_realise2",str2)
val name = Strnames.strip name
in
case name of
METASTRNAME r =>
if
Strnames.uninstantiated name' andalso
Strnames.metastrname_eq (name,name')
then
env_realise (env,env',strids)
else
let
val result =
let
val (env_realise, debugger_str) = env_realise (env,env',strids)
in
if env_realise then
if Nameset.member_of_strnames (name,!nameset_ref)
then
(true, debugger_str)
else
(error["Structure sharing violation for ",
(case strids of
[] => "impossible type error 13: top level structure"
| _ => string_strids' strids)];
(false, debugger_str))
else
(false, debugger_str)
end
in
if Nameset.member_of_strnames (name',!nameset_ref)
then
(r := name';
result)
else
(r := name';
remove_strname name;
result)
end
| _ =>
if Strnames.strname_eq (name,name') then
env_realise (env,env',strids)
else
(error["Structure sharing violation for ",
(case strids of
[] => "impossible type error 14: top level structure"
| _ => string_strids' strids)];
(false,Datatypes.EMPTY_DSTR))
end
fun tystr_check (TYSTR (tyfun,ve as VE (_,amap)),
TYSTR (tyfun',ve' as VE (_,amap')),
tycon,
strids) =
let
val tyfun = make_eta_tyfun tyfun
val tyfun' = make_eta_tyfun tyfun'
in
if Types.tyfun_eq (tyfun, tyfun')
then true
else
(error ["Type sharing violation for ",
string_strids strids,
IdentPrint.printTyCon tycon
];
false)
end
and se_check (SE amap,se',strids) =
let
fun check (b,strid,str) =
case Strenv.lookup (strid,se') of
SOME str' => str_check(str,str',strid::strids) andalso b
| _ => b
in
NewMap.fold check (true,amap)
end
and te_check (TE amap,te',strids) =
let
fun check_tyname (tycon, sig_tystr) =
let
val str_tystr = Tyenv.lookup (te', tycon)
in
tystr_check (sig_tystr,str_tystr, tycon,strids)
end
handle Tyenv.LookupTyCon _ => true
val check_result = NewMap.map check_tyname amap
in
NewMap.fold (fn (b',_,b) => b' andalso b) (true,check_result)
end
and env_check (ENV (se,te,_),ENV (se',te',_),strids) =
let
val se_check = se_check (se,se',strids)
val te_check = te_check (te,te',strids)
in
se_check andalso te_check
end
and str_check (str1,str2,strids) =
let
val (name,env) = get_name_and_env ("str_check1",str1)
val (name',env') = get_name_and_env ("str_check2",str2)
in
env_check (env,env',strids)
end
val (res1, debugger_str) = str_realise (str,str',[])
val res2 = str_check (str,str',[])
val (res3, coerce, debugger_str) =
str_enriches (level,str,str',debugger_str, [])
in
(res1 andalso res2 andalso res3, coerce, debugger_str)
end
end
;
