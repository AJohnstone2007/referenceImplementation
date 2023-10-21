fun oops () = print"Oops\n";
OS.Process.atExit oops;
val _ = OS.Process.terminate OS.Process.success;
