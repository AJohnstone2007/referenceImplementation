require "../utils/set";
signature MACHSPEC =
sig
structure Set : SET
datatype MachType = SPARC | MIPS | I386
val mach_type : MachType
eqtype register
val gcs : register list
val non_gcs : register list
val fps : register list
val caller_arg : register
val callee_arg : register
val caller_arg_regs : register list
val callee_arg_regs : register list
val caller_closure : register
val callee_closure : register
val fp_arg_regs : register list
val fp : register
val sp : register
val handler : register
val global : register
val fp_global : register
val implicit : register
val zero : register option
val tail_arg : register
val tail_closure : register
val corrupted_by_callee : {gc : register Set.Set,
non_gc : register Set.Set,
fp : register Set.Set}
val corrupted_by_alloc : {gc : register Set.Set,
non_gc : register Set.Set,
fp : register Set.Set}
val referenced_by_alloc : {gc : register Set.Set,
non_gc : register Set.Set,
fp : register Set.Set}
val defined_on_entry : {gc : register Set.Set,
non_gc : register Set.Set,
fp : register Set.Set}
val reserved : {gc : register Set.Set, non_gc : register Set.Set, fp : register Set.Set}
val debugging_reserved : {gc : register Set.Set, non_gc : register Set.Set, fp : register Set.Set}
val reserved_but_preferencable : {gc : register list, non_gc : register list, fp : register list}
val debugging_reserved_but_preferencable : {gc : register list, non_gc : register list, fp : register list}
val temporary : {gc : register list,
non_gc : register list,
fp : register list}
val allocation_order :
{gc : register * register -> bool,
non_gc : register * register -> bool,
fp : register * register -> bool}
val allocation_equal :
{gc : register * register -> bool,
non_gc : register * register -> bool,
fp : register * register -> bool}
val do_unspilling : bool
val print_register : register -> string
val has_immediate_stores : bool
val digits_in_real : int
val bits_per_word : int
val leaf_regs : int
val use_stack_colourer : bool
exception Ord
exception Chr
val ord: string -> int
val chr: int -> string
end
;
