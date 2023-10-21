require "__list";
require "__int";
require "__list_pair";
require "__string";
require "basic_util";
require "debug";
require "basic_types";
require "com";
require "config";
require "bind";
require "paths";
require "coord";
require "c_item_sig";
structure CItem : C_ITEM =
struct
local open BasicTypes BasicUtil in
exception CITEM of string
type widgetPackFun = bool -> TclPath -> IntPath -> Widget -> unit
type widgetAddFun = Widget list -> Widget -> WidPath -> Widget list
type widgetDelFun = Widget list -> WidId -> WidPath -> Widget list
type widgetUpdFun = Widget list -> WidId -> WidPath -> Widget -> Widget list
type getValFun = string -> string
type widgetDelFunc = WidId -> unit
type widgetAddFunc = WinId -> WidPath -> Widget -> unit
fun selCanvasWidId (Canvas(wid,_,_,_,_,_)) = wid
| selCanvasWidId _ =
raise WIDGET "CItem.selCanvasId applied to non-Canvas Widget"
fun selCanvasScrollType (Canvas(_,st,_,_,_,_)) = st
| selCanvasScrollType _ =
raise WIDGET "CItem.selCanvasScrollType applied to non-Canvas Widget"
fun selCanvasItems (Canvas(_,_,cit,_,_,_)) = cit
| selCanvasItems _ =
raise WIDGET "CItem.selCanvasItems applied to non-Canvas Widget"
fun selCanvasPack (Canvas(_,_,_,p,_,_)) = p
| selCanvasPack _ =
raise WIDGET "CItem.selCanvasPack applied to non-Canvas Widget"
fun selCanvasConfigure (Canvas(_,_,_,_,c,_)) = c
| selCanvasConfigure _ =
raise WIDGET "CItem.selCanvasConfigure applied to non-Canvas Widget"
fun selCanvasBinding (Canvas(_,_,_,_,_,b)) = b
| selCanvasBinding _ =
raise WIDGET "CItem.selCanvasBinding applied to non-Canvas Widget"
fun updCanvasWidId (Canvas(_,st,its,p,c,b)) wid = Canvas(wid,st,its,p,c,b)
| updCanvasWidId _ _ =
raise WIDGET "CItem.updCanvasWidId applied to non-Canvas Widget"
fun updCanvasScrollType (Canvas(wid,_,its,p,c,b)) st = Canvas(wid,st,its,p,c,b)
| updCanvasScrollType _ _ =
raise WIDGET "CItem.updCanvasScrollType applied to non-Canvas Widget"
fun updCanvasItems (Canvas(wid,st,_,p,c,b)) its = Canvas(wid,st,its,p,c,b)
| updCanvasItems _ _ =
raise WIDGET "CItem.updCanvasItems applied to non-Canvas Widget"
fun updCanvasPack (Canvas(wid,st,its,_,c,b)) p = Canvas(wid,st,its,p,c,b)
| updCanvasPack _ _ =
raise WIDGET "CItem.updCanvasPack applied to non-Canvas Widget"
fun updCanvasConfigure (Canvas(wid,st,its,p,_,b)) c = Canvas(wid,st,its,p,c,b)
| updCanvasConfigure _ _ =
raise WIDGET "CItem.updCanvasConfigure applied to non-Canvas Widget"
fun updCanvasBinding (Canvas(wid,st,its,p,c,_)) b = Canvas(wid,st,its,p,c,b)
| updCanvasBinding _ _ =
raise WIDGET "CItem.updCanvasBinding applied to non-Canvas Widget"
fun selItemType (CRectangle(_,_,_,_,_)) = CTRectangle
| selItemType (COval(_,_,_,_,_)) = CTOval
| selItemType (CLine(_,_,_,_)) = CTLine
| selItemType (CPoly(_,_,_,_)) = CTPoly
| selItemType (CIcon(_,_,_,_,_)) = CTIcon
| selItemType (CWidget(_,_,_,_,_,_,_)) = CTWidget
| selItemType (CTag(_,_)) = CTTag
fun selItemId (CRectangle(cid,_,_,_,_)) = cid
| selItemId (COval(cid,_,_,_,_)) = cid
| selItemId (CLine(cid,_,_,_)) = cid
| selItemId (CPoly(cid,_,_,_)) = cid
| selItemId (CIcon(cid,_,_,_,_)) = cid
| selItemId (CWidget(cid,_,_,_,_,_,_)) = cid
| selItemId (CTag(cid,_)) = cid
fun selItemConfigure (CRectangle(_,_,_,c,_)) = c
| selItemConfigure (COval(_,_,_,c,_)) = c
| selItemConfigure (CLine(_,_,c,_)) = c
| selItemConfigure (CPoly(_,_,c,_)) = c
| selItemConfigure (CIcon(_,_,_,c,_)) = c
| selItemConfigure (CWidget(_,_,_,_,_,c,_)) = c
| selItemConfigure (CTag _) =
raise CITEM ("CItem.selItemConfigure: CTag has no Configure")
fun selItemBinding (CRectangle(_,_,_,_,b)) = b
| selItemBinding (COval(_,_,_,_,b)) = b
| selItemBinding (CLine(_,_,_,b)) = b
| selItemBinding (CPoly(_,_,_,b)) = b
| selItemBinding (CIcon(_,_,_,_,b)) = b
| selItemBinding (CWidget(_,_,_,_,_,_,b)) = b
| selItemBinding (CTag _) =
raise CITEM ("CItem.selItemBinding: CTag has no Binding")
fun selItemCoords (CRectangle(_,c1,c2,_,_)) = [c1, c2]
| selItemCoords (COval(_,c1,c2,_,_)) = [c1, c2]
| selItemCoords (CLine(_,cl,_,b)) = cl
| selItemCoords (CPoly(_,cl,_,b)) = cl
| selItemCoords (CIcon(_,c,_,_,b)) = [c]
| selItemCoords (CWidget(_,c,_,_,_,_,_)) = [c]
| selItemCoords (CTag _) =
raise CITEM ("CItem.selItemCoords: CTag has no Coords")
fun selItemWidgets (CWidget(_,_,_,wids,_,_,_)) = wids
| selItemWidgets _ =
raise CITEM ("CItem.selItemWidgets applied to non CWidget")
fun selItemWidgetConfigure (CWidget(_,_,_,_,wc,_,_)) = wc
| selItemWidgetConfigure _ =
raise CITEM ("CItem.selItemWidgetConfigure applied to non CWidget")
fun selItemWidId (CWidget(_,_,widid,_,_,_,_)) = widid
| selItemWidId _ =
raise CITEM ("CItem.selItemWidId applied to non CWidget")
fun selItemItems (CTag(_,cits)) = cits
| selItemItems _ =
raise CITEM ("CItem.selItemItems applied to non CTag")
fun selItemIcon (CIcon(_,_,ic,_,_)) = ic
| selItemIcon _ =
raise CITEM ("CItem.selItemIcon applied to non CIcon")
fun updItemConfigure (CRectangle (cid,co1,co2,_,b)) cf = CRectangle (cid,co1,co2,cf,b)
| updItemConfigure (COval (cid,co1,co2,_,b)) cf = COval (cid,co1,co2,cf,b)
| updItemConfigure (CLine (cid,cos,_,b)) cf = CLine (cid,cos,cf,b)
| updItemConfigure (CPoly (cid,cos,_,b)) cf = CPoly (cid,cos,cf,b)
| updItemConfigure (CIcon (cid,co,ic,_,b)) cf = CIcon (cid,co,ic,cf,b)
| updItemConfigure (CWidget (cid,co,wi,w,wc,_,b)) cf = CWidget (cid,co,wi,w,wc,cf,b)
| updItemConfigure (CTag _) cf =
raise CITEM ("CItem.updItemConfigure: CTag has no Configure")
fun updItemBinding (CRectangle (cid,co1,co2,cf,_)) b = CRectangle (cid,co1,co2,cf,b)
| updItemBinding (COval (cid,co1,co2,cf,_)) b = COval (cid,co1,co2,cf,b)
| updItemBinding (CLine (cid,cos,cf,_)) b = CLine (cid,cos,cf,b)
| updItemBinding (CPoly (cid,cos,cf,_)) b = CPoly (cid,cos,cf,b)
| updItemBinding (CIcon (cid,co,ic,cf,_)) b = CIcon (cid,co,ic,cf,b)
| updItemBinding (CWidget (cid,co,wi,w,wc,cf,_)) b = CWidget (cid,co,wi,w,wc,cf,b)
| updItemBinding (CTag _) b =
raise CITEM ("CItem.updItemBinding: CTag has no Binding")
fun updItemCoords (CRectangle(cid,_,_,cf,bi)) c = CRectangle( cid, (hd c), (hd (tl c)), cf, bi)
| updItemCoords (COval(cid,_,_,cf,bi)) c = COval(cid, (hd c), (hd (tl c)), cf, bi)
| updItemCoords (CLine(cid,_,cf,bi)) c = CLine(cid,c,cf,bi)
| updItemCoords (CPoly(cid,_,cf,bi)) c = CPoly(cid,c,cf,bi)
| updItemCoords (CIcon(cid,_,ic,cf,bi)) c = CIcon(cid, (hd c), ic, cf, bi)
| updItemCoords (CWidget(cid,_,wi,w,wc,cf,bi)) c = CWidget(cid, (hd c), wi, w, wc, cf, bi)
| updItemCoords (CTag _) c =
raise CITEM ("CItem.updItemCoords: CTag has no Coords")
fun updItemWidgets (CWidget(cid,co,wi,_,wc,c,b)) wids = CWidget(cid,co,wi,wids,wc,c,b)
| updItemWidgets _ _ =
raise CITEM ("CItem.updItemWidgets applied to non CWidget")
fun updItemWidgetConfigure (CWidget(cid,co,wi,wids,_,c,b)) wc = CWidget(cid,co,wi,wids,wc,c,b)
| updItemWidgetConfigure _ _ =
raise CITEM ("CItem.updItemWidgetConfigure applied to non CWidget")
fun updItemItems (CTag(cid,_)) cids = CTag(cid,cids)
| updItemItems _ cids =
raise CITEM ("CItem.updItemItems applied to non CTag")
fun updItemIcon (CIcon(cid,co,_,c,b)) ic = CIcon(cid,co,ic,c,b)
| updItemIcon _ ic =
raise CITEM ("CItem.updItemIcon applied to non CIcon")
fun check (_:CItem) = true
fun get wid cid =
let
val cits = selCanvasItems wid
val item = ListUtil.getx
(fn it => ((selItemId it) = cid)) cits
(CITEM ("CItem.get: " ^ cid ^ " not found"))
in
item
end
fun getBindingByName wid cid name =
let
val item = get wid cid
val bis = selItemBinding item
val bi = Bind.getActionByName name bis
in
bi
end
fun upd widg cid ncit =
let
val cits = selCanvasItems widg
val cit = ListUtil.getx
(fn cit => ((selItemId cit) = cid))
cits
(CITEM ("item: " ^ cid ^ " not found"))
val ncits = ListUtil.updateVal (fn cit => ((selItemId cit) = cid))
ncit
cits
val nwidg = updCanvasItems widg ncits
in
nwidg
end
fun getCanvasWidgets (Canvas(wid,st,cits,p,c,b)) =
let
val widits = List.filter (fn cit => (selItemType cit = CTWidget)) cits
val wids = map selItemWidgets widits
val wids' = List.concat wids
in
wids'
end
| getCanvasWidgets _ =
raise WIDGET "CItem.getCanvasWidgets applied to non-Canvas Widget"
fun getCanvasCItemWidgetAssList (Canvas(wid,st,cits,p,c,b)) =
let
val widits = List.filter (fn cit => (selItemType cit = CTWidget)) cits
val wids = map selItemWidgets widits
in
ListPair.zip(widits,wids)
end
| getCanvasCItemWidgetAssList _ =
raise WIDGET "CItem.getCanvasCItemWidgetAssList applied to non-Canvas Widget"
fun addCanvasWidget af (w as (Canvas _)) widg wp =
let
val _ = Debug.print 3 ("addCanvasWidget "^(selWidgetWidId w)^" "^(selWidgetWidId widg)^" "^wp)
val (wId,nwp) = Paths.fstWidPath wp
val (wId',nwp') = Paths.fstWidPath nwp
in
if ( nwp' = "" ) then
raise CITEM "CItem.addCanvasWidgets called for CWidget-Toplevel"
else
let
val (wId'',nwp'') = Paths.fstWidPath nwp'
val citwidass = getCanvasCItemWidgetAssList w
val (cit,swidgs) = ListUtil.getx
(fn (c,(ws:Widget list)) =>
foldr
(fn (w,t) => ((selWidgetWidId w) = wId'') orelse t)
false ws)
citwidass
(CITEM ("CItem.addCanvasWidget: subwidget " ^ wId'' ^ " not found" ))
val _ = Debug.print 3 ("addCanvasWidget(ass): "^(selItemId cit)^" ["^
(String.concat (map (selWidgetWidId) swidgs))^"]")
val nswidgs = af swidgs widg nwp'
val ncit = updItemWidgets cit nswidgs
val nwidg = upd w (selItemId ncit) ncit
in
nwidg
end
end
| addCanvasWidget _ _ _ _ =
raise WIDGET "CItem.addCanvasWidgets applied to non-Canvas Widget"
fun deleteCanvasWidget df (w as (Canvas _)) wid wp =
let
val _ = Debug.print 3 ("deleteCanvasWidget "^(selWidgetWidId w)^" "^wp)
val (wId,nwp) = Paths.fstWidPath wp
val (wId',nwp') = Paths.fstWidPath nwp
val citwidass = getCanvasCItemWidgetAssList w
val (cit,swidgs) = ListUtil.getx
(fn (c,(ws:Widget list)) =>
foldr
(fn (w,t) => ((selWidgetWidId w) = wId') orelse t)
false ws)
citwidass
(CITEM ("CItem.deleteCanvasWidget: subwidget " ^ wId' ^ " not found"))
val nswidgs = df swidgs wId' nwp'
val ncit = updItemWidgets cit nswidgs
val nwidg = upd w (selItemId ncit) ncit
in
nwidg
end
| deleteCanvasWidget _ _ _ _ =
raise WIDGET "CItem.deleteCanvasWidgets applied to non-Canvas Widget"
fun updCanvasWidget uf (w as (Canvas _)) wid wp neww =
let
val _ = Debug.print 3 ("updCanvasWidget "^(selWidgetWidId w)^" "^wp)
val (wId,nwp) = Paths.fstWidPath wp
val (wId',nwp') = Paths.fstWidPath nwp
val citwidass = getCanvasCItemWidgetAssList w
val (cit,swidgs) = ListUtil.getx
(fn (c,(ws:Widget list)) =>
foldr
(fn (w,t) => ((selWidgetWidId w) = wId') orelse t)
false ws)
citwidass
(CITEM ("CItem.updCanvasWidget did not find Subwidget " ^ wId'))
val nswidgs = uf swidgs wId' nwp' neww
val ncit = updItemWidgets cit nswidgs
val nwidg = upd w (selItemId ncit) ncit
in
nwidg
end
| updCanvasWidget _ _ _ _ _ =
raise WIDGET "CItem.updCanvasWidgets applied to non-Canvas Widget"
fun pack pf tp (ip as (win, pt)) (COval(cid,co1,co2,c,b)) =
let
val coords = Coord.show [co1,co2]
val conf = Config.pack ip c
in
(Com.putTclCmd (tp ^ " create oval " ^ coords ^ " " ^ conf ^ " -tags " ^ cid);
app Com.putTclCmd (Bind.packCanvas tp ip cid b) )
end
| pack pf tp (ip as (win, pt)) (CRectangle (cid,co1,co2,c,b)) =
let
val coords = Coord.show [co1,co2]
val conf = Config.pack ip c
in
(Com.putTclCmd (tp ^ " create rectangle " ^ coords ^ " " ^ conf ^ " -tags " ^ cid);
app Com.putTclCmd (Bind.packCanvas tp ip cid b) )
end
| pack pf tp (ip as (win, pt)) (CLine (cid,col,c,b)) =
let
val coords = Coord.show col
val conf = Config.pack ip c
in
(Com.putTclCmd (tp ^ " create line " ^ coords ^ " " ^ conf ^ " -tags " ^ cid);
app Com.putTclCmd (Bind.packCanvas tp ip cid b) )
end
| pack pf tp (ip as (win, pt)) (CIcon (cid,co,ic,c,b)) =
let
val coords = Coord.show [co]
val conf = Config.pack ip c
val icon = Config.showIconKind ic
val ictype =
case ic of
NoIcon => "bitmap"
| TkBitmap _ => "bitmap"
| FileBitmap _ => "bitmap"
| FileImage _ => "image"
in
(Com.putTclCmd (tp ^ " create " ^ ictype ^" " ^ coords ^ " " ^
icon ^ " " ^ conf ^ " -tags " ^ cid);
app Com.putTclCmd (Bind.packCanvas tp ip cid b) )
end
| pack pf tp (ip as (win, pt)) (CWidget (cid,co,widId,ws,wc,c,b)) =
let
val coords = Coord.show [co]
val conf = Config.pack ip c
val frw = Frame(widId,ws,[],wc,[])
val frtp = tp ^ "." ^ widId
in
(ignore (pf false tp ip frw);
Com.putTclCmd (tp ^ " create window " ^ coords ^ " " ^ conf ^
" -window " ^ frtp ^ " -tags " ^ cid);
app Com.putTclCmd (Bind.packCanvas tp ip cid b) )
end
| pack pf tp (ip as (win, pt)) (CTag _) =
()
| pack _ _ _ _ =
raise CITEM ("CItem.pack not yet fully implemented")
fun add pf widg cit =
let
val ip as (win,pt) = Paths.getIntPathGUI (selWidgetWidId widg)
val tp = Paths.getTclPathGUI ip
val nip = (win,pt ^ ".cnv")
val ntp = tp ^ ".cnv"
val cits = selCanvasItems widg
val ncits = cits@[cit]
val nwidg = updCanvasItems widg ncits
in
(pack pf ntp nip cit;
nwidg)
end
fun delete dwf widg cid =
let
fun delete' dwf widg (cit as (CWidget(cid,_,wi,ws,_,_,_))) =
let
val ip as (win,pt) = Paths.getIntPathGUI (selWidgetWidId widg)
val tp = Paths.getTclPathGUI ip
val nip = (win,pt ^ ".cnv")
val ntp = tp ^ ".cnv"
val cits = selCanvasItems widg
val ncits = List.filter (fn cit => not ((selItemId cit) = cid)) cits
val nwidg = updCanvasItems widg ncits
in
(app (dwf o selWidgetWidId) ws;
Com.putTclCmd ("destroy " ^ ntp ^ "." ^ wi);
Com.putTclCmd (ntp ^ " delete " ^ cid);
nwidg)
end
| delete' dwf widg cit =
let
val ip as (win,pt) = Paths.getIntPathGUI (selWidgetWidId widg)
val tp = Paths.getTclPathGUI ip
val nip = (win,pt ^ ".cnv")
val ntp = tp ^ ".cnv"
val cits = selCanvasItems widg
val ncits = List.filter (fn cit => not ((selItemId cit) = cid)) cits
val nwidg = updCanvasItems widg ncits
in
(Com.putTclCmd (ntp ^ " delete " ^ cid);
nwidg)
end
val cit = get widg cid
in
delete' dwf widg cit
end
fun addItemConfigure widg cid cf =
let
val ip as (win,pt) = Paths.getIntPathGUI (selWidgetWidId widg)
val tp = Paths.getTclPathGUI ip
val nip = (win,pt ^ ".cnv")
val ntp = tp ^ ".cnv"
val cits = selCanvasItems widg
val cit = ListUtil.getx (fn cit => ((selItemId cit) = cid))
cits
(CITEM ("item: " ^ cid ^ " not found"))
val conf = selItemConfigure cit
val nconf = Config.add conf cf
val ncit = updItemConfigure cit nconf
val ncits = ListUtil.updateVal (fn cit => ((selItemId cit) = cid))
ncit
cits
val nwidg = updCanvasItems widg ncits
in
(Com.putTclCmd (ntp ^ " itemconfigure " ^ cid ^ " " ^ Config.pack nip cf);
nwidg)
end
fun addItemBinding widg cid bi =
let
val ip as (win,pt) = Paths.getIntPathGUI (selWidgetWidId widg)
val tp = Paths.getTclPathGUI ip
val nip = (win,pt ^ ".cnv")
val ntp = tp ^ ".cnv"
val cits = selCanvasItems widg
val cit = ListUtil.getx (fn cit => ((selItemId cit) = cid))
cits
(CITEM ("item: " ^ cid ^ " not found"))
val bind = selItemBinding cit
val nbind = Bind.add bind bi
val ncit = updItemBinding cit nbind
val ncits = ListUtil.updateVal (fn cit => ((selItemId cit) = cid))
ncit
cits
val nwidg = updCanvasItems widg ncits
in
(app Com.putTclCmd (Bind.packCanvas ntp nip cid bi);
nwidg)
end
fun getCoords wid cid =
let
val cit = get wid cid
in case cit of
CTag (_, []) => raise CITEM ("CItem.getCoords: CTag(_, [])")
| CTag (_, x::_) => getCoords wid x
| _ => let
val ip = Paths.getIntPathGUI (selWidgetWidId wid)
val tp = Paths.getTclPathGUI ip
val cid' = selItemId cit
val cos = Com.readTclVal (tp^ ".cnv coords "^cid')
in
Coord.read cos
end
end
fun setCoords wid cid cos =
let
fun setCoords' wid (CTag _) cos =
raise CITEM ("CItem.setCoords is not to be used for CTag")
| setCoords' wid cit cos =
let
val ip = Paths.getIntPathGUI (selWidgetWidId wid)
val tp = Paths.getTclPathGUI ip
val cid' = selItemId cit
in
Com.putTclCmd (tp ^ ".cnv coords " ^ cid' ^ " " ^ (Coord.show cos))
end
val cit = get wid cid
in
setCoords' wid cit cos
end
fun getIconWidth (NoIcon) =
0
| getIconWidth (TkBitmap _) =
raise CITEM ("CItem.getIconWidth: don't know how to get width of TkBitmaps")
| getIconWidth (FileBitmap _) =
raise CITEM ("CItem.getIconWidth: don't know how to get width of FileBitmaps")
| getIconWidth (FileImage(f,imid)) =
StringUtil.toInt (Com.readTclVal ("image width " ^ imid))
fun getWidth wid cid =
let
fun min xs = foldl Int.min (hd xs) xs
fun max xs = foldl Int.max (hd xs) xs
fun getWidth' wid (CRectangle _) ((x1,_)::(x2,_)::nil) =
x2-x1
| getWidth' wid (COval _) ((x1,_)::(x2,_)::nil) =
x2-x1
| getWidth' wid (CLine _) (cos as (co::cos')) =
let
val xs = map fst cos
val ma = max xs
val mi = min xs
in
ma-mi
end
| getWidth' wid (CPoly _) (cos as (co::cos')) =
let
val xs = map fst cos
val ma = max xs
val mi = min xs
in
ma-mi
end
| getWidth' wid (CIcon(_,_,ic,_,_)) _ =
getIconWidth ic
| getWidth' wid (CWidget _) _ =
raise CITEM ("CItem.getWidth not yet implemented for CWidget")
| getWidth' wid (CTag _) _ =
raise CITEM ("CItem.getWidth not yet implemented for CTag")
| getWidth' wid _ _ =
raise CITEM ("CItem.getWidth invalid arguments")
val cit = get wid cid
val cos = getCoords wid cid
in
getWidth' wid cit cos
end
fun getIconHeight (NoIcon) =
0
| getIconHeight (TkBitmap _) =
raise CITEM ("CItem.getIconHeight: don't know how to get width of TkBitmaps")
| getIconHeight (FileBitmap _) =
raise CITEM ("CItem.getIconHeight: don't know how to get width of FileBitmaps")
| getIconHeight (FileImage(f,imid)) =
StringUtil.toInt (Com.readTclVal ("image height " ^ imid))
fun getHeight wid cid =
let
fun min xs = foldl Int.min (hd xs) xs
fun max xs = foldl Int.max (hd xs) xs
fun getHeight' wid (CRectangle _) ((_,y1)::(_,y2)::nil) =
y2-y1
| getHeight' wid (COval _) ((_,y1)::(_,y2)::nil) =
y2-y1
| getHeight' wid (CLine _) (cos as (co::cos')) =
let
val ys = map BasicUtil.snd cos
val ma = max ys
val mi = min ys
in
ma-mi
end
| getHeight' wid (CPoly _) (cos as (co::cos')) =
let
val ys = map BasicUtil.snd cos
val ma = max ys
val mi = min ys
in
ma-mi
end
| getHeight' wid (CIcon(_,_,ic,_,_)) _ =
getIconHeight ic
| getHeight' wid (CWidget _) _ =
raise CITEM ("CItem.getHeight not yet implemented for CWidget")
| getHeight' wid (CTag _) _ =
raise CITEM ("CItem.getHeight not yet implemented for CTag")
| getHeight' wid _ _ =
raise CITEM ("CItem.getHeight invalid arguments")
val cit = get wid cid
val cos = getCoords wid cid
in
getHeight' wid cit cos
end
fun move wid cid co =
let
fun move' wid (CTag(cid,cids)) co =
app (fn cid => move wid cid co) cids
| move' wid cit (co as (x,y)) =
let
val ip = Paths.getIntPathGUI (selWidgetWidId wid)
val tp = Paths.getTclPathGUI ip
val cid' = selItemId cit
in
Com.putTclCmd (tp ^ ".cnv move " ^ cid' ^ " " ^ (Coord.show [co]))
end
val cit = get wid cid
in
move' wid cit co
end
val ANOCID_NR = ref(0);
fun newId() = (ignore(inc(ANOCID_NR));"anocid"^Int.toString(!ANOCID_NR));
val ANOFRID_NR = ref(0);
fun newFrId() = (ignore(inc(ANOFRID_NR));"cfr"^Int.toString(!ANOFRID_NR));
end;
end;
