Shell.Options.set(Shell.Options.Language.oldDefinition,true);
fun f 0.0 = 0.0
| f x = (print(Real.toString x); print"\n"; f (x / 2.0))
val it = (f 0.0, f 1.0)
;
