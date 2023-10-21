require "../utils/__lists";
require "../utils/__crash";
require "../utils/__text";
require "../utils/__smallintset";
require "../utils/_diagnostic";
require "../machine/__machspec";
require "__mirtypes";
require "__mirregisters";
require "_registercolourer";
structure GCColourer_ =
RegisterColourer
(structure Lists = Lists_
structure IntSet = SmallIntSet_
structure Crash = Crash_
structure Register = MirTypes_.GC
structure Diagnostic = Diagnostic (structure Text = Text_)
structure MachSpec = MachSpec_
val instance_name = Text_.from_string "GC"
val allocation_order = #gc MirRegisters_.allocation_order
val allocation_equal = #gc MirRegisters_.allocation_equal
val preassigned = #gc MirRegisters_.preassigned
val corrupted_by_callee = #gc MirRegisters_.corrupted_by_callee
val available = #gc MirRegisters_.general_purpose
val debugging_available = #gc MirRegisters_.debugging_general_purpose
val temporaries = #gc MirRegisters_.temporary
val reserved_but_preferencable = #gc MirRegisters_.gp_for_preferencing
val debugger_reserved_but_preferencable = #gc MirRegisters_.debugging_gp_for_preferencing)
;
