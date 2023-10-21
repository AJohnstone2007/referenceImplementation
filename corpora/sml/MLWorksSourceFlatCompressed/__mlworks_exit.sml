require "../utils/mlworks_exit";
require "__windows";
require "__sys_word";
structure MLWorksExit :> MLWORKS_EXIT =
struct
type status = Windows.Status.status
val success = SysWord.fromInt 0
val failure = SysWord.fromInt 1
val uncaughtIOException = SysWord.fromInt 2
val badUsage = SysWord.fromInt 3
val stop = SysWord.fromInt 4
val save = SysWord.fromInt 5
val badInput = SysWord.fromInt 6
val exit = Windows.exit
end
;
