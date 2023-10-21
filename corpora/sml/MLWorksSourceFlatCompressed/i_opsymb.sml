functor I_OpsymbFUN (structure T : TERM
structure iS : I_SORT
sharing type T.Sort = T.Sig.S.Sort = T.Sig.O.Sort = iS.Sort
and type T.Sig.S.Sort_Store = iS.Sort_Store
and type T.OpId = T.Sig.O.OpId
sharing T.Pretty = T.Sig.O.Pretty
) : I_OPSYMB =
struct
structure Pretty = T.Pretty
type OpId = T.Sig.O.OpId
type OpSig = T.Sig.O.OpSig
type Sort = T.Sig.S.Sort
type Term = T.Term
type Sort_Store = T.Sig.S.Sort_Store
type Op_Store = T.Sig.O.Op_Store
open T T.Sig.O T.Sig.S
local
datatype Attr = ASSOC | COMM | GEN
fun show_attr GEN = "GEN"
| show_attr COMM = "COMM"
| show_attr ASSOC = "ASSOC"
local
open Parse
in
fun id toks = (notkey ["(",")",":"] || num >> makestring) toks
fun operator_parser ss s =
let
val sortp = iS.sort_parser ss
fun make_dec (((sl,_),ssl),at) = (sl,ssl,at)
fun operator_dec_line toks =
( drop_errors (operator_dec -- $":")
-- signature_dec -- opt_attr >> make_dec
) toks
and operator_dec toks =
change_errors "Operator form element or \"_\" expected, \":\" found"
( form -- repeat form >> op::
) toks
and form toks =
( $"_" >> K ""
|| $"("
|| $")"
|| id
) toks
and signature_dec toks =
( drop_errors (sort_list -- symbol "->") -- sortp >> apply_fst #1
|| drop_errors sortp >> pair []
) toks
and sort_list toks =
( repeat sortp
) toks
and opt_attr toks =
( $"(" -- attr_list -- $")" >> (#2 o #1)
|| drop_errors empty >> K []
) toks
and attr_list toks =
( repeat attr >> foldl (insert eq) []
) toks
and attr toks =
change_errors
(case toks of
Lex.Id s :: _ => ("Attribute expected, \""^s^"\" found.")
| Lex.Num s :: _ => ("Attribute expected, \""^makestring s^"\" found.")
| Lex.NL :: _ => ("Attribute expected, Newline found.")
| [] => ("Attribute expected, - found end of input.")
)
( $"GEN" >> K GEN
|| $"ASSOC" >> K ASSOC
|| $"COMM" >> K COMM
) toks
in
(reader operator_dec_line s)
handle (SynError m) => (error_message m ; raise (SynError m))
end
end
fun generate 0 = []
| generate 1 = [""]
| generate n = ["",","] @ generate (pred n)
fun well_declared_operator (form,(si,s)) =
let val unders = eq (occurences (eq "") form)
in if unders 0 orelse unders (length si) then true else false
end
fun mk_pretty (s::ss) (p::ps) =
if s = ""
then (if Pretty.is_single p
then Pretty.blo (4 ,[p,Pretty.brk (if length ss = 0 then 0 else 1)])
else Pretty.blo (4 , [Pretty.str "(",p,Pretty.str ")",Pretty.brk (if length ss = 0 then 0 else 1)])
) :: mk_pretty ss ps
else if not (null ss) andalso symbolic (hd ss)
then Pretty.blo (2 , [Pretty.str s]) :: mk_pretty ss (p::ps)
else Pretty.blo (2 , [Pretty.str s, Pretty.brk 1]) :: mk_pretty ss (p::ps)
| mk_pretty [s] [] = [Pretty.blo (2 , [Pretty.str s])]
| mk_pretty (s::ss) [] = Pretty.blo (2 , [Pretty.str s, Pretty.brk (if length ss = 0 then 0 else 1)]) :: mk_pretty ss []
| mk_pretty [] (p::ps) =
(if Pretty.is_single p then Pretty.blo (4 ,[p,Pretty.brk (if length ps = 0 then 0 else 1)])
else Pretty.blo (4 , [Pretty.str "(",p,Pretty.str ")",Pretty.brk (if length ps = 0 then 0 else 1)])
) :: mk_pretty [] ps
| mk_pretty [] [] = []
fun modify_form form (si,s) =
if occurences (eq "") form = 0 andalso length si <> 0
then let val i = length si in
(form @ ["("] @ generate i @ [")"],
fn ps => Pretty.blo (4, (map Pretty.str form) @ [Pretty.str "("] @
interleave3 ps (copy (i - 1) (Pretty.str ","))
(copy (i - 2) (Pretty.brk 1))
@[Pretty.str ")"]) )
end
else (form , curry Pretty.blo 0 o (mk_pretty form))
fun form_opsig attrs sis =
let val opsig_no_attr = mk_OpSigSet sis
val opsig_E_attr = if member attrs COMM
then if member attrs ASSOC
then set_as_ac sis opsig_no_attr
else set_as_commutative sis opsig_no_attr
else if member attrs ASSOC
then set_as_associative sis opsig_no_attr
else opsig_no_attr
in
(if member attrs GEN
then set_as_generator sis opsig_E_attr
else opsig_E_attr )
end
fun operator_dec_parser sort_store operator_store str =
let val (form,sis,attrs) = operator_parser sort_store str
val (form,pform) = modify_form form sis
in if well_declared_operator (form,sis)
then OK (form,insert_op_form operator_store (mk_form form) pform
(form_opsig attrs sis ))
else Error "Arity Mismatch Between Operator Form and Signature"
end
fun read_operators infn endfn SS TT FS =
let val s = infn ()
in if endfn s then (FS,TT)
else (case operator_dec_parser SS FS s of
OK (form,FS') =>
let val sy = case find_operator FS' (mk_form form) of
OK s => s
| Error m => (error_message
"Something seriously wrong with Operator insertion and retrieval: Catastrophic Failure " ; raise Fail)
in read_operators infn endfn SS (TranSys.build_trans_system
(mk_OpTerm sy) form TT) FS'
end
| Error m => (error_message m ; read_operators infn endfn SS TT FS ))
handle Parse.SynError _ => read_operators infn endfn SS TT FS
end
in
val enter_operators = read_operators (fn () => prompt_line "") nl
fun load_operators infn = read_operators infn Lex.end_check1
fun delete_operators T ss =
let val s = prompt_line ""
in if nl s then (ss,T)
else let val form = Lex.lex_line s
val FS = case find_operator ss (mk_form form) of
OK f => remove_operator ss f
| Error m => (error_message m ; ss)
in delete_operators (TranSys.prune_trans_system form T) FS
end
handle Fail => delete_operators T ss
end
end
local
fun unsig3 n (proforma,sigs) =
let val unf = unform proforma
in pad Left 4 (makestring (n:int)) ^ unf ^
stringwith (" : ","\n"^spaces (size unf + 5)^": ","\n")
(show_OpSigSet sigs)
end
fun unsig4 (form,sigs) =
let val unf = unform form
val sig_strings = show_OpSigSet sigs
fun f n (s::ss) = "\n"^pad Left 4 (makestring (n:int))^
(spaces (size unf + 1))^": "^ s ^f (succ n) ss
| f n [] = ""
in unf ^ f 1 sig_strings
end
fun unsig5 (form,sigs) =
let val unf = unform form
val sig_strings = show_OpSigSet sigs
fun f (s::ss) = unf ^" : "^ s ^"\n"^ f ss
| f [] = ""
in f sig_strings
end
in
fun display_operator_sig FS symb sgn =
show_operator FS symb ^" : "^ show_OpSig sgn
fun display_operator FS symb =
let val form = display_format FS symb
val sigs = operator_sig FS symb
in write_terminal (unsig4 (form,sigs))
end
fun display_operators FS =
let fun f n [] = ()
| f n (a::t) =
((write_terminal o (unsig3 n)) a;
f (succ n) t)
in f 1 (all_forms FS)
end
fun save_operators outfn FS =
(app (outfn o unsig5) (all_forms FS) ; outfn Lex.end_marker)
end
local
fun lkup ss form = givefM (fn () =>
(error_message "Catastrophic Error in operator lookup and retrieve" ; raise Fail))
(find_operator ss form)
fun delete_operator T ss =
let fun err_rec s = (error_flush s ; delete_operator T ss )
in
(prompt1 "";
case get_next_number () of
"" => if no_current_input ()
then (ss,T)
else err_rec "Enter Number of a Operator"
| i => (case stringtoint i of
OK n =>
let val syl = all_forms ss
in
if n < 1 orelse n > length syl then
err_rec ("No Operator with Number "^makestring n)
else
let val form = fst (nth (syl,pred n))
val symb = lkup ss form
val d = act_on_no_input (fn () =>
(clear_title " OPERATOR DELETE ";
write_terminal "SIGNATURES FOR OPERATOR: ";
display_operator ss symb ; newline () ;
print_line ();
write_terminal "Enter Number of Signature (or \"all\") :\n"))
fun f (ss,T) =
(case prompt_reply ""
of "" => (ss,T)
| "all" => (remove_operator ss symb,
TranSys.prune_trans_system (get_form form) T)
| s => (case stringtoint s of
OK m =>
(let val sigs = operator_sig ss symb
val sigl = get_OpSigs sigs
in if m < 1 orelse m > length sigl then
(error_flush ("No Signature with Number "
^makestring m); f (ss,T) )
else let val sign = nth (sigl,pred m)
val newsig =
remove_OpSig sigs sign
in if null_OpSigSet newsig then
f (remove_operator ss symb,
TranSys.prune_trans_system (get_form form) T)
else
f (change_opsig ss symb newsig,T)
end
end )
| Error m =>
(error_flush "Enter number of a signature to delete"; f (ss,T))))
in
f (ss,T)
end
end
| Error _ => err_rec "Enter number of an Operator to delete"))
end
fun set f sigs = mapfold (C f) get_type sigs (get_OpSigs sigs)
val unset = set unset_as_associative o set unset_as_commutative
val setac = set set_as_ac
val setcom = set set_as_commutative
val setass = set set_as_associative
fun change_theory ss =
let fun err_rec s = (error_flush s ; change_theory ss )
in
(prompt1 "";
case get_next_number () of
"" => if no_current_input ()
then ss
else err_rec "Enter Number of a Operator"
| i => (case stringtoint i of
OK n =>
let val syl = all_forms ss
in
if n < 1 orelse n > length syl then
err_rec ("No Operator with Number "^makestring n)
else
let val (form,sigs) = nth (syl,pred n)
val f = lkup ss form
in if arity ss f = 2 then
(act_on_no_input (fn () =>
(display_operator ss f ; newline () ;
write_terminal "Theory? (NONE | ASSOC | COMM | AC) :")) ;
case prompt_reply ""
of "NONE" => change_opsig ss f (unset sigs)
| "AC" => change_opsig ss f (setac sigs)
| "COMM" => change_opsig ss f (setcom sigs)
| "ASSOC" => change_opsig ss f (setass sigs)
| "" => (write_terminal "No change in Equational Theory." ; ss)
| s => (error_and_wait "Not a valid Equational Theory" ; ss) )
else (error_and_wait "Not a Binary Operator" ; ss)
end end
| Error _ => err_rec "Enter number of an Operator"
)
)
end
val Operator_Menu = Menu.build_menu "Operator Options"
[
("a", "Add Operators",
fn (s,(ss,T)) => (newline ();
write_terminal "Enter Operators:\n";
(s,enter_operators s T ss)
)
),
("d", "Delete Operators",
fn (s,(ss,T)) => (newline ();
write_terminal "Enter Number of Operator:\n";
(s,delete_operator T ss)
)
),
("e", "Equational Theory",
fn (s,(ss,T)) => (newline () ;
write_terminal "Enter Number of Operator:\n";
(s,(change_theory ss,T))
)
)
]
in
fun operator_options (s,(ss,T)) =
snd (Menu.display_menu_screen 1 Operator_Menu
(fn (s,(ss,T)) => display_operators ss)
"OPERATORS" "Operators" (s,(ss,T))
)
end
end
;
