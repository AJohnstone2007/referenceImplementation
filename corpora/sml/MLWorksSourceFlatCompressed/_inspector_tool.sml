require "^.utils.__terminal";
require "capi";
require "menus";
require "../main/user_options";
require "../main/preferences";
require "../utils/lists";
require "../utils/lisp";
require "../interpreter/inspector_values";
require "../interpreter/shell_utils";
require "gui_utils";
require "graph_widget";
require "tooldata";
require "inspector_tool";
require "^.basis.__int";
functor InspectorTool (
structure Capi : CAPI
structure GraphWidget : GRAPH_WIDGET
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure Lists : LISTS
structure LispUtils : LISP_UTILS
structure InspectorValues : INSPECTOR_VALUES
structure ShellUtils : SHELL_UTILS
structure Menus : MENUS
structure GuiUtils : GUI_UTILS
structure ToolData : TOOL_DATA
sharing UserOptions.Options = ToolData.ShellTypes.Options =
ShellUtils.Options
sharing type UserOptions.user_tool_options =
ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options = ShellUtils.UserOptions
sharing type UserOptions.user_context_options =
GuiUtils.user_context_options =
ToolData.UserContext.user_context_options
sharing type InspectorValues.options = UserOptions.Options.options
sharing type InspectorValues.Type = ShellUtils.Type = GuiUtils.Type
sharing type Menus.Widget = Capi.Widget = GuiUtils.Widget = ToolData.Widget = GraphWidget.Widget
sharing type Capi.GraphicsPorts.GraphicsPort = GraphWidget.GraphicsPort
sharing type Capi.Region = GraphWidget.Region
sharing type Capi.Point = GraphWidget.Point
sharing type ToolData.ShellTypes.Context = ShellUtils.Context
sharing type Menus.OptionSpec = GuiUtils.OptionSpec
sharing type ToolData.ButtonSpec = Menus.ButtonSpec = GuiUtils.ButtonSpec
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context = ShellUtils.user_context
sharing type Preferences.preferences = ShellUtils.preferences
sharing type ShellUtils.user_preferences = Preferences.user_preferences =
ToolData.ShellTypes.user_preferences =
GuiUtils.user_preferences
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type ToolData.UserContext.history_entry = ShellUtils.history_entry
) : INSPECTORTOOL =
struct
structure Options = UserOptions.Options
structure ShellTypes = ToolData.ShellTypes
structure UserContext = ToolData.UserContext
structure Info = ShellUtils.Info
type Widget = Capi.Widget
type Point = Capi.Point
type Region = Capi.Region
type ToolData = ToolData.ToolData
type Type = InspectorValues.Type
fun debug_output s = Terminal.output(s ^"\n")
val unwind_protect = LispUtils.unwind_protect
val inspector_number = ref 0
val do_abbreviations = false
datatype GraphType = VALUE | TYPE
datatype GraphOptions =
GRAPH_OPTIONS of {graph_type : GraphType,
show_atoms : bool,
show_strings : bool,
graph_sharing : bool,
graph_arity : int,
graph_depth : int,
default_visibility : bool,
child_position : GraphWidget.ChildPosition,
child_expansion : GraphWidget.ChildExpansion,
show_root_children : bool,
indicateHiddenChildren : bool,
orientation : GraphWidget.Orientation,
line_style : GraphWidget.LineStyle,
horizontal_delta : int,
vertical_delta : int,
graph_origin : int * int,
show_all : bool
}
val default_options =
GRAPH_OPTIONS {graph_type = VALUE,
show_atoms = true,
show_strings = true,
graph_sharing = true,
graph_arity = 4,
graph_depth = 5,
default_visibility = false,
child_position = GraphWidget.CENTRE,
child_expansion = GraphWidget.TOGGLE,
show_root_children = false,
indicateHiddenChildren = false,
orientation = GraphWidget.VERTICAL,
line_style = GraphWidget.STRAIGHT,
horizontal_delta = 20,
vertical_delta = 30,
graph_origin = (8,8),
show_all = false}
val posRef = ref NONE
val sizeRef = ref NONE
fun make_inspector_window (initial_item, options,
select_auto, debugger_print,
parent,tooldata,destroy_fun) =
let
val ToolData.TOOLDATA
{args as ShellTypes.LISTENER_ARGS
{user_options, user_context, user_preferences,
prompter, mk_xinterface_fn},
appdata,current_context, motif_context, tools, ...} =
tooldata
val duplicated = not (isSome select_auto)
val title =
if duplicated then
(inspector_number := (!inspector_number) + 1;
"Inspector #" ^ Int.toString (!inspector_number))
else
"Inspector"
val (full_menus, update_fns) =
case user_preferences
of Preferences.USER_PREFERENCES ({full_menus, ...}, update_fns) =>
(!full_menus, update_fns)
val do_automatic =
case select_auto
of NONE => ref false
| SOME b => ref b
val (shell,frame,menuBar,_) =
Capi.make_main_popup {name = "inspector",
title = title,
parent = parent,
contextLabel = false,
visibleRef = ref true,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val valText = Capi.make_managed_widget ("valText",Capi.Text,frame, [])
val typeText = Capi.make_managed_widget ("typeText", Capi.Text,frame, [])
val srcText = Capi.make_managed_widget ("srcText", Capi.Text, frame, [])
val local_context = ref motif_context
fun get_current_context () =
(UserContext.get_context
(GuiUtils.get_user_context (!local_context)))
fun message_fun s =
Capi.send_message (shell, s)
fun get_user_tool_options () = user_options
fun get_user_context_options () =
UserContext.get_user_options
(GuiUtils.get_user_context (!local_context))
fun get_compiler_options () =
UserOptions.new_options (user_options, get_user_context_options())
fun get_options () =
ShellTypes.new_options (user_options, user_context)
fun print_fn print_options (label, typed_value) =
label ^ ": "
^ ShellUtils.print_value
(typed_value,print_options,get_current_context())
local
val new_user_options = UserOptions.copy_user_tool_options user_options
fun make_compiler_options () =
UserOptions.new_options (new_user_options,
get_user_context_options())
val UserOptions.USER_TOOL_OPTIONS(r,_) = new_user_options
val _ =
( #show_fn_details(r) := false;
#show_exn_details(r) := false;
#maximum_depth(r) := 2;
#maximum_ref_depth(r) := 1;
#maximum_str_depth(r) := 1;
#maximum_sig_depth(r) := 1;
#maximum_string_size(r) := 1;
#maximum_seq_size(r) := 10
)
val normal_compiler_options = make_compiler_options ()
val graph_string_size = 5
val _ = (#maximum_string_size(r) := graph_string_size)
val graph_label_string_options = make_compiler_options ()
val compiler_options = ref(normal_compiler_options)
val string_abbreviation = InspectorValues.string_abbreviation
val normal_string_ellipsis = !string_abbreviation
val graph_label_string_ellipsis = " .."
fun set_string_abbrev () =
string_abbreviation := graph_label_string_ellipsis
fun reset_string_abbrev () =
string_abbreviation := normal_string_ellipsis
fun set_print_string_size (ty) =
if InspectorValues.is_string_type ty
then compiler_options := graph_label_string_options
else compiler_options := normal_compiler_options
fun print_options (Options.OPTIONS{print_options,...})
= print_options
in
fun graph_print_value (typed_value as (_,ty)) =
( set_print_string_size(ty);
set_string_abbrev();
unwind_protect
(fn () =>
ShellUtils.print_value
(typed_value,(print_options (!compiler_options)),
get_current_context())
)
reset_string_abbrev
)
fun graph_print_type (_,ty) =
ShellUtils.print_type
(ty,!compiler_options,get_current_context())
end
val (initial_str, initial_item) = initial_item
val current_root = ref initial_item
val current_string = ref ""
val select_fn = ref (fn () => ())
val current_item_ref = ref NONE
fun set_state (item as (value,ty)) =
let
val compiler_options = UserOptions.new_options
(user_options,get_user_context_options())
val print_options = UserOptions.new_print_options user_options
val context = get_current_context ()
fun print_type ty =
ShellUtils.print_type (ty,compiler_options,context)
fun print_value value =
ShellUtils.print_value (item,print_options,context)
val value_string = print_value item
val type_string = print_type ty
in
Capi.Text.set_string (valText, value_string);
Capi.Text.set_string (typeText, type_string);
current_string := value_string;
current_item_ref := SOME (value_string,item)
end
fun eq ( (v,_), (v',_) ) =
let
fun cast x = (MLWorks.Internal.Value.cast x) : int ref
in
cast v = cast v'
end
fun inspect_root (new_item) =
( current_root := new_item;
set_state (new_item)
)
val hide_child_flag = ref false
fun hide_child(_) = !hide_child_flag
local
val GRAPH_OPTIONS {graph_type,show_atoms,show_strings,graph_sharing,graph_arity,
graph_depth,default_visibility,child_position,child_expansion,
show_root_children, indicateHiddenChildren, orientation,line_style,
horizontal_delta,vertical_delta,graph_origin,show_all} =
options
in
val graph_type = ref graph_type
val show_atoms = ref show_atoms
val show_strings = ref show_strings
val graph_sharing = ref graph_sharing
val graph_arity = ref graph_arity
val graph_depth = ref graph_depth
val default_visibility = ref default_visibility
val gspec = {child_position = ref child_position,
child_expansion = ref child_expansion,
default_visibility = default_visibility,
show_root_children = ref show_root_children,
indicateHiddenChildren = ref indicateHiddenChildren,
orientation = ref orientation,
line_style = ref line_style,
horizontal_delta = ref horizontal_delta,
vertical_delta = ref vertical_delta,
graph_origin = ref graph_origin,
show_all = ref show_all
}
end
val graph_spec = GraphWidget.GRAPH_SPEC gspec
fun make_options () =
GRAPH_OPTIONS {graph_type = !graph_type,
show_atoms = !show_atoms,
show_strings = !show_strings,
graph_sharing = !graph_sharing,
graph_arity = !graph_arity,
graph_depth = ! graph_depth,
default_visibility = !default_visibility,
child_position = !(#child_position gspec),
child_expansion = !(#child_expansion gspec),
show_root_children = !(#show_root_children gspec),
indicateHiddenChildren = !(#indicateHiddenChildren gspec),
orientation = !(#orientation gspec),
line_style = !(#line_style gspec),
horizontal_delta = !(#horizontal_delta gspec),
vertical_delta = !(#vertical_delta gspec),
graph_origin = !(#graph_origin gspec),
show_all = !(#show_all gspec)
}
datatype Item = ITEM of ((MLWorks.Internal.Value.T * Type) *
bool ref *
bool ref *
(string * int * int * int) option ref
)
fun abbreviate (ITEM(_,ref(atom),abbrev,_)) = abbrev := not atom
fun unabbreviate (ITEM(_,_,abbrev,_)) = abbrev := false
val abbrev_string = " *** "
fun massage_graph (root,get_children,eq) =
let
fun list_items init_depth root =
let
val items = ref []
fun add (depth,node) =
if do_abbreviations andalso (depth > !graph_depth)
then ( abbreviate(node); true )
else
let
fun lookup (node,[]) = true
| lookup (node,(_,a,_)::b) =
if eq (node,a) then false
else lookup (node,b)
in
lookup (node,!items)
end
fun scan (item as (depth,node)) =
if add item
then let val children = get_children item
in
items := (depth,node,children) :: !items;
app scan children
end
else ()
in
scan (init_depth,root);
rev(!items)
end
fun transform_items itemlist =
let
exception Index
fun index' (node,[],n) = raise Index
| index' (node,(_,node',_)::rest,n) =
if eq(node,node') then n
else index' (node,rest,n+1)
fun index (_,node) = index' (node,itemlist,0)
in
MLWorks.Internal.Array.arrayoflist
(map
(fn (item as (_,node,children)) => (node,map index children))
itemlist)
end
val itemlist = list_items 0 root
val nodes = transform_items itemlist
in
nodes
end
fun make_graph root =
let
fun get_children (depth,ITEM (item,_,ref false,_)) =
let
val new_depth = depth + 1
val not_show_atoms = not(!show_atoms)
val not_show_strings = not(!show_strings)
fun is_atomic (x as (v,ty)) =
( InspectorValues.is_scalar_value x
orelse
( not_show_strings
andalso
InspectorValues.is_string_type ty
)
)
fun new_item(x,abbrev) =
ITEM (x,ref(is_atomic x),ref abbrev,ref NONE)
fun push_scan (abbrev_flag, x, acc) =
if not_show_atoms andalso (is_atomic x)
then acc
else (new_depth,new_item(x,abbrev_flag)):: acc
fun scan (_,[],acc) = rev acc
| scan (n,(_,x)::rest,acc) =
let val abbrev = do_abbreviations andalso (n < 0)
val new_acc = push_scan(abbrev,x,acc)
val rest' = if abbrev then [] else rest
in
scan (n-1,rest',new_acc)
end
val inspect_items =
InspectorValues.get_inspector_values
(get_compiler_options()) debugger_print item
in
scan (!graph_arity,inspect_items,[])
end
| get_children(_,_) = []
val cast : 'a -> int ref = MLWorks.Internal.Value.cast
fun equal_values (ITEM ((v1,ty1),_,_,_), ITEM ((v2,ty2),_,_,_)) =
cast v1 = cast v2 andalso InspectorValues.type_eq (ty1,ty2)
fun equal_items (ITEM((v1,ty1),_,r1,_), ITEM((v2,ty2),_,r2,_)) =
if InspectorValues.is_ref_type ty1 andalso
InspectorValues.is_ref_type ty2
then cast v1 = cast v2
else r1 = r2
val equality_fn = if !graph_sharing then equal_values else equal_items
val root_item = ITEM (root,ref false,ref false,ref NONE)
val items = massage_graph (root_item, get_children, equality_fn)
in
(items,[0])
end
fun make_value_graph () = make_graph (!current_root)
fun item_string (gp,item,abbrev) =
if abbrev then abbrev_string else
case !graph_type of
VALUE => graph_print_value (item)
| TYPE => graph_print_type (item)
fun get_item_data (ITEM (item,_,ref(abbrev),extents),gp) =
case !extents of
SOME data => data
| _ =>
let
val s = item_string (gp,item,abbrev)
val {font_ascent,font_descent,width,...} =
Capi.GraphicsPorts.text_extent (gp,s)
val data = (s,font_ascent,font_descent,width)
in
extents := SOME data;
data
end
val baseline_height = 3
val surround = 3
val topleft_width = 1
val bottomright_width = 3
val topleft_w = surround + topleft_width
val bottomright_w = surround + bottomright_width
val tot_w = topleft_w + bottomright_w
val bgnd_w = topleft_width + bottomright_width
fun item_draw_item (item,selected,gp,Capi.POINT{x,y}) =
let
val (s,font_ascent,font_descent,width) = get_item_data (item,gp)
val left = width div 2
val right = width - left
val new_x = x - (left+1+topleft_w)
val new_y = y - (font_ascent+1+topleft_w)
val new_width = (width+tot_w)
val new_height = (font_ascent+font_descent+tot_w)
val backgnd_region =
Capi.REGION {x = new_x, y = new_y,
width= new_width, height= new_height}
val foregnd_region =
Capi.REGION {x = new_x+topleft_width, y = new_y+topleft_width,
width= new_width-bgnd_w, height= new_height-bgnd_w}
val new_point = Capi.POINT{x=x - left,y=y}
in
Capi.GraphicsPorts.draw_rectangle (gp,foregnd_region);
if selected then
Capi.GraphicsPorts.with_highlighting
(gp,Capi.GraphicsPorts.draw_image_string, (gp,s,new_point))
else Capi.GraphicsPorts.draw_image_string (gp,s,new_point)
end
fun item_extent (item,gp) =
let
val (s,font_ascent,font_descent,width) = get_item_data (item,gp)
val left = width div 2
val right = width - left
in
GraphWidget.EXTENT {
left = left+topleft_w+1,
right = right+bottomright_w,
up = font_ascent+topleft_w,
down = font_descent+bottomright_w
}
end
val {widget=graph_window,
initialize=init_graph,
update=update_graph,
popup_menu=graph_menu,
set_position,
set_button_actions,...} =
GraphWidget.make ("inspectorGraph","InspectorGraph",title,frame,
graph_spec,make_value_graph,
item_draw_item,item_extent)
val new_root_item = ref NONE
fun graph_select_fn (item as ITEM (entry,_,ref(abbrev),_),reg) =
if abbrev then
new_root_item := SOME(entry)
else
set_state (entry)
fun try_set_new_root () =
case !new_root_item of
SOME(entry) =>
( new_root_item := NONE;
inspect_root(entry);
update_graph()
)
| _ => ()
fun initialize_graph () = init_graph graph_select_fn
val insp_item_menu_spec =
[ Menus.OPTLABEL "Content",
Menus.OPTRADIO
[ GuiUtils.toggle_value("content_value",graph_type,VALUE),
GuiUtils.toggle_value("content_type",graph_type,TYPE)
],
Menus.OPTSEPARATOR,
GuiUtils.bool_value("show_all", (#show_all gspec)),
GuiUtils.bool_value("show_atoms",show_atoms),
GuiUtils.bool_value("graph_sharing",graph_sharing)] @
(if do_abbreviations
then [Menus.OPTSEPARATOR,
GuiUtils.int_value("graph_arity",graph_arity),
GuiUtils.int_value("graph_depth",graph_depth)]
else [])
val insp_item_popup =
#1 (Menus.create_dialog
(shell, title ^ ": Display Controls","inspectorItemMenu",
update_graph, insp_item_menu_spec
)
)
local
fun left_action (pa,_) =
( ignore(pa ()); try_set_new_root () )
fun middle_action (_) = insp_item_popup ()
fun set_hide_flag () = (hide_child_flag := true)
fun reset_hide_flag () = (hide_child_flag := false)
fun right_action (pa,_) =
( set_hide_flag (); unwind_protect pa reset_hide_flag )
in
val _ = set_button_actions
{ left = left_action,
middle = middle_action,
right = right_action }
end
val quit_funs = ref [fn () => Capi.remove_main_window shell];
fun do_quit_funs _ =
(
app (fn f => f ()) (!quit_funs);
destroy_fun())
fun set_root (item) =
( current_root := item;
set_state (item);
update_graph()
)
fun set_previous_roots (item,l) =
( set_root (item)
)
val valTitleLabel =
Capi.make_managed_widget ("valTitleLabel",Capi.Label,frame,[])
val typeTitleLabel =
Capi.make_managed_widget ("typeTitleLabel",Capi.Label,frame,[])
val graphLabel =
Capi.make_managed_widget ("graphLabel",Capi.Label,frame,[])
val srcTitleLabel =
Capi.make_managed_widget ("srcTitleLabel", Capi.Label, frame, [])
fun first_line (message) =
let
fun aux ([],_) = message
| aux ((#"\n" :: _),acc) = implode (rev acc)
| aux ((a::b),acc) = aux (b,a::acc)
in
aux (explode message,[])
end
fun select_fn item =
case ShellUtils.value_from_history_entry (item, get_options ()) of
SOME (s, v) =>
(Capi.Text.set_string (srcText, s);
inspect_root (v);
update_graph ())
| _ => ()
fun mk_tooldata () =
ToolData.TOOLDATA
{args = ShellTypes.LISTENER_ARGS
{user_options = user_options,
user_preferences = user_preferences,
user_context =
GuiUtils.get_user_context (!local_context),
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn},
current_context = current_context,
appdata = appdata,
motif_context = !local_context,
tools = tools}
fun duplicate value =
(ignore(make_inspector_window
(value, make_options(), NONE, debugger_print,
parent, mk_tooldata(), fn _ => ()));
())
val sep_size = 10
fun close_window _ =
(do_quit_funs ();
Capi.destroy shell)
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun caller_update _ =
if isSome (!current_item_ref) then
set_state (#2 (valOf (!current_item_ref)))
else
()
val view_menu =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = caller_update,
view_type = [GuiUtils.VALUE_PRINTER]}
fun get_value () = !current_item_ref
val value_menu =
GuiUtils.value_menu
{parent = shell,
user_preferences = user_preferences,
inspect_fn = NONE,
get_value = get_value,
enabled = true,
tail = []}
val values = ToolData.extract value_menu
val view = ToolData.extract view_menu
fun get_user_context () = GuiUtils.get_user_context (!local_context)
val menuSpec =
[ToolData.file_menu [("save", fn _ =>
GuiUtils.save_history (false, get_user_context (), shell),
fn _ =>
not (UserContext.null_history (get_user_context ()))
andalso UserContext.saved_name_set (get_user_context ())),
("saveAs", fn _ => GuiUtils.save_history
(true, get_user_context (), shell),
fn _ => not (UserContext.null_history (get_user_context ()))),
("close", close_window, fn _ => true)],
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (fn _ => Capi.clipboard_set (shell,!current_string)),
delete = NONE,
edit_possible = fn _ => false,
selection_made = fn _ => !current_string <> "",
delete_all = NONE,
edit_source = [value_menu]}),
ToolData.tools_menu (mk_tooldata,
fn () => GuiUtils.get_user_context (!local_context)),
ToolData.usage_menu (values @ view @
[("duplicate",
fn _ => case get_value () of SOME x => duplicate x | _ => (),
fn _ => case get_value () of SOME x => true | _ => false),
("graph", graph_menu, fn _ => true),
("insp_item", insp_item_popup, fn _ => true)],
[("autoSelection",
fn _ => !do_automatic,
fn b => do_automatic := b,
fn _ => case select_auto of NONE => false | _ => true)]),
ToolData.debug_menu values]
in
quit_funs := Menus.quit :: (!quit_funs);
quit_funs := storeSizePos :: (!quit_funs);
Menus.make_submenus (menuBar, menuSpec);
Capi.Layout.lay_out
(frame, !sizeRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.FIXED srcTitleLabel,
Capi.Layout.FIXED srcText,
Capi.Layout.FIXED valTitleLabel,
Capi.Layout.FIXED valText,
Capi.Layout.FIXED typeTitleLabel,
Capi.Layout.FIXED typeText,
Capi.Layout.FIXED graphLabel,
Capi.Layout.FLEX graph_window,
Capi.Layout.SPACE]);
Capi.Text.set_string (srcText, initial_str);
Capi.Callback.add (frame, Capi.Callback.Destroy, do_quit_funs);
inspect_root (!current_root);
Capi.reveal frame;
initialize_graph ();
fn (auto,str,item) =>
if auto andalso not (!do_automatic) then
()
else
(inspect_root (item);
update_graph ();
Capi.reveal shell;
Capi.reveal frame;
Capi.Text.set_string (srcText,str);
if auto then
()
else
Capi.to_front shell)
end
local
val display_fun = ref NONE
fun destroy_fun _ = display_fun := NONE
in
fun inspect_value (parent,debugger_print,tooldata) =
fn auto =>
fn (str,v) =>
case !display_fun of
SOME f => f (auto,str,v)
| _ =>
if auto then ()
else
let
val f =
make_inspector_window
((str,v), default_options, SOME false,
debugger_print, parent, tooldata, destroy_fun)
in
display_fun := SOME f
end
end
end
;
