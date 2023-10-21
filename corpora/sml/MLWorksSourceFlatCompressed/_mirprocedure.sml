require "../basis/__int";
require "../utils/lists";
require "../utils/crash";
require "../utils/diagnostic";
require "registerpack";
require "mirprint";
require "mirtables";
require "mirprocedure";
functor MirProcedure (
structure Lists : LISTS
structure Crash : CRASH
structure MirTables : MIRTABLES
structure MirPrint : MIRPRINT
structure RegisterPack : REGISTERPACK
structure Diagnostic : DIAGNOSTIC
sharing MirTables.MirTypes = MirPrint.MirTypes = RegisterPack.MirTypes
) : MIRPROCEDURE =
struct
structure MirTypes = MirTables.MirTypes
structure Map = MirTypes.Map
structure Set = MirTypes.Set
structure Diagnostic = Diagnostic
structure Text = MirTypes.GC.Set.Text
structure RuntimeEnv = MirTypes.Debugger_Types.RuntimeEnv
fun crash message =
Crash.impossible ("MirProcedure: " ^ message)
fun union ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.union (gc, gc'),
non_gc = MirTypes.NonGC.Pack.union (non_gc, non_gc'),
fp = MirTypes.FP.Pack.union (fp, fp')}
fun union' ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.union' (gc, gc'),
non_gc = MirTypes.NonGC.Pack.union' (non_gc, non_gc'),
fp = MirTypes.FP.Pack.union' (fp, fp')}
fun intersection ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.intersection (gc, gc'),
non_gc = MirTypes.NonGC.Pack.intersection (non_gc, non_gc'),
fp = MirTypes.FP.Pack.intersection (fp, fp')}
fun intersection' ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.intersection' (gc, gc'),
non_gc = MirTypes.NonGC.Pack.intersection' (non_gc, non_gc'),
fp = MirTypes.FP.Pack.intersection' (fp, fp')}
fun difference ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.difference (gc, gc'),
non_gc = MirTypes.NonGC.Pack.difference (non_gc, non_gc'),
fp = MirTypes.FP.Pack.difference (fp, fp')}
fun difference' ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Pack.difference' (gc, gc'),
non_gc = MirTypes.NonGC.Pack.difference' (non_gc, non_gc'),
fp = MirTypes.FP.Pack.difference' (fp, fp')}
fun equal ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
MirTypes.GC.Pack.equal (gc, gc') andalso
MirTypes.NonGC.Pack.equal (non_gc, non_gc') andalso
MirTypes.FP.Pack.equal (fp, fp')
fun pack {gc, non_gc, fp} =
{gc = MirTypes.GC.pack_set gc,
non_gc = MirTypes.NonGC.pack_set non_gc,
fp = MirTypes.FP.pack_set fp}
fun unpack {gc, non_gc, fp} =
{gc = MirTypes.GC.unpack_set gc,
non_gc = MirTypes.NonGC.unpack_set non_gc,
fp = MirTypes.FP.unpack_set fp}
fun copy' {gc, non_gc, fp} =
{gc = MirTypes.GC.Pack.copy' gc,
non_gc = MirTypes.NonGC.Pack.copy' non_gc,
fp = MirTypes.FP.Pack.copy' fp}
val empty = {gc = MirTypes.GC.Pack.empty,
non_gc = MirTypes.NonGC.Pack.empty,
fp = MirTypes.FP.Pack.empty}
val empty_set = {gc = MirTypes.GC.Set.empty,
non_gc = MirTypes.NonGC.Set.empty,
fp = MirTypes.FP.Set.empty}
fun is_empty {gc, non_gc, fp} =
MirTypes.GC.Pack.is_empty gc andalso
MirTypes.NonGC.Pack.is_empty non_gc andalso
MirTypes.FP.Pack.is_empty fp
fun pack_set_union' ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Set.reduce MirTypes.GC.Pack.add' (gc,gc'),
non_gc = MirTypes.NonGC.Set.reduce MirTypes.NonGC.Pack.add' (non_gc,non_gc'),
fp = MirTypes.FP.Set.reduce MirTypes.FP.Pack.add' (fp,fp')}
fun pack_set_difference' ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
{gc = MirTypes.GC.Set.reduce MirTypes.GC.Pack.remove' (gc,gc'),
non_gc = MirTypes.NonGC.Set.reduce MirTypes.NonGC.Pack.remove' (non_gc,non_gc'),
fp = MirTypes.FP.Set.reduce MirTypes.FP.Pack.remove' (fp,fp')}
fun gcforall f set = MirTypes.GC.Set.reduce (fn (t,i) => t andalso f i) (true,set)
fun nongcforall f set = MirTypes.NonGC.Set.reduce (fn (t,i) => t andalso f i) (true,set)
fun fpforall f set = MirTypes.FP.Set.reduce (fn (t,i) => t andalso f i) (true,set)
fun set_pack_disjoint ({gc, non_gc, fp}, {gc = gc', non_gc = non_gc', fp = fp'}) =
(gcforall (fn r => not (MirTypes.GC.Pack.member (gc',r))) gc) andalso
(nongcforall (fn r => not (MirTypes.NonGC.Pack.member (non_gc',r))) non_gc) andalso
(fpforall (fn r => not (MirTypes.FP.Pack.member (fp',r))) fp)
val substitute = RegisterPack.substitute
datatype instruction =
I of {defined : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T},
referenced : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T},
branches : MirTypes.tag Set.Set,
excepts : MirTypes.tag list,
opcode : MirTypes.opcode}
datatype block =
B of {reached : MirTypes.tag Set.Set,
excepts : MirTypes.tag list,
length : int} *
instruction list
datatype procedure =
P of {uses_stack : bool,
nr_registers : {gc : int, non_gc : int, fp : int},
parameters : MirTypes.procedure_parameters} *
string * MirTypes.tag * (block) Map.T
local
open MirTypes
in
fun branches (opcode as TBINARY (_, tags, _, _, _)) =
{next = true, branches = tags, opcode = opcode}
| branches (opcode as TBINARYFP (_, tags, _, _, _)) =
{next = true, branches = tags, opcode = opcode}
| branches (opcode as TUNARYFP (_, tags, _, _)) =
{next = true, branches = tags, opcode = opcode}
| branches (opcode as BRANCH (_, TAG tag)) =
{next = false, branches = [tag], opcode = opcode}
| branches (opcode as BRANCH (_, REG _)) =
{next = false, branches = [], opcode = opcode}
| branches (opcode as TEST (_, tag, _, _)) =
{next = true, branches = [tag], opcode = opcode}
| branches (opcode as FTEST (_, tag, _, _)) =
{next = true, branches = [tag], opcode = opcode}
| branches (opcode as TAIL_CALL _) =
{next = false, branches = [], opcode = opcode}
| branches (opcode as SWITCH (_, _, tags as [tag])) =
{next = false, branches = tags, opcode = BRANCH(BRA, TAG tag)}
| branches (opcode as SWITCH (_, _, tags)) =
{next = false, branches = tags, opcode = opcode}
| branches (opcode as RTS) =
{next = false, branches = [], opcode = opcode}
| branches (opcode as RAISE _) =
{next = false, branches = [], opcode = opcode}
| branches (opcode as FLOOR(_, tag, _, _)) =
{next = true, branches = [tag], opcode = opcode}
| branches opcode =
{next = true, branches = [], opcode = opcode}
fun raises (RAISE _) = true
| raises (BRANCH_AND_LINK _) = true
| raises (TBINARY (_, [],_,_,_)) = true
| raises (TBINARYFP (_, [],_,_,_)) = true
| raises (TUNARYFP (_, [],_,_)) = true
| raises CALL_C = true
| raises _ = false
end
fun annotate (procedure as MirTypes.PROC (name,start_tag, parameters, block_list,_)) =
let
val {nr_registers, substitute} = RegisterPack.f procedure
val old_block_map =
(Lists.reducel (fn (res, MirTypes.BLOCK(x,y)) =>
Map.define(res, x, y))
(Map.empty, block_list))
fun old_block x = Map.tryApply'(old_block_map, x)
fun block (excepts, opcodes) =
let
fun block' (done, defined, length, reached, stack, _, []) =
(rev done, defined, length, reached, stack)
| block' (done, defined, length, reached, stack, excepts, opcode::opcodes) =
let
val opcode' = substitute opcode
val excepts' =
case opcode'
of MirTypes.NEW_HANDLER(_, tag) => tag::excepts
| MirTypes.OLD_HANDLER =>
(case excepts
of _::rest => rest
| [] =>
crash ("Too many OLD_HANDLERs in procedure " ^ MirTypes.print_tag start_tag ^ "."))
| _ => excepts
val stack' =
case opcode'
of MirTypes.STACKOP _ => true
| MirTypes.ALLOCATE_STACK _ => true
| MirTypes.DEALLOCATE_STACK _ => true
| _ => stack
val {next, branches, opcode = opcode''} = branches opcode'
val reached' = (map (fn tag => (excepts', tag)) branches) @ reached
val (excepts, reached'') =
if raises opcode'' then
let
fun reach (reached, []) = reached
| reach (reached, excepts as cont::conts) =
reach ((excepts, cont)::reached, conts)
val reached'' = reach (reached',excepts)
in
(excepts, reached'')
end
else
([], reached')
val defined_here = pack (MirTables.defined_by opcode'')
val referenced_here = pack (MirTables.referenced_by opcode'')
val instruction =
I {defined = defined_here,
referenced = referenced_here,
branches = Set.list_to_set branches,
excepts = excepts,
opcode = opcode''}
in
block' (instruction::done,
union' (defined, defined_here),
case opcode'' of MirTypes.COMMENT _ => length | _ => length+1,
reached'',
stack',
excepts',
if next then opcodes else [])
end
val (instructions, defined, length, reached, stack) =
block' ([], empty, 0, [], false, excepts, opcodes)
in
(B ({reached = Set.list_to_set (map #2 reached),
length = length,
excepts = excepts},
instructions),
defined,
reached,
stack)
end
fun next (map, stack, []) = (map,stack)
| next (map, stack, (excepts, tag)::rest) =
(case Map.tryApply'(map, tag) of
SOME (B ({excepts = excepts', ...}, _)) =>
if excepts = excepts' then
next (map, stack, rest)
else
crash ("Block " ^ MirTypes.print_tag tag ^ " has been reached with inconsistent exception blocks.  " ^
"The first time round they were " ^ Lists.to_string MirTypes.print_tag excepts' ^
" but this time they were " ^ Lists.to_string MirTypes.print_tag excepts ^ ".")
| _ =>
(case old_block tag of
SOME opcodes =>
let
val (new_block, defined, reached, stack') = block (excepts, opcodes)
val map' = Map.define (map, tag, new_block)
in
next (map',
stack orelse stack',
reached @ rest)
end
| _ =>
crash ("Block " ^ MirTypes.print_tag tag ^
" has been reached but cannot be found in the original " ^
"procedure.")))
val (map, stack) =
next (Map.empty, false, [([], start_tag)])
in
P ({nr_registers = nr_registers,
uses_stack = stack,
parameters = parameters},
name,start_tag, map)
end
fun unannotate (P({parameters, ...}, name, start_tag, block_map)) =
let
fun unannotate_block (tag, B(_, instructions)) =
let
local
open MirTypes
in
fun unannotate_instruction (I {opcode = opcode as ADR (_, reg, tag),...}) =
(case Map.tryApply' (block_map, tag) of
SOME _ => opcode
| _ => UNARY (MOVE, reg, GP_IMM_ANY 0))
| unannotate_instruction (I {opcode,...}) = opcode
end
in
MirTypes.BLOCK(tag, map unannotate_instruction instructions)
end
in
MirTypes.PROC(name,start_tag, parameters, map unannotate_block
(Map.to_list block_map),RuntimeEnv.EMPTY)
end
fun to_text (P(annotation, name, start_tag, block_map)) =
let
nonfix ^^
val ^^ = Text.concatenate
val $ = Text.from_string
infix ^^
fun list printer (leader, []) = $""
| list printer (leader, [t]) = leader ^^ $(printer t) ^^ $"\n"
| list printer (leader, t::ts) =
leader ^^
Lists.reducel (fn (text, t) => $(printer t) ^^ $", " ^^ text)
($(printer t), ts) ^^
$"\n"
val tags = list MirTypes.print_tag
fun registers (leader, {gc, non_gc, fp}) =
leader ^^ MirTypes.GC.Pack.to_text gc ^^
$" " ^^ MirTypes.NonGC.Pack.to_text non_gc ^^
$" " ^^ MirTypes.FP.Pack.to_text fp ^^
$"\n"
val header =
$"Procedure " ^^ $(MirTypes.print_tag start_tag) ^^ $": " ^^ $name ^^ $"\n"
fun block (text, tag, B(annotation, instructions)) =
let
val header =
$"    Block " ^^ $(MirTypes.print_tag tag) ^^ $"\n" ^^
tags ($"      reached: ", Set.set_to_list (#reached annotation)) ^^
tags ($"      exceptions: ", #excepts annotation) ^^
$"      length: " ^^ $(Int.toString (#length annotation)) ^^ $"\n"
fun opcode (text, I {defined,referenced,branches,excepts,opcode}) =
let
val trailer =
registers ($"          defined: ", defined) ^^
registers ($"          referenced: ", referenced) ^^
tags ($"          branches: ", Set.set_to_list branches) ^^
tags ($"          exceptions: ", excepts)
in
text ^^
$"        " ^^ $(MirPrint.opcode opcode) ^^ $"\n" ^^
trailer
end
in
text ^^ Lists.reducel opcode (header, instructions)
end
in
Map.fold block (header, block_map)
end
end
;
