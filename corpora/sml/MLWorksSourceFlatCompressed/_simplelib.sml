require "simplelib";
require "basis.__array";
functor SimpleLib() : SIMPLELIB =
struct
fun nil @ M = M
| (x :: L) @ M = x :: (L @ M)
fun map f =
let fun m nil = nil
| m (a::r) = f a :: m r
in m
end
exception Hd
exception Tl
fun hd (a::r) = a | hd nil = raise Hd
fun tl (a::r) = r | tl nil = raise Tl
fun fold f (b,[]) = b
| fold f (b,a::r) = let fun f2(e,[]) = f(e,b)
| f2(e,a::r) = f(e,f2(a,r))
in f2(a,r)
end
fun min(x:real,y:real) = if x<y then x else y
fun max(x:real,y:real) = if x<y then y else x
fun abs(x:real) = if x < 0.0 then ~x else x
exception MaxList
exception MinList
exception SumList
fun max_list [] = raise MaxList | max_list l = fold max (hd l,l)
fun min_list [] = raise MinList | min_list l = fold min (hd l,l)
fun sum_list [] = 0.0
| sum_list (x :: xs) = x + sum_list xs
fun for {from=start:int,step=delta:int, to=endd:int} body =
if delta>0 andalso endd>=start then
let fun f x = if x > endd then () else (ignore (body x); f(x+delta))
in f start
end
else if endd<=start then
let fun f x = if x < endd then () else (ignore (body x); f(x+delta))
in f start
end
else ()
fun from(n,m) = if n>m then [] else n::from(n+1,m)
fun flatten [] = []
| flatten (x::xs) = x @ flatten xs
fun pow(x,y) = if y = 0 then 1.0 else x*pow(x,y-1)
fun min(a:real,b) = if a<b then a else b
fun max(a:real,b) = if a>b then a else b
exception Overflow
type bounds2 = ((int * int) * (int * int))
type 'a array2 = {rows : int, columns : int, v : 'a Array.array} * bounds2
fun array2'(rows, columns, e) =
if rows<0 orelse columns<0 then raise Size
else {rows=rows,columns=columns,v=Array.array(rows*columns,e)}
fun sub2' ({rows,columns,v}, s :int, t:int) =
if s < 0 then raise Subscript
else if s>=rows then raise Subscript
else if t<0 then raise Subscript
else if t>=columns then raise Subscript
else Array.sub(v,s*columns+t)
fun update2' ({rows,columns,v}, s : int, t:int, e) : unit = Array.update(v,s*columns+t,e)
fun array2 (bounds as ((l1,u1),(l2,u2)),v) = (array2'(u1-l1+1,u2-l2+1,v), bounds)
fun sub2 ((A,((lb1,ub1), (lb2,ub2))),(k,l)) = sub2'(A, k-lb1, l-lb2)
fun update2 ((A,((lb1,_),(lb2,_))),(k,l), v) = update2'(A,k-lb1,l-lb2,v)
type 'a array1 = 'a Array.array * (int * int)
val array1 = fn ((l,u),v) => (Array.array(u-l+1,v),(l,u))
val sub1 = fn ((A,(l:int,u:int)),i:int) => Array.sub(A,i-l)
val update1 = fn((A,(l,_)),i,v) => Array.update(A,i-l,v)
fun bounds1(_,b) = b
val grid_max = 20
val iterations = 3
end
;
