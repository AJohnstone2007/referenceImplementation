require "../utils/__text";
require "../utils/__lists";
require "../utils/__crash";
require "../utils/_diagnostic";
require "__mirprint";
require "__mirtables";
require "__registerpack";
require "_mirprocedure";
structure MirProcedure_ = MirProcedure (
structure Diagnostic =
Diagnostic (structure Text = Text_)
structure MirTables = MirTables_
structure MirPrint = MirPrint_
structure Lists = Lists_
structure Crash = Crash_
structure RegisterPack = RegisterPack_
)
;
