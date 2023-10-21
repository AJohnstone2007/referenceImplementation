require "symbol";
signature TOKEN =
sig
structure Symbol : SYMBOL
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
STRING of string|
CHAR of string|
WORD of string|
LONGID of Symbol.Symbol list * Symbol.Symbol |
TYVAR of Symbol.Symbol * bool * bool |
IGNORE |
EOF of LexerState
val makestring: Token -> string
end
;
