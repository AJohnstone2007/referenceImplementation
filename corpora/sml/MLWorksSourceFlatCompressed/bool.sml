local
fun print_result s res = print (s^":"^res^"\n")
fun check' f = (if f () then "OK" else "WRONG") handle _ => "EXN"
val test1 =
check' (fn _=>
Bool.fromString "abc" = NONE andalso
Bool.fromString "afals" = NONE andalso
Bool.fromString "afalse" = NONE andalso
Bool.fromString "false" = SOME false andalso
Bool.fromString "falsea" = SOME false andalso
Bool.fromString "atru" = NONE andalso
Bool.fromString "atrue" = NONE andalso
Bool.fromString "true" = SOME true andalso
Bool.fromString "truea" = SOME true andalso
Bool.fromString " true" = SOME true andalso
Bool.fromString " false" = SOME false andalso
Bool.fromString " FALSE" = SOME false andalso
Bool.fromString "fAlSe" = SOME false)
val test2 =
check' (fn _=>
Bool.not true = false andalso
Bool.not false = true)
val test3 =
check' (fn _=>
Bool.fromString "False" = StringCvt.scanString Bool.scan "_False")
val test4 =
check' (fn _=>
Bool.toString true = "true" andalso
Bool.toString false = "false")
in
val it =
(print_result "fromString" test1;
print_result "not" test2;
print_result "toString" test4)
end
;
