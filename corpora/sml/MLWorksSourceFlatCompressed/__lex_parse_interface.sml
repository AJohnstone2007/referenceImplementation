require "lex_parse_interface";
require "$.basis.__int";
structure LexParseInterface : LEX_PARSE_INTERFACE =
struct
type pos = int
val startPos = 1
val pos = ref startPos
val nextPos = fn i => i+1
val printPos = Int.toString
type arg = unit val arg = ()
val error = fn (s, p1, p2) =>
(pos := startPos;
raise Fail (s^" at "^(printPos p1)^", "^(printPos p2)^"\n"))
end
;
