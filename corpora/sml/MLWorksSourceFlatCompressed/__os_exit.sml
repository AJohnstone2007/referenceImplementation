require "^.basis.__sys_word";
require "os_exit";
structure OSExit :> OS_EXIT =
struct
open MLWorks.Internal.Exit;
type exit_status = SysWord.word
val os_exit = exit
fun fromStatus s = s
fun isSuccess s = (s = SysWord.fromInt 0)
fun atExit action = (ignore(MLWorks.Internal.Exit.atExit action); ())
end;
