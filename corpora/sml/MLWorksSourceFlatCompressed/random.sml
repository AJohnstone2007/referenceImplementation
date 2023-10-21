signature RANDOM =
sig
val setSeed : int -> unit
val random : int * int -> int
val randomize : unit -> unit
end
;
