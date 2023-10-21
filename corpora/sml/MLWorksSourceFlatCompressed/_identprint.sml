require "../main/options";
require "ident";
require "identprint";
functor IdentPrint (
structure Ident : IDENT
structure Options : OPTIONS
) : IDENTPRINT =
struct
structure Ident = Ident
structure Options = Options
val name = Ident.Symbol.symbol_name
fun appclass show_id_class c s =
if show_id_class then (name s) ^ " <" ^ c ^ ">" else (name s)
fun printValId' show_id_class (Ident.VAR sym) =
appclass show_id_class "VAR" sym
| printValId' show_id_class (Ident.CON sym) =
appclass show_id_class "CON" sym
| printValId' show_id_class (Ident.EXCON sym) =
appclass show_id_class "EXCON" sym
| printValId' show_id_class (Ident.TYCON' sym) =
appclass show_id_class "TYCON'" sym
fun debug_printValId id = printValId' true id
fun printValId (Options.PRINTOPTIONS options) id =
printValId' (#show_id_class options) id
fun printTyVar (Ident.TYVAR(sym,_,_)) =
case name sym
of "int literal" => "int"
| "word literal" => "word"
| "real literal" => "real"
| "wordint" => "int_or_word"
| "realint" => "real_or_int"
| "num" => "num"
| "numtext" => "text_or_num"
| str => str
fun printTyCon (Ident.TYCON sym) = name sym
fun printLab (Ident.LAB sym) = name sym
fun printStrId (Ident.STRID sym) = name sym
fun printSigId (Ident.SIGID sym) = name sym
fun printFunId (Ident.FUNID sym) = name sym
local
val follow =
Ident.followPath (fn (strid,string) =>
string ^ (printStrId strid) ^ ".")
in
fun printPath path = follow (path,"")
end
fun printLongValId options (Ident.LONGVALID (path,valid)) =
printPath path ^ printValId options valid
fun printLongTyCon (Ident.LONGTYCON (path,tycon)) =
printPath path ^ printTyCon tycon
fun printLongStrId (Ident.LONGSTRID (path,strid)) =
printPath path ^ printStrId strid
fun printSCon (Ident.INT (x,_)) = x
| printSCon (Ident.REAL (x,_)) = x
| printSCon (Ident.STRING x) =
"\"" ^ MLWorks.String.ml_string(x,20) ^ "\""
| printSCon (Ident.CHAR x) =
"#\"" ^ MLWorks.String.ml_string(x,20) ^ "\""
| printSCon(Ident.WORD (x, _)) = x
fun valid_unbound_strid_message (strid,lvalid,print_options) =
concat ["Unbound structure ", printStrId strid, " in ",
printLongValId print_options lvalid]
fun tycon_unbound_strid_message (strid,ltycon) =
concat ["Unbound structure ", printStrId strid, " in ",
printLongTyCon ltycon]
fun tycon_unbound_flex_strid_message (strid,ltycon) =
concat ["Unbound flexible structure ", printStrId strid, " in ",
printLongTyCon ltycon]
fun strid_unbound_strid_message (strid,lstrid,print_options) =
concat (["Unbound structure ",printStrId strid] @
(case lstrid of
Ident.LONGSTRID (Ident.NOPATH,_) => []
| _ => [" in ", printLongStrId lstrid]))
fun unbound_longvalid_message (valid,lvalid,class,print_options) =
let
val message = ["Unbound ", class, " ", printValId print_options valid]
in
case lvalid of
Ident.LONGVALID (Ident.NOPATH,_) =>
concat message
| _ => concat (message @ [" in ", printLongValId print_options lvalid])
end
fun unbound_lt_message message =
fn (tycon, ltycon) =>
let
val message = [message, printTyCon tycon]
in
case ltycon of
Ident.LONGTYCON (Ident.NOPATH,_) =>
concat message
| _ => concat (message @ [" in ", printLongTyCon ltycon])
end
val unbound_longtycon_message = unbound_lt_message "Unbound type constructor "
val unbound_flex_longtycon_message = unbound_lt_message "Unbound flexible type constructor "
end
;
