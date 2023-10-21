require "array";
structure Array : ARRAY =
struct
structure Bits = MLWorks.Internal.Bits
type 'a array = 'a MLWorks.Internal.Array.array
type 'a vector = 'a MLWorks.Internal.Vector.vector
val maxLen = MLWorks.Internal.ExtendedArray.maxLen
fun check_size n =
if n < 0 orelse n > maxLen then raise Size else n
fun fromList l =
(ignore(check_size (length l));
MLWorks.Internal.Array.arrayoflist l)
val length = MLWorks.Internal.Array.length
fun check_slice (array,i,SOME j) =
if i < 0 orelse j < 0 orelse i + j > length array
then raise Subscript
else j
| check_slice (array,i,NONE) =
let
val l = length array
in
if i < 0 orelse i > l
then raise Subscript
else l - i
end
fun array (n,i) =
MLWorks.Internal.Array.array (check_size n,i)
fun tabulate (n,f) =
MLWorks.Internal.Array.tabulate (check_size n,f)
val sub = MLWorks.Internal.Array.sub
val update = MLWorks.Internal.Array.update
fun extract(array, i, j) =
let
val len = check_slice (array,i,j)
in
MLWorks.Internal.Vector.tabulate(len, fn n => sub (array, n+i))
end
fun copy {src, si, len, dst, di} =
let
val len = check_slice (src,si,len)
in
MLWorks.Internal.ExtendedArray.copy(src, si, si+len, dst, di)
end
fun copyVec{src : 'a vector, si : int, len, dst : 'a array, di : int} =
let
val len = case len of
SOME len => len
| NONE => MLWorks.Internal.Vector.length src-si
fun copy (from, start, finish, to, start') =
let
val l1 = MLWorks.Internal.Vector.length from
val l2 = length to
in
if start < 0 orelse start > l1 orelse finish > l1 orelse
start > finish orelse
start' < 0 orelse start' + finish - start > l2 then
raise Subscript
else
let
fun copy' 0 = ()
| copy' n =
let
val n' = n-1
in
(update (to, start'+n', MLWorks.Internal.Vector.sub (from, start+n'));
copy' n')
end
in
copy' (finish - start)
end
end
in
copy(src, si, si+len, dst, di)
end
val app = MLWorks.Internal.ExtendedArray.iterate
fun appi f (array, i, j) =
let
val len = check_slice (array,i,j)
fun iterate' n =
if n >= i + len then
()
else
(ignore(f (n, sub (array, n)));
iterate'(n+1))
in
iterate' i
end
val foldl = fn f => fn b => fn array => MLWorks.Internal.ExtendedArray.reducel (fn (a, b) => f (b, a)) (b, array)
val foldr = fn f => fn b => fn array => MLWorks.Internal.ExtendedArray.reducer f (array, b)
fun foldli f b (array, i, j) =
let
val l = length array
val len = check_slice (array,i,j)
fun reduce (n, x) =
if n = i + len then
x
else
reduce(n + 1, f(n, sub(array, n), x))
in
reduce(i, b)
end
fun foldri f b (array, i, j) =
let
val len = check_slice (array,i,j)
fun reduce (n, x) =
if n < i then
x
else
reduce(n-1, f(n, sub(array, n), x))
in
reduce(i + len-1, b)
end
fun modify f array =
let
val l = length array
fun iterate n =
if n = l then
()
else
(update(array, n, f(sub(array, n)));
iterate(n+1))
in
iterate 0
end
fun modifyi f (array, i, j) =
let
val len = check_slice (array,i,j)
fun iterate n =
if n = len then ()
else
(update(array, i+n, f (i+n, sub (array, i+n)));
iterate(n+1))
in
iterate 0
end
end
;
