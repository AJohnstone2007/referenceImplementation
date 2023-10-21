signature NDFA =
sig
type ndfa
eqtype state
type transitions
type action
val epsilon : state list -> transitions
val single_char : int * state -> transitions
val mk_trans : (int * state) list -> transitions
val get_epsilon : (transitions * state list) -> state list
val get_char : (int * transitions * state list) -> state list
val empty : ndfa
val add : ndfa * transitions -> ndfa
val add_final : ndfa * action -> ndfa
val add_start : ndfa * state list -> ndfa
val add_rec : ndfa * (ndfa -> ndfa) -> ndfa
val start : ndfa -> state
val set_start : ndfa * state -> ndfa
val transitions : ndfa * state -> transitions
val action : ndfa * state -> action
val num_states : ndfa -> int
end;
