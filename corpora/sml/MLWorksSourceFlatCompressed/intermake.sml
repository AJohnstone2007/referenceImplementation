require "../main/compiler";
require "inter_envtypes";
require "../utils/diagnostic";
signature INTERMAKE =
sig
structure Compiler : COMPILER
structure Inter_EnvTypes : INTER_ENVTYPES
structure Diagnostic : DIAGNOSTIC
sharing Inter_EnvTypes.Options = Compiler.Options
sharing Compiler.NewMap = Inter_EnvTypes.EnvironTypes.NewMap
sharing type Compiler.Top_Env = Inter_EnvTypes.EnvironTypes.Top_Env
type Project
type ModuleId
val with_debug_information :
Compiler.DebugInformation ->
(unit -> 'a) ->
'a
val current_debug_information : unit -> Compiler.DebugInformation
val compile :
((MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T) ->
(MLWorks.Internal.Value.T -> MLWorks.Internal.Value.T)) ->
Compiler.basis * Inter_EnvTypes.inter_env ->
Compiler.Info.options * Compiler.Info.Location.T
* Compiler.Options.options ->
(string * MLWorks.Internal.Value.T) list option ->
Project * ModuleId * Compiler.DebugInformation ->
Compiler.result * MLWorks.Internal.Value.T * Compiler.DebugInformation
val load :
Compiler.Options.options ->
Project * Compiler.Info.Location.T -> ModuleId ->
Compiler.result * MLWorks.Internal.Value.T
end
;
