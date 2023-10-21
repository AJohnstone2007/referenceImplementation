require "mono_vector";
require "__pre_basis";
require "__word8";
require "__string";
structure Word8Vector :> EQ_MONO_VECTOR
where type elem = Word8.word =
struct
type elem = Word8.word
type vector = string
val etoc : elem -> char = chr o Word8.toInt
val ctoe : char -> elem = Word8.fromInt o ord
val maxLen = PreBasis.maxSize + 1
fun check_size n = if n < 0 orelse n > maxLen then raise Size else n
val str : elem -> string = str o etoc
fun fromList (xs:elem list):vector = implode (map etoc xs)
fun tabulate(len, f) =
let
val _ = check_size len
fun next(n, acc) =
if n = len then acc else next(n+1, str(f n) :: acc)
in
concat(rev(next(0, [])))
end
val length = size
fun sub (v,i) =
if i < 0 orelse i >= size v
then raise Subscript
else ctoe(String.sub(v,i))
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
fun extract (s, 0, NONE) = s
| extract (s, i, len) =
let
val len = check_slice (s, i, len)
in
String.substring(s, i, len)
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
fun map f v =
let
val l = size v
val newS = MLWorks.Internal.Value.alloc_string (l+1)
val i = ref 0
val _ =
while (!i<l) do(
MLWorks.Internal.Value.unsafe_string_update
(newS, !i,
Word8.toInt(
f(
Word8.fromInt(
MLWorks.Internal.Value.unsafe_string_sub (v,!i)))));
i := !i + 1 )
val _ =
MLWorks.Internal.Value.unsafe_string_update(newS, l, 0)
in
newS
end
fun mapi f (v, s, l) =
let
val l' = check_slice (v, s, l)
val newS = MLWorks.Internal.Value.alloc_string (l'+1)
val i = ref 0
val _ =
while (!i<l') do (
MLWorks.Internal.Value.unsafe_string_update
(newS, !i,
Word8.toInt (
f(
!i + s,
Word8.fromInt(
MLWorks.Internal.Value.unsafe_string_sub(v, !i+s )))));
i := !i + 1)
val _ =
MLWorks.Internal.Value.unsafe_string_update(newS, l', 0)
in
newS
end
end
;
