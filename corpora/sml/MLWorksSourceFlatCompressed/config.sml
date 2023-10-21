require "__int";
require "__list";
require "basic_util";
require "fonts";
require "basic_types";
require "config_sig";
require "com";
structure Config : CONFIG =
struct
local open BasicUtil in
local open BasicTypes in
infix 1 confElem;
fun showExpand t = if t then "1" else "0";
fun showStyle X = "x"
|showStyle Y = "y"
|showStyle Both = "both";
fun showEdge Top = "top"
|showEdge Bottom = "bottom"
|showEdge Left = "left"
|showEdge Right = "right";
fun packOneInfo (Expand b) = " -expand " ^ showExpand b
|packOneInfo (Fill s) = " -fill " ^ showStyle s
|packOneInfo (PadX n) = " -padx " ^ (Int.toString n)
|packOneInfo (PadY n) = " -pady " ^ (Int.toString n)
|packOneInfo (Side e) = " -side " ^ showEdge e;
fun packInfo ps = concat (map packOneInfo ps) ;
fun confEq (Width _ ) (Width _ ) = true
|confEq (Height _ ) (Height _ ) = true
|confEq (Borderwidth _ ) (Borderwidth _ ) = true
|confEq (Relief _ ) (Relief _ ) = true
|confEq (Foreground _ ) (Foreground _ ) = true
|confEq (Background _ ) (Background _ ) = true
|confEq (Text _ ) (Text _ ) = true
|confEq (Font _ ) (Font _ ) = true
|confEq (Variable _ ) (Variable _ ) = true
|confEq (Value _ ) (Value _ ) = true
|confEq (Icon _ ) (Icon _ ) = true
|confEq (Cursor _ ) (Cursor _ ) = true
|confEq (Command _ ) (Command _ ) = true
|confEq (Anchor _ ) (Anchor _ ) = true
|confEq (TextWidReadOnly _) (TextWidReadOnly _) = true
|confEq (FillColor _ ) (FillColor _ ) = true
|confEq (Outline _ ) (Outline _ ) = true
|confEq (OutlineWidth _) (OutlineWidth _) = true
|confEq (Stipple ) (Stipple ) = true
|confEq (Smooth _ ) (Smooth _ ) = true
|confEq (Arrow _ ) (Arrow _ ) = true
|confEq (Capstyle _ ) (Capstyle _ ) = true
|confEq (Joinstyle _ ) (Joinstyle _ ) = true
|confEq _ _ = false;
fun confElemH (c,[]) = false
| confElemH (c,(x::xs))= confEq c x orelse confElemH(c,xs);
val op confElem = confElemH;
fun noDblP [] = true
|noDblP (x::xs) = not (x confElem xs) andalso noDblP xs;
fun defaultWidth _ = 0;
fun defaultHeight _ = 0;
fun defaultBorderwidth _ = 0;
fun defaultRelief _ = Flat;
fun defaultForeground _ = Black;
fun defaultBackgound _ = Grey;
fun defaultText _ = "";
fun defaultFont _ = (Fonts.Normalfont [])
fun defaultVariable _ = "BLA";
fun defaultValue _ = "0";
fun defaultIcon _ = NoIcon;
fun defaultCursor _ = NoCursor;
fun defaultCommand _ = fn () => ();
fun defaultAnchor _ = Center;
fun defaultTextWidState _ = false;
fun defaultFillColor _ = White;
fun defaultOutline _ = Black;
fun defaultOutlineWidth _ = 1;
fun defaultSmooth _ = false;
fun scw [] wt = defaultWidth wt
|scw ((Width (n))::xs) _ = n
|scw (x::xs) wt = scw xs wt
fun selWidth w = scw (selWidgetConfigure w) (selWidgetWidgetType w)
fun selMWidth m = scw (selMItemConfigure m) (selMItemWidgetType m)
fun sch [] wt = defaultHeight wt
| sch ((Height h)::_) _ = h
| sch (_::xs) w = sch xs w
fun selHeight w= sch (selWidgetConfigure w) (selWidgetWidgetType w)
fun scr [] wt = defaultRelief wt
|scr ((Relief r)::xs) _ = r
|scr (x::xs) wt = scr xs wt;
fun selRelief w = scr (selWidgetConfigure w) (selWidgetWidgetType w);
fun selMRelief w = scr (selMItemConfigure w) (selMItemWidgetType w);
fun sct [] wt = defaultText wt
|sct ((Text t)::xs) _ = t
|sct (x::xs) wt = sct xs wt;
fun selText w = sct (selWidgetConfigure w) (selWidgetWidgetType w);
fun selMText w = sct (selMItemConfigure w) (selMItemWidgetType w);
fun scc [] wt = defaultCommand wt
|scc ((Command c)::xs) _ = c
|scc (x::xs) wt = scc xs wt;
fun selCommand w = scc (selWidgetConfigure w) (selWidgetWidgetType w);
fun selMCommand w = scc (selMItemConfigure w) (selMItemWidgetType w);
fun addOneConf [] c = [c]
| addOneConf (x::xs) c =
if confEq x c then c :: xs else x :: addOneConf xs c
fun add old new = List.concat (map (addOneConf old) new)
fun newOneConf cs c = List.filter (not o(confEq c)) cs
fun defaultConf wt (Width _) = Width (defaultWidth wt)
|defaultConf wt (Height _) = Height (defaultHeight wt)
|defaultConf wt (Borderwidth _) = Borderwidth (defaultBorderwidth wt)
|defaultConf wt (Relief _) = Relief (defaultRelief wt)
|defaultConf wt (Text _) = Text (defaultText wt)
|defaultConf wt (Font _) = Font (defaultFont wt)
|defaultConf wt (Variable _) = Variable (defaultVariable wt)
|defaultConf wt (Value _) = Value (defaultValue wt)
|defaultConf wt (Icon _) = Icon (defaultIcon wt)
|defaultConf wt (Cursor _) = Cursor (defaultCursor wt)
|defaultConf wt (Command _) = Command (defaultCommand wt)
|defaultConf wt (FillColor _) = FillColor (defaultFillColor wt)
|defaultConf wt (Outline _) = Outline (defaultOutline wt)
|defaultConf wt (OutlineWidth _)= OutlineWidth (defaultOutlineWidth wt)
|defaultConf wt (Smooth _) = Smooth (defaultSmooth wt)
|defaultConf wt _ =
raise CONFIG ("Config.defaultConf: not yet fully implemented")
fun new wt old nw =
let val defold = foldl (twist (uncurry (newOneConf))) old nw
in nw @ map (defaultConf wt) defold end;
fun showRel Flat = "flat"
|showRel Groove = "groove"
|showRel Raised = "raised"
|showRel Ridge = "ridge"
|showRel Sunken = "sunken";
fun showCol NoColor= "{}"
|showCol Black = "black"
|showCol White = "white"
|showCol Grey = "grey"
|showCol Blue = "blue"
|showCol Green = "green"
|showCol Red = "red"
|showCol Brown = "brown"
|showCol Yellow = "yellow";
fun showAnchorKind North = "n"
| showAnchorKind NorthEast = "ne"
| showAnchorKind East = "e"
| showAnchorKind SouthEast = "se"
| showAnchorKind South = "s"
| showAnchorKind SouthWest = "sw"
| showAnchorKind West = "w"
| showAnchorKind NorthWest = "nw"
| showAnchorKind Center = "center"
;
fun showTextWidState false = "normal"
| showTextWidState true = "disabled"
;
fun showIconKind (NoIcon) =
"-bitmap {}"
| showIconKind (TkBitmap (s)) =
"-bitmap \"" ^ s ^ "\""
| showIconKind (FileBitmap (s)) =
"-bitmap \"@" ^ s ^ "\""
| showIconKind (FileImage(f,imId)) =
"-image [image create photo " ^ imId ^ " -file " ^ f ^ "]"
;
fun showCursorKind (NoCursor) =
"{}"
| showCursorKind (XCursor(cn,NONE)) =
cn
| showCursorKind (XCursor(cn,SOME(fg,NONE))) =
cn ^ " " ^ (showCol fg)
| showCursorKind (XCursor(cn,SOME(fg,SOME(bg)))) =
cn ^ " " ^ (showCol fg)^ " " ^ (showCol bg)
| showCursorKind (FileCursor(cf,fg,NONE)) =
"{@" ^ cf ^ " " ^ (showCol fg) ^ "}"
| showCursorKind (FileCursor(cf,fg,SOME(mf,bg))) =
"{@" ^ cf ^ " " ^ mf ^ " " ^ (showCol fg) ^ " " ^ (showCol bg) ^ "}"
fun showConf _ (Width n) = " -width " ^ (Int.toString n)
|showConf _ (Height n) = " -height " ^ (Int.toString n)
|showConf _ (Borderwidth n) = " -borderwidth " ^ (Int.toString n)
|showConf _ (Relief r) = " -relief " ^ (showRel r)
|showConf _ (Foreground r) = " -foreground " ^ (showCol r)
|showConf _ (Background r) = " -background " ^ (showCol r)
|showConf _ (Text t) = " -text \"" ^ StringUtil.adaptString t ^ "\""
|showConf _ (Font r) = " -font " ^ (Fonts.fontDescr r)
|showConf _ (Variable r) = " -variable " ^ r
|showConf _ (Value r) = " -value " ^ r
|showConf _ (Icon ick) = (showIconKind ick)
|showConf _ (Cursor ck) = " -cursor " ^ (showCursorKind ck)
|showConf (w, p) (Command c) =
" -command {" ^ Com.commToTcl ^"  \"Command " ^ w ^ " " ^ p ^ "\"}"
|showConf _ (Anchor a) = " -anchor " ^ (showAnchorKind a)
|showConf _ (TextWidReadOnly s) = " -state " ^ (showTextWidState s)
|showConf _ (FillColor r) = " -fill " ^ (showCol r)
|showConf _ (Outline r) = " -outline " ^ (showCol r)
|showConf _ (OutlineWidth n) = " -width " ^ (Int.toString n)
|showConf _ (Smooth true) = " -smooth true"
|showConf _ (Smooth false) = ""
|showConf _ _ =
raise CONFIG ("Config.showConf: not yet fully implemented")
fun pack p cs = concat (map (showConf p) cs);
fun packCascPath [m:int] = Int.toString m
|packCascPath (m::n::S) = (Int.toString m) ^"."^packCascPath (n::S)
|packCascPath _ =
raise CONFIG ("Config.packCascPath: match exhausted")
fun readCascPath str =
let fun rc strS =
let val (m1,m2) = (StringUtil.breakAtDot) strS
in if m2 = "" then [StringUtil.toInt m1]
else (StringUtil.toInt m1)::(rc m2)
end
in rc str
end
fun showMConf _ _ (Text t) = " -label \"" ^ StringUtil.adaptString t ^ "\""
|showMConf _ _ (Variable r)= " -variable " ^ r
|showMConf _ _ (Value r) = " -value " ^ r
|showMConf (w, p) m (Command c) =
" -command {" ^ Com.commToTcl ^" \"MCommand " ^ w ^ " " ^ p ^ " "
^ (packCascPath m) ^ "\"}"
|showMConf _ _ _ =
raise CONFIG ("Config.showMConf: got wrong Config")
fun packM p m cs = concat (map (showMConf p m) cs);
fun winConfEq (WinAspect (_,_,_,_)) (WinAspect (_,_,_,_)) = true
| winConfEq (WinGeometry (_,_)) (WinGeometry (_,_)) = true
| winConfEq (WinMaxSize (_,_)) (WinMaxSize (_,_)) = true
| winConfEq (WinMinSize (_,_)) (WinMinSize (_,_)) = true
| winConfEq (WinPositionFrom _ ) (WinPositionFrom _ ) = true
| winConfEq (WinSizeFrom _ ) (WinSizeFrom _ ) = true
| winConfEq (WinTitle _ ) (WinTitle _ ) = true
| winConfEq (WinGroup _ ) (WinGroup _ ) = true
| winConfEq (WinTransient _ ) (WinTransient _ ) = true
| winConfEq (WinOverride _ ) (WinOverride _ ) = true
| winConfEq _ _ = false;
fun addOneWinConf [] c = [c]
| addOneWinConf (x::xs) c =
if winConfEq x c then c :: xs else x :: addOneWinConf xs c;
fun addWinConf old new = List.concat (map (addOneWinConf old) new);
fun accMaybe f wcnfgs =
let val mbs = map f wcnfgs
in List.foldl (fn (_ , SOME x)=> SOME x | (x, NONE)=> x) NONE mbs
end
fun sAsp (WinAspect(c as (_,_,_,_))) = SOME c
|sAsp _ = NONE
fun selWinAspect w = accMaybe sAsp (selWindowConfigures w)
fun sGeom (WinGeometry(c as (_,_))) = SOME c
|sGeom _ = NONE
fun selWinGeometry w = accMaybe sGeom (selWindowConfigures w)
fun sMaxSize (WinMaxSize(c as _)) = SOME c
|sMaxSize _ = NONE
fun selWinMaxSize w = accMaybe sMaxSize (selWindowConfigures w)
fun sMinSize (WinMinSize(c as _)) = SOME c
|sMinSize _ = NONE
fun selWinMinSize w = accMaybe sMinSize (selWindowConfigures w)
fun sPositionFrom (WinPositionFrom i) = SOME i
|sPositionFrom _ = NONE
fun selWinPositionFrom w = accMaybe sPositionFrom (selWindowConfigures w)
fun sSizeFrom (WinSizeFrom i) = SOME i
|sSizeFrom _ = NONE
fun selWinSizeFrom w = accMaybe sSizeFrom (selWindowConfigures w)
fun sTitle (WinTitle i) = SOME i
|sTitle _ = NONE
fun selWinTitle w = accMaybe sTitle (selWindowConfigures w)
fun sGroup (WinGroup gl) = SOME gl
| sGroup _ = NONE
fun selWinGroup w = accMaybe sGroup (selWindowConfigures w)
fun sTransient (WinTransient i) = SOME i
| sTransient _ = NONE
fun selWinTransient w = accMaybe sTransient (selWindowConfigures w)
fun sOver (WinOverride b) = SOME b
| sOver _ = NONE
fun selWinOverride w = accMaybe sOver (selWindowConfigures w)
fun showPos i =
if ( i >= 0 ) then ("+" ^ (Int.toString i))
else ("-" ^ (Int.toString (i * ~1)))
fun packWinConf win (WinAspect (x1,y1,x2,y2)) =
"wm aspect " ^ win ^ " " ^ (Int.toString x1) ^ " " ^ (Int.toString y1)
^ " " ^ (Int.toString x2) ^ " " ^ (Int.toString y2)
| packWinConf win (WinGeometry (NONE,NONE)) = ""
| packWinConf win (WinGeometry (NONE,SOME(x,y))) =
"wm geometry " ^ win ^ " =" ^ (showPos x) ^ (showPos y)
| packWinConf win (WinGeometry (SOME(w,h),NONE)) =
"wm geometry " ^ win ^ " ="^ (Int.toString w) ^ "x" ^ (Int.toString h)
| packWinConf win (WinGeometry (SOME(w,h),SOME(x,y))) =
"wm geometry " ^ win ^ " ="
^ (Int.toString w) ^ "x" ^ (Int.toString h) ^ (showPos x) ^ (showPos y)
| packWinConf win (WinMaxSize(w,h)) =
"wm maxsize " ^ win ^ " " ^ (Int.toString w) ^ " " ^ (Int.toString h)
| packWinConf win (WinMinSize(w,h)) =
"wm minsize " ^ win ^ " " ^ (Int.toString w) ^ " " ^ (Int.toString h)
| packWinConf win (WinPositionFrom User) =
"wm positionfrom " ^ win ^ " user"
| packWinConf win (WinPositionFrom Program) =
"wm positionfrom " ^ win ^ " program"
| packWinConf win (WinSizeFrom User) =
"wm sizefrom " ^ win ^ " user"
| packWinConf win (WinSizeFrom Program) =
"wm sizefrom " ^ win ^ " program"
| packWinConf win (WinTitle t) =
"wm title " ^ win ^ " \"" ^ (StringUtil.adaptString t) ^ "\""
| packWinConf win (WinGroup gl) =
if isInitWin gl then
"wm group " ^ win ^ " ."
else
"wm group " ^ win ^ " ." ^ gl
| packWinConf win (WinTransient NONE) =
"wm transient " ^ win
| packWinConf win (WinTransient (SOME w)) =
if isInitWin w then
"wm transient " ^ win ^ " ."
else
"wm transient " ^ win ^ " ." ^ w
| packWinConf win (WinOverride true) =
"wm overrideredirect " ^ win ^ " true"
| packWinConf win (WinOverride false) =
"wm overrideredirect " ^ win ^ " false"
end; end;
end;
