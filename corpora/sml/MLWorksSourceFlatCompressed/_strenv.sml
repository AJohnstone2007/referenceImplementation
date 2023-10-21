require "../typechecker/datatypes";
require "../typechecker/strenv";
functor Strenv ( structure Datatypes : DATATYPES
) : STRENV =
struct
structure Datatypes = Datatypes
open Datatypes
val empty_strenv = SE (NewMap.empty (Ident.strid_lt, Ident.strid_eq))
fun empty_strenvp (SE amap) = NewMap.is_empty amap
fun lookup (strid,SE amap) =
NewMap.tryApply'(amap, strid)
fun se_plus_se (SE amap,SE amap') =
SE (NewMap.union(amap, amap'))
fun add_to_se (strid,str,SE amap) =
SE (NewMap.define (amap,strid,str))
val initial_se = empty_strenv
end
;
