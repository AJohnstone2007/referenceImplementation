require "object";
signature C_OBJECT =
sig
type 'a option = 'a option
type bytearray
type name
type address
type word32
type store
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
val lookup_variant : (c_variant list * name) -> c_variant
val sort_variant_list : c_variant list -> c_variant list
val equalType : c_type * c_type -> bool
val structType : string * (string * c_type) list -> c_type
val unionType : string * (string * c_type) list -> c_type
val ptrType : c_type -> c_type
val typeName : string -> c_type
val enumType : string * string list -> c_type
exception ForeignType
exception StoreAccess
exception OutOfBounds
exception Currency
exception ReadOnly
exception WriteOnly
type c_object
val object : { ctype:c_type, store:store } -> c_object
datatype object_mode = LOCAL_OBJECT | REMOTE_OBJECT
val object_mode : c_object -> object_mode
val setObjectMode : c_object * object_mode -> unit
val objectType : c_object -> c_type
val castObjectType : c_object * c_type -> unit
val newObject : c_object -> c_object
val dupObject : c_object -> c_object
val tmpObject : c_object -> c_object
eqtype c_char
eqtype c_short_int
eqtype c_int
eqtype c_long_int
type c_real
type c_double
type c_long_double
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
val indexObjectLocation : c_object * int -> int
val derefObjectLocation : c_object -> int
val selectObjectLocation : c_object * name -> int
val coerceObjectLocation : c_object -> int
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
val examine_object : c_object * address -> unit
val object_address : c_object -> address
val object_value : c_object * bytearray * int -> unit
val set_object_value : c_object * bytearray * int -> unit
end;
