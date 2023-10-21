signature PRETTY =
sig
type T
val blk : int * T list -> T
val str : string -> T
val brk : int -> T
val nl : T
val lst : string * T list * string -> T list -> T list
val margin: int ref
val string_of_T : T -> string
val print_T : (string -> unit) -> T -> unit
val reduce : ('a * string -> 'a) -> ('a * int * T) -> 'a
end;
