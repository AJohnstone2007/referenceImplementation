require "sparc_assembly";
signature SPARC_SCHEDULE = sig
structure Sparc_Assembly : SPARC_ASSEMBLY
val reschedule_block :
bool *
(Sparc_Assembly.opcode * Sparc_Assembly.MirTypes.tag option * string) list ->
(Sparc_Assembly.opcode * Sparc_Assembly.MirTypes.tag option * string) list
val reschedule_proc :
(Sparc_Assembly.MirTypes.tag *
(Sparc_Assembly.MirTypes.tag *
(Sparc_Assembly.opcode * Sparc_Assembly.MirTypes.tag option * string) list) list)
->
(Sparc_Assembly.MirTypes.tag *
(Sparc_Assembly.MirTypes.tag *
(Sparc_Assembly.opcode * Sparc_Assembly.MirTypes.tag option * string) list) list)
end
;
