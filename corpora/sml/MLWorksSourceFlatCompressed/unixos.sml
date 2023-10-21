require "^.basis.__word32";
require "^.basis.__sys_word";
require "^.basis.__word8_vector";
require "^.basis.__position";
require "__time";
signature UNIXOS =
sig
structure Error : sig
type syserror
exception SysErr of (string * syserror option)
val toWord : syserror -> Word32.word
val fromWord : Word32.word -> syserror
val errorMsg : syserror -> string
val errorName : syserror -> string
val syserror : string -> syserror option
end
exception WouldBlock
datatype sockaddr = SOCKADDR_UNIX of string
val environment : unit -> string list
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
utime : Time.time}
val bind : int * sockaddr -> int
val getsockname : int -> sockaddr
val getpeername : int -> sockaddr
val accept : int -> int * sockaddr
val listen : int * int -> unit
val execve : string * string list * string list -> unit
val execv : string * string list -> unit
val execvp : string * string list -> unit
val fork_execve : string * string list * string list * int * int * int -> int
val fork_execv : string * string list * int * int * int -> int
val fork_execvp : string * string list -> int
val kill : int * int -> unit
datatype iodesc = IODESC of int
structure FileSys : sig
type file_desc
val fdToWord : file_desc -> Word32.word
val wordToFD : Word32.word -> file_desc
val fdToIOD : file_desc -> iodesc
val iodToFD : iodesc -> file_desc
type dirstream
val opendir : string -> dirstream
val readdir : dirstream -> string option
val rewinddir : dirstream -> unit
val closedir : dirstream -> unit
val chdir : string -> unit
val getcwd : unit -> string
val stdin : file_desc
val stdout : file_desc
val stderr : file_desc
structure S : sig
type mode
val irwxo : mode
end
structure O : sig
eqtype flags
val append : flags
val excl : flags
val noctty : flags
val nonblock : flags
val sync : flags
val trunc : flags
end
datatype open_mode = O_RDONLY | O_WRONLY | O_RDWR
val openf : string * open_mode * O.flags -> file_desc
val createf : string * open_mode * O.flags * S.mode -> file_desc
val creat : string * S.mode -> file_desc
val umask : S.mode -> S.mode
val link : { old: string, new: string} -> unit
val mkdir : string * S.mode -> unit
val unlink : string -> unit
val rmdir : string -> unit
val rename : { new: string, old: string} -> unit
val readlink : string -> string
eqtype dev
val wordToDev : SysWord.word -> dev
val devToWord : dev -> SysWord.word
eqtype ino
val wordToIno : SysWord.word -> ino
val inoToWord : ino -> SysWord.word
structure ST : sig
type stat
val isDir: stat -> bool
val isChr: stat -> bool
val isBlk: stat -> bool
val isReg: stat -> bool
val isFIFO: stat -> bool
val isLink: stat -> bool
val isSock: stat -> bool
val mode: stat -> S.mode
val ino: stat -> ino
val dev: stat -> dev
val size: stat -> Position.int
val mtime: stat -> Time.time
end
val stat : string -> ST.stat
val fstat : file_desc -> ST.stat
val lstat : string -> ST.stat
datatype access_mode = A_READ | A_WRITE | A_EXEC
val access : (string * access_mode list) -> bool
val utime : (string * {actime : Time.time, modtime : Time.time} option) -> unit
end
structure IO : sig
val close : FileSys.file_desc -> unit
end
val can_input : FileSys.file_desc -> int
val set_block_mode : FileSys.file_desc * bool -> unit
val open_ : string * int * int -> FileSys.file_desc
val read : FileSys.file_desc * int -> Word8Vector.vector
val write : FileSys.file_desc * Word8Vector.vector * int * int -> int
val seek : FileSys.file_desc * int * int -> int
val size : FileSys.file_desc -> int
val socket : int * int * int -> FileSys.file_desc
val connect : FileSys.file_desc * sockaddr -> unit
datatype passwd =
PASSWD of {dir : string,
gecos : string,
gid : int,
name : string,
passwd : string,
shell : string,
uid : int}
val getpwent : unit -> passwd
val setpwent : unit -> unit
val endpwent : unit -> unit
val getpwuid : int -> passwd
val getpwnam : string -> passwd
val af_unix : int
val af_inet : int
val sock_stream : int
val sock_dgram : int
val o_rdonly : int
val o_wronly : int
val o_append : int
val o_creat : int
val o_trunc : int
end
;
