require "../utils/diagnostic";
require "../utils/lists";
require "../utils/crash";
require "../utils/map";
require "../lambda/auglambda";
require "../lambda/lambdaprint";
require "machperv";
require "mlworks_io";
require "library";
functor Library(
structure Diagnostic : DIAGNOSTIC
structure Lists : LISTS
structure Crash : CRASH
structure NewMap : MAP
structure AugLambda : AUGLAMBDA
structure LambdaPrint : LAMBDAPRINT
structure MachPerv : MACHPERV
structure Io : MLWORKS_IO
sharing LambdaPrint.LambdaTypes = AugLambda.LambdaTypes
sharing type MachPerv.Pervasives.pervasive = LambdaPrint.LambdaTypes.Primitive
) : LIBRARY =
struct
structure Pervasives = MachPerv.Pervasives
structure LambdaTypes = LambdaPrint.LambdaTypes
structure NewMap = NewMap
structure Set = LambdaTypes.Set
structure AugLambda = AugLambda
structure Ident = LambdaTypes.Ident
type CompilerOptions = MachPerv.CompilerOptions
fun eta_abstract (le,annotation,ty) =
let
val lvar = LambdaTypes.new_LVar()
in
LambdaTypes.FN (([lvar],[]),
LambdaTypes.APP (le, ([LambdaTypes.VAR lvar],[]), SOME(ty)),
LambdaTypes.BODY,
annotation,
LambdaTypes.null_type_annotation,
LambdaTypes.internal_funinfo)
end
fun apply_one_level appsub =
let
fun apopt f (SOME x) = SOME (f x)
| apopt _ NONE = NONE
fun apply_tagval (LambdaTypes.EXP_TAG tagexp, value) =
(LambdaTypes.EXP_TAG (appsub tagexp), appsub value)
| apply_tagval (tagexp, value) =
(tagexp, appsub value)
fun apply (le as LambdaTypes.VAR _) = le
| apply (LambdaTypes.FN(args, le, status, x,ty,instance)) =
LambdaTypes.FN (args, appsub le,status, x,ty,instance)
| apply (LambdaTypes.LET((lv,info,lb),le)) =
LambdaTypes.LET((lv,info,appsub lb),appsub le)
| apply (LambdaTypes.LETREC(lvl, lel, le)) =
LambdaTypes.LETREC (lvl, map appsub lel, appsub le)
| apply (LambdaTypes.APP(p, (q,r), annotation)) =
LambdaTypes.APP(appsub p, (map appsub q, map appsub r), annotation)
| apply (le as LambdaTypes.SCON _) = le
| apply (le as LambdaTypes.MLVALUE _) = le
| apply (le as LambdaTypes.INT _) = le
| apply (LambdaTypes.SWITCH(le, info, clel, leo)) =
LambdaTypes.SWITCH
(appsub le,
info,
map apply_tagval clel,
apopt appsub leo)
| apply (LambdaTypes.STRUCT (lel,ty)) = LambdaTypes.STRUCT (map appsub lel,ty)
| apply (LambdaTypes.SELECT (fld, le)) = LambdaTypes.SELECT (fld, appsub le)
| apply (LambdaTypes.RAISE (le)) = LambdaTypes.RAISE (appsub le)
| apply (LambdaTypes.HANDLE (le1, le2,annotation)) = LambdaTypes.HANDLE (appsub le1,
appsub le2,
annotation)
| apply (le as LambdaTypes.BUILTIN _) = le
in
apply
end
fun externalise (options,expression, extern_map) =
let
open LambdaTypes
fun externalise' (APP (BUILTIN pervasive, (el,fpel), ty)) =
if MachPerv.is_inline (options,pervasive) then
APP (BUILTIN pervasive, (map externalise' el, map externalise' fpel), ty)
else
APP (VAR (NewMap.apply'(extern_map, pervasive)),
(map externalise' el, map externalise' fpel),
ty)
| externalise' (expression as BUILTIN pervasive) =
if (MachPerv.is_inline (options, pervasive) andalso
MachPerv.is_fun pervasive) then
externalise' (eta_abstract
(expression,"Builtin function " ^ Pervasives.print_pervasive pervasive,
ref null_type_annotation))
else
VAR (NewMap.apply'(extern_map, pervasive))
| externalise' expression =
apply_one_level externalise' expression
in
externalise' expression
end
fun external_references (options, expression) =
let
open LambdaTypes
fun find (done, APP (BUILTIN pervasive, (el,fpel), ty)) =
if MachPerv.is_inline (options,pervasive) then
Lists.reducel find ((MachPerv.implicit_references pervasive) @ done,
(fpel @ el))
else
Lists.reducel find (pervasive :: done, fpel @ el)
| find (done, BUILTIN pervasive) =
if (MachPerv.is_inline (options, pervasive) andalso
MachPerv.is_fun pervasive) then
(MachPerv.implicit_references pervasive) @ done
else
pervasive :: done
| find (done, SWITCH (expression, info, cases, default)) =
let
fun find_case (done, (EXP_TAG expression, expression')) =
find (find (done, expression), expression')
| find_case (done, (SCON_TAG (Ident.STRING _, _), expression')) =
let
val references =
Pervasives.STRINGEQ ::
(MachPerv.implicit_references Pervasives.STRINGEQ)
in
find (references @ done, expression')
end
| find_case (done, (_, expression')) =
find (done, expression')
fun find_option (done, SOME expression) =
find (done, expression)
| find_option (done, NONE) = done
in
Lists.reducel find_case
(find_option (find (done, expression), default), cases)
end
| find (done, VAR _) = done
| find (done, FN (_, expression,_,_,_,_)) = find (done, expression)
| find (done, LET ((_,_, binding),expression)) =
find (find (done,binding), expression)
| find (done, APP (expression, (el,fpel), _)) =
Lists.reducel find (find (done, expression), fpel @ el)
| find (done, SCON _) = done
| find (done, MLVALUE _) = done
| find (done, INT _) = done
| find (done, SELECT (_, expression)) = find (done, expression)
| find (done, STRUCT (expressions,_)) =
Lists.reducel find (done, expressions)
| find (done, RAISE (expression)) = find (done, expression)
| find (done, HANDLE (expression, expression',_)) =
find (find (done, expression), expression')
| find (done, LETREC (_, expressions, expression')) =
Lists.reducel find (find (done, expression'), expressions)
in
Set.list_to_set (find ([], expression))
end
fun implicit_external_references expression =
let
open AugLambda
fun find (done,
{lexp=APP ({lexp=BUILTIN(pervasive,_), ...}, (el,fpel),_),
size=_}) =
Lists.reducel find ((MachPerv.implicit_references pervasive) @ done,
fpel @ el)
| find (done, {lexp=SWITCH (expression, info, cases, default),
...}) =
let
fun find_case (done, (EXP_TAG expression, expression')) =
find (find (done, expression), expression')
| find_case (done, (SCON_TAG (Ident.STRING _, _), expression')) =
let
val references =
MachPerv.implicit_references Pervasives.STRINGEQ
in
find (references @ done, expression')
end
| find_case (done, (_, expression')) =
find (done, expression')
fun find_option (done, SOME expression) =
find (done, expression)
| find_option (done, NONE) = done
in
Lists.reducel find_case
(find_option (find (done, expression), default), cases)
end
| find (done, {lexp=BUILTIN pervasive, ...}) = done
| find (done, {lexp=VAR _, ...}) = done
| find (done, {lexp=FN (_, expression,_,_), ...}) =
find (done, expression)
| find (done, {lexp=LET ((_,_, binding), expression),...}) =
find (find (done, binding), expression)
| find (done, {lexp=APP (expression, (el,fpel),_), ...}) =
Lists.reducel find (find (done, expression), fpel @ el)
| find (done, {lexp=SCON _, ...}) = done
| find (done, {lexp=MLVALUE _, ...}) = done
| find (done, {lexp=INT _, ...}) = done
| find (done, {lexp=SELECT (_, expression), ...}) =
find (done, expression)
| find (done, {lexp=STRUCT expressions, ...}) =
Lists.reducel find (done, expressions)
| find (done, {lexp=RAISE (expression), ...}) = find (done, expression)
| find (done, {lexp=HANDLE (expression, expression'), ...}) =
find (find (done, expression), expression')
| find (done, {lexp=LETREC (_, expressions, expression'), ...}) =
Lists.reducel find (find (done, expression'), expressions)
in
Set.list_to_set (find ([], {lexp=expression, size=0}))
end
fun build_external_environment (options, expression) =
let
val external_pervasives = external_references (options,expression)
val _ = Diagnostic.output 1
(fn _ =>
"Library: External pervasives:" ::
(map (fn p => " " ^ Pervasives.print_pervasive p)
(Set.set_to_list external_pervasives)))
in
if Set.empty_setp external_pervasives then
(NewMap.empty (Pervasives.order,Pervasives.eq),
externalise(options, expression, NewMap.empty (Pervasives.order,Pervasives.eq)))
else
let
val library_lvar = LambdaTypes.new_LVar()
val library_binding =
let
open LambdaTypes
in
(library_lvar,
SELECT
({index = 0, size = 2,selecttype=TUPLE},
APP (BUILTIN Pervasives.LOAD_STRING,
([SCON (Ident.STRING Io.builtin_library_name, NONE)],[]),
NONE)))
end
fun make_bindings (bindings, map, []) =
(bindings, map)
| make_bindings (bindings, map, pervasive::pervasives) =
let
val lvar = LambdaTypes.new_LVar()
val select =
LambdaTypes.SELECT
({index = Pervasives.field_number pervasive,
size = Pervasives.nr_fields,
selecttype = LambdaTypes.STRUCTURE},
LambdaTypes.VAR library_lvar)
val map' =
NewMap.define(map, pervasive, lvar)
in
make_bindings ((lvar, select)::bindings,
map',
pervasives)
end
val (bindings, map) =
make_bindings ([], NewMap.empty (Pervasives.order,Pervasives.eq),
Set.set_to_list external_pervasives)
fun wrap_lets (expression, vas) =
let
fun nest ((var,arg),body) = LambdaTypes.LET((var, NONE, arg),body)
in
foldl nest expression (rev vas)
end
val expression' =
wrap_lets
(externalise (options, expression, map), library_binding::bindings)
val _ = Diagnostic.output 3
(fn _ =>
["Library: Externalised lambda expression:\n",
LambdaPrint.string_of_lambda expression'])
in
(map, expression')
end
end
end
;
