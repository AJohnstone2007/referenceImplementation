local
open General
open TextIO
in
fun reportOK true = print"test succeeded.\n"
| reportOK false = print"test failed.\n"
val x = openString "Blah"
val _ = reportOK (SOME #"B" = (input1 x))
val _ = reportOK (SOME #"l" = (input1 x))
val _ = reportOK (SOME #"a" = (input1 x))
val _ = reportOK (SOME #"h" = (input1 x))
val _ = reportOK (NONE = (input1 x))
end
;
