require "../utils/sexpr";
require "../utils/map";
require "../utils/diagnostic";
require "../lambda/auglambda";
require "mir_env";
signature MIR_UTILS = sig
structure Sexpr : SEXPR
structure Map : MAP
structure Diagnostic : DIAGNOSTIC
structure Mir_Env : MIR_ENV
structure AugLambda : AUGLAMBDA
sharing Mir_Env.LambdaTypes = AugLambda.LambdaTypes
datatype reg_result =
INT of Mir_Env.MirTypes.gp_operand |
REAL of Mir_Env.MirTypes.fp_operand
datatype cg_result =
ONE of reg_result |
LIST of reg_result list
val cg_lvar : Mir_Env.LambdaTypes.LVar *
Mir_Env.Lambda_Env *
Mir_Env.Closure_Env *
int ->
(reg_result * Mir_Env.MirTypes.opcode list) * int * bool
val cg_lvar_fn : Mir_Env.LambdaTypes.LVar *
Mir_Env.Lambda_Env *
Mir_Env.Closure_Env *
int ->
reg_result * Mir_Env.MirTypes.opcode list
val combine : ((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) ->
(Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
val contract_sexpr : Mir_Env.MirTypes.opcode list Sexpr.Sexpr ->
Mir_Env.MirTypes.opcode list
exception ConvertInt
exception Unrepresentable
val convert_int : string * int option -> int
val convert_long_int :
AugLambda.LambdaTypes.Ident.SCon * int option -> AugLambda.AugLambdaExp
val convert_word : string * int option -> int
val convert_long_word :
AugLambda.LambdaTypes.Ident.SCon * int option -> AugLambda.AugLambdaExp
val destruct_2_tuple : Mir_Env.MirTypes.gp_operand ->
Mir_Env.MirTypes.gp_operand *
Mir_Env.MirTypes.gp_operand *
Mir_Env.MirTypes.opcode list
val get_any_register : reg_result -> Mir_Env.MirTypes.any_register
val get_real : reg_result ->
Mir_Env.MirTypes.fp_operand * Mir_Env.MirTypes.opcode list
val get_word32 :
reg_result ->
Mir_Env.MirTypes.gp_operand
* Mir_Env.MirTypes.opcode list
* Mir_Env.MirTypes.opcode list
val gp_from_reg : Mir_Env.MirTypes.reg_operand ->
Mir_Env.MirTypes.gp_operand
val list_of : int * 'a -> 'a list
val list_of_tags : int -> Mir_Env.MirTypes.tag list
val make_closure : 'a list *
Mir_Env.LambdaTypes.LVar list *
int *
int *
Mir_Env.Lambda_Env *
Mir_Env.Closure_Env *
int ->
Mir_Env.MirTypes.reg_operand *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.Closure_Env list
val do_app : Mir_Env.MirTypes.Debugger_Types.Backend_Annotation *
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) ->
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
)
datatype CallType =
LOCAL of Mir_Env.MirTypes.tag * Mir_Env.MirTypes.GC.T list * Mir_Env.MirTypes.FP.T list
| SAMESET of int
| EXTERNAL
val do_multi_app : Mir_Env.MirTypes.Debugger_Types.Backend_Annotation *
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
CallType *
int *
Mir_Env.MirTypes.tag list *
bool ->
cg_result *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
) *
((Mir_Env.MirTypes.opcode list Sexpr.Sexpr *
Mir_Env.MirTypes.block list *
Mir_Env.MirTypes.tag option *
Mir_Env.MirTypes.opcode list Sexpr.Sexpr
) *
Mir_Env.MirTypes.value list *
Mir_Env.MirTypes.procedure list list
)
val reg_from_gp : Mir_Env.MirTypes.gp_operand ->
Mir_Env.MirTypes.reg_operand
val send_to_given_reg : cg_result * Mir_Env.MirTypes.GC.T ->
Mir_Env.MirTypes.opcode list
val send_to_new_reg : cg_result ->
Mir_Env.MirTypes.gp_operand *
Mir_Env.MirTypes.opcode list
val send_to_reg : cg_result ->
Mir_Env.MirTypes.gp_operand *
Mir_Env.MirTypes.opcode list
val save_real_to_reg : Mir_Env.MirTypes.fp_operand *
Mir_Env.MirTypes.reg_operand ->
Mir_Env.MirTypes.opcode list
val save_word32 : Mir_Env.MirTypes.GC.T ->
Mir_Env.MirTypes.GC.T * Mir_Env.MirTypes.opcode list
val save_word32_in_reg : Mir_Env.MirTypes.GC.T * Mir_Env.MirTypes.GC.T ->
Mir_Env.MirTypes.opcode list
val tuple_up : reg_result list ->
Mir_Env.MirTypes.GC.T * Mir_Env.MirTypes.opcode list
val tuple_up_in_reg : reg_result list * Mir_Env.MirTypes.GC.T ->
Mir_Env.MirTypes.GC.T *
Mir_Env.MirTypes.opcode list
val get_string : {lexp:AugLambda.AugLambdaExp, size:int} ->
string Mir_Env.LambdaTypes.Set.Set
val lift_externals :
((string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map) *
{lexp:AugLambda.AugLambdaExp, size:int} ->
((string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map *
(string, Mir_Env.LambdaTypes.LVar) Map.map) *
{lexp:AugLambda.AugLambdaExp, size:int}
val transform_needed :
bool * {lexp:AugLambda.AugLambdaExp, size:int} -> bool
val needs_prim_stringeq : AugLambda.AugLambdaExp -> bool
end
;
