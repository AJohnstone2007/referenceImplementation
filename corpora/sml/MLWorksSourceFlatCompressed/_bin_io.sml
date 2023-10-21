require "__word8_vector";
require "__word8_array";
require "__word8";
require "__position";
require "_imperative_io";
require "__bin_stream_io";
require "os_prim_io";
require "bin_io.sml";
require "__io";
require "prim_io";
require "^.system.__os";
functor BinIO(include sig
structure BinPrimIO: PRIM_IO
structure OSPrimIO: OS_PRIM_IO
sharing type OSPrimIO.bin_reader = BinPrimIO.reader
sharing type OSPrimIO.bin_writer = BinPrimIO.writer
end where type BinPrimIO.reader = BinStreamIO.reader
where type BinPrimIO.writer = BinStreamIO.writer
where type BinPrimIO.elem = Word8.word
where type BinPrimIO.array = Word8Array.array
where type BinPrimIO.vector = Word8Array.vector
where type BinPrimIO.pos = int): BIN_IO =
struct
structure BinIO' = ImperativeIO(structure StreamIO = BinStreamIO
structure Vector = Word8Vector
structure Array = Word8Array)
val openIn =
fn x =>
BinIO'.mkInstream
(BinIO'.StreamIO.mkInstream
(OSPrimIO.openRd x, Word8Vector.fromList []))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openIn",cause=OS.SysErr e}
val openOut =
fn x =>
BinIO'.mkOutstream(BinIO'.StreamIO.mkOutstream(OSPrimIO.openWr x,
IO.NO_BUF))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openOut",cause=OS.SysErr e}
val openAppend =
fn x =>
BinIO'.mkOutstream(BinIO'.StreamIO.mkOutstream(OSPrimIO.openApp x,
IO.NO_BUF))
handle MLWorks.Internal.Error.SysErr e =>
raise IO.Io{name=x,function="openAppend",cause=OS.SysErr e}
open BinIO'
structure Position = Position
end
;
