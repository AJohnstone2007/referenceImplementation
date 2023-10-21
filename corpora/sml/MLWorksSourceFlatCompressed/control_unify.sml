require "basistypes";
require "../main/info";
require "../main/options";
signature CONTROL_UNIFY =
sig
structure BasisTypes : BASISTYPES
structure Info : INFO
structure Options : OPTIONS
val unify :
(Info.options * Options.options) ->
{
first : BasisTypes.Datatypes.Type,
second : BasisTypes.Datatypes.Type,
result : BasisTypes.Datatypes.Type,
context : BasisTypes.Context,
error : unit ->
Info.Location.T *
BasisTypes.Datatypes.type_error_atom list *
BasisTypes.Datatypes.Type
}
-> BasisTypes.Datatypes.Type
end
;
