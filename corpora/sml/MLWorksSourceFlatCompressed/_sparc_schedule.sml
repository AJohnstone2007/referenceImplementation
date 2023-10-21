require "../utils/_hashset";
require "../utils/crash";
require "../utils/lists";
require "sparc_assembly";
require "sparc_schedule";
functor Sparc_Schedule(
structure Crash : CRASH
structure Lists : LISTS
structure Sparc_Assembly : SPARC_ASSEMBLY
) : SPARC_SCHEDULE =
struct
structure Sparc_Assembly = Sparc_Assembly
structure MirTypes = Sparc_Assembly.MirTypes
structure Set = MirTypes.Set
structure MachTypes = Sparc_Assembly.Sparc_Opcodes.MachTypes
structure NewMap = MirTypes.Map
structure HashSet = HashSet(
structure Crash = Crash
structure Lists = Lists
type element = MirTypes.tag
val eq = MirTypes.equal_tag
val hash = MirTypes.int_of_tag
)
val schedule_limit = 20
val hashset_size = 32
fun refs_for_opcode_list(done, []) = done
| refs_for_opcode_list(done, (_, SOME tag, _) :: rest) =
refs_for_opcode_list(HashSet.add_member(done, tag), rest)
| refs_for_opcode_list(done, _ :: rest) = refs_for_opcode_list(done, rest)
fun refs_for_block (done, (tag, opcode_list)) =
refs_for_opcode_list(done, opcode_list)
fun refs_all (tag, block_list) =
Lists.reducel
refs_for_block
(HashSet.add_member(HashSet.empty_set hashset_size, tag), block_list)
val nop_code = Sparc_Assembly.nop_code
val nop = (nop_code, NONE, "")
fun passes_from_sets((first_i_defines, first_i_uses, first_r_defines, first_r_uses),
(second_i_defines, second_i_uses, second_r_defines, second_r_uses)) =
not (Set.intersect (first_i_defines, second_i_defines)) andalso
not (Set.intersect (first_i_defines, second_i_uses)) andalso
not (Set.intersect (first_i_uses, second_i_defines)) andalso
not (Set.intersect (first_r_defines, second_r_defines)) andalso
not (Set.intersect (first_r_defines, second_r_uses)) andalso
not (Set.intersect (first_r_uses, second_r_defines))
fun convert_branch_annul(Sparc_Assembly.BRANCH_ANNUL
(branch, disp), tag_opt, comment) =
(Sparc_Assembly.BRANCH(branch, disp), tag_opt, comment)
| convert_branch_annul(Sparc_Assembly.FBRANCH_ANNUL
(branch, disp), tag_opt, comment) =
(Sparc_Assembly.FBRANCH(branch, disp), tag_opt, comment)
| convert_branch_annul opcode = opcode
fun has_delay(Sparc_Assembly.BRANCH _) = true
| has_delay(Sparc_Assembly.BRANCH_ANNUL _) = true
| has_delay(Sparc_Assembly.FBRANCH _) = true
| has_delay(Sparc_Assembly.FBRANCH_ANNUL _) = true
| has_delay(Sparc_Assembly.Call _) = true
| has_delay(Sparc_Assembly.JUMP_AND_LINK _) = true
| has_delay _ = false
fun remove_doubled_instrs(tag, opcode_list) =
let
fun remove(done, []) = rev done
| remove(done, [x]) = rev(x :: done)
| remove(done, [x, y]) = rev(y :: x :: done)
| remove(done,
(opc as (Sparc_Assembly.BRANCH_ANNUL(opcode, i),
SOME tag, comment)) ::
(other as
(op1 as (opc1, opt1, _)) ::
(op2 as (opc2, opt2, _)) :: rest)) =
(case opcode of
Sparc_Assembly.BA =>
remove(opc :: done, other)
| _ =>
if opc1 = opc2 andalso opt1 = opt2 then
remove(op1 :: (Sparc_Assembly.BRANCH(opcode, i),
SOME tag, comment) :: done, rest)
else
remove(op1 :: opc :: done, op2 :: rest))
| remove(done, x :: rest) = remove(x :: done, rest)
in
(tag, remove([], opcode_list))
end
fun get_tag_and_instr(tag, opcode :: _) = (tag, opcode)
| get_tag_and_instr(_, _) = Crash.impossible"Empty block"
fun find_baa_block(_, []) = Crash.impossible"Empty block"
| find_baa_block(_, (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
_, _) :: rest) =
(case rest of
[] => true
| [(Sparc_Assembly.ARITHMETIC_AND_LOGICAL(Sparc_Assembly.AND,
MachTypes.G0,
MachTypes.G0,
Sparc_Assembly.IMM 0),
_, _)] => true
| _ => Crash.impossible"Unreachable instructions after BAA")
| find_baa_block _ = false
fun get_transitive_dest baa_tags_and_dests =
let
fun g_t_d(visited, tag) =
(case NewMap.tryApply'(baa_tags_and_dests, tag) of
SOME dest_tag =>
(case NewMap.tryApply'(baa_tags_and_dests, dest_tag) of
SOME _ =>
if not(Lists.member(tag, visited)) then
g_t_d(tag :: visited, dest_tag)
else dest_tag
| NONE => dest_tag)
| NONE => Crash.impossible"get_transitive_dest failure")
in
g_t_d
end
fun replace_chained_branch baa_tags_and_dests =
fn (opcode as (Sparc_Assembly.BRANCH(branch, _),
SOME tag, comment)) =>
(case NewMap.tryApply'(baa_tags_and_dests, tag) of
SOME dest_tag =>
(Sparc_Assembly.BRANCH(branch, 0), SOME dest_tag, comment)
| NONE => opcode)
| (opcode as (Sparc_Assembly.BRANCH_ANNUL(branch,_),
SOME tag, comment)) =>
(case NewMap.tryApply'(baa_tags_and_dests, tag) of
SOME dest_tag =>
(Sparc_Assembly.BRANCH_ANNUL(branch, 0), SOME dest_tag, comment)
| NONE => opcode)
| opcode => opcode
fun replace_chained_branches block_list =
let
val baa_blocks = Lists.filterp find_baa_block block_list
val baa_tags_and_dests =
Lists.reducel
(fn (tree,
(tag,
(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),
SOME dest_tag, _) :: _)) =>
NewMap.define(tree, tag, dest_tag)
| _ => Crash.impossible"Bad baa block")
(NewMap.empty, baa_blocks)
val get_transitive_dest' = get_transitive_dest baa_tags_and_dests
val baa_tags_and_dests =
Lists.reducel
(fn (tree, (tag, _)) =>
NewMap.define(tree, tag, get_transitive_dest'([], tag)))
(NewMap.empty, baa_blocks)
val replace_chained_branch' = replace_chained_branch baa_tags_and_dests
in
map
(fn (tag, opcode_list) =>
(tag, map replace_chained_branch' opcode_list))
block_list
end
fun has_delay'(Sparc_Assembly.LOAD_OFFSET _) = true
| has_delay'(Sparc_Assembly.TAGGED_ARITHMETIC (_, MachTypes.G1,_,_)) = true
| has_delay' x = has_delay x
fun is_fcmp op1 =
case op1 of
Sparc_Assembly.FUNARY (funary,_,_) =>
(case funary of
Sparc_Assembly.FCMPS => true
| Sparc_Assembly.FCMPD => true
| Sparc_Assembly.FCMPX => true
| _ => false)
| _ => false
fun is_fbranch op1 =
case op1 of
Sparc_Assembly.FBRANCH _ => true
| Sparc_Assembly.FBRANCH_ANNUL _ => true
| _ => true
fun try_separate (op1,op2) =
not (has_delay op1) andalso
let
fun is_store_op (Sparc_Assembly.LOAD_AND_STORE(ls,_,_,_)) =
(case ls of
Sparc_Assembly.STB => true
| Sparc_Assembly.STH => true
| Sparc_Assembly.ST => true
| Sparc_Assembly.STD => true
| _ => false)
| is_store_op (Sparc_Assembly.LOAD_AND_STORE_FLOAT (ls,_,_,_)) =
(case ls of
Sparc_Assembly.STF => true
| Sparc_Assembly.STDF => true
| _ => false)
| is_store_op _ = false
datatype reg = GC of MachTypes.Sparc_Reg | FLOAT of MachTypes.Sparc_Reg | NOREG
val (load_reg,is_store) =
case op1 of
Sparc_Assembly.LOAD_AND_STORE (ls,reg,_,_) =>
if is_store_op op1 then (NOREG,true)
else (GC reg,false)
| Sparc_Assembly.LOAD_AND_STORE_FLOAT (ls,reg,_,_) =>
if is_store_op op1 then (NOREG,true)
else (FLOAT reg,false)
| _ => (NOREG,false)
in
case load_reg of
NOREG =>
(is_store andalso is_store_op op2) orelse
(is_fcmp op1 andalso is_fbranch op2)
| GC r =>
let
val (i_def, i_use, r_def, r_use) =
Sparc_Assembly.defines_and_uses op2
in
Set.is_member (r,i_use)
end
| FLOAT r =>
let
val (i_def, i_use, r_def, r_use) =
Sparc_Assembly.defines_and_uses op2
in
Set.is_member (r,r_use)
end
end
fun reschedule_proc(tag, block_list) =
let
val block_list = replace_chained_branches block_list
val all_tags_and_instrs = map get_tag_and_instr block_list
val ok_tags_and_instrs =
Lists.filterp
(fn (_, (x, _, _)) => not(has_delay' x))
all_tags_and_instrs
fun get_ref_tags_for_block(done, []) = done
| get_ref_tags_for_block(done,
(Sparc_Assembly.BRANCH_ANNUL(branch, _),
SOME tag, _) :: rest) =
(case (branch, rest) of
(Sparc_Assembly.BA, _) =>
get_ref_tags_for_block(done, rest)
| (_, (Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.AND, MachTypes.G0, MachTypes.G0, Sparc_Assembly.IMM 0), _, _) :: _) =>
get_ref_tags_for_block(HashSet.add_member(done, tag), rest)
| _ => get_ref_tags_for_block(done, rest))
| get_ref_tags_for_block(done,
(Sparc_Assembly.FBRANCH_ANNUL(branch, _),
SOME tag, _) :: rest) =
(case (branch, rest) of
(Sparc_Assembly.FBA, _) =>
get_ref_tags_for_block(done, rest)
| (_, (Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.AND, MachTypes.G0, MachTypes.G0, Sparc_Assembly.IMM 0), _, _) :: _) =>
get_ref_tags_for_block(HashSet.add_member(done, tag), rest)
| _ => get_ref_tags_for_block(done, rest))
| get_ref_tags_for_block(done, _ :: rest) =
get_ref_tags_for_block(done, rest)
val ref_tags =
Lists.reducel
(fn (tags, (tag, x)) => get_ref_tags_for_block(tags, x))
(HashSet.empty_set hashset_size, block_list)
fun filter_fun(tag, _) = HashSet.is_member(ref_tags, tag)
val ref_tags_etc =
Lists.reducel
(fn (tree, (tag, instr)) =>
NewMap.define(tree, tag, (instr, MirTypes.new_tag())))
(NewMap.empty,
Lists.filterp filter_fun ok_tags_and_instrs)
fun split_blocks(acc, []) = rev acc
| split_blocks(_, (_, []) :: _) = Crash.impossible"Empty block"
| split_blocks(acc, (block as (tag, opcode :: rest)) :: others) =
(case NewMap.tryApply'(ref_tags_etc, tag) of
SOME (instr, dest_tag) =>
split_blocks
((tag,
[opcode,
(Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, 0),
SOME dest_tag,
"Split off leading instruction")]) ::
(dest_tag, rest) ::
acc,
others)
| NONE =>
split_blocks(block :: acc, others))
val new_block_list = split_blocks([], block_list)
fun do_replace(done, []) = rev done
| do_replace(done, [opcode]) = rev(opcode :: done)
| do_replace(done, op1 :: op2 :: rest) =
(case (op1, op2) of
((Sparc_Assembly.BRANCH_ANNUL(branch, _), SOME tag,
bcomment),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.AND, MachTypes.G0, MachTypes.G0, Sparc_Assembly.IMM 0), _, _)) =>
(case NewMap.tryApply'(ref_tags_etc, tag) of
SOME ((opcode, tag_opt, comment), new_tag) =>
if branch = Sparc_Assembly.BA then
do_replace(op2 :: op1 :: done, rest)
else
do_replace((opcode, tag_opt, comment ^
" moved to annulled delay slot") ::
(Sparc_Assembly.BRANCH_ANNUL(branch, 0),
SOME new_tag, bcomment) :: done,
rest)
| NONE =>
do_replace(op2 :: op1 :: done, rest))
| ((Sparc_Assembly.FBRANCH_ANNUL(branch, _), SOME tag,
bcomment),
(Sparc_Assembly.ARITHMETIC_AND_LOGICAL
(Sparc_Assembly.AND, MachTypes.G0, MachTypes.G0, Sparc_Assembly.IMM 0), _, _)) =>
(case NewMap.tryApply'(ref_tags_etc, tag) of
SOME ((opcode, tag_opt, comment), new_tag) =>
if branch = Sparc_Assembly.FBA then
do_replace(op2 :: op1 :: done, rest)
else
do_replace((opcode, tag_opt, comment ^
" moved to annulled delay slot") ::
(Sparc_Assembly.FBRANCH_ANNUL(branch, 0),
SOME new_tag, bcomment) :: done,
rest)
| NONE =>
do_replace(op2 :: op1 :: done, rest))
| _ => do_replace(op1 :: done, op2 :: rest))
fun replace_delay_slots(tag, opcode_list) =
(tag, do_replace([], opcode_list))
val new_new_block_list = map replace_delay_slots new_block_list
val next_block_list = replace_chained_branches new_new_block_list
val new_ref_tags = refs_all(tag, next_block_list)
fun use_block(tag, _) = HashSet.is_member(new_ref_tags, tag)
val final_block_list = Lists.filterp use_block next_block_list
val result as (_, blocks) = (tag, map remove_doubled_instrs final_block_list)
in
result
end
fun combine((i_d1, i_u1, r_d1, r_u1), (i_d2, i_u2, r_d2, r_u2)) =
(Set.union(i_d1, i_d2), Set.union(i_u1, i_u2), Set.union(r_d1, r_d2),
Set.union(r_u1, r_u2))
fun immoveable opcode =
opcode = Sparc_Assembly.other_nop_code orelse
(case opcode of
Sparc_Assembly.LOAD_OFFSET _ => true
| Sparc_Assembly.TAGGED_ARITHMETIC (_, MachTypes.G1,_,_) => true
| Sparc_Assembly.ARITHMETIC_AND_LOGICAL (_, MachTypes.G2,_,_) => true
| Sparc_Assembly.FUNARY _ => true
| _ => false)
fun search_for_moveable_instr(pos, _, next, []) = (pos, false, nop)
| search_for_moveable_instr(pos, current_defs, next,
(opc as (opcode, _, _)) :: rest) =
if pos > schedule_limit orelse immoveable opcode
then (pos,false,nop)
else
let
fun check_next opc2 =
(case next of
NONE => true
| SOME opc1 => not (try_separate (opc1,opc2)))
val is_delay = case rest of
[] => false
| (opc', _, _) :: _ => has_delay opc'
in
if is_delay
then (pos, false, nop)
else
let
val (i_def, i_use, r_def, r_use) =
Sparc_Assembly.defines_and_uses opcode
val new_defs =
(Set.setdiff(i_def, Set.singleton MachTypes.G0),
Set.setdiff(i_use, Set.singleton MachTypes.G0),
r_def, r_use)
in
if passes_from_sets(new_defs, current_defs)
andalso check_next opcode then
(pos, true, opc)
else
search_for_moveable_instr(pos+1, combine(new_defs, current_defs),
next, rest)
end
end
fun copy_over (0, done, rest) = (done,rest)
| copy_over (n, done, []) = Crash.impossible "copy_over: out of code"
| copy_over (n, done, x::rest) = copy_over(n-1, x :: done, rest)
fun drop [] = Crash.impossible "drop: out of code"
| drop (a::b) = b
fun traverse(done, static, []) = done
| traverse(done, static, [x]) = x :: done
| traverse(done,
static,
(x as (opc1, _, _)) ::
(rest as ((y as (opc2, tag2, com2)) :: rest_of_block))) =
if opc1 = nop_code andalso has_delay opc2 then
let
val (pos, found, z) =
search_for_moveable_instr(0, Sparc_Assembly.defines_and_uses opc2,
NONE,rest_of_block)
in
if found then
let
val (done, rest) =
copy_over (pos, convert_branch_annul y :: z :: done,
rest_of_block)
in
traverse(done, static, drop rest)
end
else
traverse(y :: x :: done, static, rest_of_block)
end
else if not static andalso try_separate (opc2,opc1)
then
let
val (pos,found,z) =
search_for_moveable_instr (0,Sparc_Assembly.defines_and_uses opc2,
SOME opc2, rest_of_block)
in
if found then
let
val (done,rest) =
copy_over (pos, (opc2,tag2,com2) :: z :: x :: done,rest_of_block)
in
traverse (done,static,drop rest)
end
else
(traverse (x :: done, static, (opc2,tag2,com2) :: rest_of_block))
end
else
traverse(x :: done, static, rest)
fun trav2(acc,true, l) = rev l
| trav2(acc,static,[]) = acc
| trav2(acc,static,[a]) = a::acc
| trav2(acc,static,(opc1,tag1,com1)::(opc2,tag2,com2)::rest) =
let
fun find_filler (n,[],_) = NONE
| find_filler (n,(opc3,tag3,com3)::rest,current_defs) =
if n > schedule_limit orelse has_delay opc3 orelse immoveable opc3 then NONE
else
let
val (i_def, i_use, r_def, r_use) =
Sparc_Assembly.defines_and_uses opc3
val new_defs =
(Set.setdiff(i_def, Set.singleton MachTypes.G0),
Set.setdiff(i_use, Set.singleton MachTypes.G0),
r_def, r_use)
in
if passes_from_sets(current_defs,new_defs) andalso
not (try_separate (opc1,opc3))
then SOME ((opc3,tag3,com3),n)
else
find_filler (n+1,rest,
combine(new_defs, current_defs))
end
in
if not (has_delay opc2) andalso try_separate (opc1,opc2)
then
case find_filler (0,rest,Sparc_Assembly.defines_and_uses opc2) of
NONE => trav2 ((opc1,tag1,com1)::acc,static,(opc2,tag2,com2)::rest)
| SOME (c,n) =>
let
val (done,rest) = copy_over (n,(opc2,tag2,com2) :: c :: (opc1,tag1,com1) :: acc, rest)
in
trav2 (done,static,drop rest)
end
else trav2 ((opc1,tag1,com1)::acc,static,(opc2,tag2,com2)::rest)
end
fun check_fcmp opcodelist =
let
fun check (op1 :: op2 :: rest,acc) =
if is_fcmp (#1 op1) andalso is_fbranch (#1 op2)
then check (rest,op2 :: nop :: op1 :: acc)
else check (op2::rest,op1::acc)
| check ([a],acc) = rev (a::acc)
| check ([],acc) = rev acc
in
case opcodelist of
(op1 as (Sparc_Assembly.FBRANCH _,_,_)) :: rest => check (rest,[op1,nop])
| (op1 as (Sparc_Assembly.FBRANCH_ANNUL _,_,_)) :: rest => check (rest,[op1,nop])
| _ => check (opcodelist,[])
end
fun reschedule_block
(static, ops as [_, (Sparc_Assembly.BRANCH_ANNUL(Sparc_Assembly.BA, _),_,_), _]) = ops
| reschedule_block (static,opcode_list) =
let
val l1 = if false then trav2 ([],static,opcode_list) else rev opcode_list
val l2 = if true then traverse ([],static,l1) else rev l1
val l3 = if true then rev (trav2 ([],static,l2)) else l2
in
check_fcmp l3
end
end
;
