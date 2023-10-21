require "inet_sock";
require "__pre_sock";
require "__socket";
require "__generic_sock";
require "__option";
structure INetSock : INET_SOCK =
struct
structure SOCK = Socket.SOCK
val sockFn = MLWorks.Internal.Runtime.environment
datatype inet = INET
type 'a sock = (inet, 'a) Socket.sock
type 'a stream_sock = 'a Socket.stream sock
type dgram_sock = Socket.dgram sock
type sock_addr = inet Socket.sock_addr
val inetAF = Option.valOf(Socket.AF.fromString "INET")
local
val toInetAddr : (PreSock.addr * int) -> PreSock.addr =
sockFn "system os toInetAddr"
val fromInetAddr : PreSock.addr -> (PreSock.addr * int) =
sockFn "system os fromInetAddr"
val inetAny : int -> PreSock.addr =
sockFn "system os inetany"
in
fun toAddr (PreSock.INADDR a, port) = PreSock.ADDR(toInetAddr(a, port))
fun fromAddr (PreSock.ADDR addr) =
let val (a, port) = fromInetAddr addr
in (PreSock.INADDR a, port)
end
fun any port = PreSock.ADDR(inetAny port)
end
structure UDP =
struct
fun socket () = GenericSock.socket (inetAF, SOCK.dgram)
fun socket' proto = GenericSock.socket' (inetAF, SOCK.dgram, proto)
end
structure TCP =
struct
fun socket () = GenericSock.socket (inetAF, SOCK.stream)
fun socket' proto = GenericSock.socket' (inetAF, SOCK.stream, proto)
local
val ctlDELAY : (int * bool option) -> bool = sockFn "system os ctlNODELAY"
in
fun getNODELAY (PreSock.SOCK fd) = ctlDELAY(fd, NONE)
fun setNODELAY (PreSock.SOCK fd, flg) = ignore(ctlDELAY(fd, SOME flg))
end
end
end
;
