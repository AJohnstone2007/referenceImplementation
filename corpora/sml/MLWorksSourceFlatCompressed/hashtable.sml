signature HASHTABLE =
sig
type ('_Key,'_Value) HashTable
exception Lookup
val new : int * ('_Key * '_Key -> bool) * ('_Key -> int) -> ('_Key,'_Value) HashTable
val lookup : (('_Key,'_Value) HashTable * '_Key) -> '_Value
val lookup_default : (('_Key,'_Value) HashTable * '_Value * '_Key) -> '_Value
val tryLookup : (('_Key,'_Value) HashTable * '_Key) -> '_Value option
val update : (('_Key,'_Value) HashTable * '_Key * '_Value) -> unit
val delete : (('_Key,'_Value) HashTable * '_Key) -> unit
val to_list : ('_Key,'_Value) HashTable -> ('_Key * '_Value) list
val copy : ('_Key,'_Value) HashTable -> ('_Key,'_Value) HashTable
val map : ('_Key * '_Value1 -> '_Value2) -> ('_Key,'_Value1) HashTable -> ('_Key,'_Value2) HashTable
val fold : ('a * '_Key * '_Value -> 'a) -> ('a * ('_Key,'_Value) HashTable) -> 'a
val iterate : ('_Key * '_Value -> unit) -> ('_Key,'_Value) HashTable -> unit
val stats : ('_Key,'_Value) HashTable -> {size:int, count:int, smallest:int, largest:int}
val string_hash_table_stats : ('_Key,'_Value) HashTable -> string
end
;
