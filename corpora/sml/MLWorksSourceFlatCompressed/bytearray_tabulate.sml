fun f x = 10-x
val a = MLWorks.Internal.ByteArray.tabulate(10, f)
val b = MLWorks.Internal.ByteArray.arrayoflist[10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
fun eq(a, b) =
let
val l = MLWorks.Internal.ByteArray.length a
in
if l <> MLWorks.Internal.ByteArray.length b then
(print"Fail on unequal length\n";
false)
else
let
fun compare 0 = true
| compare n =
let
val n' = n-1
in
MLWorks.Internal.ByteArray.sub(a, n') = MLWorks.Internal.ByteArray.sub(b, n') andalso
compare n'
end
in
compare l
end
end
val _ = print(if eq(a, b) then "Pass\n" else "Fail\n")
;
