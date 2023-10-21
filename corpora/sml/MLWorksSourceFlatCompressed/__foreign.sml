require "^.basis.__word32";
require "^.basis.__word8";
require "__libml";
require "__store";
require "__aliens";
require "__object";
require "__types";
require "__c_function";
require "foreign";
structure ForeignInterface : FOREIGN_INTERFACE =
struct
type 'a option = 'a option
open ForeignTypes_
structure Store = ForeignStore_
structure Object = ForeignObject_
structure Aliens = ForeignAliens_
structure LibML = LibML_
structure C =
struct
structure Structure = CFunction_.CStructure
structure Type = CFunction_.CObject
structure Value = CFunction_.CObject
structure Signature = CFunction_.CSignature
structure Function = CFunction_
structure Diagnostic = CFunction_.CObject
end
structure Diagnostic = Store
end;
