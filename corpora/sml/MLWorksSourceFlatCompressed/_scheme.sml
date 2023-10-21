require "../basis/__int";
require "^.basis.__list";
require "../utils/set";
require "../utils/lists";
require "../utils/print";
require "../utils/crash";
require "../main/info";
require "../basics/identprint";
require "../typechecker/scheme";
require "../typechecker/types";
require "../typechecker/completion";
functor Scheme(
structure Set : SET
structure Lists : LISTS
structure Info : INFO
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Print : PRINT
structure Crash : CRASH
structure Completion : COMPLETION
sharing Info.Location = IdentPrint.Ident.Location
sharing Types.Options = IdentPrint.Options = Completion.Options
sharing Types.Datatypes = Completion.Datatypes
sharing IdentPrint.Ident = Types.Datatypes.Ident
) : TYPESCHEME =
struct
structure Datatypes = Types.Datatypes
structure Options = Types.Options
structure Set = Set
structure Location = Info.Location
type print_options = Options.print_options
type error_info = Info.options
open Datatypes
exception EnrichError of string
fun err_valid (Options.OPTIONS{print_options,...}, vname) =
Err_String (IdentPrint.printValId print_options vname)
local
fun stack_depth [] = 0
| stack_depth ((_,DEBRUIJN(n,_,_,_))::_) = n+1
| stack_depth _ = Crash.impossible "Scheme.stack_depth"
fun generalise (error_info,options, location) (subfun,ty) =
let
fun generalise (t as (TYVAR(_))) = subfun t
| generalise (t as (METATYVAR (ref (_,NULLTYPE,_),_,_))) = subfun t
| generalise (METATYVAR (ref (_,ty,_),_,_)) =
generalise ty
| generalise (ty as META_OVERLOADED {1=ref NULLTYPE,...}) = ty
| generalise (META_OVERLOADED {1=ref ty,...}) = generalise ty
| generalise (METARECTYPE (ref (_,true,ty as METARECTYPE _,_,_))) =
generalise ty
| generalise (ty as METARECTYPE (ref (_,true,subty,_,_))) =
(Info.error error_info
(Info.RECOVERABLE,
location,
"Unresolved flexible record of type " ^
Types.print_type options ty);
generalise subty)
| generalise (METARECTYPE (ref (_,_,ty,_,_))) =
generalise ty
| generalise ((RECTYPE amap)) =
RECTYPE (NewMap.map generalise_map amap)
| generalise (FUNTYPE(arg,res)) =
FUNTYPE (generalise arg,generalise res)
| generalise (CONSTYPE (tylist,tyname)) =
CONSTYPE (map generalise tylist,tyname)
| generalise (ty as DEBRUIJN _) = Crash.impossible ("impossible type error 15: debruijn ")
| generalise (ty as NULLTYPE) = ty
and generalise_map(_, ty) = generalise ty
in
generalise ty
end
fun make_new_instance instance tyvar =
case instance of
NONE => ()
| SOME (instance,_) =>
let
val tyvar_info =
case tyvar of
(METATYVAR (info,_,_)) => info
| (TYVAR (info,_)) => info
| _ => Crash.impossible "tyvar_info:make_new_instance:scheme"
in
case !instance of
NO_INSTANCE => instance := INSTANCE([tyvar_info])
| INSTANCE tyvars =>
if Lists.member(tyvar_info,tyvars) then ()
else
instance := INSTANCE(tyvar_info::tyvars)
| _ => Crash.impossible "make_new_instance:scheme"
end
fun subst new_instance (subst_list,tyvar) =
let
fun subst ([],ty) = ty
| subst (((atyvar'' as TYVAR (atyvar as _), adebruijn)::t),
ty as TYVAR atyvar') =
if atyvar = atyvar'
then (ignore(new_instance atyvar'');
adebruijn)
else
subst (t,ty)
| subst (((tyvar as METATYVAR (x',_,_), adebruijn)::t),
ty as METATYVAR (x,_,_)) =
if x = x'
then (ignore(new_instance tyvar);
adebruijn)
else
subst (t,ty)
| subst (((TYVAR _, _)::t),ty as METATYVAR _) =
subst (t,ty)
| subst (((METATYVAR _, _)::t),ty as TYVAR _) =
subst (t,ty)
| subst ((x,y)::t,ty) = Crash.impossible "Scheme.subst"
in
subst (subst_list,tyvar)
end
fun make_subst_list options ([],n,substacc) = (n,substacc)
| make_subst_list options (h::t,n,substacc) =
let val new_debruijn =
DEBRUIJN (n,Types.tyvar_equalityp h,
Types.imperativep h,
case h of
METATYVAR(tyv,_,_) => SOME (tyv)
| TYVAR(tyv,_) => SOME (tyv)
| _ => Crash.impossible
"new_debruijn:make_subst_list:scheme")
in
make_subst_list options (t,n+1,(h,new_debruijn) :: substacc)
end
in
fun make_scheme' (error_info,options,location) ([],tyexp) =
UNBOUND_SCHEME (tyexp)
| make_scheme' (error_info,options,location)
(tylist,tyexp as (ty,instance)) =
let
val (no_of_bound_vars,substlist) =
make_subst_list options (tylist,0,[])
val new_instance = make_new_instance instance
in
SCHEME (no_of_bound_vars,
(generalise (error_info,options,location)
(fn tyvar => subst new_instance
(substlist,tyvar),
ty),
instance))
end
val default_values = (Info.make_default_options (),
Options.default_options,
Location.UNKNOWN)
val make_scheme = make_scheme' default_values
fun schemify (error_info,options,location)
(alevel,exp_varp,
UNBOUND_SCHEME (atype as (atype',instance'')),
tyvars_scoped,asig) =
let
val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...},...}
= options
fun levels_ok (avar as TYVAR (ref (var_level,_,_),id)) =
var_level >= alevel
| levels_ok (avar as METATYVAR (ref (var_level,_,_),_,_)) =
var_level >= alevel
| levels_ok _ = Crash.impossible "Scheme.levels_ok"
fun tyvarp (TYVAR _) = true
| tyvarp _ = false
fun extract_tyvar (TYVAR (_,atyvar)) = atyvar
| extract_tyvar _ = Crash.impossible "Scheme.extract_tyvar"
fun substp (avar,valbind) =
if valbind
then
(not (exp_varp andalso
((not old_definition) orelse Types.imperativep avar)))
andalso
(levels_ok avar)
andalso
(if tyvarp avar then
(Set.is_member (extract_tyvar (avar),tyvars_scoped))
else
true)
else
true
val new_instance = make_new_instance instance''
val sub_stack = ref []
fun subfun (avar) =
if substp (avar,asig)
then
let
val stack = !sub_stack
val asub = subst (fn _ => ()) (!sub_stack,avar)
fun debruijnp (DEBRUIJN _) = true
| debruijnp _ = false
in
if debruijnp asub
then asub
else
let
val new_debruijn =
DEBRUIJN (stack_depth stack,
Types.tyvar_equalityp avar,
Types.imperativep avar,
case avar of
METATYVAR(tyv,_,_) => SOME (tyv)
| TYVAR(tyv,_) => SOME (tyv)
| _ => Crash.impossible "new_debruijn:subfun:scheme")
in
(new_instance avar;
sub_stack := (avar,new_debruijn)::stack;
new_debruijn)
end
end
else avar
val scheme_type = (generalise (error_info,options,location)
(subfun,atype'),instance'')
in
SCHEME (stack_depth (!sub_stack),scheme_type)
end
| schemify _ _ = Crash.impossible "Scheme.schemify"
val schemify' = schemify default_values
end
fun check_closure (use_value_polymorphism,atype,alevel,tyvars_scoped) =
let
fun levels_ok (avar as TYVAR (ref (var_level,_,_),id)) =
var_level >= alevel
| levels_ok (avar as METATYVAR (ref (var_level,_,_),_,_)) =
var_level >= alevel
| levels_ok _ = Crash.impossible "Scheme.levels_ok"
fun tyvarp (TYVAR _) = true
| tyvarp _ = false
fun extract_tyvar (TYVAR (_,atyvar)) = atyvar
| extract_tyvar _ = Crash.impossible "Scheme.extract_tyvar"
fun check_var avar =
not (use_value_polymorphism orelse Types.imperativep avar)
andalso
(levels_ok avar)
andalso
(if tyvarp avar then
(Set.is_member (extract_tyvar (avar),tyvars_scoped))
else
true)
exception CheckClosure of string
fun check (t as (TYVAR _)) = check_var t
| check (t as (METATYVAR (ref (_,NULLTYPE,_),_,_))) = check_var t
| check (METATYVAR (ref (_,ty,_),_,_)) = check ty
| check (META_OVERLOADED {1=ref NULLTYPE,...}) =
raise CheckClosure ("Unresolved overloaded function")
| check (META_OVERLOADED {1=ref ty,...}) = check ty
| check (METARECTYPE (ref (_,true,ty as METARECTYPE _,_,_))) = check ty
| check (ty as METARECTYPE (ref (_,true,_,_,_))) =
raise CheckClosure ("Unresolved flexible record of type ")
| check (METARECTYPE (ref (_,_,ty,_,_))) = check ty
| check (RECTYPE amap) = NewMap.forall check_forall amap
| check (FUNTYPE(arg,res)) = check arg andalso check res
| check (CONSTYPE (tylist,tyname)) = List.all check tylist
| check (ty as DEBRUIJN _) = raise CheckClosure ("debruijn")
| check (ty as NULLTYPE) = true
and check_forall(_, ty) = check ty
in
check atype
end
fun unary_overloaded_scheme x =
OVERLOADED_SCHEME (UNARY x)
fun binary_overloaded_scheme x =
OVERLOADED_SCHEME (BINARY x)
fun predicate_overloaded_scheme x =
OVERLOADED_SCHEME (PREDICATE x)
fun instantiate (_,UNBOUND_SCHEME (tyexp,instance),_,_) =
(tyexp,
case instance of
NONE => (ZERO,NONE)
| SOME (instance,instance') =>
case instance' of
ref(instance' as SOME (ref (SIGNATURE_INSTANCE instance_info))) =>
(instance_info,instance')
| _ => (ZERO,SOME(instance)))
| instantiate (_,OVERLOADED_SCHEME (UNARY (valid, tyvar)),loc,_) =
let
val olvar = META_OVERLOADED (ref NULLTYPE, tyvar, valid, loc)
in
(FUNTYPE (olvar,olvar),(ZERO,NONE))
end
| instantiate (_,OVERLOADED_SCHEME (BINARY (valid, tyvar)),loc,_) =
let
val olvar = META_OVERLOADED (ref NULLTYPE, tyvar, valid, loc)
in
(FUNTYPE (Types.add_to_rectype
(Ident.LAB (Ident.Symbol.find_symbol ("1")),olvar,
Types.add_to_rectype
(Ident.LAB (Ident.Symbol.find_symbol ("2")),olvar,
Types.empty_rectype)),olvar),
(ZERO,NONE))
end
| instantiate (_,OVERLOADED_SCHEME (PREDICATE (valid, tyvar)),loc,_) =
let
val olvar = META_OVERLOADED (ref NULLTYPE, tyvar, valid, loc)
in
(FUNTYPE (Types.add_to_rectype
(Ident.LAB (Ident.Symbol.find_symbol("1")),olvar,
Types.add_to_rectype
(Ident.LAB (Ident.Symbol.find_symbol("2")),olvar,
Types.empty_rectype)),Types.bool_type),
(ZERO,NONE))
end
| instantiate (alevel,SCHEME (n,(tyexp,instance')),_,generate_moduler) =
let
val (instance',instance'') =
case instance' of
SOME (instance',ref instance'') =>
(SOME instance',instance'')
| _ => (NONE,NONE)
val sub_vec = MLWorks.Internal.Array.array(n, NULLTYPE)
fun instance_map(_, ty) = instance ty
and instance (t as (TYVAR _)) = t
| instance ((RECTYPE amap)) =
RECTYPE (NewMap.map instance_map amap)
| instance (FUNTYPE(arg,res)) =
FUNTYPE (instance (arg),instance (res))
| instance (CONSTYPE (tylist,tyname)) =
CONSTYPE (map instance tylist,tyname)
| instance (DEBRUIJN (n,eq,imp,tyvar)) =
let
val asub = MLWorks.Internal.Array.sub (sub_vec,n)
in
case asub of
NULLTYPE =>
let
val new_meta' = ref (alevel,NULLTYPE,NO_INSTANCE)
val new_meta = METATYVAR(new_meta',eq,imp)
val _ =
case instance' of
NONE => ()
| SOME instance =>
(case !instance of
NO_INSTANCE => ()
| INSTANCE(_) =>
(case tyvar of
SOME (tyvar as ref(n,ty,instances)) =>
tyvar :=
(n,ty,INSTANCE(new_meta'::
(case instances of
NO_INSTANCE => nil
| INSTANCE(instances) => instances
| _ => Crash.impossible
"1:instance:instantiate:scheme")))
| _ => Crash.impossible "2:instance:instantiate:scheme")
| _ => Crash.impossible "3:instance:instantiate:scheme")
in
(MLWorks.Internal.Array.update (sub_vec,n,new_meta);
new_meta)
end
| _ => asub
end
| instance (NULLTYPE) =
Crash.impossible "impossible type error 16: nulltype in scheme"
| instance(t as (METATYVAR (ref (_,ty,_),_,_))) =
(case ty of
NULLTYPE => t
| _ => instance ty)
| instance (t as (META_OVERLOADED {1=ref ty,...})) =
(case ty of
NULLTYPE => t
| _ => instance ty)
| instance (t as METARECTYPE (ref (_,b,ty,_,_))) =
if b then
(case ty of
METARECTYPE _ => instance ty
| _ => t)
else
ty
val ty = instance (tyexp)
fun instance_length (NO_INSTANCE) = ZERO
| instance_length (INSTANCE instances) = ONE (length(instances))
| instance_length _ = Crash.impossible "length:instantiate:scheme"
fun combine_instances (ZERO,inst) = inst
| combine_instances (inst,ZERO) = inst
| combine_instances (ONE n,ONE m) = TWO (n,m)
| combine_instances _ = Crash.impossible "combine_instances"
val length =
case instance' of
NONE => ZERO
| SOME instance =>
(case !instance of
NO_INSTANCE =>
(case instance'' of
SOME (ref (SIGNATURE_INSTANCE instance_info)) =>
if generate_moduler
then ZERO
else instance_info
| _ => ZERO)
| INSTANCE((tyvar as ref(_,_,instances))::_) =>
(case instance'' of
SOME (ref (SIGNATURE_INSTANCE instance_info)) =>
if generate_moduler
then instance_length instances
else combine_instances (instance_info,instance_length instances)
| _ => instance_length instances)
| _ => Crash.impossible "instance:instantiate:scheme")
in
(ty,(length,case instance'' of
SOME (ref (SIGNATURE_INSTANCE _)) => instance''
| _ => instance'))
end
fun skolemize (UNBOUND_SCHEME (atype,_)) = atype
| skolemize (SCHEME (n,(atype,_))) = atype
| skolemize (OVERLOADED_SCHEME _) = Crash.impossible "Scheme.skolemize"
fun equalityp (SCHEME (_,(FUNTYPE (atype,atype'),_))) =
Types.type_equalityp (atype)
| equalityp (UNBOUND_SCHEME ((FUNTYPE (atype,atype'),_))) =
Types.type_equalityp (atype)
| equalityp (_) = true
fun string_scheme (OVERLOADED_SCHEME (UNARY (_, tyvar))) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat ["ALL ", olvar, ".", olvar, " -> ", olvar]
end
| string_scheme (OVERLOADED_SCHEME (BINARY (_, tyvar))) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat ["ALL ", olvar, ".", olvar, " * ", olvar, " -> ", olvar]
end
| string_scheme (OVERLOADED_SCHEME (PREDICATE (_, tyvar))) =
let val olvar = IdentPrint.printTyVar tyvar
in
concat ["ALL ", olvar, ".", olvar, " * ", olvar, " -> bool"]
end
| string_scheme (UNBOUND_SCHEME (aty,_)) =
"ALL{}." ^ (Types.extra_debug_print_type aty)
| string_scheme (SCHEME (n,(aty,_))) =
"ALL{"^ (Int.toString n)^"}." ^ (Types.extra_debug_print_type aty)
exception Error of Type * Type * string
exception Mismatch
fun internal_generalises_map isSML90 (ty1,ty2) =
let
val binding_list = ref []
fun check_debruijn (stuff as (n,eq,imp,tyvar),ty) =
(let val ty' = Lists.assoc (n,!binding_list)
in
if Types.type_eq (ty,ty',true,true)
then ()
else raise Error (ty,ty',"types clash")
end
handle
Lists.Assoc =>
(
if eq andalso not(Types.closed_type_equalityp ty)
then raise Error (DEBRUIJN stuff,ty,
"equality attribute missing")
else ();
if imp andalso isSML90
andalso not (Types.imperativep ty)
then raise Error (DEBRUIJN stuff,ty,
"imperative attribute missing")
else ();
case tyvar of
SOME (tyvar) =>
(case tyvar of
ref(n,t,instance) =>
tyvar := (n,t,
(case ty of
DEBRUIJN(_,_,_,SOME(tyv)) =>
if tyv = tyvar then
instance
else
INSTANCE (tyv::
(case instance of
NO_INSTANCE => nil
| INSTANCE(instances) => instances
| _ => Crash.impossible "2:check_debruijn:scheme"))
| DEBRUIJN _ => instance
| _ => INSTANCE(ref(0,ty,NO_INSTANCE)::
(case instance of
NO_INSTANCE => nil
| INSTANCE(instances) => instances
| _ => Crash.impossible "2:check_debruijn:scheme")))))
| _ => ();
binding_list := (n,ty)::(!binding_list)))
fun type_strip(ty as METATYVAR(ref(_, ty',_), _, _)) =
(case ty' of
NULLTYPE => ty
| _ => type_strip ty')
| type_strip(ty as META_OVERLOADED {1=ref ty',...}) =
(case ty' of
NULLTYPE => ty
| _ => type_strip ty')
| type_strip(METARECTYPE(ref{3 = ty, ...})) = type_strip ty
| type_strip(ty as CONSTYPE(l, METATYNAME{1 = ref tyfun, ...})) =
(case tyfun of
NULL_TYFUN _ => ty
| _ => type_strip(Types.apply(tyfun, l)))
| type_strip ty = ty
fun is_polymorphic (DEBRUIJN _) = true
| is_polymorphic ty =
not (Types.all_tyvars ty = [])
fun check_type (ty,ty') = check_type' (type_strip ty,type_strip ty')
and check_type' (DEBRUIJN stuff,ty) =
check_debruijn (stuff,ty)
| check_type' (ty1 as RECTYPE map,ty2 as RECTYPE map') =
if NewMap.eq (fn (ty1,ty2) => (check_type (ty1,ty2); true)) (map, map') then
()
else
raise Error (ty1,ty2,"record types have different domains")
| check_type' (FUNTYPE (ran,dom),FUNTYPE(ran',dom')) =
(check_type (ran,ran');
check_type (dom,dom'))
| check_type' (ty1 as CONSTYPE (tys,tyname), ty2 as CONSTYPE (tys',tyname')) =
if not (Types.tyname_eq (tyname,tyname'))
then raise Error (ty1,ty2,"types clash")
else
Lists.iterate check_type (Lists.zip (tys,tys'))
| check_type' (ty1 as METATYVAR(r as ref (_,NULLTYPE,_), eq, imp), ty2) =
if isSML90 then raise Error(ty1,ty2,"")
else if is_polymorphic ty2 then raise Error(ty1,ty2,"trying to instantiate to a polytype")
else if eq andalso not(Types.closed_type_equalityp ty2)
then raise Error(ty1,ty2,"missing equality attribute")
else
r := (0,ty2,NO_INSTANCE)
| check_type' (ty,ty') =
if Types.type_eq (ty,ty',true,true)
then
()
else raise Error(ty,ty',"")
in
(check_type (ty1,ty2);
!binding_list)
end
fun generalises isSML90 (ty1,ty2) =
(ignore(internal_generalises_map isSML90 (ty1,ty2)); true)
handle Error _ => raise Mismatch
fun generalises_map isSML90 (ty1,ty2) =
internal_generalises_map isSML90 (ty1,ty2)
handle Error _ => raise Mismatch
fun SML90_dynamic_generalises
(ty1 : MLWorks.Internal.Dynamic.type_rep,
ty2 : MLWorks.Internal.Dynamic.type_rep) =
let
val cast : 'a -> 'b = MLWorks.Internal.Value.cast
in
generalises true (cast ty1,cast ty2)
handle Mismatch =>
raise MLWorks.Internal.Dynamic.Coerce(ty1, ty2)
end
fun SML96_dynamic_generalises
(ty1 : MLWorks.Internal.Dynamic.type_rep,
ty2 : MLWorks.Internal.Dynamic.type_rep) =
let
val cast : 'a -> 'b = MLWorks.Internal.Value.cast
in
generalises false (cast ty1,cast ty2)
handle Mismatch =>
raise MLWorks.Internal.Dynamic.Coerce(ty1, ty2)
end
val _ = MLWorks.Internal.Dynamic.generalises_ref :=
let val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...},...}
= Options.default_options
in
if old_definition then SML90_dynamic_generalises
else SML96_dynamic_generalises
end
fun apply_instantiation (ty,binding_list) =
let
fun instance_map(_, ty) = instance ty
and instance (t as (TYVAR _)) = t
| instance ((RECTYPE amap)) =
RECTYPE (NewMap.map instance_map amap)
| instance (FUNTYPE(arg,res)) =
FUNTYPE (instance arg,instance res)
| instance (CONSTYPE (tylist,tyname)) =
CONSTYPE (map instance tylist,tyname)
| instance (t as DEBRUIJN (n,_,_,_)) =
((Lists.assoc (n,binding_list))
handle Lists.Assoc => t)
| instance (t as NULLTYPE) = t
| instance(t as (METATYVAR (ref (_,ty,_),_,_))) =
(case ty of
NULLTYPE => t
| _ => instance ty)
| instance (t as (META_OVERLOADED {1=ref ty,...})) =
(case ty of
NULLTYPE => t
| _ => instance ty)
| instance (t as METARECTYPE (ref (_,b,ty,_,_))) =
if b then
(case ty of
METARECTYPE _ => instance ty
| _ => t)
else
ty
in
instance ty
end
fun is_meta(Datatypes.METATYVAR _) = true
| is_meta _ = false
fun is_deb(Datatypes.DEBRUIJN _) = true
| is_deb _ = false
fun is_tyvar (Datatypes.TYVAR _) = true
| is_tyvar _ = false
fun scheme_generalises options (name,completion_env,_,scheme,scheme') =
let
val atype = skolemize scheme
val atype' = skolemize scheme'
val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...},...}
= options
in
(ignore(internal_generalises_map old_definition (atype',atype)); true)
handle Error(ty',ty,reason) =>
let
val same_types = Types.type_eq(atype, ty, true, true)
fun dbx ty = [Err_Type ty, Err_String ": ",
if is_meta ty then Err_String "Meta " else
Err_String "",
if is_deb ty then Err_String "Deb " else
Err_String "",
if is_tyvar ty then Err_String "Tyvar " else
Err_String ""]
fun expand_reason "" = "type clash"
| expand_reason r = r
in
raise EnrichError
(Completion.report_type_error
(options, completion_env,
[Err_String "Type mismatch in signature and structure:",
Err_String "\n    id : ", err_valid (options, name),
Err_String "\n    spec :   ", Err_Scheme atype,
Err_String "\n    actual : ", Err_Scheme atype']@
(if same_types then
[Err_String ("\n      (" ^ expand_reason reason ^ ")")]
else
Err_String "\n      because" ::
Err_String "\n         " ::
(if (is_meta ty') orelse (is_tyvar ty') then
(Err_Scheme ty' ::
Err_String "\n      is a" ::
(if is_tyvar ty' then Err_String "n explicit "
else Err_String " ") ::
(if Types.imperativep ty'
then Err_String "free imperative type variable"
else Err_String "free type variable") ::
(if is_deb ty then
[Err_String "\n      which cannot be instantiated to the bound variable",
Err_String "\n         ", Err_Type ty]
else
[Err_String "\n      which cannot be instantiated to",
Err_String "\n         ", Err_Type ty])) @
(if reason = "" then [] else [Err_String ("\n     (" ^ reason ^ ")")])
else [Err_Type ty',
Err_String "\n      is not an instance of",
Err_String "\n         ", Err_Type ty,
Err_String ("\n      (" ^ expand_reason reason ^ ")")]))))
end
end
fun typescheme_eq (OVERLOADED_SCHEME (UNARY (_,tv)),
OVERLOADED_SCHEME (UNARY (_,tv'))) = tv = tv'
| typescheme_eq (OVERLOADED_SCHEME (UNARY _),_) = false
| typescheme_eq (OVERLOADED_SCHEME (BINARY (_,tv)),
OVERLOADED_SCHEME (BINARY (_,tv'))) = tv = tv'
| typescheme_eq (OVERLOADED_SCHEME (BINARY _),_) = false
| typescheme_eq (OVERLOADED_SCHEME (PREDICATE (_,tv)),
OVERLOADED_SCHEME (PREDICATE (_,tv'))) = tv = tv'
| typescheme_eq (OVERLOADED_SCHEME (PREDICATE _),_) = false
| typescheme_eq (UNBOUND_SCHEME (atype,_),UNBOUND_SCHEME (atype',_)) =
Types.type_eq (atype,atype',true,false)
| typescheme_eq (SCHEME(n,(atype,_)),SCHEME(n',(atype',_))) =
n = n andalso Types.type_eq (atype,atype',true,false)
| typescheme_eq _ = Crash.impossible "Scheme.typescheme_eq"
local
fun follow (name as METATYNAME{1=ref(NULL_TYFUN _), ...}) =
name
| follow (METATYNAME{1=ref(ETA_TYFUN (name)), ...}) =
follow (name)
| follow _ = Crash.impossible "Scheme.follow"
fun uninstantiated (METATYNAME{1=ref(NULL_TYFUN _), ...}) = true
| uninstantiated (METATYNAME{1=ref(ETA_TYFUN (name)), ...}) =
uninstantiated (name)
| uninstantiated (_) = false
fun gather_names (namelist, CONSTYPE (tylist,tyname)) =
let
val names = Lists.reducel gather_names (namelist, tylist)
in
if (uninstantiated tyname) then
follow tyname :: names
else
names
end
| gather_names(namelist, FUNTYPE (atype,atype')) =
gather_names(gather_names(namelist, atype), atype')
| gather_names(namelist, ty as RECTYPE _) =
Lists.reducel
(fn (list, ty') => gather_names(list, ty'))
(namelist, Types.rectype_range ty)
| gather_names(namelist, METATYVAR (ref (_,atype,_),_,_)) =
gather_names(namelist, atype)
| gather_names(namelist, META_OVERLOADED {1=ref atype,...}) =
gather_names(namelist, atype)
| gather_names(namelist, METARECTYPE (ref (_,_,atype,_,_))) =
gather_names(namelist, atype)
| gather_names(namelist, _) = namelist
in
fun gather_tynames (UNBOUND_SCHEME (atype,_)) = gather_names ([], atype)
| gather_tynames (SCHEME (_,(atype,_))) = gather_names ([], atype)
| gather_tynames (OVERLOADED_SCHEME _) = []
end
fun return_both atype =
case Types.has_free_imptyvars atype of
SOME ty => SOME (ty, atype)
| _ => NONE
fun has_free_imptyvars (SCHEME (_,(atype,_))) =
return_both atype
| has_free_imptyvars (UNBOUND_SCHEME ((atype,_))) =
return_both atype
| has_free_imptyvars _ = Crash.impossible "Scheme.has_free_imptyvar"
fun scheme_copy (s as SCHEME (n,(atype,instance)),tyname_copies) =
SCHEME(n, (Types.type_copy(atype, tyname_copies),instance))
| scheme_copy (UNBOUND_SCHEME (atype,instance),tyname_copies) =
UNBOUND_SCHEME(Types.type_copy(atype,tyname_copies),instance)
| scheme_copy (scheme as OVERLOADED_SCHEME _, _) =
scheme
fun tyvars (tyvarlist, SCHEME (_,(atype,_))) = Types.tyvars(tyvarlist, atype)
| tyvars (tyvarlist, UNBOUND_SCHEME (atype,_)) =
Types.tyvars(tyvarlist, atype)
| tyvars _ = Crash.impossible "Scheme.tyvars"
end;
