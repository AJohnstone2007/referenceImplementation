require "^.basis.__int";
require "^.basis.__string";
require "^.basis.__list";
require "^.basis.__list_pair";
require "^.basis.__text_io";
require "^.basis.__io";
require "../utils/lists";
require "^.utils.__messages";
require "^.utils.__terminal";
require "../rts/gen/tags";
require "../basis/os";
require "../basis/os_path";
require "../utils/getenv";
require "../basics/module_id";
require "incremental";
require "shell_types";
require "user_context";
require "../parser/parser";
require "../typechecker/types";
require "../typechecker/basis";
require "../typechecker/completion";
require "../main/toplevel";
require "../main/project";
require "../main/proj_file";
require "../main/link_support";
require "../main/object_output";
require "../main/encapsulate";
require "../debugger/value_printer";
require "../debugger/newtrace";
require "../editor/editor";
require "../main/mlworks_io";
require "../main/user_options";
require "../main/preferences";
require "../utils/diagnostic";
require "shell_utils";
functor ShellUtils
(structure Lists : LISTS
structure Tags : TAGS
structure Incremental : INCREMENTAL
structure Editor : EDITOR
structure Parser : PARSER
structure Types : TYPES
structure Basis : BASIS
structure Completion : COMPLETION
structure ValuePrinter : VALUE_PRINTER
structure TopLevel : TOPLEVEL
structure Project : PROJECT
structure ProjFile : PROJ_FILE
structure Diagnostic: DIAGNOSTIC;
structure LinkSupport : LINK_SUPPORT
structure Encapsulate : ENCAPSULATE
structure Object_Output : OBJECT_OUTPUT
structure Trace : TRACE
structure Io : MLWORKS_IO
structure OS : OS
structure OSPath : OS_PATH
structure Getenv : GETENV
structure ModuleId : MODULE_ID
structure Preferences : PREFERENCES
structure UserOptions : USER_OPTIONS
structure UserContext : USER_CONTEXT
structure ShellTypes : SHELL_TYPES
sharing Incremental.InterMake.Compiler.Options = UserOptions.Options =
Completion.Options = ValuePrinter.Options = UserContext.Options =
ShellTypes.Options = TopLevel.Options
sharing Incremental.InterMake.Compiler.Info = Parser.Lexer.Info =
Project.Info = TopLevel.Info
sharing Completion.Datatypes = Basis.BasisTypes.Datatypes =
Incremental.Datatypes = Types.Datatypes
sharing Editor.Location = Parser.Lexer.Info.Location
sharing Incremental.InterMake.Compiler.Absyn = Parser.Absyn
sharing Basis.BasisTypes.Datatypes.Ident.Symbol =
Parser.Lexer.Token.Symbol
sharing Incremental.InterMake.Inter_EnvTypes.EnvironTypes.LambdaTypes.Ident =
Basis.BasisTypes.Datatypes.Ident
sharing type Incremental.Result = UserContext.Result
sharing type Incremental.InterMake.Compiler.TypeBasis =
Basis.BasisTypes.Basis
sharing type Incremental.InterMake.Compiler.DebugInformation =
ValuePrinter.DebugInformation
sharing type Incremental.InterMake.Compiler.ParserBasis =
Parser.ParserBasis
sharing type Parser.Lexer.Options =
Incremental.InterMake.Compiler.Options.options
sharing type Editor.preferences = Preferences.preferences =
UserContext.preferences = ShellTypes.preferences
sharing type Preferences.user_preferences = ShellTypes.user_preferences
sharing type UserOptions.user_context_options =
UserContext.user_context_options
sharing type UserContext.user_context = ShellTypes.user_context
sharing type Trace.UserOptions = UserOptions.user_tool_options =
ShellTypes.user_options
sharing type Incremental.Context = Trace.Context = UserContext.Context =
ShellTypes.Context
sharing type ValuePrinter.Type = Incremental.Datatypes.Type = Trace.Type
sharing type Parser.Lexer.TokenStream =
Incremental.InterMake.Compiler.tokenstream
sharing type Editor.Location.T = ModuleId.Location
sharing type ModuleId.ModuleId = Incremental.ModuleId =
Project.ModuleId = TopLevel.ModuleId = Object_Output.ModuleId
sharing type UserContext.identifier =
Basis.BasisTypes.Datatypes.Ident.Identifier
sharing type Project.Project = Incremental.InterMake.Project =
TopLevel.Project = Object_Output.Project
sharing type Encapsulate.Module = Object_Output.Module
) : SHELL_UTILS =
struct
structure InterMake = Incremental.InterMake
structure Inter_EnvTypes = InterMake.Inter_EnvTypes
structure Options = UserOptions.Options
structure Location = Editor.Location
structure Info = InterMake.Compiler.Info
structure Lexer = Parser.Lexer
structure Token = Lexer.Token
structure Compiler = InterMake.Compiler
structure BasisTypes = Basis.BasisTypes
structure Datatypes = BasisTypes.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
type user_preferences = Preferences.user_preferences
type UserOptions = UserOptions.user_tool_options
type Context = Incremental.Context
type Type = Incremental.Datatypes.Type
type ModuleId = Incremental.ModuleId
type ShellData = ShellTypes.ShellData
type user_context = UserContext.user_context
type preferences = Preferences.preferences
type history_entry = UserContext.history_entry
type Project = Project.Project
val _ = Diagnostic.set 0
fun diagnostic (level, output_function) =
Diagnostic.output
level
(fn verbosity => output_function verbosity)
val do_debug = false
fun debug_out s =
if do_debug then
Terminal.output("ShellUtils: " ^ s ^ "\n")
else
()
fun find_source_file (locdata:string) =
let
val sz = size locdata
fun scan i =
if i < sz then
if String.sub(locdata, i) = #":" then
substring(locdata, 0, i)
else
scan(i+1)
else
locdata
in
scan 0
end
exception FileFromLocation of string
fun uneditable_file s =
size s = 0 orelse
(MLWorks.String.ordof(s,0) = ord #"<"
andalso
MLWorks.String.ordof(s,(size s)-1) = ord #">")
fun file_from_location location =
let
fun get_filename Location.UNKNOWN =
raise FileFromLocation "Unknown location"
| get_filename (Location.FILE s) = s
| get_filename (Location.LINE (s,_)) = s
| get_filename (Location.POSITION (s,_,_)) = s
| get_filename (Location.EXTENT {name,...}) = name
val filename = get_filename location
in
if uneditable_file filename
then raise FileFromLocation (filename^" not a real file")
else filename
end
fun editable location =
(ignore(file_from_location location);
true)
handle FileFromLocation _ => false
exception EditFailed of string
fun check_exists s =
if OS.FileSys.access (s, []) then
()
else
raise EditFailed ("File " ^ s ^ " does not exist")
fun show_source (s, preferences) =
let
val location = Location.from_string s
val full_name = file_from_location location
val _ = check_exists full_name
val (edit_result, _) =
Editor.show_location preferences (full_name,location)
in
case edit_result of
NONE => ()
| SOME s => raise EditFailed s
end
handle FileFromLocation s => raise EditFailed s
fun edit_location (location, preferences) =
let
val full_name = file_from_location location
val _ = check_exists full_name
val (edit_result,quitfun) =
Editor.edit_from_location preferences (full_name,location)
in
case edit_result of
NONE => quitfun
| SOME s => raise EditFailed s
end
handle FileFromLocation s => raise EditFailed s
fun edit_file (filename, preferences) =
let
val full_name = Getenv.expand_home_dir filename
val _ = check_exists full_name
val (result,quitfun) =
Editor.edit preferences (full_name, 0)
in
case result of
NONE => quitfun
| SOME s => raise EditFailed s
end
handle Getenv.BadHomeName s => raise EditFailed s
fun edit_source (s, preferences) =
let
val location = Location.from_string s
in
edit_location (location, preferences)
end
fun is_code_item object =
let
val header = MLWorks.Internal.Value.primary object
in
header = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header object) = Tags.BACKPTR
end
fun is_closure object =
let
val header = MLWorks.Internal.Value.primary object
in
if header = Tags.POINTER then
case MLWorks.Internal.Value.header object
of (0, 0) =>
is_code_item (MLWorks.Internal.Value.sub (object,1))
| (secondary, length) =>
secondary = Tags.RECORD andalso
length > 0 andalso
is_code_item (MLWorks.Internal.Value.sub (object,1))
else
header = Tags.PAIRPTR andalso
is_code_item (MLWorks.Internal.Value.sub (object,0))
end
val object_editable = is_closure
fun edit_object (object, preferences) =
let
fun get_location code_name =
let
fun aux2((#"]" ::_),acc) = acc
| aux2(c::l,acc) = aux2(l,c::acc)
| aux2([],acc) = acc
fun aux1(#"["::l) = aux2 (l,[])
| aux1(c::l) = aux1 l
| aux1 [] = []
val locchars = aux1 (explode code_name)
in
implode (rev locchars)
end
fun closure_code_name closure =
let
val primary = MLWorks.Internal.Value.primary closure
val index = if primary = Tags.POINTER then 1 else 0
in
MLWorks.Internal.Value.code_name
(MLWorks.Internal.Value.sub (closure, index))
end
in
if object_editable object
then
let
val locstring = get_location (closure_code_name object ^ "\n")
in
edit_source (locstring, preferences)
end
else raise EditFailed "not a function object"
end
val cast = MLWorks.Internal.Value.cast
fun object_traceable object =
is_closure object andalso
MLWorks.Internal.Trace.status (cast object) <>
MLWorks.Internal.Trace.UNTRACEABLE
fun trace f =
Trace.trace (Trace.get_function_name (cast f))
fun untrace f =
Trace.untrace (Trace.get_function_name (cast f))
fun make_incremental_options (options,debugger) =
Incremental.OPTIONS
{options = options,
debugger = debugger}
fun delete_from_project (mod_name, location) =
let
val module_id = ModuleId.from_string (mod_name, location)
in
Incremental.delete_from_project module_id
end
fun make_exe_from_project(location, options, target, libs) =
let
val project = Incremental.get_project()
val project_name = case ProjFile.getProjectName() of
NONE => raise OS.SysErr("Project unnamed so can't infer dll name", NONE)
| SOME name => #base(OS.Path.splitBaseExt(#file(OS.Path.splitDirFile name)))
val targets = Project.currentTargets project
val _ =
if not(List.exists (fn t => t = target) targets) then
raise OS.SysErr("'" ^ target ^ "' is not a target for the project", NONE)
else
()
val libs = OS.Path.joinBaseExt{base=project_name, ext=SOME"lib"} :: libs
val target = #base(OS.Path.splitBaseExt target)
val declare = target ^ "$closure$declare"
val setup = target ^ "$closure$setup$get"
val glue =
"\t.text\n" ^
"\t.align\t4\n" ^
"\t.globl\t_main\n" ^
"\t.globl\t_trampoline\n" ^
"\t.globl\t" ^ setup ^ "\n" ^
"\t.globl\t" ^ declare ^ "\n" ^
"_main:\n" ^
"\tpushl\t%ebp\n" ^
"\tmovl\t%esp,%ebp\n" ^
"\tlea\t" ^ declare ^ ",%edx\t/* Get all the global declarations done first */\n" ^
"\tcall\t" ^ setup ^ "\n" ^
"\tpush\t%edx\n" ^
"\tpush\t%eax\n" ^
"\tcall\t_trampoline\n" ^
"\tadd\t$8,%esp\n" ^
"\tmov\t%ebx,%eax\n" ^
"\tleave\n" ^
"\tret\n" ^
"\t.align\t8\n"
val _ =
let
val stream = TextIO.openOut "exe_glue.S"
in
TextIO.output(stream, glue);
TextIO.closeOut stream
end
val _ = LinkSupport.gcc "exe_glue.S"
in
LinkSupport.link
{objects=["exe_glue.o"],
libs=libs,
target=target,
target_path = ".",
dll_or_exe=LinkSupport.EXE,
base=0wx10000000,
make_map=true,
linker=LinkSupport.LOCAL}
end
fun is_pervasive_unit s = String.sub(s, 0) = #" "
fun filter_eq eq [] = []
| filter_eq eq (arg as [_]) = arg
| filter_eq eq (x :: xs) =
let
fun adjoin(a,l) =
case List.find (fn b => eq(a,b)) l of
SOME _ => l
| _ => a::l
fun adjoin2(a,b,acc) = if eq(a, b) then acc else adjoin(a,acc)
fun filter_sub([],acc) = acc
| filter_sub([a],acc) = adjoin(a,acc)
| filter_sub(a::(l as b :: _),acc) = filter_sub(l,adjoin2(a,b,acc))
in filter_sub(xs, [x])
end
fun make_dll_from_project(location, options, target, libs) =
let
val project = Incremental.get_project()
val object_name = Project.objectName(options, location)
val targets = Project.currentTargets project
val targets =
map
(fn name => ModuleId.from_string(name, location))
targets
val units =
filter_eq ModuleId.eq
(List.foldr
op@
[]
(map
(fn name =>
Project.allObjects (options, location)
(project, name)
)
targets))
val units = List.filter (fn m => String.sub(ModuleId.string m, 0) <> #" ") units
val names = map (fn m => Project.get_name(project, m)) units
handle _ => raise OS.SysErr("Project system has bad unit", NONE)
val object_names =
map
(fn name => object_name(project, name))
names
val _ = LinkSupport.archive
{archive=OS.Path.joinBaseExt{base=target, ext=SOME"moa"}, files=object_names}
val stamp_source = LinkSupport.make_stamp target
val glue =
"\t.text\n" ^
"\t.globl\t_MLWDLLmain@12\n" ^
"\t.globl\tml_register_time_stamp\n" ^
"\t.globl\tuid\n" ^
"_MLWDLLmain@12:\n" ^
"\tcmpl\t$1,8(%esp)\n" ^
"\tjne\tl1\t\t/* Branch if not load */\n" ^
"\tpushl\t%ebp\n" ^
"\tmovl\t%esp,%ebp\n" ^
"\tlea\tuid,%eax\n" ^
"\tcall\tml_register_time_stamp\t/* Declare us to the runtime system */\n" ^
"\tleave\n" ^
"l1:\n" ^
"\tmov\t$1,%eax\n" ^
"\tret\t$12\n"
val text_start =
"\t.text\n" ^
"\t.globl\ttext_start\n" ^
"\t.align\t4\n" ^
"text_start:\n"
val text_end =
"\t.text\n" ^
"\t.globl\ttext_end\n" ^
"\t.align\t4\n" ^
"text_end:\n"
val data_start =
"\t.data\n" ^
"\t.globl\tdata_start\n" ^
"\t.align\t4\n" ^
"data_start:\n"
val data_end =
"\t.data\n" ^
"\t.globl\tdata_end\n" ^
"\t.align\t4\n" ^
"data_end:\n"
val ids_and_objects = Lists.zip(names, object_names)
val _ =
(app
(fn (module_id, mo_name) =>
(
Object_Output.output_object_code
(Object_Output.ASM, module_id, mo_name, project)
(Encapsulate.input_code mo_name)))
ids_and_objects)
handle Encapsulate.BadInput s => raise OS.SysErr(s, NONE)
val (text_names, data_names) =
ListPair.unzip
(map
(fn object_name =>
let
val {base, ext} = OS.Path.splitBaseExt object_name
val {dir, file} = OS.Path.splitDirFile base
val text = OS.Path.joinBaseExt{base=OS.Path.joinDirFile{dir=dir, file = file ^ "_text"},
ext = SOME"S"}
val data = OS.Path.joinBaseExt{base=OS.Path.joinDirFile{dir=dir, file = file ^ "_data"},
ext = SOME"S"}
in
(text, data)
end)
object_names)
val source_names = text_names @ data_names
val names_and_contents =
[("glue.S", glue),
("text_start.S", text_start),
("text_end.S", text_end),
("data_start.S", data_start),
("data_end.S", data_end),
("stamp.S", stamp_source)]
val _ =
app
(fn (name, contents) =>
let
val stream = TextIO.openOut name
in
TextIO.output(stream, contents);
TextIO.closeOut stream
end)
names_and_contents
val temp_names = map #1 names_and_contents
val source_names = temp_names @ source_names
val _ =
app
LinkSupport.gcc
source_names
val object_names =
map
(fn name => OS.Path.joinBaseExt{base= #base(OS.Path.splitBaseExt name), ext=SOME"o"})
source_names
val sub_projects = ProjFile.getSubprojects()
val extra_libs =
map
(fn name => OS.Path.joinBaseExt{base= #base (OS.Path.splitBaseExt name), ext=SOME"lib"})
sub_projects
in
LinkSupport.link
{objects=object_names,
libs=libs @ extra_libs,
target=target,
target_path = ".",
dll_or_exe=LinkSupport.DLL,
base=0wx10000000,
make_map=true,
linker=LinkSupport.LOCAL}
end
fun compile_file
(location, options)
error_info
filename =
let
val module_id =
ModuleId.from_string (filename, location)
val (project', _) =
Project.read_dependencies
(error_info, location)
(Incremental.get_project (), module_id, Project.empty_map)
val out_of_date =
Project.check_compiled
(error_info, location)
(project', module_id)
in
Incremental.set_project
(TopLevel.compile_file'
error_info
(options, project', out_of_date))
end
fun object_path (file_name, location) =
let
val module_id = ModuleId.from_string (file_name, location)
val project_dir = ProjFile.getProjectDir()
val (_, objects_dir, _) = ProjFile.getLocations()
val (_, modeDetails, curMode) = ProjFile.getModes()
val (_, _, curConfig) = ProjFile.getConfigurations()
val mode_prefix =
if isSome(curMode)
then
!(#location(ProjFile.getModeDetails(valOf(curMode),modeDetails)))
else ""
val config_prefix = getOpt(curConfig, "")
val path = OS.Path.concat
[ objects_dir, config_prefix, mode_prefix,
ModuleId.module_unit_to_string(module_id, "mo") ]
in
OS.Path.mkAbsolute{path=OS.Path.fromUnixPath path, relativeTo=project_dir}
end
fun force_compile (lo as (location, options)) error_info file_name =
let val module_id = ModuleId.from_string (file_name, location)
val path = object_path (file_name, location)
in OS.FileSys.remove path handle OS.SysErr (s,e) => ();
Project.set_object_info(Incremental.get_project (), module_id, NONE);
compile_file lo error_info file_name
end
fun get_units (error_info, location) =
let val _ = Incremental.reset_project()
val proj = Incremental.get_project ()
val proj' = Project.map_dag
(Project.update_dependencies (error_info, location))
proj
val _ = Incremental.set_project proj'
in List.filter
(fn (s, _) => String.sub (s, 0) <> #" ")
(Project.list_units proj')
end
fun force_compile_all (lo as (location, options)) error_info =
let fun remove_mo (file_name, module_id) =
let val path = object_path (file_name, location)
in OS.FileSys.remove path handle OS.SysErr (s,e) => ();
Project.set_object_info(Incremental.get_project (),
module_id, NONE)
end
in app remove_mo (get_units (error_info, location));
app (compile_file lo error_info) (#1 (ProjFile.getTargets()))
end
fun show_compile_file
(location, output_fn)
error_info
filename =
let
val module_id =
ModuleId.from_string
(filename, location)
val (project', _) =
Project.read_dependencies
(error_info, location)
(Incremental.get_project (), module_id, Project.empty_map)
val out_of_date =
Project.check_compiled
(error_info, location)
(Incremental.get_project (), module_id)
val _ = Incremental.set_project project'
in
case out_of_date of
[] => output_fn "No files need compiling\n"
| _ => (output_fn "Files to compile:\n";
app
(fn s => output_fn (" " ^ ModuleId.string s ^ "\n"))
out_of_date)
end
fun load_file
(user_context, location, options, preferences, output_fn)
error_info
filename =
let
val module_id = ModuleId.from_string (filename, location)
val (project', _) =
Project.read_dependencies
(error_info, location)
(Incremental.get_project(), module_id, Project.empty_map)
val out_of_date =
Project.check_load_objects
(error_info, location)
(project', module_id)
val _ = Incremental.set_project project'
in
case Incremental.load_mos
error_info
(options, UserContext.get_context user_context,
project', module_id, out_of_date, location) of
NONE =>
output_fn "up to date\n"
| SOME result =>
UserContext.process_result
{src = UserContext.COPY ("Shell.Build.loadObject \"" ^
ModuleId.string module_id ^ "\""),
result = result,
user_context = user_context,
options = options,
preferences = preferences,
output_fn = output_fn}
end
fun show_load_file
(location, output_fn)
error_info
filename =
let
val module_id = ModuleId.from_string (filename, location)
val (project', _) =
Project.read_dependencies
(error_info, location)
(Incremental.get_project (), module_id, Project.empty_map)
val out_of_date =
Project.check_load_objects
(error_info, location)
(project', module_id)
val _ = Incremental.set_project project'
val filelist = map ModuleId.string out_of_date
in
case filelist
of [] =>
output_fn "No files need loading\n"
| _ =>
(output_fn "Files to load\n";
app
(fn s => output_fn (" " ^ s ^ "\n"))
filelist)
end
fun compile_targets_for
(location, options, error_info)
project =
let
val initProject =
Project.update_dependencies (error_info, location) project
fun recompile_one
((project, depend_map, compile_map), module) =
let
fun do_recompile_one mod_id =
let
val (project, depend_map) =
Project.read_dependencies
(error_info, location)
(project, mod_id, depend_map);
val (out_of_date, compile_map) =
Project.check_compiled'
(error_info, location)
(project, mod_id)
([], compile_map)
val project =
TopLevel.compile_file'
error_info
(options, project, out_of_date);
val status_map =
Lists.reducel Project.mark_compiled (compile_map, out_of_date)
in
(project, depend_map, compile_map)
end
in
do_recompile_one (ModuleId.from_host (OS.Path.file module, location))
end
val (finalProject, _, _) =
Lists.reducel
recompile_one
((initProject, Project.empty_map, Project.visited_pervasives),
Project.currentTargets initProject);
in
finalProject
end
fun compile_targets
(location, options)
error_info =
Incremental.set_project
(Project.map_dag (compile_targets_for
(location,options,error_info))
(Incremental.get_project ()))
fun show_compile_targets_for
(location, output_fn, error_info) project =
let
val name = Project.get_project_name project
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
(error_info, location)
(project, mod_id, depend_map)
val (out_of_date_now, compile_map) =
Project.check_compiled'
(error_info, location)
(project, mod_id)
(out_of_date, compile_map)
in
(project, out_of_date_now, depend_map, compile_map)
end
in
do_check_one (ModuleId.from_host (OS.Path.file module, location))
end
val (finalProject, out_of_date, _, _) =
Lists.reducel
check_one
((initProject, [], Project.empty_map, Project.visited_pervasives),
Project.currentTargets initProject)
in
case out_of_date of
[] => output_fn ("No files need compiling for " ^ name ^ "\n")
| _ => (output_fn ("Files to compile for " ^ name ^ ":\n");
app
(fn s => output_fn (" " ^ ModuleId.string s ^ "\n"))
out_of_date);
finalProject
end
fun show_compile_targets
(location, output_fn)
error_info =
Incremental.set_project
(Project.map_dag (show_compile_targets_for
(location,output_fn,error_info))
(Incremental.get_project ()))
fun load_targets_for
(user_context, location, options, preferences, output_fn, error_info)
project =
let
val initProject =
Project.update_dependencies (error_info, location) project
fun reload_one ((project, depend_map), module) =
let
fun do_reload_one mod_id =
let
val (project', depend_map) =
Project.read_dependencies
(error_info, location)
(project, mod_id, depend_map);
val out_of_date =
Project.check_load_objects
(error_info, location)
(project', mod_id)
val _ = Incremental.set_project project'
in
case Incremental.load_mos
error_info
(options, UserContext.get_context user_context, project',
mod_id, out_of_date, location) of
NONE =>
output_fn ("Project " ^ (Project.get_project_name project)
^ " is up to date\n")
| SOME result =>
UserContext.process_result
{src = UserContext.COPY ("Shell.Build.loadObject \"" ^
ModuleId.string mod_id ^ "\""),
result = result,
user_context = user_context,
options = options,
preferences = preferences,
output_fn = output_fn};
(project', depend_map)
end
in
do_reload_one (ModuleId.from_host (OS.Path.file module, location))
end
val (finalProject, _) =
Lists.reducel
reload_one
((initProject, Project.empty_map),
Project.currentTargets initProject);
in
finalProject
end
fun load_targets
(user_context, location, options, preferences, output_fn)
error_info =
Incremental.set_project
( (load_targets_for
(user_context, location, options, preferences,
output_fn, error_info))
(Incremental.get_project ()))
fun show_load_targets_for
(location, output_fn, error_info) project =
let
val initProject =
Project.update_dependencies (error_info, location) project
fun check_one
((project, out_of_date, depend_map),
module) =
let
fun do_check_one mod_id =
let
val (project', depend_map) =
Project.read_dependencies
(error_info, location)
(project, mod_id, depend_map)
val (out_of_date_now) =
Project.check_load_objects
(error_info, location)
(project', mod_id)
in
(project, out_of_date_now, depend_map)
end
in
do_check_one (ModuleId.from_host (OS.Path.file module, location))
end
val (finalProject, out_of_date, _) =
Lists.reducel
check_one
((initProject, [], Project.empty_map),
Project.currentTargets initProject)
val _ = Incremental.set_project finalProject
val name = Project.get_project_name project
in
case out_of_date of
[] => output_fn ("No files need loading for " ^ name ^ "\n")
| _ => (output_fn ("Files to load for " ^ name ^ ":\n");
app
(fn s => output_fn (" " ^ ModuleId.string s ^ "\n"))
out_of_date);
finalProject
end
fun show_load_targets
(location, output_fn)
error_info =
Incremental.set_project
( (show_load_targets_for
(location, output_fn, error_info))
(Incremental.get_project ()))
fun use_stream
{token_stream, filename, user_context, toplevel_name,
user_options, preferences, debugger, error_info, output_fn, level} =
let
fun make_options () =
ShellTypes.new_options (user_options, user_context)
fun make_incremental_options () =
Incremental.OPTIONS
{options = make_options (),
debugger = debugger}
fun next () =
if Lexer.eof token_stream then ()
else
let
val result =
Incremental.compile_source
error_info
(make_incremental_options(),
UserContext.get_context user_context,
Compiler.TOKENSTREAM1 token_stream)
in
UserContext.process_result
{src = UserContext.COPY ("use \"" ^ String.toString filename ^ "\""),
result = result,
user_context = user_context,
options = make_options (),
preferences = preferences,
output_fn = output_fn};
next()
end
in
ShellTypes.with_toplevel_name filename (fn _ => next ())
end
handle IO.Io {name = rdr, cause, ...} =>
let
val s = exnMessage cause ^ " in: " ^ rdr
in
Info.default_error'
(Info.FATAL, Info.Location.FILE toplevel_name, s)
end
fun trim_spaces s =
let
fun trim (#" " :: rest) = trim rest
| trim l = l
in
implode (rev (trim (rev (trim (explode s)))))
end
fun resolve_file_name (filename,pathname,relative) =
let
val expanded_path =
OS.FileSys.fullPath (if relative then pathname else ".")
val expanded_file =
Getenv.expand_home_dir (trim_spaces filename)
val new_name =
OSPath.mkCanonical (OSPath.mkAbsolute {path=expanded_file, relativeTo=expanded_path})
in
(new_name,OSPath.dir new_name)
end
fun sub_use_file
{filename, user_context, toplevel_name, user_options, preferences,
debugger, error_info, output_fn, level} =
let
val Options.OPTIONS{compiler_options=Options.COMPILEROPTIONS{print_messages, ...}, ...} =
ShellTypes.new_options(user_options, user_context)
val (stream, filename) =
(TextIO.openIn filename, filename)
handle IO.Io {name = rdr, cause, ...} =>
let
val s = exnMessage cause ^ " in: " ^ rdr
val {base, ext} = OSPath.splitBaseExt filename
in
if not (isSome ext) then
let
val sml_name =
OSPath.joinBaseExt {base = base, ext = SOME "sml"}
in
(TextIO.openIn sml_name, sml_name)
handle IO.Io _ =>
Info.error'
error_info
(Info.FATAL, Location.FILE toplevel_name, s)
end
else
Info.error'
error_info
(Info.FATAL, Location.FILE toplevel_name, s)
end
val _ =
if print_messages then
let
fun spaces y =
if y <= 0 then "Use: " ^ filename else ("   " ^ spaces (y-1))
in
output_fn (spaces level ^ "\n")
end
else
()
val token_stream =
Lexer.mkFileTokenStream (stream, filename)
in
use_stream {token_stream = token_stream,
filename = filename,
user_context = user_context,
toplevel_name = toplevel_name,
user_options = user_options,
preferences = preferences,
debugger = debugger,
error_info = error_info,
output_fn = output_fn,
level = level}
handle exn => (TextIO.closeIn stream; raise exn);
TextIO.closeIn stream
end
val use_level = ref 0
val use_pathname = ref "."
fun internal_use_file (shell_data, debugger, output_fn, filename) =
let
val ShellTypes.SHELL_DATA {get_user_context,
user_options,
user_preferences,
...} =
shell_data
val Preferences.USER_PREFERENCES
({use_relative_pathname = ref relative,...},_) = user_preferences
val toplevel_name = ShellTypes.get_current_toplevel_name()
val (new_name,new_path) =
resolve_file_name (filename,!use_pathname,relative)
handle Getenv.BadHomeName s =>
Info.default_error'
(Info.FATAL, Info.Location.FILE toplevel_name,
"Invalid home name: " ^ s)
| OS.SysErr _ =>
Info.default_error'
(Info.FATAL, Info.Location.FILE toplevel_name,
"No such file: `" ^ filename ^ "'")
val error_info = Info.make_default_options ()
val level = !use_level
val old_pathname = !use_pathname
in
use_level := level + 1;
use_pathname := new_path;
sub_use_file {filename = new_name,
user_context = get_user_context (),
error_info = error_info,
toplevel_name = ShellTypes.get_current_toplevel_name(),
user_options = user_options,
preferences =
Preferences.new_preferences user_preferences,
debugger = debugger,
output_fn = output_fn,
level = level
}
handle exn => (use_level := level;
use_pathname := old_pathname;
raise exn);
use_level := level;
use_pathname := old_pathname
end
fun use_file (shell_data, output_fn, pathname) =
internal_use_file (shell_data, fn x => x, output_fn, pathname)
fun use_string (shell_data, output_fn, string) =
let
val toplevel_name = ShellTypes.get_current_toplevel_name()
val error_info = Info.make_default_options ()
val ShellTypes.SHELL_DATA {get_user_context,
user_options,
user_preferences,
...} =
shell_data
val level = !use_level
val name = "<use_string input>"
val r = ref string
val token_stream =
Lexer.mkTokenStream
(fn _ => let val s = !r in r := "" ; s end,name)
in
use_level := level + 1;
use_stream {filename = name,
token_stream = token_stream,
user_context = get_user_context (),
error_info = error_info,
toplevel_name = ShellTypes.get_current_toplevel_name(),
user_options = user_options,
preferences =
Preferences.new_preferences user_preferences,
debugger = fn x => x,
output_fn = output_fn,
level = level
}
handle exn => (use_level := level; raise exn);
use_level := level
end
fun read_dot_mlworks shell_data =
let
fun output_fn s = Messages.output s;
val ShellTypes.SHELL_DATA{debugger,...} = shell_data
in
case Getenv.get_startup_filename () of
NONE => ()
| SOME pathname =>
if OS.FileSys.access (pathname, []) handle OS.SysErr _ => false
then
internal_use_file (shell_data, debugger, output_fn, pathname)
handle Info.Stop _ => ()
| MLWorks.Interrupt => ()
| ShellTypes.DebuggerTrapped => ()
else ()
end
exception NotAnExpression = Incremental.NotAnExpression
fun eval error_info (string, options, context) =
let
val parser_basis = Incremental.parser_basis context
val input_fn =
let val sref = ref (string ^ ";")
in
fn _ => let val result = !sref in sref := "" ; result end
end
fun error_wrap f a =
let val result = Info.wrap
error_info
(Info.WARNING, Info.RECOVERABLE,
Info.FAULT,Location.FILE "Eval Input")
f
a
in
result
end
val token_stream =
Parser.Lexer.mkTokenStream (input_fn,"<Eval input>")
fun doit error_info () =
let
fun get_topdec () =
(let val _ =
Parser.parse_incrementally
error_info
(options,
token_stream,
parser_basis,
Parser.initial_parser_state,
Parser.Lexer.Token.PLAIN_STATE)
in
Info.error' error_info
(Info.FATAL, Location.FILE "Eval Input",
"Unexpected end of line")
end
handle
Parser.FoundTopDec(topdec,newpB,loc) =>
topdec
| Parser.SyntaxError(message,location) =>
Info.error' error_info (Info.FATAL, location, message))
val topdec = get_topdec ()
in
Incremental.evaluate_exp_topdec
error_info
(Incremental.OPTIONS{options = options,
debugger = fn x => x},
context,
topdec)
end
in
error_wrap doit ()
end
fun print_value ((object,ty),print_options,context) =
let
val debug_info = Incremental.debug_info context
in
ValuePrinter.stringify_value false
(print_options, object, ty, debug_info)
end
fun print_type (ty,print_options,context) =
let
val completion_env =
let
val type_basis = Incremental.type_basis context
val BasisTypes.BASIS(_,_,_,_,env) = type_basis
in
env
end
in
Completion.print_type (print_options,completion_env,ty)
end
datatype COMPLETION =
TOKEN of Token.Token |
STRING of string |
NO_COMPLETION
fun prefixp (string1,string2) =
(size string1 <= size string2) andalso
(string1 =
substring (string2,0,size string1))
fun do_map (map,to_string,string) =
NewMap.fold_in_rev_order
(fn (acc,name,_) =>
let val string' = to_string name
in
if prefixp (string,string')
then string' :: acc
else acc
end)
([],map)
fun get_valid_symbol (Ident.VAR s) = s
| get_valid_symbol (Ident.CON s) = s
| get_valid_symbol (Ident.EXCON s) = s
| get_valid_symbol (Ident.TYCON' s) = s
fun get_env_names (Datatypes.ENV (Datatypes.SE strmap,
Datatypes.TE tymap,
Datatypes.VE (_,valmap)),
id) =
do_map (strmap,(fn Ident.STRID s => Symbol.symbol_name s),id) @
do_map (tymap,(fn Ident.TYCON s => Symbol.symbol_name s),id) @
do_map (valmap,(Symbol.symbol_name o get_valid_symbol),id)
fun get_basis_names (BasisTypes.BASIS(_,
nameset,
BasisTypes.FUNENV funmap,
BasisTypes.SIGENV sigmap,
env),
id) =
get_env_names (env,id) @
do_map (funmap,(fn Ident.FUNID s => Symbol.symbol_name s),id) @
do_map (sigmap,(fn Ident.SIGID s => Symbol.symbol_name s),id)
exception NoEnv
fun get_env (path,BasisTypes.BASIS(_,_,_,_,env)) =
let
fun get_env_env ([],env) = env
| get_env_env (sym::path,Datatypes.ENV(Datatypes.SE strmap,_,_)) =
let
val strid = Ident.STRID(sym)
val str = NewMap.apply'(strmap,strid)
fun get_str_env (Datatypes.COPYSTR(_,str)) = get_str_env str
| get_str_env (Datatypes.STR (_,_,env)) = env
in
get_env_env(path,get_str_env str)
end
in
get_env_env (path,env)
end
handle NewMap.Undefined => raise NoEnv
fun split_filename s =
(OSPath.dir s, OSPath.file s)
fun find_matches (dir,s) =
let
val names = ref []
val _ = debug_out ("Opening " ^ dir)
val directory = if (dir = "") then "." else dir
val full_path = OS.FileSys.fullPath (Getenv.expand_home_dir directory)
handle OS.SysErr _ => directory
val d = OS.FileSys.openDir (full_path)
fun get_names () =
case OS.FileSys.readDir d of
NONE => ()
| SOME next =>
(if size next >= size s andalso
substring (next,0,size s) = s
then names := next :: !names
else ();
get_names ())
in
get_names ();
OS.FileSys.closeDir d;
!names
end
handle _ => []
fun complete_token (sofar,TOKEN (token as Token.RESERVED _),context) =
complete_token (sofar,
TOKEN
(Token.LONGID ([],
Symbol.find_symbol
(Token.makestring token))),
context)
| complete_token (sofar,
TOKEN (token as Token.LONGID (path,id)),
context) =
let
val type_basis = Incremental.type_basis context
val string = Symbol.symbol_name id
val completions =
case path of
[] => get_basis_names (type_basis,string)
| _ =>
(let
val env = get_env (path,type_basis)
val pathname =
concat
(rev (Lists.reducel
(fn (acc,s) => "." :: Symbol.symbol_name s :: acc)
([],path)))
in
map (fn s => pathname ^ s) (get_env_names (env,string))
end
handle NoEnv => [])
fun is_structure_path path =
(ignore(get_env (path,type_basis)); true) handle NoEnv => false
in
case completions of
[s] =>
if s = sofar andalso is_structure_path (path @ [id])
then [s ^ "."]
else completions
| _ => completions
end
| complete_token (sofar,STRING s,context) =
let
fun munge1 ([],acc) = implode (rev acc)
| munge1 (#"\\" :: #"\\" ::rest,acc) = munge1 (rest, #"\\" ::acc)
| munge1 (a::rest,acc) = munge1 (rest,a::acc)
fun munge2 ([],acc) = implode (rev acc)
| munge2 (#"\\" ::rest,acc) = munge2 (rest, #"\\" :: #"\\" :: acc)
| munge2 (a::rest,acc) = munge2 (rest,a::acc)
val munged_sofar = munge1 (explode sofar,[])
val (dir,fname) = split_filename (munge1 (explode s,[]))
val names = find_matches (dir,fname)
val completions' =
(if dir ="" then names
else map (fn n => OSPath.concat [dir,n]) names)
val completions = map (fn s => munge1 (explode s, [])) completions'
fun isDir s =
let
val s = Getenv.expand_home_dir s
handle Getenv.BadHomeName _ => s
val s = OS.FileSys.fullPath s
val result = OS.FileSys.isDir s
in
result
end
handle OS.SysErr _ => false
in
map (fn s => munge2 (explode s,[]))
(case completions of
[s] =>
(if s = munged_sofar andalso isDir s
then [OSPath.concat [s,""]]
else completions)
| _ => completions)
end
| complete_token _ = []
fun filter_completions l =
let
fun remove_duplicates ([],acc) = acc
| remove_duplicates ([a],acc) = a :: acc
| remove_duplicates (a :: (rest as (b :: l)), acc) =
if a = b
then remove_duplicates (rest,acc)
else remove_duplicates (rest,a::acc)
in
remove_duplicates (Lists.qsort ((op>):string*string->bool) l, [])
end
fun find_string (s:string) =
let
val chars = explode s
fun strip [] = NO_COMPLETION
| strip (a::b) =
if a = #"\"" then
find_string (b,[])
else strip b
and find_string ([],acc) =
STRING (implode (rev acc))
| find_string (a::b,acc) =
if a = #"\"" then
strip b
else
find_string (b,a::acc)
in
strip chars
end
fun get_completions (s,options,context) =
let
fun get_token s =
case find_string s of
STRING s => STRING s
| _ =>
let
val error_info = Info.make_default_options ()
fun doit error_info () =
let
val x = ref s
fun input_fn _ =
(let val result = !x in x := ""; result end)
val ts = Lexer.mkTokenStream (input_fn,"")
fun get_token tok =
case Lexer.getToken error_info (options,
Lexer.Token.PLAIN_STATE,
ts) of
Token.EOF (state) => tok
| new_tok => get_token (TOKEN new_tok)
in
get_token NO_COMPLETION
end
in
Info.with_report_fun
error_info
(fn _ => ())
doit
()
end
val to_complete = get_token s
val sofar =
case to_complete of
NO_COMPLETION => ""
| TOKEN tok => Token.makestring tok
| STRING string => string
in
(sofar,
filter_completions (complete_token (sofar,to_complete,context)))
end
fun find_common_completion [] = ""
| find_common_completion (target::l) =
let
fun check (s,m,n) =
if m = n
then n
else if MLWorks.String.ordof(s,m) = MLWorks.String.ordof(target,m)
then check (s,m+1,n)
else m
fun aux ([],n) = n
| aux ((a::l),n) =
aux (l,check(a,0,(Int.min (size a,n))))
val result =
substring (target,0,aux(l,size target))
in
result
end
fun lookup_name (name,context,default) =
let
val tycontext =
Basis.basis_to_context (Incremental.type_basis context)
val valid = Ident.VAR(Symbol.find_symbol name)
val valtype =
#1(Basis.lookup_val (Ident.LONGVALID (Ident.NOPATH, valid),
tycontext,
Ident.Location.UNKNOWN,
false))
val mlval = Inter_EnvTypes.lookup_val(valid,
Incremental.inter_env context)
in
(mlval,valtype)
end
handle _ => default
fun value_from_history_entry
(UserContext.ITEM {id, context, source, ...}, options) =
let val Options.OPTIONS
{listing_options, print_options, compat_options, extension_options,
compiler_options =
Options.COMPILEROPTIONS {mips_r4000, sparc_v7, ...} } = options
val options' =
Options.OPTIONS
{listing_options=listing_options, print_options=print_options,
compat_options=compat_options,
extension_options=extension_options,
compiler_options =
Options.COMPILEROPTIONS
{generate_debug_info=false, debug_variables=false,
generate_moduler=false, intercept=false, interrupt=false,
opt_handlers=false, opt_leaf_fns=false, opt_tail_calls=false,
opt_self_calls=false, local_functions=false,
print_messages=false,
mips_r4000=mips_r4000, sparc_v7=sparc_v7} }
in
case id of
Ident.VALUE (Ident.VAR s) =>
(let
val value =
eval Info.null_options
(Ident.Symbol.symbol_name s,
options', context)
in
TextIO.flushOut TextIO.stdOut;
case source of
UserContext.STRING str => SOME (str, value)
| UserContext.COPY str => SOME (str, value)
end
handle _ => NONE)
| _ =>
NONE
end
fun value_from_user_context (user_context, user_options) =
case UserContext.get_latest user_context
of NONE => NONE
| SOME item =>
value_from_history_entry
(item, ShellTypes.new_options (user_options, user_context))
end
;
