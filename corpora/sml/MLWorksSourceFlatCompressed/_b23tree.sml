require "map";
functor B23Tree () : MAP =
struct
type 'a result = 'a option
datatype ('a,'b) Node =
L1 of ('a * 'b result) |
L2 of ('a * 'b result * 'a * 'b result) |
N2 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node |
N3 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node * 'a * 'b result * ('a,'b) Node
datatype ('a,'b) map =
EMPTY of ('a * 'a -> bool) * ('a * 'a -> bool)
| TREE of ('a,'b) Node * int * ('a * 'a -> bool) * ('a * 'a -> bool)
infix eq lt
fun get_lt (EMPTY (op lt,_)) = op lt
| get_lt (TREE (_,_,op lt,_)) = op lt
fun get_eq (EMPTY (_,op eq)) = op eq
| get_eq (TREE (_,_,_,op eq)) = op eq
val get_equality = get_eq
val get_ordering = get_lt
fun empty (op lt,op eq) = EMPTY (op lt,op eq)
fun empty' (op lt) = EMPTY (op lt, op=)
fun is_empty (EMPTY _) = true
| is_empty (TREE (_,0,_,_)) = true
| is_empty _ = false
datatype ('a,'b) Cont =
C1 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node |
C2 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node |
C3 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node * 'a * 'b result * ('a,'b) Node |
C4 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node * 'a * 'b result * ('a,'b) Node |
C5 of ('a,'b) Node * 'a * 'b result * ('a,'b) Node * 'a * 'b result * ('a,'b) Node
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
val count = ref 0
fun insert_value (t,k,v,size,op lt, op eq) =
let
fun scan (L1 (k1,v1),k,op lt,op eq,c) =
if k lt k1
then
unwind1(L2 (k,v,k1,v1),c)
else
if k eq k1
then (size := !size-1;unwind1 (L1 (k,v),c))
else unwind1 (L2 (k1,v1,k,v),c)
| scan (L2 (k1,v1,k2,v2),k,op lt,op eq,c) =
if k lt k1
then unwind2 (L1 (k,v),k1,v1,L1 (k2,v2),c)
else if k lt k2
then
if k eq k1
then (size := !size-1;unwind1 (L2 (k,v,k2,v2),c))
else unwind2 (L1 (k1,v1),k,v,L1 (k2,v2),c)
else if k eq k2 then (size := !size-1;unwind1 (L2 (k1,v1,k,v),c))
else unwind2 (L1 (k1,v1),k2,v2,L1 (k,v),c)
| scan (N2 (data as (t1,k1,v1,t2)),k,op lt,op eq,c) =
if k lt k1
then scan (t1,k,op lt,op eq,C1 data :: c)
else if k eq k1
then (size := !size-1;unwind1 (N2 (t1,k,v,t2),c))
else scan (t2,k,op lt,op eq,C2 data :: c)
| scan (N3 (data as (t1,k1,v1,t2,k2,v2,t3)),k,op lt,op eq,c) =
if k lt k1
then scan (t1,k,op lt,op eq,C3 data :: c)
else if k lt k2
then if k eq k1
then (size := !size-1;unwind1 (N3 (t1,k,v,t2,k2,v2,t3),c))
else scan (t2,k,op lt,op eq,C4 data::c)
else if k eq k2
then (size := !size-1;unwind1 (N3 (t1,k1,v1,t2,k,v,t3),c))
else scan (t3,k,op lt,op eq,C5 data::c)
in
scan (t,k,op lt,op eq,[])
end
fun define (EMPTY (op lt,op eq),k,v) = TREE (L1 (k,SOME v),1,op lt,op eq)
| define (TREE (t,size,op lt, op eq),k,v) =
let
val sz = ref (size+1)
in
TREE (insert_value (t,k,SOME v,sz,op lt,op eq),!sz,op lt,op eq)
end
fun define' (t,(k,v)) = define (t,k,v)
fun insert (t,k,v,combine,op lt, op eq) =
let
fun scan (L1 (k1,v1),k,op lt,op eq,c) =
if k lt k1
then
unwind1(L2 (k,v,k1,v1),c)
else
if k eq k1
then unwind1 (L1 (k,combine v1),c)
else unwind1 (L2 (k1,v1,k,v),c)
| scan (L2 (k1,v1,k2,v2),k,op lt,op eq,c) =
if k lt k1
then unwind2 (L1 (k,v),k1,v1,L1 (k2,v2),c)
else if k lt k2
then
if k eq k1
then unwind1 (L2 (k,combine v1,k2,v2),c)
else unwind2 (L1 (k1,v1),k,v,L1 (k2,v2),c)
else if k eq k2 then unwind1 (L2 (k1,v1,k,combine v2),c)
else unwind2 (L1 (k1,v1),k2,v2,L1 (k,v),c)
| scan (N2 (data as (t1,k1,v1,t2)),k,op lt,op eq,c) =
if k lt k1
then scan (t1,k,op lt,op eq,C1 data :: c)
else if k eq k1
then unwind1 (N2 (t1,k,combine v1,t2),c)
else scan (t2,k,op lt,op eq,C2 data :: c)
| scan (N3 (data as (t1,k1,v1,t2,k2,v2,t3)),k,op lt,op eq,c) =
if k lt k1
then scan (t1,k,op lt,op eq,C3 data :: c)
else if k lt k2
then if k eq k1
then unwind1 (N3 (t1,k,combine v1,t2,k2,v2,t3),c)
else scan (t2,k,op lt,op eq,C4 data::c)
else if k eq k2
then unwind1 (N3 (t1,k1,v1,t2,k,combine v2,t3),c)
else scan (t3,k,op lt,op eq,C5 data::c)
in
scan (t,k,op lt,op eq,[])
end
fun combine f (EMPTY(op lt,op eq),k,v) = TREE (L1 (k,SOME v),1,op lt,op eq)
| combine f (TREE (t,size,op lt,op eq),k,v) =
let
val newv = SOME v
val sz = ref (size+1)
fun combine (SOME v') = (sz := size; SOME (f (k,v',v)))
| combine _ = SOME v
in
TREE (insert (t,k,newv,combine,op lt,op eq),!sz,op lt,op eq)
end
fun undefine (t as (EMPTY _),k) = t
| undefine (TREE (t,size,op lt,op eq),k) =
let
val sz = ref size
fun combine (SOME _) = (sz := size-1; NONE)
| combine NONE = NONE
fun remove (t,k) =
let
fun scan (L1 (k1,v1),c) =
if k eq k1
then unwind1 (L1 (k,combine v1),c)
else unwind1 (L1 (k1,v1),c)
| scan (L2 (k1,v1,k2,v2),c) =
if k eq k1
then unwind1 (L2 (k,combine v1,k2,v2),c)
else if k eq k2
then unwind1 (L2 (k1,v1,k,combine v2),c)
else unwind1 (L2 (k1,v1,k2,v2),c)
| scan (N2 (data as (t1,k1,v1,t2)),c) =
if k lt k1
then scan (t1,C1 data :: c)
else if k eq k1
then unwind1 (N2 (t1,k,combine v1,t2),c)
else scan (t2,C2 data :: c)
| scan (N3 (data as (t1,k1,v1,t2,k2,v2,t3)),c) =
if k lt k1
then scan (t1,C3 data :: c)
else if k lt k2
then if k eq k1
then unwind1 (N3 (t1,k,combine v1,t2,k2,v2,t3),c)
else scan (t2,C4 data::c)
else if k eq k2
then unwind1 (N3 (t1,k1,v1,t2,k,combine v2,t3),c)
else scan (t3,C5 data::c)
in
scan (t,[])
end
in
TREE (remove (t,k),!sz,op lt,op eq)
end
fun tryApply' (EMPTY(op lt,op eq),k) = NONE
| tryApply' (TREE (t,_,op lt,op eq),k) =
let
fun find (L1 (k1,v1)) =
if k eq k1 then v1 else NONE
| find (L2 (k1,v1,k2,v2)) =
if k eq k1 then v1 else if k eq k2 then v2 else NONE
| find (N2 (t1,k1,v1,t2)) =
if k lt k1 then find t1 else if k eq k1 then v1 else find t2
| find (N3 (t1,k1,v1,t2,k2,v2,t3)) =
if k lt k1 then find t1
else if k lt k2 then if k eq k1 then v1 else find t2
else if k eq k2 then v2 else find t3
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
fun tryApply'Eq (EMPTY _, k) = NONE
| tryApply'Eq (TREE (t,size,op lt,op eq),k) =
tryApply' (TREE (t,size,op lt,op =),k)
fun fold f (acc,EMPTY _) = acc
| fold f (acc,TREE (t,_,_,_)) =
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
fun fold_in_rev_order f (acc,EMPTY _) = acc
| fold_in_rev_order f (acc,TREE (t,_,_,_)) =
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
fun size (EMPTY _) = 0
| size (TREE (_,n,_,_)) = n
fun union (EMPTY _,t2) = t2
| union (t1,EMPTY _) = t1
| union (t1,t2) =
fold define (t1,t2)
fun merge f (EMPTY _,t2) = t2
| merge f (t1,EMPTY _) = t1
| merge f (t1,t2) =
fold (combine f) (t1,t2)
exception Found of int
fun rank' (m, ob) =
let
val op lt = get_lt m
val op eq = get_eq m
fun f (res, object, _) =
if object lt ob then
res + 1
else
if object eq ob then
raise Found(res)
else
raise Undefined
in
(ignore(fold_in_order f (0, m)); raise Undefined)
handle Found(res) => res
end
fun rank m ob = rank' (m, ob)
fun to_list (EMPTY _) = []
| to_list (TREE (t,_,_,_)) =
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
fun from_list (op lt,op eq) l =
let
fun aux ((k,v)::l,acc) =
aux (l,define (acc,k,v))
| aux ([],acc) = acc
in
aux (l,EMPTY (op lt,op eq))
end
fun from_list' (op lt) = from_list (op lt,op =)
fun range (EMPTY _) = []
| range (TREE (t,_,_,_)) =
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
fun domain (EMPTY _) = []
| domain (TREE (t,_,_,_)) =
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
fun iterate f (EMPTY _) = ()
| iterate f (TREE (t,_,_,_)) =
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
fun map f (t as EMPTY (op lt,op eq)) = EMPTY (op lt,op eq)
| map f (TREE (t,size,op lt,op eq)) =
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
TREE (aux t,size,op lt,op eq)
end
fun forall p (EMPTY _) = true
| forall p (TREE (t,_,_,_)) =
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
fun exists p (EMPTY _) = false
| exists p (TREE (t,_,_,_)) =
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
local nonfix eq
in
fun eq f (m1, m2) =
size m1 = size m2 andalso
((forall (fn (ob, im) => f (apply'(m1, ob), im)) m2)
handle Undefined => false)
end
fun string obP imP {start, domSep, itemSep, finish} m =
let
fun make ((doSep, res), ob, im) =
(true, obP ob :: domSep :: imP im :: (if doSep then itemSep :: res else res))
in
concat(start :: #2 (fold_in_rev_order make ((false, [finish]), m)))
end
end;
