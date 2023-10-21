require "../utils/lists";
require "../basics/absyn";
require "../basics/identprint";
require "types";
require "type_debugger";
functor TypeDebugger (structure Lists : LISTS
structure Types : TYPES
structure Absyn : ABSYN
structure IdentPrint : IDENTPRINT
sharing Types.Options = IdentPrint.Options
sharing IdentPrint.Ident = Absyn.Ident
sharing type Absyn.Type = Types.Datatypes.Type
) : TYPE_DEBUGGER =
struct
structure Ident = Absyn.Ident
structure Location = Ident.Location
structure Options = IdentPrint.Options
structure Absyn = Absyn
fun gather_vartypes absyn_tree =
let
open Absyn
fun do_id ((Ident.LONGVALID (_,var as Ident.VAR _),ty,loc),acc) =
(var,ty,loc) :: acc
| do_id (_,acc) = acc
fun gather_pat (WILDpat _,acc) = acc
| gather_pat (SCONpat _, acc) = acc
| gather_pat (VALpat ((id,(ref ty,_)),loc), acc) =
do_id ((id,ty,loc),acc)
| gather_pat (RECORDpat (labpatlist,_,_),acc) =
Lists.reducel
(fn (acc,(lab,pat)) => gather_pat(pat,acc))
(acc,labpatlist)
| gather_pat (APPpat(id,pat,loc,_),acc) =
gather_pat (pat,acc)
| gather_pat (TYPEDpat (pat,ty,loc),acc) =
gather_pat (pat,acc)
| gather_pat (LAYEREDpat ((id,(ref ty,_)), pat), acc) =
gather_pat (pat,(id,ty,Location.UNKNOWN) :: acc)
fun gather_dec (VALdec (l1,l2,_,_),acc) =
let
fun do_one (acc,(pat,exp,_)) = gather_pat (pat,(gather_exp (exp,acc)))
in
Lists.reducel do_one (Lists.reducel do_one (acc,l1), l2)
end
| gather_dec (TYPEdec _,acc) = acc
| gather_dec (DATATYPEdec _,acc) = acc
| gather_dec (DATATYPErepl _,acc) = acc
| gather_dec (ABSTYPEdec (_,_,dec),acc) = gather_dec(dec,acc)
| gather_dec (EXCEPTIONdec _,acc) = acc
| gather_dec (LOCALdec(dec,dec'),acc) =
gather_dec (dec',gather_dec(dec,acc))
| gather_dec (OPENdec _, acc) = acc
| gather_dec (SEQUENCEdec declist,acc) =
Lists.reducel (fn (acc,dec) => gather_dec(dec,acc)) (acc,declist)
and gather_exp (SCONexp _,acc) = acc
| gather_exp (VALexp _,acc) = acc
| gather_exp (RECORDexp labexplist,acc) =
Lists.reducel (fn (acc,(lab,exp)) => gather_exp (exp,acc)) (acc,labexplist)
| gather_exp (LOCALexp (dec,exp,_),acc) =
gather_exp (exp, gather_dec (dec,acc))
| gather_exp (APPexp (exp,exp',_,_,_), acc) =
gather_exp (exp', gather_exp (exp,acc))
| gather_exp (TYPEDexp (exp,_,_),acc) = gather_exp (exp,acc)
| gather_exp (HANDLEexp (exp,_,patexplist,_,_),acc) =
Lists.reducel
(fn (acc,(pat,exp,_)) => gather_exp(exp,gather_pat(pat,acc)))
(gather_exp (exp,acc),patexplist)
| gather_exp (RAISEexp (exp,_),acc) =
gather_exp (exp,acc)
| gather_exp (FNexp (patexplist,_,_,_),acc) =
Lists.reducel
(fn (acc,(pat,exp,_)) => gather_exp(exp,gather_pat(pat,acc)))
(acc,patexplist)
| gather_exp (DYNAMICexp (exp,_,_),acc) =
gather_exp (exp,acc)
| gather_exp (COERCEexp (exp,_,_,_),acc) =
gather_exp (exp,acc)
| gather_exp (MLVALUEexp _,acc) = acc
fun gather_strexp (NEWstrexp strdec,acc) = gather_strdec (strdec,acc)
| gather_strexp (OLDstrexp _,acc) = acc
| gather_strexp (APPstrexp (_,strexp,_,_,_),acc) = gather_strexp (strexp,acc)
| gather_strexp (LOCALstrexp(strdec,strexp),acc) =
gather_strexp (strexp,gather_strdec (strdec,acc))
| gather_strexp (CONSTRAINTstrexp (strexp,sigexp,abs,_,_),acc) =
gather_strexp (strexp,acc)
and gather_strdec (DECstrdec dec,acc) = gather_dec (dec,acc)
| gather_strdec (STRUCTUREstrdec l,acc) =
Lists.reducel
(fn (acc,(_,_,strexp,_,_,_,_)) => gather_strexp (strexp,acc))
(acc,l)
| gather_strdec (ABSTRACTIONstrdec l,acc) =
Lists.reducel
(fn (acc,(_,_,strexp,_,_,_,_)) => gather_strexp (strexp,acc))
(acc,l)
| gather_strdec (LOCALstrdec (strdec,strdec'),acc) =
gather_strdec (strdec',(gather_strdec (strdec,acc)))
| gather_strdec (SEQUENCEstrdec l,acc) =
Lists.reducel (fn (acc,strdec) => gather_strdec(strdec,acc)) (acc,l)
fun gather_topdec (STRDECtopdec (strdec,_),acc) =
gather_strdec (strdec,acc)
| gather_topdec (FUNCTORtopdec (funbind_list,_),acc) =
Lists.reducel
(fn (acc,FUNBIND l) =>
(Lists.reducel
(fn (acc,(_,_,_,strexp,_,_,_,_,_,_)) =>
gather_strexp(strexp,acc))
(acc,l)))
(acc,funbind_list)
| gather_topdec (_,acc) = acc
in
rev (gather_topdec (absyn_tree,[]))
end
fun print_vartypes options l =
let
val Options.OPTIONS{print_options,...} = options
val print_id = IdentPrint.printValId print_options
val print_type = Types.print_type options
in
app
(fn (id,ty,loc) =>
print(concat[print_id id,
": ",
print_type ty,
" [", Location.to_string loc,"]",
"\n"]))
l
end
end
;
