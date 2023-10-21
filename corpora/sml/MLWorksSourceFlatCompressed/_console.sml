require "../basis/__int";
require "../basis/__text_io";
require "../basis/__text_prim_io";
require "^.utils.__terminal";
require "../utils/lists";
require "../interpreter/shell_utils";
require "capi";
require "menus";
require "gui_utils";
require "console";
functor Console (
structure Capi: CAPI
structure Lists: LISTS
structure ShellUtils : SHELL_UTILS
structure GuiUtils : GUI_UTILS
structure Menus : MENUS
sharing type GuiUtils.user_tool_options = ShellUtils.UserOptions
sharing type Menus.Widget = GuiUtils.Widget = Capi.Widget
sharing type Menus.ButtonSpec = GuiUtils.ButtonSpec
sharing type ShellUtils.user_preferences = GuiUtils.user_preferences
): CONSOLE =
struct
type Widget = Capi.Widget
type user_preferences = GuiUtils.user_preferences
val do_debug = false
fun debug s = if do_debug then Terminal.output(s ^ "\n") else ()
fun fdebug f = if do_debug then Terminal.output(f() ^ "\n") else ()
fun ddebug s = Terminal.output(s ^ "\n")
fun create (parent, title, user_preferences) =
let
val (textscroll, text) =
Capi.make_scrolled_text ("textIO", parent, [])
val write_pos = ref 0
fun clear_console _ = (Capi.Text.set_string (text, "");
write_pos := 0)
fun insert_text str =
let
val str = Capi.Text.check_insertion (text, str, !write_pos, [write_pos])
in
Capi.Text.insert(text, !write_pos, str);
write_pos := Capi.Text.text_size str + !write_pos;
Capi.Text.set_insertion_position (text, !write_pos)
end
val outstream = GuiUtils.make_outstream insert_text
val (input_string, input_flag) = (ref "", ref false)
fun input_fun () =
(input_flag := true;
Capi.event_loop (input_flag);
fdebug(fn _ => "Input line is:" ^ (!input_string)^":");
!input_string)
local
val inbuff as (posref, strref) = (ref 0, ref "")
fun refill_buff () =
let
val new_string = input_fun ()
in
posref := 0;
strref := new_string
end
val eof_flag = ref false
val close_in = fn () => eof_flag:=true
val thisWindow = {output={descriptor=NONE,
put= fn {buf,i,sz} =>
let val els = case sz of
NONE=>size buf-i
| (SOME s)=> s
in insert_text (substring (buf,i,els));
els
end,
get_pos=NONE,
set_pos=NONE,
can_output=NONE,
close = fn()=>()},
error ={descriptor=NONE,
put= fn {buf,i,sz} =>
let val els = case sz of
NONE=>size buf-i
| (SOME s)=> s
in insert_text (substring (buf,i,els));
els
end,
get_pos=NONE,
set_pos=NONE,
can_output=NONE,
close=fn()=>()},
input ={descriptor=NONE,
get=fn _ =>input_fun(),
get_pos=SOME(fn()=> !posref),
set_pos=SOME(fn i=>posref:=i),
can_input=SOME(fn()=>
(!posref<size (!strref))),
close=close_in},
access = fn f => f ()}
in
fun inThisWindow () =
MLWorks.Internal.StandardIO.redirectIO thisWindow
fun get_input n =
let
val string = !strref
val pointer = !posref
val len = size string
in
if !eof_flag then
""
else if pointer + n > len then
(refill_buff ();
substring (string,pointer,len-pointer) ^
get_input (n - len + pointer))
else
let val result = substring (string,pointer,n)
in
posref := (!posref + n);
result
end
end
fun clear_input () =
(debug "Clearing input";
posref := 0;
strref := "";
eof_flag := false)
fun do_lookahead () =
(if !eof_flag then
""
else if !posref >= size (!strref) then
(refill_buff ();
do_lookahead ())
else
substring (!strref, !posref, 1))
val close_in = close_in
end;
fun mkInstream(input, lookahead, close_in) =
let
fun can_input() = do_lookahead() <> ""
val prim_reader =
TextPrimIO.RD{name = "console reader",
chunkSize = 1,
readVec = SOME get_input,
readArr = NONE,
readVecNB = NONE,
readArrNB = NONE,
block = NONE,
canInput = SOME can_input,
avail = fn () => SOME(size(do_lookahead())),
getPos = NONE,
setPos = NONE,
endPos = NONE,
verifyPos = NONE,
close = close_in,
ioDesc = NONE}
in
TextIO.mkInstream(TextIO.StreamIO.mkInstream(TextPrimIO.augmentReader prim_reader, ""))
end
val instream = mkInstream(get_input, do_lookahead, close_in)
fun replace_current_input line =
let
val last_pos = Capi.Text.get_last_position text
in
Capi.Text.replace (text, !write_pos, last_pos, line);
Capi.Text.set_insertion_position (text, !write_pos + size line)
end
fun delete_current_line () =
replace_current_input ""
val {update_history, prev_history, next_history, history_menu, ...} =
GuiUtils.make_history
(user_preferences, fn line => replace_current_input line)
fun start_of_line () =
let
val ppos = !write_pos
val pos = Capi.Text.get_insertion_position text
val new_pos =
if pos < ppos
then Capi.Text.current_line (text,pos)
else ppos
in
Capi.Text.set_insertion_position (text,new_pos)
end
fun end_of_line () =
let
val ppos = !write_pos
val pos = Capi.Text.get_insertion_position text
val new_pos =
if pos < ppos
then Capi.Text.end_line (text,pos)
else Capi.Text.get_last_position text
in
Capi.Text.set_insertion_position (text,new_pos)
end
fun eof_or_delete () =
let
val pos = Capi.Text.get_insertion_position text
val last_pos = Capi.Text.get_last_position text
in
if pos = last_pos andalso pos = !write_pos then
(debug "eof";
close_in ();
input_flag := false)
else
(debug "delete";
Capi.Text.replace (text, pos, pos + 1, ""))
end
fun do_return () =
let
val pos = Capi.Text.get_insertion_position text
val lines =
if pos < !write_pos then
let
val line = Capi.Text.get_line (text, pos) ^ "\n"
val last_pos = Capi.Text.get_last_position text
in
Capi.Text.insert(text, last_pos, line);
write_pos := last_pos + size line;
Capi.Text.set_insertion_position (text, last_pos + size line);
[line]
end
else
let
val str = Capi.Text.get_string text
val length = size str
fun get_lines ([], current, acc, _) =
map (implode o rev) (current :: acc)
| get_lines (#"\n"::rest, current, acc, column) =
get_lines (rest, [], (#"\n"::current)::acc, 1)
| get_lines (c::rest, current, acc, column) =
get_lines (rest, c::current, acc, column+1)
val line = substring (str, !write_pos,
length - !write_pos)
val lines = get_lines (explode line,[],[],0)
in
case lines of
last :: rest =>
(Capi.Text.insert(text, length, "\n");
write_pos := length + 1;
Capi.Text.set_insertion_position
(text, length+1); last ^ "\n" :: rest)
| _ => lines
end
in
input_flag := false;
input_string := concat (rev lines);
update_history [!input_string]
end
val escape_pressed = ref false
fun do_escape () = escape_pressed := true
val meta_bindings =
[("p", prev_history),
("n", next_history),
("w", fn _ => Capi.Text.copy_selection text)]
val normal_bindings =
[("\^A", start_of_line),
("\^D", eof_or_delete),
("\^E", end_of_line),
("\^W", fn _ => Capi.Text.cut_selection text),
("\^Y", fn _ => Capi.Text.paste_selection text),
("\^U", delete_current_line),
("\013",do_return),
("\027",do_escape)]
fun despatch_key bindings key =
let
fun loop [] = false
| loop ((key',action)::rest) =
if key = key' then (ignore(action ()); true)
else loop rest
in
loop bindings
end
val despatch_meta = despatch_key meta_bindings
val despatch_normal = despatch_key normal_bindings
fun text_handler (key, modifiers) =
if modifiers = [Capi.Event.meta_modifier] then
despatch_meta key
else
despatch_normal key
fun modifyVerify (start_pos, end_pos, str, doit) =
if !escape_pressed andalso size str = 1
then
(escape_pressed := false;
doit false;
ignore(despatch_meta str);
())
else
(fdebug (fn _ =>
"Verify: start_pos is " ^ Int.toString start_pos ^
", end_pos is " ^ Int.toString end_pos ^
", write_pos is " ^ Int.toString (!write_pos) ^
", string is '" ^ str ^ "'");
if end_pos < !write_pos then
write_pos := (!write_pos) - end_pos + start_pos + size str
else if start_pos < !write_pos then
write_pos := start_pos + size str
else ();
if end_pos < !write_pos
then write_pos := (!write_pos) - end_pos + start_pos + size str
else if start_pos < !write_pos
then write_pos := start_pos + size str
else ();
doit true)
in
Capi.Text.add_handler (text, text_handler);
Capi.Text.add_modify_verify (text,modifyVerify);
{instream = instream,
outstream = outstream,
console_widget = textscroll,
console_text = text,
clear_input = clear_input,
clear_console = clear_console,
set_window=inThisWindow}
end
end;
