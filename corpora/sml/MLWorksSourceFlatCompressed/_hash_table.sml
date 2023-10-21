require "hash_table";
require "key";
require "__prime";
require "$.basis.__array";
functor HashTable (Key: KEY) : HASH_TABLE =
struct
exception Full
type key = Key.key
datatype 'a item_pair = PAIR of key * 'a | EMPTY | DELETED
type 'a hash_table = 'a item_pair Array.array
fun create (size, element) =
(if Prime.testPrime (size, 5) then
()
else
print "Warning: table size should be a prime number.\n";
Array.array (size, EMPTY))
fun insert (table, newKey, newItem) =
let
val size = Array.length table
fun insert' (i, probes) =
if probes >= size then
raise Full
else
case Array.sub (table, i) of
EMPTY => Array.update (table, i, PAIR (newKey, newItem))
| DELETED => Array.update (table, i, PAIR (newKey, newItem))
| PAIR (_, _) => insert' (Key.rehash (newKey, i, size), probes + 1)
in
insert' (Key.hash (newKey, size), 0)
end
fun find (table, searchKey) =
let
val size = Array.length table
fun find' (i, probes) =
if probes >= size then
NONE
else
case Array.sub (table, i) of
EMPTY => NONE
| DELETED => find' (Key.rehash (searchKey, i, size), probes + 1)
| PAIR (itemKey, item) =>
if itemKey = searchKey then
SOME (item, i)
else
find' (Key.rehash (searchKey, i, size), probes + 1)
in
find' (Key.hash (searchKey, size), 0)
end
fun lookUp (table, searchKey) =
case find (table, searchKey) of
NONE => NONE
| SOME (item, i) => SOME item
fun remove (table, searchKey) =
case find (table, searchKey) of
NONE => NONE
| SOME (item, index) =>
(Array.update (table, index, DELETED);
SOME item)
end
;
