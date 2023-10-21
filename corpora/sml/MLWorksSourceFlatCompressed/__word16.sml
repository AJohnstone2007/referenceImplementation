require "word";
require "__word";
structure Word16: WORD =
struct
type word = MLWorks.Internal.Types.word16
val wordSize = 16
val cast = MLWorks.Internal.Value.cast
val castFromWord : Word.word -> word = cast
fun toWord (w : word) : Word.word = cast w
fun fromWord (w: Word.word): word =
cast (Word.andb (cast 0xffff, w))
val castFromWord : Word.word -> word = cast
val maxW = (Word.<< (0w1,0w16) - 0w1)
fun extend (w : word) : Word.word =
Word.~>> (Word.<< (toWord w,0w14),0w14)
fun toInt w = Word.toInt (toWord w)
fun toIntX w = Word.toIntX (extend w)
fun fromInt x = fromWord (Word.fromInt x)
fun toLargeWord x = Word.toLargeWord (toWord x)
fun toLargeInt x = Word.toLargeInt (toWord x)
fun toLargeWordX x = Word.toLargeWordX (extend x)
fun toLargeIntX x = Word.toLargeIntX (extend x)
fun fromLargeWord x = fromWord (Word.fromLargeWord x)
fun fromLargeInt x = fromWord (Word.fromLargeInt x)
val orb = cast Word.orb : word * word -> word
val xorb = cast Word.xorb : word * word -> word
val andb = cast Word.andb : word * word -> word
fun notb (w: word) = fromWord (Word.notb (toWord w))
fun << (w, n) = fromWord (Word.<< (toWord w, n))
fun >> (w, n) = fromWord (Word.>> (toWord w, n))
fun ~>> (w, n) =
let
val w' = Word.<< (toWord w, 0w14)
in
fromWord (Word.~>> (w', Word.+ (n, 0w14)))
end
fun toString w = Word.toString (toWord w)
fun fmt radix w = Word.fmt radix (toWord w)
fun fromString s =
case Word.fromString s of
SOME w => if w < 0wx10000 then SOME (castFromWord w) else raise Overflow
| _ => NONE
fun scan radix getc src =
case Word.scan radix getc src of
SOME (w,src') => if w < 0wx10000 then SOME (castFromWord w,src')
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
fun max (a,b) = if a > b then a else b
fun min (a,b) = if a < b then a else b
end;
