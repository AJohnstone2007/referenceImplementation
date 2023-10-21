require "../basis/__int";
require "../utils/lists";
require "capi";
require "menus";
require "../interpreter/shell_utils";
require "gui_utils";
require "tooldata";
require "error_browser";
functor ErrorBrowser(
structure Lists : LISTS
structure Capi : CAPI
structure Menus : MENUS
structure GuiUtils : GUI_UTILS
structure ShellUtils : SHELL_UTILS
structure ToolData : TOOL_DATA
sharing type Capi.Widget = Menus.Widget = GuiUtils.Widget = ToolData.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type GuiUtils.user_tool_options = ShellUtils.UserOptions
) : ERROR_BROWSER =
struct
structure Info = ShellUtils.Info
structure Location = Info.Location
type Widget = Capi.Widget
type Context = ShellUtils.Context
type options = ShellUtils.Options.options
type error = Info.error
type location = Location.T
type ToolData = ToolData.ToolData
type user_context = ToolData.ShellTypes.user_context
fun first_line message =
let
fun aux ([],acc) = acc
| aux (#"\n" :: _,acc) = acc
| aux (c::l,acc) = aux(l,c::acc)
in
implode (rev (aux (explode message, [])))
end
fun location_line location =
case location of
Location.UNKNOWN => ""
| Location.FILE s => ""
| Location.LINE(_,l) => "Line " ^ Int.toString l
| Location.POSITION (_,l,_) => "Line " ^ Int.toString l
| Location.EXTENT {s_line,e_line,...} =>
if s_line = e_line
then "Line " ^ Int.toString s_line
else "Line " ^ Int.toString s_line ^ " to " ^ Int.toString e_line
fun error_location (Info.ERROR(_,location,message)) = location
fun error_to_string (Info.ERROR(severity,location,message)) =
(case location_line location of
"" => "error: " ^ first_line message
| l => l ^ ": error: " ^ first_line message)
fun print_fn _ (Info.ERROR(severity,location,message)) =
(case location_line location of
"" => first_line message
| l => l ^ ": " ^ first_line message)
val posRef = ref NONE
val sizeRef = ref NONE
fun create
{parent, errors, file_message, editable, edit_action,
redo_action, close_action, mk_tooldata, get_context} =
let
val (shell,mainWindow,menuBar,_) =
Capi.make_main_popup {name = "errorBrowser",
title = "Error Browser",
parent = parent,
contextLabel = false,
visibleRef = ref true,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val frame =
Capi.make_managed_widget
("errorBrowserFrame", Capi.Paned, mainWindow, [Capi.PanedMargin true])
val listPane = Capi.make_managed_widget ("listPane", Capi.Form, frame, [])
val reason = Capi.make_managed_widget
("errorBrowserTitle", Capi.Label, listPane, [])
val textPane = Capi.make_managed_widget ("textPane", Capi.Form, frame, [])
val textTitle = Capi.make_managed_widget
("errorTextLabel", Capi.Label, textPane, [])
val (textScroll,text) =
Capi.make_scrolled_text ("errorBrowserText", textPane, [])
fun message_fun s = Capi.send_message (shell,s)
val quit_funs = ref []
val good_clean_fun = ref (fn () => ())
fun do_quit_funs _ = Lists.iterate (fn f => f ()) (!quit_funs)
fun edit_error _ (Info.ERROR(_,location,_)) =
let
val _ = (!good_clean_fun) ();
val {quit_fn, clean_fn} = edit_action location
in
quit_funs := quit_fn :: (!quit_funs);
good_clean_fun := clean_fn
end
handle ShellUtils.EditFailed s =>
message_fun ("Edit failed: " ^ s)
fun show_full_message _ (Info.ERROR(_,_,message)) =
Capi.Text.set_string(text,message)
val {scroll,list,set_items,...} =
Capi.make_scrolllist
{parent = listPane, name = "errorBrowser",
select_fn = show_full_message,
action_fn = edit_error,
print_fn = print_fn}
fun edit_fun _ =
let
val selected_items = Capi.List.get_selected_pos list
in
case MLWorks.Internal.Vector.length selected_items of
0 => message_fun "No item selected"
| 1 =>
let val index = MLWorks.Internal.Vector.sub(selected_items,0)
in
edit_error (scroll,list,set_items)
(Lists.nth (index-1,errors))
end
| _ => message_fun "Multiple selections"
end
fun can_edit _ =
let
val selected_items = Capi.List.get_selected_pos list
in
if MLWorks.Internal.Vector.length selected_items = 1 then
let
val index = MLWorks.Internal.Vector.sub(selected_items,0)
in
case Lists.nth (index-1,errors) of
Info.ERROR(_,location,_) => editable location
end
else
false
end
local
val destroyed = ref false
in
fun quit_fun _ =
if not (!destroyed) then
((!good_clean_fun) ();
do_quit_funs();
destroyed := true;
Capi.remove_main_window shell;
Capi.destroy shell)
else ()
end
fun redo_fun _ =
(quit_fun ();
redo_action ())
fun close_fun _ =
(quit_fun ();
close_action ())
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
val buttonPane =
Capi.make_managed_widget ("buttonPane", Capi.RowColumn, textPane, []);
val _ =
Menus.make_buttons
(buttonPane,
[Menus.PUSH ("repeatButton", redo_fun, fn _ => true),
Menus.PUSH ("editSourceButton", edit_fun, can_edit),
Menus.PUSH ("closeButton", close_fun, fn _ => true)])
val menuspec =
[ToolData.file_menu [("close", close_fun, fn _ => true)],
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (fn _ => Capi.clipboard_set (text,Capi.Text.get_selection text)),
delete = NONE,
selection_made = fn _ => Capi.Text.get_selection text <> "",
edit_possible = fn _ => false,
delete_all = NONE,
edit_source = [Menus.PUSH ("editSource", edit_fun, can_edit)] }),
ToolData.tools_menu (mk_tooldata, get_context),
ToolData.usage_menu ([("repeat", redo_fun, fn _ => true)], []),
ToolData.debug_menu []]
val textPaneLayout =
(textPane,
[Capi.Layout.FIXED textTitle,
Capi.Layout.FLEX textScroll,
Capi.Layout.SPACE,
Capi.Layout.FIXED buttonPane]);
val listPaneLayout =
(listPane,
[Capi.Layout.FIXED reason,
Capi.Layout.FLEX scroll]);
in
quit_funs := Menus.quit :: (!quit_funs);
quit_funs := storeSizePos :: (!quit_funs);
quit_funs := (fn _ => Capi.remove_main_window shell) :: (!quit_funs);
Capi.Layout.lay_out
(mainWindow, !sizeRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.PANED (frame, [textPaneLayout, listPaneLayout]),
Capi.Layout.SPACE]);
Menus.make_submenus(menuBar,menuspec);
Capi.Callback.add (mainWindow, Capi.Callback.Unmap, quit_fun);
Capi.set_close_callback (mainWindow, close_fun);
Capi.Callback.add (Capi.parent shell, Capi.Callback.Destroy, quit_fun);
set_items ShellUtils.Options.default_print_options errors;
Capi.set_label_string (reason, "Location: " ^ file_message);
Capi.reveal mainWindow;
Capi.List.select_pos (list, 1, false);
show_full_message (scroll,list,set_items) (Lists.nth (0,errors));
quit_fun
end
end;
