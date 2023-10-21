require "../utils/crash";
require "pretty";
functor PrettyFun (structure Crash : CRASH) : PRETTY =
struct
datatype T = Block of T list * int * int
| String of string
| Break of int
| Newline;
val margin = ref 78;
fun list_of(n, char) =
let
fun sub_fun(n, acc) =
if n <= 0 then acc
else sub_fun(n-1, char :: acc)
in
sub_fun(n, [])
end
fun spaces n = implode(list_of(n, #" "))
fun reduce f (result, indent, T) =
let
val prefix = spaces indent
val result = f (result, prefix)
val end_of_line = !margin
fun string(result, [], used, empty, prefix) = (result, empty, used)
| string(result, T :: TS, used, empty, prefix) =
let
val (result, empty, used) = case T of
String s =>
(f(result, s), false, used + size s)
| Break len =>
if len + used > end_of_line then
(f(result, "\n" ^ prefix), true, size prefix)
else
let
val space = spaces len
in
(f(result, space), empty, used + len)
end
| Newline =>
(f(result, "\n" ^ prefix), true, size prefix)
| Block(TS, indent, _) =>
let
val space = spaces indent
val prefix' = prefix ^ space
in
string(if empty then f(result, space) else result,
TS, used, empty, prefix')
end
in
string(result, TS, used, empty, prefix)
end
in
#1(string(result, [T], indent, true, prefix))
end
fun string_of_T T = reduce (op^) ("", 0, T)
fun print_T print_fn T = reduce (fn ((), s) => print_fn s) ((), 0, T)
val str = String
and brk = Break
and nl = Newline;
fun blk (indent,Ts) =
let
fun max (m,[]) = m
| max (m:int, i::is) = if i>m then max(i,is) else max(m,is)
fun length ([],k::ks) = max (k,ks)
| length (Block(_,_,len)::Ts,k::ks) = length(Ts,(k+len)::ks)
| length ((String s)::Ts,k::ks) = length(Ts,((size s)+k)::ks)
| length ((Break len)::Ts,k::ks) = length(Ts,(len+k)::ks)
| length (Newline::Ts,ks) = length(Ts, 0::ks)
| length _ = Crash.impossible "Pretty.blk";
in
Block(Ts,indent, length(Ts,[0]) )
end
fun lst(lp,sep,rp) ts =
let
fun list [] = []
| list [T] = [T]
| list (T::Ts) = T :: (sep @ (list Ts))
in
(str lp) :: ((list ts) @ [str rp])
end
end;
