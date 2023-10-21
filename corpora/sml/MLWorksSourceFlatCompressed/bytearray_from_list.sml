val a = MLWorks.Internal.ByteArray.from_list[1,2,3,4,5,1,2,3,4,5]
val b = MLWorks.Internal.ByteArray.length a
val c = MLWorks.Internal.ByteArray.sub(a, 7)
val _ = print(if b = 10 then "Pass1\n" else "Fail1\n")
val _ = print(if c = 3 then "Pass2\n" else "Fail2\n")
;
