require "types";
signature FOREIGN_CORE =
sig
structure FITypes : FOREIGN_TYPES
type address = FITypes.address
type foreign_object
type foreign_value
exception Unavailable
datatype load_mode = LOAD_LATER | LOAD_NOW
val load_object : (string * load_mode) -> foreign_object
val find_value : (foreign_object * string) -> foreign_value
val list_content : foreign_object -> string list
val call_unit_fun : foreign_value -> unit
val call_foreign_fun : (foreign_value * address * int * address) -> unit
end
;
