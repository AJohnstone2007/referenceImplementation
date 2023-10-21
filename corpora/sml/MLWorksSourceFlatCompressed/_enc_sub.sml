require "../utils/diagnostic";
require "../utils/lists";
require "../utils/crash";
require "../typechecker/datatypes";
require "enc_sub";
functor Enc_Sub(
structure DataTypes : DATATYPES where type Stamp = int
structure Diagnostic : DIAGNOSTIC
structure Lists : LISTS
structure Crash : CRASH
) : ENC_SUB =
struct
structure Diagnostic = Diagnostic
structure DataTypes = DataTypes
structure NewMap = DataTypes.NewMap
fun pair_eq(first_eq, second_eq) ((a, b), (a', b')) =
first_eq(a, a') andalso second_eq(b, b')
fun list_eq eqfn (lista, listb) =
let
fun list_subeq([], []) = true
| list_subeq (x :: xs, y :: ys) =
eqfn(x, y) andalso list_subeq (xs, ys)
| list_subeq _ = false
in
list_subeq(lista, listb)
end
fun map_eq (map1, map2) = NewMap.eq type_same (map1, map2)
and type_same(DataTypes.METATYVAR arg, DataTypes.METATYVAR arg') =
arg = arg'
| type_same(DataTypes.META_OVERLOADED arg, DataTypes.META_OVERLOADED arg') =
arg = arg'
| type_same(DataTypes.TYVAR arg, DataTypes.TYVAR arg') =
arg = arg'
| type_same(DataTypes.METARECTYPE arg, DataTypes.METARECTYPE arg') =
arg = arg'
| type_same(DataTypes.RECTYPE arg, DataTypes.RECTYPE arg') =
map_eq (arg, arg')
| type_same(DataTypes.FUNTYPE(ty1, ty2), DataTypes.FUNTYPE(ty1', ty2')) =
type_same(ty1, ty1') andalso type_same(ty2, ty2')
| type_same(DataTypes.CONSTYPE(l, t), DataTypes.CONSTYPE(l', t')) =
tyname_same(t, t') andalso list_eq type_same (l, l')
| type_same(DataTypes.DEBRUIJN arg, DataTypes.DEBRUIJN arg') =
((fn ty=>(#1(ty),#2(ty),#3(ty)))arg) = ((fn ty=>(#1(ty),#2(ty),#3(ty)))arg')
| type_same(DataTypes.NULLTYPE, DataTypes.NULLTYPE) = true
| type_same _ = false
and tyname_same(DataTypes.TYNAME{1=id, ...}, DataTypes.TYNAME{1=id', ...}) =
id = id'
| tyname_same(DataTypes.METATYNAME arg, DataTypes.METATYNAME arg') =
arg = arg'
| tyname_same _ = false
fun tyfun_same(DataTypes.TYFUN(ty1, i1), DataTypes.TYFUN(ty2, i2)) =
type_same(ty1, ty2) andalso i1 = i2
| tyfun_same(DataTypes.ETA_TYFUN tyname, DataTypes.ETA_TYFUN tyname') =
tyname_same(tyname, tyname')
| tyfun_same(DataTypes.NULL_TYFUN (id,_), DataTypes.NULL_TYFUN (id',_)) = id = id'
| tyfun_same _ = false
fun type_from_scheme(DataTypes.SCHEME(_, (ty,_))) = ty
| type_from_scheme(DataTypes.UNBOUND_SCHEME (ty,_)) = ty
| type_from_scheme _ = Crash.impossible"type_from_scheme"
fun type_same_sort(scheme1, scheme2) =
case (type_from_scheme scheme1, type_from_scheme scheme2) of
(DataTypes.FUNTYPE _, DataTypes.FUNTYPE _) => true
| (DataTypes.CONSTYPE _, DataTypes.CONSTYPE _) => true
| _ => false
fun tyname_valenv_same(DataTypes.VE(_, ve1), DataTypes.VE(_, ve2)) =
NewMap.eq type_same_sort (ve1, ve2)
fun type_hash(DataTypes.METATYVAR(ref(i, ty,_), _, _)) =
3 + i + type_hash ty
| type_hash(DataTypes.META_OVERLOADED {1=ref ty,...}) = 5 + type_hash ty
| type_hash(DataTypes.TYVAR(ref (i,_,_), tyvar)) = 7 + i
| type_hash(DataTypes.METARECTYPE(ref(i, _, ty, _, _))) =
11 + i + type_hash ty
| type_hash(DataTypes.RECTYPE(lab_ty_map)) =
NewMap.fold
(fn (i, _, ty) => i + type_hash ty)
(13, lab_ty_map)
| type_hash(DataTypes.FUNTYPE(ty1, ty2)) = 17 + type_hash ty1 + type_hash ty2
| type_hash(DataTypes.CONSTYPE(ty_list, tyname)) =
Lists.reducel
(fn (i, ty) => i + type_hash ty)
(tyname_hash tyname + 19, ty_list)
| type_hash(DataTypes.DEBRUIJN(i, b1, b2,_)) = 23 + i
| type_hash(DataTypes.NULLTYPE) = 47
and tyname_hash(DataTypes.TYNAME{1=tyname_id, ...}) =
29 + tyname_id
| tyname_hash(DataTypes.METATYNAME{1 = ref tyfun, ...}) =
31 + tyfun_hash tyfun
and tyfun_hash(DataTypes.TYFUN(ty, i)) =
41 + type_hash ty + i
| tyfun_hash(DataTypes.ETA_TYFUN tyname) = 43 + tyname_hash tyname
| tyfun_hash(DataTypes.NULL_TYFUN (id,_)) = 47 + id
fun symbol_hash symbol = size(DataTypes.Ident.Symbol.symbol_name symbol)
fun valid_hash(DataTypes.Ident.VAR sy) = symbol_hash sy
| valid_hash(DataTypes.Ident.CON sy) = symbol_hash sy
| valid_hash(DataTypes.Ident.EXCON sy) = symbol_hash sy
| valid_hash(_) = Crash.impossible "TYCON':valid_hash:enc_sub"
fun tyname_valenv_hash(DataTypes.VE(_, amap)) =
let
val assoc = NewMap.to_list_ordered amap
in
Lists.reducel
(fn (i, (valid, typescheme)) =>
i + valid_hash valid +
(case type_from_scheme typescheme of
DataTypes.FUNTYPE _ => 3
| DataTypes.CONSTYPE _ => 5
| DataTypes.NULLTYPE => 5
| _ => Crash.impossible"tyname_valenv_hash"))
(Lists.length assoc, assoc)
end
end
;
