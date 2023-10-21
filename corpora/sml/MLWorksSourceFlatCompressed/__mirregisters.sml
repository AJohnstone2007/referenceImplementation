require "../utils/__lists";
require "../utils/__crash";
require "../machine/__machspec";
require "__mirtypes";
require "_mirregisters";
structure MirRegisters_ = MirRegisters(
structure MirTypes = MirTypes_
structure MachSpec = MachSpec_
structure Lists = Lists_
structure Crash = Crash_
)
;
