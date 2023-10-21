require "__string_cvt";
require "__sys_word";
require "__word";
require "__word8";
require "__word8_vector";
require "__list";
require "__general";
structure PreSock =
struct
type system_const = (int * string)
exception SysConstNotFound of string
fun findSysConst (name, l) =
let
fun look [] = NONE
| look ((sysConst : system_const)::r) =
if (#2 sysConst = name) then SOME sysConst else look r
in
look l
end
fun bindSysConst (name, l) =
case findSysConst(name, l) of
(SOME sc) => sc
| NONE => raise(SysConstNotFound name)
type addr = Word8Vector.vector
type af = system_const
type socket = int
datatype in_addr = INADDR of addr
datatype addr_family = AF of af
datatype sock_type = SOCKTY of system_const
datatype ('sock, 'af) sock = SOCK of socket
datatype 'af sock_addr = ADDR of addr
local
structure SysW = SysWord
structure SCvt = StringCvt
fun toW (getc, strm) =
let
fun scan radix strm =
(case (SysW.scan radix getc strm) of
NONE => NONE
| (SOME(w, strm)) => SOME(w, strm) )
in
case (getc strm) of
NONE => NONE
| (SOME(#"0", strm')) =>
(case (getc strm') of
NONE => SOME(0w0, strm')
| (SOME(#"x", strm'')) => scan SCvt.HEX strm''
| (SOME(#"X", strm'')) => scan SCvt.HEX strm''
| _ => scan SCvt.OCT strm )
| _ => scan SCvt.DEC strm
end
fun chk (w, bits) =
if (SysW.>= (SysW.>>(0wxffffffff, Word.-(0w32, bits)), w))
then w
else raise General.Overflow
fun scan getc strm =
(case toW (getc, strm) of
NONE => NONE
| SOME(w, strm') => scanRest getc ([w], strm') )
and scanRest getc (l, strm) =
(case getc strm of
SOME(#".", strm') => (case toW (getc, strm')
of NONE => SOME(List.rev l, strm)
| SOME(w, strm'') => scanRest getc (w::l, strm'')
)
| _ => SOME(List.rev l, strm) )
in
fun toWords getc strm =
(case (scan getc strm) of
SOME([a, b, c, d], strm) =>
SOME([chk(a, 0w8), chk(b, 0w8), chk(c, 0w8), chk(d, 0w8)], strm)
| SOME([a, b, c], strm) =>
SOME([chk(a, 0w8), chk(b, 0w8), chk(c, 0w16)], strm)
| SOME([a, b], strm) =>
SOME([chk(a, 0w8), chk(b, 0w24)], strm)
| SOME([a], strm) =>
SOME([chk(a, 0w32)], strm)
| _ => NONE )
fun fromBytes (a, b, c, d) =
let val fmt = Word8.fmt StringCvt.DEC
in concat [fmt a, ".", fmt b, ".", fmt c, ".", fmt d]
end
end
end;
