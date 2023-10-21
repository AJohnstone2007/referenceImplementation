require "../utils/__lists";
require "../utils/__crash";
require "../utils/__smallintset";
require "../utils/__text";
require "../utils/_diagnostic";
require "../machine/__machspec";
require "__mirtypes";
require "__mirregisters";
require "_registercolourer";
structure NonGCColourer_ =
RegisterColourer
(structure Lists = Lists_
structure Crash = Crash_
structure IntSet = SmallIntSet_
structure Register = MirTypes_.NonGC
structure Diagnostic = Diagnostic (structure Text = Text_)
structure MachSpec = MachSpec_
val instance_name = Text_.from_string "NonGC"
val allocation_order = #non_gc MirRegisters_.allocation_order
val allocation_equal = #non_gc MirRegisters_.allocation_equal
val preassigned = #non_gc MirRegisters_.preassigned
val corrupted_by_callee = #non_gc MirRegisters_.corrupted_by_callee
val available = #non_gc MirRegisters_.general_purpose
val debugging_available = #non_gc MirRegisters_.debugging_general_purpose
val temporaries = #non_gc MirRegisters_.temporary
val reserved_but_preferencable = #non_gc MirRegisters_.gp_for_preferencing
val debugger_reserved_but_preferencable = #non_gc MirRegisters_.debugging_gp_for_preferencing)
;
