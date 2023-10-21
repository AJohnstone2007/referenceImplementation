signature WIN32 =
sig
type syserror
exception SysErr of (string * syserror option)
type file_desc
datatype iodesc = IODESC of int
val fdToIOD : file_desc -> iodesc
val closeIOD : iodesc -> unit
datatype priority = REAL_TIME | HIGH | NORMAL | BACKGROUND
val create_process : string * priority -> bool
end
;
