require "lowlevel";
structure LowLevel: LOWLEVEL =
struct
type 'a array = 'a MLWorks.Internal.Array.array
val maxLen = MLWorks.Internal.ExtendedArray.maxLen
fun check_size n =
if n < 0 orelse n > maxLen then raise Size else n
fun array (n,i) =
MLWorks.Internal.Array.array (check_size n,i)
val sub = MLWorks.Internal.Array.sub
infix sub
val update = MLWorks.Internal.Array.update
fun fromList l =
(ignore(check_size (length l));
MLWorks.Internal.Array.arrayoflist l)
fun makeString n =
let
fun makeDigit digit =
if digit >= 10 then chr (ord #"A" + digit - 10)
else chr (ord #"0" + digit)
val sign = if n < 0 then "~" else ""
fun makeDigits (n,acc) =
let
val digit =
if n >= 0
then n mod 10
else
let
val res = n mod 10
in
if res = 0 then 0 else 10 - res
end
val n' =
if n >= 0 orelse digit = 0 then
n div 10
else 1 + n div 10
val acc' = makeDigit digit :: acc
in
if n' <> 0
then makeDigits (n',acc')
else acc'
end
in
sign^(implode (makeDigits (n,[])))
end
fun makeRealString r = MLWorks.Internal.Value.real_to_string r
datatype inStream = FD of int | STDIN
val stdIn:inStream = STDIN
fun inputN(FD(fd),n) = (print ".";
MLWorks.Internal.IO.read
(MLWorks.Internal.IO.FILE_DESC fd,n))
| inputN(STDIN,n) =
#get(#input(MLWorks.Internal.StandardIO.currentIO())) n
val openIn: string -> inStream =
fn fileName =>
FD(MLWorks.Internal.Runtime.environment "POSIX.FileSys.openf"
(fileName,0,0))
fun noStdInput() =
MLWorks.Internal.IO.can_input
(MLWorks.Internal.IO.FILE_DESC 0) = 0
fun flushStdIn() =
if noStdInput() then () else (ignore(inputN(STDIN,1)); flushStdIn())
fun stringSub (s,i) =
if i<0 orelse i>(size s) then raise Subscript
else MLWorks.Internal.Value.unsafe_string_sub (s,i)
end
open LowLevel;
