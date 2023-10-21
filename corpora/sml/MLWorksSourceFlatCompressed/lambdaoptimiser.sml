require "lambdatypes";
require "../main/options";
signature LAMBDAOPTIMISER =
sig
structure LambdaTypes : LAMBDATYPES
structure Options : OPTIONS
val optimise : Options.options -> LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
val simple_beta_reduce : LambdaTypes.LambdaExp -> LambdaTypes.LambdaExp
end;
