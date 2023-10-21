require "$.basis.char";
require "$.basis.__char";
require "$.basis.__int";
require "$.basis.__int16";
require "$.basis.__int32";
require "$.basis.__word";
require "$.basis.__word8";
require "$.basis.__word16";
require "$.basis.__word32";
require "$.basis.__sys_word";
require "$.basis.__math";
require "$.basis.__real";
require "mlworks_c_interface";
structure MLWorksCInterface :> MLWORKS_C_INTERFACE =
struct
val env = MLWorks.Internal.Runtime.environment
type syserror = MLWorks.Internal.Error.syserror
exception SysErr = MLWorks.Internal.Error.SysErr
datatype void = VOID
datatype 'a ptr = PTR of SysWord.word
val null = PTR 0w0
fun ptrToRep (PTR addr) = addr
fun fromVoidPtr (PTR addr) = PTR addr
fun toVoidPtr (PTR addr) = PTR addr
fun applyPair (g, h) (x, y) = (g x, h y)
val wTsw = SysWord.fromLargeWord o Word.toLargeWord
val deRef8 : void ptr -> Word8.word = env "C.VoidPtr.deRef8"
val deRef16 : void ptr -> Word16.word = env "C.VoidPtr.deRef16"
val deRef32 : void ptr -> Word32.word = env "C.VoidPtr.deRef32"
val deRefReal : void ptr -> Real.real = env "C.VoidPtr.deRefReal"
val update8 : void ptr * Word8.word -> unit = env "C.VoidPtr.update8"
val update16 : void ptr * Word16.word -> unit = env "C.VoidPtr.update16"
val update32 : void ptr * Word32.word -> unit = env "C.VoidPtr.update32"
val updateReal : void ptr * Real.real -> unit = env "C.VoidPtr.updateReal"
val malloc : Word.word -> void ptr = env "C.Memory.malloc"
val memcpy : {source: void ptr, dest: void ptr, size: Word.word} -> unit = env "C.Memory.copy"
val free : void ptr -> unit = env "C.Memory.free"
val toSysWord : void ptr -> SysWord.word = ptrToRep
val fromSysWord : SysWord.word -> void ptr = PTR
val next : void ptr * Word.word -> void ptr =
fromSysWord o SysWord.+ o applyPair (toSysWord, wTsw)
val prev : void ptr * Word.word -> void ptr =
fromSysWord o SysWord.- o applyPair (toSysWord, wTsw)
val ptrSize = 0w4
structure BasisChar = Char;
structure BasisInt = Int;
structure Char =
struct
datatype char = CHAR of BasisChar.char
type string = string
fun toRep (CHAR c) = c
fun pairToRep (CHAR a, CHAR b) = (a, b)
val maxOrd = BasisChar.maxOrd
val minChar = CHAR BasisChar.minChar
val maxChar = CHAR BasisChar.maxChar
val chr = CHAR o BasisChar.chr
val ord = BasisChar.ord o toRep
val succ = CHAR o BasisChar.succ o toRep
val pred = CHAR o BasisChar.pred o toRep
val op< = BasisChar.< o pairToRep
val op<= = BasisChar.<= o pairToRep
val op> = BasisChar.> o pairToRep
val op>= = BasisChar.>= o pairToRep
val compare = BasisChar.compare o pairToRep
val contains = fn s => BasisChar.contains s o toRep
val notContains = fn s => BasisChar.notContains s o toRep
val isLower = BasisChar.isLower o toRep
val isUpper = BasisChar.isUpper o toRep
val isDigit = BasisChar.isDigit o toRep
val isAlpha = BasisChar.isAlpha o toRep
val isAlphaNum = BasisChar.isAlphaNum o toRep
val isAscii = BasisChar.isAscii o toRep
val isSpace = BasisChar.isSpace o toRep
val isCntrl = BasisChar.isCntrl o toRep
val isGraph = BasisChar.isGraph o toRep
val isHexDigit = BasisChar.isHexDigit o toRep
val isPrint = BasisChar.isPrint o toRep
val isPunct = BasisChar.isPunct o toRep
val toLower = CHAR o BasisChar.toLower o toRep
val toUpper = CHAR o BasisChar.toUpper o toRep
fun fromString s =
case BasisChar.fromString s of
NONE => NONE
| SOME c => SOME (CHAR c)
val toString = BasisChar.toString o toRep
fun scan getChar =
let
fun getChar' input =
case getChar input of
NONE => NONE
| SOME (CHAR c, i) => SOME (c, i)
in
fn input =>
case BasisChar.scan getChar' input of
NONE => NONE
| SOME (x, input) => SOME (CHAR x, input)
end
fun fromCString s =
case BasisChar.fromCString s of
NONE => NONE
| SOME c => SOME (CHAR c)
val toCString = BasisChar.toCString o toRep
end
structure Uchar =
struct
datatype word = WORD of Word8.word
fun toRep (WORD a) = a
fun pairToRep (WORD a, WORD b) = (a, b)
fun leftToRep (WORD a, b) = (a, b)
val wordSize = Word8.wordSize
val toLargeWord = Word8.toLargeWord o toRep
val toLargeWordX = Word8.toLargeWordX o toRep
val fromLargeWord = WORD o Word8.fromLargeWord
val toLargeInt = Word8.toLargeInt o toRep
val toLargeIntX = Word8.toLargeIntX o toRep
val fromLargeInt = WORD o Word8.fromLargeInt
val toInt = Word8.toInt o toRep
val toIntX = Word8.toIntX o toRep
val fromInt = WORD o Word8.fromInt
val orb = WORD o Word8.orb o pairToRep
val xorb = WORD o Word8.xorb o pairToRep
val andb = WORD o Word8.andb o pairToRep
val notb = WORD o Word8.notb o toRep
val << = WORD o Word8.<< o leftToRep
val >> = WORD o Word8.>> o leftToRep
val ~>> = WORD o Word8.~>> o leftToRep
val op+ = WORD o Word8.+ o pairToRep
val op- = WORD o Word8.- o pairToRep
val op* = WORD o Word8.* o pairToRep
val op div = WORD o Word8.div o pairToRep
val op mod = WORD o Word8.mod o pairToRep
val compare = Word8.compare o pairToRep
val op> = Word8.> o pairToRep
val op>= = Word8.>= o pairToRep
val op< = Word8.< o pairToRep
val op<= = Word8.<= o pairToRep
val min = WORD o Word8.min o pairToRep
val max = WORD o Word8.max o pairToRep
fun fmt radix = Word8.fmt radix o toRep
val toString = Word8.toString o toRep
fun fromString s =
case Word8.fromString s of
NONE => NONE
| SOME x => SOME (WORD x)
fun scan radix reader input =
case Word8.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (WORD x, input)
end
structure Short =
struct
datatype int = INT of Int16.int
fun toRep (INT a) = a
fun pairToRep (INT a, INT b) = (a, b)
val toLarge : int -> LargeInt.int = Int16.toLarge o toRep
val fromLarge : LargeInt.int -> int = INT o Int16.fromLarge
val toInt : int -> BasisInt.int = Int16.toInt o toRep
val fromInt : BasisInt.int -> int = INT o Int16.fromInt
val precision : BasisInt.int option = Int16.precision
val maxInt : int option =
case Int16.maxInt of
NONE => NONE
| SOME x => SOME (INT x)
val minInt : int option =
case Int16.minInt of
NONE => NONE
| SOME x => SOME (INT x)
val ~ : int -> int = INT o Int16.~ o toRep
val op* : int * int -> int = INT o Int16.* o pairToRep
val op div : int * int -> int = INT o Int16.div o pairToRep
val op mod : int * int -> int = INT o Int16.mod o pairToRep
val quot : int * int -> int = INT o Int16.quot o pairToRep
val rem : int * int -> int = INT o Int16.rem o pairToRep
val op+ : int * int -> int = INT o Int16.+ o pairToRep
val op- : int * int -> int = INT o Int16.- o pairToRep
val compare : int * int -> order = Int16.compare o pairToRep
val op< : int * int -> bool = Int16.< o pairToRep
val op>= : int * int -> bool = Int16.>= o pairToRep
val op> : int * int -> bool = Int16.< o pairToRep
val op<= : int * int -> bool = Int16.<= o pairToRep
val abs : int -> int = INT o Int16.abs o toRep
val min : int * int -> int = INT o Int16.min o pairToRep
val max : int * int -> int = INT o Int16.max o pairToRep
val sign : int -> BasisInt.int = Int16.sign o toRep
val sameSign : int * int -> bool = Int16.sameSign o pairToRep
fun fmt radix = Int16.fmt radix o toRep
val toString : int -> string = Int16.toString o toRep
fun fromString s =
case Int16.fromString s of
NONE => NONE
| SOME x => SOME (INT x)
fun scan radix reader input =
case Int16.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (INT x, input)
end
structure Ushort =
struct
datatype word = WORD of Word16.word
fun toRep (WORD a) = a
fun pairToRep (WORD a, WORD b) = (a, b)
fun leftToRep (WORD a, b) = (a, b)
val wordSize = Word16.wordSize
val toLargeWord = Word16.toLargeWord o toRep
val toLargeWordX = Word16.toLargeWordX o toRep
val fromLargeWord = WORD o Word16.fromLargeWord
val toLargeInt = Word16.toLargeInt o toRep
val toLargeIntX = Word16.toLargeIntX o toRep
val fromLargeInt = WORD o Word16.fromLargeInt
val toInt = Word16.toInt o toRep
val toIntX = Word16.toIntX o toRep
val fromInt = WORD o Word16.fromInt
val orb = WORD o Word16.orb o pairToRep
val xorb = WORD o Word16.xorb o pairToRep
val andb = WORD o Word16.andb o pairToRep
val notb = WORD o Word16.notb o toRep
val << = WORD o Word16.<< o leftToRep
val >> = WORD o Word16.>> o leftToRep
val ~>> = WORD o Word16.~>> o leftToRep
val op+ = WORD o Word16.+ o pairToRep
val op- = WORD o Word16.- o pairToRep
val op* = WORD o Word16.* o pairToRep
val op div = WORD o Word16.div o pairToRep
val op mod = WORD o Word16.mod o pairToRep
val compare = Word16.compare o pairToRep
val op> = Word16.> o pairToRep
val op>= = Word16.>= o pairToRep
val op< = Word16.< o pairToRep
val op<= = Word16.<= o pairToRep
val min = WORD o Word16.min o pairToRep
val max = WORD o Word16.max o pairToRep
fun fmt radix = Word16.fmt radix o toRep
val toString = Word16.toString o toRep
fun fromString s =
case Word16.fromString s of
NONE => NONE
| SOME x => SOME (WORD x)
fun scan radix reader input =
case Word16.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (WORD x, input)
end
structure Int =
struct
datatype int = INT of Int32.int
fun toRep (INT a) = a
fun pairToRep (INT a, INT b) = (a, b)
val toLarge : int -> LargeInt.int = Int32.toLarge o toRep
val fromLarge : LargeInt.int -> int = INT o Int32.fromLarge
val toInt : int -> BasisInt.int = Int32.toInt o toRep
val fromInt : BasisInt.int -> int = INT o Int32.fromInt
val precision : BasisInt.int option = Int32.precision
val maxInt : int option =
case Int32.maxInt of
NONE => NONE
| SOME x => SOME (INT x)
val minInt : int option =
case Int32.minInt of
NONE => NONE
| SOME x => SOME (INT x)
val ~ : int -> int = INT o Int32.~ o toRep
val op* : int * int -> int = INT o Int32.* o pairToRep
val op div : int * int -> int = INT o Int32.div o pairToRep
val op mod : int * int -> int = INT o Int32.mod o pairToRep
val quot : int * int -> int = INT o Int32.quot o pairToRep
val rem : int * int -> int = INT o Int32.rem o pairToRep
val op+ : int * int -> int = INT o Int32.+ o pairToRep
val op- : int * int -> int = INT o Int32.- o pairToRep
val compare : int * int -> order = Int32.compare o pairToRep
val op< : int * int -> bool = Int32.< o pairToRep
val op>= : int * int -> bool = Int32.>= o pairToRep
val op> : int * int -> bool = Int32.< o pairToRep
val op<= : int * int -> bool = Int32.<= o pairToRep
val abs : int -> int = INT o Int32.abs o toRep
val min : int * int -> int = INT o Int32.min o pairToRep
val max : int * int -> int = INT o Int32.max o pairToRep
val sign : int -> BasisInt.int = Int32.sign o toRep
val sameSign : int * int -> bool = Int32.sameSign o pairToRep
fun fmt radix = Int32.fmt radix o toRep
val toString : int -> string = Int32.toString o toRep
fun fromString s =
case Int32.fromString s of
NONE => NONE
| SOME x => SOME (INT x)
fun scan radix reader input =
case Int32.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (INT x, input)
end
structure Uint =
struct
datatype word = WORD of Word32.word
fun toRep (WORD a) = a
fun pairToRep (WORD a, WORD b) = (a, b)
fun leftToRep (WORD a, b) = (a, b)
val wordSize = Word32.wordSize
val toLargeWord = Word32.toLargeWord o toRep
val toLargeWordX = Word32.toLargeWordX o toRep
val fromLargeWord = WORD o Word32.fromLargeWord
val toLargeInt = Word32.toLargeInt o toRep
val toLargeIntX = Word32.toLargeIntX o toRep
val fromLargeInt = WORD o Word32.fromLargeInt
val toInt = Word32.toInt o toRep
val toIntX = Word32.toIntX o toRep
val fromInt = WORD o Word32.fromInt
val orb = WORD o Word32.orb o pairToRep
val xorb = WORD o Word32.xorb o pairToRep
val andb = WORD o Word32.andb o pairToRep
val notb = WORD o Word32.notb o toRep
val << = WORD o Word32.<< o leftToRep
val >> = WORD o Word32.>> o leftToRep
val ~>> = WORD o Word32.~>> o leftToRep
val op+ = WORD o Word32.+ o pairToRep
val op- = WORD o Word32.- o pairToRep
val op* = WORD o Word32.* o pairToRep
val op div = WORD o Word32.div o pairToRep
val op mod = WORD o Word32.mod o pairToRep
val compare = Word32.compare o pairToRep
val op> = Word32.> o pairToRep
val op>= = Word32.>= o pairToRep
val op< = Word32.< o pairToRep
val op<= = Word32.<= o pairToRep
val min = WORD o Word32.min o pairToRep
val max = WORD o Word32.max o pairToRep
fun fmt radix = Word32.fmt radix o toRep
val toString = Word32.toString o toRep
fun fromString s =
case Word32.fromString s of
NONE => NONE
| SOME x => SOME (WORD x)
fun scan radix reader input =
case Word32.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (WORD x, input)
end
structure Long =
struct
datatype int = INT of Int32.int
fun toRep (INT a) = a
fun pairToRep (INT a, INT b) = (a, b)
val toLarge : int -> LargeInt.int = Int32.toLarge o toRep
val fromLarge : LargeInt.int -> int = INT o Int32.fromLarge
val toInt : int -> BasisInt.int = Int32.toInt o toRep
val fromInt : BasisInt.int -> int = INT o Int32.fromInt
val precision : BasisInt.int option = Int32.precision
val maxInt : int option =
case Int32.maxInt of
NONE => NONE
| SOME x => SOME (INT x)
val minInt : int option =
case Int32.minInt of
NONE => NONE
| SOME x => SOME (INT x)
val ~ : int -> int = INT o Int32.~ o toRep
val op* : int * int -> int = INT o Int32.* o pairToRep
val op div : int * int -> int = INT o Int32.div o pairToRep
val op mod : int * int -> int = INT o Int32.mod o pairToRep
val quot : int * int -> int = INT o Int32.quot o pairToRep
val rem : int * int -> int = INT o Int32.rem o pairToRep
val op+ : int * int -> int = INT o Int32.+ o pairToRep
val op- : int * int -> int = INT o Int32.- o pairToRep
val compare : int * int -> order = Int32.compare o pairToRep
val op< : int * int -> bool = Int32.< o pairToRep
val op>= : int * int -> bool = Int32.>= o pairToRep
val op> : int * int -> bool = Int32.< o pairToRep
val op<= : int * int -> bool = Int32.<= o pairToRep
val abs : int -> int = INT o Int32.abs o toRep
val min : int * int -> int = INT o Int32.min o pairToRep
val max : int * int -> int = INT o Int32.max o pairToRep
val sign : int -> BasisInt.int = Int32.sign o toRep
val sameSign : int * int -> bool = Int32.sameSign o pairToRep
fun fmt radix = Int32.fmt radix o toRep
val toString : int -> string = Int32.toString o toRep
fun fromString s =
case Int32.fromString s of
NONE => NONE
| SOME x => SOME (INT x)
fun scan radix reader input =
case Int32.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (INT x, input)
end
structure Ulong =
struct
datatype word = WORD of Word32.word
fun toRep (WORD a) = a
fun pairToRep (WORD a, WORD b) = (a, b)
fun leftToRep (WORD a, b) = (a, b)
val wordSize = Word32.wordSize
val toLargeWord = Word32.toLargeWord o toRep
val toLargeWordX = Word32.toLargeWordX o toRep
val fromLargeWord = WORD o Word32.fromLargeWord
val toLargeInt = Word32.toLargeInt o toRep
val toLargeIntX = Word32.toLargeIntX o toRep
val fromLargeInt = WORD o Word32.fromLargeInt
val toInt = Word32.toInt o toRep
val toIntX = Word32.toIntX o toRep
val fromInt = WORD o Word32.fromInt
val orb = WORD o Word32.orb o pairToRep
val xorb = WORD o Word32.xorb o pairToRep
val andb = WORD o Word32.andb o pairToRep
val notb = WORD o Word32.notb o toRep
val << = WORD o Word32.<< o leftToRep
val >> = WORD o Word32.>> o leftToRep
val ~>> = WORD o Word32.~>> o leftToRep
val op+ = WORD o Word32.+ o pairToRep
val op- = WORD o Word32.- o pairToRep
val op* = WORD o Word32.* o pairToRep
val op div = WORD o Word32.div o pairToRep
val op mod = WORD o Word32.mod o pairToRep
val compare = Word32.compare o pairToRep
val op> = Word32.> o pairToRep
val op>= = Word32.>= o pairToRep
val op< = Word32.< o pairToRep
val op<= = Word32.<= o pairToRep
val min = WORD o Word32.min o pairToRep
val max = WORD o Word32.max o pairToRep
fun fmt radix = Word32.fmt radix o toRep
val toString = Word32.toString o toRep
fun fromString s =
case Word32.fromString s of
NONE => NONE
| SOME x => SOME (WORD x)
fun scan radix reader input =
case Word32.scan radix reader input of
NONE => NONE
| SOME (x, input) => SOME (WORD x, input)
end
structure Double =
struct
datatype real = REAL of Real.real
fun toRep (REAL a) = a
fun pairToRep (REAL a, REAL b) = (a, b)
fun tripleToRep (REAL a, REAL b, REAL c) = (a, b, c)
fun leftToRep (REAL a, b) = (a, b)
structure Math =
struct
type real = real
val pi = REAL Math.pi
val e = REAL Math.e
val sqrt = REAL o Math.sqrt o toRep
val sin = REAL o Math.sin o toRep
val cos = REAL o Math.cos o toRep
val tan = REAL o Math.tan o toRep
val asin = REAL o Math.asin o toRep
val acos = REAL o Math.acos o toRep
val atan = REAL o Math.atan o toRep
val atan2 = REAL o Math.atan2 o pairToRep
val exp = REAL o Math.exp o toRep
val pow = REAL o Math.pow o pairToRep
val ln = REAL o Math.ln o toRep
val log10 = REAL o Math.log10 o toRep
val sinh = REAL o Math.sinh o toRep
val cosh = REAL o Math.cosh o toRep
val tanh = REAL o Math.tanh o toRep
end
val radix = Real.radix
val precision = Real.precision
val maxFinite = REAL Real.maxFinite
val minPos = REAL Real.minPos
val minNormalPos = REAL Real.minNormalPos
val posInf = REAL Real.posInf
val negInf = REAL Real.negInf
val op+ = REAL o Real.+ o pairToRep
val op- = REAL o Real.- o pairToRep
val op* = REAL o Real.* o pairToRep
val op/ = REAL o Real./ o pairToRep
val *+ = REAL o Real.*+ o tripleToRep
val *- = REAL o Real.*- o tripleToRep
val ~ = REAL o Real.~ o toRep
val abs = REAL o Real.abs o toRep
val min = REAL o Real.min o pairToRep
val max = REAL o Real.max o pairToRep
val sign = Real.sign o toRep
val signBit = Real.signBit o toRep
val sameSign = Real.sameSign o pairToRep
val copySign = REAL o Real.copySign o pairToRep
val compare = Real.compare o pairToRep
val compareReal = Real.compareReal o pairToRep
val op> = Real.> o pairToRep
val op>= = Real.>= o pairToRep
val op< = Real.< o pairToRep
val op<= = Real.<= o pairToRep
val == = Real.== o pairToRep
val != = Real.!= o pairToRep
val ?= = Real.?= o pairToRep
val unordered = Real.unordered o pairToRep
val isFinite = Real.isFinite o toRep
val isNan = Real.isNan o toRep
val isNormal = Real.isNormal o toRep
val class = Real.class o toRep
fun fmt cvt = Real.fmt cvt o toRep
val toString = Real.toString o toRep
fun fromString s =
case Real.fromString s of
NONE => NONE
| SOME x => SOME (REAL x)
fun scan reader input =
case Real.scan reader input of
NONE => NONE
| SOME (x, input) => SOME (REAL x, input)
fun toManExp (REAL r) =
let
val {man, exp} = Real.toManExp r
in
{man = REAL man, exp = exp}
end
fun fromManExp {exp, man = REAL man} =
REAL (Real.fromManExp {exp= exp, man= man})
fun split (REAL r) =
let
val {whole, frac} = Real.split r
in
{whole = REAL whole, frac = REAL frac}
end
val realMod = REAL o Real.realMod o toRep
val rem = REAL o Real.rem o pairToRep
val nextAfter = REAL o Real.nextAfter o pairToRep
val checkFloat = REAL o Real.checkFloat o toRep
val realFloor = REAL o Real.realFloor o toRep
val realCeil = REAL o Real.realCeil o toRep
val realTrunc = REAL o Real.realTrunc o toRep
val floor = Real.floor o toRep
val ceil = Real.ceil o toRep
val trunc = Real.trunc o toRep
val round = Real.round o toRep
fun toInt rm = Real.toInt rm o toRep
fun toLargeInt rm = Real.toLargeInt rm o toRep
val fromInt = REAL o Real.fromInt
val fromLargeInt = REAL o Real.fromLargeInt
val toLarge = Real.toLarge o toRep
fun fromLarge rm = REAL o Real.fromLarge rm
val toDecimal = Real.toDecimal o toRep
val fromDecimal = REAL o Real.fromDecimal
end
val i32Tw32 = Word32.fromLargeInt o Int32.toLarge
val w32Ti32 = Int32.fromLarge o Word32.toLargeInt
val i16Tw16 = Word16.fromLargeInt o Int16.toLarge
val w16Ti16 = Int16.fromLarge o Word16.toLargeInt
fun applyPair (g, h) (x, y) = (g x, h y)
structure CharPtr =
struct
type 'a ptr = 'a ptr
type value = Char.char
val toVoidPtr : Char.char ptr -> void ptr = MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Char.char ptr = MLWorks.Internal.Value.cast
val op := : Char.char ptr * Char.char -> unit =
update8 o applyPair (toVoidPtr, Word8.fromInt o Char.ord)
val ! : Char.char ptr -> Char.char =
Char.chr o Word8.toInt o deRef8 o toVoidPtr
val charSize : Word.word = 0w1
fun make (): Char.char ptr = fromVoidPtr (malloc charSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*charSize))
fun scale x = charSize * x
val next : Char.char ptr * Word.word -> Char.char ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Char.char ptr * Word.word -> Char.char ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Char.char ptr -> unit = free o toVoidPtr
val fromString : string -> Char.char ptr = env "C.CharPtr.fromString"
val copySubString : Char.char ptr * Word.word -> string =
env "C.CharPtr.toStringN"
val size = ptrSize
end
structure ShortPtr =
struct
type 'a ptr = 'a ptr
type value = Short.int
val toVoidPtr : Short.int ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Short.int ptr =
MLWorks.Internal.Value.cast
val op := : Short.int ptr * Short.int -> unit =
update16 o applyPair (toVoidPtr, i16Tw16 o Short.toRep)
val ! : Short.int ptr -> Short.int =
Short.INT o w16Ti16 o deRef16 o toVoidPtr
val intSize : Word.word = 0w2
fun make (): Short.int ptr = fromVoidPtr (malloc intSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*intSize))
fun scale x = intSize * x
val next : Short.int ptr * Word.word -> Short.int ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Short.int ptr * Word.word -> Short.int ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Short.int ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure IntPtr =
struct
type 'a ptr = 'a ptr
type value = Int.int
val toVoidPtr : Int.int ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Int.int ptr =
MLWorks.Internal.Value.cast
val op := : Int.int ptr * Int.int -> unit =
update32 o applyPair (toVoidPtr, i32Tw32 o Int.toRep)
val ! : Int.int ptr -> Int.int =
Int.INT o w32Ti32 o deRef32 o toVoidPtr
val intSize : Word.word = 0w4
fun make (): Int.int ptr = fromVoidPtr (malloc intSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*intSize))
fun scale x = intSize * x
val next : Int.int ptr * Word.word -> Int.int ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Int.int ptr * Word.word -> Int.int ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Int.int ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure LongPtr =
struct
type 'a ptr = 'a ptr
type value = Long.int
val toVoidPtr : Long.int ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Long.int ptr =
MLWorks.Internal.Value.cast
val op := : Long.int ptr * Long.int -> unit =
update32 o applyPair (toVoidPtr, i32Tw32 o Long.toRep)
val ! : Long.int ptr -> Long.int =
Long.INT o w32Ti32 o deRef32 o toVoidPtr
val intSize : Word.word = 0w4
fun make () : Long.int ptr = fromVoidPtr (malloc intSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*intSize))
fun scale x = intSize * x
val next : Long.int ptr * Word.word -> Long.int ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Long.int ptr * Word.word -> Long.int ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Long.int ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure UcharPtr =
struct
type 'a ptr = 'a ptr
type value = Uchar.word
val toVoidPtr : Uchar.word ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Uchar.word ptr =
MLWorks.Internal.Value.cast
val op := : Uchar.word ptr * Uchar.word -> unit =
update8 o applyPair (toVoidPtr, Uchar.toRep)
val ! : Uchar.word ptr -> Uchar.word =
Uchar.WORD o deRef8 o toVoidPtr
val wordSize : Word.word = 0w1
fun make (): Uchar.word ptr = fromVoidPtr (malloc wordSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*wordSize))
fun scale x = wordSize * x
val next : Uchar.word ptr * Word.word -> Uchar.word ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Uchar.word ptr * Word.word -> Uchar.word ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Uchar.word ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure UshortPtr =
struct
type 'a ptr = 'a ptr
type value = Ushort.word
val toVoidPtr : Ushort.word ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Ushort.word ptr =
MLWorks.Internal.Value.cast
val op := : Ushort.word ptr * Ushort.word -> unit =
update16 o applyPair (toVoidPtr, Ushort.toRep)
val ! : Ushort.word ptr -> Ushort.word =
Ushort.WORD o deRef16 o toVoidPtr
val wordSize : Word.word = 0w2
fun make (): Ushort.word ptr = fromVoidPtr (malloc wordSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*wordSize))
fun scale x = wordSize * x
val next : Ushort.word ptr * Word.word -> Ushort.word ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Ushort.word ptr * Word.word -> Ushort.word ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Ushort.word ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure UintPtr =
struct
type 'a ptr = 'a ptr
type value = Uint.word
val toVoidPtr : Uint.word ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Uint.word ptr =
MLWorks.Internal.Value.cast
val op := : Uint.word ptr * Uint.word -> unit =
update32 o applyPair (toVoidPtr, Uint.toRep)
val ! : Uint.word ptr -> Uint.word =
Uint.WORD o deRef32 o toVoidPtr
val wordSize : Word.word = 0w4
fun make (): Uint.word ptr = fromVoidPtr (malloc wordSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*wordSize))
fun scale x = wordSize * x
val next : Uint.word ptr * Word.word -> Uint.word ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Uint.word ptr * Word.word -> Uint.word ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Uint.word ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure UlongPtr =
struct
type 'a ptr = 'a ptr
type value = Ulong.word
val toVoidPtr : Ulong.word ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Ulong.word ptr =
MLWorks.Internal.Value.cast
val op := : Ulong.word ptr * Ulong.word -> unit =
update32 o applyPair (toVoidPtr, Ulong.toRep)
val ! : Ulong.word ptr -> Ulong.word =
Ulong.WORD o deRef32 o toVoidPtr
val wordSize : Word.word = 0w4
fun make (): Ulong.word ptr = fromVoidPtr (malloc wordSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*wordSize))
fun scale x = wordSize * x
val next : Ulong.word ptr * Word.word -> Ulong.word ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Ulong.word ptr * Word.word -> Ulong.word ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Ulong.word ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure DoublePtr =
struct
type 'a ptr = 'a ptr
type value = Double.real
val toVoidPtr : Double.real ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> Double.real ptr =
MLWorks.Internal.Value.cast
val op := : Double.real ptr * Double.real -> unit =
updateReal o applyPair (toVoidPtr, Double.toRep)
val ! : Double.real ptr -> Double.real =
Double.REAL o deRefReal o toVoidPtr
val wordSize : Word.word = 0w8
fun make (): Double.real ptr = fromVoidPtr (malloc wordSize)
fun makeArray n =
fromVoidPtr (malloc ((Word.fromInt n)*wordSize))
fun scale x = wordSize * x
val next : Double.real ptr * Word.word -> Double.real ptr =
fromVoidPtr o next o applyPair (toVoidPtr, scale)
val prev : Double.real ptr * Word.word -> Double.real ptr =
fromVoidPtr o prev o applyPair (toVoidPtr, scale)
val free : Double.real ptr -> unit = free o toVoidPtr
val size = ptrSize
end
structure PtrPtr =
struct
val ptrPtrToVoidPtr : 'a ptr ptr -> void ptr =
MLWorks.Internal.Value.cast
val fromVoidPtr : void ptr -> 'a ptr =
MLWorks.Internal.Value.cast
fun op := (addr: 'a ptr ptr, v: 'a ptr) =
update32 (ptrPtrToVoidPtr addr, toSysWord (toVoidPtr v))
fun ! (addr: 'a ptr ptr): 'a ptr =
fromVoidPtr (fromSysWord (deRef32 (ptrPtrToVoidPtr addr)))
end
end
;
