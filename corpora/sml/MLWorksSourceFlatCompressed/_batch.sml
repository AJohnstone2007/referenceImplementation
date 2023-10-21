require "../basis/__int";
require "../basis/__string";
require "../basis/__text_io";
require "../basis/__io";
require "../system/__os";
require "../utils/mlworks_exit";
require "encapsulate";
require "version";
require "license";
require "user_options";
require "mlworks_io";
require "toplevel";
require "proj_file";
require "batch";
functor Batch (
structure Io : MLWORKS_IO
structure User_Options : USER_OPTIONS
structure ProjFile : PROJ_FILE
structure TopLevel : TOPLEVEL
structure Encapsulate : ENCAPSULATE
structure Version : VERSION
structure License : LICENSE
structure Exit : MLWORKS_EXIT
sharing TopLevel.Options = User_Options.Options
sharing type TopLevel.Info.Location.T = Io.Location = ProjFile.location
sharing type TopLevel.Info.options = ProjFile.error_info
) : BATCH =
struct
structure Exit = Exit
structure Options = TopLevel.Options
structure Info = TopLevel.Info
val verbose = ref false
datatype compiler =
NO_COMPILER
| BATCH of Info.options -> Options.options -> string list -> unit
| PROJECT of Info.options -> Options.options -> unit -> unit
fun out s = TextIO.output (TextIO.stdErr, s)
fun info message =
if !verbose then
(app out message;
out "\n")
else ()
fun arg_exists (test_arg, []) = false
| arg_exists (test_arg, (arg::args)) =
let
val sz = size arg
in
if sz = 0 orelse String.sub(arg, 0) <> #"-" then false
else
if substring (arg, 1, sz-1) = test_arg then true
else
arg_exists (test_arg, args)
end
fun updateContextOptionsFromProjFile
(User_Options.USER_CONTEXT_OPTIONS (context_opts, _)) =
let
val (_, modeDetails, currentMode) = ProjFile.getModes ()
in
case currentMode of
NONE => ()
| SOME name =>
case ProjFile.getModeDetails (name, modeDetails) of
r =>
(#generate_interruptable_code context_opts :=
!(#generate_interruptable_code r);
#generate_interceptable_code context_opts :=
!(#generate_interceptable_code r);
#generate_debug_info context_opts :=
!(#generate_debug_info r);
#generate_variable_debug_info context_opts :=
!(#generate_variable_debug_info r);
#optimize_leaf_fns context_opts :=
!(#optimize_leaf_fns r);
#optimize_tail_calls context_opts :=
!(#optimize_tail_calls r);
#optimize_self_tail_calls context_opts :=
!(#optimize_self_tail_calls r);
#mips_r4000 context_opts :=
!(#mips_r4000 r);
#sparc_v7 context_opts :=
!(#sparc_v7 r))
end
exception Error of string list
exception Option of string
fun usage () =
out
"Usage:   mlbatch [options...]\n\
       \Options:\n\
       \  -verbose\n\
       \          Enable verbose mode.  When in verbose mode the system will\n\
       \          give messages indicating the various options interpreted\n\
       \          during argument processing, otherwise it will not.\n\
       \  -silent\n\
       \          Disable verbose mode.\n\
       \  -project file\n\
       \          Specifies the project file to use.  This must precede all the\n\
       \          following options on the command line.\n\
       \  -mode name\n\
       \          Specifies which mode to use.  Reports an error if the mode does\n\
       \          exist in the specified project file.  This option overrides the\n\
       \          setting of a mode as current in the project file.  If no mode is\n\
       \          specified on the command line, the mode specified as current in\n\
       \          the project file is used.\n\
       \  -target name\n\
       \          Specifies a target to recompile.\n\
       \          Multiple targets may be specified by repeated use of this option.\n\
       \          If any options are specified on the command line, the setting of\n\
       \          targets as current in the project file are ignored.  If no\n\
       \          targets are specified on the command line, those specified as\n\
       \          current in the project file are recompiled.\n\
       \  -configuration name\n\
       \          If the project includes configurations, this specifies which one\n\
       \          to use.  This option overrides the setting of a configuration as\n\
       \          current in the project file.\n\
       \          If a project includes configurations, and none is specified on the\n\
       \          command line, the configuration specified as current in the\n\
       \          project file is used.\n\
       \  -object-path dir\n\
       \          Specifies the directory in which to find/put the object files.\n\
       \          The project must have been specified earlier on the command line.\n\
       \          This overrides the specification in the project file.  If the\n\
       \          directory includes the string '%S', this is expanded to the\n\
       \          directory containing the source file.\n\
       \  -build\n\
       \          Builds the specified project using the current targets in the\n\
       \          current configuration (if any) with the current mode.\n\
       \  -show-build\n\
       \          Show the files that would be recompiled by the -build option.\n\
       \  -list-objects\n\
       \          Show all the files that are used in the building of the current\n\
       \          targets.\n\
       \  -dump-objects filename\n\
       \          Write a file containing all the object files needed to build the\n\
       \          current targets in dependency order.\n\
       \  -source-path path\n\ 
       \          Sets the path that mlbatch searches for source files.  'path' is\n\
       \          a colon-separated list of directories.  For example,\n\
       \          ~/ml/src:/usr/sml/src.  The default path contains the current\n\
       \          directory and the directory in which MLWorks was installed.\n\
       \  -compile files\n\
       \          For each named file, this option recursively compiles any files\n\
       \          on which that file depends that are out of date with respect to\n\
       \          their source.  No default.\n\
       \  -check-dependencies files\n\
       \          Process the source files in check-dependencies mode.  Inclusion of\n\
       \          the .sml suffix in source file names is optional.  Produces a list\n\
       \          of the source files that would be compiled if you submitted the\n\
       \          same list of files to the -compile option.\n\
       \  -old-definition [on|off]\n\
       \          When set to on, use the semantics of the old version of SML,\n\
       \          dating back to 1990.  Default set to off.\n\
       \  -debug [on|off]\n\
       \          Controls whether MLWorks generates debugging information.\n\
       \          Default off.\n\
       \  -debug-variables [on|off]\n\
       \          Controls whether MLWorks generates variable debugging information.\n\
       \          Default off.\n\
       \  -trace-profile [on|off]\n\
       \          Controls whether MLWorks generates code that can be traced\n\
       \          or profiled with the call counter.\n\
       \          Default off.\n\
       \  -interrupt [on|off]\n\
       \          Controls whether interruptable code is enabled.  Default off.\n\
       \  -mips-r4000-and-later [on|off]\n\
       \          Compile code for MIPS R4000 and later versions.\n\
       \          Default on.\n\
       \  -sparc-v7 [on|off]\n\
       \          Compile code for Version 7 of the SPARC architecture.\n\
       \          Default off.\n"
val compiler = ref NO_COMPILER
val project = ref false
val targets = ref []: string list ref
val dump_objects_filename = ref NONE : string option ref
fun obey_options
(tool_options as User_Options.USER_TOOL_OPTIONS(tool_opts, _),
context_options as User_Options.USER_CONTEXT_OPTIONS (context_opts, _),
args) =
let
fun aux [] = (tool_options, context_options, [], !compiler, rev(!targets))
| aux (arg::args) =
let
val sz = size arg
in
if sz = 0 orelse String.sub(arg, 0) <> #"-" then
(tool_options, context_options, (arg::args), !compiler, !targets)
else
(case (substring (arg, 1, sz-1), args) of
("verbose", rest) =>
(verbose := true;
info ["Verbose mode."];
aux rest)
| ("print-timings", rest) =>
(TopLevel.print_timings := true;
info ["Printing major timings."];
aux rest)
| ("silent", rest) =>
(info ["Silent mode."];
verbose := false;
aux rest)
| ("pervasive-dir", dir::rest) =>
((Io.set_pervasive_dir (dir, Info.Location.FILE "Command Line")
handle OS.SysErr _ =>
raise Error
["Invalid pervasive directory specification: ",
dir]);
info ["Pervasive library directory set to `",
Io.get_pervasive_dir (), "'."]
handle Io.NotSet _ =>
raise Error ["Pervasive directory not set."];
aux rest)
| ("object-path", dir::rest) =>
((Io.set_object_path (dir, Info.Location.FILE "Command Line")
handle OS.SysErr _ =>
raise Error
["Invalid object path specification: ",
dir]);
info ["Object path set to `", Io.get_object_path(), "'."]
handle Io.NotSet _ =>
raise Error ["Object path not set."];
aux rest)
| ("source-path", path::rest) =>
((Io.set_source_path_from_string
(path, Info.Location.FILE "Command Line")
handle OS.SysErr _ =>
raise Error
["Invalid source path specification: ", path]);
info ["Source path set to `", path, "'."];
aux rest)
| ("float-precision", arg::rest) =>
(case Int.fromString arg of
SOME n =>
(info ["Precision of floating point printing set to ",
arg, "."];
(case tool_opts of
{float_precision, ...} =>
float_precision := n);
aux rest)
| _ =>
raise Option "float_precision")
| ("opt-handlers", "on"::rest) =>
(info ["Optimised handlers enabled."];
(case context_opts of
{optimize_handlers, ...} =>
optimize_handlers := true);
aux rest)
| ("opt-handlers", "off"::rest) =>
(info ["Optimised handlers disabled."];
(case context_opts of
{optimize_handlers, ...} =>
optimize_handlers := false);
aux rest)
| ("local-functions", "on"::rest) =>
(info ["Local Functions enabled."];
(case context_opts of
{local_functions, ...} =>
local_functions := true);
aux rest)
| ("local-functions", "off"::rest) =>
(info ["Local Functions disabled."];
(case context_opts of
{local_functions, ...} =>
local_functions := false);
aux rest)
| ("debug", "on"::rest) =>
(info ["Debugging information enabled."];
(case context_opts of
{generate_debug_info, ...} =>
generate_debug_info := true);
aux rest)
| ("debug", "off"::rest) =>
(info ["Debugging information disabled."];
(case context_opts of
{generate_debug_info, ...} =>
generate_debug_info := false);
aux rest)
| ("debug-variables", "on"::rest) =>
(info ["Variable Debugging information enabled."];
(case context_opts of
{generate_variable_debug_info, ...} =>
generate_variable_debug_info := true);
aux rest)
| ("debug-variables", "off"::rest) =>
(info ["Variable Debugging information disabled."];
(case context_opts of
{generate_variable_debug_info, ...} =>
generate_variable_debug_info := false);
aux rest)
| ("generate-moduler", "on"::rest) =>
(info ["Module Debugging information enabled."];
(case context_opts of
{generate_moduler, ...} =>
generate_moduler := true);
aux rest)
| ("generate_moduler", "off"::rest) =>
(info ["Module Debugging information disabled."];
(case context_opts of
{generate_moduler, ...} =>
generate_moduler := false);
aux rest)
| ("interrupt", "on"::rest) =>
(info ["Interruptable code enabled."];
(case context_opts of
{generate_interruptable_code, ...} =>
generate_interruptable_code := true);
aux rest)
| ("interrupt", "off"::rest) =>
(info ["Interruptable code disabled."];
(case context_opts of
{generate_interruptable_code, ...} =>
generate_interruptable_code := false);
aux rest)
| ("trace-profile", "on"::rest) =>
(info ["Interception enabled."];
(case context_opts of
{generate_interceptable_code, ...} =>
generate_interceptable_code := true);
aux rest)
| ("trace-profile", "off"::rest) =>
(info ["Interception disabled."];
(case context_opts of
{generate_interceptable_code, ...} =>
generate_interceptable_code := false);
aux rest)
| ("mips-r4000-and-later", "on"::rest) =>
(info ["Compiling for MIPS R4000 and later"];
(#mips_r4000 context_opts) := true;
aux rest)
| ("mips-r4000-and-later", "off"::rest) =>
(info ["Not compiling for MIPS R4000 and later"];
(#mips_r4000 context_opts) := false;
aux rest)
| ("sparc-v7", "on"::rest) =>
(info ["Compiling for SPARC Version 7"];
(#sparc_v7 context_opts) := true;
aux rest)
| ("sparc-v7", "off"::rest) =>
(info ["Not compiling for SPARC Version 7"];
(#sparc_v7 context_opts) := false;
aux rest)
| ("absyn", "on" :: rest) =>
(info ["abstract syntax tree printing enabled."];
(case tool_opts of
{show_absyn, ...} =>
show_absyn := true);
aux rest)
| ("absyn", "off" :: rest) =>
(info ["abstract syntax tree printing disabled."];
(case tool_opts of
{show_absyn, ...} =>
show_absyn := false);
aux rest)
| ("lambda", "on" :: rest) =>
(info ["lambda calculus printing enabled."];
(case tool_opts of
{show_lambda, ...} =>
show_lambda := true);
aux rest)
| ("lambda", "off" :: rest) =>
(info ["lambda calculus printing disabled."];
(case tool_opts of
{show_lambda, ...} =>
show_lambda := false);
aux rest)
| ("opt-lambda", "on" :: rest) =>
(info ["optimised lambda calculus printing enabled."];
(case tool_opts of
{show_opt_lambda, ...} =>
show_opt_lambda := true);
aux rest)
| ("opt-lambda", "off" :: rest) =>
(info ["optimised lambda calculus  printing disabled."];
(case tool_opts of
{show_opt_lambda, ...} =>
show_opt_lambda := false);
aux rest)
| ("environ", "on" :: rest) =>
(info ["lambda calculus environment printing enabled."];
(case tool_opts of
{show_environ, ...} =>
show_environ := true);
aux rest)
| ("environ", "off" :: rest) =>
(info ["lambda calculus environment printing disabled."];
(case tool_opts of
{show_environ, ...} =>
show_environ := false);
aux rest)
| ("mir", "on" :: rest) =>
(info ["machine independent representation printing enabled."];
(case tool_opts of
{show_mir, ...} =>
show_mir := true);
aux rest)
| ("mir", "off" :: rest) =>
(info ["machine independent representation printing disabled."];
(case tool_opts of
{show_mir, ...} =>
show_mir := false);
aux rest)
| ("opt-mir", "on" :: rest) =>
(info ["optimised machine independent representation printing enabled."];
(case tool_opts of
{show_opt_mir, ...} =>
show_opt_mir := true);
aux rest)
| ("opt-mir", "off" :: rest) =>
(info ["optimised machine independent representation printing disabled."];
(case tool_opts of
{show_opt_mir, ...} =>
show_opt_mir := false);
aux rest)
| ("machine", "on" :: rest) =>
(info ["machine code printing enabled."];
(case tool_opts of
{show_mach, ...} =>
show_mach := true);
aux rest)
| ("machine", "off" :: rest) =>
(info ["machine code printing disabled."];
(case tool_opts of
{show_mach, ...} =>
show_mach := false);
aux rest)
| ("old-definition", "on" :: rest) =>
(info ["Use semantics of old version of SML"];
(case context_opts of
{old_definition, ...} =>
old_definition := true);
aux rest)
| ("old-definition", "off" :: rest) =>
(info ["Use semantics of new version of SML"];
(case context_opts of
{old_definition, ...} =>
old_definition := false);
aux rest)
| ("nj-sig", "on" :: rest) =>
(info ["New Jersey signatures enabled."];
(case context_opts of
{nj_signatures, ...} =>
nj_signatures := true);
aux rest)
| ("nj-sig", "off" :: rest) =>
(info ["New Jersey signatures disabled."];
(case context_opts of
{nj_signatures, ...} =>
nj_signatures := false);
aux rest)
| ("project", file :: rest) =>
(info ["Reading Project file `", file, "'."];
ProjFile.open_proj file
handle ProjFile.InvalidProjectFile reason =>
raise Error
["Unable to read project file: ", reason, "."];
project := true;
aux rest)
| ("build", rest) =>
(compiler := PROJECT TopLevel.build;
info ["Building project."];
aux rest)
| ("show-build", rest) =>
(compiler := PROJECT TopLevel.show_build;
info ["Showing files to build project."];
aux rest)
| ("configuration", config :: rest) =>
(if !project then
ProjFile.setCurrentConfiguration
(Info.make_default_options (),
Info.Location.FILE "<batch compiler:options>")
(SOME config)
else
raise Error
["Configuration ", config,
" not set: no project specified."];
info ["Setting configuration to ", config, "."];
aux rest)
| ("mode", mode :: rest) =>
(if !project then
(ProjFile.setCurrentMode
(Info.make_default_options (),
Info.Location.FILE "<batch compiler:options>")
mode;
updateContextOptionsFromProjFile (context_options))
else
raise Error
["Mode ", mode, " not set: no project specified."];
info ["Setting mode to ", mode, "."];
aux rest)
| ("target", target :: rest) =>
(if !project then
targets := target :: !targets
else
raise Error
["Target ", target, " not set: no project specified."];
info ["Adding ", target, " to list of targets."];
aux rest)
| ("compile-file", rest) =>
(compiler := BATCH TopLevel.compile_file;
info ["Compile single files mode."];
aux rest)
| ("check-dependencies", rest) =>
(compiler := BATCH TopLevel.check_dependencies;
info ["Checking dependencies."];
aux rest)
| ("list-objects", rest) =>
(compiler := BATCH TopLevel.list_objects;
info ["Listing object files."];
aux rest)
| ("dump-objects", filename::rest) =>
(dump_objects_filename := SOME filename;
aux rest)
| ("compile", rest) =>
(compiler := BATCH TopLevel.recompile_file;
info ["Compile mode."];
aux rest)
| ("compile-pervasive", rest) =>
(info ["Compiling pervasive library"];
TopLevel.recompile_pervasive
(Info.make_default_options ())
(User_Options.new_options (tool_options, context_options));
aux rest)
| ("no-banner", rest) =>
aux rest
| ("save", filename::rest) =>
(info ["Saving image to `", filename, "'."];
ignore(MLWorks.Internal.save
(filename, fn () =>
let
val x = obey(MLWorks.arguments())
in
Exit.exit x
end));
aux rest)
| (other, rest) =>
raise Option other)
end
in
aux args
end
and obey (args : string list) : Exit.status =
(
let
val license_status = License.license License.ttyComplain
val default_to_free : unit -> unit =
MLWorks.Internal.Runtime.environment "license set edition"
in
case license_status of
SOME false => default_to_free ()
| _ => ()
end;
if not (arg_exists("no-banner", args)) then
info [Version.versionString ()]
else
();
Io.set_pervasive_dir_from_env (Info.Location.FILE "<Initialisation>");
let
val default_batch_options =
Options.OPTIONS
{listing_options = Options.LISTINGOPTIONS {show_absyn = false,
show_lambda = false,
show_match = false,
show_opt_lambda = false,
show_environ = false,
show_mir = false,
show_opt_mir = false,
show_mach = false
},
compiler_options = Options.COMPILEROPTIONS {generate_debug_info = false,
debug_variables = false,
generate_moduler = false,
intercept = false,
interrupt = false,
opt_handlers = false,
opt_leaf_fns = true,
opt_tail_calls = true,
opt_self_calls = true,
local_functions = true,
print_messages = true,
mips_r4000 = true,
sparc_v7 = false},
print_options = Options.PRINTOPTIONS {maximum_seq_size = 10,
maximum_string_size = 255,
maximum_ref_depth = 3,
maximum_str_depth = 2,
maximum_sig_depth = 1,
maximum_depth = 7,
float_precision = 10,
print_fn_details = false,
print_exn_details = true,
show_eq_info = false,
show_id_class = false},
compat_options = Options.default_compat_options,
extension_options = Options.default_extension_options}
val tool_options =
User_Options.make_user_tool_options
default_batch_options
val context_options =
User_Options.make_user_context_options
default_batch_options
val (tool_options, context_options, files, compiler, targets) =
obey_options (tool_options, context_options, args)
val _ =
if targets <> [] then
(out "Setting targets: ";
case targets of
[target] => out (target ^ "\n")
| targets =>
(out "\n"; app (fn s => (out ("  " ^ s ^ "\n"))) targets);
ProjFile.setCurrentTargets
(Info.make_default_options (),
Info.Location.FILE "<batch compiler:options>")
targets)
else
()
val new_options =
User_Options.new_options (tool_options, context_options)
in
case files
of [] =>
(case compiler
of PROJECT f =>
f (Info.make_default_options ()) new_options ()
| BATCH _ =>
info ["No files to compile"]
| _ => ())
| _ =>
(case compiler
of BATCH f =>
f (Info.make_default_options ()) new_options files
| _ =>
info ["Excess arguments ignored"]);
case !dump_objects_filename
of NONE => ()
| SOME filename =>
if !project
then
( info ["Dumping dependency list to `", filename, "'."];
TopLevel.dump_objects (Info.make_default_options ())
new_options filename )
else
raise Error ["No project specified."];
Exit.success
end)
handle Option s=>
(out ("Unknown, or bad usage of, option '" ^ s ^ "'\n");
usage ();
Exit.badUsage)
| MLWorks.Internal.Save message =>
(out (message ^ "\n");
Exit.save)
| Encapsulate.BadInput s =>
(out (s ^ "\n");
Exit.badInput)
| exn as IO.Io _ =>
(out ("Uncaught exception " ^ exnMessage exn ^ "\n");
Exit.uncaughtIOException)
| OS.SysErr (s,err) =>
let
in
out ("System error: " ^ s ^ "\n");
Exit.failure
end
| Error s =>
(app out s;
out "\n";
Exit.failure)
| Info.Stop _ => Exit.stop
end
;
