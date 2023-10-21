signature DIO =
sig
val solve_dio_equation : int list -> int list -> int -> (int list * int list) list
val show_dio_solutions : unit -> unit
end ;
structure Dio : DIO =
struct
open Array
infix 9 sub
datatype Dio_Equation = Dio of (int *
int *
int list *
int list *
int
)
fun homogenous (Dio(_,_,_,_,c)) = c = 0
fun read_n_times 0 s = []
| read_n_times n s =
(write_terminal s ;
case (Strings.stringtoint o
Strings.drop_last o
read_line_terminal) () of
OK m => m :: read_n_times (n - 1) s
| Error _ => (write_terminal "Only enter integers\n" ;
read_n_times n s)
)
fun read_dio_equation () =
let val m = hd (read_n_times 1 "number of lhs coefficients:  ")
val n = hd (read_n_times 1 "number of rhs coefficients:  ")
val a = (write_terminal("lhs coefficients:\n");
read_n_times m "a = ")
val b = (write_terminal("rhs coefficients:\n");
read_n_times n "b = ")
val c = hd (read_n_times 1 "inhomogenous part\n    c = ")
in Dio(m,n,a,b,c)
end
local
fun mk_coeff_strs s (n:int,l:int list) =
let fun coeffs m n l = cons
(makestring(hd l) ^ "*" ^ s ^ makestring n)
(if m = n
then []
else coeffs m (n+1) (tl l)
)
in coeffs n 1 l
end
in
fun write_dio_equation (Dio(m,n,a,b,c)) =
let val As = mk_coeff_strs "x" (m,a)
val bs = mk_coeff_strs "y" (n,b)
in stringwith ("", " + ", "") As
^
stringwith (" - "," - "," = "^makestring c) bs
end
end
abstype Dio_Graph = Graph of (int list) array * bool list *
(int list * bool) *
(int list) array * bool list
with
fun dio_graph_edges (Graph(negd,negb,zerod,posd,posb)) d =
(case intorder d 0 of
GT => posd sub (d-1)
| LT => negd sub (abs d -1)
| EQ => fst zerod
handle Nth => failwith "Integer Out of Range of Graph\n"
)
fun marked_node (Graph(negd,negb,zerod,posd,posb)) d =
(case intorder d 0 of
GT => nth (posb , (d-1))
| LT => nth (negb , (abs d -1))
| EQ => snd zerod
handle Nth => failwith "Integer Out of Range of Graph\n"
)
fun mark_node (Graph(negd,negb,zerod,posd,posb)) d =
(case intorder d 0 of
GT => Graph(negd,negb,zerod,posd,exchange posb (d-1) true)
| LT => Graph(negd,exchange negb (abs d -1) true,zerod,posd,posb)
| EQ => Graph(negd,negb,(fst zerod,true),posd,posb)
handle Nth => failwith "Integer Out of Range of Graph\n"
)
fun make_graph (Dio(m,n,a,b,c)) =
let fun mk_pos_graph al d = (map (add d) al)
fun mk_neg_graph bl d = (map (minus d) bl)
val lb = ~ (max (maximum b,c))
val ub = maximum a
val negds = ints ~1 lb
val posds = ints 1 ub
in Graph(arrayoflist (map (mk_pos_graph a) negds),
copy (abs lb) false,
(mk_pos_graph a 0,false),
arrayoflist (map (mk_neg_graph b) posds),
copy ub false)
end
end
datatype Side = Left | Right
fun null_solution m n = (copy m 0,copy n 0)
local
fun inc_list (a::l) 1 = a+1::l
| inc_list (a::l) n = a:: inc_list l (n-1)
| inc_list [] n = []
in
fun inc_right (l1,l2) k = (l1,inc_list l2 k)
fun inc_left (l1,l2) k = (inc_list l1 k,l2)
end
fun show_solution (l1:int list,l2:int list) = stringlist makestring ("<",", ",">") l1
^ "   " ^ stringlist makestring ("<",", ",">") l2 ;
fun verify_solution G Sol apos bpos node =
let val edges = dio_graph_edges G node
val (side,mono_edges) = if node <= 0
then (Left , drop (apos-1) edges)
else (Right , drop (bpos-1) edges)
in verify_path G Sol side apos bpos mono_edges
end
and verify_path G (a::ais,bl) Left apos bpos (e :: re) =
if a = 0
then verify_path G (ais,bl) Left (apos + 1) bpos re
else if e = 0
then false
else (verify_solution G ((a-1)::ais,bl) apos bpos e)
andalso
(verify_path G (ais,bl) Left (apos + 1) bpos re)
| verify_path G (al,b::bis) Right apos bpos (e :: re) =
if b = 0
then verify_path G (al,bis) Right apos (bpos + 1) re
else if e = 0
then (
false )
else (verify_solution G (al,(b-1)::bis) apos bpos e)
andalso
(verify_path G (al,bis) Right apos (bpos + 1) re)
| verify_path G Sol side apos bpos [] = true
| verify_path G ([],[]) side apos bpos es = true
| verify_path G ([],bis) Left apos bpos es = true
| verify_path G (ais,[]) Right apos bpos es = true
fun find_solutions G PrevSols CurrSol apos bpos node =
let val edges = dio_graph_edges G node
val (side,mono_edges) = if node <= 0
then (Left , drop (apos-1) edges)
else (Right , drop (bpos-1) edges)
in extend_path G PrevSols CurrSol side apos bpos mono_edges
end
and extend_path G PrevSols CurrSol Left apos bpos (e :: re) =
if e = 0
then extend_path G (if verify_solution G CurrSol 1 1 0
then (inc_left CurrSol apos :: PrevSols)
else PrevSols)
CurrSol Left (apos + 1) bpos re
else if marked_node G e
then extend_path G PrevSols CurrSol Left (apos + 1) bpos re
else extend_path G (find_solutions (mark_node G e) PrevSols
(inc_left CurrSol apos) apos bpos e)
CurrSol Left (apos + 1) bpos re
| extend_path G PrevSols CurrSol Right apos bpos (e :: re) =
if e = 0
then extend_path G (if verify_solution G CurrSol 1 1 0
then (inc_right CurrSol bpos :: PrevSols)
else PrevSols)
CurrSol Right apos (bpos + 1) re
else if marked_node G e
then extend_path G PrevSols CurrSol Right apos (bpos + 1) re
else extend_path G (find_solutions (mark_node G e) PrevSols
(inc_right CurrSol bpos) apos bpos e)
CurrSol Right apos (bpos + 1) re
| extend_path G PrevSols CurrSol side apos bpos [] = PrevSols
fun solve_dio_graph (dio as Dio(m,n,a,b,c)) =
find_solutions (make_graph dio) [] (null_solution m n) 1 1 (~c)
fun solve_dio_equation ais bis c =
solve_dio_graph (Dio(List.length ais,List.length bis,ais,bis, c))
fun show_dio_solutions () =
let val de = read_dio_equation ()
val d = write_terminal (write_dio_equation de ^ "\n")
val sols = Timer.timer (fn () => solve_dio_graph de)
in write_terminal "Press return for Solutions" ;
ignore(input(std_in,1)) ;
write_terminal ("Number of Solutions = "^ (makestring (List.length (map ((outputc std_out) o (noc "\n") o show_solution)
sols)))^"\n")
end
end ;
