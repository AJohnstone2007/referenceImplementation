require "../utils/print";
require "../utils/crash";
require "../typechecker/types";
require "../typechecker/strnames";
require "stamp";
require "namesettypes";
require "../typechecker/nameset";
functor Nameset(
structure Crash : CRASH
structure Print : PRINT
structure Types : TYPES
structure NamesetTypes : NAMESETTYPES
structure Stamp : STAMP
structure Strnames : STRNAMES
sharing Types.Datatypes = Strnames.Datatypes
sharing type NamesetTypes.TynameSet.element = Types.Datatypes.Tyname
sharing type NamesetTypes.StrnameSet.element = Types.Datatypes.Strname
sharing type Types.Datatypes.Stamp = Stamp.Stamp
sharing type Types.Datatypes.StampMap = Stamp.Map.T
) : NAMESET =
struct
structure Datatypes = Types.Datatypes
structure Options = Types.Options
structure TynameSet = NamesetTypes.TynameSet
structure StrnameSet = NamesetTypes.StrnameSet
type Nameset = NamesetTypes.Nameset
open Datatypes
val initial_tynameset_size = 32
val initial_strnameset_size = 16
fun empty_nameset () =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.empty_set initial_tynameset_size,
NamesetTypes.StrnameSet.empty_set initial_strnameset_size)
fun member_of_tynames (tyname,NamesetTypes.NAMESET(tynames,_)) =
NamesetTypes.TynameSet.is_member(tynames, tyname)
fun member_of_strnames (strname,NamesetTypes.NAMESET(_,strnames)) =
NamesetTypes.StrnameSet.is_member(strnames, strname)
fun union (NamesetTypes.NAMESET(tynames,strnames),NamesetTypes.NAMESET(tynames',strnames')) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.union(tynames,tynames'),
NamesetTypes.StrnameSet.union (strnames,strnames'))
fun intersection (NamesetTypes.NAMESET(tynames,strnames),NamesetTypes.NAMESET(tynames',strnames')) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.intersection(tynames,tynames'),
NamesetTypes.StrnameSet.intersection (strnames,strnames'))
fun nameset_eq (NamesetTypes.NAMESET(tynames,strnames),NamesetTypes.NAMESET(tynames',strnames')) =
NamesetTypes.TynameSet.seteq(tynames,tynames')
andalso
NamesetTypes.StrnameSet.seteq(strnames,strnames')
fun emptyp nameset =
nameset_eq (nameset, empty_nameset())
fun no_tynames (NamesetTypes.NAMESET(tynames,_)) = NamesetTypes.TynameSet.empty_setp tynames
fun add_tyname (name,NamesetTypes.NAMESET(tynames,strnames)) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.add_member(tynames, name),strnames)
fun add_strname (name,NamesetTypes.NAMESET(tynames,strnames)) =
NamesetTypes.NAMESET(tynames, NamesetTypes.StrnameSet.add_member(strnames, name))
fun tynames_in_nameset (tynames,NamesetTypes.NAMESET(tynames',strnames)) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.add_list(tynames', tynames),strnames)
fun remove_tyname (name,NamesetTypes.NAMESET(tynames,strnames)) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.remove_member(tynames, name),strnames)
fun remove_strname (name,NamesetTypes.NAMESET(tynames,strnames)) =
NamesetTypes.NAMESET(tynames,NamesetTypes.StrnameSet.remove_member(strnames, name))
fun diff (NamesetTypes.NAMESET(tynames,strnames),NamesetTypes.NAMESET(tynames',strnames')) =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.setdiff(tynames,tynames'),
NamesetTypes.StrnameSet.setdiff (strnames,strnames'))
val initial_nameset =
NamesetTypes.NAMESET(NamesetTypes.TynameSet.list_to_set
[Types.bool_tyname,
Types.int_tyname,
Types.word_tyname,
Types.int8_tyname,
Types.word8_tyname,
Types.int16_tyname,
Types.word16_tyname,
Types.int32_tyname,
Types.word32_tyname,
Types.int64_tyname,
Types.word64_tyname,
Types.real_tyname,
Types.float32_tyname,
Types.string_tyname,
Types.char_tyname,
Types.list_tyname,
Types.ref_tyname,
Types.exn_tyname],
NamesetTypes.StrnameSet.empty_set initial_strnameset_size)
val initial_nameset_for_builtin_library =
initial_nameset
fun string_nameset options (NamesetTypes.NAMESET(tynames,strnames)) =
"NAMESET ({" ^ (NamesetTypes.TynameSet.set_print(tynames, Types.debug_print_name)) ^ "}{" ^
(NamesetTypes.StrnameSet.set_print(strnames, Strnames.string_strname)) ^ "})"
local
val doCopyRigidTyname = Types.create_tyname_copy true
val doCopyRigidStrname = Strnames.create_strname_copy true
val doNewTyname = Types.create_tyname_copy false
val doNewStrname = Strnames.create_strname_copy false
fun copy
(tynamecopyfun,strnamecopyfun)
(NamesetTypes.NAMESET(tynames,strnames),
strname_copies,tyname_copies) newTynameLevel =
let
val tyname_copies' =
NamesetTypes.TynameSet.fold (tynamecopyfun newTynameLevel)
(tyname_copies, tynames)
val strname_copies' =
NamesetTypes.StrnameSet.fold strnamecopyfun
(strname_copies, strnames)
fun strip_tyname(meta as METATYNAME{1 = ref(NULL_TYFUN _), ...}) =
meta
| strip_tyname(METATYNAME{1=ref(ETA_TYFUN tyname), ...}) =
strip_tyname tyname
| strip_tyname _ = Crash.impossible"strip_tyname"
fun copy_type(METATYVAR(ref(i, ty,instances), eq, imp)) =
METATYVAR(ref(i, copy_type ty,instances), eq, imp)
| copy_type(METARECTYPE(ref(i, flex, ty, eq, imp))) =
METARECTYPE(ref(i, flex, copy_type ty, eq, imp))
| copy_type(RECTYPE map) =
RECTYPE(NewMap.map copy_type_map map)
| copy_type(FUNTYPE(ty1, ty2)) =
FUNTYPE(copy_type ty1, copy_type ty2)
| copy_type(CONSTYPE(type_list, tyname)) =
let
val type_list = map copy_type type_list
val tyname = case tyname of
TYNAME {1=id,...} =>
(case Stamp.Map.tryApply'(tyname_copies', id) of
SOME tyname' => tyname'
| _ => tyname)
| METATYNAME(r as ref tyfun, s, ar, ref eq, ref ve, ref is_abs) =>
if Types.null_tyfunp tyfun then
let
val stripped_tyname = strip_tyname tyname
val (r, id) = case stripped_tyname of
METATYNAME{1=r as ref(NULL_TYFUN (id,_)), ...} =>
(r, id)
| _ => Crash.impossible"strip_tyname2"
in
case Stamp.Map.tryApply'(tyname_copies', id) of
SOME x => x
| _ => stripped_tyname
end
else
tyname
in
CONSTYPE(type_list, tyname)
end
| copy_type ty = ty
and copy_type_map(_, ty) = copy_type ty
fun copy_type_for_scheme(FUNTYPE(ty, _)) =
FUNTYPE(copy_type ty, NULLTYPE)
| copy_type_for_scheme _ = NULLTYPE
and copy_scheme(scheme as OVERLOADED_SCHEME _) = scheme
| copy_scheme(UNBOUND_SCHEME (ty,instance)) =
UNBOUND_SCHEME(copy_type_for_scheme ty,instance)
| copy_scheme(SCHEME(i, (ty,instance))) =
SCHEME(i, (copy_type_for_scheme ty,instance))
fun new_valenvs(meta as METATYNAME(ref(NULL_TYFUN id), s, arity,
ref eq, r as
ref(VE(_,mapping)),
ref is_abs)) =
let
val mapping = NewMap.map (copy_scheme o #2) mapping
in
r := VE(ref 0, mapping)
end
| new_valenvs(TYNAME{5= r as ref(VE(_, mapping)) ,...}) =
let
val mapping =
NewMap.map (copy_scheme o #2) mapping
in
r := VE(ref 0, mapping)
end
| new_valenvs _ = ()
fun findstrname (strname as METASTRNAME (ref (NULLNAME id)),
strname_map) =
Stamp.Map.apply_default'(strname_map, strname, id)
| findstrname (strname as METASTRNAME (ref name),copies) =
findstrname (name,copies)
| findstrname (strname,_) = strname
fun findtyname (tyname as METATYNAME {1=ref (NULL_TYFUN (id,_)), ...},
tyname_map) =
Stamp.Map.apply_default'(tyname_map, tyname, id)
| findtyname (METATYNAME {1 = ref (ETA_TYFUN
(tyname as METATYNAME
{1 = ref tyfun,...})),...},
copies) = findtyname (tyname,copies)
| findtyname (METATYNAME {1 = ref (TYFUN
(CONSTYPE ([],tyname as METATYNAME
{1 = ref tyfun,...}),
_)),...},
copies) = findtyname (tyname,copies)
| findtyname (tyname as METATYNAME {1=ref tyfun, ...},_) = tyname
| findtyname (tyname as TYNAME {1= id,...},tyname_map) =
Stamp.Map.apply_default'(tyname_map, tyname, id)
fun substTynameCopies tyname_copies (res, tyname) =
NamesetTypes.TynameSet.add_member (res, findtyname(tyname, tyname_copies))
fun substStrnameCopies strname_copies (res, strname) =
NamesetTypes.StrnameSet.add_member (res, findstrname(strname, strname_copies))
val tynames' =
NamesetTypes.TynameSet.fold (substTynameCopies tyname_copies')
(NamesetTypes.TynameSet.empty_set (NamesetTypes.TynameSet.set_size tynames div 2),
tynames)
val strnames' =
NamesetTypes.StrnameSet.fold (substStrnameCopies strname_copies')
(NamesetTypes.StrnameSet.empty_set(NamesetTypes.StrnameSet.set_size strnames div 2),
strnames)
val _ = NamesetTypes.TynameSet.iterate new_valenvs tynames'
in
(NamesetTypes.NAMESET(tynames',strnames'), strname_copies',tyname_copies')
end
in
val nameset_rigid_copy = copy (doCopyRigidTyname,doCopyRigidStrname)
val new_names = copy (doNewTyname,doNewStrname)
val nameset_copy = new_names
fun new_names_from_scratch nameset =
new_names (nameset, Stamp.Map.empty, Stamp.Map.empty)
end
fun tynames_of_nameset (NamesetTypes.NAMESET(tl,sl)) = NamesetTypes.TynameSet.set_to_list tl
fun strnames_of_nameset (NamesetTypes.NAMESET(tl,sl)) = NamesetTypes.StrnameSet.set_to_list sl
fun nameset_of_name_lists (tl,sl) =
NamesetTypes.NAMESET (NamesetTypes.TynameSet.list_to_set tl,NamesetTypes.StrnameSet.list_to_set sl)
fun simple_copy nameset =
nameset_of_name_lists (tynames_of_nameset nameset,
strnames_of_nameset nameset)
fun nameset_rehash(NamesetTypes.NAMESET(tl,sl)) =
(TynameSet.rehash tl; StrnameSet.rehash sl)
end
;
