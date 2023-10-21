Shell.Options.set(Shell.Options.Language.oldDefinition,true);
infix 1 seq
fun e1 seq e2 = e2;
fun check b = if b then "OK" else "WRONG";
fun check' f = (if f () then "OK" else "WRONG") handle _ => "EXN";
fun range (from, to) p =
let open Int
in
(from > to) orelse (p from) andalso (range (from+1, to) p)
end;
fun checkrange bounds = check o range bounds;
local
open Array
infix 9 sub
val array0 = fromList []
in
infix ==
fun a == b =
Vector.length a = Vector.length b
andalso
let
fun scan i =
if i = Vector.length a
then true
else
Vector.sub (a,i) = Vector.sub (b,i)
andalso scan (i+1)
in
scan 0
end
val a = fromList [0.1,1.1,2.1,3.1,4.1,5.1,6.1];
val b = fromList [44.1,55.1,66.1];
val c = fromList [0.1,1.1,2.1,3.1,4.1,5.1,6.1];
val test1 = check'(fn () => a<>c);
val test2 =
check'(fn () =>
array(0, 1.1) <> array0
andalso array(0,()) <> tabulate(0, fn _ => ())
andalso tabulate(0, fn _ => ()) <> fromList []
andalso fromList [] <> fromList []
andalso array(0, ()) <> array(0, ())
andalso tabulate(0, fn _ => ()) <> tabulate(0, fn _ => ()));
val d = tabulate(100, fn i => real (i mod 7) + 0.1);
val test3 =
check'(fn () => d sub 27 = 6.1);
val test4a = (tabulate(maxLen+1, fn i => i) seq "WRONG")
handle Size => "OK" | _ => "WRONG";
val test4b = (tabulate(~1, fn i => i) seq "WRONG")
handle Size => "OK" | _ => "WRONG";
val test4c =
check'(fn () => length (tabulate(0, fn i => i div 0)) = 0);
val test5a =
check'(fn () => length (fromList []) = 0 andalso length a = 7);
val test5b =
check'(fn () => length array0 = 0);
val test6a = (c sub ~1 seq "WRONG") handle Subscript => "OK" | _ => "WRONG";
val test6b = (c sub 7 seq "WRONG") handle Subscript => "OK" | _ => "WRONG";
val test6c = check'(fn () => c sub 0 = 0.1);
val e = array(203, 0.0);
val _ = (copy{src=d, si=0, dst=e, di=0, len=NONE};
copy{src=b, si=0, dst=e, di=length d, len=NONE};
copy{src=d, si=0, dst=e, di=length d + length b, len=NONE});
fun a2v a = extract(a, 0, NONE);
val ev = Vector.concat [a2v d, a2v b, a2v d];
val test7 = check'(fn () => length e = 203);
val test8a = (update(e, ~1, 9.9) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test8b = (update(e, length e, 9.9) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val f = extract (e, 100, SOME 3);
val test9 = check'(fn () => f = a2v b);
val test9a =
check'(fn () => ev = extract(e, 0, SOME (length e))
andalso ev = extract(e, 0, NONE));
val test9b =
check'(fn () => Vector.fromList [] == extract(e, 100, SOME 0));
val test9c = (extract(e, ~1, SOME (length e)) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9d = (extract(e, length e+1, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9e = (extract(e, 0, SOME (length e+1)) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9f = (extract(e, 20, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9g = (extract(e, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9h = (extract(e, length e+1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test9i =
check'(fn () => a2v (fromList []) == extract(e, length e, SOME 0)
andalso a2v (fromList []) == extract(e, length e, NONE));
val test9j =
check'(fn () => extract(e, 3, SOME(length e - 3)) = extract(e, 3, NONE));
val _ = copy{src=e, si=0, dst=e, di=0, len=NONE};
val g = array(203, 999999.9);
val _ = copy{src=e, si=0, dst=g, di=0, len=NONE};
val test10a = check'(fn () => ev = extract(e, 0, SOME (length e))
andalso ev = extract(e, 0, NONE));
val test10b = check'(fn () => ev = extract(g, 0, SOME (length g))
andalso ev = extract(g, 0, NONE));
val _ = copy{src=g, si=203, dst=g, di=0, len=SOME 0};
val test10c = check'(fn () => ev = extract(g, 0, SOME (length g)));
val _ = copy{src=g, si=0, dst=g, di=203, len=SOME 0};
val test10d = check'(fn () => ev = extract(g, 0, SOME (length g)));
val _ = copy{src=g, si=0, dst=g, di=1, len=SOME (length g-1)};
val test10e = check'(fn () => a2v b = extract(g, 101, SOME 3));
val _ = copy{src=g, si=1, dst=g, di=0, len=SOME (length g-1)};
val test10f = check'(fn () => a2v b = extract(g, 100, SOME 3));
val test10g =
check'(fn () => g sub 202 = real ((202-1-103) mod 7) + 0.1);
val test10h =
check'(fn () => (copy{src=array0, si=0, dst=array0, di=0, len=SOME 0};
array0 <> array(0, 99999.9)));
val test10i =
check'(fn () => (copy{src=array0, si=0, dst=array0, di=0, len=NONE};
array0 <> array(0, 99999.9)));
val test11a = (copy{src=g, si= ~1, dst=g, di=0, len=NONE}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11b = (copy{src=g, si=0, dst=g, di= ~1, len=NONE}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11c = (copy{src=g, si=1, dst=g, di=0, len=NONE}; "OK")
handle _ => "WRONG"
val test11d = (copy{src=g, si=0, dst=g, di=1, len=NONE}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11e = (copy{src=g, si=203, dst=g, di=0, len=NONE}; "OK")
handle _ => "WRONG"
val test11f = (copy{src=g, si= ~1, dst=g, di=0, len=SOME (length g)}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11g = (copy{src=g, si=0, dst=g, di= ~1, len=SOME (length g)}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11h = (copy{src=g, si=1, dst=g, di=0, len=SOME (length g)}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11i = (copy{src=g, si=0, dst=g, di=1, len=SOME (length g)}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11j = (copy{src=g, si=0, dst=g, di=0, len=SOME (length g+1)}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
val test11k = (copy{src=g, si=203, dst=g, di=0, len=SOME 1}; "WRONG")
handle Subscript => "OK" | _ => "WRONG"
local
val v = ref 0
fun setv c = v := c;
fun addv c = v := c + !v;
fun setvi (i, c) = v := c + i;
fun addvi (i, c) = v := c + i + !v;
fun cons (x,r) = x :: r
fun consi (i,x,r) = (i,x) :: r
val inplist = [7,9,13];
in
val inp = fromList inplist
val pni = fromList (rev inplist)
fun copyinp a =
copy{src=inp, si=0, dst=a, di=0, len=NONE}
val array0 = fromList [] : int array;
val test12a =
check'(fn _ =>
foldl cons [1,2] array0 = [1,2]
andalso foldl cons [1,2] inp = [13,9,7,1,2]
andalso (foldl (fn (x, _) => setv x) () inp; !v = 13));
val test12b =
check'(fn _ =>
foldr cons [1,2] array0 = [1,2]
andalso foldr cons [1,2] inp = [7,9,13,1,2]
andalso (foldr (fn (x, _) => setv x) () inp; !v = 7));
val test12d =
check'(fn _ =>
(setv 117; app setv array0; !v = 117)
andalso (setv 0; app addv inp; !v = 7+9+13)
andalso (app setv inp; !v = 13));
val test12e =
let val a = array(length inp, inp sub 0)
in
check'(fn _ =>
(modify (~ : int -> int) array0; true)
andalso (copyinp a; modify ~ a; foldr (op::) [] a = map ~ inplist)
andalso (setv 117; modify (fn x => (setv x; 37)) a; !v = ~13))
end
val test13a =
check'(fn _ =>
foldli consi [] (array0, 0, NONE) = []
andalso foldri consi [] (array0, 0, NONE) = []
andalso foldli consi [] (inp, 0, NONE) = [(2,13),(1,9),(0,7)]
andalso foldri consi [] (inp, 0, NONE) = [(0,7),(1,9),(2,13)])
val test13b =
check'(fn _ => foldli consi [] (array0, 0, SOME 0) = []
andalso foldri consi [] (array0, 0, SOME 0) = []
andalso foldli consi [] (inp, 0, SOME 0) = []
andalso foldri consi [] (inp, 0, SOME 0) = []
andalso foldli consi [] (inp, 3, SOME 0) = []
andalso foldri consi [] (inp, 3, SOME 0) = []
andalso foldli consi [] (inp, 0, SOME 3) = [(2,13),(1,9),(0,7)]
andalso foldri consi [] (inp, 0, SOME 3) = [(0,7),(1,9),(2,13)]
andalso foldli consi [] (inp, 0, SOME 2) = [(1,9),(0,7)]
andalso foldri consi [] (inp, 0, SOME 2) = [(0,7),(1,9)]
andalso foldli consi [] (inp, 1, SOME 2) = [(2,13),(1,9)]
andalso foldri consi [] (inp, 1, SOME 2) = [(1,9),(2,13)]
andalso foldli consi [] (inp, 2, SOME 1) = [(2,13)]
andalso foldri consi [] (inp, 2, SOME 1) = [(2,13)]);
val test13c = (foldli consi [] (inp, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13d = (foldli consi [] (inp, 4, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13e = (foldli consi [] (inp, ~1, SOME 2) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13f = (foldli consi [] (inp, 4, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13g = (foldli consi [] (inp, 0, SOME 4) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13h = (foldli consi [] (inp, 2, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13i = (foldri consi [] (inp, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13j = (foldri consi [] (inp, 4, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13k = (foldri consi [] (inp, ~1, SOME 2) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13l = (foldri consi [] (inp, 4, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13m = (foldri consi [] (inp, 0, SOME 4) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test13n = (foldri consi [] (inp, 2, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15a =
check'(fn _ =>
(setvi (0,117); appi setvi (array0, 0, NONE); !v = 117)
andalso (setvi (0,0); appi addvi (inp, 0, NONE); !v = 0+7+1+9+2+13)
andalso (appi setvi (inp, 0, NONE); !v = 2+13));
val test15b =
check'(fn _ =>
(setvi (0,117); appi setvi (array0, 0, SOME 0); !v = 117)
andalso (setvi (0,0); appi addvi (inp, 3, SOME 0); !v = 0)
andalso (setvi (0,0); appi addvi (inp, 0, SOME 2); !v = 0+7+1+9)
andalso (setvi (0,0); appi addvi (inp, 0, SOME 0); !v = 0)
andalso (setvi (0,0); appi addvi (inp, 1, SOME 2); !v = 1+9+2+13)
andalso (setvi (0,0); appi addvi (inp, 0, SOME 0); !v = 0)
andalso (setvi (0,0); appi addvi (inp, 0, SOME 3); !v = 0+7+1+9+2+13)
andalso (appi setvi (inp, 1, SOME 2); !v = 2+13)
andalso (appi setvi (inp, 0, SOME 2); !v = 1+9)
andalso (appi setvi (inp, 0, SOME 1); !v = 0+7)
andalso (appi setvi (inp, 0, SOME 3); !v = 2+13));
val test15c = (appi setvi (inp, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15d = (appi setvi (inp, 4, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15e = (appi setvi (inp, ~1, SOME 2) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15f = (appi setvi (inp, 4, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15g = (appi setvi (inp, 0, SOME 4) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test15h = (appi setvi (inp, 2, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16a =
let val a = array(length inp, inp sub 0)
in
check'(fn _ =>
(modifyi (op +) (array0, 0, NONE); true)
andalso (modifyi (op +) (array0, 0, SOME 0); true)
andalso (copyinp a; modifyi (op -) (a, 0, SOME 0);
foldr (op::) [] a = [7,9,13])
andalso (copyinp a; modifyi (op -) (a, 3, SOME 0);
foldr (op::) [] a = [7,9,13])
andalso (copyinp a; modifyi (op -) (a, 0, NONE);
foldr (op::) [] a = [~7,~8,~11])
andalso (copyinp a; modifyi (op -) (a, 0, SOME 3);
foldr (op::) [] a = [~7,~8,~11])
andalso (copyinp a; modifyi (op -) (a, 0, SOME 2);
foldr (op::) [] a = [~7,~8,13])
andalso (copyinp a; modifyi (op -) (a, 1, SOME 2);
foldr (op::) [] a = [7,~8,~11])
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 0, NONE); !v = 2+13)
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 0, SOME 3); !v = 2+13)
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 1, SOME 2); !v = 2+13)
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 0, SOME 2); !v = 1+9)
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 0, SOME 0); !v = 117)
andalso (copyinp a; setv 117;
modifyi (fn x => (setvi x; 37)) (a, 3, SOME 0); !v = 117))
end
val test16b = (modifyi (op+) (inp, ~1, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16c = (modifyi (op+) (inp, 4, NONE) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16d = (modifyi (op+) (inp, ~1, SOME 2) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16e = (modifyi (op+) (inp, 4, SOME 0) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16f = (modifyi (op+) (inp, 0, SOME 4) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
val test16g = (modifyi (op+) (inp, 2, SOME ~1) seq "WRONG")
handle Subscript => "OK" | _ => "WRONG";
end
end
;
