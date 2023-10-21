functor I_EnvironmentFUN (structure iP : I_PRECEDENCE
structure iW : I_WEIGHTS
structure L : LOCAL_ORDER
structure Str : STRATEGY
structure Env : ENVIRONMENT
structure State : STATE
sharing type Str.Signature = State.Signature =
L.Signature = iP.Signature =
iW.Signature = Env.Signature
and type L.Equality = Str.Equality = Env.Equality
and type L.Order.ORIENTATION = Env.ORIENTATION
and type iP.Precedence = Env.Precedence
and type iW.Weights = Env.Weights
and type Env.Environment = State.Environment
) : I_ENVIRONMENT =
struct
type State = State.State
open Env L Str
local
val dots = nchars "." 15
fun disp n s = fn a => display_in_field Centre n s
fun dise n f = (display_in_field Centre n) o f
fun bool_makestring false = "false"
| bool_makestring true = "true"
in
val environment_table =
seq [fn _ => (clear_title "Current Environment"),
disp 20 "Global Ordering" ,
disp 15 dots ,
dise 10 (fst o get_globord) ,
fn _ => (newline ()) ,
disp 20 "Local Ordering" ,
disp 15 dots ,
dise 10 (fst o get_locord) ,
fn _ => (newline ()) ,
disp 20 "Selection Strategy" ,
disp 15 dots ,
dise 10 (fst o get_locstrat) ,
fn _ => (newline ()) ,
disp 20 "Display Level" ,
disp 15 dots ,
dise 20 (fn _ => makestring(Display_Level.current_display_level ()) ^ "    Sorts : " ^ (if !Display_Level.Show_Sorts then "ON" else "OFF")) ,
fn _ => (newline ()),
disp 20 "Help Directory" ,
disp 15 dots ,
fn _ => (display_in_field Left 40 (!Help.Help_Dir)) ,
fn _ => (newline ()),
disp 20 "Help File" ,
disp 15 dots ,
fn _ => (display_in_field Left 10 (!Help.Help_File)) ,
fn _ => (newline ()),
disp 20 "Log File" ,
disp 15 dots ,
fn _ => (write_terminal (logFile ())) ,
fn _ => (display_in_field Right 10 (bool_makestring (isLogSet ()))) ,
fn _ => (newline ()),
disp 20 "Maximum Rewrites" ,
disp 15 dots ,
fn _ => (display_in_field Left 10 (makestring (Statistics.showRewriteMax ()))) ,
fn _ => (newline ()),
fn _ => (print_line ())]
end
local
val get_string = drop_last o prompt
val LocOrder_Menu = Menu.build_menu "AVAILABLE LOCAL ORDERINGS"
[
("1", "by_size", set_locord ("by_size", by_size_ord ) ),
("2", " manual", set_locord ("manual", manual_ord ) ),
("3", "as_is", set_locord ("as_is", as_is_ord ) )
]
val Strat_Menu = Menu.build_menu "AVAILABLE STRATEGIES"
[
("1", "by_size", set_locstrat ("by_size", by_size_strat )),
("2", "by_age", set_locstrat ("by_age", by_age_strat )),
("3", "manual", set_locstrat ("manual", manual_strat ))
]
open State
fun Environment_Menu get_glob_ord = Menu.build_menu "ENVIRONMENT OPTIONS"
[
("o", "Global Orderings",change_Environment (fn S => set_globord (K (get_glob_ord S)) (get_Environment S))),
("l", "Local Orderings",change_Environment (
Menu.display_menu_screen 3 LocOrder_Menu
I "Pick Number of Ordering >>  " "Local_ordering" o get_Environment)),
("p", "Precedence",change_Environment (fn S => set_precord (iP.precedence_options (get_Signature S)) (get_Environment S) )
),
("w", "Weights", change_Environment (fn S => set_weights (iW.weight_options (get_Signature S)) (get_Environment S))),
("s", "Strategy",change_Environment (
Menu.display_menu_screen 3 Strat_Menu
I "Pick Number of Strategy >>  " "Strategies" o get_Environment)),
("hd", "Change Help Directory",side (fn _ =>
(Help.Help_Dir := (get_string "Enter New Directory >>  ")))),
("d", "Display Level",side (fn _ => (Display_Level.terminal_set_disp_level ()))),
("hf", "Change Help File",side (fn _ => (Help.set_help_file (get_string "Enter New File >>  ")))),
("lg", "Change Log File",side (fn _ => (setLogFile (get_string "Enter New File >>  ")))),
("rw", "Set Maximum Rewrites",
side (fn _ => (case stringtoint (get_string "Enter New Maximum Number >>  ") of
OK n => Statistics.setRewriteMax n
| Error m => Error.error_and_wait m
)))
]
in
fun environment_options get_glob_ord =
Menu.display_menu_screen 2 (Environment_Menu get_glob_ord)
(environment_table o State.get_Environment)
"Environment" "Environment"
end
fun save_environment outfn A env =
let fun out s a = outfn s in
(ignore
(seq
[out "global  " ,
outfn o fst o get_globord,
out "\n",
out "local  ",
outfn o fst o get_locord ,
out "\n",
out "precedence\n",
iP.save_precedence outfn A o get_precord ,
out "weights\n",
iW.save_weights outfn A o get_weights ,
out "strategy  " ,
outfn o fst o get_locstrat ,
out ("\n"^Lex.end_marker) ] env) ;())
end
fun load_environment get_glob_ord infn S =
let
val A = State.get_Signature S
fun load_env env =
let val s = infn ()
in if Lex.end_check1 s
then env
else let val ll = Lex.lex s
in case hd ll of
"precedence" => load_env (set_precord (iP.load_precedence infn A) env)
| "weights" => load_env (set_weights (iW.load_weight infn A) env)
| "global" => load_env (set_globord (K(get_glob_ord (implode (clear_ends (tl ll))))) env)
| "local" => (if null (tl ll)
then (error_message "No Local Ordering declared on the same line.";
wait_on_user () ; load_env env )
else case hd (tl ll)
of
"manual" => load_env (set_locord ("manual", manual_ord ) env )
| "by_size" => load_env (set_locord ("by_size", by_size_ord ) env )
| "as_is" => load_env (set_locord ("as_is", as_is_ord ) env )
| _ => load_env env
)
| "strategy" =>
if null (tl ll)
then (error_message "No Strategy declared on the same line.";
wait_on_user () ; load_env env )
else (case hd (tl ll)
of
"by_size" => load_env (set_locstrat ("by_size", by_size_strat ) env)
| "by_age" => load_env (set_locstrat ("by_age" , by_age_strat ) env)
| _ => load_env env
)
| _ => load_env env
end
end
in
load_env (State.get_Environment S)
end
end
;
