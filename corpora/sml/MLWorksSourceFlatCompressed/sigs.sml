require "$.basis.__text_io";
require "^.lib.base";
require "utils";
signature HEADER =
sig
type pos=int
val lineno : pos ref
val text : string list ref
type inputSource
val newSource : string * TextIO.instream * TextIO.outstream -> inputSource
val error : inputSource -> pos -> string -> unit
val warn : inputSource -> pos -> string -> unit
val errorOccurred : inputSource -> unit -> bool
datatype symbol = SYMBOL of string * pos
val symbolName : symbol -> string
val symbolPos : symbol -> pos
val symbolMake : string * int -> symbol
type ty
val tyName : ty -> string
val tyMake : string -> ty
datatype prec = LEFT | RIGHT | NONASSOC
datatype control = NODEFAULT | VERBOSE | PARSER_NAME of symbol |
FUNCTOR of string | START_SYM of symbol |
NSHIFT of symbol list | POS of string | PURE |
PARSE_ARG of string * string
datatype rule = RULE of {lhs : symbol, rhs : symbol list,
code : string, prec : symbol option}
datatype declData = DECL of
{eop : symbol list,
keyword : symbol list,
nonterm : (symbol * ty option) list option,
prec : (prec * (symbol list)) list,
change: (symbol list * symbol list) list,
term : (symbol * ty option) list option,
control : control list,
value : (symbol * string) list}
val join_decls : declData * declData * inputSource * pos -> declData
type parseResult
val getResult : parseResult -> string * declData * rule list
end;
signature PARSE_GEN_PARSER =
sig
structure Header : HEADER
val parse : string -> Header.parseResult * Header.inputSource
end;
signature PARSE_GEN =
sig
val parseGen : string -> unit
end;
signature GRAMMAR =
sig
datatype term = T of int
datatype nonterm = NT of int
datatype symbol = TERM of term | NONTERM of nonterm
datatype grammar = GRAMMAR of
{rules: {lhs : nonterm, rhs : symbol list,
precedence : int option, rulenum : int } list,
terms: int,
nonterms: int,
start : nonterm,
eop : term list,
noshift : term list,
precedence : term -> int option,
termToString : term -> string,
nontermToString : nonterm -> string}
end
signature INTGRAMMAR =
sig
structure Grammar : GRAMMAR
structure SymbolAssoc : TABLE
structure NontermAssoc : TABLE
sharing type SymbolAssoc.key = Grammar.symbol
sharing type NontermAssoc.key = Grammar.nonterm
datatype rule = RULE of
{lhs : Grammar.nonterm,
rhs : Grammar.symbol list,
num : int,
rulenum : int,
precedence : int option}
val gtTerm : Grammar.term * Grammar.term -> bool
val eqTerm : Grammar.term * Grammar.term -> bool
val gtNonterm : Grammar.nonterm * Grammar.nonterm -> bool
val eqNonterm : Grammar.nonterm * Grammar.nonterm -> bool
val gtSymbol : Grammar.symbol * Grammar.symbol -> bool
val eqSymbol : Grammar.symbol * Grammar.symbol -> bool
val DEBUG : bool
val prRule : (Grammar.symbol -> string) * (Grammar.nonterm -> string) *
(string -> 'b) -> rule -> unit
val prGrammar : (Grammar.symbol -> string)*(Grammar.nonterm -> string) *
(string -> unit) -> Grammar.grammar -> unit
end
signature CORE =
sig
structure Grammar : GRAMMAR
structure IntGrammar : INTGRAMMAR
sharing Grammar = IntGrammar.Grammar
datatype item = ITEM of
{ rule : IntGrammar.rule,
dot : int,
rhsAfter: Grammar.symbol list }
val eqItem : item * item -> bool
val gtItem : item * item -> bool
val insert : item * item list -> item list
val union : item list * item list -> item list
datatype core = CORE of item list * int
val gtCore : core * core -> bool
val eqCore : core * core -> bool
val prItem : (Grammar.symbol -> string) * (Grammar.nonterm -> string) *
(string -> unit) -> item -> unit
val prCore : (Grammar.symbol -> string) * (Grammar.nonterm -> string) *
(string -> unit) -> core -> unit
end
signature CORE_UTILS =
sig
structure Grammar : GRAMMAR
structure IntGrammar : INTGRAMMAR
structure Core : CORE
sharing Grammar = IntGrammar.Grammar = Core.Grammar
sharing IntGrammar = Core.IntGrammar
val mkFuncs : Grammar.grammar ->
{ produces : Grammar.nonterm -> IntGrammar.rule list,
shifts : Core.core -> (Grammar.symbol*Core.item list) list,
rules: IntGrammar.rule list,
epsProds : Core.core -> IntGrammar.rule list}
end
signature LRGRAPH =
sig
structure Grammar : GRAMMAR
structure IntGrammar : INTGRAMMAR
structure Core : CORE
sharing Grammar = IntGrammar.Grammar = Core.Grammar
sharing IntGrammar = Core.IntGrammar
type graph
val edges : Core.core * graph -> {edge:Grammar.symbol,to:Core.core} list
val nodes : graph -> Core.core list
val shift : graph -> int * Grammar.symbol -> int
val core : graph -> int -> Core.core
val mkGraph : Grammar.grammar ->
{graph : graph,
produces : Grammar.nonterm -> IntGrammar.rule list,
rules : IntGrammar.rule list,
epsProds: Core.core -> IntGrammar.rule list}
val prGraph: (Grammar.symbol -> string)*(Grammar.nonterm -> string) *
(string -> unit) -> graph -> unit
end
signature LOOK =
sig
structure Grammar : GRAMMAR
structure IntGrammar : INTGRAMMAR
sharing Grammar = IntGrammar.Grammar
val union : Grammar.term list * Grammar.term list -> Grammar.term list
val make_set : Grammar.term list -> Grammar.term list
val mkFuncs : {rules : IntGrammar.rule list, nonterms : int,
produces : Grammar.nonterm -> IntGrammar.rule list} ->
{nullable: Grammar.nonterm -> bool,
first : Grammar.symbol list -> Grammar.term list}
val prLook : (Grammar.term -> string) * (string -> unit) ->
Grammar.term list -> unit
end
signature LALR_GRAPH =
sig
structure Grammar : GRAMMAR
structure IntGrammar : INTGRAMMAR
structure Core : CORE
structure Graph : LRGRAPH
sharing Grammar = IntGrammar.Grammar = Core.Grammar = Graph.Grammar
sharing IntGrammar = Core.IntGrammar = Graph.IntGrammar
sharing Core = Graph.Core
datatype lcore = LCORE of (Core.item * Grammar.term list) list * int
val addLookahead : {graph : Graph.graph,
first : Grammar.symbol list -> Grammar.term list,
eop : Grammar.term list,
nonterms : int,
nullable: Grammar.nonterm -> bool,
produces : Grammar.nonterm -> IntGrammar.rule list,
rules : IntGrammar.rule list,
epsProds : Core.core -> IntGrammar.rule list,
print : string -> unit,
termToString : Grammar.term -> string,
nontermToString : Grammar.nonterm -> string} ->
lcore list
val prLcore : (Grammar.symbol -> string) * (Grammar.nonterm -> string) *
(Grammar.term -> string) * (string -> unit) ->
lcore -> unit
end
signature LR_ERRS =
sig
structure LrTable : LR_TABLE
datatype err = RR of LrTable.term * LrTable.state * int * int
| SR of LrTable.term * LrTable.state * int
| NS of LrTable.term * int
| NOT_REDUCED of int
| START of int
val summary : err list -> {rr : int, sr: int,
not_reduced : int, start : int,nonshift : int}
val printSummary : (string -> unit) -> err list -> unit
end
signature PRINT_STRUCT =
sig
structure LrTable : LR_TABLE
val makeStruct :
{table : LrTable.table,
name : string,
print: string -> unit,
verbose : bool
} -> int
end
signature VERBOSE =
sig
structure Errs : LR_ERRS
val printVerbose :
{table : Errs.LrTable.table,
entries : int,
termToString : Errs.LrTable.term -> string,
nontermToString : Errs.LrTable.nonterm -> string,
stateErrs : Errs.LrTable.state -> Errs.err list,
errs : Errs.err list,
print: string -> unit,
printCores : (string -> unit) -> Errs.LrTable.state -> unit,
printRule : (string -> unit) -> int -> unit} -> unit
end
signature MAKE_LR_TABLE =
sig
structure Grammar : GRAMMAR
structure Errs : LR_ERRS
structure LrTable : LR_TABLE
sharing Errs.LrTable = LrTable
sharing type LrTable.term = Grammar.term
sharing type LrTable.nonterm = Grammar.nonterm
val mkTable : Grammar.grammar * bool ->
LrTable.table *
(LrTable.state -> Errs.err list) *
((string -> unit) -> LrTable.state -> unit) *
Errs.err list
end;
signature SHRINK_LR_TABLE =
sig
structure LrTable : LR_TABLE
val shrinkActionList : LrTable.table * bool ->
(int * int list *
((LrTable.term,LrTable.action) LrTable.pairlist *
LrTable.action) list) * int
end
;
