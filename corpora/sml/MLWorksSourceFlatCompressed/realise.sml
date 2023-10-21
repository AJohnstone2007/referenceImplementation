require "basistypes";
require "../main/info";
require "../main/options";
signature REALISE =
sig
structure BasisTypes : BASISTYPES
structure Info : INFO
structure Options : OPTIONS
val sigmatch :
(Info.options * Options.options) ->
(Info.Location.T *
BasisTypes.Datatypes.Env *
int *
BasisTypes.Sigma *
BasisTypes.Datatypes.Structure)
->
bool * bool * BasisTypes.Datatypes.DebuggerStr
end
;
