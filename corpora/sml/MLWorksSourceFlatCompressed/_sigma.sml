require "../typechecker/strnames";
require "../typechecker/nameset";
require "../typechecker/types";
require "../typechecker/scheme";
require "../typechecker/environment";
require "../typechecker/basistypes";
require "stamp";
require "../typechecker/sigma";
functor Sigma (
structure Strnames : STRNAMES
structure Nameset : NAMESET
structure Types : TYPES
structure Scheme : TYPESCHEME
structure Env : ENVIRONMENT
structure BasisTypes: BASISTYPES
structure Stamp : STAMP
sharing Nameset.Options = Types.Options = Scheme.Options
sharing Nameset.Datatypes = Env.Datatypes
= Scheme.Datatypes = Strnames.Datatypes = Types.Datatypes
= BasisTypes.Datatypes
sharing type BasisTypes.Nameset = Nameset.Nameset
sharing type Types.Datatypes.Stamp = Stamp.Stamp
sharing type Types.Datatypes.StampMap = Stamp.Map.T
) : SIGMA =
struct
structure BasisTypes = BasisTypes
structure Options = Nameset.Options
open BasisTypes.Datatypes
exception GetTyname
fun get_tyname (ETA_TYFUN name) = name
| get_tyname _ = raise GetTyname
fun tyname_strip(METATYNAME{1=ref(ETA_TYFUN tyname), ...}) = tyname_strip tyname
| tyname_strip tyname = tyname
fun new_names_of str =
let
fun str_names (STR (name,_,env),nameset) =
let
val newname = Strnames.strip name
in
if (Env.empty_envp env) then
if (Strnames.uninstantiated newname) then
Nameset.add_strname (newname,nameset)
else nameset
else
if (Strnames.uninstantiated newname) then
env_names (env,Nameset.add_strname (newname,nameset))
else
env_names (env,nameset)
end
| str_names (COPYSTR((smap,tmap),str),nameset) =
let
fun do_strname (nameset,_,strname) =
if Strnames.uninstantiated strname
then Nameset.add_strname (strname,nameset)
else nameset
fun do_tyname (nameset,_,TYNAME _) = nameset
| do_tyname (nameset,_,tyname as (METATYNAME (ref tyfun,_,_,_,_,_))) =
if Types.null_tyfunp tyfun
then (Nameset.add_tyname (tyname,nameset))
else nameset
in
Stamp.Map.fold do_strname (Stamp.Map.fold do_tyname (nameset,tmap),smap)
end
and env_names (ENV (SE se_map,TE te_map, VE (_, ve_map)),nameset) =
let
fun gather_tynames (nameset, _, TYSTR (tyfun,VE (_,amap))) =
let
val nameset' = NewMap.fold gather_ve_tynames (nameset, amap)
in
if Types.null_tyfunp tyfun then
Nameset.add_tyname (tyname_strip (Types.name tyfun), nameset')
else
nameset'
end
and gather_strnames (nameset, _, str) = str_names (str,nameset)
and gather_ve_tynames (nameset, _, scheme) =
Nameset.tynames_in_nameset(Scheme.gather_tynames(scheme), nameset)
val new_nameset = NewMap.fold gather_tynames (nameset, te_map)
val new_nameset' = NewMap.fold gather_strnames (new_nameset, se_map)
in
NewMap.fold gather_ve_tynames (new_nameset', ve_map)
end
in
str_names (str,Nameset.empty_nameset())
end
local
fun str_names (STR (name,_,env),nameset) =
if (Env.empty_envp env) then
Nameset.add_strname (Strnames.strip name,nameset)
else
env_names (env,Nameset.add_strname (Strnames.strip name,nameset))
| str_names (COPYSTR((smap,tmap),str),nameset) =
str_names (Env.str_copy (str,smap,tmap),nameset)
and env_names (ENV (SE se_map,TE te_map,VE (_, ve_map)),nameset) =
let
fun gather_tynames(nameset, _, TYSTR (tyfun, _)) =
if Types.has_a_name tyfun then
Nameset.add_tyname (tyname_strip(Types.name tyfun), nameset)
else
nameset
fun gather_strnames (nameset, _, str) =
str_names (str, nameset)
fun gather_ve_tynames (nameset, _, scheme) =
Nameset.tynames_in_nameset (Scheme.gather_tynames (scheme), nameset)
val new_nameset = NewMap.fold gather_tynames (nameset, te_map)
val new_nameset' = NewMap.fold gather_strnames (new_nameset, se_map)
in
NewMap.fold gather_ve_tynames (new_nameset', ve_map)
end
in
fun names_of str = str_names (str,Nameset.empty_nameset())
fun names_of_env env = env_names (env,Nameset.empty_nameset())
end
fun string_sigma options (BasisTypes.SIGMA (nameset,str)) =
"SIGMA (" ^ Nameset.string_nameset options nameset ^ "{\n" ^ (Env.string_str str) ^ "})\n"
fun string_phi options (BasisTypes.PHI (nameset,(str,sigma))) =
"PHI (" ^ Nameset.string_nameset options nameset ^ ")" ^
"(" ^ Env.string_str str ^ "\n  =>\n  " ^ string_sigma options sigma ^ ")\n"
fun print_map (strname_copies) =
let
fun it f [] = () | it f (a::b) = (ignore(f a) ; it f b)
in
(print"Copies:\n";
it
(fn (id,name) => print(Stamp.string_stamp id ^ ":" ^ Strnames.string_strname name ^ "\n"))
(Stamp.Map.to_list strname_copies))
end
fun sig_copy_return (BasisTypes.SIGMA (nameset,str),expand,
strname_copies,tyname_copies,functorp)
newTynameLevel =
let
val (nameset', strname_copies', tyname_copies') =
Nameset.nameset_copy (nameset,strname_copies,tyname_copies)
newTynameLevel
val str' = case str of
COPYSTR(maps, str'') =>
let
val (smap,tmap) = Env.compose_maps(maps,(strname_copies',
tyname_copies'))
val (smap',tmap') =
if functorp
then (Stamp.Map.union (strname_copies,smap),
Stamp.Map.union(tyname_copies,tmap))
else (smap,tmap)
in
if expand then Env.str_copy (str'',smap',tmap')
else COPYSTR((smap',tmap'),str'')
end
| _ =>
if expand then Env.str_copy (str, strname_copies',tyname_copies')
else COPYSTR((strname_copies',tyname_copies'),str)
in
(BasisTypes.SIGMA(nameset', str'), strname_copies', tyname_copies')
end
fun sig_copy (sigma,expand) newTynameLevel =
let
val (sigma',_,_) =
sig_copy_return (sigma, expand,Stamp.Map.empty,
Stamp.Map.empty,false) newTynameLevel
in
sigma'
end
fun phi_copy (phi as BasisTypes.PHI (names,(str,sigma)),expand)
newTynameLevel =
let
val (BasisTypes.SIGMA (names',str'), strname_copies, tyname_copies) =
sig_copy_return (BasisTypes.SIGMA (names, str), expand,
Stamp.Map.empty, Stamp.Map.empty, false)
newTynameLevel
val (sigma',strname_copies',tyname_copies') =
sig_copy_return (sigma, expand,strname_copies, tyname_copies, true)
newTynameLevel
val phi' = BasisTypes.PHI (names', (str', sigma'))
in
phi'
end
fun abstract_sigma (BasisTypes.SIGMA (nameset,str)) newTynameLevel =
let
val (new_nameset, strname_copies, tyname_copies) =
Nameset.nameset_rigid_copy (nameset,Stamp.Map.empty,Stamp.Map.empty)
newTynameLevel
val new_str = case str of
COPYSTR(maps, str) =>
COPYSTR(Env.compose_maps(maps, (strname_copies, tyname_copies)),
str)
| _ =>
COPYSTR((strname_copies,tyname_copies),str)
in
BasisTypes.SIGMA (new_nameset, new_str)
end
end;
