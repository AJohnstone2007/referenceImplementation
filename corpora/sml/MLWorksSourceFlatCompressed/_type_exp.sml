require "../basis/__int";
require "../basics/absyn";
require "../main/info";
require "../basics/identprint";
require "../typechecker/types";
require "../typechecker/basis";
require "../typechecker/type_exp";
functor Type_exp(
structure IdentPrint: IDENTPRINT
structure Absyn : ABSYN
structure Types : TYPES
structure Basis : BASIS
structure Info : INFO
sharing Types.Datatypes = Basis.BasisTypes.Datatypes
sharing Absyn.Set = Basis.BasisTypes.Set
sharing Absyn.Ident.Location = Info.Location
sharing IdentPrint.Ident = Types.Datatypes.Ident = Absyn.Ident
sharing type Absyn.Type = Types.Datatypes.Type
sharing type Absyn.Structure = Types.Datatypes.Structure
) : TYPE_EXP =
struct
structure Datatypes = Types.Datatypes
structure Absyn = Absyn
structure BasisTypes = Basis.BasisTypes
structure Info = Info
open Datatypes
fun fresh_tyvar(acontext, eq, imp) =
METATYVAR (ref (Basis.context_level acontext,NULLTYPE,NO_INSTANCE), eq, imp)
fun check_type options args =
let
fun report_error args = Info.error options args
fun check_type (Absyn.TYVARty tyvar,context) =
(Basis.lookup_tyvar (tyvar,context)
handle Basis.LookupTyvar =>
TYVAR (ref (~1,NULLTYPE,NO_INSTANCE),tyvar))
| check_type (Absyn.RECORDty (alab_ty_list),acontext) =
let
fun tyrowlist ([],context) = Types.empty_rectype
| tyrowlist ((lab,ty)::rest,context) =
Types.add_to_rectype (lab,check_type (ty,context),
tyrowlist (rest,context))
in
tyrowlist (alab_ty_list,acontext)
end
| check_type (Absyn.APPty (tylist,ltycon,location),acontext) =
(let
val TYSTR (atyfun,x) =
Basis.lookup_longtycon (ltycon,acontext)
fun make_type_list [] = []
| make_type_list (h::t) =
check_type (h,acontext)::(make_type_list t)
in
if Types.arity (atyfun) = length (tylist) then
Types.apply (atyfun,make_type_list tylist)
else
(report_error
(Info.RECOVERABLE, location,
concat ["Wrong number of arguments to type constructor ",
IdentPrint.printLongTyCon ltycon,
": ",
Int.toString(Types.arity atyfun),
" required, ",
Int.toString(length tylist),
" supplied"]);
fresh_tyvar (acontext, false, false))
end
handle Basis.LookupTyCon tycon =>
(case location of
Info.Location.UNKNOWN => ()
| l =>
report_error
(Info.RECOVERABLE, l,
IdentPrint.unbound_longtycon_message (tycon,ltycon));
fresh_tyvar (acontext, false, false))
| Basis.LookupStrId strid =>
(case location of
Info.Location.UNKNOWN => ()
| l =>
report_error
(Info.RECOVERABLE, l,
IdentPrint.tycon_unbound_strid_message (strid,ltycon));
fresh_tyvar (acontext, false, false)))
| check_type (Absyn.FNty (ty,ty'),acontext) =
FUNTYPE (check_type (ty,acontext),check_type (ty',acontext))
in
check_type args
end
end;
