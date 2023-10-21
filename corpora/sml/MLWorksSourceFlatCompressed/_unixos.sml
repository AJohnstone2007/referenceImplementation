require "^.basis.__word32";
require "^.basis.__sys_word";
require "^.basis.__word8_vector";
require "^.basis.__position";
require "unixos";
require "__time";
functor UnixOS () : UNIXOS =
struct
val env = MLWorks.Internal.Runtime.environment
structure Error = struct
type syserror = MLWorks.Internal.Error.syserror
exception SysErr = MLWorks.Internal.Error.SysErr
val toWord = Word32.fromInt
val fromWord = Word32.toInt
val errorMsg = MLWorks.Internal.Error.errorMsg
val errorName = MLWorks.Internal.Error.errorName
val syserror = MLWorks.Internal.Error.syserror
end
exception WouldBlock
val would_block_ref = env "system os unix exception Would Block"
val _ = would_block_ref := WouldBlock
datatype sockaddr = SOCKADDR_UNIX of string
val environment : unit -> string list = env "system os unix environment"
val rusage :
unit ->
{idrss : int,
inblock : int,
isrss : int,
ixrss : int,
majflt : int,
maxrss : int,
minflt : int,
msgrcv : int,
msgsnd : int,
nivcsw : int,
nsignals : int,
nswap : int,
nvcsw : int,
oublock : int,
stime : Time.time,
utime : Time.time} = env "system os unix rusage"
val getsockname : int -> sockaddr = env "system os unix getsockname"
val getpeername : int -> sockaddr = env "system os unix getpeername"
val accept : int -> int * sockaddr = env "system os unix accept"
val listen : int * int -> unit = env "system os unix listen"
val execve : string * string list * string list -> unit = env "system os unix execve"
val execv : string * string list -> unit = env "system os unix execv"
val execvp : string * string list -> unit = env "system os unix execvp"
val fork_execve : string * string list * string list * int * int * int -> int = env "system os unix fork_execve"
val fork_execv : string * string list * int * int * int -> int = env "system os unix fork_execv"
val fork_execvp : string * string list -> int = env "system os unix fork_execvp"
datatype iodesc = IODESC of int
structure FileSys = struct
type file_desc = MLWorks.Internal.IO.file_desc
fun fdToWord (MLWorks.Internal.IO.FILE_DESC i) = Word32.fromInt i
fun wordToFD w = MLWorks.Internal.IO.FILE_DESC (Word32.toInt w)
fun fdToIOD (MLWorks.Internal.IO.FILE_DESC fd) = IODESC fd
fun iodToFD (IODESC iod) = MLWorks.Internal.IO.FILE_DESC iod
datatype dirstream = DIRSTREAM of MLWorks.Internal.Value.T * bool
val opendir : string -> dirstream = env "POSIX.FileSys.opendir"
val readdir : dirstream -> string option = env "POSIX.FileSys.readdir"
val rewinddir : dirstream -> unit = env "POSIX.FileSys.rewinddir"
val closedir : dirstream -> unit = env "POSIX.FileSys.closedir"
val chdir : string -> unit = env "POSIX.FileSys.chdir"
val getcwd : unit -> string = env "POSIX.FileSys.getcwd"
val stdin = MLWorks.Internal.IO.FILE_DESC 0
val stdout = MLWorks.Internal.IO.FILE_DESC 1
val stderr = MLWorks.Internal.IO.FILE_DESC 2
structure S = struct
type mode = int
val irwxo = 511
end
structure O = struct
type flags = int
val append : flags = env "POSIX.FileSys.O.append"
val excl : flags = env "POSIX.FileSys.O.excl"
val noctty : flags = env "POSIX.FileSys.O.noctty"
val nonblock : flags = env "POSIX.FileSys.O.nonblock"
val sync : flags = env "POSIX.FileSys.O.sync"
val trunc : flags = env "POSIX.FileSys.O.trunc"
end
datatype open_mode = O_RDONLY | O_WRONLY | O_RDWR
val openf : string * open_mode * O.flags -> file_desc =
env "POSIX.FileSys.openf"
val createf : string * open_mode * O.flags * S.mode -> file_desc =
env "POSIX.FileSys.createf"
val creat : string * S.mode -> file_desc = env "POSIX.FileSys.creat"
val umask : S.mode -> S.mode = env "POSIX.FileSys.umask"
val link : {new: string, old: string} -> unit = env "POSIX.FileSys.link"
val mkdir : string * S.mode -> unit = env "POSIX.FileSys.mkdir"
val rmdir : string -> unit = env "POSIX.FileSys.rmdir"
val rename : {new: string, old: string} -> unit = env "POSIX.FileSys.rename"
val unlink : string -> unit = env "POSIX.FileSys.unlink"
val readlink : string -> string = env "POSIX.FileSys.readlink"
datatype dev = DEV of int
fun wordToDev w = DEV (SysWord.toInt w)
fun devToWord (DEV d) = SysWord.fromInt d
datatype ino = I_NODE of int
fun wordToIno w = I_NODE (SysWord.toInt w)
fun inoToWord (I_NODE i) = SysWord.fromInt i
structure ST = struct
type stat =
{dev : dev,
ino : ino,
mode : S.mode,
nlink : int,
uid : int,
gid : int,
rdev : int,
size : Position.int,
atime : Time.time,
mtime : Time.time,
ctime : Time.time,
blksize: int,
blocks : int}
val isDir: stat -> bool = env "POSIX.FileSys.ST.isdir"
val isChr: stat -> bool = env "POSIX.FileSys.ST.ischr"
val isBlk: stat -> bool = env "POSIX.FileSys.ST.isblk"
val isReg: stat -> bool = env "POSIX.FileSys.ST.isreg"
val isFIFO: stat -> bool = env "POSIX.FileSys.ST.isfifo"
val isLink: stat -> bool = env "POSIX.FileSys.ST.islink"
val isSock: stat -> bool = env "POSIX.FileSys.ST.issock"
val mode : stat -> S.mode = #mode
val ino : stat -> ino = #ino
val dev : stat -> dev = #dev
val size : stat -> Position.int = #size
val mtime : stat -> Time.time = #mtime
end
val stat : string -> ST.stat = env "POSIX.FileSys.stat"
val fstat : file_desc -> ST.stat = env "POSIX.FileSys.fstat"
val lstat : string -> ST.stat = env "POSIX.FileSys.lstat"
datatype access_mode = A_READ | A_WRITE | A_EXEC
val access : (string * access_mode list) -> bool = env "POSIX.FileSys.access"
local
val utime_ : (string * Time.time * Time.time) -> unit = env "system os unix utime"
in
fun utime (pathName, NONE) =
let
val now = Time.now ();
in
utime_ (pathName, now, now)
end
| utime (pathName, SOME {actime, modtime}) =
utime_ (pathName, actime, modtime)
end
end
structure IO = struct
val close : FileSys.file_desc -> unit = MLWorks.Internal.IO.close
end
val can_input : FileSys.file_desc -> int = MLWorks.Internal.IO.can_input
val set_block_mode : FileSys.file_desc * bool -> unit =
env "system os unix set block mode"
val open_ : string * int * int -> FileSys.file_desc =
env "system os unix open"
val read : FileSys.file_desc * int -> Word8Vector.vector =
MLWorks.Internal.Value.cast MLWorks.Internal.IO.read
val write : FileSys.file_desc * Word8Vector.vector * int * int -> int =
MLWorks.Internal.Value.cast MLWorks.Internal.IO.write
val seek : FileSys.file_desc * int * int-> int = MLWorks.Internal.IO.seek
val size : FileSys.file_desc -> int = FileSys.ST.size o FileSys.fstat
val socket : int * int * int -> FileSys.file_desc =
env "system os unix socket"
val connect : FileSys.file_desc * sockaddr -> unit =
env "system os unix connect"
val bind : int * sockaddr -> int = env "system os unix bind"
datatype passwd =
PASSWD of {dir : string,
gecos : string,
gid : int,
name : string,
passwd : string,
shell : string,
uid : int}
val getpwent : unit -> passwd = env "system os unix getpwent"
val setpwent : unit -> unit = env "system os unix setpwent"
val endpwent : unit -> unit = env "system os unix endpwent"
val getpwuid : int -> passwd = env "system os unix getpwuid"
val getpwnam : string -> passwd = env "system os unix getpwnam"
val kill : int * int -> unit = env "system os unix kill"
val af_unix : int = env "system os unix af_unix"
val af_inet : int = env "system os unix af_inet"
val sock_stream : int = env "system os unix sock_stream"
val sock_dgram : int = env "system os unix sock_dgram"
val o_rdonly : int = env "system os unix o_rdonly"
val o_wronly : int = env "system os unix o_wronly"
val o_append : int = env "system os unix o_append"
val o_creat : int = env "system os unix o_creat"
val o_trunc : int = env "system os unix o_trunc"
end
;
