require "i386_assembly";
signature I386_SCHEDULE = sig
structure I386_Assembly : I386_ASSEMBLY
val reschedule_block :
(I386_Assembly.opcode * I386_Assembly.tag option * string) list ->
(I386_Assembly.opcode * I386_Assembly.tag option * string) list
val reschedule_proc :
(I386_Assembly.tag *
(I386_Assembly.tag *
(I386_Assembly.opcode * I386_Assembly.tag option * string) list) list)
->
(I386_Assembly.tag *
(I386_Assembly.tag *
(I386_Assembly.opcode * I386_Assembly.tag option * string) list) list)
end
;
