signature CAPI =
sig
type Widget
datatype Point = POINT of { x : int, y : int }
datatype Region = REGION of { x : int, y :int, width : int, height :int }
type Font
exception SubLoopTerminated
exception WindowSystemError of string
val getNextWindowPos : unit -> int * int
val initialize_application : string * string * bool -> Widget
datatype WidgetAttribute =
PanedMargin of bool
| Position of int * int
| Size of int * int
| ReadOnly of bool
datatype WidgetClass =
Frame | Graphics | Label | Button | Text | RowColumn | Paned | Form
val make_widget :
string * WidgetClass * Widget * WidgetAttribute list -> Widget
val make_managed_widget :
string * WidgetClass * Widget * WidgetAttribute list -> Widget
val add_main_window : Widget * string -> unit
val remove_main_window : Widget -> unit
val make_main_window :
{name: string,
title: string,
parent: Widget,
contextLabel: bool,
winMenu: bool,
pos: int * int} -> Widget * Widget * Widget * Widget option
val get_main_windows : unit -> (Widget * string) list
val make_main_popup :
{name: string,
title: string,
parent: Widget,
contextLabel: bool,
visibleRef: bool ref,
pos: int * int} -> Widget * Widget * Widget * Widget option
val make_messages_popup : Widget * bool ref ->
Widget * Widget * Widget * Widget option
val make_main_subwindows :
Widget * bool ->
Widget * Widget * Widget option
val make_subwindow : Widget -> Widget
val make_popup_shell : string * Widget * WidgetAttribute list * bool ref -> Widget
val make_toplevel_shell : string * string * Widget * WidgetAttribute list -> Widget
val make_scrolled_text : string * Widget * WidgetAttribute list -> Widget * Widget
val make_scrolllist:
{parent: Widget,
name: string,
select_fn:
(Widget * Widget * ('b -> '_a list -> unit) * ('b -> '_a list -> unit))
-> '_a -> unit,
action_fn:
(Widget * Widget * ('b -> '_a list -> unit) * ('b -> '_a list -> unit))
-> '_a -> unit,
print_fn: 'b -> '_a -> string}
-> {scroll: Widget,
list: Widget,
set_items: 'b -> '_a list -> unit,
add_items: 'b -> '_a list -> unit}
val make_file_selection_box : string * Widget * WidgetAttribute list ->
Widget *
{get_file : unit -> string,
get_directory : unit -> string,
set_directory : string -> unit,
set_mask : string -> unit}
val list_select :
(Widget * string * (string -> unit)) ->
('_a list * ('_a -> unit) * ('_a -> string)) ->
(unit -> unit)
val remove_menu : Widget -> unit
val destroy : Widget -> unit
val initialize_application_shell : Widget -> unit
val initialize_toplevel : Widget -> unit
val reveal : Widget -> unit
val hide : Widget -> unit
val to_front : Widget -> unit
val set_min_window_size : Widget * int * int -> unit
val transfer_focus : Widget * Widget -> unit
val set_sensitivity : Widget * bool -> unit
val set_label_string : Widget * string -> unit
val set_focus : Widget -> unit
val parent : Widget -> Widget
val set_busy : Widget -> unit
val unset_busy : Widget -> unit
val widget_size : Widget -> int * int
val widget_pos : Widget -> int * int
val set_message_widget : Widget -> unit
val no_message_widget : unit -> unit
val set_close_callback : Widget * (unit -> unit) -> unit
val event_loop : bool ref -> unit
val main_loop : unit -> unit
val open_file_dialog : Widget * string * bool -> string list option
val open_dir_dialog : Widget -> string option
val set_dir_dialog : Widget -> string option
val save_as_dialog : Widget * string -> string option
val send_message : Widget * string -> unit
val makeYesNoCancel : Widget * string * bool -> unit -> bool option
val find_dialog : Widget * ({searchStr: string,
searchDown: bool,
matchCase: bool,
wholeWord: bool} -> unit)
* {findStr: string,
downOpt: bool option,
wordOpt: bool option,
caseOpt: bool option} -> unit -> Widget
val with_message : Widget * string -> (unit -> 'a) -> 'a
val beep : Widget -> unit
structure Event:
sig
eqtype Modifier
val meta_modifier : Modifier
datatype Button = LEFT | RIGHT | OTHER
end
structure Callback:
sig
datatype Type =
Activate
| Destroy
| Unmap
| Resize
| ValueChange
val add : Widget * Type * (unit -> unit) -> unit
end
structure List:
sig
val get_selected_pos : Widget -> int MLWorks.Internal.Vector.vector
val select_pos : Widget * int * bool -> unit
val set_pos : Widget * int -> unit
val add_items: Widget * string list -> unit
end
structure Text:
sig
val add_del_handler : Widget * (unit -> unit) -> unit
val get_key_bindings:
{startOfLine: unit -> unit,
endOfLine: unit -> unit,
backwardChar: unit -> unit,
forwardChar: unit -> unit,
eofOrDelete: unit -> unit,
abandon: unit -> unit,
deleteToEnd: unit -> unit,
previousLine: unit -> unit,
nextLine: unit -> unit,
newLine: unit -> unit,
delCurrentLine: unit -> unit,
checkCutSel: unit -> unit,
checkPasteSel: unit -> unit} -> (string * (unit -> unit)) list
val insert : Widget * int * string -> unit
val replace : Widget * int * int * string -> unit
val set_insertion_position : Widget * int -> unit
val get_insertion_position : Widget -> int
val get_last_position : Widget -> int
val get_string : Widget -> string
val set_string : Widget * string -> unit
val substring : Widget * int * int -> string
val get_line_and_index : Widget * int -> string * int
val current_line : Widget * int -> int
val end_line : Widget * int -> int
val get_line : Widget * int -> string
val set_highlight : Widget * int * int * bool -> unit
val get_selection : Widget -> string
val set_selection : Widget * int * int -> unit
val remove_selection : Widget -> unit
val convert_text : string -> string
val text_size : string -> int
val cut_selection : Widget -> unit
val paste_selection : Widget -> unit
val delete_selection : Widget -> unit
val copy_selection : Widget -> unit
val add_handler : Widget * (string * Event.Modifier list -> bool) -> unit
val add_modify_verify : Widget * ((int * int * string * (bool -> unit)) -> unit) -> unit
val read_only_before_prompt : bool
val check_insertion : Widget * string * int * int ref list -> string
end
val setAttribute : Widget * WidgetAttribute -> unit
structure Layout:
sig
datatype Class =
MENUBAR of Widget
| FLEX of Widget
| FIXED of Widget
| PANED of Widget * (Widget * Class list) list
| FILEBOX of Widget
| SPACE
val lay_out: Widget * (int * int) option * Class list -> unit
end
val show_splash_screen : Widget -> unit
val license_complain : Widget -> string -> bool option
structure GraphicsPorts:
sig
type GraphicsPort
exception UnInitialized
val initialize_gp : GraphicsPort -> unit
val start_graphics : GraphicsPort -> unit
val stop_graphics : GraphicsPort -> unit
val with_graphics : GraphicsPort -> ('a -> 'b) -> 'a -> 'b
val is_initialized : GraphicsPort -> bool
val gp_widget : GraphicsPort -> Widget
val get_offset : GraphicsPort -> Point
val set_offset : GraphicsPort * Point -> unit
val clear_clip_region : GraphicsPort -> unit
val set_clip_region : GraphicsPort * Region -> unit
val with_highlighting : GraphicsPort * ('a -> 'b) * 'a -> 'b
val redisplay : GraphicsPort -> unit
val reexpose : GraphicsPort -> unit
val copy_gp_region : GraphicsPort * GraphicsPort * Region * Point -> unit
val make_gp : string * string * Widget -> GraphicsPort
val text_extent : GraphicsPort * string ->
{font_ascent : int,
font_descent : int,
ascent : int,
descent : int,
lbearing : int,
rbearing : int,
width : int}
val draw_point : GraphicsPort * Point -> unit
val draw_line : GraphicsPort * Point * Point -> unit
val draw_rectangle : GraphicsPort * Region -> unit
val clear_rectangle : GraphicsPort * Region -> unit
val fill_rectangle : GraphicsPort * Region -> unit
val draw_arc : GraphicsPort * Region * real * real -> unit
val draw_image_string : GraphicsPort * string * Point -> unit
val make_graphics :
string * string *
(GraphicsPort * Region -> unit) *
(unit -> int * int) *
(bool * bool) *
Widget ->
(Widget *
GraphicsPort *
(unit -> unit) *
(Point -> unit)
)
val add_input_handler : GraphicsPort * (Event.Button * Point -> unit) -> unit
type PixMap
datatype LineStyle = LINESOLID | LINEONOFFDASH | LINEDOUBLEDASH
datatype Attribute =
FONT of Font
| LINE_STYLE of LineStyle
| LINE_WIDTH of int
| FOREGROUND of PixMap
| BACKGROUND of PixMap
datatype Request =
REQUEST_FONT
| REQUEST_LINE_STYLE
| REQUEST_LINE_WIDTH
| REQUEST_FOREGROUND
| REQUEST_BACKGROUND
val getAttributes : GraphicsPort * Request list -> Attribute list
val setAttributes : GraphicsPort * Attribute list -> unit
val with_graphics_port :(GraphicsPort * ('a -> 'b) * 'a) -> 'b
end
val clipboard_set : Widget * string -> unit
val clipboard_get : Widget * (string -> unit) -> unit
val clipboard_empty : Widget -> bool
val restart : unit -> unit
val terminator : string
val register_interrupt_widget : Widget -> unit
val with_window_updates : (unit -> 'a) -> 'a
val evaluating : bool ref
end
;
