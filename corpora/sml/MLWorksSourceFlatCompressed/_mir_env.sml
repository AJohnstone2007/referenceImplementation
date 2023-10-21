require "../utils/intnewmap";
require "../lambda/lambdatypes";
require "mirtypes";
require "mir_env";
functor Mir_Env(
structure LambdaTypes : LAMBDATYPES where type LVar = int
structure MirTypes : MIRTYPES
structure IntMap : INTNEWMAP
sharing type MirTypes.Debugger_Types.Type = LambdaTypes.Type
) : MIR_ENV =
struct
structure LambdaTypes = LambdaTypes
structure MirTypes = MirTypes
type 'a Map = 'a IntMap.T
datatype Lambda_Env = LAMBDA_ENV of (MirTypes.any_register) IntMap.T
val empty_lambda_env =
LAMBDA_ENV(IntMap.empty)
datatype Closure_Env = CLOSURE_ENV of (int) IntMap.T
val empty_closure_env = CLOSURE_ENV(IntMap.empty)
fun add_lambda_env((lv, reg), LAMBDA_ENV env) = LAMBDA_ENV(IntMap.define(env, lv, reg))
fun lookup_lambda(lv, LAMBDA_ENV env) = IntMap.tryApply'(env, lv)
fun augment_lambda_env(LAMBDA_ENV lenv1, LAMBDA_ENV lenv2) = LAMBDA_ENV(IntMap.union(lenv1, lenv2))
fun add_closure_env((lv, i), CLOSURE_ENV env) = CLOSURE_ENV(IntMap.define(env, lv, i))
fun lookup_in_closure(lv, CLOSURE_ENV env) = IntMap.tryApply'(env, lv)
end
;
