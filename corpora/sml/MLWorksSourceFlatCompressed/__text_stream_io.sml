require "text_stream_io";
require "_stream_io";
require "__char_vector";
require "__char_array";
require "__char";
require "__substring";
require "__text_prim_io";
structure TextStreamIO: TEXT_STREAM_IO =
struct
local
structure S = StreamIO(structure PrimIO = TextPrimIO
structure Vector = CharVector
structure Array = CharArray
val someElem = Char.chr 0);
in
fun inputLine (f: S.instream) =
let
fun loop(i,g) = case S.input1 g of
SOME(c, g') =>
if c = Char.chr 10 then ([c],true,g')
else let val (l,b,g'')= loop(i+1,g') in (c::l,b,g'') end
| NONE => ([],false,g)
val (l,lastCharNewline,f') = loop(0,f)
in
(if l<>[] andalso (not lastCharNewline)
then implode (l@[#"\n"])
else implode l,
f')
end
fun outputSubstr(f:S.outstream, ss:Substring.substring) =
S.output(f,Substring.string ss)
open S
end
end
;
