require "../utils/lists";
require "../main/mlworks_io";
require "../main/info";
require "../main/options";
require "../basis/os";
require "capi";
require "menus";
require "path_tool";
functor PathTool (
structure Lists : LISTS
structure Capi : CAPI
structure Menus : MENUS
structure Io: MLWORKS_IO
structure Info: INFO
structure Options: OPTIONS
structure OS: OS
sharing type Menus.Widget = Capi.Widget
sharing type Io.Location = Info.Location.T
) : PATH_TOOL =
struct
structure Location = Info.Location
type Widget = Capi.Widget
fun objectCreate parent =
#1 (Menus.create_dialog (parent,
"Set Object Path",
"objectPathDialog",
fn _ => (),
[Menus.OPTSEPARATOR,
Menus.OPTTEXT ("objectPath",
Io.get_object_path,
fn s => (Io.set_object_path
(s, Location.UNKNOWN);
true))]))
fun setWD parent =
case Capi.open_dir_dialog parent of
SOME s => OS.FileSys.chDir s
| NONE => ()
val sizeRef = ref NONE
val posRef = ref NONE
fun sourceCreate parent =
let
val title = "Set Source Path"
val name = "pathTool"
val (shell, form, menuBar, contextLabel) =
Capi.make_main_window
{name = name,
title = title,
parent = parent,
contextLabel = false,
winMenu = true,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val pathLabel =
Capi.make_managed_widget ("pathLabel",Capi.Label,form,[])
fun number_entries ([], _) = []
| number_entries (h::t, n) = (h, n) :: number_entries (t, n + 1)
val entries = ref (number_entries (Io.get_source_path (), 1))
val current_pos = ref (if !entries = [] then 0 else 1)
val current_entry_selected = ref true;
fun print_entry print_options (s, _) = s;
fun select_fn _ (_, n) =
if !current_pos <> n then
(current_pos := n;
current_entry_selected := false)
else
()
fun action_fn _ (s, _) =
(
current_entry_selected := true)
val {scroll, list, set_items, ...} =
Capi.make_scrolllist
{parent = form, name = "sourcePath", select_fn = select_fn,
action_fn = action_fn, print_fn = print_entry}
val _ =
let val init_dir =
case !entries
of [] => OS.FileSys.getDir()
| ((dir, _) :: _) => dir
in
set_items Options.default_print_options (!entries);
if !current_pos <> 0 then
Capi.List.select_pos (list, 1, false)
else ()
end
fun renumber_up (s, n) = (s, n + 1)
fun renumber_down (s, n) = (s, n - 1)
fun is_in (n:string, []) = false
| is_in (n, (n', _)::t) = n = n' orelse is_in (n, t)
infix is_in
fun add_nth ([], s, n) = [(s, n)]
| add_nth (l as h::t, s, n) =
if n = #2 h then
(s, n) :: map renumber_up l
else
h :: add_nth (t, s, n)
fun remove_nth ([], _) = []
| remove_nth (h::t, n) =
if n = #2 h then
map renumber_down t
else
h :: remove_nth (t, n)
fun get_directory () =
case Capi.open_dir_dialog (shell)
of SOME s => s
| NONE => ""
fun crash s =
Info.default_error' (Info.FAULT,Location.UNKNOWN,s)
fun delete_from_source_path _ =
let val new_entries =
remove_nth (!entries, !current_pos)
val new_source_path = map #1 new_entries
in
entries := new_entries;
Io.set_source_path new_source_path;
current_entry_selected := false;
set_items Options.default_print_options new_entries;
if !current_pos > Lists.length new_entries then
current_pos := Lists.length new_entries
else ();
if !current_pos <> 0 then
Capi.List.select_pos (list, !current_pos, false)
else ()
end
fun insert_into_source_path _ =
let val dir = get_directory ()
in
if dir = "" orelse dir is_in !entries then
()
else let
val new_entries =
add_nth
(!entries, dir, if !current_pos = 0 then 1 else !current_pos)
val new_source_path = map #1 new_entries
in
current_pos := !current_pos + 1;
entries := new_entries;
Io.set_source_path new_source_path;
set_items Options.default_print_options new_entries;
Capi.List.select_pos (list, !current_pos, false)
end
end
fun append_into_source_path _ =
let val dir = get_directory ()
in
if dir = "" orelse dir is_in !entries then
()
else let
val new_entries =
add_nth (!entries, dir, !current_pos + 1)
val new_source_path = map #1 new_entries
in
if !current_pos = 0 then current_pos := 1 else ();
Io.set_source_path new_source_path;
entries := new_entries;
set_items Options.default_print_options new_entries;
Capi.List.select_pos (list, !current_pos, false)
end
end
fun cd_to_source_path _ =
(
current_entry_selected := true)
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun close_window _ =
(storeSizePos(); Capi.destroy shell)
val menuspec =
[Menus.CASCADE
("action",
[
Menus.PUSH ("insert", insert_into_source_path, fn _ => true),
Menus.PUSH ("append", append_into_source_path, fn _ => true),
Menus.PUSH
("delete", delete_from_source_path, fn _ => !current_pos <> 0),
Menus.SEPARATOR,
Menus.PUSH ("close", fn _ => close_window (), fn _ => true)
],
fn _ => true)]
in
Menus.make_menus (menuBar,menuspec,false);
Capi.Layout.lay_out
(form, !sizeRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.FIXED pathLabel,
Capi.Layout.FLEX scroll,
Capi.Layout.SPACE]);
Capi.set_close_callback(form, close_window);
Capi.initialize_toplevel shell
end
end;
