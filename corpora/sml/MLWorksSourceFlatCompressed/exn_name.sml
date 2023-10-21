fun reportOK true = "test succeeded"
| reportOK false = "test failed"
exception MyException
fun noDebugInfo s =
let
fun nameOnly i =
if i=size s then s
else
if String.sub(s,i)= #"[" then String.substring(s,0,i)
else nameOnly (i+1)
in
nameOnly 0
end
val x = reportOK(noDebugInfo(exnName MyException)="MyException")
handle e => "exception raised";
