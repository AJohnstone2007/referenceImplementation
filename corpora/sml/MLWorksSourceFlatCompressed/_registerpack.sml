require "../basis/__int";
require "../utils/diagnostic";
require "../utils/crash";
require "../utils/lists";
require "../utils/inthashtable";
require "mirprint";
require "mirtables";
require "mirregisters";
require "registerpack";
functor RegisterPack (
include sig
structure Diagnostic : DIAGNOSTIC
structure Crash : CRASH
structure Lists : LISTS
structure IntHashTable : INTHASHTABLE
structure MirTables : MIRTABLES
structure MirRegisters : MIRREGISTERS
structure MirPrint : MIRPRINT
val full_analysis_threshold : int
sharing MirTables.MirTypes = MirRegisters.MirTypes = MirPrint.MirTypes
sharing MirRegisters.MirTypes.GC.Set.Text = Diagnostic.Text
end where type MirTables.MirTypes.GC.T = int
where type MirTables.MirTypes.NonGC.T = int
where type MirTables.MirTypes.FP.T = int
) : REGISTERPACK =
struct
structure MirTypes = MirRegisters.MirTypes
structure Diagnostic = Diagnostic
structure Map = MirTypes.Map
val do_diagnostics = false
val $ = Diagnostic.Text.from_string
val ^^ = Diagnostic.Text.concatenate
infix ^^
fun diagnostic (level, output_function) =
Diagnostic.output_text level
(fn verbosity => $"RegisterPack: " ^^ (output_function verbosity))
fun crash message = Crash.impossible ("RegisterPack: " ^ message)
fun reducel f =
let
fun red (acc, []) = acc
| red (acc, x::xs) = red (f(acc,x), xs)
in
red
end
fun assignment_to_text assignment =
reducel (fn (text, (reg, r)) => text ^^ $"\n" ^^ MirTypes.GC.to_text reg ^^ $" -> " ^^ $(Int.toString r))
($"GC assignments", IntHashTable.to_list assignment)
fun fp_assignment_to_text assignment =
reducel (fn (text, (reg, r)) => text ^^ $"\n" ^^ MirTypes.FP.to_text reg ^^ $" -> " ^^ $(Int.toString r))
($"FP assignments", IntHashTable.to_list assignment)
fun table_update (map,x,y) = (IntHashTable.update (map,x,y);map)
fun table_from_list l =
let
val table = IntHashTable.new (Lists.length l)
fun aux [] = ()
| aux ((i,x)::l) =
(IntHashTable.update (table,i,x);
aux l)
val _ = aux l
in
table
end
fun substitute {gc, non_gc, fp} =
let
open MirTypes
fun any (GC r) = GC (gc r)
| any (NON_GC r) = NON_GC (non_gc r)
| any (FLOAT r) = FLOAT (fp r)
val gp = fn GP_GC_REG r => GP_GC_REG (gc r)
| GP_NON_GC_REG r => GP_NON_GC_REG (non_gc r)
| other => other
val reg = fn GC_REG r => GC_REG (gc r)
| NON_GC_REG r => NON_GC_REG (non_gc r)
val fp = fn FP_REG r => FP_REG (fp r)
in
fn TBINARY (operator, tag, reg1, gp1, gp2) => TBINARY (operator, tag, reg reg1, gp gp1, gp gp2)
| BINARY (operator, reg1, gp1, gp2) => BINARY (operator, reg reg1, gp gp1, gp gp2)
| UNARY (operator, reg1, gp1) => UNARY (operator, reg reg1, gp gp1)
| NULLARY (operator, reg1) => NULLARY (operator, reg reg1)
| TBINARYFP (operator, tag, fp1, fp2, fp3) => TBINARYFP (operator, tag, fp fp1, fp fp2, fp fp3)
| TUNARYFP (operator, tag, fp1, fp2) => TUNARYFP (operator, tag, fp fp1, fp fp2)
| BINARYFP (operator, fp1, fp2, fp3) => BINARYFP (operator, fp fp1, fp fp2, fp fp3)
| UNARYFP (operator, fp1, fp2) => UNARYFP (operator, fp fp1, fp fp2)
| STACKOP (operator, reg1, offset) => STACKOP (operator, reg reg1, offset)
| STOREOP (operator, reg1, reg2, gp1) => STOREOP (operator, reg reg1, reg reg2, gp gp1)
| IMMSTOREOP(operator, gp1, reg2, gp3) => IMMSTOREOP(operator, gp gp1, reg reg2, gp gp3)
| STOREFPOP (operator, fp1, reg1, gp1) => STOREFPOP (operator, fp fp1, reg reg1, gp gp1)
| REAL (operator, fp1, gp1) => REAL (operator, fp fp1, gp gp1)
| FLOOR (operator, tag, reg1, fp1) => FLOOR (operator, tag, reg reg1, fp fp1)
| BRANCH (operator, REG reg1) => BRANCH (operator, REG (reg reg1))
| TEST (operator, tag, gp1, gp2) => TEST (operator, tag, gp gp1, gp gp2)
| FTEST (operator, tag, fp1, fp2) => FTEST (operator, tag, fp fp1, fp fp2)
| TAIL_CALL (operator, REG reg1,regs) => TAIL_CALL (operator, REG (reg reg1),map any regs)
| TAIL_CALL (operator, x, regs) => TAIL_CALL (operator, x, map any regs)
| SWITCH (operator, reg1, tags) => SWITCH (operator, reg reg1, tags)
| ALLOCATE (operator, reg1, gp1) => ALLOCATE (operator, reg reg1, gp gp1)
| ADR (operator, reg1, tag) => ADR (operator, reg reg1, tag)
| RAISE reg1 => RAISE (reg reg1)
| ALLOCATE_STACK (operator, reg1, amount, offset) => ALLOCATE_STACK (operator, reg reg1, amount, offset)
| BRANCH_AND_LINK (operator, REG reg1,debug,regs) => BRANCH_AND_LINK (operator, REG (reg reg1),debug,map any regs)
| BRANCH_AND_LINK (operator,x,debug,regs) => BRANCH_AND_LINK (operator,x,debug,map any regs)
| opcode as INTERCEPT => opcode
| opcode as INTERRUPT => opcode
| opcode as ENTER regs => ENTER (map any regs)
| opcode as RTS => opcode
| opcode as NEW_HANDLER(frame, tag) =>
NEW_HANDLER(reg frame, tag)
| opcode as OLD_HANDLER => opcode
| opcode as DEALLOCATE_STACK _ => opcode
| opcode as CALL_C => opcode
| opcode as BRANCH _ => opcode
| opcode as COMMENT _ => opcode
end
datatype uses = ONCE of MirTypes.tag | MANY | FIXED
local
val fix =
let
val fixed = ref FIXED
in
map (fn reg => (reg, fixed))
end
val gc = fix (MirTypes.GC.Pack.to_list (#gc MirRegisters.preassigned))
val non_gc = fix (MirTypes.NonGC.Pack.to_list (#non_gc MirRegisters.preassigned))
val fp = fix (MirTypes.FP.Pack.to_list (#fp MirRegisters.preassigned))
in
fun make_initial_uses () =
{gc = table_from_list gc,
non_gc = table_from_list non_gc,
fp = table_from_list fp}
end
fun scan ((uses, lifespans), MirTypes.BLOCK (tag, opcodes)) =
let
local
val count_gc = MirTypes.GC.Set.reduce
(fn (uses, reg) =>
(case IntHashTable.tryLookup (uses, reg) of
SOME x =>
((case x of
ref MANY => ()
| ref FIXED => ()
| other as ref (ONCE tag') => if tag=tag' then () else other := MANY);
uses)
| _ =>
table_update (uses, reg, ref (ONCE tag))))
val count_non_gc = MirTypes.NonGC.Set.reduce
(fn (uses, reg) =>
(case IntHashTable.tryLookup (uses, reg) of
SOME x =>
((case x of
ref MANY => ()
| ref FIXED => ()
| other as ref (ONCE tag') => if tag=tag' then () else other := MANY);
uses)
| _ =>
table_update (uses, reg, ref (ONCE tag))))
val count_fp = MirTypes.FP.Set.reduce
(fn (uses, reg) =>
(case IntHashTable.tryLookup (uses, reg) of
SOME x =>
((case x of
ref MANY => ()
| ref FIXED => ()
| other as ref (ONCE tag') => if tag=tag' then () else other := MANY);
uses)
| _ =>
table_update(uses, reg, ref (ONCE tag))))
in
fun count ({gc = gc_uses, non_gc = non_gc_uses, fp = fp_uses},
{gc = gc_touched, non_gc = non_gc_touched, fp = fp_touched}) =
{gc = count_gc (gc_uses, gc_touched),
non_gc = count_non_gc (non_gc_uses, non_gc_touched),
fp = count_fp (fp_uses, fp_touched)}
end
fun union_diff (map, set) =
MirTypes.GC.Set.reduce
(fn ((map, new), reg) =>
(case IntHashTable.tryLookup (map, reg) of
SOME () => (map, new)
| _ => (table_update (map, reg, ()), reg::new)))
((map, []), set)
fun backward (uses, lifespans, referenced, []) = (uses, lifespans)
| backward (uses, lifespans, referenced, (opcode, first)::rev_opcodes_firsts) =
let
val referenced_here as {gc = gc_referenced_here, ...} =
MirTables.referenced_by opcode
val defined_here as {gc = gc_defined_here, ...} =
MirTables.defined_by opcode
val gc_referenced_here =
MirTypes.GC.Set.union(gc_referenced_here, gc_defined_here)
val (referenced, last) = union_diff (referenced, gc_referenced_here)
val uses = count (uses, referenced_here)
in
backward (uses,
case (first, last) of
([], []) => lifespans
| _ => (first, last)::lifespans,
referenced,
rev_opcodes_firsts)
end
fun forward (rev_opcodes_firsts, uses, defined, []) =
backward (uses, lifespans, IntHashTable.new 8, rev_opcodes_firsts)
| forward (rev_opcodes_firsts, uses, defined, opcode::opcodes) =
let
val defined_here as {gc = gc_defined_here, ...} = MirTables.defined_by opcode
val (defined, first) = union_diff (defined, gc_defined_here)
val uses = count (uses, defined_here)
in
forward ((opcode, first)::rev_opcodes_firsts, uses, defined, opcodes)
end
in
forward ([], uses, IntHashTable.new 8, opcodes)
end
local
fun double r = (r,r)
val gc = (map double (MirTypes.GC.Pack.to_list (#gc MirRegisters.preassigned)))
val non_gc = (map double (MirTypes.NonGC.Pack.to_list (#non_gc MirRegisters.preassigned)))
val fp = (map double (MirTypes.FP.Pack.to_list (#fp MirRegisters.preassigned)))
in
fun make_initial_assignments () =
{gc = table_from_list gc,
non_gc = table_from_list non_gc,
fp = table_from_list fp}
end
fun assign (uses, lifespans) =
let
val _ =
if do_diagnostics
then
diagnostic (4, fn _ =>
let
val reduce = reducel (fn (text, reg) => text ^^ $" " ^^ MirTypes.GC.to_text reg)
in
reducel
(fn (text, (first, last)) => reduce (reduce (text ^^ $"\n", first) ^^ $" / ", last))
($"assigning for lifespans", lifespans)
end)
else ()
val initial_assignments = make_initial_assignments ()
val (nr_packable, threshold, nr_registers, assignments) =
let
fun assign' (assignment, next, map) =
reducel
(fn ((assignment, next, others), (reg, ref MANY)) =>
(table_update (assignment, reg, next), next+1, others)
| ((assignment, next, others), (reg, ref (ONCE _))) =>
(assignment, next, others+1)
| ((assignment, next, others), (reg, ref FIXED)) =>
(assignment, next, others))
((assignment, next, 0), IntHashTable.to_list map)
fun assign_simple_non_gc' (assignment, next, map) =
(reducel
(fn ((assignment, next), (reg, ref FIXED)) => (assignment,next)
| ((assignment, next), (reg, _)) =>
(table_update (assignment, reg, next), next+1))
((assignment, next), IntHashTable.to_list map))
fun assign_simple_fp' (assignment, next, map) =
(reducel
(fn ((assignment, next), (reg, ref FIXED)) => (assignment,next)
| ((assignment, next), (reg, _)) =>
(table_update (assignment, reg, next), next+1))
((assignment, next), IntHashTable.to_list map))
val {gc = gc_uses, non_gc = non_gc_uses, fp = fp_uses} = uses
val (gc_assignment, next, nr_packable) =
assign' (#gc initial_assignments, #gc MirRegisters.pack_next, gc_uses)
val (non_gc_assignment, nr_non_gc) =
assign_simple_non_gc' (#non_gc initial_assignments, #non_gc MirRegisters.pack_next, non_gc_uses)
val (fp_assignment, nr_fp) =
assign_simple_fp' (#fp initial_assignments, #fp MirRegisters.pack_next, fp_uses)
in
(nr_packable, next,
{gc = next, non_gc = nr_non_gc, fp = nr_fp},
{gc = gc_assignment, non_gc = non_gc_assignment, fp = fp_assignment})
end
val (nr_registers', assignments') =
let
val alive = MLWorks.Internal.Array.array (threshold + nr_packable, false)
fun merge (assignment, next, []) = (next, assignment)
| merge (assignment, next, (first,last)::rest) =
let
val (assignment', next') =
reducel
(fn ((assignment, next), reg) =>
(case IntHashTable.tryLookup (assignment, reg) of
SOME _ => (assignment, next)
| _ =>
let
fun find_dead n =
if n = next then
(MLWorks.Internal.Array.update (alive, next, true);
(table_update
(assignment, reg, next), next+1))
else
if MLWorks.Internal.Array.sub (alive, n) then
find_dead (n+1)
else
(MLWorks.Internal.Array.update (alive, n, true);
(table_update
(assignment, reg, n), next))
in
find_dead threshold
end))
((assignment, next), first)
val _ =
Lists.iterate
(fn reg =>
MLWorks.Internal.Array.update (alive,
IntHashTable.lookup (assignment, reg), false)
handle IntHashTable.Lookup =>
crash ("The unassigned register " ^
MirTypes.GC.to_string reg ^ " has died.")
| MLWorks.Internal.Array.Subscript =>
crash ("Register " ^ MirTypes.GC.to_string reg ^
" was assigned outside " ^
"the alive array."))
last
in
merge (assignment', next', rest)
end
val {gc, non_gc, fp} = assignments
val {non_gc = nr_non_gc, fp = nr_fp, ...} = nr_registers
val (nr_gc, gc_assign) = merge (gc, threshold, lifespans)
val _ =
if do_diagnostics
then
diagnostic (2, fn _ =>
$"originally " ^^ $(Int.toString (threshold + nr_packable)) ^^ $" GC registers")
else ()
in
({gc = nr_gc, non_gc = nr_non_gc, fp = nr_fp},
{gc = gc_assign, non_gc = non_gc, fp = fp})
end
in
(nr_registers', assignments')
end
local
fun assign_gc ((next, map), reg) =
(case IntHashTable.tryLookup (map, reg) of
SOME _ => (next, map)
| _ =>
(next+1, table_update (map, reg, next)))
fun assign_non_gc ((next, map), reg) =
(case IntHashTable.tryLookup (map, reg) of
SOME _ => (next, map)
| _ => (next+1, table_update (map, reg, next)))
fun assign_fp ((next, map), reg) =
(case IntHashTable.tryLookup (map, reg) of
SOME _ => (next, map)
| _ => (next+1, table_update (map, reg, next)))
val gc_assign = MirTypes.GC.Set.reduce assign_gc
val non_gc_assign = MirTypes.NonGC.Set.reduce assign_non_gc
val fp_assign = MirTypes.FP.Set.reduce assign_fp
in
fun pack_simply ((nr_registers, assignments), MirTypes.BLOCK (_, opcodes)) =
let
fun scan (nr_registers, assignments, []) = (nr_registers, assignments)
| scan ({gc = gc_nr, non_gc = non_gc_nr, fp = fp_nr},
{gc = gc_map, non_gc = non_gc_map, fp = fp_map},
opcode::opcodes) =
let
val {gc, non_gc, fp} = MirTables.defined_by opcode
val (gc_nr', gc_map') = gc_assign ((gc_nr, gc_map), gc)
val (non_gc_nr', non_gc_map') = non_gc_assign ((non_gc_nr, non_gc_map), non_gc)
val (fp_nr', fp_map') = fp_assign ((fp_nr, fp_map), fp)
in
scan ({gc = gc_nr', non_gc = non_gc_nr', fp = fp_nr'},
{gc = gc_map', non_gc = non_gc_map', fp = fp_map'},
opcodes)
end
in
scan (nr_registers, assignments, opcodes)
end
end
fun f (MirTypes.PROC (name, start_tag, _, blocks,_)) =
let
val _ =
if do_diagnostics
then diagnostic (1, fn _ => $"procedure " ^^ $(MirTypes.print_tag start_tag) ^^ $": " ^^ $name)
else ()
val longest =
reducel (fn (longest, MirTypes.BLOCK (_, opcodes)) =>
let
val length = Lists.length opcodes
in
if length > longest then length else longest
end) (0, blocks)
val _ =
if do_diagnostics
then diagnostic (2, fn _ => $"longest block " ^^ $(Int.toString longest))
else ()
val do_full_analysis = longest > full_analysis_threshold
val _ = if do_full_analysis andalso do_diagnostics
then diagnostic (2,
fn _ =>
$"Full analysis for " ^^ $ name ^^ $":" ^^
$(Int.toString longest))
else ()
val (nr_registers, assignments) =
if do_full_analysis
then (assign (reducel scan ((make_initial_uses(), []), blocks)))
else
(reducel pack_simply ((MirRegisters.pack_next, make_initial_assignments()), blocks))
val _ =
if do_diagnostics
then diagnostic (2, fn _ => $(Int.toString (#gc nr_registers)) ^^ $" GC registers")
else ()
val _ =
if do_diagnostics
then diagnostic (3, fn _ => assignment_to_text (#gc assignments))
else ()
val _ =
if do_diagnostics
then diagnostic (3, fn _ => fp_assignment_to_text (#fp assignments))
else ()
val register_mappings =
let
val {gc, non_gc, fp} = assignments
val gc' = fn r => case IntHashTable.tryLookup (gc,r) of SOME g => g | _ => crash ("GC lookup failed for " ^ MirTypes.GC.to_string r)
val non_gc' = fn r => case IntHashTable.tryLookup (non_gc,r) of SOME g => g | _ => crash ("Non-GC lookup failed for " ^ MirTypes.NonGC.to_string r)
val fp' = fn r => case IntHashTable.tryLookup (fp,r) of SOME g => g | _ => crash ("FP lookup failed for " ^ MirTypes.FP.to_string r)
in
{gc = fn reg => gc' reg,
non_gc = fn reg => non_gc' reg,
fp = fn reg => fp' reg}
end
in
{nr_registers = nr_registers,
substitute = substitute register_mappings}
end
end
;
