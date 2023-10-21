require "machtypes";
signature MIPS_OPCODES = sig
structure MachTypes : MACHTYPES
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
val register_val : MachTypes.Mips_Reg -> int;
val output_opcode : opcode -> string
end
;
