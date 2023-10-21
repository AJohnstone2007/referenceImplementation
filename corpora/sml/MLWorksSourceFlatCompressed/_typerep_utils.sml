require "../basis/__int";
require "../utils/lists";
require "../utils/crash";
require "../typechecker/types";
require "../typechecker/scheme";
require "../basics/absyn";
require "typerep_utils";
functor TyperepUtils (
structure Lists : LISTS
structure Crash : CRASH
structure Types : TYPES
structure Scheme :SCHEME
structure Absyn : ABSYN
sharing Absyn.Ident = Types.Datatypes.Ident
sharing Absyn.Set = Scheme.Set
sharing Types.Datatypes = Scheme.Datatypes
sharing type Absyn.Type = Types.Datatypes.Type
sharing type Absyn.Instance = Types.Datatypes.Instance
sharing type Absyn.InstanceInfo = Types.Datatypes.InstanceInfo
) : TYPEREP_UTILS =
struct
structure Datatypes = Types.Datatypes
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
structure Location = Ident.Location
structure Absyn = Absyn
fun make_tuple_exp exps =
let fun do_one ((index,l),exp) =
(index+1,(Ident.LAB (Symbol.find_symbol(Int.toString index)),exp) :: l)
val (_,result) = Lists.reducel do_one ((0,[]),exps)
in
Absyn.RECORDexp result
end
val dynamic_path =
Ident.PATH (Symbol.find_symbol "MLWorks",
Ident.PATH (Symbol.find_symbol "Internal",
Ident.PATH(Symbol.find_symbol "Dynamic",
Ident.NOPATH)))
val coerce_id = Ident.LONGVALID(dynamic_path,Ident.VAR(Symbol.find_symbol"coerce"))
val coerce_type = Datatypes.FUNTYPE(Types.add_to_rectype
(Ident.LAB (Symbol.find_symbol "1"),
Types.dynamic_type,
Types.add_to_rectype
(Ident.LAB (Symbol.find_symbol "2"),
Types.typerep_type,
Types.empty_rectype)),
Types.ml_value_type)
fun make_coerce_expression (exp,atype) =
Absyn.APPexp (Absyn.VALexp (coerce_id,ref coerce_type,
Location.UNKNOWN,
ref(Datatypes.ZERO,NONE)),
make_tuple_exp [exp,Absyn.MLVALUEexp (MLWorks.Internal.Value.cast atype)],
Location.UNKNOWN,
ref Types.ml_value_type,
false)
exception ConvertDynamicType
fun convert_dynamic_type (use_value_polymorphism,ty,level,tyvars) =
if Scheme.check_closure (use_value_polymorphism,ty,level,tyvars)
then
case Scheme.schemify'(level,
true,
Datatypes.UNBOUND_SCHEME (ty,NONE),
tyvars,
true) of
Datatypes.SCHEME(_,(scheme_type,_)) => scheme_type
| Datatypes.UNBOUND_SCHEME (scheme_type,_) => scheme_type
| _ => Crash.impossible "convert_dynamic_type"
else
raise ConvertDynamicType
end;
