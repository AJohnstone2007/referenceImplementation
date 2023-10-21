require "../main/options";
signature USER_CONTEXT =
sig
structure Options : OPTIONS
type user_context_options
type preferences
type Context
type Result
type user_context
type identifier
datatype source_reference =
STRING of string
| COPY of string
datatype history_entry =
ITEM of
{index: int,
id: identifier,
context: Context,
result: string,
source: source_reference}
val getCurrentContexts : unit -> user_context list
val getInitialContext : unit -> user_context
val getNewInitialContext : unit -> user_context
val copyUserContext : user_context -> user_context
val dummy_context: user_context
val makeInitialUserContext :
Context * string * user_context_options -> user_context
val get_saved_file_name : user_context -> string option
val set_saved_file_name : user_context * string -> unit
val saved_name_set : user_context -> bool
val clear_debug_info : user_context * string -> unit
val clear_debug_all_info : user_context -> unit
val get_user_options : user_context -> user_context_options
val get_context : user_context -> Context
val get_delta : user_context -> Context
val get_history : user_context -> history_entry list
val null_history : user_context -> bool
val with_null_history : user_context -> ('a -> 'b) -> 'a -> 'b
val delete_from_history : user_context * history_entry -> unit
val delete_entire_history : user_context -> unit
val remove_duplicates_from_history : user_context -> unit
val move_context_history_to_system : user_context -> unit
val get_latest: user_context -> history_entry option
val get_nth: user_context * int -> history_entry option
type register_key
val add_update_fn :
user_context * (history_entry list option -> unit)
-> register_key
val remove_update_fn : user_context * register_key -> unit
val history_entry_name : history_entry -> string option
val is_const_context : user_context -> bool
val set_context_name : user_context * string -> unit
val get_context_name : user_context -> string
val process_result :
{src: source_reference,
result: Result,
user_context: user_context,
preferences: preferences,
options: Options.options,
output_fn: string -> unit}
-> unit
end;
