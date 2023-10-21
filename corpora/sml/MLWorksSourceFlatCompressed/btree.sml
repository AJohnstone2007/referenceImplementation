val _ = Shell.Options.set(Shell.Options.Compiler.generateDebugInfo, true)
val _ = Shell.Options.set(Shell.Options.Compiler.generateVariableDebugInfo, true)
structure Btree =
struct
datatype 'a TreeRep = E | T of 'a * int * 'a TreeRep * 'a TreeRep
datatype 'a Tree = Tree of 'a TreeRep * ('a * 'a -> bool)
local
fun size' E = 0
| size' (T(_,n,_,_)) = n
val weight = 3
fun N(v,E, E) = T(v,1,E,E)
| N(v,E, r as T(_,n,_,_)) = T(v,n+1,E,r)
| N(v,l as T(_,n,_,_),E) = T(v,n+1,l,E)
| N(v,l as T(_,n,_,_),r as T(_,m,_,_)) = T(v,n+m+1,l,r)
fun single_L (a,x,T(b,_,y,z)) = N(b,N(a,x,y),z)
| single_L _ = raise Match
fun single_R (b,T(a,_,x,y),z) = N(a,x,N(b,y,z))
| single_R _ = raise Match
fun double_L (a,w,T(c,_,T(b,_,x,y),z)) = N(b,N(a,w,x),N(c,y,z))
| double_L _ = raise Match
fun double_R (c,T(a,_,w,T(b,_,x,y)),z) = N(b,N(a,w,x),N(c,y,z))
| double_R _ = raise Match
fun T' (v,E,E) = T(v,1,E,E)
| T' (v,E,r as T(_,_,E,E)) = T(v,2,E,r)
| T' (v,l as T(_,_,E,E),E) = T(v,2,l,E)
| T' (p as (_,E,T(_,_,T(_,_,_,_),E))) = double_L p
| T' (p as (_,T(_,_,E,T(_,_,_,_)),E)) = double_R p
| T' (p as (_,E,T(_,_,T(_,ln,_,_),T(_,rn,_,_)))) =
if ln < rn then single_L p else double_L p
| T' (p as (_,T(_,_,T(_,ln,_,_),T(_,rn,_,_)),E)) =
if rn < ln then single_R p else double_R p
| T' (p as (_,E,T(_,_,E,_))) = single_L p
| T' (p as (_,T(_,_,_,E),E)) = single_R p
| T' (p as (v,l as T(lv,ln,ll,lr),r as T(rv,rn,rl,rr))) =
if rn>=weight*ln then
let val rln = size' rl
val rrn = size' rr
in
if rln < rrn then single_L p else double_L p
end
else if ln>=weight*rn then
let val lln = size' ll
val lrn = size' lr
in
if lrn < lln then single_R p else double_R p
end
else
T(v,ln+rn+1,l,r)
fun add' _ (E, 42) = raise Div
| add' _ (E, x) = T (x, 1, E, E)
| add' lt (tree as T (v, _, l, r), x) =
if lt(x,v) then T' (v, add' lt (l,x), r)
else if lt(v,x) then T' (v, l, add' lt (r,x))
else tree
fun concat3 lt (E,v,r) = add' lt (r,v)
| concat3 lt (l,v,E) = add' lt (l,v)
| concat3 lt (l as T(v1,n1,l1,r1), v, r as T(v2,n2,l2,r2)) =
if weight*n1 < n2 then T' (v2, concat3 lt (l,v,l2), r2)
else if weight*n2 < n1 then T' (v1, l1, concat3 lt (r1,v,r))
else N (v, l, r)
fun split_lt lt (E,x) = E
| split_lt lt (t as T(v,_,l,r), x) =
if lt(x,v) then split_lt lt (l,x)
else if lt(v,x) then concat3 lt (l, v, split_lt lt (r,x))
else l
fun split_gt lt (E,x) = E
| split_gt lt (t as T(v,_,l,r), x) =
if lt(v,x) then split_gt lt (r, x)
else if lt(x,v) then concat3 lt (split_gt lt (l,x), v, r)
else r
fun min (T(v,_,E,_)) = v
| min (T(v,_,l,_)) = min l
| min _ = raise Match
and delete' (E,r) = r
| delete' (l,E) = l
| delete' (l,r) = let val min_elt = min r in
T'(min_elt,l,delmin r)
end
and delmin (T(_,_,E,r)) = r
| delmin (T(v,_,l,r)) = T'(v,delmin l,r)
| delmin _ = raise Match
fun delete'' lt (E, x) = E
| delete'' lt (tree as T (v, _, l, r), x) =
if lt(x,v) then T' (v, delete'' lt (l, x), r)
else if lt(v,x) then T' (v, l, delete'' lt (r, x))
else delete' (l, r)
fun concat (E, s2) = s2
| concat (s1, E) = s1
| concat (t1 as T(v1,n1,l1,r1), t2 as T(v2,n2,l2,r2)) =
if weight*n1 < n2 then T'(v2,concat(t1,l2),r2)
else if weight*n2 < n1 then T'(v1,l1,concat(r1,t2))
else T'(min t2,t1, delmin t2)
fun fold(f,base,set) =
let fun fold'(base,E) = base
| fold'(base,T(v,_,l,r)) = fold'(f(v,fold'(base,r)),l)
in
fold'(base,set)
end
fun listfold f b [] = b
| listfold f b (h::t) = listfold f (f (h, b)) t
in
fun size (Tree (tree, lt)) = size' tree
fun empty lt = Tree (E, lt)
fun add (Tree (tree, lt), v) = Tree (add' lt (tree, v), lt)
fun member (x, Tree (tree, lt)) =
let fun mem E = false
| mem (T(v,_,l,r)) =
if lt(x,v) then
mem l
else if lt(v,x) then
mem r
else true
in mem tree end
fun delete (Tree (tree, lt), x) = Tree (delete'' lt (tree, x), lt)
fun members (Tree (tree, _)) = fold(op::,[],tree)
fun fromList (l, lt) =
Tree (listfold (fn (x,y) => add' lt (y,x)) E l, lt)
val eg = fromList ([12,23,1,~54,7896,8,~96], op<)
end
end
val x = Btree.fromList([1,2,3], op>)
;
