require "^.basis.__text_io";
signature CONSOLE =
sig
type Widget
type user_preferences
val create:
Widget * string * user_preferences
-> {instream: TextIO.instream,
outstream: TextIO.outstream,
console_widget: Widget,
console_text: Widget,
clear_input: unit -> unit,
clear_console: unit -> unit,
set_window: unit -> unit}
end;
