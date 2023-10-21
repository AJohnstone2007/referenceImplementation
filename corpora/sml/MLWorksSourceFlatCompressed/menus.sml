signature MENUS =
sig
type Widget
datatype ButtonSpec =
SEPARATOR |
LABEL of string |
TOGGLE of string * (unit -> bool) * (bool -> unit) * (unit -> bool) |
SLIDER of string * int * int * (int -> unit) |
RADIO of string * (unit -> bool) * (bool -> unit) * (unit -> bool) |
PUSH of string * (unit -> unit) * (unit -> bool) |
DYNAMIC of string * (unit -> ButtonSpec list) * (unit -> bool) |
CASCADE of string * ButtonSpec list * (unit -> bool)
datatype selection = SINGLE | EXTENDED
datatype OptionSpec =
OPTSEPARATOR |
OPTLABEL of string |
OPTTOGGLE of string * (unit -> bool) * (bool -> bool) |
OPTTEXT of string * (unit -> string) * (string -> bool) |
OPTINT of string * (unit -> int) * (int -> bool) |
OPTRADIO of OptionSpec list |
OPTCOMBO of string * (unit -> string * string list) * (string -> bool) |
OPTLIST of string *
(unit -> string list * string list) *
(string list -> bool) *
selection
val make_submenus : Widget * ButtonSpec list -> unit
val make_menus : Widget * ButtonSpec list * bool -> unit
val quit : unit -> unit
val send_message : Widget * string -> unit
val get_tools_menuspec : ButtonSpec list * (unit -> ButtonSpec list) -> ButtonSpec
val get_graph_menuspec : ButtonSpec * ButtonSpec -> ButtonSpec list
datatype ToolButton = TB_SEP | TB_TOGGLE | TB_PUSH | TB_GROUP | TB_TOGGLE_GROUP
datatype ToolState = CHECKED | ENABLED | HIDDEN | GRAYED | PRESSED | WRAP
datatype ToolButtonSpec = TOOLBUTTON of
{style: ToolButton,
states: ToolState list,
tooltip_id: int,
name: string}
val make_toolbar : Widget * int * ToolButtonSpec list -> Widget
val make_buttons :
Widget * ButtonSpec list
-> {update: unit -> unit, set_focus: int -> unit}
val create_dialog :
Widget * string * string * (unit -> unit) * OptionSpec list ->
((unit -> unit) * (unit -> unit))
val exit_dialog : Widget * Widget * bool -> unit
end
;
