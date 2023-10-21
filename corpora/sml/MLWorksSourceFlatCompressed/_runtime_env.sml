require "../typechecker/datatypes";
require "runtime_env";
functor RuntimeEnv (structure Datatypes : DATATYPES
) : RUNTIMEENV =
struct
type Type = Datatypes.Type
type Tyfun = Datatypes.Tyfun
type Instance = Datatypes.Instance
datatype Tag =
CONSTRUCTOR of string
| INT of string
| REAL of string
| STRING of string
| CHAR of string
| WORD of string
| DYNAMIC
| DEFAULT
datatype SpillArea = GC | NONGC | FP
datatype Offset = OFFSET1 of int | OFFSET2 of (SpillArea * int)
datatype RuntimeInfo = RUNTIMEINFO of (Instance ref option * (Tyfun ref * Offset ref) list)
datatype VarInfo =
NOVARINFO
| VARINFO of (string * (Type ref * RuntimeInfo ref) * Offset ref option)
datatype FunInfo =
USER_FUNCTION
| INTERNAL_FUNCTION
| LOCAL_FUNCTION
| FUNINFO of ((int * Type * Instance) ref list * Offset ref)
datatype RuntimeEnv =
LET of (VarInfo * RuntimeEnv) list * RuntimeEnv
| FN of string * RuntimeEnv * Offset ref * FunInfo
| APP of RuntimeEnv * RuntimeEnv * int option
| RAISE of RuntimeEnv
| SELECT of int * RuntimeEnv
| STRUCT of RuntimeEnv list
| LIST of RuntimeEnv list
| SWITCH of RuntimeEnv * Offset ref * int * (Tag * RuntimeEnv) list
| HANDLE of RuntimeEnv * Offset ref * int * int * RuntimeEnv
| EMPTY
| BUILTIN
end
;
