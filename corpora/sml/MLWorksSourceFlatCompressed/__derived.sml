require "../utils/__lists";
require "../utils/_counter";
require "../utils/__crash";
require "../basics/__absyn";
require "../basics/__identprint";
require "../main/__info";
require "__parserenv";
require "_derived";
structure Derived_ = Derived(
structure Lists = Lists_
structure Counter = Counter()
structure Crash = Crash_
structure Absyn = Absyn_
structure IdentPrint= IdentPrint_
structure PE = ParserEnv_
structure Info = Info_
)
;
