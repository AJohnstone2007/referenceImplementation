require "../utils/diagnostic";
require "mirprocedure";
signature REGISTERALLOCATOR =
sig
structure MirProcedure : MIRPROCEDURE
structure Diagnostic : DIAGNOSTIC
type Graph
val empty : {gc : int, non_gc : int, fp : int} * bool -> Graph
val clash :
Graph ->
{gc : MirProcedure.MirTypes.GC.Pack.T,
non_gc : MirProcedure.MirTypes.NonGC.Pack.T,
fp : MirProcedure.MirTypes.FP.Pack.T} *
{gc : MirProcedure.MirTypes.GC.Pack.T,
non_gc : MirProcedure.MirTypes.NonGC.Pack.T,
fp : MirProcedure.MirTypes.FP.Pack.T} *
{gc : MirProcedure.MirTypes.GC.Pack.T,
non_gc : MirProcedure.MirTypes.NonGC.Pack.T,
fp : MirProcedure.MirTypes.FP.Pack.T} ->
unit
val analyse :
MirProcedure.procedure * Graph * {fp:int,gc:int,non_gc:int} * bool ->
MirProcedure.procedure
end
;
