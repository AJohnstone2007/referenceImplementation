require "^.utils.__terminal";
require "^.basis.__list";
require "^.basis.__string";
require "../basis/__int";
require "../basis/word";
require "../utils/lists";
require "../main/version";
require "../gui/menus";
require "windows_gui";
require "capitypes";
require "labelstrings";
require "../gui/capi";
functor Capi (structure Lists : LISTS
structure WindowsGui : WINDOWS_GUI
structure LabelStrings : LABELSTRINGS
structure CapiTypes : CAPITYPES
structure Menus : MENUS
structure Word32 : WORD
structure Version : VERSION
sharing type LabelStrings.AcceleratorFlag = WindowsGui.accelerator_flag
sharing type Menus.Widget = CapiTypes.Widget
sharing type CapiTypes.Hwnd = WindowsGui.hwnd
sharing type WindowsGui.word = LabelStrings.word = Word32.word
): CAPI =
struct
val do_debug = false
fun debug s = if do_debug then Terminal.output (s() ^ "\n") else ()
fun ddebug s = Terminal.output(s() ^ "\n")
datatype Point = POINT of { x : int, y : int }
datatype Region = REGION of { x : int, y :int, width : int, height :int }
type Widget = CapiTypes.Widget
type Font = unit
fun env s = MLWorks.Internal.Value.cast (MLWorks.Internal.Runtime.environment s)
exception SubLoopTerminated
exception WindowSystemError = WindowsGui.WindowSystemError
exception Unimplemented of string
fun N n = Int.toString n
fun W w = "<word>"
fun dummy s = debug (fn _ => s ^ " unimplemented")
fun unimplemented s = (dummy s; raise Unimplemented s)
fun max (x:int,y:int) = if x > y then x else y
val evaluating = ref false;
fun munge_string s =
let
fun munge ([],acc) = implode (rev acc)
| munge (#"\013" :: #"\010" :: rest,acc) = munge (rest, #"\010" :: #"\013" :: acc)
| munge (#"\n" ::rest,acc) = munge (rest, #"\010" :: #"\013" :: acc)
| munge (c::rest,acc) = munge (rest,c::acc)
in
munge (explode s,[])
end
fun strip_string_controls (s:string):string =
implode (List.filter (fn c=>not(c < #" ")) (explode s))
val main_windows : (CapiTypes.Widget * string) list ref = ref []
fun push (a,r) = r := a :: !r
fun delete (a,[]) = []
| delete (a,((item as (a', _)) :: rest)) =
if a = a' then delete (a,rest) else item::delete (a,rest)
fun add_main_window (w,title) = push ((w, title), main_windows)
fun remove_main_window w = main_windows := delete (w,!main_windows)
fun get_main_windows () = (!main_windows)
val text_handlers = ref []
fun restart () =
(main_windows := [];
text_handlers := [])
datatype WidgetAttribute =
PanedMargin of bool
| Position of int * int
| Size of int * int
| ReadOnly of bool
datatype WidgetClass = Frame | Graphics | Label | Button | Text | RowColumn | Paned | Form
fun convert_class class =
case class of
Label => ("STATIC",[WindowsGui.SS_LEFT])
| Button => ("BUTTON",[WindowsGui.BS_PUSHBUTTON])
| Text => ("EDIT",[WindowsGui.WS_BORDER])
| _ => ("Frame",[])
val sendMessageNoResult = ignore o WindowsGui.sendMessage;
fun set_text (window,s) =
let
val string_word = WindowsGui.makeCString (munge_string s)
in
sendMessageNoResult (CapiTypes.get_real window,WindowsGui.WM_SETTEXT,
WindowsGui.WPARAM (WindowsGui.nullWord),
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end
fun remove_menu widget = ()
fun reveal window =
(WindowsGui.showWindow (window,WindowsGui.SW_SHOWNORMAL);
WindowsGui.updateWindow window)
fun hide window =
WindowsGui.showWindow (CapiTypes.get_real window,WindowsGui.SW_HIDE)
datatype window_ex_style =
WS_EX_DLGMODALFRAME |
WS_EX_STATICEDGE |
WS_EX_WINDOWEDGE
fun createWindowEx (details :
{ex_styles: window_ex_style list,
class: string,
name: string,
x: int,
y: int,
width : int,
height : int,
parent: WindowsGui.hwnd,
menu : WindowsGui.word,
styles : WindowsGui.window_style list}) : WindowsGui.hwnd =
(MLWorks.Internal.Runtime.environment "win32 create window ex") details
fun create_revealed args =
let
val window = WindowsGui.createWindow args
in
reveal window;
window
end
fun convert_name (Label,name) = LabelStrings.get_label name
| convert_name (Button,name) = LabelStrings.get_label name
| convert_name (_,name) = LabelStrings.get_title name
val default_width = 720
val toplevel_width = default_width
val toplevel_height = 100
val graphics_height = 200
val next_window = ref (0,0)
fun class_height class =
case class of
Frame => 120
| Graphics => 120
| Label => 20
| Button => 20
| Text => 26
| RowColumn => 30
| Paned => 120
| Form => 120
structure Event =
struct
type Modifier = int
val meta_modifier = 0
datatype Button = LEFT | RIGHT | OTHER
end
fun despatch_text (window, char, alt_on) =
let
fun scan [] = false
| scan ((window',handler)::rest) =
if window = CapiTypes.get_real window'
then handler (char,if alt_on then [Event.meta_modifier] else [])
else scan rest
in
scan (!text_handlers)
end
fun set_text_font window =
let
val WindowsGui.OBJECT text_font =
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
sendMessageNoResult
(CapiTypes.get_real window,
WindowsGui.WM_SETFONT,
WindowsGui.WPARAM text_font,
WindowsGui.LPARAM (WindowsGui.intToWord 1))
end
handle WindowsGui.WindowSystemError _ => ()
fun set_gui_font window =
let
val WindowsGui.OBJECT gui_font =
WindowsGui.getStockObject (WindowsGui.DEFAULT_GUI_FONT)
handle
WindowsGui.WindowSystemError _ =>
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
sendMessageNoResult
(CapiTypes.get_real window,
WindowsGui.WM_SETFONT,
WindowsGui.WPARAM gui_font,
WindowsGui.LPARAM (WindowsGui.intToWord 1))
end
handle WindowsGui.WindowSystemError _ => ()
fun class_postaction (window,class) =
case class of
Text =>
(set_text_font window;
set_text (window, ""))
| Label => set_gui_font window
| Button => set_gui_font window
| _ => ()
fun getStylesFromAttributes [] = []
| getStylesFromAttributes ((ReadOnly true)::rest) =
WindowsGui.ES_READONLY :: getStylesFromAttributes(rest)
| getStylesFromAttributes (another::rest) =
getStylesFromAttributes(rest)
fun getSize ((Size (w,h))::rest) = SOME (w,h)
| getSize (notsize::rest) = getSize rest
| getSize [] = NONE
fun make_widget (name,class,parent,attributes) =
let
val (class_name,styles) = convert_class class
val class_styles = case class of
Text => [WindowsGui.WS_BORDER,
WindowsGui.ES_MULTILINE,
WindowsGui.ES_AUTOHSCROLL]
| _ => []
val (width, height) =
getOpt (getSize attributes, (default_width, class_height class))
val window =
WindowsGui.createWindow {class = class_name,
name = convert_name (class,name),
width = width,
height = height,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_CHILD] @
class_styles @
styles @
getStylesFromAttributes (attributes)}
val widget = CapiTypes.REAL (window,parent)
in
class_postaction (widget,class);
widget
end
fun make_managed_widget (name,class,parent,attributes) =
let
val widget = make_widget (name, class, parent, attributes)
in
reveal (CapiTypes.get_real widget);
widget
end
fun make_context_label parent =
let
val window =
WindowsGui.createWindow
{class = "STATIC",
name = "contextLabel",
height = 20,
width = default_width,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_CHILD,WindowsGui.SS_CENTER]}
in
reveal window;
CapiTypes.REAL (window,parent)
end
fun make_main_subwindows (parent,has_context_label) =
let
val label_window =
if has_context_label
then SOME (make_context_label parent)
else NONE
in
(parent,parent,label_window)
end
fun make_subwindow parent = parent
fun min_child (owner, window, visible) =
WindowsGui.addMessageHandler(owner, WindowsGui.WM_SHOWWINDOW,
fn (WindowsGui.WPARAM w, WindowsGui.LPARAM l) =>
(if (!visible) then
if (w = WindowsGui.nullWord) then
WindowsGui.showWindow(window, WindowsGui.SW_HIDE)
else
WindowsGui.showWindow(window, WindowsGui.SW_SHOW)
else ();
NONE))
fun getNextWindowPos () =
let
val (curX, curY) = !next_window
val inc = 30
in
if (curX < (100 + inc * 6)) then
next_window := (curX + inc, curY + inc)
else
next_window := (100, curY - inc * 6);
(curX, curY)
end
fun initialize_application (name, title, has_controlling_tty) =
let
val window = WindowsGui.mainInit ()
val widget = CapiTypes.REAL (window,CapiTypes.NONE)
val height = ref 0
fun get_height window =
let
val _ = WindowsGui.setWindowPos (window, {x=0, y=0, height=200, width = 1000})
val (WindowsGui.RECT {bottom=c_height, ...}) = WindowsGui.getClientRect window
in
height := 200 - (c_height - 28)
end
val sizing = ref false
fun getminmax window (_, WindowsGui.LPARAM addr) =
let
val (_, _, _, maxtrack) = WindowsGui.getMinMaxInfo addr
val desk = WindowsGui.getDesktopWindow()
val (WindowsGui.RECT {right=r, left=l, ...}) = WindowsGui.getWindowRect desk
fun p (xc, yc) = WindowsGui.POINT {x=xc, y=yc}
in
if (!height) = 0 then
if (!sizing) then () else
(sizing := true;
get_height window;
ignore(WindowsGui.setMinMaxInfo (addr, p(r+6, !height), p(~4,~4), p(0,0), maxtrack));
sizing := false)
else
(ignore(WindowsGui.setMinMaxInfo (addr, p(r+6, !height), p(~4,~4), p(0,0), maxtrack));
());
NONE
end
in
next_window := (50, (!height) + 50);
restart ();
WindowsGui.addMessageHandler (window,WindowsGui.WM_DESTROY,
fn _ => (WindowsGui.postQuitMessage 0;
NONE));
WindowsGui.addMessageHandler (window,WindowsGui.WM_CLOSE,
fn _ => (if not (!evaluating) then
Menus.exit_dialog (widget,widget,has_controlling_tty)
else ();
SOME (WindowsGui.nullWord)));
WindowsGui.addMessageHandler (window, WindowsGui.WM_GETMINMAXINFO, getminmax window);
WindowsGui.addMessageHandler (window, WindowsGui.WM_ACTIVATEAPP,
fn _ => (ignore(WindowsGui.setFocus window); NONE));
WindowsGui.addCommandHandler (WindowsGui.nullWindow,
LabelStrings.get_action "exit",
fn _ => (if not (!evaluating) then
Menus.exit_dialog (widget,widget,has_controlling_tty)
else () ));
WindowsGui.setAcceleratorTable (WindowsGui.createAcceleratorTable (LabelStrings.accelerators));
widget
end
fun make_main_window {name, title, parent, contextLabel, winMenu, pos:int * int} =
let
val window =
createWindowEx
{ex_styles = [],
class = "Toplevel",
name = LabelStrings.get_title name,
x = #1(pos),
y = #2(pos),
height = toplevel_height,
width = toplevel_width,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_OVERLAPPED_WINDOW]}
val widget = CapiTypes.REAL (window,parent)
val label_window =
if contextLabel
then SOME (make_context_label widget)
else NONE
in
set_text (widget,title);
if winMenu then
push ((widget, title), main_windows)
else ();
WindowsGui.addMessageHandler (window,WindowsGui.WM_DESTROY,
fn _ => (remove_main_window widget;
NONE));
(widget,widget,widget,label_window)
end
fun make_main_popup {name, title, parent, contextLabel, visibleRef, pos: int * int} =
let
val window =
createWindowEx
{ex_styles = [],
class = "Toplevel",
name = LabelStrings.get_title name,
x = #1(pos),
y = #2(pos),
height = toplevel_height,
width = toplevel_width,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_OVERLAPPED_WINDOW]}
val widget =CapiTypes.REAL (window,parent)
val label_window =
if contextLabel
then SOME (make_context_label widget)
else NONE
val _ = WindowsGui.registerPopupWindow (window)
fun destroy_handler _ =
(WindowsGui.unregisterPopupWindow window;
NONE)
in
min_child (CapiTypes.get_real parent, window, visibleRef);
if (!visibleRef) then
push ((widget, title), main_windows)
else ();
WindowsGui.addMessageHandler (window,WindowsGui.WM_DESTROY,destroy_handler);
set_text (widget,title);
(widget,widget,widget,label_window)
end
fun make_messages_popup (parent, visible) =
make_main_popup {name = "messages",
title = "System Messages",
parent = parent,
contextLabel = false,
visibleRef = visible,
pos = getNextWindowPos()}
fun make_popup_shell (name,parent,attributes,visible) =
let
val (width, height) =
getOpt(getSize attributes, (toplevel_width, toplevel_height))
val window =
WindowsGui.createWindow
{class = "Toplevel",
name = LabelStrings.get_title name,
width = width,
height = height,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_OVERLAPPED_WINDOW
] @
getStylesFromAttributes (attributes)}
val _ = WindowsGui.registerPopupWindow (window)
fun destroy_handler _ =
(WindowsGui.unregisterPopupWindow window;
NONE)
in
min_child (CapiTypes.get_real parent, window, visible);
WindowsGui.addMessageHandler (window,WindowsGui.WM_DESTROY,destroy_handler);
CapiTypes.REAL (window,parent)
end
fun make_toplevel_shell (name,title,parent,attributes) =
let
val (width, height) =
getOpt (getSize attributes, (toplevel_width, toplevel_height))
val window =
WindowsGui.createWindow
{class = "Toplevel",
name = LabelStrings.get_title name,
width = width,
height = height,
parent = WindowsGui.nullWindow,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_OVERLAPPED_WINDOW] @
getStylesFromAttributes (attributes)}
val widget =CapiTypes.REAL (window,CapiTypes.NONE)
in
set_text (widget,title);
WindowsGui.showWindow (window,WindowsGui.SW_SHOW);
WindowsGui.updateWindow window;
widget
end
fun text_subclass window =
let
val ml_window_proc = WindowsGui.getMlWindowProc()
val original_window_proc =
WindowsGui.setWindowLong (window,
WindowsGui.GWL_WNDPROC,
ml_window_proc)
fun char_handler (WindowsGui.WPARAM wparam,WindowsGui.LPARAM lparam) =
if despatch_text
(window, String.str(chr (WindowsGui.wordToInt wparam)), false) then
SOME (WindowsGui.nullWord)
else
NONE
fun syschar_handler (WindowsGui.WPARAM wparam,WindowsGui.LPARAM lparam) =
if despatch_text
(window, String.str(chr (WindowsGui.wordToInt wparam)), true) then
SOME (WindowsGui.nullWord)
else
NONE
in
WindowsGui.addNewWindow (window,original_window_proc);
WindowsGui.addMessageHandler (window, WindowsGui.WM_CHAR, char_handler);
WindowsGui.addMessageHandler (window, WindowsGui.WM_SYSCHAR, syschar_handler)
end
val scrolled_text_id = WindowsGui.newControlId ()
fun make_scrolled_text (name,parent,attributes) =
let
val (width, height) =
getOpt (getSize attributes, (default_width, 200))
val window =
create_revealed
{class = "EDIT",
name = LabelStrings.get_title name,
width = width,
height = height,
parent = CapiTypes.get_real parent,
menu = scrolled_text_id,
styles = [WindowsGui.WS_CHILD,
WindowsGui.WS_BORDER,
WindowsGui.WS_HSCROLL,WindowsGui.WS_VSCROLL,
WindowsGui.ES_MULTILINE,
WindowsGui.ES_AUTOHSCROLL,WindowsGui.ES_AUTOVSCROLL] @
getStylesFromAttributes (attributes)}
fun command_handler (hwnd,event) =
if event = WindowsGui.wordToInt (WindowsGui.messageToWord (WindowsGui.EN_MAXTEXT))
then Terminal.output("MAXTEXT received\n")
else ()
val widget = CapiTypes.REAL (window,parent)
fun scrolling (WindowsGui.WPARAM w, WindowsGui.LPARAM l) =
let
val scroll_value = WindowsGui.loword w
val sb_left = WindowsGui.convertSbValue (WindowsGui.SB_PAGELEFT)
val sb_right = WindowsGui.convertSbValue (WindowsGui.SB_PAGERIGHT)
val (ireturned, wsize, wmask, imin, imax, wpage, ipos, itrackpos) =
WindowsGui.getScrollInfo (window, WindowsGui.SB_HORZ)
val w2i = WindowsGui.wordToInt
val (isize, imask, ipage) = (w2i wsize, w2i wmask, w2i wpage)
val pager = Int.min (imax, ipos + ipage - 1)
val pagel = Int.max (imin, ipos - ipage + 1)
val hi_word = Word32.fromInt (WindowsGui.convertSbValue WindowsGui.SB_THUMBPOSITION)
val lo_word_r = Word32.<< (Word32.fromInt pager, 0w16)
val lo_word_l = Word32.<< (Word32.fromInt pagel, 0w16)
in
if scroll_value = sb_right then
SOME (WindowsGui.sendMessage(window, WindowsGui.WM_HSCROLL,
WindowsGui.WPARAM (Word32.+ (lo_word_r, hi_word)),
WindowsGui.LPARAM WindowsGui.nullWord))
else if scroll_value = sb_left then
SOME (WindowsGui.sendMessage(window, WindowsGui.WM_HSCROLL,
WindowsGui.WPARAM (Word32.+ (lo_word_l, hi_word)),
WindowsGui.LPARAM WindowsGui.nullWord))
else NONE
end
in
set_text_font widget;
set_text (widget,"");
text_subclass window;
WindowsGui.addCommandHandler (CapiTypes.get_real parent,scrolled_text_id,command_handler);
WindowsGui.addMessageHandler (window, WindowsGui.WM_CONTEXTMENU,
fn _ => SOME WindowsGui.nullWord);
WindowsGui.addMessageHandler (window, WindowsGui.WM_HSCROLL, scrolling);
sendMessageNoResult (window,
WindowsGui.EM_LIMITTEXT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord);
(widget,widget)
end
fun set_min_window_size (widget, min_x, min_y) =
let
val min_window_size : WindowsGui.wparam * WindowsGui.lparam * int * int -> unit =
env "win32 min window size"
in
WindowsGui.addMessageHandler(CapiTypes.get_real widget, WindowsGui.WM_SIZING,
fn (wp, lp) =>
(min_window_size (wp, lp, min_x, min_y);
NONE))
end
fun make_scrolllist {parent, name, select_fn, action_fn, print_fn} =
let
val scrolllist_id = WindowsGui.newControlId ()
val items_ref = ref []
val window =
create_revealed
{class = "LISTBOX",
name = LabelStrings.get_title name,
width = default_width,
height = 150,
parent = CapiTypes.get_real parent,
menu = scrolllist_id,
styles = [WindowsGui.WS_CHILD,WindowsGui.WS_BORDER,
WindowsGui.WS_VSCROLL,WindowsGui.WS_HSCROLL,
WindowsGui.LBS_NOTIFY, WindowsGui.LBS_NOINTEGRALHEIGHT]}
val widget =CapiTypes.REAL (window,parent)
local
fun itemTextWidth (_, (itemIndex, maxWidth)) =
let
val w = WindowsGui.WPARAM (WindowsGui.intToWord itemIndex)
val l = WindowsGui.LPARAM WindowsGui.nullWord
val r = WindowsGui.sendMessage (window, WindowsGui.LB_GETTEXTLEN, w, l)
val maxWidth' = max (WindowsGui.wordToInt r, maxWidth)
in
(itemIndex+1, maxWidth')
end
in
fun itemsMaxTextWidth items =
let
val (_, maxWidth) = List.foldl itemTextWidth (0, 0) items
in
maxWidth
end
end
fun add_items opts items =
((Lists.iterate
(fn item =>
let
val string = strip_string_controls (print_fn opts item)
val string_word = WindowsGui.makeCString string
in
sendMessageNoResult (window,WindowsGui.LB_ADDSTRING,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end)
items;
items_ref := !items_ref @ items);
let
val i2w = WindowsGui.intToWord
val charWidthInPixels = WindowsGui.loword(WindowsGui.getDialogBaseUnits())
val maxTextWidth = itemsMaxTextWidth (!items_ref)
in
if maxTextWidth > 0 then
sendMessageNoResult
(window,WindowsGui.LB_SETHORIZONTALEXTENT,
WindowsGui.WPARAM (i2w (maxTextWidth * charWidthInPixels)),
WindowsGui.LPARAM WindowsGui.nullWord)
else ()
end)
fun set_items opts items =
(sendMessageNoResult (window,WindowsGui.LB_RESETCONTENT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord);
items_ref := [];
add_items opts items)
val select_fn' = select_fn (widget,widget,set_items,add_items)
val action_fn' = action_fn (widget,widget,set_items,add_items)
fun select_handler (_,event) =
if event = 1 then
let
val item =
WindowsGui.wordToSignedInt
(WindowsGui.sendMessage
(window,
WindowsGui.LB_GETCURSEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
in
debug (fn _ => "Selection of " ^ N item ^ "\n");
if item >= 0 then select_fn' (Lists.nth (item,!items_ref)) else ()
end
else if event = 2 then
let
val item =
WindowsGui.wordToSignedInt
(WindowsGui.sendMessage
(window,
WindowsGui.LB_GETCURSEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
in
debug (fn _ => "Double click of " ^ N item ^ "\n");
if item >= 0 then action_fn' (Lists.nth (item,!items_ref)) else ()
end
else
debug (fn _ => "Event " ^ N event ^ " received for list\n")
in
set_text_font widget;
WindowsGui.addCommandHandler
(CapiTypes.get_real parent,scrolllist_id,select_handler);
{scroll=widget, list=widget, set_items=set_items, add_items=add_items}
end
fun make_file_selection_box (name,parent,attributes) =
unimplemented "make_file_selection_box"
fun destroy window = WindowsGui.destroyWindow (CapiTypes.get_real window)
fun initialize_toplevel window = reveal (CapiTypes.get_real window)
fun initialize_application_shell shell =
WindowsGui.showWindow(CapiTypes.get_real shell, WindowsGui.SW_SHOWMAXIMIZED)
fun to_front window =
let val (state, _, _, _) = WindowsGui.getWindowPlacement (CapiTypes.get_real window)
in
if state = 0 then
WindowsGui.showWindow (CapiTypes.get_real window, WindowsGui.SW_RESTORE)
else if state = 1 then
WindowsGui.showWindow (CapiTypes.get_real window, WindowsGui.SW_MINIMIZE)
else
WindowsGui.showWindow (CapiTypes.get_real window, WindowsGui.SW_MAXIMIZE);
WindowsGui.bringWindowToTop (CapiTypes.get_real window)
end
fun transfer_focus (from,to) =
WindowsGui.addMessageHandler (CapiTypes.get_real from,WindowsGui.WM_SETFOCUS,
fn _ =>
(ignore(WindowsGui.setFocus (CapiTypes.get_real to));
SOME WindowsGui.nullWord))
fun set_sensitivity (widget,sensitivity) = ()
fun set_label_string (label,s) =
set_text (label,s)
fun set_focus w = (ignore(WindowsGui.setFocus (CapiTypes.get_real w)); ())
fun set_busy w = (ignore(WindowsGui.setCursor (WindowsGui.loadCursor WindowsGui.IDC_WAIT)); ())
fun unset_busy w = (ignore(WindowsGui.setCursor (WindowsGui.loadCursor WindowsGui.IDC_ARROW)); ())
fun widget_size widget =
let
val WindowsGui.RECT {left,top,right,bottom} = WindowsGui.getWindowRect (CapiTypes.get_real widget)
in
(right-left,bottom-top)
end
fun widget_pos widget =
let
val WindowsGui.RECT {left,top, ...} = WindowsGui.getWindowRect (CapiTypes.get_real widget)
in
(left, top)
end
val set_message_window : CapiTypes.Hwnd -> unit = env "nt set message widget"
fun set_message_widget widget =
set_message_window (CapiTypes.get_real widget)
val no_message_widget : unit -> unit = env "nt no message widget"
fun move_window (widget,x,y) =
let
val (w,h) = widget_size widget
in
WindowsGui.moveWindow (CapiTypes.get_real widget,x,y,w,h,true)
end
fun size_window (widget, w, h) =
let
val WindowsGui.RECT {left,top,right,bottom} = WindowsGui.getWindowRect (CapiTypes.get_real widget)
in
WindowsGui.moveWindow (CapiTypes.get_real widget, left, top, w, h, true)
end
fun init_size (window, sizeOpt) =
if isSome (sizeOpt) then
let val (w, h) = valOf(sizeOpt)
in
size_window (window, w, h)
end
else ()
fun get_pointer_pos () =
let
val WindowsGui.POINT {x,y} = WindowsGui.getCursorPos ()
in
(x,y)
end
fun set_close_callback (shell, close_fun) =
WindowsGui.addMessageHandler(CapiTypes.get_real shell, WindowsGui.WM_CLOSE,
fn _ => (ignore(close_fun()); SOME (WindowsGui.nullWord)))
fun event_loop continue =
(while (!continue) do
if WindowsGui.doInput ()
then raise SubLoopTerminated
else ();
debug (fn _ => "sub loop exited\n"))
fun main_loop () = WindowsGui.mainLoop ()
datatype FileType = DIRECTORY | FILE
fun open_file_dialog (parent, mask, multi) =
let
val (ext, desc) =
case mask of
".sml" => ("sml", "SML files")
| ".mo" => ("mo", "MLWorks objects files")
| ".mlp" => ("mlp", "MLWorks projects files")
| "" => ("*", "All files")
| s => (s, "")
in
(case WindowsGui.openFileDialog (CapiTypes.get_real parent, desc, ext, multi) of
[] => NONE
| s => SOME s)
end
fun open_dir_dialog parent =
(case WindowsGui.openDirDialog (CapiTypes.get_real parent)
of "" => NONE
| s => SOME s)
val set_dir_dialog = open_dir_dialog
fun save_as_dialog (parent, mask) =
case mask of
".sml" =>
(case WindowsGui.saveDialog (CapiTypes.get_real parent, "SML files", "sml") of
"" => NONE
| s => SOME s)
| ".img" =>
(case WindowsGui.saveDialog (CapiTypes.get_real parent, "Image files", "img") of
"" => NONE
| s => SOME s)
| ".mlp" =>
(case WindowsGui.saveDialog (CapiTypes.get_real parent, "MLW project files", "mlp") of
"" => NONE
| s => SOME s)
| _ =>
(case WindowsGui.saveDialog (CapiTypes.get_real parent, "All files", "*") of
"" => NONE
| s => SOME s)
val send_message = Menus.send_message
fun makeYesNoCancel (parent, question, cancelButton) () =
let
val yes = env "win32 yes id"
val no = env "win32 no id"
val cancel = env "win32 cancel id"
val yesNoStyle = if cancelButton then WindowsGui.MB_YESNOCANCEL else WindowsGui.MB_YESNO
val answer = WindowsGui.messageBox (CapiTypes.get_real parent, question,
"MLWorks", yesNoStyle :: [WindowsGui.MB_APPLMODAL])
in
if answer = (env "win32 yes id") then SOME true
else
if answer = (env "win32 no id") then SOME false
else NONE
end
fun find_dialog (parent, searchFn, spec) =
let
val real_parent = CapiTypes.get_real parent
val {findStr, caseOpt, downOpt, wordOpt} = spec
val dialogRef = ref WindowsGui.nullWindow
val id_cancel : int = env "win32 cancel id"
fun searching (_, WindowsGui.LPARAM addr) =
let
val {searchStr, matchCase, searchDown, wholeWord, findNext, closing} =
WindowsGui.getFindFlags addr
in
if findNext then
searchFn {searchStr=searchStr,
matchCase=matchCase,
searchDown=searchDown,
wholeWord=wholeWord}
else ();
NONE
end
in
WindowsGui.addMessageHandler(real_parent, WindowsGui.FINDMSGSTRING, searching);
fn () =>
(if ((!dialogRef) = WindowsGui.nullWindow) then
(dialogRef := WindowsGui.findDialog (real_parent,
findStr, caseOpt, downOpt, wordOpt);
WindowsGui.registerPopupWindow (!dialogRef);
WindowsGui.addCommandHandler(!dialogRef, WindowsGui.intToWord id_cancel,
fn _ => hide (CapiTypes.REAL(!dialogRef, parent)));
WindowsGui.addMessageHandler(!dialogRef, WindowsGui.WM_DESTROY,
fn _ => (WindowsGui.unregisterPopupWindow(!dialogRef);
dialogRef := WindowsGui.nullWindow;
SOME (WindowsGui.nullWord))))
else ();
reveal (!dialogRef);
CapiTypes.REAL (!dialogRef, parent))
end
fun with_message (parent,message) f =
let
val _ = set_busy parent
fun reset () = unset_busy parent
val result = f ()
handle exn as SubLoopTerminated => raise exn
| exn => (reset(); raise exn)
in
reset();
result
end
fun beep widget = WindowsGui.messageBeep WindowsGui.MB_OK
structure Callback =
struct
datatype Type =
Activate
| Destroy
| Unmap
| Resize
| ValueChange
fun print_callback c =
case c of
Activate => "Activate"
| Destroy => "Destroy"
| Unmap => "Unmap"
| Resize => "Resize"
| ValueChange => "ValueChange"
fun convert_callback c =
case c of
Activate => NONE
| Destroy => SOME WindowsGui.WM_DESTROY
| Unmap => NONE
| Resize => SOME WindowsGui.WM_SIZE
| ValueChange => SOME WindowsGui.WM_CLOSE
fun getParentIdPair CapiTypes.NONE = (WindowsGui.nullWindow, WindowsGui.nullWord)
| getParentIdPair (CapiTypes.REAL (w, p)) =
(CapiTypes.get_real p, WindowsGui.intToWord (WindowsGui.getDlgCtrlID w))
| getParentIdPair (CapiTypes.FAKE _) = (WindowsGui.nullWindow, WindowsGui.nullWord)
fun add (window,callback,handler) =
case convert_callback callback of
NONE => ()
| SOME WindowsGui.WM_CLOSE =>
let val (p, win_id) = getParentIdPair window
in WindowsGui.addCommandHandler (p, win_id, fn _ => handler())
end
| SOME message =>
WindowsGui.addMessageHandler (CapiTypes.get_real window,message,
fn _ => (handler (); NONE))
end
structure List =
struct
fun get_selected_pos list =
let
val result =
WindowsGui.wordToSignedInt (WindowsGui.sendMessage (CapiTypes.get_real list,
WindowsGui.LB_GETCURSEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
in
if result >= 0
then MLWorks.Internal.Vector.vector [result+1]
else MLWorks.Internal.Vector.vector []
end
fun select_pos (list,pos,notify) =
let
val hwnd = CapiTypes.get_real list
val id = WindowsGui.getDlgCtrlID hwnd
in
sendMessageNoResult (CapiTypes.get_real list,
WindowsGui.LB_SETCURSEL,
WindowsGui.WPARAM (WindowsGui.intToWord (pos-1)),
WindowsGui.LPARAM WindowsGui.nullWord);
if not notify then ()
else
(debug (fn _ => "Notifying\n");
sendMessageNoResult (WindowsGui.getParent (CapiTypes.get_real list),
WindowsGui.WM_COMMAND,
WindowsGui.WPARAM (WindowsGui.intToWord (256 * 256 * 1 + id)),
WindowsGui.LPARAM (WindowsGui.windowToWord hwnd)))
end
fun set_pos (list, pos) =
sendMessageNoResult (CapiTypes.get_real list,
WindowsGui.LB_SETTOPINDEX,
WindowsGui.WPARAM (WindowsGui.intToWord (pos-1)),
WindowsGui.LPARAM WindowsGui.nullWord)
fun add_items (list, items) =
(Lists.iterate
(fn item =>
let
val string = strip_string_controls item
val string_word = WindowsGui.makeCString string
in
sendMessageNoResult
(CapiTypes.get_real list,
WindowsGui.LB_ADDSTRING,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end)
items;
())
end
structure Text =
struct
fun add_del_handler (window, handler) =
let
val window' = CapiTypes.get_real window
fun del_handler (WindowsGui.WPARAM w, WindowsGui.LPARAM l) =
if ((WindowsGui.wordToInt w) = LabelStrings.VK_DELETE) then
(ignore(handler()); SOME (WindowsGui.nullWord))
else
NONE
in
WindowsGui.addMessageHandler (window', WindowsGui.WM_KEYDOWN, del_handler)
end
fun text_size text =
WindowsGui.wordToSignedInt (WindowsGui.sendMessage (CapiTypes.get_real text,
WindowsGui.WM_GETTEXTLENGTH,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
fun get_insertion_position text =
let
val res = WindowsGui.sendMessage (CapiTypes.get_real text,
WindowsGui.EM_GETSEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord)
in
WindowsGui.hiword res
end
fun set_selection (text,pos1,pos2) =
(debug (fn _ => "set_selection " ^ N pos1 ^ ", " ^ N pos2);
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.EM_SETSEL,
WindowsGui.WPARAM (WindowsGui.intToWord pos1),
WindowsGui.LPARAM (WindowsGui.intToWord pos2)))
fun set_insertion_position (text,pos) =
(set_selection (text,pos,pos);
let
val p = get_insertion_position text
in
if p = pos then ()
else debug (fn _ => "Set insertion position has failed: " ^ N pos ^ " " ^ N p)
end)
fun insert (text,pos,str) =
let
val string_word = WindowsGui.makeCString (munge_string str)
in
set_insertion_position (text,pos);
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.EM_REPLACESEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end
fun replace (text,from,to,str) =
let
val string_word = WindowsGui.makeCString (munge_string str)
in
set_selection (text,from,to);
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.EM_REPLACESEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end
val get_last_position = text_size
fun get_string text =
let
val size = text_size text
val buffer = WindowsGui.malloc (size+1)
val _ = WindowsGui.sendMessage (CapiTypes.get_real text,
WindowsGui.WM_GETTEXT,
WindowsGui.WPARAM (WindowsGui.intToWord (size+1)),
WindowsGui.LPARAM buffer)
val _ = WindowsGui.setByte (buffer,size,0)
val result = WindowsGui.wordToString buffer
in
WindowsGui.free buffer;
result
end
fun substring (text,from,size) =
MLWorks.String.substring (get_string text,from,size)
fun set_string (text,s) = set_text (text,s)
fun set_highlight (text, startpos, endpos, b) =
if not b then ()
else
let
val hwnd = CapiTypes.get_real text
val w = WindowsGui.intToWord startpos
in
sendMessageNoResult (hwnd,
WindowsGui.EM_SETSEL,
WindowsGui.WPARAM w,
WindowsGui.LPARAM w);
sendMessageNoResult (hwnd,
WindowsGui.EM_SCROLLCARET,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord)
end
fun get_selection text =
let
val res = WindowsGui.sendMessage (CapiTypes.get_real text,
WindowsGui.EM_GETSEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord)
val start = WindowsGui.loword res
val finish = WindowsGui.hiword res
in
substring (text,start,finish-start)
end
fun remove_selection text =
let
val string_word = WindowsGui.makeCString ""
in
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.EM_REPLACESEL,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM string_word);
WindowsGui.free string_word
end
local
fun lastline (str, ~1) = 0
| lastline (str,n) =
if MLWorks.String.ordof (str, n) = ord #"\n"
then n+1
else lastline (str,n-1)
in
fun get_line_and_index (text,pos) =
let
val str = get_string text
val length = size str
fun nextline n =
if n = length orelse MLWorks.String.ordof (str, n) = ord #"\n" then
n
else
nextline (n+1)
val start = lastline (str,pos-1)
val finish = nextline pos
val result = MLWorks.String.substring (str, start, finish - start)
in
(result,pos - start)
end
fun current_line (text,pos) =
lastline (get_string text,pos-1)
fun end_line (text,pos) =
let
val str = get_string text
val length = size str
fun aux n =
if n = length orelse MLWorks.String.ordof (str, n) = ord #"\n" then
n
else
aux (n+1)
in
if pos > length then pos else aux pos
end
val get_line = #1 o get_line_and_index
end
val convert_text = munge_string
fun text_size s = size (munge_string s)
fun add_handler (window,handler: string * Event.Modifier list -> bool) =
text_handlers := (window,handler) :: !text_handlers
fun get_key_bindings _ = []
fun add_modify_verify _ = ()
val read_only_before_prompt = true
fun cut_selection text =
(sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.WM_CUT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
fun paste_selection text =
(sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.WM_PASTE,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
fun copy_selection text =
(sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.WM_COPY,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord))
val delete_selection = remove_selection
val text_limit = 50000
val text_chunk = 8000
fun check_insertion(text, str, current, marks) =
let
val length = get_last_position text
val size = size str
val max = text_limit-text_chunk
in
if length + size < text_limit then
str
else
if size >= max then
check_insertion(text, String.substring(str, size-max, max), current, marks)
else
let
val reduction = if size > text_chunk then size else text_chunk
val reduction = if reduction >= current then current else reduction
in
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.WM_SETREDRAW,
WindowsGui.WPARAM (WindowsGui.intToWord 0),
WindowsGui.LPARAM WindowsGui.nullWord);
replace (text,0,text_chunk,"");
set_insertion_position (text,current-reduction);
sendMessageNoResult (CapiTypes.get_real text,
WindowsGui.WM_SETREDRAW,
WindowsGui.WPARAM (WindowsGui.intToWord 1),
WindowsGui.LPARAM WindowsGui.nullWord);
Lists.iterate (fn pos => pos := !pos - reduction) marks;
str
end
end
end
fun setAttribute (widget, attrib) =
let
val hwnd = CapiTypes.get_real widget
fun changeStyle (window_style, toAdd) =
let
val style_word = WindowsGui.convertStyle window_style
val cur_value = WindowsGui.getWindowLong (hwnd, WindowsGui.GWL_STYLE)
val new_value =
if (toAdd) then
Word32.orb (cur_value, style_word)
else
Word32.andb (cur_value, Word32.notb (style_word))
in
WindowsGui.setWindowLong (hwnd, WindowsGui.GWL_STYLE, new_value)
end
in
ignore(
case attrib of
Position (x,y) => move_window (widget, x, y)
| Size (w,h) => size_window (widget, w, h)
| ReadOnly tf => ignore(changeStyle (WindowsGui.ES_READONLY, tf))
| PanedMargin m => ())
end
structure Layout =
struct
datatype Class =
MENUBAR of CapiTypes.Widget
| FLEX of CapiTypes.Widget
| FIXED of CapiTypes.Widget
| FILEBOX of CapiTypes.Widget
| PANED of CapiTypes.Widget * (CapiTypes.Widget * Class list) list
| SPACE
fun widget_position window =
let
val window = CapiTypes.get_real window
val WindowsGui.RECT {left,top,...} = WindowsGui.getWindowRect window
val parent = WindowsGui.getParent window
in
if WindowsGui.isNullWindow parent
then (left,top)
else
let
val WindowsGui.POINT{x,y,...} = WindowsGui.screenToClient (parent,WindowsGui.POINT {x=left,y=top})
in
(x,y)
end
end
fun enum_direct_children (w,f) =
let
val realw = CapiTypes.get_real w
fun g subwindow =
if not (WindowsGui.isNullWindow subwindow) andalso
WindowsGui.getParent subwindow = realw
then f (CapiTypes.REAL (subwindow,w))
else ()
in
WindowsGui.enumChildWindows (realw,g)
end
fun count_direct_children w =
let
val count = ref 0
in
enum_direct_children (w, fn _ => count := 1 + !count);
!count
end
fun lay_out (parent, sizeOpt, children) =
let
fun do_one
(child, (maxwidth, y, min_height), (width, height)) =
let
val child = CapiTypes.get_real child
in
if not (WindowsGui.isNullWindow child) then
(debug (fn _ => "do_one: " ^ N width ^ " " ^ N height ^ " " ^ N y);
WindowsGui.moveWindow (child, 0, y, width, height, true);
(max (width, maxwidth), y + height, min_height + height))
else
(maxwidth, y, min_height)
end
fun do_all (w_list, (max_width, y, min_height)) =
Lists.reducel
(fn (a,MENUBAR w) => a
| (a as (_, _, min_height), FLEX w) =>
let
val (maxwidth, y, _) = do_one (w,a,widget_size w)
in
(maxwidth, y, min_height + 10)
end
| (a,FILEBOX w) => do_one (w,a,widget_size w)
| (a,FIXED w) => do_one (w,a,widget_size w)
| (a as (_, _, min_height), PANED (w, panes)) =>
let
val _ = map lay_out (map (fn (w,p) => (w, NONE, p)) panes)
val (max_width', y', min_height') =
Lists.reducel
(fn (a, (_, children)) =>
do_all (children, a))
((0,0,0), panes)
val (maxwidth'', y'', _) =
do_one (w, a, (max_width', y'))
in
(maxwidth'', y'', min_height + min_height')
end
| ((width,y,min_height),SPACE) =>
(width, y, min_height))
((max_width, y, min_height), w_list)
val (total_width, total_height, min_height) =
do_all (children, (0, 0, 0))
val height_ref = ref total_height
val (x,y) = widget_position parent
val (w,h) = widget_size parent
val WindowsGui.RECT {right=cright,bottom=cbottom,left=cleft,top=ctop} =
WindowsGui.getClientRect (CapiTypes.get_real parent)
fun relayout (width,height) =
let
val delta = height - !height_ref
val _ = height_ref := height
val yref = ref 0
fun move_one (window,delta) =
if WindowsGui.isNullWindow (CapiTypes.get_real window)
then ()
else
let
val (_,h) = widget_size window
val newh = h + delta
in
WindowsGui.moveWindow(CapiTypes.get_real window,0,!yref,width,newh,true);
yref := !yref + newh
end
fun do_one (MENUBAR window) = ()
| do_one (FLEX window) =
move_one (window,delta)
| do_one (FILEBOX window) = move_one (window,0)
| do_one (FIXED window) = move_one (window,0)
| do_one (PANED (window, _)) =
let
val num_children = count_direct_children window
in
if num_children = 0
then ()
else
let
val yref = ref 0
val (_,h) = widget_size window
val subheight = (h + delta) div num_children
val first_height = h + delta - ((num_children - 1) * subheight)
val height_ref = ref first_height
in
enum_direct_children
(window,
fn subwindow =>
(WindowsGui.moveWindow (CapiTypes.get_real subwindow,0,!yref,width,!height_ref,true);
yref := !yref + !height_ref;
height_ref := subheight))
end;
move_one (window,delta)
end
| do_one (SPACE) = ()
in
Lists.iterate do_one (children)
end
in
if isSome(sizeOpt) then
init_size(parent, sizeOpt)
else
WindowsGui.moveWindow
(CapiTypes.get_real parent, x, y,
total_width + w - cright,
total_height + h - cbottom,
true);
relayout (total_width, total_height);
WindowsGui.addMessageHandler
(CapiTypes.get_real parent,
WindowsGui.WM_SIZE,
fn (WindowsGui.WPARAM wparam, WindowsGui.LPARAM lparam) =>
(if WindowsGui.wordToInt wparam = 1 then
()
else
let
val width = WindowsGui.loword lparam
val height = WindowsGui.hiword lparam
in
if height < min_height then
()
else
relayout (width, height)
end;
SOME WindowsGui.nullWord))
end
end
fun list_select (parent, name, _) =
let
val shell = make_popup_shell (name,parent, [], ref true)
val form = make_subwindow shell
exception ListSelect
val select_fn_ref = ref (fn _ => raise ListSelect)
val print_fn_ref = ref (fn _ => raise ListSelect)
val exited = ref false;
fun exit _ = if !exited then () else (destroy shell; exited := true)
val {scroll, set_items, ...} =
make_scrolllist
{parent = form,
name = "listSelect",
select_fn = fn _ => fn x => (exit();(!select_fn_ref) x),
action_fn = fn _ => fn _ => (),
print_fn = fn _ => (!print_fn_ref)}
val dialogButtons = make_managed_widget ("dialogButtons", RowColumn,form,[])
val {update = buttons_updatefn, ...} =
Menus.make_buttons
(dialogButtons,
[Menus.PUSH ("cancel",
exit,
fn _ => true)])
fun moveit () =
let
val width = 200
val height = 221
val (x,y) = get_pointer_pos ()
val desktopRect = WindowsGui.getWindowRect (WindowsGui.getDesktopWindow() )
fun get_list_rect () = WindowsGui.getWindowRect (CapiTypes.get_real shell)
fun shiftWindow (hwnd, rect, desktop, new_x, new_y) =
(WindowsGui.moveWindow (hwnd, new_x, new_y, width, height, true);
unobscureWindow (hwnd, get_list_rect(), desktop))
and unobscureWindow (hwnd,
(rect as (WindowsGui.RECT {top=t, left=l, right=r, bottom=b})),
(desktop as (WindowsGui.RECT {top=dt, left=dl, right=dr, bottom=db}))) =
if (l < dl) then
shiftWindow (hwnd, rect, desktop, 0, t)
else if (r > dr) then
shiftWindow (hwnd, rect, desktop, dr - width, t)
else if (b > db) then
shiftWindow (hwnd, rect, desktop, l, db - height)
else ()
in
(WindowsGui.moveWindow (CapiTypes.get_real shell,x-100,y+10,width,height,true);
unobscureWindow (CapiTypes.get_real shell, get_list_rect(), desktopRect))
end
fun popup (items,select_fn,print_fn) =
(moveit ();
select_fn_ref := select_fn;
print_fn_ref := print_fn;
set_items () items;
reveal (CapiTypes.get_real form);
exit)
in
Layout.lay_out
(form, NONE,
[Layout.FLEX scroll,
Layout.SPACE,
Layout.FIXED dialogButtons,
Layout.SPACE]);
popup
end
fun clipboard_set (widget,s) =
let
val window = CapiTypes.get_real widget
in
if WindowsGui.openClipboard (window)
then
(WindowsGui.emptyClipboard ();
WindowsGui.setClipboardData s;
WindowsGui.closeClipboard ())
else Terminal.output("Can't open Clipboard\n")
end
fun clipboard_get (w,handler) =
if WindowsGui.openClipboard (WindowsGui.nullWindow)
then
let
val result = WindowsGui.getClipboardData ()
val _ = WindowsGui.closeClipboard ()
in
handler result
end
else
(Terminal.output("Can't open Clipboard\n");
())
fun clipboard_empty widget =
if WindowsGui.openClipboard (CapiTypes.get_real widget)
then
let
val result = WindowsGui.getClipboardData ()
val _ = WindowsGui.closeClipboard ()
in
result = ""
end
else true
local
val x_margin = 4
val y_margin = 4
val item_height = 10
val text_height = 12
val item_sep = 4
val item_width = 150
val text_width = 75
val yref = ref y_margin
val namestr = ref ""
val numstr = ref ""
val continue = ref true
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
val create_dialog_indirect : Template * WindowsGui.hwnd -> WindowsGui.hwnd =
env "nt create dialog indirect"
val name_id = WindowsGui.newControlId()
val num_id = WindowsGui.newControlId()
val continue_id = WindowsGui.newControlId()
fun make_label (string, label_width, down) =
let
val label_template = ITEMTEMPLATE
{styles = [WindowsGui.SS_LEFT,
WindowsGui.WS_CHILD,
WindowsGui.WS_VISIBLE],
x = x_margin,
y = !yref,
width = label_width,
height = item_height,
class = "STATIC",
text = string,
id = WindowsGui.nullWord}
in
(if down then yref := !yref + item_height + item_sep else ();
label_template)
end
fun make_text text_id =
let
val text_template = ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.WS_TABSTOP,
WindowsGui.WS_BORDER,
WindowsGui.WS_VISIBLE,
WindowsGui.ES_AUTOHSCROLL],
x = x_margin + text_width,
y = !yref,
width = text_width,
height = text_height,
class = "EDIT",
text = "",
id = text_id}
in
(yref := !yref + text_height + item_sep;
text_template)
end
fun make_button (string, button_id) =
let
val template = ITEMTEMPLATE
{styles = [WindowsGui.WS_CHILD,
WindowsGui.BS_DEFPUSHBUTTON,
WindowsGui.WS_TABSTOP,
WindowsGui.WS_VISIBLE],
x = x_margin,
y = !yref,
width = item_width,
height = item_height + 2,
class = "BUTTON",
text = string,
id = button_id}
in
(yref := !yref + item_height + 2 + item_sep;
template)
end
fun license_error_message (parent,message) =
let
val id_ok : int = env "win32 ok id"
val full_message = (message^
"\nYou need to reinstall MLWorks\n\nClick OK to continue with a restricted\nsession of MLWorks or Cancel to exit")
fun show_message () =
WindowsGui.messageBox (CapiTypes.get_real parent,full_message,"MLWorks",
[WindowsGui.MB_OKCANCEL,
WindowsGui.MB_APPLMODAL])
in
WindowsGui.messageBeep WindowsGui.MB_ICONEXCLAMATION;
(if show_message() = id_ok then SOME false else NONE)
end
in
fun license_complain parent message = license_error_message(parent, message)
end
local
fun getBitmap args =
(MLWorks.Internal.Runtime.environment "win32 get splash bitmap") args
fun paintBitmap dc =
(MLWorks.Internal.Runtime.environment "win32 paint splash bitmap") dc
val ref_show_splash = ref true;
fun set_timer_text_font window =
let
val WindowsGui.OBJECT text_font =
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
sendMessageNoResult
(window,
WindowsGui.WM_SETFONT,
WindowsGui.WPARAM text_font,
WindowsGui.LPARAM (WindowsGui.intToWord 1))
end
handle WindowsGui.WindowSystemError _ => ()
fun show_screen (parent, kind, duration) =
let
val desktop = WindowsGui.getDesktopWindow()
val isFree = kind <> 0
val countdown = ref duration
val splash_window =
createWindowEx
{ex_styles = [WS_EX_DLGMODALFRAME],
class = "TopLevel",
name = "",
x = 100,
y = 100,
width = 506,
height = 381,
parent = WindowsGui.nullWindow,
menu = WindowsGui.nullWord,
styles = [WindowsGui.WS_POPUP,
WindowsGui.WS_BORDER,
WindowsGui.DS_MODALFRAME]}
val _ = WindowsGui.centerWindow (splash_window, desktop)
fun closedown () = (ref_show_splash := false;
WindowsGui.destroyWindow splash_window);
val s_dialog = CapiTypes.REAL (splash_window, parent)
fun decrement_text () =
let
val dc = WindowsGui.getDC splash_window
val _ = paintBitmap dc
val old_mode = WindowsGui.setBkMode(dc, WindowsGui.TRANSPARENT)
in
set_timer_text_font splash_window;
if (kind <> 2)
then WindowsGui.textOut(dc, 350, 50,
"Time Left:  " ^ Int.toString (!countdown))
else ();
ignore(WindowsGui.setBkMode(dc, old_mode))
end
fun timercb () =
if ((!countdown) = 1) then
closedown()
else
(countdown := (!countdown) - 1;
decrement_text())
val splash_timer = WindowsGui.setTimer(splash_window, 1000, timercb);
fun close_cb _ = (WindowsGui.killTimer(splash_window, splash_timer);
WindowsGui.destroyWindow splash_window;
ref_show_splash := false;
SOME (WindowsGui.nullWord))
fun paint_cb _ = let val dc = WindowsGui.getDC splash_window
in (ignore(paintBitmap dc); NONE)
end
in
if getBitmap (splash_window, kind) then
(if kind = 1 then
()
else
(WindowsGui.addMessageHandler
(splash_window, WindowsGui.WM_CLOSE, close_cb);
WindowsGui.addMessageHandler
(splash_window, WindowsGui.WM_LBUTTONDOWN, close_cb));
decrement_text();
WindowsGui.addMessageHandler(splash_window, WindowsGui.WM_PAINT, paint_cb);
to_front s_dialog;
event_loop ref_show_splash)
else
if isFree then
(send_message (parent, "Splash screen bitmap not found.");
destroy parent)
else ()
end
in
fun show_splash_screen parent =
let
val isFree =
let
val edition = Version.edition()
in
edition = Version.PERSONAL
end
in
if isFree
then
(show_screen (parent, 1, 5);
ref_show_splash := true;
show_screen (parent, 2, 100))
else
show_screen (parent, 0, 5)
end
end
structure GraphicsPorts =
struct
fun max (x:int,y) = if x > y then x else y
fun min (x:int,y) = if x < y then x else y
datatype GraphicsPort =
GP of {window: CapiTypes.Widget,
dcref : WindowsGui.hdc option ref,
name : string,
title : string,
x_offset: int ref,
y_offset: int ref}
exception BadDC
fun gp_widget (GP {window,...}) = window
fun gp_dc (GP {dcref = ref (SOME hdc),...}) = hdc
| gp_dc (GP {dcref = ref (NONE),...}) = raise BadDC
fun start_graphics (GP {window,dcref,...}) =
case !dcref of
SOME _ => ()
| NONE =>
let
val dc = WindowsGui.getDC (CapiTypes.get_real window)
val background = WindowsGui.getSysColor (WindowsGui.COLOR_WINDOW)
val gui_font =
WindowsGui.getStockObject (WindowsGui.DEFAULT_GUI_FONT)
handle
WindowsGui.WindowSystemError _ =>
WindowsGui.getStockObject (WindowsGui.ANSI_VAR_FONT)
in
ignore(WindowsGui.setBkColor (dc,background));
ignore(WindowsGui.selectObject (dc,gui_font));
dcref := SOME dc
end
handle WindowsGui.WindowSystemError _ => ()
fun stop_graphics (GP {window,dcref,...}) =
case !dcref of
NONE => ()
| SOME dc =>
(WindowsGui.releaseDC (CapiTypes.get_real window,dc);
dcref := NONE)
fun with_graphics gp f x =
let
val _ = start_graphics gp
val result = f x handle exn => (stop_graphics gp; raise exn)
in
stop_graphics gp;
result
end
fun initialize_gp _ = ()
fun is_initialized _ = true
exception UnInitialized
fun get_offset (GP {x_offset,y_offset,...}) =
POINT{x = !x_offset,y = !y_offset}
fun set_offset (GP {x_offset,y_offset,...},POINT{x,y}) =
(x_offset := max (x,0); y_offset:= max (y,0))
fun with_highlighting (gp,f,a) =
let
val dc = gp_dc gp
val old_fg = WindowsGui.setTextColor (dc,WindowsGui.getBkColor dc);
val old_bg = WindowsGui.setBkColor (dc,old_fg);
fun undo _ = (ignore(WindowsGui.setTextColor (dc,old_fg));
WindowsGui.setBkColor (dc,old_bg))
val result = f a handle exn => (ignore(undo ()); raise exn)
in
ignore(undo ());
result
end
fun clear_clip_region (GP{...}) = ()
fun set_clip_region (GP {...},REGION{x,y,width,height}) =
()
fun redisplay (gp as GP {window,...}) =
with_graphics
gp
(fn () =>
let
val WindowsGui.HDC dcw = gp_dc gp
in
sendMessageNoResult (CapiTypes.get_real window,WindowsGui.WM_ERASEBKGND,
WindowsGui.WPARAM dcw,
WindowsGui.LPARAM WindowsGui.nullWord);
sendMessageNoResult (CapiTypes.get_real window,WindowsGui.WM_PAINT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord)
end)
()
fun reexpose (GP {window,...}) =
(WindowsGui.postMessage (CapiTypes.get_real window,WindowsGui.WM_PAINT,
WindowsGui.WPARAM WindowsGui.nullWord,
WindowsGui.LPARAM WindowsGui.nullWord);
())
fun copy_gp_region (GP{...},GP{...},
REGION{x=x1,y=y1,width,height},POINT{x=x2,y=y2}) =
()
fun make_gp (name,title,widget) =
GP {window = widget,
dcref = ref NONE,
name = LabelStrings.get_title name,
title = title,
x_offset = ref 0,
y_offset = ref 0}
fun text_extent (gp,string) =
let
val dc = gp_dc gp
val (width,height) = WindowsGui.getTextExtentPoint (dc,string)
in
{ascent=0,
descent=height,
font_ascent=0,
font_descent=height,
lbearing=0,
rbearing=0,
width=width}
end
fun draw_line (gp,POINT{x,y},POINT{x=x',y=y'}) =
let
val GP {x_offset,y_offset,...} = gp
val xo = !x_offset
val yo = !y_offset
val dc = gp_dc gp
in
WindowsGui.moveTo (dc,x-xo,y-yo,WindowsGui.nullWord);
WindowsGui.lineTo (dc,x'-xo,y'-yo)
end
fun draw_point (gp,point) =
draw_line (gp,point,point)
fun draw_rectangle (gp,REGION{x,y,width,height}) =
let
val GP {x_offset,y_offset,...} = gp
val xo = !x_offset
val yo = !y_offset
val dc = gp_dc gp
in
WindowsGui.moveTo (dc,x-xo,y-yo,WindowsGui.nullWord);
WindowsGui.lineTo (dc,x+width-xo,y-yo);
WindowsGui.lineTo (dc,x+width-xo,y+height-yo);
WindowsGui.lineTo (dc,x-xo,y+height-yo);
WindowsGui.lineTo (dc,x-xo,y-yo)
end
fun object_from_brush (WindowsGui.HBRUSH brush) =
WindowsGui.OBJECT brush
fun fill_rectangle (gp,region as REGION{x,y,width,height}) =
let
val dc = gp_dc gp
val GP {x_offset,y_offset,...} = gp
val xo = !x_offset
val yo = !y_offset
val brush = WindowsGui.createSolidBrush (WindowsGui.getSysColor (WindowsGui.COLOR_WINDOWTEXT))
in
WindowsGui.fillRect (dc,
WindowsGui.RECT {left = x-xo,
top = y-yo,
right = x+width-xo,
bottom = y+height-yo},
brush);
WindowsGui.deleteObject (object_from_brush brush)
end
fun clear_rectangle (gp,region as REGION{x,y,width,height}) =
let
val dc = gp_dc gp
val GP {x_offset,y_offset,...} = gp
val xo = !x_offset
val yo = !y_offset
val brush = WindowsGui.createSolidBrush (WindowsGui.getSysColor (WindowsGui.COLOR_WINDOW))
in
WindowsGui.fillRect (dc,
WindowsGui.RECT {left = x-xo,
top = y-yo,
right = x+width-xo,
bottom = y+height-yo},
brush);
WindowsGui.deleteObject (object_from_brush brush)
end
fun draw_image_string (gp,string,POINT{x,y}) =
let
val dc = gp_dc gp
val GP {x_offset,y_offset,...} = gp
val xo = !x_offset
val yo = !y_offset
in
WindowsGui.textOut (dc,x-xo,y-yo,string)
end
fun draw_arc (gp,REGION{x,y,width,height},theta1,theta2) =
()
fun make_graphics (name,title,draw,get_extents,
(want_hscroll, want_vscroll), parent) =
let
val styles =
[WindowsGui.WS_CHILD,WindowsGui.WS_BORDER] @
(if want_hscroll then [WindowsGui.WS_HSCROLL] else []) @
(if want_vscroll then [WindowsGui.WS_VSCROLL] else [])
val window =
create_revealed {class = "Frame",
name = LabelStrings.get_title name,
width = default_width,
height = graphics_height,
parent = CapiTypes.get_real parent,
menu = WindowsGui.nullWord,
styles = styles}
val widget = CapiTypes.REAL (window,parent)
val gp = make_gp (name,title,widget)
fun set_scrollbars () =
let
val (x,y) = get_extents ()
val (w,h) = widget_size widget
in
if want_hscroll
then WindowsGui.setScrollRange (window,WindowsGui.SB_HORZ,0,max (0,x-w),true)
else ();
if want_vscroll
then WindowsGui.setScrollRange (window,WindowsGui.SB_VERT,0,max (0,y-h),true)
else ()
end
fun draw_handler _ =
(WindowsGui.validateRect (window,NONE);
ignore(with_graphics gp draw (gp,REGION {x=0,y=0,width=1000,height=1000}));
SOME WindowsGui.nullWord)
val hinc = 20
val vinc = 20
fun getx () = #1 (widget_size widget)
fun gety () = #2 (widget_size widget)
fun vscroll_handler (WindowsGui.WPARAM wparam, WindowsGui.LPARAM lparam) =
let
val code = WindowsGui.loword wparam
val GP {y_offset,...} = gp
fun dochange pos =
let
val pos = max (0, min (#2 (get_extents ()) - gety(), pos))
in
WindowsGui.setScrollPos (window,WindowsGui.SB_VERT,pos,true);
y_offset := pos;
redisplay gp
end
val _ =
if code = WindowsGui.convertSbValue WindowsGui.SB_THUMBPOSITION
then dochange (WindowsGui.hiword wparam)
else if code = WindowsGui.convertSbValue WindowsGui.SB_LINEUP
then dochange (!y_offset - vinc)
else if code = WindowsGui.convertSbValue WindowsGui.SB_LINEDOWN
then dochange (!y_offset + vinc)
else if code = WindowsGui.convertSbValue WindowsGui.SB_PAGEUP
then dochange (!y_offset - gety ())
else if code = WindowsGui.convertSbValue WindowsGui.SB_PAGEDOWN
then dochange (!y_offset + gety ())
else ()
in
SOME WindowsGui.nullWord
end
fun hscroll_handler (WindowsGui.WPARAM wparam, WindowsGui.LPARAM lparam) =
let
val code = WindowsGui.loword wparam
val GP {x_offset,...} = gp
fun dochange pos =
let
val pos = max (0, min (#1 (get_extents ()) - getx(), pos))
in
WindowsGui.setScrollPos (window,WindowsGui.SB_HORZ,pos,true);
x_offset := pos;
redisplay gp
end
val _ =
if code = WindowsGui.convertSbValue WindowsGui.SB_THUMBPOSITION
then dochange (WindowsGui.hiword wparam)
else if code = WindowsGui.convertSbValue WindowsGui.SB_LINELEFT
then dochange (!x_offset - hinc)
else if code = WindowsGui.convertSbValue WindowsGui.SB_LINERIGHT
then dochange (!x_offset + hinc)
else if code = WindowsGui.convertSbValue WindowsGui.SB_PAGELEFT
then dochange (!x_offset - getx())
else if code = WindowsGui.convertSbValue WindowsGui.SB_PAGERIGHT
then dochange (!x_offset + getx())
else ()
in
SOME WindowsGui.nullWord
end
fun set_position (POINT{x=xi',y=yi'}) =
let
val POINT{x=cur_xi,y=cur_yi} = get_offset gp
val xi = if (xi' < 0) then cur_xi else xi'
val yi = if (yi' < 0) then cur_yi else yi'
val (ww,wh) = widget_size widget
val (xextent,yextent) = get_extents()
val new_xi = max (min (xi,xextent-ww),0)
val new_yi = max (min (yi,yextent-wh),0)
in
if want_hscroll
then WindowsGui.setScrollPos (window,WindowsGui.SB_HORZ,new_xi,true)
else ();
if want_vscroll
then WindowsGui.setScrollPos (window,WindowsGui.SB_VERT,new_yi,true)
else ();
set_offset (gp,POINT{x=new_xi,y=new_yi});
redisplay gp
end
fun resize_function data =
let
val (ww,wh) = widget_size widget
val (xextent,yextent) = get_extents()
val POINT{x=xi,y=yi} = get_offset gp
val new_xi = max (min (xi,xextent-ww),0)
val new_yi = max (min (yi,yextent-wh),0)
in
set_offset (gp,POINT{x=new_xi,y=new_yi});
set_scrollbars ()
end
in
WindowsGui.addMessageHandler (window,WindowsGui.WM_PAINT,draw_handler);
if want_vscroll
then WindowsGui.addMessageHandler (window,WindowsGui.WM_VSCROLL,vscroll_handler)
else ();
if want_hscroll
then WindowsGui.addMessageHandler (window,WindowsGui.WM_HSCROLL,hscroll_handler)
else ();
WindowsGui.addMessageHandler (window,WindowsGui.WM_SIZE,
fn _ =>
(resize_function ();
SOME WindowsGui.nullWord));
set_scrollbars();
(widget,
gp,
fn _ => (resize_function () ;redisplay gp),
set_position)
end
fun add_input_handler (gp,handler) =
let
val GP {window,x_offset,y_offset,...} = gp
val window = CapiTypes.get_real window
fun mouse_handler button (WindowsGui.WPARAM wparam,WindowsGui.LPARAM lparam) =
let
val x = WindowsGui.loword lparam
val y = WindowsGui.hiword lparam
in
ignore(handler (button,POINT {x=x + !x_offset,y=y + !y_offset}));
SOME WindowsGui.nullWord
end
in
WindowsGui.addMessageHandler (window,WindowsGui.WM_LBUTTONDOWN,mouse_handler (Event.LEFT));
WindowsGui.addMessageHandler (window,WindowsGui.WM_RBUTTONDOWN,mouse_handler (Event.RIGHT))
end
type PixMap = unit
datatype LineStyle = LINESOLID | LINEONOFFDASH | LINEDOUBLEDASH
datatype Attribute =
FONT of Font
| LINE_STYLE of LineStyle
| LINE_WIDTH of int
| FOREGROUND of PixMap
| BACKGROUND of PixMap
datatype Request =
REQUEST_FONT
| REQUEST_LINE_STYLE
| REQUEST_LINE_WIDTH
| REQUEST_FOREGROUND
| REQUEST_BACKGROUND
val getAttributes : GraphicsPort * Request list -> Attribute list =
fn(_,_) => (dummy "getAttributes"; [])
val setAttributes : GraphicsPort * Attribute list -> unit =
fn(_,_) => (dummy "setAttributes"; ())
val with_graphics_port :(GraphicsPort * ('a -> 'b) * 'a) -> 'b =
fn(_,f,a) => (dummy "with_graphics_port"; f(a))
end
fun parent CapiTypes.NONE = CapiTypes.NONE
| parent (CapiTypes.REAL (_,p)) = p
| parent (CapiTypes.FAKE (p,_)) = p
val reveal = reveal o CapiTypes.get_real
val terminator = "\r\n"
fun register_interrupt_widget w = ()
fun with_window_updates f =
let
fun toggle_updates tf = (MLWorks.Internal.Runtime.environment
"nt ml window updates toggle") tf
val start_it = toggle_updates true
val result = f () handle exn => (ignore(toggle_updates false); raise exn)
in
(ignore(toggle_updates false);
result)
end
end
;
