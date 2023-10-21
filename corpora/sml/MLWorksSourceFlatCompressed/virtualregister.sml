require "../utils/monoset";
require "../utils/mutablemonoset";
require "../utils/monomap";
signature VIRTUALREGISTER =
sig
structure Set : MONOSET
structure Pack : MUTABLEMONOSET
structure Map : MONOMAP
sharing Set.Text = Pack.Text
eqtype T
sharing type T = Set.element = Pack.element = Map.object
val new : unit -> T
val reset : unit -> unit
val order : T * T -> bool
val pack : int -> T
val unpack : T -> int
val pack_set : Set.T -> Pack.T
val unpack_set : Pack.T -> Set.T
val to_string : T -> string
val to_text : T -> Set.Text.T
end
;
