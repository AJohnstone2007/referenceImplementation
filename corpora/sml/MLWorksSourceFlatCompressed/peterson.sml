functor PetersonFUN (structure T : TERM
structure S : SUBSTITUTION
structure Eq : EQUALITY
structure Es : EQUALITYSET
structure En : ENVIRONMENT
structure iEq : I_EQUALITY
structure Str : STRATEGY
structure M : ETOOLS
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
and type Eq.Term = T.Term = Es.Term = S.Term = iEq.Term =
M.Term = Sp.Term = R.Term = Ct.Term = State.Term
and type En.Signature = T.Sig.Signature = iEq.Signature = State.Signature =
Eq.Signature = Es.Signature = S.Signature = C.Signature = Ct.Signature =
M.Signature = Sp.Signature = R.Signature = Str.Signature
and type S.Substitution = M.Substitution
and type Ord.ORIENTATION = En.ORIENTATION
and type En.Environment = State.Environment
) : KB =
struct
val CompName = "Peterson and Stickel's AC-Completion"
type EqualitySet = Es.EqualitySet
type State = State.State
open T T.Sig En Es Eq S iEq Str M Sp Ord R C Ct State
fun equality A = relate (M.equality A)
fun prnorm a level s e = Display_Level.display_at_level level (fn unit => printequality a s e)
fun cpg Sigma Order R lab e1 EO (N:int,e2) =
let
val e2 = if EqualityEq e1 e2 then rename_equality e2 else e2
val reduce = R.normaliseEquality Sigma R
fun cp L CP =
let val d = prnorm Sigma Display_Level.full ("Critical Pair : (new/"^lab^makestring N^"): ") CP
val CPN = reduce CP
val d = prnorm Sigma Display_Level.full "Reduced To :" CPN
in if equality Sigma CPN
then (prnorm Sigma Display_Level.full "Delete :" CPN ; L)
else eqinsert Order L CPN
end
val top =
let val CPS = (Statistics.inc_unify_attempts () ; top_cps Sigma e1 e2)
fun each_sub EqSet s = (Statistics.inc_unify_success () ; cp EqSet s)
in foldl each_sub EmptyEqSet CPS
end
val CP1 = if protected e1 then [] else proper_cps Sigma e1 e2
val CP2 = if protected e2 orelse EqualityEq e1 e2
then []
else proper_cps Sigma e2 e1
in (app (fn x => Statistics.inc_critical_pair_count ()) (CP1@CP2) ;
merge_eqsets Order (foldl cp (foldl cp EO CP1) CP2)) top
end
fun cpall Sigma Order R1 newe R2 EO =
foldl_over_equations (cpg Sigma Order R1 (get_label R2) newe) EO R2
fun complete S step (E,R) H =
let val (A,T,ENV,AC) = (get_Signature S, get_Parser S, get_Environment S, get_EqTheory S)
val (StratName, Strategy) = get_locstrat ENV
val orient = snd (get_globord ENV)
val RuleNum = (Statistics.reset_part_statistics () ; ref 1)
val E' = rename_eq_set E
val R' = rename_eq_set R
val ins_by_strat = foldl (insert_by_strat A Strategy)
val Strategy = Strategy A
val eqins = eqinsert Strategy
val normR = normaliseRight A Strategy
val normL = normaliseLeft A Strategy
fun normN Es E e = normalisebyNew A Strategy e E Es
fun nRby Rs R (r::rs) = nRby Rs (normR r (R::Rs) R) rs
| nRby Rs R [] = R
fun normRby Rs R ers = nRby (ins_by_strat EmptyEqSet ers :: Rs) R ers
fun nLby Rs R (r::rs) =
let val (E1,R1) = normL r (R::Rs) R
val (E2,R2) = nLby Rs R1 rs
in (E1@E2, R2)
end
| nLby Rs R [] = ([],R)
fun normLby Rs R ers = nLby (ins_by_strat EmptyEqSet ers :: Rs) R ers
fun cps R1 e R2 E = cpall A Strategy R1 e R2 E
fun valid (el,er) r =
let val ms = M.all_matches A (lhs r) el
fun rew s = not (M.equality A er (applysubtoterm s (rhs r)))
in forall rew ms
end
fun extensions R e Rs =
let val d = write_terminal "Generating Extensions.\n"
fun normr e = (lhs e,normalise_by_sets A (rhs e) Rs)
val exts = (extended_rules A AC e)
val exs = map (normr o rename_equality) exts
val Res = e :: mapapp get_equalities Rs
fun filter_fun (exs,res) e =
if forall (valid e) res
then let val r = (protect o order o uncurry mk_equality) e
in (r::exs,r::res)
end
else (exs,res)
val valid_extensions = fst (foldl filter_fun ([],Res) exs)
in (app (fn e => (Display_Level.display_at_level Display_Level.partial
(fn () => printequality A "Extended Rule: " e) ) )
valid_extensions ;
valid_extensions
)
end
fun orient_all (A,T,env) E S R H =
if forall protected (get_equalities E)
then (false,((E,S,R,H),(A,T,env)))
else let val (e1,rE) = (unprotect (select_eq E), rest_eq E)
val (orientation,newenv) = (printequality A "Scanning " e1; orient A env e1 )
in case orientation of
UNORIENTABLE => delay "Unorientable Equation By Term Ordering :\n" (A,T,env) newenv H rE S R e1
| x => let val newrule = unprotect (if x = LR then order e1 else reorder e1)
in
if sort_decreasing A (terms newrule)
then
let val d = (printequality A ("Rule No:"^(makestring(!RuleNum))^" Ordered as ")
newrule; inc RuleNum)
val R1 = (write_terminal "\nNormalising Rules on Right\n";
normR newrule [S,R] R)
val S1 = normR newrule [S,R1] S
val (E1,S2) = (write_terminal "Normalising Rules on Left\n";
apply_snd (C eqins newrule)
(normL newrule [R1,S1] S1))
val (E2,R2) = normL newrule [R1,S2] R1
val exts = extensions S2 newrule [R2,S2]
val R3 = (write_terminal "\nNormalising Rules on Right by Extensions\n";
normRby [S2,R2] R2 exts)
val S3 = nRby [S2,R3] S2 exts
val (E3,S4) = (write_terminal "Normalising Rules on Left by Extensions\n";
apply_snd (C ins_by_strat exts)
(normLby [R3,S3] S3 exts))
val (E4,R4) = nLby [S4,R3] R3 exts
val E1 = (write_terminal "Normalising Equations\n";
foldl (normN [S4,R4]) (ins_by_strat rE (E1@E2@E3@E4))
(newrule::exts))
val (b,H1) = consider_conjectures A E1 [S4,R4] H
in if b
then (b,((E1,S4,R4,H1),(A,T,newenv)))
else orient_all (A,T,newenv) E1 S4 R4 H1
end
else delay "Unorientable Equation By Term Ordering Conflict with Sort Ordering : \n " (A,T,env) newenv H rE S R e1
end
end
and delay s (A,T,env) newenv H rE S R e1 =
(printequality A s e1; newline () ;
orient_all (A,T,env) (eqinsert (by_age_strat A) rE (protect e1)) S R H )
and
kb (A,T,env) H E S R =
let val (b,((E1,S1,R1,H1),(A,T,newenv))) = orient_all (A,T,env) E S R H
in
if empty_equality_set E1 andalso empty_equality_set S1
then (write_terminal ("\nComplete.\nStatistics:\n") ;
ignore(Statistics.display_partial_statistics ()) ;
write_terminal ("\nConfluent Set of "^
(eq_set_size R1)^" Rewrite Rules\n");
((E1,merge_eqsets Strategy R1 S1,H1),(A,T,newenv)))
else
if forall protected (get_equalities E1) andalso empty_equality_set S1
then (write_terminal ("\nFailure with Unorientable rules - no more to consider.\nStatistics:\n") ;
ignore(Statistics.display_partial_statistics ()) ;
write_terminal ("\nNon-Confluent Set of "^
(eq_set_size R1)^" Rewrite Rules\nRemaining equations: "^(eq_set_size E1)^"\n");
((E1,merge_eqsets Strategy R1 S1,H1),(A,T,newenv)))
else
if b
then ((E1,merge_eqsets Strategy R1 S1,H1), (A,T,newenv))
else let val (nextrule,S2) = selectNext StratName S1
val R2 = eqins R1 nextrule
val d = printequality A "Considering Critical Pairs of Rule: " nextrule
val E2 = cps [S2,R2] nextrule R2 E1
in if stop step A [E2,S2,R2] H1
then ((E2,merge_eqsets Strategy R2 S2,H1),(A,T,newenv))
else kb (A,T,newenv) H1 E2 S2 R2
end
handle Interrupt => (write_terminal "User Interrupts Peterson and Stickels AC-Completion. \n";
message_and_wait () ;
((E,R,H),(A,T,env)))
end
val ((E',R',H'), (A',T',env)) = kb (A,T,ENV) H E' (new_equality_set "T" "Temporary Rewrites") R'
in
((E',R',H'),change_Environment (K env) (change_Parser (change_Signature S A') T'))
end
end
;
