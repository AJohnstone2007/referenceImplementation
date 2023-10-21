require "../basis/__io";
require "../basis/__text_io";
require "../basis/__int";
require "../basis/os";
require "^.system.__file_time";
require "../utils/crash";
require "../utils/print";
require "../utils/lists";
require "../utils/diagnostic";
require "../utils/mlworks_timer";
require "../basics/module_id";
require "../parser/parser";
require "../typechecker/mod_rules";
require "../typechecker/basis";
require "../typechecker/stamp";
require "../lambda/environ";
require "../lambda/lambdaprint";
require "../lambda/environprint";
require "../lambda/lambda";
require "../lambda/lambdaoptimiser";
require "../lambda/lambdamodule";
require "../lambda/topdecprint";
require "../mir/mir_cg";
require "../mir/mirprint";
require "../mir/miroptimiser";
require "mach_cg";
require "machprint";
require "object_output";
require "project";
require "primitives";
require "pervasives";
require "encapsulate";
require "mlworks_io";
require "toplevel";
functor TopLevel
(structure OS : OS
structure Crash : CRASH
structure Print : PRINT
structure Lists : LISTS
structure Diagnostic : DIAGNOSTIC
structure Timer : INTERNAL_TIMER
structure Parser : PARSER
structure Mod_Rules : MODULE_RULES
structure Basis : BASIS
structure Stamp : STAMP
structure Environ : ENVIRON
structure LambdaPrint : LAMBDAPRINT
structure EnvironPrint : ENVIRONPRINT
structure Lambda : LAMBDA
structure LambdaOptimiser : LAMBDAOPTIMISER
structure LambdaModule : LAMBDAMODULE
structure Mir_Cg : MIR_CG
structure MirPrint : MIRPRINT
structure MirOptimiser : MIROPTIMISER
structure Mach_Cg : MACH_CG
structure MachPrint : MACHPRINT
structure Object_Output : OBJECT_OUTPUT
structure TopdecPrint : TOPDECPRINT
structure Primitives : PRIMITIVES
structure Pervasives : PERVASIVES
structure Encapsulate : ENCAPSULATE
structure Io : MLWORKS_IO
structure ModuleId : MODULE_ID
structure Project : PROJECT
sharing Lambda.Options =
Mir_Cg.Options =
EnvironPrint.Options =
LambdaPrint.Options =
TopdecPrint.Options =
LambdaOptimiser.Options =
Mod_Rules.Options =
Mach_Cg.Options
sharing Parser.Lexer.Info = Lambda.Info = Mir_Cg.Info = Mach_Cg.Info =
Mod_Rules.Info = Project.Info
sharing Basis.BasisTypes = Mod_Rules.Assemblies.Basistypes =
Encapsulate.BasisTypes = Lambda.BasisTypes
sharing Mir_Cg.MirTypes.Debugger_Types =
Encapsulate.Debugger_Types
sharing Parser.Absyn.Set = Basis.BasisTypes.Set
sharing Parser.Absyn =
Mod_Rules.Absyn =
Lambda.Absyn =
TopdecPrint.Absyn
sharing Environ.EnvironTypes.LambdaTypes =
LambdaPrint.LambdaTypes =
LambdaOptimiser.LambdaTypes =
Lambda.EnvironTypes.LambdaTypes =
Mir_Cg.LambdaTypes
sharing Environ.EnvironTypes =
Lambda.EnvironTypes =
Primitives.EnvironTypes =
EnvironPrint.EnvironTypes =
Encapsulate.EnvironTypes =
LambdaModule.EnvironTypes
sharing Mir_Cg.MirTypes =
MirPrint.MirTypes =
MirOptimiser.MirTypes =
Mach_Cg.MirTypes =
MirOptimiser.MirTypes
sharing Mach_Cg.MachSpec = MirOptimiser.MachSpec
sharing Basis.BasisTypes.Datatypes.Ident = LambdaPrint.LambdaTypes.Ident
sharing Encapsulate.ParserEnv.Map = Basis.BasisTypes.Datatypes.NewMap
sharing type Parser.Lexer.Options = Lambda.Options.options
sharing type Mach_Cg.Opcode = MachPrint.Opcode = Object_Output.Opcode
sharing type Mach_Cg.Module = Encapsulate.Module = Object_Output.Module
sharing type Parser.Absyn.Type = Basis.BasisTypes.Datatypes.Type =
Mir_Cg.LambdaTypes.Type
sharing type Parser.Absyn.Structure = Basis.BasisTypes.Datatypes.Structure
sharing type Parser.ParserBasis = Encapsulate.ParserEnv.pB
sharing type LambdaPrint.LambdaTypes.Primitive = Pervasives.pervasive
sharing type ModuleId.ModuleId = Project.ModuleId = Io.ModuleId = Object_Output.ModuleId
sharing type ModuleId.Location = Basis.BasisTypes.Datatypes.Ident.Location.T
sharing type Lambda.DebugInformation =
Mir_Cg.MirTypes.Debugger_Types.information
sharing type Basis.BasisTypes.Datatypes.Stamp = Stamp.Stamp
sharing type Basis.BasisTypes.Datatypes.StampMap = Stamp.Map.T
sharing type Object_Output.Project = Project.Project
) : TOPLEVEL =
struct
structure EnvironTypes = Environ.EnvironTypes
structure Assemblies = Mod_Rules.Assemblies
structure BasisTypes = Assemblies.Basistypes
structure Absyn = Parser.Absyn
structure LambdaTypes = LambdaPrint.LambdaTypes
structure Lexer = Parser.Lexer
structure Datatypes = Basis.BasisTypes.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Parser = Parser
structure Diagnostic = Diagnostic
structure Set = LambdaTypes.Set
structure MirTypes = Mir_Cg.MirTypes
structure Debugger_Types = MirTypes.Debugger_Types
structure Info = Lexer.Info
structure Options = LambdaOptimiser.Options
structure Token = Lexer.Token
structure FileSys = OS.FileSys
type TypeBasis = BasisTypes.Basis
type ParserBasis = Parser.ParserBasis
type ModuleId = ModuleId.ModuleId
type Project = Project.Project
val do_lambda_opt = ref true
val print_timings = ref false
val do_diagnostic = true
val _ = Diagnostic.set 0
fun diagnostic_output level =
if do_diagnostic then Diagnostic.output level else fn f => ()
val do_check_bindings = ref false
val error_output_level = ref Info.ADVICE
datatype compiler_basis =
CB of (Parser.ParserBasis * BasisTypes.Basis * EnvironTypes.Top_Env)
val empty_cb =
CB (Parser.empty_pB, Basis.empty_basis, Environ.empty_top_env)
val initial_cgb =
EnvironTypes.TOP_ENV
(Primitives.initial_env, Environ.empty_fun_env)
fun augment (CB (p, t, c), CB (p', t', c')) =
CB (Parser.augment_pB (p, p'),
Basis.basis_circle_plus_basis (t, t'),
Environ.augment_top_env (c, c'))
val after_builtin_cgb =
EnvironTypes.TOP_ENV
(Primitives.env_after_builtin, Environ.empty_fun_env)
val non_ml_defineable_cgb =
EnvironTypes.TOP_ENV
(Primitives.env_for_not_ml_definable_builtins, Environ.empty_fun_env)
val initial_cgb_for_builtin_library =
EnvironTypes.TOP_ENV
(Primitives.initial_env_for_builtin_library, Environ.empty_fun_env)
val initial_cb_for_builtin_library =
CB (Parser.initial_pB_for_builtin_library,
Basis.initial_basis_for_builtin_library,
initial_cgb_for_builtin_library)
val initial_cgb_for_normal_file =
CB (Parser.initial_pB,
Basis.initial_basis,
non_ml_defineable_cgb)
val BasisTypes.BASIS(_, _, _, _, initial_env_for_normal_file) = Basis.initial_basis
val initial_compiler_basis = initial_cgb_for_normal_file
val empty_env =
CB(Parser.empty_pB, Basis.empty_basis, Environ.empty_top_env)
val empty_string_map = NewMap.empty (op < : string * string -> bool, op =)
val empty_debug_info = Debugger_Types.empty_information
fun diagnostic (level, output_function) =
diagnostic_output level
(fn verbosity => "TopLevel " :: (output_function verbosity))
fun diagnose_simple str = diagnostic_output 1 (fn i => [str])
fun augment_cb (CB(p, t, c), CB(p', t', c')) =
CB (Parser.augment_pB (p, p'),
Basis.basis_circle_plus_basis(t, t'),
Environ.augment_top_env(c, c'))
fun error_wrap filename error_info =
Info.wrap error_info
(Info.FATAL, Info.RECOVERABLE, !error_output_level,
Info.Location.FILE filename)
fun do_subrequires
options
(first, project, require_table, _, _, nil) =
(project, require_table)
| do_subrequires
options
(first, project, require_table, pervasive,
location,
{mod_name = name, time} :: cons) =
let
val _ =
diagnostic (3,
fn _ => ["do_subrequires of ", name, ", pervasive = ",
if pervasive then "true\n" else "false\n"])
val is_pervasive_file = pervasive orelse first
val module_id =
ModuleId.from_mo_string (name, location)
val module_name_string = OS.Path.mkCanonical(ModuleId.string module_id)
in
case NewMap.tryApply'(require_table, module_name_string) of
SOME _ =>
do_subrequires options
(false, project, require_table, pervasive, location, cons)
| _ =>
let
val (consistency, stamps) =
case Project.get_object_info (project, module_id)
of SOME
{stamps,
consistency = Project.DEPEND_LIST cons, ...} =>
(cons, stamps)
| NONE =>
Crash.impossible
("No object info for `" ^ module_name_string ^ "'")
val (project, require_table) =
do_subrequires options
(true, project, require_table, is_pervasive_file,
location,
consistency)
val stamp_count = Stamp.read_counter ()
val _ = Stamp.reset_counter (stamp_count + stamps)
val module_name_string =
ModuleId.string
(Project.get_name (project, module_id))
val req_info = (module_name_string, stamp_count, stamps)
in
do_subrequires
options
(false, project,
NewMap.define(require_table, module_name_string, req_info),
pervasive, location, cons)
end
end
fun compile_require
error_info
(module_id, project, pervasive, location, require_table,
counters as stamps, debug_info) =
Timer.xtime
("Require",
!print_timings,
fn () =>
let
val root = ModuleId.string module_id
val _ = diagnostic (2, fn _ => ["requireDec ", root])
val (mo_str, mo_stamp) =
case Project.get_object_info (project, module_id) of
SOME {file, time_stamp, ...} => (file, time_stamp)
| NONE =>
Crash.impossible
("Required object file `" ^ root ^ "' in project "
^ (Project.get_project_name project) ^ " not found")
val _ = diagnostic (2, fn _ => ["found mo: ", mo_str])
val {parser_env, type_env=t_env, lambda_env,
mod_name, consistency, stamps, ...} =
Encapsulate.input_all mo_str
handle Encapsulate.BadInput message =>
Info.error' error_info (Info.FATAL, Info.Location.UNKNOWN, message)
fun error_wrap error_info =
Info.wrap error_info
(Info.FATAL, Info.RECOVERABLE, !error_output_level,
location)
val (project, require_table) =
error_wrap
error_info
do_subrequires
(true, project, require_table, pervasive, location, consistency)
val stamp_count = Stamp.read_counter ()
val req_info = (mod_name, stamp_count, stamps)
val mod_name = OS.Path.mkCanonical mod_name;
val require_table =
case NewMap.tryApply' (require_table, mod_name) of
SOME _ => require_table
| _ =>
NewMap.define(require_table, mod_name, req_info)
val (parser_env, lambda_env, t, _) =
Encapsulate.decode_all
{parser_env=parser_env,
lambda_env=lambda_env,
type_env=t_env,
file_name=mod_name,
sub_modules=require_table,
decode_debug_information=false,
pervasive_env=initial_env_for_normal_file}
val _ = Stamp.reset_counter (stamp_count + stamps)
val (top_env, decls) =
LambdaModule.unpack
(lambda_env,
LambdaTypes.APP
(LambdaTypes.BUILTIN Pervasives.LOAD_STRING,
([LambdaTypes.SCON (Ident.STRING mod_name, NONE)],[]),
NONE))
in
(project, require_table,
[{mod_name = mod_name,
time = mo_stamp}],
decls, CB(parser_env, t, top_env))
end)
fun compile_dependents error_info ([],
project, location, pervasive, require_table, requires,
decls, req_cb,_,_) =
(project, require_table, requires, decls, req_cb)
| compile_dependents error_info (m::t,
project, location, pervasive, require_table, requires,
decls, req_cb, counters, debug_info) =
let val (project, require_table, requires',
decls', req_cb') =
compile_require error_info
(m, project, pervasive,
location,
require_table, counters, debug_info)
in compile_dependents error_info (t,
project, location, pervasive, require_table,
requires @ requires',
decls @ decls', augment_cb(req_cb, req_cb'), counters, debug_info)
end
fun compile_program (error_info,
Options.OPTIONS({listing_options =
Options.LISTINGOPTIONS listing_options,
print_options,extension_options,
compat_options,
compiler_options=Options.COMPILEROPTIONS
{generate_debug_info,debug_variables,
generate_moduler,
intercept,
opt_leaf_fns, opt_handlers,
opt_tail_calls,opt_self_calls,
local_functions,
print_messages,
mips_r4000,
sparc_v7,...}}),
project, module_id, ts,
initial, initial_require_table, initial_requires, initial_decls,
pervasive) =
let
val mod_path = ModuleId.path module_id
val Options.COMPATOPTIONS{old_definition,...} = compat_options
val _ =
diagnostic (2,
fn _ => ["compile_program called with mod_path = `",
ModuleId.path_string mod_path, "'\n"])
val options =
Options.OPTIONS({listing_options = Options.LISTINGOPTIONS listing_options,
print_options = print_options,
extension_options = extension_options,
compat_options = compat_options,
compiler_options=Options.COMPILEROPTIONS
{generate_debug_info = generate_debug_info,
debug_variables =
debug_variables orelse generate_moduler,
generate_moduler = false,
intercept = false,
interrupt = false,
opt_handlers = opt_handlers,
opt_leaf_fns = opt_leaf_fns,
opt_tail_calls = opt_tail_calls,
opt_self_calls = opt_self_calls,
local_functions = local_functions,
print_messages = print_messages,
mips_r4000 = mips_r4000,
sparc_v7 = sparc_v7}})
val filename = Lexer.associated_filename ts
fun error_wrap error_info =
Info.wrap error_info
(Info.FATAL, Info.RECOVERABLE, !error_output_level,
Info.Location.FILE filename)
fun compile_topdec
(arg as (project, pervasive, mod_path, stamp_info,
cb1 as CB(p, t, c), cb2 as CB(p', t', c'), parse, eof,
counters as stamps, debug_info, had_topdec)) =
let
val parse_env = Parser.augment_pB(p, p')
val (topdec, p'') =
Timer.xtime(
"Parsing",
!print_timings,
(fn () => error_wrap error_info parse parse_env)
)
val _ = diagnose_simple "Parsing complete"
in
(case topdec of
Absyn.REQUIREtopdec (root, location) =>
if had_topdec then
Info.error'
error_info
(Info.FATAL, location, concat ["Too late for require statement"])
else
(project, stamp_info, [],
empty_cb, counters, debug_info, false)
| Absyn.STRDECtopdec(Absyn.SEQUENCEstrdec [], _) =>
(if eof() then
let
val stamp_count = Stamp.read_counter ()
val stamp_info = case stamp_info of
NONE => SOME stamp_count
|_ => stamp_info
in
(project, stamp_info, [],
empty_cb, counters, debug_info, true)
end
else
compile_topdec
(project, pervasive, mod_path, stamp_info,
cb1, cb2, parse, eof, counters,debug_info, true))
| _ =>
let
val CB(p, t, c) = augment_cb(cb1, cb2)
val stamp_count = Stamp.read_counter ()
val stamp_info = case stamp_info of
NONE => SOME stamp_count
|_ => stamp_info
val t' = Timer.xtime
("Type-checking ", !print_timings,
fn () => error_wrap
error_info
Mod_Rules.check_topdec
(options,true, topdec,t, Mod_Rules.BASIS t)
)
val _ = Basis.reduce_chains t'
val _ =
if (#show_absyn listing_options) then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The abstract syntax\n"));
Info.listing_fn
error_info
(3, fn stream =>
TextIO.output(stream,
TopdecPrint.topdec_to_string options topdec));
Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "\n"))
)
else ()
val _ = diagnose_simple"Typechecking complete"
val stamp_count = Stamp.read_counter () - stamp_count
val (c', _, declarations,debug_info') =
Timer.xtime (
"Lambda translation",
! print_timings,
fn () => error_wrap
error_info
Lambda.trans_top_dec
(options, topdec, c, Environ.empty_denv,
debug_info,
Basis.basis_circle_plus_basis (t, t'), true)
)
val _ = diagnose_simple"Lambda translation complete"
in
(project, stamp_info,
declarations, CB(p'',t',c'),
stamps + stamp_count,
debug_info', true)
end)
end
fun compile_topdecs'
(project, mod_path, stamp_info, code,
initial_cb, compiled_cb, parse, eof, counters,
debug_info, had_topdec) =
let
val (project, stamp_info,
declarations, comp_cb,
counters, debug_info, had_topdec) =
compile_topdec
(project, pervasive, mod_path, stamp_info,
initial_cb, compiled_cb, parse, eof, counters, debug_info,
had_topdec)
val compiled_cb' = augment_cb(compiled_cb, comp_cb)
val code' = code @ declarations
in
if eof () then
(project, stamp_info, code',
compiled_cb', counters,debug_info)
else
compile_topdecs'
(project, mod_path, stamp_info, code',
initial_cb, compiled_cb', parse, eof,
counters, debug_info, had_topdec)
end
fun eof () = Lexer.eof ts
val initial_counters = 0
fun parse error_info pb = Parser.parse_topdec error_info (options,ts, pb)
val (project, stamp_info, require_table, reqs_list, decls_list,
CB(pbasis, basis, top_env), counters, debug_info) =
if eof () then
let
val stamp_count = Stamp.read_counter ()
in
print "_toplevel: eof\n";
(project, SOME stamp_count,
empty_string_map, [],
[], empty_cb, initial_counters,
empty_debug_info)
end
else
let val dependents =
Project.get_requires(project, module_id)
val dependents =
if pervasive
then dependents
else
dependents
val (project, require_table, requires,
declarations, req_cb) =
compile_dependents error_info (dependents,
project,
Info.Location.FILE filename,
true, initial_require_table,
initial_requires,initial_decls,initial,
initial_counters, empty_debug_info)
val (project, stamp_info, code',
compiled_cb', counters,debug_info) =
compile_topdecs'
(project, mod_path, NONE,
declarations, req_cb,
empty_cb, parse, eof, initial_counters,
empty_debug_info, false)
in
(project, stamp_info, require_table, requires, code',
compiled_cb', counters,debug_info)
end
val (top_env'', lambda_exp') =
LambdaModule.pack (top_env, decls_list)
in
(project, stamp_info, require_table, reqs_list, lambda_exp',
CB (pbasis, basis, top_env''), counters,debug_info)
end
fun do_input (error_info,
options as Options.OPTIONS
{listing_options = Options.LISTINGOPTIONS listing_options,
print_options,
compiler_options=
Options.COMPILEROPTIONS{generate_debug_info,
debug_variables,
generate_moduler,...},
...},
project, module_id, input_fn, filename, pervasive,
{ require_table = initial_require_table,
requires = initial_requires,
declarations = initial_decls,
compiler_basis = initial_cb,
lvar_count = initial_lvar,
stamp_count = initial_stamp_count}) =
let
val module_str = ModuleId.string module_id
val _ =
diagnostic (2,
fn _ => ["do_input called on `", ModuleId.string module_id, "'\n"])
val builtin = pervasive andalso
ModuleId.eq(module_id, Io.builtin_library_id)
fun error_wrap error_info =
Info.wrap
error_info
(Info.FATAL, Info.RECOVERABLE, !error_output_level,
Info.Location.FILE module_str)
val mod_path = ModuleId.path module_id
val _ = LambdaTypes.reset_counter initial_lvar
val ts =
let
val stream_name =
if pervasive then "<Pervasive>"
else filename
in
Lexer.mkTokenStream (input_fn, stream_name)
end
val _ = Stamp.push_counter()
val _ = Stamp.reset_counter initial_stamp_count
in
let
val _ = diagnostic_output 3 (fn _ => ["Cleaning refs in precompiled basis"])
val _ = let val CB(_,basis,_) = initial_cb
in Encapsulate.clean_basis basis end
val _ = diagnostic_output 3 (fn _ => ["Cleaned refs in precompiled basis"])
val _ =
diagnostic_output 3
(fn _ => ["Stamp reset to " ^
Int.toString(Stamp.read_counter()) ^ "\n"])
val (project, stamp_info, require_table, requires, lambda_exp,
cb' as CB(parser_env, type_basis, top_env),
stamp_count,debug_information) =
compile_program (error_info, options, project,
module_id, ts,
initial_cb, initial_require_table,
initial_requires, initial_decls,
pervasive)
val stamps =
case stamp_info of
SOME stamps => stamps
| _ => Crash.impossible "Garbled stamp info"
val final_stamp_count = Stamp.read_counter ()
val require_list =
("", stamps,final_stamp_count - stamps)
:: NewMap.range require_table
val src_info = Project.get_source_info (project, module_id)
val src_time =
case src_info
of SOME (_, time) => time
| NONE =>
Crash.impossible
("Can't find source time for `" ^ filename ^ "'")
val _ =
if (#show_lambda listing_options) then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The unoptimised lambda code\n"));
Info.listing_fn error_info
(3, fn stream =>
LambdaPrint.output_lambda options (stream, lambda_exp)))
else ()
val opt_lambda_exp =
if (!do_lambda_opt) then
Timer.xtime("LambdaOptimiser",
!print_timings,
fn () => LambdaOptimiser.optimise options lambda_exp)
else
lambda_exp
val _ = diagnose_simple ("Lambda optimisation complete")
val _ =
if (#show_opt_lambda listing_options) then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The optimised lambda code\n"));
Info.listing_fn error_info
(3, fn stream =>
LambdaPrint.output_lambda options (stream, opt_lambda_exp)))
else ()
val top_env = Environ.simplify_topenv (top_env,opt_lambda_exp)
val _ =
if (#show_environ listing_options) then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The environment\n"));
Info.listing_fn error_info
(3, EnvironPrint.printtopenv print_options top_env);
Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "\n")))
else ()
val (the_mir_code,debugger_information) =
Timer.xtime("Mir_Cg",
! print_timings,
fn () => error_wrap
error_info
Mir_Cg.mir_cg
(options, opt_lambda_exp,
module_str, debug_information)
)
val _ = diagnose_simple"Mir translation complete"
val _ =
if #show_mir listing_options then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The unoptimised intermediate code\n"));
Info.listing_fn error_info (3, MirPrint.print_mir_code the_mir_code))
else
()
val make_debugging_code = generate_debug_info
val the_optimised_code =
Timer.xtime("MirOptimiser", ! print_timings,
fn () => MirOptimiser.optimise (the_mir_code,make_debugging_code));
val _ = diagnose_simple"Mir optimisation complete"
val _ =
if #show_opt_mir listing_options then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The optimised intermediate code\n"));
Info.listing_fn error_info (3, MirPrint.print_mir_code the_optimised_code))
else
()
val ((the_machine_code,debugger_information), code_list_list) =
let
val assign = MirOptimiser.machine_register_assignments
in
Timer.xtime("Mach_Cg",
! print_timings,
fn () => error_wrap
error_info
Mach_Cg.mach_cg
(options, the_optimised_code,
(#gc assign, #non_gc assign, #fp assign),
debugger_information)
)
end
val _ = diagnose_simple"Machine code translation complete"
val _ =
if #show_mach listing_options then
(Info.listing_fn
error_info
(3, fn stream => TextIO.output(stream, "The final machine code\n"));
Info.listing_fn error_info (3, MachPrint.print_mach_code code_list_list))
else
()
val mo_name =
if pervasive then
Project.pervasiveObjectName module_id
else
Project.objectName
(error_info, Info.Location.FILE module_str)
(project, module_id)
val top_env =
if builtin then
if Primitives.check_builtin_env
{error_fn =
fn s =>
Info.error
error_info
(Info.RECOVERABLE, Info.Location.FILE module_str, s),
topenv = top_env}
then
(Print.print "*** Changing top level environment\n";
after_builtin_cgb)
else
Info.error'
error_info
(Info.FATAL, Info.Location.FILE module_str,
"Builtin library doesn't match pervasives")
else
top_env
val _ =
Timer.xtime
("Outputting", !print_timings,
fn () =>
(Encapsulate.output_file
(debug_variables orelse generate_moduler)
{filename = mo_name,
code = the_machine_code, parser_env = parser_env,
type_basis = type_basis,
debug_info =
if generate_debug_info then
debugger_information
else
empty_debug_info,
require_list = require_list,
lambda_env = top_env,
stamps = stamp_count,
mod_name = ModuleId.string module_id,
time_stamp = src_time,
consistency = requires}))
handle IO.Io _ =>
Info.error'
error_info
(Info.FATAL, Info.Location.FILE module_str,
"Can't create object file `" ^ mo_name ^ "'\n");
val new_mo_time = FileTime.modTime mo_name
val _ =
Project.set_object_info
(project, module_id,
SOME {file = mo_name,
file_time = new_mo_time,
time_stamp = src_time,
stamps = stamp_count,
consistency = Project.DEPEND_LIST requires})
in
Stamp.pop_counter();
project
end
handle exn =>
(Stamp.pop_counter();
raise exn)
end
fun compile_file''
error_info
(options as Options.OPTIONS
{compiler_options = Options.COMPILEROPTIONS
{print_messages, ...}, ...},
project, module_id, pervasive,
precompiled_context) =
Timer.xtime
("Compilation", !print_timings,
fn () =>
let
val source_info = Project.get_source_info (project, module_id)
val file_name =
case source_info
of SOME (file_name, _) => file_name
| NONE =>
Crash.impossible
("No source info while compiling `"
^ ModuleId.string module_id ^ "'")
val _ =
if print_messages then
Print.print ("Compiling " ^ file_name ^ "\n")
else ()
val instream =
TextIO.openIn file_name
handle IO.Io{name, function, cause} =>
Info.error'
error_info
(Info.FATAL, Info.Location.UNKNOWN,
"Io error in compile_file: " ^ name)
val module_str = ModuleId.string module_id
val result =
do_input (error_info, options, project, module_id,
fn _ => TextIO.inputN(instream, 4096), file_name, pervasive,
precompiled_context)
handle exn => (TextIO.closeIn instream; raise exn)
in
TextIO.closeIn instream;
result
end)
fun check_pervasive_objects_exist(error_info, location, project) =
let
val units = Project.list_units project
fun check_one_unit(string, module_id) =
case Project.get_object_info(project, module_id) of
NONE => Info.error
error_info
(Info.FATAL, location, "Missing pervasive object file `" ^ string ^ "'")
| _ => ()
in
Lists.iterate check_one_unit units
end
type precompiled_context =
{
require_table: (string, (string * int * int)) NewMap.map,
requires: {mod_name: string, time: MLWorks.Internal.Types.time} list,
declarations: LambdaTypes.binding list,
compiler_basis: compiler_basis,
lvar_count: int,
stamp_count: int
}
fun precompile error_info (project, is_pervasive, dependents)
: precompiled_context =
let val initial_cb =
if is_pervasive
then initial_cb_for_builtin_library
else initial_cgb_for_normal_file
val dependents' =
if is_pervasive
then dependents
else
let val pervasive_dependent =
(Project.initialize (error_info, Info.Location.UNKNOWN),
ModuleId.perv_from_require_string
(Io.pervasive_library_name, Info.Location.UNKNOWN))
in
pervasive_dependent :: dependents
end
fun precompile_dependents ([],
location, pervasive, require_table, requires,
decls, req_cb,_,_) =
let val final_count = Stamp.read_counter()
val final_lvar = LambdaTypes.read_counter()
val ctxt : precompiled_context =
{ require_table = require_table,
requires = requires,
declarations = decls,
compiler_basis = req_cb,
lvar_count = final_lvar,
stamp_count = final_count }
in Stamp.pop_counter();
ctxt
end
| precompile_dependents ((project,m)::t,
location, pervasive, require_table, requires,
decls, req_cb, counters, debug_info) =
let val (_, require_table, requires',
decls', req_cb') =
compile_require error_info
(m, project, pervasive,
location,
require_table, counters, debug_info)
in precompile_dependents (t,
location, pervasive, require_table,
requires @ requires',
decls @ decls', augment_cb(req_cb, req_cb'),
counters, debug_info)
end
in
case dependents of
[] => ()
| _ =>
( print "Precompiling ";
app (fn (_,m) => print(ModuleId.string m ^ " ")) dependents;
print "\n" );
Stamp.push_counter();
Stamp.reset_counter Basis.pervasive_stamp_count;
LambdaTypes.init_LVar ();
precompile_dependents (
dependents',
Info.Location.UNKNOWN,
true, empty_string_map,
[],[],initial_cb,
0, empty_debug_info)
end
fun get_precompiled_context (project, pervasive, error_info) =
let
val precompiled_context = ref NONE
in
fn () =>
case !precompiled_context of
NONE =>
let val dependents =
Project.get_external_requires project
val context =
precompile error_info (project, pervasive, dependents)
in precompiled_context := SOME context; context end
| SOME context => context
end
fun compile_file error_info options filenames =
let
val location = Info.Location.FILE "<batch compiler:compile-file>"
val modules = map OS.Path.file filenames
val init_project = Project.initialize (error_info, location)
val _ = check_pervasive_objects_exist(error_info, location, init_project)
val precompiled_context =
get_precompiled_context(init_project, false, error_info)
fun compile_one ((project, status_map), module) =
let
fun do_compile_one mod_id =
let
val (project, status_map) =
Project.read_object_dependencies
(Info.make_default_options (), location)
(project, mod_id, status_map);
val project =
compile_file''
error_info
(options, project, mod_id, false,
precompiled_context());
in
(project, status_map)
end
in
do_compile_one (ModuleId.from_host (module, location))
end
in
ignore(Lists.reducel
compile_one
((init_project, Project.empty_map), modules));
()
end
fun recompile_file error_info options filenames =
let
val location = Info.Location.FILE "<batch compiler:recompile-file>"
val modules = map OS.Path.file filenames
val init_project = Project.initialize (error_info, location)
val _ = check_pervasive_objects_exist(error_info, location, init_project)
val precompiled_context =
get_precompiled_context(init_project, false, error_info)
fun recompile_one
((project, depend_map, compile_map), module) =
let
fun compile_one (proj, m) =
compile_file''
error_info
(options, proj, m, false,
precompiled_context());
fun do_recompile_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(Info.make_default_options (), location)
(project, mod_id, depend_map);
val (out_of_date, compile_map) =
Project.check_compiled'
(Info.make_default_options (), location)
(project, mod_id)
([], compile_map)
val project =
Lists.reducel compile_one (project, out_of_date);
val status_map =
Lists.reducel Project.mark_compiled (compile_map, out_of_date)
in
(project, depend_map, compile_map)
end
in
do_recompile_one (ModuleId.from_host (module, location))
end
in
ignore(Lists.reducel
recompile_one
((init_project, Project.empty_map, Project.visited_pervasives),
modules));
print"Up to date\n"
end
fun recompile_pervasive error_info options =
let
val location = Info.Location.FILE "<batch compiler:compile-pervasive>"
val project = Project.initialize (error_info, location)
val precompiled_context =
get_precompiled_context(project, true, error_info)()
fun context_for module_id =
if ModuleId.eq(module_id, Io.builtin_library_id)
then precompiled_context
else
let val {require_table, requires, declarations, compiler_basis,
lvar_count, stamp_count} = precompiled_context
in {require_table = require_table, requires = requires,
declarations = declarations,
compiler_basis = initial_cgb_for_normal_file,
lvar_count = lvar_count, stamp_count = stamp_count}
end
val out_of_date =
Project.check_perv_compiled
(error_info, location)
project
fun compile_one (proj, m) =
compile_file''
error_info
(options, proj, m, true, context_for m);
in
ignore(Lists.reducel compile_one (project, out_of_date));
print"Up to date\n"
end
fun check_dependencies error_info options filenames =
let
val location = Info.Location.FILE "<batch compiler:check-dependencies>"
val modules = map OS.Path.file filenames
val project = Project.initialize (error_info, location)
fun check_one
((project, out_of_date, depend_map, compile_map),
module) =
let
fun do_check_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(Info.make_default_options (), location)
(project, mod_id, depend_map)
val (out_of_date_now, compile_map) =
Project.check_compiled'
(Info.make_default_options (), location)
(project, mod_id)
(out_of_date, compile_map)
in
(project, out_of_date_now, depend_map, compile_map)
end
in
do_check_one (ModuleId.from_host (module, location))
end
val (_, out_of_date, _, _) =
Lists.reducel
check_one
((project, [], Project.empty_map, Project.visited_pervasives),
modules)
fun print_one m = print(ModuleId.string m ^ "\n")
in
case out_of_date
of [] =>
print"No files to recompile\n"
| _ =>
app print_one out_of_date
end
fun dump_objects error_info options filename =
let
val location = Info.Location.FILE "<batch compiler:dump objects>"
val initProject =
Project.fromFileInfo
(error_info, location)
(Project.initialize (error_info, location))
val project =
Project.map_dag (Project.update_dependencies (error_info, location))
initProject
val units = Project.list_units project
val targets =
( map (fn t => Lists.assoc(
OS.Path.base(OS.Path.mkCanonical t), units))
(Project.currentTargets project) )
handle _ => []
val units' = map #2 units
fun requires m =
Lists.filterp
(fn m' => Lists.exists (fn m'' => ModuleId.eq(m', m'')) units')
(Project.get_requires(project, m))
fun topsort targets =
let fun sort([], visited) = visited
| sort(x::xs, visited) =
sort(xs,
if Lists.exists (fn x' => ModuleId.eq(x, x')) visited
then visited
else x :: sort(requires x, visited))
in sort(targets, [])
end;
fun reverse l =
let fun rev([], r) = r
| rev(h::t, r) = rev(t, h::r)
in rev(l, [])
end
val units = reverse(topsort targets)
in
let val stream = TextIO.openOut filename
in app (fn m => TextIO.output(stream, ModuleId.string m ^ ".mo\n"))
units;
TextIO.closeOut stream
end
handle IO.Io _ =>
Info.error'
error_info
(Info.FATAL, location,
"Can't create dump file `" ^ filename ^ "'\n")
end
fun list_objects error_info options filenames =
let
val location = Info.Location.FILE "<batch compiler:list-objects>"
val modules = map OS.Path.file filenames
val project = Project.initialize (error_info, location)
fun check_one
((project, out_of_date, depend_map),
module) =
let
fun do_check_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(Info.make_default_options (), location)
(project, mod_id, depend_map)
val out_of_date_now =
Project.allObjects
(Info.make_default_options (), location)
(project, mod_id)
in
(project, out_of_date_now @ out_of_date, depend_map)
end
in
do_check_one (ModuleId.from_host (module, location))
end
val (_, out_of_date, _) =
Lists.reducel
check_one
((project, [], Project.empty_map),
modules)
fun print_one m = print(ModuleId.string m ^ "\n")
in
case out_of_date
of [] =>
print"No files to recompile\n"
| _ =>
app print_one out_of_date
end
fun build_targets_for (error_info, options, location) project =
let
val initProject =
Project.update_dependencies (error_info, location) project
val precompiled_context =
get_precompiled_context(initProject, false, error_info)
fun recompile_one
((project, depend_map, compile_map), module) =
let
fun compile_one (proj, m) =
compile_file''
error_info
(options, proj, m, false,
precompiled_context());
fun do_recompile_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(Info.make_default_options (), location)
(project, mod_id, depend_map);
val (out_of_date, compile_map) =
Project.check_compiled'
(Info.make_default_options (), location)
(project, mod_id)
([], compile_map)
val project =
Lists.reducel compile_one (project, out_of_date);
val status_map =
Lists.reducel Project.mark_compiled (compile_map, out_of_date)
in
(project, depend_map, compile_map)
end
in
do_recompile_one (ModuleId.from_host (OS.Path.file module, location))
end
val project' =
#1(Lists.reducel
recompile_one
((initProject, Project.empty_map, Project.visited_pervasives),
Project.currentTargets project))
in
project'
end
fun build error_info options _ =
let
val location = Info.Location.FILE "<batch compiler:build>"
val initProject =
Project.fromFileInfo
(error_info, location)
(Project.initialize (error_info, location))
in
ignore(Project.map_dag
(build_targets_for (error_info, options, location)) initProject);
print"Up to date\n"
end
fun show_build_targets_for (error_info, options, location) project =
let
val initProject =
Project.update_dependencies (error_info, location) project
fun check_one
((project, out_of_date, depend_map, compile_map),
module) =
let
fun do_check_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(Info.make_default_options (), location)
(project, mod_id, depend_map)
val (out_of_date_now, compile_map) =
Project.check_compiled'
(Info.make_default_options (), location)
(project, mod_id)
(out_of_date, compile_map)
in
(project, out_of_date_now, depend_map, compile_map)
end
in
do_check_one (ModuleId.from_host (OS.Path.file module, location))
end
val (project', out_of_date, _, _) =
Lists.reducel
check_one
((initProject, [], Project.empty_map, Project.visited_pervasives),
Project.currentTargets initProject)
val name = Project.get_project_name project
fun print_one m = print(ModuleId.string m ^ "\n")
in
case out_of_date
of [] =>
print ("No files to recompile for " ^ name ^ "\n")
| _ =>
( print ("Files to recompile for " ^ name ^ ":\n");
app print_one out_of_date );
project'
end
fun show_build error_info options _ =
let
val location = Info.Location.FILE "<batch compiler:show_build>"
val initProject =
Project.fromFileInfo
(error_info, location)
(Project.initialize (error_info, location))
in
ignore(
Project.map_dag
(show_build_targets_for (error_info, options, location)) initProject);
()
end
fun compile_file'
error_info
(options, project, []) = project
| compile_file'
error_info
(options, project, module_ids) =
let val precompiled_context =
get_precompiled_context(project, false, error_info)
fun compile_one (proj, m) =
compile_file''
error_info
(options, proj, m, false, precompiled_context())
val module_ids' =
Lists.filterp
(fn m => Project.module_id_in_project (project, m))
module_ids
in
Lists.reducel compile_one (project, module_ids')
end
end
;
