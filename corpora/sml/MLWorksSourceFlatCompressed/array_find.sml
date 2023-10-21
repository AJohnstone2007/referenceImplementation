val a = MLWorks.Internal.ExtendedArray.arrayoflist[1,2,3,4,5,6,7,8,9,0]
val b = MLWorks.Internal.ExtendedArray.find (fn x => x = 4) a
val c = (MLWorks.Internal.ExtendedArray.find (fn x => x = ~1) a) handle MLWorks.Internal.ExtendedArray.Find => 10
val _ =
if b = 3 then print"Pass 1\n" else print"Fail 1\n"
val _ =
if c = 10 then print"Pass 2\n" else print"Fail 2\n"
;
