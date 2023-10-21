require "../interpreter/shell_types";
require "../interpreter/user_context";
signature TOOL_DATA =
sig
structure ShellTypes : SHELL_TYPES
structure UserContext : USER_CONTEXT
sharing ShellTypes.Options = UserContext.Options
sharing type ShellTypes.Context = UserContext.Context
sharing type ShellTypes.user_context = UserContext.user_context
type MotifContext
type Widget
type ButtonSpec
type current_context
datatype Writable = WRITABLE | ALL
datatype ApplicationData =
APPLICATIONDATA of {applicationShell : Widget, has_controlling_tty: bool}
datatype ToolData =
TOOLDATA of
{args: ShellTypes.ListenerArgs,
appdata : ApplicationData,
current_context : current_context,
motif_context : MotifContext,
tools : (string * (ToolData -> unit) * Writable) list}
val add_context_fn :
current_context
* ((MotifContext -> unit)
* (unit -> ShellTypes.user_options)
* Writable)
-> int
val remove_context_fn : current_context * int -> unit
val set_current :
current_context * int * ShellTypes.user_options * MotifContext
-> unit
val get_current : current_context -> MotifContext
val make_current : MotifContext -> current_context
val exit_mlworks: Widget * ApplicationData -> unit
val tools_menu:
(unit -> ToolData) * (unit -> ShellTypes.user_context)
-> ButtonSpec
val extract : ButtonSpec -> (string * (unit -> unit) * (unit -> bool)) list
val edit_menu :
Widget *
{cut : (unit -> unit) option,
paste : (unit -> unit) option,
copy : (unit -> unit) option,
delete : (unit -> unit) option,
selection_made : unit -> bool,
edit_possible : unit -> bool,
edit_source: ButtonSpec list,
delete_all: (string * (unit -> unit) * (unit -> bool)) option} -> ButtonSpec
val file_menu : (string * (unit -> unit) * (unit -> bool)) list -> ButtonSpec
val set_global_file_items : (string * (unit -> unit) * (unit -> bool)) list -> ButtonSpec
val debug_menu : (string * (unit -> unit) * (unit -> bool)) list -> ButtonSpec
val usage_menu : (string * (unit -> unit) * (unit -> bool)) list *
(string * (unit -> bool) * (bool -> unit) * (unit -> bool)) list
-> ButtonSpec
val set_global_usage_items : (string * (unit -> unit) * (unit -> bool)) list *
(string * (unit -> bool) * (bool -> unit) * (unit -> bool)) list
-> ButtonSpec
end
;
