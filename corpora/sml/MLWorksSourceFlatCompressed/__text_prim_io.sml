require "__char_vector";
require "__char_array";
require "__char";
require "prim_io";
require "_prim_io";
require "__position";
structure TextPrimIO : PRIM_IO
where type array = CharArray.array
where type vector = CharVector.vector
where type elem = Char.char
= PrimIO (structure A = CharArray
structure V = CharVector
val someElem = Char.chr 0
type pos = Position.int
val compare =
fn (x,y) =>
if Position.<(x,y) then LESS
else if x=y then EQUAL
else GREATER)
;
