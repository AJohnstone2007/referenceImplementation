require "set";
functor Set () : SET =
struct
fun length l =
let
fun len (acc,[]) = acc
| len (acc,_::ls) = len(acc+1,ls)
in
len (0,l)
end
fun reduce_left f =
let
fun itl (i,[]) = i
| itl (i,x::xs) = itl(f(x,i),xs)
in
itl
end
type ''a Set = ''a list
val empty_set = []
fun empty_setp [] = true
| empty_setp _ = false
fun is_member(_, []) = false
| is_member(a,h::t) = (a = h) orelse is_member (a,t)
fun intersect ([],_) = false
| intersect (a::b,x) =
let
fun loop(_, []) = intersect (b,x)
| loop(a,h::t) = (a = h) orelse loop (a,t)
in
loop (a,x)
end
fun add_member (m,l) = if is_member (m,l) then l else m::l
fun singleton e = [e]
fun union ([],l) = l
| union (h::t,l) = union (t,add_member (h,l))
fun intersection(set1,set2) =
let
fun intersect(result,h::t,set) =
if is_member (h,set)
then intersect(h::result,t,set)
else intersect(result,t,set)
| intersect(result,[],set) = result
in
case set2 of
[] => []
| _ => intersect([],set1,set2)
end
fun subset ([],l2) = true
| subset (h::t,l2) =
is_member (h,l2)
andalso
subset (t,l2)
fun setdiff ([],_) = []
| setdiff (h::t,l) =
if is_member (h,l)
then setdiff(t,l) else h::setdiff(t,l)
fun seteq(set1,set2) =
(length(set1) = length(set2)) andalso subset(set1,set2)
fun set_to_list set = set
fun list_to_set alist = reduce_left add_member (empty_set,alist)
val map = fn f => fn s => list_to_set (map f s)
fun fold f =
let
fun itl (i,[]) = i
| itl (i,x::xs) = itl(f(i,x),xs)
in
itl
end
fun set_print ([], _) = ""
| set_print(x, printfun) =
let
fun print_sub([], acc) = concat(rev acc)
| print_sub([x], acc) = concat(rev(printfun x :: acc))
| print_sub(x :: xs, acc) = print_sub(xs, "," :: printfun x :: acc)
in
print_sub(x, [])
end
val disjoin = op @
end
;
