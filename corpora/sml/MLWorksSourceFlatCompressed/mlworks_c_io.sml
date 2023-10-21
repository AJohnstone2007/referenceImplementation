require "^.basis.__char_array";
require "^.basis.__char_vector";
require "^.basis.__real_array";
require "^.basis.__real_vector";
require "__mlworks_c_interface";
signature MLWORKS_C_IO =
sig
type FILE
type size_t = MLWorksCInterface.Uint.word
type c_char = MLWorksCInterface.Char.char
type c_int = MLWorksCInterface.Int.int
type void = MLWorksCInterface.void
type 'a ptr = 'a MLWorksCInterface.ptr
val EOF : c_int
val clearerr : FILE ptr -> unit
val fclose : FILE ptr -> int
val ferror : FILE ptr -> c_int
val feof : FILE ptr -> c_int
val fgetc : FILE ptr -> c_int
val fopen : string * string -> FILE ptr
val fputc : FILE ptr * c_char -> c_int
val fputs : FILE ptr * c_char ptr -> c_int
val fputString : FILE ptr * string -> c_int
val fflush : FILE ptr -> c_int
val fread : void ptr * size_t * size_t * FILE ptr -> size_t
val freadCharArray : CharArray.array * int * int * FILE ptr -> int
val freadRealArray : RealArray.array * int * int * FILE ptr -> int
val freopen : string * string * FILE ptr -> FILE ptr
val fseek : FILE ptr * MLWorksCInterface.Long.int * c_int -> c_int
val ftell : FILE ptr -> MLWorksCInterface.Long.int
val fwrite : void ptr * size_t * size_t * FILE ptr -> size_t
val fwriteCharArray : CharArray.array * int * int * FILE ptr -> int
val fwriteCharVector : CharVector.vector * int * int * FILE ptr -> int
val fwriteRealArray : RealArray.array * int * int * FILE ptr -> int
val fwriteRealVector : RealVector.vector * int * int * FILE ptr -> int
val fwriteString : string * int * int * FILE ptr -> int
val perror : c_char ptr -> unit
val perrorString : string -> unit
val setvbuff : FILE ptr * c_char ptr * c_int * size_t -> c_int
val setbuff :
FILE ptr * c_char -> unit
val stdin : unit -> FILE ptr
val stdout : unit -> FILE ptr
val stderr : unit -> FILE ptr
val ungetc : FILE ptr * c_int -> c_int
end
;
