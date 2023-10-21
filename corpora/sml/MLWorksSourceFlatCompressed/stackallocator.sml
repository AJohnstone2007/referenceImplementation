require "../utils/diagnostic";
require "mirprocedure";
signature STACKALLOCATOR =
sig
structure MirProcedure : MIRPROCEDURE
structure Diagnostic : DIAGNOSTIC
val allocate : MirProcedure.procedure -> MirProcedure.procedure
end
;
