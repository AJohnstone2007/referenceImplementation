require "../typechecker/datatypes";
signature ENVIRONMENT =
sig
structure Datatypes : DATATYPES
exception LookupStrId of Datatypes.Ident.StrId
val empty_env : Datatypes.Env
val empty_envp : Datatypes.Env -> bool
val env_plus_env : Datatypes.Env * Datatypes.Env -> Datatypes.Env
val SE_in_env : Datatypes.Strenv -> Datatypes.Env
val TE_in_env : Datatypes.Tyenv -> Datatypes.Env
val VE_in_env : Datatypes.Valenv -> Datatypes.Env
val VE_TE_in_env : Datatypes.Valenv * Datatypes.Tyenv -> Datatypes.Env
val abs : Datatypes.Tyenv * Datatypes.Env -> Datatypes.Env
val string_environment : Datatypes.Env -> string
val string_str : Datatypes.Structure -> string
val no_imptyvars : Datatypes.Env -> (Datatypes.Type * Datatypes.Type) option
val lookup_strid : Datatypes.Ident.StrId * Datatypes.Env -> Datatypes.Structure option
val lookup_longtycon : Datatypes.Ident.LongTyCon * Datatypes.Env -> Datatypes.Tystr
val lookup_longvalid : Datatypes.Ident.LongValId * Datatypes.Env -> Datatypes.Typescheme
val lookup_longstrid : Datatypes.Ident.LongStrId * Datatypes.Env -> Datatypes.Structure
val compose_maps :
((Datatypes.Strname Datatypes.StampMap * Datatypes.Tyname Datatypes.StampMap) *
(Datatypes.Strname Datatypes.StampMap * Datatypes.Tyname Datatypes.StampMap)) ->
(Datatypes.Strname Datatypes.StampMap * Datatypes.Tyname Datatypes.StampMap)
val str_copy :
Datatypes.Structure * (Datatypes.Strname) Datatypes.StampMap *
Datatypes.Tyname Datatypes.StampMap -> Datatypes.Structure
val resolve_top_level : Datatypes.Structure -> Datatypes.Structure
val expand_str : Datatypes.Structure -> Datatypes.Structure
val expand_env : Datatypes.Env -> Datatypes.Env
val initial_env : Datatypes.Env
val initial_env_for_builtin_library : Datatypes.Env
val struct_eq : Datatypes.Structure * Datatypes.Structure -> bool
val compress_str : Datatypes.Structure -> Datatypes.Structure
end
;
