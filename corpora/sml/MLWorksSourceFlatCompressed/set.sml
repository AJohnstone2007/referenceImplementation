signature SET =
sig
type ''a Set
val add_member : ''a * ''a Set -> ''a Set
val singleton : ''a -> ''a Set
val empty_set : ''a Set
val empty_setp : ''a Set -> bool
val is_member : ''a * ''a Set -> bool
val intersect : ''a Set * ''a Set -> bool
val union : ''a Set * ''a Set -> ''a Set
val disjoin : ''a Set * ''a Set -> ''a Set
val intersection : ''a Set * ''a Set -> ''a Set
val subset : ''a Set * ''a Set -> bool
val setdiff : ''a Set * ''a Set -> ''a Set
val seteq : ''a Set * ''a Set -> bool
val set_to_list : ''a Set -> ''a list
val list_to_set : ''a list -> ''a Set
val map : (''a -> ''b) -> ''a Set -> ''b Set
val fold : ('b * ''a -> 'b) -> ('b * ''a Set) -> 'b
val set_print : ''a Set * (''a -> string) -> string
end
;
