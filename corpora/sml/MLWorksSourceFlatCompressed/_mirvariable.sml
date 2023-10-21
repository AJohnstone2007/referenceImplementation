require "../utils/diagnostic";
require "../utils/crash";
require "../utils/lists";
require "../utils/inthashtable";
require "registerallocator";
require "mirtables";
require "mirprint";
require "mirregisters";
require "mirvariable";
functor MirVariable(include
sig
structure Diagnostic : DIAGNOSTIC
structure Crash : CRASH
structure Lists : LISTS
structure IntHashTable : INTHASHTABLE
structure MirPrint : MIRPRINT
structure MirTables : MIRTABLES
structure MirRegisters : MIRREGISTERS
structure RegisterAllocator : REGISTERALLOCATOR
sharing MirPrint.MirTypes = MirTables.MirTypes =
RegisterAllocator.MirProcedure.MirTypes = MirRegisters.MirTypes
sharing Diagnostic.Text = MirRegisters.MirTypes.GC.Set.Text
end
where type MirPrint.MirTypes.tag = int
) : MIRVARIABLE =
struct
structure MirProcedure = RegisterAllocator.MirProcedure
structure MirTypes = MirRegisters.MirTypes
structure Set = MirTypes.Set
structure RegisterAllocator = RegisterAllocator
structure Diagnostic = Diagnostic
structure Map = MirTypes.Map
val eliminate = ref true
val $ = Diagnostic.Text.from_string
val ^^ = Diagnostic.Text.concatenate
infix ^^
val do_diagnostics = false
fun diagnostic (level, output_function) =
Diagnostic.output_text level
(fn verbosity => $"MirVariable: " ^^ output_function verbosity)
fun diagnostic_set (level, prefix_function, {gc, non_gc, fp}) =
diagnostic (level, fn _ =>
prefix_function () ^^ $"  " ^^
MirTypes.GC.Pack.to_text gc ^^ $" " ^^
MirTypes.NonGC.Pack.to_text non_gc ^^ $" " ^^
MirTypes.FP.Pack.to_text fp)
fun crash message =
Crash.impossible ("MirVariable: " ^ message)
fun empty_tag_map () = IntHashTable.new 4
fun lookup_tag (tag,map) = IntHashTable.tryLookup (map,tag)
fun tagmap_from_list l =
let
val result = empty_tag_map ()
in
Lists.iterate (fn (tag,x) => IntHashTable.update (result,tag,x)) l;
result
end
fun reach_map blocks =
let
val result = tagmap_from_list (map (fn (tag, _) => (tag, ref [])) blocks)
fun result_fn tag = lookup_tag (tag,result)
fun block [] = ()
| block ((from, MirProcedure.B ({reached, ...}, _))::rest) =
let
fun reach [] = ()
| reach (to::rest) =
let
val to_list =
case result_fn to of
SOME x => x
| NONE =>
crash ("Block " ^ MirTypes.print_tag to ^
" was reached but isn't in the list of blocks.")
in
to_list := from::(!to_list);
reach rest
end
in
reach (Set.set_to_list reached);
block rest
end
in
block blocks;
result
end
fun dfs (block_fn,start) =
let
fun scan(arg as (seen, result), tag) =
case MirTypes.Map.tryApply'(seen, tag) of
NONE =>
let
val MirProcedure.B({reached,...},_) = block_fn tag
val seen = MirTypes.Map.define(seen, tag, true)
val (seen, result) =
Lists.reducel
scan
((seen, result), Set.set_to_list reached)
in
(seen, tag :: result)
end
| _ => arg
val (_, result) = scan((MirTypes.Map.empty, []), start)
in
rev result
end
fun analyse (procedure as MirProcedure.P (annotation as {parameters, ...},
name, start, block_map), graph) =
let
val _ =
if do_diagnostics
then diagnostic (1, fn _ => $"procedure " ^^ $(MirTypes.print_tag start) ^^ $" " ^^ $name)
else ()
val clash = RegisterAllocator.clash graph
val blocks = Map.to_list block_map
local
val reach_map = reach_map blocks
val live_map =
tagmap_from_list (map (fn (tag, _) => (tag, ref MirProcedure.empty)) blocks)
val block_table = tagmap_from_list blocks
in
fun reach_map_fn tag =
(case lookup_tag (tag,reach_map) of
SOME (ref new_tags) => new_tags
| _ => crash ("Block " ^ MirTypes.print_tag tag ^ " was found but is not in the reached map."))
fun live_map_fn tag =
case IntHashTable.tryLookup (live_map,tag) of
SOME x => x
| _ => crash ("Block " ^ MirTypes.print_tag tag ^ " is not in the live map.")
fun block_fn tag =
case IntHashTable.tryLookup (block_table,tag) of
SOME x => x
| _ => crash ("Block " ^ MirTypes.print_tag tag ^ " is not in the procedure.")
end
fun after (MirProcedure.I {branches, excepts, opcode, ...}, live) =
let
fun reach (live, []) = live
| reach (live, tag::tags) =
reach (MirProcedure.union' (live, !(live_map_fn tag)), tags)
val live' = reach (live, excepts)
val live'' = reach (live', Set.set_to_list branches)
in
if do_diagnostics
then
diagnostic_set (4, fn () => $"live after " ^^ $(MirPrint.opcode opcode) ^^ $":", live'')
else ();
live''
end
nonfix before
fun before (MirProcedure.I {defined, referenced, opcode, ...}, live) =
let
val live' = MirProcedure.union' (MirProcedure.difference' (live, defined),referenced)
in
if do_diagnostics
then diagnostic_set (4, fn () => $"live before " ^^ $(MirPrint.opcode opcode) ^^ $":", live')
else ();
live'
end
fun block (MirProcedure.B (_, instructions)) =
Lists.reducer
(fn (instruction, live) => before (instruction, after (instruction, live)))
(instructions, MirProcedure.empty)
val ordered_blocks = dfs (block_fn,start)
val num_blocks = Lists.length ordered_blocks
val block_array = MLWorks.Internal.Array.arrayoflist ordered_blocks
val todo = MLWorks.Internal.Array.array (num_blocks,true)
local
fun number ([],n,acc) = rev acc
| number (a::rest,n,acc) = number (rest,n+1,(a,n)::acc)
val block_index_map = tagmap_from_list (number (ordered_blocks,0,[]))
in
fun block_index tag =
(case IntHashTable.tryLookup (block_index_map,tag) of
SOME n => n
| _ => crash "block index failure")
end
fun iterate num_passes =
let
fun aux (changed,n) =
if n = num_blocks
then changed
else
if not (MLWorks.Internal.Array.sub (todo,n))
then aux (changed,n+1)
else
let
val tag = MLWorks.Internal.Array.sub (block_array,n)
val old_live = live_map_fn tag
val new_live = block (block_fn tag)
val _ = MLWorks.Internal.Array.update (todo,n,false)
in
if MirProcedure.equal (!old_live, new_live)
then aux (true,n+1)
else
(old_live := new_live;
let
val new_tags = reach_map_fn tag
in
Lists.iterate
(fn tag => MLWorks.Internal.Array.update (todo,block_index tag,true))
new_tags;
aux (true,n+1)
end)
end
val changed = aux (false,0)
in
if changed
then iterate (num_passes + 1)
else ( )
end
val _ = iterate 0
val _ =
let
val live_at_start as {gc, non_gc, fp} = !(live_map_fn start)
in
if MirProcedure.is_empty live_at_start then () else
crash (Lists.reducel (fn (s, r) => s ^ " " ^ MirTypes.GC.to_string r)
(Lists.reducel (fn (s, r) => s ^ " " ^ MirTypes.NonGC.to_string r)
(Lists.reducel (fn (s, r) => s ^ " " ^ MirTypes.FP.to_string r)
("The following registers are used undefined in procedure " ^
MirTypes.print_tag start ^ " " ^ name ^ ":",
MirTypes.FP.Pack.to_list fp),
MirTypes.NonGC.Pack.to_list non_gc),
MirTypes.GC.Pack.to_list gc))
end
fun modify (MirProcedure.B (annotation, instructions)) =
let
val instructions' =
let
fun modify' (live, done, []) = done
| modify' (live, done,
(instruction as MirProcedure.I {defined, referenced, opcode, ...})::instructions) =
if !eliminate andalso
not (MirTables.has_side_effects opcode) andalso
MirProcedure.is_empty (MirProcedure.intersection (defined, live)) then
(if do_diagnostics
then
diagnostic_set (3, fn () => $"eliminating " ^^ $(MirPrint.opcode opcode) ^^ $" in context ", live)
else ();
modify' (live, done, instructions))
else
let
val live' = after (instruction, live)
in
clash (defined, referenced, live');
modify' (before (instruction, live'), instruction::done, instructions)
end
in
modify' (MirProcedure.empty, [], rev instructions)
end
in
MirProcedure.B (annotation, instructions')
end
val blocks' =
Lists.reducel
(fn (blocks, (tag, block)) =>
(if do_diagnostics
then diagnostic (3, fn _ => $"modifying block " ^^ $(MirTypes.print_tag tag))
else ();
(tag, modify block)::blocks))
([], blocks)
val block_map' = Map.from_list blocks'
val procedure' = MirProcedure.P (annotation, name,start, block_map')
in
procedure'
end
end
;
