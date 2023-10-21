require "__pre_basis";
require "__pre_char";
require "__pre_string_cvt";
require "__int";
require "__list";
structure PreIEEEReal =
struct
fun crash s = raise Fail ("Library Error: " ^ s)
exception Unordered
datatype real_order = LESS | EQUAL | GREATER | UNORDERED
datatype float_class =
NAN |
INF |
ZERO |
NORMAL |
SUBNORMAL
datatype rounding_mode = TO_NEAREST | TO_NEGINF | TO_POSINF | TO_ZERO
val setRoundingMode : rounding_mode -> unit =
MLWorks.Internal.Runtime.environment "real set rounding mode"
val getRoundingMode : unit -> rounding_mode =
MLWorks.Internal.Runtime.environment "real get rounding mode"
abstype decimal_approx =
DE of { kind : float_class, sign : bool, digits : int list, exp : int }
with
fun class (DE {kind, ...}) = kind
fun signBit (DE {sign, ...}) = sign
fun digits (DE {digits, ...}) = digits
fun exp (DE {exp, ...}) = exp
val DEC_APPROX = DE
end;
fun digit_to_char d = chr (d + ord #"0")
fun char_to_digit c = ord c - ord #"0"
fun toString (da : decimal_approx) =
case class(da) of
NAN => "nan"
| INF => if signBit(da) then "-inf" else "inf"
| ZERO => "0.0"
| _ =>
implode ((if signBit(da) then [#"~"] else []) @
[#"0",#"."] @
map digit_to_char (digits da) @
(if (exp da) <> 0
then #"E" :: explode (Int.toString (exp da))
else []))
fun stripWS (c::rest) =
if PreBasis.isSpace c then stripWS rest
else c :: rest
| stripWS [] = []
exception Fail
val inf_chars = explode "inf"
val infinity_chars = explode "infinity"
val nan_chars = explode "nan"
exception Fail
fun scan getc src =
let
fun trychars [] (getc,src) = SOME src
| trychars (c::rest) (getc,src) =
(case getc src of
SOME (c',src) =>
if PreChar.toLower c' = c
then trychars rest (getc,src)
else NONE
| _ => NONE)
val get_inf = trychars inf_chars
val get_infinity = trychars infinity_chars
val get_nan = trychars nan_chars
fun getdigits src =
let
fun get (src,acc) =
case getc src of
SOME (c,src') =>
if PreChar.isDigit c then get (src',char_to_digit c::acc)
else (rev acc,src)
| NONE => (rev acc,src)
in
get (src,[])
end
fun getpointnum orig_src =
case getc orig_src of
SOME (#".",src) =>
let
val (digs,src) = getdigits src
in
case digs of
[] => ([],orig_src,false)
| _ => (digs,src,true)
end
| _ => ([],orig_src,true)
fun getsign src =
case getc src of
SOME (#"+",src') => (false,src')
| SOME (#"-",src') => (true,src')
| SOME (#"~",src') => (true,src')
| _ => (false,src)
fun getenum orig_src =
case getc orig_src of
SOME (char,src) =>
if char = #"e" orelse char = #"E"
then
let
fun makenum (a::b,acc) =
makenum (b,a+10*acc)
| makenum (_,acc) = acc
val (sign,src) = getsign src
val (digs,src) = getdigits src
val signfactor = if sign then ~1 else 1
in
if digs = []
then (0,orig_src)
else (signfactor * makenum (digs,0),src)
end
else (0,orig_src)
| _ => (0,orig_src)
fun strip_zero (0::rest) = strip_zero rest
| strip_zero rest = rest
val src = PreStringCvt.skipWS getc src
val (sign,src) = getsign src
fun get_special src =
case get_infinity (getc,src) of
SOME src => SOME (DEC_APPROX{kind=INF,digits=[],sign=sign,exp=0},src)
| _ =>
(case get_inf (getc,src) of
SOME src => SOME (DEC_APPROX{kind=INF,digits=[],sign=sign,exp=0},src)
| _ =>
(case get_nan (getc,src) of
SOME src =>
SOME (DEC_APPROX{kind=NAN,sign=sign,digits=[],exp=0},src)
| _ => NONE))
in
case get_special src of
SOME (x,src) => SOME (x,src)
| _ =>
let
val (digits1,src) = getdigits src
val (digits2,src,continue) = getpointnum src
in
if digits1 = [] andalso digits2 = []
then NONE
else
let
val (exp,src) = if continue then getenum src else (0,src)
fun strip (0 ::rest) = strip rest
| strip chars = chars
val strip_end = rev o strip o rev
fun balance (0::digits,exp) =
balance (digits,exp-1)
| balance (digits,exp) =
(strip_end digits,exp)
val (digits,exp) = balance (digits1 @ digits2,
exp + List.length digits1)
in
case digits of
[] => SOME (DEC_APPROX{kind=ZERO,sign=sign,digits=[],exp=0},src)
| _ => SOME (DEC_APPROX{kind=NORMAL,sign=sign,digits=digits,exp = exp},src)
end
end
end
val fromString : string -> decimal_approx option =
PreStringCvt.scanString scan
end
;
