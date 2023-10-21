require "__word8";
require "__word8_vector";
require "__word8_array";
require "pack_word";
structure Pack8Little : PACK_WORD =
struct
val bytesPerElem : int = 1
val isBigEndian : bool = false
val subVec = Word8.toLargeWord o Word8Vector.sub
val subArr = Word8.toLargeWord o Word8Array.sub
val subVecX = Word8.toLargeWordX o Word8Vector.sub
val subArrX = Word8.toLargeWordX o Word8Array.sub
fun update(array, i, word) =
Word8Array.update(array, i, Word8.fromLargeWord word)
end;
