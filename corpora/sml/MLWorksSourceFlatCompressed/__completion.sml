require "../basics/__identprint";
require "../utils/__lists";
require "../utils/__hashtable";
require "__namehash";
require "__types";
require "__stamp";
require "_completion";
structure Completion_ = Completion(
structure Types = Types_
structure IdentPrint = IdentPrint_
structure HashTable = HashTable_
structure NameHash = NameHash_
structure Stamp = Stamp_
structure Lists = Lists_);
