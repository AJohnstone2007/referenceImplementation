let
fun foo(x, 0) = (x, 0)
| foo(x, n) = foo(MLWorks.Internal.Vector.vector [1,2,3,4,5,6] :: x, n-1)
val y = foo([], 1000000)
in
print("Pass " ^ Int.toString(#2 y) ^ "\n")
end
;
