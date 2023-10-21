require "__int";
require "__real";
require "__list";
require "__text_io";
require "basic_util";
require "sys_conf";
require "debug";
require "fonts_sig";
structure Fonts : FONTS_SIG =
struct
open BasicUtil
val Config =
{Normalfont = ref "-*-courier",
Typewriter = ref "-misc-fixed",
SansSerif = ref "-*-helvetica",
Symbol = ref "-*-symbol",
BaseSize = ref 12,
ExactMatch = ref true,
Resolution = ref 75
}
datatype FontConfig =
Bold | Italic |
Tiny | Small | NormalSize | Large | Huge |
Scale of real
val InitConfig =
{ NormalFn' = ref (fn (b:bool,it:bool) => ((!(#Normalfont(Config)) ^ "-*-*-*-*")) ),
TypewriterFn' = ref (fn (b:bool,it:bool) => ((!(#Typewriter(Config)) ^ "-*-*-*-*")) ),
SansSerifFn' = ref (fn (b:bool,it:bool) => ((!(#SansSerif(Config)) ^ "-*-*-*-*")) ),
SymbolFn' = ref (fn (b:bool,it:bool) => ((!(#Symbol(Config)) ^ "-*-*-*-*")) )
}
datatype Font =
XFont of string
| Normalfont of FontConfig list
| Typewriter of FontConfig list
| SansSerif of FontConfig list
| Symbol of FontConfig list
fun selFontConf (Normalfont c) = c
| selFontConf (Typewriter c)= c
| selFontConf (SansSerif c) = c
| selFontConf (Symbol c) = c
| selFontConf (XFont _) = []
fun updFontConf((Normalfont _), c) = Normalfont c
| updFontConf((Typewriter _), c)= Typewriter c
| updFontConf((SansSerif _), c) = SansSerif c
| updFontConf((Symbol _), c) = Symbol c
| updFontConf((XFont str), _) = XFont str
fun isBold Bold = true
| isBold _ = false
fun isItalic Italic = true
| isItalic _ = false
exception NoSize
fun sizeOf Tiny = 10.0 / 14.0
| sizeOf Small = 12.0 / 14.0
| sizeOf NormalSize= 14.0 / 14.0
| sizeOf Large = 18.0 / 14.0
| sizeOf Huge = 24.0 / 14.0
| sizeOf (Scale s) = s
| sizeOf _ = raise NoSize
fun descrFromInitConfig family true true =
family^"-bold-o-*-*"
| descrFromInitConfig family true false =
family^"-bold-r-*-*"
| descrFromInitConfig family false true =
family^"-medium-o-*-*"
| descrFromInitConfig family false false =
family^"-medium-r-*-*"
fun checkFont fntStr = not (FileUtil.exec (SysConf.getFontPath(),[fntStr]))
fun addOneFont fr fam =
let
val fstr = descrFromInitConfig fam
fun addOne b it =
if (checkFont (fstr b it)) then
let
val fr' = !fr
in
fr := (fn (b',it') =>
if ( b = b' andalso it = it' ) then
(fstr b it)
else
(fr')(b',it') )
end
else
Debug.warning("Could not find font \"" ^ (fstr b it) ^
"\"; installing default.")
in
addOne true true;
addOne true false;
addOne false true;
addOne false false
end
val FinalConfig =
{ NormalFn = ref (fn (b,it,p:int) => (
( (!(#NormalFn'(InitConfig))) (b,it) ) ^ "-*-*-*-*-*-*-*-*" )),
TypewriterFn = ref (fn (b,it,p:int) => (
( (!(#TypewriterFn'(InitConfig))) (b,it) ) ^ "-*-*-*-*-*-*-*-*" )),
SansSerifFn = ref (fn (b,it,p:int) => (
( (!(#SansSerifFn'(InitConfig))) (b,it) ) ^ "-*-*-*-*-*-*-*-*" )),
SymbolFn = ref (fn (b,it,p:int) => (
( (!(#SymbolFn'(InitConfig))) (b,it) ) ^ "-*-*-*-*-*-*-*-*" ))
}
fun descrFromFinalConfig fam b it sz =
((!fam) (b,it)) ^ "-" ^ (Int.toString sz) ^ "-*-*-*-*-*-*-*"
fun descrFromFinalConfigTest fam b it sz =
((!fam) (b,it)) ^ "-" ^ (Int.toString sz) ^ "-*"
fun addOneFontSize fr iniFr =
let
val fstr = descrFromFinalConfig (iniFr)
val fstrt = if ( !(#ExactMatch(Config)) ) then
descrFromFinalConfigTest (iniFr)
else
descrFromFinalConfig (iniFr)
fun addDefault fr =
let
val fr' = !fr
in
fr := (fn (b,it,sz) => ((!iniFr)(b,it))^"-*-*-*-*-*-*-*-*" )
end
fun findOne b it sz [] = NONE
| findOne b it sz (x::xl) =
if (checkFont (fstrt b it (sz+x))) then
SOME (fstr b it (sz+x))
else
findOne b it sz xl
fun addOne b it szIn dlst =
let
val sz = (Real.round(Real.* (Real.fromInt(!(#BaseSize(Config))), (sizeOf szIn))))
val str = findOne b it sz dlst
in
case str of
NONE => Debug.warning("Could not find font \"" ^ (fstr b it sz) ^ "\"; installing default.")
| SOME fs =>
let
val fr' = !fr
in
fr := (fn (b',it',sz') =>
if ( b = b' andalso it = it' andalso sz = sz' ) then
(Debug.print 13 ("Found FontSize: "^(fstr b' it' sz')^"\n");
fs )
else
(Debug.print 12 ("Descending FontSize: "^(fstr b' it' sz')^"\n");
(fr')(b',it',sz')) )
end
end
in
addOne true true Tiny [0,~1,1];
addOne true false Tiny [0,~1,1];
addOne false true Tiny [0,~1,1];
addOne false false Tiny [0,~1,1];
addOne true true Small [0,~1,1,~2,2];
addOne true false Small [0,~1,1,~2,2];
addOne false true Small [0,~1,1,~2,2];
addOne false false Small [0,~1,1,~2,2];
addOne true true Large [0,~1,1,~2,2,3];
addOne true false Large [0,~1,1,~2,2,3];
addOne false true Large [0,~1,1,~2,2,3];
addOne false false Large [0,~1,1,~2,2,3];
addOne true true Huge [0,~1,1,~2,2,3,4,5];
addOne true false Huge [0,~1,1,~2,2,3,4,5];
addOne false true Huge [0,~1,1,~2,2,3,4,5];
addOne false false Huge [0,~1,1,~2,2,3,4,5];
addOne true true NormalSize [0,~1,1,~2,2];
addOne true false NormalSize [0,~1,1,~2,2];
addOne false true NormalSize [0,~1,1,~2,2];
addOne false false NormalSize [0,~1,1,~2,2]
end
fun descrFromConfig (family, conf) =
let val wght = (List.exists isBold) conf
val slant= (List.exists isItalic) conf
val size =
let fun sizeFold(c, rest) =
(sizeOf c)
handle NoSize => rest
in
foldr sizeFold 1.000 conf
end
val pxlsz = (Real.round(
Real.* (Real.fromInt(!(#BaseSize(Config))), size)))
val str = (!(family(FinalConfig))) (wght,slant,pxlsz)
in
Debug.print 13 ("descrFromConfig: "^str^"\n");
str
end
fun fontDescr (XFont str) = str
| fontDescr (Normalfont conf)= descrFromConfig(#NormalFn, conf)
| fontDescr (Typewriter conf)= descrFromConfig(#TypewriterFn, conf)
| fontDescr (SansSerif conf) = descrFromConfig(#SansSerifFn, conf)
| fontDescr (Symbol conf) = descrFromConfig(#SymbolFn, conf)
fun init () =
()
end
;
