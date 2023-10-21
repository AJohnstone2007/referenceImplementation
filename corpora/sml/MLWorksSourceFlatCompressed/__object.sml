require "^.basis.__word32";
require "^.basis.__int";
require "types";
require "store";
require "utils";
require "__types";
require "__store";
require "__utils";
require "object";
structure ForeignObject_ : FOREIGN_OBJECT =
struct
structure FIStore : FOREIGN_STORE = ForeignStore_
structure FIUtils : FOREIGN_UTILS = ForeignUtils_
structure FITypes : FOREIGN_TYPES = ForeignTypes_
open FIUtils
open FITypes
open FIStore
structure ByteArray = MLWorks.Internal.ByteArray
val bytearray = ByteArray.array
val copy_ba' = ByteArray.copy
val copy_ba =
fn (src,src_st,len,tgt,tgt_st) =>
copy_ba'(src,src_st,src_st+len,tgt,tgt_st)
val length_ba = ByteArray.length
val to_list = ByteArray.to_list
val word32_to_hex = FIUtils.word32_to_hex
val bytearray_to_hex = FIUtils.bytearray_to_hex
val bytearray_to_string = FIUtils.bytearray_to_string
val peek_memory = FIUtils.peek_memory
val addrToInt = Word32.toIntX
val intToAddr = Word32.fromInt
val pl32 = Word32.+
val mi32 = Word32.-
infix 6 pl32 mi32
val null_addr = intToAddr(0)
fun new(ref(v)) = ref(v)
fun addr_plus(a,i) = a pl32 (intToAddr i)
fun addr_minus(a1,a2) = addrToInt(a1 mi32 a2)
exception OutOfBounds
exception Currency
datatype object_mode = LOCAL_OBJECT | REMOTE_OBJECT
datatype object_status = PERMANENT_OBJECT | TEMPORARY_OBJECT
abstype ('l_type) object =
OBJECT of {
store : store,
status : object_status,
currency : bool ref,
mode : object_mode ref,
ltype : 'l_type ref,
size : int ref,
base : address box,
offset : int ref
}
with
fun object{lang_type,size=object_size,status,mode,currency,store} =
let val new_offset = fresh_object_offset(store,object_size)
in
OBJECT {
store = store,
status = status,
currency = ref(currency),
mode = ref(mode),
ltype = ref(lang_type),
size = ref(object_size),
base = voidBox(),
offset = ref(new_offset)
}
end
fun object_store(OBJECT{store, ...}) = store
fun objectStatus(OBJECT{status, ...}) = status
fun objectLocation(OBJECT{offset, ...}) = !offset
fun objectMode(OBJECT{mode, ...}) = !mode
fun objectCurrency(OBJECT{currency, ...}) = !currency
fun set_object_currency(OBJECT{currency, ...},flag) = (currency := flag)
fun object_type(OBJECT{ltype, ...}) = !ltype
fun set_object_type(OBJECT{ltype, ...},new_ltype) = (ltype := new_ltype)
fun objectSize(OBJECT{size, ...}) = !size
fun set_object_size'(OBJECT{size, ...},new_size) = (size := new_size)
fun set_object_size(obj as OBJECT{size, ...},new_size) =
( set_object_size'(obj,new_size);
if (!size <> new_size) then set_object_currency(obj, false) else ()
)
fun object_base(OBJECT{base, ...}) = getBox(base)
fun set_object_base(OBJECT{base, ...},addr) = setBox(base)(addr)
local
fun object_addr'(store,offset) =
let val origin = store_origin(store)
in
addr_plus(origin,offset)
end
fun update_object_base(OBJECT{store,offset,base, ...})=
let val addr = object_addr'(store,!offset)
in
setBox(base)(addr)
end
in
fun to_location (obj,addr) =
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
val origin = store_origin(store)
val store_sz = storeSize(store)
val new_locn = addr_minus(addr,origin)
in
if (0 <= new_locn) andalso (new_locn < store_sz)
then new_locn
else raise OutOfBounds
end
| REMOTE_OBJECT => raise OutOfBounds
fun to_address (obj,locn) =
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
val store_sz = storeSize(store)
in
if (0 <= locn) andalso (locn < store_sz)
then let val origin = store_origin(store)
in
addr_plus(origin,locn)
end
else raise OutOfBounds
end
| REMOTE_OBJECT => raise OutOfBounds
fun objectAddress(OBJECT{ base=(ref(SOME(addr))), mode, offset, store, ...} ) =
(
case !mode of
LOCAL_OBJECT =>
if (addr = null_addr)
then object_addr'(store,!offset)
else addr
| REMOTE_OBJECT => addr
)
| objectAddress(OBJECT{ mode=ref(LOCAL_OBJECT), base, store, offset, ...}) =
let val addr = object_addr'(store,!offset)
val new_base = if isStandardStore(store) then addr else null_addr
in
setBox(base)(new_base);
addr
end
| objectAddress(obj) =
( update_object_base(obj);
object_base(obj)
)
fun relative_location(obj,addr) =
let val object_addr = objectAddress(obj)
in
addr_minus(addr,object_addr)
end
fun relative_address(obj,idx) =
let val object_addr = objectAddress(obj)
in
addr_plus(object_addr,idx)
end
fun set_object_mode(obj as OBJECT{mode, base, ...},new_mode) =
(
mode := new_mode;
case new_mode of
LOCAL_OBJECT => resetBox(base)
| REMOTE_OBJECT => update_object_base(obj)
)
end
fun object_value(obj, barr, offset) =
if not(objectCurrency(obj)) then raise Currency else
let val size = objectSize(obj)
in
if (length_ba(barr) < offset + size) then raise OutOfBounds else
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
in
case storeStatus(store) of
WR_STATUS => raise WriteOnly
|
LOCKED_STATUS => raise WriteOnly
|
_ => let val src_ba = store_content(store)
in
copy_ba(src_ba,objectLocation(obj),size,barr,offset)
end
end
| REMOTE_OBJECT =>
peek_memory
{ loc=objectAddress(obj),
arr=barr,
start=offset,
len=size
}
end handle Option => raise OutOfBounds
fun object_value'(obj, barr, offset) =
let val size = objectSize(obj)
in
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
val src_ba = store_content(store)
in
copy_ba(src_ba,objectLocation(obj),size,barr,offset)
end
| REMOTE_OBJECT =>
peek_memory
{ loc=objectAddress(obj),
arr=barr,
start=offset,
len=size
}
end
local
fun examine_object'(obj,addr) =
( set_object_currency(obj,true);
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
val barr = store_content(store)
val offset = objectLocation(obj)
val size = objectSize(obj)
in
peek_memory{ loc = addr,
arr = barr,
start = offset,
len = size }
end
| REMOTE_OBJECT =>
set_object_base(obj,addr)
)
in
fun examine_object(obj,addr) =
( examine_object'(obj,addr);
set_object_currency(obj, true)
)
end
local
fun update_store(barr,st,len,store,offset) =
let val dest_ba = store_content(store)
in
copy_ba(barr,st,len,dest_ba,offset)
end
in
fun set_object_value(obj,barr,idx) =
case objectMode(obj) of
LOCAL_OBJECT =>
let val store = object_store(obj)
in
if storeStatus(store) = RD_STATUS then raise ReadOnly else
let val len_barr = length_ba(barr)
val object_sz = objectSize(obj)
val upd_sz = Int.min(len_barr,object_sz)
val object_locn = objectLocation(obj)
val store_sz = storeSize(store)
val end_idx = idx + upd_sz
in
if (end_idx <= Int.min(len_barr,store_sz))
then update_store(barr,idx,upd_sz,store,object_locn)
else if end_idx <= len_barr
then (
expand_managed(store,end_idx);
update_store(barr,idx,upd_sz,store,object_locn)
)
else raise OutOfBounds;
set_object_currency(obj,true)
end handle Option => raise OutOfBounds
end
| REMOTE_OBJECT => raise ReadOnly
fun set_object_value'(obj,barr,idx) =
let val store = object_store(obj)
val object_sz = objectSize(obj)
val object_locn = objectLocation(obj)
in
update_store(barr,idx,object_sz,store,object_locn);
set_object_currency(obj,true)
end
fun copy_object_value{from,to} =
let val from_size = objectSize(from)
in
if not(objectCurrency(from)) then raise Currency else
if (from_size > objectSize(to)) then raise OutOfBounds else
if (objectMode(to) = REMOTE_OBJECT) then raise OutOfBounds else
case objectMode(from) of
LOCAL_OBJECT =>
let val from_store = object_store(from)
val barr = store_content(from_store)
val idx = objectLocation(from)
val store = object_store(to)
val to_idx = objectLocation(to)
in
update_store(barr,idx,from_size,store,to_idx);
set_object_currency(to,true)
end
| REMOTE_OBJECT =>
let val from_addr = objectAddress(from)
val to_store = object_store(to)
val to_idx = objectLocation(to)
in
peek_memory
{ loc=from_addr,
arr=store_content(to_store),
start=to_idx,
len=from_size };
set_object_currency(to,true)
end
end
fun copy_object_value'{from,to} =
if (objectMode(to) = REMOTE_OBJECT) then raise OutOfBounds else
let val to_store = object_store(to)
val barr = store_content(to_store)
val to_idx = objectLocation(to)
in
object_value'(from, barr, to_idx)
end
end
fun new_object(OBJECT{status,store,currency,mode,ltype,size,base,offset}) =
OBJECT{
status = status,
store = store,
currency = new(currency),
mode = new(mode),
ltype = new(ltype),
size = new(size),
base = newBox(base),
offset = new(offset)
}
fun dup_object(obj as OBJECT{status = TEMPORARY_OBJECT, ...}) = obj
| dup_object(OBJECT{store,currency,mode,ltype,size,base,offset, ...}) =
OBJECT{
status = PERMANENT_OBJECT,
store = store,
currency = new(currency),
mode = new(mode),
ltype = new(ltype),
size = new(size),
base = newBox(base),
offset = new(offset)
}
fun tmp_object(obj as OBJECT{status = TEMPORARY_OBJECT, ...}) =
(
set_object_currency(obj, false);
obj
)
| tmp_object(OBJECT{store,mode,ltype,size,base,offset, ...}) =
OBJECT{
status = TEMPORARY_OBJECT,
store = store,
currency = ref(false),
mode = new(mode),
ltype = new(ltype),
size = new(size),
base = newBox(base),
offset = new(offset)
}
local
fun reset_base(base) =
case extractBox(base) of
SOME(addr) =>
if (addr = null_addr) then () else resetBox(base)
|
_ => ()
fun move_object_local(store,size,base,offset,new_offset) =
(
adjust_store(store,new_offset,!size);
reset_base(base);
offset := new_offset
)
fun move_object_remote(base,incr) =
let val cur_addr = getBox(base)
val new_addr = addr_plus(cur_addr,incr)
in
setBox(base)(new_addr)
end
fun set_addr_remote(base,addr) = setBox(base)(addr)
fun set_addr_local(store,size,base,offset,addr) =
let val origin = store_origin(store)
val new_offset = addr_minus(addr,origin)
in
move_object_local(store,size,base,offset,new_offset)
end
in
fun move_object'(OBJECT{mode=ref(REMOTE_OBJECT), base, ...}, incr) =
move_object_remote(base,incr)
| move_object'(OBJECT{store, base, offset, size, ...}, new_offset) =
move_object_local(store, size, base, offset, new_offset)
fun offset_object'(OBJECT{mode=ref(REMOTE_OBJECT),base, ...}, incr) =
move_object_remote(base,incr)
| offset_object'(OBJECT{store, base, offset, size, ...},incr) =
let val new_offset = (!offset + incr)
in
move_object_local(store, size, base, offset, new_offset)
end
fun set_object_address'(OBJECT{mode=ref(REMOTE_OBJECT),base, ...}, addr) =
set_addr_remote(base,addr)
| set_object_address'(OBJECT{store, base, offset, size, ...}, addr) =
set_addr_local(store, size, base, offset, addr)
fun move_object(obj,arg) =
( move_object'(obj,arg); set_object_currency(obj,false) )
fun offset_object(obj,arg) =
( offset_object'(obj,arg); set_object_currency(obj,false) )
fun set_object_address(obj, arg) =
( set_object_address'(obj,arg); set_object_currency(obj,false) )
end
local
fun str_object_status (PERMANENT_OBJECT) = "permanent"
| str_object_status (TEMPORARY_OBJECT) = "temporary"
fun str_currency(true) = "valid"
| str_currency(false) = "not valid"
fun str_object_mode(LOCAL_OBJECT) = "local"
| str_object_mode(REMOTE_OBJECT) = "remote"
fun str_base((ref(some_addr))) = some_addr
fun object_data'(obj,size) =
let val ba = bytearray(size,0)
in
object_value'(obj,ba,0);
ba
end
in
fun object_info
type_info
(OBJECT{status, currency, mode, ltype, size, base, offset, store}) =
{ store = store,
status = str_object_status(status),
currency = str_currency(!currency),
mode = str_object_mode(!mode),
langtype = type_info(!ltype),
size = !size,
base = str_base(base),
offset = !offset
}
fun object_data(obj) =
let val size = objectSize(obj)
val ba = object_data'(obj,size)
in
to_list(ba)
end
fun object_data_hex(obj) =
let val size = objectSize(obj)
val ba = object_data'(obj,size)
in
bytearray_to_hex{arr=ba,st=0,len=size}
end
fun object_data_ascii(obj) =
let val size = objectSize(obj)
val ba = object_data'(obj,size)
in
bytearray_to_string{arr=ba,st=0,len=size}
end
end
end
end;
