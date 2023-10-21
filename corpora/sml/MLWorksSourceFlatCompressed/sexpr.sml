signature SEXPR =
sig
datatype 'a Sexpr = NIL | ATOM of 'a | CONS of 'a Sexpr * 'a Sexpr
val list : ('a Sexpr) list -> 'a Sexpr
exception Append
val append : 'a Sexpr * 'a Sexpr -> 'a Sexpr
val printSexpr : ('a -> string) -> 'a Sexpr -> string
val pprintSexpr : ('a -> string) -> 'a Sexpr -> string
val toList : 'a list Sexpr -> 'a list
end
;
