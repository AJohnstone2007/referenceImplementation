require "inbuffer";
require "../basics/location";
functor InBuffer (
structure Location: LOCATION
) : INBUFFER =
struct
datatype InBuffer =
INBUFFER of
{buffer : string ref,
lastpos : int ref,
position : int ref,
bs : int ref,
ln : int ref,
col : int ref,
lastcol : int ref,
eof : bool ref,
more : (int -> string)}
type StateEncapsulation = int * int
exception Eof
exception Position
exception Forget
fun mkInBuffer (f) = INBUFFER
{buffer = ref "", lastpos = ref 0, position = ref 0, bs = ref 0,
ln = ref Location.first_line, col = ref Location.first_col,
lastcol = ref Location.first_col, eof = ref false, more = f}
fun mkLineInBuffer (f,line, eof) = INBUFFER
{buffer = ref "", lastpos = ref 0, position = ref 0, bs = ref 0,
ln = ref line, col = ref Location.first_col,
lastcol = ref Location.first_col, eof = ref eof, more = f}
fun getsomemore (INBUFFER {buffer, lastpos, position, bs, eof, more, ln, ...}) =
if (!eof) then
raise Eof
else
let
val s = more (!ln)
val left = size(!buffer) - !lastpos
val some_more = size s <> 0
val next = if some_more then s else (eof := true; "")
val new =
if left = 0 then
next
else
substring (!buffer, !lastpos, left) ^ next
in
bs := !bs + !lastpos;
position := !position - !lastpos;
lastpos := 0;
buffer := new;
some_more
end
fun getpos (INBUFFER {position, bs, col, ...}) = (!position + !bs, !col)
fun eof (INBUFFER {eof, ...}) = !eof
fun clear_eof (ib as INBUFFER {eof, ...}) =
(eof := false;
ignore(getsomemore ib);
()
)
fun setPosition (x as INBUFFER {buffer, lastpos, position, bs, ln, col, ...}, (n,charpos)) =
let
fun count_newlines (buffer,from,to) =
let fun cnaux (b,f,t,res) =
if f = t then res
else
if MLWorks.String.ordof(b,f) = ord #"\n" then cnaux (b,f+1,t,res+1)
else cnaux (b,f+1,t,res)
in
if from > to
then ~(cnaux (buffer,to,from,0))
else cnaux (buffer,from,to,0)
end
val bp = n - !bs
in
if bp < 0 then
raise Position
else
if bp > size (!buffer) then
(ignore(getsomemore x); setPosition (x, (n,charpos)))
else
let
val deltalines = count_newlines (!buffer, !position, bp)
in
ln := !ln + deltalines;
col := charpos;
position := bp;
lastpos := bp
end
end
val position = setPosition
val tabsize = 8
fun getchar (x as INBUFFER {buffer, position, ln, col, lastcol, ...}) =
let
val pos = !position
in
if pos >= size (!buffer) then
if getsomemore x then
getchar x
else
ord #"\n"
else
(position := pos+1;
let
val char = MLWorks.String.ordof(!buffer,pos)
in
if char = ord #"\n" then
(ln := !ln + 1; lastcol := !col; col := Location.first_col)
else
col := !col + 1;
char
end)
end
fun getlinenum (INBUFFER {ln, ...}) = !ln
fun getlinepos (INBUFFER {col, ...}) = !col
fun getlastlinepos (INBUFFER {lastcol, ...}) = !lastcol
fun flush_to_nl (b as (INBUFFER {buffer, position, eof,...})) =
if (!eof)
then ()
else
let val pos = !position
in
if pos > 0 andalso pos-1 < size(!buffer) andalso MLWorks.String.ordof(!buffer,pos-1) = ord #"\n"
then ()
else
while (getchar b) <> ord #"\n"
do ()
end
end
;
