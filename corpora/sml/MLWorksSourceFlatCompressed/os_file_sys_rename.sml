val a = (OS.FileSys.rename {old = "no such file", new = "also does not exist"}; false) handle OS.SysErr _ => true;
