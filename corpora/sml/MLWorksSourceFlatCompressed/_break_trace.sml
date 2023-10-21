require "../basis/__int";
require "capi";
require "menus";
require "tooldata";
require "gui_utils";
require "../main/user_options";
require "../debugger/newtrace";
require "../utils/lists";
require "break_trace";
functor BreakTrace (
structure Capi: CAPI
structure Menus: MENUS
structure Lists: LISTS
structure Trace: TRACE
structure GuiUtils: GUI_UTILS
structure ToolData: TOOL_DATA
structure UserOptions: USER_OPTIONS
sharing type Menus.Widget = Capi.Widget = ToolData.Widget = GuiUtils.Widget
sharing type Menus.ButtonSpec = ToolData.ButtonSpec
sharing type GuiUtils.MotifContext = ToolData.MotifContext
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec
sharing type GuiUtils.user_context = ToolData.ShellTypes.user_context
): BREAK_TRACE =
struct
type Widget = Capi.Widget
type ButtonSpec = Menus.ButtonSpec
type ToolData = ToolData.ToolData
structure Options = UserOptions.Options
local
val trace_list = ref []
val break_list = ref []
val stringlist = ref [] : string list ref
fun get_trans_breaks {name:string, hits:int, max:int} = "(B) " ^
name ^ " : (hits " ^ Int.toString hits ^ ", maximum " ^
(if max >= 0 then Int.toString max else "counting")
^ ")"
fun get_trans_traces name = "(T) " ^ name
fun set_stringlist () =
stringlist := (map get_trans_traces (!trace_list)) @
(map get_trans_breaks (!break_list))
fun initialize () =
let
fun initialize' (arg_list, get) =
(arg_list := get();
set_stringlist ())
in
initialize' (trace_list, Trace.traces);
initialize' (break_list, Trace.breakpoints)
end
fun find_space(arg as (s, i)) =
if i >= size s orelse MLWorks.String.ordof arg = ord #" " then
i
else
find_space(s, i+1)
fun ignore_spaces(arg as (s, i)) =
if i >= size s orelse MLWorks.String.ordof arg <> ord #" " then
i
else
ignore_spaces(s, i+1)
fun strip_trailing(s, i) =
if i <= 0 then s
else
if i >= size s orelse MLWorks.String.ordof(s, i) = ord #" " then
strip_trailing(s, i-1)
else
substring (s, 0, i+1)
fun parse_name name =
let
val i = ignore_spaces(name, 0)
val size_name = size name
in
if i >= size_name then
"<null>"
else
let
val i' = find_space(name, i)
val n' = substring (name, i, i'-i)
val n = strip_trailing(n', size n')
in
n
end
end
fun parse_b max name =
let
val name = parse_name name
val max = if max < 0 then ~1 else max
in
{name=name, hits=0, max=max}
end
fun parse_t name = parse_name name
fun break_member_fn(_, []) = false
| break_member_fn(arg1 as {name, hits, max},
{name=name', hits=hits', max=max'} :: rest) =
name=name' orelse break_member_fn(arg1, rest)
fun break_filter_fn {name, hits, max} =
let
fun filter{name=name', hits, max} = name <> name'
in
filter
end
fun trace_filter_fn name = (fn s => s <> name)
fun ok_string s = s <> ""
fun remove_item name (arg_list, set_trans, member_fn, filter_fn) =
if (ok_string name) andalso member_fn(set_trans name, !arg_list) then
arg_list := Lists.filterp (filter_fn (set_trans name)) (!arg_list)
else ()
val bt_manager = ref NONE
val sizeRef = ref NONE
val posRef = ref NONE
in
fun create_bt_manager (tooldata as ToolData.TOOLDATA
{args, appdata, current_context, motif_context, tools}) =
let
val ToolData.APPLICATIONDATA {applicationShell,...} = appdata
val parent = applicationShell
val local_context = ref motif_context
val selection = ref NONE
fun get_current_user_context () =
GuiUtils.get_user_context (!local_context)
fun mk_tooldata _ = tooldata
val name = "Trace and Breakpoint Manager"
val (shell,form,menubar,_) =
Capi.make_main_window {name = name,
title = name,
parent = parent,
contextLabel = false,
winMenu = false,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val buttonPane =
Capi.make_managed_widget ("listManagerButtonPane", Capi.RowColumn, form, []);
val label = Capi.make_managed_widget ("Trace and Break Points:", Capi.Label, form, []);
val {scroll,list,set_items,...} =
Capi.make_scrolllist
{parent = form,
name = "listManagerList",
select_fn = fn _ => fn s => (selection := SOME (substring (s, 4, size s - 4))),
action_fn = fn _ => fn s => (),
print_fn = fn _ => fn s => s}
fun set_list _ = set_items Options.default_print_options (!stringlist)
fun delete_all _ =
(stringlist := [];
break_list := [];
trace_list := [];
Trace.trace_list [];
Trace.break_list [];
selection := NONE;
set_list ())
fun add_item name (arg_list, set_trans, member_fn, filter_fn) =
if ok_string name then
let
val trans = set_trans name
in
if member_fn(trans, !arg_list) then
arg_list := trans :: Lists.filterp (filter_fn trans) (!arg_list)
else
arg_list := trans :: !arg_list;
set_stringlist ();
set_list ()
end
else ()
val created = ref false
val get_dialog_ref = ref (fn () => ())
fun get_dialog () =
let
val isBreakpoint = ref true
val itemName = ref ""
val maxCount = ref 1
fun add_bt_item () =
if (!isBreakpoint) then
(add_item (!itemName) (break_list, parse_b (!maxCount), break_member_fn,break_filter_fn);
Trace.break_list (!break_list))
else
(add_item (!itemName) (trace_list, parse_t, Lists.member, trace_filter_fn);
Trace.trace_list (!trace_list))
fun add_item_dialog parent =
let
open Menus
val _ = created := true
in
create_dialog
(parent,
"Add Break and Trace Points",
"addBreakTraceItems",
add_bt_item,
[OPTRADIO
[OPTTOGGLE ("addBreak", fn _ => true, fn b => (isBreakpoint := b; true)),
OPTTOGGLE ("addTrace", fn _ => false, fn b => (isBreakpoint := (not b); true))],
OPTSEPARATOR,
OPTTEXT ("breakTraceItem", fn _ => (!itemName), fn s => (itemName := s; true)),
OPTINT ("maximumBreaks", fn _ => (!maxCount), fn i => (maxCount := i; true))])
end
in
if (!created) then
(!get_dialog_ref)()
else
(get_dialog_ref := #1 (add_item_dialog shell);
(!get_dialog_ref)())
end
fun reset _ = (initialize (); set_list ())
fun do_delete () =
let
val selected = if isSome (!selection) then valOf (!selection) else ""
fun remove_point point =
if Lists.member (point, !trace_list) then
remove_item point (trace_list, parse_t, Lists.member, trace_filter_fn)
else
remove_item point (break_list, parse_b 0, break_member_fn,break_filter_fn)
val new_stringlist = remove_point selected
in
Trace.break_list (!break_list);
Trace.trace_list (!trace_list);
set_stringlist();
selection := NONE;
set_list ()
end
val {update = other_buttons_update_fn, ...} =
Menus.make_buttons
(buttonPane,
[Menus.PUSH ("breakTraceButton",
get_dialog,
fn _ => true),
Menus.PUSH ("deleteSelectedButton",
do_delete,
fn _ => isSome(!selection)),
Menus.PUSH ("deleteAllButton",
delete_all,
fn _ => true)])
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun close_window _ =
(Menus.quit ();
bt_manager := NONE;
storeSizePos();
Capi.destroy shell)
val menuspec =
[ToolData.file_menu [("close", close_window, fn _ => true)],
ToolData.edit_menu (shell,
{cut = NONE,
paste = NONE,
copy = NONE,
delete = SOME do_delete,
edit_possible = fn _ => true,
selection_made = fn _ => isSome(!selection),
delete_all = SOME ("deleteAll", delete_all, fn _ => true),
edit_source = []}),
ToolData.tools_menu (mk_tooldata, get_current_user_context),
ToolData.usage_menu
([("addBreakTrace", get_dialog, fn _ => true)],[]),
ToolData.debug_menu []]
in
Menus.make_submenus (menubar,menuspec);
Capi.set_close_callback(shell, close_window);
Capi.Layout.lay_out
(form, !sizeRef,
[Capi.Layout.MENUBAR menubar,
Capi.Layout.FIXED label,
Capi.Layout.FLEX scroll,
Capi.Layout.FIXED buttonPane,
Capi.Layout.SPACE]);
reset ();
shell
end
fun create tooldata =
if isSome(!bt_manager) then
(Capi.initialize_toplevel (valOf (!bt_manager));
Capi.to_front (valOf (!bt_manager)))
else
(bt_manager := SOME (create_bt_manager tooldata);
create tooldata)
end
end
;
