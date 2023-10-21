signature SCRAMBLE =
sig
val scramble : (int -> int -> int) -> (string * string) -> unit
val unscramble : (int -> int -> int) -> (string * string) -> unit
val reverse : int -> int -> int
val shuffle : int -> int -> int
val rotate : int -> int -> int -> int
val mix : int -> int -> int -> int
end
;
