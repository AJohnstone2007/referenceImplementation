require "types";
signature FOREIGN_ALIENS =
sig
structure FITypes : FOREIGN_TYPES
type address = FITypes.address
type name = FITypes.name
type filename = FITypes.filename
type ('a,'b)Map
type foreign_module
type foreign_item
type info
val get_module_later : filename -> foreign_module
val get_item_later : (foreign_module * name) -> foreign_item
val get_module_now : filename -> foreign_module
val get_item_now : (foreign_module * name) -> foreign_item
val get_item_names : foreign_module -> name list
val get_item_info : foreign_module -> (name, info) Map
val ensure_module : foreign_module -> foreign_module
val ensure_item : foreign_item -> foreign_item
val reset_module : foreign_module -> foreign_module
val reset_item : foreign_item -> foreign_item
val refresh_module : foreign_module -> foreign_module
val refresh_item : foreign_item -> foreign_item
val ensure_alien_modules : unit -> unit
val ensure_alien_items : unit -> unit
val ensureAliens : unit -> unit
val reset_alien_modules : unit -> unit
val reset_alien_items : unit -> unit
val resetAliens : unit -> unit
val refresh_alien_modules : unit -> unit
val refresh_alien_items : unit -> unit
val refreshAliens : unit -> unit
val call_alien_code : foreign_item * address * int * address -> unit
end;
