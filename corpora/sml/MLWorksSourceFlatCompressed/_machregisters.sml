require "../utils/set";
require "machtypes";
require "machregisters";
functor MachRegisters (
structure MachTypes : MACHTYPES
structure Set : SET
) : MACHREGISTERS =
struct
structure Set = MachTypes.Set
type T = MachTypes.Mips_Reg
val gcs = MachTypes.gc_registers
val non_gcs = MachTypes.non_gc_registers
val fps = MachTypes.fp_registers
val fn_arg = MachTypes.fn_arg
val cl_arg = MachTypes.cl_arg
val fp = MachTypes.fp
val sp = MachTypes.sp
val lr = MachTypes.lr
val handler = MachTypes.handler
val global = MachTypes.global
val gc1 = MachTypes.gc1
val gc2 = MachTypes.gc2
val after_preserve = MachTypes.after_preserve
val after_restore = MachTypes.after_restore
end
;
