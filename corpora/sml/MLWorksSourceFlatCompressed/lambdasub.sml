require "lambdatypes";
signature LAMBDASUB =
sig
structure LT : LAMBDATYPES
val wrap_lets :
LT.LambdaExp * (LT.LVar * LT.LambdaExp) list -> LT.LambdaExp
val unwrap_lets :
LT.LambdaExp -> (LT.LVar * LT.LambdaExp) list * LT.LambdaExp
val apply_one_level :
(LT.LambdaExp -> LT.LambdaExp) -> LT.LambdaExp -> LT.LambdaExp
val occurs : LT.LVar * LT.LambdaExp -> bool
val eta_abstract :
LT.LambdaExp * string * LT.Type ref -> LT.LambdaExp
end;
