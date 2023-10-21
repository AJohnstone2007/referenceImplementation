require "../basis/__text_io";
require "mirtypes";
signature MIRPRINT = sig
structure MirTypes : MIRTYPES
val show_register_names : bool ref
val show_real_registers : bool ref
val string_mir_code : MirTypes.mir_code -> string
val print_mir_code : MirTypes.mir_code -> TextIO.outstream -> unit
val procedure : MirTypes.procedure -> string
val block : MirTypes.block -> string
val opcode : MirTypes.opcode -> string
val binary_op : MirTypes.binary_op -> string
val unary_op : MirTypes.unary_op -> string
val binary_fp_op : MirTypes.binary_fp_op -> string
val unary_fp_op : MirTypes.unary_fp_op -> string
val store_op : MirTypes.store_op -> string
val store_fp_op : MirTypes.store_fp_op -> string
val gp_operand : MirTypes.gp_operand -> string
val reg_operand : MirTypes.reg_operand -> string
val fp_operand : MirTypes.fp_operand -> string
val any_reg : MirTypes.any_register -> string
val gc_register : MirTypes.GC.T -> string
val non_gc_register : MirTypes.NonGC.T -> string
val fp_register : MirTypes.FP.T -> string
end
;
