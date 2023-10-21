val posinf = 1.0/0.0
val neginf = ~1.0/0.0
val nan = 0.0/0.0
fun check_exn (format, arg) =
(ignore(Real.fmt format arg); "WRONG")
handle Size => "OK"
fun check_res (format, arg, res) =
(if Real.fmt format arg = res then
"OK"
else
"WRONG")
handle _ => "EXN"
val test1a = check_exn (StringCvt.GEN (SOME 0), 10.5)
val test1b = check_exn (StringCvt.GEN (SOME ~1), 10.5)
val test1c = check_exn (StringCvt.SCI (SOME ~1), 10.5)
val test1d = check_exn (StringCvt.FIX (SOME ~1), 10.5)
val test2a = check_res (StringCvt.GEN (SOME 1), 1.5, "2");
val test2b = check_res (StringCvt.GEN (SOME 1), 10.5, "1E01");
val test2c = check_res (StringCvt.GEN (SOME 2), ~1000.0, "~1E03");
val test2d = check_res (StringCvt.GEN (SOME 1), 0.012, "0.01");
val test2e = check_res (StringCvt.GEN (SOME 3), ~0.0123, "~0.0123");
val test2f = check_res (StringCvt.GEN (SOME 3), 0.0000123, "1.23E~05");
val test2g = check_res (StringCvt.GEN NONE, 0.0000123, "1.23E~05");
val test3a = check_res (StringCvt.SCI (SOME 1), 1.5, "1.5E00");
val test3b = check_res (StringCvt.SCI (SOME 0), 10.5, "1E01");
val test3c = check_res (StringCvt.SCI (SOME 2), ~1000.0, "~1.00E03");
val test3d = check_res (StringCvt.SCI (SOME 1), 0.012, "1.2E~02");
val test3e = check_res (StringCvt.SCI (SOME 3), ~0.0123, "~1.230E~02");
val test3f = check_res (StringCvt.SCI (SOME 3), 0.0000123, "1.230E~05");
val test3g = check_res (StringCvt.SCI NONE, 0.0000123, "1.230000E~05");
val test4a = check_res (StringCvt.FIX (SOME 1), 1.5, "1.5");
val test4c = check_res (StringCvt.FIX (SOME 2), ~1000.0, "~1000.00");
val test4d = check_res (StringCvt.FIX (SOME 1), 0.012, "0.0");
val test4e = check_res (StringCvt.FIX (SOME 3), ~0.0123, "~0.012");
val test4f = check_res (StringCvt.FIX (SOME 3), 0.0000123, "0.000");
val test4g = check_res (StringCvt.FIX NONE, 0.0000123, "0.000012");
val test5a = check_res (StringCvt.FIX (SOME 1), posinf, "+inf");
val test5b = check_res (StringCvt.FIX (SOME 1), neginf, "-inf");
val test5c = check_res (StringCvt.FIX (SOME 1), nan, "nan");
val test5d = check_res (StringCvt.SCI (SOME 1), posinf, "+inf");
val test5e = check_res (StringCvt.SCI (SOME 1), neginf, "-inf");
val test5f = check_res (StringCvt.SCI (SOME 1), nan, "nan");
val test5g = check_res (StringCvt.GEN (SOME 1), posinf, "+inf");
val test5h = check_res (StringCvt.GEN (SOME 1), neginf, "-inf");
val test5i = check_res (StringCvt.GEN (SOME 1), nan, "nan");
