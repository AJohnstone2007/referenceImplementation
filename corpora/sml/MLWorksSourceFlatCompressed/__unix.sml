require "^.basis.__text_io";
require "^.basis.__text_prim_io";
require "^.basis.__io";
require "^.basis.__word8";
require "__os";
require "__os_exit";
require "__unixos";
require "unix";
structure Unix : UNIX =
struct
datatype proc =
PROC of {id: int,
pipeIn: TextIO.instream,
pipeOut: TextIO.outstream}
type signal = Word8.word
datatype exit_status = datatype OSExit.exit_status
val fromStatus = OSExit.fromStatus
val exit = OSExit.os_exit
local
fun fdWriter (name, raw_fd) =
let
val fd = MLWorks.Internal.IO.FILE_DESC raw_fd
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
end
fun fdReader (name, raw_fd) =
let
val fd = MLWorks.Internal.IO.FILE_DESC raw_fd
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
end
fun openOutFD (name, fd) =
TextIO.mkOutstream (
TextIO.StreamIO.mkOutstream (
fdWriter (name, fd), IO.BLOCK_BUF));
fun openInFD (name, fd) =
TextIO.mkInstream (
TextIO.StreamIO.mkInstream (
fdReader (name, fd), ""));
val env = MLWorks.Internal.Runtime.environment
val pipe : unit -> int * int = env "system os unix pipe"
fun execute_with_env (s, sl, env_opt) =
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
val pid =
case env_opt of
NONE => UnixOS_.fork_execv (s, sl, p21, p12, 2)
| SOME env => UnixOS_.fork_execve (s, sl, env, p21, p12, 2)
in
PROC {id = pid, pipeIn = pipeIn, pipeOut = pipeOut}
end
in
fun execute (s, sl) = execute_with_env (s, sl, NONE)
fun executeInEnv (s, sl, environ) =
execute_with_env (s, sl, SOME environ)
end
fun streamsOf (PROC {id, pipeIn, pipeOut}) = (pipeIn, pipeOut)
val wait : int -> OS.Process.status =
MLWorks.Internal.Runtime.environment "system os unix wait"
fun reap (PROC{id, pipeIn, pipeOut}) =
(TextIO.closeIn pipeIn;
TextIO.closeOut pipeOut;
wait id)
fun kill (PROC {id, pipeIn, pipeOut}, sign) =
UnixOS_.kill (id, Word8.toInt sign)
end
;
