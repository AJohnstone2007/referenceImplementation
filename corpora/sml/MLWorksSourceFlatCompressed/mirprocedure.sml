require "../utils/text";
require "../utils/diagnostic";
require "mirtypes";
signature MIRPROCEDURE =
sig
structure MirTypes : MIRTYPES
structure Diagnostic : DIAGNOSTIC
structure Text : TEXT
val empty : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val empty_set : {gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T}
val equal : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} -> bool
val is_empty : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} -> bool
val union : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val union' : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val pack_set_union' :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val pack_set_difference' :
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val set_pack_disjoint :
{gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
bool
val intersection : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val intersection' : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val difference : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val difference' : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} *
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val pack : {gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val unpack : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Set.T,
non_gc : MirTypes.NonGC.Set.T,
fp : MirTypes.FP.Set.T}
val copy' : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T} ->
{gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T}
val substitute :
{gc : MirTypes.GC.T -> MirTypes.GC.T,
non_gc : MirTypes.NonGC.T -> MirTypes.NonGC.T,
fp : MirTypes.FP.T -> MirTypes.FP.T} ->
MirTypes.opcode -> MirTypes.opcode
datatype instruction =
I of {defined : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T},
referenced : {gc : MirTypes.GC.Pack.T,
non_gc : MirTypes.NonGC.Pack.T,
fp : MirTypes.FP.Pack.T},
branches : MirTypes.tag MirTypes.Set.Set,
excepts : MirTypes.tag list,
opcode : MirTypes.opcode}
datatype block =
B of {reached : MirTypes.tag MirTypes.Set.Set,
excepts : MirTypes.tag list,
length : int} *
instruction list
datatype procedure =
P of {uses_stack : bool,
nr_registers : {gc : int, non_gc : int, fp : int},
parameters : MirTypes.procedure_parameters} *
string * MirTypes.tag * (block) MirTypes.Map.T
val annotate : MirTypes.procedure -> procedure
val unannotate : procedure -> MirTypes.procedure
val to_text : procedure -> Text.T
end
;
