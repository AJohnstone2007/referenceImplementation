val a = MLWorks.Internal.ExtendedArray.array(10, 0)
val _ = MLWorks.Internal.ExtendedArray.update(a, 3, 2)
val b = MLWorks.Internal.ExtendedArray.sub(a, 3)
val _ = print(if b = 2 then "Pass1\n" else "Fail1\n")
val b = MLWorks.Internal.ExtendedArray.sub(a, 2)
val _ = print(if b = 0 then "Pass2\n" else "Fail2\n")
val b = MLWorks.Internal.ExtendedArray.sub(a, 4)
val _ = print(if b = 0 then "Pass3\n" else "Fail3\n")
;
