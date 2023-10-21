require "../main/options";
require "../basis/__text_io";
signature USER_OPTIONS =
sig
structure Options: OPTIONS
datatype user_context_options = USER_CONTEXT_OPTIONS of
({generate_interceptable_code: bool ref,
generate_debug_info: bool ref,
generate_variable_debug_info: bool ref,
generate_moduler: bool ref,
generate_interruptable_code: bool ref,
optimize_handlers: bool ref,
optimize_leaf_fns: bool ref,
optimize_tail_calls: bool ref,
optimize_self_tail_calls: bool ref,
local_functions: bool ref,
print_messages: bool ref,
mips_r4000: bool ref,
sparc_v7: bool ref,
require_keyword: bool ref,
type_dynamic: bool ref,
abstractions: bool ref,
nj_op_in_datatype: bool ref,
nj_signatures: bool ref,
fixity_specs: bool ref,
open_fixity: bool ref,
weak_type_vars: bool ref,
old_definition: bool ref}
* (unit -> unit) list ref)
datatype user_tool_options = USER_TOOL_OPTIONS of
({show_fn_details: bool ref,
show_exn_details: bool ref,
maximum_seq_size: int ref,
maximum_string_size: int ref,
maximum_depth: int ref,
maximum_ref_depth: int ref,
maximum_str_depth: int ref,
maximum_sig_depth: int ref,
float_precision: int ref,
show_id_class: bool ref,
show_eq_info: bool ref,
show_absyn: bool ref,
show_match: bool ref,
show_environ: bool ref,
show_lambda: bool ref,
show_opt_lambda: bool ref,
show_mir: bool ref,
show_opt_mir: bool ref,
show_mach: bool ref,
show_debug_info: bool ref,
show_timings: bool ref,
show_print_timings: bool ref,
set_selection: bool ref,
sense_selection: bool ref,
set_context: bool ref,
sense_context: bool ref}
* (unit -> unit) list ref)
val make_user_context_options: Options.options -> user_context_options
val make_user_tool_options: Options.options -> user_tool_options
val update_user_context_options: user_context_options -> unit
val update_user_tool_options: user_tool_options -> unit
val select_compatibility: user_context_options -> unit
val select_sml'97: user_context_options -> unit
val select_sml'90: user_context_options -> unit
val select_quick_compile: user_context_options -> unit
val select_optimizing: user_context_options -> unit
val select_debugging: user_context_options -> unit
val is_sml'97: user_context_options -> bool
val is_compatibility: user_context_options -> bool
val is_sml'90: user_context_options -> bool
val is_debugging: user_context_options -> bool
val is_optimizing: user_context_options -> bool
val is_quick_compile: user_context_options -> bool
val new_options: user_tool_options * user_context_options -> Options.options
val new_print_options: user_tool_options -> Options.print_options
val copy_user_context_options: user_context_options -> user_context_options
val copy_user_tool_options: user_tool_options -> user_tool_options
val save_to_stream : user_context_options * TextIO.outstream -> unit
val set_from_list : user_context_options * (string * string) list -> unit
end;
