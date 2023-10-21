local
val thread = ref []: MLWorks.Threads.Internal.thread_id list ref
fun failure () = (MLWorks.Threads.sleep (hd(!thread));
print"test failed\n")
fun success id = print"test succeeded\n"
val failureThread = MLWorks.Threads.fork failure ();
val successfulThread = MLWorks.Threads.fork success failureThread;
val _ = thread:=[MLWorks.Threads.Internal.get_id successfulThread];
in
val _ = MLWorks.Threads.sleep failureThread;
val _ = MLWorks.Threads.yield();
val _ = MLWorks.Threads.yield();
val _ = MLWorks.Threads.Internal.kill failureThread;
end;