require "../basis/os";
require "../basis/os_path";
require "../main/info";
require "../basics/module_id";
require "../utils/getenv";
require "mlworks_io";
functor MLWorksIo(
structure OS : OS
structure Path : OS_PATH
structure Info: INFO
structure ModuleId: MODULE_ID
structure Getenv : GETENV
sharing type Info.Location.T = ModuleId.Location
val pervasive_library_name : string
val builtin_library_name : string
val default_source_path : string list
) : MLWORKS_IO =
struct
type ModuleId = ModuleId.ModuleId
type Location = Info.Location.T
val pervasive_library_name = " " ^ pervasive_library_name
val builtin_library_name = " " ^ builtin_library_name
val pervasive_library_id =
ModuleId.from_mo_string
(pervasive_library_name, Info.Location.FILE "<Initialisation>")
val builtin_library_id =
ModuleId.from_mo_string
(builtin_library_name, Info.Location.FILE "<Initialisation>")
exception NotSet of string
fun remove_spaces ("",_) = ""
| remove_spaces(arg as (str, n)) =
if MLWorks.String.ordof arg = ord #" " then
remove_spaces(str, n+1)
else if n = 0 then
str
else
substring (str, n, size str - n)
local
val source_path = ref ([] : string list)
in
fun set_source_path_without_expansion l =
source_path := l
fun set_source_path(l, location) =
let
val expand_path = OS.FileSys.fullPath o Getenv.expand_home_dir
fun map_sub(acc, []) = rev acc
| map_sub(acc, x::xs) =
let
val s = expand_path x
in
map_sub(s::acc, xs)
end
handle
OS.SysErr _ =>
(Info.error (Info.make_default_options())
(Info.WARNING, location,
"Bad path '" ^ x ^ "', ignoring");
map_sub(acc, xs))
| Getenv.BadHomeName s =>
(Info.error (Info.make_default_options())
(Info.WARNING, location,
"Problem expanding source path - "
^ "can't find home directory for " ^ s);
map_sub(acc, xs))
val expanded = map_sub([], l)
in
case expanded of
[] => false
| _ => (set_source_path_without_expansion expanded; true)
end
fun set_source_path_from_string(s, location) =
let
val old_source_path = !source_path
in
set_source_path(Getenv.env_path_to_list s, location)
handle
Getenv.BadHomeName s =>
(Info.error (Info.make_default_options())
(Info.WARNING, location,
"Problem expanding source path - "
^ "can't find home directory for " ^ s);
set_source_path_without_expansion old_source_path;
false)
end
fun set_source_path_from_env (location, silent) =
case Getenv.get_source_path() of
NONE =>
(ignore(set_source_path
(map
(fn s => (remove_spaces(s,0)))
default_source_path, location));
())
| SOME str =>
(if set_source_path_from_string(str, location) andalso (not silent) then
print("Setting source path to: " ^ str ^ "\n")
else
())
val set_source_path_from_string =
fn arg => (ignore(set_source_path_from_string arg); ())
val set_source_path = fn s => (ignore(set_source_path(s, Info.Location.UNKNOWN)); ())
fun get_source_path () = (!source_path)
end
local
val pervasive_dir =
ref NONE : string option ref
val object_path =
ref (SOME(Path.concat["%S", "%C"])) :
string option ref
in
fun get_pervasive_dir () =
case !pervasive_dir
of SOME str => str
| NONE => raise NotSet "pervasive_dir"
fun set_pervasive_dir (s, location) =
pervasive_dir := SOME
(OS.FileSys.fullPath (Getenv.expand_home_dir s))
handle
Getenv.BadHomeName s =>
Info.error (Info.make_default_options())
(Info.WARNING, location,
"Problem expanding pervasive directory - "
^ "can't find home directory for " ^ s)
| OS.SysErr _ =>
Info.error (Info.make_default_options())
(Info.WARNING, location,
"Problem setting pervasive directory - "
^ s ^ " is not a valid path")
fun set_pervasive_dir_from_env location =
case Getenv.get_pervasive_dir () of
SOME s =>
set_pervasive_dir (remove_spaces (s,0), location)
| NONE => ()
fun print_pervasive_dir () =
case !pervasive_dir of
SOME str =>
print("Pervasive directory set to: " ^ str ^ "\n")
| NONE =>
print("Pervasive directory not set.\n")
fun get_object_path () =
case !object_path
of SOME str => str
| NONE => raise NotSet "object_path"
fun set_object_path(s, location) =
(object_path := SOME
(Getenv.expand_home_dir s); true)
handle
Getenv.BadHomeName s =>
(Info.error (Info.make_default_options())
(Info.WARNING, location,
"Problem expanding object path - "
^ "can't find home directory for " ^ s);
false)
fun set_object_path_from_env location =
case Getenv.get_object_path() of
SOME s =>
if set_object_path(remove_spaces(s,0), location) then
print("Setting object path to: " ^ s ^ "\n")
else
()
| NONE => ()
val set_object_path = fn arg => (ignore(set_object_path arg); ())
end
val _ = (set_pervasive_dir_from_env (Info.Location.FILE "main/_io.sml");
print_pervasive_dir ())
end
;
