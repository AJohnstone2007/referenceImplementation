require "../debugger/ml_debugger";
require "shell_types";
require "user_context";
require "../main/user_options";
require "../main/preferences";
require "shell";
require "tty_listener";
require "../basis/__text_io";
require "../basis/__io";
functor TTYListener (
structure Ml_Debugger : ML_DEBUGGER
structure ShellTypes: SHELL_TYPES
structure UserContext: USER_CONTEXT
structure UserOptions: USER_OPTIONS
structure Preferences: PREFERENCES
structure Shell: SHELL
sharing UserOptions.Options = Ml_Debugger.ValuePrinter.Options =
ShellTypes.Options
sharing type ShellTypes.user_options =
UserOptions.user_tool_options
sharing type Shell.ShellData = ShellTypes.ShellData
sharing type Shell.Context = ShellTypes.Context = UserContext.Context
sharing type ShellTypes.user_context = UserContext.user_context
sharing type UserContext.user_context_options =
UserOptions.user_context_options
sharing type Ml_Debugger.preferences = Preferences.preferences = ShellTypes.preferences
sharing type ShellTypes.user_preferences = Preferences.user_preferences
): TTY_LISTENER =
struct
structure Info = Shell.Info
type ListenerArgs = ShellTypes.ListenerArgs
fun listener_aux (ShellTypes.LISTENER_ARGS
{user_context,
prompter,
user_options,
user_preferences,
mk_xinterface_fn},
initial_shell_p) =
let
fun output_fn s = TextIO.print s
val exit_fn : int -> unit = (fn n => raise Shell.Exit n)
fun debugger_function f x =
let
val call_debugger =
Ml_Debugger.ml_debugger
(Ml_Debugger.TERMINAL,
ShellTypes.new_options (user_options, user_context),
Preferences.new_preferences user_preferences)
in
Ml_Debugger.with_start_frame
(fn base_frame =>
((f x)
handle
exn as ShellTypes.DebuggerTrapped => raise exn
| exn as Shell.Exit _ => raise exn
| exn as MLWorks.Interrupt => raise exn
| exn as Info.Stop _ => raise exn
| exn =>
(call_debugger
(base_frame,
Ml_Debugger.EXCEPTION exn,
Ml_Debugger.POSSIBLE
("quit (return to listener)",
Ml_Debugger.DO_RAISE ShellTypes.DebuggerTrapped),
Ml_Debugger.NOT_POSSIBLE);
raise ShellTypes.DebuggerTrapped)))
end
fun profiler p =
TextIO.output (TextIO.stdErr, "Graphical profiler not available in TTY Listener\n")
val shell_data =
ShellTypes.SHELL_DATA
{get_user_context = fn () => user_context,
user_options = user_options,
user_preferences = user_preferences,
debugger = debugger_function,
profiler = profiler,
prompter = prompter,
exit_fn = exit_fn,
x_running = false,
mk_xinterface_fn = mk_xinterface_fn,
mk_tty_listener = initial_listener}
val title = "<TTY listener>"
fun flush_stream () = ()
val (handler, do_prompt) = Shell.shell (shell_data,title,flush_stream)
fun debugger_function exn =
let
val shell_data as ShellTypes.SHELL_DATA{prompter,
mk_xinterface_fn,
...} = !ShellTypes.shell_data_ref
val context = ShellTypes.get_current_context shell_data
in
Ml_Debugger.ml_debugger
(Ml_Debugger.get_debugger_type (),
ShellTypes.get_current_options shell_data,
ShellTypes.get_current_preferences shell_data)
(Ml_Debugger.get_start_frame(),
Ml_Debugger.EXCEPTION exn,
Ml_Debugger.POSSIBLE ("Return to top level",
Ml_Debugger.NORMAL_RETURN),
Ml_Debugger.NOT_POSSIBLE)
end
fun loop state =
let
val _ = output_fn(do_prompt ("MLWorks", state))
val line = TextIO.inputLine TextIO.stdIn
handle IO.Io _ => ""
val new_state =
(case #3 (ShellTypes.with_toplevel_name title
(fn () =>
handler
(Info.make_default_options ())
(line, state)))
of Shell.OK s => s
| _ => Shell.initial_state)
handle
MLWorks.Interrupt => Shell.initial_state
| ShellTypes.DebuggerTrapped => Shell.initial_state
| exn as Shell.Exit _ => raise exn
| exn => (debugger_function exn;
Shell.initial_state)
in
loop new_state
end
in
loop Shell.initial_state
handle Shell.Exit n => n
end
and listener args = listener_aux (args,false)
and initial_listener args = listener_aux (args,true)
end
;
