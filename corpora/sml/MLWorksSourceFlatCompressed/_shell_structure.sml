require "../basis/__int";
require "../basis/__list";
require "../basis/__timer";
require "../system/__time";
require "../utils/lists";
require "../basis/os";
require "../utils/getenv";
require "../basics/module_id";
require "../typechecker/types";
require "../typechecker/strenv";
require "../typechecker/tyenv";
require "../typechecker/environment";
require "../typechecker/valenv";
require "../typechecker/scheme";
require "../typechecker/basistypes";
require "../main/mlworks_io";
require "../debugger/debugger_utilities";
require "../debugger/value_printer";
require "../debugger/newtrace";
require "../debugger/stack_frame";
require "inspector";
require "inspector_values";
require "incremental";
require "shell_types";
require "user_context";
require "../main/user_options";
require "../main/preferences";
require "../main/proj_file";
require "../main/project";
require "../editor/custom";
require "shell_utils";
require "save_image";
require "shell_structure";
functor ShellStructure (
structure Lists : LISTS
structure OS : OS
structure Getenv : GETENV
structure ModuleId : MODULE_ID
structure Types : TYPES
structure Strenv : STRENV
structure Tyenv : TYENV
structure Valenv : VALENV
structure Scheme : SCHEME
structure BasisTypes : BASISTYPES
structure Env : ENVIRONMENT
structure DebuggerUtilities : DEBUGGER_UTILITIES
structure ValuePrinter : VALUE_PRINTER
structure Trace : TRACE
structure Io : MLWORKS_IO
structure ProjFile : PROJ_FILE
structure Project : PROJECT
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure ShellTypes: SHELL_TYPES
structure UserContext: USER_CONTEXT
structure Inspector : INSPECTOR
structure InspectorValues : INSPECTOR_VALUES
structure Incremental: INCREMENTAL
structure ShellUtils : SHELL_UTILS
structure SaveImage : SAVE_IMAGE
structure CustomEditor : CUSTOM_EDITOR
structure StackFrame : STACK_FRAME
sharing Types.Datatypes = Strenv.Datatypes = Tyenv.Datatypes =
Valenv.Datatypes = Scheme.Datatypes = BasisTypes.Datatypes =
Incremental.Datatypes = Env.Datatypes
sharing Incremental.InterMake.Compiler.Info =
ShellUtils.Info
sharing UserOptions.Options =
ValuePrinter.Options =
ShellTypes.Options =
Incremental.InterMake.Compiler.Options =
Types.Options =
ShellUtils.Options =
DebuggerUtilities.Debugger_Types.Options
sharing Types.Datatypes.Ident =
Incremental.InterMake.Compiler.Absyn.Ident
sharing Types.Datatypes.NewMap =
Incremental.InterMake.Compiler.NewMap
sharing type ModuleId.ModuleId = Incremental.ModuleId = Project.ModuleId
sharing type DebuggerUtilities.Debugger_Types.information =
ValuePrinter.DebugInformation =
Incremental.InterMake.Compiler.DebugInformation
sharing type BasisTypes.Datatypes.Type = ValuePrinter.Type =
InspectorValues.Type = Trace.Type = ShellUtils.Type =
DebuggerUtilities.Debugger_Types.Type = Inspector.Type
sharing type Incremental.InterMake.Compiler.TypeBasis =
ValuePrinter.TypeBasis =
BasisTypes.Basis
sharing type UserOptions.user_tool_options =
ShellTypes.user_options = Trace.UserOptions =
ShellUtils.UserOptions
sharing type ShellTypes.Context = Trace.Context =
Incremental.Context = ShellUtils.Context
sharing type ShellTypes.ShellData = Inspector.ShellData =
SaveImage.ShellData = ShellUtils.ShellData
sharing type ShellUtils.preferences = ShellTypes.preferences =
Preferences.preferences
sharing type ShellUtils.user_context =
ShellTypes.user_context = UserContext.user_context
sharing type UserOptions.user_context_options =
UserContext.user_context_options
sharing type Preferences.user_preferences = ShellTypes.user_preferences
sharing type Io.Location = Incremental.InterMake.Compiler.Info.Location.T =
ModuleId.Location = ProjFile.location
sharing type ShellUtils.Info.options = ProjFile.error_info
sharing type Project.Project = Incremental.InterMake.Project
): SHELL_STRUCTURE =
struct
structure InterMake = Incremental.InterMake
structure Compiler = InterMake.Compiler
structure Debugger_Types = DebuggerUtilities.Debugger_Types
structure BasisTypes = BasisTypes
structure Datatypes = Types.Datatypes
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure Info = Compiler.Info
structure Options = UserOptions.Options
structure NewMap = Datatypes.NewMap
structure Profile = MLWorks.Profile
type ShellData = ShellTypes.ShellData
type Context = Incremental.Context
type IncrementalOptions = Incremental.options
val find_symbol = Symbol.find_symbol
val cast = MLWorks.Internal.Value.cast
exception MakeDynamic of string
fun make_dynamic (f : 'a -> 'b) =
let
val debug_info = InterMake.current_debug_information ()
val string = Trace.get_function_string f
val t = (cast f) : MLWorks.Internal.Value.T
in
case Debugger_Types.lookup_debug_info (debug_info,string) of
SOME (Debugger_Types.FUNINFO {ty,...}) =>
(t,DebuggerUtilities.close_type ty)
| _ => (print(string ^ "\n");
raise MakeDynamic string)
end
fun do_trace_error s = raise MLWorks.Internal.Trace.Trace s
fun trace_full_dynamic (shell_data,(name,f2,f3)) =
let
val valtys = (name,make_dynamic f2,
make_dynamic f3)
in
Trace.trace_full valtys
end
handle MakeDynamic s => do_trace_error ("No debug information for " ^ s)
fun print_dynamic (shell_data,dyn : MLWorks.Internal.Dynamic.dynamic) =
let
val print_options = ShellTypes.get_current_print_options shell_data
val context = ShellTypes.get_current_context shell_data
val debug_info = Incremental.debug_info context
val (value,ty) = cast dyn
in
ValuePrinter.stringify_value false (print_options,
value,
ty,
debug_info)
end
fun print_type (shell_data, tyrep : MLWorks.Internal.Dynamic.type_rep) =
let
val ty : Datatypes.Type = cast tyrep
val options = ShellTypes.get_current_options shell_data
in
Types.print_type options ty
end
fun make_exn_tag s = s ^ "[<Shell>]"
fun environment_error_exn label =
(ref (),make_exn_tag label)
fun make_environment_error_exn (exn : unit ref * string, s) : exn =
cast (exn, s)
fun env_error (exn, s) =
raise (make_environment_error_exn (exn,s))
fun make_shell_structure is_a_tty_image (shell_data_ref, initial_context) =
let
val error_info = Info.make_default_options ()
fun get_context () = ShellTypes.get_current_context (!shell_data_ref)
fun shell_exit_fn n =
let val (ShellTypes.SHELL_DATA{exit_fn,...}) = (!shell_data_ref)
in
exit_fn n
end
open Datatypes
fun get_runtime_type(longtycon as Ident.LONGTYCON(_, Ident.TYCON sy)) =
let
val Incremental.CONTEXT
{compiler_basis=Compiler.BASIS
{type_basis=BasisTypes.BASIS{5=env, ...}, ...}, ...} = initial_context
val TYSTR(tyfun, valenv) = Env.lookup_longtycon (longtycon,env)
in
CONSTYPE([], METATYNAME(ref tyfun, Symbol.symbol_name sy, 0,
ref(Types.equalityp tyfun), ref valenv, ref false))
end
fun schemify ty = UNBOUND_SCHEME (ty,NONE)
fun make_tyvar name = TYVAR (ref (0,NULLTYPE,NO_INSTANCE),
Ident.TYVAR (find_symbol name,
false,
false))
fun mk_longtycon (p,s) =
let
val sym = find_symbol s
val symlist = map find_symbol p
in
Ident.LONGTYCON (Ident.mkPath symlist,
Ident.TYCON sym)
end
fun mk_record l =
let
fun mk_record' (n,[],acc) = acc
| mk_record' (n,(lab,ty)::ls,acc) =
let
val newacc =
Types.add_to_rectype (Ident.LAB (find_symbol lab),
ty,
acc)
in
mk_record' (n+1, ls, newacc)
end
in
mk_record' (1,l,Types.empty_rectype)
end
fun make_tuple tylist =
let
val lab = ref 0
in
mk_record
(map
(fn x => (lab := (!lab +1);
(Int.toString (!lab),x)))
tylist)
end
fun make_pair (a,b) =
make_tuple [a,b]
fun make_triple (a,b,c) =
make_tuple [a,b,c]
val string_pair =
make_pair(Types.string_type,Types.string_type)
val dynamic2 =
make_tuple [Types.dynamic_type,
Types.dynamic_type]
val dynamic2_to_unit =
FUNTYPE (dynamic2,Types.empty_rectype)
val option_tyname =
Types.make_tyname (1, true, "option", NONE, 0)
val target_type_tyname =
Types.make_tyname (0, true, "targetType", NONE, 0)
val target_type = schemify(CONSTYPE([], target_type_tyname))
fun build_record_type list =
List.foldl
(fn ((label, typ), rectype) =>
Types.add_to_rectype(
Types.Datatypes.Ident.LAB(
Types.Datatypes.Ident.Symbol.find_symbol label),
typ,
rectype)) Types.empty_rectype
list;
val mode_details_type =
build_record_type
[("location", Types.string_type),
("generate_interruptable_code", Types.bool_type),
("generate_interceptable_code", Types.bool_type),
("generate_debug_info", Types.bool_type),
("generate_variable_debug_info", Types.bool_type),
("optimize_leaf_fns", Types.bool_type),
("optimize_tail_calls", Types.bool_type),
("optimize_self_tail_calls", Types.bool_type),
("mips_r4000", Types.bool_type),
("sparc_v7", Types.bool_type)];
val configuration_details_type =
build_record_type
[("files", CONSTYPE([Types.string_type], Types.list_tyname)),
("library", CONSTYPE([Types.string_type], Types.list_tyname))];
val location_details_type =
build_record_type
[("libraryPath", CONSTYPE([Types.string_type], Types.list_tyname)),
("objectsLoc", Types.string_type),
("binariesLoc", Types.string_type)];
val about_details_type =
build_record_type
[("description", Types.string_type),
("version", Types.string_type)];
val consumer_type =
schemify (FUNTYPE(make_pair(Types.string_type,
FUNTYPE(Types.string_type, Types.empty_rectype)),
Types.string_type))
val show_type =
schemify (FUNTYPE (Types.empty_rectype,CONSTYPE([Types.string_type], Types.list_tyname)))
val string_to_dynamic =
schemify (FUNTYPE (Types.string_type, Types.dynamic_type))
val dynamic_to_string =
schemify (FUNTYPE (Types.dynamic_type, Types.string_type))
val dynamic_to_type =
schemify (FUNTYPE (Types.dynamic_type, Types.typerep_type))
val dynamic_to_unit =
schemify (FUNTYPE (Types.dynamic_type, Types.empty_rectype))
val type_to_string =
schemify (FUNTYPE (Types.typerep_type, Types.string_type))
val unit_to_unit =
schemify (FUNTYPE (Types.empty_rectype, Types.empty_rectype))
val int_to_unit =
schemify (FUNTYPE (Types.int_type, Types.empty_rectype))
val bool_to_unit =
schemify (FUNTYPE (Types.bool_type, Types.empty_rectype))
val string_to_unit =
schemify (FUNTYPE (Types.string_type, Types.empty_rectype))
val unit_to_string =
schemify (FUNTYPE (Types.empty_rectype, Types.string_type))
val string_cross_string_to_unit =
schemify(FUNTYPE (make_pair(Types.string_type, Types.string_type),
Types.empty_rectype))
val string_cross_bool_to_unit =
schemify(FUNTYPE (make_pair(Types.string_type, Types.bool_type),
Types.empty_rectype))
val string_to_exn_type =
FUNTYPE (Types.string_type, Types.exn_type)
val string_to_exn = schemify string_to_exn_type
val type_pair_to_exn_type =
FUNTYPE
(make_pair (Types.typerep_type, Types.typerep_type), Types.exn_type)
val type_pair_to_exn = schemify type_pair_to_exn_type
val string_cross_string_list_to_unit =
schemify(FUNTYPE(make_pair(Types.string_type, CONSTYPE([Types.string_type], Types.list_tyname)),
Types.empty_rectype))
local
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
fun make_alpha_beta_to_type ty =
let
val type_instance =
FUNTYPE (FUNTYPE (alpha,beta), ty)
in
Scheme.make_scheme([alpha,beta], (type_instance, NONE))
end
in
val fun_to_bool = make_alpha_beta_to_type Types.bool_type
val fun_to_string = make_alpha_beta_to_type Types.string_type
val fun_to_unit = make_alpha_beta_to_type Types.empty_rectype
val fun_to_fun =
let
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
in
Scheme.make_scheme ([alpha,beta], (FUNTYPE (FUNTYPE (alpha,beta),FUNTYPE (alpha,beta)),NONE))
end
val trace_full_type =
let
val c = make_tyvar "'c"
val d = make_tyvar "'d"
val e = make_tyvar "'e"
val f = make_tyvar "'f"
in
Scheme.make_scheme
([c,d,e,f],
(FUNTYPE (make_tuple [Types.string_type,
FUNTYPE (c,d),
FUNTYPE (e,f)],
Types.empty_rectype),
NONE))
end
end
val string_list_to_unit =
schemify (FUNTYPE (CONSTYPE([Types.string_type], Types.list_tyname),
Types.empty_rectype))
val string_to_string_cross_string_list =
schemify (FUNTYPE (Types.string_type,
make_pair (Types.string_type,
CONSTYPE([Types.string_type], Types.list_tyname))))
val string_list_cross_string_list =
make_pair(CONSTYPE([Types.string_type], Types.list_tyname),
CONSTYPE([Types.string_type], Types.list_tyname))
val string_cross_string_cross_string_list =
make_triple(Types.string_type,
Types.string_type,
CONSTYPE([Types.string_type], Types.list_tyname)
)
val string_cross_target_type_to_unit =
schemify (FUNTYPE (make_pair(Types.string_type,
CONSTYPE([], target_type_tyname)),
Types.empty_rectype))
val string_to_target_type =
schemify (FUNTYPE (Types.string_type,
CONSTYPE([], target_type_tyname)))
val string_to_mode_details =
schemify (FUNTYPE (Types.string_type,
mode_details_type))
val string_to_configuration_details =
schemify (FUNTYPE (Types.string_type,
configuration_details_type))
val string_cross_mode_details_to_unit =
schemify (FUNTYPE (make_pair(Types.string_type,
mode_details_type),
Types.empty_rectype))
val string_cross_configuration_details_to_unit =
schemify (FUNTYPE (make_pair(Types.string_type,
configuration_details_type),
Types.empty_rectype))
val unit_to_location_details =
schemify (FUNTYPE (Types.empty_rectype, location_details_type))
val location_details_to_unit =
schemify (FUNTYPE (location_details_type, Types.empty_rectype))
val unit_to_about_details =
schemify (FUNTYPE (Types.empty_rectype, about_details_type))
val about_details_to_unit =
schemify (FUNTYPE (about_details_type, Types.empty_rectype))
val string_cross_string_cross_string_list_to_unit =
schemify (FUNTYPE (string_cross_string_cross_string_list,
Types.empty_rectype))
val string_to_string =
schemify (FUNTYPE (Types.string_type, Types.string_type))
val string_to_string_cross_string_cross_string_list =
schemify (FUNTYPE (Types.string_type,
string_cross_string_cross_string_list))
val unit_to_string_list =
schemify (FUNTYPE (Types.empty_rectype,
CONSTYPE([Types.string_type], Types.list_tyname)))
val unit_to_string_list_cross_string_list =
schemify (FUNTYPE (Types.empty_rectype,
string_list_cross_string_list))
val opt_switches_type =
CONSTYPE([make_pair(Types.string_type,
CONSTYPE([Types.bool_type],Types.ref_tyname))],
Types.list_tyname)
fun mk_valenv (vals,exns) =
let val valvalenv =
Lists.reducel
(fn (ve, (name,scheme)) =>
Valenv.add_to_ve
(Ident.VAR(find_symbol name),scheme,ve))
(empty_valenv, vals)
in
Lists.reducel
(fn (ve, (name, scheme)) =>
Valenv.add_to_ve
(Ident.EXCON(find_symbol name),scheme,ve))
(valvalenv, exns)
end
fun mk_tyenv l =
Lists.reducel
(fn (te, (name, tystr)) =>
Tyenv.add_to_te
(te,
Ident.TYCON(find_symbol name),
tystr))
(Tyenv.empty_tyenv, l)
fun mk_strenv l =
Lists.reducel
(fn (se, (name, str)) =>
Strenv.add_to_se(
Ident.STRID(find_symbol name),
str,
se
)
)
(Strenv.empty_strenv, l)
fun mk_mixed_structure(strs, tys, vals,exns) =
STR
(STRNAME (Types.make_stamp ()),
ref NONE,
ENV(mk_strenv strs,
mk_tyenv tys,
mk_valenv (vals,exns)))
fun mk_structure vals = mk_mixed_structure([], [], vals, [])
fun mk_exn_structure (vals,exns) = mk_mixed_structure([], [], vals, exns)
fun mk_option t = schemify (CONSTYPE([t], option_tyname))
val edit_exn_label = "EditError"
val eval_exn_label = "EvalError"
val path_exn_label = "PathError"
val inspect_exn_label = "InspectError"
val project_exn_label = "ProjectError"
val edit_exn = environment_error_exn edit_exn_label
val eval_exn = environment_error_exn eval_exn_label
val path_exn = environment_error_exn path_exn_label
val inspect_exn = environment_error_exn inspect_exn_label
val project_exn = environment_error_exn project_exn_label
fun do_inspect_error s = env_error (inspect_exn,s)
fun eval_string (shell_data,string) =
let
val context = ShellTypes.get_current_context shell_data
val options = ShellTypes.get_current_options shell_data
val error_info = Info.make_default_options ()
val result =
Info.with_report_fun
error_info
(fn _ => ())
ShellUtils.eval
(string,options,context)
in
(cast result) : MLWorks.Internal.Dynamic.dynamic
end
handle Info.Stop (e, _) =>
env_error (eval_exn, Info.string_error e)
fun shell_eval_fn s =
eval_string (!shell_data_ref,s)
fun use_fun s =
let
val ShellTypes.SHELL_DATA {get_user_context,
user_options,
user_preferences,
debugger,
...} = !shell_data_ref
fun output_fn s = print s
val error_info = Info.make_default_options ()
in
ShellUtils.use_file (!shell_data_ref, print, s)
end
fun use_string_fun s =
let
val ShellTypes.SHELL_DATA {get_user_context,
user_options,
user_preferences,
debugger,
...} = !shell_data_ref
fun output_fn s = print s
val error_info = Info.make_default_options ()
in
ShellUtils.use_string (!shell_data_ref, print, s)
end
fun shell_dyn_trace_full d = trace_full_dynamic (!shell_data_ref,d)
fun shell_dyn_print_val d = print_dynamic (!shell_data_ref,d)
fun shell_dyn_print_type t = print_type (!shell_data_ref,t)
fun add_inspect_method (f : 'a -> 'b) =
InspectorValues.add_inspect_method (make_dynamic f)
handle MakeDynamic s => do_inspect_error ("No debug information for " ^ s)
fun delete_inspect_method (f : 'a -> 'b) =
InspectorValues.delete_inspect_method (make_dynamic f)
handle MakeDynamic s => do_inspect_error ("No debug information for " ^ s)
fun delete_all_inspect_methods () =
InspectorValues.delete_all_inspect_methods ()
val inspect_it_fn : unit -> unit =
(fn () => Inspector.inspect_it (!shell_data_ref))
val inspect_dyn_fn =
(fn (d: MLWorks.Internal.Dynamic.dynamic) =>
Inspector.inspect_value (cast d,!shell_data_ref))
val value_printer_structure = mk_structure
[("showFnDetails", mk_option Types.bool_type),
("showExnDetails", mk_option Types.bool_type),
("floatPrecision", mk_option Types.int_type),
("maximumSeqSize", mk_option Types.int_type),
("maximumStringSize", mk_option Types.int_type),
("maximumRefDepth", mk_option Types.int_type),
("maximumStrDepth", mk_option Types.int_type),
("maximumSigDepth", mk_option Types.int_type),
("maximumDepth", mk_option Types.int_type)
]
type internal_exn_rep = unit ref * string
type 'a option_rep = (unit -> 'a) * ('a -> unit)
val set_option_type =
let
val aty = make_tyvar "'a"
val aty_option = CONSTYPE([aty], option_tyname)
in
Scheme.make_scheme ([aty],
(FUNTYPE(make_pair (aty_option, aty),
Types.empty_rectype),NONE))
end
fun set_option ((_, setter), v) = setter v;
val get_option_type =
let
val aty = make_tyvar "'a"
val aty_option = CONSTYPE([aty], option_tyname)
in
Scheme.make_scheme ([aty], (FUNTYPE(aty_option, aty),NONE))
end
fun get_option (getter, _) = getter ();
fun get_tool_option_fun f () =
let val UserOptions.USER_TOOL_OPTIONS (user_options,_) =
ShellTypes.get_user_options (!shell_data_ref)
in !(f user_options)
end
fun set_tool_option_fun f v =
let val UserOptions.USER_TOOL_OPTIONS (user_options,ref update_fns) =
ShellTypes.get_user_options (!shell_data_ref)
in
(f user_options) := v;
app (fn f => f ()) update_fns
end
fun make_tool_option_rep f =
(get_tool_option_fun f, set_tool_option_fun f)
fun get_context_option_fun f () =
let val UserOptions.USER_CONTEXT_OPTIONS (user_options, _) =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in !(f user_options)
end
fun set_context_option_fun f v =
let
val UserOptions.USER_CONTEXT_OPTIONS
(user_options, ref update_fns) =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
(f user_options) := v;
app (fn f => f ()) update_fns
end
fun make_context_option_rep f =
(get_context_option_fun f, set_context_option_fun f)
fun make_oldDefinition_option_rep f =
(get_context_option_fun f,
(fn f => fn v => (Types.real_tyname_equality_attribute := v;
set_context_option_fun f v)) f)
fun update_user_options () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
UserOptions.update_user_context_options user_context_options
end
fun get_preference_fun f () =
let val Preferences.USER_PREFERENCES (user_preferences, _) =
ShellTypes.get_user_preferences (!shell_data_ref)
in !(f user_preferences)
end
fun set_preference_fun f v =
let
val Preferences.USER_PREFERENCES
(user_preferences, ref update_fns) =
ShellTypes.get_user_preferences (!shell_data_ref)
in
(f user_preferences) := v;
app (fn f => f ()) update_fns
end
fun make_preference_rep f =
(get_preference_fun f, set_preference_fun f)
val value_printer_record =
cast
{maximumDepth =
(make_tool_option_rep #maximum_depth) : int option_rep,
maximumRefDepth =
(make_tool_option_rep #maximum_ref_depth) : int option_rep,
maximumStrDepth =
(make_tool_option_rep #maximum_str_depth) : int option_rep,
maximumSigDepth =
(make_tool_option_rep #maximum_sig_depth) : int option_rep,
maximumSeqSize =
(make_tool_option_rep #maximum_seq_size) : int option_rep,
maximumStringSize =
(make_tool_option_rep #maximum_string_size) : int option_rep,
floatPrecision =
(make_tool_option_rep #float_precision) : int option_rep,
showFnDetails =
(make_tool_option_rep #show_fn_details) : bool option_rep,
showExnDetails =
(make_tool_option_rep #show_exn_details) : bool option_rep
}
val internals_structure = mk_structure
[("showAbsyn", mk_option Types.bool_type),
("showLambda", mk_option Types.bool_type),
("showOptLambda", mk_option Types.bool_type),
("showEnviron", mk_option Types.bool_type),
("showMir", mk_option Types.bool_type),
("showOptMir", mk_option Types.bool_type),
("showMach", mk_option Types.bool_type)]
val internals_record =
cast
{showAbsyn = (make_tool_option_rep #show_absyn) : bool option_rep,
showLambda = (make_tool_option_rep #show_lambda) : bool option_rep,
showOptLambda =
(make_tool_option_rep #show_opt_lambda) : bool option_rep,
showEnviron =
(make_tool_option_rep #show_environ) : bool option_rep,
showMir = (make_tool_option_rep #show_mir) : bool option_rep,
showOptMir =
(make_tool_option_rep #show_opt_mir) : bool option_rep,
showMach = (make_tool_option_rep #show_mach) : bool option_rep
}
val preferences_structure = mk_structure
[
("oneWayEditorName", mk_option Types.string_type),
("twoWayEditorName", mk_option Types.string_type),
("editor", mk_option Types.string_type),
("externalEditorCommand", mk_option Types.string_type),
("maximumHistorySize", mk_option Types.int_type),
("maximumErrors", mk_option Types.int_type),
("useCompletionMenu", mk_option Types.bool_type),
("useDebugger", mk_option Types.bool_type),
("useErrorBrowser", mk_option Types.bool_type),
("useRelativePathname", mk_option Types.bool_type),
("useWindowDebugger", mk_option Types.bool_type)]
val preferences_record =
cast
{editor = (make_preference_rep #editor) : string option_rep,
externalEditorCommand =
(make_preference_rep #externalEditorCommand) : string option_rep,
oneWayEditorName =
(make_preference_rep #oneWayEditorName) : string option_rep,
twoWayEditorName =
(make_preference_rep #twoWayEditorName) : string option_rep,
maximumHistorySize =
(make_preference_rep #history_length) : int option_rep,
maximumErrors =
(make_preference_rep #max_num_errors) : int option_rep,
useCompletionMenu =
(make_preference_rep #completion_menu) : bool option_rep,
useDebugger =
(make_preference_rep #use_debugger) : bool option_rep,
useErrorBrowser =
(make_preference_rep #use_error_browser) : bool option_rep,
useRelativePathname =
(make_preference_rep #use_relative_pathname) : bool option_rep,
useWindowDebugger =
(make_preference_rep #window_debugger) : bool option_rep
}
val compiler_structure = mk_structure
[("generateTraceProfileCode", mk_option Types.bool_type),
("generateDebugInfo", mk_option Types.bool_type),
("generateLocalFunctions", mk_option Types.bool_type),
("generateVariableDebugInfo", mk_option Types.bool_type),
("interruptTightLoops", mk_option Types.bool_type),
("mipsR4000andLater", mk_option Types.bool_type),
("optimizeHandlers", mk_option Types.bool_type),
("optimizeLeafFns", mk_option Types.bool_type),
("optimizeTailCalls", mk_option Types.bool_type),
("optimizeSelfTailCalls", mk_option Types.bool_type),
("printCompilerMessages", mk_option Types.bool_type),
("sparcV7", mk_option Types.bool_type)
]
val compiler_record =
cast
{generateTraceProfileCode =
(make_context_option_rep #generate_interceptable_code) :
bool option_rep,
generateDebugInfo =
(make_context_option_rep #generate_debug_info) : bool option_rep,
generateLocalFunctions =
(make_context_option_rep #local_functions) : bool option_rep,
generateVariableDebugInfo =
(make_context_option_rep #generate_variable_debug_info) :
bool option_rep,
interruptTightLoops =
(make_context_option_rep #generate_interruptable_code) :
bool option_rep,
mipsR4000andLater =
(make_context_option_rep #mips_r4000) :
bool option_rep,
optimizeHandlers = (make_context_option_rep #optimize_handlers) :
bool option_rep,
optimizeLeafFns = (make_context_option_rep #optimize_leaf_fns) :
bool option_rep,
optimizeTailCalls = (make_context_option_rep #optimize_tail_calls):
bool option_rep,
optimizeSelfTailCalls = (make_context_option_rep #optimize_self_tail_calls):
bool option_rep,
printCompilerMessages = (make_context_option_rep #print_messages):
bool option_rep,
sparcV7 = (make_context_option_rep #sparc_v7) : bool option_rep
}
val debugger_structure = mk_structure
[("hideCFrames", mk_option Types.bool_type),
("hideSetupFrames", mk_option Types.bool_type),
("hideAnonymousFrames", mk_option Types.bool_type),
("hideHandlerFrames", mk_option Types.bool_type),
("hideDeliveredFrames", mk_option Types.bool_type),
("hideDuplicateFrames", mk_option Types.bool_type)]
val debugger_record =
let
fun mkDebuggerOption flag =
(fn ()=> !flag, fn v => (flag:=v))
in
cast
{hideCFrames =
(mkDebuggerOption StackFrame.hide_c_frames)
: bool option_rep,
hideSetupFrames =
(mkDebuggerOption StackFrame.hide_setup_frames)
: bool option_rep,
hideAnonymousFrames =
(mkDebuggerOption StackFrame.hide_anonymous_frames)
: bool option_rep,
hideHandlerFrames =
(mkDebuggerOption StackFrame.hide_handler_frames)
: bool option_rep,
hideDeliveredFrames =
(mkDebuggerOption StackFrame.hide_delivered_frames)
: bool option_rep,
hideDuplicateFrames =
(mkDebuggerOption StackFrame.hide_duplicate_frames)
: bool option_rep}
end
val language_structure = mk_structure
[("oldDefinition", mk_option Types.bool_type),
("opOptional", mk_option Types.bool_type),
("limitedOpen", mk_option Types.bool_type),
("weakTyvars", mk_option Types.bool_type),
("fixityInSignatures", mk_option Types.bool_type),
("fixityInOpen", mk_option Types.bool_type),
("abstractions", mk_option Types.bool_type),
("requireReservedWord", mk_option Types.bool_type),
("typeDynamic", mk_option Types.bool_type)
]
val language_record =
cast
{oldDefinition =
(make_oldDefinition_option_rep #old_definition) : bool option_rep,
opOptional =
(make_context_option_rep #nj_op_in_datatype) : bool option_rep,
limitedOpen =
(make_context_option_rep #nj_signatures) : bool option_rep,
weakTyvars =
(make_context_option_rep #weak_type_vars) : bool option_rep,
fixityInSignatures =
(make_context_option_rep #fixity_specs) : bool option_rep,
fixityInOpen =
(make_context_option_rep #open_fixity) : bool option_rep,
abstractions =
(make_context_option_rep #abstractions) : bool option_rep,
requireReservedWord =
(make_context_option_rep #require_keyword) : bool option_rep,
typeDynamic =
(make_context_option_rep #type_dynamic) : bool option_rep
}
val mode_option_structure = mk_structure
[("compatibility", unit_to_unit),
("sml'97", unit_to_unit),
("sml'90", unit_to_unit),
("optimizing", unit_to_unit),
("quick_compile", unit_to_unit),
("debugging", unit_to_unit)]
fun select_compatibility () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
UserOptions.select_compatibility user_context_options;
UserOptions.update_user_context_options user_context_options
end
fun select_sml'97 () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
val _ = MLWorks.Internal.Dynamic.generalises_ref:=
Scheme.SML96_dynamic_generalises
val _ = Types.real_tyname_equality_attribute := false
in
UserOptions.select_sml'97 user_context_options;
UserOptions.update_user_context_options user_context_options
end
fun select_sml'90 () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
val _ = MLWorks.Internal.Dynamic.generalises_ref:=
Scheme.SML90_dynamic_generalises
val _ = Types.real_tyname_equality_attribute := true
in
UserOptions.select_sml'90 user_context_options;
UserOptions.update_user_context_options user_context_options
end
fun select_optimizing () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
UserOptions.select_optimizing user_context_options;
UserOptions.update_user_context_options user_context_options
end
fun select_debugging () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
UserOptions.select_debugging user_context_options;
UserOptions.update_user_context_options user_context_options
end
fun select_quick_compile () =
let
val user_context_options =
UserContext.get_user_options
(ShellTypes.get_user_context (!shell_data_ref))
in
UserOptions.select_quick_compile user_context_options;
UserOptions.update_user_context_options user_context_options
end
val mode_option_record =
cast
{compatibility = select_compatibility : unit -> unit,
sml'97 = select_sml'97 : unit -> unit,
sml'90 = select_sml'90 : unit -> unit,
quick_compile = select_quick_compile : unit -> unit,
optimizing = select_optimizing : unit -> unit,
debugging = select_debugging : unit -> unit}
val inspector_structure =
mk_exn_structure
([("addInspectMethod",fun_to_unit),
("deleteInspectMethod",fun_to_unit),
("deleteAllInspectMethods",unit_to_unit),
("inspectIt",unit_to_unit)],
[(inspect_exn_label,string_to_exn)])
val inspector_record =
cast
{a_InspectError = inspect_exn : internal_exn_rep,
a_inspectIt = inspect_it_fn : unit -> unit,
a_addInspectMethod = add_inspect_method : ('a -> 'b) -> unit,
a_deleteInspectMethod = delete_inspect_method : ('a -> 'b) -> unit,
a_deleteAllInspectMethods =
delete_all_inspect_methods : unit -> unit}
val dynamic_structure = mk_mixed_structure
([],
[("dynamic",
TYSTR (ETA_TYFUN Types.dynamic_tyname, empty_valenv)),
("type_rep",
TYSTR (ETA_TYFUN Types.typerep_tyname, empty_valenv))],
[("eval", string_to_dynamic),
("getType", dynamic_to_type),
("inspect",dynamic_to_unit),
("printValue",dynamic_to_string),
("printType",type_to_string)],
[(eval_exn_label, string_to_exn),
("Coerce", type_pair_to_exn)])
val coerce_exn =
let
val the_exn = MLWorks.Internal.Dynamic.Coerce (cast (0, 0))
in
case cast the_exn
of (internal_rep, _) => internal_rep: internal_exn_rep
end
val dynamic_record =
cast
{a_eval = shell_eval_fn : string -> MLWorks.Internal.Dynamic.dynamic,
a_EvalError = eval_exn : internal_exn_rep,
a_Coerce = coerce_exn: internal_exn_rep,
a_getType = MLWorks.Internal.Value.cast (fn (a,b) => b),
a_inspect =
inspect_dyn_fn : MLWorks.Internal.Dynamic.dynamic -> unit,
a_printValue =
shell_dyn_print_val : MLWorks.Internal.Dynamic.dynamic -> string,
a_printType =
shell_dyn_print_type : MLWorks.Internal.Dynamic.type_rep -> string}
val trace_structure = mk_structure
[("breakpoint",string_to_unit),
("trace",string_to_unit),
("unbreakpoint",string_to_unit),
("untrace",string_to_unit),
("traceFull",trace_full_type),
("untraceAll",unit_to_unit),
("unbreakAll",unit_to_unit)]
fun break name = Trace.break{name=name, hits=0, max=1}
val trace_record =
cast
{a_breakpoint = break : string-> unit,
a_trace = Trace.trace : string-> unit,
a_unbreakpoint = Trace.unbreak : string-> unit,
a_untrace = Trace.untrace : string-> unit,
a_traceFull =
shell_dyn_trace_full : (string * ('c -> 'd) * ('e -> 'f) -> unit),
a_untraceAll = Trace.untrace_all : unit -> unit,
a_unbreakAll = Trace.unbreak_all : unit -> unit}
type mode_details =
{location: string,
generate_interruptable_code: bool,
generate_interceptable_code: bool,
generate_debug_info: bool,
generate_variable_debug_info: bool,
optimize_leaf_fns: bool,
optimize_tail_calls: bool,
optimize_self_tail_calls: bool,
mips_r4000: bool,
sparc_v7: bool}
type configuration_details =
{files: string list,
library: string list}
type location_details =
{libraryPath: string list,
objectsLoc: string,
binariesLoc: string}
type about_details =
{description: string,
version: string}
val project_structure = mk_mixed_structure
([],
[
("mode_details",
TYSTR (TYFUN(mode_details_type,0), empty_valenv)),
("configuration_details",
TYSTR (TYFUN(configuration_details_type,0), empty_valenv)),
("location_details",
TYSTR (TYFUN(location_details_type,0), empty_valenv)),
("about_details",
TYSTR (TYFUN(about_details_type,0), empty_valenv))],
[
("newProject", string_to_unit),
("openProject", string_to_unit),
("saveProject", unit_to_unit),
("saveProjectAs", string_to_unit),
("closeProject", unit_to_unit),
("setFiles", string_list_to_unit),
("showFiles", unit_to_string_list),
("setSubprojects", string_list_to_unit),
("showSubprojects", unit_to_string_list),
("setLocations", location_details_to_unit),
("showLocations", unit_to_location_details),
("setAboutInfo", about_details_to_unit),
("showAboutInfo", unit_to_about_details),
("showFilename", unit_to_string),
("setConfiguration", string_to_unit),
("removeConfiguration", string_to_unit),
("setConfigurationDetails", string_cross_configuration_details_to_unit),
("showAllConfigurations", unit_to_string_list),
("showCurrentConfiguration", unit_to_string),
("showConfigurationDetails", string_to_configuration_details),
("setMode", string_to_unit),
("removeMode", string_to_unit),
("setModeDetails", string_cross_mode_details_to_unit),
("showAllModes", unit_to_string_list),
("showCurrentMode", unit_to_string),
("showModeDetails", string_to_mode_details),
("setTargets", string_list_to_unit),
("setTargetDetails", string_to_unit),
("removeTarget", string_to_unit),
("showAllTargets", unit_to_string_list),
("showCurrentTargets", unit_to_string_list),
("compile", string_to_unit),
("showCompile", string_to_unit),
("forceCompile", string_to_unit),
("compileAll", unit_to_unit),
("showCompileAll", unit_to_unit),
("forceCompileAll", unit_to_unit),
("load", string_to_unit),
("showLoad", string_to_unit),
("forceLoad", string_to_unit),
("loadAll", unit_to_unit),
("showLoadAll", unit_to_unit),
("forceLoadAll", unit_to_unit),
("makeExe", string_cross_string_list_to_unit),
("delete", string_to_unit),
("readDependencies", string_to_unit)],
[(project_exn_label,string_to_exn)])
fun refresh_project () =
Incremental.set_project(Incremental.get_project())
fun get_location () =
Info.Location.FILE (ShellTypes.get_current_toplevel_name ())
fun compile_all () =
(ShellUtils.compile_targets
(get_location (),
ShellTypes.get_current_options (!shell_data_ref))
error_info)
fun show_compile_all () =
(ShellUtils.show_compile_targets
(get_location (), print)
error_info)
fun compile filename =
(ShellUtils.compile_file
(get_location (),
ShellTypes.get_current_options (!shell_data_ref))
error_info
filename)
fun show_compile filename =
(ShellUtils.show_compile_file
(get_location (), print)
error_info
filename)
fun force_compile filename =
(ShellUtils.force_compile
(get_location (),
ShellTypes.get_current_options (!shell_data_ref))
error_info
filename)
fun force_compile_all () =
(ShellUtils.force_compile_all
(get_location (),
ShellTypes.get_current_options (!shell_data_ref))
error_info)
fun delete_from_project s =
ShellUtils.delete_from_project (s, get_location ())
fun load filename =
(ShellUtils.load_file
(ShellTypes.get_user_context (!shell_data_ref),
get_location (),
ShellTypes.get_current_options (!shell_data_ref),
ShellTypes.get_current_preferences (!shell_data_ref),
print)
error_info
filename)
fun show_load filename =
(ShellUtils.show_load_file
(get_location (), print)
error_info
filename)
fun load_targets () =
(ShellUtils.load_targets
(ShellTypes.get_user_context (!shell_data_ref),
get_location (),
ShellTypes.get_current_options (!shell_data_ref),
ShellTypes.get_current_preferences (!shell_data_ref),
print)
error_info)
fun show_load_targets () =
(ShellUtils.show_load_targets
(get_location (), print)
error_info)
fun force_load filename =
(let
val module_id = ModuleId.from_string (filename, get_location ())
in
Incremental.delete_module error_info module_id;
load filename
end)
fun read_dependencies filename =
let
val toplevel_name = ShellTypes.get_current_toplevel_name ()
val module_id =
ModuleId.from_string
(filename, Info.Location.FILE toplevel_name)
in
Incremental.read_dependencies
toplevel_name
error_info
module_id
end
fun show_mode_details mode =
let val (modes, details, _) = ProjFile.getModes()
in if List.exists(fn m => m = mode) modes
then let val {name, location, generate_interruptable_code,
generate_interceptable_code, generate_debug_info,
generate_variable_debug_info, optimize_leaf_fns,
optimize_tail_calls, optimize_self_tail_calls,
mips_r4000, sparc_v7} = ProjFile.getModeDetails(mode, details)
in {location = !location,
generate_interruptable_code = !generate_interruptable_code,
generate_interceptable_code = !generate_interceptable_code,
generate_debug_info = !generate_debug_info,
generate_variable_debug_info = !generate_variable_debug_info,
optimize_leaf_fns = !optimize_leaf_fns,
optimize_tail_calls = !optimize_tail_calls,
optimize_self_tail_calls = !optimize_self_tail_calls,
mips_r4000 = !mips_r4000,
sparc_v7 = !sparc_v7}
end
else env_error (project_exn, "There is no mode called " ^ mode)
end
fun set_mode_details (mode,
{location, generate_interruptable_code,
generate_interceptable_code, generate_debug_info,
generate_variable_debug_info, optimize_leaf_fns,
optimize_tail_calls, optimize_self_tail_calls,
mips_r4000, sparc_v7}) =
let val (modes, details, _) = ProjFile.getModes()
val (_, modes') = List.partition (fn m => m = mode) modes
val (_, details') = List.partition (fn r => #name r = mode) details
val details =
{name = mode,
location = ref location,
generate_interruptable_code = ref generate_interruptable_code,
generate_interceptable_code = ref generate_interceptable_code,
generate_debug_info = ref generate_debug_info,
generate_variable_debug_info = ref generate_variable_debug_info,
optimize_leaf_fns = ref optimize_leaf_fns,
optimize_tail_calls = ref optimize_tail_calls,
optimize_self_tail_calls = ref optimize_self_tail_calls,
mips_r4000 = ref mips_r4000,
sparc_v7 = ref sparc_v7}
in ProjFile.setModes (mode::modes', details::details');
refresh_project()
end
fun remove_mode mode =
let val (modes, details, current_mode) = ProjFile.getModes()
val (_, modes') = List.partition (fn m => m = mode) modes
val (mode_l, details') = List.partition (fn r => #name r = mode) details
in
if mode_l = []
then env_error (project_exn, "Cannot remove " ^ mode ^ " as it does not exist")
else if mode = getOpt(current_mode,"")
then env_error (project_exn, "Cannot remove " ^ mode ^ " as it is the current mode")
else (ProjFile.setModes (modes', details'); refresh_project())
end
fun show_config_details config =
let val (configs, details, _) = ProjFile.getConfigurations()
in if List.exists(fn c => c = config) configs
then let val {name, files, library} =
ProjFile.getConfigDetails(config, details)
in {files = files, library = library} end
else env_error (project_exn, "There is no configuration called " ^ config)
end
fun duplicate_mod_ids [] ids = NONE
| duplicate_mod_ids (a::rest) ids =
let
val filen = OS.Path.file a
val id = ModuleId.from_host (filen, get_location())
in
if (List.exists (fn id' => ModuleId.eq(id,id')) ids) then (SOME filen)
else duplicate_mod_ids rest (id :: ids)
end
fun rem_old_config_units new_config =
let
val (configs, c_details, old_config) = ProjFile.getConfigurations()
fun get_c_files config =
(#files (ProjFile.getConfigDetails (config, c_details)))
handle ProjFile.NoConfigDetailsFound c => []
fun get_c_modules config =
map (fn f => ModuleId.from_host(OS.Path.file f,
Info.Location.FILE "Project Properties"))
(get_c_files config)
fun remove_unit (mod_id, proj) = Project.delete(proj, mod_id, false)
val init_proj = Incremental.get_project()
val set = Incremental.set_project
in
case (old_config, new_config) of
(NONE, _) => ()
| (SOME old, NONE) =>
set (foldl remove_unit init_proj (get_c_modules old))
| (SOME old, SOME new) =>
if (old = new) then ()
else
set (foldl remove_unit init_proj (get_c_modules old))
end
fun set_configuration config =
let val (configs, configDetails, curConfig) = ProjFile.getConfigurations()
in
if (List.exists (fn c => c = config) configs) then
let
val common_files = ProjFile.getFiles()
val config_files = #files (ProjFile.getConfigDetails(config, configDetails))
in
case (duplicate_mod_ids (common_files @ config_files) []) of
NONE =>
(rem_old_config_units (SOME config);
ProjFile.setCurrentConfiguration
(error_info, get_location ())
(SOME config);
refresh_project())
| SOME f =>
env_error (project_exn,
"Cannot change to configuration; filename clash: " ^ f)
end
else
env_error (project_exn,
"The configuration " ^ config ^ " is undefined")
end
fun set_config_details (config, {files, library}) =
let
val (configs, details, curConfig) = ProjFile.getConfigurations()
val (_, configs') = List.partition (fn c => c = config) configs
val (_, details') = List.partition (fn r => #name r = config) details
val details = {name = config, files = files, library=library}
val com_files = ProjFile.getFiles()
in
case (duplicate_mod_ids (com_files @ files) []) of
NONE =>
(ProjFile.setConfigurations (config::configs', details::details');
refresh_project())
| SOME f =>
env_error (project_exn,
"No duplicate filenames allowed. <" ^ f ^ "> already exists");
case curConfig of
NONE => set_configuration config
| SOME c => if (List.exists (fn c' => c=c') configs') then ()
else set_configuration config
end
fun remove_config config =
let val (configs, details, current_config) = ProjFile.getConfigurations()
val (_, configs') = List.partition (fn c => c = config) configs
val (config_l, details') = List.partition (fn r => #name r = config) details
in
if config_l = []
then env_error (project_exn, "Cannot remove " ^ config ^ " as it does not exist")
else ();
if config = getOpt(current_config,"")
then
(case configs' of
[] =>
(rem_old_config_units NONE;
ProjFile.setCurrentConfiguration (error_info, get_location ()) NONE)
| (c::rest) => set_configuration c)
else ();
ProjFile.setConfigurations (configs', details');
refresh_project()
end
fun show_location_details () =
let val (libraryPath, objectsLoc, binariesLoc) = ProjFile.getLocations()
in {libraryPath=libraryPath, objectsLoc=objectsLoc, binariesLoc=binariesLoc}
end
fun set_location_details {libraryPath, objectsLoc, binariesLoc} =
( ProjFile.setLocations(libraryPath, objectsLoc, binariesLoc);
refresh_project() )
fun show_about_details () =
let val (description, version) = ProjFile.getAboutInfo()
in {description=description, version=version}
end
fun set_about_details {description, version} =
( ProjFile.setAboutInfo(description, version);
refresh_project() )
val project_record =
cast
{
a_ProjectError = project_exn : internal_exn_rep,
a_openProject =
(fn file => (ProjFile.open_proj file;
Incremental.reset_project();
refresh_project())
handle _ => env_error (project_exn, "Unable to open project file " ^ file)
): string -> unit,
a_newProject =
(fn dir =>
let val abs_dir = OS.Path.mkAbsolute{path=dir, relativeTo=OS.FileSys.getDir()}
in if (OS.FileSys.isDir abs_dir handle OS.SysErr _ => false)
then
(ProjFile.new_proj dir;
Incremental.reset_project();
ProjFile.setInitialModes();
refresh_project())
else env_error (project_exn,
abs_dir ^ " is not a directory")
end) : string -> unit,
a_saveProject =
(fn () =>
case ProjFile.getProjectName() of
SOME "" =>
env_error (project_exn, "New project has no name")
| SOME file =>
( ProjFile.save_proj file
handle _ => env_error (project_exn,
"Unable to save project file to " ^ file) )
| NONE =>
env_error (project_exn, "There is no current project to save")
): unit -> unit,
a_saveProjectAs =
(fn file =>
case ProjFile.getProjectName() of
SOME _ =>
( (ProjFile.save_proj file; refresh_project())
handle _ => env_error (project_exn,
"Unable to save project file to " ^ file) )
| NONE =>
env_error (project_exn, "There is no current project to save")
): string -> unit,
a_closeProject =
(fn () => (ProjFile.close_proj(); refresh_project())) : unit -> unit,
a_setFiles =
(fn files =>
let
val (configs, c_details, curConfig) = ProjFile.getConfigurations()
val config_files =
case curConfig of
NONE => []
| SOME c => #files (ProjFile.getConfigDetails(c, c_details))
in
case (duplicate_mod_ids (files @ config_files) []) of
NONE => (ProjFile.setFiles files; refresh_project())
| SOME f =>
env_error (project_exn,
"No duplicate filenames allowed. <" ^ f ^ "> already exists")
end
): string list -> unit,
a_showFiles =
(fn () => ProjFile.getFiles ()): unit -> string list,
a_setSubprojects =
(fn subs => (ProjFile.setSubprojects subs; refresh_project())
handle _ => env_error (project_exn, "Unable to open subprojects")
): string list -> unit,
a_showSubprojects =
(fn () => ProjFile.getSubprojects ()): unit -> string list,
a_showLocations =
show_location_details : unit -> location_details,
a_setLocations =
set_location_details : location_details -> unit,
a_showAboutInfo =
show_about_details : unit -> about_details,
a_setAboutInfo =
set_about_details : about_details -> unit,
a_showFilename =
(fn () => getOpt(ProjFile.getProjectName(), "")) : unit -> string,
a_setConfiguration =
set_configuration : string -> unit,
a_removeConfiguration =
remove_config : string -> unit,
a_setTargets =
(fn targets =>
let val (_, _, details) = ProjFile.getTargets()
in app (fn t => if List.exists(fn (t',_) => t = t') details
then ()
else env_error (project_exn,
"The target " ^ t ^ " is undefined"))
targets;
ProjFile.setCurrentTargets
(error_info, get_location ())
targets;
refresh_project()
end
): string list -> unit,
a_setTargetDetails =
(fn target =>
let val c_target = OS.Path.mkCanonical target
val (enabled, disabled, details) = ProjFile.getTargets()
val t_file = OS.Path.file c_target
val (target1, enabled') = List.partition (fn t => t = t_file) enabled
val (target2, disabled') = List.partition (fn t => t = t_file) disabled
val details' = List.filter (fn (t,_) => t <> t_file) details
in
(ProjFile.setTargets(t_file::enabled', disabled',
(t_file,ProjFile.OBJECT_FILE)::details');
refresh_project())
handle _ => env_error (project_exn, "Unable to set target details for " ^ target)
end): string -> unit,
a_removeTarget =
(fn target =>
(let val (enabled, disabled, details) = ProjFile.getTargets()
val (target1, enabled') = List.partition (fn t => t = target) enabled
val (target2, disabled') = List.partition (fn t => t = target) disabled
val details' = List.filter (fn (t,_) => t <> target) details
in if null target1
then if null target2
then env_error (project_exn, "There is no target called " ^ target)
else ProjFile.setTargets(enabled, disabled', details')
else ProjFile.setTargets(enabled', disabled, details')
end; refresh_project())): string -> unit,
a_showAllTargets =
(fn () =>
let val (enabled, disabled, _) = ProjFile.getTargets()
in enabled @ disabled end): unit -> string list,
a_showCurrentTargets =
(fn () =>
let val (enabled, _, _) = ProjFile.getTargets()
in enabled end): unit -> string list,
a_setMode =
(fn mode =>
let val (modes, _, _) = ProjFile.getModes()
in if List.exists(fn m => m = mode) modes
then (ProjFile.setCurrentMode
(error_info, get_location ())
mode; refresh_project())
else
env_error (project_exn,
"The mode " ^ mode ^ " is undefined")
end
): string -> unit,
a_removeMode =
remove_mode : string -> unit,
a_showAllModes =
(fn () =>
let val (modes, details, _) = ProjFile.getModes() in modes end)
: unit -> string list,
a_showCurrentMode =
(fn () =>
case ProjFile.getModes() of
(_, _, SOME s) => s
| _ => env_error (project_exn, "There is no current mode"))
: unit -> string,
a_showModeDetails =
show_mode_details : string -> mode_details,
a_setModeDetails =
set_mode_details : string * mode_details -> unit,
a_showAllConfigurations =
(fn () =>
let val (configs, _, _) = ProjFile.getConfigurations() in configs end)
: unit -> string list,
a_showCurrentConfiguration =
(fn () =>
case ProjFile.getConfigurations() of
(_, _, SOME s) => s
| _ => env_error (project_exn, "There is no current configuration"))
: unit -> string,
a_showConfigurationDetails =
show_config_details : string -> configuration_details,
a_setConfigurationDetails =
set_config_details : string * configuration_details -> unit,
a_compile = compile: string -> unit,
a_showCompile = show_compile: string -> unit,
a_forceCompile = force_compile: string -> unit,
a_compileAll = compile_all: unit -> unit,
a_showCompileAll = show_compile_all: unit -> unit,
a_forceCompileAll = force_compile_all: unit -> unit,
a_load = load: string -> unit,
a_showLoad = show_load: string -> unit,
a_forceLoad = force_load: string -> unit,
a_loadAll = load_targets: unit -> unit,
a_showLoadAll = show_load_targets: unit -> unit,
a_forceLoadAll =
(fn () => (Incremental.delete_all_modules true;
load_targets ())): unit -> unit,
a_makeExe =
(fn (target, libs) =>
ShellUtils.make_exe_from_project(get_location(), error_info, target, libs) handle
exn as OS.SysErr(s, _) => (print(s ^ "\n"); raise exn)) : string * string list -> unit,
a_delete = delete_from_project: string -> unit,
a_readDependencies = read_dependencies: string -> unit}
val path_structure = mk_exn_structure
([("setSourcePath", string_list_to_unit),
("sourcePath", unit_to_string_list),
("setPervasive", string_to_unit),
("pervasive", unit_to_string)],
[(path_exn_label,string_to_exn)])
fun set_source_path l =
Io.set_source_path
(map
(fn x =>
(OS.FileSys.fullPath(Getenv.expand_home_dir x))
handle Getenv.BadHomeName s => env_error (path_exn, s)
| OS.SysErr(str, err) =>
let
val str = case err of
NONE => str
| SOME err => OS.errorMsg err
in
env_error(path_exn, str ^ ": " ^ x)
end)
l)
fun get_source_path () = Io.get_source_path ()
fun set_pervasive_dir s =
Io.set_pervasive_dir
(OS.FileSys.fullPath (Getenv.expand_home_dir s),
Info.Location.FILE"<Shell>")
handle
Getenv.BadHomeName s => env_error (path_exn, s)
| OS.SysErr(str, err) =>
let
val str = case err of
NONE => str
| SOME err => OS.errorMsg err
in
env_error(path_exn, str ^ ": " ^ s)
end
fun get_pervasive_dir () =
Io.get_pervasive_dir ()
handle Io.NotSet s => env_error (path_exn, s)
val path_record =
cast
{a_setSourcePath = set_source_path : string list -> unit,
a_sourcePath = get_source_path : unit -> string list,
a_setPervasive = set_pervasive_dir : string -> unit,
a_pervasive = get_pervasive_dir : unit -> string,
a_Path = path_exn : internal_exn_rep}
val custom_editor_structure =
mk_structure
([("addCommand", string_cross_string_to_unit),
("addConnectDialog", string_cross_string_cross_string_list_to_unit),
("removeCommand", string_to_string),
("removeDialog", string_to_string_cross_string_list),
("commandNames", unit_to_string_list),
("dialogNames", unit_to_string_list)])
val custom_editor_record = cast
{a_addCommand = CustomEditor.addCommand : string * string -> unit,
a_addConnectDialog = CustomEditor.addConnectDialog : (string * string * string list) -> unit,
a_removeCommand = CustomEditor.removeCommand :
string -> string,
a_removeDialog = CustomEditor.removeDialog :
string -> (string * string list),
a_commandNames = CustomEditor.commandNames : unit -> string list,
a_dialogNames = CustomEditor.dialogNames : unit -> string list
}
fun edit_file string =
(ignore(ShellUtils.edit_file
(string, ShellTypes.get_current_preferences (!shell_data_ref)));
())
handle ShellUtils.EditFailed s => env_error (edit_exn, s)
fun edit_definition (f : 'a -> 'b) =
let
val f : MLWorks.Internal.Value.T = cast f
val preferences =
ShellTypes.get_current_preferences (!shell_data_ref)
in
ignore(ShellUtils.edit_object (f, preferences));
()
end
handle ShellUtils.EditFailed s => env_error (edit_exn, s)
val editor_structure =
mk_mixed_structure
([("Custom",custom_editor_structure)],
[],
[("editFile", string_to_unit),
("editDefinition",fun_to_unit)],
[(edit_exn_label,string_to_exn)])
val editor_record = cast
{a_editFile = edit_file : string -> unit,
a_editDefinition = edit_definition : ('a -> 'b) -> unit,
a_EditError = edit_exn : internal_exn_rep,
c_custom = custom_editor_record}
val debug_structure =
STR (
STRNAME (Types.make_stamp ()),
ref NONE,
ENV (
Strenv.empty_strenv,
Tyenv.empty_tyenv,
Lists.reducel
(fn (ve, (name, typescheme)) =>
Valenv.add_to_ve(
Ident.VAR(find_symbol name),
typescheme,
ve
)
)
(empty_valenv,
[("info", fun_to_string),
("infoAll", unit_to_string_list),
("status", fun_to_bool),
("stepThrough", fun_to_fun),
("clear", fun_to_unit),
("clearAll", unit_to_unit)])
)
)
fun debug_clear f =
let
val name = ValuePrinter.function_name f
in
UserContext.clear_debug_info
(ShellTypes.get_user_context (!shell_data_ref), name)
end
fun debug_clear_all () =
UserContext.clear_debug_all_info
(ShellTypes.get_user_context (!shell_data_ref))
fun debug_status f =
let
val debug_info = Incremental.debug_info (get_context ())
val name = ValuePrinter.function_name f
in
case Debugger_Types.lookup_debug_info (debug_info,name) of
SOME _ => true
| _ => false
end
fun debug_info f =
let
val options = ShellTypes.get_current_options
(!ShellTypes.shell_data_ref)
val debug_info = Incremental.debug_info (get_context ())
val name = ValuePrinter.function_name f
in
Debugger_Types.print_function_information options
(name,debug_info,true)
end
fun debug_info_all () =
let
val options = ShellTypes.get_current_options
(!ShellTypes.shell_data_ref)
val debug_info = Incremental.debug_info (get_context ())
in
Debugger_Types.print_information options (debug_info,true)
end
val debug_record =
cast
{a_clearAll = debug_clear_all : unit -> unit,
a_clear = debug_clear : ('a -> 'b) -> unit,
a_info = debug_info : ('a -> 'b) -> string,
a_infoAll = debug_info_all : unit -> string list,
a_status = debug_status : ('a -> 'b) -> bool,
a_stepThrough = Trace.step_through : ('a -> 'b) -> ('a -> 'b)}
val options_structure =
STR (STRNAME (Types.make_stamp ()),
ref NONE,
ENV(mk_strenv
[("Preferences", preferences_structure),
("ValuePrinter", value_printer_structure),
("Compiler", compiler_structure),
("Debugger", debugger_structure),
("Internals", internals_structure),
("Language", language_structure),
("Mode", mode_option_structure)],
mk_tyenv
[("option",
TYSTR (ETA_TYFUN option_tyname, empty_valenv))],
mk_valenv
([("set", set_option_type),
("get", get_option_type)],
[])))
val options_record =
cast
{a_set = set_option : 'a option_rep * 'a -> unit,
a_get = get_option : 'a option_rep -> 'a,
c_compiler = compiler_record,
c_debugger = debugger_record,
c_internals = internals_record,
c_valuePrinter = value_printer_record,
c_language = language_record,
c_mode = mode_option_record,
c_preferences = preferences_record
}
local
val time_space_profile_manner =
Profile.make_manner
{time = true,
space = true,
calls = false,
copies = false,
depth = 0,
breakdown = []}
val time_space_profile_options =
Profile.Options {scan = 10,
selector = fn _ => time_space_profile_manner}
val time_profile_manner =
Profile.make_manner
{time = true,
space = false,
calls = false,
copies = false,
depth = 0,
breakdown = []}
val time_profile_options =
Profile.Options {scan = 10,
selector = fn _ => time_profile_manner}
val space_profile_manner =
Profile.make_manner
{time = false,
space = true,
calls = false,
copies = false,
depth = 0,
breakdown = []}
val space_profile_options =
Profile.Options {scan = 10,
selector = fn _ => space_profile_manner}
fun profile_tool p =
ShellTypes.get_current_profiler (!shell_data_ref) p
fun profile_full opt f a =
let
val (r,p) = Profile.profile opt f a
in
(profile_tool p;
case r of
Profile.Result r => r
| Profile.Exception e => raise e)
end
val time_space_profile = profile_full time_space_profile_options
val time_profile = profile_full time_profile_options
val space_profile = profile_full space_profile_options
val profile_options_tycon = mk_longtycon(["MLWorks",
"Profile"],
"options")
val profile_tycon = mk_longtycon(["MLWorks",
"Profile"],
"profile")
val profile_options_type = get_runtime_type profile_options_tycon
val profile_type = get_runtime_type profile_tycon
val (profiler_type, profile_full_type) =
let
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
val profile_type_instance = FUNTYPE (FUNTYPE (alpha,beta),
FUNTYPE (alpha,beta))
val full_type_instance = FUNTYPE (profile_options_type,
profile_type_instance)
val free_type_vars = [alpha,beta]
in
(Scheme.make_scheme(free_type_vars, (profile_type_instance,
NONE)),
Scheme.make_scheme(free_type_vars, (full_type_instance,
NONE)))
end
val profile_tool_type = schemify (FUNTYPE (profile_type,
Types.empty_rectype))
in
val profile_structure =
STR(
STRNAME(Types.make_stamp ()),
ref NONE,
ENV (Strenv.empty_strenv,
Tyenv.empty_tyenv,
Lists.reducel
(fn (ve, (name, typescheme)) =>
Valenv.add_to_ve
(Ident.VAR (find_symbol name),
typescheme,
ve))
(empty_valenv,
[("profile",profiler_type),
("profileFull", profile_full_type),
("profileSpace",profiler_type),
("profileTime",profiler_type),
("profileTool",profile_tool_type)])))
val profile_record =
cast
{a_profile = time_space_profile : ('a -> 'b) -> 'a -> 'b,
a_profileFull =
profile_full : Profile.options -> ('a -> 'b) -> 'a -> 'b,
a_profileSpace = space_profile : ('a -> 'b) -> 'a -> 'b,
a_profileTime = time_profile : ('a -> 'b) -> 'a -> 'b,
a_profileTool = profile_tool : Profile.profile -> unit}
end
local
fun time_iterations n f a =
let
fun time' 0 = ()
| time' n = (ignore(f a);
time' (n-1))
val (cpu_timer, real_timer) =
(Timer.startCPUTimer(), Timer.startRealTimer())
in
(time' n;
(Timer.checkCPUTimer cpu_timer,
Timer.checkGCTime cpu_timer,
Timer.checkRealTimer real_timer))
end
val time = time_iterations 1
fun print_timing {outputter : string -> unit, name, function} arg =
let
val (cpu_timer, real_timer) =
(Timer.startCPUTimer(), Timer.startRealTimer())
fun times_to_string({usr, sys}, gc, real_elapsed) =
concat [Time.toString real_elapsed,
" (user: ",
Time.toString usr,
"(gc: ",
Time.toString gc,
"), system: ",
Time.toString sys,
")"]
fun print_time () =
let
val elapsed =
(Timer.checkCPUTimer cpu_timer,
Timer.checkGCTime cpu_timer,
Timer.checkRealTimer real_timer)
in
outputter(concat ["Time for ", name, " : ",
times_to_string elapsed,
"\n"])
end
val result =
function arg
handle exn => (print_time () ; raise exn)
in
(print_time () ; result)
end
val time_tycon = mk_longtycon (["MLWorks",
"Internal",
"Types"],
"time")
val time_type = get_runtime_type time_tycon
val cpu_time_type = mk_record[("usr", time_type),
("sys", time_type)]
val elapsed_t_type = make_tuple[cpu_time_type, time_type, time_type]
type elapsed_type = ({sys: Time.time, usr: Time.time} * Time.time * Time.time)
val outputter_type = FUNTYPE(Types.string_type, Types.empty_rectype)
val time_type =
let
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
val time_type_instance = FUNTYPE (FUNTYPE (alpha,beta),
FUNTYPE (alpha,elapsed_t_type))
in
Scheme.make_scheme([alpha,beta], (time_type_instance,
NONE))
end
val time_iterations_type =
let
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
val time_iterations_type_instance =
FUNTYPE(Types.int_type,
FUNTYPE (FUNTYPE (alpha,beta),
FUNTYPE (alpha,elapsed_t_type)))
in
Scheme.make_scheme([alpha,beta], (time_iterations_type_instance,
NONE))
end
val print_timing_type =
let
val alpha = make_tyvar "'a"
val beta = make_tyvar "'b"
val print_timing_type_instance =
FUNTYPE(mk_record [("outputter",outputter_type),
("name",Types.string_type),
("function",FUNTYPE (alpha, beta))],
FUNTYPE (alpha,beta))
in
Scheme.make_scheme([alpha,beta], (print_timing_type_instance,
NONE))
end
in
val timer_structure =
STR(
STRNAME (Types.make_stamp ()),
ref NONE,
ENV (Strenv.empty_strenv,
Tyenv.empty_tyenv,
Lists.reducel
(fn (ve, (name, typescheme)) =>
Valenv.add_to_ve
(Ident.VAR (find_symbol name),
typescheme,
ve))
(empty_valenv,
[("time",time_type),
("timeIterations", time_iterations_type),
("printTiming", print_timing_type)])))
val timer_record =
cast
{a_time = time : ('a -> 'b) -> 'a -> elapsed_type,
a_timeIterations =
time_iterations :
int -> ('a -> 'b) -> 'a -> elapsed_type,
a_printTiming = print_timing : {outputter: string -> unit,
name: string,
function: ('a -> 'b)} -> 'a -> 'b}
end
val context =
#1(Incremental.add_value
(#1(Incremental.add_value
(initial_context,
"use",
UNBOUND_SCHEME (FUNTYPE (Types.string_type, Types.empty_rectype),
NONE),
cast (use_fun : string -> unit)
)),
"use_string",
UNBOUND_SCHEME (FUNTYPE (Types.string_type, Types.empty_rectype),
NONE),
cast (use_string_fun : string -> unit)
))
local
fun handler_fn msg =
Info.default_error'
(Info.FATAL,
Info.Location.FILE (ShellTypes.get_current_toplevel_name ()),
msg)
val (context', identifiers) =
Incremental.add_structure
(context,
"Shell",
mk_mixed_structure
([("Project", project_structure),
("Path", path_structure),
("Options", options_structure),
("Debug", debug_structure),
("Editor", editor_structure),
("Trace", trace_structure),
("Dynamic",dynamic_structure),
("Inspector",inspector_structure),
("Timer", timer_structure),
("Profile", profile_structure)
],
[],
[("exit", int_to_unit),
("startGUI", unit_to_unit),
("saveImage", string_cross_bool_to_unit)
],
[]),
cast{
a_exit = shell_exit_fn : int -> unit,
a_saveImage =
(fn (name, exe) =>
( if (get_context_option_fun
(#generate_debug_info) ()) orelse
(get_context_option_fun
(#generate_variable_debug_info) ())
then print ("Warning: enabling the debug options " ^
"may result in large saved images.\n")
else ();
SaveImage.saveImage
(is_a_tty_image, handler_fn) (name, exe) ))
: string * bool -> unit,
a_startGUI =
(fn () =>
SaveImage.startGUI
true
(!shell_data_ref)) : unit -> unit,
c_inspector = inspector_record,
c_dynamic = dynamic_record,
c_trace = trace_record,
c_project = project_record,
c_path = path_record,
c_debug = debug_record,
c_editor = editor_record,
c_options = options_record,
c_timer = timer_record,
c_profile = profile_record
})
in
val context = context'
end
val environment_debug_info =
Lists.reducel
(fn (debug_info,(label,exn_type)) =>
(Debugger_Types.add_debug_info
(debug_info,
make_exn_tag label,
Debugger_Types.FUNINFO {ty=exn_type,
is_leaf=true,
has_saved_arg=false,
annotations=[],
runtime_env=Debugger_Types.RuntimeEnv.EMPTY,
is_exn=true})))
(Debugger_Types.empty_information,
[(edit_exn_label,string_to_exn_type),
(eval_exn_label,string_to_exn_type),
(inspect_exn_label,string_to_exn_type),
(project_exn_label,string_to_exn_type)])
val context =
Incremental.add_debug_info
(Options.default_options, environment_debug_info,context)
in
context
end
end
;
