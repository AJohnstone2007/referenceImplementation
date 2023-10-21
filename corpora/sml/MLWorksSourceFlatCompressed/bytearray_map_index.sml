val a = MLWorks.Internal.ByteArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ByteArray.map_index (fn (index, value:int) => index*value) a
val _ = case MLWorks.Internal.ByteArray.to_list b of
[0,1,4,9,16,25,36,49,64,81] => print"Pass\n"
| _ => print"Fail\n"
;
