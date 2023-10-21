require "../basis/__text_io";
require "../utils/diagnostic";
require "../parser/parser";
require "../lambda/lambda";
require "../lambda/lambdaoptimiser";
require "../lambda/lambdamodule";
require "../lambda/lambdaprint";
require "../lambda/topdecprint";
require "../mir/mir_cg";
require "../mir/miroptimiser";
require "../mir/mirprint";
require "mach_cg";
require "machprint";
require "../utils/lists";
require "../utils/crash";
require "../lambda/environ";
require "../lambda/environprint";
require "../typechecker/mod_rules";
require "../typechecker/basis";
require "../typechecker/stamp";
require "../main/primitives";
require "../main/pervasives";
require "../main/mlworks_io";
require "compiler";
functor Compiler (include sig
structure Parser : PARSER
structure Lambda : LAMBDA
structure LambdaOptimiser : LAMBDAOPTIMISER
structure LambdaModule : LAMBDAMODULE
structure LambdaPrint : LAMBDAPRINT
structure Environ : ENVIRON
structure EnvironPrint : ENVIRONPRINT
structure Mir_Cg : MIR_CG
structure MirOptimiser : MIROPTIMISER
structure MirPrint : MIRPRINT
structure Mach_Cg : MACH_CG
structure MachPrint : MACHPRINT
structure Mod_Rules : MODULE_RULES
structure Basis : BASIS
structure Stamp : STAMP
structure Primitives : PRIMITIVES
structure Pervasives : PERVASIVES
structure Io : MLWORKS_IO
structure Lists : LISTS
structure Crash : CRASH
structure TopdecPrint : TOPDECPRINT
structure Diagnostic : DIAGNOSTIC
sharing Lambda.Options =
LambdaPrint.Options =
TopdecPrint.Options =
LambdaOptimiser.Options =
EnvironPrint.Options =
Mod_Rules.Options =
Mir_Cg.Options =
Mach_Cg.Options
sharing Parser.Lexer.Info =
Lambda.Info =
Mod_Rules.Info =
Mach_Cg.Info =
Mir_Cg.Info
sharing LambdaOptimiser.LambdaTypes =
LambdaPrint.LambdaTypes =
Mir_Cg.LambdaTypes =
Environ.EnvironTypes.LambdaTypes
sharing Environ.EnvironTypes =
LambdaModule.EnvironTypes =
Lambda.EnvironTypes =
Primitives.EnvironTypes =
EnvironPrint.EnvironTypes
sharing MirOptimiser.MirTypes =
Mir_Cg.MirTypes =
Mach_Cg.MirTypes =
MirPrint.MirTypes
sharing MirOptimiser.MachSpec = Mach_Cg.MachSpec
sharing Mod_Rules.Assemblies.Basistypes = Basis.BasisTypes
sharing Parser.Absyn = Mod_Rules.Absyn =
Lambda.Absyn = TopdecPrint.Absyn
sharing Basis.BasisTypes.Datatypes.NewMap = Environ.EnvironTypes.NewMap
sharing Basis.BasisTypes.Datatypes.Ident = Parser.Absyn.Ident =
Environ.EnvironTypes.LambdaTypes.Ident
sharing type Lambda.DebugInformation = Mir_Cg.MirTypes.Debugger_Types.information
sharing type Parser.Lexer.Options = Lambda.Options.options
sharing type Basis.BasisTypes.Basis = Lambda.BasisTypes.Basis
sharing type Mach_Cg.Opcode = MachPrint.Opcode
sharing type Mir_Cg.MirTypes.Debugger_Types.Type =
Parser.Absyn.Type = Basis.BasisTypes.Datatypes.Type
sharing type Pervasives.pervasive = Environ.EnvironTypes.LambdaTypes.Primitive
sharing type Basis.BasisTypes.Datatypes.Stamp = Stamp.Stamp
sharing type Basis.BasisTypes.Datatypes.StampMap = Stamp.Map.T
end where type Environ.EnvironTypes.LambdaTypes.LVar = int
) : COMPILER =
struct
structure Diagnostic = Diagnostic
structure Parser = Parser
structure Lexer = Parser.Lexer
structure Token = Lexer.Token
structure Assemblies = Mod_Rules.Assemblies
structure BasisTypes = Basis.BasisTypes
structure EnvironTypes = Environ.EnvironTypes
structure LambdaTypes = EnvironTypes.LambdaTypes
structure Pervasives = Pervasives
structure MirTypes = Mir_Cg.MirTypes
structure Debugger_Types = MirTypes.Debugger_Types
structure Map = BasisTypes.Datatypes.NewMap
structure Info = Lexer.Info
structure Options = LambdaPrint.Options
structure NewMap = Map
structure Location = Info.Location
structure Absyn = Parser.Absyn
structure Datatypes = BasisTypes.Datatypes
structure Ident = Datatypes.Ident
type Top_Env = EnvironTypes.Top_Env
type DebuggerEnv = EnvironTypes.DebuggerEnv
type LambdaExp = LambdaTypes.LambdaExp
type Module = Mach_Cg.Module
type ParserBasis = Parser.ParserBasis
type TypeBasis = BasisTypes.Basis
type DebugInformation = Debugger_Types.information
type tokenstream = Lexer.TokenStream
val (str_ass, ty_ass) =
Assemblies.new_assemblies_from_basis Basis.initial_basis_for_builtin_library
val str_ass = ref str_ass
val ty_ass = ref ty_ass
fun diagnostic (level, output_function) =
Diagnostic.output level
(fn verbosity => "Compiler: " :: (output_function verbosity))
fun diagnostic_fn (level, output_function) =
Diagnostic.output_fn level
(fn (verbosity, stream) => (TextIO.output (stream, "Compiler: "); output_function (verbosity, stream)))
datatype basis =
BASIS of {parser_basis : Parser.ParserBasis,
type_basis : BasisTypes.Basis,
lambda_environment : EnvironTypes.Top_Env,
debugger_environment : DebuggerEnv,
debug_info : Debugger_Types.information}
val empty_basis =
BASIS
{parser_basis = Parser.empty_pB,
type_basis = Basis.empty_basis,
lambda_environment = Environ.empty_top_env,
debugger_environment =
EnvironTypes.DENV
(Map.empty (Ident.valid_lt,Ident.valid_eq),
Map.empty (Ident.strid_lt,Ident.strid_eq)),
debug_info = Debugger_Types.empty_information}
fun augment (Options.OPTIONS{compiler_options = Options.COMPILEROPTIONS{generate_debug_info,...},...},
BASIS {parser_basis, type_basis, lambda_environment, debugger_environment, debug_info},
BASIS {parser_basis = delta_parser_basis,
type_basis = delta_type_basis,
lambda_environment = delta_lambda_environment,
debugger_environment = delta_debugger_environment,
debug_info = delta_debug_info}) =
BASIS {parser_basis = Parser.augment_pB (parser_basis, delta_parser_basis),
type_basis = Basis.basis_circle_plus_basis (type_basis, delta_type_basis),
lambda_environment = Environ.augment_top_env (lambda_environment, delta_lambda_environment),
debugger_environment = Environ.augment_denv (debugger_environment, delta_debugger_environment),
debug_info = Debugger_Types.augment_information (generate_debug_info, debug_info, delta_debug_info)}
fun add_debug_info (Options.OPTIONS{compiler_options = Options.COMPILEROPTIONS{generate_debug_info,...},...},
new_debug_info,
BASIS {parser_basis, type_basis, lambda_environment, debugger_environment, debug_info}) =
BASIS {parser_basis = parser_basis,
type_basis = type_basis,
lambda_environment = lambda_environment,
debugger_environment = debugger_environment,
debug_info = Debugger_Types.augment_information (generate_debug_info, debug_info, new_debug_info)}
fun remove_str(BASIS {parser_basis, type_basis, lambda_environment, debugger_environment, debug_info},
strid) =
BASIS{parser_basis = Parser.remove_str(parser_basis, strid),
type_basis = Basis.remove_str(type_basis, strid),
lambda_environment = lambda_environment,
debugger_environment = debugger_environment,
debug_info = debug_info}
fun adjust_compiler_basis_debug_info(BASIS{parser_basis,type_basis,lambda_environment,debugger_environment,...},
new_debug_info) =
BASIS{parser_basis=parser_basis,
type_basis=type_basis,
lambda_environment=lambda_environment,
debugger_environment=debugger_environment,
debug_info=new_debug_info}
fun get_basis_debug_info(BASIS{debug_info,...}) = debug_info
fun clear_debug_info
(name,
BASIS{parser_basis,type_basis,lambda_environment,debugger_environment,debug_info}) =
BASIS{parser_basis=parser_basis,
type_basis=type_basis,
lambda_environment=lambda_environment,
debugger_environment=debugger_environment,
debug_info = Debugger_Types.clear_information (name, debug_info)}
fun clear_debug_all_info
(BASIS{parser_basis,type_basis,lambda_environment,debugger_environment,...}) =
BASIS{parser_basis=parser_basis,
type_basis=type_basis,
lambda_environment=lambda_environment,
debugger_environment=debugger_environment,
debug_info = Debugger_Types.empty_information}
fun make_external (BASIS {parser_basis, type_basis, lambda_environment,
debugger_environment, debug_info}) =
BASIS {parser_basis = parser_basis,
type_basis = type_basis,
lambda_environment = Environ.make_external lambda_environment,
debugger_environment = debugger_environment,
debug_info = debug_info}
local
val initial_top_env =
EnvironTypes.TOP_ENV (Primitives.env_for_not_ml_definable_builtins, Environ.empty_fun_env)
val initial_top_env_for_builtin_library =
EnvironTypes.TOP_ENV (Primitives.initial_env_for_builtin_library, Environ.empty_fun_env)
val builtin_top_env = EnvironTypes.TOP_ENV (Primitives.env_after_builtin, Environ.empty_fun_env)
in
val initial_basis =
BASIS {parser_basis = Parser.initial_pB,
type_basis = Basis.initial_basis,
lambda_environment = initial_top_env,
debugger_environment = Environ.empty_denv,
debug_info = Debugger_Types.empty_information}
val initial_basis_for_builtin_library =
BASIS {parser_basis = Parser.initial_pB_for_builtin_library,
type_basis = Basis.initial_basis_for_builtin_library,
lambda_environment = initial_top_env_for_builtin_library,
debugger_environment = Environ.empty_denv,
debug_info = Debugger_Types.empty_information}
val builtin_lambda_environment = builtin_top_env
end
datatype id_cache = ID_CACHE of {stamp_start:int,
stamp_no:int}
datatype result =
RESULT of {basis : basis,
signatures : (Ident.SigId, Parser.Absyn.SigExp) Map.map,
code : Module option,
id_cache : id_cache}
fun extract_signatures
(signatures, Parser.Absyn.SIGNATUREtopdec (sigbinds, _)) =
Lists.reducel
(fn (map, Parser.Absyn.SIGBIND bindings) =>
Lists.reducel
(fn (map, (ident, exp, _)) => Map.define (map, ident, exp))
(map, bindings))
(signatures, sigbinds)
| extract_signatures (signatures, _) = signatures
fun extract_identifiers (identifiers,
BasisTypes.BASIS (_,_,
BasisTypes.FUNENV functor_env,
BasisTypes.SIGENV signature_env,
Datatypes.ENV (Datatypes.SE structure_env,
Datatypes.TE type_env,
Datatypes.VE (_, value_env)))) =
let
val identifiers =
Map.fold_in_rev_order
(fn (identifiers, valid, _) => (Ident.VALUE valid)::identifiers)
(identifiers, value_env)
val identifiers =
Map.fold_in_rev_order
(fn (identifiers, tycon, _) => (Ident.TYPE tycon)::identifiers)
(identifiers, type_env)
val identifiers =
Map.fold_in_rev_order
(fn (identifiers, strid, _) => (Ident.STRUCTURE strid)::identifiers)
(identifiers, structure_env)
val identifiers =
Map.fold_in_rev_order
(fn (identifiers, funid, _) => (Ident.FUNCTOR funid)::identifiers)
(identifiers, functor_env)
val identifiers =
Map.fold_in_rev_order
(fn (identifiers, sigid, _) => (Ident.SIGNATURE sigid)::identifiers)
(identifiers, signature_env)
in
identifiers
end
datatype source =
TOKENSTREAM of Lexer.TokenStream |
TOKENSTREAM1 of Lexer.TokenStream |
TOPDEC of string * Parser.Absyn.TopDec * ParserBasis
fun compile (info_opts,
options as (Options.OPTIONS
{listing_options = Options.LISTINGOPTIONS list_opts,
compiler_options =
Options.COMPILEROPTIONS{generate_debug_info,
generate_moduler, ...},
print_options,
compat_options =
Options.COMPATOPTIONS{old_definition,...},
...}))
require_function
(require_value,
BASIS {parser_basis = initial_parser_basis,
type_basis = initial_type_basis,
lambda_environment = initial_lambda_environment,
debugger_environment = initial_debugger_environment,
...},
making)
(pervasive, source) =
let
val stamp_start = ref (Stamp.read_counter ())
val (filename, token_stream) =
case source of
TOKENSTREAM token_stream =>
(Lexer.associated_filename token_stream, SOME token_stream)
| TOKENSTREAM1 token_stream =>
(Lexer.associated_filename token_stream, SOME token_stream)
| TOPDEC (filename,_,_) => (filename, NONE)
fun error_wrap arg = Info.wrap info_opts (Info.FATAL, Info.RECOVERABLE, Info.FAULT, Location.FILE filename) arg
val _ = diagnostic
(1, fn _ =>
case source of
TOKENSTREAM _ => ["Consuming token stream `", filename, "'"]
| TOKENSTREAM1 _ => ["Consuming one topdec from token stream `", filename, "'"]
| TOPDEC _ => ["Consuming absyn topdec from source `", filename, "'"])
val (require_value,
_, _, _, _,
parser_basis, type_basis,
lambda_environment, debugger_environment, lambda_bindings, debug_info,
signatures, _) =
let
fun compile_topdec ((require_value,
total_parser_basis, total_type_basis, total_lambda_environment, total_debugger_environment,
parser_basis, type_basis, lambda_environment, debugger_environment,
lambda_bindings, debug_info,
signatures, had_topdec),
absyn) =
case absyn of
Absyn.REQUIREtopdec (module, location) =>
if had_topdec then
Info.error'
info_opts
(Info.FATAL, location, concat ["Too late for require statement"])
else
let
val (require_value,
full_module_name,
BASIS {parser_basis = require_parser_basis,
type_basis = require_type_basis,
lambda_environment = require_lambda_environment,
debugger_environment = require_debugger_environment,
...}) =
require_function (require_value, module, location)
val module_expression =
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.LOAD_STRING,
([LambdaTypes.SCON (Ident.STRING full_module_name, NONE)],[]),
NONE)
val (require_lambda_environment, require_bindings) =
LambdaModule.unpack (require_lambda_environment, module_expression)
in
stamp_start := Stamp.read_counter ();
(require_value,
Parser.augment_pB (total_parser_basis, require_parser_basis),
Basis.basis_circle_plus_basis (total_type_basis, require_type_basis),
Environ.augment_top_env (total_lambda_environment, require_lambda_environment),
Environ.augment_denv (total_debugger_environment,require_debugger_environment),
parser_basis,
type_basis,
lambda_environment,
debugger_environment,
lambda_bindings @ require_bindings,
debug_info,
signatures,
false)
end
| absyn =>
let
val topdec_type_basis =
error_wrap Mod_Rules.check_topdec
(options,false,absyn, total_type_basis,
Mod_Rules.ASSEMBLY(!str_ass, !ty_ass))
val (str_ass', ty_ass') =
Assemblies.compose_assemblies
(Assemblies.new_assemblies_from_basis topdec_type_basis,
(!str_ass, !ty_ass), topdec_type_basis, total_type_basis)
val _ = str_ass := str_ass'
val _ = ty_ass := ty_ass'
val _ =
if #show_absyn list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The abstract syntax\n"));
Info.listing_fn
info_opts
(3, fn stream =>
TextIO.output(stream,
TopdecPrint.topdec_to_string options absyn));
Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "\n"))
)
else ()
val result_type_basis =
Basis.basis_circle_plus_basis
(total_type_basis, topdec_type_basis)
val (topdec_lambda_environment, topdec_debugger_environment, topdec_lambda_bindings, debug_info) =
error_wrap
Lambda.trans_top_dec
(options,absyn, total_lambda_environment, total_debugger_environment,
debug_info, result_type_basis, false)
in
(require_value,
total_parser_basis,
result_type_basis,
Environ.augment_top_env
(total_lambda_environment, topdec_lambda_environment),
Environ.augment_denv
(total_debugger_environment, topdec_debugger_environment),
parser_basis,
Basis.basis_circle_plus_basis
(type_basis, topdec_type_basis),
Environ.augment_top_env
(lambda_environment, topdec_lambda_environment),
Environ.augment_denv
(debugger_environment, topdec_debugger_environment),
lambda_bindings @ topdec_lambda_bindings,
debug_info,
extract_signatures (signatures, absyn),
true)
end
fun parse_topdec ((require_value,
total_parser_basis, total_type_basis, total_lambda_environment, total_debugger_environment,
parser_basis, type_basis, lambda_environment, debugger_environment,
lambda_bindings, debug_info,
signatures, had_topdec),
token_stream) =
let
val (absyn, topdec_parser_basis) = error_wrap Parser.parse_topdec (options,token_stream, total_parser_basis)
in
diagnostic (2, fn _ => ["parsed topdec:\n",
TopdecPrint.topdec_to_string
options absyn]);
((require_value,
Parser.augment_pB (total_parser_basis, topdec_parser_basis),
total_type_basis,
total_lambda_environment,
total_debugger_environment,
Parser.augment_pB (parser_basis, topdec_parser_basis),
type_basis,
lambda_environment,
debugger_environment,
lambda_bindings,
debug_info,
signatures,
had_topdec),
absyn)
end
val environments =
(require_value,
initial_parser_basis,
initial_type_basis,
initial_lambda_environment,
initial_debugger_environment,
(case source of
TOPDEC(_,_,parserbasis) => parserbasis
| _ => Parser.empty_pB),
Basis.empty_basis,
Environ.empty_top_env,
Environ.empty_denv,
[],
Debugger_Types.empty_information,
Map.empty' Datatypes.Ident.sigid_lt,
false)
val _ =
if pervasive orelse not making then
()
else
let
val the_token_list =
[Token.RESERVED(Token.REQUIRE),
Token.STRING(Io.pervasive_library_name),
Token.RESERVED(Token.SEMICOLON)]
val token_stream = case token_stream of
SOME token_stream => token_stream
| _ => Crash.impossible "Non-pervasive make with no stream"
in
app
(fn token => Lexer.ungetToken((token,Info.Location.UNKNOWN), token_stream))
(rev the_token_list)
end
in
case source of
TOPDEC (_, absyn,_) =>
compile_topdec (environments, absyn)
| TOKENSTREAM1 token_stream =>
compile_topdec (parse_topdec (environments, token_stream))
| TOKENSTREAM token_stream =>
let
fun until_eof environments =
if Parser.Lexer.eof token_stream then
environments
else
until_eof (compile_topdec (parse_topdec (environments, token_stream)))
in
until_eof environments
end
end
val stamp_no = Stamp.read_counter () - !stamp_start
val new_id_cache = ID_CACHE{stamp_start = !stamp_start,
stamp_no = stamp_no}
in
let
val (lambda_environment, lambda_expression) =
LambdaModule.pack (lambda_environment, lambda_bindings)
val _ =
if #show_lambda list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The unoptimised lambda code\n"));
Info.listing_fn info_opts
(3, fn stream =>
LambdaPrint.output_lambda options (stream, lambda_expression)))
else ()
val lambda_expression = LambdaOptimiser.optimise options lambda_expression
val _ =
if #show_opt_lambda list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The optimised lambda code\n"));
Info.listing_fn info_opts
(3, fn stream =>
LambdaPrint.output_lambda options (stream, lambda_expression)))
else ()
val lambda_environment = Environ.simplify_topenv (lambda_environment,lambda_expression)
val _ =
if #show_environ list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The environment\n"));
Info.listing_fn info_opts
(3, EnvironPrint.printtopenv print_options lambda_environment);
Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "\n")))
else ()
val (mir, debug_info) =
error_wrap
Mir_Cg.mir_cg
(options, lambda_expression, filename, debug_info)
val _ =
if #show_mir list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The unoptimised intermediate code\n"));
Info.listing_fn info_opts (3, MirPrint.print_mir_code mir))
else
()
val mir = MirOptimiser.optimise (mir,generate_debug_info)
val _ =
if #show_opt_mir list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The optimised intermediate code\n"));
Info.listing_fn info_opts (3, MirPrint.print_mir_code mir))
else
()
val ((code, debug_info), code_list_list) =
let
val {gc, non_gc, fp} = MirOptimiser.machine_register_assignments
in
error_wrap
Mach_Cg.mach_cg
(options, mir, (gc, non_gc, fp), debug_info)
end
val _ =
if #show_mach list_opts then
(Info.listing_fn
info_opts
(3, fn stream => TextIO.output(stream, "The final machine code\n"));
Info.listing_fn info_opts (3, MachPrint.print_mach_code code_list_list))
else
()
in
(require_value,
RESULT {basis = BASIS {parser_basis = parser_basis,
type_basis = type_basis,
lambda_environment = lambda_environment,
debugger_environment = debugger_environment,
debug_info = debug_info},
signatures = signatures,
id_cache = new_id_cache,
code = SOME code})
end
end
end
;
