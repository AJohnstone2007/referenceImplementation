require "^.basis.os_path";
require "^.basis.os_file_sys";
require "^.basis.__word";
require "^.basis.__word32";
require "^.basis.__string";
require "^.basis.__word";
require "__time";
require "win32";
functor OSFileSys
(structure Win32: WIN32
structure Path : OS_PATH): OS_FILE_SYS =
struct
val env = MLWorks.Internal.Runtime.environment
type dir_handle = Word32.word
datatype dirstream = DIRSTREAM of string * dir_handle ref * string ref
val openDir : string -> dirstream = env "OS.FileSys.openDir"
val readDir : dirstream -> string option = env "OS.FileSys.readDir"
val rewindDir : dirstream -> unit = env "OS.FileSys.rewindDir"
val closeDir : dirstream -> unit = env "OS.FileSys.closeDir"
val chDir : string -> unit = env "OS.FileSys.chDir"
local
val getDir' : unit -> string = env "OS.FileSys.getDir"
in
fun getDir () = Path.mkCanonical (getDir' ())
end
val mkDir : string -> unit = env "OS.FileSys.mkDir"
val rmDir : string -> unit = env "OS.FileSys.rmDir"
val isDir : string -> bool = env "OS.FileSys.isDir"
fun isLink _ = false
fun readLink _ = raise Win32.SysErr ("Win32 does not support links", NONE)
local
val fullPath' : string -> string = env "OS.FileSys.fullPath"
in
fun fullPath s = Path.mkCanonical (fullPath' s)
end
fun realPath p =
if Path.isAbsolute p
then fullPath p
else Path.mkRelative {path=fullPath p, relativeTo=fullPath (getDir ())}
val modTime : string -> Time.time = env "OS.FileSys.modTime"
val fileSize : string -> int = env "OS.FileSys.fileSize"
val setTime_ : string * Time.time -> unit = env "OS.FileSys.setTime_"
fun setTime (fileName, NONE) = setTime_ (fileName, Time.now ())
| setTime (fileName, SOME time) = setTime_ (fileName, time)
val remove : string -> unit = env "OS.FileSys.remove"
val rename : { old: string, new: string} -> unit = env "OS.FileSys.rename"
datatype access_mode = A_READ | A_WRITE | A_EXEC
val access : (string * access_mode list) -> bool = env "OS.FileSys.access"
val tmpName : unit -> string = env "OS.FileSys.tmpName"
datatype file_id = FILE_ID of string
fun fileId s = FILE_ID s
fun hash' (s, i, v) =
if i = 0
then v + ord (String.sub (s, i))
else hash' (s, i-1, v + ord (String.sub (s, i)))
fun hash (FILE_ID s) = Word.fromInt (hash' (s, size s - 1, size s))
fun compare (FILE_ID a, FILE_ID b) = String.compare (a, b)
end
;
