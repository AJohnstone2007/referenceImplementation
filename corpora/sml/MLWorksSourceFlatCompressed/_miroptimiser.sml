require "../utils/diagnostic";
require "mirvariable";
require "mirregisters";
require "mirexpr";
require "stackallocator";
require "miroptimiser";
functor MirOptimiser(
structure MirVariable : MIRVARIABLE
structure StackAllocator : STACKALLOCATOR
structure MirRegisters : MIRREGISTERS
structure MirExpr : MIREXPR
structure Diagnostic : DIAGNOSTIC
sharing
MirRegisters.MirTypes =
MirVariable.RegisterAllocator.MirProcedure.MirTypes =
MirExpr.MirTypes
sharing Diagnostic.Text = MirVariable.RegisterAllocator.MirProcedure.Text
sharing MirVariable.RegisterAllocator.MirProcedure =
StackAllocator.MirProcedure
) : MIROPTIMISER =
struct
structure RegisterAllocator = MirVariable.RegisterAllocator
structure MirProcedure = RegisterAllocator.MirProcedure
structure MirTypes = MirProcedure.MirTypes
structure Diagnostic = Diagnostic
structure MachSpec = MirRegisters.MachSpec
infix ^^
val (op^^) = Diagnostic.Text.concatenate
val $ = Diagnostic.Text.from_string
fun listing (level, message, procedure) =
Diagnostic.output_text level
(fn _ =>
$"MirOptimiser: " ^^ $message ^^ $"\n" ^^
MirProcedure.to_text procedure)
val machine_register_assignments = MirRegisters.machine_register_assignments
fun optimise (MirTypes.CODE(refs, values, proc_sets),make_debugging_code) =
let
fun optimise' (procedure as MirTypes.PROC (name, tag,proc as MirTypes.PROC_PARAMS{spill_sizes=spill_sizes', ...},blocks,runtime_env)) =
let
val procedure =
MirTypes.PROC (name,tag,proc, MirExpr.simple_transform blocks,runtime_env)
val _ =
Diagnostic.output 1 (fn _ => ["MirOptimiser: procedure ", MirTypes.print_tag tag, ": ", name])
val annotated as MirProcedure.P (annotation, _, _, _) =
MirProcedure.annotate procedure
val _ = listing (2, "after annotation", annotated)
val graph =
RegisterAllocator.empty (#nr_registers annotation,make_debugging_code)
val varied = MirVariable.analyse (annotated, graph)
val _ = listing (2, "after variable analysis", varied)
val spill_sizes =
case #spill_sizes((fn MirTypes.PROC_PARAMS params=>params)
(#parameters(annotation))) of
NONE => {gc = 0, non_gc = 0, fp = 0}
| SOME(spill_sizes) => spill_sizes
val registered =
RegisterAllocator.analyse (varied, graph,spill_sizes,make_debugging_code)
val _ = listing (2, "after register allocation", registered)
val stacked = StackAllocator.allocate registered
val _ = listing (2, "after stack allocation", stacked)
val MirTypes.PROC (name, tag, proc, block,_) = MirProcedure.unannotate stacked
val block = MirExpr.transform block
val proc = case proc of
MirTypes.PROC_PARAMS{spill_sizes, stack_allocated, ...} =>
MirTypes.PROC_PARAMS
{spill_sizes = spill_sizes,
old_spill_sizes = spill_sizes',
stack_allocated = stack_allocated}
in
MirTypes.PROC (name, tag, proc, block,runtime_env)
end
in
MirTypes.CODE(refs, values, map (map optimise') proc_sets)
end
end
;
