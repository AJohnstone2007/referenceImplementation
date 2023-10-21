fun out s = print(s ^ "\n");
fun finish () = out "finished.";
fun recursive_atExit () = OS.Process.atExit (fn () => out "Oops");
fun after_exit () = out "after recursive exit, trying atExit case ...";
fun recursive_exit () =
(out "Starting recursive exit ...";
ignore(OS.Process.exit OS.Process.success);
out "Should never get here");
fun start () = out "starting ...";
OS.Process.atExit finish;
OS.Process.atExit recursive_atExit;
OS.Process.atExit after_exit;
OS.Process.atExit recursive_exit;
OS.Process.atExit start;
val _ = OS.Process.exit OS.Process.failure;
