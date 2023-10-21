require "$.basis.__int";
require "$.basis.__string";
require "../main/preferences";
require "../basics/location";
require "../editor/editor";
require "../editor/custom";
require "../utils/lists";
require "../utils/crash";
require "win32";
functor Editor
(structure Preferences: PREFERENCES
structure Location : LOCATION
structure Win32 : WIN32
structure CustomEditor : CUSTOM_EDITOR
structure Lists : LISTS
structure Crash : CRASH
) : EDITOR =
struct
structure Location = Location
type preferences = Preferences.preferences
local
open Preferences
val EDITOR_OPTIONS {editor, oneWayEditorName,
twoWayEditorName, externalEditorCommand} =
default_editor_options
in
val _ =
( editor := "External";
oneWayEditorName := "Wordpad";
twoWayEditorName := "PFE32";
externalEditorCommand :=
"\"C:\\Program Files\\Accessories\\Wordpad.exe\" \"%f\""
)
end
fun null_fun () = ()
fun show_int (i) =
if i < 0 then "-" ^ Int.toString(~i) else Int.toString(i)
val member = Lists.member
fun line_from_location(Location.UNKNOWN) = 0
| line_from_location(Location.FILE _) = 0
| line_from_location(Location.LINE(_, i)) = i
| line_from_location(Location.POSITION(_, i, _)) = i
| line_from_location(Location.EXTENT{s_line=i, ...}) = i
fun get_position_info (Location.EXTENT{s_line, s_col, e_line, e_col, ...}) =
(s_line,s_col, e_line, e_col)
| get_position_info (loc) = (line_from_location loc,0,~1,~1)
fun expand_args (fnm, st_l, st_c, e_l, e_c) =
let fun trans (#"%" :: #"%" :: l, r) =
trans(l, "%" :: r)
| trans (#"%" :: #"f" :: l, r) =
trans(l, fnm :: r)
| trans (#"%" :: #"l" :: l, r) =
trans(l, show_int(st_l) :: r)
| trans (#"%" :: #"s" :: #"l" :: l, r) =
trans(l, show_int(st_l) :: r)
| trans (#"%" :: #"c" :: l, r) =
trans(l, show_int(st_c) :: r)
| trans (#"%" :: #"s" :: #"c" :: l, r) =
trans(l, show_int(st_c) :: r)
| trans (#"%" :: #"e" :: #"l" :: l, r) =
trans(l, show_int(e_l) :: r)
| trans (#"%" :: #"e" :: #"c" :: l, r) =
trans(l, show_int(e_c) :: r)
| trans (c :: l, r) =
trans (l, (String.str c)::r)
| trans (_, r) =
concat (rev r)
fun doit str = trans(explode str,[])
in
doit
end
fun transEntry loc_details (server,commands) =
let
val translate = expand_args loc_details
in
(server, map translate commands)
end
type dde_data = word;
local
val env = MLWorks.Internal.Runtime.environment
in
val start_dde_dialog : (string * string) -> dde_data =
env "dde start dialog";
val send_dde_execute_string : (dde_data * string * int * int) -> unit =
env "dde send execute string";
val stop_dde_dialog : dde_data -> unit =
env "dde stop dialog";
end
fun dde_dialog ("", "", _) =
( SOME ("Unspecified service & topic for DDE server"), null_fun )
| dde_dialog ("", _, _) =
( SOME ("Unspecified service for DDE server"), null_fun )
| dde_dialog (_, "", _) =
( SOME ("Unspecified topic for DDE server"), null_fun )
| dde_dialog (service, topic, cmds) =
let val busy_retries = 20
val delay = 200
val dde_info = start_dde_dialog (service,topic)
handle MLWorks.Internal.Error.SysErr(msg, err) =>
raise MLWorks.Internal.Error.SysErr("Unable to contact " ^ service ^ " DDE server", err)
fun exec_dde s =
send_dde_execute_string (dde_info,s,busy_retries,delay)
fun doit (s :: lst) = (exec_dde(s) ; doit lst)
| doit [] = stop_dde_dialog dde_info
in
doit cmds;
(NONE, null_fun)
end
handle
MLWorks.Internal.Error.SysErr(msg, _) => (SOME msg, null_fun)
val standard_pfe_commands =
let val file_open = "[FileOpen(\"%f\")]"
val file_visit = "[FileVisit(\"%f\")]"
val goto_line = "[EditGotoLine(%sl,0)]"
val fwd_char = "[CaretRight(%sc,0)]"
val highlight = "[EditGotoLine(%el,1)][CaretRight(%ec,1)]"
val display_cmds = [goto_line, fwd_char, highlight]
val full_dialog = file_open :: display_cmds
in
CustomEditor.addConnectDialog("PFE32", "DDE", "PFE32" :: "Editor" :: full_dialog);
(file_open, file_visit, display_cmds)
end
fun pfe_server (fname, st_l, st_c, e_l, e_c, edit_file) =
let val service = "PFE32"
val topic = "Editor"
val (openf,viewf,display_cmds) = standard_pfe_commands
val dialog = if edit_file then openf :: display_cmds
else viewf :: display_cmds
val translate = expand_args (fname,st_l,st_c,e_l,e_c)
in
dde_dialog (service, topic, map translate dialog)
end
val _ = let
val wordpad_cmd = "\"C:\\Program Files\\Accessories\\Wordpad.exe\" \"%f\""
val textpad_cmd = "\"C:\\TextPad\\DDEOPN32.EXE\" TextPad %f(%sl,%sc)"
in
CustomEditor.addCommand("TextPad", textpad_cmd);
CustomEditor.addCommand("Wordpad", wordpad_cmd)
end;
fun do_command (s) =
(
if not(Win32.create_process(s,Win32.HIGH)) then
(SOME "Can't execute editor process\n", null_fun)
else
(NONE, null_fun)
)
fun external_server extCmd (string, s_l, s_c, e_l, e_c) =
let val translate = expand_args (string, s_l, s_c, e_l, e_c)
in
do_command ( translate extCmd )
end
fun one_way_server customName loc_details =
let
val translate = expand_args loc_details
val cmd = translate (CustomEditor.getCommandEntry customName)
val cmd_result =
if (cmd <> "") then do_command cmd
else (SOME "invalid choice for editor", null_fun)
in
cmd_result
end
fun two_way_server customName loc_details =
let
val (dialog_type, commands) =
transEntry loc_details (CustomEditor.getDialogEntry customName)
val (service, topic, commands) =
case commands of
s::t::cmds => (s,t,cmds)
| _ => ("","",commands)
in
case dialog_type of
"DDE" => dde_dialog (service, topic, commands)
| _ => (SOME ("Unknown custom dialog type : " ^ dialog_type), null_fun)
end
fun edit
(Preferences.PREFERENCES
{editor_options=Preferences.EDITOR_OPTIONS
{editor, externalEditorCommand, oneWayEditorName, twoWayEditorName, ...},
...})
(string, i) =
case !editor of
"External" =>
external_server (!externalEditorCommand) (string, i, 0, ~1, ~1)
| "OneWay" =>
one_way_server (!oneWayEditorName) (string, i, 0, ~1, ~1)
| "TwoWay" =>
two_way_server (!twoWayEditorName) (string, i, 0, ~1, ~1)
| opt => Crash.impossible ("Unknown option `" ^ opt ^ "'")
fun edit_from_location
(Preferences.PREFERENCES
{editor_options=Preferences.EDITOR_OPTIONS
{editor, externalEditorCommand, oneWayEditorName, twoWayEditorName, ...},
...})
(string, location) =
let val (s_l, s_c, e_l, e_c) = get_position_info location
in
case !editor of
"External" =>
external_server (!externalEditorCommand) (string, s_l, s_c, e_l, e_c)
| "OneWay" =>
one_way_server (!oneWayEditorName) (string, s_l, s_c, e_l, e_c)
| "TwoWay" =>
two_way_server (!twoWayEditorName) (string, s_l, s_c, e_l, e_c)
| opt => Crash.impossible ("Unknown option `" ^ opt ^ "'")
end
fun show_location
(Preferences.PREFERENCES
{editor_options=Preferences.EDITOR_OPTIONS
{editor, externalEditorCommand, oneWayEditorName, twoWayEditorName, ...},
...})
(string, location) =
let val (s_l, s_c, e_l, e_c) = get_position_info location
in
case !editor of
"External" =>
(SOME("show location requires emacs"), null_fun)
| "OneWay" =>
one_way_server (!oneWayEditorName) (string, s_l, s_c, e_l, e_c)
| "TwoWay" =>
two_way_server (!twoWayEditorName) (string, s_l, s_c, e_l, e_c)
| opt => Crash.impossible ("Unknown option `" ^ opt ^ "'")
end
end
;
