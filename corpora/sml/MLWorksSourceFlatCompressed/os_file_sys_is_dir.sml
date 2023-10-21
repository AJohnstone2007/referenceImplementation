val a = (ignore(OS.FileSys.isDir "no such file"); false) handle OS.SysErr _ => true;
val b = (ignore(OS.FileSys.isDir ""); false) handle OS.SysErr _ => true;
val c = OS.FileSys.isDir "basis";
val d = OS.FileSys.isDir "README" = false;
