require "sexpr";
functor Sexpr () : SEXPR =
struct
datatype 'a Sexpr = NIL | ATOM of 'a | CONS of 'a Sexpr * 'a Sexpr
fun list nil = NIL
| list (hd::tl) = CONS (hd,list tl)
exception Append
fun append (NIL,x) = x
| append (CONS (car,cdr), x) = CONS (car, append (cdr,x))
| append (ATOM _,_) = raise Append
fun printSexpr printer sexpr =
let
fun printexp NIL = "()"
| printexp (ATOM a) = printer a
| printexp (CONS (car,cdr)) = "(" ^ printexp car ^ printrest cdr
and printrest NIL = ")"
| printrest (ATOM a) = " . " ^ printer a ^ ")"
| printrest (CONS (car,cdr)) = " " ^ printexp car ^ printrest cdr
in
printexp sexpr
end
fun pprintSexpr printer sexpr =
let
fun spaces 0 = ""
| spaces n = " " ^ spaces (n-1)
val spaces40 = spaces 40
val indents =
MLWorks.Internal.Array.arrayoflist ["\n" ^ spaces40,
"\n>" ^ spaces40,
"\n>>" ^ spaces40,
"\n>>>" ^ spaces40,
"\n>>>>" ^ spaces40,
"\n>>>>>" ^ spaces40,
"\n>>>>>>" ^ spaces40,
"\n>>>>>>>" ^ spaces40]
exception Indent
fun indent s =
let
val a = s div 40
val b = s mod 40
in
if a > 7 then
raise Indent
else
substring (MLWorks.Internal.Array.sub(indents,a),0,a+b+1)
end
fun pprintexp (NIL,_) = "()"
| pprintexp (ATOM a,_) = printer a
| pprintexp (CONS (car,cdr),n) =
indent n ^ "(" ^ pprintexp (car,n+1) ^ pprintrest (cdr,n+1)
and pprintrest (NIL,_) = ")"
| pprintrest (ATOM a,_) = " . " ^ printer a ^ ")"
| pprintrest (CONS (car,cdr),n) =
" " ^ pprintexp (car,n) ^ pprintrest (cdr,n)
in
pprintexp (sexpr,0)
end
fun toList arg =
let
fun toList (CONS (xs, ys), zs, acc) = toList (xs, ys::zs, acc)
| toList (NIL, [], acc) =
foldl (op@) [] acc
| toList (NIL, x::xs, acc) = toList (x, xs, acc)
| toList (ATOM x, xs, acc) = toList (NIL, xs, x::acc)
in
toList (arg, [], [])
end
end
;
