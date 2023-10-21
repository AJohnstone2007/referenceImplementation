require "$.basis.__word8";
require "$.basis.__vector";
signature BINARY =
sig
eqtype binary
val fromString : string -> binary option
val toString : binary -> string
val fromByte : Word8.word -> binary
val toByte : binary -> Word8.word
val toAscii : string -> binary Vector.vector
val fromAscii : binary Vector.vector -> string
val printAscii : string -> unit
end
;
