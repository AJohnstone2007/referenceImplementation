require "mono_array";
require "__pre_real";
require "__real_vector";
structure RealArray : MONO_ARRAY where type elem = PreReal.real =
struct
type elem = PreReal.real
type vector = RealVector.vector
structure F = MLWorks.Internal.FloatArray
type array = F.floatarray
val maxLen = F.maxLen
fun check_size n =
if n < 0 orelse n > maxLen then raise Size else n
fun array (i, e) = F.array (check_size i, e)
fun tabulate (i, f) = F.tabulate (check_size i,f)
fun fromList [] = F.array(0,0.0)
| fromList l =
if length l> maxLen then raise Size else F.from_list l
val length = F.length
val sub = F.sub
val update = F.update
val cast = MLWorks.Internal.Value.cast
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
fun extract(array, i, j): vector =
let
val len = check_slice (array,i,j)
in
cast(F.tabulate(len, fn n => sub (array, n+i)))
end
fun copy { src, si, len, dst, di } =
let
val srcLen = length src
val l = case len
of SOME l => l
| NONE => srcLen - si
in
if si >= 0
andalso l >= 0
andalso si + l <= srcLen
andalso di >= 0
andalso di + l <= length dst
then F.copy(src, si, si+l, dst, di)
else raise Subscript
end
fun copyVec {src, si, len, dst, di} =
copy {src=cast src,si=si,len=len,dst=dst,di=di}
fun app f vector =
let
val l = length vector
fun iterate n =
if n = l then
()
else
(ignore(f(sub(vector, n)));
iterate(n+1))
in
iterate 0
end
fun appi f (array, i, j) =
let
val len = check_slice(array,i,j)
fun iterate' n =
if n >= i+ len then
()
else
(ignore(f (n, sub (array, n)));
iterate'(n+1))
in
iterate' i
end
fun foldl f b array =
let
val l = length array
fun reduce(n, x) =
if n = l then
x
else
reduce(n+1, f(sub(array, n), x))
in
reduce(0, b)
end
fun foldr f b array =
let
val l = length array
fun reduce(n, x) =
if n < 0 then
x
else
reduce(n-1, f(sub(array, n), x))
in
reduce(l-1, b)
end
fun foldli f b (array, i, j) =
let
val l = length array
val len = check_slice(array,i,j)
fun reduce (n, x) =
if n = i+len then
x
else
reduce(n+1, f(n, sub(array, n), x))
in
reduce(i, b)
end
fun foldri f b (array, i, j) =
let
val len = check_slice(array,i,j)
fun reduce (n, x) =
if n < i then
x
else
reduce(n-1, f(n, sub(array, n), x))
in
reduce(i+len-1, b)
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
val len = check_slice(array,i,j)
fun iterate n =
if n = i+len then
()
else
(update(array, n, f(n, sub(array, n)));
iterate(n+1))
in
iterate i
end
end
;
