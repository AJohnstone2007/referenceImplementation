require "../basis/__int";
require "../utils/counter";
require "../utils/set";
require "../utils/intnewmap";
require "../basics/ident";
require "../debugger/debugger_types";
require "virtualregister";
require "mirtypes";
functor MirTypes(
structure GC : VIRTUALREGISTER
structure NonGC : VIRTUALREGISTER
structure FP : VIRTUALREGISTER
structure Map : INTNEWMAP
structure Counter : COUNTER
structure Set : SET
structure Ident : IDENT
structure Debugger_Types : DEBUGGER_TYPES
sharing GC.Set.Text = NonGC.Set.Text = FP.Set.Text
) : MIRTYPES =
struct
structure Ident = Ident
structure Set = Set
structure Text = GC.Set.Text
structure GC = GC
structure NonGC = NonGC
structure FP = FP
structure Map = Map
structure Debugger_Types = Debugger_Types
structure RuntimeEnv = Debugger_Types.RuntimeEnv
type SCon = Ident.SCon
datatype any_register =
GC of GC.T |
NON_GC of NonGC.T |
FLOAT of FP.T
fun order_any_reg (GC r, GC r') = GC.order (r, r')
| order_any_reg (GC _, _) = true
| order_any_reg (_, GC _) = false
| order_any_reg (NON_GC r, NON_GC r') = NonGC.order (r, r')
| order_any_reg (NON_GC _, _) = true
| order_any_reg (_, NON_GC _) = false
| order_any_reg (FLOAT r, FLOAT r') = FP.order (r, r')
datatype SlotInfo = SIMPLE of int | DEBUG of RuntimeEnv.Offset ref * string
datatype symbolic =
GC_SPILL_SIZE |
NON_GC_SPILL_SIZE |
GC_SPILL_SLOT of SlotInfo |
NON_GC_SPILL_SLOT of SlotInfo |
FP_SPILL_SLOT of SlotInfo
datatype gp_operand =
GP_GC_REG of GC.T
| GP_NON_GC_REG of NonGC.T
| GP_IMM_INT of int
| GP_IMM_ANY of int
| GP_IMM_SYMB of symbolic
exception NotAnyRegister
fun gp_to_any (GP_GC_REG reg) = GC reg
| gp_to_any (GP_NON_GC_REG reg) = NON_GC reg
| gp_to_any _ = raise NotAnyRegister
fun any_to_gp (GC reg) = GP_GC_REG reg
| any_to_gp (NON_GC reg) = GP_NON_GC_REG reg
| any_to_gp _ = raise NotAnyRegister
datatype reg_operand =
GC_REG of GC.T
| NON_GC_REG of NonGC.T
fun reg_to_any (GC_REG reg) = GC reg
| reg_to_any (NON_GC_REG reg) = NON_GC reg
fun any_to_reg (GC reg) = GC_REG reg
| any_to_reg (NON_GC reg) = NON_GC_REG reg
| any_to_reg _ = raise NotAnyRegister
datatype fp_operand =
FP_REG of FP.T
fun fp_to_any (FP_REG reg) = FLOAT reg
fun any_to_fp (FLOAT reg) = FP_REG reg
| any_to_fp _ = raise NotAnyRegister
type tag = int
fun int_of_tag tag = tag
fun init_tag () = Counter.reset_counter 0
fun new_tag () = Counter.counter ()
fun print_tag t = Int.toString t
fun print_tag_list [] = "_"
| print_tag_list ([t]) = print_tag t
| print_tag_list (t::rest) = print_tag t ^ ", " ^ print_tag_list rest
fun order_tag (tag : tag, tag' : tag) = tag<tag'
fun equal_tag (tag : tag, tag' : tag) = tag=tag'
fun init_counters () = init_tag()
datatype bl_dest = TAG of tag | REG of reg_operand
datatype binary_op =
ADDU |
SUBU |
MULU |
MUL32U |
AND |
OR |
EOR |
LSR |
ASL |
ASR
datatype tagged_binary_op =
ADDS |
SUBS |
ADD32S |
SUB32S |
MULS |
DIVS |
MODS |
MUL32S |
DIV32S |
MOD32S |
DIVU |
MODU |
DIV32U |
MOD32U
datatype unary_op =
MOVE |
INTTAG |
NOT |
NOT32
datatype binary_fp_op = FADD | FSUB | FMUL | FDIV
datatype tagged_binary_fp_op = FADDV | FSUBV | FMULV | FDIVV
datatype tagged_unary_fp_op =
FABSV |
FNEGV |
FSQRTV |
FLOGEV |
FETOXV
datatype unary_fp_op =
FMOVE |
FABS |
FNEG |
FINT |
FSQRT |
FLOG10 |
FLOG2 |
FLOGE |
FLOGEP1 |
F10TOX |
F2TOX |
FETOX |
TETOXM1 |
FSIN |
FCOS |
FTAN |
FASIN |
FACOS |
FATAN
datatype stack_op = PUSH | POP
datatype store_op = LD | ST | LDB | STB | LDREF | STREF
datatype store_fp_op = FLD | FST | FSTREF | FLDREF
datatype int_to_float = ITOF
datatype float_to_int = FTOI
datatype branch = BRA
datatype cond_branch =
BTA |
BNT |
BEQ |
BNE |
BHI |
BLS |
BHS |
BLO |
BGT |
BLE |
BGE |
BLT
datatype fcond_branch =
FBEQ |
FBNE |
FBLE |
FBLT
datatype branch_and_link = BLR
datatype tail_call = TAIL
datatype computed_goto = CGT
datatype allocate =
ALLOC |
ALLOC_VECTOR |
ALLOC_REAL |
ALLOC_STRING |
ALLOC_BYTEARRAY |
ALLOC_REF
datatype adr = LEA | LEO
datatype nullary_op = CLEAN
datatype opcode =
TBINARY of tagged_binary_op * tag list * reg_operand * gp_operand * gp_operand |
BINARY of binary_op * reg_operand * gp_operand * gp_operand |
UNARY of unary_op * reg_operand * gp_operand |
NULLARY of nullary_op * reg_operand |
TBINARYFP of tagged_binary_fp_op * tag list * fp_operand * fp_operand *
fp_operand |
TUNARYFP of tagged_unary_fp_op * tag list * fp_operand * fp_operand |
BINARYFP of binary_fp_op * fp_operand * fp_operand * fp_operand |
UNARYFP of unary_fp_op * fp_operand * fp_operand |
STACKOP of stack_op * reg_operand * int option |
STOREOP of store_op * reg_operand * reg_operand * gp_operand |
IMMSTOREOP of store_op * gp_operand * reg_operand * gp_operand |
STOREFPOP of store_fp_op * fp_operand * reg_operand * gp_operand |
REAL of int_to_float * fp_operand * gp_operand |
FLOOR of float_to_int * tag * reg_operand * fp_operand |
BRANCH of branch * bl_dest |
TEST of cond_branch * tag * gp_operand * gp_operand |
FTEST of fcond_branch * tag * fp_operand * fp_operand |
BRANCH_AND_LINK of branch_and_link * bl_dest * Debugger_Types.Backend_Annotation * any_register list |
TAIL_CALL of tail_call * bl_dest * any_register list |
CALL_C |
SWITCH of computed_goto * reg_operand * tag list |
ALLOCATE of allocate * reg_operand * gp_operand |
ALLOCATE_STACK of allocate * reg_operand * int * int option |
DEALLOCATE_STACK of allocate * int |
ADR of adr * reg_operand * tag |
INTERCEPT |
INTERRUPT |
ENTER of any_register list |
RTS |
NEW_HANDLER of reg_operand *
tag |
OLD_HANDLER |
RAISE of reg_operand |
COMMENT of string
datatype refs = REFS of
(tag * int) list *
{requires : (string * int) list,
vars : (string * int) list,
exns : (string * int) list,
strs : (string * int) list,
funs : (string * int) list}
datatype valspec =
SCON of Ident.SCon
| MLVALUE of MLWorks.Internal.Value.ml_value
datatype value = VALUE of tag * valspec
datatype block = BLOCK of tag * opcode list
datatype procedure_parameters = PROC_PARAMS of
{spill_sizes : {gc : int,
non_gc : int,
fp : int} option,
old_spill_sizes : {gc : int,
non_gc : int,
fp : int} option,
stack_allocated : int option}
datatype procedure = PROC of
string *
tag *
procedure_parameters *
block list *
RuntimeEnv.RuntimeEnv
datatype mir_code = CODE of
refs *
value list *
procedure list list
val _ = init_counters()
end
;
