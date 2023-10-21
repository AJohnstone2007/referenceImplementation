structure Termcap =
struct
local
val write_terminal = write std_out
val clear = chr 12
val escape = chr 27
val bell = chr 7
val up_sequence = escape ^ "[A"
val down_sequence = escape ^ "[B"
val left_sequence = escape ^ "[D"
val right_sequence = escape ^ "[C"
fun U () = write_terminal up_sequence
fun D () = write_terminal down_sequence
fun L () = write_terminal left_sequence
fun R () = write_terminal right_sequence
val Screen_Size = ref (45, 90)
in
fun clear_screen () = write_terminal clear
fun ring_bell () = write_terminal bell
fun up 0 = (newline ();U ())
| up 1 = U ()
| up n = if n > 0 then (U () ; up (n-1)) else ()
fun down 0 = (newline ();D ())
| down 1 = D ()
| down n = if n > 0 then (D () ; down (n-1)) else ()
fun left 0 = ()
| left n = if n > 0 then (L () ; left (n-1)) else ()
fun right 0 = ()
| right n = if n > 0 then (R () ; right (n-1)) else ()
fun at (x,y) = write_terminal
(escape ^ "[" ^
(makestring (x:int)) ^
";" ^
(makestring (y:int)) ^
"H")
fun set_window_size(ROWS,COLS) = (Screen_Size := (ROWS,COLS);
write_terminal
(escape ^ "[8;" ^
(makestring (ROWS:int)) ^
";" ^
(makestring (COLS:int)) ^
"t" ) )
fun get_window_size () = ! Screen_Size
fun set_icon(ICONFILE) = write_terminal
(escape ^ "]I" ^
ICONFILE ^ escape ^
"\\" )
fun set_window_title(TITLE) = write_terminal
(escape ^ "]l" ^
TITLE ^
escape ^ "\\" )
fun highlight_on () = write_terminal
(escape ^ "[7m")
fun highlight_off () = write_terminal
(escape ^ "[m")
fun clear_line () = write_terminal
(escape ^ "[K")
fun clear_display () = write_terminal
(escape ^ "[J")
end
end ;
