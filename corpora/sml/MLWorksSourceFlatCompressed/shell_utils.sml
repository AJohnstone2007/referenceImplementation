require "../main/options";
require "../main/info";
signature SHELL_UTILS =
sig
structure Info : INFO
structure Options : OPTIONS
type preferences
type user_preferences
type UserOptions
type user_context
type history_entry
type Context
type Type
type ShellData
type Project
exception EditFailed of string
val edit_location : Info.Location.T * preferences -> (unit -> unit)
val edit_source : string * preferences -> (unit -> unit)
val edit_object : MLWorks.Internal.Value.T * preferences -> (unit -> unit)
val edit_file : string * preferences -> (unit -> unit)
val show_source : string * preferences -> unit
val editable : Info.Location.T -> bool
val object_editable : MLWorks.Internal.Value.T -> bool
val trace : MLWorks.Internal.Value.T -> unit
val untrace : MLWorks.Internal.Value.T -> unit
val object_traceable : MLWorks.Internal.Value.T -> bool
val object_path: string * Info.Location.T -> string
val force_compile:
Info.Location.T * Options.options -> Info.options -> string -> unit
val force_compile_all:
Info.Location.T * Options.options -> Info.options -> unit
val delete_from_project: string * Info.Location.T -> unit
val make_exe_from_project :
Info.Location.T *
Info.options *
string * string list ->
unit
val make_dll_from_project :
Info.Location.T *
Info.options *
string * string list ->
unit
val compile_file :
Info.Location.T * Options.options -> Info.options -> string -> unit
val show_compile_file :
Info.Location.T * (string -> unit) -> Info.options -> string -> unit
val load_file :
user_context *
Info.Location.T *
Options.options *
preferences *
(string -> unit) ->
Info.options -> string ->
unit
val show_load_file :
Info.Location.T * (string -> unit) ->
Info.options -> string ->
unit
val compile_targets :
Info.Location.T * Options.options -> Info.options -> unit
val show_compile_targets :
Info.Location.T * (string -> unit) -> Info.options -> unit
val load_targets :
user_context *
Info.Location.T *
Options.options *
preferences *
(string -> unit) ->
Info.options ->
unit
val show_load_targets :
Info.Location.T * (string -> unit) ->
Info.options -> unit
val use_file : ShellData * (string -> unit) * string -> unit
val use_string : ShellData * (string -> unit) * string -> unit
exception NotAnExpression
val eval :
Info.options ->
string *
Options.options *
Context ->
(MLWorks.Internal.Value.T * Type)
val print_value :
(MLWorks.Internal.Value.T * Type) *
Options.print_options *
Context ->
string
val print_type :
Type * Options.options * Context -> string
val get_completions :
string * Options.options * Context -> (string * string list)
val find_common_completion : string list -> string
val lookup_name :
string * Context * (MLWorks.Internal.Value.T * Type)
-> (MLWorks.Internal.Value.T * Type)
val value_from_history_entry :
history_entry * Options.options ->
(string * (MLWorks.Internal.Value.T * Type)) option
val value_from_user_context :
user_context * UserOptions ->
(string * (MLWorks.Internal.Value.T * Type)) option
val read_dot_mlworks: ShellData -> unit
end
;
