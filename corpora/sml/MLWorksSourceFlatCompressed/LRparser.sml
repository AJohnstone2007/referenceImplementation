require "actionfunctions";
signature LRPARSER =
sig
structure ActionFunctions : ACTIONFUNCTIONS
type TokenType
sharing type TokenType = ActionFunctions.Token.Token
type Parsed_Object
sharing type Parsed_Object = ActionFunctions.Parsed_Object
type ParserState
val initial_parser_state : ParserState
val is_initial_state : ParserState -> bool
val error_state : ParserState -> bool
val parse_one_token :
((ActionFunctions.Info.options * ActionFunctions.Options.options) *
TokenType * ActionFunctions.Info.Location.T * ParserState) ->
ParserState
val parse_it :
(ActionFunctions.Info.options * ActionFunctions.Options.options) ->
(unit -> TokenType * ActionFunctions.Info.Location.T) * bool ->
Parsed_Object
end
;
