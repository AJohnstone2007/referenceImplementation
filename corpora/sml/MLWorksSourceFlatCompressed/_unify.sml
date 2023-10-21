require "../utils/lists";
require "../utils/print";
require "../utils/crash";
require "../main/options";
require "../typechecker/types";
require "unify";
functor Unify(
structure Lists : LISTS
structure Print : PRINT
structure Crash : CRASH
structure Options : OPTIONS
structure Types : TYPES
) : UNIFY =
struct
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
type options = Options.options
open Datatypes
datatype record =
RIGID of (Ident.Lab * Type) list
| FLEX of (Ident.Lab * Type) list
type 'a refargs = 'a ref * 'a
datatype substitution =
SUBST of ((int * Datatypes.Type * Datatypes.Instance) refargs list *
(int * bool * Type * bool * bool) refargs list *
Type refargs list)
datatype unify_result =
OK
| FAILED of Type * Type
| RECORD_DOMAIN of record * record
| EXPLICIT_TYVAR of Type * Type
| EQ_AND_IMP of bool * bool * Type
| CIRCULARITY of Type * Type
| OVERLOADED of Ident.TyVar * Type
| SUBSTITUTION of substitution
val assign1 : (((int * Type * Instance) ref
* (int * Type * Instance)) -> unit) ref = ref(fn _ => ())
val assign2 : (((int * bool * Type * bool * bool) ref
* (int * bool * Type * bool * bool)) -> unit) ref = ref(fn _ => ())
val assign3 : ((Type ref * Type) -> unit) ref = ref(fn _ => ())
fun apply_sub1(tyv,SUBST S) = ref(Lists.assoc(tyv,#1(S)))
fun apply_sub2(recty,SUBST S) = ref(Lists.assoc(recty,#2(S)))
fun apply_sub3(ty,SUBST S) = ref(Lists.assoc(ty,#3(S)))
fun apply(SUBSTITUTION S,ty) =
let
fun substitute_type(ty as TYVAR(tyv as ref(_,NULLTYPE,_),id)) =
(TYVAR(apply_sub1(tyv,S),id)
handle Lists.Assoc => ty)
| substitute_type(ty as METATYVAR(tyv as ref(_,NULLTYPE,_),b1,b2)) =
(METATYVAR(apply_sub1(tyv,S),b1,b2)
handle Lists.Assoc => ty)
| substitute_type(METATYVAR (ref (_,ty,_),_,_)) = substitute_type ty
| substitute_type(ty as META_OVERLOADED
(tyv as ref NULLTYPE,v,valid,loc)) =
(META_OVERLOADED(apply_sub3(tyv,S),v,valid,loc)
handle Lists.Assoc => ty)
| substitute_type(META_OVERLOADED (ref ty,_,_,_)) = substitute_type ty
| substitute_type(ty as METARECTYPE (tyv as ref (_,_,NULLTYPE,_,_))) =
(METARECTYPE(apply_sub2(tyv,S))
handle Lists.Assoc => ty)
| substitute_type(METARECTYPE (ref (_,_,ty,_,_))) =
substitute_type(ty)
| substitute_type((RECTYPE amap)) =
RECTYPE(NewMap.map substitute_type_map amap)
| substitute_type(FUNTYPE(arg,res)) =
FUNTYPE(substitute_type(arg),substitute_type(res))
| substitute_type(CONSTYPE (tylist,tyname)) =
CONSTYPE(map substitute_type tylist,tyname)
| substitute_type ty = ty
and substitute_type_map(_, ty) = substitute_type ty
in
substitute_type(ty)
end
| apply _ = Crash.impossible "NOT SUBSTITUTION:apply:unify"
local
exception Failed of unify_result
fun fail ex res = raise (Failed (ex res))
fun remove_cons_meta (ty as META_OVERLOADED (ref ty',_,_,_)) =
(case ty' of
NULLTYPE => ty
| _ => remove_cons_meta ty')
| remove_cons_meta (ty as METARECTYPE (ref {2 = b,3 = t,...})) =
if (not b) orelse (case t of METARECTYPE _ => true | _ => false) then
remove_cons_meta t
else
ty
| remove_cons_meta (ty as CONSTYPE (l, tyname)) =
(case tyname of
METATYNAME {1 = ref tyfun,...} =>
(case tyfun of
ETA_TYFUN _ => remove_cons_meta (Types.apply (tyfun, l))
| TYFUN _ => remove_cons_meta (Types.apply (tyfun, l))
| _ => ty
)
| _ => ty)
| remove_cons_meta ty = ty
fun propagate_level level
(recty as (METARECTYPE (r as ref (level',flex,ty,eq,imp)))) =
(if level < level'
then r := (level, flex, propagate_level level ty, eq, imp)
else ();
recty)
| propagate_level level (RECTYPE amap) =
RECTYPE(NewMap.map (propagate_level_map level) amap)
| propagate_level level (ty as (METATYVAR (r as ref (level',t,i),_,_))) =
(if level < level'
then r := (level,propagate_level level t,i)
else ();
ty)
| propagate_level _ ty =
ty
and propagate_level_map level (_, ty) = propagate_level level ty
fun eq_and_imp (options,eq,imp,(TYVAR (_,Ident.TYVAR (_,eq',imp')))) =
let
val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...}
,...} = options
val imp = imp andalso old_definition
val imp' = imp' andalso old_definition
in
if eq
then (eq' andalso (if imp
then imp'
else true))
else (if imp
then imp'
else true)
end
| eq_and_imp (options,eq,imp,FUNTYPE (a,r)) =
if eq
then false
else (eq_and_imp (options,eq,imp,a)) andalso
(eq_and_imp (options,eq,imp,r))
| eq_and_imp (options,eq,imp,DEBRUIJN (_,eq',imp',_)) =
let
val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...}
,...} = options
val imp = imp andalso old_definition
val imp' = imp' andalso old_definition
in
if eq
then (eq' andalso (if imp
then imp'
else true))
else (if imp
then imp'
else true)
end
| eq_and_imp (options,eq,imp,
CONSTYPE (tylist,METATYNAME{1 = ref tyfun,
4 = ref eq',...})) =
(eq_and_imp (options,eq,imp,Types.apply (tyfun,tylist))
handle NullTyfun =>
let fun collect [] = true
| collect (h::t) = eq_and_imp (options,eq,imp,h) andalso
(collect t)
in
((not eq)
orelse
(eq' andalso collect tylist))
end)
| eq_and_imp (options,eq,imp,CONSTYPE ([],name)) =
if eq
then Types.eq_attrib name
else true
| eq_and_imp (options,eq,imp,CONSTYPE ([h],name)) =
if eq then
if Types.has_ref_equality name then
eq_and_imp (options,false,imp,h)
else Types.eq_attrib name andalso eq_and_imp (options,eq,imp,h)
else eq_and_imp (options,eq,imp,h)
| eq_and_imp (options,eq,imp,CONSTYPE (t,n)) =
let fun collect [] = true
| collect (h::t) = eq_and_imp (options,eq,imp,h)
andalso (collect t)
in
((not eq) orelse (Types.eq_attrib n)) andalso (collect(t))
end
| eq_and_imp (options,eq,imp,RECTYPE amap) =
NewMap.forall (fn (_, x) => eq_and_imp(options,eq, imp, x)) amap
| eq_and_imp (options,eq,imp,NULLTYPE) = true
| eq_and_imp (options,eq,imp,
METATYVAR (x as ref (n,NULLTYPE,i),eq',imp')) =
if (eq andalso not eq') orelse (imp andalso not imp')
then (x := (n,
METATYVAR (ref (n,NULLTYPE,NO_INSTANCE),
eq orelse eq',imp orelse imp'),i);
true)
else true
| eq_and_imp (options,eq,imp,METATYVAR (ref (_,t,_),eq',imp')) =
if (eq andalso not eq') orelse (imp andalso not imp')
then (eq_and_imp (options,eq,imp,t))
else true
| eq_and_imp (options,eq,imp,META_OVERLOADED(ref NULLTYPE,tv,_,_)) =
(!Types.real_tyname_equality_attribute) orelse
(
(not(tv=Ident.real_tyvar orelse tv=Ident.real_literal_tyvar))
orelse (not eq))
| eq_and_imp (options,eq,imp,m as META_OVERLOADED _) = true
| eq_and_imp (options,eq,imp,
METARECTYPE (r as ref (n,true,t,eq',imp'))) =
if (eq andalso not eq') orelse (imp andalso not imp')
then (r := (n,true,t,eq orelse eq',imp orelse imp');
true)
else true
| eq_and_imp (options,eq,imp,
METARECTYPE (r as ref (n,false,t,eq',imp'))) =
if (eq andalso not eq') orelse (imp andalso not imp')
then (eq_and_imp (options,eq,imp,t))
else true
fun not_occurs_tyfun (ty,TYFUN (ty',_)) =
not_occurs (ty,ty')
| not_occurs_tyfun (ty,ETA_TYFUN tyname) =
not_occurs_tyname (ty,tyname)
| not_occurs_tyfun (ty,NULL_TYFUN _) =
true
and not_occurs_tyname (ty,TYNAME _) = true
| not_occurs_tyname (ty,METATYNAME (ref tyfun,_,_,_,_,_)) =
not_occurs_tyfun (ty,tyfun)
and not_occurs (METATYVAR (ref (tyv as (n,_,_)),_,_),
TYVAR (r' as ref (n',_,_),_)) =
(n > n')
orelse
(r' := tyv;
true)
| not_occurs (ameta,TYVAR _) = true
| not_occurs (ameta,FUNTYPE(a,r)) =
not_occurs (ameta,a) andalso not_occurs (ameta,r)
| not_occurs (ameta,t as DEBRUIJN _) = true
| not_occurs (ameta,CONSTYPE (tylist,tyname)) =
not_occurs_tyname (ameta, tyname)
andalso
Lists.forall (fn x => not_occurs(ameta,x)) tylist
| not_occurs (ameta,RECTYPE amap) =
NewMap.forall (fn (_, x) => not_occurs(ameta, x)) amap
| not_occurs (ameta,NULLTYPE) = true
| not_occurs (ameta as METATYVAR (ref (n,NULLTYPE,i),_,_),
ameta' as METATYVAR (x as ref (n',NULLTYPE,_),_,_)) =
if (Types.type_eq (ameta,ameta',true,true))
then false
else
(n > n')
orelse
(x := (n,NULLTYPE,i);
true)
| not_occurs (ameta as METATYVAR (ref (n,_,i),_,_),
ameta' as METATYVAR (x as ref (n',NULLTYPE,_),_,_)) =
if (Types.type_eq (ameta,ameta',true,true))
then false
else
(n > n')
orelse
(x := (n,NULLTYPE,i);
true)
| not_occurs (ameta,t as METATYVAR (ref (_,NULLTYPE,_),eq',imp')) =
not (Types.type_eq (ameta,t,true,true))
| not_occurs (ameta as METATYVAR (ref (n,_,i),_,_),
METATYVAR (x as ref (n',t,_),_,_)) =
(if (n > n') then () else x := (n,t,i);
not_occurs (ameta,t))
| not_occurs (ameta,METATYVAR (ref (_,t,_),_,_)) = not_occurs (ameta,t)
| not_occurs (ameta,META_OVERLOADED _) = true
| not_occurs (ameta,t as METARECTYPE (ref (_,true,arec,_,_))) =
not_occurs (ameta,arec)
| not_occurs (ameta,METARECTYPE (ref (_,false,t,_,_))) =
not_occurs (ameta,t)
exception NotRectype
fun check set =
let
fun check(Ident.VAR sy) =
let
val sy = Ident.Symbol.symbol_name sy
in
Lists.member(sy, set)
end
| check _ = false
in
check
end
val predicates = ["<", ">", "<=", ">="]
val check_predicate = check predicates
val mod_div = ["mod", "div"]
val check_mod_or_div = check mod_div
fun unify_lists (options,[], []) = ()
| unify_lists (options,h :: t, h' :: t') =
(unify' (options,h, h');
unify_lists (options,t, t'))
| unify_lists _ =
Crash.impossible "Unify.unify_lists"
and unify'(options,atype, atype') =
let
val atype = remove_cons_meta atype
val atype' = remove_cons_meta atype'
in
unify (options, atype, atype')
end
and unify(options,atype, atype') =
case (atype, atype') of
(TYVAR (ref (n,_,_), id), TYVAR (ref (n',_,_), id')) =>
if (n = n' andalso id = id') then
()
else
fail EXPLICIT_TYVAR (atype, atype')
| (DEBRUIJN debruijn, DEBRUIJN debruijn') =>
if ((fn ty=>(#1(ty),#2(ty),#3(ty)))debruijn) =
((fn ty=>(#1(ty),#2(ty),#3(ty)))debruijn')
then ()
else
fail FAILED (atype, atype')
| (FUNTYPE (a, r), FUNTYPE (a', r')) =>
(unify'(options,a, a');
unify'(options,r, r'))
| (RECTYPE amap, RECTYPE amap') =>
let
fun unify'' (ty, ty') = (unify'(options,ty, ty') ; true)
val res = NewMap.eq unify'' (amap, amap')
in
if res then ()
else
let
val rec1 = NewMap.to_list_ordered amap
val rec2 = NewMap.to_list_ordered amap'
in
fail RECORD_DOMAIN (RIGID rec1, RIGID rec2)
end
end
| (METATYVAR (r as ref (n, NULLTYPE,_), eq, imp), _) =>
do_unify(options,r, eq, imp, atype')
| (_, METATYVAR (r' as ref (n', NULLTYPE,_), eq', imp')) =>
do_unify(options,r', eq', imp', atype)
| (METATYVAR (ref (n, t,_), _, _), _) =>
unify'(options,t, atype')
| (_, METATYVAR (ref (n', t',_), _, _)) =>
unify'(options,atype, t')
| (META_OVERLOADED (ol as (r as ref NULLTYPE, tv, _, _)), _) =>
do_overload(ol, atype')
| (_, META_OVERLOADED (ol as (r' as ref NULLTYPE, tv, _, _))) =>
do_overload(ol, atype)
| (METARECTYPE (r as ref (_, true, RECTYPE _, _, _)), _) =>
(do_unify_rec(options,r, atype')
handle NotRectype => fail FAILED (atype,atype'))
| (_, METARECTYPE (r' as ref (_, true, RECTYPE _, _, _))) =>
(do_unify_rec(options,r', atype)
handle NotRectype => fail FAILED (atype,atype'))
| (CONSTYPE (l, n), CONSTYPE (l', n')) =>
if Types.tyname_eq (n, n') then
unify_lists(options,l, l')
else
fail FAILED (atype, atype')
| (_, _) => fail FAILED (atype, atype')
and do_unify(options,r as ref (n, NULLTYPE,inst), eq, imp, t') =
(case t' of
METATYVAR (r' as ref (n', NULLTYPE,inst'), eq', imp') =>
if r = r'
then ()
else
(let
val n'' = if n < n' then n else n'
in
((!assign1)(r',(n'', NULLTYPE,inst'));
(!assign1)(r,(n'', t',inst)))
end;
if eq_and_imp (options,eq, imp, t')
then ()
else Crash.impossible "Unify.do_unify METATYVAR and METATYVAR")
| METATYVAR (r' as ref (n', t'',inst'), _, _) =>
if (n > n') andalso eq_and_imp (options,eq, imp, t') then
(!assign1)(r,(n', t',inst))
else do_unify(options,r, eq, imp, t'')
| CONSTYPE (l, METATYNAME{1=ref(tyfun as ETA_TYFUN _), ...}) =>
do_unify(options,r, eq, imp, Types.apply (tyfun, l))
| CONSTYPE (l, METATYNAME{1=ref(tyfun as TYFUN _), ...}) =>
do_unify(options,r, eq, imp, Types.apply (tyfun, l))
| TYVAR (r' as ref (n',t,inst'),_) =>
if eq_and_imp (options,eq, imp, t')
then
let
val n'' = if n < n' then n else n'
in
((!assign1)(r,(n'', t',inst));
(!assign1)(r',(n'',t,inst')))
end
else fail EQ_AND_IMP (eq, imp, t')
| METARECTYPE (r' as ref (n', true, ty, eq', imp')) =>
if not_occurs (METATYVAR (r, eq, imp), t')
then
if eq_and_imp (options,eq, imp, t')
then
let
val n'' = if n < n' then n else n'
in
((!assign1)(r,(n'', t',inst));
(!assign2)(r',(n'', true, propagate_level n'' ty,
eq orelse eq',
imp orelse imp')))
end
else fail EQ_AND_IMP (eq, imp, t')
else fail CIRCULARITY (METATYVAR (r, eq, imp), t')
| ty =>
if not_occurs (METATYVAR (r, eq, imp), t')
then
if eq_and_imp (options,eq, imp, t') then
((case t' of
NULLTYPE => Crash.impossible "nulltype"
| _ => ());
(!assign1)(r,(n, t',inst)))
else
fail EQ_AND_IMP (eq, imp, t')
else
fail CIRCULARITY (METATYVAR (r, eq, imp), t'))
| do_unify _ = Crash.impossible "Unify.do_unify"
and do_overload (ol as (r, tv, valid, loc), t') =
case t' of
META_OVERLOADED(r' as ref NULLTYPE,tv',_,_) =>
if (r = r') then
()
else if tv = Ident.numtext_tyvar then
(!assign3)(r, t')
else if tv' = Ident.numtext_tyvar then
(!assign3)(r', META_OVERLOADED ol)
else if tv = Ident.num_tyvar then
(!assign3)(r, t')
else if tv' = Ident.num_tyvar then
(!assign3)(r', META_OVERLOADED ol)
else if tv = Ident.wordint_tyvar then
if (tv' = Ident.real_tyvar orelse
tv' = Ident.real_literal_tyvar) then
fail OVERLOADED (tv, t')
else
(!assign3)(r, t')
else if tv' = Ident.wordint_tyvar then
if (tv = Ident.real_tyvar orelse
tv = Ident.real_literal_tyvar) then
fail OVERLOADED (tv', META_OVERLOADED ol)
else
(!assign3)(r', META_OVERLOADED ol)
else if tv = Ident.realint_tyvar then
if tv' = Ident.word_literal_tyvar then
fail OVERLOADED (tv, t')
else
(!assign3)(r, t')
else if tv' = Ident.realint_tyvar then
if tv = Ident.word_literal_tyvar then
fail OVERLOADED (tv', META_OVERLOADED ol)
else
(!assign3)(r', META_OVERLOADED ol)
else if tv = Ident.real_tyvar then
if (tv' = Ident.real_tyvar orelse
tv' = Ident.real_literal_tyvar) then
(!assign3)(r, t')
else
fail OVERLOADED (tv, t')
else if tv' = Ident.real_tyvar then
if tv = Ident.real_literal_tyvar then
(!assign3)(r', META_OVERLOADED ol)
else
fail OVERLOADED (tv, t')
else if tv' = tv then
(!assign3)(r, t')
else
fail OVERLOADED (tv, t')
| META_OVERLOADED(ref t'',_,_,_) =>
do_overload(ol, t'')
| _ =>
if tv = Ident.num_tyvar
andalso Types.num_typep t' then
(!assign3)(r,t')
else if tv = Ident.numtext_tyvar
andalso (Types.num_typep t'
orelse Types.num_or_string_typep t') then
(!assign3)(r,t')
else if tv = Ident.wordint_tyvar
andalso (Types.wordint_typep t') then
(!assign3)(r,t')
else if tv = Ident.realint_tyvar
andalso (Types.realint_typep t') then
(!assign3)(r,t')
else if tv = Ident.int_literal_tyvar
andalso Types.int_typep t' then
(!assign3)(r,t')
else if (tv = Ident.real_tyvar orelse tv = Ident.real_literal_tyvar)
andalso Types.real_typep t' then
(!assign3)(r,t')
else if tv = Ident.word_literal_tyvar
andalso Types.word_typep t' then
(!assign3)(r,t')
else fail OVERLOADED (tv, t')
and do_unify_rec(options,
r as ref (level, _,ty as RECTYPE amap, eq, imp), t') =
let
val t = METARECTYPE r
in
case t' of
RECTYPE amap' =>
if not_occurs (t, t') then
if eq_and_imp (options,eq,imp,t') then
let
fun unify'' (options,ty1, ty2) =
if (eq orelse imp) then
if eq_and_imp (options,eq, imp, ty2) then
(unify'(options,ty1, ty2); true)
else fail EQ_AND_IMP (eq, imp, ty2)
else (unify'(options,ty1, ty2); true)
in
NewMap.iterate
(fn (lab, ty) =>
let
val ty' = case NewMap.tryApply'(amap', lab) of
SOME ty' => ty'
| _ =>
let
val rec1 = NewMap.to_list_ordered amap
val rec2 = NewMap.to_list_ordered amap'
in
fail RECORD_DOMAIN (FLEX rec1, RIGID rec2)
end
in
if unify''(options,ty, ty') then
()
else
Crash.impossible "Unify.do_unify_rec"
end)
amap;
(!assign2)(r,(level, false,
propagate_level level t', eq, imp))
end
else fail EQ_AND_IMP (eq,imp,t')
else fail CIRCULARITY (t, t')
| METARECTYPE (r' as ref (level', true, RECTYPE amap', eq', imp')) =>
if (r = r')
then ()
else
if not_occurs (t, t') andalso not_occurs (t', t)
then
let
infix andnot
fun (true, _) andnot (false, _) = true
| (_, true) andnot (_, false) = true
| _ andnot _ = false
val leftfun =
if (eq', imp') andnot (eq, imp)
then
fn x =>
if eq_and_imp (options,eq', imp', x)
then x
else
fail EQ_AND_IMP (eq', imp', x)
else
fn x => x
val rightfun =
if (eq, imp) andnot (eq', imp')
then
fn x =>
if eq_and_imp (options,eq, imp, x)
then x
else
fail EQ_AND_IMP (eq, imp, x)
else
fn x => x
fun unify''(options,x, y) =
(unify'(options,leftfun x, rightfun y); true)
fun do_difference(in_map, out_map, res, in_fun) =
NewMap.fold
(fn (res, lab, ty) =>
case NewMap.tryApply'(out_map, lab) of
NONE =>
NewMap.define'(res, (lab, in_fun ty))
| _ => res)
(res, in_map)
val new_map =
do_difference(NewMap.empty' Ident.lab_lt, amap, amap', leftfun)
val new_map = do_difference(new_map, amap', amap, rightfun)
val new_map =
NewMap.fold
(fn (res, lab, ty) =>
case NewMap.tryApply'(amap', lab) of
SOME ty' =>
(ignore(unify''(options,ty, ty'));
NewMap.define'(res, (lab, ty)))
| _ => res)
(new_map, amap)
val level'' = if level < level' then level else level'
in
(!assign2)(r,(level'', true,
propagate_level level'' t',eq, imp));
(!assign2)(r',(level'', true,
propagate_level level'' (RECTYPE new_map),
eq', imp'))
end
else
fail CIRCULARITY (t, t')
| _ => raise NotRectype
end
| do_unify_rec _ = Crash.impossible "Unify.do_unify_rec(2)"
in
fun unified(options, x, y, return_substitution) =
let
val substitution = ref (nil,nil,nil)
fun add1 x = let val (s1,s2,s3) = !substitution
in substitution := (x::s1,s2,s3) end
fun add2 x = let val (s1,s2,s3) = !substitution
in substitution := (s1,x::s2,s3) end
fun add3 x = let val (s1,s2,s3) = !substitution
in substitution := (s1,s2,x::s3) end
in
(assign1 := (if return_substitution then add1 else op :=);
assign2 := (if return_substitution then add2 else op :=);
assign3 := (if return_substitution then add3 else op :=);
unify'(options, x, y);
if return_substitution then
SUBSTITUTION (SUBST (!substitution))
else OK)
handle Failed res =>
if return_substitution then
(print"\nWarning: Unification Failure; recipes may not be generated correctly\n";
SUBSTITUTION (SUBST (!substitution)))
else res
end
end
end;
