require "../utils/lists";
require "../utils/print";
require "../utils/crash";
require "../basics/identprint";
require "../typechecker/valenv";
require "../typechecker/strnames";
require "../typechecker/nameset";
require "../typechecker/sharetypes";
require "../typechecker/share";
functor Share(
structure Lists : LISTS
structure Print : PRINT
structure Crash : CRASH
structure IdentPrint : IDENTPRINT
structure Sharetypes : SHARETYPES
structure Valenv : VALENV
structure Strnames : STRNAMES
structure Nameset : NAMESET
sharing Sharetypes.Assemblies.Basistypes.Datatypes = Valenv.Datatypes =
Nameset.Datatypes = Strnames.Datatypes
sharing IdentPrint.Ident = Valenv.Datatypes.Ident
sharing type Sharetypes.Assemblies.Basistypes.Nameset = Nameset.Nameset
) : SHARE =
struct
structure Datatypes = Valenv.Datatypes
structure Assemblies = Sharetypes.Assemblies
structure BasisTypes = Assemblies.Basistypes
structure Nameset = Nameset
open Datatypes
fun ordered_sublist([], _) = true
| ordered_sublist(first as (x :: xs), y :: ys) =
if x = y then
ordered_sublist(xs, ys)
else
ordered_sublist(first, ys)
| ordered_sublist _ = false
exception ShareError of string
val share_failures = ref []
val old_share_failures = ref []
val failure_reasons = ref []
fun one_type_share(tycon, (tyfun, _), type_offspring, ty_ass, nameset) =
if Assemblies.inTypeOffspringDomain(tycon, type_offspring) then
let
val (tyfun',_) = Assemblies.lookupTyCon (tycon, type_offspring)
in
Sharetypes.share_tyfun (true,tyfun,tyfun',ty_ass, nameset)
handle Sharetypes.ShareError s =>
(share_failures:=tycon::(!share_failures);
failure_reasons:= s::(!failure_reasons);
(true,ty_ass))
end
else (true, ty_ass)
and str_consistent ([],_,_,str_ass,ty_ass,_) = (true,str_ass,ty_ass)
| str_consistent (strid::strids,str_offspring,str_offspring',str_ass,
ty_ass,nameset) =
let
val (strshare_successful,str_ass',ty_ass') =
if Assemblies.inStrOffspringDomain(strid, str_offspring') then
share_str(#1 (Assemblies.lookupStrId (strid,str_offspring)),
#1 (Assemblies.lookupStrId (strid,str_offspring')),
str_ass,ty_ass,nameset)
else (true,str_ass,ty_ass)
val (strshare_successful',str_ass'',ty_ass') =
str_consistent (strids,str_offspring,str_offspring',str_ass',
ty_ass',nameset)
in
if strshare_successful andalso strshare_successful' then
(true,str_ass'',ty_ass')
else (false,str_ass,ty_ass)
end
and consistent (name,name',str_ass,ty_ass,nameset) =
let
val (str_offspring,type_offspring) =
Assemblies.lookupStrname (name,str_ass)
val stridlist = Assemblies.getStrIds str_offspring
val tyconlist = Assemblies.getTyCons type_offspring
val (str_offspring',type_offspring') =
Assemblies.lookupStrname (name',str_ass)
val (strshare_successful,str_ass',ty_ass') =
str_consistent (stridlist,str_offspring,str_offspring',
str_ass,ty_ass,nameset)
val _ = old_share_failures:=[]
fun findFixpoint () =
(share_failures:=[];
failure_reasons:=[];
let val answer =
NewMap.fold
(fn (res as (ok, ty_ass), tycon, ran) =>
if ok then
one_type_share(tycon, ran, type_offspring', ty_ass, nameset)
else res)
((true, ty_ass'),Assemblies.getTypeOffspringMap type_offspring)
in
if !old_share_failures = !share_failures
then
if !share_failures=[]
then answer
else
let
fun makeE([],[]) = ""
| makeE([h1],[h2]) =
(IdentPrint.printTyCon h1)^": "^h2^"}"
| makeE(h1::t1,h2::t2) =
(IdentPrint.printTyCon h1)^": "^h2^"\n    "^
(makeE(t1,t2))
| makeE _ = Crash.impossible "makeE"
fun makeErrorMesg() =
if Lists.length (!share_failures)=1
then
("Cannot share types with type constructor " ^
(IdentPrint.printTyCon
(Lists.hd(!share_failures))) ^ ": " ^
(Lists.hd(!failure_reasons)))
else
"Cannot share types with type constructors\n   {"^
makeE(!share_failures,!failure_reasons)
in
raise ShareError (makeErrorMesg())
end
else (old_share_failures:= !share_failures;
findFixpoint())
end)
val (tyshare_successful,ty_ass'') = findFixpoint()
in
if strshare_successful andalso tyshare_successful then
(true,str_ass',ty_ass'')
else (false,str_ass,ty_ass)
end
and share_str' (name as METASTRNAME (r as ref (NULLNAME _)),
name' as METASTRNAME (r' as ref (NULLNAME _)),
str_ass,ty_ass,nameset) =
if Strnames.strname_eq (name, name') then
(true,str_ass,ty_ass)
else
let
val (consist,str_ass',ty_ass') =
consistent (name,name',str_ass,ty_ass,nameset)
in
if consist then
let
val (str_offspring,type_offspring) =
Assemblies.lookupStrname (name,str_ass')
val (str_offspring',type_offspring') =
Assemblies.lookupStrname (name',str_ass')
val str_ass'' = Assemblies.remfromStrAssembly
(name,Assemblies.remfromStrAssembly (name',str_ass'))
in
(if Nameset.member_of_strnames(name, nameset) then
r' := name
else
r := name');
(true,
Assemblies.add_to_StrAssembly
(name, str_offspring, type_offspring,
Assemblies.add_to_StrAssembly
(name', str_offspring', type_offspring',
str_ass'')),
ty_ass')
end
else
raise ShareError "impossible type error 17: structures are not consistent"
end
| share_str' (name as METASTRNAME (r as ref (NULLNAME _)),
name',str_ass,ty_ass,nameset) =
let
val (consist,str_ass',ty_ass') =
consistent (name,name',str_ass,ty_ass,nameset)
in
if consist then
if cover (name',name,str_ass',nameset) then
let
val (str_offspring,type_offspring) =
Assemblies.lookupStrname (name,str_ass')
val str_ass'' = Assemblies.remfromStrAssembly (name,
str_ass')
in
(r := name';
(true,
Assemblies.add_to_StrAssembly
(name,str_offspring,type_offspring,
str_ass''),ty_ass'))
end
else
raise ShareError "Basis does not cover structure"
else
raise ShareError "impossible type error 18: structures are not consistent"
end
| share_str' (name,name' as METASTRNAME (r as ref (NULLNAME _)),
str_ass,ty_ass,nameset) =
let
val (consist,str_ass',ty_ass') =
consistent (name,name',str_ass,ty_ass,nameset)
in
if consist then
if cover (name,name',str_ass',nameset) then
let
val (str_offspring,type_offspring) =
Assemblies.lookupStrname (name',str_ass')
val str_ass'' = Assemblies.remfromStrAssembly (name',
str_ass')
in
(r := name;
(true,
Assemblies.add_to_StrAssembly
(name',str_offspring,type_offspring,
str_ass''),ty_ass'))
end
else
raise ShareError "Basis does not cover structure"
else
raise ShareError "impossible type error 19: structures are not consistent"
end
| share_str' (name,name',str_ass,ty_ass,_) =
if Strnames.strname_eq (name,name') then
(true,str_ass,ty_ass)
else
raise ShareError "Rigid structures are not equal"
and cover (name,name' as METASTRNAME (ref (NULLNAME _)),str_ass,nameset) =
let
val (str_offspring,type_offspring) =
Assemblies.lookupStrname (name,str_ass)
val (str_offspring',type_offspring') =
Assemblies.lookupStrname (name',str_ass)
in
if Nameset.member_of_strnames (name,nameset) then
let
val stridlist = Assemblies.getStrIds str_offspring
val tyconlist = Assemblies.getTyCons type_offspring
val stridlist' = Assemblies.getStrIds str_offspring'
val tyconlist' = Assemblies.getTyCons type_offspring'
in
ordered_sublist (stridlist',stridlist) andalso
ordered_sublist (tyconlist',tyconlist)
end
else true
end
| cover _ = Crash.impossible "cover bad parameters"
and share_str(name,name',str_ass,ty_ass,nameset) =
let
val name = Strnames.strip name
val name' = Strnames.strip name'
in
if Nameset.member_of_strnames(name, nameset) andalso
Nameset.member_of_strnames(name', nameset) then
if Strnames.strname_eq (name, name') then
(true,str_ass,ty_ass)
else
raise ShareError "Rigid structures are not equal"
else
share_str'(name, name',str_ass,ty_ass, nameset)
end
end
;
