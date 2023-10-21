require "../utils/diagnostic";
require "../lambda/lambdatypes";
require "../main/info";
require "../main/options";
require "mirtypes";
signature MIR_CG = sig
structure Diagnostic : DIAGNOSTIC
structure LambdaTypes : LAMBDATYPES
structure MirTypes : MIRTYPES
structure Info : INFO
structure Options : OPTIONS
sharing type MirTypes.Debugger_Types.Type = LambdaTypes.Type
val mir_cg :
Info.options ->
Options.options * LambdaTypes.LambdaExp * string * MirTypes.Debugger_Types.information ->
MirTypes.mir_code * MirTypes.Debugger_Types.information
end
;
