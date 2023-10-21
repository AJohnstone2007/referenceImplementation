require "../basis/__text_io";
require "text";
require "diagnostic";
functor Diagnostic ( structure Text : TEXT ) : DIAGNOSTIC =
struct
structure Text = Text
val output_stream = TextIO.stdOut
val level = ref 0
fun set new_level =
level := new_level
fun iterate f [] = ()
| iterate f list =
let
fun iterate' [] = ()
| iterate' (x::xs) = (ignore(f x); iterate' xs)
in
iterate' list
end
fun output_text message_level message_function =
if !level >= message_level then
(Text.output (output_stream,
message_function (!level - message_level));
print"\n")
else
()
fun output' message_level message_function =
let
in
if !level >= message_level then
(iterate
(fn string => TextIO.output (output_stream, string))
(message_function (!level - message_level));
TextIO.output (output_stream, "\n"))
else
()
end
val output = output'
fun output_fn message_level message_function =
let
in
if !level >= message_level then
message_function (!level - message_level,
output_stream)
else
()
end
end
;
