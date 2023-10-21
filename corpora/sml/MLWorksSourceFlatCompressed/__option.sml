require "option";
structure Option : OPTION =
struct
datatype option = datatype option
exception Option = Option
val isSome = isSome
and valOf = valOf
and getOpt = getOpt
fun filter pred x = if (pred x) then (SOME x) else NONE
fun map f NONE = NONE
| map f (SOME x) = SOME (f x)
fun join NONE = NONE
| join (SOME x) = x
fun mapPartial f = join o map f
fun compose (f, g) x =
(case (g x)
of NONE => NONE
| (SOME x) => SOME(f x) )
fun composePartial (f, g) x =
(case (g x) of
NONE => NONE
| (SOME x) => f x )
end
;
