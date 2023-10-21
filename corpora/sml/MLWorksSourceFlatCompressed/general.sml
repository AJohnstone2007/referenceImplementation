signature GENERAL =
sig
eqtype unit
type exn
exception Bind
exception Match
exception Subscript
exception Size
exception Overflow
exception Domain
exception Div
exception Chr
exception Fail of string
val exnName : exn -> string
val exnMessage : exn -> string
datatype order = LESS | EQUAL | GREATER
val <> : (''a * ''a) -> bool
val ! : 'a ref -> 'a
val := : ('a ref * 'a) -> unit
val o : (('b -> 'c) * ('a -> 'b)) -> 'a -> 'c
val before : ('a * unit) -> 'a
val ignore : 'a -> unit
end
;
