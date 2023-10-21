require "__string";
require "__string_cvt";
require "__list";
require "__math";
require "__pre_ieee_real";
require "__pre_int";
require "__pre_int32";
require "__pre_ieee_real";
structure PreReal =
struct
structure Bits = MLWorks.Internal.Bits
structure IEEEReal = PreIEEEReal
structure Math = Math
type real = real
fun crash s = raise Fail ("Library Error: " ^ s)
val equal : real * real -> bool = MLWorks.Internal.Value.real_equal
val isNan : real -> bool = fn x => not (equal(x,x))
fun unordered (x,y) = isNan x orelse isNan y
val == : real * real -> bool = equal
val != : real * real -> bool = not o ==
val op <> : real * real -> bool =
fn (x,y) => x < y orelse y < x
val ?= : real * real -> bool =
not o op <>
infix ?= == !=
val radix = 2
val precision = 53
val maxexp = 2047
val fromRep:string -> real = MLWorks.Internal.Value.string_to_real
val toRep: real -> string = MLWorks.Internal.Value.real_to_string
val minNormalPos = fromRep "\000\016\000\000\000\000\000\000";
val minPos = fromRep "\000\000\000\000\000\000\000\001";
val maxFinite = fromRep "\127\239\255\255\255\255\255\255";
val neg_default_nan = fromRep "\255\255\255\255\255\255\255\255"
val pos_default_nan = fromRep "\127\255\255\255\255\255\255\255"
val posInf = 1.0 / 0.0
val negInf = ~posInf
val *+ = fn (x:real,y,z) => x * y + z
val *- = fn (x:real,y,z) => x * y - z
val min = fn (x:real,y:real) => if x < y then x else y
val max = fn (x:real,y:real) => if x > y then x else y
fun sign x = if x < 0.0 then ~1 else
if x == 0.0 then 0 else
if x > 0.0 then 1 else raise Domain
fun signBit x =
let
val s = toRep x
in
ord(String.sub(s,0)) > 127
end
fun sameSign (r1, r2) = sign r1 = sign r2
fun copySign (x,y) =
let
val signbit = signBit y
val s = toRep x
val size = size s
val first = ord(String.sub(s,0))
val rest = substring (s,1,size - 1)
val newfirst = if signbit then Bits.orb (128,first)
else Bits.andb (127,first)
in
fromRep ((str o chr) newfirst ^ rest)
end
fun compare (x:real,y:real) =
if x < y then LESS
else if x == y then EQUAL
else if x > y then GREATER
else raise IEEEReal.Unordered
fun compareReal (x:real,y:real) =
if x < y then IEEEReal.LESS
else if x == y then IEEEReal.EQUAL
else if x > y then IEEEReal.GREATER
else IEEEReal.UNORDERED
fun exponent x =
let
val s = toRep x
val b0 = Bits.andb (ord(String.sub(s,0)),127)
val b1 = Bits.rshift (ord(String.sub(s,1)),4)
in
16 * b0 + b1
end
fun info x =
let
val s = toRep x
val (a,b,rest) =
case map ord (explode s) of
(a::b::rest) => (a,b,rest)
| _ => (0,0,[])
val b0 = Bits.andb (a,127)
val b1 = Bits.rshift (b,4)
val sign = a > 127
val mantissa = Bits.andb (15,b) :: rest
val exponent = 16 * b0 + b1
in
(sign,exponent,mantissa)
end
fun make (sign,exponent,b :: mantissa) =
let
val b1 = Bits.orb (if sign then 128 else 0,
exponent div 16)
val b2 = Bits.orb (Bits.lshift (exponent mod 16,4),
b)
in
fromRep (implode (MLWorks.Internal.Value.cast (b1 :: b2 :: mantissa) : char list))
end
| make _= 0.0
fun isFinite x = exponent x < maxexp
fun isInfinite x = not (isFinite x)
fun isNormal x =
let
val e = exponent x
in
e < maxexp andalso e > 0
end
fun class x =
let
fun is_zero [] = true
| is_zero (0::b) =is_zero b
| is_zero _ = false
val (s,e,m) = info x
in
if e = maxexp
then if is_zero m
then IEEEReal.INF
else IEEEReal.NAN
else if e = 0
then if is_zero m
then IEEEReal.ZERO
else IEEEReal.SUBNORMAL
else IEEEReal.NORMAL
end
fun ldiv (from,to) =
let
fun div' (c,[],acc) = (c,rev acc)
| div' (c,d::rest,acc) =
let
val d = from * c + d
val d' = d div to
val c' = d mod to
val acc' =
case acc of
[] => if d' = 0 then [] else [d']
| _ => d'::acc
in
div' (c',rest,acc')
end
fun ldiv l = div'(0,l,[])
in
ldiv
end
fun convert_base (from,to) l =
let
fun f ([],acc) = acc
| f (l,acc) =
let
val (c,l) = ldiv (from,to) l
in
f (l,c::acc)
end
in
f (l,[])
end
fun pad (l,n) =
let
val len = length l
fun loop (0,l) =l
| loop (n,l) = loop (n-1,0::l)
in
if len < n
then
loop (n-len,l)
else l
end
val dec_to_byte = convert_base (10,256)
val byte_to_dec = convert_base (256,10)
fun make_nan (true,[]) = neg_default_nan
| make_nan (false,[]) = pos_default_nan
| make_nan (sign,digits) =
let
val bytes = dec_to_byte digits
in
if List.length bytes > 7
then raise Overflow
else
case pad (bytes,7) of
b1::rest =>
if b1 > 15 then raise Overflow
else
fromRep
(implode (map chr
((if sign then 255 else 127) ::
240 + b1 :: rest)))
| _ => crash "make_nan"
end
fun get_nan_digits x =
case map ord (explode (toRep x)) of
(b0 :: b1::rest) =>
let
val bytes = b1-240 :: rest
in
(b0 >= 128, byte_to_dec bytes)
end
| _ => crash "print_nan"
fun toChar x = chr (x + ord #"0")
fun exact_fmt_nan x =
let
val (sign,digits) = get_nan_digits x
in
(if sign then "~" else "") ^ "nan"
end
val cfmt : StringCvt.realfmt * real -> string =
MLWorks.Internal.Runtime.environment "real fmt"
fun fmt f x =
if f = StringCvt.EXACT andalso isNan x
then exact_fmt_nan (x)
else cfmt (f,x)
val toString = fmt (StringCvt.GEN NONE)
fun internalFromString x = SOME (MLWorks.Internal.string_to_real x)
handle MLWorks.Internal.StringToReal => NONE
fun scan getc orig_src =
case IEEEReal.scan getc orig_src of
NONE => NONE
| SOME (da,src) =>
let val kind = IEEEReal.class da
val sign = IEEEReal.signBit da
in
case kind of
IEEEReal.ZERO => SOME (if sign then ~(0.0) else 0.0,src)
| IEEEReal.INF => SOME (if sign then negInf else posInf,src)
| IEEEReal.NAN => SOME (make_nan (sign,IEEEReal.digits da),src)
| _ =>
let
val string = implode ((if sign then [#"~"] else []) @
[#"0", #"."] @
map toChar (IEEEReal.digits da) @
[#"E"] @
explode (PreInt.toString (IEEEReal.exp da)))
in
case internalFromString (string) of
SOME x => SOME (x,src)
| _ => raise Overflow
end
end
val fromString : string -> real option =
StringCvt.scanString scan
val toManExp : real -> {man : real, exp : int} =
MLWorks.Internal.Runtime.environment "real from exp"
val fromManExp : {man : real, exp : int} -> real =
MLWorks.Internal.Runtime.environment "real load exp"
val split : real -> {whole : real, frac : real} =
MLWorks.Internal.Runtime.environment "real split"
val realMod = #frac o split
val maxman = ([15,255,255,255,255,255,255])
val minman = ([ 0, 0, 0, 0, 0, 0, 0])
fun sub1 l =
let
fun aux [] = []
| aux (0::rest) = 255 :: aux rest
| aux (n::rest) = (n-1) :: rest
in
rev (aux (rev l))
end
fun add1 l =
let
fun aux [] = []
| aux (255::rest) = 0 :: aux rest
| aux (n::rest) = (n+1) :: rest
in
rev (aux (rev l))
end
fun do_dec x =
let
val (s,e,m) = info x
in
if e = 0
then if m = minman
then
make (true,0,add1 minman)
else
make (s,e,sub1 m)
else
if m = minman
then make (s,e-1,maxman)
else make (s,e,sub1 m)
end
fun do_inc x =
let
val (s,e,m) = info x
in
if m = maxman
then make (s,e+1,minman)
else make (s,e,add1 m)
end
fun nextAfter (r,t) =
if not(isFinite r) then r
else if r == t then r
else if r < t then if r<0.0 then do_dec r else do_inc r
else if r<0.0 then do_inc r else do_dec r
fun checkFloat x =
if isNan x then raise Div
else if isFinite x then x
else raise Overflow
fun realFloor r =
let
val {whole,frac} = split r
in
if whole < 0.0 andalso frac != 0.0 then whole - 1.0 else whole
end
fun realCeil r =
let
val {whole,frac} = split r
in
if whole > 0.0 andalso frac != 0.0 then whole + 1.0 else whole
end
fun realTrunc r =
let
val {whole,frac} = split r
in
whole
end
val floor = fn x => if isNan x then raise Domain else floor x
val ceil = fn x => if isNan x then raise Domain else ceil x
val trunc = fn x => if isNan x then raise Domain else trunc x
val round = fn x => if isNan x then raise Domain else round x
fun toInt mode x =
case mode of
IEEEReal.TO_NEAREST => round x
| IEEEReal.TO_NEGINF => floor x
| IEEEReal.TO_POSINF => ceil x
| IEEEReal.TO_ZERO => trunc x
val fromInt : PreInt.int -> real = real
fun rem (x,y) = x - (fromInt (trunc (x / y)) * y)
val cToLargeInt: IEEEReal.rounding_mode * real -> PreInt32.int =
MLWorks.Internal.Runtime.environment "real to large int"
fun toLargeInt mode x = cToLargeInt (mode,x)
val fromLargeInt : PreInt32.int -> real =
MLWorks.Internal.Runtime.environment "real from large int"
fun toLarge x = x
fun fromLarge y x = x
exception Decimal
val rawToDecimal : real -> string * int * bool =
MLWorks.Internal.Runtime.environment"real decimal rep"
fun get_digits s =
map (fn c => ord c - ord #"0") (explode s)
fun toDecimal (x: real) : IEEEReal.decimal_approx =
let
val kind = class x
val (rep,exp,sign) = rawToDecimal x
in
case kind of
IEEEReal.INF => IEEEReal.DEC_APPROX{kind=kind,sign=sign,digits=[],exp=0}
| IEEEReal.ZERO => IEEEReal.DEC_APPROX{kind=kind,sign=sign,digits=[],exp=0}
| IEEEReal.NAN => IEEEReal.DEC_APPROX{kind=kind, sign=sign, digits = #2(get_nan_digits x),exp=0}
| _ => IEEEReal.DEC_APPROX{kind=kind,sign=sign,digits=get_digits rep,exp=exp}
end
fun fromDecimal rep =
let val kind = IEEEReal.class rep
val sign = IEEEReal.signBit rep
in case kind of
IEEEReal.ZERO => if sign then ~(0.0) else 0.0
| _ =>
(case (fromString (IEEEReal.toString rep)) of
NONE => crash "fromDecimal"
| SOME x => x)
end
val ~ : real -> real = ~
val op + : real * real -> real = op +
val op - : real * real -> real = op -
val op * : real * real -> real = op *
val op / : real * real -> real = op /
val abs : real -> real = abs
val op > : real * real -> bool = op >
val op >= : real * real -> bool = op >=
val op < : real * real -> bool = op <
val op <= : real * real -> bool = op <=
end
structure PreLargeReal = PreReal
;
