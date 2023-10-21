structure Menu =
struct
abstype 'a Menu = menu of string *
string list *
(string , ('a -> 'a)) Assoc.Assoc *
int *
int
with
fun build_menu title items =
let fun make_menu (aslist,itlist) (sel, entry, action) =
(Assoc.assoc_update eq sel action aslist,
snoc itlist (sel^(spaces 3)^entry))
val (acts, its) = foldl make_menu (Assoc.Empty_Assoc,[]) items
in menu(title,its,acts,maximum (map size its),length items)
end
fun no_menu_items (menu(_,_,_,_,l)) = l
fun menu_width (menu(_,_,_,w,_)) = w
fun display_menu side (menu(title,items,actions,maxwidth,menu_length)) =
let val right = if side = Right then spaces (snd (get_window_size ()) div 2 ) else ""
in (write_terminal right ; write_highlighted title; newline (); write_terminal right ;
app ((fn () => write_terminal right) o newline o
(display_in_field Left (maxwidth+2))) items )
end
fun display_menu_two_col (menu(title,items,actions,maxwidth,menu_length)) =
( newline () ; write_highlighted title ; newline () ; left (size title);
let val (mr,mc) = get_window_size ()
val s = maxwidth+2
val half = spaces (mc div 2 - s)
fun pr2 [] = ()
| pr2 (i1::[]) =
(display_in_field Left s i1 ; newline ())
| pr2 (i1::i2::ri) =
(display_in_field Left s i1 ;
write_terminal half ;
display_in_field Left s i2 ;
newline () ; pr2 ri)
in pr2 (items @ ["f   Finish","h   Help"]) end )
fun menu_screen act arg Menu (Title : string) () =
( clear_title Title ; ignore(act arg) ;
display_menu_two_col Menu ;
newline () ; print_line (); newline () ;
prompt1 "Select Option: " )
fun disp_menu_screen act arg Menu (Title : string) () =
( clear_title Title ; ignore(act arg) ;
write_terminal (title_line "(h - help, Control-C - Interrupt)") ; newline () ;
display_menu Right Menu ; prompt1 "" )
fun disp_screen act (Title : string) (Prompt : string) =
( clear_title Title ; ignore(act ()) ; print_line (); newline () ;
prompt_reply Prompt )
fun select_from_menu (menu(title,items,actions,i,j)) arg entry =
let val (action,errmess) = case Assoc.assoc_lookup eq entry actions
of Match f => (f,"")
| NoMatch => (I,"No Menu entry for "^entry)
in action arg
end
fun display_menu_screen DispSort Menu DisplayFn Title HelpEntry currentarg =
( act_on_no_input (
case DispSort of
1 => disp_menu_screen DisplayFn currentarg Menu Title
| 2 => menu_screen DisplayFn currentarg Menu Title
| 3 => (fn () => (display_menu Left Menu ;
prompt1 Title ))
| _ => menu_screen DisplayFn currentarg Menu Title
) ;
let val selection = get_next_chars ()
in (case selection of
"h" => (Help.display_help (if no_current_input ()
then HelpEntry
else get_next_chars ()
);
message_and_wait () ;
display_menu_screen DispSort Menu DisplayFn Title HelpEntry currentarg)
| "f" => currentarg
| "" => (case DispSort of 1 => currentarg
| _ => display_menu_screen DispSort Menu DisplayFn Title HelpEntry currentarg)
| selection => if DispSort = 3
then (select_from_menu Menu currentarg selection)
else display_menu_screen DispSort Menu DisplayFn Title HelpEntry
(select_from_menu Menu currentarg selection))
handle Interrupt => display_menu_screen DispSort Menu DisplayFn Title HelpEntry currentarg
end
)
end
end ;
