val a = MLWorks.Internal.FloatArray.array(10, 0.0)
val _ = MLWorks.Internal.FloatArray.fill_range(a, 0, 4, 1.0)
val _ = case (map floor (MLWorks.Internal.FloatArray.to_list a))
of [1,1,1,1,0,0,0,0,0,0] =>
print"Pass\n"
| _ => print"Fail\n"
val c = (MLWorks.Internal.FloatArray.fill_range(a, 3, 2, 1.0);
MLWorks.Internal.FloatArray.from_list [])
handle MLWorks.Internal.FloatArray.Subscript =>
MLWorks.Internal.FloatArray.from_list(map real [3,2])
val _ = case (map floor (MLWorks.Internal.FloatArray.to_list c))
of [3,2] => print"Pass2\n"
| _ => print"Fail2\n"
val d = (MLWorks.Internal.FloatArray.fill_range(a, 3, 11, 1.0);
MLWorks.Internal.FloatArray.from_list [])
handle MLWorks.Internal.FloatArray.Subscript =>
MLWorks.Internal.FloatArray.from_list(map real [3,11])
val _ = case (map floor (MLWorks.Internal.FloatArray.to_list d))
of [3,11] => print"Pass3\n"
| _ => print"Fail3\n"
val e = (MLWorks.Internal.FloatArray.fill_range(a, ~3, 2, 1.0);
MLWorks.Internal.FloatArray.from_list [])
handle MLWorks.Internal.FloatArray.Subscript =>
MLWorks.Internal.FloatArray.from_list(map real [253,2])
val _ = case (map floor (MLWorks.Internal.FloatArray.to_list e))
of [253,2] => print"Pass4\n"
| _ => print"Fail4\n"
;
