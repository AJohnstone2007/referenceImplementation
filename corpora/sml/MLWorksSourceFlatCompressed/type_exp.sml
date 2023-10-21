require "../typechecker/basistypes";
require "../basics/absyn";
require "../main/info";
signature TYPE_EXP =
sig
structure BasisTypes : BASISTYPES
structure Absyn : ABSYN
structure Info : INFO
sharing Absyn.Set = BasisTypes.Set
sharing Absyn.Ident.Location = Info.Location
sharing Absyn.Ident = BasisTypes.Datatypes.Ident
sharing type BasisTypes.Datatypes.Structure = Absyn.Structure
sharing type BasisTypes.Datatypes.Type = Absyn.Type
val check_type :
Info.options ->
Absyn.Ty * BasisTypes.Context ->
BasisTypes.Datatypes.Type
end;
