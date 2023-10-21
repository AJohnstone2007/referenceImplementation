require "runtime_env";
require "^.main.options";
signature DEBUGGER_TYPES =
sig
structure RuntimeEnv : RUNTIMEENV
structure Options: OPTIONS
type Type
type Instance
eqtype Tyname
datatype Recipe =
SELECT of int * Recipe |
MAKERECORD of (string * Recipe) list |
NOP |
ERROR of string |
FUNARG of Recipe |
FUNRES of Recipe |
MAKEFUNTYPE of Recipe * Recipe |
DECONS of int * Recipe |
MAKECONSTYPE of Recipe list * Tyname
type information
type Backend_Annotation
sharing type Backend_Annotation = Recipe
datatype FunInfo =
FUNINFO of
{ty : Type,
is_leaf : bool,
has_saved_arg : bool,
annotations : (int * Backend_Annotation) list,
runtime_env : RuntimeEnv.RuntimeEnv,
is_exn : bool}
val print_information : Options.options ->
information * bool -> string list
val print_function_information :
Options.options -> string * information * bool -> string
val empty_information : information
val augment_information : bool * information * information -> information
val clear_information : string * information -> information
val null_backend_annotation : Backend_Annotation
val empty_runtime_env : RuntimeEnv.RuntimeEnv
val print_backend_annotation : Options.options ->
Backend_Annotation -> string
val print_type : Options.options -> Type -> string
val null_type : Type
val int_type : Type
val int_pair_type : Type
val string_pair_type : Type
val string_list_type : Type
val exn_type : Type
val string_types :
Options.options ->
Type * (int * Type * Instance) ref list ->
string * (int * Type * Instance) ref list
val set_proc_data : string * bool * bool * RuntimeEnv.RuntimeEnv * information -> information
val add_debug_info : information * string * FunInfo -> information
val add_annotation : string * int * Backend_Annotation * information -> information
val lookup_debug_info : information * string -> FunInfo option
val debug_info_to_list : information -> (string * FunInfo) list
val debug_info_from_list : (string * FunInfo) list -> information
end
;
