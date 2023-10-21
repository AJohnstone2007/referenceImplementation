require "../basis/__int";
require "text";
require "crash";
require "lists";
require "mutableintset";
functor SmallIntSet (structure Text : TEXT
structure Crash : CRASH
structure Lists : LISTS
val int_to_text : int -> Text.T) : MUTABLEINTSET =
struct
structure Bits = MLWorks.Internal.Bits
structure Text = Text
fun crash message = Crash.impossible ("SmallIntSet: " ^ message)
val % = Int.toString
type T = int MLWorks.Internal.Array.array
type element = int
fun min (x : int, y : int) = if x<y then x else y
fun max (x : int, y : int) = if x>y then x else y
val update = MLWorks.Internal.Value.unsafe_array_update
val sub = MLWorks.Internal.Value.unsafe_array_sub
fun zero (set, base, size) =
let
fun zero' 0 = true
| zero' n = sub (set, base+n-1) = 0 andalso zero' (n-1)
in
zero' size
end
fun copy (to, from, base, size) =
let
fun copy' 0 = to
| copy' n = (update (to, base+n-1, sub (from, base+n-1)); copy' (n-1))
in
copy' size
end
fun resize (set, size) =
let
val current = MLWorks.Internal.Array.length set
val new = MLWorks.Internal.Array.array (size, 0)
fun loop i =
if i = current
then ()
else
(update (new,i,sub(set,i));
loop (i+1))
in
loop 0;
new
end
val empty = MLWorks.Internal.Array.array (0, 0)
fun singleton int =
let
val index = Bits.rshift (int, 4)
val mask = Bits.lshift (1, Bits.andb (int, 15))
val new = MLWorks.Internal.Array.array (index+1, 0)
in
update (new, index, mask);
new
end
fun add (set, int) =
let
val index = Bits.rshift (int, 4)
val mask = Bits.lshift (1, Bits.andb (int, 15))
val new = resize (set, max (MLWorks.Internal.Array.length set, index+1))
in
update (new, index, Bits.orb (sub (new, index), mask));
new
end
fun add' (set, int) =
let
val index = Bits.rshift (int, 4)
val mask = Bits.lshift (1, Bits.andb (int, 15))
in
if index < MLWorks.Internal.Array.length set then
let
val old = sub(set, index)
in
update(set, index, Bits.orb(old, mask));
set
end
else
let
val new = resize(set, index+1)
in
update(new, index, mask);
new
end
end
fun remove (set, int) =
let
val size = MLWorks.Internal.Array.length set
val index = Bits.rshift (int, 4)
in
if index >= size then
set
else
let
val new = resize (set, size)
val mask = Bits.notb (Bits.lshift (1, Bits.andb (int, 15)))
in
update (new, index, Bits.andb (sub (new, index), mask));
new
end
end
fun remove' (set, int) =
let
val size = MLWorks.Internal.Array.length set
val index = Bits.rshift (int, 4)
in
if index >= size then
set
else
let
val mask = Bits.notb (Bits.lshift (1, Bits.andb (int, 15)))
in
update (set, index, Bits.andb (sub (set, index), mask));
set
end
end
fun member (set, int) =
let
val index = Bits.rshift (int, 4)
in
if index >= MLWorks.Internal.Array.length set orelse index < 0
then false
else
let
val mask = Bits.lshift (1, Bits.andb (int, 15))
in
(case Bits.andb (sub (set, index), mask)
of 0 => false
| _ => true)
end
end
fun is_empty set =
set = empty orelse
let
val size = MLWorks.Internal.Array.length set
in
zero (set, 0, size)
end
fun equal (set, set') =
set = set' orelse
let
val size' = MLWorks.Internal.Array.length set'
val size = MLWorks.Internal.Array.length set
fun compare 0 = true
| compare n = sub (set, n-1) = sub (set', n-1) andalso compare (n-1)
in
if size <= size' then
compare size andalso zero (set', size, size'-size)
else
compare size' andalso zero (set, size', size-size')
end
fun subset (set, set') =
set = set' orelse
let
val size' = MLWorks.Internal.Array.length set'
val size = MLWorks.Internal.Array.length set
fun subset' 0 = true
| subset' n = Bits.andb (sub (set, n-1), Bits.notb (sub (set', n-1))) = 0 andalso subset' (n-1)
in
if size <= size' then
subset' size
else
subset' size' andalso zero (set, size', size-size')
end
fun intersection (set, set') =
if set = set' then set else
let
val size = min (MLWorks.Internal.Array.length set, MLWorks.Internal.Array.length set')
val new = MLWorks.Internal.Array.array (size, 0)
fun intersection' 0 = new
| intersection' n =
(update (new, n-1, Bits.andb (sub (set, n-1), sub (set', n-1)));
intersection' (n-1))
in
intersection' size
end
fun intersection' (set, set') =
if set = set' then set else
let
val size = min (MLWorks.Internal.Array.length set, MLWorks.Internal.Array.length set')
fun intersection' 0 = set
| intersection' n =
(update (set, n-1, Bits.andb (sub (set, n-1), sub (set', n-1)));
intersection' (n-1))
in
intersection' size
end
fun union (set, set') =
if set = set' then set else
let
val size' = MLWorks.Internal.Array.length set'
val size = MLWorks.Internal.Array.length set
val new = MLWorks.Internal.Array.array (max (size, size'), 0)
fun union' 0 = new
| union' n =
(update (new, n-1, Bits.orb (sub (set, n-1), sub (set', n-1)));
union' (n-1))
in
if size <= size' then
(MLWorks.Internal.ExtendedArray.iterate_index
(fn (index, a) =>
update (new, index, Bits.orb (a, sub (set', index))))
set;
copy (new, set', size, size'-size))
else
(MLWorks.Internal.ExtendedArray.iterate_index
(fn (index, a) =>
update (new, index, Bits.orb (a, sub (set, index))))
set';
copy (new, set, size', size-size'))
end
fun union' (set, set') =
if set = set' then set else
let
val size = MLWorks.Internal.Array.length set
val size' = MLWorks.Internal.Array.length set'
in
if size < size' then
let
val new = MLWorks.Internal.Array.array (size', 0)
fun loop i =
if i = size then ()
else
(update (new,i,Bits.orb (sub(set,i),sub(set',i)));
loop (i+1))
in
loop 0;
copy (new, set', size, size'-size)
end
else
let
fun loop i =
if i = size' then ()
else
(update (set,i,Bits.orb(sub (set',i),sub(set,i)));
loop (i+1))
in
(loop 0;
set)
end
end
fun difference (set, set') =
if set = set' then empty else
let
val size = MLWorks.Internal.Array.length set
val size' = min (size, MLWorks.Internal.Array.length set')
val new = MLWorks.Internal.Array.array (size, 0)
fun difference' 0 = ()
| difference' n =
(update (new, n-1, Bits.andb (sub (set, n-1), Bits.notb (sub (set', n-1))));
difference' (n-1))
in
difference' size';
copy (new, set, size', size - size')
end
fun difference' (set, set') =
if set = set' then empty else
let
val size = min (MLWorks.Internal.Array.length set, MLWorks.Internal.Array.length set')
fun difference' 0 = set
| difference' n =
(update (set, n-1, Bits.andb (sub (set, n-1), Bits.notb (sub (set', n-1))));
difference' (n-1))
in
difference' size
end
fun cardinality set =
let
fun count (c, 0) = c
| count (c, w) = count (c+1, Bits.andb (w, w-1))
in
MLWorks.Internal.ExtendedArray.reducel count (0, set)
end
fun reduce f (identity, set) =
let
val size = MLWorks.Internal.Array.length set
fun word (result, p, i) =
if i = size then
result
else
let
fun chunk (result, p, 0) = result
| chunk (result, p, w) =
let
val result' =
case Bits.andb (w, 15)
of 0 => result
| 1 => f (result, p)
| 2 => f (result, p+1)
| 3 => f (f (result, p), p+1)
| 4 => f (result, p+2)
| 5 => f (f (result, p), p+2)
| 6 => f (f (result, p+1), p+2)
| 7 => f (f (f (result, p), p+1), p+2)
| 8 => f (result, p+3)
| 9 => f (f (result, p), p+3)
| 10 => f (f (result, p+1), p+3)
| 11 => f (f (f (result, p), p+1), p+3)
| 12 => f (f (result, p+2), p+3)
| 13 => f (f (f (result, p), p+2), p+3)
| 14 => f (f (f (result, p+1), p+2), p+3)
| _ => f (f (f (f (result, p), p+1), p+2), p+3)
in
chunk (result', p+4, Bits.rshift (w, 4))
end
in
word (chunk (result, p, sub (set, i)), p+16, i+1)
end
in
word (identity, 0, 0)
end
fun iterate f set =
let
val size = MLWorks.Internal.Array.length set
fun word (p, i) =
if i = size then
()
else
(chunk (p, sub (set, i));
word (p+16, i+1))
and chunk (p, 0) = ()
| chunk (p, w) =
(case Bits.andb (w, 15)
of 0 => ()
| 1 => f p
| 2 => f (p+1)
| 3 => (f p; f (p+1))
| 4 => f (p+2)
| 5 => (f p; f (p+2))
| 6 => (f (p+1); f (p+2))
| 7 => (f p; f (p+1); f (p+2))
| 8 => f (p+3)
| 9 => (f p; f (p+3))
| 10 => (f (p+1); f (p+3))
| 11 => (f p; f (p+1); f (p+3))
| 12 => (f (p+2); f (p+3))
| 13 => (f p; f (p+2); f (p+3))
| 14 => (f (p+1); f (p+2); f (p+3))
| _ => (f p; f (p+1); f (p+2); f (p+3));
chunk (p+4, Bits.rshift (w, 4)))
in
word (0, 0)
end
fun filter predicate set =
let
val size = MLWorks.Internal.Array.length set
val new = MLWorks.Internal.Array.array (size, 0)
fun word 0 = new
| word i =
let
val element = sub (set, i-1)
in
case element
of 0 => word (i-1)
| _ =>
let
val base = Bits.lshift (i-1, 4)
fun bit (w, 0) = w
| bit (w, b) =
let
val w' =
case Bits.andb (Bits.rshift (element, b-1), 1)
of 0 => Bits.lshift (w, 1)
| _ => Bits.orb (Bits.lshift (w, 1), if predicate (base + b-1) then 1 else 0)
in
bit (w', b-1)
end
in
update (new, i-1, bit (0, 16));
word (i-1)
end
end
in
word size
end
fun filter' predicate set =
let
val size = MLWorks.Internal.Array.length set
fun word 0 = set
| word i =
let
val element = sub (set, i-1)
val base = Bits.lshift (i-1, 4)
fun bit (w, 0) = w
| bit (w, b) =
let
val w' =
case Bits.andb (Bits.rshift (element, b-1), 1)
of 0 => Bits.lshift (w, 1)
| _ => Bits.orb (Bits.lshift (w, 1), if predicate (base + b-1) then 1 else 0)
in
bit (w', b-1)
end
in
update (set, i-1, bit (0, 16));
word (i-1)
end
in
word size
end
fun to_list set = reduce (fn (list, int) => int::list) ([], set)
fun from_list list =
let
val largest = Lists.reducel max (0, list)
val new = MLWorks.Internal.Array.array (Bits.rshift (largest, 4) + 1, 0)
in
Lists.iterate
(fn int =>
let
val index = Bits.rshift (int, 4)
val mask = Bits.lshift (1, Bits.andb (int, 15))
in
update (new, index, Bits.orb (sub (new, index), mask))
end)
list;
new
end
fun to_text set =
let
infix ^^
val (op^^) = Text.concatenate
val $ = Text.from_string
val (_, text) =
reduce (fn ((0, text), int) => (1, int_to_text int)
| ((_, text), int) => (1, text ^^ $"," ^^ int_to_text int)) ((0, $""), set)
in
$"{" ^^ text ^^ $"}"
end
val copy' = MLWorks.Internal.ExtendedArray.duplicate
end
;
