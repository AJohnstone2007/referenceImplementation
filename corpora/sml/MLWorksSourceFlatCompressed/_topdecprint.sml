require "../basics/absynprint";
require "../basics/identprint";
require "topdecprint";
require "pretty";
functor TopdecPrint (
structure Pretty : PRETTY
structure AbsynPrint : ABSYNPRINT
structure IdentPrint : IDENTPRINT
sharing AbsynPrint.Absyn.Ident = IdentPrint.Ident
sharing AbsynPrint.Options = IdentPrint.Options
sharing type AbsynPrint.Absyn.options = IdentPrint.Options.options
sharing type AbsynPrint.Absyn.Type = AbsynPrint.Absyn.Datatypes.Type
) : TOPDECPRINT =
struct
structure Absyn = AbsynPrint.Absyn;
structure P = Pretty;
structure Options = IdentPrint.Options
structure Datatypes = Absyn.Datatypes
fun print_options (Options.OPTIONS{print_options=p,...}) = p
fun sigexp_to_prettyT options depth =
let
fun sigexp_to_T options (Absyn.OLDsigexp(sigid,_,_)) =
P.str (IdentPrint.printSigId sigid)
| sigexp_to_T options (Absyn.NEWsigexp(topspec, _)) =
if depth > 0
then P.blk(0,[P.str "sig",
P.blk(2,(fn []=>[] | x => P.nl :: x)
(specs_to_prettyT [topspec])),
P.nl,
P.str "end"])
else P.str "sig ... end"
| sigexp_to_T options (Absyn.WHEREsigexp (sigexp,wherestuff)) =
P.blk (0, [sigexp_to_T options sigexp,
P.str " where "] @
(where_to_T true (rev wherestuff)))
and where_to_T _ [] = []
| where_to_T is_first ((tyvars,tycon,ty,_)::rest) =
P.blk(if is_first then 0 else 10,
(if is_first then [P.str "type "]
else [P.nl,P.str " and "]) @
(plongtypename (tyvars,tycon)) @
[P.str " = ",
P.str (AbsynPrint.unparseTy
(print_options options) ty)])
:: (where_to_T false rest)
and valspec(valid,ty,tyvars) =
[P.str "val ",
P.str (IdentPrint.printValId (print_options options) valid),
P.str " :",
P.brk 1,
P.blk(4,[P.str (AbsynPrint.unparseTy (print_options options) ty)])]
and ptypename(tyvars, tycon) =
(case tyvars of
[] => []
| [tv] => [P.str (IdentPrint.printTyVar tv)]
| _ => (P.lst ("(",[P.str ",", P.brk 1],")")
(map (P.str o IdentPrint.printTyVar) tyvars))) @
[P.brk (if tyvars=[] then 0 else 1),
P.str (IdentPrint.printTyCon tycon)]
and plongtypename(tyvars, longtycon) =
(case tyvars of
[] => []
| [tv] => [P.str (IdentPrint.printTyVar tv)]
| _ => (P.lst ("(",[P.str ",", P.brk 1],")")
(map (P.str o IdentPrint.printTyVar) tyvars))) @
[P.brk (if tyvars=[] then 0 else 1),
P.str (IdentPrint.printLongTyCon longtycon)]
and typespec ts = (P.str "type ") :: (ptypename ts)
and eqtypespec ts = (P.str "eqtype ") :: (ptypename ts)
and datatypespec(tyvars,tycon,valtys) =
let
val tycon_str = ptypename (tyvars,tycon)
fun string_ty NONE = []
| string_ty (SOME ty) =
[P.blk(4, [P.str" of ",
P.str (AbsynPrint.unparseTy
(print_options options) ty)])]
fun valty (valid,tyopt,_) =
P.blk(2,
P.nl ::
P.str (IdentPrint.printValId
(print_options options) valid) ::
string_ty tyopt)
fun do_contypes[] = []
| do_contypes[x] = [valty x]
| do_contypes(x :: xs) =
P.blk(0, [valty x, P.str" |"]) :: do_contypes xs
in (P.str "datatype ") :: tycon_str
@ (P.str" =" :: do_contypes valtys)
end
and datareplspec (location,tycon,longtycon,associatedConstructors) =
let
val tycon_str = P.str (IdentPrint.printTyCon tycon)
val longtycon_str = P.str (IdentPrint.printLongTyCon longtycon)
fun string_ty (Datatypes.FUNTYPE(args,_)) =
Absyn.print_type options args
| string_ty ty = Absyn.print_type options ty
fun valty (valid,tyopt,_) =
P.blk(2,
P.nl ::
P.str (IdentPrint.printValId
(print_options options) valid) ::
(case tyopt
of NONE => []
| SOME ty => [P.blk(4, [P.str" of ",
P.str (string_ty ty)])]))
fun do_contypes [] = []
| do_contypes [x] = [valty x]
| do_contypes(x :: xs) =
P.blk(0, [valty x, P.str" |"]) :: do_contypes xs
exception noTyvars
fun tyvars ((_,SOME(Datatypes.FUNTYPE(_,res)),_)::_) =
P.str(Absyn.print_type options res)
| tyvars ((_,NONE,_)::rest) = tyvars rest
| tyvars _ = raise noTyvars
val default_str = case (!associatedConstructors)
of NONE => longtycon_str
| SOME v => (tyvars v
handle noTyvars => longtycon_str)
in
(P.str "datatype "):: [tycon_str] @
(P.str " = datatype ":: [default_str]) @
(P.str " = " ::
(do_contypes (case (!associatedConstructors)
of NONE => []
| (SOME v) => v)))
end
and exceptionspec(valid, typopt,_) =
case typopt of
NONE =>
[P.str "exception ",
P.str (IdentPrint.printValId (print_options options) valid)]
| SOME typ =>
[P.str "exception ",
P.str (IdentPrint.printValId (print_options options) valid),
P.str " of",
P.brk 1,
P.blk(0,[P.str(AbsynPrint.unparseTy
(print_options options) typ)])]
and structurespec(strid, sigexp) =
let val new_depth = depth - 1
in [P.str "structure ",
P.str (IdentPrint.printStrId strid),
P.str " :",
if new_depth > 0 then P.nl else P.brk 1,
P.blk(2, [sigexp_to_prettyT options new_depth sigexp])]
end
and sharingspec(Absyn.STRUCTUREshareq strids) =
(P.str "sharing ") ::
(P.lst ("", [P.brk 1, P.str "= "],"")
(map (P.str o IdentPrint.printLongStrId) strids))
| sharingspec(Absyn.TYPEshareq tycons) =
(P.str "sharing type ") ::
(P.lst ("", [P.brk 1, P.str "= "],"")
(map (P.str o IdentPrint.printLongTyCon) tycons))
and localspec(spec1,spec2) =
[P.blk (0, [P.str "local",
P.blk(2, [P.nl] @
(specs_to_prettyT [spec1])),
P.nl,
P.str "in",
P.blk (1, [P.nl] @
(specs_to_prettyT [spec2])),
P.nl,
P.str "end"])]
and openspec(strids) =
(P.str "open ") ::
(P.lst ("",[P.brk 1],"")
(map (P.str o IdentPrint.printLongStrId) strids))
and includespec(Absyn.WHEREsigexp
(Absyn.NEWsigexp(Absyn.TYPEspec [ts],_),
[(_,_,ty,_)])) =
P.str "type " :: (ptypename ts) @
[P.str " = ",
P.str (AbsynPrint.unparseTy (print_options options) ty)]
| includespec(Absyn.WHEREsigexp
(Absyn.NEWsigexp(Absyn.EQTYPEspec [ts],_),
[(_,_,ty,_)])) =
P.str "eqtype ":: (ptypename ts) @
[P.str " = ",
P.str (AbsynPrint.unparseTy (print_options options) ty)]
| includespec(sigexp) =
(P.str "include ") :: [sigexp_to_T options sigexp]
and specs_to_prettyT speclist =
let
fun addnls [] = []
| addnls ([]::ts) = addnls ts
| addnls [t] = [P.blk(0,t)]
| addnls (t::ts) = (P.blk(0,t)) :: (P.nl) :: (addnls ts)
fun specs_to_pTl [] = []
| specs_to_pTl (spec::rest) =
let
val lines = case spec of
(Absyn.VALspec (sl,_)) => map valspec sl
| (Absyn.TYPEspec sl) => map typespec sl
| (Absyn.EQTYPEspec sl) => map eqtypespec sl
| (Absyn.DATATYPEspec sl) => map datatypespec sl
| (Absyn.DATATYPEreplSpec s) => [datareplspec s]
| (Absyn.EXCEPTIONspec sl) => map exceptionspec sl
| (Absyn.STRUCTUREspec sl) => map structurespec sl
| (Absyn.SHARINGspec (spec,sl)) => specs_to_pTl [spec] @ map (sharingspec o #1) sl
| (Absyn.LOCALspec specpair) => [localspec specpair]
| (Absyn.OPENspec (strs,_)) => [openspec strs]
| (Absyn.INCLUDEspec (sigs,_)) => [includespec sigs]
| (Absyn.SEQUENCEspec sl) => [specs_to_prettyT sl]
in
lines @ (specs_to_pTl rest)
end
in
addnls (specs_to_pTl speclist)
end
in
sigexp_to_T options
end
fun sigexp_to_string options sigexp =
let val Options.PRINTOPTIONS{maximum_sig_depth,...} = print_options options
in P.string_of_T (sigexp_to_prettyT options maximum_sig_depth sigexp) end
fun print_sigexp options f (result, indent, sigexp) =
let val Options.PRINTOPTIONS{maximum_sig_depth,...} = print_options options
in P.reduce f (result, indent, sigexp_to_prettyT options maximum_sig_depth sigexp) end
fun strexp_to_string options strexp =
case strexp of
Absyn.NEWstrexp strdec => " struct " ^ strdec_to_string options strdec ^ " end"
| Absyn.OLDstrexp (longstrid,_,_) => IdentPrint.printLongStrId longstrid
| Absyn.APPstrexp(funid, strexp, _, _, _) => IdentPrint.printFunId funid ^ "(" ^
(strexp_to_string options strexp) ^ ")"
| Absyn.LOCALstrexp(strdec, strexp) => (strdec_to_string options strdec) ^
(strexp_to_string options strexp)
| Absyn.CONSTRAINTstrexp (strexp,sigexp,abs,_,_) =>
strexp_to_string options strexp ^
(if abs then " :> " else " : ") ^
sigexp_to_string options sigexp
and strdec_to_string options strdec = case strdec of
Absyn.DECstrdec ord_dec => AbsynPrint.printDec options ord_dec
| Absyn.STRUCTUREstrdec struc_dec_list =>
let
fun struc_dec_list_to_string [] = ""
| struc_dec_list_to_string((strid, sigexp_opt, strexp, _, _, _, _) :: tl) =
"(structure " ^ (IdentPrint.printStrId strid) ^ " : " ^
(case sigexp_opt of
NONE => ""
| SOME (sigexp,abs) => "\n" ^ sigexp_to_string options sigexp) ^ "\n = " ^
(strexp_to_string options strexp) ^ ")" ^ struc_dec_list_to_string(tl)
in
struc_dec_list_to_string struc_dec_list
end
| Absyn.ABSTRACTIONstrdec struc_dec_list =>
let
fun struc_dec_list_to_string [] = ""
| struc_dec_list_to_string((strid, sigexp_opt, strexp, _, _, _, _) :: tl) =
"(abstraction " ^ (IdentPrint.printStrId strid) ^ " : " ^
(case sigexp_opt of
NONE => ""
| SOME (sigexp,e) => "\n" ^ sigexp_to_string options sigexp) ^ "\n = " ^
(strexp_to_string options strexp) ^ ")" ^ struc_dec_list_to_string(tl)
in
struc_dec_list_to_string struc_dec_list
end
| Absyn.LOCALstrdec(strdec1, strdec2) =>
"LOCAL " ^ strdec_to_string options strdec1 ^ "IN " ^ strdec_to_string options (strdec2) ^
"END"
| Absyn.SEQUENCEstrdec strdec_list =>
let
fun strdec_list_to_string [] = ""
| strdec_list_to_string(hd :: tl) = strdec_to_string options (hd) ^
strdec_list_to_string tl
in
strdec_list_to_string strdec_list
end
fun topdec_to_string options (Absyn.STRDECtopdec (strdec,_)) =
strdec_to_string options strdec
| topdec_to_string options (Absyn.SIGNATUREtopdec (sigbind_list, _)) =
let
val Options.PRINTOPTIONS{maximum_sig_depth,...} = print_options options
fun print_sig_bind(Absyn.SIGBIND sigblist) =
let
fun doublelist (id,bind,_) =
P.string_of_T
(P.blk(0,[P.str "signature ",
P.str (IdentPrint.printSigId id),
P.str " = ",
P.nl,
P.blk(2, [sigexp_to_prettyT options maximum_sig_depth bind])]))
in
concat(map doublelist sigblist)
end
in
concat(map print_sig_bind sigbind_list)
end
| topdec_to_string options (Absyn.FUNCTORtopdec (funbind_list,_)) =
let
fun print_fun_list [] = "()"
| print_fun_list((Absyn.FUNBIND head) :: tail) =
let
fun print_funbind [] = ";"
| print_funbind
((funid, strid, sigexp, strexp, sig_opt, _, _, _, _, _) ::
rest) =
"functor " ^ IdentPrint.printFunId funid ^
"(" ^ IdentPrint.printStrId strid ^ ": sig)" ^
(case sig_opt of NONE => "" | _ => ": sig") ^ "=" ^
(strexp_to_string options strexp) ^
(print_funbind rest)
in
(print_funbind head) ^ (print_fun_list tail)
end
in
print_fun_list funbind_list
end
| topdec_to_string options (Absyn.REQUIREtopdec (x, _)) =
"require \"" ^ x ^ "\""
end
;
