local
open General
open TextIO
in
fun reportOK true = print"test succeeded.\n"
| reportOK false = print"test failed.\n"
val _ = closeOut (openOut "123");
val _ = OS.FileSys.remove "123" handle _ => ()
val _ = reportOK ((ignore(TextIO.openIn "123");false)
handle IO.Io{cause =
OS.SysErr(_ , SOME 2)
,...} => true
| IO.Io{cause =
OS.SysErr(_ , SOME 4)
,...} => true)
end
;
