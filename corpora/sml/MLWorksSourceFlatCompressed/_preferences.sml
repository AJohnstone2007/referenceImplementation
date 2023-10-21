require "^.basis.__int";
require "../basis/__text_io";
require "preferences";
require "info";
functor Preferences (
structure Info: INFO
): PREFERENCES =
struct
datatype editor_options =
EDITOR_OPTIONS of
{editor : string ref,
oneWayEditorName : string ref,
twoWayEditorName : string ref,
externalEditorCommand : string ref}
val default_editor_options =
EDITOR_OPTIONS
{editor = ref "External",
oneWayEditorName = ref "Vi",
twoWayEditorName = ref "Emacs",
externalEditorCommand = ref "xterm -name VIsual -e vi +%l %f"}
datatype environment_options =
ENVIRONMENT_OPTIONS of
{history_length: int ref,
window_debugger: bool ref,
use_debugger: bool ref,
use_error_browser : bool ref,
use_relative_pathname: bool ref,
completion_menu: bool ref,
remove_duplicates_from_context:
bool ref,
full_menus: bool ref}
val default_environment_options =
ENVIRONMENT_OPTIONS
{window_debugger = ref true,
use_debugger = ref true,
use_error_browser = ref true,
use_relative_pathname = ref false,
history_length = ref 20,
completion_menu = ref true,
remove_duplicates_from_context = ref false,
full_menus = ref false}
datatype preferences =
PREFERENCES of
{editor_options : editor_options,
environment_options : environment_options}
val default_preferences =
PREFERENCES
{editor_options = default_editor_options,
environment_options = default_environment_options}
datatype user_preferences = USER_PREFERENCES of
({editor: string ref,
externalEditorCommand: string ref,
oneWayEditorName: string ref,
twoWayEditorName: string ref,
history_length: int ref,
max_num_errors: int ref,
window_debugger: bool ref,
use_debugger: bool ref,
use_error_browser: bool ref,
use_relative_pathname: bool ref,
completion_menu: bool ref,
remove_duplicates_from_context: bool ref,
full_menus: bool ref}
* (unit -> unit) list ref)
fun make_user_preferences
(PREFERENCES
{editor_options = EDITOR_OPTIONS
{editor, externalEditorCommand, oneWayEditorName, twoWayEditorName},
environment_options = ENVIRONMENT_OPTIONS
{window_debugger, history_length,
use_debugger, use_error_browser, use_relative_pathname,
completion_menu, full_menus,
remove_duplicates_from_context}}) =
USER_PREFERENCES
({editor = editor,
externalEditorCommand = externalEditorCommand,
oneWayEditorName = oneWayEditorName,
twoWayEditorName = twoWayEditorName,
history_length = history_length,
max_num_errors = Info.max_num_errors,
window_debugger = window_debugger,
use_debugger = use_debugger,
use_error_browser = use_error_browser,
use_relative_pathname = use_relative_pathname,
completion_menu = completion_menu,
remove_duplicates_from_context = remove_duplicates_from_context,
full_menus = full_menus},
ref nil)
fun new_editor_options (USER_PREFERENCES (r, _)) =
EDITOR_OPTIONS
{editor = #editor r,
externalEditorCommand = #externalEditorCommand r,
oneWayEditorName = #oneWayEditorName r,
twoWayEditorName = #twoWayEditorName r}
fun new_environment_options (USER_PREFERENCES (r, _)) =
ENVIRONMENT_OPTIONS
{history_length = #history_length r,
window_debugger = #window_debugger r,
use_debugger = #use_debugger r,
use_error_browser = #use_error_browser r,
use_relative_pathname = #use_relative_pathname r,
completion_menu = #completion_menu r,
remove_duplicates_from_context = #remove_duplicates_from_context r,
full_menus = #full_menus r}
fun new_preferences user_preferences =
PREFERENCES
{editor_options = new_editor_options user_preferences,
environment_options = new_environment_options user_preferences }
fun set_from_list (USER_PREFERENCES (r,_),items) =
let
fun do_one (component,value) =
let
fun get_bool "false" = false
| get_bool _ = true
fun get_int s =
let
fun scan ([],acc) = acc
| scan (c :: rest,acc) =
scan (rest, ord c - ord #"0" + (10 * acc))
in
scan (explode s,0)
end
in
case component of
"editor" => (#editor r) := value
| "externalEditorCommand" => (#externalEditorCommand r) := value
| "oneWayEditorName" => (#oneWayEditorName r) := value
| "twoWayEditorName" => (#twoWayEditorName r) := value
| "history_length" => (#history_length r) := get_int value
| "max_num_errors" => (#max_num_errors r) := get_int value
| "window_debugger" => (#window_debugger r) := get_bool value
| "use_debugger" => (#use_debugger r) := get_bool value
| "use_error_browser" => (#use_error_browser r) := get_bool value
| "completion_menu" => (#completion_menu r) := get_bool value
| "remove_duplicates_from_context" =>
(#remove_duplicates_from_context r) := get_bool value
| "full_menus" => (#full_menus r) := get_bool value
| _ => ()
end
in
app do_one items
end
fun save_to_stream (USER_PREFERENCES (r,_),outstream) =
let
fun out (component,value) = TextIO.output (outstream,component ^ " " ^ value ^ "\n")
fun write_bool true = "true"
| write_bool false = "false"
val write_int = Int.toString
in
out ("editor", !(#editor r));
out ("externalEditorCommand", !(#externalEditorCommand r));
out ("oneWayEditorName", !(#oneWayEditorName r));
out ("twoWayEditorName", !(#twoWayEditorName r));
out ("history_length", write_int (!(#history_length r)));
out ("max_num_errors", write_int (!(#max_num_errors r)));
out ("window_debugger", write_bool (!(#window_debugger r)));
out ("use_debugger", write_bool (!(#use_debugger r)));
out ("use_error_browser", write_bool (!(#use_error_browser r)));
out ("completion_menu", write_bool (!(#completion_menu r)));
out ("remove_duplicates_from_context",
write_bool (!(#remove_duplicates_from_context r)));
out ("full_menus", write_bool (!(#full_menus r)))
end
end
;
