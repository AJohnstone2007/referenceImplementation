require "__text_io";
require "basic_util";
require "fonts";
structure BasicTypes =
struct
type SimpleAction = unit -> unit
type WidPath = string
type TclPath = string
type WidId = string
type WinPath = string
type WinId = string
type Title = string
type AnnId = string
type IntPath = (WinId * WidPath)
type PathAss = (WidId * IntPath)
type CItemId = string
type Coord = (int * int)
type BitmapName = string
type BitmapFile = string
type ImageFile = string
type ImageId = string
type CursorName = string
type CursorFile = string
datatype TkEvent =
TkEvent of int
* string
* int
* int
* int
* int
type Action = TkEvent -> unit
datatype Event =
KeyPress of string
| KeyRelease of string
| ButtonPress of int option
| ButtonRelease of int option
| Enter | Leave | Motion
| UserEv of string
| Shift of Event | Ctrl of Event | Lock of Event | Any of Event
| Double of Event | Triple of Event
| ModButton of int* Event
| Alt of Event | Meta of Event
| Mod3 of Event | Mod4 of Event | Mod5 of Event
datatype Binding = BindEv of Event * Action
datatype RelKind =
Flat | Groove | Raised | Ridge | Sunken
datatype Color =
NoColor | Black | White | Grey | Blue | Green | Red | Brown | Yellow
datatype ArrowPos =
NoneAP | FirstAP | LastAP | BothAP
datatype CapstyleKind =
Butt | Projecting | Round
datatype JoinstyleKind =
Bevel | Miter | RoundJoin
datatype AnchorKind =
North | NorthEast |
East | SouthEast |
South | SouthWest |
West | NorthWest |
Center
datatype IconKind =
NoIcon
| TkBitmap of BitmapName
| FileBitmap of BitmapFile
| FileImage of ImageFile * ImageId
datatype CursorKind =
NoCursor
| XCursor of CursorName * ((Color * (Color option)) option)
| FileCursor of CursorFile * Color * ((CursorFile * Color) option)
datatype Configure =
Width of int
| Height of int
| Borderwidth of int
| Relief of RelKind
| Foreground of Color
| Background of Color
| Text of string
| Font of Fonts.Font
| Variable of string
| Value of string
| Icon of IconKind
| Cursor of CursorKind
| Command of SimpleAction
| Anchor of AnchorKind
| TextWidReadOnly of bool
| FillColor of Color
| Outline of Color
| OutlineWidth of int
| Stipple
| Smooth of bool
| Arrow of ArrowPos
| Capstyle of CapstyleKind
| Joinstyle of JoinstyleKind
datatype UserKind =
User
| Program
datatype WinConfigure =
WinAspect of int * int * int *int
| WinGeometry of ((int * int) option)
* ((int * int) option)
| WinMaxSize of int * int
| WinMinSize of int * int
| WinPositionFrom of UserKind
| WinSizeFrom of UserKind
| WinTitle of string
| WinGroup of WinId
| WinTransient of WinId option
| WinOverride of bool
datatype Edge = Top | Bottom | Left | Right
datatype Style = X | Y | Both
datatype ScrollType = NoneScb | LeftScb | RightScb
datatype Pack =
Expand of bool
| Fill of Style
| PadX of int
| PadY of int
| Side of Edge
datatype Mark =
Mark of int * int
| MarkToEnd of int
| MarkEnd
datatype MItem =
MCheckbutton of (Configure) list
| MRadiobutton of (Configure) list
| MCascade of MItem list * Configure list
| MSeparator
| MCommand of (Configure) list
datatype AnnotationType =
ATTag | ATWidget
datatype CItemType =
CTRectangle | CTOval | CTLine | CTPoly | CTArc | CTText |
CTIcon | CTWidget | CTTag
datatype WidgetType =
Fra | Mes | Lab | Lis | But | Che | Rad | Men
| Menbut | Ent | Can | Tex | Pop
| MChe | MRad | MCas | MSep | MCo
datatype CItem =
CRectangle of CItemId *
Coord * Coord *
Configure list * Binding list
| COval of CItemId *
Coord * Coord *
Configure list * Binding list
| CLine of CItemId *
Coord list *
Configure list * Binding list
| CPoly of CItemId *
Coord list *
Configure list * Binding list
| CIcon of CItemId *
Coord * IconKind *
Configure list * Binding list
| CWidget of CItemId *
Coord * WidId * Widget list * Configure list *
Configure list * Binding list
| CTag of CItemId *
CItemId list
and AnnoText =
AnnoText of (int* int) option* string* Annotation list
and Annotation =
TATag of AnnId * (Mark * Mark) list *
Configure list * Binding list
| TAWidget of AnnId * Mark * WidId * Widget list * Configure list *
Configure list * Binding list
and Widget =
Frame of WidId * Widget list *
Pack list * Configure list * Binding list
| Message of WidId *
Pack list * Configure list * Binding list
| Label of WidId *
Pack list * Configure list * Binding list
| Listbox of WidId * ScrollType *
Pack list * Configure list * Binding list
| Button of WidId *
Pack list * Configure list * Binding list
| Radiobutton of WidId *
Pack list * Configure list * Binding list
| Checkbutton of WidId *
Pack list * Configure list * Binding list
| Menubutton of WidId * bool * MItem list *
Pack list * Configure list * Binding list
| Entry of WidId *
Pack list * Configure list * Binding list
| TextWid of WidId * ScrollType * AnnoText *
Pack list * Configure list * Binding list
| Canvas of WidId * ScrollType * CItem list *
Pack list * Configure list * Binding list
| Popup of WidId * bool * MItem list
type Window = (WinId * (WinConfigure)list * (Widget)list * SimpleAction)
type GUI = ((Window) list * (PathAss list))
type TclAnswer = string
val GUI_state = ref([]:(Window) list,[]:PathAss list,[]:TclAnswer list)
type AppId = string
type CallBack = string -> unit
type QuitAction = unit -> unit
type programName = string
type programParms = string list
type program = (programName * programParms)
type protocolName = string
type App =
(AppId * TextIO.instream * TextIO.outstream * TextIO.outstream *
CallBack * QuitAction)
val COM_state = ref([]:App list,"","","","","")
exception CONFIG of string
exception WIDGET of string
exception WINDOWS of string
exception TCL_ERROR of string
fun selWindowWinId (a, _, _ ,_) = a
fun selWindowConfigures (_, wc, _ , _) = wc
fun selWindowWidgets (_, _, c,_ ) = c
fun selWindowAction (_, _, _,d ) = d
fun updWindowConfigures (id,wc,c,d) wc' = (id,wc',c,d)
fun isInitWin w =
(fn ([], _, _) => true
| (win::wins, _, _) => (w = (selWindowWinId win))) (!GUI_state)
fun selWidgetWidId(Frame(w, _, _, _, _)) = w
| selWidgetWidId(Message(w,_, _, _)) = w
| selWidgetWidId(Label(w, _, _, _)) = w
| selWidgetWidId(Listbox(w, _, _, _, _)) = w
| selWidgetWidId(Button(w, _, _, _)) = w
| selWidgetWidId(Radiobutton(w, _, _, _)) = w
| selWidgetWidId(Checkbutton(w, _, _, _)) = w
| selWidgetWidId(Menubutton(w,_,_,_,_,_)) = w
| selWidgetWidId(TextWid(w, _, _, _, _, _)) = w
| selWidgetWidId(Canvas(w, _, _, _, _, _)) = w
| selWidgetWidId(Popup(w, _, _)) = w
| selWidgetWidId(Entry(w, _, _, _)) = w
fun selWidgetWidgetType(Frame _) = Fra
| selWidgetWidgetType(Message _) = Mes
| selWidgetWidgetType(Label _) = Lab
| selWidgetWidgetType(Listbox _) = Lis
| selWidgetWidgetType(Button _) = But
| selWidgetWidgetType(Radiobutton _) = Rad
| selWidgetWidgetType(Checkbutton _) = Che
| selWidgetWidgetType(Menubutton _) = Menbut
| selWidgetWidgetType(TextWid _) = Tex
| selWidgetWidgetType(Canvas _) = Can
| selWidgetWidgetType(Popup _) = Pop
| selWidgetWidgetType(Entry _) = Ent
fun selWidgetBinding (Frame(_, _, _, _, b)) = b
| selWidgetBinding (Message(_, _, _, b)) = b
| selWidgetBinding (Label(_, _, _, b)) = b
| selWidgetBinding (Listbox(_, _, _, _, b)) = b
| selWidgetBinding (Button(_, _, _, b)) = b
| selWidgetBinding (Radiobutton(_, _, _, b)) = b
| selWidgetBinding (Checkbutton(_, _, _, b)) = b
| selWidgetBinding (Menubutton(_,_,_,_,_,b)) = b
| selWidgetBinding (TextWid(_, _, _, _, _, b)) = b
| selWidgetBinding (Canvas(_, _, _, _, _, b)) = b
| selWidgetBinding (Popup _) = []
| selWidgetBinding (Entry(_, _, _, b)) = b
fun updWidgetBinding (Frame(w, ws, p, c, _)) nb = Frame(w, ws, p, c, nb)
| updWidgetBinding (Message(w, p, c, _)) nb = Message(w, p, c, nb)
| updWidgetBinding (Label(w, p, c, _)) nb = Label(w, p, c, nb)
| updWidgetBinding (Listbox(w, st, p, c, _)) nb = Listbox(w, st, p, c, nb)
| updWidgetBinding (Button(w, p, c, _)) nb = Button(w, p, c, nb)
| updWidgetBinding (Radiobutton(w, p, c, _)) nb = Radiobutton(w, p, c, nb)
| updWidgetBinding (Checkbutton(w, p, c, _)) nb = Checkbutton(w, p, c, nb)
| updWidgetBinding (Menubutton(w,to,ms,p,c,_)) nb = Menubutton(w,to,ms,p,c,nb)
| updWidgetBinding (Entry(w, p, c, _)) nb = Entry(w, p, c, nb)
| updWidgetBinding (Canvas(w, st, ci, p, c, _)) nb= Canvas(w, st, ci, p, c, nb)
| updWidgetBinding (TextWid(w,st,at,p,c,_)) nb = TextWid(w,st,at,p,c,nb)
| updWidgetBinding (Popup(w, b, ml)) nb = Popup(w, b, ml)
fun selWidgetConfigure (Frame(_, _, _, c, _)) = c
| selWidgetConfigure (Message(_, _, c, _)) = c
| selWidgetConfigure (Label(_, _, c, _)) = c
| selWidgetConfigure (Listbox(_, _, _, c, _)) = c
| selWidgetConfigure (Button(_, _, c, _)) = c
| selWidgetConfigure (Radiobutton(_, _, c, _)) = c
| selWidgetConfigure (Checkbutton(_, _, c, _)) = c
| selWidgetConfigure (Menubutton(_,_,_,_,c,_)) = c
| selWidgetConfigure (TextWid(_,_,_,_,c,_)) = c
| selWidgetConfigure (Canvas(_, _, _, _, c, _)) = c
| selWidgetConfigure (Popup _) = []
| selWidgetConfigure (Entry(_, _, c, _)) = c
fun updWidgetConfigure (Frame(w, ws, p, _, b)) nc = Frame(w, ws, p ,nc, b)
| updWidgetConfigure (Message(w, p, _, b)) nc = Message(w, p ,nc, b)
| updWidgetConfigure (Label(w, p, _, b)) nc = Label(w, p, nc, b)
| updWidgetConfigure (Listbox(w, st, p, _, b)) nc = Listbox(w, st, p, nc, b)
| updWidgetConfigure (Button(w, p, _, b)) nc = Button(w, p, nc, b)
| updWidgetConfigure (Radiobutton(w, p, _, b)) nc = Checkbutton(w, p, nc, b)
| updWidgetConfigure (Checkbutton(w, p, _, b)) nc = Checkbutton(w, p, nc, b)
| updWidgetConfigure (Menubutton(w,to,ms,p,_,b)) nc = Menubutton(w,to,ms,p,nc,b)
| updWidgetConfigure (Entry(w, p, _, b)) nc = Entry(w, p, nc, b)
| updWidgetConfigure (Canvas(w, st, ci, p, _, b)) nc = Canvas(w, st, ci, p, nc, b)
| updWidgetConfigure (TextWid(w,st,at,p,_,b)) nc = TextWid(w,st,at,p,nc,b)
| updWidgetConfigure (Popup(w, b, ml)) nc = Popup(w, b, ml)
fun selMItemConfigure (MCommand cs) = cs
| selMItemConfigure (MCheckbutton cs) = cs
| selMItemConfigure (MRadiobutton cs) = cs
| selMItemConfigure (MCascade (_, cs)) = cs
| selMItemConfigure _ = []
fun selMItemWidgetType MSeparator = MSep
| selMItemWidgetType (MCheckbutton _) = MChe
| selMItemWidgetType (MRadiobutton _) = MRad
| selMItemWidgetType (MCascade (_,_)) = MCas
| selMItemWidgetType (MCommand _) = MCo
fun scrollTypeToEdge LeftScb = Left
| scrollTypeToEdge RightScb = Right
| scrollTypeToEdge NoneScb =
raise CONFIG "BasicTypes.scrollTypeToEdge: match exhausted"
fun scrollTypeToOppEdge LeftScb = Right
| scrollTypeToOppEdge RightScb = Left
| scrollTypeToOppEdge NoneScb =
raise CONFIG "BasicTypes.scrollTypeToOppEdge: match exhausted"
local open BasicUtil
in
fun getWindowsGUI () =
let
val (windows,_,_) = !GUI_state
in
windows
end
fun getPathAssGUI () =
let
val (_,pathAss,_) = !GUI_state
in
pathAss
end
fun getTclAnswersGUI () =
let
val (_,_,answ) = !GUI_state
in
answ
end
fun getWindowGUI w =
ListUtil.getx ((eq w) o selWindowWinId)
(getWindowsGUI())
(WINDOWS ("getWindowGUI with windowId \"" ^ w ^ "\""))
fun updWindowGUI win newwin =
let
val (wins, ass, answ) = !GUI_state
in
GUI_state := (ListUtil.updateVal ((eq win) o selWindowWinId)
newwin wins, ass, answ)
end
end
fun updWindowsGUI nwins =
let
val (wins, ass, answ) = !GUI_state
in
GUI_state := (nwins, ass, answ)
end
fun updPathAssGUI nass =
let
val (wins, ass, answ) = !GUI_state
in
GUI_state := (wins, nass, answ)
end
fun updTclAnswersGUI nansw =
let
val (wins, ass, answ) = !GUI_state
in
GUI_state := (wins, ass, nansw)
end
fun updGUI (nwins,nass,answs) = GUI_state := (nwins,nass,answs)
fun updLogfilename log =
let val (cms, _, wishpth,tcl_init,rootpth,fntpth) = !COM_state
in COM_state:=(cms, log, wishpth,tcl_init,rootpth,fntpth) end
fun getLogfilename () =
let val (_, ln, _, _, _, _) = !COM_state
in ln end
fun updWishPath log =
let val (cms, ln, _,tcl_init,rootpth,fntpth) = !COM_state
val _ = print ("wish path being set to " ^ log ^ "\n")
in COM_state:=(cms, ln, log,tcl_init,rootpth,fntpth) end
fun getWishPath ()=
let val (_, _, pt, _, _, _) = !COM_state
in pt end
fun updTclInit tcl_init =
let val (cms, ln, log, _, rootpth, fntpth) = !COM_state
in COM_state:=(cms, ln, log, tcl_init, rootpth, fntpth) end
fun getTclInit ()=
let val (_, _, _,ti , _, _) = !COM_state
in ti end
fun updSrcPath rp =
let val (cms, ln, log, tcli, _, fntpth) = !COM_state
in COM_state := (cms, ln, log, tcli, rp, fntpth)
end
fun getSrcPath () =
let val (_, _, _, _, rp, _) = !COM_state
in rp
end
fun updFntPath fp =
let val (cms, ln, log, tcli, rp, _) = !COM_state
in COM_state := (cms, ln, log, tcli, rp, fp)
end
fun getFntPath () =
let val (_, _, _, _, _, fp) = !COM_state
in fp
end
end
;
