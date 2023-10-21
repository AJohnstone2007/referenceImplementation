signature IEEE_REAL =
sig
exception Unordered
datatype real_order = LESS | EQUAL | GREATER | UNORDERED
datatype float_class =
NAN |
INF |
ZERO |
NORMAL |
SUBNORMAL
datatype rounding_mode = TO_NEAREST | TO_NEGINF | TO_POSINF | TO_ZERO
val setRoundingMode : rounding_mode -> unit
val getRoundingMode : unit -> rounding_mode
type decimal_approx
val class : decimal_approx -> float_class
val signBit : decimal_approx -> bool
val digits : decimal_approx -> int list
val exp : decimal_approx -> int
val toString : decimal_approx -> string
val fromString : string -> decimal_approx option
end
;
