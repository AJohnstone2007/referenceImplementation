require "capi";
require "menus";
require "../utils/lists";
require "../main/user_options";
require "../main/preferences";
require "../interpreter/shell_utils";
require "../interpreter/save_image";
require "tooldata";
require "inspector_tool";
require "file_viewer";
require "gui_utils";
require "context";
functor ContextHistory (
structure Capi : CAPI
structure ToolData : TOOL_DATA
structure Menus : MENUS
structure GuiUtils : GUI_UTILS
structure FileViewer : FILE_VIEWER
structure InspectorTool : INSPECTORTOOL
structure ShellUtils : SHELL_UTILS
structure Lists: LISTS
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure SaveImage : SAVE_IMAGE
sharing ToolData.ShellTypes.Options = UserOptions.Options = ShellUtils.Options
sharing type Preferences.user_preferences =
ToolData.ShellTypes.user_preferences =
GuiUtils.user_preferences
sharing type GuiUtils.user_context_options =
ToolData.UserContext.user_context_options
sharing type ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options =
UserOptions.user_tool_options
sharing type Menus.Widget = ToolData.Widget = FileViewer.Widget =
GuiUtils.Widget = Capi.Widget = InspectorTool.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ShellTypes.user_context = GuiUtils.user_context
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type InspectorTool.ToolData = ToolData.ToolData = FileViewer.ToolData
sharing type ShellUtils.Context = ToolData.ShellTypes.Context
sharing type GuiUtils.Type = ShellUtils.Type = InspectorTool.Type
sharing type ShellUtils.history_entry = ToolData.UserContext.history_entry
): CONTEXT_HISTORY =
struct
structure UserContext = ToolData.UserContext
structure ShellTypes = ToolData.ShellTypes
structure Options = UserOptions.Options
structure Info = ShellUtils.Info
type ToolData = ToolData.ToolData
val history_tool = ref NONE
val sizeRef = ref NONE
val posRef = ref NONE
fun create_history (tooldata as ToolData.TOOLDATA
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
val title = "History"
val (shell,frame,menuBar,contextLabel) =
Capi.make_main_window
{name = "context",
title = title,
parent = applicationShell,
contextLabel = full_menus,
winMenu = false,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val local_context = ref motif_context
fun get_user_context () =
GuiUtils.get_user_context (!local_context)
val history = ref []: UserContext.history_entry list ref;
fun empty_history () =
case !history
of [] => true
| _ => false
val curr_string = ref ""
val curr_item = ref NONE
val do_select_fn = ref (fn () => ())
val do_action_fn = ref (fn () => ())
fun unset_selection _ =
(curr_string := "";
curr_item := NONE)
fun select_fn
_
(entry as UserContext.ITEM (item as {result, ...})) =
(curr_string := result;
curr_item := SOME entry;
(!do_select_fn) ())
fun action_fn _ (entry as UserContext.ITEM (item as {result, ...})) =
(curr_string := result;
curr_item := SOME entry;
(!do_action_fn) ())
val {scroll, list, set_items, add_items} =
Capi.make_scrolllist
{parent = frame, name = "context_window",
print_fn =
fn _ => fn (UserContext.ITEM {result, ...}) => result,
select_fn = select_fn,
action_fn = action_fn}
fun get_print_options () =
UserOptions.new_print_options user_options
fun set_history_from_context user_context =
let
val hist = UserContext.get_history user_context
in
history := hist;
set_items(get_print_options ()) (rev hist)
end
fun delete_selection () =
case !curr_item
of NONE => ()
| SOME x =>
(UserContext.delete_from_history (get_user_context (), x);
unset_selection ())
fun delete_all_duplicates () =
(UserContext.remove_duplicates_from_history (get_user_context ());
unset_selection ())
fun delete_all () =
(UserContext.delete_entire_history (get_user_context ());
unset_selection ())
fun update_fn NONE =
set_history_from_context (get_user_context ())
| update_fn (SOME new_items) =
(history := new_items @ !history;
add_items(get_print_options ()) (rev new_items))
val update_register_key =
ref (UserContext.add_update_fn (get_user_context (), update_fn))
fun with_no_history f arg1 arg2 =
let
val history = !history_tool
val user_context = get_user_context ()
in
history_tool := NONE;
UserContext.remove_update_fn
(user_context, !update_register_key);
ignore(f arg1 arg2
handle exn => (history_tool := history;
update_register_key :=
UserContext.add_update_fn
(user_context, update_fn);
raise exn));
history_tool := history;
update_register_key :=
UserContext.add_update_fn(user_context, update_fn)
end
fun set_state motif_context =
let
val context_name = GuiUtils.get_context_name motif_context
val cstring = "Context: " ^ context_name
val old_user_context = get_user_context ()
val new_user_context = GuiUtils.get_user_context motif_context
in
UserContext.remove_update_fn
(old_user_context, !update_register_key);
case contextLabel
of SOME w =>
Capi.set_label_string (w,cstring)
| NONE => ();
local_context := motif_context;
set_history_from_context new_user_context;
update_register_key :=
UserContext.add_update_fn (new_user_context, update_fn)
end
val _ = set_state (!local_context)
val context_key =
ToolData.add_context_fn
(current_context,
(set_state, fn () => user_options, ToolData.WRITABLE))
fun select_state motif_context =
(set_state motif_context;
ToolData.set_current
(current_context, context_key, user_options, motif_context))
val quit_funs = ref []
fun do_quit_funs _ = Lists.iterate (fn f => f ()) (!quit_funs)
val _ =
quit_funs :=
(fn _ =>
let
val user_context = get_user_context ()
in
ToolData.remove_context_fn (current_context, context_key);
UserContext.remove_update_fn
(user_context, !update_register_key)
end)
:: (fn _ => (history_tool := NONE))
:: !quit_funs
fun close_window _ =
(do_quit_funs ();
Capi.destroy shell)
fun mk_tooldata () =
ToolData.TOOLDATA
{args = ToolData.ShellTypes.LISTENER_ARGS
{user_options = user_options,
user_context = get_user_context (),
user_preferences = user_preferences,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn},
appdata = appdata,
motif_context = !local_context,
current_context = current_context,
tools = tools}
val view_options =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = fn _ => (),
view_type = [GuiUtils.SENSITIVITY]}
fun get_current_value () =
case !curr_item of
NONE => NONE
| SOME item =>
ShellUtils.value_from_history_entry
(item, ShellTypes.new_options (user_options, get_user_context()))
val inspect_fn = InspectorTool.inspect_value (shell,false, mk_tooldata())
fun is_selection () =
case !curr_item
of SOME _ => true
| NONE => false
val show_defn_fn = FileViewer.create (shell, true, mk_tooldata())
fun show_defn auto =
case !curr_item of
NONE => ()
| SOME (item as UserContext.ITEM {source, ...}) =>
case source
of UserContext.STRING src =>
show_defn_fn auto (FileViewer.STRING src)
| UserContext.COPY src =>
show_defn_fn auto (FileViewer.STRING src)
val _ =
do_select_fn :=
(fn () =>
(show_defn true;
case get_current_value () of
SOME x => (inspect_fn true x)
| _ => ()))
val _ = do_action_fn := (fn () => (show_defn false;
Capi.set_focus shell))
val edit_menu =
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (fn _ => Capi.clipboard_set (shell,!curr_string)),
delete = SOME delete_selection,
selection_made = fn _ => !curr_string <> "",
edit_possible = fn _ => true,
delete_all = SOME ("deleteAll",
fn _ => delete_all (),
fn _ => not (empty_history ())),
edit_source = [] })
val value_menu =
GuiUtils.value_menu
{parent = shell,
user_preferences = user_preferences,
inspect_fn = SOME (inspect_fn false),
get_value = get_current_value,
enabled = true,
tail =
[Menus.PUSH
("show_defn",
fn _ => show_defn false,
fn _ => is_selection ())]}
val view = ToolData.extract view_options
val values = ToolData.extract value_menu
val menuspec =
[ToolData.file_menu
[("save",
fn _ => GuiUtils.save_history
(false, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs",
fn _ => GuiUtils.save_history
(true, get_user_context (), applicationShell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", close_window, fn _ => true) ],
edit_menu,
ToolData.tools_menu (mk_tooldata, get_user_context),
ToolData.usage_menu
(("removeDuplicates",
fn _ => delete_all_duplicates (),
fn _ => not (empty_history ())) :: (values @ view), []),
ToolData.debug_menu (values)]
val sep_size = 10
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
in
SaveImage.add_with_fn with_no_history;
history_tool := SOME shell;
Menus.make_submenus (menuBar,menuspec);
quit_funs := Menus.quit :: (!quit_funs);
quit_funs := storeSizePos :: (!quit_funs);
Capi.Layout.lay_out
(frame, !sizeRef,
[Capi.Layout.MENUBAR menuBar] @
(case contextLabel of
SOME w => [Capi.Layout.FIXED w]
| _ => [Capi.Layout.SPACE]) @
[Capi.Layout.FLEX scroll,
Capi.Layout.SPACE]);
Capi.set_close_callback(frame, close_window);
Capi.Callback.add (shell, Capi.Callback.Destroy,do_quit_funs);
Capi.initialize_toplevel shell
end
fun create tooldata =
if isSome (!history_tool) then
Capi.to_front (valOf (!history_tool))
else
create_history tooldata
end;
