require "^.basis.__sys_word";
signature OS_EXIT =
sig
type status
type exit_status = SysWord.word
val success : status
val failure : status
val isSuccess : status -> bool
val atExit : (unit -> unit) -> unit
val exit : status -> 'a
val os_exit : exit_status -> 'a
val terminate : status -> 'a
val fromStatus : status -> exit_status
end
;
