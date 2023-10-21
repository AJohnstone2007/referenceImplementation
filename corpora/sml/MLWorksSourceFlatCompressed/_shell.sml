require "../utils/lists";
require "../utils/crash";
require "../parser/parser";
require "shell_types";
require "user_context";
require "incremental";
require "shell";
functor Shell (
structure Crash : CRASH
structure Lists : LISTS
structure Parser : PARSER
structure ShellTypes: SHELL_TYPES
structure UserContext: USER_CONTEXT
structure Incremental: INCREMENTAL
sharing Parser.Lexer.Info = Incremental.InterMake.Compiler.Info
sharing Incremental.InterMake.Compiler.Absyn = Parser.Absyn
sharing ShellTypes.Options = UserContext.Options =
Incremental.InterMake.Compiler.Options
sharing type Incremental.InterMake.Compiler.ParserBasis = Parser.ParserBasis
sharing type Incremental.Result = UserContext.Result
sharing type Parser.Lexer.Options = ShellTypes.Options.options
sharing type ShellTypes.Context = Incremental.Context
sharing type ShellTypes.user_context = UserContext.user_context
sharing type UserContext.identifier =
Parser.Absyn.Ident.Identifier
sharing type ShellTypes.preferences = UserContext.preferences
): SHELL =
struct
structure Compiler = Incremental.InterMake.Compiler
structure Parser = Parser
structure Lexer = Parser.Lexer
structure Info = Compiler.Info
structure Options = ShellTypes.Options
structure Absyn = Parser.Absyn
type ShellData = ShellTypes.ShellData
type Context = Incremental.Context
exception Exit of int
datatype ShellState =
SHELL_STATE of
{parser_state: Parser.ParserState,
lexer_state: Lexer.Token.LexerState,
source: string,
line_count: int}
datatype Result =
OK of ShellState
| ERROR of Info.error * Info.error list
| INTERRUPT
| DEBUGGER_TRAPPED
| TRIVIAL
val initial_state =
SHELL_STATE
{parser_state = Parser.initial_parser_state,
lexer_state = Lexer.Token.PLAIN_STATE,
source = "",
line_count = 1}
fun shell (shell_data,stream_name,flush_stream) =
let
val ShellTypes.SHELL_DATA{debugger,
exit_fn,
...} = shell_data
val output_fn = print
fun do_prompt (s, SHELL_STATE {line_count, ...}) =
ShellTypes.get_current_prompter
shell_data
{line = ~1,
subline = line_count - 1,
name = s,
topdec =
Incremental.topdec (ShellTypes.get_current_context shell_data)}
val current_context = ref (ShellTypes.get_current_context shell_data)
fun get_current_pB () = Incremental.parser_basis (!current_context)
val current_pB = ref (get_current_pB ())
fun do_line error_info (line, state) =
ShellTypes.with_shell_data
shell_data
(fn () =>
let
val real_eof = size line = 0
val SHELL_STATE
{parser_state = parser_start_state,
lexer_state = lexer_start_state,
source = old_source,
line_count} =
state
val full_source = old_source ^ line
val end_position = ref 0;
val topdecs = ref [];
fun error_wrap f a =
Info.wrap
error_info
(Info.FATAL, Info.RECOVERABLE,
Info.FAULT, Info.Location.FILE stream_name)
f
a
fun check_eof () =
if real_eof then
exit_fn 0
else
()
fun all_chars p str =
let
fun scan i =
if i < 0 then
true
else
p (MLWorks.String.ordof (str, i)) andalso (scan (i-1))
in
scan (size str -1)
end
val trivial =
all_chars
(fn c => (c = ord #" " orelse c = ord #"\n" orelse
c = ord #"\t" orelse c = ord #"\r" orelse
c = ord #"\012"))
val trivial' =
all_chars
(fn c => (c = ord #" " orelse c = ord #"\n" orelse
c = ord #"\t" orelse c = ord #"\r" orelse
c = ord #"\012" orelse c = ord #";"))
in
if line_count = 1 andalso size line > 0
andalso trivial' line then
([], "", TRIVIAL)
else if lexer_start_state = Lexer.Token.PLAIN_STATE
andalso size line > 0 andalso trivial line then
([],
full_source,
OK (SHELL_STATE
{lexer_state = lexer_start_state,
parser_state = parser_start_state,
source = full_source,
line_count = line_count + 1}))
else
let
val input_function =
let val buff = ref line
in
fn _ => (let val out = !buff in buff := ""; out end)
end
val token_stream = Lexer.mkLineTokenStream (input_function,
stream_name,
line_count,
real_eof)
exception STOP of ShellState
fun get_topdec error_info (lexer_state, parser_state) =
(let
val (pB,new_ps,new_ls) =
Parser.parse_incrementally
error_info
(ShellTypes.get_current_options shell_data,
token_stream,
!current_pB,
parser_state,
lexer_state)
in
current_pB := pB;
raise STOP
(SHELL_STATE
{parser_state = new_ps,
lexer_state = new_ls,
source = full_source,
line_count = line_count + 1})
end
handle
Parser.FoundTopDec (topdec,newpB,loc) =>
(topdec,newpB,loc)
| Parser.SyntaxError (message,location) =>
Info.error' error_info (Info.FATAL, location, message))
fun reset_parsing_state () =
(current_context := ShellTypes.get_current_context (shell_data);
current_pB := get_current_pB ())
fun loop (lexer_state, parser_state) =
let
val _ =
if Parser.is_initial_state parser_state
andalso lexer_state = Lexer.Token.PLAIN_STATE then
reset_parsing_state ()
else
()
val parsing_context = !current_context
val (topdec, new_pB, loc2) =
error_wrap get_topdec (lexer_state, parser_state)
handle exn as Info.Stop _ =>
(ignore(flush_stream ());
raise exn)
val loc1 =
case topdec of
Absyn.STRDECtopdec (_, l) => l
| Absyn.REQUIREtopdec (_, l) => l
| Absyn.FUNCTORtopdec (_, l) => l
| Absyn.SIGNATUREtopdec (_, l) => l
val loc = Info.Location.combine (loc1, loc2);
val (s_pos, e_pos) =
Info.Location.extract (loc, full_source)
handle
Info.Location.InvalidLocation => (0, 1)
val new_source =
substring (full_source, s_pos, e_pos - s_pos)
handle Subscript => full_source
val result =
(Incremental.compile_source
error_info
(Incremental.OPTIONS
{options = ShellTypes.get_current_options shell_data,
debugger=debugger},
parsing_context,
Compiler.TOPDEC (stream_name,topdec,new_pB))
handle
exn as ShellTypes.DebuggerTrapped =>
(end_position := e_pos;
topdecs := new_source :: !topdecs;
raise exn)
| exn as MLWorks.Interrupt =>
(end_position := e_pos;
topdecs := new_source :: !topdecs;
raise exn))
in
UserContext.process_result
{src = UserContext.STRING new_source,
result = result,
user_context = ShellTypes.get_user_context shell_data,
options = ShellTypes.get_current_options shell_data,
preferences =
ShellTypes.get_current_preferences shell_data,
output_fn = output_fn};
end_position := e_pos;
topdecs := new_source :: !topdecs;
reset_parsing_state();
if Lexer.eof token_stream then
raise STOP initial_state
else
();
loop
(Lexer.Token.PLAIN_STATE, Parser.initial_parser_state)
end
in
loop (lexer_start_state, parser_start_state)
handle exn =>
let
val current_source =
let
val i = !end_position
val remainder =
substring (line, i, size line - i)
handle
Subscript => line
in
if trivial remainder then
""
else
remainder
end
in
case exn of
STOP
(state as SHELL_STATE
{parser_state, lexer_state, source, line_count}) =>
(check_eof ();
if lexer_state = Lexer.Token.PLAIN_STATE andalso
Parser.is_initial_state parser_state then
(rev (!topdecs), "", OK initial_state)
else
(rev (!topdecs), current_source, OK state))
| Info.Stop (error, error_list) =>
(reset_parsing_state();
(rev (!topdecs),
current_source,
ERROR (error, error_list)))
| ShellTypes.DebuggerTrapped =>
(check_eof ();
reset_parsing_state();
(rev (!topdecs), current_source, DEBUGGER_TRAPPED))
| MLWorks.Interrupt =>
(check_eof ();
reset_parsing_state();
(rev (!topdecs), current_source, INTERRUPT))
| exn as Exit _ => raise exn
| exn => raise exn
end
end
end)
in
(do_line, do_prompt)
end
end
;
