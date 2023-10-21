require "search.sml";
require "$.basis.__text_io";
require "$.basis.__string";
structure Search : SEARCH =
struct
fun search' (strm, searchString, pos) =
case TextIO.StreamIO.input1 strm of
NONE => false
| SOME (chr, nextStrm) =>
pos >= size searchString orelse
if chr = String.sub (searchString, pos) then
search' (nextStrm, searchString, pos + 1)
else
false
fun search (strm, searchString) =
case TextIO.StreamIO.input1 strm of
NONE => NONE
| SOME (chr, nextStrm) =>
if search' (strm, searchString, 0) then
SOME strm
else
search (nextStrm, searchString)
fun searchStream (inputStream, searchString) =
let
val funcStream = TextIO.getInstream inputStream
in
case search (funcStream, searchString) of
NONE => ignore (TextIO.inputAll inputStream)
| SOME resultStream => TextIO.setInstream (inputStream, resultStream)
end
fun searchFile (filename, searchString) =
let
val strm = TextIO.openIn filename
val _ = searchStream (strm, searchString)
val matchFound = not (TextIO.endOfStream strm)
val _ = TextIO.closeIn strm
in
matchFound
end
fun searchInput () =
let
fun removeNewline s = String.extract (s, 0, SOME (size s - 1))
val _ = TextIO.print "Enter string to search through:\n"
val inputString = removeNewline (TextIO.inputLine TextIO.stdIn)
val _ = TextIO.print "Enter string to search for:\n"
val searchString = removeNewline (TextIO.inputLine TextIO.stdIn)
val strm = TextIO.openString inputString
val _ = searchStream (strm, searchString)
val resultString = TextIO.inputAll strm
val _ = TextIO.closeIn strm
in
case resultString of
"" => TextIO.print "String not found.\n"
| s => TextIO.print ("String found at:\n" ^ s ^ "\n")
end
end
;
