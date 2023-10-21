require "monoset";
signature MUTABLEMONOSET =
sig
include MONOSET
val add' : T * element -> T
val remove' : T * element -> T
val intersection' : T * T -> T
val union' : T * T -> T
val difference' : T * T -> T
val filter' : (element -> bool) -> T -> T
val copy' : T -> T
end
;
