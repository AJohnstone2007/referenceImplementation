require "../utils/set";
require "../main/options";
require "../typechecker/datatypes";
signature SCHEME =
sig
structure Set : SET
structure Options : OPTIONS
structure Datatypes : DATATYPES
type error_info
type print_options
exception EnrichError of string
val instantiate : int * Datatypes.Typescheme * Datatypes.Ident.Location.T * bool
-> Datatypes.Type * (Datatypes.InstanceInfo * Datatypes.Instance ref option)
val make_scheme :
(Datatypes.Type list
* (Datatypes.Type * (Datatypes.Instance ref * Datatypes.Instance ref option ref) option))
-> Datatypes.Typescheme
val unary_overloaded_scheme :
Datatypes.Ident.ValId * Datatypes.Ident.TyVar -> Datatypes.Typescheme
val binary_overloaded_scheme :
Datatypes.Ident.ValId * Datatypes.Ident.TyVar -> Datatypes.Typescheme
val predicate_overloaded_scheme :
Datatypes.Ident.ValId * Datatypes.Ident.TyVar -> Datatypes.Typescheme
val equalityp : Datatypes.Typescheme -> bool
val schemify :
(error_info * Options.options * Datatypes.Ident.Location.T) ->
(int * bool * Datatypes.Typescheme * Datatypes.Ident.TyVar Set.Set * bool) ->
Datatypes.Typescheme
val schemify' :
(int * bool * Datatypes.Typescheme * Datatypes.Ident.TyVar Set.Set * bool) ->
Datatypes.Typescheme
val string_scheme : Datatypes.Typescheme -> string
val typescheme_eq : Datatypes.Typescheme * Datatypes.Typescheme -> bool
val check_closure : (bool * Datatypes.Type * int * Datatypes.Ident.TyVar Set.Set) -> bool
exception Mismatch
val SML90_dynamic_generalises: MLWorks.Internal.Dynamic.type_rep
* MLWorks.Internal.Dynamic.type_rep -> bool
val SML96_dynamic_generalises: MLWorks.Internal.Dynamic.type_rep
* MLWorks.Internal.Dynamic.type_rep -> bool
val generalises : bool -> Datatypes.Type * Datatypes.Type -> bool
val generalises_map : bool -> Datatypes.Type * Datatypes.Type -> (int * Datatypes.Type) list
val apply_instantiation : Datatypes.Type * (int * Datatypes.Type) list -> Datatypes.Type
val scheme_generalises :
Options.options ->
Datatypes.Ident.ValId * Datatypes.Env * int * Datatypes.Typescheme * Datatypes.Typescheme ->
bool
val gather_tynames : Datatypes.Typescheme -> Datatypes.Tyname list
val has_free_imptyvars : Datatypes.Typescheme -> (Datatypes.Type *Datatypes.Type) option
val scheme_copy : Datatypes.Typescheme * (Datatypes.Tyname) Datatypes.StampMap -> Datatypes.Typescheme
val tyvars : Datatypes.Ident.TyVar list * Datatypes.Typescheme -> Datatypes.Ident.TyVar list
end
signature TYPESCHEME = SCHEME
;
