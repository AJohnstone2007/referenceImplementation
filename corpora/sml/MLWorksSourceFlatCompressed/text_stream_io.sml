local
open General
in
fun reportOK true = "test succeeded."
| reportOK false = "test failed."
val _ = let val out = TextIO.openOut "123"
val _ = TextIO.output(out,"123\n456")
in
TextIO.closeOut out
end
val inp = TextIO.getInstream(TextIO.openIn "123")
val (data,inp') = TextIO.StreamIO.inputLine inp
val test1 = reportOK(data="123\n")
val (data,inp'') = TextIO.StreamIO.inputLine inp'
val test2 = reportOK(data = "456\n")
val (data,inp''') = TextIO.StreamIO.inputLine inp''
val test3 = reportOK(data = "")
val _ = TextIO.StreamIO.closeIn inp
val _ = OS.FileSys.remove "123" handle _ => ()
end
;
