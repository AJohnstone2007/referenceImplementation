require "../lambda/lambdatypes";
require "mirtypes";
signature MIR_ENV =
sig
structure LambdaTypes : LAMBDATYPES
structure MirTypes : MIRTYPES
type 'a Map
sharing type MirTypes.Debugger_Types.Type = LambdaTypes.Type
datatype Lambda_Env =
LAMBDA_ENV of (MirTypes.any_register) Map
val empty_lambda_env : Lambda_Env
datatype Closure_Env = CLOSURE_ENV of int Map
val empty_closure_env : Closure_Env
val add_lambda_env:
(LambdaTypes.LVar * MirTypes.any_register) * Lambda_Env -> Lambda_Env
val lookup_lambda: LambdaTypes.LVar * Lambda_Env -> MirTypes.any_register option
val augment_lambda_env: Lambda_Env * Lambda_Env -> Lambda_Env
val add_closure_env:
(LambdaTypes.LVar * int) * Closure_Env -> Closure_Env
val lookup_in_closure: LambdaTypes.LVar * Closure_Env -> int option
end;
