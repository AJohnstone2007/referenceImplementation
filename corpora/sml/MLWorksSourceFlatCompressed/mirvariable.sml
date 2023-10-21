require "../utils/diagnostic";
require "registerallocator";
signature MIRVARIABLE =
sig
structure RegisterAllocator : REGISTERALLOCATOR
structure Diagnostic : DIAGNOSTIC
val eliminate : bool ref
val analyse :
RegisterAllocator.MirProcedure.procedure * RegisterAllocator.Graph ->
RegisterAllocator.MirProcedure.procedure
end
;
