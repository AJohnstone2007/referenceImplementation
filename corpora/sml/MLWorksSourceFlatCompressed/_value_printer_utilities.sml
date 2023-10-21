require "../typechecker/basis";
require "../typechecker/types";
require "value_printer_utilities";
functor ValuePrinterUtilities(
structure Basis : BASIS
structure Types : TYPES
sharing Types.Datatypes = Basis.BasisTypes.Datatypes
) : VALUEPRINTERUTILITIES =
struct
structure BasisTypes = Basis.BasisTypes
structure Datatypes = Types.Datatypes
structure Ident = Datatypes.Ident
structure Symbol = Ident.Symbol
exception FailedToFind
fun find_tyname (basis,argument) =
let
val max_subscript = size argument - 1
fun upto_dot_or_end (from,to) =
if to >= max_subscript
then (substring (argument,from,to-from+1),to)
else if MLWorks.String.ordof (argument, to+1) = ord #"." then
(substring (argument,from,to-from+1),to+2)
else upto_dot_or_end(from,to+1)
fun part_list (from,acc) =
if from >= max_subscript
then acc
else
let
val (part,next) = upto_dot_or_end(from,from)
in
part_list(next,part::acc)
end
fun make_path ([],acc) = acc
| make_path (h::t,acc) =
make_path(t,Ident.PATH(Symbol.find_symbol h,acc))
in
case part_list (0,[]) of
[] => raise FailedToFind
| (final::rest) =>
let
val result =
Basis.lookup_longtycon(Ident.LONGTYCON(make_path(rest,Ident.NOPATH),Ident.TYCON (Symbol.find_symbol final)),
Basis.basis_to_context basis)
val Datatypes.TYSTR(function,_) = result
val arity = Types.arity function
fun generate_args 0 = []
| generate_args n = Datatypes.NULLTYPE :: generate_args (n-1)
in
case Types.apply(function,generate_args arity) of
Datatypes.CONSTYPE(_,tyname) => tyname
| _ => raise FailedToFind
end
handle Basis.LookupTyCon _ => raise FailedToFind
| Basis.LookupStrId _ => raise FailedToFind
end
end
;
