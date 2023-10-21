require "$.basis.__int";
require "$.basis.__int32";
require "$.basis.__word" ;
require "$.basis.__word32" ;
require "$.basis.__string_cvt" ;
require "$.basis.__char";
require "$.basis.__string";
require "^.utils.__terminal";
require "../typechecker/types" ;
require "../typechecker/valenv";
require "value_printer_utilities";
require "debugger_types";
require "../rts/gen/tags";
require "../utils/lists";
require "../utils/crash";
require "value_printer" ;
functor ValuePrinter(
structure Types : TYPES
structure Valenv : VALENV
structure ValuePrinterUtilities : VALUEPRINTERUTILITIES
structure Debugger_Types : DEBUGGER_TYPES
structure Tags : TAGS
structure Lists : LISTS
structure Crash : CRASH
sharing Types.Datatypes = Valenv.Datatypes =
ValuePrinterUtilities.BasisTypes.Datatypes
sharing type Types.Datatypes.Type = Debugger_Types.Type
) : VALUE_PRINTER =
struct
structure BasisTypes = ValuePrinterUtilities.BasisTypes
structure Datatypes = Types.Datatypes
structure NewMap = Datatypes.NewMap
structure Ident = Datatypes.Ident
structure Debugger_Types = Debugger_Types
structure Options = Types.Options
type TypeBasis = BasisTypes.Basis
type Type = Datatypes.Type
type DebugInformation = Debugger_Types.information
val do_debug = false
fun debug s = if do_debug then Terminal.output(s ^ "\n") else ()
val cast : 'a -> 'b = MLWorks.Internal.Value.cast
fun max_len (max_seq_size, actual_seq_size) =
if max_seq_size < 0 orelse max_seq_size > actual_seq_size then
actual_seq_size
else
max_seq_size
fun generate_underbar x = "_" ^ (if do_debug then (" [" ^ x ^ "] ") else "")
val ellipsis = ".."
val list_ellipsis = ", " ^ ellipsis ^ "]"
val string_abbreviation = ref "\\..."
fun get_arg_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) = get_arg_type object
| get_arg_type(Datatypes.FUNTYPE (arg,_)) = arg
| get_arg_type x = Datatypes.NULLTYPE
fun splice (left, separator, right) [] = left ^ right
| splice (left, separator, right) (s::ss) =
concat (left :: s ::
Lists.reducer
(fn (s, strings) => separator :: s :: strings)
(ss, [right]))
fun vector_map (object, length) f =
let
fun iterate (list, 0) = list
| iterate (list, n) =
iterate ((f (MLWorks.Internal.Value.sub (object, n)))::list, n-1)
in
iterate ([], length)
end
fun record_map (object, 2) f =
[f (MLWorks.Internal.Value.sub (object, 0)),
f (MLWorks.Internal.Value.sub (object, 1))]
| record_map (object, length) f =
vector_map (object,length) f
fun array_map (object, length) f =
let
fun iterate (list, 0) = list
| iterate (list, n) =
iterate ((f (MLWorks.Internal.Value.sub (object, n+2)))::list, n-1)
in
iterate ([], length)
end
fun bytearray_map (object, length) f =
let
fun iterate (list, 0) = list
| iterate (list, n) =
iterate
(cast
(f(MLWorks.Internal.Value.sub_byte(object, n+3))):: list, n-1)
in
iterate ([], length)
end
fun floatarray_map (object, length) f =
let
fun iterate (list, 0) = list
| iterate (list, n) =
iterate
(cast
(f(MLWorks.Internal.FloatArray.sub(object, n-1))):: list, n-1)
in
iterate ([], length)
end
exception Value of string
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
fun integer value =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
cast value : int
else
raise Value "not an integer"
end
fun word value =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
cast value : word
else
raise Value "not a word"
end
fun word32 value =
if
MLWorks.Internal.Value.primary value = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header value) = Tags.STRING
then
(cast value : Word32.word)
else
raise Value "not a word32"
fun int32 value =
if
MLWorks.Internal.Value.primary value = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header value) = Tags.STRING
then
(cast value : Int32.int)
else
raise Value "not a int32"
fun contag value =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
cast value : int
else if primary = Tags.PAIRPTR then
cast (MLWorks.Internal.Value.sub (value, 0)) : int
else
raise Value "contag: not a constructor"
end
fun bool value =
let
val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
case cast value : int of
0 => false
| 1 => true
| _ => raise Value "bool: invalid integer"
else
raise Value "bool: wrong primary"
end
fun string value =
if
MLWorks.Internal.Value.primary (cast value) = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header (cast value)) = Tags.STRING
then
cast value : string
else
raise Value "not a string"
fun list (count, value, acc) =
let val primary = MLWorks.Internal.Value.primary value
in
if primary = Tags.INTEGER1 then
if (cast value : int) = 1 then
(true, rev acc)
else
raise Value "list: invalid integer"
else if primary = Tags.PAIRPTR then
let val head = select 0 value
val tail = select 1 value
in
if count = 0 then
(false, rev acc)
else
list (if count < 0 then count else count - 1, tail, head :: acc)
end
else
raise Value "invalid list"
end
fun real value =
if
MLWorks.Internal.Value.primary value = Tags.POINTER andalso
MLWorks.Internal.Value.header value = (Tags.BYTEARRAY, 12)
then
cast value : real
else
raise Value "not a real"
fun code_name value =
if
MLWorks.Internal.Value.primary value = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header value) = Tags.BACKPTR
then
MLWorks.Internal.Value.code_name value
else
raise Value "code_name: not a code item"
fun exn value =
if MLWorks.Internal.Value.primary value = Tags.PAIRPTR then
let
val (name, arg) = cast value
in
if MLWorks.Internal.Value.primary name = Tags.PAIRPTR then
let
val (unique, string) = cast name
in
if MLWorks.Internal.Value.primary unique = Tags.REFPTR then
if MLWorks.Internal.Value.primary string = Tags.POINTER then
if #1 (MLWorks.Internal.Value.header string) = Tags.STRING then
cast value : exn
else raise Value "exn: wrong secondary on exn name string"
else raise Value "exn: wrong primary on exn name string"
else raise Value "exn: wrong primary on exn name unique"
end
else raise Value "exn: wrong primary on exn name"
end
else raise Value "exn: wrong primary"
fun convert_ref value =
if
MLWorks.Internal.Value.primary value = Tags.REFPTR andalso
MLWorks.Internal.Value.header value = (Tags.ARRAY, 1)
then
cast value : MLWorks.Internal.Value.T ref
else
raise Value "not a ref cell"
fun get_location s =
let
val sz = size s
fun find_end_of_name x =
if x=sz orelse substring (s,x,1) = "["
then x
else find_end_of_name (x+1)
val ix = find_end_of_name 0
in
substring (s,ix,sz-ix)
end
fun find_end_of_name name =
let
val s = size name
fun f x =
if x=s
then name
else if substring (name,x,1) = "["
then substring (name,0,x)
else f (x+1)
in
f 0
end
fun exn_lookup (debug_info, name) =
let
val name' = find_end_of_name name
in
case Debugger_Types.lookup_debug_info (debug_info,name) of
SOME (Debugger_Types.FUNINFO {ty,...}) =>
(name',get_arg_type ty)
| _ => (name',Datatypes.NULLTYPE)
end
val error_notify = false
datatype environment = EMPTY | ENTRY of Datatypes.Type list * environment
fun unknown (message,primary,secondary,length) =
concat [message,
"primary = ",
Int.toString primary,
", secondary = ",
Int.toString secondary,
", length = ",
Int.toString length]
fun is_closure value =
let
val fst = select 0 value
in
MLWorks.Internal.Value.primary fst = Tags.POINTER andalso
#1 (MLWorks.Internal.Value.header fst) = Tags.BACKPTR
end
fun shape (0,_,_,_,_) = generate_underbar("shape")
| shape (depth, max_seq_size, max_str_size, float_precision,object) =
let
val primary = MLWorks.Internal.Value.primary object
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1
then Int.toString(cast object)
else if primary = Tags.PAIRPTR
then
if is_closure object
then "<fn>"
else
splice ("{", ", ", "}")
(record_map (object, 2)
(fn object => shape (depth-1, max_seq_size, max_str_size, float_precision,object)))
else
if primary = Tags.POINTER then
let
val (secondary, length) = MLWorks.Internal.Value.header object
in
if secondary = Tags.RECORD then
if is_closure object
then "<fn>"
else
splice ("{", ", ", "}")
(record_map (object, length)
(fn object => shape (depth-1, max_seq_size, max_str_size, float_precision,object)))
else if secondary = Tags.STRING then
concat ["\"",
MLWorks.String.ml_string
(cast object,
max_str_size), "\""]
else if secondary = Tags.BYTEARRAY then
if length = 12 then
MLWorks.Internal.real_to_string (cast object,float_precision)
else
unknown("bad real: ",primary, secondary,length)
else if secondary = Tags.CODE then
"<CODE>"
else if secondary = Tags.BACKPTR then
"<BACKPTR>"
else if secondary = 0 andalso length = 0
then "<fn>"
else
unknown("bad ptr: ", primary, secondary, length)
end
else if primary = Tags.REFPTR then
let
val (secondary, length) = MLWorks.Internal.Value.header object
val tail = if length > max_seq_size andalso max_seq_size > 0
then list_ellipsis
else "]"
in
if secondary = Tags.ARRAY then
splice ("array[", ", ", tail)
(array_map (object, max_len (max_seq_size, length))
(fn object => shape (depth-1, max_seq_size, max_str_size, float_precision,object)))
else
if secondary = Tags.BYTEARRAY then
splice ("bytearray[", ",", tail)
(bytearray_map (object, max_len (max_seq_size, length))
(fn object =>
shape (depth-1, max_seq_size, max_str_size,
float_precision, cast object)))
else
unknown("bad refptr: ", primary, secondary,length)
end
else
unknown("bad primary: ", primary, 0, 0)
end
fun stringify_value debugger_print
(print_options as
Options.PRINTOPTIONS {maximum_seq_size,
maximum_string_size,
maximum_ref_depth,
maximum_depth,
print_fn_details,
print_exn_details,
float_precision,
...},
object,
ty,
interpreter_information) =
let
fun error_notification (object, message) =
let
val shape = "_"
in
concat
(if error_notify then
["<", message, ": ", shape, ">"]
else
["_"])
end
fun get_arg_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) = get_arg_type object
| get_arg_type(Datatypes.FUNTYPE (arg,_)) = arg
| get_arg_type x = Datatypes.NULLTYPE
fun get_next_part_of_type(Datatypes.METATYVAR(ref(_,object,_),_,_)) =
get_next_part_of_type object
| get_next_part_of_type x = x
fun needs_brackets (ty as Datatypes.METATYVAR _) =
needs_brackets (get_next_part_of_type ty)
| needs_brackets (Datatypes.META_OVERLOADED {1=ref ty,...}) =
needs_brackets (get_next_part_of_type ty)
| needs_brackets (Datatypes.CONSTYPE(tys,Datatypes.METATYNAME(ref(tyfun as (Datatypes.TYFUN _)),_,_,_,_,_))) =
needs_brackets (Types.apply(tyfun,tys))
| needs_brackets (Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME(ref(Datatypes.ETA_TYFUN tyname),_,_,_,_,_))) =
needs_brackets (Datatypes.CONSTYPE(tys,tyname))
| needs_brackets (Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME(ref(Datatypes.NULL_TYFUN _),_,_,_,_,_))) = false
| needs_brackets (ty as Datatypes.CONSTYPE(tys,tyname)) =
if
Types.num_or_string_typep ty orelse
Types.tyname_eq (tyname,Types.bool_tyname) orelse
Types.tyname_eq (tyname,Types.list_tyname) orelse
Types.tyname_eq (tyname,Types.ml_value_tyname) orelse
Types.tyname_eq (tyname,Types.dynamic_tyname)
then false
else true
| needs_brackets _ = false
val entry1 = "<Entry1>"
val entry2 = "<Entry2>"
val closure = "<Closure>"
val sz_entry1 = size entry1
val sz_closure = size closure
fun strip_fn_name name =
let
val sz = size name
in
if sz >= sz_entry1 andalso
let
val en_string = substring (name, sz-sz_entry1, sz_entry1)
in
en_string = entry1 orelse en_string = entry2
end then
substring (name, 0, sz-sz_entry1)
else
if sz >= sz_closure andalso
substring (name, sz-sz_closure, sz_closure) = closure then
substring (name, 0, sz-sz_closure)
else
name
end
fun value_to_string(object,ty,env) =
let
fun value_to_string'(_,_,_,_,0) = ellipsis
| value_to_string'(object,ty as Datatypes.METATYVAR _,env,ref_depth,depth) =
value_to_string'(object,get_next_part_of_type ty,env,ref_depth,depth)
| value_to_string'
(object, Datatypes.META_OVERLOADED {1=ref ty,...},
env,ref_depth,depth) =
value_to_string'(object,get_next_part_of_type ty,env,ref_depth,depth)
| value_to_string'(object,Datatypes.TYVAR _,env,ref_depth,depth) =
generate_underbar("tyvar")
| value_to_string'(object,Datatypes.FUNTYPE _,env,ref_depth,depth) =
if print_fn_details then
let
val name = MLWorks.String.ml_string (code_name (select 0 object), ~1)
in
"fn[" ^ strip_fn_name name ^ "]"
end
handle Value message => error_notification (object, message)
else
"fn"
| value_to_string' (object,Datatypes.METARECTYPE (ref (_,uninstantiated,ty,_,_)),env,ref_depth,depth) =
if uninstantiated
then
error_notification (object,"Uninstantiated METARECTYPE")
else
value_to_string' (object,ty,env,ref_depth,depth)
| value_to_string'(object,ty as (Datatypes.RECTYPE _),env,ref_depth,depth) =
if depth <= 1
then ellipsis
else
let
val dom = Types.rectype_domain ty
val len = length dom
val range = Types.rectype_range ty
val primary = MLWorks.Internal.Value.primary object
in
if len = 0 then
"()"
else
if primary = Tags.PAIRPTR orelse primary = Tags.POINTER then
let
fun get_elements ([],_) = []
| get_elements (ty::tys,pos) =
value_to_string'
(MLWorks.Internal.Value.sub (object,pos),
ty,env,ref_depth,depth-1) ::
get_elements(tys,pos+1)
fun tuple_indices () =
if len < 2 then NONE
else
let
fun tuple_indices' (acc,0) = SOME acc
| tuple_indices' (acc,n) =
(let
val name = Int.toString n
val sym = Ident.Symbol.find_symbol name
val lab = Ident.LAB sym
val pos = Lists.find (lab,dom)
in
tuple_indices' (pos::acc, n-1)
end)
in
tuple_indices' ([],len)
handle Lists.Find => NONE
end
fun print_as_record values =
let
fun print_as_record' (Ident.LAB name,value) =
Ident.Symbol.symbol_name name ^ "=" ^ value
val result = map print_as_record' (Lists.zip(dom,values))
in
case result of
[] => "()"
| [one] => "{" ^ one ^ "}"
| arg::args =>
let
fun put_together [] = ""
| put_together (h::t) = ", " ^ h ^ put_together t
in
"{" ^ arg ^ put_together args ^ "}"
end
end
fun print_as_tuple (ilist, els) =
let
val ordered_els = (map (fn n => Lists.nth(n,els)) ilist
handle Lists.Nth => Crash.impossible "Problem (2) in value_printer")
fun put_together [] = ""
| put_together (h::t) = ", " ^ h ^ put_together t
in
case ordered_els of
arg::args =>
"(" ^ arg ^ put_together args ^ ")"
| _ =>
Crash.impossible "Problem (1) in value_printer"
end
val record_size =
if primary = Tags.PAIRPTR then
2
else
#2 (MLWorks.Internal.Value.header object)
in
if length range = record_size then
let val elements = get_elements (range, if record_size = 2 then 0 else 1)
in
case tuple_indices () of
NONE =>
print_as_record elements
| SOME index_list =>
print_as_tuple (index_list,elements)
end
else
error_notification(object,"(Record is not of correct size)")
end
else
error_notification(object,"(record pointer not found when expected)")
end
| value_to_string'(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME(ref(tyfun as (Datatypes.TYFUN _)),_,_,_,_,_)),
env,ref_depth,depth) =
value_to_string'(object,Types.apply(tyfun,tys),env,ref_depth,depth)
| value_to_string'(object,
Datatypes.CONSTYPE
(tys,Datatypes.METATYNAME(ref(Datatypes.ETA_TYFUN tyname),_,_,_,_,_)),
env,ref_depth,depth) =
value_to_string'(object,Datatypes.CONSTYPE(tys,tyname),env,ref_depth,depth)
| value_to_string' (object, ty as Datatypes.CONSTYPE(tys,tyname),env,ref_depth,depth) =
if debugger_print andalso
(case tyname of
Datatypes.METATYNAME(ref(Datatypes.NULL_TYFUN _),_,_,_,ref ve,_) =>
Valenv.empty_valenvp ve
| _ => false) then
(case tyname of
Datatypes.METATYNAME(ref(Datatypes.NULL_TYFUN(_,tyfun)),name,n,b,ve,is_abs) =>
(value_to_string'(object,
Datatypes.CONSTYPE(tys,
Datatypes.METATYNAME(tyfun,name,n,b,ve,is_abs)),
env,ref_depth,depth))
| _ => Crash.impossible "CONSTYPE:value_printer")
else
let
val (in_table,func) =
(false, fn _ => "")
val primary = MLWorks.Internal.Value.primary object
in
if in_table then
let
val print_methods_for_arguments =
map (fn ty => fn (object) => value_to_string'(object,ty,env,ref_depth,depth-1)) tys
fun extract_elements object =
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
[object]
else if primary = Tags.PAIRPTR then
[MLWorks.Internal.Value.sub (object, 0),
MLWorks.Internal.Value.sub (object, 1)]
else if primary = Tags.REFPTR then
let
val (secondary, length) = MLWorks.Internal.Value.header object
in
if secondary = Tags.ARRAY then
array_map
(object, max_len (maximum_seq_size, length))
(fn x => x)
else
bytearray_map
(object, max_len (maximum_seq_size, length))
(fn x => x)
end
else if primary = Tags.POINTER then
let
val (secondary, length) = MLWorks.Internal.Value.header object
in
if secondary = Tags.RECORD then
record_map (object, length) (fn x => x)
else
[]
end
else
[]
val list_of_elements = extract_elements object
fun is_integer_tagged object =
let
val primary = MLWorks.Internal.Value.primary object
in
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
(true, cast object : int)
else
(false, 0)
end
in
func(list_of_elements,print_methods_for_arguments,extract_elements,is_integer_tagged)
handle _ => error_notification (object,"(Failure in a user print function)")
end
else
if Types.type_eq (ty, Types.int32_type, true, true) then
Int32.toString (int32 object)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.int_typep ty then
Int.toString (integer object)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.type_eq (ty, Types.word32_type, true, true) then
"0w" ^ Word32.fmt StringCvt.DEC (word32 object)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.word_typep ty then
"0w" ^ Word.fmt StringCvt.DEC (word object)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.real_typep ty then
MLWorks.Internal.real_to_string(real object,float_precision)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.bool_tyname) then
(fn true => "true" | false => "false") (bool object)
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.string_tyname) then
concat ["\"", MLWorks.String.ml_string
(string object,
maximum_string_size), "\""]
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.char_tyname) then
concat ["#\"", MLWorks.String.ml_string
(string (String.str(Char.chr(ord(MLWorks.Internal.Value.cast object)))),
maximum_string_size), "\""]
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.list_tyname) then
(case tys of
[ty] =>
if depth <= 1 then "[...]"
else
let
val (total,element_list) =
list (maximum_seq_size,object,[])
in
concat
("[" ::
rev (
(if total then "]" else list_ellipsis) ::
Lists.reducel
(fn (list, object) =>
value_to_string' (object, ty, env, ref_depth, depth-1) ::
(case list of
[] => []
| list => ", " :: list))
([],element_list)))
end
| _ => error_notification (object, "<list arity>"))
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.ml_value_tyname) then
shape (depth, maximum_seq_size,
maximum_string_size, float_precision,object)
else if Types.tyname_eq (tyname,Types.exn_tyname) then
let
val s = MLWorks.Internal.Value.exn_name (exn object)
in
if print_exn_details then s
else find_end_of_name s
end
else if Types.tyname_eq (tyname,Types.ref_tyname) then
if ref_depth <= 0 then
generate_underbar("ref_depth")
else
(case (tys, convert_ref object) of
([ty], ref object) =>
concat ["ref(",
value_to_string' (object,ty,env,ref_depth-1,depth-1),
")"]
| _ => error_notification (object, "<ref arity>"))
handle Value message => error_notification (object, "<" ^ message ^ ">")
else if Types.tyname_eq (tyname,Types.array_tyname) then
if ref_depth <= 0 then
generate_underbar("ref depth")
else
case tys of
[ty] =>
if MLWorks.Internal.Value.primary object = Tags.REFPTR
then
let
val (secondary,length) =
MLWorks.Internal.Value.header object
in
if secondary = Tags.ARRAY then
let
val element_list =
array_map
(object,
max_len (maximum_seq_size, length))
(fn x => x)
val tail =
if length > maximum_seq_size andalso
maximum_seq_size > 0 then
list_ellipsis
else
"]"
in
concat
("#A[" ::
rev (tail ::
Lists.reducel
(fn (list, object') =>
value_to_string' (object', ty, env, ref_depth, depth-1) ::
(case list of
[] => []
| list => ", " :: list))
([],element_list)))
end
else
error_notification (object,"<Array not an array>")
end
else
error_notification(object,"<Array not a ref pointer>")
| _ => error_notification (object,"<Bad array type>")
else if Types.tyname_eq (tyname,Types.bytearray_tyname) then
if ref_depth <= 0 then
generate_underbar("ref depth")
else
if MLWorks.Internal.Value.primary object = Tags.REFPTR then
let
val (secondary,length) =
MLWorks.Internal.Value.header object
in
if secondary = Tags.BYTEARRAY then
let
val element_list =
bytearray_map
(object,
max_len (maximum_seq_size, length))
(fn x => x)
val tail =
if length > maximum_seq_size andalso
maximum_seq_size > 0 then
list_ellipsis
else
"]"
in
concat
("#B[" ::
rev (tail ::
Lists.reducel
(fn (list, object') =>
value_to_string'
(cast object',
Types.int_type, env, ref_depth, depth-1) ::
(case list of
[] => []
| list => ", " :: list))
([],element_list)))
end
else
error_notification
(object,"<Bytearray not a bytearray>")
end
else
error_notification(object,"<Byte array not a ref>")
else if Types.tyname_eq (tyname,Types.floatarray_tyname)
then
if ref_depth <= 0 then
generate_underbar("ref depth")
else
if MLWorks.Internal.Value.primary object
= Tags.REFPTR then
let
val (secondary,length) =
MLWorks.Internal.Value.header object
val length = length div 8
in
if secondary = Tags.BYTEARRAY then
let
val element_list =
floatarray_map
(cast object,
max_len (maximum_seq_size, length))
(fn x => x)
val tail =
if length > maximum_seq_size andalso
maximum_seq_size > 0 then
list_ellipsis
else
"]"
in
concat
("#F[" ::
rev (tail ::
Lists.reducel
(fn (list, object') =>
value_to_string'
(cast object',
Types.real_type, env, ref_depth, depth-1) ::
(case list of
[] => []
| list => ", " :: list))
([],element_list)))
end
else
error_notification
(object,"<Floatarray not a floatarray>")
end
else
error_notification(object,"<Float array not a ref>")
else if Types.tyname_eq(tyname, Types.vector_tyname) then
if ref_depth <= 0 then
generate_underbar("ref depth")
else
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
in
if secondary = Tags.RECORD then
let
val element_list =
vector_map
(object, max_len(maximum_seq_size, length))
(fn x => x)
val tail =
if length > maximum_seq_size then
list_ellipsis
else
"]"
in
concat
("#V[" ::
rev (tail ::
Lists.reducel
(fn (list, object') =>
value_to_string' (object', ty, env, ref_depth, depth-1) ::
(case list of
[] => []
| list => ", " :: list))
([],element_list)))
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
fun do_it (ty_name,val_map as Datatypes.VE(_,constructor_map),is_abs) =
if (is_abs andalso not debugger_print)
orelse NewMap.is_empty constructor_map then
generate_underbar("empty constructor map:"^ty_name)
else
let
val (domain,range) =
Lists.unzip(NewMap.to_list_ordered constructor_map)
val is_a_single_constructor = (length domain = 1)
fun test_scheme (Datatypes.SCHEME(_,
(Datatypes.FUNTYPE _,_))) = true
| test_scheme (Datatypes.UNBOUND_SCHEME(
Datatypes.FUNTYPE _,_)) = true
| test_scheme _ = false
val is_a_single_vcc =
case range of
[x] => test_scheme x
| _ => false
in
if is_a_single_constructor andalso not is_a_single_vcc then
case domain of
[Ident.CON name] => Ident.Symbol.symbol_name name
| _ => error_notification(object,
"(single non-vcc problem with name)")
else
if is_a_single_vcc then
case domain of
[name' as Ident.CON name] =>
let
val name = Ident.Symbol.symbol_name name
val scheme = Valenv.lookup(name',val_map)
val (ty,env') =
(case scheme of
Datatypes.SCHEME(_,(ty',_)) =>
(ty',ENTRY(tys,env))
| Datatypes.UNBOUND_SCHEME(ty',_) => (ty',env)
| Datatypes.OVERLOADED_SCHEME _ =>
(Datatypes.NULLTYPE,env))
val arg_type = get_arg_type ty
val brackets = needs_brackets arg_type
in
concat [name,
(if brackets then "(" else " "),
value_to_string'(object,
arg_type,
env',ref_depth,depth-1),
(if brackets then ")" else "")]
end
| _ => error_notification(object,"(Problems in vcc code)")
else
if primary = Tags.INTEGER0 orelse primary = Tags.INTEGER1 then
(case Lists.nth(cast(object),domain) of
name' as Ident.CON name =>
(if test_scheme(NewMap.apply'(constructor_map, name')) then
error_notification(object,"(should carry value)")
else
Ident.Symbol.symbol_name name)
| _ => error_notification(object,"(Not a CONS in a datatype)"))
handle Lists.Nth =>
(debug ("Yargh: " ^
Int.toString (cast object));
generate_underbar("lists.nth 1"))
else
if primary = Tags.PAIRPTR then
let
val (code, packet) =
(MLWorks.Internal.Value.sub (object,0),
MLWorks.Internal.Value.sub (object,1))
val code_primary =
MLWorks.Internal.Value.primary code
in
if code_primary = Tags.INTEGER0 orelse
code_primary = Tags.INTEGER1 then
let
val name' = Lists.nth(cast(code),domain)
val name =
case name' of
Ident.CON x => Ident.Symbol.symbol_name x
| _ => "CantFigureNameOut"
val scheme = Valenv.lookup(name',val_map)
val (ty,env') =
(case scheme of
Datatypes.SCHEME(_,(ty',_)) =>
(ty',ENTRY(tys,env))
| Datatypes.UNBOUND_SCHEME(ty',_) => (ty',env)
| Datatypes.OVERLOADED_SCHEME _ =>
(Datatypes.NULLTYPE,env))
val arg_type = get_arg_type ty
val brackets = needs_brackets arg_type
in
if test_scheme scheme then
concat [name,
(if brackets then "(" else " "),
value_to_string'(packet,
arg_type,
env',ref_depth,depth-1),
if brackets then ")" else ""]
else
error_notification(object,"(should not carry value)")
end
handle Lists.Nth => generate_underbar("lists.nth 2")
else
error_notification
(object,
"(Constructor tag not integer in expected datatype case)")
end
else
error_notification
(object,
"(Not INTEGER or PAIR in expected datatype case)")
end
in
case tyname of
Datatypes.TYNAME (_,name,_,_,ref valenv,_,
ref is_abs,_,_) =>
do_it (name,valenv,is_abs)
| Datatypes.METATYNAME (_,name,_,_,ref valenv,
ref is_abs) =>
do_it (name,valenv,is_abs)
end
end
| value_to_string'(object,Datatypes.DEBRUIJN(level,_,_,_),env,ref_depth,depth) =
(case env of
ENTRY(env,old_env) =>
let
exception DeBruijn_In_ValuePrinter
fun find_it (level) =
let
fun find_it'(0,h::t) = h
| find_it'(n,h::t) = find_it'(n-1,t)
| find_it'(_,[]) = raise DeBruijn_In_ValuePrinter
in
find_it'(level,env)
end
val ty = find_it(level)
in
value_to_string'(object,ty,old_env,ref_depth,depth)
end
| _ => generate_underbar("Unbound debruijn"))
| value_to_string'(_,Datatypes.NULLTYPE,env,ref_depth,depth) = generate_underbar("nulltype")
in
value_to_string'(object,ty,env,maximum_ref_depth,maximum_depth)
end
in
value_to_string(object,ty,EMPTY)
end
fun function_name f =
let val object = cast f
in MLWorks.String.ml_string (code_name (select 0 object), ~1)
handle Value _ => Crash.impossible "Error in function_name"
end
end
;
