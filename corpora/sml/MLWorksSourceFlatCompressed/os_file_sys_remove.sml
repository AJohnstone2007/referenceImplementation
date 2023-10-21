val a = (OS.FileSys.remove "no such file"; false) handle OS.SysErr _ => true;
val b = (OS.FileSys.remove ""; false) handle OS.SysErr _ => true;
