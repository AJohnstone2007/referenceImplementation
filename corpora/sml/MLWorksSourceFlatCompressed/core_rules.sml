require "../basics/absyn";
require "../main/info";
require "../main/options";
require "basistypes";
signature CORE_RULES =
sig
structure Absyn : ABSYN
structure Basistypes : BASISTYPES
structure Info : INFO
structure Options : OPTIONS
sharing Info.Location = Absyn.Ident.Location
sharing Basistypes.Datatypes.Ident = Absyn.Ident
sharing Absyn.Set = Basistypes.Set
sharing type Absyn.Type = Basistypes.Datatypes.Type
sharing type Absyn.Structure = Basistypes.Datatypes.Structure
val check_dec :
(Info.options * Options.options) ->
Absyn.Dec * Basistypes.Context ->
Basistypes.Datatypes.Env
val check_type :
Info.options ->
Absyn.Ty * Basistypes.Context ->
Basistypes.Datatypes.Type
end
;
