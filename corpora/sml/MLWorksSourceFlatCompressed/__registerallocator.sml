require "../utils/__lists";
require "../utils/__text";
require "../utils/_diagnostic";
require "../utils/__crash";
require "__mirprint";
require "__mirprocedure";
require "__mirtables";
require "__mirregisters";
require "__gccolourer";
require "__nongccolourer";
require "__fpcolourer";
require "_registerallocator";
structure RegisterAllocator_ =
RegisterAllocator
(structure MirProcedure = MirProcedure_
structure MirRegisters = MirRegisters_
structure Crash = Crash_
structure Diagnostic =
Diagnostic (structure Text = Text_)
structure MirPrint = MirPrint_
structure MirTables = MirTables_
structure Lists = Lists_
structure GCColourer = GCColourer_
structure NonGCColourer = NonGCColourer_
structure FPColourer = FPColourer_)
;
