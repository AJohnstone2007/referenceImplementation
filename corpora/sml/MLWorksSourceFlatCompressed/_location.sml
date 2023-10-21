require "^.basis.__int";
require "^.basis.__char";
require "^.basis.__string";
require "location";
functor Location () : LOCATION =
struct
datatype T =
FILE of string
| LINE of string * int
| POSITION of string * int * int
| EXTENT of {name:string, s_line:int, s_col:int, e_line:int, e_col: int}
| UNKNOWN
val unknown_string = "unknown location"
val unknown_size = size unknown_string
val first_line = 1
val first_col = 1
fun to_string UNKNOWN = unknown_string
| to_string (FILE name) = name
| to_string (LINE (name, line)) =
concat [name, ":", Int.toString line]
| to_string (POSITION (name, line, column)) =
concat [name, ":", Int.toString line, ",", Int.toString column]
| to_string
(EXTENT{name:string, s_line:int, s_col:int, e_line:int, e_col: int}) =
if s_line = e_line andalso (s_col >= e_col - 1) then
concat [name, ":", Int.toString s_line, ",", Int.toString s_col]
else
concat [name, ":", Int.toString s_line, ",",
Int.toString s_col, " to ", Int.toString e_line,
",", Int.toString
(if e_col = first_col then first_col else e_col - 1)]
fun parse_string (delimiters, s) =
let
val sz = size s
fun isPrefix (x,offset, s) =
let val szx = size x
fun scan i =
if i < szx then
String.sub(x, i) = String.sub(s, i+offset) andalso
scan (i+1)
else
true
in
scan 0
end
fun subp (sub,index) =
let
val subsize = size sub
fun aux (index) =
if (index + subsize <= sz) then
if isPrefix (sub, index, s) then
index
else
aux (index+1)
else
~1
in
aux index
end
fun scan (delim::l, index, acc) =
(case subp (delim, index) of
~1 => String.substring(s, index, sz-index) :: acc
| n => scan (l, n+size delim, String.substring(s, index, n-index)::acc))
| scan ([], index, acc) =
if index < sz then
String.substring(s, index, sz-index) :: acc
else acc
in
rev (scan (delimiters, 0, []))
end
exception InvalidLocation
fun extract_components s =
let
val (device,rest) =
if size s >= 3
andalso Char.isAlpha (String.sub (s, 0))
andalso String.sub (s, 1) = #":"
andalso not (Char.isDigit (String.sub (s, 2)))
then
(substring (s, 0, 3), substring (s, 3, size s - 3))
else
("",s)
in
case parse_string ([":", ",", " to ", ","],rest) of
[] => []
| (name::stuff) => device ^ name ::stuff
end
exception InvalidInt
fun getint s =
case Int.fromString s of
SOME n => n
| _ =>
(
raise InvalidInt)
fun from_string s =
if String.isPrefix unknown_string s then
UNKNOWN
else
(case extract_components s of
[name] => FILE name
| [name,line] => LINE(name,getint line)
| [name,line,column] =>
POSITION(name,
getint line,
getint column)
| [name,s_line,s_col,e_line,e_col] =>
EXTENT {name = name,
s_line = getint s_line,
s_col = getint s_col,
e_line = getint e_line,
e_col = getint e_col}
| _ => (
raise InvalidLocation))
handle InvalidInt => (
raise InvalidLocation)
fun combine(EXTENT{name, s_line, s_col, ...}, EXTENT{e_line, e_col, ...}) =
EXTENT{name=name, s_line=s_line, s_col=s_col, e_line=e_line, e_col=e_col}
| combine(t, _) = t
local
fun n_lines (str, 1, pos) = pos
| n_lines (str, n, pos) =
if MLWorks.String.ordof (str, pos) = ord #"\n" then
n_lines (str, n-1, pos+1)
else
n_lines (str, n, pos+1)
handle
MLWorks.String.Ord => size str - 1
in
fun extract (EXTENT {s_line, s_col, e_line, e_col, ...}, str) =
let
val s_pos = n_lines (str, s_line, 0) + s_col - 1;
val e_pos =
if s_line = e_line then
if e_col = s_col then
s_pos + 1
else
s_pos + e_col - s_col
else
n_lines (str, e_line - s_line + 1, s_pos) + e_col - 1
in
(s_pos, e_pos)
end
| extract (POSITION (_, col, line), str) =
let
val pos = n_lines (str, line, 0) + col - 1
in
(pos, pos + 1)
end
| extract (loc,_) =
(
raise InvalidLocation)
end
fun file_of_location(FILE name) = name
| file_of_location(LINE(name, _)) = name
| file_of_location(POSITION(name, _, _)) = name
| file_of_location(EXTENT{name, ...}) = name
| file_of_location UNKNOWN = ""
end
;
