require "$.basis.__word";
require "$.foreign.__mlworks_dynamic_library";
require "$.foreign.__mlworks_c_interface";
require "ndbm";
require "__ndbm_stub";
structure Ndbm : NDBM =
struct
structure C' = MLWorksCInterface
type c_int = C'.Int.int
type c_uint = C'.Uint.word
type c_char = C'.Char.char
type 'a c_ptr = 'a C'.ptr
fun scale'' offset addr = C'.next (addr, offset)
datatype DBM = DBM of C'.void c_ptr
datatype datum = DATUM of C'.void c_ptr
fun DBM'toRep' (DBM addr) = addr
fun datum'toRep' (DATUM addr) = addr
val DBM'size' = 0w11 * 0w4 + 0w1024 + 0w4096
val DBM'addr' = C'.fromVoidPtr o DBM'toRep'
val datum'size' : Word.word = 0w8
val datum'addr' : datum -> datum c_ptr = C'.fromVoidPtr o datum'toRep'
val DBM'dbm_dirf'addr : DBM -> c_int c_ptr =
C'.fromVoidPtr o scale'' 0w0 o DBM'toRep'
val DBM'dbm_pagf'addr : DBM -> c_int c_ptr =
C'.fromVoidPtr o scale'' 0w4 o DBM'toRep'
val DBM'dbm_dirf : DBM -> c_int =
C'.IntPtr.! o DBM'dbm_dirf'addr
val DBM'dbm_pagf : DBM -> c_int =
C'.IntPtr.! o DBM'dbm_pagf'addr
val datum'dptr'addr : datum -> c_char c_ptr c_ptr =
C'.fromVoidPtr o scale'' 0w0 o datum'toRep'
val datum'dsize'addr : datum -> C'.Uint.word c_ptr =
C'.fromVoidPtr o scale'' 0w4 o datum'toRep'
val datum'dptr : datum -> c_char c_ptr =
C'.PtrPtr.! o datum'dptr'addr
val datum'dsize : datum -> C'.Uint.word =
C'.UintPtr.! o datum'dsize'addr
val DBM_INSERT = C'.Int.fromInt 0
val DBM_REPLACE = C'.Int.fromInt 1
val dbm_open : c_char c_ptr * c_int * c_int -> DBM c_ptr =
MLWorksDynamicLibrary.bind "dbm_open"
val dbm_store : DBM c_ptr * datum * datum * c_int -> c_int =
MLWorksDynamicLibrary.bind "dbm_store"
val dbm_delete : DBM c_ptr * datum -> c_int =
MLWorksDynamicLibrary.bind "dbm_delete"
val dbm_fetch : DBM c_ptr * datum -> datum =
MLWorksDynamicLibrary.bind "dbm_fetch"
val dbm_firstkey : DBM c_ptr -> datum =
MLWorksDynamicLibrary.bind "dbm_firstkey"
val dbm_nextkey : DBM c_ptr -> datum =
MLWorksDynamicLibrary.bind "dbm_nextkey"
val dbm_error : DBM c_ptr -> c_int =
MLWorksDynamicLibrary.bind "dbm_error"
val dbm_clearerr : DBM c_ptr -> c_int =
MLWorksDynamicLibrary.bind "dbm_clearerr"
val dbm_close : DBM c_ptr -> unit =
MLWorksDynamicLibrary.bind "dbm_close"
end
;
