require "win32";
functor Win32(): WIN32 =
struct
val env = MLWorks.Internal.Runtime.environment
type syserror = MLWorks.Internal.Error.syserror
exception SysErr = MLWorks.Internal.Error.SysErr
type file_desc = MLWorks.Internal.IO.file_desc
datatype iodesc = IODESC of int
val fdToIOD : file_desc -> iodesc = env "Win32.fdToIOD"
val closeIOD : iodesc -> unit = env"Win32.closeIOD"
datatype priority = REAL_TIME | HIGH | NORMAL | BACKGROUND
val create_process : string * priority -> bool =
env "system os win32 create_process"
end
;
