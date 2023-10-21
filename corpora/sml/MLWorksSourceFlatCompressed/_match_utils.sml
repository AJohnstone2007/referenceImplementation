require "match_utils";
functor Match_utils () : MATCH_UTILS =
struct
fun member (_,[]) = false
| member (a,h::t) = (a=h) orelse member(a,t)
fun union_lists [] rest = rest
| union_lists rest [] = rest
| union_lists (head::tail) rest =
if member(head, rest) then
union_lists tail rest
else
union_lists tail (head::rest)
end
;
