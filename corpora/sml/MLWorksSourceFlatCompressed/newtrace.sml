signature TRACE =
sig
type Type
type Context
type UserOptions
val trace_full_internal :
bool ->
((MLWorks.Internal.Value.T * Type) *
(MLWorks.Internal.Value.T * Type)) ->
MLWorks.Internal.Value.T -> unit
val trace_full :
string *
(MLWorks.Internal.Value.T * Type) *
(MLWorks.Internal.Value.T * Type) ->
unit
val trace : string -> unit
val break : {name:string,hits:int,max:int} -> unit
val trace_list : string list -> unit
val break_list : {name:string,hits:int,max:int} list -> unit
val untrace : string -> unit
val unbreak : string -> unit
val breakpoints : unit -> {name:string,hits:int,max:int} list
val traces : unit -> string list
val is_a_break : string -> {name:string,hits:int,max:int} option
val update_break : {name:string,hits:int,max:int} -> unit
val untrace_all : unit -> unit
val unbreak_all : unit -> unit
val set_stepping: bool -> unit
val stepping: unit -> bool
val set_step_count: int -> unit
val step_through: ('a -> 'b) -> 'a -> 'b
val set_trace_all : bool -> unit
val next : MLWorks.Internal.Value.Frame.frame -> unit
val get_function_string : ('a -> 'b) -> string
val get_function_name : ('a -> 'b) -> string
end
;
