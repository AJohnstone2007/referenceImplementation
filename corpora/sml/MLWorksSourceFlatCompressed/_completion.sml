require "../basics/identprint";
require "../utils/lists";
require "../utils/hashtable";
require "types";
require "namehash";
require "stamp";
require "completion";
functor Completion(
structure Types : TYPES
structure Lists : LISTS
structure IdentPrint : IDENTPRINT
structure NameHash : NAMEHASH
structure HashTable : HASHTABLE
structure Stamp : STAMP
sharing NameHash.Datatypes = Types.Datatypes
sharing IdentPrint.Ident = Types.Datatypes.Ident
sharing type Types.Datatypes.Stamp = Stamp.Stamp
sharing type Types.Datatypes.StampMap = Stamp.Map.T
): COMPLETION =
struct
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Options = Types.Options
fun first (a,b) = a
open Datatypes
fun tyname_eq (id, TYNAME {1=id',...}) = id = id'
| tyname_eq (id, METATYNAME {1=ref tyfun,...}) =
tyfun_eq (id,tyfun)
and tyfun_eq (id,TYFUN _) = false
| tyfun_eq (id,ETA_TYFUN tyname) = tyname_eq (id,tyname)
| tyfun_eq (id,NULL_TYFUN (id',ref tyfun)) = id = id'
exception TyfunStamp
fun tyname_stamp (TYNAME {1=id,...}) = id
| tyname_stamp (METATYNAME {1=ref tyfun,...}) = tyfun_stamp tyfun
and tyfun_stamp (TYFUN _) = raise TyfunStamp
| tyfun_stamp (ETA_TYFUN tyname) = tyname_stamp tyname
| tyfun_stamp (NULL_TYFUN (id,ref tyfun)) = id
type Cache = ((Type,Type) HashTable.HashTable *
(Stamp.Stamp,string) HashTable.HashTable)
local
fun type_eq(ty1, ty2) = Types.type_eq(ty1, ty2, false, false)
in
fun empty_cache () = (HashTable.new(10, type_eq, NameHash.type_hash),
HashTable.new(10, (op =), Stamp.stamp))
end
datatype 'a result = YES of 'a | NO
fun searchE (ENV(strenv, tyenv, _), tyname_id) =
case searchTE(tyenv, tyname_id) of
YES tycon => [IdentPrint.printTyCon tycon]
| NO => searchSE(strenv, tyname_id)
and searchSE (SE mapping, tyname_id) =
let
fun check ([], strid, (STR(_,_,env)),tyname_id) =
(case searchE(env, tyname_id) of
[] => []
| namelist => (IdentPrint.printStrId strid ^ ".") :: namelist)
| check (namelist, strid, (STR(_,_,env)), tyname_id) =
(case searchE(env, tyname_id) of
[] => namelist
| namelist' =>
let
val len = length namelist
val len' = length namelist'
in
if len'+1 < len then
(IdentPrint.printStrId strid ^ ".") :: namelist'
else
namelist
end)
| check (namelist,strid,COPYSTR((smap,tmap),str),tyname_id) =
let
val result =
Stamp.Map.fold
(fn (result, tyfun_id, tyname') =>
if tyname_eq(tyname_id, tyname') then
tyfun_id :: result
else
result)
([], tmap)
in
case result of
[] => check(namelist, strid, str, tyname_id)
| tyfun_id_list =>
let
fun check_one(namelist, tyfun_id) =
check(namelist, strid, str, tyfun_id)
val new_namelist = Lists.reducel check_one (namelist, tyfun_id_list)
in
new_namelist
end
end
in
NewMap.fold (fn (a,b,c) => check(a,b,c,tyname_id)) ([],mapping)
end
and searchTE (TE mapping, tyname_id) =
let
fun print (args as (_,_,TYSTR(tyfun,_))) =
args
fun check(NO, tycon, (TYSTR(ETA_TYFUN tyname', _))) =
if tyname_eq(tyname_id, tyname') then
YES tycon
else
NO
| check(NO, tycon, (TYSTR(tyfun as TYFUN _, _))) =
let
val tyfun' = Types.tyfun_strip tyfun
in
case tyfun' of
ETA_TYFUN tyname' =>
if tyname_eq(tyname_id, tyname') then
YES tycon
else
NO
| _ => NO
end
| check(NO, _, _) = NO
| check(arg as YES _, _, _) = arg
in
NewMap.fold (check o print) (NO, mapping)
end
fun check_tyname (env, (map1, map2), tyname, name) =
case HashTable.tryLookup(map2, tyname) of
SOME result => result
| _ =>
(case searchE(env, tyname) of
[] => name ^ "(hidden)"
| namelist => concat namelist)
fun complete_tycons (env, m as (m1, _),
t as METATYVAR(ref (x, ty,i), eq, imp)) =
(case HashTable.tryLookup(m1, t) of
SOME result => (result, m)
| _ =>
(case ty of
NULLTYPE =>
(t,m)
| _ =>
let
val (ty', m as (m1, _)) = complete_tycons (env, m, ty)
val t' = METATYVAR(ref (x, ty',i), eq, imp)
val _ = HashTable.update(m1, t, t')
in
(t', m)
end))
| complete_tycons (env, m as (m1, _),
t as META_OVERLOADED(ref ty,tv,valid,loc)) =
(case HashTable.tryLookup(m1, t) of
SOME result => (result, m)
| _ =>
let
val (ty', m as (m1, _)) = complete_tycons (env, m, ty)
val t' = META_OVERLOADED(ref ty',tv,valid,loc)
val _ = HashTable.update(m1, t, t')
in
(t', m)
end)
| complete_tycons (env, m, ty as (TYVAR _)) = (ty, m)
| complete_tycons (env, m, METARECTYPE (ref (level, flex, ty, eq, imp))) =
let
val (ty', m') = complete_tycons (env, m, ty)
in
(METARECTYPE(ref (level, flex, ty, eq, imp)), m')
end
| complete_tycons (env, m, RECTYPE fields) =
let
fun f((fields, m), lab, ty) =
let
val (ty', m') = complete_tycons(env, m, ty)
in
(NewMap.define'(fields, (lab, ty')), m')
end
val (fields', m') = NewMap.fold f ((NewMap.empty' Ident.lab_lt, m), fields)
in
(RECTYPE fields', m')
end
| complete_tycons (env, m, FUNTYPE(ty1, ty2)) =
let
val (ty1', m1) = complete_tycons(env, m , ty1)
val (ty2', m2) = complete_tycons(env, m1, ty2)
in
(FUNTYPE(ty1', ty2'), m2)
end
| complete_tycons (env, m, CONSTYPE(type_list, tyname)) =
let
fun f (ty, (ty_list, m1)) =
let
val (ty', m2) = complete_tycons(env, m1, ty)
in
(ty' :: ty_list, m2)
end
val (tyname', m') = change_name (env, m, tyname)
val (type_list', m'') = Lists.reducer f (type_list, ([], m'))
in
(CONSTYPE(type_list', tyname'), m'')
end
| complete_tycons (env, m, ty as (DEBRUIJN _)) = (ty, m)
| complete_tycons (env, m, ty as NULLTYPE) = (ty, m)
and change_name (env, m as (_, m2), tyname) =
case tyname of
METATYNAME(ref tyfun, name, arity, ref eq, ref valenv,ref is_abs) =>
(let
val stamp = tyname_stamp tyname
val name' = check_tyname(env, m, stamp, name)
val tyname' = METATYNAME(ref tyfun, name', arity, ref eq, ref valenv,ref is_abs)
val _ = HashTable.update(m2, stamp, name')
in
(tyname', m)
end
handle TyfunStamp => (tyname,m))
| TYNAME(stamp, name, arity, ref eq, ref valenv,location,ref is_abs,
ve_copy, level) =>
let
val name' = check_tyname(env, m, stamp, name)
val tyname' = TYNAME(stamp, name', arity, ref eq, ref valenv,
location,ref is_abs,ve_copy,level)
val _ = HashTable.update(m2, stamp, name')
in
(tyname', m)
end
val complete_tycons =
fn (env,cache,ty) => first(complete_tycons(env,cache, Types.simplify_type ty))
fun print_type (options,env,ty) =
let
val ty' = complete_tycons (env,empty_cache (),ty)
in
Types.print_type options ty'
end
fun cached_print_type (options,env,ty,cache) =
let
val ty' = complete_tycons (env,cache,ty)
in
(Types.print_type options ty',cache)
end
fun has_debruijns(deb_list, METATYVAR(ref(_, ty, _), _, _)) =
has_debruijns(deb_list, ty)
| has_debruijns(deb_list, META_OVERLOADED _) = deb_list
| has_debruijns(deb_list, TYVAR _) = deb_list
| has_debruijns(deb_list, METARECTYPE(ref{3= ty, ...})) =
has_debruijns(deb_list, ty)
| has_debruijns(deb_list, RECTYPE map) =
NewMap.fold
(fn (deb_list, _, ty) => has_debruijns(deb_list, ty))
(deb_list, map)
| has_debruijns(deb_list, FUNTYPE(ty, ty')) =
has_debruijns(has_debruijns(deb_list, ty), ty')
| has_debruijns(deb_list, CONSTYPE(ty_list, _)) =
Lists.reducel has_debruijns (deb_list, ty_list)
| has_debruijns(deb_list, DEBRUIJN deb) =
if Lists.member (deb, deb_list) then deb_list else deb :: deb_list
| has_debruijns(deb_list, NULLTYPE) = deb_list
fun string_debruijn options (i, eq, imp, _)
= Types.string_debruijn(options,i, eq, imp)
fun print_debs _ [] = []
| print_debs options [a] = [string_debruijn options a]
| print_debs options (a :: rest) = string_debruijn options a
:: ", " :: print_debs options rest
fun print_debruijns _ [] = ""
| print_debruijns options [a]
= "(for all " ^ string_debruijn options a ^ ")."
| print_debruijns options list = "(for all "
^ concat(print_debs options list) ^ ")."
fun report_type_error (options,env,l) =
let
val cache = empty_cache ()
fun f (Err_String s,tyvars) = (s,tyvars)
| f (Err_Type t,tyvars) =
Types.print_type_with_seen_tyvars
(options,complete_tycons (env,cache,t),tyvars)
| f (Err_Scheme t,tyvars) =
let
val result as (str, tyvars) =
Types.print_type_with_seen_tyvars
(options,complete_tycons (env,cache,t),tyvars)
in
case has_debruijns([], t) of
[] => result
| deb_list => (print_debruijns options deb_list
^ str, tyvars)
end
| f (Err_Reset, _) = ("",Types.no_tyvars)
fun foldmap ([],tyvars,acc) = rev acc
| foldmap (ty::tys,tyvars,acc) =
let
val (name,tyvars) = f(ty,tyvars)
in
foldmap (tys,tyvars,name::acc)
end
in
concat (foldmap (l,Types.no_tyvars,[]))
end
end
;
