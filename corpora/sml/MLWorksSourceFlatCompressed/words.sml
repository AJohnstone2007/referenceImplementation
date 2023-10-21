local
open Word
val max : word = 0wx3fffffff
val maxstring = "3FFFFFFF"
val ones = "FFFFFFFF"
val t1 = wordSize = 30
in
val t2 = fromLargeWord (toLargeWord 0w0) = 0w0
val t3 = fromLargeWord (toLargeWord max) = max
val t4 = Word32.+ (toLargeWordX max,0w1) = 0w0
val t5 = (ignore(toLargeInt max); true) handle Overflow => false
val t6 = (ignore(toInt max); false) handle Overflow => true
val t7 = toLargeIntX max = ~1
val t8 = fromLargeInt (valOf LargeInt.maxInt) = max
val t9 = map (fn (a,b) => a (0w0,max) = b)
[(orb,max),
(xorb,max),
(andb,0w0)]
val t10 = 0w0 = notb max
val t11 = max = ~>> (<< (0w1,0w29),0w29)
val t12 = 0w1 = >> (<< (0w1,0w29),0w29)
val t13 = 0w0 = >> (<< (0w1,0w30),0w30)
val t14 = max - 0w1 = max + max
val t15 = compare (0w0,max) = LESS
val t16 = 0w0 < max andalso max > 0w0 andalso 0w0 < 0w255
val t17 = fmt StringCvt.HEX max = maxstring
val t18 = fmt StringCvt.HEX 0w0 = "0"
val t19 = fmt StringCvt.DEC 0w0 = "0"
val t20 = valOf (fromString maxstring) = max
val t21 = (ignore(valOf (fromString ones) = max); false) handle Overflow => true
end;
local
open Word32
val max : word = 0wxffffffff
val maxstring = "FFFFFFFF"
val ones = "FFFFFFFF"
in
val t1 = wordSize = 32
val t2 = fromLargeWord (toLargeWord 0w0) = 0w0
val t3 = fromLargeWord (toLargeWord max) = max
val t4 = Word32.+ (toLargeWordX max,0w1) = 0w0
val t5 = (ignore(toLargeInt max); false) handle Overflow => true
val t6 = (ignore(toInt max); false) handle Overflow => true
val t7 = toLargeIntX max = ~1
val t9 = map (fn (a,b) => a (0w0,max) = b)
[(orb,max),
(xorb,max),
(andb,0w0)]
val t11 = max = ~>> (<< (0w1,0w31),0w31)
val t12 = 0w1 = >> (<< (0w1,0w31),0w31)
val t13 = 0w0 = >> (<< (0w1,0w32),0w32)
val t14 = max - 0w1 = max + max
val t15 = compare (0w0,max) = LESS
val t16 = 0w0 < max andalso max > 0w0 andalso 0w0 < 0w255
val t17 = fmt StringCvt.HEX max = maxstring
val t18 = fmt StringCvt.HEX 0w0 = "0"
val t19 = fmt StringCvt.DEC 0w0 = "0"
val t20 = valOf (fromString maxstring) = max
val t21 = valOf (fromString ones) = max
end;
local
open Word8
val max : word = 0wxff
val maxstring = "FF"
val ones = "FFFFFFFF"
val wwsize = Word.fromInt (wordSize)
in
val t1 = wordSize = 8
val t2 = fromLargeWord (toLargeWord 0w0) = 0w0
val t3 = fromLargeWord (toLargeWord max) = max
val t4 = Word32.+ (toLargeWordX max,0w1) = 0w0
val t5 = (ignore(toLargeInt max); true) handle Overflow => false
val t6 = toInt max = 255
val t7 = toLargeIntX max = ~1
val t8 = fromLargeInt (valOf LargeInt.maxInt) = max
val t9 = map (fn (a,b) => a (0w0,max) = b)
[(orb,max),
(xorb,max),
(andb,0w0)]
val t10 = 0w0 = notb max
val t11 = max = ~>> (<< (0w1,Word.- (wwsize,0w1)),Word.- (wwsize,0w1))
val t12 = 0w1 = >> (<< (0w1,Word.- (wwsize,0w1)),Word.- (wwsize,0w1))
val t13 = 0w0 = >> (<< (0w1,wwsize),wwsize)
val t14 = max - 0w1 = max + max
val t15 = compare (0w0,max) = LESS
val t16 = 0w0 < max andalso max > 0w0 andalso 0w0 < 0w255
val t17 = fmt StringCvt.HEX max = maxstring
val t18 = fmt StringCvt.HEX 0w0 = "0"
val t19 = fmt StringCvt.DEC 0w0 = "0"
val t20 = valOf (fromString maxstring) = max
val t21 = (ignore(valOf (fromString ones) = max); false) handle Overflow => true
end;
