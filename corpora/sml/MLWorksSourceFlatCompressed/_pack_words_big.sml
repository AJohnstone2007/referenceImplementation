require "__int";
require "__word8_vector";
require "__word8_array";
require "pack_word";
require "word";
functor PackWordsBig(
structure Word : WORD
) : PACK_WORD =
struct
val wordSize = Word.wordSize
val wordTag = " word" ^ Int.toString(wordSize)
val MLWenvironment = MLWorks.Internal.Runtime.environment;
fun env s = MLWenvironment(s ^ wordTag);
val bytesPerElem : int = (wordSize div 8)
val isBigEndian : bool = true
val subVec : (Word8Vector.vector * int) -> Word.word = env "subV"
val subArr : (Word8Array.array * int) -> Word.word = env "subA"
val update : (Word8Array.array * int * Word.word) -> unit = env "update"
val subVecX = Word.toLargeWordX o subVec
val subVec = Word.toLargeWord o subVec
val subArrX = Word.toLargeWordX o subArr
val subArr = Word.toLargeWord o subArr
val update =
fn (array, i, word) => update(array, i, Word.fromLargeWord word)
end
;
