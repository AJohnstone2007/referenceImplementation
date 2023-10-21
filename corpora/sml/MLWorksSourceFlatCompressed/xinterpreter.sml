require "../main/__toplevel";
require "../main/__info";
require "../debugger/__ml_debugger";
require "__shell_types";
require "__user_context";
require "../main/__user_options";
require "../main/__preferences";
require "__tty_listener";
require "__shell_structure";
require "__incremental";
require "__os";
require "../winsys/__podium";
local
structure Options = TopLevel_.Options
structure UserContext = UserContext_
structure ShellTypes = ShellTypes_
structure Ml_Debugger = Ml_Debugger_
structure Incremental = Incremental_
structure Info = Info_
fun default_prompter {name, topdec, line, subline} =
concat [name, if subline > 0 then ">> " else "> "]
exception NoDebugger
val initial_context =
ShellStructure_.make_shell_structure false
(ShellTypes.shell_data_ref, Incremental.initial)
val user_context_options =
UserOptions_.make_user_context_options Options.default_options
val user_preferences =
Preferences_.make_user_preferences Preferences_.default_preferences
val _ =
case user_context_options
of UserOptions_.USER_CONTEXT_OPTIONS
{1={generate_interruptable_code, ...}, ...} =>
generate_interruptable_code := true;
val _ = UserContext.makeInitialUserContext
(initial_context, "Initial", user_context_options)
fun main arguments =
TTYListener_.listener
(ShellTypes_.LISTENER_ARGS
{user_context = UserContext.getNewInitialContext(),
user_options =
UserOptions_.make_user_tool_options Options.default_options,
user_preferences = user_preferences,
prompter=default_prompter,
mk_xinterface_fn = Podium_.start_x_interface})
in
val _ = MLWorks.Internal.Runtime.modules :=
(case rev(!MLWorks.Internal.Runtime.modules) of
x :: y :: _ => [y, x]
| _ => !MLWorks.Internal.Runtime.modules )
fun handle_fatal_signal shell_data_ref s =
let
val shell_data as ShellTypes.SHELL_DATA{prompter,
mk_xinterface_fn,
...} = !shell_data_ref
val context = ShellTypes.get_current_context shell_data
in
Ml_Debugger.ml_debugger
(Ml_Debugger.get_debugger_type (),
ShellTypes.get_current_options shell_data,
ShellTypes.get_current_preferences shell_data)
(Ml_Debugger.get_start_frame(),
Ml_Debugger.FATAL_SIGNAL s,
Ml_Debugger.POSSIBLE ("Return to top level",
Ml_Debugger.FUN
(fn _ =>
(MLWorks.Threads.Internal.reset_fatal_status();
raise MLWorks.Interrupt))),
Ml_Debugger.NOT_POSSIBLE)
end
fun interrupt_function shell_data_ref s =
let
val shell_data as ShellTypes.SHELL_DATA{prompter,
mk_xinterface_fn,
...} = !shell_data_ref
val context = ShellTypes.get_current_context shell_data
in
Ml_Debugger.ml_debugger
(Ml_Debugger.get_debugger_type (),
ShellTypes.get_current_options shell_data,
ShellTypes.get_current_preferences shell_data)
(Ml_Debugger.get_start_frame(),
Ml_Debugger.INTERRUPT,
Ml_Debugger.POSSIBLE ("Return to top level",
Ml_Debugger.DO_RAISE MLWorks.Interrupt),
Ml_Debugger.POSSIBLE ("Continue interrupted code",
Ml_Debugger.NORMAL_RETURN))
end
fun double_stack_limit () =
let val m = MLWorks.Internal.Runtime.Memory.max_stack_blocks
in m := (!m * 2)
end
fun stack_overflow_function shell_data_ref s =
let
val shell_data as ShellTypes.SHELL_DATA{prompter,
mk_xinterface_fn,
...} = !shell_data_ref
val context = ShellTypes.get_current_context shell_data
in
Ml_Debugger.ml_debugger
(Ml_Debugger.get_debugger_type (),
ShellTypes.get_current_options shell_data,
ShellTypes.get_current_preferences shell_data)
(Ml_Debugger.get_start_frame(),
Ml_Debugger.STACK_OVERFLOW,
Ml_Debugger.POSSIBLE
("Return to top level", Ml_Debugger.DO_RAISE MLWorks.Interrupt),
Ml_Debugger.POSSIBLE
("Continue with extended stack",
Ml_Debugger.FUN double_stack_limit))
end
val _ =
MLWorks.Internal.Runtime.Event.stack_overflow_handler
(stack_overflow_function ShellTypes.shell_data_ref)
val _ =
MLWorks.Internal.Runtime.Event.interrupt_handler
(interrupt_function ShellTypes.shell_data_ref)
val _ =
MLWorks.Threads.Internal.set_handler
(handle_fatal_signal ShellTypes.shell_data_ref)
fun break_function (shell_data_ref) s =
let
val shell_data as ShellTypes.SHELL_DATA{prompter,
mk_xinterface_fn,
...} = !shell_data_ref
val context = ShellTypes.get_current_context shell_data
in
Ml_Debugger.ml_debugger
(Ml_Debugger.get_debugger_type (),
ShellTypes.get_current_options shell_data,
ShellTypes.get_current_preferences shell_data)
(Ml_Debugger.get_start_frame(),
Ml_Debugger.BREAK s,
Ml_Debugger.POSSIBLE ("Return to top level",
Ml_Debugger.DO_RAISE MLWorks.Interrupt),
Ml_Debugger.POSSIBLE ("Continue interrupted code",
Ml_Debugger.NORMAL_RETURN))
end
val _ = MLWorks.Internal.Debugger.break_hook :=
break_function (ShellTypes.shell_data_ref)
val _ = case main (MLWorks.arguments ()) of
0 => OS.Process.exit (OS.Process.success)
| _ => OS.Process.exit (OS.Process.failure);
end
;
