signature OPTIONS =
sig
datatype listing_options =
LISTINGOPTIONS of {show_absyn : bool,
show_lambda : bool,
show_match : bool,
show_opt_lambda : bool,
show_environ : bool,
show_mir : bool,
show_opt_mir : bool,
show_mach : bool}
val default_listing_options : listing_options
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
val default_compiler_options : compiler_options
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
val default_print_options : print_options
datatype compat_options =
COMPATOPTIONS of {nj_op_in_datatype : bool,
nj_signatures : bool,
weak_type_vars : bool,
fixity_specs : bool,
open_fixity : bool,
abstractions : bool,
old_definition : bool}
val default_compat_options : compat_options
datatype extension_options =
EXTENSIONOPTIONS of {require_keyword : bool,
type_dynamic : bool
}
val default_extension_options : extension_options
datatype options = OPTIONS of {listing_options : listing_options,
compiler_options : compiler_options,
print_options : print_options,
compat_options : compat_options,
extension_options : extension_options}
val default_options : options
end
;
