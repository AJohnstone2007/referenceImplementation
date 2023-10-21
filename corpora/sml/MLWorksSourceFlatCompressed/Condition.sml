functor Condition(structure NameSubstitution: NAMESUBSTITUTION
structure Formula: FORMULA
sharing NameSubstitution.N = Formula.ACT.N): CONDITION =
struct
structure NS = NameSubstitution
structure F = Formula
type general_cond = (NS.N.name list) list *
(NS.N.name * NS.N.name) list
datatype cond = NameSubst of NS.name_subst |
GeneralCond of general_cond
exception cannot_happen
fun mk_cond1 nil = (nil,nil) |
mk_cond1 (nil::ns) = mk_cond1 ns |
mk_cond1 ((x::nl)::ns) =
let val ineqs = McList.headers ns;
val (EQ,NEQ) = mk_cond1 ns;
fun set_neq x nil = NEQ |
set_neq x (y::nl) = (x,y)::(set_neq x nl)
in (nl::EQ,set_neq x ineqs) end
fun mk_cond ns = GeneralCond(mk_cond1 (NS.unpack ns))
fun eq (NameSubst nc1) (NameSubst nc2) = NS.eq nc1 nc2
| eq (GeneralCond (eq1,neq1)) (GeneralCond (eq2,neq2)) =
let fun sorteq eq =
Lib.sort (fn (x,y)=>NS.N.le(hd x,hd y)) (map (Lib.sort NS.N.le) eq)
and sortneq neq =
Lib.sort (fn ((a,c),(b,d)) =>
NS.N.le(a,b) andalso
(not(NS.N.le(b,a)) orelse NS.N.le(c,d))) neq
in
Lib.eq (Lib.eq NS.N.eq) (sorteq eq1,sorteq eq2)
andalso
Lib.eq (fn ((a,c),(b,d)) => NS.N.eq(a,b) andalso NS.N.eq(c,d))
(sortneq neq1,sortneq neq2)
end
| eq _ _ = raise Match
fun entails (NameSubst ns1) (NameSubst ns2) =
let val nl = NS.domain ns2
in NS.eq (NS.restrict ns1 nl) ns2 end
| entails _ _ = raise Match
fun diff1 x nil = nil |
diff1 x (nl::nc) =
if McList.member NS.N.curry_eq x nl
then
case nl of
nil => raise cannot_happen |
(y::nl1) =>
if NS.N.eq (x, y)
then diff1 x (nl1::nc)
else (F.mk_eq x y)::(diff1 x nc)
else
case nl of
nil => diff1 x nc |
(y::nl1) => (F.mk_ineq x y)::(diff1 x nc)
fun diff x (NameSubst ns) =
let fun build_and nil = F.mk_eq x x |
build_and (F::nil) = F |
build_and (F::fl) = F.mk_and F (build_and fl)
in build_and (diff1 x (NS.unpack ns)) end
| diff x (GeneralCond nc) = raise Match
fun mk_form1 nil = F.mk_true |
mk_form1 (nil::l) = mk_form1 l |
mk_form1 ((x::nil)::nil) = F.mk_true |
mk_form1 ((x::nil)::nil::ns) = mk_form1 ((x::nil)::ns) |
mk_form1 ((x::nil)::(y::nl)::ns) =
F.mk_and (F.mk_ineq x y) (mk_form1 ((y::nl)::ns)) |
mk_form1 ((x::y::nl)::ns) =
F.mk_and (F.mk_eq x y) (mk_form1 ((y::nl)::ns))
fun mk_form (NameSubst ns) = mk_form1 (NS.unpack ns)
| mk_form (GeneralCond (eq,neq)) =
F.mk_and (fold (fn (h::t,f) =>
F.mk_and (fold (fn (x,f)=>F.mk_and (F.mk_eq h x) f)
t F.mk_true)
f
| ([],f) => f) eq F.mk_true)
(fold (fn ((a,b),f) => F.mk_and (F.mk_ineq a b) f) neq F.mk_true)
fun domain (NameSubst ns) = NS.domain ns |
domain (GeneralCond(EQ,_)) = McList.flatten EQ
fun consistent nil nl NEQ = true |
consistent nl nil NEQ = true |
consistent (x1::nl1) (x2::nl2) NEQ =
McList.member (fn (x1,y1) => fn (x2,y2) =>
NS.N.eq (x1, x2) andalso NS.N.eq (y1, y2)) (x1,x2) NEQ
fun select nl nil NEQ = nil |
select nl (nl1::ns1) NEQ =
if consistent nl nl1 NEQ
then (NS.pack ((nl@nl1)::ns1))::(select nl ns1 NEQ)
else (NS.pack (nl1::ns1))::(select nl ns1 NEQ)
fun partition1 (nil,NEQ) = [NS.init] |
partition1 (nl::l,NEQ) =
McList.del_dup NS.eq (McList.flatten (map
(fn ns =>
(NS.pack (nl::(NS.unpack ns)))::
(select nl (NS.unpack ns) NEQ))
(partition1 (l,NEQ))))
fun enlarge_domain nil c = c |
enlarge_domain (x::nl) c =
if McList.member NS.N.curry_eq x (domain (GeneralCond c))
then enlarge_domain nl c
else case (enlarge_domain nl c) of
(EQ,NEQ) => ([x]::EQ,NEQ)
fun partition nl (NameSubst ns) = NS.partition nl ns |
partition nl (GeneralCond c) =
McList.del_dup NS.eq (partition1 (enlarge_domain (McList.del_dup NS.N.curry_eq nl) c))
end
;
