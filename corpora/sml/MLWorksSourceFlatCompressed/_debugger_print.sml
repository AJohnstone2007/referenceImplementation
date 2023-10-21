require "../basis/__int";
require "../basis/__text_io";
require "../utils/crash";
require "../utils/lists";
require "../main/options";
require "../typechecker/types";
require "../rts/gen/tags";
require "debugger_utilities";
require "runtime_env";
require "debugger_print";
functor DebuggerPrint (
structure Lists : LISTS
structure Crash : CRASH
structure Options : OPTIONS
structure Types : TYPES
structure Tags : TAGS
structure RuntimeEnv : RUNTIMEENV
structure DebuggerUtilities : DEBUGGER_UTILITIES
sharing type RuntimeEnv.Type = Types.Datatypes.Type
sharing type Options.options = Types.Options.options
sharing type Types.Options.print_options = Options.print_options
sharing type Types.Datatypes.Instance = RuntimeEnv.Instance
sharing type Types.Datatypes.Tyfun = RuntimeEnv.Tyfun
) : DEBUGGER_PRINT =
struct
structure Options = Options
structure Datatypes = Types.Datatypes
structure RuntimeEnv = RuntimeEnv
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Value = MLWorks.Internal.Value
structure Frame = Value.Frame
val frame_double = MLWorks.Internal.Runtime.environment "debugger frame double"
val do_debug = false
val do_debug_print_env = false
val do_spill_debug = false
val debug_out = TextIO.stdErr
val my_debug = false
fun debug s = if my_debug then TextIO.output (debug_out,"  # " ^ s ^ "\n") else ()
local
exception Finished of string
val initial_value = Value.cast 0
open RuntimeEnv
type Substitution = ((int * Type * Instance) ref * Type) list
fun is_builtin_typename typename =
(Lists.member(typename,
["bool","int","real","string","list","ref",
"exn","ml_value","array","vector","bytearray","dynamic","type_rep"]))
fun print_tag (CONSTRUCTOR (tag)) = ": " ^ tag
| print_tag (INT(tag)) = "int: " ^ tag
| print_tag (REAL(tag)) = "real: " ^ tag
| print_tag (STRING(tag)) = "string: " ^ tag
| print_tag (CHAR(tag)) = "char: " ^ tag
| print_tag (WORD(tag)) = "word: " ^ tag
| print_tag DYNAMIC = "<dynamic>"
| print_tag DEFAULT = "<default>"
fun debug_print env =
let
fun print_spill_type RuntimeEnv.GC = "(GC)"
| print_spill_type RuntimeEnv.NONGC = "(NONGC)"
| print_spill_type RuntimeEnv.FP = "(FP)"
fun print_offset NONE = ""
| print_offset (SOME(ref(OFFSET2(spill_ty, offset)))) =
":OFFSET2" ^ print_spill_type spill_ty ^
Int.toString offset ^ "\n" ^ " = "
| print_offset (SOME(ref(OFFSET1 offset))) =
":OFFSET1" ^ Int.toString offset ^ "\n" ^ " = "
fun debug_print (LET(envs,env)) indent =
(TextIO.output(debug_out,"\n"^indent^"LET ");
ignore(map (fn (RuntimeEnv.VARINFO (name,(ref ty,_),offset),env) =>
(TextIO.output(debug_out,"\n" ^ indent ^ "  val " ^ name ^ ":" ^
Types.extra_debug_print_type ty ^ print_offset offset);
debug_print env (indent^" "))
| (_,env) => debug_print env (indent^" ")) envs);
TextIO.output(debug_out,"\n"^indent^"IN ");
debug_print env (indent^" ");
TextIO.output(debug_out,"\n"^indent^"END "))
| debug_print (FN(name,env,offset,_)) indent =
(TextIO.output(debug_out,"\n"^indent^"FN "^name^" "
^print_offset (SOME(offset)));
debug_print env (indent^" "))
| debug_print (APP(env1,env2,opt)) indent =
(TextIO.output(debug_out,"\n"^indent^"APP ");
ignore(case opt of
NONE => ""
| SOME(n) => ":"^Int.toString n);
TextIO.output(debug_out,"\n"^indent^"(");
debug_print env1 (indent^" ");
TextIO.output(debug_out,"\n"^indent^")");
TextIO.output(debug_out,"\n"^indent^"(");
debug_print env2 (indent^" ");
TextIO.output(debug_out,"\n"^indent^")"))
| debug_print (RAISE(env)) indent =
(TextIO.output(debug_out,"\n"^indent^"RAISE ");
debug_print env (indent^" "))
| debug_print (SELECT(n,env)) indent =
(TextIO.output(debug_out,"\n"^indent^"SELECT:"^Int.toString n);
debug_print env (indent^" "))
| debug_print (STRUCT(envs)) indent =
(TextIO.output(debug_out,"\n"^indent^"STRUCT ");
ignore(map (fn env => debug_print env (indent^" ")) envs);
())
| debug_print (LIST(envs)) indent =
(TextIO.output(debug_out,"\n"^indent^"LIST ");
ignore(map (fn env => debug_print env (indent^" ")) envs);
())
| debug_print (SWITCH(env,offset,apps,envs)) indent =
(TextIO.output(debug_out,"\n"^indent^"SWITCH "
^print_offset (SOME(offset))^":"^
Int.toString apps);
debug_print env (indent^" ");
TextIO.output(debug_out,"\n"^indent^"IN ");
ignore(map (fn (tag,env) =>
(TextIO.output(debug_out,"\n"^indent^"TAG:"^print_tag tag^" ");
debug_print env (indent^" "))) envs);
TextIO.output(debug_out,"\n"^indent^"END "))
| debug_print (HANDLE(env1,offset,apps,apps',env2)) indent =
(TextIO.output(debug_out,"\n"^indent^"HANDLE "
^print_offset (SOME(offset))^":"^
Int.toString apps^":"^
Int.toString apps');
debug_print env1 (indent^" ");
TextIO.output(debug_out,"\n"^indent^"IN ");
debug_print env2 (indent^" ");
TextIO.output(debug_out,"\n"^indent^"END "))
| debug_print (EMPTY) indent = TextIO.output(debug_out,"\n"^indent^"EMPTY")
| debug_print (BUILTIN) indent = TextIO.output(debug_out,"\n"^indent^"BUILTIN")
in
debug_print env ""
end
fun subst_type(substs,ty) =
let
fun substitute_type(ty as Datatypes.TYVAR(tyv as
ref(_,Datatypes.NULLTYPE,_),_)) =
(Lists.assoc(tyv,substs)
handle Lists.Assoc => ty)
| substitute_type(Datatypes.TYVAR(ref (_,ty,_),_)) =
substitute_type(ty)
| substitute_type(ty as Datatypes.METATYVAR(tyv as
ref(_,Datatypes.NULLTYPE,_),
_,_)) =
(Lists.assoc(tyv,substs)
handle Lists.Assoc => ty)
| substitute_type(Datatypes.METATYVAR (ref (_,ty,_),_,_)) =
substitute_type(ty)
| substitute_type(ty as Datatypes.META_OVERLOADED
{1=ref Datatypes.NULLTYPE,...}) = ty
| substitute_type(Datatypes.META_OVERLOADED {1=ref ty,...}) =
substitute_type(ty)
| substitute_type(Datatypes.METARECTYPE
(ref (_,true,
ty as Datatypes.METARECTYPE _,_,_))) =
substitute_type(ty)
| substitute_type(Datatypes.METARECTYPE (ref (_,_,ty,_,_))) =
substitute_type(ty)
| substitute_type((Datatypes.RECTYPE amap)) =
Datatypes.RECTYPE(NewMap.map
substitute_type_map
amap)
| substitute_type(Datatypes.FUNTYPE(arg,res)) =
Datatypes.FUNTYPE(substitute_type(arg),substitute_type(res))
| substitute_type(Datatypes.CONSTYPE (tylist,tyname)) =
Datatypes.CONSTYPE(map substitute_type tylist,tyname)
| substitute_type(ty as Datatypes.DEBRUIJN(_,_,_,SOME(tyv))) =
(Lists.assoc(tyv,substs)
handle Lists.Assoc => ty)
| substitute_type ty = ty
and substitute_type_map(_, ty) = substitute_type ty
in
substitute_type(ty)
end
fun instantiate_instance (instance,offset,tyvar) =
(case instance of
Datatypes.NO_INSTANCE => Crash.impossible "1:instantiate_instance:debugger_print"
| Datatypes.SIGNATURE_INSTANCE _ => Crash.impossible "2:instantiate_instance:debugger_print"
| Datatypes.INSTANCE instance_list =>
(case offset of
Datatypes.ZERO =>
(debug "No instance"; Datatypes.METATYVAR (tyvar,false,false))
| Datatypes.ONE inst =>
(debug "One instance"; Datatypes.METATYVAR (Lists.nth (inst-1, rev instance_list), false, false))
| Datatypes.TWO (inst,inst') =>
(debug "Double instance";
case Lists.nth (inst-1, rev instance_list) of
tyvar as ref (_,ty,instances) =>
(case ty of
Datatypes.NULLTYPE => instantiate_instance (instances,Datatypes.ONE inst',tyvar)
| _ =>
let
val tyvars = Types.all_tyvars(ty)
val tys = map (fn tyvar as ref (_,_,instances) =>
instantiate_instance (instances,Datatypes.ONE inst',tyvar)) tyvars
in
Lists.reducel
(fn (ty,tyvar) =>
subst_type ([tyvar],ty))
(ty,Lists.zip (tyvars,tys))
end))))
fun fetch_offset (ref (OFFSET2 spill_info)) = spill_info
| fetch_offset _ = Crash.impossible "fetch_offset:debugger_print"
in
fun print_env ((present_frame,env,present_ty),print_value,options,windowing,frames) =
let
val result : string ref = ref ""
fun output' str = result := (!result) ^ str
local
val windowing_info:
(string * (Type * Value.ml_value * string)) list ref = ref []
in
val (accumulate_windowing_info,return_windowing_info) =
if windowing then
(fn info =>
windowing_info := info::(!windowing_info),
fn () => rev(!windowing_info))
else
(fn _ => (),
fn _ => nil)
end
val Options.OPTIONS{print_options,...} = options
val _ =
if not (Frame.frame_allocations present_frame) then
raise Finished("No stack allocations \n")
else ()
val apps = ref 0
val (fnstr,env,first_spill,bound_tyvars) =
case env of
FN (name,env,ref(OFFSET2(_, offset)),bound_tyvars) =>
("fn " ^ name,env,Value.cast (Frame.frame_offset(present_frame,offset)) : int,bound_tyvars)
| FN (name,env,ref (OFFSET1 1),_) =>
raise Finished("Nothing useful to print : No Code Transfer \n")
| EMPTY => raise Finished("")
| _ => Crash.impossible "print_env:debugger_print"
val _ =
if do_debug_print_env then
(TextIO.output(debug_out,"Debug printing env ...\n");
debug_print env;
TextIO.output(debug_out,"\n first spill = "^
Int.toString first_spill^"\n"))
else ()
val arbitraryUpperBound = 8192
val _ =
if first_spill = 0 orelse first_spill > arbitraryUpperBound
then raise Finished("Nothing useful to print : No calls made \n")
else ()
datatype type_function = TYFUN of (int * type_function) list
exception TyfunInstantiation of string
exception ValenvInstantiation of string
type tyfun_substitution = (Datatypes.Tyfun ref * Datatypes.Tyfun ref) list
val tfs : tyfun_substitution ref = ref []
val ves : (Datatypes.Valenv ref * Datatypes.Valenv ref) list ref = ref []
val dummy_false = ref(false)
val dummy_ve = ref(Datatypes.empty_valenv)
fun type_spills (tyfun,Datatypes.CONSTYPE(tys,tyn)) =
let
val (tyn, tyfun) = tyname_spills (tyfun,tyn)
val (tys, tyfun) =
Lists.reducel
(fn ((tys,tyfun),ty) =>
let
val (ty, tyfun) = type_spills(tyfun,ty)
in
(ty::tys, tyfun)
end) ((nil, tyfun), tys)
in
(Datatypes.CONSTYPE(tys, tyn), tyfun)
end
| type_spills (tyfun,Datatypes.FUNTYPE(ty1,ty2)) =
let
val (ty1, tyfun) = type_spills (tyfun,ty1)
val (ty2, tyfun) = type_spills (tyfun,ty2)
in
(Datatypes.FUNTYPE(ty1,ty2), tyfun)
end
| type_spills (tyfun,Datatypes.RECTYPE map) =
let
val (map, tyfun) =
NewMap.fold
(fn ((map, tyfun), lab, ty) =>
let
val (ty, tyfun) = type_spills (tyfun,ty)
in
(NewMap.define'(map, (lab,ty)), tyfun)
end)
((NewMap.empty' Ident.lab_lt, tyfun), map)
in
(Datatypes.RECTYPE(map), tyfun)
end
| type_spills (tyfun,Datatypes.METATYVAR(ref(n,ty,i),b1,b2)) =
let
val (ty, tyfun) = type_spills (tyfun,ty)
in
(Datatypes.METATYVAR(ref(n,ty,i),b1,b2), tyfun)
end
| type_spills
(tyfun,Datatypes.META_OVERLOADED(ref(ty),tv,valid,loc)) =
let
val (ty, tyfun) = type_spills(tyfun,ty)
in
(Datatypes.META_OVERLOADED(ref(ty),tv,valid,loc), tyfun)
end
| type_spills (tyfun,Datatypes.TYVAR(ref(n,ty,i),id)) =
let
val (ty, tyfun) = type_spills(tyfun,ty)
in
(Datatypes.TYVAR(ref(n,ty,i),id), tyfun)
end
| type_spills (tyfun,Datatypes.METARECTYPE(ref(n,b1,ty,b2,b3))) =
let
val (ty, tyfun) = type_spills(tyfun,ty)
in
(Datatypes.METARECTYPE(ref(n,b1,ty,b2,b3)), tyfun)
end
| type_spills (tyfun,ty) = (ty, tyfun)
and typescheme_spills (tyfun,Datatypes.SCHEME(n,(ty,i))) =
let
val (ty, tyfun) = type_spills (tyfun,ty)
in
(Datatypes.SCHEME(n,(ty,i)), tyfun)
end
| typescheme_spills (tyfun,Datatypes.UNBOUND_SCHEME(ty,i)) =
let
val (ty, tyfun) = type_spills (tyfun,ty)
in
(Datatypes.UNBOUND_SCHEME(ty,i), tyfun)
end
| typescheme_spills (tyfun,sch) = (sch, tyfun)
and tyname_spills (tyfun,tyname) =
let
fun tyname_spills'
(m as Datatypes.METATYNAME(tf as ref(Datatypes.NULL_TYFUN(_)),name,n,b,
ve' as ref(Datatypes.VE(n',ve)),abs)) =
if NewMap.is_empty ve then
(Datatypes.METATYNAME(Lists.assoc(tf,!tfs),name,n,b,ve',abs),tyfun)
handle Lists.Assoc =>
let
val ((spill,tyfun),tyfun') =
(case tyfun of
TYFUN (spill::spills) => (spill, TYFUN spills)
| _ => raise TyfunInstantiation name)
val _ = debug ("spill for: " ^ name ^ " = " ^ Int.toString spill)
in
(case spill of
~1 => Crash.impossible "~1:tyname_spills':debugger_print"
| ~2 => Crash.impossible "~2:tyname_spills':debugger_print"
| ~3 => Crash.impossible "~3:tyname_spills':debugger_print"
| ~4 => Crash.impossible "~4:tyname_spills':debugger_print"
| ~5 =>
(TextIO.output(debug_out,"WARNING: tyfun instantiation incomplete because of a previous compilation\n");m)
| ~6 => Crash.impossible "~6:tyname_spills':debugger_print"
| _ =>
let
val tf' = ref (Types.fetch_tyfun_instantiation spill)
val _ =
if do_spill_debug then
debug ("tf = "^Types.string_tyfun (!tf') ^
"\nspill = " ^ Int.toString spill)
else ()
val old_tfs = !tfs
val old_ves = !ves
val _ = (tfs := nil;
ves := nil)
val tf'' = case tyname_spills
(tyfun,Datatypes.METATYNAME(tf',"",0,dummy_false,
dummy_ve,dummy_false)) of
(Datatypes.METATYNAME(ref(tf''),_,_,_,_,_),_) => tf''
| _ => Crash.impossible"tyname_spills':debugger_print"
val _ =
if do_debug then
TextIO.output(debug_out,"\n instantiation completed for spill "
^Int.toString spill^"\n")
else ()
in
(tfs := (tf,tf')::old_tfs;
ves := old_ves;
tf' := tf'';
Datatypes.METATYNAME(tf',name,n,b,ve',abs))
end,
tyfun')
end
else
((Datatypes.METATYNAME(tf,name,n,b,Lists.assoc(ve',!ves),abs), tyfun)
handle Lists.Assoc =>
let
val ve'' = ref(Datatypes.VE(n',NewMap.empty (Ident.valid_lt, Ident.valid_eq)))
val (map, tyfun) =
(ves := (ve',ve'')::(!ves);
NewMap.fold (fn ((map,tyfun),valid,sch) =>
let
val (sch, tyfun) = typescheme_spills (tyfun,sch)
in
(NewMap.define(map,valid,sch), tyfun)
end)
((NewMap.empty (Ident.valid_lt, Ident.valid_eq), tyfun), ve))
in
(ve'' := Datatypes.VE(n',map);
(Datatypes.METATYNAME(tf,name,n,b,ve'',abs), tyfun))
end)
| tyname_spills' (Datatypes.METATYNAME(ref(Datatypes.ETA_TYFUN(tyn)),name,n,b,
ve' as ref(Datatypes.VE(n',ve)),abs)) =
let
val (tyn, tyfun) = tyname_spills' tyn
val tf = ref(Datatypes.ETA_TYFUN(tyn))
in
((Datatypes.METATYNAME(tf,name,n,b,Lists.assoc(ve',!ves),abs), tyfun)
handle Lists.Assoc =>
let
val ve'' = ref(Datatypes.VE(n',NewMap.empty (Ident.valid_lt, Ident.valid_eq)))
val (map, tyfun) =
(ves := (ve',ve'')::(!ves);
NewMap.fold (fn ((map,tyfun),valid,sch) =>
let
val (sch, tyfun) = typescheme_spills (tyfun,sch)
in
(NewMap.define(map,valid,sch), tyfun)
end)
((NewMap.empty (Ident.valid_lt, Ident.valid_eq), tyfun), ve))
in
(ve'' := Datatypes.VE(n',map);
(Datatypes.METATYNAME(tf,name,n,b,ve'',abs), tyfun))
end)
end
| tyname_spills' (Datatypes.METATYNAME(ref(Datatypes.TYFUN(ty,n)),name,n',b,
ve' as ref(Datatypes.VE(n'',ve)),abs)) =
let
val (ty, tyfun) = type_spills (tyfun,ty)
val tf = ref(Datatypes.TYFUN(ty,n))
in
((Datatypes.METATYNAME(tf,name,n,b,Lists.assoc(ve',!ves),abs), tyfun)
handle Lists.Assoc =>
let
val ve'' = ref(Datatypes.VE(n'',NewMap.empty (Ident.valid_lt, Ident.valid_eq)))
val (map, tyfun) =
(ves := (ve',ve'')::(!ves);
NewMap.fold (fn ((map,tyfun),valid,sch) =>
let
val (sch, tyfun) = typescheme_spills (tyfun,sch)
in
(NewMap.define(map,valid,sch), tyfun)
end)
((NewMap.empty (Ident.valid_lt, Ident.valid_eq), tyfun), ve))
in
(ve'' := Datatypes.VE(n'',map);
(Datatypes.METATYNAME(tf,name,n',b,ve'',abs), tyfun))
end)
end
| tyname_spills'
(Datatypes.TYNAME(id,namestring,n,b,
ve1 as ref(Datatypes.VE(n1,ve2)),
s',abs,
ve3 as ref(Datatypes.VE(n2,ve4)),
lev)) =
let
val _ =
if Datatypes.NewMap.is_empty ve4 andalso
not (is_builtin_typename namestring) then
raise ValenvInstantiation namestring
else ()
val (ve',ve,n') = (ve3,ve4,n2)
in
((Datatypes.TYNAME(id,namestring,n,b,
Lists.assoc(ve',!ves),s',abs,ve3,lev),
tyfun)
handle Lists.Assoc =>
let
val ve'' = ref(Datatypes.VE(n',NewMap.empty
(Ident.valid_lt,
Ident.valid_eq)))
val (map, tyfun) =
(ves := (ve',ve'')::(!ves);
NewMap.fold
(fn ((map,tyfun),valid,sch) =>
let
val (sch, tyfun) = typescheme_spills (tyfun,sch)
in
(NewMap.define(map,valid,sch), tyfun)
end)
((NewMap.empty (Ident.valid_lt, Ident.valid_eq),
tyfun), ve))
in
(ve'' := Datatypes.VE(n',map);
(Datatypes.TYNAME(id,namestring,n,b,ve'',s',abs,
ve',lev), tyfun))
end)
end
in
tyname_spills' tyname
end
val substs_ref : Substitution ref = ref []
fun print_env_type(LET _) = "LET\n"
| print_env_type(FN(str, _, _, info)) =
(case info of
USER_FUNCTION => "FN(USER_FUNCTION)"
| LOCAL_FUNCTION => "FN(LOCAL_FUNCTION)"
| INTERNAL_FUNCTION => "FN(INTERNAL_FUNCTION)"
| FUNINFO _ => "FN(FUNINFO)") ^ " for " ^ str
| print_env_type(APP _) = "APP"
| print_env_type(RAISE _) = "RAISE"
| print_env_type(SELECT _) = "SELECT"
| print_env_type(STRUCT _) = "STRUCT"
| print_env_type(LIST _) = "LIST"
| print_env_type(SWITCH _) = "SWITCH"
| print_env_type(HANDLE _) = "HANDLE"
| print_env_type(EMPTY) = "EMPTY"
| print_env_type(BUILTIN) = "BUILTIN"
fun instantiate_type ([], ty) = (debug "Out of frames"; subst_type (!substs_ref,ty))
| instantiate_type ((frame,env,frame_ty)::frames, ty) =
let
val ty = subst_type (!substs_ref,ty)
val tyvars = Types.all_tyvars ty
in
case tyvars of
[] => (debug "instantiate_type done"; ty)
| _ =>
case env of
FN (_,_,_,RuntimeEnv.FUNINFO (bound_tyvars,offset)) =>
let
val _ = debug"instantiate_type: FN"
val bound_tyvars =
Lists.filterp
(fn tyv => Lists.member(tyv,tyvars))
bound_tyvars
val _ = if Lists.length bound_tyvars > 0 then
print("Found " ^ Int.toString (Lists.length bound_tyvars) ^ "\n")
else ()
val offset =
Value.cast (Frame.frame_offset (frame,#2(fetch_offset offset)))
val _ =
substs_ref :=
Lists.reducel
(fn (substs,tyvar as ref(_,_,instance)) =>
(tyvar,instantiate_instance (instance,offset,tyvar)) :: substs)
(!substs_ref,bound_tyvars)
in
instantiate_type (frames, ty)
end
| env =>
let
val _ = debug"instantiate_type: not FN"
val _ = debug(print_env_type env)
in
instantiate_type (frames, ty)
end
end
val seen_tyvars = ref(Types.no_tyvars)
fun print_env((LET (envs, env)), indent, ns, spill) =
(case env of
LET(envs',env) => print_env((LET (envs@envs',env)), indent, ns, spill)
| _ =>
(ignore(map
(fn (RuntimeEnv.VARINFO (name,(ref ty,ref (RuntimeEnv.RUNTIMEINFO(_,spills))),offset),env) =>
(let
val ty =
if Types.isFunType ty then ty
else
let
val tyfun =
TYFUN
(map (fn (tf as ref(Datatypes.NULL_TYFUN(_)),spill) =>
(if do_spill_debug then
(TextIO.output(debug_out,name^ ":spill:" ^
Int.toString
(#1(Value.cast (Frame.frame_offset
(present_frame,#2(fetch_offset spill)))
:int * type_function)) ^ "\n")
)
else ();
Value.cast (Frame.frame_offset
(present_frame,#2(fetch_offset spill)))
)
| _ => Crash.impossible "spills:print_env:debugger_print") spills)
in
#1(type_spills(tyfun,ty))
handle TyfunInstantiation(tyname) =>
(if do_debug
then TextIO.output(debug_out,"WARNING: Tyfun Instantiation failed in "^tyname^" in "^name^"\n")
else ();
ty)
| ValenvInstantiation(tyname) =>
(if do_debug
then TextIO.output(debug_out,"WARNING: Valenv Instantiation failed in "^tyname^
" in "^name^"\n")
else ();
ty)
end
val (ty,inst_ty) =
case Types.all_tyvars ty of
nil =>
(ty,NONE)
| tyvars =>
let
val _ =
case frames of
[] => debug"Instantiating with no frames"
| _ => debug"Instantiating with some frames"
in
(instantiate_type (frames, ty),
SOME ty)
end
val _ = print_env(env, (indent ^ " "), ns, spill)
val (value,value_string) =
case offset of
NONE => (initial_value,"unavailable")
| SOME (ref (OFFSET2(spill_ty, offset))) =>
let
val value =
if Types.real_typep ty then
case spill_ty of
RuntimeEnv.GC =>
Frame.frame_offset (present_frame,offset)
| RuntimeEnv.FP =>
Frame.frame_double(present_frame, offset)
| RuntimeEnv.NONGC =>
Crash.impossible"print_env:NONGC"
else
Frame.frame_offset (present_frame,offset)
val _ =
if do_debug then
if Types.isFunType ty
then ()
else
let
val (type_str, tyvars) =
Types.print_type_with_seen_tyvars
(options, ty, !seen_tyvars)
val _ = seen_tyvars := tyvars
in
TextIO.output
(debug_out," type: " ^ type_str ^ "\n")
end
else ();
in
(value,print_value (ty,value))
end
| _ => Crash.impossible "LET:print_env:debugger_print"
val _ =
output'("\n" ^ indent ^ "val " ^ name ^ " : " ^
(case inst_ty of
SOME ty =>
Types.print_type options ty
| NONE =>
Types.print_type options ty) ^
" = " ^ value_string)
in
accumulate_windowing_info(name,(ty,value,value_string))
end)
| (_,env) => print_env(env, (indent ^ " "), ns, spill))
envs);
print_env(env, (indent ^ " "), ns, spill)))
| print_env((FN (name,_,_,_)), indent, _, _) =
Crash.impossible "FN:print_env:debugger_print"
| print_env((APP(BUILTIN,env,NONE)), indent, ns, spill) =
print_env(env, indent, ns, spill)
| print_env(APP(env1,env2,SOME(apps')), indent, ns, spill) =
let
val old_apps = !apps
val _ = apps := apps'
val _ = print_env(env1, indent, ns, spill)
val new_apps = !apps
val _ = apps := old_apps
val _ = print_env(env2, indent, ns, spill)
val _ = apps := new_apps
in
(apps := !apps + 1;
if !apps = spill then
raise Finished(!result)
else
())
end
| print_env((APP(env1,env2,NONE)), indent, ns, spill) =
let
fun print_envs()=
(print_env(env1, indent, ns, spill);
print_env(env2, indent, ns, spill))
in
(apps := !apps + 1;
if !apps = spill then
let
val _ = print_envs()
handle Finished _ =>
Crash.impossible "Finished:APP:print_env:debugger_print"
in
(apps := spill;
raise Finished(!result))
end
else
print_envs())
end
| print_env((RAISE(env)), indent, ns, spill) =
(apps := !apps + 1;
if !apps = spill then
let
val _ =
print_env(env, (indent ^ " "), ns, spill)
handle Finished _ =>
Crash.impossible "Finished:RAISE:print_env:debugger_print"
in
(apps := spill;
raise Finished(!result))
end
else
print_env(env, (indent ^ " "), ns, spill))
| print_env((SELECT(n,env)), indent, ns, spill) =
print_env(env, indent, (n::ns), spill)
| print_env((STRUCT(envs)), indent, ns, spill) =
(case ns of
nil => (ignore(map (fn env => print_env(env, indent, ns, spill)) envs); ())
| n::ns =>
print_env((Lists.nth(n,envs)), indent, ns, spill))
| print_env((LIST(envs)), indent, ns, spill) =
(ignore(map (fn env => print_env(env, indent, ns, spill)) envs); ())
| print_env((SWITCH(env,ref(OFFSET2(spill_ty, offset)),apps',envs)), indent, ns, spill) =
let
val old_apps = !apps
val _ =
(apps := apps';
print_env(env, (indent ^ " "), ns, spill))
val new_apps = !apps
val which_switch = Value.cast (Frame.frame_offset (present_frame,offset)) : int
val (tag,which_env) = Lists.nth (which_switch,envs)
val _ = output'("\n" ^ indent ^ "switching on " ^ print_tag tag)
val old_tfs = !tfs
val old_ves = !ves
val envsstr =
(apps := old_apps;
tfs := []; ves := [];
print_env(which_env, (indent ^ " "), ns, spill))
handle exn => (tfs := old_tfs; ves := old_ves; raise exn)
val _ = tfs := old_tfs
val _ = ves := old_ves
in
apps := new_apps
end
| print_env((HANDLE(env1,ref(offset),
apps',apps'',env2)), indent, ns, spill) =
let
val old_tfs = !tfs
val old_ves = !ves
val _ = (tfs := []; ves := [])
val old_apps = !apps
val _ = apps := apps'
val spill' = case offset of
OFFSET2(spill_ty, offset) =>
(case spill_ty of
RuntimeEnv.FP => Frame.frame_double(present_frame,offset)
| _ => Frame.frame_offset(present_frame,offset))
| OFFSET1 _ => Crash.impossible "OFFSET1 in HANDLE print_env"
val spill' : int = Value.cast spill'
val _ =
if do_debug then
TextIO.output(debug_out,"  handle spill = " ^
Int.toString spill' ^ "\n")
else
()
val _ = print_env(env1, (indent ^ " "), ns, spill')
handle Finished _ => ()
in
(tfs := old_tfs;
ves := old_ves;
if !apps = spill then
raise Finished(!result)
else
apps := apps'')
end
| print_env((EMPTY), _, _, _) = ()
| print_env((BUILTIN), _, _, _) = ()
| print_env(_, _, _, _) = Crash.impossible "print_env:print_env:debugger_print"
in
(fnstr ^ ((print_env(env, (" "), nil, first_spill); !result ^ "\n")
handle Finished str => str ^ "\n"),
return_windowing_info())
end
handle Finished(str) => (str,[])
| Lists.Nth => ("Can't display information for this frame\n",[])
end
end
;
