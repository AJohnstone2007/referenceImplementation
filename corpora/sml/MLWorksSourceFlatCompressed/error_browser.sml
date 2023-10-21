signature ERROR_BROWSER =
sig
type Widget
type error
type location
type ToolData
type user_context
val error_to_string: error -> string
val create:
{parent: Widget,
errors: error list,
file_message: string,
editable: location -> bool,
edit_action: location -> {quit_fn: unit -> unit, clean_fn: unit -> unit},
close_action: unit -> unit,
redo_action: unit -> unit,
mk_tooldata: unit -> ToolData,
get_context: unit -> user_context}
-> (unit -> unit)
end;
