require "text";
require "intset";
functor IntSetList (
structure Text : TEXT
val int_to_text : int -> Text.T
) : INTSET =
struct
structure Text = Text
type T = int list
type element = int
val empty = []
fun singleton (x : int) = [x]
fun member (set, x : int) =
let
fun member' [] = false
| member' (e::es) = if e = x then true else member' es
in
member' set
end
fun add (set, x) = if member (set, x) then set else x::set
fun remove (set, x : int) =
let
fun find (n, []) = (0, set)
| find (n, e::es) = if e = x then (n, es) else find (n+1, es)
fun head (done, 0, _) = done
| head (done, n, []) = done
| head (done, n, e::es) = head (e::done, n-1, es)
val (n, tail) = find (0, set)
in
head (tail, n, set)
end
fun intersection (_, []) = []
| intersection ([], _) = []
| intersection (set, set') =
let
fun intersection' (passed, []) = passed
| intersection' (passed, e::es) =
if member (set', e) then
intersection' (e::passed, es)
else
intersection' (passed, es)
in
intersection' ([], set)
end
fun union (set, []) = set
| union ([], set) = set
| union (set, set') =
let
fun union' (done, []) = done
| union' (done, e::es) =
if member (set', e) then
union' (done, es)
else
union' (e::done, es)
in
union' (set', set)
end
fun difference (set, []) = set
| difference ([], _) = []
| difference (set, set') =
let
fun difference' (done, []) = done
| difference' (done, e::es) =
if member (set', e) then
difference' (done, es)
else
difference' (e::done, es)
in
difference' ([], set)
end
fun cardinality [] = 0
| cardinality (e::es) =
let
fun length (n, []) = n
| length (n, e::es) = length (n+1, es)
in
length (1, es)
end
fun subset ([], _) = true
| subset (_, []) = false
| subset (set, set') =
let
fun subset' [] = true
| subset' (e::es) = member (set', e) andalso subset' es
in
subset' set
end
fun is_empty [] = true
| is_empty _ = false
fun equal ([], []) = true
| equal ([], _) = false
| equal (_, []) = false
| equal (set, set') = cardinality set = cardinality set' andalso subset (set, set')
fun reduce _ (i, []) = i
| reduce f (i, set) =
let
fun reduce' (i, []) = i
| reduce' (i, e::es) =
reduce' (f (i, e), es)
in
reduce' (i, set)
end
fun iterate f [] = ()
| iterate f set =
let
fun iterate' [] = ()
| iterate' (e::es) = (ignore(f e); iterate' es)
in
iterate' set
end
fun filter f [] = []
| filter f set =
let
fun filter' (passed, []) = passed
| filter' (passed, e::es) =
filter' (if f e then e::passed else passed, es)
in
filter' ([], set)
end
fun to_list set = set
fun from_list [] = []
| from_list list =
let
fun from_list' (done, []) = done
| from_list' (done, x::xs) =
if member (done, x) then
from_list' (done, xs)
else
from_list' (x::done, xs)
in
from_list' ([], list)
end
fun to_text set =
let
infix ^^
val (op^^) = Text.concatenate
val $ = Text.from_string
fun to_text' (text, []) = text
| to_text' (text, [e]) = text ^^ int_to_text e
| to_text' (text, e::es) =
to_text' (text ^^ int_to_text e ^^ $", ", es)
in
to_text' ($"{", set) ^^ $"}"
end
end
;
