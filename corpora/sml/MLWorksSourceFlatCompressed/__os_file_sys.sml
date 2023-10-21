require "__win32";
require "__os_path";
require "_os_file_sys";
structure OSFileSys_ =
OSFileSys
(structure Win32 = Win32_
structure Path = OSPath_)
;
