require "^.basis.__int";
require "^.basis.__string";
require "^.basis.__timer";
require "^.basis.__text_io";
require "^.system.__time";
require "^.utils.lists";
require "^.utils.crash";
require "^.utils.mutex";
require "^.utils.__terminal";
require "capi";
require "menus";
require "gui_utils";
require "tooldata";
require "debugger_window";
require "inspector_tool";
require "profile_tool";
require "error_browser";
require "^.main.preferences";
require "^.main.user_options";
require "^.interpreter.shell";
require "^.interpreter.shell_utils";
require "^.interpreter.tty_listener";
require "^.interpreter.save_image";
require "^.debugger.ml_debugger";
require "^.debugger.newtrace";
require "listener";
functor Listener (
structure Lists: LISTS
structure Crash : CRASH
structure Capi: CAPI
structure Preferences : PREFERENCES
structure UserOptions : USER_OPTIONS
structure Shell: SHELL
structure ShellUtils : SHELL_UTILS
structure TTYListener : TTY_LISTENER
structure Ml_Debugger: ML_DEBUGGER
structure Trace : TRACE
structure ToolData : TOOL_DATA
structure GuiUtils : GUI_UTILS
structure Menus : MENUS
structure DebuggerWindow : DEBUGGERWINDOW
structure InspectorTool : INSPECTORTOOL
structure ProfileTool : PROFILE_TOOL
structure ErrorBrowser: ERROR_BROWSER
structure Mutex : MUTEX
structure SaveImage : SAVE_IMAGE
sharing UserOptions.Options = Ml_Debugger.ValuePrinter.Options =
ShellUtils.Options = ToolData.ShellTypes.Options
sharing Shell.Info = ShellUtils.Info
sharing type ToolData.ShellTypes.user_options = UserOptions.user_tool_options =
GuiUtils.user_tool_options = ShellUtils.UserOptions
sharing type GuiUtils.user_context_options =
ToolData.UserContext.user_context_options
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context
sharing type Shell.Context = ShellUtils.Context = ToolData.ShellTypes.Context
sharing type Shell.ShellData = ToolData.ShellTypes.ShellData
sharing type Menus.Widget = DebuggerWindow.Widget = ToolData.Widget = GuiUtils.Widget =
Capi.Widget = ProfileTool.Widget = ErrorBrowser.Widget = InspectorTool.Widget
sharing type TTYListener.ListenerArgs = ToolData.ShellTypes.ListenerArgs
sharing type ErrorBrowser.location = ShellUtils.Info.Location.T
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ToolData = DebuggerWindow.ToolData =
InspectorTool.ToolData = ProfileTool.ToolData = ErrorBrowser.ToolData
sharing type Preferences.preferences = ToolData.ShellTypes.preferences =
Ml_Debugger.preferences = ShellUtils.preferences
sharing type ShellUtils.user_context = GuiUtils.user_context =
ProfileTool.user_context = ErrorBrowser.user_context
sharing type ShellUtils.Type = GuiUtils.Type = InspectorTool.Type
sharing type Preferences.user_preferences = ShellUtils.user_preferences =
ToolData.ShellTypes.user_preferences = GuiUtils.user_preferences =
ProfileTool.user_preferences
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type Ml_Debugger.debugger_window = DebuggerWindow.debugger_window
sharing type ErrorBrowser.error = Shell.Info.error
): LISTENER =
struct
structure Info = ShellUtils.Info
structure Location = Info.Location
structure Options = ShellUtils.Options
structure ShellTypes = ToolData.ShellTypes
structure UserContext = ToolData.UserContext
type ToolData = ToolData.ToolData
val do_debug = false
fun debug s = if do_debug then Terminal.output(s ^ "\n") else ()
fun fdebug f = if do_debug then Terminal.output(f() ^ "\n") else ()
fun ddebug s = Terminal.output(s ^ "\n")
fun make_debugger_function (debugger_type,user_options,user_preferences,local_context) f x =
Ml_Debugger.with_start_frame
(fn base_frame =>
(f x)
handle
exn as ShellTypes.DebuggerTrapped => raise exn
| exn as Shell.Exit _ => raise exn
| exn as MLWorks.Interrupt => raise exn
| exn as Info.Stop _ => raise exn
| exn as Capi.SubLoopTerminated => raise exn
| exn =>
(Ml_Debugger.ml_debugger
(debugger_type,
ShellTypes.new_options
(user_options,
GuiUtils.get_user_context (!local_context)),
Preferences.new_preferences user_preferences)
(base_frame,
Ml_Debugger.EXCEPTION exn,
Ml_Debugger.POSSIBLE
("quit (return to listener)",
Ml_Debugger.DO_RAISE ShellTypes.DebuggerTrapped),
Ml_Debugger.NOT_POSSIBLE);
raise ShellTypes.DebuggerTrapped))
val prompt = "MLWorks>"
val prompt_len = size prompt
fun strip_prompt (s:string):string =
if String.isPrefix prompt s then
String.extract (s, prompt_len, NONE)
else
s
exception NoLocation
fun get_location line =
let
val sz = size line
fun aux index =
if index < sz
then
if String.sub (line,index) = #":" then
index+1
else
aux (index+1)
else
raise NoLocation
val result = SOME (substring (line,0,(aux (aux 0))-1))
handle NoLocation => NONE
in
result
end
val inputBuffer = ref ""
val anyOutput = ref false
val anyRecentOutput = ref false
local
val mutex = Mutex.newBinaryMutex false
val claimant = ref NONE
val numClaims = ref 0
in
fun claimWindow () =
let
val me = MLWorks.Threads.Internal.id()
val notClaimed = case !claimant
of (SOME id) => id<>me
| NONE => true
in
if notClaimed then (Mutex.wait [mutex];
claimant:=(SOME me);
numClaims:=1)
else numClaims:=(!numClaims+1)
end
fun releaseWindow () =
(numClaims:= !numClaims-1;
if !numClaims=0 then (claimant:=NONE;
Mutex.signal [mutex])
else ())
fun lockWindow f a =
(claimWindow ();
(f a)
before (releaseWindow())
)
handle e => (releaseWindow(); raise e)
end
fun get_current_line text =
let
val pos = Capi.Text.get_insertion_position text
in
Capi.Text.get_line (text, pos)
end
fun get_current_subline (text,start_pos) =
let
val (line,ix) = Capi.Text.get_line_and_index (text,start_pos)
val line2 = strip_prompt line
val ix2 = ix - (size line - size line2)
in
if ix2 > 0 then substring (line2,0,ix2) else ""
end
val (input_flag,input_string) = (ref false,ref "")
val text_size = Capi.Text.text_size
fun replace_current_input ((text,prompt_pos,write_pos),line) =
if !input_flag then
()
else
let
val length = Capi.Text.get_last_position text
val line = Capi.Text.check_insertion(text, line, !prompt_pos, [write_pos,prompt_pos])
in
Capi.Text.replace (text,!prompt_pos,length,line);
Capi.Text.set_insertion_position
(text, !prompt_pos + text_size line)
end
fun start_of_line (text,prompt_pos,write_pos) () =
let
val ppos = !prompt_pos
val pos = Capi.Text.get_insertion_position text
val new_pos =
if pos < ppos
then Capi.Text.current_line (text,pos)
else ppos
in
Capi.Text.set_insertion_position (text,new_pos)
end
fun end_of_line (text,prompt_pos,write_pos) () =
let
val ppos = !prompt_pos
val pos = Capi.Text.get_insertion_position text
val new_pos =
if pos < ppos
then Capi.Text.end_line (text,pos)
else Capi.Text.get_last_position text
in
Capi.Text.set_insertion_position (text,new_pos)
end
fun forward_char (text,prompt_pos,write_pos) () =
let
val pos = Capi.Text.get_insertion_position text
val last_pos = Capi.Text.get_last_position text
val end_line = Capi.Text.end_line (text, pos)
val new_pos =
if pos = last_pos orelse pos = last_pos - 1 then
last_pos
else if pos = end_line - 1 then
pos + size Capi.terminator
else
pos + 1
in
Capi.Text.set_insertion_position (text, new_pos)
end
fun backward_char (text,prompt_pos,write_pos) () =
let
val pos = Capi.Text.get_insertion_position text
val end_line = Capi.Text.end_line (text, pos)
val last_pos = Capi.Text.get_last_position text
val term_size = size Capi.terminator
val new_pos =
if pos = 0 then
0
else if pos = last_pos then
if Capi.Text.substring (text, last_pos - term_size, term_size) =
Capi.terminator then
pos - term_size
else
pos - 1
else if pos = end_line then
pos - term_size
else
pos - 1
in
Capi.Text.set_insertion_position (text, new_pos)
end
fun previous_line (text,prompt_pos,write_pos) () =
let
val pos = Capi.Text.get_insertion_position text
val start_line = Capi.Text.current_line (text, pos)
val end_line = Capi.Text.end_line (text, pos)
val column =
if pos = start_line then
0
else if pos = end_line then
pos - start_line - (size Capi.terminator - 1)
else
pos - start_line
val prev_line =
if start_line = 0 then
0
else
Capi.Text.current_line (text, start_line - 1)
val length_prev_line = start_line - prev_line
val new_pos =
if start_line = 0 then
pos
else if column > length_prev_line then
start_line - 1
else
prev_line + column
in
Capi.Text.set_insertion_position (text, new_pos)
end
fun next_line (text,prompt_pos,write_pos) () =
let
val pos = Capi.Text.get_insertion_position text
val start_line = Capi.Text.current_line (text, pos)
val end_line = Capi.Text.end_line (text, pos)
val column =
if pos = end_line then
pos - start_line - (size Capi.terminator - 1)
else
pos - start_line
val last_pos = Capi.Text.get_last_position text
val end_next_line =
if last_pos = end_line then
last_pos
else
Capi.Text.end_line (text, end_line + 1)
val length_next_line = end_next_line - end_line
val new_pos =
if last_pos = end_line then
pos
else if column >= length_next_line then
end_next_line
else
end_line + column + 1
in
Capi.Text.set_insertion_position (text, new_pos)
end
fun get_current_input (text, start_pos, write_pos) =
let
val last_pos = Capi.Text.get_last_position text
val input =
Capi.Text.substring (text, start_pos, last_pos - start_pos)
in
if size input = 0 orelse
String.sub (input, size input - 1) <> #"\n" then
(debug ("Inserting return at " ^ Int.toString last_pos);
Capi.Text.insert (text, last_pos, "\n");
write_pos := last_pos + text_size "\n";
Capi.Text.set_insertion_position
(text, last_pos + text_size "\n");
input ^ "\n")
else
(write_pos := last_pos;
input)
end
fun get_input_line (text, start_pos, write_pos) =
let
val last_pos = Capi.Text.get_last_position text
val current_pos = Capi.Text.get_insertion_position text
in
if current_pos < start_pos then
let
val line =
strip_prompt (Capi.Text.get_line (text, current_pos)) ^ "\n"
in
Capi.Text.insert(text, last_pos, line);
write_pos := last_pos + text_size line;
Capi.Text.set_insertion_position
(text, last_pos + text_size line);
line
end
else
get_current_input (text, start_pos, write_pos)
end
val listener_tool = ref NONE
val sizeRef = ref NONE
val posRef = ref NONE
fun create_listener podiumExists
(tooldata as ToolData.TOOLDATA
{args,appdata,current_context,motif_context,tools}) =
let
val ShellTypes.LISTENER_ARGS {user_options,
user_preferences,
prompter,
mk_xinterface_fn,
...} = args
val ToolData.APPLICATIONDATA {applicationShell,...} = appdata
val full_menus =
case user_preferences
of Preferences.USER_PREFERENCES ({full_menus, ...}, _) =>
!full_menus
val title = "Listener"
val location_title = "<"^title^">"
val (shell,mainWindow,menuBar,contextLabel) =
if podiumExists then
Capi.make_main_window
{name = "listener",
title = title,
parent = applicationShell,
contextLabel = full_menus,
winMenu = false,
pos = getOpt(!posRef, Capi.getNextWindowPos())}
else
let
val (mainWindow, menuBar, contextLabel) =
Capi.make_main_subwindows (applicationShell, full_menus)
in
Capi.reveal mainWindow;
(applicationShell, mainWindow, menuBar, contextLabel)
end
val buttonPane =
Capi.make_managed_widget ("buttonPane", Capi.RowColumn, mainWindow, []);
fun beep () = Capi.beep shell
fun message_fun s = Capi.send_message (shell,s)
val local_context = ref motif_context
val (run_debugger, clean_debugger) =
DebuggerWindow.make_debugger_window
(shell, title ^ " Debugger", tooldata)
val debugger_type =
Ml_Debugger.WINDOWING
(run_debugger,
print,
true)
val debugger_function =
make_debugger_function
(debugger_type,user_options,user_preferences,local_context)
fun get_current_user_context () =
GuiUtils.get_user_context (!local_context)
fun get_user_context_options () =
ToolData.UserContext.get_user_options (get_current_user_context ())
fun get_user_options () = user_options
fun mk_listener_args () =
ShellTypes.LISTENER_ARGS
{user_context = GuiUtils.get_user_context (!local_context),
user_options = UserOptions.copy_user_tool_options user_options,
user_preferences = user_preferences,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn}
fun mk_tooldata () =
ToolData.TOOLDATA {args = mk_listener_args(),
appdata = appdata,
current_context = current_context,
motif_context = !local_context,
tools = tools}
val profiler =
ProfileTool.create (applicationShell, user_preferences,
mk_tooldata, get_current_user_context)
val time_space_profile_manner =
MLWorks.Profile.make_manner
{time = true,
space = true,
calls = false,
copies = false,
depth = 0,
breakdown = []}
val time_space_profile_options =
MLWorks.Profile.Options {scan = 10,
selector = fn _ => time_space_profile_manner}
val profiling = ref false
fun profile f a =
let
val (r,p) =
MLWorks.Profile.profile time_space_profile_options f a
in
(profiler p;
case r of
MLWorks.Profile.Result r => r
| MLWorks.Profile.Exception e => raise e)
end
fun profiling_debugger_function f a =
if (!profiling) then
debugger_function (profile f) a
else
debugger_function f a
val shell_data =
ShellTypes.SHELL_DATA
{get_user_context =
fn () => GuiUtils.get_user_context (!local_context),
user_options = user_options,
user_preferences = user_preferences,
prompter = prompter,
debugger = profiling_debugger_function,
profiler = profiler,
exit_fn = fn n => raise Shell.Exit n,
x_running = true,
mk_xinterface_fn = mk_xinterface_fn,
mk_tty_listener = TTYListener.listener
}
val quit_funs = ref [fn () => listener_tool := NONE]
fun do_quit_funs _ = Lists.iterate (fn f => f ()) (!quit_funs)
val (scroll,text) = (Capi.make_scrolled_text ("textIO",mainWindow,[]))
val interrupt_button =
if podiumExists then
NONE
else
let
val i = Capi.make_managed_widget("interruptButton", Capi.Button, mainWindow, [])
in
Capi.Callback.add (i, Capi.Callback.Activate, fn _ => Capi.set_focus text);
SOME i
end
val _ = Capi.transfer_focus (mainWindow,text)
val write_pos = ref 0
val prompt_pos = ref 0
fun modification_ok pos =
(fdebug (fn () => "modification_ok: pos = " ^ Int.toString pos ^
", prompt_pos = " ^ Int.toString (!prompt_pos));
not Capi.Text.read_only_before_prompt orelse pos >= !prompt_pos)
fun modification_at_current_ok () =
modification_ok
(Capi.Text.get_insertion_position text
- size (Capi.Text.get_selection text))
val text_info = (text,prompt_pos,write_pos)
fun insert_at_current s =
if modification_at_current_ok ()
then Capi.Text.insert (text, Capi.Text.get_insertion_position text, s)
else beep ()
val buttons_update_fn_ref = ref (fn () => ())
fun buttons_update_fn () = (!buttons_update_fn_ref) ()
local
fun insert_text str =
let
val str = Capi.Text.check_insertion
(text,str,!write_pos,[write_pos,prompt_pos])
val old_pos = !write_pos
val new_pos = old_pos + text_size str;
in
Capi.Text.insert (text, old_pos, str);
write_pos := new_pos;
Capi.Text.set_insertion_position (text, new_pos)
end
val inbuff as (posref,strref) = (ref 0,ref "")
fun input_fun () =
(input_flag := true;
buttons_update_fn ();
Capi.event_loop input_flag;
!input_string)
fun refill_buff () =
let val new_string = input_fun ()
in
posref := 0;
strref := new_string
end
val eof_flag = ref false
fun get_input n =
let
val string = !strref
val pointer = !posref
val len = size string
in
if !eof_flag then
""
else if pointer + n > len then
(refill_buff ();
substring (string,pointer,len-pointer) ^
get_input (n - len + pointer))
else
let val result = substring (string,pointer,n)
in
posref := (!posref + n);
result
end
end
fun do_lookahead () =
(if !eof_flag then
""
else if !posref >= size (!strref) then
(refill_buff ();
do_lookahead ())
else
substring (!strref, !posref, 1))
fun close_in () = eof_flag := true
fun closed_in () = !eof_flag
fun clear_eof () = eof_flag := false
val thisWindow = {output={descriptor=NONE,
put=
lockWindow
(fn {buf,i,sz} =>
let val els = case sz of
NONE=>size buf-i
| (SOME s)=> s
in insert_text (substring (buf,i,els));
anyRecentOutput:=true;
els
end),
get_pos=NONE,
set_pos=NONE,
can_output=NONE,
close = fn()=>()},
error ={descriptor=NONE,
put=lockWindow
(fn {buf,i,sz} =>
let val els = case sz of
NONE=>size buf-i
| (SOME s)=> s
in insert_text (substring (buf,i,els));
anyRecentOutput:=true;
els
end),
get_pos=NONE,
set_pos=NONE,
can_output=NONE,
close=fn()=>()},
input ={descriptor=NONE,
get= lockWindow (fn _ => input_fun()),
get_pos=SOME(lockWindow (fn()=> !posref)),
set_pos=SOME(lockWindow (fn i=>posref:=i)),
can_input=SOME(lockWindow (fn()=>
(!posref<size (!strref)))),
close=lockWindow(close_in)}
,
access = fn f => lockWindow f ()}
in
fun inThisWindow () =
MLWorks.Internal.StandardIO.redirectIO thisWindow
val outstream = GuiUtils.make_outstream (lockWindow insert_text)
val clear_input = lockWindow (fn ()=>
(debug "Clearing input";
posref := 0;
strref := "";
eof_flag := false))
end
fun delete_current_line text_info _ =
if modification_at_current_ok () then
replace_current_input (text_info,"")
else
beep ()
fun eof_or_delete (text,prompt_pos,write_pos) () =
let
val pos = Capi.Text.get_insertion_position text
val last_pos = Capi.Text.get_last_position text
in
if pos = last_pos andalso pos = !write_pos then
(debug "eof";
if !input_flag then
(
buttons_update_fn ();
input_flag := false)
else
beep ())
else
if modification_ok pos then
Capi.Text.replace (text, pos, pos + 1, "")
else beep ()
end
fun edit_error _ =
(let
val line = get_current_line text
val locstring =
let val lstring = get_location line
in
if isSome lstring then valOf lstring
else raise NoLocation
end
val quit_fun =
ShellUtils.edit_source
(locstring, ShellTypes.get_current_preferences shell_data)
in
quit_funs := quit_fun :: (!quit_funs)
end
handle ShellUtils.EditFailed s => message_fun ("Edit failed: " ^ s)
| NoLocation => message_fun "Edit failed: no location info found"
| Location.InvalidLocation => message_fun "Edit failed: no location info found")
fun edit_error_sens _ = isSome (get_location (get_current_line text))
local
val actions_after_input = ref []
in
fun after_input _ =
(Lists.iterate (fn f => f ()) (!actions_after_input);
actions_after_input := [])
fun add_after_input_action action =
actions_after_input := action :: !actions_after_input
end
fun do_completion start_pos =
let
val subline = get_current_subline (text,start_pos)
val use_completion_menu =
let
val preferences = ShellTypes.get_current_preferences shell_data
val Preferences.PREFERENCES
{environment_options =
Preferences.ENVIRONMENT_OPTIONS {completion_menu,...},
...} =
preferences
in
!completion_menu
end
val options = ShellTypes.get_current_options shell_data
val (sofar,completions) =
ShellUtils.get_completions
(subline, options,
UserContext.get_context
(GuiUtils.get_user_context (!local_context)))
fun replace_at(pos,str) =
if modification_ok pos then
let
val npos = pos - size sofar
in
if ((size sofar) > (size str)) then
(Capi.Text.replace (text, npos, npos + size sofar, str);
Capi.Text.set_insertion_position (text, npos + size sofar))
else
(Capi.Text.replace (text, npos, npos + size str, str);
Capi.Text.set_insertion_position (text, npos + size str))
end
else
beep ()
fun replace_fun a = replace_at (start_pos, a)
in
case completions of
[] => beep ()
| [a] => replace_fun a
| l =>
let val c = ShellUtils.find_common_completion l
in
if c = sofar then
if (use_completion_menu) then
let
val popdown =
Capi.list_select
(shell, "completions",
fn c => replace_at (start_pos, sofar ^ c))
(l,replace_fun, fn x => x)
in
Capi.set_focus text;
add_after_input_action popdown
end
else beep ()
else replace_fun c
end
end
val replace_current_input = fn s => replace_current_input (text_info,s)
fun get_input_from_stdin () =
get_input_line (text, !write_pos, write_pos)
fun get_input_to_evaluate () =
(ignore(get_input_line (text, !prompt_pos, write_pos));
get_current_input (text, !prompt_pos, write_pos))
val input_disabled = ref false
fun with_input_disabled f =
(input_disabled := true;
let
val result = f () handle exn => (input_disabled := false;raise exn)
in
input_disabled := false;
result
end)
val flush_rest = ref false
fun flush_stream () = flush_rest := true
fun get_preferences () = ShellTypes.get_current_preferences shell_data
fun use_error_browser () =
case get_preferences ()
of Preferences.PREFERENCES
{environment_options =
Preferences.ENVIRONMENT_OPTIONS {use_error_browser, ...},
...} =>
!use_error_browser
val (handler'', make_prompt) =
Shell.shell (shell_data,location_title,flush_stream)
fun handler' s =
if use_error_browser () then
let
fun report_warnings (error as Info.ERROR (severity,location,_)) =
if severity = Info.WARNING orelse Info.< (severity, Info.WARNING)
then TextIO.output (outstream, Info.string_error error ^ "\n")
else ()
in
Info.with_report_fun
(Info.make_default_options ())
report_warnings
handler''
s
end
else
handler'' (Info.make_default_options ()) s
fun handler s =
(inThisWindow();
(Capi.with_window_updates
(fn () =>
(with_input_disabled
(fn () =>
Ml_Debugger.with_debugger_type
debugger_type
(fn _ =>
ShellTypes.with_toplevel_name location_title
(fn _ => handler' s)))))))
fun time_handler x =
let
val (start_cpu, start_real) =
(Timer.startCPUTimer(), Timer.startRealTimer())
fun times_to_string(real_elapsed, {usr, sys}, gc) =
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
val time =
(Timer.checkRealTimer start_real,
Timer.checkCPUTimer start_cpu,
Timer.checkGCTime start_cpu)
in
TextIO.output (outstream, times_to_string time ^ "\n");
TextIO.flushOut outstream
end
val result =
handler x
handle
exn => (print_time (); raise exn)
in
case result
of ([], _, Shell.OK _) => ()
| _ => print_time ();
result
end
fun output_prompt () =
(claimWindow();
anyOutput:=false;
anyRecentOutput:=false;
TextIO.output
(outstream, make_prompt ("MLWorks", Shell.initial_state));
TextIO.flushOut outstream;
prompt_pos := !write_pos)
fun force_prompt () =
(TextIO.output (outstream,"\n");
output_prompt ())
fun set_context_state (motif_context) =
case contextLabel of
SOME w =>
(local_context := motif_context;
Capi.set_label_string (w,"Context: " ^ GuiUtils.get_context_name motif_context))
| NONE => ()
val _ = set_context_state motif_context
fun set_state context = (set_context_state context; force_prompt ())
val context_key =
ToolData.add_context_fn
(current_context, (set_state, get_user_options, ToolData.WRITABLE))
val _ =
quit_funs :=
(fn () => ToolData.remove_context_fn (current_context, context_key))
:: !quit_funs
fun select_context motif_context =
(set_state motif_context;
ToolData.set_current
(current_context, context_key, user_options, motif_context))
val {update_history, prev_history, next_history, history_start,
history_end, history_menu} =
GuiUtils.make_history
(user_preferences,
fn line => (replace_current_input line; buttons_update_fn ()))
val update_history = fn x => (inputBuffer:="";
update_history x)
fun finish_up str =
(output_prompt ();
TextIO.output (outstream, str);
TextIO.flushOut outstream;
buttons_update_fn ();
clear_input ())
val do_select_fn = ref (fn () => ())
fun highlight (str, loc, b, offset) =
let
val (s_pos, e_pos) = Info.Location.extract (loc, str)
in
Capi.Text.set_highlight
(text,
s_pos + !prompt_pos - offset,
e_pos + !prompt_pos - offset,
b)
end
handle Info.Location.InvalidLocation => ()
fun error_handler
(error_list, redo_action, close_action, input, offset) =
let
fun edit_action location =
if ShellUtils.editable location then
{quit_fn = ShellUtils.edit_location (location, get_preferences()),
clean_fn = fn () => ()}
else
(highlight (input, location, true, offset);
{quit_fn = fn () => (),
clean_fn = fn () => highlight (input, location, false, offset)})
val location_file = case error_list of
[] => location_title
| Info.ERROR(_, location, _) :: _ =>
case Location.file_of_location location of
"" => location_title
| s => s
in
ErrorBrowser.create
{parent = shell,
errors = rev error_list,
file_message = location_file,
edit_action = edit_action,
editable = fn _ => true,
close_action = close_action,
redo_action = redo_action,
mk_tooldata = mk_tooldata,
get_context = get_current_user_context}
end
val evaluating = ref false;
local
val error_browser_ref = ref NONE
fun kill_error_browser () =
case !error_browser_ref
of NONE => ()
| SOME f =>
(f ();
error_browser_ref := NONE)
in
fun do_evaluate time_it =
let
val _ = kill_error_browser ()
val input = get_input_to_evaluate ()
val input = if !anyOutput then !inputBuffer ^ input
else input
val _ = releaseWindow()
val _ = fdebug (fn () => "input: " ^ input);
val end_pos = !write_pos
val prev_capi_eval = !Capi.evaluating
val _ = evaluating := true
val _ = Capi.evaluating := true
val _ = buttons_update_fn ()
val result =
(if time_it then
time_handler (input, Shell.initial_state)
else
handler (input, Shell.initial_state))
handle exn => (evaluating := false;
Capi.evaluating := prev_capi_eval;
buttons_update_fn ();
raise exn)
in
evaluating := false;
Capi.evaluating := prev_capi_eval;
buttons_update_fn ();
clean_debugger ();
case result
of ([], str, Shell.OK _) =>
(claimWindow();
if !anyRecentOutput then
(anyOutput:=true;
inputBuffer:= input;
anyRecentOutput:=false;
prompt_pos:= !write_pos)
else ())
| (_, _, Shell.TRIVIAL) =>
finish_up ""
| (l, str, Shell.INTERRUPT) =>
(update_history l;
case l of
[] => ()
| _ => (!do_select_fn) ();
TextIO.output (outstream, "Interrupt\n");
TextIO.flushOut outstream;
finish_up str)
| (l, str, Shell.DEBUGGER_TRAPPED) =>
(update_history l;
case l of
[] => ()
| _ => (!do_select_fn) ();
finish_up str)
| (l, str, Shell.OK _) =>
(update_history l;
(!do_select_fn) ();
finish_up str)
| (l, str, Shell.ERROR (_, error_list)) =>
(update_history l;
case l of
[] => ()
| _ => (!do_select_fn) ();
if use_error_browser () then
let
val offset = Lists.reducel (fn (i, s) => i + size s) (0, l)
fun print_error () =
case rev error_list
of [] => ()
| (err::_) =>
TextIO.output
(outstream, ErrorBrowser.error_to_string err ^ "\n")
in
if end_pos <> !write_pos then
(print_error ();
finish_up str;
error_browser_ref :=
SOME
(error_handler
(error_list,
fn () =>
do_evaluate time_it,
fn () =>
(update_history [str];
buttons_update_fn ();
clear_input ()),
input,
offset)))
else
error_browser_ref :=
SOME
(error_handler
(error_list,
fn () =>
do_evaluate time_it,
fn () =>
(update_history [str];
print_error ();
finish_up ""),
input,
offset))
end
else
(update_history [str];
finish_up ""))
end
handle
Shell.Exit _ => Capi.destroy shell
end
fun do_return () =
if !input_flag then
let
val input = get_input_from_stdin ()
in
(input_string := input;
buttons_update_fn ();
input_flag := false;
releaseWindow())
end
else if !input_disabled then
beep ()
else
do_evaluate false
val escape_pressed = ref false
fun do_escape () = escape_pressed := true
fun check_copy_selection _ =
Capi.clipboard_set (text,Capi.Text.get_selection text)
fun check_paste_selection _ =
if modification_at_current_ok ()
then
Capi.clipboard_get (text,
fn s =>
Capi.Text.insert (text,
Capi.Text.get_insertion_position text,
s))
else beep ()
fun check_cut_selection _ =
if modification_at_current_ok ()
then
let
val s = Capi.Text.get_selection text
in
Capi.Text.delete_selection text;
Capi.clipboard_set (text,s)
end
else beep ()
fun check_delete_selection _ =
if modification_at_current_ok ()
then Capi.Text.delete_selection text
else beep ()
fun abandon (text, prompt_pos, write_pos) () =
(lockWindow update_history
[get_current_input (text, !prompt_pos, write_pos)];
finish_up "")
val meta_bindings =
[("p", prev_history),
("n", next_history)]
fun delete_to_end () =
let
val ppos = !prompt_pos
val pos = Capi.Text.get_insertion_position text
val end_pos =
if pos < ppos
then Capi.Text.end_line (text,pos)
else Capi.Text.get_last_position text
in
if modification_ok pos then
(Capi.Text.set_selection (text,pos,end_pos);
check_cut_selection ())
else
beep ()
end
fun do_delete _ =
let
val sel = Capi.Text.get_selection text
val pos = Capi.Text.get_insertion_position text
in
if modification_at_current_ok() then
if sel = "" then
Capi.Text.replace(text, pos, pos + 1, "")
else
check_delete_selection()
else beep()
end
val common_bindings =
[("\t" , fn _ => do_completion (Capi.Text.get_insertion_position text)),
("\r" , do_return),
("\027", do_escape)]
val key_handlers =
{startOfLine = start_of_line text_info,
endOfLine = end_of_line text_info,
backwardChar = backward_char text_info,
forwardChar = forward_char text_info,
previousLine = previous_line text_info,
nextLine = next_line text_info,
abandon = abandon text_info,
eofOrDelete = eof_or_delete text_info,
deleteToEnd = delete_to_end,
newLine = fn _ => insert_at_current "\n",
delCurrentLine = delete_current_line text_info,
checkCutSel = check_cut_selection,
checkPasteSel = check_paste_selection}
val normal_bindings =
common_bindings @ (Capi.Text.get_key_bindings key_handlers)
fun despatch_key bindings key =
let
fun loop [] = false
| loop ((key',action)::rest) =
if key = key' then (ignore(action ()); true)
else loop rest
in
loop bindings
end
val despatch_meta = despatch_key meta_bindings
val despatch_normal = despatch_key normal_bindings
fun do_insert_text ((text,prompt_pos,write_pos),start_pos,end_pos,str) =
(fdebug (fn _ =>
"Verify: start_pos is " ^ Int.toString start_pos ^
", end_pos is " ^ Int.toString end_pos ^
", write_pos is " ^ Int.toString (!write_pos) ^
", prompt_pos is " ^ Int.toString (!prompt_pos) ^
", string is '" ^ str ^ "'");
if end_pos < !write_pos
then write_pos := (!write_pos) - end_pos + start_pos + text_size str
else if start_pos < !write_pos then
write_pos := start_pos + text_size str
else ();
if end_pos < !prompt_pos
then prompt_pos := (!prompt_pos) - end_pos + start_pos + text_size str
else if start_pos < !prompt_pos
then prompt_pos := start_pos + text_size str
else ())
fun modifyVerify (start_pos,end_pos,str,set_fn) =
let
val _ = after_input ()
in
if !escape_pressed andalso size str = 1
then
(escape_pressed := false;
set_fn false;
ignore(despatch_meta str);
())
else if not Capi.Text.read_only_before_prompt orelse
start_pos >= !prompt_pos then
(do_insert_text (text_info,start_pos,end_pos,str);
set_fn true)
else
(beep ();
set_fn false)
end
fun bad_key key =
not (("\000" <= key andalso key <= "\007") orelse
("\009" <= key andalso key <= "\031")) andalso
Capi.Text.read_only_before_prompt andalso
let
val pos = if key = "\008" then !prompt_pos+1 else !prompt_pos
in
Capi.Text.get_insertion_position text < pos
end
fun text_handler (key,modifiers) =
(debug ("Text handler: " ^ MLWorks.String.ml_string (key,100));
after_input ();
if bad_key key then
(beep (); true)
else if !escape_pressed then
(escape_pressed := false; despatch_meta key)
else if Lists.member (Capi.Event.meta_modifier, modifiers) then
despatch_meta key
else
despatch_normal key)
fun close_window _ =
if not (!evaluating) then
(do_quit_funs ();
Capi.destroy shell)
else
()
fun get_user_context () = GuiUtils.get_user_context (!local_context)
fun get_value () =
let
val user_context = get_user_context ()
in
ShellUtils.value_from_user_context (user_context,user_options)
end
val inspect_fn = InspectorTool.inspect_value (shell,false,mk_tooldata())
val _ =
do_select_fn :=
(fn () =>
case get_value () of
SOME x => inspect_fn true x
| _ => ())
val searchButtonSpec =
GuiUtils.search_button
(shell, fn _ => !local_context, insert_at_current, true)
val value_menu =
GuiUtils.value_menu
{parent = shell,
user_preferences = user_preferences,
inspect_fn = SOME (inspect_fn false),
get_value = get_value,
enabled = true,
tail = []}
val view_options =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = fn _ => (),
view_type =
[GuiUtils.SENSITIVITY,
GuiUtils.VALUE_PRINTER,
GuiUtils.INTERNALS]}
fun use_action _ =
case Capi.open_file_dialog (shell, ".sml", false)
of NONE => ()
| SOME [] => ()
| SOME (s::rest) =>
(replace_current_input ("use");
insert_at_current (" \"" ^ MLWorks.String.ml_string (s, ~1) ^ "\";");
do_evaluate false)
fun evaluate_fn () = (Capi.set_focus text;
do_evaluate false)
fun step_fn () = (Trace.set_stepping true;
Capi.set_focus text;
do_evaluate false;
Trace.set_stepping false)
fun time_fn () = (Capi.set_focus text;
do_evaluate true)
fun profile_fn () = (profiling := true;
Capi.set_focus text;
do_evaluate false;
profiling := false)
fun clear_fn () = (delete_current_line text_info ();
Capi.set_focus text)
fun abandon_fn () = (abandon text_info ();
Capi.set_focus text)
fun previous_fn () = (prev_history ();
Capi.set_focus text)
fun next_fn () = (next_history ();
Capi.set_focus text)
val file_menu = ToolData.file_menu
[("use", use_action, fn _ => not (!evaluating)),
("save", fn _ =>
GuiUtils.save_history (false, get_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", close_window, fn _ => not (!evaluating) andalso podiumExists)]
val view = ToolData.extract view_options
val values = ToolData.extract value_menu
val search = ToolData.extract
(Menus.CASCADE ("dummy", [searchButtonSpec], fn () => false))
val usage = view @ values @ search
val menuspec =
[file_menu,
ToolData.edit_menu (shell,
{cut = SOME (check_cut_selection),
paste = SOME (check_paste_selection),
copy = SOME (check_copy_selection),
delete = SOME (check_delete_selection),
edit_possible = fn _ => modification_at_current_ok (),
selection_made = fn _ => Capi.Text.get_selection text <> "",
edit_source = [value_menu],
delete_all = SOME ("deleteAll", fn _ => (prompt_pos := 0;
write_pos := 0;
Capi.Text.set_string (text, "");
finish_up "";
Capi.set_focus text),
fn _ => not (!evaluating)) }),
ToolData.tools_menu (mk_tooldata, get_current_user_context),
ToolData.usage_menu (usage, []),
Menus.CASCADE ("listener_menu",
[Menus.PUSH ("evaluate", evaluate_fn, fn _ => not (!evaluating)),
Menus.PUSH ("stepEval", step_fn, fn _ => not (!evaluating)),
Menus.PUSH ("time", time_fn, fn _ => not (!evaluating)),
Menus.PUSH ("profile", profile_fn, fn _ => not (!evaluating)),
Menus.SEPARATOR,
Menus.PUSH ("clear_def", clear_fn, fn _ => not (!evaluating)),
Menus.PUSH ("abandon", abandon_fn, fn _ => not (!evaluating)),
Menus.PUSH ("previous_def", previous_fn,
fn () => not (!evaluating) andalso not (history_start ())),
Menus.PUSH ("next_def", next_fn,
fn () => not (!evaluating) andalso not (history_end ())),
GuiUtils.listener_properties (shell, fn _ => (!local_context))],
fn _ => true),
ToolData.debug_menu values]
@ (if full_menus then
[GuiUtils.context_menu
{set_state = select_context,
get_context = fn _ => !local_context,
writable = GuiUtils.WRITABLE,
applicationShell = applicationShell,
shell = shell,
user_preferences = user_preferences}]
else
[])
@ [history_menu]
val {update, ...} =
Menus.make_buttons
(buttonPane,
[Menus.PUSH ("evaluateButton", evaluate_fn, fn _ => not (!evaluating)),
Menus.PUSH ("stepButton", step_fn, fn _ => not (!evaluating)),
Menus.PUSH ("timeButton", time_fn, fn _ => not (!evaluating)),
Menus.PUSH ("profileButton", profile_fn, fn _ => not (!evaluating)),
Menus.PUSH ("clearButton", clear_fn, fn _ => not (!evaluating)),
Menus.PUSH ("abandonButton", abandon_fn, fn _ => not (!evaluating)),
Menus.PUSH ("prevButton", previous_fn,
fn _ => not (!evaluating) andalso not (history_start ())),
Menus.PUSH ("nextButton", next_fn,
fn _ => not (!evaluating) andalso not (history_end ()))]);
fun with_no_listener f arg1 arg2 =
let
val listener = !listener_tool
val currentIO = MLWorks.Internal.StandardIO.currentIO()
in
listener_tool := NONE;
MLWorks.Internal.StandardIO.resetIO();
ignore(
f arg1 arg2 handle exn => (listener_tool := listener;
MLWorks.Internal.StandardIO.redirectIO currentIO;
raise exn));
listener_tool := listener;
MLWorks.Internal.StandardIO.redirectIO currentIO
end
fun store_size_pos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
in
quit_funs := store_size_pos :: (!quit_funs);
SaveImage.add_with_fn with_no_listener;
Capi.Text.add_del_handler(text, do_delete);
listener_tool := SOME shell;
buttons_update_fn_ref := update;
quit_funs := Menus.quit :: (!quit_funs);
Menus.make_submenus (menuBar,menuspec);
Capi.Layout.lay_out
(mainWindow, !sizeRef,
[Capi.Layout.MENUBAR menuBar] @
(case contextLabel of
SOME w => [Capi.Layout.FIXED w]
| _ => [Capi.Layout.SPACE]) @
(if podiumExists then [] else
[Capi.Layout.FIXED (valOf interrupt_button),
Capi.Layout.SPACE]) @
[Capi.Layout.FLEX scroll,
Capi.Layout.SPACE,
Capi.Layout.FIXED buttonPane,
Capi.Layout.SPACE]);
Capi.Text.add_handler (text, text_handler);
Capi.Text.add_modify_verify (text, modifyVerify);
Capi.set_close_callback(mainWindow, close_window);
Capi.Callback.add (shell, Capi.Callback.Destroy,do_quit_funs);
Capi.initialize_toplevel shell;
buttons_update_fn ();
if podiumExists then
()
else
Capi.register_interrupt_widget (valOf interrupt_button);
Capi.set_focus text;
output_prompt ()
end
fun create podium tooldata =
if isSome (!listener_tool) then
Capi.to_front (valOf (!listener_tool))
else
create_listener podium tooldata
end;
