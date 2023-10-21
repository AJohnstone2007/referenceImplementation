require "fi_abs_syntax";
signature FI_INT_ABS_SYN =
sig
structure FIAbsSyntax : FI_ABS_SYNTAX
datatype type_info =
NO_TYPE_INFO
| AS_POINTER of type_info
| AS_ARRAY of
{dimensions: FIAbsSyntax.expression option, elemTypeInfo: type_info}
| AS_FUNCTION of
{returnTypeInfo : type_info,
params: {name : string option, paramType : FIAbsSyntax.types} list }
val insertTypeInfo : type_info * type_info -> type_info
val makeType : FIAbsSyntax.types * type_info -> FIAbsSyntax.types
val makeSignedType : FIAbsSyntax.types -> FIAbsSyntax.types
val makeUnsignedType : FIAbsSyntax.types -> FIAbsSyntax.types
val makeLongType : FIAbsSyntax.types -> FIAbsSyntax.types
val makeShortType : FIAbsSyntax.types -> FIAbsSyntax.types
val makeField :
FIAbsSyntax.types * {name :string, typeInfo :type_info}
-> FIAbsSyntax.field
val makeDeclaration :
FIAbsSyntax.types *
{name : string, typeInfo : type_info} ->
FIAbsSyntax.declaration
val makeTypedef :
FIAbsSyntax.types *
{name : string, typeInfo : type_info } ->
FIAbsSyntax.declaration
val makeConst :
FIAbsSyntax.types *
{name : string, typeInfo : type_info } *
FIAbsSyntax.expression ->
FIAbsSyntax.declaration
end
;
