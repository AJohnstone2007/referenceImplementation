require "../basis/__text_io";
require "../basics/location";
signature INFO =
sig
structure Location : LOCATION
datatype severity =
ADVICE |
WARNING |
NONSTANDARD |
RECOVERABLE |
FATAL |
FAULT
val < : severity * severity -> bool
type options
val null_options: options
val make_default_options : unit -> options
datatype error =
ERROR of severity * Location.T * string
val string_error : error -> string
exception Stop of (error * error list)
val error : options -> severity * Location.T * string -> unit
val error' : options -> severity * Location.T * string -> 'a
val default_error' : severity * Location.T * string -> 'a
val wrap : options -> severity * severity * severity * Location.T ->
(options -> 'a -> 'b) -> 'a -> 'b
val with_report_fun :
options -> (error -> unit) -> (options -> 'a -> 'b) -> 'a -> 'b
val listing_fn : options -> int * (TextIO.outstream -> unit) -> unit
val max_num_errors : int ref
val worst_error : options -> severity
end;
