signature INSPECTORTOOL =
sig
type Type
type ToolData
type Widget
val inspect_value :
(Widget * bool * ToolData) ->
bool ->
string * (MLWorks.Internal.Value.ml_value * Type) ->
unit
end
;
