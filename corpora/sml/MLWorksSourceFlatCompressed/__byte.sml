require "byte";
require "__word8";
require "__word8_vector";
require "__word8_array";
require "__substring";
structure Byte : BYTE =
struct
fun byteToChar (w:Word8.word) : char = MLWorks.Internal.Value.cast w
fun charToByte (c:char) : Word8.word = MLWorks.Internal.Value.cast c
fun bytesToString (v:Word8Vector.vector) : string =
MLWorks.Internal.Value.cast v
fun stringToBytes (s:string) : Word8Vector.vector =
MLWorks.Internal.Value.cast s
val unpackStringVec =
bytesToString o Word8Vector.extract
val unpackString =
bytesToString o Word8Array.extract
fun packString (a, i, ss) =
let
val length = Word8Array.length a
fun copyFrom i =
if i < length then
(Word8Array.update(a, i, charToByte (Substring.sub(ss, i)));
copyFrom (i+1))
else
()
in
copyFrom i
end
end
;
