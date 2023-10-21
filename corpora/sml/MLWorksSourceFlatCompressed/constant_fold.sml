exception Test;
val result =
(((raise Test) - (raise Test)) handle Test => 10) = 10
andalso
(((raise Test) * 0) handle Test => 10) = 10
andalso
((0 * (raise Test)) handle Test => 10) = 10
andalso
(((raise Test) div (raise Test)) handle Test => 10) = 10
andalso
(((raise Test) mod 1) handle Test => 10) = 10
andalso
((0 mod (raise Test)) handle Test => 10) = 10
andalso
(((raise Test) mod 0) handle Test => 10 | Mod => 0) = 10
andalso
not (((raise Test) = (raise Test)) handle Test => false)
andalso
(((raise Test) <> (raise Test)) handle Test => true)
val _ = if result then print"Pass\n" else print"Fail\n"
;
