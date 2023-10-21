require "^.utils.__messages";
require "../utils/getenv";
require "^.basis.os_path";
require "windows";
functor Win_ntGetenv (structure OSPath: OS_PATH
structure Windows: WINDOWS): GETENV =
struct
fun env_path_to_list s =
let
fun str_to_list (0, seperator_index, result) =
substring (s, 0, seperator_index) :: result
| str_to_list (n, seperator_index, result) =
if MLWorks.String.ordof (s, n) = ord #";" then
str_to_list
(n - 1, n,
substring (s, n + 1, seperator_index - n - 1) :: result)
else
str_to_list (n - 1, seperator_index, result)
in
str_to_list (size s - 1, size s, [])
end
exception BadHomeName of string
fun expand_home_dir string = string
fun get_option_value option_name =
let
val option_length = size option_name
fun get_value [] = NONE
| get_value(arg :: rest) =
if size arg < option_length then
get_value rest
else if substring(arg, 0, option_length) = option_name then
if size arg - option_length = 0 then
NONE
else
SOME
(substring(arg, option_length, size arg - option_length))
else
get_value rest
in
get_value
end
local
fun close_key (SOME key) = Windows.Reg.closeKey key
| close_key NONE = ()
fun openKey (reg_key, reg_string) =
Windows.Reg.openKeyEx(reg_key, reg_string,
Windows.Key.execute)
fun warning s = Messages.output s;
fun print_warn value_string =
(warning ("Software/Harlequin/MLWorks/" ^ value_string ^
" value not set in registry.\n");
NONE)
fun openMLWorksKey start_key =
let
val software_key =
if (isSome start_key) then
openKey ((valOf start_key), "Software")
else NONE
val harlequin_key =
if (isSome software_key) then
openKey ((valOf software_key), "Harlequin")
else NONE
val mlworks_key =
if (isSome harlequin_key) then
openKey ((valOf harlequin_key), "MLWorks")
else NONE
in
(software_key, mlworks_key)
end
fun getMLWorksValue value_string failAction =
let
val (software_key, mlworks_key) =
openMLWorksKey (SOME Windows.Reg.currentUser)
val the_value = if (isSome mlworks_key) then
Windows.Reg.queryValueEx((valOf mlworks_key),
value_string)
else ""
in
(close_key software_key;
if the_value = "" then
(failAction value_string)
else
SOME the_value)
end
fun mkCanonical (SOME s) = SOME (OSPath.mkCanonical s)
| mkCanonical NONE = NONE
in
fun get_startup_dir () =
mkCanonical (getMLWorksValue "Startup Directory" print_warn)
fun get_source_path () =
mkCanonical (getMLWorksValue "Source Path" print_warn)
fun get_object_path () =
mkCanonical (getMLWorksValue "Object Path" print_warn)
fun get_pervasive_dir () =
mkCanonical (getMLWorksValue "Pervasive Path" print_warn)
fun get_version_setting () =
getMLWorksValue "Version Setting" (fn _ => NONE)
fun get_doc_dir () = NONE
end
fun get_startup_filename () =
case get_startup_dir () of
NONE => NONE
| SOME dir => SOME (dir ^ "/.mlworks")
fun get_preferences_filename () =
case get_startup_dir () of
NONE => NONE
| SOME dir => SOME (dir ^ "/.mlworks_preferences")
end
;
