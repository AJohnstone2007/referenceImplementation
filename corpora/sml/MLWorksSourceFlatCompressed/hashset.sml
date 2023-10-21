signature HASHSET =
sig
type HashSet
type element
val max_size : int
val add_member : (HashSet * element) -> HashSet
val add_list : (HashSet * element list) -> HashSet
val remove_member : (HashSet * element) -> HashSet
val empty_set : int -> HashSet
val empty_setp : HashSet -> bool
val is_member : (HashSet * element) -> bool
val union : HashSet * HashSet -> HashSet
val intersection : HashSet * HashSet -> HashSet
val subset : HashSet * HashSet -> bool
val setdiff : HashSet * HashSet -> HashSet
val seteq : HashSet * HashSet -> bool
val set_to_list : HashSet -> element list
val fold : ('a * element -> 'a) -> ('a * HashSet) -> 'a
val iterate : (element -> unit) -> HashSet -> unit
val list_to_set : element list -> HashSet
val set_print : HashSet * (element -> string) -> string
val set_size : HashSet -> int
val rehash : HashSet -> unit
end
;
