require "symbol";
require "^.main.info";
require "^.basis.os_path";
require "$.basis.__string";
require "module_id";
functor ModuleId
(structure Symbol: SYMBOL
structure Info: INFO
structure Path: OS_PATH
): MODULE_ID =
struct
type Symbol = Symbol.Symbol
type Location = Info.Location.T
structure Location = Info.Location
datatype ModuleId = MODID of Symbol * Symbol list
datatype Path = PATH of Symbol list
fun insert ([], _) = []
| insert ([x], _) = [x]
| insert (x::l, c) = x :: c :: insert (l, c)
fun string (MODID (s, l)) =
concat (insert (map Symbol.symbol_name (rev (s :: l)), "."))
fun lt' ((s, l), (s', l')) =
if Symbol.symbol_lt (s, s') then true
else
if Symbol.symbol_lt (s', s) then false
else
case l of
[] => l' <> []
| (h :: t) =>
case l' of
[] => false
| (h' :: t') => lt'((h, t), (h', t'))
fun isPervasiveName s =
String.sub(s, 0) = #" "
fun is_pervasive (MODID (s, _)) =
isPervasiveName (Symbol.symbol_name s)
fun module_unit_to_string(MODID(s, l), ext) =
let
val path_ext = if ext = "" then NONE else SOME ext
val name = Symbol.symbol_name s
val name' =
if isPervasiveName name then
substring (name, 1, size name - 1)
else
name
in
Path.joinBaseExt {base = name', ext = path_ext}
end
fun lt (MODID a, MODID b) = lt'(a, b)
fun eq (m:ModuleId, m') = m = m'
fun int_is_alpha c =
(c >= ord #"A" andalso c <= ord #"Z") orelse
(c >= ord #"a" andalso c <= ord #"z") orelse
(c = ord #"_") orelse (c = ord #"-")
fun legal c =
(c >= ord #"0" andalso c <= ord #"9") orelse
int_is_alpha c orelse c = ord #"'"
fun path (MODID (_, l)) = PATH l
fun path_string (PATH l) =
concat (insert (map Symbol.symbol_name (rev l), "."))
val empty_path = PATH []
fun squash_path(name, [], acc) = rev acc
| squash_path(name, x :: xs, acc) =
case Symbol.symbol_name x of
"^" =>
(case xs of
[] =>
(Info.default_error'
(Info.FATAL, Location.UNKNOWN,
"can't find parent for module name: " ^ string name))
| y :: ys =>
squash_path(name, ys, acc))
| "$" => rev acc
| _ => squash_path(name, xs, x :: acc)
fun add_path (PATH l, MODID (s, l')) = MODID (s, [])
exception NoParent
fun parent (PATH (_::l)) = PATH l
| parent _ = raise NoParent
exception Parse of string
fun internal_from_string (s, init_offset, ord_sep, allow_space) =
let
val len = size s
val s' =
if len - init_offset >= 3 andalso
substring (s, len - 3, 3) = ".mo" then
substring (s, 0, len - 3)
else if len - init_offset >= 4 andalso
substring (s, len - 4, 4) = ".sml" then
substring (s, 0, len - 4)
else
s
val len = size s'
fun find_separator' i =
if i = len then i
else
let
val ch = MLWorks.String.ordof (s', i)
in
if ch = ord_sep then
i
else
if legal ch then
find_separator' (i+1)
else
raise Parse s
end
fun find_separator i =
if i = len orelse
let
val c = MLWorks.String.ordof(s', i)
in
int_is_alpha c orelse
(allow_space andalso c = ord #" ") orelse
((c = ord #"^" orelse c = ord #"$")
andalso i+1 < len andalso MLWorks.String.ordof(s', i+1) = ord_sep)
end then
find_separator' (i+1)
else
raise Parse s
fun from i =
if i = len then raise Parse s
else
let
val split = find_separator i
val substr =
substring (s', i, split - i)
val symbol =
Symbol.find_symbol(Path.mkCanonical substr)
in
if split = len then
([], symbol)
else
let val (path, child) = from (split + 1)
in (symbol :: path, child)
end
end
val (l, s'') = from init_offset
in
MODID (s'', rev l)
end
fun from_string (s, location) =
internal_from_string (s, 0, ord #".", false)
handle Parse s =>
(Info.default_error'
(Info.FATAL, location,
"from_string: invalid module name: `" ^ s ^ "'"))
fun from_mo_string (s, location) =
internal_from_string (s, 0, ord #".", true)
handle Parse s =>
(Info.default_error'
(Info.FATAL, location,
"from_mo_string: invalid module name: `" ^ s ^ "'"))
fun perv_from_string (s, location) =
case from_string (s, location)
of MODID (s, l) =>
MODID (Symbol.find_symbol (" " ^ Symbol.symbol_name s), l)
fun from_host (s, location) =
let
val {dir, file} = Path.splitDirFile s
val {isAbs, vol, arcs} = Path.fromString dir
val name = Path.base file
fun validComponent (s, ~1) = true
| validComponent (s, i) =
if legal (MLWorks.String.ordof (s, i)) then
validComponent (s, i-1)
else
false
fun mk_symbol s =
if validComponent (s, size s - 1) then
Symbol.find_symbol (Path.mkCanonical s)
else
raise Parse s
val list = map mk_symbol arcs
val sym = mk_symbol name
in
MODID (sym, rev list)
end
handle Parse s =>
(Info.default_error'
(Info.FATAL, location,
"from_host: invalid module name: `" ^ s ^ "'"))
fun find_a_slash(s, i, size) =
(i < size) andalso
(MLWorks.String.ordof(s, i) = ord #"/" orelse find_a_slash(s, i+1, size))
fun prefix_hats(0, list) = concat list
| prefix_hats(n, list) =
prefix_hats(n-1, "^/" :: list)
val prefix_hats =
fn ((n, hats, location)) =>
if n < 0 then
Info.default_error'
(Info.FAULT, location, "negative hats value")
else
prefix_hats(n, hats)
fun from_require_string (s, location) =
let
val len = size s
fun from(i, hats) =
(if len - i >= 3 andalso substring (s, i, 3) = "../" then
from (i + 3, hats+1)
else
if hats >= 1 orelse find_a_slash(s, 0, size s) then
internal_from_string
(prefix_hats
(hats, [substring (s, i, size s - 3*hats)], location),
0, ord #"/", false)
else
internal_from_string(s, 0, ord #".", false))
handle Parse s =>
(Info.default_error'
(Info.FATAL, location,
"from_require_string: invalid module name: `" ^ s ^ "'"))
in
from(0, 0)
end
fun perv_from_require_string (s, location) =
from_mo_string (s, location)
fun from_string' s =
SOME (internal_from_string (s, 0, ord #".", true))
handle Parse s =>
NONE
end;
