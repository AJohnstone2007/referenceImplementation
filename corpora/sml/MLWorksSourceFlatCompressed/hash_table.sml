signature HASH_TABLE =
sig
exception Full
type 'a hash_table
eqtype key
val create : int * 'a -> 'a hash_table
val insert : 'a hash_table * key * 'a -> unit
val remove : 'a hash_table * key -> 'a option
val lookUp : 'a hash_table * key -> 'a option
end
;
