require "__pre_int";
require "__string_cvt";
structure PreWord32 =
struct
type word = MLWorks.Internal.Types.word32
val wordSize = 32
val env = MLWorks.Internal.Runtime.environment
val cast = MLWorks.Internal.Value.cast
val toInt : word -> int = env "word32 word to int"
val toIntX : word -> int =
fn w => PreInt.fromLarge (cast w)
val fromInt : int -> word = env "word32 extend int to word32"
fun toLargeWord x = x
fun toLargeWordX x = x
fun toLargeInt x =
let
val i = cast x : MLWorks.Internal.Types.int32
in
if i < 0 then raise Overflow
else i
end
fun toLargeIntX x = cast x
fun fromLargeWord x = x
fun fromLargeInt x = cast x
val fromReal : real -> word =
MLWorks.Internal.Runtime.environment "word real to word32"
val toReal : word -> real =
MLWorks.Internal.Runtime.environment "word word32 to real"
val realmax = 256.0 * 256.0 * 256.0 * 256.0
fun makeString (base,n) =
if n = 0w0 then "0"
else
let
fun make_digit digit =
if digit >= 10 then chr (ord #"A" + digit - 10)
else chr (ord #"0" + digit)
fun makedigits (0w0,acc) = acc
| makedigits (n,acc) =
let
val digit = toInt (n mod base)
val n' = n div base
in
makedigits (n',make_digit digit :: acc)
end
in
implode (makedigits (n,[]))
end
fun tobase StringCvt.BIN = 0w2 : word
| tobase StringCvt.OCT = 0w8 : word
| tobase StringCvt.DEC = 0w10 : word
| tobase StringCvt.HEX = 0w16 : word
fun fmt radix n =
makeString (tobase radix,n)
fun toString n = fmt StringCvt.HEX n
fun scan radix getc src =
let
val base = tobase radix
val ibase = toInt base
val rbase = real ibase
fun skip_prefix src =
case getc src of
SOME (#"0",src') =>
(case radix of
StringCvt.HEX =>
(case getc src' of
SOME (#"w",src'') =>
(case getc src'' of
SOME (#"x",src''') => src'''
| SOME (#"X",src''') => src'''
| _ => src)
| SOME (#"x",src'') => src''
| SOME (#"X",src'') => src''
| _ => src)
| _ =>
(case getc src' of
SOME (#"w",src'') => src''
| _ => src))
| _ => src
fun isDigit a =
if ibase <= 10
then
a >= ord #"0" andalso
a < ord #"0" + ibase
else
(a >= ord #"0" andalso a < ord #"0" + 10) orelse
(a >= ord #"A" andalso a < ord #"A" + ibase - 10) orelse
(a >= ord #"a" andalso a < ord #"a" + ibase - 10)
exception Valof
fun valof n =
if n >= ord #"0" andalso n <= ord #"9"
then n - ord #"0"
else if n >= ord #"a" andalso n <= ord #"z"
then n - ord #"a" + 10
else if n >= ord #"A" andalso n <= ord #"Z"
then n - ord #"A" + 10
else raise Valof
fun convert_digits s =
let
fun convert ([],acc) = acc
| convert (c :: rest,acc) =
convert (rest,acc * rbase + real (valof c))
val x = convert (map ord (explode s), 0.0)
in
if x >= realmax
then raise Overflow
else fromReal x
end
val src = skip_prefix (StringCvt.skipWS getc src)
in
case StringCvt.splitl (isDigit o ord) getc src of
("",src) => NONE
| (digits,src) =>
SOME (convert_digits digits,src)
end
val fromString = StringCvt.scanString (scan StringCvt.HEX)
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
val orb = MLWorks.Internal.Word32.word32_orb
val xorb = MLWorks.Internal.Word32.word32_xorb
val andb = MLWorks.Internal.Word32.word32_andb
val notb = MLWorks.Internal.Word32.word32_notb
val << = MLWorks.Internal.Word32.word32_lshift
val >> = MLWorks.Internal.Word32.word32_rshift
val ~>> = MLWorks.Internal.Word32.word32_arshift
fun max (a,b) = if a > b then a else b
fun min (a,b) = if a < b then a else b
end;
structure PreLargeWord = PreWord32;
