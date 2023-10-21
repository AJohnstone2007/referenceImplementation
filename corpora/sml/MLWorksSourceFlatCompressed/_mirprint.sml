require "../basis/__int";
require "../basis/__text_io";
require "../utils/lists";
require "../utils/crash";
require "../basics/identprint";
require "mirregisters";
require "mirprint";
functor MirPrint(
structure Lists : LISTS
structure Crash : CRASH
structure IdentPrint : IDENTPRINT
structure MirRegisters : MIRREGISTERS
sharing type IdentPrint.Ident.SCon = MirRegisters.MirTypes.SCon
) : MIRPRINT =
struct
structure MirTypes = MirRegisters.MirTypes
structure Set = MirTypes.Set
structure RuntimeEnv = MirTypes.Debugger_Types.RuntimeEnv
fun binary MirTypes.ADDU = "ADDU"
| binary MirTypes.SUBU = "SUBU"
| binary MirTypes.MULU = "MULU"
| binary MirTypes.MUL32U = "MUL32U"
| binary MirTypes.AND = "AND"
| binary MirTypes.OR = "OR"
| binary MirTypes.EOR = "EOR"
| binary MirTypes.LSR = "LSR"
| binary MirTypes.ASL = "ASL"
| binary MirTypes.ASR = "ASR"
fun tagged_binary MirTypes.ADDS = "ADDS"
| tagged_binary MirTypes.SUBS = "SUBS"
| tagged_binary MirTypes.ADD32S = "ADD32S"
| tagged_binary MirTypes.SUB32S = "SUB32S"
| tagged_binary MirTypes.MULS = "MULS"
| tagged_binary MirTypes.DIVS = "DIVS"
| tagged_binary MirTypes.MODS = "MODS"
| tagged_binary MirTypes.MUL32S = "MUL32S"
| tagged_binary MirTypes.DIV32S = "DIV32S"
| tagged_binary MirTypes.MOD32S = "MOD32S"
| tagged_binary MirTypes.DIVU = "DIVU"
| tagged_binary MirTypes.MODU = "MODU"
| tagged_binary MirTypes.DIV32U = "DIV32U"
| tagged_binary MirTypes.MOD32U = "MOD32U"
fun unary MirTypes.MOVE = "MOVE"
| unary MirTypes.INTTAG = "INTTAG"
| unary MirTypes.NOT = "NOT"
| unary MirTypes.NOT32 = "NOT32"
fun nullary MirTypes.CLEAN = "CLEAN"
fun binaryfp MirTypes.FADD = "FADD"
| binaryfp MirTypes.FSUB = "FSUB"
| binaryfp MirTypes.FMUL = "FMUL"
| binaryfp MirTypes.FDIV = "FDIV"
fun tagged_binaryfp MirTypes.FADDV = "FADDV"
| tagged_binaryfp MirTypes.FSUBV = "FSUBV"
| tagged_binaryfp MirTypes.FMULV = "FMULV"
| tagged_binaryfp MirTypes.FDIVV = "FDIVV"
fun unaryfp MirTypes.FABS = "FABS"
| unaryfp MirTypes.FNEG = "FNEG"
| unaryfp MirTypes.FMOVE = "FMOVE"
| unaryfp MirTypes.FINT = "FINT"
| unaryfp MirTypes.FSQRT = "FSQRT"
| unaryfp MirTypes.FLOG10 = "FLOG10"
| unaryfp MirTypes.FLOG2 = "FLOG2"
| unaryfp MirTypes.FLOGE = "FLOGE"
| unaryfp MirTypes.FLOGEP1 = "FLOGEP1"
| unaryfp MirTypes.F10TOX = "F10TOX"
| unaryfp MirTypes.F2TOX = "F2TOX"
| unaryfp MirTypes.FETOX = "FETOX"
| unaryfp MirTypes.TETOXM1 = "TETOXM1"
| unaryfp MirTypes.FSIN = "FSIN"
| unaryfp MirTypes.FCOS = "FCOS"
| unaryfp MirTypes.FTAN = "FTAN"
| unaryfp MirTypes.FASIN = "FASIN"
| unaryfp MirTypes.FACOS = "FACOS"
| unaryfp MirTypes.FATAN = "FATAN"
fun tagged_unaryfp MirTypes.FABSV = "FABSV"
| tagged_unaryfp MirTypes.FNEGV = "FNEGV"
| tagged_unaryfp MirTypes.FSQRTV = "FSQRTV"
| tagged_unaryfp MirTypes.FLOGEV = "FLOGEV"
| tagged_unaryfp MirTypes.FETOXV = "FETOXV"
fun store MirTypes.LD = "LD"
| store MirTypes.ST = "ST"
| store MirTypes.LDB = "LDB"
| store MirTypes.STB = "STB"
| store MirTypes.LDREF = "LDREF"
| store MirTypes.STREF = "STREF"
fun storefp MirTypes.FLD = "FLD"
| storefp MirTypes.FST = "FST"
| storefp MirTypes.FSTREF = "FSTREF"
| storefp MirTypes.FLDREF = "FLDREF"
fun int_to_float MirTypes.ITOF = "ITOF"
fun float_to_int MirTypes.FTOI = "FTOI"
fun allocate MirTypes.ALLOC = "ALLOC"
| allocate MirTypes.ALLOC_VECTOR = "ALLOC_VECTOR"
| allocate MirTypes.ALLOC_REAL = "ALLOC_REAL"
| allocate MirTypes.ALLOC_STRING = "ALLOC_STRING"
| allocate MirTypes.ALLOC_REF = "ALLOC_REF"
| allocate MirTypes.ALLOC_BYTEARRAY = "ALLOC_BYTEARRAY"
fun allocate_stack operator =
"STACK_" ^ allocate operator
fun deallocate_stack operator =
"STACK_DE" ^ allocate operator
fun branch MirTypes.BRA = "BRA"
fun test MirTypes.BTA = "BTA"
| test MirTypes.BNT = "BNT"
| test MirTypes.BEQ = "BEQ"
| test MirTypes.BNE = "BNE"
| test MirTypes.BHI = "BHI"
| test MirTypes.BLS = "BLS"
| test MirTypes.BHS = "BHS"
| test MirTypes.BLO = "BLO"
| test MirTypes.BGT = "BGT"
| test MirTypes.BLE = "BLE"
| test MirTypes.BGE = "BGE"
| test MirTypes.BLT = "BLT"
fun ftest MirTypes.FBEQ = "FBEQ"
| ftest MirTypes.FBNE = "FBNE"
| ftest MirTypes.FBLE = "FBLE"
| ftest MirTypes.FBLT = "FBLT"
fun adr MirTypes.LEA = "LEA"
| adr MirTypes.LEO = "LEO"
fun stack_op MirTypes.PUSH = "PUSH"
| stack_op MirTypes.POP = "POP"
fun branch_and_link MirTypes.BLR = "BLR"
fun tail_call MirTypes.TAIL = "TAIL"
val show_register_names = ref true
val show_real_registers = ref true
local
open MirRegisters
in
fun gc_register gc_reg =
let
val name =
if !show_register_names then
if gc_reg = caller_arg then "/caller_argument"
else if gc_reg = callee_arg then "/argument"
else if gc_reg = caller_closure then "/caller_closure"
else if gc_reg = callee_closure then "/closure"
else if gc_reg = fp then "/frame"
else if gc_reg = sp then "/stack"
else if gc_reg = global then "/global"
else if gc_reg = implicit then "/implicit"
else
case MirRegisters.zero
of SOME zero => if gc_reg = zero then "/zero" else ""
| NONE => ""
else ""
val machine_reg =
if !show_real_registers then
("/" ^ MachSpec.print_register (MirTypes.GC.Map.apply (#gc machine_register_assignments) gc_reg))
handle MirTypes.GC.Map.Undefined => ""
else ""
in
MirTypes.GC.to_string gc_reg ^ name ^ machine_reg
end
fun non_gc_register non_gc_reg =
let
val machine_reg =
if !show_real_registers then
("/" ^ MachSpec.print_register (MirTypes.NonGC.Map.apply (#non_gc machine_register_assignments) non_gc_reg))
handle MirTypes.NonGC.Map.Undefined => ""
else ""
in
MirTypes.NonGC.to_string non_gc_reg ^ machine_reg
end
fun fp_register fp_reg =
let
val machine_reg =
if !show_real_registers then
("/" ^ MachSpec.print_register (MirTypes.FP.Map.apply (#fp machine_register_assignments) fp_reg))
handle MirTypes.FP.Map.Undefined => ""
else ""
in
MirTypes.FP.to_string fp_reg ^ machine_reg
end
end
fun reg_operand(MirTypes.GC_REG gc_reg) =
gc_register gc_reg
| reg_operand(MirTypes.NON_GC_REG non_gc_reg) =
non_gc_register non_gc_reg
fun fp_operand(MirTypes.FP_REG fp_reg) =
fp_register fp_reg
fun any_reg(MirTypes.GC gc_reg) =
gc_register gc_reg
| any_reg(MirTypes.NON_GC non_gc_reg) =
non_gc_register non_gc_reg
| any_reg(MirTypes.FLOAT fp_reg) =
fp_register fp_reg
fun offset2(ref(RuntimeEnv.OFFSET2(RuntimeEnv.GC, i)), name) =
"GC_SPILL_SLOT (mach_cg) (" ^ Int.toString i ^ ":" ^ name ^ ")"
| offset2(ref(RuntimeEnv.OFFSET2(RuntimeEnv.NONGC, i)), name) =
"NON_GC_SPILL_SLOT (mach_cg) (" ^ Int.toString i ^ ":" ^ name ^ ")"
| offset2(ref(RuntimeEnv.OFFSET2(RuntimeEnv.FP, i)), name) =
"FP_SPILL_SLOT (mach_cg) (" ^ Int.toString i ^ ":" ^ name ^ ")"
| offset2 _ = Crash.impossible "offset2:_mirprint.sml"
fun symb MirTypes.GC_SPILL_SIZE = "GC_SPILL_SIZE"
| symb MirTypes.NON_GC_SPILL_SIZE = "NON_GC_SPILL_SIZE"
| symb (MirTypes.GC_SPILL_SLOT (MirTypes.SIMPLE n)) =
"GC_SPILL_SLOT(" ^ Int.toString n ^ ")"
| symb (MirTypes.GC_SPILL_SLOT (MirTypes.DEBUG (ref (RuntimeEnv.OFFSET1(n)),name))) =
"GC_SPILL_SLOT(" ^ Int.toString n ^ ":"^name^")"
| symb (MirTypes.NON_GC_SPILL_SLOT (MirTypes.SIMPLE n)) =
"NON_GC_SPILL_SLOT(" ^ Int.toString n ^ ")"
| symb (MirTypes.NON_GC_SPILL_SLOT (MirTypes.DEBUG (ref (RuntimeEnv.OFFSET1(n)),name))) =
"NON_GC_SPILL_SLOT(" ^ Int.toString n ^ ":"^name^")"
| symb (MirTypes.FP_SPILL_SLOT (MirTypes.SIMPLE n)) =
"FP_SPILL_SLOT(" ^ Int.toString n ^ ")"
| symb (MirTypes.FP_SPILL_SLOT (MirTypes.DEBUG (ref (RuntimeEnv.OFFSET1(n)),name))) =
"FP_SPILL_SLOT(" ^ Int.toString n ^ ":"^name^")"
| symb(MirTypes.GC_SPILL_SLOT(MirTypes.DEBUG x)) = offset2 x
| symb(MirTypes.NON_GC_SPILL_SLOT(MirTypes.DEBUG x)) = offset2 x
| symb(MirTypes.FP_SPILL_SLOT(MirTypes.DEBUG x)) = offset2 x
fun gp_operand(MirTypes.GP_GC_REG gc_reg) =
gc_register gc_reg
| gp_operand(MirTypes.GP_NON_GC_REG non_gc_reg) =
non_gc_register non_gc_reg
| gp_operand(MirTypes.GP_IMM_INT imm) =
Int.toString imm
| gp_operand(MirTypes.GP_IMM_ANY imm) =
"Any:" ^ Int.toString imm
| gp_operand(MirTypes.GP_IMM_SYMB symbol) =
symb symbol
fun bl_dest(MirTypes.TAG tag) = MirTypes.print_tag tag
| bl_dest(MirTypes.REG reg) = reg_operand reg
fun cat x =
let
fun cat'([], done) = concat(rev done)
| cat'(s :: ss, done) = cat'(ss, " " :: s :: done)
in
cat'(x, [])
end
val tag = MirTypes.print_tag
fun opcode(MirTypes.BINARY(operator, reg, gp1, gp2)) =
cat [binary operator, reg_operand reg, gp_operand gp1, gp_operand gp2]
| opcode(MirTypes.TBINARY(operator, tag1, reg, gp1, gp2)) =
cat [tagged_binary operator, MirTypes.print_tag_list tag1, reg_operand reg,
gp_operand gp1, gp_operand gp2]
| opcode(MirTypes.UNARY(operator, reg, gp)) =
cat [unary operator, reg_operand reg, gp_operand gp]
| opcode(MirTypes.NULLARY(operator, reg)) =
cat [nullary operator, reg_operand reg]
| opcode(MirTypes.STOREOP(operator, reg1, reg2, gp)) =
cat [store operator, reg_operand reg1, reg_operand reg2, gp_operand gp]
| opcode(MirTypes.IMMSTOREOP(operator, gp1, reg, gp2)) =
cat [store operator, gp_operand gp1, reg_operand reg, gp_operand gp2]
| opcode(MirTypes.ALLOCATE(operator, reg1, imm)) =
cat [allocate operator, reg_operand reg1,
case operator of
MirTypes.ALLOC_REAL => ""
| _ => gp_operand imm]
| opcode(MirTypes.ALLOCATE_STACK(operator, reg1, imm, NONE)) =
cat [allocate_stack operator, reg_operand reg1,
case operator
of MirTypes.ALLOC_REAL => ""
| _ => Int.toString imm]
| opcode(MirTypes.ALLOCATE_STACK(operator, reg1, imm,
SOME offset)) =
cat [allocate_stack operator, reg_operand reg1,
case operator
of MirTypes.ALLOC_REAL => ""
| _ => Int.toString imm,
"OFFSET", Int.toString offset]
| opcode(MirTypes.DEALLOCATE_STACK(operator, imm)) =
cat [deallocate_stack operator, Int.toString imm]
| opcode(MirTypes.BRANCH(operator, bl_dest1)) =
cat [branch operator, bl_dest bl_dest1]
| opcode(MirTypes.TEST(operator, tag1, gp, gp')) =
cat [test operator, tag tag1, gp_operand gp, gp_operand gp']
| opcode(MirTypes.FTEST(operator, tag1, fp, fp')) =
cat [ftest operator, tag tag1, fp_operand fp, fp_operand fp']
| opcode(MirTypes.SWITCH(_, reg, tag_list)) =
cat (["CGT", reg_operand reg] @ map tag tag_list)
| opcode(MirTypes.ADR(operator, reg, tag1)) =
cat [adr operator, reg_operand reg, tag tag1]
| opcode MirTypes.INTERCEPT = "Intercept"
| opcode MirTypes.INTERRUPT = "Interrupt"
| opcode (MirTypes.ENTER _) = "Procedure entry"
| opcode MirTypes.RTS = "Procedure exit"
| opcode(MirTypes.NEW_HANDLER (stack, tag1)) =
"New exception handler at tag " ^ tag tag1
| opcode(MirTypes.OLD_HANDLER) =
"Restore previous exception handler"
| opcode(MirTypes.RAISE reg) =
"Raise " ^ reg_operand reg
| opcode(MirTypes.BINARYFP(operator, fp1, fp2, fp3)) =
cat [binaryfp operator, fp_operand fp1, fp_operand fp2,
fp_operand fp3]
| opcode(MirTypes.UNARYFP(operator, fp, fp')) =
cat [unaryfp operator, fp_operand fp, fp_operand fp']
| opcode(MirTypes.TBINARYFP(operator, tag, fp1, fp2, fp3)) =
cat [tagged_binaryfp operator, MirTypes.print_tag_list tag, fp_operand fp1,
fp_operand fp2, fp_operand fp3]
| opcode(MirTypes.TUNARYFP(operator, tag, fp, fp')) =
cat [tagged_unaryfp operator, MirTypes.print_tag_list tag, fp_operand fp,
fp_operand fp']
| opcode(MirTypes.STACKOP(operator, reg, NONE)) =
cat [stack_op operator, reg_operand reg]
| opcode (MirTypes.STACKOP(operator, reg, SOME offset)) =
cat [stack_op operator, reg_operand reg,
"OFFSET", Int.toString offset]
| opcode(MirTypes.STOREFPOP(operator, fp, reg, gp)) =
cat [storefp operator, fp_operand fp, reg_operand reg, gp_operand gp]
| opcode(MirTypes.REAL(operator, fp, gp)) =
cat [int_to_float operator, fp_operand fp, gp_operand gp]
| opcode(MirTypes.FLOOR(operator, tag, reg, fp)) =
cat [float_to_int operator, MirTypes.print_tag tag, reg_operand reg,
fp_operand fp]
| opcode(MirTypes.BRANCH_AND_LINK(operator, bl_dest1,_,_)) =
cat [branch_and_link operator, bl_dest bl_dest1]
| opcode(MirTypes.TAIL_CALL(operator, bl_dest1,_)) =
cat [tail_call operator, bl_dest bl_dest1]
| opcode MirTypes.CALL_C = "CALL_C"
| opcode(MirTypes.COMMENT s) = " ; " ^ s
local
fun any_block_list (name, indent) (MirTypes.BLOCK(tag, opcodes)) =
let
val newl = "\n " ^ indent
in
indent ^ name ^ " " ^ MirTypes.print_tag tag ::
map
(fn opc => newl ^ opcode opc)
opcodes
end
fun any_block(name, indent) (MirTypes.BLOCK(tag, opcodes)) =
concat(any_block_list (name, indent) (MirTypes.BLOCK(tag, opcodes)))
in
val block = any_block ("Block", "    ")
fun block_print(stream, MirTypes.BLOCK(tag, opcodes)) =
Lists.iterate
(fn x => TextIO.output(stream, x))
(any_block_list ("Block", "    ")
(MirTypes.BLOCK(tag, opcodes)))
end
fun procedure (MirTypes.PROC(name_string,
start_tag,
MirTypes.PROC_PARAMS {spill_sizes,
old_spill_sizes,
stack_allocated},
blocks,_)) =
let
val spill_string =
case spill_sizes
of NONE => "\n    No spill size information."
| SOME {gc, non_gc, fp} =>
("\n    Spill areas: GC " ^ Int.toString gc ^
", NON_GC " ^ Int.toString non_gc ^
", FP " ^ Int.toString fp)
val old_spill_string =
case old_spill_sizes
of NONE => "\n    No previous spill size information."
| SOME {gc, non_gc, fp} =>
("\n    Spill areas: GC " ^ Int.toString gc ^
", NON_GC " ^ Int.toString non_gc ^
", FP " ^ Int.toString fp)
val stack_string =
case stack_allocated
of NONE => "\n    No stack allocation information."
| SOME s =>
("\n    " ^ Int.toString s ^
" words of stack required.")
val message =
"   Procedure " ^ MirTypes.print_tag start_tag ^ " " ^ name_string ^
spill_string ^ old_spill_string ^ stack_string
in
Lists.reducel
(fn (a,b) => a ^ "\n" ^ b)
(message, map block blocks)
end
fun procedure_print(stream,
MirTypes.PROC(name_string,
start_tag,
MirTypes.PROC_PARAMS {spill_sizes,
old_spill_sizes,
stack_allocated},
blocks,_)) =
let
val spill_string =
case spill_sizes
of NONE => "\n    No spill size information."
| SOME {gc, non_gc, fp} =>
("\n    Spill areas: GC " ^ Int.toString gc ^
", NON_GC " ^ Int.toString non_gc ^
", FP " ^ Int.toString fp)
val old_spill_string =
case old_spill_sizes
of NONE => "\n    No previous spill size information."
| SOME {gc, non_gc, fp} =>
("\n    Spill areas: GC " ^ Int.toString gc ^
", NON_GC " ^ Int.toString non_gc ^
", FP " ^ Int.toString fp)
val stack_string =
case stack_allocated
of NONE => "\n    No stack allocation information."
| SOME s =>
("\n    " ^ Int.toString s ^
" words of stack required.")
in
TextIO.output(stream, "   Procedure ");
TextIO.output(stream, MirTypes.print_tag start_tag);
TextIO.output(stream, " ");
TextIO.output(stream, name_string);
TextIO.output(stream, " (");
TextIO.output(stream, spill_string);
TextIO.output(stream, old_spill_string);
TextIO.output(stream, stack_string);
Lists.iterate
(fn x => (TextIO.output(stream, "\n"); block_print(stream, x)))
blocks
end
val set_up = procedure
fun proc_set_print(stream, procs) =
(TextIO.output(stream, "  Procedure set { ");
Lists.iterate
(fn (MirTypes.PROC(_,tag,_,_,_)) =>
(TextIO.output(stream, MirTypes.print_tag tag); TextIO.output(stream, " ")))
procs;
TextIO.output(stream, "}");
Lists.iterate
(fn proc => (TextIO.output(stream, "\n"); procedure_print(stream, proc)))
procs
)
fun proc_set procs =
let
val proc_tags =
Lists.reducel
(fn (a,b) => a ^ " " ^ b)
("", map (fn MirTypes.PROC(_,tag,_,_,_) => MirTypes.print_tag tag) procs)
in
Lists.reducel
(fn (a,b) => a ^ "\n" ^ b)
("  Procedure set {" ^ proc_tags ^ " }",
map procedure procs)
end
fun refs (MirTypes.REFS(tags,
{requires, vars, exns, strs, funs})) =
"  References\n   Local " ^
Lists.to_string
(fn (x, i) => ("Tag " ^ MirTypes.print_tag x ^ ", position " ^
Int.toString i)) tags ^
"\n   External " ^
Lists.to_string
(fn (s, i) => s ^ ", position " ^ Int.toString i)
requires ^
"\n   Interpreter vars " ^
Lists.to_string
(fn (s, i) => s ^ ", position " ^ Int.toString i)
vars ^
"\n   Interpreter exns " ^
Lists.to_string
(fn (s, i) => s ^ ", position " ^ Int.toString i)
exns ^
"\n   Interpreter strs " ^
Lists.to_string
(fn (s, i) => s ^ ", position " ^ Int.toString i)
strs ^
"\n   Interpreter funs " ^
Lists.to_string
(fn (s, i) => s ^ ", position " ^ Int.toString i)
funs
fun value (MirTypes.VALUE(tag, value)) =
case value of
MirTypes.SCON scon =>
MirTypes.print_tag tag ^ ": " ^ IdentPrint.printSCon scon
| MirTypes.MLVALUE _ =>
MirTypes.print_tag tag ^ ": <ml_value>"
fun string_mir_code(MirTypes.CODE(the_refs, values, proc_sets)) =
"MIR code unit\n" ^
refs the_refs ^ "\n" ^
(Lists.reducel
(fn (a,b) => a ^ "\n  " ^ b)
(" Values", map value values)) ^ "\n" ^
(Lists.reducel
(fn (a,b) => a ^ "\n" ^ b)
(" Procedure sets", map proc_set proc_sets)) ^ "\n"
fun print_mir_code (MirTypes.CODE(the_refs, values, proc_sets)) stream =
(TextIO.output(stream, "MIR code unit\n");
TextIO.output(stream, refs the_refs);
TextIO.output(stream, "\n Values\n");
Lists.iterate
(fn x => (TextIO.output(stream, value x); TextIO.output(stream, "\n")))
values;
TextIO.output(stream, " Procedure sets\n");
Lists.iterate
(fn x => (proc_set_print(stream, x); TextIO.output(stream, "\n")))
proc_sets)
val binary_op = binary
val unary_op = unary
val binary_fp_op = binaryfp
val unary_fp_op = unaryfp
val store_op = store
val store_fp_op = storefp
end
;
