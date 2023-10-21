require "^.basis.__text_io";
require "^.basis.__word8";
require "__os";
signature UNIX =
sig
type proc
type signal
datatype exit_status =
W_EXITED
| W_EXITSTATUS of Word8.word
| W_SIGNALED of signal
| W_STOPPED of signal
val fromStatus : OS.Process.status -> exit_status
val exit : exit_status -> 'a
val executeInEnv : (string * string list * string list) -> proc
val execute : (string * string list) -> proc
val streamsOf : proc -> (TextIO.instream * TextIO.outstream)
val reap : proc -> OS.Process.status
val kill : (proc * signal) -> unit
end
;
