require "socket.sml";
require "__pre_sock.sml";
require "__word8_array";
require "__word8_vector";
require "__time";
require "__os";
require "__int";
require "__list";
structure Socket : SOCKET =
struct
structure W8A = Word8Array
structure W8V = Word8Vector
val sockFn = MLWorks.Internal.Runtime.environment
type w8vector = W8V.vector
type w8array = W8A.array
type sockFD = PreSock.socket
open PreSock
datatype dgram = DGRAM
datatype 'a stream = STREAM
datatype passive = PASSIVE
datatype active = ACTIVE
structure AF =
struct
type addr_family = PreSock.addr_family
val listAddrFamilies : unit -> PreSock.system_const list
= sockFn "system os listAddrFamilies"
fun list () =
List.map (fn arg => (#2 arg, AF arg)) (listAddrFamilies ())
fun toString (AF(_, name)) = name
fun fromString name =
(case PreSock.findSysConst(name, listAddrFamilies ()) of
NONE => NONE
| (SOME af) => SOME(AF af) )
end
structure SOCK =
struct
type sock_type = PreSock.sock_type
val listSockTypes : unit -> PreSock.system_const list
= sockFn "system os listSockTypes"
val stream = SOCKTY(PreSock.bindSysConst ("STREAM", listSockTypes ()))
val dgram = SOCKTY(PreSock.bindSysConst ("DGRAM", listSockTypes ()))
fun list () =
List.map (fn arg => (#2 arg, SOCKTY arg)) (listSockTypes ())
fun toString (SOCKTY(_, name)) = name
fun fromString name =
(case PreSock.findSysConst(name, listSockTypes ()) of
NONE => NONE
| (SOME ty) => SOME(SOCKTY ty) )
end
structure Ctl =
struct
local
fun getOpt ctlFn (PreSock.SOCK fd) = ctlFn(fd, NONE)
fun setOpt ctlFn (PreSock.SOCK fd, value) = ignore(ctlFn(fd, SOME value))
val ctlDEBUG : (sockFD * bool option) -> bool =
sockFn "system os ctlDEBUG"
val ctlREUSEADDR : (sockFD * bool option) -> bool =
sockFn "system os ctlREUSEADDR"
val ctlKEEPALIVE : (sockFD * bool option) -> bool =
sockFn "system os ctlKEEPALIVE"
val ctlDONTROUTE : (sockFD * bool option) -> bool =
sockFn "system os ctlDONTROUTE"
val ctlLINGER : (sockFD * int option option) -> int option =
sockFn "system os ctlLINGER"
val ctlBROADCAST : (sockFD * bool option) -> bool =
sockFn "system os ctlBROADCAST"
val ctlOOBINLINE : (sockFD * bool option) -> bool =
sockFn "system os ctlOOBINLINE"
val ctlSNDBUF : (sockFD * int option) -> int =
sockFn "system os ctlSNDBUF"
val ctlRCVBUF : (sockFD * int option) -> int =
sockFn "system os ctlSNDBUF"
in
fun getDEBUG x = getOpt ctlDEBUG x
fun setDEBUG x = setOpt ctlDEBUG x
fun getREUSEADDR x = getOpt ctlREUSEADDR x
fun setREUSEADDR x = setOpt ctlREUSEADDR x
fun getKEEPALIVE x = getOpt ctlKEEPALIVE x
fun setKEEPALIVE x = setOpt ctlKEEPALIVE x
fun getDONTROUTE x = getOpt ctlDONTROUTE x
fun setDONTROUTE x = setOpt ctlDONTROUTE x
fun getLINGER sock =
(case (getOpt ctlLINGER sock) of
NONE => NONE
| (SOME t) => SOME(Time.fromSeconds(Int.toLarge t)) )
fun setLINGER (sock, NONE) = setOpt ctlLINGER (sock, NONE)
| setLINGER (sock, SOME time) =
setOpt ctlLINGER (sock, SOME(Int.fromLarge (Time.toSeconds time)))
fun getBROADCAST x = getOpt ctlBROADCAST x
fun setBROADCAST x = setOpt ctlBROADCAST x
fun getOOBINLINE x = getOpt ctlOOBINLINE x
fun setOOBINLINE x = setOpt ctlOOBINLINE x
fun getSNDBUF x = getOpt ctlSNDBUF x
fun setSNDBUF x = setOpt ctlSNDBUF x
fun getRCVBUF x = getOpt ctlRCVBUF x
fun setRCVBUF x = setOpt ctlRCVBUF x
local
val getTYPE' : sockFD -> PreSock.system_const = sockFn "system os getTYPE"
val getERROR' : sockFD -> bool = sockFn "system os getERROR"
in
fun getTYPE (SOCK fd) = SOCKTY(getTYPE' fd)
fun getERROR (SOCK fd) = getERROR' fd
end
local
val getPeerName' : sockFD -> addr = sockFn "system os getPeerName"
val getSockName' : sockFD -> addr = sockFn "system os getSockName"
fun getName f (SOCK fd) = ADDR(f fd)
in
fun getPeerName sock = getName getPeerName' sock
fun getSockName sock = getName getSockName' sock
end
local
val setNBIO' : (sockFD * bool) -> unit = sockFn "system os setNBIO"
val getNREAD' : sockFD -> int = sockFn "system os getNREAD"
val getATMARK' : sockFD -> bool = sockFn "system os getATMARK"
in
fun setNBIO (SOCK fd, flg) = setNBIO'(fd, flg)
fun getNREAD (SOCK fd) = getNREAD' fd
fun getATMARK (SOCK fd) = getATMARK' fd
end
end
end
fun sameAddr (ADDR a1, ADDR a2) = (a1 = a2)
local
val getAddrFamily : addr -> af = sockFn "system os getAddrFamily"
in
fun familyOfAddr (ADDR a) = AF(getAddrFamily a)
end
local
val accept' : int -> (int * addr) = sockFn "system os accept"
val bind' : (int * addr) -> unit = sockFn "system os bind"
val connect' : (int * addr) -> unit = sockFn "system os connect"
val listen' : (int * int) -> unit = sockFn "system os listen"
val close' : int -> unit = sockFn "system os close"
in
fun accept (SOCK fd) =
let val (newFD, addr) = accept' fd
in (SOCK newFD, ADDR addr)
end
fun bind (SOCK fd, ADDR addr) = bind' (fd, addr)
fun connect (SOCK fd, ADDR addr) = connect' (fd, addr)
fun listen (SOCK fd, backLog) = listen' (fd, backLog)
fun close (SOCK fd) = close' fd
end
datatype shutdown_mode = NO_RECVS | NO_SENDS | NO_RECVS_OR_SENDS
local
val shutdown' : (int * int) -> unit = sockFn "system os shutdown"
fun how NO_RECVS = 0
| how NO_SENDS = 1
| how NO_RECVS_OR_SENDS = 2
in
fun shutdown (SOCK fd, mode) = shutdown' (fd, how mode)
end
local
val fdToIOD: MLWorks.Internal.IO.file_desc -> OS.IO.iodesc =
sockFn "system os fdToIOD"
in
fun pollDesc (SOCK fd) =
valOf(OS.IO.pollDesc(fdToIOD(MLWorks.Internal.IO.FILE_DESC fd)))
end
type out_flags = {don't_route : bool, oob : bool}
type in_flags = {peek : bool, oob : bool}
type 'a buf = {buf : 'a, i : int, sz : int option}
local
fun chk (len, buf, i, NONE) =
if ((i < 0) orelse (len < i))
then raise Subscript
else (buf, i, len - i)
| chk (len, buf, i, SOME sz) =
if ((i < 0) orelse (sz < 0) orelse (len-i < sz))
then raise Subscript
else (buf, i, sz)
in
fun vbuf {buf, i, sz} = chk (W8V.length buf, buf, i, sz)
fun abuf {buf, i, sz} = chk (W8A.length buf, buf, i, sz)
end
val dfltDon'tRoute = false
val dfltOOB = false
val dfltPeek = false
local
val sendV : (int * w8vector * int * int * bool * bool) -> int
= sockFn "system os sendBuf"
val sendA : (int * w8array * int * int * bool * bool) -> int
= sockFn "system os sendBuf"
in
fun sendVec (SOCK fd, buffer) =
let val (vec, i, len) = vbuf buffer
in if (len > 0) then sendV (fd, vec, i, len, dfltDon'tRoute, dfltOOB) else 0
end
fun sendArr (SOCK fd, buffer) =
let val (arr, i, len) = abuf buffer
in if (len > 0) then sendA (fd, arr, i, len, dfltDon'tRoute, dfltOOB) else 0
end
fun sendVec' (SOCK fd, buffer, {don't_route, oob}) =
let val (vec, i, len) = vbuf buffer
in if (len > 0) then sendV (fd, vec, i, len, don't_route, oob) else 0
end
fun sendArr' (SOCK fd, buffer, {don't_route, oob}) =
let val (arr, i, len) = abuf buffer
in if (len > 0) then sendA (fd, arr, i, len, don't_route, oob) else 0
end
end
local
val sendToV : (int * w8vector * int * int * bool * bool * addr) -> int
= sockFn "system os sendBufTo"
val sendToA : (int * w8array * int * int * bool * bool * addr) -> int
= sockFn "system os sendBufTo"
in
fun sendVecTo (SOCK fd, ADDR addr, buffer) =
let
val (vec, i, len) = vbuf buffer
in
if (len > 0)
then sendToV(fd, vec, i, len, dfltDon'tRoute, dfltOOB, addr)
else 0
end
fun sendArrTo (SOCK fd, ADDR addr, buffer) =
let
val (arr, i, len) = abuf buffer
in
if (len > 0)
then sendToA(fd, arr, i, len, dfltDon'tRoute, dfltOOB, addr)
else 0
end
fun sendVecTo' (SOCK fd, ADDR addr, buffer, {don't_route, oob}) =
let
val (vec, i, len) = vbuf buffer
in
if (len > 0)
then sendToV(fd, vec, i, len, don't_route, oob, addr)
else 0
end
fun sendArrTo' (SOCK fd, ADDR addr, buffer, {don't_route, oob}) =
let
val (arr, i, len) = abuf buffer
in
if (len > 0)
then sendToA(fd, arr, i, len, don't_route, oob, addr)
else 0
end
end
local
val recvV' : (int * int * bool * bool) -> w8vector
= sockFn "system os recv"
fun recvV (_, 0, _, _) = W8V.fromList[]
| recvV (SOCK fd, nbytes, peek, oob) =
if (nbytes < 0)
then raise Subscript
else recvV' (fd, nbytes, peek, oob)
val recvA : (int * w8array * int * int * bool * bool) -> int
= sockFn "system os recvBuf"
in
fun recvVec (sock, sz) = recvV (sock, sz, dfltPeek, dfltOOB)
fun recvArr (SOCK fd, buffer) =
let
val (buf, i, sz) = abuf buffer
in
if (sz > 0)
then recvA(fd, buf, i, sz, dfltPeek, dfltOOB)
else 0
end
fun recvVec' (sock, sz, {peek, oob}) = recvV (sock, sz, peek, oob)
fun recvArr' (SOCK fd, buffer, {peek, oob}) =
let
val (buf, i, sz) = abuf buffer
in
if (sz > 0) then recvA(fd, buf, i, sz, peek, oob) else 0
end
end
local
val recvFromV' : (int * int * bool * bool) -> (w8vector * addr)
= sockFn "system os recvFrom"
fun recvFromV (_, 0, _, _) = (W8V.fromList[], (ADDR(W8V.fromList[])))
| recvFromV (SOCK fd, sz, peek, oob) =
if (sz < 0)
then raise Size
else
let val (data, addr) = recvFromV' (fd, sz, peek, oob)
in (data, ADDR addr)
end
val recvFromA : (int * w8array * int * int * bool * bool) -> (int * addr)
= sockFn "system os recvBufFrom"
in
fun recvVecFrom (sock, sz) = recvFromV (sock, sz, dfltPeek, dfltOOB)
fun recvArrFrom (SOCK fd, {buf, i}) =
let
val (buf, i, sz) = abuf{buf=buf, i=i, sz=NONE}
in
if (sz > 0)
then
let val (n, addr) = recvFromA(fd, buf, i, sz, dfltPeek, dfltOOB)
in (n, ADDR addr)
end
else (0, (ADDR(W8V.fromList[])))
end
fun recvVecFrom' (sock, sz, {peek, oob}) = recvFromV (sock, sz, peek, oob)
fun recvArrFrom' (SOCK fd, {buf, i}, {peek, oob}) =
let
val (buf, i, sz) = abuf{buf=buf, i=i, sz=NONE}
in
if (sz > 0)
then let val (n, addr) = recvFromA(fd, buf, i, sz, peek, oob)
in (n, ADDR addr)
end
else (0, (ADDR(W8V.fromList[])))
end
end
end
;
