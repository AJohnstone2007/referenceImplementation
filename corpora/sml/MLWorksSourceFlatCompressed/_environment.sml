require "../basis/__int";
require "../utils/print";
require "../basics/identprint";
require "../typechecker/environment";
require "../typechecker/types";
require "../typechecker/valenv";
require "../typechecker/scheme";
require "../typechecker/strnames";
require "../typechecker/tyenv";
require "../typechecker/strenv";
require "stamp";
require "../utils/hashtable";
functor Environment(
structure IdentPrint : IDENTPRINT
structure Print : PRINT
structure Types : TYPES
structure Valenv : VALENV
structure Scheme : TYPESCHEME
structure Strnames : STRNAMES
structure Tyenv : TYENV
structure Strenv : STRENV
structure HashTable : HASHTABLE
structure Stamp : STAMP
sharing Types.Datatypes = Valenv.Datatypes
= Scheme.Datatypes = Strnames.Datatypes
= Tyenv.Datatypes = Strenv.Datatypes
sharing IdentPrint.Ident = Types.Datatypes.Ident
sharing type Types.Datatypes.Stamp = Stamp.Stamp
sharing type Types.Datatypes.StampMap = Stamp.Map.T
) : ENVIRONMENT =
struct
structure Datatypes = Types.Datatypes
structure Valenv = Valenv
structure Exconenv = Valenv
structure Ident = Datatypes.Ident
open Datatypes
exception LookupStrId of Ident.StrId
exception EnrichError of string
local
fun strname_hash_fun (STRNAME id) = Stamp.stamp id
| strname_hash_fun (METASTRNAME (ref strname)) = strname_hash_fun strname
| strname_hash_fun (NULLNAME id) = Stamp.stamp id
fun makememotable () =
HashTable.new (16,Strnames.strname_eq,strname_hash_fun)
fun lookup(strname,table) =
case HashTable.tryLookup (table,strname) of
SOME x => x
| NONE => []
fun update_table (table,strname,newentry) =
HashTable.update (table,strname,newentry)
in
fun memoize_strfun (eqtest,f,str) =
let
val table = makememotable()
exception NotFound
fun f' str =
let
fun find [] = raise NotFound
| find ((str',value)::l) =
if eqtest(str,str') then value
else find l
fun get_strname (STR(strname,_,_)) = strname
| get_strname (COPYSTR(_,str)) = get_strname str
val strname = get_strname str
in
case lookup(strname,table) of
[] =>
let val result = f f' str
in
update_table(table,strname,[(str,result)]);
result
end
| entries =>
((find entries)
handle NotFound =>
let
val result = f f' str
in
update_table(table,strname,(str,result)::entries);
result
end)
end
in
f' str
end
end
local
val cast = MLWorks.Internal.Value.cast
in
fun struct_fast_eq (x,y) =
((cast x):int) = ((cast y):int)
end
fun struct_eq (str as STR(strname,_,ENV(SE se,TE te,ve)),
str' as STR(strname',_,ENV(SE se',TE te',ve'))) =
let
val valenv_eq =
Valenv.valenv_eq
fun strenv_eq (se,se') =
NewMap.eq struct_eq (se,se')
fun tystr_eq (TYSTR (tyfun,valenv),TYSTR(tyfun',valenv')) =
Types.tyfun_eq(tyfun,tyfun')
andalso
valenv_eq(valenv,valenv')
fun tyenv_eq (te,te') =
NewMap.eq tystr_eq (te,te')
in
(struct_fast_eq (str,str') )
orelse
(Strnames.strname_eq(strname,strname')
andalso
valenv_eq(ve,ve')
andalso
tyenv_eq(te,te')
andalso
strenv_eq (se,se'))
end
| struct_eq (str as COPYSTR(maps,sstr),str' as COPYSTR(maps',sstr')) =
let
val smap_eq =
Stamp.Map.eq Strnames.strname_eq
val tmap_eq =
Stamp.Map.eq Types.tyname_eq
fun maps_eq((smap,tmap),(smap',tmap')) =
smap_eq(smap,smap')
andalso
tmap_eq(tmap,tmap')
in
(struct_fast_eq (str,str')
orelse
(maps_eq(maps,maps')
andalso
struct_eq(sstr,sstr')))
end
| struct_eq _ = false
fun struct_copy strcopyfun (STR(strname,r,ENV(SE se,te,ve))) =
STR (strname,r,ENV(SE (NewMap.map (fn(_,a) => strcopyfun a) se),
te,ve))
| struct_copy _ str = str
fun compress_str str =
let
val result = memoize_strfun (struct_eq,struct_copy,str)
in
result
end
val empty_env = ENV (Strenv.empty_strenv,
Tyenv.empty_tyenv,
empty_valenv)
val initial_env = ENV (Strenv.initial_se,
Tyenv.initial_te,
Valenv.initial_ve)
val initial_env_for_builtin_library =
ENV (Strenv.initial_se,
Tyenv.initial_te_for_builtin_library,
Valenv.initial_ve_for_builtin_library)
fun empty_envp (ENV (se,te,ve)) =
(Strenv.empty_strenvp se) andalso
(Tyenv.empty_tyenvp te) andalso
(Valenv.empty_valenvp ve)
fun env_plus_env (ENV (se,te,ve),ENV (se',te',ve')) =
ENV (Strenv.se_plus_se (se,se'),
Tyenv.te_plus_te (te,te'),
Valenv.ve_plus_ve (ve,ve'))
fun compose_maps ((smap1,tmap1),(smap2,tmap2)) =
let
val smap2 =
Stamp.Map.map
(fn (n1,n2) =>
Strnames.strname_copy(n2,smap2))
smap1
val tmap2 =
Stamp.Map.map
(fn (tyfun,tyname) => Types.tyname_copy(tyname,tmap2))
tmap1
in
(smap2,tmap2)
end
fun se_copy (SE amap,strname_copies,tyname_copies) =
SE(NewMap.map (fn (_, str) => str_copy (str,strname_copies,tyname_copies)) amap)
and env_copy (ENV (se,te,ve),strname_copies,tyname_copies) =
ENV (se_copy (se,strname_copies,tyname_copies),
Tyenv.te_copy (te,tyname_copies),
Valenv.ve_copy (ve,tyname_copies))
and str_copy (STR(name,r,env),strname_copies,tyname_copies) =
STR(Strnames.strname_copy (name,strname_copies),
r,
env_copy (env,strname_copies,tyname_copies))
| str_copy (COPYSTR(maps,str),strname_copies,tyname_copies) =
let val (smap,tmap) = compose_maps (maps,(strname_copies,tyname_copies))
in
str_copy (str,smap,tmap)
end
fun expand_str str =
let
fun expand (STR (strid,r,env)) =
STR (strid,r,expand_env env)
| expand (COPYSTR((smap,tmap),str)) =
expand (str_copy (str,smap,tmap))
in
expand str
end
and expand_se (SE se) =
SE (NewMap.map (fn (strid,str) => expand_str str) se)
and expand_env (ENV(se,te,ve)) =
ENV (expand_se se,te,ve)
fun resolve_top_level (str as STR _) = str
| resolve_top_level (COPYSTR((smap,tmap),STR(name,r,ENV(se,te,ve)))) =
let fun se_copy (SE amap) =
SE(NewMap.map
(fn (_,COPYSTR(maps',str')) =>
COPYSTR(compose_maps(maps',(smap,tmap)),str')
| (_,str) => COPYSTR((smap,tmap),str))
amap)
in
STR(Strnames.strname_copy (name,smap),
r,
ENV(se_copy se,
Tyenv.te_copy(te,tmap),
Valenv.ve_copy (ve,tmap)))
end
| resolve_top_level (COPYSTR (maps,str)) =
resolve_top_level (COPYSTR (maps,resolve_top_level str))
fun tyname_make_abs (TYNAME (_,_,_,_,_,_,is_abs_ref,_,_)) =
is_abs_ref := true
| tyname_make_abs (METATYNAME (ref tyfun,_,_,_,_,is_abs_ref)) =
(case tyfun of
ETA_TYFUN tyname => tyname_make_abs tyname
| NULL_TYFUN _ => is_abs_ref := true
| TYFUN _ => ())
fun make_abs (TYFUN (_)) = ()
| make_abs (ETA_TYFUN (tyname)) = tyname_make_abs tyname
| make_abs (NULL_TYFUN (_)) = ()
fun abs (TE amap,ENV (se,te,ve)) =
let
val abste =
TE(NewMap.fold
(fn (map, tycon, TYSTR (tyfun,_)) =>
(ignore(Types.make_false tyfun);
make_abs tyfun;
NewMap.define(map, tycon, TYSTR (tyfun,empty_valenv))))
(NewMap.empty (Ident.tycon_lt, Ident.tycon_eq), amap))
in
ENV (se,Tyenv.te_plus_te (abste,te),ve)
end
fun lookup_strid (strid,ENV (se,_,_)) =
Strenv.lookup (strid,se)
fun lookup_longstrid (Ident.LONGSTRID (Ident.NOPATH,strid),ENV(se,_,_)) =
(case Strenv.lookup(strid,se) of
SOME str => str
| _ => raise LookupStrId strid)
| lookup_longstrid (Ident.LONGSTRID (Ident.PATH (sym,path),strid),ENV(se,_,_)) =
let
fun sort_out (STR (_,_,env)) =
lookup_longstrid(Ident.LONGSTRID (path,strid), env)
| sort_out (COPYSTR (maps,str)) =
COPYSTR(maps,sort_out str)
in
case Strenv.lookup (Ident.STRID sym,se) of
SOME str => sort_out str
| _ => raise LookupStrId (Ident.STRID sym)
end
local
fun lookup_str (STR (_,_,env),path,valid) =
follow_path (path,valid,env)
| lookup_str (COPYSTR ((smap,tmap),str),path,valid) =
Scheme.scheme_copy (lookup_str (str,path,valid),tmap)
and follow_path (Ident.NOPATH,valid,ENV (_,_,ve)) = Valenv.lookup (valid,ve)
| follow_path (Ident.PATH (sym,path),valid,ENV (se,_,_)) =
case Strenv.lookup (Ident.STRID sym,se) of
SOME str => lookup_str (str,path,valid)
| _ => raise LookupStrId (Ident.STRID sym)
in
fun lookup_longvalid (Ident.LONGVALID (path,valid),env) =
follow_path (path,valid,env)
end
local
fun lookup_str (STR (_,_,env),path,tycon) =
follow_path (path,tycon,env)
| lookup_str (COPYSTR ((smap,tmap),str),path,tycon) =
Tyenv.tystr_copy (lookup_str (str,path,tycon),tmap)
and follow_path (Ident.NOPATH,tycon,ENV (_,te,_)) = Tyenv.lookup (te,tycon)
| follow_path (Ident.PATH (sym,path),tycon,ENV (se,_,_)) =
(case Strenv.lookup (Ident.STRID sym,se) of
SOME str => lookup_str (str,path,tycon)
| _ => raise LookupStrId (Ident.STRID sym))
in
fun lookup_longtycon (Ident.LONGTYCON (path,tycon),env) =
follow_path (path,tycon,env)
end
fun SE_in_env se = ENV (se,Tyenv.empty_tyenv,empty_valenv)
fun TE_in_env te = ENV (Strenv.empty_strenv,te,empty_valenv)
fun VE_in_env ve = ENV (Strenv.empty_strenv,Tyenv.empty_tyenv,ve)
fun VE_TE_in_env (ve,te) = ENV (Strenv.empty_strenv,te,ve)
fun string_environment (ENV (se,te,ve)) =
(if Strenv.empty_strenvp se
then ""
else
"SE\n" ^ (string_strenv se) ^ "\n") ^
(if Tyenv.empty_tyenvp te
then ""
else
"TE\n" ^ (Tyenv.string_tyenv te) ^ "\n")
and string_strenv (SE amap) =
let
val strid_str_list = NewMap.to_list_ordered amap
fun make_string (strid,str) =
"structure " ^ IdentPrint.printStrId strid ^ " =\n" ^
string_str str ^ "\n"
in
concat(map make_string strid_str_list)
end
and string_str (STR (name,_,env)) =
"(" ^ (Strnames.string_strname name) ^ "," ^ "\n" ^
string_environment env ^ ")" ^ "\n"
| string_str (COPYSTR ((smap,tmap),str)) =
let
fun string_smap smap =
let
val strings =
map (fn (strname_id,strname) =>
"(" ^ Stamp.string_stamp strname_id ^
" -> " ^ Strnames.string_strname strname ^ ")")
(Stamp.Map.to_list smap)
in
concat ("{" :: strings @ ["}"])
end
fun string_tmap tmap =
let
val strings =
map (fn (tyfun_id,tyname) =>
"(" ^ Int.toString (Stamp.stamp tyfun_id) ^
" -> " ^ Types.debug_print_name tyname ^ ")")
(Stamp.Map.to_list tmap)
in
concat ("{" :: strings @ ["}"])
end
in
"COPYSTR (" ^ string_tmap tmap ^ string_smap smap ^ string_str str ^ ")"
end
fun no_imptyvars (ENV (SE amap',_,VE (_,amap))) =
let
fun look_at_schemes(found as SOME _, _, scheme) =
found
| look_at_schemes(_, _, scheme) = Scheme.has_free_imptyvars scheme
fun aux (found as SOME _, _, STR(_,_,env)) = found
| aux (_, _, STR(_,_,env)) = no_imptyvars env
| aux (found, x,COPYSTR((smap,tmap),str)) =
aux (found, x, str)
in
case NewMap.fold look_at_schemes (NONE, amap) of
NONE => NewMap.fold aux (NONE, amap')
| x => x
end
end
;
