require "../utils/__btree";
require "../utils/__crash";
require "../utils/__set";
require "../utils/__lists";
require "../mir/__mirtypes";
require "__machtypes";
require "__sparc_assembly";
require "_sparc_schedule";
structure Sparc_Schedule_ = Sparc_Schedule(
structure NewMap = BTree_
structure Crash = Crash_
structure Set = Set_
structure Lists = Lists_
structure MirTypes = MirTypes_
structure MachTypes = MachTypes_
structure Sparc_Assembly = Sparc_Assembly_
)
;
