require "../utils/diagnostic";
require "../typechecker/datatypes";
signature ENC_SUB =
sig
structure Diagnostic : DIAGNOSTIC
structure DataTypes : DATATYPES
val type_same : DataTypes.Type * DataTypes.Type -> bool
val tyname_same : DataTypes.Tyname * DataTypes.Tyname -> bool
val type_hash : DataTypes.Type -> int
val tyname_hash : DataTypes.Tyname -> int
val tyfun_hash : DataTypes.Tyfun -> int
val type_from_scheme : DataTypes.Typescheme -> DataTypes.Type
val tyname_valenv_same : DataTypes.Valenv * DataTypes.Valenv -> bool
val tyname_valenv_hash : DataTypes.Valenv -> int
end
;
