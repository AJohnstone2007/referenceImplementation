require "../basis/__int";
require "../utils/crash";
require "../utils/lists";
require "../basics/identprint";
require "../main/info";
require "types";
require "unify";
require "completion";
require "basis";
require "control_unify";
functor Control_Unify
(structure Crash : CRASH
structure Lists : LISTS
structure IdentPrint : IDENTPRINT
structure Types : TYPES
structure Unify : UNIFY
structure Completion : COMPLETION
structure Basis : BASIS
structure Info : INFO
sharing Completion.Options = IdentPrint.Options
sharing Basis.BasisTypes.Datatypes = Unify.Datatypes = Completion.Datatypes = Types.Datatypes
sharing IdentPrint.Ident = Basis.BasisTypes.Datatypes.Ident
sharing type IdentPrint.Options.options = Unify.options
) : CONTROL_UNIFY =
struct
structure Info = Info
structure BasisTypes = Basis.BasisTypes
structure Datatypes = BasisTypes.Datatypes
structure Options = IdentPrint.Options
structure Ident = IdentPrint.Ident
structure Symbol = Ident.Symbol
local
fun string_labels [] = ""
| string_labels [(label,_)] = (IdentPrint.printLab label)
| string_labels ((label,_)::labels) =
(IdentPrint.printLab label) ^ ", " ^ (string_labels labels)
fun string_domain (Unify.RIGID record) =
"{" ^ string_labels record ^ "}"
| string_domain (Unify.FLEX record) =
case record of
[] => "{...}"
| _ => "{" ^ (string_labels record) ^ ", ...}"
fun to_type (Unify.RIGID record) =
let
val ty =
Lists.reducel
(fn (t,(lab,t')) => Types.add_to_rectype (lab,t',t))
(Types.empty_rectype,record)
in
ty
end
| to_type (Unify.FLEX record) =
let
val ty =
Lists.reducel
(fn (t,(lab,t')) => Types.add_to_rectype (lab,t',t))
(Types.empty_rectype,record)
in
Datatypes.METARECTYPE (ref (0,false,ty,false, false))
end
fun is_tuple_domain (Unify.RIGID record) =
let
val lablist = map #1 record
val len = Lists.length lablist
fun check n =
if n > len then true
else
Lists.member (Ident.LAB (Symbol.find_symbol (Int.toString n)),lablist)
andalso
check (n+1)
in
check 1
end
| is_tuple_domain _ = false
fun describe (Unify.FAILED (ty,ty')) =
[Datatypes.Err_String "\n    Type clash between\n      ",
Datatypes.Err_Type ty,
Datatypes.Err_String "\n    and\n      ",
Datatypes.Err_Type ty']
| describe (Unify.RECORD_DOMAIN (domain,domain')) =
if is_tuple_domain domain andalso is_tuple_domain domain'
then
[Datatypes.Err_String "\n    Lengths of tuples differ:\n      ",
Datatypes.Err_Type (to_type domain),
Datatypes.Err_String "\n    and\n      ",
Datatypes.Err_Type (to_type domain')]
else
[Datatypes.Err_String "\n    Domains of record types differ:\n      ",
Datatypes.Err_String (string_domain domain),
Datatypes.Err_String "\n    and\n      ",
Datatypes.Err_String (string_domain domain')]
| describe (Unify.EXPLICIT_TYVAR (ty,ty')) =
[Datatypes.Err_String "\n     because the type variable ",
Datatypes.Err_Type ty,
Datatypes.Err_String " of the first type",
Datatypes.Err_String " has a different scope from",
Datatypes.Err_String "\n     the type variable ",
Datatypes.Err_Type ty',
Datatypes.Err_String " of the second type.",
Datatypes.Err_String
"\n        (One of them probably cannot be generalized.) "]
| describe (Unify.EQ_AND_IMP (eq,imp,ty)) =
[Datatypes.Err_String "\n    ",
Datatypes.Err_Type ty,
Datatypes.Err_String (case (eq, imp) of
(true, false) => " does not admit equality"
| (true, true) => " does not admit equality and is not imperative"
| (false, true) => " is not imperative"
| _ => Crash.impossible "Control_Unify.describe EQ_AND_IMP")]
| describe (Unify.CIRCULARITY (ty,ty')) =
[Datatypes.Err_String "\n    Circular type results from unifying\n      ",
Datatypes.Err_Type ty,
Datatypes.Err_String "\n    and\n      ",
Datatypes.Err_Type ty']
| describe (Unify.OVERLOADED (tv, ty)) =
let
val has_default =
(tv = Ident.real_literal_tyvar) orelse
(tv = Ident.int_literal_tyvar) orelse
(tv = Ident.word_literal_tyvar)
val initial_string =
if has_default then
"\n    Type clash between "
else
"\n    Type clash between overloaded type variable "
in
[Datatypes.Err_String
(initial_string
^ IdentPrint.printTyVar tv
^ " and "),
Datatypes.Err_Type ty]
end
| describe Unify.OK = Crash.impossible "ControlUnify.generate_message"
| describe (Unify.SUBSTITUTION _) = Crash.impossible "SUBSTITUTION:ControlUnify.generate_message"
in
fun unify
(error_info,options as Options.OPTIONS{print_options,...})
{
first : Datatypes.Type,
second : Datatypes.Type,
result : Datatypes.Type,
context : BasisTypes.Context,
error : unit -> Info.Location.T * Datatypes.type_error_atom list * Datatypes.Type
} =
case Unify.unified(options, first, second, false) of
Unify.OK => result
| error_code =>
let
val (location, err_list, result) = error ()
val unify_err_list = describe error_code
val report =
Completion.report_type_error
(options, Basis.env_of_context context,
err_list@unify_err_list)
in
(Info.error error_info
(Info.RECOVERABLE, location, report);
result)
end
end
end
;
