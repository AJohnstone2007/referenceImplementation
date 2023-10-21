require "^.basis.__int";
require "../utils/crash";
require "../utils/lists";
require "../utils/map";
require "../utils/diagnostic";
require "../basics/ident";
require "../main/pervasives";
functor Pervasives (
structure Lists : LISTS
structure Map : MAP
structure Ident : IDENT
structure Diagnostic : DIAGNOSTIC
structure Crash : CRASH
) : PERVASIVES =
struct
structure Symbol = Ident.Symbol
structure Diagnostic = Diagnostic
datatype pervasive =
REF |
EXORD |
EXCHR |
EXDIV |
EXSQRT |
EXEXP |
EXLN |
EXIO |
EXMATCH |
EXBIND |
EXINTERRUPT |
EXOVERFLOW |
EXRANGE |
MAP |
UMAP |
REV |
NOT |
ABS |
FLOOR |
REAL |
SQRT |
SIN |
COS |
ARCTAN |
EXP |
LN |
SIZE |
CHR |
ORD |
CHARCHR |
CHARORD |
ORDOF |
EXPLODE |
IMPLODE |
DEREF |
FDIV |
DIV |
MOD |
PLUS |
STAR |
MINUS |
HAT |
AT |
NE |
LESS |
GREATER |
LESSEQ |
GREATEREQ |
BECOMES |
O |
UMINUS |
EQ |
EQFUN |
LOAD_STRING |
REALPLUS |
INTPLUS |
UNSAFEINTPLUS |
UNSAFEINTMINUS |
REALSTAR |
INTSTAR |
REALMINUS |
INTMINUS |
REALUMINUS |
INTUMINUS |
INTMOD |
INTDIV |
INTLESS |
REALLESS |
INTGREATER |
REALGREATER |
INTLESSEQ |
REALLESSEQ |
INTGREATEREQ |
REALGREATEREQ |
INTEQ |
INTNE |
REALEQ |
REALNE |
STRINGEQ |
STRINGNE |
STRINGLT |
STRINGLE |
STRINGGT |
STRINGGE |
CHAREQ |
CHARNE |
CHARLT |
CHARLE |
CHARGT |
CHARGE |
INTABS |
REALABS |
CALL_C |
ARRAY_FN |
LENGTH |
SUB |
UNSAFE_SUB |
UPDATE |
UNSAFE_UPDATE |
BYTEARRAY |
BYTEARRAY_LENGTH |
BYTEARRAY_SUB |
BYTEARRAY_UNSAFE_SUB |
BYTEARRAY_UPDATE |
BYTEARRAY_UNSAFE_UPDATE |
FLOATARRAY |
FLOATARRAY_LENGTH |
FLOATARRAY_SUB |
FLOATARRAY_UNSAFE_SUB |
FLOATARRAY_UPDATE |
FLOATARRAY_UNSAFE_UPDATE |
VECTOR |
VECTOR_LENGTH |
VECTOR_SUB |
EXSIZE |
EXSUBSCRIPT |
ANDB |
LSHIFT |
NOTB |
ORB |
RSHIFT |
ARSHIFT |
XORB |
WORDEQ |
WORDNE |
WORDLT |
WORDLE |
WORDGT |
WORDGE |
WORDPLUS |
WORDMINUS |
WORDSTAR |
WORDDIV |
WORDMOD |
WORDORB |
WORDXORB |
WORDANDB |
WORDNOTB |
WORDLSHIFT |
WORDRSHIFT |
WORDARSHIFT |
INT32PLUS |
INT32STAR |
INT32MINUS |
INT32UMINUS |
INT32ABS |
INT32MOD |
INT32DIV |
INT32LESS |
INT32GREATER |
INT32LESSEQ |
INT32GREATEREQ |
INT32EQ |
INT32NE |
WORD32EQ |
WORD32NE |
WORD32LT |
WORD32LE |
WORD32GT |
WORD32GE |
WORD32PLUS |
WORD32MINUS |
WORD32STAR |
WORD32DIV |
WORD32MOD |
WORD32ORB |
WORD32XORB |
WORD32ANDB |
WORD32NOTB |
WORD32LSHIFT |
WORD32RSHIFT |
WORD32ARSHIFT |
CAST |
ALLOC_STRING |
ALLOC_VECTOR |
ALLOC_PAIR |
RECORD_UNSAFE_SUB |
RECORD_UNSAFE_UPDATE |
STRING_UNSAFE_SUB |
STRING_UNSAFE_UPDATE |
IDENT_FN |
ML_OFFSET |
ENTUPLE |
ML_CALL |
ML_REQUIRE |
LOAD_VAR |
LOAD_EXN |
LOAD_STRUCT |
LOAD_FUNCT |
GET_IMPLICIT
fun print_pervasive REF = "ref"
| print_pervasive EXORD = "<name of Ord>"
| print_pervasive EXCHR = "<name of Chr>"
| print_pervasive EXDIV = "<name of Div>"
| print_pervasive EXSQRT = "<name of Sqrt>"
| print_pervasive EXEXP = "<name of Exp>"
| print_pervasive EXLN = "<name of Ln>"
| print_pervasive EXIO = "<name of Io>"
| print_pervasive EXMATCH = "<name of Match>"
| print_pervasive EXBIND = "<name of Bind>"
| print_pervasive EXINTERRUPT = "<name of Interrupt>"
| print_pervasive EXOVERFLOW = "<name of Overflow>"
| print_pervasive EXRANGE = "<name of Range>"
| print_pervasive MAP = "map"
| print_pervasive UMAP = "umap"
| print_pervasive REV = "rev"
| print_pervasive NOT = "not"
| print_pervasive ABS = "abs"
| print_pervasive FLOOR = "floor"
| print_pervasive REAL = "real"
| print_pervasive SQRT = "sqrt"
| print_pervasive SIN = "sin"
| print_pervasive COS = "cos"
| print_pervasive ARCTAN = "arctan"
| print_pervasive EXP = "exp"
| print_pervasive LN = "ln"
| print_pervasive SIZE = "size"
| print_pervasive CHR = "chr"
| print_pervasive ORD = "ord"
| print_pervasive CHARCHR = "char_chr"
| print_pervasive CHARORD = "char_ord"
| print_pervasive ORDOF = "ordof"
| print_pervasive EXPLODE = "explode"
| print_pervasive IMPLODE = "implode"
| print_pervasive DEREF = "!"
| print_pervasive FDIV = "/"
| print_pervasive DIV = "div"
| print_pervasive MOD = "mod"
| print_pervasive PLUS = "+"
| print_pervasive STAR = "*"
| print_pervasive MINUS = "-"
| print_pervasive HAT = "^"
| print_pervasive AT = "@"
| print_pervasive NE = "<>"
| print_pervasive LESS = "<"
| print_pervasive GREATER = ">"
| print_pervasive LESSEQ = "<="
| print_pervasive GREATEREQ = ">="
| print_pervasive BECOMES = "becomes"
| print_pervasive O = "o"
| print_pervasive UMINUS = "~"
| print_pervasive EQ = "inline_equality"
| print_pervasive EQFUN = "external_equality"
| print_pervasive LOAD_STRING = "load_string"
| print_pervasive REALPLUS = "_real+"
| print_pervasive INTPLUS = "_int+"
| print_pervasive UNSAFEINTPLUS = "_unsafeint+"
| print_pervasive UNSAFEINTMINUS = "_unsafeint-"
| print_pervasive REALSTAR = "_real*"
| print_pervasive INTSTAR = "_int*"
| print_pervasive REALMINUS = "_real-"
| print_pervasive INTMINUS = "_int-"
| print_pervasive REALUMINUS = "_real~"
| print_pervasive INTUMINUS = "_int~"
| print_pervasive INTDIV = "_intdiv"
| print_pervasive INTMOD = "_intmod"
| print_pervasive INTLESS = "_int<"
| print_pervasive REALLESS = "_real<"
| print_pervasive INTGREATER = "_int>"
| print_pervasive REALGREATER = "_real>"
| print_pervasive INTLESSEQ = "_int<="
| print_pervasive REALLESSEQ = "_real<="
| print_pervasive INTGREATEREQ = "_int>="
| print_pervasive REALGREATEREQ = "_real>="
| print_pervasive INTEQ = "_int="
| print_pervasive INTNE = "_int<>"
| print_pervasive REALEQ = "_real="
| print_pervasive REALNE = "_real<>"
| print_pervasive STRINGEQ = "_string="
| print_pervasive STRINGNE = "_string<>"
| print_pervasive STRINGLT = "_string<"
| print_pervasive STRINGLE = "_string<="
| print_pervasive STRINGGT = "_string>"
| print_pervasive STRINGGE = "_string>="
| print_pervasive CHAREQ = "_char="
| print_pervasive CHARNE = "_char<>"
| print_pervasive CHARLT = "_char<"
| print_pervasive CHARLE = "_char<="
| print_pervasive CHARGT = "_char>"
| print_pervasive CHARGE = "_char>="
| print_pervasive INTABS = "_intabs"
| print_pervasive REALABS = "realabs"
| print_pervasive CALL_C = "call_c"
| print_pervasive ARRAY_FN = "array"
| print_pervasive LENGTH = "length"
| print_pervasive SUB = "sub"
| print_pervasive UPDATE = "update"
| print_pervasive UNSAFE_SUB = "unsafe_sub"
| print_pervasive UNSAFE_UPDATE = "unsafe_update"
| print_pervasive BYTEARRAY = "bytearray"
| print_pervasive BYTEARRAY_LENGTH = "bytearray_length"
| print_pervasive BYTEARRAY_SUB = "bytearray_sub"
| print_pervasive BYTEARRAY_UPDATE = "bytearray_update"
| print_pervasive BYTEARRAY_UNSAFE_SUB = "bytearray_unsafe_sub"
| print_pervasive BYTEARRAY_UNSAFE_UPDATE = "bytearray_unsafe_update"
| print_pervasive FLOATARRAY = "floatarray"
| print_pervasive FLOATARRAY_LENGTH = "floatearray_length"
| print_pervasive FLOATARRAY_SUB = "floatarray_sub"
| print_pervasive FLOATARRAY_UPDATE = "floatarray_update"
| print_pervasive FLOATARRAY_UNSAFE_SUB = "floatarray_unsafe_sub"
| print_pervasive FLOATARRAY_UNSAFE_UPDATE = "floatarray_unsafe_update"
| print_pervasive VECTOR = "vector"
| print_pervasive VECTOR_LENGTH = "vector_length"
| print_pervasive VECTOR_SUB = "vector_sub"
| print_pervasive EXSIZE = "<name of Size>"
| print_pervasive EXSUBSCRIPT = "<name of Subscript>"
| print_pervasive ANDB = "andb"
| print_pervasive LSHIFT = "lshift"
| print_pervasive NOTB = "notb"
| print_pervasive ORB = "orb"
| print_pervasive RSHIFT = "rshift"
| print_pervasive ARSHIFT = "arshift"
| print_pervasive XORB = "xorb"
| print_pervasive CAST = "cast"
| print_pervasive ALLOC_STRING = "alloc_string"
| print_pervasive ALLOC_VECTOR = "alloc_vector"
| print_pervasive ALLOC_PAIR = "alloc_pair"
| print_pervasive RECORD_UNSAFE_SUB = "record_unsafe_sub"
| print_pervasive RECORD_UNSAFE_UPDATE = "record_unsafe_update"
| print_pervasive STRING_UNSAFE_SUB = "string_unsafe_sub"
| print_pervasive STRING_UNSAFE_UPDATE = "string_unsafe_update"
| print_pervasive IDENT_FN = "make_ml_value"
| print_pervasive ML_OFFSET = "ml_value_from_offset"
| print_pervasive ENTUPLE = "make_ml_value_tuple"
| print_pervasive ML_CALL = "call_ml_value"
| print_pervasive ML_REQUIRE = "ml_require"
| print_pervasive LOAD_VAR = "load_var"
| print_pervasive LOAD_EXN = "load_exn"
| print_pervasive LOAD_STRUCT = "load_struct"
| print_pervasive LOAD_FUNCT = "load_funct"
| print_pervasive GET_IMPLICIT = "get_implicit"
| print_pervasive WORDEQ = "_word="
| print_pervasive WORDNE = "_word<>"
| print_pervasive WORDLT = "_word<"
| print_pervasive WORDLE = "_word<="
| print_pervasive WORDGT = "_word>"
| print_pervasive WORDGE = "_word>="
| print_pervasive WORDPLUS = "_word+"
| print_pervasive WORDMINUS = "_word-"
| print_pervasive WORDSTAR = "_word*"
| print_pervasive WORDDIV = "_worddiv"
| print_pervasive WORDMOD = "_wordmod"
| print_pervasive WORDORB = "word_orb"
| print_pervasive WORDXORB = "word_xorb"
| print_pervasive WORDANDB = "word_andb"
| print_pervasive WORDNOTB = "word_notb"
| print_pervasive WORDLSHIFT = "word_lshift"
| print_pervasive WORDRSHIFT = "word_rshift"
| print_pervasive WORDARSHIFT = "word_arshift"
| print_pervasive INT32PLUS = "_int32+"
| print_pervasive INT32STAR = "_int32*"
| print_pervasive INT32MINUS = "_int32-"
| print_pervasive INT32UMINUS = "_int32~"
| print_pervasive INT32ABS = "_int32abs"
| print_pervasive INT32MOD = "_int32mod"
| print_pervasive INT32DIV = "_int32div"
| print_pervasive INT32LESS = "_int32<"
| print_pervasive INT32GREATER = "_int32>"
| print_pervasive INT32LESSEQ = "_int32<="
| print_pervasive INT32GREATEREQ = "_int32>="
| print_pervasive INT32EQ = "_int32="
| print_pervasive INT32NE = "_int32<>"
| print_pervasive WORD32EQ = "_word32="
| print_pervasive WORD32NE = "_word32<>"
| print_pervasive WORD32LT = "_word32<"
| print_pervasive WORD32LE = "_word32<="
| print_pervasive WORD32GT = "_word32>"
| print_pervasive WORD32GE = "_word32>="
| print_pervasive WORD32PLUS = "_word32+"
| print_pervasive WORD32MINUS = "_word32-"
| print_pervasive WORD32STAR = "_word32*"
| print_pervasive WORD32DIV = "_word32div"
| print_pervasive WORD32MOD = "_word32mod"
| print_pervasive WORD32ORB = "word32_orb"
| print_pervasive WORD32XORB = "word32_xorb"
| print_pervasive WORD32ANDB = "word32_andb"
| print_pervasive WORD32NOTB = "word32_notb"
| print_pervasive WORD32LSHIFT = "word32_lshift"
| print_pervasive WORD32RSHIFT = "word32_rshift"
| print_pervasive WORD32ARSHIFT = "word32_arshift"
val constructor_name_list = [(REF,"ref")]
val value_name_list =
[(CALL_C,"call_c"),
(EQ, "inline_equality"),
(EQFUN,"external_equality"),
(NE,"<>"),
(LENGTH,"length"),
(SUB,"sub"),
(UPDATE,"update"),
(UNSAFE_SUB,"unsafe_sub"),
(UNSAFE_UPDATE,"unsafe_update"),
(FLOOR,"floor"),
(REAL,"real"),
(FDIV,"/"),
(LOAD_STRING,"load_string"),
(AT, "@"),
(ARCTAN, "arctan"),
(CHR, "chr"),
(CHARCHR, "char_chr"),
(HAT, "^"),
(COS, "cos"),
(EXP, "exp"),
(EXPLODE, "explode"),
(IMPLODE, "implode"),
(INTABS, "int_abs"),
(INTEQ, "int_equal"),
(INTGREATER, "int_greater"),
(INTGREATEREQ, "int_greater_or_equal"),
(INTLESS, "int_less"),
(INTLESSEQ, "int_less_or_equal"),
(INTUMINUS, "int_negate"),
(INTNE, "int_not_equal"),
(INTMINUS, "int_minus"),
(INTSTAR, "int_multiply"),
(INTPLUS, "int_plus"),
(INTMOD, "int_mod"),
(INTDIV, "int_div"),
(UNSAFEINTPLUS, "unsafe_int_plus"),
(UNSAFEINTMINUS, "unsafe_int_minus"),
(LN, "ln"),
(DEREF,"!"),
(BECOMES,":="),
(MAP, "map"),
(UMAP, "umap"),
(O, "o"),
(NOT, "not"),
(ORD, "ord"),
(CHARORD, "char_ord"),
(ORDOF, "ordof"),
(REALABS, "real_abs"),
(REALEQ, "real_equal"),
(REALGREATER, "real_greater"),
(REALGREATEREQ, "real_greater_or_equal"),
(REALLESS, "real_less"),
(REALLESSEQ, "real_less_or_equal"),
(REALUMINUS, "real_negate"),
(REALNE, "real_not_equal"),
(REALMINUS, "real_minus"),
(REALSTAR, "real_multiply"),
(REALPLUS, "real_plus"),
(REV, "rev"),
(SIN, "sin"),
(SIZE, "size"),
(SQRT, "sqrt"),
(STRINGEQ, "string_equal"),
(STRINGNE, "string_not_equal"),
(STRINGLT, "string_less"),
(STRINGLE, "string_less_equal"),
(STRINGGT, "string_greater"),
(STRINGGE, "string_greater_equal"),
(CHAREQ, "char_equal"),
(CHARNE, "char_not_equal"),
(CHARLT, "char_less"),
(CHARLE, "char_less_equal"),
(CHARGT, "char_greater"),
(CHARGE, "char_greater_equal"),
(ARRAY_FN,"array"),
(BYTEARRAY, "bytearray"),
(BYTEARRAY_LENGTH, "bytearray_length"),
(BYTEARRAY_SUB, "bytearray_sub"),
(BYTEARRAY_UNSAFE_SUB, "bytearray_unsafe_sub"),
(BYTEARRAY_UPDATE, "bytearray_update"),
(BYTEARRAY_UNSAFE_UPDATE, "bytearray_unsafe_update"),
(FLOATARRAY, "floatarray"),
(FLOATARRAY_LENGTH, "floatarray_length"),
(FLOATARRAY_SUB, "floatarray_sub"),
(FLOATARRAY_UNSAFE_SUB, "floatarray_unsafe_sub"),
(FLOATARRAY_UPDATE, "floatarray_update"),
(FLOATARRAY_UNSAFE_UPDATE, "floatarray_unsafe_update"),
(VECTOR, "vector"),
(VECTOR_LENGTH, "vector_length"),
(VECTOR_SUB, "vector_sub"),
(ANDB,"andb"),
(LSHIFT,"lshift"),
(NOTB,"notb"),
(ORB,"orb"),
(RSHIFT,"rshift"),
(ARSHIFT,"arshift"),
(XORB,"xorb"),
(CAST, "cast"),
(ALLOC_STRING, "alloc_string"),
(ALLOC_VECTOR, "alloc_vector"),
(ALLOC_PAIR, "alloc_pair"),
(RECORD_UNSAFE_SUB, "record_unsafe_sub"),
(RECORD_UNSAFE_UPDATE, "record_unsafe_update"),
(STRING_UNSAFE_SUB, "string_unsafe_sub"),
(STRING_UNSAFE_UPDATE, "string_unsafe_update"),
(IDENT_FN, "make_ml_value"),
(ML_OFFSET, "ml_value_from_offset"),
(ENTUPLE, "make_ml_value_tuple"),
(ML_CALL, "call_ml_value"),
(ML_REQUIRE, "ml_require"),
(LOAD_VAR, "load_var"),
(LOAD_EXN, "load_exn"),
(LOAD_STRUCT, "load_struct"),
(LOAD_FUNCT, "load_funct"),
(GET_IMPLICIT,"get_implicit"),
(WORDEQ, "word_equal"),
(WORDNE, "word_not_equal"),
(WORDLT, "word_less"),
(WORDLE, "word_less_equal"),
(WORDGT, "word_greater"),
(WORDGE, "word_greater_equal"),
(WORDPLUS, "word_plus"),
(WORDMINUS, "word_minus"),
(WORDSTAR, "word_star"),
(WORDDIV, "word_div"),
(WORDMOD, "word_mod"),
(WORDORB, "word_orb"),
(WORDXORB, "word_xorb"),
(WORDANDB, "word_andb"),
(WORDNOTB, "word_notb"),
(WORDLSHIFT, "word_lshift"),
(WORDRSHIFT, "word_rshift"),
(WORDARSHIFT, "word_arshift"),
(INT32PLUS, "int32_plus"),
(INT32STAR, "int32_multiply"),
(INT32MINUS, "int32_minus"),
(INT32UMINUS, "int32_negate"),
(INT32ABS, "int32_abs"),
(INT32MOD, "int32_mod"),
(INT32DIV, "int32_div"),
(INT32LESS, "int32_less"),
(INT32GREATER, "int32_greater"),
(INT32LESSEQ, "int32_less_equal"),
(INT32GREATEREQ, "int32_greater_equal"),
(INT32EQ, "int32_equal"),
(INT32NE, "int32_not_equal"),
(WORD32EQ, "word32_equal"),
(WORD32NE, "word32_not_equal"),
(WORD32LT, "word32_less"),
(WORD32LE, "word32_less_equal"),
(WORD32GT, "word32_greater"),
(WORD32GE, "word32_greater_equal"),
(WORD32PLUS, "word32_plus"),
(WORD32MINUS, "word32_minus"),
(WORD32STAR, "word32_star"),
(WORD32DIV, "word32_div"),
(WORD32MOD, "word32_mod"),
(WORD32ORB, "word32_orb"),
(WORD32XORB, "word32_xorb"),
(WORD32ANDB, "word32_andb"),
(WORD32NOTB, "word32_notb"),
(WORD32LSHIFT, "word32_lshift"),
(WORD32RSHIFT, "word32_rshift"),
(WORD32ARSHIFT, "word32_arshift")]
val exception_name_list =
[(EXBIND, "Bind"),
(EXCHR, "Chr"),
(EXDIV, "Div"),
(EXEXP, "Exp"),
(EXINTERRUPT, "Interrupt"),
(EXIO, "Io"),
(EXLN, "Ln"),
(EXMATCH, "Match"),
(EXORD, "Ord"),
(EXSQRT, "Sqrt"),
(EXSIZE, "Size"),
(EXSUBSCRIPT, "Subscript"),
(EXOVERFLOW, "Overflow"),
(EXRANGE, "Range")]
val pervasives =
map (fn (x,y) => x)
(value_name_list @ exception_name_list @ constructor_name_list)
val _ =
map (fn x => ((print_pervasive x)
handle _ =>
(print("*** DANGER *** Failed to find a print_pervasive in _pervasives for something\n ");
Crash.impossible "_pervasives - failure to print_pervasive for something")))
pervasives
val _ =
Lists.reducel( fn (y,x) =>
let
val str = print_pervasive x
in
if Lists.member(str, y)
then Crash.impossible("print_pervasive '" ^ str ^
"' is not unique")
else str::y
end)
([],pervasives)
val library_contents =
let
fun make_cons (cons, list) =
map (fn (pervasive, name) =>
(cons (Symbol.find_symbol name), pervasive))
list
fun make_sorted (list, order) =
map #2
(Lists.qsort (fn ((id, _), (id', _)) => order (id, id')) list)
in
make_sorted
(make_cons (Ident.VAR, value_name_list) @
make_cons (Ident.EXCON, exception_name_list),
Ident.valid_order)
end
fun field_number pervasive =
let
fun find (_, []) =
Crash.impossible ("No field number for " ^ print_pervasive pervasive)
| find (number, pervasive'::rest) =
if pervasive = pervasive' then
number
else
find (number + 1, rest)
val field = find (0, library_contents)
in
field
end
val nr_fields = length library_contents
local
val sort_map =
let
fun number ([],_) = []
| number (pervasive::rest,num) = (print_pervasive pervasive,num) :: number(rest,num+1)
in
Map.from_list
((op<):string*string->bool,op =)
(number (pervasives,1))
end
val sort = fn x => Map.apply sort_map (print_pervasive x)
in
val encode = sort
local
val inverse =
Map.apply (Map.from_list
( (op<) : int * int -> bool,(op=) : int * int -> bool)
(map (fn p => (encode p, p)) pervasives))
in
fun decode i =
inverse i
handle Map.Undefined =>
Crash.impossible
("Pervasives: Unable to find inverse of sort function for " ^ Int.toString i)
end
fun order (pervasive, pervasive') = (sort pervasive) < (sort pervasive')
val eq = op= : pervasive * pervasive -> bool
end
end
;
