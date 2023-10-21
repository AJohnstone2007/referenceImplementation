require "types";
signature FOREIGN_UTILS =
sig
type 'a option = 'a option
structure FITypes : FOREIGN_TYPES
type bytearray = FITypes.bytearray
type word32 = FITypes.word32
type address = FITypes.address
type 'a box = 'a option ref
val someBox : 'a box -> bool
val getBox : 'a box -> 'a
val setBox : 'a box -> 'a -> unit
val extractBox : 'a box -> 'a option
val updateBox : 'a box -> 'a option -> unit
val resetBox : 'a box -> unit
val makeBox : 'a -> 'a box
val newBox : 'a box -> 'a box
val voidBox : unit -> 'a box
val disp : ('a -> string) -> 'a -> 'a
val sep_items : 'a -> 'a list -> 'a list
val term_items : 'a -> 'a list -> 'a list
val is_big_endian : bool
val int_to_bytearray : {src:int, len:int, arr:bytearray, st:int} -> unit
val bytearray_to_int : {arr:bytearray, st:int, len:int} -> int
val string_to_bytearray : {src:string, arr:bytearray, st:int} -> unit
val bytearray_to_string : {arr:bytearray, st:int, len:int} -> string
val bytearray_to_hex : {arr:bytearray, st:int, len:int} -> string
val word32_to_bytearray : {src:word32, arr:bytearray, st:int} -> unit
val word32_to_hex : word32 -> string
val bytearray_to_word32 : {arr:bytearray, st:int} -> word32
val peek_memory : { loc : address,
arr : bytearray,
start : int,
len : int } -> unit
end;
