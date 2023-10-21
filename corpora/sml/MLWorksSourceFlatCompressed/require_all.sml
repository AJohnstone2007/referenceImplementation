require "option";
require "__option";
require "array";
require "array2";
require "bool";
require "__bool";
require "string_cvt";
require "__string_cvt";
require "char";
require "__char";
require "word";
require "__word";
require "__word8";
require "mono_array";
require "mono_array2";
require "mono_vector";
require "__word8_vector";
require "__word8_array";
require "__word8_array2";
require "__real_vector";
require "__real_array";
require "__real_array2";
require "byte";
require "time";
require "../system/__time";
require "date";
require "real";
require "__date";
require "integer";
require "prim_io";
require "stream_io";
require "text_stream_io";
require "io";
require "__io";
require "list_pair";
require "list";
require "math";
require "ieee_real";
require "__ieee_real";
require "__sys_word";
require "os_path";
require "os_file_sys";
require "os_process";
require "os";
require "os_io";
require "pack_word";
require "string";
require "__string";
require "substring";
require "__substring";
require "timer";
require "vector";
require "imperative_io";
require "bin_io";
require "text_io";
require "__text_prim_io";
require "__bin_prim_io";
require "__text_io";
require "__bin_io";
require "__char_vector";
require "__char_array";
require "__int";
require "__position";
require "_prim_io";
require "_stream_io";
require "_imperative_io";
require "__byte";
require "__word32";
require "__word16";
require "__list";
require "__list_pair";
require "__math";
require "../system/__os";
require "__pack16_big";
require "__pack16_little";
require "__pack32_big";
require "__pack32_little";
require "__pack8_big";
require "__pack8_little";
require "__real";
require "__timer";
require "__word16_vector";
require "__word16_array";
require "__word16_array2";
require "__word32_vector";
require "__word32_array";
require "__word32_array2";
require "__int8";
require "__int16";
require "__int32";
require "__large_int";
require "__large_real";
require "__large_word";
require "__array";
require "__vector";
require "__array2";
require "sml90";
require "__sml90";
require "command_line";
require "__command_line";
require "__sys_word";
require "__general";
require "general";
require "__net_db";
require "__serv_db";
require "__prot_db";
require "__host_db";
require "__socket";
require "__inet_sock";
require "__unix_sock";
signature ARRAY=ARRAY
signature ARRAY2=ARRAY2
signature BOOL=BOOL
signature STRING_CVT=STRING_CVT
signature CHAR=CHAR
signature WORD=WORD
signature MONO_ARRAY=MONO_ARRAY
signature MONO_ARRAY2=MONO_ARRAY2
signature MONO_VECTOR=MONO_VECTOR
signature BYTE=BYTE
signature TIME=TIME
signature DATE=DATE
signature REAL=REAL
signature INTEGER=INTEGER
signature PRIM_IO=PRIM_IO
signature STREAM_IO=STREAM_IO
signature IO=IO
signature LIST_PAIR=LIST_PAIR
signature LIST=LIST
signature MATH=MATH
signature IEEE_REAL=IEEE_REAL
signature OPTION=OPTION
signature OS_PATH=OS_PATH
signature OS_FILE_SYS=OS_FILE_SYS
signature OS_PROCESS=OS_PROCESS
signature OS=OS
signature OS_IO=OS_IO
signature PACK_WORD=PACK_WORD
signature STRING=STRING
signature SUBSTRING=SUBSTRING
signature TIMER=TIMER
signature VECTOR=VECTOR
signature BIN_IO=BIN_IO
signature TEXT_IO=TEXT_IO
signature TEXT_STREAM_IO=TEXT_STREAM_IO
signature IMPERATIVE_IO=IMPERATIVE_IO
signature SML90=SML90
signature COMMAND_LINE=COMMAND_LINE
signature GENERAL=GENERAL
structure Array=Array
structure Array2=Array2
structure BinIO=BinIO
structure BinPrimIO=BinPrimIO
structure Bool=Bool
structure Byte=Byte
structure Char=Char
structure CharArray=CharArray
structure CharVector=CharVector
structure Date=Date
structure General=General
structure IEEEReal=IEEEReal
structure Int=Int
structure Int8=Int8
structure Int16=Int16
structure Int32=Int32
structure IO=IO
structure LargeInt=LargeInt
structure LargeReal=LargeReal
structure LargeWord=LargeWord
structure List=List
structure ListPair=ListPair
structure Math=Math
structure Option=Option
structure OS=OS
structure Pack8Big=Pack8Big
structure Pack8Little=Pack8Little
structure Pack16Big=Pack16Big
structure Pack16Little=Pack16Little
structure Pack32Big=Pack32Big
structure Pack32Little=Pack32Little
structure Position=Position
structure Real=Real
structure String=String
structure StringCvt=StringCvt
structure Substring=Substring
structure SysWord=SysWord
structure TextIO=TextIO
structure TextPrimIO=TextPrimIO
structure Time=Time
structure Timer=Timer
structure Vector=Vector
structure Word=Word
structure Word8=Word8
structure Word16=Word16
structure Word32=Word32
structure Word8Array=Word8Array
structure Word8Array2=Word8Array2
structure Word8Vector=Word8Vector
structure Word16Array=Word16Array
structure Word16Array2=Word16Array2
structure Word16Vector=Word16Vector
structure Word32Array=Word32Array
structure Word32Array2=Word32Array2
structure Word32Vector=Word32Vector
structure RealVector=RealVector
structure RealArray=RealArray
structure RealArray2=RealArray2
structure SML90=SML90;
structure CommandLine=CommandLine;
structure SysWord = SysWord;
structure NetDB = NetDB;
structure NetServDB = NetServDB;
structure NetProtDB = NetProtDB;
structure NetHostDB = NetHostDB;
structure Socket = Socket;
structure INetSock = INetSock;
structure UnixSock = UnixSock;
functor ImperativeIO (
structure StreamIO : STREAM_IO
structure Vector: MONO_VECTOR
structure Array: MONO_ARRAY
sharing type StreamIO.elem = Vector.elem = Array.elem
sharing type StreamIO.vector = Vector.vector = Array.vector
) : IMPERATIVE_IO =
ImperativeIO
(structure StreamIO = StreamIO
structure Vector = Vector
structure Array = Array);
functor PrimIO (
include sig
structure A : MONO_ARRAY
structure V : MONO_VECTOR
end
sharing type A.vector = V.vector
sharing type A.elem = V.elem
val someElem : A.elem
type pos
val compare : pos * pos -> order
) : PRIM_IO =
PrimIO (
structure A = A
structure V = V
val someElem = someElem
type pos = pos
val compare = compare);
functor StreamIO (
structure PrimIO : PRIM_IO
structure Vector : MONO_VECTOR
structure Array: MONO_ARRAY
val someElem : PrimIO.elem
sharing type PrimIO.vector = Array.vector = Vector.vector
sharing type PrimIO.array = Array.array
sharing type Array.elem = PrimIO.elem = Vector.elem
) : STREAM_IO =
StreamIO (
structure PrimIO = PrimIO
structure Vector = Vector
structure Array = Array
val someElem = someElem);
