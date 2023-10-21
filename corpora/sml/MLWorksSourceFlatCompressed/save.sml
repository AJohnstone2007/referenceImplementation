functor SaveFUN (structure iS : I_SORT
structure iO : I_OPSYMB
structure iV : I_VARIABLE
structure iE : I_EQUALITY
structure iP : I_PRECEDENCE
structure Sig : SIGNATURE
structure Es : EQUALITYSET
structure iEn : I_ENVIRONMENT
structure State : STATE
sharing type Sig.S.Sort = Sig.V.Sort = Sig.O.Sort =
iS.Sort = iO.Sort
and type Sig.S.Sort_Store = Sig.V.Sort_Store =
iS.Sort_Store = iO.Sort_Store = iV.Sort_Store
and type Sig.O.OpId = iO.OpId
and type Sig.O.Op_Store = iO.Op_Store
and type Sig.V.Variable_Store = iV.Variable_Store
and type Sig.Signature = iE.Signature =
State.Signature = iEn.Signature
and type Es.EqualitySet = iE.EqualitySet =
State.EqualitySet
and type iEn.Environment = State.Environment
and type State.State = iEn.State
) : SAVE =
struct
type Signature = Sig.Signature
type EqualitySet = Es.EqualitySet
type State = State.State
open iS iO iV iE iP Sig Es iEn
fun open_save_file () =
let val file_name = prompt_reply "Enter Name of File to Write to: "
in
if file_name = "" then Error "" else
OK (open_out file_name)
handle io_failure => Error ("Unable to open output file "^file_name)
end ;
fun get_title outfn =
(prompt1 "Enter title of file ";
outfn ("# " ^(read_line_terminal ())));
local
fun save_all_equalities outfn A (es::ES) =
(outfn (get_label es ^"   " ^ get_name es ^"\n") ;
save_equality_set outfn A es ;
save_all_equalities outfn A ES )
| save_all_equalities outfn A [] = outfn Lex.end_marker
fun save_all_signature outfn A = ( outfn "sorts\n";
save_sorts outfn (get_sorts A) ;
outfn "sort_ordering\n";
save_sort_ordering outfn (get_sorts A) ;
outfn "opns\n";
save_operators outfn (get_operators A) ;
outfn "vars\n";
save_variables outfn (get_variables A) ;
outfn Lex.end_marker )
in
fun save_signature A =
(case open_save_file () of
OK os => let val outfn = write os
in (get_title outfn ;
outfn "signature\n" ;
save_all_signature outfn A ;
close_out os)
end |
Error "" => () |
Error m => (error_message m ; save_signature A))
fun save_equalities (A,Es) =
(case act_with_message "Select Equality Set or \"all\" to save\n>>  "
of
"all" => (case open_save_file () of
OK os => let val outfn = write os
in (get_title outfn;
outfn "eqns\n";
save_all_equalities outfn A Es ;
outfn Lex.end_marker;
close_out os)
end |
Error "" => () |
Error m => (error_message m; save_equalities (A,Es)))
| "" => ()
| s => (case get_by_label Es s of
OK E =>
(case open_save_file () of
OK os => let val outfn = write os
in (get_title outfn;
outfn "eqns\n";
outfn (get_label E ^"   " ^ get_name E ^"\n") ;
save_equality_set outfn A E ;
outfn Lex.end_marker;
close_out os)
end
| Error "" => ()
| Error m => (error_message m; save_equalities (A,Es)))
| Error _ => (error_and_wait
("Non-existent Equality Set "^s^" Specified");
save_equalities (A,Es))
)
)
fun save_state S =
let val A = State.get_Signature S
val Es = State.get_Equalities S
val En = State.get_Environment S
in case open_save_file () of
OK os => let val outfn = write os
in (get_title outfn;
outfn "signature\n";
save_all_signature outfn A ;
outfn "env\n" ;
save_environment outfn A En;
outfn "eqns\n";
save_all_equalities outfn A Es ;
close_out os)
end |
Error "" => () |
Error m => (error_message m ; save_state S)
end
end
local
val Save_Menu = Menu.build_menu "Save Options"
[
("a", "Save Signature", side (save_signature o State.get_Signature)),
("e", "Save Equality Sets", side (
save_equalities o tee (State.get_Signature, State.get_Equalities))),
("s", "Save State", side save_state)
]
in
val save_options = Menu.display_menu_screen 2 Save_Menu I "Save Options" "Save"
end
end
;
