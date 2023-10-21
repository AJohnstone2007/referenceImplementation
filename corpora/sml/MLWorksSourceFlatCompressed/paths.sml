require "__substring";
require "__int";
require "__list";
require "basic_util";
require "basic_types";
require "paths_sig";
structure Paths : PATHS =
struct
open BasicTypes BasicUtil
fun fstWidPath s =
let val (m1, m2) = Substring.splitl (not o StringUtil.isDot)
(Substring.triml 1 (Substring.all s))
in (Substring.string m1, Substring.string m2)
end
fun lastWidPath p =
let val (rp, rw) = Substring.splitr (not o StringUtil.isDot)
(Substring.all p)
in
if (Substring.size rp)= 0 then
("", Substring.string rw)
else
(Substring.string (Substring.trimr 1 rp), Substring.string rw)
end
fun occursWindowGUI w =
List.exists (eq w) (map selWindowWinId (BasicTypes.getWindowsGUI()))
fun occursWidgetGUI w =
List.exists (eq w) (map fst (BasicTypes.getPathAssGUI()))
fun addWidget wid winid path ass = ass @ [(wid,(winid,path))]
fun deleteWidget w ass =
List.filter ( (fn x => not (x= w)) o fst) ass;
fun deleteWidgetPath (wi,wp) ass =
List.filter ( (fn (x,y) =>not (x=wi andalso y=wp)) o snd) ass;
fun deleteWindow w ass = List.filter ((fn x => not (x= w)) o fst o snd) ass;
fun getTclPathGUI(w,p) = if isInitWin w then p else ("." ^ w ^ p);
fun gip w ((x, y)::ass) = if w = x then y else gip w ass
| gip w _ = raise WIDGET ("Error in function gip: WidId " ^ w
^ " undeclared.");
fun getIntPathGUI w = gip w (BasicTypes.getPathAssGUI());
fun getWidPathGUI wid = snd(getIntPathGUI(wid));
fun getIntPathFromTclPathGUI tp =
let
val (front,r) = lastWidPath tp;
val (front2,r2) = lastWidPath front;
val wid = if (r="txt") andalso (occursWidgetGUI r2) then r2 else
if (r="cnv") andalso (occursWidgetGUI r2) then r2 else
if (r="box") andalso (occursWidgetGUI r2) then r2 else r
in
(fst(getIntPathGUI wid), wid)
end;
val ANOWID_NR = ref(0)
fun newWidgetId() = (ignore(inc(ANOWID_NR));
"anowid"^Int.toString(!ANOWID_NR))
end
;
