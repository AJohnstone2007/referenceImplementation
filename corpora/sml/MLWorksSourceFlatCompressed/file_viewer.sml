require "../basics/location";
signature FILE_VIEWER =
sig
structure Location : LOCATION
type ToolData
type Widget
datatype source = LOCATION of Location.T | STRING of string
exception ViewFailed of string
val create :
(Widget * bool * ToolData) ->
(bool -> source -> unit)
end;
