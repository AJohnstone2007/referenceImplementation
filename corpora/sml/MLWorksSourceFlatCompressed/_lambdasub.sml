require "^.basis.__list";
require "lambdatypes";
require "lambdasub";
functor LambdaSub(
structure LambdaTypes : LAMBDATYPES
) : LAMBDASUB =
struct
structure Set = LambdaTypes.Set;
structure LT = LambdaTypes;
structure Ident = LT.Ident
fun unwrap_lets expr =
let
fun unwl(acc, LT.LET((var,info,bind),body)) =
unwl((var,bind)::acc,body)
| unwl(acc, expr) = (rev acc, expr)
in
unwl([],expr)
end
fun wrap_lets (expression, vas) =
let
fun nest ((var,arg),body) = LT.LET((var, NONE, arg),body)
in
foldl nest expression (rev vas)
end
fun eta_abstract (le,annotation,ty) =
let
val lvar = LT.new_LVar()
in
LT.FN([lvar], LT.APP(le, [LT.VAR lvar], SOME(ty)),
annotation,LT.null_type_annotation,LT.internal_funinfo)
end
val LVar_eq = op=
fun apopt f (SOME x) = SOME (f x)
| apopt _ NONE = NONE
fun occurs (lvar, expression) =
let
fun occurs(LT.VAR lv) = LVar_eq (lv, lvar)
| occurs(LT.FN(_, le,_,_,_)) = occurs le
| occurs(LT.LET((lv,_,lb),le)) = occurs lb orelse occurs le
| occurs(LT.LETREC(lv_list, le_list, le)) =
(occurs le orelse List.exists occurs le_list)
| occurs(LT.APP(le, lel,_)) = (occurs le orelse List.exists occurs lel)
| occurs(LT.SCON _) = false
| occurs(LT.MLVALUE _) = false
| occurs(LT.INT _) = false
| occurs(LT.SWITCH(le, info, tag_le_list, leo)) =
(occurs le orelse
occurs_opt leo orelse
List.exists occurs_tag tag_le_list)
| occurs(LT.STRUCT (le_list,_)) = List.exists occurs (le_list)
| occurs(LT.SELECT(_, le)) = occurs le
| occurs(LT.RAISE (le)) = occurs le
| occurs(LT.HANDLE(le, le',_)) = (occurs le orelse occurs le')
| occurs(LT.BUILTIN _) = false
and occurs_opt(NONE) = false
| occurs_opt(SOME le) = occurs le
and occurs_tag (tag, le) =
(case tag of
LT.EXP_TAG le' => occurs le' orelse occurs le
| _ => occurs le)
in
occurs expression
end
fun apply_one_level appsub =
let
fun apply_tagval (LT.EXP_TAG tagexp, value) =
(LT.EXP_TAG (appsub tagexp), appsub value)
| apply_tagval (tagexp, value) =
(tagexp, appsub value)
fun apply (le as LT.VAR _) = le
| apply (LT.FN(lvl, le,x,ty,instance)) = LT.FN (lvl, appsub le,x,ty,instance)
| apply (LT.LET((lv,info,lb),le)) = LT.LET((lv,info,appsub lb),appsub le)
| apply (LT.LETREC(lvl, lel, le)) =
LT.LETREC (lvl, map appsub lel, appsub le)
| apply (LT.APP(p, q, annotation)) =
LT.APP(appsub p, map appsub q, annotation)
| apply (le as LT.SCON _) = le
| apply (le as LT.MLVALUE _) = le
| apply (le as LT.INT _) = le
| apply (LT.SWITCH(le, info, clel, leo)) =
LT.SWITCH
(appsub le,
info,
map apply_tagval clel,
apopt appsub leo)
| apply (LT.STRUCT (lel,ty)) = LT.STRUCT (map appsub lel,ty)
| apply (LT.SELECT (fld, le)) = LT.SELECT (fld, appsub le)
| apply (LT.RAISE (le)) = LT.RAISE (appsub le)
| apply (LT.HANDLE (le1, le2,annotation)) = LT.HANDLE (appsub le1,
appsub le2,
annotation)
| apply (le as LT.BUILTIN _) = le
in
apply
end
end
;
