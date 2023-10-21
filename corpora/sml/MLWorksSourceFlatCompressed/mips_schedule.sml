require "mips_assembly";
signature MIPS_SCHEDULE = sig
structure Mips_Assembly : MIPS_ASSEMBLY
val reschedule_proc :
bool ->
(Mips_Assembly.MirTypes.tag *
(Mips_Assembly.MirTypes.tag *
(Mips_Assembly.opcode * Mips_Assembly.MirTypes.tag option * string) list) list)
->
(Mips_Assembly.MirTypes.tag *
(Mips_Assembly.MirTypes.tag *
(Mips_Assembly.opcode * Mips_Assembly.MirTypes.tag option * string) list) list)
end
;
