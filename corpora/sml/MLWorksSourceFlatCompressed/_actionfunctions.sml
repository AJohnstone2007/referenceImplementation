require "^.basis.__string";
require "../utils/lists";
require "../utils/set";
require "../basics/identprint";
require "../basics/token";
require "../parser/derived";
require "../utils/crash";
require "LRbasics";
require "actionfunctions";
functor ActionFunctions (
structure LRbasics : LRBASICS
structure Derived : DERIVED
structure Lists : LISTS
structure IdentPrint : IDENTPRINT
structure Token : TOKEN
structure Crash : CRASH
structure Set : SET
sharing Set = Derived.Absyn.Set
sharing Derived.Absyn.Ident.Symbol = Token.Symbol
sharing Derived.PE.Ident = IdentPrint.Ident
) : ACTIONFUNCTIONS =
struct
structure Token = Token
structure Absyn = Derived.Absyn
structure LRbasics = LRbasics
structure PE = Derived.PE
structure Info = Derived.Info
structure Options = IdentPrint.Options
structure Map = PE.Map
structure Location = Info.Location
structure Absyn = Derived.Absyn
structure Ident = Absyn.Ident
structure Location = Ident.Location
structure Symbol = Ident.Symbol
type ParserBasis = PE.pB;
local
type TyVarSet = Ident.TyVar Set.Set
type TyVarList = Ident.TyVar list
type PatExp = Absyn.Pat * Absyn.Exp * Location.T
type PatExpMark = Absyn.Pat * Absyn.Exp * Location.T
type TypeRef = Absyn.Type ref
type ConBind = ((Ident.ValId * TypeRef) * Absyn.Ty option)
type FVal = Ident.ValId * Absyn.Pat list * Absyn.Exp * Location.T
type LabExp = Ident.Lab * Absyn.Exp
type LabPat = Ident.Lab * Absyn.Pat
type ConType = Ident.ValId * Absyn.Ty option * Location.T
type ExType = Ident.ValId * Absyn.Ty option * Location.T
type FunBind = (Ident.FunId * Ident.StrId * Absyn.SigExp * Absyn.StrExp * (Absyn.SigExp * bool) option * string * bool ref * Location.T * Absyn.DebuggerStr ref option * Absyn.Structure option ref option)
type DatBind = TyVarList * Ident.TyCon * TypeRef * Absyn.Tyfun ref option * ConBind list
in
datatype Parsed_Object =
ENV of PE.pE
| LONGID of Symbol.Symbol list * Symbol.Symbol
| LONGVALID of Ident.LongValId * string option
| SYM of Symbol.Symbol
| FUNID of Ident.FunId
| STRID of Ident.StrId
| SIGID of Ident.SigId
| LONGSTRID of Ident.LongStrId
| LONGTYCON of Ident.LongTyCon
| LAB of Ident.Lab
| SCON of Ident.SCon
| TYVAR of Ident.TyVar
| VALID of Ident.ValId
| TYCON of Ident.TyCon
| INTEGER of string
| REAL of string
| STRING of string
| CHAR of string
| WORD of string
| BOOL of bool
| EXP of Absyn.Exp * TyVarSet
| DEC of Absyn.Dec * PE.pE * TyVarSet
| CONBIND of ConBind list * PE.pVE * TyVarSet
| CONBIND1 of ConBind * Ident.ValId * TyVarSet
| DATAHDR of TyVarList * Ident.TyCon
| DATREPL of TyVarList * Ident.TyCon * Ident.LongTyCon
| DATBIND of DatBind list * PE.pVE * PE.pTE
| DATBIND1 of DatBind * PE.pVE * PE.pTE
| PAT of Absyn.Pat * PE.pVE * TyVarSet
| BINPAT of Absyn.Pat * Ident.ValId * Absyn.Pat * PE.pVE * TyVarSet
| TY of Absyn.Ty * TyVarSet
| STRDEC of Absyn.StrDec * PE.pE
| STRDECLIST of Absyn.StrDec list * PE.pE
| STREXP of Absyn.StrExp * PE.pE
| SIGEXP of Absyn.SigExp * (PE.pE * Ident.TyCon list)
| SPEC of Absyn.Spec * (PE.pE * Ident.TyCon list)
| SHAREQ of (Absyn.SharEq * Location.T) list
| SIGBIND of (Ident.SigId * Absyn.SigExp * Location.T) list * PE.pG
| FUNBIND of FunBind list * PE.pF
| TOPDEC of Absyn.TopDec * PE.pB
| EXBIND of Absyn.ExBind list * PE.pVE * TyVarSet
| EXBIND1 of Absyn.ExBind * Ident.ValId * TyVarSet
| VALDESC of (Ident.ValId * Absyn.Ty * TyVarSet) list * PE.pVE
| TYPDESC of (TyVarList * Ident.TyCon * (Absyn.Ty * TyVarSet) option) list
| DATDESC of (TyVarList * Ident.TyCon * (ConType list)) list * PE.pVE * PE.pTE
| DATREPLDESC of (TyVarList * Ident.TyCon * Ident.LongTyCon)
| DATDESC1 of (TyVarList * Ident.TyCon * (ConType list)) * PE.pVE * PE.pTE
| CONDESC of ConType list * PE.pVE * TyVarSet
| EXDESC of ExType list * PE.pVE
| EXDESC1 of ExType
| STRDESC of (Ident.StrId * Absyn.SigExp) list * PE.pSE
| STRDESC1 of Ident.StrId * Absyn.SigExp * PE.pE
| STRBIND of ((Ident.StrId * (Absyn.SigExp * bool) option * Absyn.StrExp * bool ref * Location.T * Absyn.DebuggerStr ref option * Absyn.Structure option ref option) list * PE.pSE)
| STRBIND1 of ((Ident.StrId * (Absyn.SigExp * bool) option * Absyn.StrExp * bool ref * Location.T * Absyn.DebuggerStr ref option * Absyn.Structure option ref option) * (Ident.StrId * PE.pE))
| FUNDEC of Absyn.FunBind list * PE.pF
| SIGDEC of Absyn.SigBind list * PE.pG
| FUNBIND1 of FunBind * Ident.FunId * PE.pE
| STARTFUNBIND1 of Ident.FunId * PE.pE * Ident.StrId * Absyn.SigExp * PE.pE
| STARTFUNBIND2 of Ident.FunId * PE.pE * Absyn.Spec * PE.pE
| FUNIDBIND of Ident.FunId * PE.pE
| LONGIDLIST of (Symbol.Symbol list * Symbol.Symbol) list
| DECLIST of Absyn.Dec list * PE.pE * TyVarSet
| EXPLIST of Absyn.Exp list * TyVarSet
| EXPSEQ of (Absyn.Exp * string * Location.T) list * TyVarSet
| EXPROW of LabExp list * TyVarSet
| LONGSTRIDLIST of Ident.LongStrId list
| LONGTYCONLIST of Ident.LongTyCon list
| SIGIDLIST of Ident.SigId list
| TYLIST of Absyn.Ty list * TyVarSet
| TYVARLIST of TyVarList
| TYROW of (Ident.Lab * Absyn.Ty) list * TyVarSet
| PATROW1 of LabPat * PE.pVE * TyVarSet
| PATROW of (LabPat list) * bool * PE.pVE * TyVarSet
| PATLIST of Absyn.Pat list * PE.pVE * TyVarSet
| SYMLIST of Symbol.Symbol list
| MRULE of PatExp * TyVarSet
| MATCH of PatExp list * TyVarSet
| VALBIND of PatExpMark list * PatExpMark list * TyVarSet * PE.pVE
| TYPBIND of (TyVarList * Ident.TyCon * Absyn.Ty * Absyn.Tyfun ref option) list
| LONGTYPBIND of (TyVarList * Ident.LongTyCon * Absyn.Ty * Location.T) list
| FVAL of (FVal * TyVarSet) * Ident.ValId
| FVALLIST of FVal list * TyVarSet * Ident.ValId * Location.T
| FVALBIND of ((FVal list * (string -> string) * Location.T) list * TyVarSet) * PE.pVE
| NULLTYPE
| LABEXP of LabExp * TyVarSet
| OPPRESENT
| DUMMY
| EQVAL
| LOCATION of Location.T
| OPTsigexp of Absyn.SigExp option
end
datatype ActionOpts = OPTS of (Location.T * Info.options * Options.options)
fun get_location (OPTS(l,_,_)) = l
fun options_of (OPTS(_,x,_)) = x
fun options_of' (OPTS(_,_,x)) = x
fun print_options_of (OPTS(_,_,Options.OPTIONS{print_options,...})) = print_options
fun compat_options_of (OPTS(_,_,Options.OPTIONS{compat_options,...})) = compat_options
fun op_optional (OPTS(_,_,Options.OPTIONS
{compat_options = Options.COMPATOPTIONS x, ...})) =
#nj_op_in_datatype x
fun generate_moduler(OPTS(_,_,
Options.OPTIONS
{compiler_options=Options.COMPILEROPTIONS{generate_moduler, ...}, ...})) =
generate_moduler
fun do_abstraction (strid,SOME (sigexp,abs),strexp,boolref,location,a,b) =
(strid,SOME (sigexp,true),strexp,boolref,location,a,b)
| do_abstraction (strid,NONE,Absyn.CONSTRAINTstrexp (strexp,sigexp,_,bref,location'),boolref,location,a,b) =
(strid,NONE,Absyn.CONSTRAINTstrexp (strexp,sigexp,true,bref,location),boolref,location,a,b)
| do_abstraction a = a
val dummy_location = Location.UNKNOWN
fun get_sym (Ident.VAR s) = s
| get_sym (Ident.CON s) = s
| get_sym (Ident.EXCON s) = s
| get_sym (_) = Crash.impossible "get_sym:actionfunctions"
local
fun locate(OPTS(location,_,_),string) =
concat [string,"[",Location.to_string location,"]"]
fun locate'(location,string) =
concat [string,"[",Location.to_string location,"]"]
in
fun make_hash_info (opts,Ident.LAB sym) = locate (opts,"#" ^ Symbol.symbol_name sym ^ " ")
fun make_seq_info (opts) = locate (opts,"<seq>")
fun make_and_info (opts) = locate (opts,"<andalso>")
fun make_orelse_info (opts) = locate (opts,"<orelse>")
fun make_handle_info (opts) = locate (opts,"<handle>")
fun make_if_info (opts) = locate (opts,"<if>")
fun make_case_info (opts) = locate (opts,"<case>")
fun make_fn_info (opts) = locate (opts,"<anon>")
fun make_exbind_info (opts,Ident.EXCON sym) = locate (opts,Symbol.symbol_name sym)
| make_exbind_info _ = Crash.impossible "Not an excon in an exbind"
fun make_funbind_info (opts,Ident.FUNID sym) = locate (opts,"Functor " ^ Symbol.symbol_name sym)
fun make_while_info (opts) = fn x => locate(opts,x)
fun make_fval_info loc = fn x => locate'(loc,x)
end
fun do_info (OPTS(location,options,_),message_type,message) =
Info.error options (message_type,location,message)
fun error (opts,message) =
do_info(opts,Info.RECOVERABLE,message)
fun warn (opts,message) =
do_info(opts,Info.WARNING,message)
fun function_pattern_error (opts,pattern) =
let
val message =
case pattern of
Absyn.VALpat ((longid,_),_) =>
"Constructor " ^
IdentPrint.printLongValId (print_options_of opts) longid ^
" occurs as function name"
| _ => "Pattern occurs as function name"
in
error (opts,message)
end
fun make_id_name ([],s) =
Symbol.symbol_name s
| make_id_name (str :: l,s) =
Symbol.symbol_name str ^ "." ^ make_id_name(l,s)
fun print_token (LRbasics.LONGID,LONGID x) = make_id_name x
| print_token (LRbasics.INTEGER,INTEGER i) = i
| print_token (LRbasics.STRING,STRING s) = "\"" ^ MLWorks.String.ml_string (s,10) ^ "\""
| print_token (LRbasics.CHAR,CHAR s) = "#\"" ^ MLWorks.String.ml_string (s,10) ^ "\""
| print_token (LRbasics.REAL,REAL s) = s
| print_token (LRbasics.WORD, WORD s) = s
| print_token (x,_) = LRbasics.token_string x
fun report_long_error (opts,longid,ty) =
error(opts,"Unexpected long " ^ ty ^ ": " ^ (make_id_name longid))
val do_debug = ref false
fun do_output message = (print message; print"\n")
fun debug message =
if !do_debug then do_output message else ()
fun char_digit s = (ord (String.sub(s, 0))) - ord #"0"
fun parse_precedence (opts,s) =
case size s of
1 => char_digit s
| _ => (error(opts,"Only Single digit allowed for precedence, using 0");
0)
val asterisk_symbol = Symbol.find_symbol "*"
val equal_symbol = Symbol.find_symbol "="
fun mkTyCon (opts,sym) =
(if sym = asterisk_symbol then
error(opts,"Asterisk not allowed as type constructor")
else
();
Ident.TYCON sym)
val mkPath = Ident.mkPath
fun mkLongVar (syms,sym) =
Ident.LONGVALID (mkPath syms, Ident.VAR sym)
val equal_lvalid = mkLongVar ([],equal_symbol)
fun mkLongCon (syms,sym) =
Ident.LONGVALID (mkPath syms, Ident.CON sym)
fun mkLongExCon (syms,sym) =
Ident.LONGVALID (mkPath syms, Ident.EXCON sym)
fun mkLongTyCon (opts,syms,sym) =
(if sym = asterisk_symbol then
error(opts,"Asterisk not allowed as type constructor")
else
();
Ident.LONGTYCON (mkPath syms, Ident.TYCON sym))
fun mkLongStrId (syms,sym) =
Ident.LONGSTRID (mkPath syms, Ident.STRID sym)
exception LongError
fun make_long_id x = Ident.LONGVALID (Ident.NOPATH, x)
fun is_constructor (Ident.CON _) = true
| is_constructor (Ident.EXCON _) = true
| is_constructor _ = false
fun is_long_constructor (Ident.LONGVALID(_,id)) =
is_constructor id
val error_symbol = Symbol.find_symbol "<Error>"
fun make_id_value s = LONGID ([],Symbol.find_symbol s)
val error_id_value = LONGID ([],error_symbol)
val error_id = Ident.VAR error_symbol
fun is_error_longid (Ident.LONGVALID (Ident.NOPATH,valid)) =
get_sym valid = error_symbol
| is_error_longid _ = false
fun get_old_definition opts =
let
val Options.COMPATOPTIONS {old_definition,...} = compat_options_of opts
in
old_definition
end
fun check_is_old_definition (opts,message) =
if get_old_definition opts then ()
else
error (opts,message)
fun check_is_new_definition (opts,message) =
if get_old_definition opts then error (opts,message)
else ()
fun check_is_constructor (opts,id,strname_opt) =
let val print_options = print_options_of opts
in
if is_long_constructor id then
()
else
case strname_opt of
SOME strname =>
error (opts,"Unbound structure " ^ strname ^ " in " ^ (IdentPrint.printLongValId print_options id))
| _ =>
error (opts,"Constructor " ^ (IdentPrint.printLongValId print_options id) ^ " not defined")
end
fun check_is_short_constructor (opts,id) =
if get_sym id = error_symbol then ()
else
let val print_options = print_options_of opts
in
if is_constructor id then
()
else
error (opts,"Non-constructor " ^
(IdentPrint.printValId print_options id) ^ " used in pattern")
end
fun check_not_short_constructor (opts,id) =
if get_sym id = error_symbol then ()
else
let val print_options = print_options_of opts
in
if is_constructor id then
error (opts,"Cannot bind constructor " ^
(IdentPrint.printValId print_options id))
else
()
end
fun check_integer_bounds (opts, int:string) =
if size int = 0 then
error (opts, "Malformed integer label")
else
let val c = String.sub(int, 0)
in
if c = #"0" then
error (opts, "Leading zero in numeric label not allowed")
else if c = #"~" then
error (opts, "Integer label must be positive")
else
()
end
fun zap_pFE (OPTS(_,_,options),x as PE.E(_,pVE,pTE,pSE)) =
let
val Options.OPTIONS {compat_options = Options.COMPATOPTIONS{open_fixity, ...},
...} = options
in
if open_fixity
then x
else
PE.E(PE.empty_pFE,pVE,pTE,pSE)
end
fun zap_pFE(_, x ) = x
fun zap_for_sig (PE.E(_,_,pTE,pSE)) =
PE.E(PE.empty_pFE,PE.empty_pVE,pTE,pSE)
fun pe_error _ = Crash.impossible"identifier clash in empty env"
fun make_pSE (opts,id,pE) = PE.addStrId (pe_error,id,zap_pFE (opts,pE),PE.empty_pSE)
fun make_pVE id = PE.addValId (pe_error,id, PE.empty_pVE)
fun make_pTE (id,pVE) = PE.addTyCon (pe_error,id, pVE, PE.empty_pTE)
fun make_pFE ids_fixity = PE.make_pFE ids_fixity
fun make_pF (id,pE) = PE.addFunId (pe_error,id,pE,PE.empty_pF)
fun make_pG (id,pE,tycons) = PE.addSigId (pe_error,id,pE,tycons,PE.empty_pG)
fun pVE_in_pE pVE = PE.E (PE.empty_pFE,pVE,PE.empty_pTE,PE.empty_pSE)
fun pVEpTE_in_pE (pVE,pTE) = PE.E (PE.empty_pFE,pVE,pTE,PE.empty_pSE)
fun pTE_in_pE pTE = PE.E (PE.empty_pFE,PE.empty_pVE,pTE,PE.empty_pSE)
fun pSE_in_pE pSE = PE.E(PE.empty_pFE,PE.empty_pVE,PE.empty_pTE,pSE)
fun pFE_in_pE pFE = PE.E(pFE,PE.empty_pVE,PE.empty_pTE,PE.empty_pSE)
fun pE_in_pB pE = PE.B(PE.empty_pF,PE.empty_pG,pE)
fun pF_in_pB pF = PE.B(pF,PE.empty_pG,PE.empty_pE)
fun pG_in_pB pG = PE.B(PE.empty_pF,pG,PE.empty_pE)
val ref_pE = ref PE.empty_pE;
fun get_current_pE () = !ref_pE
fun set_pE pE = ref_pE := pE
fun extend_pE (pE) = ref_pE := PE.augment_pE (!ref_pE,pE)
fun extend_pVE (pVE) = extend_pE (pVE_in_pE pVE)
fun extend_pTE (pTE) = extend_pE (pTE_in_pE pTE)
fun extend_pSE (pSE) = extend_pE (pSE_in_pE pSE)
fun lookupValId x = PE.tryLookupValId (x,!ref_pE)
fun lookupLongTycon x =
(case PE.lookupTycon (x,!ref_pE)
of NONE => (case PE.lookupTycon (x,PE.builtins_pE)
of NONE => PE.empty_pVE
| SOME x => x)
| SOME x => x)
handle PE.LookupStrId _ => PE.empty_pVE
fun lookupStrId (opts,strid) =
PE.lookupStrId (strid,!ref_pE)
handle PE.LookupStrId sym =>
(error (opts,
concat (case strid of
([],_) => ["Unbound structure ",
IdentPrint.printLongStrId (mkLongStrId strid)]
| _ => ["Unbound structure ", Symbol.symbol_name sym,
" in " ^ IdentPrint.printLongStrId (mkLongStrId strid)]));
PE.empty_pE)
fun getValId (id as ([],sym)) =
(case lookupValId id of
SOME x => x
| NONE => Ident.VAR sym)
| getValId id = Crash.impossible "Longid in getvalid"
fun resolveLongValId (opts,id) =
let val print_options = print_options_of opts
in
(case PE.lookupValId (id,!ref_pE) of
SOME (Ident.VAR _) => (mkLongVar id,NONE)
| SOME (Ident.CON _) => (mkLongCon id,NONE)
| SOME (Ident.EXCON _) => (mkLongExCon id,NONE)
| SOME _ => Crash.impossible "TYCON':resolveLongValId:actionfunctions"
| NONE => (mkLongVar id,NONE))
handle PE.LookupStrId sym => (mkLongVar id,SOME (Symbol.symbol_name sym))
end
fun resolveValId sym =
case lookupValId ([],sym) of
SOME x => x
| NONE => Ident.VAR sym
fun is_infix s =
case PE.lookupFixity (s, !ref_pE) of
PE.NONFIX => false
| _ => true
fun check_excon (id as Ident.LONGVALID (_,valid),strname_opt,opts) =
if is_error_longid id then ()
else
case valid of
Ident.EXCON _ => ()
| _ =>
(case strname_opt of
SOME strname =>
error (opts, "Unbound Structure " ^ strname ^ " in " ^
IdentPrint.printLongValId (print_options_of opts) id)
| _ =>
error (opts,"Identifier " ^
IdentPrint.printLongValId (print_options_of opts) id ^
" not an exception constructor"))
fun check_is_infix (opts,id) =
let
val sym = get_sym id
in
if sym = error_symbol
then ()
else
if is_infix sym then ()
else
let val print_options = print_options_of opts
in
error (opts,"Symbol " ^
(IdentPrint.printValId print_options id) ^
" not infix")
end
end
fun check_is_infix_constructor (opts,longid) =
if is_error_longid longid
then ()
else
let
val print_options = print_options_of opts
val infixp =
(case longid of
Ident.LONGVALID(Ident.NOPATH,id) => is_infix (get_sym id)
| _ => false)
val consp = is_long_constructor longid
in
if consp then
if infixp
then ()
else
error (opts,"Constructor " ^
(IdentPrint.printLongValId print_options longid) ^
" not infix")
else
if infixp
then error (opts,"Infix constructor " ^
(IdentPrint.printLongValId print_options longid) ^
" not defined")
else
error (opts,"Constructor " ^
(IdentPrint.printLongValId print_options longid) ^
" not defined and not infix")
end
fun check_not_constructor_symbol (opts,s) =
(case lookupValId ([],s) of
SOME valid =>
if is_constructor valid
then error (opts,"Trying to bind constructor " ^
Symbol.symbol_name s ^ " in record")
else ()
| NONE => ())
val ref_pB = ref PE.empty_pB
fun set_pB (pB) = ref_pB := pB
fun setParserBasis (pB as (PE.B(pF,pG,pE))) = (set_pB pB;ref_pE := pE)
fun getParserBasis () =
let val (PE.B(pF,pG,_)) = !ref_pB
in
PE.B(pF,pG,!ref_pE)
end
fun lookupFunId (opts,x) =
PE.lookupFunId (x,!ref_pB)
handle PE.Lookup =>
(error (opts,"functor " ^ IdentPrint.printFunId x ^ " not defined");
PE.empty_pE)
fun lookupSigId (opts,x) =
PE.lookupSigId (x,!ref_pB)
handle PE.Lookup =>
(error (opts,"signature " ^ IdentPrint.printSigId x ^ " not defined");
(PE.empty_pE,[]))
local
fun get_pB () = !ref_pB
in
fun extend_pF (pF) = set_pB(PE.augment_pB(get_pB(),
PE.B(pF,PE.empty_pG,PE.empty_pE)))
fun extend_pG (pG) = set_pB(PE.augment_pB(get_pB(),PE.B(PE.empty_pF,pG,
PE.empty_pE)))
end
fun augment_tycons ([],tycons2,opts) = tycons2
| augment_tycons (tycon::rest,tycons2,opts) =
(if Lists.member (tycon, tycons2)
then error (opts,"Duplicate type specifications in signature: " ^
IdentPrint.printTyCon tycon)
else ();
augment_tycons (rest,tycon::tycons2,opts))
fun spec_augment_pE ((pe1,tycons1),(pe2,tycons2),opts) =
if get_old_definition opts
then
(PE.augment_pE (pe1,pe2),tycons1 @ tycons2)
else
(PE.unique_augment_pE
((fn id => error (opts,"Duplicate value specifications in signature: " ^
IdentPrint.printValId (print_options_of opts) id),
fn id => error (opts,
"Duplicate structure specifications in signature: " ^
IdentPrint.printStrId id)),
pe1,pe2),
augment_tycons (tycons1,tycons2,opts))
fun combine_specs (Absyn.SEQUENCEspec speclist1,Absyn.SEQUENCEspec speclist2) =
Absyn.SEQUENCEspec (speclist1 @ speclist2)
| combine_specs (Absyn.SEQUENCEspec speclist1,spec2) =
Absyn.SEQUENCEspec (speclist1 @ [spec2])
| combine_specs (spec1,Absyn.SEQUENCEspec speclist2) =
Absyn.SEQUENCEspec (spec1 :: speclist2)
| combine_specs (spec1,spec2) =
Absyn.SEQUENCEspec [spec1,spec2]
fun names_of_typedesc t = map (fn (a,b,c) => b) t
fun names_of_datdesc t = map (fn (a,b,c) => b) t
fun make_long tycon = Ident.LONGTYCON (Ident.NOPATH,tycon)
fun do_type_spec (eq,speclist,opts) =
Absyn.SEQUENCEspec
(map
(fn (tyvars,name,optty) =>
case optty of
NONE => if eq then Absyn.EQTYPEspec [(tyvars,name)]
else Absyn.TYPEspec [(tyvars,name)]
| SOME (ty,tyvarset) =>
let
val location = get_location opts
in
Absyn.INCLUDEspec
(Absyn.WHEREsigexp(Absyn.NEWsigexp
(if eq then Absyn.EQTYPEspec [(tyvars,name)]
else Absyn.TYPEspec [(tyvars,name)],
ref NONE),
[(tyvars,make_long name,ty,get_location opts)]),
get_location opts)
end)
speclist)
fun extend_pE_for_fixity symbols_fixity =
let val new_pE = pFE_in_pE (make_pFE symbols_fixity)
in
(extend_pE new_pE; DEC (Absyn.SEQUENCEdec [],new_pE,Set.empty_set))
end
fun make_fixity_pE (symbols_fixity) =
pFE_in_pE (make_pFE symbols_fixity)
fun label_name (Ident.LAB sym) = Symbol.symbol_name sym
fun tyvar_name (Ident.TYVAR(sym,_,_)) = Symbol.symbol_name sym
fun member eq_test (a,l) =
let
fun member_aux [] = false
| member_aux (b::l) =
eq_test(a,b) orelse member_aux l
in
member_aux l
end
fun get_intersection (l1,l2,eq_test,key) =
let
val member' = member eq_test
fun aux ([],result) = rev result
| aux (a::l,result) =
if member'(a,l2)
then aux(l,(key a)::result)
else aux(l,result)
in
aux(l1,[])
end
fun check_list_inclusion (sublist,list,eq_test) =
let
val member' = member eq_test
fun check_all ([],_) = true
| check_all (x::l,l') = member'(x,l') andalso check_all(l,l')
in
check_all(sublist,list)
end
fun check_disjoint_labels (opts,lab,[]) = ()
| check_disjoint_labels (opts,lab,(lab',_):: l) =
if Ident.lab_eq(lab,lab')
then error(opts,"Duplicate labels in record: " ^ (label_name lab))
else check_disjoint_labels(opts,lab,l)
fun check_disjoint_tyvars (opts,tyvar,[]) = ()
| check_disjoint_tyvars (opts,tyvar,(tyvar'::tyvars)) =
if Ident.tyvar_eq(tyvar,tyvar')
then error(opts,"Duplicate type variables in sequence: " ^ (tyvar_name tyvar))
else check_disjoint_tyvars (opts,tyvar,tyvars)
fun check_tyvar_inclusion (opts,tyvars,tyvarlist) =
if check_list_inclusion (Set.set_to_list tyvars,tyvarlist,Ident.tyvar_eq)
then ()
else error(opts,"Free type variable in type declaration")
fun check_empty_tyvars (opts,tyvars) =
if tyvars=[] then ()
else error (opts,"Unexpected type variables in datatype replication.")
local
fun check_disjoint_tycons (key,message)(opts,item,list) =
if member(fn (x,y) => Ident.tycon_eq(key x,key y))(item,list)
then
let val Ident.TYCON sym = key item
in
error (opts,"Multiple declaration of type constructor " ^
Symbol.symbol_name sym ^ " in " ^ message)
end
else ()
in
val check_disjoint_datbind = check_disjoint_tycons
(fn (_,x,_,_,_) => x, "datatype binding")
val check_disjoint_typbind = check_disjoint_tycons
(fn (_,x,_,_) => x, "type binding")
val check_disjoint_datdesc = check_disjoint_tycons
(fn (_,x,_) => x, "datatype specification")
val check_disjoint_typdesc = check_disjoint_tycons
(fn (_,x,_) => x, "type specification")
end
fun is_con_pat(Absyn.WILDpat _) = false
| is_con_pat(Absyn.SCONpat _) = false
| is_con_pat(Absyn.VALpat({1=long_valid, ...}, _)) =
is_long_constructor long_valid
| is_con_pat(Absyn.RECORDpat _) = false
| is_con_pat(Absyn.APPpat _) = false
| is_con_pat(Absyn.TYPEDpat{1=pat, ...}) = is_con_pat pat
| is_con_pat(Absyn.LAYEREDpat({1=valid, ...}, pat)) =
is_constructor valid orelse is_con_pat pat
fun is_fn (Absyn.FNexp _) = true
| is_fn (Absyn.TYPEDexp (exp,_,_)) = is_fn exp
| is_fn _ = false
fun check_rec_bindings (opts,[]) = ()
| check_rec_bindings(opts,(pat,exp,location)::l) =
(if is_fn exp then
if not (get_old_definition opts) orelse not (is_con_pat pat) then
()
else
error(opts, "Attempt to rebind constructor as variable")
else
error(opts,"Non-function expression in value binding");
check_rec_bindings(opts,l))
fun check_disjoint_withtype (opts,datbindlist,typbindlist) =
let
fun get_datbind_tycon (_,tycon,_,_,_) = tycon
fun get_typbind_tycon (_,tycon,_,_) = tycon
fun compare (datbind,typbind) = Ident.tycon_eq (get_datbind_tycon datbind,get_typbind_tycon typbind)
fun make_string [] = ""
| make_string [Ident.TYCON sym] = Symbol.symbol_name sym
| make_string ((Ident.TYCON sym) :: l) = (Symbol.symbol_name sym) ^ ", " ^ make_string l
val intersection = get_intersection (datbindlist,typbindlist,compare,get_datbind_tycon)
in
case intersection of
[] => ()
| tycons => (error (opts,"Duplicate bindings in withtype: " ^ make_string tycons))
end
fun identity x = x;
fun print_list([],_) = ""
| print_list([x],f) = f x
| print_list(x::l,f) = f x ^ ", " ^ print_list(l,f)
fun ordered_intersection(l1,l2,eq,order,key) =
let
fun aux([],_,result) = rev result
| aux(_,[],result) = rev result
| aux(all as (a::l),all' as (a'::l'),result) =
let
val b = key a and b' = key a'
in
if eq(b,b')
then aux(l,l',b::result)
else
if order(b,b')
then aux(l,all',result)
else aux(all,l',result)
end
in
aux (l1,l2,[])
end
fun merge_pVEs (opts as OPTS(location,options,_),pVE1 as (PE.VE ve1),
pVE2 as (PE.VE ve2)) =
let
val print_options = print_options_of opts
fun error_fun (_,valid1,valid2) =
(error (opts,"Multiple declaration of value " ^
IdentPrint.printValId print_options valid1);
valid1)
in
PE.VE (Map.merge error_fun (ve1,ve2))
end
fun merge_pTEs (opts as OPTS(location,options,_),pTE1 as (PE.TE te1),
pTE2 as (PE.TE te2)) =
let
val print_options = print_options_of opts
fun error_fun (_,tycon1,tycon2) = tycon1
in
PE.TE (Map.merge error_fun (te1,te2))
end
fun varify (v as Ident.VAR x) = v
| varify (Ident.CON x) = Ident.VAR x
| varify (Ident.EXCON x) = Ident.VAR x
| varify (Ident.TYCON' x ) = Ident.VAR x
local
fun newvar (x,opts,pVE) =
let val vid = Ident.VAR x
in pVE:=merge_pVEs(opts,!pVE,make_pVE vid);
vid
end
fun varify (v as Ident.VAR x,_,_) = v
| varify (Ident.CON x,opts,pVE) = newvar (x,opts,pVE)
| varify (Ident.EXCON x,opts,pVE) = newvar(x,opts,pVE)
| varify (Ident.TYCON' x,opts,pVE) = newvar(x,opts,pVE)
fun varifyLong(Ident.LONGVALID(Ident.NOPATH,id),opts,pVE) =
Ident.LONGVALID(Ident.NOPATH,varify (id,opts,pVE))
| varifyLong (x,_,_) = x
fun map f [] = []
| map f (h::t) = (f h)::(map f t)
fun varify_pat (w as Absyn.WILDpat _,_,_) = w
| varify_pat (s as Absyn.SCONpat _,_,_) = s
| varify_pat (Absyn.VALpat((vid,s),l),opts,pVE) =
Absyn.VALpat((varifyLong (vid,opts,pVE),s),l)
| varify_pat (Absyn.RECORDpat(l,b,r),opts,pVE) =
let fun varify_label_binding (lab,pat) =
(lab,varify_pat(pat,opts,pVE))
in Absyn.RECORDpat(map varify_label_binding l,b,r)
end
| varify_pat (Absyn.APPpat(c,p,l,b),opts,pVE) =
Absyn.APPpat(c,varify_pat (p,opts,pVE),l,b)
| varify_pat (Absyn.TYPEDpat(pat,ty,loc),opts,pVE) =
Absyn.TYPEDpat(varify_pat (pat,opts,pVE),ty,loc)
| varify_pat (Absyn.LAYEREDpat((vid,tuple),pat),opts,pVE) =
Absyn.LAYEREDpat((varify (vid,opts,pVE),tuple),
varify_pat (pat,opts,pVE))
in
fun varify_valbind(VALBIND(v1,v2,tyvars,pVE),opts) =
let val global_pVE = ref pVE
fun varify_patexp (pat,exp,loc) =
(varify_pat (pat,opts,global_pVE),exp,loc)
in VALBIND(map varify_patexp v1,
map varify_patexp v2, tyvars, !global_pVE)
end
| varify_valbind _ = Crash.impossible "_actionfunctions:varify_valbind"
end
local
fun valid_error opts (_,_,valid) =
let
val print_options = print_options_of opts
in
(error(opts,"Multiple declaration of value " ^
IdentPrint.printValId print_options valid);
valid)
end
fun tycon_error opts (_,_,x) = x
fun strid_error opts (strid,_,pE) =
(error(opts,"Multiple declaration of structure " ^
IdentPrint.printStrId strid);
pE)
fun funid_error opts (funid,_,pE) =
(error(opts,"Multiple declaration of functor " ^
IdentPrint.printFunId funid);
pE)
fun sigid_error opts (sigid,_,pE) =
(error(opts,"Multiple declaration of signature " ^
IdentPrint.printSigId sigid);
pE)
in
fun addNewStrId (opts,(strid,pE),pSE) =
PE.addStrId (strid_error opts,strid,zap_pFE (opts,pE),pSE)
fun addNewSigId (opts,(id,pE,tycons),pGE) =
PE.addSigId (sigid_error opts,id,pE,tycons,pGE)
fun addNewFunId (opts,(id,pE),pFE) = PE.addFunId (funid_error opts,id,pE,pFE)
fun addNewValId (opts,id,pE) = PE.addValId (valid_error opts,id,pE)
fun addNewTycon (opts,id,pVE,pTE) = PE.addTyCon (tycon_error opts,id,
pVE,pTE)
end
fun addNewSymId (opts,sym,pVE) = addNewValId(opts,Ident.VAR sym,pVE)
fun make_Sym_pVE (sym) = make_pVE(Ident.VAR sym)
local
fun is_needed (Ident.NOPATH,s) = is_infix s
| is_needed _ = false
in
fun check_longid_op (opts,id as Ident.LONGVALID (p,v)) =
if is_error_longid id
then ()
else
if is_needed (p, get_sym v) then
()
else
warn (opts,"Reserved word `op' ignored before `" ^ Symbol.symbol_name(get_sym v) ^ "'")
fun check_non_longid_op (opts,id as Ident.LONGVALID (p,v)) =
if is_error_longid id
then ()
else
if is_needed (p, get_sym v) then
error (opts,"Reserved word `op' required before infix identifier `" ^ Symbol.symbol_name(get_sym v) ^ "'")
else
()
end
fun check_valid_op (opts,v) =
if get_sym v = error_symbol then ()
else
if is_infix (get_sym v) then
()
else
warn (opts,"Reserved word `op' ignored before `" ^
Symbol.symbol_name(get_sym v) ^ "'")
fun check_non_valid_op (opts,v) =
if get_sym v = error_symbol then ()
else
if is_infix (get_sym v) then
error (opts,"Reserved word `op' required before infix identifier `" ^
Symbol.symbol_name(get_sym v) ^ "'") else
()
fun check_conid_op (opts,v) =
if get_sym v = error_symbol then ()
else
if not(is_infix (get_sym v)) orelse op_optional opts then
warn (opts,"Reserved word `op' ignored before `" ^
Symbol.symbol_name(get_sym v) ^ "'")
else
()
fun check_non_conid_op (opts,v) =
if get_sym v = error_symbol then ()
else
if is_infix (get_sym v) andalso not (op_optional opts) then
error (opts,"Reserved word `op' required before infix identifier `" ^
Symbol.symbol_name(get_sym v) ^ "'")
else
()
fun annotate x = (x,ref Absyn.nullType)
fun annotate' x = (x,(ref Absyn.nullType,ref (Absyn.nullRuntimeInfo)))
fun mannotate (OPTS(l,_,_),x) =
(x,ref Absyn.nullType,l,
ref(Absyn.nullInstanceInfo,NONE))
fun fold_pEs pEs =
let fun fold ([],pE) = pE
| fold (pE'::l,pE) = fold(l,PE.augment_pE(pE,pE'))
in
fold (pEs,PE.empty_pE)
end
fun fold_sig_pEs (pEs,opts) =
let fun fold ([],e) = e
| fold (e::l,e') =
fold(l,spec_augment_pE (e,e',opts))
in
fold (pEs,(PE.empty_pE,[]))
end
fun do_open_dec_action (opts as OPTS(_, _, options),longids) =
let
val Options.OPTIONS {compat_options =
Options.COMPATOPTIONS{open_fixity, ...},
...} = options
val pEs = map (fn x => lookupStrId(opts,x)) longids
val valids = map mkLongStrId longids
val new_pE as PE.E(fixity, pVE, pTE, pSE) = fold_pEs pEs
val new_pE = if open_fixity then new_pE else
PE.E(PE.empty_pFE, pVE, pTE, pSE)
in
(extend_pE new_pE;
DEC (Absyn.OPENdec (valids,get_location opts),new_pE,Set.empty_set))
end
fun do_open_spec_action (opts,longids) =
let
val pEs = map (fn x => zap_for_sig (lookupStrId(opts,x))) longids
val valids = map mkLongStrId longids
val new_pE = fold_pEs pEs
in
(extend_pE new_pE;
SPEC(Absyn.OPENspec (valids,get_location opts),(new_pE,[])))
end
fun do_include_action (opts,sigids) =
let
val pEs = map (fn x => lookupSigId(opts,x)) sigids
val (new_pE,new_tycons) = fold_sig_pEs (pEs,opts)
val location = get_location opts
in
(extend_pE new_pE;
SPEC (Absyn.SEQUENCEspec
(map (fn sigid =>
Absyn.INCLUDEspec
(Absyn.OLDsigexp
(sigid,ref NONE,location),
location))
sigids),
(new_pE,new_tycons)))
end
fun join_tyvars [] = Set.empty_set
| join_tyvars [t] = t
| join_tyvars (t::l) = Set.union (t,join_tyvars l)
fun make_infix_fval (opts,id,pat1,pat2,patlist,exp,tvarlist) =
(check_not_short_constructor(opts,id);
check_is_infix (opts,id);
((id,(Derived.make_tuple_pat [pat1,pat2]) :: patlist,
exp,get_location opts),join_tyvars tvarlist))
fun do_derived_funbind (opts,funid,spec,opt_sig,strexp,pE) =
let
val generate_moduler = generate_moduler opts
val (a,b,c,d,f) =
Derived.make_funbind (funid,
Absyn.NEWsigexp(spec,ref NONE),
strexp,
opt_sig,
get_location opts)
in
FUNBIND1 ((a,b,c,d,f,make_funbind_info(opts,funid),ref false,
get_location opts,
if generate_moduler
then SOME (ref(Absyn.nullDebuggerStr))
else NONE,
if generate_moduler
then SOME(ref(NONE))
else NONE),funid,pE)
end
fun do_fixity_spec (ids,precedence,opts as OPTS(_,_,options)) =
let
val Options.OPTIONS {compat_options =
Options.COMPATOPTIONS{fixity_specs, ...},
...} = options
in
if fixity_specs
then
SPEC(Absyn.STRUCTUREspec [],
(make_fixity_pE (ids,precedence),
[]))
else
(error (opts,"Fixity declarations in signatures not valid in this mode");
SPEC(Absyn.STRUCTUREspec [],(PE.empty_pE,[])))
end
exception FoundTopDec of (Absyn.TopDec * PE.pB)
exception ActionError of int
val actions =
MLWorks.Internal.Array.arrayoflist[
fn ([dec],opts) => dec
| _ => raise ActionError 0,
fn ([SCON scon],opts) => EXP (Absyn.SCONexp (annotate scon),Set.empty_set)
| _ => raise ActionError 1,
fn ([LONGVALID (id,_)],opts) => EXP (Absyn.VALexp (mannotate(opts,id)), Set.empty_set)
| _ => raise ActionError 2,
fn ([_],opts) =>
(check_non_longid_op (opts,equal_lvalid);
EXP (Absyn.VALexp (mannotate (opts,equal_lvalid)), Set.empty_set))
| _ => raise ActionError 3,
fn ([_,_],opts) =>
(check_longid_op (opts,equal_lvalid);
EXP (Absyn.VALexp (mannotate (opts,equal_lvalid)), Set.empty_set))
| _ => raise ActionError 4,
fn ([_,EXPROW (l,tyvars),_],opts) => EXP (Absyn.RECORDexp (rev l),tyvars)
| _ => raise ActionError 5,
fn ([_,_],opts) => EXP (Absyn.RECORDexp [],Set.empty_set)
| _ => raise ActionError 6,
fn ([_,LAB lab],opts) =>
EXP (Derived.make_select (lab,get_location opts,make_hash_info(opts,lab)),Set.empty_set)
| _ => raise ActionError 7,
fn ([_,EXP (e,tyvars),_],opts) => EXP(Absyn.DYNAMICexp (e, tyvars, ref (Absyn.nullType,0,tyvars)),tyvars)
| _ => raise ActionError 8,
fn ([_,_,EXP(e,tyvars),_,TY(ty,tyvars'),_],opts) => EXP (Absyn.COERCEexp(e,ty,ref Absyn.nullType, get_location opts),Set.union(tyvars,tyvars'))
| _ => raise ActionError 9,
fn ([_,_],opts) => EXP (Derived.make_unit_exp (),Set.empty_set)
| _ => raise ActionError 10,
fn ([_,EXPLIST (l,tyvars),_],opts) => EXP (Derived.make_tuple_exp (rev l),tyvars)
| _ => raise ActionError 11,
fn ([_,EXPLIST (l,tyvars),_],opts) => EXP (Derived.make_list_exp (rev l,get_location opts,get_current_pE()),tyvars)
| _ => raise ActionError 12,
fn ([_,EXPSEQ (l,tyvars),_],opts) => EXP (Derived.make_sequence_exp (rev l),tyvars)
| _ => raise ActionError 13,
fn ([ENV pE,DEC (dec,_,tyvars1),_,EXPSEQ (l,tyvars2),_],opts) =>
(set_pE pE; EXP (Absyn.LOCALexp (dec,Derived.make_sequence_exp (rev l),
get_location opts),Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 14,
fn ([_,x,_],opts) => x
| _ => raise ActionError 15,
fn ([_],opts) => ENV (get_current_pE())
| _ => raise ActionError 16,
fn ([EXPLIST (l,tyvars1),_,EXP (exp,tyvars2)],opts) => EXPLIST (exp::l,Set.union (tyvars1,tyvars2))
| _ => raise ActionError 17,
fn ([EXP (exp1,tyvars1),_,EXP (exp2,tyvars2)],opts) => EXPLIST ([exp2,exp1],Set.union (tyvars1,tyvars2))
| _ => raise ActionError 18,
fn ([],opts) => EXPLIST ([],Set.empty_set)
| _ => raise ActionError 19,
fn ([EXP (exp,tyvars)],opts) => EXPLIST ([exp],tyvars)
| _ => raise ActionError 20,
fn ([exp],opts) => exp
| _ => raise ActionError 21,
fn ([EXPSEQ(l,tyvars1),_,EXP (exp,tyvars2)],opts) =>
EXPSEQ ((exp,make_seq_info(opts),get_location opts)::l,Set.union (tyvars1,tyvars2))
| _ => raise ActionError 22,
fn ([EXP(exp,tyvars)],opts) => EXPSEQ ([(exp,make_seq_info(opts),get_location opts)],tyvars)
| _ => raise ActionError 23,
fn ([EXPSEQ(l,tyvars1),_,EXP(exp,tyvars2)],opts) =>
EXPSEQ ((exp,make_seq_info(opts),get_location opts)::l,Set.union (tyvars1,tyvars2))
| _ => raise ActionError 24,
fn ([LAB lab,_,EXP (exp,tyvars)],opts) => EXPROW ([(lab,exp)],tyvars)
| _ => raise ActionError 25,
fn ([EXPROW(l,tyvars1),_,LAB lab,_,EXP (exp,tyvars2)],opts) =>
(check_disjoint_labels(opts,lab,l);EXPROW ((lab,exp)::l,Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 26,
fn ([x],opts) => x
| _ => raise ActionError 27,
fn ([EXP (exp1,tyvars1), EXP (exp2,tyvars2)],opts) =>
EXP(Absyn.APPexp(exp1,exp2,get_location opts,ref Absyn.nullType,false),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 28,
fn ([x],opts) => x
| _ => raise ActionError 29,
fn ([EXP (exp1,tyvars1), LONGVALID (id,_), EXP (exp2,tyvars2)],opts) =>
EXP (Absyn.APPexp (Absyn.VALexp (mannotate(opts,id)),Derived.make_tuple_exp [exp1,exp2],get_location opts,ref Absyn.nullType,true),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 30,
fn ([x],opts) => x
| _ => raise ActionError 31,
fn ([_],opts) => LONGVALID (equal_lvalid,NONE)
| _ => raise ActionError 32,
fn ([x],opts) => x
| _ => raise ActionError 33,
fn ([EXP (exp,tyvars1),_,TY (ty,tyvars2)],opts) =>
EXP (Absyn.TYPEDexp (exp,ty,get_location opts),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 34,
fn ([EXP (exp1,tyvars1),_,EXP (exp2,tyvars2)],opts) =>
EXP (Derived.make_andalso (exp1,exp2,make_and_info(opts),get_location opts,get_current_pE()),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 35,
fn ([EXP(exp1,tyvars1),_,EXP (exp2,tyvars2)],opts) =>
EXP (Derived.make_orelse (exp1,exp2,make_orelse_info(opts),get_location opts,get_current_pE()),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 36,
fn ([EXP(exp1,tyvars1),_,MATCH(match,tyvars2)],opts) =>
EXP (Absyn.HANDLEexp (exp1, ref Absyn.nullType, (rev match),get_location opts,make_handle_info(opts)),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 37,
fn ([_,EXP (exp,tyvars)],opts) => EXP (Absyn.RAISEexp (exp,get_location opts),tyvars)
| _ => raise ActionError 38,
fn ([_,EXP (exp1,tyvars1),_,EXP (exp2,tyvars2),_,EXP (exp3,tyvars3)],opts) =>
EXP(Derived.make_if (exp1,exp2,exp3,make_if_info(opts),get_location opts,get_current_pE()),join_tyvars[tyvars1,tyvars2,tyvars3])
| _ => raise ActionError 39,
fn ([_,EXP (exp1,tyvars1),_,EXP (exp2,tyvars2)],opts) =>
EXP (Derived.make_while (exp1,exp2,make_while_info(opts),get_location opts,get_current_pE()),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 40,
fn ([_,EXP(exp,tyvars1),_,MATCH(m,tyvars2)],opts) =>
EXP (Derived.make_case (exp,(rev m),make_case_info(opts),get_location opts),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 41,
fn ([_,MATCH (match,tyvars)],opts) =>
let val (a,b) = annotate (rev match) in EXP (Absyn.FNexp (a,b,make_fn_info(opts),get_location opts),tyvars) end
| _ => raise ActionError 42,
fn ([MATCH (match,tyvars1),_,MRULE (rule,tyvars2)],opts) => MATCH (rule::match,Set.union(tyvars1,tyvars2))
| _ => raise ActionError 43,
fn ([MRULE(rule,tyvars)],opts) => MATCH([rule],tyvars)
| _ => raise ActionError 44,
fn ([PAT(pat,pE,tyvars1),_,EXP(exp,tyvars2)],opts) => MRULE((pat,exp,get_location opts),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 45,
fn ([_,_],opts) => DUMMY
| _ => raise ActionError 46,
fn ([_],opts) => DEC (Absyn.SEQUENCEdec[],PE.empty_pE,Set.empty_set)
| _ => raise ActionError 47,
fn ([_,dec,_],opts) => dec
| _ => raise ActionError 48,
fn ([_,DECLIST(l,pE,tyvars),_],opts) => DEC(Absyn.SEQUENCEdec(rev l),pE,tyvars)
| _ => raise ActionError 49,
fn ([DEC(dec1,pE1,tyvars1),_,DEC(dec2,pE2,tyvars2)],opts) => DECLIST([dec2,dec1],PE.augment_pE(pE1,pE2),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 50,
fn ([DECLIST(l,pE1,tyvars1),_,DEC(dec,pE2,tyvars2)],opts) => DECLIST(dec::l,PE.augment_pE(pE1,pE2),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 51,
fn ([_, TYVARLIST explicit_tyvars, VALBIND (valbinds1,valbinds2,tyvars,pVE)],opts) =>
DEC (Absyn.VALdec (rev valbinds1,rev valbinds2,tyvars,explicit_tyvars),pVE_in_pE pVE,Set.empty_set)
| _ => raise ActionError 52,
fn ([_, VALBIND (valbinds1,valbinds2,tyvars,pVE)],opts) =>
DEC (Absyn.VALdec (rev valbinds1,rev valbinds2,tyvars,[]),pVE_in_pE pVE,Set.empty_set)
| _ => raise ActionError 53,
fn ([_,TYVARLIST explicit_tyvars,FVALBIND ((l,tyvars),pVE)],opts) =>
DEC (Derived.make_fun ((map (fn y => Derived.make_fvalbind (y,options_of opts)) (rev l)),
tyvars,explicit_tyvars,get_location opts),pVE_in_pE pVE,Set.empty_set)
| _ => raise ActionError 54,
fn ([_,FVALBIND ((l,tyvars),pVE)],opts) =>
DEC (Derived.make_fun ((map (fn y => Derived.make_fvalbind (y,options_of opts)) (rev l)),
tyvars,[],get_location opts),pVE_in_pE pVE,Set.empty_set)
| _ => raise ActionError 55,
fn ([_,TYPBIND l],opts) => DEC (Absyn.TYPEdec (rev l),PE.empty_pE,Set.empty_set)
| _ => raise ActionError 56,
fn ([_,DATREPL(tyvars,tycon,longtycon)],opts)=>
(check_is_new_definition(opts,
"Datatype replication is not a feature of SML'90");
check_empty_tyvars(opts,tyvars);
let val pVE = lookupLongTycon longtycon
val pTE = (make_pTE (tycon,pVE))
in
extend_pVE pVE;
extend_pTE pTE;
DEC (Absyn.DATATYPErepl (get_location opts,(tycon,longtycon),
ref NONE),
pVEpTE_in_pE(pVE,pTE),Set.empty_set)
end)
| _ => raise ActionError 57,
fn ([_,DATBIND (d,pVE,pTE)],opts) =>
(extend_pVE pVE;
extend_pTE pTE;
DEC (Absyn.DATATYPEdec (get_location opts,rev d),pVEpTE_in_pE (pVE,pTE),
Set.empty_set))
| _ => raise ActionError 58,
fn ([_,DATBIND (d,pVE,pTE),_,TYPBIND t],opts) =>
(check_disjoint_withtype(opts,d,t);
extend_pVE pVE; extend_pTE pTE;
DEC (Derived.make_datatype_withtype (get_location opts,rev d, t,
options_of opts),
pVEpTE_in_pE(pVE,pTE),Set.empty_set))
| _ => raise ActionError 59,
fn ([_,DATBIND (d,pVE,pTE),_,DEC(dec,pE,tyvars),_],opts) =>
DEC (Absyn.ABSTYPEdec (get_location opts,rev d,dec),
PE.augment_pE (pTE_in_pE(pTE),pE),tyvars)
| _ => raise ActionError 60,
fn ([_,DATBIND (d,pVE,pTE),_,TYPBIND t,_,DEC(dec,pE,tyvars),_],
opts) =>
(check_disjoint_withtype(opts,d,t);
DEC (Derived.make_abstype_withtype
(get_location opts,rev d,t,dec,options_of opts),
PE.augment_pE (pTE_in_pE(pTE),pE),tyvars))
| _ => raise ActionError 61,
fn ([d as (DATBIND(_,pVE,pTE))],opts) =>
(extend_pVE pVE; extend_pTE pTE; d)
| _ => raise ActionError 62,
fn ([_,EXBIND (l,pVE,tyvars)],opts) =>
(extend_pVE pVE; DEC (Absyn.EXCEPTIONdec (rev l),pVE_in_pE pVE,
tyvars))
| _ => raise ActionError 63,
fn ([ENV env_pE,DEC (dec1,_,tyvars1),_,
DEC (dec2,pE,tyvars2),_],opts) =>
(set_pE (PE.augment_pE (env_pE,pE));
DEC (Absyn.LOCALdec (dec1,dec2),pE,Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 64,
fn ([_,LONGIDLIST l],opts) =>
do_open_dec_action (opts,rev l)
| _ => raise ActionError 65,
fn ([_,SYMLIST l],opts) =>
extend_pE_for_fixity (l, PE.LEFT 0)
| _ => raise ActionError 66,
fn ([_,INTEGER i,SYMLIST l],opts) =>
extend_pE_for_fixity (l, PE.LEFT (parse_precedence(opts,i)))
| _ => raise ActionError 67,
fn ([_,SYMLIST l],opts) => extend_pE_for_fixity (l, PE.RIGHT 0)
| _ => raise ActionError 68,
fn ([_,INTEGER i,SYMLIST l],opts) =>
extend_pE_for_fixity (l, PE.RIGHT (parse_precedence(opts,i)))
| _ => raise ActionError 69,
fn ([_,SYMLIST l],opts) =>
extend_pE_for_fixity (l, PE.NONFIX)
| _ => raise ActionError 70,
fn ([_],opts) => ENV (get_current_pE())
| _ => raise ActionError 71,
fn ([x],opts) => x
| _ => raise ActionError 72,
fn ([VALBIND(v1,v2,tyvars1,pVE1),_,VALBIND([a],[],tyvars2,pVE2)],
opts) =>
VALBIND (a::v1,v2,Set.union(tyvars1,tyvars2),
merge_pVEs(opts,pVE1,pVE2))
| _ => raise ActionError 73,
fn ([VALBIND (v1,v1',tyvars1,pVE1),_,_,
VALBIND(v2,v2',tyvars2,pVE2)],opts) =>
(check_rec_bindings (opts,v2);
VALBIND (v1,v1' @ v2 @ v2',Set.union(tyvars1,tyvars2),
merge_pVEs(opts,pVE1,pVE2)))
| _ => raise ActionError 74,
fn ([_,VALBIND (v1,v2,tyvars,pVE)],opts) =>
(check_rec_bindings (opts,v1);
let val vbind = VALBIND ([],v2@v1,tyvars,pVE)
in if get_old_definition opts then vbind
else varify_valbind (vbind,opts)
end)
| _ => raise ActionError 75,
fn ([PAT (pat,pVE,tyvars1),_,EXP (exp,tyvars2)],opts) =>
VALBIND ([(pat,exp,get_location opts)],[],
Set.union (tyvars1,tyvars2),pVE)
| _ => raise ActionError 76,
fn ([FVALLIST (fvals,tyvars,id,loc)],opts) =>
FVALBIND (([((rev fvals),make_fval_info loc,get_location opts)],
tyvars),make_pVE id)
| _ => raise ActionError 77,
fn ([FVALBIND ((l,tyvars1),pVE),_,
FVALLIST (fvals,tyvars2,id,loc)],opts) =>
FVALBIND (((rev fvals,make_fval_info loc,get_location opts)::l,
Set.union(tyvars1,tyvars2)),addNewValId(opts,id,pVE))
| _ => raise ActionError 78,
fn ([FVAL ((fval,tyvars),id)],opts) =>
FVALLIST ([fval],tyvars,id,get_location opts)
| _ => raise ActionError 79,
fn ([FVALLIST (fvals,tyvars1,id,_),_,
FVAL ((fval,tyvars2),id')],opts) =>
FVALLIST (fval::fvals,Set.union(tyvars1,tyvars2),id,
get_location opts)
| _ => raise ActionError 80,
fn ([VALID id,PATLIST(l,pE,tyvars1),TY(ty,tyvars2),
_,EXP (exp,tyvars3)],opts) =>
let val vid = varify id
in FVAL(((vid,rev l,Absyn.TYPEDexp(exp,ty,get_location opts),
get_location opts),
join_tyvars[tyvars1,tyvars2,tyvars3]),vid)
end
| ([VALID id,PATLIST (l,pE,tyvars1),NULLTYPE,_,
EXP (exp,tyvars3)],opts) =>
let val vid = varify id
in FVAL(((vid,rev l,exp,get_location opts),
Set.union(tyvars1,tyvars3)),vid)
end
| _ => raise ActionError 81,
fn ([PAT(pat1,pVE1,tyvars1),VALID id,PAT(pat2,pVE2,tyvars2),
TY (ty,tyvars3),_,EXP (exp,tyvars4)],opts) =>
let val vid = varify id
in (ignore(merge_pVEs(opts,pVE1,pVE2));
FVAL(make_infix_fval (opts,vid,pat1,pat2,[],
Absyn.TYPEDexp(exp,ty,get_location opts),
[tyvars1,tyvars2,tyvars3,tyvars4]),vid))
end
| ([PAT (pat1,pVE1,tyvars1),VALID id,PAT(pat2,pVE2,tyvars2),
NULLTYPE,_,EXP (exp,tyvars3)],opts) =>
let val vid = varify id
in
(ignore(merge_pVEs(opts,pVE1,pVE2));
FVAL(make_infix_fval(opts,vid,pat1,pat2,[],exp,
[tyvars1,tyvars2,tyvars3]),vid))
end
| _ => raise ActionError 82,
fn ([PAT (pat1,pVE1,tyvars1),VALID id,PAT (pat2,pVE2,tyvars2),
_,TY (ty,tyvars3),_,EXP (exp,tyvars4)],opts) =>
(error (opts,"Too many patterns for infix identifier "
^ IdentPrint.printValId (print_options_of opts) id);
ignore(merge_pVEs(opts,pVE1,pVE2));
FVAL(make_infix_fval (opts,id,pat1,pat2,[],
Absyn.TYPEDexp(exp,ty,get_location opts),
[tyvars1,tyvars2,tyvars3,tyvars4]),id))
| ([PAT (pat1,pVE1,tyvars1),VALID id,PAT(pat2,pVE2,tyvars2),
_,NULLTYPE,_,EXP (exp,tyvars3)],opts) =>
(error (opts,"Too many patterns for infix identifier "
^ IdentPrint.printValId (print_options_of opts) id);
ignore(merge_pVEs(opts,pVE1,pVE2));
FVAL(make_infix_fval (opts,id,pat1,pat2,[],exp,
[tyvars1,tyvars2,tyvars3]),id))
| _ => raise ActionError 83,
fn ([PAT (pat1,pVE1,tyvars1),PATLIST(l,pE,tyvars2),
TY (ty,tyvars3),_,EXP (exp,tyvars4)],opts) =>
(function_pattern_error (opts,pat1);
FVAL(((error_id,rev l,Absyn.TYPEDexp(exp,ty,get_location opts),
get_location opts),
Set.union (tyvars2,Set.union (tyvars3,tyvars4))),error_id))
| ([PAT (pat1,pVE1,tyvars1),PATLIST(l,pE,tyvars2),NULLTYPE,_,
EXP(exp,tyvars3)],opts) =>
(function_pattern_error (opts,pat1);
FVAL(((error_id,rev l,exp,get_location opts),
Set.union (tyvars2,tyvars3)),error_id))
| _ => raise ActionError 84,
fn ([PAT (pat1,pVE1,tyvars1),VALID id,TY (ty,tyvars3),_,
EXP (exp,tyvars4)],opts) =>
(function_pattern_error (opts,pat1);
FVAL(make_infix_fval (opts,id,pat1,pat1,[],
Absyn.TYPEDexp(exp,ty,get_location opts),
[tyvars1,tyvars3,tyvars4]),id))
| ([PAT (pat1,pVE1,tyvars1),VALID id,NULLTYPE,_,
EXP (exp,tyvars3)],opts) =>
(function_pattern_error (opts,pat1);
FVAL(make_infix_fval (opts,id,pat1,pat1,[],exp,
[tyvars1,tyvars3]),id))
| _ => raise ActionError 85,
fn ([BINPAT (pat1,id,pat2,pE1,tyvars1),TY (ty,tyvars2),_,
EXP (exp,tyvars3)],opts) =>
let val vid = varify id
in FVAL(make_infix_fval (opts,vid,pat1,pat2,[],
Absyn.TYPEDexp(exp,ty,get_location opts),
[tyvars1,tyvars2,tyvars3]),vid)
end
| ([BINPAT (pat1,id,pat2,pE1,tyvars1),NULLTYPE,_,
EXP (exp,tyvars2)],opts) =>
let val vid = varify id
in FVAL(make_infix_fval (opts,vid,pat1,pat2,[],exp,
[tyvars1,tyvars2]),vid)
end
| _ => raise ActionError 86,
fn ([BINPAT (pat1,id,pat2,pE1,tyvars1),
PATLIST(patl,pE2,tyvars2),TY (ty,tyvars3),_,
EXP (exp,tyvars4)],opts) =>
let val vid = varify id
in FVAL(make_infix_fval(opts,vid,pat1,pat2,rev patl,
Absyn.TYPEDexp(exp,ty,get_location opts),
[tyvars1,tyvars2,tyvars3,tyvars4]),vid)
end
| ([BINPAT (pat1,id,pat2,pE1,tyvars1),
PATLIST(patl,pE2,tyvars2),NULLTYPE,_,EXP (exp,tyvars3)],opts) =>
let val vid = varify id
in FVAL(make_infix_fval(opts,vid,pat1,pat2,rev patl,exp,
[tyvars1,tyvars2,tyvars3]),vid)
end
| _ => raise ActionError 87,
fn ([],opts) => NULLTYPE
| _ => raise ActionError 88,
fn ([_,ty],opts) => ty
| _ => raise ActionError 89,
fn ([x],opts) => x
| _ => raise ActionError 90,
fn ([TYPBIND tbl,_,TYPBIND [tb]],opts) =>
(check_disjoint_typbind(opts,tb,tbl);TYPBIND (tb::tbl))
| _ => raise ActionError 91,
fn ([TYVARLIST tyvarlist, TYCON tycon,_,TY (ty,tyvars)],opts) =>
(check_tyvar_inclusion(opts,tyvars,tyvarlist);TYPBIND [(rev tyvarlist,tycon,ty,if generate_moduler opts then SOME(ref(Absyn.nullTyfun))
else NONE)])
| _ => raise ActionError 92,
fn ([x],opts) => x
| _ => raise ActionError 93,
fn ([LONGTYPBIND tbl,_,LONGTYPBIND [tb]],opts) =>
LONGTYPBIND (tb::tbl)
| _ => raise ActionError 94,
fn ([TYVARLIST tyvarlist, LONGTYCON tycon,_,TY (ty,tyvars)],opts) =>
(check_tyvar_inclusion(opts,tyvars,tyvarlist);
LONGTYPBIND [(rev tyvarlist,tycon,ty,get_location opts)])
| _ => raise ActionError 95,
fn ([],opts) => TYVARLIST []
| _ => raise ActionError 96,
fn ([x],opts) => x
| _ => raise ActionError 97,
fn ([TYVAR t],opts) => TYVARLIST [t]
| _ => raise ActionError 98,
fn ([_,TYVARLIST l,_],opts) => TYVARLIST l
| _ => raise ActionError 99,
fn ([TYVAR t],opts) => TYVARLIST [t]
| _ => raise ActionError 100,
fn ([TYVARLIST l,_,TYVAR t],opts) => (check_disjoint_tyvars(opts,t,l);TYVARLIST (t::l))
| _ => raise ActionError 101,
fn ([DATAHDR(tyvars, tycon),_, LONGTYCON longtycon],opts) =>
DATREPL(tyvars,tycon,longtycon)
| _ => raise ActionError 102,
fn ([DATBIND1(d,pVE,pTE)],opts) => DATBIND([d],pVE,pTE)
| _ => raise ActionError 103,
fn ([DATBIND(l,pVE,pTE),_,DATBIND1(d,pVE',pTE')],opts) =>
(check_disjoint_datbind(opts,d,l);DATBIND(d::l,merge_pVEs(opts,pVE,pVE'),
merge_pTEs(opts,pTE,pTE')))
| _ => raise ActionError 104,
fn ([DATAHDR(tyvarlist,tycon),CONBIND (conbind,pVE,tyvars)],opts) =>
(check_tyvar_inclusion(opts,tyvars,tyvarlist);
DATBIND1 ((rev tyvarlist,tycon,ref Absyn.nullType,
if generate_moduler opts
then SOME(ref(Absyn.nullTyfun))
else NONE,rev conbind),
pVE, make_pTE (tycon,pVE)))
| _ => raise ActionError 105,
fn ([TYVARLIST tyvarlist, TYCON tycon,_],opts) =>
DATAHDR(tyvarlist,tycon)
| _ => raise ActionError 106,
fn ([CONBIND1(cb,id,tyvars)],opts) => CONBIND([cb],make_pVE id,tyvars)
| _ => raise ActionError 107,
fn ([CONBIND (cbl,pVE,tyvars1),_,CONBIND1 (cb,id,tyvars2)],opts) =>
CONBIND(cb::cbl,addNewValId(opts,id,pVE),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 108,
fn ([VALID id,TY (ty,tyvars)],opts) => CONBIND1 ((annotate id,SOME ty),id,tyvars)
| ([VALID id,NULLTYPE], opts) => CONBIND1 ((annotate id,NONE),id,Set.empty_set)
| _ => raise ActionError 109,
fn ([EXBIND1 (e,id,tyvars)],opts) => EXBIND ([e],make_pVE id,tyvars)
| _ => raise ActionError 110,
fn ([EXBIND (l,pVE,tyvars1),_,EXBIND1 (e,id,tyvars2)],opts) =>
EXBIND (e::l,addNewValId(opts,id,pVE),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 111,
fn ([VALID id, TY (ty,tyvars)],opts) =>
EXBIND1 (Absyn.NEWexbind (annotate id,SOME ty,get_location opts,make_exbind_info(opts,id)),id,tyvars)
| ([VALID id, NULLTYPE],opts) =>
EXBIND1 (Absyn.NEWexbind (annotate id,NONE,get_location opts,make_exbind_info(opts,id)),id,Set.empty_set)
| _ => raise ActionError 112,
fn ([VALID id,_,LONGVALID (id',strname_opt)],opts) =>
(check_excon (id',strname_opt,opts);
EXBIND1 (Absyn.OLDexbind (annotate id,id',get_location opts,make_exbind_info (opts,id)),id,Set.empty_set))
| _ => raise ActionError 113,
fn ([],opts) => NULLTYPE
| _ => raise ActionError 114,
fn ([_,x],opts) => x
| _ => raise ActionError 115,
fn ([_],opts as OPTS(location,options,_)) =>
PAT (Absyn.WILDpat location,PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 116,
fn ([SCON (x as (Ident.REAL _))],opts) =>
(check_is_old_definition
(opts, "Real is not an equality type in SML'96");
PAT (Absyn.SCONpat (annotate x),PE.empty_pVE,Set.empty_set))
| ([SCON x], opts) =>
PAT (Absyn.SCONpat (annotate x),PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 117,
fn ([VALID id],opts) =>
PAT (Absyn.VALpat (annotate' (make_long_id id),
get_location opts),
if is_constructor id then PE.empty_pVE
else make_pVE id,Set.empty_set)
| _ => raise ActionError 118,
fn ([LONGVALID (id,strname_opt)],opts) =>
(check_is_constructor (opts,id,strname_opt);
(PAT (Absyn.VALpat (annotate' id,get_location opts),
PE.empty_pVE,Set.empty_set)))
| _ => raise ActionError 119,
fn ([_,PATROW (columns,wild,pVE,tyvars),_],opts) =>
PAT (Absyn.RECORDpat (rev columns,wild,ref Absyn.nullType),pVE,tyvars)
| _ => raise ActionError 120,
fn ([_,PATROW (columns,wild,pVE,tyvars),_,_,_],opts) =>
PAT (Absyn.RECORDpat (rev columns,true,ref Absyn.nullType),pVE,tyvars)
| _ => raise ActionError 121,
fn ([_,_,_],opts) =>
PAT (Absyn.RECORDpat ([],true,ref Absyn.nullType),PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 122,
fn ([_,_],opts) => PAT (Derived.make_unit_pat(),PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 123,
fn ([_,_],opts) => PAT (Derived.make_unit_pat(),PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 124,
fn ([_,PATLIST (l,pVE,tyvars),_],opts) => PAT (Derived.make_tuple_pat (rev l),pVE,tyvars)
| _ => raise ActionError 125,
fn ([_,_],opts) => PAT (Derived.make_list_pat([],get_location opts,get_current_pE()),PE.empty_pVE,Set.empty_set)
| _ => raise ActionError 126,
fn ([_,PAT(l,pVE,tyvars),_],opts) => PAT (Derived.make_list_pat([l],get_location opts,get_current_pE()),pVE,tyvars)
| _ => raise ActionError 127,
fn ([_,x,_],opts) => x
| _ => raise ActionError 128,
fn ([_,PATLIST (l,pVE,tyvars),_],opts) => PAT (Derived.make_list_pat(rev l,get_location opts,get_current_pE()),pVE,tyvars)
| _ => raise ActionError 129,
fn ([_,PAT (pat1,pVE1,tyvars1),VALID id,
PAT (pat2,pVE2,tyvars2),_],opts) =>
BINPAT(pat1,id,pat2,merge_pVEs(opts,pVE1,pVE2),Set.union(tyvars1,tyvars2))
| _ => raise ActionError 130,
fn ([BINPAT (pat1,id,pat2,pVE,tyvars)],opts) =>
(check_is_short_constructor (opts,id);
PAT (Absyn.APPpat (annotate (make_long_id id),
Derived.make_tuple_pat[pat1,pat2],
get_location opts,true),pVE,tyvars))
| _ => raise ActionError 131,
fn ([x],opts) => x
| _ => raise ActionError 132,
fn ([PAT (pat1,pVE1,tyvars1),_,PAT (pat2,pVE2,tyvars2)],opts) =>
PATLIST ([pat2,pat1], merge_pVEs(opts,pVE1,pVE2), Set.union (tyvars1,tyvars2))
| _ => raise ActionError 133,
fn ([PATLIST (pat1,pVE1,tyvars1),_,PAT (pat2,pVE2,tyvars2)],opts) =>
PATLIST (pat2::pat1, merge_pVEs(opts,pVE1,pVE2), Set.union (tyvars1,tyvars2))
| _ => raise ActionError 134,
fn ([PATROW1 (lp,pVE,tyvars)],opts) => PATROW ([lp],false,pVE,tyvars)
| _ => raise ActionError 135,
fn ([PATROW (l,wild,pVE1,tyvars1),_,PATROW1 (lp as (lab,_),pVE2,tyvars2)],opts) =>
(check_disjoint_labels(opts,lab,l);
PATROW (lp :: l,wild,merge_pVEs(opts,pVE1,pVE2), Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 136,
fn ([LAB lab,_,PAT (pat,pVE,tyvars)],opts) => PATROW1 ((lab,pat),pVE,tyvars)
| _ => raise ActionError 137,
fn ([SYM sym, TY (ty,tyvars)],opts) =>
(check_not_constructor_symbol (opts,sym);
PATROW1 ((Derived.make_patrow (sym, SOME ty, NONE,get_location opts)),make_Sym_pVE sym,tyvars))
| ([SYM sym, NULLTYPE],opts) =>
(check_not_constructor_symbol (opts,sym);
PATROW1 (Derived.make_patrow (sym, NONE, NONE,get_location opts),make_Sym_pVE sym,Set.empty_set))
| _ => raise ActionError 138,
fn ([SYM sym, TY (ty,tyvars1),_,PAT (pat,pVE,tyvars2)],opts) =>
(check_not_constructor_symbol (opts,sym);
PATROW1 (Derived.make_patrow (sym, SOME ty, SOME pat,get_location opts),addNewSymId(opts,sym,pVE),Set.union (tyvars1,tyvars2)))
| ([SYM sym, NULLTYPE,_,PAT (pat,pVE,tyvars)],opts) =>
(check_not_constructor_symbol (opts,sym);
PATROW1 (Derived.make_patrow (sym, NONE, SOME pat,get_location opts),addNewSymId(opts,sym,pVE),tyvars))
| _ => raise ActionError 139,
fn ([x],opts) => x
| _ => raise ActionError 140,
fn ([LONGVALID (id,strname_opt), PAT (pat,pVE,tyvars)],opts) =>
(check_is_constructor (opts,id,strname_opt);
(PAT (Absyn.APPpat (annotate id, pat,get_location opts,false),pVE,tyvars)))
| _ => raise ActionError 141,
fn ([VALID id, PAT (pat,pVE,tyvars)],opts) =>
(check_is_short_constructor (opts,id);
PAT (Absyn.APPpat (annotate (make_long_id id), pat,get_location opts,false),pVE,tyvars))
| _ => raise ActionError 142,
fn ([PAT(pat1,pVE1,tyvars1), LONGVALID (id,_), PAT(pat2,pVE2,tyvars2)],opts) =>
(check_is_infix_constructor (opts,id);
PAT(Absyn.APPpat(annotate id, Derived.make_tuple_pat[pat1,pat2],get_location opts,true),merge_pVEs(opts,pVE1,pVE2),Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 143,
fn ([PAT (pat,pVE,tyvars1),_,TY (ty,tyvars2)],opts) => PAT (Absyn.TYPEDpat(pat,ty,get_location opts),pVE,Set.union(tyvars1,tyvars2))
| _ => raise ActionError 144,
fn ([VALID id, TY(ty,tyvars1),_,PAT(pat,pVE,tyvars2)],opts) =>
PAT (Absyn.TYPEDpat (Absyn.LAYEREDpat (annotate' id, pat), ty,get_location opts),addNewValId(opts,id,pVE),Set.union(tyvars1,tyvars2))
| ([VALID id, NULLTYPE,_,PAT (pat,pVE,tyvars)],opts) =>
PAT (Absyn.LAYEREDpat (annotate' id, pat),addNewValId(opts,id,pVE),tyvars)
| _ => raise ActionError 145,
fn ([TYVAR t],opts) => TY (Absyn.TYVARty (t),Set.singleton t)
| _ => raise ActionError 146,
fn ([_,TYROW (l,tvs),_],opts) => TY (Absyn.RECORDty l,tvs)
| _ => raise ActionError 147,
fn ([_,_],opts) => TY (Absyn.RECORDty [],Set.empty_set)
| _ => raise ActionError 148,
fn ([TYLIST (s,tvs), LONGTYCON c],opts) => TY (Absyn.APPty (rev s,c,get_location opts),tvs)
| _ => raise ActionError 149,
fn ([TY (t,tvs), LONGTYCON c],opts) => TY (Absyn.APPty ([t],c,get_location opts),tvs)
| _ => raise ActionError 150,
fn ([LONGTYCON c],opts) => TY (Absyn.APPty ([],c,get_location opts),Set.empty_set)
| _ => raise ActionError 151,
fn ([TYLIST (tl,tvs)],opts) => TY (Derived.make_tuple_ty (rev tl),tvs)
| _ => raise ActionError 152,
fn ([TY (t1,tvs1),_,TY (t2,tvs2)],opts) => TY (Absyn.FNty (t1,t2),Set.union(tvs1,tvs2))
| _ => raise ActionError 153,
fn ([_,t,_],opts) => t
| _ => raise ActionError 154,
fn ([_,TYLIST (l,tyvars1),_,TY (ty,tyvars2),_],opts) => TYLIST (ty :: l,Set.union(tyvars1,tyvars2))
| _ => raise ActionError 155,
fn ([TY (ty,tyvars)],opts) => TYLIST ([ty],tyvars)
| _ => raise ActionError 156,
fn ([TYLIST (tl,tyvars1),_,TY (ty,tyvars2)],opts) => TYLIST (ty::tl,Set.union(tyvars1,tyvars2))
| _ => raise ActionError 157,
fn ([TYLIST (tl,tyvars1),_,TY (ty,tyvars2)],opts) => TYLIST (ty::tl,Set.union(tyvars1,tyvars2))
| _ => raise ActionError 158,
fn ([TY (t1,tyvars1),_,TY (t2,tyvars2)],opts) => TYLIST ([t2,t1],Set.union(tyvars1,tyvars2))
| _ => raise ActionError 159,
fn ([LAB l,_,TY (ty,tyvars)],opts) => TYROW ([(l,ty)],tyvars)
| _ => raise ActionError 160,
fn ([TYROW (l,tyvars1),_,LAB lab,_,TY (ty,tyvars2)],opts) =>
(check_disjoint_labels(opts,lab,l);TYROW ((lab,ty)::l,Set.union(tyvars1,tyvars2)))
| _ => raise ActionError 161,
fn ([PAT (pat,pE,tyvars)],opts) => PATLIST ([pat],pE,tyvars)
| _ => raise ActionError 162,
fn ([PATLIST (l,pVE1,tyvars1), PAT (pat,pVE2,tyvars2)],opts) =>
PATLIST (pat::l,merge_pVEs(opts,pVE1,pVE2),Set.union (tyvars1,tyvars2))
| _ => raise ActionError 163,
fn ([LONGIDLIST l,LONGID id],opts) => LONGIDLIST(id :: l)
| _ => raise ActionError 164,
fn ([LONGID id],opts) => LONGIDLIST[id]
| _ => raise ActionError 165,
fn ([LONGSTRIDLIST l, LONGSTRID i],opts) => LONGSTRIDLIST(i::l)
| _ => raise ActionError 166,
fn ([LONGSTRID i],opts) => LONGSTRIDLIST[i]
| _ => raise ActionError 167,
fn ([SYM sym],opts) => SYMLIST [sym]
| _ => raise ActionError 168,
fn ([SYMLIST l,SYM sym],opts) => SYMLIST (sym::l)
| _ => raise ActionError 169,
fn ([INTEGER s],opts) => SCON (Ident.INT(s,get_location opts))
| _ => raise ActionError 170,
fn ([REAL s],opts) => SCON (Ident.REAL (s,get_location opts))
| _ => raise ActionError 171,
fn ([STRING s],opts) => SCON (Ident.STRING s)
| _ => raise ActionError 172,
fn ([CHAR s],opts) => SCON (Ident.CHAR s)
| _ => raise ActionError 173,
fn ([WORD s],opts) => SCON (Ident.WORD(s, get_location opts))
| _ => raise ActionError 174,
fn ([LONGID ([],s)],opts) => LAB (Ident.LAB s)
| ([LONGID (id as (p,s))],opts) =>
(report_long_error (opts,id,"record label"); LAB (Ident.LAB s))
| _ => raise ActionError 175,
fn ([INTEGER int],opts) => (check_integer_bounds (opts,int); LAB (Ident.LAB (Symbol.find_symbol int)))
| _ => raise ActionError 176,
fn ([LONGID id],opts) => LONGVALID (resolveLongValId(opts,id))
| _ => raise ActionError 177,
fn ([l as LONGVALID (id,_)],opts) => (check_non_longid_op (opts,id); l)
| _ => raise ActionError 178,
fn ([_, l as LONGVALID (id,_)],opts) => (check_longid_op (opts,id); l)
| _ => raise ActionError 179,
fn ([LONGID (l,s)],opts) => LONGTYCON (mkLongTyCon (opts,l,s))
| _ => raise ActionError 180,
fn ([LONGID (l,s)],opts) => LONGSTRID (mkLongStrId (l,s))
| _ => raise ActionError 181,
fn ([LONGID ([],s)],opts) => SYM s
| ([LONGID (id as (l,s))],opts) =>
(report_long_error (opts,id,"symbol"); SYM s)
| _ => raise ActionError 182,
fn ([_],opts) => SYM equal_symbol
| _ => raise ActionError 183,
fn ([LONGID ([],s)],opts) => SYM s
| ([LONGID (id as (l,s))],opts) =>
(report_long_error (opts,id,"symbol"); SYM s)
| _ => raise ActionError 184,
fn ([SYM s],opts) => FUNID(Ident.FUNID s)
| _ => raise ActionError 185,
fn ([SYM s],opts) => SIGID(Ident.SIGID s)
| _ => raise ActionError 186,
fn ([SYM s],opts) => STRID(Ident.STRID s)
| _ => raise ActionError 187,
fn ([LONGID (longid as ([],id))],opts) => LONGVALID (Ident.LONGVALID(Ident.NOPATH,resolveValId id),NONE)
| ([LONGID (longid as (_,id))],opts) => (report_long_error(opts,longid,"variable");LONGVALID (Ident.LONGVALID(Ident.NOPATH,Ident.VAR id),NONE))
| _ => raise ActionError 188,
fn ([LONGID ([],id)],opts) =>
let val valid = resolveValId id
in if get_old_definition opts
then check_not_short_constructor (opts,valid)
else (); VALID valid
end
| ([LONGID (longid as (_,id))],opts) =>
(report_long_error (opts,longid,"variable");
VALID (Ident.VAR id))
| _ => raise ActionError 189,
fn ([valid as VALID id],opts) =>
(check_non_valid_op (opts,id); valid)
| _ => raise ActionError 190,
fn ([_,valid as VALID id],opts) =>
(check_valid_op(opts,id); valid)
| _ => raise ActionError 191,
fn ([LONGID (id as ([],_))],opts) => VALID (getValId id)
| ([LONGID (longid as (_,id))],opts) => (report_long_error (opts,longid,"variable"); VALID (Ident.VAR id))
| _ => raise ActionError 192,
fn ([LONGID ([],s)],opts) => VALID (Ident.CON s)
| ([LONGID (id as (p,s))],opts) => (report_long_error (opts,id,"constructor"); VALID (Ident.CON s))
| _ => raise ActionError 193,
fn ([valid as (VALID con)],opts) => (check_non_conid_op(opts,con); valid)
| _ => raise ActionError 194,
fn ([_,valid as (VALID con)],opts) => (check_conid_op(opts,con); valid)
| _ => raise ActionError 195,
fn ([LONGID ([],s)],opts) => VALID (Ident.EXCON s)
| ([LONGID (id as (p,s))],opts) => (report_long_error (opts,id,"exception constructor"); VALID (Ident.EXCON s))
| _ => raise ActionError 196,
fn ([valid as (VALID excon)],opts) => (check_non_conid_op(opts,excon); valid)
| _ => raise ActionError 197,
fn ([_,valid as (VALID excon)],opts) => (check_conid_op(opts,excon); valid)
| _ => raise ActionError 198,
fn ([LONGID ([],s)],opts) => TYCON (mkTyCon (opts,s))
| ([LONGID (id as (l,s))],opts) => (report_long_error (opts,id,"type constructor"); TYCON (mkTyCon(opts,s)))
| _ => raise ActionError 199,
fn ([LONGID ([],s)],opts) => DUMMY
| _ => raise ActionError 200,
fn ([ENV pE,STRDEC (x,e),_],opts) =>
(set_pE pE;
STREXP (Absyn.NEWstrexp x,e))
| _ => raise ActionError 201,
fn ([LONGID x],opts) =>
STREXP (Absyn.OLDstrexp (mkLongStrId x,get_location opts,
if generate_moduler opts then SOME(ref(NONE))
else NONE),lookupStrId(opts,x))
| _ => raise ActionError 202,
fn ([FUNID funid,_,STREXP(strexp,_),_],opts) =>
STREXP (Absyn.APPstrexp (funid,strexp,ref false,get_location opts,if generate_moduler opts then SOME(ref(Absyn.nullDebuggerStr)) else NONE),lookupFunId (opts,funid))
| _ => raise ActionError 203,
fn ([FUNID funid,_,STRDEC(strdec,e),_],opts) =>
STREXP (Absyn.APPstrexp (funid,Derived.make_strexp strdec,ref false,get_location opts,if generate_moduler opts then SOME(ref(Absyn.nullDebuggerStr)) else NONE),lookupFunId (opts,funid))
| _ => raise ActionError 204,
fn ([ENV pE,STRDEC(strdec,e),_,STREXP(strexp,e'),_],opts) =>
(set_pE pE; STREXP (Absyn.LOCALstrexp(strdec,strexp),e'))
| _ => raise ActionError 205,
fn ([STREXP (strexp,e),BOOL abs,SIGEXP (sigexp,(e',tycons))],opts) =>
STREXP (Absyn.CONSTRAINTstrexp (strexp,sigexp,abs,ref false,get_location opts),e')
| _ => raise ActionError 206,
fn ([_],opts) => ENV (get_current_pE())
| _ => raise ActionError 207,
fn ([_],opts) => STRDEC (Absyn.SEQUENCEstrdec[],PE.empty_pE)
| _ => raise ActionError 208,
fn ([_,strdec,_],opts) => strdec
| _ => raise ActionError 209,
fn ([_,STRDECLIST(l,pE),_],opts) => STRDEC(Absyn.SEQUENCEstrdec(rev l),pE)
| _ => raise ActionError 210,
fn ([STRDEC(strdec1,pE1),_,STRDEC(strdec2,pE2)],opts) => STRDECLIST([strdec2,strdec1],PE.augment_pE(pE1,pE2))
| _ => raise ActionError 211,
fn ([STRDECLIST(l,pE1),_,STRDEC(strdec,pE2)],opts) => STRDECLIST(strdec::l,PE.augment_pE(pE1,pE2))
| _ => raise ActionError 212,
fn ([STRDEC (x,pE)],opts) => STRDEC(Absyn.SEQUENCEstrdec[x],pE)
| _ => raise ActionError 213,
fn ([STRDECLIST(l,pE)],opts) => STRDEC(Absyn.SEQUENCEstrdec(rev l),pE)
| _ => raise ActionError 214,
fn ([STRDEC(strdec1,pE1),STRDEC(strdec2,pE2)],opts) =>
STRDECLIST([strdec2,strdec1],PE.augment_pE(pE1,pE2))
| _ => raise ActionError 215,
fn ([STRDECLIST(l,pE1),STRDEC(strdec,pE2)],opts) =>
STRDECLIST(strdec::l,PE.augment_pE(pE1,pE2))
| _ => raise ActionError 216,
fn ([DEC(dec,pE,tyvars)],opts) => STRDEC(Absyn.DECstrdec dec,pE)
| _ => raise ActionError 217,
fn ([_,STRBIND(l,pSE)],opts) =>
(extend_pSE pSE;STRDEC(Absyn.STRUCTUREstrdec l,pSE_in_pE pSE))
| _ => raise ActionError 218,
fn ([_,STRBIND(l,pSE)],opts) =>
(extend_pSE pSE;STRDEC(Absyn.ABSTRACTIONstrdec (map do_abstraction l),pSE_in_pE pSE))
| _ => raise ActionError 219,
fn ([ENV pE,STRDEC(strdec1,_),_,STRDEC(strdec2,pE'),_],opts) =>
(set_pE (PE.augment_pE(pE,pE'));STRDEC(Absyn.LOCALstrdec(strdec1,strdec2),pE'))
| _ => raise ActionError 220,
fn ([_],opts) => BOOL false
| _ => raise ActionError 221,
fn ([_],opts) => BOOL true
| _ => raise ActionError 222,
fn ([STRBIND1 (d,(id,pE))],opts) => STRBIND([d],make_pSE (opts,id,pE))
| _ => raise ActionError 223,
fn ([STRBIND(l,pSE),_,STRBIND1(d,id_pE)],opts) =>
STRBIND(d::l,addNewStrId(opts,id_pE,pSE))
| _ => raise ActionError 224,
fn ([STRID id,BOOL abs,SIGEXP(sigexp,(pE1,tycons)),_,
STREXP(strexp,pE2)],opts) =>
STRBIND1((id,NONE,
Absyn.CONSTRAINTstrexp
(strexp,sigexp,abs,ref false,get_location opts),
ref false,
get_location opts,
if generate_moduler opts
then SOME(ref(Absyn.nullDebuggerStr))
else NONE,
if generate_moduler opts
then SOME(ref(NONE))
else NONE),(id,pE1))
| _ => raise ActionError 225,
fn ([STRID id,_,STREXP(strexp,pE)],opts) =>
STRBIND1((id,NONE,strexp,ref false,
get_location opts,
if generate_moduler opts
then SOME(ref(Absyn.nullDebuggerStr))
else NONE,
if generate_moduler opts
then SOME(ref(NONE))
else NONE),(id,pE))
| _ => raise ActionError 226,
fn ([ENV pE,SPEC (spec,(e,tycons)),_],opts) =>
(set_pE pE; SIGEXP(Absyn.NEWsigexp(spec,ref NONE),(e,tycons)))
| _ => raise ActionError 227,
fn ([SIGID id],opts) => SIGEXP(Absyn.OLDsigexp(id,ref NONE,get_location opts),lookupSigId(opts,id))
| _ => raise ActionError 228,
fn ([SIGEXP (sigexp,e),_,_,LONGTYPBIND tybind],opts) =>
SIGEXP (Absyn.WHEREsigexp (sigexp,tybind),e)
| _ => raise ActionError 229,
fn ([_],opts) => let val pE = get_current_pE() in set_pE (zap_for_sig pE); ENV pE end
| _ => raise ActionError 230,
fn ([SIGBIND(sigb,pG)],opts) => SIGDEC([Absyn.SIGBIND sigb],pG)
| _ => raise ActionError 231,
fn ([SIGDEC(l,pG),SIGBIND(sigb,pG')],opts) => SIGDEC((Absyn.SIGBIND sigb)::l,PE.augment_pG(pG,pG'))
| _ => raise ActionError 232,
fn ([_,x as SIGBIND(_,pG)],opts) => (extend_pG pG;x)
| _ => raise ActionError 233,
fn ([SIGID id,_,SIGEXP(sigexp,(e,tycons))],opts) => SIGBIND([(id,sigexp,get_location opts)],make_pG(id,e,tycons))
| _ => raise ActionError 234,
fn ([SIGBIND(l,pG),_,SIGID id,_,SIGEXP(sigexp,(e,tycons))],opts) =>
SIGBIND((id,sigexp,get_location opts)::l,addNewSigId(opts,(id,e,tycons),pG))
| _ => raise ActionError 235,
fn ([sb as SIGBIND(l,pG),_,_,LONGTYPBIND tybind],opts) =>
( case l of
(id, wsigexp as Absyn.WHEREsigexp _, loc) :: t
=> let val new_sigexp = Absyn.WHEREsigexp(wsigexp, tybind)
in SIGBIND((id, new_sigexp, loc) :: t, pG)
end
| _ => (error(opts, "Unexpected occurrence of \"and type\""); sb) )
| _ => raise ActionError 236,
fn ([],opts) => SPEC(Absyn.SEQUENCEspec [],(PE.empty_pE,[]))
| _ => raise ActionError 237,
fn ([SPEC (spec1,specstuff1),SPEC (spec2,specstuff2)],opts) =>
SPEC (combine_specs (spec1,spec2),spec_augment_pE (specstuff1,specstuff2,opts))
| _ => raise ActionError 238,
fn ([SPEC (spec,(pE,tycons)),_,SHAREQ l],opts) =>
SPEC(Absyn.SHARINGspec (spec,rev l),(pE,tycons))
| _ => raise ActionError 239,
fn ([_],opts) => (SPEC (Absyn.SEQUENCEspec [],(PE.empty_pE,[])))
| _ => raise ActionError 240,
fn ([_,VALDESC (l,pVE)],opts) => SPEC(Absyn.VALspec (rev l,get_location opts),(pVE_in_pE pVE,[]))
| _ => raise ActionError 241,
fn ([_,TYPDESC t],opts) => SPEC(do_type_spec (false,rev t,opts),(PE.empty_pE,names_of_typedesc t))
| _ => raise ActionError 242,
fn ([_,TYPDESC t],opts) => SPEC(do_type_spec (true,rev t,opts),(PE.empty_pE,names_of_typedesc t))
| _ => raise ActionError 243,
fn ([_,DATREPLDESC(tyvars,tycon,longtycon)],opts) =>
(check_is_new_definition(opts,
"Datatype replication is not a feature of SML'90.");
check_empty_tyvars(opts,tyvars);
let val pVE = lookupLongTycon longtycon
val pTE = (make_pTE (tycon,pVE))
in
extend_pVE pVE;
extend_pTE pTE;
SPEC(Absyn.DATATYPEreplSpec(get_location opts,tycon,longtycon,
ref NONE),
(pVEpTE_in_pE(pVE,pTE), nil))
end)
| _ => raise ActionError 244,
fn ([_,DATDESC(l,pVE,pTE)],opts) =>
(extend_pVE pVE;
extend_pTE pTE;
SPEC(Absyn.DATATYPEspec (rev l), (pVEpTE_in_pE (pVE,pTE),
names_of_datdesc l)))
| _ => raise ActionError 245,
fn ([_,EXDESC(l,pVE)],opts) => (extend_pVE pVE;SPEC(Absyn.EXCEPTIONspec (rev l),(pVE_in_pE pVE,[])))
| _ => raise ActionError 246,
fn ([_,STRDESC(l,pSE)],opts) => (extend_pSE pSE;SPEC(Absyn.STRUCTUREspec (rev l),(pSE_in_pE pSE,[])))
| _ => raise ActionError 247,
fn ([ENV pE,SPEC(spec1,(pE1,tycons1)),_,SPEC(spec2,(pE2,tycons2)),_],opts) =>
(check_is_old_definition (opts,"local specification no longer in language");
set_pE (PE.augment_pE(pE,pE2));SPEC(Absyn.LOCALspec(spec1,spec2),(pE2,tycons2)))
| _ => raise ActionError 248,
fn ([_,LONGIDLIST l],opts) =>
(check_is_old_definition (opts,"open specification no longer in language");
do_open_spec_action (opts,rev l))
| _ => raise ActionError 249,
fn ([_,SIGIDLIST l],opts) => do_include_action (opts,rev l)
| _ => raise ActionError 250,
fn ([_,SIGEXP (e,(pE,tycons))],opts) =>
SPEC (Absyn.INCLUDEspec (e,get_location opts),(pE,tycons))
| _ => raise ActionError 251,
fn ([_,SYMLIST l],opts) => do_fixity_spec (l,PE.LEFT 0,opts)
| _ => raise ActionError 252,
fn ([_,INTEGER i,SYMLIST l],opts) => do_fixity_spec(l,PE.LEFT(parse_precedence (opts,i)),opts)
| _ => raise ActionError 253,
fn ([_,SYMLIST l],opts) => do_fixity_spec (l,PE.RIGHT 0,opts)
| _ => raise ActionError 254,
fn ([_,INTEGER i,SYMLIST l],opts) => do_fixity_spec(l,PE.RIGHT(parse_precedence (opts,i)),opts)
| _ => raise ActionError 255,
fn ([SIGID sigid],opts) => SIGIDLIST [sigid]
| _ => raise ActionError 256,
fn ([SIGIDLIST l,SIGID sigid],opts) => SIGIDLIST(sigid::l)
| _ => raise ActionError 257,
fn ([VALID v,_,TY (ty,tyvars)],opts) => VALDESC([(v,ty,tyvars)], make_pVE v)
| _ => raise ActionError 258,
fn ([VALDESC(l,pVE),_,VALID v,_,TY (ty,tyvars)],opts) =>
VALDESC((v,ty,tyvars)::l,addNewValId(opts,v,pVE))
| _ => raise ActionError 259,
fn ([x],opts) => x
| _ => raise ActionError 260,
fn ([TYPDESC l,_,TYPDESC [t]],opts) =>
(check_disjoint_typdesc(opts,t,l); TYPDESC (t::l))
| _ => raise ActionError 261,
fn ([TYVARLIST tyvarlist,TYCON tycon],opts) =>
TYPDESC[(rev tyvarlist,tycon,NONE)]
| _ => raise ActionError 262,
fn ([TYVARLIST tyvarlist,TYCON tycon,_,TY (ty,tyvarset)],opts) =>
TYPDESC[(rev tyvarlist,tycon,SOME (ty,tyvarset))]
| _ => raise ActionError 263,
fn ([DATAHDR(tyvars,tycon),_,LONGTYCON longtycon],opts) =>
DATREPLDESC(tyvars,tycon,longtycon)
| _ => raise ActionError 264,
fn ([DATDESC1(d,pVE,pTE)],opts) =>
DATDESC([d],pVE,pTE)
| _ => raise ActionError 265,
fn ([DATDESC(l,pVE,pTE),_,DATDESC1(d,pVE',pTE')],opts) =>
(check_disjoint_datdesc(opts,d,l);
DATDESC(d::l, merge_pVEs(opts,pVE,pVE'), merge_pTEs(opts,pTE,pTE')))
| _ => raise ActionError 266,
fn ([DATAHDR(tyvarlist,tycon),CONDESC (l,pVE,tyvars)],opts) =>
(check_tyvar_inclusion(opts,tyvars,tyvarlist);
DATDESC1((rev tyvarlist,tycon,(rev l)), pVE, make_pTE (tycon,pVE)))
| _ => raise ActionError 267,
fn ([x],opts) => x
| _ => raise ActionError 268,
fn ([CONDESC (l,e,tyvars),_,CONDESC ([x],e',tyvars')],opts) =>
CONDESC(x::l,merge_pVEs(opts,e,e'),Set.union(tyvars,tyvars'))
| _ => raise ActionError 269,
fn ([VALID con,TY(ty,tyvars)],opts) =>
CONDESC([(con,SOME ty,get_location opts)],make_pVE con,tyvars)
| ([VALID con,NULLTYPE],opts) =>
CONDESC([(con,NONE,get_location opts)],make_pVE con,Set.empty_set)
| _ => raise ActionError 270,
fn ([EXDESC1(excon,ty,marks)],opts) => EXDESC([(excon,ty,marks)],make_pVE(excon))
| _ => raise ActionError 271,
fn ([EXDESC(l,pVE),_,EXDESC1(excon,ty,marks)],opts) =>
EXDESC((excon,ty,marks)::l,addNewValId(opts,excon,pVE))
| _ => raise ActionError 272,
fn ([VALID excon,TY(ty,_)],opts) =>
EXDESC1(excon,SOME ty,get_location opts)
| ([VALID excon,NULLTYPE],opts) =>
EXDESC1(excon,NONE,get_location opts)
| _ => raise ActionError 273,
fn ([STRDESC1(strid,e,pE)],opts) => STRDESC([(strid,e)],make_pSE(opts,strid,pE))
| _ => raise ActionError 274,
fn ([STRDESC(l,pSE),_,STRDESC1(strid,e,pE)],opts) =>
STRDESC((strid,e)::l,addNewStrId(opts,(strid,pE),pSE))
| _ => raise ActionError 275,
fn ([STRID strid,_,SIGEXP(e,(pE,tycons))],opts) => STRDESC1(strid,e,pE)
| _ => raise ActionError 276,
fn ([x],opts) => x
| _ => raise ActionError 277,
fn ([SHAREQ l,_,SHAREQ [x]],opts) => SHAREQ(x::l)
| _ => raise ActionError 278,
fn ([LONGSTRIDLIST l],opts) => SHAREQ[(Absyn.STRUCTUREshareq (rev l),get_location opts)]
| _ => raise ActionError 279,
fn ([_, LONGTYCONLIST l],opts) => SHAREQ[(Absyn.TYPEshareq (rev l),get_location opts)]
| _ => raise ActionError 280,
fn ([LONGSTRID id,_,LONGSTRID id'],opts) => LONGSTRIDLIST[id',id]
| _ => raise ActionError 281,
fn ([LONGSTRIDLIST l,_,LONGSTRID id],opts) => LONGSTRIDLIST(id::l)
| _ => raise ActionError 282,
fn ([LONGTYCON tycon,_,LONGTYCON tycon'],opts) => LONGTYCONLIST[tycon',tycon]
| _ => raise ActionError 283,
fn ([LONGTYCONLIST l,_,LONGTYCON tycon],opts) => LONGTYCONLIST(tycon::l)
| _ => raise ActionError 284,
fn ([FUNBIND(fbind,pF)],opts) => FUNDEC([Absyn.FUNBIND fbind],pF)
| _ => raise ActionError 285,
fn ([FUNDEC(l,pF),FUNBIND(fbind,pF')],opts) => FUNDEC((Absyn.FUNBIND fbind)::l,PE.augment_pF(pF,pF'))
| _ => raise ActionError 286,
fn ([_,fbind as FUNBIND(f,pF)],opts) => (extend_pF pF; fbind)
| _ => raise ActionError 287,
fn ([FUNBIND1(fbind,funid,pE)],opts) =>
FUNBIND([fbind],make_pF(funid,pE))
| _ => raise ActionError 288,
fn ([FUNBIND(l,pF),_,FUNBIND1(fbind,funid,pE)],opts) =>
FUNBIND (fbind::l,addNewFunId(opts,(funid,pE),pF))
| _ => raise ActionError 289,
fn ([STARTFUNBIND1(funid,pE,strid,sigexp,pE'),BOOL abs,SIGEXP(sigexp',(pE'',tycons)),_,STREXP(strexp,pE''')],opts) =>
(set_pE pE;
FUNBIND1((funid,
strid,
sigexp,
Absyn.CONSTRAINTstrexp (strexp,sigexp',abs,ref false,get_location opts),
NONE,
make_funbind_info(opts,funid),
ref false,
get_location opts,
if generate_moduler opts then SOME (ref(Absyn.nullDebuggerStr)) else NONE,
if generate_moduler opts then SOME (ref(NONE)) else NONE),
funid,pE''))
| _ => raise ActionError 290,
fn ([STARTFUNBIND1(funid,pE,strid,sigexp,pE'),_,STREXP(strexp,pE''')],opts) =>
(set_pE pE;
FUNBIND1((funid,
strid,
sigexp,
strexp,
NONE,
make_funbind_info (opts,funid),
ref false,
get_location opts,
if generate_moduler opts then SOME (ref (Absyn.nullDebuggerStr)) else NONE,
if generate_moduler opts then SOME (ref (NONE)) else NONE),
funid,pE'''))
| _ => raise ActionError 291,
fn ([STARTFUNBIND2(funid,pE,spec,pE'),BOOL abs,SIGEXP(sigexp,(pE'',tycons)),_,STREXP(strexp,pE''')],opts) =>
(set_pE pE;do_derived_funbind (opts,funid,spec,NONE,Absyn.CONSTRAINTstrexp (strexp,sigexp,abs,ref false,get_location opts),pE''))
| _ => raise ActionError 292,
fn ([STARTFUNBIND2(funid,pE,spec,pE'),_,STREXP(strexp,pE''')],opts) =>
(set_pE pE;do_derived_funbind (opts,funid,spec,NONE,strexp,pE'''))
| _ => raise ActionError 293,
fn ([FUNIDBIND (funid,pE),_,STRID strid,_,SIGEXP(sigexp,(pE',tycons)),_],opts) =>
(extend_pSE(make_pSE(opts,strid,pE'));STARTFUNBIND1(funid,pE,strid,sigexp,pE'))
| _ => raise ActionError 294,
fn ([FUNIDBIND (funid,pE),_,SPEC(spec,(pE',tycons)),_],opts) =>
(extend_pE pE'; STARTFUNBIND2(funid,pE,spec,pE'))
| _ => raise ActionError 295,
fn ([FUNID funid],opts) => FUNIDBIND(funid,get_current_pE())
| _ => raise ActionError 296,
fn ([STRDEC(strdec,pE)],opts) => TOPDEC (Absyn.STRDECtopdec(strdec,get_location opts),pE_in_pB pE)
| _ => raise ActionError 297,
fn ([SIGDEC(l,pG)],opts) => TOPDEC (Absyn.SIGNATUREtopdec (rev l,get_location opts),pG_in_pB pG)
| _ => raise ActionError 298,
fn ([FUNDEC(l,pF)],opts) => TOPDEC (Absyn.FUNCTORtopdec(rev l,get_location opts),pF_in_pB pF)
| _ => raise ActionError 299,
fn ([EXP(exp,tyvars)],opts) =>
TOPDEC (Derived.make_it_strdec(exp,tyvars,get_location opts,get_current_pE()),PE.empty_pB)
| _ => raise ActionError 300,
fn ([_,STRING s],opts) => TOPDEC (Absyn.REQUIREtopdec (s, get_location opts),PE.empty_pB)
| _ => raise ActionError 301,
fn ([TOPDEC x],opts) => raise FoundTopDec x
| _ => raise ActionError 302,
fn ([x,_,_],opts) => x
| _ => raise ActionError 303,
fn ([_,x],opts) => x
| _ => raise ActionError 304,
fn ([x,_],opts) => x
| _ => raise ActionError 305,
fn ([_],opts) => raise (FoundTopDec (Absyn.STRDECtopdec(Absyn.SEQUENCEstrdec [],get_location opts),PE.empty_pB))
| _ => raise ActionError 306,
fn ([],opts) => DUMMY
| _ => raise ActionError 307,
fn _ => DUMMY
]
fun get_function n =
MLWorks.Internal.Array.sub (actions,n)
type FinalValue = Absyn.TopDec
exception FinalValue
val dummy = DUMMY
fun get_final_value (TOPDEC x) = x
| get_final_value _ = raise FinalValue
exception ResolveError of string
fun ifInfixInput _ (act1,act2, [], LONGID ([],s)) =
(debug ("Checking infix for " ^ Symbol.symbol_name s);
if is_infix s then act1 else act2)
| ifInfixInput _ (act1,act2,[], EQVAL) =
(debug "Checking infix for =";
if is_infix equal_symbol then act1 else act2)
| ifInfixInput _ (act1,act2,_,_) = act2
fun ifVarStack oldDefinition (varred,conred,[LONGID (id as ([],s))],_) =
(case lookupValId id of
SOME (Ident.VAR _) => varred
| SOME (Ident.CON _) => if oldDefinition then conred else varred
| SOME _ => conred
| NONE => varred)
| ifVarStack _ (_,conred,_,_) = conred
fun ifStarStack _ (starred,tyconred,[LONGID (id as ([],s))],_) =
if Symbol.eq_symbol(s,asterisk_symbol) then starred else tyconred
| ifStarStack _ (starred,tyconred,_,_) = tyconred
fun ifStarInput _ (staract,otheract,[],LONGID ([],s)) =
if Symbol.eq_symbol(s,asterisk_symbol) then staract else otheract
| ifStarInput _ (_,otheract,_,_) = otheract
fun get_precedence s =
case PE.lookupFixity (s, !ref_pE) of
PE.LEFT n => n
| PE.RIGHT n => n
| _ => 0
fun associate_left s =
case PE.lookupFixity (s, !ref_pE) of
PE.LEFT n => true
| PE.RIGHT n => false
| _ => false
fun get_associativity(old_definition, s1, s2) =
(
if Symbol.eq_symbol(s1,s2)
then associate_left s1
else
let
val p1 = get_precedence s1
val p2 = get_precedence s2
in
if p1 <> p2 then
p1 > p2
else
if old_definition then true
else associate_left s1
end)
fun ifLeftAssoc old_definition (act1,act2,[LONGVALID (lvalid,_),_], LONGID ([],s)) =
let
val Ident.LONGVALID (_,valid) = lvalid
in
if get_associativity (old_definition, get_sym valid, s) then act1 else act2
end
| ifLeftAssoc old_definition (act1,act2, [LONGVALID (lvalid,_),_], EQVAL) =
let
val Ident.LONGVALID (_,valid) = lvalid
in
if get_associativity (old_definition, get_sym valid,equal_symbol) then act1 else act2
end
| ifLeftAssoc _ (act1,act2,[LONGVALID (lvalid,_),_], _) = act1
| ifLeftAssoc _ _ = raise ResolveError "Error in precedence"
type Act = LRbasics.Action
val resolutions : (bool -> Act * Act * Parsed_Object list * Parsed_Object -> Act) MLWorks.Internal.Array.array =
MLWorks.Internal.Array.arrayoflist[ifInfixInput,
ifVarStack,
ifStarStack,
ifStarInput,
ifLeftAssoc
]
fun get_resolution (n, Options.OPTIONS{compat_options =
Options.COMPATOPTIONS{old_definition, ...}, ...}) =
MLWorks.Internal.Array.sub(resolutions,n) old_definition
exception UnexpectedIgnore
fun token_to_parsed_object (weak_tyvars, token) =
case token of
Token.RESERVED (x) =>
(case x of
Token.ABSTYPE => (LRbasics.ABSTYPE,DUMMY)
| Token.ABSTRACTION => (LRbasics.ABSTRACTION,DUMMY)
| Token.AND => (LRbasics.AND,DUMMY)
| Token.ANDALSO => (LRbasics.ANDALSO,DUMMY)
| Token.AS => (LRbasics.AS,DUMMY)
| Token.CASE => (LRbasics.CASE,DUMMY)
| Token.DO => (LRbasics.DO,DUMMY)
| Token.DATATYPE => (LRbasics.DATATYPE,DUMMY)
| Token.ELSE => (LRbasics.ELSE,DUMMY)
| Token.END => (LRbasics.END,DUMMY)
| Token.EXCEPTION => (LRbasics.EXCEPTION,DUMMY)
| Token.FN => (LRbasics.FN,DUMMY)
| Token.FUN => (LRbasics.FUN,DUMMY)
| Token.HANDLE => (LRbasics.HANDLE,DUMMY)
| Token.IF => (LRbasics.IF,DUMMY)
| Token.IN => (LRbasics.IN,DUMMY)
| Token.INFIX => (LRbasics.INFIX,DUMMY)
| Token.INFIXR => (LRbasics.INFIXR,DUMMY)
| Token.LET => (LRbasics.LET,DUMMY)
| Token.LOCAL => (LRbasics.LOCAL,DUMMY)
| Token.NONFIX => (LRbasics.NONFIX,DUMMY)
| Token.OF => (LRbasics.OF,DUMMY)
| Token.OP => (LRbasics.OP,DUMMY)
| Token.OPEN => (LRbasics.OPEN,DUMMY)
| Token.ORELSE => (LRbasics.ORELSE,DUMMY)
| Token.RAISE => (LRbasics.RAISE,DUMMY)
| Token.REC => (LRbasics.REC,DUMMY)
| Token.REQUIRE => (LRbasics.REQUIRE,DUMMY)
| Token.THEN => (LRbasics.THEN,DUMMY)
| Token.TYPE => (LRbasics.TYPE,DUMMY)
| Token.VAL => (LRbasics.VAL,DUMMY)
| Token.WITH => (LRbasics.WITH,DUMMY)
| Token.WITHTYPE => (LRbasics.WITHTYPE,DUMMY)
| Token.WHERE => (LRbasics.WHERE,DUMMY)
| Token.WHILE => (LRbasics.WHILE,DUMMY)
| Token.EQTYPE => (LRbasics.EQTYPE,DUMMY)
| Token.FUNCTOR => (LRbasics.FUNCTOR,DUMMY)
| Token.INCLUDE => (LRbasics.INCLUDE,DUMMY)
| Token.SHARING => (LRbasics.SHARING,DUMMY)
| Token.SIG => (LRbasics.SIG,DUMMY)
| Token.SIGNATURE => (LRbasics.SIGNATURE,DUMMY)
| Token.STRUCT => (LRbasics.STRUCT,DUMMY)
| Token.STRUCTURE => (LRbasics.STRUCTURE,DUMMY)
| Token.LPAR => (LRbasics.LPAR,DUMMY)
| Token.RPAR => (LRbasics.RPAR,DUMMY)
| Token.BRA => (LRbasics.BRA,DUMMY)
| Token.KET => (LRbasics.KET,DUMMY)
| Token.LBRACE => (LRbasics.LBRACE,DUMMY)
| Token.RBRACE => (LRbasics.RBRACE,DUMMY)
| Token.COMMA => (LRbasics.COMMA,DUMMY)
| Token.COLON => (LRbasics.COLON,DUMMY)
| Token.ABSCOLON => (LRbasics.ABSCOLON,DUMMY)
| Token.SEMICOLON => (LRbasics.SEMICOLON,DUMMY)
| Token.ELLIPSIS => (LRbasics.ELLIPSIS,DUMMY)
| Token.UNDERBAR => (LRbasics.UNDERBAR,DUMMY)
| Token.VBAR => (LRbasics.VBAR,DUMMY)
| Token.EQUAL => (LRbasics.EQUAL,EQVAL)
| Token.DARROW => (LRbasics.DARROW,DUMMY)
| Token.ARROW => (LRbasics.ARROW,DUMMY)
| Token.HASH => (LRbasics.HASH,DUMMY)
| Token.MAGICOPEN => (LRbasics.MAGICOPEN,DUMMY))
| Token.EOF _ => (LRbasics.EOF,DUMMY)
| Token.INTEGER s => (LRbasics.INTEGER,INTEGER s)
| Token.REAL s => (LRbasics.REAL,REAL s)
| Token.STRING s => (LRbasics.STRING, STRING s)
| Token.CHAR s => (LRbasics.CHAR, CHAR s)
| Token.WORD s => (LRbasics.WORD, WORD s)
| Token.LONGID (l,s) => (LRbasics.LONGID,LONGID (l,s))
| Token.TYVAR t =>
if weak_tyvars then
let
val (sy, eq, imp) = t
val string = Symbol.symbol_name sy
val len = size string
val weak =
if len <= 1 orelse (eq andalso len <= 2) then
false
else
let
val weak_char =
MLWorks.String.ordof(string, if eq then 2 else 1)
in
ord #"1" <= weak_char andalso ord #"9" >= weak_char
end
in
(LRbasics.TYVAR, TYVAR (Ident.TYVAR(if weak then (sy, eq, true) else t)))
end
else
(LRbasics.TYVAR, TYVAR (Ident.TYVAR t))
| Token.IGNORE => raise UnexpectedIgnore
end;
