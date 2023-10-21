require "$.basis.__word";
signature MLWORKS_C_POINTER =
sig
type 'a ptr
type value
val size : Word.word
val ! : value ptr -> value
val := : value ptr * value -> unit
val make : unit -> value ptr
val makeArray : int -> value ptr
val free : value ptr -> unit
val next : value ptr * Word.word -> value ptr
val prev : value ptr * Word.word -> value ptr
end
;
