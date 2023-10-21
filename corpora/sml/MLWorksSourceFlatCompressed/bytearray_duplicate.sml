val a = MLWorks.Internal.ByteArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ByteArray.duplicate a
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
val _ = print(if eq(a, b) andalso a <> b then "Pass\n" else "Fail\n")
;
