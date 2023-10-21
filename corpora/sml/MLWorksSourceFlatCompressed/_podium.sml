require "^.basis.__string";
require "^.utils.__messages";
require "^.utils.__terminal";
require "../main/user_options";
require "../main/preferences";
require "../main/version";
require "../main/license";
require "../main/proj_file";
require "../debugger/ml_debugger";
require "^.gui.capi";
require "^.gui.menus";
require "^.gui.debugger_window";
require "^.gui.tooldata";
require "^.gui.listener";
require "^.gui.gui_utils";
require "^.gui.break_trace";
require "^.gui.browser_tool";
require "^.gui.context";
require "^.gui.sys_messages";
require "^.gui.proj_workspace";
require "^.gui.proj_properties";
require "^.gui.path_tool";
require "../interpreter/save_image";
require "podium";
functor Podium (
structure Capi: CAPI
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure License : LICENSE
structure Version : VERSION
structure Debugger_Window : DEBUGGERWINDOW
structure ToolData : TOOL_DATA
structure Menus : MENUS
structure Listener: LISTENER
structure BrowserTool : BROWSERTOOL
structure ProjectWorkspace : PROJECT_WORKSPACE
structure ContextHistory : CONTEXT_HISTORY
structure GuiUtils : GUI_UTILS
structure Ml_Debugger : ML_DEBUGGER
structure SaveImage : SAVE_IMAGE
structure BreakTrace : BREAK_TRACE
structure SysMessages : SYS_MESSAGES
structure ProjProperties : PROJ_PROPERTIES
structure PathTool : PATH_TOOL
structure ProjFile : PROJ_FILE
sharing Ml_Debugger.ValuePrinter.Options = ToolData.ShellTypes.Options
sharing type Ml_Debugger.preferences = ToolData.ShellTypes.preferences
sharing type Listener.ToolData = ToolData.ToolData =
ProjectWorkspace.ToolData = BrowserTool.ToolData =
ContextHistory.ToolData = Debugger_Window.ToolData =
BreakTrace.ToolData = SysMessages.ToolData
sharing type Menus.Widget = ToolData.Widget = ProjProperties.Widget =
GuiUtils.Widget = Debugger_Window.Widget = Capi.Widget =
PathTool.Widget
sharing type GuiUtils.ButtonSpec = ToolData.ButtonSpec = Menus.ButtonSpec
sharing type GuiUtils.user_tool_options = ToolData.ShellTypes.user_options =
UserOptions.user_tool_options
sharing type GuiUtils.user_context_options =
UserOptions.user_context_options =
ToolData.UserContext.user_context_options
sharing type Preferences.user_preferences = GuiUtils.user_preferences =
ToolData.ShellTypes.user_preferences
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type Ml_Debugger.debugger_window = Debugger_Window.debugger_window
sharing type ToolData.ShellTypes.ShellData = SaveImage.ShellData
): PODIUM =
struct
structure ShellTypes = ToolData.ShellTypes
structure UserContext = ToolData.UserContext
type ListenerArgs = ShellTypes.ListenerArgs
val tool_list =
[("listener", Listener.create true, ToolData.WRITABLE),
("projWorkspace", ProjectWorkspace.create, ToolData.WRITABLE),
("contextBrowser", BrowserTool.create, ToolData.ALL),
("initialBrowser", BrowserTool.create_initial, ToolData.ALL),
("contextWindow", ContextHistory.create, ToolData.ALL),
("breakTrace", BreakTrace.create, ToolData.ALL)]
fun start_x_interface args has_controlling_tty =
let
val print_message = Messages.output
fun put_string {buf, i, sz} =
let val s = String.extract(buf, i, sz) in print_message s; size s end
val _ = MLWorks.Internal.StandardIO.redirectIO
{output={descriptor=NONE,
put=put_string,
get_pos = NONE,
set_pos = NONE,
can_output=NONE,
close = fn()=>()},
error={descriptor=NONE,
put=put_string,
get_pos = NONE,
set_pos = NONE,
can_output=NONE,
close=fn()=>()},
input={descriptor=NONE,
get=fn _ => "",
get_pos=NONE,
set_pos=NONE,
can_input=NONE,
close=fn()=>()},
access=fn f=> f ()}
val applicationShell = Capi.initialize_application ("mlworks","MLWorks",
has_controlling_tty)
val ShellTypes.LISTENER_ARGS
{user_options, user_preferences, user_context, ...} = args
val (full_menus, update_fns) =
case user_preferences
of Preferences.USER_PREFERENCES ({full_menus, ...}, update_fns) =>
(!full_menus, update_fns)
val (mainWindow,menuBar,contextLabel) =
Capi.make_main_subwindows (applicationShell,full_menus)
fun set_context_label motif_context =
case contextLabel
of SOME w =>
let
val context_name =
GuiUtils.get_context_name motif_context
val string = "Context: " ^ context_name
in
Capi.set_label_string (w,string)
end
| NONE => ()
fun get_user_options () = user_options
val _ = GuiUtils.makeInitialContext
(applicationShell, user_preferences)
val current_context =
ToolData.make_current
(GuiUtils.make_context
(user_context, applicationShell, user_preferences))
fun get_context () = ToolData.get_current current_context
fun copy_args (ShellTypes.LISTENER_ARGS {user_context,
user_options,
user_preferences,
prompter,
mk_xinterface_fn}) =
ShellTypes.LISTENER_ARGS
{user_context =
GuiUtils.get_user_context (get_context ()),
user_preferences = user_preferences,
user_options = UserOptions.copy_user_tool_options user_options,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn}
val appdata =
ToolData.APPLICATIONDATA
{applicationShell = applicationShell,
has_controlling_tty = has_controlling_tty}
fun mk_tooldata () =
ToolData.TOOLDATA
{args = copy_args args, appdata = appdata,
motif_context = get_context (),
current_context = current_context, tools = tool_list}
val context_key =
ToolData.add_context_fn
(current_context, (set_context_label, get_user_options, ToolData.ALL))
fun get_current_user_context () =
GuiUtils.get_user_context (get_context ())
fun get_user_context_options () =
UserContext.get_user_options (get_current_user_context ())
local
fun handler_fn msg = Capi.send_message (applicationShell, msg)
in
fun save_image _ =
( let val UserOptions.USER_CONTEXT_OPTIONS
({generate_debug_info, generate_variable_debug_info, ...},
_) =
get_user_context_options ()
in if !generate_debug_info orelse !generate_variable_debug_info
then Capi.send_message(applicationShell,
"Enabling the debug options " ^
"may result in large saved images")
else ()
end;
case Capi.save_as_dialog (applicationShell, ".img")
of NONE => ()
| SOME filename =>
SaveImage.saveImage
(false, handler_fn)
(filename, false) )
end
val messagesWindow = SysMessages.create (mk_tooldata())
fun wrapUpdate condition_fn =
(ProjectWorkspace.updateDisplay();
if condition_fn applicationShell then
ProjectWorkspace.updateDisplay()
else ())
fun new_project () =
let
fun create_new appShell =
let val wantNew = ProjProperties.new_project appShell
in
if wantNew then ProjectWorkspace.create (mk_tooldata()) else ();
wantNew
end
in
wrapUpdate create_new
end
fun open_project () =
let
fun open_it appShell =
ProjProperties.open_project appShell
(fn () => ProjectWorkspace.create (mk_tooldata()))
in
wrapUpdate open_it
end
fun save_project () = wrapUpdate ProjProperties.save_project
fun save_project_as () = wrapUpdate ProjProperties.save_project_as
fun project_exists () = isSome (ProjFile.getProjectName())
val file_menu = ToolData.set_global_file_items
([("new_proj", new_project, fn _ => true),
("open_proj", open_project, fn _ => true),
("save_proj", save_project, project_exists),
("save_proj_as", save_project_as, project_exists),
("save",
fn _ =>
GuiUtils.save_history
(false, get_current_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_current_user_context ()))
andalso UserContext.saved_name_set
(get_current_user_context ())),
("saveAs",
fn _ =>
GuiUtils.save_history
(true, get_current_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_current_user_context ()))),
("saveImage", save_image, fn _ => true),
("setWD", fn _ => PathTool.setWD applicationShell, fn _ => true),
("exit",
fn _ => ToolData.exit_mlworks (applicationShell, appdata),
fn _ => not (!Capi.evaluating))])
fun pushButtons [] = []
| pushButtons (one::rest) =
(if one = "sep" then
Menus.SEPARATOR
else
Menus.PUSH (one, fn () => (), fn () => false))
:: pushButtons rest
val listener_menu =
(pushButtons ["evaluate", "stepEval", "time", "profile",
"sep",
"clear_def", "abandon", "previous_def", "next_def",
"sep"])
@ [Menus.CASCADE ("listen_props",
pushButtons ["mode", "compiler", "language"],
fn _ => true)]
val usage_menu =
ToolData.set_global_usage_items
( (GuiUtils.setup_menu
(mainWindow, get_context, user_preferences,get_user_context_options)) @
[("sysMessages", messagesWindow, fn _ => true)],
[])
val debug_menu = ToolData.debug_menu []
val edit_menu =
ToolData.edit_menu
(applicationShell,
{cut = NONE,
paste = NONE,
copy = NONE,
delete = NONE,
selection_made = fn _ => false,
edit_possible = fn _ => false,
edit_source = pushButtons ["editSource"],
delete_all = SOME ("deleteAll", fn () => (), fn () => false) })
val project_menu =
Menus.CASCADE ("project_menu",
(pushButtons ["compile", "compile_all", "load",
"load_targets", "recompile", "reload",
"sep",
"check",
"sep"]) @
[Menus.CASCADE
("show",
pushButtons ["check_compile", "check_build",
"check_load", "check_targets", "show_graph"],
fn () => true),
Menus.CASCADE
("proj_properties",
pushButtons ["prop_files", "prop_target", "prop_subproj",
"prop_mode", "prop_config", "prop_lib",
"prop_obj_dir", "prop_about"],
fn () => true)] @
(pushButtons ["sep", "deleteSelection",
"removeAllUnits", "clear_console"]),
fn () => true)
val menuspec =
[file_menu,
edit_menu,
ToolData.tools_menu (mk_tooldata, get_current_user_context),
usage_menu,
Menus.CASCADE ("listener_menu", listener_menu, fn _ => false),
project_menu,
debug_menu,
Menus.DYNAMIC ("history", fn () => [], fn _ => false)]
val (run_debugger, clean_debugger) =
Debugger_Window.make_debugger_window
(applicationShell, "MLWorks Debugger", mk_tooldata ())
val debugger_type =
Ml_Debugger.WINDOWING
(run_debugger,
print_message, false)
fun stop_messages () = Capi.no_message_widget ()
fun delivery_hook deliverer args =
let
fun inDeliveredImage f =
let
val oldIO = MLWorks.Internal.StandardIO.currentIO()
val _ = MLWorks.Internal.StandardIO.resetIO();
val result = (f() handle exn =>
(MLWorks.Internal.StandardIO.redirectIO oldIO; raise exn))
in
MLWorks.Internal.StandardIO.redirectIO oldIO;
result
end
in
(fn () => inDeliveredImage (fn () => deliverer args)) ()
end
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
val default_to_free : unit -> unit =
MLWorks.Internal.Runtime.environment "license set edition"
fun license_ok () =
has_controlling_tty orelse
(
let
val license_status =
License.license (Capi.license_complain applicationShell)
in
(case license_status of
SOME false => default_to_free ()
| _ => ());
if SaveImage.showBanner() then
print_message (Version.versionString() ^ "\n")
else ();
(case license_status of
SOME _ => true
| _ => false)
end
)
fun mainLoop frame =
let
val loop =
(Capi.main_loop (); false)
handle
MLWorks.Interrupt => true
| ShellTypes.DebuggerTrapped => true
| Capi.SubLoopTerminated => false
| exn =>
(debugger_function exn;
clean_debugger ();
true)
in
if loop then mainLoop frame else stop_messages()
end
fun mk_toolbutton (style, states, tip_id, name) =
Menus.TOOLBUTTON {style = style, states = states, tooltip_id = tip_id, name = name}
fun push (name, id) = mk_toolbutton (Menus.TB_PUSH, [Menus.ENABLED], id, name)
fun toggle (name, id) = mk_toolbutton (Menus.TB_TOGGLE, [Menus.ENABLED], id, name)
val separator = mk_toolbutton (Menus.TB_SEP, [Menus.GRAYED], 0, "")
val podium_buttons =
[push ("new_proj", 2000), push ("open_proj", 2001), push ("save_proj", 2002),
separator,
push ("cut", 2005), push ("copy", 2006),
push ("paste", 2007), push ("delete", 2008),
separator,
push ("listener", 2010), push ("projWorkspace", 2011),
push ("contextBrowser", 2012), push ("initialBrowser", 2013),
push ("contextWindow", 2014), push ("breakTrace", 2015),
separator,
push ("compile_all", 2030), push ("check", 2031),
push ("removeAllUnits", 2032),
separator,
push ("inspect", 2035), push ("repeat", 2036), push ("search", 2037),
push ("duplicate", 2038), toggle ("autoSelection", 2039),
separator,
push ("abort", 2045), push ("continue", 2046),
push ("step", 2047), push ("next", 2048),
push ("trace", 2049), push ("untrace", 2050),
separator,
push ("interruptButton", 2060)]
val _ = Menus.make_menus(menuBar, menuspec, true );
val toolbar = Menus.make_toolbar(mainWindow, 109, podium_buttons)
in
if (license_ok ()) then
(Capi.Layout.lay_out
(mainWindow, NONE,
[Capi.Layout.MENUBAR menuBar] @
(case contextLabel of
NONE => [Capi.Layout.SPACE]
| SOME w => [Capi.Layout.FIXED w]) @
[Capi.Layout.FIXED toolbar,
Capi.Layout.SPACE]);
Capi.show_splash_screen applicationShell;
Capi.reveal toolbar;
Capi.initialize_application_shell applicationShell;
set_context_label (get_context ());
Capi.evaluating := false;
Listener.create true (mk_tooldata());
if isSome(ProjFile.getProjectName()) then
ProjectWorkspace.create (mk_tooldata())
else ();
MLWorks.Deliver.with_delivery_hook delivery_hook
(Ml_Debugger.with_debugger_type debugger_type) mainLoop;
())
else
(Capi.destroy applicationShell;
MLWorks.Internal.StandardIO.resetIO())
end
handle Capi.WindowSystemError s =>
Terminal.output("Graphics interface problem: "^s^"\n")
end;
