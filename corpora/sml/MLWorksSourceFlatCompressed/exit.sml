signature EXIT =
sig
type status
val success : status
val failure : status
val isSuccess : status -> bool
val atExit : (unit -> unit) -> unit
val exit : status -> 'a
val terminate : status -> 'a
end
;
