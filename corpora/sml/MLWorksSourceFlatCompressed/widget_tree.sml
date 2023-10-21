require "__list";
require "__string";
require "__bool";
require "__int";
require "basic_util";
require "debug";
require "fonts";
require "basic_types";
require "com";
require "config";
require "bind";
require "paths";
require "coord";
require "c_item";
require "ann_texts";
require "mark";
require "annotation";
require "widget_tree_sig";
structure WidgetTree : WIDGET_TREE =
struct
local open BasicTypes BasicUtil in
fun checkWidget w =
checkWidId (selWidgetWidId w) andalso
checkWidgetConfigure (selWidgetWidgetType w) (selWidgetConfigure w) andalso
case w
of Menubutton(_, _, ms, _, _, _) => List.all checkMItem ms
| Popup(_, _, ms) => List.all checkMItem ms
| _ => true
and checkWidId s =
if (size(s) = 0) then
false
else
Char.isLower(String.sub(s, 0))
andalso StringUtil.all Char.isAlphaNum s
and checkOneMConfigure MCo (Text _) = true
| checkOneMConfigure MCo (Command c) = true
| checkOneMConfigure MCo _ = false
| checkOneMConfigure _ _ = false
and checkMItem MSeparator = true
| checkMItem (MCheckbutton _) = true
| checkMItem (MRadiobutton _) = true
| checkMItem (MCascade (_,_)) = true
| checkMItem (MCommand cs) =
Config.noDblP cs andalso
List.all (checkOneMConfigure MCo) cs
and checkOneWidgetConfigure _ _ = true
and checkWidgetConfigure wt cs =
Config.noDblP cs andalso
List.all (checkOneWidgetConfigure wt) cs
and checkOneWidgetBinding _ _ = true
and checkWidgetBinding wt bs =
Bind.noDblP bs andalso
List.all ((checkOneWidgetBinding wt) o Bind.selEvent) bs
fun getWidgetGUIPath (win, p) =
let
fun selWid w "" = w
| selWid (w as Listbox(_, _, _, _,_)) p =
if p=".box" then w
else raise WIDGET "Error occurred in function selWid 1"
| selWid (w as Canvas(_, _, cits, _, _, _)) p =
if (p = ".cnv") then
w
else if (ListUtil.prefix (rev (explode".cnv.cfr")) (rev (explode p))) then
raise WIDGET ("WidgetTree.getWidgetGUIPath: \"cfr\" should not appear")
else
let
val _ = Debug.print 2 ("selWid(Canv) "^(selWidgetWidId w)^" "^p)
val (wid,np) = Paths.fstWidPath p
val (wid',np') = Paths.fstWidPath np
val (wid'',np'') = Paths.fstWidPath np'
val _ = Debug.print 2 ("selWid(Canv) "^wid''^" "^np'')
in
selWids (CItem.getCanvasWidgets w) wid'' np''
end
| selWid (w as TextWid(_,_,at,_,_,_)) p =
if p=".txt" then
w
else if (ListUtil.prefix (rev (explode".cnv.tfr")) (rev (explode p))) then
raise WIDGET ("WidgetTree.getWidgetGUIPath: \"tfr\" should not appear")
else
let
val _ = Debug.print 2 ("selWid(Canv) "^(selWidgetWidId w)^" "^p)
val (wid,np) = Paths.fstWidPath p
val (wid',np') = Paths.fstWidPath np
val (wid'',np'') = Paths.fstWidPath np'
val _ = Debug.print 2 ("selWid(Canv) "^wid''^" "^np'')
in
selWids (Annotation.getTextWidWidgets w) wid'' np''
end
| selWid (Frame(_, ws, _, _,_)) p =
let
val (wid, np) = Paths.fstWidPath p
in
selWids ws wid np
end
| selWid _ S = raise WIDGET ("Error occurred in function selWid 3 " ^ S)
and selWids wids w p =
selWid (ListUtil.getx ((fn x=>w=x)o selWidgetWidId) wids
(WIDGET ("selWids with widgetId \"" ^ w ^ "\""))) p
val (w, np) = Paths.fstWidPath p
in
selWids (selWindowWidgets (getWindowGUI win)) w np
end;
fun getWidgetGUI wId = getWidgetGUIPath (Paths.getIntPathGUI wId);
fun addWidgetPathAssGUI win p wid =
if Paths.occursWidgetGUI (selWidgetWidId wid) then
raise WIDGET("Two identical widget names not allowed: "^
(selWidgetWidId wid))
else
let
val np = p ^ ("." ^ (selWidgetWidId wid))
val ass = getPathAssGUI()
val nass = Paths.addWidget (selWidgetWidId wid) win np ass
in
(updPathAssGUI nass;
case wid of
Frame(w, ws, _, _, _) => addWidgetsPathAssGUI win np ws
| Canvas(w,_,cits,_,_,_) =>
let
fun addOne (cit,ws) =
let
val np' = np ^ ".cnv." ^ (CItem.selItemWidId cit)
in
addWidgetsPathAssGUI win np' ws
end
val assl = CItem.getCanvasCItemWidgetAssList wid
in
app addOne assl
end
| TextWid(w,_,at,_,_,_) =>
let
fun addOne (an,ws) =
let
val np' = np ^ ".txt." ^ (Annotation.selAnnotationWidId an)
in
addWidgetsPathAssGUI win np' ws
end
val assl = Annotation.getTextWidAnnotationWidgetAssList wid
in
app addOne assl
end
| _ => ())
end
and addWidgetsPathAssGUI w p wids = app (addWidgetPathAssGUI w p) wids;
fun addWidgetGUI win p wid =
let
fun addWids widgs widg "" =
(Debug.print 2 ("addWids(final)");
widgs @ [widg])
| addWids widgs widg wp =
let
val (wId,nwp) = Paths.fstWidPath wp
val nwidg = ListUtil.getx ((fn x => x=wId) o selWidgetWidId) widgs
(WIDGET ("addWids with widgetId \"" ^ wId ^ "\""))
val newwidg = addWid nwidg widg nwp
in
ListUtil.updateVal ((fn x => x=wId) o selWidgetWidId) newwidg widgs
end
and addWid (Frame(wId',widgs',pack,c,b)) widg wp =
Frame(wId',(addWids widgs' widg wp),pack,c,b)
| addWid (w as (Canvas _)) widg wp =
(Debug.print 2 ("addWid(canv) "^" "^(selWidgetWidId w)^" "^
(selWidgetWidId widg)^" "^wp);
CItem.addCanvasWidget addWids w widg wp)
| addWid (w as (TextWid _)) widg wp =
(Debug.print 2 ("addWid(textw) "^" "^(selWidgetWidId w)^" "^
(selWidgetWidId widg)^" "^wp);
Annotation.addTextWidWidget addWids w widg wp)
| addWid _ _ _ =
raise WIDGET "addWidgetGUI: attempt to add widget to non-container widget";
in
if (checkWidget wid) then
let
val window = getWindowGUI win
val newwindow = (win, selWindowConfigures window,
addWids (selWindowWidgets window) wid p,
selWindowAction window)
val _ = Debug.print 2 ("addWidgetGUI: done")
in
(addWidgetPathAssGUI win p wid;
updWindowGUI win newwindow)
end
else
raise WIDGET("Definition of widget "^((selWidgetWidId wid)^" is not OK"))
end
and addWidgetsGUI w p wids = app (addWidgetGUI w p) wids;
fun deleteWidgetGUI wId =
let
fun deleteWidgetPathAss ((widg as Frame(wId,widgs,_,_,_)),ass) =
let
val nass = deleteWidgetsPathAss (widgs,ass)
in
Paths.deleteWidget wId nass
end
| deleteWidgetPathAss ((widg as Canvas(wId,_,_,_,_,_)),ass) =
let
val widgs = CItem.getCanvasWidgets widg
val nass = deleteWidgetsPathAss (widgs,ass)
in
Paths.deleteWidget wId nass
end
| deleteWidgetPathAss ((widg as TextWid(wId,_,_,_,_,_)),ass) =
let
val widgs = Annotation.getTextWidWidgets widg
val nass = deleteWidgetsPathAss (widgs,ass)
in
Paths.deleteWidget wId nass
end
| deleteWidgetPathAss (widg,ass) =
Paths.deleteWidget (selWidgetWidId widg) ass
and deleteWidgetsPathAss (widgs,ass) =
foldr deleteWidgetPathAss ass widgs
fun delWid (Frame(wid,ws,pack,c,b)) w p =
Frame(wid,(delWids ws w p),pack,c,b)
| delWid (widg as (Canvas _)) w p =
CItem.deleteCanvasWidget delWids widg w p
| delWid (widg as (TextWid _)) w p =
Annotation.deleteTextWidWidget delWids widg w p
| delWid _ _ _ =
raise WIDGET "Error occurred in function delWid"
and delWids wids w "" =
List.filter ((fn x=> not (w = x))o selWidgetWidId) wids
| delWids wids w p =
let
val _ = Debug.print 2 ("delWids(Canv) "^w^" "^p)
val wid = ListUtil.getx ((fn x=>w=x)o selWidgetWidId) wids
(WIDGET ("delWids with widgetId \"" ^ w ^ "\""))
val (nw, np) = Paths.fstWidPath p
val newwid = delWid wid nw np
in
ListUtil.updateVal ((fn x=>w=x)o selWidgetWidId) newwid wids
end
val _ = Debug.print 2 ("deleteWidgetGUI "^wId)
val widg = getWidgetGUI wId;
val (ip as (win, p)) = Paths.getIntPathGUI wId;
val ass = getPathAssGUI()
val nass = deleteWidgetPathAss (widg,ass)
val _ = Debug.print 2 ("deleteWidgetGUI(after nass) "^wId)
val (nw, np) = Paths.fstWidPath p
val window = getWindowGUI win
val newwindow = (win, selWindowConfigures window,
delWids (selWindowWidgets window) nw np,
selWindowAction window)
in
updWindowGUI win newwindow;
updPathAssGUI nass
end;
fun deleteWidgetGUIPath ip = deleteWidgetGUI(selWidgetWidId(getWidgetGUIPath ip));
fun updWidgetGUIPath (win, p) w =
let
val _ = Debug.print 2 ("updWidgetGUIPath "^win^" "^p^" "^(selWidgetWidId w))
fun updWids wids w "" neww =
ListUtil.updateVal ((fn x=>w=x)o selWidgetWidId) neww wids
| updWids wids w p neww =
let
val _ = Debug.print 2 ("updWids "^w^" "^p);
val wid = ListUtil.getx ((fn x=>w=x)o selWidgetWidId) wids
(WIDGET ("updWids with widgetId " ^ w))
val (nw, np) = Paths.fstWidPath p
val newwid = updWid wid nw np neww
in
ListUtil.updateVal ((fn x=>w=x)o selWidgetWidId) newwid wids
end
and updWid (Frame(wid, ws, pack, c, b)) w p neww =
Frame(wid, (updWids ws w p neww), pack, c, b)
| updWid (widg as (Canvas _)) w p neww =
(Debug.print 2 ("updWid(Canv) "^(selWidgetWidId widg)^" "^w^" "^p);
CItem.updCanvasWidget updWids widg w p neww)
| updWid (widg as (TextWid _)) w p neww =
(Debug.print 2 ("updWid(TextWid) "^(selWidgetWidId widg)^" "^w^" "^p);
Annotation.updTextWidWidget updWids widg w p neww)
| updWid _ _ _ _ =
raise WIDGET "Error occurred in function updWid";
val (nw, np) = Paths.fstWidPath p
val window = getWindowGUI win
val newwindow = (win, selWindowConfigures window,
updWids (selWindowWidgets window) nw np w,
selWindowAction window)
in
updWindowGUI win newwindow
end;
fun updWidgetGUI w = updWidgetGUIPath (Paths.getIntPathGUI (selWidgetWidId w)) w;
fun packWidgets doP tp ip ws = app (packWidget doP tp ip) ws
and packWidget doP tp (win, p) w =
let
val wid = selWidgetWidId w;
val nip = (win, p ^ "." ^ wid);
val ntp = tp ^ "." ^ wid
in
case w of
Frame (_,wids,p,c,b) => (packWid doP "frame" ntp nip wid p c b;
packWidgets true ntp nip wids)
| Message (_, p, c, b) => packWid doP "message" ntp nip wid p c b
| Listbox (_,sc, p, c, b) => packListbox doP ntp nip wid sc p c b
| Label (_, p, c, b) => packWid doP "label" ntp nip wid p c b
| Button(_, p, c, b) => packWid doP "button" ntp nip wid p c b
| Radiobutton(_, p, c, b) => packWid doP "radiobutton" ntp nip wid p c b
| Checkbutton(_, p, c, b) => packWid doP "checkbutton" ntp nip wid p c b
| Menubutton(_,to,ms,p,c,b) => packMenu doP ntp nip wid to ms p c b
| TextWid (_,sc,at,p,c,b) => packTextWid doP ntp nip wid sc
(AnnotatedText.selText at)
(AnnotatedText.selAnno at) p c b
| Canvas (_,sc,ci,p,c,b) => packCanvas doP ntp nip wid sc ci p c b
| Popup (_,to,ms) => packPopup doP ntp nip wid to ms
| Entry (_, p, c, b) => packWid doP "entry" ntp nip wid p c b
end
and packWid0 doP s tp ip w pack conf confstr binds =
if ( doP ) then
(Com.putTclCmd ("pack [" ^ s ^ " " ^ tp ^ " " ^
(Config.pack ip conf) ^ confstr ^ "] " ^ (Config.packInfo pack) );
app Com.putTclCmd (Bind.packWidget tp ip binds) )
else
(Com.putTclCmd ( s ^ " " ^ tp ^ " " ^ (Config.pack ip conf) ^ confstr);
app Com.putTclCmd (Bind.packWidget tp ip binds) )
and packWid doP s tp ip w pack conf binds = packWid0 doP s tp ip w pack conf "" binds
and packMenu doP tp (ip as (win, p)) w to ms pack conf binds =
let
val mip = (win, p ^ ".m");
val mtp = tp ^ ".m";
in
(if ( doP ) then
(Com.putTclCmd ("pack [menubutton " ^ tp ^ " " ^
Config.pack ip conf ^ " -menu " ^ mtp ^ "] " ^
Config.packInfo pack);
app Com.putTclCmd (Bind.packWidget tp ip binds) )
else
(Com.putTclCmd ("menubutton " ^ tp ^ " " ^
Config.pack ip conf ^ " -menu " ^ mtp);
app Com.putTclCmd (Bind.packWidget tp ip binds));
Com.putTclCmd ("menu " ^ mtp ^ " -tearoff " ^ (Bool.toString to)) ;
packMenuItems mtp mip w ms [] )
end
and packPopup doP tp (ip as (win, p)) w to ms =
let
val mip = (win, p ^ ".pop");
val mtp = tp ^ ".pop";
in
Com.putTclCmd ("menu " ^ tp ^ " -tearoff " ^ (Bool.toString to)) ;
packMenuItems tp ip w ms []
end
and packMenuItems tp ip wid mis m_item_path=
let
fun pmi tp ip w [] n = ()
| pmi tp ip w (m::ms) n = (packMenuItem tp ip w m (n :: m_item_path);
pmi tp ip w ms (n+1))
in
pmi tp ip wid mis 0
end
and packMenuItem tp ip w (MSeparator) n =
Com.putTclCmd (tp ^ " add separator")
| packMenuItem tp ip w (MCheckbutton (cs)) n =
Com.putTclCmd (tp ^ " add checkbutton " ^ Config.packM ip (rev n) cs)
| packMenuItem tp ip w (MRadiobutton (cs)) n =
Com.putTclCmd (tp ^ " add radiobutton "^ Config.packM ip (rev n) cs)
| packMenuItem tp ip w (MCascade (ms, cs)) [] =
raise WIDGET ("WidgetTree.packMenuItem illegal arguments")
| packMenuItem tp (ip as (win, p)) w (MCascade (ms,cs)) (n::S) =
let
val ntp = tp ^ ".m" ^ Int.toString(n);
val n2 = rev(n::S)
in
(Com.putTclCmd (tp ^ " add cascade "^ Config.packM ip n2 cs ^ " -menu "^ntp);
Com.putTclCmd ("menu " ^ ntp);
packMenuItems ntp ip w ms (n::S))
end
| packMenuItem tp ip w (MCommand cs) n =
Com.putTclCmd (tp ^ " add command " ^ Config.packM ip (rev n) cs)
and packListbox doP tp (ip as (win, pt)) wid NoneScb p c b =
let
val bip = (win, pt ^ ".box");
val btp = tp ^ ".box";
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "listbox" btp bip wid [Fill Both, Expand true] c b)
end
| packListbox doP tp (ip as (win, pt)) wid C p c b =
let
val bip = (win, pt ^ ".box");
val btp = tp ^ ".box";
val scip = (win, pt ^ ".scr");
val sctp = tp ^ ".scr";
val si = Side(scrollTypeToEdge C);
val siquer= Side(scrollTypeToOppEdge C)
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "listbox" btp bip wid [siquer,Fill Both, Expand true] c b;
packWid true "scrollbar" sctp scip wid [si,Fill Y] [] [];
Com.putTclCmd (btp^" configure -yscrollcommand \""^ sctp ^ " set \" ");
Com.putTclCmd (sctp^" configure -command \""^ btp ^ " yview\""))
end
and packCanvas doP tp (ip as (win, pt)) wid NoneScb ci p c b =
let
val cip = (win, pt ^ ".cnv");
val ctp = tp ^ ".cnv";
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "canvas" ctp cip wid [Fill Both, Expand true] c b;
app (CItem.pack packWidget ctp cip) ci )
end
| packCanvas doP tp (ip as (win, pt)) wid C ci p c b =
let
val cip = (win, pt ^ ".cnv");
val ctp = tp ^ ".cnv";
val scip = (win, pt ^ ".scr");
val sctp = tp ^ ".scr";
val si = Side(scrollTypeToEdge C);
val siquer= Side(scrollTypeToOppEdge C)
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "canvas" ctp cip wid [siquer,Fill Both, Expand true] c b;
packWid true "scrollbar" sctp scip wid [si,Fill Y] [] [];
Com.putTclCmd (ctp^" configure -yscrollcommand \""^ sctp ^ " set \" ");
Com.putTclCmd (sctp^" configure -command \""^ ctp ^ " yview\"");
app (CItem.pack packWidget ctp cip) ci )
end
and packTextWid doP tp (ip as (win, pt)) wid NoneScb t ans p c b =
let
val fdef = Font (Fonts.Normalfont [Fonts.NormalSize])
val bip = (win, pt ^ ".txt");
val btp = tp ^ ".txt";
val nc = List.filter (not o (Config.confEq (TextWidReadOnly false))) c
val sc = List.filter (Config.confEq (TextWidReadOnly false)) c
val tt = btp^" insert end \""^(StringUtil.adaptString t)^"\""
val stt = btp^" configure "^(Config.pack bip sc)
val nc' = if (List.exists (Config.confEq fdef) nc) then
nc
else
fdef::nc
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "text" btp bip wid [Fill Both,Expand true] nc' b;
Com.putTclCmd tt;
Com.putTclCmd stt;
app (Annotation.pack packWidget btp bip) ans )
end
| packTextWid doP tp (ip as (win, pt)) wid C t ans p c b =
let
val fdef = Font (Fonts.Normalfont [Fonts.NormalSize])
val bip = (win, pt ^ ".txt");
val btp = tp ^ ".txt";
val scip = (win, pt ^ ".scr");
val sctp = tp ^ ".scr";
val si = Side(scrollTypeToEdge C);
val siquer= Side(scrollTypeToOppEdge C)
val nc = List.filter (not o (Config.confEq (TextWidReadOnly false))) c
val sc = List.filter (Config.confEq (TextWidReadOnly false)) c
val tt = btp^" insert end \""^(StringUtil.adaptString t)^"\""
val stt = btp^" configure "^(Config.pack bip sc)
val nc' = if (List.exists (Config.confEq fdef) nc) then
nc
else
fdef::nc
in
(packWid doP "frame" tp ip wid p [] [];
packWid true "text" btp bip wid [siquer,Fill Both,Expand true] nc' b;
packWid true "scrollbar" sctp scip wid [si,Fill Y] [] [];
Com.putTclCmd (btp^" configure -yscrollcommand \""^ sctp ^ " set \" ");
Com.putTclCmd (sctp^" configure -command \""^ btp ^ " yview\"");
Com.putTclCmd tt;
Com.putTclCmd stt;
app (Annotation.pack packWidget btp bip) ans )
end;
val selectWidget = getWidgetGUI;
val selectWidgetPath = getWidgetGUIPath;
fun deleteWidget wid =
(Debug.print 2 ("deleteWidget "^wid);
Com.putTclCmd("destroy " ^ (Paths.getTclPathGUI(Paths.getIntPathGUI wid)));
deleteWidgetGUI wid);
fun addWidget winId widId widg =
let
val widPath = Paths.getWidPathGUI widId
in
addWidgetGUI winId widPath widg;
let
val wId = selWidgetWidId widg;
val (win,wp) = Paths.getIntPathGUI wId;
val (nwp,l) = Paths.lastWidPath wp;
val nip = (win,nwp)
val ntclp = Paths.getTclPathGUI nip;
val nwidg = getWidgetGUI wId;
in
Debug.print 2 ("addWidget: " ^ntclp^" ("^win^","^nwp^") "^wId);
packWidget true ntclp nip nwidg
end
end
val select = selWidgetConfigure o getWidgetGUI;
val selectCommand = Config.selCommand o getWidgetGUI;
val selectCommandPath = Config.selCommand o getWidgetGUIPath;
fun selectMCommandPath ip n =
let
val w = getWidgetGUIPath ip
fun sel_cascade ms [] = raise WIDGET ("WidgetTree.selectMCommandPath illegal arguments")
| sel_cascade ms [n] = List.nth(ms,n)
| sel_cascade ms (n::m::S) =
case List.nth(ms,n) of
MCascade (mms,_) => sel_cascade mms (m::S)
| _ => raise WIDGET ("WidgetTree.selectMCommandPath illegal arguments")
in
case w of Menubutton(_,_,ms,_,_,_) => Config.selMCommand (sel_cascade ms n)
| Popup(_,_,ms) => Config.selMCommand (sel_cascade ms n)
| _ => fn () => ()
end;
fun selectMCommandMPath (win, mp) n =
let
val (p, m) = Paths.lastWidPath mp
in
if ( m = "m" ) then
selectMCommandPath (win, p) n
else
selectMCommandPath (win,mp) n
end;
fun selectMCommand wId n = selectMCommandPath (Paths.getIntPathGUI wId) n;
val selectBindings = selWidgetBinding o getWidgetGUI;
fun selectBindKey wId name = Bind.getActionByName name
(selWidgetBinding(getWidgetGUI wId))
fun selectBindKeyPath ip name = Bind.getActionByName name
(selWidgetBinding(getWidgetGUIPath ip))
val selectWidth = Config.selWidth o getWidgetGUI
val selectHeight = Config.selHeight o getWidgetGUI
val selectRelief = Config.selRelief o getWidgetGUI
fun configure w cs =
let
val ip = Paths.getIntPathGUI w;
val wid = getWidgetGUIPath ip;
val tp = Paths.getTclPathGUI ip
val ntp =
case wid of
TextWid _ => tp^".txt"
| Canvas _ => tp^".cnv"
| _ => tp
in
if checkWidgetConfigure (selWidgetWidgetType wid) cs then
let
val oldcs = selWidgetConfigure wid;
val newcs = Config.add oldcs cs;
val newwid = updWidgetConfigure wid newcs;
in
(updWidgetGUIPath ip newwid;
Com.putTclCmd (ntp ^ " configure " ^ Config.pack ip cs))
end
else
raise CONFIG "Trying to reconfigure with wrong type of configures"
end;
fun newconfigure w cs =
let
val ip = Paths.getIntPathGUI w;
val wid = getWidgetGUIPath ip;
val wt = selWidgetWidgetType wid
val tp = Paths.getTclPathGUI ip
val ntp =
case wid of
TextWid _ => tp^".txt"
| Canvas _ => tp^".cnv"
| _ => tp
in
if checkWidgetConfigure wt cs then
let val oldcs = selWidgetConfigure wid;
val newcs = Config.new wt oldcs cs;
val newwid = updWidgetConfigure wid newcs
in
(updWidgetGUIPath ip newwid;
Com.putTclCmd (ntp ^ " configure " ^ Config.pack ip newcs))
end
else
raise CONFIG "Trying to reconfigure with wrong type of configures"
end;
fun configureCommand w c = configure w [Command c];
fun configureWidth w n = configure w [Width n];
fun configureRelief w r = configure w [Relief r];
fun configureText w t = configure w [Text t];
fun addBindings w bs =
let
val ip = Paths.getIntPathGUI w
val wid = getWidgetGUIPath ip
val tp = Paths.getTclPathGUI ip
val ntp =
case wid of
TextWid _ => tp^".txt"
| Canvas _ => tp^".cnv"
| _ => tp
in
if checkWidgetBinding (selWidgetWidgetType wid) bs then
let
val oldbs = selWidgetBinding wid;
val newbs = Bind.add oldbs bs;
val newwid = updWidgetBinding wid newbs
in
(updWidgetGUIPath ip newwid;
app Com.putTclCmd (Bind.packWidget ntp ip bs))
end
else
raise CONFIG "Trying to add wrong bindings"
end;
fun newBindings w bs =
let
val ip = Paths.getIntPathGUI w;
val wid = getWidgetGUIPath ip;
val wt = selWidgetWidgetType wid
val tp = Paths.getTclPathGUI ip
val ntp =
case wid of
TextWid _ => tp^".txt"
| Canvas _ => tp^".cnv"
| _ => tp
in
if checkWidgetBinding wt bs then
let
val oldbs = selWidgetBinding wid;
val oldks = Bind.delete oldbs bs;
val newwid = updWidgetBinding wid bs
in
(updWidgetGUIPath ip newwid;
app Com.putTclCmd (Bind.unpackWidget ntp wt oldks);
app Com.putTclCmd (Bind.packWidget ntp ip bs))
end
else
raise CONFIG "Trying to newly set wrong bindings"
end;
fun insertText wid str m =
let
val tp = Paths.getWidPathGUI wid;
val ip = Paths.getIntPathGUI wid;
val w = getWidgetGUIPath ip;
val (m1,_)= StringUtil.breakAtDot (Mark.show m)
in
case w of
TextWid _ =>
Com.putTclCmd((Paths.getTclPathGUI ip) ^
".txt insert " ^ Mark.show(m) ^ " \""
^ StringUtil.adaptString str ^"\"")
| Listbox _ =>
Com.putTclCmd((Paths.getTclPathGUI ip) ^
".box insert " ^ m1 ^
" \"" ^ StringUtil.adaptString str ^"\" ")
| Entry _ =>
Com.putTclCmd((Paths.getTclPathGUI ip) ^ " insert " ^ m1 ^
" \"" ^ StringUtil.adaptString str ^"\" ")
| _ =>
raise WIDGET "text insertion in illegal window"
end
fun insertTextEnd wid str = insertText wid str MarkEnd
fun deleteText wid (from,to) =
let
val tp = Paths.getWidPathGUI wid
val ip = Paths.getIntPathGUI wid
val w = getWidgetGUIPath ip
val (m1,_)= StringUtil.breakAtDot (Mark.show from)
val (m2,_)= StringUtil.breakAtDot (Mark.show to)
in
case w of
TextWid _ =>
Com.putTclCmd((Paths.getTclPathGUI ip) ^ ".txt delete " ^
Mark.show(from) ^ " " ^ Mark.show(to))
| Listbox _ =>
Com.putTclCmd((Paths.getTclPathGUI ip)^".box delete "^m1^" "^m2)
| Entry _ =>
Com.putTclCmd((Paths.getTclPathGUI ip)^" delete "^m1^" "^m2)
| _ =>
raise WIDGET "text deletion in illegal window"
end
fun clearText wid = deleteText wid (Mark(0,0),MarkEnd)
fun focus win =
if ( win = "main" orelse win = "." ) then
Com.putTclCmd ("focus .")
else
Com.putTclCmd ("focus ."^win)
fun deFocus _ =
Com.putTclCmd ("focus .")
fun grab win =
if ( win = "main" orelse win = "." ) then
Com.putTclCmd ("grab set .")
else
Com.putTclCmd ("grab set ."^win)
fun deGrab win =
if ( win = "main" orelse win = "." ) then
Com.putTclCmd ("grab release .")
else
Com.putTclCmd ("grab release ."^win)
fun popUpMenu wid index co =
let
val tp = Paths.getTclPathGUI(Paths.getIntPathGUI wid)
val cot = Coord.show [co]
fun popItUp (Menubutton _) (SOME i) =
Com.putTclCmd ("tk_popup "^tp^".m "^cot^" "^(Int.toString (i:int)))
| popItUp (Menubutton _) (NONE) =
Com.putTclCmd ("tk_popup "^tp^".m "^cot)
| popItUp (Popup _ ) (SOME i) =
Com.putTclCmd ("tk_popup "^tp^" "^cot^" "^(Int.toString (i:int)))
| popItUp (Popup _ ) (NONE) =
Com.putTclCmd ("tk_popup "^tp^" "^cot)
| popItUp _ _ =
raise WIDGET "WidgetTree.popUpMenu: tried to pop up non-MenuWidget"
val widg = getWidgetGUI wid
in
popItUp widg index
end
end
end
;
