signature PRETTY =
sig
type T
val blo : int * T list -> T
val str : string -> T
val brk : int -> T
val is_single : T -> bool
val pr : T * int -> unit
end;
functor PrettyFUN () : PRETTY =
struct
datatype T =
Block of T list * int * int
| String of string
| Break of int
fun is_single (Block(l,_,_)) = is_singleton l orelse null l
| is_single (String _ ) = true
| is_single _ = false
fun breakdist (Block(_,_,len)::sexps, after) = len + breakdist(sexps, after)
| breakdist (String s :: sexps, after) = size s + breakdist (sexps, after)
| breakdist (Break _ :: sexps, after) = 0
| breakdist ([], after) = after
fun pr (sexp, margin) =
let val space = ref margin
fun blanks n = (space := !space - n ; spaces n)
fun newline () = (space := margin ; "\n" )
fun printing ([], _, _) = ""
| printing (sexp::sexps, blockspace, after) =
(case sexp of
Block(bsexps,indent,len) =>
printing(bsexps, !space-indent, breakdist(sexps,after))
| String s => (space := !space - size s; s)
| Break len =>
if len + breakdist(sexps,after) <= !space
then blanks len
else (newline() ^ blanks(margin-blockspace))
) ^ printing (sexps, blockspace, after)
in write_terminal (printing([sexp], margin, 0))
end
fun length (Block(_,_,len)) = len
| length (String s) = size s
| length (Break len) = len
val str = String and brk = Break
fun blo (indent,sexps) = Block(sexps,indent, foldl (C(add o length)) 0 sexps)
end;
