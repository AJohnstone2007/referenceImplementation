Shell.Options.set (Shell.Options.ValuePrinter.maximumStrDepth, 0);
let
val dir = OS.FileSys.getDir()
fun setTargets l = (app Shell.Project.setTargetDetails l; Shell.Project.setTargets l)
val _ = Shell.Project.openProject (OS.Path.concat [dir, "..", "src", "basis.mlp"]);
val _ = Shell.Project.setConfiguration "I386/NT";
val {binariesLoc, libraryPath, objectsLoc} = Shell.Project.showLocations();
val _ = Shell.Project.setLocations
{binariesLoc=binariesLoc, libraryPath=libraryPath, objectsLoc="\\tmp\\objects"};
in
setTargets ["__io.sml", "__bin_prim_io.sml", "__os_prim_io.sml", "__text_io.sml",
"__text_stream_io.sml", "__bin_stream_io.sml", "__text_prim_io.sml"];
Shell.Project.forceCompileAll();
Shell.Project.loadAll()
end;
local
open OSPrimIO
open BinPrimIO
in
datatype Result = CORRECT_EX | WRONG_EX of exn | NO_EX
val test = fn () =>
let
val currIO = MLWorks.Internal.StandardIO.currentIO ()
val newIO =
{ access = #access currIO,
error = {
can_output = NONE,
close = #close (#error currIO),
descriptor = #descriptor (#error currIO),
get_pos = NONE : (unit -> int) option,
put = #put (#error currIO),
set_pos = NONE : (int -> unit) option},
input = {
can_input = NONE,
close = #close (#input currIO),
descriptor = #descriptor (#input currIO),
get_pos = NONE : (unit -> int) option,
get = #get (#input currIO),
set_pos = NONE : (int -> unit) option},
output = {
can_output = NONE,
close = #close (#output currIO),
descriptor = #descriptor (#output currIO),
get_pos = NONE : (unit -> int) option,
put = #put (#output currIO),
set_pos = NONE : (int -> unit) option}
}
val redirect = MLWorks.Internal.StandardIO.redirectIO newIO
val RD OSPrimIOStdIn = stdIn
val getPosStdIn =
case (#getPos(OSPrimIOStdIn)) of SOME a => a | _ => raise Fail "?"
val testGetPosStdIn =
(ignore(getPosStdIn ()); NO_EX)
handle IO.RandomAccessNotSupported => CORRECT_EX
| e => WRONG_EX e
val testSetPosStdIn =
let
val RD OSPrimIOStdIn = stdIn
val setPosStdIn =
case (#setPos(OSPrimIOStdIn)) of SOME a => a | _ => raise Fail "?"
in
(setPosStdIn 1; NO_EX)
end handle IO.RandomAccessNotSupported => CORRECT_EX
val testGetPosStdOut =
let
val WR OSPrimIOStdOut = stdOut
val getPosStdOut =
case (#getPos(OSPrimIOStdOut)) of SOME a => a | _ => raise Fail "?"
in
(ignore(getPosStdOut ()); NO_EX)
end handle IO.RandomAccessNotSupported => CORRECT_EX
val testSetPosStdOut =
let
val WR OSPrimIOStdOut = stdOut
val setPosStdOut =
case (#setPos(OSPrimIOStdOut)) of SOME a => a | _ => raise Fail "?"
in
(setPosStdOut 1; NO_EX)
end handle IO.RandomAccessNotSupported => CORRECT_EX
val testGetPosStdErr =
let
val WR OSPrimIOStdErr = stdErr
val getPosStdErr =
case (#getPos(OSPrimIOStdErr)) of SOME a => a | _ => raise Fail "?"
in
(ignore(getPosStdErr ()); NO_EX)
end handle IO.RandomAccessNotSupported => CORRECT_EX
val testSetPosStdErr =
let
val WR OSPrimIOStdErr = stdErr
val setPosStdErr =
case (#setPos(OSPrimIOStdErr)) of SOME a => a | _ => raise Fail "?"
in
(setPosStdErr 1; NO_EX)
end handle IO.RandomAccessNotSupported => CORRECT_EX
local
open TextIO
fun setSetPosIn (stream, newval) =
let val instream = getInstream stream
val (r as TextPrimIO.RD
{avail, block, canInput, chunkSize, close, endPos, getPos,
ioDesc, name, readArr, readArrNB, readVec, readVecNB,
setPos, verifyPos},
v) = StreamIO.getReader(instream);
val r' = TextPrimIO.RD
{avail=avail, block=block, canInput=canInput,
chunkSize=chunkSize, close=close, endPos=endPos,
getPos=getPos, ioDesc=ioDesc, name=name, readArr=readArr,
readArrNB=readArrNB, readVec=readVec, readVecNB=readVecNB,
setPos=newval, verifyPos=verifyPos}
val newinstream = StreamIO.mkInstream(r', v)
in setInstream(stream, newinstream);
setPos
end
in
val oldsetpos = setSetPosIn (stdIn, NONE);
val testStreamIO =
{getPosStdIn =
(ignore(getPosIn stdIn); NO_EX)
handle IO.Io { cause = IO.RandomAccessNotSupported, ...} => CORRECT_EX
| e => WRONG_EX e ,
getPosStdOut =
(ignore(getPosOut stdOut); NO_EX)
handle IO.Io { cause = IO.RandomAccessNotSupported, ...} => CORRECT_EX
| e => WRONG_EX e
}
val repeatTest =
let
fun doIt () = (ignore(getPosIn stdIn); NO_EX)
handle IO.Io {cause = IO.RandomAccessNotSupported, ...} =>
CORRECT_EX
| e => WRONG_EX e
fun repeat 0 = []
| repeat n = (doIt ())::(repeat (n-1))
in
repeat 10
end
val _ = setSetPosIn(stdIn, oldsetpos);
end
val redirect = MLWorks.Internal.StandardIO.redirectIO currIO
in
{testGetPosStdIn = testGetPosStdIn,
testSetPosStdIn = testSetPosStdIn,
testGetPosStdOut = testGetPosStdOut,
testSetPosStdOut = testSetPosStdOut,
testGetPosStdErr = testGetPosStdErr,
testSetPosStdErr = testSetPosStdErr,
testStreamIO = testStreamIO,
repeatTest = repeatTest}
end
end
test();
