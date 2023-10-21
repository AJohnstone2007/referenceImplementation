functor SortFUN () : SORT =
struct
abstype Sort = sort of string
with
val Top = sort "Top"
val Bottom = sort "Bottom"
fun name_sort s = sort s
fun sort_name (sort s) = s
fun SortEq (sort s) (sort s') = (s = s')
fun ord_s (sort s) (sort s') = (s <= s')
fun sord (sort s) (sort s') = if s = s' then EQ
else if s <= s' then LT else GT
end ;
structure SOL = OrdList2FUN (struct type T = Sort val order = sord end)
abstype Sort_Order = SO of (Sort * Sort) list * (Sort * Sort) list * (Sort,Sort list) Assoc.Assoc
with
local
val in_sortlist = element SortEq
fun compare_sort_pair (s,t) (s',t') = SortEq s s' andalso SortEq t t'
in
val Empty_Sort_Order = SO ([],[],Assoc.Empty_Assoc)
val Null_Sort_Order = SO ([(Bottom,Top)],[(Bottom,Top)],Assoc.Empty_Assoc)
fun subsorts (So as SO (_,_,a)) s =
(case Assoc.assoc_lookup SortEq s a of
Match sl => sl | NoMatch => [])
fun supersorts (So as SO (so,_,_)) s =
let fun check ((sl,su)::rs) =
if SortEq s sl then
union SortEq (su :: supersorts So su) (check rs)
else (check rs)
| check [] = []
in check so
end
fun sort_ordered so (s1,s2) = SortEq Top s2 orelse
SortEq Bottom s1 orelse
in_sortlist (subsorts so s2) s1
fun sort_ordered_reflexive so (s1,s2) = SortEq s1 s2 orelse sort_ordered so (s1,s2)
fun maximal_sorts so sl =
let fun f (s::l) = if exists (curry (sort_ordered so) s) sl
then f l
else union SortEq [s] (f l)
| f [] = []
in f sl
end
fun minimal_sorts so sl =
let fun f (s::l) = if exists (C (curry (sort_ordered so)) s) sl
then f l
else union SortEq [s] (f l)
| f [] = []
in f sl
end
fun update So subs a hi = Assoc.assoc_update SortEq hi (union SortEq (subsorts So hi) subs) a
fun extend_sort_order (So as SO (so,sl,a)) (lo,hi) =
if SortEq lo hi
then (Error.error_message ((sort_name lo)^" < "^(sort_name hi)^
" reflexive declarations unnecessary.") ; So)
else
let val subs = (subsorts So lo)
in
if in_sortlist subs hi
then (Error.error_message ("adding "^(sort_name lo)^" < "^(sort_name hi)^
" generates a circularity.") ; So)
else if element compare_sort_pair sl (lo,hi) then So
else if sort_ordered So (lo,hi)
then SO (so, snoc sl (lo,hi),a)
else let val sups = supersorts So hi
fun P (s,t) =
(in_sortlist sups t orelse SortEq hi t)
andalso
(in_sortlist subs s orelse SortEq lo s)
in
SO ((lo,hi)::(filter (not o P) so),
snoc sl (lo,hi),
foldl (update So (lo::subs)) a (hi::sups)
)
end
end
fun meet_of_sorts so (s,s') =
if SortEq s s' then [s]
else let val ss = subsorts so s
in if in_sortlist ss s'
then [s']
else let val ss' = subsorts so s'
in if in_sortlist ss' s
then [s]
else maximal_sorts so (intersection SortEq ss ss')
end
end
fun restrict_sort_order (SO (_,sp,_)) (s,s') =
foldl extend_sort_order Empty_Sort_Order (remove compare_sort_pair sp (s,s'))
fun remove_from_order (SO (_,sp,_)) s =
foldl extend_sort_order Empty_Sort_Order
(filter (non (fn (s1,s2) => SortEq s s1 orelse SortEq s s2)) sp)
fun name_sort_order (SO (_,so,_)) = map (apply_both sort_name) so
fun sort_ordered_list so [] [] = true
| sort_ordered_list so [] _ = false
| sort_ordered_list so _ [] = false
| sort_ordered_list so (s::ss) (t::tt) =
sort_ordered_reflexive so (s,t)
andalso
sort_ordered_list so ss tt
end
end
local
open SOL
in
abstype Sort_Store = Sort_Store of (OrdList * Sort_Order)
with
val Empty_Sort_Store = Sort_Store(EMPTY,Empty_Sort_Order)
fun insert_sort (Sort_Store(ss,so)) s =
Sort_Store(insert ss s, so)
fun insert_sort_order (Sort_Store(ss,so)) nso = Sort_Store(ss,nso)
fun is_declared_sort (Sort_Store(ss,so)) s = mem ss s
fun get_sort_order (Sort_Store(ss,so)) = so
fun delete_sort (Sort_Store(ss,so)) s =
Sort_Store(remove ss s,
remove_from_order so s)
fun fold_over_sorts f b (Sort_Store(ss,so)) =
fold f b ss
fun name_all_sorts SS =
fold_over_sorts (C (cons o sort_name)) [] SS
end
end
end
;
