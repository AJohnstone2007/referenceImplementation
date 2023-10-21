require "../basics/location";
signature EDITOR =
sig
structure Location : LOCATION
type preferences
val show_location :
preferences
-> string * Location.T
-> (string option * (unit->unit))
val edit_from_location :
preferences
-> string * Location.T
-> (string option * (unit->unit))
val edit :
preferences
-> string * int
-> (string option * (unit->unit))
end;
