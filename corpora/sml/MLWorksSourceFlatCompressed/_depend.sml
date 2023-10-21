require "../basis/__io";
require "../basis/__text_io";
require "../basics/module_id";
require "../lexer/lexer";
require "../main/options";
require "depend";
functor Depend
(structure Lexer : LEXER
structure ModuleId : MODULE_ID
structure Options : OPTIONS
sharing type ModuleId.Symbol = Lexer.Token.Symbol.Symbol
sharing type ModuleId.Location = Lexer.Info.Location.T
sharing type Options.options = Lexer.Options
) : DEPEND =
struct
structure Lexer = Lexer
structure Info = Lexer.Info
structure Token = Lexer.Token
type ModuleId = ModuleId.ModuleId;
fun get_imports_from_stream (is_pervasive, error_info, ts, imports) =
let
val options = Options.default_options
in
case Lexer.getToken error_info (options, Token.PLAIN_STATE, ts) of
Token.RESERVED Token.REQUIRE =>
(case Lexer.getToken error_info (options, Token.PLAIN_STATE, ts) of
Token.STRING filename =>
let
val _ =
case Lexer.getToken
error_info
(options, Token.PLAIN_STATE, ts)
of Token.RESERVED Token.SEMICOLON =>
()
| _ =>
Info.error' error_info
(Info.RECOVERABLE,
Lexer.locate ts,
"missing `;' after `require'")
val module_id =
if is_pervasive then
ModuleId.perv_from_require_string
(filename, Lexer.locate ts)
else
ModuleId.from_require_string
(filename, Lexer.locate ts)
in
get_imports_from_stream
(is_pervasive, error_info, ts, module_id :: imports)
end
| _ =>
Info.error' error_info
(Info.FATAL,
Lexer.locate ts,
"missing string after `require'"))
| _ => imports
end
fun get_imports (is_pervasive, error_info, filename) =
let
val stream = TextIO.openIn filename
val ts = Lexer.mkFileTokenStream (stream, filename)
val imports =
get_imports_from_stream (is_pervasive, error_info, ts, [])
in
TextIO.closeIn stream;
rev imports
end handle IO.Io _ => []
end
;
