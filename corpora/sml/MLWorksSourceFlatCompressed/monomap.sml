signature MONOMAP =
sig
type ('image) T
eqtype object
val empty : ('image) T
val is_empty : ('image) T -> bool
val define : ('image) T * object * 'image -> ('image) T
val define' : ('image) T * (object * 'image) -> ('image) T
val combine : (object * 'image * 'image -> 'image) -> ('image) T * object * 'image -> ('image) T
val undefine : ('image) T * object -> ('image) T
exception Undefined
val apply : ('image) T -> object -> 'image
val apply' : ('image) T * object -> 'image
val tryApply : ('image) T -> object -> 'image option
val tryApply' : ('image) T * object -> 'image option
val apply_default : ('image) T * 'image -> object -> 'image
val apply_default' : ('image) T * 'image * object -> 'image
val domain : ('image) T -> object list
val domain_ordered : ('image) T -> object list
val range : ('image) T -> 'image list
val range_ordered : ('image) T -> 'image list
val to_list : ('image) T -> (object * 'image) list
val to_list_ordered : ('image) T -> (object * 'image) list
val from_list :
(object * 'image) list ->
('image) T
val fold : ('a * object * 'c -> 'a) -> 'a * ('c)T -> 'a
val fold_in_order : ('a * object * 'c -> 'a) -> 'a * ('c)T -> 'a
val fold_in_rev_order : ('a * object * 'c -> 'a) -> 'a * ('c)T -> 'a
val map : (object * 'b -> 'c) -> ('b)T -> ('c)T
val size : ('b)T -> int
val eq : ('a * 'b -> bool) -> ('a)T * ('b)T -> bool
val rank : ('b)T -> object -> int
val rank' : ('b)T * object -> int
val union : ('b)T * ('b)T -> ('b)T
val merge : ('b * 'b -> 'b) -> ('b)T * ('b)T -> ('b)T
val forall : (object * 'b -> bool) -> ('b)T -> bool
val exists : (object * 'b -> bool) -> ('b)T -> bool
val iterate : (object * 'b -> unit) -> ('b)T -> unit
val iterate_ordered : (object * 'b -> unit) -> ('b)T -> unit
val string :
(object -> string) -> ('b -> string) ->
{start : string, domSep : string, itemSep : string, finish : string} ->
('b)T -> string
end
;
