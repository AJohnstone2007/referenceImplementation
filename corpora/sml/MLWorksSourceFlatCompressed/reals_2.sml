fun opt (SOME x) = x | opt NONE = raise Div
val from = opt o Real.fromString
val exact = Real.fmt (StringCvt.EXACT);
val rep = MLWorks.Internal.Value.real_to_string;
fun equal (x,y) = (Real.isNan x andalso Real.isNan y) orelse rep x = rep y
fun test1 x = equal (opt (Real.fromString ("   " ^ exact x ^ "foobar")),x)
fun test2 s = s = exact (from s)
fun test_read s =
let
val sz = size s
fun getc i = if i >= sz then NONE else SOME (String.sub (s,i),i+1)
fun mkstring (src,acc) =
case getc src of
NONE => implode (rev acc)
| SOME (c,src) => mkstring (src,c::acc)
in
case Real.scan getc 0 of
SOME (r,src) => SOME (rep r,mkstring (src,[]))
| _ => NONE
end;
fun check_overflow f =
(ignore(f ()); false) handle Overflow => true
val t1 = map test1 [0.0, 0.0/0.0, ~(0.0/0.0),1E34,Real.nextAfter(1E34,1E35),
Real.nextAfter (1.0,2.0), Real.nextAfter(1.0,0.0),
from "nan", from "+inf"]
val t2 = map test2 ["0.1E1",exact (Real.nextAfter (1.0,2.0)),
"nan"]
val t3 = equal (from "nan", from "nan")
val t4 = [test_read "nan" = SOME (rep (from "nan"),""),
test_read "nanfoo" = SOME (rep (from "nan"),"foo"),
test_read "foo" = NONE,
test_read "1.23E10" = SOME (rep (from "1.23E10"),""),
test_read "1.23Efoo" = SOME (rep (from "1.23"),"Efoo")]
val t5 = [test_read "1.0" = SOME (rep 1.0,""),
test_read ".1" = SOME (rep 0.1,""),
test_read "1.0E0" = SOME (rep 1.0,""),
test_read "0.1E1" = SOME (rep 1.0,""),
test_read "10E~1" = SOME (rep 1.0,""),
test_read "1" = SOME (rep 1.0,""),
test_read "1foo" = SOME (rep 1.0,"foo"),
test_read "1.0foo" = SOME (rep 1.0,"foo"),
test_read "1E00foo" = SOME (rep 1.0,"foo"),
test_read "1.0E00foo" = SOME (rep 1.0,"foo")]
val t6 = [test_read "  foo" = NONE,
test_read "  1." = SOME (rep 1.0,"."),
test_read "  1.E" = SOME (rep 1.0,".E"),
test_read "  1.E23" = SOME (rep 1.0,".E23")]
val t7 = [check_overflow (fn () => test_read ("1E1234455678988976563"))]
val t8 = [equal (Real.realFloor 1.0,1.0),
equal (Real.realCeil 1.0,1.0),
equal (Real.realTrunc 1.0,1.0),
equal (Real.realFloor 1.5,1.0),
equal (Real.realCeil 1.5,2.0)]
val t9 = [equal (Real.realTrunc 1.0,1.0),
Real.== (Real.realFloor (~(0.0)),0.0),
Real.== (Real.realCeil (~(0.0)),0.0),
Real.== (Real.realTrunc (~(0.0)),0.0),
equal (Real.realFloor Real.posInf,Real.posInf),
equal (Real.realCeil Real.posInf,Real.posInf),
equal (Real.realTrunc Real.posInf,Real.posInf)]
fun deccheck x =
let
fun chk x = equal (x, Real.fromDecimal (Real.toDecimal (x)))
in
chk x andalso chk (Real.nextAfter (x,Real.posInf))
end
val t10 = map deccheck [1.0,~1.0,0.0,~(0.0),
opt (Real.fromString "nan"),
Real.minPos, Real.minNormalPos,
Real.maxFinite, Real.posInf, Real.negInf]
;
