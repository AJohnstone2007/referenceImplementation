require "basistypes";
require "../main/options";
signature SIGMA =
sig
structure BasisTypes : BASISTYPES
structure Options : OPTIONS
val string_sigma : Options.print_options -> BasisTypes.Sigma -> string
val string_phi : Options.print_options -> BasisTypes.Phi -> string
val sig_copy : BasisTypes.Sigma * bool -> int -> BasisTypes.Sigma
val phi_copy : BasisTypes.Phi * bool -> int -> BasisTypes.Phi
val abstract_sigma : BasisTypes.Sigma -> int -> BasisTypes.Sigma
val new_names_of : BasisTypes.Datatypes.Structure -> BasisTypes.Nameset
val names_of : BasisTypes.Datatypes.Structure -> BasisTypes.Nameset
val names_of_env : BasisTypes.Datatypes.Env -> BasisTypes.Nameset
end
;
