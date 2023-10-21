require "_bignumsize";
require "__crash";
require "__bignum_inf";
require "^.machine.__machspec";
structure BigNum_ =
BigNumSize(structure Crash = Crash_
structure BigNum = BigNum_Inf_
val check_range = true
val bits_per_word = MachSpec_.bits_per_word);
structure BigNum32_ =
BigNumSize(structure Crash = Crash_
structure BigNum = BigNum_Inf_
val check_range = true
val bits_per_word = 32);
structure BigNum64_ =
BigNumSize(structure Crash = Crash_
structure BigNum = BigNum_Inf_
val check_range = true
val bits_per_word = 64);
