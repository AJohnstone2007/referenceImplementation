require "../utils/set";
require "../basics/ident";
signature LAMBDATYPES = sig
structure Set : SET
structure Ident : IDENT
type Type
type Tyfun
type Instance
type Structure
type DebuggerStr
type FunInfo
type VarInfo
eqtype LVar
eqtype Field
eqtype Primitive
datatype StructType = STRUCTURE | TUPLE | CONSTRUCTOR
datatype Status = ENTRY | BODY | FUNC
datatype Tag =
VCC_TAG of string * int
| IMM_TAG of string * int
| SCON_TAG of Ident.SCon * int option
| EXP_TAG of LambdaExp
and LambdaExp =
VAR of LVar
| FN of ((LVar list * LVar list) * LambdaExp * Status * string * Type * FunInfo)
| LET of (LVar * VarInfo ref option * LambdaExp) * LambdaExp
| LETREC of (LVar * VarInfo ref option) list * LambdaExp list * LambdaExp
| APP of (LambdaExp * (LambdaExp list * LambdaExp list) * Type ref option)
| SCON of Ident.SCon * int option
| MLVALUE of MLWorks.Internal.Value.ml_value
| INT of int
| SWITCH of LambdaExp * {num_vccs: int, num_imms: int} option *
(Tag * LambdaExp) list * LambdaExp option
| STRUCT of LambdaExp list * StructType
| SELECT of {index : int, size : int, selecttype : StructType} * LambdaExp
| RAISE of LambdaExp
| HANDLE of (LambdaExp * LambdaExp * string)
| BUILTIN of Primitive
datatype binding =
LETB of (LVar * VarInfo ref option * LambdaExp) |
RECLETB of (LVar * VarInfo ref option) list * LambdaExp list
datatype Dec =
VAL of LambdaExp |
FUNCTOR of LVar * string * ((LVar * VarInfo ref option * Dec) list * LambdaExp)
datatype program =
PROGRAM of (LVar * VarInfo ref option * Dec) list * LambdaExp
val do_binding : binding * LambdaExp -> LambdaExp
val new_LVar: unit -> LVar
val init_LVar: unit -> unit
val reset_counter: int -> unit
val read_counter: unit -> int
val lvar_to_int : LVar -> int
val printLVar: LVar -> string
val printPrim: Primitive -> string
val printField: {index : int, size : int,selecttype : StructType} -> string
val null_type_annotation : Type
val user_funinfo : FunInfo
val internal_funinfo : FunInfo
val isLocalFn : FunInfo -> bool
val telfun : (LambdaExp -> LambdaExp) -> (Tag * LambdaExp) -> (Tag * LambdaExp)
end
;
