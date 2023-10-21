require "../basis/__int";
require "capi";
require "menus";
require "../utils/lists";
require "../main/user_options";
require "../main/preferences";
require "../utils/crash";
require "../interpreter/shell_utils";
require "../interpreter/save_image";
require "../interpreter/entry";
require "inspector_tool";
require "graph_widget";
require "gui_utils";
require "tooldata";
require "browser_tool";
functor BrowserTool (
structure Capi : CAPI
structure GraphWidget : GRAPH_WIDGET
structure Crash : CRASH
structure Lists : LISTS
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure Menus : MENUS
structure InspectorTool : INSPECTORTOOL
structure GuiUtils : GUI_UTILS
structure ToolData : TOOL_DATA
structure ShellUtils : SHELL_UTILS
structure SaveImage : SAVE_IMAGE
structure Entry : ENTRY
sharing UserOptions.Options =
ToolData.ShellTypes.Options =
ShellUtils.Options
sharing type Entry.options = UserOptions.Options.options
sharing type ToolData.UserContext.identifier = Entry.Identifier
sharing type UserOptions.user_tool_options =
ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options = ShellUtils.UserOptions
sharing type Menus.Widget = GuiUtils.Widget = ToolData.Widget = Capi.Widget =
GraphWidget.Widget = InspectorTool.Widget
sharing type Capi.GraphicsPorts.GraphicsPort = GraphWidget.GraphicsPort
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ShellTypes.Context =
Entry.Context = ShellUtils.Context
sharing type ToolData.ShellTypes.user_context = GuiUtils.user_context
sharing type GuiUtils.user_context_options =
ToolData.UserContext.user_context_options =
UserOptions.user_context_options
sharing type ShellUtils.user_preferences =
ToolData.ShellTypes.user_preferences =
Preferences.user_preferences =
GuiUtils.user_preferences
sharing type GuiUtils.Type = ShellUtils.Type = InspectorTool.Type
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type GraphWidget.Point = Capi.Point
sharing type GraphWidget.Region = Capi.Region
sharing type InspectorTool.ToolData = ToolData.ToolData
) : BROWSERTOOL =
struct
structure UserContext = ToolData.UserContext
structure Options = UserOptions.Options
structure Info = ShellUtils.Info
structure ShellTypes = ToolData.ShellTypes
type Widget = Capi.Widget
type UserOptions = UserOptions.user_tool_options
type ShellData = ShellTypes.ShellData
type ToolData = ToolData.ToolData
fun title () = "Browser"
fun initial_title () = "System Browser"
val context_browser = ref NONE
val system_browser = ref NONE
val context_number = ref 1
val system_number = ref 1
val sizeRef = ref NONE
val posRef = ref NONE
fun create_internal (browser_ref, number_ref, duplicated) (tooldata, orig_title) =
let
val ToolData.TOOLDATA {args,appdata,current_context,motif_context,tools} =
tooldata
val ShellTypes.LISTENER_ARGS
{user_options, user_preferences, user_context,
mk_xinterface_fn, prompter, ...} = args
val ToolData.APPLICATIONDATA {applicationShell,...} =
appdata
val full_menus =
case user_preferences
of Preferences.USER_PREFERENCES ({full_menus, ...}, _) =>
!full_menus
val local_context = ref motif_context
fun get_user_context () = GuiUtils.get_user_context (!local_context)
fun get_user_context_options () =
UserContext.get_user_options
(GuiUtils.get_user_context (!local_context))
fun get_compiler_options () =
UserOptions.new_options (user_options, get_user_context_options())
fun getItemsFromContext () = let
val context = UserContext.get_delta (get_user_context ())
in
Entry.context2entry context
end
val title =
if not duplicated then orig_title
else
(number_ref := (!number_ref) + 1;
orig_title ^ " #" ^ (Int.toString (!number_ref - 1)))
val (shell,frame,menuBar,contextLabel) =
Capi.make_main_window
{name = "browser",
title = title,
parent = applicationShell,
contextLabel = full_menus,
winMenu = duplicated,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val listLabel = Capi.make_managed_widget ("listLabel",Capi.Label,frame,[])
val selectionLabel =
Capi.make_managed_widget ("selectionLabel",Capi.Label,frame,[])
fun get_context () = UserContext.get_context (get_user_context ())
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
local
val browse_options = Entry.new_options ()
val Entry.BROWSE_OPTIONS
{show_vars,
show_cons,
show_exns,
show_types,
show_strs,
show_sigs,
show_funs,
show_conenvs} = browse_options
in
val filter_entries =
Entry.filter_entries browse_options
fun getter r () = !r
fun setter r b = (r := b; true)
fun toggle (s,r) = Menus.OPTTOGGLE(s,getter r, setter r)
val filter_spec =
[toggle ("show_sigs", show_sigs),
toggle ("show_funs", show_funs),
toggle ("show_strs", show_strs),
toggle ("show_types", show_types),
toggle ("show_conenvs", show_conenvs),
toggle ("show_exns", show_exns),
toggle ("show_cons", show_cons),
toggle ("show_vars", show_vars)]
end
datatype Node = ROOT | NODE of Entry.Entry
fun print_node item =
let
val compilerOptions = get_compiler_options()
in
case item of
ROOT => "Top Level"
| NODE entry => Entry.printEntry compilerOptions entry
end
val baseline_height = 3
fun max (x,y) = if x > y then x else y
datatype Item = ITEM of Node * Node list * (string * int * int * int) option ref
fun get_item_data (ITEM (entry,selection,extents),gp) =
case !extents of
SOME data => data
| _ =>
let
val s = print_node entry
val {font_ascent,font_descent,width,...} = Capi.GraphicsPorts.text_extent (gp,s)
val data = (s,font_ascent,font_descent,width)
in
extents := SOME data;
data
end
fun entry_draw_item (item,selected,gp,Capi.POINT{x,y}) =
let
val (s,font_ascent,font_descent,width) = get_item_data (item,gp)
val left = width div 2
val right = width - left
fun doit () =
(
Capi.GraphicsPorts.draw_image_string
(gp,s,Capi.POINT{x=x - left, y=y - baseline_height}))
in
if selected
then Capi.GraphicsPorts.with_highlighting (gp,doit, ())
else doit ()
end
fun entry_extent (item,gp) =
let
val (s,font_ascent,font_descent,width) = get_item_data (item,gp)
val left = width div 2
val right = width - left
in
GraphWidget.EXTENT{
left = left,
right = right+2,
up = baseline_height + font_ascent+1,
down = max (0,font_descent+3-baseline_height)
}
end
fun make_context_graph () =
let
fun entryfun selection entry = ITEM (NODE entry,selection,ref NONE)
fun top_entry entry = ITEM (NODE entry, [], ref NONE)
fun toplevel_items () = map top_entry (filter_entries (getItemsFromContext ()))
fun get_children (ITEM (ROOT,_,_)) = toplevel_items ()
| get_children (ITEM (node as NODE entry,selection,_)) =
map (entryfun (node :: selection)) (filter_entries (Entry.browse_entry true entry))
fun get_node (ITEM (node,_,_)) = node
val nodes_ref = ref []
val iref = ref 0
fun do_node item =
let
val node = get_node item
val children = get_children item
val index = !iref
val children_ref = ref []
val _ = nodes_ref := (item,children_ref) :: !nodes_ref
val _ = iref := 1 + !iref
val children_ids = map do_node (get_children item)
in
children_ref := children_ids;
index
end
val _ = do_node (ITEM (ROOT,[],ref NONE))
val nodes =
MLWorks.Internal.Array.arrayoflist
(rev (map (fn (node,ref children) => (node,children)) (!nodes_ref)))
in
(nodes,[0])
end
val graph_spec =
GraphWidget.GRAPH_SPEC {child_position = ref GraphWidget.BELOW,
child_expansion = ref GraphWidget.TOGGLE,
default_visibility = ref false,
show_root_children = ref true,
indicateHiddenChildren = ref false,
orientation = ref GraphWidget.HORIZONTAL,
line_style = ref GraphWidget.STEP,
horizontal_delta = ref 20,
vertical_delta = ref 1,
graph_origin = ref (3,3),
show_all = ref false}
val {widget=graph_window,
initialize=init_graph,
update=update_graph,
set_position,
initialiseSearch,
...} =
GraphWidget.make ("browserGraph","BrowserGraph",title,
frame,
graph_spec,
make_context_graph,
entry_draw_item,
entry_extent)
val selectionStr = ref ""
val selectionLength = ref 0
val shortSelectionStr = ref ""
fun removePostfix s =
let
fun f [] = []
| f (#"<" :: t) = []
| f (h::t) = h::(f t)
in
implode (f (explode s))
end
fun getShortName ROOT = ""
| getShortName (NODE entry) = removePostfix (#1(Entry.get_id entry))
val do_select_fn = ref (fn () => ())
fun graph_select_fn (item as ITEM (node,selection,_),reg) =
let
fun node_string (ROOT) = "ROOT"
| node_string (NODE entry) = #1 (Entry.get_id entry)
fun printit (node,[]) = node_string node
| printit (node,node'::selection) =
case node' of
ROOT => printit (node,selection)
| NODE(entry) =>
if Entry.is_tip(entry)
then printit(node,selection)
else node_string node' ^ "." ^ printit (node,selection)
val string = printit (node,rev selection)
val shortString = getShortName node
in
Capi.set_label_string (selectionLabel, "Selection: "^string);
selectionLength := size string;
selectionStr := string;
shortSelectionStr := shortString;
(!do_select_fn) ()
end
fun initialize_graph () = init_graph graph_select_fn
val filter_popup =
#1 (Menus.create_dialog
(shell, "Browser Settings", "browserDialog",
update_graph, filter_spec))
val quit_funs = ref []
fun do_quit_funs _ = Lists.iterate (fn f => f ()) (!quit_funs)
val update_register_key =
ref (UserContext.add_update_fn
(get_user_context (), fn _ => update_graph ()))
fun with_no_context browser_ref f arg1 arg2 =
let
val browser = !browser_ref
val user_context = GuiUtils.get_user_context (!local_context)
in
browser_ref := NONE;
UserContext.remove_update_fn
(user_context, !update_register_key);
ignore(f arg1 arg2
handle exn => (browser_ref := browser;
update_register_key :=
UserContext.add_update_fn
(user_context, fn _ => update_graph ());
raise exn));
browser_ref := browser;
update_register_key :=
UserContext.add_update_fn
(user_context, fn _ => update_graph ())
end
fun set_context c =
let
val old_user_context = GuiUtils.get_user_context (!local_context)
val new_user_context = GuiUtils.get_user_context c
in
local_context := c;
set_context_label c;
UserContext.remove_update_fn
(old_user_context, !update_register_key);
update_register_key :=
UserContext.add_update_fn
(new_user_context, fn _ => update_graph ());
update_graph ()
end
val context_key =
ToolData.add_context_fn
(current_context, (set_context, get_user_options, ToolData.ALL))
val _ =
quit_funs :=
(fn _ =>
let
val user_context = GuiUtils.get_user_context (!local_context)
in
ToolData.remove_context_fn
(current_context, context_key);
UserContext.remove_update_fn
(user_context, !update_register_key)
end)
:: (fn _ => (browser_ref := NONE))
:: !quit_funs
fun select_context user_context =
(set_context user_context;
ToolData.set_current
(current_context, context_key, user_options, user_context))
fun close_window _ =
(do_quit_funs ();
Capi.destroy shell)
fun mk_tooldata () =
let
val user_options = UserOptions.copy_user_tool_options user_options
in
case user_options
of UserOptions.USER_TOOL_OPTIONS
({set_selection, sense_selection, set_context, sense_context, ...},
_) =>
(set_selection := true;
sense_selection := true;
set_context := true;
sense_context := true);
ToolData.TOOLDATA
{args = ShellTypes.LISTENER_ARGS
{user_options = user_options,
user_preferences = user_preferences,
user_context =
user_context,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn},
appdata = appdata,
current_context = current_context,
motif_context =
ToolData.get_current current_context,
tools = tools}
end
val sep_size = 10
val view_menu =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = fn (_) => (),
view_type = [GuiUtils.SENSITIVITY]}
fun get_current_value () =
if !selectionStr = ""
then NONE
else
SOME
(!selectionStr,
ShellUtils.eval
Info.null_options
(!selectionStr,
(ShellTypes.new_options (user_options, user_context)),
UserContext.get_context user_context))
handle _ => NONE
val inspect_fn = InspectorTool.inspect_value (shell,false, mk_tooldata())
val _ =
do_select_fn :=
(fn _ =>
case get_current_value () of
NONE => ()
| SOME x => inspect_fn true x)
fun duplicate () = create_internal (ref NONE, number_ref, true) (tooldata, orig_title)
val value_menu =
GuiUtils.value_menu
{parent = shell,
user_preferences = user_preferences,
inspect_fn = SOME (inspect_fn false),
get_value = get_current_value,
enabled = not (UserContext.is_const_context
(GuiUtils.get_user_context (!local_context))),
tail = []}
val values = ToolData.extract value_menu
val view = ToolData.extract view_menu
val search =
let
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
fun getDefault () = (!shortSelectionStr)
fun matchWeak string (ITEM(entry,_,_)) =
isSubstring(string, getShortName entry)
fun matchStrong string (ITEM(entry,_,_)) =
string = getShortName entry
in
initialiseSearch getDefault (matchStrong, matchWeak)
end
val menuSpec =
[ToolData.file_menu [("save", fn _ =>
GuiUtils.save_history (false, get_user_context (), applicationShell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", close_window, fn _ => true)],
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (fn _ => Capi.clipboard_set (shell,!selectionStr)),
delete = NONE,
selection_made = fn _ => !selectionStr <> "",
edit_possible = fn _ => false,
delete_all = NONE,
edit_source = [value_menu]}),
ToolData.tools_menu (mk_tooldata, fn () => user_context),
ToolData.usage_menu (("filter", filter_popup, fn _ => true) ::
("duplicate", duplicate, fn _ => true) ::
("search", search, fn _ => true) ::
values @ view, []),
ToolData.debug_menu values]
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
in
SaveImage.add_with_fn (with_no_context browser_ref);
browser_ref := SOME shell;
quit_funs := Menus.quit :: (!quit_funs);
quit_funs := storeSizePos :: (!quit_funs);
Menus.make_submenus (menuBar,menuSpec);
Capi.Layout.lay_out
(frame, !sizeRef,
[Capi.Layout.MENUBAR menuBar] @
(case contextLabel of
SOME w => [Capi.Layout.FIXED w]
| _ => [Capi.Layout.SPACE]) @
[Capi.Layout.FIXED selectionLabel,
Capi.Layout.SPACE,
Capi.Layout.FIXED listLabel,
Capi.Layout.FLEX graph_window,
Capi.Layout.SPACE]);
Capi.set_close_callback(frame, close_window);
Capi.Callback.add (frame, Capi.Callback.Destroy, do_quit_funs);
set_context_label (!local_context);
Capi.initialize_toplevel shell;
initialize_graph ()
end
fun create_initial
(ToolData.TOOLDATA
{args = ShellTypes.LISTENER_ARGS
{user_context, user_options, user_preferences, prompter,
mk_xinterface_fn},
appdata, tools, motif_context, current_context, ...}) =
let
val initial = GuiUtils.getInitialContext ()
in
case user_options
of UserOptions.USER_TOOL_OPTIONS
({set_selection, sense_selection, set_context, sense_context, ...},
_) =>
(set_selection := false;
sense_selection := false;
set_context := false;
sense_context := false);
if isSome (!system_browser) then
Capi.to_front (valOf (!system_browser))
else
create_internal (system_browser, system_number, false)
(ToolData.TOOLDATA
{args =
ShellTypes.LISTENER_ARGS
{user_context = user_context,
user_preferences = user_preferences,
user_options = user_options,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn},
appdata = appdata,
motif_context = initial,
current_context = current_context,
tools = tools},
initial_title ())
end
fun create tooldata =
if isSome (!context_browser) then
Capi.to_front (valOf (!context_browser))
else
create_internal (context_browser, context_number, false) (tooldata,title())
end
;
