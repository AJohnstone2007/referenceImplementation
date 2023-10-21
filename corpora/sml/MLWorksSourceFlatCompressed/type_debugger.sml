require "../basics/absyn";
require "../main/options";
signature TYPE_DEBUGGER =
sig
structure Options : OPTIONS
structure Absyn: ABSYN
val gather_vartypes :
Absyn.TopDec ->
(Absyn.Ident.ValId * Absyn.Type * Absyn.Ident.Location.T) list
val print_vartypes :
Options.options ->
(Absyn.Ident.ValId * Absyn.Type * Absyn.Ident.Location.T) list -> unit
end
;
