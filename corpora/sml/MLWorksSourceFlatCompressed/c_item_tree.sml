require "basic_types";
require "paths";
require "c_item";
require "widget_tree";
require "c_item_tree_sig";
structure CItemTree : C_ITEM_TREE =
struct
local open BasicTypes in
exception CITEM_TREE of string
fun get wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val cit = CItem.get widg cid
in
cit
end
fun upd wid cid cit =
let
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.upd widg cid cit
in
WidgetTree.updWidgetGUI nwidg
end
fun add wid (cit as (CWidget _)) =
let
val (win,p) = Paths.getIntPathGUI wid
val np = p ^ ".cnv." ^ (CItem.selItemWidId cit)
val wids = CItem.selItemWidgets cit
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.add WidgetTree.packWidget widg cit
in
(WidgetTree.updWidgetGUI nwidg;
app (WidgetTree.addWidgetPathAssGUI win np) wids)
end
| add wid cit =
let
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.add WidgetTree.packWidget widg cit
in
WidgetTree.updWidgetGUI nwidg
end
fun delete wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.delete WidgetTree.deleteWidgetGUI widg cid
in
WidgetTree.updWidgetGUI nwidg
end
fun getConfigure wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val cit = CItem.get widg cid
val cl = CItem.selItemConfigure cit
in
cl
end
fun addConfigure wid cid cf =
let
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.addItemConfigure widg cid cf
in
WidgetTree.updWidgetGUI nwidg
end
fun getBinding wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val cit = CItem.get widg cid
val cl = CItem.selItemBinding cit
in
cl
end
fun addBinding wid cid bi =
let
val widg = WidgetTree.getWidgetGUI wid
val nwidg = CItem.addItemBinding widg cid bi
in
WidgetTree.updWidgetGUI nwidg
end
fun getCoords wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val col = CItem.getCoords widg cid
in
col
end
fun setCoords wid cid col =
let
val widg = WidgetTree.getWidgetGUI wid
in
CItem.setCoords widg cid col
end
fun getWidth wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val w = CItem.getWidth widg cid
in
w
end
fun getHeight wid cid =
let
val widg = WidgetTree.getWidgetGUI wid
val w = CItem.getHeight widg cid
in
w
end
val getIconWidth = CItem.getIconWidth
val getIconHeight = CItem.getIconHeight
fun move wid cid delta =
let
val widg = WidgetTree.getWidgetGUI wid
in
CItem.move widg cid delta
end
end
end
;
