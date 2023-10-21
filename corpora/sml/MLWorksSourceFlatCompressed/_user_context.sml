require "../basis/__int";
require "incremental";
require "interprint";
require "../main/user_options";
require "../main/preferences";
require "../utils/lists";
require "../utils/map";
require "../utils/crash";
require "user_context";
functor UserContext
(structure Incremental: INCREMENTAL
structure InterPrint: INTERPRINT
structure Lists: LISTS
structure UserOptions: USER_OPTIONS
structure Preferences: PREFERENCES
structure Map: MAP
structure Crash: CRASH
sharing Incremental.InterMake.Compiler = InterPrint.Compiler
sharing InterPrint.Compiler.Options = UserOptions.Options
sharing type InterPrint.Context = Incremental.Context
): USER_CONTEXT =
struct
structure Options = UserOptions.Options
structure Ident = Incremental.InterMake.Compiler.Absyn.Ident
structure Symbol = Ident.Symbol
type user_context_options = UserOptions.user_context_options
type preferences = Preferences.preferences
type Context = Incremental.Context
datatype ContextName = CONTEXTNAME of string * int list
type identifier = Ident.Identifier
type Result = Incremental.Result
datatype source_reference =
STRING of string
| COPY of string
datatype history_entry =
ITEM of
{index: int,
id: identifier,
context: Context,
result: string,
source: source_reference}
type register_key = int
datatype user_context =
USER_CONTEXT of
{info: (Context * Context * history_entry list) ref,
user_options: UserOptions.user_context_options,
update_register:
((register_key,
(history_entry list option -> unit)) Map.map * int) ref,
name: ContextName ref,
version_number: int ref,
is_constant: bool,
save_file: string option ref}
exception DebuggerTrapped
val initialContext = ref NONE
val contextList = ref []
fun makeInitialUserContext (context, name, user_options) =
let
val newcontext =
USER_CONTEXT
{info = ref (context, context, []),
name = ref (CONTEXTNAME(name, [])),
user_options = user_options,
update_register = ref (Map.empty' op<, 0),
version_number = ref 0,
is_constant = true,
save_file = ref NONE}
exception AlreadyInitialised
in
case !initialContext of
NONE => initialContext := SOME newcontext
| _ => raise AlreadyInitialised;
contextList := [newcontext];
newcontext
end
fun pushContext c = contextList := c :: (!contextList)
fun getCurrentContexts () = !contextList
fun copyUserContext
(USER_CONTEXT
{info = ref (context, _, _),
name = ref (CONTEXTNAME(name, l)),
user_options,
version_number = r,
...}) =
let
val newcontext =
USER_CONTEXT
{info = ref (context, Incremental.empty_context, []),
user_options = UserOptions.copy_user_context_options user_options,
update_register = ref (Map.empty' op<, 0),
name = ref (CONTEXTNAME(name, !r+1 :: l)),
version_number = ref 0,
is_constant = false,
save_file = ref NONE}
in
r := !r + 1;
pushContext newcontext;
newcontext
end
fun add_update_fn
(USER_CONTEXT {update_register as ref (map, count), ...}, update_fn) =
(update_register := (Map.define (map, count, update_fn), count + 1);
count)
fun remove_update_fn
(USER_CONTEXT {update_register as ref (map, count), ...}, key) =
update_register := (Map.undefine (map, key), count)
local
fun string_index_list (str, [], l) = concat (str :: "-" :: l)
| string_index_list (str, x::t, []) =
string_index_list (str, t, [Int.toString x])
| string_index_list (str, x::t, l) =
string_index_list (str, t, Int.toString x :: "." :: l)
fun string_context_name (CONTEXTNAME(str, [])) = str
| string_context_name (CONTEXTNAME(str, index)) =
string_index_list (str, index, [])
in
fun get_context_name (USER_CONTEXT {name, is_constant, ...}) =
let
val str = string_context_name (!name)
in
if is_constant then
str ^ " (Read Only)"
else
str
end
fun set_context_name (USER_CONTEXT {name, is_constant, ...}, new_name) =
name := CONTEXTNAME (new_name, [1])
end
fun getInitialContext () =
case !initialContext
of SOME c => c
| _ => Crash.impossible "Bad initial context!"
fun getNewInitialContext () =
let
val c = copyUserContext (getInitialContext ())
in
case c of
USER_CONTEXT {name as ref (CONTEXTNAME (_, l)), ...} =>
name := CONTEXTNAME ("MLWorks", l);
c
end
fun get_saved_file_name (USER_CONTEXT {save_file, ...}) = !save_file
fun set_saved_file_name (USER_CONTEXT {save_file, ...}, name) =
save_file := SOME name
fun saved_name_set user_context =
case get_saved_file_name user_context
of NONE => false
| SOME _ => true
fun get_context_info (USER_CONTEXT {info,...}) = !info
fun set_context_info
(USER_CONTEXT {info, update_register as ref (map, _), ...}, newinfo) =
(info := newinfo)
local
fun get_hist c = #3 (get_context_info c)
in
fun get_latest c =
case get_hist c
of [] => NONE
| (h::t) => SOME h
fun get_nth (c, n) =
case get_hist c
of [] => NONE
| l as (ITEM {index, ...} :: _) =>
SOME (Lists.nth (index - n, l))
handle
Lists.Nth => NONE
end
fun clear_debug_info (user_context, name) =
let val (c, delta, hist) = get_context_info user_context
in
set_context_info
(user_context,
(Incremental.clear_debug_info (name, c),
Incremental.clear_debug_info (name, delta),
hist))
end
fun clear_debug_all_info user_context =
let val (c, delta, hist) = get_context_info user_context
in
set_context_info
(user_context,
(Incremental.clear_debug_all_info c,
Incremental.clear_debug_all_info delta,
hist))
end
fun get_user_options (USER_CONTEXT {user_options,...}) = user_options
fun get_context (USER_CONTEXT {info,...}) = #1(!info)
fun get_delta (USER_CONTEXT {info,...}) = #2(!info)
fun get_history (USER_CONTEXT {info,...}) = #3(!info)
fun null_history user_context =
length (get_history user_context) = 0
fun with_null_history (USER_CONTEXT {info,...}) f x =
let
val old_info = !info
val new_info = (#1 (!info), Incremental.empty_context, [])
val _ = info := new_info
val result =
f x
handle exn => (info := old_info; raise exn)
in
info := old_info;
result
end
fun is_const_context (USER_CONTEXT r) = #is_constant r
fun renumber_from (n, l) =
let
fun renumber ((i, l), ITEM {id, context, result, source, ...}) =
let
val newItem =
ITEM
{index = i+1, id = id, context = context,
result = result, source = source}
in
(i+1, newItem :: l)
end
val (_, result) =
Lists.reducel renumber ((n, []), rev l)
in
result
end
local
exception NotFound
fun aux (n, [], l) = raise NotFound
| aux (n, ITEM {index = n', id, context, result, source} :: t, l) =
if n = n' then
rev l @ t
else
let
val newItem =
ITEM
{index = n' - 1,
id = id,
context = context,
result = result,
source = source}
in
aux (n, t, newItem :: l)
end
in
fun delete_item_from_history (ITEM {index, ...}, hist) =
aux (index, hist, [])
handle
NotFound => hist
end
fun delete_from_history (user_context, item) =
let
val USER_CONTEXT {info, update_register as ref (fn_map, _), ...} =
user_context
val (c, delta, hist) = !info
val new_hist = delete_item_from_history (item, hist)
in
set_context_info (user_context, (c, delta, new_hist));
Map.iterate (fn (_, f) => f NONE) fn_map
end
fun delete_entire_history user_context =
let
val USER_CONTEXT {info, update_register as ref (fn_map, _), ...} =
user_context
val (c, delta, hist) = !info
in
set_context_info (user_context, (c, delta, []));
Map.iterate (fn (_, f) => f NONE) fn_map
end
fun remove_duplicate_items_from_history (id, hist) =
let
fun aux ([], _, acc) = acc
| aux ((item as ITEM {index, id = id', context, result, source}) :: l,
decrement, acc) =
if id = id' then
aux (l, decrement + 1, acc)
else if decrement = 0 then
aux (l, 0, item :: acc)
else
let
val newItem =
ITEM
{index = index - decrement,
id = id',
context = context,
result = result,
source = source}
in
aux (l, decrement, newItem :: acc)
end
in
aux (rev hist, 0, [])
end
fun remove_all_duplicates_from_history hist =
let
fun aux ([], result) = rev result
| aux ((item as ITEM {id, ...}) :: l, result) =
let
val l' = remove_duplicate_items_from_history (id, l)
in
aux (l', item :: result)
end
in
aux (hist, [])
end
fun remove_duplicates_from_history user_context =
let
val USER_CONTEXT {info, update_register as ref (fn_map, _), ...} =
user_context
val (c, delta, hist) = !info
val stripped_hist = remove_all_duplicates_from_history hist
val new_hist = renumber_from (0, stripped_hist)
in
set_context_info (user_context, (c, delta, new_hist));
Map.iterate (fn (_, f) => f NONE) fn_map
end
fun move_context_history_to_system (USER_CONTEXT {info=from_info, ...}) =
let val USER_CONTEXT {info=system_info, ...} = getInitialContext()
val (from_context, from_delta, from_history) = !from_info
in system_info := (from_context, from_context, []);
from_info := (#1 (!from_info), Incremental.empty_context, [])
end
fun process_result
{src, result, user_context, preferences, options, output_fn} =
let
val USER_CONTEXT {info, update_register as ref (fn_map, _), ...} =
user_context
val remove_duplicates_from_context =
case preferences
of Preferences.PREFERENCES
{environment_options =
Preferences.ENVIRONMENT_OPTIONS
{remove_duplicates_from_context, ...},
...} =>
!remove_duplicates_from_context
val (c, delta, hist) = !info
val new_delta = Incremental.add_definitions(options, delta, result)
val new_context = Incremental.add_definitions (options, c, result)
val result_strings =
InterPrint.strings
(new_context, options,
Incremental.identifiers_from_result result,
Incremental.pb_from_result result);
val current_index =
case hist
of [] => 0
| (ITEM {index, ...} :: _) => index
val copy_src = case src of
COPY s => COPY s
| STRING s => COPY s
fun mkItem ((n, l, source), (id, result)) =
let
val newItem =
ITEM
{index = n+1,
id = id,
context = new_delta,
result = result,
source = source}
in
(n+1, newItem :: l, copy_src)
end
val (_, new_items, _) =
Lists.reducel
mkItem
((current_index, [], src), result_strings)
val new_items' =
if remove_duplicates_from_context then
remove_all_duplicates_from_history new_items
else
new_items
val old_hist =
if remove_duplicates_from_context then
Lists.reducel
(fn (h, ITEM {id, ...}) =>
remove_duplicate_items_from_history (id, h))
(hist, new_items')
else
hist
val new_index =
case old_hist
of [] => 0
| (ITEM {index, ...} :: _) => index
val new_items'' =
if new_index = current_index then
new_items'
else
renumber_from (new_index, new_items')
val new_hist = new_items'' @ old_hist
in
Lists.iterate (fn (_, s) => output_fn s) result_strings;
info := (new_context, new_delta, new_hist);
Map.iterate (fn (_, f) => f (SOME new_items)) fn_map
end
val dummy_context =
USER_CONTEXT
{info = ref (Incremental.empty_context, Incremental.empty_context, []),
name = ref (CONTEXTNAME("", [])),
user_options =
UserOptions.make_user_context_options Options.default_options,
update_register = ref (Map.empty' op<, 0),
version_number = ref 0,
is_constant = true,
save_file = ref NONE}
fun history_entry_name (ITEM {id, ...}) =
case id of
Ident.VALUE (Ident.VAR s) => SOME (Symbol.symbol_name s)
| _ => NONE
end;
