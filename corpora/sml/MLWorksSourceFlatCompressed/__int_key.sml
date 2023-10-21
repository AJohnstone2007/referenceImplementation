require "key";
structure IntKey : KEY =
struct
type key = int
fun hash (hashKey : key, tableSize) = hashKey mod tableSize
fun rehash (hashKey : key, index, tableSize) =
(index + 8 - (hashKey mod 8)) mod tableSize
end
;
