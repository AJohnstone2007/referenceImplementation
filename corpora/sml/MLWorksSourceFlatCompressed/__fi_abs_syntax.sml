require "fi_abs_syntax";
require "$.basis.__int";
require "$.basis.__real";
require "$.basis.__list";
structure FIAbsSyntax : FI_ABS_SYNTAX =
struct
datatype field = FIELD of {name : string, fieldType : types}
and e_field = E_FIELD of {name : string, value : expression option }
and aggregate_type = STRUCT | UNION
and types =
NAME of string
| SIGNED | INT | CHAR | SHORT | LONG
| UNSIGNED | UNS_INT | UNS_CHAR | UNS_SHORT | UNS_LONG
| FLOAT | DOUBLE | VOID
| ILLEGAL_TYPE of string
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
| CONSTANT_DECL of {name: string, value: expression, valueType : types}
datatype declaration_list = DECL_LIST of declaration list
fun stringDeclList (DECL_LIST l) =
let
fun stringUnOp BIN_NEG = "!"
and stringBinOp MULTIPLY = "*"
| stringBinOp DIVIDE = "/"
| stringBinOp MODULUS = "%"
| stringBinOp PLUS = "+"
| stringBinOp MINUS = "-"
| stringBinOp L_SHIFT = "<<"
| stringBinOp R_SHIFT = ">>"
| stringBinOp BIT_AND = "&"
| stringBinOp BIT_EOR = "^"
| stringBinOp BIT_OR = "|"
and stringExp (HEX_CONSTANT s) = s
| stringExp (OCT_CONSTANT s) = s
| stringExp (DEC_CONSTANT s) = s
| stringExp (STRING_CONSTANT s) = s
| stringExp (CHAR_CONSTANT s) = s
| stringExp (FLOAT_CONSTANT s) = s
| stringExp (IDENTIFIER_EXP s) = s
| stringExp (UNARY_EXP(a, e)) =
"("^(stringUnOp a)^(stringExp e)^")"
| stringExp (BINARY_EXP(e1, a, e2)) =
"("^(stringExp e1)^" "^(stringBinOp a)^" "^(stringExp e2)^")"
| stringExp (CAST_EXP(t, e)) =
let val (pre, post) = stringType t in
"( ("^pre^post^") "^(stringExp e)^")" end
| stringExp (LIST_EXP l) =
"{ "^(List.foldl (fn(e,s) => s^(stringExp e)^", ") "" l)^" }"
and stringEFields (_,[]) = ""
| stringEFields (c, ((E_FIELD{name, value})::l)) =
((if c then ", " else "")^
name^
(case value of NONE => ""
| SOME v => " = " ^ stringExp v)^
(stringEFields (true, l)))
and stringAggType STRUCT = "struct "
| stringAggType UNION = "union "
and stringFields [] = ""
| stringFields ((FIELD{name, fieldType})::l) =
("\n  "^
(#1(stringType fieldType))^" "^
name^(#2(stringType fieldType))^";"^
(stringFields l ))
and stringAgg {aggType, name, fields} =
((stringAggType aggType)^
(getOpt(name, ""))^
(case fields of
[] => ""
| l => ("{"^(stringFields l)^"\n}")))
and stringParam {name, paramType} =
let
val n = getOpt (name, " /* no param name */")
val (pre, post) = stringType paramType
in pre^n^post end
and stringParams [] = ""
| stringParams [p] = stringParam p
| stringParams (h::t) =
(stringParam h)^", "^(stringParams t)
and stringType (NAME t) = (t^" ", "")
| stringType (ILLEGAL_TYPE s) = ("Illegal type: "^s, "")
| stringType (INT) = ("int ", "")
| stringType (UNS_INT) = ("unsigned int ", "")
| stringType (CHAR) = ("char ", "")
| stringType (UNS_CHAR) = ("unsigned char ", "")
| stringType (SHORT) = ("short ", "")
| stringType (UNS_SHORT) = ("unsigned short ", "")
| stringType (LONG) = ("long ", "")
| stringType (UNS_LONG) = ("unsigned long ", "")
| stringType (FLOAT) = ("float ", "")
| stringType (DOUBLE) = ("double ", "")
| stringType (SIGNED) = ("signed ", "")
| stringType (UNSIGNED) = ("unsigned ", "")
| stringType (VOID) = ("void ", "")
| stringType MLW_INT = ("mlw_int ", "")
| stringType MLW_WORD = ("mlw_word ", "")
| stringType MLW_INT32 = ("mlw_int32 ", "")
| stringType MLW_CHAR = ("mlw_char ", "")
| stringType MLW_WORD8 = ("mlw_word8 ", "")
| stringType MLW_INT32_V = ("mlw_int32_vector ", "")
| stringType MLW_INT32_A = ("mlw_int32_array ", "")
| stringType MLW_WORD32_V = ("mlw_word32_vector ", "")
| stringType MLW_WORD32_A = ("mlw_word32_array ", "")
| stringType MLW_INT16_V = ("mlw_int16_vector ", "")
| stringType MLW_INT16_A = ("mlw_int16_array ", "")
| stringType MLW_WORD16_V = ("mlw_word16_vector ", "")
| stringType MLW_WORD16_A = ("mlw_word_16_array ", "")
| stringType MLW_STRING = ("mlw_string ", "")
| stringType MLW_CHAR_A = ("mlw_char_array ", "")
| stringType MLW_WORD8_V = ("mlw_word8_vector ", "")
| stringType MLW_WORD8_A = ("mlw_word8_array ", "")
| stringType MLW_FLOAT_V = ("mlw_float_vector ", "")
| stringType MLW_FLOAT_A = ("mlw_float_array ", "")
| stringType MLW_DOUBLE_V = ("mlw_double_vector ", "")
| stringType MLW_DOUBLE_A = ("mlw_double array ", "")
| stringType (POINTER t) =
let
val (pre, post) = stringType t
val (pre', post') =
if post = "" then ( pre^"*" , "" )
else ( pre^"(*" , ")"^post)
in
(pre', post')
end
| stringType (ARRAY{dimensions, elemType})=
let val dim = case dimensions of NONE => ""
| SOME e => stringExp e in
(#1(stringType elemType),
(#2(stringType elemType)) ^ "["^dim^"]" ) end
| stringType (ENUM {name,fields}) =
("enum "^
(getOpt (name, ""))^
" { "^
(stringEFields (false, fields))^
" }", "")
| stringType (AGGREGATE a) = (stringAgg a, "")
| stringType (FUNCTION {returnType, params}) =
let
val (rTS, rtSPost) = stringType returnType
val pS = stringParams params
val rtSPost' =
if rtSPost="" then ""
else "Error: function return type cannot have postfix"
in
(rTS, rtSPost'^"("^pS^")")
end
| stringType FLEX = ("...", "")
and stringDecl (TYPE_DEF {oldType, newName}) =
( "typedef " ^
(#1(stringType oldType)) ^
newName ^ (#2(stringType oldType)) ^ ";\n" )
| stringDecl (TYPE_DECL e) =
(#1(stringType e))^(#2(stringType e))^" ;\n"
| stringDecl (CONSTANT_DECL {name, valueType, value}) =
"const " ^ (#1(stringType valueType)) ^" "^
name ^ (#2(stringType valueType)) ^
" = " ^ (stringExp value) ^";\n"
| stringDecl (ID_DECL {name, idType}) =
let
val (pre, post) = stringType idType
in
pre ^ " " ^ name ^ post ^ ";\n"
end
and stringDeclList' [] = ""
| stringDeclList' (h::t) =
( (stringDecl h) ^ "\n" ^ (stringDeclList' t) )
in
stringDeclList' l
end
end
;
