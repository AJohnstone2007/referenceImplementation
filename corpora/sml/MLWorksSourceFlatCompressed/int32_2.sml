val a : MLWorks.Internal.Types.int32 = 12
val b = a + 7
val c = b + ~4
val d = a - 3
val e = d - ~5
val f : MLWorks.Internal.Types.int32 = 0x40000000
val g = f + f handle Overflow => 3
val h = ~f
val i = h + h
val j = i - 1 handle Overflow => 1
val k = i + ~1 handle Overflow => 5
val l = f - h handle Overflow => 7
val m = f * f handle Overflow => 11
val n : MLWorks.Internal.Types.int32 = 0x8000
val oo = n*2
val p = n * oo handle Overflow => 13
val q = n * ~oo
val r = oo * ~oo handle Overflow => 15
val s = n div 0 handle Div => 17
val t = q div 1
val u = q div ~1 handle Overflow => 19
val w = q div ~3
val x = q mod 0 handle Mod => 23
val y = abs q handle Overflow => 29
val z = ~q handle Overflow => 31
;
