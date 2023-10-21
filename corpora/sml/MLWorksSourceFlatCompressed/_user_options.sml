require "../basis/__int";
require "../basis/__text_io";
require "../utils/lists";
require "options";
require "user_options";
functor UserOptions (
structure Options: OPTIONS
structure Lists: LISTS
): USER_OPTIONS =
struct
structure Options = Options
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
fun make_user_tool_options
(Options.OPTIONS
{listing_options =
Options.LISTINGOPTIONS
{show_absyn, show_lambda, show_match, show_opt_lambda,
show_environ, show_mir, show_opt_mir, show_mach},
print_options =
Options.PRINTOPTIONS
{maximum_seq_size, maximum_string_size, maximum_ref_depth,
maximum_str_depth, maximum_sig_depth, maximum_depth, float_precision,
print_fn_details, print_exn_details, show_id_class, show_eq_info},
...}) =
USER_TOOL_OPTIONS
({maximum_seq_size = ref maximum_seq_size,
maximum_string_size = ref maximum_string_size,
maximum_ref_depth = ref maximum_ref_depth,
maximum_str_depth = ref maximum_str_depth,
maximum_sig_depth = ref maximum_sig_depth,
maximum_depth = ref maximum_depth,
float_precision = ref float_precision,
show_fn_details = ref print_fn_details,
show_exn_details = ref print_exn_details,
show_id_class = ref show_id_class,
show_eq_info = ref show_eq_info,
show_absyn = ref show_absyn,
show_match = ref show_match,
show_lambda = ref show_lambda,
show_environ = ref show_environ,
show_opt_lambda = ref show_opt_lambda,
show_mir = ref show_mir,
show_opt_mir = ref show_opt_mir,
show_mach = ref show_mach,
show_debug_info = ref false,
show_timings = ref false,
show_print_timings = ref false,
set_context = ref true,
sense_context = ref true,
set_selection = ref true,
sense_selection = ref true},
ref nil)
fun make_user_context_options
(Options.OPTIONS
{compiler_options = Options.COMPILEROPTIONS
{generate_debug_info, debug_variables, generate_moduler,
intercept, interrupt, opt_handlers, opt_leaf_fns, opt_tail_calls,
opt_self_calls, local_functions, print_messages,
mips_r4000, sparc_v7},
compat_options = Options.COMPATOPTIONS
{nj_op_in_datatype,
nj_signatures, weak_type_vars, fixity_specs, open_fixity,
abstractions, old_definition},
extension_options = Options.EXTENSIONOPTIONS
{require_keyword, type_dynamic},
...}) =
USER_CONTEXT_OPTIONS
({generate_interceptable_code = ref intercept,
generate_debug_info = ref generate_debug_info,
generate_variable_debug_info = ref debug_variables,
generate_moduler = ref generate_moduler,
generate_interruptable_code = ref interrupt,
optimize_handlers = ref opt_handlers,
optimize_leaf_fns = ref opt_leaf_fns,
optimize_tail_calls = ref opt_tail_calls,
optimize_self_tail_calls = ref opt_self_calls,
local_functions = ref local_functions,
print_messages = ref print_messages,
mips_r4000 = ref mips_r4000,
sparc_v7 = ref sparc_v7,
require_keyword = ref require_keyword,
type_dynamic = ref type_dynamic,
abstractions = ref abstractions,
old_definition = ref old_definition,
nj_op_in_datatype = ref nj_op_in_datatype,
nj_signatures = ref nj_signatures,
fixity_specs = ref fixity_specs,
open_fixity = ref open_fixity,
weak_type_vars = ref weak_type_vars},
ref nil)
fun copy_user_tool_options
(USER_TOOL_OPTIONS
({maximum_seq_size, maximum_string_size, maximum_ref_depth,
maximum_str_depth, maximum_sig_depth, maximum_depth, float_precision,
show_fn_details, show_exn_details,
show_id_class, show_eq_info, show_absyn, show_match,
show_lambda, show_environ, show_opt_lambda, show_mir,
show_opt_mir, show_mach, show_debug_info, show_timings,
show_print_timings,
set_selection, sense_selection, set_context, sense_context},
_)) =
USER_TOOL_OPTIONS
({maximum_seq_size = ref (!maximum_seq_size),
maximum_string_size = ref (!maximum_string_size),
maximum_ref_depth = ref (!maximum_ref_depth),
maximum_str_depth = ref (!maximum_str_depth),
maximum_sig_depth = ref (!maximum_sig_depth),
maximum_depth = ref (!maximum_depth),
float_precision = ref (!float_precision),
show_fn_details = ref (!show_fn_details),
show_exn_details = ref (!show_exn_details),
show_id_class = ref (!show_id_class),
show_eq_info = ref (!show_eq_info),
show_absyn = ref (!show_absyn),
show_match = ref (!show_match),
show_lambda = ref (!show_lambda),
show_environ = ref (!show_environ),
show_opt_lambda = ref (!show_opt_lambda),
show_mir = ref (!show_mir),
show_opt_mir = ref (!show_opt_mir),
show_mach = ref (!show_mach),
show_debug_info = ref (!show_debug_info),
show_timings = ref (!show_timings),
show_print_timings = ref (!show_print_timings),
set_context = ref (!set_context),
sense_context = ref (!sense_context),
set_selection = ref (!set_selection),
sense_selection = ref (!sense_selection)},
ref nil)
fun copy_user_context_options
(USER_CONTEXT_OPTIONS
({generate_interceptable_code, generate_interruptable_code,
generate_debug_info, generate_variable_debug_info,
generate_moduler,
optimize_handlers,
optimize_leaf_fns, optimize_tail_calls, optimize_self_tail_calls,
local_functions,
print_messages,
mips_r4000,sparc_v7,
abstractions, old_definition,
nj_op_in_datatype, nj_signatures,
fixity_specs,
open_fixity,
require_keyword,
type_dynamic,
weak_type_vars}, _)) =
USER_CONTEXT_OPTIONS
({generate_interceptable_code = ref (!generate_interceptable_code),
generate_debug_info = ref (!generate_debug_info),
generate_variable_debug_info = ref (!generate_variable_debug_info),
generate_moduler = ref (!generate_moduler),
generate_interruptable_code = ref (!generate_interruptable_code),
optimize_handlers = ref (!optimize_handlers),
optimize_leaf_fns = ref (!optimize_leaf_fns),
optimize_tail_calls = ref (!optimize_tail_calls),
optimize_self_tail_calls = ref (!optimize_self_tail_calls),
local_functions = ref (!local_functions),
print_messages = ref (!print_messages),
mips_r4000 = ref (!mips_r4000),
sparc_v7 = ref (!sparc_v7),
require_keyword = ref (!require_keyword),
type_dynamic = ref (!type_dynamic),
abstractions = ref (!abstractions),
old_definition = ref (!old_definition),
nj_op_in_datatype = ref (!nj_op_in_datatype),
nj_signatures = ref (!nj_signatures),
fixity_specs = ref (!fixity_specs),
open_fixity = ref (!open_fixity),
weak_type_vars = ref (!weak_type_vars)},
ref nil)
fun update_user_context_options user_context_options =
let
val USER_CONTEXT_OPTIONS (_, ref update_fns) = user_context_options
in
Lists.iterate (fn f => f ()) update_fns
end
fun update_user_tool_options user_tool_options =
let
val USER_TOOL_OPTIONS (_, ref update_fns) = user_tool_options
in
Lists.iterate (fn f => f ()) update_fns
end
fun get_user_context_option (f, user_context_options) =
let
val USER_CONTEXT_OPTIONS (user_options, _) = user_context_options
in
!(f user_options)
end
fun set_user_context_option (f, user_context_options) =
let
val USER_CONTEXT_OPTIONS (user_options, _) = user_context_options
in
(f user_options) := true
end
fun clear_user_context_option (f, user_context_options) =
let
val USER_CONTEXT_OPTIONS (user_options, _) = user_context_options
in
(f user_options) := false
end
fun select_compatibility user_options =
(set_user_context_option (#nj_op_in_datatype, user_options);
set_user_context_option (#nj_signatures, user_options);
set_user_context_option (#weak_type_vars, user_options);
set_user_context_option (#abstractions, user_options);
set_user_context_option (#fixity_specs, user_options);
set_user_context_option (#open_fixity, user_options);
set_user_context_option (#old_definition, user_options))
fun select_sml'97 user_options =
(clear_user_context_option (#nj_op_in_datatype, user_options);
clear_user_context_option (#nj_signatures, user_options);
clear_user_context_option (#weak_type_vars, user_options);
clear_user_context_option (#abstractions, user_options);
clear_user_context_option (#fixity_specs, user_options);
clear_user_context_option (#open_fixity, user_options);
clear_user_context_option (#old_definition, user_options))
fun select_sml'90 user_options =
(clear_user_context_option (#nj_op_in_datatype, user_options);
clear_user_context_option (#nj_signatures, user_options);
clear_user_context_option (#weak_type_vars, user_options);
clear_user_context_option (#abstractions, user_options);
clear_user_context_option (#fixity_specs, user_options);
clear_user_context_option (#open_fixity, user_options);
set_user_context_option (#old_definition, user_options))
val unset = clear_user_context_option
val set = set_user_context_option
fun select_quick_compile user_options =
(set (#optimize_leaf_fns, user_options);
set (#optimize_tail_calls, user_options);
set (#optimize_self_tail_calls, user_options);
unset (#generate_interceptable_code, user_options);
unset (#generate_debug_info, user_options);
unset (#generate_variable_debug_info, user_options);
unset (#local_functions, user_options))
fun select_optimizing user_options =
(set (#optimize_leaf_fns, user_options);
set (#optimize_tail_calls, user_options);
set (#optimize_self_tail_calls, user_options);
unset (#generate_interceptable_code, user_options);
unset (#generate_debug_info, user_options);
unset (#generate_variable_debug_info, user_options);
set (#local_functions, user_options))
fun select_debugging user_options =
(set (#generate_interceptable_code, user_options);
set (#generate_debug_info, user_options);
set (#generate_variable_debug_info, user_options);
unset (#optimize_leaf_fns, user_options);
unset (#optimize_tail_calls, user_options);
unset (#optimize_self_tail_calls, user_options))
fun is_sml'97 user_options =
not (get_user_context_option (#nj_op_in_datatype, user_options)) andalso
not (get_user_context_option (#nj_signatures, user_options)) andalso
not (get_user_context_option (#weak_type_vars, user_options)) andalso
not (get_user_context_option (#open_fixity, user_options)) andalso
not (get_user_context_option (#fixity_specs, user_options)) andalso
not (get_user_context_option (#abstractions, user_options)) andalso
not (get_user_context_option (#old_definition, user_options))
fun is_compatibility user_options =
get_user_context_option (#nj_op_in_datatype, user_options) andalso
get_user_context_option (#nj_signatures, user_options) andalso
get_user_context_option (#weak_type_vars, user_options) andalso
get_user_context_option (#open_fixity, user_options) andalso
get_user_context_option (#fixity_specs, user_options) andalso
get_user_context_option (#abstractions, user_options) andalso
get_user_context_option (#old_definition, user_options)
fun is_sml'90 user_options =
not (get_user_context_option (#nj_op_in_datatype, user_options)) andalso
not (get_user_context_option (#nj_signatures, user_options)) andalso
not (get_user_context_option (#weak_type_vars, user_options)) andalso
not (get_user_context_option (#open_fixity, user_options)) andalso
not (get_user_context_option (#fixity_specs, user_options)) andalso
not (get_user_context_option (#abstractions, user_options)) andalso
get_user_context_option (#old_definition, user_options)
val get_uc = get_user_context_option
fun is_quick_compile user_options =
get_uc (#optimize_leaf_fns, user_options) andalso
get_uc (#optimize_tail_calls, user_options) andalso
get_uc (#optimize_self_tail_calls, user_options) andalso
not (get_uc (#generate_debug_info, user_options)) andalso
not (get_uc (#generate_interceptable_code, user_options)) andalso
not (get_uc (#generate_variable_debug_info, user_options)) andalso
not (get_uc (#local_functions, user_options))
fun is_debugging user_options =
get_uc (#generate_debug_info, user_options) andalso
get_uc (#generate_interceptable_code, user_options) andalso
get_uc (#generate_variable_debug_info, user_options) andalso
not (get_uc (#optimize_leaf_fns, user_options)) andalso
not (get_uc (#optimize_tail_calls, user_options)) andalso
not (get_uc (#optimize_self_tail_calls, user_options))
fun is_optimizing user_options =
get_uc (#optimize_leaf_fns, user_options) andalso
get_uc (#optimize_tail_calls, user_options) andalso
get_uc (#optimize_self_tail_calls, user_options) andalso
not (get_uc (#generate_debug_info, user_options)) andalso
not (get_uc (#generate_interceptable_code, user_options)) andalso
not (get_uc (#generate_variable_debug_info, user_options)) andalso
get_uc (#local_functions, user_options)
fun new_print_options (USER_TOOL_OPTIONS (r, _)) =
Options.PRINTOPTIONS
{print_fn_details = !(#show_fn_details r),
print_exn_details = !(#show_exn_details r),
maximum_seq_size = !(#maximum_seq_size r),
maximum_string_size = !(#maximum_string_size r),
maximum_ref_depth = !(#maximum_ref_depth r),
maximum_str_depth = !(#maximum_str_depth r),
maximum_sig_depth = !(#maximum_sig_depth r),
maximum_depth = !(#maximum_depth r),
float_precision = !(#float_precision r),
show_eq_info = !(#show_eq_info r),
show_id_class = !(#show_id_class r) }
fun new_compiler_options (USER_CONTEXT_OPTIONS (r, _)) =
Options.COMPILEROPTIONS
{generate_debug_info = !(#generate_debug_info r),
debug_variables = !(#generate_variable_debug_info r),
generate_moduler = !(#generate_moduler r),
intercept = !(#generate_interceptable_code r),
interrupt = !(#generate_interruptable_code r),
opt_handlers = !(#optimize_handlers r),
opt_leaf_fns = !(#optimize_leaf_fns r),
opt_tail_calls = !(#optimize_tail_calls r),
opt_self_calls = !(#optimize_self_tail_calls r),
local_functions = !(#local_functions r),
print_messages = !(#print_messages r),
mips_r4000 = !(#mips_r4000 r),
sparc_v7 = !(#sparc_v7 r)
}
fun new_listing_options (USER_TOOL_OPTIONS (r,_)) =
Options.LISTINGOPTIONS
{show_absyn = !(#show_absyn r),
show_lambda = !(#show_lambda r),
show_match = !(#show_match r),
show_opt_lambda = !(#show_opt_lambda r),
show_environ = !(#show_environ r),
show_mir = !(#show_mir r),
show_opt_mir = !(#show_opt_mir r),
show_mach = !(#show_mach r)}
fun new_compat_options (USER_CONTEXT_OPTIONS (r, _)) =
Options.COMPATOPTIONS
{old_definition = !(#old_definition r),
nj_op_in_datatype = !(#nj_op_in_datatype r),
nj_signatures = !(#nj_signatures r),
weak_type_vars = !(#weak_type_vars r),
fixity_specs = !(#fixity_specs r),
open_fixity = !(#open_fixity r),
abstractions = !(#abstractions r)}
fun new_extension_options (USER_CONTEXT_OPTIONS (r, _)) =
Options.EXTENSIONOPTIONS
{type_dynamic = !(#type_dynamic r),
require_keyword = !(#require_keyword r)}
fun new_options (user_tool_options, user_context_options) =
Options.OPTIONS
{listing_options = new_listing_options user_tool_options,
compiler_options = new_compiler_options user_context_options,
print_options = new_print_options user_tool_options,
extension_options = new_extension_options user_context_options,
compat_options = new_compat_options user_context_options
}
fun save_to_stream (USER_CONTEXT_OPTIONS (r,_), stream) =
let
fun out (name,value) = TextIO.output (stream, name ^ " " ^ value ^ "\n")
fun write_bool true = "true"
| write_bool false = "false"
val write_int = Int.toString
in
out ("generate_interceptable_code",write_bool (!(#generate_interceptable_code r)));
out ("generate_debug_info",write_bool (!(#generate_debug_info r)));
out ("generate_variable_debug_info",write_bool (!(#generate_variable_debug_info r)));
out ("generate_moduler",write_bool (!(#generate_moduler r)));
out ("generate_interruptable_code",write_bool (!(#generate_interruptable_code r)));
out ("optimize_handlers",write_bool (!(#optimize_handlers r)));
out ("optimize_leaf_fns",write_bool (!(#optimize_leaf_fns r)));
out ("optimize_tail_calls",write_bool (!(#optimize_tail_calls r)));
out ("optimize_self_tail_calls",write_bool (!(#optimize_self_tail_calls r)));
out ("local_functions",write_bool (!(#local_functions r)));
out ("require_keyword",write_bool (!(#require_keyword r)));
out ("type_dynamic",write_bool (!(#type_dynamic r)));
out ("abstractions",write_bool (!(#abstractions r)));
out ("old_definition",write_bool (!(#old_definition r)));
out ("op_in_datatype",write_bool (!(#nj_op_in_datatype r)));
out ("limited_open",write_bool (!(#nj_signatures r)));
out ("fixity_specs",write_bool (!(#fixity_specs r)));
out ("open_fixity",write_bool (!(#open_fixity r)));
out ("weak_type_vars",write_bool (!(#weak_type_vars r)))
end
fun set_from_list (USER_CONTEXT_OPTIONS (r,_),items) =
let
fun get_bool "false" = false
| get_bool _ = true
fun do_one (component,value) =
case component of
"generate_interceptable_code" => (#generate_interceptable_code r) := get_bool value
| "generate_debug_info" => (#generate_debug_info r) := get_bool value
| "generate_variable_debug_info" => (#generate_variable_debug_info r) := get_bool value
| "generate_moduler" => (#generate_moduler r) := get_bool value
| "generate_interruptable_code" => (#generate_interruptable_code r) := get_bool value
| "optimize_handlers" => (#optimize_handlers r) := get_bool value
| "optimize_leaf_fns" => (#optimize_leaf_fns r) := get_bool value
| "optimize_tail_calls" => (#optimize_tail_calls r) := get_bool value
| "optimize_self_tail_calls" => (#optimize_self_tail_calls r) := get_bool value
| "local_functions" => (#local_functions r) := get_bool value
| "require_keyword" => (#require_keyword r) := get_bool value
| "type_dynamic" => (#type_dynamic r) := get_bool value
| "abstractions" => (#abstractions r) := get_bool value
| "old_definition" => (#old_definition r) := get_bool value
| "op_in_datatype" => (#nj_op_in_datatype r) := get_bool value
| "limited_open" => (#nj_signatures r) := get_bool value
| "fixity_specs" => (#fixity_specs r) := get_bool value
| "open_fixity" => (#open_fixity r) := get_bool value
| "weak_type_vars" => (#weak_type_vars r) := get_bool value
| _ => ()
fun iterate f [] = ()
| iterate f (a::b) = (ignore(f a) ; iterate f b)
in
iterate do_one items
end
end
;
