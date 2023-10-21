require "../utils/lists";
require "../utils/crash";
require "../lambda/environ";
require "pervasives";
require "primitives";
functor Primitives
(structure Crash : CRASH
structure Lists : LISTS
structure Environ : ENVIRON
structure Pervasives : PERVASIVES
sharing type Pervasives.pervasive = Environ.EnvironTypes.LambdaTypes.Primitive
) : PRIMITIVES =
struct
structure EnvironTypes = Environ.EnvironTypes
structure LambdaTypes = EnvironTypes.LambdaTypes
structure Ident = LambdaTypes.Ident
structure Set = LambdaTypes.Set
structure Symbol = Ident.Symbol
structure NewMap = EnvironTypes.NewMap
val values_for_builtin_library =
[("call_c",Pervasives.CALL_C),
("length", Pervasives.LENGTH),
("unsafe_sub", Pervasives.UNSAFE_SUB),
("unsafe_update", Pervasives.UNSAFE_UPDATE),
("real", Pervasives.REAL),
("int_equal", Pervasives.INTEQ),
("int_greater", Pervasives.INTGREATER),
("int_greater_or_equal", Pervasives.INTGREATEREQ),
("int_less", Pervasives.INTLESS),
("int_less_or_equal", Pervasives.INTLESSEQ),
("int_not_equal", Pervasives.INTNE),
("char_equal", Pervasives.CHAREQ),
("char_greater", Pervasives.CHARGT),
("char_greater_or_equal", Pervasives.CHARGE),
("char_less", Pervasives.CHARLT),
("char_less_or_equal", Pervasives.CHARLE),
("char_not_equal", Pervasives.CHARNE),
("word_equal", Pervasives.WORDEQ),
("word_greater", Pervasives.WORDGT),
("word_greater_or_equal", Pervasives.WORDGE),
("word_less", Pervasives.WORDLT),
("word_less_or_equal", Pervasives.WORDLE),
("word_not_equal", Pervasives.WORDNE),
("unsafe_int_plus", Pervasives.UNSAFEINTPLUS),
("unsafe_int_minus", Pervasives.UNSAFEINTMINUS),
("!", Pervasives.DEREF),
(":=", Pervasives.BECOMES),
("size", Pervasives.SIZE),
("bytearray_unsafe_sub", Pervasives.BYTEARRAY_UNSAFE_SUB),
("bytearray_unsafe_update", Pervasives.BYTEARRAY_UNSAFE_UPDATE),
("vector_length", Pervasives.VECTOR_LENGTH),
("andb", Pervasives.ANDB),
("lshift", Pervasives.LSHIFT),
("notb", Pervasives.NOTB),
("orb", Pervasives.ORB),
("rshift", Pervasives.RSHIFT),
("arshift", Pervasives.ARSHIFT),
("xorb", Pervasives.XORB),
("cast", Pervasives.CAST),
("alloc_string", Pervasives.ALLOC_STRING),
("alloc_vector", Pervasives.ALLOC_VECTOR),
("alloc_pair", Pervasives.ALLOC_PAIR),
("record_unsafe_sub", Pervasives.RECORD_UNSAFE_SUB),
("record_unsafe_update", Pervasives.RECORD_UNSAFE_UPDATE),
("string_unsafe_sub", Pervasives.STRING_UNSAFE_SUB),
("string_unsafe_update", Pervasives.STRING_UNSAFE_UPDATE),
("get_implicit", Pervasives.GET_IMPLICIT)]
val overload_table_for_lambda =
[("_real+", Pervasives.REALPLUS),
("_int+", Pervasives.INTPLUS),
("_int32+", Pervasives.INT32PLUS),
("_word+", Pervasives.WORDPLUS),
("_word32+", Pervasives.WORD32PLUS),
("_real*", Pervasives.REALSTAR),
("_int*", Pervasives.INTSTAR),
("_int32*", Pervasives.INT32STAR),
("_word*", Pervasives.WORDSTAR),
("_word32*", Pervasives.WORD32STAR),
("_real-", Pervasives.REALMINUS),
("_int-", Pervasives.INTMINUS),
("_int32-", Pervasives.INT32MINUS),
("_word-", Pervasives.WORDMINUS),
("_word32-", Pervasives.WORD32MINUS),
("_real~", Pervasives.REALUMINUS),
("_int~", Pervasives.INTUMINUS),
("_int32~", Pervasives.INT32UMINUS),
("_int<", Pervasives.INTLESS),
("_real<", Pervasives.REALLESS),
("_int>", Pervasives.INTGREATER),
("_real>", Pervasives.REALGREATER),
("_int<=", Pervasives.INTLESSEQ),
("_real<=", Pervasives.REALLESSEQ),
("_int>=", Pervasives.INTGREATEREQ),
("_real>=", Pervasives.REALGREATEREQ),
("_int=", Pervasives.INTEQ),
("_int<>", Pervasives.INTNE),
("_real=", Pervasives.REALEQ),
("_real<>", Pervasives.REALNE),
("_string=", Pervasives.STRINGEQ),
("_string<>", Pervasives.STRINGNE),
("_string<", Pervasives.STRINGLT),
("_string>", Pervasives.STRINGGT),
("_string<=", Pervasives.STRINGLE),
("_string>=", Pervasives.STRINGGE),
("_char=", Pervasives.CHAREQ),
("_char<>", Pervasives.CHARNE),
("_char<", Pervasives.CHARLT),
("_char>", Pervasives.CHARGT),
("_char<=", Pervasives.CHARLE),
("_char>=", Pervasives.CHARGE),
("_word=", Pervasives.WORDEQ),
("_word<>", Pervasives.WORDNE),
("_word<", Pervasives.WORDLT),
("_word>", Pervasives.WORDGT),
("_word<=", Pervasives.WORDLE),
("_word>=", Pervasives.WORDGE),
("_word32=", Pervasives.WORD32EQ),
("_word32<>", Pervasives.WORD32NE),
("_word32<", Pervasives.WORD32LT),
("_word32>", Pervasives.WORD32GT),
("_word32<=", Pervasives.WORD32LE),
("_word32>=", Pervasives.WORD32GE),
("_int32=", Pervasives.INT32EQ),
("_int32<>", Pervasives.INT32NE),
("_int32<", Pervasives.INT32LESS),
("_int32>", Pervasives.INT32GREATER),
("_int32<=", Pervasives.INT32LESSEQ),
("_int32>=", Pervasives.INT32GREATEREQ),
("_intabs", Pervasives.INTABS),
("_int32abs", Pervasives.INT32ABS),
("_realabs", Pervasives.REALABS),
("_intmod", Pervasives.INTMOD),
("_intdiv", Pervasives.INTDIV),
("_int32mod", Pervasives.INT32MOD),
("_int32div", Pervasives.INT32DIV),
("_wordmod", Pervasives.WORDMOD),
("_worddiv", Pervasives.WORDDIV),
("_word32mod", Pervasives.WORD32MOD),
("_word32div", Pervasives.WORD32DIV)
]
fun add_cons(env, names) =
Lists.reducel
(fn (env, (name, prim)) =>
Environ.add_valid_env(env, (Ident.CON(Symbol.find_symbol name),
EnvironTypes.PRIM prim)))
(env, names)
fun add_exns(env, names) =
Lists.reducel
(fn (env, (name, prim)) =>
Environ.add_valid_env(env, (Ident.EXCON(Symbol.find_symbol name),
EnvironTypes.PRIM prim)))
(env, names)
fun add_strs(env, names) =
Lists.reducel
(fn (env, (name, prim, inner_env)) =>
Environ.add_strid_env(env, (Ident.STRID(Symbol.find_symbol name),
(inner_env, prim, false))))
(env, names)
fun add_vals(env, names) =
Lists.reducel
(fn (env, (name, prim)) =>
Environ.add_valid_env(env, (Ident.VAR(Symbol.find_symbol name),
EnvironTypes.PRIM prim)))
(env, names)
fun add_overloads env ov_list =
let
val _ = Environ.define_overloaded_ops ov_list
in
add_vals(env, ov_list)
end
val initial_env =
add_vals
(add_exns
(add_cons (Environ.empty_env, map (fn (x,y) => (y,x)) Pervasives.constructor_name_list),
map (fn (x,y) => (y,x)) Pervasives.exception_name_list),
map (fn (x,y) => (y,x)) Pervasives.value_name_list)
val initial_env_for_builtin_library =
add_vals
(add_exns
(add_cons (Environ.empty_env, []), []), values_for_builtin_library)
val builtin_library_strname =
Ident.STRID(Symbol.find_symbol "BuiltinLibrary_")
val env_after_builtin =
Environ.add_strid_env
(Environ.empty_env,
(builtin_library_strname,
(initial_env, EnvironTypes.FIELD {index=0,size=1}, false)))
fun check_builtin_env {error_fn, topenv = EnvironTypes.TOP_ENV (env, _)} =
let
val EnvironTypes.ENV (top_ve, top_se) = env
val flag =
if NewMap.size top_ve <> 0 then
(ignore(error_fn "builtin library defines values at top_level");
false)
else
true
in
case NewMap.tryApply' (top_se, builtin_library_strname)
of SOME (EnvironTypes.ENV (ve, se), _, _) =>
if NewMap.size top_se <> 1 orelse NewMap.size se <> 0 then
(ignore(error_fn "builtin library defines extra structures");
false)
else
let
val EnvironTypes.ENV (initial_ve, _) = initial_env
fun check_new (flag, valId as Ident.VAR sym, _) =
(case NewMap.tryApply' (ve, valId)
of SOME _ => flag
| NONE =>
(ignore(error_fn
("can't find value " ^ Ident.Symbol.symbol_name sym
^ " in builtin library"));
false))
| check_new (flag, valId as Ident.EXCON sym, _) =
(case NewMap.tryApply' (ve, valId)
of SOME _ => flag
| NONE =>
(ignore(error_fn
("can't find exception " ^ Ident.Symbol.symbol_name sym
^ " in builtin library"));
false))
| check_new (flag, _, _) = flag
fun check_internal (flag, valId as Ident.VAR sym, _) =
(case NewMap.tryApply' (initial_ve, valId)
of SOME _ => flag
| NONE =>
(ignore(error_fn
("builtin library defines unknown value " ^
Ident.Symbol.symbol_name sym
^ " - see main._primitives"));
false))
| check_internal (flag, valId as Ident.EXCON sym, _) =
(case NewMap.tryApply' (initial_ve, valId)
of SOME _ => flag
| NONE =>
(ignore(error_fn
("builtin library defines unknown exception " ^
Ident.Symbol.symbol_name sym
^ " - see main._primitives"));
false))
| check_internal (flag, _, _) = flag
val flag = NewMap.fold check_new (flag, initial_ve)
in
NewMap.fold check_internal (flag, ve)
end
| NONE =>
(ignore(error_fn
"builtin library does not define structure BuiltinLibrary_!");
false)
end
local
val ml_non_definable_values =
[("=", Pervasives.EQ)]
val ml_non_definable_overloads =
[("~", Pervasives.UMINUS),
("abs", Pervasives.ABS),
("+", Pervasives.PLUS),
("*", Pervasives.STAR),
("-", Pervasives.MINUS),
("mod", Pervasives.MOD),
("div", Pervasives.DIV),
("<", Pervasives.LESS),
(">", Pervasives.GREATER),
("<=", Pervasives.LESSEQ),
(">=", Pervasives.GREATEREQ)]
in
val env_for_not_ml_definable_builtins =
add_cons
((add_overloads
(add_vals
(add_exns
(add_cons (Environ.empty_env, []), []),
ml_non_definable_values))
ml_non_definable_overloads),
map (fn (x,y) => (y,x)) Pervasives.constructor_name_list)
end
val env_for_lookup_in_lambda =
add_vals(Environ.empty_env,overload_table_for_lambda)
end
;
