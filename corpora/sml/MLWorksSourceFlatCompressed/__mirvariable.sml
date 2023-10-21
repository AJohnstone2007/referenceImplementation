require "../utils/__lists";
require "../utils/__crash";
require "../utils/_diagnostic";
require "../utils/__text";
require "../utils/__inthashtable";
require "__mirprint";
require "__mirtables";
require "__mirregisters";
require "__registerallocator";
require "_mirvariable";
structure MirVariable_ = MirVariable(
structure Lists = Lists_
structure Crash = Crash_
structure Diagnostic = Diagnostic( structure Text = Text_ )
structure IntHashTable = IntHashTable_
structure MirPrint = MirPrint_
structure MirTables = MirTables_
structure MirRegisters = MirRegisters_
structure RegisterAllocator = RegisterAllocator_
)
;
