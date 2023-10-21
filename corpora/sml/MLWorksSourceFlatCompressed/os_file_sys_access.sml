val a = OS.FileSys.access ("basis", [OS.FileSys.A_READ, OS.FileSys.A_WRITE, OS.FileSys.A_EXEC]);
val b = OS.FileSys.access ("basis", []);
val c = OS.FileSys.access ("no such file", [OS.FileSys.A_READ, OS.FileSys.A_WRITE, OS.FileSys.A_EXEC]) = false;
val d = OS.FileSys.access ("no such file", []) = false;
