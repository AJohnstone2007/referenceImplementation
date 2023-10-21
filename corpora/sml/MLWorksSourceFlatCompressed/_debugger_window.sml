require "capi";
require "menus";
require "../utils/lists";
require "^.utils.__messages";
require "../main/preferences";
require "../debugger/newtrace";
require "../debugger/ml_debugger";
require "../typechecker/types";
require "tooldata";
require "inspector_tool";
require "gui_utils";
require "file_viewer";
require "../interpreter/shell_utils";
require "../debugger/stack_frame";
require "debugger_window";
require "../main/user_options";
require "^.basis.__string";
functor DebuggerWindow(
structure Capi : CAPI
structure Lists : LISTS
structure Trace : TRACE
structure InspectorTool : INSPECTORTOOL
structure FileViewer : FILE_VIEWER
structure UserOptions : USER_OPTIONS
structure Types : TYPES
structure Menus : MENUS
structure GuiUtils: GUI_UTILS
structure ShellUtils: SHELL_UTILS
structure ToolData: TOOL_DATA
structure Preferences: PREFERENCES
structure StackFrame : STACK_FRAME
structure Ml_Debugger : ML_DEBUGGER
sharing UserOptions.Options = ToolData.ShellTypes.Options =
ShellUtils.Options = Types.Options
sharing type Types.Datatypes.Type = InspectorTool.Type
sharing type Types.Options.print_options = ShellUtils.Options.print_options
sharing type Menus.Widget = GuiUtils.Widget = ToolData.Widget = Capi.Widget =
InspectorTool.Widget = FileViewer.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ToolData = InspectorTool.ToolData = FileViewer.ToolData
sharing type ToolData.ShellTypes.user_preferences =
Preferences.user_preferences
sharing type ShellUtils.preferences = Preferences.preferences
sharing type FileViewer.Location.T = ShellUtils.Info.Location.T
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type UserOptions.user_tool_options = ToolData.ShellTypes.user_options
sharing type GuiUtils.user_context = ToolData.UserContext.user_context
sharing type UserOptions.user_context_options = ToolData.UserContext.user_context_options
) : DEBUGGERWINDOW =
struct
structure Options = ShellUtils.Options
structure UserContext = ToolData.UserContext
type Widget = Capi.Widget
type ToolData = InspectorTool.ToolData
type Type = InspectorTool.Type
local
type part_of_a_frame =
(string
* (Type * MLWorks.Internal.Value.ml_value * string)
) list
type frame_details =
string
* string
* (Type * MLWorks.Internal.Value.ml_value * string)
* (unit -> string * part_of_a_frame,
string * part_of_a_frame)
Ml_Debugger.union ref option
type frame =
{name : string, loc : string, details: frame_details}
in
datatype Frame =
FRAME of frame
type debugger_window =
{parameter_details: string,
frames: frame list,
quit_fn: (unit -> unit) option,
continue_fn: (unit -> unit) option,
top_ml_user_frame: MLWorks.Internal.Value.Frame.frame option}
-> unit
end
fun make_debugger_window (parent,title,tooldata) =
let
val ToolData.TOOLDATA
{args as ToolData.ShellTypes.LISTENER_ARGS
{user_preferences as Preferences.USER_PREFERENCES
({full_menus, ...}, _),
user_options,user_context,prompter,mk_xinterface_fn,...},
appdata as ToolData.APPLICATIONDATA {applicationShell, ...},
current_context, motif_context, tools, ...} =
tooldata
fun get_user_context_options () =
UserContext.get_user_options
(GuiUtils.get_user_context (motif_context))
fun get_compiler_options () =
UserOptions.new_options (user_options, get_user_context_options())
val title = "Stack Browser"
val show_debug_info = ref true
val show_variable_debug_info = ref(true)
val visible = ref false
val (shell,form,menuBar,_) =
Capi.make_main_popup
{name = "debugger",
title = title,
parent = parent,
contextLabel = false,
visibleRef = visible,
pos = Capi.getNextWindowPos()}
val continue = ref true
fun popup () =
(Capi.reveal form;
Capi.to_front shell;
Capi.add_main_window (shell, title);
visible := true)
fun popdown () =
(Capi.hide form;
visible := false)
val buttonPane =
Capi.make_managed_widget ("buttonPane", Capi.RowColumn, form, [])
val text = Capi.make_managed_widget
("debuggerText", Capi.Text, form,[])
val debuggerFrame =
Capi.make_managed_widget
("debuggerFrame", Capi.Paned, form, [Capi.PanedMargin true])
val varPane =
Capi.make_managed_widget ("varPane", Capi.Form, debuggerFrame, []);
val argsLabel =
Capi.make_managed_widget
("debuggerArgsLabel", Capi.Label, varPane, [])
val argsText =
Capi.make_managed_widget
("debuggerArgsText", Capi.Text, varPane, [])
val framePane =
Capi.make_managed_widget ("framePane", Capi.Form, debuggerFrame, []);
val framesLabel =
Capi.make_managed_widget
("debuggerFramesLabel", Capi.Label, framePane, [])
val quit_fns = ref [fn () => Capi.remove_main_window shell];
fun do_quit_fns () = Lists.iterate (fn f => f ()) (!quit_fns)
local
val viewer_fn_ref = ref NONE
in
fun viewer_fn auto location =
case !viewer_fn_ref
of SOME f =>
f auto (FileViewer.LOCATION location)
| NONE =>
let
val the_fn = FileViewer.create (parent, true, tooldata)
in
viewer_fn_ref := SOME (the_fn);
quit_fns :=
(fn _ => viewer_fn_ref := NONE) :: !quit_fns;
the_fn false (FileViewer.LOCATION location)
end
end
val frames_ref = ref [] : Frame list ref
val displayed_frames = ref [] : Frame list ref
val present_name = ref "";
val logical_top_frame : MLWorks.Internal.Value.Frame.frame option ref =
ref NONE
val present_frame_info : (InspectorTool.Type * MLWorks.Internal.Value.ml_value
* string) option ref =
ref NONE
val present_variable_frame_info :
(unit -> string * ((string * (InspectorTool.Type * MLWorks.Internal.Value.ml_value
* string)) list),
string * ((string * (InspectorTool.Type * MLWorks.Internal.Value.ml_value
* string)) list)) Ml_Debugger.union ref ref =
ref(ref(Ml_Debugger.INR("",[])))
val inspect_fn = InspectorTool.inspect_value (parent,true,tooldata)
fun fetch_frame_info (info' as ref(Ml_Debugger.INL info_fn)) =
let val info as (_,info'') = info_fn ()
in
info' := Ml_Debugger.INR info;
info''
end
| fetch_frame_info (ref(Ml_Debugger.INR(_,info))) = info
fun exists_frame_info () =
case (!present_frame_info,
fetch_frame_info (!present_variable_frame_info))
of (NONE, []) => false
| _ => true
fun print_frames [] =
Capi.send_message (shell, "See System Messages window for backtrace")
| print_frames (frame1::rest) =
let
val FRAME {details, ...} = frame1
val (name, _, (_, _, info), _) = details
in
Messages.output(name ^ info ^ "\n");
print_frames rest
end
val max_length = 80
fun strip str =
if size str <= max_length then str
else substring (str,0,max_length-3) ^ "..."
val current_var = ref NONE
fun exists_current_var () =
case !current_var
of NONE => false
| _ => true
fun inspect_current_var () =
case !current_var
of NONE => ()
| SOME (var,(ty,value,valuestr)) =>
inspect_fn false (var,(value,ty))
val {scroll = varsScroll, list = varsList,
set_items = set_var_items, ...} =
Capi.make_scrolllist
{parent = varPane,
name = "debuggerVars",
select_fn = fn _ =>
fn x as (var,(ty,value,valuestr)) =>
(current_var := SOME x;
inspect_fn true (var,(value,ty))),
action_fn = fn _ =>
fn (var,(ty,value,valuestr)) =>
inspect_fn false (var,(value,ty)),
print_fn = fn _ =>
fn (var,(ty,value,valuestr)) =>
"val " ^ var
^ ": " ^ Types.print_type (get_compiler_options()) ty ^
" = " ^ strip valuestr}
fun is_editable ("<Cframe>", _) = false
| is_editable ("<Setup>", _) = false
| is_editable (_, location) = ShellUtils.editable location
fun show_fn (auto, name, loc) =
let
val location = ShellUtils.Info.Location.from_string loc
in
if is_editable (name, location) then
(ShellUtils.show_source
(loc, Preferences.new_preferences user_preferences))
handle ShellUtils.EditFailed s =>
(viewer_fn auto location
handle FileViewer.ViewFailed filename =>
if not auto then
Capi.send_message (shell, "Cannot view: " ^ filename)
else ())
else
()
end
fun edit_fn (name, loc) =
let
val location = ShellUtils.Info.Location.from_string loc
in
if is_editable (name, location) then
let
val quit_fn =
ShellUtils.edit_source
(loc, Preferences.new_preferences user_preferences)
in
quit_fns := quit_fn :: !quit_fns
end
handle ShellUtils.EditFailed s =>
Capi.send_message (shell, "Edit failed: " ^ s)
else
Capi.send_message (shell, "Can't edit: " ^ loc)
end
fun show_vars NONE = ()
| show_vars (SOME info') =
(present_variable_frame_info := info';
if !show_variable_debug_info then
case !present_frame_info of
NONE =>
set_var_items
Options.default_print_options
(fetch_frame_info(!present_variable_frame_info))
| SOME x =>
set_var_items
Options.default_print_options
(("frame argument", x)
:: fetch_frame_info (!present_variable_frame_info))
else
set_var_items Options.default_print_options [])
fun frame_select_fn _ frame =
let val FRAME
{name, loc, details = (a,b,(ty,value,valuestr),info'),...} =
frame
in
(case valuestr of
"" => ()
| "_" => ()
| _ => present_frame_info := SOME(ty,value,valuestr));
present_name := name;
Capi.Text.set_string (argsText, b);
show_vars info';
show_fn (true, name, loc)
end
val {scroll=framesScroll, list=framesList,
set_items=set_frame_items, ...} =
Capi.make_scrolllist
{parent = framePane,
name = "debuggerFrames",
select_fn = frame_select_fn,
action_fn = fn _ => fn FRAME{name, loc, ...} =>
(show_fn (false, name, loc);
Capi.set_focus shell),
print_fn =
fn _ =>
fn FRAME {details = (a,_,(_,_,info),_),...} =>
a ^ (if !show_debug_info then info else "")
}
fun getter r () = !r
fun setter r b = (r := b; true)
fun toggle (s,r) = Menus.OPTTOGGLE(s,getter r, setter r)
val settings_spec =
toggle ("hideAnonymousFrames", StackFrame.hide_anonymous_frames)
:: toggle ("hideHandlerFrames", StackFrame.hide_handler_frames)
:: (if !full_menus then
[toggle ("hideSetupFrames", StackFrame.hide_setup_frames),
toggle ("hideCFrames", StackFrame.hide_c_frames),
toggle ("hideDeliveredFrames",
StackFrame.hide_delivered_frames),
toggle ("hideDuplicateFrames",
StackFrame.hide_duplicate_frames)]
else
nil)
local
fun classify "<Cframe>" (cframe, _, _, _, _) = cframe ()
| classify "<Setup>" (_, setup, _, _, _) = setup ()
| classify "<anon>" (_, _, anon, _, _) = anon ()
| classify "<handle>" (_, _, _, handler, _) = handler ()
| classify _ (_, _, _, _, user) = user ()
fun user_frame loc = size loc > 0 andalso String.sub (loc, 0) <> #" "
in
fun filter ([], acc, _) = acc
| filter ((f as FRAME{name,loc,...})::rest, acc, previousDelivered) =
let
fun keep_it () = filter (rest, f::acc, false)
fun skip_it () = filter (rest, acc, false)
fun loop var = if var then skip_it () else keep_it ()
fun cframe () = loop (!StackFrame.hide_c_frames)
fun setup () = loop (!StackFrame.hide_setup_frames)
fun anon () = loop (!StackFrame.hide_anonymous_frames orelse (previousDelivered andalso (!StackFrame.hide_duplicate_frames)))
fun handler () = loop (!StackFrame.hide_handler_frames)
fun delivered () = loop (!StackFrame.hide_delivered_frames)
fun user () =
if user_frame loc then
loop (previousDelivered andalso (!StackFrame.hide_duplicate_frames))
else
if (!StackFrame.hide_delivered_frames)
then filter (rest, acc, true)
else filter (rest, f::acc, true)
in
classify name (cframe, setup, anon, handler, user)
end
fun filter_frames frames = filter (rev frames, [], false)
end
val info_settings_spec =
[toggle ("showDebugInfo",show_debug_info),
toggle ("showVariableDebugInfo", show_variable_debug_info)]
fun update_items () =
let
val frame_list = filter_frames (!frames_ref)
in
displayed_frames := frame_list;
show_vars (SOME (!present_variable_frame_info));
set_frame_items Options.default_print_options frame_list
end
fun clear_window () =
(frames_ref := [];
update_items ();
set_var_items
Options.default_print_options
[];
Capi.Text.set_string(text,"");
Capi.Text.set_string(argsText,""))
fun item_selected _ =
MLWorks.Internal.Vector.length (Capi.List.get_selected_pos framesList) = 1
fun show_callback _ =
let
val pos = Capi.List.get_selected_pos framesList
in
if MLWorks.Internal.Vector.length pos = 1
then
let
val index = MLWorks.Internal.Vector.sub (pos,0)
val FRAME{name, loc, ...} =
Lists.nth (index-1,!displayed_frames)
in
show_fn (false, name, loc)
end
handle Lists.Nth => ()
else ()
end
fun edit_callback _ =
let
val pos = Capi.List.get_selected_pos framesList
in
if MLWorks.Internal.Vector.length pos = 1
then
let
val index = MLWorks.Internal.Vector.sub (pos,0)
val FRAME{name, loc, ...} =
Lists.nth (index-1,!displayed_frames)
in
edit_fn (name, loc)
end
handle Lists.Nth => ()
else ()
end
fun can_edit _ =
let
val pos = Capi.List.get_selected_pos framesList
in
if MLWorks.Internal.Vector.length pos = 1
then
let
val index = MLWorks.Internal.Vector.sub (pos,0)
val FRAME{name, loc,...} =
Lists.nth (index-1,!displayed_frames)
val location = ShellUtils.Info.Location.from_string loc
in
is_editable (name, location)
end
handle Lists.Nth =>
false
else
false
end
val settings_popup =
#1 (Menus.create_dialog (shell,
"Debugger Settings",
"debuggerDialog",
update_items,
settings_spec))
val info_settings_popup =
#1(Menus.create_dialog (shell,
"Debugger Settings",
"debuggerDialog",
update_items,
info_settings_spec))
val quit_fn_ref = ref NONE
val continue_fn_ref = ref NONE
fun present (ref NONE) = false
| present (ref (SOME _)) = true
fun abort_present _ = present quit_fn_ref
fun continue_present _ = present continue_fn_ref
fun next_present _ = present continue_fn_ref andalso
present logical_top_frame
fun abort_action _ =
(do_quit_fns ();
case !quit_fn_ref of
NONE => ()
| SOME f =>
(continue := false;
clear_window ();
popdown();
f ()))
fun unmap_action _ =
(do_quit_fns ();
clear_window ();
visible := false;
continue := false;
case !quit_fn_ref of
NONE => ()
| SOME f => f ())
val set_active_buttons = ref (fn () => ())
fun continue_action _ =
case !continue_fn_ref of
NONE => ()
| SOME f =>
(continue_fn_ref := NONE;
continue := false;
f ())
fun step_action _ =
case !continue_fn_ref of
NONE => ()
| SOME f =>
(Trace.set_stepping true;
continue_fn_ref := NONE;
continue := false;
f ())
fun next_action _ =
case !continue_fn_ref of
NONE => ()
| SOME f =>
case !logical_top_frame of
NONE => ()
| SOME frame =>
(Trace.next frame;
continue_fn_ref := NONE;
continue := false;
f ())
val _ =
let
val {update, set_focus} =
Menus.make_buttons
(buttonPane,
[Menus.PUSH ("abortButton", abort_action, abort_present),
Menus.PUSH
("continueButton", continue_action, continue_present),
Menus.PUSH ("stepButton", step_action, continue_present),
Menus.PUSH ("nextButton", next_action, next_present)])
fun do_set_focus () =
if continue_present () then
if Trace.stepping () then
set_focus 2
else
set_focus 1
else
set_focus 0
in
set_active_buttons := (do_set_focus o update)
end
fun mk_tooldata () =
ToolData.TOOLDATA
{args = ToolData.ShellTypes.LISTENER_ARGS
{user_options = user_options,
user_preferences = user_preferences,
user_context =
GuiUtils.get_user_context motif_context,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn},
current_context = current_context,
appdata = appdata,
motif_context = motif_context,
tools = tools}
fun get_user_context () = GuiUtils.get_user_context (motif_context)
val menuspec =
[ToolData.file_menu [("save", fn _ =>
GuiUtils.save_history (false, get_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", abort_action, abort_present)],
ToolData.edit_menu
(shell, {cut = NONE, paste = NONE, copy = NONE, delete = NONE,
edit_possible = fn _ => false, selection_made = fn _ => false,
edit_source = [Menus.PUSH ("editSource",edit_callback,can_edit)],
delete_all = NONE}),
ToolData.tools_menu (mk_tooldata, get_user_context),
ToolData.usage_menu
([("show_defn",show_callback,can_edit),
("inspect", fn _ => inspect_current_var (), fn _ => exists_current_var ()),
("filterFrames", fn _ => settings_popup (), fn _ => true),
("showFrameInfo", fn _ => info_settings_popup (), fn _ => true),
("backtrace", fn _ => print_frames (!frames_ref), fn _ => true)], []),
ToolData.debug_menu [("abort",abort_action,abort_present),
("continue",continue_action,continue_present),
("step",step_action,continue_present),
("next", next_action, next_present)]]
fun run_debugger {parameter_details, frames, quit_fn, continue_fn, top_ml_user_frame} =
(frames_ref := map FRAME frames;
continue := true;
quit_fn_ref := quit_fn;
continue_fn_ref := continue_fn;
logical_top_frame := top_ml_user_frame;
update_items ();
case !displayed_frames of
[] => ()
| _ =>
Capi.List.select_pos (framesList, 1, true);
Capi.Text.set_string(text, parameter_details);
if not (!visible) then
popup()
else
();
(!set_active_buttons) ();
Capi.event_loop continue)
fun clean_debugger () =
clear_window ()
val varPaneLayout =
(varPane,
[Capi.Layout.FIXED argsLabel,
Capi.Layout.FIXED argsText,
Capi.Layout.FLEX varsScroll]);
val framePaneLayout =
(framePane,
[Capi.Layout.FIXED framesLabel,
Capi.Layout.FLEX framesScroll]);
in
quit_fns := Menus.quit :: (!quit_fns);
Menus.make_submenus (menuBar, menuspec);
Capi.Callback.add
(form, Capi.Callback.Unmap, unmap_action);
Capi.set_close_callback (form, abort_action);
Capi.Layout.lay_out
(form, NONE,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.FIXED buttonPane,
Capi.Layout.FIXED text,
Capi.Layout.PANED (debuggerFrame, [varPaneLayout, framePaneLayout]),
Capi.Layout.SPACE]);
(run_debugger, clean_debugger)
end
end;
