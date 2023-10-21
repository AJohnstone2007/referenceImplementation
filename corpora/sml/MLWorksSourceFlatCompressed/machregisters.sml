require "../utils/set";
signature MACHREGISTERS =
sig
structure Set : SET
eqtype T
val gcs : T Set.Set
val non_gcs : T Set.Set
val fps : T Set.Set
val fn_arg : T
val cl_arg : T
val fp : T
val sp : T
val lr : T
val handler : T
val global : T
val gc1 : T
val gc2 : T
val after_preserve : T -> T
val after_restore : T -> T
end
;
