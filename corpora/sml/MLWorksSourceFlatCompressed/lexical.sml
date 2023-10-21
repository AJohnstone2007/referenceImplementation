structure Lex : LEXICAL =
struct
datatype Token = Id of string | Num of int | NL
local
fun next_lexeme (c::ss) =
if c = "\n" then (c,ss) else
if space c then next_lexeme ss else
if letter c orelse c = "_" then first_ident (c::ss) else
if digit c then first_number (c::ss) else
if symbolic c then (c,ss) else
if c = "\"" then first_quoted ss
else (c,ss)
| next_lexeme [] = ("",[])
fun ff ss =
let val (l,ss) = next_lexeme ss
in if l = "" then [] else l::(ff ss)
end
in
fun lex str = ff (explode str)
end
fun lex_line s = let val ls = lex s
in if non null ls andalso lastone ls = "\n"
then front ls
else ls
end
val lex_input = lex o get_next_line
local
fun next_token (c::ss) =
if c = "\n" then (NL,ss) else
if space c then next_token ss else
if letter c orelse c = "_" then apply_fst Id (first_ident (c::ss)) else
if digit c then apply_fst (Num o (fn s => (case stringtoint s of
OK n => n |
Error m => raise (MERILL_ERROR m))) )
(first_number (c::ss)) else
if symbolic c then (Id c,ss) else
if c = "\"" then apply_fst Id (first_quoted ss)
else (Id c,ss)
| next_token [] = (Id "",[])
fun ff ss =
let val (l,ss) = next_token ss
in if l = Id "" then [] else l::(ff ss)
end
in
fun scan str = ff (explode str)
end
fun scan_line s = let val ls = scan s
in if non null ls andalso lastone ls = NL
then front ls
else ls
end
val end_marker = ".\n"
val end_check1 = ou (eq end_marker) (eq "")
val end_check2 = ou (ou (eq ["\n"]) null) (eq (lex end_marker))
end
;
