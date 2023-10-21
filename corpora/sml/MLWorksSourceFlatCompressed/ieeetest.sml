fun opt (SOME x) = x | opt _ = raise Div
val f = IEEEReal.toString o opt o IEEEReal.fromString;
val f = IEEEReal.toString o Real.toDecimal o Real.fromDecimal o opt o IEEEReal.fromString;
val rep = MLWorks.Internal.Value.real_to_string
fun eqrep (x,y) = rep x = rep y
fun check s =
let
val x = opt (IEEEReal.fromString ("  " ^ s ^ "foobar"))
fun eq_decimal_approx(de1, de2) =
IEEEReal.class(de1) = IEEEReal.class(de2) andalso
IEEEReal.signBit(de1) = IEEEReal.signBit(de2) andalso
IEEEReal.digits(de1) = IEEEReal.digits(de2) andalso
IEEEReal.exp(de1) = IEEEReal.exp(de2)
in
eq_decimal_approx(x, opt (IEEEReal.fromString (IEEEReal.toString x))) andalso
eqrep (opt (Real.fromString s),Real.fromDecimal x)
end;
val t1 =
map check [" 1.0",
" InFiNiTy",
"-InFiNiTy",
"InFinI",
"NaN(1234)",
"nan"];
val t2 =
map check ["0.1E+1",
".1E+1",
"10.0E~1",
"1.2345E123",
"1.2345E~123",
"~1.2345E123",
"~1.2345E~123"];
val t3 =
map check ["0.1E+",
".1E",
"10.0E~",
"1.2345E~",
"~1."];
val t4 =
map check ["1.0",
"1.00000",
"0.1E1",
"0.001E3",
"1000E~4"];
