require "../debugger/value_printer";
signature ML_DEBUGGER =
sig
structure ValuePrinter : VALUE_PRINTER
datatype ('a, 'b)union = INL of 'a | INR of 'b
type preferences
val size_of_data_cache : int ref
type debugger_window
datatype TypeOfDebugger =
WINDOWING of debugger_window * (string -> unit) * bool
| TERMINAL
datatype parameter =
INTERRUPT
| FATAL_SIGNAL of int
| EXCEPTION of exn
| BREAK of string
| STACK_OVERFLOW
datatype Continuation =
POSSIBLE of string * Continuation_action
| NOT_POSSIBLE
and Continuation_action =
NORMAL_RETURN
| DO_RAISE of exn
| FUN of (unit -> unit)
val get_start_frame : unit -> MLWorks.Internal.Value.Frame.frame
val with_start_frame :
(MLWorks.Internal.Value.Frame.frame -> 'a) -> 'a
val with_debugger_type :
TypeOfDebugger ->
(MLWorks.Internal.Value.Frame.frame -> 'a) ->
'a
val get_debugger_type : unit -> TypeOfDebugger
val ml_debugger :
(TypeOfDebugger * ValuePrinter.Options.options * preferences)
-> (MLWorks.Internal.Value.Frame.frame * parameter
* Continuation * Continuation)
-> unit
end
;
