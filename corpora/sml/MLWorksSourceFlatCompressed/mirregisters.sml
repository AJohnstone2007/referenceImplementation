require "../main/machspec";
require "mirtypes";
signature MIRREGISTERS =
sig
structure MirTypes : MIRTYPES
structure MachSpec : MACHSPEC
val machine_register_assignments :
{gc : (MachSpec.register) MirTypes.GC.Map.T,
non_gc : (MachSpec.register) MirTypes.NonGC.Map.T,
fp : (MachSpec.register) MirTypes.FP.Map.T}
val general_purpose :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val debugging_general_purpose :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val gp_for_preferencing :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val debugging_gp_for_preferencing :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val allocation_order :
{gc : MirTypes.GC.T * MirTypes.GC.T -> bool,
non_gc : MirTypes.NonGC.T * MirTypes.NonGC.T -> bool,
fp : MirTypes.FP.T * MirTypes.FP.T -> bool}
val allocation_equal :
{gc : MirTypes.GC.T * MirTypes.GC.T -> bool,
non_gc : MirTypes.NonGC.T * MirTypes.NonGC.T -> bool,
fp : MirTypes.FP.T * MirTypes.FP.T -> bool}
val preassigned :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val temporary :
{gc : MirTypes.GC.T list,
non_gc : MirTypes.NonGC.T list,
fp : MirTypes.FP.T list}
val caller_arg : MirTypes.GC.T
val callee_arg : MirTypes.GC.T
val caller_arg_regs : MirTypes.GC.T list
val callee_arg_regs : MirTypes.GC.T list
val caller_closure : MirTypes.GC.T
val callee_closure : MirTypes.GC.T
val tail_arg : MirTypes.GC.T
val tail_arg_regs : MirTypes.GC.T list
val tail_closure : MirTypes.GC.T
val fp : MirTypes.GC.T
val sp : MirTypes.GC.T
val global : MirTypes.GC.T
val implicit : MirTypes.GC.T
val zero : MirTypes.GC.T option
val fp_arg_regs: MirTypes.FP.T list
val defined_on_entry :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val defined_on_exit :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val corrupted_by_callee :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val corrupted_by_alloc :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val referenced_by_alloc :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val pack_next : {gc : int, non_gc : int, fp : int}
end
;
