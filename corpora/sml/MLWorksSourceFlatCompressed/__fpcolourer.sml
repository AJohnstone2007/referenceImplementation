require "../utils/__lists";
require "../utils/__crash";
require "../utils/__smallintset";
require "../utils/__text";
require "../utils/_diagnostic";
require "__mirtypes";
require "__mirregisters";
require "../machine/__machspec";
require "_registercolourer";
structure FPColourer_ =
RegisterColourer
(structure Lists = Lists_
structure Crash = Crash_
structure IntSet = SmallIntSet_
structure Register = MirTypes_.FP
structure Diagnostic = Diagnostic (structure Text = Text_)
structure MachSpec = MachSpec_
val instance_name = Text_.from_string "FP"
val allocation_order = #fp MirRegisters_.allocation_order
val allocation_equal = #fp MirRegisters_.allocation_equal
val preassigned = #fp MirRegisters_.preassigned
val corrupted_by_callee = #fp MirRegisters_.corrupted_by_callee
val available = #fp MirRegisters_.general_purpose
val debugging_available = #fp MirRegisters_.debugging_general_purpose
val temporaries = #fp MirRegisters_.temporary
val reserved_but_preferencable = #fp MirRegisters_.gp_for_preferencing
val debugger_reserved_but_preferencable = #fp MirRegisters_.debugging_gp_for_preferencing)
;
