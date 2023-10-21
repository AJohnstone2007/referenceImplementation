signature I386TYPES = sig
datatype I386_Reg =
EAX |
EBX |
ECX |
EDX |
ESP |
EBP |
EDI |
ESI |
AX |
BX |
CX |
DX |
SP |
BP |
DI |
SI |
AH |
AL |
BH |
BL |
CH |
CL |
DH |
DL |
cond |
heap |
stack |
nil_v |
i_arg1 |
i_arg2 |
i_arg3 |
i_arg4 |
i_arg5 |
i_arg6 |
i_arg7 |
o_arg1 |
o_arg2 |
o_arg3 |
o_arg4 |
o_arg5 |
o_arg6 |
o_arg7
val byte_reg_name : I386_Reg -> I386_Reg
val has_byte_name : I386_Reg -> bool
val half_reg_name : I386_Reg -> I386_Reg
val full_reg_name : I386_Reg -> I386_Reg
val register_value : I386_Reg -> int
datatype fp_type = single | double | extended
val fp_used : fp_type
val reg_to_string : I386_Reg -> string
val fp_reg_to_string : I386_Reg -> string
val digits_in_real : int
val bits_per_word : int
val caller_arg : I386_Reg
val callee_arg : I386_Reg
val caller_closure : I386_Reg
val callee_closure : I386_Reg
val sp : I386_Reg
val global : I386_Reg
val implicit : I386_Reg
exception Ord
exception Chr
val ord: string -> int
val chr: int -> string
end
;
