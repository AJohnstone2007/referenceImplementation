require "../system/__link_support";
require "../basis/__int";
require "../basis/__real";
require "../basis/__list";
require "../basis/__string";
require "../system/__time";
require "^.system.__file_time";
require "../main/encapsulate";
require "../main/compiler";
require "../main/proj_file";
require "../utils/map";
require "../utils/crash";
require "../utils/lists";
require "../utils/diagnostic";
require "../make/depend";
require "../basics/module_id";
require "../basis/os";
require "options";
require "mlworks_io";
require "../dependency/_group_dag";
require "../dependency/module_dec_io.sml";
require "../dependency/import_export.sml";
require "../dependency/__ordered_set";
require "project";
functor Project (
structure Encapsulate: ENCAPSULATE;
structure ProjFile: PROJ_FILE;
structure Compiler: COMPILER;
structure Diagnostic: DIAGNOSTIC;
structure NewMap: MAP;
structure Crash: CRASH;
structure ModuleId: MODULE_ID;
structure Io: MLWORKS_IO;
structure Depend: DEPEND;
structure Options: OPTIONS;
structure Lists: LISTS;
structure OS : OS
structure ModuleDecIO : MODULE_DEC_IO
structure ImportExport : IMPORT_EXPORT
sharing type Depend.ModuleId = ModuleId.ModuleId = Io.ModuleId
sharing type Depend.Info.Location.T = ModuleId.Location
sharing ModuleDecIO.ModuleDec = ImportExport.ModuleDec
sharing type ImportExport.context = Compiler.Top_Env
): PROJECT =
struct
structure Info = Depend.Info
structure Location = Info.Location
structure FileSys = OS.FileSys
structure ModuleDec = ModuleDecIO.ModuleDec
structure GD = GroupDagFun(structure ImportExport = ImportExport
structure ModuleId = ModuleId);
type Options = Options.options
type ('a, 'b) Map = ('a, 'b) NewMap.map
type ModuleId = ModuleId.ModuleId
type CompilerBasis = Compiler.basis
type IdCache = Compiler.id_cache
type target_type = ProjFile.target_type
val _ = Diagnostic.set 0
fun diagnostic (level, output_function) =
Diagnostic.output
level
(fn verbosity => output_function verbosity)
datatype Dependencies =
DEPEND_LIST of
{mod_name : string, time : Time.time} list
datatype FileTime =
OBJECT of Time.time
| SOURCE of Time.time
datatype Unit =
UNIT of
{name: ModuleId.ModuleId,
source: (string * Time.time) option ref,
requires: {explicit: ModuleId.ModuleId list,
implicit: ModuleId.ModuleId list,
subreqs : ModuleId.ModuleId list} ref,
object: {file_time: Time.time,
time_stamp: Time.time,
file: string,
stamps: int,
consistency: Dependencies} option ref,
loaded: {file_time: FileTime,
load_time: Time.time,
basis: Compiler.basis,
id_cache: Compiler.id_cache,
module: MLWorks.Internal.Value.T,
dependencies: Dependencies} option ref,
visible: bool ref,
options: Options.options option ref,
mod_decls: (ModuleDec.Dec * bool) ref
}
datatype Project =
PROJECT of
{name: string,
units: (ModuleId.ModuleId, Unit) NewMap.map,
files: string list,
library_path: string list,
object_dir: {base: string, config: string, mode: string},
subprojects: Project list,
current_targets: string list,
disabled_targets: string list,
dependency_info: Dependency_info
}
and Dependency_info =
DEPEND of
(ImportExport.ModuleName.set * dag) list
* (ImportExport.ModuleName.set * dag) list
withtype dag = (ModuleId.ModuleId OrderedSet.set) GD.dag
type project_cache = ({filename: string,
targets: string list}, Project) NewMap.map
fun string_list_lt([], []) = false
| string_list_lt([], _) = true
| string_list_lt(h::t, []) = false
| string_list_lt(h1::t1, h2::t2) =
String.<(h1,h2)
orelse (h1 = h2 andalso string_list_lt(t1,t2))
fun project_cache_lt
({filename = f1, targets = tl1},
{filename = f2, targets = tl2}) =
String.<(f1, f2)
orelse (f1 = f2
andalso (string_list_lt(tl1, tl2)))
val empty_project_cache : project_cache =
NewMap.empty (project_cache_lt, op =)
fun get_subprojects (proj as PROJECT{subprojects,...}) = subprojects
fun set_subprojects (PROJECT{name,units,files,library_path,object_dir,
current_targets,disabled_targets,
dependency_info,...}, subprojects) =
PROJECT{name=name,units=units,files=files,library_path=library_path,
object_dir=object_dir,current_targets=current_targets,
disabled_targets=disabled_targets,dependency_info=dependency_info,
subprojects=subprojects}
fun map_dag (f: Project -> Project) (p: Project) =
let
val project_cache = ref empty_project_cache
fun process (proj as PROJECT {name, current_targets, subprojects, ...}) =
case NewMap.tryApply' (!project_cache,
{filename = name, targets = current_targets}) of
SOME project => project
| NONE =>
let val proj' = f (set_subprojects(proj, map process subprojects))
in
project_cache :=
NewMap.define (!project_cache,
{ filename=name, targets=current_targets },
proj');
proj'
end
in process p
end
val { union = union_mid_set, memberOf = member_mid_set, ... } =
OrderedSet.gen { eq = ModuleId.eq, lt = ModuleId.lt }
val empty_mid_set = OrderedSet.empty
val singleton_mid_set = OrderedSet.singleton
exception NoSuchModule of ModuleId.ModuleId
datatype Kind = PERVASIVE | USER
fun currentTargets (PROJECT {current_targets, ...}) = current_targets
fun combineOpt (NONE, NONE) = NONE
| combineOpt (NONE, SOME a) = SOME a
| combineOpt (SOME a, NONE) = SOME a
| combineOpt (SOME a, SOME b) = SOME a
fun module_id_in_project (PROJECT {units, ...}, m) =
case NewMap.tryApply' (units, m) of
SOME _ => true
| NONE => false
fun get_unit (PROJECT {units, subprojects, ...}, m) =
case NewMap.tryApply' (units, m)
of SOME unit => SOME unit
| NONE =>
case subprojects of
[] => NONE
| (a::rest) =>
foldl combineOpt NONE (map (fn p => get_unit(p, m)) subprojects)
fun get_project_name (PROJECT {name, ...}) = name
fun get_name (proj, m) =
case get_unit (proj, m)
of SOME (UNIT {name, ...}) => name
| NONE =>
(diagnostic (1,
fn _ => ["No such module in get_name: `", ModuleId.string m, "'"]);
raise NoSuchModule m)
fun requires_from_unit
(UNIT {name, requires = ref {explicit,implicit,subreqs},...}) =
case explicit of
[] => implicit
| _ => explicit
fun get_requires (proj, m) =
case get_unit(proj, m) of
SOME u => requires_from_unit u
| NONE =>
(diagnostic (1,
fn _ => ["No such module in get_requires: `", ModuleId.string m, "'"]);
raise NoSuchModule m)
fun get_external_requires (proj as PROJECT{subprojects,...}) =
let fun convert (subproject as PROJECT {name,...}) =
map (fn t => (subproject, ModuleId.from_host (t, Location.FILE name)))
(currentTargets subproject)
in List.concat (map convert subprojects)
end
fun is_visible (proj, m) =
case get_unit (proj, m)
of SOME (UNIT {visible, ...}) => !visible
| NONE =>
(diagnostic (1,
fn _ => ["No such module in is_visible: `", ModuleId.string m, "'"]);
raise NoSuchModule m)
fun set_visible (proj, m, b) =
case get_unit (proj, m)
of SOME (UNIT {visible, ...}) => visible := b
| NONE =>
(diagnostic (1,
fn _ => ["No such module in is_visible: `", ModuleId.string m, "'"]);
raise NoSuchModule m)
fun get_source_info (proj, m) =
case get_unit (proj, m)
of SOME (UNIT {source, ...}) => !source
| NONE => NONE
fun set_source_info (proj, m, info) =
case get_unit (proj, m)
of SOME (UNIT {source, ...}) => source := info
| NONE =>
(diagnostic (1,
fn _ => ["No such module in set_source_info: `",
ModuleId.string m, "'"]);
raise NoSuchModule m)
fun get_object_info (proj, m) =
case get_unit (proj, m)
of SOME (UNIT {object, ...}) => !object
| NONE => NONE
fun set_object_info (proj, m, info) =
case get_unit (proj, m)
of SOME (UNIT {object, ...}) => object := info
| NONE =>
(diagnostic (1,
fn _ => ["No such module in set_object_info: `",
ModuleId.string m, "'"]);
raise NoSuchModule m)
fun get_loaded_info (proj, m) =
case get_unit (proj, m)
of SOME (UNIT {loaded, ...}) => !loaded
| NONE => NONE
fun set_loaded_info (proj, m, info) =
case get_unit (proj, m)
of SOME (UNIT {loaded, ...}) => loaded := info
| NONE =>
(diagnostic (1,
fn _ => ["No such module in set_loaded_info: `",
ModuleId.string m, "'"]);
raise NoSuchModule m)
fun clear_info pred (PROJECT {units, subprojects, ...}) =
(NewMap.iterate
(fn (m, UNIT u) =>
if pred m then
#loaded u := NONE
else
())
units;
app (clear_info pred) subprojects)
fun clear_all_loaded_info (proj, pred) =
clear_info pred proj
fun mesg_fn (location, s) =
print(Info.Location.to_string location ^ ": " ^ s ^ "\n")
fun findFile ext (search_path, module_id) =
let
fun search [] =
(diagnostic (2, fn _ => ["Failed to find file"]);
NONE)
| search (dir::rest) =
let
val filename =
OS.Path.joinDirFile
{dir = dir,
file = ModuleId.module_unit_to_string (module_id, ext)}
val _ =
diagnostic (2, fn _ => ["searching: ", filename])
in
let
val mod_time = FileTime.modTime filename
in
diagnostic (2, fn _ => ["Found `", filename, "'"]);
SOME (filename, mod_time)
end
handle OS.SysErr _ =>
search rest
end
in
search search_path
end
val source_ext = "sml"
val object_ext = "mo"
local
fun objectName' ({base, config, mode}, module_id) =
OS.Path.mkCanonical
(OS.Path.concat
[base, config, mode,
ModuleId.module_unit_to_string(module_id, object_ext)])
fun search_lib_path (NONE, _) = NONE
| search_lib_path (SOME lib_path, module_id) =
findFile object_ext (lib_path, module_id)
in
fun findObject (object_dir, lib_path_opt, module_id) =
let
val filename = objectName' (object_dir, module_id)
in
let
val mod_time = FileTime.modTime filename
in
diagnostic (2, fn _ => ["Found `", filename, "'"]);
SOME (filename, mod_time)
end
handle OS.SysErr _ =>
search_lib_path (lib_path_opt, module_id)
end
fun objectName
(error_info, loc)
(PROJECT {units, object_dir, subprojects, ...}, module_id) =
let
val top_unit = NewMap.tryApply' (units, module_id)
fun get_subproject_info ([], m) = (NONE, object_dir)
| get_subproject_info
((PROJECT {object_dir, subprojects, units, ...}) :: rest, m) =
case NewMap.tryApply' (units, m) of
SOME unit => (SOME unit, object_dir)
| NONE => get_subproject_info (subprojects @ rest, m)
val (unitOpt, object_dir) =
if isSome(top_unit) then
(top_unit, object_dir)
else
get_subproject_info (subprojects, module_id)
val unit =
case top_unit of
SOME unit => unit
| NONE =>
case unitOpt of
SOME unit => unit
| NONE =>
(Info.default_error'
(Info.FATAL, loc,
"Missing unit information for module " ^
ModuleId.string module_id))
in
case unit of
UNIT {object as ref (SOME {file, ...}), ...} =>
file
| _ =>
objectName' (object_dir, module_id)
end
fun pervasiveObjectName module_id =
objectName' ({base=Io.get_pervasive_dir(), config="", mode=""},module_id)
end
fun findPervasiveFile ext module_id =
let
val filename =
OS.Path.joinDirFile
{dir = Io.get_pervasive_dir(),
file = ModuleId.module_unit_to_string (module_id, ext)}
val _ = diagnostic (2, fn _ => ["looking for: ", filename])
val mod_time = FileTime.modTime filename
in
diagnostic (2, fn _ => ["Found `", filename, "'"]);
SOME (filename, mod_time)
end
handle
OS.SysErr _ => NONE
| Io.NotSet _ => NONE
val findPervasiveObject = findPervasiveFile object_ext
val findPervasiveSource = findPervasiveFile source_ext
fun findSource (file_list, module_id) =
let
val fname =
OS.Path.mkCanonical (ModuleId.module_unit_to_string (module_id, "sml"))
fun search [] =
(diagnostic (2, fn _ => ["Failed to find file in Project file list"]);
NONE)
| search (filename::rest) =
if (fname = OS.Path.mkCanonical (OS.Path.file filename)) then
let
val mod_time = FileTime.modTime filename
in
diagnostic (2, fn _ => ["Found `", filename, "'"]);
SOME (filename, mod_time)
end
handle OS.SysErr _ =>
(diagnostic (2, fn _ => ["File `", filename, "' cannot be inspected."]);
NONE)
else
search rest
in
search file_list
end
fun module_id_from_string (filename, is_pervasive) name =
let val loc = Location.FILE filename
in if is_pervasive
then ModuleId.perv_from_require_string(name, loc)
else ModuleId.from_require_string(name, loc)
end
fun module_name_from_require req_name =
ModuleId.add_path (ModuleId.empty_path, req_name)
fun load_src (is_pervasive, error_info,
SOME (filename, mod_time), unit,
object_dir as {base, config, mode}) =
let
val (mod_decs, requires, partial) =
ModuleDecIO.source_to_module_dec (filename, SOME mod_time,
if is_pervasive then Io.get_pervasive_dir()
else OS.Path.mkCanonical(OS.Path.concat[base, config]))
val sub_modules =
map ( module_name_from_require
o (module_id_from_string (filename, is_pervasive)) )
requires
in
case unit of
UNIT {source, requires, mod_decls, ...} =>
(source := SOME (filename, mod_time);
mod_decls := (mod_decs, partial);
requires := {explicit=sub_modules,implicit=[],subreqs=[]})
end
| load_src (is_pervasive, error_info, NONE,
UNIT {name, source, requires, mod_decls, ...},
object_dir as {base, config, mode}) =
let
val filename = ModuleId.module_unit_to_string(name, "sml")
val (mod_decs, reqs, partial) =
ModuleDecIO.source_to_module_dec (filename, NONE,
if is_pervasive then Io.get_pervasive_dir()
else OS.Path.mkCanonical(OS.Path.concat[base, config]))
val sub_modules =
map ( module_name_from_require
o (module_id_from_string (filename, is_pervasive)) )
reqs
in
(source := NONE;
mod_decls := (mod_decs, partial);
requires := {explicit=sub_modules,implicit=[],subreqs=[]})
end
fun load_object (SOME (filename, mod_time)) unit =
(let
val {time_stamp, consistency, mod_name, stamps} =
Encapsulate.input_info filename
in
case unit of
UNIT {object, ...} =>
object :=
SOME
{file_time = mod_time,
time_stamp = time_stamp,
file = filename,
stamps = stamps,
consistency = DEPEND_LIST consistency}
end
handle Encapsulate.BadInput str =>
(print("Corrupt object file '" ^ filename ^ "' treating as out of date\n");
case unit of
UNIT {object, ...} => object := NONE))
| load_object NONE unit =
case unit of
UNIT {object, ...} => object := NONE
fun new_unit
error_info
(is_pervasive, map, module_id, sml_info, mo_info, info_from_mo, object_dir) =
let
val unit =
UNIT
{name = module_id,
source = ref NONE,
requires = ref {explicit=[],implicit=[],subreqs=[]},
loaded = ref NONE,
visible = ref false,
options = ref NONE,
mod_decls = ref (ModuleDec.SeqDec [], false),
object = ref NONE}
val _ = load_src (is_pervasive, error_info, sml_info, unit, object_dir)
val _ =
case info_from_mo of
SOME {consistency, mod_name, time_stamp, stamps} =>
(case mo_info of
SOME (filename, mod_time) =>
(case unit of
UNIT {object, ...} =>
object :=
SOME
{file_time = mod_time,
time_stamp = time_stamp,
file = filename,
stamps = stamps,
consistency = DEPEND_LIST consistency})
| NONE =>
Crash.impossible ("new_unit has info_from_mo but no mo_info in `"
^ ModuleId.string module_id ^ "'!"))
| NONE =>
(diagnostic (3,
fn _ => ["calling load_object from new_unit"]);
load_object mo_info unit;
case (mo_info, sml_info) of
(NONE, NONE) =>
if is_pervasive then
Info.error'
error_info
(Info.FATAL, Info.Location.UNKNOWN,
"No files found for pervasive file: `" ^ ModuleId.string module_id ^ "'")
else
()
| _ => ())
val map' = NewMap.define (map, module_id, unit)
in
(map', unit)
end
type StatusMap = (ModuleId, bool) Map
fun mark_visited (v, m) = NewMap.define (v, m, false)
fun mark_compiled (v, m) = NewMap.define (v, m, true)
val empty_map = NewMap.empty (ModuleId.lt, ModuleId.eq)
val visited_pervasives = mark_compiled (empty_map, Io.pervasive_library_id)
fun no_targets (error_info, location) projectName =
let
val name = getOpt (ProjFile.getProjectName(), "")
val err_project_str =
if (name = "") orelse
(OS.Path.mkCanonical projectName) = (OS.Path.mkCanonical name) then
"No targets specified in current project"
else
"No targets specified in project: " ^ projectName
in
Info.error' error_info
(Info.FATAL, location, err_project_str)
end
fun check_src (unit as UNIT {source = src_info, ...}, module_id,
is_pervasive, files, error_info, location, object_dir) =
case !src_info of
sml_info as SOME (filename, time_stamp) =>
(let val mod_time = FileTime.modTime filename
in if time_stamp = mod_time
then ()
else
load_src
(is_pervasive, error_info,
SOME (filename, mod_time), unit, object_dir)
end
handle OS.SysErr _ =>
(mesg_fn (location,
"Source file " ^ filename ^ " has disappeared");
if is_pervasive then
load_src
(true, error_info,
findPervasiveSource module_id,
unit, object_dir)
else
load_src
(false, error_info,
findSource (files, module_id),
unit, object_dir)))
| NONE =>
if is_pervasive
then
load_src
(true, error_info,
findPervasiveSource module_id,
unit, object_dir)
else
load_src
(false, error_info,
findSource (files, module_id),
unit, object_dir)
fun getObject (unit as UNIT {source = src_info, ...}, module_id,
is_pervasive, library_path, object_dir) =
if is_pervasive
then
findPervasiveObject module_id
else
let
val lib_path_opt =
if isSome (!src_info) then NONE else SOME library_path
in
findObject (object_dir, lib_path_opt, module_id)
end
fun check_obj (unit as UNIT {object, ...}, module_id,
is_pervasive, library_path, object_dir, location) =
let
val get_object = getObject(unit, module_id,
is_pervasive, library_path, object_dir)
in
case !object of
SOME {file, time_stamp, file_time, stamps, consistency} =>
(let val mod_time = FileTime.modTime file
in
case get_object of
NONE => load_object NONE unit
| SOME (obj_file, obj_time) =>
if (obj_file = file) then
if time_stamp = mod_time then
()
else
load_object (SOME (file, mod_time)) unit
else
load_object (SOME (obj_file, obj_time)) unit
end handle OS.SysErr _ =>
(mesg_fn (location,
"Object file " ^ file ^ " has disappeared");
load_object get_object unit))
| NONE =>
load_object get_object unit
end
fun check_module (module_id, is_pervasive,
units, files, subprojects, library_path, object_dir, error_info, location) =
case NewMap.tryApply' (units, module_id) of
SOME unit =>
(check_src (unit, module_id, is_pervasive, files, error_info, location, object_dir);
check_obj (unit, module_id, is_pervasive, library_path, object_dir, location);
(units, SOME unit))
| NONE =>
if is_pervasive
then
let
val sml_info = findPervasiveSource module_id
val mo_info = findPervasiveObject module_id
val (units', unit) =
new_unit
error_info
(true, units, module_id, sml_info, mo_info, NONE, object_dir)
in
(units', SOME unit)
end
else
let
val unit =
foldl combineOpt
NONE
(map (fn p => get_unit(p, module_id)) subprojects)
val returnUnits =
if isSome(unit) then
(units, NONE)
else
let
val src_info = findSource (files, module_id)
val _ = diagnostic (3, fn _ => ["calling new_unit"]);
val (units', unit) =
new_unit
error_info
(false, units, module_id, src_info, NONE, NONE, object_dir);
val lib_path_opt =
if isSome src_info then NONE else SOME library_path
in
diagnostic (3, fn _ => ["calling load_object"]);
load_object
(findObject (object_dir, lib_path_opt, module_id))
unit;
(units', SOME unit)
end
in
returnUnits
end
fun do_read_dependencies
(error_info, location)
(PROJECT {name, units, files, library_path, object_dir,
subprojects, current_targets, disabled_targets, dependency_info},
module_id, seen_init)
subprojects_read: (Project * StatusMap) =
let
fun getCurTargets (PROJECT {name, current_targets, ...}) =
if null current_targets then
no_targets (error_info, location) name
else
map (fn t => ModuleId.from_host(t, location)) current_targets
fun read_subproj_dependencies proj =
let
val (pervasive_proj, pervasive_smap) =
do_read_dependencies (error_info, location)
(proj, Io.pervasive_library_id, empty_map)
true
fun read_sub_dep (m, (proj, smap)) =
do_read_dependencies (error_info, location)
(proj, m, smap)
false
in
#1 (foldl read_sub_dep (pervasive_proj, pervasive_smap) (getCurTargets proj))
end
val subprojects = subprojects
fun read_dependencies'
(location, ancestors)
((units, seen), module_id) =
let
val mod_name = ModuleId.string module_id
val is_pervasive = ModuleId.is_pervasive (module_id)
val _ =
diagnostic (2,
fn _ => ["read_dependencies called with `", mod_name, "'"])
fun sub_units (unit as UNIT {requires, source, object, ...}) =
case !source
of SOME _ => requires_from_unit unit
| NONE =>
case !object
of SOME {consistency = DEPEND_LIST cons, ...} =>
map
(fn {mod_name, ...} =>
ModuleId.from_mo_string (mod_name, Location.UNKNOWN))
cons
| NONE =>
Info.error'
error_info
(Info.FATAL, location,
"In project: " ^ name ^ ", no such unit exists: " ^ mod_name)
in
if Lists.member (mod_name, ancestors) then
Info.error'
error_info
(Info.FATAL, Info.Location.FILE mod_name,
concat
("Circular require structure within"
:: map (fn x => "\n" ^ x) ancestors))
else
case NewMap.tryApply'(seen, module_id)
of SOME _ =>
(diagnostic (3, fn _ => ["seen it!"]);
(units, seen))
| NONE =>
(case
check_module (module_id, is_pervasive,
units, files, subprojects, library_path, object_dir, error_info, location)
of (units', SOME unit) =>
Lists.reducel
(read_dependencies'
(Location.FILE mod_name, mod_name :: ancestors))
((units', mark_visited (seen, module_id)),
sub_units unit)
| (units', NONE) => (units', mark_visited (seen, module_id)))
handle Io.NotSet _ =>
Info.error'
error_info
(Info.FATAL, location, "Pervasive directory not set")
end
val (units', seen') =
read_dependencies'
(location, [])
((units, seen_init), module_id)
in
(PROJECT
{name = name,
units = units',
files = files,
library_path = library_path,
object_dir = object_dir,
subprojects = subprojects,
disabled_targets = disabled_targets,
current_targets = current_targets,
dependency_info = dependency_info},
seen')
end
fun read_object_dependencies
(error_info, location)
(proj as PROJECT {name, units, files, library_path, object_dir,
subprojects, current_targets, disabled_targets, dependency_info},
module_id, seen_init): Project * StatusMap =
let
fun getCurTargets (PROJECT {name, current_targets, ...}) =
if null current_targets then
no_targets (error_info, location) name
else
map (fn t => ModuleId.from_host(t, location)) current_targets
fun read_subproj_dependencies subproj =
let
val (pervasive_proj, pervasive_smap) =
do_read_dependencies (error_info, location)
(subproj, Io.pervasive_library_id, empty_map)
true
fun read_sub_dep (m, (proj, smap)) =
do_read_dependencies (error_info, location)
(proj, m, smap)
false
in
#1 (foldl read_sub_dep (pervasive_proj, pervasive_smap) (getCurTargets proj))
end
val subprojects =
map read_subproj_dependencies subprojects
fun read_object_dependencies'
(location, ancestors)
(module_id, (units, seen)) =
let
val mod_name = ModuleId.string module_id
val _ =
diagnostic (2,
fn _ => ["read_object_dependencies called with `",
mod_name, "'"])
fun getObject' (unit as UNIT {source = src_info, ...}) =
let
val lib_path_opt =
if isSome (!src_info) then NONE else SOME library_path
in
findObject (object_dir, lib_path_opt, module_id)
end
fun check_obj' (unit, object) =
case !object
of SOME {file, time_stamp, consistency, file_time, stamps} =>
(let
val mod_time = FileTime.modTime file
val get_object = getObject' unit
in
case get_object of
NONE => load_object NONE unit
| SOME (obj_file, obj_time) =>
if (obj_file = file) then
if time_stamp = mod_time then
()
else
load_object (SOME (file, mod_time)) unit
else
load_object (SOME (obj_file, obj_time)) unit
end handle OS.SysErr _ =>
(mesg_fn (location,
"Object file " ^ file ^ " has disappeared");
load_object
(findObject (object_dir, SOME library_path, module_id))
unit))
| NONE =>
load_object
(findObject (object_dir, SOME library_path, module_id))
unit
fun check_module' () =
case NewMap.tryApply' (units, module_id)
of SOME (unit as UNIT {object, ...}) =>
(check_obj' (unit, object);
(units, SOME unit))
| NONE =>
let
val unit =
foldl combineOpt
NONE
(map (fn p => get_unit(p, module_id)) subprojects)
in
case unit of
NONE =>
let
val (units', unit) =
new_unit
error_info
(false, units, module_id, NONE, NONE, NONE, object_dir);
in
case findObject (object_dir, SOME library_path, module_id)
of mo_info as SOME _ =>
(load_object mo_info unit;
(units', SOME unit))
| NONE =>
Info.error'
error_info
(Info.FATAL, Info.Location.FILE mod_name,
"No object file.")
end
| SOME u =>
(units, NONE)
end
fun recurse ((units, seen),
{mod_name = sub_name, time}) =
read_object_dependencies'
(Location.FILE mod_name, mod_name :: ancestors)
(ModuleId.from_mo_string (sub_name, Location.FILE mod_name),
(units, mark_visited (seen, module_id)))
in
if Lists.member (mod_name, ancestors) then
Info.error'
error_info
(Info.FATAL, Info.Location.FILE mod_name,
concat
("Circular require structure within"
:: map (fn x => "\n" ^ x) ancestors))
else
case NewMap.tryApply'(seen, module_id)
of SOME _ =>
(diagnostic (3, fn _ => ["seen it!"]);
(units, seen))
| NONE =>
(case check_module' () of
(units',
SOME (UNIT {object =
ref (SOME {consistency = DEPEND_LIST (_ :: const),
...}),
...})) =>
Lists.reducel recurse ((units', seen), const)
| (units', NONE) => (units', seen)
| _ =>
Info.error'
error_info
(Info.FATAL, Info.Location.FILE mod_name,
"Invalid consistency information"))
handle Io.NotSet _ =>
Info.error'
error_info
(Info.FATAL, location, "Pervasive directory not set")
end
fun check_src' (unit, src_info) =
case !src_info
of sml_info as SOME (filename, time_stamp) =>
(let
val mod_time = FileTime.modTime filename
in
if time_stamp = mod_time then
()
else
load_src
(false, error_info, SOME (filename, mod_time), unit, object_dir)
end handle OS.SysErr _ =>
(mesg_fn (location,
"Source file " ^ filename ^ " has disappeared");
load_src
(false, error_info,
findSource (files, module_id),
unit, object_dir)))
| NONE =>
load_src
(false, error_info,
findSource (files, module_id),
unit, object_dir)
val (units, UNIT unit) =
case NewMap.tryApply' (units, module_id)
of SOME (unit as UNIT {source, ...}) =>
(check_src' (unit, source);
(units, unit))
| NONE =>
let
val sml_info =
findSource (files, module_id)
in
new_unit
error_info
(false, units, module_id, sml_info, NONE, NONE, object_dir)
end
val (units', seen') =
let
val mod_name = ModuleId.string module_id
fun recurse ((units, seen), require_id) =
read_object_dependencies'
(Location.FILE mod_name, [mod_name])
(require_id, (units, mark_visited (seen, module_id)))
in
Lists.reducel recurse ((units, seen_init),
requires_from_unit (UNIT unit))
end
in
(PROJECT
{name = name,
units = units',
files = files,
library_path = library_path,
object_dir = object_dir,
subprojects = subprojects,
current_targets = current_targets,
disabled_targets = disabled_targets,
dependency_info = dependency_info},
seen')
end
fun compare_timestamp (t1, t2) =
let
val diff =
if Time.<(t1, t2) then Time.-(t2, t1) else Time.-(t1, t2)
val rdiff = Time.toReal diff
in
rdiff < real(5)
end
fun check_one_mo (location, time, UNIT unit, sub_module_name) =
case !(#object unit)
of SOME {time_stamp, ...} =>
if Real.abs(Time.toReal time - Time.toReal time_stamp) < 1.0 then
(diagnostic (3, fn _ => ["Time OK for `", sub_module_name, "'"]);
true)
else
(diagnostic (3, fn _ => ["Time mismatch for `", sub_module_name, "'"]);
false)
| NONE =>
(mesg_fn (location,
"can't find object info for `" ^ sub_module_name ^ "'");
false)
fun check_one_loaded
(location, load_time', UNIT unit, sub_module_name) =
case !(#loaded unit)
of SOME {load_time, ...} =>
if load_time = load_time' then
(diagnostic (2, fn _ => ["load time OK for `", sub_module_name, "'"]);
true)
else
(diagnostic (2,
fn _ => ["old load time for `", sub_module_name, "'"]);
false)
| NONE =>
(mesg_fn (location,
"can't find loaded info for `" ^ sub_module_name ^"'");
false)
fun check_sub_unit
(error_info, location, project, check_perv, check_normal)
{mod_name, time} =
case ModuleId.from_string' mod_name
of NONE =>
(mesg_fn
(location,
"invalid module name `" ^ mod_name ^ "' -- treating as out of date");
false)
| SOME sub_module_id =>
case get_unit (project, sub_module_id)
of SOME unit =>
if ModuleId.is_pervasive sub_module_id then
(diagnostic (3, fn _ => ["Calling check_perv for `", mod_name, "'"]);
check_perv (location, time, unit, mod_name))
else
check_normal (location, time, unit, mod_name)
| NONE =>
(diagnostic (1,
fn _ => ["Module name `", mod_name,
"' from object file not found in project\n",
"-- treating as out of date"]);
false)
fun check_dependencies
(error_info, location)
(proj, dependencies, check_one) =
Lists.forall
(check_sub_unit (error_info, location, proj, check_one, check_one))
dependencies
fun always_valid _ _ args = args
fun is_valid_object
(error_info, location)
(proj, level, module_id, UNIT unit)
args =
case !(#object unit) of
NONE =>
Info.error'
error_info
(Info.FATAL, location,
"No object file for : `" ^ ModuleId.string module_id ^ "'")
| SOME {file_time, time_stamp, consistency = DEPEND_LIST cons, ...} =>
(case
Lists.findp
(not o
(check_sub_unit
(error_info, location, proj, check_one_loaded, check_one_mo)))
cons of
{mod_name, ...} =>
Info.error'
error_info
(Info.FATAL, location,
"Object file '" ^ ModuleId.string module_id ^
"' is out of date with respect to `" ^ mod_name ^ "'"))
handle
Lists.Find =>
(diagnostic (3, fn _ => ["object is valid"]);
args)
fun check_compile_times
(error_info, location)
(project, module_id, UNIT unit) =
case !(#source unit)
of NONE =>
(diagnostic (2, fn _ => [" no source unit"]);
true)
| SOME (s_file, s_time) =>
case !(#object unit)
of SOME {time_stamp, consistency = DEPEND_LIST dependencies, ...} =>
if compare_timestamp (time_stamp, s_time) then
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id, "': source stamp OK"]);
check_dependencies
(error_info, location)
(project, dependencies, check_one_mo))
else
false
| NONE =>
(diagnostic (2, fn _ => [" no object file"]);
false)
fun check_object_load_times
(error_info, location)
(project, module_id, UNIT unit) =
case !(#object unit)
of NONE =>
Crash.impossible
("No object file for : `" ^ ModuleId.string module_id ^ "'")
| SOME {file_time, time_stamp, consistency = DEPEND_LIST cons, ...} =>
case !(#loaded unit)
of SOME {file_time = OBJECT time,
dependencies = DEPEND_LIST deps, ...} =>
if time = time_stamp then
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': object stamp OK"]);
check_dependencies
(error_info, location)
(project, deps, check_one_loaded))
else
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': object stamp out of date"]);
false)
| SOME {file_time = SOURCE time,
dependencies = DEPEND_LIST deps, ...} =>
(case !(#source unit)
of NONE =>
(diagnostic (2,
fn _ =>
["No source file for loaded compilation unit in load_object: ",
ModuleId.string module_id]);
true)
| SOME (s_file, s_time) =>
if compare_timestamp (time, s_time) then
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': source stamp OK"]);
if compare_timestamp (time_stamp, s_time) then
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': object stamp OK"]);
check_dependencies
(error_info, location)
(project, deps, check_one_loaded))
else
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': old object stamp"]);
false))
else
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': old object stamp"]);
false))
| NONE =>
(diagnostic (2, fn _ => [" not loaded"]);
false)
fun check_source_load_times
(error_info, location)
(project, module_id, UNIT unit) =
case !(#loaded unit)
of SOME {file_time = SOURCE time, dependencies = DEPEND_LIST deps, ...} =>
(case !(#source unit)
of NONE =>
(diagnostic (2,
fn _ =>
["No source file for loaded compilation unit in load_source: ",
ModuleId.string module_id]);
true)
| SOME (s_file, s_time) =>
if time = s_time then
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id, "': source stamp OK"]);
check_dependencies
(error_info, location)
(project, deps, check_one_loaded))
else
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id, "': old source stamp"]);
false))
| SOME {file_time = OBJECT time, dependencies = DEPEND_LIST deps, ...} =>
(case !(#object unit)
of NONE =>
(diagnostic (2,
fn _ =>
["No object file for loaded compilation unit in load_source: ",
ModuleId.string module_id]);
true)
| SOME {file_time, time_stamp, ...} =>
if time = time_stamp then
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id, "': object stamp OK"]);
case !(#source unit)
of NONE =>
(diagnostic (2, fn _ =>
["No source file for `", ModuleId.string module_id,
"' assuming object file up to date"]);
true)
| SOME (s_file, s_time) =>
if compare_timestamp (time_stamp, s_time) then
(diagnostic (2, fn _ =>
["`", ModuleId.string module_id, "': file time OK"]);
check_dependencies
(error_info, location)
(project, deps, check_one_loaded))
else
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id,
"': old file time"]);
false))
else
(diagnostic (2,
fn _ => ["`", ModuleId.string module_id, "': old object stamp"]);
false))
| NONE =>
(diagnostic (2, fn _ => [" not loaded"]);
false)
fun get_sub_mos (UNIT unit) =
case !(#object unit)
of NONE => []
| SOME {consistency = DEPEND_LIST l, ...} =>
map
(fn {mod_name, ...} =>
ModuleId.from_mo_string (mod_name, Location.UNKNOWN))
l
fun check_visited_map([], _) = true
| check_visited_map(x :: xs, visited) =
case NewMap.tryApply'(visited, x)
of NONE => check_visited_map(xs, visited)
| SOME true => check_visited_map(xs, visited)
| SOME false => false
fun check_dep
(error_info, level, get_sub_units,
check_times, check_validity, project as PROJECT {units, subprojects, ...})
((out_of_date, visited, real_ids), module_id) =
let
val mod_name = ModuleId.string module_id
val _ =
diagnostic (2,
fn _ => [Int.toString level, " `", mod_name, "'"]);
fun undefined_module mod_id =
Crash.impossible ("Undefined module: " ^ ModuleId.string mod_id)
fun get_defined_unit mod_id =
case get_unit (project, mod_id)
of SOME unit => unit
| NONE => undefined_module mod_id
val UNIT unit = get_defined_unit module_id
val real_mod_id = #name unit
val unit_time = case !(#object unit) of
NONE => NONE
| SOME{file_time, ...} => SOME file_time
fun check_object_time module_id =
case unit_time of
NONE => false
| SOME unit_time =>
let
val UNIT sub_unit = get_defined_unit module_id
in
case !(#object sub_unit) of
NONE => false
| SOME{file_time, ...} => Time.>=(unit_time, file_time)
end
in
if NewMap.exists (fn (m, _) => ModuleId.eq(m,real_mod_id)) visited then
(out_of_date, visited, real_mod_id :: real_ids)
else
let
val (out_of_date, visited, _) =
check_validity
(error_info, Location.FILE mod_name)
(project, level, module_id, UNIT unit)
(out_of_date, visited, [])
val sub_units = get_sub_units (UNIT unit)
val (out_of_date_now, visited_now, real_sub_ids) =
Lists.reducel
(check_dep
(error_info, level + 1, get_sub_units,
check_times, check_validity, project))
((out_of_date, visited, []), sub_units)
in
if check_visited_map (real_sub_ids, visited_now) andalso
check_times
(error_info, Location.FILE mod_name)
(project, module_id, UNIT unit) andalso
Lists.forall check_object_time sub_units
then
(out_of_date_now,
mark_compiled (visited_now, real_mod_id),
real_mod_id :: real_ids)
else
(real_mod_id :: out_of_date_now,
mark_visited (visited_now, real_mod_id),
real_mod_id :: real_ids)
end
end
fun list_units (PROJECT {units, ...}) =
Lists.msort
(fn ((s, _), (s', _)) => s < s')
(map (fn m => (ModuleId.string m, m)) (NewMap.domain units))
fun initialize (error_info, location) =
let
val project =
PROJECT
{name = "",
units = NewMap.empty (ModuleId.lt, ModuleId.eq),
files = [],
library_path = [],
object_dir = {base = "", config = "", mode = ""},
subprojects = [],
current_targets = [],
disabled_targets = [],
dependency_info = DEPEND([],[])}
in
#1 (do_read_dependencies
(error_info, location)
(project, Io.pervasive_library_id, empty_map) false)
end
fun delete (PROJECT {name, units, files, library_path, object_dir,
subprojects, current_targets, disabled_targets, dependency_info},
module_id,
delete_from_sub) =
PROJECT
{name = name,
units = NewMap.undefine (units, module_id),
files = files,
library_path = library_path,
object_dir = object_dir,
subprojects = if delete_from_sub then
map (fn p => delete (p, module_id, true)) subprojects
else
subprojects,
current_targets = current_targets,
disabled_targets = disabled_targets,
dependency_info = DEPEND([],[])}
fun display_dependency_info (info as h :: t) =
let
open MLWorks.Internal.Array
val _ = print "Displaying dependencies\n"
val max_node =
foldl (fn ((set, GD.DAG {seq_no,...}),max) => Int.max(max, seq_no))
0 info
val info_array = array(max_node + 1, h)
val _ = app (fn info_node as (set, GD.DAG {seq_no,...}) =>
update(info_array, seq_no, info_node)) info
fun display_dependency
(set, GD.DAG {seq_no,marked,smlsource,symmap,intern,extern}) =
(print(ModuleId.string smlsource); print " (";
print (ImportExport.ModuleName.setToString set);
print ")\n";
app (fn GD.DAG {smlsource, ...} =>
print ("  -> " ^ (ModuleId.string smlsource) ^ "\n"))
(OrderedSet.makelist intern)
)
in
app display_dependency info;
print "Done\n"
end
| display_dependency_info [] = ()
fun calculate_dependency_info (name, files, units, all_targets, env_fn) =
let val _ = diagnostic (1, fn _ => ["Calculating dependencies for ",
name])
val all_target_mids =
map (fn t => ModuleId.from_host(t, Location.FILE t))
all_targets
fun is_target m =
List.exists (fn m' => ModuleId.eq(m,m')) all_target_mids
fun cvt m =
let val UNIT {mod_decls, ...} = valOf(NewMap.tryApply' (units, m))
in (m, #1(!mod_decls), is_target m) end
handle exn =>
(print "cvt error!\n";
print("Looking for " ^ (ModuleId.string m) ^ "\n");
app (fn m => print("  " ^ (ModuleId.string m) ^ "\n"))
(NewMap.domain units);
raise exn)
val units_list =
map cvt
(Lists.filterp (not o ModuleId.is_pervasive) (NewMap.domain units))
fun eq_dag (GD.DAG { seq_no = s1, ... }: dag,
GD.DAG { seq_no = s2, ... }) = s1 = s2
fun lt_dag (GD.DAG { seq_no = s1, ... }: dag,
GD.DAG { seq_no = s2, ... }) = s1 < s2
val { union = union_dag, addl = addl_dag, makeset = makeset_dag, ... } =
OrderedSet.gen { eq = eq_dag, lt = lt_dag }
val dependencies =
GD.analyze { union_dag = union_dag,
smlsources = units_list,
enone = empty_mid_set,
eglob = env_fn,
ecombine = union_mid_set,
seq_no = ref 0 }
fun unit_of m = valOf(NewMap.tryApply' (units, m))
fun update_requires
(set, GD.DAG {seq_no,marked,smlsource,symmap,intern,extern}) =
let val UNIT unit = unit_of smlsource
val {implicit, explicit, subreqs} = !(#requires unit)
val implicit =
map (fn GD.DAG {smlsource, ...} => smlsource)
(OrderedSet.makelist intern)
val subreqs = OrderedSet.makelist extern
fun eq m1 m2 = ModuleId.eq(m1,m2)
val explicit =
List.filter (not o (member_mid_set extern)) explicit
val (_, partial_info) = !(#mod_decls unit)
in case explicit of
[] => #requires unit := {explicit=[], implicit=implicit,
subreqs=subreqs}
| _ =>
let fun print_warning (m, missing, definite) =
if partial_info then () else
( print ( "Require of "
^ (ModuleId.string m)
^ " in unit "
^ (ModuleId.string smlsource));
if missing
then
print " is missing.\n"
else if definite then
print " is unnecessary.\n"
else
print " may be unnecessary.\n" )
in
app (fn m =>
if List.exists (eq m) implicit
then ()
else
case NewMap.tryApply' (units, m) of
NONE =>
if true
then ()
else print_warning(m,false,true)
| SOME _ =>
print_warning(m,false,false))
explicit;
app (fn m =>
if List.exists (eq m) explicit
then ()
else print_warning(m,true,false))
implicit;
#requires unit :=
{explicit=explicit, implicit=implicit, subreqs=subreqs}
end
end
in app update_requires dependencies;
dependencies
end handle _ => []
fun read_dependencies info (p, m, smap) =
let
val _ =
diagnostic (1,
fn _ => ["Calling read_dependencies on pervasive library"]);
val (p', smap') =
do_read_dependencies
info
(p, Io.pervasive_library_id, smap)
true
in
diagnostic (1,
fn _ => ["Calling read_dependencies on " ^ ModuleId.string m]);
do_read_dependencies info (p', m, smap') false
end
fun check_load_objects (error_info, location) (project, module_id) =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, get_sub_mos,
check_object_load_times, is_valid_object, project)
(([], visited_pervasives, []), module_id)
in
rev out_of_date
end
fun source_exists
(error_info, location)
(project, level, module_id, UNIT unit)
(out_of_date, visited, real_ids) =
case !(#source unit)
of NONE =>
let
val sub_units = get_sub_mos (UNIT unit)
in
Lists.reducel
(check_dep
(error_info, level + 1, get_sub_mos,
check_object_load_times, is_valid_object, project))
((out_of_date, visited, []), sub_units)
end
| _ => (out_of_date, visited, real_ids)
fun check_load_source (error_info, location) (project, module_id) =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, requires_from_unit,
check_source_load_times, source_exists, project)
(([], visited_pervasives, []), module_id)
in
rev out_of_date
end
fun check_compiled (error_info, location) (project, module_id) =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, requires_from_unit,
check_compile_times, always_valid, project)
(([], visited_pervasives, []), module_id)
in
rev out_of_date
end
fun check_compiled'
(error_info, location) (project, module_id) (out_of_date, visited) =
let
val (out_of_date_now, visited_now, _) =
check_dep
(error_info, 0, requires_from_unit,
check_compile_times, always_valid, project)
((rev out_of_date, visited, []), module_id)
in
(rev out_of_date_now, visited_now)
end
fun check_perv_compiled (error_info, location) project =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, requires_from_unit,
check_compile_times, always_valid, project)
(([], empty_map, []), Io.pervasive_library_id)
in
rev out_of_date
end
fun check_perv_loaded (error_info, location) project =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, requires_from_unit,
check_source_load_times, always_valid, project)
(([], empty_map, []), Io.pervasive_library_id)
in
rev out_of_date
end
fun allObjects (error_info, location) (project, module_id) =
let
val (out_of_date, _, _) =
check_dep
(error_info, 0, get_sub_mos,
fn _ => fn _ => false, always_valid, project)
(([], empty_map, []), module_id)
in
rev out_of_date
end
local
fun reset_file_info (_, UNIT unit) =
let
val r = #loaded unit
in
case !(#object unit) of
SOME {time_stamp, file_time = object_time, ...} =>
(case !r of
SOME {basis, id_cache, module, dependencies, ...} =>
r := SOME
{file_time = OBJECT object_time,
load_time = time_stamp,
basis = basis,
id_cache = id_cache,
module = module,
dependencies = dependencies}
| NONE =>
Crash.impossible
("Can't find loaded info for `"
^ ModuleId.string (#name unit)
^ "' in reset_pervasives"))
| NONE =>
Crash.impossible
("Can't find object file for `"
^ ModuleId.string (#name unit)
^ "' in reset_pervasives");
#object unit := NONE;
#source unit := NONE;
#requires unit := {implicit=[],explicit=[],subreqs=[]}
end
in
fun reset_pervasives (PROJECT {units, ...}) =
NewMap.iterate
reset_file_info
units
end
local
fun reset_file_info (_, UNIT unit) =
(#object unit := NONE;
#source unit := NONE;
#requires unit := {implicit=[],explicit=[],subreqs=[]})
in
fun remove_file_info (PROJECT {units, subprojects, ...}) =
(NewMap.iterate
reset_file_info
units;
app remove_file_info subprojects)
end
fun pad_out_units(files, units, updating, library_path, object_dir, error_info) =
let fun pad ([], units) = units
| pad (filename::t, units) =
let val location = Location.FILE filename
val module_id =
ModuleId.from_host (OS.Path.file filename, location)
in case NewMap.tryApply' (units, module_id) of
SOME unit =>
if updating
then
( check_src (unit, module_id, false,
files, error_info, location, object_dir);
check_obj (unit, module_id, false,
library_path, object_dir, location);
pad(t, units))
else
pad(t, units)
| NONE =>
let val src_info =
let
val mod_time = FileTime.modTime filename
in
SOME (filename, mod_time)
end
handle OS.SysErr _ => NONE
val (units', unit) =
new_unit
error_info
(false, units, module_id, src_info,
NONE, NONE, object_dir);
val lib_path_opt =
if isSome src_info
then NONE else SOME library_path
in
load_object
(findObject (object_dir, lib_path_opt, module_id))
unit;
pad(t, units')
end
end
val units' = pad(files, units)
val (units_plus_builtin,_) =
check_module(Io.builtin_library_id, true,
units',files,[],library_path,object_dir,
error_info, Location.UNKNOWN)
val (units_plus_builtin_plus_pervasive,_) =
check_module(Io.pervasive_library_id, true,
units_plus_builtin,files,[],library_path,object_dir,
error_info, Location.UNKNOWN)
in
units_plus_builtin_plus_pervasive
end
fun partition_dependencies current_targets dependencies =
let
val current_target_mids =
map (fn t => ModuleId.from_host(t, Location.FILE t))
current_targets
fun is_current_target m =
List.exists (fn m' => ModuleId.eq(m,m')) current_target_mids
in
List.partition
(fn (_, GD.DAG{smlsource, ...}) => is_current_target smlsource)
dependencies
end
fun lookup_function units =
let
fun cvt m =
let val UNIT {mod_decls, ...} = valOf(NewMap.tryApply' (units, m))
in (m, #1(!mod_decls)) end
val (pervasives', _) =
Lists.partition ModuleId.is_pervasive (NewMap.domain units)
val pervasives_list = map cvt pervasives'
fun mk_lookup (name, decl, default) =
ImportExport.imports (decl, empty_mid_set, default, union_mid_set, name)
val Compiler.BASIS{lambda_environment, ...} =
Compiler.initial_basis
val initial_env_fn =
fn m => raise ImportExport.Undefined m
val env_fn =
case pervasives_list of
[(builtin, builtin_dec), (pervasive, pervasive_dec)] =>
let val env_fn' =
let val (f, i, _) =
mk_lookup(ModuleId.string builtin,
builtin_dec, initial_env_fn)
in fn m => (f m,i)
end
val env_fn'' =
let val (f, i, _) =
mk_lookup(ModuleId.string pervasive,
pervasive_dec, env_fn')
in fn m => (f m,i)
end
in
fn m => env_fn'' m
handle ImportExport.Undefined _ =>
env_fn' m
handle ImportExport.Undefined _ =>
initial_env_fn m
end
| _ => initial_env_fn
fun lookup subprojects modulename =
let open ImportExport
fun search [] =
env_fn modulename
| search ((project as
PROJECT {dependency_info = DEPEND (depinfo,_),
...})
:: t) =
let fun find_in [] = search t
| find_in ((set, GD.DAG {symmap,smlsource, ...}) :: rest) =
if ModuleName.memberOf set modulename
then (symmap modulename, singleton_mid_set smlsource)
else find_in rest
in find_in depinfo end
in search subprojects
end
in lookup
end
fun fromFileInfo
(error_info, loc)
(proj as (PROJECT {units, subprojects, ...})) =
let
fun getFullFilename (filename, dirName) =
let
val local_name = OS.Path.fromUnixPath filename
val abs_name = OS.Path.mkAbsolute {path=local_name, relativeTo=dirName}
in
abs_name
end
val (projName, projDir) =
(getOpt (ProjFile.getProjectName(), ""),
ProjFile.getProjectDir())
val projectName = getFullFilename (projName, projDir)
val (curTargets, disTargets, _) =
ProjFile.getTargets ()
val (libraryPath, objectsLoc, binariesLoc) =
ProjFile.getLocations ()
val (_, configDetails, currentConfig) = ProjFile.getConfigurations ()
val (_, modeDetails, currentMode) = ProjFile.getModes ()
fun toAbs s = OS.Path.mkAbsolute {path=s, relativeTo=projDir}
val common_files = ProjFile.getFiles()
val (filesC, libraryC, binariesC, config_ext) =
case currentConfig of
NONE => (common_files, libraryPath, binariesLoc, "")
| SOME name =>
(case ProjFile.getConfigDetails (name, configDetails) of
{files, library, ...} =>
(files @ common_files,
library @ libraryPath,
OS.Path.concat[binariesLoc, name],
name))
val (files, library, binaries, mode_ext) =
case currentMode of
NONE => (filesC, libraryC, binariesC, config_ext)
| SOME modeName =>
(case ProjFile.getModeDetails (modeName, modeDetails) of
{location, ...} =>
(filesC, libraryC,
OS.Path.concat[binariesC, !location],
!location))
val (abs_files, abs_library, abs_objects, abs_binaries) =
if OS.Path.isRelative projDir then
(diagnostic (1, fn _ => ["Project Dir is relative: ", projDir]);
(files, library,
{base = objectsLoc, config = config_ext, mode = mode_ext},
binaries))
else
(diagnostic (1, fn _ => ["Project Dir is absolute: ", projDir]);
(map toAbs files,
map toAbs library,
{base = toAbs objectsLoc, config = config_ext, mode = mode_ext},
toAbs binaries))
fun mkdir' s =
(if OS.FileSys.isDir(s) then
()
else
Info.error' error_info
(Info.FATAL, loc,
s ^ " is not a valid directory or is inaccessable."))
handle OS.SysErr _ =>
(mkdir' (OS.Path.getParent s); OS.FileSys.mkDir s);
fun mkdir s = mkdir' (OS.Path.fromUnixPath s)
val mk_obj_dir =
let val {base, config, mode} = abs_objects
in mkdir (OS.Path.mkCanonical(OS.Path.concat[base, config, mode]))
end
fun get_proj_units projName (PROJECT {name, subprojects, units, ...}) =
if (projName = name) then
SOME units
else get_next_proj_units projName subprojects
and get_next_proj_units projName [] = NONE
| get_next_proj_units projName (p::rest) =
case (get_proj_units projName p) of
SOME u => SOME u
| NONE => get_next_proj_units projName []
val units' = pad_out_units(abs_files, units, false, abs_library, abs_objects, error_info)
val lookup = lookup_function units'
val project_cache = ref empty_project_cache
fun getSubDetails config filename =
let val filename = getFullFilename (filename, projDir)
val {name, files, libraryPath, objectsLoc, binariesLoc,
configDetails, modeDetails, subprojects,
curTargets, disTargets, currentMode, ...} =
ProjFile.peek_project filename
in case NewMap.tryApply' (!project_cache,
{filename = filename, targets = curTargets}) of
SOME subproject => subproject
| NONE =>
let
val {name=configName, files=filesC, library=libraryC} =
ProjFile.getConfigDetails (getOpt(config, ""),
configDetails)
handle ProjFile.NoConfigDetailsFound c =>
{name = "", files = [], library = []}
val modeName = getOpt(currentMode, "")
val {location, ...} =
ProjFile.getModeDetails (modeName, modeDetails)
handle ProjFile.NoModeDetailsFound m =>
Info.error' error_info
(Info.FATAL, loc,
"No mode found or none set when getting details of sub projects")
val dir = OS.Path.dir name
fun mk_abs f = OS.Path.mkAbsolute {path=f, relativeTo=dir}
val abs_files = map mk_abs (files @ filesC)
val abs_library = map mk_abs (libraryPath @ libraryC)
val abs_objects =
{base =
OS.Path.mkAbsolute {path=objectsLoc, relativeTo=dir},
config = configName, mode = !location }
val abs_binaries =
OS.Path.mkAbsolute
{path=OS.Path.concat[binariesLoc, configName, !location],
relativeTo=dir}
val subprojDir = OS.Path.dir filename
fun getProjName n = getFullFilename (n, subprojDir)
val subprojects = map getProjName subprojects
val units = getOpt (get_proj_units name proj, NewMap.empty (ModuleId.lt, ModuleId.eq))
val units' = pad_out_units(abs_files, units, false, abs_library, abs_objects, error_info)
val subprojects = map (getSubDetails config) subprojects
val dependency_info = DEPEND([],[])
val subproject =
PROJECT {name = name,
units = units',
files = abs_files,
library_path = abs_library,
object_dir = abs_objects,
subprojects = subprojects,
current_targets = curTargets,
disabled_targets = disTargets,
dependency_info = dependency_info}
in
project_cache :=
NewMap.define (!project_cache,
{ filename=filename,
targets=curTargets },
subproject);
subproject
end
end
val subprojects =
if isSome(ProjFile.getProjectName()) then
let val proj_name = valOf(ProjFile.getProjectName())
in
map (getSubDetails currentConfig) (ProjFile.getSubprojects())
end
else []
val dependency_info = DEPEND([],[])
val new_proj =
PROJECT
{name = projectName,
files = abs_files,
library_path = abs_library,
object_dir = abs_objects,
subprojects = subprojects,
current_targets = curTargets,
disabled_targets = disTargets,
dependency_info = dependency_info,
units = units'}
in
ignore(ProjFile.changed());
new_proj
end
fun update_dependencies (error_info, loc) proj =
let
fun update (proj as PROJECT {name, units, files, library_path, object_dir,
subprojects, current_targets, disabled_targets,
dependency_info}) =
let
val units' = pad_out_units(files, units, true, library_path,
object_dir, error_info)
val lookup = lookup_function units'
val (current, other) =
partition_dependencies current_targets
(calculate_dependency_info(name, files, units',
current_targets @ disabled_targets,
lookup subprojects))
val dependency_info' =DEPEND (current,other)
val proj' = PROJECT
{name = name, units = units', files = files,
library_path = library_path, object_dir = object_dir,
subprojects = subprojects, dependency_info = dependency_info',
current_targets=current_targets, disabled_targets=disabled_targets}
val init_proj =
#1 (do_read_dependencies
(error_info, loc)
(proj', Io.pervasive_library_id, empty_map)
true)
fun do_one (target, (p, smap)) =
do_read_dependencies
(error_info, loc)
(p, ModuleId.from_host (target, loc), smap)
false
val result =
if null current_targets then
no_targets (error_info, loc) name
else
#1 (foldl do_one (init_proj, empty_map) current_targets)
in
result
end
in update proj
end
end
;
