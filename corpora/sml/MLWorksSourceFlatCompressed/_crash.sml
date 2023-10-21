require "../main/info";
require "crash";
functor Crash (structure Info : INFO) : CRASH =
struct
exception Impossible of string
fun impossible message =
Info.default_error'
(Info.FAULT, Info.Location.UNKNOWN, message)
fun unimplemented message =
Info.default_error'
( Info.FAULT,
Info.Location.UNKNOWN,
"Unimplemented facility: " ^ message
)
end;
