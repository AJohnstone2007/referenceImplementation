val a = MLWorks.Internal.ByteArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ByteArray.subarray(a, 3, 5)
val _ = case MLWorks.Internal.ByteArray.to_list b of
[3,4] => print"Pass1\n"
| _ => print"Fail1\n"
val c = MLWorks.Internal.ByteArray.subarray(a, 3, 2)
handle MLWorks.Internal.ByteArray.Subscript =>
MLWorks.Internal.ByteArray.from_list[3,2]
val _ = case MLWorks.Internal.ByteArray.to_list c of
[3,2] => print"Pass2\n"
| _ => print"Fail2\n"
val d = MLWorks.Internal.ByteArray.subarray(a, 3, 11)
handle MLWorks.Internal.ByteArray.Subscript =>
MLWorks.Internal.ByteArray.from_list[3,11]
val _ = case MLWorks.Internal.ByteArray.to_list d of
[3,11] => print"Pass3\n"
| _ => print"Fail3\n"
val e = MLWorks.Internal.ByteArray.subarray(a, ~3, 2)
handle MLWorks.Internal.ByteArray.Subscript =>
MLWorks.Internal.ByteArray.from_list[253,2]
val _ = case MLWorks.Internal.ByteArray.to_list e of
[253,2] => print"Pass4\n"
| _ => print"Fail4\n"
;
