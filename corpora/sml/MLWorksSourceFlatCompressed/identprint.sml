require "../main/options";
require "ident";
signature IDENTPRINT =
sig
structure Ident : IDENT
structure Options : OPTIONS
val printValId : Options.print_options -> Ident.ValId -> string
val debug_printValId : Ident.ValId -> string
val printTyVar : Ident.TyVar -> string
val printTyCon : Ident.TyCon -> string
val printLab : Ident.Lab -> string
val printStrId : Ident.StrId -> string
val printSigId : Ident.SigId -> string
val printFunId : Ident.FunId -> string
val printPath : Ident.Path -> string
val printLongValId : Options.print_options -> Ident.LongValId -> string
val printLongTyCon : Ident.LongTyCon -> string
val printLongStrId : Ident.LongStrId -> string
val printSCon : Ident.SCon -> string
val valid_unbound_strid_message :
Ident.StrId * Ident.LongValId * Options.print_options -> string
val strid_unbound_strid_message :
Ident.StrId * Ident.LongStrId * Options.print_options -> string
val tycon_unbound_strid_message :
Ident.StrId * Ident.LongTyCon -> string
val tycon_unbound_flex_strid_message :
Ident.StrId * Ident.LongTyCon -> string
val unbound_longvalid_message :
Ident.ValId * Ident.LongValId * string * Options.print_options -> string
val unbound_longtycon_message :
Ident.TyCon * Ident.LongTyCon -> string
val unbound_flex_longtycon_message :
Ident.TyCon * Ident.LongTyCon -> string
end
;
