functor I_SignatureFUN (structure T : TERM
structure iS : I_SORT
structure iO : I_OPSYMB
structure iV : I_VARIABLE
structure E : EQUALITYSET
structure N : ENVIRONMENT
structure C : CAC_THEORY
structure State : STATE
sharing type T.Term = iO.Term = E.Term = State.Term
and type iS.Sort = iO.Sort = T.Sort = T.Sig.S.Sort
and type T.Sig.S.Sort_Store = iS.Sort_Store = iO.Sort_Store = iV.Sort_Store
and type T.Sig.O.OpId = iO.OpId
and type T.Sig.O.OpSig = iO.OpSig
and type T.Sig.O.Op_Store = iO.Op_Store
and type T.Sig.V.Variable_Store = iV.Variable_Store
and type T.Sig.Signature = N.Signature = E.Signature = C.Signature = State.Signature
and type E.Equality = N.Equality = C.Equality
and type E.EqualitySet = State.EqualitySet
and type N.Environment = State.Environment
) : I_SIGNATURE =
struct
type State = State.State
open T iS iO iV T.Sig T.Sig.S State
local
fun disp_op_pairs fs s [] = ""
| disp_op_pairs fs s ((sig1,sig2)::ss) =
"Between  "^(display_operator_sig fs s sig1)^"\n    and  "^
(display_operator_sig fs s sig2)^"\n"^(disp_op_pairs fs s ss)
fun disp_not_satisfies fs [] = ""
| disp_not_satisfies fs ((s,l)::rs) =
disp_op_pairs fs s l ^ disp_not_satisfies fs rs
fun sort_op f (A,T) = ((change_sorts A o f o get_sorts) A, T)
fun operator_op f (A,T) = ((change_operators A o f o get_operators) A, T)
val Signature_Menu = Menu.build_menu "SIGNATURE OPTIONS"
[
("s", "Sort Options",sort_op sort_options),
("i", "Inhabitedness test",fn (A,T) => (write_terminal "Testing Inhabitedness\n" ;
Timer.timer (fn () =>
let val (U,I) = inhabited A
in display_two_cols Left ("INHABITED SORTS",map sort_name I,
"UNINHABITED SORTS",map sort_name U)
end ) ;
message_and_wait (); (A,T) )),
("o", "Sort Ordering Options",sort_op sort_order_options),
("m", "Montonicity test",fn (A,T) => (write_terminal "Testing Monotonicity\n" ;
Timer.timer (fn () =>
let val non_monos = monotonic A
in if null non_monos
then write_terminal "The Signature is Monotonic\n"
else (write_terminal "These Pairs of Operator Signature Break Monotonicity\n" ;
write_terminal (disp_not_satisfies (get_operators A) non_monos))
end) ;
message_and_wait (); (A,T) )),
("p", "Operator Options",fn (A,T) => (apply_pair (change_operators A,I)
(operator_options (get_sorts A,(get_operators A,T))))),
("r", "Regularity test",fn (A,T) => (write_terminal "Testing Regularity\n" ;
Timer.timer (fn () =>
let val non_regulars = regular A
in if null non_regulars
then write_terminal "The Signature is Regular\n"
else (write_terminal "These Pairs of Operator Signatures Break Regularity\n" ;
write_terminal (disp_not_satisfies (get_operators A) non_regulars))
end) ;
message_and_wait (); (A,T) )),
("v", "Variable Options",fn (A,T) => (change_variables A (variable_options
(get_sorts A,get_variables A)),T))
]
fun decompose S = (get_Signature S , get_Parser S , get_Equalities S , get_Environment S)
in
fun signature_options S =
let val (A,T,Es,En) = decompose S
val (A',T') = Menu.display_menu_screen 2 Signature_Menu
I "Signature Options" "Signature" (A,T)
val strat = snd (N.get_locstrat En)
val d = write_terminal "Calculating new equational theory"
val newes = C.CAC_Theory A'
val es' = foldl (E.eqinsert (strat A')) E.EmptyEqSet newes
in change_EqTheory (change_Parser (change_Signature S A') T') es'
end
end
end
;
