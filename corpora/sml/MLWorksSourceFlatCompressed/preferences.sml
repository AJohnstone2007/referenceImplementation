require "../basis/__text_io";
signature PREFERENCES =
sig
datatype editor_options =
EDITOR_OPTIONS of
{editor : string ref,
oneWayEditorName : string ref,
twoWayEditorName : string ref,
externalEditorCommand : string ref}
val default_editor_options : editor_options
datatype environment_options =
ENVIRONMENT_OPTIONS of
{window_debugger: bool ref,
use_debugger: bool ref,
use_error_browser: bool ref,
use_relative_pathname: bool ref,
history_length: int ref,
completion_menu: bool ref,
remove_duplicates_from_context:
bool ref,
full_menus: bool ref}
val default_environment_options : environment_options
datatype preferences =
PREFERENCES of
{editor_options : editor_options,
environment_options : environment_options}
val default_preferences : preferences
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
val make_user_preferences: preferences -> user_preferences
val new_preferences: user_preferences -> preferences
val set_from_list : user_preferences * (string * string) list -> unit
val save_to_stream : user_preferences * TextIO.outstream -> unit
end
;
