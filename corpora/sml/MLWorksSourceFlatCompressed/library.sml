require "../utils/map";
require "../lambda/auglambda";
signature LIBRARY = sig
structure AugLambda : AUGLAMBDA
structure NewMap : MAP
type CompilerOptions
val build_external_environment :
CompilerOptions * AugLambda.LambdaTypes.LambdaExp ->
(AugLambda.LambdaTypes.Primitive, AugLambda.LambdaTypes.LVar) NewMap.map *
AugLambda.LambdaTypes.LambdaExp
val implicit_external_references :
AugLambda.AugLambdaExp ->
AugLambda.LambdaTypes.Primitive AugLambda.LambdaTypes.Set.Set
end
;
