require "../basics/absyn";
require "../main/info";
require "../main/options";
require "../typechecker/assemblies";
signature MODULE_RULES =
sig
structure Absyn : ABSYN
structure Assemblies : ASSEMBLIES
structure Info : INFO
structure Options : OPTIONS
sharing Info.Location = Absyn.Ident.Location
sharing type Absyn.Type = Assemblies.Basistypes.Datatypes.Type
sharing type Absyn.Structure = Assemblies.Basistypes.Datatypes.Structure
datatype assembly =
ASSEMBLY of (Assemblies.StrAssembly * Assemblies.TypeAssembly)
| BASIS of Assemblies.Basistypes.Basis
val check_topdec :
Info.options ->
Options.options * bool * Absyn.TopDec * Assemblies.Basistypes.Basis * assembly ->
Assemblies.Basistypes.Basis
end
;
