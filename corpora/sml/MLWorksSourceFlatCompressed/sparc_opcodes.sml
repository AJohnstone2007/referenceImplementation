require "machtypes";
signature SPARC_OPCODES = sig
structure MachTypes : MACHTYPES
exception not_done_yet
datatype opcode =
FORMAT1 of int
| FORMAT2A of MachTypes.Sparc_Reg * int * int
| FORMAT2B of bool * int * int * int
| FORMAT3A of int * MachTypes.Sparc_Reg * int *
MachTypes.Sparc_Reg * bool * int *
MachTypes.Sparc_Reg
| FORMAT3B of int * MachTypes.Sparc_Reg * int *
MachTypes.Sparc_Reg * bool * int
| FORMAT3C of int * MachTypes.Sparc_Reg * int *
MachTypes.Sparc_Reg * int * MachTypes.Sparc_Reg
val output_opcode : opcode -> string
end
;
