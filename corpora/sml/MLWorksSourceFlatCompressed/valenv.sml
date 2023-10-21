require "../typechecker/datatypes";
signature VALENV =
sig
structure Datatypes : DATATYPES
type Options
type ErrorInfo
exception LookupValId of Datatypes.Ident.ValId
val initial_ve : Datatypes.Valenv
val initial_ve_for_builtin_library : Datatypes.Valenv
val initial_ee : Datatypes.Valenv
val lookup : Datatypes.Ident.ValId * Datatypes.Valenv -> Datatypes.Typescheme
val add_to_ve : Datatypes.Ident.ValId * Datatypes.Typescheme * Datatypes.Valenv -> Datatypes.Valenv
val ve_plus_ve : Datatypes.Valenv * Datatypes.Valenv -> Datatypes.Valenv
val ve_domain : Datatypes.Valenv -> Datatypes.Ident.ValId list
val string_valenv : int * Datatypes.Valenv -> string
val valenv_eq : Datatypes.Valenv * Datatypes.Valenv -> bool
val dom_valenv_eq : Datatypes.Valenv * Datatypes.Valenv -> bool
val empty_valenvp : Datatypes.Valenv -> bool
val tyvars : Datatypes.Valenv -> Datatypes.Ident.TyVar list
val resolve_overloads : ErrorInfo -> Datatypes.Env * Options -> unit
val ve_copy :
Datatypes.Valenv * Datatypes.Tyname Datatypes.StampMap ->
Datatypes.Valenv
end;
