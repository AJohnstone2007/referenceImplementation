functor PrecedenceFUN (structure T : TERM
) : PRECEDENCE =
struct
type OpId = T.Sig.O.OpId
val OpIdEq = T.Sig.O.OpIdeq
type Signature = T.Sig.Signature
type Term = T.Term
type Precedence = (OpId * OpId) list
* (OpId * OpId) list
val Null_Precedence : Precedence = ([] , [])
fun subops (so: Precedence) s =
let fun check ((sl,su)::rs) =
if OpIdEq s su
then union OpIdEq (sl :: subops so sl) (check rs)
else (check rs)
| check [] = []
in check (fst so)
end
fun Prec_Order l:Precedence =
let fun cut (f1::f2::rf) = (f1,f2)::cut (f2::rf)
| cut _ = []
in (cut l,[])
end
fun apply_prec p f1 f2 = exists (OpIdEq f2) (subops p f1)
fun same_prec po f =
let val es = snd po
fun check el ((sl,su)::rs) i =
(check (union OpIdEq
(if OpIdEq i sl andalso not (element OpIdEq el su)
then (check (su :: el) es su)
else el )
(if OpIdEq i su andalso not (element OpIdEq el sl)
then (check (sl::el) es sl)
else el ) )
rs i)
| check el [] _ = el
in check [f] es f
end
fun equal_prec po f g = element OpIdEq (same_prec po f) g
fun add_to_prec_order (po as (so,se): Precedence) p =
if uncurry (equal_prec po) p
then po else (p::so,se):Precedence
fun add_eq_to_prec_order (po as (so,se): Precedence) p =
if uncurry (apply_prec po) p orelse uncurry (apply_prec po) (swap p) orelse uncurry OpIdEq p
then po else (so,p::se):Precedence
fun remove_from_prec_order (((sy1,sy2)::rp,se):Precedence) (f1,f2) =
if OpIdEq sy1 f1 andalso OpIdEq sy2 f2
then (rp,se)
else let val (rp,se) = remove_from_prec_order (rp,se) (f1,f2)
in ((sy1,sy2) :: rp, se) end
| remove_from_prec_order ([],se) _ = ([],se) : Precedence
fun remove_eq_from_prec_order ((rp,(sy1,sy2)::se):Precedence) (f1,f2) =
if OpIdEq sy1 f1 andalso OpIdEq sy2 f2
then (rp,se)
else let val (rp,se) = remove_eq_from_prec_order (rp,se) (f1,f2)
in (rp, (sy1,sy2) :: se) end
| remove_eq_from_prec_order (se,[]) _ = (se,[]) : Precedence
fun form sigma = T.Sig.O.show_operator (T.Sig.get_operators sigma)
fun unparse_prec sigma ((P,E):Precedence) =
map (fn (f1,f2) => form sigma f1 ^ "  <  "^ form sigma f2) P @
map (fn (f1,f2) => form sigma f1 ^ "  ~  "^ form sigma f2) E
fun sub_prec po i =
let fun check ((sl,su)::rs) =
if OpIdEq i su
then union OpIdEq (sl :: sub_prec po sl) (check rs)
else check rs
| check [] = []
in check (fst po)
end
fun sup_prec po i =
let fun check ((sl,su)::rs) =
if OpIdEq i sl
then union OpIdEq (sl :: sup_prec po su) (check rs)
else check rs
| check [] = []
in check (fst po)
end
local
open T
in
fun permutatively_congruent t1 t2 =
TermEq t1 t2
orelse
(not(variable t1 orelse variable t2)
andalso
same_root t1 t2
andalso
permuted (subterms t1) (subterms t2))
and
permuted [] [] = true
| permuted [] _ = false
| permuted (t1::ra1) ra2 =
let fun perm (t2::ras) = if permutatively_congruent t1 t2
then Match t2 else perm ras
| perm [] = NoMatch
in case perm ra2 of
Match t => permuted ra1 (remove TermEq ra2 t)
| NoMatch => false
end
end
end
;
