require "os_file_sys";
require "os_path";
require "os_process";
require "os_io";
signature OS =
sig
type syserror
exception SysErr of (string * syserror option)
val errorMsg : syserror -> string
val errorName : syserror -> string
val syserror : string -> syserror option
structure FileSys : OS_FILE_SYS
structure Path : OS_PATH
structure Process : OS_PROCESS
structure IO : OS_IO
end
;
