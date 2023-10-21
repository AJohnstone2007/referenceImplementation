signature STREAM =
sig type 'xa stream
val streamify : (unit -> '_a) -> '_a stream
val cons : '_a * '_a stream -> '_a stream
val get : '_a stream -> '_a * '_a stream
end
signature LR_TABLE =
sig
datatype ('a,'b) pairlist = EMPTY | PAIR of 'a * 'b * ('a,'b) pairlist
datatype state = STATE of int
datatype term = T of int
datatype nonterm = NT of int
datatype action = SHIFT of state
| REDUCE of int
| ACCEPT
| ERROR
type table
val numStates : table -> int
val numRules : table -> int
val describeActions : table -> state ->
(term,action) pairlist * action
val describeGoto : table -> state -> (nonterm,state) pairlist
val action : table -> state * term -> action
val goto : table -> state * nonterm -> state
val initialState : table -> state
exception Goto of state * nonterm
val mkLrTable : {actions : ((term,action) pairlist * action) array,
gotos : (nonterm,state) pairlist array,
numStates : int, numRules : int,
initialState : state} -> table
end
signature TOKEN =
sig
structure LrTable : LR_TABLE
datatype ('a,'b) token = TOKEN of LrTable.term * ('a * 'b * 'b)
val sameToken : ('a,'b) token * ('a,'b) token -> bool
end
signature LR_PARSER =
sig
structure Stream: STREAM
structure LrTable : LR_TABLE
structure Token : TOKEN
sharing LrTable = Token.LrTable
exception ParseError
val parse : {table : LrTable.table,
lexer : ('_b,'_c) Token.token Stream.stream,
arg: 'arg,
saction : int *
'_c *
(LrTable.state * ('_b * '_c * '_c)) list *
'arg ->
LrTable.nonterm *
('_b * '_c * '_c) *
((LrTable.state *('_b * '_c * '_c)) list),
void : '_b,
ec : { is_keyword : LrTable.term -> bool,
noShift : LrTable.term -> bool,
preferred_change : (LrTable.term list * LrTable.term list) list,
errtermvalue : LrTable.term -> '_b,
showTerminal : LrTable.term -> string,
terms: LrTable.term list,
error : string * '_c * '_c -> unit
},
lookahead : int
} -> '_b *
(('_b,'_c) Token.token Stream.stream)
end
signature LEXER =
sig
structure UserDeclarations :
sig
type ('a,'b) token
type pos
type svalue
end
val makeLexer : (int -> string) -> unit ->
(UserDeclarations.svalue,UserDeclarations.pos) UserDeclarations.token
end
signature ARG_LEXER =
sig
structure UserDeclarations :
sig
type ('a,'b) token
type pos
type svalue
type arg
end
val makeLexer : (int -> string) -> UserDeclarations.arg -> unit ->
(UserDeclarations.svalue,UserDeclarations.pos) UserDeclarations.token
end
signature PARSER_DATA =
sig
type pos
type svalue
type arg
type result
structure LrTable : LR_TABLE
structure Token : TOKEN
sharing Token.LrTable = LrTable
structure Actions :
sig
val actions : int * pos *
(LrTable.state * (svalue * pos * pos)) list * arg->
LrTable.nonterm * (svalue * pos * pos) *
((LrTable.state *(svalue * pos * pos)) list)
val void : svalue
val extract : svalue -> result
end
structure EC :
sig
val is_keyword : LrTable.term -> bool
val noShift : LrTable.term -> bool
val preferred_change : (LrTable.term list * LrTable.term list) list
val errtermvalue : LrTable.term -> svalue
val showTerminal : LrTable.term -> string
val terms: LrTable.term list
end
val table : LrTable.table
end
signature PARSER =
sig
structure Token : TOKEN
structure Stream : STREAM
exception ParseError
type pos
type result
type arg
type svalue
val makeLexer : (int -> string) ->
(svalue,pos) Token.token Stream.stream
val parse : int * ((svalue,pos) Token.token Stream.stream) *
(string * pos * pos -> unit) * arg ->
result * (svalue,pos) Token.token Stream.stream
val sameToken : (svalue,pos) Token.token * (svalue,pos) Token.token ->
bool
end
signature ARG_PARSER =
sig
structure Token : TOKEN
structure Stream : STREAM
exception ParseError
type arg
type lexarg
type pos
type result
type svalue
val makeLexer : (int -> string) -> lexarg ->
(svalue,pos) Token.token Stream.stream
val parse : int * ((svalue,pos) Token.token Stream.stream) *
(string * pos * pos -> unit) * arg ->
result * (svalue,pos) Token.token Stream.stream
val sameToken : (svalue,pos) Token.token * (svalue,pos) Token.token ->
bool
end
;
