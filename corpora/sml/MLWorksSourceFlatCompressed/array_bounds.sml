local
val a = MLWorks.Internal.ExtendedArray.array(10, 0)
in
val x = (MLWorks.Internal.Array.update(a, 9, 0);
0) handle MLWorks.Internal.Array.Subscript => 1
val y = (MLWorks.Internal.Array.update(a, 0, 0);
0) handle MLWorks.Internal.Array.Subscript => 1
val z = (MLWorks.Internal.Array.update(a, ~1, 0);
0) handle MLWorks.Internal.Array.Subscript => 1
val b = (MLWorks.Internal.Array.update(a, 10, 0);
0) handle MLWorks.Internal.Array.Subscript => 1
val c = (MLWorks.Internal.Array.sub(a, 9)) handle MLWorks.Internal.Array.Subscript => 1
val d = (MLWorks.Internal.Array.sub(a, 0)) handle MLWorks.Internal.Array.Subscript => 1
val e = (MLWorks.Internal.Array.sub(a, ~1)) handle MLWorks.Internal.Array.Subscript => 1
val f = (MLWorks.Internal.Array.sub(a, 10)) handle MLWorks.Internal.Array.Subscript => 1
end
;
