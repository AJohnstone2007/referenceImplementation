require "environtypes";
signature ENVIRON =
sig
structure EnvironTypes: ENVIRONTYPES
type Structure
val empty_env: EnvironTypes.Env
val empty_fun_env: EnvironTypes.Fun_Env
val empty_top_env: EnvironTypes.Top_Env
val empty_denv: EnvironTypes.DebuggerEnv
val add_valid_env: EnvironTypes.Env * (EnvironTypes.LambdaTypes.Ident.ValId * EnvironTypes.comp) ->
EnvironTypes.Env
val add_strid_env:
EnvironTypes.Env * (EnvironTypes.LambdaTypes.Ident.StrId * (EnvironTypes.Env * EnvironTypes.comp * bool))
-> EnvironTypes.Env
val add_valid_denv: EnvironTypes.DebuggerEnv * (EnvironTypes.LambdaTypes.Ident.ValId * EnvironTypes.DebuggerExp) ->
EnvironTypes.DebuggerEnv
val add_strid_denv:
EnvironTypes.DebuggerEnv *
(EnvironTypes.LambdaTypes.Ident.StrId * EnvironTypes.DebuggerStrExp)
-> EnvironTypes.DebuggerEnv
val augment_env: EnvironTypes.Env * EnvironTypes.Env -> EnvironTypes.Env
val augment_denv :
EnvironTypes.DebuggerEnv * EnvironTypes.DebuggerEnv -> EnvironTypes.DebuggerEnv
val lookup_valid: EnvironTypes.LambdaTypes.Ident.ValId * EnvironTypes.Env -> EnvironTypes.comp
val lookup_strid: EnvironTypes.LambdaTypes.Ident.StrId * EnvironTypes.Env ->
EnvironTypes.Env * EnvironTypes.comp * bool
val lookup_valid': EnvironTypes.LambdaTypes.Ident.ValId * EnvironTypes.DebuggerEnv -> EnvironTypes.DebuggerExp
val lookup_strid': EnvironTypes.LambdaTypes.Ident.StrId * EnvironTypes.DebuggerEnv ->
EnvironTypes.DebuggerStrExp
val FindBuiltin: EnvironTypes.LambdaTypes.Ident.LongValId * EnvironTypes.Env ->
EnvironTypes.LambdaTypes.Primitive option
val define_overloaded_ops: (string * EnvironTypes.LambdaTypes.Primitive) list -> unit
val overloaded_op: EnvironTypes.LambdaTypes.Ident.ValId ->
EnvironTypes.LambdaTypes.Primitive option
val add_funid_env:
EnvironTypes.Fun_Env *
(EnvironTypes.LambdaTypes.Ident.FunId * (EnvironTypes.comp * EnvironTypes.Env * bool))
-> EnvironTypes.Fun_Env
val augment_top_env:
EnvironTypes.Top_Env * EnvironTypes.Top_Env -> EnvironTypes.Top_Env
val lookup_funid: EnvironTypes.LambdaTypes.Ident.FunId * EnvironTypes.Fun_Env ->
EnvironTypes.comp * EnvironTypes.Env * bool
val assign_fields : EnvironTypes.Top_Env -> EnvironTypes.Top_Env
val number_envs : ('a list * 'b list * 'c list) ->
(('a * EnvironTypes.comp) list *
('b * EnvironTypes.comp) list *
('c * EnvironTypes.comp) list)
val make_external : EnvironTypes.Top_Env -> EnvironTypes.Top_Env
val make_str_env : Structure * bool -> EnvironTypes.Env
val make_str_dexp : Structure -> EnvironTypes.DebuggerStrExp
val simplify_topenv : EnvironTypes.Top_Env * EnvironTypes.LambdaTypes.LambdaExp -> EnvironTypes.Top_Env
end
;
