require "../utils/crash";
require "i386types";
require "i386_opcodes";
functor I386_Opcodes(
structure Crash : CRASH
structure I386Types : I386TYPES
) : I386_OPCODES =
struct
structure I386Types = I386Types
datatype opcode = OPCODE of int list
fun make_list(bytes, value, acc) =
if bytes <= 0 then acc
else
make_list(bytes-1, value div 256, chr(value mod 256) :: acc)
fun output_int(bytes, value) =
make_list(bytes, value, [])
fun output_opcode(OPCODE i) =
implode (map chr i)
end
;
