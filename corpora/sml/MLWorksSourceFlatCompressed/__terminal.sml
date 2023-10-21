require "terminal";
structure Terminal : TERMINAL =
struct
val terminalIO =
let
val current = MLWorks.Internal.StandardIO.currentIO()
val _ = MLWorks.Internal.StandardIO.resetIO()
val res = MLWorks.Internal.StandardIO.currentIO()
in
MLWorks.Internal.StandardIO.redirectIO current;
res
end
fun output s = (ignore(#put(#output(terminalIO)) {buf=s,i=0,sz=NONE});())
end;
