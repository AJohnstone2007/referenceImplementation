require "module_dec";
require "import_export";
require "../basis/__list";
require "../lambda/environprint";
require "../main/info";
functor ImportExport (structure ModuleDec: MODULE_DEC
structure EnvironPrint: ENVIRONPRINT
structure Info: INFO): IMPORT_EXPORT =
struct
structure EnvironTypes = EnvironPrint.EnvironTypes
structure ModuleDec = ModuleDec
structure ModuleName = ModuleDec.ModuleName
type name = ModuleName.t
type context = EnvironTypes.Top_Env
datatype env =
EMPTY
| BINDING of (name * value)
| LAYER of env * env
| UNKNOWN
withtype value = env
exception Undefined of name
and IllegalToplevelOpen
and InternalError of string
fun layer (EMPTY, e) = e
| layer (UNKNOWN, e) = e
| layer (e1, e2) = LAYER (e1, e2)
fun mkBaseLookup senv name =
( print "Looking up ";
print (ModuleName.nameOf name);
print "\n";
EMPTY )
fun bindSetOf EMPTY = ModuleName.empty
| bindSetOf (BINDING (name, _)) = ModuleName.singleton name
| bindSetOf (LAYER (e1, e2)) =
ModuleName.union (bindSetOf e1, bindSetOf e2)
| bindSetOf UNKNOWN = ModuleName.empty
fun imports (dcl, none, glob, combine, sourcename) = let
fun look otherwise = let
fun lk EMPTY name = otherwise name
| lk (BINDING (n, v)) name =
if ModuleName.equal(name, n) then
(v, none)
else
otherwise name
| lk (LAYER (e1, e2)) name = look (lk e2) e1 name
| lk UNKNOWN name = (UNKNOWN, none)
in
lk
end
fun lookup_name (name, env) = let
in
look glob env name
handle Undefined name =>
( Info.error (Info.make_default_options())
(Info.RECOVERABLE,
Info.Location.FILE sourcename,
"Cannot find " ^ (ModuleName.makestring name)
^ " during dependency analysis of unit "
^ sourcename ^ ".\n");
(UNKNOWN, none) )
end
fun lookup_path (path, env) = let
val (e1, il) = lookup_name (ModuleName.pathFirstModule path, env)
fun loop (NONE, env) = (env, il)
| loop (SOME p, env) = let
val(e, _) = look
(fn n =>
(print (sourcename ^
": Undefined " ^
(ModuleName.makestring n) ^
" in path " ^
(ModuleName.nameOfPath path) ^
"\n");
(UNKNOWN, none)))
env
(ModuleName.pathFirstModule p)
in
loop (ModuleName.restOfPath p, e)
end
in
loop (ModuleName.restOfPath path, e1)
end
fun ModuleNames2il (nl, env, il0) = let
fun touch (n, il) = let
val (_, ril) = lookup_name (n, env)
in
combine (ril, il)
end
in
ModuleName.fold touch il0 nl
end
fun i_decl (dcl, env, il0) =
case dcl of
ModuleDec.StrDec l => let
fun bind ({ name, def, constraint = NONE}, (e, il)) =
let
val (v, il) = i_strExp (def, env, il)
in
(LAYER (BINDING (name, v), e), il)
end
| bind ({ name, def, constraint = SOME c }, (e, il)) =
let
val (_, il) = i_strExp (def, env, il)
val (v, il) = i_strExp (c, env, il)
in
(LAYER (BINDING (name, v), e), il)
end
in
foldl bind (EMPTY, il0) l
end
| ModuleDec.FctDec l =>
let
fun param ((n_opt, se), (e, il)) = let
val (e1, il) = i_strExp (se, env, il)
val e = case n_opt of
NONE => layer (e1, e)
| SOME n => LAYER (BINDING (n, e1), e)
in
(e, il)
end
fun bind ({ name, params, body, constraint }, (e, il)) =
let
val (v, il) =
let
val (benv, il) = foldl param (env, il) params
val (r as (_, il)) = i_strExp (body, benv, il)
in
case constraint of
NONE => r
| SOME se => i_strExp (se, benv, il)
end
in
(LAYER (BINDING (name, v), e), il)
end
in
foldl bind (EMPTY, il0) l
end
| ModuleDec.LocalDec (d1, d2) => let
val (e1, il) = i_decl (d1, env, il0)
val lenv = layer (e1, env)
val (e2, il) = i_decl (d2, lenv, il)
in
(e2, il)
end
| ModuleDec.SeqDec l => let
fun lay (dcl, (small_e, big_e, il)) = let
val (de, il) = i_decl (dcl, big_e, il)
in
(layer (de, small_e), layer (de, big_e), il)
end
val (e, _, il) = foldl lay (EMPTY, env, il0) l
in
(e, il)
end
| ModuleDec.OpenDec sel => let
fun open' (se, (e, il)) = let
val (oe, il') = i_strExp (se, env, il)
in
(layer (oe, e), il')
end
in
foldl open' (EMPTY, il0) sel
end
| ModuleDec.DecRef nl => (EMPTY, ModuleNames2il (nl, env, il0))
and i_strExp (se, env, il) =
case se of
ModuleDec.VarStrExp p => let
val (e, pil) = lookup_path (p, env)
in
(e, combine (pil, il))
end
| ModuleDec.BaseStrExp dcl => i_decl (dcl, env, il)
| ModuleDec.AppStrExp (p, se) => let
val (e, pil) = lookup_name (p, env)
in
(e, combine (pil, #2 (i_strExp (se, env, il))))
end
| ModuleDec.LetStrExp (dcl, se) => let
val (e, il) = i_decl (dcl, env, il)
val env = layer (e, env)
in
i_strExp (se, env, il)
end
| ModuleDec.AugStrExp (se, s) => i_strExp (se, env, ModuleNames2il (s, env, il))
| ModuleDec.ConStrExp (stre, sige) => let
val (_, il') = i_strExp (stre, env, il)
in
i_strExp (sige, env, il')
end
val (e, i) = i_decl (dcl, EMPTY, none)
fun get_ext n =
#1 (look (fn m => raise Undefined m) e n)
in
(get_ext, i, fn () => bindSetOf e)
end
fun exports (d, filename) = let
fun e (ModuleDec.StrDec l, (ctxt, a)) = (l::ctxt, ModuleName.addl (map #name l, a))
| e (ModuleDec.FctDec l, (ctxt, a)) = (ctxt, ModuleName.addl (map #name l, a))
| e (ModuleDec.LocalDec (l, b), p) = e (b, p)
| e (ModuleDec.SeqDec l, p) = foldl e p l
| e (ModuleDec.OpenDec l, p) = foldl find p l
| e (ModuleDec.DecRef _, p) = p
and top_level_open_of (s, p) =
( print ("Ignoring top-level open of "
^ (ModuleName.makestring s)
^" encountered in unit " ^ filename ^ ".\n");
print "The resulting dependency analysis may be inaccurate.\n";
p )
and find (ModuleDec.VarStrExp v, p as (ctxt, a)) =
( case ModuleName.mnListOfPath v of
[s] =>
let fun look_in_ctxt [] =
top_level_open_of(s,p)
| look_in_ctxt (l::t) =
( case List.find (fn h =>
let val s' = #name h
val eq = ModuleName.equal (s, s')
in eq
end) l of
NONE => look_in_ctxt t
| SOME {name, def, constraint} =>
let val exp =
case constraint of
SOME exp => exp
| NONE => def
in case exp of
ModuleDec.BaseStrExp dec => e(dec, (t, a))
| ModuleDec.ConStrExp(_, ModuleDec.BaseStrExp dec) =>
e(dec, (t, a))
| _ =>
top_level_open_of(s,p)
end )
in look_in_ctxt ctxt
end
| _ =>
top_level_open_of(ModuleName.pathFirstModule v,p))
| find (exp, _) = (print "Ooops 1\n";
raise IllegalToplevelOpen)
in
#2(e (d, ([], ModuleName.empty)))
end
end
;
