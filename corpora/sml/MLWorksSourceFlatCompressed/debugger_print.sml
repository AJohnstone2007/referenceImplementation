require "../main/options";
require "runtime_env";
signature DEBUGGER_PRINT =
sig
structure RuntimeEnv : RUNTIMEENV
structure Options : OPTIONS
val print_env :
((MLWorks.Internal.Value.Frame.frame * RuntimeEnv.RuntimeEnv * RuntimeEnv.Type)
* ((RuntimeEnv.Type * MLWorks.Internal.Value.T) -> string)
* Options.options * bool
* (MLWorks.Internal.Value.Frame.frame * RuntimeEnv.RuntimeEnv * RuntimeEnv.Type) list) ->
string *
(string * (RuntimeEnv.Type * MLWorks.Internal.Value.ml_value * string)) list
end
;
