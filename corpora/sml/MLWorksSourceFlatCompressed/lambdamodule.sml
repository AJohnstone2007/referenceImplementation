require "environtypes";
signature LAMBDAMODULE =
sig
structure EnvironTypes : ENVIRONTYPES
val pack :
EnvironTypes.Top_Env * EnvironTypes.LambdaTypes.binding list ->
EnvironTypes.Top_Env * EnvironTypes.LambdaTypes.LambdaExp
val unpack :
EnvironTypes.Top_Env * EnvironTypes.LambdaTypes.LambdaExp ->
EnvironTypes.Top_Env * EnvironTypes.LambdaTypes.binding list
end;
