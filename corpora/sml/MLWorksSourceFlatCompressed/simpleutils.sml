require "lambdatypes";
signature SIMPLEUTILS =
sig
structure LambdaTypes : LAMBDATYPES
val occurs : LambdaTypes.LVar * LambdaTypes.LambdaExp -> bool
val freevars : LambdaTypes.LambdaExp * LambdaTypes.LVar list -> LambdaTypes.LVar list
val vars_of : LambdaTypes.LambdaExp -> LambdaTypes.LVar list
val linearize : LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
val is_atomic : LambdaTypes.LambdaExp -> bool
val is_simple : LambdaTypes.LambdaExp -> bool
val switchable_exp : LambdaTypes.LambdaExp -> bool
val safe : LambdaTypes.LambdaExp -> bool
val safe_elim : LambdaTypes.LambdaExp -> bool
val safe_cse : LambdaTypes.LambdaExp -> bool
val get_arity : LambdaTypes.Primitive -> int
val insert_as_needed :
(LambdaTypes.LVar *
LambdaTypes.VarInfo ref option *
LambdaTypes.LambdaExp) list
* LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
val schedule : LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
val size_less : LambdaTypes.LambdaExp * int * bool -> bool
val exp_eq : LambdaTypes.LambdaExp * LambdaTypes.LambdaExp -> bool
val wrap_lets :
LambdaTypes.LambdaExp *
(LambdaTypes.LVar * LambdaTypes.VarInfo ref option * LambdaTypes.LambdaExp) list ->
LambdaTypes.LambdaExp
val unwrap_lets :
LambdaTypes.LambdaExp ->
(LambdaTypes.LVar * LambdaTypes.VarInfo ref option * LambdaTypes.LambdaExp) list *
LambdaTypes.LambdaExp
val subst :
(LambdaTypes.LVar -> LambdaTypes.LambdaExp) * LambdaTypes.LambdaExp ->
LambdaTypes.LambdaExp
val function_lvars : LambdaTypes.LambdaExp -> LambdaTypes.LVar list
val is_fp_builtin : LambdaTypes.Primitive -> bool
end
;
