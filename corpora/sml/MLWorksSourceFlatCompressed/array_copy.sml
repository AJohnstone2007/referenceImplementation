val a = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ExtendedArray.array(10, 0)
val _ = MLWorks.Internal.ExtendedArray.copy(a, 0, 4, b, 1)
val _ = case MLWorks.Internal.ExtendedArray.to_list b of
[0,0,1,2,3,0,0,0,0,0] => print"Pass\n"
| _ => print"Fail\n"
val c = (MLWorks.Internal.ExtendedArray.copy(a, 3, 2, b, 1);
MLWorks.Internal.ExtendedArray.from_list [])
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[3,2,1]
val _ = case MLWorks.Internal.ExtendedArray.to_list c of
[3,2,1] => print"Pass2\n"
| _ => print"Fail2\n"
val d = (MLWorks.Internal.ExtendedArray.copy(a, 3, 11, b ,1);
MLWorks.Internal.ExtendedArray.from_list [])
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[3,11,1]
val _ = case MLWorks.Internal.ExtendedArray.to_list d of
[3,11, 1] => print"Pass3\n"
| _ => print"Fail3\n"
val e = (MLWorks.Internal.ExtendedArray.copy(a, ~3, 2, b, 1);
MLWorks.Internal.ExtendedArray.from_list [])
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[~3,2,1]
val _ = case MLWorks.Internal.ExtendedArray.to_list e of
[~3,2, 1] => print"Pass4\n"
| _ => print"Fail4\n"
val f = (MLWorks.Internal.ExtendedArray.copy(a, 0, 9, b, 4);
MLWorks.Internal.ExtendedArray.from_list [])
handle MLWorks.Internal.ExtendedArray.Subscript =>
MLWorks.Internal.ExtendedArray.from_list[0,9,4]
val _ = case MLWorks.Internal.ExtendedArray.to_list f of
[0, 9, 4] => print"Pass5\n"
| _ => print"Fail5\n"
;
