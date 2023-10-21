require "inter_envtypes";
signature INTERLOAD =
sig
structure Inter_EnvTypes : INTER_ENVTYPES
type Module
val load :
(('a -> 'b) -> ('a -> 'b)) ->
Inter_EnvTypes.inter_env * (string -> MLWorks.Internal.Value.T) ->
Module ->
MLWorks.Internal.Value.T
end;
