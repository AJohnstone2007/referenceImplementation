functor HashTable (Key : HASH_KEY) : HASH_TABLE =
struct
structure Key = Key
open Key
datatype 'a bucket_t
= NIL
| B of (int * hash_key * 'a * 'a bucket_t)
datatype 'a hash_table = HT of {
not_found : exn,
table : 'a bucket_t array ref,
n_items : int ref
}
fun index (i, sz) = Bits.andb(i, sz-1)
fun roundUp n = let
fun f i = if (i >= n) then i else f (Bits.lshift (i, 1))
in
f 32
end
fun mkTable (sizeHint, notFound) = HT{
not_found = notFound,
table = ref (Array.array(roundUp sizeHint, NIL)),
n_items = ref 0
}
fun growTable (HT{table, n_items, ...}) = let
val arr = !table
val sz = Array.length arr
in
if (!n_items >= sz)
then let
val newSz = sz+sz
val newArr = Array.array (newSz, NIL)
fun copy NIL = ()
| copy (B(h, key, v, rest)) = let
val indx = index (h, newSz)
in
Array.update (newArr, indx,
B(h, key, v, Array.sub(newArr, indx)));
copy rest
end
fun bucket n = (copy (Array.sub(arr, n)); bucket (n+1))
in
(bucket 0) handle _ => ();
table := newArr
end
else ()
end
fun insert (tbl as HT{table, n_items, ...}) (key, item) = let
val arr = !table
val sz = Array.length arr
val hash = hashVal key
val indx = index (hash, sz)
fun look NIL = (
Array.update(arr, indx, B(hash, key, item, Array.sub(arr, indx)));
n_items := !n_items + 1;
growTable tbl;
NIL)
| look (B(h, k, v, r)) = if ((hash = h) andalso sameKey(key, k))
then B(hash, key, item, r)
else (case (look r)
of NIL => NIL
| rest => B(h, k, v, rest)
)
in
case (look (Array.sub (arr, indx)))
of NIL => ()
| b => Array.update(arr, indx, b)
end
fun find (HT{table, not_found, ...}) key = let
val arr = !table
val sz = Array.length arr
val hash = hashVal key
val indx = index (hash, sz)
fun look NIL = raise not_found
| look (B(h, k, v, r)) = if ((hash = h) andalso sameKey(key, k))
then v
else look r
in
look (Array.sub (arr, indx))
end
fun peek (HT{table, ...}) key = let
val arr = !table
val sz = Array.length arr
val hash = hashVal key
val indx = index (hash, sz)
fun look NIL = NONE
| look (B(h, k, v, r)) = if ((hash = h) andalso sameKey(key, k))
then SOME v
else look r
in
look (Array.sub (arr, indx))
end
fun remove (HT{not_found, table, n_items}) key = let
val arr = !table
val sz = Array.length arr
val hash = hashVal key
val indx = index (hash, sz)
fun look NIL = raise not_found
| look (B(h, k, v, r)) = if ((hash = h) andalso sameKey(key, k))
then (v, r)
else let val (item, r') = look r in (item, B(h, k, v, r')) end
val (item, bucket) = look (Array.sub (arr, indx))
in
Array.update (arr, indx, bucket);
n_items := !n_items - 1;
item
end
fun numItems (HT{n_items, ...}) = !n_items
fun listItems (HT{table = ref arr, n_items, ...}) = let
fun f (_, l, 0) = l
| f (~1, l, _) = l
| f (i, l, n) = let
fun g (NIL, l, n) = f (i-1, l, n)
| g (B(_, k, v, r), l, n) = g(r, (k, v)::l, n-1)
in
g (Array.sub(arr, i), l, n)
end
in
f ((Array.length arr) - 1, [], !n_items)
end
fun apply f (HT{table, ...}) = let
fun appF NIL = ()
| appF (B(_, key, item, rest)) = (
ignore(f (key, item));
appF rest)
val arr = !table
val sz = Array.length arr
fun appToTbl i = if (i < sz)
then (appF (Array.sub (arr, i)); appToTbl(i+1))
else ()
in
appToTbl 0
end
fun map f (HT{table, n_items, not_found}) = let
fun mapF NIL = NIL
| mapF (B(hash, key, item, rest)) =
B(hash, key, f (key, item), mapF rest)
val arr = !table
val sz = Array.length arr
val newArr = Array.array (sz, NIL)
fun mapTbl i = if (i < sz)
then (
Array.update(newArr, i, mapF (Array.sub(arr, i)));
mapTbl (i+1))
else ()
in
mapTbl 0;
HT{table = ref newArr, n_items = ref(!n_items), not_found = not_found}
end
fun filter pred (HT{table, n_items, not_found}) = let
fun filterP NIL = NIL
| filterP (B(hash, key, item, rest)) = if (pred(key, item))
then B(hash, key, item, filterP rest)
else filterP rest
val arr = !table
val sz = Array.length arr
fun filterTbl i = if (i < sz)
then (
Array.update (arr, i, filterP (Array.sub (arr, i)));
filterTbl (i+1))
else ()
in
filterTbl 0
end
fun transform f (HT{table, n_items, not_found}) = let
fun mapF NIL = NIL
| mapF (B(hash, key, item, rest)) = B(hash, key, f item, mapF rest)
val arr = !table
val sz = Array.length arr
val newArr = Array.array (sz, NIL)
fun mapTbl i = if (i < sz)
then (
Array.update(newArr, i, mapF (Array.sub(arr, i)));
mapTbl (i+1))
else ()
in
mapTbl 0;
HT{table = ref newArr, n_items = ref(!n_items), not_found = not_found}
end
fun copy (HT{table, n_items, not_found}) = let
val arr = !table
val sz = Array.length arr
val newArr = Array.array (sz, NIL)
fun mapTbl i = (
Array.update (newArr, i, Array.sub(arr, i));
mapTbl (i+1))
in
(mapTbl 0) handle _ => ();
HT{table = ref newArr, n_items = ref(!n_items), not_found = not_found}
end
fun bucketSizes (HT{table = ref arr, ...}) = let
fun len (NIL, n) = n
| len (B(_, _, _, r), n) = len(r, n+1)
fun f (~1, l) = l
| f (i, l) = f (i-1, len (Array.sub (arr, i), 0) :: l)
in
f ((Array.length arr)-1, [])
end
end
;
