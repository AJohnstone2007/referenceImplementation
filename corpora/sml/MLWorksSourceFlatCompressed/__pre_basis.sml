structure PreBasis =
struct
val maxSize = MLWorks.String.maxLen
local
fun unsafe_alloc_string (n:int) : string =
let val alloc_s = MLWorks.Internal.Value.alloc_string n
in
MLWorks.Internal.Value.unsafe_string_update(alloc_s, n-1, 0);
alloc_s
end
in
fun alloc_string (n:int) : string =
if n > maxSize then
raise Size
else
unsafe_alloc_string n
end
local
fun rev_map f =
let
fun aux(acc, []) = acc
| aux(acc, x :: xs) = aux(f x :: acc, xs)
in
aux
end
in
fun revImplode ([]:char list) : string = ""
| revImplode cs = MLWorks.String.implode_char (rev_map ord ([], cs))
end
fun isSpace c = c = #" " orelse
c >= #"\009" andalso c <= #"\013"
fun isOctDigit c = #"0" <= c andalso c <= #"7"
fun isDigit c = #"0" <= c andalso c <= #"9"
fun isHexDigit c = isDigit c orelse
(#"a" <= c andalso c <= #"f") orelse
(#"A" <= c andalso c <= #"F")
end
;
