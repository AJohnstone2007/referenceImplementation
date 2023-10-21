require "../basis/__text_io";
signature TEXT =
sig
type T
val from_string : string -> T
val from_list : string list -> T
val concatenate : T * T -> T
val output : TextIO.outstream * T -> unit
end
;
