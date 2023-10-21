require "../utils/text";
require "../utils/intset";
require "../utils/mutableintset";
require "../utils/intnewmap";
require "virtualregister";
functor VirtualRegister (
structure IntSet : INTSET
structure SmallIntSet : MUTABLEINTSET
structure Map : INTNEWMAP
structure Text : TEXT
val int_to_text : int -> Text.T
val int_to_string : int -> string
sharing Text = IntSet.Text = SmallIntSet.Text
) : VIRTUALREGISTER =
struct
structure Text = Text
structure Set = IntSet
structure Map = Map
structure Pack = SmallIntSet
type T = int
val source = ref 0
fun new () = (source := !source-1; !source)
fun reset () = source := 0
val order = op< : int * int -> bool
fun pack r = r
fun unpack r = r
fun pack_set set = Set.reduce Pack.add' (Pack.empty, set)
fun unpack_set pack = Pack.reduce Set.add (Set.empty, pack)
val to_string = int_to_string
val to_text = int_to_text
end
;
