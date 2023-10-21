signature MLWORKS_C_RESOURCE =
sig
type 'a ptr
val withResource : ('a -> 'b) * 'a * ('a -> 'c) -> 'c
val withNonNullResource :
(''a ptr -> 'b) * exn * ''a ptr * (''a ptr -> 'c) -> 'c
end
;
