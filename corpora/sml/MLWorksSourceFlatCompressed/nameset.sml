require "../typechecker/datatypes";
require "../main/options";
signature NAMESET =
sig
structure Datatypes : DATATYPES
structure Options : OPTIONS
type Nameset
val empty_nameset : unit -> Nameset
val union : Nameset * Nameset -> Nameset
val intersection : Nameset * Nameset -> Nameset
val add_tyname : Datatypes.Tyname * Nameset -> Nameset
val add_strname : Datatypes.Strname * Nameset -> Nameset
val member_of_tynames : Datatypes.Tyname * Nameset -> bool
val member_of_strnames : Datatypes.Strname * Nameset -> bool
val tynames_in_nameset : Datatypes.Tyname list * Nameset -> Nameset
val remove_tyname : Datatypes.Tyname * Nameset -> Nameset
val remove_strname : Datatypes.Strname * Nameset -> Nameset
val nameset_eq : Nameset * Nameset -> bool
val emptyp : Nameset -> bool
val no_tynames : Nameset -> bool
val diff : Nameset * Nameset -> Nameset
val initial_nameset : Nameset
val initial_nameset_for_builtin_library : Nameset
val string_nameset : Options.print_options -> Nameset -> string
val simple_copy : Nameset -> Nameset
val nameset_copy :
Nameset *
Datatypes.Strname Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap -> int ->
Nameset * Datatypes.Strname Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap
val nameset_rigid_copy :
Nameset *
Datatypes.Strname Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap -> int ->
Nameset * Datatypes.Strname Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap
val nameset_rehash : Nameset -> unit
val new_names_from_scratch :
Nameset -> int ->
Nameset *
Datatypes.Strname Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap
val tynames_of_nameset : Nameset -> Datatypes.Tyname list
val strnames_of_nameset : Nameset -> Datatypes.Strname list
val nameset_of_name_lists :
Datatypes.Tyname list *
Datatypes.Strname list ->
Nameset
end
;
