require "^.basis.__int";
require "^.basis.__text_io";
require "../utils/lists";
require "../basics/identprint";
require "../debugger/debugger_types";
require "pretty";
require "lambdatypes";
require "lambdaprint";
functor LambdaPrint (
structure Lists : LISTS
structure Pretty : PRETTY
structure IdentPrint : IDENTPRINT
structure LambdaTypes : LAMBDATYPES where type LVar = int
structure DebuggerTypes : DEBUGGER_TYPES
sharing LambdaTypes.Ident = IdentPrint.Ident
sharing DebuggerTypes.Options = IdentPrint.Options
sharing type LambdaTypes.Type = DebuggerTypes.Type
sharing type DebuggerTypes.RuntimeEnv.VarInfo = LambdaTypes.VarInfo
) : LAMBDAPRINT =
struct
structure P = Pretty
structure IP = IdentPrint
structure Ident = IdentPrint.Ident
structure Symbol = Ident.Symbol
structure LambdaTypes = LambdaTypes
structure Options = IdentPrint.Options
structure RuntimeEnv = DebuggerTypes.RuntimeEnv
val show_types = true
exception Crash of string
fun crash s = raise Crash s
fun string_var var =
"v" ^ LambdaTypes.printLVar var
fun string_info_var (var,info) =
let
fun print_spill_type RuntimeEnv.GC = "(GC spill)"
| print_spill_type RuntimeEnv.NONGC = "(NONGC spill)"
| print_spill_type RuntimeEnv.FP = "(FP spill)"
fun print_spill NONE = "<no spill>"
| print_spill (SOME (ref (RuntimeEnv.OFFSET1 i))) =
"Virtual spill " ^ Int.toString i
| print_spill (SOME (ref (RuntimeEnv.OFFSET2(spill_ty, i)))) =
"Real spill " ^ print_spill_type spill_ty ^ Int.toString i
val info_name =
case info of
SOME (ref (RuntimeEnv.VARINFO (name,_,spill))) =>
": " ^ name ^ ": " ^ print_spill spill
| _ => ""
in
"v" ^ LambdaTypes.printLVar var ^ info_name
end
local
fun is_app (LambdaTypes.APP _) = true
| is_app _ = false
fun compound (LambdaTypes.APP _) = true
| compound (LambdaTypes.FN _) = true
| compound (LambdaTypes.HANDLE _) = true
| compound _ = false
fun bracket t x =
if t
then [P.brk 0, P.str "("] @ x @ [P.str ")"]
else x
fun unwrap_lets expr =
let
fun unwl(acc, LambdaTypes.LET((var,info,bind),body)) = unwl((var,info,bind)::acc,body)
| unwl(acc, expr) = (rev acc, expr)
in
unwl([],expr)
end
fun map_passing_on_env (f,[],env,acc) = (rev acc,env)
| map_passing_on_env (f,h::t,env,acc) =
let
val (res,env') = f (h,env)
in
map_passing_on_env(f,t,env',res::acc)
end
fun short_name name =
let fun aux ([],acc) = acc
| aux (#"[" ::rest,acc) = aux2 (rest,acc)
| aux (a::l,acc) = aux (l,a::acc)
and aux2 ([],acc) = acc
| aux2 (#"]" ::rest,acc) = aux (rest,acc)
| aux2 (_::rest,acc) = aux2 (rest,acc)
in
"[" ^ implode (rev (aux (explode name,[]))) ^ "]"
end
fun printvarlist lvl =
let
fun f ([],acc) = acc
| f ([lvar],acc) = string_var lvar :: acc
| f (lvar::rest,acc) = f (rest,", " :: string_var lvar :: acc)
in
concat (rev (f (lvl,[])))
end
fun printarglist (lvl,fplvl) =
case fplvl of
[] => printvarlist lvl
| _ => printvarlist lvl ^ "; " ^ printvarlist fplvl
in
fun decodetag options (tag,env) =
case tag of
LambdaTypes.VCC_TAG (_,i) =>
(P.blk(0,[P.str("vcc "),
P.str(Int.toString i)]),env)
| LambdaTypes.IMM_TAG (_,i) =>
(P.blk(0,[P.str("imm "),
P.str(Int.toString i)]),env)
| LambdaTypes.SCON_TAG (scon, _) =>
(P.blk(0,[P.str("scon "),
P.str(IP.printSCon scon)]),env)
| LambdaTypes.EXP_TAG exp =>
decodelambda options (exp,env)
and decodelambda options (expression,env) =
case expression of
LambdaTypes.VAR lvar => (P.blk(0,[P.str(string_var lvar)]),env)
| LambdaTypes.FN (args, lambda,_,name,ty,_) =>
let
val (ty,env') =
if not show_types then ([],env)
else
let
val (str,env') = DebuggerTypes.string_types options (ty,env)
in
([P.brk 1, P.str ": ",P.str(str)],
env')
end
val (lambda',env'') = decodelambda options (lambda,env')
in
(P.blk(0,[P.str "fn ",P.str (short_name name)] @
ty @
[P.str " ",
P.blk(0,[P.str("["),
P.str(printarglist args),
P.str("]"),
P.str(" => "),
P.blk (2,[lambda'])])]),env'')
end
| LambdaTypes.LETREC(lvar_list, lambda_list, lambda_exp) =>
let
val (decoded_lambda_list,env') =
map_passing_on_env (decodelambda options,lambda_list,env,[])
val (lambda_exp',env'') = decodelambda options (lambda_exp,env')
in
(P.blk(0,
[P.str "letrec ",P.nl,
P.blk(0, P.lst ("",[P.nl],"")
(map (fn (lv, le) =>
P.blk(2,[P.str(string_var lv),
P.str " = ",
P.blk(0,[le])]))
(Lists.zip(map #1 lvar_list,
decoded_lambda_list)))),
P.nl,
P.str "in",
P.blk (2,[lambda_exp']),
P.nl,P.str"end "]),
env'')
end
| LambdaTypes.LET _ =>
let
val (lets,expr) = unwrap_lets expression
val (decoded_lets,env') =
map_passing_on_env
((fn ((x,info,y),env) =>
let
val (exp,env') = decodelambda options (y,env)
in
((x,info,exp),env')
end),
lets,env,[])
val (expr',env'') = decodelambda options (expr,env')
in
(P.blk(0,[P.nl,P.str "let ",P.nl,
P.blk(2,
P.lst ("",[P.nl],"")
(map (fn (var,info,arg) =>
P.blk(0,[P.str (string_info_var (var,info)),
P.str " = ",
P.blk(0,[arg])])) decoded_lets)),
P.nl,
P.str "in "] @
[P.blk (2,[expr'])] @ [P.nl,P.str"end "]),
env'')
end
| LambdaTypes.APP(lambda_fn, (lambda_args, fp_args), ty) =>
let
val (decoded_lambda_fn,env') = decodelambda options (lambda_fn,env)
val (exp,env'') = map_passing_on_env(decodelambda options ,lambda_args,env,[])
val (exp',env'') = map_passing_on_env(decodelambda options ,fp_args,env,[])
val (str,env''') =
if not show_types then ([],env'')
else
let
val (tystr,env''') =
case ty of
NONE => ("Nulltype",env'')
| SOME (ty) =>
DebuggerTypes.string_types options (!ty,env'')
in
([P.brk 1, P.str ":", P.brk 1,
P.str(tystr)],
env''')
end
val argstuff = P.blk(0,[P.blk(1,
case exp' of
[] => P.lst ("[", [P.str ",", P.brk 1], "]") exp
| _ =>
(P.lst ("[", [P.str ",", P.brk 1], "") exp @
P.lst (";", [P.str ",", P.brk 1], "]") exp'))])
in
(P.blk(0,[P.blk(0,(bracket ((compound lambda_fn)
andalso (not(is_app lambda_fn)))
[decoded_lambda_fn])),
P.blk(1,argstuff :: str)]),
env)
end
| LambdaTypes.SCON (scon, _) =>
(P.blk(0,[P.str (IP.printSCon scon)]),env)
| LambdaTypes.MLVALUE scon =>
(P.blk(0,[P.str("mlvalue "),P.str ("_")]),env)
| LambdaTypes.INT i =>
(P.blk(0,[P.str("INT "),P.str (Int.toString i)]),env)
| LambdaTypes.SWITCH(lambda, info_opt, c_lambda_list, lambda_opt) =>
let
val (pt1, env) = decodelambda options (lambda,env)
val (pt3, env) = map_passing_on_env
(fn ((tag, x), env) =>
let
val (tag', env) = decodetag options (tag, env)
val (lambda', env) = decodelambda options (x, env)
in
((tag', lambda'), env)
end,
c_lambda_list, env, [])
val (pt4, env) =
case lambda_opt of
NONE => ([], env)
| SOME lam =>
let
val (pt5,env) = decodelambda options (lam, env)
in
([P.blk(3,[P.str"_ ",P.str"=> ",pt5])],env)
end
in
(P.blk(0,
[P.nl, P.str "switch ", pt1, P.nl,
P.blk(0,
(P.lst ("  ",[P.nl, P.str"| "],"")
((map (fn (c, l) =>
(P.blk(3, [c,P.str" => ",l])))
pt3) @ pt4)))]),
env)
end
| LambdaTypes.STRUCT (lambda_list,_)
=>
let
val (exp,env') = map_passing_on_env(decodelambda options ,lambda_list,env,[])
in
(P.blk(0,[P.blk(1,P.lst ("(", [P.str ",", P.brk 1], ")")
exp)]),env')
end
| LambdaTypes.SELECT(field, lambda)
=>
let
val (expr,env') = decodelambda options (lambda,env)
in
(P.blk(0,bracket(compound lambda) [expr,
P.str "[",
P.str(LambdaTypes.printField(field)),
P.str "]"]),env')
end
| LambdaTypes.RAISE (lambda)
=>
let
val (expr,env') = decodelambda options (lambda,env)
in
(P.blk(0,[P.str "raise ",expr]),env')
end
| LambdaTypes.HANDLE(lambda1, lambda2,_)
=>
let
val (expr,env') = decodelambda options (lambda1,env)
val (expr',env'') = decodelambda options (lambda2,env')
in
(P.blk(0,(bracket (compound lambda1) [expr]) @
[P.str(" handle"), P.brk 1] @
(bracket (compound lambda2) [expr'])),env'')
end
| LambdaTypes.BUILTIN prim
=> (P.blk(0,[P.str"builtin ", P.str(LambdaTypes.printPrim prim)]),env)
end
fun string_of_lambda e =
P.string_of_T (P.blk(1,[P.brk 1, #1(decodelambda Options.default_options (e,[]))]));
fun output_lambda options (stm,e) =
let
val pt = (P.blk(0,[P.brk 0, #1(decodelambda options (e,[]))]));
in
(P.print_T (fn s=>TextIO.output(stm,s)) pt;
TextIO.output(stm,"\n"))
end
fun print_lambda options lam = output_lambda options (TextIO.stdOut,lam)
exception PrintExp
val N = Int.toString
fun print_var v = "v" ^ N v
fun print_vars vl =
let
fun aux [] = ""
| aux [a] = print_var a
| aux (a::l) = print_var a ^ "," ^ aux l
in
aux vl
end
fun print_args (vl,fpvl) =
case fpvl of
[] => "(" ^ print_vars vl ^ ")"
| _ => "(" ^ print_vars vl ^ "; " ^ print_vars fpvl ^ ")"
fun indent level =
let
fun aux (0,acc) = acc
| aux (n,acc) = aux (n-1, #" " :: #" "::acc)
in
implode (#"\n" :: aux (level,[]))
end
exception ExpTag
fun print_builtin b = "<" ^ LambdaTypes.printPrim b ^ ">"
local
open LambdaTypes
in
fun iprint_exp (e,level,acc) =
case e of
INT n => N n :: acc
| SCON (s, _) => IdentPrint.printSCon s :: acc
| BUILTIN b => print_builtin b :: acc
| VAR var => print_var var :: acc
| APP (e,(el,_),_) => print_explist (el,level,iprint_exp (e,level,acc))
| STRUCT ([],_) => "1" :: acc
| STRUCT (el,STRUCTURE) => print_explist (el,level,"structure " :: acc)
| STRUCT (el,TUPLE) => print_explist (el,level,"tuple " :: acc)
| STRUCT (el,CONSTRUCTOR) => print_explist (el,level,"cons " :: acc)
| SWITCH (e,info,tel,oe) => print_opte (oe, level+1,
print_tel (tel,level+1,indent (level+1)::" of ":: iprint_exp (e,level+1,"case " :: acc)))
| HANDLE (e1,e2,_) => iprint_exp (e2,level," handle ":: indent (level+1) :: iprint_exp (e1,level+1,acc))
| RAISE e => iprint_exp (e,level+1,"raise " :: acc)
| FN (args,e,functorp,name,_,_) =>
iprint_exp (e,level+1," => "::
"]" :: name :: "[" ::
print_args args ::"fn "::indent level :: acc)
| LETREC (fl, vel,e) =>
"end" :: indent level :: iprint_exp (e,level+1, indent (level+1) :: "in" :: indent level :: print_flist (Lists.zip (fl,vel),level+1,"let rec "::acc))
| SELECT ({index,selecttype=STRUCTURE,...},e) =>
")" :: iprint_exp (e,level, ", " :: N index :: "sselect(" :: acc)
| SELECT ({index,...},e) =>
")" :: iprint_exp (e,level, ", " :: N index :: "select(" :: acc)
| LET _ => print_let (e,level,acc)
| MLVALUE _ => "<mlvalue>" :: acc
and print_opte (NONE,level,acc) = acc
| print_opte (SOME e,level,acc) = iprint_exp (e,level,"_ => " :: indent level :: " |":: acc)
and print_tel (l,level,acc) =
let fun print_one ((t,e),acc) =
iprint_exp (e, level+1," => " :: print_tag (t,level+1,acc))
in
case l of
[] => acc
| [x] => print_one (x,acc)
| (x::y) => print_tel (y,level, indent level :: " |" :: print_one (x,acc))
end
and print_tag (VCC_TAG (_,i),level,acc) = N i :: "vcc " :: acc
| print_tag (IMM_TAG (_,i),level,acc) = N i :: "imm " :: acc
| print_tag (SCON_TAG (s, _),level,acc) = IdentPrint.printSCon s :: "scon " :: acc
| print_tag (EXP_TAG e,level,acc) = iprint_exp (e,level, "exp " :: acc)
and print_flist ([],level,acc) = acc
| print_flist (((f,_),(FN (args,e,_,name,_,_)))::l,level,acc) =
print_flist (l,level," " :: iprint_exp (e,level," = "::"]"::name::"["::print_args args ::print_var f::acc))
| print_flist ((f,_)::l,level,acc) = crash "Bad exp in letrec"
and print_explist (el,level,acc) =
let
fun aux ([],acc) = ")"::acc
| aux ([e],acc) = ")"::iprint_exp (e,level,acc)
| aux (e::el,acc) = aux(el,","::iprint_exp(e,level,acc))
in
aux (el,"("::acc)
end
and print_let (e,level,acc) =
let
fun aux (e,acc) =
case e of
LET ((v,_,FN (args,body,status,name,_,_)),e2) =>
aux (e2," " ::
iprint_exp (body,
level+2,
" = "::"]"::
name::"["::print_args args::" "::print_var v::(case status of FUNC => "functor " | _ => "fun ")
::indent(level+1)::acc))
| LET ((v,_,e1),e2) =>
aux (e2," " :: iprint_exp (e1,level+2," = "::print_var v::"val "::indent(level+1)::acc))
| e => "end " :: indent level :: iprint_exp (e,level+2,indent (level+1)::"in"::indent level :: acc)
in
case e of
LET _ => aux (e,"let "::indent level :: acc)
| _ => iprint_exp (e,level,acc)
end
fun print_exp e = concat (rev (iprint_exp (e,0,[])))
fun to_expression ([],exp) = exp
| to_expression ((v,i,dec)::rest,exp) =
LET ((v,i,dec_to_expression dec),
to_expression (rest,exp))
and dec_to_expression (VAL e) = e
| dec_to_expression (FUNCTOR (arg,name,body)) =
FN (([arg],[]),to_expression body,FUNC,name,null_type_annotation,user_funinfo)
fun pds (PROGRAM p) = concat (rev (print_let (to_expression p,0,[])))
fun pde e = concat (rev (print_let (e,0,[])))
end
end
;
