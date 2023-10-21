require "../basis/__int";
require "^.utils.__terminal";
require "../main/info";
require "../utils/lists";
require "../main/preferences";
require "../main/user_options";
require "tooldata";
require "gui_utils";
require "capi";
require "menus";
require "evaluator";
functor Evaluator (
structure Lists: LISTS
structure UserOptions : USER_OPTIONS
structure Preferences : PREFERENCES
structure ToolData : TOOL_DATA
structure Menus : MENUS
structure GuiUtils : GUI_UTILS
structure Capi : CAPI
structure Info : INFO
sharing type Preferences.user_preferences =
ToolData.ShellTypes.user_preferences =
GuiUtils.user_preferences
sharing type GuiUtils.user_context_options =
ToolData.UserContext.user_context_options =
UserOptions.user_context_options
sharing type ToolData.ShellTypes.user_options =
GuiUtils.user_tool_options =
UserOptions.user_tool_options
sharing type Menus.Widget =
ToolData.Widget = GuiUtils.Widget =
Capi.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec = ToolData.ButtonSpec
sharing type ToolData.ShellTypes.user_context =
GuiUtils.user_context
sharing type Preferences.preferences =
ToolData.ShellTypes.preferences
sharing type GuiUtils.MotifContext = ToolData.MotifContext
): EVALUATOR =
struct
structure Location = Info.Location
structure ShellTypes = ToolData.ShellTypes
structure Options = ShellTypes.Options
structure UserContext = ToolData.UserContext
structure Option = MLWorks.Option
type Widget = Capi.Widget
type Context = ShellTypes.Context
type UserOptions = UserOptions.user_tool_options
type ToolData = ToolData.ToolData
val evaluator_number = ref 1
val do_debug = false
fun debug s =
if do_debug then Terminal.output(s ^ "\n") else ()
fun create (parent,
tooldata as ToolData.TOOLDATA
{args, current_context, motif_context, tools, ...},
destroy_fn) =
let
val ShellTypes.LISTENER_ARGS {user_options,
user_preferences,
prompter,
mk_xinterface_fn,
...} = args
val (full_menus, update_fns) =
case user_preferences
of Preferences.USER_PREFERENCES ({full_menus, ...}, update_fns) =>
(!full_menus, update_fns)
val title =
let
val n = !evaluator_number
in
evaluator_number := n+1;
"Source Browser #" ^ Int.toString n
end
val (shell,frame,menuBar,contextLabel) =
Capi.make_main_window ("evaluator", title, parent, false)
val paned =
Capi.make_managed_widget ("paned", Capi.Paned, frame, [])
val sourcePane =
Capi.make_managed_widget ("sourcePane", Capi.Form, paned,[]);
val sourceTitleLabel =
Capi.make_managed_widget
("sourceTitleLabel", Capi.Label, sourcePane, [])
val (sourceScroll,sourceText) =
Capi.make_scrolled_text ("sourceText", sourcePane, [])
val resultPane =
Capi.make_managed_widget ("resultPane", Capi.Form, paned, [])
val resultTitleLabel =
Capi.make_managed_widget
("resultTitleLabel", Capi.Label, resultPane, [])
val (resultScroll,resultText) =
Capi.make_scrolled_text ("resultText", resultPane, [])
val local_context = ref motif_context
fun get_user_context () = GuiUtils.get_user_context (!local_context)
fun get_context () = UserContext.get_context (get_user_context ())
fun get_user_options () = user_options
fun beep _ = Capi.beep shell
fun get_user_options () = user_options
fun close_window _ =
(destroy_fn ();
Capi.destroy shell)
val do_automatic = ref false
val current_item_ref = ref NONE
fun get_value () = !current_item_ref
fun duplicate (src, res) =
let
val f = create (parent, tooldata, fn _ => ())
in
f false (src, res)
end
val main_menu =
Menus.CASCADE
("main",
[Menus.TOGGLE
("autoSelection",
fn _ => !do_automatic,
fn b => do_automatic := b,
fn _ => true),
Menus.PUSH
("duplicate",
fn _ =>
case get_value () of
SOME x => duplicate x
| _ => (),
fn _ =>
case get_value () of
SOME x => true
| _ => false),
Menus.SEPARATOR,
Menus.PUSH ("close", close_window, fn _ => true)],
fn _ => true)
val view_options =
GuiUtils.view_options
{parent = shell, title = title, user_options = user_options,
user_preferences = user_preferences,
caller_update_fn = fn _ => (),
view_type = GuiUtils.VIEW_ALL}
val view_menu =
Menus.CASCADE ("view", view_options, fn _ => true)
fun show_defn auto (src, res) =
if auto andalso not (!do_automatic) then
()
else
(Capi.Text.set_string (sourceText, src);
Capi.Text.set_string (resultText, res);
current_item_ref := SOME (src, res);
Capi.to_front shell)
fun get_selection _ =
let
val s1 = Capi.Text.get_selection sourceText
in
if s1 = "" then Capi.Text.get_selection resultText
else s1
end
val menuspec =
[main_menu,
ToolData.edit_menu
(shell,
{cut = NONE,
paste = NONE,
copy = SOME (fn _ => Capi.Text.copy_selection sourceText),
delete = NONE,
edit_possible = fn _ => false,
selection_made = fn _ => get_selection () <> "",
tail = []}),
view_menu]
val sep_size = 10
in
Menus.make_submenus (menuBar,menuspec);
Capi.Layout.lay_out
(sourcePane,
[Capi.Layout.FIXED sourceTitleLabel,
Capi.Layout.FLEX sourceScroll,
Capi.Layout.SPACE]);
Capi.Layout.lay_out
(resultPane,
[Capi.Layout.FIXED resultTitleLabel,
Capi.Layout.FLEX resultScroll,
Capi.Layout.SPACE]);
case contextLabel
of MLWorks.SOME w =>
Capi.Layout.lay_out
(frame,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.FIXED w,
Capi.Layout.PANED paned])
| MLWorks.NONE =>
Capi.Layout.lay_out
(frame,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.PANED paned]);
Capi.initialize_toplevel shell;
show_defn
end
fun show_defn (parent, tooldata) =
let
val display_fun = ref NONE
fun destroy_fun _ = display_fun := NONE
in
fn auto =>
fn (src, res) =>
case !display_fun of
SOME f => f auto (src, res)
| _ =>
if auto then
()
else
let
val f = create (parent, tooldata, destroy_fun)
in
f false (src, res);
display_fun := SOME f
end
end
end;
