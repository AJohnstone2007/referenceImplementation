require "../basis/__text_io";
require "../basis/__io";
require "../system/__time";
require "../basics/module_id";
require "../main/project";
require "../utils/lists";
require "../utils/diagnostic";
require "../typechecker/basis";
require "../typechecker/stamp";
require "../main/compiler";
require "../main/mlworks_io";
require "../main/encapsulate";
require "../utils/crash";
require "../lexer/lexer";
require "interload";
require "intermake";
functor InterMake (
structure ModuleId: MODULE_ID
structure Project: PROJECT
structure Lists : LISTS
structure Compiler : COMPILER
structure Lexer : LEXER
structure InterLoad : INTERLOAD
structure MLWorksIo : MLWORKS_IO
structure Encapsulate : ENCAPSULATE
structure Basis : BASIS
structure Stamp : STAMP
structure Crash : CRASH
structure Diagnostic : DIAGNOSTIC
sharing InterLoad.Inter_EnvTypes.Options = Compiler.Options
sharing Encapsulate.BasisTypes = Basis.BasisTypes
sharing Compiler.NewMap = InterLoad.Inter_EnvTypes.EnvironTypes.NewMap =
Encapsulate.ParserEnv.Map
sharing InterLoad.Inter_EnvTypes.EnvironTypes = Encapsulate.EnvironTypes
sharing Project.Info = Compiler.Info
sharing Compiler.Options = Encapsulate.Debugger_Types.Options
sharing type Compiler.Top_Env = Encapsulate.EnvironTypes.Top_Env
sharing type Basis.BasisTypes.Basis = Compiler.TypeBasis
sharing type Encapsulate.ParserEnv.pB = Compiler.ParserBasis
sharing type Compiler.DebugInformation =
Encapsulate.Debugger_Types.information
sharing type Lexer.TokenStream = Compiler.tokenstream
sharing type Encapsulate.Module = InterLoad.Module = Compiler.Module
sharing type ModuleId.ModuleId = MLWorksIo.ModuleId = Project.ModuleId
sharing type ModuleId.Location = Compiler.Absyn.Ident.Location.T
sharing type Encapsulate.EnvironTypes.LambdaTypes.Ident.ValId =
Compiler.Absyn.Ident.ValId
sharing type Encapsulate.EnvironTypes.LambdaTypes.Ident.StrId =
Compiler.Absyn.Ident.StrId
sharing type Encapsulate.EnvironTypes.DebuggerEnv = Compiler.DebuggerEnv
sharing type Basis.BasisTypes.Datatypes.Stamp = Stamp.Stamp
sharing type Basis.BasisTypes.Datatypes.StampMap = Stamp.Map.T
sharing type Project.CompilerBasis = Compiler.basis
sharing type Project.IdCache = Compiler.id_cache
) : INTERMAKE =
struct
structure Debugger_Types = Encapsulate.Debugger_Types
structure Compiler = Compiler
structure Diagnostic = Diagnostic
structure Inter_EnvTypes = InterLoad.Inter_EnvTypes
structure Ident = Compiler.Absyn.Ident
structure Info = Compiler.Info
structure Options = Compiler.Options
structure Map = Inter_EnvTypes.EnvironTypes.NewMap
structure Token = Lexer.Token
structure Location = Ident.Location
structure Datatypes = Encapsulate.BasisTypes.Datatypes
type Project = Project.Project
type ModuleId = ModuleId.ModuleId
fun diagnostic (level, output_function) =
Diagnostic.output level
(fn verbosity => "InterMake: " :: (output_function verbosity))
fun augment_accumulated_info(options, basis, basis') =
Compiler.augment(options, basis, Compiler.make_external basis')
val get_basis_debug_info = Compiler.get_basis_debug_info
exception GetSubRequires of string
fun get_subrequires (acc, modname, project) =
let
fun test [] = false
| test ((m', _, _)::l) = modname = m' orelse test l
fun lookupmodname modname =
let
val mod_id = ModuleId.from_mo_string (modname, Location.UNKNOWN)
in
case Project.get_loaded_info (project, mod_id)
of SOME {id_cache, dependencies, ...} =>
let
val Compiler.ID_CACHE{stamp_start,stamp_no} = id_cache
in
(stamp_start, stamp_no, dependencies)
end
| NONE =>
Crash.impossible ("Unknown module: " ^ ModuleId.string mod_id)
end
in
if test acc
then acc
else
let
val (stamps, stamp_no, Project.DEPEND_LIST dependencies) =
lookupmodname modname
val acc' =
Lists.reducel
(fn (acc, modname) =>
get_subrequires (acc, modname, project))
((modname, stamps,stamp_no) :: acc,
(map #mod_name dependencies))
in
acc'
end
end
val Basis.BasisTypes.BASIS(_, _, _, _, initial_env_for_normal_file) = Basis.initial_basis
fun get_mo_information (project, location) module_id =
let
val modname = ModuleId.string module_id
val dir = ModuleId.path_string (ModuleId.path module_id)
val mo_name =
case Project.get_object_info (project, module_id)
of SOME {file, ...} =>
file
| NONE =>
raise MLWorks.Internal.Runtime.Loader.Load "cannot find mo file"
val {parser_env, type_env, lambda_env, stamps, consistency,
time_stamp = src_time, mod_name} =
Encapsulate.input_all mo_name
val stamp_count = Stamp.read_counter ()
val _ = Stamp.reset_counter (stamp_count + stamps)
val id_cache = Compiler.ID_CACHE{stamp_start = stamp_count,
stamp_no = stamps}
val subnames = map #mod_name consistency
val require_list =
Lists.reducel
(fn (acc,modname) =>
get_subrequires (acc, modname, project))
([(modname, stamp_count, stamps)], subnames)
val require_table =
Map.from_list'
op<
(map (fn (x as (name, _, _)) => (name, x)) require_list)
val (parser_env, lambda_env, type_basis, debug_info) =
Encapsulate.decode_all
{parser_env=parser_env,
lambda_env=lambda_env,
type_env=type_env,
file_name=mod_name,
sub_modules=require_table,
decode_debug_information=false,
pervasive_env=initial_env_for_normal_file}
val compiler_basis =
Compiler.BASIS
{parser_basis = parser_env,
type_basis = type_basis,
lambda_environment = lambda_env,
debugger_environment =
Inter_EnvTypes.EnvironTypes.DENV
(Map.empty (Ident.valid_lt,Ident.valid_eq),
Map.empty (Ident.strid_lt,Ident.strid_eq)),
debug_info = debug_info}
val compiler_result =
Compiler.RESULT
{basis = compiler_basis,
signatures = Map.empty (Ident.sigid_lt,Ident.sigid_eq),
code = NONE,
id_cache = id_cache}
in
compiler_result
end
fun load options (project, location) module_id =
let
val Options.OPTIONS
{compiler_options = Options.COMPILEROPTIONS
{print_messages, ...},
...} =
options
val _ =
if print_messages then
print ("Loading mo module " ^ ModuleId.string module_id ^ "\n")
else
()
val (mo_name, time, dependencies) =
case Project.get_object_info (project, module_id)
of SOME {file, time_stamp,
consistency = Project.DEPEND_LIST (_ :: cons), ...} =>
(file, time_stamp, cons)
| SOME _ =>
Crash.impossible "No entry for pervasive library in consistency"
| NONE =>
raise MLWorks.Internal.Runtime.Loader.Load "cannot find mo file"
val (module_id', module) =
MLWorks.Internal.Runtime.Loader.load_module mo_name
val compiler_result =
get_mo_information (project, location) module_id
val (id_cache, basis) =
case compiler_result
of Compiler.RESULT {id_cache, basis, ...} => (id_cache, basis)
fun get_load_time {mod_name, time} =
case
Project.get_loaded_info
(project, ModuleId.from_string (mod_name, Location.UNKNOWN))
of SOME {load_time, ...} =>
{mod_name = mod_name, time = load_time}
| NONE => Crash.impossible ("no loaded info for `" ^ mod_name ^ "'")
in
Project.set_visible (project, module_id, false);
Project.set_loaded_info
(project, module_id,
SOME
{file_time = Project.OBJECT time,
load_time = Time.now (),
basis = basis,
id_cache = id_cache,
module = module,
dependencies =
Project.DEPEND_LIST (map get_load_time dependencies)});
(compiler_result, module)
end
handle
MLWorks.Internal.Runtime.Loader.Load s =>
Info.error'
(Info.make_default_options ())
(Info.FATAL,
Info.Location.FILE (ModuleId.string module_id),
"Load failed: " ^ s)
| Encapsulate.BadInput s =>
Info.error'
(Info.make_default_options ())
(Info.FATAL,
Info.Location.FILE (ModuleId.string module_id),
"Load failed: " ^ s)
val debug_info_ref = ref Debugger_Types.empty_information
fun with_debug_information debug_info f =
let
val old = !debug_info_ref
val _ = debug_info_ref := debug_info
val result = f () handle exn => (debug_info_ref := old;raise exn)
in
debug_info_ref := old;
result
end
fun current_debug_information () = !debug_info_ref
local
fun load' debugger
initial_inter_env
(error_info, location, options)
preloaded_opt
(project, module_id, module_str, accumulated_info,
compiler_result, src_time, consistency) =
let
val _ = diagnostic (4, fn _ => ["loading ", module_str])
val generate_debug_info =
case options of
Options.OPTIONS
{compiler_options =
Options.COMPILEROPTIONS {generate_debug_info, ...},
...} =>
generate_debug_info
val Compiler.RESULT
{code, basis, signatures, id_cache} =
compiler_result
val Compiler.BASIS {lambda_environment, ...} = basis
val accumulated_info =
Debugger_Types.augment_information
(generate_debug_info,
accumulated_info,
get_basis_debug_info basis)
val module =
case preloaded_opt
of SOME preloaded =>
(Lists.assoc (module_str, preloaded)
handle Lists.Assoc =>
Crash.impossible "No pervasive modules!")
| NONE =>
let
fun module_map module_str =
case Project.get_loaded_info
(project, ModuleId.from_mo_string
(module_str, Info.Location.UNKNOWN))
of SOME {module, ...} => module
| NONE =>
Crash.impossible
("Can't find compilation unit `" ^ module_str
^ "' in project when loading")
in
case code of
NONE =>
Info.error'
error_info
(Info.FATAL, location,
concat
["`", module_str,
"' database entry has no code recorded."])
| SOME code' =>
with_debug_information
accumulated_info
(fn () =>
InterLoad.load
debugger
(initial_inter_env, module_map)
code')
end
val compiler_result =
Compiler.RESULT
{code=NONE,
basis=basis,
signatures=signatures,
id_cache=id_cache}
val _ =
Project.set_visible (project, module_id, false);
val _ =
Project.set_loaded_info
(project,
module_id,
SOME
{load_time = Time.now(),
file_time = Project.SOURCE src_time,
basis = basis,
id_cache = id_cache,
module = module,
dependencies = Project.DEPEND_LIST consistency})
val _ = diagnostic (2, fn _ => ["finished ", module_str])
in
(compiler_result, module, accumulated_info)
end
fun compile'
initial_compiler_basis
(error_info, location, options)
(project, module_id, filename, time) =
let
val module_str = ModuleId.string module_id
val is_pervasive = ModuleId.is_pervasive module_id
val Options.OPTIONS
{compiler_options = Options.COMPILEROPTIONS
{print_messages, ...},
...} =
options
val _ =
if print_messages then
print ("Compiling " ^ module_str ^ "\n")
else
()
val _ = diagnostic
(2, fn _ => ["Compiling ", module_str, " as ", filename]);
val (dependencies, compiler_result) =
let
val instream =
TextIO.openIn filename
handle IO.Io {name, cause, ...} =>
let
val message = exnMessage cause ^ " in: " ^ name
in
Info.error'
error_info
(Info.FATAL, Info.Location.FILE filename,
"Io error during make, " ^ message)
end
in
let
val token_stream =
let
val stream_name =
if is_pervasive then "<Pervasive>"
else filename
in
Lexer.mkFileTokenStream (instream, stream_name)
end
fun require_function
(dependencies, sub_module_name, source_location) =
let
val _ = diagnostic (3, fn _ =>
[ModuleId.string module_id, " requiring ",
sub_module_name])
val (full_module_id, loaded_info) =
if is_pervasive orelse
sub_module_name = MLWorksIo.pervasive_library_name then
let
val m =
ModuleId.perv_from_require_string
(sub_module_name, source_location)
in
(m, Project.get_loaded_info (project, m))
end
else
let
val m =
ModuleId.add_path
(ModuleId.empty_path,
ModuleId.from_require_string
(sub_module_name, source_location))
in
(Project.get_name (project, m),
Project.get_loaded_info (project, m))
end
val (sub_time, basis) =
case loaded_info
of NONE =>
Crash.impossible
("No loaded info for `"
^ sub_module_name ^ "' in project")
| SOME {load_time, basis, ...} =>
(load_time, basis)
in
({mod_name = ModuleId.string full_module_id,
time = sub_time}
:: dependencies,
ModuleId.string full_module_id,
basis)
end
val (dependencies, compiler_result) =
Compiler.compile
(error_info, options)
require_function
([], initial_compiler_basis, true)
(is_pervasive, Compiler.TOKENSTREAM token_stream)
in
TextIO.closeIn instream;
(dependencies, compiler_result)
end
handle exn => (TextIO.closeIn instream; raise exn)
end
in
(module_str, compiler_result, time, dependencies)
end
in
fun compile
debugger
(initial_compiler_basis, initial_inter_env)
(error_info, location, options)
preloaded_opt
(project, module_id, accumulated_info) =
case Project.get_source_info (project, module_id)
of NONE =>
let
val (compilerResult, module) =
load options (project, location) module_id
in
(compilerResult, module, accumulated_info)
end
| SOME (filename, time) =>
let
val (module_str, compiler_result, time, dependencies) =
compile'
initial_compiler_basis
(error_info, location, options)
(project, module_id, filename, time)
val result =
load'
debugger
initial_inter_env
(error_info, location, options)
preloaded_opt
(project, module_id, module_str, accumulated_info,
compiler_result, time, dependencies)
in
result
end
fun get_src_information
initial_compiler_basis
(error_info, location, options)
(project, module_id) =
case Project.get_source_info (project, module_id)
of NONE =>
Info.error'
error_info
(Info.FATAL, location,
"can't find compilation unit `" ^
ModuleId.string module_id ^ "'")
| SOME (filename, time) =>
let
val (_, compiler_result, _, _) =
compile'
initial_compiler_basis
(error_info, location, options)
(project, module_id, filename, time)
in
compiler_result
end
end
end
;
