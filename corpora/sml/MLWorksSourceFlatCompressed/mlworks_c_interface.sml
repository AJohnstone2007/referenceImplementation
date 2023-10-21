require "$.basis.__real";
require "$.basis.__word";
require "$.basis.__word8";
require "$.basis.__word16";
require "$.basis.__word32";
require "$.basis.char";
require "$.basis.integer";
require "$.basis.word";
require "$.basis.real";
require "mlworks_c_pointer";
signature MLWORKS_C_INTERFACE =
sig
eqtype 'a ptr
eqtype void
val null : 'a ptr
val fromVoidPtr : void ptr -> 'a ptr
val toVoidPtr : 'a ptr -> void ptr
val deRef8 : void ptr -> Word8.word
val deRef16 : void ptr -> Word16.word
val deRef32 : void ptr -> Word32.word
val deRefReal : void ptr -> Real.real
val update8 : void ptr * Word8.word -> unit
val update16 : void ptr * Word16.word -> unit
val update32 : void ptr * Word32.word -> unit
val updateReal : void ptr * Real.real -> unit
val malloc : Word.word -> void ptr
val free : void ptr -> unit
val memcpy : {source: void ptr, dest: void ptr, size: Word.word} -> unit
val ptrSize : Word.word
val toSysWord : void ptr -> SysWord.word
val fromSysWord : SysWord.word -> void ptr
val next : void ptr * Word.word -> void ptr
val prev : void ptr * Word.word -> void ptr
type syserror
exception SysErr of (string * syserror option)
structure Char : CHAR
structure Short : INTEGER
structure Int : INTEGER
structure Long : INTEGER
structure Uchar : WORD
structure Ushort : WORD
structure Uint : WORD
structure Ulong : WORD
structure Double : REAL
structure CharPtr :
sig
include MLWORKS_C_POINTER
val fromString : string -> Char.char ptr
val copySubString : Char.char ptr * Word.word -> string
end where type value = Char.char
where type 'a ptr = 'a ptr
structure ShortPtr : MLWORKS_C_POINTER
where type value = Short.int
where type 'a ptr = 'a ptr
structure IntPtr : MLWORKS_C_POINTER
where type value = Int.int
where type 'a ptr = 'a ptr
structure LongPtr : MLWORKS_C_POINTER
where type value = Long.int
where type 'a ptr = 'a ptr
structure UcharPtr : MLWORKS_C_POINTER
where type value = Uchar.word
where type 'a ptr = 'a ptr
structure UshortPtr : MLWORKS_C_POINTER
where type value = Ushort.word
where type 'a ptr = 'a ptr
structure UintPtr : MLWORKS_C_POINTER
where type value = Uint.word
where type 'a ptr = 'a ptr
structure UlongPtr : MLWORKS_C_POINTER
where type value = Ulong.word
where type 'a ptr = 'a ptr
structure DoublePtr : MLWORKS_C_POINTER
where type value = Double.real
where type 'a ptr = 'a ptr
structure PtrPtr :
sig
val := : 'a ptr ptr * 'a ptr -> unit
val ! : 'a ptr ptr -> 'a ptr
end
end
;
