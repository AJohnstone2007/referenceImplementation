signature WINDOWS_GUI =
sig
eqtype hwnd
eqtype hmenu
eqtype word
datatype accelerator_table = ACCELERATOR_TABLE of word
datatype wparam = WPARAM of word
datatype lparam = LPARAM of word
datatype hdc = HDC of word
datatype rect = RECT of {left:int,top:int,right:int,bottom:int}
datatype point = POINT of {x:int,y:int}
datatype color = COLOR of word
datatype cursor = CURSOR of word
datatype hinst = HINSTANCE of word
exception WindowSystemError of string
datatype message =
BM_GETCHECK |
BM_GETSTATE |
BM_SETCHECK |
BM_SETSTATE |
BM_SETSTYLE |
BN_CLICKED |
BN_DISABLE |
BN_DOUBLECLICKED |
BN_HILITE |
BN_PAINT |
BN_UNHILITE |
CBN_CLOSEUP |
CBN_DBLCLK |
CBN_DROPDOWN |
CBN_EDITCHANGE |
CBN_EDITUPDATE |
CBN_ERRSPACE |
CBN_KILLFOCUS |
CBN_SELCHANGE |
CBN_SELENDCANCEL |
CBN_SELENDOK |
CBN_SETFOCUS |
CB_ADDSTRING |
CB_DELETESTRING |
CB_DIR |
CB_FINDSTRING |
CB_FINDSTRINGEXACT |
CB_GETCOUNT |
CB_GETCURSEL |
CB_GETDROPPEDCONTROLRECT |
CB_GETDROPPEDSTATE |
CB_GETDROPPEDWIDTH |
CB_GETEDITSEL |
CB_GETEXTENDEDUI |
CB_GETHORIZONTALEXTENT |
CB_GETITEMDATA |
CB_GETITEMHEIGHT |
CB_GETLBTEXT |
CB_GETLBTEXTLEN |
CB_GETLOCALE |
CB_GETTOPINDEX |
CB_INITSTORAGE |
CB_INSERTSTRING |
CB_LIMITTEXT |
CB_RESETCONTENT |
CB_SELECTSTRING |
CB_SETCURSEL |
CB_SETDROPPEDWIDTH |
CB_SETEDITSEL |
CB_SETEXTENDEDUI |
CB_SETHORIZONTALEXTENT |
CB_SETITEMDATA |
CB_SETITEMHEIGHT |
CB_SETLOCALE |
CB_SETTOPINDEX |
CB_SHOWDROPDOWN |
DM_GETDEFID |
DM_SETDEFID |
EM_CANUNDO |
EM_EMPTYUNDOBUFFER |
EM_FMTLINES |
EM_GETFIRSTVISIBLELINE |
EM_GETHANDLE |
EM_GETLINE |
EM_GETLINECOUNT |
EM_GETMODIFY |
EM_GETPASSWORDCHAR |
EM_GETRECT |
EM_GETSEL |
EM_GETWORDBREAKPROC |
EM_LIMITTEXT |
EM_LINEFROMCHAR |
EM_LINEINDEX |
EM_LINELENGTH |
EM_LINESCROLL |
EM_REPLACESEL |
EM_SCROLL |
EM_SCROLLCARET |
EM_SETHANDLE |
EM_SETMODIFY |
EM_SETPASSWORDCHAR |
EM_SETREADONLY |
EM_SETRECT |
EM_SETRECTNP |
EM_SETSEL |
EM_SETTABSTOPS |
EM_SETWORDBREAKPROC |
EM_UNDO |
EN_CHANGE |
EN_ERRSPACE |
EN_HSCROLL |
EN_KILLFOCUS |
EN_MAXTEXT |
EN_SETFOCUS |
EN_UPDATE |
EN_VSCROLL |
FINDMSGSTRING |
LBN_DBLCLK |
LBN_ERRSPACE |
LBN_KILLFOCUS |
LBN_SELCANCEL |
LBN_SELCHANGE |
LBN_SETFOCUS |
LB_ADDFILE |
LB_ADDSTRING |
LB_DELETESTRING |
LB_DIR |
LB_FINDSTRING |
LB_FINDSTRINGEXACT |
LB_GETANCHORINDEX |
LB_GETCARETINDEX |
LB_GETCOUNT |
LB_GETCURSEL |
LB_GETHORIZONTALEXTENT |
LB_GETITEMDATA |
LB_GETITEMHEIGHT |
LB_GETITEMRECT |
LB_GETLOCALE |
LB_GETSEL |
LB_GETSELCOUNT |
LB_GETSELITEMS |
LB_GETTEXT |
LB_GETTEXTLEN |
LB_GETTOPINDEX |
LB_INSERTSTRING |
LB_RESETCONTENT |
LB_SELECTSTRING |
LB_SELITEMRANGE |
LB_SELITEMRANGEEX |
LB_SETANCHORINDEX |
LB_SETCARETINDEX |
LB_SETCOLUMNWIDTH |
LB_SETCOUNT |
LB_SETCURSEL |
LB_SETHORIZONTALEXTENT |
LB_SETITEMDATA |
LB_SETITEMHEIGHT |
LB_SETLOCALE |
LB_SETSEL |
LB_SETTABSTOPS |
LB_SETTOPINDEX |
TB_GETSTATE |
TB_SETSTATE |
WM_ACTIVATE |
WM_ACTIVATEAPP |
WM_CANCELMODE |
WM_CHAR |
WM_CHARTOITEM |
WM_CHILDACTIVATE |
WM_CLOSE |
WM_COMMAND |
WM_CONTEXTMENU |
WM_COPY |
WM_COPYDATA |
WM_CUT |
WM_CREATE |
WM_CTLCOLORBTN |
WM_CTLCOLOREDIT |
WM_DEADCHAR |
WM_DESTROY |
WM_ENABLE |
WM_ENDSESSION |
WM_ERASEBKGND |
WM_GETFONT |
WM_GETMINMAXINFO |
WM_GETTEXT |
WM_GETTEXTLENGTH |
WM_HSCROLL |
WM_HOTKEY |
WM_INITDIALOG |
WM_INITMENU |
WM_KEYDOWN |
WM_KEYUP |
WM_KILLFOCUS |
WM_LBUTTONDBLCLK |
WM_LBUTTONDOWN |
WM_LBUTTONUP |
WM_MBUTTONDBLCLK |
WM_MBUTTONDOWN |
WM_MBUTTONUP |
WM_MOUSEACTIVATE |
WM_MOUSEMOVE |
WM_MOVE |
WM_NCACTIVATE |
WM_NCCALCSIZE |
WM_NCCREATE |
WM_NCDESTROY |
WM_NCHITTEST |
WM_NCLBUTTONDBLCLK |
WM_NCLBUTTONDOWN |
WM_NCLBUTTONUP |
WM_NCMBUTTONDBLCLK |
WM_NCMBUTTONDOWN |
WM_NCMBUTTONUP |
WM_NCMOUSEMOVE |
WM_NCRBUTTONDBLCLK |
WM_NCRBUTTONDOWN |
WM_NCRBUTTONUP |
WM_NOTIFY |
WM_PAINT |
WM_PARENTNOTIFY |
WM_PASTE |
WM_POWER |
WM_QUERYENDSESSION |
WM_QUERYOPEN |
WM_QUEUESYNC |
WM_QUIT |
WM_RBUTTONDBLCLK |
WM_RBUTTONDOWN |
WM_RBUTTONUP |
WM_SETCURSOR |
WM_SETFOCUS |
WM_SETFONT |
WM_SETREDRAW |
WM_SETTEXT |
WM_SHOWWINDOW |
WM_SIZE |
WM_SIZING |
WM_SYSCHAR |
WM_SYSCOMMAND |
WM_SYSDEADCHAR |
WM_SYSKEYDOWN |
WM_SYSKEYUP |
WM_UNDO |
WM_VSCROLL |
WM_WINDOWPOSCHANGED |
WM_WINDOWPOSCHANGING |
WM_USER0 |
WM_USER1 |
WM_USER2 |
WM_USER3 |
WM_USER4 |
WM_USER5
datatype window_style =
BS_3STATE |
BS_AUTO3STATE |
BS_AUTOCHECKBOX |
BS_AUTORADIOBUTTON |
BS_CHECKBOX |
BS_DEFPUSHBUTTON |
BS_GROUPBOX |
BS_LEFTTEXT |
BS_OWNERDRAW |
BS_PUSHBUTTON |
BS_RADIOBUTTON |
BS_USERBUTTON |
CBS_AUTOHSCROLL |
CBS_DISABLENOSCROLL |
CBS_DROPDOWN |
CBS_DROPDOWNLIST |
CBS_HASSTRINGS |
CBS_NOINTEGRALHEIGHT |
CBS_OEMCONVERT |
CBS_OWNERDRAWFIXED |
CBS_OWNERDRAWVARIABLE |
CBS_SIMPLE |
CBS_SORT |
DS_ABSALIGN |
DS_LOCALEDIT |
DS_MODALFRAME |
DS_NOIDLEMSG |
DS_SETFONT |
DS_SETFOREGROUND |
DS_SYSMODAL |
ES_AUTOHSCROLL |
ES_AUTOVSCROLL |
ES_CENTER |
ES_LEFT |
ES_LOWERCASE |
ES_MULTILINE |
ES_NOHIDESEL |
ES_OEMCONVERT |
ES_PASSWORD |
ES_READONLY |
ES_RIGHT |
ES_UPPERCASE |
ES_WANTRETURN |
LBS_DISABLENOSCROLL |
LBS_EXTENDEDSEL |
LBS_HASSTRINGS |
LBS_MULTICOLUMN |
LBS_MULTIPLESEL |
LBS_NODATA |
LBS_NOINTEGRALHEIGHT |
LBS_NOREDRAW |
LBS_NOTIFY |
LBS_OWNERDRAWFIXED |
LBS_OWNERDRAWVARIABLE |
LBS_SORT |
LBS_STANDARD |
LBS_USETABSTOPS |
LBS_WANTKEYBOARDINPUT |
SBS_BOTTOMALIGN |
SBS_HORZ |
SBS_LEFTALIGN |
SBS_RIGHTALIGN |
SBS_SIZEBOX |
SBS_SIZEBOXBOTTOMRIGHTALIGN |
SBS_SIZEBOXTOPLEFTALIGN |
SBS_TOPALIGN |
SBS_VERT |
SS_BLACKFRAME |
SS_BLACKRECT |
SS_CENTER |
SS_GRAYFRAME |
SS_GRAYRECT |
SS_ICON |
SS_LEFT |
SS_LEFTNOWORDWRAP |
SS_NOPREFIX |
SS_RIGHT |
SS_SIMPLE |
SS_WHITEFRAME |
SS_WHITERECT |
TBSTYLE_ALTDRAG |
TBSTYLE_TOOLTIPS |
TBSTYLE_WRAPABLE |
WS_BORDER |
WS_CAPTION |
WS_CHILD |
WS_CLIPCHILDREN |
WS_CLIPSIBLINGS |
WS_DISABLED |
WS_DLGFRAME |
WS_GROUP |
WS_HSCROLL |
WS_ICONIC |
WS_MAXIMIZE |
WS_MAXIMIZEBOX |
WS_MINIMIZE |
WS_MINIMIZEBOX |
WS_OVERLAPPED |
WS_OVERLAPPED_WINDOW |
WS_POPUP |
WS_POPUPWINDOW |
WS_SYSMENU |
WS_TABSTOP |
WS_THICKFRAME |
WS_TILEDWINDOW |
WS_VISIBLE |
WS_VSCROLL
datatype sw_arg =
SW_HIDE |
SW_MAXIMIZE |
SW_MINIMIZE |
SW_RESTORE |
SW_SHOW |
SW_SHOWDEFAULT |
SW_SHOWMAXIMIZED |
SW_SHOWMINIMIZED |
SW_SHOWMINNOACTIVE |
SW_SHOWNA |
SW_SHOWNOACTIVE |
SW_SHOWNORMAL
datatype gw_arg =
GW_CHILD |
GW_HWNDFIRST |
GW_HWNDLAST |
GW_HWNDNEXT |
GW_HWNDPREV |
GW_OWNER
datatype gwl_value =
DWL_DLGPROC |
DWL_MSGRESULT |
DWL_USER |
GWL_EXSTYLE |
GWL_HINSTANCE |
GWL_HWNDPARENT |
GWL_ID |
GWL_STYLE |
GWL_USERDATA |
GWL_WNDPROC
datatype sb_value =
SB_BOTH |
SB_BOTTOM |
SB_CTL |
SB_ENDSCROLL |
SB_HORZ |
SB_LINEDOWN |
SB_LINELEFT |
SB_LINERIGHT |
SB_LINEUP |
SB_PAGEDOWN |
SB_PAGELEFT |
SB_PAGERIGHT |
SB_PAGEUP |
SB_THUMBPOSITION |
SB_THUMBTRACK |
SB_TOP |
SB_VERT
datatype esb_value =
ESB_DISABLE_BOTH
| ESB_DISABLE_DOWN
| ESB_DISABLE_LEFT
| ESB_DISABLE_LTUP
| ESB_DISABLE_RIGHT
| ESB_DISABLE_RTDN
| ESB_DISABLE_UP
| ESB_ENABLE_BOTH
datatype sc_value =
SC_CLOSE
| SC_CONTEXTHELP
| SC_DEFAULT
| SC_HOTKEY
| SC_HSCROLL
| SC_KEYMENU
| SC_MAXIMIZE
| SC_MINIMIZE
| SC_MOUSEMENU
| SC_MOVE
| SC_NEXTWINDOW
| SC_PREVWINDOW
| SC_RESTORE
| SC_SCREENSAVE
| SC_SIZE
| SC_TASKLIST
| SC_VSCROLL
datatype wa_value =
WA_ACTIVE |
WA_CLICKACTIVE |
WA_INACTIVE
datatype menu_value = SUBMENU of hmenu | ITEM of word
datatype menu_flag =
MF_BITMAP |
MF_BYCOMMAND |
MF_BYPOSITION |
MF_CHECKED |
MF_DISABLED |
MF_ENABLED |
MF_GRAYED |
MF_MENUBARBREAK |
MF_MENUBREAK |
MF_OWNERDRAW |
MF_POPUP |
MF_SEPARATOR |
MF_STRING |
MF_UNCHECKED
val windowToWord : hwnd -> word
val menuToWord : hmenu -> word
val intToWord : int -> word
val nullWord : word
val nullWindow : hwnd
val isNullWindow : hwnd -> bool
datatype res_type =
RT_ACCELERATOR |
RT_ANICURSOR |
RT_ANIICON |
RT_BITMAP |
RT_CURSOR |
RT_DIALOG |
RT_FONT |
RT_FONTDIR |
RT_GROUP_CURSOR |
RT_GROUP_ICON |
RT_ICON |
RT_MENU |
RT_MESSAGETABLE |
RT_RCDATA |
RT_STRING |
RT_VERSION
val findResource : hinst * string * res_type -> word
val lockResource : word -> unit
val loadResource : hinst * word -> word
val getModuleHandle : string -> hinst
val loadLibrary : string -> hinst
val freeLibrary : hinst -> bool
val mainLoop : unit -> unit
val mainInit : unit -> hwnd
val doInput : unit -> bool
val convertWaValue : wa_value -> int
val convertSbValue : sb_value -> int
val convertScValue : sc_value -> int
val convertStyle : window_style -> word
val newControlId : unit -> word
val anyPopup : unit -> bool
val bringWindowToTop : hwnd -> unit
val centerWindow : hwnd * hwnd -> unit
val childWindowFromPoint : hwnd * (int * int) -> hwnd
val closeWindow : hwnd -> unit
val createWindow : {class: string,
name: string,
styles : window_style list,
width : int,
height : int,
parent : hwnd,
menu : word} -> hwnd
datatype window_ex_style =
WS_EX_DLGMODALFRAME |
WS_EX_STATICEDGE |
WS_EX_WINDOWEDGE
val createWindowEx : {ex_styles: window_ex_style list,
class: string,
name: string,
x: int,
y: int,
styles : window_style list,
width : int,
height : int,
parent : hwnd,
menu : word} -> hwnd
val destroyWindow : hwnd -> unit
val enumChildWindows : hwnd * (hwnd -> unit) -> unit
val enumWindows : (hwnd -> unit) -> unit
val findWindow : string * string -> hwnd
val getClientRect : hwnd -> rect
val getDesktopWindow : unit-> hwnd
val getForegroundWindow : unit-> hwnd
val getLastActivePopup : hwnd -> hwnd
val getNextWindow : hwnd * gw_arg -> hwnd
val getParent : hwnd -> hwnd
val getTopWindow : hwnd -> hwnd
val getWindow : hwnd * gw_arg -> hwnd
val getWindowRect : hwnd -> rect
val getWindowPlacement : hwnd -> int * point * point * rect
val isChild : hwnd * hwnd -> bool
val isIconic : hwnd -> bool
val isWindow : hwnd -> bool
val isWindowUnicode : hwnd -> bool
val isWindowVisible : hwnd -> bool
val isZoomed : hwnd -> bool
val moveWindow : hwnd * int * int * int * int * bool -> unit
val setForegroundWindow : hwnd -> unit
val setParent : hwnd * hwnd -> hwnd
val setWindowText : hwnd * string -> unit
val setWindowPos : hwnd * {x: int, y: int, height: int, width: int} -> unit
val showOwnedPopups : hwnd * bool -> unit
val showWindow : hwnd * sw_arg -> unit
val updateWindow : hwnd -> unit
val windowFromPoint : int * int -> hwnd
val getMinMaxInfo : word -> point * point * point * point
val setMinMaxInfo : word * point * point * point * point -> word
val getInputState : unit -> bool
val getMessagePos : unit -> int * int
val getMessageTime : unit -> int
val inSendMessage : unit -> bool
val postMessage : hwnd * message * wparam * lparam -> unit
val postQuitMessage : int -> unit
val sendMessage : hwnd * message * wparam * lparam -> word
val messageToWord : message -> word
val getWindowLong : hwnd * gwl_value -> word
val setWindowLong : hwnd * gwl_value * word -> word
val enableWindow : hwnd * bool -> bool
val getActiveWindow : unit -> hwnd
val getFocus : unit -> hwnd
val isWindowEnabled : hwnd -> bool
val setActiveWindow : hwnd -> hwnd
val setFocus : hwnd -> hwnd
val getCapture : unit -> hwnd
val releaseCapture : unit -> unit
val setCapture : hwnd -> hwnd
type timer
val killTimer : hwnd * timer -> unit
val setTimer : hwnd *
int *
(unit -> unit) -> timer
val checkDlgButton: hwnd * word * int -> unit
val checkRadioButton: hwnd * word * word * word -> unit
val isDlgButtonChecked: hwnd * word -> int;
val enableScrollBar : hwnd * sb_value * esb_value -> unit
val getScrollPos : hwnd * sb_value -> int
val getScrollRange : hwnd * sb_value -> int * int
val setScrollPos : hwnd * sb_value * int * bool -> unit
val setScrollRange : hwnd * sb_value * int * int * bool -> unit
val showScrollBar : hwnd * sb_value * bool -> unit
val getScrollInfo : hwnd * sb_value -> int * word * word * int * int * word * int * int
val appendMenu: hmenu * menu_flag list * menu_value * string -> unit
val checkMenuItem : hmenu * word * menu_flag list -> unit
val createMenu : unit -> hmenu
val createPopupMenu : unit -> hmenu
val destroyMenu : hmenu -> unit
val deleteMenu : hmenu * word * menu_flag -> unit
val drawMenuBar : hwnd -> unit
val enableMenuItem : hmenu * word * menu_flag list -> unit
val getMenu : hwnd -> hmenu
val getMenuItemId : hmenu * int -> word
val getMenuItemCount : hmenu -> int
val getMenuState : hmenu * word * menu_flag -> menu_flag list
val getMenuString : hmenu * word * menu_flag -> string
val getSubmenu : hmenu * int -> hmenu
val getSystemMenu : hwnd * bool -> hmenu
val setMenu : hwnd * hmenu -> unit
val removeMenu : hmenu * word * menu_flag -> unit
datatype accelerator_flag =
FALT
| FCONTROL
| FNOINVERT
| FSHIFT
| FVIRTKEY
val createAcceleratorTable : (accelerator_flag list * int * int) list -> accelerator_table
val destroyAcceleratorTable : accelerator_table -> unit
datatype message_box_style =
MB_ABORTRETRYIGNORE |
MB_APPLMODAL |
MB_ICONASTERISK |
MB_ICONEXCLAMATION |
MB_ICONHAND |
MB_ICONINFORMATION |
MB_ICONQUESTION |
MB_ICONSTOP |
MB_OK |
MB_OKCANCEL |
MB_RETRYCANCEL |
MB_YESNO |
MB_YESNOCANCEL
val messageBox : hwnd * string * string * message_box_style list -> int
val endDialog : hwnd * int -> unit
val getDlgItem : hwnd * word -> hwnd
val getDlgCtrlID : hwnd -> int
val getDialogBaseUnits : unit -> word
val getFindFlags : word -> {searchStr: string,
searchDown: bool,
matchCase: bool,
wholeWord: bool,
findNext: bool,
closing: bool}
val findDialog : hwnd * string * bool option * bool option * bool option -> hwnd
val createDialog : hinst * hwnd * string -> hwnd
datatype bk_mode =
OPAQUE |
TRANSPARENT
val getBkMode : hdc -> bk_mode
val setBkMode : hdc * bk_mode -> bk_mode
val getBkColor : hdc -> color
val setBkColor : hdc * color -> color
val validateRect : hwnd * rect option -> unit
val invalidateRect : hwnd * rect option * bool -> unit
val windowFromDC : hdc -> hwnd
val setPixel : hdc * int * int * color -> color
datatype rop2_mode =
R2_BLACK |
R2_COPYPEN |
R2_MASKNOTPEN |
R2_MASKPEN |
R2_MASKPENNOT |
R2_MERGENOTPEN |
R2_MERGEPEN |
R2_MERGEPENNOT |
R2_NOP |
R2_NOT |
R2_NOTCOPYPEN |
R2_NOTMASKPEN |
R2_NOTMERGEPEN |
R2_NOTXORPEN |
R2_WHITE |
R2_XORPEN
val getRop2 : hdc -> rop2_mode
val setRop2 : hdc * rop2_mode -> rop2_mode
datatype rop_mode =
BLACKNESS |
DSTINVERT |
MERGECOPY |
MERGEPAINT |
NOTSRCCOPY |
NOTSRCERASE |
PATCOPY |
PATINVERT |
PATPAINT |
SRCAND |
SRCCOPY |
SRCERASE |
SRCINVERT |
SRCPAINT |
WHITENESS
datatype cursor_shape =
IDC_APPSTARTING |
IDC_ARROW |
IDC_CROSS |
IDC_IBEAM |
IDC_ICON |
IDC_NO |
IDC_SIZE |
IDC_SIZEALL |
IDC_SIZENESW |
IDC_SIZENS |
IDC_SIZEWE |
IDC_UPARROW |
IDC_WAIT
val clipCursor : rect option -> unit
val getClipCursor : unit -> rect
val getCursorPos : unit -> point
val setCursorPos : int * int -> unit
val showCursor : bool -> int
val loadCursor : cursor_shape -> cursor
val setCursor : cursor -> cursor
val openClipboard : hwnd -> bool
val closeClipboard : unit -> unit
val emptyClipboard : unit -> unit
val setClipboardData : string -> unit
val getClipboardData : unit -> string
datatype object = OBJECT of word
datatype hbrush = HBRUSH of word
datatype hpen = HPEN of word
datatype object_type =
OBJ_PEN |
OBJ_BRUSH |
OBJ_PAL |
OBJ_FONT |
OBJ_BITMAP
datatype stock_object =
ANSI_FIXED_FONT |
ANSI_VAR_FONT |
BLACK_BRUSH |
BLACK_PEN |
DEFAULT_GUI_FONT |
DEFAULT_PALETTE |
DKGRAY_BRUSH |
GRAY_BRUSH |
HOLLOW_BRUSH |
LTGRAY_BRUSH |
NULL_BRUSH |
NULL_PEN |
OEM_FIXED_FONT |
SYSTEM_FIXED_FONT |
SYSTEM_FONT |
WHITE_BRUSH |
WHITE_PEN
val cancelDC : hdc -> unit
val createCompatibleDC : hdc -> hdc
val deleteObject : object -> unit
val getCurrentObject : hdc * object_type -> object
val getDC : hwnd -> hdc
val getDCOrgEx : hdc -> point
val getStockObject : stock_object -> object
val releaseDC : hwnd * hdc -> unit
val restoreDC : hdc * int -> unit
val saveDC : hdc -> int
val selectObject : hdc * object -> object
val bitBlt :
{hdcDest: hdc,
hdcSrc: hdc,
height: int,
ropMode: rop_mode,
width: int,
xDest: int,
xSrc: int,
yDest: int,
ySrc: int} -> unit
datatype hatch_style =
HS_BDIAGONAL
| HS_CROSS
| HS_DIAGCROSS
| HS_FDIAGONAL
| HS_HORIZONTAL
| HS_VERTICAL
val createHatchBrush : hatch_style * color -> hbrush
val createSolidBrush : color -> hbrush
datatype pen_style =
PS_DASH |
PS_DASHDOT |
PS_DASHDOTDOT |
PS_DOT |
PS_NULL |
PS_SOLID |
PS_INSIDEFRAME
val createPen : pen_style * int * color -> hpen
datatype arc_direction = AD_COUNTERCLOCKWISE | AD_CLOCKWISE
val angleArc : hdc * int * int * int * real * real -> unit
val arc : hdc * int * int * int * int * int * int * int * int -> unit
val arcTo : hdc * int * int * int * int * int * int * int * int -> unit
val getArcDirection : hdc -> arc_direction
val lineTo : hdc * int * int -> unit
val moveTo : hdc * int * int * word -> unit
val polyBezier : hdc * point list -> unit
val polyBezierTo : hdc * point list -> unit
val polyline : hdc * point list -> unit
val polylineTo : hdc * point list -> unit
val polyPolyline : hdc * point list list -> unit
val setArcDirection : hdc * arc_direction -> unit
val chord : hdc * int * int * int * int * int * int * int * int -> unit
val ellipse : hdc * int * int * int * int -> unit
val fillRect : hdc * rect * hbrush -> unit
val frameRect : hdc * rect * hbrush -> unit
val invertRect : hdc * rect -> unit
val pie : hdc * int * int * int * int * int * int * int * int -> unit
val polygon : hdc * point list -> unit
val polyPolygon : hdc * point list list -> unit
val rectangle : hdc * int * int * int * int -> unit
val roundRect : hdc * int * int * int * int * int * int -> unit
val getTextColor : hdc -> color
val getTextExtentPoint : hdc * string -> int * int
val setTextColor : hdc * color -> color
val textOut : hdc * int * int * string -> unit
val clientToScreen : hwnd * point -> point
val screenToClient : hwnd * point -> point
val messageBeep : message_box_style -> unit
datatype tb_button_state =
TBSTATE_CHECKED |
TBSTATE_ENABLED |
TBSTATE_HIDDEN |
TBSTATE_INDETERMINATE |
TBSTATE_PRESSED |
TBSTATE_WRAP
val tbStatesToWord : tb_button_state list -> word
datatype tb_button_style =
TBSTYLE_BUTTON |
TBSTYLE_CHECK |
TBSTYLE_CHECKGROUP |
TBSTYLE_GROUP |
TBSTYLE_SEP
val createToolbarEx :
{bmp_id: word,
buttons: (int * word * tb_button_state list * tb_button_style list * word * int) list,
num_bmps: int,
num_buttons: int,
parent: hwnd,
styles: window_style list,
toolbar_id: word,
x_bitmap: int,
x_button: int,
y_bitmap: int,
y_button: int} -> hwnd
datatype color_spec =
COLOR_ACTIVEBORDER |
COLOR_ACTIVECAPTION |
COLOR_APPWORKSPACE |
COLOR_BACKGROUND |
COLOR_BTNSHADOW |
COLOR_BTNTEXT |
COLOR_CAPTIONTEXT |
COLOR_GRAYTEXT |
COLOR_HIGHLIGHT |
COLOR_HIGHLIGHTTEXT |
COLOR_INACTIVEBORDER |
COLOR_INACTIVECAPTION |
COLOR_INACTIVECAPTIONTEXT |
COLOR_MENU |
COLOR_SCROLLBAR |
COLOR_WINDOW |
COLOR_WINDOWFRAME |
COLOR_WINDOWTEXT
val getSysColor : color_spec -> color
val openFileDialog : hwnd * string * string * bool -> string list
val openDirDialog : hwnd -> string
val saveDialog : hwnd * string * string -> string
val setAcceleratorTable : accelerator_table -> unit
val registerPopupWindow : hwnd -> unit
val unregisterPopupWindow : hwnd -> unit
val addMessageHandler :
hwnd * message *
(wparam * lparam -> word option) -> unit
val addNewWindow : hwnd * word -> unit
val removeWindow : hwnd -> unit
val getMlWindowProc : unit -> word
val addCommandHandler : hwnd * word * (hwnd * int -> unit) -> unit
val malloc : int -> word
val free : word -> unit
val wordToString : word -> string
val wordToInt : word -> int
val wordToSignedInt : word -> int
val setByte : word * int * int -> unit
val makeCString : string -> word
val hiword : word -> int
val loword : word -> int
end
;
