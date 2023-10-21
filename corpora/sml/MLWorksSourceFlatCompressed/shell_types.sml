require "../main/options";
signature SHELL_TYPES =
sig
structure Options : OPTIONS
type preferences
type user_preferences
type user_options
type Context
type user_context
datatype ListenerArgs =
LISTENER_ARGS of
{user_context: user_context,
user_options : user_options,
user_preferences : user_preferences,
prompter :
{line : int, subline : int, name : string, topdec : int} -> string,
mk_xinterface_fn : ListenerArgs -> bool -> unit}
val new_options: user_options * user_context -> Options.options
datatype ShellData =
SHELL_DATA of
{get_user_context: unit -> user_context,
user_options : user_options,
user_preferences: user_preferences,
prompter :
{line : int, subline : int, name : string, topdec : int} -> string,
debugger :
(MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T) ->
(MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T),
profiler : MLWorks.Profile.profile -> unit,
exit_fn : int -> unit,
x_running : bool,
mk_xinterface_fn : ListenerArgs -> bool -> unit,
mk_tty_listener : ListenerArgs -> int}
val get_user_options : ShellData -> user_options
val get_user_preferences : ShellData -> user_preferences
val get_current_context : ShellData -> Context
val get_user_context : ShellData -> user_context
val get_current_options : ShellData -> Options.options
val get_current_preferences : ShellData -> preferences
val get_current_prompter :
ShellData -> ({line : int, subline : int, name : string, topdec : int}
-> string)
val get_current_profiler : ShellData -> (MLWorks.Profile.profile -> unit)
val get_current_print_options : ShellData -> Options.print_options
val get_listener_args : ShellData -> ListenerArgs
val shell_data_ref : ShellData ref
val with_shell_data : ShellData -> (unit -> 'a) -> 'a
val with_toplevel_name : string -> (unit -> 'a) -> 'a
val get_current_toplevel_name : unit -> string
exception DebuggerTrapped
end;
