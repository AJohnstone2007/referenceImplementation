require "../lexer/lexer";
require "../basics/absyn";
signature PARSER =
sig
structure Lexer : LEXER
structure Absyn : ABSYN
sharing Lexer.Info.Location = Absyn.Ident.Location
sharing Absyn.Ident.Symbol = Lexer.Token.Symbol
type ParserBasis
val empty_pB : ParserBasis
val initial_pB : ParserBasis
val initial_pB_for_builtin_library : ParserBasis
val augment_pB : ParserBasis * ParserBasis -> ParserBasis
val remove_str : ParserBasis * Absyn.Ident.StrId -> ParserBasis
val parse_topdec :
Lexer.Info.options ->
Lexer.Options * Lexer.TokenStream * ParserBasis ->
Absyn.TopDec * ParserBasis
type ParserState
val initial_parser_state : ParserState
val is_initial_state : ParserState -> bool
exception SyntaxError of string * Lexer.Info.Location.T
exception FoundTopDec of
(Absyn.TopDec * ParserBasis * Lexer.Info.Location.T)
val parse_incrementally :
Lexer.Info.options ->
(Lexer.Options * Lexer.TokenStream *
ParserBasis * ParserState * Lexer.Token.LexerState) ->
(ParserBasis * ParserState * Lexer.Token.LexerState)
end
;
