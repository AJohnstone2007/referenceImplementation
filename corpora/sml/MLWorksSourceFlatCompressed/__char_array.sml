require "mono_array";
require "__char_vector";
structure CharArray : MONO_ARRAY =
struct
val cast = MLWorks.Internal.Value.cast
type elem = char
type vector = CharVector.vector
datatype array = A of MLWorks.Internal.ByteArray.bytearray
val maxLen = MLWorks.Internal.ByteArray.maxLen
fun check_size n =
if (cast n:word) < (cast maxLen:word) then n else raise Size
fun array (i: int, e: elem) : array =
A (MLWorks.Internal.ByteArray.array (check_size i, cast e))
fun tabulate (i : int, f : int -> elem) : array =
A (MLWorks.Internal.ByteArray.tabulate (check_size i,cast f))
fun fromList (l : elem list) : array =
(ignore(check_size (length l));
A (MLWorks.Internal.ByteArray.arrayoflist (cast l)))
val length : array -> int = cast(MLWorks.Internal.ByteArray.length)
val sub : (array * int) -> elem = cast(MLWorks.Internal.ByteArray.sub)
val update : (array * int * elem) -> unit = cast(MLWorks.Internal.ByteArray.update)
val extract : (array * int * int option ) -> vector =
fn (A a,i,len) =>
let
val len =
case len of
SOME l => l
| NONE => MLWorks.Internal.ByteArray.length a - i
in
if i >= 0 andalso len >= 0 andalso i + len <= MLWorks.Internal.ByteArray.length a
then cast(MLWorks.Internal.ByteArray.substring (a,i,len))
else raise Subscript
end
fun copy { src=A(src_ba), si, len, dst=A(dst_ba), di } =
let
val l = case len of
SOME l => l
| NONE => MLWorks.Internal.ByteArray.length src_ba - si
in
if si >= 0 andalso l >= 0 andalso si + l <= MLWorks.Internal.ByteArray.length src_ba
andalso di >= 0 andalso di + l <= MLWorks.Internal.ByteArray.length dst_ba
then MLWorks.Internal.ByteArray.copy(src_ba, si, si+l, dst_ba, di)
else raise Subscript
end
fun copyv_ba (from, start, len, to, start') =
let
val finish = start+len
val l1 = CharVector.length from
val l2 = MLWorks.Internal.ByteArray.length to
val unsafe_update = MLWorks.Internal.Value.unsafe_bytearray_update
val unsafe_sub = MLWorks.Internal.Value.unsafe_string_sub
in
if start < 0 orelse start > l1 orelse start+len > l1 orelse
start' < 0 orelse start' + len > l2 then
raise Subscript
else
let
fun copy' 0 = ()
| copy' n =
let
val n' = n-1
in
(unsafe_update (to, start'+n', unsafe_sub (from, start+n'));
copy' n')
end
in
copy' len
end
end
fun copyVec {src, si, len, dst=A(dst_ba), di} =
let
val len =
case len of
SOME l => l
| _ => CharVector.length src - si
in
copyv_ba(src, si, len, dst_ba, di)
end
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
val l = length array
val len = case j of
SOME len => i+len
| NONE => l
fun iterate' n =
if n >= len then
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
val len = case j of
SOME len => i+len
| NONE => l
fun reduce (n, x) =
if n >= l then
x
else
reduce(n+1, f(n, sub(array, n), x))
in
reduce(i, b)
end
fun foldri f b (array, i, j) =
let
val l = length array
val len = case j of
SOME len => i+len
| NONE => l
fun reduce (n, x) =
if n < i then
x
else
reduce(n-1, f(n, sub(array, n), x))
in
reduce(len-1, b)
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
val l = length array
val len = case j of
SOME len => i+len
| NONE => l
fun iterate n =
if n >= l then
()
else
(update(array, n, f(n, sub(array, n)));
iterate(n+1))
in
iterate i
end
end
;
