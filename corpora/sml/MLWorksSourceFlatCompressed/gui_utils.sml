require "^.basis.__text_io";
signature GUI_UTILS =
sig
type Widget
type ButtonSpec
type OptionSpec
type Type
type user_context_options
type user_tool_options
type user_preferences
type user_context
val make_outstream : (string -> unit) -> TextIO.outstream
type MotifContext
val listener_properties:
Widget * (unit -> MotifContext) -> ButtonSpec
val setup_menu:
Widget * (unit -> MotifContext) * user_preferences * (unit -> user_context_options) ->
(string * (unit -> unit) * (unit -> bool)) list
datatype Writable = WRITABLE | ALL
val make_context: user_context * Widget * user_preferences -> MotifContext
val makeInitialContext: Widget * user_preferences -> unit
val getInitialContext: unit -> MotifContext
val get_user_context: MotifContext -> user_context
val get_context_name: MotifContext -> string
val context_menu :
{set_state: MotifContext -> unit,
get_context: unit -> MotifContext,
writable: Writable,
applicationShell: Widget,
shell: Widget,
user_preferences: user_preferences}
-> ButtonSpec
val search_button :
Widget * (unit -> MotifContext) * (string -> unit) * bool
-> ButtonSpec
datatype ViewOptions = SENSITIVITY | VALUE_PRINTER | INTERNALS
val view_options :
{parent: Widget,
title: string,
user_options: user_tool_options,
user_preferences: user_preferences,
caller_update_fn: user_tool_options -> unit,
view_type: ViewOptions list}
-> ButtonSpec
val value_menu :
{parent: Widget,
user_preferences: user_preferences ,
inspect_fn: ((string * (MLWorks.Internal.Value.T * Type))->unit)
option,
get_value: unit -> (string * (MLWorks.Internal.Value.T * Type))
option,
enabled: bool,
tail: ButtonSpec list}
-> ButtonSpec
val toggle_value : string * ''a ref * ''a -> OptionSpec
val bool_value : string * bool ref -> OptionSpec
val text_value : string * string ref -> OptionSpec
val int_value : string * int ref -> OptionSpec
val save_history : bool * user_context * Widget -> unit
val make_history :
user_preferences * (string -> unit) ->
{update_history: string list -> unit,
prev_history: unit -> unit,
next_history: unit -> unit,
history_end: unit -> bool,
history_start: unit -> bool,
history_menu: ButtonSpec}
end
;
