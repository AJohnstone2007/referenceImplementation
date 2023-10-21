require "^.basis.__int";
require "^.basis.__list";
require "../utils/lists";
require "../utils/crash";
require "../typechecker/types" ;
require "../typechecker/valenv";
require "../typechecker/scheme";
require "../rts/gen/tags";
require "../debugger/value_printer";
require "inspector_values";
functor InspectorValues(
structure Lists : LISTS
structure Crash : CRASH
structure Types : TYPES
structure Valenv : VALENV
structure Scheme : SCHEME
structure Tags : TAGS
structure ValuePrinter : VALUE_PRINTER
sharing Types.Datatypes = Valenv.Datatypes = Scheme.Datatypes
) : INSPECTOR_VALUES =
struct
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure Options = Scheme.Options
type Type = Datatypes.Type
type options = Options.options
val jump_vccs = true
val inspect_method_table = ref [] : (MLWorks.Internal.Value.ml_value * Type * Type) list ref
fun delete_all_inspect_methods _ = inspect_method_table := []
exception InspectMethodNotFound
fun fify f x = x := f (!x)
fun delete_method ran =
let
fun pred (fun_value',ran',dom') =
not (Types.type_eq (ran,ran',true,true))
in
fify (List.filter pred) inspect_method_table
end
fun insert_method (args as (_,ran,_)) =
(delete_method ran handle InspectMethodNotFound => ();
inspect_method_table := args :: (!inspect_method_table))
exception InspectMethodInvalidDomain
exception InspectMethodInvalidType
fun is_valid_domain _ = true
fun add_inspect_method (fun_value,ty) =
case ty of
Datatypes.FUNTYPE(ran,dom) =>
if is_valid_domain dom
then insert_method (fun_value,ran,dom)
else raise InspectMethodInvalidDomain
| _ => raise InspectMethodInvalidType
fun delete_inspect_method (fun_value,ty) =
case ty of
Datatypes.FUNTYPE(ran,dom) =>
if is_valid_domain dom
then delete_method ran
else raise InspectMethodInvalidDomain
| _ => raise InspectMethodInvalidType
exception TryApply
fun try_apply options ((method,ran,dom),(object,ty)) =
let
val Options.OPTIONS{compat_options=
Options.COMPATOPTIONS{old_definition,...},...}
= options
val binding_list =
Scheme.generalises_map old_definition (ran,ty)
handle Scheme.Mismatch => raise TryApply
val result_type = Scheme.apply_instantiation (dom,binding_list)
in
((MLWorks.Internal.Value.cast method) object,result_type)
end
exception ApplyAll
fun apply_all options ([],(object,ty)) = raise ApplyAll
| apply_all options (method_info::rest,dyn) =
try_apply options (method_info,dyn)
handle TryApply => apply_all options (rest,dyn)
exception Value of string
fun generate_underbar(x) = "_"
exception Error of string
fun error_notification (_,s) = raise Error s
fun contag value =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
MLWorks.Internal.Value.cast value : int
else if primary = Tags.PAIRPTR then
MLWorks.Internal.Value.cast (MLWorks.Internal.Value.sub (value, 0)) : int
else
raise Value "contag: not a constructor"
end
fun select field =
if field < 0 then
raise Value "select: negative field"
else
fn value =>
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.PAIRPTR
then
if field >= 2
then
raise Value "select: field >= 2 in pair"
else
MLWorks.Internal.Value.sub (value, field)
else if primary = Tags.POINTER
then
let
val (secondary, length) = MLWorks.Internal.Value.header value
in
if (secondary = Tags.INTEGER0 andalso field=0)
orelse (secondary = Tags.INTEGER1 andalso field=0)
then
MLWorks.Internal.Value.sub (value, 1)
else
if secondary = Tags.RECORD then
if field >= length then
raise Value "select: field >= length in record"
else
MLWorks.Internal.Value.sub (value, field+1)
else
raise Value "select: invalid secondary"
end
else
raise Value "select: invalid primary"
end
fun get_list_values (value, acc) =
let val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER1 then
if (MLWorks.Internal.Value.cast value : int) = 1 then
rev acc
else
raise Value "list: invalid integer"
else if primary = Tags.PAIRPTR then
let val head = select 0 value
val tail = select 1 value
in
get_list_values (tail, head :: acc)
end
else
raise Value "invalid list"
end
fun scheme_instantiate (Datatypes.SCHEME (n,(ty,_)),tylist) =
Types.apply (Datatypes.TYFUN(ty,n),tylist)
| scheme_instantiate _ = raise Error "Yoicks"
fun type_eq (ty1,ty2) = Types.type_eq (ty1,ty2,true,true)
fun is_scalar_value (x,ty) =
let
val primary = MLWorks.Internal.Value.primary x
in
primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1
end
fun is_string_type ty = type_eq (Types.string_type,ty)
fun is_ref_type (Datatypes.CONSTYPE (_,tyname)) =
Types.has_ref_equality tyname
| is_ref_type _ = false
val string_abbreviation = ValuePrinter.string_abbreviation
exception DuffUserMethod of exn
fun get_inspector_values options debugger_print (object,ty) =
let
fun get_arg_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) = get_arg_type object
| get_arg_type(Datatypes.FUNTYPE (arg,_)) = arg
| get_arg_type x = Datatypes.NULLTYPE
fun get_next_part_of_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) =
get_next_part_of_type object
| get_next_part_of_type x = x
fun get_values' options dyn =
get_values (apply_all options (!inspect_method_table,dyn))
handle ApplyAll => get_values dyn
| exn => raise DuffUserMethod exn
and get_values(object,ty as Datatypes.METATYVAR _) =
get_values(object,get_next_part_of_type ty)
| get_values(object,Datatypes.META_OVERLOADED {1=ref ty,...}) =
get_values(object,get_next_part_of_type ty)
| get_values(_,Datatypes.NULLTYPE) = []
| get_values(object,Datatypes.FUNTYPE _) = []
| get_values(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME
(ref(tyfun as (Datatypes.TYFUN _)),
_,_,_,_,_))) =
get_values(object,Types.apply(tyfun,tys))
| get_values(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME
(ref(Datatypes.ETA_TYFUN tyname),_,_,_,_,_))) =
get_values(object,Datatypes.CONSTYPE(tys,tyname))
| get_values(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME
(ref(Datatypes.NULL_TYFUN
(_,(ref(Datatypes.TYFUN(Datatypes.NULLTYPE,0))))),
name,n,b,ve,abs))) =
if debugger_print then
get_values(object,
Datatypes.CONSTYPE
(tys,
Datatypes.TYNAME(Types.make_stamp(),
name,n,b,ve,NONE,
abs,ve,0)))
else
[]
| get_values(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME
(ref(Datatypes.NULL_TYFUN(_,tyfun)),
name,n,b,ve,abs))) =
if debugger_print then
get_values(object,Datatypes.CONSTYPE(tys,
Datatypes.METATYNAME(tyfun,name,n,b,ve,abs)))
else
[]
| get_values (object, ty as Datatypes.CONSTYPE(tys,tyname)) =
if Types.num_or_string_typep ty then []
else if Types.tyname_eq (tyname,Types.bool_tyname) then []
else if Types.tyname_eq (tyname,Types.list_tyname) then
(case tys of
[ty] =>
let
val element_list = get_list_values (object,[])
val (_,tagged_list) =
Lists.reducel
(fn ((n,l),object) =>
(n+1,(Int.toString n,(object,ty))::l))
((0,[]),element_list)
in
rev tagged_list
end
| _ => error_notification (object, "<list arity>"))
handle Value message =>
error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.ml_value_tyname) then []
else if Types.tyname_eq (tyname,Types.ref_tyname) then
let
fun convert_ref value =
if
MLWorks.Internal.Value.primary value =
Tags.REFPTR andalso
MLWorks.Internal.Value.header value = (Tags.ARRAY, 1)
then
MLWorks.Internal.Value.cast value :
MLWorks.Internal.Value.T ref
else
error_notification (value,"Not a ref")
in
case (tys, convert_ref object) of
([ty], ref object) => [("ref",(object,ty))]
| _ => error_notification (object, "<ref arity>")
end
else if Types.tyname_eq (tyname,Types.array_tyname) then
if MLWorks.Internal.Value.primary object = Tags.REFPTR
then
let
val (secondary, length) =
MLWorks.Internal.Value.header object
in
if secondary = Tags.ARRAY
then
case tys of
[ty] =>
let
fun iterate (0,acc) = acc
| iterate (n,acc) =
iterate (n-1,(Int.toString (n-1),
MLWorks.Internal.Value.sub (object, n+2))::acc)
val subvalues = iterate (length,[])
val string_list =
map (fn (s,object) => (s,(object,ty))) subvalues
in
string_list
end
| _ => error_notification (object, "array arity")
else
error_notification(object,"Array not an array")
end
else error_notification(object,"Array not an ref")
else if Types.tyname_eq (tyname,Types.vector_tyname) then
case tys of
[ty] =>
let
val primary = MLWorks.Internal.Value.primary object
val (secondary, length) =
if primary = Tags.POINTER then
MLWorks.Internal.Value.header object
else
if primary = Tags.PAIRPTR then
(Tags.RECORD, 2)
else
(Tags.MLERROR, 0)
fun record_map (object, 2) f =
[f (MLWorks.Internal.Value.sub (object, 0)),
f (MLWorks.Internal.Value.sub (object, 1))]
| record_map (object, length) f =
let
fun iterate (list, 0) = list
| iterate (list, n) =
iterate ((f (MLWorks.Internal.Value.sub (object, n)))::list, n-1)
in
iterate ([], length)
end
in
if secondary = Tags.RECORD then
let
val element_list =
record_map
(object,length)
(fn x => x)
val (_,tagged_list) =
Lists.reducel
(fn ((n,l),object) =>
(n+1,(Int.toString n,(object,ty))::l))
((0,[]),element_list)
in
rev tagged_list
end
else
if secondary = Tags.MLERROR then
error_notification(object,"<Vector not a pointer>")
else
error_notification (object,"<Vector not a record>")
end
| _ => error_notification (object,"<Bad vector type>")
else
let
val primary = MLWorks.Internal.Value.primary object
val result =
case tyname of
Datatypes.TYNAME
(_,ty_name,_,_,
ref(val_map as Datatypes.VE(_,constructor_map)),
_,ref is_abs,_,_) =>
if is_abs orelse
Datatypes.NewMap.is_empty constructor_map then []
else
let
val (domain,range) =
Lists.unzip(NewMap.to_list_ordered
constructor_map)
val is_a_single_constructor =
(length domain = 1)
fun test_scheme
(Datatypes.SCHEME(_,(Datatypes.FUNTYPE _,_))) =
true
| test_scheme
(Datatypes.UNBOUND_SCHEME
(Datatypes.FUNTYPE _,_)) =
true
| test_scheme _ = false
val is_a_single_vcc =
case range of
[x] => test_scheme x
| _ => false
in
if is_a_single_constructor andalso not is_a_single_vcc then []
else if is_a_single_vcc then
case domain of
[name' as Ident.CON name] =>
let
val name = Symbol.symbol_name name
val scheme = Valenv.lookup(name',val_map)
val ty =
(case scheme of
Datatypes.SCHEME _ => scheme_instantiate(scheme,tys)
| Datatypes.UNBOUND_SCHEME (ty',_) => ty'
| Datatypes.OVERLOADED_SCHEME _ => Datatypes.NULLTYPE)
val arg_type = get_arg_type ty
in
[(name, (object,arg_type))]
end
| _ => error_notification(object,"(Problems in vcc code)")
else if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then []
else if primary = Tags.POINTER orelse primary = Tags.PAIRPTR then
let
val (code, packet) =
if primary = Tags.PAIRPTR then
(MLWorks.Internal.Value.sub (object,0),
MLWorks.Internal.Value.sub (object,1))
else
(MLWorks.Internal.Value.sub (object,1),
MLWorks.Internal.Value.sub (object,2))
val name' = Lists.nth(MLWorks.Internal.Value.cast(code),domain)
val name =
case name' of
Ident.CON x => Ident.Symbol.symbol_name x
| _ => error_notification (object,"Can't figure out name")
val scheme = Valenv.lookup(name',val_map)
val ty =
(case scheme of
Datatypes.SCHEME _ => scheme_instantiate (scheme,tys)
| Datatypes.UNBOUND_SCHEME(ty',_) => ty'
| Datatypes.OVERLOADED_SCHEME _ => Datatypes.NULLTYPE)
val arg_type = get_arg_type ty
in
[(name,(packet,arg_type))]
end
handle Lists.Nth => error_notification(object,"lists.nth 2")
else
error_notification(object, "(Not INTEGER,POINTER,PAIR in expected datatype case)")
end
| _ => error_notification(object,"(Can't handle this part of the object)")
in
case result of
[(_,x as (_,ty))] =>
if jump_vccs andalso not (is_ref_type ty)
then get_values' options x
else result
| _ => result
end
| get_values(object,ty as Datatypes.RECTYPE _) =
let
val dom =
map
(fn (Ident.LAB s) => Symbol.symbol_name s)
(Types.rectype_domain ty)
val range = Types.rectype_range ty
val primary = MLWorks.Internal.Value.primary object
in
if length dom = 0 then []
else
if primary = Tags.PAIRPTR orelse primary = Tags.POINTER then
let
fun get_elements ([],_) = []
| get_elements (ty::tys,pos) =
(MLWorks.Internal.Value.sub (object,pos),ty)
:: get_elements(tys,pos+1)
val record_size =
if primary = Tags.PAIRPTR then
2
else
#2 (MLWorks.Internal.Value.header object)
in
if length range = record_size then
let
val labtyvals = Lists.zip (dom,get_elements(range, if record_size = 2 then 0 else 1))
val items =
map (fn (lab,(value,ty)) => (lab,(value,ty))) labtyvals
in
items
end
else
error_notification(object,"(Record is not of correct size)")
end
else
case dom of
[] => []
| _ => error_notification(object,"(record pointer not found when expected)")
end
| get_values _ = []
in
get_values' options (object,ty)
end
end
;
