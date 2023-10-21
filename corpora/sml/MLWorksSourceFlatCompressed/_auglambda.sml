require "../utils/crash";
require "../lambda/lambdatypes";
require "../main/pervasives";
require "../debugger/debugger_utilities";
require "auglambda";
functor AugLambda(
structure Crash : CRASH
structure LambdaTypes : LAMBDATYPES
structure Pervasives : PERVASIVES
structure DebuggerUtilities : DEBUGGER_UTILITIES
sharing DebuggerUtilities.Options = DebuggerUtilities.Debugger_Types.Options
sharing type LambdaTypes.Type = DebuggerUtilities.Debugger_Types.Type
sharing type LambdaTypes.Primitive = Pervasives.pervasive
) : AUGLAMBDA =
struct
structure LambdaTypes = LambdaTypes
structure Ident = LambdaTypes.Ident
structure Debugger_Types = DebuggerUtilities.Debugger_Types
structure Options = Debugger_Types.Options
datatype Tag =
VCC_TAG of string * int
| IMM_TAG of string * int
| SCON_TAG of Ident.SCon * int option
| EXP_TAG of sized_AugLambdaExp
and AugLambdaExp =
VAR of LambdaTypes.LVar
| FN of ((LambdaTypes.LVar list * LambdaTypes.LVar list) * {size:int, lexp:AugLambdaExp} * string * LambdaTypes.FunInfo)
| LET of ((LambdaTypes.LVar * LambdaTypes.VarInfo ref option * sized_AugLambdaExp) * sized_AugLambdaExp)
| LETREC of
((LambdaTypes.LVar * LambdaTypes.VarInfo ref option) list *
sized_AugLambdaExp list *
sized_AugLambdaExp)
| APP of (sized_AugLambdaExp *
(sized_AugLambdaExp list * sized_AugLambdaExp list) *
Debugger_Types.Backend_Annotation)
| SCON of LambdaTypes.Ident.SCon * int option
| MLVALUE of MLWorks.Internal.Value.ml_value
| INT of int
| SWITCH of
(sized_AugLambdaExp *
{num_vccs: int, num_imms: int} option *
(Tag * sized_AugLambdaExp) list *
sized_AugLambdaExp option)
| STRUCT of sized_AugLambdaExp list
| SELECT of {index : int, size : int} * sized_AugLambdaExp
| RAISE of sized_AugLambdaExp
| HANDLE of (sized_AugLambdaExp * sized_AugLambdaExp)
| BUILTIN of LambdaTypes.Primitive * LambdaTypes.Type
withtype sized_AugLambdaExp = {size:int, lexp:AugLambdaExp}
fun count_gc_objects (options,
exp,generate_debug_info,mapping,setup_function) =
let
fun count_gc_objects' (LambdaTypes.VAR lv,_,mapping,_) =
({size=0, lexp=VAR lv} : sized_AugLambdaExp ,mapping)
| count_gc_objects'(LambdaTypes.LET((lv,info,lb), le),ty,mapping,name) =
let
val (lb' as {size,lexp},mapping') = count_gc_objects' (lb,ty,mapping,name)
val (le' as {size=size',lexp},mapping'') = count_gc_objects' (le,ty,mapping',name)
in
({size=size+size', lexp=LET((lv,info,lb'),le')},mapping'')
end
| count_gc_objects' (LambdaTypes.APP(LambdaTypes.BUILTIN prim, ([le],[]),annotation),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
val size = case prim of
Pervasives.LOAD_STRING => 0
| Pervasives.LOAD_VAR => 0
| Pervasives.LOAD_EXN => 0
| Pervasives.LOAD_STRUCT => 0
| Pervasives.LOAD_FUNCT => 0
| _ => size
in
({size=size, lexp=APP({size=0, lexp=BUILTIN(prim,
case annotation of
NONE => LambdaTypes.null_type_annotation
| SOME(annotation) => !annotation)},
([le],[]),Debugger_Types.null_backend_annotation)},
mapping')
end
| count_gc_objects' (LambdaTypes.APP(le, ([le'],[]),ty'),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
val (le' as {size=size', ...},mapping'') =
count_gc_objects' (le',ty,mapping',name)
val annotation =
if generate_debug_info
then
let
val this_ty =
case ty' of
NONE => LambdaTypes.null_type_annotation
| SOME ty' => !ty'
in
DebuggerUtilities.generate_recipe options (ty,this_ty,name)
end
else Debugger_Types.null_backend_annotation
in
({size=size+size', lexp=APP(le, ([le'],[]),annotation)},mapping'')
end
| count_gc_objects' (LambdaTypes.APP(le,(lel,fpel),ty'),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
fun countlist el =
foldr
(fn (form,(x,forms,mapping)) =>
let
val (le as {size=size,lexp=lexp},mapping') =
count_gc_objects'(form,ty,mapping,name)
in
(x+size,le::forms,mapping')
end)
(0,[],mapping')
el
val (size',lel',mapping'') = countlist lel
val (size'',fpel',mapping'') = countlist fpel
val annotation =
if generate_debug_info
then
let
val this_ty =
case ty' of
NONE => LambdaTypes.null_type_annotation
| SOME(ty') => !ty'
in
DebuggerUtilities.generate_recipe options (ty,this_ty,name)
end
else (Debugger_Types.null_backend_annotation)
in
({size=size+size'+size'', lexp=APP(le,(lel',fpel'),annotation)},mapping'')
end
| count_gc_objects' (LambdaTypes.FN((lvl,fpvl), lexp, status, name,ty,funinfo),_,
debug_info,_) =
let
val (le as {size=size, ...},debug_info) =
count_gc_objects' (lexp,ty,debug_info,name)
val debug_info =
if DebuggerUtilities.is_nulltype ty orelse not generate_debug_info
then debug_info
else
Debugger_Types.add_debug_info
(debug_info,
name,
Debugger_Types.FUNINFO {ty = DebuggerUtilities.slim_down_a_type ty,
is_leaf = false,
has_saved_arg = false,
annotations = [],
runtime_env = Debugger_Types.empty_runtime_env,
is_exn = false})
val newsize = if LambdaTypes.isLocalFn funinfo then size else size+1
in
({size=newsize, lexp=FN((lvl,fpvl), le, name,funinfo)},debug_info)
end
| count_gc_objects'(LambdaTypes.LETREC(lv_list, le_list, lexp),ty,mapping,name) =
let
val (le_list,mapping') =
foldr
(fn (form,(forms,mapping)) =>
let
val (form',mapping') =
count_gc_objects' (case form of
LambdaTypes.FN _ => form
| _ =>
Crash.impossible "Bad letrec form",
ty,mapping,name)
in
(form'::forms,mapping')
end)
([],mapping)
le_list
val (lexp as {size=size, ...},mapping'') = count_gc_objects' (lexp,ty,mapping',name)
in
({size=
foldl
(fn ({size=size, ...},x) => x+size)
size
le_list,
lexp=LETREC(lv_list, le_list, lexp)},mapping'')
end
| count_gc_objects'(LambdaTypes.SCON (scon, opt), _,mapping,_) =
({size=case scon of
Ident.INT _ => 0
| Ident.CHAR _ => 0
| Ident.WORD _ => 0
| Ident.REAL _ => 1
| Ident.STRING _ => 1,
lexp=SCON (scon, opt)},mapping)
| count_gc_objects'(LambdaTypes.MLVALUE value,_,mapping,_) =
({size= 1, lexp=MLVALUE value},mapping)
| count_gc_objects'(LambdaTypes.INT i,_,mapping,_) =
({size=0, lexp=INT i},mapping)
| count_gc_objects'(LambdaTypes.SWITCH(le, info, tag_le_list, le_opt),
ty,mapping,name) =
let
fun transform_tag((tag, le),mapping) =
let
val (le,mapping') =
count_gc_objects' (le,ty,mapping,name)
val (tag,mapping''') = case tag of
LambdaTypes.EXP_TAG lexp =>
let
val (lexp as {size=size, ...},mapping'') =
count_gc_objects' (lexp,ty,mapping',name)
in
if size <> 0 then
Crash.impossible"EXP_TAG contains static gc"
else
(EXP_TAG lexp,mapping'')
end
| LambdaTypes.IMM_TAG i => (IMM_TAG i,mapping')
| LambdaTypes.VCC_TAG i => (VCC_TAG i,mapping')
| LambdaTypes.SCON_TAG scon => (SCON_TAG scon,mapping')
in
((tag, le),mapping''')
end
fun transform_opt (NONE,mapping) =
((0, NONE),mapping)
| transform_opt(SOME le,mapping) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
in
((size, SOME le),mapping')
end
val (tag_le_list,mapping') =
foldr(fn (form,(forms,mapping)) =>
let
val (a,mapping') =
transform_tag (form,mapping)
in
(a::forms,mapping')
end)
([],mapping)
tag_le_list
val ((size1, le_opt),mapping'') =
transform_opt (le_opt,mapping')
val (le as {size=size, ...},mapping''') =
count_gc_objects' (le,ty,mapping'',name)
val sizes =
foldl
(fn ((tag, {size=size, ...}),x) =>
x + size +
(case tag of
SCON_TAG(Ident.REAL _, _) => 1
| SCON_TAG(Ident.STRING _, _) => 1
| SCON_TAG(Ident.INT _, _) => 0
| SCON_TAG(Ident.CHAR _, _) => 0
| SCON_TAG(Ident.WORD _, _) => 0
| _ => 0))
(size1+size)
tag_le_list
in
({size=sizes, lexp=SWITCH(le, info, tag_le_list, le_opt)},
mapping''')
end
| count_gc_objects'(LambdaTypes.STRUCT (le_list,_),ty,mapping,name) =
let
val (size,le_list,mapping') =
foldr
(fn (form,(x,forms,mapping)) =>
let
val (le as {size=size,lexp=lexp},mapping') =
count_gc_objects'(form,ty,mapping,name)
in
(x+size,le::forms,mapping')
end)
(0,[],mapping)
le_list
in
({size=size, lexp=STRUCT le_list},mapping')
end
| count_gc_objects'(LambdaTypes.SELECT({index,size=lsize,...}, le),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
val info = {index=index,size=lsize}
in
({size=size, lexp=SELECT(info, le)},mapping')
end
| count_gc_objects'(LambdaTypes.RAISE (le),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') =
count_gc_objects' (le,ty,mapping,name)
in
({size=size, lexp=RAISE (le)},mapping')
end
| count_gc_objects'(LambdaTypes.HANDLE(le, le',annotation),ty,mapping,name) =
let
val (le as {size=size, ...},mapping') = count_gc_objects' (le,ty,mapping,name)
val function = le'
val (le' as {size=size', ...},mapping'') =
count_gc_objects'(case function of
LambdaTypes.FN (a,b,status,c,_,info) =>
LambdaTypes.FN(a,b,status,c,DebuggerUtilities.handler_type,info)
| x => x,
ty,mapping',
name)
in
({size=size+size', lexp=HANDLE(le, le')},mapping'')
end
| count_gc_objects'(LambdaTypes.BUILTIN prim,ty,mapping,_) =
({size=0, lexp=BUILTIN(prim,ty)},mapping)
in
count_gc_objects' (exp,DebuggerUtilities.setup_function_type,
if generate_debug_info then
Debugger_Types.add_debug_info
(mapping,
setup_function,
Debugger_Types.FUNINFO {ty = DebuggerUtilities.setup_function_type,
is_leaf = false,
has_saved_arg = false,
annotations = [],
runtime_env = Debugger_Types.empty_runtime_env,
is_exn = false})
else mapping,
setup_function)
end
end
;
