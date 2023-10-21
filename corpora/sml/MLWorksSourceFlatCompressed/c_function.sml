require "types";
require "c_signature";
require "c_object";
require "c_structure";
signature C_FUNCTION =
sig
structure CSignature : C_SIGNATURE
structure CStructure : C_STRUCTURE
structure CObject : C_OBJECT
structure FITypes : FOREIGN_TYPES
type name = FITypes.name
type c_type = CObject.c_type
type c_structure = CStructure.c_structure
type c_signature = CSignature.c_signature
type c_object = CObject.c_object
type c_function
val defineForeignFun : (c_structure * c_signature) -> (name -> c_function)
val call : c_function -> (c_object list * c_object) -> unit
end;
