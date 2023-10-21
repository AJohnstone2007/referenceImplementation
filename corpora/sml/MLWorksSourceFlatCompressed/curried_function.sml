Shell.Options.Mode.debugging ();
fun foldl f z [] = z
| foldl f z (x::xs) = foldl f (f (z, x)) xs;
fun sum xs = foldl op+ 0 xs;
Shell.Trace.breakpoint "sum";
sum [1,2,3];
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
b
s
;
