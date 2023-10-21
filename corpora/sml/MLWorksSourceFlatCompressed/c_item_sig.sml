require "basic_types";
signature C_ITEM =
sig
exception CITEM of string
type widgetPackFun
type widgetAddFun
type widgetDelFun
type widgetUpdFun
type widgetAddFunc
type widgetDelFunc
val selCanvasWidId : BasicTypes.Widget -> BasicTypes.WidId
val selCanvasScrollType : BasicTypes.Widget -> BasicTypes.ScrollType
val selCanvasItems : BasicTypes.Widget -> BasicTypes.CItem list
val selCanvasPack : BasicTypes.Widget -> BasicTypes.Pack list
val selCanvasConfigure : BasicTypes.Widget -> BasicTypes.Configure list
val selCanvasBinding : BasicTypes.Widget -> BasicTypes.Binding list
val updCanvasWidId : BasicTypes.Widget -> BasicTypes.WidId ->
BasicTypes.Widget
val updCanvasScrollType : BasicTypes.Widget -> BasicTypes.ScrollType ->
BasicTypes.Widget
val updCanvasItems : BasicTypes.Widget -> BasicTypes.CItem list ->
BasicTypes.Widget
val updCanvasPack : BasicTypes.Widget -> BasicTypes.Pack list ->
BasicTypes.Widget
val updCanvasConfigure : BasicTypes.Widget -> BasicTypes.Configure list ->
BasicTypes.Widget
val updCanvasBinding : BasicTypes.Widget -> BasicTypes.Binding list ->
BasicTypes.Widget
val getCanvasWidgets : BasicTypes.Widget -> BasicTypes.Widget list
val getCanvasCItemWidgetAssList : BasicTypes.Widget ->
(BasicTypes.CItem * BasicTypes.Widget list) list
val addCanvasWidget : (widgetAddFun) ->
BasicTypes.Widget -> BasicTypes.Widget ->
BasicTypes.WidPath -> BasicTypes.Widget
val deleteCanvasWidget : (widgetDelFun) ->
BasicTypes.Widget -> BasicTypes.WidId ->
BasicTypes.WidPath -> BasicTypes.Widget
val updCanvasWidget : (widgetUpdFun) ->
BasicTypes.Widget -> BasicTypes.WidId ->
BasicTypes.WidPath -> BasicTypes.Widget ->
BasicTypes.Widget
val selItemType : BasicTypes.CItem -> BasicTypes.CItemType
val selItemId : BasicTypes.CItem -> BasicTypes.CItemId
val selItemConfigure : BasicTypes.CItem -> BasicTypes.Configure list
val selItemBinding : BasicTypes.CItem -> BasicTypes.Binding list
val selItemCoords : BasicTypes.CItem -> BasicTypes.Coord list
val selItemWidgets : BasicTypes.CItem -> BasicTypes.Widget list
val selItemWidgetConfigure : BasicTypes.CItem -> BasicTypes.Configure list
val selItemWidId : BasicTypes.CItem -> BasicTypes.WidId
val selItemItems : BasicTypes.CItem -> BasicTypes.CItemId list
val selItemIcon : BasicTypes.CItem -> BasicTypes.IconKind
val updItemConfigure : BasicTypes.CItem -> BasicTypes.Configure list ->
BasicTypes.CItem
val updItemBinding : BasicTypes.CItem -> BasicTypes.Binding list ->
BasicTypes.CItem
val updItemCoords : BasicTypes.CItem -> BasicTypes.Coord list ->
BasicTypes.CItem
val updItemWidgets : BasicTypes.CItem -> BasicTypes.Widget list ->
BasicTypes.CItem
val updItemWidgetConfigure : BasicTypes.CItem -> BasicTypes.Configure list ->
BasicTypes.CItem
val updItemItems : BasicTypes.CItem -> BasicTypes.CItemId list ->
BasicTypes.CItem
val updItemIcon : BasicTypes.CItem -> BasicTypes.IconKind ->
BasicTypes.CItem
val get : BasicTypes.Widget -> BasicTypes.CItemId -> BasicTypes.CItem
val getBindingByName :
BasicTypes.Widget -> BasicTypes.CItemId -> string ->
BasicTypes.Action
val upd : BasicTypes.Widget -> BasicTypes.CItemId -> BasicTypes.CItem ->
BasicTypes.Widget
val add : widgetPackFun ->
BasicTypes.Widget -> BasicTypes.CItem -> BasicTypes.Widget
val delete : widgetDelFunc ->
BasicTypes.Widget -> BasicTypes.CItemId -> BasicTypes.Widget
val addItemConfigure : BasicTypes.Widget -> BasicTypes.CItemId ->
BasicTypes.Configure list -> BasicTypes.Widget
val addItemBinding : BasicTypes.Widget -> BasicTypes.CItemId ->
BasicTypes.Binding list -> BasicTypes.Widget
val pack : widgetPackFun -> BasicTypes.TclPath -> BasicTypes.IntPath ->
BasicTypes.CItem -> unit
val newId : unit -> BasicTypes.CItemId
val newFrId : unit -> BasicTypes.WidId
val check : BasicTypes.CItem -> bool
val getCoords : BasicTypes.Widget -> BasicTypes.CItemId ->
BasicTypes.Coord list
val setCoords : BasicTypes.Widget -> BasicTypes.CItemId ->
BasicTypes.Coord list -> unit
val getWidth : BasicTypes.Widget -> BasicTypes.CItemId -> int
val getHeight : BasicTypes.Widget -> BasicTypes.CItemId -> int
val getIconWidth : BasicTypes.IconKind -> int
val getIconHeight : BasicTypes.IconKind -> int
val move : BasicTypes.Widget -> BasicTypes.CItemId ->
BasicTypes.Coord -> unit
end
;
