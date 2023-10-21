require "../utils/set";
require "../utils/crash";
require "../main/machspec";
require "i386types";
functor I386Spec (
structure I386Types : I386TYPES
structure Set : SET
structure Crash : CRASH
) : MACHSPEC =
struct
structure Set = Set
datatype MachType = SPARC | MIPS | I386
val mach_type : MachType = I386
type register = I386Types.I386_Reg
local
open I386Types
in
val caller_arg = caller_arg
val callee_arg = callee_arg
val caller_arg_regs = [caller_arg, o_arg1, o_arg2, o_arg3, o_arg4, o_arg5, o_arg6, o_arg7]
val callee_arg_regs = [callee_arg, i_arg1, i_arg2, i_arg3, i_arg4, i_arg5, i_arg6, i_arg7]
val caller_closure = caller_closure
val callee_closure = callee_closure
val fp_arg_regs = []
val tail_arg = callee_arg
val tail_closure = caller_closure
val fp = stack
val sp = sp
val handler = heap
val global = global
val implicit = implicit
val fp_global = stack
val zero = NONE
val gcs = [EAX, EDX]
val non_gcs = []
val fps : register list = case I386Types.fp_used of
I386Types.single =>
Crash.impossible "i386spec not configured for single-float case"
| I386Types.double =>
[]
| I386Types.extended =>
Crash.impossible "i386spec not configured for extended-float case"
val empty_register_set : register Set.Set = Set.empty_set
val corrupted_by_callee =
{gc = Set.list_to_set(caller_closure :: implicit :: caller_arg_regs),
non_gc = empty_register_set,
fp = empty_register_set}
val corrupted_by_alloc =
{gc = empty_register_set,
non_gc = empty_register_set,
fp = empty_register_set }
val defined_on_entry =
{gc = Set.list_to_set [caller_closure],
non_gc = empty_register_set,
fp = empty_register_set }
val referenced_by_alloc =
{gc = Set.list_to_set [callee_closure],
non_gc = empty_register_set,
fp = empty_register_set }
val reserved =
{gc = Set.list_to_set [sp, fp, global,
implicit,
handler, callee_closure,
o_arg1, o_arg2, o_arg3, o_arg4, o_arg5, o_arg6, o_arg7,
i_arg1, i_arg2, i_arg3, i_arg4, i_arg5, i_arg6, i_arg7],
non_gc = empty_register_set,
fp = Set.list_to_set[fp_global]}
val debugging_reserved = reserved
val reserved_but_preferencable =
{gc = [callee_closure, global, i_arg1, i_arg2, i_arg3, i_arg4, i_arg5, i_arg6, i_arg7],
non_gc = [],
fp = []}
val debugging_reserved_but_preferencable =
{gc = [],
non_gc = [],
fp = []}
val temporary =
{gc = [],
non_gc = [],
fp = []}
local
fun rank EAX = 2
| rank EBX = 0
| rank ECX = 5
| rank EDX = 3
| rank EBP = 1
| rank ESP = 5
| rank EDI = 4
| rank ESI = 5
| rank _ = 5
fun order (reg, reg') =
let
val r = rank reg
val r' = rank reg'
in
r < r'
end
fun equal (reg, reg') =
let
val r = rank reg
val r' = rank reg'
in
r = r'
end
fun fp_order(reg, reg') =
let
val b1 = Set.is_member(reg, #fp corrupted_by_callee)
val b2 = Set.is_member(reg', #fp corrupted_by_callee)
in
b1 andalso not b2
end
fun fp_equal (reg, reg') =
let
val b1 = Set.is_member(reg, #fp corrupted_by_callee)
val b2 = Set.is_member(reg', #fp corrupted_by_callee)
in
b1 = b2
end
in
val allocation_order =
{gc = order,
non_gc = order,
fp = fp_order}
val allocation_equal =
{gc = equal,
non_gc = equal,
fp = fp_equal}
end
end
val do_unspilling = false
val print_register = I386Types.reg_to_string
val has_immediate_stores = true
val digits_in_real = I386Types.digits_in_real
val bits_per_word = I386Types.bits_per_word
val leaf_regs = 0
val use_stack_colourer = false
exception Ord = I386Types.Ord
exception Chr = I386Types.Chr
val ord = I386Types.ord
val chr = I386Types.chr
end
;
