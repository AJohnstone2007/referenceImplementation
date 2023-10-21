require "../utils/crash";
require "../utils/lists";
require "../main/info";
require "../main/primitives";
require "../basics/identprint";
require "../typechecker/types";
require "../typechecker/scheme";
require "../typechecker/valenv";
functor Valenv(
structure Crash : CRASH
structure Lists : LISTS
structure Info : INFO
structure Primitives : PRIMITIVES
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Scheme : TYPESCHEME
sharing Types.Options = IdentPrint.Options = Scheme.Options
sharing Types.Datatypes = Scheme.Datatypes
sharing Types.Datatypes.Ident = IdentPrint.Ident
sharing IdentPrint.Ident.Location = Info.Location
) : VALENV =
struct
structure Datatypes = Types.Datatypes
type Options = Types.Options.options
type ErrorInfo = Info.options
open Datatypes
exception LookupValId of Ident.ValId
fun ve_plus_ve (VE (_,amap),VE (_,amap')) =
VE (ref 0, NewMap.union(amap, amap'))
fun lookup (valid,VE (_,valenv)) =
case NewMap.tryApply'(valenv, valid) of
SOME t => t
| _ => raise LookupValId valid
fun ve_domain (VE (_,amap)) =
NewMap.fold_in_rev_order
(fn (res, Ident.CON sym, _) => Ident.VAR sym :: res
| (res, Ident.EXCON sym, _) => Ident.VAR sym :: res
| (res, v as (Ident.VAR _), _) => v :: res
| _ => Crash.impossible "TYCON':ve_domain:valenv")
([], amap)
fun add_to_ve (valid,scheme,VE (_,amap)) =
VE ((ref 0, NewMap.define (amap,valid,scheme)))
local
val bool_scheme = Scheme.make_scheme ([],(CONSTYPE ([],Types.bool_tyname),NONE))
fun atyvar (id,eq,imp) =
TYVAR (ref (0,NULLTYPE,NO_INSTANCE),
Ident.TYVAR (Ident.Symbol.find_symbol (id),eq,imp))
in
val initial_ee = empty_valenv
val basic_constructor_set =
Lists.reducel
(fn (ve,(name,scheme)) =>
(add_to_ve (Ident.CON (Ident.Symbol.find_symbol name),
scheme,
ve)))
(empty_valenv,
[("true",bool_scheme),
("false",bool_scheme),
("ref",
let val aty = atyvar ("'_a",false,true)
in
Scheme.make_scheme([aty],(FUNTYPE (aty,CONSTYPE
([aty],Types.ref_tyname)),
NONE))
end),
("nil",
let
val aty = atyvar ("'a",false,false)
in
Scheme.make_scheme([aty],(CONSTYPE ([aty],Types.list_tyname),
NONE))
end),
("::",
let val aty = atyvar ("'a",false,false)
in
Scheme.make_scheme ([aty],
(FUNTYPE
(Types.add_to_rectype
(Ident.LAB
(Ident.Symbol.find_symbol ("1")),
aty,Types.add_to_rectype
(Ident.LAB
(Ident.Symbol.find_symbol ("2")),
CONSTYPE
([aty],Types.list_tyname),
Types.empty_rectype)),
CONSTYPE
([aty],Types.list_tyname)),NONE))
end)])
val initial_ve =
add_to_ve
(Ident.VAR (Ident.Symbol.find_symbol ("=")),
let val aty = atyvar ("''a",true,false)
in
Scheme.make_scheme ([aty],
(FUNTYPE
(Types.add_to_rectype
(Ident.LAB
(Ident.Symbol.find_symbol ("1")),aty,
Types.add_to_rectype
(Ident.LAB
(Ident.Symbol.find_symbol ("2")),aty,
Types.empty_rectype)),
CONSTYPE ([],Types.bool_tyname)),
NONE))
end,
Lists.reducel
(fn (ve,(s, f, t)) =>
let val v = Ident.VAR (Ident.Symbol.find_symbol s)
in add_to_ve (v, f (v, t), ve)
end)
(basic_constructor_set,
[("~", Scheme.unary_overloaded_scheme, Ident.realint_tyvar),
("abs", Scheme.unary_overloaded_scheme, Ident.realint_tyvar),
("*", Scheme.binary_overloaded_scheme, Ident.num_tyvar),
("+", Scheme.binary_overloaded_scheme, Ident.num_tyvar),
("-", Scheme.binary_overloaded_scheme, Ident.num_tyvar),
("mod", Scheme.binary_overloaded_scheme, Ident.wordint_tyvar),
("div", Scheme.binary_overloaded_scheme, Ident.wordint_tyvar),
("/", Scheme.binary_overloaded_scheme, Ident.real_tyvar),
("<", Scheme.predicate_overloaded_scheme, Ident.numtext_tyvar),
(">", Scheme.predicate_overloaded_scheme, Ident.numtext_tyvar),
("<=", Scheme.predicate_overloaded_scheme, Ident.numtext_tyvar),
(">=", Scheme.predicate_overloaded_scheme, Ident.numtext_tyvar)]))
val initial_ve_for_builtin_library =
Lists.reducel
(fn (env,s) =>
let
val aty = atyvar ("'a", false, false)
in
add_to_ve (Ident.VAR (Ident.Symbol.find_symbol s),
Scheme.make_scheme ([aty],(aty,NONE)),
env)
end)
(basic_constructor_set,
(map #1 Primitives.values_for_builtin_library))
end
fun string_valenv (start, VE (_,amap)) =
let
fun print_spaces (res, n) =
if n = 0 then concat(" " :: res)
else print_spaces (" " :: res, n-1)
in
NewMap.string IdentPrint.debug_printValId Scheme.string_scheme
{start = "{", domSep = " |--> ", itemSep = "\n" ^ print_spaces([], start), finish = "}"}
(NewMap.map (fn (id,sch)=>sch) amap)
end
fun empty_valenvp (VE (_,amap)) = NewMap.is_empty amap
fun valenv_eq (ve as VE (_,amap),ve' as VE (_,amap')) =
NewMap.eq (fn (sch,sch')=> Scheme.typescheme_eq (sch,sch')) (amap, amap')
fun dom_valenv_eq (ve as VE(_, m), ve' as VE(_, m')) =
NewMap.eq (fn _ => true) (m, m')
fun resolve_overloads
error_info
(ENV (_, _, VE (_, amap)),
options as Types.Options.OPTIONS
{print_options,
compat_options = Types.Options.COMPATOPTIONS
{old_definition, ...},
...}) =
let
fun error_fn (valid, loc) =
Info.error' error_info
(Info.FATAL, loc,
"Unresolved overloading for "
^ IdentPrint.printValId print_options valid)
fun resolve_scheme (_, SCHEME (i, (ty, inst))) =
Types.resolve_overloading (not old_definition, ty, error_fn)
| resolve_scheme (_, UNBOUND_SCHEME (ty, inst)) =
Types.resolve_overloading (not old_definition, ty, error_fn)
| resolve_scheme (_, s as OVERLOADED_SCHEME _) =
()
in
NewMap.iterate resolve_scheme amap
end
fun ve_copy (VE (_,amap),tyname_copies) =
let
fun copy (_, s) = Scheme.scheme_copy (s,tyname_copies)
in
VE(ref 0, NewMap.map copy amap)
end
fun tyvars (VE (_,amap)) =
Lists.filter
(NewMap.fold (fn (acc, _, ran) => Scheme.tyvars(acc, ran)) ([], amap))
end
;
