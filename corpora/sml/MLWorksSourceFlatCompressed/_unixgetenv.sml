require "../utils/getenv";
require "^.utils.__messages";
require "^.basis.__string";
require "unixos";
require "^.basis.__int";
functor UnixGetenv (structure UnixOS: UNIXOS): GETENV =
struct
fun get_option_value option_name =
let
val option_length = size option_name
fun get_value [] = NONE
| get_value(arg :: rest) =
if size arg < option_length then
get_value rest
else if String.isPrefix option_name arg then
if size arg = option_length then
NONE
else
SOME
(substring (arg, option_length, size arg - option_length))
else
get_value rest
in
get_value
end
fun get_version_setting () =
get_option_value "MLWORKS_VERSION=" (UnixOS.environment())
fun get_source_path () =
get_option_value "MLWORKS_SRC_PATH=" (UnixOS.environment())
fun get_object_path () =
get_option_value "MLWORKS_OBJ_PATH=" (UnixOS.environment())
fun get_pervasive_dir () =
get_option_value "MLWORKS_PERVASIVE=" (UnixOS.environment())
fun get_doc_dir () =
get_option_value "MLWORKS_DOC=" (UnixOS.environment())
fun get_startup_dir () = NONE
local
fun get_home_dir () =
get_option_value "HOME=" (UnixOS.environment())
fun get_user_name () =
get_option_value "USER=" (UnixOS.environment())
in
fun get_startup_filename () =
case get_home_dir () of
NONE =>
(Messages.output"Warning, no HOME variable set -- can't read .mlworks file\n";
NONE)
| SOME dir =>
SOME (dir ^ "/.mlworks")
fun get_preferences_filename () =
case get_home_dir () of
NONE => NONE
| SOME dir =>
SOME (dir ^ "/.mlworks_preferences")
exception BadHomeName of string
fun expand_home_dir string =
let
val len = size string
fun upto_slash n =
if n = len then
n
else if String.sub (string, n) = #"/" then
n
else
upto_slash (n+1)
val expanded =
if len = 0 orelse String.sub(string, 0) <> #"~" then
string
else
let
val start = upto_slash 0
val name =
if start = 1 then
get_user_name()
else
SOME (substring (string, 1, start-1))
val dir =
case name of
SOME str =>
((case UnixOS.getpwnam str of
UnixOS.PASSWD {dir, ...} => dir)
handle UnixOS.Error.SysErr _ =>
raise BadHomeName ("~" ^ str))
| NONE =>
case get_home_dir () of
NONE =>
raise BadHomeName "~"
| SOME dir =>
dir
val rest = substring (string, start, len-start)
in
dir ^ rest
end
in
expanded
end
end
fun env_path_to_list s =
let
fun str_to_list (0, seperator_index, result) =
substring (s, 0, seperator_index) :: result
| str_to_list (n, seperator_index, result) =
if String.sub (s, n) = #":" then
str_to_list
(n - 1, n,
substring (s, n + 1, seperator_index - n - 1) :: result)
else
str_to_list (n - 1, seperator_index, result)
in
map
expand_home_dir
(str_to_list (size s - 1, size s, []))
end
end
;
