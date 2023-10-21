require "../utils/set";
require "../utils/crash";
require "../main/machspec";
require "machtypes";
functor MachSpec (
structure MachTypes : MACHTYPES
structure Set : SET
structure Crash : CRASH
) : MACHSPEC =
struct
structure Set = Set
datatype MachType = SPARC | MIPS | I386
val mach_type : MachType = SPARC
type register = MachTypes.Sparc_Reg
local
open MachTypes
in
val caller_arg = MachTypes.caller_arg
val callee_arg = MachTypes.callee_arg
val caller_arg_regs = MachTypes.caller_arg_regs
val callee_arg_regs = MachTypes.callee_arg_regs
val caller_closure = MachTypes.caller_closure
val callee_closure = MachTypes.callee_closure
val fp_arg_regs = [F0,F2,F6,F8,F10,F12,F14,F16]
val tail_arg = callee_arg
val tail_closure = callee_closure
val fp = MachTypes.fp
val sp = MachTypes.sp
val handler = MachTypes.handler
val global = MachTypes.global
val implicit = MachTypes.implicit
val fp_global = MachTypes.fp_global
val zero = SOME G0
val gcs = [I0,I2,I3, I4, I5,
L0, L1, L2, L3, L4, L5, L6, L7,
O0,O2,O3, O4, O5, lr, G7]
val non_gcs = []
val fps = case MachTypes.fp_used of
MachTypes.single =>
Crash.impossible "machspec not configured for single-float case"
| MachTypes.double =>
[F24,F26,F28,F30, F16,F18,F20,F22, F8,F10,F12,F14, F0,F2,F6]
| MachTypes.extended =>
Crash.impossible "machspec not configured for extended-float case"
val empty_register_set = Set.empty_set : register Set.Set
val corrupted_by_callee =
{gc = Set.list_to_set [O1, O2, O3, O4, O5, lr, G4, G5, G7],
non_gc = empty_register_set,
fp = Set.list_to_set[F0, F2, F4, F6, F8, F10, F12, F14,
F16, F18, F20, F22, F24, F26, F28, F30] }
val corrupted_by_alloc =
{gc = Set.list_to_set [lr],
non_gc = empty_register_set,
fp = empty_register_set }
val defined_on_entry =
{gc = empty_register_set,
non_gc = empty_register_set,
fp = empty_register_set }
val referenced_by_alloc =
{gc = Set.list_to_set [callee_closure],
non_gc = empty_register_set,
fp = empty_register_set }
val normal_temporaries = [L5]
val reserved =
{gc = Set.list_to_set ([callee_closure, sp, fp, global,
lr, gc1, gc2, implicit,
G0, stack_limit, handler] @ normal_temporaries),
non_gc = empty_register_set,
fp = Set.list_to_set [fp_global, F24, F26, F28]}
val debugging_reserved =
{gc = Set.list_to_set([callee_arg] @ [callee_closure, sp, fp, global,
lr, gc1, gc2, implicit,
G0, stack_limit, handler] @ normal_temporaries),
non_gc = empty_register_set,
fp = Set.list_to_set[fp_global, F24, F26, F28]}
val reserved_but_preferencable =
{gc = [callee_closure, global, lr],
non_gc = [],
fp = [fp_global]}
val debugging_reserved_but_preferencable =
{gc = [],
non_gc = [],
fp = []}
val temporary =
{gc = normal_temporaries @ [lr],
non_gc = [] : register list,
fp = [F24,F26,F28]}
local
fun rank I0 = 0
| rank I1 = 0
| rank I2 = 0
| rank I3 = 0
| rank I4 = 0
| rank I5 = 0
| rank I6 = 0
| rank I7 = 0
| rank G0 = 0
| rank G1 = 0
| rank G2 = 0
| rank G3 = 0
| rank G4 = 0
| rank G5 = 0
| rank G6 = 0
| rank G7 = 0
| rank O0 = 1
| rank O1 = 1
| rank O2 = 1
| rank O3 = 1
| rank O4 = 1
| rank O5 = 1
| rank O6 = 1
| rank O7 = 1
| rank L0 = 2
| rank L1 = 2
| rank L2 = 2
| rank L3 = 2
| rank L4 = 2
| rank L5 = 2
| rank L6 = 2
| rank L7 = 2
| rank _ = 3
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
fun fp_equal(reg, reg') =
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
val do_unspilling = true
val print_register = MachTypes.reg_to_string
val has_immediate_stores = false
val digits_in_real = MachTypes.digits_in_real
val bits_per_word = MachTypes.bits_per_word
val leaf_regs = 6
val use_stack_colourer = true
exception Ord = MachTypes.Ord
exception Chr = MachTypes.Chr
val ord = MachTypes.ord
val chr = MachTypes.chr
end
;
