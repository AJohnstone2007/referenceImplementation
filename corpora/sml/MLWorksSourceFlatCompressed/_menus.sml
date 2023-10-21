require "^.utils.__terminal";
require "../basis/__int";
require "../basis/__list";
require "../utils/lists";
require "../utils/crash";
require "../utils/getenv";
require "windows_gui";
require "capitypes";
require "labelstrings";
require "control_names";
require "../main/version";
require "../gui/menus";
functor Menus (structure Lists : LISTS
structure Crash : CRASH
structure WindowsGui : WINDOWS_GUI
structure CapiTypes : CAPITYPES
structure LabelStrings : LABELSTRINGS
structure ControlName : CONTROL_NAME
structure Getenv : GETENV
structure Version : VERSION
sharing type LabelStrings.word = WindowsGui.word
sharing type CapiTypes.Hwnd = WindowsGui.hwnd
) : MENUS =
struct
type Widget = CapiTypes.Widget
type word = WindowsGui.word
fun env s = MLWorks.Internal.Value.cast (MLWorks.Internal.Runtime.environment s)
fun dummy s = Terminal.output(s ^ " unimplemented \n")
val P = Int.toString
val W = Int.toString o WindowsGui.wordToInt
fun k x y = x
val print = Terminal.output
val IDM_HELPCONTENTS = WindowsGui.intToWord 400
val IDM_HELPSEARCH = WindowsGui.intToWord 401
val IDM_HELPHELP = WindowsGui.intToWord 402
val IDM_ABOUT = WindowsGui.intToWord 403
val null_item = WindowsGui.ITEM WindowsGui.nullWord
datatype ButtonSpec =
SEPARATOR
| LABEL of string
| TOGGLE of string * (unit -> bool) * (bool -> unit) * (unit -> bool)
| SLIDER of string * int * int * (int -> unit)
| PUSH of string * (unit -> unit) * (unit -> bool)
| RADIO of string * (unit -> bool) * (bool -> unit) * (unit -> bool)
| CASCADE of string * ButtonSpec list * (unit -> bool)
| DYNAMIC of string * (unit -> ButtonSpec list) * (unit -> bool)
val id_cache = ref []
val toggles = ref []
val dynamics = ref []
val menuItems = ref []
val windowHandle = ref WindowsGui.nullWindow
val deactivate_fun = ref (fn () => ())
val initial_fn = ref (fn () => ())
fun quit () = ((!deactivate_fun) ();
deactivate_fun := (fn () => ());
(!initial_fn) ())
fun getMenuRef name itemListRef =
Lists.findp (fn (s,_,_) => (s = name)) (!itemListRef)
fun addRef name =
(if Lists.member (name, map (fn (s,_,_) => s) (!menuItems)) then ()
else menuItems := (name, ref (fn () => ()), ref (fn () => true)) :: (!menuItems);
getMenuRef name menuItems)
fun addDynamicRef name =
(if Lists.member (name, map (fn (s,_,_) => s) (!dynamics)) then ()
else dynamics := (name, ref (fn () => []), ref (fn () => true)) :: (!dynamics);
getMenuRef name dynamics)
fun changeItem (name, new_act, new_sens) =
let
val (name, act_ref, sens_ref) = getMenuRef name menuItems
handle Lists.Find => (print (name ^ " menu item not found in reference list\n");
("", ref (fn () => ()), ref (fn () => false)))
in
act_ref := (fn () => (ignore(new_act());
ignore(WindowsGui.setFocus (!windowHandle));
()));
sens_ref := new_sens
end
fun changeDynamic (name, new_act, new_sens) =
let
val (name, act_ref, sens_ref) = getMenuRef name dynamics
handle Lists.Find => (print (name ^ " dynamic menu item not found in reference list\n");
("", ref (new_act), ref (fn () => false)))
in
act_ref := new_act;
sens_ref := new_sens
end
fun changeToggleItem (name, new_get, new_act, new_sens) =
let
val (name, act_ref, sens_ref) = getMenuRef name menuItems
handle Lists.Find => (print (name ^ " menu item not found in reference list\n");
("", ref (fn () => ()), ref (fn () => false)))
val (name, toggle_ref, get_ref) =
Lists.findp (fn (s,_,_) => (s = name)) (!toggles)
handle Lists.Find => (print (name ^ " toggle menu item not found in reference list\n");
("", ref false, ref (fn () => false)))
in
get_ref := new_get;
act_ref := (fn () => (toggle_ref := not (!toggle_ref);
ignore(new_act (!toggle_ref));
ignore(WindowsGui.setFocus (!windowHandle)); ()));
sens_ref := new_sens
end
val update_ref = ref (fn () => ())
local
fun get_sensitive SEPARATOR = false
| get_sensitive (LABEL _) = false
| get_sensitive (PUSH (name, act, sens)) = sens()
| get_sensitive (SLIDER _) = false
| get_sensitive (RADIO _) = false
| get_sensitive (TOGGLE (name, get, act, sens)) = sens()
| get_sensitive (DYNAMIC (name, blist, sens)) = sens()
| get_sensitive (CASCADE (name, items, sens)) =
foldl (fn (a,b) => (get_sensitive a) orelse b) false items
fun convertItems ((casc as (CASCADE (name, items, sens))) :: rest) sens_fn =
(changeItem (name, fn () => (), fn () => get_sensitive casc);
convertItems (items @ rest) sens_fn)
| convertItems ((PUSH (name, act, sens)) :: rest) sens_fn =
(changeItem (name, act, sens_fn sens);
convertItems rest sens_fn)
| convertItems ((TOGGLE (name, get, act, sens)) :: rest) sens_fn =
(changeToggleItem (name, get, act, sens_fn sens);
convertItems rest sens_fn)
| convertItems (SEPARATOR :: rest) sens_fn = convertItems rest sens_fn
| convertItems ((LABEL _) :: rest) sens_fn = convertItems rest sens_fn
| convertItems ((SLIDER _) :: rest) sens_fn = convertItems rest sens_fn
| convertItems ((RADIO _) :: rest) sens_fn = convertItems rest sens_fn
| convertItems ((DYNAMIC (name, act, sens)) :: rest) sens_fn =
(changeDynamic (name, act, sens_fn sens);
convertItems rest sens_fn)
| convertItems [] sens_fn = ()
in
fun changeItems (parent, itemList) =
let
fun reset () = (convertItems itemList (fn _ => (fn _ => false)))
fun convert () = (convertItems itemList (fn f => f))
fun activate (WindowsGui.WPARAM w, WindowsGui.LPARAM l) =
let
val deactivated =
(WindowsGui.loword w) = (WindowsGui.convertWaValue WindowsGui.WA_INACTIVE)
in
windowHandle := CapiTypes.get_real parent;
if (not deactivated) then
((!deactivate_fun) ();
deactivate_fun := reset;
convert())
else ();
(!update_ref) ();
NONE
end
in
WindowsGui.addMessageHandler(CapiTypes.get_real parent, WindowsGui.WM_ACTIVATE, activate)
end
fun setItems itemList = initial_fn := (fn () => (convertItems itemList (fn f => f)))
end
fun send_message (parent,message) =
(WindowsGui.messageBeep WindowsGui.MB_ICONQUESTION;
ignore(WindowsGui.messageBox (CapiTypes.get_real parent,message,"MLWorks",
[WindowsGui.MB_OK,WindowsGui.MB_APPLMODAL]));
())
val ok_env : int = env "win32 ok id"
val cancel_env : int = env "win32 cancel id"
val i2w = WindowsGui.intToWord
val ok_id = i2w ok_env
val apply_id = i2w (ControlName.getResID "IDAPPLY")
val reset_id = i2w (ControlName.getResID "IDRESET")
val cancel_id = i2w cancel_env
fun make_submenus (parent, itemList) = changeItems (parent, itemList)
fun minimizefun window (WindowsGui.WPARAM w, l) =
(if (w = WindowsGui.nullWord) then
WindowsGui.showWindow(window, WindowsGui.SW_HIDE)
else
WindowsGui.showWindow(window, WindowsGui.SW_SHOW);
NONE)
fun make_menus (parent, menuspec, isPodium) =
let
val real_parent = CapiTypes.get_real parent
val itemCount = ref 0
fun add_item menu isSubmenu item =
(itemCount := (!itemCount) + 1;
case item of
SEPARATOR =>
(WindowsGui.appendMenu (menu,[WindowsGui.MF_SEPARATOR],null_item,"");
NONE)
| LABEL name =>
let
val label = LabelStrings.get_label name
val value = WindowsGui.ITEM WindowsGui.nullWord
in
WindowsGui.appendMenu (menu, [WindowsGui.MF_STRING,WindowsGui.MF_DISABLED],value,label);
NONE
end
| TOGGLE (name,get,action,sensitive) =>
let
val label = LabelStrings.get_label name
val id = LabelStrings.get_action name
val value = WindowsGui.ITEM id
val checked = ref false
val get_ref = ref get
val _ = if Lists.member (name, map (fn (s,_,_) => s) (!toggles)) then ()
else
toggles := (name, checked, get_ref) :: (!toggles)
val (_, actionRef, sensitiveRef) = addRef name
in
actionRef := (fn () => (checked := not (!checked);
action (!checked)));
sensitiveRef := sensitive;
WindowsGui.appendMenu (menu,[WindowsGui.MF_STRING],value,label);
WindowsGui.addCommandHandler (real_parent,
id,
fn _ => (!actionRef) ());
SOME (fn _ =>
(checked := (!get_ref) ();
WindowsGui.enableMenuItem
(menu,
id,
[if (!sensitiveRef) ()
then WindowsGui.MF_ENABLED
else WindowsGui.MF_GRAYED]);
WindowsGui.checkMenuItem
(menu,
id,
[if !checked
then WindowsGui.MF_CHECKED
else WindowsGui.MF_UNCHECKED])))
end
| SLIDER (name,min,max,set_value) =>
Crash.impossible "No sliders in MS Windows"
| RADIO _ =>
Crash.impossible "No radio buttons in menus in MS Windows"
| PUSH (name,action,sensitive) =>
let
val label = LabelStrings.get_label name
val id = LabelStrings.get_action name
val (_, actionRef, sensitiveRef) = addRef name
in
actionRef := action;
sensitiveRef := sensitive;
WindowsGui.appendMenu (menu,[WindowsGui.MF_STRING],WindowsGui.ITEM id,label);
WindowsGui.addCommandHandler (real_parent,id,fn _ => (!actionRef) ());
SOME
(fn _ =>
WindowsGui.enableMenuItem
(menu,
id,
[if (!sensitiveRef) ()
then WindowsGui.MF_ENABLED
else WindowsGui.MF_GRAYED]))
end
| DYNAMIC (name,f,sensitive) =>
let
val (_, actionRef, sensitiveRef) = addDynamicRef name
val label = LabelStrings.get_label name
val submenu = WindowsGui.createPopupMenu()
val count_ref = ref 0
val all_ids = ref []
val base_count = (!itemCount) - 1
fun loopto (dmenu, r, q) =
if r <= q then ()
else
(WindowsGui.deleteMenu (dmenu,WindowsGui.intToWord (r-1),WindowsGui.MF_BYPOSITION);
loopto (dmenu, r-1, q))
fun loop n = loopto (submenu, n, 0)
fun get_id () =
case !id_cache of
[] =>
let
val id = WindowsGui.newControlId ()
in
all_ids := id :: !all_ids; id
end
| (id::rest) =>
(id_cache := rest;
all_ids := id :: !all_ids;
id)
fun add_dynamic_item menu (PUSH (name,action,sensitive)) =
let
val id = get_id ()
in
WindowsGui.appendMenu (menu,[WindowsGui.MF_STRING],WindowsGui.ITEM id,name);
WindowsGui.addCommandHandler (real_parent,id,fn _ => action ());
(fn _ =>
WindowsGui.enableMenuItem
(menu,id,
[if sensitive ()
then WindowsGui.MF_ENABLED
else WindowsGui.MF_GRAYED]))
end
| add_dynamic_item _ _ = Crash.impossible "add_dynamic_item"
fun init (the_menu, loop_fn) =
(ignore(loop_fn (!count_ref));
id_cache := !all_ids @ !id_cache;
all_ids := [];
let
val subfns = map (add_dynamic_item the_menu) ( (!actionRef) () )
in
count_ref := length subfns;
app (fn f => f ()) subfns
end)
in
actionRef := f;
sensitiveRef := sensitive;
if name = "" then
SOME (fn _ =>
(init (menu, fn n => loopto (menu, (!count_ref) + base_count, base_count))))
else
(WindowsGui.appendMenu (menu,[WindowsGui.MF_POPUP],WindowsGui.SUBMENU submenu,label);
SOME (fn _ => (init (submenu, loop))))
end
| CASCADE (name,subitems,sensitive) =>
let
val label = LabelStrings.get_label name
val id = WindowsGui.intToWord ((!itemCount) - 1)
val temp = (!itemCount)
val _ = itemCount := 0
val (_, actionRef, sensitiveRef) = addRef name
val submenu = WindowsGui.createPopupMenu ()
fun enable_fn () =
WindowsGui.enableMenuItem (menu, id,
if (!sensitiveRef)() orelse (not isSubmenu) then
[WindowsGui.MF_ENABLED, WindowsGui.MF_BYPOSITION]
else
[WindowsGui.MF_GRAYED, WindowsGui.MF_BYPOSITION])
val subfns = (SOME enable_fn) :: (map (add_item submenu true) subitems)
val _ = itemCount := temp
in
sensitiveRef := sensitive;
if (!sensitiveRef)() orelse (not isSubmenu) then
WindowsGui.appendMenu (menu, [WindowsGui.MF_POPUP,
WindowsGui.MF_ENABLED],
WindowsGui.SUBMENU submenu, label)
else
WindowsGui.appendMenu (menu,[WindowsGui.MF_POPUP,
WindowsGui.MF_GRAYED],WindowsGui.SUBMENU submenu,label);
SOME
(fn _ =>
app (fn (SOME f) => f () | NONE => ()) subfns)
end)
fun add_help menu =
let
val help_menu = WindowsGui.createPopupMenu ()
datatype Item = ITEM of string * WindowsGui.word | SEPARATOR
fun add_item (ITEM (name,id)) =
WindowsGui.appendMenu
(help_menu,[WindowsGui.MF_STRING],WindowsGui.ITEM id,name)
| add_item SEPARATOR =
WindowsGui.appendMenu (help_menu,[WindowsGui.MF_SEPARATOR],null_item,"")
fun help_item (name, action) =
let
val label = LabelStrings.get_label name
val id = LabelStrings.get_action name
in
WindowsGui.addCommandHandler(real_parent, id, fn _ => action());
ITEM (label, id)
end
fun getBitmap args =
(MLWorks.Internal.Runtime.environment "win32 get splash bitmap") args
fun paintBitmap dc =
(MLWorks.Internal.Runtime.environment "win32 paint splash bitmap") dc
val adWindow = ref WindowsGui.nullWindow
val licWindow = ref WindowsGui.nullWindow
fun mkDialog (resourceName, windowRef, creationFn) =
let
val w =
if (WindowsGui.isWindow (!windowRef)) then (!windowRef)
else WindowsGui.createDialog(WindowsGui.getModuleHandle(""),
real_parent, resourceName)
val _ = if (w = WindowsGui.nullWindow) then
Crash.impossible "dialog resource not found\n"
else ()
fun destroyW _ = (WindowsGui.destroyWindow w;
windowRef := WindowsGui.nullWindow)
fun addCommands () =
(WindowsGui.addCommandHandler(w, ok_id, destroyW);
WindowsGui.addMessageHandler(real_parent, WindowsGui.WM_SHOWWINDOW,
minimizefun w))
in
if ((!windowRef) = WindowsGui.nullWindow) then
(ignore (creationFn w);
addCommands();
windowRef := w)
else ();
WindowsGui.showWindow (w, WindowsGui.SW_SHOW);
WindowsGui.bringWindowToTop w
end
fun personalAd () =
mkDialog ("PERSONAL_AD", adWindow,
fn w => (ignore (getBitmap (w, 2));
WindowsGui.centerWindow (w, WindowsGui.getDesktopWindow());
WindowsGui.addMessageHandler(w, WindowsGui.WM_PAINT,
fn _ => (ignore (paintBitmap (WindowsGui.getDC w)); NONE))))
fun mlwLicensing () = mkDialog ("MLWORKS_LICENSE", licWindow, fn w => ())
val help_items =
let
val open_web_location : string -> unit = env "win32 open web location"
val source_path_opt = Getenv.get_source_path()
val source_path =
if isSome(source_path_opt) then
valOf(source_path_opt)
else ""
val doc_path = source_path ^ "\\documentation\\"
fun guide () =
open_web_location (doc_path ^ "guide\\html\\index.htm")
fun reference () =
open_web_location (doc_path ^ "reference\\html\\index.htm")
fun install () =
open_web_location (doc_path ^ "installation-notes\\html\\index.htm")
fun relnotes () =
open_web_location (doc_path ^ "release-notes\\html\\index.htm")
val aboutMLW =
if (Version.edition() = Version.PERSONAL) then
[("personalAd", personalAd), ("mlwLicensing", mlwLicensing)]
else [("mlwLicensing", mlwLicensing)]
in
(map help_item
[("userGuide", guide),
("referenceMan", reference),
("installationHelp", install),
("releaseNotes", relnotes)]) @
[SEPARATOR] @
(map help_item aboutMLW)
end
in
app add_item
(help_items @ [ITEM ("&About MLWorks...", IDM_ABOUT)]);
WindowsGui.appendMenu
(menu,[WindowsGui.MF_POPUP],WindowsGui.SUBMENU help_menu,"&Help")
end
val menu = WindowsGui.createMenu ()
val itemfns = map (add_item menu false) menuspec
fun update_fn _ =
(app
(fn NONE => ()
| SOME f => f ())
itemfns;
SOME WindowsGui.nullWord)
in
add_help menu;
WindowsGui.addMessageHandler (real_parent,WindowsGui.WM_INITMENU,update_fn);
if isPodium then
setItems menuspec
else
changeItems (parent, menuspec);
WindowsGui.setMenu (real_parent,menu)
end
fun get_graph_menuspec (close, graph) = [close, graph]
fun get_tools_menuspec (tools_buttons, update_fn) =
CASCADE ("tools", tools_buttons @
[SEPARATOR,
DYNAMIC ("", update_fn, k true)], fn _ => true)
datatype ToolButton = TB_SEP | TB_TOGGLE | TB_PUSH | TB_GROUP | TB_TOGGLE_GROUP
datatype ToolState = CHECKED | ENABLED | HIDDEN | GRAYED | PRESSED | WRAP
datatype ToolButtonSpec = TOOLBUTTON of
{style: ToolButton,
states: ToolState list,
tooltip_id: int,
name: string}
fun make_toolbar (parent, bmp_id, buttonSpec) =
let
val i2w = WindowsGui.intToWord
val pwin = CapiTypes.get_real parent
val toolbar_id = WindowsGui.newControlId()
val num_buttons = Lists.length buttonSpec
val button_count = ref 0
fun add_button (TOOLBUTTON
{style = style, states = states,
tooltip_id = tip_id, name = name}) =
let
val id = if (style = TB_PUSH) orelse (style = TB_TOGGLE) then
WindowsGui.newControlId()
else
WindowsGui.nullWord
val style =
case style of
TB_SEP => WindowsGui.TBSTYLE_SEP
| TB_TOGGLE => WindowsGui.TBSTYLE_CHECK
| TB_PUSH => WindowsGui.TBSTYLE_BUTTON
| TB_GROUP => WindowsGui.TBSTYLE_GROUP
| TB_TOGGLE_GROUP => WindowsGui.TBSTYLE_CHECKGROUP
fun get_state state =
case state of
CHECKED => WindowsGui.TBSTATE_CHECKED
| ENABLED => WindowsGui.TBSTATE_ENABLED
| HIDDEN => WindowsGui.TBSTATE_HIDDEN
| GRAYED => WindowsGui.TBSTATE_INDETERMINATE
| PRESSED => WindowsGui.TBSTATE_PRESSED
| WRAP => WindowsGui.TBSTATE_WRAP
val tb_states = map get_state states
fun addCommand () =
let
val (name, act_ref, sens_ref) = getMenuRef name menuItems
handle Lists.Find =>
if (name <> "interruptButton") then raise Lists.Find
else ("interruptButton", ref (fn () => ()), ref (fn () => true))
fun command _ = (!act_ref)()
in
(WindowsGui.addCommandHandler(pwin, id, command);
sens_ref)
end
val sens_ref_opt = case style of
WindowsGui.TBSTYLE_CHECK => SOME (addCommand())
| WindowsGui.TBSTYLE_BUTTON => SOME (addCommand())
| _ => NONE
val bitmap_index =
if (style = WindowsGui.TBSTYLE_SEP) then
0
else
(!button_count)
in
if (style <> WindowsGui.TBSTYLE_SEP) then button_count := (!button_count) + 1 else ();
(name, sens_ref_opt, (bitmap_index, id, tb_states, [style], i2w tip_id, 0))
end
val buttonList = map add_button buttonSpec
val buttons = map #3 buttonList
val widg = WindowsGui.createToolbarEx
{parent = pwin, styles = [WindowsGui.TBSTYLE_TOOLTIPS, WindowsGui.WS_CHILD],
bmp_id = i2w bmp_id, toolbar_id = toolbar_id,
num_bmps = num_buttons, num_buttons = num_buttons,
x_bitmap = 16, y_bitmap = 16,
x_button = 16, y_button = 16,
buttons = buttons}
val processNotify : WindowsGui.hwnd * WindowsGui.wparam * WindowsGui.lparam -> unit =
env "win32 process notify"
fun process_notify (w,l) = (processNotify (widg,w,l); NONE)
fun get_sens (name, sens, (_,id,_,_,_,_)) = (name, id, fn () => (!(valOf(sens))) () )
val sens_list = Lists.filterp (fn (_,sens_ref_opt,_) => isSome sens_ref_opt) buttonList
val idsensList = map get_sens sens_list
fun setButtonState (id, states) =
WindowsGui.sendMessage(widg, WindowsGui.TB_SETSTATE, WindowsGui.WPARAM id,
WindowsGui.LPARAM (WindowsGui.tbStatesToWord states))
fun isToggle name = Lists.member (name, map (fn (s,_,_) => s) (!toggles))
fun toggleChecked name =
if isToggle name then
!(#2 (Lists.findp (fn (s,_,_) => (s = name)) (!toggles)))
else
false
fun get_states (name, sens_fn) =
(if sens_fn() then WindowsGui.TBSTATE_ENABLED else WindowsGui.TBSTATE_INDETERMINATE) ::
(if (isToggle name) andalso (toggleChecked name) then [WindowsGui.TBSTATE_CHECKED]
else [])
fun update_one (name, id, sens_fn) = setButtonState (id, get_states(name, sens_fn))
fun update _ = (app (ignore o update_one) idsensList; NONE)
val interrupt = env "win32 interrupt"
val set_interrupt_window = env "nt set interrupt window"
val (_,_,(_,interrupt_id,_,_,_,_)) =
Lists.findp (fn (name,_,_) => (name = "interruptButton")) buttonList
in
ignore(set_interrupt_window widg);
WindowsGui.addCommandHandler(pwin, interrupt_id, fn _ => interrupt());
update_ref := (fn () => (app (ignore o update_one) idsensList; ()));
WindowsGui.addMessageHandler(pwin, WindowsGui.WM_NCACTIVATE, update);
WindowsGui.addMessageHandler(pwin, WindowsGui.WM_PARENTNOTIFY, update);
WindowsGui.addMessageHandler(pwin, WindowsGui.WM_INITMENU, update);
WindowsGui.addMessageHandler(pwin, WindowsGui.WM_NOTIFY, process_notify);
CapiTypes.REAL (widg, parent)
end
val sendMessageNoResult = ignore o WindowsGui.sendMessage;
fun set_gui_font window =
let
val WindowsGui.OBJECT gui_font =
WindowsGui.getStockObject (WindowsGui.DEFAULT_GUI_FONT)
handle
WindowsGui.WindowSystemError _ =>
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
sendMessageNoResult
(window,
WindowsGui.WM_SETFONT,
WindowsGui.WPARAM gui_font,
WindowsGui.LPARAM (WindowsGui.intToWord 0))
end
handle WindowsGui.WindowSystemError _ => ()
fun make_buttons (parent, menuspec) =
let
val real_parent = CapiTypes.get_real parent
val right_margin = 5
val spacing = 5
val top_margin = 5
val height = 20
val internal_space = 25
val xref = ref right_margin
val dc = WindowsGui.getDC real_parent
val first_radio = ref true
val _ =
let
val WindowsGui.OBJECT gui_font =
WindowsGui.getStockObject (WindowsGui.DEFAULT_GUI_FONT)
handle
WindowsGui.WindowSystemError _ =>
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
ignore(WindowsGui.selectObject (dc, WindowsGui.OBJECT gui_font));
()
end
handle WindowsGui.WindowSystemError _ => ()
fun do_one (PUSH (name,callback,sensitive)) =
let
val id = LabelStrings.get_action name
val label = LabelStrings.get_label name
val (twidth,_) = WindowsGui.getTextExtentPoint (dc,label)
val width = twidth + internal_space
val button =
WindowsGui.createWindow
{class = "BUTTON",
name = label,
styles = [WindowsGui.WS_CHILD,WindowsGui.BS_PUSHBUTTON],
width = 10,
height = 10,
parent = real_parent,
menu = id}
fun set_sensitivity () =
(ignore(WindowsGui.enableWindow (button,sensitive()));
())
in
set_gui_font button;
WindowsGui.moveWindow (button,!xref,top_margin,width,height,false);
WindowsGui.showWindow (button,WindowsGui.SW_SHOW);
WindowsGui.updateWindow button;
xref := !xref + width + spacing;
WindowsGui.addCommandHandler (real_parent,id,fn n => callback ());
WindowsGui.addCommandHandler
(WindowsGui.getParent real_parent, id, fn n => callback ());
set_sensitivity
end
| do_one (TOGGLE (name, get, set, sensitive)) =
let
val id = LabelStrings.get_action name
val label = LabelStrings.get_label name
val (twidth,_) = WindowsGui.getTextExtentPoint (dc,label)
val width = twidth + internal_space
val button =
WindowsGui.createWindow
{class = "BUTTON",
name = label,
styles = [WindowsGui.WS_CHILD,WindowsGui.BS_AUTOCHECKBOX],
width = 10,
height = 10,
parent = real_parent,
menu = id}
fun set_sensitivity () =
(WindowsGui.checkDlgButton (real_parent, id, if get() then 1 else 0);
ignore(WindowsGui.enableWindow (button,sensitive()));
())
in
set_gui_font button;
WindowsGui.moveWindow (button,!xref,top_margin,width,height,false);
WindowsGui.showWindow (button,WindowsGui.SW_SHOW);
WindowsGui.updateWindow button;
xref := !xref + width + spacing;
WindowsGui.addCommandHandler
(WindowsGui.getParent real_parent, id, fn n => set(not(get())));
WindowsGui.addCommandHandler
(WindowsGui.getParent (WindowsGui.getParent real_parent), id, fn n => set(not(get())));
set_sensitivity
end
| do_one (RADIO (name, get, set, sensitive)) =
let
val id = LabelStrings.get_action name
val label = LabelStrings.get_label name
val (twidth,_) = WindowsGui.getTextExtentPoint (dc,label)
val width = twidth + internal_space
val button =
WindowsGui.createWindow
{class = "BUTTON",
name = label,
styles = [WindowsGui.WS_CHILD,WindowsGui.BS_AUTORADIOBUTTON] @
(if (!first_radio) then [WindowsGui.WS_GROUP] else []),
width = 10,
height = 10,
parent = real_parent,
menu = id}
fun set_sensitivity () =
(WindowsGui.checkDlgButton (real_parent, id, if get() then 1 else 0);
ignore(WindowsGui.enableWindow (button,sensitive()));
())
in
set_gui_font button;
first_radio := false;
WindowsGui.moveWindow (button,!xref,top_margin,width,height,false);
WindowsGui.showWindow (button,WindowsGui.SW_SHOW);
WindowsGui.updateWindow button;
xref := !xref + width + spacing;
WindowsGui.addCommandHandler
(real_parent, id, fn n => set true);
WindowsGui.addCommandHandler
(WindowsGui.getParent real_parent, id, fn n => set true);
WindowsGui.addCommandHandler
(WindowsGui.getParent (WindowsGui.getParent real_parent), id, fn n => set true);
set_sensitivity
end
| do_one (LABEL (name)) =
let
val label = LabelStrings.get_label name
val (twidth,_) = WindowsGui.getTextExtentPoint (dc,label)
val width = twidth + internal_space
val button =
WindowsGui.createWindow
{class = "STATIC",
name = label,
styles = [WindowsGui.WS_CHILD,WindowsGui.SS_CENTER],
width = 10,
height = 10,
parent = real_parent,
menu = WindowsGui.nullWord}
in
set_gui_font button;
WindowsGui.moveWindow (button,!xref,top_margin,width,height,false);
WindowsGui.showWindow (button,WindowsGui.SW_SHOW);
WindowsGui.updateWindow button;
xref := !xref + width + spacing;
fn _ => ()
end
| do_one (SLIDER (name,min,max,set_value)) =
let
val width = 300
val id = LabelStrings.get_action name
val label = LabelStrings.get_label name
val curpos = ref 0
val line_increment = (max - min) div 50
val page_increment = (max - min) div 10
val slider =
WindowsGui.createWindow
{class = "SCROLLBAR",
name = label,
styles = [WindowsGui.WS_CHILD,WindowsGui.SBS_HORZ],
width = 10,
height = 10,
parent = real_parent,
menu = id}
fun handler (WindowsGui.WPARAM wparam, WindowsGui.LPARAM lparam) =
let
val code = WindowsGui.loword wparam
val convert = WindowsGui.convertSbValue
val pos =
if code = convert WindowsGui.SB_THUMBPOSITION then
WindowsGui.hiword wparam
else if code = convert WindowsGui.SB_LINELEFT then
let val temp_pos = !curpos - line_increment
in
if (temp_pos < min) then min else temp_pos
end
else if code = convert WindowsGui.SB_LINERIGHT then
let val temp_pos = !curpos + line_increment
in
if (temp_pos > max) then max else temp_pos
end
else if code = convert WindowsGui.SB_PAGELEFT then
let val temp_pos = !curpos - page_increment
in
if (temp_pos < min) then min else temp_pos
end
else if code = convert WindowsGui.SB_PAGERIGHT then
let val temp_pos = !curpos + page_increment
in
if (temp_pos > max) then max else temp_pos
end
else !curpos
in
set_value pos;
curpos := pos;
WindowsGui.setScrollPos (slider, WindowsGui.SB_CTL, pos, true);
SOME WindowsGui.nullWord
end
in
WindowsGui.setScrollRange (slider,WindowsGui.SB_CTL,min,max,false);
WindowsGui.moveWindow (slider,!xref,top_margin,width,height,false);
WindowsGui.showWindow (slider,WindowsGui.SW_SHOW);
WindowsGui.updateWindow slider;
xref := !xref + width + spacing;
WindowsGui.addMessageHandler (real_parent,WindowsGui.WM_HSCROLL,handler);
fn _ => ()
end
| do_one _ = (fn _ => ())
val set_sensitivity_fns = map do_one menuspec
in
WindowsGui.releaseDC (real_parent,dc);
{update = fn _ => app (fn f => f ()) set_sensitivity_fns,
set_focus = fn _ => ()}
end
datatype ItemTemplate =
ITEMTEMPLATE of
{styles: WindowsGui.window_style list,
x: int,
y: int,
width: int,
height: int,
id : WindowsGui.word,
class : string,
text: string}
datatype Template =
TEMPLATE of
{styles: WindowsGui.window_style list,
x: int,
y: int,
width: int,
height: int,
title: string,
items: ItemTemplate list,
nitems: int}
datatype selection = SINGLE | EXTENDED
datatype OptionSpec =
OPTSEPARATOR |
OPTLABEL of string |
OPTTOGGLE of string * (unit -> bool) * (bool -> bool) |
OPTTEXT of string * (unit -> string) * (string -> bool) |
OPTINT of string * (unit -> int) * (int -> bool) |
OPTRADIO of OptionSpec list |
OPTCOMBO of string * (unit -> string * string list) * (string -> bool) |
OPTLIST of string *
(unit -> string list * string list) *
(string list -> bool) *
selection
val create_dialog_indirect : Template * WindowsGui.hwnd -> WindowsGui.hwnd = env "nt create dialog indirect"
val dialog_box_indirect : Template * WindowsGui.hwnd -> int = env "nt dialog box indirect"
fun munge_string s =
let
fun munge ([],acc) = implode (rev acc)
| munge (#"\013" :: #"\010" :: rest,acc) = munge (rest, #"\010" :: #"\013" :: acc)
| munge (#"\n" ::rest,acc) = munge (rest, #"\010" :: #"\013" :: acc)
| munge (c::rest,acc) = munge (rest,c::acc)
in
munge (explode s,[])
end
fun set_text (window,s) =
let
val string_word = WindowsGui.makeCString (munge_string s)
in
sendMessageNoResult (window,WindowsGui.WM_SETTEXT,
WindowsGui.WPARAM (WindowsGui.nullWord),
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end
fun text_size text =
WindowsGui.wordToInt (WindowsGui.sendMessage (text,
WindowsGui.WM_GETTEXTLENGTH,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
fun get_text (window) =
let
val size = text_size window
val buffer = WindowsGui.malloc (size+1)
val _ = WindowsGui.sendMessage (window,
WindowsGui.WM_GETTEXT,
WindowsGui.WPARAM (WindowsGui.intToWord (size+1)),
WindowsGui.LPARAM buffer)
val _ = WindowsGui.setByte (buffer,size,0)
val result = WindowsGui.wordToString buffer
in
WindowsGui.free buffer;
result
end
val x_margin = 4
val y_margin = 4
val item_height = 10
val text_height = 12
val item_sep = 4
val item_width = 150
val text_width = 75
val int_width = 40
fun bell () = WindowsGui.messageBeep WindowsGui.MB_OK
exception InvalidControl of string
fun convert_spec (title,action,speclist) =
let
val yref = ref y_margin
fun do_spec (acc as (templates,initializers,setters,ids),spec) =
case spec of
OPTSEPARATOR =>
let
val template =
ITEMTEMPLATE
{styles = [WindowsGui.SS_GRAYRECT,
WindowsGui.WS_CHILD,
WindowsGui.WS_VISIBLE],
x = 0,
y = !yref,
width = item_width + x_margin + x_margin,
height = 1,
class = "STATIC",
text = "",
id = WindowsGui.nullWord}
in
yref := !yref + item_sep;
(template :: templates,initializers,setters,ids)
end
| OPTLABEL string =>
let
val template =
ITEMTEMPLATE
{styles = [WindowsGui.SS_LEFT,
WindowsGui.WS_CHILD,
WindowsGui.WS_VISIBLE],
x = x_margin,
y = !yref,
width = item_width,
height = item_height,
class = "STATIC",
text = LabelStrings.get_label string,
id = WindowsGui.nullWord}
in
yref := !yref + item_height + item_sep;
(template :: templates,initializers,setters,ids)
end
| OPTTOGGLE (string,get,set) =>
let
val id = LabelStrings.get_action string
val template =
ITEMTEMPLATE
{styles = [WindowsGui.BS_AUTOCHECKBOX,
WindowsGui.WS_CHILD,
WindowsGui.WS_TABSTOP,
WindowsGui.WS_VISIBLE],
x = x_margin,
y = !yref,
width = item_width,
height = item_height,
class = "BUTTON",
text = LabelStrings.get_label string,
id = id}
fun initializer hwnd =
let val value = get ()
in WindowsGui.checkDlgButton (hwnd,id,if value then 1 else 0)
end
fun setter hwnd =
let
val value = (WindowsGui.isDlgButtonChecked (hwnd,id) = 1)
val settable = set value
in
if settable then () else (bell ();
initializer hwnd)
end
in
yref := !yref + item_height + item_sep;
(template :: templates,
initializer:: initializers,
setter :: setters,
id :: ids)
end
| OPTTEXT (string,get,set) =>
let
val id = LabelStrings.get_action string
val text_template =
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.WS_TABSTOP,
WindowsGui.WS_BORDER,
WindowsGui.WS_VISIBLE,
WindowsGui.ES_AUTOHSCROLL],
x = x_margin,
y = !yref,
width = text_width,
height = text_height,
class = "EDIT",
text = "",
id = id}
val label_template =
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.SS_LEFT,
WindowsGui.WS_VISIBLE],
x = x_margin + text_width + 2,
y = !yref + text_height - item_height,
width = item_width - text_width,
height = item_height,
class = "STATIC",
text = LabelStrings.get_label string,
id = WindowsGui.nullWord}
fun get_input_pane hwnd = WindowsGui.getDlgItem (hwnd,id)
fun initializer hwnd = set_text (get_input_pane hwnd,get ())
fun setter hwnd =
let val settable = set (get_text (get_input_pane hwnd))
in if settable then () else (bell();
initializer hwnd)
end
in
yref := !yref + text_height + item_sep;
(label_template :: text_template :: templates,
initializer:: initializers,
setter :: setters,
id :: ids)
end
| OPTCOMBO _ => raise InvalidControl "Combo box only available as resource"
| OPTLIST _ => raise InvalidControl "List box only available as resource"
| OPTINT (string,get,set) =>
let
val id = LabelStrings.get_action string
val text_template =
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.WS_BORDER,
WindowsGui.WS_TABSTOP,
WindowsGui.WS_VISIBLE],
x = x_margin,
y = !yref,
width = int_width,
height = text_height,
class = "EDIT",
text = "",
id = id}
val label_template =
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.SS_LEFT,
WindowsGui.WS_VISIBLE],
x = x_margin + int_width + 2,
y = !yref + text_height - item_height,
width = item_width - int_width,
height = item_height,
class = "STATIC",
text = LabelStrings.get_label string,
id = WindowsGui.nullWord}
fun get_input_pane hwnd = WindowsGui.getDlgItem (hwnd,id)
fun initializer hwnd = set_text (get_input_pane hwnd,Int.toString (get ()))
fun setter hwnd =
let
val num = Int.fromString (get_text (get_input_pane hwnd))
val settable =
(case num of
SOME n => set n
| _ => false)
in
if settable then () else
(bell();
initializer hwnd)
end
in
yref := !yref + text_height + item_sep;
(label_template :: text_template :: templates,
initializer:: initializers,
setter :: setters,
id :: ids)
end
| OPTRADIO (itemspecs) =>
let
val first = ref true
fun do_one (OPTTOGGLE (string,get,set)) =
let
val is_first = !first
val _ = first := false
val id = LabelStrings.get_action string
val text = LabelStrings.get_label string
val template =
ITEMTEMPLATE
{styles = (if is_first then [WindowsGui.WS_GROUP,
WindowsGui.WS_TABSTOP]
else []) @
[WindowsGui.WS_CHILD,
WindowsGui.WS_VISIBLE,
WindowsGui.BS_AUTORADIOBUTTON],
x = x_margin,
y = !yref,
width = item_width,
height = item_height,
class = "BUTTON",
text = text,
id = id}
in
yref := !yref + item_height + item_sep;
(template,(get,id,string),(set,id))
end
| do_one _ = Crash.impossible "Non toggle button in OPTRADIO"
val stuff = map do_one itemspecs
val new_templates = map #1 stuff
val getids = map #2 stuff
val setids = map #3 stuff
val new_ids = map #2 getids
fun get_ends (a::rest) =
let
fun aux [] = (a,a)
| aux [b] = (a,b)
| aux (b::rest) = aux rest
in
aux rest
end
| get_ends _ = Crash.impossible "get_ends"
val (first,last) = get_ends new_ids
fun initializer hwnd =
Lists.iterate
(fn (get,id,string) =>
WindowsGui.checkDlgButton (hwnd,id,if get() then 1 else 0))
getids
fun setter hwnd =
Lists.iterate
(fn (set,id) =>
if WindowsGui.isDlgButtonChecked (hwnd,id) = 1
then set true
else true)
setids
in
(rev new_templates @ templates,
initializer :: initializers,
setter :: setters,
rev new_ids @ ids)
end
val (itemspecs,initializers,setters,ids) = Lists.reducel do_spec (([],[],[],[]),speclist)
val button_specs =
ITEMTEMPLATE
{styles = [WindowsGui.SS_GRAYRECT,
WindowsGui.WS_CHILD,
WindowsGui.WS_VISIBLE],
x = 0,
y = !yref,
width = item_width + x_margin + x_margin,
height = 1,
class = "STATIC",
text = "",
id = WindowsGui.nullWord} ::
map
(fn (x,text,id,default) =>
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
if default then WindowsGui.BS_DEFPUSHBUTTON
else WindowsGui.BS_PUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = x,
y = !yref + item_sep,
width = 30,
height = 10,
class = "BUTTON",
text = text,
id = id})
[(x_margin,"OK",ok_id,true),
(x_margin + 35,"Apply",apply_id,false),
(x_margin + 70,"Reset",reset_id,false),
(x_margin + 105,"Cancel",cancel_id,false)]
val items = rev itemspecs @ button_specs
in
(TEMPLATE
{styles = [WindowsGui.WS_POPUP,
WindowsGui.WS_CAPTION,
WindowsGui.WS_SYSMENU,
WindowsGui.WS_VISIBLE],
x = 40,
y = 40,
width = item_width + x_margin + x_margin + 2,
height = !yref + y_margin + item_sep + 10,
title = title,
items = items,
nitems = length items},
rev initializers, rev setters,
ids,
(ok_id,apply_id,reset_id,cancel_id))
end
fun strip_string_controls (s:string):string =
implode (List.filter (fn c=>not(c < #" ")) (explode s))
local
fun itemTextWidth (hwnd, combo) (_, (itemIndex, maxWidth)) =
let
val w = WindowsGui.WPARAM (WindowsGui.intToWord itemIndex)
val l = WindowsGui.LPARAM WindowsGui.nullWord
val message =
if combo then
WindowsGui.CB_GETLBTEXTLEN
else
WindowsGui.LB_GETTEXTLEN
val r = WindowsGui.sendMessage (hwnd, message, w, l)
val maxWidth' = Int.max (WindowsGui.wordToInt r, maxWidth)
in
(itemIndex+1, maxWidth')
end
in
fun itemsMaxTextWidth (hwnd, items, combo) =
let
val (_, maxWidth) =
List.foldl (itemTextWidth (hwnd, combo)) (0, 0) items
in
maxWidth
end
end
fun add_items (hwnd, items, message) =
let
fun do_one item =
let val CString = WindowsGui.makeCString (strip_string_controls item)
in
sendMessageNoResult (hwnd, message,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM CString);
WindowsGui.free CString
end
in
Lists.iterate do_one items
end
fun resource_convert_spec speclist =
let
fun getResID name = WindowsGui.intToWord (ControlName.getResID name)
fun do_spec ((initializers, setters, ids), idspec) =
case idspec of
OPTTOGGLE (name, get, set) =>
let
val id = getResID name
fun initializer hwnd =
let val value = get()
in WindowsGui.checkDlgButton (hwnd, id, if value then 1 else 0)
end
fun setter hwnd =
let val settable = set (WindowsGui.isDlgButtonChecked (hwnd, id) = 1)
in if settable then () else (bell(); initializer hwnd)
end
in
(initializer :: initializers,
setter :: setters,
id :: ids)
end
| OPTTEXT (name, get, set) =>
let
val id = getResID name
fun get_input_pane hwnd = WindowsGui.getDlgItem (hwnd,id)
fun initializer hwnd = set_text (get_input_pane hwnd, get())
fun setter hwnd =
let val settable = set (get_text (get_input_pane hwnd))
in if settable then () else (bell();
initializer hwnd)
end
in
(initializer :: initializers,
setter :: setters,
id :: ids)
end
| OPTCOMBO (name, get, set) =>
let
val id = getResID name
fun set_horizontal_extent hwnd =
let
val i2w = WindowsGui.intToWord
val charWidthInPixels = WindowsGui.loword(WindowsGui.getDialogBaseUnits())
val (init_string, items) = get()
val maxTextWidth = itemsMaxTextWidth (hwnd, items, true)
in
if maxTextWidth > 0 then
sendMessageNoResult
(hwnd, WindowsGui.CB_SETHORIZONTALEXTENT,
WindowsGui.WPARAM (i2w (maxTextWidth * charWidthInPixels)),
WindowsGui.LPARAM WindowsGui.nullWord)
else ()
end
fun initializer dialog =
let
val hwnd = WindowsGui.getDlgItem (dialog, id)
in
sendMessageNoResult(hwnd, WindowsGui.CB_RESETCONTENT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord);
add_items (hwnd, #2(get()), WindowsGui.CB_ADDSTRING);
set_horizontal_extent hwnd;
set_text (hwnd, #1(get()))
end
fun setter dialog =
let
val new_text = get_text (WindowsGui.getDlgItem (dialog, id))
val settable = set new_text
in
if settable then () else
(bell();
initializer dialog)
end
in
(initializer :: initializers,
setter :: setters,
id :: ids)
end
| OPTLIST (name, get, set, sel_type) =>
let
val id = getResID name
val null_w = WindowsGui.WPARAM WindowsGui.nullWord
val null_l = WindowsGui.LPARAM WindowsGui.nullWord
fun set_horizontal_extent hwnd =
let
val i2w = WindowsGui.intToWord
val charWidthInPixels = WindowsGui.loword(WindowsGui.getDialogBaseUnits())
val (items, sel_items) = get()
val maxTextWidth = itemsMaxTextWidth (hwnd, items, false)
in
if maxTextWidth > 0 then
sendMessageNoResult
(hwnd, WindowsGui.LB_SETHORIZONTALEXTENT,
WindowsGui.WPARAM (i2w (maxTextWidth * charWidthInPixels)),
null_l)
else ()
end
fun select_items hwnd [] = ()
| select_items hwnd (str::rest) =
let
val w = WindowsGui.WPARAM (WindowsGui.intToWord (~1))
val CString = WindowsGui.makeCString (strip_string_controls str)
val l = WindowsGui.LPARAM CString
in
if sel_type = EXTENDED then
let val index =
WindowsGui.sendMessage(hwnd, WindowsGui.LB_FINDSTRING, w, l)
in
sendMessageNoResult
(hwnd,
WindowsGui.LB_SETSEL,
WindowsGui.WPARAM (WindowsGui.intToWord 1),
WindowsGui.LPARAM index)
end
else
sendMessageNoResult(hwnd, WindowsGui.LB_SELECTSTRING, w, l);
WindowsGui.free CString;
select_items hwnd rest
end
fun initializer dialog =
let
val hwnd = WindowsGui.getDlgItem (dialog, id)
val (all_items, sel_items) = get()
in
sendMessageNoResult(hwnd, WindowsGui.LB_RESETCONTENT, null_w, null_l);
add_items (hwnd, all_items, WindowsGui.LB_ADDSTRING);
set_horizontal_extent hwnd;
select_items hwnd sel_items
end
fun setter dialog =
let
val i2w = WindowsGui.intToWord
val hwnd = WindowsGui.getDlgItem (dialog, id)
val (all_items, sel_items) = get()
val maxWidth = itemsMaxTextWidth (hwnd, all_items, false)
fun get_list_string index =
let
val w = WindowsGui.WPARAM (WindowsGui.intToWord index)
val size = WindowsGui.wordToInt (WindowsGui.sendMessage
(hwnd, WindowsGui.LB_GETTEXTLEN, w, null_l))
val buffer = WindowsGui.malloc (size+1)
val _ = WindowsGui.sendMessage
(hwnd,
WindowsGui.LB_GETTEXT, w,
WindowsGui.LPARAM buffer)
val _ = WindowsGui.setByte (buffer,size,0)
val result = WindowsGui.wordToString buffer
in
WindowsGui.free buffer;
result
end
fun get_count () =
WindowsGui.wordToInt
(WindowsGui.sendMessage
(hwnd, WindowsGui.LB_GETCOUNT, null_w, null_l))
fun get_sel 0 sel_list = sel_list
| get_sel i sel_list =
let
val w = WindowsGui.WPARAM (WindowsGui.intToWord (i-1))
val selected =
(WindowsGui.sendMessage (hwnd, WindowsGui.LB_GETSEL, w, null_l)) <>
WindowsGui.nullWord
val new_sel_list =
if selected then
(get_list_string (i-1)) :: sel_list
else sel_list
in
get_sel (i-1) new_sel_list
end
val settable = set (get_sel (get_count()) [])
in
if settable then () else
(bell();
initializer dialog)
end
in
(initializer :: initializers,
setter :: setters,
id :: ids)
end
| OPTINT (name, get, set) =>
let
val id = getResID name
fun get_input_pane hwnd = WindowsGui.getDlgItem (hwnd, id)
fun initializer hwnd = set_text (get_input_pane hwnd,
Int.toString (get()))
fun setter hwnd =
let
val num = Int.fromString (get_text (get_input_pane hwnd))
val settable = (case num of
SOME n => set n
| _ => false)
in
if settable then () else
(bell(); initializer hwnd)
end
in
(initializer :: initializers,
setter :: setters,
id :: ids)
end
| OPTRADIO toggle_list =>
let
fun do_one (OPTTOGGLE (name, get, set)) =
(getResID name, get, set)
| do_one _ = Crash.impossible "Non toggle button in OPTRADIO"
val idgetset_list = map do_one toggle_list
fun initializer hwnd =
Lists.iterate (fn (id, get, set) =>
WindowsGui.checkDlgButton (hwnd, id, if get() then 1 else 0))
idgetset_list
fun setter hwnd =
Lists.iterate (fn (id, get, set) =>
if WindowsGui.isDlgButtonChecked (hwnd, id) = 1 then
set true
else true) idgetset_list
val new_ids = map #1 idgetset_list
in
(initializer :: initializers,
setter :: setters,
rev new_ids @ ids)
end
| _ => (initializers, setters, ids)
val (initializers, setters, ids) = Lists.reducel do_spec (([],[],[]), speclist)
in
(TEMPLATE {styles=[], height=0, width=0, x=0, y=0, title="", items=[], nitems=0},
initializers, setters, ids, (ok_id, apply_id, reset_id, cancel_id))
end
fun isResourceDialog str =
case str of
"modeOptions" => true
| "editorOptions" => true
| "environmentOptions" => true
| "languageOptions" => true
| "compilerOptions" => true
| _ => false
fun resourceDialog (window, resName) =
WindowsGui.createDialog (WindowsGui.getModuleHandle(""), window, resName)
fun create_dialog (parent, title, name, action, spec) =
let
val (template,initializers,setters,ids,(ok_id,apply_id,reset_id,cancel_id)) =
if (isResourceDialog name) then
resource_convert_spec spec
else
convert_spec (title, action, spec)
val window_ref = ref (WindowsGui.nullWindow)
val changed_ref = ref false
fun set_sensitivity window =
(ignore(WindowsGui.enableWindow (WindowsGui.getDlgItem (window,apply_id),!changed_ref));
ignore(WindowsGui.enableWindow (WindowsGui.getDlgItem (window,reset_id),!changed_ref));
())
val real_parent = CapiTypes.get_real parent
in
(fn _ =>
if WindowsGui.isWindow (!window_ref)
then (windowHandle := (!window_ref);
WindowsGui.bringWindowToTop (!window_ref))
else
let
val dbox = if (isResourceDialog name) then
resourceDialog (real_parent, name)
else
create_dialog_indirect (template,real_parent)
val _ = if (dbox = WindowsGui.nullWindow) then
Crash.impossible "no resource for dialog\n"
else ()
val _ = windowHandle := dbox
val _ = WindowsGui.registerPopupWindow (dbox)
fun destroy _ =
(WindowsGui.unregisterPopupWindow dbox;
WindowsGui.destroyWindow dbox)
in
WindowsGui.addMessageHandler(real_parent, WindowsGui.WM_SHOWWINDOW,
minimizefun dbox);
Lists.iterate
(fn id => WindowsGui.addCommandHandler(dbox, id,
fn _ =>
if not (!changed_ref)
then (changed_ref := true;
set_sensitivity dbox; ())
else ()))
ids;
WindowsGui.addCommandHandler(dbox, ok_id,
fn _ => (if !changed_ref
then (Lists.iterate (fn f => f dbox) setters; action ())
else ();
destroy()));
WindowsGui.addCommandHandler(dbox, apply_id,
fn _ => (app (fn f => f dbox) setters;
changed_ref := false;
set_sensitivity dbox;
action ()));
WindowsGui.addCommandHandler(dbox, reset_id,
fn _ => (app (fn f => f dbox) initializers;
changed_ref := false;
set_sensitivity dbox));
WindowsGui.addCommandHandler(dbox, cancel_id, destroy);
window_ref := dbox;
app (fn f => f dbox) initializers;
changed_ref := false;
set_sensitivity dbox;
WindowsGui.showWindow (dbox,WindowsGui.SW_SHOW)
end,
fn _ => if WindowsGui.isWindow (!window_ref)
then
(app (fn f => f (!window_ref)) initializers;
changed_ref := false;
set_sensitivity (!window_ref))
else ())
end
local
fun tty_action (hwnd,_) =
WindowsGui.endDialog (hwnd,2)
fun exit_action (hwnd,_) =
WindowsGui.endDialog (hwnd,1)
fun cancel_action (hwnd,_) =
WindowsGui.endDialog (hwnd,0)
val quit_on_exit : unit -> unit = env "nt quit on exit"
in
fun exit_dialog (parent,applicationShell,has_controlling_tty) =
let
val tty_id = WindowsGui.newControlId ()
val exit_id = WindowsGui.newControlId ()
val cancel_id = i2w cancel_env
val _ =
(WindowsGui.addCommandHandler (WindowsGui.nullWindow,
tty_id,tty_action);
WindowsGui.addCommandHandler (WindowsGui.nullWindow,
exit_id,exit_action);
WindowsGui.addCommandHandler (WindowsGui.nullWindow,
cancel_id,cancel_action))
val button_y = 20
val (dialog_width,items) =
if has_controlling_tty
then
(190,
[ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.SS_CENTER,
WindowsGui.WS_VISIBLE],
x = 0,
y = 5,
width = 190,
height = 15,
class = "STATIC",
text = "Select an action:",
id = WindowsGui.nullWord},
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,WindowsGui.BS_DEFPUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = 5,
y = button_y,
width = 60,
height = 12,
class = "BUTTON",
text = "Return to TTY",
id = tty_id},
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,WindowsGui.BS_DEFPUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = 75,
y = button_y,
width = 60,
height = 12,
class = "BUTTON",
text = "Exit MLWorks",
id = exit_id},
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,WindowsGui.BS_PUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = 145,
y = button_y,
width = 40,
height = 12,
class = "BUTTON",
text = "Cancel",
id = cancel_id}])
else
(120,
[ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.SS_CENTER,
WindowsGui.WS_VISIBLE],
x = 0,
y = 5,
width = 100,
height = 15,
class = "STATIC",
text = "Select an action:",
id = WindowsGui.nullWord},
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,WindowsGui.BS_DEFPUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = 5,
y = button_y,
width = 60,
height = 12,
class = "BUTTON",
text = "Exit MLWorks",
id = exit_id},
ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,WindowsGui.BS_PUSHBUTTON,
WindowsGui.WS_VISIBLE],
x = 75,
y = button_y,
width = 40,
height = 12,
class = "BUTTON",
text = "Cancel",
id = cancel_id}])
val template =
TEMPLATE
{styles = [WindowsGui.WS_POPUP,
WindowsGui.WS_CAPTION,
WindowsGui.DS_MODALFRAME],
x = 40,
y = 40,
width = dialog_width,
height = 40,
title = "Exit Dialog",
items = items,
nitems = length items}
val result = dialog_box_indirect (template,CapiTypes.get_real parent)
fun do_destroy () =
(WindowsGui.destroyWindow (CapiTypes.get_real applicationShell);
MLWorks.Internal.StandardIO.resetIO())
val uninitialise : unit -> unit =
MLWorks.Internal.Runtime.environment "uninitialise mlworks"
in
case result of
0 => ()
| 1 => (do_destroy o quit_on_exit) ()
| 2 => (do_destroy o uninitialise) ()
| _ => print "Bad return from exit dialog"
end
end
end
;
