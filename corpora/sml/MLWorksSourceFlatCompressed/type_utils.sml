require "../typechecker/datatypes";
signature TYPE_UTILS =
sig
structure Datatypes : DATATYPES
val get_valenv :
Datatypes.Type -> string * (Datatypes.Ident.ValId,Datatypes.Typescheme) Datatypes.NewMap.map
val type_from_scheme : Datatypes.Typescheme -> Datatypes.Type
val is_vcc : Datatypes.Type -> bool
val get_no_cons : Datatypes.Type -> int
val get_no_vcc_cons : Datatypes.Type -> int
val get_no_null_cons : Datatypes.Type -> int
val has_null_cons : Datatypes.Type -> bool
val has_value_cons : Datatypes.Type -> bool
val get_cons_type : Datatypes.Type -> Datatypes.Type
val is_integral2 : Datatypes.Type -> bool
val is_integral3 : Datatypes.Type -> bool
end
;
