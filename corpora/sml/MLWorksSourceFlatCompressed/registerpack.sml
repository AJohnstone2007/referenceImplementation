require "../utils/diagnostic";
require "mirtypes";
signature REGISTERPACK =
sig
structure MirTypes : MIRTYPES
structure Diagnostic : DIAGNOSTIC
val substitute :
{gc : MirTypes.GC.T -> MirTypes.GC.T,
non_gc : MirTypes.NonGC.T -> MirTypes.NonGC.T,
fp : MirTypes.FP.T -> MirTypes.FP.T} ->
MirTypes.opcode -> MirTypes.opcode
val f : MirTypes.procedure ->
{nr_registers : {gc : int, non_gc : int, fp : int},
substitute : MirTypes.opcode -> MirTypes.opcode}
end
;
