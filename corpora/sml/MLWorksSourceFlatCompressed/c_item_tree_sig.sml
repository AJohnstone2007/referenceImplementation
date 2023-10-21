require "basic_types";
signature C_ITEM_TREE =
sig
exception CITEM_TREE of string
val get : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.CItem
val upd : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.CItem -> unit
val add : BasicTypes.WidId -> BasicTypes.CItem -> unit
val delete : BasicTypes.WidId -> BasicTypes.CItemId -> unit
val getConfigure : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Configure list
val addConfigure : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Configure list -> unit
val getBinding : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Binding list
val addBinding : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Binding list -> unit
val getCoords : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Coord list
val setCoords : BasicTypes.WidId -> BasicTypes.CItemId ->
BasicTypes.Coord list -> unit
val getWidth : BasicTypes.WidId -> BasicTypes.CItemId -> int
val getHeight : BasicTypes.WidId -> BasicTypes.CItemId -> int
val getIconWidth : BasicTypes.IconKind -> int
val getIconHeight : BasicTypes.IconKind -> int
val move : BasicTypes.WidId -> BasicTypes.CItemId -> BasicTypes.Coord -> unit
end
;
