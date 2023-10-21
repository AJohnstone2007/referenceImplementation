require "../basis/__int";
require "^.basis.__string";
require "^.system.__os";
require "../utils/lists";
require "../utils/intnewmap";
require "../utils/crash";
require "../utils/print";
require "../utils/diagnostic";
require "../utils/inthashtable";
require "../typechecker/types";
require "../typechecker/typerep_utils";
require "../typechecker/basis";
require "../basics/absynprint";
require "../basics/identprint";
require "../basics/module_id";
require "../match/type_utils";
require "../main/primitives";
require "../main/pervasives";
require "../main/machspec";
require "../rts/gen/implicit";
require "../main/info";
require "../debugger/debugger_types";
require "../match/match";
require "environ";
require "lambdaprint";
require "lambdaoptimiser";
require "lambda";
functor Lambda (
structure Diagnostic : DIAGNOSTIC
structure Lists : LISTS
structure IntHashTable : INTHASHTABLE
structure IntNewMap : INTNEWMAP
structure Crash : CRASH
structure Print: PRINT
structure AbsynPrint : ABSYNPRINT
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure TypeUtils : TYPE_UTILS
structure Basis: BASIS
structure Primitives : PRIMITIVES
structure Pervasives : PERVASIVES
structure MachSpec : MACHSPEC
structure ImplicitVector : IMPLICIT_VECTOR
structure TyperepUtils : TYPEREP_UTILS
structure Info : INFO
structure Debugger_Types : DEBUGGER_TYPES
structure Match : MATCH where type Matchvar = int
structure Environ : ENVIRON
structure LambdaPrint: LAMBDAPRINT
structure LambdaOptimiser: LAMBDAOPTIMISER
sharing LambdaOptimiser.LambdaTypes =
Environ.EnvironTypes.LambdaTypes = LambdaPrint.LambdaTypes
sharing Types.Datatypes = TypeUtils.Datatypes = TyperepUtils.Datatypes =
Basis.BasisTypes.Datatypes = AbsynPrint.Absyn.Datatypes
sharing LambdaPrint.LambdaTypes.Ident = AbsynPrint.Absyn.Ident = IdentPrint.Ident = Types.Datatypes.Ident
sharing Match.Absyn = AbsynPrint.Absyn = TyperepUtils.Absyn
sharing Environ.EnvironTypes = Primitives.EnvironTypes
sharing Info.Location = AbsynPrint.Absyn.Ident.Location
sharing Match.Options = Types.Options = AbsynPrint.Options =
IdentPrint.Options = LambdaPrint.Options
sharing Environ.EnvironTypes.NewMap = Types.Datatypes.NewMap
sharing LambdaPrint.LambdaTypes.Set = AbsynPrint.Absyn.Set
sharing type LambdaPrint.LambdaTypes.FunInfo = Debugger_Types.RuntimeEnv.FunInfo
sharing type LambdaPrint.LambdaTypes.VarInfo = Debugger_Types.RuntimeEnv.VarInfo
sharing type Debugger_Types.RuntimeEnv.RuntimeInfo = AbsynPrint.Absyn.RuntimeInfo
sharing type Environ.EnvironTypes.LambdaTypes.LVar = Match.lvar
sharing type LambdaPrint.LambdaTypes.Primitive = Pervasives.pervasive
sharing type Environ.Structure = Types.Datatypes.Structure = AbsynPrint.Absyn.Structure
sharing type Debugger_Types.Type = LambdaPrint.LambdaTypes.Type
= Types.Datatypes.Type = AbsynPrint.Absyn.Type = Debugger_Types.RuntimeEnv.Type
sharing type Types.Datatypes.InstanceInfo = AbsynPrint.Absyn.InstanceInfo
sharing type Types.Datatypes.Instance = AbsynPrint.Absyn.Instance =
Debugger_Types.RuntimeEnv.Instance
sharing type LambdaPrint.LambdaTypes.Tyfun = Types.Datatypes.Tyfun
= AbsynPrint.Absyn.Tyfun = Debugger_Types.RuntimeEnv.Tyfun
sharing type LambdaPrint.LambdaTypes.DebuggerStr = Types.Datatypes.DebuggerStr
= AbsynPrint.Absyn.DebuggerStr
sharing type LambdaPrint.LambdaTypes.Structure = AbsynPrint.Absyn.Structure
) : LAMBDA =
struct
structure Diagnostic = Diagnostic
structure Absyn = AbsynPrint.Absyn
structure Datatypes = Types.Datatypes
structure BasisTypes = Basis.BasisTypes
structure EnvironTypes = Environ.EnvironTypes
structure LambdaTypes = EnvironTypes.LambdaTypes
structure Ident = IdentPrint.Ident
structure Location = Ident.Location
structure Symbol = Ident.Symbol
structure NewMap = Datatypes.NewMap
structure Set = LambdaTypes.Set
structure Debugger_Types = Debugger_Types
structure Info = Info
structure Options = Types.Options
structure RuntimeEnv = Debugger_Types.RuntimeEnv
type DebugInformation = Debugger_Types.information
val do_exit_stuff = false
val generate_moduler_debug = false
val do_fancy_stuff = false
val show_match = false
val cast : 'a -> 'b = MLWorks.Internal.Value.cast
val unit_exp = LambdaTypes.STRUCT ([],LambdaTypes.TUPLE)
fun valid_symbol (Ident.VAR sy) = sy
| valid_symbol (Ident.CON sy) = sy
| valid_symbol (Ident.EXCON sy) = sy
| valid_symbol (Ident.TYCON' sy) = sy
val function_return_string = "<function return>"
val overload_function_string = "<overload function>"
local
val functor_app_string = "functor app"
in
val is_functor_app = String.isPrefix functor_app_string
val new_LVar = LambdaTypes.new_LVar
fun make_functor_app n =
Symbol.find_symbol ("<" ^ functor_app_string ^ Int.toString n ^ ">")
end
fun funny_name_p name =
((substring(name,0,4) = "<if>" orelse
substring(name,0,5) = "<seq>" orelse
substring(name,0,6) = "<case>" orelse
substring(name,0,8) = "<handle>")
handle Subscript => false)
fun valid_name valid = Symbol.symbol_name (valid_symbol valid)
fun make_short_id name = Ident.VAR (Symbol.find_symbol name)
fun make_longid (path,name) =
let
fun make_path [] = Ident.NOPATH
| make_path (a::l) = Ident.PATH (Symbol.find_symbol a,make_path l)
in
Ident.LONGVALID (make_path path,make_short_id name)
end
fun combine_paths (Ident.NOPATH,p) = p
| combine_paths (Ident.PATH(s,p), p') = Ident.PATH(s,combine_paths (p,p'))
fun select_exn_unique(LambdaTypes.STRUCT([unique, _],_)) = unique
| select_exn_unique lexp =
LambdaTypes.SELECT({index=0, size=2,selecttype = LambdaTypes.CONSTRUCTOR}, lexp)
fun v_order((v1, _), (v2, _)) = Ident.valid_order(v1, v2)
fun s_order((s1, _), (s2, _)) = Ident.strid_order(s1, s2)
fun known_order ((lab1, _, _), (lab2, _, _)) = Ident.lab_order (lab1, lab2)
fun is_list_type (Datatypes.CONSTYPE(_, tyname)) =
Types.tyname_eq(tyname, Types.list_tyname)
| is_list_type _ = false
val dummy_var = new_LVar()
val dummy_varexp = LambdaTypes.VAR dummy_var
val env_reduce =
Lists.reducel
(fn (env, (env', _, _)) => Environ.augment_env(env, env'))
val denv_reduce =
Lists.reducel
(fn (env, (_, env',_)) => Environ.augment_denv(env, env'))
fun env_from_list env_le_list =
env_reduce(Environ.empty_env, env_le_list)
fun denv_from_list env_le_list =
denv_reduce(Environ.empty_denv, env_le_list)
fun constructor_tag (valid,ty) =
(let
val (location,valenv) = TypeUtils.get_valenv(TypeUtils.get_cons_type ty)
in
(location, NewMap.rank'(valenv, valid))
end
handle NewMap.Undefined =>
Crash.impossible("constructor_tag(3): " ^
Types.debug_print_type
Types.Options.default_options ty ^ "," ^
IdentPrint.debug_printValId valid))
fun record_label_offset(lab, the_type,error_info,loc) =
let
fun record_domain(Datatypes.RECTYPE map) = map
| record_domain(Datatypes.METARECTYPE(ref (_,flex,
ty as Datatypes.METARECTYPE _,
_,_))) =
if flex then
record_domain ty
else
Crash.impossible
"contradiction between boolean and type in METARECTYPE 1"
| record_domain(Datatypes.METARECTYPE(ref (_,flex,
ty as Datatypes.RECTYPE _,
_,_))) =
if flex then
let
val Ident.LAB sym = lab
val sym_name = Symbol.symbol_name sym
in
Info.error
error_info
(Info.RECOVERABLE, loc,
"Unresolved record type for label: #" ^ sym_name);
record_domain ty
end
else record_domain ty
| record_domain _ = Crash.impossible ("record_tag(2)")
val record_domain = record_domain the_type
in
{index = NewMap.rank record_domain lab,
size = NewMap.size record_domain,
selecttype = LambdaTypes.TUPLE}
end
fun overloaded_name (Datatypes.FUNTYPE(Datatypes.RECTYPE record_map, _)) =
(case NewMap.range record_map of
ty as Datatypes.META_OVERLOADED (r, tv, valid, loc) :: _ =>
(case Types.the_type (!r) of
Datatypes.CONSTYPE(_, Datatypes.TYNAME{2=s,...}) => s
| Datatypes.CONSTYPE(_, Datatypes.METATYNAME{2=s,...}) => s
| _ => Crash.impossible "overloaded_name (1)")
| Datatypes.CONSTYPE(_, Datatypes.TYNAME{2=s,...}) :: _ => s
| Datatypes.CONSTYPE(_, Datatypes.METATYNAME{2=s,...}) :: _ => s
| _ => Crash.impossible "overloaded_name (2)")
| overloaded_name
(Datatypes.FUNTYPE
(ty as Datatypes.META_OVERLOADED (r, tv, valid, loc), _)) =
(case Types.the_type (!r) of
Datatypes.CONSTYPE(_, Datatypes.TYNAME{2=s,...}) => s
| Datatypes.CONSTYPE(_, Datatypes.METATYNAME{2=s,...}) => s
| _ => Crash.impossible "overloaded_name (3)")
| overloaded_name(Datatypes.FUNTYPE(Datatypes.CONSTYPE(_, tyname), _)) =
(case tyname of
Datatypes.TYNAME{2=s,...} => s
| Datatypes.METATYNAME{2=s,...} => s)
| overloaded_name _ = Crash.impossible "overloaded_name (4)"
fun mk_binop_type t =
Datatypes.FUNTYPE
(Datatypes.RECTYPE
(NewMap.define'
(NewMap.define'(NewMap.empty' Ident.lab_lt, (Ident.LAB(Symbol.find_symbol"1"), t)),
(Ident.LAB(Symbol.find_symbol"2"), t))),
t)
local
fun check_range (abs_min_int, max_word) x =
let
val addition =
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.UNSAFEINTPLUS,
([LambdaTypes.STRUCT ([LambdaTypes.VAR x,
LambdaTypes.SCON
(Ident.INT (abs_min_int, Location.UNKNOWN), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE)
val comparison =
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.WORDGT,
([LambdaTypes.STRUCT ([addition,
LambdaTypes.SCON
(Ident.INT (max_word, Location.UNKNOWN), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE)
in
LambdaTypes.SWITCH
(comparison,
SOME {num_vccs = 0, num_imms = 2},
[(LambdaTypes.IMM_TAG ("", 1),
LambdaTypes.RAISE
(LambdaTypes.STRUCT
([LambdaTypes.BUILTIN Pervasives.EXOVERFLOW, unit_exp],
LambdaTypes.CONSTRUCTOR)))],
SOME (LambdaTypes.VAR x))
end
val check_range_8 = check_range ("128", "255")
val check_range_16 = check_range ("32768", "65535")
fun overloaded_int_op (check_range, result_type) opcode () =
let
val arg = LambdaTypes.new_LVar ()
val tmp = LambdaTypes.new_LVar ()
in
LambdaTypes.FN
(([arg],[]),
LambdaTypes.LET
((tmp,
NONE,
LambdaTypes.APP
(LambdaTypes.BUILTIN opcode,
([LambdaTypes.VAR arg],[]),
NONE)),
check_range (tmp)),
LambdaTypes.BODY,
"<Built in fixed size int operation>",
result_type,
RuntimeEnv.INTERNAL_FUNCTION)
end
in
val int8_op =
overloaded_int_op (check_range_8, mk_binop_type Types.int8_type)
val int16_op =
overloaded_int_op (check_range_16, mk_binop_type Types.int16_type)
end
local
fun clamp_word mask var =
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.WORDANDB,
([LambdaTypes.STRUCT ([LambdaTypes.VAR var,
LambdaTypes.SCON
(Ident.WORD (mask, Location.UNKNOWN), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE)
val clamp_word_8 = clamp_word "0wxff"
val clamp_word_16 = clamp_word "0wxffff"
fun overloaded_word_op (clamp_word, result_type) opcode () =
let
val arg = LambdaTypes.new_LVar ()
val tmp = LambdaTypes.new_LVar ()
in
LambdaTypes.FN
(([arg],[]),
LambdaTypes.LET
((tmp,
NONE,
LambdaTypes.APP
(LambdaTypes.BUILTIN opcode,
([LambdaTypes.VAR arg],[]),
NONE)),
clamp_word (tmp)),
LambdaTypes.BODY,
"<Built in fixed size word operation>",
result_type,
RuntimeEnv.INTERNAL_FUNCTION)
end
in
val word8_op =
overloaded_word_op (clamp_word_8, mk_binop_type Types.word8_type)
val word16_op =
overloaded_word_op (clamp_word_16, mk_binop_type Types.word16_type)
end
val derived_overload_table =
[("_int8+", int8_op Pervasives.UNSAFEINTPLUS),
("_int8-", int8_op Pervasives.UNSAFEINTMINUS),
("_int8*", int8_op Pervasives.INTSTAR),
("_int8div", int8_op Pervasives.INTDIV),
("_int8mod", int8_op Pervasives.INTMOD),
("_int8~", int8_op Pervasives.INTUMINUS),
("_int8abs", int8_op Pervasives.INTABS),
("_int8<", fn () => LambdaTypes.BUILTIN Pervasives.INTLESS),
("_int8<=", fn () => LambdaTypes.BUILTIN Pervasives.INTLESSEQ),
("_int8>", fn () => LambdaTypes.BUILTIN Pervasives.INTGREATER),
("_int8>=", fn () => LambdaTypes.BUILTIN Pervasives.INTGREATEREQ),
("_word8+", word8_op Pervasives.WORDPLUS),
("_word8-", word8_op Pervasives.WORDMINUS),
("_word8*", word8_op Pervasives.WORDSTAR),
("_word8div", word8_op Pervasives.WORDDIV),
("_word8mod", word8_op Pervasives.WORDMOD),
("_word8<", fn () => LambdaTypes.BUILTIN Pervasives.WORDLT),
("_word8<=", fn () => LambdaTypes.BUILTIN Pervasives.WORDLE),
("_word8>", fn () => LambdaTypes.BUILTIN Pervasives.WORDGT),
("_word8>=", fn () => LambdaTypes.BUILTIN Pervasives.WORDGE),
("_int16+", int16_op Pervasives.UNSAFEINTPLUS),
("_int16-", int16_op Pervasives.UNSAFEINTMINUS),
("_int16*", int16_op Pervasives.INTSTAR),
("_int16div", int16_op Pervasives.INTDIV),
("_int16mod", int16_op Pervasives.INTMOD),
("_int16~", int16_op Pervasives.INTUMINUS),
("_int16abs", int16_op Pervasives.INTABS),
("_int16<", fn () => LambdaTypes.BUILTIN Pervasives.INTLESS),
("_int16<=", fn () => LambdaTypes.BUILTIN Pervasives.INTLESSEQ),
("_int16>", fn () => LambdaTypes.BUILTIN Pervasives.INTGREATER),
("_int16>=", fn () => LambdaTypes.BUILTIN Pervasives.INTGREATEREQ),
("_word16+", word16_op Pervasives.WORDPLUS),
("_word16-", word16_op Pervasives.WORDMINUS),
("_word16*", word16_op Pervasives.WORDSTAR),
("_word16div", word16_op Pervasives.WORDDIV),
("_word16mod", word16_op Pervasives.WORDMOD),
("_word16<", fn () => LambdaTypes.BUILTIN Pervasives.WORDLT),
("_word16<=", fn () => LambdaTypes.BUILTIN Pervasives.WORDLE),
("_word16>", fn () => LambdaTypes.BUILTIN Pervasives.WORDGT),
("_word16>=", fn () => LambdaTypes.BUILTIN Pervasives.WORDGE)]
fun lookup_derived_overload s =
SOME (Lists.assoc (s, derived_overload_table))
handle
Lists.Assoc => NONE
fun domain_type_name(Datatypes.FUNTYPE(Datatypes.RECTYPE record_map, _)) =
(case NewMap.range record_map of
h :: _ => (true, Types.the_type h)
| _ => (false, Types.int_type))
| domain_type_name _ = (false, Types.int_type)
fun domain_tyname(Datatypes.CONSTYPE(_, tyname)) = (true, tyname)
| domain_tyname _ = (false, Types.int_tyname)
fun check_no_vcc_for_eq(h as Datatypes.CONSTYPE _) =
TypeUtils.has_null_cons h andalso not(TypeUtils.has_value_cons h)
| check_no_vcc_for_eq _ = false
fun check_one_vcc_and_no_nullaries(h as Datatypes.CONSTYPE _) =
not(TypeUtils.has_null_cons h) andalso TypeUtils.get_no_vcc_cons h = 1
| check_one_vcc_and_no_nullaries _ = false
fun GetConTag lexp =
LambdaTypes.SELECT({index = 0, size = 2,selecttype=LambdaTypes.CONSTRUCTOR}, lexp)
fun GetConVal lexp =
LambdaTypes.SELECT ({index = 1, size = 2,selecttype=LambdaTypes.CONSTRUCTOR}, lexp)
fun get_lamb_env(strid as Ident.STRID sy, env) =
let
val (env', comp, _) = Environ.lookup_strid(strid, env)
in
case comp of
EnvironTypes.LAMB(lvar,_) => (env', LambdaTypes.VAR lvar)
| EnvironTypes.PRIM prim => (env', LambdaTypes.BUILTIN prim)
| EnvironTypes.EXTERNAL =>
(env',
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.LOAD_STRUCT,
([LambdaTypes.SCON (Ident.STRING (Symbol.symbol_name sy), NONE)],[]),
NONE))
| EnvironTypes.FIELD{index, size} =>
Crash.impossible "get_lamb_env gives field"
end
fun get_lamb_env'(strid as Ident.STRID sy, env) =
let
val (env', comp, moduler_generated) = Environ.lookup_strid(strid, env)
in
case comp of
EnvironTypes.LAMB (lvar,longstrid) => (env', LambdaTypes.VAR lvar, longstrid, moduler_generated)
| EnvironTypes.PRIM prim => (env', LambdaTypes.BUILTIN prim, EnvironTypes.NOSPEC, moduler_generated)
| EnvironTypes.EXTERNAL =>
(env',
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.LOAD_STRUCT,
([LambdaTypes.SCON(Ident.STRING(Symbol.symbol_name sy), NONE)],[]),
NONE),
EnvironTypes.NOSPEC, moduler_generated)
| EnvironTypes.FIELD _ =>
Crash.impossible "get_lamb_env gives field"
end
fun make_struct_select {index,size} =
{index = index,size = size, selecttype = LambdaTypes.STRUCTURE}
fun get_field_env(strid, (env, lambda)) =
let
val (env', field) =
case Environ.lookup_strid(strid, env) of
(env'', EnvironTypes.FIELD field, _) => (env'', field)
| _ => Crash.impossible "get_field_env fails to get field"
in
(env', LambdaTypes.SELECT(make_struct_select field, lambda))
end
fun get_field_env'(strid, (env, lambda, longstrid, moduler_generated)) =
let
val (env', field, _) =
case Environ.lookup_strid(strid, env) of
(env'', EnvironTypes.FIELD field, moduler_generated) => (env'', field, moduler_generated)
| _ => Crash.impossible "get_field_env fails to get field"
in
(env', LambdaTypes.SELECT(make_struct_select field, lambda), longstrid, moduler_generated)
end
fun cg_longvalid (longvalid, env) =
case longvalid of
Ident.LONGVALID(Ident.NOPATH, valid) =>
((case Environ.lookup_valid(valid, env) of
EnvironTypes.LAMB(lvar,_) => LambdaTypes.VAR lvar
| EnvironTypes.PRIM prim =>
LambdaTypes.BUILTIN prim
| EnvironTypes.EXTERNAL =>
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.LOAD_VAR,
([LambdaTypes.SCON(Ident.STRING(valid_name valid), NONE)],[]),
NONE)
| EnvironTypes.FIELD _ => Crash.impossible "cg_longvalid gives field")
handle NewMap.Undefined =>
Crash.impossible
(IdentPrint.debug_printValId valid ^ " undefined in cg_longvalid"))
| Ident.LONGVALID(path, valid) =>
let
val (env', lambda) =
Ident.followPath'(get_lamb_env, get_field_env) (path, env)
in
(case Environ.lookup_valid(valid, env') of
EnvironTypes.FIELD field => LambdaTypes.SELECT(make_struct_select field, lambda)
| EnvironTypes.PRIM prim => LambdaTypes.BUILTIN prim
| EnvironTypes.LAMB _ =>
Crash.impossible "cg_longvalid gets lambda var at end of longvalid"
| EnvironTypes.EXTERNAL =>
Crash.impossible "cg_longvalid gets external at end of longvalid")
handle NewMap.Undefined =>
Crash.impossible
(IdentPrint.debug_printValId valid
^ " undefined in cg_longvalid")
end
fun cg_longexid (longvalid, env) =
case longvalid of
Ident.LONGVALID(Ident.NOPATH, valid) =>
(case Environ.lookup_valid(valid, env) of
EnvironTypes.LAMB (lvar,longstrid) => (LambdaTypes.VAR lvar,longstrid)
| EnvironTypes.PRIM prim =>
(LambdaTypes.BUILTIN prim,
EnvironTypes.NOSPEC)
| EnvironTypes.EXTERNAL =>
(LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.LOAD_VAR,
([LambdaTypes.SCON(Ident.STRING(valid_name valid), NONE)],[]),
NONE),
EnvironTypes.NOSPEC)
| EnvironTypes.FIELD _ => Crash.impossible "cg_longexid gives field")
| Ident.LONGVALID(path, valid) =>
let
val (env', lambda, longstrid, _) =
Ident.followPath'(get_lamb_env', get_field_env') (path, env)
in
(case Environ.lookup_valid(valid, env') of
EnvironTypes.FIELD field => LambdaTypes.SELECT(make_struct_select field, lambda)
| EnvironTypes.PRIM prim => LambdaTypes.BUILTIN prim
| EnvironTypes.LAMB _ =>
Crash.impossible "cg_longexid gets lambda var at end of longvalid"
| EnvironTypes.EXTERNAL =>
Crash.impossible "cg_longexid gets external at end of longvalid",
longstrid)
end
fun cg_longstrid (longstrid, environment) =
case longstrid of
Ident.LONGSTRID(Ident.NOPATH, strid as Ident.STRID sy) =>
(case Environ.lookup_strid(strid, environment) of
(env, EnvironTypes.LAMB (lvar,longstrid), moduler_generated) =>
(env, LambdaTypes.VAR lvar, longstrid, moduler_generated)
| (env, EnvironTypes.PRIM prim, moduler_generated) =>
(env, LambdaTypes.BUILTIN prim, EnvironTypes.NOSPEC, moduler_generated)
| (env, EnvironTypes.EXTERNAL, moduler_generated) =>
(env,
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.LOAD_STRUCT,
([LambdaTypes.SCON(Ident.STRING(Symbol.symbol_name sy), NONE)],[]),
NONE),
EnvironTypes.NOSPEC,
moduler_generated)
| (_, EnvironTypes.FIELD _, _) =>
Crash.impossible "cg_longstrid gives field")
| Ident.LONGSTRID(path, strid) =>
let
val (env, lambda, longstrid, moduler_generated) =
Ident.followPath'(get_lamb_env', get_field_env') (path, environment)
in
case Environ.lookup_strid(strid, env) of
(env', EnvironTypes.FIELD field, _) =>
(env', LambdaTypes.SELECT(make_struct_select field, lambda), longstrid, moduler_generated)
| (env', EnvironTypes.PRIM prim, _) =>
(env', LambdaTypes.BUILTIN prim, longstrid, moduler_generated)
| (_, EnvironTypes.LAMB _, _) =>
Crash.impossible "cg_longstrid gets lambda var at end of longstrid"
| (_, EnvironTypes.EXTERNAL, _) =>
Crash.impossible "cg_longstrid gets external at end of longstrid"
end
val eq_prim = LambdaTypes.BUILTIN Pervasives.EQ
fun is_eq_prim(LambdaTypes.BUILTIN Pervasives.EQ) = true
| is_eq_prim _ = false
fun isnt_eq_prim(LambdaTypes.BUILTIN Pervasives.EQ) = false
| isnt_eq_prim _ = true
val neq_prim = LambdaTypes.BUILTIN Pervasives.NE
fun isnt_neq_prim(LambdaTypes.BUILTIN Pervasives.NE) = false
| isnt_neq_prim _ = true
fun let_lambdas_in_exp (bindings, lambda_exp) =
Lists.reducer LambdaTypes.do_binding (bindings,lambda_exp)
fun unordered_let_lambdas_in_exp (bindings, lambda_exp) =
Lists.reducel (fn (exp, bind) => LambdaTypes.do_binding(bind, exp))
(lambda_exp, bindings)
fun print_redundancy_info (print_options,[], _) = Crash.impossible "print_redundancy_info:lambda"
| print_redundancy_info (print_options,clauses, pat_exp_list) =
let
fun dynamic_member (_,nil) = false
| dynamic_member (n,(n',_)::_) = n=n'
fun static_member (_,nil) = false
| static_member (n,(n',Match.TRUE)::_) = n=n'
| static_member _ = false
fun new_clauses nil = Crash.impossible "new_clauses:print_redundancy_info:lambda"
| new_clauses (_::clauses) = clauses
fun fetch_clause nil = Crash.impossible "fetch_clause:print_redundancy_info:lambda"
| fetch_clause (clause::_) = clause
fun to_string(n, [], _, ((b,static_str),(b',dynamic_str))) =
((b,rev static_str), (b',rev dynamic_str))
| to_string(n, (p,_,_)::l,clauses,((static,static_str),(dynamic,dynamic_str))) =
let
val pat_str = AbsynPrint.unparsePat true print_options p ^ " => ..."
in
if static_member (n, clauses) then
to_string(n+1, l, new_clauses clauses,
((true,"\n  " ^ pat_str::static_str),
(dynamic,(fetch_clause clauses,"\n      " ^ pat_str)::dynamic_str)))
else
if dynamic_member (n, clauses) then
to_string(n+1, l, new_clauses clauses,
((static, static_str),
(true,(fetch_clause clauses,"\n      " ^ pat_str)::dynamic_str)))
else
to_string(n+1, l, clauses,
((static, static_str),
(dynamic,((n,Match.FALSE),"\n      " ^ pat_str)::dynamic_str)))
end
in
to_string(1, pat_exp_list, clauses, ((false,[]), (false,[])))
end
fun compare_sig_env generate_moduler (Datatypes.COPYSTR (_, str), env) =
compare_sig_env generate_moduler (str, env)
| compare_sig_env generate_moduler (Datatypes.STR (_, _, Datatypes.ENV
(Datatypes.SE sm,
Datatypes.TE tm,
Datatypes.VE (_, vm))),
EnvironTypes.ENV(v_map, s_map)) =
NewMap.size vm + (if generate_moduler then NewMap.size tm else 0) = NewMap.size v_map andalso
NewMap.size sm = NewMap.size s_map andalso
Lists.forall
(compare_sig_env generate_moduler)
(Lists.zip(NewMap.range_ordered sm,
map #1 (NewMap.range_ordered s_map)))
fun complete_struct_with_sig (Datatypes.COPYSTR (_, str), env, lv, coerce, generate_moduler) =
complete_struct_with_sig (str, env, lv, coerce, generate_moduler)
| complete_struct_with_sig
(interface as Datatypes.STR (_, _,
Datatypes.ENV (Datatypes.SE sm,
Datatypes.TE tm,
Datatypes.VE (_, vm))),
env as EnvironTypes.ENV(v_map, s_map),
lambda_var,
coerce,
generate_moduler) =
(if not coerce andalso compare_sig_env generate_moduler (interface, env)
then (env, LambdaTypes.VAR lambda_var)
else
let
val v_list = NewMap.to_list_ordered v_map
val s_list = NewMap.to_list_ordered s_map
val ordered_int_map = NewMap.to_list_ordered sm
val vm =
if generate_moduler then
let
val dummy_scheme = Datatypes.UNBOUND_SCHEME(Datatypes.NULLTYPE,NONE)
in
NewMap.fold
(fn (map,Ident.TYCON sym,_) =>
NewMap.define (map,Ident.TYCON' sym, dummy_scheme))
(vm,tm)
end
else vm
val (v_f_list,s_f_list,_) =
let
fun val_filter_map (v_list, [], done) = rev done
| val_filter_map ([], z :: _, done) =
Crash.impossible(IdentPrint.debug_printValId z ^ " missing in val_filter_map")
| val_filter_map((x, y)::xs, second as (z :: zs), done) =
if Ident.valid_eq (x, z)
then val_filter_map(xs, zs, (x, y) :: done)
else val_filter_map(xs, second, done)
fun str_filter_map (a_list, [], done) = rev done
| str_filter_map ([], x :: _, done) =
Crash.impossible(IdentPrint.printStrId x ^ " missing in str_filter_map")
| str_filter_map((x, y)::xs, second as (z :: zs), done) =
if Ident.strid_eq (x, z)
then str_filter_map(xs, zs, (x, y) :: done)
else str_filter_map(xs, second, done)
val domain = NewMap.domain_ordered vm
in
Environ.number_envs
(val_filter_map (v_list, domain,[]),
str_filter_map (s_list, map #1 ordered_int_map,[]),
[])
end
val v_f_l_list =
map (fn x => (x, new_LVar())) v_f_list
val s_f_l_list =
map (fn x => (x, new_LVar(), new_LVar()))
s_f_list
val the_structure_list =
map (fn (_, x) => LambdaTypes.VAR x) v_f_l_list @
map (fn (_, x, _) => LambdaTypes.VAR x) s_f_l_list
val env_le_list =
map complete_struct_with_sig
(map (fn ((((_, (env, _, _)), _), _, l2), (_, inte)) =>
(inte, env, l2, coerce, generate_moduler))
(Lists.zip(s_f_l_list, ordered_int_map)))
fun keep_prims (x as EnvironTypes.PRIM _) _ = x
| keep_prims _ x = x
val env =
Lists.reducel
(fn (env, ((v, x), f_new)) =>
Environ.add_valid_env(env, (v, keep_prims x f_new)))
(Lists.reducel
(fn (env, (((strid, _), field), (env', _))) =>
Environ.add_strid_env(env, (strid, (env', field, generate_moduler))))
(Environ.empty_env, (Lists.zip(s_f_list, env_le_list))),
v_f_list)
fun coerce (valid as Ident.EXCON _, lexp) =
let
val res = NewMap.tryApply'Eq (vm, valid)
val (need_coerce, res') =
case res of
SOME ty => (false, res)
| _ => (true, NewMap.tryApply' (vm, valid))
in
if need_coerce then
let
val _ = Diagnostic.output 2 (fn _ => ["coercing ",IdentPrint.debug_printValId valid])
val is_vcc =
case res' of
SOME ty =>
TypeUtils.is_vcc (TypeUtils.type_from_scheme ty)
| _ => Crash.impossible "coerce:_lambda"
in
if is_vcc then
let
val lv = new_LVar()
in
LambdaTypes.FN(([lv],[]),
LambdaTypes.STRUCT([lexp, LambdaTypes.VAR lv],LambdaTypes.CONSTRUCTOR),
LambdaTypes.BODY,
"Builtin code to construct an exception",
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION)
end
else LambdaTypes.STRUCT([lexp, unit_exp],LambdaTypes.CONSTRUCTOR)
end
else lexp
end
| coerce (_, lexp) = lexp
val l1 =
map
(fn (((valid, EnvironTypes.FIELD f_old), _), lv) =>
LambdaTypes.LETB
(lv,NONE,
coerce (valid, LambdaTypes.SELECT
(make_struct_select f_old, LambdaTypes.VAR lambda_var)))
| (((valid, EnvironTypes.PRIM prim), _), lv) =>
LambdaTypes.LETB(lv,NONE,coerce (valid,LambdaTypes.BUILTIN prim))
| (((_, EnvironTypes.LAMB _), _), _) =>
Crash.impossible "c_s_w_i(1)"
| (((_, EnvironTypes.EXTERNAL), _), _) =>
Crash.impossible "c_s_w_i(2)")
v_f_l_list
val l2 =
map
(fn ((((_, (env, EnvironTypes.FIELD f_old, _)), _), lv, lv'), le) =>
LambdaTypes.LETB
(lv,NONE,
LambdaTypes.do_binding
(LambdaTypes.LETB
(lv',NONE,
LambdaTypes.SELECT(make_struct_select f_old, LambdaTypes.VAR lambda_var)),
le))
| ((((_, (_, EnvironTypes.LAMB _, _)), _), _, _), _) =>
Crash.impossible "c_s_w_i (3) LAMB"
| ((((_, (_, EnvironTypes.PRIM _, _)), _), _, _), _) =>
Crash.impossible "c_s_w_i (4) PRIM"
| ((((_, (_, EnvironTypes.EXTERNAL, _)), _), _, _), _) =>
Crash.impossible "c_s_w_i (5) EXTERNAL")
(Lists.zip(s_f_l_list, map #2 env_le_list))
val lambdas = let_lambdas_in_exp(l1 @ l2,
LambdaTypes.STRUCT (the_structure_list,LambdaTypes.STRUCTURE))
in
(env, lambdas)
end)
fun complete_struct_from_topenv(topenv as EnvironTypes.TOP_ENV(
EnvironTypes.ENV(mv, ms), EnvironTypes.FUN_ENV m), lv_le_list) =
let
val valids = NewMap.to_list_ordered mv
val strids = NewMap.to_list_ordered ms
val funids = NewMap.to_list_ordered m
fun extract_op (EnvironTypes.LAMB (x,_)) = LambdaTypes.VAR x
| extract_op (EnvironTypes.PRIM x) = LambdaTypes.BUILTIN x
| extract_op (EnvironTypes.FIELD _) =
Crash.impossible "extract_op problem (1)"
| extract_op EnvironTypes.EXTERNAL =
Crash.impossible "extract_op problem (2)"
in
(Environ.assign_fields topenv,
let_lambdas_in_exp(lv_le_list,
LambdaTypes.STRUCT((map (fn (_, x) => extract_op x) valids) @
(map (fn (_, (_, x, _)) => extract_op x) strids) @
(map (fn (_,(x, _, _)) => extract_op x) funids),
LambdaTypes.STRUCTURE)))
end
fun make_top_env env = EnvironTypes.TOP_ENV(env, Environ.empty_fun_env)
fun complete_struct((env, lambda_exp: LambdaTypes.LambdaExp),
interface_opt,
coerce, generate_moduler) =
let
val EnvironTypes.TOP_ENV(new_env, new_fun_env) =
Environ.assign_fields(make_top_env env)
val result = (new_env, lambda_exp)
in
case interface_opt of
NONE => result
| SOME interface =>
if not coerce andalso compare_sig_env generate_moduler (interface, new_env)
then result
else
let
val new_lv = new_LVar()
val (new_env', new_lambda') =
complete_struct_with_sig(interface, new_env, new_lv, coerce, generate_moduler)
in
(new_env',
LambdaTypes.do_binding(LambdaTypes.LETB(new_lv,NONE,
lambda_exp),
new_lambda'))
end
end
fun interface_from_sigexp (Absyn.NEWsigexp(_, ref (SOME str))) = str
| interface_from_sigexp (Absyn.OLDsigexp(_, ref (SOME str),_)) = str
| interface_from_sigexp (Absyn.WHEREsigexp (sigexp,_)) = interface_from_sigexp sigexp
| interface_from_sigexp _ = Crash.impossible "No interface structure for signature"
type MatchEnv = (LambdaTypes.LVar * LambdaTypes.VarInfo ref option) IntNewMap.T
val empty_match_env = IntNewMap.empty
fun add_match_env(pair, me) = IntNewMap.define'(me, pair)
fun lookup_match(mv, me) = IntNewMap.apply'(me, mv)
val functor_refs_ct : int ref = ref 0
val functor_refs : (EnvironTypes.Foo ref * Datatypes.Structure) list ref = ref []
fun trans_top_dec
error_info
(options as Options.OPTIONS
{print_options,
compiler_options = Options.COMPILEROPTIONS
{generate_debug_info,
debug_variables,
generate_moduler, ...},
compat_options = Types.Options.COMPATOPTIONS {old_definition,...},
...},
topdec,
top_env as EnvironTypes.TOP_ENV(env, _),
top_denv,
initial_debugger_env,
basis,batch_compiler) =
let
val use_value_polymorphism = not old_definition
val generate_moduler = do_fancy_stuff andalso generate_moduler
val redundant_exceptions_ref : (LambdaTypes.LVar * string) list ref = ref []
val dynamic_redundancy_report_ref : (LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp) ref = ref(fn exp => exp)
val variable_debug = debug_variables orelse generate_moduler
val null_runtimeinfo = RuntimeEnv.RUNTIMEINFO (NONE,nil)
fun dummy_instance () = (ref Datatypes.NULLTYPE, ref null_runtimeinfo)
fun mklongvalid valid = Ident.LONGVALID (Ident.NOPATH,valid)
fun new_tyvar_slot () = ref (RuntimeEnv.OFFSET1 0)
fun do_moduler_debug message =
if generate_moduler_debug then
print ("  # " ^ message() ^ "\n")
else
()
fun lookup f =
let
fun aux (Ident.NOPATH,result) = f result
| aux (Ident.PATH(sym,path), EnvironTypes.DENVEXP(EnvironTypes.DENV(_,strmap))) =
aux (path, NewMap.apply strmap (Ident.STRID sym)
handle NewMap.Undefined =>
(do_moduler_debug (fn () =>"UNDEFINED 14:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined))
| aux (Ident.PATH(sym,path),
EnvironTypes.LAMBDASTREXP(selects,lv,
Datatypes.STR(_,_,Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap))))) =
let
val offset = NewMap.size tyconmap + NewMap.size validmap
in
aux(path,
EnvironTypes.LAMBDASTREXP({index= NewMap.rank' (stridmap,Ident.STRID sym) + offset,
size= NewMap.size stridmap + offset}
::selects,lv,
NewMap.apply stridmap (Ident.STRID sym)))
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 3:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined)
end
| aux (path,EnvironTypes.LAMBDASTREXP(selects,lv,Datatypes.COPYSTR(_,str))) =
aux (path, EnvironTypes.LAMBDASTREXP(selects,lv,str))
| aux (Ident.PATH(sym,path),
EnvironTypes.LAMBDASTREXP'(selects,lv,
Datatypes.STR(_,_,Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap))))) =
let
val offset = NewMap.size tyconmap + NewMap.size validmap
in
aux(path,
EnvironTypes.LAMBDASTREXP'({index=
NewMap.rank' (stridmap,Ident.STRID sym) + offset,
size= NewMap.size stridmap + offset}
::selects,lv,
NewMap.apply stridmap (Ident.STRID sym)))
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 3:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined)
end
| aux(path,EnvironTypes.LAMBDASTREXP'(selects,lv,Datatypes.COPYSTR(_,str))) =
aux(path, EnvironTypes.LAMBDASTREXP'(selects,lv,str))
in
aux
end
fun lookup_sym sym =
let
fun aux (EnvironTypes.DENVEXP(EnvironTypes.DENV(validmap,_))) =
(NewMap.apply validmap (Ident.VAR sym)
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 5:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined))
| aux (EnvironTypes.LAMBDASTREXP(selects,lv,
Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap))))) =
let
val offset = NewMap.size tyconmap
in
EnvironTypes.LAMBDAEXP({index= NewMap.rank' (validmap,Ident.VAR sym) + offset,
size=NewMap.size validmap + NewMap.size stridmap + offset} ::
selects,
lv,NONE)
end
| aux (EnvironTypes.LAMBDASTREXP(selects,lv,Datatypes.COPYSTR(_,str))) =
aux (EnvironTypes.LAMBDASTREXP(selects,lv,str))
| aux (EnvironTypes.LAMBDASTREXP'(selects,lv,
Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap))))) =
let
val offset = NewMap.size tyconmap
in
EnvironTypes.LAMBDAEXP'({index= NewMap.rank' (validmap,Ident.VAR sym) + offset,
size=NewMap.size validmap + NewMap.size stridmap + offset}
::selects,lv,NONE)
end
| aux(EnvironTypes.LAMBDASTREXP'(selects,lv,Datatypes.COPYSTR(_,str))) =
aux(EnvironTypes.LAMBDASTREXP'(selects,lv,str))
in
aux
end
fun dlookup_longvalid (longvalid, denv) =
case longvalid of
Ident.LONGVALID(Ident.NOPATH, valid as Ident.VAR sym) =>
(Environ.lookup_valid'(valid, denv)
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 13:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined))
| Ident.LONGVALID(Ident.PATH(sym,path), valid as Ident.VAR sym') =>
(case denv of
EnvironTypes.DENV(_,strmap) =>
lookup (lookup_sym sym')
(path, NewMap.apply strmap (Ident.STRID sym)
handle NewMap.Undefined =>
(do_moduler_debug
(fn () =>
"UNDEFINED 4:" ^ Symbol.symbol_name sym ^ ".." ^ Symbol.symbol_name sym' ^ "..."
^ NewMap.fold (fn (str,Ident.STRID sym,_) => str ^ "," ^ Symbol.symbol_name sym) ("",strmap));
raise NewMap.Undefined)))
| _ => Crash.impossible "dlookup_longvalid:lambda"
local
val dummy_tf = ref(Datatypes.TYFUN(Datatypes.NULLTYPE,0))
fun fetch_nulltyfun (Datatypes.METATYNAME{1=tf as ref(Datatypes.NULL_TYFUN _), ...}) = tf
| fetch_nulltyfun (Datatypes.METATYNAME{1=ref(Datatypes.ETA_TYFUN m), ...}) =
fetch_nulltyfun m
| fetch_nulltyfun _ = Crash.impossible "fetch_nulltyfun:lambda"
in
fun fetch_ntf (Datatypes.TYSTR(tf,_)) =
if Types.null_tyfunp tf then
fetch_nulltyfun(Types.meta_tyname tf)
else
dummy_tf
end
fun dlookup_tycon (tyfun,denv) =
let
exception Lookup
fun denv_lookup (EnvironTypes.DENV(id_map, str_map)) =
let
fun lookup_id map =
let
fun aux nil = NONE
| aux ((_,lexp as EnvironTypes.LAMBDAEXP(_,_,SOME(tyfun')))::rest) =
if tyfun = tyfun'
then SOME lexp
else aux rest
| aux (_::rest) = aux rest
in
aux (NewMap.to_list map)
end
in
case lookup_id id_map of
SOME lexp => lexp
| _ =>
let
fun strexp_lookup (EnvironTypes.DENVEXP denv) = denv_lookup denv
| strexp_lookup (EnvironTypes.LAMBDASTREXP (selects,lv,str)) =
EnvironTypes.LAMBDAEXP(rev (str_lookup str) @ selects,lv,NONE)
| strexp_lookup (EnvironTypes.LAMBDASTREXP'(selects,lv,str)) =
EnvironTypes.LAMBDAEXP' (rev (str_lookup str) @ selects,lv,NONE)
fun aux nil = raise Lookup
| aux ((_,strexp)::rest) =
(strexp_lookup strexp
handle Lookup => aux rest)
in
aux (NewMap.to_list str_map)
end
end
and str_lookup(Datatypes.COPYSTR(_,str)) = str_lookup str
| str_lookup(Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE se_map,
Datatypes.TE te_map,
Datatypes.VE(_,ve_map)))) =
let
val size' = NewMap.size te_map + NewMap.size ve_map
val size = NewMap.size se_map + size'
fun find_tyfun nil _ = NONE
| find_tyfun ((_,tystr)::rest) n =
if tyfun = fetch_ntf tystr then SOME n
else find_tyfun rest (n+1)
in
case find_tyfun (NewMap.to_list_ordered te_map) 0 of
SOME n =>
[{index = n, size = size}]
| _ =>
let
val se_list = NewMap.to_list_ordered se_map
fun find_str (nil, _) = raise Lookup
| find_str ((_,str)::rest, n) =
{index = n + size', size = size} :: str_lookup str
handle Lookup => find_str (rest,n+1)
in
find_str (se_list, 0)
end
end
in
denv_lookup denv
end
fun cg_longstrid' (longstrid, denv) =
case longstrid of
Ident.LONGSTRID(Ident.NOPATH, strid as Ident.STRID sym) =>
(Environ.lookup_strid'(strid, denv)
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 12:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined))
| Ident.LONGSTRID(Ident.PATH(sym,path), strid as Ident.STRID sy) =>
let
fun insert_strid Ident.NOPATH = Ident.PATH (sy,Ident.NOPATH)
| insert_strid (Ident.PATH (sym,path)) = Ident.PATH (sym,insert_strid path)
in
case denv of
EnvironTypes.DENV(_,strmap) =>
lookup (fn result => result) (insert_strid path, NewMap.apply strmap (Ident.STRID sym))
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"UNDEFINED 11:" ^ Symbol.symbol_name sym);
raise NewMap.Undefined)
end
fun open_debugger_env (debugger_strexp, denv) =
let
fun open_lambdastrexp LAMBDAEXP LAMBDASTREXP
(selects,lv,
Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap)))) =
let
val size_validmap = NewMap.size validmap
val size_tyconmap = NewMap.size tyconmap
val size = NewMap.size stridmap + size_validmap + size_tyconmap
in
NewMap.fold
(fn (env, tc' as Ident.TYCON tc, tystr) =>
Environ.add_valid_denv
(env, (Ident.TYCON' tc, LAMBDAEXP({index=NewMap.rank' (tyconmap,tc'),
size=size}
::selects,lv,
SOME(fetch_ntf tystr)))))
(NewMap.fold
(fn (env, s, str) =>
Environ.add_strid_denv
(env, (s, LAMBDASTREXP({index=NewMap.rank' (stridmap,s) + size_validmap + size_tyconmap,
size=size}
::selects,lv,
str))))
((NewMap.fold
(fn (env, v, _) =>
Environ.add_valid_denv
(env, (v, LAMBDAEXP({index=NewMap.rank' (validmap,v) + size_tyconmap,
size=size}
::selects,lv,NONE))))
(denv, validmap)), stridmap), tyconmap)
end
| open_lambdastrexp _ LAMBDASTREXP(selects,lv,Datatypes.COPYSTR(_,str)) =
open_debugger_env (LAMBDASTREXP(selects,lv,str), denv)
in
case debugger_strexp of
EnvironTypes.DENVEXP(denv') => Environ.augment_denv(denv, denv')
| EnvironTypes.LAMBDASTREXP args =>
open_lambdastrexp EnvironTypes.LAMBDAEXP EnvironTypes.LAMBDASTREXP args
| EnvironTypes.LAMBDASTREXP' args =>
open_lambdastrexp EnvironTypes.LAMBDAEXP' EnvironTypes.LAMBDASTREXP' args
end
val dlookup_longvalid =
if generate_moduler then
fn denv => dlookup_longvalid denv
handle NewMap.Undefined => EnvironTypes.NULLEXP
else
fn _ => EnvironTypes.NULLEXP
val empty_denv = Environ.empty_denv
val empty_dstrexp = EnvironTypes.DENVEXP empty_denv
val cg_longstrid' =
if generate_moduler then
fn denv =>
cg_longstrid' denv
handle NewMap.Undefined => empty_dstrexp
else
fn _ => empty_dstrexp
val add_valid_denv =
if generate_moduler then
fn arg => Environ.add_valid_denv arg
else
fn _ => empty_denv
val add_strid_denv =
if generate_moduler then
fn arg => Environ.add_strid_denv arg
else
fn _ => empty_denv
val augment_denv =
if generate_moduler then
fn arg => Environ.augment_denv arg
else
fn _ => empty_denv
val new_dLVar =
if generate_moduler then fn _ => new_LVar()
else
fn lvar => lvar
fun sub_functor_refs (EnvironTypes.TOP_ENV (env',fun_env)) =
let
val env_list =
case env of
EnvironTypes.ENV (env,_) => NewMap.to_list env
fun sub_functor_refs ([], env) = EnvironTypes.TOP_ENV (env,fun_env)
| sub_functor_refs ((entry as (Ident.VAR sym,comp))::rest, env) =
let
val name_string = Symbol.symbol_name sym
val new_env =
if is_functor_app name_string
then
case comp of
EnvironTypes.LAMB(lvar,_) =>
Environ.add_valid_env (env,entry)
| _ => env
else
env
in
sub_functor_refs (rest,new_env)
end
| sub_functor_refs (_::rest, env) = sub_functor_refs (rest, env)
in
sub_functor_refs (env_list,env')
end
val (overload_exp,overload_binding,make_env) =
if generate_moduler then
(cg_longvalid (mklongvalid (Ident.VAR (Symbol.find_symbol overload_function_string)),env),[],
fn env => env)
handle NewMap.Undefined =>
(do_moduler_debug(fn () =>"WARNING : redefining overload function");
let
val lvar = new_LVar()
val lv = new_LVar()
val args = new_LVar()
val new_cg = new_LVar()
val dexp' = new_LVar()
val instance_var = new_LVar()
val lexp =
LambdaTypes.FN
(([args],[]),
LambdaTypes.LET((new_cg,NONE,
LambdaTypes.SELECT({index=0,size=3,selecttype=LambdaTypes.TUPLE},
LambdaTypes.VAR args)),
LambdaTypes.LET((dexp',NONE,
LambdaTypes.SELECT({index=1,size=3,selecttype=LambdaTypes.TUPLE},
LambdaTypes.VAR args)),
LambdaTypes.LET((instance_var,NONE,
LambdaTypes.SELECT({index=2,size=3,selecttype=LambdaTypes.TUPLE},
LambdaTypes.VAR args)),
LambdaTypes.LET((lvar,NONE,LambdaTypes.VAR dexp'),
LambdaTypes.SWITCH
(LambdaTypes.VAR lvar,
SOME {num_vccs=1,num_imms=1},
[(LambdaTypes.IMM_TAG ("ABSENT",0),
LambdaTypes.VAR new_cg),
(LambdaTypes.VCC_TAG("PRESENT",1),
LambdaTypes.APP
(LambdaTypes.VAR new_cg,
([let
val lexp = LambdaTypes.SELECT
({index=1,
size=2,
selecttype=LambdaTypes.CONSTRUCTOR},
LambdaTypes.VAR lvar)
in
LambdaTypes.SWITCH
(LambdaTypes.VAR instance_var,
NONE,
[(LambdaTypes.SCON_TAG
(Ident.INT ("~1", Location.UNKNOWN),
NONE),
LambdaTypes.STRUCT
([LambdaTypes.INT 0, lexp],
LambdaTypes.CONSTRUCTOR))],
SOME
(LambdaTypes.STRUCT
([LambdaTypes.INT 1,
LambdaTypes.STRUCT
([lexp,
LambdaTypes.VAR instance_var],
LambdaTypes.TUPLE)],
LambdaTypes.CONSTRUCTOR)))
end],[]),
NONE))],
NONE))))),
LambdaTypes.BODY,
overload_function_string,Datatypes.NULLTYPE,
RuntimeEnv.INTERNAL_FUNCTION)
in
(LambdaTypes.VAR lv,
[LambdaTypes.LETB(lv,NONE,lexp)],
fn env =>
Environ.add_valid_env(env,
(Ident.VAR (Symbol.find_symbol overload_function_string),
EnvironTypes.LAMB(lv, EnvironTypes.NOSPEC))))
end)
else (dummy_varexp,[],fn env => env)
fun wrap_selects (selects,lexp) =
let
fun aux [] = lexp
| aux ({index,size}::rest) =
LambdaTypes.SELECT({index = index,size = size, selecttype = LambdaTypes.TUPLE},
aux rest)
in
aux selects
end
fun dexp_to_lambda EnvironTypes.NULLEXP = LambdaTypes.INT 0
| dexp_to_lambda (EnvironTypes.INT i) =
LambdaTypes.STRUCT([LambdaTypes.INT 1,LambdaTypes.INT i],
LambdaTypes.CONSTRUCTOR)
| dexp_to_lambda (EnvironTypes.LAMBDAEXP(selects,(lv,_),_)) =
wrap_selects (selects,LambdaTypes.VAR lv)
| dexp_to_lambda( EnvironTypes.LAMBDAEXP'(selects,functorlv,_)) =
wrap_selects (selects,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([case !functorlv of
EnvironTypes.LVARFOO functorlv => LambdaTypes.VAR functorlv
| EnvironTypes.INTFOO ct =>
cg_longvalid(mklongvalid (Ident.VAR(make_functor_app ct)),env)],
[]),
NONE))
fun dexp_to_lambda' EnvironTypes.NULLEXP = LambdaTypes.INT (~6)
| dexp_to_lambda' (EnvironTypes.INT i) = LambdaTypes.INT i
| dexp_to_lambda' (EnvironTypes.LAMBDAEXP (selects,(lv,_),_)) =
wrap_selects (selects,LambdaTypes.VAR lv)
| dexp_to_lambda' (EnvironTypes.LAMBDAEXP' (selects,functorlv,_)) =
wrap_selects
(selects,
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.DEREF,
([case !functorlv of
EnvironTypes.LVARFOO functorlv => LambdaTypes.VAR functorlv
| EnvironTypes.INTFOO ct =>
cg_longvalid(mklongvalid (Ident.VAR (make_functor_app ct)),env)],[]),
NONE))
val dexps_ref :
({index : int, size : int} list *
(LambdaTypes.LVar * LambdaTypes.LambdaExp)) list ref = ref []
fun longvalid_dexp_to_lambda(longValId as Ident.LONGVALID(_, Ident.VAR sy),
denv,new_cg,instance,instance_info) =
let
fun make_instance () =
(case instance of
NONE => new_cg
| _ => Crash.impossible "polyvariable 1")
fun fetch_instance' () =
case instance_info of
Datatypes.ZERO => NONE
| Datatypes.ONE i => SOME i
| _ => Crash.impossible "fetch_instance':longvalid_dexp_to_lambda:lambda"
val dexp = dlookup_longvalid (longValId,denv)
val dexp' = dexp_to_lambda dexp
in
case dexp of
EnvironTypes.NULLEXP => make_instance()
| EnvironTypes.INT i =>
LambdaTypes.APP
(new_cg,
([let
val lexp = LambdaTypes.INT i
in
case fetch_instance'() of
NONE =>
LambdaTypes.STRUCT([LambdaTypes.INT 0, lexp],LambdaTypes.CONSTRUCTOR)
| SOME i =>
LambdaTypes.STRUCT([LambdaTypes.INT 1,
LambdaTypes.STRUCT([lexp,
LambdaTypes.INT i],
LambdaTypes.TUPLE)],
LambdaTypes.CONSTRUCTOR)
end],[]),
NONE)
| EnvironTypes.LAMBDAEXP' _ =>
LambdaTypes.APP(overload_exp,
([LambdaTypes.STRUCT([new_cg,dexp',
case fetch_instance'() of
NONE => LambdaTypes.INT ~1
| SOME i => LambdaTypes.INT i],
LambdaTypes.TUPLE)],
[]),
NONE)
| _ =>
let
fun selects(EnvironTypes.LAMBDAEXP(selects,(_,lv),_)) = (selects,lv)
| selects _ = Crash.impossible "selects:longvalid_dexp_to_lambda:lambda"
val (selects,root_lv) = selects dexp
in
(case Lists.assoc (selects,!dexps_ref) of
(lv,_) =>
case fetch_instance'() of
NONE => LambdaTypes.VAR lv
| SOME i =>
LambdaTypes.APP(LambdaTypes.VAR lv,
([LambdaTypes.INT i],[]),
NONE))
handle Lists.Assoc =>
let
val (lexp,instance') =
case fetch_instance'() of
NONE =>
(LambdaTypes.APP(overload_exp,
([LambdaTypes.STRUCT([wrap_selects(selects,LambdaTypes.VAR root_lv),
dexp',LambdaTypes.INT ~1],
LambdaTypes.TUPLE)],
[]),
NONE),
NONE)
| instance' =>
(let
val lv = new_LVar()
in
LambdaTypes.FN
(([lv],[]),
LambdaTypes.APP
(overload_exp,
([LambdaTypes.STRUCT
([wrap_selects(selects,LambdaTypes.VAR root_lv),
dexp', LambdaTypes.VAR lv],
LambdaTypes.TUPLE)],[]),
NONE),
LambdaTypes.BODY,
"overload for " ^ Symbol.symbol_name sy,
Datatypes.NULLTYPE,
RuntimeEnv.INTERNAL_FUNCTION)
end,
instance')
val lv = new_LVar()
in
(dexps_ref := (selects,(lv,lexp))::(!dexps_ref);
case instance' of
NONE => LambdaTypes.VAR lv
| SOME i =>
LambdaTypes.APP(LambdaTypes.VAR lv,
([LambdaTypes.INT i],[]),
NONE))
end
end
end
| longvalid_dexp_to_lambda _ = Crash.impossible "longvalid_dexp_to_lambda:lambda"
fun make_type_function (EnvironTypes.LAMBDAEXP (selects, (lv,lv'),_)) =
LambdaTypes.STRUCT ([wrap_selects (selects, LambdaTypes.VAR lv),
LambdaTypes.APP(wrap_selects (selects,LambdaTypes.VAR lv'),
([unit_exp],[]),
NONE)],
LambdaTypes.TUPLE)
| make_type_function _ =
Crash.impossible "make_type_function:lambda"
fun dstrexp_to_lambda dstrexp =
case dstrexp of
EnvironTypes.LAMBDASTREXP (selects, lv, Datatypes.COPYSTR(_,str)) =>
dstrexp_to_lambda (EnvironTypes.LAMBDASTREXP(selects, lv, str))
| EnvironTypes.LAMBDASTREXP(selects, (lv,_), str as Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap)))) =>
wrap_selects(selects, LambdaTypes.VAR lv)
| EnvironTypes.LAMBDASTREXP'(selects,lv,Datatypes.COPYSTR(_,str)) =>
dstrexp_to_lambda (EnvironTypes.LAMBDASTREXP'(selects,lv,str))
| EnvironTypes.LAMBDASTREXP'(selects,functorlv,_) =>
(wrap_selects
(selects,
LambdaTypes.APP (LambdaTypes.BUILTIN Pervasives.DEREF,
([case !functorlv of
EnvironTypes.LVARFOO functorlv => LambdaTypes.VAR functorlv
| EnvironTypes.INTFOO ct =>
cg_longvalid (mklongvalid (Ident.VAR (make_functor_app ct)),env)],
[]),
NONE)))
| EnvironTypes.DENVEXP(EnvironTypes.DENV(validmap,stridmap)) =>
let
val validmap = NewMap.to_list_ordered validmap
in
LambdaTypes.STRUCT(Lists.reducer (fn ((Ident.TYCON' _,dexp),tycons) =>
dexp_to_lambda' dexp::tycons
| (_,tycons) => tycons)
(validmap,nil) @
Lists.reducer (fn ((Ident.TYCON' _,_),vars) => vars
| ((_,dexp),vars) => dexp_to_lambda dexp::vars)
(validmap,nil) @
map (fn (_,dstrexp) => dstrexp_to_lambda dstrexp)
(NewMap.to_list_ordered stridmap),
LambdaTypes.TUPLE)
end
fun str_to_lambda(Datatypes.STR(_,_,Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap)))) =
LambdaTypes.STRUCT(map (fn _ => LambdaTypes.INT(~4))
(NewMap.to_list_ordered tyconmap) @
map (fn _ => LambdaTypes.INT 0)
(NewMap.to_list_ordered validmap) @
map (fn (_,str) => str_to_lambda str)
(NewMap.to_list_ordered stridmap),
LambdaTypes.TUPLE)
| str_to_lambda(Datatypes.COPYSTR(_,str)) = str_to_lambda str
fun compare_strs(Datatypes.COPYSTR (_, str), str') =
compare_strs (str, str')
| compare_strs(str, Datatypes.COPYSTR (_, str')) =
compare_strs (str, str')
| compare_strs(Datatypes.STR (_, _,Datatypes.ENV
(Datatypes.SE sm,
Datatypes.TE tm,
Datatypes.VE (_, vm))),
Datatypes.STR (_, _, Datatypes.ENV
(Datatypes.SE sm',
Datatypes.TE tm',
Datatypes.VE (_, vm')))) =
NewMap.size vm + NewMap.size tm = NewMap.size vm' + NewMap.size tm' andalso
NewMap.size sm = NewMap.size sm' andalso
Lists.forall
compare_strs
(Lists.zip(NewMap.range_ordered sm,NewMap.range_ordered sm'))
fun merge_dexps (dint,EnvironTypes.NULLEXP,_,location) = dint
| merge_dexps (_,EnvironTypes.LAMBDAEXP(selects,lv,SOME _),SOME tystr,_) =
EnvironTypes.LAMBDAEXP(selects,lv,SOME(fetch_ntf tystr))
| merge_dexps(_,EnvironTypes.LAMBDAEXP'(selects,lv,SOME _),SOME tystr,_) =
EnvironTypes.LAMBDAEXP'(selects,lv,SOME(fetch_ntf tystr))
| merge_dexps(_,dexp,_,_) = dexp
fun merge_dstrexps(dstr,SOME(Datatypes.COPYSTR(_,str)), dstrexp, location) =
merge_dstrexps(dstr,SOME str,dstrexp, location)
| merge_dstrexps(debugger_str,
SOME(str as
Datatypes.STR(_,_,Datatypes.ENV(Datatypes.SE(stridmap''),
Datatypes.TE(tyconmap''),
Datatypes.VE(_,validmap'')))),
dstrexp, location) =
let
fun merge_lambdastrexps LAMBDAEXP LAMBDASTREXP(selects,lv,str' as
Datatypes.STR(_,_,Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap)))) =
if compare_strs(str',str) then
LAMBDASTREXP(selects,lv,str)
else
EnvironTypes.DENVEXP(
let
val size_validmap = NewMap.size validmap
val size_tyconmap = NewMap.size tyconmap
val size = NewMap.size stridmap + size_validmap + size_tyconmap
in
NewMap.fold
(fn (env, tc' as Ident.TYCON tc, tystr) =>
Environ.add_valid_denv
(env, (Ident.TYCON' tc, LAMBDAEXP({index=NewMap.rank' (tyconmap,tc'),
size=size}
::selects,lv,
SOME(fetch_ntf tystr)))))
(NewMap.fold
(fn (env, s, str) =>
Environ.add_strid_denv
(env, (s,
merge_dstrexps(debugger_str,SOME str,
LAMBDASTREXP({index=NewMap.rank' (stridmap,s) + size_validmap + size_tyconmap,
size=size}
::selects,lv,
NewMap.apply' (stridmap,s)), location))))
((NewMap.fold
(fn (env, v, _) =>
Environ.add_valid_denv
(env, (v, LAMBDAEXP({index=NewMap.rank' (validmap,v) + size_tyconmap,
size=size}
::selects,lv,NONE))))
(Environ.empty_denv, validmap'')), stridmap''), tyconmap'')
end)
| merge_lambdastrexps _ LAMBDASTREXP(selects,lv,Datatypes.COPYSTR(_,str')) =
merge_dstrexps (debugger_str,SOME str,LAMBDASTREXP(selects,lv,str'), location)
in
(case dstrexp of
EnvironTypes.LAMBDASTREXP args =>
merge_lambdastrexps EnvironTypes.LAMBDAEXP EnvironTypes.LAMBDASTREXP args
| EnvironTypes.LAMBDASTREXP' args =>
merge_lambdastrexps EnvironTypes.LAMBDAEXP' EnvironTypes.LAMBDASTREXP' args
| EnvironTypes.DENVEXP(EnvironTypes.DENV(validmap',stridmap')) =>
(case !debugger_str of
Datatypes.DSTR(stridmap,tyconmap,validmap) =>
EnvironTypes.DENVEXP(
EnvironTypes.DENV(NewMap.fold
(fn (map,tc' as Ident.TYCON tc,i) =>
NewMap.define(map,Ident.TYCON' tc,
merge_dexps(EnvironTypes.INT i,
NewMap.apply validmap' (Ident.TYCON' tc),
SOME(NewMap.apply'(tyconmap'',tc')), location)))
(NewMap.map (fn (v,NONE) => merge_dexps (EnvironTypes.NULLEXP,
(NewMap.apply' (validmap',v)),NONE, location)
| (v,SOME i) =>
merge_dexps(EnvironTypes.INT i,
NewMap.apply' (validmap',v),NONE, location))
validmap,tyconmap),
NewMap.map (fn (strid,dstr) =>
merge_dstrexps(ref dstr,
SOME(NewMap.apply' (stridmap'',strid)),
NewMap.apply' (stridmap',strid), location))
stridmap))
| Datatypes.EMPTY_DSTR =>
EnvironTypes.DENVEXP(EnvironTypes.DENV(
NewMap.map (fn (Ident.TYCON' tc,dexp) =>
merge_dexps(EnvironTypes.NULLEXP,dexp,
SOME(NewMap.apply'(tyconmap'',Ident.TYCON tc)), location)
| (_,dexp) => dexp) validmap',
NewMap.map (fn (strid,dstrexp) =>
merge_dstrexps(debugger_str,
SOME(NewMap.apply' (stridmap'',strid)),
dstrexp, location))
stridmap'))))
end
| merge_dstrexps(ref Datatypes.EMPTY_DSTR, NONE, dstrexp, _) = dstrexp
| merge_dstrexps _ = Crash.impossible "merge_dstrexps:lambda"
fun strip_tyfuns(lexp,env as EnvironTypes.ENV(valid_env, strid_env)) =
let
val lvar = new_LVar()
val valid_map = NewMap.to_list_ordered valid_env
val strid_map = NewMap.to_list_ordered strid_env
val size1 = NewMap.size valid_env
val size2 = size1 + NewMap.size strid_env
fun filter_vars [] _ = []
| filter_vars ((Ident.TYCON' _,_)::vars) index = filter_vars vars (index+1)
| filter_vars (_::vars) index =
LambdaTypes.SELECT({index=index, size=size2,selecttype=LambdaTypes.STRUCTURE},
LambdaTypes.VAR lvar)::filter_vars vars (index+1)
fun strip_strs [] _ = []
| strip_strs ((_,(env,_,_))::strs) index =
strip_tyfuns(LambdaTypes.SELECT({index=size1 + index, size=size2,selecttype=LambdaTypes.STRUCTURE},
LambdaTypes.VAR lvar),
env)::strip_strs strs (index+1)
val vars = filter_vars valid_map 0
val strs = strip_strs strid_map 0
val size3 = length valid_map - length vars
val size4 = size2 - size3
in
(LambdaTypes.LET((lvar,NONE,lexp),
LambdaTypes.STRUCT(vars @ map #1 strs,LambdaTypes.TUPLE)),
Lists.reducel (fn (env,((strid,(_,EnvironTypes.FIELD{index, ...},_)),env')) =>
Environ.add_strid_env(env,(strid,(env',
EnvironTypes.FIELD{index=index-size3,size=size4},false)))
| (env,((strid,(_,comp,_)),env')) =>
Environ.add_strid_env(env,(strid,(env',comp,false))))
(Lists.reducel (fn (env,(Ident.TYCON' _,_)) => env
| (env,(valid,EnvironTypes.FIELD{index, ...})) =>
Environ.add_valid_env(env,(valid,
EnvironTypes.FIELD{index=index-size3,size=size4}))
| (env,valid) => Environ.add_valid_env(env,valid))
(Environ.empty_env,valid_map),
Lists.zip(strid_map,map #2 strs)))
end
fun include_tyfuns(lexp,Datatypes.COPYSTR(_,str),env) = include_tyfuns(lexp,str,env)
| include_tyfuns(lexp,str as Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap))),
env as EnvironTypes.ENV(valid_env, strid_env)) =
let
val lvar = new_LVar()
val size1 = NewMap.size tyconmap
val size2 = NewMap.size validmap
val size3 = size2 + NewMap.size stridmap
val size4 = size3 + size1
val tyconmap = NewMap.to_list_ordered tyconmap
val strid_env = NewMap.to_list_ordered strid_env
fun new_vars [] _ = []
| new_vars (_::vars) index =
LambdaTypes.SELECT({index=index, size=size3,selecttype=LambdaTypes.STRUCTURE},
LambdaTypes.VAR lvar)::new_vars vars (index+1)
fun prepend_tycons [] = new_vars (NewMap.to_list_ordered validmap) 0
| prepend_tycons (_::tycons) =
LambdaTypes.FN(([new_LVar()],[]),
LambdaTypes.INT 1,
LambdaTypes.BODY,
"dummy tyfun tyfun",
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION) ::
prepend_tycons tycons
fun include_strs [] _ = []
| include_strs (((_,str),(env,_,_))::strs) index =
include_tyfuns(LambdaTypes.SELECT({index=size2 + index, size=size3,selecttype=LambdaTypes.STRUCTURE},
LambdaTypes.VAR lvar),
str,env)::include_strs strs (index+1)
val strs = include_strs (Lists.zip(NewMap.to_list_ordered stridmap,map #2 strid_env)) 0
in
(LambdaTypes.LET((lvar,NONE,lexp),
LambdaTypes.STRUCT(prepend_tycons tyconmap @
map #1 strs,
LambdaTypes.TUPLE)),
Lists.reducel (fn (map,((strid,(_,EnvironTypes.FIELD{index, ...},_)),env)) =>
Environ.add_strid_env(map,(strid,(env,
EnvironTypes.FIELD{index=index+size1,size=size4},true)))
| (map,((strid,(_,comp,_)),env)) =>
Environ.add_strid_env(map,(strid,(env,comp,true))))
(#1(Lists.reducel (fn ((map,index),(Ident.TYCON tc,_)) =>
(Environ.add_valid_env(map,(Ident.TYCON' tc,
EnvironTypes.FIELD{index=index,size=size4})),index+1))
((EnvironTypes.ENV(NewMap.map (fn (_,EnvironTypes.FIELD{index, ...}) =>
EnvironTypes.FIELD{index=index+size1,size=size4}
| (_,comp) => comp) valid_env,
NewMap.empty (Ident.strid_lt,Ident.strid_eq)),0),tyconmap)),
Lists.zip(strid_env,map #2 strs)))
end
fun make_dstrexp(Datatypes.COPYSTR(_,str)) = make_dstrexp str
| make_dstrexp(str as Datatypes.STR(_,_,
Datatypes.ENV(Datatypes.SE stridmap,
Datatypes.TE tyconmap,
Datatypes.VE(_,validmap)))) =
EnvironTypes.DENVEXP(
EnvironTypes.DENV(
NewMap.union
(NewMap.fold (fn (map,Ident.TYCON tc,_) =>
NewMap.define(map,Ident.TYCON' tc,EnvironTypes.INT(~5)))
(NewMap.empty (Ident.valid_lt,Ident.valid_eq),tyconmap),
NewMap.map (fn _ => EnvironTypes.NULLEXP) validmap),
NewMap.map (fn (_,str) => make_dstrexp str) stridmap))
fun fetch_interface(SOME(ref(SOME interface))) = interface
| fetch_interface(SOME _) =
Crash.impossible "1:NONE:fetch_interface:lambda"
| fetch_interface _ =
Crash.impossible "2:NONE:fetch_interface:lambda"
local
val empty_dstr = ref Datatypes.EMPTY_DSTR
in
fun fetch_debugger_str (SOME debugger_str) = debugger_str
| fetch_debugger_str NONE = empty_dstr
end
fun fetch_tyfun (SOME tyfun) = tyfun
| fetch_tyfun NONE =
Crash.impossible "NONE:fetch_tyfun:lambda"
val tyfun_refs_ref : Datatypes.Tyfun ref list ref = ref []
val valenv_refs_ref : Datatypes.Valenv ref list ref = ref []
val tyfun_spills_ref : (Datatypes.Tyfun ref * RuntimeEnv.Offset ref * LambdaTypes.LambdaExp) list ref = ref []
val tyfun_lvars_ref : (Datatypes.Tyfun ref * (LambdaTypes.LVar * LambdaTypes.LambdaExp)) list ref = ref []
fun type_spills (denv,ty) =
let
fun type_spills (Datatypes.CONSTYPE(tys,tyn)) =
Lists.reducel (fn (spills,ty) => spills@type_spills ty)
(tyname_spills (denv,tyn),
tys)
| type_spills (Datatypes.FUNTYPE(ty1,ty2)) =
type_spills ty1 @ type_spills ty2
| type_spills (Datatypes.RECTYPE map) =
NewMap.fold (fn (spills,_, ty) => spills@type_spills ty) (nil,map)
| type_spills (Datatypes.METATYVAR(ref(_,ty,_),_,_)) = type_spills ty
| type_spills (Datatypes.META_OVERLOADED {1=ref ty,...}) =
type_spills ty
| type_spills (Datatypes.TYVAR(ref(_,ty,_),_)) = type_spills ty
| type_spills (Datatypes.METARECTYPE(ref(_,_,ty,_,_))) = type_spills ty
| type_spills _ = nil
in
type_spills ty
end
and typescheme_spills (denv,Datatypes.SCHEME(_,(ty,_))) = type_spills (denv,ty)
| typescheme_spills (denv,Datatypes.UNBOUND_SCHEME(ty,_)) = type_spills (denv,ty)
| typescheme_spills _ = nil
and tyname_spills (denv,tyname) =
let
fun tyname_spills
(Datatypes.METATYNAME(tf as ref(Datatypes.NULL_TYFUN _),name,_,_,
ve' as ref(Datatypes.VE(_,ve)),_)) =
if Datatypes.NewMap.is_empty ve then
if Lists.member(tf,!tyfun_refs_ref) then nil
else
(tyfun_refs_ref := tf::(!tyfun_refs_ref);
[(tf,
(case Lists.assoc(tf,!tyfun_lvars_ref) of
(lv,_) => LambdaTypes.VAR lv)
handle Lists.Assoc =>
let
val lv = new_LVar()
val lexp = make_type_function (dlookup_tycon(tf,denv))
in
(tyfun_lvars_ref := (tf,(lv,lexp))::(!tyfun_lvars_ref);
lexp)
end)]
handle exn =>
(ignore(fn () =>
do_moduler_debug
(fn () =>
"WARNING dlookup_tycon:" ^
IdentPrint.printLongValId print_options (mklongvalid (Ident.VAR(Symbol.find_symbol name)))));
[]))
else
if Lists.member(ve',!valenv_refs_ref) then nil
else
(valenv_refs_ref := ve'::(!valenv_refs_ref);
NewMap.fold (fn (spills,_,tysch) => spills@typescheme_spills (denv,tysch)) (nil,ve))
| tyname_spills (Datatypes.METATYNAME(ref(Datatypes.ETA_TYFUN tyn),_,_,_,
ve' as ref(Datatypes.VE(_,ve)),_)) =
tyname_spills tyn @
(if Lists.member(ve',!valenv_refs_ref) then nil
else
(valenv_refs_ref := ve'::(!valenv_refs_ref);
NewMap.fold (fn (spills,_,tysch) =>
spills@typescheme_spills (denv,tysch)) (nil,ve)))
| tyname_spills (Datatypes.METATYNAME(ref(Datatypes.TYFUN(ty,_)),_,_,_,
ve' as ref(Datatypes.VE(_,ve)),_)) =
type_spills (denv,ty)@
(if Lists.member(ve',!valenv_refs_ref) then nil
else
(valenv_refs_ref := ve'::(!valenv_refs_ref);
NewMap.fold (fn (spills,_,tysch) =>
spills@typescheme_spills (denv,tysch)) (nil,ve)))
| tyname_spills (Datatypes.TYNAME
(_,_,_,_,
ve1 as ref(Datatypes.VE(_,ve2)),_,_,
ve3 as ref(Datatypes.VE(_,ve4)),_)) =
let
val (ve',ve) =
(ve3,ve4)
in
if Lists.member(ve',!valenv_refs_ref) then nil
else
(valenv_refs_ref := ve'::(!valenv_refs_ref);
NewMap.fold (fn (spills,_,tysch) =>
spills@typescheme_spills (denv,tysch)) (nil,ve))
end
in
tyname_spills tyname
end
fun null_tyfun_spills (denv,
(RuntimeEnv.VARINFO(name,
(ref ty,inforef as ref (RuntimeEnv.RUNTIMEINFO (i,_))),_))) =
if Types.isFunType ty then ()
else
let
val spills =
map (fn (tf,dexp) =>(tf, ref (RuntimeEnv.OFFSET1 0),dexp))
(type_spills (denv,ty))
in
(inforef := (RuntimeEnv.RUNTIMEINFO (i,map (fn (tf,spill,_) =>(tf,spill)) spills));
tyfun_spills_ref := spills@(!tyfun_spills_ref))
end
| null_tyfun_spills _ = ()
val null_tyfun_spills =
if generate_moduler then null_tyfun_spills
else
fn _ => ()
fun make_null_tyfun_spills lexp =
let_lambdas_in_exp(
map (fn (_,spill,tyfun) =>
LambdaTypes.LETB(new_LVar(),
SOME(ref (RuntimeEnv.VARINFO
("null_tyfun_spill",
dummy_instance (),
SOME spill))),
tyfun))
(!tyfun_spills_ref),
lexp)
val store_null_tyfun_spills =
if generate_moduler then
fn () => (!tyfun_refs_ref, !valenv_refs_ref, !tyfun_spills_ref)
else
fn () => ([], [], [])
fun init_null_tyfun_spills () =
(tyfun_refs_ref := []; valenv_refs_ref := []; tyfun_spills_ref := [])
val restore_null_tyfun_spills =
if generate_moduler then
fn (old_tyfun_refs_ref,old_valenv_refs_ref,old_tyfun_spills) =>
(tyfun_refs_ref := old_tyfun_refs_ref;
valenv_refs_ref := old_valenv_refs_ref;
tyfun_spills_ref := old_tyfun_spills)
else
fn _ => ()
fun make_lambdalist lambdas =
Lists.reducer
(fn (lambda,lambdalist) => LambdaTypes.STRUCT([lambda,lambdalist],LambdaTypes.TUPLE)) (lambdas,LambdaTypes.INT(1))
val dummy_false = ref false
val dummy_ve = ref Datatypes.empty_valenv
val TYPEdec_spills =
if generate_moduler then
fn denvir =>
let
fun TYPEdec_spills nil bindings = bindings
| TYPEdec_spills ((_,tycon as Ident.TYCON sym,_,tyf)::rest) (env,denv,bindings) =
TYPEdec_spills rest
let
val tyfun_lvar = new_LVar()
in
(Environ.add_valid_env(env, (Ident.TYCON' sym,
EnvironTypes.LAMB(tyfun_lvar, EnvironTypes.NOSPEC))),
add_valid_denv(denv, (Ident.TYCON' sym,EnvironTypes.NULLEXP)),
LambdaTypes.LETB(tyfun_lvar,NONE,
LambdaTypes.FN(([new_LVar()],[]),
make_lambdalist
(map (fn (_,spill) => spill)
(tyname_spills (denvir,
Datatypes.METATYNAME(fetch_tyfun tyf,"",0,
dummy_false,dummy_ve,dummy_false)))),
LambdaTypes.BODY,
(init_null_tyfun_spills();
"spills for tycon " ^ IdentPrint.printTyCon tycon),
Datatypes.NULLTYPE,
RuntimeEnv.INTERNAL_FUNCTION))::bindings)
end
in
TYPEdec_spills
end
else
fn _ => fn _ => fn bindings => bindings
val DATATYPEdec_spills =
if generate_moduler then
let
fun DATATYPEdec_spills (denvir,nil,bindings) = bindings
| DATATYPEdec_spills (denvir,(_,tycon as Ident.TYCON sym,_,tyf,_)::rest,(env,denv,bindings)) =
let
val tyfun_lvar = new_LVar()
val new_env = Environ.add_valid_env(env, (Ident.TYCON' sym,
EnvironTypes.LAMB(tyfun_lvar, EnvironTypes.NOSPEC)))
val new_denv = add_valid_denv(denv, (Ident.TYCON' sym,EnvironTypes.NULLEXP))
val new_binding =
LambdaTypes.LETB(tyfun_lvar,NONE,
LambdaTypes.FN
(([new_LVar()],[]),
make_lambdalist
(map (fn (_,spill) => spill)
(tyname_spills (denvir,
Datatypes.METATYNAME(fetch_tyfun tyf,"",0,
dummy_false,dummy_ve,dummy_false)))),
LambdaTypes.BODY,
(init_null_tyfun_spills();
"spills for tycon " ^ IdentPrint.printTyCon tycon),
Datatypes.NULLTYPE,
RuntimeEnv.INTERNAL_FUNCTION))
in
DATATYPEdec_spills (denvir,rest,(new_env,new_denv,new_binding::bindings))
end
in
DATATYPEdec_spills
end
else
fn (_,_,bindings) => bindings
fun make_binding (lv,debug_info,instance,lexp,comment,location) =
if variable_debug
then [LambdaTypes.LETB(lv,SOME(ref debug_info),lexp)]
else
[LambdaTypes.LETB (lv,NONE,lexp)]
val debugger_env_ref = ref initial_debugger_env
fun trans_exp(name, x, env, denv, fnname) =
case x of
Absyn.SCONexp (sc, ref ty) => LambdaTypes.SCON (sc, Types.sizeof ty)
| Absyn.VALexp(longValId, ref ty, location, ref(instance_info,instance)) =>
(case longValId of
Ident.LONGVALID(p, valid as Ident.VAR sy) =>
let
val sy_name = Symbol.symbol_name sy
val env_ol =
case p of
Ident.NOPATH =>
(case Environ.lookup_valid(valid, env) of
EnvironTypes.PRIM prim =>
(case Environ.overloaded_op valid of
SOME prim' => prim = prim'
| _ => false)
| _ => false)
| _ => false
fun error_fn (valid, loc) =
Info.error'
error_info
(Info.FATAL, loc,
"Unresolved overloading for "
^ IdentPrint.printValId print_options valid)
val cg =
if env_ol then
let
val _ = Types.resolve_overloading
(not old_definition, ty, error_fn)
val sy_name' =
"_" ^ (overloaded_name ty) ^ sy_name
val small_type =
case Types.sizeof ty
of NONE => true
| SOME sz =>
sz <= MachSpec.bits_per_word
val cg_opt =
if small_type then
lookup_derived_overload sy_name'
else
NONE
in
case cg_opt
of SOME cg => cg ()
| NONE =>
(Diagnostic.output 2
(fn _ =>
["Overloaded operator ",sy_name,
" instantiated to  ",sy_name',"\n"]);
cg_longvalid
(mklongvalid
(Ident.VAR (Symbol.find_symbol sy_name')),
Primitives.env_for_lookup_in_lambda))
end
else
cg_longvalid(longValId,env)
val (new_cg,built_in) =
case cg of
LambdaTypes.BUILTIN prim =>
(if isnt_eq_prim cg andalso isnt_neq_prim cg then
cg
else
let
val _ = Types.resolve_overloading
(not old_definition, ty, error_fn)
val sy_name = if is_eq_prim cg then "=" else "<>"
val (ok, ty') = domain_type_name ty
val (ok, tyname) =
if ok then
domain_tyname ty'
else
(false, Types.int_tyname)
in
if ok then
let
fun ty_to_check(arg as (_, ty')) =
if check_one_vcc_and_no_nullaries ty' then
let
val (_, map) = TypeUtils.get_valenv ty'
in
case NewMap.to_list map of
(_, scheme) :: _ =>
let
val ty = TypeUtils.type_from_scheme scheme
val ty = case ty of
Datatypes.FUNTYPE(ty, _) => ty
| _ => ty
val (ok, tyname) = domain_tyname ty
in
if ok then
ty_to_check(tyname, ty)
else
arg
end
| _ => Crash.impossible"ty_to_check: bad map"
end
else
arg
val (tyname, ty') = ty_to_check(tyname, ty')
val (ident, changed) =
if Types.has_int_equality tyname orelse
Types.has_ref_equality tyname orelse
check_no_vcc_for_eq ty' then
(mklongvalid(Ident.VAR(Symbol.find_symbol("_int" ^ sy_name))), true)
else
if Types.has_real_equality tyname then
(mklongvalid(Ident.VAR(Symbol.find_symbol("_real" ^ sy_name))), true)
else
if Types.has_string_equality tyname then
(mklongvalid(Ident.VAR(Symbol.find_symbol("_string" ^ sy_name))), true)
else
if Types.has_int32_equality tyname then
(mklongvalid(Ident.VAR(Symbol.find_symbol("_int32" ^ sy_name))), true)
else
(longValId, false)
in
if changed then
cg_longvalid(ident, Primitives.env_for_lookup_in_lambda)
else
cg
end
else
cg
end
,true)
| _ => (cg,false)
in
if built_in then
new_cg
else
(longvalid_dexp_to_lambda (longValId,denv,new_cg,instance,instance_info))
end
| Ident.LONGVALID(_, valid as Ident.CON symbol) =>
let
val (location,tag) = constructor_tag(valid, ty)
val lexp = LambdaTypes.INT tag
in
case Environ.FindBuiltin(longValId, env) of
SOME prim => LambdaTypes.BUILTIN prim
| _ =>
if TypeUtils.is_vcc ty then
let
val new_lv = new_LVar()
in
LambdaTypes.FN
(([new_lv],[]),
if TypeUtils.get_no_cons ty > 1 andalso
not (is_list_type (TypeUtils.get_cons_type ty))
then
LambdaTypes.STRUCT([lexp, LambdaTypes.VAR new_lv],LambdaTypes.CONSTRUCTOR)
else
LambdaTypes.VAR new_lv,
LambdaTypes.BODY,
let
val cons_type = TypeUtils.get_cons_type ty
val typename =
case cons_type of
Datatypes.CONSTYPE(_, tyname) =>
Types.print_name options tyname
| _ => Crash.impossible"lambda:bad cons type"
val con_name =
"constructor " ^ Symbol.symbol_name symbol ^
" of " ^ typename
in
if location = "" then
con_name
else
con_name ^ " [" ^ location ^ "]"
end,
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION)
end
else
lexp
end
| Ident.LONGVALID(_, valid as Ident.EXCON _) =>
let val (le,_) = cg_longexid(longValId, env)
in
if TypeUtils.is_vcc ty then
let
val lv = new_LVar()
in
LambdaTypes.FN(([lv],[]),
LambdaTypes.STRUCT([le, LambdaTypes.VAR lv],LambdaTypes.CONSTRUCTOR),
LambdaTypes.BODY,
"Builtin code to construct an exception",
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION)
end
else
LambdaTypes.STRUCT([le, unit_exp],LambdaTypes.CONSTRUCTOR)
end
| _ => Crash.impossible "TYCON':trans_exp:lambda")
| Absyn.RECORDexp label_exp_list =>
let
val lvar_lab_lexp_list =
map (fn (lab, exp) =>
(lab, new_LVar(),
trans_exp (" no_name", exp, env, denv, fnname)))
label_exp_list
in
let_lambdas_in_exp(map (fn (_, lv, le) =>
LambdaTypes.LETB(lv,NONE, le))
lvar_lab_lexp_list,
LambdaTypes.STRUCT(map (fn (_, lvar, _) =>
LambdaTypes.VAR lvar)
(Lists.qsort known_order lvar_lab_lexp_list),
LambdaTypes.TUPLE))
end
| Absyn.LOCALexp (decl, exp, _) =>
let
val (env', denv', lambda_list) = trans_dec (decl, env, false, denv, fnname)
in
let_lambdas_in_exp(lambda_list,
trans_exp(" no_name", exp, Environ.augment_env(env, env'),
augment_denv(denv, denv'),
fnname))
end
| Absyn.APPexp(fun_exp, val_exp,_,annotation,_) =>
let
val fcn =
trans_exp(" inline_app", fun_exp, env, denv, fnname)
val (is_poly, is_eq) =
case fcn of
LambdaTypes.BUILTIN Pervasives.EQ =>
(true, true)
| LambdaTypes.BUILTIN Pervasives.NE =>
(true, false)
| _ => (false, false)
val arg =
let
val arg =
trans_exp(" no_name", val_exp, env, denv, fnname)
in
if is_poly then
LambdaOptimiser.simple_beta_reduce arg
else
arg
end
val (good_arg, new_arg, absyn) =
if is_poly then
(case (arg, val_exp) of
(LambdaTypes.STRUCT([le, le' as LambdaTypes.INT _],_),
Absyn.RECORDexp[_, (_, valexp as Absyn.VALexp _)]) =>
(true, LambdaTypes.STRUCT([le', le],LambdaTypes.TUPLE), valexp)
| (LambdaTypes.STRUCT([le' as LambdaTypes.INT _, le],_),
Absyn.RECORDexp[(_, valexp as Absyn.VALexp _), _]) =>
(true, LambdaTypes.STRUCT([le', le],LambdaTypes.TUPLE), valexp)
| _ => (false, arg, val_exp))
else
(false, arg, val_exp)
val (true_val, false_val) =
if is_eq then (LambdaTypes.INT 1, LambdaTypes.INT 0)
else (LambdaTypes.INT 0, LambdaTypes.INT 1)
in
if is_poly andalso good_arg then
let
val (exp_arg, tag) = case new_arg of
LambdaTypes.STRUCT([LambdaTypes.INT tag, le],_) => (le, tag)
| _ => Crash.impossible "Bad polyeq arg"
val ty = case absyn of
Absyn.VALexp(_, ref ty,_,_) => ty
| _ => Crash.impossible "Non-val generates poly eq"
val def1 =
if TypeUtils.get_no_cons ty > 1 then
SOME false_val
else
NONE
in
LambdaTypes.SWITCH
(exp_arg,
SOME {num_imms = 1,num_vccs = 0},
[(LambdaTypes.IMM_TAG (Int.toString tag,tag), true_val)],
def1)
end
else
LambdaTypes.APP(fcn, ([arg],[]),SOME annotation)
end
| Absyn.TYPEDexp (expression, _,_) =>
trans_exp(" no_name", expression, env, denv, fnname)
| Absyn.RAISEexp (exp,_) =>
LambdaTypes.RAISE(trans_exp(" no_name", exp, env, denv, fnname))
| Absyn.HANDLEexp (exp, ty, pat_exp_list,location,annotation) =>
let
val old_null_tyfun_spills = store_null_tyfun_spills()
val handle_exp =
(init_null_tyfun_spills();
make_null_tyfun_spills(
LambdaTypes.HANDLE(
trans_exp(annotation, exp, env, denv, fnname),
let
val (root, tree,redundant_clauses,not_exhaustive) =
Match.compile_match pat_exp_list
in
if show_match then
(print"Exception match tree is\n";
Lists.iterate
print
(Match.unparseTree print_options tree "");
print "\n";
())
else ();
let
val ((static_report_required,static_str),
(dynamic_report_required,dynamic_str)) =
case redundant_clauses of
[] => ((false,nil),(false,nil))
| _ => print_redundancy_info (print_options,
redundant_clauses,
pat_exp_list)
in
if static_report_required then
Info.error error_info (Info.WARNING, location,
Lists.reducel op ^
("Redundant patterns in match:",static_str))
else ();
trans_match((root, tree), env, denv, true,
annotation,
LambdaTypes.null_type_annotation,fnname,
if dynamic_report_required then
(length pat_exp_list,dynamic_str,location)
else (0,[],location))
end
end,
annotation)))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
handle_exp
end
| Absyn.FNexp (pat_exp_list, ty, name_string,location) =>
let
val (root, tree,redundant_clauses,not_exhaustive) =
Match.compile_match pat_exp_list
fun print_list print_fn (list, sep, start, finish) =
case list of
[] => start ^ finish
| (x :: xs) =>
Lists.reducel
(fn (str, elem) => str ^ sep ^ print_fn elem)
(start ^ print_fn x, xs) ^ finish
in
if name_string = "Sequence expression"
then
let val lhs_ty = #1(Types.argres(!ty))
in if (Types.type_eq(lhs_ty, Types.empty_rectype, true, true))
then
()
else
Info.error error_info (Info.WARNING, location,
"Non-final expression in a sequence has type "
^ Types.print_type options lhs_ty ^ "." )
end
else ();
if show_match then
(print "Match tree is\n";
Lists.iterate print (Match.unparseTree print_options tree "");
print "\n";
())
else
();
(case not_exhaustive of
SOME missing_constructors =>
Info.error error_info
(Info.WARNING, location,
print_list
(fn (ty, valid_list) =>
case valid_list of
[] => ("missing values of type " ^ Types.print_type options ty)
| _ =>
print_list
(IdentPrint.printValId print_options)
(valid_list, ", ",
"missing constructors of type " ^
Types.print_type options ty ^
" : ",
""))
(missing_constructors, "\n", "Match not exhaustive\n", ""))
| _ => ());
let
val ((static_report_required,static_str),
(dynamic_report_required,dynamic_str)) =
case redundant_clauses of
[] => ((false,nil),(false,nil))
| _ => print_redundancy_info (print_options,
redundant_clauses,
pat_exp_list)
in
if static_report_required then
Info.error error_info (Info.WARNING, location,
Lists.reducel op ^
("Redundant patterns in match:",static_str))
else ();
trans_match((root, tree), env, denv, false,name_string,!ty,fnname,
if dynamic_report_required then
(length pat_exp_list,dynamic_str,location)
else (0,[],location))
end
end
| Absyn.MLVALUEexp value => LambdaTypes.MLVALUE value
| Absyn.DYNAMICexp (exp,_,ref (ty,level,tyvars))=>
(let
val _ =
let
fun error_fn (valid, loc) =
Info.error' error_info
(Info.FATAL, loc,
"Unresolved overloading for "
^ IdentPrint.printValId print_options valid)
in
Types.resolve_overloading (not old_definition,ty,error_fn)
end
val lexpr = trans_exp (name,exp,env, denv, fnname)
val tyexpr = LambdaTypes.MLVALUE
(cast
(TyperepUtils.convert_dynamic_type (use_value_polymorphism,ty,level,tyvars)))
in
LambdaTypes.STRUCT([lexpr,tyexpr],LambdaTypes.TUPLE)
end
handle TyperepUtils.ConvertDynamicType =>
Info.error' error_info (Info.FATAL,Location.UNKNOWN,"Free variables in dynamic type"))
| Absyn.COERCEexp (exp,_,ref atype, _) =>
trans_exp (name, TyperepUtils.make_coerce_expression (exp,atype),
env, denv, fnname)
and trans_match((root, tree), env, denv, is_exn, name_string, ty, fnname_info,
(number_of_clauses,redundant_clauses,location)) =
let
local
val match_trans_count_ref : int ref = ref ~1
in
fun new_match_trans() =
(match_trans_count_ref := (!match_trans_count_ref)+1;
!match_trans_count_ref)
end
local
open LambdaTypes
fun telfun f (EXP_TAG e, e') = (EXP_TAG (f e), f e')
| telfun f (t,e) = (t,f e)
fun optfun f (SOME x) = SOME (f x)
| optfun f NONE = NONE
fun hashmap_find (v,env) = IntHashTable.tryLookup (env,lvar_to_int v)
val new_valid = new_LVar
fun alpha (binds,e) =
let
fun aux (e as INT _) = e
| aux (e as SCON _) = e
| aux (e as MLVALUE _) = e
| aux (e as BUILTIN _) = e
| aux (e as VAR v) =
(case hashmap_find (v,binds) of
SOME e' => e'
| NONE => e)
| aux (APP (e,(el,fpel),ty)) = APP (aux e,(map aux el,map aux fpel),ty)
| aux (STRUCT (el,ty)) = STRUCT (map aux el,ty)
| aux (SWITCH (e,info,tel,opte)) =
SWITCH (aux e,
info,
map (telfun aux) tel,
optfun aux opte)
| aux (HANDLE (e1,e2,s)) = HANDLE(aux e1,aux e2,s)
| aux (RAISE e) = RAISE (aux e)
| aux (SELECT (info,e)) = SELECT (info,aux e)
| aux (LET ((v,i,e1),e2)) =
let
val v' = new_valid ()
val e1' = aux e1
val _ = IntHashTable.update (binds,lvar_to_int v,VAR v')
in
LET((v',i,e1'),aux e2)
end
| aux (FN ((vl,fpvl),body,status,name,ty,info)) =
let
val new_vl = map (fn v => (v,new_valid ())) vl
val new_fpvl = map (fn v => (v,new_valid ())) fpvl
val _ =
Lists.iterate
(fn (v,v') => IntHashTable.update (binds,lvar_to_int v,VAR v'))
(new_vl @ new_fpvl)
in
FN ((map #2 new_vl,map #2 new_fpvl),aux body,status,name,ty,info)
end
| aux (LETREC (fl,el,e)) =
let
val fl' = map (fn (v,info) => (new_valid(),info)) fl
val _ =
Lists.iterate
(fn ((v,_),(v',_)) => IntHashTable.update (binds,lvar_to_int v,VAR v'))
(Lists.zip (fl,fl'))
in
LETREC (fl',map aux el,aux e)
end
in
aux e
end
fun empty_hashmap () =
IntHashTable.new 16
in
fun rename e = alpha (empty_hashmap () ,e)
end
local
open LambdaTypes
in
fun has_bounds(INT _) = false
| has_bounds(SCON _) = false
| has_bounds(MLVALUE _) = false
| has_bounds(BUILTIN _) = false
| has_bounds(VAR _) = false
| has_bounds(APP(e,(el,fpel), _)) =
has_bounds e orelse Lists.exists has_bounds (el@fpel)
| has_bounds(STRUCT(el,_)) = Lists.exists has_bounds el
| has_bounds(SWITCH(e,info,tel,opte)) =
has_bounds e orelse Lists.exists has_bounds_tag_exp tel orelse
has_bounds_opt opte
| has_bounds(HANDLE (e1,e2,_)) = has_bounds e1 orelse has_bounds e2
| has_bounds(RAISE e) = has_bounds e
| has_bounds(SELECT (_,e)) = has_bounds e
| has_bounds(LET _) = true
| has_bounds(FN _) = true
| has_bounds(LETREC _) = true
and has_bounds_opt(SOME e) = has_bounds e
| has_bounds_opt _ = false
and has_bounds_tag_exp(t, e) = has_bounds e orelse has_bounds_tag t
and has_bounds_tag(VCC_TAG _) = false
| has_bounds_tag(IMM_TAG _) = false
| has_bounds_tag(SCON_TAG _) = false
| has_bounds_tag(EXP_TAG e) = has_bounds e
fun size(n, INT _) = n+1
| size(n, SCON _) = n+1
| size(n, MLVALUE _) = n+1
| size(n, BUILTIN _) = n+1
| size(n, VAR _) = n+1
| size(n, APP(e, (el,fpel), _)) =
size(Lists.reducel size (n+1, (fpel @ el)), e)
| size(n, STRUCT(el,_)) = Lists.reducel size (n+1, el)
| size(n, SWITCH(e,info,tel,opte)) =
Lists.reducel size_tag_exp (size_opt(size(n+1, e), opte), tel)
| size(n, HANDLE(e1,e2,_)) = size(size(n+1, e1), e2)
| size(n, RAISE e) = size(n+1, e)
| size(n, SELECT(_,e)) = size(n+1, e)
| size(n, LET((_, _, le), le')) = size(size(n+1, le), le')
| size(n, FN(_, le, _, _, _, _)) = size(n+1, le)
| size(n, LETREC(_, lel, le)) = Lists.reducel size (size(n+1, le), lel)
and size_opt(n, SOME e) = size(n, e)
| size_opt(n, _) = n
and size_tag_exp(n, (t, e)) = size(size_tag(n, t), e)
and size_tag(n, VCC_TAG _) = n+1
| size_tag(n, IMM_TAG _) = n+1
| size_tag(n, SCON_TAG _) = n+1
| size_tag(n, EXP_TAG e) = size(n+1, e)
end
val debugging = generate_debug_info orelse generate_moduler orelse debug_variables
val binding_list = ref ([] : (LambdaTypes.LVar * LambdaTypes.LambdaExp) list)
fun find_binding lv =
if debugging then
SOME(Lists.assoc(lv, !binding_list))
handle Lists.Assoc => NONE
else
NONE
fun report_binding(lv, opt_e) =
()
fun add_binding(lv, le) =
if debugging then
let
val arg as (_, e) = (lv, LambdaOptimiser.simple_beta_reduce le)
in
if size(0, e) <= 200 then
binding_list := arg :: !binding_list
else
()
end
else
()
val root_lambda = new_LVar()
val excp =
if is_exn then
LambdaTypes.RAISE(LambdaTypes.VAR root_lambda)
else
LambdaTypes.RAISE(LambdaTypes.STRUCT([LambdaTypes.BUILTIN Pervasives.EXMATCH, unit_exp],LambdaTypes.CONSTRUCTOR))
val match_env =
add_match_env((root,(root_lambda,NONE)),empty_match_env)
fun filter_name (name_string,fnname) =
if funny_name_p name_string then fnname else name_string
val fnname =
let
val name =
case fnname_info of
SOME (s,_) => s
| _ => ""
in
filter_name (name_string,name)
end
val fnname_lv = new_LVar ()
fun redundancy_code () =
case redundant_clauses of
nil => ()
| _ =>
let
val redundant_clauses_lambda = new_LVar()
val MLWorks_Internal_StandardIO_printError =
cg_longvalid(make_longid (["MLWorks","Internal","StandardIO"],"printError"),env)
fun redundant_clauses_lambdas nil = nil
| redundant_clauses_lambdas (((_,exns),rc)::rcs) =
let
val lvar = new_LVar()
in
(lvar,exns,rc,
LambdaTypes.LETB
(lvar,
NONE,
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.REF,
([LambdaTypes.SCON(Ident.STRING rc, NONE)],[]),
NONE)))
::redundant_clauses_lambdas rcs
end
val redundant_clauses = redundant_clauses_lambdas redundant_clauses
val bindings = map (fn (_,_,_,bd) => bd) redundant_clauses
fun make_dynamic_redundancy_report() =
let
fun report nil = LambdaTypes.SCON(Ident.STRING("\n"), NONE)
| report ((lvar,_,_,_)::rcs) =
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.HAT,
([LambdaTypes.STRUCT([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([LambdaTypes.VAR lvar],[]),
NONE),
report rcs],
LambdaTypes.TUPLE)],
[]),
NONE)
in
LambdaTypes.SWITCH(
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.INTEQ,
([LambdaTypes.STRUCT([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([LambdaTypes.VAR redundant_clauses_lambda],[]),
NONE),
LambdaTypes.SCON(Ident.INT("0",Location.UNKNOWN), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE),
SOME {num_imms = 2,num_vccs = 0},
[(LambdaTypes.IMM_TAG ("",1),unit_exp),
(LambdaTypes.IMM_TAG ("",0),
LambdaTypes.APP(MLWorks_Internal_StandardIO_printError,
([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.HAT,
([LambdaTypes.STRUCT([LambdaTypes.SCON(Ident.STRING(
Location.to_string location ^ ": " ^ "warning" ^ ": " ^
"Redundant patterns in match:\n"), NONE),
report redundant_clauses],
LambdaTypes.TUPLE)],
[]),
NONE)],
[]),
NONE))],
NONE)
end
fun redundancy_code nil = make_dynamic_redundancy_report()
| redundancy_code ((lvar,exns,rc,_)::rcs) =
let
fun exn_code (exn as Ident.LONGVALID(p,s)) =
let
val (exp,longstrid) = cg_longexid(exn,env)
val select_exp =
case (longstrid,exn) of
(EnvironTypes.STRIDSPEC (Ident.LONGSTRID(p,Ident.STRID s)),
Ident.LONGVALID(p',s')) =>
let
val newlongstrid =
Ident.LONGVALID (combine_paths (p, combine_paths (Ident.PATH(s,Ident.NOPATH),p')),s')
in
#1(cg_longexid (newlongstrid,env))
end
| (EnvironTypes.VARSPEC lvar, _) =>
LambdaTypes.VAR lvar
| (EnvironTypes.NOSPEC, _) => exp
in
LambdaTypes.SELECT({index = 0, size = 2,selecttype=LambdaTypes.CONSTRUCTOR},
select_exp)
end
fun switches (Match.==(exn1,exn2)) =
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.INTEQ,
([LambdaTypes.STRUCT([exn_code exn1,exn_code exn2],
LambdaTypes.TUPLE)],
[]),
NONE)
| switches (Match.&&(exn1,exn2)) =
LambdaTypes.SWITCH(switches exn1,
SOME {num_imms = 2,num_vccs = 0},
[(LambdaTypes.IMM_TAG ("",1),switches exn2),
(LambdaTypes.IMM_TAG ("",0),LambdaTypes.INT 0)],
NONE)
| switches (Match.||(exn1,exn2)) =
LambdaTypes.SWITCH(switches exn1,
SOME {num_imms = 2,num_vccs = 0},
[(LambdaTypes.IMM_TAG ("",1),LambdaTypes.INT 1),
(LambdaTypes.IMM_TAG ("",0),switches exn2)],
NONE)
| switches Match.TRUE = LambdaTypes.INT 1
| switches Match.FALSE = LambdaTypes.INT 0
in
LambdaTypes.LET((new_LVar(),NONE,
LambdaTypes.SWITCH(switches exns,
SOME {num_imms = 2,num_vccs = 0},
[(LambdaTypes.IMM_TAG ("",1),
LambdaTypes.STRUCT([
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.BECOMES,
([LambdaTypes.STRUCT([LambdaTypes.VAR lvar,
LambdaTypes.SCON (Ident.STRING("\n  ->" ^ (String.extract (rc, 5, NONE))), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE),LambdaTypes.INT 1],
LambdaTypes.TUPLE)),
(LambdaTypes.IMM_TAG ("",0),
LambdaTypes.STRUCT([
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.BECOMES,
([LambdaTypes.STRUCT([LambdaTypes.VAR redundant_clauses_lambda,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.INTMINUS,
([LambdaTypes.STRUCT([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([LambdaTypes.VAR redundant_clauses_lambda],[]),
NONE),
LambdaTypes.SCON(Ident.INT("1",Location.UNKNOWN), NONE)],
LambdaTypes.TUPLE)],
[]),
NONE)],
LambdaTypes.TUPLE)],[]),
NONE), LambdaTypes.INT 1],
LambdaTypes.TUPLE))],
NONE)),
redundancy_code rcs)
end
in
(dynamic_redundancy_report_ref :=
let
val dynamic_report = !dynamic_redundancy_report_ref
in
fn exp =>
dynamic_report
(LambdaTypes.LET
((new_LVar(),NONE,
let_lambdas_in_exp
(LambdaTypes.LETB
(redundant_clauses_lambda,NONE,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([LambdaTypes.SCON(Ident.INT(Int.toString number_of_clauses,
Location.UNKNOWN), NONE)],
[]),
NONE)) ::
bindings,redundancy_code redundant_clauses)),
exp))
end)
end
fun tr_match(tree, match_env, val_env) =
let
fun ensure_default (LambdaTypes.SWITCH (exp,info,tel,NONE)) =
let
fun last ([],_) = Crash.impossible "ensure_default"
| last ([a],acc) = (rev acc,a)
| last (a::rest,acc) = last (rest,a::acc)
val (tel',(t,e)) = last (tel,[])
in
LambdaTypes.SWITCH (exp,info,tel',SOME e)
end
| ensure_default exp = exp
fun lvar_let_lambdas_in_exp (lambda_exp,bindings,lvar_info) =
let
fun add_match_string (name, match_string) =
let
val match_string' = explode match_string
fun aux (#"[" ::rest,acc) =
(implode (rev acc)) ^ match_string ^
(implode( #"[" :: rest))
| aux (c::rest,acc) = aux (rest,c::acc)
| aux ([],acc) = name ^ match_string
in
aux (explode name,[])
end
val (this_lvar,this_lexp) =
case lvar_info of
SOME lvar => lvar
| NONE => (new_LVar(), LambdaTypes.INT 1)
fun do_binding nil = nil
| do_binding (Match.INL (ref NONE)::bindings) =
do_binding bindings
| do_binding (Match.INL (ref (SOME (lvar as ref(Match.INL(_,tree)))))::bindings) =
let
val lv = new_LVar()
val match_fnname =
if fnname = name_string then
add_match_string (name_string,"<Match" ^ Int.toString(new_match_trans()) ^ ">")
else
add_match_string (name_string,
"<Match" ^ Int.toString(new_match_trans()) ^ ">")
val _ = lvar := Match.INR lv
val fnbody =
let
val old_null_tyfun_spills = store_null_tyfun_spills()
val tr_match =
(init_null_tyfun_spills();
make_null_tyfun_spills(tr_match(tree,match_env,val_env)))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
ensure_default tr_match
end
val fnexp =
LambdaTypes.FN(([new_LVar()],[]),
fnbody,
LambdaTypes.BODY,
match_fnname,
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION)
val _ = add_binding(lv, fnbody)
in
LambdaTypes.LETB
(lv, NONE,
fnexp) :: do_binding bindings
end
| do_binding (Match.INL _::_) =
Crash.impossible "1:do_binding:lvar_let_lambdas_in_exp:Match_translator:_lambda.sml"
| do_binding (Match.INR(lvar' as ref (Match.INL (_,tree)),matchvar)::bindings) =
let
val lv = new_LVar()
val match_fnname =
add_match_string (name_string,"<Match" ^ Int.toString(new_match_trans()) ^ ">")
val _ = lvar' := Match.INR lv
val fnbody =
let
val old_null_tyfun_spills = store_null_tyfun_spills()
val tr_match =
(init_null_tyfun_spills();
make_null_tyfun_spills
(case matchvar of
~1 => tr_match(tree, match_env, val_env)
| _ =>
LambdaTypes.LET
((this_lvar,NONE,this_lexp),
tr_match
(tree,
add_match_env
((matchvar,(this_lvar,NONE)),
match_env),
val_env))))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
ensure_default tr_match
end
val fnexp =
LambdaTypes.FN
(([new_LVar()],[]),
fnbody,
LambdaTypes.BODY,
match_fnname,
LambdaTypes.null_type_annotation,
RuntimeEnv.INTERNAL_FUNCTION)
val _ = add_binding(lv, fnbody)
in
LambdaTypes.LETB
(lv,NONE,
fnexp) :: do_binding bindings
end
| do_binding (Match.INR _::_) =
Crash.impossible "2:do_binding:lvar_let_lambdas_in_exp:Match_translator:_lambda.sml"
in
let_lambdas_in_exp (do_binding bindings,lambda_exp())
end
fun Tr_Default default =
let
fun Tr_Default (ref(Match.BUILT(ref(Match.INR lvar)))) =
let
val opt_e = find_binding lvar
val _ = report_binding(lvar, opt_e)
in
case opt_e of
SOME y => SOME(rename y)
| _ =>
SOME(LambdaTypes.APP(LambdaTypes.VAR lvar, ([unit_exp],[]), NONE))
end
| Tr_Default (ref(Match.BUILT(ref(Match.INL(0,tree))))) =
SOME
(init_null_tyfun_spills();
make_null_tyfun_spills
(tr_match(tree, match_env, val_env)))
| Tr_Default (ref(Match.ERROR _)) =
SOME excp
| Tr_Default _ = Crash.impossible "1:Tr_Default:tr_match:_lambda.sml"
in
case default of
Match.INL (SOME default) => Tr_Default default
| Match.INL NONE => NONE
| Match.INR (ref (Match.INR lvar)) =>
let
val opt_e = find_binding lvar
val _ = report_binding(lvar, opt_e)
in
case opt_e of
SOME y => SOME(rename y)
| _ =>
SOME (LambdaTypes.APP
(LambdaTypes.VAR lvar, ([unit_exp],[]), NONE))
end
| Match.INR _ =>
Crash.impossible "2:Tr_Default:tr_match:_lambda.sml"
end
in
case tree of
Match.LEAF(exp, n, mv_valid_ty_list) =>
let
fun do_leaf([], env, denv) =
trans_exp(" match_leaf", exp, env, denv, SOME (fnname,fnname_lv))
| do_leaf((mv, valid, ty) :: tl, env, denv) =
case lookup_match(mv, match_env) of
(lv, NONE) =>
if variable_debug then
(case valid of
Ident.VAR symbol =>
let
val dummylv = new_LVar()
val debug_info =
RuntimeEnv.VARINFO
(Symbol.symbol_name symbol,
ty,
NONE)
in
(null_tyfun_spills (denv,debug_info);
LambdaTypes.LET
((dummylv,SOME (ref debug_info),
LambdaTypes.VAR lv),
do_leaf(tl,
Environ.add_valid_env(env,
(valid,
EnvironTypes.LAMB(lv,EnvironTypes.NOSPEC))),
add_valid_denv(denv, (valid,EnvironTypes.NULLEXP)))))
end
| _ =>
do_leaf(tl, Environ.add_valid_env(env, (valid,
EnvironTypes.LAMB(lv,EnvironTypes.NOSPEC))),
add_valid_denv(denv, (valid,EnvironTypes.NULLEXP))))
else
do_leaf(tl, Environ.add_valid_env(env, (valid,
EnvironTypes.LAMB(lv,EnvironTypes.NOSPEC))),
add_valid_denv(denv, (valid,EnvironTypes.NULLEXP)))
| (lv, SOME varinforef) =>
Crash.impossible("debug info already set for " ^
IdentPrint.printValId print_options valid)
in
do_leaf(rev mv_valid_ty_list, val_env, denv)
end
| Match.SCON(mv, scon_tree_list, default, binding, opt_size) =>
let
fun lambda_exp() =
let
val old_null_tyfun_spills = store_null_tyfun_spills()
in
LambdaTypes.SWITCH(
LambdaTypes.VAR(#1(lookup_match(mv, match_env))),
NONE,
map (fn (scon,tree) =>
(LambdaTypes.SCON_TAG (scon, opt_size),
(init_null_tyfun_spills();
make_null_tyfun_spills(tr_match(tree, match_env, val_env)))))
scon_tree_list,
let
val default =
case default of
SOME default =>
Tr_Default (Match.INL(SOME default))
| _ => NONE
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
default
end)
end
in
lvar_let_lambdas_in_exp (lambda_exp,
case binding of
NONE => []
| SOME bd =>[Match.INL bd],
NONE)
end
| Match.CONSTRUCTOR(ty, mv, longvalid_mv_tree_list, default, binding, exception_tree) =>
let
val ORIG_LV = lookup_match(mv, match_env)
val orig_lv = #1 ORIG_LV
val lv_e = LambdaTypes.VAR orig_lv
val (_,type_val_env) =
TypeUtils.get_valenv(TypeUtils.get_cons_type ty)
val is_exn =
case longvalid_mv_tree_list of
{1=Ident.LONGVALID(_, Ident.EXCON _), ...} :: _ => true
| {1=Ident.LONGVALID(_, Ident.CON _), ...} :: _ => false
| _ => Crash.impossible "Match.CONS bad arg"
fun has_value(Ident.LONGVALID(_, valid as Ident.CON _)) =
(case TypeUtils.type_from_scheme(
NewMap.apply'(type_val_env, valid)) of
Datatypes.FUNTYPE _ => true
| _ => false
)
| has_value(Ident.LONGVALID(_, Ident.VAR _)) =
Crash.impossible"VAR in match CONS"
| has_value(Ident.LONGVALID(_, Ident.TYCON' _)) =
Crash.impossible"TYCON' in match CONS"
| has_value(Ident.LONGVALID(_, Ident.EXCON excon)) = true
val new_lv = new_LVar()
val new_le = GetConVal lv_e
val con_field = GetConTag lv_e
val vcc_lv_list =
Lists.filterp (has_value o #1) longvalid_mv_tree_list
fun tr_match'(Match.INL tree,match_env, val_env) =
tr_match(tree, match_env, val_env)
| tr_match'(Match.INR(ref(Match.INR lvar)),_, _) =
let
val opt_e = find_binding lvar
val _ = report_binding(lvar, opt_e)
in
case opt_e of
SOME x => rename x
| _ =>
LambdaTypes.APP(LambdaTypes.VAR lvar,
([unit_exp],[]),
NONE)
end
| tr_match'(Match.INR _, _, _) =
Crash.impossible "tr_match':trans_match:lambda"
fun mk_branch (id as Ident.LONGVALID(_, valid), mv, tree) =
if has_value id then
(LambdaTypes.VCC_TAG(valid_name valid,#2(constructor_tag(valid, ty))),
if is_list_type ty then
tr_match'(tree,
add_match_env((mv, ORIG_LV), match_env),
val_env)
else
LambdaTypes.do_binding
let
val info = NONE
in
(LambdaTypes.LETB(new_lv, info, new_le),
tr_match'(tree,
add_match_env((mv, (new_lv,info)), match_env),
val_env))
end)
else
(LambdaTypes.IMM_TAG(valid_name valid,#2(constructor_tag(valid, ty))),
tr_match'(tree, match_env, val_env))
fun lambda_exp1() =
if TypeUtils.get_no_cons ty = 1 then
if length vcc_lv_list <> 0 then
let val (name, mv, tree) =
case vcc_lv_list of
[x] => x
| _ => Crash.impossible "list size"
in
case Environ.FindBuiltin(name, val_env) of
SOME Pervasives.REF =>
let
val new_lv = new_LVar ()
val info = NONE
in
LambdaTypes.do_binding
(LambdaTypes.LETB
(new_lv,info,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([LambdaTypes.VAR orig_lv],[]),
NONE)),
tr_match'(tree,
add_match_env((mv, (new_lv,info)),
match_env),
val_env)
)
end
| _ =>
tr_match'(tree,
add_match_env((mv, ORIG_LV),match_env),
val_env)
end
else
let val tree = case longvalid_mv_tree_list of
[(_, _, tree)] => tree
| _ => Crash.impossible "list size"
in
tr_match'(tree, match_env, val_env)
end
else
let
val old_null_tyfun_spills = store_null_tyfun_spills()
in
LambdaTypes.SWITCH(
lv_e,
SOME{
num_vccs = TypeUtils.get_no_vcc_cons ty,
num_imms = TypeUtils.get_no_null_cons ty
},
map (fn tree=>
let
val (tag,tree) =
(init_null_tyfun_spills();
mk_branch tree)
in
(tag,make_null_tyfun_spills tree)
end) longvalid_mv_tree_list,
let
val default =
if length longvalid_mv_tree_list =
TypeUtils.get_no_cons ty then
NONE
else Tr_Default default
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
default
end)
end
fun lambda_exp2() =
let
val info = NONE
in
let_lambdas_in_exp
([LambdaTypes.LETB(new_lv,info,new_le)],
let
val old_null_tyfun_spills = store_null_tyfun_spills()
in
LambdaTypes.SWITCH
(select_exn_unique con_field,
SOME{num_vccs = 0,num_imms = 0},
map (fn (longvalid, mv, tree) =>
(LambdaTypes.EXP_TAG
(select_exn_unique(#1(cg_longexid(longvalid, val_env)))),
(init_null_tyfun_spills();
make_null_tyfun_spills
(tr_match'(tree,
add_match_env((mv, (new_lv,info)),match_env),
val_env)))))
vcc_lv_list,
let
val default = Tr_Default default
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
default
end)
end)
end
fun lambda_exp3() =
let
val old_null_tyfun_spills = store_null_tyfun_spills()
in
LambdaTypes.SWITCH(
select_exn_unique con_field,
SOME{num_vccs = 0,num_imms = 0},
map (fn (longvalid, _, tree) =>
(LambdaTypes.EXP_TAG(select_exn_unique(#1(cg_longexid(longvalid, val_env)))),
(init_null_tyfun_spills();
make_null_tyfun_spills(tr_match'(tree,match_env,val_env)))))
vcc_lv_list,
let
val default = Tr_Default default
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
default
end)
end
in
if exception_tree then
lvar_let_lambdas_in_exp (lambda_exp3, binding, NONE)
else
case is_exn of
false =>
lvar_let_lambdas_in_exp (lambda_exp1, binding, NONE)
| true =>
lvar_let_lambdas_in_exp (lambda_exp2, binding, SOME(new_lv,new_le))
end
| Match.RECORD(ty, mv, lab_mv_list, tree) =>
let
val lab_mv_lv_list =
if variable_debug then
map (fn (x,y) => (x, y,
(new_LVar(), NONE)))
lab_mv_list
else
map
(fn (x,y) => (x, y,(new_LVar(), NONE)))
lab_mv_list
val le = LambdaTypes.VAR (#1(lookup_match(mv, match_env)))
val new_env = Lists.reducel
(fn (env, (_, x, y)) => add_match_env((x, y), env))
(match_env, lab_mv_lv_list)
val lv_le_list =
map (fn (lab,_, (lv,info)) =>
LambdaTypes.LETB(lv,info,
LambdaTypes.SELECT(record_label_offset(lab,ty,error_info,location),le)))
lab_mv_lv_list
in
unordered_let_lambdas_in_exp
(lv_le_list, tr_match(tree, new_env, val_env))
end
| Match.DEFAULT(default,binding) =>
let
fun lambda_exp() =
case Tr_Default (Match.INL(SOME default)) of
NONE => Crash.impossible "Match.DEFAULT:trans_match:lambda"
| SOME lexp => lexp
in
lvar_let_lambdas_in_exp (lambda_exp,
case binding of
NONE => []
| SOME bd => [Match.INL bd],
NONE)
end
end
val debug_info = RuntimeEnv.NOVARINFO
in
(redundancy_code();
LambdaTypes.FN(([root_lambda],[]),
let
val old_null_tyfun_spills = store_null_tyfun_spills()
val tr_match =
(init_null_tyfun_spills();
make_null_tyfun_spills
(null_tyfun_spills (denv,debug_info);
tr_match (tree, match_env,env)))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
tr_match
end,
LambdaTypes.BODY,
name_string,ty,RuntimeEnv.INTERNAL_FUNCTION))
end
and trans_dec(some_dec, envir, is_toplevel, denvir, fnname) =
let
val excp = SOME
(LambdaTypes.RAISE
(LambdaTypes.STRUCT
([LambdaTypes.BUILTIN Pervasives.EXBIND, unit_exp],
LambdaTypes.CONSTRUCTOR)))
fun do_datatypeinfo_list datatypeinfo_list =
let
fun trans_single_datatype(_, _, _, _, v_tref_topt_list) =
let
fun munge((valid as Ident.CON sy, ref ty), _) =
(Absyn.VALpat ((mklongvalid (Ident.VAR sy),(ref ty,ref null_runtimeinfo)),
Location.UNKNOWN),
Absyn.VALexp (mklongvalid valid,ref ty,
Location.UNKNOWN, ref (Datatypes.ZERO, NONE)),
Location.UNKNOWN)
| munge _ = Crash.impossible"Absyn.DATATYPE"
val valdec_list = map munge v_tref_topt_list
in
trans_dec(Absyn.VALdec(valdec_list, [], Set.empty_set,[]),
Environ.empty_env, false, denvir, fnname)
end
val e_l_list = map trans_single_datatype datatypeinfo_list
val old_null_tyfun_spills = store_null_tyfun_spills()
val _ = init_null_tyfun_spills()
val (env, denv, spills) =
DATATYPEdec_spills
(denvir,datatypeinfo_list,
(env_from_list e_l_list, denv_from_list e_l_list, nil))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
val new_spills = Lists.reducel (fn (l1, (_, _, l2)) => l1 @ l2) ([], e_l_list)
in
(env,denv,spills @ new_spills)
end
in
case some_dec of
Absyn.VALdec (non_rec_list, rec_list, _,_) =>
let
fun trans_valdec(non_rec_list, rec_list) =
let
fun trans_individual_dec(pattern, lambda_var, location) =
case pattern of
Absyn.WILDpat _ => (true, true, Environ.empty_env, empty_denv, [],
RuntimeEnv.NOVARINFO,
NONE)
| Absyn.SCONpat (scon, ref ty) =>
let
val dummy_lv = new_LVar()
in
(false, false, Environ.empty_env, empty_denv,
[LambdaTypes.LETB
(dummy_lv,NONE,
LambdaTypes.SWITCH
(LambdaTypes.VAR lambda_var,
NONE,
[(LambdaTypes.SCON_TAG (scon, Types.sizeof ty),
LambdaTypes.VAR lambda_var)],
excp))],
RuntimeEnv.NOVARINFO,NONE)
end
| Absyn.VALpat ((longvalid as Ident.LONGVALID(path, valid),
stuff as (ref ty,ref (RuntimeEnv.RUNTIMEINFO (instance,_)))),_) =>
(case valid of
Ident.VAR symbol =>
(case path of
Ident.NOPATH =>
((true, true, Environ.add_valid_env(Environ.empty_env, (valid,
EnvironTypes.LAMB(lambda_var, EnvironTypes.NOSPEC))),
add_valid_denv(empty_denv, (valid,
EnvironTypes.NULLEXP)), [],
RuntimeEnv.VARINFO(Symbol.symbol_name symbol,
stuff,NONE),
instance))
| Ident.PATH _ => Crash.impossible
"Long valid with non-empty path to trans_dec")
| Ident.CON _ =>
let
val dummy_lv = new_LVar()
val tag = #2(constructor_tag(valid, ty))
val one_con = TypeUtils.get_no_cons ty = 1
in
(false, one_con, Environ.empty_env, empty_denv,
[LambdaTypes.LETB(
dummy_lv,NONE,
LambdaTypes.SWITCH(
LambdaTypes.VAR lambda_var,
SOME{
num_imms = TypeUtils.get_no_null_cons ty,
num_vccs = TypeUtils.get_no_vcc_cons ty},
[(LambdaTypes.IMM_TAG (valid_name valid,tag),
LambdaTypes.VAR lambda_var)],
if one_con then NONE else excp
))
], RuntimeEnv.NOVARINFO, NONE)
end
| Ident.EXCON excon =>
let
val lexp = LambdaTypes.VAR lambda_var
val dummy_lv = new_LVar()
in
(false, false, Environ.empty_env, empty_denv,
[LambdaTypes.LETB(dummy_lv,NONE,
LambdaTypes.SWITCH(
GetConTag lexp,
SOME{num_imms = 0,
num_vccs = 0
},
[(LambdaTypes.EXP_TAG(#1(cg_longexid (longvalid, envir))),
GetConVal lexp)],
excp
))],
RuntimeEnv.NOVARINFO,
NONE)
end
| _ => Crash.impossible "TYCON':VALpat:trans_individual_dec:lambda"
)
| Absyn.RECORDpat(lab_pat_list, flex, ref ty) =>
let
val big_list =
map (fn (lab, pat) =>
(pat, new_LVar(),
LambdaTypes.SELECT(record_label_offset(lab,ty,error_info,location),
LambdaTypes.VAR lambda_var)))
lab_pat_list
val env_list_lambda_list_list =
map
(fn (pat, lv, le) =>
let
val (has_vars, exhaustive, env, denv, lambda_list,debug_info,instance) =
trans_individual_dec(pat,lv, location)
in
(has_vars, exhaustive, env, denv,
make_binding (lv,debug_info,instance,le,"",location)
@ lambda_list)
end)
big_list
in
(Lists.exists (fn (has_vars, _, _, _, _) => has_vars)
env_list_lambda_list_list,
Lists.forall (fn (_, exhaustive, _, _, _) => exhaustive)
env_list_lambda_list_list,
Lists.reducel (fn (env, (_, _, env', _, _)) =>
Environ.augment_env(env, env'))
(Environ.empty_env, env_list_lambda_list_list),
Lists.reducel (fn (env, (_, _, _, env', _)) =>
augment_denv(env, env'))
(empty_denv, env_list_lambda_list_list),
Lists.reducel
(fn (l1, (_, _, _, _, l2)) => l1 @ l2)
([], env_list_lambda_list_list),RuntimeEnv.NOVARINFO,
NONE)
end
| Absyn.APPpat((longvalid, ref ty), pat,_,_) =>
(case longvalid of
Ident.LONGVALID(_, Ident.VAR _) =>
Crash.impossible"APPpat of Ident.VAR"
| Ident.LONGVALID(_, valid as Ident.CON con) =>
(case Environ.FindBuiltin(longvalid, envir) of
SOME Pervasives.REF =>
let
val new_lv = new_LVar()
val (has_vars, exhaustive, new_env, new_denv, new_lambda_exp,
debug_info,instance) =
trans_individual_dec(pat, new_lv, location)
val lexp = LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.DEREF,
([LambdaTypes.VAR lambda_var],[]),
NONE)
in
(has_vars, exhaustive, new_env, new_denv,
make_binding(new_lv,debug_info,instance,lexp,
"Dereferencing a pattern",location)
@ new_lambda_exp,RuntimeEnv.NOVARINFO,
NONE)
end
| _ =>
if TypeUtils.get_no_cons ty = 1 then
trans_individual_dec(pat, lambda_var, location)
else
let
val new_lv = new_LVar()
val (has_vars, _, new_env, new_denv, new_lambda_exp,
debug_info,instance) =
trans_individual_dec(pat, new_lv,location)
val lexp = LambdaTypes.VAR lambda_var
val tag = #2(constructor_tag(valid, ty))
val lexp =
LambdaTypes.SWITCH
(lexp,
SOME
{num_vccs = TypeUtils.get_no_vcc_cons ty,
num_imms = TypeUtils.get_no_null_cons ty
},
[(LambdaTypes.VCC_TAG (valid_name valid,tag),
if is_list_type (TypeUtils.get_cons_type ty) then
lexp
else
GetConVal lexp
)],
if TypeUtils.get_no_cons ty <> 1 then
excp
else
NONE)
in
(has_vars, false, new_env, new_denv,
make_binding(new_lv, debug_info,
instance,lexp,"", location)
@ new_lambda_exp,RuntimeEnv.NOVARINFO,
NONE)
end)
| Ident.LONGVALID(_, Ident.EXCON excon) =>
let
val new_lv = new_LVar()
val (has_vars, _, new_env, new_denv, new_lambda_exp,
debug_info,instance) =
trans_individual_dec(pat, new_lv,location)
val lexp = LambdaTypes.VAR lambda_var
val lexp = LambdaTypes.SWITCH(
GetConTag lexp,
SOME
{num_imms = 0,
num_vccs = 0},
[(LambdaTypes.EXP_TAG(#1(cg_longexid(longvalid, envir))),
GetConVal lexp)],
excp
)
in
(has_vars, false, new_env, new_denv,
make_binding(new_lv,debug_info,instance,lexp,"",
location)
@ new_lambda_exp,
RuntimeEnv.NOVARINFO,
NONE)
end
| _ => Crash.impossible "TYCON':APPpat:trans_individual_dec:lambda"
)
| Absyn.TYPEDpat(typed, _,_) =>
trans_individual_dec(typed, lambda_var, location)
| Absyn.LAYEREDpat((valid, stuff as (ref ty,ref (RuntimeEnv.RUNTIMEINFO (instance,_)))), pat) =>
(case valid of
Ident.VAR vid =>
let
val (_, exhaustive, env, denv, lambda,_,_) =
trans_individual_dec(pat, lambda_var, location)
in
(true, exhaustive, Environ.add_valid_env(env,
(valid, EnvironTypes.LAMB(lambda_var, EnvironTypes.NOSPEC))),
add_valid_denv(denv, (valid, EnvironTypes.NULLEXP)),
lambda,RuntimeEnv.VARINFO (Symbol.symbol_name vid,stuff,NONE),
instance)
end
| _ => Crash.impossible "LAYEREDpat with non-VAR valid")
in
case (non_rec_list, rec_list) of
((pat, exp, location) :: tl, _) =>
let
val lvar = new_LVar()
val (has_vars, exhaustive, more_env, more_denv, more_lambda,debug_info,instance) =
trans_individual_dec(pat,lvar, location)
val _ =
if exhaustive orelse is_toplevel then ()
else
Info.error error_info (Info.WARNING, location, "Binding not exhaustive")
val _ =
if has_vars orelse is_toplevel then ()
else
(Info.error error_info (Info.WARNING, location, "Binding has no variables");
(if exhaustive then ()
else
Info.error error_info (Info.WARNING, location,
"Possible attempt to rebind constructor name")))
val _ = null_tyfun_spills (denvir,debug_info)
val lambda = trans_exp(" pattern", exp, envir, denvir, fnname)
val (more_env, more_denv, updated) =
case (pat, lambda) of
(Absyn.VALpat((Ident.LONGVALID(Ident.NOPATH,valid as Ident.VAR _), _),_),
LambdaTypes.BUILTIN prim) =>
(Environ.add_valid_env(more_env, (valid,EnvironTypes.PRIM prim)),
add_valid_denv(more_denv, (valid,EnvironTypes.NULLEXP)),
true)
| _ => (more_env, more_denv, false)
val _ = map
(fn LambdaTypes.LETB(_,SOME(ref debug_info),_) =>
null_tyfun_spills (denvir,debug_info)
| _ => ()) more_lambda
val (rest_env, rest_denv, rest_lambda) = trans_valdec(tl,rec_list)
val new_bindings = more_lambda @ rest_lambda
in
(Environ.augment_env(more_env, rest_env),
augment_denv(more_denv, rest_denv),
(if updated then
new_bindings
else
make_binding(lvar,debug_info,instance,lambda,"",
location)
@ new_bindings))
end
| ([], []) => (Environ.empty_env, empty_denv, [])
| ([], rec_list) =>
let
val lv_pat_exp_list =
if variable_debug
then
map
(fn (pat, exp, location) =>
let
val new_lv = new_LVar()
in
((new_lv,SOME (ref RuntimeEnv.NOVARINFO)),pat, exp, location)
end)
rec_list
else
map
(fn (pat, exp, location) => ((new_LVar(),NONE), pat, exp, location))
rec_list
val env_le_list = map
(fn ((lv,SOME varinfo_ref), pat, _, location) =>
let
val (has_vars, exhaustive, env, denv, le, debug_info,instance) =
trans_individual_dec(pat,lv,location)
val _ =
(varinfo_ref := debug_info;
if exhaustive then ()
else
Info.error error_info (Info.WARNING, location,
"Binding not exhaustive"))
val _ =
if has_vars then ()
else
Info.error error_info (Info.WARNING, location,
"Binding has no variables")
in
(env,denv,lv,debug_info,instance,location)
end
| ((lv,NONE), pat, _, location) =>
let
val (has_vars, exhaustive, env, denv, le, debug_info,instance) =
trans_individual_dec(pat,lv,location)
val _ =
if exhaustive then ()
else
Info.error error_info (Info.WARNING, location,
"Binding not exhaustive")
val _ =
if has_vars then ()
else
Info.error error_info (Info.WARNING, location,
"Binding has no variables")
in
(env,denv,lv,debug_info,instance,location)
end)
lv_pat_exp_list
val all_env =
Lists.reducel Environ.augment_env (Environ.empty_env, map #1 env_le_list)
val all_denv =
Lists.reducel augment_denv (empty_denv, map #2 env_le_list)
val trans_env = Environ.augment_env(envir, all_env)
val trans_denv = augment_denv(denvir, all_denv)
fun pat_name (Absyn.VALpat ((Ident.LONGVALID (_, valid), _),_)) = valid_name valid
| pat_name (Absyn.WILDpat _) = "<wild>"
| pat_name (Absyn.LAYEREDpat ((valid, _), pat)) =
(case pat_name pat of
"<wild>" => valid_name valid
| name => name)
| pat_name (Absyn.TYPEDpat (pat,_,_)) = pat_name pat
| pat_name _ = Crash.impossible"Bad pat name in val rec"
val recletb =
[LambdaTypes.RECLETB(map #1 lv_pat_exp_list,
map
(fn (pat,exp,_) =>
trans_exp(pat_name pat, exp, trans_env,
trans_denv, fnname))
rec_list)]
in
(all_env, all_denv, recletb)
end
end
in
trans_valdec(non_rec_list, rec_list)
end
| Absyn.TYPEdec typeinfo_list =>
let
val old_null_tyfun_spills = store_null_tyfun_spills()
val (env, denv, spills) =
(init_null_tyfun_spills();
TYPEdec_spills denvir typeinfo_list (Environ.empty_env, empty_denv, nil))
val _ = restore_null_tyfun_spills old_null_tyfun_spills
in
(env, denv, spills)
end
| Absyn.DATATYPEdec (_,datatypeinfo_list) =>
do_datatypeinfo_list datatypeinfo_list
| Absyn.DATATYPErepl (location,(tycon,longtycon),constructors) =>
let val Datatypes.VE(intRef,nameToTypeMap) = valOf (!constructors)
handle Option => Crash.impossible
"replicating datatype with no constructors"
fun makeConbinds nameToTypeMap =
map (fn (valid,Datatypes.SCHEME(_,(ty,_))) =>
((valid,ref ty),NONE)
| (valid,Datatypes.UNBOUND_SCHEME(ty,_)) =>
((valid, ref ty),NONE)
| _ => Crash.impossible
"constructors with overloaded typescheme")
(NewMap.to_list_ordered nameToTypeMap)
in
do_datatypeinfo_list [([],tycon,ref Absyn.nullType,NONE,
makeConbinds nameToTypeMap)]
end
| Absyn.ABSTYPEdec (_,datatypeinfo_list, dec) =>
let
val (env,denv,spills) = do_datatypeinfo_list datatypeinfo_list
val (env',denv',spills') = trans_dec(dec, envir, false, denvir, fnname)
in
(Environ.augment_env (env,env'),
augment_denv (denv,denv'),
spills @ spills')
end
| Absyn.EXCEPTIONdec except_list =>
let
fun make_exbind_info(loc, old_name, Ident.EXCON sym) =
let
val sym_string = Symbol.symbol_name sym
in
case loc of
Location.UNKNOWN => old_name
| _ =>
let
val file_loc = OS.Path.file (Location.to_string loc)
in
concat [sym_string, "[", file_loc, "]"]
end
end
| make_exbind_info _ = Crash.impossible "Not an excon in an exbind"
fun do_exns([], env, denv, lambdas) =
(env, denv, Lists.reducel (fn (x, y) => y @ x) ([], lambdas))
| do_exns(ex :: exns, env, denv, lambdas) =
let
val lv = new_LVar()
val (v, ty, loc, exception_name) = case ex of
Absyn.NEWexbind((v, ref ty), _,loc,n) => (v, ty, loc, n)
| Absyn.OLDexbind((v, ref ty), longv,loc,n) => (v, ty, loc, n)
val exn_string = make_exbind_info(loc, exception_name, v)
val (this_lambda,longstrid) =
let
val _ =
debugger_env_ref :=
Debugger_Types.add_debug_info
(!debugger_env_ref,
exn_string,
Debugger_Types.FUNINFO {ty = ty,
is_leaf = false,
has_saved_arg=false,
annotations = [],
runtime_env = Debugger_Types.empty_runtime_env,
is_exn = true})
val (lambda_exp,longstrid) =
let
in
case ex of
Absyn.NEWexbind _ =>
let
val lvar = new_LVar()
in
(redundant_exceptions_ref := (lvar,exn_string)::(!redundant_exceptions_ref);
(LambdaTypes.STRUCT
([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([unit_exp],[]),
NONE),
LambdaTypes.SCON(Ident.STRING exn_string, NONE)],
LambdaTypes.CONSTRUCTOR),
EnvironTypes.VARSPEC lvar))
end
| Absyn.OLDexbind(_, longv,_,_) =>
let
val (exp, longstrid) = cg_longexid(longv, envir)
in
(LambdaTypes.STRUCT
([select_exn_unique exp,
LambdaTypes.SCON(Ident.STRING exn_string, NONE)],
LambdaTypes.CONSTRUCTOR),
longstrid)
end
end
in
([LambdaTypes.LETB(lv, NONE,lambda_exp)],
longstrid)
end
val this_env =
Environ.add_valid_env (Environ.empty_env,
(v, EnvironTypes.LAMB(lv,longstrid)))
val this_denv =
add_valid_denv (empty_denv,(v, EnvironTypes.NULLEXP))
in
do_exns(exns, Environ.augment_env(env, this_env),
augment_denv(denv, this_denv),
this_lambda :: lambdas)
end
in
do_exns(except_list, Environ.empty_env, empty_denv, [])
end
| Absyn.LOCALdec (dec1, dec2) =>
let
val (local_env, local_denv, local_lambda) = trans_dec(dec1, envir, false, denvir, fnname)
val (main_env, main_denv, main_lambda) =
trans_dec(dec2, Environ.augment_env(envir, local_env), false,
augment_denv(denvir, local_denv), fnname)
in
(main_env, main_denv, local_lambda @ main_lambda)
end
| Absyn.OPENdec (longStrId_list,_) =>
let
fun trans_open([], new_env, new_denv, new_lambdas) = (new_env, new_denv, new_lambdas)
| trans_open(longstrid :: tl, new_env, new_denv, new_lambdas) =
let
val (env as EnvironTypes.ENV(valid_env, strid_env),
lambda_exp, longstrid', _) =
cg_longstrid(longstrid, envir)
val new_denv =
if generate_moduler then
open_debugger_env (cg_longstrid'(longstrid, denvir), new_denv)
else new_denv
val valid_map' = NewMap.to_list valid_env
val valid_map = map (fn (v, c) => (v, c, new_LVar())) valid_map'
val strid_map' = NewMap.to_list strid_env
val strid_map = map
(fn (s, ec) => (s, ec, new_LVar())) strid_map'
val longstrid' =
case (longstrid',longstrid) of
(EnvironTypes.STRIDSPEC (Ident.LONGSTRID(p,Ident.STRID s)), Ident.LONGSTRID(p',s')) =>
EnvironTypes.STRIDSPEC (Ident.LONGSTRID(combine_paths (p,combine_paths (Ident.PATH(s,Ident.NOPATH),p')),s'))
| (EnvironTypes.NOSPEC,longstrid) => EnvironTypes.STRIDSPEC longstrid
| _ => Crash.impossible "longstrid':trans_open:lambda"
fun identity_if_builtin (x as EnvironTypes.PRIM _,_) = x
| identity_if_builtin (_,y) = EnvironTypes.LAMB(y,longstrid')
val new_v_env = Lists.reducel
(fn (env, (v, x, l)) =>
Environ.add_valid_env(env, (v, identity_if_builtin(x,l))))
(new_env, valid_map)
val new_s_env = Lists.reducel
(fn (env, (s, (e, c, generate_moduler), l)) =>
Environ.add_strid_env(env, (s, (e, EnvironTypes.LAMB(l,longstrid'), generate_moduler))))
(new_v_env, strid_map)
val new_v_lambdas =
map
(fn (_, EnvironTypes.FIELD field, l) =>
LambdaTypes.LETB (l, NONE,LambdaTypes.SELECT(make_struct_select field, lambda_exp))
| (_, EnvironTypes.PRIM prim, l) =>
LambdaTypes.LETB (l,NONE,LambdaTypes.BUILTIN prim)
| _ => Crash.impossible "Absyn.OPENdec(1)")
valid_map
val new_s_lambdas = map
(fn (_, (_, EnvironTypes.FIELD field, _), l) =>
LambdaTypes.LETB(l,NONE, LambdaTypes.SELECT(make_struct_select field, lambda_exp))
| (_, (_, EnvironTypes.PRIM prim, _), l) =>
LambdaTypes.LETB(l, NONE,LambdaTypes.BUILTIN prim)
| _ => Crash.impossible "Absyn.OPENdec(2)"
) strid_map
in
trans_open(tl, new_s_env, new_denv,
new_v_lambdas @ new_s_lambdas @ new_lambdas)
end
in
trans_open(longStrId_list, Environ.empty_env, empty_denv, [])
end
| Absyn.SEQUENCEdec dec_list =>
trans_sequence_dec(envir, Environ.empty_env, denvir, empty_denv,
[], dec_list, is_toplevel, fnname)
end
and trans_sequence_dec(_, new_env, _, new_denv, bindings, [], _, _) =
(new_env, new_denv, Lists.reducel (fn (x, y) => y @ x) ([], bindings))
| trans_sequence_dec(old_env, new_env, old_denv, new_denv,
bindings, dec :: dec_list, is_toplevel, fnname) =
let
val (env, denv, lambda) = trans_dec (dec, old_env, is_toplevel, old_denv, fnname)
in
trans_sequence_dec(Environ.augment_env(old_env, env),
Environ.augment_env(new_env, env),
augment_denv(old_denv, denv),
augment_denv(new_denv, denv),
lambda :: bindings, dec_list, is_toplevel, fnname)
end
fun wrapped_trans_dec (dec, env, is_toplevel, denv, fnname) =
(tyfun_lvars_ref := [];
dexps_ref := [];
redundant_exceptions_ref := [];
let
val (env, denv, dec_bindings) = trans_dec(dec, env, is_toplevel, denv, fnname)
val tfs_bindings =
map (fn (_,(lvar,lexp)) =>
LambdaTypes.LETB(lvar,NONE, lexp))
(!tyfun_lvars_ref)
val dexp_bindings =
map (fn (_,(lvar,lexp)) =>
LambdaTypes.LETB(lvar,NONE, lexp))
(!dexps_ref)
val redundant_exn_bindings =
map (fn (lvar,exn_string) =>
LambdaTypes.LETB(lvar,NONE,
LambdaTypes.STRUCT
([LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([unit_exp],[]),
NONE),
LambdaTypes.SCON(Ident.STRING exn_string, NONE)],
LambdaTypes.CONSTRUCTOR)))
(!redundant_exceptions_ref)
val bindings =
let
val exp = (!dynamic_redundancy_report_ref) unit_exp
val _ = dynamic_redundancy_report_ref := (fn exp => exp)
in
case exp of
LambdaTypes.STRUCT([],_) => dec_bindings
| dynamic_report =>
LambdaTypes.LETB(new_LVar(),NONE,
dynamic_report) :: dec_bindings
end
in
(env, denv,
tfs_bindings @
dexp_bindings @
redundant_exn_bindings @
bindings)
end)
fun trans_str_exp (strexp, top_env as EnvironTypes.TOP_ENV(env, fun_env),denv, fnname) =
case strexp of
Absyn.NEWstrexp strdec =>
let
val (env, denv, lv_le_list) = trans_str_dec(strdec, top_env, denv, false, fnname)
val (EnvironTypes.TOP_ENV(env, _), lambda_exp) =
complete_struct_from_topenv(make_top_env env, lv_le_list)
in
(env, lambda_exp, EnvironTypes.DENVEXP denv)
end
| Absyn.OLDstrexp (longstrid,_,interface) =>
let
val (env, lexp, _, moduler_generated) = cg_longstrid(longstrid, env)
val ((strexp, env), dstrexp) =
case (moduler_generated,generate_moduler) of
(true,false) =>
(strip_tyfuns (lexp,env),
empty_dstrexp)
| (false,true) =>
let
val interface = fetch_interface interface
in
(include_tyfuns (lexp,interface,env),
make_dstrexp interface)
end
| (true,true) =>
((lexp, env),
cg_longstrid'(longstrid, denv))
| (false,false) => ((lexp, env), empty_dstrexp)
in
(env, strexp, dstrexp)
end
| Absyn.APPstrexp(funid as Ident.FUNID sy, strexp, coerce, location, debugger_str) =>
let
val (lv, result_env as EnvironTypes.ENV(valid_env, strid_env), moduler_generated) =
Environ.lookup_funid(funid, fun_env)
val old_functor_refs = !functor_refs
val _ = functor_refs := []
val (env, arg, dstrexp) = trans_str_exp(strexp, top_env, denv, fnname)
val Basis.BasisTypes.PHI (_, (interface, Basis.BasisTypes.SIGMA(_,interface'))) =
Basis.lookup_funid (funid, basis)
handle NewMap.Undefined =>
Crash.impossible "Undefined functor id in trans_str_exp"
val (new_env, new_arg) =
complete_struct((env, arg), SOME interface, !coerce, generate_moduler)
val dlvar = new_LVar()
val new_arg =
if generate_moduler then
let_lambdas_in_exp
(map (fn (ref (EnvironTypes.LVARFOO lv),interface) =>
LambdaTypes.LETB(lv,NONE,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([str_to_lambda interface],[]),
NONE))
| _ => Crash.impossible "1:INTFOO:functor_refs:lambda")
(!functor_refs),
(functor_refs := old_functor_refs;
LambdaTypes.STRUCT([new_arg,
dstrexp_to_lambda(merge_dstrexps (fetch_debugger_str debugger_str,
SOME interface,dstrexp,
location)),
LambdaTypes.VAR dlvar],
LambdaTypes.TUPLE)))
else
new_arg
val functorexp =
LambdaTypes.APP(
case lv of
EnvironTypes.LAMB (lv,_) => LambdaTypes.VAR lv
| EnvironTypes.EXTERNAL =>
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.LOAD_FUNCT,
([LambdaTypes.SCON(Ident.STRING(Symbol.symbol_name sy), NONE)],[]),
NONE)
| EnvironTypes.PRIM _ => Crash.impossible "APPstrexp(1)"
| EnvironTypes.FIELD _ => Crash.impossible "APPstrexp(2)",
([new_arg],[]),
NONE)
val ((functorexp, env), dfunctorexp) =
case (moduler_generated,generate_moduler) of
(true,false) =>
(strip_tyfuns (functorexp,result_env),
empty_dstrexp)
| (false,true) =>
(functor_refs := (ref (EnvironTypes.LVARFOO dlvar),interface')::(!functor_refs);
(include_tyfuns (functorexp,interface',result_env),
make_dstrexp interface'))
| (true,true) =>
let
val dlvar = ref (EnvironTypes.LVARFOO dlvar)
in
(functor_refs := (dlvar,interface')::(!functor_refs);
((functorexp, env),
EnvironTypes.LAMBDASTREXP' ([],dlvar,interface')))
end
| (false,false) => ((functorexp, env), empty_dstrexp)
in
(result_env, functorexp, dfunctorexp)
end
| Absyn.LOCALstrexp(strdec, strexp) =>
let
val (local_env, local_denv, local_lambdas) =
trans_str_dec(strdec, top_env, denv, false, fnname)
val (new_env, new_lambda, new_dexp) =
trans_str_exp(strexp,
Environ.augment_top_env(top_env, make_top_env local_env),
augment_denv (denv, local_denv),
fnname)
in
(new_env,
let_lambdas_in_exp(local_lambdas, new_lambda),
new_dexp)
end
| Absyn.CONSTRAINTstrexp (strexp,sigexp,abs,coerce,location) =>
let
val inte_opt = SOME (interface_from_sigexp sigexp)
val (str_env, lambda_exp, dstrexp) =
trans_str_exp(strexp, top_env, denv, fnname)
val (str_env, lambda_exp) =
complete_struct((str_env, lambda_exp),
inte_opt,
!coerce, generate_moduler)
in
(str_env,lambda_exp,dstrexp)
end
and trans_str_dec(strdec, top_env as EnvironTypes.TOP_ENV(env, _),
denv, is_toplevel, fnname) =
case strdec of
Absyn.DECstrdec dec => wrapped_trans_dec (dec, env, is_toplevel, denv, fnname)
| Absyn.STRUCTUREstrdec struct_dec_list =>
(case struct_dec_list of
[] => (Environ.empty_env, empty_denv, [])
| _ =>
let
fun trans_structs(aug_env, aug_denv, bindings, []) = (aug_env, aug_denv, rev bindings)
| trans_structs(aug_env, aug_denv, bindings,
(strid, sigexp_opt, strexp, coerce, location, debugger_str, interface') :: rest) =
let
val inte_opt = case sigexp_opt of
NONE => NONE
| SOME (sigexp,_) =>
SOME (interface_from_sigexp sigexp)
val (str_env, lambda_exp, dstrexp) =
trans_str_exp(strexp, top_env, denv, fnname)
val (str_env, lambda_exp) =
complete_struct((str_env, lambda_exp),
inte_opt,
!coerce, generate_moduler)
val lambda_var = new_LVar()
in
trans_structs
(Environ.add_strid_env(aug_env,
(strid, (str_env,
EnvironTypes.LAMB (lambda_var,EnvironTypes.NOSPEC),generate_moduler))),
if generate_moduler then
add_strid_denv(aug_denv,
(strid, merge_dstrexps (fetch_debugger_str debugger_str,
SOME(fetch_interface interface'),dstrexp, location)))
else aug_denv,
LambdaTypes.LETB(lambda_var,NONE,lambda_exp)
:: bindings, rest)
end
in
trans_structs(Environ.empty_env, empty_denv, [], struct_dec_list)
end)
| Absyn.ABSTRACTIONstrdec struct_dec_list =>
(case struct_dec_list of
[] => (Environ.empty_env, empty_denv, [])
| _ =>
let
fun trans_structs(aug_env, aug_denv, bindings, []) = (aug_env, aug_denv, rev bindings)
| trans_structs(aug_env, aug_denv, bindings,
(strid, sigexp_opt, strexp, coerce, location, debugger_str, interface') :: rest) =
let
val inte_opt = case sigexp_opt of
NONE => NONE
| SOME (sigexp,_) =>
SOME (interface_from_sigexp sigexp)
val (str_env, lambda_exp, dstrexp) =
trans_str_exp(strexp, top_env, denv, fnname)
val (str_env, lambda_exp) =
complete_struct((str_env, lambda_exp),
inte_opt,
!coerce, generate_moduler)
val lambda_var = new_LVar()
in
trans_structs
(Environ.add_strid_env(aug_env,
(strid, (str_env,
EnvironTypes.LAMB(lambda_var,EnvironTypes.NOSPEC),generate_moduler))),
if generate_moduler then
add_strid_denv(aug_denv,
(strid, merge_dstrexps (fetch_debugger_str debugger_str,
SOME(fetch_interface interface'),dstrexp, location)))
else aug_denv,
LambdaTypes.LETB(lambda_var, NONE,lambda_exp)
:: bindings, rest)
end
in
trans_structs(Environ.empty_env, empty_denv, [], struct_dec_list)
end)
| Absyn.LOCALstrdec(strdec1, strdec2) =>
let
val (local_env, local_denv, local_lambda) =
trans_str_dec(strdec1, top_env, denv, false, fnname)
val (main_env, main_denv, main_lambda) =
trans_str_dec
(strdec2,
Environ.augment_top_env(top_env, make_top_env local_env),
augment_denv(denv, local_denv), false,
fnname)
in
(main_env, main_denv, local_lambda @ main_lambda)
end
| Absyn.SEQUENCEstrdec strdec_list =>
trans_sequence_strdec(top_env, Environ.empty_env,
denv, empty_denv, [], strdec_list, is_toplevel, fnname)
and trans_sequence_strdec(_, new_env, _, new_denv, bindings, [], _, _) =
(new_env, new_denv, Lists.reducel (fn (x, y) => y @ x) ([], bindings))
| trans_sequence_strdec
(old_env, new_env, old_denv, new_denv, bindings, decs :: dec_list, is_toplevel, fnname) =
let
val (env, denv, new_bindings) = trans_str_dec(decs, old_env, old_denv, is_toplevel, fnname)
in
trans_sequence_strdec(Environ.augment_top_env
(old_env, make_top_env env),
Environ.augment_env(new_env, env),
augment_denv(old_denv, denv),
augment_denv(new_denv, denv),
new_bindings :: bindings, dec_list,
is_toplevel, fnname)
end
fun trans_individual_funbind
(funbind as
(funid, strid, sigexp, strexp, sigexp_opt,
annotation_string, coerce, location, debugger_str, str),
top_env as EnvironTypes.TOP_ENV(env, fun_env),
top_denv) =
let
val interface = interface_from_sigexp sigexp
val env = Environ.make_str_env (interface,generate_moduler)
val lvar' = new_LVar()
val lvar = new_dLVar lvar'
val lvar'' = new_dLVar lvar'
val lvar''' = new_dLVar lvar'
val inte_opt = case sigexp_opt of
NONE => NONE
| SOME (sigexp,_) =>
SOME (interface_from_sigexp sigexp)
val old_functor_refs = !functor_refs
val _ = functor_refs := []
val (str_env, lambda_exp, dstrexp) =
trans_str_exp
(strexp,
Environ.augment_top_env
(top_env,
make_top_env(Environ.add_strid_env(Environ.empty_env,
(strid,
(env,
EnvironTypes.LAMB(lvar',EnvironTypes.NOSPEC),
generate_moduler))))),
augment_denv
(top_denv,
if generate_moduler then
let
val str = fetch_interface str
in
add_strid_denv(empty_denv,
(strid,
EnvironTypes.LAMBDASTREXP([],(lvar'',lvar'),str)))
end
else empty_denv),
NONE)
val (str_env, lambda_exp) =
complete_struct ((str_env, lambda_exp),
inte_opt,
!coerce, generate_moduler)
val lambda_var = new_LVar()
val lambda_var' = new_dLVar lambda_var
val dstrexp =
if generate_moduler then
merge_dstrexps (fetch_debugger_str debugger_str,inte_opt,dstrexp, location)
else empty_dstrexp
val lambda_exp =
if generate_moduler then
LambdaTypes.LET((lvar',NONE,
LambdaTypes.SELECT({index=0,size=3,selecttype=LambdaTypes.TUPLE},LambdaTypes.VAR lvar)),
LambdaTypes.LET((lvar'',NONE,
LambdaTypes.SELECT({index=1,size=3,selecttype=LambdaTypes.TUPLE},LambdaTypes.VAR lvar)),
LambdaTypes.LET((lvar''',NONE,
LambdaTypes.SELECT({index=2,size=3,selecttype=LambdaTypes.TUPLE},LambdaTypes.VAR lvar)),
let_lambdas_in_exp(map (fn (ref (EnvironTypes.LVARFOO lv),interface) =>
LambdaTypes.LETB(lv,NONE,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([str_to_lambda interface],[]),
NONE))
| _ => Crash.impossible "2:INTFOO:functor_refs:lambda")
(!functor_refs),
(functor_refs := old_functor_refs;
LambdaTypes.LET((lambda_var',NONE,lambda_exp),
LambdaTypes.LET((new_LVar(),NONE,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.BECOMES,
([LambdaTypes.STRUCT([LambdaTypes.VAR lvar''',
dstrexp_to_lambda dstrexp],
LambdaTypes.TUPLE)],
[]),
NONE)),
LambdaTypes.VAR lambda_var')))))))
else
lambda_exp
in
(EnvironTypes.TOP_ENV
(Environ.empty_env,
Environ.add_funid_env
(Environ.empty_fun_env,
(funid,
(EnvironTypes.LAMB(lambda_var,EnvironTypes.NOSPEC), str_env, generate_moduler)))),
empty_denv,
[LambdaTypes.LETB(lambda_var,NONE,
LambdaTypes.FN(([lvar],[]), lambda_exp, LambdaTypes.FUNC,annotation_string,
LambdaTypes.null_type_annotation,RuntimeEnv.USER_FUNCTION))])
end
fun trans_fun_dec([], _, _) = (make_top_env Environ.empty_env,
empty_denv, [])
| trans_fun_dec(funbind :: rest, top_env, top_denv) =
let
val (new_env, new_denv, new_lambda) =
trans_individual_funbind(funbind, top_env, top_denv)
val (rest_env, rest_denv, rest_lambda) = trans_fun_dec(rest, top_env, top_denv)
in
(Environ.augment_top_env(new_env, rest_env),
augment_denv(new_denv, rest_denv), new_lambda @ rest_lambda)
end
fun trans_fun_dec_list([], _, _) = (make_top_env Environ.empty_env,
empty_denv, [])
| trans_fun_dec_list((Absyn.FUNBIND funbind):: rest, top_env, top_denv) =
let
val (new_env, new_denv, new_lambda) = trans_fun_dec(funbind, top_env, top_denv)
val (rest_env, rest_denv, rest_lambda) =
trans_fun_dec_list(rest, Environ.augment_top_env(top_env, new_env),
augment_denv(top_denv, new_denv))
in
(Environ.augment_top_env(new_env, rest_env),
augment_denv(new_denv, rest_denv), new_lambda @ rest_lambda)
end
val old_functor_refs = !functor_refs
val _ = functor_refs := []
val (a,b,c) =
case topdec of
Absyn.STRDECtopdec (strdec,_) =>
let
val (new_env, new_denv, lambdas) =
trans_str_dec(strdec, top_env, top_denv, true, NONE)
in
(make_top_env new_env, new_denv, lambdas)
end
| Absyn.SIGNATUREtopdec _ =>
(make_top_env Environ.empty_env, empty_denv, [])
| Absyn.FUNCTORtopdec (funbind_list,_) =>
trans_fun_dec_list(funbind_list, top_env, top_denv)
| Absyn.REQUIREtopdec _ =>
Crash.impossible"trans_topdec REQUIREtopdec"
val a = if generate_moduler then sub_functor_refs a else a
val c = overload_binding @
map
(fn (ref(EnvironTypes.LVARFOO lv),interface) =>
LambdaTypes.LETB(lv,NONE,
LambdaTypes.APP(LambdaTypes.BUILTIN Pervasives.REF,
([str_to_lambda interface],[]),
NONE))
| _ => Crash.impossible "3:INTFOO:functor_refs:lambda") (!functor_refs) @ c
val a =
let
val (env,ct) =
Lists.reducel
(fn ((env,ct),(lv' as ref (EnvironTypes.LVARFOO lv),_)) =>
(lv' := EnvironTypes.INTFOO ct;
(Environ.add_valid_env(env,
(Ident.VAR(make_functor_app ct),
EnvironTypes.LAMB(lv,EnvironTypes.NOSPEC))),ct+1))
| _ => Crash.impossible "4:INTFOO:functor_refs:lambda")
((Environ.empty_env,!functor_refs_ct),!functor_refs)
in
(functor_refs_ct := ct;
Environ.augment_top_env(a, make_top_env (make_env env)))
end
val _ = functor_refs := (!functor_refs) @ old_functor_refs
val result_debug_info = ! debugger_env_ref
in
(a, b, c, result_debug_info)
end
end;
