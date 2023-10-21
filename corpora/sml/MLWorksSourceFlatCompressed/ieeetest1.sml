local
fun test_mode(mode) =
let
val _ = IEEEReal.setRoundingMode mode;
in
mode = IEEEReal.getRoundingMode()
end
fun check s func arg =
if func arg then
s ^ ": OK"
else
s ^ ": Wrong"
val test = check "Rounding mode" test_mode
in
val t1 = test IEEEReal.TO_NEAREST;
val t2 = test IEEEReal.TO_ZERO;
val t3 = test IEEEReal.TO_NEGINF;
val t4 = test IEEEReal.TO_POSINF;
end
;
