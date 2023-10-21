require "lists";
functor Lists() :LISTS =
struct
exception Assoc
exception Find
exception Hd
exception Last
exception Nth
exception Tl
exception Zip
fun length ([], acc) = acc
| length (_::t, acc) = length(t, 1+acc)
fun member (_,[]) = false
| member (a,h::t) = (a=h) orelse member(a,t)
fun last [x] = x
| last (_::xs) = last xs
| last [] = raise Last
fun rev_append ([],ys) = ys
| rev_append (x::xs,ys) = rev_append(xs,x::ys)
fun adjoin(a,l) = if member(a,l) then l else a::l
local
fun adjoin2(a,b,acc) = if a = b then acc else adjoin(a,acc)
fun filter_sub([],acc) = acc
| filter_sub([a],acc) = adjoin(a,acc)
| filter_sub(a::(l as b :: _),acc) = filter_sub(l,adjoin2(a,b,acc))
in
fun filter [] = []
| filter(arg as [_]) = arg
| filter(x :: xs) = filter_sub(xs, [x])
end
val rev_remove_dups = filter
fun filter_length filter_fun filter_list =
let
fun loop([], n) = n
| loop(h :: t, n) = loop(t, if filter_fun h then n+1 else n)
in
loop(filter_list, 0)
end
fun hd ([]) = raise Hd
| hd (h::t) = h
fun tl ([]) = raise Tl
| tl (h::t) = t
fun difference (nil,x) = nil
| difference (hd::tl,x) =
if member (hd,x) then
difference (tl,x)
else
hd :: difference(tl,x)
fun sublist ([],l2) = true
| sublist (h::t,l2) =
member (h,l2) andalso sublist (t,l2)
fun iterate f [] = ()
| iterate f (h :: t) = (ignore(f h); iterate f t)
fun zip (L1,L2) =
let
fun loop ([], [], res) = rev res
| loop (h1 :: t1, h2 :: t2, res) = loop (t1, t2, (h1, h2) :: res)
| loop _ = raise Zip
in
loop (L1,L2,[])
end
fun unzip L =
let
fun loop ([], res1, res2) = (rev res1, rev res2)
| loop ((p,q)::xs, res1, res2) = loop(xs, p :: res1, q :: res2)
in
loop (L,[],[])
end
fun nth (0, x::_) = x
| nth (n, _::t) = nth(n-1,t)
| nth _ = raise Nth
fun nthtail (_,[]) = raise Nth
| nthtail (0,_::t) = t
| nthtail (n,_::t) = nthtail(n-1,t)
fun find (x,xs) =
let
fun count (n,z::zs) = if x=z then n else count(n+1,zs)
| count _ = raise Find
in
count (0,xs)
end
fun findp predicate list =
let
fun f [] = raise Find
| f (x::xs) =
if predicate x then x else f xs
in
f list
end
fun filterp P list =
let
fun filter (acc,[]) = rev acc
| filter (acc,x::xs) = if P x
then filter(x::acc,xs)
else filter(acc,xs)
in
filter ([],list)
end
fun filter_outp P list =
let
fun filter (acc,[]) = rev acc
| filter (acc,x::xs) = if P x
then filter(acc,xs)
else filter(x::acc,xs)
in
filter ([],list)
end
fun partition P list =
let
fun part (ys,ns,[]) = (rev ys, rev ns)
| part (ys,ns,x::xs) = if P x
then part(x::ys,ns,xs)
else part(ys,x::ns,xs)
in
part ([],[],list)
end
fun number_from (L, start : int, inc : int, num_fun) =
let
fun loop ([], result, next) = (rev result, next)
| loop (x :: xs, result, i) =
loop (xs, (x, num_fun i) :: result, i+inc)
in
loop (L, [], start)
end
fun number_from_by_one (l, i, f) = number_from (l, i, 1, f)
fun forall P =
let
fun test [] = true
| test (x::xs) = (P x) andalso test xs
in
test
end;
fun exists P =
let
fun test [] = false
| test (x::xs) = (P x) orelse test xs
in
test
end
fun assoc (key, list) =
let
fun ass [] = raise Assoc
| ass ((thiskey,value)::kvs) = if thiskey=key
then value
else ass kvs
in
ass list
end
fun assoc_returning_others(key,list) =
let
fun ass (others,[]) = raise Assoc
| ass (others,(this as (thiskey,value))::kvs) = if thiskey=key
then (rev_append(others, kvs),value)
else ass(this::others,kvs)
in
ass([],list)
end
fun reducel f =
let
fun red (acc, []) = acc
| red (acc, x::xs) = red (f(acc,x), xs)
in
red
end
fun reducer f (list,i) =
let
fun red ([], acc) = acc
| red (x::xs, acc) = red (xs, f(x,acc))
in
red (rev list,i)
end
fun findOption element_fn =
let
fun search [] = NONE
| search (x :: xs) = case element_fn x of
NONE => search xs
| x => x
in
search
end
fun merge (order_fn,args) =
let
fun do_merge (x,[]) = x
| do_merge ([],x) = x
| do_merge (arg as (h::t),arg' as (a::b)) =
if order_fn (h,a)
then h :: do_merge(t,arg')
else a :: do_merge(arg,b)
in
do_merge args
end
fun qsort order_fn [] = []
| qsort order_fn (arg as [x]) = arg
| qsort order_fn (arg as [a,b]) =
if order_fn (a,b)
then arg
else [b,a]
| qsort order_fn (a::(rest as [b,c])) =
merge(order_fn,([a],if order_fn(b,c) then rest else [c,b]))
| qsort order_fn [a,b,c,d] =
merge(order_fn,
(if order_fn(a,b)
then [a,b]
else [b,a],
if order_fn(c,d)
then [c,d]
else [d,c]))
| qsort order_fn yukky_list =
let
fun qs ([],nice_list) = nice_list
| qs (pivot::xs, sofar) =
let
fun part (left,right,[]) = qs(left, pivot::(qs (right, sofar)))
| part (left,right,y::ys) = if order_fn(y,pivot)
then part (y::left,right,ys)
else part (left,y::right,ys)
in
part([],[],xs)
end
in
qs (yukky_list,[])
end
local
fun split' (0,a,b) = (a,b)
| split' (n,a::b,c) = split' (n-1,b,a::c)
| split' (n,[],res) = ([],res)
fun split l =
split' (length (l,0) div 2,l,[])
in
fun msort order_fn l =
let
fun merge ([],l,acc) = rev_append (acc,l)
| merge (l,[],acc) = rev_append (acc,l)
| merge (l1 as (a::b),l2 as (c::d),acc) =
if order_fn (a,c) then
merge (b,l2,a::acc)
else
merge (l1,d,c::acc)
fun mergesort [] = []
| mergesort (arg as [x]) = arg
| mergesort (arg as [a,b]) =
if order_fn (a,b)
then arg
else [b,a]
| mergesort (arg as [a,b,c]) =
if order_fn (a,b)
then if order_fn (b,c) then arg
else
if order_fn (a,c) then [a,c,b]
else [c,a,b]
else
if order_fn (a,c) then [b,a,c]
else
if order_fn (b,c) then [b,c,a]
else [c,b,a]
| mergesort l =
let
val (l1,l2) = split (l)
in
merge (mergesort l1,
mergesort l2,
[])
end
in
mergesort l
end
end
fun to_string _ [] = "[]"
| to_string print_element list =
let
fun p (s,[]) = s
| p (s,[x]) = s ^ print_element x
| p (s,x::xs) = p(s ^ print_element x ^ ", " , xs)
in
"[" ^ p ("", list) ^ "]"
end
fun check_order order_fn =
let
fun check [] = true
| check [_] = true
| check (x :: (rest as (y :: z))) =
order_fn(x, y) andalso check rest
in
check
end
val length =
fn [] => 0
| (_ :: xs) => length(xs, 1)
end
;
