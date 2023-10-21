require "$.basis.__text_io";
signature SEARCH =
sig
val searchStream : TextIO.instream * string -> unit
val searchFile : string * string -> bool
val searchInput : unit -> unit
end
;
