require "intnewmap";
functor IntB23Tree () : INTNEWMAP =
struct
type object = int
type 'a result = 'a option
datatype 'a Node =
L1 of (object * 'a result) |
L2 of (object * 'a result * object * 'a result) |
N2 of 'a Node * object * 'a result * 'a Node |
N3 of 'a Node * object * 'a result * 'a Node * object * 'a result * 'a Node
datatype 'a T = EMPTY | TREE of 'a Node * int
val empty = EMPTY
fun is_empty EMPTY = true
| is_empty (TREE (_,0)) = true
| is_empty _ = false
datatype 'a Result =
SINGLE of 'a Node |
SPLIT of 'a Node * object * 'a result * 'a Node
datatype 'a Cont =
C1 of 'a Node * object * 'a result * 'a Node |
C2 of 'a Node * object * 'a result * 'a Node |
C3 of 'a Node * object * 'a result * 'a Node * object * 'a result * 'a Node |
C4 of 'a Node * object * 'a result * 'a Node * object * 'a result * 'a Node |
C5 of 'a Node * object * 'a result * 'a Node * object * 'a result * 'a Node
fun unwind1 (t1',C1(t1,k1,v1,t2)::c) = unwind1(N2 (t1',k1,v1,t2),c)
| unwind1 (t2',C2(t1,k1,v1,t2)::c) = unwind1 (N2 (t1,k1,v1,t2'),c)
| unwind1 (t1',C3(t1,k1,v1,t2,k2,v2,t3)::c) = unwind1 (N3 (t1',k1,v1,t2,k2,v2,t3),c)
| unwind1 (t2',C4(t1,k1,v1,t2,k2,v2,t3)::c) = unwind1 (N3 (t1,k1,v1,t2',k2,v2,t3),c)
| unwind1 (t3',C5(t1,k1,v1,t2,k2,v2,t3)::c) = unwind1 (N3 (t1,k1,v1,t2,k2,v2,t3'),c)
| unwind1 (t,[]) = t
fun unwind2 (t1',k1',v1',t2',C1(t1,k1,v1,t2)::c) = unwind1 (N3 (t1',k1',v1',t2',k1,v1,t2),c)
| unwind2 (t2',k2',v2',t3',C2(t1,k1,v1,t2)::c) = unwind1 (N3 (t1,k1,v1,t2',k2',v2',t3'),c)
| unwind2 (t1',k1',v1',t2',C3(t1,k1,v1,t2,k2,v2,t3)::c) =
unwind2 (N2 (t1',k1',v1',t2'),k1,v1,N2 (t2,k2,v2,t3),c)
| unwind2 (t2',k2',v2',t3',C4(t1,k1,v1,t2,k2,v2,t3)::c) =
unwind2 (N2 (t1,k1,v1,t2'),k2',v2',N2 (t3',k2,v2,t3),c)
| unwind2 (t3',k3',v3',t4',C5(t1,k1,v1,t2,k2,v2,t3)::c) =
unwind2 (N2 (t1,k1,v1,t2),k2,v2,N2 (t3',k3',v3',t4'),c)
| unwind2 (t1,k1,v1,t2,[]) = N2 (t1,k1,v1,t2)
fun insert (t,k,v,combine) =
let
fun scan (L1 (k1,v1),c) =
if k < k1
then
unwind1(L2 (k,v,k1,v1),c)
else
if k = k1
then unwind1 (L1 (k,combine v1),c)
else unwind1 (L2 (k1,v1,k,v),c)
| scan (L2 (k1,v1,k2,v2),c) =
if k < k1
then unwind2 (L1 (k,v),k1,v1,L1 (k2,v2),c)
else if k < k2
then
if k = k1
then unwind1 (L2 (k,combine v1,k2,v2),c)
else unwind2 (L1 (k1,v1),k,v,L1 (k2,v2),c)
else if k = k2 then unwind1 (L2 (k1,v1,k,combine v2),c)
else unwind2 (L1 (k1,v1),k2,v2,L1 (k,v),c)
| scan (N2 (data as (t1,k1,v1,t2)),c) =
if k < k1
then scan (t1,C1 data :: c)
else if k = k1
then unwind1 (N2 (t1,k,combine v1,t2),c)
else scan (t2,C2 data :: c)
| scan (N3 (data as (t1,k1,v1,t2,k2,v2,t3)),c) =
if k < k1
then scan (t1,C3 data :: c)
else if k < k2
then if k = k1
then unwind1 (N3 (t1,k,combine v1,t2,k2,v2,t3),c)
else scan (t2,C4 data::c)
else if k = k2
then unwind1 (N3 (t1,k1,v1,t2,k,combine v2,t3),c)
else scan (t3,C5 data::c)
in
scan (t,[])
end
fun define (EMPTY,k,v) = TREE (L1 (k,SOME v),1)
| define (TREE (t,size),k,v) =
let
val sz = ref (size+1)
val newv = SOME v
fun combine (SOME _) = (sz := size; newv)
| combine _ = newv
in
TREE (insert (t,k,newv,combine),!sz)
end
fun define' (t,(k,v)) = define (t,k,v)
fun combine f (EMPTY,k,v) = TREE (L1 (k,SOME v),1)
| combine f (TREE (t,size),k,v) =
let
val newv = SOME v
val sz = ref (size+1)
fun combine (SOME v') = (sz := size; SOME (f (k,v,v')))
| combine _ = SOME v
in
TREE (insert (t,k,newv,combine),!sz)
end
fun undefine (EMPTY,k) = EMPTY
| undefine (TREE (t,size),k) =
let
val sz = ref size
fun combine (SOME _) = (sz := size-1; NONE)
| combine NONE = NONE
fun remove (t,k) =
let
fun scan (L1 (k1,v1),c) =
if k = k1
then unwind1 (L1 (k,combine v1),c)
else unwind1 (L1 (k1,v1),c)
| scan (L2 (k1,v1,k2,v2),c) =
if k = k1
then unwind1 (L2 (k,combine v1,k2,v2),c)
else if k = k2
then unwind1 (L2 (k1,v1,k,combine v2),c)
else unwind1 (L2 (k1,v1,k2,v2),c)
| scan (N2 (data as (t1,k1,v1,t2)),c) =
if k < k1
then scan (t1,C1 data :: c)
else if k = k1
then unwind1 (N2 (t1,k,combine v1,t2),c)
else scan (t2,C2 data :: c)
| scan (N3 (data as (t1,k1,v1,t2,k2,v2,t3)),c) =
if k < k1
then scan (t1,C3 data :: c)
else if k < k2
then if k = k1
then unwind1 (N3 (t1,k,combine v1,t2,k2,v2,t3),c)
else scan (t2,C4 data::c)
else if k = k2
then unwind1 (N3 (t1,k1,v1,t2,k,combine v2,t3),c)
else scan (t3,C5 data::c)
in
scan (t,[])
end
in
TREE (remove (t,k),!sz)
end
fun tryApply' (EMPTY,k) = NONE
| tryApply' (TREE (t,_),k) =
let
fun find (L1 (k1,v1)) =
if k = k1 then v1 else NONE
| find (L2 (k1,v1,k2,v2)) =
if k = k1 then v1 else if k = k2 then v2 else NONE
| find (N2 (t1,k1,v1,t2)) =
if k < k1 then find t1 else if k = k1 then v1 else find t2
| find (N3 (t1,k1,v1,t2,k2,v2,t3)) =
if k < k1 then find t1
else if k < k2 then if k = k1 then v1 else find t2
else if k = k2 then v2 else find t3
in
find t
end
fun tryApply t k = tryApply'(t,k)
exception Undefined
fun apply' (t,k) =
case tryApply' (t,k) of
NONE => raise Undefined
| SOME x => x
fun apply t k = apply' (t,k)
fun apply_default' (t,v,k) =
case tryApply' (t,k) of
NONE => v
| SOME x => x
fun apply_default (t,v) k = apply_default'(t,v,k)
fun fold f (acc,EMPTY) = acc
| fold f (acc,TREE (t,_)) =
let
fun one (k,SOME x,acc) =
f (acc,k,x)
| one (_,_,acc) = acc
fun aux (L1 (k1,v1),acc) = one (k1,v1,acc)
| aux (L2 (k1,v1,k2,v2),acc) = one (k2,v2,one (k1,v1,acc))
| aux (N2 (t1,k1,v1,t2),acc) =
aux (t2,one(k1,v1,aux (t1,acc)))
| aux (N3 (t1,k1,v1,t2,k2,v2,t3),acc) =
aux (t3,one(k2,v2,aux (t2,one (k1,v1,aux (t1,acc)))))
in
aux (t,acc)
end
val fold_in_order = fold
fun fold_in_rev_order f (acc,EMPTY) = acc
| fold_in_rev_order f (acc,TREE (t,_)) =
let
fun one (k,SOME x,acc) =
f (acc,k,x)
| one (_,_,acc) = acc
fun aux (L1 (k1,v1),acc) = one (k1,v1,acc)
| aux (L2 (k1,v1,k2,v2),acc) = one (k1,v1,one (k2,v2,acc))
| aux (N2 (t1,k1,v1,t2),acc) =
aux (t1,one(k1,v1,aux (t2,acc)))
| aux (N3 (t1,k1,v1,t2,k2,v2,t3),acc) =
aux (t1,one(k1,v1,aux (t2,one (k2,v2,aux (t3,acc)))))
in
aux (t,acc)
end
fun union args = fold define args
fun merge f = fold (combine (fn (ob, im, im') => f(im, im')))
exception Found of int
fun rank' (m, ob) =
let
fun f (res, object:int, _) =
if op<(object, ob) then
res + 1
else
if op=(object, ob) then
raise Found(res)
else
raise Undefined
in
(ignore(fold_in_order f (0, m)); raise Undefined)
handle Found(res) => res
end
fun rank m ob = rank' (m, ob)
fun to_list EMPTY = []
| to_list (TREE (t,_)) =
let
fun add (k,SOME x,acc) =
(k,x)::acc
| add (_,_,acc) = acc
fun aux (L1 (k1,v1),acc) = add (k1,v1,acc)
| aux (L2 (k1,v1,k2,v2),acc) = add (k1,v1,add(k2,v2,acc))
| aux (N2 (t1,k1,v1,t2),acc) =
aux (t1,add(k1,v1,aux (t2,acc)))
| aux (N3 (t1,k1,v1,t2,k2,v2,t3),acc) =
aux (t1,add(k1,v1,aux (t2,add (k2,v2,aux (t3,acc)))))
in
aux (t,[])
end
val to_list_ordered = to_list
fun from_list l =
let
fun aux ((k,v)::l,acc) =
aux (l,define (acc,k,v))
| aux ([],acc) = acc
in
aux (l,EMPTY)
end
fun range EMPTY = []
| range (TREE (t,_)) =
let
fun add (k,SOME x,acc) = x::acc
| add (_,_,acc) = acc
fun aux (L1 (k1,v1),acc) = add (k1,v1,acc)
| aux (L2 (k1,v1,k2,v2),acc) = add (k1,v1,add(k2,v2,acc))
| aux (N2 (t1,k1,v1,t2),acc) =
aux (t1,add(k1,v1,aux (t2,acc)))
| aux (N3 (t1,k1,v1,t2,k2,v2,t3),acc) =
aux (t1,add(k1,v1,aux (t2,add (k2,v2,aux (t3,acc)))))
in
aux (t,[])
end
val range_ordered = range
fun domain EMPTY = []
| domain (TREE (t,_)) =
let
fun add (k,SOME x,acc) = k::acc
| add (_,_,acc) = acc
fun aux (L1 (k1,v1),acc) = add (k1,v1,acc)
| aux (L2 (k1,v1,k2,v2),acc) = add (k1,v1,add(k2,v2,acc))
| aux (N2 (t1,k1,v1,t2),acc) =
aux (t1,add(k1,v1,aux (t2,acc)))
| aux (N3 (t1,k1,v1,t2,k2,v2,t3),acc) =
aux (t1,add(k1,v1,aux (t2,add (k2,v2,aux (t3,acc)))))
in
aux (t,[])
end
val domain_ordered = domain
fun size EMPTY = 0
| size (TREE (_,n)) = n
fun iterate f EMPTY = ()
| iterate f (TREE (t,_)) =
let
fun one (k,SOME x) = f (k,x)
| one (_,_) = ()
fun aux (L1 (k1,v1)) = one(k1,v1)
| aux (L2 (k1,v1,k2,v2)) = (one (k1,v1);one(k2,v2))
| aux (N2 (t1,k1,v1,t2)) =
(aux t1;one (k1,v1);aux t2)
| aux (N3 (t1,k1,v1,t2,k2,v2,t3)) =
(aux t1;one(k1,v1);aux t2;one (k2,v2);aux t3)
in
aux t
end
val iterate_ordered = iterate
fun map f EMPTY = EMPTY
| map f (TREE (t,size)) =
let
fun one (k,SOME x) = SOME (f (k,x))
| one (_,_) = NONE
fun aux (L1 (k1,v1)) = L1(k1,one(k1,v1))
| aux (L2 (k1,v1,k2,v2)) = L2(k1,one (k1,v1),k2,one(k2,v2))
| aux (N2 (t1,k1,v1,t2)) =
N2 (aux t1,k1,one (k1,v1),aux t2)
| aux (N3 (t1,k1,v1,t2,k2,v2,t3)) =
N3(aux t1,k1,one(k1,v1),aux t2,k2,one (k2,v2),aux t3)
in
TREE (aux t,size)
end
fun forall p EMPTY = true
| forall p (TREE (t,_)) =
let
fun one (k,SOME x) = p (k,x)
| one (_,_) = true
fun aux (L1 (k1,v1)) = one(k1,v1)
| aux (L2 (k1,v1,k2,v2)) = one (k1,v1) andalso one(k2,v2)
| aux (N2 (t1,k1,v1,t2)) =
aux t1 andalso one (k1,v1) andalso aux t2
| aux (N3 (t1,k1,v1,t2,k2,v2,t3)) =
aux t1 andalso one(k1,v1) andalso aux t2 andalso one (k2,v2) andalso aux t3
in
aux t
end
fun exists p EMPTY = false
| exists p (TREE (t,_)) =
let
fun one (k,SOME x) = p (k,x)
| one (_,_) = false
fun aux (L1 (k1,v1)) = one(k1,v1)
| aux (L2 (k1,v1,k2,v2)) = one (k1,v1) orelse one(k2,v2)
| aux (N2 (t1,k1,v1,t2)) =
aux t1 orelse one (k1,v1) orelse aux t2
| aux (N3 (t1,k1,v1,t2,k2,v2,t3)) =
aux t1 orelse one(k1,v1) orelse aux t2 orelse one (k2,v2) orelse aux t3
in
aux t
end
fun eq f (m1, m2) =
size m1 = size m2 andalso
((forall (fn (ob, im) => f (apply'(m1, ob), im)) m2)
handle Undefined => false)
fun string obP imP {start, domSep, itemSep, finish} m =
let
fun make ((doSep, res), ob, im) =
(true, obP ob :: domSep :: imP im :: (if doSep then itemSep :: res else res))
in
concat(start :: #2 (fold_in_rev_order make ((false, [finish]), m)))
end
end;
