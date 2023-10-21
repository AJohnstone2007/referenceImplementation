signature HASH_TABLE =
sig
structure Key : HASH_KEY
type 'a hash_table
val mkTable : (int * exn) -> '1a hash_table
val insert : '2a hash_table -> (Key.hash_key * '2a) -> unit
val find : 'a hash_table -> Key.hash_key -> 'a
val peek : 'a hash_table -> Key.hash_key -> 'a option
val remove : 'a hash_table -> Key.hash_key -> 'a
val numItems : 'a hash_table -> int
val listItems : '_a hash_table -> (Key.hash_key * '_a) list
val apply : ((Key.hash_key * 'a) -> 'b) -> 'a hash_table -> unit
val map : ((Key.hash_key * 'a) -> '2b) -> 'a hash_table -> '2b hash_table
val filter : ((Key.hash_key * 'a) -> bool) -> 'a hash_table -> unit
val transform : ('a -> '2b) -> 'a hash_table -> '2b hash_table
val copy : '1a hash_table -> '1a hash_table
val bucketSizes : '_a hash_table -> int list
end
;
