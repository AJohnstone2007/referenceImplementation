require "../utils/print";
require "../typechecker/sharetypes";
require "../typechecker/types";
require "../typechecker/valenv";
require "../typechecker/assemblies";
require "../typechecker/nameset";
require "../typechecker/namesettypes";
functor Sharetypes(
structure Print : PRINT
structure Types : TYPES
structure Conenv : VALENV
structure Assemblies : ASSEMBLIES
structure Nameset : NAMESET
structure NamesetTypes : NAMESETTYPES
sharing Assemblies.Basistypes.Datatypes =
Types.Datatypes = Conenv.Datatypes = Nameset.Datatypes
sharing type Nameset.Nameset = NamesetTypes.Nameset =
Assemblies.Basistypes.Nameset
) : SHARETYPES =
struct
structure Assemblies = Assemblies
structure Datatypes = Assemblies.Basistypes.Datatypes
structure NamesetTypes = NamesetTypes
open Datatypes
exception ShareError of string
fun strip (tyfun as ETA_TYFUN (METATYNAME{1=ref(NULL_TYFUN _), ...})) =
tyfun
| strip (ETA_TYFUN (METATYNAME{1=ref tyfun, ...})) = strip tyfun
| strip tyfun = tyfun
fun valenv_of_tyfun(ETA_TYFUN(TYNAME{5=ref valenv, ...})) = valenv
| valenv_of_tyfun(TYFUN(CONSTYPE(_, TYNAME{5=ref valenv, ...}), _)) =
valenv
| valenv_of_tyfun _ = empty_valenv
fun same_tyfun (ETA_TYFUN (METATYNAME{1=r as ref(NULL_TYFUN _), ...}),
ETA_TYFUN (METATYNAME{1=r' as ref(NULL_TYFUN _), ...})) =
r = r'
| same_tyfun (TYFUN (CONSTYPE (_,METATYNAME{1=r as ref (NULL_TYFUN _),
...}),_),
TYFUN (CONSTYPE (_,METATYNAME{1=r' as ref (NULL_TYFUN _),
...}),_)) = r = r'
| same_tyfun (ETA_TYFUN (METATYNAME{1=r as ref(NULL_TYFUN _), ...}),
TYFUN (CONSTYPE (_,METATYNAME{1=r' as ref (NULL_TYFUN _),
...}),_)) = r = r'
| same_tyfun (TYFUN (CONSTYPE (_,METATYNAME{1=r as ref(NULL_TYFUN _),
...}),_),
ETA_TYFUN (METATYNAME{1=r' as ref(NULL_TYFUN _), ...})) =
r = r'
| same_tyfun (tyfun,tyfun') = Types.tyfun_eq (tyfun,tyfun')
fun tystr_consistent (conenv,conenv') =
Conenv.empty_valenvp conenv
orelse
Conenv.empty_valenvp conenv'
orelse
Conenv.dom_valenv_eq (conenv,conenv')
fun update_and_share
(tyfun,tyfun',ce,ce',tyfun_ref,ty_ass,valenv_ref,valenv) =
let
val (ty_ass',count) = Assemblies.remfromTypeAssembly (tyfun, ty_ass)
val (ty_ass'',count') = Assemblies.remfromTypeAssembly (tyfun',ty_ass')
val _ = tyfun_ref := tyfun'
val _ = valenv_ref := valenv
in
(true,Assemblies.add_to_TypeAssembly
((strip tyfun),ce,count,
Assemblies.add_to_TypeAssembly ((strip tyfun'),ce',count',ty_ass'')))
end
fun update_eqrefs (eqref,eqref',neweq) =
(eqref := neweq;
eqref' := neweq)
fun do_share_tyfun(old_definition,
tyfun as ETA_TYFUN (meta as METATYNAME
{1=r as ref (NULL_TYFUN _),
4=b,
5=ref_valenv,...}),
tyfun' as ETA_TYFUN (meta' as METATYNAME
{1=r' as ref (NULL_TYFUN _),
4=b',
5=ref_valenv',...}),ty_ass, nameset) =
let
val (ce as VE (_,vemap),_) = Assemblies.lookupTyfun (tyfun,ty_ass)
val (ce' as VE(_,vemap'),_) = Assemblies.lookupTyfun (tyfun',ty_ass)
val (ref_valenv, valenv) =
if NewMap.is_empty vemap then
(ref_valenv,ce')
else (ref_valenv',ce)
in
if tystr_consistent (ce,ce') then
if Types.arity (tyfun) = Types.arity (tyfun') then
let
val neweq =
if (not (NewMap.is_empty vemap)
andalso
not (NewMap.is_empty vemap'))
then
(!b) andalso (!b')
else
(!b) orelse (!b')
in
if Nameset.member_of_tynames(meta, nameset) then
(if not(neweq = (!b))
then raise ShareError "incompatible equality attributes"
else ();
update_eqrefs(b,b',neweq);
update_and_share (tyfun',tyfun,ce',ce,r',ty_ass,
ref_valenv,valenv))
else
(if Nameset.member_of_tynames(meta',nameset) andalso
not(neweq = (!b'))
then raise ShareError "incompatible equality attributes"
else ();
update_eqrefs(b,b',neweq);
update_and_share (tyfun,tyfun',ce,ce',r,ty_ass,ref_valenv,valenv))
end
else
raise ShareError "different arities"
else
raise ShareError "inconsistent value constructors"
end
| do_share_tyfun(old_definition,
tyfun as ETA_TYFUN (METATYNAME{1=r as ref(NULL_TYFUN _),
4=b as ref eq,
5=ref_valenv, ...}),
tyfun',ty_ass, nameset) =
if old_definition then
let
val (ce,_) = Assemblies.lookupTyfun (tyfun,ty_ass)
val (ce',_) = Assemblies.lookupTyfun (tyfun',ty_ass)
in
if tystr_consistent (ce,ce') then
if Types.arity (tyfun) = Types.arity (tyfun') then
let
val eq' = Types.equalityp tyfun'
in
if (eq andalso eq') orelse not eq then
(b := eq';
update_and_share (tyfun,tyfun',ce,ce',r,ty_ass,
ref_valenv, ce'))
else
raise ShareError "incompatible equality attributes"
end
else
raise ShareError "different arities"
else
raise ShareError "inconsistent value constructors"
end
else
raise ShareError "sharing with rigid names"
| do_share_tyfun(old_definition,
tyfun,tyfun' as ETA_TYFUN(METATYNAME
{1=r as ref(NULL_TYFUN _),
4=b as ref eq,
5=ref_valenv,...}),
ty_ass, nameset) =
if old_definition then
let
val (ce,_) = Assemblies.lookupTyfun (tyfun,ty_ass)
val (ce',_) = Assemblies.lookupTyfun (tyfun',ty_ass)
in
if tystr_consistent (ce,ce') then
if Types.arity (tyfun) = Types.arity (tyfun') then
let
val eq' = Types.equalityp tyfun
in
if (eq andalso eq') orelse not eq then
(b := eq';
update_and_share (tyfun',tyfun,ce',ce,r,ty_ass,
ref_valenv,ce))
else
raise ShareError "incompatible equality attributes"
end
else
raise ShareError "different arities"
else
raise ShareError "inconsistent value constructors"
end
else
raise ShareError "sharing with rigid names"
| do_share_tyfun(old_definition,tyfun, tyfun', ty_ass, nameset) =
(false,ty_ass)
fun get_meta(ETA_TYFUN meta) = meta
| get_meta tyfun =
METATYNAME(ref tyfun, "Bad tyname", 0, ref false, ref empty_valenv,ref false)
fun share_tyfun (old_definition,tyfun, tyfun', ty_ass, nameset) =
let
val tyfun = strip tyfun
val tyfun' = strip tyfun'
in
if same_tyfun(tyfun, tyfun')
then (true, ty_ass)
else
let
val null = Types.null_tyfunp tyfun
val null' = Types.null_tyfunp tyfun'
in
if null orelse null' then
let
val bad =
(not null) orelse Nameset.member_of_tynames(get_meta tyfun, nameset)
val bad' =
(not null') orelse Nameset.member_of_tynames(get_meta tyfun', nameset)
in
if bad andalso bad' then
raise ShareError "different rigid types"
else
do_share_tyfun(old_definition,tyfun, tyfun', ty_ass, nameset)
end
else
raise ShareError "different rigid types"
end
end
end
;
