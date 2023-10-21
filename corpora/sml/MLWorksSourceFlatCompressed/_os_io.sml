require "__time";
require "win32";
require "^.basis.__word";
require "^.basis.os_io";
functor OSIO (structure Win32: WIN32) : OS_IO =
struct
val env = MLWorks.Internal.Runtime.environment
type iodesc = Win32.iodesc
fun hash (Win32.IODESC fd) = Word.fromInt fd
fun compare (Win32.IODESC a, Win32.IODESC b) =
if a < b then
LESS
else if a > b then
GREATER
else
EQUAL
datatype iodesc_kind = IODESC_KIND of int
structure Kind = struct
val file = IODESC_KIND 1
val dir = IODESC_KIND 2
val symlink = IODESC_KIND 3
val tty = IODESC_KIND 4
val pipe = IODESC_KIND 5
val socket = IODESC_KIND 6
val device = IODESC_KIND 7
end
val kind : iodesc -> iodesc_kind = env "OS.IO.kind"
datatype event_set = EVENT_SET of int
datatype poll_desc = POLL_DESC of iodesc * event_set
datatype poll_info = POLL_INFO of poll_desc * event_set
val pollDesc : iodesc -> poll_desc option = env "OS.IO.pollDesc"
fun pollToIODesc (POLL_DESC (fd, _)) = fd
exception Poll
val pollIn : poll_desc -> poll_desc = env "OS.IO.pollIn"
val pollOut : poll_desc -> poll_desc = env "OS.IO.pollOut"
val pollPri : poll_desc -> poll_desc = env "OS.IO.pollPri"
val poll : (poll_desc list * Time.time option) -> poll_info list = env "OS.IO.poll"
val isIn : poll_info -> bool = env "OS.IO.isIn"
val isOut : poll_info -> bool = env "OS.IO.isOut"
val isPri : poll_info -> bool = env "OS.IO.isPri"
fun infoToPollDesc (POLL_INFO (pollDesc, _)) = pollDesc
end
;
