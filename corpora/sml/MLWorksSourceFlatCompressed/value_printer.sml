require "../main/options";
signature VALUE_PRINTER =
sig
structure Options : OPTIONS
type TypeBasis
type Type
type DebugInformation
val stringify_value : bool ->
(Options.print_options *
MLWorks.Internal.Value.T *
Type *
DebugInformation)
-> string
val function_name : ('a -> 'b) -> string
val string_abbreviation : string ref
end
;
