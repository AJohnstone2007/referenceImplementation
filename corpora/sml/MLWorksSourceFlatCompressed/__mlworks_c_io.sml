require "^.basis.__char_array";
require "^.basis.__char_vector";
require "^.basis.__real_array";
require "^.basis.__real_vector";
require "__mlworks_dynamic_library";
require "__mlworks_c_interface";
require "mlworks_c_io";
structure MLWorksCIO : MLWORKS_C_IO =
struct
structure C = MLWorksCInterface
type c_char = MLWorksCInterface.Char.char
type c_int = MLWorksCInterface.Int.int
type void = MLWorksCInterface.void
type 'a ptr = 'a MLWorksCInterface.ptr
datatype FILE = FILE of void ptr
type size_t = C.Uint.word
val bind = MLWorksDynamicLibrary.bind
val EOF : c_int = C.Int.fromInt ~1
val clearerr : FILE ptr -> unit = bind "mlw clearerr"
val fclose : FILE ptr -> int = bind "mlw fclose"
val ferror : FILE ptr -> c_int = bind "mlw ferror"
val feof : FILE ptr -> c_int = bind "mlw feof"
val fgetc : FILE ptr -> c_int = bind "mlw fgetc"
val fopen : string * string -> FILE ptr = bind "mlw fopen"
val fputc : FILE ptr * c_char -> c_int = bind "mlw fputc"
val fputs : FILE ptr * c_char ptr -> c_int = bind "mlw fputs"
val fputString : FILE ptr * string -> c_int = bind "mlw fput string"
val fflush : FILE ptr -> c_int = bind "mlw fflush"
val fread : void ptr * size_t * size_t * FILE ptr -> size_t =
bind "mlw fread"
val freadCharArray_ : CharArray.array * int * int * FILE ptr -> int =
bind "mlw fread char array"
fun freadCharArray (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > CharArray.length vector then
raise Subscript
else
freadCharArray_ (vector, offset, n, file)
val freadRealArray_ : RealArray.array * int * int * FILE ptr -> int =
bind "mlw fread real array"
fun freadRealArray (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > RealArray.length vector then
raise Subscript
else
freadRealArray_ (vector, offset, n, file)
val freopen : string * string * FILE ptr -> FILE ptr = bind "mlw freopen"
val fseek : FILE ptr * C.Long.int * c_int -> c_int =bind "mlw fseek"
val ftell : FILE ptr -> C.Long.int = bind "mlw ftell"
val fwrite : void ptr * size_t * size_t * FILE ptr -> size_t =
bind "mlw fwrite"
val fwriteCharArray_ : CharArray.array * int * int * FILE ptr -> int =
bind "mlw fwrite char array"
fun fwriteCharArray (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > CharArray.length vector then
raise Subscript
else
fwriteCharArray_ (vector, offset, n, file)
val fwriteCharVector_ : CharVector.vector * int * int * FILE ptr -> int =
bind "mlw fwrite char vector"
fun fwriteCharVector (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > CharVector.length vector then
raise Subscript
else
fwriteCharVector_ (vector, offset, n, file)
val fwriteRealArray_ : RealArray.array * int * int * FILE ptr -> int =
bind "mlw fwrite real array"
fun fwriteRealArray (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > RealArray.length vector then
raise Subscript
else
fwriteRealArray_ (vector, offset, n, file)
val fwriteRealVector_ : RealVector.vector * int * int * FILE ptr -> int =
bind "mlw fwrite real vector"
fun fwriteRealVector (vector, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > RealVector.length vector then
raise Subscript
else
fwriteRealVector_ (vector, offset, n, file)
val fwriteString_ : string * int * int * FILE ptr -> int =
bind "mlw fwrite string"
fun fwriteString (string, offset, n, file) =
if n = 0 then
n
else if offset < 0
orelse n < 0
orelse (offset-1)+n > size string then
raise Subscript
else
fwriteString_ (string, offset, n, file)
val setvbuff : FILE ptr * c_char ptr * c_int * size_t -> c_int =
bind "mlw setvbuf"
val perror : c_char ptr -> unit = bind "mlw perror"
val perrorString : string -> unit = bind "mlw perror string"
val setbuff : FILE ptr * c_char -> unit = bind "mlw setbuf"
val stdin : unit -> FILE ptr = bind "mlw stdin"
val stdout : unit -> FILE ptr = bind "mlw stdout"
val stderr : unit -> FILE ptr = bind "mlw stderr"
val ungetc : FILE ptr * c_int -> c_int = bind "mlw ungetc"
end
;
