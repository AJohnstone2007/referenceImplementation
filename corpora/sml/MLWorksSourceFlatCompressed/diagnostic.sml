require "../basis/__text_io";
require "text";
signature DIAGNOSTIC =
sig
structure Text : TEXT
val set : int -> unit
val output : int -> (int -> string list) -> unit
val output_text : int -> (int -> Text.T) -> unit
val output_fn : int -> (int * TextIO.outstream -> unit) -> unit
end
;
