signature INBUFFER =
sig
type InBuffer
exception Eof
exception Position
type StateEncapsulation
val mkInBuffer : (int -> string) -> InBuffer
val mkLineInBuffer : ((int -> string) * int * bool) -> InBuffer
val getpos : InBuffer -> StateEncapsulation
val position : (InBuffer * StateEncapsulation) -> unit
val eof : InBuffer -> bool
val clear_eof : InBuffer -> unit
val getchar : InBuffer -> int
val getlinenum : InBuffer -> int
val getlinepos : InBuffer -> int
val getlastlinepos : InBuffer -> int
val flush_to_nl : InBuffer -> unit
end
;
