val a = MLWorks.Internal.ByteArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = ref [] : int list ref
fun f x = b := x :: (!b)
val _ = MLWorks.Internal.ByteArray.iterate f a
val _ = case b of
ref[9,8,7,6,5,4,3,2,1,0] => print"Pass\n"
| _ => print"Fail\n"
;
