require "../lambda/environtypes";
signature PRIMITIVES = sig
structure EnvironTypes : ENVIRONTYPES
val values_for_builtin_library : (string * EnvironTypes.LambdaTypes.Primitive) list
val initial_env_for_builtin_library : EnvironTypes.Env
val initial_env : EnvironTypes.Env
val env_after_builtin : EnvironTypes.Env
val check_builtin_env:
{error_fn: (string -> unit),
topenv: EnvironTypes.Top_Env}
-> bool
val env_for_not_ml_definable_builtins : EnvironTypes.Env
val env_for_lookup_in_lambda : EnvironTypes.Env
end
;
