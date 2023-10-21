require "../basis/__int";
require "^.utils.__terminal";
require "../main/user_options";
require "../utils/crash";
require "../basics/identprint";
require "../interpreter/incremental";
require "../typechecker/basistypes";
require "../typechecker/types";
require "../typechecker/valenv";
require "../typechecker/scheme";
require "entry";
functor Entry(
structure IdentPrint : IDENTPRINT
structure Incremental : INCREMENTAL
structure BasisTypes : BASISTYPES
structure Types : TYPES
structure Valenv : VALENV
structure Scheme : SCHEME
structure Crash : CRASH
structure UserOptions : USER_OPTIONS
sharing IdentPrint.Ident = BasisTypes.Datatypes.Ident = Types.Datatypes.Ident
sharing IdentPrint.Options = UserOptions.Options = Types.Options
sharing Valenv.Datatypes = Types.Datatypes = BasisTypes.Datatypes
sharing type Incremental.InterMake.Compiler.TypeBasis = BasisTypes.Basis
) : ENTRY =
struct
structure Datatypes = BasisTypes.Datatypes
structure Ident = Datatypes.Ident
structure Options = UserOptions.Options
structure NewMap = Datatypes.NewMap
type options = Options.options
type Context = Incremental.Context
type Identifier = Ident.Identifier
fun print_options (Options.OPTIONS{print_options,...}) = print_options
datatype Entry =
VAR of (Ident.ValId * Datatypes.Typescheme)
| CONVAR of (Ident.ValId * Datatypes.Typescheme)
| CONENV of (Ident.ValId * Datatypes.Typescheme)
| EXN of (Ident.ValId * Datatypes.Typescheme)
| TYPE of Ident.TyCon * Datatypes.Tystr
| STR of Ident.StrId * Datatypes.Structure
| SIG of Ident.SigId * BasisTypes.Sigma
| FUN of Ident.FunId * BasisTypes.Phi
| NSIG of Ident.SigId * (Entry list)
| NSTR of Ident.StrId * (Entry list)
| NFUN of Ident.FunId * (Entry list)
fun fst (x,_) = x
datatype SearchOptions =
SEARCH_OPTIONS of
{showSig : bool,
showStr : bool,
showFun : bool,
searchInitial : bool,
searchContext : bool,
showType : bool
}
fun debug_output s = Terminal.output(s ^"\n")
local
val printValId = fn id => IdentPrint.printValId Options.default_print_options id
in
fun get_id e = case e of
VAR (id,_) => (printValId id, true)
| CONVAR (id,_) => (printValId id, true)
| CONENV (id,_) => (printValId id, true)
| EXN (id,_) => (printValId id, true)
| TYPE (id,_) => (IdentPrint.printTyCon id ^ "<type>", true)
| STR (id,_) => (IdentPrint.printStrId id, false)
| SIG (id,_) => (IdentPrint.printSigId id ^ "<signature>", false)
| FUN (id,_) => (IdentPrint.printFunId id ^ "<functor>", false)
| NSIG _ => Crash.impossible "NSIG uncaught"
| NSTR _ => Crash.impossible "NSTR uncaught"
| NFUN _ => Crash.impossible "NFUN uncaught"
end
fun split_vallist e = let
fun split_vallist ([], vars, exns) = (rev vars, rev exns)
| split_vallist ((pair as (Ident.VAR _, _)) :: l, vars, exns) =
split_vallist (l, VAR pair :: vars, exns)
| split_vallist ((pair as (Ident.CON _, _)) :: l, vars, exns) =
split_vallist (l, CONVAR pair :: vars, exns)
| split_vallist ((pair as (Ident.EXCON _, _)) :: l, vars, exns) =
split_vallist (l, vars, EXN pair :: exns)
| split_vallist _ = Crash.impossible "TYCON':split_vallist:browser_tool"
in
split_vallist (e,[],[])
end
fun printOverloaded (Datatypes.UNARY (_, tyvar)) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat [olvar, " -> ", olvar]
end
| printOverloaded (Datatypes.BINARY (_, tyvar)) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat [olvar, " * ", olvar, " -> ", olvar]
end
| printOverloaded (Datatypes.PREDICATE (_, tyvar)) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat [olvar, " * ", olvar, " -> bool"]
end
fun printValScheme options (Datatypes.SCHEME (arity, (ty,_))) =
Types.print_type options ty
| printValScheme options (Datatypes.UNBOUND_SCHEME (ty,_)) =
Types.print_type options ty
| printValScheme _ (Datatypes.OVERLOADED_SCHEME overloaded) =
printOverloaded overloaded
fun print_tyvars options n = Types.print_tyvars options (Types.make_tyvars n)
fun printTyFun options (tycon, Datatypes.TYFUN (ty, arity)) =
print_tyvars options arity ^ " " ^ IdentPrint.printTyCon tycon
| printTyFun options (tycon, Datatypes.ETA_TYFUN tyname) =
print_tyvars options (case tyname of
Datatypes.TYNAME (_, _, n, _, _,_,_,_,_) => n
| Datatypes.METATYNAME (_, _, n, _, _, _) => n) ^
" " ^ IdentPrint.printTyCon tycon
| printTyFun options (tycon, Datatypes.NULL_TYFUN (id,_)) =
Int.toString (Types.stamp_num id)
fun printConScheme options (Datatypes.SCHEME (arity, (ty,_))) =
(case Types.apply (Datatypes.TYFUN (ty, arity),
Types.make_tyvars arity) of
Datatypes.FUNTYPE (arg, _) =>
" of " ^ Types.print_type options arg
| _ => "")
| printConScheme options (Datatypes.UNBOUND_SCHEME
(Datatypes.FUNTYPE (arg, _),_)) =
" of " ^ Types.print_type options arg
| printConScheme _ (Datatypes.UNBOUND_SCHEME _) =
""
| printConScheme _ (Datatypes.OVERLOADED_SCHEME _) =
" <strange overloaded scheme>"
fun printValenv options (Datatypes.VE (_,amap)) =
let fun printMember ((first, str), valid, typescheme) =
(false,
concat
[str,
if first then "" else " | ",
IdentPrint.printValId (print_options options) valid,
printConScheme options typescheme])
in
NewMap.fold printMember ((true, ""), amap)
end
fun printEntry options e = let
val print_options = print_options options
val printValId = fn id => IdentPrint.printValId print_options id
val printValScheme = fn ts => printValScheme options ts
val printStrId = fn id => IdentPrint.printStrId id
in
case e of
VAR (id,ts) => "val " ^ (printValId id) ^ " : " ^ (printValScheme ts)
| CONVAR (id,ts) => "val " ^ (printValId id) ^ " : " ^ (printValScheme ts)
| CONENV (id,ts) => "val " ^ (printValId id) ^ " : " ^ (printValScheme ts)
| EXN (id,ts) => "exception " ^ (printValId id) ^ (printConScheme options ts)
| TYPE (id,Datatypes.TYSTR (tyfun, valenv)) =>
if Valenv.empty_valenvp valenv then
if Types.equalityp tyfun then
"eqtype " ^ (printTyFun options (id, tyfun))
else
"type " ^ (printTyFun options (id, tyfun))
else
"datatype " ^ (printTyFun options (id, tyfun)) ^ " = " ^
#2(printValenv options valenv)
| STR (id, str) => "structure " ^ (IdentPrint.printStrId id)
| SIG (id, ts) => "signature " ^ (IdentPrint.printSigId id)
| FUN (id, ts) => "functor " ^ (IdentPrint.printFunId id)
| NSIG _ => Crash.impossible "printEntry NSIG unhandled"
| NSTR _ => Crash.impossible "printEntry NSTR unhandled"
| NFUN _ => Crash.impossible "printEntry NFUN unhandled"
end
fun printEntry1 (SEARCH_OPTIONS searchOptions, options, entry) =
let
val printValId = fn id => IdentPrint.printValId (print_options options) id
val printValScheme = fn ts =>printValScheme options ts
val printValenv = fn id => printValenv options id
val printConScheme = fn ts =>printConScheme options ts
val printStrId = fn id => IdentPrint.printStrId id
fun printEntry1 (acc, prefix, []) = acc
| printEntry1 (acc, prefix, e::es) =
(case e of
NSIG (id,si) =>
if (#showSig searchOptions) then
let
val id = IdentPrint.printSigId id
val this = prefix ^ id
val entrys = printEntry1 ([], this ^ ".", si)
in
printEntry1 (("signature " ^ this, id) :: entrys @ acc, prefix, es)
end
else printEntry1 (acc,prefix, es)
| NSTR (id,str) =>
if (#showStr searchOptions)
then
let
val id = IdentPrint.printStrId id
val this = prefix ^ id
val entrys = printEntry1 ([], this ^ ".", str)
in
printEntry1 (("structure " ^ this, id) :: entrys@acc, prefix, es)
end
else printEntry1 (acc, prefix, es)
| NFUN (id, ts) =>
if (#showFun searchOptions)
then
let
val id = IdentPrint.printFunId id
val this = prefix ^ id
val entrys = printEntry1 ([], this^".", ts)
in
printEntry1 (("functor " ^ this,id)::entrys@acc, prefix, es)
end
else printEntry1 (acc, prefix, es)
| _ =>
if (#showType searchOptions)
then
case e of
VAR (id, ts) => printEntry1
(("val "^ prefix^(printValId id)^" : "^(printValScheme ts),printValId id)::acc,
prefix, es)
| CONVAR (id, ts) => printEntry1
(("val "^prefix^(printValId id)^(printValScheme ts),printValId id)::acc,
prefix, es)
| CONENV (id, ts) => printEntry1
(("val " ^ prefix ^ (printValId id) ^ " : " ^ (printValScheme ts),printValId id) :: acc,
prefix, es)
| EXN (id,ts) => printEntry1
(("exception " ^ prefix ^ (printValId id) ^ (printConScheme ts),printValId id)::acc,
prefix, es)
| TYPE (id, Datatypes.TYSTR (tyfun, valenv)) => printEntry1
((if Valenv.empty_valenvp valenv then
if Types.equalityp tyfun then
"eqtype " ^ prefix ^(printTyFun options (id, tyfun))
else
"type " ^ prefix ^ (printTyFun options (id, tyfun))
else
"datatype " ^ prefix ^ (printTyFun options (id, tyfun)) ^ " = " ^
#2(printValenv valenv),printTyFun options (id,tyfun)) :: acc,
prefix, es)
| e => Crash.impossible "unconverted SIG STR FUN unhandled"
else
let
val entry = fst (get_id e)
in
printEntry1 ((prefix^entry,entry)::acc, prefix, es)
end)
in
printEntry1 ([], "", entry)
end
fun is_tip (STR(_)) = false
| is_tip (SIG(_)) = false
| is_tip (FUN(_)) = false
| is_tip(_) = true
fun browse_env
(Datatypes.ENV
(Datatypes.SE se,
Datatypes.TE te,
Datatypes.VE (_, ve))) = let
val strlist = NewMap.to_list_ordered se
val tylist = NewMap.to_list_ordered te
val vallist = NewMap.to_list_ordered ve
val strlist' = map STR strlist
val tylist' = map TYPE tylist
val (varlist, exnlist) = split_vallist vallist
in
strlist' @ tylist' @ exnlist @ varlist
end
fun browse_str (Datatypes.STR(_,_,env)) = browse_env env
| browse_str (Datatypes.COPYSTR(_,str)) = browse_str str
fun browse_conenv (Datatypes.VE (_, ve)) =
map CONENV (NewMap.to_list_ordered ve)
fun browse_sigma (BasisTypes.SIGMA (_,str)) = browse_str str
fun massage (SIG (id, sigma)) = NSIG (id, map massage (browse_sigma sigma))
| massage (STR (id, str)) = NSTR (id, map massage (browse_str str))
| massage (FUN (id, BasisTypes.PHI (_, (str, sigma)))) = NFUN (id, map massage (browse_sigma sigma))
| massage e = e
fun browse_entry showConEnv (VAR _) = []
| browse_entry showConEnv (CONVAR _) = []
| browse_entry showConEnv (CONENV _) = []
| browse_entry showConEnv (EXN _) = []
| browse_entry showConEnv (TYPE (id, Datatypes.TYSTR (_, conenv))) =
if not showConEnv then
[] else browse_conenv conenv
| browse_entry showConEnv (STR (_, str)) = browse_str str
| browse_entry showConEnv (SIG (_, sigma)) = browse_sigma sigma
| browse_entry showConEnv (FUN (_, BasisTypes.PHI (_, (str, sigma)))) = browse_sigma sigma
| browse_entry showConEnv x = Crash.impossible "browse_entry barfed"
fun env2entry
(Datatypes.ENV
(Datatypes.SE se,
Datatypes.TE te,
Datatypes.VE (_, ve))) = let
val strl = map STR (NewMap.to_list_ordered se)
val tyl = map TYPE (NewMap.to_list_ordered te)
val (exnl, vall) = split_vallist (NewMap.to_list_ordered ve)
in
strl @ tyl @ exnl @ vall
end
fun context2entry context =
let
val (BasisTypes.BASIS
(_,_, BasisTypes.FUNENV fune,
BasisTypes.SIGENV sige, env)) = Incremental.type_basis context
val sigl = map SIG (NewMap.to_list_ordered sige)
val funl = map FUN (NewMap.to_list_ordered fune)
val envl = env2entry env
in
sigl @ funl @ envl
end
datatype BrowseOptions =
BROWSE_OPTIONS of
{show_sigs : bool ref,
show_funs : bool ref,
show_strs : bool ref,
show_types : bool ref,
show_exns : bool ref,
show_vars : bool ref,
show_conenvs : bool ref,
show_cons : bool ref
}
fun new_options () =
BROWSE_OPTIONS
{show_sigs = ref true,
show_funs = ref true,
show_strs = ref true,
show_types = ref true,
show_exns = ref true,
show_vars = ref true,
show_conenvs = ref true,
show_cons = ref false}
fun filter_entries options entries =
let
val BROWSE_OPTIONS
{show_vars,
show_cons,
show_exns,
show_types,
show_strs,
show_sigs,
show_funs,
...} = options
fun aux ([],acc) = rev acc
| aux (entry::entries,acc) =
let
val is_selected =
case entry
of VAR _ => !show_vars
| CONVAR _ => !show_cons
| CONENV _ => true
| EXN _ => !show_exns
| TYPE _ => !show_types
| STR _ => !show_strs
| SIG _ => !show_sigs
| FUN _ => !show_funs
| NSTR _ => !show_strs
| NSIG _ => !show_sigs
| NFUN _ => !show_funs
in
if is_selected then
aux (entries,entry :: acc)
else
aux (entries,acc)
end
in
aux (entries,[])
end
fun get_entry (identifier,context) =
let
val BasisTypes.BASIS (_,_, BasisTypes.FUNENV fe,
BasisTypes.SIGENV ge, env) =
Incremental.type_basis (context)
val (Datatypes.ENV
(Datatypes.SE se,
Datatypes.TE te,
Datatypes.VE (_, ve))) = env
in
case identifier of
Ident.VALUE i =>
NONE
| Ident.TYPE i =>
SOME
(TYPE (i, NewMap.apply' (te, i)))
| Ident.STRUCTURE i =>
SOME
(STR (i, NewMap.apply' (se, i)))
| Ident.SIGNATURE i =>
SOME
(SIG (i, NewMap.apply' (ge, i)))
| Ident.FUNCTOR i =>
SOME
(FUN (i, NewMap.apply' (fe, i)))
end
end
;
