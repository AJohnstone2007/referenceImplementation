require "basistypes";
signature BASIS =
sig
structure BasisTypes : BASISTYPES
exception LookupValId of BasisTypes.Datatypes.Ident.ValId
exception LookupTyCon of BasisTypes.Datatypes.Ident.TyCon
exception LookupStrId of BasisTypes.Datatypes.Ident.StrId
exception LookupTyvar
exception LookupSigId
exception LookupFunId
type error_info
type print_options
type options
val empty_sigenv : BasisTypes.Sigenv
val add_to_sigenv : BasisTypes.Datatypes.Ident.SigId * BasisTypes.Sigma *
BasisTypes.Sigenv -> BasisTypes.Sigenv
val sigenv_plus_sigenv : BasisTypes.Sigenv * BasisTypes.Sigenv
-> BasisTypes.Sigenv
val empty_funenv : BasisTypes.Funenv
val add_to_funenv : BasisTypes.Datatypes.Ident.FunId * BasisTypes.Phi
* BasisTypes.Funenv -> BasisTypes.Funenv
val funenv_plus_funenv : BasisTypes.Funenv * BasisTypes.Funenv
-> BasisTypes.Funenv
val lookup_tyvar : BasisTypes.Datatypes.Ident.TyVar * BasisTypes.Context
-> BasisTypes.Datatypes.Type
val lookup_val :
BasisTypes.Datatypes.Ident.LongValId *
BasisTypes.Context *
BasisTypes.Datatypes.Ident.Location.T *
bool ->
BasisTypes.Datatypes.Type *
(BasisTypes.Datatypes.InstanceInfo *
BasisTypes.Datatypes.Instance ref option)
val lookup_tycon :
BasisTypes.Datatypes.Ident.TyCon * BasisTypes.Context
-> BasisTypes.Datatypes.Tystr
val lookup_longtycon :
BasisTypes.Datatypes.Ident.LongTyCon * BasisTypes.Context
-> BasisTypes.Datatypes.Tystr
val get_tyvarset : BasisTypes.Context ->
BasisTypes.Datatypes.Ident.TyVar BasisTypes.Set.Set
val context_plus_env : BasisTypes.Context * BasisTypes.Datatypes.Env
-> BasisTypes.Context
val context_plus_ve : BasisTypes.Context * BasisTypes.Datatypes.Valenv
-> BasisTypes.Context
val context_plus_te : BasisTypes.Context * BasisTypes.Datatypes.Tyenv
-> BasisTypes.Context
val context_plus_tyvarset :
BasisTypes.Context *
BasisTypes.Datatypes.Ident.TyVar BasisTypes.Set.Set ->
BasisTypes.Context
val context_plus_tyvarlist :
BasisTypes.Context * BasisTypes.Datatypes.Ident.TyVar list
-> BasisTypes.Context
val context_for_datbind :
BasisTypes.Context * string *
(BasisTypes.Datatypes.Ident.TyVar list *
BasisTypes.Datatypes.Ident.TyCon) list ->
BasisTypes.Context
val context_level : BasisTypes.Context -> int
val close :
(error_info * options * BasisTypes.Datatypes.Ident.Location.T) ->
int * BasisTypes.Datatypes.Valenv *
BasisTypes.Datatypes.Ident.ValId list *
BasisTypes.Datatypes.Ident.TyVar BasisTypes.Set.Set * bool ->
BasisTypes.Datatypes.Valenv
val basis_to_context : BasisTypes.Basis -> BasisTypes.Context
val env_of_context : BasisTypes.Context -> BasisTypes.Datatypes.Env
val te_of_context : BasisTypes.Context -> BasisTypes.Datatypes.Tyenv
val env_to_context : BasisTypes.Datatypes.Env -> BasisTypes.Context
val lookup_longstrid :
BasisTypes.Datatypes.Ident.LongStrId * BasisTypes.Basis
-> BasisTypes.Datatypes.Structure
val basis_plus_env : BasisTypes.Basis * BasisTypes.Datatypes.Env
-> BasisTypes.Basis
val env_in_basis : BasisTypes.Datatypes.Env -> BasisTypes.Basis
val basis_plus_names : BasisTypes.Basis * BasisTypes.Nameset
-> BasisTypes.Basis
val basis_plus_sigenv : BasisTypes.Basis * BasisTypes.Sigenv
-> BasisTypes.Basis
val sigenv_in_basis : BasisTypes.Sigenv -> BasisTypes.Basis
val lookup_sigid : BasisTypes.Datatypes.Ident.SigId * BasisTypes.Basis
-> BasisTypes.Sigma
val basis_plus_funenv : BasisTypes.Basis * BasisTypes.Funenv
-> BasisTypes.Basis
val funenv_in_basis : BasisTypes.Funenv -> BasisTypes.Basis
val lookup_funid : BasisTypes.Datatypes.Ident.FunId * BasisTypes.Basis
-> BasisTypes.Phi
val basis_circle_plus_basis : BasisTypes.Basis * BasisTypes.Basis
-> BasisTypes.Basis
val basis_level : BasisTypes.Basis -> int
val initial_basis : BasisTypes.Basis
val initial_basis_for_builtin_library : BasisTypes.Basis
val empty_basis : BasisTypes.Basis
val reduce_chains : BasisTypes.Basis -> unit
val remove_str :
BasisTypes.Basis * BasisTypes.Datatypes.Ident.StrId -> BasisTypes.Basis
val add_str :
BasisTypes.Basis * BasisTypes.Datatypes.Ident.StrId *
BasisTypes.Datatypes.Structure -> BasisTypes.Basis
val add_val :
BasisTypes.Basis * BasisTypes.Datatypes.Ident.ValId *
BasisTypes.Datatypes.Typescheme -> BasisTypes.Basis
val pervasive_stamp_count : int
val tynamesNotIn: BasisTypes.Datatypes.Type
* BasisTypes.Context
-> BasisTypes.Datatypes.Tyname list
val valEnvTynamesNotIn: BasisTypes.Datatypes.Valenv
* BasisTypes.Context
-> BasisTypes.Datatypes.Tyname list
end;
