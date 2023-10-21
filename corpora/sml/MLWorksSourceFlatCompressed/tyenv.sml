require "../typechecker/datatypes";
signature TYENV =
sig
structure Datatypes : DATATYPES
exception LookupTyCon of Datatypes.Ident.TyCon
val empty_tyenv : Datatypes.Tyenv
val initial_te : Datatypes.Tyenv
val initial_te_for_builtin_library : Datatypes.Tyenv
val lookup : Datatypes.Tyenv * Datatypes.Ident.TyCon -> Datatypes.Tystr
val te_plus_te : Datatypes.Tyenv * Datatypes.Tyenv -> Datatypes.Tyenv
val add_to_te : (Datatypes.Tyenv * Datatypes.Ident.TyCon * Datatypes.Tystr) -> Datatypes.Tyenv
val string_tyenv : Datatypes.Tyenv -> string
val empty_tyenvp : Datatypes.Tyenv -> bool
val te_copy : Datatypes.Tyenv * Datatypes.Tyname Datatypes.StampMap -> Datatypes.Tyenv
val tystr_copy : Datatypes.Tystr * Datatypes.Tyname Datatypes.StampMap -> Datatypes.Tystr
end
;
