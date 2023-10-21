require "^.basis.__word32";
require "^.basis.__word8";
signature FOREIGN_INTERFACE =
sig
type 'a option = 'a option
type word32 = Word32.word
type address = word32
type bytearray = MLWorks.Internal.ByteArray.bytearray
type name = string
type filename = string
structure Store :
sig
type store
exception ReadOnly
exception WriteOnly
datatype store_status = LOCKED_STATUS | RD_STATUS | WR_STATUS | RDWR_STATUS
val storeStatus : store -> store_status
val setStoreStatus : (store * store_status) -> unit
datatype alloc_policy = ORIGIN | SUCC | ALIGNED_4 | ALIGNED_8
datatype overflow_policy = BREAK | EXTEND | RECYCLE
val store : { alloc : alloc_policy,
overflow : overflow_policy,
status : store_status,
size : int } -> store
val storeSize : store -> int
val storeAlloc : store -> alloc_policy
val storeOverflow : store -> overflow_policy
exception ExpandStore
val isStandardStore : store -> bool
val isEphemeralStore : store -> bool
val expand : (store * int) -> unit
end
structure Object :
sig
type ('a) object
datatype object_mode = LOCAL_OBJECT | REMOTE_OBJECT
datatype object_status = PERMANENT_OBJECT | TEMPORARY_OBJECT
exception OutOfBounds
exception ReadOnly
exception WriteOnly
exception Currency
val objectStatus : ('l_type) object -> object_status
val objectCurrency : ('l_type) object -> bool
val objectMode : ('l_type) object -> object_mode
val objectSize : ('l_type) object -> int
val objectLocation : ('l_type) object -> int
val objectAddress : ('l_type) object -> address
end;
structure Aliens :
sig
val ensureAliens : unit -> unit
val resetAliens : unit -> unit
val refreshAliens : unit -> unit
end
structure LibML :
sig
val registerExternalValue : string * 'a -> unit
val deleteExternalValue : string -> unit
val externalValues : unit -> string list
val clearExternalValues : unit -> unit
end
structure C :
sig
structure Structure :
sig
type c_structure
datatype load_mode = IMMEDIATE_LOAD | DEFERRED_LOAD
val loadObjectFile : filename * load_mode -> c_structure
val fileInfo : c_structure -> (filename * load_mode)
val filesLoaded : unit -> filename list
val symbols : c_structure -> name list
datatype value_type = CODE_VALUE | VAR_VALUE | UNKNOWN_VALUE
val symbolInfo : c_structure * name -> value_type
end
structure Type :
sig
datatype pointer_kind = LOCAL_PTR | RELATIVE_PTR | REMOTE_PTR
datatype c_type =
VOID_TYPE
|
CHAR_TYPE | UNSIGNED_CHAR_TYPE | SIGNED_CHAR_TYPE
|
SHORT_TYPE | INT_TYPE | LONG_TYPE
|
UNSIGNED_SHORT_TYPE | UNSIGNED_INT_TYPE | UNSIGNED_LONG_TYPE
|
FLOAT_TYPE | DOUBLE_TYPE | LONG_DOUBLE_TYPE
|
STRING_TYPE of { length : int }
|
TYPENAME of { name : name,
size : int option }
|
FUNCTION_TYPE of { source : c_type list,
target : c_type
}
|
POINTER_TYPE of { ctype : c_type, mode : pointer_kind }
|
STRUCT_TYPE of { tag : name option,
fields : c_field list,
size : int option }
|
UNION_TYPE of { tag : name option,
variants : c_variant list,
size : int option,
current : c_variant }
|
ARRAY_TYPE of { length : int, ctype : c_type, size : int option }
|
ENUM_TYPE of { tag : name option,
elems : name list,
card : int }
and c_variant = VARIANT of { name : name,
ctype : c_type,
size : int option }
and c_field = FIELD of { name : name,
ctype : c_type,
size : int option,
padding : int,
offset : int option }
exception UnknownTypeName of string
val sizeOf : c_type -> int
val equalType : c_type * c_type -> bool
val structType : string * (string * c_type) list -> c_type
val unionType : string * (string * c_type) list -> c_type
val ptrType : c_type -> c_type
val typeName : string -> c_type
val enumType : string * string list -> c_type
end
structure Value :
sig
type store = Store.store
type object_mode = Object.object_mode
type c_type = Type.c_type
type c_object
val object : { ctype : c_type,
store : store } -> c_object
val setObjectMode : c_object * object_mode -> unit
val objectType : c_object -> c_type
val castObjectType : c_object * c_type -> unit
val newObject : c_object -> c_object
val dupObject : c_object -> c_object
val tmpObject : c_object -> c_object
type c_char
type c_short_int
type c_int
type c_long_int
type c_real
type c_double
type c_long_double
exception ForeignType
exception StoreAccess
val setChar : c_object * c_char -> unit
val setUnsignedChar : c_object * c_char -> unit
val setSignedChar : c_object * c_char -> unit
val setShort : c_object * c_short_int -> unit
val setInt : c_object * c_int -> unit
val setLong : c_object * c_long_int -> unit
val setUnsignedShort : c_object * c_short_int -> unit
val setUnsigned : c_object * c_int -> unit
val setUnsignedLong : c_object * c_long_int -> unit
val setWord32 : c_object * word32 -> unit
val setFloat : c_object * c_real -> unit
val setDouble : c_object * c_double -> unit
val setLongDouble : c_object * c_long_double -> unit
val setString : c_object * string -> unit
val setAddr : { obj:c_object, addr:c_object } -> unit
val setPtrAddr : { ptr:c_object, addr:c_object } -> unit
val setPtrAddrOf : { ptr:c_object, data:c_object } -> unit
val setPtrData : { ptr:c_object, data:c_object } -> unit
val setPtrType : { ptr:c_object, data:c_object } -> unit
val castPtrType : { ptr:c_object, ctype:c_type } -> unit
val setLocalPtr : c_object -> unit
val setRelativePtr : c_object -> unit
val setRemotePtr : c_object -> unit
val isEqPtr : c_object * c_object -> bool
val isNullPtr : c_object -> bool
val setStruct : c_object * (c_object list) -> unit
val setField : { record:c_object, field:name, data:c_object } -> unit
val setMember : { union:c_object, member:name } -> unit
val setUnion : { union:c_object, data:c_object } -> unit
val setArray : c_object * (c_object list) * int -> unit
val setEnum : c_object * int -> unit
val indexObject : { array:c_object, tgt:c_object, index:int } -> unit
val derefObject : { ptr:c_object, tgt:c_object } -> unit
val selectObject : { record:c_object, tgt:c_object, field:name } -> unit
val coerceObject : { union:c_object, tgt:c_object } -> unit
val copyIndexObject : c_object * int -> c_object
val copyDerefObject : c_object -> c_object
val copySelectObject : c_object * name -> c_object
val copyCoerceObject : c_object -> c_object
val indexObjectType : c_object -> c_type
val derefObjectType : c_object -> c_type
val selectObjectType : c_object * name -> c_type
val coerceObjectType : c_object -> c_type
val indexObjectSize : c_object -> int
val derefObjectSize : c_object -> int
val selectObjectSize : c_object * name -> int
val coerceObjectSize : c_object -> int
val nextArrayItem : c_object -> unit
val prevArrayItem : c_object -> unit
val getChar : c_object -> c_char
val getUnsignedChar : c_object -> c_char
val getSignedChar : c_object -> c_char
val getShort : c_object -> c_short_int
val getInt : c_object -> c_int
val getLong : c_object -> c_long_int
val getUnsignedShort : c_object -> c_short_int
val getUnsigned : c_object -> c_int
val getUnsignedLong : c_object -> c_long_int
val getWord32 : c_object -> word32
val getFloat : c_object -> c_real
val getDouble : c_object -> c_double
val getLongDouble : c_object -> c_long_double
val getString : c_object -> string
val getData : c_object -> c_object
val getStruct : c_object -> c_object list
val getField : c_object * name -> c_object
val getUnion : c_object -> c_object
val getArray : c_object -> c_object list
val getEnum : c_object -> int
end
structure Signature :
sig
type c_type = Type.c_type
type c_signature
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
val newSignature : unit -> c_signature
val lookupEntry : c_signature -> name -> c_decl
val defEntry : c_signature * c_decl -> unit
val removeEntry : c_signature * name -> unit
val showEntries : c_signature -> c_decl list
val normaliseType : c_signature -> c_type -> c_type
val loadHeader : filename -> c_signature
end
structure Function :
sig
type c_type = Type.c_type
type c_object = Value.c_object
type c_structure = Structure.c_structure
type c_signature = Signature.c_signature
type c_function
val defineForeignFun : (c_structure * c_signature) -> name -> c_function
val call : c_function -> (c_object list * c_object) -> unit
end
structure Diagnostic :
sig
type store = Store.store
type c_type = Type.c_type
type c_object = Value.c_object
val cTypeInfo : c_type -> string
val viewObject : c_object -> string
val dispObject : c_object -> c_object
val objectInfo : c_object ->
{ store : store,
status : string,
currency : string,
mode : string,
langtype : string,
size : int,
base : address option,
offset : int
}
val objectData : c_object -> int list
val objectDataHex : c_object -> string
val objectDataAscii : c_object -> string
end
end
structure Diagnostic :
sig
type store = Store.store
val viewStore : store -> string
val dispStore : store -> store
val storeInfo : store ->
{ kind : string,
origin : address,
status : string,
alloc : string,
overflow : string,
size : int,
top : int,
free : int }
val storeData :
{ store : store,
start : int,
length : int } -> int list
val storeDataHex :
{ store : store,
start : int,
length : int } -> string
val storeDataAscii :
{ store : store,
start : int,
length : int } -> string
val diffAddr : address -> address -> int
val incrAddr : address * int -> address
end
end;
