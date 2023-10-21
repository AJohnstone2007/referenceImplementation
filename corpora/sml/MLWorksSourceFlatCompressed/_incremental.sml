require "^.basis.list";
require "^.basis.__text_io";
require "../utils/diagnostic";
require "../utils/crash";
require "../basics/module_id";
require "../main/project";
require "../main/proj_file";
require "../main/encapsulate";
require "../lambda/environ";
require "../typechecker/basis";
require "../typechecker/stamp";
require "../lexer/lexer";
require "../parser/parserenv";
require "../main/mlworks_io";
require "interload";
require "intermake";
require "incremental";
functor Incremental
(structure Environ : ENVIRON
structure InterLoad : INTERLOAD
structure InterMake : INTERMAKE
structure Basis : BASIS
structure Stamp : STAMP
structure Lexer : LEXER
structure ParserEnv : PARSERENV
structure List : LIST
structure Diagnostic : DIAGNOSTIC
structure Io : MLWORKS_IO
structure ModuleId : MODULE_ID
structure Project : PROJECT
structure ProjFile : PROJ_FILE
structure Encapsulate : ENCAPSULATE
structure Crash : CRASH
sharing Environ.EnvironTypes = InterMake.Inter_EnvTypes.EnvironTypes
sharing InterLoad.Inter_EnvTypes = InterMake.Inter_EnvTypes
sharing ParserEnv.Map = Basis.BasisTypes.Datatypes.NewMap
sharing ParserEnv.Ident = Basis.BasisTypes.Datatypes.Ident =
InterMake.Compiler.Absyn.Ident =
Environ.EnvironTypes.LambdaTypes.Ident
sharing Environ.EnvironTypes.NewMap = Basis.BasisTypes.Datatypes.NewMap =
InterMake.Compiler.NewMap
sharing ParserEnv.Ident.Location = InterMake.Compiler.Info.Location
sharing InterMake.Compiler.Info = Project.Info
sharing type Project.Project = InterMake.Project
sharing type InterMake.Compiler.Module = InterLoad.Module
sharing type Environ.Structure = Basis.BasisTypes.Datatypes.Structure
sharing type Environ.EnvironTypes.LambdaTypes.Type =
Basis.BasisTypes.Datatypes.Type
sharing type InterMake.Compiler.TypeBasis = Basis.BasisTypes.Basis
sharing type Lexer.TokenStream = InterMake.Compiler.tokenstream
sharing type InterMake.Compiler.ParserBasis = ParserEnv.pB
sharing type Io.ModuleId = Project.ModuleId =
InterMake.ModuleId = ModuleId.ModuleId
sharing type InterMake.Compiler.DebuggerEnv =
Environ.EnvironTypes.DebuggerEnv
sharing type ParserEnv.Ident.Location.T = ModuleId.Location = Io.Location
sharing type Basis.BasisTypes.Datatypes.Stamp = Stamp.Stamp
sharing type Basis.BasisTypes.Datatypes.StampMap = Stamp.Map.T
sharing type InterMake.Compiler.basis = Project.CompilerBasis
sharing type InterMake.Compiler.id_cache = Project.IdCache
) : INCREMENTAL =
struct
structure Compiler = InterMake.Compiler
structure PE = ParserEnv
structure Lexer = Lexer
structure Info = Compiler.Info
structure EnvironTypes = Environ.EnvironTypes
structure BasisTypes = Basis.BasisTypes
structure Datatypes = BasisTypes.Datatypes
structure Map = Datatypes.NewMap
structure Diagnostic = Diagnostic
structure Absyn = Compiler.Absyn
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure LambdaTypes = EnvironTypes.LambdaTypes
structure Inter_EnvTypes = InterMake.Inter_EnvTypes
structure InterMake = InterMake
structure Options = Inter_EnvTypes.Options
type ModuleId = ModuleId.ModuleId
val empty_compiler_basis =
case InterMake.Compiler.initial_basis of
InterMake.Compiler.BASIS
{parser_basis,
type_basis,
lambda_environment,
debugger_environment,
debug_info} =>
InterMake.Compiler.BASIS
{parser_basis = ParserEnv.empty_pB,
type_basis = type_basis,
lambda_environment = lambda_environment,
debugger_environment = debugger_environment,
debug_info = debug_info}
fun diagnostic (level, output_function) =
Diagnostic.output level
(fn verbosity => "Incremental: " :: (output_function verbosity))
fun diagnostic_fn (level, output_function) =
Diagnostic.output_fn level
(fn (verbosity, stream) => (TextIO.output (stream, "Incremental: ");
output_function (verbosity, stream)))
fun fatality (location, message) =
Info.error'
(Info.make_default_options ())
(Info.FATAL, location, message)
datatype Context =
CONTEXT of {topdec : int,
compiler_basis : Compiler.basis,
inter_env : Inter_EnvTypes.inter_env,
signatures : (Ident.SigId, Absyn.SigExp) Map.map}
datatype options =
OPTIONS of
{options : Options.options,
debugger : (MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T) ->
(MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T)}
val default_error_info = Info.make_default_options ()
val cast = MLWorks.Internal.Value.cast
val _ = Io.set_source_path_from_env
((Info.Location.FILE "Initial Context"), false);
val _ = Io.set_object_path_from_env
(Info.Location.FILE "Initial Context");
val _ = Stamp.reset_counter Basis.pervasive_stamp_count
type register_key = int
val key_count = ref 0;
val register_map = ref (Map.empty' (op< : int * int -> bool))
fun add_update_fn f =
(key_count := !key_count + 1;
register_map := Map.define (!register_map, !key_count, f);
!key_count)
fun remove_update_fn c =
register_map := Map.undefine (!register_map, c)
val inf_loc = (default_error_info, Info.Location.FILE "_incremental");
val project =
ref (Project.initialize inf_loc);
fun set_project p =
(project := p;
Map.iterate (fn (_, f) => f()) (!register_map))
fun get_project () =
if ProjFile.changed()
then
let val p = Project.fromFileInfo inf_loc (!project)
in project := p; p
end
else
!project
fun delete_from_project m =
set_project (Project.delete (!project, m, true))
local
val _ = diagnostic_fn (0, fn (_, stream) =>
TextIO.output (stream, "making the builtin library\n"))
val runtime_modules =
map (fn (str, value, _) => (str, value))
(!MLWorks.Internal.Runtime.modules)
val (builtin_library_result, builtin_module, _) =
InterMake.compile
(fn y => y)
(Compiler.initial_basis_for_builtin_library, Inter_EnvTypes.empty_env)
(default_error_info,
Info.Location.FILE "Making BuiltIn Library",
Options.default_options)
(SOME runtime_modules)
(!project, Io.builtin_library_id,
Compiler.get_basis_debug_info
Compiler.initial_basis_for_builtin_library)
val debugger_environment =
case Project.get_loaded_info
(!project, Io.builtin_library_id)
of NONE =>
Crash.impossible "Can't find loaded information for builtin library"
| SOME {file_time, load_time, basis, id_cache, module, dependencies} =>
let
val Compiler.BASIS
{parser_basis, type_basis, debugger_environment, debug_info,
...} = basis
val new_basis =
Compiler.BASIS
{parser_basis = parser_basis,
type_basis = type_basis,
debug_info = debug_info,
lambda_environment = Compiler.builtin_lambda_environment,
debugger_environment = debugger_environment}
in
Project.set_loaded_info
(!project,
Io.builtin_library_id,
SOME
{file_time = file_time,
load_time = load_time,
id_cache = id_cache,
basis = new_basis,
module = module,
dependencies = dependencies});
debugger_environment
end
val _ =
diagnostic_fn (0, fn (_, stream) =>
TextIO.output (stream, "making the pervasive library\n"))
val out_of_date =
Project.check_perv_loaded
(default_error_info,
Info.Location.FILE "Making Pervasive Library")
(!project)
fun compile_perv (module_id, (_, _, accumulated_info)) =
InterMake.compile
(fn y => y)
(Compiler.initial_basis, Inter_EnvTypes.empty_env)
(default_error_info,
Info.Location.FILE "Making Pervasive Library",
Options.default_options)
(SOME runtime_modules)
(!project, module_id, accumulated_info)
val (pervasive_library_result, pervasive_module, _) =
List.foldl
compile_perv
(builtin_library_result, builtin_module,
Compiler.get_basis_debug_info Compiler.initial_basis)
out_of_date
val Compiler.RESULT {basis, signatures, ...} =
pervasive_library_result
val Compiler.BASIS {lambda_environment, parser_basis, ...} = basis
val ParserEnv.B(_,_,ParserEnv.E(_,_,pervasive_pTE,_)) = parser_basis
val inter_env =
Inter_EnvTypes.augment_with_module
(Inter_EnvTypes.empty_env, lambda_environment, pervasive_module)
handle Inter_EnvTypes.Augment =>
Crash.impossible "Module does not match generated environment"
val strid = Ident.STRID(Symbol.find_symbol "FullPervasiveLibrary_")
val inter_env = Inter_EnvTypes.remove_str(inter_env, strid)
in
val initial_basis =
let
val Compiler.BASIS{parser_basis,
type_basis,
lambda_environment,
debug_info,
...} = Compiler.initial_basis
val augmented_parser_basis =
ParserEnv.augment_pB
(ParserEnv.B(ParserEnv.empty_pF,
ParserEnv.empty_pG,
ParserEnv.E(ParserEnv.empty_pFE,
ParserEnv.empty_pVE,
pervasive_pTE,
ParserEnv.empty_pSE)),
parser_basis)
in
Compiler.BASIS{parser_basis=augmented_parser_basis,
type_basis=type_basis,
lambda_environment=lambda_environment,
debugger_environment=debugger_environment,
debug_info=debug_info}
end
val basis = Compiler.remove_str(basis, strid)
val initial_compiler_basis =
Compiler.make_external
(Compiler.augment (Options.default_options, initial_basis, basis))
val initial_inter_env = inter_env
val initial =
CONTEXT {topdec = 0,
compiler_basis = initial_compiler_basis,
inter_env = inter_env,
signatures = signatures}
end
val _ = Project.reset_pervasives (!project)
fun clear_debug_all_info
(CONTEXT {compiler_basis, topdec, inter_env, signatures}) =
CONTEXT{topdec=topdec,
inter_env=inter_env,
signatures=signatures,
compiler_basis = Compiler.clear_debug_all_info compiler_basis}
fun clear_debug_info
(name,
CONTEXT {compiler_basis, topdec, inter_env, signatures}) =
CONTEXT{topdec=topdec,
inter_env=inter_env,
signatures=signatures,
compiler_basis =
Compiler.clear_debug_info (name, compiler_basis)}
fun add_debug_info
(options, debug_info,
CONTEXT {compiler_basis, topdec, inter_env, signatures}) =
CONTEXT{topdec=topdec,
inter_env=inter_env,
signatures=signatures,
compiler_basis =
Compiler.add_debug_info
(options, debug_info, compiler_basis)}
fun topdec (CONTEXT record) = #topdec record
fun compiler_basis (CONTEXT record) = #compiler_basis record
fun parser_basis (CONTEXT {compiler_basis =
Compiler.BASIS {parser_basis, ...}, ...}) = parser_basis
fun type_basis (CONTEXT {compiler_basis = Compiler.BASIS {type_basis, ...}, ...}) = type_basis
fun lambda_environment (CONTEXT {compiler_basis = Compiler.BASIS {lambda_environment, ...}, ...}) = lambda_environment
fun debug_info (CONTEXT {compiler_basis = Compiler.BASIS {debug_info, ...}, ...}) = debug_info
fun inter_env (CONTEXT record) = #inter_env record
fun signatures (CONTEXT record) = #signatures record
val empty_context =
CONTEXT
{topdec = 0,
compiler_basis = InterMake.Compiler.empty_basis,
inter_env = Inter_EnvTypes.empty_env,
signatures = Map.empty' Ident.sigid_lt}
datatype Result =
RESULT of
{delta_basis: Compiler.basis,
delta_signatures: (Ident.SigId,Absyn.SigExp) Map.map,
new_module: MLWorks.Internal.Value.T}
val empty_signatures = Map.empty' Datatypes.Ident.sigid_lt
fun identifiers_from_result
(RESULT {delta_basis = Compiler.BASIS {type_basis, ...}, ...}) =
Compiler.extract_identifiers ([], type_basis)
fun pb_from_result
(RESULT {delta_basis = Compiler.BASIS {parser_basis, ...}, ...}) =
parser_basis
fun compile_source
error_info
(inc_options as OPTIONS {options,debugger,...},
(CONTEXT {compiler_basis, inter_env, ...}),
source) =
let
val filename =
case source of
Compiler.TOKENSTREAM token_stream => Lexer.associated_filename token_stream
| Compiler.TOKENSTREAM1 token_stream => Lexer.associated_filename token_stream
| Compiler.TOPDEC (filename,_,_) => filename
val _ = diagnostic_fn
(2, fn (_, stream) =>
(TextIO.output (stream, "Compiling topdec from ");
TextIO.output (stream, filename)))
val (_, Compiler.RESULT {basis = topdec_compiler_basis,
signatures = aug_signatures,
id_cache,
code}) =
Compiler.compile
(error_info, options)
(fn (_, _, location) =>
Info.error'
error_info
(Info.FATAL, location, "require used at top level")
)
((), compiler_basis, false)
(false, source)
val aug_compiler_basis = Compiler.make_external topdec_compiler_basis
val Compiler.BASIS{debug_info,...} =
Compiler.augment (options, compiler_basis, topdec_compiler_basis)
val module =
let
fun module_map module_name =
case Project.get_loaded_info
(!project,
ModuleId.from_mo_string
(module_name, Info.Location.UNKNOWN))
of SOME {module, ...} => module
| NONE =>
Crash.impossible
("can't find module " ^ module_name ^ " in project")
in
case code of
NONE =>
Info.error'
error_info
(Info.FAULT, Info.Location.FILE filename,
concat ["`", filename, "' database entry has no code recorded. "])
| SOME code' =>
InterMake.with_debug_information
debug_info
(fn () =>
InterLoad.load
debugger
(inter_env, module_map)
code')
end
in
RESULT
{delta_basis = aug_compiler_basis,
delta_signatures = aug_signatures,
new_module = module}
end
exception NotAnExpression
fun evaluate_exp_topdec error_info (inc_options,context,topdec) =
let
open Absyn
fun is_an_expression (STRDECtopdec (strdec,_)) =
(case strdec of
(DECstrdec (VALdec ([(pat,_,_)],[],_,_))) =>
(case pat of
VALpat ((valid,_),_) =>
(case valid of
Ident.LONGVALID (Ident.NOPATH,Ident.VAR sym) =>
Symbol.symbol_name sym = "it"
| _ => false)
| _ => false)
| _ => false)
| is_an_expression _ = false
in
if not(is_an_expression topdec)
then raise NotAnExpression
else
let
val (RESULT {delta_basis, new_module, ...}) =
compile_source
error_info
(inc_options,
context,
Compiler.TOPDEC("",topdec,PE.empty_pB))
val Compiler.BASIS{type_basis,...} = delta_basis
val ittype = #1(Basis.lookup_val (Ident.LONGVALID (Ident.NOPATH,Ident.VAR(Symbol.find_symbol"it")),
Basis.basis_to_context type_basis,
Info.Location.UNKNOWN,
false))
val val_list : MLWorks.Internal.Value.T list = cast new_module
in
case val_list of
[itval] => (itval,ittype)
| _ => Crash.impossible "Wrong number of bindings in evaluate_exp_topdec"
end
end
fun add_definitions
(options,
CONTEXT{topdec, compiler_basis, signatures, inter_env, ...},
RESULT
{delta_basis, delta_signatures, new_module, ...}) =
let
val new_compiler_basis =
Compiler.augment (options, compiler_basis, delta_basis)
val new_signatures =
Map.union (signatures, delta_signatures)
val new_inter_env =
let
val Compiler.BASIS {lambda_environment, ...} = delta_basis
in
Inter_EnvTypes.augment_with_module
(inter_env, lambda_environment, new_module)
handle Inter_EnvTypes.Augment =>
Crash.impossible "Module does not match generated environment"
end
in
CONTEXT {topdec = topdec+1,
compiler_basis = new_compiler_basis,
signatures = new_signatures,
inter_env = new_inter_env}
end
fun load_mos
error_info
(options, CONTEXT {compiler_basis, ...}, project,
moduleid, mod_id_list, location) =
let
fun load_one (module_id, _) =
SOME (InterMake.load
options
(project, location)
module_id)
in
case List.foldl load_one NONE mod_id_list
of SOME (compiler_result, new_module) =>
let
val result =
case compiler_result
of Compiler.RESULT
{basis = new_basis, signatures = new_signatures, ...} =>
RESULT
{delta_basis = Compiler.make_external new_basis,
delta_signatures = new_signatures,
new_module = new_module}
in
Project.set_visible (project, moduleid, true);
SOME result
end
| NONE =>
if Project.is_visible (project, moduleid) then
NONE
else
case Project.get_loaded_info (project, moduleid)
of SOME {basis, id_cache, module, ...} =>
let
val _ = Project.set_visible (project, moduleid, true);
val result =
RESULT
{delta_basis = Compiler.make_external basis,
delta_signatures = empty_signatures,
new_module = module}
in
SOME result
end
| NONE =>
Crash.impossible "impossible NONE returned in Incremental.load.mos"
end
fun delete_module error_info module_id =
Project.set_loaded_info (!project, module_id, NONE)
fun delete_all_modules true =
Project.clear_all_loaded_info (!project, not o ModuleId.is_pervasive)
| delete_all_modules false =
Project.clear_all_loaded_info (!project, fn _ => true)
fun remove_file_info () = Project.remove_file_info (!project)
val overload_var =
Ident.VAR (Ident.Symbol.find_symbol "<overload function>")
fun read_dependencies toplevel_name error_info module_id =
project :=
(#1 (Project.read_dependencies
(error_info, Info.Location.FILE toplevel_name)
(!project, module_id, Project.empty_map)))
fun check_module error_info (module_id, toplevel_name) =
let
val _ =
diagnostic
(2, fn _ =>
["Checking load source `", ModuleId.string module_id, "'"])
val (new_project, _) =
Project.read_dependencies
(error_info, Info.Location.FILE toplevel_name)
(!project, module_id, Project.empty_map)
val _ = set_project new_project
val module_ids =
Project.check_load_source
(error_info, Info.Location.FILE toplevel_name)
(new_project, module_id)
in
module_ids
end
local
val pervasive_project = !project
in
fun reset_project () =
(ignore(ProjFile.changed());
set_project (Project.fromFileInfo inf_loc (pervasive_project)))
end
fun add_value (context, identifier, scheme, value) =
let
val
CONTEXT {topdec,
compiler_basis =
Compiler.BASIS
{parser_basis,
type_basis,
lambda_environment = EnvironTypes.TOP_ENV (lambda_env, lambda_functor_env),
debugger_environment = debugger_env,
debug_info},
inter_env,
signatures} = context
val ident = Ident.VAR
(Ident.Symbol.find_symbol identifier)
val new_lambda_env =
Environ.add_valid_env (lambda_env,
(ident, EnvironTypes.EXTERNAL))
val new_debugger_env =
Environ.add_valid_denv (debugger_env,
(ident, EnvironTypes.NULLEXP))
val new_inter_env = Inter_EnvTypes.add_val (inter_env, (ident, value))
local val PE.B(pF,pG,PE.E(pFE,pVE,pTE,pSE)) = parser_basis
in
val new_parser_basis =
PE.B(pF,pG,PE.E(pFE,PE.addValId(fn (_,_,x) => x,ident,pVE),
pTE,pSE))
end
in
(CONTEXT {topdec = topdec+1,
compiler_basis =
Compiler.BASIS
{parser_basis = new_parser_basis,
type_basis = Basis.add_val (type_basis,ident,scheme),
lambda_environment = EnvironTypes.TOP_ENV (new_lambda_env,
lambda_functor_env),
debugger_environment = new_debugger_env,
debug_info = debug_info},
inter_env = new_inter_env,
signatures = signatures},
[Ident.VALUE ident])
end
fun env (CONTEXT{
compiler_basis =
Compiler.BASIS{
type_basis =
BasisTypes.BASIS(_,_, _, _, env),
...
},
...
}) = env
fun extend_parser_basis (strid,str,PE.B(pF,pG,PE.E(pFE,pVE,pTE,pSE))) =
let
fun make_pVE valenv =
Map.fold
(fn (pVE,valid,_) => PE.addValId(fn (_,_,x) => x,valid,pVE))
(PE.empty_pVE,valenv)
fun make_str_pE(Datatypes.COPYSTR(_,str)) = make_str_pE(str)
| make_str_pE(Datatypes.STR(_,_,env)) = make_env_pE env
and make_env_pE (Datatypes.ENV(Datatypes.SE strenv,
_,
Datatypes.VE(_,valenv))) =
PE.E(PE.empty_pFE,
make_pVE valenv, pTE,
PE.SE (Map.map (fn (_,str) => make_str_pE (str)) strenv))
in
PE.B(pF,
pG,
PE.E(pFE,pVE,pTE,PE.addStrId(fn (_,_,pE) => pE,
strid,make_str_pE str,
pSE)))
end
fun add_structure (context, identifier, str, value) =
let
val
CONTEXT {topdec,
compiler_basis =
Compiler.BASIS
{parser_basis,
type_basis,
lambda_environment = EnvironTypes.TOP_ENV (lambda_env, lambda_functor_env),
debugger_environment = debugger_env,
debug_info = debug_info},
inter_env, signatures} = context
val ident = Ident.STRID (Ident.Symbol.find_symbol identifier)
val new_lambda_env = Environ.add_strid_env (lambda_env,
(ident,
(Environ.make_str_env (str,false),
EnvironTypes.EXTERNAL,false)))
val new_debugger_env = Environ.add_strid_denv (debugger_env, (ident, Environ.make_str_dexp str))
val new_inter_env = Inter_EnvTypes.add_str (inter_env, (ident, value))
val new_parser_basis = extend_parser_basis (ident,str,parser_basis)
in
(CONTEXT {topdec = topdec+1,
compiler_basis =
Compiler.BASIS
{parser_basis = new_parser_basis,
type_basis = Basis.add_str (type_basis,ident,str),
lambda_environment = EnvironTypes.TOP_ENV (new_lambda_env, lambda_functor_env),
debugger_environment = new_debugger_env,
debug_info = debug_info},
inter_env = new_inter_env,
signatures = signatures},
[Ident.STRUCTURE ident])
end
end
;
