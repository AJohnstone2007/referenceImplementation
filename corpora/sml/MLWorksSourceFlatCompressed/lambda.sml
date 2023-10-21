require "../basics/absyn";
require "../main/info";
require "../main/options";
require "../typechecker/basistypes";
require "environtypes";
signature LAMBDA = sig
structure Absyn: ABSYN
structure EnvironTypes: ENVIRONTYPES
structure Info : INFO
structure Options : OPTIONS
structure BasisTypes : BASISTYPES
type DebugInformation
sharing Info.Location = Absyn.Ident.Location
sharing Absyn.Ident = EnvironTypes.LambdaTypes.Ident
sharing type Absyn.Type = EnvironTypes.LambdaTypes.Type = BasisTypes.Datatypes.Type
val trans_top_dec :
Info.options ->
Options.options * Absyn.TopDec *
EnvironTypes.Top_Env * EnvironTypes.DebuggerEnv *
DebugInformation * BasisTypes.Basis * bool ->
EnvironTypes.Top_Env * EnvironTypes.DebuggerEnv *
EnvironTypes.LambdaTypes.binding list * DebugInformation
end
;
