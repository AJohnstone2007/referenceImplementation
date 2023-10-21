require "../utils/diagnostic";
require "../main/machspec";
require "mirtypes";
signature MIROPTIMISER =
sig
structure MirTypes : MIRTYPES
structure Diagnostic : DIAGNOSTIC
structure MachSpec : MACHSPEC
val optimise : MirTypes.mir_code * bool -> MirTypes.mir_code
val machine_register_assignments :
{gc : (MachSpec.register) MirTypes.GC.Map.T,
non_gc : (MachSpec.register) MirTypes.NonGC.Map.T,
fp : (MachSpec.register) MirTypes.FP.Map.T}
end
;
