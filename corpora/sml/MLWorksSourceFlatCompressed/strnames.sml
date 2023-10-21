require "../typechecker/datatypes";
signature STRNAMES =
sig
structure Datatypes : DATATYPES
val uninstantiated : Datatypes.Strname -> bool
val strname_eq : Datatypes.Strname * Datatypes.Strname -> bool
val metastrname_eq : Datatypes.Strname * Datatypes.Strname -> bool
val strname_ord : Datatypes.Strname * Datatypes.Strname -> bool
val strip : Datatypes.Strname -> Datatypes.Strname
val string_strname : Datatypes.Strname -> string
val create_strname_copy :
bool ->
Datatypes.Strname Datatypes.StampMap * Datatypes.Strname ->
Datatypes.Strname Datatypes.StampMap
val strname_copy :
Datatypes.Strname * Datatypes.Strname Datatypes.StampMap-> Datatypes.Strname
end
;
