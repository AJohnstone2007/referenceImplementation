require "../basis/__int";
require "inthashtable";
require "lists";
functor IntHashTable(structure Lists : LISTS
) : INTHASHTABLE =
struct
structure Bits = MLWorks.Internal.Bits
type '_a T = ((int * '_a option) list MLWorks.Internal.Array.array * int ref * int) ref
val array_sub = MLWorks.Internal.Value.unsafe_array_sub
val array_update = MLWorks.Internal.Value.unsafe_array_update
val print_debug = false
val expansion_ratio = 2
val max_size_ratio = 4
exception Lookup
local
fun nearest_power_of_two (size,x:int) =
if x >= size
then x
else nearest_power_of_two (size,x+x)
in
fun new size =
let
val initial_size = nearest_power_of_two(size,1)
in
ref(MLWorks.Internal.Array.array(initial_size, []),
ref(max_size_ratio * initial_size),
initial_size - 1)
end
end
fun lookup(ref (elt_array,_,mask), key:int) =
let
fun assoc [] = raise Lookup
| assoc((k, v) :: xs) =
if key = k then
(case v of
SOME x => x
| _ => raise Lookup)
else assoc xs
val list_map = array_sub (elt_array, Bits.andb(key,mask))
in
assoc list_map
end
fun tryLookup(ref (elt_array,_,mask),key:int) =
let
fun assoc_default (key,[]) = NONE
| assoc_default (key,(k, v) :: xs) =
if key = k
then v
else assoc_default (key,xs)
val list_map = array_sub(elt_array, Bits.andb(key,mask))
in
assoc_default(key,list_map)
end
fun member (_, []) = false
| member (key:int, (k, _) :: rest) =
key = k orelse member (key,rest)
fun is_defined (ref (elt_array,_,mask),key) =
let
val list_map = array_sub(elt_array, Bits.andb(key,mask))
in
member (key,list_map)
end
fun lookup_default (table,value,key) =
case tryLookup (table,key) of
SOME res => res
| _ => value
fun iterate_hash f (arr,_,_) =
let
val len = MLWorks.Internal.Array.length arr
fun down i =
if i = len
then ()
else
(Lists.iterate
(fn (k, SOME v) => f(k,v) | _ => ())
(array_sub (arr, i));
down (i+1))
in
down 0
end
fun iterate f (ref h) = iterate_hash f h
fun fold f (res, ref (arr,_,_)) =
let
val len = MLWorks.Internal.Array.length arr
fun do_bucket (res,((k,SOME v)::rest)) =
do_bucket (f (res,k,v),rest)
| do_bucket (res, ((k,NONE)::rest)) =
do_bucket (res,rest)
| do_bucket (res,[]) = res
fun down (res, i) =
if i = len
then res
else down (do_bucket (res, array_sub(arr,i)),i+1)
in
down (res, 0)
end
val makestring = Int.toString
fun assoc_default' (_, []) = NONE
| assoc_default' (key:int, (k, vref) :: xs) =
if key = k
then SOME vref
else assoc_default' (key, xs)
exception Crash of string
fun crash s = raise Crash s
fun replace (k:int,value,[],acc) = crash "empty list in replace"
| replace (k:int,value,(entry as (k',_))::rest,acc) =
if k = k'
then (k,SOME value) :: acc @ rest
else
replace (k,value,rest,entry::acc)
fun update(initial as ref (elt_array,count_down as ref c,old_mask), key, value) =
let
val pos = Bits.andb(key,old_mask)
val list_map = array_sub(elt_array, pos)
in
(if member (key,list_map)
then MLWorks.Internal.Array.update (elt_array,pos,replace (key,value,list_map,[]))
else
(if c <= 0
then
let
val old_size = MLWorks.Internal.Array.length elt_array
val _ = if print_debug then
print("Resizing int table: " ^ makestring(old_size) ^ "...\n")
else ()
val new_size = old_size * expansion_ratio
val new_arr = MLWorks.Internal.Array.array(new_size, [])
val new_mask = new_size - 1
val _ = initial := (new_arr,ref(max_size_ratio * (new_size - old_size) + c - 1),new_mask)
fun partition([],size,f,s) = (f,s)
| partition((elem as (key,_))::rest,size,f,s) =
if Bits.andb(key,size) = 0
then partition(rest,size,elem::f,s)
else partition(rest,size,f,elem::s)
fun deal_with_elements index =
if index < 0
then ()
else
let
val elems = array_sub(elt_array,index)
in
case elems of
[] => deal_with_elements (index-1)
| _ =>
let
val (f,s) =
partition(elems,old_size,[],[])
in
array_update(new_arr,index,f);
array_update(new_arr,index + old_size,s);
deal_with_elements (index-1)
end
end
in
deal_with_elements (old_size-1);
let
val new_pos = Bits.andb(key,new_mask)
val old_value = array_sub(new_arr, new_pos)
in
array_update(new_arr, new_pos, (key,SOME value)::old_value)
end
end
else (count_down := c - 1;
array_update(elt_array, pos, (key, SOME value) :: list_map))))
end
fun delete_from_list (key, [], acc) = (acc, false)
| delete_from_list (key:int, (elt as (k, _)) :: rest, acc) =
if key = k then
(acc @ rest, true)
else
delete_from_list(key, rest, elt :: acc)
fun delete(ref (elt_array,count_down,mask), key) =
let
val pos = Bits.andb(key,mask)
val list_map = array_sub(elt_array, pos)
val (list_map, found) = delete_from_list(key, list_map, [])
val _ = count_down := !count_down + 1
in
array_update(elt_array, pos, list_map)
end
fun check_nil(array, curr, max) =
if curr >= max then true
else
case array_sub(array, curr) of
[] => check_nil(array, curr + 1, max)
| _ => false
fun empty_setp(ref (elt_array,_,_)) =
check_nil(elt_array, 0, MLWorks.Internal.Array.length elt_array)
fun add_members(pos:int, max, mems, elts) =
if pos >= max then
mems
else
let
val set = map (fn (x,SOME y) => (x,y) | _ => crash "add_members") (array_sub(elts, pos))
in
add_members(pos+1, max, set @ mems, elts)
end
fun to_list(ref (elt_array,_,_)) =
add_members(0, MLWorks.Internal.Array.length elt_array, [], elt_array)
fun copy_array(old_array, new_array, max, curr) =
if curr >= max then new_array
else
let
val this = array_sub(old_array, curr)
val _ = array_update(new_array, curr, this)
in
copy_array(old_array, new_array, max, curr+1)
end
fun copy(ref (elt_array,count_down,mask)) =
let
val len = MLWorks.Internal.Array.length elt_array
val new_array = MLWorks.Internal.Array.array(len, [])
val _ = copy_array(elt_array, new_array, len, 0)
in
ref (new_array,ref(!count_down),mask)
end
fun map f (ref(table as (elt_array,count_down,_))) =
let
val new_table = new(MLWorks.Internal.Array.length elt_array)
in
iterate_hash (fn (x,y) => update(new_table,x,f (x,y))) table;
new_table
end
fun stats(ref (arr,_,_)) =
let
val len = MLWorks.Internal.Array.length arr
val largest = ref 0
val smallest = ref(if len >0
then Lists.length(array_sub(arr,0))
else 0)
val count = ref(0)
fun walk_buckets x =
if x<0
then ()
else
let
val length = Lists.length(array_sub(arr,x))
val _ = if length > !largest
then largest := length
else ()
val _ = if length < !smallest
then smallest := length
else ()
in
(count := !count + length;
walk_buckets(x-1))
end
val _ = walk_buckets (len - 1)
in
{size = len,
smallest = !smallest,
largest = ! largest,
count = ! count}
end
fun print_stats table =
let
val {size : int ,count : int ,smallest : int ,largest : int} = stats table
val string =
if count>0
then
("statistics:\n"^
" size = "^(makestring size)^"\n"^
" count = "^(makestring count)^"\n"^
" smallest = " ^(makestring smallest)^"\n"^
" largest = "^(makestring largest)^"\n")
else " EMPTY\n"
in
print string
end
end
;
