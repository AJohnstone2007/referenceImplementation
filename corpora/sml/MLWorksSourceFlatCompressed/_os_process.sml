require "os_process";
require "exit";
functor OSProcess (structure Exit : EXIT) : OS_PROCESS =
struct
val env = MLWorks.Internal.Runtime.environment
type status = Exit.status
val success = Exit.success
val failure = Exit.failure
val isSuccess = Exit.isSuccess
val system : string -> status = env "system os system"
val terminate = Exit.terminate
val atExit = Exit.atExit
val exit = Exit.exit
val getEnv : string -> string option = env "system os getenv"
end
;
