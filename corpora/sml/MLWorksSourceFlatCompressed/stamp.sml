require "../utils/monomap";
signature STAMP =
sig
structure Map : MONOMAP
eqtype Stamp
sharing type Stamp = Map.object
val make_stamp_n : int -> Stamp
val make_stamp : unit -> Stamp
val stamp : Stamp -> int
val string_stamp : Stamp -> string
val stamp_lt : (Stamp * Stamp) -> bool
val stamp_eq : Stamp * Stamp -> bool
val reset_counter : int -> unit
val read_counter : unit -> int
val push_counter : unit -> unit
val pop_counter : unit -> unit
end;
