require "$.basis.__string";
require "../utils/crash";
require "machtypes";
require "mips_opcodes";
functor Mips_Opcodes(
structure Crash : CRASH
structure MachTypes : MACHTYPES
) : MIPS_OPCODES =
struct
structure Bits = MLWorks.Internal.Bits
structure MachTypes = MachTypes
exception not_done_yet
datatype opcode =
FORMATI of int * MachTypes.Mips_Reg * MachTypes.Mips_Reg *
int
| FORMATI2 of int * int * int * int
| FORMATJ of int * int
| FORMATR of int * MachTypes.Mips_Reg * MachTypes.Mips_Reg *
MachTypes.Mips_Reg * int * int
| FORMATR2 of int * int * int * int * int * int
| OFFSET of int
fun register_val MachTypes.R0 = 0
| register_val MachTypes.R1 = 1
| register_val MachTypes.R2 = 2
| register_val MachTypes.R3 = 3
| register_val MachTypes.R4 = 4
| register_val MachTypes.R5 = 5
| register_val MachTypes.R6 = 6
| register_val MachTypes.R7 = 7
| register_val MachTypes.R8 = 8
| register_val MachTypes.R9 = 9
| register_val MachTypes.R10 = 10
| register_val MachTypes.R11 = 11
| register_val MachTypes.R12 = 12
| register_val MachTypes.R13 = 13
| register_val MachTypes.R14 = 14
| register_val MachTypes.R15 = 15
| register_val MachTypes.R16 = 16
| register_val MachTypes.R17 = 17
| register_val MachTypes.R18 = 18
| register_val MachTypes.R19 = 19
| register_val MachTypes.R20 = 20
| register_val MachTypes.R21 = 21
| register_val MachTypes.R22 = 22
| register_val MachTypes.R23 = 23
| register_val MachTypes.R24 = 24
| register_val MachTypes.R25 = 25
| register_val MachTypes.R26 = 26
| register_val MachTypes.R27 = 27
| register_val MachTypes.R28 = 28
| register_val MachTypes.R29 = 29
| register_val MachTypes.R30 = 30
| register_val MachTypes.R31 = 31
| register_val MachTypes.cond = Crash.impossible"register_val cond"
| register_val MachTypes.heap = Crash.impossible"register_val heap"
| register_val MachTypes.stack = Crash.impossible"register_val stack"
| register_val MachTypes.mult_result = Crash.impossible"register_val mult_result"
| register_val MachTypes.nil_v = Crash.impossible"register_val nil_v"
fun bool_val false = 0
| bool_val true = 1
fun make_list(bytes, value, acc) =
if bytes <= 0 then acc
else
make_list(bytes-1, value div 256, String.str(chr(value mod 256)) :: acc)
fun output_int(bytes, value) =
make_list(bytes, value, [])
fun output_opcode(FORMATI(op1, rs, rt, imm16)) =
concat
(output_int(2, Bits.lshift(op1,10) + Bits.lshift(register_val rs,5) + register_val rt) @
output_int(2, imm16))
| output_opcode(FORMATI2(op1, rs, rt, imm16)) =
concat
(output_int(2, Bits.lshift(op1,10) + Bits.lshift(rs,5) + rt) @
output_int(2, imm16))
| output_opcode(FORMATJ(op1, target26)) =
let
val top = Bits.andb(Bits.rshift(target26, 24), 4)
val bottom3 = Bits.andb(target26, (256*256*256)-1)
in
concat
(String.str(chr(Bits.lshift(op1,2) + top)) ::
output_int(3, bottom3))
end
| output_opcode(FORMATR(op1, rs, rt, rd, shamt, funct)) =
concat
(output_int(2, Bits.lshift(op1,10) + Bits.lshift(register_val rs,5) + register_val rt) @
output_int(2, Bits.lshift(register_val rd,11) + Bits.lshift(Bits.andb (shamt,31),6) + funct))
| output_opcode(FORMATR2(op1, fmt, ft, fs, fd, funct)) =
concat
(output_int(2, Bits.lshift(op1,10) + Bits.lshift(fmt,5) + ft) @
output_int(2, Bits.lshift(fs, 11) + Bits.lshift(fd, 6) + funct))
| output_opcode(OFFSET i) =
concat(output_int(4, i))
end
;
