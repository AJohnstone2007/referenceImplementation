signature MLWORKS_EXIT =
sig
type status
val success : status
val failure : status
val uncaughtIOException : status
val badUsage : status
val stop : status
val save : status
val badInput : status
val exit : status -> 'a
end
;
