require "../utils/print";
require "../utils/lists";
require "../utils/crash";
require "../basics/identprint";
require "../typechecker/basis";
require "../typechecker/types";
require "../typechecker/tyenv";
require "../typechecker/environment";
require "../typechecker/scheme";
require "../typechecker/valenv";
require "../typechecker/type_exp";
require "../typechecker/completion";
require "../typechecker/control_unify";
require "../typechecker/context_print";
require "../debugger/runtime_env";
require "../typechecker/patterns";
functor Patterns(
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Basis : BASIS
structure Tyenv : TYENV
structure Env : ENVIRONMENT
structure Scheme : SCHEME
structure Valenv : VALENV
structure Type_exp : TYPE_EXP
structure Completion : COMPLETION
structure Control_Unify : CONTROL_UNIFY
structure Context_Print : CONTEXT_PRINT
structure Print : PRINT
structure Lists : LISTS
structure Crash : CRASH
structure RuntimeEnv : RUNTIMEENV
sharing Control_Unify.Info = Type_exp.Info
sharing Control_Unify.BasisTypes = Basis.BasisTypes = Type_exp.BasisTypes
sharing Types.Datatypes = Scheme.Datatypes = Valenv.Datatypes =
Completion.Datatypes = Env.Datatypes = Tyenv.Datatypes = Basis.BasisTypes.Datatypes
sharing Types.Datatypes.Ident = IdentPrint.Ident
sharing Completion.Options = IdentPrint.Options = Types.Options = Control_Unify.Options
sharing Context_Print.Absyn = Type_exp.Absyn
sharing Completion.Options = Context_Print.Options
sharing type Type_exp.Absyn.RuntimeInfo = RuntimeEnv.RuntimeInfo
sharing type Types.Datatypes.Type = RuntimeEnv.Type
sharing type Types.Datatypes.Instance = Type_exp.Absyn.Instance = RuntimeEnv.Instance
) : PATTERNS =
struct
structure Absyn = Type_exp.Absyn
structure BasisTypes = Basis.BasisTypes
structure Info = Type_exp.Info
structure Options = Completion.Options
structure Datatypes = Types.Datatypes
open Datatypes
fun fresh_tyvar acontext =
METATYVAR (ref (Basis.context_level acontext, NULLTYPE,NO_INSTANCE), false, false)
fun in_scheme atype =
Scheme.make_scheme ([], atype)
local
infix &&
fun ve1 && ve2 = Valenv.ve_plus_ve (ve1, ve2)
fun singleVE (valid, ts) =
Valenv.add_to_ve (valid, ts, empty_valenv)
in
fun pat_context (Absyn.WILDpat _) = empty_valenv
| pat_context (Absyn.SCONpat _) = empty_valenv
| pat_context (Absyn.VALpat ((Ident.LONGVALID (_, valid), (ref ty,_)),_)) =
singleVE (valid, UNBOUND_SCHEME (ty,NONE))
| pat_context (Absyn.RECORDpat (fields, _, _)) =
Lists.reducel
(fn (res, (_, pat)) => pat_context pat && res)
(empty_valenv, fields)
| pat_context (Absyn.APPpat ((Ident.LONGVALID (_,valid),ref ty),pat,_,_)) =
singleVE (valid, UNBOUND_SCHEME (ty,NONE)) && pat_context pat
| pat_context (Absyn.TYPEDpat (pat, _,_)) = pat_context pat
| pat_context (Absyn.LAYEREDpat ((valid, (ref ty,_)), pat)) =
singleVE (valid, UNBOUND_SCHEME (ty,NONE)) && pat_context pat
end
fun near (opts, pat) =
Datatypes.Err_String
(concat ["\nNear: ", Context_Print.pat_to_string opts pat])
fun check_pat
(error_info,options as Options.OPTIONS{print_options,
compiler_options = Options.COMPILEROPTIONS{generate_moduler,...},...}) args =
let
val report_error = Info.error error_info
fun report_strid_error (location,print_options,strid,lvalid) =
report_error
(Info.RECOVERABLE,
location,
IdentPrint.valid_unbound_strid_message (strid,lvalid,print_options))
val check_type = Type_exp.check_type error_info
val unify = Control_Unify.unify (error_info,options)
fun check_pat (Absyn.WILDpat _, acontext) =
(empty_valenv, fresh_tyvar acontext, [])
| check_pat (Absyn.SCONpat (scon, type_ref), acontext) =
let
val ty = Types.type_of scon
in
type_ref := ty;
(empty_valenv, ty, [])
end
| check_pat (Absyn.VALpat ((Ident.LONGVALID (_, aval as (Ident.VAR _)),
(stuff as (type_ref,info_ref))),_),
acontext) =
let
val new_ty = fresh_tyvar acontext
val instance' = ref NO_INSTANCE
val instance = NONE
in
type_ref := new_ty;
info_ref := RuntimeEnv.RUNTIMEINFO (SOME instance',nil);
(Valenv.add_to_ve (aval, in_scheme (new_ty,instance), empty_valenv),
new_ty, [stuff])
end
| check_pat (Absyn.VALpat ((lvalid as Ident.LONGVALID (_,Ident.CON _), (type_ref,info_ref)),
location),
acontext) =
let fun error_return () =
let
val alpha = fresh_tyvar acontext
in
(type_ref := alpha;
(empty_valenv, alpha, []))
end
in
let
val atype =
#1(Basis.lookup_val (lvalid, acontext, location, generate_moduler))
in
if Types.cons_typep atype then
(type_ref := atype;
(empty_valenv, atype, []))
else
(report_error
(Info.RECOVERABLE, location,
concat ["Value constructor ",
IdentPrint.printLongValId print_options lvalid,
" used without argument in pattern"]);
error_return())
end
handle Basis.LookupValId valid =>
(report_error (Info.RECOVERABLE, location,
IdentPrint.unbound_longvalid_message (valid,lvalid,"constructor",print_options));
error_return ())
| Basis.LookupStrId strid =>
(report_strid_error (location,print_options,strid,lvalid);
error_return ())
end
| check_pat (Absyn.VALpat ((lvalid as Ident.LONGVALID (_,Ident.EXCON _), (type_ref,_)),location),
acontext) =
let
val atype =
#1(Basis.lookup_val (lvalid, acontext, location, generate_moduler))
handle Basis.LookupValId valid =>
(report_error
(Info.RECOVERABLE, location,
IdentPrint.unbound_longvalid_message (valid,lvalid,"exception",print_options));
Types.exn_type)
| Basis.LookupStrId strid =>
(report_strid_error (location,print_options,strid,lvalid);
Types.exn_type)
in
if Types.type_eq (atype, Types.exn_type, true, true)
then
(type_ref := atype;
(empty_valenv, atype, []))
else
(report_error
(Info.RECOVERABLE, location,
concat ["Exception constructor ",
IdentPrint.printLongValId print_options lvalid,
" used without argument in pattern"]);
type_ref := Types.exn_type;
(empty_valenv, Types.exn_type, []))
end
| check_pat (Absyn.RECORDpat (apatrowlist, flexp, type_ref), acontext) =
let
fun check_pat_row ([], acontext) =
(empty_valenv, Types.empty_rectype, [])
| check_pat_row ((alab, apat) :: patrowlist, acontext) =
let
val (ve1, atype1, pat_ty) = check_pat (apat, acontext)
val (ve2, atype2, pat_tys) = check_pat_row (patrowlist, acontext)
in
(Valenv.ve_plus_ve (ve1, ve2),
Types.add_to_rectype (alab, atype1, atype2),
pat_ty@pat_tys)
end
val (new_ve, arectype, pat_tys) = check_pat_row (apatrowlist, acontext)
in
(new_ve,
if flexp then
let
val metarectype =
METARECTYPE (ref (Basis.context_level acontext,
true, arectype, false, false))
in
type_ref := metarectype;
metarectype
end
else
(type_ref := arectype;
arectype),
pat_tys)
end
| check_pat (fun_arg as Absyn.APPpat ((lvalid,type_ref), apat,location,_), acontext) =
let
exception unsplit
val (new_ve, atype2, pat_ty2) = check_pat (apat, acontext)
in
let
fun split (FUNTYPE (arg, res)) = (arg, res)
| split (_) = raise unsplit
val (arg, res) =
split (#1(Basis.lookup_val (lvalid, acontext, location, generate_moduler)))
handle Basis.LookupValId valid =>
(report_error
(Info.RECOVERABLE, location,
IdentPrint.unbound_longvalid_message (valid,lvalid,"constructor",print_options));
(fresh_tyvar acontext, fresh_tyvar acontext))
| Basis.LookupStrId strid =>
(report_strid_error (location,print_options,strid,lvalid);
(fresh_tyvar acontext, fresh_tyvar acontext))
val result_type =
unify
{
first = arg, second = atype2, result = res,
context = acontext,
error = fn () =>
(location,
[Datatypes.Err_String "Constructor applied to argument of wrong type",
near (print_options, fun_arg),
Datatypes.Err_String "\n  Required argument type: ",
Datatypes.Err_Type arg,
Datatypes.Err_String "\n  Actual argument type:   ",
Datatypes.Err_Type atype2],
res)
}
in
type_ref := FUNTYPE (arg, result_type);
(new_ve, result_type, pat_ty2)
end
handle unsplit =>
(report_error
(Info.RECOVERABLE, location,
concat ["Nullary value constructor ",
IdentPrint.printLongValId print_options lvalid,
" applied to argument in pattern"]);
(new_ve, fresh_tyvar acontext, []))
end
| check_pat (arg as Absyn.TYPEDpat (apat, aty,location), acontext) =
let
val (new_ve, atype, pat_type) = check_pat (apat, acontext)
val ty_exp_ty = check_type (aty, acontext)
in
(new_ve,
(unify
{
first = atype, second = ty_exp_ty, result = atype,
context = acontext,
error = fn () =>
(location,
[Datatypes.Err_String "Types of pattern and constraint do not agree",
near (print_options,arg),
Datatypes.Err_String "\n  Pattern type:    ",
Datatypes.Err_Type atype,
Datatypes.Err_String "\n  Constraint type: ",
Datatypes.Err_Type ty_exp_ty],
ty_exp_ty)
}),
pat_type)
end
| check_pat (Absyn.LAYEREDpat ((avar, stuff as (type_ref,info_ref)), apat), acontext) =
let
val (new_ve, pat_type, pat_type') = check_pat (apat, acontext)
val instance' = ref(NO_INSTANCE)
val instance = NONE
in
type_ref := pat_type;
info_ref := RuntimeEnv.RUNTIMEINFO (SOME (instance'),nil);
(Valenv.add_to_ve (avar, in_scheme (pat_type,instance), new_ve),
pat_type,
pat_type'@[stuff])
end
| check_pat _ = Crash.impossible "TYCON':check_pat:patterns"
in
check_pat args
end
end
;
