require "stamp";
require "../utils/lists";
require "../utils/print";
require "../basics/identprint";
require "../typechecker/scheme";
require "../typechecker/valenv";
require "../typechecker/strenv";
require "../typechecker/tyenv";
require "../typechecker/environment";
require "../typechecker/nameset";
require "../typechecker/types";
require "../typechecker/sigma";
require "../typechecker/basis";
functor Basis (
structure IdentPrint : IDENTPRINT
structure Valenv : VALENV
structure Strenv : STRENV
structure Tyenv : TYENV
structure Nameset : NAMESET
structure Scheme : SCHEME
structure Types : TYPES
structure Env : ENVIRONMENT
structure Sigma : SIGMA
structure Lists : LISTS
structure Print : PRINT
structure Stamp : STAMP
sharing Scheme.Datatypes = Env.Datatypes = Nameset.Datatypes =
Valenv.Datatypes = Tyenv.Datatypes = Types.Datatypes =
Strenv.Datatypes = Sigma.BasisTypes.Datatypes
sharing Valenv.Datatypes.Ident = IdentPrint.Ident
sharing Scheme.Set = Sigma.BasisTypes.Set
sharing type Nameset.Nameset = Sigma.BasisTypes.Nameset
sharing type Stamp.Map.T = Types.Datatypes.StampMap
) : BASIS =
struct
structure BasisTypes = Sigma.BasisTypes
structure Datatypes = Types.Datatypes
structure Set = Scheme.Set
type print_options = Scheme.print_options
type options = Scheme.Options.options
type error_info = Scheme.error_info
open Datatypes
exception LookupValId = Valenv.LookupValId
exception LookupTyCon = Tyenv.LookupTyCon
exception LookupStrId = Env.LookupStrId
exception LookupSigId = NewMap.Undefined
exception LookupFunId = NewMap.Undefined
exception LookupTyvar = NewMap.Undefined
fun tynameLevelsOK(tyname as TYNAME(_,_,_,_,_,_,_,_,lev)) lev'=
if lev<lev' then [] else [tyname]
| tynameLevelsOK(METATYNAME(ref tyfun,_,_,_,_,_)) lev =
tyfunLevelsOK tyfun lev
and tyfunLevelsOK(TYFUN(t,_)) l= typeLevelsOK t l
| tyfunLevelsOK(ETA_TYFUN tyname) l= tynameLevelsOK tyname l
| tyfunLevelsOK(NULL_TYFUN (stamp,ref tyfun)) l =
tyfunLevelsOK tyfun l
and typeLevelsOK(METATYVAR(ref (_,t,_),_,_)) l=
typeLevelsOK t l
| typeLevelsOK(META_OVERLOADED(ref t,_,_,_)) l =
typeLevelsOK t l
| typeLevelsOK(TYVAR(ref(_,t,_),_)) l =
typeLevelsOK t l
| typeLevelsOK(METARECTYPE(ref(_,_,t,_,_))) l =
typeLevelsOK t l
| typeLevelsOK(RECTYPE(amap)) l =
Datatypes.NewMap.fold (fn (acc,_,t)=> acc@(typeLevelsOK t l))
([],amap)
| typeLevelsOK(FUNTYPE(t,t')) l =
(typeLevelsOK t l) @ (typeLevelsOK t' l)
| typeLevelsOK(CONSTYPE(tylist,tyname)) l =
(Lists.reducel (fn (acc,t) => acc@(typeLevelsOK t l))
(tynameLevelsOK tyname l,tylist))
| typeLevelsOK(DEBRUIJN(_,_,_,t)) l =
(case t of NONE => []
| (SOME (ref (_,t',_))) => typeLevelsOK t' l)
| typeLevelsOK NULLTYPE _ = []
fun tyschemeLevelsOK (SCHEME(_,(t,_))) l = typeLevelsOK t l
| tyschemeLevelsOK (UNBOUND_SCHEME(t,_)) l = typeLevelsOK t l
| tyschemeLevelsOK (OVERLOADED_SCHEME _) _ = []
fun valenvLevelsOK (VE (_,vmap)) lev =
Lists.reducel
(fn (acc,tscheme) => (tyschemeLevelsOK tscheme lev)@acc)
([],Datatypes.NewMap.range vmap)
fun tyenvLevelsOK(TE amap) lev =
Lists.reducel (fn (acc,TYSTR(tyFun,_)) => (tyfunLevelsOK tyFun lev)@acc)
([],Datatypes.NewMap.range amap)
fun strEnvLevelsOK (SE smap) lev =
let
fun structureLevelsOK(STR(_,_,env)) l = envLevelsOK env l
| structureLevelsOK(COPYSTR((_,t),s)) l =
(Stamp.Map.range t) @ (structureLevelsOK s l)
in
Lists.reducel (fn (acc,s) => acc @ (structureLevelsOK s lev))
([],Datatypes.NewMap.range smap)
end
and envLevelsOK (ENV(strEnv ,tyEnv, valEnv)) l =
(strEnvLevelsOK strEnv l)@(valenvLevelsOK valEnv l)@
(tyenvLevelsOK tyEnv l)
val empty_tyvarenv = BasisTypes.TYVARENV (NewMap.empty (Ident.tyvar_lt,
Ident.tyvar_eq))
fun tyvarenv_lookup (tyvar, BasisTypes.TYVARENV amap) =
NewMap.apply'(amap, tyvar)
fun add_to_tyvarenv (alevel,tyvar,BasisTypes.TYVARENV amap) =
BasisTypes.TYVARENV
(NewMap.define
(amap,tyvar,Datatypes.TYVAR(ref (alevel,
Datatypes.NULLTYPE,Datatypes.NO_INSTANCE),tyvar)))
val empty_funenv = BasisTypes.FUNENV (NewMap.empty (Ident.funid_lt, Ident.funid_eq))
fun add_to_funenv (funid,phi,BasisTypes.FUNENV amap) =
BasisTypes.FUNENV (NewMap.define (amap,funid,phi))
fun funenv_lookup (funid,BasisTypes.FUNENV amap) =
NewMap.apply'(amap, funid)
fun funenv_plus_funenv (BasisTypes.FUNENV amap,BasisTypes.FUNENV amap') =
BasisTypes.FUNENV (NewMap.union(amap, amap'))
val empty_sigenv = BasisTypes.SIGENV (NewMap.empty (Ident.sigid_lt, Ident.sigid_eq))
fun add_to_sigenv (sigid,asig,BasisTypes.SIGENV amap) =
BasisTypes.SIGENV (NewMap.define (amap,sigid,asig))
fun sigenv_lookup (sigid,BasisTypes.SIGENV amap) =
NewMap.apply'(amap, sigid)
fun sigenv_plus_sigenv (BasisTypes.SIGENV amap,BasisTypes.SIGENV amap') =
BasisTypes.SIGENV (NewMap.union(amap, amap'))
fun lookup_tyvar (tyvar,BasisTypes.CONTEXT (_,_,_,tyvarenv)) =
tyvarenv_lookup (tyvar,tyvarenv)
fun lookup_longtycon (longtycon,
BasisTypes.CONTEXT (_,_,env,_)) =
Env.lookup_longtycon (longtycon,env)
fun lookup_val (longvalid,BasisTypes.CONTEXT (level,_,env,_),
location,generate_moduler) =
let
val scheme = Env.lookup_longvalid (longvalid,env)
in
Scheme.instantiate (level, scheme, location, generate_moduler)
end
fun lookup_tycon (tycon,BasisTypes.CONTEXT (_,_,ENV (_,te,_),_)) =
Tyenv.lookup (te, tycon)
val level_num_ref = ref 0
fun new_level_num () =
let val new = 1 + (!level_num_ref)
in level_num_ref := new; new
end
fun context_plus_ve (BasisTypes.CONTEXT (alevel,tyvars,
ENV (se,te,ve),tyvarenv),ve') =
BasisTypes.CONTEXT (new_level_num(),
tyvars,
ENV (se,te,Valenv.ve_plus_ve (ve,ve')),
tyvarenv)
fun context_plus_te (BasisTypes.CONTEXT (level,tyvarset,
ENV (se,te,ve),tyvarenv),te') =
BasisTypes.CONTEXT (level,
tyvarset,
ENV (se,Tyenv.te_plus_te (te,te'),ve),
tyvarenv)
fun context_plus_tyvarset (BasisTypes.CONTEXT(level,tyvars,
env,tyvarenv), tyvarset) =
let
fun collect (tve, tv) = add_to_tyvarenv (level, tv, tve)
in
BasisTypes.CONTEXT (level,Set.union (tyvars,tyvarset), env,
Set.fold collect (tyvarenv,tyvarset))
end
fun context_plus_tyvarlist (BasisTypes.CONTEXT(level,
tyvars,env,tyvarenv),
tyvarlist) =
let
fun collect ([],amap) = amap
| collect (h::t,amap) =
add_to_tyvarenv (level,h, collect(t,amap))
fun make_tyvarenv (tyvars) =
collect (tyvars, empty_tyvarenv)
in
BasisTypes.CONTEXT (level,tyvars,env,make_tyvarenv tyvarlist)
end
fun context_plus_env (BasisTypes.CONTEXT (alevel,tyvars,
env,tyvarenv),env') =
BasisTypes.CONTEXT (new_level_num(),
tyvars,
Env.env_plus_env (env,env'),tyvarenv)
fun context_for_datbind (BasisTypes.CONTEXT (level,tyvarset,
ENV(se,te,ve),_),
location,
dummy_tycons) =
let
val loc_string = SOME location
fun make_dummy_te ([],te) = te
| make_dummy_te ((tyvars,tycon)::t,te) =
make_dummy_te(t,
Tyenv.add_to_te
(te, tycon,
TYSTR
(Types.make_eta_tyfun
(Types.make_tyname
(Lists.length tyvars,
true,
IdentPrint.printTyCon tycon,
loc_string,level)),
empty_valenv)))
in
BasisTypes.CONTEXT (level,tyvarset,
ENV (se,make_dummy_te (dummy_tycons,te),ve),
empty_tyvarenv)
end
fun close
(error_info,options,location)
(alevel,VE (r,amap),exp_vars,tyvars_scoped_here,asig) =
let
fun close_ve (tree, valid, scheme) =
let
val scheme' =
Scheme.schemify
(error_info,options,location)
(alevel,Lists.member (valid,exp_vars),
scheme,tyvars_scoped_here,asig)
in
NewMap.define(tree, valid, scheme')
end
in
VE(ref 0,
NewMap.fold
close_ve
(NewMap.empty (Ident.valid_lt, Ident.valid_eq), amap))
end
fun env_of_context (BasisTypes.CONTEXT (_,_,env,_)) = env
fun te_of_context (BasisTypes.CONTEXT (_,_,ENV (_,te,_),_)) = te
fun env_to_context env = BasisTypes.CONTEXT (0,Set.empty_set,env,
empty_tyvarenv)
fun context_level (BasisTypes.CONTEXT (alevel,_,_,_)) = alevel
fun get_tyvarset (BasisTypes.CONTEXT (_,tyvars,_,_)) = tyvars
fun basis_to_context (BasisTypes.BASIS (level,_,_,_,env)) =
BasisTypes.CONTEXT (level,
Set.empty_set,env,empty_tyvarenv)
fun lookup_sigid (sigid,BasisTypes.BASIS (_,_,_,sigenv,_)) =
sigenv_lookup (sigid,sigenv)
fun lookup_longstrid (longstrid,BasisTypes.BASIS (_,_,_,_,env)) =
Env.lookup_longstrid (longstrid,env)
fun lookup_funid (funid,BasisTypes.BASIS (_,_,funenv,_,env)) =
funenv_lookup (funid,funenv)
fun env_in_basis env = BasisTypes.BASIS (0,
Nameset.empty_nameset(),
empty_funenv,
empty_sigenv,
env)
fun sigenv_in_basis sigenv = BasisTypes.BASIS (0,
Nameset.empty_nameset (),
empty_funenv,sigenv,
Env.empty_env)
fun funenv_in_basis funenv = BasisTypes.BASIS (0,
Nameset.empty_nameset (),
funenv,
empty_sigenv,
Env.empty_env)
fun basis_plus_env (BasisTypes.BASIS (level,names,funenv,sigenv,env),
env') =
let
in
BasisTypes.BASIS (new_level_num(),
names,
funenv,sigenv,
Env.env_plus_env (env,env'))
end
fun basis_plus_sigenv (BasisTypes.BASIS (level,names,funenv,sigenv,env),
sigenv') =
BasisTypes.BASIS (level,names,funenv,
sigenv_plus_sigenv (sigenv,sigenv'),env)
fun basis_plus_funenv (BasisTypes.BASIS (level,names,funenv,sigenv,env),
funenv') =
BasisTypes.BASIS (level,names,funenv_plus_funenv (funenv,funenv'),
sigenv,env)
fun basis_plus_names (BasisTypes.BASIS (level,names,funenv,sigenv,env),
names') =
BasisTypes.BASIS (level,Nameset.union (names,names'),funenv,sigenv,env)
fun basis_circle_plus_basis
(BasisTypes.BASIS (level,names,funenv,sigenv,env),
BasisTypes.BASIS (level',names',funenv',sigenv',env')) =
BasisTypes.BASIS (new_level_num(),
Nameset.union (names,names'),
funenv_plus_funenv (funenv,funenv'),
sigenv_plus_sigenv (sigenv,sigenv'),
Env.env_plus_env (env,env'))
fun basis_level (BasisTypes.BASIS{1=level,...}) = level
val initial_basis =
BasisTypes.BASIS (0,
Nameset.initial_nameset,
empty_funenv,
empty_sigenv,
Env.initial_env)
val initial_basis_for_builtin_library =
BasisTypes.BASIS (0,
Nameset.initial_nameset_for_builtin_library,
empty_funenv,
empty_sigenv,
Env.initial_env_for_builtin_library)
val empty_basis =
BasisTypes.BASIS (0,
Nameset.empty_nameset (),
empty_funenv,
empty_sigenv,
Env.empty_env)
fun remove_str(BasisTypes.BASIS(level,n, f, s, ENV(SE map, te, ve)),
strid) =
BasisTypes.BASIS(level,n, f, s, ENV(SE(NewMap.undefine(map, strid)),
te, ve))
fun add_str (BasisTypes.BASIS (level,nameset, functor_env, signature_env,
Datatypes.ENV (structure_env, type_env,
value_env)),
strid, str) =
let
val new_structure_env = Strenv.add_to_se (strid, str, structure_env)
in
BasisTypes.BASIS (level,nameset, functor_env, signature_env,
Datatypes.ENV (new_structure_env, type_env,
value_env))
end
fun add_val (BasisTypes.BASIS (level, nameset, functor_env, signature_env,
Datatypes.ENV (structure_env, type_env,
value_env)),
valid, scheme) =
let
val new_value_env = Valenv.add_to_ve (valid, scheme, value_env)
in
BasisTypes.BASIS (level, nameset, functor_env, signature_env,
Datatypes.ENV (structure_env, type_env,
new_value_env))
end
local
fun follow_tyname(arg as TYNAME _) = arg
| follow_tyname(arg as METATYNAME{1=ref(NULL_TYFUN _), ...}) = arg
| follow_tyname(METATYNAME{1=ref(ETA_TYFUN tyname), ...}) =
follow_tyname tyname
| follow_tyname(METATYNAME{1=ref(TYFUN(CONSTYPE([], tyname), 0)),
...}) = follow_tyname tyname
| follow_tyname tyname = tyname
fun follow_tyfun(arg as NULL_TYFUN _) = arg
| follow_tyfun(arg as ETA_TYFUN(TYNAME _)) = arg
| follow_tyfun(ETA_TYFUN(METATYNAME{1=ref tyfun, ...})) =
follow_tyfun tyfun
| follow_tyfun(tyfun as
TYFUN(CONSTYPE(l, tyname), 0)) =
if Types.tyname_arity tyname = 0 andalso
Types.check_debruijns(l, 0) then
follow_tyfun(ETA_TYFUN tyname)
else
tyfun
| follow_tyfun tyfun = tyfun
fun long_follow_tyfun(arg as NULL_TYFUN _) = arg
| long_follow_tyfun(arg as ETA_TYFUN(TYNAME _)) = arg
| long_follow_tyfun(arg as ETA_TYFUN(METATYNAME{1=ref(NULL_TYFUN _),
...})) =
arg
| long_follow_tyfun(ETA_TYFUN(METATYNAME{1=ref tyfun, ...})) =
long_follow_tyfun tyfun
| long_follow_tyfun(tyfun as
TYFUN(CONSTYPE(l, tyname), 0)) =
if Types.tyname_arity tyname = 0 andalso
Types.check_debruijns(l, 0) then
long_follow_tyfun(ETA_TYFUN tyname)
else
tyfun
| long_follow_tyfun tyfun = tyfun
fun follow_strname(arg as STRNAME _) = arg
| follow_strname(arg as NULLNAME _) = arg
| follow_strname(arg as
METASTRNAME(ref(NULLNAME _))) =
arg
| follow_strname(arg as METASTRNAME(ref strname)) =
follow_strname strname
fun reduce_strname(arg as STRNAME _) = ()
| reduce_strname(arg as NULLNAME _) = ()
| reduce_strname(METASTRNAME(r as ref strname)) =
r := follow_strname strname
fun reduce_tyfun(arg as NULL_TYFUN _) = ()
| reduce_tyfun(arg as ETA_TYFUN(METATYNAME{1=ref(NULL_TYFUN _),
...})) =
()
| reduce_tyfun(ETA_TYFUN(METATYNAME{1=r as ref tyfun,
5=ref_ve, ...})) =
let
val tyfun = long_follow_tyfun tyfun
in
(case tyfun of
ETA_TYFUN _ => ref_ve := empty_valenv
| _ => ());
r := tyfun
end
| reduce_tyfun(ETA_TYFUN _) = ()
| reduce_tyfun(TYFUN(ty, _)) = reduce_type ty
and reduce_tyname(arg as TYNAME _) = ()
| reduce_tyname(arg as METATYNAME{1=ref(NULL_TYFUN _), ...}) =
()
| reduce_tyname(arg as METATYNAME{1=r as ref tyfun,
5=ref_ve, ...}) =
let
val tyfun = long_follow_tyfun tyfun
in
(case tyfun of
ETA_TYFUN _ => ref_ve := empty_valenv
| _ => ());
r := tyfun
end
and reduce_scheme(SCHEME(_, (ty,_))) = reduce_type ty
| reduce_scheme(UNBOUND_SCHEME (ty,_)) = reduce_type ty
| reduce_scheme _ = ()
and reduce_type(METATYVAR(ref(_, ty,_), _, _)) = reduce_type ty
| reduce_type(META_OVERLOADED {1=ref ty,...}) =
reduce_type ty
| reduce_type(TYVAR _) = ()
| reduce_type(METARECTYPE(ref{3=ty, ...})) = reduce_type ty
| reduce_type(ty as RECTYPE _) =
Lists.iterate reduce_type (Types.rectype_range ty)
| reduce_type(FUNTYPE(ty1, ty2)) =
(reduce_type ty1;
reduce_type ty2)
| reduce_type(CONSTYPE(l, tyname)) =
(Lists.iterate reduce_type l;
reduce_tyname tyname)
| reduce_type _ = ()
and reduce_valenv(VE(_, ve_map)) =
NewMap.iterate (fn (_, type_scheme) => reduce_scheme type_scheme)
ve_map
fun reduce_str(STR(strname,_,env)) =
(reduce_strname strname; reduce_env env)
| reduce_str (COPYSTR((smap,tmap),str)) =
reduce_str (Env.str_copy(str,smap,tmap))
and reduce_env(ENV(SE strenv,
TE tyenv,
ve)) =
(NewMap.iterate (fn (_, str) => reduce_str str) strenv;
NewMap.iterate (fn (_, TYSTR(tyfun, ve)) =>
(reduce_valenv ve;
reduce_tyfun tyfun)) tyenv;
reduce_valenv ve)
fun reduce_nameset nameset =
(Lists.iterate reduce_strname (Nameset.strnames_of_nameset nameset);
Lists.iterate reduce_tyname (Nameset.tynames_of_nameset nameset))
fun reduce_sigma(BasisTypes.SIGMA(nameset, str)) =
(reduce_nameset nameset; reduce_str str)
in
fun reduce_chains (BasisTypes.BASIS(_,
nameset,
BasisTypes.FUNENV funid_map,
BasisTypes.SIGENV sigid_map, env)) =
let
val _ = reduce_nameset nameset
val _ = NewMap.iterate
(fn (_, BasisTypes.PHI(nameset, (str, sigma))) =>
let
val _ = reduce_nameset nameset
val _ = reduce_str str
in
reduce_sigma sigma
end)
funid_map
val _ = NewMap.iterate (fn (_, sigma) => reduce_sigma sigma)
sigid_map
in
reduce_env env
end
end
val pervasive_stamp_count = Types.pervasive_stamp_count
fun tynamesNotIn (t,BasisTypes.CONTEXT(level,_,_,_)) =
typeLevelsOK t level
fun valEnvTynamesNotIn (valenv, BasisTypes.CONTEXT(level,_,_,_)) =
valenvLevelsOK valenv level
end
;
