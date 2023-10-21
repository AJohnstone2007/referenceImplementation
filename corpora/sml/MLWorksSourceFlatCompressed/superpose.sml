functor SuperposeFUN (structure T : TERM
structure S : SUBSTITUTION
structure E : ETOOLS
structure P : PATH
sharing type T.Term = S.Term = E.Term = P.Term
and type S.Substitution = E.Substitution
and type T.Sig.Signature = S.Signature = E.Signature
) : SUPERPOSE =
struct
type Signature = S.Signature
type Term = T.Term
type Substitution = S.Substitution
type Path = P.Path
open S T
local open P in
fun superpose Sigma T1 T2 =
let
fun traverse p (s,n) hl = (walk hl (deepen p n) s,n+1)
and walk t1 p subl =
if compound t1
then let val subs = (Statistics.inc_unify_attempts () ;
E.unify Sigma t1 T2 )
val subl' = map (fn s => (
Statistics.inc_unify_success ();
(s,p)))
subs
in fst (foldl (traverse p) (subl',1) (subterms t1))
end
else subl
in walk T1 root []
end
end ;
fun superposerep Sigma rhs T1 T2 =
let fun unh Term1 subl =
if compound Term1 then
let val subs = (Statistics.inc_unify_attempts () ;
E.unify Sigma Term1 T2)
val subl' = map (fn s => (Statistics.inc_unify_success ();
(s,rhs))) subs
fun gg (hl::rl) s done =
let val shl = unh hl []
in gg rl (s @ map (apply_snd (fn x => done@(x::rl))) shl ) (done@[hl])
end
| gg [] s _ = s
in
map (apply_snd (mk_OpTerm (root_operator Term1))) (gg (subterms Term1) [] []) @ subl'
end
else subl
in
unh T1 []
end
fun supreponsubterms Sigma rhs T1 T2 =
let fun gg done (t::todo) =
(map (apply_snd (fn s => mk_OpTerm (root_operator T1) (done@(s::todo))))
(superposerep Sigma rhs t T2 ))
@ (gg (snoc done t) todo)
| gg _ [] = []
in gg [] (subterms T1)
end
end
;
