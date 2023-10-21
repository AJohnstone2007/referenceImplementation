require "../utils/diagnostic";
require "../main/info";
require "../main/options";
require "../mir/mirtypes";
signature MACH_CG = sig
structure MachSpec : sig eqtype register end
structure Diagnostic : DIAGNOSTIC
structure Info : INFO
structure Options : OPTIONS
structure MirTypes : MIRTYPES
type Module
type Opcode
val mach_cg :
Info.options ->
Options.options *
MirTypes.mir_code *
((MachSpec.register) MirTypes.GC.Map.T *
(MachSpec.register) MirTypes.NonGC.Map.T *
(MachSpec.register) MirTypes.FP.Map.T) *
MirTypes.Debugger_Types.information ->
(Module *
MirTypes.Debugger_Types.information) *
(((MirTypes.tag * (Opcode * string) list) * string) list list)
val print_code_size : bool ref
val do_timings : bool ref
end
;
