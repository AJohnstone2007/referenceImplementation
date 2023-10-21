require "../utils/lists";
require "../lambda/environtypes";
require "../basics/identprint";
require "inter_envtypes";
functor Inter_EnvTypes(
structure Lists : LISTS
structure EnvironTypes : ENVIRONTYPES
structure IdentPrint : IDENTPRINT
sharing IdentPrint.Ident = EnvironTypes.LambdaTypes.Ident
) : INTER_ENVTYPES =
struct
structure NewMap = EnvironTypes.NewMap
structure Ident = EnvironTypes.LambdaTypes.Ident
structure EnvironTypes = EnvironTypes
structure Options = IdentPrint.Options
datatype inter_env =
INTER_ENV of
(Ident.ValId, MLWorks.Internal.Value.ml_value) NewMap.map *
(Ident.StrId, MLWorks.Internal.Value.ml_value) NewMap.map *
(Ident.FunId, MLWorks.Internal.Value.ml_value) NewMap.map
val castit = MLWorks.Internal.Value.cast
val empty_val_map = NewMap.empty (Ident.valid_lt,Ident.valid_eq)
val empty_str_map = NewMap.empty (Ident.strid_lt,Ident.strid_eq)
val empty_fun_map = NewMap.empty (Ident.funid_lt,Ident.funid_eq)
fun lookup_val(valid, INTER_ENV(val_map, _, _)) =
NewMap.apply'(val_map, valid)
fun lookup_str(strid, INTER_ENV(_, str_map, _)) =
NewMap.apply'(str_map, strid)
fun lookup_fun(funid, INTER_ENV(_, _, fun_map)) =
NewMap.apply'(fun_map, funid)
fun add_val(INTER_ENV(val_map, str_map, fun_map), (valid, value)) =
INTER_ENV(NewMap.define(val_map, valid, value), str_map, fun_map)
fun add_str(INTER_ENV(val_map, str_map, fun_map), (strid, value)) =
INTER_ENV(val_map, NewMap.define(str_map, strid, value), fun_map)
fun add_fun(INTER_ENV(val_map, str_map, fun_map), (funid, value)) =
INTER_ENV(val_map, str_map, NewMap.define(fun_map, funid, value))
fun add_val'(INTER_ENV(val_map, str_map, fun_map), valid, value) =
INTER_ENV(NewMap.define(val_map, valid, value), str_map, fun_map)
fun add_str'(INTER_ENV(val_map, str_map, fun_map), strid, value) =
INTER_ENV(val_map, NewMap.define(str_map, strid, value), fun_map)
fun add_fun'(INTER_ENV(val_map, str_map, fun_map), funid, value) =
INTER_ENV(val_map, str_map, NewMap.define(fun_map, funid, value))
fun add_val_list arg =
Lists.reducel
add_val
arg
fun add_str_list arg =
Lists.reducel
add_str
arg
fun add_fun_list arg =
Lists.reducel
add_fun
arg
val empty_env =
INTER_ENV(empty_val_map, empty_str_map, empty_fun_map)
fun remove_str(INTER_ENV(val_map, str_map, fun_map), strid) =
INTER_ENV(val_map, NewMap.undefine(str_map, strid), fun_map)
fun augment(inter_env,
INTER_ENV(val_map, str_map, fun_map)) =
let
val inter_env = NewMap.fold add_val' (inter_env, val_map)
val inter_env = NewMap.fold add_fun' (inter_env, fun_map)
val inter_env = NewMap.fold add_str' (inter_env, str_map)
in
inter_env
end
exception Augment
fun augment_with_module
(inter_env,
EnvironTypes.TOP_ENV (EnvironTypes.ENV (values, structures),
EnvironTypes.FUN_ENV functors),
module) =
let
fun link (values, [], alist, f) = (values, alist)
| link ([], h::_, alist, f) = (print(f h ^ "\n"); raise Augment)
| link (value::values, x::xs, alist, f) =
link (values, xs, (x, value)::alist, f)
val module : MLWorks.Internal.Value.T list = castit module
val (module, value_bindings) = link (module, NewMap.domain_ordered values, [], IdentPrint.debug_printValId)
val (module, structure_bindings) = link (module, NewMap.domain_ordered structures, [], IdentPrint.printStrId)
val (module, functor_bindings) = link (module, NewMap.domain_ordered functors, [], IdentPrint.printFunId)
in
ignore(if module = [] then [] else (print"module not empty\n"; raise Augment));
add_fun_list
(add_str_list
(add_val_list
(inter_env,
value_bindings),
structure_bindings),
functor_bindings)
end
fun print options print (out, INTER_ENV (values, structures, functors)) =
let
val out =
NewMap.fold
(fn (out, valid, _) => print (print (out, " "),
IdentPrint.debug_printValId valid))
(print (out, "values:"), values)
val out = print (out, "\n")
val out =
NewMap.fold
(fn (out, strid, _) => print (print (out, " "), IdentPrint.printStrId strid))
(print (out, "structures:"), structures)
val out = print (out, "\n")
val out =
NewMap.fold
(fn (out, funid, _) => print (print (out, " "), IdentPrint.printFunId funid))
(print (out, "functors:"), functors)
val out = print (out, "\n")
in
out
end
end
;
