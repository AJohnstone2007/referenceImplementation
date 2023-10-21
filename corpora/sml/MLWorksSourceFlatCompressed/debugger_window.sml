signature DEBUGGERWINDOW =
sig
type Widget
type ToolData
type debugger_window
val make_debugger_window :
Widget * string * ToolData ->
debugger_window * (unit -> unit)
end;
