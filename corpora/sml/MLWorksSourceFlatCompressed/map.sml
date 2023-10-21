signature MAP =
sig
type ('object, 'image) map
val empty : (('object * 'object -> bool) * ('object * 'object -> bool)) -> ('object, 'image) map
val empty' : (''object * ''object -> bool) -> (''object, 'image) map
val is_empty : ('object, 'image) map -> bool
val define : ('object, 'image) map * 'object * 'image -> ('object, 'image) map
val define' : ('object, 'image) map * ('object * 'image) -> ('object, 'image) map
val combine : ('object * 'image * 'image -> 'image) -> ('object, 'image) map * 'object * 'image -> ('object, 'image) map
val undefine : ('object, 'image) map * 'object -> ('object, 'image) map
exception Undefined
val apply : ('object, 'image) map -> 'object -> 'image
val apply' : ('object, 'image) map * 'object -> 'image
val tryApply : ('object, 'image) map -> 'object -> 'image option
val tryApply' : ('object, 'image) map * 'object -> 'image option
val tryApply'Eq : (''object, 'image) map * ''object -> 'image option
val apply_default : ('object, 'image) map * 'image -> 'object -> 'image
val apply_default' : ('object, 'image) map * 'image * 'object -> 'image
val domain : ('object, 'image) map -> 'object list
val domain_ordered : ('object, 'image) map -> 'object list
val range : ('object, 'image) map -> 'image list
val range_ordered : ('object, 'image) map -> 'image list
val to_list : ('object, 'image) map -> ('object * 'image) list
val to_list_ordered : ('object, 'image) map -> ('object * 'image) list
val from_list :
(('object * 'object -> bool) * ('object * 'object -> bool)) ->
('object * 'image) list ->
('object, 'image) map
val from_list' :
(''object * ''object -> bool) ->
(''object * 'image) list ->
(''object, 'image) map
val fold : ('a * 'b * 'c -> 'a) -> 'a * ('b, 'c) map -> 'a
val fold_in_order : ('a * 'b * 'c -> 'a) -> 'a * ('b, 'c) map -> 'a
val fold_in_rev_order : ('a * 'b * 'c -> 'a) -> 'a * ('b, 'c) map -> 'a
val map : ('a * 'b -> 'c) -> ('a, 'b) map -> ('a, 'c) map
val size : ('a, 'b) map -> int
val eq : ('a * 'b -> bool) -> ('o, 'a) map * ('o, 'b) map -> bool
val rank : ('a, 'b) map -> 'a -> int
val rank' : ('a, 'b) map * 'a -> int
val union : ('a, 'b) map * ('a, 'b) map -> ('a, 'b) map
val merge : ('a * 'b * 'b -> 'b) -> ('a, 'b) map * ('a, 'b) map -> ('a, 'b) map
val forall : ('a * 'b -> bool) -> ('a, 'b) map -> bool
val exists : ('a * 'b -> bool) -> ('a, 'b) map -> bool
val iterate : ('a * 'b -> unit) -> ('a, 'b) map -> unit
val iterate_ordered : ('a * 'b -> unit) -> ('a, 'b) map -> unit
val string :
('a -> string) -> ('b -> string) ->
{start : string, domSep : string, itemSep : string, finish : string} ->
('a, 'b) map -> string
val get_ordering : ('a,'b) map -> ('a * 'a -> bool)
val get_equality : ('a,'b) map -> ('a * 'a -> bool)
end
;
