val a = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = ref [] : (int * int) list ref
fun f(index, value) = b := (index, 9-value) :: (!b)
val _ = MLWorks.Internal.ExtendedArray.iterate_index f a
val _ = case b of
ref[(9,0),(8,1),(7,2),(6,3),(5,4),(4,5),(3,6),(2,7),(1,8),(0,9)] => print"Pass\n"
| _ => print"Fail\n"
;
