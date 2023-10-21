require "../basis/__int";
require "../utils/set";
require "../utils/counter";
require "../main/pervasives";
require "../typechecker/datatypes" ;
require "../debugger/runtime_env";
require "lambdatypes";
functor LambdaTypes(
structure Set : SET
structure Counter: COUNTER
structure Pervasives: PERVASIVES
structure Datatypes : DATATYPES
structure RuntimeEnv : RUNTIMEENV
) : LAMBDATYPES =
struct
structure Set = Set
structure Ident = Datatypes.Ident
structure Datatypes = Datatypes
type Type = Datatypes.Type
type Tyfun = Datatypes.Tyfun
type Instance = Datatypes.Instance
type Structure = Datatypes.Structure
type DebuggerStr = Datatypes.DebuggerStr
type FunInfo = RuntimeEnv.FunInfo
type VarInfo = RuntimeEnv.VarInfo
type LVar = int
type Field = int
type Primitive = Pervasives.pervasive
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
| LETREC of ((LVar * VarInfo ref option) list * LambdaExp list * LambdaExp)
| APP of (LambdaExp * (LambdaExp list * LambdaExp list) * Type ref option)
| SCON of Ident.SCon * int option
| MLVALUE of MLWorks.Internal.Value.ml_value
| INT of int
| SWITCH of
LambdaExp *
{num_vccs: int, num_imms: int} option *
(Tag * LambdaExp) list *
LambdaExp option
| STRUCT of LambdaExp list * StructType
| SELECT of {index : int, size : int, selecttype : StructType} * LambdaExp
| RAISE of LambdaExp
| HANDLE of (LambdaExp * LambdaExp * string)
| BUILTIN of Primitive
datatype binding =
LETB of (LVar * VarInfo ref option * LambdaExp) |
RECLETB of ((LVar * VarInfo ref option) list * LambdaExp list)
fun do_binding(LETB(lv,info,le), exp) = LET((lv,info,le),exp)
| do_binding(RECLETB(lv_list, le_list), exp)= LETREC(lv_list, le_list, exp)
fun new_LVar () = Counter.counter ()
fun init_LVar () = Counter.reset_counter 0
fun lvar_to_int x = x
fun printLVar lvar = Int.toString lvar
val read_counter = Counter.read_counter
val reset_counter = Counter.reset_counter
fun printPrim prim = Pervasives.print_pervasive prim
fun printField {index : int, size : int,selecttype : StructType} =
Int.toString index ^ "/" ^
Int.toString size;
val null_type_annotation = Datatypes.NULLTYPE
val user_funinfo = RuntimeEnv.USER_FUNCTION
val internal_funinfo = RuntimeEnv.INTERNAL_FUNCTION
fun isLocalFn (RuntimeEnv.LOCAL_FUNCTION) = true
| isLocalFn _ = false
datatype Dec =
VAL of LambdaExp |
FUNCTOR of LVar * string * ((LVar * VarInfo ref option * Dec) list * LambdaExp)
datatype program =
PROGRAM of (LVar * VarInfo ref option * Dec) list * LambdaExp
fun telfun f (EXP_TAG e, e') = (EXP_TAG (f e), f e')
| telfun f (t,e) = (t,f e)
val new_valid = new_LVar
end
;
