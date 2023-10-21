require "__pre_basis";
structure PreStringCvt =
struct
datatype radix = BIN | OCT | DEC | HEX
datatype realfmt =
EXACT
| SCI of int option
| FIX of int option
| GEN of int option
type cs = int
type ('a,'b) reader = 'b -> ('a * 'b) option
fun copyFrom (src, dest, src_x, src_y, dest_x) =
let
fun aux x =
if x <= src_y then
(MLWorks.Internal.Value.unsafe_string_update
(dest, x+dest_x, MLWorks.String.ordof(src, x));
aux (x+1))
else
dest
in
aux src_x
end
fun fillFrom (c:char, dest:string, dest_x:int, dest_y:int) =
let fun aux i =
if i <= dest_y then
(MLWorks.Internal.Value.unsafe_string_update (dest, i, ord c);
aux (i+1))
else dest
in
aux dest_x
end
fun padLeft (c:char) (i:int) (s:string) : string =
let
val size = size s
val pad = i - size
in
if pad > 0 then
let val alloc_s = PreBasis.alloc_string (i+1)
in
ignore(fillFrom (c, alloc_s, 0, pad-1));
ignore(copyFrom (s, alloc_s, 0, size-1, pad));
alloc_s
end
else
s
end
fun padRight (c:char) (i:int) (s:string) : string =
let
val size = size s
val pad = i - size
in
if pad > 0 then
let
val alloc_s = PreBasis.alloc_string(i+1)
in
ignore(copyFrom (s, alloc_s, 0, size-1, 0));
ignore(fillFrom (c, alloc_s, size, i-1));
alloc_s
end
else
s
end
fun scanList f (cs:char list) =
let fun aux ([]:char list) = NONE
| aux (c::cs) = SOME (c, cs)
in
case f aux cs of
SOME (c,_) => SOME c
| NONE => NONE
end
fun scanString f (s:string) =
let val size = size s
fun aux i =
if i < size then
SOME (chr(MLWorks.String.ordof(s, i)), i+1)
else
NONE
in
case f aux 0 of
SOME (x,_) => SOME x
| NONE => NONE
end
fun skipWS f s =
let fun aux cs =
case f cs of
SOME (c, cs') =>
if PreBasis.isSpace c then
aux cs'
else
cs
| NONE => cs
in
aux s
end
fun dropl p f src =
let
fun aux cs =
case f cs of
SOME (c, cs') =>
if p c then
aux cs'
else
cs
| NONE => cs
in
aux src
end
fun splitl (p:char -> bool) f src =
let
fun aux (acc, cs) =
case f cs of
SOME (c, cs') =>
if p c then
aux (c::acc, cs')
else
(PreBasis.revImplode acc, cs)
| NONE => (PreBasis.revImplode acc, cs)
in
aux ([], src)
end
fun takel p f src =
let
fun aux (acc, cs) =
case f cs of
SOME (c, cs) =>
if p c then
aux (c::acc, cs)
else
acc
| NONE => acc
in
PreBasis.revImplode (aux ([], src))
end
fun getNChar (n:int) getc cs =
let
fun aux (i, acc, cs) =
if i < n then
(case getc cs of
SOME (c, cs) => aux (i+1, c::acc, cs)
| NONE => ([], cs))
else
(acc, cs)
in
case aux (0, [], cs) of
([], _) => NONE
| (acc, cs) => SOME (rev acc, cs)
end
fun splitlN (n:int) (p:char -> bool) getc cs =
let
fun aux (count, acc, cs) =
if count < n then
(case getc cs of
SOME (c, cs') =>
if p c then
aux (count+1, c::acc, cs')
else
(PreBasis.revImplode acc, cs)
| NONE => (PreBasis.revImplode acc, cs))
else
(PreBasis.revImplode acc, cs)
in
aux (0, [], cs)
end
fun splitlNC (n:int) (p:char -> bool) (f:char -> char) getc cs =
let fun aux (count, acc, cs) =
if count < n then
(case getc cs of
SOME (c, cs') =>
if p c then
aux (count+1, f c::acc, cs')
else
(acc, cs)
| NONE => (rev acc, cs))
else
(rev acc, cs)
in
aux (0, [], cs)
end
end
;
