fun reportOK true = "test succeeded."
| reportOK false = "test failed.";
local
val file = TextIO.openOut "foo";
val _ = TextIO.outputSubstr (file, Substring.all "abc");
val stream = TextIO.getOutstream file;
val (write, close) = case TextIO.StreamIO.getWriter stream of
(TextPrimIO.WR {writeVec= SOME write, close, ...}, _) => (write, close)
| _ => raise Match
val _ = TextIO.StreamIO.closeOut stream;
in
val ans1 = reportOK((ignore(write {buf= "def", i=0, sz=NONE});
close(); false) handle OS.SysErr _ => true);
end
local
val file = TextIO.openIn "foo";
val (read, close) = case TextIO.StreamIO.getReader(TextIO.getInstream file) of
(TextPrimIO.RD{readVec=SOME read, close,...},_) => (read, close)
| _ => raise Match
val _ = TextIO.closeIn file;
in
val ans2 = reportOK((ignore(read 1); close(); false) handle OS.SysErr _ => true);
end
val _ = OS.FileSys.remove "foo";
