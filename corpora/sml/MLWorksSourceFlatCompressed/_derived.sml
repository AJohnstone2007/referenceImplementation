require "../basis/__int";
require "../utils/lists";
require "../utils/counter";
require "../utils/crash";
require "../basics/absyn";
require "../basics/identprint";
require "../main/info";
require "parserenv";
require "derived";
functor Derived(
structure Lists : LISTS
structure Counter : COUNTER
structure Crash : CRASH
structure Absyn : ABSYN
structure Info : INFO
structure PE : PARSERENV
structure IdentPrint : IDENTPRINT
sharing PE.Ident = Absyn.Ident = IdentPrint.Ident
sharing Absyn.Ident.Location = Info.Location
) : DERIVED =
struct
structure Absyn = Absyn
structure Ident = Absyn.Ident
structure Symbol = Ident.Symbol
structure Set = Absyn.Set
structure Info = Info
structure PE = PE
fun annotate thing = (thing, ref Absyn.nullType)
fun annotate_exp (thing,location) =
(thing, ref Absyn.nullType, location,ref(Absyn.nullInstanceInfo,NONE))
fun annotate_pat thing = (thing, (ref Absyn.nullType,ref (Absyn.nullRuntimeInfo)))
fun new_var () =
let
val s = "_id" ^ Int.toString (Counter.counter ())
in
Ident.LONGVALID (Ident.mkPath [],Ident.VAR (Symbol.find_symbol s))
end
fun new_vars 0 = nil
| new_vars n = new_var () :: new_vars (n - 1)
val small_labs =
MLWorks.Internal.Array.tabulate (16,
fn n => Ident.LAB (Symbol.find_symbol (Int.toString n)))
val num_small_labs = MLWorks.Internal.Array.length small_labs
fun labn n =
if n >= 0 andalso n < num_small_labs
then MLWorks.Internal.Array.sub (small_labs,n)
else Ident.LAB (Symbol.find_symbol (Int.toString n))
local
fun lookup s =
let val sym = Symbol.find_symbol s
in
(fn pE =>
(case PE.tryLookupValId (([],sym),pE) of
SOME x => x
| _ => Ident.VAR sym))
end
in
val NILid = lookup "nil"
val CONSid = lookup "::"
val TRUEid = lookup "true"
val FALSEid = lookup "false"
val itid = lookup"it"
end
local
fun make_exp idfn (location,pE) =
Absyn.VALexp (annotate_exp (Ident.LONGVALID (Ident.mkPath [], idfn pE),location))
in
val NILexp = make_exp NILid
val CONSexp = make_exp CONSid
val TRUEexp = make_exp TRUEid
val FALSEexp = make_exp FALSEid
end
local
fun make_pat(x,location) =
Absyn.VALpat (annotate_pat (Ident.LONGVALID (Ident.mkPath [], x)),location)
in
fun NILpat(location,pE) = make_pat (NILid pE,location)
fun TRUEpat (location,pE) = make_pat (TRUEid pE,location)
fun FALSEpat (location,pE) = make_pat (FALSEid pE,location)
fun itpat (location,pE) = make_pat (itid pE,location)
end
fun make_tuple l =
let
fun make' ([],_,r) = rev r
| make' (a::l,n,r) = make'(l, n+1, (labn n, a)::r)
in
make'(l,1,[])
end
fun make_tuple_ty tys = Absyn.RECORDty (make_tuple tys)
fun make_unit_pat () = Absyn.RECORDpat ([],false,ref Absyn.nullType)
fun make_tuple_pat [pat] = pat
| make_tuple_pat pats =
Absyn.RECORDpat (make_tuple pats, false, ref Absyn.nullType)
fun make_list_pat (l,location,pE) =
let
val init_val = NILpat(location,pE)
val annotation =
annotate (Ident.LONGVALID (Ident.mkPath [], CONSid pE))
fun mk_list_pat ([],res) = res
| mk_list_pat (pat::pats,res) =
let
val new_val =
Absyn.APPpat(annotation, make_tuple_pat[pat,res], location, true)
in
mk_list_pat(pats,new_val)
end
in
mk_list_pat(rev l,init_val)
end
fun make_patrow (sym, opt_ty, opt_pat,location) =
let
val lab = Ident.LAB sym
val var = Ident.VAR sym
val valpat =
Absyn.VALpat (annotate_pat (Ident.LONGVALID (Ident.mkPath [], var)),location)
in
case (opt_ty, opt_pat) of
(NONE, NONE) => (lab,valpat)
| (NONE, SOME pat) =>
(lab, Absyn.LAYEREDpat (annotate_pat var, pat))
| (SOME ty, NONE) =>
(lab, Absyn.TYPEDpat (valpat, ty,location))
| (SOME ty, SOME pat) =>
(lab, Absyn.TYPEDpat (Absyn.LAYEREDpat (annotate_pat var, pat), ty, location))
end
fun make_unit_exp () = Absyn.RECORDexp []
fun make_tuple_exp [exp] = exp
| make_tuple_exp exps = Absyn.RECORDexp (make_tuple exps)
fun make_select (lab as Ident.LAB sym,location,annotation) =
let
val var = Ident.LONGVALID (Ident.mkPath [],Ident.VAR sym)
val (a,b) =
(annotate
[(Absyn.RECORDpat ([(lab,Absyn.VALpat (annotate_pat var,location))],
true,ref Absyn.nullType),
Absyn.VALexp (annotate_exp (var,location)),
location)])
in
Absyn.FNexp (a,b,annotation,location)
end
fun make_case (exp,match,annotation,location) =
let val (a,b) = (annotate match)
in
Absyn.APPexp (Absyn.FNexp(a,b,annotation,location) , exp,
location,ref Absyn.nullType,false)
end
fun make_case' (exp,match,annotation,location) =
let val (a,b) = (annotate match)
in
Absyn.APPexp (Absyn.FNexp(a,b,annotation,location) , exp,
location,ref(Absyn.nullType),false)
end
fun make_if (exp1,exp2,exp3,annotation,location,pE) =
make_case (exp1,[(TRUEpat(location,pE),exp2,location),
(FALSEpat(location,pE),exp3,location)],
annotation,
location)
fun make_orelse (exp1,exp2,annotation,location,pE) =
make_if (exp1,TRUEexp(location,pE),exp2,annotation,location,pE)
fun make_andalso (exp1,exp2,annotation,location,pE) =
make_case
(exp1,[(FALSEpat(location,pE), FALSEexp(location,pE), location),
(TRUEpat(location,pE),exp2,location)],
annotation, location)
local
fun mk_sequence_exp ([],res) = res
| mk_sequence_exp ((exp,annotation,location)::exps,res) =
let
val new_val =
make_case (exp,[(Absyn.WILDpat location,res,location)],
"Sequence expression",location)
in
mk_sequence_exp(exps,new_val)
end
in
fun make_sequence_exp ([(exp,_,_)]) = exp
| make_sequence_exp (exps as (_ :: _)) =
let
val rev_exps = rev exps
val (exp,_,_) = Lists.hd rev_exps
val rest = Lists.tl rev_exps
in
mk_sequence_exp(rest,exp)
end
| make_sequence_exp ([]) = Crash.impossible"make_sequence nil"
end
fun make_while (exp1,exp2,annotation_function,location,pE) =
let
val var = new_var ()
val (a,b) =
(annotate
[(make_unit_pat (),
make_if (exp1,
make_sequence_exp [(exp2,"in make_while",location),
(Absyn.APPexp (Absyn.VALexp
(annotate_exp(var,location)),
make_unit_exp (),
location,
ref Absyn.nullType,
false),
"in make_while",location)],
make_unit_exp (),
"While statement",
location,
pE),
location)])
in
Absyn.LOCALexp
(Absyn.VALdec
([],
[(Absyn.VALpat (annotate_pat var,location),
Absyn.FNexp (a,b,annotation_function "while statement",location),location)],
Set.empty_set,[]),
Absyn.APPexp (Absyn.VALexp (annotate_exp(var,location)),
make_unit_exp (),location,
ref(Absyn.nullType),
false),
location)
end
fun make_list_exp (exps,location,pE) =
let
val cons = CONSexp (location,pE)
fun make_list_exp' (acc,[]) = acc
| make_list_exp' (acc,h::t) =
make_list_exp' (Absyn.APPexp(cons,
make_tuple_exp [h,acc],
location, ref Absyn.nullType, true),
t)
in
make_list_exp' (NILexp (location,pE), rev exps)
end
exception FvalBind of string
fun make_fvalbind ((clauses as ((var,patlist,_,_) :: _),info_generator,location),
options) =
let
fun name_to_string(Ident.VAR x) = Ident.Symbol.symbol_name x
| name_to_string(Ident.CON x) = Ident.Symbol.symbol_name x
| name_to_string(Ident.EXCON x) = Ident.Symbol.symbol_name x
| name_to_string(Ident.TYCON' _) = Crash.impossible "name_to_string:make_fvalbind:derived"
val name_string = name_to_string var
val patlength = Lists.length patlist
val vars = new_vars patlength
fun make_line (var',patlist',exp,loc) =
(if var = var' then
if Lists.length patlist' = patlength then
()
else Info.error options(Info.RECOVERABLE, loc, "Different pattern lengths in clauses")
else Info.error options (Info.RECOVERABLE, loc, "Inconsistent function names");
(make_tuple_pat patlist',exp,loc))
fun wrap (nil,body,_,_) = body
| wrap (var::vars,body,num,location) =
let val (a,b) =
(annotate
[(Absyn.VALpat (annotate_pat var,location), wrap (vars,body,num+1,location),location)])
in
Absyn.FNexp (a,b,
info_generator(name_string ^ (" argument " ^ Int.toString num)),
location)
end
val (patexplist,ty) = (annotate (map make_line clauses))
in
if patlength = 1
then
let
val body = Absyn.FNexp(patexplist,ty,info_generator name_string,location)
in
[(Absyn.VALpat (annotate_pat (Ident.LONGVALID (Ident.mkPath [], var)),location),body,location)]
end
else
let
val funid = Ident.LONGVALID (Ident.mkPath [], var)
val tuple_exp = make_tuple_exp (map (fn var => Absyn.VALexp
(annotate_exp(var,location))) vars)
val body = Absyn.FNexp(patexplist,ty,info_generator (name_string^"<Uncurried>"),location)
in
[(Absyn.VALpat (annotate_pat funid,location),
wrap (vars,
Absyn.APPexp (body,
tuple_exp,
location,
ref Absyn.nullType,
false),
0,
location),
location)]
end
end
| make_fvalbind _ = Crash.impossible"make_fvalbind bad parameters"
fun make_it_strdec (e,tyvars,location,pE) =
Absyn.STRDECtopdec
(Absyn.DECstrdec
(Absyn.VALdec ([(itpat (location,pE),
e,
location)],
nil,
tyvars,[])),
location)
fun make_fun (matches,tyvarset,explicitys,location) =
let
val no_subfns =
Lists.forall (fn [_] => true | _ => false) matches
in
if no_subfns
then
Absyn.VALdec ([],
map (fn [triple] => triple
| _ => Crash.impossible "Bad singleton list in make_fun")
matches,
tyvarset,
explicitys)
else
let
val match = Lists.reducer (op @) (matches,[])
val external_pats =
map (fn ((x,_,_)::_) =>
(case x of
Absyn.VALpat((lvi,_),loc) =>
Absyn.VALpat((lvi,(ref Absyn.nullType,ref Absyn.nullRuntimeInfo)),loc)
| _ => x)
| _ => Crash.impossible "Bad list in make_fun") matches
val external_exps = map (fn (Absyn.VALpat ((valid,_),location)) => Absyn.VALexp (annotate_exp(valid,location))
| _ => Crash.impossible "Bad pattern variables in make_fun")
external_pats
val external_decs = Absyn.VALdec (map (fn (pat,exp) => (pat,exp,location))
(Lists.zip (external_pats,external_exps)),
[],
Set.empty_set,
explicitys)
in
Absyn.LOCALdec (Absyn.VALdec ([],match, tyvarset,[]),
external_decs)
end
end
fun substitute_datbind (datbind,typbind,opts) =
let
fun subst_ty (ty as (Absyn.TYVARty _)) = ty
| subst_ty (Absyn.RECORDty tyrow) =
Absyn.RECORDty (map (fn (lab,ty) => (lab, subst_ty ty)) tyrow)
| subst_ty (Absyn.FNty (ty,ty')) =
Absyn.FNty (subst_ty ty, subst_ty ty')
| subst_ty (ty as (Absyn.APPty (tys,tycon,location))) =
let
fun subst_appty nil = Absyn.APPty(map subst_ty tys,tycon,location)
| subst_appty ((tyvars',tycon',ty',_)::rest) =
if tycon = Ident.LONGTYCON (Ident.mkPath [],tycon') then
(if Lists.length tys = Lists.length tyvars' then ()
else
Info.error opts
(Info.RECOVERABLE,
location,
"Wrong number of arguments to type constructor " ^
IdentPrint.printLongTyCon tycon);
let
fun subst_tyvars (Absyn.RECORDty tyrow) =
Absyn.RECORDty
(map (fn (lab,ty) => (lab, subst_tyvars ty)) tyrow)
| subst_tyvars (Absyn.FNty (ty,ty')) =
Absyn.FNty (subst_tyvars ty, subst_tyvars ty')
| subst_tyvars (Absyn.APPty (tys,tycon,location)) =
Absyn.APPty (map subst_tyvars tys, tycon,
Ident.Location.UNKNOWN)
| subst_tyvars (ty as (Absyn.TYVARty tyvar)) =
let
fun subst_tyvar (nil,nil) = ty
| subst_tyvar (tyvar'::tyvars,ty'::tys) =
if tyvar = tyvar' then
ty'
else
subst_tyvar (tyvars,tys)
| subst_tyvar _ = ty
in
subst_tyvar (tyvars',tys)
end
in
subst_tyvars ty'
end)
else
subst_appty rest
in
subst_appty typbind
end
fun subst_conbind nil = nil
| subst_conbind ((binding as (_,NONE)) :: rest) =
binding :: subst_conbind rest
| subst_conbind ((con,SOME ty) :: rest) =
(con,SOME (subst_ty ty)) :: subst_conbind rest
fun subst_binding (tyvars,tycon,tyref,tyfunref,conbind) =
(tyvars,tycon,tyref,tyfunref,subst_conbind conbind)
in
map subst_binding datbind
end
fun make_datatype_withtype (location,datbind,typbind,opts) =
Absyn.SEQUENCEdec
[Absyn.DATATYPEdec (location,substitute_datbind (datbind,typbind,opts)),
Absyn.TYPEdec typbind]
fun make_abstype_withtype (location,datbind,typbind,dec,opts) =
Absyn.ABSTYPEdec
(location,substitute_datbind (datbind,typbind,opts),
Absyn.SEQUENCEdec [Absyn.TYPEdec typbind, dec])
fun make_strexp (strdec) = Absyn.NEWstrexp strdec
val dummy_strid = Ident.STRID (Symbol.find_symbol "_")
val dummy_longstrid = Ident.LONGSTRID (Ident.mkPath [], dummy_strid)
fun make_funbind (funid,sigexp,strexp,sigexp',location) =
let
val sigexp'' =
case sigexp' of
NONE => sigexp'
| SOME (e as Absyn.WHEREsigexp sigexp,abs) => Crash.impossible "Meep!"
| SOME (e as Absyn.OLDsigexp _,abs) => sigexp'
| SOME (Absyn.NEWsigexp (spec',int),abs) =>
SOME
(Absyn.NEWsigexp
(Absyn.LOCALspec (Absyn.OPENspec ([dummy_longstrid],location), spec'),int),
abs)
val strexp' =
Absyn.LOCALstrexp
(Absyn.DECstrdec (Absyn.OPENdec ([dummy_longstrid],location)), strexp)
in
(funid,dummy_strid,sigexp,strexp',sigexp'')
end
end
;
