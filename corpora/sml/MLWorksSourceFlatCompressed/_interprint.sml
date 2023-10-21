require "^.basis.__text_io";
require "^.basis.__int";
require "../utils/diagnostic";
require "../lambda/topdecprint";
require "../basics/identprint";
require "../debugger/value_printer";
require "../lambda/environ";
require "../typechecker/basis";
require "../typechecker/environment";
require "../typechecker/valenv";
require "../typechecker/strenv";
require "../typechecker/tyenv";
require "../typechecker/types";
require "../typechecker/completion";
require "../typechecker/sigma";
require "../parser/parserenv";
require "../rts/gen/tags";
require "incremental";
require "interprint";
functor InterPrint (
structure Incremental : INCREMENTAL
structure TopdecPrint : TOPDECPRINT
structure IdentPrint : IDENTPRINT
structure ValuePrinter: VALUE_PRINTER
structure Environ : ENVIRON
structure Basis : BASIS
structure Env : ENVIRONMENT
structure Valenv : VALENV
structure Strenv : STRENV
structure Tyenv : TYENV
structure Types : TYPES
structure Completion : COMPLETION
structure Sigma : SIGMA
structure ParserEnv : PARSERENV
structure Diagnostic : DIAGNOSTIC
structure Tags : TAGS
sharing Types.Datatypes.Ident = IdentPrint.Ident = ParserEnv.Ident
sharing Incremental.InterMake.Compiler.Absyn = TopdecPrint.Absyn
sharing Basis.BasisTypes.Datatypes = Completion.Datatypes =
Types.Datatypes = Valenv.Datatypes = Tyenv.Datatypes = Strenv.Datatypes = Env.Datatypes
sharing TopdecPrint.Options = Types.Options = IdentPrint.Options =
Incremental.InterMake.Inter_EnvTypes.Options = Completion.Options =
ValuePrinter.Options = Sigma.Options
sharing Incremental.InterMake.Inter_EnvTypes.EnvironTypes = Environ.EnvironTypes
sharing Incremental.InterMake.Compiler.NewMap =
Basis.BasisTypes.Datatypes.NewMap
sharing TopdecPrint.Absyn.Ident = Basis.BasisTypes.Datatypes.Ident =
Environ.EnvironTypes.LambdaTypes.Ident
sharing Basis.BasisTypes = Sigma.BasisTypes
sharing type Incremental.InterMake.Compiler.DebugInformation =
ValuePrinter.DebugInformation
sharing type Environ.Structure = Basis.BasisTypes.Datatypes.Structure
sharing type TopdecPrint.Absyn.Type = Basis.BasisTypes.Datatypes.Type =
ValuePrinter.Type = Environ.EnvironTypes.LambdaTypes.Type
sharing type Incremental.InterMake.Compiler.TypeBasis = ValuePrinter.TypeBasis = Basis.BasisTypes.Basis
sharing type Incremental.InterMake.Compiler.ParserBasis = ParserEnv.pB
) : INTERPRINT =
struct
structure Incremental = Incremental
structure Datatypes = Types.Datatypes
structure Map = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Inter_EnvTypes = Incremental.InterMake.Inter_EnvTypes
structure Compiler = Incremental.InterMake.Compiler
structure BasisTypes = Basis.BasisTypes
structure Diagnostic = Diagnostic
structure ValuePrinter = ValuePrinter
structure Info = Compiler.Info
structure Options = ValuePrinter.Options
type Context = Incremental.Context
fun diagnostic_fn (level, output_function) =
Diagnostic.output_fn level
(fn (verbosity, stream) => (TextIO.output (stream, "Incremental: ");
output_function (verbosity, stream)))
val show_structures = false
exception Undefined of Ident.Identifier
val make_tyvars = Types.make_tyvars
fun strings
(context,
options as
Options.OPTIONS
{print_options,
compiler_options =
Options.COMPILEROPTIONS
{generate_moduler, ...},
compat_options =
Options.COMPATOPTIONS
{old_definition, ...},
...},
identifiers,
parser_basis) =
let
val cache_ref = ref (Completion.empty_cache ())
fun print (so_far, string) = so_far ^ string
val out = ""
val signatures = Incremental.signatures context
val type_basis = Incremental.type_basis context
val fixity = case parser_basis of
ParserEnv.B(_, _, ParserEnv.E(ParserEnv.FE fixity_map, _, _,_)) =>
fixity_map
val BasisTypes.BASIS (_,_, functor_env, _, environment as
Datatypes.ENV (structure_env,
type_env,
value_env)) = type_basis
val inter_env = Incremental.inter_env context
fun print_one_fixity(symbol, ParserEnv.LEFT i) =
"infix " ^ Int.toString i ^ " " ^
ParserEnv.Ident.Symbol.symbol_name symbol
| print_one_fixity(symbol, ParserEnv.RIGHT i) =
"infixr " ^ Int.toString i ^ " " ^
ParserEnv.Ident.Symbol.symbol_name symbol
| print_one_fixity(symbol, ParserEnv.NONFIX) = ""
fun print_fixity (out, fix) =
let
val string = print_one_fixity fix
in
if string = "" then out
else
print(out, string ^ "\n")
end
fun print_typescheme_closed(out, scheme) =
case scheme of
Datatypes.SCHEME (arity, (ty,_)) =>
let
val (str,newcache) =
Completion.cached_print_type(options,
environment,ty,!cache_ref)
in
cache_ref := newcache;
print (out,str)
end
| Datatypes.UNBOUND_SCHEME (ty,_) =>
let
val (str,newcache) = Completion.cached_print_type
(options,environment,ty,!cache_ref)
in
cache_ref := newcache;
print (out,str)
end
| Datatypes.OVERLOADED_SCHEME _ =>
print (out, "<strange overloaded scheme>")
fun print_indent (out, 0) = out
| print_indent (out, indent) =
let
fun reduce (out, 0) = out
| reduce (out, indent) =
if indent >= 8 then
reduce (print (out, "        "), indent-8)
else if indent >= 4 then
reduce (print (out, "    "), indent-4)
else if indent >= 2 then
reduce (print (out, "  "), indent-2)
else
print (out, " ")
in
reduce (out, indent)
end
fun tyname_name(Datatypes.TYNAME{2=name, ...}) = name
| tyname_name(Datatypes.METATYNAME{2=name, ...}) = name
fun print_value (out, indent,
valid as Ident.VAR _,
typescheme, value_opt) =
let
val out = print_indent (out, indent)
val out = print (out, "val ")
val out = print (out, IdentPrint.printValId print_options valid)
val out = print (out, " : ")
val out = print_typescheme_closed (out, typescheme)
in
case value_opt of
NONE => print (out, " = _\n")
| SOME value =>
let
val out = print (out, " = ")
val out =
let
val string =
case typescheme of
Datatypes.SCHEME (arity, (ty,_)) =>
ValuePrinter.stringify_value false
(print_options, value,
Types.apply (Datatypes.TYFUN (ty, arity), make_tyvars arity),
Incremental.debug_info context)
| Datatypes.UNBOUND_SCHEME (ty,_) =>
ValuePrinter.stringify_value false
(print_options, value, ty,
Incremental.debug_info context)
| Datatypes.OVERLOADED_SCHEME _ =>
"<strange overloaded value>"
in
print (out, string)
end
val out = print (out, "\n")
in
out
end
end
| print_value (out, indent, valid as Ident.EXCON _, typescheme, _) =
let
val out = print_indent (out, indent)
val out = print (out, "exception ")
val out = print (out, IdentPrint.printValId print_options valid)
val out =
case typescheme of
Datatypes.UNBOUND_SCHEME (ty as Datatypes.FUNTYPE (arg, exn),_) =>
if Types.type_eq (exn, Types.exn_type, true, true) then
print (print (out, " of "),
Types.print_type options arg)
else
print (print (out, " <strange function type> "),
Types.print_type options ty)
| Datatypes.UNBOUND_SCHEME (ty,_) =>
if Types.type_eq (ty, Types.exn_type, true, true) then
out
else
print (print (out, " <strange type> "),
Types.print_type options ty)
| scheme =>
print_typescheme_closed (print (out, " <strange scheme> "),
scheme)
val out = print (out, "\n")
in
out
end
| print_value (out, indent, valid as Ident.CON _, typescheme, _) =
let
val out = print_indent (out, indent)
val out = print (out, "val ")
val out = print (out, IdentPrint.printValId print_options valid)
val out = print (out, " : ")
val out = print_typescheme_closed (out, typescheme)
in
print (out, "\n")
end
| print_value {1=out, ...} = out
fun print_type (out, indent, tycon,
Datatypes.TYSTR(tyfun, value_env as
Datatypes.VE (_, values))) =
case tyfun of
Datatypes.NULL_TYFUN _ =>
let
val out = print_indent (out, indent)
val out = print (out, "<strange null tyfun> ")
val out = print (out, IdentPrint.printTyCon tycon)
val out = print (out, "\n")
in
out
end
| Datatypes.ETA_TYFUN tyname =>
(case tyname of
Datatypes.METATYNAME{1=ref tyfun', ...} =>
print_type(out, indent, tycon,
Datatypes.TYSTR(tyfun', value_env))
| _ =>
if Valenv.empty_valenvp value_env then
let
val tyvars = make_tyvars (Types.tyname_arity tyname)
val out = print_indent (out, indent)
val out = print (out,
if Types.eq_attrib tyname then
"eqtype "
else
"type ")
val out = print (out, Types.print_tyvars options tyvars)
val out = case tyvars of [] => out | _ => print (out," ")
val out = print (out, IdentPrint.printTyCon tycon)
val out = print (out, " = ")
val out = print (out, Types.print_type
options
(Types.apply (tyfun, tyvars)))
val out = print (out, "\n")
in
out
end
else
let
val tyvars = make_tyvars (Types.tyname_arity tyname)
val out = print_indent (out, indent)
val out = print (out, "datatype ")
val out = print (out, Types.print_tyvars options tyvars)
val out = case tyvars of [] => out | _ =>
print (out," ")
val out = print (out, IdentPrint.printTyCon tycon)
val (out, _) =
Datatypes.NewMap.fold_in_order
(fn ((out, first), valid, typescheme) =>
let
val out = print (out, if first then " =\n" else " |\n")
val out = print_indent (out, indent+2)
val out = print (out, IdentPrint.printValId print_options valid)
in
(case typescheme of
Datatypes.UNBOUND_SCHEME (Datatypes.FUNTYPE (arg, _),_) =>
print (print (out, " of "),
Types.print_type options arg)
| Datatypes.UNBOUND_SCHEME _ => out
| Datatypes.SCHEME (arity, (ty,_)) =>
(case Types.apply (Datatypes.TYFUN (ty, arity), tyvars) of
Datatypes.FUNTYPE (arg, _) =>
print (print (out, " of "),
Types.print_type options arg)
| _ => out)
| Datatypes.OVERLOADED_SCHEME _ =>
print (out, " <strange overloaded scheme>"),
false)
end)
((out, true), values)
val out = print (out, "\n")
in
out
end)
| tyfun as Datatypes.TYFUN (ty, arity) =>
let
val tyvars = make_tyvars arity
val out = print_indent (out, indent)
val out = print (out, if Types.equalityp tyfun then "eqtype " else "type ")
val out = print (out, Types.print_tyvars options tyvars)
val out = if arity = 0 then out else print (out," ")
val out = print (out, IdentPrint.printTyCon tycon)
val out = print (out, " = ")
val out = print (out, Types.print_type options
(Types.apply (tyfun, tyvars)))
val out = print (out, "\n")
in
out
end
exception ExpandStr
fun print_structure (out, indent, depth, strid,
str,
value_opt) =
let
val out = print_indent (out, indent)
val out = print (out, "structure ")
val out = print (out, IdentPrint.printStrId strid)
val out = if show_structures then print (print (out, ": "), Env.string_str str) else out
val lambda_env = Environ.make_str_env (str,generate_moduler)
val (_,_,Datatypes.ENV (Datatypes.SE structures,
Datatypes.TE types,
Datatypes.VE (_, values))) =
case Env.expand_str str of
Datatypes.STR data => data
| _ => raise ExpandStr
fun sub_structure (value, index) =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.PAIRPTR then
SOME (MLWorks.Internal.Value.sub (value, index))
else if primary = Tags.POINTER then
SOME (MLWorks.Internal.Value.sub (value, index+1))
else
NONE
end
in
if depth = 0
then print (out, " = struct ... end\n")
else
let
val out = print (out, " =\n")
val out = print_indent (out, indent+2)
val out = print (out, "struct\n")
val out =
Datatypes.NewMap.fold_in_order
(fn (out, strid, str) =>
let
val substructure =
case value_opt of
NONE => NONE
| SOME value =>
(case Environ.lookup_strid (strid, lambda_env) of
(_, Environ.EnvironTypes.FIELD {index, ...}, _) =>
sub_structure (value, index)
| _ => NONE)
in
print_structure (out, indent+4, depth-1, strid, str, substructure)
end)
(out, structures)
val out =
Datatypes.NewMap.fold_in_order
(fn (out, tycon, tystr) => print_type (out, indent+4, tycon, tystr))
(out, types)
val out =
Datatypes.NewMap.fold_in_order
(fn (out, valid, typescheme) =>
let
val subvalue =
case value_opt of
NONE => NONE
| SOME value =>
(case Environ.lookup_valid
(valid, lambda_env) of
Environ.EnvironTypes.FIELD {index, ...} =>
sub_structure (value, index)
| _ => NONE)
in
print_value (out, indent+4, valid, typescheme, subvalue)
end)
(out,values)
val out = print_indent (out, indent+2)
val out = print (out, "end\n")
in
out
end
end
fun print_signature (out, indent, sigid, sigexp) =
let
val out = print_indent (out, indent)
val out = print (out, "signature ")
val out = print (out, IdentPrint.printSigId sigid)
val out =
if show_structures then
let
val sigma = Basis.lookup_sigid (sigid,type_basis)
in
print (print (out,": "), Sigma.string_sigma Options.default_print_options sigma)
end
else out
val Options.PRINTOPTIONS{maximum_sig_depth,...} = print_options
in
if (maximum_sig_depth = 0)
then print(out, " = sig ... end\n")
else
let val out = print(out, " =\n");
val out = TopdecPrint.print_sigexp options
print (out, indent+2, sigexp)
val out = print (out, "\n")
in
out
end
end
fun print_functor (out, indent, funid) =
let
val out = print_indent (out, indent)
val out = print (out, "functor ")
val out = print (out, IdentPrint.printFunId funid)
val out = print (out, "\n")
val out =
if show_structures then
let
val phi = Basis.lookup_funid (funid,type_basis)
in
print (out, Sigma.string_phi Options.default_print_options phi)
end
else out
in
out
end
fun print_identifier (out, ident as Ident.SIGNATURE sigid) =
(print_signature (out, 0, sigid, Map.apply' (signatures, sigid))
handle Map.Undefined => print (out,"signature " ^ IdentPrint.printSigId sigid ^ "\n"))
| print_identifier (out, ident as Ident.VALUE valid) =
(print_value (out, 0, valid,
Valenv.lookup (valid, value_env),
(SOME
(Inter_EnvTypes.lookup_val
(valid, inter_env)))
handle Map.Undefined => NONE)
handle Valenv.LookupValId _ => raise Undefined ident)
| print_identifier (out, ident as Ident.TYPE tycon) =
(print_type (out, 0, tycon, Tyenv.lookup (type_env, tycon))
handle Tyenv.LookupTyCon _ => raise Undefined ident)
| print_identifier (out, ident as Ident.STRUCTURE strid) =
let
val Options.PRINTOPTIONS{maximum_str_depth,...} = print_options
in
case Strenv.lookup (strid, structure_env) of
SOME str =>
(print_structure (out, 0, maximum_str_depth, strid,
str,
SOME(Inter_EnvTypes.lookup_str (strid, inter_env))))
| _ => raise Undefined ident
end
| print_identifier (out, ident as Ident.FUNCTOR funid) =
print_functor(out, 0, funid)
fun group_by_class ([], sigs, funs, strs, types, values) =
(rev sigs, rev funs, rev strs, rev types, rev values)
| group_by_class ((ident as Ident.SIGNATURE _) :: t, sigs, funs, strs, types, values) =
group_by_class (t, ident :: sigs, funs, strs, types, values)
| group_by_class ((ident as Ident.FUNCTOR _) :: t, sigs, funs, strs, types, values) =
group_by_class (t, sigs, ident :: funs, strs, types, values)
| group_by_class ((ident as Ident.STRUCTURE _) :: t, sigs, funs, strs, types, values) =
group_by_class (t, sigs, funs, ident :: strs, types, values)
| group_by_class ((ident as Ident.TYPE _) :: t, sigs, funs, strs, types, values) =
group_by_class (t, sigs, funs, strs, ident :: types, values)
| group_by_class ((ident as Ident.VALUE _) :: t, sigs, funs, strs, types, values) =
group_by_class (t, sigs, funs, strs, types, ident :: values)
val (sigs, funs, strs, types, values) =
group_by_class (identifiers, [], [], [], [], [])
in
diagnostic_fn
(1,
fn (_, stream) =>
app
(fn Ident.SIGNATURE sigid =>
TextIO.output(stream,
concat ["signature ", IdentPrint.printSigId sigid, "\n"]
)
| Ident.STRUCTURE strid =>
TextIO.output(stream,
concat ["structure ", IdentPrint.printStrId strid, "\n"]
)
| Ident.VALUE (valid as Ident.EXCON _) =>
TextIO.output(stream,
concat ["exception ", IdentPrint.printValId print_options valid, "\n"]
)
| Ident.VALUE valid =>
TextIO.output(stream,
concat ["value ", IdentPrint.printValId print_options valid, "\n"]
)
| Ident.TYPE tycon =>
TextIO.output(stream,
concat ["type ", IdentPrint.printTyCon tycon, "\n"]
)
| Ident.FUNCTOR funid =>
TextIO.output(stream,
concat ["functor ", IdentPrint.printFunId funid, "\n"]
)
)
identifiers
);
map
(fn entry as (sym, fix) =>
(Ident.VALUE (Ident.VAR sym), print_fixity (out, entry)))
(ParserEnv.Map.to_list fixity)
@ map (fn id => (id, print_identifier (out, id))) sigs
@ map (fn id => (id, print_identifier (out, id))) funs
@ map (fn id => (id, print_identifier (out, id))) strs
@ map (fn id => (id, print_identifier (out, id))) types
@ map (fn id => (id, print_identifier (out, id))) values
end
end
;
