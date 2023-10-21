require "mirtypes";
signature MIRTABLES =
sig
structure MirTypes : MIRTYPES
val referenced_by : MirTypes.opcode -> {gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T}
val defined_by : MirTypes.opcode -> {gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T}
val has_side_effects : MirTypes.opcode -> bool
end
;
