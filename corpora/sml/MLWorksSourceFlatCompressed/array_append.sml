val a = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,5]
val c = MLWorks.Internal.ExtendedArray.append(a, b)
val _ = case MLWorks.Internal.ExtendedArray.to_list c of
[0,1,2,3,4,5,6,7,8,9,0,1,2,3,5] => print"Pass\n"
| _ => print"Fail\n"
;
