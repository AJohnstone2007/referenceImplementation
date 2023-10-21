local
open General
open TextIO
in
fun reportOK true = "test succeeded."
| reportOK false = "test failed."
val _ = let val out = openOut "123"
val _ = output(out,"123\n456")
in
closeOut out
end
val inp = openIn "123"
val test1 = reportOK(inputLine inp = "123\n")
val test2 = reportOK(inputLine inp = "456\n")
val test3 = reportOK(inputLine inp = "")
val _ = closeIn inp
val _ = OS.FileSys.remove "123" handle _ => ()
end
;
