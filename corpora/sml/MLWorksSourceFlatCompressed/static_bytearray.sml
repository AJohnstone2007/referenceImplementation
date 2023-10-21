require "../basis/__word8";
require "../basis/__word32";
signature STATIC_BYTEARRAY =
sig
eqtype bytearray = MLWorks.Internal.ByteArray.bytearray
eqtype address = Word32.word
eqtype word8 = Word8.word
eqtype static_bytearray
val array : int * word8 -> static_bytearray
val length : static_bytearray -> int
val update : static_bytearray * int * word8 -> unit
val sub : static_bytearray * int -> word8
val array_of_list : word8 list -> static_bytearray
val tabulate : int * (int -> word8) -> static_bytearray
val to_bytearray : static_bytearray -> bytearray
val address_of : static_bytearray * int -> address
val from_list : word8 list -> static_bytearray
val to_list : static_bytearray -> word8 list
val from_string : string -> static_bytearray
val to_string : static_bytearray -> string
val alloc_array : int -> static_bytearray
end;
