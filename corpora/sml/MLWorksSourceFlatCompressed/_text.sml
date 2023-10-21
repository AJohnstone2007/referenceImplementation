require "../basis/__text_io";
require "lists";
require "text";
functor Text (
structure Lists : LISTS
) : TEXT =
struct
datatype T =
concatenate of T * T
| from_list of string list
| from_string of string
local
fun output' (stream, concatenate (left, right)) =
(output' (stream, left); output' (stream, right))
| output' (stream, from_list list) =
Lists.iterate (fn string => TextIO.output (stream, string)) list
| output' (stream, from_string string) =
TextIO.output (stream, string)
in
val output = output'
end
end
;
