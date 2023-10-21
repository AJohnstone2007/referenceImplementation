functor RewriteFUN (structure E : EQUALITY
structure Es : EQUALITYSET
structure T : TERM
structure S : SUBSTITUTION
structure P : PATH
structure M : ETOOLS
sharing type T.Term = E.Term = S.Term = P.Term = Es.Term = M.Term
and type T.Sig.Signature = E.Signature =
Es.Signature = S.Signature = M.Signature
and type S.Substitution = E.Substitution = M.Substitution
and type E.Equality = Es.Equality
sharing T.Sig.O.Pretty = T.Pretty = E.Pretty = Es.Pretty
) : REWRITE =
struct
structure Pretty = E.Pretty
type Signature = T.Sig.Signature
type Term = T.Term
type Path = P.Path
type Equality = E.Equality
type EqualitySet = Es.EqualitySet
type Substitution = S.Substitution
open E Es T S
fun equality S = relate (M.equality S)
fun printequality a s e =
Pretty.pr (Pretty.blo(2,[Pretty.str s,Pretty.str "  ",(pretty_equality a e),Pretty.str "\n"]),
snd (get_window_size ()))
fun print2equalities a s1 e1 s2 e2=
(Pretty.pr (Pretty.blo(2,[Pretty.str s1,Pretty.str "  ",(pretty_equality a e1),Pretty.str "\n"]),
snd (get_window_size ())) ;
Pretty.pr (Pretty.blo(2,[Pretty.str s2,Pretty.str "  ",(pretty_equality a e2),Pretty.str "\n"]),
snd (get_window_size ())))
local open P in
fun matchsubterm A T1 T2 =
let val numopsT2 = num_ops_in_term T2
fun traverse (hl::rl) p n=
let val (path',subs') = walk hl (deepen p n)
in if isfail subs' then traverse rl p (n+1)
else (path',subs')
end
| traverse [] p n = (root,FailSub)
and walk t1 p =
if numopsT2 > (num_ops_in_term t1)
then (root,FailSub)
else let val subs = (Statistics.inc_match_attempts (); M.match A T2 t1 )
in if isfail subs
then if compound t1
then traverse (subterms t1) p 1
else (root,FailSub)
else (Statistics.inc_match_success (); (p,subs))
end
in walk T1 root
end
end
fun matchandapply A T1 T2 T3 =
let val numopsT3 = num_ops_in_term T3
fun traverse (hl::rl)=
let val (hl',subs') = walk hl
in if isfail subs'
then apply_fst (cons hl) (traverse rl)
else (hl'::rl,subs')
end
| traverse [] = ([],FailSub)
and walk t1 =
if numopsT3 > (num_ops_in_term t1) then (t1,FailSub)
else let val subs = (Statistics.inc_match_attempts () ;
M.match A T3 t1 )
in if isfail subs
then if compound t1
then apply_fst (mk_OpTerm (root_operator t1)) (traverse (subterms t1))
else (t1,FailSub)
else (Statistics.inc_match_success () ;
(applysubtoterm subs T1,subs))
end
in walk T2
end
fun rewrite_flag Sigma e t =
if is_rule e
then let val (l,r) = terms e
val (t',s') = matchandapply Sigma r t l
in if isfail s'
then (false,t)
else (true,t')
end
else (false,t)
fun rewrite Sigma e = snd o rewrite_flag Sigma e
val ContinueRewrite = ref true
fun rewrite_cond A t cr R =
if is_rule cr
then let val (l,r) = terms cr
val (t',s') = matchandapply A r t l
in if non isfail s'
andalso
forall (equality A) (map (normaliseEquality A [R] o applysubtoequality s')
(conditions cr))
then (true,t')
else (false, t)
end
else (false, t)
and norm_once A t R =
let fun norm Es =
if empty_equality_set Es
then (false,t)
else let val e = select_eq Es
val (b,t') = if is_conditional e
then rewrite_cond A t e R
else rewrite_flag A (select_eq Es) t
in if b
then (Statistics.incRewriteCounter () ; (b,t'))
else norm (rest_eq Es)
end
in norm R
end
and normalise A t rewlist =
let val (simplified,t') = norm_once A t rewlist
in if simplified andalso compound t'
then if Statistics.testRewriteCounter ()
then if (write_terminal
("Rewriting suspended after "^(makestring (Statistics.showRewriteMax ()))^" rewrites.\n") ;
Pretty.pr (show_pretty_term A t',snd (get_window_size ())) ; newline ();
confirm "Do you wish to stop rewriting"
)
then (ContinueRewrite := false ; t')
else (Statistics.resetRewriteCounter () ; normalise A t' rewlist)
else normalise A t' rewlist
else t'
end
and normaliseflag A t rewlist =
(Statistics.resetRewriteCounter () ; ContinueRewrite := true ;
let val (simplified,t') = norm_once A t rewlist
in if simplified
then if compound t'
then let val t'' = normalise A t' rewlist in (!ContinueRewrite,t'') end
else (true,t')
else (false,t')
end )
and normalise_by_sets A t Es =
let fun norm (es::res) t =
let val (b,t') = normaliseflag A t es
in if b then norm Es t'
else norm res t'
end
| norm [] t = t
in norm Es t
end
and prnorm a level s e = Display_Level.display_at_level level
(fn unit => printequality a s e)
and normaliseEquality A Rs e =
if is_conditional e
then let val e1 = normaliseEquality A Rs (conclusion e)
in mk_conditional e1 (norm_conditions A Rs e)
end
else mk_equality (normalise_by_sets A (lhs e) Rs) (normalise_by_sets A (rhs e) Rs)
and normFlag A t (R1::Rs) =
let val (simplified,t1) = norm_once A t R1
in if simplified
then (true,t1)
else normFlag A t1 Rs
end
| normFlag A t [] = (false,t)
and normToId A Rs (l,r) (bl,br) =
if bl andalso (!ContinueRewrite)
then let val (bl',l1) = normFlag A l Rs
val e1 = mk_equality l1 r
in if equality A e1
then (true, e1)
else if br then
let val (br',r1) = normFlag A r Rs
val e2 = mk_equality l1 r1
in if equality A e2
then (true, e2)
else if bl' orelse br'
then normToId A Rs (l1, r1) (bl',br')
else (false, e2)
end
else if bl'
then normToId A Rs (l1, r) (bl',br)
else (false, mk_equality l1 r)
end
else if br andalso (!ContinueRewrite) then
let val (br',r1) = normFlag A r Rs
val e2 = mk_equality l r1
in if equality A e2
then (true, e2)
else if br'
then normToId A Rs (l, r1) (bl,br')
else (false, e2)
end
else (false, mk_equality l r)
and normToIdentity A Rs e =
(Statistics.resetRewriteCounter () ; ContinueRewrite := true ;
if is_conditional e
then let val e' = normaliseEquality A Rs e
in (equality A e',e')
end
else if equality A e then (true, e)
else normToId A Rs (terms e) (true, true)
)
and norm_conditions A Rs e = filtermap (non (equality A)) (normaliseEquality A Rs) (conditions e)
fun normaliseRight A Order New Rs R =
let val pr = prnorm A Display_Level.partial
val label = get_label R
val self = case get_by_label Rs label of OK _ => true | Error _ => false
val NewR = eqinsert Order EmptyEqSet New
val Rext = NewR :: (if self then remove_by_label Rs label else Rs)
fun keep_rule e l r = if is_rule e then order (mk_equality l r)
else (mk_equality l r)
fun notselfnormr e =
let val (l,r) = terms e
val (rb,r') = normaliseflag A r NewR
in if rb
then let val r'' = normalise_by_sets A r' Rext
val norme = keep_rule e l r''
val norme = if is_conditional e
then mk_conditional norme (norm_conditions A Rext e)
else norme
in (pr "Rule Rewritten to Rule: " norme;
norme)
end
else if is_conditional e
then mk_conditional (conclusion e) (norm_conditions A Rext e)
else e
end
fun selfnormr R (n,e) =
let val (l,r) = terms e
val (rb,r') = normaliseflag A r NewR
in if rb
then let val R' = delete_by_number R n
val r'' = normalise_by_sets A r' (R'::Rext)
val norme = keep_rule e l r''
val norme = if is_conditional e
then mk_conditional (conclusion norme) (norm_conditions A Rext e)
else norme
in (pr ("Rule "^label^makestring n^" Rewritten to Rule: ") norme;
eqinsert Order R' norme)
end
else R
end
in if self
then foldl_over_equations selfnormr R R
else equality_map Order notselfnormr R
end
fun normaliseLeft A Order New Rs R =
let val pr = prnorm A Display_Level.partial
val label = get_label R
val self = case get_by_label Rs label of OK _ => true | Error _ => false
val NewR = eqinsert Order EmptyEqSet New
val Rext = NewR :: (if self then remove_by_label Rs label else Rs)
fun norml e =
let val (l,r) = terms e
val (lb,l') = normaliseflag A l NewR
in if lb
then (lb,if is_conditional e
then mk_conditional (mk_equality l' r) (norm_conditions A Rext e)
else (mk_equality l' r))
else (lb,if is_conditional e
then mk_conditional (conclusion e) (norm_conditions A Rext e)
else e)
end
fun finish_norm R ((n,e)::es) =
if equality A e
then (pr ("Delete Rule "^label^makestring n^": ") e; finish_norm R es)
else
let val e' = normaliseEquality A (R::Rext) e
in if equality A e'
then (pr ("Delete Rule "^label^makestring n^": ") e';
finish_norm R es)
else (pr ("Rule "^label^makestring n^" Rewritten to Equation: ") e';
e' :: finish_norm R es)
end
| finish_norm R [] = []
val (lreds, R') = equality_filter norml R
in (finish_norm R' lreds , R')
end
fun normalisebyNew A Order New Equations Rules =
let fun pr e = print2equalities A "Equation" e "Rewritten to "
val NewR = eqinsert Order EmptyEqSet New
val Rext = NewR :: Rules
fun norm t = normalise_by_sets A t Rext
fun normb e =
let val (l,r) = terms e
val (bl,l') = normaliseflag A l NewR
val (br,r') = normaliseflag A r NewR
in if bl
then let val e' = mk_equality l' r'
in if equality A e'
then (pr e e' ;(true,e'))
else
let val l'' = norm l'
in if br
then let val e' = mk_equality l'' r'
in if equality A e'
then (pr e e' ;(true,e'))
else let val e' = if is_conditional e
then mk_conditional (mk_equality l'' (norm r'))
(norm_conditions A Rext e)
else (mk_equality l'' (norm r'))
val d = pr e e'
in (equality A e' , e')
end
end
else let val e' = if is_conditional e
then mk_conditional (mk_equality l'' r) (norm_conditions A Rext e)
else (mk_equality l'' r)
val d = pr e e'
in (equality A e' , e')
end
end
end
else if br
then let val e' =if is_conditional e
then mk_conditional (mk_equality l' r') (norm_conditions A Rext e)
else (mk_equality l' r')
in if equality A e'
then (pr e e' ;(true,e'))
else
let val e' = if is_conditional e
then mk_conditional (mk_equality l' (norm r')) (norm_conditions A Rext e)
else (mk_equality l' (norm r'))
val d = pr e e'
in (equality A e' , e')
end
end
else (false, if is_conditional e
then mk_conditional (conclusion e) (norm_conditions A Rext e)
else e)
end
in snd (equality_filter normb Equations)
end
end
;