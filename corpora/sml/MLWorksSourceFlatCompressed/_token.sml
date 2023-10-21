require "symbol";
require "token";
require "../basis/__int";
functor Token (
structure Symbol : SYMBOL
) : TOKEN =
struct
structure Symbol = Symbol
datatype Reserved =
ABSTYPE | AND | ANDALSO | AS | CASE | DO | DATATYPE | ELSE |
END | EXCEPTION | FN | FUN | HANDLE | IF | IN | INFIX |
INFIXR | LET | LOCAL | NONFIX | OF | OP | OPEN | ORELSE |
RAISE | REC | REQUIRE | THEN | TYPE | VAL | WHERE | WITH | WITHTYPE | WHILE |
EQTYPE | FUNCTOR | INCLUDE | SHARING |
SIG | SIGNATURE | STRUCT | STRUCTURE |
LPAR | RPAR | BRA | KET | LBRACE | RBRACE | COMMA | COLON | ABSCOLON |
SEMICOLON | ELLIPSIS | UNDERBAR | VBAR | EQUAL | DARROW | ARROW |
HASH |
ABSTRACTION |
MAGICOPEN
datatype LexerState =
PLAIN_STATE |
IN_COMMENT of int |
IN_STRING of int list
datatype Token =
RESERVED of Reserved |
INTEGER of string |
REAL of string |
STRING of string |
CHAR of string |
WORD of string |
LONGID of Symbol.Symbol list * Symbol.Symbol |
TYVAR of Symbol.Symbol * bool * bool |
IGNORE |
EOF of LexerState
fun mkstring PLAIN_STATE = ""
| mkstring (IN_STRING _) = " (in string)"
| mkstring (IN_COMMENT n) =
" (in " ^ Int.toString n ^ " levels of comment)"
fun makestring token =
case token of
RESERVED (reserved) =>
(case reserved of
ABSTYPE => "abstype"
| AND => "and"
| ANDALSO => "andalso"
| AS => "as"
| CASE => "case"
| DO => "do"
| DATATYPE => "datatype"
| ELSE => "else"
| END => "end"
| EXCEPTION => "exception"
| FN => "fn"
| FUN => "fun"
| HANDLE => "handle"
| IF => "if"
| IN => "in"
| INFIX => "infix"
| INFIXR => "infixr"
| LET => "let"
| LOCAL => "local"
| NONFIX => "nonfix"
| OF => "of"
| OP => "op"
| OPEN => "open"
| ORELSE => "orelse"
| RAISE => "raise"
| REC => "rec"
| REQUIRE => "require"
| THEN => "then"
| TYPE => "type"
| VAL => "val"
| WHERE => "where"
| WITH => "with"
| WITHTYPE => "withtype"
| WHILE => "while"
| EQTYPE => "eqtype"
| FUNCTOR => "functor"
| INCLUDE => "include"
| SHARING => "sharing"
| SIG => "sig"
| SIGNATURE => "signature"
| STRUCT => "struct"
| STRUCTURE => "structure"
| LPAR => "("
| RPAR => ")"
| BRA => "["
| KET => "]"
| LBRACE => "{"
| RBRACE => "}"
| COMMA => ","
| COLON => ":"
| ABSCOLON => ":>"
| SEMICOLON => ";"
| ELLIPSIS => "..."
| UNDERBAR => "_"
| VBAR => "|"
| EQUAL => "="
| DARROW => "=>"
| ARROW => "->"
| HASH => "#"
| ABSTRACTION => "abstraction"
| MAGICOPEN => "<<")
| INTEGER (s) => s
| REAL (s) => s
| STRING (s) => s
| CHAR (s) => s
| WORD (s) => s
| LONGID (slist, sym) => concat (map (fn sym => Symbol.symbol_name sym ^ ".") slist) ^ Symbol.symbol_name sym
| TYVAR (sym, bool1, bool2) => Symbol.symbol_name sym
| IGNORE => "ignore"
| EOF ls => "eof" ^ mkstring ls
end
;
