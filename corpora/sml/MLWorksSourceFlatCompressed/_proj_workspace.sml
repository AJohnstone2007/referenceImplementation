require "$.basis.__string";
require "$.basis.__date";
require "$.basis.__text_io";
require "^.system.__file_time";
require "../system/__os";
require "../basics/module_id";
require "../utils/map";
require "../utils/crash";
require "../basis/list";
require "../editor/editor";
require "../main/preferences";
require "../main/user_options";
require "../main/project";
require "../main/proj_file";
require "../debugger/ml_debugger";
require "../interpreter/incremental";
require "../interpreter/save_image";
require "../interpreter/shell_utils";
require "capi";
require "menus";
require "graph_widget";
require "tooldata";
require "gui_utils";
require "console";
require "debugger_window";
require "error_browser";
require "proj_properties";
require "proj_workspace";
functor ProjectWorkspace (
structure ModuleId: MODULE_ID
structure DebuggerWindow : DEBUGGERWINDOW
structure Ml_Debugger : ML_DEBUGGER
structure ErrorBrowser : ERROR_BROWSER
structure NewMap: MAP
structure List: LIST
structure UserOptions: USER_OPTIONS
structure Preferences: PREFERENCES
structure Crash: CRASH
structure Editor: EDITOR
structure Capi: CAPI
structure Incremental: INCREMENTAL
structure Project: PROJECT
structure ProjFile: PROJ_FILE
structure ShellUtils: SHELL_UTILS
structure Menus: MENUS
structure GraphWidget: GRAPH_WIDGET
structure ToolData: TOOL_DATA
structure GuiUtils: GUI_UTILS
structure Console: CONSOLE
structure ProjProperties: PROJ_PROPERTIES
structure SaveImage : SAVE_IMAGE
sharing Project.Info = ShellUtils.Info =
Incremental.InterMake.Compiler.Info
sharing Ml_Debugger.ValuePrinter.Options =
ShellUtils.Options = ToolData.ShellTypes.Options
sharing type Capi.Widget = Menus.Widget =
GraphWidget.Widget = ToolData.Widget = GuiUtils.Widget =
Console.Widget = DebuggerWindow.Widget =
ErrorBrowser.Widget = ProjProperties.Widget
sharing type Ml_Debugger.preferences = Preferences.preferences =
ShellUtils.preferences = Editor.preferences
sharing type Preferences.user_preferences = GuiUtils.user_preferences =
ToolData.ShellTypes.user_preferences = Console.user_preferences
sharing type ToolData.ShellTypes.Context = ShellUtils.Context
sharing type UserOptions.user_context_options =
ToolData.UserContext.user_context_options =
GuiUtils.user_context_options
sharing type UserOptions.user_tool_options = ShellUtils.UserOptions =
ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options
sharing type ToolData.ButtonSpec = Menus.ButtonSpec = GuiUtils.ButtonSpec
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context =
ErrorBrowser.user_context = ShellUtils.user_context
sharing type Project.Info.Location.T = ModuleId.Location =
ErrorBrowser.location = Incremental.Datatypes.Ident.Location.T
sharing type Project.ModuleId = ModuleId.ModuleId = Incremental.ModuleId
sharing type NewMap.map = Project.Map
sharing type GraphWidget.GraphicsPort = Capi.GraphicsPorts.GraphicsPort
sharing type GraphWidget.Point = Capi.Point
sharing type ToolData.ToolData = DebuggerWindow.ToolData = ErrorBrowser.ToolData
sharing type Project.Project = Incremental.InterMake.Project
sharing type Ml_Debugger.debugger_window = DebuggerWindow.debugger_window
sharing type ErrorBrowser.error = Project.Info.error
sharing type Menus.OptionSpec = GuiUtils.OptionSpec
sharing type ShellUtils.Options.options = UserOptions.Options.options
): PROJECT_WORKSPACE =
struct
structure Info = Project.Info
structure Location = Info.Location
structure ShellTypes = ToolData.ShellTypes
structure Options = ShellUtils.Options
structure UserContext = ToolData.UserContext
type ToolData = ToolData.ToolData
datatype Node =
NODE of
ModuleId.ModuleId * (string * int * int * int) option ref
val nodeName = fn (NODE (m,_)) => ModuleId.string m
fun location_file (Info.ERROR(_,location,message)) =
case location of
Location.UNKNOWN => NONE
| Location.FILE f => SOME f
| Location.LINE(f,l) => SOME f
| Location.POSITION (f,l,_) => SOME f
| Location.EXTENT {name,...} => SOME name
fun optionsFromProjFile
(Options.OPTIONS {listing_options, print_options, compat_options,
extension_options, compiler_options}) =
let
val (_, modeDetails, currentMode) = ProjFile.getModes ()
val Options.COMPILEROPTIONS {print_messages, generate_moduler,
opt_handlers, local_functions, ...} =
compiler_options
val new_compiler_options =
case currentMode of
NONE => compiler_options
| SOME name =>
(case ProjFile.getModeDetails (name, modeDetails) of
r =>
Options.COMPILEROPTIONS
{interrupt = !(#generate_interruptable_code r),
intercept = !(#generate_interceptable_code r),
generate_debug_info = !(#generate_debug_info r),
debug_variables = !(#generate_variable_debug_info r),
opt_leaf_fns = !(#optimize_leaf_fns r),
opt_tail_calls = !(#optimize_tail_calls r),
opt_self_calls = !(#optimize_self_tail_calls r),
mips_r4000 = !(#mips_r4000 r),
sparc_v7 = !(#sparc_v7 r),
print_messages = print_messages,
generate_moduler = generate_moduler,
opt_handlers = opt_handlers,
local_functions = local_functions})
in
Options.OPTIONS
{listing_options = listing_options,
print_options = print_options,
compat_options = compat_options,
extension_options = extension_options,
compiler_options = new_compiler_options}
end
val project_tool = ref NONE
fun with_no_project_tool f arg1 arg2 =
let
val pt = !project_tool
in
project_tool := NONE;
ignore(f arg1 arg2
handle exn => (project_tool := pt; raise exn));
project_tool := pt
end
val selection = ref NONE : (string * ModuleId.ModuleId) option ref
val updatePW = ref (fn () => ())
fun updateDisplay () = (!updatePW) ()
val originalProj = ref (Incremental.get_project())
val sizeRef = ref NONE
val posRef = ref NONE
val graphSizeRef = ref NONE
val graphPosRef = ref NONE
fun create_project_tool (tooldata as ToolData.TOOLDATA
{args, appdata, current_context, motif_context, tools}) =
let
val ToolData.APPLICATIONDATA {applicationShell,...} = appdata
val ShellTypes.LISTENER_ARGS
{user_options, user_preferences,
mk_xinterface_fn, prompter, ...} = args
val local_context = ref motif_context
fun get_current_user_context () =
GuiUtils.get_user_context (!local_context)
fun get_user_context_options () =
ToolData.UserContext.get_user_options (get_current_user_context ())
val title = "Project Workspace"
val (shell, frame, menubar, _) =
Capi.make_main_window
{name = "projWorkspace",
title = title,
parent = applicationShell,
contextLabel = false,
winMenu = false,
pos = getOpt(!posRef, Capi.getNextWindowPos())};
val projNameLabel = Capi.make_managed_widget
("projNameLabel", Capi.Label, frame, [])
val projCurTargetsText = Capi.make_managed_widget
("projCurTargetsText", Capi.Text, frame,
[(Capi.ReadOnly true)])
val projSubprojText = Capi.make_managed_widget
("projSubprojText", Capi.Text, frame,
[(Capi.ReadOnly true)])
val relativeObj =
let val (_, obj, _) = ProjFile.getLocations()
in
ref (OS.Path.isRelative obj)
end
val relativeBin =
let val (_, _, bin) = ProjFile.getLocations()
in
ref (OS.Path.isRelative bin)
end
val projObjectsLabel = Capi.make_managed_widget
("projObjectsLabel", Capi.Label, frame, [])
val projButtonsRC = Capi.make_managed_widget
("projButtonsRC", Capi.RowColumn, frame, [])
fun list2string [] = ""
| list2string (h::t) = h ^ "; " ^ list2string t
fun updateName (SOME name) =
Capi.set_label_string (projNameLabel, "Project Name:  " ^ name)
| updateName NONE =
Capi.set_label_string (projNameLabel, "Project Name:  ")
val files_up_to_date = ref true
val listLabel =
Capi.make_managed_widget ("listLabel", Capi.Label, frame, [])
fun update_files_label () =
if (!files_up_to_date) then
Capi.set_label_string (listLabel, "Files (information up to date):")
else
Capi.set_label_string (listLabel, "Files (information possibly out of date):")
fun updateTargets targets =
(files_up_to_date := false;
update_files_label();
Capi.Text.set_string (projCurTargetsText,
"Current Targets (sources):  " ^ list2string targets))
fun updateFiles newFiles =
updateTargets (#1 (ProjFile.getTargets()))
val (files_dialog, applyResetFiles) =
ProjProperties.mk_files_dialog (shell, updateFiles)
fun updateSubprojs projs =
Capi.Text.set_string (projSubprojText,
"Sub projects:  " ^ list2string projs)
fun update_proj orig_proj =
(if orig_proj then
(Incremental.set_project (!originalProj);
files_up_to_date := true;
update_files_label())
else ();
updateDisplay())
val (subproj_dialog, applyResetSubprojs) =
ProjProperties.mk_subprojects_dialog (shell, updateSubprojs, update_proj)
val (targets_dialog, applyResetTargets) =
ProjProperties.mk_targets_dialog (shell, updateTargets)
fun updateLibPath _ = ()
fun updateLocs (obj, bin, lib) =
(files_up_to_date := false;
update_files_label();
Capi.set_label_string (projObjectsLabel, "Location for object files: " ^
(OS.Path.fromUnixPath obj));
updateLibPath (list2string lib))
fun updateObjLoc (obj', bin', lib) =
let
val (_, modeDetails, curMode) = ProjFile.getModes()
val (_, _, curConfig) = ProjFile.getConfigurations()
val modeLoc =
if isSome(curMode) then
!(#location (ProjFile.getModeDetails (valOf(curMode), modeDetails)))
else ""
val cc = getOpt(curConfig, "")
val obj = OS.Path.mkCanonical (OS.Path.concat[obj', cc, modeLoc])
val bin = OS.Path.mkCanonical (OS.Path.concat[bin', cc, modeLoc])
in
updateLocs (obj, bin, lib)
end
fun newObjDir objDir =
let val (lib, obj, bin) = ProjFile.getLocations()
in
updateObjLoc (objDir, bin, lib)
end
fun newBinDir binDir =
let val (lib, obj, bin) = ProjFile.getLocations()
in
updateObjLoc (obj, binDir, lib)
end
val relativeRC = Capi.make_managed_widget
("PW_relativeRC", Capi.RowColumn, frame, [])
fun getRelObj () = (!relativeObj)
fun getRelBin () = (!relativeBin)
val changeObj =
ProjProperties.setRelObjBin (true, newObjDir, relativeObj)
val {update = updateRel, ...} =
Menus.make_buttons (relativeRC,
[Menus.TOGGLE ("PW_relativeObj", getRelObj, changeObj, fn _ => true)])
fun updateConfig () =
let
val (lib, obj, bin) = ProjFile.getLocations()
in
updateObjLoc (obj, bin, lib)
end
fun updateMode modes =
let
val (lib, obj, bin) = ProjFile.getLocations()
in
updateObjLoc (obj, bin, lib)
end
val (modes_dialog, applyResetModes) =
ProjProperties.mk_modes_dialog (shell, updateMode)
val (configs_dialog, applyResetConfigs) =
ProjProperties.mk_configs_dialog (shell, updateConfig)
val (library_dialog, applyResetLibPath) =
ProjProperties.mk_library_dialog (shell, updateLibPath)
fun obj_dialog () =
ProjProperties.set_objects_dir (shell, relativeObj, newObjDir)
val (about_dialog, applyResetAboutInfo) =
ProjProperties.mk_about_dialog shell
val modNameLabel =
Capi.make_managed_widget ("modNameLabel", Capi.Label, frame, [])
val sourceFileLabel =
Capi.make_managed_widget ("sourceFileLabel", Capi.Label, frame, [])
val objectFileLabel =
Capi.make_managed_widget ("objectFileLabel", Capi.Label, frame, [])
fun get_user_options () = user_options
fun mk_tooldata () = tooldata
val consoleLabel =
Capi.make_managed_widget ("console", Capi.Label, frame, [])
val {outstream, console_widget, console_text,
clear_console, set_window, ...} =
Console.create (frame, title, user_preferences)
fun message_fun s = Capi.send_message(shell,s)
val (run_debugger, clean_debugger) =
DebuggerWindow.make_debugger_window
(shell, title ^ " Debugger", tooldata)
val debugger_type =
Ml_Debugger.WINDOWING (run_debugger, message_fun, false)
fun debugger_function f x =
Ml_Debugger.with_start_frame
(fn base_frame =>
(f x)
handle
exn as Capi.SubLoopTerminated => raise exn
| exn as ToolData.ShellTypes.DebuggerTrapped => raise exn
| exn as MLWorks.Interrupt => raise exn
| exn as Info.Stop _ => raise exn
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
("quit (return to file tool)",
Ml_Debugger.DO_RAISE ShellTypes.DebuggerTrapped),
Ml_Debugger.NOT_POSSIBLE);
raise ShellTypes.DebuggerTrapped))
local
val error_browser_ref = ref NONE
in
fun kill_error_browser () =
case !error_browser_ref
of NONE => ()
| SOME f =>
(f ();
error_browser_ref := NONE)
fun error_handler
(filename, error, error_list, header,
preferences_fn, redo_action) =
let
val action_message =
header ^ ": " ^ filename
val file_message =
case location_file error of
NONE => ""
| SOME s => s
fun edit_action location =
{quit_fn =
ShellUtils.edit_location (location, preferences_fn()),
clean_fn = fn () => ()}
in
TextIO.output
(outstream, header ^ ": Error in " ^ file_message ^ "\n");
error_browser_ref :=
SOME
(ErrorBrowser.create
{parent = shell, errors = rev error_list,
file_message = file_message,
editable = fn _ => true,
edit_action = edit_action,
close_action = updateRel,
redo_action = redo_action,
mk_tooldata = fn () => tooldata,
get_context = get_current_user_context})
end
end
fun update_labels NONE =
(Capi.set_label_string (modNameLabel, "Selected Unit: <none>");
Capi.set_label_string (sourceFileLabel, "Source: <none>");
Capi.set_label_string (objectFileLabel, "Object: <none>"))
| update_labels (SOME (s, m)) =
let
val project = Incremental.get_project ()
val (source_file, source_file_time) =
case Project.get_source_info (project, m) of
NONE => ("<none>", "")
| SOME (file, _) =>
(OS.Path.fromUnixPath file,
Date.toString (Date.fromTimeLocal (FileTime.modTime file)))
val (object_file, object_file_time) =
case Project.get_object_info (project, m) of
NONE => ("<none>", "")
| SOME {file, ...} =>
(OS.Path.fromUnixPath file,
Date.toString (Date.fromTimeLocal (FileTime.modTime file)))
val loaded =
case Project.get_loaded_info (project, m) of
NONE => false
| SOME _ => true
val visible = Project.is_visible (project, m)
val status_string =
case (loaded, visible)
of (false, false) => ""
| (true, false) => " (loaded)"
| (false, true) => " (visible)"
| (true, true) => " (loaded, visible)"
in
selection := SOME (s, m);
Capi.set_label_string
(modNameLabel, "Selected Unit: " ^ s ^ status_string);
Capi.set_label_string
(sourceFileLabel, "Source: " ^ source_file ^ " \t" ^ source_file_time);
Capi.set_label_string
(objectFileLabel, "Object: " ^ object_file ^ " \t" ^ object_file_time)
end
datatype layout_style = CASCADE | TREE
val selectFnForGraphRef = ref (fn _ => ())
exception EmptyGraph
fun project_graph_tool
{parent, project, module = module_id, title, filter, winNamePrefix, layout} =
let
val moduleName = ModuleId.string module_id
val windowName = winNamePrefix ^ moduleName
val (shell, frame, menuBar, _) =
Capi.make_main_window
{name = "compManagerGraph",
title = windowName,
parent = parent,
contextLabel = false,
winMenu = true,
pos = getOpt (!graphPosRef, Capi.getNextWindowPos())};
val originalRoot = module_id
val currentRoot = ref module_id
val graphSelection = ref module_id
val userExpandedNode = ref false
fun map_to_graph_fn {ordering, eq, get_children, mk_node, filter} =
let
val nodesList = ref []
val iref = ref 0
val seen = ref (NewMap.empty (ordering, eq))
fun do_node key =
if filter key then
case NewMap.tryApply' (!seen, key)
of SOME index => SOME index
| NONE =>
let
val children = get_children key
val index = !iref
val children_ref = ref []
val _ = seen := NewMap.define (!seen, key, index)
val _ = nodesList :=
(NODE (mk_node key, ref NONE), children_ref) ::
!nodesList
val _ = iref := 1 + !iref
val children_ids = List.mapPartial do_node children
in
children_ref := children_ids;
SOME index
end
else
NONE
val nodesArr = ref (MLWorks.Internal.Array.arrayoflist [])
fun recompute () =
(nodesList := [] ;
iref := 0 ;
seen := (NewMap.empty (ordering, eq)) ;
ignore(do_node (!currentRoot));
case !nodesList of [] => raise EmptyGraph | _ => ();
nodesArr := MLWorks.Internal.Array.arrayoflist
(rev (map
(fn (node,ref children) => (node,children))
(!nodesList)))
)
val _ = recompute ()
val lastRoot = ref (!currentRoot)
in
fn () => ((if not(eq(!currentRoot, !lastRoot))
then (recompute () ; lastRoot := !currentRoot )
else () );
(!nodesArr, [0]) )
end
val layoutRef = ref layout
val graph_spec =
GraphWidget.GRAPH_SPEC
{child_position = ref GraphWidget.CENTRE,
child_expansion = ref GraphWidget.TOGGLE,
default_visibility = ref false,
show_root_children = ref true,
indicateHiddenChildren = ref true,
orientation = ref GraphWidget.VERTICAL,
line_style = ref GraphWidget.STRAIGHT,
horizontal_delta = ref 8,
vertical_delta = ref 60,
graph_origin = ref (8, 8),
show_all = ref false
}
val baseline_height = 3
fun max (x: int,y) = if x > y then x else y
val boxMargin = 4
fun get_node_data (NODE (entry, extents),gp) =
case !extents of
SOME data => data
| _ =>
let
val s = ModuleId.string entry
val {font_ascent,font_descent,width,...} =
Capi.GraphicsPorts.text_extent (gp,s)
val data = (s,font_ascent,font_descent,width)
in
extents := SOME data;
data
end
fun entry_draw_node (node, selected, gp, Capi.POINT{x,y}) =
let
val (s, font_ascent, font_descent, width) =
get_node_data (node, gp)
val left = width div 2
val right = width - left
val rectangle = Capi.REGION {x = x-left-boxMargin,
y = y-baseline_height-font_ascent-
boxMargin,
width = width + 2 * boxMargin,
height = font_ascent+font_descent+
2*boxMargin }
fun canHighlight gp f x =
if selected then Capi.GraphicsPorts.with_highlighting (gp,f,x)
else f x
in
if selected
then Capi.GraphicsPorts.fill_rectangle (gp, rectangle)
else (Capi.GraphicsPorts.clear_rectangle (gp, rectangle);
Capi.GraphicsPorts.draw_rectangle (gp, rectangle) );
canHighlight
gp
Capi.GraphicsPorts.draw_image_string
(gp,s,Capi.POINT{x=x - left, y=y - baseline_height} )
end
fun entry_extent (node,gp) =
let
val (s,font_ascent,font_descent,width) = get_node_data (node,gp)
val left = width div 2
val right = width - left
in
GraphWidget.EXTENT
{left = left + boxMargin,
right = right + 2 + boxMargin,
up = baseline_height + font_ascent + 1 + boxMargin,
down = max (0, font_descent-baseline_height) + boxMargin}
end
fun make_project_graph
{project, module = module_id, parent, title, filter} =
let
fun get_requires m = Project.get_requires (project, m)
fun mk_node m = Project.get_name (project, m)
in
GraphWidget.make
("projectGraph", "ProjectGraph", "Dependency graph",
parent, graph_spec,
map_to_graph_fn
{ordering = ModuleId.lt, eq = ModuleId.eq, get_children = get_requires,
mk_node = mk_node, filter = filter},
entry_draw_node, entry_extent)
end
val {initialize, widget, popup_menu, update, initialiseSearch, ...} =
make_project_graph
{project = project, module = module_id, parent = frame,
title = title, filter = filter}
fun isSubstring (s1, s2) =
let
val l1 = explode s1 val l2 = explode s2
fun isSub l =
let
fun isPre ([], _) = true
| isPre (l, []) = false
| isPre ((h1::t1), (h2::t2)) =
(h1=h2) andalso (isPre (t1, t2))
in
(isPre (l1, l)) orelse
(case l of (h::t) => isSub t | _ => false)
end
in
isSub l2
end
fun matchWeak key (NODE(m,_)) =
isSubstring (key, (ModuleId.string m))
fun matchStrong key (NODE(m,_)) = (ModuleId.string m)=key
fun setExpanded b =
(#show_all ((fn GraphWidget.GRAPH_SPEC gs => gs) graph_spec) := b ;
userExpandedNode := false ;
layoutRef := {style = #style (!layoutRef), expanded = b};
update() )
fun isExpanded () = #expanded (!layoutRef)
fun focus module () =
(currentRoot := module ;
graphSelection := module;
(!selectFnForGraphRef) module ;
update () )
val focusOnSelection = fn () => (focus (!graphSelection) ())
fun canFocusOnSelection () = not(ModuleId.eq(!graphSelection, !currentRoot))
fun canUnfocus () = not(ModuleId.eq(!currentRoot, originalRoot))
fun setCascadeLayout () =
let val (GraphWidget.GRAPH_SPEC gs) = graph_spec in
((#child_position gs) := GraphWidget.BELOW;
(#orientation gs) := GraphWidget.HORIZONTAL;
(#line_style gs) := GraphWidget.STEP;
(#horizontal_delta gs) := 20; (#vertical_delta gs) := 8;
layoutRef := {expanded = #expanded (!layoutRef), style = CASCADE};
update () )
end
fun setTreeLayout () =
let val (GraphWidget.GRAPH_SPEC gs) = graph_spec in
((#child_position gs) := GraphWidget.CENTRE;
(#orientation gs) := GraphWidget.VERTICAL;
(#line_style gs) := GraphWidget.STRAIGHT;
(#horizontal_delta gs) := 8; (#vertical_delta gs) := 60;
layoutRef := {expanded = #expanded (!layoutRef), style = TREE};
update () )
end
fun setLayoutStyle style =
case style of TREE => setTreeLayout ()
| CASCADE => setCascadeLayout ()
fun getLayoutStyle () =
#style (!layoutRef)
fun storeGraphSizePos () =
(graphSizeRef := SOME (Capi.widget_size shell);
graphPosRef := SOME (Capi.widget_pos shell))
val close_push =
("close", fn _ => (storeGraphSizePos ();
Capi.destroy shell), fn _ => true)
val searchFn =
initialiseSearch (fn _ => ModuleId.string (!graphSelection))
(matchStrong, matchWeak)
datatype expandType = ALL | ROOT
val expand = ref ROOT
val layoutStyle = ref TREE
val graphSettingsSpec =
[Menus.OPTRADIO
[GuiUtils.toggle_value ("expand_all", expand, ALL),
GuiUtils.toggle_value ("only_root", expand, ROOT)],
Menus.OPTSEPARATOR,
Menus.OPTLABEL "Layout Style",
Menus.OPTRADIO
[GuiUtils.toggle_value ("cascading_layout", layoutStyle, CASCADE),
GuiUtils.toggle_value ("tree_layout", layoutStyle, TREE)]]
fun update_graph () =
(if isExpanded() = ((!expand) = ROOT) then
setExpanded ((!expand) = ALL)
else ();
if getLayoutStyle() <> (!layoutStyle) then
setLayoutStyle (!layoutStyle)
else ())
val dep_graph_settings =
#1 (Menus.create_dialog
(shell, "Graph Layout: " ^ moduleName, "depGraphLayout",
update_graph, graphSettingsSpec)
)
val menuSpec =
[ToolData.file_menu [close_push],
ToolData.edit_menu (frame,
{cut = NONE,
paste = NONE,
copy = NONE,
delete = NONE,
edit_possible = fn _ => false,
selection_made = fn _ => false,
delete_all = NONE,
edit_source = []}),
ToolData.tools_menu (mk_tooldata, get_current_user_context),
ToolData.usage_menu ([("search", searchFn, fn _ => true),
("graph", dep_graph_settings, fn _ => true),
("make_root",
focusOnSelection,
canFocusOnSelection),
("original_root",
focus originalRoot,
canUnfocus)]
,[]),
ToolData.debug_menu []]
val selectFn =
fn (a as (NODE(m,_),_)) =>
( graphSelection := m ;
userExpandedNode := true;
(!selectFnForGraphRef) m )
in
setLayoutStyle (#style layout);
setExpanded (#expanded layout);
Menus.make_submenus (menuBar, menuSpec);
Capi.Layout.lay_out (frame, !graphPosRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.FLEX widget,
Capi.Layout.SPACE]);
Capi.initialize_toplevel shell;
initialize selectFn
end
fun show_graph m =
project_graph_tool
{parent = applicationShell,
project = Incremental.get_project (),
module = m,
title = title,
winNamePrefix = "Dependency graph for ",
layout = {style = TREE, expanded = false},
filter = fn _ => true }
fun selectFnForList _ (s, m) = update_labels (SOME (s, m))
fun action_fn _ (_, m) = show_graph m
fun print_item _ (s, _) = s;
val {scroll, list, set_items, add_items} =
Capi.make_scrolllist
{parent = frame, name = "list", select_fn = selectFnForList,
action_fn = action_fn, print_fn = print_item}
fun redisplay selection_opt =
let
val curProject = Incremental.get_project()
val items =
List.filter
(fn (s, _) => String.sub (s, 0) <> #" ")
(Project.list_units curProject)
val offset = 2
fun index (m, n, []) = NONE
| index (m, n, (s, m') :: rest) =
if ModuleId.eq(m, m') then
SOME n
else
index (m, n+1, rest)
in
set_items () items;
case selection_opt of
NONE =>
update_labels NONE
| SOME (_, m) =>
case index (m, 1, items) of
SOME n =>
(Capi.List.select_pos (list, n, true);
if n < offset+1 orelse List.length items < offset then
Capi.List.set_pos (list, 1)
else
Capi.List.set_pos (list, n - offset))
| NONE => ();
updateDisplay()
end
val setSelectFn =
selectFnForGraphRef := (fn m => redisplay (SOME(ModuleId.string m, m)))
val key = Incremental.add_update_fn (fn () => redisplay (!selection))
val quit_funs = ref [fn () => Incremental.remove_update_fn key,
fn () => project_tool := NONE]
fun do_quit_funs () =
List.app (fn f => f ()) (!quit_funs)
val evaluating = ref false;
fun with_evaluating f x =
let
val prev_capi_eval = !Capi.evaluating;
val _ = Capi.evaluating := true;
val _ = evaluating := true;
val result =
f x
handle exn => (evaluating := false;
Capi.evaluating := prev_capi_eval;
raise exn)
in
evaluating := false;
Capi.evaluating := prev_capi_eval;
result
end
fun reload module_id =
Incremental.read_dependencies
title
(Info.make_default_options ())
module_id
fun graph _ =
case !selection
of NONE => ()
| SOME (_, m) => show_graph m
fun touch_all _ =
(TextIO.output (outstream, "Touch all loaded modules\n");
Incremental.delete_all_modules true;
redisplay (!selection))
fun delete _ =
case !selection
of NONE => ()
| SOME (s, m) =>
(TextIO.output (outstream,
"Delete unit " ^ s ^ " from project\n");
Incremental.delete_from_project m;
selection := NONE;
redisplay NONE)
fun clear_all _ =
(TextIO.output (outstream, "Delete all units\n");
Incremental.reset_project ();
selection := NONE;
redisplay NONE)
val closing = ref false
fun close_window _ =
if !evaluating orelse !closing orelse
(not (ProjProperties.test_save (shell, true))) then
()
else
(closing := true;
do_quit_funs ();
updatePW := (fn () => ());
Capi.destroy shell;
closing := false)
fun updateProjWorkspace () =
let
val (curTargets, _, _) = ProjFile.getTargets()
val name = ProjFile.getProjectName()
val subprojs = ProjFile.getSubprojects()
val files = ProjFile.getFiles()
val (lib, obj, bin) = ProjFile.getLocations()
val old_ref = (!files_up_to_date);
in
if isSome name
then
(
applyResetFiles();
applyResetTargets();
applyResetSubprojs();
applyResetModes();
applyResetConfigs();
applyResetLibPath();
applyResetAboutInfo();
updateName name;
updateFiles files;
updateTargets curTargets;
updateSubprojs subprojs;
relativeObj := OS.Path.isRelative obj;
relativeBin := OS.Path.isRelative bin;
updateRel();
updateObjLoc (obj, bin, lib);
files_up_to_date := old_ref;
update_files_label())
else
close_window()
end
val _ = updatePW := updateProjWorkspace
fun mk_action f x =
(kill_error_browser ();
set_window();
updateProjWorkspace();
with_evaluating
Capi.with_window_updates
(fn () =>
ShellTypes.with_toplevel_name
title
(fn () =>
(Ml_Debugger.with_debugger_type
debugger_type
(fn _ => f x))))
handle
MLWorks.Interrupt => ()
| ShellTypes.DebuggerTrapped => ();
clean_debugger ())
fun setProject () =
(Incremental.set_project
(Project.map_dag
(Project.update_dependencies
(Info.make_default_options (), Location.FILE title))
(Incremental.get_project ()));
files_up_to_date := true;
update_files_label();
message_fun "Finished Reading Dependencies")
handle
Info.Stop (error,error_list) =>
error_handler
("initialisation", error, error_list, "Reading Dependencies",
fn () => Preferences.new_preferences user_preferences,
mk_action setProject)
fun destroy_window _ = project_tool := NONE
val {update=updateProjButtons, ...} =
Menus.make_buttons (projButtonsRC,
[Menus.PUSH ("PW_files", files_dialog, fn _ => true),
Menus.PUSH ("PW_curTargets", targets_dialog, fn _ => true),
Menus.PUSH ("PW_subprojects", subproj_dialog, fn _ => true),
Menus.PUSH ("PW_objectsDir", obj_dialog, fn _ => true),
Menus.PUSH
("PW_readDependencies", mk_action setProject, fn _ => true)])
fun checkGraph
{commandMessage, function, noUnitsMessage, winNamePrefix} =
let
fun f (modName, module) =
(Capi.set_busy shell;
let
val project = Incremental.get_project ()
val errorInfo = Info.make_default_options ()
val toplevelName = ShellTypes.get_current_toplevel_name ()
val location = Location.FILE toplevelName
val (newProject, _) = Project.read_dependencies
(errorInfo, location)
(project, module, Project.empty_map)
in
Incremental.set_project newProject;
case function (errorInfo, location) (newProject, module) of
[] => message_fun noUnitsMessage
| modList => project_graph_tool
{parent = applicationShell,
project = Incremental.get_project (),
module = module,
title = title,
winNamePrefix = winNamePrefix,
layout = {style = CASCADE, expanded = true},
filter =
(fn m => List.exists (fn m' => ModuleId.eq(m',m)) modList)}
end;
redisplay (SOME(modName, module));
Capi.unset_busy shell )
handle
Info.Stop (error, error_list) =>
(error_handler
(modName, error, error_list, commandMessage,
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f (modName, module));
redisplay (SOME(modName, module));
Capi.unset_busy shell)
| MLWorks.Interrupt =>
(redisplay (SOME(modName, module));
Capi.unset_busy shell)
in
mk_action f
end
val showCompileSelection =
checkGraph
{commandMessage = "Check compile",
function = Project.check_compiled,
noUnitsMessage = "No units require compilation.\n",
winNamePrefix = "Units to compile for " }
val showLoadSelection =
checkGraph
{commandMessage = "Check load",
function = Project.check_load_objects,
noUnitsMessage = "No units need loading.\n",
winNamePrefix = "Units to load for " }
val edit_file =
mk_action
(fn (s, m) =>
case Project.get_source_info (Incremental.get_project (), m) of
NONE => message_fun ("No source file for " ^ s)
| SOME (file, _) =>
(case Editor.edit
(Preferences.new_preferences user_preferences)
(file,0) of
(NONE,_) => ()
| (SOME s,_) => message_fun s))
fun get_options () =
UserOptions.new_options (user_options, get_user_context_options())
val read_depend =
let
fun f (s, m) =
Capi.with_message (shell, "Reading dependencies from " ^ s)
(fn () =>
(reload m; redisplay (SOME (s, m))))
handle
Info.Stop (error,error_list) =>
error_handler
(s, error, error_list, "Reading Dependencies",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f (s, m))
in
mk_action f
end
val touch_loaded =
let
fun f (s, m) =
let
val error_info = Info.make_default_options()
in
Incremental.delete_module
error_info
m;
message_fun ("Touched loaded unit " ^ s)
end
in
mk_action f
end
val loadSelection =
let
fun f (s, m) =
let
val error_info = Info.make_default_options()
in
Capi.with_message (shell, "Load Objects: " ^ s)
(fn () =>
ShellUtils.load_file
(GuiUtils.get_user_context (!local_context),
Location.FILE title,
optionsFromProjFile (get_options()),
Preferences.new_preferences user_preferences,
print)
error_info
s);
message_fun ("Load of " ^ s ^ " finished")
end
handle
Info.Stop (error,error_list) =>
error_handler
(s, error, error_list, "Load",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f (s, m))
in
mk_action f
end
val compileSelection =
let
fun f (s, m) =
let
val error_info = Info.make_default_options()
in
Capi.with_message
(shell, "Compiling " ^ s)
(fn () =>
ShellUtils.compile_file
(Location.FILE title, optionsFromProjFile (get_options()))
error_info
s);
message_fun ("Compilation of " ^ s ^ " finished")
end
handle
Info.Stop (error, error_list) =>
error_handler
(s, error, error_list, "Compile",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f (s, m))
in
mk_action f
end
val compileTargets =
let
fun f () =
let
val error_info = Info.make_default_options()
in
Capi.with_message (shell, "Compiling target sources")
(fn () =>
ShellUtils.compile_targets
(Location.FILE title, optionsFromProjFile (get_options()))
error_info);
message_fun ("Finished compiling target sources")
end
handle
Info.Stop (error,error_list) =>
error_handler
("", error, error_list, "Compile Target Sources",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f ())
in
mk_action f
end
val showCompileTargets =
let
fun f () =
let
val error_info = Info.make_default_options()
in
Capi.with_message (shell, "Show files to compile target sources")
(fn () =>
ShellUtils.show_compile_targets
(Location.FILE title, print)
error_info);
message_fun ("Finished showing files to compile target sources")
end
handle
Info.Stop (error,error_list) =>
error_handler
("", error, error_list, "Show Compile Target Sources",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f ())
in
mk_action f
end
val loadTargets =
let
fun f () =
let
val error_info = Info.make_default_options()
in
Capi.with_message (shell, "Loading targets")
(fn () =>
ShellUtils.load_targets
(GuiUtils.get_user_context (!local_context),
Location.FILE title,
optionsFromProjFile (get_options()),
Preferences.new_preferences user_preferences,
print)
error_info);
message_fun ("Finished loading targets")
end
handle
Info.Stop (error,error_list) =>
error_handler
("", error, error_list, "Load Targets",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f ())
in
mk_action f
end
val showLoadTargets =
let
fun f () =
let
val error_info = Info.make_default_options()
in
Capi.with_message (shell, "Show files to load targets")
(fn () =>
ShellUtils.show_load_targets
(Location.FILE title, print)
error_info);
message_fun ("Finished showing files to load targets")
end
handle
Info.Stop (error,error_list) =>
error_handler
("", error, error_list, "Show Load Targets",
fn () => Preferences.new_preferences user_preferences,
fn () => mk_action f ())
in
mk_action f
end
val remove_object =
let
fun f (s, m) =
let val path = ShellUtils.object_path (s, Location.FILE title)
in OS.FileSys.remove path handle OS.SysErr (s,e) => ();
Project.set_object_info(Incremental.get_project (), m, NONE)
end
in
mk_action f
end
datatype action =
EDIT of (string * ModuleId.ModuleId)
| COMPILE_SELECTION of (string * ModuleId.ModuleId)
| COMPILE_TARGETS
| LOAD_SELECTION of (string * ModuleId.ModuleId)
| LOAD_TARGETS
| SHOW_COMPILE_SELECTION of (string * ModuleId.ModuleId)
| SHOW_COMPILE_TARGETS
| SHOW_LOAD_SELECTION of (string * ModuleId.ModuleId)
| SHOW_LOAD_TARGETS
| REMOVE_OBJECT of (string * ModuleId.ModuleId)
| TOUCH_LOADED of (string * ModuleId.ModuleId)
fun get_action (EDIT (s, m)) = edit_file (s, m)
| get_action (COMPILE_SELECTION (s, m)) = compileSelection (s, m)
| get_action COMPILE_TARGETS = compileTargets ()
| get_action (LOAD_SELECTION (s, m)) = loadSelection (s, m)
| get_action LOAD_TARGETS = loadTargets ()
| get_action (SHOW_COMPILE_SELECTION (s, m)) =
showCompileSelection (s, m)
| get_action SHOW_COMPILE_TARGETS = showCompileTargets ()
| get_action (SHOW_LOAD_SELECTION (s, m)) =
showLoadSelection (s, m)
| get_action SHOW_LOAD_TARGETS = showLoadTargets ()
| get_action (REMOVE_OBJECT (s, m)) = remove_object (s, m)
| get_action (TOUCH_LOADED (s, m)) = touch_loaded (s, m)
fun eq_action (EDIT(s1,m1), EDIT(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (COMPILE_SELECTION(s1,m1), COMPILE_SELECTION(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (COMPILE_TARGETS,COMPILE_TARGETS) = true
| eq_action (LOAD_SELECTION(s1,m1), LOAD_SELECTION(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (LOAD_TARGETS,LOAD_TARGETS) = true
| eq_action (SHOW_COMPILE_SELECTION(s1,m1), SHOW_COMPILE_SELECTION(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (SHOW_COMPILE_TARGETS,SHOW_COMPILE_TARGETS)= true
| eq_action (SHOW_LOAD_SELECTION(s1,m1), SHOW_LOAD_SELECTION(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (SHOW_LOAD_TARGETS,SHOW_LOAD_TARGETS)= true
| eq_action (REMOVE_OBJECT(s1,m1), REMOVE_OBJECT(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (TOUCH_LOADED(s1,m1), TOUCH_LOADED(s2,m2)) =
s1 = s2 andalso ModuleId.eq(m1,m2)
| eq_action (_,_) = false
val history = ref []: action list ref
fun get_max_history () =
let
val Preferences.USER_PREFERENCES ({history_length,...},_) =
user_preferences
in
!history_length
end
fun ministry_of_truth ([], _, _) = []
| ministry_of_truth (s::l, new_factoid, finish) =
if finish > 0 then
if eq_action(s, new_factoid) then
l
else
s :: ministry_of_truth (l, new_factoid, finish - 1)
else []
fun add_action a =
history := a :: (ministry_of_truth (!history,a,get_max_history()))
fun string_action action =
let
fun add_name (str, s) = str ^ " " ^ s ^ "\n";
in
case action of
EDIT (s, _) => add_name ("Edit source of", s)
| COMPILE_SELECTION (s, _) => add_name ("Compile", s)
| COMPILE_TARGETS => "Compile Target Sources\n"
| LOAD_SELECTION (s, _) => add_name ("Load", s)
| LOAD_TARGETS => "Load Targets\n"
| SHOW_COMPILE_SELECTION (s, m) =>
add_name ("Show files to compile", s)
| SHOW_LOAD_SELECTION (s, m) =>
add_name ("Show files to load", s)
| SHOW_COMPILE_TARGETS => "Show files to compile target sources\n"
| SHOW_LOAD_TARGETS => "Show files to load targets\n"
| REMOVE_OBJECT (s, _) => add_name ("Remove compiled object for", s)
| TOUCH_LOADED (s, _) => add_name ("Touch loaded module", s)
end
fun do_action action =
(TextIO.output (outstream, string_action action);
add_action action;
get_action action)
fun make_callback action _ =
case !selection
of NONE => ()
| SOME x => do_action (action x)
fun get_history_menu () =
let
fun mkItem action =
Menus.PUSH (string_action action,
fn _ => do_action action,
fn _ => true)
in
map mkItem (!history)
end
fun is_selection _ =
isSome (!selection)
val view_menu =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = fn _ => (),
view_type =
[GuiUtils.SENSITIVITY,
GuiUtils.VALUE_PRINTER,
GuiUtils.INTERNALS]}
fun get_user_context () = GuiUtils.get_user_context (!local_context)
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun storeProj () =
(originalProj := Incremental.get_project();
updateDisplay())
fun wrapUpdate condition_fn =
(updateDisplay();
if condition_fn shell then
updateDisplay()
else ())
fun new_project () = wrapUpdate ProjProperties.new_project
fun open_project () =
(updateDisplay();
if ProjProperties.open_project shell (fn () => ()) then
storeProj()
else ())
fun save_project () = wrapUpdate ProjProperties.save_project
fun save_project_as () = wrapUpdate ProjProperties.save_project_as
fun closeProject () =
(ProjFile.close_proj();
Incremental.set_project (Incremental.get_project()))
val file_menu = ToolData.file_menu
[("new_proj", new_project, fn _ => true),
("open_proj", open_project, fn _ => true),
("save_proj", save_project, fn _ => true),
("save_proj_as", save_project_as, fn _ => true),
("save", fn _ =>
GuiUtils.save_history
(false, get_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set
(get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history
(get_user_context ()))),
("close", close_window, fn () => not (!evaluating))]
val view = ToolData.extract view_menu
fun action_ () = ()
fun sens_ () = isSome (ProjFile.getProjectName())
fun eval_sel () = is_selection() andalso not (!evaluating)
fun not_eval () = not (!evaluating)
val project_menu = Menus.CASCADE ("project_menu",
[Menus.PUSH ("compile",
make_callback COMPILE_SELECTION,
eval_sel),
Menus.PUSH ("compile_all",
fn () => do_action COMPILE_TARGETS,
not_eval),
Menus.PUSH ("load",
make_callback LOAD_SELECTION,
eval_sel),
Menus.PUSH ("load_targets",
fn () => do_action LOAD_TARGETS,
not_eval),
Menus.PUSH ("recompile",
make_callback REMOVE_OBJECT,
eval_sel),
Menus.PUSH ("reload",
make_callback TOUCH_LOADED,
eval_sel),
Menus.SEPARATOR,
Menus.PUSH ("check",
mk_action setProject,
not_eval),
Menus.SEPARATOR,
Menus.CASCADE ("show",
[Menus.PUSH ("check_compile",
make_callback SHOW_COMPILE_SELECTION,
eval_sel),
Menus.PUSH ("check_build",
fn _ => do_action SHOW_COMPILE_TARGETS,
not_eval),
Menus.PUSH ("check_load",
make_callback SHOW_LOAD_SELECTION,
eval_sel),
Menus.PUSH ("check_targets",
fn _ => do_action SHOW_LOAD_TARGETS,
not_eval),
Menus.PUSH ("show_graph",
graph,
eval_sel)],
fn _ => true),
Menus.CASCADE ("proj_properties",
[Menus.PUSH ("prop_files", files_dialog, sens_),
Menus.PUSH ("prop_target", targets_dialog, sens_),
Menus.PUSH ("prop_subproj", subproj_dialog, sens_),
Menus.PUSH ("prop_mode", modes_dialog, sens_),
Menus.PUSH ("prop_config", configs_dialog, sens_),
Menus.PUSH ("prop_lib", library_dialog, sens_),
Menus.PUSH ("prop_obj_dir", obj_dialog, sens_),
Menus.PUSH ("prop_about", about_dialog, sens_)],
fn _ => true),
Menus.SEPARATOR,
Menus.PUSH ("deleteSelection", delete, eval_sel),
Menus.PUSH ("removeAllUnits", clear_all, not_eval),
Menus.PUSH ("clear_console", clear_console, fn _ => true)],
fn () => true)
val menuspec =
[file_menu,
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME
(fn _ => Capi.clipboard_set
(console_text, Capi.Text.get_selection console_text)),
delete = NONE,
selection_made =
fn _ => Capi.Text.get_selection console_text <> "",
edit_possible = fn _ => false,
delete_all = NONE,
edit_source = [Menus.PUSH ("editSource",
make_callback EDIT, is_selection)] }),
ToolData.tools_menu (mk_tooldata, get_current_user_context),
ToolData.usage_menu (view, []),
project_menu,
ToolData.debug_menu [],
Menus.DYNAMIC ("history", get_history_menu, fn _ => true)]
val textPaneLayout =
[Capi.Layout.FIXED consoleLabel,
Capi.Layout.FIXED console_widget,
Capi.Layout.SPACE]
val listPaneLayout =
[Capi.Layout.FIXED projNameLabel,
Capi.Layout.FIXED projCurTargetsText,
Capi.Layout.FIXED projSubprojText,
Capi.Layout.FIXED relativeRC,
Capi.Layout.FIXED projObjectsLabel,
Capi.Layout.FIXED projButtonsRC,
Capi.Layout.SPACE,
Capi.Layout.FIXED modNameLabel,
Capi.Layout.FIXED sourceFileLabel,
Capi.Layout.FIXED objectFileLabel,
Capi.Layout.SPACE,
Capi.Layout.FIXED listLabel,
Capi.Layout.FLEX scroll,
Capi.Layout.SPACE]
in
SaveImage.add_with_fn with_no_project_tool;
project_tool := SOME shell;
quit_funs := Menus.quit :: (!quit_funs);
quit_funs := storeSizePos :: (!quit_funs);
quit_funs := closeProject :: (!quit_funs);
quit_funs := (fn () => ProjProperties.need_saved := false) :: (!quit_funs);
Menus.make_submenus (menubar,menuspec);
Capi.Layout.lay_out
(frame, !sizeRef,
[Capi.Layout.MENUBAR menubar,
Capi.Layout.SPACE] @
listPaneLayout @ textPaneLayout);
Capi.Callback.add (shell, Capi.Callback.Destroy, destroy_window);
Capi.set_close_callback(frame, close_window);
updateProjWorkspace();
redisplay (!selection);
Capi.initialize_toplevel shell
end
val newOpenDialog = ref NONE
fun newOpenProject (tooldata as ToolData.TOOLDATA
{args, appdata, current_context, motif_context, tools}) =
let
val ToolData.APPLICATIONDATA {applicationShell = parent,...} = appdata
val (shell, form, menuBar, _) =
Capi.make_main_popup {name = "Project",
title = "Project",
parent = parent,
contextLabel = false,
visibleRef = ref false,
pos = Capi.getNextWindowPos()}
val _ = newOpenDialog := SOME shell
val label = Capi.make_managed_widget ("PW_choiceLabel", Capi.Label, form, [])
val rc = Capi.make_managed_widget ("projectRC", Capi.RowColumn, form, [])
fun new_project () =
(Capi.hide shell;
Capi.destroy shell;
if ProjProperties.new_project parent then
(create_project_tool tooldata;
updateDisplay())
else ())
fun open_project () =
(Capi.hide shell;
Capi.destroy shell;
if ProjProperties.open_project parent
(fn () => create_project_tool tooldata) then
updateDisplay()
else ())
fun closeCB () = newOpenDialog := NONE
in
Menus.make_submenus (menuBar, []);
Capi.remove_menu menuBar;
Capi.Callback.add(shell, Capi.Callback.Destroy, closeCB);
Capi.Callback.add(form, Capi.Callback.Unmap, fn _ => Capi.destroy shell);
ignore(Menus.make_buttons (rc,
[Menus.PUSH ("PW_new", new_project, fn _ => true),
Menus.PUSH ("PW_open", open_project, fn _ => true)]));
Capi.Layout.lay_out (form, NONE,
[Capi.Layout.FIXED label,
Capi.Layout.FIXED rc]);
Capi.reveal form;
Capi.reveal shell;
Capi.to_front shell;
shell
end
fun create tooldata =
if isSome (!project_tool) then
Capi.to_front (valOf (!project_tool))
else
if isSome (ProjFile.getProjectName()) then
create_project_tool tooldata
else
let
val shell =
if not(isSome(!newOpenDialog)) then
newOpenProject tooldata
else
valOf(!newOpenDialog)
in
Capi.reveal shell;
Capi.to_front shell
end
end
;
