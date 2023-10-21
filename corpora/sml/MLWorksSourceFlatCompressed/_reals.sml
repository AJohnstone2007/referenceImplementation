require "../utils/crash";
require "../main/machspec";
require "reals";
functor Reals(
structure Crash : CRASH
structure MachSpec : MACHSPEC
) : REALS =
struct
exception too_small
exception too_big
structure Bits = MLWorks.Internal.Bits
fun convert_nybble n =
let val blist = MLWorks.Internal.Array.arrayoflist
["0000",
"0001",
"0010",
"0011",
"0100",
"0101",
"0110",
"0111",
"1000",
"1001",
"1010",
"1011",
"1100",
"1101",
"1110",
"1111"]
in
MLWorks.Internal.Array.sub(blist,n)
end
fun convert_byte b
= convert_nybble (Bits.rshift (b,4)) ^ convert_nybble (Bits.andb(b,15))
fun make_n_list (n:int,a:char) =
let fun aux (0,r) = r
| aux (n,r) = aux(n-1,a::r)
in
aux(n,[])
end
fun pad (n,string,c:char) =
let val len = size string
in
if len > n then
substring (string,0,n)
else if len = n then
string
else
string ^ implode (make_n_list (n - len, c))
end
val zero_exp = ~1023
fun extract_double_exponent (exp1::exp2::rest) =
((Bits.andb(exp1,127) * 16) + (Bits.rshift(exp2,4))) - 1023
| extract_double_exponent _ = Crash.impossible"extract_double_exponent"
fun extract_double_mantissa (exp1::exp2::rest) =
(convert_nybble (Bits.andb(exp2,15))) :: (map convert_byte rest)
| extract_double_mantissa _ = Crash.impossible"extract_double_mantissa"
fun find_real_components x =
let
val str = MLWorks.Internal.Value.real_to_string x
val bytes = map ord (explode str)
val (exp1, exp2, rest) = case bytes of
(exp1 :: exp2 :: rest) => (exp1, exp2, rest)
| _ => Crash.impossible"find_real_components gets bad value from MLWorks.Internal.Value.real_to_string"
val sign = not (Bits.andb(exp1,128) = 0)
val exponent = extract_double_exponent bytes
val mant1 = concat ("1" :: extract_double_mantissa bytes)
in
(sign, pad(MachSpec.digits_in_real,mant1,#"0"), exponent)
end
val evaluate_real = MLWorks.Internal.string_to_real
end
;
