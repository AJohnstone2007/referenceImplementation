local
open SML90;
in
val _ = TextIO.closeOut (TextIO.openOut "old_io.txt");
val _ = OS.FileSys.remove "old_io.txt" handle _ => ()
val test1 = output (std_out, "test1\n");
val os = open_out "old_io.txt";
val test2 = output(os, "test2\n");
val test3 = close_out os;
val is = open_in "old_io.txt";
val test4 = input (is, 10);
val test5 = close_in is;
val test6 = (output (os, "test4\n"); "wrong")
handle Io "Output stream is closed" => "right"
| _ => "wrong";
val test7 = (ignore(open_in "/this does not exist"); "wrong")
handle Io "Cannot open /this does not exist" => "right"
| _ => "wrong"
val test8 = (ignore(open_out "/"); "wrong")
handle Io "Cannot open /" => "right"
| _ => "wrong"
val _ = OS.FileSys.remove "old_io.txt" handle _ => ()
end
;
