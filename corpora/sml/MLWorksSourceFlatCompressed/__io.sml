require "io";
structure IO : IO =
struct
exception Io = MLWorks.Internal.IO.Io
exception BlockingNotSupported
exception NonblockingNotSupported
exception RandomAccessNotSupported
exception TerminatedStream
exception ClosedStream
datatype buffer_mode = NO_BUF | LINE_BUF | BLOCK_BUF
end
;
