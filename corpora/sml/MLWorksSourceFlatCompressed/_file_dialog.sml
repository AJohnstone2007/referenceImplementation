require "../utils/__terminal";
require "../motif/xm";
require "../main/info";
require "../basis/os";
require "../utils/__lists";
require "file_dialog";
functor FileDialog(
structure Xm : XM
structure Info : INFO
structure OS : OS
) : FILE_DIALOG =
struct
structure Location = Info.Location
type Widget = Xm.widget
datatype FileType = DIRECTORY | FILE
datatype exist = MUST_EXIST | MAY_EXIST
val last_visit_dir = ref "./"
val last_filesys_dir = ref (OS.FileSys.getDir())
handle OS.SysErr _ => ref "./"
fun send_message (parent,message) =
let
val dialog =
Xm.Widget.createPopupShell ("messageDialog",
Xm.Widget.DIALOG_SHELL,
parent, [])
val widget =
Xm.Widget.create
("message", Xm.Widget.MESSAGE_BOX, dialog,
[(Xm.MESSAGE_STRING, Xm.COMPOUND_STRING (Xm.CompoundString.createSimple message))])
val _ =
map
(fn c =>
Xm.Widget.unmanageChild (Xm.MessageBox.getChild(widget,c)))
[Xm.Child.CANCEL_BUTTON,
Xm.Child.HELP_BUTTON]
fun exit _ = Xm.Widget.destroy dialog
in
Xm.Callback.add (widget, Xm.Callback.OK, exit);
Xm.Widget.manage widget
end
fun crash s =
Info.default_error'
(Info.FAULT,Location.UNKNOWN,s)
fun find_files (parent, mask: string, file_type: FileType, exist, multi) =
let
val title =
case file_type of
FILE => "File Selection Dialog"
| DIRECTORY => "Directory Selection Dialog"
val shell =
Xm.Widget.createPopupShell ("fileDialog",
Xm.Widget.DIALOG_SHELL,
parent,
[(Xm.TITLE, Xm.STRING title),
(Xm.ICON_NAME, Xm.STRING title)])
val filesys_dir = OS.FileSys.getDir()
handle OS.SysErr _ => !last_filesys_dir
val box = Xm.Widget.create
("selectionBox",
Xm.Widget.FILE_SELECTION_BOX,
shell, [])
fun set_mask s =
(Xm.Widget.valuesSet
(box,
[(Xm.DIR_MASK,
Xm.COMPOUND_STRING
(Xm.CompoundString.createSimple s))]))
val setLastDir = (!last_filesys_dir <> filesys_dir) orelse
((file_type = DIRECTORY) andalso (exist = MUST_EXIST))
val _ = if setLastDir then
(last_filesys_dir := filesys_dir;
last_visit_dir := filesys_dir;
set_mask(!last_filesys_dir ^ "/*" ^ mask))
else
set_mask(!last_visit_dir ^ "/*" ^ mask)
fun get_dir() =
(case Xm.Widget.valuesGet(box,[Xm.DIRECTORY]) of
[Xm.COMPOUND_STRING filename] =>
Xm.CompoundString.convertStringText filename
| _ => crash "Bad values for valuesGet (get_dir)")
val current_sel = ref NONE
fun get_files () =
if (multi) then
getOpt (!current_sel, [])
else
(case Xm.Widget.valuesGet(box,[Xm.DIR_SPEC]) of
[Xm.COMPOUND_STRING filename] =>
[Xm.CompoundString.convertStringText filename]
| _ => crash "Bad values for valuesGet (get_file)")
val _ =
Xm.Widget.unmanageChild
(Xm.FileSelectionBox.getChild (box, Xm.Child.HELP_BUTTON))
val _ =
if file_type = DIRECTORY then
(app
Xm.Widget.unmanageChild
[Xm.Widget.parent
(Xm.FileSelectionBox.getChild(box,Xm.Child.LIST)),
Xm.FileSelectionBox.getChild(box,Xm.Child.LIST_LABEL)];
())
else if (multi) then
app
Xm.Widget.unmanageChild
[Xm.Widget.parent
(Xm.FileSelectionBox.getChild(box,Xm.Child.TEXT)),
Xm.FileSelectionBox.getChild(box,Xm.Child.TEXT),
Xm.FileSelectionBox.getChild(box,Xm.Child.SELECTION_LABEL)]
else ()
fun filterChanged callback_data =
if (multi) then
current_sel := NONE
else
let
val text_w = Xm.FileSelectionBox.getChild (box, Xm.Child.TEXT)
val sel_text = #3 (Xm.Callback.convertList callback_data)
in
Xm.Text.setString (text_w, (Xm.CompoundString.convertStringText sel_text) ^ "/")
end
fun selectionMade callback_data =
let
val selection =
map Xm.CompoundString.convertStringText (#6 (Xm.Callback.convertList callback_data))
in
current_sel := SOME selection
end
val _ =
if file_type = DIRECTORY then
Xm.Callback.add
(Xm.FileSelectionBox.getChild (box, Xm.Child.DIR_LIST),
Xm.Callback.BROWSE_SELECTION,
filterChanged)
else if (multi) then
let
val file_list_w = Xm.FileSelectionBox.getChild (box, Xm.Child.FILE_LIST)
in
Xm.Widget.valuesSet(file_list_w, [(Xm.SELECTION_POLICY, Xm.SELECTION_POLICY_VALUE Xm.EXTENDED_SELECT)]);
Xm.Callback.add
(Xm.FileSelectionBox.getChild (box, Xm.Child.DIR_LIST),
Xm.Callback.BROWSE_SELECTION,
fn _ => current_sel := NONE);
Xm.Callback.add
(file_list_w,
Xm.Callback.EXTENDED_SELECTION,
selectionMade)
end
else ()
val result = ref NONE
val continue = ref true
in
Xm.Widget.valuesSet
(Xm.FileSelectionBox.getChild (box, Xm.Child.LIST),
[(Xm.SCROLLBAR_DISPLAY_POLICY,
Xm.SCROLLBAR_DISPLAY_POLICY_VALUE Xm.STATIC)]);
Xm.Callback.add
(box,
Xm.Callback.CANCEL,
fn _ =>
(result := NONE;
continue := false;
Xm.Widget.destroy shell));
Xm.Callback.add
(box,
Xm.Callback.OK,
fn _ =>
let
val files = get_files ()
fun filename_ok filename =
((if OS.FileSys.access(filename, []) then
case (file_type, OS.FileSys.isDir filename) of
(DIRECTORY, true) => true
| (FILE, false) => true
| (DIRECTORY, false) =>
(send_message(shell, "Directory " ^ filename ^
" is a file");
false)
| _ =>
(send_message(shell, "File " ^ filename ^
" is a directory");
false)
else
case (file_type, exist) of
(_, MUST_EXIST) =>
(send_message(shell, "Path " ^ filename ^
" does not exist");
false)
| (DIRECTORY, MAY_EXIST) =>
((let
val isDir = OS.FileSys.isDir filename
in
if not isDir then
(send_message(shell, "Directory " ^
filename ^ " is a file");
false)
else
true
end)
handle OS.SysErr _ =>
(OS.FileSys.mkDir filename; true))
| (FILE, _) =>
let
val path = OS.Path.dir filename
in
(if OS.FileSys.isDir path then
true
else
(send_message(shell, "Directory " ^
filename ^ " is a file");
false))
handle OS.SysErr _ =>
(send_message(shell, "Path " ^ path ^
" does not exist");
false)
end))
handle OS.SysErr _ =>
(send_message(shell, "Path " ^ filename ^
" does not exist");
false)
val ok = Lists_.forall filename_ok files
in
if ok then
(result :=
SOME
(map OS.FileSys.fullPath files
handle OS.SysErr _ => files);
continue := false;
last_visit_dir := get_dir();
Xm.Widget.destroy shell)
else ()
end);
Xm.Widget.manage box;
Xm.Widget.manage shell;
Xm.Widget.realize shell;
while !continue do
Xm.doInput ();
!result
end
fun find_file (parent, mask, file_or_dir, existence) =
case (find_files(parent, mask, file_or_dir, existence, false)) of
SOME [] => crash "Invalid return value from selection dialog"
| SOME [s] => SOME s
| SOME (a::rest) => crash "Multiple values returned from single selection box"
| NONE => NONE
fun open_file_dialog (parent, mask, multi) = find_files (parent, mask, FILE, MUST_EXIST, multi)
fun open_dir_dialog parent = find_file (parent, "", DIRECTORY, MUST_EXIST)
fun set_dir_dialog parent = find_file (parent, "", DIRECTORY, MAY_EXIST)
fun save_as_dialog (parent, mask) = find_file (parent, mask, FILE, MAY_EXIST)
end;
