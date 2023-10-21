signature INTHASHTABLE =
sig
type '_a T
exception Lookup
val new : int -> '_a T
val lookup : ('_a T * int) -> '_a
val lookup_default : ('_a T * '_a * int) -> '_a
val tryLookup : ('_a T * int) -> '_a option
val is_defined : ('_a T * int) -> bool
val update : ('_a T * int * '_a) -> unit
val delete : ('_a T * int) -> unit
val to_list : '_a T -> (int * '_a) list
val copy : '_a T -> '_a T
val map : (int * '_a -> '_b) -> '_a T -> '_b T
val fold : ('b * int * '_a -> 'b) -> ('b * '_a T) -> 'b
val iterate : (int * '_a -> unit) -> '_a T -> unit
val stats : '_a T -> {size:int, count:int, smallest:int, largest:int}
val print_stats : '_a T -> unit
end
;
