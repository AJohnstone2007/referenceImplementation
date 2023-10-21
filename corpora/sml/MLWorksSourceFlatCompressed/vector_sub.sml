val a = MLWorks.Internal.Vector.vector[0,1,2,3,4,5,6,7,8,9]
val _ =
if MLWorks.Internal.Vector.sub(a, 0) = 0 andalso
MLWorks.Internal.Vector.sub(a, 1) = 1 andalso
MLWorks.Internal.Vector.sub(a, 2) = 2 andalso
MLWorks.Internal.Vector.sub(a, 3) = 3 andalso
MLWorks.Internal.Vector.sub(a, 4) = 4 andalso
MLWorks.Internal.Vector.sub(a, 5) = 5 andalso
MLWorks.Internal.Vector.sub(a, 6) = 6 andalso
MLWorks.Internal.Vector.sub(a, 7) = 7 andalso
MLWorks.Internal.Vector.sub(a, 8) = 8 andalso
MLWorks.Internal.Vector.sub(a, 9) = 9 then
print"Pass1\n"
else
print"Fail1\n"
val _ =
(ignore(MLWorks.Internal.Vector.sub(a, ~1));
print"Fail2.1\n") handle MLWorks.Internal.Vector.Subscript =>
((ignore(MLWorks.Internal.Vector.sub(a, 10));
print"Fail2.2\n") handle MLWorks.Internal.Vector.Subscript =>
print"Pass2\n")
;
