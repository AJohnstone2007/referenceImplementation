require "$.basis.__string";
require "../utils/crash";
require "machtypes";
functor MachTypes(
structure Crash : CRASH
) : MACHTYPES =
struct
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
val F0 = G0
val F1 = G1
val F2 = G2
val F3 = G3
val F4 = G4
val F5 = G5
val F6 = G6
val F7 = G7
val F8 = O0
val F9 = O1
val F10 = O2
val F11 = O3
val F12 = O4
val F13 = O5
val F14 = O6
val F15 = O7
val F16 = L0
val F17 = L1
val F18 = L2
val F19 = L3
val F20 = L4
val F21 = L5
val F22 = L6
val F23 = L7
val F24 = I0
val F25 = I1
val F26 = I2
val F27 = I3
val F28 = I4
val F29 = I5
val F30 = I6
val F31 = I7
fun next_reg I0 = I1
| next_reg I1 = I2
| next_reg I2 = I3
| next_reg I3 = I4
| next_reg I4 = I5
| next_reg I5 = I6
| next_reg I6 = I7
| next_reg I7 = Crash.impossible"Next reg of register 31"
| next_reg L0 = L1
| next_reg L1 = L2
| next_reg L2 = L3
| next_reg L3 = L4
| next_reg L4 = L5
| next_reg L5 = L6
| next_reg L6 = L7
| next_reg L7 = I0
| next_reg O0 = O1
| next_reg O1 = O2
| next_reg O2 = O3
| next_reg O3 = O4
| next_reg O4 = O5
| next_reg O5 = O6
| next_reg O6 = O7
| next_reg O7 = L0
| next_reg G0 = G1
| next_reg G1 = G2
| next_reg G2 = G3
| next_reg G3 = G4
| next_reg G4 = G5
| next_reg G5 = G6
| next_reg G6 = G7
| next_reg G7 = O0
| next_reg cond = Crash.impossible"next_reg cond"
| next_reg heap = Crash.impossible"next_reg heap"
| next_reg stack = Crash.impossible"next_reg stack"
| next_reg y_reg = Crash.impossible"next_reg y_reg"
| next_reg nil_v = Crash.impossible"next_reg nil_v"
datatype fp_type = single | double | extended
val fp_used = double
fun reg_to_string I0 = "carg"
| reg_to_string I1 = "cclos"
| reg_to_string I2 = "i2"
| reg_to_string I3 = "i3"
| reg_to_string I4 = "i4"
| reg_to_string I5 = "i5"
| reg_to_string I6 = "fp"
| reg_to_string I7 = "i7"
| reg_to_string L0 = "l0"
| reg_to_string L1 = "l1"
| reg_to_string L2 = "l2"
| reg_to_string L3 = "l3"
| reg_to_string L4 = "l4"
| reg_to_string L5 = "l5"
| reg_to_string L6 = "l6"
| reg_to_string L7 = "l7"
| reg_to_string O0 = "arg"
| reg_to_string O1 = "clos"
| reg_to_string O2 = "o2"
| reg_to_string O3 = "o3"
| reg_to_string O4 = "o4"
| reg_to_string O5 = "o5"
| reg_to_string O6 = "sp"
| reg_to_string O7 = "lr"
| reg_to_string G0 = "zero"
| reg_to_string G1 = "alloc"
| reg_to_string G2 = "limit"
| reg_to_string G3 = "handler"
| reg_to_string G4 = "temp"
| reg_to_string G5 = "implicit"
| reg_to_string G6 = "stacklimit"
| reg_to_string G7 = "g7"
| reg_to_string cond = "the condition codes"
| reg_to_string heap = "the heap"
| reg_to_string stack = "the stack"
| reg_to_string y_reg = "the y register"
| reg_to_string nil_v = "the nil vector"
fun fp_reg_to_string I0 = "f24"
| fp_reg_to_string I1 = "f25"
| fp_reg_to_string I2 = "f26"
| fp_reg_to_string I3 = "f27"
| fp_reg_to_string I4 = "f28"
| fp_reg_to_string I5 = "f29"
| fp_reg_to_string I6 = "f30"
| fp_reg_to_string I7 = "f31"
| fp_reg_to_string L0 = "f16"
| fp_reg_to_string L1 = "f17"
| fp_reg_to_string L2 = "f18"
| fp_reg_to_string L3 = "f19"
| fp_reg_to_string L4 = "f20"
| fp_reg_to_string L5 = "f21"
| fp_reg_to_string L6 = "f22"
| fp_reg_to_string L7 = "f23"
| fp_reg_to_string O0 = "f8"
| fp_reg_to_string O1 = "f9"
| fp_reg_to_string O2 = "f10"
| fp_reg_to_string O3 = "f11"
| fp_reg_to_string O4 = "f12"
| fp_reg_to_string O5 = "f13"
| fp_reg_to_string O6 = "f14"
| fp_reg_to_string O7 = "f15"
| fp_reg_to_string G0 = "f0"
| fp_reg_to_string G1 = "f1"
| fp_reg_to_string G2 = "f2"
| fp_reg_to_string G3 = "f3"
| fp_reg_to_string G4 = "f4"
| fp_reg_to_string G5 = "f5"
| fp_reg_to_string G6 = "f6"
| fp_reg_to_string G7 = "f7"
| fp_reg_to_string cond = "the condition codes"
| fp_reg_to_string heap = "the heap"
| fp_reg_to_string stack = "the stack"
| fp_reg_to_string y_reg = "the y register"
| fp_reg_to_string nil_v = "the nil vector"
val caller_arg = O0
val callee_arg = I0
val caller_arg_regs = [O0,O2,O3,O4,O5]
val callee_arg_regs = [I0,I2,I3,I4,I5]
val caller_closure = O1
val callee_closure = I1
val fp = I6
val sp = O6
val lr = O7
val handler = G3
val gc1 = G1
val gc2 = G2
val global = G4
val fp_global = G4
val implicit = G5
val stack_limit = G6
exception OutOfScope of Sparc_Reg
fun after_preserve I0 = raise OutOfScope I0
| after_preserve I1 = raise OutOfScope I1
| after_preserve I2 = raise OutOfScope I2
| after_preserve I3 = raise OutOfScope I3
| after_preserve I4 = raise OutOfScope I4
| after_preserve I5 = raise OutOfScope I5
| after_preserve I6 = raise OutOfScope I6
| after_preserve I7 = raise OutOfScope I7
| after_preserve L0 = raise OutOfScope L0
| after_preserve L1 = raise OutOfScope L1
| after_preserve L2 = raise OutOfScope L2
| after_preserve L3 = raise OutOfScope L3
| after_preserve L4 = raise OutOfScope L4
| after_preserve L5 = raise OutOfScope L5
| after_preserve L6 = raise OutOfScope L6
| after_preserve L7 = raise OutOfScope L7
| after_preserve O0 = I0
| after_preserve O1 = I1
| after_preserve O2 = I2
| after_preserve O3 = I3
| after_preserve O4 = I4
| after_preserve O5 = I5
| after_preserve O6 = I6
| after_preserve O7 = I7
| after_preserve G0 = G0
| after_preserve G1 = G1
| after_preserve G2 = G2
| after_preserve G3 = G3
| after_preserve G4 = G4
| after_preserve G5 = G5
| after_preserve G6 = G6
| after_preserve G7 = G7
| after_preserve cond = Crash.impossible"after_preserve cond"
| after_preserve heap = Crash.impossible"after_preserve heap"
| after_preserve stack = Crash.impossible"after_preserve stack"
| after_preserve y_reg = Crash.impossible"after_preserve y_reg"
| after_preserve nil_v = Crash.impossible"after_preserve nil_v"
fun after_restore I0 = O0
| after_restore I1 = O1
| after_restore I2 = O2
| after_restore I3 = O3
| after_restore I4 = O4
| after_restore I5 = O5
| after_restore I6 = O6
| after_restore I7 = O7
| after_restore L0 = raise OutOfScope L0
| after_restore L1 = raise OutOfScope L1
| after_restore L2 = raise OutOfScope L2
| after_restore L3 = raise OutOfScope L3
| after_restore L4 = raise OutOfScope L4
| after_restore L5 = raise OutOfScope L5
| after_restore L6 = raise OutOfScope L6
| after_restore L7 = raise OutOfScope L7
| after_restore O0 = raise OutOfScope O0
| after_restore O1 = raise OutOfScope O1
| after_restore O2 = raise OutOfScope O2
| after_restore O3 = raise OutOfScope O3
| after_restore O4 = raise OutOfScope O4
| after_restore O5 = raise OutOfScope O5
| after_restore O6 = raise OutOfScope O6
| after_restore O7 = raise OutOfScope O7
| after_restore G0 = G0
| after_restore G1 = G1
| after_restore G2 = G2
| after_restore G3 = G3
| after_restore G4 = G4
| after_restore G5 = G5
| after_restore G6 = G6
| after_restore G7 = G7
| after_restore cond = Crash.impossible"after_restore cond"
| after_restore heap = Crash.impossible"after_restore heap"
| after_restore stack = Crash.impossible"after_restore stack"
| after_restore y_reg = Crash.impossible"after_restore y_reg"
| after_restore nil_v = Crash.impossible"after_restore nil_v"
val bits_per_word = 30
val digits_in_real = 64
exception Ord and Chr
val ord = fn x=>(ord (String.sub(x, 0))) handle ? => raise Ord
val chr = fn x=>(String.str(chr x)) handle ? => raise Chr
exception NeedsPreserve
fun check_reg L0 = raise NeedsPreserve
| check_reg L1 = raise NeedsPreserve
| check_reg L2 = raise NeedsPreserve
| check_reg L3 = raise NeedsPreserve
| check_reg L4 = raise NeedsPreserve
| check_reg L5 = raise NeedsPreserve
| check_reg L6 = raise NeedsPreserve
| check_reg L7 = raise NeedsPreserve
| check_reg O0 = raise NeedsPreserve
| check_reg O1 = raise NeedsPreserve
| check_reg O2 = raise NeedsPreserve
| check_reg O3 = raise NeedsPreserve
| check_reg O4 = raise NeedsPreserve
| check_reg O5 = raise NeedsPreserve
| check_reg O7 = raise NeedsPreserve
| check_reg _ = ()
end
;
