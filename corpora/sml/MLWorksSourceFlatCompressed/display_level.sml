structure Display_Level : sig
val Show_Sorts : bool ref
val silent : int
val full : int
val partial : int
val set_display_level : int -> unit
val display_at_level : int -> (unit -> unit) -> unit
val current_display_level : unit -> int
val terminal_set_disp_level : unit -> unit
end
=
struct
val Show_Sorts = ref false
local
val display_level = ref 0
in
val silent = 2
val partial = 1
val full = 0
fun set_display_level level = display_level := level
fun display_at_level level display_function =
if (!display_level) <= level then display_function ()
else ()
fun current_display_level () = (!display_level)
fun terminal_set_disp_level () =
(write_terminal "Set Display Level :\n" ;
write_terminal "0  Full\n" ;
write_terminal "1  Partial\n" ;
write_terminal "2  Silent\n" ;
write_terminal "s  Toggle Sort display.\n" ;
case prompt_reply "Enter Level Number, or toggle Sort display "
of
"0" => set_display_level full
| "1" => set_display_level partial
| "2" => set_display_level silent
| "s" => Show_Sorts := not (!Show_Sorts)
| _ => ()
)
end
end
;
