require "../utils/diagnostic";
require "../utils/lists";
require "../utils/crash";
require "mirprocedure";
require "mirprint";
require "stackallocator";
functor StackAllocator (
structure MirPrint : MIRPRINT
structure Crash : CRASH
structure Lists : LISTS
structure MirProcedure : MIRPROCEDURE
structure Diagnostic : DIAGNOSTIC
sharing MirProcedure.MirTypes = MirPrint.MirTypes
) : STACKALLOCATOR =
struct
structure MirProcedure = MirProcedure
structure Diagnostic = Diagnostic
structure MirTypes = MirProcedure.MirTypes
structure Map = MirTypes.Map
infix ^^
val op^^ = Diagnostic.Text.concatenate
val $ = Diagnostic.Text.from_string
fun diagnostic (level, output_function) =
Diagnostic.output_text level
(fn verbosity =>
$"Stack Allocator: " ^^ output_function verbosity)
fun crash message = Crash.impossible ("Stack Allocator: " ^ message)
val do_diagnostics = false
local
open MirTypes
val message =
"Stack Allocator: I don't know how to deal with non-word stack allocation."
in
fun stack_effect (STACKOP (PUSH, _, _)) = 1
| stack_effect (STACKOP (POP, _, _)) = ~1
| stack_effect (ALLOCATE_STACK (ALLOC, _, amount, _)) = amount
| stack_effect (ALLOCATE_STACK _) = Crash.unimplemented message
| stack_effect (DEALLOCATE_STACK (ALLOC, amount)) = ~amount
| stack_effect (DEALLOCATE_STACK _) = Crash.unimplemented message
| stack_effect _ = 0
end
fun used_by_procedure (MirProcedure.P (_, _,start_tag, block_map)) =
let
val _ = if do_diagnostics
then diagnostic (1, fn _ => $"analysing stack usage of procedure " ^^
$(MirTypes.print_tag start_tag))
else ()
val block_map_fn = Map.tryApply block_map
fun used_by_block (visited, stack_position, maximum_used, exception_stack_positions, tag) =
(case MirTypes.Map.tryApply'(visited, tag) of
SOME previous_position =>
if previous_position = stack_position then
(0, visited)
else
crash ("The block tagged " ^ MirTypes.print_tag tag ^
" has been reached with inconsistent stack positions.")
| _ =>
let
fun used_by_opcodes (visited, _, maximum_used, _, []) = (maximum_used, visited)
| used_by_opcodes (visited, stack_position, maximum_used, exception_stack_positions,
MirProcedure.I {branches, excepts, opcode, ...} ::instructions) =
let
val stack_position_after =
let
val new = stack_position + (stack_effect opcode)
in
if new >= 0 then
new
else
crash ("The stack position has gone negative in block " ^
MirTypes.print_tag tag ^
".  There must be badly nested stack operations somewhere.")
end
val exception_stack_positions' =
case opcode of
MirTypes.NEW_HANDLER _ =>
stack_position_after :: exception_stack_positions
| MirTypes.OLD_HANDLER =>
(case exception_stack_positions of
_::rest => rest
| [] =>
crash ("Badly nested OLD_HANDLER in block " ^
MirTypes.print_tag tag ^ "."))
| _ => exception_stack_positions
val maximum_used_after =
if stack_position_after > maximum_used then stack_position_after else maximum_used
val (maximum_used_after', visited') =
Lists.reducel
(fn ((maximum, visited), tag) =>
let
val (used, visited') =
used_by_block (visited,
stack_position_after,
maximum_used_after,
exception_stack_positions',
tag)
in
(if used > maximum then used else maximum, visited')
end)
((maximum_used_after, visited), MirTypes.Set.set_to_list branches)
val (maximum_used_after'', visited'') =
case excepts of
[] => (maximum_used_after', visited')
| _ =>
let
fun reach (maximum, visited, [], []) = (maximum, visited)
| reach (maximum, visited, cont::conts, exception_stack_positions as sp::sps) =
let
val (used, visited') =
used_by_block (visited, sp, maximum, exception_stack_positions, cont)
in
reach (if used > maximum then used else maximum,
visited',
conts, sps)
end
| reach _ =
crash ("The exception block " ^ MirTypes.print_tag tag ^ " was reached " ^
"without a stack position in force.")
in
reach (maximum_used_after', visited', excepts, exception_stack_positions')
end
in
used_by_opcodes (visited'',
stack_position_after,
maximum_used_after'',
exception_stack_positions',
instructions)
end
val MirProcedure.B (annotation, instructions) =
case block_map_fn tag of
SOME x => x
| _ =>
crash ("Block " ^ MirTypes.print_tag tag ^ " was reached but not found.")
in
used_by_opcodes (MirTypes.Map.define(visited, tag, stack_position),
stack_position,
maximum_used,
exception_stack_positions,
instructions)
end)
in
used_by_block (MirTypes.Map.empty, 0, 0, [], start_tag)
end
fun add_offsets (positions, block_map) =
let
fun add_to_block (tag, MirProcedure.B (annotation, instructions)) =
let
fun add_to_opcodes (done, _, []) = rev done
| add_to_opcodes (done, position, MirProcedure.I {defined,referenced,branches,excepts,opcode}::instructions) =
let
val position_after = position + (stack_effect opcode)
local
open MirTypes
in
val opcode_with_offset =
case opcode
of STACKOP(PUSH, reg, _) =>
STACKOP(PUSH, reg, SOME position)
| STACKOP(POP, reg, _) =>
STACKOP(POP, reg, SOME position_after)
| ALLOCATE_STACK(oper, reg, amount, _) =>
ALLOCATE_STACK(oper, reg, amount, SOME position)
| other_opcode => other_opcode
end
in
add_to_opcodes (MirProcedure.I {defined = defined,
referenced = referenced,
branches = branches,
excepts = excepts,
opcode = opcode_with_offset} :: done,
position_after,
instructions)
end
val new_instructions =
add_to_opcodes ([], positions tag, instructions)
in
MirProcedure.B (annotation, new_instructions)
end
in
Map.map add_to_block block_map
handle Map.Undefined => crash ("Block does not have a starting stack position.")
end
fun allocate (procedure as MirProcedure.P (annotation, name, start, block_map)) =
let
val (used, block_map') =
if #uses_stack annotation then
let
val (used, positions) = used_by_procedure procedure
val positions = Map.apply positions
val block_map' = add_offsets (positions, block_map)
in
(used, block_map')
end
else
(0, block_map)
val annotation' =
let
val {uses_stack,
nr_registers,
parameters = MirTypes.PROC_PARAMS
{spill_sizes, ...}} = annotation
in
{nr_registers = nr_registers,
uses_stack = uses_stack,
parameters = MirTypes.PROC_PARAMS {spill_sizes = spill_sizes,
old_spill_sizes = NONE,
stack_allocated = SOME used}}
end
in
MirProcedure.P (annotation', name, start, block_map')
end
end
;
