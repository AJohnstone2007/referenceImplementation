require "user_context";
require "../main/user_options";
require "../main/preferences";
require "shell_types";
functor ShellTypes (
structure UserContext: USER_CONTEXT
structure UserOptions: USER_OPTIONS
structure Preferences: PREFERENCES
sharing UserOptions.Options = UserContext.Options
sharing type UserOptions.user_context_options =
UserContext.user_context_options
): SHELL_TYPES =
struct
structure Options = UserOptions.Options
type user_options = UserOptions.user_tool_options
type user_preferences = Preferences.user_preferences
type user_context = UserContext.user_context
type Context = UserContext.Context
type preferences = Preferences.preferences
exception DebuggerTrapped
datatype ListenerArgs =
LISTENER_ARGS of
{user_context : user_context,
user_options : user_options,
user_preferences : user_preferences,
prompter :
{line : int, subline : int, name : string, topdec : int} -> string,
mk_xinterface_fn : ListenerArgs -> bool -> unit}
fun new_options (user_options, user_context) =
UserOptions.new_options
(user_options, UserContext.get_user_options user_context)
datatype ShellData =
SHELL_DATA of
{get_user_context : unit -> user_context,
user_options : user_options,
user_preferences : user_preferences,
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
fun get_listener_args (SHELL_DATA{get_user_context,
user_options,
user_preferences,
prompter,
mk_xinterface_fn,
...}) =
LISTENER_ARGS
{user_context = get_user_context (),
user_options = UserOptions.copy_user_tool_options user_options,
user_preferences = user_preferences,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn}
fun get_current_options (SHELL_DATA{user_options, get_user_context, ...}) =
UserOptions.new_options
(user_options, UserContext.get_user_options (get_user_context ()))
fun get_current_preferences (SHELL_DATA{user_preferences,...}) =
Preferences.new_preferences user_preferences
fun get_user_options (SHELL_DATA{user_options,...}) =
user_options
fun get_user_preferences (SHELL_DATA{user_preferences,...}) =
user_preferences
fun get_user_context
(SHELL_DATA{get_user_context, ...}) =
get_user_context ()
fun get_current_context shell_data =
UserContext.get_context (get_user_context shell_data)
fun get_current_prompter (SHELL_DATA{prompter,...}) = prompter
fun get_current_profiler (SHELL_DATA{profiler,...}) = profiler
fun get_print_options (Options.OPTIONS{print_options,...}) = print_options
fun get_current_print_options shell_data =
get_print_options(get_current_options shell_data)
exception BadShellData of string
val shell_data_ref =
ref
(SHELL_DATA
{get_user_context = fn () => UserContext.dummy_context,
user_options =
UserOptions.make_user_tool_options Options.default_options,
user_preferences =
Preferences.make_user_preferences Preferences.default_preferences,
prompter = fn _ => raise BadShellData "prompter",
debugger = fn _ => raise BadShellData "debugger",
profiler = fn _ => raise BadShellData "profiler",
exit_fn = fn _ => raise BadShellData "exit",
x_running = false,
mk_xinterface_fn = fn _ => raise BadShellData "xinterface",
mk_tty_listener = fn _ => raise BadShellData "tty listener"})
fun with_shell_data shell_data f =
let
val old_data = !shell_data_ref
val _ = shell_data_ref := shell_data
val result = f () handle exn => (shell_data_ref := old_data; raise exn)
in
shell_data_ref := old_data;
result
end
val toplevel_name_ref = ref []
fun get_current_toplevel_name () =
case !toplevel_name_ref of
(n::_) => n
| _ => "Top Level"
fun with_toplevel_name name f =
let
val previous = !toplevel_name_ref
val _ = toplevel_name_ref := name :: !toplevel_name_ref;
val result = (f ()) handle exn => (toplevel_name_ref := previous;raise exn)
in
toplevel_name_ref := previous;
result
end
end;
