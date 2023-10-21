require "../basis/__text_io";
require "../basis/__io";
require "../basis/__text_prim_io";
require "^.utils.__terminal";
require "capi";
require "menus";
require "../utils/lists";
require "../main/user_options";
require "../utils/crash";
require "../utils/getenv";
require "../main/preferences";
require "../main/machspec";
require "../typechecker/types";
require "../interpreter/shell_utils";
require "../interpreter/user_context";
require "../interpreter/entry";
require "../editor/custom";
require "gui_utils";
functor GuiUtils (
structure Capi: CAPI
structure Lists: LISTS
structure Crash: CRASH
structure UserOptions: USER_OPTIONS
structure Preferences: PREFERENCES
structure MachSpec : MACHSPEC
structure Menus: MENUS
structure UserContext: USER_CONTEXT
structure Entry: ENTRY
structure ShellUtils: SHELL_UTILS
structure Getenv: GETENV
structure Types : TYPES
structure CustomEditor: CUSTOM_EDITOR
sharing UserOptions.Options = ShellUtils.Options = UserContext.Options
sharing type Menus.Widget = Capi.Widget
sharing type ShellUtils.preferences = Preferences.preferences
sharing type ShellUtils.user_preferences = Preferences.user_preferences
sharing type ShellUtils.Context = UserContext.Context = Entry.Context
sharing type UserOptions.user_tool_options = ShellUtils.UserOptions
sharing type UserOptions.user_context_options =
UserContext.user_context_options
sharing type ShellUtils.user_context = UserContext.user_context
sharing type Entry.options = UserOptions.Options.options
): GUI_UTILS =
struct
structure Options = UserOptions.Options
type Widget = Capi.Widget
type ButtonSpec = Menus.ButtonSpec
type OptionSpec = Menus.OptionSpec
type Type = ShellUtils.Type
type user_tool_options = UserOptions.user_tool_options
type user_context_options = UserOptions.user_context_options
type user_preferences = Preferences.user_preferences
type user_context = UserContext.user_context
val print = Terminal.output
fun make_outstream insert_text =
let
fun writeVec{buf, i, sz} =
let
val len = case sz of
NONE => size buf - i
| SOME i => i
val _ = insert_text(if i = 0 andalso len = size buf then buf else substring(buf, i, len));
in
len
end
val prim_writer =
TextPrimIO.WR{name = "console writer",
chunkSize = 1,
writeVec = SOME writeVec,
writeArr = NONE,
writeVecNB = NONE,
writeArrNB = NONE,
block = NONE,
canOutput = SOME(fn _ => true),
getPos = NONE,
setPos = NONE,
endPos = NONE,
verifyPos = NONE,
close = fn _ => (),
ioDesc = NONE}
in
TextIO.mkOutstream(TextIO.StreamIO.mkOutstream(TextPrimIO.augmentWriter prim_writer, IO.NO_BUF))
end
datatype Writable = WRITABLE | ALL
val title_for_global_dialogs = "MLWorks Preferences"
abstype MotifContext =
SHORT_MOTIF_CONTEXT of
{user_context: user_context,
mode_dialog: unit -> unit,
update_fn: unit -> unit}
| FULL_MOTIF_CONTEXT of
{user_context: user_context,
mode_dialog: unit -> unit,
compiler_dialog: unit -> unit,
language_dialog: unit -> unit,
update_fn: unit -> unit}
with
fun get_user_context (SHORT_MOTIF_CONTEXT r) = #user_context r
| get_user_context (FULL_MOTIF_CONTEXT r) = #user_context r;
fun get_context_name m =
UserContext.get_context_name (get_user_context m)
val context_list = ref []
fun get_mode_dialog (FULL_MOTIF_CONTEXT r) = #mode_dialog r
| get_mode_dialog (SHORT_MOTIF_CONTEXT r) = #mode_dialog r
fun get_compiler_dialog (FULL_MOTIF_CONTEXT r) = #compiler_dialog r
| get_compiler_dialog (SHORT_MOTIF_CONTEXT r) =
Crash.impossible "get_compiler_dialog"
fun get_language_dialog (FULL_MOTIF_CONTEXT r) =
#language_dialog r
| get_language_dialog (SHORT_MOTIF_CONTEXT r) =
Crash.impossible "get_language_dialog"
fun make_context (user_context, parent, user_preferences) =
let
val user_context_options = UserContext.get_user_options user_context
val UserOptions.USER_CONTEXT_OPTIONS (options, update_fns) =
user_context_options
fun set_context_option_fun f a =
((f options) := a;
true)
fun get_context_option_fun f () =
!(f options)
fun int_context_widget (name, accessor) =
Menus.OPTINT
(name,
get_context_option_fun accessor,
set_context_option_fun accessor)
fun bool_context_widget (name, accessor) =
Menus.OPTTOGGLE
(name,
get_context_option_fun accessor,
set_context_option_fun accessor)
fun do_update () = app (fn f => f ()) (!update_fns);
fun is_sml'97 _ =
UserOptions.is_sml'97 user_context_options
fun sml'97 true =
(UserOptions.select_sml'97 user_context_options;
Types.real_tyname_equality_attribute := false;
true)
| sml'97 false = true
fun is_compatibility _ =
UserOptions.is_compatibility user_context_options
fun compatibility true =
(UserOptions.select_compatibility user_context_options;
true)
| compatibility false = true
fun is_sml'90 _ =
UserOptions.is_sml'90 user_context_options
fun sml'90 true =
(UserOptions.select_sml'90 user_context_options;
Types.real_tyname_equality_attribute := true;
true)
| sml'90 false = true
fun is_quick_compile _ =
UserOptions.is_quick_compile user_context_options
fun quick_compile true =
(UserOptions.select_quick_compile user_context_options;
true)
| quick_compile false = true
fun is_optimizing _ =
UserOptions.is_optimizing user_context_options
fun optimizing true =
(UserOptions.select_optimizing user_context_options;
true)
| optimizing false = true
fun is_debugging _ =
UserOptions.is_debugging user_context_options
fun debugging true =
(UserOptions.select_debugging user_context_options;
true)
| debugging false = true
val Preferences.USER_PREFERENCES (user_preferences, _) =
user_preferences
val full_menus = !(#full_menus user_preferences)
fun popup_mode_options parent =
Menus.create_dialog
(parent,
title_for_global_dialogs,
"modeOptions",
do_update,
[Menus.OPTLABEL "modeOptionsLabel",
Menus.OPTSEPARATOR,
Menus.OPTRADIO
[Menus.OPTTOGGLE ("sml_97", is_sml'97, sml'97),
Menus.OPTTOGGLE ("sml_90", is_sml'90, sml'90),
Menus.OPTTOGGLE
("compatibilityMode", is_compatibility, compatibility)],
Menus.OPTSEPARATOR,
Menus.OPTRADIO
[Menus.OPTTOGGLE ("debugging", is_debugging, debugging),
Menus.OPTTOGGLE ("quick_compile", is_quick_compile, quick_compile),
Menus.OPTTOGGLE ("optimizing", is_optimizing, optimizing)]])
val (mode_dialog,mode_dialog_update) =
popup_mode_options parent
in
let
fun popup_compiler_options parent =
Menus.create_dialog
(parent,
title_for_global_dialogs,
"compilerOptions",
do_update,
[Menus.OPTLABEL "compilerOptionsLabel",
Menus.OPTSEPARATOR,
bool_context_widget
("generateInterruptableCode", #generate_interruptable_code),
bool_context_widget
("generateInterceptableCode", #generate_interceptable_code),
bool_context_widget
("generateDebugInfo", #generate_debug_info),
bool_context_widget
("generateVariableDebugInfo",#generate_variable_debug_info)]
@ [Menus.OPTSEPARATOR,
bool_context_widget("optimizeLeafFns", #optimize_leaf_fns),
bool_context_widget
("optimizeTailCalls", #optimize_tail_calls),
bool_context_widget
("optimizeSelfTailCalls",#optimize_self_tail_calls)]
@
(case MachSpec.mach_type of
MachSpec.MIPS =>
[Menus.OPTSEPARATOR,
bool_context_widget("mipsR4000", #mips_r4000)]
| MachSpec.SPARC =>
[Menus.OPTSEPARATOR,
bool_context_widget("sparcV7", #sparc_v7)]
| MachSpec.I386 =>
[]))
fun popup_language_options parent =
Menus.create_dialog
(parent,
title_for_global_dialogs,
"languageOptions",
do_update,
[Menus.OPTLABEL "compatibilityOptionsLabel",
Menus.OPTSEPARATOR,
Menus.OPTTOGGLE ("oldDefinition",
get_context_option_fun #old_definition,
fn b =>
(Types.real_tyname_equality_attribute := b;
set_context_option_fun #old_definition b)),
bool_context_widget("abstractions",#abstractions),
bool_context_widget("opInDatatype", #nj_op_in_datatype),
bool_context_widget("njSignatures", #nj_signatures),
bool_context_widget("weakTyvars", #weak_type_vars),
bool_context_widget("fixitySpecs", #fixity_specs),
bool_context_widget("openFixity", #open_fixity),
Menus.OPTSEPARATOR,
Menus.OPTLABEL "extensionsOptionsLabel",
Menus.OPTSEPARATOR,
bool_context_widget("requireKeyword",#require_keyword),
bool_context_widget("typeDynamic",#type_dynamic)
])
val (compiler_dialog,compiler_dialog_update) =
popup_compiler_options parent
val (language_dialog,language_dialog_update) =
popup_language_options parent
fun update_dialogues () =
app
(fn f => f ())
[mode_dialog_update,
compiler_dialog_update,
language_dialog_update]
val result =
FULL_MOTIF_CONTEXT
{user_context = user_context,
mode_dialog = mode_dialog,
compiler_dialog = compiler_dialog,
language_dialog = language_dialog,
update_fn = update_dialogues}
in
context_list := result :: !context_list;
update_fns := [update_dialogues];
result
end
end
end
val initialContext = ref NONE
fun makeInitialContext (parent, user_preferences) =
let
val user_context = UserContext.getInitialContext ()
val motif_context =
make_context (user_context, parent, user_preferences)
in
initialContext := SOME motif_context;
context_list := [motif_context]
end
fun getInitialContext () =
case !initialContext
of SOME c => c
| _ => Crash.impossible "Bad initial motif context!"
fun save_history (prompt, user_context, applicationShell) =
let
val filename_opt =
if prompt then
Capi.save_as_dialog (applicationShell, ".sml")
else
case UserContext.get_saved_file_name user_context
of NONE =>
Capi.save_as_dialog (applicationShell, ".sml")
| x => x
in
case filename_opt of
NONE => ()
| SOME filename =>
let
val file = TextIO.openOut filename
fun examine_source (s, ~1, seen_newline) =
(false, false)
| examine_source (s, n, seen_newline) =
let
val c = MLWorks.String.ordof (s, n)
in
if c = ord #";" then
(true, seen_newline)
else if c = ord #"\n" then
examine_source (s, n-1, true)
else if c = ord #" " orelse c = ord #"\t" then
examine_source (s, n-1, seen_newline)
else
(false, false)
end
fun massage_source s =
let
val (has_semicolon, has_newline) =
examine_source (s, size s - 1, false)
in
s ^ (if has_semicolon then "" else ";")
^ (if has_newline then "" else Capi.terminator)
end
fun write_hist (UserContext.ITEM {source, ...}) =
case source
of UserContext.STRING str =>
TextIO.output (file, massage_source str)
| _ => ()
val context_name = UserContext.get_context_name user_context
val hist = UserContext.get_history user_context
in
app write_hist (rev hist);
TextIO.flushOut file;
TextIO.closeOut file;
Capi.send_message
(applicationShell, "Saved " ^ context_name ^ " to " ^ filename);
UserContext.set_saved_file_name (user_context, filename)
end
handle IO.Io _ => ()
end
fun null_history user_context =
let
val hist = UserContext.get_history user_context
in
length hist = 0
end
fun save_name_set user_context =
case UserContext.get_saved_file_name user_context
of NONE => false
| SOME _ => true
fun make_search_dialog (shell, get_context, action_fn, choose_contexts) =
let
fun flat (x::xs) = (concat x ^ "\n") :: (flat xs)
| flat [] = []
val searchOptions =
{showSig = ref true,
showStr = ref true,
showFun = ref true,
searchInitial = ref choose_contexts,
searchContext = ref choose_contexts,
showType = ref false}
fun mkSearchOptions
{showSig, showStr, showFun, searchInitial,
searchContext, showType} =
Entry.SEARCH_OPTIONS
{showSig = !showSig,
showStr = !showStr,
showFun = !showFun,
searchInitial = !searchInitial,
searchContext = !searchContext,
showType = !showType}
val searchString = ref ""
fun search s =
let
fun getItemsFromContext c =
let
val context = UserContext.get_delta (get_user_context c)
in
Entry.context2entry context
end
fun grep regexp line =
let
fun startsWith [] ys = true
| startsWith xs [] = false
| startsWith (x::xs) (y::ys) = (x=y) andalso (startsWith xs ys)
fun check [] ys = false
| check xs [] = false
| check xs (y::ys) = startsWith xs (y::ys) orelse check xs ys
in
check (explode regexp) (explode line)
end
val _ = searchString := s
val context_list =
if !(#searchInitial searchOptions) then
if !(#searchContext searchOptions) then
getItemsFromContext(getInitialContext())
@ getItemsFromContext (get_context ())
else
getItemsFromContext(getInitialContext())
else
getItemsFromContext (get_context ())
val options = Options.default_options
val entrys = map Entry.massage context_list
val entrys' =
Entry.printEntry1
(mkSearchOptions searchOptions, options, entrys)
val found = map #1 (Lists.filterp (fn (_,name) => grep s name) entrys')
val _ =
Capi.list_select
(shell, "searchList", fn _ => ())
(found, action_fn, fn x => x)
in
()
end
fun getter r () = !r
fun setter r b = (r := b; true)
fun toggle (s, r) = Menus.OPTTOGGLE(s, getter r, setter r)
val tail =
if choose_contexts then
[Menus.OPTSEPARATOR,
toggle ("searchPervasives",#searchInitial searchOptions),
toggle ("searchUserContext",#searchContext searchOptions)]
else
[]
val search_for = ref NONE
val searchSpec =
[Menus.OPTTEXT
("itemSearch", fn () => !searchString ,
fn s => (search_for := SOME s; true)),
Menus.OPTSEPARATOR,
Menus.OPTLABEL "Search inside...",
Menus.OPTSEPARATOR,
toggle ("signatures", #showSig searchOptions),
toggle ("functors", #showFun searchOptions),
Menus.OPTSEPARATOR,
toggle ("displayEntryTypes", #showType searchOptions)]
@ tail
in
Menus.create_dialog
(shell, "Search Dialog", "browserDialog",
fn () => (case !search_for of NONE => () | SOME s => (search s; ())),
searchSpec)
end
fun search_button (shell, get_context, action_fn, choose_contexts) =
let
val (searchPopup, _) =
make_search_dialog (shell, get_context, action_fn, choose_contexts)
in
Menus.PUSH ("search", searchPopup, fn _ => true)
end
fun context_menu
{set_state, get_context, writable, applicationShell,
shell, user_preferences} =
let
val Preferences.USER_PREFERENCES (preferences_record, _) =
user_preferences
fun get_current_user_context () =
get_user_context (get_context ())
val tail_menu =
if !(#full_menus preferences_record) then
let
fun select_menu () =
let
fun make_item c =
let
val name = get_context_name c
in
Menus.PUSH (name, fn _ => set_state c, fn _ => true)
end
val contexts =
if writable = WRITABLE then
Lists.filter_outp
(UserContext.is_const_context o get_user_context)
(!context_list)
else
!context_list
in
map make_item contexts
end
fun push_state _ =
set_state
(make_context
(UserContext.copyUserContext (get_current_user_context ()),
applicationShell, user_preferences))
fun initialContext _ =
set_state
(make_context
(UserContext.getNewInitialContext (),
applicationShell, user_preferences))
val is_constant =
UserContext.is_const_context o get_current_user_context
val sub_tail =
[Menus.SEPARATOR,
Menus.DYNAMIC ("contextSelect", select_menu, fn _ => true)]
in
if writable = WRITABLE then
[Menus.SEPARATOR,
Menus.PUSH ("pushContext", push_state, fn _ => true),
Menus.PUSH ("initialContext", initialContext, fn _ => true)]
@ sub_tail
else
sub_tail
end
else
[]
val save_items =
if UserContext.is_const_context (get_current_user_context ()) then
[]
else
[Menus.PUSH
("save",
fn _ =>
save_history
(false, get_current_user_context (), shell),
fn _ => not (null_history (get_current_user_context ()))
andalso save_name_set (get_current_user_context ())),
Menus.PUSH
("saveAs",
fn _ =>
save_history
(true, get_current_user_context (), shell),
fn _ => not (null_history (get_current_user_context ())))]
in
Menus.CASCADE
("context", save_items @ tail_menu, fn _ => true)
end
fun listener_properties (parent, get_context) =
let
fun popup_mode_dialog _ =
(get_mode_dialog (get_context ())) ()
fun popup_compiler_dialog _ =
(get_compiler_dialog (get_context ())) ()
fun popup_language_dialog _ =
(get_language_dialog (get_context ())) ()
in
Menus.CASCADE ("listen_props",
[Menus.PUSH("mode", popup_mode_dialog, fn _ => true),
Menus.PUSH("compiler", popup_compiler_dialog, fn _ => true),
Menus.PUSH("language", popup_language_dialog, fn _ => true)],
fn _ => true)
end
fun setup_menu (parent, get_context, user_preferences, get_user_context_options) =
let
val Preferences.USER_PREFERENCES (preferences, update_fns) =
user_preferences
fun preference_update () = ()
fun set_preference_fun f a =
((f preferences) := a;
true)
fun get_preference_fun f () =
!(f preferences)
fun popup_editor_options parent =
Menus.create_dialog
(parent,
title_for_global_dialogs,
"editorOptions",
preference_update,
[Menus.OPTLABEL "editorOptionsLabel",
Menus.OPTSEPARATOR,
Menus.OPTRADIO
[Menus.OPTTOGGLE ("select_external_editor",
fn () => case get_preference_fun (#editor) () of
"External" => true
| _ => false,
fn true => set_preference_fun (#editor) "External"
| false => true),
Menus.OPTTOGGLE ("select_one_way_editor",
fn () => case get_preference_fun (#editor) () of
"OneWay" => true
| _ => false,
fn true => set_preference_fun (#editor) "OneWay"
| false => true),
Menus.OPTTOGGLE ("select_two_way_editor",
fn () => case get_preference_fun (#editor) () of
"TwoWay" => true
| _ => false,
fn true => set_preference_fun (#editor) "TwoWay"
| false => true)],
Menus.OPTTEXT ("external_editor_command",
get_preference_fun (#externalEditorCommand),
set_preference_fun (#externalEditorCommand)),
Menus.OPTLABEL "editorOneWayLabel",
Menus.OPTCOMBO ("one_way_editor_name",
fn () => (
get_preference_fun (#oneWayEditorName) (),
CustomEditor.commandNames()),
set_preference_fun (#oneWayEditorName)),
Menus.OPTLABEL "editorTwoWayLabel",
Menus.OPTCOMBO ("two_way_editor_name",
fn () => (
get_preference_fun (#twoWayEditorName) (),
CustomEditor.dialogNames()),
set_preference_fun (#twoWayEditorName))])
fun popup_environment_options parent =
Menus.create_dialog
(parent,
title_for_global_dialogs,
"environmentOptions",
preference_update,
[Menus.OPTLABEL "environmentOptionsLabel",
Menus.OPTSEPARATOR,
Menus.OPTINT ("maximumHistoryLength",
get_preference_fun (#history_length),
fn x =>
x > 0 andalso
(set_preference_fun (#history_length) x)),
Menus.OPTINT ("maximumNumberErrors",
get_preference_fun (#max_num_errors),
fn x =>
x > 0 andalso
(set_preference_fun (#max_num_errors) x)),
Menus.OPTTOGGLE ("useRelativePathname",
get_preference_fun (#use_relative_pathname),
set_preference_fun (#use_relative_pathname)),
Menus.OPTTOGGLE ("completionMenu",
get_preference_fun (#completion_menu),
set_preference_fun (#completion_menu)),
Menus.OPTTOGGLE ("useDebugger",
get_preference_fun (#use_debugger),
set_preference_fun (#use_debugger)),
Menus.OPTTOGGLE ("useErrorBrowser",
get_preference_fun (#use_error_browser),
set_preference_fun (#use_error_browser)),
Menus.OPTTOGGLE ("windowDebugger",
get_preference_fun (#window_debugger),
set_preference_fun (#window_debugger))])
val (editor_dialog,editor_update) =
popup_editor_options parent
val (environment_dialog,environment_update) =
popup_environment_options parent
fun save_preferences _ =
case Getenv.get_preferences_filename () of
NONE => ()
| SOME pathname =>
let
val outstream = TextIO.openOut pathname
in
(Preferences.save_to_stream (user_preferences,outstream);
UserOptions.save_to_stream (get_user_context_options(),outstream))
handle exn => (TextIO.closeOut outstream; raise exn);
TextIO.closeOut outstream
end
in
update_fns := editor_update :: environment_update :: !update_fns;
[("editor", fn _ => editor_dialog (), fn _ => true),
("environment", fn _ => environment_dialog (), fn _ => true),
("savePreferences", save_preferences, fn _ => true)]
end
datatype ViewOptions = SENSITIVITY | VALUE_PRINTER | INTERNALS
fun view_options
{parent, title, user_options, user_preferences,
caller_update_fn, view_type} =
let
val UserOptions.USER_TOOL_OPTIONS (options, update_fns) =
user_options
val Preferences.USER_PREFERENCES (preferences, _) =
user_preferences
fun set_tool_option_fun f a =
((f options) := a;
true)
fun get_tool_option_fun f () =
!(f options)
fun int_tool_widget (name, accessor) =
Menus.OPTINT
(name,
get_tool_option_fun accessor,
set_tool_option_fun accessor)
fun bool_tool_widget (name, accessor) =
Menus.OPTTOGGLE
(name,
get_tool_option_fun accessor,
set_tool_option_fun accessor)
fun do_update () =
(app
(fn f => f ())
(!update_fns);
caller_update_fn user_options)
fun popup_valueprinter_options parent =
Menus.create_dialog
(parent,
title,
"valuePrinterOptions",
do_update,
[Menus.OPTLABEL "valuePrinterOptionsLabel",
Menus.OPTSEPARATOR,
bool_tool_widget("showFnDetails",#show_fn_details),
bool_tool_widget("showExnDetails",#show_exn_details),
int_tool_widget("floatPrecision",#float_precision),
int_tool_widget("maximumSeqSize",#maximum_seq_size),
int_tool_widget("maximumStringSize",#maximum_string_size),
int_tool_widget("maximumDepth",#maximum_depth),
int_tool_widget("maximumRefDepth",#maximum_ref_depth),
int_tool_widget("maximumSigDepth",#maximum_sig_depth),
int_tool_widget("maximumStrDepth",#maximum_str_depth)
])
fun popup_sensitivity_options parent =
Menus.create_dialog
(parent,
title,
"sensitivityOptions",
do_update,
[Menus.OPTLABEL "sensitivityOptionsLabel",
Menus.OPTSEPARATOR]
@ (if !(#full_menus preferences) then
[bool_tool_widget("senseContext",#sense_context),
bool_tool_widget("setContext",#set_context)]
else
[]))
fun popup_internals_options parent =
Menus.create_dialog
(parent,
title,
"internalsOptions",
do_update,
[Menus.OPTLABEL "internalsOptionsLabel",
Menus.OPTSEPARATOR,
bool_tool_widget("showAbsyn",#show_absyn),
bool_tool_widget("showLambda",#show_lambda),
bool_tool_widget("showOptLambda",#show_opt_lambda),
bool_tool_widget("showEnviron",#show_environ),
bool_tool_widget("showMir",#show_mir),
bool_tool_widget("showOptMir",#show_opt_mir),
bool_tool_widget("showMach",#show_mach)])
fun add_item (menu_spec, SENSITIVITY) =
if !(#full_menus preferences) then
let
val (sensitivity_dialog, sensitivity_dialog_update) =
popup_sensitivity_options parent
in
update_fns := sensitivity_dialog_update :: !update_fns;
Menus.PUSH
("sensitivity", fn _ => sensitivity_dialog (), fn _ => true) ::
menu_spec
end
else
menu_spec
| add_item (menu_spec, VALUE_PRINTER) =
let
val (valueprinter_dialog, valueprinter_dialog_update) =
popup_valueprinter_options parent
in
update_fns := valueprinter_dialog_update :: !update_fns;
Menus.PUSH
("valueprinter", fn _ => valueprinter_dialog (), fn _ => true) ::
menu_spec
end
| add_item (menu_spec, INTERNALS) =
let
val (internals_dialog, internals_dialog_update) =
popup_internals_options parent
in
update_fns := internals_dialog_update :: !update_fns;
Menus.PUSH
("internals", fn _ => internals_dialog (), fn _ => true) ::
menu_spec
end
in
update_fns := [];
Menus.CASCADE ("dummy", Lists.reducel add_item ([], rev view_type), fn () => false)
end
local
val do_debug = false
fun debug s = if do_debug then Terminal.output(s ^ "\n") else ()
in
fun value_menu {parent, user_preferences, inspect_fn, get_value, enabled, tail} =
let
val current_item = ref NONE : (string * (MLWorks.Internal.Value.T * Type)) option ref
fun message_fun s =
Capi.send_message (parent, s)
fun set_current_item () =
current_item := get_value ()
fun is_current_item _ =
case !current_item of
NONE => false
| _ => true
fun get_current_item _ =
case !current_item of
NONE => Crash.impossible "get_current_item"
| SOME x => x
fun get_current_value _ = #1 (#2 (get_current_item ()))
fun object_editable _ =
(set_current_item ();
is_current_item () andalso enabled andalso
ShellUtils.object_editable (get_current_value ()))
fun object_traceable _ =
(set_current_item (); is_current_item () andalso enabled andalso
ShellUtils.object_traceable (get_current_value()))
fun edit_object _ =
let
val preferences = Preferences.new_preferences user_preferences
in
(ignore(ShellUtils.edit_object (get_current_value (), preferences));())
handle ShellUtils.EditFailed s => message_fun ("Edit failed: " ^ s)
end
fun trace_object _ = ShellUtils.trace (get_current_value())
fun untrace_object _ = ShellUtils.untrace(get_current_value())
val (inspect_object,object_inspectable) =
case inspect_fn of
SOME f => (f o get_current_item,
fn () => (set_current_item(); is_current_item() andalso enabled))
| _ => (fn _ => Crash.impossible "inspect_fn",fn _ => false)
in
Menus.CASCADE ("value",
[Menus.PUSH ("editSource",
edit_object,
object_editable),
Menus.PUSH ("inspect",
inspect_object,
object_inspectable),
Menus.PUSH ("trace",
trace_object,
object_traceable),
Menus.PUSH ("untrace",
untrace_object,
object_traceable)]
@ (case tail
of [] => []
| l => Menus.SEPARATOR :: l),
fn _ => true)
end
end
local
fun test_val (r,v) = (fn _ => (!r = v))
fun set_val (r,v) = (fn b => ((if b then (r := v) else ());
true))
fun test_ref (r) = (fn _ => !r)
fun set_ref (r) = (fn n => (r := n;
true))
in
fun toggle_value (s,r,v) = Menus.OPTTOGGLE (s, test_val(r,v), set_val(r,v))
fun bool_value (s,r) = Menus.OPTTOGGLE (s,test_ref r, set_ref r)
fun text_value (s,r) = Menus.OPTTEXT (s,test_ref r, set_ref r)
fun int_value (s,r) = Menus.OPTINT (s,test_ref r, set_ref r)
end
local
fun get_max_history_width user_options = 30
fun contains_nasty_chars s =
let
fun aux n =
if n = 0 then false
else
let val chr = MLWorks.String.ordof(s,n-1)
in
if chr = ord #"\n" orelse chr = ord #"\t"
then true
else aux (n-1)
end
in
aux (size s)
end
fun remove_nasty_chars s =
let fun subst #"\n" = #" "
| subst #"\t" = #" "
| subst c = c
in
implode (map subst (explode s))
end
fun trim_history_string (s,user_options) =
let
val max_width = get_max_history_width user_options
val trim_string =
if size s > max_width
then substring (s,0,max_width - 2) ^ ".."
else s
in
if contains_nasty_chars trim_string
then remove_nasty_chars trim_string
else trim_string
end
fun get_max_history_length user_preferences =
let
val Preferences.USER_PREFERENCES ({history_length,...}, _) =
user_preferences
in
!history_length
end
fun whitespacep x =
case x of
#" " => true
| #"\n" => true
| #"\t" => true
| #"\012" => true
| #"\013" => true
| _ => false
fun strip_whitespace s =
let
fun strip [] = []
| strip (l as (a::b)) =
if whitespacep a then strip b else l
in
implode (rev (strip (rev (strip (explode s)))))
end
in
fun make_history (user_preferences, use_entry) =
let
val history = ref []: (string * int) list ref;
val history_size = ref 0;
val initial_index = ~1;
val history_index = ref initial_index;
fun add_history_entry "" = ()
| add_history_entry item =
let
fun aux ([], _) = ([], 0)
| aux ((s,i)::l, ix) =
if ix <= 1 then
([], 0)
else if s = item then
(l, i)
else
let
val (l, i) = aux (l, ix - 1)
in
((s, i) :: l, i + 1)
end
val (new_history, new_size) =
aux (!history, get_max_history_length user_preferences)
in
history := (item, new_size) :: new_history;
history_size := new_size + 1
end
fun update_history l =
(app (add_history_entry o strip_whitespace) l;
history_index := initial_index)
fun prev_history () =
let val _ = history_index := !history_index + 1
val line = #1 (Lists.nth (!history_index, !history))
in
use_entry line
end
handle Lists.Nth => history_index := !history_index - 1;
fun next_history () =
let val _ = history_index := !history_index - 1;
val line = #1 (Lists.nth (!history_index, !history))
handle
Lists.Nth =>
(history_index := initial_index;
"" )
in
use_entry line
end
fun history_end () = !history_index = initial_index
fun history_start () = !history_index = !history_size - 1
fun warp_history string =
trim_history_string (string, user_preferences)
val history_menu =
Menus.DYNAMIC
("history",
fn () =>
map
(fn (s,i) =>
Menus.PUSH
(warp_history s,
fn _ =>
(history_index := !history_size - i - 1;
use_entry s),
fn _ => true))
(!history),
fn _ => !history <> [])
in
{update_history = update_history,
prev_history = prev_history,
next_history = next_history,
history_end = history_end,
history_start = history_start,
history_menu = history_menu}
end
end
end
;
