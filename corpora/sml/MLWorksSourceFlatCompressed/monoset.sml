require "text";
signature MONOSET =
sig
structure Text : TEXT
type T
type element
val empty : T
val singleton : element -> T
val add : T * element -> T
val remove : T * element -> T
val member : T * element -> bool
val is_empty : T -> bool
val equal : T * T -> bool
val subset : T * T -> bool
val intersection : T * T -> T
val union : T * T -> T
val difference : T * T -> T
val cardinality : T -> int
val reduce : ('a * element -> 'a) -> ('a * T) -> 'a
val iterate : (element -> unit) -> T -> unit
val filter : (element -> bool) -> T -> T
val to_list : T -> element list
val from_list : element list -> T
val to_text : T -> Text.T
end
;
