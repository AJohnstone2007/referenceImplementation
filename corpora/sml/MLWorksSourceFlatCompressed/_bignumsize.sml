require "crash";
require "bignum";
require "_bignum";
functor BigNumSize
(structure Crash : CRASH
structure BigNum : BIGNUM
val bits_per_word : int
) : BIGNUM =
struct
fun power(acc, base, index) =
if index <= 0 then acc else power(BigNum.*(acc, base), base, index-1)
val bignum_one = BigNum.int_to_bignum 1
val intpower = power(bignum_one, BigNum.int_to_bignum 2, bits_per_word-1)
val largest_int =
BigNum.bignum_to_string(BigNum.-(intpower, bignum_one))
val smallest_int = BigNum.bignum_to_string(BigNum.~ intpower)
val largest_word =
BigNum.bignum_to_string
(BigNum.-(BigNum.*(intpower, BigNum.int_to_bignum 2), bignum_one))
structure BigNumSize = BigNumFun
(structure Crash = Crash
val check_range = true
val smallest_int = smallest_int
val largest_int = largest_int
val largest_word = largest_word)
open BigNumSize
end
;
