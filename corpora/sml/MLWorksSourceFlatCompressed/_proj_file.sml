require "../basis/__list";
require "../basis/__string";
require "../basis/__substring";
require "../basis/__char";
require "^.basis.os";
require "^.basis.text_io";
require "^.utils.terminal";
require "^.utils.getenv";
require "^.main.info";
require "^.utils.diagnostic";
require "^.utils.lists";
require "sectioned_file";
require "proj_file";
functor ProjFile (
structure OS: OS
structure SectionedFile: SECTIONED_FILE
structure TextIO: TEXT_IO
structure Terminal: TERMINAL
structure Getenv: GETENV
structure Info: INFO
structure Lists: LISTS
structure Diagnostic: DIAGNOSTIC
): PROJ_FILE =
struct
type error_info = Info.options
type location = Info.Location.T
val _ = Diagnostic.set 0
fun diagnostic (level, output_function) =
Diagnostic.output
level
(fn verbosity => output_function verbosity)
val STAMP = "MLWorks 2.0"
exception InvalidProjectFile of string
fun get_sectioned_file fname =
let val (STAMP', sf) =
SectionedFile.readSectionedFile fname
handle SectionedFile.InvalidSectionedFile s =>
raise (InvalidProjectFile s)
in if STAMP' = STAMP
then sf
else raise (InvalidProjectFile ("Invalid file header in " ^ fname))
end
val fromUnixPath = OS.Path.fromUnixPath;
val toUnixPath = OS.Path.toUnixPath;
datatype target_type = IMAGE | OBJECT_FILE | EXECUTABLE | LIBRARY
type target_details = (string * target_type)
type target_refs =
{curTargets: string list ref,
disTargets: string list ref,
targetDetails: target_details list ref}
type mode_details =
{name: string,
location: string ref,
generate_interruptable_code: bool ref,
generate_interceptable_code: bool ref,
generate_debug_info: bool ref,
generate_variable_debug_info: bool ref,
optimize_leaf_fns: bool ref,
optimize_tail_calls: bool ref,
optimize_self_tail_calls: bool ref,
mips_r4000: bool ref,
sparc_v7: bool ref}
type mode_refs =
{modes: string list ref,
modeDetails: mode_details list ref,
currentMode: string option ref}
type config_details =
{name: string,
files: string list,
library: string list}
type configuration_refs =
{configs: string list ref,
configDetails: config_details list ref,
currentConfig: string option ref}
type locations =
{libraryPath: string list ref,
objectsLoc: string ref,
binariesLoc: string ref}
type about_refs =
{description: string ref,
version: string ref}
datatype project_file = PROJECT_FILE of
{projectName: string option ref,
projectDir: string ref,
files: string list ref,
subprojects: string list ref,
targets: target_refs,
modes: mode_refs,
configs: configuration_refs,
locations: locations,
about: about_refs}
fun warn_error s = Terminal.output(s ^ "\n")
fun mk_empty_proj () =
PROJECT_FILE {projectName = ref NONE,
projectDir = ref "",
files = ref [],
subprojects = ref [],
about =
{description = ref "",
version = ref ""},
targets =
{curTargets = ref [],
disTargets = ref [],
targetDetails = ref []},
modes =
{modes = ref [],
modeDetails = ref [],
currentMode = ref NONE},
configs =
{configs = ref [],
configDetails = ref [],
currentConfig = ref NONE},
locations =
{libraryPath = ref [],
objectsLoc = ref "",
binariesLoc = ref ""}}
val proj_file_changed = ref false;
fun changed() = !proj_file_changed before (proj_file_changed := false);
fun initialize project_file =
let
val PROJECT_FILE {projectName, projectDir, files, subprojects, targets,
modes, configs, locations, about} = project_file
val {curTargets, disTargets, targetDetails} = targets
val {modes, modeDetails, currentMode} = modes
val {configs, configDetails, currentConfig} = configs
val {libraryPath, objectsLoc, binariesLoc} = locations
val {description, version} = about
in
diagnostic (1, fn _ => ["Initializing project file info"]);
projectName := SOME "";
projectDir := OS.FileSys.getDir();
description := "";
version := "";
files := [];
subprojects := [];
curTargets := [];
disTargets := [];
targetDetails := [];
modes := [];
modeDetails := [];
currentMode := NONE;
configs := [];
configDetails := [];
currentConfig := NONE;
libraryPath := [];
objectsLoc := "objects";
binariesLoc := ""
end
fun getProjectName project_file =
let
val PROJECT_FILE {projectName, ...} = project_file
in
!projectName
end
fun getProjectDir project_file =
let
val PROJECT_FILE {projectName, projectDir, ...} = project_file
in
case !projectName of
NONE =>
(diagnostic (2, fn _ => ["Project Name is not set"]);
OS.FileSys.getDir())
| SOME _ => !projectDir
end
fun getAboutInfo project_file =
let
val PROJECT_FILE {about, ...} = project_file
val {description, version} = about
in
(!description, !version)
end
fun setAboutInfo project_file (descStr, verStr) =
let
val PROJECT_FILE {about, ...} = project_file
val {description, version} = about
in
proj_file_changed := true;
description := descStr;
version := verStr
end
fun getLocations project_file =
let
val PROJECT_FILE {locations, ...} = project_file
val {libraryPath, objectsLoc, binariesLoc} = locations
in
(!libraryPath, !objectsLoc, !binariesLoc)
end
fun setLocations project_file (libPath, obj, bin) =
let
val PROJECT_FILE {locations, ...} = project_file
val {libraryPath, objectsLoc, binariesLoc} = locations
in
proj_file_changed := true;
libraryPath := libPath;
objectsLoc := obj;
binariesLoc := bin
end
fun getSubprojects project_file =
let
val PROJECT_FILE {subprojects, ...} = project_file
in
(!subprojects)
end
fun setSubprojects project_file proj_list =
let
val PROJECT_FILE {subprojects, ...} = project_file
in
proj_file_changed := true;
subprojects := proj_list
end
exception NoConfigDetailsFound of string
fun getConfigDetails (configName, detailsList: config_details list) =
let
val details =
List.find (fn {name=n, ...} => n = configName) detailsList
in
if isSome details then valOf(details)
else
raise NoConfigDetailsFound configName
end
exception NoTargetDetailsFound of string
fun getTargets project_file =
let
val PROJECT_FILE {targets, ...} = project_file
val {curTargets, disTargets, targetDetails} = targets
in
(!curTargets, !disTargets, !targetDetails)
end
fun getTargetDetails (name, detailsList) =
let
val details = List.find (fn (n,t) => n = name) detailsList
in
if isSome details then valOf(details)
else
raise NoTargetDetailsFound name
end
exception InvalidTarget of string
fun setTargets project_file (current, disabled, details) =
let
val PROJECT_FILE {targets, files, configs, ...} = project_file
val {curTargets, disTargets, targetDetails} = targets
val {configs, configDetails, currentConfig} = configs
val {name, files = c_files, library} =
case (!currentConfig) of
NONE => {name = "", files = [], library = []}
| SOME cc => getConfigDetails (cc, !configDetails)
handle NoConfigDetailsFound c => {name="", files=[], library=[]}
val total_files = (!files) @ c_files
fun check_targets [] = ()
| check_targets (t::rest) =
if List.exists (fn f => (OS.Path.file f) = t) total_files then
check_targets rest
else
raise InvalidTarget t
in
check_targets current;
check_targets disabled;
proj_file_changed := true;
curTargets := current;
disTargets := disabled;
targetDetails := details
end
fun setCurrentTargets project_file (error_info, location) targetList =
let
val PROJECT_FILE {targets, ...} = project_file
val {curTargets, disTargets, ...} = targets
val _ = disTargets := !curTargets @ !disTargets
val _ = curTargets := []
fun setOne target =
case (List.find (fn s => s=target) (!disTargets))
of SOME _ =>
(curTargets := target :: !curTargets;
disTargets := List.filter (fn s => s<>target) (!disTargets))
| NONE =>
Info.error'
error_info
(Info.FATAL, location, "No such target as " ^ target)
in
proj_file_changed := true;
app setOne (rev targetList)
end
fun remove_invalid_targets project_file =
let
val PROJECT_FILE {files, targets, configs, ...} = project_file
val {curTargets, disTargets, targetDetails} = targets
val {configs, configDetails, currentConfig} = configs
val c_files =
case (!currentConfig) of
NONE => []
| SOME cc => #files (getConfigDetails (cc, !configDetails))
handle NoConfigDetailsFound c => []
val target_files = map OS.Path.file ((!files) @ c_files)
fun remove_t_details (target, details) =
#2 (List.partition (fn (a,_) => a=target) details)
fun remove_targets ([], details) = ([], details)
| remove_targets (target::rest, details) =
let val (targ_test, ts) = List.partition (fn f => f=target) target_files
in
if null targ_test then
remove_targets (rest, remove_t_details (target, details))
else
let val (ts,ds) = remove_targets (rest, details)
in (target::ts, ds)
end
end
val (new_cur, cur_details) = remove_targets (!curTargets, !targetDetails)
val (new_dis, final_details) = remove_targets (!disTargets, cur_details)
in
setTargets project_file (new_cur, new_dis, final_details)
end
fun getConfigurations project_file =
let
val PROJECT_FILE {configs, ...} = project_file
val {configs, configDetails, currentConfig} = configs
in
(!configs, !configDetails, !currentConfig)
end
fun modifyConfigDetails
({name = testName, files = f, library = l},
(detailsList: config_details list)) =
let
val {name=configName, ...} =
case (List.find (fn {name=n, ...} => n=testName) detailsList) of
SOME result => result
| NONE => raise NoConfigDetailsFound testName
val others = List.filter (fn {name=n, ...} => n <> testName) detailsList;
in
{name = configName,
files = f,
library = l} :: others
end
fun setConfigurations project_file (configList, details) =
let
val PROJECT_FILE {configs, ...} = project_file
val {configs, configDetails, currentConfig} = configs
in
proj_file_changed := true;
configs := configList;
configDetails := details;
remove_invalid_targets project_file
end
fun setCurrentConfiguration
project_file (error_info, location) optional_config =
let
val PROJECT_FILE {configs, ...} = project_file
val {configDetails, currentConfig, ...} = configs
in
proj_file_changed := true;
case optional_config of
SOME config =>
(case (List.find (fn {name, ...} => name=config) (!configDetails)) of
SOME _ => currentConfig := SOME config
| NONE =>
Info.error'
error_info
(Info.FATAL, location, "No such configuration as " ^ config))
| NONE => currentConfig := NONE;
remove_invalid_targets project_file
end
exception NoModeDetailsFound of string
fun getModes project_file =
let
val PROJECT_FILE {modes, ...} = project_file
val {modes, modeDetails, currentMode} = modes
in
(!modes, !modeDetails, !currentMode)
end
fun getModeDetails (modeName, detailsList: mode_details list) =
let
val details =
List.find (fn {name=n, ...} => n = modeName) detailsList
in
if isSome details then valOf(details)
else
raise NoModeDetailsFound modeName
end
fun setModes project_file (modeList, details) =
let
val PROJECT_FILE {modes, ...} = project_file
val {modes, modeDetails, ...} = modes
in
proj_file_changed := true;
modes := modeList;
modeDetails := details
end
fun setInitialModes project_file =
let
val debugMode =
{name = "Debug",
location = ref "Debug",
generate_interruptable_code = ref true,
generate_interceptable_code = ref true,
generate_debug_info = ref true,
generate_variable_debug_info = ref true,
optimize_leaf_fns = ref false,
optimize_tail_calls = ref false,
optimize_self_tail_calls = ref false,
mips_r4000 = ref true,
sparc_v7 = ref false}
val releaseMode =
{name = "Release",
location = ref "Release",
generate_interruptable_code = ref true,
generate_interceptable_code = ref false,
generate_debug_info = ref false,
generate_variable_debug_info = ref false,
optimize_leaf_fns = ref true,
optimize_tail_calls = ref true,
optimize_self_tail_calls = ref true,
mips_r4000 = ref true,
sparc_v7 = ref false}
val (modeList, details) =
(["Debug", "Release"],
[debugMode, releaseMode]);
val PROJECT_FILE {modes, ...} = project_file
val {modes, modeDetails, ...} = modes
in
proj_file_changed := true;
modes := modeList;
modeDetails := details
end
fun setCurrentMode project_file (error_info, location) mode =
let
val PROJECT_FILE {modes, ...} = project_file
val {modeDetails, currentMode, ...} = modes
in
proj_file_changed := true;
case (List.find (fn {name, ...} => name=mode) (!modeDetails))
of SOME _ =>
currentMode := SOME mode
| NONE =>
Info.error'
error_info
(Info.FATAL, location, "No such mode as " ^ mode)
end
fun getFiles project_file =
let
val PROJECT_FILE {files, ...} = project_file
in
(!files)
end
fun setFiles project_file new_files =
let
val PROJECT_FILE {files, ...} = project_file
in
proj_file_changed := true;
files := new_files;
remove_invalid_targets project_file
end
fun getFullFilename (filename, dirName) =
let
val local_name = fromUnixPath filename
val abs_name = OS.Path.mkAbsolute {path=local_name, relativeTo=dirName}
in
abs_name
end
fun split2 s =
let val (ss1,ss2) =
Substring.splitl (not o Char.isSpace) (Substring.all s)
val (_, ss2) = Substring.splitl Char.isSpace ss2
in (Substring.string ss1, Substring.string ss2) end
fun item_to_target_item str =
case split2 str of
(name, "EXECUTABLE") => (name, EXECUTABLE)
| (name, "LIBRARY") => (name, LIBRARY)
| (name, "IMAGE") => (name, IMAGE)
| (name, "OBJECT_FILE")=> (name, OBJECT_FILE)
| (name, _) => (name, EXECUTABLE);
fun target_item_to_item (name, EXECUTABLE) = name ^ " " ^ "EXECUTABLE"
| target_item_to_item (name, LIBRARY) = name ^ " " ^ "LIBRARY"
| target_item_to_item (name, IMAGE) = name ^ " " ^ "IMAGE"
| target_item_to_item (name, OBJECT_FILE) = name ^ " " ^ "OBJECT_FILE"
fun newModeDetails name =
{name = name,
location = ref name,
generate_interruptable_code = ref false,
generate_interceptable_code = ref false,
generate_debug_info = ref false,
generate_variable_debug_info = ref false,
optimize_leaf_fns = ref false,
optimize_tail_calls = ref false,
optimize_self_tail_calls = ref false,
mips_r4000 = ref false,
sparc_v7 = ref false}
fun mode_flags_to_refs (r: mode_details) =
[("generate_interruptable_code", #generate_interruptable_code r),
("generate_interceptable_code", #generate_interceptable_code r),
("generate_debug_info", #generate_debug_info r),
("generate_variable_debug_info",#generate_variable_debug_info r),
("optimize_leaf_fns", #optimize_leaf_fns r),
("optimize_tail_calls", #optimize_tail_calls r),
("optimize_self_tail_calls", #optimize_self_tail_calls r),
("mips_r4000", #mips_r4000 r),
("sparc_v7", #sparc_v7 r)]
fun item_to_set_mode_flag mode_flag_refs str =
let val (s, b) = split2 str
in case List.find (fn (s', r) => s = s') mode_flag_refs of
SOME (_, r) => r := (b = "true")
| NONE => () end;
fun mode_flags_to_items mode_flag_refs =
List.mapPartial (fn (s, r) => if !r then SOME (s ^ " true") else NONE)
mode_flag_refs
fun getAllSubProjects filename =
let
fun subprojects (fname, seen) =
let fun real_file_name proj =
if OS.Path.isRelative proj then
OS.Path.mkCanonical
(OS.Path.mkAbsolute {path=proj, relativeTo=OS.Path.dir fname})
else
OS.Path.mkCanonical proj
in
if List.exists (fn n => fname = n) seen then seen
else
let val sf = get_sectioned_file fname
val subs =
SectionedFile.getItems(
SectionedFile.getDescendent(sf,["Subprojects"]))
handle SectionedFile.InvalidPath => []
val filenames = map (real_file_name o fromUnixPath) subs
in fname :: (foldl subprojects seen filenames)
end
end
in rev (subprojects (OS.Path.mkCanonical filename, [])) end
fun getSubTargets1 filename =
let val sf = get_sectioned_file filename
in SectionedFile.getItems(
SectionedFile.getDescendent(sf,["Targets"]))
end handle SectionedFile.InvalidPath => []
fun getSubTargets filename =
let val sf = get_sectioned_file filename
val enabled_targets =
SectionedFile.getItems(
SectionedFile.getDescendent(sf,["Targets", "Enabled"]))
in map (#1 o item_to_target_item) enabled_targets
end handle SectionedFile.InvalidPath => getSubTargets1 filename
fun maybe_add_extension extension filename =
let val {base, ext} = OS.Path.splitBaseExt filename
in case ext of
SOME _ => filename
| NONE => OS.Path.joinBaseExt { base = base, ext = SOME extension }
end
fun save_proj projfile filename =
let
val filename = maybe_add_extension "mlp" filename
val PROJECT_FILE
{projectName, projectDir, files, subprojects,
targets as {curTargets, disTargets, targetDetails},
modes = m as {modes, currentMode, modeDetails},
configs = c as {configs, currentConfig, configDetails},
locations as {libraryPath, objectsLoc, binariesLoc},
about as {description, version}} = projfile
val abs_name = getFullFilename (filename, getProjectDir projfile);
val _ = projectName := SOME abs_name;
val _ = projectDir := OS.Path.dir abs_name;
val create = SectionedFile.createSection
val about_info_section =
create("AboutInfo",
[create("Version", [],
if !version = "" then [] else [!version]),
create("Description", [],
if !description = "" then [] else [!description])],
[])
val target_section =
create("Targets",
[create("Details",
[], map target_item_to_item (!targetDetails))],
!curTargets)
val mode_section =
let
fun section_mode name =
let
val mode = getModeDetails(name, !modeDetails)
handle NoModeDetailsFound m =>
(warn_error ("Warning: Error in saving mode " ^ m);
newModeDetails m)
in
create(name,
[create("Location", [], [!(#location mode)]),
create("Flags", [],
mode_flags_to_items (mode_flags_to_refs mode))], [])
end
in
create("Modes", map section_mode (!modes),
case !currentMode of SOME name => [name] | NONE => [])
end
val config_section =
let
fun section_config name =
let
val configRec = getConfigDetails (name, !configDetails)
handle NoConfigDetailsFound c =>
(warn_error
("Warning: Error in saving configuration " ^ c);
{name=name, library=[], files=[]})
in
create(name,
[create("Library",[],map toUnixPath (#library configRec))],
map toUnixPath (#files configRec))
end
in
create("Configurations", map section_config (!configs),
case !currentConfig of SOME name => [name] | NONE => [])
end
val locations_section =
create("Locations",
[create("LibraryPath", [], map toUnixPath (!libraryPath)),
create("Objects", [], [toUnixPath (!objectsLoc)]),
create("Binaries", [], [toUnixPath (!binariesLoc)])],
[])
val sectioned_file =
create(String.toString(OS.Path.file abs_name),
[ about_info_section,
create("Files", [], map toUnixPath (!files)),
create("Subprojects", [], map toUnixPath (!subprojects)),
target_section, mode_section, config_section,
locations_section ],
[])
in
SectionedFile.writeSectionedFile (abs_name, STAMP, sectioned_file)
end
fun load_from_file(project_file, abs_name) =
let
open SectionedFile
val PROJECT_FILE
{projectName, projectDir, files, subprojects,
targets as {curTargets, disTargets, targetDetails},
modes = m as {modes, currentMode, modeDetails},
configs = c as {configs, currentConfig, configDetails},
locations as {libraryPath, objectsLoc, binariesLoc},
about as {description, version}} = project_file
val sf = get_sectioned_file abs_name
fun get_items sf path =
getItems(getDescendent(sf, path)) handle InvalidPath => []
val get_sf_items = get_items sf
fun hd_item (h::_) = h | hd_item _ = ""
in
projectDir := OS.Path.dir abs_name;
version := hd_item(get_sf_items ["AboutInfo", "Version"]);
description := hd_item(get_sf_items ["AboutInfo", "Description"]);
files := map fromUnixPath (get_sf_items ["Files"]);
subprojects := map fromUnixPath (get_sf_items ["Subprojects"]);
let val details =
map item_to_target_item (get_sf_items ["Targets", "Details"])
val target_names = map #1 details
val current_targets = get_sf_items ["Targets"]
fun is_current t = List.exists (fn t' => t = t') current_targets
in targetDetails := details;
curTargets := current_targets;
disTargets := List.filter (not o is_current) target_names
end;
let val mode_sec = getDescendent(sf, ["Modes"])
val mode_names = map getName (getSubsections mode_sec)
fun get_details sec =
let val md = newModeDetails (getName sec)
in ( #location md := (hd_item(get_items sec ["Location"]));
app (item_to_set_mode_flag (mode_flags_to_refs md))
(get_items sec ["Flags"]);
md) end
in modes := mode_names;
modeDetails := map get_details (getSubsections mode_sec);
case getItems mode_sec of
[current_mode] => currentMode := SOME current_mode
| _ => currentMode := NONE
end;
let val config_sec = getDescendent(sf, ["Configurations"])
val config_names = map getName (getSubsections config_sec)
fun get_details sec =
{name = getName sec,
files = map fromUnixPath (getItems sec),
library = map fromUnixPath (get_items sec ["Library"])}
in configs := config_names;
configDetails := map get_details (getSubsections config_sec);
case getItems config_sec of
[current_config] => currentConfig := SOME current_config
| _ => currentConfig := NONE
end;
libraryPath :=
map fromUnixPath (get_sf_items ["Locations", "LibraryPath"]);
objectsLoc :=
fromUnixPath (hd_item(get_sf_items ["Locations", "Objects"]));
binariesLoc :=
fromUnixPath (hd_item(get_sf_items ["Locations", "Binaries"]))
end
fun new_proj (project_file as PROJECT_FILE fields) working_dir =
( initialize project_file;
#projectDir fields := OS.Path.mkAbsolute{path=working_dir,
relativeTo=OS.FileSys.getDir()} )
fun open_proj project_file filename =
let val PROJECT_FILE {projectName, ...} = project_file
val abs_name = getFullFilename (filename, OS.FileSys.getDir())
in
proj_file_changed := true;
initialize project_file;
projectName := SOME abs_name;
diagnostic (2, fn _ => ["Project opened from ", filename]);
load_from_file (project_file, abs_name)
end
fun peek_project project_file filename =
let
val new_proj = mk_empty_proj()
val PROJECT_FILE
{projectName, projectDir, files, subprojects,
targets as {curTargets, disTargets, targetDetails},
modes = m as {modes, currentMode, modeDetails},
configs = c as {configs, currentConfig, configDetails},
locations as {libraryPath, objectsLoc, binariesLoc},
about as {description, version}} = new_proj
val abs_name = getFullFilename (filename, getProjectDir project_file)
in
diagnostic (2, fn _ => ["Project opened from ", abs_name]);
projectName := SOME abs_name;
load_from_file (new_proj, abs_name);
{name = abs_name,
files = !files,
curTargets = !curTargets,
disTargets = !disTargets,
subprojects = !subprojects,
libraryPath = !libraryPath,
objectsLoc = !objectsLoc,
binariesLoc = !binariesLoc,
currentConfig = !currentConfig,
configDetails = !configDetails,
currentMode = !currentMode,
modeDetails = !modeDetails}
end
fun close_proj project_file =
let
val PROJECT_FILE {projectName, ...} = project_file
in
proj_file_changed := true;
initialize project_file;
projectName := NONE
end
val project_file = mk_empty_proj()
val new_proj = new_proj project_file
val open_proj = open_proj project_file
val peek_project = peek_project project_file
val save_proj = save_proj project_file
val close_proj = fn () => close_proj project_file
val getAboutInfo = fn () => getAboutInfo project_file
val setAboutInfo = setAboutInfo project_file
val getProjectName = fn () => getProjectName project_file
val getProjectDir = fn () => getProjectDir project_file
val getFiles = fn () => getFiles project_file
val setFiles = setFiles project_file
val getSubprojects = fn () => getSubprojects project_file
val setSubprojects = setSubprojects project_file
val getTargets = fn () => getTargets project_file
val setTargets = setTargets project_file
val setCurrentTargets = setCurrentTargets project_file
val getModes = fn () => getModes project_file
val setModes = setModes project_file
val setInitialModes = fn () => setInitialModes project_file
val setCurrentMode = setCurrentMode project_file;
val getConfigurations = fn () => getConfigurations project_file
val setConfigurations = setConfigurations project_file
val setCurrentConfiguration = setCurrentConfiguration project_file
val getLocations = fn () => getLocations project_file
val setLocations = setLocations project_file
end
;
