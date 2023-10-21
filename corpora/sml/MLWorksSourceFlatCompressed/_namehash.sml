require "^.utils.lists";
require "^.typechecker.types";
require "^.typechecker.namehash";
functor NameHash(
structure Lists : LISTS
structure Types : TYPES
) : NAMEHASH =
struct
structure Datatypes = Types.Datatypes
structure Ident = Datatypes.Ident
fun sy_hash (str:string):int = Lists.reducel (fn (x,y)=>x+ord y) (size str, explode str)
fun strname_hash(Datatypes.STRNAME id) =
3 + Types.stamp_num id
| strname_hash(Datatypes.NULLNAME id) =
5 + Types.stamp_num id
| strname_hash(Datatypes.METASTRNAME(ref s)) = strname_hash s
fun tyname_hash(Datatypes.TYNAME{1=id, ...}) =
3 + Types.stamp_num id
| tyname_hash(Datatypes.METATYNAME{1=ref(Datatypes.ETA_TYFUN tyname),
...}) =
tyname_hash tyname
| tyname_hash(Datatypes.METATYNAME{1=ref tyfun, ...}) =
tyfun_hash tyfun
and tyfun_hash(Datatypes.TYFUN(ty, _)) = type_hash ty
| tyfun_hash(Datatypes.ETA_TYFUN tyname) = tyname_hash tyname
| tyfun_hash(Datatypes.NULL_TYFUN (id,_)) = 5 + Types.stamp_num id
and type_hash(Datatypes.METATYVAR(ref(_, ty,_), _, _)) = type_hash ty
| type_hash(Datatypes.META_OVERLOADED {1=ref ty,...}) = type_hash ty
| type_hash(Datatypes.TYVAR(_, Datatypes.Ident.TYVAR(sy, _, _))) =
7 + sy_hash(Ident.Symbol.symbol_name sy)
| type_hash(Datatypes.METARECTYPE(ref{3=ty, ...})) = type_hash ty
| type_hash(ty as Datatypes.RECTYPE _) =
let
val domain = Types.rectype_domain ty
in
Lists.reducel
(fn (x, Ident.LAB y) => x +
sy_hash(Ident.Symbol.symbol_name y))
(11 + length domain, domain)
end
| type_hash(Datatypes.FUNTYPE(ty, ty')) =
type_hash ty + type_hash ty'
| type_hash(Datatypes.CONSTYPE(_, tyname)) = tyname_hash tyname
| type_hash(Datatypes.DEBRUIJN _) = 13
| type_hash Datatypes.NULLTYPE = 17
end
;
