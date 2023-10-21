require "^.basis.__text_io";
signature MACHPRINT = sig
type Opcode
val print_mach_code :
(('a * (Opcode * string) list) * string) list list ->
TextIO.outstream ->
unit
end
;
