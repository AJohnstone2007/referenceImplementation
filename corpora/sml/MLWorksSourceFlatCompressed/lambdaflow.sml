require "lambdatypes";
signature LAMBDAFLOW =
sig
structure LambdaTypes : LAMBDATYPES
val preanalyse : LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
val tail_convert : LambdaTypes.program -> LambdaTypes.program
val lift_locals : LambdaTypes.program -> LambdaTypes.program
val loop_analysis : LambdaTypes.program -> LambdaTypes.program
val findfpargs : LambdaTypes.program -> LambdaTypes.program
end
;
