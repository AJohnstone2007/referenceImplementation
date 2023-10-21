require "^.basis.__int";
require "hashset";
require "crash";
require "lists";
functor HashSet(
structure Crash : CRASH
structure Lists : LISTS
type element
val eq : element * element -> bool
val hash : element -> int
) : HASHSET =
struct
structure Bits = MLWorks.Internal.Bits
type element = element
type HashSet = ((int * element) list MLWorks.Internal.ExtendedArray.array * int ref) ref
val max_size = 16384
fun member(elt, []) = false
| member(elt, (_, x) :: xs) = eq(elt, x) orelse member(elt, xs)
val expansion_ratio = 2
val max_size_ratio = 4
fun set_size(ref(the_lists, ref count)) =
let
val c1 =
MLWorks.Internal.ExtendedArray.reducel
(fn (sum, list) => sum + length list)
(0, the_lists)
val c2 = MLWorks.Internal.ExtendedArray.length the_lists * max_size_ratio - count
in
if c1 = c2 then c1
else
Crash.impossible
(concat["Strange hashset size not as expected\n",
"Length = ",
Int.toString(MLWorks.Internal.ExtendedArray.length the_lists),
", count = ",
Int.toString count,
", c1 = ",
Int.toString c1,
"\n"])
end
local
fun nearest_power_of_two (size,x) =
if x >= size
then x
else nearest_power_of_two (size,2*x)
in
fun empty_set size =
let
val initial_size = nearest_power_of_two(size, 1)
in
ref(MLWorks.Internal.ExtendedArray.array(initial_size, [] : (int * element) list),
ref(max_size_ratio * initial_size))
end
end
fun force_shrink(elts as ref(the_list, count as ref c)) =
let
val old_size = MLWorks.Internal.ExtendedArray.length the_list
val new_size = old_size div expansion_ratio
val _ =
if new_size <= 0 then
Crash.impossible"Hashset resized down to zero length??"
else
()
val new_arr = MLWorks.Internal.ExtendedArray.array(new_size, [] : (int * element) list)
fun deal_with_elements index =
if index < 0 then ()
else
(let
val f = MLWorks.Internal.ExtendedArray.sub(the_list, index)
val s = MLWorks.Internal.ExtendedArray.sub(the_list, index + new_size)
in
MLWorks.Internal.ExtendedArray.update(new_arr, index, f @ s)
end;
deal_with_elements(index - 1))
val new_count = max_size_ratio * (new_size - old_size) + c
in
deal_with_elements(new_size - 1);
elts := (new_arr, ref new_count);
elts
end
fun force_resize(elts as ref(the_list, count as ref c)) =
let
val old_size = MLWorks.Internal.ExtendedArray.length the_list
val new_size = old_size * expansion_ratio
val new_arr = MLWorks.Internal.ExtendedArray.array(new_size, [] : (int * element) list)
fun partition([], size, f, s) = (f, s)
| partition((elem as (hashvalue,_)) :: rest, size, f, s) =
if Bits.andb(hashvalue, size) = 0
then partition(rest, size, elem :: f, s)
else partition(rest, size, f, elem :: s)
fun deal_with_elements(_, []) = ()
| deal_with_elements(index, elems) =
let
val (f,s) = partition(elems, old_size, [], [])
in
MLWorks.Internal.ExtendedArray.update(new_arr, index, f);
MLWorks.Internal.ExtendedArray.update(new_arr, index + old_size, s)
end
val new_count = max_size_ratio * (new_size - old_size) + c
in
MLWorks.Internal.ExtendedArray.iterate_index deal_with_elements the_list;
elts := (new_arr, ref new_count);
elts
end
fun resize(elts as ref(_, ref c)) =
if c >= 0 then elts
else
resize(force_resize elts)
fun add_member'(elts as ref(the_list, count as ref c), elt) =
let
val hash = hash elt
val old_size = MLWorks.Internal.ExtendedArray.length the_list
val pos = Bits.andb(hash, old_size-1)
val set = MLWorks.Internal.ExtendedArray.sub(the_list, pos)
in
if member(elt, set) then
elts
else
(MLWorks.Internal.ExtendedArray.update(the_list, pos, (hash, elt) :: set);
count := c - 1;
resize elts)
end
fun add_member(arg as (elts, _)) = (ignore(add_member' arg); elts)
fun remove_member(elts as ref(the_list, count as ref c), elt) =
let
val hash = hash elt
val size = MLWorks.Internal.ExtendedArray.length the_list
val pos = Bits.andb(hash, size-1)
val set = MLWorks.Internal.ExtendedArray.sub(the_list, pos)
in
if member(elt, set) then
(MLWorks.Internal.ExtendedArray.update(the_list, pos,
Lists.filterp (fn (_, x) => not(eq(x, elt))) set);
count := c + 1;
elts)
else
elts
end
fun empty_setp(ref(the_list, _)) =
MLWorks.Internal.ExtendedArray.reducel
(fn (false, _) => false
| (_, []) => true
| _ => false)
(true, the_list)
fun is_member(ref(the_list, _), elt) =
let
val hash = hash elt
val size = MLWorks.Internal.ExtendedArray.length the_list
val pos = Bits.andb(hash, size-1)
val set = MLWorks.Internal.ExtendedArray.sub(the_list, pos)
in
member(elt, set)
end
fun fold f (base, ref(the_list, _)) =
MLWorks.Internal.ExtendedArray.reducel
(Lists.reducel (fn (acc, (_, x)) => f(acc, x)))
(base, the_list)
fun iterate f (ref(the_list, _)) =
MLWorks.Internal.ExtendedArray.iterate
(Lists.iterate (fn (_, x) => f x))
the_list
fun set_to_list s = fold (fn (L, e) => e :: L) ([], s)
val add_list_to_set = Lists.reducel add_member
val add_list = add_list_to_set
fun list_to_set set = add_list_to_set(empty_set(length set div 2), set)
fun union_lists([], _, acc) = acc
| union_lists((z as (_, x)) :: xs, y, acc) =
union_lists(xs, y, if member(x, y) then acc else z :: acc)
fun unite_sets(ref(list, ref count), ref(list', _)) =
let
val count = ref count
in
resize
(ref
(MLWorks.Internal.ExtendedArray.map_index
(fn (index, elem) =>
let
val old_list = MLWorks.Internal.ExtendedArray.sub(list, index)
val new_list = union_lists(old_list, elem, elem)
in
count := !count - length new_list + length old_list;
new_list
end)
list', count))
end
fun union(arg as (a1 as ref(set1, _), a2 as ref(set2, _))) =
let
val l1 = MLWorks.Internal.ExtendedArray.length set1
val l2 = MLWorks.Internal.ExtendedArray.length set2
in
if l1 = l2 then
unite_sets arg
else
if l1 < l2 then
union(force_resize a1, a2)
else
union(a1, force_resize a2)
end
fun intersect_lists([], list, acc) = acc
| intersect_lists(list, [], acc) = acc
| intersect_lists((z as (_, x)) :: xs, y, acc) =
intersect_lists(xs, y, if member(x, y) then z :: acc else acc)
fun intersect_sets(set1 as ref(list, ref count), ref(list', _)) =
let
val count = ref count
in
ref
(MLWorks.Internal.ExtendedArray.map_index
(fn (index, elem) =>
let
val old_list = MLWorks.Internal.ExtendedArray.sub(list, index)
val new_list = intersect_lists(old_list, elem, [])
in
count := !count - length new_list + length old_list;
new_list
end)
list', count)
end
fun intersection(arg as (a1 as ref(set1, _), a2 as ref(set2, _))) =
let
val l1 = MLWorks.Internal.ExtendedArray.length set1
val l2 = MLWorks.Internal.ExtendedArray.length set2
in
if l1 = l2 then
intersect_sets arg
else
if l2 < l1 then
intersection(force_shrink a1, a2)
else
intersection(a1, force_shrink a2)
end
fun subset(ref(list, _), arg2 as ref(list', _)) =
MLWorks.Internal.ExtendedArray.reducel
(fn (false, _) => false
| (_, list) => Lists.forall (fn (_, elt) => is_member(arg2, elt)) list)
(true, list)
fun list_diff([], _, acc) = acc
| list_diff((z as (_, x)) :: xs, y, acc) =
list_diff(xs, y, if member(x, y) then acc else z :: acc)
fun diff_sets(ref(list, ref count), ref(list', _)) =
let
val count = ref count
in
ref
(MLWorks.Internal.ExtendedArray.map_index
(fn (index, elem) =>
let
val old_list = MLWorks.Internal.ExtendedArray.sub(list, index)
val new_list = list_diff(old_list, elem, [])
in
count := !count - length new_list + length old_list;
new_list
end)
list', count)
end
fun setdiff(arg as (a1 as ref(set1, _), a2 as ref(set2, _))) =
let
val l1 = MLWorks.Internal.ExtendedArray.length set1
val l2 = MLWorks.Internal.ExtendedArray.length set2
in
if l1 = l2 then
diff_sets arg
else
if l1 < l2 then
setdiff(force_resize a1, a2)
else
setdiff(a1, force_resize a2)
end
fun seteq(arg as (set1, set2)) =
subset(set1, set2) andalso
set_size set1 = set_size set2
fun set_print(hashset, printfun) =
let
val list = set_to_list hashset
fun print_sub([], acc) = concat(rev acc)
| print_sub([x], acc) = concat(rev(printfun x :: acc))
| print_sub(x :: xs, acc) = print_sub(xs, "," :: printfun x :: acc)
in
print_sub(list, [])
end
fun rehash(set as ref(array, _)) =
let
val length = MLWorks.Internal.ExtendedArray.length array
val new_array = MLWorks.Internal.ExtendedArray.array(length, [] : (int * element)list)
val array_and_count =
(new_array, ref(max_size_ratio * length))
val new_set = ref array_and_count
in
MLWorks.Internal.ExtendedArray.iterate
(fn list =>
Lists.iterate (fn (_, element) => add_member'(new_set, element))
(list))
array;
set := array_and_count
end
end
;
