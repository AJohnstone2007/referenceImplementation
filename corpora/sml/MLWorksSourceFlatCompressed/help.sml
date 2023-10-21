structure HelpLex : LEXICAL =
struct
datatype Token = Id of string | Num of int | NL
local
val Idfst = apply_fst Id
fun get_lexeme (s as c::ss) =
if letter c then first_ident s
else if digit c then first_number s
else (c,ss)
| get_lexeme [] = ("",[])
val takechar = first_string (non (member ["\n","{","}","\\","$","`","'","[","]"]))
val Special = ref false
fun next_token (s as c::ss) =
if !Special then (Special := false; Idfst (get_lexeme s)) else
(case c of
"\n" => (NL,ss)
| "{" => (Id c, ss)
| "}" => (Id c, ss)
| "[" => (Id c, ss)
| "]" => (Id c, ss)
| "\\" => (Special := true ;(Id c, ss))
| "$" => (Id c, ss)
| "`" => (if hd ss = "`" then (Id "\"", tl ss) else (Id c,ss))
| "'" => (if hd ss = "'" then (Id "\"", tl ss) else (Id c,ss))
| _ => Idfst (takechar s) )
| next_token [] = (Id "",[])
fun ff ss =
let val (l,ss) = next_token ss
in if l = Id "" then [] else l::(ff ss)
end
in
fun scan str = ff (explode str)
end
val scan_line = scan
val end_marker = Lex.end_marker
val end_check1 = Lex.end_check1
val end_check2 = Lex.end_check2
val lex = Lex.lex
val lex_line = Lex.lex_line
val lex_input = Lex.lex_input
end;
functor HelpFUN () : HELP
=
struct
val Help_Dir = ref "/users/peril/brian/LOTOS/src/help"
val Help_File = ref "help.tex"
local
structure HelpParse = ParseFUN(HelpLex)
open HelpParse
val indentLength = 6
val indents = ref 0
val indentLabel = ref ""
val helpPrint = ref false
val verbatim = ref false
val helpDone = ref false
val Help_Line_Number = ref 0
exception HelpStop
fun begL toks = $"begin" toks
fun endL toks = $"end" toks
fun snl toks =
(repeat ($" " || $"\t") -- nl >> (fn _ => (inc Help_Line_Number;"\n"))) toks
fun parse_begin key toks =
(begL -- $"{" -- $"erilhelp" -- $"}" -- $"{" -- $ key -- $"}" -- snl
>> (fn _ => helpPrint := true)
) toks
fun parse_end toks =
(endL -- $"{"-- $"erilhelp"-- $"}"-- snl
>> (fn _ => (helpPrint := false; raise HelpStop))
) toks
fun mathmode toks =
( $"\\" -- ($"_" || $"{" || $"}") >> snd
|| $"{" >> (K "")
|| $"}" >> (K "")
|| $"\\" -- ($"em" || $"tt" || $"bf" || $"sc") >> (K "")
|| $"\\" -- ($"merill") >> (K "MERILL")
|| notkey ["$"]
|| num >> makestring
) toks
and idn toks =
( $"$" -- repeat mathmode -- $"$" >> (implode o snd o fst)
|| mathmode
) toks
fun normal_line toks =
( idn -- normal_line >> op^
|| nl >> (fn _ => (inc Help_Line_Number; "\n"))
) toks
fun verb_begin toks =
(begL -- $"{" -- $"verbatim" -- $"}" -- snl >> (fn _ => verbatim := true )
) toks
fun verb_end toks =
(endL -- $"{" -- $"verbatim" -- $"}" -- snl >> (fn _ => verbatim := false )
) toks
fun desc_begin toks =
(begL -- $"{" -- $"description" -- $"}" -- snl >> (fn _ => inc indents )
) toks
fun desc_end toks =
(endL -- $"{" -- $"description" -- $"}" -- snl >> (fn _ => dec indents)
) toks
fun item toks =
($"item" -- $"[" -- id -- $"]" -- snl >> (fn ((((_,_),i),_),_) => indentLabel := i)
) toks
fun indent () = if !indents = 0 then ()
else (display_in_field Left (!indents * indentLength)
(!indentLabel) ; indentLabel := "")
fun ifPrint s = if !helpPrint then
(indent () ; write_terminal s ;
if !Help_Line_Number = (fst( get_window_size () ) - 1 )
then (write_highlighted "  MORE - Hit Return >>" ;
wait_on_user () ;
Help_Line_Number := 0)
else ())
else ()
fun parse_line key toks =
( $"\\" -- (parse_begin key
|| verb_begin
|| parse_end
|| verb_end
|| desc_begin
|| desc_end
|| item ) >> (K ())
|| normal_line >> ifPrint
) toks
fun init () = (indents := 0 ;
indentLabel := "";
helpPrint := false;
verbatim := false;
helpDone := false;
Help_Line_Number := 2 )
fun help_reader key infn =
let val s = infn ()
in if s = ""
then ()
else (reader (parse_line key) s ; help_reader key infn)
handle HelpStop => ()
end
val Keys = ref [] : (string * int) list ref
fun com (s:string,_) (t,_) = s<t
fun key ks infn n =
(case Lex.lex (infn ()) of
["\\","begin","{","erilhelp","}","{", k, "}", "\n"] => key (insert eq ks (k,n)) infn (n+1)
| [] => let val ks = quicksort com ks in (Keys := ks; ks) end
| _ => key ks infn (n+1))
fun get_keys infn =
if null (!Keys)
then key [] infn 1
else !Keys
fun pr maxsize n ks 0 = (newline (); pr maxsize n ks n)
| pr maxsize n [] m = newline ()
| pr maxsize n ((k,_)::ks) m = (display_in_field Left maxsize k ; pr maxsize n ks (m-1))
fun apropos infn =
let val d = (write_highlighted "Help Keys" ; newline ())
val keys = get_keys infn
val maxsize = maximum (map (size o fst) keys) + 2
val n = (snd (get_window_size ())) div maxsize
in pr (maxsize-1) n keys n
end
fun find infn s = filter (C initial_substring s o capitalise o fst) (get_keys infn)
fun skipToLine infn 1 = ()
| skipToLine infn n = (ignore(infn ()) ; skipToLine infn (n-1))
in
fun display_help key =
(let val key = capitalise key
val help_stream = open_in (!Help_Dir ^ "/" ^ !Help_File )
val infn = fn () => read_line help_stream
in if key = "?"
then (apropos infn ; close_in help_stream)
else
case find infn key of
[] => (warning_message ("No help information available for "^key);
close_in help_stream)
|[(s,ln)] => (close_in help_stream ;
let val help_stream = open_in (!Help_Dir ^ "/" ^ !Help_File )
val infn = fn () => read_line help_stream
in (init () ;
newline () ;
write_highlighted s ;
newline () ;
skipToLine infn ln;
help_reader s infn ;
close_in help_stream)
end)
| ss => (warning_message ("Ambiguous key: "^key) ;
write_terminal ("Possible Completions "^"\n");
let val maxsize = (maximum (map (size o fst) ss) + 2)
val n = (snd (get_window_size ())) div maxsize
in pr maxsize n ss n end ;
write_terminal ("Complete : "^key) ;
close_in help_stream;
display_help (capitalise(key^get_next_chars ()))
)
end
handle Io{function=m, ...} => (error_message (m^ "\n Help Failed"); () )
)
fun set_help_file s =
(Keys := [] ; Help_File := s)
end
end
;
