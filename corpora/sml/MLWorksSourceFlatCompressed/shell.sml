require "../main/info";
signature SHELL =
sig
structure Info: INFO
type ShellData
type ShellState
type Context
exception Exit of int
val initial_state: ShellState
datatype Result =
OK of ShellState
| ERROR of Info.error * Info.error list
| INTERRUPT
| DEBUGGER_TRAPPED
| TRIVIAL
val shell :
ShellData * string * (unit -> unit)
-> (Info.options -> string * ShellState -> string list * string * Result)
* (string * ShellState -> string)
end;
