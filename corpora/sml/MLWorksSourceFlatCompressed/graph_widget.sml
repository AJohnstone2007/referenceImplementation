signature GRAPH_WIDGET =
sig
type Point
type Region
type Widget
type GraphicsPort
datatype ChildPosition = NEXT | BELOW | CENTRE
datatype ChildExpansion = ALWAYS | TOGGLE | SOMETIMES of (bool -> bool)
datatype Orientation = HORIZONTAL | VERTICAL
datatype LineStyle = STRAIGHT | STEP
datatype GraphSpec =
GRAPH_SPEC of
{child_position : ChildPosition ref,
child_expansion : ChildExpansion ref,
default_visibility : bool ref,
show_root_children : bool ref,
indicateHiddenChildren : bool ref,
orientation : Orientation ref,
line_style: LineStyle ref,
horizontal_delta : int ref,
vertical_delta : int ref,
graph_origin : (int * int) ref,
show_all : bool ref}
datatype Extent = EXTENT of { left : int, right : int, up : int, down : int }
val make :
string * string * string * Widget *
GraphSpec *
(unit -> ('_a * int list) MLWorks.Internal.Array.array * int list) *
('_a * bool * GraphicsPort * Point -> unit) *
('_a * GraphicsPort -> Extent)
->
{widget: Widget,
initialize : ('_a * Region -> unit) -> unit,
update : unit -> unit,
popup_menu : unit-> unit,
set_position : Point -> unit,
set_button_actions : { left : ((unit -> unit) * Point -> unit),
right : ((unit -> unit) * Point -> unit),
middle : ((unit -> unit) * Point -> unit)
} -> unit,
initialiseSearch :
(unit -> string) ->
((string -> '_a -> bool) * (string -> '_a -> bool)) ->
unit -> unit
}
datatype Position = NONE | TOP | CENTER | BOTTOM | LEFT | RIGHT | ORIGIN
val reposition_graph_selection :
( Widget * (Point -> unit) ) ->
{ reposition_fn : Region -> unit,
redisplay_fn : unit -> unit,
popup_fn : Widget -> (unit -> unit),
v_position : Position ref,
h_position : Position ref,
v_offset : int ref,
h_offset : int ref
}
end
;
