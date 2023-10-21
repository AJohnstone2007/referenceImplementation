require "../utils/crash";
require "../utils/lists";
require "../main/machspec";
require "mirtypes";
require "mirregisters";
functor MirRegisters(
structure MirTypes : MIRTYPES where type GC.T = int where type NonGC.T = int where type FP.T = int
structure MachSpec : MACHSPEC
structure Lists : LISTS
structure Crash : CRASH
) : MIRREGISTERS =
struct
structure MirTypes = MirTypes
structure MachSpec = MachSpec
structure Set = MachSpec.Set
fun crash message = Crash.impossible ("MirRegisters: " ^ message)
fun ++r = let val n = !r in r:=n+1; n end
val next_gc = ref 0
val next_non_gc = ref 0
val next_fp = ref 0
fun new_gc () = (++next_gc)
fun new_non_gc () = (++next_non_gc)
fun new_fp () = (++next_fp)
val caller_equal_callee = MachSpec.caller_arg_regs = MachSpec.callee_arg_regs
val caller_arg_regs = map (fn _ => new_gc ()) MachSpec.caller_arg_regs
val caller_arg =
case caller_arg_regs of
(r::_) => r
| _ => Crash.impossible "No caller arg reg"
val callee_arg_regs =
if caller_equal_callee then
caller_arg_regs
else
let
fun assoc([], el) = NONE
| assoc((x, y) :: rest, el) =
if y = el then SOME x else assoc(rest, el)
val callee_arg_regs =
map
(fn _ => new_gc ())
MachSpec.callee_arg_regs
val assoc1 = Lists.zip(caller_arg_regs, MachSpec.caller_arg_regs)
val assoc2 = Lists.zip(callee_arg_regs, MachSpec.callee_arg_regs)
val callee_arg_regs =
map
(fn reg =>
case assoc(assoc1, reg) of
SOME x => x
| NONE =>
case assoc(assoc2, reg) of
SOME x => x
| NONE => Crash.impossible"MirRegisters: assoc")
MachSpec.callee_arg_regs
in
callee_arg_regs
end
val callee_arg =
case callee_arg_regs of
(r::_) => r
| _ => Crash.impossible "No callee arg reg"
val fp_arg_regs = map (fn _ => new_fp ()) MachSpec.fp_arg_regs
val caller_closure = new_gc ()
val callee_closure = new_gc ()
val fp = new_gc ()
val sp = new_gc ()
val global = new_gc ()
val fp_global = new_fp ()
val implicit = new_gc ()
val zero_virtual = new_gc ()
val zero =
case MachSpec.zero
of SOME _ => SOME zero_virtual
| NONE => NONE
val tail_arg_regs =
if MachSpec.tail_arg = MachSpec.callee_arg
then callee_arg_regs
else
caller_arg_regs
val tail_arg =
case tail_arg_regs of
(r::_) => r
| _ => Crash.impossible "No tail arg reg"
val tail_closure =
if MachSpec.tail_closure = MachSpec.callee_closure then
callee_closure
else
caller_closure
val special_assignments =
let
val callers = Lists.zip (caller_arg_regs, MachSpec.caller_arg_regs)
val callees = Lists.zip (callee_arg_regs, MachSpec.callee_arg_regs)
val callees = Lists.difference(callees, callers)
in
{gc = callers @
callees @
[(caller_closure, MachSpec.caller_closure),
(callee_closure, MachSpec.callee_closure),
(fp, MachSpec.fp),
(sp, MachSpec.sp),
(global, MachSpec.global),
(implicit,MachSpec.implicit)] @
(case MachSpec.zero
of SOME zero_real => [(zero_virtual, zero_real)]
| NONE => []),
non_gc = [],
fp = Lists.zip (fp_global :: fp_arg_regs, MachSpec.fp_global :: MachSpec.fp_arg_regs)}
end
val assignments =
{gc = (#gc special_assignments) @
(map (fn r => (new_gc (), r))
(Lists.difference (MachSpec.gcs,map #2 (#gc special_assignments)))),
non_gc = (#non_gc special_assignments) @
(map (fn r => (new_non_gc (), r))
(Lists.difference (MachSpec.non_gcs,map #2 (#non_gc special_assignments)))),
fp = (#fp special_assignments) @
(map (fn r => (new_fp (), r))
(Lists.difference (MachSpec.fps,map #2 (#fp special_assignments))))}
val preassigned =
{gc = MirTypes.GC.Pack.from_list (map (fn(v,r)=>v) (#gc assignments)),
non_gc = MirTypes.NonGC.Pack.from_list (map (fn(v,r)=>v) (#non_gc assignments)),
fp = MirTypes.FP.Pack.from_list (map (fn(v,r)=>v) (#fp assignments))}
val gc_assignments = MirTypes.GC.Map.from_list (#gc assignments)
val non_gc_assignments = MirTypes.NonGC.Map.from_list (#non_gc assignments)
val fp_assignments = MirTypes.FP.Map.from_list (#fp assignments)
val machine_register_assignments =
{gc = gc_assignments,
non_gc = non_gc_assignments,
fp = fp_assignments}
val machine_to_virtual =
let
fun rassoc list real =
let
fun find [] = crash ("Failed to find virtual register for " ^ MachSpec.print_register real)
| find ((virtual, real')::rest) = if real = real' then virtual else find rest
in
find list
end
in
{gc = rassoc (#gc assignments),
non_gc = rassoc (#non_gc assignments),
fp = rassoc (#fp assignments)}
end
local
fun separate (convert, reserved, registers) =
let
fun separate' (g, []) = convert g
| separate' (g, (virtual,real) ::rest) =
if Set.is_member (real,reserved) then
separate' (g, rest)
else
separate' (virtual::g, rest)
in
separate' ([], registers)
end
val gc_general =
separate (MirTypes.GC.Pack.from_list, #gc MachSpec.reserved, #gc assignments)
val non_gc_general =
separate (MirTypes.NonGC.Pack.from_list, #non_gc MachSpec.reserved, #non_gc assignments)
val fp_general =
separate (MirTypes.FP.Pack.from_list, #fp MachSpec.reserved, #fp assignments)
val debugging_gc_general =
separate (MirTypes.GC.Pack.from_list, #gc MachSpec.debugging_reserved, #gc assignments)
val debugging_non_gc_general =
separate (MirTypes.NonGC.Pack.from_list, #non_gc MachSpec.debugging_reserved, #non_gc assignments)
val debugging_fp_general =
separate (MirTypes.FP.Pack.from_list, #fp MachSpec.debugging_reserved, #fp assignments)
val gc_general_for_preferencing =
MirTypes.GC.Pack.from_list(map (#gc machine_to_virtual) (#gc MachSpec.reserved_but_preferencable))
val non_gc_general_for_preferencing =
MirTypes.NonGC.Pack.from_list(map (#non_gc machine_to_virtual) (#non_gc MachSpec.reserved_but_preferencable))
val fp_general_for_preferencing =
MirTypes.FP.Pack.from_list(map (#fp machine_to_virtual) (#fp MachSpec.reserved_but_preferencable))
val debugging_gc_general_for_preferencing =
MirTypes.GC.Pack.from_list(map (#gc machine_to_virtual) (#gc MachSpec.debugging_reserved_but_preferencable))
val debugging_non_gc_general_for_preferencing =
MirTypes.NonGC.Pack.from_list(map (#non_gc machine_to_virtual) (#non_gc MachSpec.debugging_reserved_but_preferencable))
val debugging_fp_general_for_preferencing =
MirTypes.FP.Pack.from_list(map (#fp machine_to_virtual) (#fp MachSpec.debugging_reserved_but_preferencable))
in
val general_purpose = {gc = gc_general,
non_gc = non_gc_general,
fp = fp_general}
val debugging_general_purpose =
{gc = debugging_gc_general,
non_gc = debugging_non_gc_general,
fp = debugging_fp_general}
val gp_for_preferencing =
{gc = gc_general_for_preferencing,
non_gc = non_gc_general_for_preferencing,
fp = fp_general_for_preferencing}
val debugging_gp_for_preferencing =
{gc = debugging_gc_general_for_preferencing,
non_gc = debugging_non_gc_general_for_preferencing,
fp = debugging_fp_general_for_preferencing}
end
local
val gc = MirTypes.GC.Map.apply gc_assignments
val non_gc = MirTypes.NonGC.Map.apply non_gc_assignments
val fp = MirTypes.FP.Map.apply fp_assignments
val {gc = gc_order, non_gc = non_gc_order, fp = fp_order} = MachSpec.allocation_order
in
val allocation_order =
{gc = fn (r,r') => gc_order (gc r, gc r'),
non_gc = fn (r,r') => non_gc_order (non_gc r, non_gc r'),
fp = fn (r,r') => fp_order (fp r, fp r')}
end
local
val gc = MirTypes.GC.Map.apply gc_assignments
val non_gc = MirTypes.NonGC.Map.apply non_gc_assignments
val fp = MirTypes.FP.Map.apply fp_assignments
val {gc = gc_equal, non_gc = non_gc_equal, fp = fp_equal} = MachSpec.allocation_equal
in
val allocation_equal =
{gc = fn (r,r') => gc_equal (gc r, gc r'),
non_gc = fn (r,r') => non_gc_equal (non_gc r, non_gc r'),
fp = fn (r,r') => fp_equal (fp r, fp r')}
end
local
fun corrupt set (done, []) = done
| corrupt set (done, (virtual, real)::rest) =
if Set.is_member (real, set) then
corrupt set (virtual::done, rest)
else
corrupt set (done, rest)
in
val corrupted_by_callee =
{gc =
MirTypes.GC.Pack.from_list
(corrupt (#gc MachSpec.corrupted_by_callee) ([], #gc assignments)),
non_gc =
MirTypes.NonGC.Pack.from_list
(corrupt (#non_gc MachSpec.corrupted_by_callee) ([], #non_gc assignments)),
fp =
MirTypes.FP.Pack.from_list
(corrupt (#fp MachSpec.corrupted_by_callee) ([], #fp assignments))}
val corrupted_by_alloc =
{gc =
MirTypes.GC.Pack.from_list
(corrupt (#gc MachSpec.corrupted_by_alloc) ([], #gc assignments)),
non_gc =
MirTypes.NonGC.Pack.from_list
(corrupt (#non_gc MachSpec.corrupted_by_alloc) ([], #non_gc assignments)),
fp =
MirTypes.FP.Pack.from_list
(corrupt (#fp MachSpec.corrupted_by_alloc) ([], #fp assignments))}
val referenced_by_alloc =
{gc =
MirTypes.GC.Pack.from_list
(corrupt (#gc MachSpec.referenced_by_alloc) ([], #gc assignments)),
non_gc =
MirTypes.NonGC.Pack.from_list
(corrupt (#non_gc MachSpec.referenced_by_alloc) ([], #non_gc assignments)),
fp =
MirTypes.FP.Pack.from_list
(corrupt (#fp MachSpec.referenced_by_alloc) ([], #fp assignments))}
end
val temporary =
{gc = (map (#gc machine_to_virtual) (#gc MachSpec.temporary)),
non_gc = (map (#non_gc machine_to_virtual) (#non_gc MachSpec.temporary)),
fp = (map (#fp machine_to_virtual) (#fp MachSpec.temporary))}
val defined_on_entry =
{gc =
MirTypes.GC.Pack.from_list
((callee_closure :: map (#gc machine_to_virtual) (Set.set_to_list(#gc MachSpec.defined_on_entry))) @
(case zero
of SOME virtual_zero => [virtual_zero]
| NONE => []) @
[fp, sp, implicit]),
non_gc = MirTypes.NonGC.Pack.empty,
fp = MirTypes.FP.Pack.empty}
val defined_on_exit =
{gc =
MirTypes.GC.Pack.from_list
((callee_arg) ::
[fp, sp, implicit]),
non_gc = MirTypes.NonGC.Pack.empty,
fp = MirTypes.FP.Pack.empty}
val pack_next = {gc = !next_gc,
non_gc = !next_non_gc,
fp = !next_fp}
end
;
