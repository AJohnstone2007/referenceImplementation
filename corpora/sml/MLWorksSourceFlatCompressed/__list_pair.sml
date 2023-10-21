require "list_pair";
structure ListPair : LIST_PAIR =
struct
local
fun red(acc, x :: xs, y :: ys) = red ((x, y) :: acc, xs, ys)
| red (acc, _, _) = rev acc
in
val zip : ('a list * 'b list) -> ('a * 'b) list =
fn (x, y) => red([], x, y)
end
local
fun red(acc1, acc2, []) = (rev acc1, rev acc2)
| red(acc1, acc2, (x, y) :: rest) = red(x:: acc1, y :: acc2, rest)
in
val unzip : ('a * 'b) list -> ('a list * 'b list) =
fn x => red([], [], x)
end
val map : ('a * 'b -> 'c) -> ('a list * 'b list) -> 'c list =
fn f =>
let
fun red(acc, x :: xs, y :: ys) = red(f(x, y) :: acc, xs, ys)
| red(acc, _, _) = rev acc
in
fn (x, y) => red([], x, y)
end
val app : ('a * 'b -> unit) -> ('a list * 'b list) -> unit =
fn f =>
let
fun red(x :: xs, y :: ys) = (f(x, y); red(xs, ys))
| red _ = ()
in
red
end
fun foldl f z (xs, ys) =
let
fun loop (acc, [], _) = acc
| loop (acc, _, []) = acc
| loop (acc, x::xs, y::ys) = loop (f (x, y, acc), xs, ys)
in
loop (z, xs, ys)
end
fun foldr f z (xs, ys) =
let
fun loop ([], _) = z
| loop ( _, []) = z
| loop (x::xs, y::ys) = f (x, y, loop (xs, ys))
in
loop (xs, ys)
end
val all : ('a * 'b -> bool) -> ('a list * 'b list) -> bool =
fn f =>
let
fun red(x :: xs, y :: ys) = f(x, y) andalso red(xs, ys)
| red _ = true
in
red
end
val exists : ('a * 'b -> bool) -> ('a list * 'b list) -> bool =
fn f =>
let
fun red(x :: xs, y :: ys) = f(x, y) orelse red(xs, ys)
| red _ = false
in
red
end
end
;
