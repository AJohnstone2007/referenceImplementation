require "../basis/__int";
require "../basis/__string";
require "types";
require "__types";
require "utils";
structure ForeignUtils_ : FOREIGN_UTILS =
struct
structure FITypes : FOREIGN_TYPES = ForeignTypes_
structure Bits = MLWorks.Internal.Bits
open FITypes
structure ByteArray = MLWorks.Internal.ByteArray
val MLWcast = MLWorks.Internal.Value.cast : 'a -> 'b
val MLWenvironment = MLWorks.Internal.Runtime.environment;
fun env s = MLWcast(MLWenvironment s);
val bytearray = ByteArray.array
val sub_bytearray = ByteArray.subarray
val to_list = ByteArray.to_list
val sub_ba = MLWorks.Internal.Value.unsafe_bytearray_sub
val update_ba = MLWorks.Internal.Value.unsafe_bytearray_update
val copy_ba' = ByteArray.copy
val copy_ba =
fn (src,src_st,len,tgt,tgt_st) =>
copy_ba'(src,src_st,src_st+len,tgt,tgt_st)
val word32_repn : word32 -> bytearray = env "word32 word to bytearray";
val word32_abstr : bytearray -> word32 = env "word32 bytearray to word";
val rshift = Bits.rshift
val andb = Bits.andb
val lshift = Bits.lshift
fun mask_lower(i) = andb(i,255)
fun get_upper_bits(i) = rshift(i,8)
type 'a box = 'a option ref
fun getBox(df) = valOf(!df)
fun setBox(rval)(a) = rval := SOME(a)
fun extractBox(rval) = !rval
fun updateBox(rval)(optv) = rval := optv
fun resetBox(rval) = rval := NONE
fun newBox(rval) = ref(!rval)
fun makeBox(v) = ref(SOME(v))
fun voidBox() = ref(NONE)
fun someBox(rval) = isSome(!rval)
fun disp view_fn x =
let
val str = view_fn(x)
in
print str;
x
end
fun sep_items sep =
let fun doit(x::y,r) = doit(y,x::sep::r)
| doit([],r) = rev r
in
fn [] => []
| (h :: t) => doit(t,[h])
end
fun term_items sep =
let fun doit(x::y,r) = doit(y,x::sep::r)
| doit([],r) = rev (sep :: r)
in
fn [] => []
| (h :: t) => doit(t,[h])
end
local
fun int_to_bytearray{src,len,arr=ba,st} =
let fun doit(k,idx,n) =
if (k < 1) then () else
let val lwb = mask_lower(n)
val upb = get_upper_bits(n)
in
update_ba(ba,idx,lwb);
doit(k-1,idx-1,upb)
end
in
doit(len,st+len-1,src)
end
fun bytearray_to_int{arr=ba,st,len} =
let fun doit(k,idx,v) =
if (k < 1) then v else
let val lwb = sub_ba(ba,idx)
val new_v = lshift(v,8) + lwb
in
doit(k-1,idx+1,new_v)
end
in
doit(len,st,0)
end
fun int_to_bytearray_rev{src,len,arr=ba,st} =
let fun doit(k,idx,n) =
if (k < 1) then () else
let val lwb = mask_lower(n)
val upb = get_upper_bits(n)
in
update_ba(ba,idx,lwb);
doit(k-1,idx+1,upb)
end
in
doit(len,st,src)
end
fun bytearray_to_int_rev{arr=ba,st,len} =
let fun doit(k,idx,v) =
if (k < 1) then v else
let val lwb = sub_ba(ba,idx)
val new_v = lshift(v,8) + lwb
in
doit(k-1,idx-1,new_v)
end
in
doit(len,st+len-1,0)
end
in
val is_big_endian : bool = (env "big endian flag" ())
val int_to_bytearray =
if is_big_endian then int_to_bytearray
else int_to_bytearray_rev
val bytearray_to_int =
if is_big_endian then bytearray_to_int
else bytearray_to_int_rev
end
fun string_to_bytearray{src=str,arr=ba,st} =
let val size' = size(str) - 1
fun doit(k,idx) =
if (k < 0) then () else
let val byte = ord(String.sub(str,k))
in
update_ba(ba,idx,byte);
doit(k-1,idx-1)
end
in
doit(size',st+size')
end
fun bytearray_to_string{arr=ba,st,len} =
let
val len' = len - 1
fun doit(k, idx, acc) =
if (k < 0) then
String.implode acc
else
let
val byte = sub_ba(ba,idx)
in
doit(k-1, idx-1, chr byte :: acc)
end
in
doit(len', st+len', [])
end
fun word32_to_bytearray{src=wd,arr=ba,st} =
let val src_ba = word32_repn( wd )
in
copy_ba(src_ba,0,4,ba,st)
end
fun bytearray_to_word32{arr=ba,st} =
let val wd_repn = bytearray(4,0)
in
copy_ba(ba,st,4,wd_repn,0);
word32_abstr( wd_repn )
end
local
val a = ord #"A"
fun hex_digit(i) =
if 0 <= i andalso i <= 9 then
Int.toString i
else
(str o chr) (a + (i-10))
fun hex_byte(i,strl) =
let val hi = andb(rshift(i,4),15)
val lo = andb(i,15)
in
hex_digit(lo) :: hex_digit(hi) :: strl
end
fun hex_byte'(i,strl) =
let val hi = andb(rshift(i,4),15)
val lo = andb(i,15)
in
" " :: hex_digit(lo) :: hex_digit(hi) :: strl
end
fun loop(f) =
let fun doit([],strl) = strl
| doit(i::l,strl) = doit(l, f(i,strl))
in
doit
end
in
fun word32_to_hex (wd) =
let val loopf = loop hex_byte
val ba = word32_repn(wd)
val data = loopf(to_list(ba),["x", "0"])
in
concat (rev data)
end
fun bytearray_to_hex {arr,st,len} =
let val loopf = loop hex_byte'
val ba = sub_bytearray(arr,st,st+len)
val data = loopf(to_list(ba),[])
in
if data = nil then
""
else
(concat o rev o tl) data
end
end
local
val peek_mem : (address * bytearray * int * int) -> unit =
env "bytearray peek memory"
in
fun peek_memory {loc, arr, start, len} =
peek_mem(loc,arr,start,len)
handle _ => raise Fail("peek_memory")
end
end;
