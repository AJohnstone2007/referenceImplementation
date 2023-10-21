local
val print = fn s => fn res => (print(s^":"^res^"\n"))
fun check' s f = print s ((if f () then "OK" else "WRONG") handle exn => "FAIL:"^General.exnName exn)
fun checkexn' s exn f =
let val result = (ignore(f ()); "FAIL") handle ex =>
if General.exnName ex = General.exnName exn then
"OKEXN"
else
"BADEXN:" ^ (General.exnName ex)
in
print s result
end
val add1 = fn c => chr (1 + ord c)
val addi = fn (i, c) => chr (i + ord c)
val s = "ABCDabcd"
val test31 = check' "test31"
(fn _ => (CharVector.map add1 s)="BCDEbcde")
val test32 = check' "test32"
(fn _ => (CharVector.map add1 "")="")
val test33 = check' "test33"
(fn _ => (CharVector.mapi addi (s, 0, NONE))="ACEGegik")
val test34 = check' "test34"
(fn _ => (CharVector.mapi addi (s, 1, NONE))="CEGegik")
val test35 = check' "test35"
(fn _ => (CharVector.mapi addi (s, 7, NONE))="k")
val test36 = checkexn' "test36" Subscript
(fn _ => (CharVector.mapi addi (s, ~1, NONE)))
val test37a = check' "test37a"
(fn _ => (CharVector.mapi addi (s, 8, NONE)) = "")
val test37b = checkexn' "test37b" Subscript
(fn _ => (CharVector.mapi addi (s, 9, NONE)))
val test38 = check' "test38"
(fn _ => (CharVector.mapi addi (s, 0, SOME 2))="AC")
val test39 = check' "test39"
(fn _ => (CharVector.mapi addi (s, 6, SOME 2))="ik")
val test40 = checkexn' "test40" Subscript
(fn _ => (CharVector.mapi addi (s, 7, SOME 2)))
val test41 = check' "test41"
(fn _ => (CharVector.mapi addi (s, 2, SOME 0))="")
val test42 = checkexn' "test42" Subscript
(fn _ => (CharVector.mapi addi (s, 2, SOME (~1))))
in
val it = () ;
end;
