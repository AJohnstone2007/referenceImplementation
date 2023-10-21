require "$.basis.__list";
require "ut1";
require "utils.sml";
require "term.sml";
require "pretty.sml";
datatype pat =
Pcons of binding
| Pmv of string
| Prel of int
| Papp of pat * (pat list) * (prntVisSort list)
local
val rec pp =
fn Pcons(Bd{bd=(bv,s,c,d),...}) => prs s
| Pmv(s) => prs("["^s^"]")
| Prel(n) => (prs"<"; pri n; prs">")
| Papp(p,args,_) =>
(prs"("; pp p; prs" ";
do_list (fn x => (pp x;prs" ")) args; prs")")
in
fun print_pat pat = (pp pat; line())
end;
local
val reductions = ref ([] : (pat*pat) list)
in
fun init_reductions() = reductions:= []
fun add_reductions rs = reductions:= (!reductions)@rs
fun curnt_reductions() = (!reductions)
end;
val match_debug = ref false;
local
exception nomatch
fun buildMatch nf tm =
let
fun bmnf(c,p as (Pcons _)) = (nf c,p)
| bmnf(c,p as (Papp _)) = (nf c,p)
| bmnf(p as (_,Pmv _)) = p
| bmnf _ = raise Match
fun foo env =
let
fun bar(Ref brc,Pcons brp) =
if sameRef brc brp then env
else raise nomatch
| bar(c,Pmv s) =
(case tryAssoc s env of
SOME x => if tm c x then (s, c) :: env else raise nomatch
| _ => (s, c) :: env)
| bar(App((Ref brc,cas),_),Papp(Pcons brp,pas,_)) =
if sameRef brc brp andalso (length cas) = (length pas)
then
List.foldl
foo_sub
env
(zip(cas,pas))
else raise nomatch
| bar _ = raise nomatch
in
bar
end
and foo_sub(x, a) = foo a (bmnf x)
in
foo
end
fun makeCnstr1(env, (Pcons br)) = Ref(br)
| makeCnstr1(env, Pmv s) =
(case tryAssoc s env of
SOME x => x
| _ => failwith("unbound var `"^s^
"' in RHS of a special reduction"))
| makeCnstr1(env, Papp(p,args,vs)) =
MkApp((makeCnstr1(env, p),map (fn x => makeCnstr1(env, x)) args),vs)
| makeCnstr1 _ = raise Match
fun matchContract(nf, tm, c as App((f,cas),xx)) =
let
val lcas = length cas
val bm = buildMatch nf tm []
fun foo((pi as (Papp(_,pas,_)),po)) =
let
val lpas = length pas
in
if lcas < lpas then raise nomatch
else
let
val (ff,ll) = chop_list lpas cas
val (zz,yy) = chop_list lpas xx
in
MkApp((makeCnstr1((bm
(App((f,ff),zz),pi)), po),ll),yy)
end
end
| foo(pi,po) = makeCnstr1((bm (c,pi)), po)
in
foo
end
| matchContract(nf, tm, c) =
let
val bm = buildMatch nf tm []
fun foo(pi, po) = makeCnstr1((bm (c,pi)), po)
in
foo
end
fun findMatch(x as (nf, tm, c)) =
let
val mc = matchContract x
fun fm con = (SOME (mc con)
handle nomatch => NONE)
fun foo [] = (false, c)
| foo (con :: t) =
(case fm con of
SOME x => (true, x)
| _ => foo t)
in
foo
end
in
fun specialReduce nf tm c =
findMatch (nf, tm, c) (curnt_reductions())
end;
fun makePats nf c =
let
fun stripLocs nams =
fn Bind((Lda,_),n,_,bod)
=> if member n nams then bug"stripLocs"
else stripLocs (n::nams) bod
| p as (Bind((_),_,_,_))
=> (prs"not a pattern: ";legoprint p;
failwith"not a pattern: non-lambda binder")
| (Tuple(Bot,pairs)) => (nams,pairs)
| _ => bug"makePats1"
val timestamp =
let val ts = ref 0 in fn () => (ts:= succ (!ts); !ts) end;
fun mpp nams =
let
fun mp lhsFlg =
fn (Rel n,_) => Pmv (nth nams n)
| (trm,NoShow) => if lhsFlg then Pmv (int_to_string (timestamp()))
else mp lhsFlg (trm,ShowNorm)
| (App((f,aa),vs),_) =>
Papp(mp lhsFlg (f,ShowNorm),map (mp lhsFlg) (zip (aa,vs)),vs)
| (Ref(br),_) => Pcons(br)
| _ => failwith"illegal pattern"
in
fn lhs::rhs::rest => (mp true (nf lhs,ShowNorm),
mp false (rhs,ShowNorm))::(mpp nams rest)
| [] => []
| _ => bug"makePats2"
end
val (nams,pairs) = stripLocs [] c
in
mpp nams pairs
end;
