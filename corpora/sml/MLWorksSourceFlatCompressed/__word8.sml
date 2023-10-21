require "word";
require "__word";
structure Word8: WORD =
struct
type word = MLWorks.Internal.Types.word8
val wordSize = 8
val wwordSize = 0w8 : Word.word
val cast = MLWorks.Internal.Value.cast
fun toWord (w : word) : Word.word = cast w
fun castFromWord (w: Word.word): word = cast w
fun maskFromWord (w: Word.word): word =
cast (Word.andb (cast 0xff, w))
fun checkFromWord (w:Word.word) : word =
if w < 0w100 then castFromWord w
else raise Overflow
val maxW = (Word.<< (0w1,0w8) - 0w1)
fun extend (w : word) : Word.word =
Word.~>> (Word.<< (toWord w,0w22),0w22)
fun toInt w = Word.toInt (toWord w)
fun toIntX w = Word.toIntX (extend w)
fun fromInt x = maskFromWord (Word.fromInt x)
fun toLargeWord x = Word.toLargeWord (toWord x)
fun toLargeInt x = Word.toLargeInt (toWord x)
fun toLargeWordX x = Word.toLargeWordX (extend x)
fun toLargeIntX x = Word.toLargeIntX (extend x)
fun fromLargeWord x = maskFromWord (Word.fromLargeWord x)
fun fromLargeInt x = maskFromWord (Word.fromLargeInt x)
val orb = cast Word.orb : word * word -> word
val xorb = cast Word.xorb : word * word -> word
val andb = cast Word.andb : word * word -> word
fun notb (w: word) = maskFromWord (Word.notb (toWord w))
fun << (w, n) = maskFromWord (Word.<< (toWord w, n))
fun >> (w, n) = maskFromWord (Word.>> (toWord w, n))
fun ~>> (w, n) =
if n >= wwordSize then
if w >= 0wx80 then 0wxFF else 0w0
else
let
val w' = Word.<< (toWord w, 0w22)
in
maskFromWord (Word.~>> (w', Word.+ (n, 0w22)))
end
fun toString w = Word.toString (toWord w)
fun fmt radix w = Word.fmt radix (toWord w)
fun fromString s =
case Word.fromString s of
SOME w => if w < 0wx100 then SOME (castFromWord w) else raise Overflow
| _ => NONE
fun scan radix getc src =
case Word.scan radix getc src of
SOME (w,src') => if w < 0wx100 then SOME (castFromWord w,src')
else raise Overflow
| _ => NONE
val op+ = op+ : word * word -> word
val op- = op- : word * word -> word
val op* = op* : word * word -> word
val op div = op div : word * word -> word
val op mod = op mod : word * word -> word
val op < = op < : word * word -> bool
val op > = op > : word * word -> bool
val op <= = op <= : word * word -> bool
val op >= = op >= : word * word -> bool
fun compare (w1,w2) =
if w1 < w2 then LESS
else if w1 = w2 then EQUAL
else GREATER
fun max (a : word, b) = if a > b then a else b
fun min (a : word, b) = if a < b then a else b
end;
