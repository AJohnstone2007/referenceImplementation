require "../basis/__int";
require "../basis/__text_io";
require "../basis/__io";
require "../utils/lists";
require "../utils/map";
require "../basics/location";
require "^.utils.__terminal";
require "capi";
require "menus";
require "gui_utils";
require "tooldata";
require "file_viewer";
functor FileViewer (
structure Lists: LISTS
structure Map: MAP
structure Capi: CAPI
structure ToolData : TOOL_DATA
structure GuiUtils : GUI_UTILS
structure Menus : MENUS
structure Location : LOCATION
sharing type ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options
sharing type GuiUtils.user_context_options =
ToolData.UserContext.user_context_options
sharing type Menus.Widget = ToolData.Widget =
GuiUtils.Widget = Capi.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ShellTypes.user_preferences =
GuiUtils.user_preferences
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context
): FILE_VIEWER =
struct
structure Location = Location
structure ShellTypes = ToolData.ShellTypes
structure UserContext = ToolData.UserContext
type Widget = Capi.Widget
type ToolData = ToolData.ToolData
val do_debug = false
fun debug s = if do_debug then Terminal.output(s ^ "\n") else ()
val viewer_number = ref 0
datatype source = STRING of string | LOCATION of Location.T
exception ViewFailed of string
val posRef = ref NONE
val sizeRef = ref NONE
fun make_file_viewer
(src, select_auto, parent, destroy_fun,
tooldata as ToolData.TOOLDATA {args, ...}) =
let
val ShellTypes.LISTENER_ARGS {user_options,
user_preferences,
user_context, ...} = args
val duplicated = not (isSome select_auto)
val title =
if duplicated then
(viewer_number := (!viewer_number) + 1;
"File Viewer #" ^ Int.toString (!viewer_number))
else
"File Viewer"
val do_automatic =
case select_auto
of NONE => ref false
| SOME b => ref b
val (shell, mainWindow, menuBar, _) =
Capi.make_main_popup {name = "fileViewer",
title = title,
parent = parent,
contextLabel = false,
visibleRef = ref true,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val locationLabel =
Capi.make_managed_widget ("locationLabel", Capi.Label, mainWindow, [])
val (scroll,text) = Capi.make_scrolled_text ("textoutput", mainWindow, [])
val _ = Capi.transfer_focus (mainWindow,text)
fun highlight (str, loc, b) =
let
val (s_pos, e_pos) = Location.extract (loc, str)
in
Capi.Text.set_highlight (text, s_pos, e_pos, b)
end
handle Location.InvalidLocation => ()
fun check_copy_selection _ =
Capi.clipboard_set (text,Capi.Text.get_selection text)
local
val current_src = ref (STRING "")
val current_text = ref ""
val current_file = ref NONE
val file_cache = ref (Map.empty' (op< : string * string -> bool))
in
fun set_text contents =
let
val s = Capi.Text.convert_text contents
in
current_text := s;
Capi.Text.set_string (text, s)
end
fun get_text () = !current_text
fun set_file file = current_file := SOME file
fun unset_file () = current_file := NONE
fun get_file () = !current_file
fun cache_file (arg as (file, contents)) =
(set_file file;
file_cache := Map.define' (!file_cache, arg))
fun lookup_file file =
Map.tryApply'Eq (!file_cache, file)
fun set_location loc =
(current_src := LOCATION loc;
Capi.set_label_string (locationLabel, Location.to_string loc))
fun set_string str =
(current_src := STRING str;
unset_file ();
Capi.set_label_string (locationLabel, "<User input>"))
fun get_source () = !current_src
end
fun read_file filename =
case lookup_file filename
of SOME contents =>
(set_text contents;
set_file filename;
NONE)
| NONE =>
let
val instream = TextIO.openIn filename
val contents = TextIO.inputN (instream, 4194304)
in
TextIO.closeIn instream;
set_text contents;
set_file filename;
cache_file (filename, contents);
NONE
end
handle IO.Io _ => SOME filename
fun open_file location =
case Location.file_of_location location
of "" => ()
| filename =>
if get_file () = SOME filename then
(case get_source () of
LOCATION old_loc =>
highlight (get_text (), old_loc, false)
| _ =>
();
highlight (get_text (), location, true);
set_location location)
else
let
val success_opt = read_file filename
in
if isSome success_opt then
(set_text "";
raise ViewFailed (getOpt (success_opt,"")))
else
(highlight (get_text (), location, true);
set_location location)
end
fun popup () =
let
val main_windows = map (fn (a,b) => b) (Capi.get_main_windows())
val exists = Lists.member (title, main_windows)
in
Capi.reveal mainWindow;
if not exists then Capi.add_main_window (shell, title) else ();
Capi.to_front shell
end
fun show_source (auto, src) =
if auto andalso not (!do_automatic) then
()
else
(if src = get_source () then
()
else
case src of
LOCATION loc =>
open_file loc
| STRING str =>
(set_text str;
set_string str);
if auto then
()
else
popup ())
fun duplicate src =
let
val (f, q) = make_file_viewer (src, NONE, parent, fn _ => (), tooldata)
in
f (false, src)
end
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun quit_fun _ =
(destroy_fun ();
Menus.quit();
storeSizePos();
Capi.remove_main_window shell)
fun close_window _ =
(quit_fun();
Capi.destroy shell)
fun get_user_context () = user_context
val menuspec =
[ToolData.file_menu [("save", fn _ =>
GuiUtils.save_history (false, get_user_context (), shell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), shell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", close_window, fn _ => true)],
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (check_copy_selection),
delete = NONE,
edit_possible = fn _ => false,
selection_made = fn _ => Capi.Text.get_selection text <> "",
edit_source = [],
delete_all = NONE}),
ToolData.tools_menu (fn () => tooldata, fn () => user_context),
ToolData.usage_menu
([("duplicate", fn _ => duplicate (get_source ()), fn _ => true)],
[("autoSelection",
fn _ => !do_automatic,
fn b => do_automatic := b,
fn _ => case select_auto of NONE => false | _ => true)]),
ToolData.debug_menu []]
in
Menus.make_submenus (menuBar,menuspec);
Capi.Layout.lay_out
(mainWindow, !sizeRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.FIXED locationLabel,
Capi.Layout.FLEX scroll,
Capi.Layout.SPACE]);
Capi.Callback.add (shell, Capi.Callback.Destroy, quit_fun);
(show_source, close_window)
end
local
val display_fun = ref NONE
in
fun create (parent, select_auto, tooldata) =
let
fun reset _ = display_fun := NONE;
fun update auto source =
case !display_fun of
SOME f => f (auto, source)
| _ =>
if auto then
()
else
let
val (f, q) = make_file_viewer
(source, SOME select_auto, parent, reset, tooldata)
in
display_fun := SOME f;
f (false, source)
handle ViewFailed f => (reset(); q (); raise ViewFailed f)
end
in
update
end
end
end;
