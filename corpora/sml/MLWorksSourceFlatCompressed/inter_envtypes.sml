require "../main/options";
require "../lambda/environtypes";
signature INTER_ENVTYPES =
sig
structure EnvironTypes : ENVIRONTYPES
structure Options : OPTIONS
datatype inter_env =
INTER_ENV of
(EnvironTypes.LambdaTypes.Ident.ValId, MLWorks.Internal.Value.ml_value) EnvironTypes.NewMap.map *
(EnvironTypes.LambdaTypes.Ident.StrId, MLWorks.Internal.Value.ml_value) EnvironTypes.NewMap.map *
(EnvironTypes.LambdaTypes.Ident.FunId, MLWorks.Internal.Value.ml_value) EnvironTypes.NewMap.map
val empty_env : inter_env
val lookup_val : EnvironTypes.LambdaTypes.Ident.ValId * inter_env -> MLWorks.Internal.Value.ml_value
val lookup_str : EnvironTypes.LambdaTypes.Ident.StrId * inter_env -> MLWorks.Internal.Value.ml_value
val lookup_fun : EnvironTypes.LambdaTypes.Ident.FunId * inter_env -> MLWorks.Internal.Value.ml_value
val add_val : inter_env * (EnvironTypes.LambdaTypes.Ident.ValId * MLWorks.Internal.Value.ml_value) -> inter_env
val add_str : inter_env * (EnvironTypes.LambdaTypes.Ident.StrId * MLWorks.Internal.Value.ml_value) -> inter_env
val add_fun : inter_env * (EnvironTypes.LambdaTypes.Ident.FunId * MLWorks.Internal.Value.ml_value) -> inter_env
val remove_str : inter_env * (EnvironTypes.LambdaTypes.Ident.StrId) -> inter_env
val augment : inter_env * inter_env -> inter_env
exception Augment
val augment_with_module :
inter_env * EnvironTypes.Top_Env * MLWorks.Internal.Value.T -> inter_env
val print : Options.print_options -> ('a * string -> 'a) -> ('a * inter_env) -> 'a
end
;
