require "$.basis.__word";
require "$.foreign.__mlworks_dynamic_library";
require "$.foreign.__mlworks_c_interface";
require "dirent";
require "__dirent_stub";
structure Dirent : DIRENT =
struct
structure C' = MLWorksCInterface
type c_char = C'.Char.char
type c_ushort = C'.Ushort.word
type c_int = C'.Int.int
type c_long = C'.Long.int
type c_ulong = C'.Ulong.word
type 'a c_ptr = 'a C'.ptr
fun scale'' offset addr = C'.next (addr, offset)
datatype DIR = DIR of C'.void c_ptr
datatype struct'dirent = STRUCT'DIRENT of C'.void c_ptr
type off_t = c_long
fun DIR'toRep' (DIR addr) = addr
fun struct'dirent'toRep' (STRUCT'DIRENT addr) = addr
val DIR'size' = 0w24
val DIR'addr' = C'.fromVoidPtr o DIR'toRep'
val DIR'dd_fd'addr : DIR -> c_int c_ptr =
C'.fromVoidPtr o scale'' 0w0 o DIR'toRep'
val DIR'dd_loc'addr : DIR -> c_long c_ptr =
C'.fromVoidPtr o scale'' 0w4 o DIR'toRep'
val DIR'dd_size'addr : DIR -> c_long c_ptr =
C'.fromVoidPtr o scale'' 0w8 o DIR'toRep'
val DIR'dd_bsize'addr : DIR -> c_long c_ptr =
C'.fromVoidPtr o scale'' 0w12 o DIR'toRep'
val DIR'dd_off'addr : DIR -> c_long c_ptr =
C'.fromVoidPtr o scale'' 0w16 o DIR'toRep'
val DIR'dd_buff'addr : DIR -> c_char c_ptr c_ptr =
C'.fromVoidPtr o scale'' 0w20 o DIR'toRep'
val DIR'dd_fd : DIR -> c_int =
C'.IntPtr.! o DIR'dd_fd'addr
val DIR'dd_loc : DIR -> c_long =
C'.LongPtr.! o DIR'dd_loc'addr
val DIR'dd_size : DIR -> c_long =
C'.LongPtr.! o DIR'dd_size'addr
val DIR'dd_bsize : DIR -> c_long =
C'.LongPtr.! o DIR'dd_bsize'addr
val DIR'dd_off : DIR -> c_long =
C'.LongPtr.! o DIR'dd_off'addr
val DIR'dd_buff : DIR -> c_char c_ptr =
C'.PtrPtr.! o DIR'dd_buff'addr
val struct'dirent'size' = 0w14 + 0w256
val struct'dirent'addr' = C'.fromVoidPtr o struct'dirent'toRep'
val struct'dirent'd_off'addr : struct'dirent -> off_t c_ptr =
C'.fromVoidPtr o scale'' 0w0 o struct'dirent'toRep'
val struct'dirent'd_fileno'addr : struct'dirent -> c_ulong c_ptr =
C'.fromVoidPtr o scale'' 0w4 o struct'dirent'toRep'
val struct'dirent'd_reclen'addr : struct'dirent -> c_ushort c_ptr =
C'.fromVoidPtr o scale'' 0w8 o struct'dirent'toRep'
val struct'dirent'd_namlen'addr : struct'dirent -> c_ushort c_ptr =
C'.fromVoidPtr o scale'' 0w10 o struct'dirent'toRep'
val struct'dirent'd_name'addr : struct'dirent -> c_char c_ptr =
C'.fromVoidPtr o scale'' 0w12 o struct'dirent'toRep'
val struct'dirent'd_off : struct'dirent -> c_long =
C'.LongPtr.! o struct'dirent'd_off'addr
val struct'dirent'd_fileno : struct'dirent -> c_ulong =
C'.UlongPtr.! o struct'dirent'd_fileno'addr
val struct'dirent'd_reclen : struct'dirent -> c_ushort =
C'.UshortPtr.! o struct'dirent'd_reclen'addr
val struct'dirent'd_namlen : struct'dirent -> c_ushort =
C'.UshortPtr.! o struct'dirent'd_namlen'addr
val struct'dirent'd_name : struct'dirent -> c_char c_ptr =
struct'dirent'd_name'addr
val opendir : c_char c_ptr -> DIR c_ptr =
MLWorksDynamicLibrary.bind "dirent_opendir"
val readdir : DIR c_ptr -> struct'dirent c_ptr =
MLWorksDynamicLibrary.bind "dirent_readdir"
val telldir : DIR c_ptr -> c_long =
MLWorksDynamicLibrary.bind "dirent_telldir"
val seekdir : DIR c_ptr * c_long -> unit =
MLWorksDynamicLibrary.bind "dirent_seekdir"
val rewinddir : DIR c_ptr -> unit =
MLWorksDynamicLibrary.bind "dirent_rewinddir"
val closedir : DIR c_ptr -> unit =
MLWorksDynamicLibrary.bind "dirent_closedir"
end
;
