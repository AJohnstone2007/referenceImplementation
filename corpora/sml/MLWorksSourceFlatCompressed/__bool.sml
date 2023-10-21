require "bool";
require "__pre_string_cvt";
require "__char";
structure Bool : BOOL =
struct
datatype bool = datatype bool
fun not false = true
| not true = false
fun toString true = "true"
| toString false = "false"
fun scan getc cs =
let val cs = PreStringCvt.skipWS getc cs
in
case getc cs of
SOME (c, cs) =>
(case Char.toLower c of
#"t" =>
(case PreStringCvt.splitlNC 3 (Char.contains "rueRUE") (Char.toLower) getc cs of
([#"r", #"u", #"e"], cs) => SOME (true, cs)
| (_, _) => NONE)
| #"f" =>
(case PreStringCvt.splitlNC 4 (Char.contains "alseALSE") (Char.toLower) getc cs of
([#"a", #"l", #"s", #"e"], cs) => SOME (false, cs)
| (_, _) => NONE)
| _ => NONE)
| NONE => NONE
end
fun fromString "" = NONE
| fromString s = PreStringCvt.scanString scan s
end
;
