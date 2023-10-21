require "../utils/set";
require "../utils/lists";
require "../typechecker/types";
require "../debugger/runtime_env";
require "absyn";
functor Absyn (
structure Types : TYPES
structure Set : SET
structure Lists : LISTS
structure RuntimeEnv : RUNTIMEENV
sharing type RuntimeEnv.Type = Types.Datatypes.Type
) : ABSYN =
struct
structure Datatypes = Types.Datatypes
structure Set = Set
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure Location = Ident.Location
type Type = Datatypes.Type
type Tyfun = Datatypes.Tyfun
type Instance = Datatypes.Instance
type DebuggerStr = Datatypes.DebuggerStr
type Structure = Datatypes.Structure
type RuntimeInfo = RuntimeEnv.RuntimeInfo
type InstanceInfo = Datatypes.InstanceInfo
type options = Types.Options.options
val print_type = Types.print_type
val nullType = Datatypes.NULLTYPE
val nullTyfun = Datatypes.TYFUN(nullType,0)
val nullDebuggerStr = Datatypes.EMPTY_DSTR
val nullRuntimeInfo = RuntimeEnv.RUNTIMEINFO (NONE,nil)
val nullInstanceInfo = Datatypes.ZERO
datatype Exp =
SCONexp of Ident.SCon * Type ref |
VALexp of Ident.LongValId * Type ref * Ident.Location.T
* (InstanceInfo * Instance ref option) ref |
RECORDexp of (Ident.Lab * Exp) list|
LOCALexp of Dec * Exp * Location.T |
APPexp of Exp * Exp * Location.T * Datatypes.Type ref * bool |
TYPEDexp of Exp * Ty * Location.T |
HANDLEexp of Exp * Datatypes.Type ref * (Pat * Exp * Ident.Location.T) list * Location.T * string |
RAISEexp of Exp * Location.T |
FNexp of (Pat * Exp * Ident.Location.T) list * Type ref * string * Ident.Location.T |
DYNAMICexp of (Exp * Ident.TyVar Set.Set * (Type * int * Ident.TyVar Set.Set) ref) |
COERCEexp of (Exp * Ty * Type ref * Ident.Location.T) |
MLVALUEexp of MLWorks.Internal.Value.T
and Dec =
VALdec of (Pat * Exp * Location.T) list * (Pat * Exp * Location.T) list *
Ident.TyVar Set.Set * Ident.TyVar list |
TYPEdec of (Ident.TyVar list * Ident.TyCon * Ty * Tyfun ref option) list |
DATATYPEdec of Location.T *
(Ident.TyVar list * Ident.TyCon *
Datatypes.Type ref * Tyfun ref option *
((Ident.ValId * Datatypes.Type ref) *
Ty option) list) list |
DATATYPErepl of Ident.Location.T *
(Ident.TyCon * Ident.LongTyCon) *
Datatypes.Valenv option ref|
ABSTYPEdec of Location.T *
(Ident.TyVar list * Ident.TyCon *
Datatypes.Type ref * Tyfun ref option *
((Ident.ValId * Datatypes.Type ref) *
Ty option) list) list * Dec |
EXCEPTIONdec of ExBind list|
LOCALdec of Dec * Dec |
OPENdec of Ident.LongStrId list * Ident.Location.T |
SEQUENCEdec of Dec list
and ExBind =
NEWexbind of ((Ident.ValId * Datatypes.Type ref) * Ty option * Location.T * string) |
OLDexbind of ((Ident.ValId * Datatypes.Type ref) *
Ident.LongValId * Location.T * string)
and Pat =
WILDpat of Ident.Location.T |
SCONpat of Ident.SCon * Type ref |
VALpat of (Ident.LongValId * (Datatypes.Type ref * RuntimeInfo ref))
* Ident.Location.T |
RECORDpat of (Ident.Lab * Pat) list * bool * Datatypes.Type ref |
APPpat of (Ident.LongValId * Datatypes.Type ref) * Pat * Location.T * bool |
TYPEDpat of Pat * Ty * Location.T |
LAYEREDpat of (Ident.ValId * (Datatypes.Type ref * RuntimeInfo ref)) * Pat
and Ty =
TYVARty of Ident.TyVar |
RECORDty of (Ident.Lab * Ty) list |
APPty of Ty list * Ident.LongTyCon * Location.T |
FNty of Ty * Ty
datatype StrExp =
NEWstrexp of StrDec |
OLDstrexp of Ident.LongStrId * Ident.Location.T * Structure option ref option |
APPstrexp of Ident.FunId * StrExp * bool ref * Location.T * DebuggerStr ref option |
CONSTRAINTstrexp of StrExp * SigExp * bool * bool ref * Location.T |
LOCALstrexp of StrDec * StrExp
and StrDec =
DECstrdec of Dec |
STRUCTUREstrdec of
(Ident.StrId * (SigExp * bool) option * StrExp * bool ref
* Ident.Location.T * DebuggerStr ref option * Structure option ref option) list
|
ABSTRACTIONstrdec of
(Ident.StrId * (SigExp * bool) option * StrExp * bool ref
* Ident.Location.T * DebuggerStr ref option * Structure option ref option) list |
LOCALstrdec of StrDec * StrDec |
SEQUENCEstrdec of StrDec list
and SigExp =
NEWsigexp of Spec * Datatypes.Structure option ref |
OLDsigexp of Ident.SigId * Datatypes.Structure option ref * Location.T |
WHEREsigexp of (SigExp * (Ident.TyVar list * Ident.LongTyCon * Ty * Location.T) list)
and Spec =
VALspec of (Ident.ValId * Ty * Ident.TyVar Set.Set) list * Location.T |
TYPEspec of (Ident.TyVar list * Ident.TyCon) list |
EQTYPEspec of (Ident.TyVar list * Ident.TyCon) list |
DATATYPEspec of (Ident.TyVar list * Ident.TyCon *
(Ident.ValId * Ty option * Location.T) list) list |
DATATYPEreplSpec of Ident.Location.T * Ident.TyCon * Ident.LongTyCon *
(Ident.ValId * Type option * Location.T) list option ref |
EXCEPTIONspec of (Ident.ValId * Ty option * Location.T) list |
STRUCTUREspec of (Ident.StrId * SigExp) list |
SHARINGspec of Spec * (SharEq * Location.T) list |
LOCALspec of Spec * Spec |
OPENspec of Ident.LongStrId list * Location.T |
INCLUDEspec of SigExp * Location.T |
SEQUENCEspec of Spec list
and SharEq =
STRUCTUREshareq of Ident.LongStrId list |
TYPEshareq of Ident.LongTyCon list
datatype SigBind = SIGBIND of (Ident.SigId * SigExp * Location.T) list
datatype FunBind =
FUNBIND of (Ident.FunId * Ident.StrId * SigExp * StrExp * (SigExp * bool) option *
string * bool ref * Ident.Location.T * DebuggerStr ref option * Structure option ref option) list
datatype TopDec =
STRDECtopdec of StrDec * Location.T |
SIGNATUREtopdec of SigBind list * Location.T |
FUNCTORtopdec of FunBind list * Location.T |
REQUIREtopdec of string * Location.T
val empty_tyvarset = Set.empty_set
fun expansive_op (Ident.LONGVALID (Ident.NOPATH,Ident.CON s)) =
Symbol.symbol_name s = "ref"
| expansive_op (Ident.LONGVALID (_,Ident.CON s)) = false
| expansive_op (Ident.LONGVALID (_,Ident.EXCON s)) = false
| expansive_op _ = true
fun expansivep (SCONexp _) = false
| expansivep (VALexp _) = false
| expansivep (RECORDexp labexplist) =
Lists.exists (fn (lab,exp) => expansivep exp) labexplist
| expansivep (APPexp (VALexp (v,_,_,_),exp2,_,_,_)) =
expansive_op v orelse expansivep exp2
| expansivep (TYPEDexp (e,_,_)) = expansivep e
| expansivep (HANDLEexp (exp,_,_,_,_)) =
expansivep exp
| expansivep (FNexp _) = false
| expansivep _ = true
fun has_tyvar (TYVARty _) = true
| has_tyvar (RECORDty lab_tylist) =
let
fun collect ([]) = false
| collect ((_,ty)::lab_tylist) =
has_tyvar (ty) orelse collect (lab_tylist)
in
collect (lab_tylist)
end
| has_tyvar (APPty (tylist,_,_)) =
let
fun collect ([]) = false
| collect (ty::tylist) = has_tyvar (ty) orelse collect (tylist)
in
collect (tylist)
end
| has_tyvar (FNty (ty,ty')) = has_tyvar (ty) orelse has_tyvar (ty')
fun check_ty(ty, ty', loc) =
if Types.type_occurs(ty', ty) then
SOME loc
else
NONE
fun get_loc_from_pat(WILDpat loc) = SOME loc
| get_loc_from_pat(SCONpat _) = NONE
| get_loc_from_pat(VALpat(_, loc)) = SOME loc
| get_loc_from_pat(RECORDpat(lab_pat_list, _, _)) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, pat)) => get_loc_from_pat pat)
(NONE, lab_pat_list)
| get_loc_from_pat(APPpat(_, _, loc, _)) =
SOME loc
| get_loc_from_pat(TYPEDpat(_, _, loc)) =
SOME loc
| get_loc_from_pat(LAYEREDpat(_, pat)) =
get_loc_from_pat pat
fun check_pat_for_free_imp(WILDpat _, _, _) = NONE
| check_pat_for_free_imp(SCONpat _, _, _) = NONE
| check_pat_for_free_imp(VALpat((_, (ref ty', _)), loc), ty, _) =
check_ty(ty', ty, loc)
| check_pat_for_free_imp(RECORDpat(lab_pat_list, _, _), ty, loc') =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, pat)) => check_pat_for_free_imp(pat, ty, loc'))
(NONE, lab_pat_list)
| check_pat_for_free_imp(APPpat(_, pat, _, _), ty, loc) =
check_pat_for_free_imp(pat, ty, loc)
| check_pat_for_free_imp(TYPEDpat(pat, _, _), ty, loc) =
check_pat_for_free_imp(pat, ty, loc)
| check_pat_for_free_imp(LAYEREDpat((_, (ref ty', _)), pat), ty, loc) =
if Types.type_occurs(ty, ty') then
case get_loc_from_pat pat of
NONE => SOME loc
| x => x
else
NONE
fun check_exbind_for_free_imp(NEWexbind((_, ref ty'), _, loc, _), ty) =
check_ty(ty, ty', loc)
| check_exbind_for_free_imp(OLDexbind((_, ref ty'), _, loc, _), ty) =
check_ty(ty, ty', loc)
fun check_dec_for_free_imp(VALdec(dec_list1, dec_list2,_,_), ty) =
let
fun check_pat_exp(loc as SOME _, _) =
loc
| check_pat_exp(_, (pat, exp, loc)) =
case check_pat_for_free_imp(pat, ty, loc) of
NONE =>
check_exp_for_free_imp(exp, ty)
| x => x
in
case Lists.reducel
check_pat_exp
(NONE, dec_list1) of
NONE => Lists.reducel
check_pat_exp
(NONE, dec_list2)
| x => x
end
| check_dec_for_free_imp(TYPEdec _, _) = NONE
| check_dec_for_free_imp(DATATYPEdec _, _) = NONE
| check_dec_for_free_imp(DATATYPErepl _, _) = NONE
| check_dec_for_free_imp(ABSTYPEdec _, _) = NONE
| check_dec_for_free_imp(EXCEPTIONdec e_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, e_bind) => check_exbind_for_free_imp(e_bind, ty))
(NONE, e_list)
| check_dec_for_free_imp(LOCALdec(dec, dec'), ty) =
(case check_dec_for_free_imp(dec, ty) of
loc as SOME _ => loc
| _ => check_dec_for_free_imp(dec', ty))
| check_dec_for_free_imp(OPENdec _, _) = NONE
| check_dec_for_free_imp(SEQUENCEdec dec_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, dec) =>
check_dec_for_free_imp(dec, ty))
(NONE, dec_list)
and check_exp_for_free_imp(SCONexp _, ty) = NONE
| check_exp_for_free_imp(VALexp _, ty) = NONE
| check_exp_for_free_imp(RECORDexp lab_exp_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, exp)) =>
check_exp_for_free_imp(exp, ty))
(NONE, lab_exp_list)
| check_exp_for_free_imp(LOCALexp(dec, exp,_), ty) =
(case check_dec_for_free_imp(dec, ty) of
NONE =>
check_exp_for_free_imp(exp, ty)
| x => x)
| check_exp_for_free_imp(APPexp(exp1, exp2, _, _, _), ty) =
(case check_exp_for_free_imp(exp1, ty) of
NONE =>
check_exp_for_free_imp(exp2, ty)
| x => x)
| check_exp_for_free_imp(TYPEDexp(exp, _, _), ty) =
check_exp_for_free_imp(exp, ty)
| check_exp_for_free_imp(HANDLEexp(exp, _, pe_list, _, _), ty)=
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, exp, _)) =>
check_exp_for_free_imp(exp, ty))
(NONE, pe_list)
| check_exp_for_free_imp(RAISEexp(exp, _), ty) =
check_exp_for_free_imp(exp, ty)
| check_exp_for_free_imp(FNexp(pe_list, _, _, _), ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, exp, _)) =>
check_exp_for_free_imp(exp, ty))
(NONE, pe_list)
| check_exp_for_free_imp(DYNAMICexp(exp, _, _), ty) =
check_exp_for_free_imp(exp, ty)
| check_exp_for_free_imp(COERCEexp(exp, _, _, _), ty) =
check_exp_for_free_imp(exp, ty)
| check_exp_for_free_imp(MLVALUEexp _, ty) = NONE
fun check_strexp_for_free_imp(NEWstrexp strdec, ty) =
check_strdec_for_free_imp(strdec, ty)
| check_strexp_for_free_imp(OLDstrexp _, ty) =
NONE
| check_strexp_for_free_imp(APPstrexp _, ty) =
NONE
| check_strexp_for_free_imp(LOCALstrexp(strdec, strexp), ty) =
(case check_strdec_for_free_imp(strdec, ty) of
NONE =>
check_strexp_for_free_imp(strexp, ty)
| x => x)
| check_strexp_for_free_imp(CONSTRAINTstrexp (strexp,_,_,_,_),ty) =
check_strexp_for_free_imp (strexp,ty)
and check_strdec_for_free_imp(DECstrdec dec, ty) =
check_dec_for_free_imp(dec, ty)
| check_strdec_for_free_imp(STRUCTUREstrdec e_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, (_, _, strexp, _, _, _, _)) =>
check_strexp_for_free_imp(strexp, ty))
(NONE, e_list)
| check_strdec_for_free_imp(ABSTRACTIONstrdec abs_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, {3=strexp, ...}) => check_strexp_for_free_imp(strexp, ty))
(NONE, abs_list)
| check_strdec_for_free_imp(LOCALstrdec(strdec1, strdec2), ty) =
(case check_strdec_for_free_imp(strdec1, ty) of
NONE => check_strdec_for_free_imp(strdec2, ty)
| x => x)
| check_strdec_for_free_imp(SEQUENCEstrdec s_list, ty) =
Lists.reducel
(fn (loc as SOME _, _) => loc
| (_, strdec) => check_strdec_for_free_imp(strdec, ty))
(NONE, s_list)
end
;
