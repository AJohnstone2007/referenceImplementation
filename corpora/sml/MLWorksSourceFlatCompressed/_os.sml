require "win32";
require "^.basis.os";
require "^.basis.os_path";
require "^.basis.os_file_sys";
require "^.basis.os_process";
require "^.basis.os_io";
functor OS
(structure Win32: WIN32
structure FileSys: OS_FILE_SYS
structure Path: OS_PATH
structure Process: OS_PROCESS
structure IO: OS_IO) : OS =
struct
val env = MLWorks.Internal.Runtime.environment
type syserror = MLWorks.Internal.Error.syserror
exception SysErr = MLWorks.Internal.Error.SysErr
val errorMsg = MLWorks.Internal.Error.errorMsg
val errorName = MLWorks.Internal.Error.errorName
val syserror = MLWorks.Internal.Error.syserror
structure FileSys = FileSys
structure Path = Path
structure Process = Process
structure IO = IO
end
;
