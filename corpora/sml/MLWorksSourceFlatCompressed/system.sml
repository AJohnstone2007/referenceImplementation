functor SystemFUN (structure EO : EQ_OPTIONS
structure iSig : I_SIGNATURE
structure En : I_ENVIRONMENT
structure Ord : ORDERINGS
structure State : STATE
structure L : LOAD
structure S : SAVE
sharing type En.Signature = Ord.Signature = S.Signature
and type S.EqualitySet = State.EqualitySet
and type En.Environment = State.Environment = Ord.Environment
and type En.ORIENTATION = Ord.ORIENTATION
and type En.Equality = Ord.Equality
and type State.State = EO.State = L.State =
En.State = iSig.State = S.State
) : ERILSYSTEM =
struct
open EO iSig En L S State
type State = State.State
val Author = "Brian Matthews "
val Institution = "Glasgow University and Rutherford Appleton Laboratory "
val SysName = "MERILL "
val SysVersion = "v.0.5 "
val SysDescription = "An Equational Reasoning System in Standard ML. "
val Year = "1993 "
local
fun title_banner x =
((app (newline o display_in_field Centre (snd (get_window_size () )))
[ SysName^SysVersion , "",
SysDescription, "",
Author, "",
Institution^Year, ""
]
);
print_line ();
newline ()
)
val Initial_Basis = State.Initial_State
val Eril_Menu = Menu.build_menu "TOP LEVEL OPTIONS"
[
("a", "Signature", signature_options ),
("s", "Save", save_options ),
("n", "Environment", environment_options Ord.globalord_options),
("l", "Load", load_options),
("e", "Equalities", equation_options),
("c", "Clear System",
(fn S =>
if confirm "Do You Really Want to Clear the Whole System"
then (Display_Level.set_display_level Display_Level.partial ; Initial_Basis)
else S) ),
("S", "Statistics",Statistics.display_total_statistics )
]
val main_loop =
Menu.display_menu_screen 2 Eril_Menu title_banner "" "Top_level"
exception Last_State of State
val bye = "Exiting MERILL session.\n\n   Bye. \n\n";
fun call_main_loop state =
let val final = main_loop state
handle x => (system_error ("Uncaught Exception \"" ^ System.exn_name x ^
"\" Raised - Return to Top Level");
wait_on_user (); call_main_loop state)
in if confirm "Do You Really Want to Quit"
then (write_terminal bye; raise Last_State final)
else call_main_loop final
end
val gc = ref 0
in
fun eril_restart start =
(
gc := 0 ;
flush_input ();
Display_Level.set_display_level Display_Level.partial ;
let val final = call_main_loop start
handle Last_State final => final
in (gc := 1; final)
end
)
fun MERILL () = (
gc := 0 ;
Termcap.set_window_size(get_window_size ());
flush_input ();
Display_Level.set_display_level Display_Level.partial ;
Display_Level.Show_Sorts := false ;
Statistics.reset_total_statistics () ;
Statistics.start_system_timer () ;
Statistics.setRewriteMax 1000 ;
CommonIO.noLog () ;
let val final = call_main_loop Initial_Basis
handle Last_State final => final
in (gc := 1 ; final)
end
)
end
fun save_eril_image () =
(exportML "merill.x" ;
write_terminal (stringwith ("\n","\n","\n")
[SysName^SysDescription,Author^Institution^Year,"",
"\n To run call MERILL () ; \n"]
))
fun make_eril_exec () =
if confirm "Do You Really Want to Save the Executable and Close SML"
then (write_terminal "Saving executable and closing SML";
exportFn ("runmerill",fn (l,m) => (ignore(MERILL ()); ())))
else ()
end ;
