require "LRparser";
require "../lexer/lexer";
require "../utils/crash";
require "parser";
functor NewParser (
structure LRparser : LRPARSER
structure Lexer : LEXER
structure Crash : CRASH
sharing Lexer.Token = LRparser.ActionFunctions.Token
sharing LRparser.ActionFunctions.Info.Location = Lexer.Info.Location
sharing Lexer.Token.Symbol = LRparser.ActionFunctions.Absyn.Ident.Symbol
sharing LRparser.ActionFunctions.Info = Lexer.Info
sharing type LRparser.ActionFunctions.Options.options = Lexer.Options
) : PARSER =
struct
structure Lexer = Lexer
structure Absyn = LRparser.ActionFunctions.Absyn
structure ActionFunctions = LRparser.ActionFunctions
structure Info = ActionFunctions.Info
structure Token = Lexer.Token
structure Options = ActionFunctions.Options
structure PE = ActionFunctions.PE
structure Symbol = Absyn.Ident.Symbol
type ParserBasis = PE.pB
val empty_pB = PE.empty_pB
exception WrongParseResultType
exception SeriousParseError
fun with_parser_basis pB f =
let
val old_pB = ActionFunctions.getParserBasis()
val _ = ActionFunctions.setParserBasis pB
val result =
f ()
handle exn =>
(ActionFunctions.setParserBasis old_pB;raise exn)
in
ActionFunctions.setParserBasis old_pB;
result
end
fun token_to_string tok =
ActionFunctions.print_token (ActionFunctions.token_to_parsed_object (false, tok))
fun check_semicolon (error_info,options,ts,(tok,loc)) =
case tok of
Token.RESERVED Token.SEMICOLON => ()
| Token.EOF _ => ()
| Token.IGNORE => ()
| _ => Lexer.ungetToken((tok,loc), ts)
fun getToken error_info (args as (options, ls, ts)) =
let
val token = Lexer.getToken error_info args
val _ =
case token of
Token.LONGID (_,sym) =>
if Symbol.symbol_name sym = ""
then Info.error error_info (Info.RECOVERABLE,
Lexer.locate ts,
"Invalid long identifier: " ^ Token.makestring token)
else ()
| _ => ()
in
token
end
fun parse_topdec error_info (options,ts,pB) =
let
val gettok = getToken error_info
val parse_it = LRparser.parse_it (error_info,options)
val lasttok = ref (Token.IGNORE, Info.Location.UNKNOWN)
fun get_next () =
let
val loc1 = Lexer.locate ts
val tok = gettok (options, Token.PLAIN_STATE, ts)
val loc = Lexer.locate ts
in
(lasttok := (tok,loc1);
case tok of
Token.EOF (Token.IN_COMMENT _) =>
Info.error error_info
(Info.RECOVERABLE, loc,
"End of file reached while reading comment")
| Token.EOF (Token.IN_STRING _) =>
Info.error error_info
(Info.RECOVERABLE, loc,
"End of file reached while reading string")
| _ => ();
(tok, loc))
end
in
with_parser_basis
pB
(fn () =>
(ignore(parse_it (get_next, Lexer.is_interactive ts));
Crash.impossible "Topdec not found in parser??!!"))
handle ActionFunctions.FoundTopDec x =>
(check_semicolon (error_info,options,ts,!lasttok);
x)
end
exception NotExpression
fun parse_string (s,error_info,options,pB) =
let
open Absyn
fun get_expression (STRDECtopdec (strdec,_)) =
(case strdec of
(DECstrdec (VALdec ([(pat,exp,_)],[],_,_))) =>
(case pat of
VALpat ((valid,_),_) =>
(case valid of
Ident.LONGVALID (Ident.NOPATH,Ident.VAR sym) =>
if Symbol.symbol_name sym = "it"
then exp
else raise NotExpression
| _ => raise NotExpression)
| _ => raise NotExpression)
| _ => raise NotExpression)
| get_expression _ = raise NotExpression
val sref = ref s
fun input_fn _ = let val result = !sref in sref := ""; result end
val ts = Lexer.mkTokenStream(input_fn,"String Input")
val (topdec,_) = parse_topdec error_info (options,ts,pB)
in
get_expression topdec
end
val (initial_pB,initial_pB_for_builtin_library) =
let
fun parse pB s =
let
val done = ref false
val ts =
Lexer.mkTokenStream
(fn _ => if !done then "" else (done := true; s), "")
in
parse_topdec
(Info.make_default_options ())
(Options.default_options,ts,pB)
end
val (_, initial) =
parse empty_pB
"(* first value constructors *) \
          \  datatype constructors = true | false | nil | :: | ref \

          \  (* next exception constructors *) \
          \  exception Ord and Chr and Div and Sqrt and Exp and Ln and Io \
          \ and Match and Bind and Interrupt \

          \  (* next value variables *) \
          \  val map = () and rev = () and not = () and ~ = () and abs = () \
          \  and floor = () and real = () and sqrt = () and sin = () and cos = () \
          \  and arctan = () and exp = () and ln = () and size = () and chr = () \
          \  and ord = () and explode = () and implode = () and ! = () \
          \  and substring = ()   \
          
          \  and / = () and div = () and mod = () and + = () and * = () and - = () \
          \  and ^ = () and @ = () and <> = () and < = () and > = () \
          \  and <= = () and >= = () and := = () and o = () \

          \  (* finally we define the infix identifiers *) \
          \  infix 7 / * div mod \
          \  infix 6 + - ^ \
          \  (*infix 5 @*) \
          \  infixr 5 :: @ \
          \  infix 4 <> < > <= >= =\
          \  infix 3 := o   \
          \  infix 0 before \
          \\
          \\
          \ structure Array = \
           \    struct     \
           \       val update = () and length = () and array = () \
           \         and sub = () and tabulate = () and arrayoflist = () \
           \        exception Size and Subscript   \
           \    end ; ";
val (_, initial') =
parse empty_pB
"(* first value constructors *) \
          \  datatype constructors = true | false | nil | :: | ref \

          \  (* next value variables *) \
          \  fun call_c x = ()  \

          \  (* finally we define the infix identifiers *) \
          \  infixr 5 ::  \
          \        ;"
in
(initial,initial')
end
val augment_pB = PE.augment_pB
val remove_str = PE.remove_str
type ParserState = LRparser.ParserState
val initial_parser_state = LRparser.initial_parser_state
fun is_initial_state ps = LRparser.is_initial_state ps
exception FoundTopDec of (Absyn.TopDec * ParserBasis * Info.Location.T)
exception SyntaxError of string * Info.Location.T
fun parse_incrementally error_info (options,ts,pB,ps,ls) =
let
val gettok = getToken error_info
val lasttok = ref (Token.IGNORE, Info.Location.UNKNOWN)
fun loop (ps, ls) =
let
val loc1 = Lexer.locate ts
val tok = gettok (options, ls, ts)
val location = Lexer.locate ts
in
lasttok := (tok,loc1);
case tok of
Token.EOF ls' =>
(ActionFunctions.getParserBasis(), ps, ls')
| _ =>
let val new_state =
LRparser.parse_one_token((error_info,options),tok,location,ps)
in
if LRparser.error_state new_state then
raise SyntaxError ("Unexpected `" ^ token_to_string tok ^ "'",location)
else
loop (new_state, Token.PLAIN_STATE)
end
end
in
with_parser_basis
pB
(fn () =>
if Lexer.eof ts then
let
val location = Lexer.locate ts
in
case ls of
(Token.IN_COMMENT _) =>
(Info.error error_info
(Info.RECOVERABLE, location,
"End of file reached while reading comment");
(pB, ps, ls))
| (Token.IN_STRING _) =>
(Info.error error_info
(Info.RECOVERABLE, location,
"End of file reached while reading string");
(pB, ps, ls))
| _ =>
let
val new_state =
LRparser.parse_one_token ((error_info,options),
Lexer.Token.EOF ls, location, ps)
in
(ActionFunctions.getParserBasis(), new_state, ls)
end
end
else
loop (ps, ls))
handle ActionFunctions.FoundTopDec (dec, pB) =>
(check_semicolon (error_info,options,ts,!lasttok);
raise FoundTopDec (dec, pB, Lexer.locate ts))
end
end
;
