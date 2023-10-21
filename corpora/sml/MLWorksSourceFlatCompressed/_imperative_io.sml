require "mono_array";
require "mono_vector";
require "stream_io";
require "imperative_io";
functor ImperativeIO(structure StreamIO : STREAM_IO
structure Vector: MONO_VECTOR
structure Array: MONO_ARRAY
sharing type StreamIO.elem=Vector.elem=Array.elem
sharing type StreamIO.vector=Vector.vector
=Array.vector
) : IMPERATIVE_IO =
struct
structure StreamIO = StreamIO
type instream = StreamIO.instream ref
type outstream = StreamIO.outstream ref
type elem = StreamIO.elem
type vector = StreamIO.vector
val mkInstream = ref
val getInstream = !
val setInstream = op :=
val mkOutstream = ref
val getOutstream = !
val setOutstream = op :=
fun endOf f = if StreamIO.endOfStream f then f
else endOf(#2(StreamIO.input f))
fun closeIn(r as ref f) = (StreamIO.closeIn f; r := endOf f)
fun inputN (r as ref f, n) = let val (v,f') = StreamIO.inputN(f,n)
in r:=f'; v end
fun input (r as ref f) = let val (v,f') = StreamIO.input f in r:=f'; v end
fun input1 (r as ref f) =
case StreamIO.input1 f of
SOME (v,f') => (r:=f'; SOME v)
| NONE => NONE
fun inputAll(r as ref f) =
let val v = StreamIO.inputAll f
in
r := endOf f; v
end
val endOfStream = StreamIO.endOfStream o !
fun lookahead(ref f) = case StreamIO.input1 f of
NONE => NONE
| SOME(e, _) => SOME e
fun getPosIn(ref f) = StreamIO.getPosIn f
fun setPosIn(f, pos) = f:=(StreamIO.setPosIn pos)
val closeOut = StreamIO.closeOut o !
fun output(ref f, v) = StreamIO.output(f,v)
fun output1(ref f, x) = StreamIO.output1(f,x)
val flushOut = StreamIO.flushOut o !
fun canInput(ref instream, i) = case StreamIO.canInput(instream, i) of
SOME j => i=j
| NONE => false
fun getPosOut (ref f) = StreamIO.getPosOut f
fun setPosOut (f,pos) = f:=(StreamIO.setPosOut pos)
end
;
