signature MACHTYPES = sig
datatype Sparc_Reg =
I0 |
I1 |
I2 |
I3 |
I4 |
I5 |
I6 |
I7 |
L0 |
L1 |
L2 |
L3 |
L4 |
L5 |
L6 |
L7 |
O0 |
O1 |
O2 |
O3 |
O4 |
O5 |
O6 |
O7 |
G0 |
G1 |
G2 |
G3 |
G4 |
G5 |
G6 |
G7 |
cond |
heap |
stack |
y_reg |
nil_v
val F0 : Sparc_Reg
val F1 : Sparc_Reg
val F2 : Sparc_Reg
val F3 : Sparc_Reg
val F4 : Sparc_Reg
val F5 : Sparc_Reg
val F6 : Sparc_Reg
val F7 : Sparc_Reg
val F8 : Sparc_Reg
val F9 : Sparc_Reg
val F10 : Sparc_Reg
val F11 : Sparc_Reg
val F12 : Sparc_Reg
val F13 : Sparc_Reg
val F14 : Sparc_Reg
val F15 : Sparc_Reg
val F16 : Sparc_Reg
val F17 : Sparc_Reg
val F18 : Sparc_Reg
val F19 : Sparc_Reg
val F20 : Sparc_Reg
val F21 : Sparc_Reg
val F22 : Sparc_Reg
val F23 : Sparc_Reg
val F24 : Sparc_Reg
val F25 : Sparc_Reg
val F26 : Sparc_Reg
val F27 : Sparc_Reg
val F28 : Sparc_Reg
val F29 : Sparc_Reg
val F30 : Sparc_Reg
val F31 : Sparc_Reg
val next_reg : Sparc_Reg -> Sparc_Reg
datatype fp_type = single | double | extended
val fp_used : fp_type
val reg_to_string : Sparc_Reg -> string
val fp_reg_to_string : Sparc_Reg -> string
val digits_in_real : int
val bits_per_word : int
val caller_arg : Sparc_Reg
val callee_arg : Sparc_Reg
val caller_arg_regs : Sparc_Reg list
val callee_arg_regs : Sparc_Reg list
val caller_closure : Sparc_Reg
val callee_closure : Sparc_Reg
val fp : Sparc_Reg
val sp : Sparc_Reg
val lr : Sparc_Reg
val handler : Sparc_Reg
val global : Sparc_Reg
val fp_global : Sparc_Reg
val gc1 : Sparc_Reg
val gc2 : Sparc_Reg
val implicit : Sparc_Reg
val stack_limit : Sparc_Reg
exception OutOfScope of Sparc_Reg
val after_preserve : Sparc_Reg -> Sparc_Reg
val after_restore : Sparc_Reg -> Sparc_Reg
exception Ord
exception Chr
val ord: string -> int
val chr: int -> string
exception NeedsPreserve
val check_reg : Sparc_Reg -> unit
end
;
