require "../utils/__text";
require "../utils/_diagnostic";
require "__mirvariable";
require "__mirregisters";
require "__mirexpr";
require "__stackallocator";
require "_miroptimiser";
structure MirOptimiser_ = MirOptimiser(
structure MirVariable = MirVariable_
structure StackAllocator = StackAllocator_
structure MirRegisters = MirRegisters_
structure MirExpr = MirExpr_
structure Diagnostic = Diagnostic (structure Text = Text_)
)
;
