require "../utils/__set";
require "../utils/__crash";
require "../utils/__print";
require "__types";
require "__strnames";
require "__namesettypes";
require "__stamp";
require "_nameset";
structure Nameset_ = Nameset(
structure Set = Set_
structure Crash = Crash_
structure Print = Print_
structure Types = Types_
structure NamesetTypes = NamesetTypes_
structure Strnames = Strnames_
structure Stamp = Stamp_
);
