require "../basis/__list";
require "../basis/__string";
require "../basics/ident";
require "module_name";
functor ModuleName (structure Ident : IDENT): MODULE_NAME =
struct
structure Ident = Ident
structure Symbol = Ident.Symbol
type symbol = Symbol.Symbol
type namespace = symbol ref
exception ModuleNameError and PathError
type t = { namespace: namespace, name: symbol }
fun equal ({namespace=ns1, name=n1}, {namespace=ns2, name=n2}) =
ns1 = ns2 andalso Symbol.eq_symbol(n1, n2)
fun namespaceOf {namespace, name} = namespace
fun symbolOf {namespace, name} = name
fun nameOf {namespace, name} = Symbol.symbol_name name
fun lt ({namespace=ref ns1, name=n1}, {namespace=ref ns2, name=n2}) =
Symbol.symbol_lt(ns1, ns2)
orelse (Symbol.eq_symbol(ns1, ns2) andalso Symbol.symbol_lt(n1, n2))
val STRspace = ref(Symbol.find_symbol "structure")
val SIGspace = ref(Symbol.find_symbol "signature")
val FCTspace = ref(Symbol.find_symbol "functor")
fun makestring {namespace, name} =
(if namespace = STRspace
then "structure "
else if namespace = SIGspace
then "signature "
else if namespace = FCTspace
then "functor "
else raise ModuleNameError)
^ (Symbol.symbol_name name)
fun structMN name = {namespace = STRspace, name = Symbol.find_symbol name}
fun sigMN name = {namespace = SIGspace, name = Symbol.find_symbol name}
fun functMN name = {namespace = FCTspace, name = Symbol.find_symbol name}
fun create (ns, n) = {namespace = ns, name = Symbol.find_symbol n}
type path = t list
fun pathFirstModule [] = raise PathError
| pathFirstModule (h :: _) = h
fun restOfPath [] = NONE
| restOfPath [_] = NONE
| restOfPath (_ :: t) = SOME t
fun pathLastModule [] = raise PathError
| pathLastModule [m] = m
| pathLastModule (_ :: t) = pathLastModule t
fun mnListOfPath p = p
fun pathOfMNList l = l
fun createPathSML (sl, x) =
foldl (fn (s, p) => (structMN s) :: p) [x] sl
fun nameOfPath [] = raise PathError
| nameOfPath [m] = nameOf m
| nameOfPath (h :: t) = (nameOf h) ^ "." ^ (nameOfPath t)
type set = t list
fun is_member(_, []) = false
| is_member(a,h::t) = equal(a, h) orelse is_member (a,t)
fun memberOf s mn = is_member(mn, s)
fun singleton mn = [mn]
fun add_member (m,l) = if is_member (m,l) then l else m::l
fun add (m,l) = if is_member (m,l) then l else m::l
fun addl (s1, s2) = List.foldr add s2 s1
fun makeset l = List.foldr add [] l
fun makelist l = l
val empty = []
fun isEmpty [] = true | isEmpty _ = false
val fold = List.foldr
fun union ([],l) = l
| union (h::t,l) = union (t,add_member (h,l))
fun intersection(set1,set2) =
let
fun intersect(result,h::t,set) =
if is_member (h,set)
then intersect(h::result,t,set)
else intersect(result,t,set)
| intersect(result,[],set) = result
in
case set2 of
[] => []
| _ => intersect([],set1,set2)
end
fun difference ([],_) = []
| difference (h::t,l) =
if is_member (h,l)
then difference(t,l) else h::difference(t,l)
fun sameSet (s1, s2) =
let fun inc([], s) = true
| inc(h::t, s) = is_member(h, s) andalso inc(t,s)
in inc(s1, s2) andalso inc(s2, s1)
end
fun setToString l =
let fun loop [] = [""]
| loop [h] = [makestring h]
| loop (h::t) = (makestring h) :: ", " :: loop t
in String.concat (loop l)
end
end
;
