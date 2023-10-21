let
val _ = print "calling simpleExecute\n";
val _ = Windows.simpleExecute ("basis\\win32\\execute", []);
val _ = print "calling execute\n";
val p = Windows.execute ("basis\\win32\\execute", [])
val _ = print "finding streams\n";
val ins = Windows.textInstreamOf p;
val outs = Windows.textOutstreamOf p;
in
print "testing stdOut\n";
print (TextIO.inputLine ins);
print "testing stdIn\n";
TextIO.output (outs, "Test input\n");
print (TextIO.inputLine ins);
Windows.fromStatus (Windows.reap p) = 0w78
end
;
