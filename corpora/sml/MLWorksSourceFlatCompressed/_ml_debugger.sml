require "$.basis.__int";
require "$.basis.__text_io";
require "$.basis.__string";
require "^.utils.lists";
require "^.utils.crash";
require "^.utils.__messages";
require "^.utils.__terminal";
require "^.basics.module_id";
require "^.basics.location";
require "^.basis.os_path";
require "^.main.encapsulate";
require "^.main.preferences";
require "^.main.project";
require "^.typechecker.types";
require "^.interpreter.incremental";
require "^.interpreter.shell_utils";
require "^.rts.gen.tags";
require "debugger_print";
require "^.main.stack_interface";
require "debugger_utilities";
require "value_printer";
require "newtrace";
require "stack_frame";
require "ml_debugger";
functor Ml_Debugger(
structure Lists : LISTS
structure Crash : CRASH
structure Path : OS_PATH
structure Encapsulate : ENCAPSULATE
structure Preferences : PREFERENCES
structure Project : PROJECT
structure ModuleId : MODULE_ID
structure Location : LOCATION
structure Types : TYPES
structure Incremental : INCREMENTAL
structure ShellUtils : SHELL_UTILS
structure ValuePrinter : VALUE_PRINTER
structure StackInterface : STACK_INTERFACE
structure DebuggerUtilities : DEBUGGER_UTILITIES
structure Tags : TAGS
structure DebuggerPrint : DEBUGGER_PRINT
structure Trace : TRACE
structure StackFrame : STACK_FRAME
sharing DebuggerPrint.Options = DebuggerUtilities.Options =
ShellUtils.Options = Types.Options = ValuePrinter.Options =
Incremental.InterMake.Inter_EnvTypes.Options
sharing DebuggerUtilities.Debugger_Types = Encapsulate.Debugger_Types
sharing DebuggerUtilities.Debugger_Types.Options = DebuggerPrint.Options
sharing Incremental.Datatypes = Types.Datatypes
sharing DebuggerPrint.RuntimeEnv = DebuggerUtilities.Debugger_Types.RuntimeEnv
sharing Types.Datatypes.NewMap = Encapsulate.ParserEnv.Map
sharing Encapsulate.BasisTypes.Datatypes = Types.Datatypes
sharing type Incremental.Context = ShellUtils.Context
sharing type DebuggerUtilities.Debugger_Types.information =
ValuePrinter.DebugInformation = Incremental.InterMake.Compiler.DebugInformation
sharing type Types.Datatypes.Type = DebuggerUtilities.Debugger_Types.Type =
ValuePrinter.Type
sharing type ValuePrinter.TypeBasis =
Incremental.InterMake.Compiler.TypeBasis
sharing type DebuggerUtilities.Debugger_Types.Type = DebuggerPrint.RuntimeEnv.Type
sharing type Preferences.preferences = ShellUtils.preferences
sharing type ModuleId.Location = Location.T
sharing type Project.ModuleId = ModuleId.ModuleId
sharing type Project.Project = Incremental.InterMake.Project
) : ML_DEBUGGER =
struct
structure Incremental = Incremental
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Debugger_Types = DebuggerUtilities.Debugger_Types
structure InterMake = Incremental.InterMake
structure Info = InterMake.Compiler.Info
structure ValuePrinter = ValuePrinter
structure Options = DebuggerPrint.Options
structure RuntimeEnv = DebuggerPrint.RuntimeEnv
datatype ('a, 'b)union = INL of 'a | INR of 'b
type preferences = Preferences.preferences
local
fun debug_print s = Terminal.output("  # " ^ s ^ "\n")
in
val do_debug = false
fun debugl f =
if do_debug then
app debug_print (f ())
else ()
fun debug f = if do_debug then debug_print (f ()) else ()
fun ddebug f = debug_print (f ())
end
fun assoc (a,[]) = NONE
| assoc (a,(a',b)::rest) =
if a = a' then SOME b
else assoc (a,rest)
fun firstn (l,n) =
if n < 0 then []
else
let
fun aux (_,0,acc) = rev acc
| aux ([],n,_) = l
| aux (a::b,n,acc) = aux (b,n-1,a::acc)
in
aux (l,n,[])
end
val cast : 'a -> 'b = MLWorks.Internal.Value.cast
val empty_map = NewMap.empty' (op < : string * string -> bool)
fun outputMessage (message:string):unit =
(Messages.output message;
Messages.flush())
fun location_source_file locdata =
let
fun aux1(#":"::l,acc) = implode (rev acc)
| aux1(c::l,acc) = aux1(l,c::acc)
| aux1([],acc) = implode (rev acc)
in
aux1(explode locdata,[])
end
fun get_data_from_frame frame =
let
fun get_name_and_location code_name =
let
fun duff () = debug (fn _ => "Odd codename:"^code_name^":")
fun aux1( #"[" ::l,acc) = (acc,l)
| aux1(c::l,acc) = aux1(l,c::acc)
| aux1([],acc) = (duff();(acc,[]))
fun aux2([ #"]" ],acc) = (acc,nil)
| aux2( #"]" ::l,acc) = (duff();(acc,l))
| aux2(c::l,acc) = aux2(l,c::acc)
| aux2([],acc) = (duff();(acc,nil))
val (namechars,rest) = aux1(explode code_name,[])
val (locchars,fnname) = aux2 (rest,[])
in
(implode(rev namechars),(implode(rev locchars),implode fnname))
end
val name_string = StackInterface.frame_name frame
in
(get_name_and_location name_string,name_string)
end
fun get_arg_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) = get_arg_type object
| get_arg_type(Datatypes.FUNTYPE (arg,_)) = arg
| get_arg_type x = Datatypes.NULLTYPE
fun get_res_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) = get_res_type object
| get_res_type(Datatypes.FUNTYPE (_,res)) = res
| get_res_type x = Datatypes.NULLTYPE
exception FailedToGetTypeInfo
exception CacheFail
val size_of_data_cache = ref(7)
val data_cache = ref([] : (string * Debugger_Types.information) list)
fun cache_lookup file =
let
fun cache_lookup(x,[]) =
(debug (fn _ => file ^ " not found in cache") ;
raise CacheFail)
| cache_lookup(x,(elem as (h,t))::rest) =
if x=h
then (debug (fn _ => file ^ " found in cache");
(t,rest))
else
let
val (a,b) = cache_lookup(x,rest)
in
(a,elem::b)
end
val (result,rest) = cache_lookup(file,!data_cache)
val _ = data_cache := (file,result) :: rest
in
result
end
fun add_to_cache x = data_cache := x :: !data_cache
fun initialise_cache () = data_cache := nil
val stamp_map =
ref empty_map : (string, (string * int * int))NewMap.map ref
fun update_stamp_map project =
let
fun update_stamp_map' id =
let
val name = ModuleId.string id
in
case NewMap.tryApply'(!stamp_map, name) of
SOME _ => ()
| NONE =>
let
val requires =
if name = " __pervasive_library" then
[]
else
Project.get_requires(project, id)
in
case Project.get_object_info(project, id) of
SOME{stamps, ...} =>
(stamp_map :=
NewMap.define(!stamp_map, name, (name, stamps, 0));
Lists.iterate
update_stamp_map'
requires)
| _ => ()
end
handle _ => ()
end
in
update_stamp_map'
end
fun get_type_information (options,debug_info,((function_name,loc),name_string)) =
let
val source_file = location_source_file loc
in
case Debugger_Types.lookup_debug_info (debug_info,name_string) of
SOME funinfo =>
(funinfo,source_file)
| _ =>
(let
val file = Path.base source_file
val file_debug_info =
if Lists.member(file,["","__pervasive_library","__builtin_library"]) then
Debugger_Types.empty_information
else
cache_lookup file
handle CacheFail =>
(let
val project = Incremental.get_project()
val base = Path.base(Path.file source_file)
val information =
case ModuleId.from_string' base of
SOME id =>
let
val _ = update_stamp_map project id
val _ = update_stamp_map
project
(ModuleId.perv_from_string
("__pervasive_library",
Location.FILE"<debugger>"))
val information =
case Project.get_object_info(project, id) of
SOME{file, ...} =>
let
val information =
Encapsulate.input_debug_info
{file_name=file,
sub_modules= !stamp_map}
in
information
end
| _ => Debugger_Types.empty_information
in
information
end
| _ => Debugger_Types.empty_information
in
add_to_cache (file,information);
information
end
handle Encapsulate.BadInput msg =>
(debug (fn _ =>
"Decapsulation failed for function " ^
function_name ^ ": " ^ file ^ ":" ^ msg);
let
val information = Debugger_Types.empty_information
in
(add_to_cache (file,information); information)
end))
in
case Debugger_Types.lookup_debug_info (file_debug_info, name_string) of
SOME funinfo => (funinfo,source_file)
| _ => raise FailedToGetTypeInfo
end)
end
local
type part_of_a_frame =
(string
* (ValuePrinter.Type * MLWorks.Internal.Value.ml_value * string)
) list
type frame_details =
string
* string
* (ValuePrinter.Type * MLWorks.Internal.Value.ml_value * string)
* (unit -> string * part_of_a_frame,
string * part_of_a_frame)
union ref option
type frame = {name : string, loc : string, details: frame_details}
in
type debugger_window =
{parameter_details: string,
frames: frame list,
quit_fn: (unit -> unit) option,
continue_fn: (unit -> unit) option,
top_ml_user_frame: MLWorks.Internal.Value.Frame.frame option}
-> unit
end
datatype TypeOfDebugger =
WINDOWING of debugger_window * (string -> unit) * bool
| TERMINAL
datatype parameter =
INTERRUPT |
FATAL_SIGNAL of int |
EXCEPTION of exn |
BREAK of string |
STACK_OVERFLOW
datatype Continuation_action =
NORMAL_RETURN |
DO_RAISE of exn |
FUN of (unit -> unit)
datatype Continuation =
POSSIBLE of string * Continuation_action |
NOT_POSSIBLE
datatype FrameInfo =
FRAMEINFO of
Datatypes.Type * Debugger_Types.Backend_Annotation
* RuntimeEnv.RuntimeEnv * Datatypes.Type ref |
NOFRAMEINFO
datatype FrameSpec =
MLFRAME of
MLWorks.Internal.Value.Frame.frame *
((string * (string * string)) * string) *
MLWorks.Internal.Value.T option*
FrameInfo |
CFRAME of (MLWorks.Internal.Value.Frame.frame * string)
fun make_continuation_function (POSSIBLE (_,NORMAL_RETURN)) =
SOME (fn _ => ())
| make_continuation_function (POSSIBLE (_,DO_RAISE exn)) =
SOME (fn _ => raise exn)
| make_continuation_function (POSSIBLE (_,FUN f)) =
SOME f
| make_continuation_function NOT_POSSIBLE =
NONE
fun get_interpreter_information () = InterMake.current_debug_information ()
fun get_frame_details options (frame,offset,is_ml) =
if is_ml then
let
val debug_info as ((name,(loc,_)),name_str) = get_data_from_frame frame
val (info, arg) =
let
val (Debugger_Types.FUNINFO {ty,is_leaf,has_saved_arg,annotations,runtime_env,...},
source_file) =
get_type_information (options,
get_interpreter_information(),
((name,loc),name_str))
val arg_type = get_arg_type ty
val _ = debug (fn _ => "Getting info for " ^ name ^ ", offset: " ^ Int.toString (offset))
val current_annotation =
case assoc (offset, annotations) of
SOME a => a
| _ =>
let
val string =
("No annotation for offset " ^
Int.toString offset ^ " for " ^ name)
in
ignore(Debugger_Types.ERROR string);
Debugger_Types.NOP
end
val _ = debug (fn _ => "Annotation: " ^ Debugger_Types.print_backend_annotation options current_annotation)
in
(FRAMEINFO(arg_type,current_annotation,runtime_env,
ref Datatypes.NULLTYPE),
if has_saved_arg then
SOME (StackInterface.frame_arg frame)
else
NONE)
end
handle FailedToGetTypeInfo => (NOFRAMEINFO, NONE)
in
MLFRAME(frame,debug_info,arg,info)
end
else
let
val closure_bits = MLWorks.Internal.Bits.lshift(MLWorks.Internal.Value.cast (StackInterface.frame_closure frame),2)
fun clos_type n =
if n = Tags.STACK_START then "start"
else if n = Tags.STACK_EXTENSION then "extension"
else if n = Tags.STACK_DISTURB_EVENT then "disturb event"
else if n = Tags.STACK_EXTENSION then "extension"
else if n = Tags.STACK_RAISE then "raise"
else if n = Tags.STACK_EVENT then "event"
else if n = Tags.STACK_C_RAISE then "c raise"
else if n = Tags.STACK_C_CALL then "c call"
else if n = Tags.STACK_INTERCEPT then "intercept"
else if n = Tags.STACK_SPACE_PROFILE then "space profile"
else "unknown closure "^(Int.toString n)
in
CFRAME(frame, clos_type (closure_bits))
end
fun type_list_to_rectype ty_list =
let
val (numbered_list, _) =
Lists.number_from_by_one
(ty_list, 1,
fn i => Datatypes.Ident.LAB(Datatypes.Ident.Symbol.find_symbol
(Int.toString i)))
in
Datatypes.RECTYPE
(Lists.reducel
(fn (map, (ty, lab)) => NewMap.define'(map, (lab, ty)))
(NewMap.empty' Datatypes.Ident.lab_lt, numbered_list))
end
fun make_partial_map(poly_type, mono_type, partial_map) =
let
val poly_type = DebuggerUtilities.slim_down_a_type poly_type
val mono_type = DebuggerUtilities.slim_down_a_type mono_type
in
make_map(poly_type, mono_type, partial_map)
end
and make_map(Datatypes.METATYVAR(r as ref (_, ty, _), _, _),
mono_type, partial_map) =
(case ty of
Datatypes.NULLTYPE => (r, mono_type) :: partial_map
| _ => make_map(ty, mono_type, partial_map))
| make_map(Datatypes.META_OVERLOADED{1=ref ty, ...}, mono_type, partial_map) =
make_map(ty, mono_type, partial_map)
| make_map(ty as Datatypes.TYVAR _, mono_type, partial_map) =
partial_map
| make_map(Datatypes.METARECTYPE(ref {3=ty, ...}), mono_type, partial_map) =
make_map(ty, mono_type, partial_map)
| make_map(Datatypes.RECTYPE mapping, mono_type, partial_map) =
(case mono_type of
Datatypes.RECTYPE mapping' =>
Lists.reducel
(fn (map, ((_, ty1), (_, ty2))) =>
make_map(ty1, ty2, map))
(partial_map, Lists.zip(NewMap.to_list_ordered mapping,
NewMap.to_list_ordered mapping'))
| _ => partial_map )
| make_map(Datatypes.FUNTYPE(ty, ty'), mono_type, partial_map) =
(case mono_type of
Datatypes.FUNTYPE(ty'', ty''') =>
make_map(ty', ty'', make_map(ty, ty'', partial_map))
| _ => partial_map )
| make_map(Datatypes.CONSTYPE(ty_list, tyname), mono_type, partial_map) =
(case mono_type of
Datatypes.CONSTYPE(ty_list', tyname') =>
Lists.reducel
(fn (map, (ty1, ty2)) =>
make_map(ty1, ty2, map))
(partial_map, Lists.zip(ty_list, ty_list'))
| _ => partial_map )
| make_map(Datatypes.DEBRUIJN _, mono_type, partial_map) =
partial_map
| make_map(Datatypes.NULLTYPE, mono_type, partial_map) =
partial_map
fun apply_map partial_map =
let
fun apply_map(ty as Datatypes.METATYVAR(r as ref (_, ty', _), _, _)) =
(case ty' of
Datatypes.NULLTYPE =>
(Lists.assoc(r, partial_map) handle Lists.Assoc => ty)
| _ => apply_map ty')
| apply_map(Datatypes.META_OVERLOADED{1=ref ty, ...}) = apply_map ty
| apply_map(ty as Datatypes.TYVAR _) = ty
| apply_map(Datatypes.METARECTYPE(ref {3=ty, ...})) = apply_map ty
| apply_map(Datatypes.RECTYPE mapping) =
Datatypes.RECTYPE(NewMap.map apply_map_map mapping)
| apply_map(Datatypes.FUNTYPE(ty, ty')) =
Datatypes.FUNTYPE(apply_map ty, apply_map ty')
| apply_map(Datatypes.CONSTYPE(ty_list, tyname)) =
Datatypes.CONSTYPE(map apply_map ty_list, tyname)
| apply_map(ty as Datatypes.DEBRUIJN _) = ty
| apply_map(ty as Datatypes.NULLTYPE) = ty
and apply_map_map(_, ty) = apply_map ty
in
apply_map
end
fun print_parameter(INTERRUPT) = "INTERRUPT"
| print_parameter(FATAL_SIGNAL _) = "FATAL_SIGNAL"
| print_parameter(EXCEPTION _) = "EXCEPTION"
| print_parameter(BREAK _) = "BREAK"
| print_parameter STACK_OVERFLOW = "STACK_OVERFLOW"
fun make_frames (basic_frames,parameter,options) =
let
val Options.OPTIONS{print_options, ...} = options
exception ApplyRecipe
fun apply_recipe (annotation,ty,name) =
let
val result =
DebuggerUtilities.apply_recipe(annotation,ty)
handle DebuggerUtilities.ApplyRecipe problem =>
(debugl (fn _ =>
["Recipe problem " ^ problem ^ " for " ^ name,
"Annotation: " ^ Debugger_Types.print_backend_annotation options annotation,
"Arg : " ^ Types.debug_print_type options ty]);
raise ApplyRecipe)
in
if do_debug then
debugl (fn _ => ["Name: " ^ name,
"Annotation: " ^ Debugger_Types.print_backend_annotation options annotation,
"Arg : " ^ Types.debug_print_type options ty,
"Res : " ^Types.debug_print_type options result])
else ();
result
end
datatype TypeThunk =
NORECIPE |
RECIPE of (Debugger_Types.Backend_Annotation * Datatypes.Type)
fun generate_from_previous_types([], _, ty) = ty
| generate_from_previous_types(poly_type_list, mono_type_list, ty) =
let
val new_poly_type =
Datatypes.FUNTYPE
(type_list_to_rectype poly_type_list,
Datatypes.NULLTYPE)
val new_mono_type = type_list_to_rectype mono_type_list
val recipe =
DebuggerUtilities.generate_recipe options (new_poly_type, ty, "Debugger internal")
in
DebuggerUtilities.apply_recipe(recipe, new_mono_type)
handle DebuggerUtilities.ApplyRecipe problem =>
(debug(fn _ =>"Problem '" ^ problem ^
"' with record type inference mechanism\nty = " ^
Types.debug_print_type options ty ^
" as " ^ Types.extra_debug_print_type ty ^
" and\nnew_poly_type = " ^
Types.debug_print_type options new_poly_type ^
" as " ^ Types.extra_debug_print_type new_poly_type ^ "\n"
);
raise ApplyRecipe)
end
val sys_indicator = "[ "
fun upto_sys_indicator(s, i) =
if i+1 >= size s then s
else
if substring (s, i, 2) = sys_indicator then
substring (s, 0, i+2)
else
upto_sys_indicator(s, i+1)
fun get_code closure =
MLWorks.Internal.Value.cast
(MLWorks.Internal.Value.unsafe_record_sub
(MLWorks.Internal.Value.cast closure, 0))
val step_through : int = get_code(Trace.step_through (fn x => x))
val step_through_name =
upto_sys_indicator
(MLWorks.Internal.Value.code_name(MLWorks.Internal.Value.cast step_through),
0)
fun infer_types (recipe,[], _, _) = ()
| infer_types (recipe,frame::rest, done_poly_types, done_mono_types) =
let
in
(case frame of
CFRAME (frame,_) =>
(debug(fn _ => "CFRAME");
if StackInterface.is_stack_extension_frame frame then
infer_types (recipe,rest, done_poly_types, done_mono_types)
else
infer_types (NORECIPE,rest, done_poly_types, done_mono_types)
)
| MLFRAME (fr,((name,_),_),_,NOFRAMEINFO) =>
let
val frame_name = upto_sys_indicator(StackInterface.frame_name fr, 0)
val recipe =
if frame_name = step_through_name then recipe else NORECIPE
in
(debug(fn _ => "MLFRAME for " ^ name ^
" - no info, => no recipe,\n" ^
"parameter type: " ^ print_parameter parameter ^
" but inferred type was: " ^
(case recipe of
RECIPE(r, ty) =>
Types.debug_print_type options (apply_recipe (r,ty,name))
| _ => "unavailable due to missing recipe"));
infer_types (recipe,rest, done_poly_types, done_mono_types))
end
| MLFRAME (_,((name,_),_),_,FRAMEINFO(arg_type,annotation,_,tyref)) =>
let
val _ = debug (fn _ => "Doing " ^ name)
val itype =
if not (DebuggerUtilities.is_type_polymorphic arg_type)
then (debug (fn _ => "Not polymorphic"); arg_type)
else
let
val reconstructed_type =
case recipe of
NORECIPE =>
(debug (fn _ => "No recipe, trying previous types");
let
val done_poly_types =
type_list_to_rectype done_poly_types
val done_mono_types =
type_list_to_rectype done_mono_types
val map =
make_partial_map(done_poly_types,
done_mono_types, [])
val seen_tyvars = Types.no_tyvars
val (str1, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options, done_poly_types, seen_tyvars)
val (str2, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options, done_mono_types, seen_tyvars)
val (str3, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options, arg_type, seen_tyvars)
in
debug
(fn () =>
("Attempting partial tyvar substitution in stack backtrace for\n" ^
str1 ^ " as\n" ^ str2 ^
" with\n" ^ str3));
apply_map map arg_type
end)
| RECIPE(r,ty) =>
let
val _ = debug (fn _ => "Applying recipe to " ^
Types.debug_print_type options ty ^
" when arg_type = " ^
Types.debug_print_type options arg_type)
in
apply_recipe (r,ty,name)
handle ApplyRecipe =>
(debug(fn _ => "generating from previous types");
generate_from_previous_types(done_poly_types, done_mono_types, arg_type)
handle ApplyRecipe =>
let
val done_poly_types =
type_list_to_rectype done_poly_types
val done_mono_types =
type_list_to_rectype done_mono_types
val map =
make_partial_map(done_poly_types,
done_mono_types, [])
val seen_tyvars = Types.no_tyvars
val (str1, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options, done_poly_types, seen_tyvars)
val (str2, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options,done_mono_types, seen_tyvars)
val (str3, seen_tyvars) =
Types.print_type_with_seen_tyvars
(options, arg_type, seen_tyvars)
in
debug(fn _ => "Attempting partial tyvar substitution in stack backtrace for\n" ^
str1 ^ " as\n" ^
str2 ^ " with\n" ^
str3);
apply_map map arg_type
end)
end
in
Types.combine_types (reconstructed_type,arg_type)
handle Types.CombineTypes =>
(debug (fn _ =>
concat ["Combine types fails for: ",
name, "\n argtype: ",
Types.debug_print_type options arg_type,
"\n reconstructed type: ",
Types.debug_print_type options reconstructed_type]);
arg_type)
end
in
tyref := itype;
debug (fn _ => " Type = " ^ Types.extra_debug_print_type itype);
infer_types (RECIPE (annotation,itype), rest,
arg_type :: done_poly_types,
itype :: done_mono_types)
end)
end
val _ = stamp_map := empty_map
val frames = map (get_frame_details options) basic_frames
val _ = infer_types (NORECIPE,frames, [], [])
in
case parameter of
EXCEPTION _ =>
(
case rev frames of
(_::rest) => rest
| rest => rest)
| _ => rev frames
end
fun fix_runtime_env_types options (arg_type, inferred_type, raise_excp) =
let
fun fix_runtime_env_types(RuntimeEnv.LET(varinfo_env_list, env)) =
let
val varinfo_env_list =
map
(fn (RuntimeEnv.NOVARINFO, env) =>
(RuntimeEnv.NOVARINFO, fix_runtime_env_types env)
| (var as RuntimeEnv.VARINFO(str, (ty' as ref ty, rti_ref), offs_ref_opt),
env) =>
let
val recipe =
DebuggerUtilities.generate_recipe options
(Datatypes.FUNTYPE(arg_type, Datatypes.NULLTYPE), ty,
"fix_runtime_env_types")
val env = fix_runtime_env_types env
in
(RuntimeEnv.VARINFO
(str, (ref(DebuggerUtilities.apply_recipe(recipe, inferred_type)),
rti_ref), offs_ref_opt), env)
handle exn as DebuggerUtilities.ApplyRecipe _ =>
if raise_excp then raise exn
else
(
let
val map = make_partial_map(arg_type, inferred_type, [])
in
(RuntimeEnv.VARINFO
(str, (ref(apply_map map ty), rti_ref), offs_ref_opt), env)
end)
end)
varinfo_env_list
in
RuntimeEnv.LET(varinfo_env_list, fix_runtime_env_types env)
end
| fix_runtime_env_types(RuntimeEnv.FN(string, env, offs, funinfo)) =
RuntimeEnv.FN(string, fix_runtime_env_types env, offs, funinfo)
| fix_runtime_env_types(RuntimeEnv.APP(env, env', int_opt)) =
RuntimeEnv.APP(fix_runtime_env_types env, fix_runtime_env_types env',
int_opt)
| fix_runtime_env_types(RuntimeEnv.RAISE env) =
RuntimeEnv.RAISE(fix_runtime_env_types env)
| fix_runtime_env_types(RuntimeEnv.SELECT(int, env)) =
RuntimeEnv.SELECT(int, fix_runtime_env_types env)
| fix_runtime_env_types(RuntimeEnv.STRUCT(env_list)) =
RuntimeEnv.STRUCT(map fix_runtime_env_types env_list)
| fix_runtime_env_types(RuntimeEnv.LIST(env_list)) =
RuntimeEnv.LIST(map fix_runtime_env_types env_list)
| fix_runtime_env_types(RuntimeEnv.SWITCH(env, offs_ref, int, tag_env_list)) =
RuntimeEnv.SWITCH
(fix_runtime_env_types env, offs_ref, int,
map (fn (tag, env) => (tag, fix_runtime_env_types env)) tag_env_list)
| fix_runtime_env_types(RuntimeEnv.HANDLE(env, offs_ref, int, int', env')) =
RuntimeEnv.HANDLE(fix_runtime_env_types env, offs_ref, int, int',
fix_runtime_env_types env')
| fix_runtime_env_types(RuntimeEnv.EMPTY) = RuntimeEnv.EMPTY
| fix_runtime_env_types(RuntimeEnv.BUILTIN) = RuntimeEnv.BUILTIN
in
fix_runtime_env_types
end
fun find_space(arg as (s, i)) =
if i >= size s orelse MLWorks.String.ordof arg = ord #" " then
i
else
find_space(s, i+1)
fun ignore_spaces(arg as (s, i)) =
if i >= size s orelse MLWorks.String.ordof arg <> ord #" " then
i
else
ignore_spaces(s, i+1)
fun find_colon(arg as (s, i)) =
if i >= size s then
0
else
let
val arg' = (s, i+1)
in
if MLWorks.String.ordof arg = ord #":" then
ignore_spaces arg'
else
find_colon arg'
end
fun strip_name s =
let
val start = find_colon(s, 0)
val finish = find_space(s, start)
in
substring (s, start, finish-start)
end
local
fun should_ignore_break {hits, max, name} =
let
val (new_hits, new_max, ignore) =
let val new_hits = hits+1
in if max >= 0 andalso new_hits >= max
then (0, 1, false)
else (new_hits, max, true)
end
in
Trace.update_break{hits=new_hits, max=new_max, name=name};
ignore
end
in
fun should_ignore INTERRUPT = false
| should_ignore (FATAL_SIGNAL _) = false
| should_ignore (EXCEPTION _) = false
| should_ignore (BREAK fn_info) =
(case Trace.is_a_break (strip_name fn_info) of
SOME break_info => should_ignore_break break_info
| NONE => false)
| should_ignore STACK_OVERFLOW = false
end
fun ignore_command breakpoint_name ignore_count =
case Trace.is_a_break breakpoint_name of
SOME {hits, max, name} =>
(Trace.update_break {hits=hits, max=hits+ignore_count, name=name};
print"ignoring next ";
print(Int.toString ignore_count);
print" hits on the breakpoint ";
print breakpoint_name;
print"\n")
| NONE =>
(print breakpoint_name;
print" is not a breakpoint\n")
fun breakpoints_command () =
let fun display_breakpoint {hits, max, name} =
(print name;
print" ";
print(Int.toString hits);
print" ";
print(Int.toString max);
print"\n")
val breakpoints = Trace.breakpoints ()
in case breakpoints of
[] =>
print"No breakpoints set\n\n"
| _ =>
(app display_breakpoint breakpoints;
print"\n")
end
local
val frame_visibility =
[ ("c", StackFrame.hide_c_frames)
, ("anon", StackFrame.hide_anonymous_frames)
, ("setup", StackFrame.hide_setup_frames)
, ("handler", StackFrame.hide_handler_frames)
, ("delivered", StackFrame.hide_delivered_frames)
, ("duplicate", StackFrame.hide_duplicate_frames)
]
fun frame_type_to_ref frame_type =
SOME (Lists.assoc (frame_type, frame_visibility))
handle Lists.Assoc => NONE
fun apply action frame_type =
case frame_type_to_ref frame_type of
NONE => (print"No such frame type as ";
print frame_type;
print"\n")
| SOME flag => action flag
fun display_frames_status status =
let
fun out (name, hide) =
if !hide = status
then (print name; print" ")
else ()
in
app out frame_visibility;
print"\n"
end
val hide_user_frame = ref false
in
fun display_hidden_frames_command () =
display_frames_status true
fun display_revealed_frames_command () =
display_frames_status false
fun hide_frame_command frame_types =
app (apply (fn var => var := true)) frame_types
fun reveal_frame_command frame_types =
app (apply (fn var => var := false)) frame_types
local
fun classify ([], fs') = rev fs'
| classify (((f as (CFRAME (_, name)))::fs), fs') =
classify (fs, (StackFrame.hide_c_frames, f)::fs')
| classify (((f as (MLFRAME (_, ((name, (loc, file_name)), _), _, _)))::fs), fs') =
case name of
"<Setup>" =>
classify (fs, (StackFrame.hide_setup_frames, f)::fs')
| "<anon>" =>
classify (fs, (StackFrame.hide_anonymous_frames, f)::fs')
| "<handle>" =>
classify (fs, (StackFrame.hide_handler_frames, f)::fs')
| _ =>
if file_name = "" andalso size loc > 0 andalso
MLWorks.String.ordof (loc, 0) <> ord #" " then
classify (fs, (hide_user_frame, f)::fs')
else
let
val f' = (StackFrame.hide_delivered_frames, f)
in
case fs' of
[] => classify (fs, [f'])
| ((hide, f)::rest) =>
if hide = hide_user_frame
orelse hide = StackFrame.hide_anonymous_frames
then classify (fs, f'::(StackFrame.hide_duplicate_frames, f)::rest)
else classify (fs, f'::fs')
end
in
fun classify_frames frames = classify (frames, [])
end
end
fun display_frame (hide, (MLFRAME (frame, ((name, (loc,file_name)), nameStr), _, _))) =
(print"MLFRAME";
(if !hide then print" (hidden)" else ());
print"\nname = X";
print name;
print"X\nfile_name= X";
print file_name;
print"X\nlocation= X";
print loc;
print"X\nnameStr= X";
print nameStr;
print"X\n")
| display_frame (hide, (CFRAME (frame, name))) =
(print"CFRAME";
(if !hide then print" (hidden)" else ());
print"\nname = ";
print name;
print"\n")
fun show_frame_above_command ([], _, _) = print"TOP\n"
| show_frame_above_command (above as (a::as'), below, n) =
if n = 0
then display_frame a
else show_frame_above_command (as', a::below, n-1)
fun show_frame_below_command (_, [], _) = print"BOTTOM\n"
| show_frame_below_command (above, below as (b::bs), n) =
if n = 0
then display_frame b
else show_frame_below_command (b::above, bs, n-1)
local
fun out m = (print"Parameter = ";
print m;
print"\n")
in
fun show_parameter_command INTERRUPT = out "interrupt"
| show_parameter_command (FATAL_SIGNAL _) = out "fatal signal"
| show_parameter_command (EXCEPTION _) = out "exception"
| show_parameter_command (BREAK _) = out "breakpoint"
| show_parameter_command STACK_OVERFLOW = out "stack overflow"
end
local
fun make_frame_details (_, CFRAME (f,s),_) _ =
("<Cframe> "^s,"", (Datatypes.NULLTYPE,cast 0,""), NONE)
| make_frame_details (_, MLFRAME (f,((name,(loc,_)),st),_,NOFRAMEINFO),_) _ =
(concat[name,"<??>"],
concat["[",loc,"]"],
(Datatypes.NULLTYPE,cast 0,""),
NONE)
| make_frame_details (_, MLFRAME (f,((name,(loc,_)),st),NONE,_),_) _ =
(concat[name,"<??>"],
concat["[",loc,"]"],
(Datatypes.NULLTYPE,cast 0,""),
NONE)
| make_frame_details (options as (Options.OPTIONS {print_options, ...}), MLFRAME (f,
((name,(loc,_)),st),
SOME arg,
FRAMEINFO(arg_type,annotation,runtime_env,
ref inferredType)),frames) windowing =
(concat[name," ",
if windowing then ""
else
ValuePrinter.stringify_value false (print_options,
arg,
inferredType,
get_interpreter_information())],
concat[name,"[",loc,"]", ":",Types.print_type options inferredType],
if windowing then
(inferredType, arg,
ValuePrinter.stringify_value false (print_options,
arg,
inferredType,
get_interpreter_information()))
else (Datatypes.NULLTYPE, cast 0,""),
if windowing then
let
val new_runtime_env =
(fix_runtime_env_types options
(arg_type, inferredType, true)
runtime_env)
handle DebuggerUtilities.ApplyRecipe _ =>
let
fun collect_types(poly_list, mono_list, []) =
(poly_list, mono_list)
| collect_types(poly_list, mono_list, CFRAME _ :: frames) =
collect_types(poly_list, mono_list, frames)
| collect_types(poly_list, mono_list,
MLFRAME(_, _, _, NOFRAMEINFO) :: frames) =
collect_types(poly_list, mono_list, frames)
| collect_types(poly_list, mono_list,
MLFRAME(_, _, _,
FRAMEINFO(arg_type, _, _,
ref inferredType)) ::
frames) =
collect_types(arg_type :: poly_list,
inferredType :: mono_list,
frames)
val (poly_type_list, mono_type_list) =
collect_types([arg_type], [inferredType], frames)
val new_poly_type =
type_list_to_rectype poly_type_list
val new_mono_type = type_list_to_rectype mono_type_list
in
(fix_runtime_env_types options
(new_poly_type, new_mono_type, false)
runtime_env)
handle DebuggerUtilities.ApplyRecipe _ =>
runtime_env
end
val frame = StackInterface.variable_debug_frame f
in
SOME (ref (INL
(fn () =>
DebuggerPrint.print_env
((frame, new_runtime_env, inferredType),
fn (ty1,value) =>
ValuePrinter.stringify_value true (print_options,
value,
ty1,get_interpreter_information())
handle _ => "_" ,
options,true,
map (fn MLFRAME(frame,_,_,FRAMEINFO(_,_,env,ref ty))=>(frame,env,ty)
| MLFRAME (frame,_,_,_) => (frame,RuntimeEnv.EMPTY,Datatypes.NULLTYPE)
| CFRAME(frame,_) => (frame,RuntimeEnv.EMPTY,Datatypes.NULLTYPE))
frames))))
end
else NONE)
fun make_frame_strings (options, frames) =
let
fun aux (frame::frames,acc) =
let
val details = make_frame_details (options, frame,frames) true
val stuff =
case frame of
CFRAME (_,loc) => {name = "<Cframe>", loc = loc,details = details}
| MLFRAME (_,((name,(loc,_)),_),_,_) => {name = name,loc = loc, details = details}
in
aux (frames,stuff::acc)
end
| aux (nil,acc) = rev acc
in
aux (frames,[])
end
local
fun turn_on_exn_details_printing
(Options.PRINTOPTIONS{maximum_seq_size,
maximum_string_size,
maximum_ref_depth,
maximum_str_depth,
maximum_sig_depth,
maximum_depth,
float_precision,
print_fn_details,
print_exn_details,
show_id_class,
show_eq_info}) =
Options.PRINTOPTIONS{maximum_seq_size = maximum_seq_size,
maximum_string_size = maximum_string_size,
maximum_ref_depth = maximum_ref_depth,
maximum_str_depth = maximum_str_depth,
maximum_sig_depth = maximum_sig_depth,
maximum_depth = maximum_depth,
float_precision = float_precision,
print_fn_details = print_fn_details,
print_exn_details = true,
show_id_class = show_id_class,
show_eq_info = show_eq_info}
in
fun parameter_details (EXCEPTION exn, print_options) =
let
val print_options = turn_on_exn_details_printing print_options
val exn_name = ValuePrinter.stringify_value false (print_options,
cast exn,
Datatypes.CONSTYPE([],Types.exn_tyname),
get_interpreter_information())
in
concat ["Exception ", exn_name, " raised"]
end
| parameter_details (INTERRUPT, _) = "Interrupt"
| parameter_details (FATAL_SIGNAL s, _) =
"Fatal OS signal " ^ Int.toString s
| parameter_details (BREAK s, _) = s
| parameter_details (STACK_OVERFLOW, _) = "Break on stack overflow"
end
fun string_to_int str =
let
exception Finished of int
fun factor 0 = 1
| factor n = 10*factor (n-1)
fun string_to_int nil (n,_) = n
| string_to_int (m::ms) (n,power) =
string_to_int ms
((case m of
#"1" => 1*(factor power)+n
| #"2" => 2*(factor power)+n
| #"3" => 3*(factor power)+n
| #"4" => 4*(factor power)+n
| #"5" => 5*(factor power)+n
| #"6" => 6*(factor power)+n
| #"7" => 7*(factor power)+n
| #"8" => 8*(factor power)+n
| #"9" => 9*(factor power)+n
| #"0" => n
| _ => raise Finished(n)),power+1)
in
string_to_int (rev(explode str)) (0,0)
handle Finished(n) => n
end
local
fun userFrame (MLFRAME (_, ((name, (loc, fileName)), _), _, _)) =
fileName = "" andalso
size loc > 0 andalso
(String.sub(loc, 0)) <> #" "
| userFrame _ = false
in
fun locateFirstUserFrame (BREAK _, frames) =
((case (Lists.findp (fn frame => userFrame frame) frames) of
(MLFRAME (frame, _, _, _)) => SOME frame
| _ => NONE) handle Find => NONE)
| locateFirstUserFrame (_, _) = NONE
end
local
fun next ((_,[]), orig, _, k) = k orig
| next (frames as (above, (frame as (hide, _))::rest), orig, n, k) =
if !hide then
next ((frame::above, rest), orig, n, k)
else
if n = 0
then frames
else next ((frame::above, rest), frames, n-1, k)
fun prev (([], _), orig, _, k) = k orig
| prev (frames as ((frame as (hide, _))::rest, below), orig, n, k) =
let
val frames' = (rest, frame::below)
in
if !hide then
prev (frames', orig, n, k)
else
if n = 0
then frames'
else prev (frames', frames', n-1, k)
end
in
local
fun hit_bottom frames =
(print"No calling frame\n"; frames)
in
fun next_frame_command (frames, n) =
next (frames, frames, n, hit_bottom)
end
local
fun hit_top frames =
(print"No called frame\n"; frames)
in
fun previous_frame_command (frames, n) =
prev (frames, frames, n, hit_top)
end
local
fun hit_top (above, below) = ((rev below) @ above, [])
fun hit_bottom frames = prev (frames, frames, 0, hit_top)
in
fun move_to_first_visible_frame frames =
next (frames, frames, 0, hit_bottom)
end
fun goto_top_command frames = move_to_first_visible_frame frames
local
fun hit_top frames = frames
fun next (frames as (_, []), (_, [])) =
prev (frames, frames, 0, hit_top)
| next (frames as (_, []), orig as (_, ((hide, _)::rest))) =
if !hide
then prev (orig, frames, 0, hit_top)
else orig
| next (frames as (above, (frame as (hide, _))::rest), orig) =
let
val above' = (frame::above, rest)
in
if !hide
then next (above', orig)
else next (above', frames)
end
in
fun goto_bottom_command frames = next (frames, frames)
end
end
fun output_frame_details (options, frame) =
let
val frame_details = make_frame_details (options, frame, nil) false
in
print( #1 frame_details);
print"\n"
end
fun backtrace_command (options, frames) =
let
fun display (hide, frame) =
if !hide
then ()
else output_frame_details (options, frame)
in
print"(Current frame)\n";
app display frames
end
fun next_command NONE =
print"No ML function to step over\n"
| next_command (SOME frame) =
Trace.next frame
fun frame_details_command ([], _) = ()
| frame_details_command ((_, (CFRAME _))::_, _) = ()
| frame_details_command ((frames as (_, (MLFRAME (frame, _, _, frameinfo)))::_), options) =
case frameinfo of
NOFRAMEINFO => ()
| FRAMEINFO(_, _, runtime_env, ref ty) =>
let
fun munge_frame (_, (MLFRAME (frame,_,_,FRAMEINFO(_,_,env,ref ty)))) =
(frame, env, ty)
| munge_frame (_, (MLFRAME (frame,_,_,_))) =
(frame, RuntimeEnv.EMPTY, Datatypes.NULLTYPE)
| munge_frame (_, (CFRAME (frame,_))) =
(frame, RuntimeEnv.EMPTY, Datatypes.NULLTYPE)
val Options.OPTIONS {print_options, ...} = options
fun display_value (ty,value) =
ValuePrinter.stringify_value true
(print_options,
value,
ty,
get_interpreter_information())
val windowing = false
val frame = StackInterface.variable_debug_frame frame
val frame_details = (frame ,runtime_env, ty)
val frames = map munge_frame frames
val (str, _) = DebuggerPrint.print_env (frame_details,
fn (ty,x) => display_value (ty,x) handle _ => "_",
options, windowing, frames)
in
print str
end
fun edit_frame (preferences, (CFRAME _)) =
print"Cannot edit source for C frame\n"
| edit_frame (preferences, (MLFRAME (_,((_,(loc,_)),_),_,_))) =
(ignore(ShellUtils.edit_source (loc, preferences)); ())
handle ShellUtils.EditFailed s =>
print("Edit failed: " ^ s ^ "\n")
in
fun ml_debugger
(type_of_debugger, options, preferences)
(base_frame, parameter, quit_continuation, continue_continuation) =
if should_ignore parameter then
()
else
let
val Options.OPTIONS {print_options, ...} = options
val Preferences.PREFERENCES {environment_options,...} = preferences
val Preferences.ENVIRONMENT_OPTIONS
{use_debugger, window_debugger, ...} =
environment_options
fun stack_empty basic_frames =
case parameter of
EXCEPTION _ => length basic_frames <= 2
| _ => false
val outfun = print
fun outline s = outfun (s ^ "\n")
val output_frame_details = fn frame => output_frame_details (options, frame)
fun output_full_frame_details frame =
outline (#2 (make_frame_details (options, frame,nil) false))
fun prompt s = (outfun s; TextIO.flushOut TextIO.stdOut)
fun do_quit_action () =
(case quit_continuation of
NOT_POSSIBLE => ()
| POSSIBLE (_,NORMAL_RETURN) => ()
| POSSIBLE (_,DO_RAISE exn) => raise exn
| POSSIBLE (_,FUN f) => f())
exception Exit
val edit_frame = fn frame => edit_frame (preferences, frame)
fun do_input frames =
let
fun is_whitespace char =
case char of
#" " => true
| #"\n" => true
| _ => false
fun parse_command s =
let
fun parse ([],acc) =
(case acc of
[] => []
| _ => [implode (rev acc)])
| parse (char::rest,acc) =
if is_whitespace char
then
(case acc of
nil => parse (rest,nil)
| _ => implode(rev acc)::parse(rest,nil))
else
parse (rest,char::acc)
in
parse (explode s,[])
end
fun display_simple_help_info () =
outline "Enter ? or help for help"
fun display_help_info () =
app
outline
(["Commands:",
" <                - go to the earliest frame in the stack",
" >                - go to the latest frame in the stack",
" i                - next frame INto the stack or next later frame (callee)",
" i <n>            - next n frames INto the stack",
" o                - next frame OUT of the stack or next earlier frame (caller)",
" o <n>            - next n frames OUT of the stack",
" b                - do a backtrace of the stack",
" f                - show full frame details",
" e                - edit definition",
" h {<name>}       - hide the given types of frame or list hidden frame types if none given",
" r {<name>}       - reveal the given types of frame or list the reveald frame types if none given",
" p                - print values of local and closure variables",
" c                - continue interrupted computation",
" s                - step through computation",
" s <n>            - step through computation to nth function call",
" n                - step over the current function to the start of the next one",
" trace <name>     - set trace on function entry at <name>",
" breakpoint <name> - set breakpoint on function entry at <name>",
" untrace <name>   - unset trace on function entry at <name>",
" unbreakpoint <name> - unset breakpoint on function entry at <name>",
" breakpoints      - display the list of breakpoints",
" ignore name n    - ignore the next n hits on the breakpoint on function <name>",
" help             - display this help info",
" ?                - display this help info"] @
(case continue_continuation of
POSSIBLE(st,_) => [" c                - " ^ st]
| _ => []) @
(case quit_continuation of
POSSIBLE(st,_) => [" q                - " ^ st]
| _ => []))
val firstUserFrame = locateFirstUserFrame (parameter, frames)
val classified_frames = ([], classify_frames frames)
fun loop (frames as (above, below)) =
let
fun do_continue action =
(case continue_continuation of
NOT_POSSIBLE => outline "Cannot Continue"
| POSSIBLE (_,NORMAL_RETURN) => (ignore(action ()); raise Exit)
| POSSIBLE (_,DO_RAISE exn) => raise exn
| POSSIBLE (_,FUN f) => (ignore(action ()); f (); raise Exit))
fun do_quit () =
(case quit_continuation of
NOT_POSSIBLE => outline "Cannot Quit"
| POSSIBLE (_,NORMAL_RETURN) => raise Exit
| POSSIBLE (_,DO_RAISE exn) => raise exn
| POSSIBLE (_,FUN f) => (f()))
fun apply_to_current_frame action =
case below of
[] => print"No visible frame\n"
| ((_, this)::_) => action this
val _ = if Trace.stepping ()
then ()
else apply_to_current_frame output_frame_details
val _ = prompt "Debugger> "
val command_and_args =
let
val _ =
if TextIO.endOfStream TextIO.stdIn then
(do_quit(); raise Exit)
else ()
val line = TextIO.inputLine TextIO.stdIn
in
parse_command line
end
in
case command_and_args of
">"::_ => loop (goto_top_command classified_frames)
| "<"::_ => loop (goto_bottom_command frames)
| "f"::_ =>
(apply_to_current_frame output_full_frame_details;
loop frames)
| "e"::_ =>
(apply_to_current_frame edit_frame; loop frames)
| ["i"] =>
loop (previous_frame_command (frames, 1))
| "i"::arg::_ =>
let
val n = string_to_int arg
in
loop (previous_frame_command (frames, n))
end
| ["o"] =>
loop (next_frame_command (frames, 1))
| "o"::arg::_ =>
let
val n = string_to_int arg
in
loop (next_frame_command (frames, n))
end
| ["b"] =>
(backtrace_command (options, below);
print"(Outermost frame)\n";
loop frames)
| ("b"::arg::_) =>
let
val frames' = firstn (below, string_to_int arg)
in
backtrace_command (options, frames');
loop frames
end
| "c"::_ =>
(do_continue (fn () => ());
loop frames)
| ["h"] =>
(display_hidden_frames_command ();
loop frames)
| "h"::names =>
(hide_frame_command names;
loop (move_to_first_visible_frame frames))
| "q"::_ =>
(do_quit ();
ignore(raise Exit);
loop frames)
| ["r"] =>
(display_revealed_frames_command ();
loop frames)
| "r"::names =>
(reveal_frame_command names;
loop (move_to_first_visible_frame frames))
| ""::_ => loop frames
| "?"::_ =>
(display_help_info(); loop frames)
| "help"::_ =>
(display_help_info(); loop frames)
| "trace" :: args =>
(app Trace.trace args; loop frames)
| ["breakpoints"] =>
(breakpoints_command (); loop frames)
| "breakpoint" :: args =>
(app Trace.break
(map (fn name => {name=name, hits=0, max=1}) args);
loop frames)
| "ignore" :: fn_name :: n :: _ =>
(ignore_command fn_name (string_to_int n);
loop frames)
| "untrace" :: args => (app Trace.untrace args;
loop frames)
| "unbreakpoint" :: args =>
(app Trace.unbreak args; loop frames)
| ["s"] =>
(do_continue (fn () => Trace.set_stepping true);
loop frames)
| "s" :: arg :: _ =>
(do_continue (fn () =>
(Trace.set_stepping true;
Trace.set_step_count (string_to_int arg)));
loop frames)
| ["n"] =>
(do_continue (fn () => next_command firstUserFrame);
loop frames)
| "show_frame_above"::arg::_ =>
(show_frame_above_command (above, below, string_to_int arg);
loop frames)
| "show_frame_below"::arg::_ =>
(show_frame_below_command (above, below, string_to_int arg);
loop frames)
| ["show_parameter"] =>
(show_parameter_command parameter;
loop frames)
| ["t"] =>
(do_continue (fn () => Trace.set_trace_all true);
loop frames)
| "p"::_ =>
(frame_details_command (below, options);
loop frames)
| "show_debug"::_ =>
(let
val debug_info = get_interpreter_information ()
in
app (fn s => print(s^"\n")) (Debugger_Types.print_information options (debug_info,true))
end;
loop frames)
| _ => (display_simple_help_info();
loop frames)
end
in
(if Trace.stepping () then
()
else
outline "Current (innermost) stack frame:");
loop (move_to_first_visible_frame classified_frames)
handle Exit => ()
end
fun make_entry_info_line () =
(case quit_continuation of
POSSIBLE (s,_) => "q : " ^ s ^ ", "
| _ => "") ^
(case continue_continuation of
POSSIBLE (s,_) => "c : " ^ s ^ ", "
| _ => "") ^
" ? : more help"
fun tty_debugger basic_frames =
if not (!use_debugger) then
(outline (parameter_details (parameter, print_options));
do_quit_action ())
else
if stack_empty basic_frames then
outline (parameter_details (parameter, print_options) ^ " at top level")
else
(outline (parameter_details (parameter, print_options));
(if Trace.stepping () then
()
else
outline ("Entering debugger, commands: " ^ make_entry_info_line ()));
do_input (make_frames (basic_frames,parameter,options)))
in
let
val top_frame = MLWorks.Internal.Value.Frame.current ()
val no_message = Trace.stepping () orelse not (!use_debugger)
val _ =
if no_message
then ()
else outputMessage "Entering Debugger, scanning stack ... "
val basic_frames =
StackInterface.get_basic_frames (top_frame,base_frame)
val _ =
if no_message
then ()
else outputMessage "done.\n";
val debugger_call =
case type_of_debugger of
TERMINAL =>
tty_debugger basic_frames
| WINDOWING (make_window, send_message, tty_ok) =>
if tty_ok andalso not (!window_debugger) then
tty_debugger basic_frames
else if not (!use_debugger) then
(send_message (parameter_details (parameter, print_options) ^ "\n");
do_quit_action ())
else if stack_empty basic_frames then
send_message (parameter_details (parameter, print_options) ^ " at top level\n")
else
let
val frames = make_frames (basic_frames,parameter,options)
val firstUserFrame = locateFirstUserFrame (parameter, frames)
val frame_strings = make_frame_strings (options, frames)
in
make_window
{parameter_details = parameter_details (parameter, print_options),
frames = frame_strings,
quit_fn =
make_continuation_function quit_continuation,
continue_fn =
make_continuation_function continue_continuation,
top_ml_user_frame = firstUserFrame};
()
end
in
()
end
end
end
val start_frame_ref = ref (MLWorks.Internal.Value.Frame.current())
fun with_start_frame f =
let
val frame = MLWorks.Internal.Value.Frame.current ()
val old_frame = !start_frame_ref
val _ = start_frame_ref := frame
val previousStepStatus = Trace.stepping ()
val result =
f frame
handle exn => (start_frame_ref := old_frame; Trace.set_stepping previousStepStatus; raise exn)
in
start_frame_ref := old_frame;
Trace.set_stepping previousStepStatus;
result
end
fun get_start_frame () = !start_frame_ref
val debugger_type_ref = ref TERMINAL
fun with_debugger_type debugger_type f =
let
val old_type = !debugger_type_ref
val _ = debugger_type_ref := debugger_type
val result = (with_start_frame f) handle exn => (debugger_type_ref := old_type; raise exn)
in
debugger_type_ref := old_type;
result
end
fun get_debugger_type () = !debugger_type_ref
end
;
