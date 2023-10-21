require "../utils/lists";
require "../utils/print";
require "../utils/crash";
require "../basics/identprint";
require "../typechecker/tyenv";
require "../typechecker/types";
require "../typechecker/valenv";
require "../typechecker/scheme";
functor Tyenv(
structure Lists : LISTS
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Valenv : VALENV
structure Scheme : TYPESCHEME
structure Print : PRINT
structure Crash : CRASH
sharing Types.Datatypes = Valenv.Datatypes = Scheme.Datatypes
sharing IdentPrint.Ident = Types.Datatypes.Ident
) : TYENV =
struct
structure Datatypes = Types.Datatypes
open Datatypes
exception LookupTyCon of Ident.TyCon
val empty_tyenv = TE (NewMap.empty (Ident.tycon_lt, Ident.tycon_eq))
fun lookup (TE amap, tycon) =
case NewMap.tryApply' (amap, tycon) of
SOME tystr => tystr
| _ => raise LookupTyCon tycon
fun te_plus_te (TE amap,TE amap') =
TE (NewMap.union(amap, amap'))
fun add_to_te (TE amap, tycon, tystr) =
TE (NewMap.define (amap,tycon,tystr))
fun string_tyenv (TE amap) =
let
val tycon_length = ref 0
val tycon_tystr_list = NewMap.to_list_ordered amap
fun print_tycon tycon =
let
val string_tycon = IdentPrint.printTyCon tycon
val tycon_size = size string_tycon
in
(tycon_length := tycon_size;
string_tycon)
end
fun string_tystr (start,TYSTR (tyfun,conenv)) =
let
val tyfun_string = Types.string_tyfun tyfun
val conenv_string = Valenv.string_valenv (start +
(size tyfun_string) + 8,
conenv)
in
"(" ^ tyfun_string ^ "," ^ conenv_string ^ ")\n"
end
fun str_tystr tystr = string_tystr (!tycon_length,tystr)
fun print_pair ((object,image),print_object,print_image,connector) =
print_object object ^ connector ^ print_image image
in
Lists.to_string
(fn (x,y) => print_pair ((x,y),IdentPrint.printTyCon,
fn tystr => (string_tystr (!tycon_length,tystr)),
" |==> "))
tycon_tystr_list
end
fun empty_tyenvp (TE amap) = NewMap.is_empty amap
fun tystr_copy (TYSTR (tyfun,conenv), tyname_copies) =
TYSTR(Types.tyfun_copy (tyfun,tyname_copies), Valenv.ve_copy(conenv,tyname_copies))
fun te_copy (TE amap,tyname_copies) =
let
fun copy (_, tystr) = tystr_copy (tystr, tyname_copies)
in
TE(NewMap.map copy amap)
end
local
fun atyvar (id,eq,imp) =
TYVAR (ref (0,NULLTYPE,NO_INSTANCE),
Ident.TYVAR (Ident.Symbol.find_symbol (id),eq,imp))
fun do_one (te,(id,tystr)) = add_to_te (te,id,tystr)
in
val basic_te =
Lists.reducel
do_one
(empty_tyenv,
[(Ident.TYCON (Ident.Symbol.find_symbol ("int")),
TYSTR (Types.make_eta_tyfun (Types.int_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("word")),
TYSTR (Types.make_eta_tyfun (Types.word_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("real")),
TYSTR (Types.make_eta_tyfun (Types.real_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("string")),
TYSTR (Types.make_eta_tyfun (Types.string_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("char")),
TYSTR (Types.make_eta_tyfun (Types.char_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("ref")),
let
val aty = atyvar ("'_a",false,true)
in
TYSTR (Types.make_eta_tyfun (Types.ref_tyname),
let
val valenv =
Valenv.add_to_ve
(Ident.CON (Ident.Symbol.find_symbol ("ref")),
Scheme.make_scheme
([aty],
(FUNTYPE (aty,CONSTYPE([aty],
Types.ref_tyname)),NONE)),
empty_valenv)
val valenvref = case Types.ref_tyname of
TYNAME {5=valenvref,...} => valenvref
| _ => Crash.impossible"Types.ref_tyname bad"
in
(valenvref := valenv ; valenv)
end)
end),
(Ident.TYCON (Ident.Symbol.find_symbol ("exn")),
TYSTR (Types.make_eta_tyfun (Types.exn_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("list")),
let
val aty = atyvar ("'a",false,false)
in
TYSTR (Types.make_eta_tyfun (Types.list_tyname),
let
val valenv =
Valenv.add_to_ve
(Ident.CON (Ident.Symbol.find_symbol ("nil")),
Scheme.make_scheme
([aty],(CONSTYPE ([aty],Types.list_tyname),
NONE)),
Valenv.add_to_ve
(Ident.CON (Ident.Symbol.find_symbol ("::")),
Scheme.make_scheme
([aty],(FUNTYPE
(Types.add_to_rectype
(Ident.LAB
(Ident.Symbol.find_symbol ("1")),aty,
Types.add_to_rectype
(Ident.LAB (Ident.Symbol.find_symbol ("2")),
CONSTYPE ([aty],Types.list_tyname),
Types.empty_rectype)),
CONSTYPE ([aty],Types.list_tyname)),NONE)),
empty_valenv))
val valenvref = case Types.list_tyname of
TYNAME {5=valenvref,...} => valenvref
| _ => Crash.impossible"Types.list_tyname bad"
in
(valenvref := valenv ; valenv)
end)
end),
(Ident.TYCON (Ident.Symbol.find_symbol ("bool")),
TYSTR (Types.make_eta_tyfun (Types.bool_tyname),
let
val valenv =
Valenv.add_to_ve
(Ident.CON (Ident.Symbol.find_symbol ("true")),
Scheme.make_scheme
([],(CONSTYPE ([],Types.bool_tyname),NONE)),
Valenv.add_to_ve
(Ident.CON (Ident.Symbol.find_symbol ("false")),
Scheme.make_scheme
([],(CONSTYPE ([],Types.bool_tyname),NONE)),
empty_valenv))
val valenvref = case Types.bool_tyname of
TYNAME {5=valenvref,...} => valenvref
| _ => Crash.impossible"Types.bool_tyname bad"
in
(valenvref := valenv ; valenv)
end)),
(Ident.TYCON (Ident.Symbol.find_symbol ("unit")),
TYSTR (Types.make_tyfun ([],Types.empty_rectype),
empty_valenv))])
val initial_te_for_builtin_library =
Lists.reducel
do_one
(basic_te,
[(Ident.TYCON (Ident.Symbol.find_symbol "type_rep"),
TYSTR (Types.make_eta_tyfun (Types.typerep_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol "vector"),
TYSTR (Types.make_eta_tyfun (Types.vector_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol "bytearray"),
TYSTR (Types.make_eta_tyfun (Types.bytearray_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol "floatarray"),
TYSTR (Types.make_eta_tyfun (Types.floatarray_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol "array"),
TYSTR (Types.make_eta_tyfun (Types.array_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("dynamic")),
TYSTR (Types.make_eta_tyfun (Types.dynamic_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("int8")),
TYSTR (Types.make_eta_tyfun (Types.int8_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("word8")),
TYSTR (Types.make_eta_tyfun (Types.word8_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("int16")),
TYSTR (Types.make_eta_tyfun (Types.int16_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("word16")),
TYSTR (Types.make_eta_tyfun (Types.word16_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("int32")),
TYSTR (Types.make_eta_tyfun (Types.int32_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("word32")),
TYSTR (Types.make_eta_tyfun (Types.word32_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("int64")),
TYSTR (Types.make_eta_tyfun (Types.int64_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("word64")),
TYSTR (Types.make_eta_tyfun (Types.word64_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("float32")),
TYSTR (Types.make_eta_tyfun (Types.float32_tyname),
empty_valenv)),
(Ident.TYCON (Ident.Symbol.find_symbol ("ml_value")),
TYSTR (Types.make_eta_tyfun (Types.ml_value_tyname),
empty_valenv))])
val initial_te = basic_te
end
end
;
