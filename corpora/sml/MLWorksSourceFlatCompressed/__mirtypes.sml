require "../basis/__int";
require "../utils/_counter";
require "../utils/__set";
require "../utils/__text";
require "../utils/__crash";
require "../basics/__ident";
require "../utils/__lists";
require "../utils/__intbtree";
require "../utils/_smallintset";
require "../utils/_intsetlist";
require "../debugger/__debugger_types";
require "_virtualregister";
require "_mirtypes";
local
fun text_prefix s =
let
val t = Text_.from_string s
in
fn i => Text_.concatenate (t, Text_.from_string (Int.toString i))
end
fun string_prefix s i = s ^ Int.toString i
structure GC_ =
VirtualRegister (structure Text = Text_
structure Map = IntBTree_
val int_to_text = text_prefix "GC"
val int_to_string = string_prefix "GC"
structure IntSet =
IntSetList (
structure Text = Text_
val int_to_text = int_to_text)
structure SmallIntSet =
SmallIntSet (structure Text = Text_
structure Crash = Crash_
structure Lists = Lists_
val int_to_text = int_to_text))
structure NonGC_ =
VirtualRegister (structure Text = Text_
structure Map = IntBTree_
val int_to_text = text_prefix "NonGC"
val int_to_string = string_prefix "NonGC"
structure IntSet =
IntSetList (
structure Text = Text_
val int_to_text = int_to_text)
structure SmallIntSet =
SmallIntSet (structure Text = Text_
structure Crash = Crash_
structure Lists = Lists_
val int_to_text = int_to_text))
structure FP_ =
VirtualRegister (structure Text = Text_
structure Map = IntBTree_
val int_to_text = text_prefix "FP"
val int_to_string = string_prefix "FP"
structure IntSet =
IntSetList (
structure Text = Text_
val int_to_text = int_to_text)
structure SmallIntSet =
SmallIntSet (structure Text = Text_
structure Crash = Crash_
structure Lists = Lists_
val int_to_text = int_to_text))
in
structure MirTypes_ = MirTypes(
structure GC = GC_
structure NonGC = NonGC_
structure FP = FP_
structure Map = IntBTree_
structure Counter = Counter ()
structure Set = Set_
structure Ident = Ident_
structure Text = Text_
structure Debugger_Types = Debugger_Types_
)
end
;
