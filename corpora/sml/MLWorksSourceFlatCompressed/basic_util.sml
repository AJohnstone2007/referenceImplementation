require "__string";
require "__int";
require "__substring";
require "__os";
require "__text_io";
require "__text_prim_io";
require "__unixos";
require "__io";
require "basic_util_sig";
structure Char = String.Char;
structure BasicUtil : BASIC_UTIL =
struct
fun fst (a, _) = a
fun snd (_, b) = b
fun pair (f, g) z = (f z, g z)
fun a /= b = not(a = b)
fun eq a b = a = b
fun upto(i, j) = if (i:int) <= j then i::(upto(i+1, j)) else []
fun inc x = (x := (!x + 1); !x)
fun curry f x y = f (x, y)
fun uncurry f (x, y) = f x y
fun twist (f:'a*'b-> 'c) = fn (y, x)=> f(x, y)
val K0 = fn _ => ()
structure ListUtil =
struct
fun getx p [] ex = raise ex
| getx p (x::xs) ex = if p x then x else getx p xs ex
fun updateVal p y = map (fn x=> if p x then y else x)
fun dropWhile p [] = []
| dropWhile p (xs as(x::xs2)) = if p x then dropWhile p xs2
else xs
fun span p [] = ([], [])
| span p (x::xs) = if p x then let val (ys, zs)= span p xs
in (x::ys, zs)
end
else ([], x::xs)
fun break p = span (not o p)
fun sort (less: 'a*'a -> bool) =
let fun insert (x, []) = [x]
| insert (x, y::ys) =
if less(y, x) then y :: insert (x, ys) else x::y::ys
fun sort1 [] = []
| sort1 (x::xs) = insert (x, sort1 xs)
in sort1 end
fun prefix [] ys = true
| prefix (x::xs) [] = false
| prefix (x::xs) (y::ys) = (x=y andalso prefix xs ys)
end
structure StringUtil =
struct
fun isDot c = #"." = c
fun isComma c = #"," = c
fun isLinefeed c = #"\n" = c
fun isOpenParen c = #"(" = c
fun isCloseParen c = #")" = c
fun concatWith s [] = ""
| concatWith s [t] = t
| concatWith s (t::l) = t^s^(concatWith s l)
val words = String.fields Char.isSpace
local
open Substring
in
fun breakAtDot s =
let val (hd, tl) = splitl (not o isDot) (all s)
in (string hd, string (triml 1 tl))
end
end
fun toInt s =
getOpt(Int.fromString s, 0)
handle Overflow =>
(TextIO.output(TextIO.stdErr,
"WARNING: caught int conversion overflow\n");
0)
fun all p str = Substring.foldl (fn (c, r)=> (p c) andalso r)
true (Substring.all str)
fun adaptString s =
let fun escape c =
if Char.contains "\"\\$[]{}" c
then "\\" ^(str c)
else if c = #"\n" then "\\n"
else str c
in String.translate escape s
end
end
structure FileUtil =
struct
val cd = OS.FileSys.chDir
fun ls dir =
let
val dirStr = OS.FileSys.openDir dir;
fun loopDir dirLst =
let
val str = OS.FileSys.readDir dirStr
in
if ( str = "" ) then
dirLst
else
loopDir dirLst@[str]
end
val dirlst = loopDir []
val _ = OS.FileSys.closeDir dirStr
in
dirlst
end
val pwd = OS.FileSys.getDir
val isDir = OS.FileSys.isDir
fun fdWriter (name, raw_fd) =
let val fd = MLWorks.Internal.IO.FILE_DESC raw_fd
in
TextPrimIO.WR
{name = name,
chunkSize = 1,
writeVec = SOME (fn {buf, i, sz} =>
case sz of
NONE => MLWorks.Internal.IO.write (fd, buf, i, (size buf) - i)
| SOME szi => MLWorks.Internal.IO.write (fd, buf, i, szi)),
writeArr = NONE,
writeVecNB = NONE,
writeArrNB = NONE,
block = NONE,
canOutput = NONE,
getPos = NONE,
setPos = NONE,
endPos = NONE,
verifyPos = NONE,
close = fn () => MLWorks.Internal.IO.close fd,
ioDesc = SOME (UnixOS_.FileSys.fdToIOD fd)}
end;
fun fdReader (name, raw_fd) =
let val fd = MLWorks.Internal.IO.FILE_DESC raw_fd
in
TextPrimIO.RD
{name = name,
chunkSize = 1,
readVec = SOME (fn i => MLWorks.Internal.IO.read (fd, i)),
readArr = NONE,
readVecNB = NONE,
readArrNB = NONE,
block = NONE,
canInput =
SOME (fn _ =>
case MLWorks.Internal.IO.can_input fd of
0 => false
| _ => true),
avail = (fn () =>
case MLWorks.Internal.IO.can_input fd of
0 => NONE
| i => SOME i),
getPos = NONE,
setPos = NONE,
endPos = NONE,
verifyPos = NONE,
close = fn () => MLWorks.Internal.IO.close fd,
ioDesc = SOME (UnixOS_.FileSys.fdToIOD fd)}
end;
fun openOutFD (name, fd) =
TextIO.mkOutstream (
TextIO.StreamIO.mkOutstream (
fdWriter (name, fd), IO.BLOCK_BUF));
fun openInFD (name, fd) =
TextIO.mkInstream (
TextIO.StreamIO.mkInstream (
fdReader (name, fd), ""));
val pipe : unit -> int * int = fn () => (1,1)
fun execute (s, sl) =
let
val prevIn = TextIO.getInstream TextIO.stdIn
val prevOut = TextIO.getOutstream TextIO.stdOut
val (p11, p12) = pipe()
val (p21, p22) = pipe()
val childIn = openInFD ("in_child", p21)
val childOut = openOutFD ("out_child", p12)
val pipeIn = openInFD ("pipe_in", p11)
val pipeOut = openOutFD ("pipe_out", p22)
fun intToFD i = MLWorks.Internal.IO.FILE_DESC i
val fdIn = intToFD p21
val fdOut = intToFD p12
val newIn = {descriptor = SOME fdIn,
get_pos = NONE, set_pos = NONE,
get = fn i => MLWorks.Internal.IO.read (fdIn, i),
can_input =
SOME (fn _ =>
case MLWorks.Internal.IO.can_input fdIn of
0 => false
| _ => true),
close = fn () => MLWorks.Internal.IO.close fdIn}
val newOut = {descriptor = SOME fdOut,
can_output = NONE,
close = fn () => MLWorks.Internal.IO.close fdOut,
get_pos = NONE, set_pos = NONE,
put = (fn {buf, i, sz} =>
case sz of
NONE => MLWorks.Internal.IO.write (fdOut, buf, i, (size buf) - i)
| SOME szi => MLWorks.Internal.IO.write (fdOut, buf, i, szi))}
val {error, access, output = oldOut, input = oldIn} =
MLWorks.Internal.StandardIO.currentIO ()
in
(pipeIn, pipeOut)
end;
fun exec (s, sl) =
(print ("exec called with " ^ s ^ "!\n");
OS.Process.success = OS.Process.system s)
val getEnv = OS.Process.getEnv
fun isFileRdAndEx pn =
OS.FileSys.access (pn,[OS.FileSys.A_READ])
fun isFileRd pn =
OS.FileSys.access (pn,[OS.FileSys.A_READ])
fun isDirRdAndWr pn =
(OS.FileSys.access (pn,[OS.FileSys.A_READ,OS.FileSys.A_WRITE])) andalso
(OS.FileSys.isDir pn)
fun openFile e f =
TextIO.openIn f
handle (OS.SysErr(err, _)) => raise (e ("Can't open file "^f^": "^err))
end
end
;
