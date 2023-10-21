require "types";
signature FOREIGN_STORE =
sig
structure FITypes : FOREIGN_TYPES
type bytearray = FITypes.bytearray
type address = FITypes.address
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
val store_origin : store -> address
val storeAlloc : store -> alloc_policy
val storeOverflow : store -> overflow_policy
val store_content : store -> bytearray
exception ExpandStore
val isStandardStore : store -> bool
val isEphemeralStore : store -> bool
val expand : (store * int) -> unit
val expand_managed : (store * int) -> unit
val fresh_object_offset : (store * int) -> int
val adjust_store : (store * int * int) -> unit
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
end;
