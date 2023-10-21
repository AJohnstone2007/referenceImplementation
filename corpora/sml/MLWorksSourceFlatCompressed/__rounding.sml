require "rounding";
require "$.basis.__ieee_real";
require "$.basis.__string_cvt";
require "$.basis.__text_io";
require "$.basis.__real";
require "$.basis.__io";
structure Rounding : ROUNDING =
struct
val columnWidth = 18
fun skipComments strm =
case (TextIO.lookahead strm) of
SOME #"#" => (ignore (TextIO.inputLine strm);
skipComments strm)
| _ => strm
fun inputReals strm =
case TextIO.scanStream Real.scan strm of
NONE => []
| SOME x => x :: inputReals strm
fun getReals (file, decimals) =
let
fun toRealText x = Real.fmt (StringCvt.FIX decimals) x
val strm = skipComments (TextIO.openIn file)
val realText = map toRealText (inputReals strm)
val _ = TextIO.closeIn strm
in
realText
end
fun printTableLine (col1, col2, col3, col4) =
TextIO.print (StringCvt.padLeft #" " columnWidth col1 ^ " " ^
StringCvt.padLeft #" " columnWidth col2 ^ " " ^
StringCvt.padLeft #" " columnWidth col3 ^ " " ^
StringCvt.padLeft #" " columnWidth col4 ^ "\n")
fun printReals ([], [], [], []) = TextIO.print ""
| printReals ((h1::t1), (h2::t2), (h3::t3), (h4::t4)) =
(printTableLine (h1, h2, h3, h4);
printReals (t1, t2, t3, t4))
| printReals _ = raise Fail "File altered during input."
fun roundingTable (file, decimals) =
let
val toNearest =
(IEEEReal.setRoundingMode IEEEReal.TO_NEAREST;
getReals (file, decimals))
val toNeginf =
(IEEEReal.setRoundingMode IEEEReal.TO_NEGINF;
getReals (file, decimals))
val toPosinf =
(IEEEReal.setRoundingMode IEEEReal.TO_POSINF;
getReals (file, decimals))
val toZero =
(IEEEReal.setRoundingMode IEEEReal.TO_ZERO;
getReals (file, decimals))
in
(printTableLine ("To nearest:", "To -inf:", "To +inf:", "To zero:");
printReals (toNearest, toNeginf, toPosinf, toZero))
end
fun roundingDemo (file, decimals) =
roundingTable (file, decimals)
handle IO.Io _ => TextIO.print ("Error reading file.\n")
| Fail message => TextIO.print message
end
;
