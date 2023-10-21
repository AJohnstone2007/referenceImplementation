require "../basis/__int";
require "../typechecker/types";
require "../basics/identprint";
require "../utils/crash";
require "runtime_env";
require "debugger_types";
functor Debugger_Types(
structure Types : TYPES
structure Crash : CRASH
structure IdentPrint : IDENTPRINT
structure RuntimeEnv : RUNTIMEENV
sharing Types.Datatypes.Ident = IdentPrint.Ident
sharing Types.Options = IdentPrint.Options
) : DEBUGGER_TYPES =
struct
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure Options = IdentPrint.Options
structure RuntimeEnv = RuntimeEnv
type ('a,'b) Map = ('a,'b) NewMap.map
type Type = Datatypes.Type
type Instance = Datatypes.Instance
type Tyname = Datatypes.Tyname
type printOptions = Options.print_options
datatype Recipe =
SELECT of int * Recipe |
MAKERECORD of (string * Recipe) list |
NOP |
ERROR of string |
FUNARG of Recipe |
FUNRES of Recipe |
MAKEFUNTYPE of Recipe * Recipe |
DECONS of int * Recipe |
MAKECONSTYPE of Recipe list * Types.Datatypes.Tyname
type Backend_Annotation = Recipe
datatype FunInfo =
FUNINFO of
{ty : Type,
is_leaf : bool,
has_saved_arg : bool,
annotations : (int * Backend_Annotation) list,
runtime_env : RuntimeEnv.RuntimeEnv,
is_exn : bool}
datatype information =
INFO of (string,FunInfo) NewMap.map
val empty_information = INFO (Datatypes.NewMap.empty' ((op<):string*string->bool))
fun augment_information (debug,INFO info, INFO more_info) =
if debug
then INFO (Datatypes.NewMap.union (info, more_info))
else
INFO(Datatypes.NewMap.fold
(fn (info, name, funinfo as FUNINFO {is_exn,...}) =>
if is_exn then NewMap.define (info,name,funinfo)
else NewMap.undefine(info, name))
(info, more_info))
fun clear_information (name, INFO (info)) =
INFO (NewMap.undefine (info, name))
val null_backend_annotation = NOP
val empty_runtime_env = RuntimeEnv.EMPTY
fun print_backend_annotation options NOP = "Nop"
| print_backend_annotation options (ERROR s) = "Error: " ^ s
| print_backend_annotation options (SELECT(x,recipe)) =
"Select{" ^ Int.toString x ^ "," ^
print_backend_annotation options recipe ^ "}"
| print_backend_annotation options (MAKERECORD(recipes)) =
let
fun join [] = ""
| join [(name,h)] = name ^ "=" ^ print_backend_annotation
options h
| join ((name,h)::t) = name ^ "=" ^ print_backend_annotation
options h ^ "," ^ join t
in
"MakeRecord{" ^ join recipes ^ "}"
end
| print_backend_annotation options (FUNARG x) =
"Funarg{" ^ print_backend_annotation options x ^ "}"
| print_backend_annotation options (FUNRES x) =
"Funres{" ^ print_backend_annotation options x ^ "}"
| print_backend_annotation options (MAKEFUNTYPE(from,to)) =
"(" ^ print_backend_annotation options from ^ " -> " ^
print_backend_annotation options to ^ ")"
| print_backend_annotation options (DECONS(n,recipe)) =
"DeCons{" ^ Int.toString n ^ "," ^
print_backend_annotation options recipe ^ "}"
| print_backend_annotation options (MAKECONSTYPE(recipe_list,tyname)) =
let
fun join [] = ""
| join [x] = x
| join (h::t) = h ^ "," ^ join t
in
"MakeConsType{" ^
Types.debug_print_type options (Types.Datatypes.CONSTYPE([],tyname))
^
(case recipe_list of
[] => ""
| _ => "," ^ join(map (fn x => print_backend_annotation options x)
recipe_list)) ^
"}"
end
local
fun tostring options print_recipes (name,FUNINFO {annotations,...}) =
if print_recipes
then
concat (name ::
map
(fn (i,recipe) =>
"\n   " ^ Int.toString i ^ ":" ^
print_backend_annotation options recipe)
(rev annotations))
else name
in
fun print_information options (INFO debug_info,print_recipes) =
map (tostring options print_recipes)
(NewMap.to_list_ordered debug_info)
fun print_function_information options
(name,INFO debug_info,print_recipes) =
case NewMap.tryApply' (debug_info,name) of
SOME info => tostring options print_recipes
(name,info)
| _ => "No info for " ^ name
end
val print_type = Types.debug_print_type
fun make_pair(ty1,ty2) =
Types.add_to_rectype(Ident.LAB (Symbol.find_symbol "1"),
ty1,
Types.add_to_rectype(Ident.LAB (Symbol.find_symbol "2"),
ty2,
Types.empty_rectype))
val null_type = Datatypes.NULLTYPE
val int_type = Types.int_type
val int_pair_type = make_pair(int_type,int_type)
val string_type = Datatypes.CONSTYPE([],Types.string_tyname)
val string_pair_type = make_pair(string_type,string_type)
val string_list_type = Datatypes.CONSTYPE([string_type],Types.list_tyname)
val exn_type = Datatypes.CONSTYPE([],Types.exn_tyname)
fun string_metatyvar (t as Datatypes.METATYVAR (_,eq,imp),metastack) =
let
val (how_deep,metastack') = find_depth (t,metastack)
val alpha = "meta-" ^ (Int.toString how_deep)
val eq_bit = if eq then "'" else ""
val imp_bit = if imp then "_" else ""
in
("'"^ eq_bit ^ imp_bit ^ alpha,metastack')
end
| string_metatyvar _ = Crash.impossible "string_metatyvar in _debugger_types"
and string_overloaded _ = Crash.impossible "string_overloaded in _debugger_types"
and find_depth (Datatypes.METATYVAR(code,_,_),metastack) =
let
fun find_depth' [] = (length metastack + 1,code::metastack)
| find_depth'(code'::rest) =
if code = code'
then (length rest + 1,metastack)
else find_depth' rest
in
find_depth' metastack
end
| find_depth _ = Crash.impossible "find_depth in _debugger_types"
and string_metarec _ = Crash.impossible "string_metarec in _debugger_types"
and string_constype options (t as (Datatypes.CONSTYPE ([],name)),stack,acc_string) =
if acc_string = ""
then
(Types.print_name options name,stack)
else
("(" ^ acc_string ^ ")" ^ (Types.print_name options name),stack)
| string_constype options (Datatypes.CONSTYPE (h::t,name),stack,acc_string) =
let
val (s,stack') = string_types options (h,stack)
in
if acc_string = "" then
string_constype options (Datatypes.CONSTYPE (t, name), stack', s)
else
string_constype options (Datatypes.CONSTYPE (t,name),
stack',acc_string ^ ", " ^ s)
end
| string_constype _ _ = Crash.impossible "string_constype in _debugger_types"
and string_types options (t as (Datatypes.METATYVAR (ref(_,Datatypes.NULLTYPE,_),_,_)),stack) =
string_metatyvar (t,stack)
| string_types options (Datatypes.METATYVAR (ref(_,t,_),_,_),stack) =
string_types options (t,stack)
| string_types options (t as (Datatypes.META_OVERLOADED {1=ref Datatypes.NULLTYPE,...}), stack) =
let
fun error_fn _ = Crash.impossible"_debugger_types: string_types"
val _ = Types.resolve_overloading(true, t, error_fn)
in
string_types options(t, stack)
end
| string_types options
(Datatypes.META_OVERLOADED {1=ref t,...},stack) =
string_types options (t,stack)
| string_types options (Datatypes.METARECTYPE (ref (_,true,t as Datatypes.METARECTYPE _,_,_)),stack) =
string_types options (t,stack)
| string_types options (t as (Datatypes.METARECTYPE (ref (_,true,_,_,_))),stack) =
string_metarec (t,stack)
| string_types options (Datatypes.METARECTYPE (ref(_,_,t,_,_)),stack) =
string_types options (t,stack)
| string_types options (Datatypes.DEBRUIJN n,stack) =
("Debruijn found",stack)
| string_types options (Datatypes.TYVAR (_,t),stack) =
((IdentPrint.printTyVar t),stack)
| string_types options (Datatypes.NULLTYPE,stack) = ("Nulltype ",stack)
| string_types options (Datatypes.FUNTYPE (a,r),stack) =
let
val (s,m) = string_types options (a,stack)
val (s',m') = string_types options (r,m)
in
("(" ^ s ^ ") -> " ^ s',m')
end
| string_types options (t as (Datatypes.CONSTYPE _),stack) =
string_constype options (t,stack,"")
| string_types options (Datatypes.RECTYPE amap,stack) =
let
val stack_ref = ref stack
fun ref_printer t =
let
val ref stack = stack_ref
val (s,new_stack) = string_types options (t,stack)
in
(stack_ref := new_stack;
s)
end
val comma_rec_string = NewMap.string
(fn x => "," ^ (IdentPrint.printLab x))
ref_printer
{start="", domSep=" : ", itemSep="", finish=""}
amap
fun rec_list([]) = []
| rec_list(_::t) = t
in
("{"^ (implode (rec_list(explode comma_rec_string))) ^ "}",
!stack_ref)
end
fun set_proc_data (name,is_leaf,has_saved_arg,runtime_env,INFO debug_map) =
(case NewMap.tryApply' (debug_map, name) of
SOME
(FUNINFO {ty,annotations,is_exn,...}) =>
(INFO (NewMap.define(debug_map,
name,
(FUNINFO
{ty=ty,
is_leaf=is_leaf,
has_saved_arg=has_saved_arg,
annotations=annotations,
runtime_env=runtime_env,
is_exn=is_exn}))))
| _ =>
(INFO (NewMap.define(debug_map,
name,
(FUNINFO
{ty=null_type,
is_leaf=is_leaf,
has_saved_arg=has_saved_arg,
annotations=nil,
runtime_env=runtime_env,
is_exn=false})))))
fun add_debug_info (INFO map,name,funinfo) = INFO (NewMap.define (map,name,funinfo))
fun lookup_debug_info (INFO map,name) = NewMap.tryApply' (map,name)
fun add_annotation (name,count,debug,INFO debug_map) =
case NewMap.tryApply'(debug_map, name) of
SOME (FUNINFO {ty,is_leaf,has_saved_arg,annotations,runtime_env, is_exn}) =>
INFO (NewMap.define
(debug_map, name,
FUNINFO {ty=ty,
is_leaf=is_leaf,
has_saved_arg=has_saved_arg,
annotations=(count,debug)::annotations,
runtime_env=runtime_env,
is_exn=is_exn}))
| _ => INFO debug_map
fun debug_info_to_list (INFO map) =
NewMap.to_list map
fun debug_info_from_list list =
INFO (NewMap.from_list ((op<):string*string->bool, op=) list)
end
;
