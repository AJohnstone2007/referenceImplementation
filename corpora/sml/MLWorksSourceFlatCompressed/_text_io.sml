require "__char_array";
require "__char_vector";
require "__char";
require "__position";
require "__io";
require "_imperative_io";
require "os_prim_io";
require "text_io";
require "__text_stream_io";
require "prim_io";
require "__substring";
require "^.system.__os";
functor TextIO(include sig
structure TextPrimIO: PRIM_IO
structure OSPrimIO: OS_PRIM_IO
sharing type OSPrimIO.text_reader = TextPrimIO.reader
sharing type OSPrimIO.text_writer = TextPrimIO.writer
end where type OSPrimIO.text_reader = TextStreamIO.reader
where type OSPrimIO.text_writer = TextStreamIO.writer
where type TextPrimIO.elem = Char.char
where type TextPrimIO.array = CharArray.array
where type TextPrimIO.vector = CharVector.vector): TEXT_IO =
struct
structure TextIO' =
ImperativeIO(structure StreamIO = TextStreamIO
structure Vector = CharVector
structure Array = CharArray)
val translateIn = OSPrimIO.translateIn
val translateOut = OSPrimIO.translateOut
val openIn =
fn x => TextIO'.mkInstream
(TextIO'.StreamIO.mkInstream
(translateIn(OSPrimIO.openRd x), ""))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openIn",cause=OS.SysErr e}
val openOut =
fn x => TextIO'.mkOutstream
(TextIO'.StreamIO.mkOutstream
(translateOut(OSPrimIO.openWr x), IO.NO_BUF))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openOut",cause=OS.SysErr e}
val openAppend =
fn x => TextIO'.mkOutstream
(TextIO'.StreamIO.mkOutstream
(translateOut(OSPrimIO.openApp x), IO.NO_BUF))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openAppend",cause=OS.SysErr e}
fun openString s =
TextIO'.mkInstream
(TextIO'.StreamIO.mkInstream
(OSPrimIO.openString s,""))
fun inputLine (f: TextIO'.instream) =
let
val g0 = TextIO'.getInstream f
val (s,gn) = TextStreamIO.inputLine g0
val _ = TextIO'.setInstream(f,gn)
in
s
end
fun outputSubstr(f:TextIO'.outstream, ss:Substring.substring) =
TextIO'.output(f,Substring.string ss)
val stdIn =
TextIO'.mkInstream(TextIO'.StreamIO.mkInstream
(translateIn OSPrimIO.stdIn,""))
val stdOut =
TextIO'.mkOutstream(TextIO'.StreamIO.mkOutstream
(translateOut OSPrimIO.stdOut, IO.NO_BUF))
val stdErr =
TextIO'.mkOutstream(TextIO'.StreamIO.mkOutstream
(translateOut OSPrimIO.stdErr, IO.NO_BUF))
fun print s = (TextIO'.output(stdOut, s); TextIO'.flushOut stdOut)
fun scanStream scanFn stream =
let
val instream=TextIO'.getInstream stream
in
case (scanFn TextIO'.StreamIO.input1 instream)
of NONE => NONE
| (SOME (v,instream')) =>
(TextIO'.setInstream (stream,instream');
SOME v)
end
open TextIO'
structure StreamIO = TextStreamIO
structure Position = Position
end
;
