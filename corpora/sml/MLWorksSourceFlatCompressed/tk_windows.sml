require "__string";
require "__list";
require "__text_io";
require "basic_util";
require "basic_types";
require "com";
require "config";
require "paths";
require "widget_tree";
require "tk_windows_sig";
structure Window : WINDOW =
struct
open BasicUtil BasicTypes
fun checkWinId "" = false
| checkWinId s =
(Char.isLower(String.sub(s, 0))) andalso (StringUtil.all Char.isAlphaNum s)
val checkTitle = StringUtil.all Char.isPrint
fun check (win as (w, wcnfgs, wids,_)) =
let
val mbt = Config.selWinTitle win
val bb = checkWinId w
in
case mbt of
NONE => bb
| SOME t => checkTitle t andalso bb
end;
fun appendGUI w = updWindowsGUI(getWindowsGUI() @ [w]);
fun addGUI (w as (winId,wcnfgs,widgs,act)) =
if check w then
if Paths.occursWindowGUI (selWindowWinId w) then
raise WINDOWS ("Two identical window names not allowed: " ^
(selWindowWinId w))
else
let
val tmpWin = (winId,wcnfgs,[],act)
in
(appendGUI tmpWin;
WidgetTree.addWidgetsGUI winId "" widgs)
end
else
raise WINDOWS ("Definition of window " ^ selWindowWinId w ^ " is not OK");
fun deleteGUI w =
let
val wins = getWindowsGUI()
val ass = getPathAssGUI()
val anws = getTclAnswersGUI()
val nwins = List.filter ((fn x => not (w=x)) o selWindowWinId) wins
val nass = Paths.deleteWindow w ass
in
updGUI(nwins,nass,anws)
end;
val deleteAllGUI = updGUI([], [], []);
fun openW (w as (win, wconfigs, widgets, init_action)) =
(addGUI w;
if isInitWin win then
(app (fn wcnfg => Com.putTclCmd (Config.packWinConf "." wcnfg)) wconfigs;
WidgetTree.packWidgets true "" (win, "") widgets)
else
(Com.putTclCmd ("toplevel ." ^ win);
app (fn wcnfg => Com.putTclCmd (Config.packWinConf ("."^win) wcnfg)) wconfigs;
WidgetTree.packWidgets true ("."^win) (win, "") widgets);
init_action();
TextIO.output (TextIO.stdOut, "tried opening wish window ... did it display?\n");
TextIO.flushOut (TextIO.stdOut));
fun close win =
if isInitWin win then
(Com.exitTcl();
deleteAllGUI)
else
(Com.putTclCmd ("destroy ." ^ win);
deleteGUI win);
fun changeTitle winid title =
let
val win = getWindowGUI winid
val wc = selWindowConfigures win
val wc' = Config.addWinConf wc [WinTitle title]
val win' = updWindowConfigures win wc'
in
if checkTitle title then
(updWindowGUI winid win';
if isInitWin winid then
Com.putTclCmd (Config.packWinConf "." (WinTitle title))
else
Com.putTclCmd (Config.packWinConf ("."^winid) (WinTitle title)))
else
raise WINDOWS ("Title " ^ title ^ " for window " ^ winid ^ " is not OK")
end;
end;
