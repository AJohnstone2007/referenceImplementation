require "../typechecker/datatypes";
signature STRENV =
sig
structure Datatypes : DATATYPES
val empty_strenv : Datatypes.Strenv
val empty_strenvp : Datatypes.Strenv -> bool
val lookup : Datatypes.Ident.StrId * Datatypes.Strenv -> Datatypes.Structure option
val se_plus_se : Datatypes.Strenv * Datatypes.Strenv -> Datatypes.Strenv
val add_to_se : Datatypes.Ident.StrId * Datatypes.Structure * Datatypes.Strenv -> Datatypes.Strenv
val initial_se : Datatypes.Strenv
end
;
