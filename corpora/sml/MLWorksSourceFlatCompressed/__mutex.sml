require "mutex.sml";
structure Mutex : MUTEX =
struct
structure T = MLWorks.Threads;
structure I = T.Internal;
structure P = I.Preemption;
exception Mutex of string;
fun state_eq(T.Died,T.Died) = true
| state_eq(T.Running,T.Running) = true
| state_eq(T.Sleeping,T.Sleeping) = true
| state_eq(T.Exception _,T.Exception _) = true
| state_eq(T.Expired,T.Expired) = true
| state_eq(T.Killed,T.Killed) = true
| state_eq(T.Result _,T.Result _) = true
| state_eq(T.Waiting,T.Waiting) = true
| state_eq _ = false
abstype mutex = COUNTING of {counter: int, waiting: I.thread_id list} ref
| BINARY of {claimed: bool, waiting: I.thread_id list} ref
with
fun newCountingMutex size =
COUNTING (ref {counter=
(if size<0
then raise Mutex "initial counter must be greater than 0"
else size),
waiting=[]})
fun newBinaryMutex flag = BINARY (ref {claimed=flag, waiting=[]})
fun wait mutices =
let
val self = I.id()
fun allFree [] = true
| allFree (COUNTING(m as ref{counter,waiting})::rest) =
if counter=0 then (m:={counter=counter,
waiting=waiting@[self]};
false)
else allFree rest
| allFree (BINARY(m as ref{claimed,waiting})::rest) =
if claimed then (m:={claimed=claimed,
waiting=waiting@[self]};
false)
else allFree rest
fun claimAll [] = ()
| claimAll (COUNTING(m as ref{counter,waiting})::rest) =
(m:={counter=counter-1,waiting=waiting};
claimAll rest)
| claimAll (BINARY(m as ref{claimed,waiting})::rest) =
(m:={claimed=true,waiting=waiting};
claimAll rest)
in
P.enter_critical_section();
if allFree mutices
then (
claimAll mutices;
P.exit_critical_section()
)
else (
T.sleep(self);
wait mutices
)
end
fun test mutices =
let
fun allFree [] = true
| allFree (COUNTING(ref{counter,...})::rest) =
counter>0 andalso allFree rest
| allFree (BINARY(ref{claimed,...})::rest) =
not claimed andalso allFree rest
in
(P.enter_critical_section();
allFree mutices before (P.exit_critical_section()))
end
fun testAndClaim mutices =
let
fun allFree [] = true
| allFree (COUNTING(ref{counter,...})::rest) =
counter>0 andalso allFree rest
| allFree (BINARY(ref{claimed,...})::rest) =
not claimed andalso allFree rest
fun claimAll [] = ()
| claimAll (COUNTING(m as ref{counter,waiting})::rest) =
(m:={counter=counter-1,waiting=waiting};
claimAll rest)
| claimAll (BINARY(m as ref{claimed,waiting})::rest) =
(m:={claimed=true,waiting=waiting};
claimAll rest)
in
(P.enter_critical_section();
(if allFree mutices then (claimAll mutices; true)
else false) before (P.exit_critical_section())
)
end
fun signal mutices =
let
fun wakeAll [] = ()
| wakeAll (h::t) =
(if state_eq(I.state h,T.Sleeping)
then T.wake h
else ();
wakeAll t)
fun releaseAll [] = ()
| releaseAll (COUNTING(m as ref{counter,waiting})::rest) =
(wakeAll waiting;
m:={counter=counter+1,waiting=[]};
releaseAll rest)
| releaseAll (BINARY(m as ref{claimed,waiting})::rest) =
(wakeAll waiting;
m:={claimed=false,waiting=[]};
releaseAll rest)
in
P.enter_critical_section();
releaseAll mutices;
P.exit_critical_section()
end
fun query (COUNTING(ref{counter,waiting})) = waiting
| query (BINARY(ref{claimed,waiting})) = waiting
fun allSleeping mutices =
let
fun allAsleep [] = true
| allAsleep (h::t) = state_eq(I.state h,T.Sleeping)
andalso allAsleep t
in
P.enter_critical_section();
allAsleep mutices before
(P.exit_critical_section())
end
fun cleanUp () =
let
fun kill [] = P.exit_critical_section()
| kill (h::t) =
(if I.get_num h>1 then I.kill h else (); kill t)
in
P.enter_critical_section();
kill(I.all());
P.exit_critical_section()
end
end
fun critical (mutices,f) x =
let val _ = wait mutices
val result = f x
val _ = signal mutices
in
result
end handle e => (signal mutices; raise e)
fun await(mutices,condition) =
(wait mutices;
if (condition() handle e => (signal mutices; raise e)) then ()
else await((signal mutices;mutices),condition)
)
end;
