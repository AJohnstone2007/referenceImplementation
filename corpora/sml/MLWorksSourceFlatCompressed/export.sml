require "basic_util";
require "basic_types";
require "com";
require "config";
require "tk_event";
require "paths";
require "coord";
require "c_item";
require "ann_texts";
require "mark";
require "annotation";
require "tk_types";
require "widget_tree";
require "c_item_tree";
require "annotation_tree";
require "tk_windows";
require "eventloop";
require "widget_ops";
require "sys_init";
signature SML_TK =
sig
val newWinId : unit -> TkTypes.WinId
val newWidgetId : unit -> TkTypes.WidId
val newCItemId : unit -> TkTypes.CItemId
val newCItemFrameId : unit -> TkTypes.WidId
val newAnnotationId : unit -> TkTypes.AnnId
val newAnnotationFrameId : unit -> TkTypes.WidId
val mkWinId : string -> TkTypes.WinId
val mkCItemId : string -> TkTypes.CItemId
val mkWidgetId : string -> TkTypes.WidId
val subWidId : TkTypes.WidId* string-> TkTypes.WidId
val startTcl : TkTypes.Window list -> unit
val startTclExn : TkTypes.Window list -> string
val exitTcl : unit -> unit
val resetTcl : unit -> unit
val getWindow : TkTypes.WinId -> TkTypes.Window
val getAllWindows : unit -> TkTypes.Window list
val openWindow : TkTypes.Window -> unit
val closeWindow : TkTypes.WinId -> unit
val changeTitle : TkTypes.WinId -> TkTypes.Title -> unit
val occursWin : TkTypes.WinId -> bool
val occursWidget : TkTypes.WidId-> bool
val getWidget : TkTypes.WidId -> TkTypes.Widget
val addWidget : TkTypes.WinId -> TkTypes.WidId -> TkTypes.Widget -> unit
val delWidget : TkTypes.WidId -> unit
val addBind : TkTypes.WidId -> TkTypes.Binding list -> unit
val addConf : TkTypes.WidId -> TkTypes.Configure list -> unit
val setBind : TkTypes.WidId -> TkTypes.Binding list -> unit
val setConf : TkTypes.WidId -> TkTypes.Configure list -> unit
val getTextWidWidgets : TkTypes.Widget -> TkTypes.Widget list
val getCanvasWidgets : TkTypes.Widget -> TkTypes.Widget list
val getConf : TkTypes.WidId -> TkTypes.Configure list
val getRelief : TkTypes.WidId -> TkTypes.RelKind
val getCommand : TkTypes.WidId -> TkTypes.SimpleAction
val getBindings : TkTypes.WidId -> TkTypes.Binding list
val getWidth : TkTypes.WidId -> int
val getHeight : TkTypes.WidId -> int
val getMCommand : TkTypes.WidId -> int list -> TkTypes.SimpleAction
val insertText : TkTypes.WidId -> string -> TkTypes.Mark -> unit
val insertTextEnd : TkTypes.WidId -> string -> unit
val clearText : TkTypes.WidId -> unit
val deleteText : TkTypes.WidId -> TkTypes.Mark * TkTypes.Mark -> unit
val readText : TkTypes.WidId -> TkTypes.Mark * TkTypes.Mark -> string
val readTextAll : TkTypes.WidId -> string
val readTextWidState : TkTypes.WidId -> bool
val setTextWidReadOnly : TkTypes.WidId -> bool -> unit
val clearTextWidText : TkTypes.WidId -> unit
val replaceTextWidText : TkTypes.WidId -> TkTypes.AnnoText-> unit
val readCursor : TkTypes.WidId -> TkTypes.Mark
val readSelRange : TkTypes.WidId -> (TkTypes.Mark * TkTypes.Mark) list
val mkAT : string -> TkTypes.AnnoText
val mtAT : TkTypes.AnnoText
infix 6 ++
val ++ : TkTypes.AnnoText * TkTypes.AnnoText -> TkTypes.AnnoText
val nlAT : TkTypes.AnnoText -> TkTypes.AnnoText
val concatATWith : string -> TkTypes.AnnoText list -> TkTypes.AnnoText
val getAnnotation : TkTypes.WidId -> TkTypes.AnnId -> TkTypes.Annotation
val addAnnotation : TkTypes.WidId -> TkTypes.Annotation -> unit
val delAnnotation : TkTypes.WidId -> TkTypes.AnnId -> unit
val getAnnotationBind : TkTypes.WidId -> TkTypes.AnnId -> TkTypes.Binding list
val getAnnotationConf : TkTypes.WidId -> TkTypes.AnnId -> TkTypes.Configure list
val addAnnotationBind : TkTypes.WidId -> TkTypes.AnnId -> TkTypes.Binding list -> unit
val addAnnotationConf : TkTypes.WidId -> TkTypes.AnnId -> TkTypes.Configure list -> unit
val readAnnotationMarks : TkTypes.WidId -> TkTypes.AnnId -> (TkTypes.Mark * TkTypes.Mark) list
val readSelection : TkTypes.WidId -> (TkTypes.Mark * TkTypes.Mark) list
val getCItem : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.CItem
val addCItem : TkTypes.WidId -> TkTypes.CItem -> unit
val delCItem : TkTypes.WidId -> TkTypes.CItemId -> unit
val getCItemBind : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Binding list
val getCItemConf : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Configure list
val addCItemBind : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Binding list -> unit
val addCItemConf : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Configure list -> unit
val readCItemCoords : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Coord list
val readCItemHeight : TkTypes.WidId -> TkTypes.CItemId -> int
val readCItemWidth : TkTypes.WidId -> TkTypes.CItemId -> int
val moveCItem : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Coord -> unit
val setCItemCoords : TkTypes.WidId -> TkTypes.CItemId -> TkTypes.Coord list -> unit
val popUpMenu : TkTypes.WidId -> int option -> TkTypes.Coord -> unit
val setVarValue : string -> string -> unit
val readVarValue : string -> string
val addCoord : TkTypes.Coord -> TkTypes.Coord -> TkTypes.Coord
val subCoord : TkTypes.Coord-> TkTypes.Coord -> TkTypes.Coord
val smultCoord : TkTypes.Coord-> int-> TkTypes.Coord
type Rect
val inside : TkTypes.Coord -> Rect -> bool
val moveRect : Rect -> TkTypes.Coord -> Rect
val addApp : TkTypes.AppId * TkTypes.program * TkTypes.protocolName
* TkTypes.CallBack * TkTypes.QuitAction -> unit
val removeApp : TkTypes.AppId -> unit
val getApp : TkTypes.AppId -> TkTypes.App
val getLineApp : TkTypes.AppId -> string
val getLineMApp : TkTypes.AppId -> string
val putLineApp : TkTypes.AppId -> string -> unit
val checkWidId : TkTypes.WidId -> bool
val checkWinId : TkTypes.WinId -> bool
val checkWinTitle : TkTypes.Title -> bool
val focus : TkTypes.WinId -> unit
val deFocus : TkTypes.WinId -> unit
val grab : TkTypes.WinId -> unit
val deGrab : TkTypes.WinId -> unit
val readSelWindow : unit -> (TkTypes.WinId * TkTypes.WidId) option
val initSmlTk : unit -> unit
val getSrcPath : unit -> string
end
structure SmlTk : SML_TK =
struct
open BasicUtil
open Com
open Coord
open CItem
open CItemTree
open Annotation
open AnnotationTree
open Paths
open Config
open TkEvent
open AnnotatedText
open WidgetTree
open Window
open Eventloop
open WidgetOps
val getSrcPath = BasicTypes.getSrcPath
val occursWin = occursWindowGUI;
val changeTitle = Window.changeTitle;
val checkWin = Window.check;
val checkWinId = Window.checkWinId;
val checkWinTitle = Window.checkTitle;
val openWindow = Window.openW
val closeWindow = Window.close
val getWindow = BasicTypes.getWindowGUI
val getAllWindows = BasicTypes.getWindowsGUI
val addCoord = Coord.add
val subCoord = Coord.sub
val smultCoord = Coord.smult
val showCoord = Coord.show
val convCoord = Coord.read
val showMark = Mark.show
val showMarkL = Mark.showL
val occursWidget = Paths.occursWidgetGUI
val delWidget = WidgetTree.deleteWidget
val addConf = WidgetTree.configure
val addBind = WidgetTree.addBindings
val setBind = WidgetTree.newBindings
val setConf = WidgetTree.newconfigure
val getWidget = selectWidget
val getConf = select
val getRelief = selectRelief
val getCommand = selectCommand
val getWidth = selectWidth
val getHeight = selectHeight
val getBindings = selectBindings
val getMCommand = selectMCommand
val addCItem = CItemTree.add
val delCItem = CItemTree.delete
val addCItemBind = CItemTree.addBinding
val addCItemConf = CItemTree.addConfigure
val getCItem = CItemTree.get
val getCItemBind = CItemTree.getBinding
val getCItemConf = CItemTree.getConfigure
val moveCItem = CItemTree.move
val setCItemCoords = CItemTree.setCoords
val updCItem = CItemTree.upd
val mkAT = mk
val nlAT = nl
val getAnnotation = AnnotationTree.get
val updAnnotation = AnnotationTree.upd
val addAnnotation = AnnotationTree.add
val delAnnotation = AnnotationTree.delete
val getAnnotationBind = AnnotationTree.getBinding
val getAnnotationConf = AnnotationTree.getConfigure
val addAnnotationBind = AnnotationTree.addBinding
val addAnnotationConf = AnnotationTree.addConfigure
val readAnnotationMarks = AnnotationTree.readMarks
val readSelWindow = selectSelWindow
val readVarValue = selectVarValue
val readText = selectText
val readTextAll = selectTextAll
val readCursor = WidgetOps.selectCursor
val readSelRange = WidgetOps.selectSelRange
val readCItemCoords = CItemTree.getCoords
val readCItemHeight = CItemTree.getHeight
val readCItemWidth = CItemTree.getWidth
val readIconHeight = getIconHeight
val readIconWidth = getIconWidth
val newCItemId = CItem.newId
val newCItemFrameId = newFrId
val newAnnotationId = Annotation.newId
val newAnnotationFrameId = Annotation.newFrId
val newWinId = newWidgetId
fun mkWinId str = str ^ newWinId()
fun mkFrameId str = str ^ newFrId()
fun mkCItemId str = str ^ CItem.newId()
fun mkWidgetId str = str ^ newWidgetId()
fun subWidId(w, str)= w ^ str
val initSmlTk = SysInit.initSmlTk
end
;
