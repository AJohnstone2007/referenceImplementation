require "../basis/__int";
require "../basis/__text_io";
require "../basis/__text_prim_io";
require "../utils/lists";
require "../utils/map";
require "../utils/mlworks_timer";
require "../basics/token";
require "ndfa";
require "lexrules";
require "lexer";
functor LexGen (structure Lists : LISTS
structure Map : MAP
structure Timer : INTERNAL_TIMER
structure Token : TOKEN
structure Ndfa : NDFA where type action = int where type state = int
structure LexRules : LEXRULES
sharing type LexRules.Result = Token.Token
) : LEXER =
struct
structure Token = Token
structure RegExp = LexRules.RegExp
structure Info = LexRules.Info
structure InBuffer = LexRules.InBuffer
type Options = LexRules.options
type Result = LexRules.Result
local
val column = ref 0
in
fun printDot () =
let
val c = !column
in
if c = 70 then
(print ".\n"; column := 0)
else
(print "."; column := c + 1)
end
end
fun counter m = let val n = ref m in fn () => (n := (!n)+1; !n) end
fun quicksort ([], accum) = accum
| quicksort (pivot :: rest, accum) =
partition (pivot, [], [], rest, accum)
and partition (pivot, left, right, [], accum) =
quicksort (left, pivot :: quicksort (right, accum))
| partition (pivot, left, right, y::ys, accum) =
if (y : int) < pivot then partition (pivot, y :: left, right, ys, accum)
else if y > pivot then partition (pivot, left, y :: right, ys, accum)
else partition (pivot, left, right, ys, accum)
val canonical = fn L => quicksort (L,[])
datatype DfaNode = D of MLWorks.Internal.ByteArray.bytearray * int * int
datatype Dfa = DFA of MLWorks.Internal.ByteArray.bytearray MLWorks.Internal.Array.array * int MLWorks.Internal.Array.array
fun loop (_, _, [], accum) = accum
| loop (char, ndfa, state :: rest, accum) =
loop (char, ndfa, rest, Ndfa.get_char (char, Ndfa.transitions (ndfa,state), accum))
fun trans_subset arg =
Lists.filter(loop arg )
fun epsclosure'(_, [], subset) = subset
| epsclosure'(ndfa, state :: rest, subset) =
let
val new = Lists.filter (Ndfa.get_epsilon (Ndfa.transitions (ndfa, state), rest))
in
epsclosure'(ndfa, new, state :: subset)
end
fun epsclosure arg = canonical (epsclosure' arg)
fun best_action(ndfa, l) =
Lists.reducel
(fn (m, state) =>
let
val an = Ndfa.action(ndfa, state)
in
if an > m then an else m
end)
(0, l)
local
fun loop (n :: ns, res:int) = loop (ns, n + res)
| loop ([], res) = res
val total = fn L => loop (L,0)
in
fun transform ndfa =
let
val nextnode = counter 1
val init = epsclosure(ndfa, [Ndfa.start ndfa], [])
val unmarkedstates = (ref []) : (int list * int) list ref
val markedstates = (ref [D (MLWorks.Internal.ByteArray.array(256,0),0,0)]) : DfaNode list ref
val currentstate = ref (init,1)
local
fun loop ([],[]) = false
| loop (_ ,[]) = false
| loop ([], _) = true
| loop ((h1:int)::t1,h2::t2) =
if h1 < h2 then true
else if h1 > h2 then false
else loop (t1,t2)
in
fun ordering ((hash1:int, subset1), (hash2, subset2)) =
if hash1 < hash2 then true
else if hash1 > hash2 then false
else loop (subset1, subset2)
end
local
fun loop ([],[]) = true
| loop (_ ,[]) = false
| loop ([], _) = false
| loop ((h1:int)::t1,h2::t2) =
if h1 < h2 then false
else if h1 > h2 then false
else loop (t1,t2)
in
fun ordering_eq ((hash1:int, subset1), (hash2, subset2)) =
if hash1 < hash2 then false
else if hash1 > hash2 then false
else loop (subset1, subset2)
end
val states = ref (Map.from_list (ordering,ordering_eq) [((0, []), 0), ((total init, init), 1)])
fun addstate (t,l) =
let
val nodeno = nextnode ()
val actno = best_action(ndfa, l)
in
unmarkedstates := (l,nodeno) :: (!unmarkedstates);
states := Map.define (!states, (t,l), nodeno);
printDot();
nodeno
end
fun find subset =
let
val t = total subset
in
case Map.tryApply'(!states, (t,subset)) of
SOME answer => answer
| _ => addstate(t,subset)
end
fun loop (res, c, subset, ndfa) =
if c < 0 then res
else loop(find(epsclosure(ndfa, trans_subset(c, ndfa, subset, []), [])) ::
res, c-1, subset, ndfa)
exception NotAByte
fun check [] = ()
| check (n::rest) =
if n < 0 orelse n > 255
then raise NotAByte
else check rest
fun transtable subset =
let
val elements = (loop([], 255, subset, ndfa))
val _ = check elements
in
MLWorks.Internal.ByteArray.arrayoflist elements
end
fun doit () =
let
val (s,n) = !currentstate
in
(markedstates := (D(transtable s,n,best_action(ndfa, s))) :: (!markedstates);
(fn [] => () | (s::ss) => (unmarkedstates := ss;
currentstate := s;
doit ()))
(!unmarkedstates))
end
val _ = doit ()
val maxnode = nextnode ()
val trans = MLWorks.Internal.Array.array (maxnode,MLWorks.Internal.ByteArray.array(0,0))
val actions = MLWorks.Internal.Array.array(maxnode,0)
fun addit (D(t,n,a)) =
(MLWorks.Internal.Array.update(trans,n,t); MLWorks.Internal.Array.update(actions,n,a))
val _ = app addit (!markedstates)
in
DFA(trans,actions)
end
end
fun re2ndfa regexp ndfa =
case regexp of
RegExp.EPSILON =>
Ndfa.add(ndfa, Ndfa.epsilon [Ndfa.start ndfa])
| RegExp.NODE s =>
let
fun loop (res, x) =
if x < 0 then res
else loop(Ndfa.add (res, Ndfa.single_char (MLWorks.String.ordof(s, x), Ndfa.start res)), x-1)
in
loop(ndfa, size s - 1)
end
| RegExp.CLASS s =>
let
val start = Ndfa.start ndfa
fun loop (res, x) =
if x < 0 then Ndfa.mk_trans res
else loop ((MLWorks.String.ordof(s, x), start) :: res, x-1)
in
Ndfa.add(ndfa, loop([], size s - 1))
end
| RegExp.BAR(s,t) =>
let
val ndfa1 = re2ndfa s ndfa
val ndfa2 = re2ndfa t (Ndfa.set_start(ndfa1, Ndfa.start ndfa))
val transitions = Ndfa.epsilon [Ndfa.start ndfa1, Ndfa.start ndfa2]
in
Ndfa.add (ndfa2, transitions)
end
| RegExp.DOT(s,t) =>
re2ndfa s (re2ndfa t ndfa)
| RegExp.STAR s =>
Ndfa.add_rec (ndfa, re2ndfa s)
fun convert_regexps [] = (Ndfa.empty, [], 1)
| convert_regexps (regexp :: rest) =
let
val (ndfa, initials, action) = convert_regexps rest
val ndfa' = Ndfa.add_final (ndfa, action)
val ndfa'' = re2ndfa regexp ndfa'
in
(ndfa'', (Ndfa.start ndfa'') :: initials, action + 1)
end
fun convert_rules rules =
let
val (regexps, actions) = Lists.unzip rules
val (ndfa, initials, _) = convert_regexps regexps
val ndfa' = Ndfa.add_start (ndfa, initials)
in
(ndfa', MLWorks.Internal.Array.arrayoflist (rev actions))
end
fun make_dfa rules =
let
val (ndfa, actions) = convert_rules rules
val dfa as DFA(trans, _) = transform ndfa
in
print("\nDFA has " ^ Int.toString(MLWorks.Internal.Array.length trans) ^ " states\n");
(dfa, actions)
end
fun chr_to_string n =
if n >= ord #" " andalso n < 127
then (str o chr) n
else
if n < (ord #" ")
then "^" ^ (str o chr) (n + 64)
else
"\\" ^ Int.toString n
datatype TokenStream =
TOKEN_STREAM of
{buffer : InBuffer.InBuffer,
source_name : string,
interactive : bool,
line_and_col : (int * int) ref,
pushed_back : (Token.Token * Info.Location.T) list ref}
fun lex (dfa as DFA (trans,action_numbers), actions)
((error_info, options),
ts as TOKEN_STREAM {buffer, source_name = filename,...}
) =
let
val startpoint = InBuffer.getpos buffer
val location =
Info.Location.POSITION (filename, InBuffer.getlinenum buffer, InBuffer.getlinepos buffer)
fun lex1 (string,state,finishaction,finishstring,finishpoint,found_one_earlier) =
if InBuffer.eof buffer then
if found_one_earlier then
(InBuffer.position(buffer, finishpoint);
MLWorks.Internal.Array.sub (actions,finishaction-1) (location, buffer, finishstring, (error_info, options))
)
else
(case string of
[] => ()
| _ =>
Info.error
error_info
(Info.RECOVERABLE, location,
"Unexpected end of file");
LexRules.eof)
else
let
val c = InBuffer.getchar buffer
val string = c :: string
val newstate = MLWorks.Internal.ByteArray.sub (MLWorks.Internal.Array.sub (trans,state), c)
in
if newstate = 0 then
if found_one_earlier then
(InBuffer.position(buffer, finishpoint);
MLWorks.Internal.Array.sub (actions,finishaction-1) (location, buffer, finishstring, (error_info,options))
)
else
(InBuffer.position(buffer, startpoint);
let
val discard_char = InBuffer.getchar buffer
in
Info.error
error_info
(Info.RECOVERABLE,
location,
"Illegal character `" ^ (chr_to_string discard_char) ^ "'");
lex (dfa,actions) ((error_info,options), ts)
end)
else
let
val act = MLWorks.Internal.Array.sub(action_numbers,newstate)
in
if act > 0 then
lex1(string,newstate,act,string,InBuffer.getpos buffer,true)
else
lex1(string,newstate,finishaction,finishstring,finishpoint,found_one_earlier)
end
end
fun unexpected _ =
Info.error' error_info
(Info.FAULT, location, "Unexpected lexical error")
in
lex1([], 1, ~1, [], startpoint, false)
end
val lexer = (lex o make_dfa) LexRules.rules
fun fix_input f = (!MLWorks.Internal.text_preprocess) f
fun mkTokenStream (f, name_of_file) =
let
val buffer = InBuffer.mkInBuffer (fix_input f)
in
TOKEN_STREAM{buffer=buffer, source_name=name_of_file, interactive=false,
line_and_col=
ref(Info.Location.first_line, Info.Location.first_col),
pushed_back=ref []}
end
fun mkLineTokenStream (f, name_of_file, line, eof) =
let
val buffer = InBuffer.mkLineInBuffer (fix_input f, line, eof)
in
TOKEN_STREAM{buffer=buffer, source_name=name_of_file, interactive=false,
line_and_col=ref(line,Info.Location.first_col),
pushed_back=ref []}
end
fun mkFileTokenStream (instream, name_of_file) =
let
val (TextPrimIO.RD {readVec, ...}, vec) =
TextIO.StreamIO.getReader (TextIO.getInstream instream)
fun input_fn _ = case readVec of
NONE => raise Fail "readVec not supported.\n"
| SOME rv => rv 4096
in
mkTokenStream (input_fn, name_of_file)
end
fun getToken error_info
(options, Token.IN_COMMENT n,
ts as TOKEN_STREAM{buffer=b, source_name=file, ...}) =
(case LexRules.read_comment (b, n) of
Token.IGNORE =>
getToken error_info (options, Token.PLAIN_STATE, ts)
| t => t)
| getToken error_info
(options, Token.IN_STRING s,
ts as TOKEN_STREAM{buffer=b, source_name=file, line_and_col=ref (line, col),
...}) =
let
val result =
LexRules.continue_string
(Info.Location.EXTENT {name = file, s_line = line, s_col = col,
e_line = line, e_col = col},
b, error_info, s)
in
result
end
| getToken error_info (options, Token.PLAIN_STATE,
ts as TOKEN_STREAM{buffer=b, pushed_back=list as ref ((x,_) :: xs),
...}) =
(list := xs;
x)
| getToken error_info (options, Token.PLAIN_STATE,
ts as TOKEN_STREAM{buffer=b, line_and_col=loc, pushed_back=ref [],
...}) =
let
fun get Token.IGNORE =
(loc := (InBuffer.getlinenum b, InBuffer.getlinepos b);
get (lexer ((error_info, options), ts))
)
|get other = other
val result = get Token.IGNORE
in
result
end
fun associated_filename (TOKEN_STREAM{source_name, ...}) = source_name
fun locate (TOKEN_STREAM {buffer, source_name, pushed_back,line_and_col,...}) =
case !pushed_back of
(_,loc) :: _ => loc
| _ =>
let
val ref (s_line, s_col) = line_and_col
in
Info.Location.EXTENT
{name=source_name, s_line=s_line, s_col=s_col,
e_line=InBuffer.getlinenum buffer, e_col=InBuffer.getlinepos buffer}
end
fun eof (TOKEN_STREAM{buffer=b, pushed_back, ...}) =
!pushed_back = [] andalso InBuffer.eof b
fun clear_eof (TOKEN_STREAM{buffer=b, ...}) = InBuffer.clear_eof b
fun is_interactive (TOKEN_STREAM{interactive, ...}) = interactive
fun flush_to_nl (TOKEN_STREAM{buffer=b, ...}) = InBuffer.flush_to_nl b
fun ungetToken (tokloc, TOKEN_STREAM{pushed_back, ...}) =
pushed_back := tokloc :: !pushed_back
end
;
