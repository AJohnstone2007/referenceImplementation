require "c_object";
require "types";
signature C_SIGNATURE =
sig
structure CObject : C_OBJECT
type name = CObject.name
type filename
type c_type = CObject.c_type
datatype c_decl =
UNDEF_DECL
|
VAR_DECL of { name : name, ctype : c_type }
|
FUN_DECL of { name : name,
source : c_type list,
target : c_type }
|
TYPE_DECL of { name : name,
defn : c_type,
size : int }
|
CONST_DECL of { name : name, ctype : c_type }
type c_signature
val newSignature : unit -> c_signature
val lookupEntry : c_signature -> name -> c_decl
val defEntry : c_signature * c_decl -> unit
val removeEntry : c_signature * name -> unit
val showEntries : c_signature -> c_decl list
exception UnknownTypeName of string
val normaliseType : c_signature -> (c_type -> c_type)
val loadHeader : filename -> c_signature
end;
