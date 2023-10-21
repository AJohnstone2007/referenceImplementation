require "../basics/token";
require "../basics/absyn";
require "../parser/parserenv";
require "../main/info";
require "../main/options";
require "LRbasics";
signature ACTIONFUNCTIONS =
sig
structure Token: TOKEN
structure Absyn : ABSYN
structure LRbasics : LRBASICS
structure PE : PARSERENV
structure Info : INFO
structure Options : OPTIONS
sharing Info.Location = Absyn.Ident.Location
sharing PE.Ident = Absyn.Ident
sharing PE.Ident.Symbol = Token.Symbol
type Parsed_Object
type ParserBasis
datatype ActionOpts = OPTS of (Absyn.Ident.Location.T * Info.options * Options.options)
sharing type ParserBasis = PE.pB
val do_debug : bool ref
val dummy_location : Absyn.Ident.Location.T
val setParserBasis : ParserBasis -> unit
val getParserBasis : unit -> ParserBasis
exception ActionError of int
exception ResolveError of string
val dummy : Parsed_Object
val make_id_value : string -> Parsed_Object
val error_id_value : Parsed_Object
val print_token : LRbasics.GSymbol * Parsed_Object -> string
val get_function : int -> (Parsed_Object list * ActionOpts -> Parsed_Object)
val get_resolution : int * Options.options ->
(LRbasics.Action * LRbasics.Action * Parsed_Object list * Parsed_Object -> LRbasics.Action)
type FinalValue
sharing type FinalValue = Absyn.TopDec
exception FoundTopDec of (FinalValue * ParserBasis)
val get_final_value : Parsed_Object -> FinalValue * ParserBasis
val token_to_parsed_object : bool * Token.Token -> (LRbasics.GSymbol * Parsed_Object)
end
;
