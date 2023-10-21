val a = MLWorks.Internal.ExtendedArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ExtendedArray.duplicate a
fun eq(a, b) =
let
val l = MLWorks.Internal.ExtendedArray.length a
in
if l <> MLWorks.Internal.ExtendedArray.length b then
(print"Fail on unequal length\n";
false)
else
let
fun compare 0 = true
| compare n =
let
val n' = n-1
in
MLWorks.Internal.ExtendedArray.sub(a, n') = MLWorks.Internal.Array.sub(b, n') andalso
compare n'
end
in
compare l
end
end
val _ = print(if eq(a, b) andalso a <> b then "Pass\n" else "Fail\n")
;
