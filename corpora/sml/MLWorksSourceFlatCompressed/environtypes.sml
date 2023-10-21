require "../utils/map";
require "lambdatypes";
signature ENVIRONTYPES = sig
structure NewMap : MAP
structure LambdaTypes: LAMBDATYPES
datatype ValSpec =
STRIDSPEC of LambdaTypes.Ident.LongStrId |
VARSPEC of LambdaTypes.LVar |
NOSPEC
datatype comp =
EXTERNAL |
LAMB of LambdaTypes.LVar * ValSpec |
FIELD of {index : int, size : int} |
PRIM of LambdaTypes.Primitive
datatype Env =
ENV of (LambdaTypes.Ident.ValId, comp) NewMap.map *
(LambdaTypes.Ident.StrId, Env * comp * bool) NewMap.map
datatype Fun_Env =
FUN_ENV of (LambdaTypes.Ident.FunId, comp * Env * bool) NewMap.map
datatype Top_Env = TOP_ENV of Env * Fun_Env
datatype Foo = LVARFOO of LambdaTypes.LVar | INTFOO of int
datatype DebuggerExp =
NULLEXP |
LAMBDAEXP of {index : int, size : int} list * (LambdaTypes.LVar * LambdaTypes.LVar)
* LambdaTypes.Tyfun ref option |
LAMBDAEXP' of {index : int, size : int} list * Foo ref * LambdaTypes.Tyfun ref option |
INT of int
datatype DebuggerEnv =
DENV of (LambdaTypes.Ident.ValId, DebuggerExp) NewMap.map *
(LambdaTypes.Ident.StrId, DebuggerStrExp) NewMap.map
and DebuggerStrExp =
LAMBDASTREXP of {index : int, size : int} list * (LambdaTypes.LVar * LambdaTypes.LVar) * LambdaTypes.Structure |
LAMBDASTREXP' of {index : int, size : int} list * Foo ref * LambdaTypes.Structure |
DENVEXP of DebuggerEnv
end
;
