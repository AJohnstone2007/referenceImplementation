functor NameSubstitution(structure Name: NAME
structure Boolean: TEST
sharing Boolean.N = Name):NAMESUBSTITUTION =
struct
structure N = Name
structure B = Boolean
type name_subst = (N.name list) list
exception bound_name_expected of N.name * N.name
exception unbound_name_expected of N.name
exception cannot_happen
fun eq nil nil = true |
eq (nl::nc1) nc =
let val (was_removed,nc2) = McList.l_rm_and_tell (McList.l_eq N.curry_eq) nl nc
in if was_removed then eq nc1 nc2 else false end |
eq _ _ = false
fun mkstr ns =
"["^(Lib.mapconcat (fn h=>"["^(Lib.mapconcat N.mkstr h "=")^"]")
ns ",")^"]"
fun domain ns = McList.flatten ns
val init = []
fun if_bound t x nil = raise bound_name_expected(x,x) |
if_bound t x (l::r) =
if McList.member N.curry_eq x l then t else if_bound t x r
fun if_unbound t x nil = t |
if_unbound t x (l::r) =
if McList.member N.curry_eq x l
then raise unbound_name_expected(x)
else if_unbound t x r
fun is_eq x y nil = raise bound_name_expected(x,y) |
is_eq x y (l::r) =
if McList.member N.curry_eq x l
then if McList.member N.curry_eq y l
then true
else if_bound false y r
else if McList.member N.curry_eq y l
then if_bound false x r
else is_eq x y r
fun is_neq x y nil = raise bound_name_expected(x,y) |
is_neq x y (l::r) =
if McList.member N.curry_eq x l
then if McList.member N.curry_eq y l
then false
else if_bound true y r
else if McList.member N.curry_eq y l
then if_bound true x r
else is_neq x y r
fun restrict ns l =
let fun intersect l m =
Lib.filter (fn x=>Lib.member N.eq (x,l)) m
fun rmnil l =
Lib.filter (fn x=>not(Lib.isnil x)) l
in
rmnil (map (intersect l) ns)
end
fun add_distinct x nc =
let val _ = if not(Flags.trace()) then ()
else print("*add_distinct "^(N.mkstr x)^" "^(mkstr nc)^"\n")
in if_unbound ([x]::nc) x nc end
fun add_new x nc =
let
val _ = if not(Flags.trace()) then ()
else print("*add_new "^(N.mkstr x)^" "^(mkstr nc)^"\n")
fun add_existing x nil = nil |
add_existing x (al::nc) =
((x::al)::nc)::
(map (fn nc1 => al::nc1) (add_existing x nc))
in if_unbound (([x]::nc)::(add_existing x nc)) x nc end
fun partition1 nil c = [c] |
partition1 (x::nl) c =
if McList.member N.curry_eq x (domain c)
then [c]
else
let val _ = if not(Flags.trace()) then ()
else print("*partition1 ["^(Lib.mapconcat N.mkstr (x::nl) ",")^"] "^(mkstr c)^"\n")
in McList.flatten(map (partition1 nl) (add_new x c)) end
fun partition nl ns = McList.del_dup eq (partition1 (McList.del_dup N.curry_eq nl) ns)
fun pack ns = ns
fun unpack ns = ns
fun entails ns b =
B.implies (fold (fn (s,t) =>
B.join(t,(fold (fn (x,r) =>
B.join(r,B.match(x,Lib.hd s)))
(Lib.tl s) B.True)))
ns B.True,
b)
fun private x nil = nil |
private x (nl::ns) =
case McList.l_rm_and_tell N.curry_eq x nl of
(true,nil) => [x]::ns |
(true,nl1) => [x]::nl1::ns |
(false,nl1) => nl1 :: (private x ns)
end;
