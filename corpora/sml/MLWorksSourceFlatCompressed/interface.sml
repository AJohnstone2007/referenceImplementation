structure Interface =
struct
datatype Justify = Left | Right | Centre
fun pad Left n s =
let val sn = size s in
if n < sn then truncate n s
else s^(nchars " " (n-sn))
end
| pad Right n s =
let val sn = size s in
if n < sn then truncate n s
else (nchars " " (n-sn))^s
end
| pad Centre n s =
let val sn = size s in
if n < sn then truncate n s
else let val p = (n - sn)
val padding = nchars " " (p div 2)
in if even p then padding^s^padding
else padding^s^padding^" "
end
end
fun prompt string =
(Termcap.clear_line () ;
write_terminal string ;
get_next_line () )
val Prompt1 = ">>  "
fun prompt1 s = write_terminal (s^Prompt1)
fun prompt_reply s = let val d = prompt1 s in get_next_chars () end
fun prompt_line s = (prompt1 s ; get_next_line ())
fun wait_on_user () = (ignore(read_terminal 1);())
fun message_and_wait () = (ignore(prompt_reply "Press Enter/Return to continue. ") ; ())
fun display_in_field just n s = write_terminal (pad just n s) ;
fun display_two_cols just (t1,l1,t2,l2) =
let val (mr,mc) = get_window_size ()
val maxleft = maximum (map size (t1::l1)) + 2
val maxright = maximum (map size (t2::l2)) + 2
val half = mc div 2
val halfline = spaces half
val right = spaces (half - maxleft)
fun disp2 [] [] = ()
| disp2 [] (r::rs) = (write_terminal halfline ;
display_in_field just maxright r ;
newline () ; disp2 [] rs)
| disp2 (l::ls) [] = (display_in_field just maxleft l ; newline () ; disp2 ls [])
| disp2 (l::ls) (r::rs) = (display_in_field just maxleft l ;
write_terminal right ;
display_in_field just maxright r ;
newline () ; disp2 ls rs)
in disp2 (t1::l1) (t2::l2)
end
fun title_line s =
let val (mr,mc) = get_window_size ()
val sn = size s
val h = mc div 2 - sn div 2
val line = if odd sn then nchars "-" (h - 1)
else nchars "-" h
in if odd sn then line ^ s ^ "-" ^ line else line ^ s ^ line
end
fun clear_title title =
(clear_screen (); write_terminal (title_line title ^ "\n"))
fun print_line () = write_terminal (title_line "")
fun act_on_no_input f = if no_current_input () then f () else ()
fun act_and_get f = if no_current_input () then f () else get_next_chars ()
fun act_with_message s = act_and_get (write_terminal s;get_next_chars)
fun confirm s =
(write_terminal (s^" (y/n) ? ");
let val reply = (hd (explode (read_line std_in))
handle Hd => "")
in reply = "y" orelse reply = "Y"
end)
fun read_n_times 0 s = []
| read_n_times n s = (write_terminal s ;
case (stringtoint o drop_last o read_line_terminal) () of
OK m => m :: read_n_times (n - 1) s
| Error _ => (write_terminal "Only enter integers\n" ;
read_n_times n s)
) ;
end ;
