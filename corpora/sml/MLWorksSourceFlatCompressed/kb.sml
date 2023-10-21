functor KbFUN (structure T : TERM
structure Eq : EQUALITY
structure Es : EQUALITYSET
structure En : ENVIRONMENT
structure iEq : I_EQUALITY
structure Str : STRATEGY
structure Sp : SORT_PRESERVE
structure Ord : ORDER
structure R : REWRITE
structure C : CRITICALPAIR
structure Ct : COMPLETIONTOOLS
structure State : STATE
sharing type Eq.Equality = Es.Equality = En.Equality = iEq.Equality =
Str.Equality = C.Equality = R.Equality = Ct.Equality
and type Es.EqualitySet = iEq.EqualitySet = Str.EqualitySet =
C.EqualitySet = R.EqualitySet = Ct.EqualitySet = State.EqualitySet
and type Eq.Term = T.Term = Es.Term = iEq.Term =
Sp.Term = R.Term = Ct.Term = State.Term
and type En.Signature = T.Sig.Signature = iEq.Signature = Ct.Signature =
Eq.Signature = Es.Signature = C.Signature = State.Signature =
Sp.Signature = R.Signature = Str.Signature
and type Ord.ORIENTATION = En.ORIENTATION
and type En.Environment = State.Environment
) : KB =
struct
val CompName = "Knuth-Bendix Completion"
type EqualitySet = Es.EqualitySet
type State = State.State
open T T.Sig En Es Eq iEq Str Sp Ord R C Ct State
fun complete S step (E,R) H =
let
val (A,T,ENV) = (get_Signature S, get_Parser S, get_Environment S)
val (StratName , Strategy) = get_locstrat ENV
val orient = snd (get_globord ENV)
val RuleNum = (Statistics.reset_part_statistics () ; ref (total_entered_in_eqset R))
val E' = rename_eq_set E
val R' = rename_eq_set R
val ins_by_strat = foldl (insert_by_strat A Strategy)
val eqins = eqinsert (Strategy A)
val normR = normaliseRight A (Strategy A)
val normL = normaliseLeft A (Strategy A)
fun cps e R E = (write_terminal "Generating Critical Pairs\n"; cpall A (Strategy A) e R E)
val normN = normalisebyNew A (Strategy A)
fun delay s (A,T,env) newenv H rE R e1 =
(printequality A s e1; newline () ;
if confirm "Do you wish to try Splitting by Introducing new Operator"
then (write_terminal "Splitting Equation" ;
let val (eqs, A' , T') = split A T e1
in (write_terminal "Introducing Equations :\n" ;
display_equality_set A' eqs ;
write_terminal "Contuinuing.\n";
kb (A',T',newenv) H (merge_eqsets (Strategy A) rE eqs) R )
end)
else if confirm "Do you wish to continue"
then (printequality A "Delaying Consideration of " e1;
kb (A,T,newenv) H (eqinsert (by_age_strat A) rE e1) R)
else ((eqins rE e1,R,H), (A,T,env))
)
and kb (A,T,env) H E R =
if interrupt ()
then (write_terminal "User Interrupts Knuth-Bendix Completion. \n";
message_and_wait () ;
((E,R,H),(A,T,env)))
else
if empty_equality_set E
then (write_terminal ("\nComplete.\nStatistics:\n") ;
ignore(Statistics.display_partial_statistics ()) ;
write_terminal ("\nConfluent Set of "^
(eq_set_size R)^" Rewrite Rules\n");
((E,R,H),(A,T,env)))
else
let
val (e1,rE) = (newline () ;
write_terminal ((eq_set_size E)^" Equations, "
^(eq_set_size R)^" Rules\n") ;
selectNext StratName E )
val (orientation,newenv) = (printequality A "Scanning " e1; orient A env e1 )
in
case orientation of
UNORIENTABLE => delay "Unorientable Equation By Term Ordering :\n" (A,T,env) newenv H rE R e1
| x => (let val newrule = if x = LR then order e1 else reorder e1
in
if sort_decreasing A (terms newrule)
then
let val R' = (printequality A ("Rule No:"^(makestring(!RuleNum))^" Ordered as ")
newrule; inc RuleNum;
write_terminal "\nNormalising Rules\n";
normR newrule [R] R)
val (E',R'') = apply_snd (C eqins newrule) (normL newrule [R'] R')
val E' = (write_terminal "\nNormalising Equations\n";
ins_by_strat (normN newrule rE [R'']) E')
val (b,H') = consider_conjectures A E' [R''] H
in if b
then ((E',R'',H'), (A,T,newenv))
else let val E'' = (cps R'' newrule R'' E')
in if stop step A [E'',R''] H'
then ((E'',R'',H'), (A,T,newenv))
else kb (A,T,newenv) H' E'' R''
end
end
else delay "Unorientable Equation By Term Ordering Conflict with Sort Ordering : \n " (A,T,env) newenv H rE R e1
end )
end
val ((E',R',H'), (A',T',env)) = kb (A,T,ENV) H E' R'
in
((E',R',H'),change_Environment (K env) (change_Parser (change_Signature S A') T'))
end
end
;
