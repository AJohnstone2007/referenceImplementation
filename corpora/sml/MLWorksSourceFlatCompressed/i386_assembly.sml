require "i386_opcodes";
signature I386_ASSEMBLY = sig
structure I386_Opcodes : I386_OPCODES
type ''a Set
type tag
eqtype Backend_Annotation
datatype ('a, 'b)union = INL of 'a | INR of 'b
datatype cc =
above
| above_or_equal
| below
| below_or_equal
| cx_equal_zero
| ecx_equal_zero
| equal
| greater
| greater_or_equal
| less
| less_or_equal
| not_equal
| not_overflow
| not_parity
| not_sign
| overflow
| parity
| sign
datatype mnemonic =
aaa
| aad
| aam
| aas
| adc
| add
| align
| and_op
| arpl
| bound
| bsf
| bsr
| bt
| btc
| btr
| bts
| call
| cbw
| cwde
| clc
| cld
| cli
| clts
| cmc
| cmp
| cmps
| cmpsb
| cmpsw
| cmpsd
| cwd
| cdq
| daa
| das
| dec
| div_op
| enter
| fld
| fst
| fstp
| fild
| fist
| fistp
| fadd
| fsub
| fsubr
| fmul
| fdiv
| fdivr
| fabs
| fpatan
| fchs
| fcos
| fsin
| fsqrt
| fptan
| fldz
| fld1
| fyl2x
| fyl2xp
| f2xm1
| fcom
| fcomp
| fcompp
| fucom
| fucomp
| fucompp
| ftst
| fxam
| fldcw
| fstcw
| fnstcw
| fstsw
| fstsw_ax
| fnstsw
| fnclex
| hlt
| idiv
| imul
| in_op
| inc
| ins
| insb
| insw
| insd
| int
| into
| iret
| iretd
| jcc of cc
| jmp
| lahf
| lar
| lea
| leave
| lgdt
| lidt
| lgs
| lss
| lds
| les
| lfs
| lldt
| lmsw
| lock
| lods
| lodsb
| lodsw
| lodsd
| loop
| loopz
| loopnz
| lsl
| ltr
| mov
| movs
| movsb
| movsw
| movsd
| movsx
| movzx
| mul
| neg
| nop
| not
| or
| out
| outs
| outsb
| outsw
| outsd
| pop
| popa
| popad
| popf
| popfd
| push
| pusha
| pushad
| pushf
| pushfd
| rcl
| rcr
| rol
| ror
| rep
| repz
| repnz
| ret
| sahf
| sal
| sar
| shl
| shr
| sbb
| scas
| scasb
| scasw
| scasd
| setcc of cc
| sgdt
| sidt
| shld
| shrd
| sldt
| smsw
| stc
| std
| sti
| stos
| stosb
| stosw
| stosd
| str
| sub
| test
| verr
| verw
| wait
| xchg
| xlat
| xlatb
| xor
datatype offset_operand =
SMALL of int
| LARGE of int * int
datatype mem_operand =
MEM of {base: I386_Opcodes.I386Types.I386_Reg option,
index: (I386_Opcodes.I386Types.I386_Reg * int option) option,
offset: offset_operand option}
datatype adr_mode =
rel8 of int
| rel16 of int
| rel32 of int
| fix_rel32 of int
| ptr16_16 of int * int
| ptr16_32 of int * int
| r8 of I386_Opcodes.I386Types.I386_Reg
| r16 of I386_Opcodes.I386Types.I386_Reg
| r32 of I386_Opcodes.I386Types.I386_Reg
| imm8 of int
| imm16 of int
| imm32 of (int * int)
| r_m8 of (I386_Opcodes.I386Types.I386_Reg, mem_operand) union
| r_m16 of (I386_Opcodes.I386Types.I386_Reg, mem_operand) union
| r_m32 of (I386_Opcodes.I386Types.I386_Reg, mem_operand) union
| m8 of mem_operand
| m16 of mem_operand
| m32 of mem_operand
| fp_mem of mem_operand
| fp_reg of int
datatype opcode =
OPCODE of mnemonic * (adr_mode list)
| AugOPCODE of opcode * Backend_Annotation
val check: opcode -> unit
val assemble : opcode -> I386_Opcodes.opcode
val opcode_size : opcode -> int
type LabMap
val make_labmap : opcode list list list -> LabMap
val print_mnemonic : mnemonic -> string
val print : opcode -> string
val labprint : opcode * int * LabMap -> string * string
val reverse_branch : mnemonic -> mnemonic
val inverse_branch : mnemonic -> mnemonic
val defines_and_uses :
opcode ->
I386_Opcodes.I386Types.I386_Reg Set * I386_Opcodes.I386Types.I386_Reg Set *
I386_Opcodes.I386Types.I386_Reg Set * I386_Opcodes.I386Types.I386_Reg Set
val nop_code : opcode
val other_nop_code : opcode
val no_op : opcode * tag option * string
end
;
