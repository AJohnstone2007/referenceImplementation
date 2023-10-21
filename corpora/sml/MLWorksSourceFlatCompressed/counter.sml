signature COUNTER =
sig
val counter : unit -> int
val previous_count : int -> int
val next_count : int -> int
val reset_counter : int -> unit
val read_counter : unit -> int
end;
