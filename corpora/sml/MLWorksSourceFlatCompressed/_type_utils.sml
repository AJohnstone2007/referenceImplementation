require "../utils/crash";
require "../utils/lists";
require "../typechecker/types";
require "type_utils";
functor TypeUtils(
structure Crash : CRASH
structure Lists : LISTS
structure Types : TYPES
) : TYPE_UTILS =
struct
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
fun get_cons_type(Datatypes.FUNTYPE(_, ty2)) = ty2
| get_cons_type(ty as Datatypes.CONSTYPE _) = ty
| get_cons_type _ = Crash.impossible "get_cons_type on non-constructed type"
fun get_valenv (Datatypes.CONSTYPE
(_, Datatypes.TYNAME {5=ref (Datatypes.VE (_, valenv)),6=location,
...})) =
(case location of SOME x => x | NONE => "",valenv)
| get_valenv(Datatypes.CONSTYPE
(_, tyname as Datatypes.METATYNAME{1 = ref tyfun,
5=ref(Datatypes.VE(_, valenv)),
...})) =
if NewMap.is_empty valenv then
let
val (new_tyname, ok) = case tyfun of
Datatypes.TYFUN(Datatypes.CONSTYPE(_, tyname), _) =>
(tyname, true)
| Datatypes.ETA_TYFUN tyname => (tyname, true)
| _ => (tyname, false)
in
if ok then
get_valenv(Datatypes.CONSTYPE([], new_tyname))
else
("",valenv)
end
else
("",valenv)
| get_valenv ty =
Crash.impossible("bad ty '" ^ Types.debug_print_type
Types.Options.default_options ty
^ "' in get_valenv")
fun get_no_cons ty =
NewMap.size (#2(get_valenv (get_cons_type ty)))
fun type_from_scheme(Datatypes.SCHEME(_, (the_type,_))) = the_type
| type_from_scheme(Datatypes.UNBOUND_SCHEME (the_type,_)) = the_type
| type_from_scheme _ = Crash.impossible"OVERLOADED_SCHEME"
fun is_vcc(Datatypes.FUNTYPE _) = true
| is_vcc(Datatypes.CONSTYPE _) = false
| is_vcc Datatypes.NULLTYPE = false
| is_vcc _ = Crash.impossible"is_vcc on non-constructed type"
val vcc_fun = is_vcc o type_from_scheme
val null_fun = not o vcc_fun
val null_exists = NewMap.exists (null_fun o #2)
val vcc_exists = NewMap.exists (vcc_fun o #2)
val vcc_len = Lists.filter_length vcc_fun
val null_len = Lists.filter_length null_fun
val get_no_vcc_cons = vcc_len o NewMap.range o #2 o get_valenv o get_cons_type
val get_no_null_cons = null_len o NewMap.range o #2 o get_valenv o get_cons_type
val has_null_cons = null_exists o #2 o get_valenv o get_cons_type
val has_value_cons = vcc_exists o #2 o get_valenv o get_cons_type
fun is_integral ty =
case Types.the_type ty of
(ty as Datatypes.CONSTYPE(_,tyname)) =>
Types.tyname_eq(tyname, Types.int_tyname) orelse
Types.tyname_eq(tyname, Types.word_tyname) orelse
Types.tyname_eq(tyname, Types.char_tyname) orelse
(case NewMap.to_list(#2(get_valenv ty)) of
[] =>
false
| assoc =>
Lists.forall (not o is_vcc o type_from_scheme o #2) assoc)
| _ => false
fun is_integral2 primTy =
case Types.the_type primTy of
ty as Datatypes.RECTYPE _ =>
is_integral(Types.get_type_from_lab (Ident.LAB(Symbol.find_symbol "2"), ty))
| _ => false
fun is_integral3 primTy =
case Types.the_type primTy of
ty as Datatypes.RECTYPE _ =>
is_integral(Types.get_type_from_lab (Ident.LAB(Symbol.find_symbol "3"), ty))
| _ => false
end
;
