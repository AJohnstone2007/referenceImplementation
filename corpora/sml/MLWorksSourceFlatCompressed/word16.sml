val a : MLWorks.Internal.Types.word16 = 0w12
val b = a mod 0w5
val c = a div 0w5
val f = 0w0 - a
val g = f mod 0w5
val h = f div 0w5
val aa : MLWorks.Internal.Types.word16 = 0w12
val ab = aa + 0w7
val ad = aa - 0w3
val af : MLWorks.Internal.Types.word16 = 0w32768
val ag = af + af handle Overflow => 0w3
val ah = 0w0 - af
val ai = ah + ah
val aj = ai - 0w1 handle Overflow => 0w1
val al = af - ah handle Overflow => 0w7
val am = af * af handle Overflow => 0w11
val an : MLWorks.Internal.Types.word16 = 0w8
val ao = an*0w2
val ap = an * ao handle Overflow => 0w13
val aq = an * (0w0 - ao)
val ar = ao * (0w0 - ao) handle Overflow => 0w15
val ss = an div 0w0 handle Div => 0w17
val at = aq div 0w1
val ax = aq mod 0w0 handle Div => 0w23
;
