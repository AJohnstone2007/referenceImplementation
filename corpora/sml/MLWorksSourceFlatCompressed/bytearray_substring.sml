val a = MLWorks.Internal.ByteArray.arrayoflist[0,1,2,3,4,5,6,7,8,9]
val b = MLWorks.Internal.ByteArray.substring(a, 3, 5)
val _ = (ignore(MLWorks.Internal.ByteArray.substring (a,0,10)); print"Pass0\n")
handle MLWorks.Internal.ByteArray.Substring => print"Fail0\n"
val _ = case b of
"\003\004\005\006\007" => print"Pass1\n"
| _ => print"Fail1\n"
val d = MLWorks.Internal.ByteArray.substring(a, 3, 11) handle MLWorks.Internal.ByteArray.Substring =>
(str o chr) 3 ^ (str o chr) 11
val _ = case d of
"\003\011" => print"Pass2\n"
| _ => print"Fail2\n"
val e = MLWorks.Internal.ByteArray.substring(a, ~3, 2) handle MLWorks.Internal.ByteArray.Substring =>
(str o chr) 253 ^ (str o chr) 2
val _ = case e of
"\253\002" => print"Pass3\n"
| _ => print"Fail3\n"
;
