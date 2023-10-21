require "../utils/diagnostic";
require "virtualregister";
signature REGISTERCOLOURER =
sig
structure Register : VIRTUALREGISTER
structure Diagnostic : DIAGNOSTIC
type Graph
val empty : int * bool -> Graph
val clash : Graph * Register.Pack.T * Register.Pack.T * Register.Pack.T -> unit
datatype assignment = REGISTER of Register.T | SPILL of int
val colour :
Graph * (Register.T * Register.T) list * bool * string ->
{assign : Register.T -> assignment,
nr_spills : int}
end
;
