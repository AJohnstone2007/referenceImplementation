require "$.basis.__int";
require "$.basis.__string";
require "../utils/diagnostic";
require "../utils/_hashset";
require "../utils/lists";
require "../utils/crash";
require "../utils/intnewmap";
require "../utils/bignum";
require "../main/info";
require "../main/options";
require "../lambda/lambdaprint";
require "../lambda/simpleutils";
require "../match/type_utils";
require "../main/library";
require "../rts/gen/implicit";
require "../main/machspec";
require "../main/machperv";
require "mirregisters";
require "mirprint";
require "mir_utils";
require "mirtables";
require "mir_cg";
functor Mir_Cg(
include sig
structure Diagnostic : DIAGNOSTIC
structure Lists : LISTS
structure Crash : CRASH
structure Info : INFO
structure Options : OPTIONS
structure IntMap : INTNEWMAP
structure BigNum : BIGNUM
structure BigNum32 : BIGNUM
structure Library : LIBRARY
structure LambdaPrint : LAMBDAPRINT
structure SimpleUtils : SIMPLEUTILS
structure MirPrint : MIRPRINT
structure MirRegisters : MIRREGISTERS
structure Mir_Utils : MIR_UTILS
structure MirTables : MIRTABLES
structure Implicit_Vector : IMPLICIT_VECTOR
structure MachSpec : MACHSPEC
structure MachPerv : MACHPERV
structure TypeUtils : TYPE_UTILS
sharing Library.AugLambda.Debugger_Types.Options = Options
sharing TypeUtils.Datatypes.Ident.Location = Info.Location
sharing LambdaPrint.LambdaTypes = Library.AugLambda.LambdaTypes =
SimpleUtils.LambdaTypes = Mir_Utils.Mir_Env.LambdaTypes
sharing MirRegisters.MirTypes = MirPrint.MirTypes = Mir_Utils.Mir_Env.MirTypes =
MirTables.MirTypes
sharing Library.AugLambda = Mir_Utils.AugLambda
sharing Mir_Utils.Mir_Env.MirTypes.Debugger_Types =
Library.AugLambda.Debugger_Types
sharing TypeUtils.Datatypes.NewMap = Library.NewMap = Mir_Utils.Map
sharing LambdaPrint.LambdaTypes.Ident = TypeUtils.Datatypes.Ident
sharing type Library.CompilerOptions = Options.compiler_options
sharing type Mir_Utils.Mir_Env.Map = IntMap.T
sharing type MirPrint.MirTypes.Debugger_Types.RuntimeEnv.VarInfo = LambdaPrint.LambdaTypes.VarInfo
sharing type TypeUtils.Datatypes.Ident.SCon = MirPrint.MirTypes.SCon
sharing type LambdaPrint.LambdaTypes.Type = TypeUtils.Datatypes.Type
sharing type MachPerv.Pervasives.pervasive = LambdaPrint.LambdaTypes.Primitive
sharing type MirPrint.MirTypes.Debugger_Types.RuntimeEnv.Type = LambdaPrint.LambdaTypes.Type =
TypeUtils.Datatypes.Type
sharing type MirPrint.MirTypes.Debugger_Types.RuntimeEnv.Tyfun = LambdaPrint.LambdaTypes.Tyfun
sharing type MirPrint.MirTypes.Debugger_Types.RuntimeEnv.Instance = LambdaPrint.LambdaTypes.Instance =
TypeUtils.Datatypes.Instance
sharing type MirPrint.MirTypes.Debugger_Types.RuntimeEnv.FunInfo = LambdaPrint.LambdaTypes.FunInfo
end where type LambdaPrint.LambdaTypes.LVar = int
): MIR_CG =
struct
structure Pervasives = MachPerv.Pervasives
structure LambdaTypes = LambdaPrint.LambdaTypes
structure Set = LambdaTypes.Set
structure Sexpr = Mir_Utils.Sexpr
structure Mir_Env = Mir_Utils.Mir_Env
structure AugLambda = Library.AugLambda
structure MirTypes = MirPrint.MirTypes
structure Datatypes = TypeUtils.Datatypes
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure NewMap = Datatypes.NewMap
structure Diagnostic = Diagnostic
structure Debugger_Types = AugLambda.Debugger_Types
structure HashSet = HashSet(
structure Crash = Crash
structure Lists = Lists
type element = LambdaTypes.LVar
val eq = op=
val size = 200
val hash = fn x => x
)
structure Info = Info
structure Options = Options
structure RuntimeEnv = Debugger_Types.RuntimeEnv
val do_diagnostics = false
val print_timings = false
val N = Int.toString
val real_offset = 3
val hashset_size = 64
fun lists_reducel f =
let
fun red (acc, []) = acc
| red (acc, x::xs) = red (f(acc,x), xs)
in
red
end
fun empty_hashset () = HashSet.empty_set hashset_size
val caller_arg = MirRegisters.caller_arg
val callee_arg = MirRegisters.callee_arg
val caller_closure = MirRegisters.caller_closure
val callee_closure = MirRegisters.callee_closure
val fp = MirRegisters.fp
val sp = MirRegisters.sp
val no_code = ((Sexpr.NIL, [], NONE, Sexpr.NIL), [], [])
val ident_fn = fn x => x
fun count_gc_tags(AugLambda.SCON_TAG(Ident.REAL _, _)) = 1
| count_gc_tags(AugLambda.SCON_TAG(Ident.STRING _, _)) = 1
| count_gc_tags _ = 0
val empty_string_tree = NewMap.empty ((op<):string*string->bool,op= : string * string -> bool)
fun last [] = Crash.impossible"Last empty list"
| last [x] = x
| last (_ :: xs) = last xs
fun new_frees(vars as (old_set, new_set), {lexp=lexp, size=_}) =
case lexp of
AugLambda.APP(le, (lel,fpel),_) => new_frees_list (new_frees (vars, le), fpel @ lel)
| AugLambda.LET((_,_,lb),le) =>
new_frees (new_frees (vars,lb), le)
| AugLambda.FN (args, le,_,_) => new_frees (vars, le)
| AugLambda.STRUCT le_list => new_frees_list (vars, le_list)
| AugLambda.SELECT(_, le) => new_frees (vars, le)
| AugLambda.SWITCH(le, info, tag_le_list, opt) =>
lists_reducel
(fn (vars, (tag, le)) =>
let
val vars = new_frees (vars, le)
in
case tag of
AugLambda.EXP_TAG lexp => new_frees (vars, lexp)
| _ => vars
end)
(bandf_opt (new_frees (vars, le), opt), tag_le_list)
| AugLambda.VAR lv =>
(old_set,
if HashSet.is_member(old_set, lv)
then HashSet.add_member(new_set, lv)
else new_set)
| AugLambda.LETREC(lv_list, le_list, le) =>
new_frees_list (vars, le :: le_list)
| AugLambda.INT _ => vars
| AugLambda.SCON _ => vars
| AugLambda.MLVALUE _ => vars
| AugLambda.HANDLE(le, le') =>
new_frees(new_frees (vars, le), le')
| AugLambda.RAISE (le) => new_frees (vars, le)
| AugLambda.BUILTIN _ => vars
and new_frees_list (vars, []) = vars
| new_frees_list (vars,le::rest) = new_frees_list (new_frees (vars,le),rest)
and bandf_opt(vars, SOME le) = new_frees (vars, le)
| bandf_opt(vars,_) = vars
fun do_pos1(_, []) = []
| do_pos1(pos, (_, {size=size:int, lexp=_}) :: rest) =
pos :: do_pos1(pos+size, rest)
fun do_pos2(_, []) = []
| do_pos2(pos, (tag,_) :: rest) =
pos :: do_pos2(pos+count_gc_tags tag, rest)
fun do_pos3(_, []) = []
| do_pos3(pos, {size=size:int, lexp=_} :: rest) =
pos :: do_pos3(pos+size, rest)
val zip2 = Lists.zip
fun zip3 (l1,l2,l3) =
let
fun aux ([],[],[],acc) = rev acc
| aux (a1::b1,a2::b2,a3::b3,acc) =
aux (b1,b2,b3,(a1,a2,a3)::acc)
| aux _ = Crash.impossible "zip3"
in
aux (l1,l2,l3,[])
end
fun zip4 (l1,l2,l3,l4) =
let
fun aux ([],[],[],[],acc) = rev acc
| aux (a1::b1,a2::b2,a3::b3,a4::b4,acc) =
aux (b1,b2,b3,b4,(a1,a2,a3,a4)::acc)
| aux _ = Crash.impossible "zip5"
in
aux (l1,l2,l3,l4,[])
end
fun zip5 (l1,l2,l3,l4,l5) =
let
fun aux ([],[],[],[],[],acc) = rev acc
| aux (a1::b1,a2::b2,a3::b3,a4::b4,a5::b5,acc) =
aux (b1,b2,b3,b4,b5,(a1,a2,a3,a4,a5)::acc)
| aux _ = Crash.impossible "zip5"
in
aux (l1,l2,l3,l4,l5,[])
end
fun zip7 (l1,l2,l3,l4,l5,l6,l7) =
let
fun aux ([],[],[],[],[],[],[],acc) = rev acc
| aux (a1::b1,a2::b2,a3::b3,a4::b4,a5::b5,a6::b6,a7::b7,acc) =
aux (b1,b2,b3,b4,b5,b6,b7,(a1,a2,a3,a4,a5,a6,a7)::acc)
| aux _ = Crash.impossible "zip7"
in
aux (l1,l2,l3,l4,l5,l6,l7,[])
end
local
val caller_arg_regs = MirRegisters.caller_arg_regs
val callee_arg_regs = MirRegisters.callee_arg_regs
fun assign_regs regs args =
let
fun assign ([],regs,acc) = rev acc
| assign (rest,[reg],acc) = rev (reg::acc)
| assign (arg::rest,reg::restregs,acc) = assign (rest,restregs,reg::acc)
| assign _ = Crash.impossible "Not enough arg regs"
in
assign (args,regs,[])
end
in
val assign_caller_regs = assign_regs caller_arg_regs
val assign_callee_regs = assign_regs callee_arg_regs
val assign_fp_regs = assign_regs MirRegisters.fp_arg_regs
end
fun make_get_args_code (args,copies) =
let
fun make_code ([],[],acc) = rev acc
| make_code ([arg],copies as (_::_::_),acc) =
let
fun do_loads (n,copy::rest,acc) =
do_loads (n+1,rest,
MirTypes.STOREOP (MirTypes.LD,
MirTypes.GC_REG copy,
MirTypes.GC_REG arg,
MirTypes.GP_IMM_ANY (n * 4 - 1)) ::
acc)
| do_loads (n,[],acc) = rev acc
in
do_loads (0,copies,acc)
end
| make_code (arg::restargs,copy::restcopies,acc) =
make_code (restargs,restcopies,
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG copy,
MirTypes.GP_GC_REG arg) ::
acc)
| make_code _ = Crash.impossible "make_get_args_code"
in
make_code (args,copies,[])
end
fun make_fp_get_args_code (args,copies) =
let
fun make_code ([],[],acc) = rev acc
| make_code (arg::restargs,copy::restcopies,acc) =
make_code (restargs,restcopies,
MirTypes.UNARYFP(MirTypes.FMOVE,
MirTypes.FP_REG copy,
MirTypes.FP_REG arg) ::
acc)
| make_code _ = Crash.impossible "make_fp_get_args_code"
in
make_code (args,copies,[])
end
fun is_simple_relation lexp =
case lexp of
AugLambda.APP({lexp=AugLambda.BUILTIN(prim,_), ...},_,_) =>
(case prim of
Pervasives.INTLESS => true
| Pervasives.REALLESS => true
| Pervasives.CHARLT => true
| Pervasives.WORDLT => true
| Pervasives.INTGREATER => true
| Pervasives.REALGREATER => true
| Pervasives.CHARGT => true
| Pervasives.WORDGT => true
| Pervasives.INTLESSEQ => true
| Pervasives.REALLESSEQ => true
| Pervasives.CHARLE => true
| Pervasives.WORDLE => true
| Pervasives.INTGREATEREQ => true
| Pervasives.REALGREATEREQ => true
| Pervasives.CHARGE => true
| Pervasives.WORDGE => true
| Pervasives.INTEQ => true
| Pervasives.INTNE => true
| Pervasives.REALEQ => true
| Pervasives.REALNE => true
| Pervasives.CHAREQ => true
| Pervasives.CHARNE => true
| Pervasives.WORDEQ => true
| Pervasives.WORDNE => true
| _ => false)
| _ => false
fun convert_tag tag =
case tag of
AugLambda.VCC_TAG(tag,_) => RuntimeEnv.CONSTRUCTOR(tag)
| AugLambda.IMM_TAG(tag,_) => RuntimeEnv.CONSTRUCTOR(tag)
| AugLambda.SCON_TAG(tag, _) =>
(case tag of
Ident.INT(tag,_) => RuntimeEnv.INT(tag)
| Ident.REAL(tag,_) => RuntimeEnv.REAL(tag)
| Ident.STRING(tag) => RuntimeEnv.STRING(tag)
| Ident.CHAR(tag) => RuntimeEnv.CHAR(tag)
| Ident.WORD(tag, _) => RuntimeEnv.WORD(tag))
| AugLambda.EXP_TAG(_) => RuntimeEnv.DYNAMIC
fun make_prim_info (options,lambda_exp) =
let
val (prim_to_lambda_map, new_lambda_exp) =
Library.build_external_environment (options, lambda_exp)
val prim_to_lambda = NewMap.apply prim_to_lambda_map
in
(prim_to_lambda,new_lambda_exp)
end
fun do_externals (new_exp_and_size,number_of_gc_objects) =
let
val (top_tags_list, next) =
Lists.number_from_by_one(Mir_Utils.list_of_tags (number_of_gc_objects+1), 0,
ident_fn)
val top_closure = IntMap.apply (IntMap.from_list (map (fn (x,y) => (y,x)) top_tags_list))
fun wrap_tree_bindings(tree, prim, le) =
NewMap.fold
(fn (le as {size=size, ...}, string, lv) =>
{lexp=AugLambda.LET
((lv, NONE,
{lexp=AugLambda.APP
({lexp=AugLambda.BUILTIN(prim,LambdaTypes.null_type_annotation),
size=0},
([{lexp=AugLambda.SCON(Ident.STRING string, NONE), size=0}],[]),
Debugger_Types.null_backend_annotation),
size=0}), le), size=size})
(le, tree)
val needs_transform = Mir_Utils.transform_needed(false, new_exp_and_size)
val ((var_tree, exn_tree, str_tree, fun_tree, ext_tree), new_exp_and_size) =
if needs_transform then
Mir_Utils.lift_externals((empty_string_tree, empty_string_tree, empty_string_tree,
empty_string_tree, empty_string_tree), new_exp_and_size)
else
((empty_string_tree, empty_string_tree, empty_string_tree,
empty_string_tree, empty_string_tree), new_exp_and_size)
val ext_vars = NewMap.domain var_tree
val ext_exns = NewMap.domain exn_tree
val ext_strs = NewMap.domain str_tree
val ext_funs = NewMap.domain fun_tree
val new_exp_and_size =
if needs_transform then
wrap_tree_bindings
(ext_tree, Pervasives.LOAD_STRING, wrap_tree_bindings
(var_tree, Pervasives.LOAD_VAR, wrap_tree_bindings
(exn_tree, Pervasives.LOAD_EXN, wrap_tree_bindings
(str_tree, Pervasives.LOAD_STRUCT, wrap_tree_bindings
(fun_tree, Pervasives.LOAD_FUNCT, new_exp_and_size)))))
else
new_exp_and_size
val ext_strings =
if needs_transform
then NewMap.domain ext_tree
else Set.set_to_list (Mir_Utils.get_string new_exp_and_size)
val (ext_string_list, next) =
Lists.number_from_by_one(ext_strings, next, ident_fn)
val (ext_var_list, next) =
Lists.number_from_by_one(ext_vars, next, ident_fn)
val (ext_exn_list, next) =
Lists.number_from_by_one(ext_exns, next, ident_fn)
val (ext_str_list, next) =
Lists.number_from_by_one(ext_strs, next, ident_fn)
val (ext_fun_list, next) =
Lists.number_from_by_one(ext_funs, next, ident_fn)
in
(new_exp_and_size, top_tags_list, top_closure,
(ext_string_list,ext_var_list,ext_exn_list,ext_str_list,ext_fun_list))
end
fun find_frees (env,closure,fcn,prim_to_lambda) =
let
val Mir_Env.LAMBDA_ENV lambda_env = env
val Mir_Env.CLOSURE_ENV closure_env = closure
fun add_members x = IntMap.fold (fn (set, x,_) => HashSet.add_member(set, x)) x
val (_,free) =
new_frees
((add_members (add_members (empty_hashset(), closure_env), lambda_env),
empty_hashset()),
{lexp=fcn, size=0})
val free' =
if Mir_Utils.needs_prim_stringeq fcn then
HashSet.add_member(free, prim_to_lambda Pervasives.STRINGEQ)
else
free
in
HashSet.set_to_list
(lists_reducel
(fn (free', x) => HashSet.add_member(free', prim_to_lambda x))
(free', Set.set_to_list (Library.implicit_external_references fcn)))
end
fun load_external (lexp,index_fn,env,spills,calls) =
(case lexp of
AugLambda.SCON(Ident.STRING chars, _) =>
let
val result = MirTypes.GC.new()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
((Sexpr.ATOM[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG result,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY(~1 + 4 * (index_fn chars)))],
[], NONE, Sexpr.NIL), [], []),
env,spills,calls)
end
| _ => Crash.impossible"Bad parameter to load_external")
fun do_get_implicit (lexp,env,spills,calls) =
(case lexp of
AugLambda.INT offset =>
let
val result = MirTypes.GC.new()
in
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_GC_REG result)),
((Sexpr.ATOM
[MirTypes.STOREOP(MirTypes.LDREF, MirTypes.GC_REG result,
MirTypes.GC_REG MirRegisters.implicit,
MirTypes.GP_IMM_ANY (4 * offset))],
[],NONE, Sexpr.NIL),[],[]),
env,spills,calls)
end
| _ => Crash.impossible "Bad parameter to get_implicit")
fun get_int_pair regs =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg as MirTypes.GP_GC_REG _)) =>
Mir_Utils.destruct_2_tuple reg
| Mir_Utils.LIST[Mir_Utils.INT reg1, Mir_Utils.INT reg2] =>
(reg1, reg2, [])
| _ => Crash.impossible "get_int_pair"
fun get_real_pair regs =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg as MirTypes.GP_GC_REG _)) =>
let
val (reg1, reg2, des_code) = Mir_Utils.destruct_2_tuple reg
val (fp1, code1) = Mir_Utils.get_real (Mir_Utils.INT reg1)
val (fp2, code2) = Mir_Utils.get_real (Mir_Utils.INT reg2)
in
(fp1, fp2, des_code @ code1 @ code2)
end
| Mir_Utils.LIST[reg1, reg2] =>
let
val (fp1, code1) = Mir_Utils.get_real reg1
val (fp2, code2) = Mir_Utils.get_real reg2
in
(fp1, fp2, code1 @ code2)
end
| _ => Crash.impossible"get_real_pair"
fun get_word32_pair regs =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg as MirTypes.GP_GC_REG _)) =>
let
val (reg1, reg2, des_code) = Mir_Utils.destruct_2_tuple reg
val (w1, code1, clean1) =
Mir_Utils.get_word32 (Mir_Utils.INT reg1)
val (w2, code2, clean2) =
Mir_Utils.get_word32 (Mir_Utils.INT reg2)
in
(w1, w2, des_code @ code1 @ code2, clean1 @ clean2)
end
| Mir_Utils.LIST[reg1, reg2] =>
let
val (w1, code1, clean1) = Mir_Utils.get_word32 reg1
val (w2, code2, clean2) = Mir_Utils.get_word32 reg2
in
(w1, w2, code1 @ code2, clean1 @ clean2)
end
| _ => Crash.impossible"get_word32_pair"
fun getint (Mir_Utils.INT x) = x
| getint _ = Crash.impossible "getint"
fun get_word32_and_word regs =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg as MirTypes.GP_GC_REG _)) =>
let
val (reg1, reg2, des_code) = Mir_Utils.destruct_2_tuple reg
val (w1, code1, clean1) =
Mir_Utils.get_word32 (Mir_Utils.INT reg1)
in
(w1, reg2, des_code @ code1, clean1)
end
| Mir_Utils.LIST[reg1, reg2] =>
let
val (w1, code1, clean1) = Mir_Utils.get_word32 reg1
in
(w1, getint reg2, code1, clean1)
end
| _ => Crash.impossible"get_word32_pair"
fun unary_negate(opcode,regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
in
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine(the_code,
((Sexpr.ATOM[(MirTypes.TBINARY(opcode, exn_tag_list, res2,
MirTypes.GP_IMM_INT 0, reg))], exn_blocks,
NONE, Sexpr.NIL), [], [])))
| _ => Crash.impossible"unary_negate"
end
fun int32_unary_negate (opcode, Mir_Utils.ONE(Mir_Utils.INT reg), the_code, exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
val (w, arg_code, clean_arg) =
Mir_Utils.get_word32 (Mir_Utils.INT reg)
val new_code =
arg_code @
[MirTypes.TBINARY
(opcode, exn_tag_list, res2, MirTypes.GP_IMM_INT 0, w)] @ clean_arg
val (final_res, final_code) = Mir_Utils.save_word32 result
val clean_res = [MirTypes.NULLARY(MirTypes.CLEAN, res2)]
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG final_res)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM(new_code @ final_code @ clean_res), exn_blocks,
NONE, Sexpr.NIL),
[], [])))
end
| int32_unary_negate _ = Crash.impossible "int32_unary_negate"
fun tagged_binary_fcalc(opcode,regs,the_code,exn_code) =
let
val result = MirTypes.FP_REG(MirTypes.FP.new())
val (val1,val2, new_code) = get_real_pair regs
val (exn_blocks, exn_tag_list) = exn_code
in
(Mir_Utils.ONE(Mir_Utils.REAL result),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @
[(MirTypes.TBINARYFP(opcode, exn_tag_list, result,
val1, val2))]),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun unary_fcalc (opcode,regs,the_code) =
let
val result = MirTypes.FP_REG(MirTypes.FP.new())
in
(Mir_Utils.ONE(Mir_Utils.REAL result),
Mir_Utils.combine(the_code,
(((case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
Sexpr.ATOM[MirTypes.STOREFPOP(MirTypes.FLD, result,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY real_offset),
MirTypes.UNARYFP(opcode, result, result)]
| Mir_Utils.ONE(Mir_Utils.REAL reg) =>
Sexpr.ATOM[MirTypes.UNARYFP(opcode, result, reg)]
| _ => Crash.impossible"unary_fcalc"),
[], NONE, Sexpr.NIL), [], [])))
end
fun tagged_unary_fcalc (opcode,regs,the_code,exn_code) =
let
val result = MirTypes.FP_REG(MirTypes.FP.new())
val (exn_blocks, exn_tag_list) = exn_code
in
(Mir_Utils.ONE(Mir_Utils.REAL result),
Mir_Utils.combine(the_code,
(((case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
Sexpr.ATOM[MirTypes.STOREFPOP(MirTypes.FLD, result,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY real_offset),
MirTypes.TUNARYFP(opcode, exn_tag_list, result, result)]
| Mir_Utils.ONE(Mir_Utils.REAL reg) =>
Sexpr.ATOM[MirTypes.TUNARYFP(opcode, exn_tag_list, result, reg)]
| _ => Crash.impossible"tagged_unary_fcalc"),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun make_test (test_instr,res_reg,finish_tag) =
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG res_reg,
MirTypes.GP_IMM_INT 1),
test_instr,
MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG res_reg,
MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG finish_tag)]
fun test (opcode,regs,the_code) =
let
val res_reg = MirTypes.GC.new()
val finish_tag = MirTypes.new_tag()
val (val1, val2, new_code) = get_int_pair regs
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res_reg)),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @
make_test (MirTypes.TEST(opcode, finish_tag, val1, val2),
res_reg,
finish_tag)),
[], SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"Code following test"]), [], [])))
end
fun test32 (opcode,regs,the_code) =
let
val res = MirTypes.GC.new()
val res2 = MirTypes.GC_REG res
val finish_tag = MirTypes.new_tag()
val (val1, val2, arg_code, clean_arg_code) = get_word32_pair regs
val new_code =
arg_code
@ make_test
(MirTypes.TEST (opcode, finish_tag, val1, val2),
res, finish_tag)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code, [], SOME finish_tag,
Sexpr.ATOM clean_arg_code),
[], [])))
end
fun imake_if (opcode,regs,the_code,true_tag,false_tag) =
let
val (val1, val2, new_code) = get_int_pair regs
in
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @
[MirTypes.TEST(opcode, true_tag, val1, val2),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG false_tag)]),
[], NONE, Sexpr.NIL), [], []))
end
fun fmake_if (opcode,sense,regs,the_code,true_tag,false_tag) =
let
val (val1, val2, new_code) = get_real_pair regs
val (val1, val2) = if sense then (val1, val2) else (val2, val1)
in
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @
[MirTypes.FTEST(opcode, true_tag, val1, val2),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG false_tag)]),
[], NONE, Sexpr.NIL), [], []))
end
fun make_if (test, regs, code, true_tag, false_tag) =
case test
of Pervasives.INTLESS =>
imake_if (MirTypes.BLT, regs, code, true_tag, false_tag)
| Pervasives.CHARLT =>
imake_if (MirTypes.BLT, regs, code, true_tag, false_tag)
| Pervasives.WORDLT =>
imake_if (MirTypes.BLO, regs, code, true_tag, false_tag)
| Pervasives.REALLESS =>
fmake_if (MirTypes.FBLT,true, regs, code, true_tag, false_tag)
| Pervasives.INTGREATER =>
imake_if (MirTypes.BGT, regs, code, true_tag, false_tag)
| Pervasives.CHARGT =>
imake_if (MirTypes.BGT, regs, code, true_tag, false_tag)
| Pervasives.WORDGT =>
imake_if (MirTypes.BHI, regs, code, true_tag, false_tag)
| Pervasives.REALGREATER =>
fmake_if (MirTypes.FBLT,false, regs, code, true_tag, false_tag)
| Pervasives.INTLESSEQ =>
imake_if (MirTypes.BLE, regs, code, true_tag, false_tag)
| Pervasives.CHARLE =>
imake_if (MirTypes.BLE, regs, code, true_tag, false_tag)
| Pervasives.WORDLE =>
imake_if (MirTypes.BLS, regs, code, true_tag, false_tag)
| Pervasives.REALLESSEQ =>
fmake_if (MirTypes.FBLE, true, regs, code, true_tag, false_tag)
| Pervasives.INTGREATEREQ =>
imake_if (MirTypes.BGE, regs, code, true_tag, false_tag)
| Pervasives.CHARGE =>
imake_if (MirTypes.BGE, regs, code, true_tag, false_tag)
| Pervasives.WORDGE =>
imake_if (MirTypes.BHS, regs, code, true_tag, false_tag)
| Pervasives.REALGREATEREQ =>
fmake_if (MirTypes.FBLE, false, regs, code, true_tag, false_tag)
| Pervasives.INTEQ =>
imake_if (MirTypes.BEQ, regs, code, true_tag, false_tag)
| Pervasives.INTNE =>
imake_if (MirTypes.BNE, regs, code, true_tag, false_tag)
| Pervasives.CHAREQ =>
imake_if (MirTypes.BEQ, regs, code, true_tag, false_tag)
| Pervasives.CHARNE =>
imake_if (MirTypes.BNE, regs, code, true_tag, false_tag)
| Pervasives.WORDEQ =>
imake_if (MirTypes.BEQ, regs, code, true_tag, false_tag)
| Pervasives.WORDNE =>
imake_if (MirTypes.BNE, regs, code, true_tag, false_tag)
| Pervasives.REALEQ =>
fmake_if (MirTypes.FBEQ, true, regs, code, true_tag, false_tag)
| Pervasives.REALNE =>
fmake_if (MirTypes.FBNE, true, regs, code, true_tag, false_tag)
| _ => Crash.impossible "bad relational operator"
fun ftest(opcode,sense,regs,the_code) =
let
val res_reg = MirTypes.GC.new()
val finish_tag = MirTypes.new_tag()
in
let
val (val1, val2, new_code) = get_real_pair regs
val (val1, val2) = if sense then (val1, val2) else (val2, val1)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res_reg)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM(new_code @
make_test (MirTypes.FTEST(opcode, finish_tag, val1, val2),
res_reg,finish_tag)),
[], SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of float test"]), [], [])))
end
end
fun do_external_prim prim =
Crash.impossible"do_external_prim"
fun make_size_code regs =
let
val new_reg = case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) => MirTypes.GC_REG reg
| _ => Crash.impossible"Bad string pointer"
val res_reg = MirTypes.GC.new()
in
(new_reg, MirTypes.GP_GC_REG res_reg,
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG res_reg, new_reg,
MirTypes.GP_IMM_ANY ~5),
MirTypes.BINARY(MirTypes.LSR, MirTypes.GC_REG res_reg,
MirTypes.GP_GC_REG res_reg,
MirTypes.GP_IMM_ANY 6),
MirTypes.COMMENT"Divide by 64",
MirTypes.BINARY(MirTypes.SUBU, MirTypes.GC_REG res_reg,
MirTypes.GP_GC_REG res_reg,
MirTypes.GP_IMM_ANY 1),
MirTypes.COMMENT"Remove one for 0 terminator",
MirTypes.BINARY(MirTypes.ASL, MirTypes.GC_REG res_reg,
MirTypes.GP_GC_REG res_reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.COMMENT"And tag"])
end
fun tagged_binary_calc(opcode,regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (val1, val2, new_code) = get_int_pair regs
val (exn_blocks, exn_tag_list) = exn_code
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine (the_code,
((Sexpr.ATOM(new_code @
[(MirTypes.TBINARY(opcode,
exn_tag_list, res2, val1, val2))]),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun tagged_binary_calc(opcode,regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (val1, val2, new_code) = get_int_pair regs
val (exn_blocks, exn_tag_list) = exn_code
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine (the_code,
((Sexpr.ATOM(new_code @
[(MirTypes.TBINARY(opcode,
exn_tag_list, res2, val1, val2))]),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun tagged32_binary_calc (opcode, regs, the_code, exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val tmp = MirTypes.GC.new()
val tmp2 = MirTypes.GC_REG tmp
val (val1, val2, arg_code, clean_arg_code) = get_word32_pair regs
val (exn_blocks, exn_tag_list) = exn_code
val new_code =
MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 4)
:: arg_code
@ [MirTypes.TBINARY(opcode, exn_tag_list, tmp2, val1, val2),
MirTypes.STOREOP
(MirTypes.ST, tmp2, res2, MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, tmp2)]
@ clean_arg_code
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code, exn_blocks, NONE, Sexpr.NIL),
[], [])))
end
fun binary_calc (opcode,regs,the_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (val1, val2, new_code) = get_int_pair regs
in
(Mir_Utils.ONE(Mir_Utils.INT res1), Mir_Utils.combine
(the_code,
((Sexpr.ATOM(new_code @
[(MirTypes.BINARY(opcode, res2, val1, val2))]),
[], NONE, Sexpr.NIL), [], [])))
end
fun untagged32_binary_calc (opcode,regs,the_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val tmp = MirTypes.GC.new()
val tmp2 = MirTypes.GC_REG tmp
val (val1, val2, arg_code, clean_arg_code) = get_word32_pair regs
val new_code =
MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 4)
:: arg_code
@ [MirTypes.BINARY(opcode, tmp2, val1, val2),
MirTypes.STOREOP
(MirTypes.ST, tmp2, res2, MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, tmp2)]
@ clean_arg_code
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code, [], NONE, Sexpr.NIL), [], [])))
end
fun do_shift_operator(mir_operator,need_to_clear_bottom_two_bits,regs,the_code) =
let
val _ =
if need_to_clear_bottom_two_bits then
if mir_operator = MirTypes.ASR orelse
mir_operator = MirTypes.LSR then
()
else
Crash.impossible("mir_cg:do_shift_operator:bad shift and clear combination with " ^ MirPrint.binary_op mir_operator)
else
()
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val spare = MirTypes.GC.new()
val spare1 = MirTypes.GP_GC_REG spare
val spare2 = MirTypes.GC_REG spare
val (val1, val2, new_code) = get_int_pair regs
val shift_code =
case val2 of
MirTypes.GP_IMM_INT i =>
let
val (shift_limit, replace_by_zero) = case mir_operator of
MirTypes.ASR => (MachSpec.bits_per_word-1, false)
| _ => (MachSpec.bits_per_word, true)
val i = if i > shift_limit then shift_limit else i
in
if i >= shift_limit andalso replace_by_zero then
[MirTypes.UNARY(MirTypes.MOVE, res2, MirTypes.GP_IMM_INT 0)]
else
if need_to_clear_bottom_two_bits then
[MirTypes.BINARY(mir_operator, res2, val1,
MirTypes.GP_IMM_ANY(i+2)),
MirTypes.BINARY(MirTypes.ASL, res2, res1,
MirTypes.GP_IMM_ANY 2)]
else
[MirTypes.BINARY(mir_operator, res2, val1,
MirTypes.GP_IMM_ANY i)]
end
| _ =>
let
val clean_code =
[MirTypes.NULLARY(MirTypes.CLEAN, spare2)]
val clear_code =
if need_to_clear_bottom_two_bits then
MirTypes.UNARY(MirTypes.INTTAG, res2, res1) :: clean_code
else
clean_code
in
MirTypes.BINARY(MirTypes.LSR, spare2,
val2, MirTypes.GP_IMM_ANY 2) ::
MirTypes.BINARY(mir_operator,MirTypes.GC_REG result,val1,
spare1) ::
clear_code
end
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine(the_code,
((Sexpr.CONS(Sexpr.ATOM new_code, Sexpr.ATOM shift_code),
[],NONE,Sexpr.NIL),[],[])))
end
fun full_machine_word_shift_operator(mir_operator, regs, the_code) =
let
val result = MirTypes.GC.new()
val res2 = MirTypes.GC_REG result
val tmp = MirTypes.GC.new()
val tmp2 = MirTypes.GC_REG tmp
val spare = MirTypes.GC.new()
val spare1 = MirTypes.GP_GC_REG spare
val spare2 = MirTypes.GC_REG spare
val (val1, val2, arg_code, clean_arg_code) = get_word32_and_word regs
val alloc_code = MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 4)
val imm_val = case val2 of
MirTypes.GP_IMM_INT i => SOME i
| MirTypes.GP_IMM_ANY w => SOME w
| _ => NONE
val new_code =
case imm_val of
SOME i =>
let
val (shift_limit, replace_by_zero) = case mir_operator of
MirTypes.ASR => (MachSpec.bits_per_word+1, false)
| _ => (MachSpec.bits_per_word+2, true)
val i = if i > shift_limit then shift_limit else i
val shift_operation =
if i >= shift_limit andalso replace_by_zero then
MirTypes.UNARY(MirTypes.MOVE, tmp2, MirTypes.GP_IMM_INT 0)
else
MirTypes.BINARY(mir_operator, tmp2, val1, MirTypes.GP_IMM_ANY i)
in
alloc_code ::
(arg_code @
(shift_operation ::
MirTypes.STOREOP
(MirTypes.ST, tmp2, res2, MirTypes.GP_IMM_ANY ~1) ::
MirTypes.NULLARY(MirTypes.CLEAN, tmp2) ::
clean_arg_code))
end
| _ =>
alloc_code ::
(arg_code @
(MirTypes.BINARY(MirTypes.LSR, spare2,val2, MirTypes.GP_IMM_ANY 2) ::
MirTypes.BINARY(mir_operator, tmp2, val1, spare1) ::
MirTypes.STOREOP(MirTypes.ST, tmp2, res2, MirTypes.GP_IMM_ANY ~1) ::
MirTypes.NULLARY(MirTypes.CLEAN, tmp2) ::
MirTypes.NULLARY(MirTypes.CLEAN, spare2) ::
clean_arg_code))
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code, [],NONE,Sexpr.NIL),
[],[])))
end
fun array_code (bytearray,regs,the_code,exn_code) =
let
val (constantp, constant_value) =
(case regs
of Mir_Utils.LIST[Mir_Utils.INT(size), initial] =>
(case size
of MirTypes.GP_IMM_INT v => (true,v )
| _ => (false,0))
| _ => (false,0))
val ((new_reg, code),(new_reg', code')) =
case regs of
Mir_Utils.LIST[ Mir_Utils.INT(size), initial] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE(initial)),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE(Mir_Utils.INT size)))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]))
end)
| _ => Crash.impossible "_mir_cg : array_fn can't code generate arguments "
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val work1 = MirTypes.GC_REG(MirTypes.GC.new())
val work2 = MirTypes.GC_REG(MirTypes.GC.new())
val temp_reg = MirTypes.GC.new()
val temp = MirTypes.GC_REG temp_reg
val temp_gp = MirTypes.GP_GC_REG temp_reg
val count = MirTypes.GC.new()
val main_tag = MirTypes.new_tag()
val loop_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list of
[tag] => tag
| _ => Crash.impossible "bad exn_tag for Bytearray.array"
val after_alignment = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
(if constantp then
(if constant_value >= 0 then
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG main_tag)]
else
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])
else
[MirTypes.TEST(MirTypes.BGE, main_tag, new_reg', MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])),
MirTypes.BLOCK(main_tag,
(if constantp then
[MirTypes.UNARY(MirTypes.MOVE,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_INT constant_value)]
else []) @
(if bytearray then
[MirTypes.COMMENT"ByteArray creation operation",
MirTypes.ALLOCATE(MirTypes.ALLOC_BYTEARRAY, res1,
if constantp then
MirTypes.GP_IMM_INT constant_value
else new_reg')]
else
[MirTypes.COMMENT"Array creation operation",
MirTypes.ALLOCATE(MirTypes.ALLOC_REF, res1,
(if constantp then
MirTypes.GP_IMM_INT constant_value
else new_reg'))]
)@
[MirTypes.COMMENT"Initialise all of the values"] @
(if constantp then
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG count,
MirTypes.GP_IMM_INT constant_value)]
else
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG count,
new_reg')]
) @
(if bytearray then
[MirTypes.BINARY(MirTypes.LSR,
Mir_Utils.reg_from_gp new_reg,
new_reg,
MirTypes.GP_IMM_ANY 2)]
else []) @
[MirTypes.UNARY(MirTypes.MOVE,
temp,
MirTypes.GP_GC_REG result),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG loop_tag)])
::
(if bytearray then
MirTypes.BLOCK
(loop_tag,
[MirTypes.STOREOP(MirTypes.STB,
Mir_Utils.reg_from_gp new_reg,
temp,
MirTypes.GP_IMM_ANY 1),
MirTypes.BINARY(MirTypes.ADDU,
temp,
temp_gp,
MirTypes.GP_IMM_ANY 1),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG count,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_INT ~1),
MirTypes.TEST(MirTypes.BGT,
loop_tag,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_ANY 0),
MirTypes.NULLARY(MirTypes.CLEAN,
Mir_Utils.reg_from_gp new_reg),
MirTypes.NULLARY(MirTypes.CLEAN, temp),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)])
else
MirTypes.BLOCK
(loop_tag,
[MirTypes.STOREOP(MirTypes.STREF,
Mir_Utils.reg_from_gp new_reg,
temp,
MirTypes.GP_IMM_ANY 9),
MirTypes.BINARY(MirTypes.ADDU,
temp,
temp_gp,
MirTypes.GP_IMM_ANY 4),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG count,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_INT ~1),
MirTypes.TEST(MirTypes.BGT,
loop_tag,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_ANY 0),
MirTypes.NULLARY(MirTypes.CLEAN, temp),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]))
::
exn_blocks,
SOME finish_tag,
if bytearray then
Sexpr.ATOM[MirTypes.COMMENT"End bytearray code"]
else
Sexpr.ATOM[MirTypes.UNARY(MirTypes.MOVE,
work1,
MirTypes.GP_IMM_ANY 0),
MirTypes.STOREOP(MirTypes.STREF,
work1,
res1,
MirTypes.GP_IMM_ANY 5),
MirTypes.UNARY(MirTypes.MOVE,
work2,
MirTypes.GP_IMM_INT 1),
MirTypes.STOREOP(MirTypes.STREF,
work2,
res1,
MirTypes.GP_IMM_ANY 1)]),
[], [])))
end
fun floatarray_code (regs,the_code,exn_code) =
let
val (constantp, constant_value) =
(case regs
of Mir_Utils.LIST[Mir_Utils.INT(size), initial] =>
(case size
of MirTypes.GP_IMM_INT v => (true,v )
| _ => (false,0))
| _ => (false,0))
val ((float_reg,code),(new_reg', code')) =
case regs of
Mir_Utils.LIST[Mir_Utils.INT(size),
Mir_Utils.REAL reg] =>
((reg,[]),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE(Mir_Utils.INT size)))
| Mir_Utils.LIST[Mir_Utils.INT(size),
Mir_Utils.INT(MirTypes.GP_GC_REG reg)] =>
let val float_reg = MirTypes.FP_REG(MirTypes.FP.new())
in
((float_reg,
[MirTypes.STOREFPOP(MirTypes.FLD,
float_reg,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY real_offset)]),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE(Mir_Utils.INT size)))
end
| Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
(let
val float_reg = MirTypes.FP.new()
val new_reg' = MirTypes.GC.new()
in
((MirTypes.FP_REG float_reg,
[MirTypes.STOREFPOP(MirTypes.FLD,
MirTypes.FP_REG float_reg,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY ~1)]))
end)
| _ => Crash.impossible "_mir_cg : floatarray can't code generate arguments "
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val work1 = MirTypes.GC_REG(MirTypes.GC.new())
val work2 = MirTypes.GC_REG(MirTypes.GC.new())
val temp_reg = MirTypes.GC.new()
val temp = MirTypes.GC_REG temp_reg
val temp_gp = MirTypes.GP_GC_REG temp_reg
val count = MirTypes.GC.new()
val main_tag = MirTypes.new_tag()
val loop_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list of
[tag] => tag
| _ => Crash.impossible "bad exn_tag for FloatArray.array"
val after_alignment = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
(if constantp then
(if constant_value >= 0 then
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG main_tag)]
else
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])
else
[MirTypes.TEST(MirTypes.BGE, main_tag, new_reg', MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])),
MirTypes.BLOCK(main_tag,
(if constantp then
[MirTypes.UNARY(MirTypes.MOVE,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_INT constant_value)]
else []) @
[MirTypes.COMMENT"FloatArray creation operation"] @
(if constantp then []
else [MirTypes.BINARY(MirTypes.ASL,
temp,
new_reg',
MirTypes.GP_IMM_ANY 3),
MirTypes.BINARY(MirTypes.ADDU,
temp,
temp_gp,
MirTypes.GP_IMM_ANY 4)]) @
[MirTypes.ALLOCATE(MirTypes.ALLOC_BYTEARRAY, res1,
if constantp then
MirTypes.GP_IMM_INT(constant_value*8+4)
else temp_gp)]
@
[MirTypes.COMMENT"Initialise all of the values"] @
(if constantp then
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG count,
MirTypes.GP_IMM_INT constant_value)]
else
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG count,
new_reg')]
) @
[MirTypes.UNARY(MirTypes.MOVE,
temp,
MirTypes.GP_GC_REG result),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG loop_tag)])
::
(MirTypes.BLOCK
(loop_tag,
[MirTypes.STOREFPOP(MirTypes.FST,
float_reg,
temp,
MirTypes.GP_IMM_ANY 5),
MirTypes.BINARY(MirTypes.ADDU,
temp,
temp_gp,
MirTypes.GP_IMM_ANY 8),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG count,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_INT ~1),
MirTypes.TEST(MirTypes.BGT,
loop_tag,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_ANY 0),
MirTypes.NULLARY(MirTypes.CLEAN, temp),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]))
::
exn_blocks,
SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of float_array code"]),
[], [])))
end
fun do_alloc_vector_code (regs,arg_code) =
let
val (constantp, constant_value) =
(case regs of
Mir_Utils.ONE (Mir_Utils.INT size) =>
(case size
of MirTypes.GP_IMM_INT v => (true,v )
| _ => (false,0))
| _ => (false,0))
val (size_reg, get_arg_code) =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg)) => Mir_Utils.send_to_reg regs
| _ => Crash.impossible "_mir_cg : args to alloc_vector_code "
val result = MirTypes.GC.new()
val result_gc = MirTypes.GC_REG result
val temp_reg = MirTypes.GC.new()
val temp = MirTypes.GC_REG temp_reg
val temp_gp = MirTypes.GP_GC_REG temp_reg
val value_reg = MirTypes.GC_REG (MirTypes.GC.new ())
val count = MirTypes.GC.new()
val loop_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(arg_code,
((Sexpr.ATOM
(get_arg_code @
[MirTypes.COMMENT "Vector creation",
MirTypes.ALLOCATE(MirTypes.ALLOC_VECTOR, result_gc,
(if constantp then
MirTypes.GP_IMM_INT constant_value
else
size_reg))] @
[MirTypes.COMMENT"Initialise all of the values"] @
(if constantp then
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG count,
MirTypes.GP_IMM_INT constant_value)]
else
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG count, size_reg)]) @
[MirTypes.UNARY(MirTypes.MOVE,
temp,
MirTypes.GP_GC_REG result),
MirTypes.UNARY(MirTypes.MOVE,
value_reg,
MirTypes.GP_IMM_ANY 0),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG loop_tag)]),
MirTypes.BLOCK(loop_tag,
[MirTypes.TEST(MirTypes.BLE, finish_tag, MirTypes.GP_GC_REG count,MirTypes.GP_IMM_ANY 0),
MirTypes.STOREOP(MirTypes.STREF, value_reg,
temp,
MirTypes.GP_IMM_ANY ~1),
MirTypes.BINARY(MirTypes.ADDU,
temp,
temp_gp,
MirTypes.GP_IMM_ANY 4),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG count,
MirTypes.GP_GC_REG count,
MirTypes.GP_IMM_INT ~1),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG loop_tag)]) ::
[],
SOME finish_tag,
Sexpr.ATOM [MirTypes.NULLARY(MirTypes.CLEAN, temp)]),
[], [])))
end
fun do_alloc_pair_code (regs,arg_code) =
let
val result = MirTypes.GC.new()
val result_gc = MirTypes.GC_REG result
val value_reg = MirTypes.GC_REG (MirTypes.GC.new ())
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(arg_code,
((Sexpr.ATOM
([MirTypes.COMMENT "Pair creation",
MirTypes.ALLOCATE(MirTypes.ALLOC, result_gc,
(MirTypes.GP_IMM_INT 2)),
MirTypes.COMMENT"Initialise the values",
MirTypes.UNARY(MirTypes.MOVE,
value_reg,
MirTypes.GP_IMM_ANY 0),
MirTypes.STOREOP(MirTypes.STREF, value_reg,
result_gc,
MirTypes.GP_IMM_ANY ~1),
MirTypes.STOREOP(MirTypes.STREF, value_reg,
result_gc,
MirTypes.GP_IMM_ANY 3)]),
[],
NONE,
Sexpr.NIL),
[], [])))
end
fun do_alloc_string_code (regs,arg_code) =
let
val (constantp, constant_value) =
(case regs of
Mir_Utils.ONE (Mir_Utils.INT size) =>
(case size
of MirTypes.GP_IMM_INT v => (true,v )
| _ => (false,0))
| _ => (false,0))
val (size_reg, get_arg_code) =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(reg)) => Mir_Utils.send_to_reg regs
| _ => Crash.impossible "_mir_cg : args to alloc_string_code "
val result = MirTypes.GC.new()
val result_gc = MirTypes.GC_REG result
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(arg_code,
((Sexpr.ATOM
(get_arg_code @
[MirTypes.COMMENT "String creation",
MirTypes.ALLOCATE(MirTypes.ALLOC_STRING, result_gc,
(if constantp then
MirTypes.GP_IMM_INT (constant_value)
else
size_reg))]),
[],
NONE,
Sexpr.NIL),
[], [])))
end
fun length_code (bytearray,floatarray,regs,the_code) =
(case regs of
Mir_Utils.ONE(array) =>
let
val (new_reg, code) = Mir_Utils.send_to_reg regs
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @
[MirTypes.COMMENT ((if bytearray then "Byte" else
if floatarray then "Float" else "")
^ "Array length operation"),
MirTypes.STOREOP(MirTypes.LDREF,res1,
Mir_Utils.reg_from_gp new_reg,
MirTypes.GP_IMM_ANY (~3)),
MirTypes.BINARY(MirTypes.LSR,res1,
MirTypes.GP_GC_REG result,
MirTypes.GP_IMM_ANY
(if floatarray then 9 else 6)),
MirTypes.BINARY(MirTypes.ASL,res1,
MirTypes.GP_GC_REG result,
MirTypes.GP_IMM_ANY 2)
]),
[], NONE, Sexpr.NIL), [], [])))
end
| _ =>
Crash.impossible "Array.length not called with one argument")
fun vector_length_code (regs,the_code) =
(case regs of
Mir_Utils.ONE vector =>
let
val (new_reg, code) = Mir_Utils.send_to_reg regs
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @
[MirTypes.COMMENT"Vector length operation",
MirTypes.STOREOP(MirTypes.LDREF, res1, Mir_Utils.reg_from_gp new_reg,
MirTypes.GP_IMM_ANY(~5)),
MirTypes.BINARY(MirTypes.LSR, res1,
MirTypes.GP_GC_REG result, MirTypes.GP_IMM_ANY 6),
MirTypes.BINARY(MirTypes.ASL,res1,
MirTypes.GP_GC_REG result,MirTypes.GP_IMM_ANY 2)
]),
[], NONE, Sexpr.NIL), [], [])))
end
| _ =>
Crash.impossible "Vector.length not called with one argument")
fun sub_code (bytearray, safe,regs,the_code,exn_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[array,offset] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((new_reg, code),(new_reg', code')) =
case regs of
Mir_Utils.LIST[array,offset] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE array))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]))
end)
| _ => Crash.impossible
"can't code generate the argument given to\
                    \ Array.sub in _mir_cg"
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val res2 = MirTypes.GP_GC_REG result
in
if safe then
let
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for Array.sub"
val main_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
[MirTypes.COMMENT "Check the subscript range"] @
(if constantp then
if constant_value < 0 then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 6),
MirTypes.TEST(MirTypes.BGT,
main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY constant_value),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 4),
MirTypes.BINARY(MirTypes.SUBU,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 1),
MirTypes.TEST(MirTypes.BHI, main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])
),
MirTypes.BLOCK
(main_tag,
if bytearray then
[MirTypes.COMMENT "ByteArray subscript operation",
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
new_reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg'),
MirTypes.STOREOP(MirTypes.LDB,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 1),
MirTypes.BINARY(MirTypes.ASL,
res1,
res2,
MirTypes.GP_IMM_ANY 2),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]
else
[MirTypes.COMMENT "Array subscript operation",
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
new_reg',
new_reg),
MirTypes.STOREOP(MirTypes.LDREF,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]) ::
exn_blocks, SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of array sub"]), [], [])))
end
else
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
(if bytearray then
[MirTypes.COMMENT "Unsafe ByteArray subscript operation",
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
new_reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg'),
MirTypes.STOREOP(MirTypes.LDB,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 1),
MirTypes.BINARY(MirTypes.ASL,
res1,
res2,
MirTypes.GP_IMM_ANY 2)]
else
[MirTypes.COMMENT "Unsafe array subscript operation",
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
new_reg',
new_reg),
MirTypes.STOREOP(MirTypes.LDREF,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9)])),
[], NONE, Sexpr.NIL), [], [])))
end
fun floatarray_sub_code (safe,regs,the_code,exn_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[array,offset] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((new_reg, code),(new_reg', code')) =
case regs of
Mir_Utils.LIST[array,offset] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE array))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]))
end)
| _ => Crash.impossible
"can't code generate the argument given to\
                    \ FloatArray.sub in _mir_cg"
val result = MirTypes.FP.new()
val res1 = MirTypes.FP_REG result
in
if safe then
let
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for FloatArray.sub"
val main_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.REAL res1),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @
[MirTypes.BINARY(MirTypes.ASL,
Mir_Utils.reg_from_gp new_reg,
new_reg,
MirTypes.GP_IMM_ANY 1)] @
code' @
[MirTypes.COMMENT "Check the subscript range"] @
(if constantp then
if constant_value < 0 then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9),
MirTypes.TEST(MirTypes.BLE,
exn_tag,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY constant_value),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG main_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9),
MirTypes.BINARY(MirTypes.ASL,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 3),
MirTypes.TEST(MirTypes.BLS ,
exn_tag,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG main_tag)]
)),
MirTypes.BLOCK
(main_tag,
[MirTypes.COMMENT "FloatArray subscript operation",
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
new_reg',
new_reg),
MirTypes.STOREFPOP(MirTypes.FLD,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 5),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]) ::
exn_blocks, SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of floatarray_sub"]), [], [])))
end
else
(Mir_Utils.ONE(Mir_Utils.REAL res1),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
[MirTypes.COMMENT "Unsafe FloatArray subscript operation",
MirTypes.BINARY(MirTypes.ASL,
Mir_Utils.reg_from_gp new_reg,
new_reg,
MirTypes.GP_IMM_ANY 1),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
new_reg',
new_reg),
MirTypes.STOREFPOP(MirTypes.FLD,
res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 5)]),
[], NONE, Sexpr.NIL), [], [])))
end
fun vector_sub_code (safe,regs,the_code,exn_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[vector, offset] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((vector_reg, code),(offset_reg, code')) =
case regs of
Mir_Utils.LIST[vector ,offset] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE vector),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]),
(MirTypes.GP_GC_REG new_reg',[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]))
end)
| _ => Crash.impossible "can't code generate the argument given to Vector.sub in _mir_cg"
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val res2 = MirTypes.GP_GC_REG result
in
if safe then
let
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for Vector.sub"
val finish_tag = MirTypes.new_tag()
val main_tag = MirTypes.new_tag()
in
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
[MirTypes.COMMENT "Check the subscript range"] @
(if constantp then
if constant_value < 0 then
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP
(MirTypes.LDREF, MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp vector_reg, MirTypes.GP_IMM_ANY ~5),
MirTypes.BINARY
(MirTypes.LSR, MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 6),
MirTypes.TEST(MirTypes.BGT, main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY constant_value),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF, MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp vector_reg,
MirTypes.GP_IMM_ANY ~5),
MirTypes.BINARY(MirTypes.LSR,MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 4),
MirTypes.TEST(MirTypes.BHI, main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
offset_reg),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)])
),
MirTypes.BLOCK(main_tag,
[MirTypes.COMMENT "Vector subscript operation",
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
vector_reg, offset_reg),
MirTypes.STOREOP(MirTypes.LD, res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY(~1)),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]) ::
exn_blocks, SOME finish_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of vector sub"]), [], [])))
end
else
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @
[MirTypes.COMMENT "Unsafe vector subscript operation"] @
(if constantp
then
[MirTypes.STOREOP(MirTypes.LD, res1,
Mir_Utils.reg_from_gp vector_reg,
MirTypes.GP_IMM_ANY((constant_value * 4) - 1))]
else
[MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
vector_reg, offset_reg),
MirTypes.STOREOP(MirTypes.LD, res1,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY(~1))])),
[], NONE, Sexpr.NIL), [], [])))
end
fun update_code (bytearray,safe,isIntegral,regs,the_code,exn_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[array,offset,value] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((new_reg, code),(new_reg', code'),(new_reg'', code'')) =
case regs of
Mir_Utils.LIST[array,offset,value] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE array),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE value))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
val new_reg'' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]),
(MirTypes.GP_GC_REG new_reg'',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg'',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 7)]))
end)
| _ => Crash.impossible "_mir_cg : update can't code generate\
                                     \ arguments "
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val reg1 = new_reg'
val scratch = MirTypes.GC.new()
val scratch_reg = MirTypes.GC_REG scratch
val forward = MirTypes.GC.new()
val forward_reg = MirTypes.GC_REG forward
val backward = MirTypes.GC.new()
val backward_reg = MirTypes.GC_REG backward
val unlink_tag = MirTypes.new_tag()
val modified_tag = MirTypes.new_tag()
val main_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val exn_test =
if safe then
let val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for update"
in
[MirTypes.COMMENT "Check the subscript range"] @
(if constantp then
if constant_value < 0 then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 6),
MirTypes.TEST(MirTypes.BGT,
main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY constant_value),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 4),
MirTypes.BINARY(MirTypes.SUBU,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 1),
MirTypes.TEST(MirTypes.BHI,
main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)])
end
else
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG main_tag)]
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @ code'' @ exn_test),
MirTypes.BLOCK
(main_tag,
let
val address = MirTypes.GC.new ()
in
if bytearray then
[MirTypes.COMMENT ((if safe then "" else "Unsafe ")
^ "ByteArray update operation"),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG address,
new_reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG address,
MirTypes.GP_GC_REG address,
new_reg'),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
new_reg'',
MirTypes.GP_IMM_ANY 2),
MirTypes.STOREOP(MirTypes.STB,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GC_REG address,
MirTypes.GP_IMM_ANY 1),
MirTypes.NULLARY(MirTypes.CLEAN,
MirTypes.GC_REG address),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]
else
[MirTypes.COMMENT ((if safe then "" else "Unsafe ")
^ "Array update operation"),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG address,
new_reg',
new_reg),
MirTypes.STOREOP(MirTypes.STREF,
Mir_Utils.reg_from_gp new_reg'',
MirTypes.GC_REG address,
MirTypes.GP_IMM_ANY 9),
MirTypes.NULLARY(MirTypes.CLEAN,
MirTypes.GC_REG address)] @
(if isIntegral then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]
else
[MirTypes.COMMENT"Do we need to unlink it",
MirTypes.STOREOP(MirTypes.LDREF,
forward_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 1),
MirTypes.TEST(MirTypes.BEQ,
finish_tag,
MirTypes.GP_GC_REG forward,
MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG unlink_tag)])
end) ::
(if isIntegral then
exn_blocks
else
MirTypes.BLOCK
(unlink_tag,
[MirTypes.STOREOP(MirTypes.LDREF,
backward_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 5),
MirTypes.TEST(MirTypes.BEQ,
modified_tag,
MirTypes.GP_GC_REG backward,
MirTypes.GP_IMM_INT 0),
MirTypes.COMMENT "Unlink the cell",
MirTypes.STOREOP(MirTypes.STREF,
backward_reg,
forward_reg,
MirTypes.GP_IMM_ANY 8),
MirTypes.STOREOP(MirTypes.STREF,
forward_reg,
backward_reg,
MirTypes.GP_IMM_ANY 4),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG modified_tag)]) ::
MirTypes.BLOCK
(modified_tag,
[MirTypes.UNARY(MirTypes.MOVE,
scratch_reg,
MirTypes.GP_IMM_INT 0),
MirTypes.STOREOP(MirTypes.STREF,
scratch_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 1),
MirTypes.STOREOP(MirTypes.LDREF,
scratch_reg,
MirTypes.GC_REG MirRegisters.implicit,
MirTypes.GP_IMM_ANY
Implicit_Vector.ref_chain),
MirTypes.STOREOP(MirTypes.STREF,
scratch_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 5),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG MirRegisters.global,
reg1,
MirTypes.GP_IMM_ANY ~3),
MirTypes.STOREOP(MirTypes.STREF,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GC_REG MirRegisters.implicit,
MirTypes.GP_IMM_ANY
Implicit_Vector.ref_chain),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]) ::
exn_blocks),
SOME finish_tag,
Sexpr.ATOM[MirTypes.UNARY(MirTypes.MOVE,
res1,
MirTypes.GP_IMM_INT 0)]), [], [])))
end
fun floatarray_update_code (safe,regs,the_code,exn_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[array,offset,value] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((new_reg, code),(new_reg', code'),(float_reg, code'')) =
case regs of
Mir_Utils.LIST[array,offset,Mir_Utils.REAL fp_op] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE array),
(fp_op,[]))
| Mir_Utils.LIST[array,offset,
Mir_Utils.INT(MirTypes.GP_GC_REG reg)] =>
let val float_reg = MirTypes.FP_REG(MirTypes.FP.new())
in
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE array),
(float_reg,
[MirTypes.STOREFPOP(MirTypes.FLD,
float_reg,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY real_offset)]))
end
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
val float_reg = MirTypes.FP.new()
in
((MirTypes.GP_GC_REG new_reg,
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]),
(MirTypes.FP_REG float_reg,
[MirTypes.STOREFPOP(MirTypes.FLD,
MirTypes.FP_REG float_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 7)]))
end)
| _ => Crash.impossible "_mir_cg : update can't code generate\
                                     \ arguments "
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val reg1 = new_reg'
val scratch = MirTypes.GC.new()
val scratch_reg = MirTypes.GC_REG scratch
val forward = MirTypes.GC.new()
val forward_reg = MirTypes.GC_REG forward
val backward = MirTypes.GC.new()
val backward_reg = MirTypes.GC_REG backward
val unlink_tag = MirTypes.new_tag()
val modified_tag = MirTypes.new_tag()
val main_tag = MirTypes.new_tag()
val finish_tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val exn_test =
if safe then
let val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for floatarray_update"
in
[MirTypes.COMMENT "Check the subscript range"] @
(if constantp then
if constant_value < 0 then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9),
MirTypes.TEST(MirTypes.BGT,
main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY constant_value),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)]
else
[MirTypes.STOREOP(MirTypes.LDREF,
MirTypes.GC_REG MirRegisters.global,
Mir_Utils.reg_from_gp new_reg',
MirTypes.GP_IMM_ANY ~3),
MirTypes.BINARY(MirTypes.LSR,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 9),
MirTypes.BINARY(MirTypes.ASL,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY 3),
MirTypes.TEST(MirTypes.BHI,
main_tag,
MirTypes.GP_GC_REG MirRegisters.global,
new_reg),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG exn_tag)])
end
else
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG main_tag)]
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @
[MirTypes.BINARY(MirTypes.ASL,
Mir_Utils.reg_from_gp new_reg,
new_reg,
MirTypes.GP_IMM_ANY 1)
] @
code' @ code'' @ exn_test),
MirTypes.BLOCK
(main_tag,
let
val address = MirTypes.GC.new ()
in
[MirTypes.COMMENT ((if safe then "" else "Unsafe ")
^ "FloatArray update operation"),
MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG address,
new_reg',
new_reg),
MirTypes.STOREFPOP(MirTypes.FST,
float_reg,
MirTypes.GC_REG address,
MirTypes.GP_IMM_ANY 5),
MirTypes.NULLARY(MirTypes.CLEAN,
MirTypes.GC_REG address),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG finish_tag)]
end) ::
exn_blocks,
SOME finish_tag,
Sexpr.ATOM[MirTypes.UNARY(MirTypes.MOVE,
res1,
MirTypes.GP_IMM_INT 0)]), [], [])))
end
fun string_unsafe_update_code (regs,the_code) =
let
val (constantp,constant_value) =
case regs of
Mir_Utils.LIST[array,offset,value] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((string_reg, code),(offset_reg, code'),(value_reg, code'')) =
case regs of
Mir_Utils.LIST[string,offset,value] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE string),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE value))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
val new_reg'' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg'',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg'',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 7)]))
end)
| _ => Crash.impossible "_mir_cg : string update can't code generate arguments "
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val reg1 = offset_reg
val address = MirTypes.GC.new ()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @ code'' @
[MirTypes.COMMENT ("Unsafe string update operation"),
MirTypes.BINARY(MirTypes.LSR, MirTypes.GC_REG address,
offset_reg, MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU, MirTypes.GC_REG address,
MirTypes.GP_GC_REG address, string_reg),
MirTypes.BINARY(MirTypes.LSR, MirTypes.GC_REG MirRegisters.global,
value_reg, MirTypes.GP_IMM_ANY 2),
MirTypes.STOREOP(MirTypes.STB, MirTypes.GC_REG MirRegisters.global,
MirTypes.GC_REG address, MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, MirTypes.GC_REG address),
MirTypes.UNARY(MirTypes.MOVE, res1, MirTypes.GP_IMM_INT 0)]),
[],
NONE,
Sexpr.NIL), [], [])))
end
fun record_unsafe_update_code (regs,the_code) =
let
val (constantp,offset_value) =
case regs of
Mir_Utils.LIST[array,offset,value] =>
(case offset of
Mir_Utils.INT(MirTypes.GP_IMM_INT v) => (true,v)
| _ => (false,0))
| _ => (false,0)
val ((vector_reg, code),(offset_reg, code'),(value_reg, code'')) =
case regs of
Mir_Utils.LIST[vector,offset,value] =>
(Mir_Utils.send_to_new_reg(Mir_Utils.ONE vector),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE offset),
Mir_Utils.send_to_new_reg(Mir_Utils.ONE value))
| Mir_Utils.ONE(Mir_Utils.INT(reg)) =>
(let
val new_reg = MirTypes.GC.new()
val new_reg' = MirTypes.GC.new()
val new_reg'' = MirTypes.GC.new()
in
((MirTypes.GP_GC_REG new_reg,
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg,
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY ~1)]),
(MirTypes.GP_GC_REG new_reg',
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 3)]),
(MirTypes.GP_GC_REG new_reg'',
[MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG new_reg'',
Mir_Utils.reg_from_gp reg,
MirTypes.GP_IMM_ANY 7)]))
end)
| _ => Crash.impossible "_mir_cg : update can't code generate arguments "
val result = MirTypes.GC.new()
val address = MirTypes.GC.new ()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM
(code @ code' @ code'' @
[MirTypes.COMMENT ("Unsafe vector update operation")] @
(if constantp
then
[MirTypes.STOREOP(MirTypes.STREF,
Mir_Utils.reg_from_gp value_reg,
Mir_Utils.reg_from_gp vector_reg,
MirTypes.GP_IMM_ANY ((4 * offset_value) - 1))]
else
[MirTypes.BINARY(MirTypes.ADDU,
MirTypes.GC_REG address, offset_reg,
vector_reg),
MirTypes.STOREOP(MirTypes.STREF,
Mir_Utils.reg_from_gp value_reg,
MirTypes.GC_REG address,
MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN,
MirTypes.GC_REG address)]) @
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG result, MirTypes.GP_IMM_INT 0)]),
[],
NONE,
Sexpr.NIL), [], [])))
end
fun do_ml_call (regs,the_code) =
let
val res = MirTypes.GC.new()
val scratch_reg = MirTypes.GC_REG (MirTypes.GC.new ())
val opcodes = Sexpr.ATOM (Mir_Utils.send_to_given_reg(regs, caller_arg))
val call_code =
Sexpr.ATOM
([MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG caller_closure,
MirTypes.GP_GC_REG caller_arg),
MirTypes.STOREOP(MirTypes.LD, scratch_reg,
MirTypes.GC_REG caller_closure,
MirTypes.GP_IMM_ANY ~1),
MirTypes.BRANCH_AND_LINK(MirTypes.BLR,
MirTypes.REG scratch_reg,
Debugger_Types.null_backend_annotation,
[MirTypes.GC caller_arg]),
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG res,
MirTypes.GP_GC_REG caller_arg)])
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res)),
Mir_Utils.combine(the_code,
((Sexpr.CONS(opcodes, call_code), [],
NONE, Sexpr.NIL), [], [])))
end
fun do_ref (regs,the_code) =
let
val scratch = MirTypes.GC.new()
val scratch_reg = MirTypes.GC_REG scratch
val (new_reg, code) = Mir_Utils.send_to_reg(regs)
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val work1 = MirTypes.GC_REG(MirTypes.GC.new ())
val work2 = MirTypes.GC_REG(MirTypes.GC.new ())
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @
[MirTypes.ALLOCATE(MirTypes.ALLOC_REF, res1, MirTypes.GP_IMM_INT 1),
MirTypes.COMMENT"Allocate a ref cell",
MirTypes.STOREOP(MirTypes.STREF, Mir_Utils.reg_from_gp new_reg, res1,
MirTypes.GP_IMM_ANY 9),
MirTypes.UNARY(MirTypes.MOVE, work1, MirTypes.GP_IMM_ANY 0),
MirTypes.STOREOP(MirTypes.STREF, work1, res1, MirTypes.GP_IMM_ANY 5),
MirTypes.UNARY(MirTypes.MOVE, work2, MirTypes.GP_IMM_INT 1),
MirTypes.STOREOP(MirTypes.STREF, work2, res1, MirTypes.GP_IMM_ANY 1),
MirTypes.COMMENT"Initialise the other pointers"]),
[], NONE, Sexpr.NIL), [], [])))
end
fun do_ml_offset (regs,the_code) =
let
val (reg1, reg2, code) =
case regs of
Mir_Utils.LIST[Mir_Utils.INT reg1, Mir_Utils.INT reg2] =>
(reg1, reg2, [])
| Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
let
val reg1 = MirTypes.GC.new()
val reg2 = MirTypes.GC.new()
val gc_reg = MirTypes.GC_REG reg
in
(MirTypes.GP_GC_REG reg1, MirTypes.GP_GC_REG reg2,
[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG reg1,
gc_reg, MirTypes.GP_IMM_ANY ~1),
MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG reg2,
gc_reg, MirTypes.GP_IMM_ANY 3)])
end
| _ => Crash.impossible"Mir_Cg bad args for ML_OFFSET"
val reg = MirTypes.GC.new()
val gc_reg = MirTypes.GC_REG reg
val gp_gc_reg = MirTypes.GP_GC_REG reg
val op_code =
[MirTypes.BINARY(MirTypes.LSR, gc_reg, reg2,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU, gc_reg, reg1, gp_gc_reg)]
in
(Mir_Utils.ONE(Mir_Utils.INT gp_gc_reg),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @ op_code), [],
NONE, Sexpr.NIL), [], [])))
end
fun do_becomes (isIntegral,regs,the_code) =
let
val scratch = MirTypes.GC.new()
val scratch_reg = MirTypes.GC_REG scratch
val forward = MirTypes.GC.new()
val forward_reg = MirTypes.GC_REG forward
val backward = MirTypes.GC.new()
val backward_reg = MirTypes.GC_REG backward
val unlink_tag = MirTypes.new_tag()
val modified_tag = MirTypes.new_tag()
val already_on_ref_chain_tag = MirTypes.new_tag()
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (reg1, arg, new_code) =
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) => Mir_Utils.destruct_2_tuple reg
| Mir_Utils.LIST[Mir_Utils.INT reg1, arg] =>
let
val (new_reg, code) = Mir_Utils.send_to_reg(Mir_Utils.ONE arg)
in
(reg1, new_reg, code)
end
| _ => Crash.impossible"BECOMES of bad parms"
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @
[MirTypes.COMMENT"Update a reference cell",
MirTypes.STOREOP(MirTypes.STREF,
Mir_Utils.reg_from_gp arg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 9)] @
(if isIntegral then
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG already_on_ref_chain_tag)]
else
[MirTypes.COMMENT"Do we need to unlink it",
MirTypes.STOREOP(MirTypes.LDREF, forward_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 1),
MirTypes.TEST(MirTypes.BEQ, already_on_ref_chain_tag,
MirTypes.GP_GC_REG forward,
MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG unlink_tag)])),
(if isIntegral then [] else
[MirTypes.BLOCK(unlink_tag,
[MirTypes.STOREOP(MirTypes.LDREF, backward_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 5),
MirTypes.TEST(MirTypes.BEQ, modified_tag,
MirTypes.GP_GC_REG backward,
MirTypes.GP_IMM_INT 0),
MirTypes.COMMENT "Unlink the cell",
MirTypes.STOREOP(MirTypes.STREF, backward_reg,
forward_reg,
MirTypes.GP_IMM_ANY 8),
MirTypes.STOREOP(MirTypes.STREF, forward_reg,
backward_reg,
MirTypes.GP_IMM_ANY 4),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG modified_tag)]),
MirTypes.BLOCK(modified_tag,
[MirTypes.UNARY(MirTypes.MOVE, scratch_reg, MirTypes.GP_IMM_INT 0),
MirTypes.STOREOP(MirTypes.STREF,
scratch_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 1),
MirTypes.STOREOP(MirTypes.LDREF, scratch_reg,
MirTypes.GC_REG MirRegisters.implicit,
MirTypes.GP_IMM_ANY Implicit_Vector.ref_chain),
MirTypes.STOREOP(MirTypes.STREF, scratch_reg,
Mir_Utils.reg_from_gp reg1,
MirTypes.GP_IMM_ANY 5),
MirTypes.BINARY(MirTypes.ADDU, MirTypes.GC_REG MirRegisters.global,
reg1,
MirTypes.GP_IMM_ANY ~3),
MirTypes.STOREOP(MirTypes.STREF,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GC_REG MirRegisters.implicit,
MirTypes.GP_IMM_ANY Implicit_Vector.ref_chain),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG already_on_ref_chain_tag)])]),
SOME already_on_ref_chain_tag,
Sexpr.ATOM[MirTypes.UNARY(MirTypes.MOVE, res2, MirTypes.GP_IMM_INT 0),
MirTypes.COMMENT"Dummy result"]), [], [])))
end
fun do_deref (regs,the_code) =
(case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine(the_code,
((Sexpr.ATOM [MirTypes.STOREOP(MirTypes.LDREF, res1,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY 9),
MirTypes.COMMENT"Dereference a ref cell"],
[], NONE, Sexpr.NIL), [], [])))
end
| _ => Crash.impossible"DEREF of non-gc")
fun do_floor (regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val result_gp = MirTypes.GP_GC_REG result
val result_reg = MirTypes.GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for floor"
in
(Mir_Utils.ONE(Mir_Utils.INT result_gp),
Mir_Utils.combine(the_code,
(((case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
let
val fp_op = MirTypes.FP_REG(MirTypes.FP.new())
in
Sexpr.ATOM[MirTypes.STOREFPOP(MirTypes.FLD, fp_op,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY real_offset),
MirTypes.FLOOR(MirTypes.FTOI, exn_tag, result_reg,
fp_op)]
end
| Mir_Utils.ONE(Mir_Utils.REAL reg) =>
Sexpr.ATOM[MirTypes.FLOOR(MirTypes.FTOI, exn_tag, result_reg, reg)]
| _ => Crash.impossible"FLOOR bad value"),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun do_real (regs,the_code) =
let
val result = MirTypes.FP_REG(MirTypes.FP.new())
in
(Mir_Utils.ONE(Mir_Utils.REAL result),
Mir_Utils.combine(the_code,
(((case regs of
Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_GC_REG _)) =>
Sexpr.ATOM[MirTypes.REAL(MirTypes.ITOF, result,
arg)]
| Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_IMM_INT _)) =>
Sexpr.ATOM[MirTypes.REAL(MirTypes.ITOF, result,
arg)]
| Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_IMM_SYMB _)) =>
Sexpr.ATOM[MirTypes.REAL(MirTypes.ITOF, result,
arg)]
| _ => Crash.impossible"convert of non-gc"),
[], NONE, Sexpr.NIL), [], [])))
end
fun do_size (regs,the_code) =
let
val (_, result, code) = make_size_code regs
in
(Mir_Utils.ONE(Mir_Utils.INT result),
Mir_Utils.combine(the_code,
((Sexpr.ATOM code, [], NONE,
Sexpr.NIL), [], [])))
end
fun do_char_chr(regs, the_code, exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for chr"
in
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
let
val (new_reg, code) = Mir_Utils.send_to_reg(regs)
val byte_reg = MirTypes.GC.new()
in
(Mir_Utils.ONE(Mir_Utils.INT res1), Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @
[MirTypes.TEST(MirTypes.BLT, exn_tag, new_reg,
MirTypes.GP_IMM_INT 0),
MirTypes.TEST(MirTypes.BGT, exn_tag, new_reg,
MirTypes.GP_IMM_INT 255),
MirTypes.COMMENT"Fail if out of range 0 <= x <= 255",
MirTypes.ALLOCATE(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 2),
MirTypes.UNARY(MirTypes.MOVE, res2, new_reg)]), exn_blocks,
NONE, Sexpr.NIL), [], [])))
end
| _ => Crash.impossible"do_char_chr"
end
fun do_chr (regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for chr"
in
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
let
val (new_reg, code) = Mir_Utils.send_to_reg(regs)
val byte_reg = MirTypes.GC.new()
in
(Mir_Utils.ONE(Mir_Utils.INT res1), Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @
[MirTypes.TEST(MirTypes.BLT, exn_tag, new_reg,
MirTypes.GP_IMM_INT 0),
MirTypes.TEST(MirTypes.BGT, exn_tag, new_reg,
MirTypes.GP_IMM_INT 255),
MirTypes.COMMENT"Fail if out of range 0 <= x <= 255",
MirTypes.ALLOCATE(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 2),
MirTypes.UNARY(MirTypes.MOVE,MirTypes.GC_REG byte_reg,MirTypes.GP_IMM_ANY 0),
MirTypes.STOREOP(MirTypes.ST, MirTypes.GC_REG byte_reg, res2,
MirTypes.GP_IMM_ANY ~1),
MirTypes.BINARY(MirTypes.LSR,MirTypes.GC_REG byte_reg,
new_reg,MirTypes.GP_IMM_ANY 2),
MirTypes.STOREOP(MirTypes.STB, MirTypes.GC_REG byte_reg, res2,
MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, MirTypes.GC_REG byte_reg)
]), exn_blocks,
NONE, Sexpr.NIL), [], [])))
end
| _ => Crash.impossible"do_chr"
end
fun do_ord (regs,the_code,exn_code) =
let
val (the_ptr, the_size, code) = make_size_code regs
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val res2 = MirTypes.GP_GC_REG result
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for ord"
in
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @
[MirTypes.TEST(MirTypes.BLE, exn_tag, the_size,
MirTypes.GP_IMM_INT 0),
MirTypes.STOREOP(MirTypes.LDB, res1, the_ptr,
MirTypes.GP_IMM_ANY ~1),
MirTypes.BINARY(MirTypes.ASL, res1,res2, MirTypes.GP_IMM_ANY 2)]),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
fun do_ordof (safe,regs,the_code,exn_code) =
let
val (string, offset, new_code) = get_int_pair regs
val result = MirTypes.GC.new()
val res1 = MirTypes.GC_REG result
val res2 = MirTypes.GP_GC_REG result
val (is_constant, constant_value) =
case offset of
MirTypes.GP_IMM_INT x => (true, x)
| _ => (false, 0)
in
if safe
then
let
val (exn_blocks, exn_tag_list) = exn_code
val exn_tag =
case exn_tag_list
of [tag] => tag
| _ => Crash.impossible "no exn_tag for ordof"
in
if is_constant andalso constant_value < 0 then
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine(the_code,
((Sexpr.ATOM[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG exn_tag)],
exn_blocks, NONE, Sexpr.NIL),
[], [])))
else
let
val (the_ptr, the_size, code) =
make_size_code(Mir_Utils.ONE(Mir_Utils.INT string))
val final_instructions =
case offset of
MirTypes.GP_IMM_INT i =>
[MirTypes.STOREOP(MirTypes.LDB, res1, the_ptr,
MirTypes.GP_IMM_ANY(i-1)),
MirTypes.BINARY(MirTypes.ASL, res1,res2,
MirTypes.GP_IMM_ANY 2)]
| reg as MirTypes.GP_GC_REG _ =>
[MirTypes.TEST(MirTypes.BGT, exn_tag,
MirTypes.GP_IMM_INT 0, reg),
MirTypes.BINARY(MirTypes.LSR, res1, reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU, res1, res2,
Mir_Utils.gp_from_reg the_ptr),
MirTypes.STOREOP(MirTypes.LDB, res1, res1,
MirTypes.GP_IMM_ANY ~1),
MirTypes.BINARY(MirTypes.ASL, res1,res2,
MirTypes.GP_IMM_ANY 2)]
| _ => Crash.impossible"Bad args for ordof"
in
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @ code @
(MirTypes.TEST(MirTypes.BLE,exn_tag,the_size,offset) ::
final_instructions)),
exn_blocks, NONE, Sexpr.NIL), [], [])))
end
end
else
let
val string_reg = Mir_Utils.reg_from_gp string
val final_instructions =
case offset of
MirTypes.GP_IMM_INT i =>
[MirTypes.STOREOP(MirTypes.LDB, res1, string_reg,
MirTypes.GP_IMM_ANY(i-1)),
MirTypes.BINARY(MirTypes.ASL, res1,res2,
MirTypes.GP_IMM_ANY 2)]
| reg as MirTypes.GP_GC_REG _ =>
[MirTypes.BINARY(MirTypes.LSR, res1, reg,
MirTypes.GP_IMM_ANY 2),
MirTypes.BINARY(MirTypes.ADDU, res1, res2,string),
MirTypes.STOREOP(MirTypes.LDB, res1, res1,MirTypes.GP_IMM_ANY ~1),
MirTypes.BINARY(MirTypes.ASL, res1,res2,MirTypes.GP_IMM_ANY 2)]
| _ => Crash.impossible"Bad args for ordof"
in
(Mir_Utils.ONE(Mir_Utils.INT res2),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(new_code @ final_instructions),
[], NONE, Sexpr.NIL), [], [])))
end
end
fun do_intabs (regs,the_code,exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val opcode = MirTypes.SUBS
in
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
(Mir_Utils.ONE(Mir_Utils.INT res1), Mir_Utils.combine(the_code,
((Sexpr.ATOM[MirTypes.UNARY(MirTypes.MOVE, res2, reg),
MirTypes.TEST(MirTypes.BGE, tag, reg, MirTypes.GP_IMM_INT 0),
MirTypes.TBINARY(opcode, exn_tag_list, res2,
MirTypes.GP_IMM_INT 0, reg),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG tag)], exn_blocks,
SOME tag,
Sexpr.ATOM[MirTypes.COMMENT"End of intabs"]), [], [])))
| _ => Crash.impossible"do_intabs"
end
fun do_int32abs (Mir_Utils.ONE(Mir_Utils.INT reg), the_code, exn_code) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val tag = MirTypes.new_tag()
val (exn_blocks, exn_tag_list) = exn_code
val opcode = MirTypes.SUB32S
val (w, arg_code, clean_arg_code) =
Mir_Utils.get_word32 (Mir_Utils.INT reg)
val tmp = MirTypes.GC.new()
val tmp2 = MirTypes.GC_REG tmp
val new_code =
arg_code @
[MirTypes.UNARY(MirTypes.MOVE, res2, reg),
MirTypes.TEST(MirTypes.BGE, tag, w, MirTypes.GP_IMM_INT 0),
MirTypes.TBINARY
(opcode, exn_tag_list, tmp2, MirTypes.GP_IMM_INT 0, w)] @
Mir_Utils.save_word32_in_reg(tmp, result) @
[MirTypes.NULLARY(MirTypes.CLEAN, tmp2),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG tag)]
in
(Mir_Utils.ONE(Mir_Utils.INT res1),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code,
exn_blocks,
SOME tag,
Sexpr.ATOM clean_arg_code),
[], [])))
end
| do_int32abs _ = Crash.impossible "do_int32abs"
fun do_call_c (regs,the_code) =
let
val res_reg = MirTypes.GC.new()
fun make_args_for_call_c arg =
let
val get_result =
[MirTypes.CALL_C,
MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG res_reg,
MirTypes.GP_GC_REG caller_arg),
MirTypes.COMMENT"And acquire result"]
in
case arg of
Mir_Utils.ONE (Mir_Utils.INT(reg)) =>
MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG caller_arg, reg) ::
get_result
| _ => Crash.impossible "Bad arguments to make_args_for_call_c"
end
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res_reg)),
Mir_Utils.combine (the_code,
((Sexpr.ATOM(make_args_for_call_c regs), [],
NONE, Sexpr.NIL),
[], [])))
end
fun do_cast_code (regs,the_code) = (regs,the_code)
fun do_notb (regs,the_code) =
let
val (new_reg,code) =
Mir_Utils.send_to_new_reg regs
val result = MirTypes.GC.new()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine(the_code,
((Sexpr.ATOM(code @ [MirTypes.UNARY(MirTypes.NOT,MirTypes.GC_REG result,new_reg)]),
[],NONE,Sexpr.NIL),
[],[])))
end
fun word32_notb (regs,the_code) =
let
val (new_reg, arg_code, clean_arg_code) =
case regs of
(arg as Mir_Utils.ONE(reg)) => Mir_Utils.get_word32 reg
| _ => Crash.impossible "Not32 applied to more than one argument"
val result = MirTypes.GC.new()
val res2 = MirTypes.GC_REG result
val tmp = MirTypes.GC.new()
val tmp2 = MirTypes.GC_REG tmp
val new_code =
MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT 4)
:: arg_code
@ [MirTypes.UNARY (MirTypes.NOT32, tmp2, new_reg),
MirTypes.STOREOP
(MirTypes.ST, tmp2, res2, MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, tmp2)]
@ clean_arg_code
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result)),
Mir_Utils.combine
(the_code,
((Sexpr.ATOM new_code, [], NONE, Sexpr.NIL),
[],[])))
end
fun convert32 (w, size, spills, calls) =
let
val result = MirTypes.GC.new()
val res1 = MirTypes.GP_GC_REG result
val res2 = MirTypes.GC_REG result
val tmp = MirTypes.GC.new()
val (use_zero, zero_reg) =
if w = 0 then
case MirRegisters.zero of
SOME zero_reg => (true, zero_reg)
| _ => (false, MirRegisters.global)
else
(false, MirRegisters.global)
val opcodes =
if MachSpec.has_immediate_stores then
[MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT (size div 8)),
MirTypes.IMMSTOREOP
(MirTypes.ST, MirTypes.GP_IMM_ANY w , res2, MirTypes.GP_IMM_ANY ~1)]
else
if use_zero then
[MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT (size div 8)),
MirTypes.STOREOP
(MirTypes.ST, MirTypes.GC_REG zero_reg , res2, MirTypes.GP_IMM_ANY ~1)]
else
[MirTypes.ALLOCATE
(MirTypes.ALLOC_STRING, res2, MirTypes.GP_IMM_INT (size div 8)),
MirTypes.UNARY
(MirTypes.MOVE, MirTypes.GC_REG tmp, MirTypes.GP_IMM_ANY w),
MirTypes.STOREOP
(MirTypes.ST, MirTypes.GC_REG tmp, res2, MirTypes.GP_IMM_ANY ~1),
MirTypes.NULLARY(MirTypes.CLEAN, MirTypes.GC_REG tmp)]
val code =
((Sexpr.ATOM opcodes, [], NONE, Sexpr.NIL), [], [])
in
(Mir_Utils.ONE(Mir_Utils.INT res1), code, RuntimeEnv.EMPTY, spills, calls)
end
fun mir_cg
error_info
(options as Options.OPTIONS {compiler_options,print_options,...},
lambda_exp, filename, mapping) =
let
val Options.COMPILEROPTIONS {generate_debug_info,
debug_variables,
generate_moduler,
intercept,
interrupt,
opt_handlers,
opt_self_calls,
opt_tail_calls,
local_functions,
...} =
compiler_options
val do_full_lambda_opt = not generate_moduler andalso
not generate_debug_info andalso
not debug_variables andalso
not intercept
val do_local_functions = do_full_lambda_opt andalso local_functions
val _ =
(MirTypes.GC.reset();
MirTypes.NonGC.reset();
MirTypes.FP.reset())
val name_of_setup_function = "<Setup>[" ^ filename ^ ":1,1]"
val insert_interrupt = interrupt
val variable_debug = debug_variables orelse generate_moduler
val opt_first_spill : RuntimeEnv.Offset ref list ref option =
if variable_debug then
SOME (ref ( [ref(RuntimeEnv.OFFSET1 1)]))
else
NONE
fun get_current_spills () =
case opt_first_spill of
NONE => Crash.impossible "current_spills:mir_cg:mir_cg"
| SOME first_spill => !first_spill
fun spill_restorer () =
case opt_first_spill of
NONE => (fn _ => ())
| SOME first_spill =>
let
val old_spill = !first_spill
in
fn _ => first_spill := old_spill
end
fun initialize_spills () =
case opt_first_spill of
NONE => []
| SOME first_spill =>
let
val spill = [ref(RuntimeEnv.OFFSET1 1)]
in
(first_spill := spill;
spill)
end
fun reset_spills spill =
case opt_first_spill of
NONE => ()
| SOME first_spill =>
first_spill := spill
fun append_spill spill =
case opt_first_spill of
NONE => ()
| SOME first_spill =>
first_spill := spill::(!first_spill)
fun head_spill [spill] = spill
| head_spill _ = Crash.impossible "head_spill:mir_cg:mir_cg"
fun new_ref_slot slot = ref (RuntimeEnv.OFFSET1 slot)
fun make_call_code(calls,code) =
if variable_debug then
let
val reg = MirTypes.GC.new()
val current_spills = get_current_spills ()
val ((code,bs,ts,ops),vs,ps) = code
in
((Lists.reducer
(fn (ref_slot,code) =>
Sexpr.CONS(Sexpr.ATOM [MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG reg,
MirTypes.GP_IMM_INT (calls)),
MirTypes.STOREOP(MirTypes.STREF,MirTypes.GC_REG (reg),
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB (MirTypes.GC_SPILL_SLOT
(MirTypes.DEBUG (ref_slot,
"call_code"))))],
code))
(current_spills,code),bs,ts,ops),vs,ps)
end
else
code
val (prim_to_lambda, new_lambda_exp) = make_prim_info (compiler_options, lambda_exp)
val top_lambdas = SimpleUtils.function_lvars new_lambda_exp
val top_lambda_loop_tags =
IntMap.apply
(lists_reducel
(fn (tree, lv) =>
IntMap.define(tree, lv, MirTypes.new_tag()))
(IntMap.empty, top_lambdas))
val (new_exp_and_size as {size=number_of_gc_objects, lexp=new_lambda_exp},debug_information) =
AugLambda.count_gc_objects (options,new_lambda_exp,
true ,
mapping,
name_of_setup_function)
val (new_exp_and_size, top_tags_list, top_closure,
(ext_string_list,ext_var_list,ext_exn_list,ext_str_list,ext_fun_list)) =
do_externals (new_exp_and_size,number_of_gc_objects)
val {lexp = new_lambda_exp, ... } = new_exp_and_size
local
val make_str_order_tree = NewMap.from_list ((op<):string*string->bool, op =)
val ext_string_tree = make_str_order_tree ext_string_list
val ext_var_tree = make_str_order_tree ext_var_list
val ext_exn_tree = make_str_order_tree ext_exn_list
val ext_str_tree = make_str_order_tree ext_str_list
val ext_fun_tree = make_str_order_tree ext_fun_list
in
val find_ext_string = NewMap.apply ext_string_tree
val find_ext_var = NewMap.apply ext_var_tree
val find_ext_exn = NewMap.apply ext_exn_tree
val find_ext_str = NewMap.apply ext_str_tree
val find_ext_fun = NewMap.apply ext_fun_tree
end
fun append_runtime_envs(env1,env2) =
case !env1 of
RuntimeEnv.LIST(env) => RuntimeEnv.LIST(env@[env2])
| RuntimeEnv.EMPTY => env2
| env1 => RuntimeEnv.LIST[env1,env2]
fun has_local_functions ({lexp = AugLambda.FN (vl,e,s,info),size} :: _) =
LambdaTypes.isLocalFn info
| has_local_functions _ = false
fun make_exit_code res =
let
val restemp = MirTypes.GC.new ()
in
((Sexpr.ATOM
(Mir_Utils.send_to_given_reg(res, restemp) @
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG callee_arg,
MirTypes.GP_GC_REG restemp),
MirTypes.RTS]),
[], NONE, Sexpr.NIL), [], [])
end
fun cg_bind({lexp=lexp, size=gc_in_arg},
env, static_offset,start_at,
(closure,funs_in_closure, fn_tag_list, local_fns),
spills,calls) =
let
val (regs,the_code,runtime_env,spills,calls) =
cg_sub(lexp, env, static_offset, start_at,false,
(closure,funs_in_closure, fn_tag_list, local_fns),spills,calls)
val (reg, more_code) =
case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
(case reg of
MirTypes.GP_GC_REG x =>
(MirTypes.GC x, [])
| MirTypes.GP_NON_GC_REG x =>
(MirTypes.NON_GC x, [])
| MirTypes.GP_IMM_INT _ =>
let
val new_reg = MirTypes.GC.new()
in
(MirTypes.GC new_reg,
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG new_reg,
reg)])
end
| MirTypes.GP_IMM_SYMB _ =>
let
val new_reg = MirTypes.GC.new()
in
(MirTypes.GC new_reg,
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG new_reg,
reg)])
end
| _ => Crash.impossible"Untagged value to LET")
| Mir_Utils.ONE(Mir_Utils.REAL(MirTypes.FP_REG fp_reg)) =>
(MirTypes.FLOAT fp_reg, [])
| Mir_Utils.LIST many =>
let val (gc_reg, more_code) = Mir_Utils.tuple_up many
in
(MirTypes.GC gc_reg, more_code)
end
in
(reg,
Mir_Utils.combine(the_code,((Sexpr.ATOM more_code, [], NONE,Sexpr.NIL),[], [])),
static_offset + gc_in_arg, start_at + gc_in_arg,runtime_env,spills,calls)
end
and cg_bind_list([], env,static_offset, start_at,_,spills,calls) =
(no_code, env, static_offset, start_at,RuntimeEnv.LIST(nil),spills,calls)
| cg_bind_list((lv, le) :: rest, env, static_offset, start_at,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
val (reg, code, static_offset', start_at',runtime_env,spills,calls) =
cg_bind(le, env, static_offset, start_at,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls)
val env' = Mir_Env.add_lambda_env ((lv, reg), env)
val (new_code, env, static_offset, start_at, runtime_envs,spills,calls) =
cg_bind_list (rest, env', static_offset', start_at',
(closure,funs_in_closure, fn_tag_list,local_fns), spills,calls)
val runtime_envs =
case runtime_envs of
RuntimeEnv.LIST runtime_envs => runtime_envs
| _ => Crash.impossible "cg_bind_list:_mir_cg.sml"
in
(Mir_Utils.combine(code, new_code), env, static_offset, start_at,
RuntimeEnv.LIST(runtime_env::runtime_envs),spills,calls)
end
and cg_letrec_sub (lexp,env,static_offset,start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),
spills,calls) =
case lexp of
AugLambda.LETREC (lv_list,le_list,{lexp = body_exp,...}) =>
if not (has_local_functions le_list)
then
cg_sub (lexp,env,static_offset,start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),
spills,calls)
else
let
val vars = map #1 lv_list
val tags = map (fn _ => MirTypes.new_tag ()) vars
val args_bodies = map
(fn {lexp = AugLambda.FN((args,fp_args),body,_,_),...} =>
((args,fp_args),body)
| _ => Crash.impossible ("Bad function in letrec"))
le_list
val full_args_list = map #1 args_bodies
val args_list = map #1 full_args_list
val fp_args_list = map #2 full_args_list
val bodies = map #2 args_bodies
val arg_regs_list = map (map (fn _ => MirTypes.GC.new ())) args_list
val fp_arg_regs_list = map (map (fn _ => MirTypes.FP.new ())) fp_args_list
val new_local_functions =
local_fns @
map
(fn (var,tag,args,fp_args) => (var,(tag,(args,fp_args))))
(zip4 (vars,tags, arg_regs_list,fp_arg_regs_list))
val new_envs =
map
(fn (args,regs,fp_args,fp_regs) =>
let
val env =
Lists.reducel
(fn (env,(arg,reg)) => Mir_Env.add_lambda_env ((arg,MirTypes.GC reg),env))
(Mir_Env.empty_lambda_env, zip2 (args,regs))
in
Lists.reducel
(fn (env,(arg,reg)) => Mir_Env.add_lambda_env ((arg,MirTypes.FLOAT reg),env))
(env, zip2 (fp_args,fp_regs))
end)
(zip4 (args_list,arg_regs_list,fp_args_list,fp_arg_regs_list))
val start_offsets = ref 0
val locals_code =
map
(fn ({lexp=body,size=body_size},new_env,tag) =>
let
val (regs, body_code,runtime_env,spills,calls) =
cg_sub (body,new_env,static_offset + !start_offsets,start_at + !start_offsets,tail_position,
(closure,funs_in_closure,fn_tag_list,new_local_functions),
spills,calls)
val ((first,blocks,tag_opt,last),vals,procs) =
Mir_Utils.combine (body_code, make_exit_code (regs))
val _ = start_offsets := !start_offsets + body_size
in
(MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr first) ::
blocks @
(case tag_opt of
NONE => []
| SOME tag =>
[MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr last)]),
vals,
procs)
end)
(zip3 (bodies,new_envs,tags))
fun appendl' ([],[],acc) = rev acc
| appendl' (a::b, rest, acc) = appendl' (b,rest,a::acc)
| appendl' ([], l :: rest, acc) = appendl' (l,rest,acc)
fun appendl l = appendl' ([],l,[])
val local_blocks = appendl (map #1 locals_code)
val local_vals = appendl (map #2 locals_code)
val local_procs = appendl (map #3 locals_code)
val (regs, ((first, body_blocks, tag_opt, last), body_vals, body_procs),runtime_env,spills,calls) =
cg_sub (body_exp,env,static_offset + !start_offsets,start_at + !start_offsets,tail_position,
(closure,funs_in_closure,fn_tag_list,new_local_functions),
spills,calls)
in
(regs, ((first, body_blocks @ local_blocks, tag_opt, last), body_vals @ local_vals, body_procs @ local_procs),
runtime_env,spills,calls)
end
| _ =>
cg_sub (lexp,env,static_offset,start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),
spills,calls)
and cg_sub(arg as AugLambda.APP ({lexp=AugLambda.BUILTIN(prim,primTy), ...},
([{lexp=lexp, size=gc_objects_in_parm}],[]),_),
env,static_offset, start_at, tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
in
(case prim of
Pervasives.LOAD_STRING => load_external (lexp,find_ext_string, RuntimeEnv.EMPTY,spills,calls)
| Pervasives.LOAD_VAR => load_external (lexp,find_ext_var, RuntimeEnv.EMPTY,spills,calls)
| Pervasives.LOAD_EXN => load_external (lexp,find_ext_exn, RuntimeEnv.EMPTY,spills,calls)
| Pervasives.LOAD_STRUCT => load_external (lexp,find_ext_str, RuntimeEnv.EMPTY,spills,calls)
| Pervasives.LOAD_FUNCT => load_external (lexp,find_ext_fun, RuntimeEnv.EMPTY,spills,calls)
| Pervasives.GET_IMPLICIT => do_get_implicit (lexp,RuntimeEnv.EMPTY,spills,calls)
| _ =>
let
val (regs, the_code,runtime_env',spills,calls') =
cg_sub(lexp, env, static_offset, start_at, false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls)
val calls : int ref = ref(calls')
val spills : (int * int * int) ref = ref(spills)
val runtime_env : RuntimeEnv.RuntimeEnv ref = ref(RuntimeEnv.BUILTIN)
fun exn_code_for_prim prim =
let
fun scan [] = ([], [])
| scan (exception_needed::rest) =
let
val exception_packet =
AugLambda.VAR(prim_to_lambda exception_needed)
val (exn_f, exn_b, exn_o, exn_l) =
case cg_sub (AugLambda.RAISE({lexp=AugLambda.STRUCT
[{lexp=exception_packet, size=0},
{lexp=AugLambda.STRUCT[], size=0}],
size=0}),
env, static_offset, start_at, false,
(closure,funs_in_closure,fn_tag_list,local_fns),!spills,!calls)
of (_, ((exn_f, exn_b, exn_o, exn_l), [], []),
runtime_env',spills',calls') =>
(calls := calls';
runtime_env := append_runtime_envs(runtime_env,runtime_env');
spills := spills';
(exn_f, exn_b, exn_o, exn_l))
| _ => Crash.impossible"Bad code for RAISE primitive exception"
val _ =
case exn_o of
NONE => ()
| _ => Crash.impossible"Too much raise code"
val exn_tag = MirTypes.new_tag()
val (rest_blocks,rest_tags) = scan rest
in
(MirTypes.BLOCK(exn_tag, Mir_Utils.contract_sexpr exn_f) :: exn_b @ rest_blocks,
exn_tag :: rest_tags)
end
in
scan (MachPerv.implicit_references prim)
end
fun do_eq (regs,the_code) =
let
val primitive =
case MachPerv.implicit_references Pervasives.EQ
of [p] => p
| _ =>
Crash.impossible
("Mir_Cg: I was expecting the implicit reference of EQ " ^
"to be a single external thing!")
val polymorphic_equality =
AugLambda.VAR(prim_to_lambda primitive)
val (reg, extra_code) =
case Mir_Utils.send_to_reg regs of
(MirTypes.GP_GC_REG reg, code) => (reg, code)
| _ => Crash.impossible"Mir_Utils.send_to_reg doesn't give GP_GC_REG"
val extra_code = ((Sexpr.ATOM extra_code, [], NONE,Sexpr.NIL), [], [])
val lvar = LambdaTypes.new_LVar()
val env' = Mir_Env.add_lambda_env((lvar, MirTypes.GC reg), env)
val (reg, poly_code,runtime_env',spills',calls') =
cg_sub(AugLambda.APP({lexp=polymorphic_equality, size=0},
([{lexp=AugLambda.VAR lvar, size=0}],[]),
Debugger_Types.null_backend_annotation), env',
static_offset + gc_objects_in_parm,
start_at + gc_objects_in_parm,
tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),!spills,!calls)
val _ = calls := calls'
val _ = spills := spills'
val _ = runtime_env := append_runtime_envs(runtime_env,runtime_env')
in
case regs of
Mir_Utils.LIST[Mir_Utils.INT reg1, Mir_Utils.INT reg2] =>
let
val good_tag = MirTypes.new_tag()
val bad_tag = MirTypes.new_tag()
val test_tag = MirTypes.new_tag()
val final_tag = MirTypes.new_tag()
val res_reg = MirTypes.GC.new()
val gc_res_reg = MirTypes.GC_REG res_reg
val new_code =
(Sexpr.ATOM[MirTypes.COMMENT"Start of poly eq",
MirTypes.TEST(MirTypes.BEQ, good_tag, reg1,
reg2),
MirTypes.BINARY(MirTypes.AND,
MirTypes.GC_REG(MirRegisters.global),
reg1, MirTypes.GP_IMM_ANY 3),
MirTypes.BINARY(MirTypes.AND,
MirTypes.GC_REG MirRegisters.global, reg2,
MirTypes.GP_GC_REG MirRegisters.global),
MirTypes.TEST(MirTypes.BNE, bad_tag,
MirTypes.GP_GC_REG(MirRegisters.global),
MirTypes.GP_IMM_ANY 1),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG test_tag)],
[MirTypes.BLOCK(good_tag,
[MirTypes.UNARY(MirTypes.MOVE, gc_res_reg,
MirTypes.GP_IMM_INT 1),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG final_tag)]),
MirTypes.BLOCK(bad_tag,
[MirTypes.UNARY(MirTypes.MOVE, gc_res_reg,
MirTypes.GP_IMM_INT 0),
MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG final_tag)])],
SOME test_tag, Sexpr.NIL)
val new_code =
Mir_Utils.combine
(the_code,
Mir_Utils.combine
((new_code, [], []),
Mir_Utils.combine
(Mir_Utils.combine(extra_code, poly_code),
((Sexpr.ATOM(Mir_Utils.send_to_given_reg(reg, res_reg) @
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG final_tag)]),
[], SOME final_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of poly eq"]),
[], []))))
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG res_reg)),
new_code)
end
| _ =>
(reg,
Mir_Utils.combine(the_code,
Mir_Utils.combine(extra_code, poly_code)))
end
val (result, code) =
case prim of
Pervasives.IDENT_FN => Crash.impossible"Unexpanded IDENT_FN"
| Pervasives.ENTUPLE => do_external_prim prim
| Pervasives.ML_REQUIRE => do_external_prim prim
| Pervasives.ML_CALL => do_ml_call (regs,the_code)
| Pervasives.ML_OFFSET => do_ml_offset (regs,the_code)
| Pervasives.LOAD_VAR => do_external_prim prim
| Pervasives.LOAD_EXN => do_external_prim prim
| Pervasives.LOAD_STRUCT => do_external_prim prim
| Pervasives.LOAD_FUNCT => do_external_prim prim
| Pervasives.REF => do_ref (regs,the_code)
| Pervasives.BECOMES => do_becomes (TypeUtils.is_integral2 primTy,regs,the_code)
| Pervasives.DEREF => do_deref (regs,the_code)
| Pervasives.EXORD => Crash.impossible"APP of non-function"
| Pervasives.EXCHR => Crash.impossible"APP of non-function"
| Pervasives.EXDIV => Crash.impossible"APP of non-function"
| Pervasives.EXSQRT => Crash.impossible"APP of non-function"
| Pervasives.EXEXP => Crash.impossible"APP of non-function"
| Pervasives.EXLN => Crash.impossible"APP of non-function"
| Pervasives.EXIO => Crash.impossible"APP of non-function"
| Pervasives.EXMATCH => Crash.impossible"APP of non-function"
| Pervasives.EXBIND => Crash.impossible"APP of non-function"
| Pervasives.EXINTERRUPT => Crash.impossible"APP of non-function"
| Pervasives.EXOVERFLOW => Crash.impossible"APP of non-function"
| Pervasives.EXRANGE => Crash.impossible"APP of non-function"
| Pervasives.MAP => do_external_prim prim
| Pervasives.UMAP => do_external_prim prim
| Pervasives.REV => do_external_prim prim
| Pervasives.NOT => Crash.impossible"Primitive NOT"
| Pervasives.ABS => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.FLOOR => do_floor (regs,the_code,exn_code_for_prim prim)
| Pervasives.REAL => do_real (regs,the_code)
| Pervasives.SQRT => tagged_unary_fcalc(MirTypes.FSQRTV,regs,the_code,exn_code_for_prim prim)
| Pervasives.SIN => unary_fcalc (MirTypes.FSIN,regs,the_code)
| Pervasives.COS => unary_fcalc (MirTypes.FCOS,regs,the_code)
| Pervasives.ARCTAN => unary_fcalc (MirTypes.FATAN,regs,the_code)
| Pervasives.EXP => tagged_unary_fcalc(MirTypes.FETOXV,regs,the_code,exn_code_for_prim prim)
| Pervasives.LN => tagged_unary_fcalc(MirTypes.FLOGEV,regs,the_code,exn_code_for_prim prim)
| Pervasives.SIZE => do_size (regs,the_code)
| Pervasives.CHR => do_chr (regs,the_code,exn_code_for_prim prim)
| Pervasives.ORD => do_ord (regs,the_code,exn_code_for_prim prim)
| Pervasives.CHARCHR => do_char_chr (regs,the_code,exn_code_for_prim prim)
| Pervasives.CHARORD => (regs, the_code)
| Pervasives.ORDOF => do_ordof (true,regs,the_code,exn_code_for_prim prim)
| Pervasives.EXPLODE => do_external_prim prim
| Pervasives.IMPLODE => do_external_prim prim
| Pervasives.FDIV => tagged_binary_fcalc(MirTypes.FDIVV,regs,the_code,exn_code_for_prim prim)
| Pervasives.DIV => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.MOD => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.INTDIV => tagged_binary_calc(MirTypes.DIVS,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTMOD => tagged_binary_calc(MirTypes.MODS,regs,the_code,exn_code_for_prim prim)
| Pervasives.PLUS => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.STAR => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.MINUS => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.HAT => do_external_prim prim
| Pervasives.AT => do_external_prim prim
| Pervasives.NE => do_external_prim prim
| Pervasives.LESS => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.GREATER => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.LESSEQ => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.GREATEREQ => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.O => Crash.impossible"Primitive composition"
| Pervasives.UMINUS => Crash.impossible"Unresolved overloaded primitive"
| Pervasives.EQFUN => do_external_prim prim
| Pervasives.EQ => do_eq (regs,the_code)
| Pervasives.REALPLUS => tagged_binary_fcalc(MirTypes.FADDV,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTPLUS => tagged_binary_calc(MirTypes.ADDS,regs,the_code,exn_code_for_prim prim)
| Pervasives.UNSAFEINTPLUS => binary_calc (MirTypes.ADDU,regs,the_code)
| Pervasives.UNSAFEINTMINUS => binary_calc (MirTypes.SUBU,regs,the_code)
| Pervasives.WORDPLUS => binary_calc (MirTypes.ADDU,regs,the_code)
| Pervasives.WORDMINUS => binary_calc (MirTypes.SUBU,regs,the_code)
| Pervasives.WORDSTAR => binary_calc (MirTypes.MULU,regs,the_code)
| Pervasives.WORDDIV => tagged_binary_calc (MirTypes.DIVU,regs, the_code, exn_code_for_prim prim)
| Pervasives.WORDMOD => tagged_binary_calc (MirTypes.MODU,regs, the_code, exn_code_for_prim prim)
| Pervasives.INT32PLUS => tagged32_binary_calc (MirTypes.ADD32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32MINUS => tagged32_binary_calc (MirTypes.SUB32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32STAR => tagged32_binary_calc (MirTypes.MUL32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32DIV => tagged32_binary_calc (MirTypes.DIV32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32MOD => tagged32_binary_calc (MirTypes.MOD32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32UMINUS => int32_unary_negate(MirTypes.SUB32S,regs,the_code,exn_code_for_prim prim)
| Pervasives.INT32ABS => do_int32abs (regs,the_code,exn_code_for_prim prim)
| Pervasives.WORD32PLUS => untagged32_binary_calc (MirTypes.ADDU,regs,the_code)
| Pervasives.WORD32MINUS => untagged32_binary_calc (MirTypes.SUBU,regs,the_code)
| Pervasives.WORD32STAR => untagged32_binary_calc (MirTypes.MUL32U,regs,the_code)
| Pervasives.WORD32DIV => tagged32_binary_calc (MirTypes.DIV32U,regs,the_code,exn_code_for_prim prim)
| Pervasives.WORD32MOD => tagged32_binary_calc (MirTypes.MOD32U,regs,the_code,exn_code_for_prim prim)
| Pervasives.REALSTAR => tagged_binary_fcalc(MirTypes.FMULV,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTSTAR => tagged_binary_calc(MirTypes.MULS,regs,the_code,exn_code_for_prim prim)
| Pervasives.REALMINUS => tagged_binary_fcalc(MirTypes.FSUBV,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTMINUS => tagged_binary_calc(MirTypes.SUBS,regs,the_code,exn_code_for_prim prim)
| Pervasives.REALUMINUS => tagged_unary_fcalc(MirTypes.FNEGV,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTUMINUS => unary_negate(MirTypes.SUBS,regs,the_code,exn_code_for_prim prim)
| Pervasives.INTLESS => test (MirTypes.BLT,regs,the_code)
| Pervasives.CHARLT => test (MirTypes.BLT,regs,the_code)
| Pervasives.WORDLT => test (MirTypes.BLO,regs,the_code)
| Pervasives.REALLESS => ftest(MirTypes.FBLT, true,regs,the_code)
| Pervasives.INTGREATER => test (MirTypes.BGT,regs,the_code)
| Pervasives.CHARGT => test (MirTypes.BGT,regs,the_code)
| Pervasives.WORDGT => test (MirTypes.BHI,regs,the_code)
| Pervasives.REALGREATER => ftest(MirTypes.FBLT, false,regs,the_code)
| Pervasives.INTLESSEQ => test (MirTypes.BLE,regs,the_code)
| Pervasives.CHARLE => test (MirTypes.BLE,regs,the_code)
| Pervasives.WORDLE => test (MirTypes.BLS,regs,the_code)
| Pervasives.REALLESSEQ => ftest(MirTypes.FBLE, true,regs,the_code)
| Pervasives.INTGREATEREQ => test (MirTypes.BGE,regs,the_code)
| Pervasives.CHARGE => test (MirTypes.BGE,regs,the_code)
| Pervasives.WORDGE => test (MirTypes.BHS,regs,the_code)
| Pervasives.REALGREATEREQ => ftest(MirTypes.FBLE, false,regs,the_code)
| Pervasives.INTEQ => test (MirTypes.BEQ,regs,the_code)
| Pervasives.INTNE => test (MirTypes.BNE,regs,the_code)
| Pervasives.CHAREQ => test (MirTypes.BEQ,regs,the_code)
| Pervasives.CHARNE => test (MirTypes.BNE,regs,the_code)
| Pervasives.WORDEQ => test (MirTypes.BEQ,regs,the_code)
| Pervasives.WORDNE => test (MirTypes.BNE,regs,the_code)
| Pervasives.REALEQ => ftest(MirTypes.FBEQ, true,regs,the_code)
| Pervasives.REALNE => ftest(MirTypes.FBNE, true,regs,the_code)
| Pervasives.INT32EQ => test32 (MirTypes.BEQ,regs,the_code)
| Pervasives.INT32NE => test32 (MirTypes.BNE,regs,the_code)
| Pervasives.INT32LESS => test32 (MirTypes.BLT,regs,the_code)
| Pervasives.INT32LESSEQ => test32 (MirTypes.BLE,regs,the_code)
| Pervasives.INT32GREATER => test32 (MirTypes.BGT,regs,the_code)
| Pervasives.INT32GREATEREQ => test32 (MirTypes.BGE,regs,the_code)
| Pervasives.WORD32EQ => test32 (MirTypes.BEQ,regs,the_code)
| Pervasives.WORD32NE => test32 (MirTypes.BNE,regs,the_code)
| Pervasives.WORD32LT => test32 (MirTypes.BLO,regs,the_code)
| Pervasives.WORD32LE => test32 (MirTypes.BLS,regs,the_code)
| Pervasives.WORD32GT => test32 (MirTypes.BHI,regs,the_code)
| Pervasives.WORD32GE => test32 (MirTypes.BHS,regs,the_code)
| Pervasives.STRINGEQ => do_external_prim prim
| Pervasives.STRINGNE => do_external_prim prim
| Pervasives.STRINGLT => do_external_prim prim
| Pervasives.STRINGLE => do_external_prim prim
| Pervasives.STRINGGT => do_external_prim prim
| Pervasives.STRINGGE => do_external_prim prim
| Pervasives.INTABS => do_intabs (regs,the_code,exn_code_for_prim prim)
| Pervasives.REALABS => tagged_unary_fcalc(MirTypes.FABSV,regs,the_code,exn_code_for_prim prim)
| Pervasives.CALL_C => do_call_c (regs,the_code)
| Pervasives.LOAD_STRING => Crash.impossible "Impossible load_string"
| Pervasives.ARRAY_FN =>
array_code (false,regs,the_code,exn_code_for_prim prim)
| Pervasives.LENGTH => length_code (false,false,regs,the_code)
| Pervasives.SUB =>
sub_code (false, true,regs,the_code,exn_code_for_prim prim)
| Pervasives.UPDATE =>
update_code (false, true,TypeUtils.is_integral3 primTy,
regs,the_code,exn_code_for_prim prim)
| Pervasives.UNSAFE_SUB =>
sub_code (false, false,regs,the_code,exn_code_for_prim prim)
| Pervasives.UNSAFE_UPDATE =>
update_code(false, false,TypeUtils.is_integral3 primTy,
regs,the_code,exn_code_for_prim prim)
| Pervasives.BYTEARRAY =>
array_code (true,regs,the_code,exn_code_for_prim prim)
| Pervasives.BYTEARRAY_LENGTH => length_code (true,false,regs,the_code)
| Pervasives.BYTEARRAY_SUB =>
sub_code (true, true,regs,the_code,exn_code_for_prim prim)
| Pervasives.BYTEARRAY_UNSAFE_SUB =>
sub_code (true, false,regs,the_code,exn_code_for_prim prim)
| Pervasives.BYTEARRAY_UPDATE =>
update_code (true, true,true,regs, the_code,
exn_code_for_prim prim)
| Pervasives.BYTEARRAY_UNSAFE_UPDATE =>
update_code(true, false, true,regs,the_code,
exn_code_for_prim prim)
| Pervasives.FLOATARRAY =>
floatarray_code(regs,the_code,exn_code_for_prim prim)
| Pervasives.FLOATARRAY_LENGTH =>
length_code (false,true,regs,the_code)
| Pervasives.FLOATARRAY_SUB =>
floatarray_sub_code (true,regs,the_code,exn_code_for_prim prim)
| Pervasives.FLOATARRAY_UPDATE =>
floatarray_update_code (true,regs, the_code,
exn_code_for_prim prim)
| Pervasives.FLOATARRAY_UNSAFE_SUB =>
floatarray_sub_code (false,regs,the_code,exn_code_for_prim prim)
| Pervasives.FLOATARRAY_UNSAFE_UPDATE =>
floatarray_update_code (false,regs, the_code,
exn_code_for_prim prim)
| Pervasives.VECTOR_LENGTH => vector_length_code (regs,the_code)
| Pervasives.VECTOR_SUB => vector_sub_code (true,regs,the_code,exn_code_for_prim prim)
| Pervasives.VECTOR => do_external_prim prim
| Pervasives.EXSIZE => Crash.impossible"APP of non-function"
| Pervasives.EXSUBSCRIPT => Crash.impossible"APP of non-function"
| Pervasives.ANDB => binary_calc (MirTypes.AND,regs,the_code)
| Pervasives.ORB => binary_calc (MirTypes.OR,regs,the_code)
| Pervasives.XORB => binary_calc (MirTypes.EOR,regs,the_code)
| Pervasives.WORDANDB => binary_calc (MirTypes.AND,regs,the_code)
| Pervasives.WORDORB => binary_calc (MirTypes.OR,regs,the_code)
| Pervasives.WORDXORB => binary_calc (MirTypes.EOR,regs,the_code)
| Pervasives.WORD32ANDB => untagged32_binary_calc (MirTypes.AND,regs,the_code)
| Pervasives.WORD32ORB => untagged32_binary_calc (MirTypes.OR,regs,the_code)
| Pervasives.WORD32XORB => untagged32_binary_calc (MirTypes.EOR,regs,the_code)
| Pervasives.LSHIFT => do_shift_operator(MirTypes.ASL,false,regs,the_code)
| Pervasives.RSHIFT => do_shift_operator(MirTypes.LSR,true,regs,the_code)
| Pervasives.ARSHIFT => do_shift_operator(MirTypes.ASR,true,regs,the_code)
| Pervasives.WORDLSHIFT =>
do_shift_operator(MirTypes.ASL,false,regs,the_code)
| Pervasives.WORDRSHIFT =>
do_shift_operator(MirTypes.LSR,true,regs,the_code)
| Pervasives.WORDARSHIFT =>
do_shift_operator(MirTypes.ASR,true,regs,the_code)
| Pervasives.WORD32LSHIFT =>
full_machine_word_shift_operator (MirTypes.ASL, regs, the_code)
| Pervasives.WORD32RSHIFT =>
full_machine_word_shift_operator (MirTypes.LSR, regs, the_code)
| Pervasives.WORD32ARSHIFT =>
full_machine_word_shift_operator (MirTypes.ASR, regs, the_code)
| Pervasives.NOTB => do_notb (regs,the_code)
| Pervasives.WORDNOTB => do_notb (regs,the_code)
| Pervasives.WORD32NOTB => word32_notb (regs,the_code)
| Pervasives.CAST => do_cast_code (regs,the_code)
| Pervasives.ALLOC_STRING => do_alloc_string_code (regs,the_code)
| Pervasives.ALLOC_VECTOR => do_alloc_vector_code (regs,the_code)
| Pervasives.ALLOC_PAIR => do_alloc_pair_code (regs,the_code)
| Pervasives.RECORD_UNSAFE_SUB => vector_sub_code (false,regs,the_code,exn_code_for_prim prim)
| Pervasives.RECORD_UNSAFE_UPDATE => record_unsafe_update_code (regs,the_code)
| Pervasives.STRING_UNSAFE_SUB => do_ordof (false,regs,the_code,exn_code_for_prim prim)
| Pervasives.STRING_UNSAFE_UPDATE => string_unsafe_update_code (regs, the_code)
| Pervasives.GET_IMPLICIT => do_external_prim "get_implicit"
val (runtime_env,calls) = (!runtime_env,!calls)
val (code,option,calls) =
case runtime_env of
RuntimeEnv.BUILTIN => (code,NONE,calls)
| _ => (make_call_code(calls+1,code),
SOME calls',
calls+1)
in
(result, code,
if variable_debug then
RuntimeEnv.APP(runtime_env,runtime_env',option)
else
RuntimeEnv.EMPTY,
!spills,calls)
end)
end
| cg_sub(arg as AugLambda.LET ((lvar, info,lexp2), {lexp=lexp1,...}),
env,static_offset, start_at, tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),
spills,calls) =
let
val (reg, code, static_offset', start_at', runtime_env,
spills as (gc_spills,non_gc_spills,fp_spills),calls) =
cg_bind(lexp2, env, static_offset, start_at,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls)
val debug_info =
case info of
SOME (ref debug_info) => debug_info
| _ => RuntimeEnv.NOVARINFO
val ((code,spills),debug_info) =
case debug_info of
RuntimeEnv.NOVARINFO => ((code,spills),RuntimeEnv.NOVARINFO)
| RuntimeEnv.VARINFO (name,info,tyvar_slot) =>
let
fun find_tyvar_slot (tyvar_slot,slot) =
case tyvar_slot of
NONE =>
(ref (RuntimeEnv.OFFSET1 slot),false)
| SOME offset =>
(offset := RuntimeEnv.OFFSET1 slot;
(offset,true))
in
(case reg of
MirTypes.GC reg =>
let
val slot = gc_spills+1
val (ref_slot,has_tyvar_slot) = find_tyvar_slot (tyvar_slot,slot)
in
((Mir_Utils.combine(code,
((Sexpr.ATOM [MirTypes.STOREOP(MirTypes.STREF,MirTypes.GC_REG (reg),
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB (MirTypes.GC_SPILL_SLOT (MirTypes.DEBUG (ref_slot,name))))],
[],NONE, Sexpr.NIL), [], [])),
(slot,non_gc_spills,fp_spills)),
if has_tyvar_slot then RuntimeEnv.NOVARINFO
else RuntimeEnv.VARINFO (name,info,SOME(ref_slot)))
end
| MirTypes.NON_GC reg =>
let
val slot = non_gc_spills+1
val (ref_slot,has_tyvar_slot) = find_tyvar_slot (tyvar_slot,slot)
in
((Mir_Utils.combine(code,
((Sexpr.ATOM [MirTypes.STOREOP(MirTypes.STREF,
MirTypes.NON_GC_REG reg,
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB (MirTypes.NON_GC_SPILL_SLOT
(MirTypes.DEBUG (ref_slot,name))))],
[],NONE, Sexpr.NIL), [], [])),
(gc_spills,slot,fp_spills)),
if has_tyvar_slot then RuntimeEnv.NOVARINFO
else RuntimeEnv.VARINFO(name,info,SOME(ref_slot)))
end
| MirTypes.FLOAT reg =>
let
val slot = fp_spills+1
val (ref_slot,has_tyvar_slot) = find_tyvar_slot (tyvar_slot,slot)
in
((Mir_Utils.combine
(code,
((Sexpr.ATOM [MirTypes.STOREFPOP(MirTypes.FSTREF,
MirTypes.FP_REG reg,
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB (MirTypes.FP_SPILL_SLOT
(MirTypes.DEBUG (ref_slot,name))))],
[],NONE, Sexpr.NIL), [], [])),
(gc_spills,non_gc_spills,slot)),
if has_tyvar_slot then RuntimeEnv.NOVARINFO
else RuntimeEnv.VARINFO (name,info,SOME(ref_slot)))
end)
end
val env' = Mir_Env.add_lambda_env((lvar, reg), env)
val (rest_regs, rest_code, runtime_env',spills,calls) =
cg_sub(lexp1, env', static_offset', start_at',tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls)
in
(rest_regs, Mir_Utils.combine(code, rest_code),
if variable_debug then
RuntimeEnv.LET([(debug_info,runtime_env)], runtime_env')
else
RuntimeEnv.EMPTY,spills,calls)
end
| cg_sub(arg as AugLambda.APP({lexp=le1,size=gc_objects_in_call},
(arg_list,fp_arg_list),
debugger_information),
env,static_offset, start_at,tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
fun lookup (lvar, l) =
if (opt_self_calls orelse do_local_functions) andalso tail_position
then
let
fun lookup' (lvar, []) = NONE
| lookup' (lvar, (lvar',stuff)::rest) =
if lvar = lvar' then SOME stuff else lookup' (lvar,rest)
in
lookup' (lvar,l)
end
else NONE
val dummy_info =
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_IMM_INT 0)),
((Sexpr.ATOM [], [], NONE, Sexpr.NIL), [], []),
RuntimeEnv.EMPTY,spills,calls+1)
val ((fn_reg,fn_code,runtime_env,spills,calls'), calltype) =
case le1 of
AugLambda.VAR lvar =>
(case lookup (lvar, local_fns) of
SOME (tag,(arglist,fp_arglist)) => (dummy_info,Mir_Utils.LOCAL (tag,arglist,fp_arglist))
| _ =>
let
val ((reg, code), pos, is_same_set) =
Mir_Utils.cg_lvar (lvar, env, closure, funs_in_closure)
in
if is_same_set then
(dummy_info, Mir_Utils.SAMESET pos)
else
((Mir_Utils.ONE reg,
((Sexpr.ATOM code, [], NONE, Sexpr.NIL), [], []),
RuntimeEnv.EMPTY,spills,calls+1),
Mir_Utils.EXTERNAL)
end)
| _ =>
(cg_sub(le1, env, static_offset, start_at,false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls+1),
Mir_Utils.EXTERNAL)
val gc_objects_in_arglist =
Lists.reducel
(fn (n,{size,lexp}) => n + size)
(0,arg_list)
val (arg_reg, arg_code,runtime_env',spills,calls') =
cg_sub(AugLambda.STRUCT arg_list, env,
static_offset + gc_objects_in_call,
start_at + gc_objects_in_call, false,
(closure,funs_in_closure, fn_tag_list,local_fns),
spills,calls')
val (fp_arg_reg, fp_arg_code,runtime_env'',spills,calls') =
cg_sub(AugLambda.STRUCT fp_arg_list, env,
static_offset + gc_objects_in_call + gc_objects_in_arglist,
start_at + gc_objects_in_call + gc_objects_in_arglist,
false,
(closure,funs_in_closure, fn_tag_list,local_fns),
spills,calls')
val (reg, fn_code, arg_code, call_code) =
Mir_Utils.do_multi_app(debugger_information,
fn_reg, fn_code,
arg_reg, arg_code,
fp_arg_reg,fp_arg_code,
calltype,
funs_in_closure, fn_tag_list,
tail_position andalso opt_tail_calls)
val code as ((first, blocks, tag_opt, last), values, procs_list) =
Mir_Utils.combine
(fn_code,
Mir_Utils.combine
(arg_code, make_call_code(calls+1,call_code)))
val runtime_env =
if variable_debug then
RuntimeEnv.APP (RuntimeEnv.APP(runtime_env,runtime_env',NONE),runtime_env'',NONE)
else
RuntimeEnv.EMPTY
in
(reg, code, runtime_env,spills,calls')
end
| cg_sub(arg as AugLambda.STRUCT le_list, env, static_offset, start_at,_,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
(case le_list of
[] => (Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_IMM_INT 0)), no_code,
RuntimeEnv.EMPTY,spills,calls)
| _ =>
let
fun make_code([],_,spills,calls) = ([],no_code,[],spills,calls)
| make_code({lexp=le, size=size} :: rest, pos,spills,calls) =
let
val (reg,code,runtime_env,spills,calls) =
cg_sub(le, env, static_offset + pos, start_at + pos, false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls)
val (reg_list,code',runtime_envs,spills,calls) =
make_code(rest, pos+size,spills,calls)
in
(reg::reg_list,Mir_Utils.combine(code, code'),
runtime_env::runtime_envs,spills,calls)
end
val (reg_list,the_code,runtime_env,spills,calls) = make_code(le_list, 0, spills,calls)
val new_reg_list =
map
(fn Mir_Utils.ONE reg =>
(reg, [MirTypes.COMMENT("Argument to tuple")])
| Mir_Utils.LIST regs =>
let
val (reg, code) = Mir_Utils.tuple_up regs
in
(Mir_Utils.INT(MirTypes.GP_GC_REG reg),
MirTypes.COMMENT("Argument to tuple") :: code)
end)
reg_list
val new_code =
lists_reducel
(fn (code', (_,code)) => Sexpr.CONS(code', Sexpr.ATOM code))
(Sexpr.NIL, new_reg_list)
val runtime_env =
if variable_debug
then RuntimeEnv.STRUCT runtime_env
else
RuntimeEnv.EMPTY
in
(Mir_Utils.LIST(map #1 new_reg_list),
Mir_Utils.combine(the_code, ((new_code, [], NONE,
Sexpr.NIL), [], [])),
runtime_env,spills,calls)
end)
| cg_sub(arg as AugLambda.SELECT({index, size}, {lexp=lexp, ...}),
env, static_offset, start_at,_,
(closure,funs_in_closure, fn_tag_list,local_fns),
spills,calls) =
let
val (regs, the_code,runtime_env,spills,calls) =
cg_sub(lexp, env, static_offset,start_at, false,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls)
val offset = (index * 4) - 1
in
case regs of
Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG reg)) =>
let
val new_reg = MirTypes.GC.new()
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG new_reg)), Mir_Utils.combine(the_code,
((Sexpr.ATOM[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg,
MirTypes.GC_REG reg,
MirTypes.GP_IMM_ANY offset),
MirTypes.COMMENT("Destructure tuple")], [], NONE,
Sexpr.NIL), [], [])),
if variable_debug
then RuntimeEnv.SELECT(index,runtime_env)
else RuntimeEnv.EMPTY,
spills,calls)
end
| Mir_Utils.ONE _ => Crash.impossible"SELECT(Mir_Utils.ONE(bad value))"
| Mir_Utils.LIST many =>
((Mir_Utils.ONE(Lists.nth(index, many)), the_code,
if variable_debug
then RuntimeEnv.SELECT(index,runtime_env)
else RuntimeEnv.EMPTY,spills,calls)
handle Lists.Nth =>
Crash.impossible
("Trying to select item " ^
Int.toString index ^ " from list size " ^
Int.toString(length many) ^
" with struct size " ^ Int.toString size ^ "\n"))
end
| cg_sub(arg as AugLambda.SWITCH
({lexp=lexp,size=arg_size}, info, tag_le_list, dflt),
env, static_offset, start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls) =
let
val positions = do_pos1(arg_size, tag_le_list)
val list_size =
lists_reducel (fn (x:int, (_, {size=size, lexp=_})) => x + size)
(0, tag_le_list)
val tag_sizes =
lists_reducel
(fn (x, (tag,_)) => x + count_gc_tags tag)
(0, tag_le_list)
val tag_test_mask = 3
val (gc_spills,non_gc_spills,fp_spills) = spills
val slot = gc_spills+1
val ref_slot = new_ref_slot slot
in
let
val tag_positions = do_pos2(arg_size + list_size, tag_le_list)
val main_tag = MirTypes.new_tag()
val end_tag = MirTypes.new_tag()
val main_branch = MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG main_tag)
val final_branch = MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG end_tag)
val is_con =
case tag_le_list of
(AugLambda.IMM_TAG _,_) :: _ => true
| (AugLambda.VCC_TAG _,_) :: _ => true
| _ => false
val dflt_exists = case dflt of
NONE => false
| SOME _ => true
val end_reg = MirTypes.GC.new()
fun get_blocks_for_dflts(dflt, static_offset, start_at, comment) =
case dflt of
NONE => ([], end_tag, [], [],RuntimeEnv.EMPTY,spills,calls)
| SOME {lexp,size} =>
let
val (regs, body_code,runtime_env,spills,calls) =
cg_sub(lexp, env, static_offset, start_at,tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),
(slot,non_gc_spills,fp_spills),calls)
val debug_code =
if variable_debug then
let
val new_reg = MirTypes.GC.new()
in
((Sexpr.ATOM [MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG (new_reg),
MirTypes.GP_IMM_INT 0),
MirTypes.STOREOP(MirTypes.STREF,
MirTypes.GC_REG (new_reg),
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB
(MirTypes.GC_SPILL_SLOT
(MirTypes.DEBUG (ref_slot,"default for switch"))))],
[],NONE, Sexpr.NIL), [], [])
end
else
no_code
val result_code =
(case regs of
Mir_Utils.ONE(Mir_Utils.INT reg) =>
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG end_reg,
reg)]
| Mir_Utils.ONE(Mir_Utils.REAL reg) =>
Mir_Utils.save_real_to_reg(reg,
MirTypes.GC_REG end_reg)
| Mir_Utils.LIST many =>
(#2 (Mir_Utils.tuple_up_in_reg (many,end_reg))))
val full_body_code =
Mir_Utils.combine (debug_code,
Mir_Utils.combine (body_code,
((Sexpr.ATOM(MirTypes.COMMENT comment ::
result_code @ [final_branch]),
[],NONE,Sexpr.NIL),
[],[])))
val new_tag = MirTypes.new_tag()
val ((first,blocks,opttag,last),values,procs) = full_body_code
val newblocks =
case opttag of
NONE =>
MirTypes.BLOCK(new_tag,Mir_Utils.contract_sexpr first) ::
blocks
| SOME tag =>
MirTypes.BLOCK(new_tag,Mir_Utils.contract_sexpr first) ::
MirTypes.BLOCK(tag,Mir_Utils.contract_sexpr last) ::
blocks
in
(newblocks,new_tag,values,procs,runtime_env,spills,calls)
end
val (dflt_blocks, dflt_tag, dflt_values, dflt_procs,runtime_env,spills',calls') =
get_blocks_for_dflts(dflt,
static_offset + arg_size + list_size + tag_sizes,
start_at + arg_size + list_size + tag_sizes,
"simple default")
val (num_vcc_tags, num_imm_tags) =
if not is_con then (0, 0)
else
lists_reducel
(fn ((nv, ni), (AugLambda.IMM_TAG _,_)) => (nv, ni + 1)
| ((nv, ni), (AugLambda.VCC_TAG _,_)) => (nv + 1, ni)
| _ => Crash.impossible "bad constructor tag")
((0, 0), tag_le_list)
val tagged_code =
if variable_debug
then
let
val switch_cases = length tag_le_list
in
#2 (Lists.reducer
(fn (((t, {lexp=x, ...}), le_offset),(switch_case,tagged_code)) =>
let
val (reg, code,runtime_env,spills,calls) =
cg_sub(x, env, static_offset + le_offset,start_at + le_offset,tail_position,
(closure,funs_in_closure, fn_tag_list, local_fns),
(slot,non_gc_spills,fp_spills),calls)
val new_reg = MirTypes.GC.new()
in
(switch_case-1,
(t, (reg,
Mir_Utils.combine
(((Sexpr.ATOM [MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG (new_reg),
MirTypes.GP_IMM_INT switch_case),
MirTypes.STOREOP(MirTypes.STREF,MirTypes.GC_REG (new_reg),
MirTypes.GC_REG fp,
MirTypes.GP_IMM_SYMB
(MirTypes.GC_SPILL_SLOT
(MirTypes.DEBUG (ref_slot,
"switch case " ^
Int.toString switch_case))))],
[],NONE, Sexpr.NIL), [], []),code),
(convert_tag t,runtime_env),
spills,calls),
MirTypes.new_tag ()) :: tagged_code)
end)
(Lists.zip (tag_le_list, positions), (switch_cases,nil)))
end
else
map
(fn ((t, {lexp=x, ...}), le_offset) =>
let
val (reg, code,runtime_env,spills,calls) =
cg_sub(x, env, static_offset + le_offset,start_at + le_offset,tail_position,
(closure,funs_in_closure, fn_tag_list, local_fns),
(slot,non_gc_spills,fp_spills),calls)
in
(t, (reg, code, (convert_tag t,runtime_env),
spills,calls),MirTypes.new_tag())
end)
(Lists.zip (tag_le_list, positions))
val spills = map (fn tuple => #4 (#2 tuple)) tagged_code
val calls = map (fn tuple => #5 (#2 tuple)) tagged_code
local
fun max(a, b : int) = if a >= b then a else b
fun order ((gc_spills:int, non_gc_spills:int, fp_spills:int),
(gc_spills':int, non_gc_spills':int, fp_spills':int)) =
gc_spills >= gc_spills' andalso
non_gc_spills >= non_gc_spills' andalso
fp_spills >= fp_spills'
in
val maximum_spills =
lists_reducel
(fn (max_spill as (s1, s2, s3),spill as (t1, t2, t3)) =>
if order (max_spill,spill) then
max_spill
else
if order(spill, max_spill) then
spill
else
(max(s1, t1), max(s2, t2), max(s3, t3)))
((spills', spills))
end
val maximum_calls =
lists_reducel
(fn (max_app,app) => if max_app > app then max_app else app)
(0,(calls'::calls))
val switch_runtime_env = (RuntimeEnv.DEFAULT,runtime_env) :: map (fn tuple => #3 (#2 tuple)) tagged_code
val is_rel = is_simple_relation lexp
val (true_tag, false_tag) =
if is_rel then
case (tagged_code, dflt) of
([(AugLambda.IMM_TAG (_,t1),_, tag)], SOME _) =>
if t1 = 1 then (tag, dflt_tag) else (dflt_tag, tag)
| ([(AugLambda.IMM_TAG (_,t1),_, tag),
(AugLambda.IMM_TAG (_,t2),_, tag')], NONE) =>
if t1 = 1 then (tag, tag') else (tag', tag)
| _ => Crash.impossible "Relational expression with bad IMM_TAGs"
else (dflt_tag, dflt_tag)
fun get_vcc_tag [] = Crash.impossible "Missing VCC tag"
| get_vcc_tag ((AugLambda.VCC_TAG _,_, tag) :: _) = tag
| get_vcc_tag (_ :: rest) = get_vcc_tag rest
fun get_imm_tag [] = Crash.impossible "Missing IMM tag"
| get_imm_tag ((AugLambda.IMM_TAG t,_, tag) :: _) = tag
| get_imm_tag (_ :: rest) = get_imm_tag rest
fun con_tag_to_reg (from_reg, to_reg) =
let
val offset = ~1
in
MirTypes.STOREOP(MirTypes.LD,
MirTypes.GC_REG to_reg,
MirTypes.GC_REG from_reg,
MirTypes.GP_IMM_ANY offset)
end
fun destructuring_code regs =
case regs of
Mir_Utils.ONE(Mir_Utils.INT(gp_op as MirTypes.GP_GC_REG reg)) =>
let
val (num_vccs, num_imms) = case info of
NONE => Crash.impossible "Missing switch info"
| SOME {num_imms, num_vccs, ...} => (num_vccs, num_imms)
val tmp_reg = MirTypes.GC.new()
in
if num_imms = 0 then
(MirTypes.GP_GC_REG tmp_reg,
[MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 0 IMM_TAG",
con_tag_to_reg (reg, tmp_reg),
main_branch],
true)
else if num_vccs = 0 then
(gp_op,
[MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 0 VCC_TAG",
main_branch],
true)
else if num_vccs = 1 then
if num_vcc_tags = 1 orelse dflt_exists then
let
val vcc_tag =
if num_vcc_tags = 1 then
get_vcc_tag tagged_code
else
dflt_tag
in
(gp_op,
MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 1 VCC" ::
MirTypes.TEST (MirTypes.BTA,
vcc_tag,
gp_op,
MirTypes.GP_IMM_ANY tag_test_mask) ::
(if num_imm_tags = 1 andalso num_imms = 1 then
[MirTypes.BRANCH (MirTypes.BRA,
MirTypes.TAG (get_imm_tag tagged_code))]
else if num_imm_tags = 0 then
let
val branch =
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG dflt_tag)]
in
case dflt of
NONE =>
MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG end_reg,
MirTypes.GP_IMM_INT 0) :: branch
| _ => branch
end
else [main_branch]),
num_imms <> 1 andalso num_imm_tags <> 0)
end
else
(gp_op,
[MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 0 VCC_TAG",
main_branch],
true)
else if num_imms = 1 then
if num_imm_tags = 1 orelse dflt_exists then
let
val imm_tag =
if num_imm_tags = 1 then
get_imm_tag tagged_code
else dflt_tag
in
(MirTypes.GP_GC_REG tmp_reg,
MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 1 IMM_TAG" ::
MirTypes.TEST(MirTypes.BNT,
imm_tag,
gp_op,
MirTypes.GP_IMM_ANY tag_test_mask) ::
(if num_vcc_tags = 0 then
[MirTypes.BRANCH(MirTypes.BRA,
MirTypes.TAG dflt_tag)]
else
[con_tag_to_reg (reg, tmp_reg),
main_branch]),
num_vcc_tags <> 0)
end
else
(MirTypes.GP_GC_REG tmp_reg,
[MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, 0 IMM_TAG",
con_tag_to_reg (reg, tmp_reg),
main_branch],
true)
else
if num_vcc_tags = 0 then
(gp_op,
[MirTypes.TEST(MirTypes.BTA,
dflt_tag,
gp_op,
MirTypes.GP_IMM_ANY tag_test_mask),
main_branch],
true)
else if num_imm_tags = 0 then
(MirTypes.GP_GC_REG tmp_reg,
[MirTypes.TEST(MirTypes.BNT,
dflt_tag,
gp_op,
MirTypes.GP_IMM_ANY tag_test_mask),
con_tag_to_reg (reg, tmp_reg),
main_branch],
true)
else
(MirTypes.GP_GC_REG tmp_reg,
[MirTypes.COMMENT "select for ONE/INT/GP_GC_REG, general case",
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG tmp_reg,
gp_op),
MirTypes.TEST(MirTypes.BNT,
main_tag,
MirTypes.GP_GC_REG tmp_reg,
MirTypes.GP_IMM_ANY tag_test_mask),
con_tag_to_reg (reg, tmp_reg),
main_branch],
true)
end
| Mir_Utils.ONE(Mir_Utils.INT(gp_op)) => Crash.impossible "_mir_cg: Mir_Utils.INT\n"
| Mir_Utils.ONE(Mir_Utils.REAL _) => Crash.impossible "SWITCH(Mir_Utils.ONE(Mir_Utils.REAL))"
| Mir_Utils.LIST many =>
let
val tmp_reg = MirTypes.GC.new()
val index = 0
val extra =
(case Lists.nth(index, many) of
Mir_Utils.INT gp_op =>
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG tmp_reg,
gp_op)
| Mir_Utils.REAL fp_op => Crash.impossible "REAL when decoding list")
handle
Lists.Nth =>
Crash.impossible("Trying to select constructor tag " ^
" from list size " ^
Int.toString(length many) ^
"\n")
in
(MirTypes.GP_GC_REG tmp_reg,
MirTypes.COMMENT "select for LIST" ::
[extra, main_branch],
true)
end
val (arg_regs, the_reg_opt, switch_arg_code,need_main_test,runtime_env',spills,calls) =
if is_rel then
let
val (test,arg) =
case lexp of
AugLambda.APP({lexp=AugLambda.BUILTIN (test,_), ...},([{lexp=arg,...}],[]),_) =>
(test,arg)
| AugLambda.APP({lexp=AugLambda.BUILTIN _, ...},args,_) =>
Crash.impossible "Arglist in rel expr"
| _ => Crash.impossible"Bad rel lexp"
val (arg_regs, arg_code,arg_runtime_env,spills,calls) =
cg_sub
(arg, env, static_offset, start_at,false,
(closure, funs_in_closure, fn_tag_list, local_fns),
maximum_spills,maximum_calls)
val (the_reg, extra) = Mir_Utils.send_to_reg arg_regs
val code =
make_if (test, arg_regs,arg_code, true_tag, false_tag)
in
(arg_regs, SOME the_reg, code,
false,arg_runtime_env,spills,calls)
end
else if is_con then
let
val (regs, the_code,runtime_env,spills,calls) =
cg_sub(lexp, env, static_offset, start_at,false,
(closure,funs_in_closure, fn_tag_list,local_fns),
maximum_spills,maximum_calls)
val (the_reg, select_code, need_main_test) = destructuring_code regs
val block =
Mir_Utils.combine(the_code,
((Sexpr.ATOM (select_code),[],NONE,Sexpr.NIL),
[],
[]))
in
(Mir_Utils.ONE(Mir_Utils.INT(the_reg)),
SOME the_reg,
block,
need_main_test,runtime_env,spills,calls)
end
else
let
val (regs, the_code,runtime_env,spills,calls) =
cg_sub(lexp, env, static_offset, start_at,false,
(closure,funs_in_closure, fn_tag_list,local_fns),
maximum_spills,maximum_calls);
val block =
Mir_Utils.combine(the_code,
((Sexpr.ATOM [main_branch],[],NONE,Sexpr.NIL),
[],[]))
in
(regs, NONE, block, true,runtime_env,spills,calls)
end
val calls : int ref = ref calls
val spills : (int * int * int) ref = ref spills
val runtime_env' : RuntimeEnv.RuntimeEnv ref = ref runtime_env'
fun make_cgt(MirTypes.GP_IMM_INT _,_,_,_) =
Crash.impossible"make_cgt GP_IMM_INT"
| make_cgt(MirTypes.GP_IMM_ANY _,_,_,_) =
Crash.impossible"make_cgt GP_IMM_ANY"
| make_cgt(MirTypes.GP_IMM_SYMB _,_,_,_) =
Crash.impossible"make_cgt GP_IMM_SYMB"
| make_cgt(gp_operand, low, high, val_code_tag_list) =
let
val reg_op = Mir_Utils.reg_from_gp gp_operand
val (dflt_tag, end_blocks) =
case dflt of
NONE =>
let val tag1 = MirTypes.new_tag()
in (tag1,
[MirTypes.BLOCK(tag1,
[MirTypes.COMMENT "CGT default",
MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG end_reg,
MirTypes.GP_IMM_ANY 1),
final_branch])])
end
| _ => (dflt_tag, [])
val full_tag_list =
let
fun expand [] = []
| expand ([(_,_, tag)]) = [tag]
| expand ((i,_, tag) :: (rest as ((j,_,_) :: _))) =
tag :: Mir_Utils.list_of (j-i-1, dflt_tag) @ expand rest
in
expand val_code_tag_list
end
val values =
Lists.reducer
(fn ((_, (_, (_, value,_),_,_,_),_), value') =>
value @ value')
(val_code_tag_list, [])
val procs =
Lists.reducer
(fn ((_, (_, (_,_, proc),_,_,_),_), proc') => proc @ proc')
(val_code_tag_list, [])
val body =
map
(fn (_, (regs, ((first, blocks, tag_opt, last),_,_),_,_,_),tag) =>
let
val end_code =
Mir_Utils.send_to_given_reg(regs, end_reg) @
[MirTypes.COMMENT "end CGT", final_branch]
in
(case tag_opt of
NONE =>
[MirTypes.BLOCK(tag,
Mir_Utils.contract_sexpr(Sexpr.CONS(first, Sexpr.ATOM end_code)))]
| SOME tag1 =>
[MirTypes.BLOCK(tag,
Mir_Utils.contract_sexpr first),
MirTypes.BLOCK(tag1,
Mir_Utils.contract_sexpr(Sexpr.CONS(last, Sexpr.ATOM end_code)))])
@ blocks
end)
val_code_tag_list
in
((if is_rel orelse not need_main_test
then Sexpr.NIL
else
Sexpr.ATOM(
if low = 0 then
[MirTypes.SWITCH(MirTypes.CGT,
reg_op,
full_tag_list)]
else
let val new_reg_op = MirTypes.GC.new()
in [MirTypes.TEST(MirTypes.BLT,
dflt_tag,
gp_operand,
MirTypes.GP_IMM_INT low),
MirTypes.BINARY(MirTypes.SUBU,
MirTypes.GC_REG new_reg_op,
gp_operand,
MirTypes.GP_IMM_INT low),
MirTypes.SWITCH(MirTypes.CGT,
MirTypes.GC_REG new_reg_op,
full_tag_list),
MirTypes.COMMENT "Switch relative to lowest tag"]
end
),
Lists.reducer
op@
(end_blocks :: body, []),
NONE,
Sexpr.NIL
),
values,
procs
)
end
fun bounds(low:int, high, []) = (low, high)
| bounds(low, high, i :: xs) =
if (i < low) then bounds(i, high, xs)
else if (i > high) then bounds(low, i, xs)
else bounds(low, high, xs)
fun do_chained_tests(_, default, [], test_opt, clean_code) =
(((case (default, test_opt) of
(SOME _, SOME _) =>
Sexpr.ATOM
(clean_code @
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG dflt_tag),
MirTypes.COMMENT "default or end"])
| (SOME _, NONE) =>
Sexpr.NIL
| (_, SOME _) =>
Sexpr.NIL
| _ => Sexpr.NIL ),
[],
NONE,
Sexpr.NIL),
[],[])
| do_chained_tests
(the_reg, default, head :: rest, test_opt, clean_code) =
let
val (scon, le, (regs, code_block, _, _, _), tag) = head
val ((first, blocks, tag_opt, last), values, procs) =
code_block
val {test_code, test_clean} =
case test_opt of
SOME f => f (the_reg, scon, le, tag)
| NONE => {test_code = Sexpr.NIL, test_clean = []}
val the_test = case rest of
[] =>
(case default of
NONE =>
(case test_opt of
SOME _ =>
(case test_code of
Sexpr.ATOM[MirTypes.TEST(MirTypes.BEQ, tag,_,_)] =>
Sexpr.ATOM
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG tag)]
| _ => Crash.impossible"Bad constructor test")
| _ => test_code)
| _ => test_code)
| _ => test_code
val end_code =
Mir_Utils.send_to_given_reg(regs, end_reg) @
[MirTypes.COMMENT "end chained test", final_branch]
val result =
case tag_opt of
NONE =>
(Sexpr.CONS (the_test, Sexpr.ATOM test_clean),
MirTypes.BLOCK
(tag,
Mir_Utils.contract_sexpr
(Sexpr.CONS
(Sexpr.ATOM (test_clean @ clean_code),
Sexpr.CONS
(first, Sexpr.ATOM end_code))))
:: blocks,
NONE,
Sexpr.NIL)
| SOME tag'=>
(Sexpr.CONS (the_test, Sexpr.ATOM test_clean),
MirTypes.BLOCK
(tag,
Mir_Utils.contract_sexpr
(Sexpr.CONS
(Sexpr.ATOM (test_clean @ clean_code),
first)))
:: blocks
@ [MirTypes.BLOCK
(tag',
Mir_Utils.contract_sexpr
(Sexpr.CONS(last, Sexpr.ATOM end_code)))],
NONE,
Sexpr.NIL)
in Mir_Utils.combine(
(result, values, procs),
do_chained_tests(the_reg, default, rest, test_opt, clean_code))
end
fun constructor_code () =
let
val val_code_tags_list =
Lists.qsort (fn ((i:int,_,_), (i',_,_)) => i < i')
(map
(fn (AugLambda.IMM_TAG (_,i), le, tag) => (i, le, tag)
| (AugLambda.VCC_TAG (_,i), le, tag) => (i, le, tag)
| _ => Crash.impossible "Mixed tag type in switch")
tagged_code)
val (i, (result_reg, code,_,_,_), first_tag) =
case val_code_tags_list of
x :: _ => x
| _ => Crash.impossible "Empty datatype list"
val the_reg =
case the_reg_opt of
SOME x => x
| NONE => Crash.impossible "Missing the_reg"
in
if length val_code_tags_list < 3
then
let
fun do_test(reg, i,_, tag) =
{test_code =
Sexpr.ATOM [MirTypes.TEST(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_INT i)],
test_clean = []}
val val_le_tags_list =
map
(fn (i, code, tag) => (i, 0, code, tag))
val_code_tags_list
in ([],
do_chained_tests
(the_reg, dflt, val_le_tags_list,
if need_main_test then
SOME do_test
else
NONE,
[]))
end
else
let
val (low, high) = bounds (i, i, map #1 val_code_tags_list)
val dflt_code =
case dflt of
NONE => []
| SOME _ =>
[MirTypes.TEST(MirTypes.BGT, dflt_tag, the_reg,
MirTypes.GP_IMM_INT high)]
val main =
make_cgt(the_reg, low, high, val_code_tags_list)
in
(dflt_code, main)
end
end
fun empty_tel_code () =
let
val dflt_code = case dflt of
NONE => []
| SOME _ =>
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG dflt_tag)]
in
(MirTypes.COMMENT "nil main def" :: dflt_code, no_code)
end
fun find_large_value to_bignum (v1, v2) =
let
val v = to_bignum v1
in
v2
end
handle
BigNum.Unrepresentable => v1
| BigNum32.Unrepresentable => v1
fun scon_code (scon, max_size_opt) =
(case scon of
Ident.INT _ =>
let
val is_32_bits =
case max_size_opt of
NONE => false
| SOME sz =>
if sz <= MachSpec.bits_per_word then
false
else if sz = 32 then
true
else
Crash.impossible
("Unknown integer size in pattern " ^ Int.toString sz)
val (the_reg, extra, clean_code) =
if is_32_bits then
case arg_regs of
Mir_Utils.ONE reg => Mir_Utils.get_word32 reg
| _ =>
Crash.impossible "LIST found in switch on Integer32"
else
let
val (reg, code) = Mir_Utils.send_to_reg arg_regs
in
(reg, code, [])
end
in
let
fun do_conversion (AugLambda.SCON_TAG (Ident.INT (i,location), max_size_opt),
code, tag) =
(Mir_Utils.convert_int (i, max_size_opt), 0, code, tag)
| do_conversion _ = Crash.impossible "Mixed tag type in switch"
val val_le_tags_list =
Lists.qsort
(fn ((i:int,_,_,_), (i',_,_,_)) => i < i')
(map do_conversion tagged_code)
val a_value = case val_le_tags_list of
{1=i, ...} :: _ => i
| _ => Crash.impossible"empty val_le_tags_list"
val (low, high) =
bounds (a_value, a_value, map #1 val_le_tags_list)
val len = length val_le_tags_list
val use_cgt =
not (is_32_bits) andalso
high + 1 - low <= 2 * len andalso len > 2
val dflt_code = case dflt of
NONE =>
[MirTypes.COMMENT
"No default (strange for scon match)"]
| SOME _ =>
if is_32_bits then
[MirTypes.COMMENT "Default",
MirTypes.TEST
(MirTypes.BGT, dflt_tag, the_reg,
MirTypes.GP_IMM_ANY high)]
else
[MirTypes.COMMENT "Default",
MirTypes.TEST
(MirTypes.BGT, dflt_tag, the_reg,
MirTypes.GP_IMM_INT high)]
in
if use_cgt then
(extra @ dflt_code,
make_cgt
(the_reg, low, high,
map
(fn (x,_, y, z) => (x, y, z))
val_le_tags_list))
else
let
fun do_test(reg, i, _, tag) =
if is_32_bits then
{test_code =
Sexpr.ATOM
[MirTypes.TEST
(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_ANY i)],
test_clean = []}
else
{test_code =
Sexpr.ATOM
[MirTypes.TEST
(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_INT i)],
test_clean = []}
in
(extra,
do_chained_tests
(the_reg, dflt, val_le_tags_list,
SOME do_test, clean_code))
end
end
handle
Mir_Utils.ConvertInt =>
let
fun to_string(Ident.INT(i,_)) = i
| to_string _ =
Crash.impossible"Mixed tag type in switch"
fun location_scon(Ident.INT(_, location)) =
location
| location_scon _ =
Crash.impossible"Mixed tag type in switch"
fun to_bignum x =
let
val str_x = to_string x
in
if size str_x < 2 then
BigNum.string_to_bignum str_x
else
case substring (str_x, 0, 2)
of "0x" =>
BigNum.hex_string_to_bignum str_x
| _ =>
BigNum.string_to_bignum str_x
end
fun to_bignum32 x =
let
val str_x = to_string x
in
if size str_x < 2 then
BigNum32.string_to_bignum str_x
else
case substring (str_x, 0, 2)
of "0x" =>
BigNum32.hex_string_to_bignum str_x
| _ =>
BigNum32.string_to_bignum str_x
end
fun compare_bignums ((i,_,_,_), (i',_,_,_)) =
BigNum.<(to_bignum i, to_bignum i')
handle BigNum.Unrepresentable =>
let
val i = find_large_value to_bignum (i, i')
in
Info.error'
error_info
(Info.FATAL, location_scon i,
"Integer too big: " ^ to_string i)
end
fun compare_bignums32 ((i,_,_,_), (i',_,_,_)) =
BigNum32.<(to_bignum32 i, to_bignum32 i')
handle BigNum32.Unrepresentable =>
let
val i = find_large_value to_bignum32 (i, i')
in
Info.error'
error_info
(Info.FATAL, location_scon i,
"Integer too big: " ^ to_string i)
end
fun check_tag
(AugLambda.SCON_TAG (scon, _), code, tag) =
(scon, 0, code, tag)
| check_tag _ =
Crash.impossible"Mixed tag type in switch"
val val_le_tags_list =
Lists.qsort
(if is_32_bits then
compare_bignums32
else
compare_bignums)
(map check_tag tagged_code)
val low =
case val_le_tags_list of
(scon,_,_,_) :: _ => scon
| _ => Crash.impossible "Empty switch list"
val high =
case (last val_le_tags_list) of
(scon,_,_,_) => scon
fun do_test(reg, long_i,_, tag) =
let
val (reg', the_code) =
(case cg_sub(Mir_Utils.convert_long_int (long_i, max_size_opt), env,
static_offset, start_at,false,
(closure,funs_in_closure, fn_tag_list,local_fns),
!spills,!calls) of
(Mir_Utils.ONE(Mir_Utils.INT(r as MirTypes.GP_GC_REG _)),
((code, [], NONE, Sexpr.NIL),
[], []),_,_,_) => (r, code)
| _ => Crash.impossible"Bad code for big integer")
handle
Mir_Utils.Unrepresentable =>
Info.error'
error_info
(Info.FATAL, location_scon long_i,
"Integer too big: " ^ to_string long_i)
val (arg_reg, arg_code, arg_clean) =
Mir_Utils.get_word32 (Mir_Utils.INT reg')
val test =
MirTypes.TEST
(MirTypes.BEQ, tag, reg, arg_reg)
in
{test_code =
Sexpr.CONS
(the_code,
Sexpr.ATOM (arg_code @ [test ])),
test_clean = arg_clean}
end
in
(extra,
do_chained_tests
(the_reg, dflt, val_le_tags_list,
SOME do_test, clean_code))
end
end
| Ident.WORD _ =>
let
val is_32_bits =
case max_size_opt
of NONE => false
| SOME sz =>
if sz <= MachSpec.bits_per_word then
false
else if sz = 32 then
true
else
Crash.impossible
("Unknown word size in pattern "
^ Int.toString sz)
val (the_reg, extra, clean_code) =
if is_32_bits then
case arg_regs
of Mir_Utils.ONE reg =>
Mir_Utils.get_word32 reg
| _ =>
Crash.impossible
"LIST found in switch on Word32"
else
let
val (reg, code) = Mir_Utils.send_to_reg arg_regs
in
(reg, code, [])
end
in
let
fun do_conversion
(AugLambda.SCON_TAG
(Ident.WORD (i,location), max_size_opt),
code, tag) =
(Mir_Utils.convert_word (i, max_size_opt),
0, code, tag)
| do_conversion _ =
Crash.impossible "Mixed tag type in switch"
val val_le_tags_list =
Lists.qsort
(fn ((i:int,_,_,_), (i',_,_,_)) => i < i')
(map do_conversion tagged_code)
val a_value = case val_le_tags_list of
{1=i, ...} :: _ => i
| _ => Crash.impossible"empty val_le_tags_list"
val (low, high) =
bounds (a_value, a_value, map #1 val_le_tags_list)
val len = length val_le_tags_list
val use_cgt =
not (is_32_bits) andalso
high + 1 - low <= 2 * len andalso len > 2
val dflt_code = case dflt of
NONE =>
[MirTypes.COMMENT
"No default (strange for scon match)"]
| SOME _ =>
if is_32_bits then
[MirTypes.COMMENT "Default",
MirTypes.TEST
(MirTypes.BHI, dflt_tag, the_reg,
MirTypes.GP_IMM_ANY high)]
else
[MirTypes.COMMENT "Default",
MirTypes.TEST
(MirTypes.BHI, dflt_tag, the_reg,
MirTypes.GP_IMM_INT high)]
in
if use_cgt then
(extra @ dflt_code,
make_cgt
(the_reg, low, high,
map
(fn (x,_, y, z) => (x, y, z))
val_le_tags_list))
else
let
fun do_test(reg, i, _, tag) =
if is_32_bits then
{test_code =
Sexpr.ATOM
[MirTypes.TEST
(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_ANY i)],
test_clean = []}
else
{test_code =
Sexpr.ATOM
[MirTypes.TEST
(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_INT i)],
test_clean = []}
in
(extra,
do_chained_tests
(the_reg, dflt, val_le_tags_list,
SOME do_test, clean_code))
end
end
handle
Mir_Utils.ConvertInt =>
let
fun to_string(Ident.WORD(i,_)) = i
| to_string _ =
Crash.impossible"Mixed tag type in switch"
fun location_scon(Ident.WORD(_, location)) =
location
| location_scon _ =
Crash.impossible"Mixed tag type in switch"
fun to_bignum x =
let
val str_x = to_string x
in
if size str_x < 3 then
BigNum.word_string_to_bignum str_x
else
case substring (str_x, 0, 3)
of "0wx" =>
BigNum.hex_word_string_to_bignum str_x
| _ =>
BigNum.word_string_to_bignum str_x
end
fun to_bignum32 x =
let
val str_x = to_string x
in
if size str_x < 3 then
BigNum32.word_string_to_bignum str_x
else
case substring (str_x, 0, 3)
of "0wx" =>
BigNum32.hex_word_string_to_bignum str_x
| _ =>
BigNum32.word_string_to_bignum str_x
end
fun compare_bignums ((i,_,_,_), (i',_,_,_)) =
BigNum.<(to_bignum i, to_bignum i')
handle BigNum.Unrepresentable =>
let
val i = find_large_value to_bignum (i, i')
in
Info.error'
error_info
(Info.FATAL, location_scon i,
"Word too big: " ^ to_string i)
end
fun compare_bignums32 ((i,_,_,_), (i',_,_,_)) =
BigNum32.<(to_bignum32 i, to_bignum32 i')
handle BigNum32.Unrepresentable =>
let
val i = find_large_value to_bignum32 (i, i')
in
Info.error'
error_info
(Info.FATAL, location_scon i,
"Word too big: " ^ to_string i)
end
fun check_tag
(AugLambda.SCON_TAG (scon, _), code, tag) =
(scon, 0, code, tag)
| check_tag _ =
Crash.impossible"Mixed tag type in switch"
val val_le_tags_list =
Lists.qsort
(if is_32_bits then
compare_bignums32
else
compare_bignums)
(map check_tag tagged_code)
val low =
case val_le_tags_list of
(scon,_,_,_) :: _ => scon
| _ => Crash.impossible "Empty switch list"
val high =
case (last val_le_tags_list) of
(scon,_,_,_) => scon
fun do_test(reg, long_i,_, tag) =
let
val (reg', the_code) =
(case cg_sub(Mir_Utils.convert_long_word (long_i, max_size_opt), env,
static_offset, start_at,false,
(closure,funs_in_closure, fn_tag_list,local_fns),
!spills,!calls) of
(Mir_Utils.ONE(Mir_Utils.INT(r as MirTypes.GP_GC_REG _)),
((code, [], NONE, Sexpr.NIL),
[], []),_,_,_) => (r, code)
| _ => Crash.impossible"Bad code for big word")
handle
Mir_Utils.Unrepresentable =>
Info.error'
error_info
(Info.FATAL, location_scon long_i,
"Word too big: " ^ to_string long_i)
val (arg_reg, arg_code, arg_clean) =
Mir_Utils.get_word32 (Mir_Utils.INT reg')
val test =
MirTypes.TEST
(MirTypes.BEQ, tag, reg, arg_reg)
in
{test_code =
Sexpr.CONS
(the_code,
Sexpr.ATOM (arg_code @ [test ])),
test_clean = arg_clean}
end
in
(extra,
do_chained_tests
(the_reg, dflt, val_le_tags_list,
SOME do_test, clean_code))
end
end
| Ident.CHAR _ =>
let
val (the_reg, extra) = Mir_Utils.send_to_reg arg_regs
in
let
val val_le_tags_list =
Lists.qsort (fn ((i:int,_,_,_), (i',_,_,_)) =>
i < i')
(map (fn (AugLambda.SCON_TAG(Ident.CHAR s, _), code, tag) =>
(ord(String.sub (s, 0)), 0, code, tag)
| _ => Crash.impossible"Mixed tag type in switch")
tagged_code)
val a_value = case val_le_tags_list of
{1=i, ...} :: _ => i
| _ => Crash.impossible"empty val_le_tags_list"
val (low, high) = bounds(a_value, a_value, map #1 val_le_tags_list)
val len = length val_le_tags_list
val use_cgt = high + 1 - low <= 2 * len andalso len > 2
val dflt_code = case dflt of
NONE => [MirTypes.COMMENT"No default (strange for scon match)"]
| SOME _ =>
[MirTypes.COMMENT "Default",
MirTypes.TEST(MirTypes.BHI, dflt_tag, the_reg,
MirTypes.GP_IMM_INT high)]
in
if use_cgt then
(extra @ dflt_code,
make_cgt(the_reg, low, high,
map
(fn (x,_, y, z) => (x, y, z))
val_le_tags_list))
else
let
fun do_test(reg, i,_, tag) =
{test_code =
Sexpr.ATOM
[MirTypes.TEST(MirTypes.BEQ, tag, reg,
MirTypes.GP_IMM_INT i)],
test_clean = []}
in
(extra,
do_chained_tests(the_reg,
dflt,
val_le_tags_list,
SOME do_test,
[]))
end
end
end
| Ident.REAL _ =>
let
val val_le_tags_list =
map (fn ((AugLambda.SCON_TAG(Ident.REAL _, _), code, tag), p) =>
(p, 0, code, tag)
| _ => Crash.impossible"Mixed tag type in switch")
(Lists.zip(tagged_code, tag_positions))
val (the_reg, extra) =
case arg_regs of
Mir_Utils.ONE reg => Mir_Utils.get_real reg
| _ => Crash.impossible "struct gives single REAL"
fun do_test(reg, i,_, tag) =
let
val fp_op = MirTypes.FP_REG(MirTypes.FP.new())
in
{test_code =
Sexpr.ATOM
[MirTypes.STOREOP
(MirTypes.LD,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY
(4 * (static_offset + i + (funs_in_closure * 2 - 1)) - 1)),
MirTypes.STOREFPOP
(MirTypes.FLD, fp_op,
MirTypes.GC_REG MirRegisters.global,
MirTypes.GP_IMM_ANY real_offset),
MirTypes.FTEST(MirTypes.FBEQ, tag, fp_op, reg)],
test_clean = []}
end
val tags_code =
map
(fn (p, (t,_)) =>
case t of
AugLambda.SCON_TAG(scon as Ident.REAL _, _) =>
MirTypes.VALUE(top_closure (start_at + p + 1),
MirTypes.SCON scon)
| _ => Crash.impossible"non-REAL in REAL switch"
)
(Lists.zip(tag_positions, tag_le_list))
in
(extra,
Mir_Utils.combine(((Sexpr.NIL, [], NONE, Sexpr.NIL), tags_code, []),
do_chained_tests(the_reg, dflt, val_le_tags_list, SOME do_test, [])))
end
| Ident.STRING _ =>
let
val val_le_tags_list =
map
(fn ((AugLambda.SCON_TAG(Ident.STRING _, _), code, tag), p) =>
(p, 0, code, tag)
| _ => Crash.impossible"Mixed tag type in switch")
(Lists.zip(tagged_code, tag_positions))
val the_reg =
case arg_regs of
Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_GC_REG _)) => arg
| _ => Crash.impossible"struct gives single STRING"
fun do_test(reg, i,_, tag) =
let
val scon_reg = MirTypes.GC_REG(MirTypes.GC.new())
val (regs', the_code',runtime_env'',spills',calls') =
cg_sub(AugLambda.VAR(prim_to_lambda Pervasives.STRINGEQ),
env, static_offset, start_at,false,
(closure,funs_in_closure, [], []),!spills,!calls)
val _ = calls := calls'
val _ = spills := spills'
val _ =
runtime_env' := append_runtime_envs(runtime_env',runtime_env'')
val app_code =
case Mir_Utils.do_app(Debugger_Types.null_backend_annotation,
regs', the_code',
Mir_Utils.ONE (Mir_Utils.INT(MirTypes.GP_GC_REG
caller_arg)),
no_code)
of
(_, ((app_code, [], NONE, last), [], [])) =>
(case Mir_Utils.contract_sexpr last of
[] => app_code
| _ => Crash.impossible"Bad result for STRINGEQ")
| _ => Crash.impossible"Bad result for STRINGEQ"
in
{test_code =
Sexpr.CONS
(Sexpr.ATOM
[MirTypes.STOREOP
(MirTypes.LD, scon_reg,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY
(4 * (static_offset + i + (2 * funs_in_closure - 1)) - 1)),
MirTypes.ALLOCATE_STACK
(MirTypes.ALLOC,
MirTypes.GC_REG caller_arg,
2, NONE),
MirTypes.STOREOP
(MirTypes.ST,
Mir_Utils.reg_from_gp the_reg,
MirTypes.GC_REG caller_arg,
MirTypes.GP_IMM_ANY ~1),
MirTypes.STOREOP
(MirTypes.ST, scon_reg,
MirTypes.GC_REG caller_arg,
MirTypes.GP_IMM_ANY 3),
MirTypes.COMMENT "Call external STRINGEQ"],
Sexpr.CONS
(app_code,
Sexpr.ATOM
[MirTypes.DEALLOCATE_STACK
(MirTypes.ALLOC, 2),
MirTypes.TEST
(MirTypes.BEQ, tag,
MirTypes.GP_GC_REG caller_arg,
MirTypes.GP_IMM_INT 1)])),
test_clean = []}
end
val tags_code =
map
(fn (p, (t,_)) =>
case t of
AugLambda.SCON_TAG (scon as Ident.STRING _, _) =>
MirTypes.VALUE(
top_closure(start_at + p + 1),
MirTypes.SCON scon)
| _ =>
Crash.impossible"non-STRING in STRING switch")
(Lists.zip(tag_positions, tag_le_list))
in
([],
Mir_Utils.combine(((Sexpr.NIL, [], NONE, Sexpr.NIL), tags_code, []),
do_chained_tests(the_reg, dflt, val_le_tags_list, SOME do_test, [])))
end )
fun exp_code () =
let
val val_le_tags_list =
map
(fn (AugLambda.EXP_TAG{lexp=le, ...}, code, tag) =>
(0, le, code, tag)
| _ => Crash.impossible"Mixed tag type in switch")
tagged_code
val the_reg =
case arg_regs of
Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_GC_REG _)) => arg
| _ => Crash.impossible"Bad reg for exception"
fun do_test(reg,_, le, tag) =
let
val (regs, the_code) =
case cg_sub(le, env, static_offset, start_at,false,
(closure,funs_in_closure, [],[]),!spills,!calls) of
(Mir_Utils.ONE(Mir_Utils.INT(reg as MirTypes.GP_GC_REG _)),
((code, [], NONE, last), [], []),
runtime_env'',spills',calls') =>
(calls := calls';
spills := spills';
runtime_env' := append_runtime_envs(runtime_env',runtime_env'');
(case Mir_Utils.contract_sexpr last of
[] => (reg, code)
| _ => Crash.impossible"Bad result from cg(exception)"))
| _ =>
Crash.impossible"Bad result from cg(exception)"
in
{test_code =
Sexpr.CONS
(the_code,
Sexpr.ATOM
[MirTypes.TEST(MirTypes.BEQ, tag, reg, regs)]),
test_clean = []}
end
in
([], do_chained_tests(the_reg, dflt, val_le_tags_list, SOME do_test, []))
end
val (main_default, main_code) =
case tag_le_list of
[] => empty_tel_code ()
| (AugLambda.IMM_TAG _,_) :: _ => constructor_code ()
| (AugLambda.VCC_TAG _,_) :: _ => constructor_code ()
| (AugLambda.SCON_TAG scon,_) :: rest => scon_code scon
| (AugLambda.EXP_TAG _,_) :: rest => exp_code ()
val result_code =
Mir_Utils.combine
(Mir_Utils.combine
(switch_arg_code,
Mir_Utils.combine
(((Sexpr.NIL, [], SOME main_tag,
Sexpr.ATOM main_default),
[],
[]),
main_code
)
),
((Sexpr.NIL, dflt_blocks, SOME end_tag,
Sexpr.ATOM[MirTypes.COMMENT"End of switch"]),
dflt_values,
dflt_procs)
)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG end_reg)), result_code,
if variable_debug
then RuntimeEnv.SWITCH(!runtime_env',ref_slot,
maximum_calls,switch_runtime_env)
else RuntimeEnv.EMPTY,
!spills,!calls)
end
end
| cg_sub(arg as AugLambda.VAR lvar,env,_,_,_,(closure,funs_in_closure,_,_),spills,calls) =
let
val (reg, code) = Mir_Utils.cg_lvar_fn(lvar, env, closure, funs_in_closure)
in
(Mir_Utils.ONE reg, ((Sexpr.ATOM code, [], NONE,
Sexpr.NIL), [], []),RuntimeEnv.EMPTY,spills,calls)
end
| cg_sub(arg as AugLambda.INT i,_,_,_,_,_,spills,calls) =
let
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_IMM_INT i)), no_code,
RuntimeEnv.EMPTY,spills,calls)
end
| cg_sub(arg as AugLambda.SCON (scon, size), e,static_offset, start_at,tail_position,
(closure,funs_in_closure, fn_tag_list, local_fns),spills,calls) =
let
in
(case scon of
Ident.INT(i,location) =>
((let
val w = Mir_Utils.convert_int (i, size)
in
case size
of NONE =>
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_IMM_INT w)),
no_code,RuntimeEnv.EMPTY,spills,calls)
| SOME sz =>
if sz <= MachSpec.bits_per_word then
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_IMM_INT w)),
no_code,RuntimeEnv.EMPTY,spills,calls)
else if sz = 32 then
convert32 (w, sz, spills, calls)
else
Crash.impossible
("unknown int size " ^ Int.toString sz)
end
handle Mir_Utils.ConvertInt =>
cg_sub(Mir_Utils.convert_long_int (scon, size), e,
static_offset,start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),
spills,calls))
handle
Mir_Utils.Unrepresentable =>
Info.error'
error_info
(Info.FATAL, location, "Integer too big: " ^ i))
| Ident.WORD(i, location) =>
((let
val w = Mir_Utils.convert_word (i, size)
in
case size
of NONE =>
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_IMM_INT w)),
no_code,RuntimeEnv.EMPTY,spills,calls)
| SOME sz =>
if sz <= MachSpec.bits_per_word then
(Mir_Utils.ONE (Mir_Utils.INT (MirTypes.GP_IMM_INT w)),
no_code,RuntimeEnv.EMPTY,spills,calls)
else if sz = 32 then
convert32 (w, sz, spills, calls)
else
Crash.impossible
("unknown word size " ^ Int.toString sz)
end
handle Mir_Utils.ConvertInt =>
cg_sub(Mir_Utils.convert_long_word (scon, size), e,
static_offset,start_at,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),
spills,calls))
handle
Mir_Utils.Unrepresentable =>
Info.error'
error_info
(Info.FATAL, location, "Word too big: " ^ i))
| Ident.CHAR s =>
(Mir_Utils.ONE
(Mir_Utils.INT
(MirTypes.GP_IMM_INT
(ord (String.sub(s, 0))))),
no_code,RuntimeEnv.EMPTY,spills,calls)
| _ =>
let
val new_reg = MirTypes.GC.new()
val new_tag =
top_closure(start_at + 1)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG new_reg)),
((Sexpr.ATOM[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY(4*(static_offset +
(funs_in_closure * 2 - 1)) - 1))],
[],
NONE, Sexpr.NIL),
[MirTypes.VALUE(new_tag, MirTypes.SCON scon)],
[]),RuntimeEnv.EMPTY,spills,calls)
end)
end
| cg_sub(arg as AugLambda.MLVALUE mlvalue,_,static_offset, start_at,_,
(closure,funs_in_closure,_,_),spills,calls) =
let
val new_reg = MirTypes.GC.new()
val new_tag = top_closure(start_at + 1)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG new_reg)),
((Sexpr.ATOM[MirTypes.STOREOP(MirTypes.LD, MirTypes.GC_REG new_reg,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY(4*(static_offset +
(funs_in_closure * 2 - 1)) - 1))],
[],
NONE, Sexpr.NIL),
[MirTypes.VALUE(new_tag, MirTypes.MLVALUE mlvalue)],
[]),RuntimeEnv.EMPTY,spills,calls)
end
| cg_sub(fcn as AugLambda.FN((lvl,fp_vars),
{lexp=lexp, size=gc_objects_within},
name_string,instances),
env, static_offset,start_at,_,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
val _ =
if do_diagnostics
then
if LambdaTypes.isLocalFn instances then print "local function found\n"
else if name_string = "letbody fun" then print "letbody found\n"
else if name_string = "switch body<Match>" then print "switch body found\n"
else ()
else ()
val restore_spills = spill_restorer ()
val _ = initialize_spills()
val free = find_frees (env,closure,fcn,prim_to_lambda)
val tag = top_closure (start_at + gc_objects_within + 1)
val gc_reg = MirTypes.GC_REG(MirTypes.GC.new())
val (cl_reg, code, new_closure) =
case Mir_Utils.make_closure([tag], free, gc_objects_within,
static_offset + (funs_in_closure * 2 - 1), env,
closure, funs_in_closure) of
(reg, code, [new_closure]) =>
(reg,
Sexpr.CONS(code,
Sexpr.ATOM[MirTypes.COMMENT"Copy in function pointer",
MirTypes.STOREOP(MirTypes.LD, gc_reg,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY(4*((funs_in_closure*2-1) + static_offset +
gc_objects_within) - 1)),
MirTypes.STOREOP(MirTypes.ST, gc_reg, reg,
MirTypes.GP_IMM_ANY ~1)]),
new_closure)
| _ => Crash.impossible"Single FN with multiple tags"
val arg_regs = assign_callee_regs lvl
val fp_arg_regs = assign_fp_regs fp_vars
val internal_regs = map (fn lvar => (lvar,MirTypes.GC.new())) lvl
val fp_internal_regs = map (fn lvar => (lvar,MirTypes.FP.new())) fp_vars
val comment_string =
if name_string = ""
then []
else [MirTypes.COMMENT name_string]
val entry_code =
(Sexpr.ATOM
(comment_string @
[MirTypes.ENTER (map MirTypes.GC arg_regs @ map MirTypes.FLOAT fp_arg_regs)] @
(if intercept then [MirTypes.INTERCEPT] else []) @
make_get_args_code (arg_regs,map #2 internal_regs) @
make_fp_get_args_code (fp_arg_regs,map #2 fp_internal_regs)),
[], NONE, Sexpr.NIL)
val lambda_env =
lists_reducel
(fn (env,(lvar,copy)) =>
Mir_Env.add_lambda_env((lvar, MirTypes.GC copy),env))
(Mir_Env.empty_lambda_env,internal_regs)
val lambda_env =
lists_reducel
(fn (env,(lvar,copy)) =>
Mir_Env.add_lambda_env((lvar, MirTypes.FLOAT copy),env))
(lambda_env,fp_internal_regs)
val (fn_reg, fn_code,runtime_env,(gc_spills,non_gc_spills,fp_spills),_) =
cg_letrec_sub(lexp,lambda_env, 0, start_at, true,
(new_closure,1, [tag] , []),
(1,0,0),0)
val exit_code =
let
val result_temporary = MirTypes.GC.new ()
in
(Sexpr.ATOM (Mir_Utils.send_to_given_reg(fn_reg, result_temporary) @
[MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG callee_arg,
MirTypes.GP_GC_REG result_temporary),
MirTypes.RTS]),
[], NONE, Sexpr.NIL)
end
val ((first, blocks, tag_opt, last), values, procs) =
Mir_Utils.combine((entry_code, [], []), Mir_Utils.combine (make_call_code (0,fn_code), (exit_code, [], [])))
val spill_sizes =
if variable_debug
then SOME{gc = gc_spills+1,
non_gc = non_gc_spills+1,
fp = fp_spills+1}
else NONE
val runtime_env =
if variable_debug
then RuntimeEnv.FN (name_string,runtime_env,head_spill (get_current_spills ()),instances)
else RuntimeEnv.EMPTY
val the_fn =
[MirTypes.PROC(name_string,
tag,
MirTypes.PROC_PARAMS {spill_sizes = spill_sizes,
old_spill_sizes = NONE,
stack_allocated = NONE},
MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr first) ::
blocks @
(case tag_opt of
NONE => []
| SOME tag =>
[MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr last)]),
runtime_env)]
val _ = restore_spills()
in
(Mir_Utils.ONE (Mir_Utils.INT (Mir_Utils.gp_from_reg cl_reg)),
((code, [], NONE, Sexpr.NIL),
values,
the_fn :: procs),
RuntimeEnv.EMPTY,spills,calls)
end
| cg_sub(exp as AugLambda.LETREC(lv_list, le_list, {lexp=lexp, ...}),
env, static_offset, start_at,tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
val runtime_lets =
Lists.reducer
(fn ((lvar, SOME (ref (RuntimeEnv.VARINFO (name,ty,_)))),acc) =>
(RuntimeEnv.VARINFO (name,ty,NONE),RuntimeEnv.EMPTY) :: acc
| ((lvar,_),acc) => acc)
(lv_list,[])
val lv_list = map #1 lv_list
val funs = length lv_list
val fn_args_and_bodies =
map
(fn {lexp=AugLambda.FN((lvl,fp_args), le,_,info), ...} => ((lvl,fp_args), le)
| _ => Crash.impossible"non-FN in LETREC")
le_list
val lambda_names =
map
(fn {lexp=AugLambda.FN(_,_,name,instances), ...} => (name,instances)
| _ => Crash.impossible"non-FN in LETREC")
le_list
val _ =
if do_diagnostics
then
Lists.iterate
(fn ((name,instances),lv) =>
if LambdaTypes.isLocalFn instances then print ("local function found: " ^ N lv ^ " \n")
else if name = "letbody fun" then print ("letbody found: " ^ N lv ^ " \n")
else if name = "switch body<Match>" then print ("switch body found: " ^ N lv ^ "\n")
else ())
(zip2 (lambda_names,lv_list))
else ()
val (fn_args, fn_bodies) = Lists.unzip fn_args_and_bodies
val free = find_frees (env,closure,AugLambda.STRUCT fn_bodies,prim_to_lambda)
val gc_objects_within =
lists_reducel (fn (x, {size=size, lexp=_}) => x+size) (0, fn_bodies)
val positions = do_pos3 (0, fn_bodies)
val offsets =
map #2 (#1 (Lists.number_from (Mir_Utils.list_of (funs, 0), 0, 2, ident_fn)))
val tags =
map (fn x => top_closure(start_at + gc_objects_within + (x div 2) + 1))
offsets
val gc_reg = MirTypes.GC_REG(MirTypes.GC.new())
val (cl_reg, code, new_closure_list) =
Mir_Utils.make_closure(tags, free, gc_objects_within,
static_offset + (2 * funs_in_closure - 1),
env, closure,funs_in_closure)
val new_reg_list = map (fn x => (x, MirTypes.GC.new())) offsets
val new_closure_code =
map
(fn (0, reg) => MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG reg,
Mir_Utils.gp_from_reg cl_reg)
| (x, reg) => MirTypes.BINARY(MirTypes.ADDU, MirTypes.GC_REG reg,
Mir_Utils.gp_from_reg cl_reg,
MirTypes.GP_IMM_INT x))
new_reg_list
val code = Lists.reducer
(fn (x, y) => Sexpr.CONS(x, y))
(code :: Sexpr.ATOM new_closure_code ::
Sexpr.ATOM[MirTypes.COMMENT "Copy in function pointers"] :: (map
(fn x =>
Sexpr.ATOM[MirTypes.STOREOP(MirTypes.LD, gc_reg,
MirTypes.GC_REG callee_closure,
MirTypes.GP_IMM_ANY(4*((funs_in_closure*2-1) + static_offset +
gc_objects_within + (x div 2)) - 1)),
MirTypes.STOREOP(MirTypes.ST, gc_reg, cl_reg,
MirTypes.GP_IMM_ANY(4*x-1))])
offsets), Sexpr.NIL)
val code = case offsets of
[] => Crash.impossible"Empty recursive set"
| [_] => code
| _ =>
let
val gc_reg = MirTypes.GC_REG(MirTypes.GC.new())
in
Sexpr.CONS
(code,
Sexpr.ATOM
(MirTypes.UNARY(MirTypes.MOVE, gc_reg, MirTypes.GP_IMM_INT 0) ::
map
(fn x =>
MirTypes.STOREOP(MirTypes.ST, gc_reg, cl_reg,
MirTypes.GP_IMM_ANY(4*(x-1)-1)))
(Lists.tl offsets)))
end
val new_closure_list =
map (fn (closure, offset) =>
lists_reducel
(fn (clos, (lv, off)) => Mir_Env.add_closure_env((lv, off), clos))
(closure, #1 (Lists.number_from(lv_list, 0 - offset, 2, fn x=> x))))
(Lists.zip(new_closure_list, offsets))
val arg_regs_list =
map (fn (arg_regs,fp_regs) => (assign_callee_regs arg_regs,
assign_fp_regs fp_regs))
fn_args
val args_and_copied_callee_arg_list =
map
(fn (arg_regs, fp_regs) =>
(map (fn lvar => (lvar, MirTypes.GC.new())) arg_regs,
map (fn lvar => (lvar, MirTypes.FP.new())) fp_regs))
fn_args
val initial_env_list =
map
(fn (arg_reg_copies,fn_arg_reg_copies) =>
let
val env =
lists_reducel
(fn (env,(lvar, copy)) => Mir_Env.add_lambda_env((lvar, MirTypes.GC copy),env))
(Mir_Env.empty_lambda_env,arg_reg_copies)
in
lists_reducel
(fn (env,(lvar, copy)) => Mir_Env.add_lambda_env((lvar, MirTypes.FLOAT copy),env))
(env,fn_arg_reg_copies)
end)
args_and_copied_callee_arg_list
fun generate_entry_code ((name,_),arginfo) =
let
val comment_for_name =
if name = ""
then []
else [MirTypes.COMMENT name]
in
((Sexpr.ATOM(comment_for_name @ [MirTypes.ENTER arginfo] @
(if intercept
then [MirTypes.INTERCEPT]
else [])),
[], NONE, Sexpr.NIL), [], [])
end
val entry_code_list =
map
generate_entry_code
(Lists.zip (lambda_names,
map (fn (arg_regs,fp_arg_regs) =>
map MirTypes.GC arg_regs @
map MirTypes.FLOAT fp_arg_regs)
arg_regs_list))
val arg_copy_list =
(map (fn ((arg_regs,fp_arg_regs),(arg_copies,fp_arg_copies)) =>
(make_get_args_code (arg_regs,map #2 arg_copies) @
make_fp_get_args_code (fp_arg_regs,map #2 fp_arg_copies)))
(Lists.zip (arg_regs_list,args_and_copied_callee_arg_list)))
val restore_spills = spill_restorer()
val code_env_static_start_list =
map
(fn (arg_copy, env, new_closure, pos, x) =>
let
val first_spill = initialize_spills()
val (static, start,spills,calls) = (pos, start_at + pos, (1,0,0),0)
val entry_code = ((Sexpr.ATOM arg_copy, [], NONE, Sexpr.NIL), [], [])
in
(make_call_code (0,entry_code),env, static, start,spills,calls,first_spill)
end)
(zip5 (arg_copy_list,
initial_env_list,
new_closure_list,
positions,
offsets))
val tuple_bindings_list =
map
(fn (args,fp_args) => (map #2 args,map #2 fp_args))
args_and_copied_callee_arg_list
val fn_reg_code_list =
map
(fn ({lexp,...},
(entry_code, env, static_offset, start_at,spills,calls,first_spill),
new_closure, x, lvar, tuple_bindings, fn_args) =>
let
val _ = reset_spills first_spill
val loop_tag = top_lambda_loop_tags lvar
val new_local_functions = [(lvar,(loop_tag,tuple_bindings))]
val (regs, body_code,runtime_env',spills,calls) =
cg_letrec_sub(lexp, env, static_offset, start_at,true,
(new_closure,funs - x div 2, tags,new_local_functions),spills,calls)
val end_block =
((Sexpr.ATOM [MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG loop_tag)],
[],
SOME loop_tag,
Sexpr.ATOM
[if insert_interrupt then
MirTypes.INTERRUPT
else
MirTypes.COMMENT"Interrupt point (not coded)"]),
[], [])
val runtime_env =
RuntimeEnv.LIST([runtime_env'])
in
(regs, Mir_Utils.combine(Mir_Utils.combine(entry_code, end_block),body_code),
(runtime_env,spills,first_spill),
loop_tag)
end)
(zip7 (fn_bodies,
code_env_static_start_list,
new_closure_list,
offsets,
lv_list,
tuple_bindings_list,
fn_args))
val _ = restore_spills()
val fn_list =
map
(fn ((res, fn_code, env_spills, loop_tag), entry_code) =>
(Mir_Utils.combine (entry_code, Mir_Utils.combine (fn_code, make_exit_code res)),
env_spills,
loop_tag))
(zip2 (fn_reg_code_list, entry_code_list))
val fn_val_procs_proc_list =
map
(fn (tag,
(((first, blocks, tag_opt, last), vals, procs),
(runtime_env, (gc_spills, non_gc_spills, fp_spills), first_spill),
loop_tag),
(name,instances)) =>
let
val spill_sizes =
if variable_debug
then SOME{gc = gc_spills+1,
non_gc = non_gc_spills+1,
fp = fp_spills+1}
else NONE
val runtime_env =
if variable_debug
then RuntimeEnv.FN(name,runtime_env,head_spill(first_spill),instances)
else RuntimeEnv.EMPTY
val proc =
MirTypes.PROC(name,
tag,
MirTypes.PROC_PARAMS {spill_sizes = spill_sizes,
old_spill_sizes = NONE,
stack_allocated = NONE},
MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr first) ::
blocks @
(case tag_opt of
NONE => []
| SOME tag =>
[MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr last)]),
runtime_env)
in
(vals,procs,proc)
end)
(zip3 (tags, fn_list,lambda_names))
val letrec_vals =
Lists.reducer
(fn ((value,_,_), value') => value @ value')
(fn_val_procs_proc_list, [])
val proc_list =
Lists.reducer
(fn ((_, procs,_), procs') => procs @ procs')
(fn_val_procs_proc_list, [])
val letrec_proc_list = map #3 fn_val_procs_proc_list
val letrec_env =
lists_reducel
(fn (env, (lv, (_, reg))) =>
Mir_Env.add_lambda_env((lv, MirTypes.GC reg), env))
(Mir_Env.empty_lambda_env, Lists.zip(lv_list, new_reg_list))
val (regs, ((first, body_blocks, tag_opt, last), body_vals, body_procs),runtime_env,spills,calls) =
cg_sub(lexp, Mir_Env.augment_lambda_env (env, letrec_env),
static_offset + gc_objects_within + funs,start_at + gc_objects_within + funs,tail_position,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls)
val runtime_env =
if variable_debug
then RuntimeEnv.LET(runtime_lets,runtime_env)
else RuntimeEnv.EMPTY
in
(regs, ((Sexpr.CONS(code, first), body_blocks, tag_opt, last),
body_vals @ letrec_vals,
letrec_proc_list :: body_procs @ proc_list),
runtime_env,
spills,calls)
end
| cg_sub(arg as AugLambda.RAISE({lexp=le, ...}), env, static_offset,start_at,_,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
val (reg, code,runtime_env,spills,calls') =
cg_sub(le, env, static_offset, start_at, false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls+1)
val final_code =
let
val r = MirTypes.GC.new ()
in
Mir_Utils.send_to_given_reg(reg, r) @
[MirTypes.RAISE (MirTypes.GC_REG r),
MirTypes.COMMENT"Call the exception handler, so we can backtrace"]
end
val total_code =
Mir_Utils.combine(code,
make_call_code(calls+1,
((Sexpr.ATOM final_code, [], NONE,
Sexpr.NIL), [], [])))
in
(reg, total_code,
if variable_debug then
RuntimeEnv.RAISE(runtime_env)
else
RuntimeEnv.EMPTY,spills,calls')
end
| cg_sub(exp as AugLambda.HANDLE
({lexp=le, size=gc_objects_in_le}, {lexp=le', size=gc_objects_in_le'}),
env, static_offset,start_at, tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls) =
let
val lv = LambdaTypes.new_LVar()
val handler_arg = MirTypes.GC.new()
val foo = ref ""
fun convert_handler(AugLambda.FN(([lv'],[]), body, name, _)) =
(foo := name;
SOME
(AugLambda.LET((lv', NONE,
{size=0, lexp=AugLambda.VAR lv}), body))
)
| convert_handler(AugLambda.LET(larg, {lexp=body, ...})) =
(case convert_handler body of
SOME le =>
SOME
(AugLambda.LET(larg, {size=0, lexp=le}))
| x => x)
| convert_handler(AugLambda.FN _) =
Crash.impossible"Handler has multiple parameters"
| convert_handler le = NONE
val end_tag = MirTypes.new_tag()
val common_tag = MirTypes.new_tag()
val continue_tag = MirTypes.new_tag()
val handler_frame_reg = MirTypes.GC.new()
val handler_frame = MirTypes.GC_REG handler_frame_reg
val (gc_spills,non_gc_spills,fp_spills) = spills
val slot = gc_spills+1
val spills = (gc_spills+1,non_gc_spills,fp_spills)
val ref_slot = new_ref_slot slot
val result_reg = MirTypes.GC.new()
val frame_size = 4
val exn_common =
MirTypes.BLOCK
(common_tag,
[MirTypes.OLD_HANDLER,
MirTypes.DEALLOCATE_STACK(MirTypes.ALLOC, frame_size),
MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG end_tag)])
fun make_frame_setup(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG r))) =
let
val exn_result_reg = MirTypes.GC_REG r
val offset_reg = MirTypes.GC.new()
val offset = MirTypes.GC_REG offset_reg
val gp_offset = MirTypes.GP_GC_REG offset_reg
val frame_setup =
[MirTypes.STOREOP(MirTypes.ST, MirTypes.GC_REG sp, handler_frame,
MirTypes.GP_IMM_ANY 3),
MirTypes.STOREOP(MirTypes.ST, exn_result_reg,
handler_frame, MirTypes.GP_IMM_ANY 7),
MirTypes.STOREOP(MirTypes.ST, offset, handler_frame,
MirTypes.GP_IMM_ANY 11)]
in
MirTypes.ALLOCATE_STACK(MirTypes.ALLOC,
handler_frame,
frame_size,
NONE) ::
MirTypes.ADR(MirTypes.LEO, offset, continue_tag) ::
MirTypes.COMMENT "Calculate offset of continuation code" ::
MirTypes.BINARY(MirTypes.ASL, offset, gp_offset, MirTypes.GP_IMM_ANY 2) ::
MirTypes.COMMENT "Ensure it's tagged" ::
MirTypes.NEW_HANDLER(handler_frame, continue_tag) ::
frame_setup @
[MirTypes.COMMENT"Set up new handler pointer"]
end
| make_frame_setup _ =
Crash.impossible"Exn_result_reg""Exn_result_reg"
in
case (convert_handler le',
opt_handlers andalso gc_objects_in_le' = 1 andalso not debug_variables) of
(SOME new_la, true) =>
let
val new_handler_lambda =
let
val lv = LambdaTypes.new_LVar()
in
AugLambda.FN(([lv],[]),{size = 0, lexp = AugLambda.VAR lv},
"replacement handler", RuntimeEnv.USER_FUNCTION)
end
val (exn_result_reg,
exn_code
,runtime_env,spills,calls') =
cg_sub(new_handler_lambda, env,
static_offset + gc_objects_in_le,
start_at + gc_objects_in_le,false,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls)
local
val env' = Mir_Env.add_lambda_env ((lv, MirTypes.GC handler_arg), env)
val (tail_reg, tail_code,runtime_env',spills,calls) =
cg_sub(new_la, env',
static_offset + gc_objects_in_le,start_at + gc_objects_in_le,tail_position,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls)
in
val exn_end =
Mir_Utils.combine
(Mir_Utils.combine
(((Sexpr.NIL, [], SOME continue_tag,
Sexpr.ATOM
(Mir_Utils.send_to_given_reg(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG caller_arg)),
handler_arg) @
[MirTypes.OLD_HANDLER,
MirTypes.DEALLOCATE_STACK(MirTypes.ALLOC, frame_size)])),
[], []), tail_code),
((Sexpr.ATOM
(Mir_Utils.send_to_given_reg(tail_reg, result_reg) @
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG end_tag)]),
[], SOME end_tag,
Sexpr.ATOM[MirTypes.COMMENT"Handle result point"]), [], [])
)
end
val frame_setup = make_frame_setup exn_result_reg
val exn_whole =
Mir_Utils.combine(exn_code,
((Sexpr.ATOM frame_setup, [exn_common], NONE,
Sexpr.NIL), [], []))
val restore_spills = spill_restorer()
val _ = append_spill ref_slot
val (main_reg, main_code,runtime_env',spills,calls) =
cg_sub(le, env, static_offset, start_at, false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls')
val _ = restore_spills()
val main_end =
Mir_Utils.send_to_given_reg(main_reg, result_reg) @
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG common_tag)]
val main_whole =
Mir_Utils.combine(main_code,
((Sexpr.ATOM main_end, [], NONE,
Sexpr.NIL), [], []))
val total_code =
Mir_Utils.combine(Mir_Utils.combine(exn_whole, main_whole), exn_end)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result_reg)), total_code,
RuntimeEnv.EMPTY,spills,calls)
end
| _ =>
let
val (exn_result_reg,
exn_code
,runtime_env,spills,calls') =
cg_sub(le', env,
static_offset + gc_objects_in_le,start_at + gc_objects_in_le,false,
(closure,funs_in_closure, fn_tag_list,local_fns),spills,calls)
val exn_end =
MirTypes.BLOCK
(continue_tag,
Mir_Utils.send_to_given_reg(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG caller_arg)),
result_reg) @
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG common_tag)])
val frame_setup = make_frame_setup exn_result_reg
val exn_whole =
Mir_Utils.combine(exn_code,
((Sexpr.ATOM frame_setup, [exn_common, exn_end], NONE,
Sexpr.NIL), [], []))
val restore_spills = spill_restorer()
val _ = append_spill ref_slot
val (main_reg, main_code,runtime_env',spills,calls) =
cg_sub(le, env, static_offset, start_at, false,
(closure,funs_in_closure,fn_tag_list,local_fns),spills,calls')
val _ = restore_spills()
val main_end =
Mir_Utils.send_to_given_reg(main_reg, result_reg) @
[MirTypes.BRANCH(MirTypes.BRA, MirTypes.TAG common_tag)]
val main_whole =
Mir_Utils.combine(main_code,
((Sexpr.ATOM main_end, [], SOME end_tag,
Sexpr.ATOM[MirTypes.COMMENT"Handle result point"]), [], []))
val total_code =
Mir_Utils.combine(exn_whole, main_whole)
in
(Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_GC_REG result_reg)), total_code,
if variable_debug then
RuntimeEnv.HANDLE(runtime_env',ref_slot,calls',calls,runtime_env)
else
RuntimeEnv.EMPTY,spills,calls)
end
end
| cg_sub(AugLambda.BUILTIN(prim,_), env, static_offset,start_at,_,
(closure,funs_in_closure, fn_tag_list,local_fns),_,_) =
let
val string = LambdaPrint.string_of_lambda(LambdaTypes.BUILTIN prim)
in
Crash.impossible (concat ["cg_sub(BUILTIN): ",string, " should have been translated"])
end
val restore_spills = spill_restorer()
val new_tag = top_closure 0
val (regs,
((first, blocks, tag_opt, last), values, proc_lists),
runtime_env,
(gc_spills,non_gc_spills,fp_spills),_) =
cg_sub(new_lambda_exp, Mir_Env.empty_lambda_env, 0, 0, false,
(Mir_Env.empty_closure_env,1, [new_tag], []),(1,0,0),0)
val (reg, last') =
case regs of
Mir_Utils.LIST sub_regs =>
let
val result_reg = MirTypes.GC.new()
val (reg, new_code) = Mir_Utils.tuple_up_in_reg (sub_regs, result_reg)
in
(MirTypes.GP_GC_REG callee_arg,
Sexpr.CONS
(last,
Sexpr.CONS
(Sexpr.ATOM new_code,
Sexpr.ATOM [MirTypes.UNARY(MirTypes.MOVE,
MirTypes.GC_REG callee_arg,
MirTypes.GP_GC_REG result_reg)])))
end
| Mir_Utils.ONE(Mir_Utils.INT(arg as MirTypes.GP_GC_REG _)) =>
(MirTypes.GP_GC_REG callee_arg,
Sexpr.CONS(last,
Sexpr.ATOM
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG callee_arg,
arg)]))
| Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_IMM_INT 0)) =>
(MirTypes.GP_GC_REG callee_arg,
Sexpr.CONS
(last, Sexpr.ATOM
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG callee_arg,
MirTypes.GP_IMM_INT 0)]))
| Mir_Utils.ONE(Mir_Utils.INT(MirTypes.GP_IMM_INT 1)) =>
(MirTypes.GP_GC_REG callee_arg,
Sexpr.CONS
(last, Sexpr.ATOM
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG callee_arg,
MirTypes.GP_IMM_INT 1)]))
| _ => Crash.impossible"Non-struct final result??"
val last'' = Sexpr.CONS(last', Sexpr.ATOM[MirTypes.RTS])
val setup_entry =
Sexpr.CONS
((if intercept then
Sexpr.ATOM[MirTypes.ENTER [MirTypes.GC callee_arg],
MirTypes.INTERCEPT]
else
Sexpr.ATOM[MirTypes.ENTER [MirTypes.GC callee_arg]]),
Sexpr.CONS
(Sexpr.ATOM
[MirTypes.UNARY(MirTypes.MOVE, MirTypes.GC_REG callee_closure,
MirTypes.GP_GC_REG callee_arg)], first))
val (first, blocks) =
case (tag_opt, Mir_Utils.contract_sexpr last) of
(NONE, []) => (Sexpr.CONS (setup_entry, last''), blocks)
| (SOME tag,_) =>
(setup_entry, MirTypes.BLOCK(tag, Mir_Utils.contract_sexpr last'') ::
blocks)
| (NONE,_) =>
Crash.impossible "ABSENT tag with non-empty last"
val loc_refs = top_tags_list
val set_up_proc =
MirTypes.PROC(name_of_setup_function,
new_tag,
MirTypes.PROC_PARAMS {spill_sizes =
if variable_debug then
SOME{gc = gc_spills+1,
non_gc = non_gc_spills+1,
fp = fp_spills+1}
else
NONE,
old_spill_sizes = NONE,
stack_allocated = NONE},
MirTypes.BLOCK(new_tag,
Mir_Utils.contract_sexpr first) :: blocks,
if variable_debug then
(restore_spills ();
RuntimeEnv.FN(name_of_setup_function,runtime_env,
head_spill(get_current_spills ()),RuntimeEnv.USER_FUNCTION))
else
RuntimeEnv.EMPTY)
val all_procs = [set_up_proc] :: proc_lists
in
(MirTypes.CODE(MirTypes.REFS(loc_refs,
{requires = ext_string_list,
vars = ext_var_list,
exns = ext_exn_list,
strs = ext_str_list,
funs = ext_fun_list}),
values, all_procs),
debug_information)
end
end
;
