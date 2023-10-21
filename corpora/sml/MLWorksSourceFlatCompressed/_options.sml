require "options";
functor Options () : OPTIONS =
struct
datatype listing_options =
LISTINGOPTIONS of {show_absyn : bool,
show_lambda : bool,
show_match : bool,
show_opt_lambda : bool,
show_environ : bool,
show_mir : bool,
show_opt_mir : bool,
show_mach : bool}
val default_listing_options =
LISTINGOPTIONS {show_absyn = false,
show_lambda = false,
show_match = false,
show_opt_lambda = false,
show_environ = false,
show_mir = false,
show_opt_mir = false,
show_mach = false}
datatype compiler_options =
COMPILEROPTIONS of {generate_debug_info : bool,
debug_variables : bool,
generate_moduler : bool,
intercept : bool,
interrupt : bool,
opt_handlers : bool,
opt_leaf_fns : bool,
opt_tail_calls : bool,
opt_self_calls : bool,
local_functions : bool,
print_messages : bool,
mips_r4000 : bool,
sparc_v7 : bool}
val default_compiler_options =
COMPILEROPTIONS {generate_debug_info = false,
debug_variables = false,
generate_moduler = false,
intercept = false,
interrupt = false,
opt_handlers = false,
opt_leaf_fns = true,
opt_tail_calls = true,
opt_self_calls = true,
local_functions = false,
print_messages = true,
mips_r4000 = true,
sparc_v7 = false}
datatype print_options =
PRINTOPTIONS of {maximum_seq_size : int,
maximum_string_size : int,
maximum_ref_depth : int,
maximum_str_depth : int,
maximum_sig_depth : int,
maximum_depth : int,
float_precision : int,
print_fn_details : bool,
print_exn_details : bool,
show_id_class: bool,
show_eq_info : bool
}
val default_print_options =
PRINTOPTIONS {maximum_seq_size = 10,
maximum_string_size = 255,
maximum_ref_depth = 3,
maximum_str_depth = 2,
maximum_sig_depth = 1,
maximum_depth = 7,
float_precision = 10,
print_fn_details = false,
print_exn_details = false,
show_eq_info = false,
show_id_class = false}
datatype extension_options =
EXTENSIONOPTIONS of {require_keyword : bool,
type_dynamic : bool}
val default_extension_options =
EXTENSIONOPTIONS {require_keyword = true,
type_dynamic = false}
datatype compat_options =
COMPATOPTIONS of {nj_op_in_datatype : bool,
nj_signatures : bool,
weak_type_vars : bool,
fixity_specs : bool,
open_fixity : bool,
abstractions : bool,
old_definition : bool}
val default_compat_options =
COMPATOPTIONS {nj_op_in_datatype = false,
nj_signatures = false,
weak_type_vars = false,
fixity_specs = false,
open_fixity = false,
abstractions = false,
old_definition = false}
datatype options = OPTIONS of {listing_options : listing_options,
compiler_options : compiler_options,
print_options : print_options,
extension_options : extension_options,
compat_options : compat_options}
val default_options = OPTIONS {listing_options = default_listing_options,
compiler_options = default_compiler_options,
print_options = default_print_options,
extension_options = default_extension_options,
compat_options = default_compat_options}
end
;
