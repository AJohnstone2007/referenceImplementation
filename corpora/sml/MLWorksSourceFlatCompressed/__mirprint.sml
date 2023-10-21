require "../utils/__lists";
require "../utils/__crash";
require "../basics/__identprint";
require "__mirregisters";
require "_mirprint";
structure MirPrint_ = MirPrint(
structure Lists = Lists_
structure Crash = Crash_
structure IdentPrint = IdentPrint_
structure MirRegisters = MirRegisters_
)
;
