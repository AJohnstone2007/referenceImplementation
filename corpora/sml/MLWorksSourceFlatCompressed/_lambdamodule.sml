require "../utils/crash";
require "../utils/lists";
require "environ";
require "lambdamodule";
functor LambdaModule (structure Environ : ENVIRON
structure Lists : LISTS
structure Crash : CRASH
) : LAMBDAMODULE =
struct
structure EnvironTypes = Environ.EnvironTypes
structure LambdaTypes = EnvironTypes.LambdaTypes
structure Map = EnvironTypes.NewMap
structure Ident = LambdaTypes.Ident
fun ident(x as EnvironTypes.FIELD {index, ...}) = (true,x)
| ident(x as EnvironTypes.PRIM _) = (false,x)
| ident _ = Crash.impossible"External env not field"
fun replace_ident(x, lvar) = lvar
fun get_field_from_funenv(comp, _, _) = ident comp
fun replace_field_in_funenv((_, env, gm), lvar) =
(lvar, env, gm)
fun get_field_from_strenv(_, comp, _) = ident comp
fun replace_field_in_strenv((env, _, gm), lvar) = (env, lvar, gm)
fun let_lambdas_in_exp(lv_le_list, lambda_exp) =
Lists.reducer LambdaTypes.do_binding (lv_le_list, lambda_exp)
fun extract_op (EnvironTypes.LAMB (x,_)) = LambdaTypes.VAR x
| extract_op (EnvironTypes.PRIM x) = LambdaTypes.BUILTIN x
| extract_op _ = Crash.impossible "extract_op problem"
fun do_env([], _, le) = le
| do_env(x :: xs, extract_fn, le) =
let
val lexp = extract_op(extract_fn x)
in
LambdaTypes.STRUCT([lexp, do_env(xs, extract_fn, le)],LambdaTypes.TUPLE)
end
val generate_moduler_debug = true
fun pack (topenv as EnvironTypes.TOP_ENV
(EnvironTypes.ENV(mv, ms),
EnvironTypes.FUN_ENV m), decls_list) =
let
val valids = Map.to_list_ordered mv
val strids = Map.to_list_ordered ms
val funids = Map.to_list_ordered m
in
(Environ.assign_fields topenv,
(let_lambdas_in_exp
(decls_list,
do_env(valids, fn (_, x) => x,
do_env(strids, fn (_, (_, x, _)) => x,
do_env(funids, fn (_, (x, _, _)) => x,
LambdaTypes.INT 1))))))
end
fun unpack (EnvironTypes.TOP_ENV (EnvironTypes.ENV (val_map,struct_map),
EnvironTypes.FUN_ENV fun_env),
lambda_expression) =
let
val fun_list = Map.to_list_ordered fun_env
val val_list = Map.to_list_ordered val_map
val struct_list = Map.to_list_ordered struct_map
val main_lvar = LambdaTypes.new_LVar()
val var_main_lvar = LambdaTypes.VAR main_lvar
fun get_new_binding_and_env(bindings, env, get_field, replace_field,
x, start_lvar) =
let
fun sub_fun(bindings, env, [], finish_lvar) = (bindings, env, finish_lvar)
| sub_fun(bindings, env, (x, y) :: rest, entry_lvar) =
let
val (really_is_a_field, field) = get_field y
val lvar = LambdaTypes.new_LVar()
val lvar'' = LambdaTypes.new_LVar()
in
sub_fun
(let
val bindings =
LambdaTypes.LETB(lvar'', NONE,LambdaTypes.SELECT
({index=1, size=2,selecttype=LambdaTypes.TUPLE}, entry_lvar)) ::
bindings
in
if really_is_a_field then
LambdaTypes.LETB
(lvar, NONE,
LambdaTypes.SELECT({index=0, size=2,selecttype=LambdaTypes.TUPLE},
entry_lvar)) ::
bindings
else
bindings
end,
Map.define
(env, x,
if really_is_a_field then
replace_field(y, EnvironTypes.LAMB(lvar,EnvironTypes.NOSPEC))
else y),
rest, LambdaTypes.VAR lvar'')
end
in
sub_fun(bindings, env, x, start_lvar)
end
val main_binding =
LambdaTypes.LETB(main_lvar, NONE, lambda_expression)
val (bindings, val_env, finish_lvar) =
get_new_binding_and_env([], Map.empty(Ident.valid_lt,Ident.valid_eq),
ident, replace_ident, val_list,
var_main_lvar)
val (bindings, struct_env,finish_lvar) =
get_new_binding_and_env(bindings,
Map.empty(Ident.strid_lt,Ident.strid_eq),
get_field_from_strenv,
replace_field_in_strenv, struct_list,
finish_lvar)
val (bindings, fun_env,finish_lvar) =
get_new_binding_and_env(bindings,
Map.empty(Ident.funid_lt,Ident.funid_eq),
get_field_from_funenv,
replace_field_in_funenv, fun_list,
finish_lvar)
val new_fun_env =
EnvironTypes.FUN_ENV fun_env
val new_val_env =
EnvironTypes.ENV(val_env, struct_env)
in
(EnvironTypes.TOP_ENV(new_val_env, new_fun_env),
main_binding :: rev bindings)
end
end;
