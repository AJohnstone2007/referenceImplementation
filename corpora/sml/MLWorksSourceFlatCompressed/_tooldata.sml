require "../interpreter/shell_types";
require "../interpreter/user_context";
require "../utils/map";
require "../utils/lists";
require "../main/user_options";
require "capi";
require "menus";
require "gui_utils";
require "tooldata";
functor ToolData (
structure ShellTypes : SHELL_TYPES
structure UserContext : USER_CONTEXT
structure UserOptions : USER_OPTIONS
structure GuiUtils : GUI_UTILS
structure Map : MAP
structure Capi : CAPI
structure Menus : MENUS
structure Lists : LISTS
sharing UserContext.Options = ShellTypes.Options
sharing type Menus.Widget = Capi.Widget = GuiUtils.Widget
sharing type UserContext.user_context = ShellTypes.user_context =
GuiUtils.user_context
sharing type UserContext.Context = ShellTypes.Context
sharing type ShellTypes.user_options = UserOptions.user_tool_options
sharing type GuiUtils.ButtonSpec = Menus.ButtonSpec
) : TOOL_DATA =
struct
structure ShellTypes = ShellTypes
structure UserContext = UserContext
type Widget = Capi.Widget
type ButtonSpec = Menus.ButtonSpec
datatype Writable = WRITABLE | ALL
type MotifContext = GuiUtils.MotifContext
datatype current_context =
CURRENT of
{motif_context : MotifContext ref,
context_register:
((int,
(MotifContext -> unit)
* (unit -> UserOptions.user_tool_options)
* Writable) Map.map
* int) ref}
datatype ApplicationData =
APPLICATIONDATA of {applicationShell : Widget, has_controlling_tty: bool}
datatype ToolData =
TOOLDATA of
{args: ShellTypes.ListenerArgs,
appdata : ApplicationData,
current_context : current_context,
motif_context : MotifContext,
tools : (string * (ToolData -> unit) * Writable) list}
fun add_context_fn
(CURRENT {context_register as ref (map, count), ...}, context_fn) =
(context_register := (Map.define (map, count, context_fn), count + 1);
count)
fun remove_context_fn
(CURRENT {context_register as ref (map, count), ...}, key) =
context_register := (Map.undefine (map, key), count)
fun set_current
(CURRENT {motif_context, context_register = ref (map, _), ...},
register_key,
UserOptions.USER_TOOL_OPTIONS ({set_context, ...}, _),
new_context) =
if !set_context then
let
fun do_context (key, (f, mk_user_options, writable)) =
if key <> register_key then
if writable = WRITABLE
andalso UserContext.is_const_context
(GuiUtils.get_user_context (!motif_context)) then
()
else
let
val UserOptions.USER_TOOL_OPTIONS ({sense_context, ...}, _) =
mk_user_options ()
in
if !sense_context then
f new_context
else
()
end
else
()
in
motif_context := new_context;
Map.iterate do_context map
end
else
();
fun get_current (CURRENT {motif_context, ...}) =
!motif_context
fun make_current motif_context =
CURRENT
{motif_context = ref motif_context,
context_register = ref (Map.empty' op<, 0)}
fun exit_mlworks (parent, APPLICATIONDATA {applicationShell, has_controlling_tty}) =
Menus.exit_dialog (parent,applicationShell,has_controlling_tty)
fun copy_args
(ShellTypes.LISTENER_ARGS
{user_context, user_options, user_preferences, prompter, mk_xinterface_fn}) =
ShellTypes.LISTENER_ARGS
{user_context = user_context,
user_options = UserOptions.copy_user_tool_options user_options,
user_preferences = user_preferences,
prompter = prompter,
mk_xinterface_fn = mk_xinterface_fn}
fun copy_tooldata
(TOOLDATA {args, appdata, current_context, motif_context, tools}) =
TOOLDATA {args = copy_args args, appdata = appdata,
current_context = current_context,
motif_context = motif_context, tools = tools}
fun k x y = x
fun tools_menu (mk_tooldata, get_user_context) =
let
val tooldata = mk_tooldata ()
val TOOLDATA {tools, appdata, ...} = tooldata
val APPLICATIONDATA {applicationShell, ...} = appdata
fun is_valid writable =
let
val user_context = get_user_context ()
in
not (UserContext.is_const_context user_context
andalso writable = WRITABLE)
end
val tools_buttons =
map
(fn (name,toolfun,writable) =>
Menus.PUSH (name,
fn _ => toolfun (copy_tooldata (mk_tooldata ())),
fn _ => is_valid writable))
tools
fun get_menu_item (w, s) =
Menus.PUSH (s, fn _ => Capi.to_front w, k true)
in
Menus.get_tools_menuspec (tools_buttons,
fn _ => (map get_menu_item (Capi.get_main_windows())))
end
fun defPush name = (name, fn () => (), fn () => false)
fun getPush items name default =
if (Lists.member (name, map (fn (s,_,_) => s) items)) then
Lists.findp (fn (s,_,_) => (s = name)) items
else
default
fun defToggle name = (name, fn () => false, fn _ => (), fn () => false)
fun getToggle items name default =
if (Lists.member (name, map (fn (s,_,_,_) => s) items)) then
Lists.findp (fn (s,_,_,_) => (s = name)) items
else
default
local
fun extractMenu (Menus.PUSH (s, act, sens)) [] = [(s, act, sens)]
| extractMenu (Menus.PUSH (s, act, sens)) (m1 :: rest) =
(s, act, sens) :: extractMenu m1 rest
| extractMenu (Menus.CASCADE (s, new_list, sens)) [] =
extractMenu Menus.SEPARATOR new_list
| extractMenu (Menus.CASCADE (s, new_list, sens)) (m1 :: rest) =
extractMenu m1 (new_list @ rest)
| extractMenu _ (m1 :: rest) = extractMenu m1 rest
| extractMenu _ [] = []
in
fun extract (Menus.CASCADE (_, item1::rest, _)) = extractMenu item1 rest
| extract (Menus.CASCADE (_,[],_)) = []
| extract _ = []
end
val fileItems = ref []
fun file_menu itemList =
let
fun push name = Menus.PUSH
(getPush itemList name (getPush (!fileItems) name (defPush name)))
in
Menus.CASCADE ("file",
[push "new_proj", push "open_proj", push "save_proj", push "save_proj_as",
Menus.SEPARATOR,
push "close",
Menus.SEPARATOR,
push "setWD", push "load_proj_files", push "saveImage",
Menus.SEPARATOR,
push "use", push "save", push "saveAs",
Menus.SEPARATOR,
push "exit"],
fn _ => true)
end
fun set_global_file_items itemList =
(fileItems := itemList;
file_menu itemList)
val usagePushes = ref []
val usageToggles = ref []
fun usage_menu (pushes, toggles) =
let
fun push s = Menus.PUSH
(getPush pushes s (getPush (!usagePushes) s (defPush s)))
fun toggle s = Menus.TOGGLE
(getToggle toggles s (getToggle (!usageToggles) s (defToggle s)))
in
Menus.CASCADE ("usage_menu",
[push "inspect",
push "show_defn",
push "duplicate",
toggle "autoSelection",
Menus.SEPARATOR,
push "sysMessages",
Menus.SEPARATOR,
push "savePreferences",
Menus.CASCADE ("general", [push "editor",
push "environment"],
fn () => true),
Menus.CASCADE ("tool_settings", [push "valueprinter",
push "internals",
push "layout",
push "graph",
push "insp_item",
push "filterFrames",
push "showFrameInfo",
push "filter"],
fn () => false),
Menus.SEPARATOR,
push "search",
push "sort",
push "addBreakTrace",
push "repeat",
push "removeDuplicates",
push "peel",
push "make_root",
push "original_root",
push "backtrace"], fn () => true)
end
fun set_global_usage_items (pushes, toggles) =
(usagePushes := pushes;
usageToggles := toggles;
usage_menu (pushes, toggles))
fun debug_menu itemList =
let
fun push s = Menus.PUSH (getPush itemList s (defPush s))
in
Menus.CASCADE ("debug_menu",
[push "abort", push "continue", push "step", push "next",
Menus.SEPARATOR,
push "trace", push "untrace"],
fn () => true)
end
fun edit_menu
(widget,
{cut,paste,copy,delete,selection_made,edit_possible,edit_source,delete_all}) =
let
val (cut,can_cut) =
case cut of
SOME c => (c,true)
| _ => (k (),false)
val (paste,can_paste) =
case paste of
SOME p => (p,true)
| _ => (k (),false)
val (copy, can_copy) =
case copy of
SOME c => (c,true)
| _ => (k (),false)
val (delete,can_delete) =
case delete of
SOME d => (d,true)
| _ => (k (), false)
val es = extract (Menus.CASCADE ("dummy", edit_source, fn _ => false))
val items = es @
(case delete_all of SOME da => [da] | NONE => [])
fun push item = Menus.PUSH (getPush items item (defPush item))
in
Menus.CASCADE
("edit",
[Menus.PUSH ("cut", cut,
fn _ => can_cut andalso edit_possible () andalso selection_made()),
Menus.PUSH ("copy", copy,
fn _ => can_copy andalso selection_made ()),
Menus.PUSH ("paste", paste,
fn _ => can_paste andalso edit_possible () andalso
not (Capi.clipboard_empty widget)),
Menus.PUSH ("delete", delete,
fn _ => can_delete andalso edit_possible () andalso
selection_made ()),
push "deleteAll",
Menus.SEPARATOR,
push "editSource"],
k true)
end
end
;
