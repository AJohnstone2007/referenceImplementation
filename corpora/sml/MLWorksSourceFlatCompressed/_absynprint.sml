require "../basis/__int";
require "../utils/sexpr";
require "../utils/lists";
require "../utils/set";
require "../typechecker/types";
require "identprint";
require "absynprint";
require "absyn";
functor AbsynPrint(
structure Sexpr : SEXPR
structure Lists : LISTS
structure Absyn : ABSYN
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Set : SET
sharing Types.Datatypes.Ident = IdentPrint.Ident = Absyn.Ident
sharing Types.Options = IdentPrint.Options
sharing Set = Absyn.Set
sharing type Absyn.Type = Types.Datatypes.Type
sharing type Absyn.Structure = Types.Datatypes.Structure
) : ABSYNPRINT =
struct
structure Absyn = Absyn
structure S = Sexpr
structure Set = Set
structure IP = IdentPrint
structure Ident = IP.Ident
structure Symbol = Ident.Symbol
structure Options = Types.Options
structure Location = Ident.Location
type Ty = Absyn.Ty and Pat = Absyn.Pat and Dec = Absyn.Dec and
Exp = Absyn.Exp
type 'a Sexpr = 'a Sexpr.Sexpr
fun detreeTy options ty =
case ty of
Absyn.TYVARty tyvar => S.ATOM (IP.printTyVar tyvar)
| Absyn.RECORDty tyrow =>
S.list (S.ATOM "RECORDty" :: detreeTyRow options tyrow)
| Absyn.APPty (tyseq, longtycon,_) =>
S.list (S.ATOM "APPty" :: S.ATOM (IP.printLongTyCon longtycon) ::
map (detreeTy options) tyseq)
| Absyn.FNty (ty,ty') =>
S.list [S.ATOM "FNty", detreeTy options ty,
detreeTy options ty']
and detreeTyRow options (tyrow) =
map (fn (lab,ty) => S.list [S.ATOM (IP.printLab lab),
detreeTy options ty]) tyrow
fun detreePat options pat =
let val Options.OPTIONS{print_options,...}=options
in
case pat of
Absyn.WILDpat _ => S.ATOM "_"
| Absyn.SCONpat (scon, _) => S.ATOM (IP.printSCon scon)
| Absyn.VALpat ((longvalid,(ref ty,_)),_) =>
S.list [S.ATOM (IP.printLongValId print_options longvalid),
S.ATOM (Types.print_type options ty)]
| Absyn.RECORDpat (patrow, false, ref ty) =>
S.list [S.list (S.ATOM "RECORDpat"
:: detreePatRow options patrow),
S.ATOM (Types.print_type options ty)]
| Absyn.RECORDpat (patrow, true, ref ty) =>
S.list [S.list (S.ATOM "FLEXRECORDpat" ::
detreePatRow options patrow),
S.ATOM (Types.print_type options ty)]
| Absyn.APPpat ((longvalid,_), pat,_,_) =>
S.list [S.ATOM (IP.printLongValId print_options longvalid),
detreePat options pat]
| Absyn.TYPEDpat (pat,ty,_) =>
S.list [detreePat options pat,
detreeTy options ty]
| Absyn.LAYEREDpat ((valid,(ref ty,_)), pat) =>
S.list [S.list [S.ATOM (IP.printValId print_options valid),
S.ATOM (Types.print_type
options ty)],
detreePat options pat]
end
and detreePatRow options (patrow) =
map (fn (lab,pat) => S.list [S.ATOM (IP.printLab lab), detreePat options pat])
patrow
fun detreeExp options exp =
let val Options.OPTIONS{print_options,...} = options
in
case exp of
Absyn.SCONexp (scon, _) => S.ATOM (IP.printSCon scon)
| Absyn.VALexp (longvalid,ref ty,location,_) =>
S.list [S.ATOM (IP.printLongValId print_options longvalid),
S.ATOM (Types.print_type options ty),
S.ATOM (Location.to_string location)]
| Absyn.RECORDexp exprow =>
S.list (S.ATOM "RECORDexp" ::
detreeExpRow options exprow)
| Absyn.LOCALexp (dec,exp,location) =>
S.list [S.ATOM "LOCALexp",
S.ATOM (Location.to_string location),
detreeDec options dec, detreeExp options exp]
| Absyn.APPexp (exp,exp',location,_,_) =>
S.list [S.ATOM "APPexp",
S.ATOM (Location.to_string location),
detreeExp options exp,
detreeExp options exp']
| Absyn.TYPEDexp (exp,ty,location) =>
S.list [S.ATOM "TYPEDexp",S.ATOM(Location.to_string location),
detreeExp options exp,
detreeTy options ty]
| Absyn.HANDLEexp (exp,_,match,_,annotation) =>
S.list (S.ATOM "HANDLEexp" :: S.ATOM (annotation) ::
detreeExp options exp ::
detreeMatch options match)
| Absyn.RAISEexp (exp,location) =>
S.list [S.ATOM "RAISEexp", S.ATOM(Location.to_string location) ,
detreeExp options exp]
| Absyn.FNexp (match,_,name,_) =>
S.list (S.ATOM "FNexp" :: S.ATOM name ::
detreeMatch options match)
| Absyn.DYNAMICexp(exp,_,_) =>
S.list [S.ATOM "DYNAMICexp",
detreeExp options exp]
| Absyn.COERCEexp(exp,ty,_,_) =>
S.list [S.ATOM "COERCEexp",
detreeExp options exp,
detreeTy options ty]
| Absyn.MLVALUEexp (mlvalue) => S.ATOM "MLVALUEexp"
end
and detreeExpRow options exprow =
map (fn (lab,exp) => S.list [S.ATOM (IP.printLab lab),
detreeExp options exp])
exprow
and detreeMatch options match =
map (fn (pat,exp,loc) => S.list
[detreePat options pat,
detreeExp options exp]) match
and detreeDec options dec =
case dec of
Absyn.VALdec (valbind,valbind',tyvars,_) =>
S.list[S.ATOM "VALdec",
S.list(detreeValBind options valbind),
S.list(detreeValBind options valbind'),
S.list (detreeTyVars (Set.set_to_list tyvars))]
| Absyn.TYPEdec typbind =>
S.list (S.ATOM "TYPEdec" ::
detreeTypBind options typbind)
| Absyn.DATATYPEdec datbind =>
S.list (S.ATOM "DATATYPEdec" ::
detreeDatBind options datbind)
| Absyn.DATATYPErepl (_,(tycon,longtycon),_) =>
S.list [S.ATOM "DATATYPErepl",
S.ATOM (IP.printTyCon tycon),
S.ATOM (IP.printLongTyCon longtycon)]
| Absyn.ABSTYPEdec (location,datbind,dec) =>
S.list [S.ATOM "ABSTYPEdec",
S.list (detreeDatBind options (location,datbind)),
detreeDec options dec]
| Absyn.EXCEPTIONdec (exbind) =>
S.list (S.ATOM "EXCEPTIONdec" ::
detreeExBind options exbind)
| Absyn.LOCALdec (dec,dec') =>
S.list [S.ATOM "LOCALdec",
detreeDec options dec,
detreeDec options dec']
| Absyn.OPENdec (longstrids) =>
S.list (S.ATOM "OPENdec" :: detreeLongStrIds longstrids)
| Absyn.SEQUENCEdec (decs) =>
S.list (S.ATOM "SEQUENCEdec" ::
map (detreeDec options) decs)
and detreeTyVars (tyvars) =
map (fn tyvar => S.ATOM (IP.printTyVar tyvar)) tyvars
and detreeLongStrIds (longstrids) =
map (fn longstrid => S.ATOM (IP.printLongStrId longstrid))
(#1 longstrids)
and detreeTypBind options (typbind) =
map (fn (tyvars,tycon,ty,_) =>
S.list [S.list (S.ATOM (IP.printTyCon tycon) ::
detreeTyVars tyvars), detreeTy options ty])
typbind
and detreeDatBind options (_,datbind) =
map (fn (tyvars,tycon,_,_,conbind) =>
S.list (S.list (S.ATOM (IP.printTyCon tycon) ::
detreeTyVars tyvars) ::
detreeConBind options conbind))
datbind
and detreeConBind options conbind =
let val Options.OPTIONS{print_options,...} = options
in
map (fn ((valid,ref ty),NONE) =>
S.list [S.ATOM (IP.printValId print_options valid),
S.ATOM (Types.print_type options ty)]
| ((valid,_),SOME ty) =>
S.list [S.ATOM (IP.printValId print_options valid),
detreeTy options ty])
conbind
end
and detreeExBind options exbind =
let val Options.OPTIONS{print_options,...} = options
in
map (fn Absyn.NEWexbind ((valid,ref ty),NONE,_,_) =>
S.list [S.ATOM (IP.printValId print_options valid),
S.ATOM (Types.print_type options ty)]
| Absyn.NEWexbind ((valid,ref ty),SOME ty',_,_) =>
S.list [S.list [S.ATOM (IP.printValId print_options valid),
S.ATOM (Types.print_type options ty)],
detreeTy options ty']
| Absyn.OLDexbind ((valid,ref ty),longvalid,_,_) =>
S.list [S.list [S.ATOM (IP.printValId print_options valid),
S.ATOM (Types.print_type options ty)],
S.ATOM (IP.printLongValId print_options longvalid)])
exbind
end
and detreeValBind options (valbind) =
map (fn (pat,exp,location) => S.list
[detreePat options pat,
S.ATOM(Location.to_string location),
detreeExp options exp]) valbind
val printSexpr = S.pprintSexpr (fn x => x)
fun printDec options x = printSexpr (detreeDec options x)
fun printExp options x = printSexpr (detreeExp options x)
fun printPat options x = printSexpr (detreePat options x)
fun printTy options x = printSexpr (detreeTy options x)
fun record_print labsep sep unparseLab unparseOther options all =
let
fun rec_f [] rest = rest
| rec_f [(lab, other)] rest =
rest ^ (unparseLab lab) ^ labsep ^ (unparseOther options other)
| rec_f ((lab, other)::more) rest =
rec_f more (rest ^ (unparseLab lab) ^
labsep ^ (unparseOther options other) ^ sep)
in
rec_f all ""
end
fun tuple_print _ unparseOther options [ one ] _ = unparseOther options one
| tuple_print sep unparseOther options all need_brackets =
let
fun internal [] rest = rest
| internal [ h ] rest =
rest ^ (unparseOther options h)
| internal (h::t) rest =
internal t (rest ^ (unparseOther options h) ^ sep)
val tuple = internal all ""
in
if need_brackets then "(" ^ tuple ^ ")"
else tuple
end
fun tuple_p all =
let
fun num_labs 0 _ rest = (true, rest)
| num_labs _ [] _ = (false, [])
| num_labs n all rest =
let
fun present a [] = (false, [])
| present a ((Ident.LAB h, the_val)::t) =
if (Symbol.eq_symbol (a, h)) then
(true, the_val::rest)
else
present a t
val (found, newlist) = present (Symbol.find_symbol (Int.toString n)) all
in
if found then
num_labs (n - 1) all newlist
else
(false, [])
end
in
num_labs (Lists.length all) all []
end
datatype NeedBrackets = NONE | FUNCTION | TUPLE
fun unparseTy' need_brackets options ty =
let
fun record all =
case tuple_p all of
(true, []) => "unit"
| (true, new) =>
tuple_print " * " (unparseTy' TUPLE) options new (need_brackets = TUPLE)
| (false, _) =>
"{" ^ (record_print ":" "," IP.printLab (unparseTy' NONE) options all) ^ "}"
in
case ty of
Absyn.TYVARty tyvar => IP.printTyVar tyvar
| Absyn.RECORDty tyrow => record tyrow
| Absyn.APPty ([],ltc,_) => (IP.printLongTyCon ltc)
| Absyn.APPty (tyseq, ltc,_) =>
((tuple_print "," (unparseTy' NONE) options tyseq true) ^
" " ^ (IP.printLongTyCon ltc))
| Absyn.FNty (ty, ty') =>
let val function =
(unparseTy' FUNCTION options ty) ^ " -> " ^ (unparseTy' NONE options ty')
in if need_brackets = TUPLE orelse need_brackets = FUNCTION then
"(" ^ function ^ ")"
else function
end
end
fun unparseTy options ty = unparseTy' NONE options ty
fun unparsePat print_loc =
let
fun find_record_loc [] = Location.UNKNOWN
| find_record_loc ((_, pat) :: rest) =
case pat_loc pat of
Location.UNKNOWN => find_record_loc rest
| loc => loc
and pat_loc(Absyn.WILDpat loc) = loc
| pat_loc(Absyn.SCONpat (scon, _)) =
(case scon of
Ident.INT(_, loc) => loc
| Ident.REAL(_, loc) => loc
| Ident.WORD(_, loc) => loc
| _ => Location.UNKNOWN)
| pat_loc(Absyn.VALpat((lvi, _), loc)) = loc
| pat_loc(Absyn.RECORDpat(pr, _, _)) = find_record_loc pr
| pat_loc(Absyn.APPpat(_, _, loc, _)) = loc
| pat_loc(Absyn.TYPEDpat(_, _, loc)) = loc
| pat_loc(Absyn.LAYEREDpat(_, pat)) = pat_loc pat
fun unparsePat' options pat =
let
fun record all =
let
val (is_tuple, new) = tuple_p all
in
if is_tuple then
tuple_print "," (unparsePat false) options new true
else
"{" ^ (record_print "=" "," IP.printLab (unparsePat false) options all) ^ "}"
end
fun flex_record all =
"{" ^ (record_print "=" "," IP.printLab (unparsePat false) options all ) ^ ",...}"
in
case pat of
Absyn.WILDpat loc =>
if print_loc then
Location.to_string loc ^ ": _"
else
"_"
| Absyn.SCONpat (scon, _) => IP.printSCon scon
| Absyn.VALpat ((lvi, _), loc) =>
(if print_loc then
Location.to_string loc ^ ": "
else
"") ^
IP.printLongValId options lvi
| Absyn.RECORDpat (pr, false, _) =>
(if print_loc then
Location.to_string(find_record_loc pr) ^ ": "
else
"") ^
record pr
| Absyn.RECORDpat (pr, true, _) =>
(if print_loc then
Location.to_string(find_record_loc pr) ^ ": "
else
"") ^
flex_record pr
| Absyn.APPpat ((lvi, _), pat,loc,_) =>
(((if print_loc then
Location.to_string loc ^ ": "
else
"")
^ IP.printLongValId options lvi) ^ " " ^
unparsePat false options pat)
| Absyn.TYPEDpat (pat, ty,_) =>
("(" ^ (unparsePat print_loc options pat) ^ " : " ^
(unparseTy options ty) ^ ")")
| Absyn.LAYEREDpat ((vi, _), pat) =>
((IP.printValId options vi) ^ " as " ^
(unparsePat print_loc options pat))
end
in
unparsePat'
end
fun unparseExp options exp =
let
fun record all =
let
val (is_tuple, new) = tuple_p all
in
if (is_tuple) then
tuple_print "," unparseExp options new true
else
"{" ^ (record_print "=" "," IP.printLab unparseExp options all ) ^ "}"
end
fun fn_body [] rest = rest
| fn_body [ (pat, exp,_) ] rest =
rest ^ (unparsePat false options pat) ^ " => " ^ (unparseExp options exp)
| fn_body ((pat, exp,_)::ll) rest =
fn_body ll (rest ^ (unparsePat false options pat) ^ " => " ^
(unparseExp options exp) ^ " | ")
in
case exp of
Absyn.SCONexp (scon, _) => IP.printSCon scon
| Absyn.VALexp (lvi, _,_,_) => IP.printLongValId options lvi
| Absyn.RECORDexp pr => record pr
| Absyn.LOCALexp (dec, e,_) => "let ... in " ^ (unparseExp options e) ^ " end"
| Absyn.APPexp (e1, e2,_,_,_) => "(" ^ (unparseExp options e1) ^ " " ^ (unparseExp options e2) ^ ")"
| Absyn.TYPEDexp (e, ty,_) => (unparseExp options e) ^ ":" ^ (unparseTy options ty)
| Absyn.HANDLEexp _ => "...handle..."
| Absyn.RAISEexp _ => "...raise..."
| Absyn.FNexp (all,_,_,_) => fn_body all "fn "
| Absyn.DYNAMICexp _ => "Dynamic ..."
| Absyn.COERCEexp _ => "Coerce ..."
| Absyn.MLVALUEexp _ => "MLValue"
end
end
;
