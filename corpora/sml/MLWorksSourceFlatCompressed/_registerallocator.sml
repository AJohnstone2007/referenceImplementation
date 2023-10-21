require "../basis/__int";
require "../utils/diagnostic";
require "../utils/lists";
require "../utils/crash";
require "mirtables";
require "mirprint";
require "mirprocedure";
require "mirregisters";
require "registercolourer";
require "registerallocator";
functor RegisterAllocator (
structure MirProcedure : MIRPROCEDURE
structure Diagnostic : DIAGNOSTIC
structure MirRegisters : MIRREGISTERS
structure MirTables : MIRTABLES
structure MirPrint : MIRPRINT
structure Lists : LISTS
structure Crash : CRASH
structure GCColourer : REGISTERCOLOURER
sharing GCColourer.Register = MirRegisters.MirTypes.GC
structure NonGCColourer : REGISTERCOLOURER
sharing NonGCColourer.Register = MirRegisters.MirTypes.NonGC
structure FPColourer : REGISTERCOLOURER
sharing FPColourer.Register = MirRegisters.MirTypes.FP
sharing MirProcedure.MirTypes = MirPrint.MirTypes = MirRegisters.MirTypes = MirTables.MirTypes
sharing MirProcedure.Text = Diagnostic.Text
) : REGISTERALLOCATOR =
struct
structure MirTypes = MirRegisters.MirTypes
structure Set = MirTypes.Set
structure MirProcedure = MirProcedure
structure Diagnostic = Diagnostic
structure Map = MirTypes.Map
structure MachSpec = MirRegisters.MachSpec
val do_unspilling = MachSpec.do_unspilling
val do_diagnostics = false
fun diagnostic (level, output_function) =
if do_diagnostics then
Diagnostic.output level
(fn verbosity => "RegisterAllocator: " :: (output_function verbosity))
else ()
fun crash message = Crash.impossible ("RegisterAllocator: " ^ message)
type Graph =
{gc : GCColourer.Graph,
non_gc : NonGCColourer.Graph,
fp : FPColourer.Graph}
fun empty ({gc = nr_gc, non_gc = nr_non_gc, fp = nr_fp},make_debugging_code) =
{gc = GCColourer.empty (nr_gc,make_debugging_code),
non_gc = NonGCColourer.empty (nr_non_gc,make_debugging_code),
fp = FPColourer.empty (nr_fp,make_debugging_code)}
fun clash {gc, non_gc, fp} ({gc = gc_defined, non_gc = non_gc_defined, fp = fp_defined},
{gc = gc_referenced, non_gc = non_gc_referenced, fp = fp_referenced},
{gc = gc_live, non_gc = non_gc_live, fp = fp_live}) =
(GCColourer.clash (gc, gc_defined, gc_referenced, gc_live);
NonGCColourer.clash (non_gc, non_gc_defined, non_gc_referenced, non_gc_live);
FPColourer.clash (fp, fp_defined, fp_referenced, fp_live))
local
val frame = MirTypes.GC_REG MirRegisters.fp
val gc_temporaries = MLWorks.Internal.Array.arrayoflist (#gc MirRegisters.temporary)
val non_gc_temporaries = MLWorks.Internal.Array.arrayoflist (#non_gc MirRegisters.temporary)
val fp_temporaries = MLWorks.Internal.Array.arrayoflist (#fp MirRegisters.temporary)
fun generate_spills (instructions, []) = instructions
| generate_spills (instructions, f::fs) =
generate_spills (MirProcedure.I {defined = MirProcedure.empty,
referenced = MirProcedure.empty,
branches = Set.empty_set,
excepts = [],
opcode = f ()} ::
instructions, fs)
in
fun substitute ({gc = gc_assign, non_gc = non_gc_assign, fp = fp_assign},
{gc = gc_spills, non_gc = non_gc_spills, fp = fp_spills},
{gc = gc_spills', non_gc = non_gc_spills', fp = fp_spills'}) =
if gc_spills + non_gc_spills + fp_spills + gc_spills' + non_gc_spills' + fp_spills' = 0 then
let
val message =
"The register colourers said that there were no spills, but I've found one."
val substitute_opcode =
MirProcedure.substitute
{gc = fn r => case gc_assign r
of GCColourer.REGISTER r => r
| GCColourer.SPILL _ => crash message,
non_gc = fn r => case non_gc_assign r
of NonGCColourer.REGISTER r => r
| NonGCColourer.SPILL _ => crash message,
fp = fn r => case fp_assign r
of FPColourer.REGISTER r => r
| FPColourer.SPILL _ => crash message}
in
fn (tag, MirProcedure.B (annotation, instructions)) =>
(MirProcedure.B
(annotation,
Lists.reducer
(fn (MirProcedure.I {defined, referenced, branches, excepts, opcode}, instructions) =>
let
val instruction =
MirProcedure.I {defined = MirProcedure.empty,
referenced = MirProcedure.empty,
branches = branches,
excepts = excepts,
opcode = substitute_opcode opcode}
in
instruction :: instructions
end)
(instructions, [])))
end
else
let
val gc_slots = MLWorks.Internal.Array.array (gc_spills, MirTypes.GC.new ())
val non_gc_slots = MLWorks.Internal.Array.array (non_gc_spills, MirTypes.NonGC.new ())
val fp_slots = MLWorks.Internal.Array.array (fp_spills, MirTypes.FP.new ())
local
open MirTypes
open MLWorks.Internal.Array nonfix sub
in
fun bad_spill () = crash "spill found to be register or other problem"
fun gc_in (instructions, r, t) =
case gc_assign r
of GCColourer.REGISTER _ => instructions
| GCColourer.SPILL slot =>
(update (gc_slots, slot, sub (gc_temporaries, t));
(fn () => STOREOP (LDREF, GC_REG (sub (gc_slots, slot)),
frame, GP_IMM_SYMB (GC_SPILL_SLOT (MirTypes.SIMPLE (slot+gc_spills')))))
:: instructions)
fun non_gc_in (instructions, r, t) =
case non_gc_assign r
of NonGCColourer.REGISTER _ => instructions
| NonGCColourer.SPILL slot =>
(update (non_gc_slots, slot, sub (non_gc_temporaries, t));
(fn () => STOREOP (LDREF, NON_GC_REG (sub (non_gc_slots, slot)),
frame, GP_IMM_SYMB (NON_GC_SPILL_SLOT (MirTypes.SIMPLE (slot+non_gc_spills')))))
:: instructions)
fun fp_in (instructions, FP_REG r, t) =
case fp_assign r
of FPColourer.REGISTER _ => instructions
| FPColourer.SPILL slot =>
(update (fp_slots, slot, sub (fp_temporaries, t));
(fn () => STOREFPOP (FLDREF, FP_REG (sub (fp_slots, slot)),
frame, GP_IMM_SYMB (FP_SPILL_SLOT (MirTypes.SIMPLE (slot+fp_spills')))))
:: instructions)
fun gc_spill r =
case gc_assign r of
GCColourer.REGISTER _ => bad_spill()
| GCColourer.SPILL slot => slot
fun reg_spill(GC_REG r) = gc_spill r
| reg_spill _ = bad_spill()
fun gp_spill(GP_GC_REG r) = gc_spill r
| gp_spill _ = bad_spill()
fun add_spill(instructions, r, s, t) =
let
val slot1 = reg_spill r
val slot2 = reg_spill s
val slot3 = gp_spill t
in
(fn () => BINARY(ADDU, GC_REG(sub(gc_slots, slot1)),
GP_GC_REG(sub(gc_slots, slot2)),
GP_GC_REG(sub(gc_slots, slot3)))) :: instructions
end
fun reg_in (instructions, GC_REG r, t) = gc_in (instructions, r, t)
| reg_in (instructions, NON_GC_REG r, t) = non_gc_in (instructions, r, t)
fun gp_in (instructions, GP_GC_REG r, t) = gc_in (instructions, r, t)
| gp_in (instructions, GP_NON_GC_REG r, t) = non_gc_in (instructions, r, t)
| gp_in (instructions, _, t) = instructions
fun gc_out (instructions, r, t) =
case gc_assign r
of GCColourer.REGISTER _ => instructions
| GCColourer.SPILL slot =>
(update (gc_slots, slot, sub (gc_temporaries, t));
(fn () => STOREOP (STREF, GC_REG (sub (gc_slots, slot)),
frame, GP_IMM_SYMB (GC_SPILL_SLOT (MirTypes.SIMPLE (slot+gc_spills')))))
:: instructions)
fun non_gc_out (instructions, r, t) =
case non_gc_assign r
of NonGCColourer.REGISTER _ => instructions
| NonGCColourer.SPILL slot =>
(update (non_gc_slots, slot, sub (non_gc_temporaries, t));
(fn () => STOREOP (STREF, NON_GC_REG (sub (non_gc_slots, slot)),
frame, GP_IMM_SYMB (NON_GC_SPILL_SLOT (MirTypes.SIMPLE (slot+non_gc_spills')))))
:: instructions)
fun fp_out (instructions, FP_REG r, t) =
case fp_assign r
of FPColourer.REGISTER _ => instructions
| FPColourer.SPILL slot =>
(update (fp_slots, slot, sub (fp_temporaries, t));
(fn () => STOREFPOP (FSTREF, FP_REG (sub (fp_slots, slot)),
frame, GP_IMM_SYMB (FP_SPILL_SLOT (MirTypes.SIMPLE (slot+fp_spills')))))
:: instructions)
fun reg_out (instructions, GC_REG r, t) = gc_out (instructions, r, t)
| reg_out (instructions, NON_GC_REG r, t) = non_gc_out (instructions, r, t)
fun gp_out (instructions, GP_GC_REG r, t) = gc_out (instructions, r, t)
| gp_out (instructions, GP_NON_GC_REG r, t) = non_gc_out (instructions, r, t)
| gp_out (instructions, _, t) = instructions
end
val substitute_opcode =
MirProcedure.substitute
{gc = fn r => case gc_assign r
of GCColourer.REGISTER r => r
| GCColourer.SPILL n => MLWorks.Internal.Array.sub (gc_slots, n),
non_gc = fn r => case non_gc_assign r
of NonGCColourer.REGISTER r => r
| NonGCColourer.SPILL n => MLWorks.Internal.Array.sub (non_gc_slots, n),
fp = fn r => case fp_assign r
of FPColourer.REGISTER r => r
| FPColourer.SPILL n => MLWorks.Internal.Array.sub (fp_slots, n)}
local
open MirTypes
in
val ref_opcode = ref (COMMENT "")
fun is_gc_spill r =
case gc_assign r
of GCColourer.REGISTER _ => false
| GCColourer.SPILL _ => true
fun is_non_gc_spill r =
case non_gc_assign r
of NonGCColourer.REGISTER _ => false
| NonGCColourer.SPILL _ => true
fun is_reg_spill(GC_REG r) = is_gc_spill r
| is_reg_spill(NON_GC_REG r) = is_non_gc_spill r
fun is_gp_spill(GP_GC_REG r) = is_gc_spill r
| is_gp_spill(GP_NON_GC_REG r) = is_non_gc_spill r
| is_gp_spill _ = false
fun same_reg_and_gp_spill(r, g) =
is_reg_spill r andalso is_gp_spill g andalso
reg_spill r = gp_spill g
fun do_store_spill(LD, _, _, _) = bad_spill()
| do_store_spill(LDREF, _, _, _) = bad_spill()
| do_store_spill(LDB, _, _, _) = bad_spill()
| do_store_spill(store, in0, in1, in2) =
if is_reg_spill in0 then
if is_reg_spill in1 then
if is_gp_spill in2 then
(
ref_opcode :=
STOREOP(store, in0, in1, GP_IMM_INT 0);
(reg_in(gp_in (add_spill(reg_in([], in0, 1), in1, in1, in2), in2, 1), in1, 0),
[
let
val slot = reg_spill in1
in
fn () => NULLARY(CLEAN, GC_REG(MLWorks.Internal.Array.sub(gc_slots, slot)))
end]))
else
(reg_in (reg_in (gp_in ([], in2, 1), in1, 1), in0, 0), [])
else
(reg_in (reg_in (gp_in ([], in2, 1), in1, 1), in0, 0), [])
else
(reg_in (reg_in (gp_in ([], in2, 1), in1, 0), in0, 0), [])
fun spill (TBINARY (_, _, out0, in1, in2)) =
if same_reg_and_gp_spill(out0, in2) then
(
(gp_in (gp_in ([], in2, 0), in1, 1), reg_out ([], out0, 0)))
else
(gp_in (gp_in ([], in2, 1), in1, 0), reg_out ([], out0, 0))
| spill (BINARY (_, out0, in1, in2)) =
if same_reg_and_gp_spill(out0, in2) then
(
(gp_in (gp_in ([], in2, 0), in1, 1), reg_out ([], out0, 0)))
else
(gp_in (gp_in ([], in2, 1), in1, 0), reg_out ([], out0, 0))
| spill (TBINARYFP (_, _, out0, in1, in2)) = (fp_in (fp_in ([], in2, 2), in1, 1), fp_out ([], out0, 0))
| spill (BINARYFP (_, out0, in1, in2)) = (fp_in (fp_in ([], in2, 2), in1, 1), fp_out ([], out0, 0))
| spill (UNARY (_, out0, in1)) = (gp_in ([], in1, 1), reg_out ([], out0, 0))
| spill (NULLARY (_, out0)) = ([], reg_out ([], out0, 0))
| spill (UNARYFP (FMOVE, out0 as FP_REG r1, in1 as FP_REG r2)) =
(case (fp_assign r1, fp_assign r2) of
(FPColourer.REGISTER r, FPColourer.SPILL n) =>
(ref_opcode :=
STOREFPOP (FLDREF, FP_REG r, frame, GP_IMM_SYMB (FP_SPILL_SLOT (MirTypes.SIMPLE (n + fp_spills'))));
([],[]))
| (FPColourer.SPILL n, FPColourer.REGISTER r) =>
(ref_opcode :=
STOREFPOP (FSTREF, FP_REG r, frame, GP_IMM_SYMB (FP_SPILL_SLOT (MirTypes.SIMPLE (n + fp_spills'))));
([],[]))
| _ => (fp_in ([], in1, 1), fp_out ([], out0, 0)))
| spill (UNARYFP (_, out0, in1)) = (fp_in ([], in1, 1), fp_out ([], out0, 0))
| spill (TUNARYFP (_, _, out0, in1)) = (fp_in ([], in1, 1), fp_out ([], out0, 0))
| spill (STACKOP (PUSH, in0, _)) = (reg_in ([], in0, 0), [])
| spill (STACKOP (POP, out0, _)) = ([], reg_out ([], out0, 0))
| spill (STOREOP (store as (ST, in0, in1, in2))) =
do_store_spill store
| spill (STOREOP (store as (STREF, in0, in1, in2))) =
do_store_spill store
| spill (STOREOP (store as (STB, in0, in1, in2))) =
do_store_spill store
| spill (STOREOP (LD, out0, in1, in2)) =
if same_reg_and_gp_spill(out0, in2) then
(
(reg_in (gp_in ([], in2, 0), in1, 1), reg_out ([], out0, 0)))
else
(reg_in (gp_in ([], in2, 1), in1, 0), reg_out ([], out0, 0))
| spill (STOREOP (LDREF, out0, in1, in2)) =
if same_reg_and_gp_spill(out0, in2) then
(
(reg_in (gp_in ([], in2, 0), in1, 1), reg_out ([], out0, 0)))
else
(reg_in (gp_in ([], in2, 1), in1, 0), reg_out ([], out0, 0))
| spill (STOREOP (LDB, out0, in1, in2)) =
if same_reg_and_gp_spill(out0, in2) then
(
(reg_in (gp_in ([], in2, 0), in1, 1), reg_out ([], out0, 0)))
else (reg_in (gp_in ([], in2, 1), in1, 0), reg_out ([], out0, 0))
| spill (IMMSTOREOP _) = Crash.impossible"Immediate store on spilling architecture"
| spill (STOREFPOP (FST, in0, in1, in2)) = (fp_in (reg_in (gp_in ([], in2, 1), in1, 0), in0, 0), [])
| spill (STOREFPOP (FSTREF, in0, in1, in2)) = (fp_in (reg_in (gp_in ([], in2, 1), in1, 0), in0, 0), [])
| spill (STOREFPOP (FLD, out0, in1, in2)) = (reg_in (gp_in ([], in2, 1), in1, 0), fp_out ([], out0, 0))
| spill (STOREFPOP (FLDREF, out0, in1, in2)) = (reg_in (gp_in ([], in2, 1), in1, 0), fp_out ([], out0, 0))
| spill (REAL (_, out0, in1)) = (gp_in ([], in1, 1), fp_out ([], out0, 0))
| spill (FLOOR (_, _, out0, in1)) = (fp_in ([], in1, 1), reg_out ([], out0, 0))
| spill (BRANCH (_, REG in0)) = (reg_in ([], in0, 0), [])
| spill (BRANCH (_, TAG _)) = ([], [])
| spill (TEST (_, _, in0, in1)) = (gp_in (gp_in ([], in0, 0), in1, 1), [])
| spill (FTEST (_, _, in0, in1)) = (fp_in (fp_in ([], in0, 0), in1, 1), [])
| spill (BRANCH_AND_LINK (_, REG in0,_,_)) = (reg_in ([], in0, 0), [])
| spill (BRANCH_AND_LINK (_, TAG _,_,_)) = ([], [])
| spill (TAIL_CALL (_, REG in0,_)) = (reg_in ([], in0, 0), [])
| spill (TAIL_CALL (_, TAG _,_)) = ([], [])
| spill (CALL_C) = ([], [])
| spill (SWITCH (_, in0, _)) = (reg_in ([], in0, 0), [])
| spill (ALLOCATE (_, out0, in1)) = (gp_in ([], in1, 1), reg_out ([], out0, 0))
| spill (ALLOCATE_STACK (_, out0, _, _)) = ([], reg_out ([], out0, 0))
| spill (DEALLOCATE_STACK _) = ([], [])
| spill (ADR (_, out0, _)) = ([], reg_out ([], out0, 0))
| spill (INTERCEPT) = ([], [])
| spill (INTERRUPT) = ([], [])
| spill (ENTER _) = ([], [])
| spill (RTS) = ([], [])
| spill (NEW_HANDLER(frame, tag)) = (reg_in([], frame, 0), [])
| spill (OLD_HANDLER) = ([], [])
| spill (RAISE in0) = (reg_in ([], in0, 0), [])
| spill (COMMENT _) = ([], [])
end
fun is_null_move (MirTypes.UNARYFP (MirTypes.FMOVE, MirTypes.FP_REG r1, MirTypes.FP_REG r2)) =
fp_assign r1 = fp_assign r2
| is_null_move (MirTypes.UNARY (MirTypes.MOVE, MirTypes.GC_REG r1, MirTypes.GP_GC_REG r2)) =
gc_assign r1 = gc_assign r2
| is_null_move _ = false
in
if do_unspilling then
fn (tag, MirProcedure.B (annotation, instructions)) =>
(MirProcedure.B
(annotation,
Lists.reducer
(fn (MirProcedure.I {defined, referenced, branches, excepts, opcode}, instructions) =>
if is_null_move opcode then instructions
else
let
val _ = ref_opcode := opcode
val (loads, stores) = spill opcode
val instruction =
MirProcedure.I {defined = MirProcedure.empty,
referenced = MirProcedure.empty,
branches = branches,
excepts = excepts,
opcode = substitute_opcode (!ref_opcode)}
in
generate_spills (instruction :: generate_spills (instructions, stores), loads)
end)
(instructions, [])))
else
let
val substitute_opcode =
MirProcedure.substitute
{gc =
fn r => case gc_assign r of
GCColourer.REGISTER r => r
| GCColourer.SPILL n =>
MirTypes.GC.pack((#gc MirRegisters.pack_next)+n),
non_gc =
fn r => case non_gc_assign r of
NonGCColourer.REGISTER r => r
| NonGCColourer.SPILL n =>
MirTypes.NonGC.pack((#non_gc MirRegisters.pack_next)+n),
fp =
fn r => case fp_assign r of
FPColourer.REGISTER r => r
| FPColourer.SPILL n =>
MirTypes.FP.pack((#fp MirRegisters.pack_next)+n)}
in
fn (tag, MirProcedure.B (annotation, instructions)) =>
(MirProcedure.B
(annotation,
Lists.reducer
(fn (MirProcedure.I {defined, referenced, branches, excepts, opcode}, instructions) =>
if is_null_move opcode then instructions
else
let
val instruction =
MirProcedure.I {defined = MirProcedure.empty,
referenced = MirProcedure.empty,
branches = branches,
excepts = excepts,
opcode = substitute_opcode opcode}
in
instruction :: instructions
end)
(instructions, [])))
end
end
end
fun block_preferences (MirProcedure.B (_,instructions),acc) =
Lists.reducel
(fn ((acc1,acc2), MirProcedure.I {opcode = MirTypes.UNARY (MirTypes.MOVE,
MirTypes.GC_REG r1,
MirTypes.GP_GC_REG r2),
...}) =>
((r1,r2) :: acc1,acc2)
| ((acc1,acc2), MirProcedure.I {opcode = MirTypes.UNARYFP (MirTypes.FMOVE,
MirTypes.FP_REG r1,
MirTypes.FP_REG r2),
...}) =>
(acc1, (r1,r2) :: acc2)
| (acc,_) => acc)
(acc,instructions)
fun analyse (procedure as MirProcedure.P (annotation, name, start, block_map),
{gc = gc_graph, non_gc = non_gc_graph, fp = fp_graph},
{gc = gc_spills', non_gc = non_gc_spills', fp = fp_spills'},
make_debug_code) =
let
val _ = diagnostic (1, fn _ => ["procedure ", MirTypes.print_tag start, ": ", name])
val {uses_stack,
nr_registers,
parameters = MirTypes.PROC_PARAMS {stack_allocated, ...}} = annotation
local
val (gcs,fps) =
(Map.fold
(fn (acc,_,block) =>
block_preferences (block,acc))
(([],[]),block_map))
in
val gc_preferences = rev gcs
val fp_preferences = rev fps
end
val ({assign = gc_assign, nr_spills = gc_spills},
{assign = non_gc_assign, nr_spills = non_gc_spills},
{assign = fp_assign, nr_spills = fp_spills}) =
(GCColourer.colour (gc_graph,gc_preferences,make_debug_code, name),
NonGCColourer.colour (non_gc_graph,[],make_debug_code, name),
FPColourer.colour (fp_graph,fp_preferences,make_debug_code, name))
val _ = diagnostic (2, fn _ => ["rewriting procedure"])
val substitute =
let
val num_spills = gc_spills + non_gc_spills + fp_spills
val _ =
if num_spills > 0
then
diagnostic (2, fn _ => [Int.toString num_spills,
" spills for ", name])
else ()
in
substitute ({gc = gc_assign, non_gc = non_gc_assign, fp = fp_assign},
{gc = gc_spills, non_gc = non_gc_spills, fp = fp_spills},
{gc = gc_spills', non_gc = non_gc_spills', fp = fp_spills'})
end
val blocks =
Map.map substitute block_map
val spill_sizes = SOME {gc = gc_spills+gc_spills',
non_gc = non_gc_spills+non_gc_spills',
fp = fp_spills+fp_spills'}
in
MirProcedure.P ({nr_registers = nr_registers,
uses_stack = uses_stack,
parameters = MirTypes.PROC_PARAMS
{stack_allocated = stack_allocated,
old_spill_sizes = NONE,
spill_sizes = spill_sizes}},
name,
start,
blocks)
end
end
;
