require "crash";
require "bignum";
require "^.basis.__string";
require "^.basis.__char";
require "^.basis.__list";
functor BigNumFun
(structure Crash : CRASH
val check_range : bool
val largest_int : string
val smallest_int : string
val largest_word : string) : BIGNUM =
struct
fun int_plus(x, y: int) = x + y
fun int_minus(x, y: int) = x - y
fun int_less_eq(x, y: int) = x <= y
fun strip_zeros (#"0" :: (xs as #"0"::_)) = strip_zeros xs
| strip_zeros (#"0" :: (xs as _::_)) = xs
| strip_zeros x = x
datatype bignum = Positive of int list | Negative of int list | Zero
val base = 256
val bigone = Positive [1]
fun int_to_bignum 0 = Zero
| int_to_bignum i =
if i > 0 andalso i < base then Positive[i]
else if i < 0 andalso i > ~base then Negative [~i]
else
Crash.impossible"int_to_bignum:int too big for simple conversion"
fun fromHexChar (c:char):int =
if #"0" <= c andalso c <= #"9" then
ord c - ord #"0"
else if #"A" <= c andalso c <= #"F" then
ord c - ord #"A" + 10
else if #"a" <= c andalso c <= #"f" then
(ord c) - ord #"a" + 10
else raise Fail ("fromHexChar " ^ (str c))
exception Unrepresentable
local
fun convert_intlist ([],acc) = acc
| convert_intlist (a::rest,acc) =
convert_intlist (rest,a + base * acc)
handle Overflow => raise Unrepresentable
in
fun bignum_to_int Zero = 0
| bignum_to_int (Positive l) =
convert_intlist (rev l,0)
| bignum_to_int (Negative l) =
~ (convert_intlist (rev l,0))
end
exception Runtime of string
val Prod_exn = Runtime "Prod"
and Sum_exn = Runtime "Sum"
and Diff_exn = Runtime "Diff"
and Mod_exn = Runtime "Mod"
and Div_exn = Runtime "Div"
and Neg_exn = Runtime "Neg"
and Abs_exn = Runtime "Abs"
fun normilist l =
let
fun strip_zero (0::xs) = strip_zero xs
| strip_zero xs = xs
in
rev (strip_zero (rev l))
end
fun addilists (x,y) =
let
fun add (acc, x::xs, y::ys, carry) = add((x+y+carry) mod base :: acc,
xs, ys,
(x+y+carry) div base)
| add (acc, [], [], carry) = if carry=0 then rev acc
else add(acc,[carry],[],0)
| add (acc, x::xs, [], carry) = add((x+carry) mod base :: acc,
xs, [], (x+carry) div base)
| add (acc, [], ys, carry) = add(acc, ys, [], carry)
in
normilist(add ([], x, y, 0))
end
fun subilists (x,y) =
let
fun sbt (acc, x::xs, y::ys, carry) = sbt((x-y+carry) mod base :: acc,
xs, ys,
(x-y+carry) div base)
| sbt (acc, [], [], carry) = if carry=0
then rev acc
else sbt(acc,[carry],[],0)
| sbt (acc, xs, [], carry) = if carry=0
then rev acc @ xs
else sbt(acc,xs,[~carry],0)
| sbt (acc, [], y::ys, carry) = sbt((~y+carry) mod base :: acc,
[], ys,
(~y+carry) div base)
in
normilist(sbt ([],x,y,0))
end
fun multilists(x: int list, y:int list) =
let
val lx = length x and ly = length y
fun buildpps (shortlist, longlist) =
let
fun pp dig =
let
fun formpp (acc,x::xs,carry) =
formpp((x*dig+carry) mod base :: acc,
xs,
(x*dig+carry) div base)
| formpp (acc,[],carry) =
if carry=0
then rev acc
else formpp(acc,[0],carry)
in
normilist(formpp ([],longlist,0))
end
fun shiftleft (pref,acc,[]) = rev acc
| shiftleft (pref,acc,x::xs) = shiftleft(0::pref,
(pref@x)::acc,
xs)
in
shiftleft([],[],map pp shortlist)
end
in
foldl addilists [] (buildpps (if lx<ly then (x,y) else (y,x)))
end
fun ltilists(x:int list, y:int list) =
let
val lx=(length x) and ly=(length y)
fun lt ([],[]) = false
| lt ([x:int],[y]) = x<y
| lt (x::xs,y::ys) = x<y orelse (x=y andalso lt(xs,ys))
| lt _ = Crash.impossible "ltilists"
in
(lx < ly) orelse (lx=ly andalso lt(rev x,rev y))
end
local
fun fst (x,_) = x
fun split (n,xs) =
let
fun take(0, acc, xs) = (rev acc, xs)
| take(n, acc, x::xs) = take(n-1, x::acc, xs)
| take(_, _, []) = Crash.impossible "split"
in
take (n, [], xs)
end
fun pad (n,xs) =
let
fun padzero (0,xs) = xs
| padzero (n,xs) = padzero(n-1,0::xs)
in
if (length xs<n)
then padzero(n - (length xs),xs)
else xs
end
in
fun divilists ([]:int list, v:int list) = ([],[])
| divilists (u, [v]) =
let
fun noddy(qs,u::us,r) = noddy(((r*base+u) div v)::qs,
us,
(r*base+u) mod v)
| noddy(qs,[],r) = (normilist qs, normilist [r])
in
noddy ([],rev u,0)
end
| divilists (u', v') =
if (length u') >= (length v')
then
let
val n = length v'
val m = (length u') - n
val b = base
val d = b div (hd (rev v') + 1)
val u = let
val u'' = multilists(u',[d])
in
if (length u'')=(length u')
then u''@[0]
else u''
end
val v = multilists(v',[d])
val (v1,v2) = case (rev v) of
(v1::v2::_) => (v1,v2)
| _ => Crash.impossible "size of v in divilists"
fun well_hard (us, qs, ~1) = (normilist qs,
fst(divilists(normilist
(rev us), [d])))
| well_hard (us as uj::uj1::uj2::_, qs, jm) =
let
val (u,urest) = split(n+1,us)
fun fiddle_qhat qhat =
if (qhat*v2) > ((uj*b + uj1 - qhat*v1) * b + uj2)
then fiddle_qhat (qhat-1)
else qhat
val qhat = fiddle_qhat (if uj=v1
then b-1
else (uj*b + uj1) div v1)
val (qj,u) =
let
val qhv = multilists(v, [qhat])
in
if ltilists(normilist (rev u), qhv)
then
let
val u' = addilists(pad(n+1,[1]),
normilist (rev u))
val bcomp = subilists(u',qhv)
in
(qhat-1, rev(addilists(v,bcomp)))
end
else
(qhat, rev (subilists(normilist (rev u), qhv)))
end
in
well_hard((tl (pad(n+1,u)))@urest, qj::qs, jm-1)
end
| well_hard (us,qs,jm) = Crash.impossible "well hard"
in
well_hard (rev u, [], m)
end
else ([],u')
end
fun bignum_to_string bn =
let
fun barbeque (digs, value) =
let
val (q,r) = divilists(value, [10])
in
if q=[]
then (r@digs)
else barbeque((case r of []=>0 | [r]=>r | _=>
Crash.impossible "base in b_to_s")::digs, q)
end
fun chargrill il = implode (strip_zeros (map (fn c=>chr (c + ord #"0"))
(barbeque([],il))))
in
case bn of
Positive il => chargrill il
| Negative il => str #"~" ^ (chargrill il)
| Zero => "0"
end
fun string_to_bignum s =
let
val (signval,rest) = case explode s of
(#"~" :: ss) => (false,strip_zeros ss)
| ss => (true,strip_zeros ss)
fun make_big (biglist, []) = biglist
| make_big (biglist, c::cs) =
let
val digval = ord c - ord #"0"
val biglist' = addilists(multilists(biglist, [10]),
[digval])
in
make_big (biglist',cs)
end
in
if List.all Char.isDigit rest then
case rest of
[#"0"] => Zero
| _ => let
val ilist = make_big ([],rest)
in
if signval then
Positive ilist
else
Negative ilist
end
else
Crash.impossible ("string_to_bignum: bad characters in string '" ^ s ^ "'")
end
infix 4 eq <> < > <= >=
infix 6 + -
infix 7 div mod *
fun Zero eq Zero = true
| (Positive x) eq (Positive y) = x=y
| (Negative x) eq (Negative y) = x=y
| _ eq _ = false
fun x <> y = not(x eq y)
fun Zero < Zero = false
| Zero < (Positive _) = true
| (Positive x) < (Positive y) = ltilists(x,y)
| (Negative x) < (Negative y) = ltilists(y,x)
| (Negative x) < _ = true
| _ < _ = false
fun x <= y = (x eq y) orelse (x < y)
fun x > y = not(x <= y)
fun x >= y = not(x < y)
local
val maximum_positive = string_to_bignum largest_int
val minimum_negative = string_to_bignum smallest_int
val maximum_positive_word = string_to_bignum largest_word
in
fun normalise x =
case x of
Zero => Zero
| Positive l => (case normilist l of
[] => Zero
| x => Positive x)
| Negative l => (case normilist l of
[] => Zero
| x => Negative x)
fun range_check(bignum, exn) =
let
val rc =
if check_range then
fn Zero => Zero
| (n as Positive _) =>
if n <= maximum_positive then n else
(
raise exn)
| (n as Negative _) =>
if n >= minimum_negative then n else
(
raise exn)
else
fn x => x
in
rc(normalise bignum)
end
fun word_range_check(bignum, exn) =
let
val rc =
if check_range then
fn Zero => Zero
| (n as Positive _) =>
if n <= maximum_positive_word then n else
(
raise exn)
| (n as Negative _) => raise exn
else
fn x => x
in
rc(normalise bignum)
end
end
fun neg Zero = Zero
| neg (Positive x) = Negative x
| neg (Negative x) = Positive x
fun ~x = range_check(neg x, Neg_exn)
fun abs Zero = Zero
| abs (Positive x) = Positive x
| abs (Negative x) = range_check(Positive x, Abs_exn)
fun (Positive x) + (Positive y) =range_check(Positive(addilists(x,y)),Sum_exn)
| (Negative x) + (Negative y) =range_check(Negative(addilists(x,y)),Sum_exn)
| (Positive x) + (Negative y) =
if x=y then Zero
else if (Positive x) > (Positive y)
then range_check(Positive(subilists(x,y)),Sum_exn)
else range_check(Negative(subilists(y,x)),Sum_exn)
| Zero + anything = anything
| x + y = y + x
fun x - y = (x + (neg y)) handle Runtime "Sum" => raise Diff_exn
fun Zero * anything = Zero
| anything * Zero = Zero
| (Positive x)*(Positive y) =range_check(Positive(multilists(x,y)),Prod_exn)
| (Negative x)*(Negative y) =range_check(Positive(multilists(x,y)),Prod_exn)
| (Positive x)*(Negative y) =range_check(Negative(multilists(x,y)),Prod_exn)
| (Negative x)*(Positive y) =range_check(Negative(multilists(x,y)),Prod_exn)
fun word_plus((Positive x), (Positive y)) =
word_range_check(Positive(addilists(x,y)), Sum_exn)
| word_plus(x as Positive _, Zero) = x
| word_plus(x as Negative _, _) = Crash.impossible"word_plus: negative values"
| word_plus(Zero, Zero) = Zero
| word_plus(x, y) = word_plus(y, x)
fun word_star(Zero, _) = Zero
| word_star(Positive x, Positive y) =
word_range_check(Positive(multilists(x, y)), Prod_exn)
| word_star(Negative _, _) =
Crash.impossible"word_star: negative values"
| word_star(x, y) = word_star(y, x)
fun is_positive (Positive _) = true
| is_positive _ = false
local
fun divit((u,v), sign) =
if u=v then sign [1]
else if ltilists(u,v) then Zero
else
let
val (quotient,_) = divilists(u,v)
in
range_check (sign quotient, Runtime "div range")
end
in
fun _ div Zero = raise Div_exn
| Zero div _ = Zero
| (Positive i) div (Positive d) = divit((i,d), Positive)
| (Negative i) div (Negative d) = divit((i,d), Positive)
| (Positive i) div (Negative d) =
divit((subilists(i,[1]),d), Negative) - bigone
| (Negative i) div (Positive d) =
divit((subilists(i,[1]),d), Negative) - bigone
end
fun _ mod Zero = raise Mod_exn
| Zero mod _ = Zero
| i mod d =
let
fun modi(i, d) =
let
val (_, r) = divilists(i, d)
in
r
end
val r = case (i, d) of
(Positive i', Positive d') => normalise(Positive(modi(i', d')))
| (Negative i', Positive d') =>
let
val r = Positive(modi(i', d'))
in
case normalise r of
Zero => Zero
| _ => d - r
end
| (Positive i', Negative d') =>
let
val r = Positive(modi(i', d'))
in
case normalise r of
Zero => Zero
| _ => r + d
end
| (Negative i', Negative d') =>
let
val r = Positive(modi(i', d'))
in
case normalise r of
Zero => Zero
| r => neg r
end
| _ => Crash.impossible"mod: unexpected case"
in
r
end
fun quot(a, b) =
let
val q = a div b
val r = a mod b
in
if r = Zero orelse (a > Zero andalso b > Zero) orelse
(a < Zero andalso b < Zero) then
q
else
q + bigone
end
fun rem(a, b) =
let
val r = a mod b
in
if r = Zero orelse (a > Zero andalso b > Zero) orelse
(a < Zero andalso b < Zero) then
r
else
r - b
end
val bignum_sixteen = int_to_bignum 16
fun convert_hex_to_bignum(chars, ptr) =
let
val size = size chars
fun do_hex_digit(ptr', acc) =
if int_less_eq(size, ptr') then acc
else
let
val digit =
fromHexChar (String.sub (chars, ptr'))
in
do_hex_digit(int_plus(ptr', 1), (bignum_sixteen * acc) +
int_to_bignum digit)
end
in
do_hex_digit(ptr, Zero)
end
fun neg_convert_hex_to_bignum(chars, ptr) =
let
val size = size chars
fun do_hex_digit(ptr', acc) =
if int_less_eq(size, ptr') then acc
else
let
val digit = fromHexChar (String.sub (chars, ptr'))
in
do_hex_digit(int_plus(ptr', 1), (bignum_sixteen * acc) -
int_to_bignum digit)
end
in
do_hex_digit(ptr, Zero)
end
fun convert_hex_word_to_bignum(chars, ptr) =
let
val size = size chars
fun do_hex_digit(ptr', acc) =
if int_less_eq(size, ptr') then acc
else
let
val digit = fromHexChar (String.sub (chars, ptr'))
in
do_hex_digit(int_plus(ptr', 1),
word_plus(word_star(bignum_sixteen, acc),
int_to_bignum digit))
end
in
do_hex_digit(ptr, Zero)
end
fun hex_string_to_bignum (chars:string) =
(if String.sub (chars, 0) = #"~" then
neg_convert_hex_to_bignum (chars, 3)
else
convert_hex_to_bignum(chars, 2))
handle Runtime _ => raise Unrepresentable
fun hex_word_string_to_bignum chars =
(convert_hex_word_to_bignum(chars, 3))
handle Runtime _ => raise Unrepresentable
fun word_string_to_bignum (x:string) =
if String.isPrefix "0w" x then
word_range_check (string_to_bignum (substring (x, 2, int_minus(size x, 2))),
Unrepresentable)
else
Crash.impossible ("word_string_to_bignum:bad word string '" ^ x ^ "'")
val string_to_bignum = fn s=>range_check(string_to_bignum s, Unrepresentable)
end
;
