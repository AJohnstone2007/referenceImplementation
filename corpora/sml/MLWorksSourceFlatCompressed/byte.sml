local
fun print_result s res = print (s^":"^res^"\n")
fun check' s f = print_result s ((if (f ()) then "OK" else "WRONG") handle exn => "FAIL:"^General.exnName exn)
fun checkexn' s exn f =
let val result = (ignore(f ()); "FAIL") handle ex =>
if General.exnName ex = General.exnName exn then
"OKEXN"
else
"BADEXN:" ^ (General.exnName ex)
in
print_result s result
end
fun check s b = print_result s (if b then "OK" else "WRONG")
fun range (from, to) p =
(from > to) orelse (p from) andalso (range (from+1, to) p)
fun checkrange bounds = (fn x=> if x then "OK" else "WRONG") o range bounds
val test1 = print_result "test1"
(checkrange (0,Char.maxOrd)
(fn i =>
(Word8.toInt o Byte.charToByte o Byte.byteToChar o Word8.fromInt) i = i))
val test1a = print_result "test1a"
(checkrange (0,Char.maxOrd)
(fn i=>
(Char.ord o Byte.byteToChar o Byte.charToByte o Char.chr) i = i))
val test2 = print_result "test2"
(checkrange (0, Char.maxOrd)
(fn i=>
(Word8.toInt o Byte.charToByte o Char.chr) i = i))
val test3 = print_result "test3"
(checkrange (0,255)
(fn i => (Char.ord o Byte.byteToChar o Word8.fromInt) i = i))
val test4 = print_result "test4"
(checkrange (0, Char.maxOrd)
(fn i => (Char.ord o Char.chr) i = i))
val test5 = check "test5"
("" = Byte.bytesToString (Word8Vector.fromList []))
val test5a = check "test5a"
("abc\000def" = (Byte.bytesToString o Byte.stringToBytes) "abc\000def")
val test6 = check "test6"
("ABDC" = (Byte.bytesToString o Word8Vector.fromList o map Word8.fromInt)
[65,66,68,67])
val test7 = check "test7"
("" = Byte.unpackString (Word8Array.fromList [], 0, SOME 0))
val arr = Word8Array.tabulate(10, fn i => Word8.fromInt(i+65))
val test8 = check "test8"
("" = Byte.unpackString(arr, 0, SOME 0))
val test9 = check "test9"
("" = Byte.unpackString(arr, 10, SOME 0) andalso
"" = Byte.unpackString(arr, 10, NONE))
val test10 = check "test10"
("BCDE" = Byte.unpackString(arr, 1, SOME 4))
val test10d = checkexn' "test10d" Subscript
(fn _=> Byte.unpackString(arr, ~1, SOME 0))
val test10e = checkexn' "test10e" Subscript
(fn _=>Byte.unpackString(arr, 11, SOME 0))
val test10f = checkexn' "test10f" Subscript
(fn _=>Byte.unpackString(arr, 0, SOME ~1))
val test10g = checkexn' "test10g" Subscript
(fn _=>Byte.unpackString(arr, 0, SOME 11))
val test10h = checkexn' "test10h" Subscript
(fn _=>Byte.unpackString(arr, 10, SOME 1))
val test10i = checkexn' "test10i" Subscript
(fn _=>Byte.unpackString(arr, ~1, NONE))
val test10j = checkexn' "test10j" Subscript
(fn _=>Byte.unpackString(arr, 11, NONE))
val vec = Word8Vector.tabulate(10, fn i => Word8.fromInt(i+65))
val test11a = check "test11a"
("" = Byte.unpackStringVec(vec, 0, SOME 0))
val test11b = check "test11b"
("" = Byte.unpackStringVec(vec, 10, SOME 0) andalso
"" = Byte.unpackStringVec(vec, 10, NONE))
val test11c = check "test11c"
("BCDE" = Byte.unpackStringVec(vec, 1, SOME 4))
val test11d = checkexn' "test11d" Subscript
(fn _=>Byte.unpackStringVec(vec, ~1, SOME 0))
val test11e = checkexn' "test11e" Subscript
(fn _=>Byte.unpackStringVec(vec, 11, SOME 0))
val test11f = checkexn' "test11f" Subscript
(fn _=>Byte.unpackStringVec(vec, 0, SOME ~1))
val test11g = checkexn' "test11g" Subscript
(fn _=>Byte.unpackStringVec(vec, 0, SOME 11))
val test11h = checkexn' "test11h" Subscript
(fn _=>Byte.unpackStringVec(vec, 10, SOME 1))
val test11i = checkexn' "test11i" Subscript
(fn _=>Byte.unpackStringVec(vec, ~1, NONE))
val test11j = checkexn' "test11j" Subscript
(fn _=>Byte.unpackStringVec(vec, 11, NONE))
in
val it = ()
end
;
