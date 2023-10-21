require "../utils/_diagnostic";
require "../utils/__text";
require "../utils/__lists";
require "../utils/__crash";
require "__mirprocedure";
require "__mirprint";
require "_stackallocator";
structure StackAllocator_ = StackAllocator (
structure MirPrint = MirPrint_
structure Crash = Crash_
structure Lists = Lists_
structure MirProcedure = MirProcedure_
structure Diagnostic = Diagnostic ( structure Text = Text_ )
)
;
