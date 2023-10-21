require "mono_vector";
require "__string";
require "__pre_basis";
structure CharVector : MONO_VECTOR =
struct
type elem = char
type vector = string
val maxLen = PreBasis.maxSize + 1
fun check_size n = if n < 0 orelse n > maxLen then raise Size else n
val toint : elem -> int = ord
val fromint : int -> elem = chr
fun fromList x = (ignore(check_size (length x)); implode x)
fun tabulate(len, f) =
let
val _ = check_size len
fun next(n, acc) =
if n = len then acc else next(n+1, (f n) :: acc)
in
implode(rev(next(0, [])))
end
val length = size
fun sub (v,i) =
if i < 0 orelse i >= size v
then raise Subscript
else String.sub(v, i)
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
val extract =
fn (s,i,len) =>
let
val len = check_slice (s,i,len)
in
String.substring (s,i,len)
end
val concat = concat
fun appi f (vector, i, j) =
let
val l = length vector
val len = case j of
SOME len => i+len
| NONE => l
fun iterate n =
if n >= l then
()
else
(ignore(f(n, sub(vector, n)));
iterate(n+1))
in
iterate i
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
fun foldl f b vector =
let
val l = length vector
fun reduce(n, x) =
if n = l then
x
else
reduce(n+1, f(sub(vector, n), x))
in
reduce(0, b)
end
fun foldr f b vector =
let
val l = length vector
fun reduce(n, x) =
if n < 0 then
x
else
reduce(n-1, f(sub(vector, n), x))
in
reduce(l-1, b)
end
fun foldli f b (vector, i, j) =
let
val l = length vector
val len = case j of
SOME len => i+len
| NONE => l
fun reduce(n, x) =
if n >= len then
x
else
reduce(n+1, f(n, sub(vector, n), x))
in
reduce(0, b)
end
fun foldri f b (vector, i, j) =
let
val l = length vector
val len = case j of
SOME len => i+len
| NONE => l
fun reduce(n, x) =
if n < 0 then
x
else
reduce(n-1, f(n, sub(vector, n), x))
in
reduce(len-1, b)
end
val map = String.map and mapi = String.mapi
end
;
