require "types";
require "store";
signature FOREIGN_OBJECT =
sig
type 'a option = 'a option
structure FITypes : FOREIGN_TYPES
type bytearray = FITypes.bytearray
type address = FITypes.address
structure FIStore : FOREIGN_STORE
type store = FIStore.store
exception ReadOnly
exception WriteOnly
type ('l_type) object
datatype object_mode = LOCAL_OBJECT | REMOTE_OBJECT
datatype object_status = PERMANENT_OBJECT | TEMPORARY_OBJECT
exception OutOfBounds
exception Currency
val object : { lang_type : 'l_type,
size : int,
status : object_status,
mode : object_mode,
currency : bool,
store : store } -> ('l_type) object
val objectStatus : ('l_type) object -> object_status
val objectCurrency : ('l_type) object -> bool
val set_object_currency : ('l_type) object * bool -> unit
val objectMode : ('l_type) object -> object_mode
val set_object_mode : ('l_type) object * object_mode -> unit
val object_type : ('l_type) object -> 'l_type
val set_object_type : ('l_type) object * 'l_type -> unit
val objectSize : ('l_type) object -> int
val set_object_size : ('l_type) object * int -> unit
val set_object_size' : ('l_type) object * int -> unit
val object_value : ('l_type) object * bytearray * int -> unit
val object_value' : ('l_type) object * bytearray * int -> unit
val set_object_value : ('l_type) object * bytearray * int -> unit
val copy_object_value : { from : ('l_type) object,
to : ('l_type) object } -> unit
val copy_object_value' : { from : ('l_type) object,
to : ('l_type) object } -> unit
val new_object : ('l_type) object -> ('l_type) object
val dup_object : ('l_type) object -> ('l_type) object
val tmp_object : ('l_type) object -> ('l_type) object
val to_location : ('l_type) object * address -> int
val to_address : ('l_type) object * int -> address
val relative_location : ('l_type) object * address -> int
val relative_address : ('l_type) object * int -> address
val objectLocation : ('l_type) object -> int
val move_object : ('l_type) object * int -> unit
val offset_object : ('l_type) object * int -> unit
val move_object' : ('l_type) object * int -> unit
val offset_object' : ('l_type) object * int -> unit
val objectAddress : ('l_type) object -> address
val set_object_address : ('l_type) object * address -> unit
val set_object_address' : ('l_type) object * address -> unit
val examine_object : ('l_type) object * address -> unit
val object_info : ('l_type -> 'l_info) -> ('l_type) object ->
{ store : store,
status : string,
currency : string,
mode : string,
langtype : 'l_info,
size : int,
offset : int,
base : address option
}
val object_data : ('l_type)object -> int list
val object_data_hex : ('l_type)object -> string
val object_data_ascii : ('l_type)object -> string
end;
