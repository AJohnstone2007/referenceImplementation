require "../typechecker/datatypes";
require "../basics/absyn";
signature TYPEREP_UTILS =
sig
structure Datatypes : DATATYPES
structure Absyn : ABSYN
val make_coerce_expression : Absyn.Exp * Datatypes.Type -> Absyn.Exp
exception ConvertDynamicType
val convert_dynamic_type : (bool * Datatypes.Type * int * Absyn.Ident.TyVar Absyn.Set.Set) -> Datatypes.Type
end
;
