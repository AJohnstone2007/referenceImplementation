require "perm";
require "$.basis.__list";
structure Perm : PERM =
struct
exception Rest of int
fun rest (h::t, 1) = t
| rest (h::t, n) = h :: rest (t, n - 1)
| rest (_, n) = raise Rest n
fun addHead item l = item :: l
fun perm [item] = [[item]]
| perm l = select (l, List.length l)
and select (l, 0) = []
| select (l, n) = select (l, n - 1)
@ (List.map (addHead (List.nth (l, n - 1))) (perm (rest (l, n))))
fun permString s = List.map implode (perm (explode s))
fun partition [] = [[]]
| partition l = divide (l, List.length l)
and divide (l, 0) = []
| divide (l, pos) =
let
val front = List.take (l, pos)
fun addFront back = front :: back
in
(List.map addFront (partition (List.drop (l, pos))))
@ divide (l, pos - 1)
end
fun partitionString s =
List.map (List.map implode) (partition (explode s))
end
;
