require "_os";
require "__win32";
require "__os_file_sys";
require "__os_path";
require "__os_io";
require "^.basis.__os_process";
structure OS = OS
(structure Win32 = Win32_
structure FileSys = OSFileSys_
structure Path = OSPath_
structure Process = OSProcess_
structure IO = OSIO_);
