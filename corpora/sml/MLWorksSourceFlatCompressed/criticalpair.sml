functor CriticalPairFUN (structure E : EQUALITY
structure Es : EQUALITYSET
structure S : SUPERPOSE
structure U : ETOOLS
structure R : REWRITE
structure P : PATH
structure T : TERM
sharing type E.Term = S.Term = U.Term = Es.Term = R.Term = P.Term = T.Term
and type E.Signature = Es.Signature = R.Signature
= S.Signature = U.Signature = T.Sig.Signature
and type S.Substitution = E.Substitution = U.Substitution
and type E.Equality = Es.Equality = R.Equality
and type Es.EqualitySet = R.EqualitySet
and type P.Path = S.Path
sharing T.Sig.O.Pretty = T.Pretty = E.Pretty = Es.Pretty
) : CRITICALPAIR =
struct
structure Pretty = E.Pretty
type Signature = E.Signature
type Equality = E.Equality
type EqualitySet = Es.EqualitySet
open E Es
fun equality A = relate (U.equality A)
local
fun printequality a s pe =
Pretty.pr (Pretty.blo(2,[Pretty.str s,Pretty.str "  ",pe,Pretty.str "\n"]),
snd (get_window_size ()))
fun prnorm a level s e = Display_Level.display_at_level level (fn unit => printequality a s e)
in
fun proper_cps Sigma e1 e2 =
let val (l1,r1) = terms e1
val (l2,r2) = terms e2
val cexs = S.supreponsubterms Sigma r2 l1 l2
in map (fn (s,l) => rename_equality(applysubtoequality s (mk_equality r1 l))) cexs
end
fun top_cps Sigma e1 e2 =
let val e2 = if EqualityEq e1 e2 then rename_equality e2 else e2
val (l1,r1) = terms e1
val (l2,r2) = terms e2
val subs = (Statistics.inc_unify_attempts () ; U.unify Sigma l1 l2)
in map (fn s => rename_equality(applysubtoequality s (mk_equality r1 r2))) subs
end
fun cpg Sigma Order R lab e1 EO (N:int,e2) =
let
val e2 = if EqualityEq e1 e2 then rename_equality e2 else e2
val reduce = R.normToIdentity Sigma [R]
fun cp L CP =
let val d = prnorm Sigma Display_Level.full ("Critical Pair : (new/"^lab^makestring N^"): ") (pretty_equality Sigma CP)
val (cpb,CPN) = reduce CP
val PCPN = pretty_equality Sigma CPN
val d = prnorm Sigma Display_Level.full "Reduced To :" PCPN
in if cpb
then (prnorm Sigma Display_Level.full "Delete :" PCPN ; L)
else (prnorm Sigma Display_Level.full "Add Equation:" PCPN ; eqinsert Order L CPN)
end
val top =
let val CPS = (Statistics.inc_unify_attempts () ; top_cps Sigma e1 e2)
fun each_sub EqSet s = (Statistics.inc_unify_success () ; cp EqSet s)
in foldl each_sub EmptyEqSet CPS
end
val CP1 = proper_cps Sigma e1 e2
val CP2 = if EqualityEq e1 e2
then []
else proper_cps Sigma e2 e1
in (app (fn x => Statistics.inc_critical_pair_count ()) (CP1@CP2) ;
merge_eqsets Order (foldl cp (foldl cp EO CP1) CP2)) top
end
fun cpall Sigma Order R1 newe R2 EO =
foldl_over_equations (cpg Sigma Order R1 (get_label R2) newe) EO R2
fun coherencepairs Sigma e1 e2 =
let val (l1,r1) = (lhs e1,rhs e1)
val e2 = if EqualityEq e1 e2 then rename_equality e2 else e2
val (l2,r2) = (lhs e2,rhs e2)
fun cp r L (s,l1) =
let val CP = rename_equality (applysubtoequality s (mk_equality r l1))
val d = prnorm Sigma Display_Level.full
("Coherence Pair : (on "^unparse_equality Sigma e1 ^" by "^
unparse_equality Sigma e2^"): ")
(pretty_equality Sigma CP)
in CP :: L
end
val CP1 = S.supreponsubterms Sigma r2 l1 l2
in (app (fn x => Statistics.inc_critical_pair_count ()) CP1 ;
foldl (cp r1) [] CP1)
end
fun all_coherencepairs Sigma E r = mapapp (C (coherencepairs Sigma) r) (get_equalities E)
local
open P S
in
fun rep Sigma u p s = replace u p s
handle Nth =>
(write_terminal ("Failed to replace in "^T.show_term Sigma u^" at path "^show_path p^" by "^T.show_term Sigma s^"\n"); raise Nth)
fun extend Sigma e2 e1 =
let
val u = lhs e1
val s = lhs e2
val t = rhs e2
fun change p = if is_root p then []
else [order (mk_equality (rep Sigma u p s) (rep Sigma u p t))]
in mapapp change (mapfold (insert PathEq) snd [] (superpose Sigma u s))
end
fun extended_rules Sigma E e =
equality_foldl (fn l => append l o extend Sigma e) [] E
end
end
end
;