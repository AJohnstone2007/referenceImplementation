require "../typechecker/basistypes";
require "../basics/absyn";
require "../main/info";
require "../main/options";
signature PATTERNS =
sig
structure BasisTypes : BASISTYPES
structure Absyn : ABSYN
structure Info : INFO
structure Options : OPTIONS
sharing Absyn.Ident.Location = Info.Location
sharing Absyn.Ident = BasisTypes.Datatypes.Ident
sharing type Absyn.Type = BasisTypes.Datatypes.Type
sharing type Absyn.Structure = BasisTypes.Datatypes.Structure
val pat_context :
Absyn.Pat -> BasisTypes.Datatypes.Valenv
val check_pat :
(Info.options * Options.options) ->
Absyn.Pat * BasisTypes.Context ->
(BasisTypes.Datatypes.Valenv * BasisTypes.Datatypes.Type * (BasisTypes.Datatypes.Type ref * Absyn.RuntimeInfo ref) list)
end
;
