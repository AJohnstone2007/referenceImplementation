require "../main/info";
require "inbuffer";
require "regexp";
signature LEXRULES =
sig
structure RegExp : REGEXP
structure Info : INFO
structure InBuffer : INBUFFER
type options
eqtype Result
val eof : Result
val read_comment: InBuffer.InBuffer * int -> Result
val continue_string:
Info.Location.T * InBuffer.InBuffer * Info.options * int list -> Result
val rules :
(RegExp.RegExp *
((Info.Location.T * InBuffer.InBuffer *
int list * (Info.options * options)) -> Result)
) list
end
;
