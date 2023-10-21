functor Sort_PreserveFUN (structure T : TERM
structure S : SUBSTITUTION
sharing type S.Term = T.Term
and type S.Variable = T.Sig.V.Variable = T.Variable
and type T.Sig.S.Sort = T.Sig.V.Sort = T.Sort
and type S.Signature = T.Sig.Signature
) : SORT_PRESERVE =
struct
type Term = T.Term
type Substitution = S.Substitution
structure Sort = T.Sig.S
structure Vars = T.Sig.V
open S T
fun weaken_variable so (V : Vars.Variable) =
let val var_sort = Vars.variable_sort V
val ss = var_sort :: (Sort.subsorts so var_sort)
in map ((pair V) o mk_VarTerm o Vars.generate_variable) ss
end
fun tau_weakenings (Sigma : Sig.Signature) (Vars : Vars.Variable list) =
let val new_var_pairs = map (weaken_variable (Sig.get_sort_ordering Sigma)) Vars
val proto_subs = all_seqs new_var_pairs
in map (foldl addsub EMPTY) proto_subs
end
fun test_all_weakenings p (Sigma : Sig.Signature) (t1,t2) =
let val vars = vars_of_term t1
val taus = tau_weakenings Sigma vars
fun sort_test (s,t) Sub =
p (least_sort Sigma (applysubtoterm Sub s)) (applysubtoterm Sub t)
val failTaus = filter (not o sort_test (t1,t2)) taus
in null failTaus
orelse
(if Display_Level.current_display_level () = Display_Level.full
then let val b = !Display_Level.Show_Sorts
in (Display_Level.Show_Sorts := true ;
write_terminal (stringlist (show_substitution Sigma)
("\nNon-Sort Decreasing Weakenings:\n",",\n","\n") failTaus) ;
Display_Level.Show_Sorts := b )
end
else ();
false)
end
fun sort_decreasing (Sigma : Sig.Signature) (t,t') =
(test_all_weakenings (of_sort Sigma) Sigma (t,t')
handle (Least_Sort _) => (error_message "Signature is not Regular" ; false))
fun sort_preserving (Sigma : Sig.Signature) (t,t') =
(test_all_weakenings (C (Sort.SortEq o least_sort Sigma)) Sigma (t,t')
handle (Least_Sort _) => (error_message "Signature is not Regular" ; false))
end
;
