fun f x = x > ~32768
fun f' x = ~32768 < x
fun g x = x > 2
fun g' x = 2 < x
fun h x = (x <= 32768)
fun h' x = (32768 >= x)
fun i x = (x - 2) < (2 - x)
fun i' x = (2 - x) > (x - 2);
val a = (f 2) andalso (f' 2)
andalso not (g 2) andalso not (g' 2)
andalso (h 2) andalso (h' 2)
andalso (i ~32768) andalso (i' ~32768)
fun f x = x > 0;
not (f ~1) andalso not (f ~0) andalso not (f 0) andalso f 1 andalso f 2
fun f x = 0 < x;
not (f ~1) andalso not (f ~0) andalso not (f 0) andalso f 1 andalso f 2
fun f x = x >= 0;
not (f ~1) andalso (f ~0) andalso (f 0) andalso f 1 andalso f 2
fun f x = 0 <= x;
not (f ~1) andalso (f ~0) andalso (f 0) andalso f 1 andalso f 2
fun f x = x < 0;
(f ~1) andalso not (f ~0) andalso not (f 0) andalso not(f 1) andalso not (f 2)
fun f x = 0 > x;
(f ~1) andalso not (f ~0) andalso not (f 0) andalso not(f 1) andalso not (f 2)
fun f x = x <= 0;
(f ~1) andalso (f ~0) andalso (f 0) andalso not(f 1) andalso not (f 2)
fun f x = 0 >= x;
(f ~1) andalso (f ~0) andalso (f 0) andalso not(f 1) andalso not (f 2)
fun f x = x = 0;
not (f ~1) andalso (f ~0) andalso (f 0) andalso not(f 1) andalso not (f 2)
fun f x = 0 = x;
not (f ~1) andalso (f ~0) andalso (f 0) andalso not(f 1) andalso not (f 2)
fun f x = x <> 0;
(f ~1) andalso not (f ~0) andalso not (f 0) andalso (f 1) andalso (f 2)
fun f x = 0 <> x;
(f ~1) andalso not (f ~0) andalso not (f 0) andalso (f 1) andalso (f 2)
fun f x = x > 0w0;
(f 0w1) andalso not (f 0w0) andalso (f 0w2) andalso (f 0w32769)
fun f x = 0w0 < x;
(f 0w1) andalso not (f 0w0) andalso (f 0w2) andalso (f 0w32769)
fun f x = x >= 0w0;
(f 0w1) andalso (f 0w0) andalso (f 0w2) andalso (f 0w32769)
fun f x = 0w0 <= x;
(f 0w1) andalso (f 0w0) andalso (f 0w2) andalso (f 0w32769)
fun f x = x < 0w0;
not (f 0w1) andalso not (f 0w0) andalso not (f 0w2) andalso not (f 0w32769)
fun f x = 0w0 > x;
not (f 0w1) andalso not (f 0w0) andalso not (f 0w2) andalso not (f 0w32769)
fun f x = x <= 0w0;
not (f 0w1) andalso (f 0w0) andalso not (f 0w2) andalso not (f 0w32769)
fun f x = 0w0 >= x;
not (f 0w1) andalso (f 0w0) andalso not (f 0w2) andalso not (f 0w32769)
fun f x = x = 2;
not (f 1) andalso f 2 andalso not (f 3)
fun f x = 2 = x;
not (f 1) andalso f 2 andalso not (f 3)
fun f x = x <> 2;
f ~1 andalso not (f 2) andalso f 3
fun f x = 2 <> x;
f ~1 andalso not (f 2) andalso f 3
fun f x = x = 0w2;
not (f 0w0) andalso f 0w2
fun f x = 0w2 = x;
not (f 0w0) andalso f 0w2
fun f x = x <> 0w2;
f 0w0 andalso not (f 0w2)
fun f x = 0w2 <> x;
f 0w0 andalso not (f 0w2)
fun f x = x > 2;
not (f ~1) andalso not (f ~0) andalso not (f 0) andalso not (f 1) andalso not (f 2) andalso (f 3)
fun f x = 2 < x;
not (f ~1) andalso not (f ~0) andalso not (f 0) andalso not (f 1) andalso not (f 2) andalso (f 3)
fun f x = x < 2;
(f 1) andalso not (f 2) andalso not (f 3)
fun f x = 2 > x;
(f 1) andalso not (f 2) andalso not (f 3)
fun f x = x >= 2;
not (f ~1) andalso not (f 0) andalso (f 2) andalso (f 3)
fun f x = 2 <= x;
not (f ~1) andalso not (f 0) andalso (f 2) andalso (f 3)
fun f x = x <= 2;
f ~1 andalso f 0 andalso f 2 andalso not (f 3)
fun f x = 2 >= x;
f ~1 andalso f 0 andalso f 2 andalso not (f 3)
fun f x = x > 0w2;
not (f 0w0) andalso not (f 0w1) andalso not (f 0w2) andalso (f 0w3)
fun f x = 0w2 < x;
not (f 0w0) andalso not (f 0w1) andalso not (f 0w2) andalso (f 0w3)
fun f x = x < 0w2;
f 0w0 andalso f 0w1 andalso not (f 0w2) andalso not (f 0w3)
fun f x = 0w2 > x;
f 0w0 andalso f 0w1 andalso not (f 0w2) andalso not (f 0w3)
fun f x = x >= 0w2;
not (f 0w0) andalso not (f 0w1) andalso (f 0w2) andalso (f 0w3)
fun f x = 0w2 <= x;
not (f 0w0) andalso not (f 0w1) andalso (f 0w2) andalso (f 0w3)
fun f x = x <= 0w2;
(f 0w0) andalso (f 0w1) andalso (f 0w2) andalso not (f 0w3)
fun f x = 0w2 >= x;
(f 0w0) andalso (f 0w1) andalso (f 0w2) andalso not (f 0w3)
fun f x y = x > (y:int);
not (f 2 3) andalso not (f 2 2) andalso (f 2 1)
fun f x y = y < (x:int);
not (f 2 3) andalso not (f 2 2) andalso (f 2 1)
fun f x y = x < (y:int);
(f 2 3) andalso not (f 2 2) andalso not (f 2 1)
fun f x y = y > (x:int);
(f 2 3) andalso not (f 2 2) andalso not (f 2 1)
fun f x y = x >= (y:int);
not (f 2 3) andalso (f 2 2) andalso (f 2 1) andalso (f 2 0)
fun f x y = y <= (x:int);
not (f 2 3) andalso (f 2 2) andalso (f 2 1) andalso (f 2 0)
fun f x y = x > (y:word);
not (f 0w2 0w3) andalso not (f 0w2 0w2) andalso (f 0w2 0w1) andalso (f 0w2 0w0)
fun f x y = y < (x:word);
not (f 0w2 0w3) andalso not (f 0w2 0w2) andalso (f 0w2 0w1) andalso (f 0w2 0w0)
fun f x y = x < (y:word);
(f 0w2 0w3) andalso not (f 0w2 0w2) andalso not (f 0w2 0w1) andalso not (f 0w2 0w0)
fun f x y = y > (x:word);
(f 0w2 0w3) andalso not (f 0w2 0w2) andalso not (f 0w2 0w1) andalso not (f 0w2 0w0)
fun f x y = x >= (y:word);
not (f 0w2 0w3) andalso (f 0w2 0w2) andalso (f 0w2 0w1) andalso (f 0w2 0w0)
fun f x y = y <= (x:word);
not (f 0w2 0w3) andalso (f 0w2 0w2) andalso (f 0w2 0w1) andalso (f 0w2 0w0)
fun f x = x > ~32768;
not (f ~32769) andalso not (f ~32768) andalso (f 0) andalso (f 32768)
fun f x = ~32768 < x;
not (f ~32769) andalso not (f ~32768) andalso (f 0) andalso (f 32768)
fun f x = x < ~32768;
(f ~32769) andalso not (f ~32768) andalso not (f 0) andalso not (f 32768)
fun f x = ~32768 > x;
(f ~32769) andalso not (f ~32768) andalso not (f 0) andalso not (f 32768)
fun f x = x >= ~32768;
not (f ~32769) andalso (f ~32768) andalso (f 0) andalso (f 32768)
fun f x = ~32768 <= x;
not (f ~32769) andalso (f ~32768) andalso (f 0) andalso (f 32768)
fun f x = x <= ~32768;
(f ~32769) andalso (f ~32768) andalso not (f 0) andalso not (f 32768)
fun f x = ~32768 >= x;
(f ~32769) andalso (f ~32768) andalso not (f 0) andalso not (f 32768)
fun f x = x > 536870911;
not (f (~536870911)) andalso not (f 0)
;
