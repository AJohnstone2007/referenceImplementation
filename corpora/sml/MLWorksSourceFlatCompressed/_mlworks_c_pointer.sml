require "$.basis.__word";
require "$.foreign.__mlworks_c_interface";
require "$.foreign.mlworks_c_pointer";
functor MLWorksCPointer
( type value
val size : Word.word
val addr : value -> value MLWorksCInterface.ptr
) : MLWORKS_C_POINTER =
struct
structure C = MLWorksCInterface
type 'a ptr = 'a C.ptr
type value = value
val ! = MLWorks.Internal.Value.cast
fun op := (p: value C.ptr, v: value): unit =
C.memcpy {dest = C.toVoidPtr p, source = C.toVoidPtr (addr v), size = size}
fun make () = C.fromVoidPtr (C.malloc size)
fun makeArray n =
if n < 0 then
C.null
else
C.fromVoidPtr (C.malloc ((Word.fromInt n)*size))
val free : value C.ptr -> unit = C.free o C.toVoidPtr
fun applyPair (g, h) (x, y) = (g x, h y)
fun scale x = size * x
val next : value C.ptr * Word.word -> value C.ptr =
C.fromVoidPtr o C.next o applyPair (C.toVoidPtr, scale)
val prev : value C.ptr * Word.word -> value C.ptr =
C.fromVoidPtr o C.prev o applyPair (C.toVoidPtr, scale)
val size = C.ptrSize
end
;
