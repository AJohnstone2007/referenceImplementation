require "^.basis.__word8";
require "^.basis.__word8_array";
require "^.basis.__word8_vector";
require "^.basis.__char";
require "^.basis.__char_array";
require "^.basis.__char_vector";
require "^.basis.os_prim_io";
require "^.basis.__text_prim_io";
require "^.basis.__bin_prim_io";
require "^.basis.__io";
require "__win32os";
require "__win32";
structure OSPrimIO : OS_PRIM_IO =
struct
type bin_reader = BinPrimIO.reader
type bin_writer = BinPrimIO.writer
type text_reader = TextPrimIO.reader
type text_writer = TextPrimIO.writer
type file_desc = Win32_.file_desc
fun fdToIOD fd =
SOME (Win32_.fdToIOD fd)
handle MLWorks.Internal.Error.SysErr _ => NONE
fun mkWin32Reader
{fd: file_desc, name: string, initialPos: BinPrimIO.pos,
checkSize : bool} =
let
val pos = ref initialPos
val stringsize = size
val blksize = 1024
val size = if checkSize then Win32OS_.size fd else 1
val ioDesc = fdToIOD fd
in
BinPrimIO.augmentReader
(BinPrimIO.RD
{readVec =
SOME (fn i =>
(let val result = Win32OS_.read (fd, i)
in
pos := !pos + Word8Vector.length result;
result
end)),
readVecNB = NONE,
readArr = NONE,
readArrNB = NONE,
block = NONE,
canInput = NONE,
avail = fn()=>NONE,
name = name,
chunkSize = blksize,
close =
fn () => (case ioDesc of
NONE => Win32OS_.close fd
| SOME iod => Win32_.closeIOD iod),
getPos = SOME(fn () => !pos),
setPos = SOME(fn newPos =>
(ignore(Win32OS_.seek (fd, newPos,Win32OS_.FROM_BEGIN));
pos := newPos)),
endPos = SOME(fn () => size),
verifyPos = NONE,
ioDesc = ioDesc})
end
fun openRd filename =
mkWin32Reader
{fd = Win32OS_.open_ (filename,
Win32OS_.READ,
Win32OS_.OPEN_EXISTING),
name = filename,
initialPos = 0, checkSize = true}
fun mkWin32Writer
{fd: file_desc, name: string, blocksize: int, size: int,
initialPos: BinPrimIO.pos} =
let
val pos = ref initialPos
val ioDesc = fdToIOD fd
in
BinPrimIO.augmentWriter
(BinPrimIO.WR
{writeVec =
SOME (fn {buf, i, sz} =>
(let
val nelems = case sz of
SOME i => i
| NONE => Word8Vector.length buf - i
val result = Win32OS_.write (fd, buf, i, nelems)
in
pos := !pos + result;
result
end)),
writeVecNB = NONE,
writeArrNB = NONE,
writeArr = NONE,
block = NONE,
canOutput = NONE,
name = name,
chunkSize = blocksize,
close =
fn () => (case ioDesc of
NONE => Win32OS_.close fd
| SOME iod => Win32_.closeIOD iod),
getPos = SOME(fn () => !pos),
setPos = SOME(fn newPos =>
(ignore(Win32OS_.seek (fd, newPos,Win32OS_.FROM_BEGIN));
pos := newPos)),
endPos = SOME(fn () => size),
verifyPos = NONE,
ioDesc = ioDesc})
end
fun openWr filename =
let
val fd = Win32OS_.open_ (filename, Win32OS_.WRITE, Win32OS_.CREATE_ALWAYS)
val blksize = 1024
val size = Win32OS_.size fd
in
mkWin32Writer
{fd = fd,
name = filename,
blocksize = blksize,
size = size,
initialPos = 0}
end
fun openApp filename =
let
val fd = Win32OS_.open_ (filename, Win32OS_.READ_WRITE,
Win32OS_.OPEN_ALWAYS)
val _ = Win32OS_.seek (fd, 0, Win32OS_.FROM_END)
val blksize = 1024
val size = Win32OS_.size fd
in
mkWin32Writer
{fd = fd,
name = filename,
blocksize = blksize,
size = size,
initialPos = size}
end
local
open MLWorks.Internal.StandardIO
val cast = MLWorks.Internal.Value.cast
in
val stdIn = BinPrimIO.augmentReader
(BinPrimIO.RD
{readVec = SOME (fn i => cast (#get(#input(currentIO()))) i),
readVecNB =NONE,
readArr = NONE,
readArrNB = NONE,
block = NONE,
canInput = SOME(fn ()=>valOf(#can_input(#input(currentIO()))) ()
handle Option => raise IO.RandomAccessNotSupported),
avail = fn()=>NONE,
name = "<stdIn>",
chunkSize = 1,
close = fn () =>
raise IO.Io
{name = "<stdIn>",
function = "close",
cause = Fail "Cannot close stdIn"},
getPos = SOME(fn ()=> valOf(#get_pos(#input(currentIO())))()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
setPos = SOME(fn newPos => valOf(#set_pos(#input(currentIO()))) newPos
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => ()),
endPos = NONE,
verifyPos = SOME(fn ()=> valOf(#get_pos(#input(currentIO())))()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
ioDesc = NONE})
val stdOut =
BinPrimIO.augmentWriter
(BinPrimIO.WR
{writeVec = SOME (fn s => #put(#output(currentIO())) (cast s)),
writeVecNB = NONE,
writeArrNB = NONE,
writeArr = NONE,
block = NONE,
canOutput = SOME(fn () => valOf(#can_output(#output(currentIO())))()
handle Option => true),
name = "<stdOut>",
chunkSize = 1,
close = fn () =>
raise IO.Io
{name = "<stdOut>",
function = "close",
cause = Fail "Cannot close stdOut"},
getPos = SOME(fn ()=> valOf(#get_pos(#output(currentIO())))()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
setPos = SOME(fn newPos => valOf(#set_pos(#output(currentIO()))) newPos
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => ()),
endPos = NONE,
verifyPos = SOME(fn ()=> valOf(#get_pos(#output(currentIO())))()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
ioDesc=NONE})
val stdErr =
BinPrimIO.augmentWriter
(BinPrimIO.WR
{writeVec = SOME (fn s => #put(#error(currentIO())) (cast s)),
writeVecNB = NONE,
writeArrNB = NONE,
writeArr = NONE,
block = NONE,
canOutput = SOME(fn ()=> valOf(#can_output(#error(currentIO())))()
handle Option => raise IO.RandomAccessNotSupported),
name = "<stdErr>",
chunkSize = 1,
close = fn () =>
raise IO.Io
{name = "<stdErr>",
function = "close",
cause = Fail "Cannot close stdErr"},
getPos = SOME(fn () => valOf(#get_pos(#error(currentIO()))) ()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
setPos = SOME(fn newPos => valOf(#set_pos(#error(currentIO()))) newPos
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => ()),
endPos = NONE,
verifyPos = SOME(fn () => valOf(#get_pos(#error(currentIO()))) ()
handle Option => raise IO.RandomAccessNotSupported
| MLWorks.Internal.Error.SysErr _ => 0),
ioDesc=NONE})
end
fun translateIn (x : BinPrimIO.reader) : TextPrimIO.reader =
let
val BinPrimIO.RD({name= n,
chunkSize=cS,
readVec=rV,
readArr=rA,
readVecNB=rVNB,
readArrNB=rANB,
block= b,
canInput=cI,
avail=av,
getPos=gP,
setPos=sP,
endPos=eP,
verifyPos=vP,
close=c,
ioDesc=iD}) = x
val CR = 13
local
fun removeCRinVector v f =
if (Word8Vector.length v) = 0 then
CharVector.fromList []
else
case (Word8Vector.foldr (fn (w,l)=>
let val i = (Word8.toInt w)
in if i=CR then l
else (Char.chr i)::l
end) [] v)
of [] => removeCRinVector (f()) f
| l => CharVector.fromList l
in
fun rVconv NONE = NONE
| rVconv (SOME f) = SOME(fn args => removeCRinVector (f args)
(fn () => f args))
fun rVNBconv NONE = NONE
| rVNBconv (SOME f) =
SOME(fn args => case (f args)
of NONE => NONE
| (SOME v) =>
let exception block
in
SOME(removeCRinVector v
(fn () => case (f args)
of NONE => raise block
| (SOME v) => v))
handle block => NONE
end)
end
local
fun removeCRinArray src size trg posn more=
let
fun removeCR i j s =
if i>= size then (size-s,j)
else let val k=(Word8.toInt(Word8Array.sub(src,i)))
in if k=CR then removeCR (i+1) j (size-1)
else (CharArray.update(trg,j,Char.chr k);
removeCR (i+1) (j+1) size)
end
in
if size=0 then 0 else
case (removeCR 0 posn size)
of (CRs,0)=>removeCRinArray
src (more()) trg
posn more
| (CRs,k)=> k-CRs
end
in
fun rAconv NONE=NONE
| rAconv (SOME f) =
SOME(fn (args as {buf=a,i=p,sz=s}) =>
let
val len = case s
of NONE=> CharArray.length a-p
| (SOME z)=> z
val b = Word8Array.array(len,Word8.fromInt 0)
in
removeCRinArray b (f {buf=b,i=0,sz=NONE}) a p
(fn () => f {buf=b,i=0,sz=NONE})
end)
fun rANBconv NONE=NONE
| rANBconv (SOME f) =
SOME(fn (args as {buf=a,i=p,sz=s}) =>
let
val len = case s
of NONE=> CharArray.length a-p
| (SOME z)=> z
val b = Word8Array.array(len,Word8.fromInt 0)
exception block
fun more () = case (f {buf=b,i=0,sz=NONE})
of NONE=> raise block
| (SOME k)=> k
in
SOME(removeCRinArray b (more ()) a p more)
handle block => NONE
end)
end
in
TextPrimIO.RD({name=n,
chunkSize=cS,
readVec=rVconv rV,
readArr=rAconv rA,
readVecNB=rVNBconv rVNB,
readArrNB=rANBconv rANB,
block= b,
canInput=cI,
avail=av,
getPos=gP,
setPos=sP,
endPos=eP,
verifyPos=vP,
close=c,
ioDesc=iD})
end
fun translateOut (x : BinPrimIO.writer) : TextPrimIO.writer =
let
val BinPrimIO.WR({name = n,
chunkSize=cS,
writeVec=wV,
writeArr=wA,
writeVecNB=wVNB,
writeArrNB=wANB,
block=b,
canOutput=cO,
endPos=eP,
getPos=gP,
setPos=sP,
verifyPos=vP,
close=c,
ioDesc=iD})=x
val LF = 10
val CR = 13
val LFcode=Word8.fromInt LF
val CRcode=Word8.fromInt 13
local
fun addCRtoVec v =
let val CRs = ref 0
val v'= Word8Vector.fromList (CharVector.foldr
(fn (c,l)=> if Char.ord c=LF then
(CRs:=(!CRs)+1;CRcode::LFcode::l) else
Word8.fromInt (Char.ord c)::l)
[] v)
in
(!CRs,v')
end
fun CRs_actually_outputV
(CRs, augmented_vector, amount_output) =
if Word8Vector.length(augmented_vector) = amount_output
then CRs
else
Word8Vector.foldli
(fn (_, c, CRs) => if Word8.toInt c = CR then CRs + 1 else CRs)
0
(augmented_vector, 0, SOME amount_output)
fun CRs_actually_outputA
(CRs, augmented_array, amount_output) =
if Word8Array.length(augmented_array) = amount_output
then CRs
else
Word8Array.foldli
(fn (_, c, CRs) => if Word8.toInt c = CR then CRs + 1 else CRs)
0
(augmented_array, 0, SOME amount_output)
in
fun wVconv NONE=NONE
| wVconv (SOME f) =
SOME(fn {buf=v,i=p,sz=(s:int option)} =>
let val (CRs,v')=addCRtoVec (CharVector.extract(v,p,s))
val real_out = f{buf=v',i=0, sz=NONE}
in real_out-(CRs_actually_outputV(CRs, v', real_out))
end)
fun wVNBconv NONE=NONE
| wVNBconv (SOME f) =
SOME(fn {buf=v,i=p,sz=(s:int option)}=>
let val (CRs,v') = addCRtoVec (CharVector.extract(v,p,s))
in
case f{buf=v',i=0,sz=NONE}
of NONE=> NONE
| (SOME k) =>
SOME(k-CRs_actually_outputV(CRs, v', k))
end)
fun wAconv NONE=NONE
| wAconv (SOME f) =
SOME(fn {buf=a,i=p,sz=(s:int option)}=>
let val (CRs,v) = addCRtoVec (CharArray.extract(a,p,s))
val a'= Word8Array.array(Word8Vector.length v,
Word8.fromInt 0)
in
(Word8Array.copyVec{src=v,si=0,len=NONE,dst=a',di=0};
let val amount_output = f{buf=a',i=0,sz=NONE}
in amount_output -
CRs_actually_outputA(CRs, a', amount_output) end)
end)
fun wANBconv NONE=NONE
| wANBconv (SOME f) =
SOME(fn {buf=a,i=p,sz=(s:int option)}=>
let val (CRs,v) = addCRtoVec (CharArray.extract(a,p,s))
val a'= Word8Array.array(Word8Vector.length v,
Word8.fromInt 0)
in
(Word8Array.copyVec{src=v,si=0,len=NONE,dst=a',di=0};
case f{buf=a',i=0,sz=NONE}
of NONE=> NONE
| (SOME k) => SOME (k-CRs_actually_outputA(CRs, a', k)))
end)
end
in
TextPrimIO.WR({name=n,
chunkSize=cS,
writeVec=wVconv wV,
writeArr=wAconv wA,
writeVecNB=wVNBconv wVNB,
writeArrNB=wANBconv wANB,
block=b,
canOutput=cO,
endPos=eP,
getPos=gP,
setPos=sP,
verifyPos=vP,
close=c,
ioDesc=iD})
end
fun openString s =
let
val pos = ref 0
val len = size s
fun stringReadVec i =
if !pos>=len then "" else
(CharVector.extract(s,!pos,if !pos+i>=len
then (pos:=len;NONE)
else (pos:=(!pos+i);SOME i)))
fun stringReadArr {buf,i,sz} =
if !pos>=len then 0 else
let val startPos = !pos
val num_elems = case sz
of NONE => (pos:=len;
len-startPos)
| (SOME n) =>
if !pos+n>=len then
(pos:=len;
len-startPos)
else (pos:=(!pos+n); n)
in
CharArray.copyVec {src=s, si= !pos, len=SOME num_elems,
dst=buf, di=i};
num_elems
end
in
TextPrimIO.RD
{readVec=SOME(stringReadVec),
readArr=SOME(stringReadArr),
readVecNB=SOME(SOME o stringReadVec),
readArrNB=SOME(SOME o stringReadArr),
block=NONE,
canInput=SOME(fn ()=> !pos< (len-1)),
avail=fn()=> SOME(len-(!pos)),
name="<stringIn>",
chunkSize=1,
close=fn () => pos:=len-1,
getPos=SOME(fn()=> !pos),
setPos=SOME(fn i => pos:=i),
endPos=SOME(fn()=> len-1),
verifyPos=SOME(fn() => !pos),
ioDesc=NONE}
end
end
;
