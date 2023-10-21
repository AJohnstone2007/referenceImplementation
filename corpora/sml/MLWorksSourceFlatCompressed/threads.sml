signature THREADS =
sig
type 'a thread
val fork : ('a -> 'b) -> 'a -> 'b thread
val yield : unit -> unit
datatype 'a result =
Running
| Waiting
| Sleeping
| Result of 'a
| Exception of exn
| Died
| Killed
| Expired
val result : 'a thread -> 'a result
structure Internal :
sig
eqtype thread_id
val id : unit -> thread_id
val get_id : 'a thread -> thread_id
val children : thread_id -> thread_id list
val parent : thread_id -> thread_id
val all : unit -> thread_id list
val kill : thread_id -> unit
val raise_in : thread_id * exn -> unit
val yield_to : thread_id -> unit
val state : thread_id -> unit result
val get_num : thread_id -> int
val set_handler : (unit -> unit) -> unit
structure Preemption :
sig
val start : unit -> unit
val stop : unit -> unit
val on : unit -> bool
val get_interval : unit -> int
val set_interval : int -> unit
end
end
end;
fun start () = (MLWorks.Threads.Internal.Preemption.set_interval 20;
MLWorks.Threads.Internal.Preemption.start())
;
