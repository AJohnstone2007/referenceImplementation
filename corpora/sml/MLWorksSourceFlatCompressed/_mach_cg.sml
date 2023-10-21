require "$.basis.__int";
require "$.basis.__string";
require "../utils/print";
require "../utils/mlworks_timer";
require "../utils/lists";
require "../utils/crash";
require "../utils/diagnostic";
require "../utils/sexpr";
require "../basics/ident";
require "../main/reals";
require "../main/code_module";
require "../mir/mirtables";
require "../mir/mirregisters";
require "../rts/gen/implicit";
require "../rts/gen/tags";
require "../main/info";
require "../main/options";
require "../main/machspec";
require "sparc_schedule";
require "../main/mach_cg";
functor Mach_Cg(
structure Tags : TAGS
structure Print : PRINT
structure Timer : INTERNAL_TIMER
structure Lists : LISTS
structure Crash : CRASH
structure Info : INFO
structure Options : OPTIONS
structure Sexpr : SEXPR
structure Reals : REALS
structure Ident : IDENT
structure MirTables : MIRTABLES
structure MirRegisters : MIRREGISTERS
structure MachSpec : MACHSPEC
structure Code_Module : CODE_MODULE
structure Sparc_Schedule : SPARC_SCHEDULE
structure Implicit_Vector : IMPLICIT_VECTOR
structure Diagnostic : DIAGNOSTIC
sharing Info.Location = Ident.Location
sharing MirTables.MirTypes.Set = MachSpec.Set
sharing MirTables.MirTypes = MirRegisters.MirTypes = Sparc_Schedule.Sparc_Assembly.MirTypes
sharing type Ident.SCon = MirTables.MirTypes.SCon
sharing type Sparc_Schedule.Sparc_Assembly.Sparc_Opcodes.MachTypes.Sparc_Reg
= MachSpec.register
) : MACH_CG =
struct
structure Sparc_Assembly = Sparc_Schedule.Sparc_Assembly
structure Sparc_Opcodes = Sparc_Assembly.Sparc_Opcodes
structure MirTypes = MirTables.MirTypes
structure MachTypes = Sparc_Opcodes.MachTypes
structure MachSpec = MachSpec
structure Diagnostic = Diagnostic
structure Debugger_Types = MirTypes.Debugger_Types
structure Map = MirTypes.Map
structure Ident = Ident
structure Set = MirTypes.Set
structure Info = Info
structure RuntimeEnv = Debugger_Types.RuntimeEnv
structure Options = Options
structure Bits = MLWorks.Internal.Bits
type Module = Code_Module.Module
type Opcode = Sparc_Assembly.opcode
val do_timings = ref false
val trace_dummy_instructions =
[(Sparc_Assembly.other_nop_code,NONE,"Dummy instructions for tracing"),
(Sparc_Assembly.other_nop_code,NONE,"Dummy instructions for tracing"),
(Sparc_Assembly.other_nop_code,NONE,"Dummy instructions for tracing")]
val do_diagnostic = false
fun diagnostic_output level =
if do_diagnostic then Diagnostic.output level else fn f => ()
val print_code_size = ref false
val arith_imm_limit = 4096
val branch_disp_limit = 512 * 4096
val call_disp_limit = 16 * 256 * 256 * 256
fun contract_sexpr(Sexpr.NIL, [], acc) =
Lists.reducel (fn (x, y) => y @ x) ([], acc)
| contract_sexpr(Sexpr.NIL, x :: xs, acc) = contract_sexpr(x, xs, acc)
| contract_sexpr(Sexpr.ATOM x, to_do, acc) =
contract_sexpr(Sexpr.NIL, to_do, x :: acc)
| contract_sexpr(Sexpr.CONS(x, y), to_do, acc) =
contract_sexpr(x, y :: to_do, acc)
val contract_sexpr =
fn x => contract_sexpr(x, [], [])
fun find_nop_offsets(_, []) = ~1
| find_nop_offsets(offset, (opcode, _) :: rest) =
if opcode = Sparc_Assembly.other_nop_code then
offset
else
find_nop_offsets(offset+1, rest)
val find_nop_offsets = fn (tag, code) => find_nop_offsets(0, code)
fun check_range(i:int, signed, pos_limit) =
if signed then
(i >= 0 andalso i < pos_limit) orelse
(i < 0 andalso i >= ~pos_limit)
else i >= 0 andalso i < pos_limit
fun fault_range(i, signed, pos_limit) =
if check_range(i, signed, pos_limit) then i
else
(diagnostic_output 3
(fn _ => ["fault_range called with value ",
Int.toString i,
" in positive range ",
Int.toString pos_limit]);
Crash.impossible"Immediate constant out of range" )
fun make_imm_fault(i, signed, max_pos) =
let
val _ = fault_range(i, signed, max_pos)
val res = Sparc_Assembly.IMM i
in
res
end
fun mantissa_is_zero mantissa =
let
val exp_mant = explode mantissa
fun exp_mant_is_zero [] = true
| exp_mant_is_zero(#"0" :: xs) = exp_mant_is_zero xs
| exp_mant_is_zero _ = false
in
exp_mant_is_zero exp_mant
end
fun binary_list_to_string(done, [], _, 128) = concat(rev done)
| binary_list_to_string(_, [], _, l) =
Crash.impossible("Binary_list_to_string length not 8, remainder length " ^
Int.toString l)
| binary_list_to_string(done, x :: xs, digit, power) =
let
val x = MLWorks.String.ord x - ord #"0"
in
if power = 1 then
binary_list_to_string(String.str(chr(digit + x)) :: done, xs, 0, 128)
else
binary_list_to_string(done, xs, digit + x * power, power div 2)
end
fun to_binary(digits, value) =
let
fun to_sub(0, _, done) = done
| to_sub(digs_to_go, value, done) =
let
val digit = String.str(chr(value mod 2 + ord #"0"))
in
to_sub(digs_to_go - 1, value div 2, digit :: done)
end
in
to_sub(digits, value, [])
end
fun n_zeroes(done, 0) = done
| n_zeroes(done, n) = n_zeroes("0" :: done, n-1)
fun adjust (error_info,x,location) (mantissa, exponent, max_exponent, bits) =
if mantissa_is_zero mantissa then
(mantissa, 0)
else
if exponent > 0 andalso exponent < max_exponent then
(mantissa, exponent)
else
if exponent >= max_exponent then
Info.error'
error_info
(Info.FATAL,location,
"Real number unrepresentable: " ^ x)
else
if exponent < ~bits then (concat(n_zeroes([], bits)), 0)
else
(concat(n_zeroes([], abs exponent)) ^ mantissa, 0)
fun to_single_string args (sign, mantissa, exponent) =
let
val real_exponent = exponent + 127
val (mantissa, real_exponent) = adjust args (mantissa, real_exponent, 255, 23)
val binary_list =
(if sign then "1" else "0") ::
to_binary(8, real_exponent) @
(map str (explode (MLWorks.String.substring (mantissa, 1, 23))))
in
binary_list_to_string([], binary_list, 0, 128)
end
fun to_double_string args (sign, mantissa, exponent) =
let
val real_exponent = exponent + 1023
val (mantissa, real_exponent) = adjust args (mantissa, real_exponent, 2047, 52)
val binary_list =
(if sign then "1" else "0") ::
to_binary(11, real_exponent) @
(map String.str
(explode(MLWorks.String.substring (mantissa, 1, 52))))
in
binary_list_to_string([], binary_list, 0, 128)
end
fun to_extended_string args (sign, mantissa, exponent) =
let
val real_exponent = exponent + 16383
val (mantissa, real_exponent) =
adjust args (mantissa, real_exponent, 32767, 63)
val binary_list =
(if sign then "1" else "0") ::
to_binary(15, real_exponent) @
n_zeroes([], 16) @
(map String.str
(explode(MLWorks.String.substring (mantissa, 0, 64)))) @
n_zeroes([], 32)
in
binary_list_to_string([], binary_list, 0, 128)
end
fun value_cg(i, MirTypes.SCON (Ident.STRING x),_) = Code_Module.STRING(i, x)
| value_cg(i, MirTypes.SCON (Ident.REAL(x,location)),error_info) =
(let
val the_real = Reals.evaluate_real x
val (sign, mantissa, exponent) = Reals.find_real_components the_real
val encoding_function = case MachTypes.fp_used of
MachTypes.single => to_single_string (error_info,x,location)
| MachTypes.double => to_double_string (error_info,x,location)
| MachTypes.extended => to_extended_string (error_info,x,location)
in
Code_Module.REAL(i, encoding_function(sign, mantissa, exponent))
end handle MLWorks.Internal.StringToReal =>
Info.error'
error_info
(Info.FATAL, location, "Real number unrepresentable: " ^ x)
)
| value_cg(_, MirTypes.SCON (Ident.INT _),_) = Crash.impossible"VALUE(INT)"
| value_cg(_, MirTypes.SCON (Ident.CHAR _),_) = Crash.impossible"VALUE(CHAR)"
| value_cg(_, MirTypes.SCON (Ident.WORD _),_) = Crash.impossible"VALUE(WORD)"
| value_cg (i,MirTypes.MLVALUE value,_) =
Code_Module.MLVALUE (i,value)
val absent = NONE
fun last_opcode [] = (Sparc_Assembly.nop, false)
| last_opcode [elem as (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
_, _)] =
(elem, true)
| last_opcode([(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
_, _),
elem as (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
_, _)]) =
(elem, true)
| last_opcode([elem as (Sparc_Assembly.BRANCH(Sparc_Assembly.BA, _), _, _),
_]) =
(elem, true)
| last_opcode([elem as (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
_, _),
_]) =
(elem, true)
| last_opcode(_ :: xs) = last_opcode xs
fun make_proc_info(res as (main_tree, tag_tree), []) = res
| make_proc_info((main_tree, tag_tree),
((block_tag, opcode_list)) :: rest) =
let
val last_tag_exists as (tag, ok) = case last_opcode opcode_list of
((_, SOME tag, _), true) => (tag, true)
| _ => (block_tag, false)
in
make_proc_info
((Map.define (main_tree, block_tag, last_tag_exists),
if ok then
Map.define (tag_tree, tag, 0)
else tag_tree), rest)
end
fun rev_app([], acc) = acc
| rev_app(x :: xs, acc) = rev_app(xs, x :: acc)
fun remove_trailing_branch(block_tag, opcode_list) =
let
val rev_opc = rev opcode_list
val new_opc =
case rev_opc of
(Sparc_Assembly.BRANCH_ANNUL _, _, _) :: rest => rest
| (operation, opt, comment) :: (Sparc_Assembly.BRANCH _, _, _) :: rest =>
(operation, opt, comment ^ " preceding BA removed") :: rest
| _ :: (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _), _, _) :: rest => rest
| _ =>
Crash.impossible"Remove trailing branch fails"
in
(block_tag, rev new_opc)
end
fun find_dest_block(tag, [], [], x,y) = ((tag, []), false, x,y)
| find_dest_block(dest_tag,
(block as (block_tag, opcode_list)) ::
rest,
other,
x , [] ) =
if dest_tag = block_tag then
(block, true, x @ rest, other)
else find_dest_block(dest_tag, rest, other, block :: x,[])
| find_dest_block(dest_tag,
[],
(block as (block_tag, opcode_list)) ::
rest,
x , y ) =
if dest_tag = block_tag then
(block, true, x , y @ rest)
else find_dest_block(dest_tag, [], rest, x, block :: y)
| find_dest_block _ =
Crash.impossible "This should never happen in _mach_cg "
fun reorder_blocks(proc as (proc_tag, block_list)) =
let
val (proc_info, tag_tree) =
make_proc_info((Map.empty , Map.empty), block_list)
val proc_info_map = Map.tryApply proc_info
val tag_tree_map = Map.tryApply tag_tree
fun do_fall_throughs_with_continuers_calculated(done, (block as (block_tag, opcode_list)),
continuers,non_continuers) =
let
val (dest_tag, found_block) =
Map.apply_default'(proc_info, (block_tag, false), block_tag)
fun do_next() =
case continuers of
[] =>
rev_app(done, (block :: non_continuers))
| _ =>
let
val (next_block, continuers') =
(let
val (tag, _) = Lists.findp
(fn (x, _) =>
case tag_tree_map x of
NONE => true
| _ => false)
continuers
val (others,value) =
Lists.assoc_returning_others(tag,continuers)
in
((tag, value),others)
end) handle Lists.Find =>
(Lists.hd continuers, Lists.tl continuers)
in
do_fall_throughs_with_continuers_calculated(block :: done,
next_block,
continuers',
non_continuers)
end
in
if found_block then
let
val (dest_block, found_dest, non_continuers',continuers') =
find_dest_block(dest_tag, non_continuers, continuers, [] , [])
in
if found_dest then
do_fall_throughs_with_continuers_calculated
(remove_trailing_branch block :: done,
dest_block, continuers', non_continuers')
else
do_next()
end
else
do_next()
end
fun do_fall_throughs(done, block, []) = rev(block :: done)
| do_fall_throughs(done, block,rest) =
let
fun continues(tag, _) =
case proc_info_map tag of
SOME (_, t) => t
| _ => false
val (continuers,non_continuers) =
Lists.partition continues rest
in
do_fall_throughs_with_continuers_calculated(done,block,continuers,non_continuers)
end
val (hd_block_list, tl_block_list) = case block_list of
x :: y => (x, y)
| _ => Crash.impossible"Empty block list"
in
(proc_tag, do_fall_throughs([], hd_block_list, tl_block_list))
end
fun tag_offsets([], offset, tag_env) = (offset, tag_env)
| tag_offsets((tag, ho_list) :: rest, disp, tag_env) =
tag_offsets(rest, disp + 4 * (length ho_list),
Map.define (tag_env, tag, disp))
fun tag_offsets_for_list(_, [], env) = env
| tag_offsets_for_list(offset, (_, proc) :: rest, env) =
let
val (next_offset, env) = tag_offsets(proc, offset, env)
val next_offset' =
next_offset + 4 + 4
val next_offset'' =
if next_offset' mod 8 = 4
then next_offset'+4
else next_offset'
in
tag_offsets_for_list(next_offset'', rest, env)
end
exception bad_offset of
MirTypes.tag * (Sparc_Assembly.opcode * MirTypes.tag option * string) list
fun do_little_block(block as (tag, opcode_list)) =
case opcode_list of
[ins,
(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, i), SOME tag', comm),
_] =>
(tag,
[(Sparc_Assembly.BRANCH(Sparc_Assembly.BA, i), SOME tag', comm),
ins])
| _ => block
fun reschedule_little_blocks(proc_tag, block_list) =
(proc_tag, map do_little_block block_list)
fun linearise_list proc_list =
let
val new_proc_list =
Timer.xtime
("reordering blocks", !do_timings,
fn () => map reorder_blocks proc_list)
val new_proc_list = map reschedule_little_blocks new_proc_list
fun do_linearise proc_list =
let
val tag_env = tag_offsets_for_list(0, proc_list, Map.empty)
val _ = diagnostic_output 3 (fn _ => ["Tag_env ="])
val _ =
diagnostic_output 3
(fn _ => (ignore(map
(fn (x, y) => Print.print("Tag " ^ MirTypes.print_tag x ^ ", value " ^ Int.toString y ^ "\n"))
(Map.to_list tag_env)) ;
[] ))
fun lookup_env tag = Map.tryApply'(tag_env,tag)
fun rev_map f arg =
let
fun map_sub([], acc) = acc
| map_sub(x :: xs, acc) = map_sub(xs, f x :: acc)
in
map_sub arg
end
fun rev_app([], y) = y
| rev_app(x :: xs, y) = rev_app(xs, x :: y)
fun copy_n(n, from, acc, new_tail) =
if n < 0 then
Crash.impossible"copy_n negative arg"
else
if n = 0 then
rev_app(acc, new_tail)
else
case from of
(x :: xs) =>
copy_n(n-1, xs, x :: acc, new_tail)
| _ => Crash.impossible"copy_n short list"
fun drop(n, the_list) =
if n < 0 then
Crash.impossible"drop negative arg"
else
if n = 0 then the_list
else
case the_list of
[] => Crash.impossible"drop bad list"
| _ :: rest => drop(n-1, rest)
fun linearise_proc(_, offset, [], done) = (offset, rev done)
| linearise_proc(proc_offset, start, blocks as (block :: block_list), done) =
let
fun do_block(block_start, (block_tag, opcode_list), done) =
let
fun do_opcode((Sparc_Assembly.BRANCH(branch, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
fault_range(( res - offset) div 4,
true, branch_disp_limit)
in
(Sparc_Assembly.BRANCH(branch, disp), comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode branch")
| do_opcode((Sparc_Assembly.BRANCH_ANNUL(branch, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
fault_range((res - offset) div 4,
true, branch_disp_limit)
in
(Sparc_Assembly.BRANCH_ANNUL(branch, disp), comment)
end
| NONE =>
(Crash.impossible"Assoc do_opcode branch_annul"))
| do_opcode((Sparc_Assembly.FBRANCH(branch, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
fault_range((res - offset) div 4,
true, branch_disp_limit)
in
(Sparc_Assembly.FBRANCH(branch, disp), comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode fbranch")
| do_opcode((Sparc_Assembly.FBRANCH_ANNUL(branch, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
fault_range((res - offset) div 4,
true, branch_disp_limit)
in
(Sparc_Assembly.FBRANCH_ANNUL(branch, disp), comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode fbranch_annul")
| do_opcode((Sparc_Assembly.Call(Sparc_Assembly.CALL, i,debug_info),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
fault_range(( res + i - offset) div 4,
true, call_disp_limit)
in
(Sparc_Assembly.Call(Sparc_Assembly.CALL, disp,debug_info), comment)
end
| NONE => Crash.impossible "Assoc do_opcode Call")
| do_opcode((Sparc_Assembly.LOAD_OFFSET(Sparc_Assembly.LEO, rd, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp = (res + i) - proc_offset
in
if check_range(disp, true, arith_imm_limit) then
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, rd, MachTypes.G0, Sparc_Assembly.IMM disp),
comment)
else
let
val _ =
diagnostic_output 3
(fn _ => ["Found bad LEO, substituting\n"])
val head_size = (offset - block_start) div 4
val tail = drop(1 + head_size, opcode_list)
val new_comment = comment ^ " (expanded adr)"
val new_tail =
(Sparc_Assembly.SPECIAL_LOAD_OFFSET
(Sparc_Assembly.LOAD_OFFSET_HIGH, rd, MachTypes.G0, i),
SOME tag, new_comment) ::
(Sparc_Assembly.SPECIAL_LOAD_OFFSET
(Sparc_Assembly.LOAD_OFFSET_AND_MASK, rd, rd, i),
SOME tag, new_comment) :: tail
in
raise bad_offset
(block_tag,
copy_n(head_size, opcode_list, [], new_tail))
end
end
| NONE => Crash.impossible "Assoc do_opcode LEO")
| do_opcode((Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd,
rs1, Sparc_Assembly.IMM i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp = res + i - offset
in
if check_range(disp, true, arith_imm_limit) then
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd,
rs1, Sparc_Assembly.IMM disp),
comment)
else
let
val _ =
diagnostic_output 3
(fn _ => ["Found bad LEA, substituting\n"])
val head_size = (offset - block_start) div 4
val tail = drop(1 + head_size, opcode_list)
val _ =
if rs1 = rd then
Crash.impossible"ADR has dest in lr"
else ()
val new_comment = comment ^ " (expanded adr)"
val new_tail =
(Sparc_Assembly.SetHI
(Sparc_Assembly.SETHI, rd, i),
SOME tag, new_comment) ::
(Sparc_Assembly.SPECIAL_ARITHMETIC
(Sparc_Assembly.ADD_AND_MASK, rd,
rd, Sparc_Assembly.IMM(i + 4)),
SOME tag, new_comment) ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd,
rs1, Sparc_Assembly.REG rd),
NONE, new_comment) :: tail
in
raise bad_offset
(block_tag,
copy_n(head_size, opcode_list, [], new_tail))
end
end
| NONE =>
Crash.impossible"Assoc do_opcode arith")
| do_opcode((Sparc_Assembly.SPECIAL_ARITHMETIC
(_, rd, rs1, Sparc_Assembly.IMM i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp =
make_imm_fault
((res + i - offset) mod 1024,
true, arith_imm_limit)
in
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd, rs1, disp),
comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode arith")
| do_opcode((Sparc_Assembly.SPECIAL_LOAD_OFFSET(load, rd, rn, i),
SOME tag, comment), _) =
(case lookup_env tag of
SOME res =>
let
val disp = res + i - proc_offset
in
case load of
Sparc_Assembly.LOAD_OFFSET_HIGH =>
(Sparc_Assembly.SetHI
(Sparc_Assembly.SETHI, rd,
(disp div 1024) mod (1024 * 1024 * 4)),
comment)
| Sparc_Assembly.LOAD_OFFSET_AND_MASK =>
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd,
rn,
make_imm_fault(disp mod 1024, true, arith_imm_limit)),
comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode SPECIAL_LOAD_OFFSET")
| do_opcode((Sparc_Assembly.SetHI(_, rd, i),
SOME tag, comment), offset) =
(case lookup_env tag of
SOME res =>
let
val disp = res + i - offset
val disp = (disp div 1024) mod (1024 * 1024 * 4)
in
(Sparc_Assembly.SetHI(Sparc_Assembly.SETHI, rd, disp),
comment)
end
| NONE =>
Crash.impossible"Assoc do_opcode arith")
| do_opcode((opcode, NONE, comment), offset) =
(opcode, comment)
| do_opcode _ = Crash.impossible"Bad tagged instruction"
val (opcodes_and_offsets, next) =
Lists.number_from(opcode_list, block_start, 4, fn x => x)
in
(rev_map do_opcode (opcodes_and_offsets, done), next)
end
val (so_far, next) = do_block(start, block, done)
in
linearise_proc(proc_offset, next, block_list, so_far)
end
fun do_linearise_sub(_, []) = []
| do_linearise_sub(offset, ((tag, proc) ) :: rest) =
let
val (offset', done') =
linearise_proc(offset, offset, proc, [])
val offset'' =
offset' + 4 + 4
val offset''' =
if offset'' mod 8 = 4
then offset'' + 4
else offset''
in
(tag, done') :: do_linearise_sub(offset''', rest)
end
fun subst_bad_offset_block(proc_list, block as (tag, opcode_list)) =
let
fun remap(proc_tag, block_list) =
(proc_tag,
map
(fn (block' as (block_tag, _)) =>
if block_tag = tag then block else block')
block_list)
in
map remap proc_list
end
in
do_linearise_sub(0, proc_list)
handle bad_offset bad_offset_block =>
do_linearise (subst_bad_offset_block(proc_list, bad_offset_block))
end
in
do_linearise new_proc_list
end
fun is_reg(MirTypes.GP_GC_REG reg) = true
| is_reg(MirTypes.GP_NON_GC_REG reg) = true
| is_reg _ = false
fun move_reg(rd, rs) =
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, rd, rs, Sparc_Assembly.REG MachTypes.G0), absent, "")
fun move_imm(rd, imm) =
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, rd, MachTypes.G0, Sparc_Assembly.IMM imm),
absent, "")
fun gp_from_reg(MirTypes.GC_REG reg) = MirTypes.GP_GC_REG reg
| gp_from_reg(MirTypes.NON_GC_REG reg) = MirTypes.GP_NON_GC_REG reg
datatype proc_stack =
PROC_STACK of
{non_gc_spill_size : int,
fp_spill_size : int,
fp_save_size : int,
gc_spill_size : int,
gc_stack_alloc_size : int,
register_save_size : int,
non_gc_spill_offset : int,
fp_spill_offset : int,
fp_save_offset : int,
gc_spill_offset : int,
gc_stack_alloc_offset : int,
register_save_offset : int,
allow_fp_spare_slot : bool,
float_value_size : int
}
fun mach_cg
error_info
(Options.OPTIONS {compiler_options =
Options.COMPILEROPTIONS {generate_debug_info,
debug_variables,
generate_moduler,
opt_leaf_fns, ...},
...},
MirTypes.CODE(MirTypes.REFS(loc_refs,
{requires = ext_refs,
vars = vars,
exns = exns,
strs = strs,
funs = funs}),
value_list,
proc_list_list),
(gc_map,
non_gc_map,
fp_map),
debugging_map) =
let
val {gc, non_gc, fp} = MirRegisters.pack_next
val gc_array = MLWorks.Internal.Array.array(gc, MachSpec.global)
val _ =
MirTypes.GC.Map.iterate
(fn (mir_reg, reg) =>
MLWorks.Internal.Array.update(gc_array, MirTypes.GC.unpack mir_reg, reg))
gc_map
val non_gc_array = MLWorks.Internal.Array.array(non_gc, MachSpec.global)
val _ =
MirTypes.NonGC.Map.iterate
(fn (mir_reg, reg) =>
MLWorks.Internal.Array.update(non_gc_array, MirTypes.NonGC.unpack mir_reg, reg))
non_gc_map
val fp_array = MLWorks.Internal.Array.array(fp, MachSpec.global)
val _ =
MirTypes.FP.Map.iterate
(fn (mir_reg, reg) =>
MLWorks.Internal.Array.update(fp_array, MirTypes.FP.unpack mir_reg, reg))
fp_map
val debug_map = ref debugging_map
val value_elements =
(map
(fn(MirTypes.VALUE(tag, x)) =>
value_cg(Lists.assoc(tag, loc_refs), x,error_info))
value_list) handle Lists.Assoc => Crash.impossible"Assoc value_elements"
fun symb_value(PROC_STACK
{non_gc_spill_size,
fp_spill_size,
fp_save_size,
gc_spill_size,
gc_stack_alloc_size,
register_save_size,
non_gc_spill_offset,
fp_spill_offset,
fp_save_offset,
gc_spill_offset,
gc_stack_alloc_offset,
register_save_offset,
allow_fp_spare_slot,
float_value_size
}) =
let
fun symbolic_value MirTypes.GC_SPILL_SIZE = gc_spill_size * 4
| symbolic_value MirTypes.NON_GC_SPILL_SIZE =
non_gc_spill_size * 4
| symbolic_value(MirTypes.GC_SPILL_SLOT i) =
let
fun resolve_value i =
if i >= gc_spill_size then
Crash.impossible
("Spill slot " ^ Int.toString i ^
" requested, but only " ^ Int.toString gc_spill_size ^
" allocated\n")
else
~(gc_spill_offset + 4 * (1 + i))
in
case i of
MirTypes.DEBUG (spill as ref (RuntimeEnv.OFFSET1(i)), name) =>
let
val print = fn s => print(s ^ "\n")
val _ =
if i = 0 then print ("Zero spill for " ^ name)
else ()
val value = resolve_value i
in
spill := RuntimeEnv.OFFSET2(RuntimeEnv.GC, value);
value
end
| MirTypes.DEBUG (ref(RuntimeEnv.OFFSET2(_, i)),name) => i
| MirTypes.SIMPLE i => resolve_value i
end
| symbolic_value(MirTypes.NON_GC_SPILL_SLOT i) =
let
fun resolve_value i =
let
val offset = if allow_fp_spare_slot then 1 else 0
in
if i >= non_gc_spill_size then
Crash.impossible
("non gc spill slot " ^ Int.toString i ^
" requested, but only " ^
Int.toString non_gc_spill_size ^
" allocated\n")
else
~(non_gc_spill_offset + 4 * (1 + offset + i))
end
in
case i of
MirTypes.DEBUG (spill as ref(RuntimeEnv.OFFSET1(i)),name) =>
let
val value = resolve_value i
in
spill := RuntimeEnv.OFFSET2(RuntimeEnv.NONGC, value);
value
end
| MirTypes.DEBUG (ref(RuntimeEnv.OFFSET2(_, i)),name) => i
| MirTypes.SIMPLE i => resolve_value i
end
| symbolic_value(MirTypes.FP_SPILL_SLOT i) =
let
fun resolve_value i =
if i >= fp_spill_size then
Crash.impossible
("fp spill slot " ^ Int.toString i ^
" requested, but only " ^
Int.toString fp_spill_size ^
" allocated\n")
else
~(fp_spill_offset + float_value_size * (1 + i))
in
case i of
MirTypes.DEBUG(spill as ref(RuntimeEnv.OFFSET1(i)),name) =>
let
val value = resolve_value i
in
spill := RuntimeEnv.OFFSET2(RuntimeEnv.FP, value);
value
end
| MirTypes.DEBUG(ref(RuntimeEnv.OFFSET2(_, i)),name) => i
| MirTypes.SIMPLE i => resolve_value i
end
in
symbolic_value
end
fun do_store(_, _, 0, done) = done
| do_store(reg, offset, 1, done) =
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.G0,
reg,
Sparc_Assembly.IMM offset), absent,
"Initialise a stack slot") :: done
| do_store(reg, offset, n, done) =
if n < 0 then Crash.impossible"Do_store"
else
if offset mod 8 = 4 then
do_store(reg, offset+4, n-1,
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.G0,
reg,
Sparc_Assembly.IMM offset), absent,
"Initialise one misaligned stack slot") :: done)
else
do_store(reg, offset+8, n-2,
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.STD, MachTypes.G0,
reg,
Sparc_Assembly.IMM offset), absent,
"Initialise two stack slots") :: done)
fun n_stores(from, no_of_stores, end_tag) =
let
val end_limit = from + (no_of_stores-1)*4
val end_instrs =
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME end_tag,
"Finish cleaning stack"),
Sparc_Assembly.nop]
in
if check_range(end_limit, true,
arith_imm_limit) then
do_store(MachTypes.sp, from, no_of_stores, end_instrs)
else
Crash.impossible
("n_stores end_limit = " ^
Int.toString end_limit)
end
fun inline_allocate (reg, tag, bytes, leaf, rest) =
((Sparc_Assembly.TAGGED_ARITHMETIC
(Sparc_Assembly.TADDCCTV, MachTypes.gc1, MachTypes.gc1, bytes),
absent, "Attempt to allocate some heap") ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
((if leaf then
Sparc_Assembly.OR
else
Sparc_Assembly.ADD),
reg, MachTypes.gc2, Sparc_Assembly.IMM tag),
absent, "Tag allocated pointer") ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.gc2, MachTypes.gc2, bytes),
absent, "Advance allocation point") ::
rest)
fun do_blocks(_, [], _, _, _, _) = []
| do_blocks(needs_preserve, MirTypes.BLOCK(tag,opcodes) :: rest,
stack_layout as PROC_STACK
{non_gc_spill_size,
fp_spill_size,
fp_save_size,
gc_spill_size,
gc_stack_alloc_size,
register_save_size,
non_gc_spill_offset,
fp_spill_offset,
fp_save_offset,
gc_spill_offset,
gc_stack_alloc_offset,
register_save_offset,
allow_fp_spare_slot,
float_value_size
},
spills_need_init,
stack_need_init,
fps_to_preserve
) =
let
val frame_size = register_save_offset + register_save_size
val non_save_frame_size = register_save_offset
val symbolic_value = symb_value stack_layout
fun gp_check_range(MirTypes.GP_IMM_INT i, signed, pos_limit) =
check_range(i, signed, pos_limit div 4)
| gp_check_range(MirTypes.GP_IMM_ANY i, signed, pos_limit) =
check_range(i, signed, pos_limit)
| gp_check_range(MirTypes.GP_IMM_SYMB symb, signed, pos_limit) =
check_range(symbolic_value symb, signed, pos_limit)
| gp_check_range _ =
Crash.impossible"gp_check_range of non-immediate"
fun split_int(MirTypes.GP_IMM_INT i) =
(((i div (4096 div 4)) mod (256 * 256 * 16))*4,
(i mod (4096 div 4))*4)
| split_int(MirTypes.GP_IMM_ANY i) =
(((i div 4096) mod (256 * 256 * 16))*4, i mod 4096)
| split_int(MirTypes.GP_IMM_SYMB symb) =
let
val i = symbolic_value symb
in
(((i div 4096) mod (256 * 256 * 16))*4, i mod 4096)
end
| split_int _ = Crash.impossible"split_int of non-immediate"
fun load_large_number_into_register (reg, gp_operand) =
case split_int gp_operand of
(0, 0) =>
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, reg,
MachTypes.G0, Sparc_Assembly.REG MachTypes.G0),
absent, "")]
| (0, lo) =>
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, reg,
MachTypes.G0, Sparc_Assembly.IMM lo),
absent, "")]
| (hi, 0) =>
[(Sparc_Assembly.SetHI
(Sparc_Assembly.SETHI, reg, hi),
absent, "Get high part")]
| (hi, lo) =>
[(Sparc_Assembly.SetHI
(Sparc_Assembly.SETHI, reg, hi),
absent, "Get high part"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, reg,
reg, Sparc_Assembly.IMM lo),
absent, "Add in low part")]
fun make_imm_format3(MirTypes.GP_IMM_INT i) =
make_imm_fault(4 * i, true, arith_imm_limit)
| make_imm_format3(MirTypes.GP_IMM_ANY i) =
make_imm_fault(i, true, arith_imm_limit)
| make_imm_format3(MirTypes.GP_IMM_SYMB symb) =
make_imm_fault(symbolic_value symb, true, arith_imm_limit)
| make_imm_format3 _ = Crash.impossible"make_imm of non-immediate"
fun make_imm_for_store(MirTypes.GP_IMM_ANY i) =
make_imm_fault(i, true, arith_imm_limit)
| make_imm_for_store(MirTypes.GP_IMM_SYMB symb) =
make_imm_fault(symbolic_value symb, true, arith_imm_limit)
| make_imm_for_store _ =
Crash.impossible"make_imm_for_store(bad value)"
fun do_save_instrs(_, []) = []
| do_save_instrs(offset, fp :: rest) =
case MachTypes.fp_used of
MachTypes.single =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.STF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"save float") :: do_save_instrs(offset+4, rest)
| MachTypes.double =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.STDF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"save float") :: do_save_instrs(offset+8, rest)
| MachTypes.extended =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.STDF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"save float") ::
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.STDF,
MachTypes.next_reg(MachTypes.next_reg fp),
MachTypes.fp, Sparc_Assembly.IMM (offset+8)), NONE,
"save float") ::
do_save_instrs(offset+16, rest)
fun do_restore_instrs(_, []) = []
| do_restore_instrs(offset, fp :: rest) =
case MachTypes.fp_used of
MachTypes.single =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"restore float") ::
do_restore_instrs(offset+4, rest)
| MachTypes.double =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDDF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"restore float") ::
do_restore_instrs(offset+8, rest)
| MachTypes.extended =>
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDDF, fp, MachTypes.fp,
Sparc_Assembly.IMM offset), NONE,
"restore float") ::
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDDF,
MachTypes.next_reg(MachTypes.next_reg fp),
MachTypes.fp, Sparc_Assembly.IMM (offset+8)), NONE,
"restore float") ::
do_restore_instrs(offset+16, rest)
val fp_save_start = fp_save_offset + fp_save_size * float_value_size
val save_fps = do_save_instrs(~fp_save_start, fps_to_preserve)
val restore_fps = do_restore_instrs(~fp_save_start, fps_to_preserve)
fun is_comment(MirTypes.COMMENT _) = true
| is_comment _ = false
fun do_everything
(_, tag, [], done, [], final_result) =
(tag, contract_sexpr done) :: final_result
| do_everything
(needs_preserve, tag, [], done,
MirTypes.BLOCK(tag',opcodes) :: blocks,
final_result) =
do_everything
(needs_preserve, tag', Lists.filter_outp is_comment opcodes, Sexpr.NIL, blocks,
(tag, contract_sexpr done) :: final_result)
| do_everything
(needs_preserve, tag, opcode :: opcode_list, done,
block_list, final_result) =
let
fun lookup_reg(reg, table) =
let
val reg = MLWorks.Internal.Array.sub(table, reg)
in
if needs_preserve then reg
else MachTypes.after_restore reg
end
fun lookup_reg_operand(MirTypes.GC_REG reg) =
lookup_reg(MirTypes.GC.unpack reg, gc_array)
| lookup_reg_operand(MirTypes.NON_GC_REG reg) =
lookup_reg(MirTypes.NonGC.unpack reg, non_gc_array)
fun lookup_gp_operand(MirTypes.GP_GC_REG reg) =
lookup_reg(MirTypes.GC.unpack reg, gc_array)
| lookup_gp_operand(MirTypes.GP_NON_GC_REG reg) =
lookup_reg(MirTypes.NonGC.unpack reg, non_gc_array)
| lookup_gp_operand _ =
Crash.impossible"lookup_gp_operand(constant)"
fun lookup_fp_operand(MirTypes.FP_REG reg) =
MLWorks.Internal.Array.sub(fp_array, MirTypes.FP.unpack reg)
val (result_list, opcode_list, new_blocks, new_final_result) =
case opcode of
MirTypes.TBINARY(tagged_binary_op, taglist, reg_operand,
gp_operand, gp_operand') =>
let
val tag = case taglist of [] => NONE | a::_ => SOME a
val rd = lookup_reg_operand reg_operand
fun preserve_order MirTypes.SUBS = true
| preserve_order MirTypes.DIVS = true
| preserve_order MirTypes.MODS = true
| preserve_order MirTypes.SUB32S = true
| preserve_order MirTypes.DIV32S = true
| preserve_order MirTypes.MOD32S = true
| preserve_order _ = false
val (gp_operand, gp_operand', redo) =
if is_reg gp_operand then
(gp_operand, gp_operand', false)
else
if is_reg gp_operand'
then
if preserve_order tagged_binary_op
then
(gp_operand, gp_operand', true)
else
(gp_operand', gp_operand, false)
else
(gp_operand, gp_operand', false)
in
if redo then
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.TBINARY(tagged_binary_op, taglist, reg_operand,
MirTypes.GP_GC_REG MirRegisters.global,
gp_operand') ::
opcode_list, block_list, final_result)
else
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
if is_reg gp_operand' orelse
gp_check_range(gp_operand', true,
arith_imm_limit) then
let
val reg_or_imm =
if is_reg gp_operand' then
Sparc_Assembly.REG(lookup_gp_operand
gp_operand')
else make_imm_format3 gp_operand'
val (use_traps,long_op) =
case tagged_binary_op of
MirTypes.ADDS => (true,false)
| MirTypes.SUBS => (true,false)
| MirTypes.MULS => (false,false)
| MirTypes.DIVS => (false,false)
| MirTypes.MODS => (false,false)
| MirTypes.ADD32S => (false,true)
| MirTypes.SUB32S => (false,true)
| MirTypes.MUL32S => (false,true)
| MirTypes.DIV32S => (false,true)
| MirTypes.MOD32S => (false,true)
| MirTypes.DIVU => (false,false)
| MirTypes.MODU => (false,false)
| MirTypes.DIV32U => (false,true)
| MirTypes.MOD32U => (false,true)
in
if use_traps then
let
val opcode = case tagged_binary_op of
MirTypes.ADDS =>
Sparc_Assembly.TADDCCTV
| MirTypes.SUBS =>
Sparc_Assembly.TSUBCCTV
| _ => Crash.impossible"do_opcodes(TBINARY)"
in
([(Sparc_Assembly.TAGGED_ARITHMETIC
(opcode, rd, rs1, reg_or_imm), absent, "")],
opcode_list,
block_list,
final_result)
end
else
let
val (opcode, untag_arg) =
case tagged_binary_op of
MirTypes.ADD32S => (Sparc_Assembly.ADDCC,false)
| MirTypes.SUB32S => (Sparc_Assembly.SUBCC,false)
| MirTypes.MULS => (Sparc_Assembly.SMUL,true)
| MirTypes.MUL32S => (Sparc_Assembly.SMUL,false)
| _ => Crash.impossible"do_opcodes(TBINARY)"
val cont_tag = MirTypes.new_tag()
val clean_code =
if not long_op
then []
else
let
val regs_to_clean = [rd, rs1]
val regs_to_clean = case reg_or_imm of
Sparc_Assembly.REG reg => reg :: regs_to_clean
| _ => regs_to_clean
val regs_to_clean = Lists.rev_remove_dups regs_to_clean
val regs_to_clean =
Lists.filterp
(fn reg => reg <> MachTypes.G0 andalso
reg <> MachTypes.global)
regs_to_clean
in
map
(fn reg =>
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, reg,
MachTypes.G0, Sparc_Assembly.REG MachTypes.G0),
absent, "Clean"))
regs_to_clean
end
val error_block =
clean_code @
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
tag, ""),
Sparc_Assembly.nop]
fun make_overflow_error () =
((Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BVC, 0),
SOME cont_tag,
"Branch if not overflow") ::
Sparc_Assembly.nop ::
error_block,
[])
fun make_multiply_error () =
let
val error_tag = MirTypes.new_tag()
val main_code =
[(Sparc_Assembly.READ_STATE (Sparc_Assembly.RDY,
MachTypes.global),
NONE,"get high word of result"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD,
MachTypes.global,
MachTypes.global,
Sparc_Assembly.IMM 1),
NONE,""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC,
MachTypes.G0,
MachTypes.global,
Sparc_Assembly.IMM 1),
NONE,""),
(Sparc_Assembly.BRANCH (Sparc_Assembly.BGU,0),
SOME error_tag,""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUB,
MachTypes.global,
MachTypes.global,
Sparc_Assembly.IMM 1),
NONE,"adjust back"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.XORCC,
MachTypes.G0,
rd,
Sparc_Assembly.REG MachTypes.global),
NONE, ""),
(Sparc_Assembly.BRANCH_ANNUL (Sparc_Assembly.BL,0),
SOME error_tag,""),
Sparc_Assembly.nop,
(Sparc_Assembly.BRANCH_ANNUL (Sparc_Assembly.BA,0),
SOME cont_tag,""),
Sparc_Assembly.nop]
in
(main_code,
[(error_tag,error_block)])
end
val (error_check, error_blocks) =
case tagged_binary_op of
MirTypes.ADD32S => make_overflow_error ()
| MirTypes.SUB32S => make_overflow_error ()
| MirTypes.MULS => make_multiply_error ()
| MirTypes.MUL32S => make_multiply_error ()
| _ => Crash.impossible"do_opcodes(TBINARY)"
val binary_operation =
(if not untag_arg
then
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, reg_or_imm), absent, "")]
else
let
val (rs1,reg_or_imm) =
if rs1 = rd then (rs1,reg_or_imm)
else
case reg_or_imm of
Sparc_Assembly.REG rs2 =>
if rd = rs2 then (rs2,Sparc_Assembly.REG rs1)
else (rs1,reg_or_imm)
| _ => (rs1,reg_or_imm)
val both_result_reg =
rs1 = rd andalso
(case reg_or_imm of
Sparc_Assembly.REG rs2 => rs2 = rs1
| _ => false)
val shift_amount = if both_result_reg then 1 else 2
in
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SRA, rd, rs1, Sparc_Assembly.IMM shift_amount), absent, "untag argument"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rd, reg_or_imm), absent, "")]
end) @
error_check
in
(binary_operation,
[],
MirTypes.BLOCK(cont_tag, opcode_list) :: block_list,
error_blocks @ final_result)
end
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG
MirRegisters.global,
gp_operand') ::
MirTypes.TBINARY(tagged_binary_op, taglist,
reg_operand,
gp_operand,
MirTypes.GP_GC_REG
MirRegisters.global) ::
opcode_list, block_list, final_result)
end
else
(diagnostic_output 3
(fn _ => ["Mach_Cg(TBINARY) first arg not reg\n"]);
([],
MirTypes.UNARY(MirTypes.MOVE, reg_operand,
gp_operand) ::
MirTypes.TBINARY(tagged_binary_op, taglist,
reg_operand,
gp_from_reg reg_operand,
gp_operand') ::
opcode_list, block_list, final_result))
end
| MirTypes.BINARY(binary_op, reg_operand, gp_operand,
gp_operand') =>
let
fun is_shift MirTypes.ADDU = false
| is_shift MirTypes.SUBU = false
| is_shift MirTypes.MULU = false
| is_shift MirTypes.MUL32U = false
| is_shift MirTypes.AND = false
| is_shift MirTypes.OR = false
| is_shift MirTypes.EOR = false
| is_shift MirTypes.LSR = true
| is_shift MirTypes.ASL = true
| is_shift MirTypes.ASR = true
val rd = lookup_reg_operand reg_operand
val (opcode,untag_arg) =
case binary_op of
MirTypes.ADDU => (Sparc_Assembly.ADD,false)
| MirTypes.SUBU => (Sparc_Assembly.SUB,false)
| MirTypes.MULU => (Sparc_Assembly.UMUL,true)
| MirTypes.MUL32U => (Sparc_Assembly.UMUL,false)
| MirTypes.AND => (Sparc_Assembly.AND,false)
| MirTypes.OR => (Sparc_Assembly.OR,false)
| MirTypes.EOR => (Sparc_Assembly.XOR,false)
| MirTypes.LSR => (Sparc_Assembly.SRL,false)
| MirTypes.ASL => (Sparc_Assembly.SLL,false)
| MirTypes.ASR => (Sparc_Assembly.SRA,false)
fun needs_reverse Sparc_Assembly.SUB = true
| needs_reverse Sparc_Assembly.SUBCC = true
| needs_reverse Sparc_Assembly.SUBX = true
| needs_reverse Sparc_Assembly.SUBXCC = true
| needs_reverse Sparc_Assembly.SRL = true
| needs_reverse Sparc_Assembly.SLL = true
| needs_reverse Sparc_Assembly.SRA = true
| needs_reverse _ = false
val (gp_operand, gp_operand', redo) =
if is_reg gp_operand then
(gp_operand, gp_operand', false)
else
if is_reg gp_operand' then
if needs_reverse opcode then
(gp_operand, gp_operand', true)
else
(gp_operand', gp_operand, false)
else
(gp_operand, gp_operand', false)
val is_a_shift = is_shift binary_op
in
if redo andalso not is_a_shift then
let
val inter_reg =
case gp_operand' of
MirTypes.GP_GC_REG r =>
(if r = MirRegisters.global then
(case reg_operand of
MirTypes.GC_REG r' =>
if r = r' then
Crash.impossible
"source and dest global with large int"
else
r'
| MirTypes.NON_GC_REG _ =>
Crash.impossible"BINARY doesn't deliver GC")
else
MirRegisters.global)
| _ => Crash.impossible "BINARY has non-gc register"
in
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG inter_reg,
gp_operand) ::
MirTypes.BINARY(binary_op, reg_operand,
MirTypes.GP_GC_REG inter_reg,
gp_operand') ::
opcode_list, block_list, final_result)
end
else
if is_a_shift then
let
val const_shift = case gp_operand' of
MirTypes.GP_GC_REG _ => false
| MirTypes.GP_NON_GC_REG _ => false
| _ => true
in
if const_shift then
let
val shift_size = make_imm_format3 gp_operand'
fun get_shift(Sparc_Assembly.IMM i) = i
| get_shift _ =
Crash.impossible"mach_cg:non_constant in shift by constant"
val shift_val = get_shift shift_size
in
case binary_op of
MirTypes.LSR =>
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, shift_size),
absent, "")],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.BINARY(binary_op,
reg_operand,
MirTypes.GP_GC_REG MirRegisters.global,
gp_operand') ::
opcode_list,
block_list, final_result)
| MirTypes.ASR =>
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, shift_size),
absent, "")],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.BINARY(binary_op,
reg_operand,
MirTypes.GP_GC_REG MirRegisters.global,
gp_operand') ::
opcode_list,
block_list, final_result)
| MirTypes.ASL =>
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, shift_size),
absent, "")],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.BINARY(binary_op,
reg_operand,
MirTypes.GP_GC_REG MirRegisters.global,
gp_operand') ::
opcode_list,
block_list, final_result)
| _ => Crash.impossible"mach_cg: non-shift in shift case"
end
else
let
val rs1 = lookup_gp_operand gp_operand'
val cont_tag = MirTypes.new_tag()
fun make_range_test limit =
let
val bad_tag = MirTypes.new_tag()
in
(bad_tag,
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC,
MachTypes.G0, rs1, Sparc_Assembly.IMM limit),
absent, "shift range test"),
(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BCC, 0),
SOME bad_tag, "branch if shift arg too big"),
Sparc_Assembly.nop])
end
val continue =
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME cont_tag, ""),
Sparc_Assembly.nop]
fun constant_out_of_range_shift gp_op =
case binary_op of
MirTypes.ASL => [move_imm(rd, 0)]
| MirTypes.ASR =>
if gp_check_range(gp_op, false, arith_imm_limit) then
[move_imm(rd, 0)]
else
(case gp_operand of
MirTypes.GP_IMM_INT i =>
[move_imm(rd, if i < 0 then ~4 else 0)]
| MirTypes.GP_IMM_ANY i =>
[move_imm(rd, 0)]
| _ => Crash.impossible"Mach_cg:shift:bad constant")
| MirTypes.LSR => [move_imm(rd, 0)]
| _ => Crash.impossible"mach_cg: non-shift in shift case"
val shift_limit = case binary_op of
MirTypes.ASL => 32
| MirTypes.ASR => 31
| MirTypes.LSR => 32
| _ => Crash.impossible"mach_cg: non-shift in shift case"
fun variable_out_of_range_shift gp_op =
case binary_op of
MirTypes.ASL => [move_imm(rd, 0)]
| MirTypes.ASR =>
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd,lookup_gp_operand gp_op, Sparc_Assembly.IMM 31),
absent, "")]
| MirTypes.LSR => [move_imm(rd, 0)]
| _ => Crash.impossible"mach_cg: non-shift in shift case"
val (bad_tag, range_test) =
make_range_test shift_limit
val shift_op =
if is_reg gp_operand then
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd,
lookup_gp_operand gp_operand,
Sparc_Assembly.REG rs1),
absent, "") :: continue
else
load_large_number_into_register(rd, gp_operand) @
((Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rd, Sparc_Assembly.REG rs1),
absent, "") :: continue)
in
(range_test @ shift_op @ continue, [],
MirTypes.BLOCK(cont_tag, opcode_list) ::
block_list,
(bad_tag,
(if is_reg gp_operand then
variable_out_of_range_shift gp_operand
else
constant_out_of_range_shift gp_operand) @
continue) :: final_result)
end
end
else
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
if is_reg gp_operand' orelse
gp_check_range(gp_operand', true,
arith_imm_limit) then
let
val reg_or_imm =
if is_reg gp_operand' then
Sparc_Assembly.REG(lookup_gp_operand
gp_operand')
else make_imm_format3 gp_operand'
in
if not untag_arg
then
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, reg_or_imm), absent, "")],
opcode_list, block_list, final_result)
else
let
val both_rd =
rs1 = rd andalso
(case reg_or_imm of
Sparc_Assembly.REG rs2 => rs2 = rs1
| _ => false)
val shift_amount =
if both_rd then 1 else 2
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SRA, rd, rs1, Sparc_Assembly.IMM shift_amount),
absent, ""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rd, reg_or_imm), absent, "")],
opcode_list, block_list, final_result)
end
end
else
let
val inter_reg =
case gp_operand of
MirTypes.GP_GC_REG r =>
(if r = MirRegisters.global then
(case reg_operand of
MirTypes.GC_REG r' =>
if r = r' then
Crash.impossible
"source and dest global with large int"
else
r'
| MirTypes.NON_GC_REG _ =>
Crash.impossible"BINARY doesn't deliver GC")
else
MirRegisters.global)
| _ => Crash.impossible "BINARY has non-gc register"
in
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG inter_reg,
gp_operand') ::
MirTypes.BINARY(binary_op, reg_operand,
gp_operand,
MirTypes.GP_GC_REG inter_reg) ::
opcode_list, block_list, final_result)
end
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.BINARY(binary_op, reg_operand,
MirTypes.GP_GC_REG MirRegisters.global,
gp_operand') ::
opcode_list, block_list, final_result)
end
| MirTypes.UNARY(MirTypes.MOVE, reg_operand, gp_operand) =>
let
val rd = lookup_reg_operand reg_operand
val opcode = Sparc_Assembly.OR
val imm = Sparc_Assembly.REG MachTypes.G0
val code_list =
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
if rd = rs1 then []
else
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, imm), absent, "")]
end
else
if gp_check_range(gp_operand, true,
arith_imm_limit) then
let
val imm = case make_imm_format3 gp_operand of
Sparc_Assembly.IMM 0 => imm
| non_zero_imm => non_zero_imm
in
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, MachTypes.G0, imm), absent, "")]
end
else
load_large_number_into_register(rd, gp_operand)
in
(code_list, opcode_list, block_list, final_result)
end
| MirTypes.UNARY(MirTypes.NOT, reg_operand, gp_operand) =>
let
val rd = lookup_reg_operand reg_operand
val opcode = Sparc_Assembly.XORN
val simple_imm = Sparc_Assembly.IMM 3
in
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, simple_imm), absent, "")],
opcode_list, block_list, final_result)
end
else
([], MirTypes.UNARY(MirTypes.MOVE, reg_operand,
gp_operand) ::
MirTypes.UNARY(MirTypes.NOT, reg_operand,
gp_from_reg reg_operand) ::
opcode_list,
block_list, final_result)
end
| MirTypes.UNARY(MirTypes.NOT32, reg_operand, gp_operand) =>
let
val rd = lookup_reg_operand reg_operand
val opcode = Sparc_Assembly.XORN
val simple_imm = Sparc_Assembly.IMM 0
in
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, simple_imm), absent, "")],
opcode_list, block_list, final_result)
end
else
([], MirTypes.UNARY(MirTypes.MOVE, reg_operand,
gp_operand) ::
MirTypes.UNARY(MirTypes.NOT32, reg_operand,
gp_from_reg reg_operand) ::
opcode_list,
block_list, final_result)
end
| MirTypes.UNARY(MirTypes.INTTAG, reg_operand, gp_operand) =>
let
val rd = lookup_reg_operand reg_operand
val opcode = Sparc_Assembly.ANDN
val simple_imm = Sparc_Assembly.IMM 3
in
if is_reg gp_operand then
let
val rs1 = lookup_gp_operand gp_operand
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(opcode, rd, rs1, simple_imm), absent, "")],
opcode_list, block_list, final_result)
end
else
([], MirTypes.UNARY(MirTypes.MOVE, reg_operand,
gp_operand) ::
MirTypes.UNARY(MirTypes.INTTAG, reg_operand,
gp_from_reg reg_operand) ::
opcode_list,
block_list, final_result)
end
| MirTypes.NULLARY(MirTypes.CLEAN, reg_operand) =>
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, lookup_reg_operand reg_operand,
MachTypes.G0, Sparc_Assembly.REG MachTypes.G0),
absent, "Clean")],
opcode_list, block_list, final_result)
| MirTypes.BINARYFP(binary_fp_op, fp_operand, fp_operand',
fp_operand'') =>
let
val rd = lookup_fp_operand fp_operand
val rs1 = lookup_fp_operand fp_operand'
val rs2 = lookup_fp_operand fp_operand''
val operation = case (MachTypes.fp_used, binary_fp_op) of
(MachTypes.single, MirTypes.FADD) => Sparc_Assembly.FADDS
| (MachTypes.single, MirTypes.FSUB) => Sparc_Assembly.FSUBS
| (MachTypes.single, MirTypes.FMUL) => Sparc_Assembly.FMULS
| (MachTypes.single, MirTypes.FDIV) => Sparc_Assembly.FDIVS
| (MachTypes.double, MirTypes.FADD) => Sparc_Assembly.FADDD
| (MachTypes.double, MirTypes.FSUB) => Sparc_Assembly.FSUBD
| (MachTypes.double, MirTypes.FMUL) => Sparc_Assembly.FMULD
| (MachTypes.double, MirTypes.FDIV) => Sparc_Assembly.FDIVD
| (MachTypes.extended, MirTypes.FADD) =>
Sparc_Assembly.FADDX
| (MachTypes.extended, MirTypes.FSUB) =>
Sparc_Assembly.FSUBX
| (MachTypes.extended, MirTypes.FMUL) =>
Sparc_Assembly.FMULX
| (MachTypes.extended, MirTypes.FDIV) =>
Sparc_Assembly.FDIVX
in
([(Sparc_Assembly.FBINARY(operation, rd, rs1, rs2), absent,
"")], opcode_list, block_list, final_result)
end
| MirTypes.UNARYFP(unary_fp_op, fp_operand, fp_operand') =>
let
val rd = lookup_fp_operand fp_operand
val rs2 = lookup_fp_operand fp_operand'
val (operation, extra_moves) =
case (MachTypes.fp_used, unary_fp_op) of
(MachTypes.single, MirTypes.FSQRT) =>
(Sparc_Assembly.FSQRTS, 0)
| (MachTypes.single, MirTypes.FMOVE) =>
(Sparc_Assembly.FMOV, 0)
| (MachTypes.single, MirTypes.FABS) =>
(Sparc_Assembly.FABS, 0)
| (MachTypes.single, MirTypes.FNEG) =>
(Sparc_Assembly.FNEG, 0)
| (MachTypes.double, MirTypes.FSQRT) =>
(Sparc_Assembly.FSQRTD, 0)
| (MachTypes.double, MirTypes.FMOVE) =>
(Sparc_Assembly.FMOV, 1)
| (MachTypes.double, MirTypes.FABS) =>
(Sparc_Assembly.FABS, 1)
| (MachTypes.double, MirTypes.FNEG) =>
(Sparc_Assembly.FNEG, 1)
| (MachTypes.extended, MirTypes.FSQRT) =>
(Sparc_Assembly.FSQRTX, 0)
| (MachTypes.extended, MirTypes.FMOVE) =>
(Sparc_Assembly.FMOV, 3)
| (MachTypes.extended, MirTypes.FABS) =>
(Sparc_Assembly.FABS, 3)
| (MachTypes.extended, MirTypes.FNEG) =>
(Sparc_Assembly.FNEG, 3)
| _ =>
Crash.impossible"Bad unary fp generated"
fun add_moves(_, _, 0) = []
| add_moves(rd, rs2, moves) =
let
val rd = MachTypes.next_reg rd
val rs2 = MachTypes.next_reg rs2
in
(Sparc_Assembly.FUNARY(Sparc_Assembly.FMOV, rd, rs2),
absent, "") :: add_moves(rd, rs2, moves - 1)
end
val extra_code = add_moves(rd, rs2, extra_moves)
in
((Sparc_Assembly.FUNARY(operation, rd, rs2), absent,
"") :: extra_code, opcode_list, block_list,
final_result)
end
| MirTypes.TBINARYFP(tagged_binary_fp_op, taglist, fp_operand,
fp_operand', fp_operand'') =>
let
val tag = case taglist of [] => NONE | a::_ => SOME a
val rd = lookup_fp_operand fp_operand
val rs1 = lookup_fp_operand fp_operand'
val rs2 = lookup_fp_operand fp_operand''
val operation =
case (MachTypes.fp_used, tagged_binary_fp_op)
of (MachTypes.single, MirTypes.FADDV) =>
Sparc_Assembly.FADDS
| (MachTypes.single, MirTypes.FSUBV) =>
Sparc_Assembly.FSUBS
| (MachTypes.single, MirTypes.FMULV) =>
Sparc_Assembly.FMULS
| (MachTypes.single, MirTypes.FDIVV) =>
Sparc_Assembly.FDIVS
| (MachTypes.double, MirTypes.FADDV) =>
Sparc_Assembly.FADDD
| (MachTypes.double, MirTypes.FSUBV) =>
Sparc_Assembly.FSUBD
| (MachTypes.double, MirTypes.FMULV) =>
Sparc_Assembly.FMULD
| (MachTypes.double, MirTypes.FDIVV) =>
Sparc_Assembly.FDIVD
| (MachTypes.extended, MirTypes.FADDV) =>
Sparc_Assembly.FADDX
| (MachTypes.extended, MirTypes.FSUBV) =>
Sparc_Assembly.FSUBX
| (MachTypes.extended, MirTypes.FMULV) =>
Sparc_Assembly.FMULX
| (MachTypes.extended, MirTypes.FDIVV) =>
Sparc_Assembly.FDIVX
in
([(Sparc_Assembly.FBINARY(operation, rd, rs1, rs2),
absent, "")],
opcode_list, block_list, final_result)
end
| MirTypes.TUNARYFP(tagged_unary_fp_op, tag, fp_operand,
fp_operand') =>
let
val rd = lookup_fp_operand fp_operand
val rs2 = lookup_fp_operand fp_operand'
val (operation, extra_moves) =
case (MachTypes.fp_used, tagged_unary_fp_op) of
(MachTypes.single, MirTypes.FSQRTV) =>
(Sparc_Assembly.FSQRTS, 0)
| (MachTypes.single, MirTypes.FABSV) =>
(Sparc_Assembly.FABS, 0)
| (MachTypes.single, MirTypes.FNEGV) =>
(Sparc_Assembly.FNEG, 0)
| (MachTypes.double, MirTypes.FSQRTV) =>
(Sparc_Assembly.FSQRTD, 0)
| (MachTypes.double, MirTypes.FABSV) =>
(Sparc_Assembly.FABS, 1)
| (MachTypes.double, MirTypes.FNEGV) =>
(Sparc_Assembly.FNEG, 1)
| (MachTypes.extended, MirTypes.FSQRTV) =>
(Sparc_Assembly.FSQRTX, 0)
| (MachTypes.extended, MirTypes.FABSV) =>
(Sparc_Assembly.FABS, 3)
| (MachTypes.extended, MirTypes.FNEGV) =>
(Sparc_Assembly.FNEG, 3)
| _ =>
Crash.impossible"Bad unary fp generated"
fun add_moves(_, _, 0) = []
| add_moves(rd, rs2, moves) =
let
val rd = MachTypes.next_reg rd
val rs2 = MachTypes.next_reg rs2
in
(Sparc_Assembly.FUNARY(Sparc_Assembly.FMOV, rd, rs2),
absent, "") :: add_moves(rd, rs2, moves - 1)
end
val extra_code = add_moves(rd, rs2, extra_moves)
in
((Sparc_Assembly.FUNARY(operation, rd, rs2), absent,
"") :: extra_code, opcode_list, block_list,
final_result)
end
| MirTypes.STACKOP(stack_op, reg_operand,
SOME offset) =>
let
val opcode = case stack_op of
MirTypes.PUSH => MirTypes.STREF
| MirTypes.POP => MirTypes.LDREF
val _ =
if offset > gc_stack_alloc_size then
Crash.impossible("Stack access at offset " ^
Int.toString offset ^
" requested, in total area of only " ^
Int.toString gc_stack_alloc_size ^
"\n")
else()
in
([],
MirTypes.STOREOP(opcode, reg_operand,
MirTypes.GC_REG MirRegisters.fp,
MirTypes.GP_IMM_ANY
(~(gc_stack_alloc_offset + 4 * (offset + 1)))) ::
opcode_list, block_list, final_result)
end
| MirTypes.STACKOP _ =>
Crash.impossible"Offset missing on STACK_OP"
| opcode as MirTypes.IMMSTOREOP _ =>
Crash.impossible"IMMSTOREOP not supported on sparc"
| opcode as MirTypes.STOREOP(store_op, reg_operand, reg_operand',
gp_operand) =>
let
val (shuffle, new_opcode_list) =
if is_reg gp_operand orelse
gp_check_range(gp_operand, true, arith_imm_limit) then
(
case opcode_list of
(store_op as MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG g,
c_reg as MirTypes.GC_REG c,
MirTypes.GP_IMM_ANY ~1)) ::
(tail as (MirTypes.BRANCH_AND_LINK _ :: _)) =>
if g = MirRegisters.global andalso c = MirRegisters.caller_closure
andalso reg_operand' <> c_reg andalso reg_operand <> c_reg then
(true, store_op :: opcode :: tail)
else
(false, [])
| (store_op as MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG g,
c_reg as MirTypes.GC_REG c,
MirTypes.GP_IMM_ANY ~1)) ::
(tail as (MirTypes.TAIL_CALL _ :: _)) =>
if (not needs_preserve) andalso
g = MirRegisters.global andalso c = MirRegisters.callee_closure
andalso reg_operand' <> c_reg andalso reg_operand <> c_reg then
(true, store_op :: opcode :: tail)
else
(false, [])
| (move_op as MirTypes.UNARY(MirTypes.MOVE, c_reg as MirTypes.GC_REG c,
MirTypes.GP_GC_REG r)) ::
(store_op as MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG g,
MirTypes.GC_REG c',
MirTypes.GP_IMM_ANY ~1)) ::
(tail as (MirTypes.BRANCH_AND_LINK _ :: _)) =>
let
val r_reg = MirTypes.GC_REG r
in
if g = MirRegisters.global andalso c = MirRegisters.caller_closure
andalso c' = c
andalso reg_operand' <> c_reg
andalso reg_operand' <> r_reg
andalso reg_operand <> c_reg
andalso reg_operand <> r_reg then
(true, move_op :: store_op :: opcode :: tail)
else
(false, [])
end
| (move_op as MirTypes.UNARY(MirTypes.MOVE, c_reg as MirTypes.GC_REG c,
MirTypes.GP_GC_REG r)) ::
(store_op as MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG g,
MirTypes.GC_REG c',
MirTypes.GP_IMM_ANY ~1)) ::
(tail as (MirTypes.TAIL_CALL _ :: _)) =>
let
val r_reg = MirTypes.GC_REG r
in
if (not needs_preserve) andalso
g = MirRegisters.global andalso c = MirRegisters.callee_closure
andalso c' = c
andalso reg_operand' <> c_reg
andalso reg_operand' <> r_reg
andalso reg_operand <> c_reg
andalso reg_operand <> r_reg then
(true, move_op :: store_op :: opcode :: tail)
else
(false, [])
end
| _ => (false, [])
)
else
(false, [])
in
if shuffle then
([], new_opcode_list, block_list, final_result)
else
let
val rd = lookup_reg_operand reg_operand
val rs1 = lookup_reg_operand reg_operand'
val store = case store_op of
MirTypes.LD => Sparc_Assembly.LD
| MirTypes.ST => Sparc_Assembly.ST
| MirTypes.LDB => Sparc_Assembly.LDUB
| MirTypes.STB => Sparc_Assembly.STB
| MirTypes.LDREF => Sparc_Assembly.LD
| MirTypes.STREF => Sparc_Assembly.ST
in
if is_reg gp_operand orelse
gp_check_range(gp_operand, true, arith_imm_limit) then
let
val reg_or_imm =
if is_reg gp_operand then
Sparc_Assembly.REG(lookup_gp_operand gp_operand)
else make_imm_for_store gp_operand
in
([(Sparc_Assembly.LOAD_AND_STORE(store, rd, rs1,
reg_or_imm), absent, "")],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.STOREOP(store_op, reg_operand,
reg_operand',
MirTypes.GP_GC_REG
MirRegisters.global) ::
opcode_list, block_list, final_result)
end
end
| MirTypes.STOREFPOP(store_fp_op, fp_operand, reg_operand,
gp_operand) =>
let
val frd = lookup_fp_operand fp_operand
val rs1 = lookup_reg_operand reg_operand
val (store, repeat) =
case (MachTypes.fp_used, store_fp_op) of
(MachTypes.single, MirTypes.FLD) =>
(Sparc_Assembly.LDF, false)
| (MachTypes.single, MirTypes.FST) =>
(Sparc_Assembly.STF, false)
| (MachTypes.single, MirTypes.FLDREF) =>
(Sparc_Assembly.LDF, false)
| (MachTypes.single, MirTypes.FSTREF) =>
(Sparc_Assembly.STF, false)
| (MachTypes.double, MirTypes.FLD) =>
(Sparc_Assembly.LDDF, false)
| (MachTypes.double, MirTypes.FST) =>
(Sparc_Assembly.STDF, false)
| (MachTypes.double, MirTypes.FLDREF) =>
(Sparc_Assembly.LDDF, false)
| (MachTypes.double, MirTypes.FSTREF) =>
(Sparc_Assembly.STDF, false)
| (MachTypes.extended, MirTypes.FLD) =>
(Sparc_Assembly.LDDF, true)
| (MachTypes.extended, MirTypes.FST) =>
(Sparc_Assembly.STDF, true)
| (MachTypes.extended, MirTypes.FLDREF) =>
(Sparc_Assembly.LDDF, true)
| (MachTypes.extended, MirTypes.FSTREF) =>
(Sparc_Assembly.STDF, true)
val gp_op = case reg_operand of
MirTypes.GC_REG reg => MirTypes.GP_GC_REG reg
| MirTypes.NON_GC_REG reg => MirTypes.GP_NON_GC_REG reg
fun gp_op_is_large(arg as MirTypes.GP_IMM_ANY i) =
gp_check_range(arg, true, arith_imm_limit) andalso
gp_check_range(MirTypes.GP_IMM_INT(i+8), true,
arith_imm_limit)
| gp_op_is_large(MirTypes.GP_IMM_INT i) =
gp_op_is_large(MirTypes.GP_IMM_ANY(i*4))
| gp_op_is_large(arg as MirTypes.GP_IMM_SYMB symb) =
gp_op_is_large(MirTypes.GP_IMM_ANY(symbolic_value symb))
| gp_op_is_large(MirTypes.GP_GC_REG _) = true
| gp_op_is_large(MirTypes.GP_NON_GC_REG _) = true
in
if repeat then
if gp_op_is_large gp_operand then
([],
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
gp_op,
gp_operand) ::
MirTypes.STOREFPOP(store_fp_op, fp_operand,
MirTypes.GC_REG
MirRegisters.global,
MirTypes.GP_IMM_ANY 0) ::
opcode_list, block_list, final_result)
else
let
val (imm, arg) =
case make_imm_for_store gp_operand of
imm as Sparc_Assembly.IMM arg => (imm, arg)
| _ => Crash.impossible
"make_imm_for_store fails to return IMM"
val imm' = Sparc_Assembly.IMM(arg+8)
in
([(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(store, frd, rs1, imm),
absent, ""),
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(store,
MachTypes.next_reg(MachTypes.next_reg frd),
MachTypes.next_reg(MachTypes.next_reg rs1),
imm'),
absent, "")],
opcode_list, block_list, final_result)
end
else
if is_reg gp_operand orelse
gp_check_range(gp_operand, true, arith_imm_limit) then
let
val reg_or_imm =
if is_reg gp_operand then
Sparc_Assembly.REG(lookup_gp_operand gp_operand)
else make_imm_for_store gp_operand
in
([(Sparc_Assembly.LOAD_AND_STORE_FLOAT(store, frd,
rs1,
reg_or_imm),
absent, "")],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
gp_op,
gp_operand) ::
MirTypes.STOREFPOP(store_fp_op, fp_operand,
MirTypes.GC_REG
MirRegisters.global,
MirTypes.GP_IMM_ANY 0) ::
opcode_list, block_list, final_result)
end
| MirTypes.REAL(int_to_float, fp_operand, gp_operand) =>
let
val operation = case MachTypes.fp_used of
MachTypes.single => Sparc_Assembly.FITOS
| MachTypes.double => Sparc_Assembly.FITOD
| MachTypes.extended => Sparc_Assembly.FITOX
val rd = lookup_fp_operand fp_operand
val rs2 =
if is_reg gp_operand then
lookup_gp_operand gp_operand
else
MachTypes.global
in
if is_reg gp_operand then
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SRA, MachTypes.global,
rs2, Sparc_Assembly.IMM 2), absent,
"Untag operand"),
(Sparc_Assembly.LOAD_AND_STORE(Sparc_Assembly.ST,
MachTypes.global,
MachTypes.fp,
Sparc_Assembly.IMM
~4), absent,
"Store the value to be converted in spare slot"),
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDF, rd, MachTypes.fp,
Sparc_Assembly.IMM ~4), absent,
"And reload to fp register"),
(Sparc_Assembly.CONV_OP(operation, rd, rd),
absent, "")],
opcode_list, block_list, final_result)
else
([],
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand) ::
MirTypes.REAL(int_to_float, fp_operand,
MirTypes.GP_GC_REG
MirRegisters.global) ::
opcode_list, block_list, final_result)
end
| MirTypes.FLOOR(float_to_int, tag, reg_operand, fp_operand) =>
let
val (operation,operation',test,subtract) =
case MachTypes.fp_used of
MachTypes.single => (Sparc_Assembly.FSTOI,Sparc_Assembly.FITOS,Sparc_Assembly.FCMPS,Sparc_Assembly.FSUBS)
| MachTypes.double => (Sparc_Assembly.FDTOI,Sparc_Assembly.FITOD,Sparc_Assembly.FCMPD,Sparc_Assembly.FSUBD)
| MachTypes.extended => (Sparc_Assembly.FXTOI,Sparc_Assembly.FITOX,Sparc_Assembly.FCMPX,Sparc_Assembly.FSUBX)
val rs2 = lookup_fp_operand fp_operand
val rd = lookup_reg_operand reg_operand
val finish_tag = MirTypes.new_tag()
val code_list =
[
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR,MachTypes.global,
MachTypes.G0,Sparc_Assembly.IMM 1),absent,""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SLL,MachTypes.global,
MachTypes.global, Sparc_Assembly.IMM 29),absent,""),
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST,
MachTypes.global,
MachTypes.fp,
Sparc_Assembly.IMM ~4), absent,""),
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDF, MachTypes.fp_global,
MachTypes.fp,
Sparc_Assembly.IMM ~4), absent,
"Save converted value"),
(Sparc_Assembly.CONV_OP(operation', MachTypes.fp_global,
MachTypes.fp_global),
absent, ""),
(Sparc_Assembly.FUNARY(test, rs2, MachTypes.fp_global), absent, ""),
(Sparc_Assembly.FBRANCH(Sparc_Assembly.FBGE, 0), SOME tag, ""),
Sparc_Assembly.nop,
(Sparc_Assembly.FUNARY(Sparc_Assembly.FNEG,
MachTypes.fp_global, MachTypes.fp_global), absent, ""),
(Sparc_Assembly.FUNARY(test, rs2, MachTypes.fp_global), absent, ""),
(Sparc_Assembly.FBRANCH(Sparc_Assembly.FBL, 0), SOME tag, ""),
Sparc_Assembly.nop,
(Sparc_Assembly.CONV_OP(operation, MachTypes.fp_global, rs2),
absent, ""),
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.STF, MachTypes.fp_global, MachTypes.fp, Sparc_Assembly.IMM ~4),
absent,"Save converted value"),
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD,rd, MachTypes.fp,Sparc_Assembly.IMM ~4),
absent, "And reload into destination"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SLL, rd, rd, Sparc_Assembly.IMM 2),
absent, "Tag the result"),
(Sparc_Assembly.FBINARY(subtract, MachTypes.fp_global, rs2, rs2),
absent, ""),
(Sparc_Assembly.FUNARY(test, rs2, MachTypes.fp_global), absent, ""),
(Sparc_Assembly.FBRANCH(Sparc_Assembly.FBGE, 0), SOME finish_tag, ""),
Sparc_Assembly.nop,
(Sparc_Assembly.LOAD_AND_STORE_FLOAT
(Sparc_Assembly.LDF, MachTypes.fp_global,MachTypes.fp,Sparc_Assembly.IMM ~4),
absent, "Load back converted value"),
(Sparc_Assembly.CONV_OP
(operation', MachTypes.fp_global,MachTypes.fp_global),
absent, ""),
(Sparc_Assembly.FUNARY(test, rs2, MachTypes.fp_global), absent, ""),
(Sparc_Assembly.FBRANCH(Sparc_Assembly.FBE, 0), SOME finish_tag, ""),
Sparc_Assembly.nop,
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUB, rd, rd, Sparc_Assembly.IMM 4),
absent, "Adjust the result"),
(Sparc_Assembly.BRANCH_ANNUL (Sparc_Assembly.BA, 0),
SOME finish_tag,
"Done now"),
Sparc_Assembly.nop]
in
(code_list,[], MirTypes.BLOCK (finish_tag,opcode_list)::block_list, final_result)
end
| MirTypes.BRANCH(branch, bl_dest) =>
((case bl_dest of
MirTypes.REG reg =>
[(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
lookup_reg_operand reg, Sparc_Assembly.IMM 0,
Debugger_Types.null_backend_annotation),
absent, "Branch indirect"),
Sparc_Assembly.nop]
| MirTypes.TAG tag =>
[(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, 0),
SOME tag, "Branch relative"),
Sparc_Assembly.nop]),
opcode_list, block_list, final_result)
| MirTypes.TEST(mn, tag, gp_operand, gp_operand') => let
val mn' = case mn of
MirTypes.BNT => Sparc_Assembly.BE
| MirTypes.BTA => Sparc_Assembly.BNE
| MirTypes.BEQ => Sparc_Assembly.BE
| MirTypes.BNE => Sparc_Assembly.BNE
| MirTypes.BHI => Sparc_Assembly.BGU
| MirTypes.BLS => Sparc_Assembly.BLEU
| MirTypes.BHS => Sparc_Assembly.BCC
| MirTypes.BLO => Sparc_Assembly.BCS
| MirTypes.BGT => Sparc_Assembly.BG
| MirTypes.BLE => Sparc_Assembly.BLE
| MirTypes.BGE => Sparc_Assembly.BGE
| MirTypes.BLT => Sparc_Assembly.BL
val redo = not (is_reg gp_operand) andalso (is_reg gp_operand')
val ok = is_reg gp_operand andalso (not (is_reg gp_operand'))
val both = is_reg gp_operand andalso is_reg gp_operand'
val test = case mn of
MirTypes.BTA => Sparc_Assembly.ANDCC
| MirTypes.BNT => Sparc_Assembly.ANDCC
| _ => Sparc_Assembly.SUBCC
in
if redo then
if gp_check_range(gp_operand, true, arith_imm_limit) then let
in
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(test, MachTypes.G0, lookup_gp_operand gp_operand',
make_imm_format3 gp_operand), absent, "test..."),
(Sparc_Assembly.BRANCH_ANNUL (Sparc_Assembly.reverse_branch mn', 0),
SOME tag, ""),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
end
else
([],
MirTypes.UNARY (MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand)
:: MirTypes.TEST (mn, tag, MirTypes.GP_GC_REG MirRegisters.global, gp_operand')
:: opcode_list, block_list, final_result)
else if ok then
if gp_check_range (gp_operand', true, arith_imm_limit) then
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(test, MachTypes.G0, lookup_gp_operand gp_operand, make_imm_format3 gp_operand'), absent, "test..."),
(Sparc_Assembly.BRANCH_ANNUL (mn', 0), SOME tag, ""),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
else
([],
MirTypes.UNARY (MirTypes.MOVE,
MirTypes.GC_REG MirRegisters.global,
gp_operand')
:: MirTypes.TEST (mn, tag, gp_operand, MirTypes.GP_GC_REG MirRegisters.global)
:: opcode_list, block_list, final_result)
else if both then
([(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(test, MachTypes.G0,lookup_gp_operand gp_operand,
Sparc_Assembly.REG (lookup_gp_operand gp_operand')), absent, "test..."),
(Sparc_Assembly.BRANCH_ANNUL (mn', 0), SOME tag, ""),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
else let
fun gp_value(MirTypes.GP_IMM_INT i) = (i, 0)
| gp_value(MirTypes.GP_IMM_ANY i) = (i div 4, i mod 4)
| gp_value(MirTypes.GP_IMM_SYMB symb) = (symbolic_value symb, 0)
| gp_value _ = Crash.impossible "gp_value:non-constant operand"
val (gp_op as (hilhs, lolhs)) = gp_value gp_operand
val (gp_op' as (hirhs, lorhs)) = gp_value gp_operand'
infix less greater lesseq greatereq
fun n less m =
if n >= 0
then if m >=0 then n < m else true
else if m >= 0 then false else n > m
fun n lesseq m = n = m orelse n less m
fun n greater m = not (n lesseq m)
fun n greatereq m = not (n less m)
val pre = case mn of
MirTypes.BGT => (hilhs > hirhs) orelse (hilhs = hirhs andalso lolhs > lorhs)
| MirTypes.BLE => (hilhs < hirhs) orelse (hilhs = hirhs andalso lolhs <= lorhs)
| MirTypes.BGE => (hilhs > hirhs) orelse (hilhs = hirhs andalso lolhs >= lorhs)
| MirTypes.BLT => (hilhs < hirhs) orelse (hilhs = hirhs andalso lolhs < lorhs)
| MirTypes.BNT => Bits.andb (lolhs, lorhs) = 0
| MirTypes.BTA => Bits.andb (lolhs, lorhs) <> 0
| MirTypes.BEQ => gp_op = gp_op'
| MirTypes.BNE => gp_op <> gp_op'
| MirTypes.BHI => (hilhs greater hirhs) orelse (hilhs = hirhs andalso lolhs greater lorhs)
| MirTypes.BLS => (hilhs less hirhs) orelse (hilhs = hirhs andalso lolhs lesseq lorhs)
| MirTypes.BHS => (hilhs greater hirhs) orelse (hilhs = hirhs andalso lolhs greatereq lorhs)
| MirTypes.BLO => (hilhs less hirhs) orelse (hilhs = hirhs andalso lolhs less lorhs)
in
if pre then
([],
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG tag)],
block_list, final_result)
else
([], opcode_list, block_list, final_result)
end
end
| MirTypes.FTEST(fcond_branch, tag, fp_operand,
fp_operand') =>
let
val branch = case fcond_branch of
MirTypes.FBEQ => Sparc_Assembly.FBE
| MirTypes.FBNE => Sparc_Assembly.FBNE
| MirTypes.FBLE => Sparc_Assembly.FBLE
| MirTypes.FBLT => Sparc_Assembly.FBL
val rs1 = lookup_fp_operand fp_operand
val rs2 = lookup_fp_operand fp_operand'
val test_instr = case MachTypes.fp_used of
MachTypes.single => Sparc_Assembly.FCMPS
| MachTypes.double => Sparc_Assembly.FCMPD
| MachTypes.extended => Sparc_Assembly.FCMPX
in
([(Sparc_Assembly.FUNARY(test_instr, rs1, rs2),
absent, "Do the test"),
(Sparc_Assembly.FBRANCH_ANNUL(branch, 0),
SOME tag, "Do the branch"),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
end
| MirTypes.BRANCH_AND_LINK(_, MirTypes.REG reg_operand,debug_information,_) =>
([(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.lr,
lookup_reg_operand reg_operand, Sparc_Assembly.IMM Tags.CODE_OFFSET,
debug_information),
absent, "Call to tagged value"),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
| MirTypes.BRANCH_AND_LINK(_, MirTypes.TAG tag,debug_info,_) =>
([(Sparc_Assembly.Call (Sparc_Assembly.CALL, 0,debug_info),
SOME tag, "Call"),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
| MirTypes.TAIL_CALL(_, bl_dest,_) =>
let
val restore =
if needs_preserve then
(Sparc_Assembly.SAVE_AND_RESTORE
(Sparc_Assembly.RESTORE, MachTypes.G0,
MachTypes.G0, Sparc_Assembly.IMM 0),
absent,
"Restore in delay slot")
else
Sparc_Assembly.nop
val fp_restore =
if needs_preserve then restore_fps else []
in
(fp_restore @
[(case bl_dest of
MirTypes.REG reg =>
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
lookup_reg_operand reg, Sparc_Assembly.IMM Tags.CODE_OFFSET,
Debugger_Types.null_backend_annotation),
absent, "Branch indirect")
| MirTypes.TAG tag =>
(Sparc_Assembly.BRANCH(Sparc_Assembly.BA, 0),
SOME tag, "Branch relative (tail call)")
), restore],
opcode_list, block_list, final_result)
end
| MirTypes.SWITCH(computed_goto, reg_operand, tag_list) =>
let
val reg = lookup_reg_operand reg_operand
val _ =
if reg = MachTypes.lr andalso not needs_preserve then
Crash.impossible "SWITCH from lr in leaf case"
else
()
in
if length tag_list <= 2 then
let
val (numbered_tag_list, _) =
Lists.number_from(tag_list, 0, 4, fn x=> x)
fun do_tests(done, []) = rev done
| do_tests(done, (tag, imm) :: (rest as _ :: _)) =
do_tests((Sparc_Assembly.BRANCH
(Sparc_Assembly.BE, 0),
SOME tag, "Do the branch") ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC, MachTypes.G0,
reg, Sparc_Assembly.IMM imm),
absent, "Do the test") :: done, rest)
| do_tests(done, (tag, imm) :: rest) =
do_tests((Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME tag, "Do the branch") ::
(Sparc_Assembly.nop_code, absent,
"No test required in final case") ::
done, rest)
in
(do_tests([], numbered_tag_list),
opcode_list, block_list, final_result)
end
else
let
val switch_to_global = reg = MachTypes.lr
val final_instr =
if switch_to_global then
move_reg(MachTypes.lr, MachTypes.global)
else
Sparc_Assembly.nop
val reg =
if switch_to_global then MachTypes.global else reg
val instrs =
(Sparc_Assembly.Call (Sparc_Assembly.CALL, 2, Debugger_Types.null_backend_annotation),
absent, "Call self") ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.lr,
MachTypes.lr, Sparc_Assembly.IMM(4*4)),
absent,
"Offset to start of table") ::
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
reg, Sparc_Assembly.REG MachTypes.lr,
Debugger_Types.null_backend_annotation), absent,
"Branch into table") ::
final_instr ::
map
(fn tag =>
(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0), SOME tag, ""))
tag_list
val instrs =
if switch_to_global then
move_reg(MachTypes.global, MachTypes.lr) :: instrs
else
instrs
in
(instrs, opcode_list, block_list, final_result)
end
end
| MirTypes.ALLOCATE_STACK(allocate, reg_operand, alloc_size,
SOME fp_offset) =>
(if alloc_size + fp_offset > gc_stack_alloc_size then
Crash.impossible("Stack allocation of " ^
Int.toString alloc_size ^
" at offset " ^
Int.toString fp_offset ^
" requested, in total area of only " ^
Int.toString
gc_stack_alloc_size ^
"\n")
else();
case allocate of
MirTypes.ALLOC =>
([],
MirTypes.BINARY(MirTypes.SUBU, reg_operand,
MirTypes.GP_GC_REG MirRegisters.fp,
MirTypes.GP_IMM_ANY
(gc_stack_alloc_offset +
4 * (fp_offset + alloc_size) - Tags.PAIRPTR)) ::
opcode_list, block_list, final_result)
| _ => Crash.impossible"ALLOCATE_STACK strange allocate")
| MirTypes.ALLOCATE_STACK _ =>
Crash.impossible"ALLOCATE_STACK with no offset from fp"
| MirTypes.DEALLOCATE_STACK _ =>
([], opcode_list, block_list, final_result)
| MirTypes.ALLOCATE(allocate, reg_operand, gp_operand) =>
let
val rd = lookup_reg_operand reg_operand
val _ =
if rd = MachTypes.lr then Crash.impossible "ALLOC into lr" else ()
val (link, gc_entry) =
if needs_preserve then
(MachTypes.lr, Sparc_Assembly.IMM (4 * Implicit_Vector.gc))
else
(MachTypes.gc2, Sparc_Assembly.IMM (4 * Implicit_Vector.gc_leaf))
val needs_unaligned_zero =
case allocate of
MirTypes.ALLOC_STRING => false
| MirTypes.ALLOC_BYTEARRAY => false
| _ => true
val allocation =
case gp_operand of
MirTypes.GP_IMM_INT size =>
let
val (bytes, primary, aligned, header) =
case allocate of
MirTypes.ALLOC =>
if size = 2 then
(8, Tags.PAIRPTR, true, 0)
else
(8 * ((size+2) div 2), Tags.POINTER,
size mod 2 <> 0, 64*size+Tags.RECORD)
| MirTypes.ALLOC_VECTOR =>
(8 * ((size+2) div 2), Tags.POINTER,
size mod 2 <> 0, 64*size+Tags.RECORD)
| MirTypes.ALLOC_STRING =>
(((size+11) div 8) * 8,
Tags.POINTER, true, 64*size+Tags.STRING)
| MirTypes.ALLOC_REAL =>
(case MachTypes.fp_used
of MachTypes.single => Crash.unimplemented "ALLOC_REAL single"
| MachTypes.extended => Crash.unimplemented "ALLOC_REAL extended"
| MachTypes.double =>
(16, Tags.POINTER, true,
64*(16 - 4) + Tags.BYTEARRAY))
| MirTypes.ALLOC_REF =>
(8 + 8*((size+2) div 2),
Tags.REFPTR, size mod 2 <> 0, 64*size+Tags.ARRAY)
| MirTypes.ALLOC_BYTEARRAY =>
(((size+11) div 8) * 8, Tags.REFPTR, true,
64*size+Tags.BYTEARRAY)
val header_code =
if header = 0 then [] else
load_large_number_into_register
(MachTypes.global, MirTypes.GP_IMM_ANY header) @
[(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.global, rd,
Sparc_Assembly.IMM (~primary)),
absent, "Initialise header")]
val (high, low) = split_int (MirTypes.GP_IMM_ANY bytes)
in
if high = 0 then
inline_allocate
(rd, primary, Sparc_Assembly.IMM bytes, not needs_preserve,
(if aligned orelse not needs_unaligned_zero then
header_code
else
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.G0, rd,
Sparc_Assembly.IMM (bytes - primary - 4)),
absent, "Zero unaligned extra word") ::
header_code))
else
let val load_global1 =
(load_large_number_into_register
(MachTypes.global, MirTypes.GP_IMM_ANY bytes))
val load_global2 =
(load_large_number_into_register
(MachTypes.global, MirTypes.GP_IMM_ANY (bytes - primary - 4)))
in
load_global1 @
inline_allocate
(rd, primary,
Sparc_Assembly.REG MachTypes.global,
not needs_preserve,
if aligned orelse not needs_unaligned_zero then
header_code
else
load_global2 @
((Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.G0, rd,
Sparc_Assembly.REG MachTypes.global),
absent, "Zero unaligned extra word")::
header_code))
end
end
| MirTypes.GP_GC_REG _ =>
let
val rs = lookup_gp_operand gp_operand
val _ =
if rs = MachTypes.lr then Crash.impossible"ALLOC from lr" else ()
val (primary, secondary, length_code) =
case allocate of
MirTypes.ALLOC => Crash.unimplemented "ALLOC variable size"
| MirTypes.ALLOC_VECTOR =>
(Tags.POINTER,Tags.RECORD,
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, rs, Sparc_Assembly.IMM (4+7)),
absent, "Calculate length of record"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ANDN, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM 7),
absent, "Calculate aligned size in bytes")])
| MirTypes.ALLOC_STRING =>
(Tags.POINTER, Tags.STRING,
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SRL, MachTypes.global, rs, Sparc_Assembly.IMM 2),
absent, "Calculate length of string"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM (4+7)),
absent, ""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ANDN, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM 7),
absent, "Calculate aligned size in bytes")])
| MirTypes.ALLOC_REAL => Crash.unimplemented "ALLOC_REAL variable size"
| MirTypes.ALLOC_REF =>
(Tags.REFPTR, Tags.ARRAY,
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, rs, Sparc_Assembly.IMM (12+7)),
absent, "Calculate length of Array"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ANDN, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM 7),
absent, "Calculate aligned size in bytes")])
| MirTypes.ALLOC_BYTEARRAY =>
(Tags.REFPTR, Tags.BYTEARRAY,
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SRL, MachTypes.global, rs, Sparc_Assembly.IMM 2),
absent, "Calculate length of ByteArray"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM (4+7)),
absent, ""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ANDN, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM 7),
absent, "Calculate aligned size in bytes")])
val header_code =
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SLL, MachTypes.global, rs, Sparc_Assembly.IMM 4),
absent, ""),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, MachTypes.global, Sparc_Assembly.IMM secondary),
absent, "Calculate header tag"),
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.global, rd, Sparc_Assembly.IMM (~primary)),
absent, "Initialise header tag")]
in
length_code @
inline_allocate
(rd, primary, Sparc_Assembly.REG MachTypes.global,
not needs_preserve,
if needs_unaligned_zero
then
length_code @
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.global, MachTypes.global, Sparc_Assembly.REG rd),
absent, "Calculate end of object"),
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.G0, MachTypes.global, Sparc_Assembly.IMM (~4 - primary)),
absent, "Zero last word in case it's unaligned")] @
header_code
else header_code)
end
| _ => Crash.impossible "Strange parameter to ALLOCATE"
in
(allocation, opcode_list, block_list, final_result)
end
| MirTypes.ADR(adr, reg_operand, tag) =>
let
val rd = lookup_reg_operand reg_operand
val _ =
if rd = MachTypes.lr then Crash.impossible "ADR into lr" else ()
in
(case adr of
MirTypes.LEA =>
[(Sparc_Assembly.Call
(Sparc_Assembly.CALL, 2, Debugger_Types.null_backend_annotation),
absent, "Call self"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, rd,
MachTypes.lr, Sparc_Assembly.IMM 4),
SOME tag, "Update gc pointer")]
| MirTypes.LEO =>
[(Sparc_Assembly.LOAD_OFFSET
(Sparc_Assembly.LEO, rd, 0),
SOME tag,
"Get offset of tag from procedure start")],
opcode_list, block_list, final_result)
end
| MirTypes.INTERCEPT =>
(trace_dummy_instructions, opcode_list, block_list, final_result)
| MirTypes.INTERRUPT =>
let
val continue_tag = MirTypes.new_tag()
val check_instrs =
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADDCC, MachTypes.G0,
MachTypes.stack_limit, Sparc_Assembly.IMM 1),
NONE, "check for interrupt"),
(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BNE, 0),
SOME continue_tag, "branch if no interrupt"),
Sparc_Assembly.nop]
val continue =
[(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, 0),
SOME continue_tag, "branch if no interrupt"),
Sparc_Assembly.nop]
val irupt_code =
if needs_preserve then
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit,
Sparc_Assembly.IMM (4 * Implicit_Vector.event_check)),
absent, "Get address of event check") ::
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.lr,
MachTypes.global, Sparc_Assembly.IMM 0,
Debugger_Types.null_backend_annotation),
absent, "Do event_check") ::
Sparc_Assembly.nop :: continue
else
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit,
Sparc_Assembly.IMM (4 * Implicit_Vector.event_check_leaf)),
absent, "Get address of event check") ::
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.global,
MachTypes.global, Sparc_Assembly.IMM 0,
Debugger_Types.null_backend_annotation),
absent, "Do event_check_leaf") ::
Sparc_Assembly.nop :: continue
in
(check_instrs @ irupt_code, [],
MirTypes.BLOCK(continue_tag, opcode_list) :: block_list,
final_result)
end
| MirTypes.ENTER _ =>
if needs_preserve then
let
val gc_stack_slots =
(if stack_need_init then
gc_stack_alloc_size
else
0) +
(if spills_need_init then
gc_spill_size
else
0)
val top_tag = MirTypes.new_tag()
val end_tag = MirTypes.new_tag()
val clean_start =
if stack_need_init then
register_save_size
else
register_save_size + 4 * gc_stack_alloc_size
val (clean_stack, opcodes, block) =
if gc_stack_slots <= 10 then
if gc_stack_slots = 0 then
(false,
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME end_tag,
"Finish cleaning stack"),
Sparc_Assembly.nop],
(top_tag, []))
else
(false,
n_stores(clean_start, gc_stack_slots, end_tag),
(top_tag, []))
else
let
val branch_out =
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME top_tag,
"")]
val (clean_start, extra) =
if clean_start mod 8 = 4 then
(clean_start - 4, 4)
else
(clean_start, 0)
val load_limit =
let
val the_limit =
let
val gc_stack_size =
4 * gc_stack_slots + extra
in
if gc_stack_size mod 8 = 0 then
gc_stack_size
else
gc_stack_size+4
end
in
if check_range(the_limit, true,
arith_imm_limit) then
move_imm(MachTypes.global,
the_limit) :: branch_out
else
load_large_number_into_register
(MachTypes.global, MirTypes.GP_IMM_ANY the_limit) @
branch_out
end
val load_start =
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.ADD, MachTypes.O2,
MachTypes.sp, Sparc_Assembly.IMM clean_start),
absent,
"") ::
load_limit
val store_loop =
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC, MachTypes.global,
MachTypes.global, Sparc_Assembly.IMM 8),
absent, "Update counter"),
(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BGE, 0),
SOME top_tag,
"Branch if not finished"),
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.STD, MachTypes.G0,
MachTypes.global,
Sparc_Assembly.REG MachTypes.O2),
absent,
"Initialise a stack slot (delay slot)"),
(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME end_tag, ""),
Sparc_Assembly.nop]
in
(true, load_start, (top_tag, store_loop))
end
val (opcode_list, block_list, final_result) =
if clean_stack then
([],
(MirTypes.BLOCK(end_tag, opcode_list)) ::
block_list,
block :: final_result)
else (opcode_list, block_list, final_result)
val ov_tag = MirTypes.new_tag()
val non_ov_tag = MirTypes.new_tag()
val join_tag = MirTypes.new_tag()
val final_result = (join_tag, opcodes) :: final_result
val immediate_size =
check_range(frame_size, true, arith_imm_limit)
val test_opcodes =
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BLEU, 0),
SOME non_ov_tag,
"Unsigned stack overflow test"),
Sparc_Assembly.nop,
(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME ov_tag, ""),
Sparc_Assembly.nop]
val check_and_test_opcodes =
if non_save_frame_size = 0 then
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC, MachTypes.G0,
MachTypes.stack_limit, Sparc_Assembly.REG MachTypes.sp),
absent,
"Compare stack size") ::
test_opcodes
else
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUB,
MachTypes.global,
MachTypes.sp,
if immediate_size then
Sparc_Assembly.IMM frame_size
else
Sparc_Assembly.REG MachTypes.G7),
absent, "Check the stack for underflow") ::
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUBCC, MachTypes.G0,
MachTypes.stack_limit,
Sparc_Assembly.REG(MachTypes.global)),
absent,
"Compare stack size") ::
test_opcodes
val check_for_stack_overflow_wrap =
if immediate_size then
check_and_test_opcodes
else
load_large_number_into_register
(MachTypes.G7, MirTypes.GP_IMM_ANY frame_size) @
check_and_test_opcodes
val post_ov_code =
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME non_ov_tag, ""),
Sparc_Assembly.nop]
val post_ov_code =
if immediate_size then
post_ov_code
else
load_large_number_into_register
(MachTypes.G7, MirTypes.GP_IMM_ANY frame_size) @
post_ov_code
val post_ov_code =
(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit,
Sparc_Assembly.IMM (4 * Implicit_Vector.extend)),
absent, "Get address of stack_overflow") ::
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.global,
MachTypes.global,Sparc_Assembly.IMM 0,
Debugger_Types.null_backend_annotation),
absent, "Do stack_overflow") ::
Sparc_Assembly.nop ::
post_ov_code
val ov_tag_code =
if immediate_size then
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, MachTypes.G7,
MachTypes.G0, Sparc_Assembly.IMM frame_size),
absent, "Set the required size in G7") :: post_ov_code
else
post_ov_code
in
(check_for_stack_overflow_wrap,
[],
(case opcode_list of
[] => block_list
| _ =>
MirTypes.BLOCK(end_tag,opcode_list) ::
block_list),
(non_ov_tag,
(if immediate_size
then []
else
[(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.SUB, MachTypes.G7,
MachTypes.G0, Sparc_Assembly.REG(MachTypes.G7)),
absent, "Negate the frame size")]) @
((Sparc_Assembly.SAVE_AND_RESTORE
(Sparc_Assembly.SAVE, MachTypes.sp,
MachTypes.sp,
if immediate_size
then Sparc_Assembly.IMM(~frame_size)
else Sparc_Assembly.REG MachTypes.G7),
absent, "New frame") ::
(save_fps @
[(Sparc_Assembly.BRANCH_ANNUL
(Sparc_Assembly.BA, 0),
SOME join_tag, ""),
Sparc_Assembly.nop]))) ::
(ov_tag,ov_tag_code) ::
final_result)
end
else
([], opcode_list, block_list, final_result)
| MirTypes.RTS =>
(if needs_preserve then
restore_fps @
[(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
MachTypes.after_preserve MachTypes.lr,
Sparc_Assembly.IMM 8,
Debugger_Types.null_backend_annotation),
absent, "Scheduled return"),
(Sparc_Assembly.SAVE_AND_RESTORE
(Sparc_Assembly.RESTORE, MachTypes.G0, MachTypes.G0,
Sparc_Assembly.IMM 0),
absent, "Restore in the delay slot")]
else
[(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
MachTypes.lr, Sparc_Assembly.IMM 8,
Debugger_Types.null_backend_annotation),
absent, "Ordinary return"),
Sparc_Assembly.nop],
opcode_list, block_list, final_result)
| MirTypes.NEW_HANDLER(handler_frame, tag) =>
([(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, MachTypes.handler,
lookup_reg_operand handler_frame,
Sparc_Assembly.IMM(~1)),
absent,
"Insert pointer to previous handler"),
move_reg(MachTypes.handler, lookup_reg_operand handler_frame)],
opcode_list, block_list, final_result)
| MirTypes.OLD_HANDLER =>
([(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.handler, MachTypes.handler,
Sparc_Assembly.IMM(~1)),
absent,
"Restore old handler")], opcode_list, block_list, final_result)
| MirTypes.RAISE reg =>
let
val code =
if needs_preserve then
[(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit,
Sparc_Assembly.IMM (4 * Implicit_Vector.raise_code)),
absent, "Get the handler"),
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.lr,
MachTypes.G0, Sparc_Assembly.REG MachTypes.global,
Debugger_Types.null_backend_annotation),
absent, "Raise"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, MachTypes.caller_arg,
lookup_reg_operand reg,
Sparc_Assembly.REG MachTypes.G0),
absent, "Move arg to raise into arg reg")]
else
[(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit,
Sparc_Assembly.IMM (4 * Implicit_Vector.leaf_raise_code)),
absent, "Get the handler"),
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.G0,
MachTypes.G0, Sparc_Assembly.REG MachTypes.global,
Debugger_Types.null_backend_annotation),
absent, "Raise"),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.OR, MachTypes.caller_arg,
lookup_reg_operand reg,
Sparc_Assembly.REG MachTypes.G0),
absent,"Move arg to raise into arg reg")]
in
(code, opcode_list, block_list, final_result)
end
| MirTypes.COMMENT string =>
Crash.impossible"MirTypes.COMMENT not filtered out"
| MirTypes.CALL_C =>
([(Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, MachTypes.global,
MachTypes.implicit, Sparc_Assembly.IMM (4 * Implicit_Vector.external)),
absent, "Get address of callc"),
(Sparc_Assembly.JUMP_AND_LINK
(Sparc_Assembly.JMPL, MachTypes.lr,
MachTypes.global,Sparc_Assembly.IMM 0, Debugger_Types.null_backend_annotation),
absent, "Do call_c"), Sparc_Assembly.nop],
opcode_list, block_list, final_result)
in
do_everything
(needs_preserve, tag, opcode_list,
Sexpr.CONS(done, Sexpr.ATOM result_list), new_blocks,
new_final_result)
end
in
do_everything(needs_preserve, tag, Lists.filter_outp is_comment opcodes,
Sexpr.NIL, rest, [])
end
fun exit_block [] = NONE
| exit_block((block as MirTypes.BLOCK(tag, opcode_list)) :: rest) =
if Lists.exists
(fn MirTypes.RTS => true | _ => false)
opcode_list
then SOME block
else exit_block rest
fun small_exit_block(MirTypes.BLOCK(tag,opcode_list)) =
let
fun less_than_three_opcodes_that_are_not_comments([],occ) = true
| less_than_three_opcodes_that_are_not_comments(MirTypes.COMMENT _ :: rest,occ) =
less_than_three_opcodes_that_are_not_comments(rest,occ)
| less_than_three_opcodes_that_are_not_comments(_,2) = false
| less_than_three_opcodes_that_are_not_comments(h::t,occ) =
less_than_three_opcodes_that_are_not_comments(t,occ+1)
in
less_than_three_opcodes_that_are_not_comments(opcode_list,0)
end
fun append_small_exit(MirTypes.BLOCK(tag, opcode_list), block_list) =
let
fun do_block(block as MirTypes.BLOCK(tag', opc_list)) =
if Lists.exists
(fn (MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG t)) => tag = t
| _ => false)
opc_list then
let
val opc' = rev opc_list
fun get_new_opc_list((comm as MirTypes.COMMENT _) :: rest) =
comm :: get_new_opc_list rest
| get_new_opc_list(MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG t) ::
rest) =
if t = tag then rest
else
Crash.impossible"get_new_opc fails to find proper branch"
| get_new_opc_list _ =
Crash.impossible"get_new_opc fails to find proper branch"
val new_opc = get_new_opc_list opc'
fun rev_app([], x) = x
| rev_app(y, []) = y
| rev_app(y :: ys, x) = rev_app(ys, y :: x)
in
MirTypes.BLOCK(tag', rev_app(new_opc, opcode_list))
end
else
block
in
map do_block block_list
end
fun proc_cg(MirTypes.PROC
(procedure_name,
proc_tag,
MirTypes.PROC_PARAMS {spill_sizes, stack_allocated, ...},
block_list,runtime_env)) =
let
val exit_block = exit_block block_list
val block_list =
case exit_block of
NONE => block_list
| SOME exit_block =>
if small_exit_block exit_block then
append_small_exit(exit_block, block_list)
else
block_list
fun define_fp(map, MirTypes.FP_REG fp) =
case MirTypes.FP.Map.tryApply'(map, fp) of
NONE => MirTypes.FP.Map.define(map, fp, true)
| _ => map
fun get_fps_from_opcode(map, MirTypes.TBINARYFP(_, _, fp1, fp2, fp3)) =
define_fp(define_fp(define_fp(map, fp1), fp2), fp3)
| get_fps_from_opcode(map, MirTypes.TUNARYFP(_, _, fp1, fp2)) =
define_fp(define_fp(map, fp1), fp2)
| get_fps_from_opcode(map, MirTypes.BINARYFP(_, fp1, fp2, fp3)) =
define_fp(define_fp(define_fp(map, fp1), fp2), fp3)
| get_fps_from_opcode(map, MirTypes.UNARYFP(_, fp1, fp2)) =
define_fp(define_fp(map, fp1), fp2)
| get_fps_from_opcode(map, MirTypes.STOREFPOP(_, fp1, _, _)) =
define_fp(map, fp1)
| get_fps_from_opcode(map, MirTypes.REAL(_, fp1, _)) =
define_fp(map, fp1)
| get_fps_from_opcode(map, MirTypes.FLOOR(_, _, _, fp1)) =
define_fp(map, fp1)
| get_fps_from_opcode(map, MirTypes.FTEST(_, _, fp1, fp2)) =
define_fp(define_fp(map, fp1), fp2)
| get_fps_from_opcode(map, _) = map
fun get_fps_from_block(map, MirTypes.BLOCK(_, instr_list)) =
Lists.reducel get_fps_from_opcode (map, instr_list)
val fp = MirTypes.FP.Map.domain(Lists.reducel get_fps_from_block (MirTypes.FP.Map.empty, block_list))
val fps = Set.list_to_set(map (fn r => MirTypes.FP.Map.apply'(fp_map, r)) fp)
val fps_to_preserve =
Set.set_to_list(Set.setdiff(fps,
#fp MachSpec.corrupted_by_callee))
val fp_save_size = length fps_to_preserve
val preserve_fps = fp_save_size <> 0
fun check_instr(MirTypes.BRANCH_AND_LINK _) = true
| check_instr MirTypes.CALL_C = true
| check_instr(MirTypes.NEW_HANDLER _) = true
| check_instr(MirTypes.ADR _) = true
| check_instr (MirTypes.REAL _) = true
| check_instr (MirTypes.FLOOR _) = true
| check_instr (MirTypes.STACKOP _) = true
| check_instr _ = false
fun check_instr_block(MirTypes.BLOCK(_, instr_list)) =
Lists.exists check_instr instr_list
val stack_opt = stack_allocated
val stack_extra = case stack_opt of
SOME stack_extra => stack_extra
| _ => Crash.impossible"Stack size missing to mach_cg"
fun check_reg_op(MirTypes.GC_REG r) =
MachTypes.check_reg(MirTypes.GC.Map.apply'(gc_map, r))
| check_reg_op(MirTypes.NON_GC_REG r) =
MachTypes.check_reg(MirTypes.NonGC.Map.apply'(non_gc_map, r))
fun check_gp_op(MirTypes.GP_GC_REG r) =
MachTypes.check_reg(MirTypes.GC.Map.apply'(gc_map, r))
| check_gp_op(MirTypes.GP_NON_GC_REG r) =
MachTypes.check_reg(MirTypes.NonGC.Map.apply'(non_gc_map, r))
| check_gp_op(MirTypes.GP_IMM_INT _) = ()
| check_gp_op(MirTypes.GP_IMM_ANY _) = ()
| check_gp_op(MirTypes.GP_IMM_SYMB symbolic) =
case symbolic of
MirTypes.GC_SPILL_SIZE => ()
| MirTypes.NON_GC_SPILL_SIZE => ()
| MirTypes.GC_SPILL_SLOT _ => raise MachTypes.NeedsPreserve
| MirTypes.NON_GC_SPILL_SLOT _ => raise MachTypes.NeedsPreserve
| MirTypes.FP_SPILL_SLOT _ => raise MachTypes.NeedsPreserve
fun check_instr_regs(MirTypes.TBINARY(_, _, reg_op, gp_op, gp_op')) =
(check_reg_op reg_op;
check_gp_op gp_op;
check_gp_op gp_op')
| check_instr_regs(MirTypes.BINARY(_, reg_op, gp_op, gp_op')) =
(check_reg_op reg_op;
check_gp_op gp_op;
check_gp_op gp_op')
| check_instr_regs(MirTypes.UNARY(_, reg_op, gp_op )) =
(check_reg_op reg_op;
check_gp_op gp_op)
| check_instr_regs(MirTypes.NULLARY(_, reg_op )) =
(check_reg_op reg_op)
| check_instr_regs(MirTypes.TBINARYFP _) = ()
| check_instr_regs(MirTypes.TUNARYFP _) = ()
| check_instr_regs(MirTypes.BINARYFP _) = ()
| check_instr_regs(MirTypes.UNARYFP _) = ()
| check_instr_regs(MirTypes.STACKOP _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.IMMSTOREOP _) =
Crash.impossible"IMMSTOREOP not supported on sparc"
| check_instr_regs(MirTypes.STOREOP(_, reg_op, reg_op', gp_op )) =
(check_reg_op reg_op;
check_reg_op reg_op';
check_gp_op gp_op)
| check_instr_regs(MirTypes.STOREFPOP(_, _, reg_op, gp_op )) =
(check_reg_op reg_op;
check_gp_op gp_op)
| check_instr_regs(MirTypes.REAL _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.FLOOR _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.BRANCH(_, bl_dest )) =
(case bl_dest of
MirTypes.REG reg_op => (check_reg_op reg_op)
| _ => ())
| check_instr_regs(MirTypes.TEST(_, _, gp_op, gp_op')) =
(check_gp_op gp_op;
check_gp_op gp_op')
| check_instr_regs(MirTypes.FTEST _) = ()
| check_instr_regs(MirTypes.BRANCH_AND_LINK _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.TAIL_CALL(_, bl_dest,_)) =
(case bl_dest of
MirTypes.REG reg_op => (check_reg_op reg_op)
| _ => ())
| check_instr_regs(MirTypes.CALL_C) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.SWITCH _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.ALLOCATE(_, reg_op, gp_op )) =
(check_reg_op reg_op;
check_gp_op gp_op)
| check_instr_regs(MirTypes.ALLOCATE_STACK _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.DEALLOCATE_STACK _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.ADR _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.INTERCEPT) = ()
| check_instr_regs(MirTypes.INTERRUPT) = ()
| check_instr_regs(MirTypes.ENTER _) = ()
| check_instr_regs(MirTypes.RTS) = ()
| check_instr_regs(MirTypes.NEW_HANDLER _) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.OLD_HANDLER) =
raise MachTypes.NeedsPreserve
| check_instr_regs(MirTypes.RAISE reg_op) =
(check_reg_op reg_op)
| check_instr_regs(MirTypes.COMMENT _) = ()
fun check_instr_block_regs(MirTypes.BLOCK(_, instr_list)) =
Lists.iterate check_instr_regs instr_list
val needs_preserve =
not (opt_leaf_fns) orelse
stack_extra <> 0 orelse
preserve_fps orelse
Lists.exists check_instr_block block_list orelse
((Lists.iterate check_instr_block_regs block_list;
false) handle MachTypes.NeedsPreserve => true)
val _ =
if generate_debug_info orelse debug_variables orelse generate_moduler
then
debug_map := Debugger_Types.set_proc_data (procedure_name,
not needs_preserve,
true,
runtime_env,
!debug_map)
else ()
val _ =
if needs_preserve then ()
else
diagnostic_output 3
(fn _ => [procedure_name, " is leaf\n"])
fun move_first (_, []) =
Crash.impossible "move_first"
| move_first (L, (t, code) :: rest) =
if t = proc_tag then (t, code) :: (L @ rest)
else move_first ((t, code) :: L, rest)
fun block_needs_fp_spare(MirTypes.BLOCK(_, opc_list)) =
let
fun opc_needs_fp_spare [] = false
| opc_needs_fp_spare(MirTypes.REAL _ :: _) = true
| opc_needs_fp_spare(MirTypes.FLOOR _ :: _) = true
| opc_needs_fp_spare(_ :: rest) = opc_needs_fp_spare rest
in
opc_needs_fp_spare opc_list
end
fun proc_needs_fp_spare [] = false
| proc_needs_fp_spare(block :: block_list) =
block_needs_fp_spare block orelse proc_needs_fp_spare block_list
val needs_fp_spare = proc_needs_fp_spare block_list
val spills_opt = spill_sizes
val (gc_spill_size, non_gc_spill_size, fp_spill_size) =
case spills_opt of
SOME{gc = gc_spill_size,
non_gc = non_gc_spill_size,
fp = fp_spill_size} =>
(gc_spill_size, non_gc_spill_size, fp_spill_size)
| _ => Crash.impossible"Spill sizes missing to mach_cg"
val non_gc_spill_size =
if needs_fp_spare then non_gc_spill_size + 1
else non_gc_spill_size
val float_value_size = case MachTypes.fp_used of
MachTypes.single => 4
| MachTypes.double => 8
| MachTypes.extended => 16
val total_fp_size = fp_spill_size + fp_save_size
val total_gc_size = gc_spill_size + stack_extra
val non_gc_spill_size =
if total_fp_size <> 0 andalso float_value_size <> 4 andalso
non_gc_spill_size mod 2 <> 0 then
non_gc_spill_size + 1
else
non_gc_spill_size
val needs_preserve = needs_preserve orelse needs_fp_spare
val non_gc_stack_size =
non_gc_spill_size * 4 + float_value_size * total_fp_size
val total = non_gc_stack_size + total_gc_size * 4
val non_gc_stack_size =
if total mod 8 = 0 then non_gc_stack_size else non_gc_stack_size + 4
val fp_spill_offset = non_gc_spill_size * 4
val fp_save_offset = fp_spill_offset + fp_spill_size * float_value_size
val gc_spill_offset = non_gc_stack_size
val gc_stack_alloc_offset = gc_spill_offset + gc_spill_size * 4
val register_save_offset = gc_stack_alloc_offset + stack_extra * 4
val stack_layout =
PROC_STACK
{non_gc_spill_size = non_gc_spill_size,
fp_spill_size = fp_spill_size,
fp_save_size = fp_save_size,
gc_spill_size = gc_spill_size,
gc_stack_alloc_size = stack_extra,
register_save_size = 64,
non_gc_spill_offset = 0,
fp_spill_offset = fp_spill_offset,
fp_save_offset = fp_save_offset,
gc_spill_offset = gc_spill_offset,
gc_stack_alloc_offset = gc_stack_alloc_offset,
register_save_offset = register_save_offset,
allow_fp_spare_slot = needs_fp_spare,
float_value_size = float_value_size
}
val block_tree =
Lists.reducel
(fn (map, MirTypes.BLOCK x) => MirTypes.Map.define'(map, x))
(MirTypes.Map.empty, block_list)
val spill_array = MLWorks.Internal.Array.array(gc_spill_size, true)
val stack_array = MLWorks.Internal.Array.array(stack_extra, true)
fun ok_store MirTypes.ST = true
| ok_store MirTypes.STREF = true
| ok_store _ = false
fun analyse_instr(_, MirTypes.TBINARY _) = ([], true)
| analyse_instr(_, MirTypes.TBINARYFP _) = ([], true)
| analyse_instr(_, MirTypes.TUNARYFP _) = ([], true)
| analyse_instr(_, MirTypes.REAL _) = ([], true)
| analyse_instr(_, MirTypes.FLOOR _) = ([], true)
| analyse_instr(_, MirTypes.BRANCH_AND_LINK _) = ([], true)
| analyse_instr(_, MirTypes.TAIL_CALL _) = ([], true)
| analyse_instr(_, MirTypes.CALL_C) = ([], true)
| analyse_instr(_, MirTypes.SWITCH _) = ([], true)
| analyse_instr(_, MirTypes.RAISE _) = ([], true)
| analyse_instr(_, MirTypes.INTERCEPT) = ([], true)
| analyse_instr(_, MirTypes.INTERRUPT) = ([], true)
| analyse_instr(_, MirTypes.BRANCH _) = ([], true)
| analyse_instr(_, MirTypes.TEST _) = ([], true)
| analyse_instr(_, MirTypes.FTEST _) = ([], true)
| analyse_instr(_, MirTypes.ALLOCATE _) = ([], true)
| analyse_instr(a as (list, x),
MirTypes.ALLOCATE_STACK(MirTypes.ALLOC, r, size, offset)) =
if x then a else
let
val offset = case offset of
SOME x => x
| _ => Crash.impossible"offset missing from stack allocation"
in
((r, stack_extra - offset - size) :: list, false)
end
| analyse_instr(a as (list, x), MirTypes.STOREOP(store, _, r2, g)) =
if x orelse not(ok_store store) then a else
(case g of
MirTypes.GP_IMM_SYMB symb =>
(case symb of
MirTypes.GC_SPILL_SLOT _ =>
let
val offset = symb_value stack_layout symb
val i = gc_spill_size + (offset + gc_spill_offset) div 4
val _ = MLWorks.Internal.Array.update(spill_array, gc_spill_size -1 - i, false)
in
a
end
| _ => a)
| MirTypes.GP_IMM_ANY i =>
(let
val start = Lists.assoc(r2, list)
val offset = (i + 1) div 4
val _ = MLWorks.Internal.Array.update(stack_array, offset+start, false)
in
a
end
handle Lists.Assoc => a)
| _ => a)
| analyse_instr(x, _) = x
fun analyse_block block_tag =
let
val instrs = MirTypes.Map.tryApply'(block_tree, block_tag)
in
case instrs of
SOME instrs =>
Lists.reducel analyse_instr (([], false), instrs)
| _ => ([], false)
end
val _ = analyse_block proc_tag
fun needs_init(a, b) = a orelse b
val spills_need_init =
MLWorks.Internal.ExtendedArray.reducel needs_init (false, spill_array)
val stack_need_init =
MLWorks.Internal.ExtendedArray.reducel needs_init (false, stack_array)
val code =
move_first([], do_blocks(needs_preserve,
block_list,
stack_layout,
spills_need_init,
stack_need_init,
fps_to_preserve))
val code_len =
Lists.reducel op +
(0, map (fn (_, opcodes) => length opcodes) code)
val padded_name =
let
fun generate_nulls 0 = ""
| generate_nulls n = String.str (chr(0)) ^ generate_nulls (n-1)
fun normalise_to_four_bytes (x) =
x ^ generate_nulls((4 - ((size x) mod 4)) mod 4)
in
normalise_to_four_bytes(procedure_name ^ String.str(chr(0)))
end
in
{code=(proc_tag, code),
non_gc_area_size=non_gc_stack_size,
name=procedure_name,
padded_name=padded_name,
leaf=not needs_preserve,
parms=0}
end
fun remove_redundant_loads(acc, []) = rev acc
| remove_redundant_loads(acc, arg as [x]) = rev(x :: acc)
| remove_redundant_loads(acc, (ins1 as (Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.ST, rd1, rs11, rs12),
tag1, comment1)) ::
(ins2 as (Sparc_Assembly.LOAD_AND_STORE
(Sparc_Assembly.LD, rd2, rs21, rs22),
tag2, comment2)) :: rest) =
if rs11 = rs21 andalso rs12 = rs22 andalso rd1 = rd2 then
(diagnostic_output 3
(fn _ => ["Removing redundant load after store\n"]);
remove_redundant_loads(acc, ins1 :: rest))
else
remove_redundant_loads(ins2 :: ins1 :: acc, rest)
| remove_redundant_loads(acc, x :: rest) = remove_redundant_loads(x :: acc, rest)
val remove_redundant_loads = fn x => remove_redundant_loads([], x)
fun remove_redundant_loads_from_block(tag, opcode_list) =
(tag, remove_redundant_loads opcode_list)
fun remove_redundant_loads_from_proc(tag, block_list) =
(tag, map remove_redundant_loads_from_block block_list)
fun list_proc_cg proc_list =
let
fun print_unscheduled_code((tag, block_list),name) =
let
fun print_block(tag, opcode_list) =
let
fun print_opcode(opcode, tag_opt, comment) =
Print.print(
Sparc_Assembly.print opcode ^
(case tag_opt of
SOME tag =>
" tag " ^ MirTypes.print_tag tag
| NONE => " no tag") ^
" ; " ^ comment ^ "\n")
in
(Print.print("Block tag " ^ MirTypes.print_tag tag ^ "\n");
map print_opcode opcode_list)
end
in
(Print.print("Procedure entry tag " ^ MirTypes.print_tag tag ^
" " ^ name ^
"\n");
map print_block block_list)
end
val temp_code_list =
Timer.xtime
("main proc_cg stage", !do_timings,
fn () => map proc_cg proc_list)
val code_list =
map (fn tuple=>remove_redundant_loads_from_proc (#code(tuple))) temp_code_list
val procedure_name_list = map #name temp_code_list
val leaf_list = map #leaf temp_code_list
val stack_parameters = map #parms temp_code_list
val code_list' = code_list
val _ = diagnostic_output 3
(fn _ => ["Unscheduled code\n"])
val _ = diagnostic_output 3
(fn _ => (ignore(map print_unscheduled_code
(Lists.zip(code_list',procedure_name_list)));
[]))
fun is_static s =
let
fun is_prefix n =
let
val l = size n
in
size s > l andalso MLWorks.String.substring (s,0,l) = n
end
in
is_prefix "<Setup>" orelse is_prefix "Functor "
end
fun do_reschedule code_list =
let
val code_list' =
Timer.xtime
("rescheduling blocks", !do_timings,
fn () =>
map
(fn ((proc_tag, proc),name) =>
let
val static = is_static name
in
(proc_tag, map
(fn (tag, x) => (tag, Sparc_Schedule.reschedule_block (static,x)))
proc)
end)
(Lists.zip (code_list,procedure_name_list)))
val _ = diagnostic_output 3 (fn _ => ["Rescheduled at block level, now doing proc level\n"])
val _ = diagnostic_output 3 (fn _ => ["Result so far\n"])
val _ = diagnostic_output 3 (fn _ => (ignore(map print_unscheduled_code
(Lists.zip(code_list',procedure_name_list)));
[]))
val code_list'' =
Timer.xtime
("rescheduling procs", !do_timings,
fn () => map Sparc_Schedule.reschedule_proc code_list')
in
code_list''
end
fun print_scheduled_code (code_list) =
let
fun print_proc((proc_tag, proc),name) =
let
fun print_block(tag, opcode_list) =
let
fun print_opcode(opcode, tag_opt, comment) =
Print.print(
Sparc_Assembly.print opcode ^
(case tag_opt of
SOME tag =>
" tag " ^ MirTypes.print_tag tag
| NONE => " no tag") ^
" ; " ^ comment ^ "\n")
in
(Print.print("Block tag " ^ MirTypes.print_tag tag ^ " " ^ name ^ "\n");
map print_opcode opcode_list)
end
in
(Print.print("Procedure tag " ^ MirTypes.print_tag proc_tag ^ "\n");
map print_block proc)
end
in
map print_proc code_list
end
val _ = diagnostic_output 3 (fn _ => (["Rescheduling code\n"]))
val new_code_list' =
Timer.xtime
("rescheduling", !do_timings,
fn () => do_reschedule code_list')
val _ = diagnostic_output 3 (fn _ => ["Rescheduled code\n"])
val _ = diagnostic_output 3 (fn _ => (ignore(print_scheduled_code (Lists.zip(new_code_list',procedure_name_list)));
[]))
val _ = diagnostic_output 3 (fn _ => ["Linearising\n"])
val linear_code' =
Timer.xtime
("linearising", !do_timings,
fn () => linearise_list new_code_list')
val nop_offsets = map find_nop_offsets linear_code'
val _ = diagnostic_output 3 (fn _ => ["Linearised\n"])
val nop_instruction =
Sparc_Opcodes.output_opcode
(Sparc_Assembly.assemble (Sparc_Assembly.nop_code))
fun make_tagged_code linear_code =
(map
(fn ((tag, code),{non_gc_area_size, padded_name, ...}) =>
{a_clos=Lists.assoc(tag, loc_refs),
b_spills=non_gc_area_size,
c_saves=0,
d_code=
let
fun do_annotation (debug,count) =
let
val unpadded_name =
let
val s = size padded_name
fun check_index to =
if MLWorks.String.ordof(padded_name,to) = 0
then check_index(to-1)
else MLWorks.String.substring(padded_name,0,to+1)
in
check_index (s-1)
handle MLWorks.String.Substring => ""
| MLWorks.String.Ord => ""
end
in
debug_map := Debugger_Types.add_annotation (unpadded_name,
count,
debug,
!debug_map)
end
fun annotation_points ([],_,res) = rev res
| annotation_points ((inst,_)::t,count,res) =
(case inst of
Sparc_Assembly.JUMP_AND_LINK (_,_,_,_,debug) => do_annotation (debug,count)
| Sparc_Assembly.Call (_,_,debug) => do_annotation (debug,count)
| _ => ();
annotation_points(t,count+4,
Sparc_Opcodes.output_opcode(Sparc_Assembly.assemble inst)::res))
val code =
if generate_debug_info then
concat (annotation_points (code,0,[]))
else
concat
(map
(fn (x, _) =>
Sparc_Opcodes.output_opcode(Sparc_Assembly.assemble x))
code)
val padded_code =
if size code mod 8 = 4
then code ^ nop_instruction
else code
in
padded_code
end})
(Lists.zip(linear_code,temp_code_list)))
handle Lists.Assoc => Crash.impossible"Assoc tagged_code"
handle Lists.Assoc => Crash.impossible"Assoc tagged_code"
val tagged_code' = make_tagged_code linear_code'
in
(Code_Module.WORDSET(Code_Module.WORD_SET
{a_names=procedure_name_list,
b=tagged_code',
c_leafs=leaf_list,
d_intercept=nop_offsets,
e_stack_parameters=stack_parameters}),
Lists.zip(linear_code', procedure_name_list))
end
val (proc_elements, code_list) = Lists.unzip(map list_proc_cg proc_list_list)
val _ =
if ! print_code_size then
print("Normalised code size is " ^
Int.toString
(Lists.reducel
(fn(x,Code_Module.WORDSET(Code_Module.WORD_SET{b=tagged_code', ...})) =>
(Lists.reducel (fn (x,{d_code=y, ...}) => (size y) + x) (x,tagged_code'))
| _ => Crash.impossible "what the ?")
(0,proc_elements)) ^ "\n")
else ()
fun make_external_refs(con, list) =
map (fn (x, y) => con(y, x)) list
val ext_elements = make_external_refs(Code_Module.EXTERNAL, ext_refs)
val ext_vars = make_external_refs(Code_Module.VAR, vars)
val ext_exns = make_external_refs(Code_Module.EXN, exns)
val ext_strs = make_external_refs(Code_Module.STRUCT, strs)
val ext_funs = make_external_refs(Code_Module.FUNCT, funs)
val module =
Code_Module.MODULE(value_elements @
proc_elements @
ext_elements @
ext_vars @
ext_exns @
ext_strs @
ext_funs)
in
((module, !debug_map), code_list)
end
end
;
