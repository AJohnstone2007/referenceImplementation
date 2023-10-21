Shell.Options.set (Shell.Options.ValuePrinter.maximumStrDepth, 0);
structure S = Shell.Options;
structure D = Shell.Options.Debugger;
S.Mode.debugging ();
fun fact x = if x =0 then 1 else fact (x-1)*x;
Shell.Trace.breakpoint "fact";
fact 4;
s
s
b
q
S.set(D.hideDeliveredFrames,false);
fact 4;
s
s
b
q
S.set(D.hideDuplicateFrames,false);
fact 4;
s
s
b
q
;
