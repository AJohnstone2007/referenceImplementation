require "../lambda/lambdatypes";
require "../debugger/debugger_types";
signature AUGLAMBDA = sig
structure LambdaTypes : LAMBDATYPES
structure Debugger_Types : DEBUGGER_TYPES
datatype Tag =
VCC_TAG of string * int
| IMM_TAG of string * int
| SCON_TAG of LambdaTypes.Ident.SCon * int option
| EXP_TAG of {size:int, lexp:AugLambdaExp}
and AugLambdaExp =
VAR of LambdaTypes.LVar
| FN of ((LambdaTypes.LVar list * LambdaTypes.LVar list) * {size:int, lexp:AugLambdaExp} * string
* LambdaTypes.FunInfo)
| LET of (LambdaTypes.LVar * LambdaTypes.VarInfo ref option * {size:int, lexp:AugLambdaExp}) *
{size:int, lexp:AugLambdaExp}
| LETREC of
((LambdaTypes.LVar * LambdaTypes.VarInfo ref option) list *
{size:int, lexp:AugLambdaExp} list *
{size:int, lexp:AugLambdaExp})
| APP of ({size:int, lexp:AugLambdaExp} *
({size:int, lexp:AugLambdaExp} list *
{size:int, lexp:AugLambdaExp} list) *
Debugger_Types.Backend_Annotation)
| SCON of LambdaTypes.Ident.SCon * int option
| MLVALUE of MLWorks.Internal.Value.ml_value
| INT of int
| SWITCH of
({size:int, lexp:AugLambdaExp} *
{num_vccs: int, num_imms: int} option *
(Tag * {size:int, lexp:AugLambdaExp}) list *
{size:int, lexp:AugLambdaExp} option)
| STRUCT of {size:int, lexp:AugLambdaExp} list
| SELECT of {index : int, size : int} * {size:int, lexp:AugLambdaExp}
| RAISE of {size:int, lexp:AugLambdaExp}
| HANDLE of ({size:int, lexp:AugLambdaExp} * {size:int, lexp:AugLambdaExp})
| BUILTIN of LambdaTypes.Primitive * LambdaTypes.Type
val count_gc_objects : Debugger_Types.Options.options *
LambdaTypes.LambdaExp * bool * Debugger_Types.information * string ->
{size:int, lexp:AugLambdaExp} * Debugger_Types.information
end
;
