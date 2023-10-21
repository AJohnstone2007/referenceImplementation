require "../utils/lists";
require "../utils/crash";
require "mirregisters";
require "mirtables";
functor MirTables (
structure MirRegisters: MIRREGISTERS
structure Lists : LISTS
structure Crash : CRASH
) : MIRTABLES =
struct
structure MirTypes = MirRegisters.MirTypes
local
open MirTypes
fun pack {gc, non_gc, fp} =
{gc = MirTypes.GC.pack_set gc,
non_gc = MirTypes.NonGC.pack_set non_gc,
fp = MirTypes.FP.pack_set fp}
fun unpack {gc, non_gc, fp} =
{gc = MirTypes.GC.unpack_set gc,
non_gc = MirTypes.NonGC.unpack_set non_gc,
fp = MirTypes.FP.unpack_set fp}
val caller_arg = MirRegisters.caller_arg
val callee_arg = MirRegisters.callee_arg
val caller_closure = MirRegisters.caller_closure
val callee_closure = MirRegisters.callee_closure
val stack = MirRegisters.sp
val frame = MirRegisters.fp
val global = MirRegisters.global
fun reg ({gc, non_gc, fp}, GC_REG r) =
{gc = GC.Set.add (gc, r), non_gc = non_gc, fp = fp}
| reg ({gc, non_gc, fp}, NON_GC_REG r) =
{gc = gc, non_gc = NonGC.Set.add (non_gc, r), fp = fp}
fun single_reg (GC_REG r) =
{gc = GC.Set.singleton r, non_gc = NonGC.Set.empty, fp = FP.Set.empty}
| single_reg (NON_GC_REG r) =
{gc = GC.Set.empty, non_gc = NonGC.Set.singleton r, fp = FP.Set.empty}
fun gp ({gc, non_gc, fp}, GP_GC_REG r) =
{gc = GC.Set.add (gc, r), non_gc = non_gc, fp = fp}
| gp ({gc, non_gc, fp}, GP_NON_GC_REG r) =
{gc = gc, non_gc = NonGC.Set.add (non_gc, r), fp = fp}
| gp (sets, _) = sets
fun fp ({gc, non_gc, fp}, FP_REG r) =
{gc = gc, non_gc = non_gc, fp = FP.Set.add (fp, r)}
fun single_fp (FP_REG r) =
{gc = GC.Set.empty, non_gc = NonGC.Set.empty, fp = FP.Set.singleton r}
val empty = {gc = GC.Set.empty, non_gc = NonGC.Set.empty, fp = FP.Set.empty}
val defined_by_call = reg (unpack MirRegisters.corrupted_by_callee, GC_REG caller_arg)
val defined_by_alloc = unpack MirRegisters.corrupted_by_alloc
val referenced_by_alloc = unpack MirRegisters.referenced_by_alloc
fun merge_any_registers ([],acc) = acc
| merge_any_registers (GC reg::rest, {gc,non_gc,fp}) =
merge_any_registers (rest,{gc=GC.Set.add (gc,reg),non_gc=non_gc,fp=fp})
| merge_any_registers (NON_GC reg::rest, {gc,non_gc,fp}) =
merge_any_registers (rest,{gc=gc,non_gc=NonGC.Set.add (non_gc,reg),fp=fp})
| merge_any_registers (FLOAT reg::rest, {gc,non_gc,fp}) =
merge_any_registers (rest,{gc=gc,non_gc=non_gc,fp=FP.Set.add (fp,reg)})
val always_referenced_by_call =
{gc = GC.Set.from_list [caller_closure, stack, frame],
non_gc = NonGC.Set.empty,
fp = FP.Set.empty}
fun referenced_by_call regs =
merge_any_registers (regs,always_referenced_by_call)
val always_referenced_by_tail =
{gc = GC.Set.from_list [MirRegisters.tail_closure,
stack, frame],
non_gc = NonGC.Set.empty,
fp = FP.Set.empty}
fun referenced_by_tail regs =
merge_any_registers (regs,always_referenced_by_tail)
val always_defined_on_entry = unpack MirRegisters.defined_on_entry
fun defined_on_entry regs =
merge_any_registers (regs,always_defined_on_entry)
val defined_on_exit = unpack MirRegisters.defined_on_exit
in
fun defined_by (ENTER regs) = defined_on_entry regs
| defined_by INTERCEPT = single_reg (GC_REG callee_arg)
| defined_by INTERRUPT = empty
| defined_by (UNARY(_, reg1, _)) = single_reg reg1
| defined_by (NULLARY(_, reg1)) = single_reg reg1
| defined_by (BINARY(_, reg1, _, _)) = single_reg reg1
| defined_by (TBINARY(_, _, reg1, _, _)) = single_reg reg1
| defined_by (BINARYFP(_, fp1, _, _)) = single_fp fp1
| defined_by (TBINARYFP(_, _, fp1, _, _)) = single_fp fp1
| defined_by (UNARYFP(_, fp1, _)) = single_fp fp1
| defined_by (TUNARYFP(_, _, fp1, _)) = single_fp fp1
| defined_by (STACKOP(POP, reg1, _)) = single_reg reg1
| defined_by (STACKOP(PUSH, _, _)) = empty
| defined_by (STOREOP(LD, reg1, _, _)) = single_reg reg1
| defined_by (STOREOP(ST, _, _, _)) = empty
| defined_by (IMMSTOREOP(ST, _, _, _)) = empty
| defined_by (STOREFPOP(FLD, fp1, _, _)) = single_fp fp1
| defined_by (STOREFPOP(FST, _, _, _)) = empty
| defined_by (STOREFPOP(FLDREF, fp1, _, _)) = single_fp fp1
| defined_by (STOREFPOP(FSTREF, _, _, _)) = empty
| defined_by (STOREOP(LDREF, reg1, _, _)) = single_reg reg1
| defined_by (STOREOP(STREF, _, _, _)) = empty
| defined_by (IMMSTOREOP(STREF, _, _, _)) = empty
| defined_by (STOREOP(LDB, reg1, _, _)) = single_reg reg1
| defined_by (STOREOP(STB, _, _, _)) = empty
| defined_by (IMMSTOREOP(STB, _, _, _)) = empty
| defined_by (IMMSTOREOP _) =
Crash.impossible"STORE immediate without a store"
| defined_by (REAL(ITOF, fp1, _)) = single_fp fp1
| defined_by (FLOOR(FTOI, _, reg1, _)) = single_reg reg1
| defined_by (ALLOCATE(_, reg1,gp1)) = reg (reg (gp (defined_by_alloc, gp1), GC_REG global), reg1)
| defined_by (ALLOCATE_STACK(_, reg1, _, _)) = single_reg reg1
| defined_by (ADR(_, reg1, _)) = reg(defined_by_alloc, reg1)
| defined_by (BRANCH_AND_LINK _) = defined_by_call
| defined_by CALL_C = defined_by_call
| defined_by (BRANCH _) = empty
| defined_by (TEST _) = empty
| defined_by (FTEST _) = empty
| defined_by (TAIL_CALL _) = empty
| defined_by (SWITCH _) = defined_by_alloc
| defined_by (DEALLOCATE_STACK _) = empty
| defined_by RTS = empty
| defined_by (NEW_HANDLER _) = empty
| defined_by OLD_HANDLER = empty
| defined_by (RAISE _) = defined_by_call
| defined_by (COMMENT _) = empty
fun referenced_by RTS = defined_on_exit
| referenced_by (BRANCH_AND_LINK(_,REG reg1,_,args))= reg (referenced_by_call args, reg1)
| referenced_by (BRANCH_AND_LINK(_,_,_,args)) = referenced_by_call args
| referenced_by (TAIL_CALL(_, REG reg1,args)) = reg (referenced_by_tail args, reg1)
| referenced_by (TAIL_CALL (_,_,args)) = referenced_by_tail args
| referenced_by (UNARY(_, _, gp2)) = gp (empty, gp2)
| referenced_by (UNARYFP(_, _, fp2)) = single_fp fp2
| referenced_by (TUNARYFP(_, _, _, fp2)) = single_fp fp2
| referenced_by (BINARY(_, _, gp2, gp3)) = gp (gp (empty, gp2), gp3)
| referenced_by (BINARYFP(_, _, fp2, fp3)) = fp (single_fp fp2, fp3)
| referenced_by (TBINARYFP(_, _, _, fp2, fp3)) = fp (single_fp fp2, fp3)
| referenced_by (TBINARY(_, _, _, gp2, gp3)) = gp (gp (empty, gp2), gp3)
| referenced_by (STACKOP(PUSH, reg1, _)) = single_reg reg1
| referenced_by (STACKOP(POP, _, _)) = empty
| referenced_by (STOREOP(ST, reg1, reg2, gp3)) = gp (reg (single_reg reg1, reg2), gp3)
| referenced_by (IMMSTOREOP(ST, gp1, reg2, gp3)) = gp (reg (gp(empty, gp1), reg2), gp3)
| referenced_by (STOREOP(LD, _, reg2, gp3)) = gp (single_reg reg2, gp3)
| referenced_by (STOREOP(STREF, reg1, reg2, gp3)) = gp (reg (single_reg reg1, reg2), gp3)
| referenced_by (IMMSTOREOP(STREF, gp1, reg2, gp3)) = gp (reg (gp(empty, gp1), reg2), gp3)
| referenced_by (STOREOP(LDREF, _, reg2, gp3)) = gp (single_reg reg2, gp3)
| referenced_by (STOREOP(STB, reg1, reg2, gp3)) = gp (reg (single_reg reg1, reg2), gp3)
| referenced_by (IMMSTOREOP(STB, gp1, reg2, gp3)) = gp (reg (gp(empty, gp1), reg2), gp3)
| referenced_by (STOREOP(LDB, _, reg2, gp3)) = gp (single_reg reg2, gp3)
| referenced_by (IMMSTOREOP _) =
Crash.impossible"STORE immediate without a store"
| referenced_by (STOREFPOP(FST, fp1, reg2, gp3)) = gp (fp (single_reg reg2, fp1), gp3)
| referenced_by (STOREFPOP(FSTREF, fp1, reg2, gp3)) = gp (fp (single_reg reg2, fp1), gp3)
| referenced_by (STOREFPOP(FLD, _, reg2, gp3)) = gp (single_reg reg2, gp3)
| referenced_by (STOREFPOP(FLDREF, _, reg2, gp3)) = gp (single_reg reg2, gp3)
| referenced_by (REAL(ITOF, _, gp2)) = gp (empty, gp2)
| referenced_by (FLOOR(FTOI, _, _, fp1)) = single_fp fp1
| referenced_by (BRANCH(_, REG reg1)) = single_reg reg1
| referenced_by (BRANCH _) = empty
| referenced_by (TEST(_, _, gp1, gp2)) = gp (gp (empty, gp2), gp1)
| referenced_by (FTEST(_, _, fp1, fp2)) = fp (single_fp fp1, fp2)
| referenced_by (SWITCH(_, reg1, _)) = single_reg reg1
| referenced_by (COMMENT _) = empty
| referenced_by (ENTER _) = empty
| referenced_by INTERCEPT = single_reg (GC_REG callee_arg)
| referenced_by INTERRUPT = empty
| referenced_by (NEW_HANDLER(frame, _)) =
single_reg frame
| referenced_by OLD_HANDLER = empty
| referenced_by (ALLOCATE (_, _, gp1)) = gp (referenced_by_alloc, gp1)
| referenced_by (ALLOCATE_STACK _) = empty
| referenced_by (DEALLOCATE_STACK _) = empty
| referenced_by (ADR _) = empty
| referenced_by (NULLARY _) = empty
| referenced_by CALL_C =
{gc = GC.Set.from_list [caller_arg, stack, frame],
non_gc = NonGC.Set.empty,
fp = FP.Set.empty}
| referenced_by (RAISE reg1) =
reg ({gc = GC.Set.from_list [stack, frame],
non_gc = NonGC.Set.empty,
fp = FP.Set.empty}, reg1)
end
local
open MirTypes
in
fun has_side_effects (BINARY _) = false
| has_side_effects (UNARY _) = false
| has_side_effects (BINARYFP _) = false
| has_side_effects (UNARYFP _) = false
| has_side_effects (STOREOP(LD, _, _, _)) = false
| has_side_effects (STOREOP(LDB, _, _, _)) = false
| has_side_effects (STOREOP(LDREF, _, _, _)) = false
| has_side_effects (REAL _) = false
| has_side_effects (ALLOCATE _) = false
| has_side_effects (ALLOCATE_STACK _) = false
| has_side_effects (ADR _) = false
| has_side_effects _ = true
end
end
;
