infix 1 seq
fun e1 seq e2 = e2;
fun check b = if b then "OK" else "WRONG";
fun check' f = (if f () then "OK" else "WRONG") handle _ => "EXN";
fun checkexn' e f = (ignore(f ()); "WRONG")
handle e' => if (General.exnName e= General.exnName e')
then "OKEXN" else "WRONG EXN"
fun range (from, to) p =
let open Int
in
(from > to) orelse (p from) andalso (range (from+1, to) p)
end;
fun checkrange bounds = check o range bounds;
local
open Word8Vector;
val i2w = Word8.fromInt;
val w2i = Word8.toInt;
infix 9 sub;
infix ==;
fun a == b =
let
val len = Word8Vector.length a
fun scan i = if i=len then true
else (Word8Vector.sub(a,i) = Word8Vector.sub(b,i)
andalso scan (i+1))
in
len = Word8Vector.length b
andalso scan 0
end
in
val a = fromList (List.map i2w [0,1,2,3,4,5,6]);
val b = fromList (List.map i2w [44,55,66]);
val c = fromList (List.map i2w [0,1,2,3,4,5,6]);
val fromList' = fn l => fromList (List.map i2w l)
val test1 = check'(fn _ => not(a==b));
val test2 = check'(fn _ => a==c);
val d = tabulate(100, fn i => i2w (i mod 7));
val test3 = check'(fn _ => d sub 27 = i2w 6);
val test4a = (tabulate(maxLen+1, i2w) seq "WRONG")
handle Size => "OK" | _ => "WRONG";
val test4b = (tabulate(~1, i2w) seq "WRONG")
handle Size => "OK" | _ => "WRONG";
val test4c = check'(fn _ => length (tabulate(0, fn i => i2w (i div 0))) = 0);
val test5 = check'(fn _ => length (fromList []) = 0 andalso length a = 7);
val test6a = (c sub ~1 seq "WRONG") handle Subscript => "OK" | _ => "WRONG";
val test6b = (c sub 7 seq "WRONG") handle Subscript => "OK" | _ => "WRONG";
val test6c = check'(fn _ => c sub 0 = i2w 0);
val e = concat [d, b, d];
val test7 = check'(fn _ => length e = 203);
val test8 = check'(fn _ => length (concat []) = 0);
val f = Word8Vector.extract(e, 100, SOME 3)
val test9 = check'(fn _ => f == b);
val test9a = check'(fn _ => e == extract(e, 0, SOME (length e))
andalso e == extract(e, 0, NONE));
val test9b = check'(fn _ => fromList [] == extract(e, 100, SOME 0));
val test9c = (extract(e, ~1, SOME (length e)) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9d = (extract(e, length e + 1, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9e = (extract(e, 0, SOME (length e+1)) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9f = (extract(e, 20, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9g = (extract(e, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9h = (extract(e, length e + 1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9i = check'(fn _ => fromList [] == extract(e, length e, SOME 0)
andalso fromList [] == extract(e, length e, NONE));
val v = (fromList' [0, 1, 2, 3, 4, 5, 6, 7]) ;
val add1 = (fn i => i2w (w2i i+1)) ;
val addi = (fn (i, j) => i2w (i+ w2i j)) ;
val testAa = check'
(fn _ => (map add1 v) == fromList' [1, 2, 3, 4, 5, 6, 7, 8]) ;
val testAb = check'
(fn _ => (map add1 (fromList' [])) == (fromList' [])) ;
val testAc = check'
(fn _ => (mapi addi (v, 0, NONE)) == fromList' [0,2,4,6,8,10,12,14]);
val testAd = check'
(fn _ => (mapi addi (v, 1, NONE)) == fromList' [2,4,6,8,10,12,14]);
val testAe = check'
(fn _ => (mapi addi (v, 7, NONE)) == fromList' [14]);
val testAf =
(ignore(mapi addi (v, ~1, NONE)); "WRONG") handle Subscript => "OKEXN"
| _ => "WRONG EXN" ;
val testAga = check'
(fn _ => (mapi addi (v, 8, NONE)) == fromList' [])
val testAgb =
(ignore(mapi addi (v, 9, NONE)); "WRONG") handle Subscript => "OKEXN"
| _ => "WRONG EXN" ;
val testAh =
check' (fn _ => mapi addi (v, 0, SOME 2) == fromList' [0, 2]) ;
val testAi =
check' (fn _ => mapi addi (v, 6, SOME 2) == fromList' [12,14]) ;
val testAj =
(ignore(mapi addi (v,7,SOME 2)); "WRONG") handle Subscript => "OKEXN"
| _ => "WRONG EXN" ;
val testAk = check'
(fn _ => mapi addi (v, 2, SOME 0) == fromList' [])
val testAl =
(ignore(mapi addi (v, 2, SOME ~1)); "WRONG") handle Subscript => "OKEXN"
| _ => "WRONG EXN" ;
end;
