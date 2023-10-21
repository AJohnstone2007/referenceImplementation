require "namesettypes";
require "assemblies";
signature SHARETYPES =
sig
structure Assemblies : ASSEMBLIES
structure NamesetTypes : NAMESETTYPES
sharing type Assemblies.Basistypes.Nameset = NamesetTypes.Nameset
exception ShareError of string
val share_tyfun : bool * Assemblies.Basistypes.Datatypes.Tyfun * Assemblies.Basistypes.Datatypes.Tyfun *
Assemblies.TypeAssembly * NamesetTypes.Nameset -> bool * Assemblies.TypeAssembly
end
;
