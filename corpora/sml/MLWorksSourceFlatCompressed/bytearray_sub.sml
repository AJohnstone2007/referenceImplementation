val a = MLWorks.Internal.ByteArray.array(10, 5)
val b = MLWorks.Internal.ByteArray.sub(a, 3)
val _ = print(if b = 5 then "Pass\n" else "Fail\n")
;
