require "ut1";
require "utils.sml";
require "universe.sml";
require "term.sml";
require "pretty.sml";
require "type.sml";
require "toc.sml";
require "unif.sml";
require "namespace.sml";
fun Consider nam cxt uneval eval =
let
val br = search nam cxt
in
if !MacroMode andalso ref_isDefn br
then let val VT as (_,T) = eval (uneval (ref_val br))
in (Ref(ref_updat_vt br VT),T)
end
else (Ref(br),coerceGe(ref_typ br))
end
fun ConsiderVar n t = (Var(n,t),coerceGe t)
fun ConsiderProp() = (Prop,mkTyp(uconst 0))
fun ConsiderType(s) =
let
fun typez i =
case theory()
of xtndCC => (mkTyp(i),mkTyp(uvar "" [UniGt i]))
| _ => bug"typez"
in typez (uvar s [])
end
fun ConsiderTypen(n) = (mkTyp(uconst n),mkTyp(uconst (n+1)));
fun letize (V,T) br =
let val ((_,vis),s,c,_) = ref_bd br
in (MkBind((Let,vis),s,c,subst2 br V),
coerceGe(MkBind((Let,vis),s,c,subst2 br T)))
end;
fun abstract (V,T) br =
let
val ((_,vis),s,c,t) = ref_bd br
fun abstr() = (MkBind((Lda,vis),s,c,subst2 br V),
MkBind((Pi,vis),s,c,subst2 br T))
fun fail1() = (prs("attempt to abstract "^s^" : "); prnt_vt_expand c t;
failwith"LF: only a Prop may be the domain of a function")
fun fail2() = (prs"attempt to abstract over ";prnt_vt_expand V (hnf T);
failwith"Pure CC: Type may not be the range of a function")
in case theory()
of pureCC => (case hnf T
of (Type _) => fail2()
| _ => abstr())
| xtndCC => abstr()
| lf => if t = Prop then abstr() else fail1()
end;
val gen_debug = ref false
fun generalize (V,T) br =
let
val _ = if !gen_debug then (prs("\n** gen debug ** "^ref_nam br^", ");
prnt_vt V T)
else()
val ((_,vis),s,c,t) = ref_bd br
val typ =
case (t,hnf T)
of (_,Prop) => Prop
| (Prop,Type(i)) => mkTyp(i)
| (Type(j),Type(i)) => mkTyp(uvar "" [UniGe(i),UniGe(j)])
| _ => (prs"attempt to generalize over "; prnt_vt_expand V T;
failwith "only a Prop or a Type may be the range of a product")
fun genlz() = (MkBind((Pi,vis),s,c,subst2 br V),typ)
fun failure() = (prs("Attempt to generalize "^s^" : ");
prnt_vt_expand c t;
failwith"LF: only a Prop may be the domain of a function")
in case theory() of xtndCC => genlz()
| pureCC => genlz()
| lf => if t = Prop then genlz() else failure()
end;
fun sigize (V,T) br =
let
val ((_,vis),s,c,t) = ref_bd br
val typ =
case (t,hnf T)
of (Prop,Prop) => mkTyp(uconst 0)
| (Prop,Ti as Type(i)) => Ti
| (Ti as Type(i),Prop) => Ti
| (Type(j),Type(i)) => mkTyp(uvar "" [UniGe(i),UniGe(j)])
| _ => failwith"the domain and range of SIGMA must be Props or Types"
fun sigz() = (MkBind((Sig,vis),s,c,subst2 br V),typ)
fun failure() = failwith"No SIGMA in current theory"
in case theory()
of xtndCC => sigz()
| pureCC => failure()
| lf => failure()
end;
fun dischCxt VT =
let
fun preDischCxt br =
case ref_bind br
of Let => letize VT br
| Lda => abstract VT br
| Pi => generalize VT br
| Sig => sigize VT br
in
fn b::bs => (preDischCxt b,b,bs)
| [] => failwith "cannot discharge; context empty"
end
fun dischCxtGbl VT = let val (vt,b,bs) = dischCxt VT (!NSP)
in (NSP:= bs; (vt,b))
end;
fun Apply sbst mkVar pv (VTf as (Vf,Tf)) (VTa as (Va,Ta)) =
let val Tf = hnf (sub sbst Tf)
in case (pv,Tf,VTa)
of (ShowNorm,Bind((Pi,Hid),nam,dom,rng),_) =>
let val var = mkVar dom
val newVf = App((Vf,[var]),[NoShow])
in Apply sbst mkVar ShowNorm (newVf,coerceGe (subst1 var rng)) VTa
end
| (ShowForce,Bind((Pi,Hid),nam,dom,rng),_) =>
Apply sbst mkVar ShowForce (Vf,Bind((Pi,Vis),nam,dom,rng)) VTa
| (NoShow,Bind((Pi,Hid),nam,dom,rng),_) =>
Apply sbst mkVar NoShow (Vf,Bind((Pi,Vis),nam,dom,rng)) VTa
| (pv,Bind((Pi,Vis),_,dom,_),(Bot,Bot)) =>
Apply sbst mkVar pv VTf (mkVar dom,dom)
| (pv,Bind((Pi,Vis),nam,dom,rng),_) =>
let
val (b,s) = type_match_unif sbst Ta dom
in
if b then ((MkApp((Vf,[Va]),[pv]),coerceGe (subst1 Va rng)),s)
else (prs"attempt to apply  "; print_expand Vf;
prs"with domain type  "; print_expand (dnf dom);
prs"to  "; prnt_vt_expand Va (dnf Ta);
failwith "type mismatch in application")
end
| (_,Bind((Pi,_),_,_,_),_) => bug"Apply; unknown Pi"
| _ =>
(prs"attempt to apply  "; prnt_vt_expand Vf (dnf Tf);
failwith "application of a non-function")
end;
fun Projection proj (V,T) =
case hnf T
of Bind((Sig,_),s,d,r)
=> let val XX = case proj
of Fst => d
| Snd => MkBind((Let,Def),s,MkProj(Fst,V),r)
| _ => failwith"general projection not yet implemented"
in (MkProj(proj,V),coerceGe XX) end
| _ => (prs"\nattempt to project\n  ";
prnt_vt_expand V (dnf (hnf T));
failwith"Projection: type of body not a SIG");
local
fun errRpt t T lr = (message"constructing tuple:";
legoprint t;
message"isn't a specialization of";
legoprint T;
failwith("tuple doesn't have purported type on "^lr))
fun chkTpl (T:cnstr) (vts:(cnstr*cnstr)list) sbst =
case (hnf T,vts)
of (Bind((Sig,_),_,tl,tr),(v,t)::(vts as _::_)) =>
let
val (b,sbst) = type_match_unif sbst t tl
in
if b then chkTpl (subst1 v tr) vts sbst
else errRpt t tl "left"
end
| (T,[(v,t)]) => let
val (b,sbst) = type_match_unif sbst t T
in
if b then sbst else errRpt t T "right"
end
| _ => failwith"tuple doesn't have a Sigma type"
in
fun tuplize sbst Bot (vts as _::_::_) =
let
fun mkT t T = Bind((Sig,VBot),"",t,T)
val T = foldr1 mkT (map snd vts) handle Empty _ => bug"tuplize"
val _ = type_of_constr T
in
tuplize sbst T vts
end
| tuplize sbst T (vts as _::_::_) =
let
val sbst = chkTpl T vts sbst
in
((Tuple(T,map fst vts),T),sbst)
end
| tuplize _ _ _ = bug"tuplizec"
end;
fun lclGen vt backto =
let
fun dch (vt as (v,t)) br = if (depends br v) orelse (depends br t)
then case ref_bind br
of Let => letize vt br
| Lda => abstract vt br
| _ => bug"funny Gen"
else vt
fun step vt =
fn br::rmndr => let val nvt = dch vt br
in if sameNam(br, backto) then nvt
else step nvt rmndr
end
| [] => failwith(backto^" undefined or out of scope")
in
step vt (!NSP)
end;
