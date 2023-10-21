require "../utils/__btree";
require "signature";
structure Signature_ : FOREIGN_SIGNATURE =
struct
structure Map = BTree_
type ('a,'b)Map = ('a,'b)Map.map
fun empty_map () = Map.empty' ((op<):string*string->bool)
val apply' = Map.apply'
val define' = Map.define'
val undefine = Map.undefine
val to_list_ordered = Map.to_list_ordered
type 'a option = 'a option
abstype ('entry) fSignature =
FCODEINFO of ((string,'entry)Map)ref
with
fun newSignature () =
FCODEINFO(ref(empty_map ()))
fun lookupEntry (FCODEINFO(ref(map)),nm) =
SOME(apply'(map,nm)) handle Undefined => NONE
fun defEntry (FCODEINFO(map_r),itm) =
map_r := define'(!map_r,itm)
fun removeEntry(FCODEINFO(map_r),nm) =
map_r := undefine(!map_r,nm)
fun showEntries (FCODEINFO(ref(map))) =
to_list_ordered(map)
end
end;
