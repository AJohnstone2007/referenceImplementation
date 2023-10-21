functor Eq_OptionsFUN (structure T : TERM
structure Eq : EQUALITY
structure Es : EQUALITYSET
structure En : ENVIRONMENT
structure iT : I_TERM
structure iE : I_EQUALITY
structure EU : UNIFY
structure S : SUBSTITUTION
structure Str : STRATEGY
structure ER : REWRITE
structure ECP : CRITICALPAIR
structure K : KB
structure H : KB
structure P : KB
structure State : STATE
sharing type T.Sig.Signature = Es.Signature = En.Signature =
Eq.Signature = iT.Signature = EU.Signature =
iE.Signature = S.Signature = ER.Signature =
ECP.Signature = State.Signature = Str.Signature
and type T.Term = Es.Term = Eq.Term = ER.Term = State.Term =
iT.Term = EU.Term = iE.Term
and type S.Substitution = EU.Substitution = Eq.Substitution
and type T.Sig.O.OpId = T.OpId
and type T.Sig.V.Variable = T.Variable = iT.Variable
and type T.Sig.V.Variable_Print_Env = S.Variable_Print_Env
and type Eq.Equality = Es.Equality =
En.Equality = iE.Equality = Str.Equality =
ER.Equality = ECP.Equality
and type Es.EqualitySet = K.EqualitySet = H.EqualitySet =
P.EqualitySet = State.EqualitySet =
iE.EqualitySet = Str.EqualitySet =
ER.EqualitySet = ECP.EqualitySet
and type En.Environment = State.Environment
and type En.ORIENTATION = Eq.ORIENTATION = iE.ORIENTATION
and type State.State = K.State = H.State = P.State
) : EQ_OPTIONS =
struct
type State = State.State
structure Ops = T.Sig.O
structure Vars = T.Sig.V
open Es T En Eq iT iE S Str
open State
fun strat e a = snd (get_locstrat e) a
local
val Eqset_Menu = Menu.build_menu "Equality Set Options"
[
("a", "Add Equations",
fn (A,T,E,Ev) => (A,T,
(enter_equality_set A T (strat Ev A) E),
Ev)
),
("d", "Delete Equations",
fn (A,T,E,Ev) => (A,T,delete_from_equality_set E,Ev)
),
("o", "Orient Equations",
fn (A,T,E,Ev) =>
let val (Es',env) = orient_select A (strat Ev A) Ev
(snd (get_globord Ev) A) E
in (A,T,Es',env)
end
)
]
fun equation_table eqslist =
let val d = (display_in_field Centre 6 "Label" ;
display_in_field Centre 25 "Title" ;
display_in_field Right 7 "Current" ;
display_in_field Right 7 "Total" ;
newline () )
fun deq eqs =
(display_in_field Centre 6 (get_label eqs) ;
display_in_field Centre 25 (get_name eqs) ;
display_in_field Right 7 (eq_set_size eqs) ;
display_in_field Right 7 (makestring (total_entered_in_eqset eqs)) ;
newline () )
in (app deq eqslist ; print_line (); newline () )
end
fun eqset_options A T E Ev =
let val (_,_,E,Ev) = Menu.display_menu_screen 1 Eqset_Menu
(fn (A,T,E,Ev) =>
(write_terminal (eq_set_size E^" Equalities\n");
display_equality_set A E ;(A,T,E,Ev)))
(get_name E) "Equalities"
(A,T,E,Ev)
in (E,Ev)
end
datatype SelectMode = Copy | Move
fun select_from_user A T =
(prompt1 "Enter Equation: ";
let val ss = Lex.lex_input ()
in if nl (hd ss)
then []
else case parse_equality A T ss
of OK e => e :: select_from_user A T
| Error m => (error_message m ; select_from_user A T )
end)
fun select_from_eq_set selectmode Eqs es =
let val s = act_and_get (fn () => prompt_reply "Pick Equation Number (or \"all\") " )
in if s = ""
then (es,Eqs)
else case stringtoint s
of OK n => (case select_by_number Eqs n
of OK e => select_from_eq_set selectmode
(case selectmode
of Move => delete_by_number Eqs n
| Copy => Eqs )
(snoc es e)
| Error m => (error_message m ;
select_from_eq_set selectmode Eqs es )
)
| Error _ => if s = "all"
then (get_equalities Eqs,
(case selectmode
of Move => clear_equality_set Eqs
| Copy => Eqs ))
else (error_message (s^" not an integer") ;
select_from_eq_set selectmode Eqs es )
end
fun select_equation selectmode (A,T,Es) =
(case act_with_message ("Equation Set or \"user\": "^Prompt1) of
"user" => (OK (select_from_user A T), Es )
| "" => (Error "", Es)
| s => (case get_by_label Es s of
OK Eqs => (act_on_no_input (fn () => (clear_title (get_name Eqs) ;
write_terminal (eq_set_size Eqs ^ " Equalities\n") ;
display_equality_set A Eqs;
print_line () ));
let val (es,Eqs') = select_from_eq_set selectmode Eqs []
in (OK es, case selectmode
of Move => change_by_label Es s Eqs'
| Copy => Es )
end)
| Error m => (Error m,Es) )
)
fun get_from_label s Es =
(case get_by_label Es s of
OK e => e
| Error _ => (error_message
("Non-existent Equality Set "^s^" Specified"); EmptyEqSet)
)
local
fun ins A = insert_by_strat A by_age_strat
fun equations_from_user A T eqset =
let val estring = (prompt1 "Enter Equation : ";
read_line_terminal ())
in if estring = "" orelse nl estring
then eqset
else case parse_equality A T (Lex.lex estring) of
OK e => equations_from_user A T (ins A eqset (mk_conjecture (lhs e) (rhs e)))
| Error m => (error_message m ;
equations_from_user A T eqset)
end
fun equations_from_set A Eqs eqset =
let val s = prompt_reply "Pick Equation Number or \"all\": "
in if nl s then eqset
else if s = "all" then Eqs
else equations_from_set A Eqs
(givefM (fn () => equations_from_set A Eqs eqset)
((stringtoint s) propM (select_by_number Eqs) propM (returnM o ins A eqset)))
end
in
fun select_conjectures (A,T,Es) =
(case act_with_message ("Equation Set or \"user\": "^Prompt1) of
"user" => equations_from_user A T EmptyEqSet
| "" => EmptyEqSet
| s => (case get_by_label Es s of
OK Eqs => let val s = (clear_title (get_name Eqs) ;
write_terminal (eq_set_size Eqs ^ " Equalities\n") ;
display_equality_set A Eqs;
print_line () )
in equations_from_set A Eqs EmptyEqSet
end
| Error m => (error_message "Non-existent Equality Set " ;
select_conjectures (A,T,Es))
)
)
end
fun copy_equation (A,T,Es,En) =
(write_terminal "Copying an Equalities from one set to another.\n";
write_terminal "Pick Equations from ";
case select_equation Copy (A,T,Es) of
(OK e1,_) =>
(let val lab = prompt_reply "Equality Set to Copy to ";
in if lab = "" then Es
else (case get_by_label Es lab of
OK e => change_by_label Es lab
(foldl (eqinsert (strat En A)) e e1)
| Error _ => (error_message ("Non-existent Equality Set "^lab^" Specified"); Es)
)
end )
| (Error m,_) => (error_message ("no valid 1st equation "^m); Es) )
fun move_equation (A,T,Es,En) =
(write_terminal "Moving Equalities from one set to another.\n";
write_terminal "Pick Equations from ";
case select_equation Move (A,T,Es) of
(OK e1,Es') => (let val lab = prompt_reply "Equality Set to Move to ";
in if lab = "" then Es
else (case get_by_label Es' lab of
OK e => change_by_label Es' lab (foldl (eqinsert (strat En A)) e e1)
| Error _ => (error_message ("Non-existent Equality Set "^lab^" Specified"); Es)
)
end )
| (Error m,_) => (error_message ("no valid 1st equation "^m); Es) )
fun show_normalising A E n t () =
if n <> 0 andalso
n mod (fst (get_window_size ()) -1) = 0 andalso
confirm "Do you want to stop rewriting"
then ()
else
let val (b,t') = ER.norm_once A t E
in if b
then (write_terminal (makestring (n+1:int) ^ " ---> " ^ show_term A t' ^"\n") ;
show_normalising A E (n+1) t' ())
else (prompt1 "Reduces To "; display_term A t; newline ();
write_terminal ("After "^ makestring n ^" Rewrites.") ; newline () )
end
fun norm_term (A,T,Es) =
let val en = (Statistics.reset_part_statistics () ;
prompt_reply "Rewriting by C/AC Rewriting.\nEnter Set of Equalities to Reduce by ")
val eqs = get_from_label en Es
in case (prompt1 "Enter Term "; enter_term A T Assoc.Empty_Assoc) of
Match (t,_) => (Timer.timer (show_normalising A eqs 0 t);
newline (); norm_term (A,T,Es))
| NoMatch => ()
end
fun printeq A = write_terminal o (unparse_equality A)
local
fun normaliser A e equ =
let val d = (Statistics.reset_part_statistics () ;
write_terminal "Reducing by C/AC Rewriting: ";
printeq A equ ; newline ())
val redeq = Timer.timer (fn () => ER.normaliseEquality A [e] equ)
in (if identity redeq
then write_terminal "Reduces to an Identity : "
else printequality A "Reduces to : " redeq ;
newline ();
write_terminal ("After "^
makestring (!Statistics.Part_Match_Success) ^
" Rewrites.") ;
newline ();
newline ();
redeq)
end
in
fun norm_equation (A,T,Es) =
let val s = (prompt_reply "Enter Set of Equalities to Reduce by ")
in case get_by_label Es s of
OK e => (case select_equation Copy (A,T,Es) of
(OK equ,_) => OK (map (normaliser A e) equ)
| (Error "",_) => errM
| (Error m,_) => (error_message m ; errM)
)
| Error _ => (error_message
("Non-existent Equality Set "^s^" Specified"); errM)
end
end
local
fun all_cps A En Eqs [] _ [] _ = EmptyEqSet
| all_cps A En Eqs (e1::e1s) n (e2::e2s) m =
(write_terminal "Generating Critical Pairs between\n";
printequality A "" e1 ;
printequality A "" e2 ;
let val order = strat En A
val cps = ECP.cpg A order EmptyEqSet "" e1 EmptyEqSet (m,e2)
in (write_terminal "Critical Pairs\n";
display_equality_set A cps ;
merge_eqsets order cps (all_cps A En Eqs (e1::e1s) n e2s (m+1)))
end)
| all_cps A En Eqs (e1::e1s) n [] _ = all_cps A En Eqs e1s (n+1) Eqs 1
| all_cps A En Eqs [] _ (e2::e2s) _ = EmptyEqSet
in
fun all_critical_pairs (A,T,Es,En) =
(write_terminal "Generating Critical Pairs between equations.\n";
write_terminal "Pick Equations from ";
case select_equation Copy (A,T,Es) of
(OK e1,_) => (write_terminal "Pick Equations from ";
case select_equation Copy (A,T,Es) of
(OK e2,_) =>
let val cps = Timer.timer (fn () => all_cps A En e2 e1 1 e2 1 )
val lab = (act_and_get (fn () => (
write_terminal "Do you wish to keep these Critical Pairs ?\n";
prompt_reply "If so, Enter Label of Equality of Set.\n")))
in
if lab = "" then Es
else (case get_by_label Es lab of
OK e => change_by_label Es lab (merge_eqsets (strat En A) e cps)
| Error _ => (error_message ("Non-existent Equality Set "^lab^" Specified"); Es)
)
end
| (Error m,_) => (error_message ("no valid 2nd equations "^m); Es) )
| (Error m,_) => (error_message ("no valid 1st equations "^m); Es) )
end
fun unify_terms (Sigma, T) =
(write_terminal ("Unifying Two Terms using Associative-Commutative and \nCommutative Order-Sorted Unification.\n");
case (prompt1 "Enter First Term: "; enter_term Sigma T Assoc.Empty_Assoc) of
Match (T1,_) =>
(case (prompt1 "Enter Second Term: "; enter_term Sigma T Assoc.Empty_Assoc) of
Match (T2,_) =>
let val U_timer = Timer.start_timer ()
val unifs = (Statistics.inc_unify_attempts () ;
EU.unify Sigma T1 T2)
val non_gc_time = Timer.check_timer U_timer
val gc_time = Timer.check_timer_gc U_timer
val total = Timer.add_time(non_gc_time,gc_time)
val (s1,e) = unparse_term Sigma Vars.Empty_Var_Print_Env T1
val (s2,vpe) = unparse_term Sigma e T2
val cu = fn () => Statistics.inc_unify_success ()
in if null unifs
then write_terminal ("\nNo Unifiers found for "^s1^" and "^s2^" .\n")
else
(write_terminal ("\nUnifiers for "^s1^" and "^s2^":\n");
app (cu o write_terminal o noc"\n" o (show_subs_context Sigma vpe)) unifs ;
write_terminal ("Number of Unifiers : "^makestring (length unifs)^"\n");
write_terminal ("\nExecution Time: " ^ (Timer.show_time non_gc_time) ^
"\nGarbage Collecting Time: " ^ (Timer.show_time gc_time) ^
"\nTotal Time: " ^ (Timer.show_time total) ^ "\n");
() )
end
| NoMatch => ()
)
| NoMatch => () ;
case (prompt_reply "Finished (y/n) ?  " )
of
"y" => ()
| "Y" => ()
| _ => unify_terms (Sigma, T)
)
fun decompose S = (get_Signature S , get_Parser S , get_Equalities S , get_Environment S)
fun call_completion comp s S =
let val (A,T,Es,En) = (write_terminal (title_line s ^ "\n"); decompose S)
in
givefM (K S o message_and_wait)
(get_by_label Es (prompt_reply "Enter Set of Equations ") propM (fn E =>
get_by_label Es (prompt_reply "Enter Set of Rules ") eachM (fn R =>
let val ((E',R',H'),S') = Timer.timer (fn () => comp S (confirm "By Steps") (E,R)
(if confirm "Test Conjectures"
then select_conjectures (A,T,Es)
else EmptyEqSet) )
in (message_and_wait ();
change_Equalities (K (change_on_label (change_on_label Es E') R')) S' )
end )))
end
fun gotoEqSet S =
( act_on_no_input (fn () => write_terminal "Select Equality Set by Label. \nLabel");
let val (A,T,Es,En) = decompose S
val s = prompt_reply ""
in case get_by_label Es s of
OK E => let val (Es',En') = eqset_options A T E En
in change_Equalities (K
(case new_labES (remove_by_label Es s) Es' of
OK e => e
| Error _ =>
(error_and_wait ("two sets labelled with "^s) ; Es)
) )
(change_Environment (K En') S )
end
| Error _ => (error_and_wait
("Non-existent Equality Set "^s^" Specified"); S)
end )
val Equations_Menu = Menu.build_menu "Equality Options"
[
("g", "Goto Equality Set", gotoEqSet ),
("n", "Normalise a Term",fn S => let val (A,T,Es,_) = decompose S
in (norm_term (A,T,Es) ; S)
end ) ,
("a", "Add a New Equality Set",fn S =>
let val lab = prompt_reply "Enter New Label "
val name = (prompt1 "Enter New Name " ;
drop_last (get_next_line ()))
in if lab = "" orelse nl lab
then (error_and_wait "No Valid Label for Equality Set" ; S)
else change_Equalities (fn S => let val Es = get_Equalities S in
case new_labES Es (new_equality_set lab name)
of OK EE => EE
| Error m => (error_and_wait m; Es)
end) S
end ) ,
("r", "Normalise an Equation",fn S => let val (A,T,Es,_) = decompose S
in (ignore(norm_equation (A,T,Es)) ; message_and_wait (); S)
end ),
("d", "Delete an Equality Set", fn S =>
change_Equalities (C remove_by_label (prompt_reply "Enter Label ") o get_Equalities) S),
("p", "Critical Pairs",change_Equalities (all_critical_pairs o decompose)),
("m", "Move Equations",change_Equalities (move_equation o decompose)),
("k", K.CompName,call_completion K.complete K.CompName ),
("c", "Copy Equations",change_Equalities (copy_equation o decompose)),
("H", H.CompName,call_completion H.complete H.CompName),
("P", P.CompName,call_completion P.complete P.CompName),
("u", "Unify Terms",fn S => let val (A,T,Es,_) = decompose S
in (unify_terms (A,T) ; S)
end )
]
in
val equation_options =
Menu.display_menu_screen 2 Equations_Menu
(equation_table o get_Equalities) "Equality Sets"
"Equalitysets"
end
end
;
