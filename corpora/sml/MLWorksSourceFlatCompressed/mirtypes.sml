require "../utils/set";
require "../utils/monomap";
require "../debugger/debugger_types";
require "virtualregister";
signature MIRTYPES =
sig
structure Set : SET
structure Map : MONOMAP
structure Debugger_Types : DEBUGGER_TYPES
eqtype SCon
structure GC : VIRTUALREGISTER
structure NonGC : VIRTUALREGISTER
structure FP : VIRTUALREGISTER
sharing GC.Set.Text = NonGC.Set.Text = FP.Set.Text
datatype any_register =
GC of GC.T |
NON_GC of NonGC.T |
FLOAT of FP.T
val order_any_reg : any_register * any_register -> bool
datatype SlotInfo = SIMPLE of int | DEBUG of Debugger_Types.RuntimeEnv.Offset ref * string
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
val gp_to_any : gp_operand -> any_register
val any_to_gp : any_register -> gp_operand
datatype reg_operand =
GC_REG of GC.T
| NON_GC_REG of NonGC.T
val reg_to_any : reg_operand -> any_register
val any_to_reg : any_register -> reg_operand
datatype fp_operand =
FP_REG of FP.T
val fp_to_any : fp_operand -> any_register
val any_to_fp : any_register -> fp_operand
eqtype tag
sharing type tag = Map.object
val init_tag: unit -> unit
val new_tag: unit -> tag
val int_of_tag : tag -> int
val print_tag: tag -> string
val print_tag_list: tag list -> string
val order_tag: tag * tag -> bool
val equal_tag: tag * tag -> bool
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
datatype tagged_binary_fp_op = FADDV | FSUBV | FMULV | FDIVV
datatype binary_fp_op = FADD | FSUB | FMUL | FDIV
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
SCON of SCon
| MLVALUE of MLWorks.Internal.Value.ml_value
datatype value =
VALUE of tag * valspec
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
Debugger_Types.RuntimeEnv.RuntimeEnv
datatype mir_code = CODE of
refs *
value list *
procedure list list
end
;
