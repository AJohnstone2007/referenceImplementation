require "../basis/__char_array";
require "../basis/__char_vector";
require "../basis/__text_io";
require "../basis/__text_prim_io";
require "../basis/__io";
require "^.basics.location";
require "info";
functor Info (structure Location : LOCATION) : INFO =
struct
structure Location = Location
datatype severity =
ADVICE |
WARNING |
NONSTANDARD |
RECOVERABLE |
FATAL |
FAULT
local
fun sort ADVICE = 0
| sort WARNING = 1
| sort NONSTANDARD = 2
| sort RECOVERABLE = 3
| sort FATAL = 4
| sort FAULT = 4
in
val (op<) = fn (s1, s2) => sort s1 < sort s2
end
fun level_to_string ADVICE = "advice"
| level_to_string WARNING = "warning"
| level_to_string NONSTANDARD = "non-SML feature"
| level_to_string RECOVERABLE = "error"
| level_to_string FATAL = "error"
| level_to_string FAULT = "compiler fault"
datatype error =
ERROR of severity * Location.T * string
datatype options =
OPTIONS of
{error : {report_fun : (error -> unit) option,
stop : severity,
report : severity,
worst : severity ref},
listing : {outstream : TextIO.outstream,
level : int},
error_list : error list ref,
error_count : int ref
}
fun worst_error (OPTIONS {error = {worst,...},
...}) =
!worst
exception Stop of error * error list
fun make_list (severity,location,message) =
[Location.to_string location, ": ", level_to_string severity,
": ", message]
fun string_error (ERROR packet) = concat (make_list packet)
fun report_fun (ERROR packet) =
(app
(fn s => TextIO.output(TextIO.stdErr,s))
(make_list packet);
TextIO.output(TextIO.stdErr,"\n"))
fun make_default_options () =
OPTIONS {error = {report_fun = SOME report_fun,
stop = FATAL,
report = ADVICE,
worst = ref ADVICE},
listing = {outstream = TextIO.stdOut,
level = 2},
error_list = ref [],
error_count = ref 0
}
val null_writer =
let
fun null_fun_vec {buf, i, sz} =
CharVector.length(CharVector.extract(buf,i,sz))
fun null_fun_arr {buf, i, sz} =
CharVector.length(CharArray.extract(buf,i,sz))
val null_fun_option = fn _ => NONE
in
TextPrimIO.WR
{writeVecNB = SOME null_fun_option,
writeArrNB = SOME null_fun_option,
writeVec = SOME null_fun_vec,
writeArr = SOME null_fun_arr,
block = NONE,
canOutput = SOME(fn _ => true),
name = "sink",
chunkSize = 1,
close = fn _ => (),
getPos = NONE,
setPos = NONE,
endPos = NONE,
verifyPos = NONE,
ioDesc = NONE}
end
val null_out =
TextIO.mkOutstream(TextIO.StreamIO.mkOutstream(null_writer, IO.NO_BUF))
val null_options =
OPTIONS {error = {report_fun = NONE,
stop = FATAL,
report = ADVICE,
worst = ref ADVICE},
listing = {outstream = null_out,
level = 2},
error_list = ref [],
error_count = ref 0
}
val max_num_errors = ref 30;
fun error (OPTIONS {error = {report_fun=NONE, stop, ...},error_list,...})
(packet as (severity, location, message)) =
if severity < stop then () else raise Stop (ERROR packet,[])
| error (OPTIONS {error = {report_fun=SOME report_fun, stop, report, worst},
error_list, error_count,...})
(packet as (severity, location, message)) =
let
val error = ERROR packet
in
error_list := error :: (!error_list);
if severity < report then () else report_fun error;
if severity < !worst then () else worst := severity;
if NONSTANDARD < severity then error_count := (!error_count) + 1 else ();
if (!error_count) >= (!max_num_errors)
then
let val error2 = ERROR (FATAL,location,"Too many errors, giving up")
in
report_fun error2;
raise Stop (error2,(error2::(!error_list)))
end
else
if severity < stop then () else raise Stop (error,!error_list)
end
fun error'
(OPTIONS {error = {report_fun=NONE, report, worst, ...},
error_list,...})
(packet as (severity, location, message)) =
raise Stop (ERROR packet,[])
| error'
(OPTIONS {error = {report_fun=SOME report_fun, report, worst, ...},
error_list,...})
(packet as (severity, location, message)) =
let
val error = ERROR packet
in
error_list := error :: (!error_list);
if severity < report then () else report_fun error;
if severity < !worst then () else worst := severity;
raise Stop (error,!error_list)
end
fun default_error' packet = error' (make_default_options ()) packet
fun with_report_fun
(OPTIONS {error = {report_fun, stop, report, worst},
listing,
error_list,
error_count})
new_report_fun f a =
f
(OPTIONS
{error = {report_fun = SOME new_report_fun,
stop = stop,
report = report,
worst = worst},
listing = listing,
error_list = error_list,
error_count = error_count
})
a
fun wrap (OPTIONS {error = {report_fun, stop, report, worst},
listing,
...})
(stop', finish', report',location) f a =
let
val error_list = ref []
val error_count = ref 0
val old_worst = !worst
val _ = worst := ADVICE
fun finish () =
if !worst < finish'
then
if !worst < old_worst then worst := old_worst else ()
else
raise Stop (ERROR (!worst,location,"Finish of block"),!error_list)
val result =
f (OPTIONS
{error = {report_fun = report_fun,
stop = if stop' < stop then stop' else stop,
report = if report' < report then report' else report,
worst = worst},
listing = listing,
error_list = error_list,
error_count = error_count
})
a
handle exn => (finish (); raise exn)
in
finish ();
result
end
fun listing_fn(OPTIONS {listing = {level,outstream},...}) (level',text_fn) =
if level' > level
then text_fn outstream
else ()
end
;
