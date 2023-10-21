require "__pre_basis";
require "string";
require "__pre_char";
structure String : STRING =
struct
val maxSize = PreBasis.maxSize
val char_sub = chr o MLWorks.String.ordof
val unsafe_concatenate : string * string -> string =
MLWorks.Internal.Runtime.environment "string concatenate"
structure Char = PreChar
type char = Char.char
type string = string
val size = size
fun sub (s, i) =
let val size = size s in
if i < 0 orelse i >= size then
raise Subscript
else
char_sub(s, i)
end
val substring: string * int * int -> string = substring
val concat: string list -> string = concat
val str: char -> string = str
val explode: string -> char list = explode
val implode: char list -> string = implode
fun compare (x:string,y) =
if x<y then LESS else if x>y then GREATER else EQUAL
fun collate (comp: (char * char) -> order) ((s1,s2):string * string)
: order =
let val s1l = explode s1
val s2l = explode s2
fun aux ([],[]) = EQUAL
| aux ([], _) = LESS
| aux (_, []) = GREATER
| aux (x::xs, y::ys) =
case comp (x,y) of
EQUAL => aux (xs, ys)
| arg => arg
in
aux (s1l, s2l)
end
fun fields (isDelimiter:char -> bool) (s:string) : string list =
let
val size = size s
fun aux (i, j, acc) =
let val size' = i+j in
if (size' = size) then (MLWorks.String.substring (s, i, j) :: acc)
else if isDelimiter (char_sub(s, size')) then
aux (size'+1, 0, MLWorks.String.substring(s, i, j)::acc)
else aux (i, j+1, acc)
end
in
rev(aux (0, 0, []))
end
fun tokens (p:char -> bool) "" = []
| tokens p s =
let
val size = size s
fun mySubstring (s, i, j, acc) =
if j = 0 then acc else MLWorks.String.substring (s, i, j) :: acc
fun skip i =
if i = size then i else if p (char_sub(s, i)) then skip (i+1) else i
fun aux (i, j, acc) =
let
val size' = i+j
in
if (size' = size) then mySubstring (s, i, j, acc)
else if p (char_sub (s, size')) then
aux (skip size', 0, mySubstring(s, i, j, acc))
else
aux (i, j+1, acc)
end
in
rev(aux (0, 0, []))
end
fun isPrefix (s:string) (t:string) =
let
val size_s = size s
in
if size_s > size t then false
else
let fun aux i =
if i < size_s then
char_sub(s,i) = char_sub (t, i) andalso
aux (i+1)
else
true
in
aux 0
end
end
fun extract (s, i, NONE) =
let
val size_s = size s
val size_s' = MLWorks.Internal.Value.cast size_s : word
val i' = MLWorks.Internal.Value.cast i : word
in
if size_s' < i' then
raise Subscript
else
MLWorks.String.substring (s, i, size_s - i)
end
| extract (s, i, SOME n) =
let
val size_s = size s
val size_s' = MLWorks.Internal.Value.cast size_s : word
val i' = MLWorks.Internal.Value.cast i : word
val n' = MLWorks.Internal.Value.cast n : word
in
if size_s' < i' orelse MLWorks.Internal.Value.cast (size_s - i) < n'
then
raise Subscript
else
MLWorks.String.substring (s, i, n)
end
fun map f s =
let
val l = size s
val newS = MLWorks.Internal.Value.alloc_string (l+1)
val i = ref 0
val _ =
while (!i<l) do(
MLWorks.Internal.Value.unsafe_string_update
(newS, !i,
ord (f (chr(MLWorks.Internal.Value.unsafe_string_sub (s,!i)))));
i := !i + 1 )
val _ = MLWorks.Internal.Value.unsafe_string_update (newS, l, 0)
in
newS
end
fun check_slice (s, i, SOME j) =
if i < 0 orelse j < 0 orelse i + j > size s
then raise Subscript
else j
| check_slice (s, i, NONE) =
let
val l = size s
in
if i < 0 orelse i > l
then raise Subscript
else l - i
end
fun mapi f (st, s, l) =
let
val l' = check_slice (st, s, l)
val newS = MLWorks.Internal.Value.alloc_string (l' + 1)
val i = ref 0
val _ =
while (!i<l') do (
MLWorks.Internal.Value.unsafe_string_update
(newS, !i,
ord (f (!i + s,
chr(MLWorks.Internal.Value.unsafe_string_sub(st, !i+s )
)
)
)
) ;
i := !i + 1)
val _ = MLWorks.Internal.Value.unsafe_string_update (newS, l', 0)
in
newS
end
fun translate _ "" = ""
| translate (p:char -> string) (s:string) : string =
let
val size = size s
fun aux (i, acc) =
if i < size then
aux (i+1, (p (char_sub (s, i)))::acc)
else
concat (rev acc)
in
aux (0, [])
end
fun toString s = translate Char.toString s
fun fromString s =
let
val sz = size s
fun getc i =
if i < sz then
SOME (char_sub(s, i), i+1)
else
NONE
fun aux (i, acc) =
if i < sz then
case Char.scan getc i of
SOME (c, i) =>
aux (i, c::acc)
| NONE => acc
else
acc
in
case aux (0, []) of
[] => NONE
| xs => SOME (PreBasis.revImplode xs)
end
fun fromCString "" = NONE
| fromCString s =
let
val sz = size s
fun getc i =
if i < sz then
SOME (char_sub(s, i), i+1)
else
NONE
fun scan (i, acc) =
if i < sz then
case PreChar.scanc getc i of
SOME (c, i) =>
scan (i, c::acc)
| NONE => acc
else
acc
in
case scan (0, []) of
[] => NONE
| xs => SOME (PreBasis.revImplode xs)
end
fun toCString s =
translate Char.toCString s
fun op^(s1, s2) =
if size s1 + size s2 > maxSize then raise Size
else unsafe_concatenate (s1, s2)
val op< : string * string -> bool = op<
val op<= : string * string -> bool = op<=
val op> : string * string -> bool = op>
val op>= : string * string -> bool = op>=
end
;
