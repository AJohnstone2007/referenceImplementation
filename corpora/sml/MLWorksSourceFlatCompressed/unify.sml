require "../typechecker/datatypes";
signature UNIFY =
sig
structure Datatypes : DATATYPES
type options
type 'a refargs
datatype substitution =
SUBST of ((int * Datatypes.Type * Datatypes.Instance) refargs list *
(int * bool * Datatypes.Type * bool * bool) refargs list *
Datatypes.Type refargs list)
datatype record =
RIGID of (Datatypes.Ident.Lab * Datatypes.Type) list
| FLEX of (Datatypes.Ident.Lab * Datatypes.Type) list
datatype unify_result =
OK
| FAILED of Datatypes.Type * Datatypes.Type
| RECORD_DOMAIN of record * record
| EXPLICIT_TYVAR of Datatypes.Type * Datatypes.Type
| EQ_AND_IMP of bool * bool * Datatypes.Type
| CIRCULARITY of Datatypes.Type * Datatypes.Type
| OVERLOADED of Datatypes.Ident.TyVar * Datatypes.Type
| SUBSTITUTION of substitution
val apply : unify_result * Datatypes.Type -> Datatypes.Type
val unified : options * Datatypes.Type * Datatypes.Type * bool -> unify_result
end;
