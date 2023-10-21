val a = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ExtendedArray.subarray(a, 3, 5)
val _ = case MLWorks.Internal.ExtendedArray.to_list b of
[3,4] => print"Pass1\n"
| _ => print"Fail1\n"
val c = MLWorks.Internal.ExtendedArray.subarray(a, 3, 2)
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[3,2]
val _ = case MLWorks.Internal.ExtendedArray.to_list c of
[3,2] => print"Pass2\n"
| _ => print"Fail2\n"
val d = MLWorks.Internal.ExtendedArray.subarray(a, 3, 11)
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[3,11]
val _ = case MLWorks.Internal.ExtendedArray.to_list d of
[3,11] => print"Pass3\n"
| _ => print"Fail3\n"
val e = MLWorks.Internal.ExtendedArray.subarray(a, ~3, 2)
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[~3,2]
val _ = case MLWorks.Internal.ExtendedArray.to_list e of
[~3,2] => print"Pass4\n"
| _ => print"Fail4\n"
;
