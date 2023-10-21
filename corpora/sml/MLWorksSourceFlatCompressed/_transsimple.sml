require "../utils/lists";
require "../utils/crash";
require "../main/pervasives";
require "simpleutils";
require "transsimple";
functor TransSimple ( structure Lists : LISTS
structure Crash : CRASH
structure SimpleUtils : SIMPLEUTILS
structure Pervasives : PERVASIVES
sharing type SimpleUtils.LambdaTypes.Primitive = Pervasives.pervasive
) : TRANSSIMPLE =
struct
structure LambdaTypes = SimpleUtils.LambdaTypes
structure Ident = LambdaTypes.Ident
structure Location = Ident.Location
structure Array = MLWorks.Internal.Array
open LambdaTypes
val do_diag = true
val diag_level = 1
fun diag (level,f) =
if do_diag andalso level < diag_level then print(f()) else ()
fun make_app (e1,e2,ty) =
APP (e1,([e2],[]),ty)
fun make_closure_ty ty = NONE
fun make_builtin_call (b,e,ty) =
APP (BUILTIN b,([e],[]),ty)
fun trans_back exp =
case exp of
VAR n => VAR n
| FN stuff => trans_fun stuff
| LET ((n,info,e1),e2) => LET ((n,info,trans_back e1),trans_back e2)
| LETREC (fl,el,e) => LETREC (fl,map trans_back el,trans_back e)
| APP (BUILTIN Pervasives.CALL_C,([e],[]),ty) =>
APP (BUILTIN Pervasives.CALL_C,([trans_back e],[]),ty)
| APP (BUILTIN b,([e],[]),ty) => make_builtin_call (b,trans_back e,ty)
| APP (BUILTIN b,(el,[]),ty) => make_builtin_call (b,STRUCT (map trans_back el,TUPLE),ty)
| APP (BUILTIN b, (el,_),ty) => Crash.impossible "fp regs in app builtin"
| APP (f,(el,fpel),ty) =>
APP (trans_back f,(map trans_back el, map trans_back fpel), ty)
| SCON scon => SCON scon
| MLVALUE mlvalue => MLVALUE mlvalue
| INT i => INT i
| SWITCH (INT i,info,tel,opte) =>
let
fun find_case ([],NONE) = Crash.impossible "trans_simple: No application case in switch"
| find_case ([],SOME e) = e
| find_case ((IMM_TAG (_,j),e)::rest,opte) =
if i = j then e else find_case (rest,opte)
| find_case ((tag,e)::rest,opte) =
find_case (rest,opte)
in
trans_back (find_case (tel,opte))
end
| SWITCH (e,info,tel,opte) =>
let
fun trans_choice (tag,e) =
let
val tag' =
case tag of
VCC_TAG i => VCC_TAG i
| IMM_TAG i => IMM_TAG i
| SCON_TAG scon => SCON_TAG scon
| EXP_TAG exp => EXP_TAG (trans_back exp)
in
(tag',trans_back e)
end
fun trans_opte NONE = NONE
| trans_opte (SOME e) = SOME (trans_back e)
in
SWITCH (trans_back e,
info,
map trans_choice tel,
trans_opte opte)
end
| STRUCT (expl,ty) =>
STRUCT (map trans_back expl, ty)
| SELECT ({index,size,selecttype},exp) =>
SELECT ({index = index,
size = size,
selecttype = selecttype},
trans_back exp)
| RAISE exp => RAISE (trans_back exp)
| HANDLE (exp1,exp2,s) => HANDLE (trans_back exp1,trans_back exp2,s)
| BUILTIN b => BUILTIN b
and trans_fun (args,body,status,name,ty,info) =
FN (args,trans_back body,status, name,ty,info)
fun make_functor (arg,name,body) =
FN (([arg],[]),body,FUNC, name,null_type_annotation,user_funinfo)
fun stuff_body (args,body,status,name,ty,info) = body
fun trans_decs ([],e) = trans_back e
| trans_decs ((i,info,VAL (FN stuff)) :: decs,body) =
trans_fndecs (decs,body,[(i,info,stuff)])
| trans_decs ((v,info,(VAL e)) :: decs,body) =
LET ((v,info,trans_back e),trans_decs (decs,body))
| trans_decs ((v,info,FUNCTOR (x,name,prog)) :: rest, body) =
LET ((v,info,make_functor (x,name,trans_decs prog)),
trans_decs (rest,body))
and do_funs (body,funs) =
let
val funs = rev funs
val ids = map #1 funs
val augfuns = map (fn (id,info,stuff) => (id,info,stuff,SimpleUtils.freevars (stuff_body stuff,ids))) funs
fun partition sets =
let
val setarray = Array.arrayoflist sets
val num_items = Array.length setarray
fun find item =
let
fun aux n =
if n >= num_items
then Crash.impossible "find item"
else if item = #1 (Array.sub (setarray,n))
then n
else aux (n+1)
in
aux 0
end
val leaders = Array.tabulate (num_items,fn n => n)
fun leader i = Array.sub (leaders,i)
val partitions = Array.tabulate (num_items,fn n => [n])
fun partition i = Array.sub (partitions,i)
val canonical_sets =
map (fn (item,a,b,itemlist) => (find item,map find itemlist)) sets
fun revapp (a::b) acc = revapp b (a::acc)
| revapp [] acc = acc
fun merge ([],rest,acc) = revapp acc rest
| merge (rest,[],acc) = revapp acc rest
| merge ((a::b),(c::d),acc) =
if a < c then merge (b,c::d,a::acc)
else merge (a::b,d,c::acc)
fun doone (item1,itemlist) =
app
(fn item2 =>
let
val l1 = leader item1
val l2 = leader item2
in
if l1 = l2 then ()
else
let
val set = merge (partition l1, partition l2,[])
in
Array.update (partitions,l1,set);
app
(fn i => Array.update (leaders,i,l1))
set
end
end)
itemlist
val set_them = app doone canonical_sets;
val done = Array.array (num_items,false)
fun foo (n,done,acc) =
if n >= num_items then rev acc
else
let
val l = leader n
in
if Lists.member (l,done)
then foo (n+1,done,acc)
else
foo (n+1,l::done,map (fn i => (Array.sub (setarray,i))) (partition l) :: acc)
end
in
foo (0,[],[])
end
val partitions = partition augfuns
datatype 'a fnset = NONREC of 'a | REC of 'a list
val decsets =
map
(fn [(id,info,stuff,[])] => NONREC (id,info,stuff)
| l => REC (map (fn (id,info,stuff,_) => (id,info,stuff)) l))
partitions
fun make_decs [] = body
| make_decs (NONREC (id,info,stuff) :: rest) =
LET ((id,info,trans_fun stuff),
make_decs rest)
| make_decs (REC l :: rest) =
let
val ids = map #1 l
val infos = map #2 l
val bodies = map trans_fun (map #3 l)
in
LETREC (Lists.zip (ids,infos),bodies,make_decs rest)
end
in
make_decs decsets
end
and trans_fndecs ([],body,acc) = do_funs (trans_back body,acc)
| trans_fndecs ((v,info,VAL (FN stuff)) :: decs,body,acc) =
trans_fndecs (decs,body,(v,info,stuff) :: acc)
| trans_fndecs (decs,body,acc) =
do_funs (trans_decs (decs,body),acc)
fun trans_program (PROGRAM p) = trans_decs p
end
;
