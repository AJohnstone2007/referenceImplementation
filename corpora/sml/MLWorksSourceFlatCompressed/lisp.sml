signature LISP_UTILS =
sig
val unwind_protect : (unit -> 'a) -> (unit -> 'b) -> 'a
val unwind_protect' : ('a -> 'b) -> ('a -> 'c) -> ('a -> 'b)
type 'a svref
val svref : '_a -> '_a svref
val letv : ('_a svref * '_a) list -> (unit -> 'b) -> 'b
val letv' : ('_a svref * '_a) list -> ('c -> 'b) -> 'c -> 'b
val setv : '_a svref -> '_a -> unit
val !! : '_a svref -> '_a
end
;
