require "../basis/__int";
require "../basis/__real";
require "../system/__time";
require "capi";
require "menus";
require "../utils/lists";
require "../utils/crash";
require "^.utils.__messages";
require "../interpreter/shell_utils";
require "../main/preferences";
require "tooldata";
require "gui_utils";
require "bar_chart";
require "profile_tool";
functor ProfileTool (
structure Capi : CAPI
structure Menus : MENUS
structure Preferences : PREFERENCES
structure ShellUtils : SHELL_UTILS
structure Lists : LISTS
structure Crash : CRASH
structure BarChart : BAR_CHART
structure ToolData : TOOL_DATA
structure GuiUtils : GUI_UTILS
sharing type Menus.Widget = Capi.Widget = BarChart.Widget = ToolData.Widget = GuiUtils.Widget
sharing type Menus.ButtonSpec = ToolData.ButtonSpec = GuiUtils.ButtonSpec
sharing type ShellUtils.preferences = Preferences.preferences
) : PROFILE_TOOL =
struct
type Widget = Capi.Widget
type user_preferences = Preferences.user_preferences
type ToolData = ToolData.ToolData
type user_context = ToolData.ShellTypes.user_context
structure Profile = MLWorks.Profile
val int_string = Int.toString
val real_string = Real.toString
fun message s = Messages.output("pt: "^s^"\n")
local
datatype function_cost = Cost of
{time : real,
space : real,
profile : Profile.function_profile,
id: bool ref
}
datatype results = Result of
{total_time : real,
total_space : real,
total_copied : real,
total_ticks : int,
profile_ticks : int,
gc_ticks : int,
mutator_ticks : int,
functions : function_cost list}
datatype profile_tool =
ProfileTool of {update : results -> unit,
id: unit ref}
fun update_profile_tool (ProfileTool {update,...}, results) =
update results
val active_tool_ref : profile_tool option ref = ref NONE
fun real_size (Profile.Large_Size {megabytes,bytes}) =
if bytes = ~1 then 0.0 else
(real megabytes)* 1048576.0 + (real bytes)
fun allocated (Profile.Function_Space_Profile {allocated,...}) =
real_size allocated
fun copied (Profile.Function_Space_Profile {copied,...}) =
real_size copied
fun top_ticks (Profile.Function_Time_Profile {top,...}) =
real top
fun proportion (x,y) = if Real.==(y,0.0) then 0.0 else x/y
fun mix (f1,f2) (Cost {time,space,...}) = f1 * time + f2 * space
fun mix_greater factors (r1,r2) = let val mix' = mix factors
in (mix' r1) > (mix' r2)
end
val space_factors = (0.0,1.0)
val time_factors = (1.0,0.0)
fun factor space_weight = (1.0-space_weight, space_weight)
fun sort factors functions =
Lists.msort (mix_greater factors) functions
fun results (Profile.Profile
{general = Profile.General {period, ...},
time = Profile.Time {scans, gc_ticks,
profile_ticks,...},
space = Profile.Space {total_profiled,...},
functions,
...}) =
let
val period = Time.toReal period
val total_ticks = scans + gc_ticks + profile_ticks
val real_scans = real scans
val total_space = allocated total_profiled
val total_copied = copied total_profiled
fun convert (fs,
prof as Profile.Function_Profile {id,
time,
space,...}) =
let
val alloc = allocated space
val ticks = top_ticks time
in
if Real.==(alloc,0.0) andalso Real.==(ticks,0.0) then
fs
else
Cost {profile = prof,
time = proportion (ticks, real_scans),
space = proportion (alloc, total_space),
id = ref false} :: fs
end
val functions =
Lists.reducel convert ([],functions)
val sorted_functions = sort (factor 0.0) functions
in
((case sorted_functions of
((Cost {id,...})::_) => id := true
| _ => ());
Result {total_time = period,
total_space = total_space,
total_copied = total_copied,
total_ticks = total_ticks,
profile_ticks = profile_ticks,
gc_ticks = gc_ticks,
mutator_ticks = scans,
functions = sorted_functions})
end
local
fun pad_zero (n,s) =
let
val zeroes = "00000"
val length = size s
in
if length >= n then s
else (MLWorks.String.substring (zeroes,0,n-length))^s
end
in
fun decimal_places n =
let
fun mult (0,x) = x
| mult (n,x) = mult(n-1,x*10.0)
fun round x = floor (mult(n,x)+0.5)
val factor = floor (mult(n,1.0)+0.5)
fun dp x =
(Int.toString (x div factor))^"."^
(pad_zero (n,Int.toString (x mod factor)))
in
fn x => dp (round x)
end
end
fun bytes n =
let
val kilo = 1024.0
val mega = kilo * kilo
val giga = kilo * mega
fun num x = Int.toString (floor x)
val num_two = decimal_places 2
val (num, prefix) =
if n > 10.0 * giga then (num_two (n/giga), "G")
else if n > 10.0 * mega then (num_two (n/mega), "M")
else if n > 10.0 * kilo
then (num_two (n/kilo), "k")
else (num n, "")
in
(num^" "^prefix^"bytes")
end
fun percentage (x,y) =
if Real.==(y,0.0) then "0.00%"
else (decimal_places 2 (x * 100.0/y))^"%"
val chart_spec_ref = ref (BarChart.ChartSpec
{bar_width = 20,
maximum_bars = 20,
maximum_tick_space = 100,
ideal_label_space = 100})
fun set_chart_spec chart_spec = chart_spec_ref := chart_spec
val profile_tool_number = ref 1
val sizeRef = ref NONE
val posRef = ref NONE
fun make_profile_tool (parent, results, user_preferences, mk_tooldata, get_context) =
let
val title =
let val n = !profile_tool_number
in
(profile_tool_number := n+1;
"Profile Tool #" ^ (Int.toString n))
end
val (shell, frame, menuBar,_) =
Capi.make_main_window
{name = "profiler",
title = title,
parent = parent,
contextLabel = false,
winMenu = true,
pos = getOpt (!posRef, Capi.getNextWindowPos())}
val space_weight = ref 0.0
val time_results_widget1 =
Capi.make_managed_widget ("timeResults1",Capi.Label,frame, [])
val time_results_widget2 =
Capi.make_managed_widget ("timeResults2",Capi.Label,frame, [])
val time_results_widget3 =
Capi.make_managed_widget ("timeResults3",Capi.Label,frame, [])
val time_results_widget4 =
Capi.make_managed_widget ("timeResults4",Capi.Label,frame, [])
val space_results_widget1 =
Capi.make_managed_widget ("spaceResults1",Capi.Label,frame, [])
val space_results_widget2 =
Capi.make_managed_widget ("spaceResults2",Capi.Label,frame, [])
val function_name_widget =
Capi.make_managed_widget ("functionName",Capi.Label,frame, [])
val function_time_widget =
Capi.make_managed_widget ("functionTime",Capi.Label,frame, [])
val function_space_widget =
Capi.make_managed_widget ("functionSpace",Capi.Label,frame, [])
val function_button_pane =
Capi.make_managed_widget ("functionButtonPane", Capi.RowColumn,
frame,[])
val slider_widget =
Capi.make_managed_widget ("sliderPane",Capi.RowColumn,frame, [])
val popups = ref [] : (unit ref * Capi.Widget) list ref
local
fun kill_popup (_,shell) = Capi.destroy shell
in
fun add_popup (id,shell) = popups := (id,shell)::(!popups)
fun remove_popup id =
let
fun popup_removed (acc,[]) = acc
| popup_removed (acc,(popup as (id',_))::xs) =
if id = id' then popup_removed (acc,xs)
else popup_removed (popup::acc,xs)
in
popups := popup_removed ([],!popups)
end
fun kill_popups () = (ignore(map kill_popup (!popups));
popups := [])
end
val default_function_info =
{fun_id = ref false,
function_text = "No functions profiled",
time_text = "",
space_text = "",
edit_fn = fn () => (),
editable = false,
peelable = false}
val current_function_info = ref default_function_info
fun peel_off _ =
case !current_function_info of
{peelable = false,...} => ()
| {function_text, time_text, space_text,
edit_fn, editable,...} =>
let
val visible = ref false
val shell = Capi.make_popup_shell("functionProfile",shell,[],visible)
val frame = Capi.make_subwindow shell
val function_name_widget =
Capi.make_managed_widget ("functionName",Capi.Label,
frame, [])
val function_time_widget =
Capi.make_managed_widget ("functionTime",Capi.Label,
frame,[])
val function_space_widget =
Capi.make_managed_widget ("functionSpace",Capi.Label,
frame,[])
val button_pane =
Capi.make_managed_widget ("buttonPane",
Capi.RowColumn, frame,[])
fun popup () =
(Capi.reveal frame;
visible := true;
Capi.to_front shell)
val popup_id = ref ()
fun quit _ = (remove_popup popup_id;
Capi.destroy shell)
val {update = buttons_update,
set_focus = buttons_set_focus} =
Menus.make_buttons (button_pane,
[Menus.PUSH ("editButton",
fn _ =>edit_fn(),
fn _ => editable),
Menus.PUSH ("closeButton",quit,
fn _ => true)])
fun set_focus () = if editable then buttons_set_focus 0
else buttons_set_focus 1
in
Capi.set_label_string(function_name_widget, function_text);
Capi.set_label_string(function_time_widget, time_text);
Capi.set_label_string(function_space_widget, space_text);
add_popup (popup_id, shell);
Capi.Layout.lay_out
(frame, NONE,
[Capi.Layout.FIXED function_name_widget,
Capi.Layout.FIXED function_time_widget,
Capi.Layout.FIXED function_space_widget,
Capi.Layout.SPACE,
Capi.Layout.FIXED button_pane]);
popup();
set_focus();
buttons_update()
end
fun get_name_and_location code_name =
let
fun aux1(#"["::l,acc) = (acc,l)
| aux1(c::l,acc) = aux1(l,c::acc)
| aux1([],acc) = (acc,[])
fun aux2([#"]"],acc) = (acc,nil)
| aux2(#"]"::l,acc) = (acc,l)
| aux2(c::l,acc) = aux2(l,c::acc)
| aux2([],acc) = (acc,nil)
val (namechars,rest) = aux1(explode code_name,[])
val (locchars,rest) = aux2 (rest,[])
in
(implode(rev namechars),implode(rev locchars),implode rest)
end
fun name_and_edit code_name =
let
val (fun_name,loc_string,_) = get_name_and_location code_name
val location =
ShellUtils.Info.Location.from_string loc_string
handle ShellUtils.Info.Location.InvalidLocation
=> ShellUtils.Info.Location.UNKNOWN
fun edit () =
(ignore(ShellUtils.edit_location
(location, Preferences.new_preferences user_preferences));
())
handle
ShellUtils.EditFailed s =>
Capi.send_message (shell, "Edit failed: " ^ s)
val editable = ShellUtils.editable location
in
(fun_name,edit,editable)
end
fun edit _ = #edit_fn (!current_function_info) ()
fun editable _ = #editable (!current_function_info)
fun peelable _ = #peelable (!current_function_info)
val {update = fn_buttons_update,
set_focus = fn_buttons_set_focus} =
Menus.make_buttons
(function_button_pane,
[Menus.PUSH("editButton",edit,editable),
Menus.PUSH("peelButton",peel_off,peelable)])
fun set_current_function_info fi =
(current_function_info := fi;
(if #editable fi then
fn_buttons_set_focus 0
else ());
fn_buttons_update())
fun reset_function_info () =
set_current_function_info default_function_info
fun show_function_info () =
let
val {function_text, time_text, space_text,...} =
!current_function_info
in
Capi.set_label_string(function_name_widget, function_text);
Capi.set_label_string(function_time_widget, time_text);
Capi.set_label_string(function_space_widget, space_text)
end
fun do_function_info (Profile.Function_Profile
{id, time, space, call_count}, fun_id,
(mutator_ticks, tick,total_space)) =
let val old_fun_id = #fun_id (!current_function_info)
in
if (fun_id = old_fun_id) then ()
else
let
val _ = old_fun_id := false
val _ = fun_id := true
val (fn_name, edit,editable) = name_and_edit id
val function_text = "Function: "^fn_name
val Profile.Function_Time_Profile
{found,top,scans,depth,self,callers} = time
val approx_time = (real top) * tick
val time_text =
"Time: "^(decimal_places 2 approx_time)^" s ("^
percentage (real top,real mutator_ticks)^", "^
(int_string top)^ " ticks)"
val Profile.Function_Space_Profile
{allocated,copied,copies,allocation} = space
val alloc = real_size allocated
val copy = real_size copied
val space_text =
"Space: "^ (bytes alloc) ^" ("^
percentage (alloc,total_space)^"), "^
(bytes copy)^" copied ("^(percentage (copy,alloc))^
" alloc)"
in
set_current_function_info
{fun_id = fun_id,
function_text = function_text,
time_text = time_text,
space_text = space_text,
edit_fn = edit,
editable = editable,
peelable = true};
show_function_info()
end
end
fun show_textual_results (total_time, total_space, total_copied,
total_ticks, profile_ticks, gc_ticks,
mutator_ticks) =
let
val profile_time =
if total_ticks = 0 then 0.0 else
total_time * (real profile_ticks/real total_ticks)
val non_profile_time = total_time - profile_time
val ticks = gc_ticks + mutator_ticks
val tick = if ticks = 0 then 0.0
else non_profile_time / (real ticks)
val mutator_time = (real mutator_ticks) * tick
val gc_time = (real gc_ticks) * tick
val time_results_1 =
"Total time: "^(decimal_places 2 total_time)^ " s ("^
(int_string total_ticks)^" ticks)"
val time_results_2 =
"Profile time: "^(decimal_places 2 profile_time)^" s ("^
(int_string profile_ticks)^" ticks, "^
percentage(profile_time,total_time)^")"
val time_results_3 =
"ML time: "^(decimal_places 2 mutator_time)^" s ("^
(int_string mutator_ticks)^" ticks, "^
percentage(mutator_time, non_profile_time)^")"
val time_results_4 =
"GC time: "^(decimal_places 2 gc_time)^" s ("^
(int_string gc_ticks)^" ticks, "^
percentage(gc_time,non_profile_time)^")"
val space_results_1 = "Total allocation: "^(bytes total_space)
val space_results_2 = "Total copying: "^(bytes total_copied)
in
Capi.set_label_string (time_results_widget1,time_results_1);
Capi.set_label_string (time_results_widget2,time_results_2);
Capi.set_label_string (time_results_widget3,time_results_3);
Capi.set_label_string (time_results_widget4,time_results_4);
Capi.set_label_string (space_results_widget1,space_results_1);
Capi.set_label_string (space_results_widget2,space_results_2);
(mutator_ticks, tick, total_space)
end
val selected_bar = ref 0
val bar_list_ref : BarChart.bar list ref = ref []
fun get_bar_list () = (!bar_list_ref,!selected_bar)
fun make_bar_list
(info,(cost as Cost {time, space, profile,id})::costs, n, bars) =
let
val height = (mix (factor (!space_weight)) cost) * 100.0
val Profile.Function_Profile {id = key,...} = profile
fun click_action n =
(selected_bar := n;
do_function_info (profile,id,info))
val bar =
BarChart.Bar {height=height,key=key,click_action=click_action}
in
(if (!id) then click_action n else ();
make_bar_list (info, costs, n+1, bar::bars))
end
| make_bar_list (info, [], _, bars) = rev bars
val {widget = bc_widget,
initialize = bc_initialize,
update = bc_update,
popup = bc_popup}
= BarChart.make (!chart_spec_ref,set_chart_spec,
get_bar_list,frame)
val results_ref = ref results
val functions_ref = ref [] : function_cost list ref
fun show_results results =
let
val Result {total_time, total_space, total_copied, total_ticks,
profile_ticks, gc_ticks, mutator_ticks,
functions} = results
in
reset_function_info();
functions_ref := functions;
show_textual_results(total_time, total_space, total_copied,
total_ticks, profile_ticks, gc_ticks,
mutator_ticks)
end
val info_ref = ref (show_results results)
fun any_space () = (#3 (!info_ref)) > 0.5
fun any_time () = (#1 (!info_ref)) > 0
fun show_functions () =
(bar_list_ref := make_bar_list (!info_ref, !functions_ref, 0, []);
(case (!functions_ref) of
[] => show_function_info()
| _ => ());
bc_update ())
fun sort_functions () =
let
val functions = !functions_ref
val factors = factor (!space_weight)
val sorted_functions = sort factors functions
in
functions_ref := sorted_functions;
show_functions()
end
fun update results =
(info_ref := (show_results results);
results_ref := results;
show_functions ())
val tool_id = ref ()
val tool = ProfileTool {update = update,
id = tool_id}
fun layout_action _ = bc_popup()
fun storeSizePos () =
(sizeRef := SOME (Capi.widget_size shell);
posRef := SOME (Capi.widget_pos shell))
fun destroy_action _ =
(kill_popups ();
Menus.quit ();
storeSizePos ();
active_tool_ref := NONE)
fun quit a =
(destroy_action a;
Capi.destroy shell)
val space_factor = ref 0
fun set_space_factor n =
if (any_space()) andalso (n <> !space_factor) then
(space_factor := n;
space_weight := (real n / 100.0);
show_functions ())
else ()
fun duplicate _ =
(ignore(make_profile_tool (parent, !results_ref,user_preferences,mk_tooldata,get_context));
())
val menuspec =
[ToolData.file_menu [("close",quit, fn _ => true)],
ToolData.edit_menu (frame,
{cut = NONE,
paste = NONE,
copy = NONE,
delete = NONE,
edit_possible = fn _ => false,
selection_made = fn _ => false,
delete_all = NONE,
edit_source = [Menus.PUSH ("editSource", edit, editable)]}),
ToolData.tools_menu (mk_tooldata, get_context),
ToolData.usage_menu ([("duplicate", duplicate, fn _ => true),
("layout", layout_action, fn () => true),
("sort", sort_functions, fn _ => true),
("peel", peel_off, peelable)], []),
ToolData.debug_menu []]
val {update = slider_update,
set_focus = slider_set_focus} =
Menus.make_buttons (slider_widget,
[Menus.LABEL "timeButton",
Menus.SLIDER ("slider", 0, 100,
set_space_factor),
Menus.LABEL "spaceButton"])
in
Menus.make_submenus(menuBar, menuspec);
Capi.Layout.lay_out
(frame, !sizeRef,
[Capi.Layout.MENUBAR menuBar,
Capi.Layout.SPACE,
Capi.Layout.FIXED time_results_widget1,
Capi.Layout.FIXED time_results_widget2,
Capi.Layout.FIXED time_results_widget3,
Capi.Layout.FIXED time_results_widget4,
Capi.Layout.SPACE,
Capi.Layout.FIXED space_results_widget1,
Capi.Layout.FIXED space_results_widget2,
Capi.Layout.SPACE,
Capi.Layout.FIXED slider_widget,
Capi.Layout.FLEX bc_widget,
Capi.Layout.SPACE,
Capi.Layout.FIXED function_name_widget,
Capi.Layout.FIXED function_time_widget,
Capi.Layout.FIXED function_space_widget,
Capi.Layout.FIXED function_button_pane,
Capi.Layout.SPACE
]);
Capi.Callback.add (shell, Capi.Callback.Destroy, destroy_action);
Capi.set_close_callback(frame, quit);
Capi.set_min_window_size(shell, 420, 600);
Capi.initialize_toplevel shell;
bc_initialize ();
show_functions();
fn_buttons_update();
tool
end
in
fun create (shell, user_preferences, mk_tooldata, get_context) profile =
let val results = results profile
in
case !active_tool_ref of
NONE =>
let
val t = make_profile_tool (shell, results, user_preferences,
mk_tooldata, get_context)
in
active_tool_ref := SOME t
end
| SOME t => update_profile_tool (t,results)
end
end
end
;
