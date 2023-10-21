require "ident.sml";
require "^.basis.__string";
require "^.utils.bignum";
require "scons";
functor Scons(
structure Ident : IDENT
structure BigNum : BIGNUM) : SCONS =
struct
type SCon = Ident.SCon
fun is_hex_int (s:string):bool =
String.isPrefix "0x" s orelse String.isPrefix "~0x" s
fun is_hex_word (s:string):bool = String.isPrefix "0wx" s
fun sign (s:string) = String.sub(s,0) = #"~"
fun strip_zeroes(s, i) =
if i >= size s then ""
else if String.sub(s, i) = #"0" then
strip_zeroes (s, i+1)
else
String.extract (s, i, NONE)
fun int_is_zero s =
let
val ptr = if sign s then 1 else 0
in
strip_zeroes(s, if is_hex_int s then 2+ptr else ptr) = ""
end
fun word_is_zero s =
let
val ptr = if sign s then 1 else 0
in
strip_zeroes(s, if is_hex_word s then 3+ptr else ptr) = ""
end
fun scon_eqval(Ident.INT(s, _), Ident.INT(t, _)) =
s = t orelse
(int_is_zero s andalso int_is_zero t)
orelse
((sign s = sign t) andalso
let
val s_is_hex = is_hex_int s
val t_is_hex = is_hex_int t
in
if s_is_hex = t_is_hex then
let
val ptr = if sign s then 1 else 0
val ptr = if s_is_hex then ptr+2 else ptr
in
strip_zeroes(s, ptr) = strip_zeroes(t, ptr)
end
else
(if s_is_hex then
BigNum.eq(BigNum.hex_string_to_bignum s, BigNum.string_to_bignum t)
else
BigNum.eq(BigNum.hex_string_to_bignum t, BigNum.string_to_bignum s))
handle BigNum.Unrepresentable => false
end)
| scon_eqval(Ident.WORD(s, _), Ident.WORD(t, _)) =
s = t orelse
(word_is_zero s andalso word_is_zero t)
orelse
((sign s = sign t) andalso
let
val s_is_hex = is_hex_word s
val t_is_hex = is_hex_word t
in
if s_is_hex = t_is_hex then
let
val ptr = if sign s then 1 else 0
val ptr = if s_is_hex then ptr+3 else ptr
in
strip_zeroes(s, ptr) = strip_zeroes(t, ptr)
end
else
(if s_is_hex then
BigNum.eq(BigNum.hex_word_string_to_bignum s,
BigNum.word_string_to_bignum t)
else
BigNum.eq(BigNum.hex_word_string_to_bignum t,
BigNum.word_string_to_bignum s))
handle BigNum.Unrepresentable => false
end)
| scon_eqval (Ident.REAL(s, _), Ident.REAL(t, _)) = s = t
| scon_eqval (Ident.STRING s, Ident.STRING t) = s = t
| scon_eqval (Ident.CHAR s, Ident.CHAR t) = s = t
| scon_eqval (_, _) = false
end
;
