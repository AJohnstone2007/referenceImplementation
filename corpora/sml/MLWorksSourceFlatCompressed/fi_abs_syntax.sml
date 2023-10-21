signature FI_ABS_SYNTAX =
sig
datatype field = FIELD of {name : string, fieldType : types}
and e_field = E_FIELD of {name : string, value : expression option }
and aggregate_type = STRUCT | UNION
and types =
NAME of string
| ILLEGAL_TYPE of string
| SIGNED | INT | CHAR | SHORT | LONG
| UNSIGNED| UNS_INT | UNS_CHAR | UNS_SHORT | UNS_LONG
| FLOAT | DOUBLE | VOID
| MLW_INT | MLW_WORD | MLW_INT32 | MLW_CHAR | MLW_WORD8
| MLW_INT32_V | MLW_INT32_A | MLW_WORD32_V | MLW_WORD32_A
| MLW_INT16_V | MLW_INT16_A | MLW_WORD16_V | MLW_WORD16_A
| MLW_STRING | MLW_CHAR_A | MLW_WORD8_V | MLW_WORD8_A
| MLW_FLOAT_V | MLW_FLOAT_A | MLW_DOUBLE_V | MLW_DOUBLE_A
| POINTER of types
| ENUM of {name : string option, fields : e_field list}
| AGGREGATE of
{aggType : aggregate_type, name : string option,
fields : field list}
| ARRAY of {elemType: types, dimensions: expression option}
| FUNCTION of
{returnType : types,
params : {name: string option, paramType: types} list}
| FLEX
and expression =
HEX_CONSTANT of string
| OCT_CONSTANT of string
| DEC_CONSTANT of string
| STRING_CONSTANT of string
| CHAR_CONSTANT of string
| FLOAT_CONSTANT of string
| IDENTIFIER_EXP of string
| UNARY_EXP of unary_op * expression
| BINARY_EXP of expression * binary_op * expression
| CAST_EXP of types * expression
| LIST_EXP of expression list
and unary_op =
BIN_NEG
and binary_op =
MULTIPLY | DIVIDE | MODULUS | PLUS | MINUS
| L_SHIFT | R_SHIFT | BIT_AND | BIT_EOR | BIT_OR
datatype declaration =
TYPE_DEF of {oldType: types, newName:string}
| TYPE_DECL of types
| ID_DECL of {name: string, idType : types}
| CONSTANT_DECL of {name: string, value: expression, valueType :types }
datatype declaration_list = DECL_LIST of declaration list
val stringDeclList : declaration_list -> string
end
;
