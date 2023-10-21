require "mono_array";
require "mono_vector";
require "__io";
require "prim_io";
require "stream_io";
functor StreamIO(structure PrimIO : PRIM_IO
structure Vector : MONO_VECTOR
structure Array: MONO_ARRAY
val someElem : PrimIO.elem
sharing type PrimIO.vector=Array.vector=Vector.vector
sharing type PrimIO.array=Array.array
sharing type Array.elem = PrimIO.elem = Vector.elem
) : STREAM_IO =
struct
structure PrimIO=PrimIO
type elem = PrimIO.elem
type Array = PrimIO.array
type vector = PrimIO.vector
type pos = PrimIO.pos
type reader = PrimIO.reader
type writer = PrimIO.writer
datatype buffer = Buf of {
more : more ref,
data : vector,
basePos : pos option,
emptyMeansEof: bool,
name : string
}
and more = GETmore of PrimIO.reader
| ISmore of buffer
| NOmore
| Truncated of unit -> unit
and instream = In of {
pos : int,
buffer : buffer}
datatype in_pos = INP of (instream ref * pos * int)
val empty = Vector.fromList[]
fun mkInstream(r, v) =
let
val r' as PrimIO.RD{name,getPos,...} = PrimIO.augmentReader r
in In{pos=0,
buffer=Buf{name=name,
emptyMeansEof=false,
data=v,
more = ref(GETmore r'),
basePos=SOME(valOf getPos())
handle Option => NONE
| IO.RandomAccessNotSupported => NONE
}}
end
fun handler(Buf{name,...},function,e) =
raise IO.Io{function=function,name=name,cause=e}
fun closeBuf (Buf{more=ref NOmore,...}) = ()
| closeBuf (Buf{more=ref(ISmore buf),...}) = closeBuf buf
| closeBuf (b as Buf{more as ref(GETmore(PrimIO.RD{close,...})),...}) =
(close() handle e => handler(b,"closeIn",e);
more := NOmore)
| closeBuf (b as Buf{more as ref(Truncated closeFn),...}) =
(closeFn() handle e => handler(b,"closeIn",e);
more:=NOmore)
fun closeIn (In{buffer,...}) = closeBuf buffer
exception WouldBlock
fun filbuf (doRead: PrimIO.reader -> vector, mlOp: string)
(buffer as Buf{data,more,emptyMeansEof,name,basePos})=
let val len = Vector.length data
in if len = 0 andalso emptyMeansEof
then {eof=true,
rest=Buf{data=empty,more=more,name=name,
emptyMeansEof=false,basePos=basePos}}
else case !more
of ISmore buf' =>
{eof=false, rest=buf'}
| NOmore => {eof=true,
rest=Buf{data=empty,more=more,
emptyMeansEof=true,
name=name,basePos=basePos}}
| Truncated _ => {eof=true,
rest=Buf{data=empty,more=more,
emptyMeansEof=true,
name=name,basePos=basePos}}
| m as GETmore (gm as PrimIO.RD{getPos,...}) =>
(let val basePos' = SOME(valOf getPos())
handle Option => NONE
| IO.RandomAccessNotSupported => NONE
val v = doRead gm
val buf' = Buf{data=v, more=ref m,
name=name, basePos=basePos',
emptyMeansEof=true}
in more := ISmore buf';
{eof=false,
rest=buf'}
end handle e => handler(buffer,mlOp,e))
end
fun generalizedInput (filbuf': buffer -> {eof: bool, rest: buffer})
: instream -> vector * instream =
let fun get(In{pos,buffer as Buf{data,...}}) =
let val len = Vector.length data
in if pos < len
then (Vector.extract(data, pos, SOME (len - pos)),
In{pos=len,buffer=buffer})
else case filbuf' buffer
of {eof=true,rest} => (empty,In{pos=0,buffer=rest})
| {eof=false,rest} => get (In{pos=0,buffer=rest})
end
in get
end
fun chunk1 (PrimIO.RD{chunkSize,readVec=SOME read,...}) = read chunkSize
| chunk1 _ = raise IO.BlockingNotSupported
fun chunkN n (PrimIO.RD{chunkSize=1,readVec=SOME read,...}) = read n
| chunkN n (PrimIO.RD{chunkSize=k,readVec=SOME read,...}) =
read (((n-1+k) div k) * k)
| chunkN _ _ = raise IO.BlockingNotSupported
val eofFilbuf = filbuf (chunk1, "endOfStream")
fun endOfStream (In{pos,buffer as Buf{data,...}}) =
if pos < Vector.length data then false
else let val {eof,rest=Buf{data,emptyMeansEof,...}}
= eofFilbuf buffer
in eof orelse (emptyMeansEof
andalso (Vector.length data = 0))
end
val input = generalizedInput(filbuf(chunk1, "input"))
local
fun bigchunk (arg as PrimIO.RD{getPos,endPos,...}) = chunk1 arg
val biginput = generalizedInput(filbuf(bigchunk,"inputAll"))
in
fun inputAll f =
let fun loop f =
let val (s,rest) = input f
in if Vector.length s = 0 then nil else s :: loop rest
end
val (s,rest) = biginput f
in if Vector.length s = 0 then s else Vector.concat(s :: loop rest)
end
end
local
fun nonBlockChunk (PrimIO.RD{chunkSize,readVecNB=SOME read,...})=
(case read chunkSize
of NONE => raise WouldBlock
| SOME stuff => stuff)
| nonBlockChunk _ = raise IO.NonblockingNotSupported
val inpNob = generalizedInput (filbuf (nonBlockChunk, "inputNoBlock"))
in
fun inputNoBlock x = SOME(inpNob x) handle WouldBlock => NONE
end
val input1Filbuf = filbuf(chunk1, "input1")
fun input1(In{pos,buffer as Buf{data,...}}) =
let
val len = Vector.length data
in
if pos < len
then SOME(Vector.sub(data,pos), In{pos=pos+1,buffer=buffer})
else
case input1Filbuf buffer of
{eof=true,rest} => NONE
| {eof=false,rest} => input1(In{pos=0,buffer=rest})
end
fun listInputN(In{pos,buffer as Buf{data,...}}, n) =
let val len = Vector.length data
in if pos + n <= len
then ([Vector.extract(data,pos,SOME n)],
In{pos=pos+n,buffer=buffer})
else if pos < len
then let val hd = Vector.extract(data,pos,SOME (len-pos))
val (tl,f') = listInputN(In{pos=len,buffer=buffer},
n-(len-pos))
in (hd::tl,f')
end
else case filbuf (chunkN n, "inputN") buffer
of {eof=true,rest} => (nil,In{pos=0,buffer=rest})
| {eof=false,rest} => listInputN(In{pos=0,buffer=rest},n)
end
fun inputN(f,n) =
let val (vl,f') = listInputN(f,n)
in (Vector.concat vl, f')
end
fun getReader' (Buf{more=ref(NOmore),...}, truncate) =
raise IO.ClosedStream
| getReader' (Buf{more=ref(Truncated _),...}, truncate) =
raise IO.TerminatedStream
| getReader' (Buf{more=ref(ISmore b),...}, truncate) =
getReader' (b, truncate)
| getReader' (Buf{more=more as ref(GETmore (m as PrimIO.RD{close,...})),
data, ...}, truncate) =
(if truncate then more:=(Truncated close) else ();
(m, data))
fun getReader(In{pos,buffer}) =
getReader' (buffer, true) handle e => handler(buffer,"getReader",e)
fun getPosIn(instream as In{pos,buffer as Buf bufrec}) =
let
val (PrimIO.RD r,_) = getReader' (buffer, false)
in
case (#getPos r,#setPos r,#basePos bufrec)
of (SOME getPos, SOME setPos,SOME psn) => INP(ref instream, psn,pos)
| _ => raise IO.RandomAccessNotSupported
end
handle e => raise IO.Io{name = #name bufrec,
function = "getPosIn",
cause = e}
fun filePosIn(INP(_,p,0)) = p
| filePosIn(INP(ref(In{pos,buffer as Buf bufrec}),p,n)) =
let
val (PrimIO.RD r,_) = getReader' (buffer, false)
fun readN record num =
case (#readVec r,#readVecNB r,#readArr r,#readArrNB r)
of (SOME read,_,_,_) => ignore (read num)
| (_,SOME read,_,_) => ignore (read num)
| (_,_,SOME read,_) => ignore
let val tempBuf = Array.array(num,someElem)
in read{buf=tempBuf,i=0,sz=NONE}
end
| (_,_,_,SOME read) => ignore
let val tempBuf = Array.array(num,someElem)
in read{buf=tempBuf,i=0,sz=NONE}
end
| _ => raise IO.Io{name= #name r,
function="filePosIn",
cause=IO.RandomAccessNotSupported}
in
case (#getPos r,#setPos r)
of (SOME getPos, SOME setPos) =>
let
val tmpPos = getPos ()
in
setPos p;
readN r n;
let val result = getPos()
in (setPos tmpPos;result)
end
end
| _ => raise IO.Io{name = #name bufrec,
function="filePosIn",
cause=IO.RandomAccessNotSupported}
end
fun setPosIn(pos as INP(rs as ref(instream as In{buffer as Buf bufrec,...}),
_,_)) =
let
val fpos = filePosIn pos
val (PrimIO.RD r,_) = getReader instream
in
valOf(#setPos r) fpos;
let val newstream = mkInstream (PrimIO.RD r,Vector.fromList [])
in (rs := newstream; newstream) end
end
handle Option => raise IO.Io{name= #name bufrec,
function="setPosIn",
cause = IO.RandomAccessNotSupported}
| e =>
raise IO.Io{name = #name bufrec,
function = "setPosIn",
cause = e}
datatype outputter = PUTmore of PrimIO.writer
| Terminated of unit -> unit
| Delivered of outputter
| NOmore
datatype outstream = Out of {
name : string,
data : Array,
pos : int ref,
mode : IO.buffer_mode ref,
writer : outputter ref
}
val openStreams = ref []
fun addOpenStream (s as Out{name,...}) =
(openStreams := s :: !openStreams; s)
fun rmOpenStream (s as Out{name,...}) =
let fun rm [] = [] | rm (h::t) = if h=s then t else h::(rm t)
in openStreams := rm (!openStreams) end
datatype out_pos = OUTP of (outstream ref * pos)
fun handler(Out{name,...},function, cause) =
raise IO.Io{name=name,function=function,cause=cause}
val exit_function_key = ref NONE
fun mkOutstream(w, mode) =
let val s =
case PrimIO.augmentWriter w
of w' as PrimIO.WR{name,chunkSize,...} =>
Out{name=name,data=Array.array(chunkSize,someElem),
pos=ref 0, writer=ref (PUTmore w'), mode=ref mode}
in maybe_install_exit_function s; s end
and flushOut' (Out{data,pos,
writer=ref(PUTmore(PrimIO.WR{writeArr=SOME write,...})),
...}) =
let val p = !pos
fun loop i = if i<p
then loop(i+write{buf=data,i=i,sz=SOME(p-i)}
handle e => (Array.copy{src=data,si=i,len=SOME (p-i),
dst=data,di=0};
pos := p-i;
raise e))
else ()
in pos := 0;
loop 0
end
| flushOut'(Out{writer=ref (Terminated _),...}) = ()
| flushOut'(stream as Out{writer=w as ref (Delivered old_out),...}) =
(w := old_out; maybe_install_exit_function stream; flushOut' stream)
| flushOut'(Out{writer=ref NOmore,...}) = ()
| flushOut' _ = raise IO.BlockingNotSupported
and flushOut f = flushOut' f
handle e => handler(f,"flushOut",e)
and closeOut(f as Out{writer=w as ref(PUTmore(PrimIO.WR{close,name,...})),
...})=((flushOut f;
close();
ignore(rmOpenStream f);
w:=NOmore)
handle e => handler(f,"closeOut",e))
| closeOut(f as Out{writer=w as ref(Terminated closeFn),...})=
((closeFn();
ignore(rmOpenStream f);
w:=NOmore)
handle e=> handler(f,"closeOut",e))
| closeOut(f as Out{writer=w as ref(Delivered old_out),name,...})=
(w := old_out; maybe_install_exit_function f; closeOut f)
| closeOut _ = ()
and maybe_install_exit_function s =
( ignore(addOpenStream s);
case !exit_function_key of
SOME _ => ()
| NONE =>
(exit_function_key :=
SOME(MLWorks.Internal.Exit.atExit exitFunction);
MLWorks.Deliver.add_delivery_hook delivery_hook) )
and exitFunction () =
let
fun app [] = ()
| app ((h as Out{name, ...})::t) =
(if (name = "<stdOut>" orelse name = "<stdErr>")
then flushOut h
else closeOut h;
app t)
in
app (!openStreams)
end
and delivery_hook deliverer args =
let val open_streams = !openStreams
in (
app (fn (s as Out{writer, name, ...}) =>
(flushOut s; writer := Delivered(!writer)))
open_streams;
case !exit_function_key of
NONE => ()
| SOME k => ( MLWorks.Internal.Exit.removeAtExit(k);
exit_function_key := NONE);
openStreams := [];
let val result = deliverer args
in
app (fn (s as Out{writer = writer as ref (Delivered w), ...}) =>
writer := w
| _ => ())
open_streams;
openStreams := open_streams;
exit_function_key :=
SOME(MLWorks.Internal.Exit.atExit exitFunction);
result
end)
end
val _ = exit_function_key := SOME(MLWorks.Internal.Exit.atExit exitFunction)
val _ = MLWorks.Deliver.add_delivery_hook delivery_hook
fun bigoutput(f as Out{writer=ref(PUTmore
(PrimIO.WR{writeVec=SOME write,...})),
...},
buffer as {buf,i=first,sz}) =
let
val nelems = case sz of
SOME i => i
| NONE => Vector.length buf - first
in
if nelems=0 then ()
else let val written = write buffer
in bigoutput(f, {buf=buf,i=first+written,
sz=SOME(nelems-written)})
end
end
| bigoutput (Out{name,writer=ref NOmore,...},_) = raise IO.ClosedStream
| bigoutput (stream as Out{name,writer=w as ref (Delivered old_out),...},
buffer) =
(w := old_out; maybe_install_exit_function stream;
bigoutput (stream, buffer))
| bigoutput (Out{name,writer=ref (Terminated _),...},_)
= raise IO.TerminatedStream
| bigoutput _ = raise IO.BlockingNotSupported
fun output(f as Out{writer=w as (ref NOmore),...}, _) =
handler(f,"output",IO.ClosedStream)
| output(f as Out{writer=w as (ref (Terminated _)),...},_) =
handler(f,"output",IO.TerminatedStream)
| output(f as Out{writer=w as (ref (Delivered old_out)),name,...},s) =
(w := old_out; maybe_install_exit_function f;
output(f, s))
| output(f as Out{data,pos,...}, s)=
let val slen = Vector.length s
val blen = Array.length data
val p = !pos
fun copy offset =
(Array.copyVec {src=s, si=0, len=SOME slen, dst=data, di=offset};
pos := offset + slen)
in if p+slen < blen
then copy p
else ((flushOut' f;
if slen < blen
then copy 0
else bigoutput(f,{buf=s,i=0,sz=SOME slen}))
handle e => handler(f,"output",e))
end
fun output1(f as Out{writer=w as (ref NOmore),...},_)=
handler(f,"output1",IO.ClosedStream)
| output1(f as Out{writer=w as (ref (Terminated _)),...},_) =
handler(f,"output1",IO.TerminatedStream)
| output1(f as Out{writer=w as (ref (Delivered old_out)),...},e) =
(w := old_out; maybe_install_exit_function f;
output1(f, e))
| output1(f as Out{data,pos,...}, e) =
let val blen = Array.length data
val p = !pos
in if p < blen
then (Array.update(data,p,e); pos := p+1)
else if p=0
then bigoutput(f,{buf=Vector.fromList[e],i=0,sz=SOME 1})
else (flushOut' f handle e=> handler(f,"output1",e);
output1(f,e))
end
fun getWriter(f as Out{writer=ref NOmore,...}) =
handler(f,"getWriter",IO.ClosedStream)
| getWriter(f as Out{writer=ref (Terminated _),...})=
handler(f,"getWriter",IO.TerminatedStream)
| getWriter(f as Out{writer=w as ref (Delivered old_out),...})=
(w := old_out; maybe_install_exit_function f;
getWriter f)
| getWriter(f as Out{writer=writer as
ref (PUTmore (w as PrimIO.WR {close,...})),
mode = ref m, ...}) =
(flushOut' f; writer:=(Terminated close); (w, m))
handle e => handler(f,"getWriter",e)
fun getPosOut (outstream as Out{writer=writer as ref w,name,...}) =
let
fun get_record (PUTmore(PrimIO.WR record)) = record
| get_record (Terminated _) =
raise IO.Io{name=name,
function="getPosOut",
cause=IO.TerminatedStream}
| get_record (Delivered old_out) =
(writer := old_out; maybe_install_exit_function outstream;
get_record old_out)
| get_record NOmore =
raise IO.Io{name=name,
function="getPosOut",
cause=IO.ClosedStream}
val record = get_record w
in
case (#getPos record)
of (SOME getPos) => ((flushOut outstream;
OUTP(ref outstream,getPos()))
handle e =>
raise IO.Io{name = name,
function = "getPosOut",
cause = e})
| NONE => raise IO.Io{name=name,
function="getPosOut",
cause=IO.RandomAccessNotSupported}
end
fun filePosOut (OUTP(_,p)) = p
fun setPosOut (OUTP(r as ref (outstream as Out{name,...}),fpos)) =
let
val (PrimIO.WR w,blkmde) = getWriter outstream
in
valOf(#setPos w) fpos;
ignore(rmOpenStream outstream);
let val newstream = mkOutstream(PrimIO.WR w,blkmde)
in (r := newstream; newstream) end
end
handle Option => raise IO.Io{name=name,
function="setPosOut",
cause=IO.RandomAccessNotSupported}
| e =>
raise IO.Io{name = name,
function = "setPosOut",
cause = e}
fun setBufferMode(Out{mode, ...}, bm) = mode := bm
fun getBufferMode(Out{mode=ref m, ...}) = m
local
fun nonBlockChunk n (PrimIO.RD{chunkSize=k,readVecNB=SOME read,...})=
(case read (((n-1+k) div k) * k)
of NONE => Vector.fromList []
| SOME stuff => stuff)
| nonBlockChunk _ _ = raise IO.NonblockingNotSupported
in
fun canInput(In{pos=pos,
buffer = buffer as Buf{more, data, basePos, emptyMeansEof,
...}}, i) =
let val len = Vector.length data
in
if emptyMeansEof andalso (len=0) then SOME 0 else
if (pos+i <= len) then SOME i
else let
val {eof,rest} =
filbuf (nonBlockChunk (pos+i-len),"canInput") buffer
val remaining = pos+i-len
in
case canInput(In{pos=0,buffer=rest},remaining)
of NONE => if pos=len andalso (not eof) then NONE
else SOME (len-pos)
| SOME k => if k+len-pos =0 andalso (not eof)
then NONE else SOME (len-pos+k)
end
end
end
end
;
