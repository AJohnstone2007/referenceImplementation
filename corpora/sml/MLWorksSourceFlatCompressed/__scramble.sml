require "scramble";
require "$.basis.__position";
require "$.basis.__bin_prim_io";
require "$.basis.__bin_io";
require "$.basis.__io";
require "$.basis.__word8_array";
require "$.basis.__word8_vector";
require "$.basis.__word8";
structure Scramble : SCRAMBLE =
struct
exception Prim
val emptyBuf = Word8Vector.fromList []
fun jumpTo (strm, pos) =
let
val (rdr as BinPrimIO.RD {setPos = setPos, ...}, _) =
BinIO.StreamIO.getReader strm
in
case setPos of
NONE => raise Prim
| SOME doSetPos =>
(doSetPos pos;
BinIO.StreamIO.mkInstream (rdr, emptyBuf))
end
fun getSize strm =
let
val (rdr as BinPrimIO.RD {endPos = endPos, ...}, buf) =
BinIO.StreamIO.getReader strm
val rebuiltStrm = BinIO.StreamIO.mkInstream (rdr, buf)
in
case endPos of
NONE => raise Prim
| SOME getEndPos => (getEndPos (), rebuiltStrm)
end
fun toPosFn f size n =
Position.fromInt (f (Position.toInt size) (Position.toInt n))
fun scrambleStream (f, instrm, outstrm, outPos, size) =
if BinPrimIO.compare (outPos, size) <> LESS then
()
else
let
val jumpPos = f size outPos
val newInstrm = jumpTo (instrm, jumpPos)
in
case BinIO.StreamIO.input1 newInstrm of
NONE => raise Subscript
| SOME (byte, _) =>
(BinIO.StreamIO.output1 (outstrm, byte);
scrambleStream (f, newInstrm, outstrm,
Position.+ (outPos, 1), size))
end
fun scramble f (inFile, outFile) =
let
val instrm = BinIO.getInstream (BinIO.openIn inFile)
val outstrm = BinIO.getOutstream (BinIO.openOut outFile)
val (size, instrm) = getSize instrm
in
(scrambleStream (toPosFn f, instrm, outstrm, Position.fromInt 0, size);
print "File scrambled.\n";
BinIO.StreamIO.closeIn instrm;
BinIO.StreamIO.closeOut outstrm)
end
handle IO.Io _ => print "File error.\n"
| Prim => print "Primitive IO does not support required functions.\n"
| Subscript => print "Permutation function out of range.\n"
fun outputArray (strm, arr) =
let
val vec = Word8Array.extract (arr, 0, NONE)
in
BinIO.StreamIO.output (strm, vec)
end
fun unscrambleStream (f, instrm, outstrm, i, size, buffer) =
case BinIO.StreamIO.input1 instrm of
NONE => outputArray (outstrm, buffer)
| SOME (byte, nextStrm) =>
(Word8Array.update (buffer, f size i, byte);
unscrambleStream (f, nextStrm, outstrm, i + 1, size, buffer))
fun unscramble f (inFile, outFile) =
let
val instrm = BinIO.getInstream (BinIO.openIn inFile)
val outstrm = BinIO.getOutstream (BinIO.openOut outFile)
val (sizePos, instrm) = getSize instrm
val size = Position.toInt sizePos
val newBuffer = Word8Array.array (size, Word8.fromInt 0)
in
(unscrambleStream (f, instrm, outstrm, 0, size, newBuffer);
print "File unscrambled.\n";
BinIO.StreamIO.closeIn instrm;
BinIO.StreamIO.closeOut outstrm)
end
handle IO.Io _ => print "File error.\n"
| Prim => print "Primitive IO does not support required functions.\n"
| Subscript => print "Permutation function out of range.\n"
| Size => print "File too large.\n"
fun rotate (k : int) =
fn size => fn m => (m + k) mod size
val reverse =
fn size => fn m => size - m - 1
val shuffle =
fn size => fn m =>
let
val split = (size + 1) div 2
in
if m < split then
m * 2
else
(m - split) * 2 + 1
end
val minShuffles = 3
val maxShuffles = 10
fun mix key =
fn size => fn m =>
let
fun shuffleN (i, 0) = i
| shuffleN (i, n) =
shuffleN (shuffle size (rotate key size i), n - 1)
in
shuffleN (m, minShuffles + key mod (maxShuffles - minShuffles + 1))
end
end
;
