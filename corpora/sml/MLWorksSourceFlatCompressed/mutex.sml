signature MUTEX =
sig
exception Mutex of string
type mutex
val newCountingMutex: int -> mutex
val newBinaryMutex: bool -> mutex
val test: mutex list -> bool
val testAndClaim : mutex list -> bool
val wait: mutex list -> unit
val signal: mutex list -> unit
val query: mutex -> MLWorks.Threads.Internal.thread_id list
val allSleeping: MLWorks.Threads.Internal.thread_id list -> bool
val cleanUp : unit -> unit
val critical: mutex list * ('a -> 'b) -> 'a -> 'b
val await: mutex list * (unit -> bool) -> unit
end
;
