require "^.basis.__text_io";
require "../utils/lists";
require "../interpreter/incremental";
require "../typechecker/basis";
require "../typechecker/types";
require "../interpreter/inspector_values";
require "../main/user_options";
require "../interpreter/shell_types";
require "../debugger/value_printer";
require "inspector";
functor Inspector (
structure Lists : LISTS
structure Incremental : INCREMENTAL
structure UserOptions : USER_OPTIONS
structure Basis : BASIS
structure Types : TYPES
structure InspectorValues : INSPECTOR_VALUES
structure ValuePrinter : VALUE_PRINTER
structure ShellTypes : SHELL_TYPES
sharing Incremental.InterMake.Inter_EnvTypes.EnvironTypes.LambdaTypes.Ident =
Basis.BasisTypes.Datatypes.Ident
sharing Basis.BasisTypes.Datatypes = Types.Datatypes =
Incremental.Datatypes
sharing ValuePrinter.Options = UserOptions.Options = Types.Options =
Incremental.InterMake.Compiler.Options = ShellTypes.Options
sharing type UserOptions.user_tool_options = ShellTypes.user_options
sharing type ValuePrinter.DebugInformation =
Incremental.InterMake.Compiler.DebugInformation
sharing type Basis.BasisTypes.Datatypes.Type = InspectorValues.Type =
ValuePrinter.Type
sharing type Types.Options.options = InspectorValues.options
sharing type Basis.BasisTypes.Basis = Incremental.InterMake.Compiler.TypeBasis
sharing type ShellTypes.Context = Incremental.Context
) : INSPECTOR =
struct
structure Incremental = Incremental
structure Ident = Basis.BasisTypes.Datatypes.Ident
structure Symbol = Ident.Symbol
structure Inter_EnvTypes = Incremental.InterMake.Inter_EnvTypes
structure Options = Inter_EnvTypes.Options
type Context = Incremental.Context
type ShellData = ShellTypes.ShellData
type Type = Types.Datatypes.Type
fun get_name () = "it"
fun printname (Ident.VAR s) = Symbol.symbol_name s
| printname (Ident.CON s) = Symbol.symbol_name s
| printname (Ident.EXCON s) = Symbol.symbol_name s
| printname (Ident.TYCON' s) = Symbol.symbol_name s
fun lookup_name (name,context) =
let
val tycontext = Basis.basis_to_context (Incremental.type_basis context)
val valid = Ident.VAR(Symbol.find_symbol name)
val valtype =
#1(Basis.lookup_val (Ident.LONGVALID (Ident.NOPATH, valid),
tycontext,
Ident.Location.FILE "Inspector",
false))
val mlval = Inter_EnvTypes.lookup_val(valid,Incremental.inter_env context)
in
(mlval,valtype)
end
val show_them =
Lists.iterate
(fn (tag,string) =>
print(concat [tag,": ",string,"\n"]))
fun getreps (item as (value,ty),subitems,shelldata) =
let
val options = ShellTypes.get_current_options shelldata
val Options.OPTIONS{print_options,...} = options
val debug_info = Incremental.debug_info (ShellTypes.get_current_context shelldata)
fun print_value (object,ty) =
ValuePrinter.stringify_value false (print_options,
object,
ty,
debug_info)
val print_type = Types.print_type options
in
((print_value item,print_type ty),
map (fn (x,y) => (x,print_value y)) subitems)
end
fun lookup (tag,list) =
Lists.assoc(tag,list)
fun inspect (arg as (valtype,mlval),stack,shelldata) =
let
val options = ShellTypes.get_current_options shelldata
val subobjects = InspectorValues.get_inspector_values options false arg
val ((valuestring,typestring),items) = getreps (arg,subobjects,shelldata)
fun show_help () =
print (concat (["Commands:\n",
" p        - inspect the object containing this one\n",
" q        - quit the inspector\n",
" <field name> - inspect the named field\n",
" ?        - print this message\n"]))
fun doit () =
let
val _ = print("Value: " ^ valuestring ^ "\n")
val _ = print("Type: " ^ typestring ^ "\n")
val _ = show_them items;
val _ = print "Inspector> "
val _ = TextIO.flushOut TextIO.stdOut;
in
case rev (explode (TextIO.inputLine(TextIO.stdIn))) of
[] => ()
| (_::tagl) =>
let
val tag = implode (rev tagl)
in
case tag of
"q" => ()
| "?" => (show_help (); doit())
| "p" => (case stack of [] => doit () | (arg'::stack') => inspect(arg',stack',shelldata))
| _ => inspect (lookup (tag,subobjects),arg::stack,shelldata)
handle Lists.Assoc => (print("Enter ? for help\n");
doit())
end
end
in
doit ()
end
fun inspect_value (typed_object,shelldata) =
(print "Entering TTY inspector - enter ? for help\n";
inspect (typed_object,[],shelldata))
fun inspect_it (shelldata) =
let
val name = get_name ()
val typed_object = lookup_name (name,ShellTypes.get_current_context shelldata)
in
print "Entering TTY inspector - enter ? for help\n";
inspect (typed_object,[],shelldata)
end
handle Basis.LookupValId valid =>
print ("Error: variable " ^ printname valid ^ " not defined\n")
end
;
