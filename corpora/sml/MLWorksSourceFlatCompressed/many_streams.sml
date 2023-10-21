fun discard_stream () = TextIO.closeIn(TextIO.openIn "perl_script");
fun makelist () = let fun mkl (0,a) = a
| mkl (n,a) = mkl (n-1,n::a)
in mkl (100000,[])
end
fun body l = (discard_stream ();
(makelist ())::l);
fun loop n f a = let fun loop' (0,acc) = ()
| loop' (n,acc) = loop'(n-1,f acc)
in loop'(n,a)
end
val _ = loop 30 body [];
