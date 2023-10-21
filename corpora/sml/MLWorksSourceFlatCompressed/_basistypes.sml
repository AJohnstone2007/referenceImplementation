require "../typechecker/datatypes";
require "../utils/set";
require "namesettypes";
require "basistypes";
functor BasisTypes
( structure Datatypes : DATATYPES
structure Set : SET
structure NamesetTypes : NAMESETTYPES
) : BASISTYPES =
struct
structure Datatypes = Datatypes
structure Set = Set
type Nameset = NamesetTypes.Nameset
datatype Sigma = SIGMA of (Nameset * Datatypes.Structure)
datatype Sigenv = SIGENV of (Datatypes.Ident.SigId, Sigma) Datatypes.NewMap.map
datatype Phi = PHI of (Nameset * (Datatypes.Structure * Sigma))
datatype Funenv = FUNENV of (Datatypes.Ident.FunId, Phi) Datatypes.NewMap.map
datatype Tyvarenv = TYVARENV of (Datatypes.Ident.TyVar,Datatypes.Type) Datatypes.NewMap.map
datatype Context = CONTEXT of (int *
Datatypes.Ident.TyVar Set.Set *
Datatypes.Env * Tyvarenv)
datatype Basis = BASIS of (int * Nameset * Funenv * Sigenv * Datatypes.Env)
end
;
