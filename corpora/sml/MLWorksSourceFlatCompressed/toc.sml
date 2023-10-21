require "utils.sml";
require "universe.sml";
require "term.sml";
require "pretty.sml";
require "type.sml";
fun kind T = case hnf T of Prop => true | Type(_) => true | _ => false;
fun coerceGe c = c;
val toc_debug = ref false;
fun type_of_constr c =
let val T = toc [] c
val _ = if (!toc_debug) then (prs"*toc* "; prnt_vt c T)
else()
in T
end
and toc cxt c =
let
val t = (toc_rec cxt c
handle Failure s => (line(); prs"toc fail on: ";legoprint c;
raise (Failure s)))
val _ = if (!toc_debug) then (prs"*toc1* "; prnt_vt c t)
else()
in t end
and toc_rec cxt c =
case c
of Ref(br) => coerceGe (ref_typ br)
| Prop => mkTyp(uconst 0)
| Type(Uconst n)=> mkTyp(uconst (n+1))
| Type(n) => mkTyp(uvar "" [UniGt(n)])
| Var(_,c) => c
| Rel(n) => coerceGe (typ n cxt)
| Bind((Let,_),_,v,b) =>
coerceGe (toc cxt (subst1 v b))
| App((f,cs),_) =>
let fun toa ft a =
let
val t =
(case hnf ft
of Bind((Pi,_),_,_,r) => coerceGe (subst1 a r)
| _ => bug"toc:application of a non-function")
val _ = if (!toc_debug) then (prs"*tocApp* "; legoprint t)
else()
in t
end
in foldl toa (toc cxt f) cs
end
| Bind((Lda,v),n,d,r) =>
MkBind((Pi,v),n,d,toc (assume d cxt) r)
| Bind((Pi,_),n,d,r) =>
(case (hnf(toc cxt d),hnf(toc (assume d cxt) r))
of (_,Prop) => Prop
| (Prop,Ti as (Type i)) => Ti
| (Type(j),Type(i)) => mkTyp(uvar "" [UniGe(i),UniGe(j)])
| _ => bug"type_of_constr;Pi")
| Bind((Sig,_),_,d,r) =>
(case (hnf (toc cxt d),hnf (toc (d::cxt) r))
of (Prop,Prop) => mkTyp(uconst 0)
| (Prop,Ti as Type(i)) => Ti
| (Ti as Type(i),Prop) => Ti
| (Type(j),Type(i)) => mkTyp(uvar "" [UniGe(i),UniGe(j)])
| _ => bug"type_of_constr;Sig")
| Tuple(T,_) => T
| Proj(Fst,c) =>
(case hnf (toc cxt c)
of Bind((Sig,_),_,d,_) => coerceGe d
| _ => bug"type_of_constr;Fst")
| Proj(Snd,c) =>
(case hnf (toc cxt c)
of Bind((Sig,_),s,_,r) => coerceGe (subst1 (MkProj(Fst,c)) r)
| _ => bug"type_of_constr;Snd")
| Proj(Psn i,c) => bug"toc: general projection not yet implemented"
| Bot => bug"type_of_constr;Bot"
| AbsKind => bug"type_of_constr;AbsKind"
and assume d cxt = d::cxt
and typ n cxt = lift n (nth cxt n);
