fun long_list (a,0) = a
| long_list (a,n) = long_list (n::a, n-1);
val _ =
(ignore(TextIO.openIn"pooooo"); [])
handle IO.Io _ =>
long_list ([], 1000000);
