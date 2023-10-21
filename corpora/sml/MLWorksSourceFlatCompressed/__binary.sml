require "binary";
require "$.basis.__vector";
require "$.basis.__byte";
require "$.basis.__word8";
require "$.basis.__word8_vector";
require "$.basis.__word";
structure Binary : BINARY =
struct
datatype bit = ZERO | ONE
type binary = bit Vector.vector
exception Binary
fun bitToChar ZERO = #"0"
| bitToChar ONE = #"1"
fun charToBit #"0" = ZERO
| charToBit #"1" = ONE
| charToBit _ = raise Binary
fun fromString s =
SOME (Vector.fromList (map charToBit (rev (explode s))))
handle Binary => NONE
fun toString bin =
let
fun bits i =
bitToChar (Vector.sub (bin, i)) :: bits (i + 1)
handle Subscript => []
in
implode (rev (bits 0))
end
fun getBit byte n =
let
val mask = Word8.<< (Word8.fromInt 1, Word.fromInt n);
val b = Word8.andb (byte, mask)
in
if b = (Word8.fromInt 0) then ZERO else ONE
end
fun fromByte byte = Vector.tabulate (8, (getBit byte))
fun pow2 p = Word8.<< (Word8.fromInt 1, Word.fromInt p)
val powersOfTwo = Word8Vector.tabulate (8, pow2)
fun addBits (i, ZERO, total) = total
| addBits (i, ONE, total) =
Word8.+ (Word8Vector.sub (powersOfTwo, i), total)
fun toByte bin =
if Vector.length bin > 8 then
raise Size
else
Vector.foldli addBits (Word8.fromInt 0) (bin, 0, NONE)
fun toAscii s =
let
val byteVector = Byte.stringToBytes s
fun doByte i = fromByte (Word8Vector.sub (byteVector, i))
in
Vector.tabulate (size s, doByte)
end
fun fromAscii ascii =
let
fun doByte i = toByte (Vector.sub (ascii, i))
in
Byte.bytesToString (Word8Vector.tabulate (Vector.length ascii, doByte))
end
fun printAscii s =
let
fun display bin = print (toString bin ^ "\n")
in
Vector.app display (toAscii s)
end
end
;
