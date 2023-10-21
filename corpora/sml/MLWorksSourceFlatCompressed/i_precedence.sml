functor I_PrecedenceFUN (structure P : PRECEDENCE
structure S : SIGNATURE
sharing type P.OpId = S.O.OpId
and type P.Signature = S.Signature
) : I_PRECEDENCE =
struct
type Signature = S.Signature
type Precedence = P.Precedence
open P S.O
local
fun write_precedence outfn Sigma p =
(app (outfn o (fn s => s^"\n")) (unparse_prec Sigma p);())
in
fun save_precedence outfn Sigma p = (write_precedence outfn Sigma p ;
outfn Lex.end_marker)
val display_precedence = write_precedence write_terminal
end
local
fun direct "~" = EQ
| direct "=" = EQ
| direct "<" = LT
| direct ">" = GT
| direct _ = failwith "i_precedence.sml:direct - this should be guarded against"
fun find_fst_symbol FS (s::ss) cand =
(case find_operator FS (mk_form cand) of
OK f => if member ["<",">","~","="] s then OK (f,ss,direct s)
else find_fst_symbol FS ss (snoc cand s)
| Error _ => find_fst_symbol FS ss (snoc cand s) )
| find_fst_symbol _ [] _ = Error ""
fun change_precedence Prec_fun infn endfn FS P =
let val ss = infn ()
in if endfn (strips ss)
then P
else
let val ssl = Lex.lex_line ss
in if null ssl
then change_precedence Prec_fun infn endfn FS P
else
(case
find_fst_symbol FS ssl []
of OK (f,rss,dir) =>
(case find_operator FS (mk_form rss)
of
OK g => change_precedence Prec_fun infn endfn FS (Prec_fun dir P (f,g))
| Error _ =>
(error_message ("Invalid Precedence Declaration - 2nd Symbol "^ss) ;
change_precedence Prec_fun infn endfn FS P ))
| Error _ =>
(error_message ("Invalid Precedence Declaration - 1st Symbol "^ss) ;
change_precedence Prec_fun infn endfn FS P )
)
end
end
fun add_prec EQ P (f,g) = add_eq_to_prec_order P (f,g)
| add_prec LT P (f,g) = add_to_prec_order P (f,g)
| add_prec GT P (f,g) = add_to_prec_order P (g,f)
fun rem_prec EQ P (f,g) = remove_eq_from_prec_order P (f,g)
| rem_prec LT P (f,g) = remove_from_prec_order P (f,g)
| rem_prec GT P (f,g) = remove_from_prec_order P (g,f)
in
fun load_precedence infn = change_precedence add_prec infn Lex.end_check1
o S.get_operators
val remove_precedence = change_precedence (fn P => (prompt1 "";rem_prec P))
read_line_terminal nl
o S.get_operators
val enter_precedence = change_precedence (fn P => (prompt1 "";add_prec P))
read_line_terminal nl
o S.get_operators
end ;
local
fun Prec_Menu Sigma = Menu.build_menu "Precedence Options"
[
("a", "Add Precedence Declarations",
fn P => (newline (); prompt1 "Enter (f < g)\n" ;
enter_precedence Sigma P) ),
("d", "Delete Precedence Declarations",
fn P => (newline (); prompt1 "Enter (f < g)\n" ;
remove_precedence Sigma P) )
]
in
fun precedence_options A =
Menu.display_menu_screen 1 (Prec_Menu A) (display_precedence A) "PRECEDENCE" "Precedence"
end
end
;
