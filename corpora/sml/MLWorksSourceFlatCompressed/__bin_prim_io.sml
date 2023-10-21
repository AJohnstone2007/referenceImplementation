require "__word8_vector";
require "__word8_array";
require "__word8";
require "prim_io";
require "_prim_io";
require "__position";
structure BinPrimIO : PRIM_IO
where type array = Word8Array.array
where type vector= Word8Vector.vector
where type elem = Word8.word
where type pos = Position.int
= PrimIO (structure A = Word8Array
structure V = Word8Vector
val someElem = 0w0 : Word8.word
type pos = Position.int
val compare =
fn (x,y) =>
if (Position.<)(x,y) then LESS
else if x=y then EQUAL
else GREATER)
;
