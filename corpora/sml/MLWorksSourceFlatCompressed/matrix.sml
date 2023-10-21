infixr 6 revConc;
signature MATRIX =
sig
type Row
type Matrix
exception Dimension
val zeroRow : int -> int list -> int list
val allZero : int list -> bool
val revConc : 'a list * 'a list -> 'a list
val crossPair : 'a list -> 'b list -> ('a * 'b) list
val distrPair : 'a -> 'b list -> ('a * 'b) list
val dotProd : int list -> int list -> int
exception Width
val width : 'a list list -> int
val add0Col : int list list -> int list list
val transpose : int list list -> int list list
val rowProd : int list -> int list list -> int list
val matProdT : int list list -> int list list -> int list list
val matProd : int list list -> int list list -> int list list
end
;
functor MatrixFUN () : MATRIX =
struct
type Row = int list;
type Matrix = Row list;
exception Dimension;
fun zeroRow z xs = copy z 0 @ xs
val allZero = forall (eq 0)
val op revConc = fn x => uncurry (append o rev) x
fun crossPair [] _ = []
| crossPair (x::xs) ys = distrPair x ys @ crossPair xs ys
and distrPair x [] = []
| distrPair x (y::ys) = (x, y) :: distrPair x ys;
local
fun dotProd' s [] [] = s : int
| dotProd' s (x::xs) (y::ys) = dotProd' (s + x*y) xs ys
| dotProd' s _ _ = raise Dimension
in
fun dotProd r1 r2 = dotProd' 0 r1 r2
end;
exception Width;
fun width [] = raise Width
| width (r::rs) = length r;
val add0Col = map (cons 0) ;
fun transpose ([]::_) = []
| transpose m = map hd m :: transpose (map tl m);
fun rowProd r [] = []
| rowProd r (c::cs) = dotProd r c :: rowProd r cs;
fun matProdT [] m2 = []
| matProdT (r::rs) m2 = rowProd r m2 :: matProdT rs m2;
fun matProd m1 m2 = matProdT m1 (transpose m2);
end
;
