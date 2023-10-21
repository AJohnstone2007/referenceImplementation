require "../basis/__int";
require "^.basis.__list";
require "../utils/crash";
require "../utils/lists";
require "LRbasics";
require "LRparser";
require "actionfunctions";
functor LRparser (structure LRbasics : LRBASICS
structure ActionFunctions : ACTIONFUNCTIONS
structure Lists: LISTS
structure Crash : CRASH
sharing LRbasics = ActionFunctions.LRbasics
) : LRPARSER =
struct
structure ActionFunctions = ActionFunctions
structure Info = ActionFunctions.Info
structure Location = Info.Location
structure Options = ActionFunctions.Options
structure Token = ActionFunctions.Token
type Parsed_Object = ActionFunctions.Parsed_Object
type TokenType = ActionFunctions.Token.Token
type FinalType = Parsed_Object
val dummy = ActionFunctions.dummy
fun value_for_sym (LRbasics.LONGID) =
ActionFunctions.error_id_value
| value_for_sym _ = ActionFunctions.dummy
val print_token = ActionFunctions.print_token
val do_debug = false
type 'item lex_buffer = (unit -> 'item) * 'item list ref
fun mk_lexbuff function = (function,ref [])
fun get_next (next_fn,ref []) = next_fn()
| get_next (next_fn,r as ref (a::l)) = (r := l; a)
fun push_item (item, (_, r as ref l)) = r := (item::l)
fun peek_next lexbuff =
let val a = get_next lexbuff
in
push_item(a,lexbuff);
a
end
exception Foundit of Parsed_Object
type stack = (int * Parsed_Object * Location.T) list
fun concat [] = []
| concat ([] :: x) = concat x
| concat ((x :: y) :: z) = x :: (concat (y :: z))
fun popn (0,s) = s
| popn (n, x :: s) = popn (n - 1, s)
| popn _ = Crash.impossible "Rubbish stack in parser (popn)"
fun getn (0, x, acc) = acc
| getn (n, (_,a,_) :: b, acc) = getn (n-1, b, a :: acc)
| getn _ = Crash.impossible "Rubbish stack in parser (getn)"
fun get_stack_bits (0,rest,end_location,args) = (end_location,rest,args)
| get_stack_bits (n,(_,arg,end_location)::rest,_,args) =
get_stack_bits (n-1,rest,end_location,arg::args)
| get_stack_bits (_,[],_,_) = Crash.impossible "Rubbish stack in parser (get_stack_bits)"
fun top_state ((s,_,_) :: _) = s
| top_state _ = Crash.impossible"Empty stack in run_parser"
fun append_list [] = []
| append_list (l::ll) = l @ (append_list ll)
fun remove_duplicates l =
let
fun aux ([],acc) = acc
| aux (a::l,acc) = if Lists.member(a,l) then aux (l,acc) else aux (l, a::acc)
in
aux (rev l,[])
end
fun call_resolution_fn (action1,action2,function,options, n,stack,value) =
let
val args = getn (n, stack, [])
in
(ActionFunctions.get_resolution(function, options)) (action1,action2,args,value)
end
fun get_any_reduction state =
let
fun find [] = NONE
| find (action::rest) =
case action of
LRbasics.Reduce _ => SOME action
| _ => find rest
in
find (LRbasics.get_all_actions state)
end
fun dummy_apply options (stk as (state,_,_)::_,sym,value,location) =
let
val _ = if do_debug
then print ("Dummy apply - state=" ^
Int.toString state ^
" symbol=" ^ print_token (sym,value) ^ "\n")
else ()
fun dummy_apply_action (stk as (state,_,current_location)::_,action,sym,value,location) =
(case action of
LRbasics.Accept => [stk]
| LRbasics.Shift =>
(let
val next_state = LRbasics.get_next_state(sym,state)
val _ = if do_debug then print ("Shifting " ^ print_token (sym,value) ^ "\n") else ();
val new_stack = (next_state,dummy,location)::stk
in
[new_stack]
end)
| LRbasics.Reduce(n,nonT,f) =>
(let
val (end_location,new_stack,args) =
if n = 0
then (current_location,stk,[])
else get_stack_bits (n,stk,current_location,[])
val new_state = LRbasics.get_next_state (nonT,top_state new_stack)
val new_location = Location.combine (end_location,current_location)
in
dummy_apply options ((new_state,dummy,new_location)::new_stack,sym,value,location)
end)
| LRbasics.Resolve actions =>
append_list (map (fn action => dummy_apply_action (stk,action,sym,value,location)) actions)
| LRbasics.Funcall (function,n,act1,act2) =>
(dummy_apply_action(stk,call_resolution_fn
(act1,act2,function, options, n,stk,value),sym,value,location)
handle ActionFunctions.ResolveError _ =>
dummy_apply_action(stk,act1,sym,value,location))
| LRbasics.NoAction => [])
| dummy_apply_action _ = Crash.impossible "Bad arg to dummy_apply_action"
in
dummy_apply_action (stk,LRbasics.get_action(sym,state),sym,value,location)
end
| dummy_apply _ _ = Crash.impossible "Bad arg to dummy_apply"
fun dummy_stklist_apply options (stklist,sym,value,location) =
append_list (map (fn stk => dummy_apply options (stk,sym,value,location)) stklist)
fun try_one _ ([],_,_,_,acc) = acc
| try_one options ((sym',stklist)::l,sym,value,location,acc) =
(case dummy_stklist_apply options (stklist,sym,value,location) of
(((_,_,location)::_)::_) => try_one options (l,sym,value,location,(sym',location)::acc)
| _ => try_one options (l,sym,value,location,acc))
fun is_shiftable sym =
case sym of
LRbasics.TYVAR => false
| LRbasics.REAL => false
| LRbasics.INTEGER => false
| LRbasics.STRING => false
| LRbasics.CHAR => false
| LRbasics.WORD => false
| LRbasics.EOF => false
| LRbasics.BRA => false
| LRbasics.LPAR => false
| LRbasics.LBRACE => false
| LRbasics.LOCAL => false
| LRbasics.ABSTYPE => false
| LRbasics.TYPE => false
| LRbasics.EXCEPTION => false
| LRbasics.FUN => false
| LRbasics.DATATYPE => false
| LRbasics.SIGNATURE => false
| LRbasics.STRUCTURE => false
| LRbasics.FUNCTOR => false
| LRbasics.AND => false
| LRbasics.OP => false
| LRbasics.INFIX => false
| LRbasics.INFIXR => false
| LRbasics.NONFIX => false
| LRbasics.OPEN => false
| LRbasics.IN => false
| LRbasics.HANDLE => false
| LRbasics.WITHTYPE => false
| LRbasics.ABSTRACTION => false
| LRbasics.MAGICOPEN => false
| LRbasics.WHILE => false
| _ => true
fun is_closing_token sym =
case sym of
LRbasics.RPAR => true
| LRbasics.RBRACE => true
| LRbasics.KET => true
| LRbasics.END => true
| _ => false
fun closing_token_actions [] = []
| closing_token_actions (((sym,value,location),_)::l) =
if is_closing_token sym
then (sym,value,location)::(closing_token_actions l)
else closing_token_actions l
exception ParsingStopped
fun run_parser_once opts (sym, value, location, []) = Crash.impossible "Empty stack list in parser"
| run_parser_once (error_info,options as Options.OPTIONS{print_options,...}) (sym, value, location, stacks) =
let
fun call_action_fn (function,args,location) =
(if do_debug then print ("#"^(Int.toString function)^"#") else ();
(ActionFunctions.get_function function)
(args,ActionFunctions.OPTS(location,error_info,options)))
fun apply_action (_,[]) = []
| apply_action (action,stack as (current_state,current_value,current_location) :: _) =
(case action of
LRbasics.Shift =>
let
val new_state = (LRbasics.get_next_state (sym,current_state))
in
[(new_state,value,location)::stack]
end
| LRbasics.Reduce (n,non_term,function) =>
let
val (end_location,new_stack,args) =
if n = 0
then (current_location,stack,[])
else get_stack_bits (n,stack,current_location,[])
val new_location = Location.combine (end_location,current_location)
val result = call_action_fn (function,args,new_location)
val new_state = LRbasics.get_next_state (non_term,(top_state new_stack))
val new_new_stack = (new_state,result,new_location) :: new_stack
val new_action = LRbasics.get_action (sym, new_state)
in
apply_action (new_action,new_new_stack)
end
| LRbasics.Resolve actions =>
(if do_debug then print"Splitting.." else ();
concat (map (fn act => (apply_action (act,stack))) actions))
| LRbasics.Funcall (function,n,action1,action2) =>
apply_action
(call_resolution_fn (action1,action2,function,options, n,stack,value),
stack)
| LRbasics.Accept => raise Foundit current_value
| LRbasics.NoAction => [])
fun dostacks [] = []
| dostacks ([] :: rest) = dostacks rest
| dostacks (stack :: rest) =
let
val current_state = top_state stack
val action = LRbasics.get_action (sym, current_state)
in
case apply_action (action,stack) of
[] => dostacks rest
| new => new @ dostacks rest
end
in
dostacks stacks
end
fun
start_string _ = ""
fun parse_it (options as (error_info, options' as Options.OPTIONS
{compat_options = Options.COMPATOPTIONS
{weak_type_vars, ...}, ...})) (lexer,interactivep) =
let
fun lex_it () =
let
val (token,location) = lexer ()
val (sym,value) = ActionFunctions.token_to_parsed_object(weak_type_vars, token)
in
if do_debug then print ("$" ^ Token.makestring token) else ();
(sym,value,location)
end
val (sym,value,location) = lex_it ()
val lexbuff = mk_lexbuff lex_it
val run_once = run_parser_once options
fun restart_parser stklist =
let val (sym,value,location) = get_next lexbuff
in
run_parser (sym,value,location,stklist)
end
and try_local_correction (stklist,sym,value,location) =
let
val _ = if do_debug then print ("Entering local correction, " ^ Int.toString (length stklist) ^ " stack(s)\n") else ()
val top_states = map top_state stklist
val possible_symbols = remove_duplicates (append_list (map LRbasics.get_possible_symbols top_states))
val (next_symbol,next_value,next_location) = peek_next lexbuff
val possible_continuations =
List.filter
(fn (_,[]) => false | _ => true)
(map
(fn sym' =>
let
val value = value_for_sym sym'
in
((sym',value,location),
dummy_stklist_apply options' (stklist,sym',value,location))
end)
possible_symbols)
val continuations_to_try = List.filter (is_shiftable o #1 o #1) possible_continuations
fun report_error message =
Info.error
error_info
(Info.RECOVERABLE, location,
"Unexpected `" ^ print_token (sym,value) ^ "', " ^ message)
in
case try_one options' (continuations_to_try,sym,value,location,[]) of
((sym',value',location'),location'') :: _ =>
(push_item ((sym,value,location),lexbuff);
push_item ((sym',value',location'),lexbuff);
report_error ("inserting `" ^ print_token (sym',value') ^ "'" ^ start_string location'');
restart_parser stklist)
| _ =>
(case try_one options' (continuations_to_try,next_symbol,next_value,next_location,[]) of
((sym',value',location'),location'')::_ =>
(push_item ((sym',value',location'),lexbuff);
report_error ("replacing with `" ^ print_token (sym',value') ^ "'" ^ start_string location'');
restart_parser stklist)
| _ =>
(case closing_token_actions(continuations_to_try) of
(sym',value',location')::_ =>
(push_item ((sym,value,location),lexbuff);
push_item ((sym',value',location'),lexbuff);
report_error ("inserting `" ^ print_token (sym',value') ^ "'");
restart_parser stklist)
| _ => (case sym of
LRbasics.EOF =>
Info.error' error_info (Info.FATAL, location, "Unexpected end of input")
| _ =>
(report_error "ignoring";
restart_parser stklist))))
end
and run_parser (args as (sym,value,location,stacks)) =
let val newstacks = run_once args
in
case newstacks of
[] =>
if interactivep then
Info.error' error_info (Info.FATAL, location, "Unexpected `" ^ print_token (sym,value) ^ "'")
else
try_local_correction(stacks,sym,value,location)
| _ =>
let
val (newsym,newval,newlocation) = get_next lexbuff
in
run_parser (newsym,newval,newlocation,newstacks)
end
end
in
run_parser (sym, value, location, [[(0, dummy,ActionFunctions.dummy_location)]])
handle Foundit x => x
end
type ParserState = stack list
val initial_parser_state = [[(0, dummy,ActionFunctions.dummy_location)]]
fun is_initial_state [[(0,_,_)]] = true
| is_initial_state _ = false
fun error_state [] = true
| error_state _ = false
fun parse_one_token(opts as (_, Options.OPTIONS
{compat_options = Options.COMPATOPTIONS
{weak_type_vars, ...}, ...}),token,location,state) =
let
val (symbol,value) = ActionFunctions.token_to_parsed_object(weak_type_vars, token)
in
run_parser_once opts (symbol,value,location,state)
end
end
;
