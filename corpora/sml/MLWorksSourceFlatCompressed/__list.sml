require "list";
structure List : LIST =
struct
datatype list = datatype list
val op @ = op @
exception Empty=Empty
val hd = hd
val tl = tl
fun last [x] = x
| last (_::xs) = last xs
| last [] = raise Empty
val length = length
fun all p [] = true
| all p (x::xs) = (p x) andalso all p xs
fun exists P =
let
fun test [] = false
| test (x::xs) = (P x) orelse test xs
in
test
end
val app = app
fun filter p list =
let
fun loop (acc, []) = rev acc
| loop (acc, x::xs) =
if p x then loop (x::acc, xs) else loop (acc, xs)
in
loop ([], list)
end
fun partition p list =
let
fun part (ys, ns, []) = (rev ys, rev ns)
| part (ys, ns, x::xs) =
if p x then part (x::ys, ns, xs) else part (ys, x::ns, xs)
in
part ([], [], list)
end
fun find f =
let
fun red [] = NONE
| red (x :: xs) =
if f x then SOME x else red xs
in
red
end
val foldl = foldl
val foldr = foldr
fun concat x = foldr op@ [] x
val map = map
fun nth ([], _) = raise Subscript
| nth (x :: _, 0) = x
| nth (_ :: xs, n) = nth (xs, n-1)
fun take (_, 0) = []
| take ([], _) = raise Subscript
| take (x::xs, n) = x::take (xs, n-1)
fun drop (arg as (xs, n)) =
if n < 0 then
raise Subscript
else
let fun scan (xs, 0) = xs
| scan ([], _) = raise Subscript
| scan (_::xs, n) = scan (xs, n-1)
in
scan arg
end
val null = null
val rev = rev
fun revAppend([], acc) = acc
| revAppend(x :: xs, acc) = revAppend(xs, x :: acc)
fun mapPartial f =
let
fun red(acc, []) = rev acc
| red(acc, x :: xs) =
red((case f x of NONE => acc | SOME x => x :: acc), xs)
in
fn list => red([], list)
end
fun tabulate(n, f) =
if n < 0 then
raise Size
else
let
fun red(m, acc) = if m = n then rev acc else red(m+1, f m :: acc)
in
red(0, [])
end
end
;
