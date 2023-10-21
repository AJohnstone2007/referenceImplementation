signature BIGNUM =
sig
type bignum
exception Unrepresentable
val int_to_bignum : int -> bignum
val bignum_to_int : bignum -> int
val string_to_bignum : string -> bignum
val hex_string_to_bignum : string -> bignum
val word_string_to_bignum : string -> bignum
val hex_word_string_to_bignum : string -> bignum
val bignum_to_string : bignum -> string
exception Runtime of string
val ~ : bignum -> bignum
val abs : bignum -> bignum
val + : bignum * bignum -> bignum
val - : bignum * bignum -> bignum
val * : bignum * bignum -> bignum
val div : bignum * bignum -> bignum
val mod : bignum * bignum -> bignum
val quot : bignum * bignum -> bignum
val rem : bignum * bignum -> bignum
val < : bignum * bignum -> bool
val > : bignum * bignum -> bool
val <= : bignum * bignum -> bool
val >= : bignum * bignum -> bool
val eq : bignum * bignum -> bool
val <> : bignum * bignum -> bool
end;
