require "../utils/__inthashtable";
require "../utils/__text";
require "../utils/_diagnostic";
require "../utils/__crash";
require "../utils/__lists";
require "__mirtables";
require "__mirregisters";
require "__mirprint";
require "_registerpack";
structure RegisterPack_ =
RegisterPack (structure Diagnostic = Diagnostic (structure Text = Text_)
structure Crash = Crash_
structure Lists = Lists_
structure IntHashTable = IntHashTable_
structure MirTables = MirTables_
structure MirRegisters = MirRegisters_
structure MirPrint = MirPrint_
val full_analysis_threshold = 500)
;
